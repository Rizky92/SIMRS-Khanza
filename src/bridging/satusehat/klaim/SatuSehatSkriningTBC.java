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
 * Pengiriman Skrining TBC ke SATUSEHAT sebagai FHIR QuestionnaireResponse.
 *
 * Sumber data: tabel skrining_tbc (riwayat kontak, gejala, faktor risiko, kesimpulan). Tiap variabel
 * jadi item (linkId + text + answer valueBoolean/valueString). Idempotent: id deterministik -> PUT.
 *
 * PENTING: ganti QUESTIONNAIRE_TBC dengan canonical Questionnaire Skrining TBC resmi SATUSEHAT, dan
 * sesuaikan linkId + text tiap pertanyaan agar SAMA PERSIS dengan definisi Questionnaire-nya.
 */
public class SatuSehatSkriningTBC {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    // Questionnaire resmi SATUSEHAT: Klasifikasi Kasus Terduga TB.
    private static final String QUESTIONNAIRE_TBC = "https://fhir.kemkes.go.id/Questionnaire/Q0001";

    public SatuSehatSkriningTBC() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi SkriningTBC : " + e);
        }
    }

    private static class SkrData {
        String noRawat="", idEncounter="", idPasien="", namaPasien="", idOrg="", idPetugas="", namaPetugas="", waktu="";
        String riwayatKontak="", kesimpulan="";
        String gBatuk="", gBbTurun="", gDemam="", gKeringat="";
        String rTerdiagnosa="", rBerobat="", rMalnutrisi="", rMerokok="", rDm="", rOdhiv="",
                rLansia="", rHamil="", rWbp="", rKumuh="";
    }

    /**
     * Bangun & kirim QuestionnaireResponse Skrining TBC untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter dari server (WAJIB)
     */
    /** PREVIEW: rakit Bundle Skrining TBC (QuestionnaireResponse) tanpa mengirim; null bila tak berlaku. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        SkrData d = ambilData(noRawat, idEncounter);
        if (d == null || d.idPasien.equals("")) return null;
        String[] kriteria = mapKriteriaTerduga(d.kesimpulan);
        if (kriteria == null) return null;
        String id = stableResourceId("QuestionnaireResponseTBC", noRawat);
        ObjectNode qr = buatQuestionnaireResponse(d, id, kriteria);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "QuestionnaireResponse/" + id);
        entry.set("resource", qr);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "QuestionnaireResponse/" + id);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        SkrData d = ambilData(noRawat, idEncounter);
        if (d == null) {
            return;   // tidak ada baris skrining_tbc
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi SkriningTBC : ID pasien SATUSEHAT belum ada untuk no_rawat "
                    + noRawat + ". Dilewati.");
            return;
        }

        // Q0001 = Klasifikasi Kasus Terduga TB -> hanya berlaku bila kesimpulan "Terduga TBC".
        String[] kriteria = mapKriteriaTerduga(d.kesimpulan);
        if (kriteria == null) {
            System.out.println("Notifikasi SkriningTBC : kesimpulan '" + d.kesimpulan + "' bukan Terduga TBC (no_rawat "
                    + noRawat + "). Q0001 (Klasifikasi Kasus Terduga TB) tidak berlaku. Dilewati.");
            return;
        }
        String id = stableResourceId("QuestionnaireResponseTBC", noRawat);
        ObjectNode qr = buatQuestionnaireResponse(d, id, kriteria);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "QuestionnaireResponse/" + id);
        entry.set("resource", qr);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "QuestionnaireResponse/" + id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Skrining TBC : " + link);
        System.out.println("Request JSON Skrining TBC : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Skrining TBC Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Skrining TBC OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Skrining TBC Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Skrining TBC : " + hasil);
    }

    /** QuestionnaireResponse Q0001 (Klasifikasi Kasus Terduga TB) dari skrining_tbc. */
    private ObjectNode buatQuestionnaireResponse(SkrData d, String id, String[] kriteria) {
        ObjectNode qr = mapper.createObjectNode();
        qr.put("resourceType", "QuestionnaireResponse");
        qr.put("id", id);
        ObjectNode iden = qr.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/creator");
        iden.put("value", d.idOrg);
        qr.put("questionnaire", QUESTIONNAIRE_TBC);
        qr.put("status", "completed");
        ObjectNode subject = qr.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);
        ObjectNode source = qr.putObject("source");
        source.put("reference", "Patient/" + d.idPasien);
        source.put("display", d.namaPasien);
        qr.putObject("encounter").put("reference", "Encounter/" + d.idEncounter);
        if (!d.idPetugas.equals("")) {
            ObjectNode author = qr.putObject("author");
            author.put("reference", "Practitioner/" + d.idPetugas);
            if (!d.namaPetugas.equals("")) author.put("display", d.namaPetugas);
        }
        if (!d.waktu.equals("")) qr.put("authored", d.waktu);

        // Struktur Q0001: item "1" (grup) -> "1.1" Kriteria Terduga TB (valueCoding).
        ObjectNode grup = qr.putArray("item").addObject();
        grup.put("linkId", "1");
        grup.put("text", "Klasifikasi Kasus Terduga TB");
        ObjectNode sub = grup.putArray("item").addObject();
        sub.put("linkId", "1.1");
        sub.put("text", "Kriteria Terduga TB");
        ObjectNode coding = sub.putArray("answer").addObject().putObject("valueCoding");
        coding.put("system", "http://terminology.kemkes.go.id/CodeSystem/clinical-term");
        coding.put("code", kriteria[0]);
        coding.put("display", kriteria[1]);
        return qr;
    }

    /**
     * Map kesimpulan_skrining -> kriteria terduga TB (item 1.1). null bila bukan terduga (Q0001 N/A).
     * QRI000001 Terduga TBC SO | QRI000002 Terduga TBC RO (RO tak bisa ditentukan dari skrining -> default SO).
     */
    private String[] mapKriteriaTerduga(String kesimpulan) {
        if (kesimpulan == null) return null;
        String s = kesimpulan.toLowerCase().trim();
        if (s.contains("terduga") && !s.contains("bukan")) {
            return new String[]{"QRI000001", "Terduga TBC SO"};
        }
        return null;
    }

    /** Ambil 1 baris skrining_tbc + identitas pasien/petugas. null bila tidak ada baris. */
    private SkrData ambilData(String noRawat, String idEncounter) {
        SkrData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select st.tanggal, st.riwayat_kontak_tbc, st.kesimpulan_skrining, "
                    + "st.gejala_tbc_batuk, st.gejala_tbc_bb_turun, st.gejala_tbc_demam, st.gejala_tbc_berkeringat_malam_hari, "
                    + "st.faktor_resiko_pernah_terdiagnosa_tbc, st.faktor_resiko_pernah_berobat_tbc, "
                    + "st.faktor_resiko_malnutrisi, st.faktor_resiko_merokok, st.faktor_resiko_riwayat_dm, "
                    + "st.faktor_resiko_odhiv, st.faktor_resiko_lansia, st.faktor_resiko_ibu_hamil, "
                    + "st.faktor_resiko_wbp, st.faktor_resiko_tinggal_diwilayah_padat_kumuh, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(pg.no_ktp,'') as ktp_petugas, ifnull(pg.nama,'') as nama_petugas "
                    + "from skrining_tbc st "
                    + "inner join reg_periksa rp on rp.no_rawat=st.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join pegawai pg on pg.nik=st.nip "
                    + "where st.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new SkrData();
                d.noRawat = noRawat;
                d.idEncounter = idEncounter;
                d.idOrg = koneksiDB.IDSATUSEHAT();
                d.waktu = formatWaktu(nz(r.getString("tanggal")));
                d.riwayatKontak = nz(r.getString("riwayat_kontak_tbc"));
                d.kesimpulan = nz(r.getString("kesimpulan_skrining"));
                d.gBatuk = nz(r.getString("gejala_tbc_batuk"));
                d.gBbTurun = nz(r.getString("gejala_tbc_bb_turun"));
                d.gDemam = nz(r.getString("gejala_tbc_demam"));
                d.gKeringat = nz(r.getString("gejala_tbc_berkeringat_malam_hari"));
                d.rTerdiagnosa = nz(r.getString("faktor_resiko_pernah_terdiagnosa_tbc"));
                d.rBerobat = nz(r.getString("faktor_resiko_pernah_berobat_tbc"));
                d.rMalnutrisi = nz(r.getString("faktor_resiko_malnutrisi"));
                d.rMerokok = nz(r.getString("faktor_resiko_merokok"));
                d.rDm = nz(r.getString("faktor_resiko_riwayat_dm"));
                d.rOdhiv = nz(r.getString("faktor_resiko_odhiv"));
                d.rLansia = nz(r.getString("faktor_resiko_lansia"));
                d.rHamil = nz(r.getString("faktor_resiko_ibu_hamil"));
                d.rWbp = nz(r.getString("faktor_resiko_wbp"));
                d.rKumuh = nz(r.getString("faktor_resiko_tinggal_diwilayah_padat_kumuh"));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.namaPetugas = nz(r.getString("nama_petugas"));
                String ktpPetugas = nz(r.getString("ktp_petugas"));
                if (!ktpPetugas.equals("")) {
                    d.idPetugas = nz(cek.tampilIDParktisi(ktpPetugas));
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi SkriningTBC ambilData : " + e);
        }
        return d;
    }

    private String formatWaktu(String dt) {
        if (dt == null) return "";
        String t = dt.trim();
        if (t.equals("") || t.startsWith("0000-00-00")) return "";
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
