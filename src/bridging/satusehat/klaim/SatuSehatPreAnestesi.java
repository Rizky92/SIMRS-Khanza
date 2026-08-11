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
 * Sub-sender Pre-Anestesi (Observation LOINC 34751-8) untuk flow SatuSehatBundle.
 *
 * Sumber: penilaian_pre_anestesi (asa, rencana_anestesi, diagnosa, rencana_tindakan, dst).
 * Observation: code 34751-8, category exam, component ASA (SNOMED), note rencana anestesi,
 * performer = dokter anestesi (kd_dokter). Mallampati tidak ada di tabel -> di-skip.
 * Idempotent: id deterministik per no_rawat -> PUT; id disimpan ke satu_sehat_preanestesi.
 */
public class SatuSehatPreAnestesi {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatPreAnestesi() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi PreAnestesi : " + e);
        }
    }

    private static class PreData {
        String waktu="", asa="", rencanaAnestesi="", diagnosa="", rencanaTindakan="", puasa="", catatan="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /** PREVIEW: rakit Bundle Pre-Anestesi (Observation) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        PreData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        String id = stableResourceId("ObservationPreAnestesi", noRawat);
        ObjectNode obs = buatObservation(noRawat, d, id, idEncounter);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", "Observation/" + id);
        entry.set("resource", obs);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "Observation/" + id);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        PreData d = ambilData(noRawat);
        if (d == null) {
            System.out.println("Notifikasi PreAnestesi : penilaian_pre_anestesi (no_rawat " + noRawat
                    + ") belum diisi. Dilewati.");
            return;
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi PreAnestesi : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        String id = stableResourceId("ObservationPreAnestesi", noRawat);
        ObjectNode obs = buatObservation(noRawat, d, id, idEncounter);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", "Observation/" + id);
        entry.set("resource", obs);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "Observation/" + id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL PreAnestesi : " + link);
        System.out.println("Request JSON PreAnestesi : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error PreAnestesi Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error PreAnestesi OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error PreAnestesi Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON PreAnestesi : " + hasil);
        Sequel.queryu2("replace into satu_sehat_preanestesi (no_rawat,id_observation) values (?,?)",
                2, new String[]{noRawat, id});
    }

    private ObjectNode buatObservation(String noRawat, PreData d, String id, String idEncounter) {
        ObjectNode obs = mapper.createObjectNode();
        obs.put("resourceType", "Observation");
        obs.put("id", id);
        ObjectNode iden = obs.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/observation/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "PREANES-" + noRawat);
        obs.put("status", "final");

        // category: exam
        ObjectNode cat = obs.putArray("category").addObject().putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        cat.put("code", "exam");
        cat.put("display", "Exam");

        // code: LOINC 34751-8
        ObjectNode code = obs.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", "http://loinc.org");
        cc.put("code", "34751-8");
        cc.put("display", "Anesthesia Preoperative evaluation and management note");
        code.put("text", "Evaluasi dan Manajemen Pre-Anestesi");

        ObjectNode subject = obs.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);
        obs.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!d.waktu.equals("")) {
            obs.put("effectiveDateTime", d.waktu);
        }
        if (!d.idDokter.equals("")) {
            ObjectNode perf = obs.putArray("performer").addObject();
            perf.put("reference", "Practitioner/" + d.idDokter);
            perf.put("display", d.namaDokter);
        }

        // component: ASA physical status (SNOMED). Skip bila asa tak terpetakan (hanya 1-5).
        String[] asaVal = asaClass(d.asa);
        if (asaVal != null) {
            ObjectNode comp = obs.putArray("component").addObject();
            ObjectNode compCode = comp.putObject("code");
            ObjectNode ccCode = compCode.putArray("coding").addObject();
            ccCode.put("system", "http://snomed.info/sct");
            ccCode.put("code", "273270000");
            ccCode.put("display", "American Society of Anesthesiologists physical status classification");
            compCode.put("text", "ASA " + d.asa.trim());
            ObjectNode val = comp.putObject("valueCodeableConcept").putArray("coding").addObject();
            val.put("system", "http://snomed.info/sct");
            val.put("code", asaVal[0]);
            val.put("display", asaVal[1]);
        }

        // note: rencana anestesi + ringkasan pra-bedah.
        String catatan = gabung(
                "Rencana anestesi", d.rencanaAnestesi,
                "Diagnosa pra-bedah", d.diagnosa,
                "Rencana tindakan/operasi", d.rencanaTindakan,
                "ASA", d.asa.equals("") ? "" : ("ASA " + d.asa.trim()),
                "Puasa sejak", d.puasa,
                "Catatan khusus", d.catatan);
        if (!catatan.equals("")) {
            obs.putArray("note").addObject().put("text", catatan);
        }
        return obs;
    }

    /** {kode, display} SNOMED ASA physical status class; null bila tak terpetakan (selain 1-5). */
    private String[] asaClass(String asa) {
        String a = (asa == null) ? "" : asa.trim();
        if (a.startsWith("1")) return new String[]{"413495001", "American Society of Anesthesiologists physical status class 1"};
        if (a.startsWith("2")) return new String[]{"413496000", "American Society of Anesthesiologists physical status class 2"};
        if (a.startsWith("3")) return new String[]{"413497009", "American Society of Anesthesiologists physical status class 3"};
        if (a.startsWith("4")) return new String[]{"413498004", "American Society of Anesthesiologists physical status class 4"};
        if (a.startsWith("5")) return new String[]{"413499007", "American Society of Anesthesiologists physical status class 5"};
        return null;
    }

    private PreData ambilData(String noRawat) {
        PreData d = null;
        try {
            // Dokter anestesi dari penilaian_pre_anestesi.kd_dokter; fallback DPJP reg_periksa.
            PreparedStatement p = koneksi.prepareStatement(
                    "select t.tanggal, ifnull(t.asa,'') as asa, ifnull(t.rencana_anestesi,'') as rencana_anestesi, "
                    + "ifnull(t.diagnosa,'') as diagnosa, ifnull(t.rencana_tindakan,'') as rencana_tindakan, "
                    + "ifnull(t.puasa,'') as puasa, ifnull(t.catatan_khusus,'') as catatan_khusus, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dra.nm_dokter, ifnull(drp.nm_dokter,'')) as nm_dokter, "
                    + "ifnull(pga.no_ktp, ifnull(pgp.no_ktp,'')) as ktp_dokter "
                    + "from penilaian_pre_anestesi t "
                    + "inner join reg_periksa rp on rp.no_rawat=t.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dra on dra.kd_dokter=t.kd_dokter "
                    + "left join pegawai pga on pga.nik=t.kd_dokter "
                    + "left join dokter drp on drp.kd_dokter=rp.kd_dokter "
                    + "left join pegawai pgp on pgp.nik=rp.kd_dokter "
                    + "where t.no_rawat=? order by t.tanggal desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new PreData();
                d.waktu = formatWaktu(nz(r.getString("tanggal")));
                d.asa = nz(r.getString("asa"));
                d.rencanaAnestesi = bersih(r.getString("rencana_anestesi"));
                d.diagnosa = bersih(r.getString("diagnosa"));
                d.rencanaTindakan = bersih(r.getString("rencana_tindakan"));
                d.puasa = formatWaktu(nz(r.getString("puasa")));
                d.catatan = bersih(r.getString("catatan_khusus"));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaDokter = nz(r.getString("nm_dokter"));
                d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi PreAnestesi ambilData : " + e);
        }
        return d;
    }

    // ====================== UTIL ======================

    /** Gabung pasangan label-nilai (lewati yg kosong) jadi satu teks "Label: nilai. ...". */
    private String gabung(String... lv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < lv.length; i += 2) {
            String nilai = lv[i + 1] == null ? "" : lv[i + 1].trim();
            if (nilai.equals("")) continue;
            if (sb.length() > 0) sb.append(". ");
            sb.append(lv[i]).append(": ").append(nilai);
        }
        return sb.toString();
    }

    private String bersih(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", " ").trim();
    }

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
