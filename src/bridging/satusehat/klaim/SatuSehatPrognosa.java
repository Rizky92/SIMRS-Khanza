/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
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
 * Sub-sender Prognosis (RME Rawat Jalan) untuk flow SatuSehatBundle.
 *
 * Mengirim ClinicalImpression dengan prognosisCodeableConcept (SNOMED) sesuai Lampiran Terminologi
 * "Variabel Prognosis". Sumber: persetujuan_penolakan_tindakan.prognosis (teks bebas) -> dipetakan ke
 * SNOMED prognosis. Idempotent: id deterministik (stableResourceId) -> PUT (tidak menduplikasi).
 */
public class SatuSehatPrognosa {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatPrognosa() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Prognosa : " + e);
        }
    }

    private static class PrognosaData {
        String prognosisTeks="", waktu="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /** PREVIEW: rakit Bundle Prognosa (ClinicalImpression) tanpa mengirim; null bila tak terpetakan. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        PrognosaData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        String[] snomed = mapPrognosis(d.prognosisTeks);
        if (snomed == null) return null;
        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String id = stableResourceId("ClinicalImpressionPrognosa", noRawat);
        ObjectNode ci = buatClinicalImpression(noRawat, d, patientRef, encounterRef, snomed);
        ci.put("id", id);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "ClinicalImpression/" + id);
        entry.set("resource", ci);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "ClinicalImpression/" + id);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        PrognosaData d = ambilData(noRawat);
        if (d == null) {
            return;   // tidak ada prognosis
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Prognosa : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        String[] snomed = mapPrognosis(d.prognosisTeks);
        if (snomed == null) {
            System.out.println("Notifikasi Prognosa : prognosis '" + d.prognosisTeks + "' (no_rawat " + noRawat
                    + ") tidak bisa dipetakan ke SNOMED. Dilewati.");
            return;
        }

        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String id = stableResourceId("ClinicalImpressionPrognosa", noRawat);

        ObjectNode ci = buatClinicalImpression(noRawat, d, patientRef, encounterRef, snomed);
        ci.put("id", id);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "ClinicalImpression/" + id);
        entry.set("resource", ci);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "ClinicalImpression/" + id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Prognosa : " + link);
        System.out.println("Request JSON Prognosa : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Prognosa Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Prognosa OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Prognosa Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Prognosa : " + hasil);
    }

    /** ClinicalImpression dengan prognosisCodeableConcept (SNOMED). */
    private ObjectNode buatClinicalImpression(String noRawat, PrognosaData d, String patientRef,
            String encounterRef, String[] snomed) {
        ObjectNode ci = mapper.createObjectNode();
        ci.put("resourceType", "ClinicalImpression");
        ObjectNode iden = ci.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/clinical-impression/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "PROGNOSA-" + noRawat);
        ci.put("status", "completed");
        ci.put("description", "Penilaian prognosis berdasarkan tingkat kesadaran"
                + (d.prognosisTeks.equals("") ? "" : (": " + d.prognosisTeks)));
        ObjectNode subject = ci.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", d.namaPasien);
        ci.putObject("encounter").put("reference", encounterRef);
        if (!d.waktu.equals("")) {
            ci.put("effectiveDateTime", d.waktu);
            ci.put("date", d.waktu);
        }
        if (!d.idDokter.equals("")) {
            ci.putObject("assessor").put("reference", "Practitioner/" + d.idDokter);
        }
        ObjectNode prognosis = ci.putArray("prognosisCodeableConcept").addObject();
        ObjectNode coding = prognosis.putArray("coding").addObject();
        coding.put("system", "http://snomed.info/sct");
        coding.put("code", snomed[0]);
        coding.put("display", snomed[1]);
        prognosis.put("text", snomed[1]);
        return ci;
    }

    /**
     * Map tingkat kesadaran (pemeriksaan_ranap.kesadaran) -> {kode SNOMED prognosis, display}.
     * null bila tak bisa dipetakan. good 170968001 | fair 65872000 | guarded 67334001 | poor 170969009.
     */
    private String[] mapPrognosis(String kesadaran) {
        if (kesadaran == null) return null;
        String s = kesadaran.toLowerCase().trim();
        if (s.equals("")) return null;
        if (s.contains("compos") || s.contains("alert"))
            return new String[]{"170968001", "Prognosis good"};
        if (s.contains("apatis") || s.contains("apathy") || s.contains("confusion") || s.contains("voice"))
            return new String[]{"65872000", "Fair prognosis"};
        if (s.contains("somnolen") || s.contains("delirium") || s.contains("pain"))
            return new String[]{"67334001", "Guarded prognosis"};
        if (s.contains("sopor") || s.contains("coma") || s.contains("unresponsive"))
            return new String[]{"170969009", "Prognosis bad"};
        return null;
    }

    private PrognosaData ambilData(String noRawat) {
        PrognosaData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select pr.kesadaran, pr.tgl_perawatan, pr.jam_rawat, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from pemeriksaan_ranap pr "
                    + "inner join reg_periksa rp on rp.no_rawat=pr.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter "
                    + "where pr.no_rawat=? and pr.kesadaran is not null and pr.kesadaran<>'' "
                    + "order by pr.tgl_perawatan desc, pr.jam_rawat desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new PrognosaData();
                d.prognosisTeks = bersihkan(r.getString("kesadaran"));
                d.waktu = formatWaktu(nz(r.getString("tgl_perawatan")) + " " + nz(r.getString("jam_rawat")));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaDokter = nz(r.getString("nm_dokter"));
                d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Prognosa ambilData : " + e);
        }
        return d;
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

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
