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
 * Sub-sender RiskAssessment (penilaian risiko jatuh) untuk flow SatuSehatBundle.
 *
 * Sumber: penilaian_awal_keperawatan_ranap (skrining jatuh saat asesmen masuk ranap):
 *   - Morse  -> penilaian_jatuhmorse_totalnilai
 *   - Sydney -> penilaian_jatuhsydney_totalnilai
 * Tabel ini hanya menyimpan skor, jadi level risiko (outcome + qualitativeRisk) diturunkan dari skor.
 * Idempotent: id deterministik (stableResourceId per no_rawat+jenis) -> PUT; id disimpan ke satu_sehat_riskassessment.
 */
public class SatuSehatRiskAssessment {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatRiskAssessment() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi RiskAssessment : " + e);
        }
    }

    private static class RiskData {
        String jenis="", metode="", total="", waktu="";
        String[] risk;   // {kode risk-probability, display, level teks Indonesia}
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /** PREVIEW: rakit Bundle RiskAssessment + ClinicalImpression tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        RiskData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        String id = stableResourceId("RiskAssessment", noRawat, d.jenis);
        ObjectNode ra = buatRiskAssessment(noRawat, d, id, idEncounter);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", "RiskAssessment/" + id);
        entry.set("resource", ra);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "RiskAssessment/" + id);
        String ciId = stableResourceId("ClinicalImpressionRiskAssessment", noRawat, d.jenis);
        ObjectNode ci = buatClinicalImpression(noRawat, d, ciId, idEncounter);
        ObjectNode eCi = entries.addObject();
        eCi.put("fullUrl", "ClinicalImpression/" + ciId);
        eCi.set("resource", ci);
        ObjectNode reqCi = eCi.putObject("request");
        reqCi.put("method", "PUT");
        reqCi.put("url", "ClinicalImpression/" + ciId);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        RiskData d = ambilData(noRawat);
        if (d == null) {
            System.out.println("Notifikasi RiskAssessment : skrining jatuh (no_rawat " + noRawat
                    + ") belum diisi / pasien tidak ada. Dilewati.");
            return;
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi RiskAssessment : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        String id = stableResourceId("RiskAssessment", noRawat, d.jenis);
        ObjectNode ra = buatRiskAssessment(noRawat, d, id, idEncounter);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", "RiskAssessment/" + id);
        entry.set("resource", ra);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "RiskAssessment/" + id);
        String idPertama = id;

        // ClinicalImpression -> agar terbaca di SOAP bagian A (Assessment) viewer SATUSEHAT
        // (viewer claims merender ClinicalImpression sebagai kartu di A-Assessment; RiskAssessment tidak dirender).
        String ciId = stableResourceId("ClinicalImpressionRiskAssessment", noRawat, d.jenis);
        ObjectNode ci = buatClinicalImpression(noRawat, d, ciId, idEncounter);
        ObjectNode eCi = entries.addObject();
        eCi.put("fullUrl", "ClinicalImpression/" + ciId);
        eCi.set("resource", ci);
        ObjectNode reqCi = eCi.putObject("request");
        reqCi.put("method", "PUT");
        reqCi.put("url", "ClinicalImpression/" + ciId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL RiskAssessment : " + link);
        System.out.println("Request JSON RiskAssessment : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error RiskAssessment Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error RiskAssessment OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error RiskAssessment Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON RiskAssessment : " + hasil);
        // Simpan id (1 baris per no_rawat).
        Sequel.queryu2("replace into satu_sehat_riskassessment (no_rawat,id_riskassessment) values (?,?)",
                2, new String[]{noRawat, idPertama});
    }

    private ObjectNode buatRiskAssessment(String noRawat, RiskData d, String id, String idEncounter) {
        ObjectNode ra = mapper.createObjectNode();
        ra.put("resourceType", "RiskAssessment");
        ra.put("id", id);
        ObjectNode iden = ra.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/riskassessment/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "RISK-" + d.jenis + "-" + noRawat);
        ra.put("status", "final");
        ra.putObject("method").put("text", d.metode);
        ObjectNode code = ra.putObject("code");
        ObjectNode coding = code.putArray("coding").addObject();
        coding.put("system", "http://snomed.info/sct");
        coding.put("code", "225338004");
        coding.put("display", "Falls risk assessment");
        code.put("text", "Penilaian Risiko Jatuh");
        ObjectNode subject = ra.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);
        ra.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!d.waktu.equals("")) {
            ra.put("occurrenceDateTime", d.waktu);
        }
        // prediction: outcome (level risiko) + qualitativeRisk (dari skor) + rationale (nilai skor).
        ObjectNode prediction = ra.putArray("prediction").addObject();
        prediction.putObject("outcome").put("text", "Risiko jatuh: " + d.risk[2]);
        ObjectNode qc = prediction.putObject("qualitativeRisk").putArray("coding").addObject();
        qc.put("system", "http://terminology.hl7.org/CodeSystem/risk-probability");
        qc.put("code", d.risk[0]);
        qc.put("display", d.risk[1]);
        if (!d.total.equals("")) {
            prediction.put("rationale", "Skor " + d.metode + ": " + d.total);
        }
        return ra;
    }

    /**
     * ClinicalImpression berisi narasi risiko jatuh -> dirender viewer SATUSEHAT sebagai kartu
     * di SOAP bagian A (Assessment).
     */
    private ObjectNode buatClinicalImpression(String noRawat, RiskData d, String id, String idEncounter) {
        ObjectNode ci = mapper.createObjectNode();
        ci.put("resourceType", "ClinicalImpression");
        ci.put("id", id);
        ObjectNode iden = ci.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/clinical-impression/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "RISKCI-" + d.jenis + "-" + noRawat);
        ci.put("status", "completed");
        ci.put("description", "Penilaian Risiko Jatuh (" + d.metode + "): " + d.risk[2]
                + (d.total.equals("") ? "" : (" — skor " + d.total)));
        ObjectNode subject = ci.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);
        ci.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!d.waktu.equals("")) {
            ci.put("effectiveDateTime", d.waktu);
            ci.put("date", d.waktu);
        }
        if (!d.idDokter.equals("")) {
            ObjectNode assessor = ci.putObject("assessor");
            assessor.put("reference", "Practitioner/" + d.idDokter);
            assessor.put("display", d.namaDokter);
        }
        ci.put("summary", "Risiko jatuh: " + d.risk[2]);
        // prognosisCodeableConcept -> agar kartu "Prognosis" di A-Assessment ikut merender (seperti Prognosa).
        ObjectNode prog = ci.putArray("prognosisCodeableConcept").addObject();
        prog.put("text", "Risiko jatuh (" + d.metode + "): " + d.risk[2]
                + (d.total.equals("") ? "" : (" — skor " + d.total)));
        return ci;
    }

    /**
     * Turunkan level risiko dari skor sesuai skala. Mengembalikan {kode risk-probability, display, level teks}.
     * null bila skor tidak valid / 0.
     * Morse : <25 negligible | 25-44 low | >=45 high.
     * Sydney: <6 low | 6-16 moderate | >=17 high.
     */
    private String[] deriveRisk(String jenis, int skor) {
        if (jenis.equals("MORSE")) {
            if (skor < 25)  return new String[]{"negligible", "Negligible likelihood", "Tidak berisiko"};
            if (skor < 45)  return new String[]{"low",        "Low likelihood",        "Risiko rendah"};
            return new String[]{"high", "High likelihood", "Risiko tinggi"};
        } else { // SYDNEY
            if (skor < 6)   return new String[]{"low",      "Low likelihood",      "Risiko rendah"};
            if (skor < 17)  return new String[]{"moderate", "Moderate likelihood", "Risiko sedang"};
            return new String[]{"high", "High likelihood", "Risiko tinggi"};
        }
    }

    /** Parse angka; null bila kosong / tidak numerik (0 valid). */
    private Integer parseInt(String s) {
        if (s == null) return null;
        String t = s.trim().replaceAll("[^0-9-]", "");
        if (t.equals("") || t.equals("-")) return null;
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Ambil skrining jatuh asesmen awal ranap, pilih SATU skala:
     *  - Sydney bila skornya > 0 (Sydney jarang dipakai; >0 berarti memang digunakan).
     *  - selain itu Morse (skor 0 tetap valid = "Tidak berisiko"), asalkan form terisi (skala1 ada isinya).
     * null bila tidak ada baris / form tidak terisi.
     */
    private RiskData ambilData(String noRawat) {
        RiskData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select t.tanggal, t.penilaian_jatuhmorse_totalnilai as morse, "
                    + "t.penilaian_jatuhsydney_totalnilai as sydney, "
                    + "ifnull(t.penilaian_jatuhmorse_skala1,'') as morse_isi, "
                    + "ifnull(t.penilaian_jatuhsydney_skala1,'') as sydney_isi, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from penilaian_awal_keperawatan_ranap t "
                    + "inner join reg_periksa rp on rp.no_rawat=t.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter "
                    + "where t.no_rawat=? order by t.tanggal desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                Integer morse = parseInt(nz(r.getString("morse")));
                Integer sydney = parseInt(nz(r.getString("sydney")));
                boolean morseIsi = !nz(r.getString("morse_isi")).trim().equals("");
                boolean sydneyIsi = !nz(r.getString("sydney_isi")).trim().equals("");

                String jenis = "", metode = "";
                int skor = 0;
                if (sydneyIsi && sydney != null && sydney > 0) {
                    jenis = "SYDNEY"; metode = "Sydney Scoring (Ontario STRATIFY)"; skor = sydney;
                } else if (morseIsi) {
                    jenis = "MORSE"; metode = "Morse Fall Scale"; skor = (morse == null ? 0 : morse);
                }

                if (!jenis.equals("")) {
                    String[] risk = deriveRisk(jenis, skor);
                    d = new RiskData();
                    d.jenis = jenis;
                    d.metode = metode;
                    d.total = String.valueOf(skor);
                    d.risk = risk;
                    d.waktu = formatWaktu(nz(r.getString("tanggal")));
                    d.namaPasien = nz(r.getString("nm_pasien"));
                    d.namaDokter = nz(r.getString("nm_dokter"));
                    d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                    d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                    d.idOrg = koneksiDB.IDSATUSEHAT();
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi RiskAssessment ambilData : " + e);
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

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
