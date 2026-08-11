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
 * Sub-sender Goal (tujuan/rencana tindak lanjut perawatan) untuk flow SatuSehatBundle.
 *
 * Sumber: resume_pasien_ranap (dilanjutkan + ket_dilanjutkan) -> Goal.description (teks).
 * Idempotent: id deterministik (stableResourceId per no_rawat) -> PUT; id disimpan ke satu_sehat_goal.
 */
public class SatuSehatGoal {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatGoal() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Goal : " + e);
        }
    }

    private static class GoalData {
        String deskripsi="", startDate="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /** PREVIEW: rakit Bundle Goal tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        GoalData d = ambilData(noRawat);
        if (d == null || d.deskripsi.equals("") || d.idPasien.equals("")) return null;
        String id = stableResourceId("Goal", noRawat);
        ObjectNode goal = buatGoal(noRawat, d, id, idEncounter);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "Goal/" + id);
        entry.set("resource", goal);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "Goal/" + id);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        GoalData d = ambilData(noRawat);
        if (d == null || d.deskripsi.equals("")) {
            return;   // tidak ada rencana tindak lanjut
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Goal : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        String id = stableResourceId("Goal", noRawat);
        ObjectNode goal = buatGoal(noRawat, d, id, idEncounter);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "Goal/" + id);
        entry.set("resource", goal);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "Goal/" + id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Goal : " + link);
        System.out.println("Request JSON Goal : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Goal Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Goal OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Goal Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Goal : " + hasil);
        // Simpan id ke satu_sehat_goal.
        Sequel.queryu2("replace into satu_sehat_goal (no_rawat,id_goal) values (?,?)", 2, new String[]{noRawat, id});
    }

    private ObjectNode buatGoal(String noRawat, GoalData d, String id, String idEncounter) {
        ObjectNode goal = mapper.createObjectNode();
        goal.put("resourceType", "Goal");
        goal.put("id", id);
        ObjectNode iden = goal.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/goal/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "GOAL-" + noRawat);
        goal.put("lifecycleStatus", "active");
        ObjectNode achv = goal.putObject("achievementStatus").putArray("coding").addObject();
        achv.put("system", "http://terminology.hl7.org/CodeSystem/goal-achievement");
        achv.put("code", "in-progress");
        achv.put("display", "In Progress");
        goal.putObject("description").put("text", d.deskripsi);
        ObjectNode subject = goal.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);
        if (!d.startDate.equals("")) {
            goal.put("startDate", d.startDate);
        }
        if (!d.idDokter.equals("")) {
            ObjectNode expressedBy = goal.putObject("expressedBy");
            expressedBy.put("reference", "Practitioner/" + d.idDokter);
            expressedBy.put("display", d.namaDokter);
        }
        return goal;
    }

    private GoalData ambilData(String noRawat) {
        GoalData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(rpr.dilanjutkan,'') as dilanjutkan, ifnull(rpr.ket_dilanjutkan,'') as ket_dilanjutkan, "
                    + "rp.tgl_registrasi, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from resume_pasien_ranap rpr "
                    + "inner join reg_periksa rp on rp.no_rawat=rpr.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=rpr.kd_dokter "
                    + "left join pegawai pg on pg.nik=rpr.kd_dokter "
                    + "where rpr.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new GoalData();
                d.deskripsi = gabungLabel("Dilanjutkan", bersihkan(r.getString("dilanjutkan")),
                        "Keterangan", bersihkan(r.getString("ket_dilanjutkan")));
                d.startDate = tanggalSaja(nz(r.getString("tgl_registrasi")));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaDokter = nz(r.getString("nm_dokter"));
                d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Goal ambilData : " + e);
        }
        return d;
    }

    /** Gabung pasangan (label, value) yang value-nya terisi, dipisah ". ". */
    private String gabungLabel(String... labelValue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < labelValue.length; i += 2) {
            String label = nz(labelValue[i]).trim();
            String value = nz(labelValue[i + 1]).trim();
            if (!value.equals("") && !value.equals("-")) {
                if (sb.length() > 0) sb.append(". ");
                sb.append(label).append(": ").append(value);
            }
        }
        return sb.toString();
    }

    /** Ambil bagian tanggal (YYYY-MM-DD) saja; "" bila tidak valid. */
    private String tanggalSaja(String dt) {
        if (dt == null) return "";
        String t = dt.trim();
        if (t.length() < 10) return "";
        t = t.substring(0, 10);
        if (t.startsWith("0000-00-00") || !t.matches("\\d{4}-\\d{2}-\\d{2}")) return "";
        return t;
    }

    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(resourceType);
        if (keys != null) {
            for (String key : keys) sb.append("|").append(key == null ? "-" : key.trim());
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
