/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;
import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sub-sender Telaah/Pengkajian Resep (farmasi) sebagai FHIR QuestionnaireResponse Q0007.
 *
 * Sumber: telaah_farmasi (per no_resep) -> di-link ke kunjungan via resep_obat. Satu QuestionnaireResponse
 * per resep yang ditelaah, mengikuti struktur Q0007 (Administrasi 1.x, Farmasetik 2.x, Klinis 3.x, +ref resep).
 * Idempotent: id deterministik (stableResourceId per no_resep) -> PUT.
 */
public class SatuSehatTelaahFarmasi {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    private static final String QUESTIONNAIRE = "https://fhir.kemkes.go.id/Questionnaire/Q0007";
    private static final String CLINICAL_TERM = "http://terminology.kemkes.go.id/CodeSystem/clinical-term";

    public SatuSehatTelaahFarmasi() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi TelaahFarmasi : " + e);
        }
    }

    private static class Telaah {
        String noResep="", waktu="", idMedReq="";
        String identifikasiPasien="", tepatObat="", tepatDosis="", tepatCaraPemberian="", tepatWaktuPemberian="";
        String duplikasi="", interaksi="", kontraindikasi="";
    }

    /** PREVIEW: rakit Bundle Telaah Farmasi (QuestionnaireResponse per resep) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        String idPasien = "", namaPasien = "", idPetugas = "", namaPetugas = "";
        List<Telaah> daftar = new ArrayList<>();
        String idOrg = koneksiDB.IDSATUSEHAT();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select tf.no_resep, tf.resep_identifikasi_pasien, tf.resep_tepat_obat, tf.resep_tepat_dosis, "
                    + "tf.resep_tepat_cara_pemberian, tf.resep_tepat_waktu_pemberian, tf.resep_ada_tidak_duplikasi_obat, "
                    + "tf.resep_interaksi_obat, tf.resep_kontra_indikasi_obat, "
                    + "ro.tgl_peresepan, ro.jam_peresepan, ro.tgl_perawatan, ro.jam, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(pg.no_ktp,'') as ktp_petugas, ifnull(pg.nama,'') as nama_petugas, "
                    + "ifnull(smr.id_medicationrequest,'') as id_medreq "
                    + "from telaah_farmasi tf "
                    + "inner join resep_obat ro on ro.no_resep=tf.no_resep "
                    + "inner join reg_periksa rp on rp.no_rawat=ro.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join pegawai pg on pg.nik=tf.nip "
                    + "left join satu_sehat_medicationrequest smr on smr.no_resep=tf.no_resep "
                    + "where ro.no_rawat=? order by tf.no_resep");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            Set<String> seen = new LinkedHashSet<>();
            while (r.next()) {
                String noResep = nz(r.getString("no_resep"));
                if (!seen.add(noResep)) continue;   // 1 telaah per resep
                Telaah t = new Telaah();
                t.noResep = noResep;
                t.waktu = formatWaktu(nz(r.getString("tgl_peresepan")) + " " + nz(r.getString("jam_peresepan")));
                if (t.waktu.equals("")) {
                    t.waktu = formatWaktu(nz(r.getString("tgl_perawatan")) + " " + nz(r.getString("jam")));
                }
                if (t.waktu.equals("")) {
                    t.waktu = waktuSekarang();
                }
                t.idMedReq = nz(r.getString("id_medreq"));
                t.identifikasiPasien = nz(r.getString("resep_identifikasi_pasien"));
                t.tepatObat = nz(r.getString("resep_tepat_obat"));
                t.tepatDosis = nz(r.getString("resep_tepat_dosis"));
                t.tepatCaraPemberian = nz(r.getString("resep_tepat_cara_pemberian"));
                t.tepatWaktuPemberian = nz(r.getString("resep_tepat_waktu_pemberian"));
                t.duplikasi = nz(r.getString("resep_ada_tidak_duplikasi_obat"));
                t.interaksi = nz(r.getString("resep_interaksi_obat"));
                t.kontraindikasi = nz(r.getString("resep_kontra_indikasi_obat"));
                daftar.add(t);
                if (idPasien.equals("")) {
                    namaPasien = nz(r.getString("nm_pasien"));
                    idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                }
                if (idPetugas.equals("")) {
                    namaPetugas = nz(r.getString("nama_petugas"));
                    String ktp = nz(r.getString("ktp_petugas"));
                    if (!ktp.equals("")) idPetugas = nz(cek.tampilIDParktisi(ktp));
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi TelaahFarmasi bangun : " + e);
        }
        if (daftar.isEmpty()) return null;
        if (idPasien.equals("")) return null;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        for (Telaah t : daftar) {
            String id = stableResourceId("TelaahFarmasi", t.noResep);
            ObjectNode qr = buatQuestionnaireResponse(t, id, idPasien, namaPasien, idEncounter, idPetugas, namaPetugas, idOrg);
            ObjectNode entry = entries.addObject();
            entry.put("fullUrl", "QuestionnaireResponse/" + id);
            entry.set("resource", qr);
            ObjectNode request = entry.putObject("request");
            request.put("method", "PUT");
            request.put("url", "QuestionnaireResponse/" + id);
        }
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        String idPasien = "", namaPasien = "", idPetugas = "", namaPetugas = "";
        List<Telaah> daftar = new ArrayList<>();
        String idOrg = koneksiDB.IDSATUSEHAT();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select tf.no_resep, tf.resep_identifikasi_pasien, tf.resep_tepat_obat, tf.resep_tepat_dosis, "
                    + "tf.resep_tepat_cara_pemberian, tf.resep_tepat_waktu_pemberian, tf.resep_ada_tidak_duplikasi_obat, "
                    + "tf.resep_interaksi_obat, tf.resep_kontra_indikasi_obat, "
                    + "ro.tgl_peresepan, ro.jam_peresepan, ro.tgl_perawatan, ro.jam, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(pg.no_ktp,'') as ktp_petugas, ifnull(pg.nama,'') as nama_petugas, "
                    + "ifnull(smr.id_medicationrequest,'') as id_medreq "
                    + "from telaah_farmasi tf "
                    + "inner join resep_obat ro on ro.no_resep=tf.no_resep "
                    + "inner join reg_periksa rp on rp.no_rawat=ro.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join pegawai pg on pg.nik=tf.nip "
                    + "left join satu_sehat_medicationrequest smr on smr.no_resep=tf.no_resep "
                    + "where ro.no_rawat=? order by tf.no_resep");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            Set<String> seen = new LinkedHashSet<>();
            while (r.next()) {
                String noResep = nz(r.getString("no_resep"));
                if (!seen.add(noResep)) continue;   // 1 telaah per resep
                Telaah t = new Telaah();
                t.noResep = noResep;
                // authored WAJIB di SATUSEHAT -> fallback: tgl_peresepan -> tgl_perawatan -> waktu sekarang.
                t.waktu = formatWaktu(nz(r.getString("tgl_peresepan")) + " " + nz(r.getString("jam_peresepan")));
                if (t.waktu.equals("")) {
                    t.waktu = formatWaktu(nz(r.getString("tgl_perawatan")) + " " + nz(r.getString("jam")));
                }
                if (t.waktu.equals("")) {
                    t.waktu = waktuSekarang();
                }
                t.idMedReq = nz(r.getString("id_medreq"));
                t.identifikasiPasien = nz(r.getString("resep_identifikasi_pasien"));
                t.tepatObat = nz(r.getString("resep_tepat_obat"));
                t.tepatDosis = nz(r.getString("resep_tepat_dosis"));
                t.tepatCaraPemberian = nz(r.getString("resep_tepat_cara_pemberian"));
                t.tepatWaktuPemberian = nz(r.getString("resep_tepat_waktu_pemberian"));
                t.duplikasi = nz(r.getString("resep_ada_tidak_duplikasi_obat"));
                t.interaksi = nz(r.getString("resep_interaksi_obat"));
                t.kontraindikasi = nz(r.getString("resep_kontra_indikasi_obat"));
                daftar.add(t);
                if (idPasien.equals("")) {
                    namaPasien = nz(r.getString("nm_pasien"));
                    idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                }
                if (idPetugas.equals("")) {
                    namaPetugas = nz(r.getString("nama_petugas"));
                    String ktp = nz(r.getString("ktp_petugas"));
                    if (!ktp.equals("")) idPetugas = nz(cek.tampilIDParktisi(ktp));
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi TelaahFarmasi ambilData : " + e);
        }
        if (daftar.isEmpty()) {
            return;   // tidak ada telaah_farmasi untuk kunjungan ini
        }
        if (idPasien.equals("")) {
            System.out.println("Notifikasi TelaahFarmasi : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        for (Telaah t : daftar) {
            String id = stableResourceId("TelaahFarmasi", t.noResep);
            ObjectNode qr = buatQuestionnaireResponse(t, id, idPasien, namaPasien, idEncounter, idPetugas, namaPetugas, idOrg);
            ObjectNode entry = entries.addObject();
            entry.put("fullUrl", "QuestionnaireResponse/" + id);
            entry.set("resource", qr);
            ObjectNode request = entry.putObject("request");
            request.put("method", "PUT");
            request.put("url", "QuestionnaireResponse/" + id);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Telaah Farmasi : " + link);
        System.out.println("Total Telaah Farmasi : " + daftar.size() + " (1 bundle).");
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Request JSON Telaah Farmasi (gagal) : " + payload);
            System.out.println("Error Telaah Farmasi Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Telaah Farmasi OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Telaah Farmasi Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Telaah Farmasi : " + hasil);
    }

    /** QuestionnaireResponse Q0007 (Pengkajian Resep) dari satu baris telaah_farmasi. */
    private ObjectNode buatQuestionnaireResponse(Telaah t, String id, String idPasien, String namaPasien,
            String idEncounter, String idPetugas, String namaPetugas, String idOrg) {
        ObjectNode qr = mapper.createObjectNode();
        qr.put("resourceType", "QuestionnaireResponse");
        qr.put("id", id);
        ObjectNode iden = qr.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/creator");
        iden.put("value", idOrg);
        qr.put("questionnaire", QUESTIONNAIRE);
        qr.put("status", "completed");
        ObjectNode subject = qr.putObject("subject");
        subject.put("reference", "Patient/" + idPasien);
        subject.put("display", namaPasien);
        ObjectNode source = qr.putObject("source");
        source.put("reference", "Patient/" + idPasien);
        source.put("display", namaPasien);
        qr.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!idPetugas.equals("")) {
            ObjectNode author = qr.putObject("author");
            author.put("reference", "Practitioner/" + idPetugas);
            if (!namaPetugas.equals("")) author.put("display", namaPetugas);
        }
        if (!t.waktu.equals("")) qr.put("authored", t.waktu);

        // Struktur Q0007: grup "1" Administrasi berisi 1.x + grup 2 (Farmasetik) + grup 3 (Klinis) + item 4.
        ObjectNode g1 = qr.putArray("item").addObject();
        g1.put("linkId", "1");
        g1.put("text", "Persyaratan Administrasi");
        ArrayNode i1 = g1.putArray("item");
        itemCoding(i1, "1.1", "Apakah nama, umur, jenis kelamin, berat badan dan tinggi badan pasien sudah sesuai?", t.identifikasiPasien);
        itemCoding(i1, "1.2", "Apakah nama, nomor ijin, alamat dan paraf dokter sudah sesuai?", "Ya");
        itemCoding(i1, "1.3", "Apakah tanggal resep sudah sesuai?", "Ya");
        itemCoding(i1, "1.4", "Apakah ruangan/unit asal resep sudah sesuai?", "Ya");

        ObjectNode g2 = i1.addObject();
        g2.put("linkId", "2");
        g2.put("text", "Persyaratan Farmasetik");
        ArrayNode i2 = g2.putArray("item");
        itemCoding(i2, "2.1", "Apakah nama obat, bentuk dan kekuatan sediaan sudah sesuai?", t.tepatObat);
        itemCoding(i2, "2.2", "Apakah dosis dan jumlah obat sudah sesuai?", t.tepatDosis);
        itemCoding(i2, "2.3", "Apakah stabilitas obat sudah sesuai?", "Ya");
        itemCoding(i2, "2.4", "Apakah aturan dan cara penggunaan obat sudah sesuai?", t.tepatCaraPemberian);

        ObjectNode g3 = i1.addObject();
        g3.put("linkId", "3");
        g3.put("text", "Persyaratan Klinis");
        ArrayNode i3 = g3.putArray("item");
        itemCoding(i3, "3.1", "Apakah ketepatan indikasi, dosis, dan waktu penggunaan obat sudah sesuai?", t.tepatWaktuPemberian);
        itemBool(i3, "3.2", "Apakah terdapat duplikasi pengobatan?", t.duplikasi);
        itemBool(i3, "3.3", "Apakah terdapat alergi dan reaksi obat yang tidak dikehendaki (ROTD)?", "Tidak");
        itemBool(i3, "3.4", "Apakah terdapat kontraindikasi pengobatan?", t.kontraindikasi);
        itemBool(i3, "3.5", "Apakah terdapat dampak interaksi obat?", t.interaksi);

        // Resep yang dikaji -> referensi MedicationRequest (bila ada).
        if (!t.idMedReq.equals("")) {
            ObjectNode i4 = i1.addObject();
            i4.put("linkId", "4");
            i4.put("text", "Resep yang dilakukan pengkajian resep");
            i4.putArray("answer").addObject().putObject("valueReference")
                    .put("reference", "MedicationRequest/" + t.idMedReq);
        }
        return qr;
    }

    /** Item jawaban Sesuai/Tidak Sesuai (valueCoding clinical-term). Default Sesuai bila bukan "Tidak". */
    private void itemCoding(ArrayNode parent, String linkId, String text, String yaTidak) {
        boolean sesuai = yaTidak == null || !yaTidak.equalsIgnoreCase("Tidak");
        ObjectNode it = parent.addObject();
        it.put("linkId", linkId);
        it.put("text", text);
        ObjectNode coding = it.putArray("answer").addObject().putObject("valueCoding");
        coding.put("system", CLINICAL_TERM);
        coding.put("code", sesuai ? "OV000052" : "OV000053");
        coding.put("display", sesuai ? "Sesuai" : "Tidak Sesuai");
    }

    /** Item jawaban boolean (Ya->true, lainnya->false). */
    private void itemBool(ArrayNode parent, String linkId, String text, String yaTidak) {
        ObjectNode it = parent.addObject();
        it.put("linkId", linkId);
        it.put("text", text);
        it.putArray("answer").addObject().put("valueBoolean", "Ya".equalsIgnoreCase(yaTidak));
    }

    private String formatWaktu(String dt) {
        if (dt == null) return "";
        String t = dt.trim();
        if (t.equals("") || t.startsWith("0000-00-00")) return "";
        if (t.contains(".")) t = t.substring(0, t.indexOf('.'));
        if (t.contains(" ")) t = t.replace(" ", "T");
        // Wajib ada tanggal valid (YYYY-MM-DD) di depan; kalau cuma jam/kosong -> abaikan (authored di-omit).
        if (!t.matches("\\d{4}-\\d{2}-\\d{2}.*")) return "";
        if (t.length() == 10) t = t + "T00:00:00";
        if (t.length() == 16) t = t + ":00";
        return t + "+07:00";
    }

    /** Waktu sekarang (zona +07:00) ISO -> fallback authored bila tanggal sumber tidak ada. */
    private String waktuSekarang() {
        return java.time.OffsetDateTime.now(java.time.ZoneOffset.ofHours(7)).withNano(0)
                .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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
