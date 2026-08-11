/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sub-sender EpisodeOfCare (rencana/episode perawatan) untuk flow SatuSehatBundle.
 *
 * EpisodeOfCare mengelompokkan perawatan pasien pada satu organisasi untuk periode + diagnosa tertentu
 * (bagian "Plan"/rencana perawatan). Field wajib SATUSEHAT: status, statusHistory, diagnosis.condition,
 * patient, managingOrganization. Karena diagnosis.condition wajib, bundle berisi Condition + EpisodeOfCare.
 * Idempotent: id deterministik (stableResourceId) + PUT; id disimpan ke satu_sehat_episodeofcare.
 */
public class SatuSehatEpisodeOfCare {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatEpisodeOfCare() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi EpisodeOfCare : " + e);
        }
    }

    private static class Konteks {
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
        String mulai="", selesai="", kdPenyakit="", namaPenyakit="";
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || k.idPasien.equals("")) {
            System.out.println("Notifikasi EpisodeOfCare : konteks/pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        if (k.kdPenyakit.equals("")) {
            System.out.println("Notifikasi EpisodeOfCare : diagnosa belum ada untuk no_rawat " + noRawat
                    + ". Dilewati (diagnosis.condition wajib).");
            return;
        }

        String condId = stableResourceId("ConditionEpisode", noRawat);
        String eocId  = stableResourceId("EpisodeOfCare", noRawat);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        tambahEntry(entries, "Condition", condId, buatCondition(k, condId, idEncounter));
        tambahEntry(entries, "EpisodeOfCare", eocId, buatEpisodeOfCare(noRawat, k, eocId, condId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL EpisodeOfCare : " + link);
        System.out.println("Request JSON EpisodeOfCare : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("Error EpisodeOfCare Status Code: " + ex.getStatusCode());
            System.out.println("Error EpisodeOfCare Body: " + ex.getResponseBodyAsString());
            throw ex;
        }
        System.out.println("Result JSON EpisodeOfCare : " + hasil);
        Sequel.queryu2("replace into satu_sehat_episodeofcare (no_rawat,id_episodeofcare) values (?,?)",
                2, new String[]{noRawat, eocId});
    }

    /** Condition diagnosa utama (dirujuk EpisodeOfCare.diagnosis.condition). */
    private ObjectNode buatCondition(Konteks k, String id, String idEncounter) {
        ObjectNode c = mapper.createObjectNode();
        c.put("resourceType", "Condition");
        c.put("id", id);
        ObjectNode cs = c.putObject("clinicalStatus").putArray("coding").addObject();
        cs.put("system", "http://terminology.hl7.org/CodeSystem/condition-clinical");
        cs.put("code", "active");
        cs.put("display", "Active");
        ObjectNode cat = c.putArray("category").addObject().putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/condition-category");
        cat.put("code", "encounter-diagnosis");
        cat.put("display", "Encounter Diagnosis");
        ObjectNode code = c.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", "http://hl7.org/fhir/sid/icd-10");
        cc.put("code", k.kdPenyakit);
        cc.put("display", k.namaPenyakit);
        code.put("text", k.namaPenyakit);
        ObjectNode subject = c.putObject("subject");
        subject.put("reference", "Patient/" + k.idPasien);
        subject.put("display", k.namaPasien);
        c.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!k.mulai.equals("")) c.put("recordedDate", k.mulai);
        return c;
    }

    /** EpisodeOfCare: status, statusHistory, diagnosis->Condition, patient, managingOrganization, period, careManager. */
    private ObjectNode buatEpisodeOfCare(String noRawat, Konteks k, String id, String condId) {
        boolean selesai = !k.selesai.equals("");
        String status = selesai ? "finished" : "active";

        ObjectNode e = mapper.createObjectNode();
        e.put("resourceType", "EpisodeOfCare");
        e.put("id", id);
        // identifier sengaja TIDAK dikirim: SATUSEHAT menolak system episodeofcare utk org-UUID (RuleNumber 10458),
        // dan identifier bukan field wajib. Idempotensi cukup dari id deterministik + PUT.
        e.put("status", status);

        // statusHistory (wajib) — satu entri sesuai periode.
        ObjectNode sh = e.putArray("statusHistory").addObject();
        sh.put("status", status);
        ObjectNode shPeriod = sh.putObject("period");
        if (!k.mulai.equals("")) shPeriod.put("start", k.mulai);
        if (selesai) shPeriod.put("end", k.selesai);

        // diagnosis (wajib) — referensi Condition.
        ObjectNode diag = e.putArray("diagnosis").addObject();
        diag.putObject("condition").put("reference", "Condition/" + condId);
        ObjectNode roleCoding = diag.putObject("role").putArray("coding").addObject();
        roleCoding.put("system", "http://terminology.hl7.org/CodeSystem/diagnosis-role");
        roleCoding.put("code", "AD");
        roleCoding.put("display", "Admission diagnosis");
        diag.put("rank", 1);

        ObjectNode patient = e.putObject("patient");
        patient.put("reference", "Patient/" + k.idPasien);
        patient.put("display", k.namaPasien);

        e.putObject("managingOrganization").put("reference", "Organization/" + k.idOrg);

        ObjectNode period = e.putObject("period");
        if (!k.mulai.equals("")) period.put("start", k.mulai);
        if (selesai) period.put("end", k.selesai);

        if (!k.idDokter.equals("")) {
            ObjectNode cm = e.putObject("careManager");
            cm.put("reference", "Practitioner/" + k.idDokter);
            cm.put("display", k.namaDokter);
        }
        return e;
    }

    private void tambahEntry(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    // ====================== DATA ======================

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "concat(rp.tgl_registrasi,' ',rp.jam_reg) as waktu_reg, "
                    + "ifnull(d.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dpjp, "
                    + "ifnull((select dp.kd_penyakit from diagnosa_pasien dp where dp.no_rawat=rp.no_rawat order by dp.prioritas asc limit 1),'') as kd_penyakit, "
                    + "ifnull((select pny.nm_penyakit from diagnosa_pasien dp2 left join penyakit pny on pny.kd_penyakit=dp2.kd_penyakit where dp2.no_rawat=rp.no_rawat order by dp2.prioritas asc limit 1),'') as nm_penyakit, "
                    + "ifnull((select max(concat(ki.tgl_keluar,' ',ki.jam_keluar)) from kamar_inap ki where ki.no_rawat=rp.no_rawat and ki.tgl_keluar<>'0000-00-00'),'') as tgl_keluar "
                    + "from reg_periksa rp inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter d on d.kd_dokter=rp.kd_dokter "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter "
                    + "where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                k = new Konteks();
                k.namaPasien = nz(r.getString("nm_pasien"));
                k.namaDokter = nz(r.getString("nm_dokter"));
                k.mulai = formatWaktu(nz(r.getString("waktu_reg")));
                k.selesai = formatWaktu(nz(r.getString("tgl_keluar")));
                k.kdPenyakit = nz(r.getString("kd_penyakit"));
                k.namaPenyakit = nz(r.getString("nm_penyakit"));
                if (k.namaPenyakit.equals("")) k.namaPenyakit = k.kdPenyakit;
                k.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                k.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dpjp"))));
                k.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi EpisodeOfCare ambilKonteks : " + e);
        }
        return k;
    }

    // ====================== UTIL ======================

    private String formatWaktu(String dt) {
        if (dt == null || dt.trim().equals("")) return "";
        String t = dt.trim();
        if (t.startsWith("0000-00-00")) return "";
        if (t.contains(".")) t = t.substring(0, t.indexOf('.'));
        if (t.contains(" ")) t = t.replace(" ", "T");
        if (t.length() == 10) t = t + "T00:00:00";
        return t + "+07:00";
    }

    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(resourceType);
        if (keys != null) {
            for (String key : keys) sb.append("|").append(key == null ? "-" : key.trim());
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
