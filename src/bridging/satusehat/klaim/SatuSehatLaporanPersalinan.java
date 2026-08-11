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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Pengiriman "Laporan Persalinan" (FHIR Composition - LOINC 57057-2, Labor and delivery summary note)
 * ke SATUSEHAT, mengacu Modul INC (Intrapartum Care).
 *
 * Satu Bundle transaction berisi:
 *   - Observation Pelayanan Persalinan (Keadaan Ibu, Penolong, Cara Persalinan, Kala I-IV)
 *   - Condition diagnosis/komplikasi persalinan
 *   - Procedure tindakan persalinan (ICD-9-CM)
 *   - Observation Data Bayi Baru Lahir per bayi (Berat, Panjang, APGAR 1/5/10)
 *   - Composition yang mengindeks semuanya
 *
 * Sumber data ibu: catatan_persalinan. Sumber data bayi: pasien_bayi yang dicapai lewat
 * ranap_gabung (relasi ibu-bayi yang dicatat petugas), dengan cadangan kolom bayi di
 * catatan_persalinan bila kunjungan bayi tidak pernah digabungkan. Idempotent via business
 * identifier + ifNoneExist (kirim ulang tidak menduplikasi resource di server).
 */
public class SatuSehatLaporanPersalinan {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private String performerRef = "";   // Practitioner/{id} penolong (Observation.performer)
    /** no_rawat yang sedang dirakit; dipakai tambahEntry untuk membaca id Composition tersimpan. */
    private String curNoRawat = "";

    public SatuSehatLaporanPersalinan() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Laporan Persalinan : " + e);
        }
        pastikanTabel();
    }

    /**
     * Tabel pelacak id Composition. Sebelumnya id Persalinan hanya hidup di server dan dicari ulang
     * lewat GET tiap kirim, sehingga status "sudah/belum kirim" tak bisa dijawab dari basis data
     * lokal — itu yang dibutuhkan panel status di form RME.
     */
    private void pastikanTabel() {
        try (PreparedStatement p = koneksi.prepareStatement(
                "create table if not exists satu_sehat_laporan_persalinan ("
                + "no_rawat varchar(17) not null, id_composition varchar(50) default '', "
                + "primary key (no_rawat)) engine=InnoDB default charset=latin1")) {
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan pastikanTabel : " + e);
        }
    }

    /** Id Composition tersimpan. "" bila belum pernah dikirim dari instalasi ini. */
    private String ambilIdLokal(String noRawat) {
        if (noRawat == null || noRawat.equals("")) {
            return "";
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(id_composition,'') as id_composition from satu_sehat_laporan_persalinan "
                + "where no_rawat=? limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return nz(r.getString("id_composition"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan ambilIdLokal : " + e);
        }
        return "";
    }

    /** Upsert id Composition (idempotent per no_rawat). */
    private void simpanIdLokal(String noRawat, String idComposition) {
        if (idComposition == null || idComposition.equals("")) {
            return;
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "replace into satu_sehat_laporan_persalinan (no_rawat, id_composition) values (?,?)")) {
            p.setString(1, noRawat);
            p.setString(2, idComposition);
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan simpanIdLokal : " + e);
        }
    }

    /**
     * Id Composition setelah pengiriman: kalau entry-nya PUT, fullUrl sudah "Composition/{id}";
     * kalau POST, id-nya baru diketahui dari transaction-response pada indeks entry yang sama.
     */
    private String idComposition(String fullUrlComp, String responseBody, int indeks) {
        String fu = nz(fullUrlComp);
        if (fu.startsWith("Composition/")) {
            return fu.substring("Composition/".length());
        }
        try {
            JsonNode resp = mapper.readTree(responseBody).path("entry").path(indeks).path("response");
            String id = nz(resp.path("resourceID").asText());
            if (!id.equals("")) {
                return id;
            }
            String loc = nz(resp.path("location").asText());   // .../Composition/{id}/_history/{v}
            String[] bagian = loc.split("/");
            for (int i = 0; i < bagian.length - 1; i++) {
                if (bagian[i].equals("Composition")) {
                    return bagian[i + 1];
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan idComposition : " + e);
        }
        return "";
    }

    /** Data persalinan ibu (catatan_persalinan + header). */
    private static class PersalinanData {
        String noRawat="", noRkmMedis="", waktu="";
        String kondisiUmum="", td="", nadi="", suhu="", rr="";
        String statusLahir="", kelainan="", ketuban="";
        String placenta="", taliPusat="", insertio="", ukuran="";
        String kontraksiUterus="", perineum="", jahitanDalam1="", jahitanDalam2="", jahitanLuar1="", jahitanLuar2="";
        String waktuKala1="", waktuKala2="", waktuKala3="", waktuJumlah="";
        String darahKala1="", darahKala2="", darahKala3="", darahKala4="", darahJumlah="";
        String catatan="", pengobatan="";
        String idIbu="", namaIbu="", idPenolong="", namaPenolong="", idOrg="";
        // Kolom bayi yang menempel di catatan_persalinan; dipakai bila jalur ranap_gabung kosong.
        String anak="", apgarScore="", bbBayi="", pbBayi="";
    }

    /**
     * Data bayi baru lahir. Sumber utama pasien_bayi (dicapai lewat ranap_gabung), sumber cadangan
     * kolom bayi di catatan_persalinan bila kunjungan bayi tidak pernah digabungkan ke kunjungan ibu.
     */
    private static class BayiData {
        String rmBayi="", anakke="", beratBadan="", panjangBadan="", lingkarKepala="";
        Double apgar1=null, apgar5=null, apgar10=null;
        String jenisKelamin="", tglLahir="", jamLahir="", prosesLahir="";
    }

    /** Satu section "Data Bayi Baru Lahir": referensi Observation + narasi identitas bayi. */
    private static class SectionBayi {
        final List<String> refs = new ArrayList<>();
        String narasi = "";
    }

    /**
     * Bangun & kirim Bundle Laporan Persalinan untuk satu kunjungan ibu.
     * @param noRawat     no_rawat kunjungan ibu (yang ada catatan_persalinan-nya)
     * @param idEncounter id Encounter SATUSEHAT ibu (WAJIB — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Laporan Persalinan (Observation/Condition/Procedure + Composition) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        PersalinanData d = ambilData(noRawat);
        if (d == null) return null;
        if (d.idIbu.equals("")) return null;

        String patientRef = "Patient/" + d.idIbu;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu = d.waktu;
        performerRef = d.idPenolong.equals("") ? "" : ("Practitioner/" + d.idPenolong);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        List<String> refKeadaanIbu = new ArrayList<>();
        List<String> refPelayanan = new ArrayList<>();
        List<String> refDiagnosis = new ArrayList<>();
        List<String> refTindakan = new ArrayList<>();

        String keadaan = gabung("KU: " + d.kondisiUmum, d.td.equals("") ? "" : ("TD " + d.td),
                d.nadi.equals("") ? "" : ("Nadi " + d.nadi), d.suhu.equals("") ? "" : ("Suhu " + d.suhu),
                d.rr.equals("") ? "" : ("RR " + d.rr));
        if (!keadaan.equals("")) {
            refKeadaanIbu.add(tambahObsTeks(entries, d, "keadaanibu", "10210-3",
                    "Physical findings of General status Narrative", "exam", keadaan, patientRef, encounterRef, waktu));
        }
        if (!d.namaPenolong.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "penolong", "48767-8", "Annotation comment", "exam",
                    "Penolong persalinan: " + d.namaPenolong, patientRef, encounterRef, waktu));
        }
        String cara = gabung(d.statusLahir.equals("") ? "" : ("Cara: " + d.statusLahir),
                d.ketuban.equals("") ? "" : ("Ketuban: " + d.ketuban),
                d.kelainan.equals("") ? "" : ("Kelainan: " + d.kelainan));
        if (!cara.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "carapersalinan", "48767-8", "Annotation comment", "exam",
                    cara, patientRef, encounterRef, waktu));
        }
        String kala1 = gabungLabel("Lama Kala I", d.waktuKala1, "Perdarahan", d.darahKala1);
        if (!kala1.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala1", "48767-8", "Annotation comment", "exam",
                    kala1, patientRef, encounterRef, waktu));
        }
        String kala2 = gabungLabel("Lama Kala II", d.waktuKala2, "Perdarahan", d.darahKala2);
        if (!kala2.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala2", "48767-8", "Annotation comment", "exam",
                    kala2, patientRef, encounterRef, waktu));
        }
        String kala3 = gabung(gabungLabel("Lama Kala III", d.waktuKala3, "Perdarahan", d.darahKala3),
                d.placenta.equals("") ? "" : ("Plasenta: " + d.placenta),
                d.taliPusat.equals("") ? "" : ("Tali pusat: " + d.taliPusat),
                d.insertio.equals("") ? "" : ("Insertio: " + d.insertio));
        if (!kala3.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala3", "48767-8", "Annotation comment", "exam",
                    kala3, patientRef, encounterRef, waktu));
        }
        String kala4 = gabung(d.darahKala4.equals("") ? "" : ("Perdarahan Kala IV: " + d.darahKala4),
                d.kontraksiUterus.equals("") ? "" : ("Kontraksi uterus: " + d.kontraksiUterus),
                d.perineum.equals("") ? "" : ("Perineum: " + d.perineum),
                jahitanNarasi(d));
        if (!kala4.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala4", "48767-8", "Annotation comment", "exam",
                    kala4, patientRef, encounterRef, waktu));
        }

        for (String[] dx : ambilDiagnosa(noRawat)) {
            refDiagnosis.add(tambahCondition(entries, d, dx[0], dx[1], patientRef, encounterRef, waktu));
        }
        for (String[] pr : ambilProsedur(noRawat)) {
            refTindakan.add(tambahProcedure(entries, d, pr[0], pr[1], patientRef, encounterRef, waktu));
        }

        List<SectionBayi> refBayi = rakitBayi(entries, d, patientRef, encounterRef, waktu);

        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef, waktu,
                refKeadaanIbu, refPelayanan, refDiagnosis, refTindakan, refBayi);
        tambahEntry(entries, comp, "Composition",
                "http://sys-ids.kemkes.go.id/composition/" + d.idOrg, "PERSALINAN-" + noRawat);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            System.out.println("Notifikasi Laporan Persalinan : Encounter belum ada untuk no_rawat " + noRawat
                    + ". Dilewati (kirim Encounter dulu).");
            return;
        }
        PersalinanData d = ambilData(noRawat);
        if (d == null) {
            System.out.println("Notifikasi Laporan Persalinan : catatan_persalinan tidak ada untuk no_rawat "
                    + noRawat + ". Dilewati.");
            return;
        }
        if (d.idIbu.equals("")) {
            System.out.println("Notifikasi Laporan Persalinan : ID pasien (ibu) SATUSEHAT belum ada untuk no_rawat "
                    + noRawat + ". Dilewati.");
            return;
        }

        String patientRef = "Patient/" + d.idIbu;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu = d.waktu;
        performerRef = d.idPenolong.equals("") ? "" : ("Practitioner/" + d.idPenolong);
        curNoRawat = noRawat;

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        // Referensi tiap resource untuk dipakai Composition.section.entry.
        List<String> refKeadaanIbu = new ArrayList<>();
        List<String> refPelayanan = new ArrayList<>();   // penolong, cara, kala1-4 (selain keadaan ibu)
        List<String> refDiagnosis = new ArrayList<>();
        List<String> refTindakan = new ArrayList<>();

        // --- Observation Keadaan Ibu ---
        String keadaan = gabung("KU: " + d.kondisiUmum, d.td.equals("") ? "" : ("TD " + d.td),
                d.nadi.equals("") ? "" : ("Nadi " + d.nadi), d.suhu.equals("") ? "" : ("Suhu " + d.suhu),
                d.rr.equals("") ? "" : ("RR " + d.rr));
        if (!keadaan.equals("")) {
            refKeadaanIbu.add(tambahObsTeks(entries, d, "keadaanibu", "10210-3",
                    "Physical findings of General status Narrative", "exam", keadaan, patientRef, encounterRef, waktu));
        }
        // --- Observation Penolong Persalinan ---
        if (!d.namaPenolong.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "penolong", "48767-8", "Annotation comment", "exam",
                    "Penolong persalinan: " + d.namaPenolong, patientRef, encounterRef, waktu));
        }
        // --- Observation Cara Persalinan ---
        String cara = gabung(d.statusLahir.equals("") ? "" : ("Cara: " + d.statusLahir),
                d.ketuban.equals("") ? "" : ("Ketuban: " + d.ketuban),
                d.kelainan.equals("") ? "" : ("Kelainan: " + d.kelainan));
        if (!cara.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "carapersalinan", "48767-8", "Annotation comment", "exam",
                    cara, patientRef, encounterRef, waktu));
        }
        // --- Observation Kala I-IV ---
        String kala1 = gabungLabel("Lama Kala I", d.waktuKala1, "Perdarahan", d.darahKala1);
        if (!kala1.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala1", "48767-8", "Annotation comment", "exam",
                    kala1, patientRef, encounterRef, waktu));
        }
        String kala2 = gabungLabel("Lama Kala II", d.waktuKala2, "Perdarahan", d.darahKala2);
        if (!kala2.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala2", "48767-8", "Annotation comment", "exam",
                    kala2, patientRef, encounterRef, waktu));
        }
        String kala3 = gabung(gabungLabel("Lama Kala III", d.waktuKala3, "Perdarahan", d.darahKala3),
                d.placenta.equals("") ? "" : ("Plasenta: " + d.placenta),
                d.taliPusat.equals("") ? "" : ("Tali pusat: " + d.taliPusat),
                d.insertio.equals("") ? "" : ("Insertio: " + d.insertio));
        if (!kala3.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala3", "48767-8", "Annotation comment", "exam",
                    kala3, patientRef, encounterRef, waktu));
        }
        String kala4 = gabung(d.darahKala4.equals("") ? "" : ("Perdarahan Kala IV: " + d.darahKala4),
                d.kontraksiUterus.equals("") ? "" : ("Kontraksi uterus: " + d.kontraksiUterus),
                d.perineum.equals("") ? "" : ("Perineum: " + d.perineum),
                jahitanNarasi(d));
        if (!kala4.equals("")) {
            refPelayanan.add(tambahObsTeks(entries, d, "kala4", "48767-8", "Annotation comment", "exam",
                    kala4, patientRef, encounterRef, waktu));
        }

        // --- Condition diagnosis/komplikasi persalinan ---
        for (String[] dx : ambilDiagnosa(noRawat)) {
            refDiagnosis.add(tambahCondition(entries, d, dx[0], dx[1], patientRef, encounterRef, waktu));
        }
        // --- Procedure tindakan persalinan (ICD-9-CM) ---
        for (String[] pr : ambilProsedur(noRawat)) {
            refTindakan.add(tambahProcedure(entries, d, pr[0], pr[1], patientRef, encounterRef, waktu));
        }

        // --- Observation Data Bayi Baru Lahir (per bayi) ---
        List<SectionBayi> refBayi = rakitBayi(entries, d, patientRef, encounterRef, waktu);

        // --- Composition (Laporan Persalinan) ---
        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef, waktu,
                refKeadaanIbu, refPelayanan, refDiagnosis, refTindakan, refBayi);
        String fullUrlComp = tambahEntry(entries, comp, "Composition",
                "http://sys-ids.kemkes.go.id/composition/" + d.idOrg, "PERSALINAN-" + noRawat);
        int indeksComp = entries.size() - 1;   // Composition selalu entry terakhir

        // === Kirim Bundle ===
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Laporan Persalinan : " + link);
        System.out.println("Request JSON Persalinan : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (°, ±, en/em-dash) rusak jadi "?"/"ï¿½" di server. Bytes UTF-8 = server baca benar.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Persalinan Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Persalinan OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Persalinan Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Persalinan : " + hasil);
        // Catat id agar kiriman berikutnya tak perlu GET dan status "Sudah Kirim" terbaca lokal.
        simpanIdLokal(noRawat, idComposition(fullUrlComp, hasil, indeksComp));
    }

    // ====================== ENTRY BUNDLE (POST + ifNoneExist) ======================

    /** Tambah Observation valueString ke bundle; return fullUrl (urn:uuid) untuk referensi Composition. */
    private String tambahObsTeks(ArrayNode entries, PersalinanData d, String slot, String loinc, String display,
            String kategori, String valueString, String patientRef, String encounterRef, String waktu) {
        ObjectNode o = dasarObservation(loinc, display, kategori, patientRef, d.namaIbu, encounterRef, waktu);
        o.put("valueString", valueString);
        return tambahEntry(entries, o, "Observation",
                "http://sys-ids.kemkes.go.id/observation/" + d.idOrg, d.noRawat + "-" + slot);
    }

    /** Tambah Observation valueQuantity (UCUM) ke bundle; return fullUrl. */
    private String tambahObsKuantitas(ArrayNode entries, PersalinanData d, String slot, String loinc, String display,
            String kategori, double nilai, String unit, String ucum, String patientRef, String encounterRef, String waktu) {
        ObjectNode o = dasarObservation(loinc, display, kategori, patientRef, d.namaIbu, encounterRef, waktu);
        ObjectNode value = o.putObject("valueQuantity");
        value.put("value", nilai);
        value.put("unit", unit);
        value.put("system", "http://unitsofmeasure.org");
        value.put("code", ucum);
        return tambahEntry(entries, o, "Observation",
                "http://sys-ids.kemkes.go.id/observation/" + d.idOrg, d.noRawat + "-" + slot);
    }

    /**
     * Rakit Observation bayi baru lahir ke bundle + narasi identitas per bayi.
     * Dipanggil bangun() dan kirim() supaya isi preview tidak pernah berbeda dari yang dikirim.
     *
     * effectiveDateTime memakai waktu lahir bayi bila diketahui (lebih benar secara klinis daripada
     * waktu selesai persalinan ibu), dan jatuh ke waktu persalinan bila tanggal lahir tidak ada.
     */
    private List<SectionBayi> rakitBayi(ArrayNode entries, PersalinanData d, String patientRef,
            String encounterRef, String waktu) {
        List<SectionBayi> hasil = new ArrayList<>();
        List<BayiData> daftar = ambilBayi(d);
        int urut = 0;
        for (BayiData b : daftar) {
            urut++;
            SectionBayi s = new SectionBayi();
            // RM bayi dipakai sebagai pembeda identifier karena stabil lintas kiriman; nomor urut
            // hanya dipakai untuk data cadangan yang tidak punya record pasien bayi.
            String suffix = "bayi" + (b.rmBayi.equals("") ? String.valueOf(urut) : normal(b.rmBayi));
            String waktuBayi = pilihTeks(formatWaktu(gabungTanggalJam(b.tglLahir, b.jamLahir)), waktu);
            Double bb = parseAngka(b.beratBadan);
            if (bb != null) {
                s.refs.add(tambahObsKuantitas(entries, d, suffix + "-bb", "8339-4", "Birth weight Measured",
                        "vital-signs", bb, "g", "g", patientRef, encounterRef, waktuBayi));
            }
            Double pb = parseAngka(b.panjangBadan);
            if (pb != null) {
                s.refs.add(tambahObsKuantitas(entries, d, suffix + "-pb", "8305-5", "Body height --at birth",
                        "vital-signs", pb, "cm", "cm", patientRef, encounterRef, waktuBayi));
            }
            if (b.apgar1 != null) {
                s.refs.add(tambahObsKuantitas(entries, d, suffix + "-apgar1", "9272-6", "1 minute Apgar Score",
                        "survey", b.apgar1, "{score}", "{score}", patientRef, encounterRef, waktuBayi));
            }
            if (b.apgar5 != null) {
                s.refs.add(tambahObsKuantitas(entries, d, suffix + "-apgar5", "9274-2", "5 minute Apgar Score",
                        "survey", b.apgar5, "{score}", "{score}", patientRef, encounterRef, waktuBayi));
            }
            if (b.apgar10 != null) {
                s.refs.add(tambahObsKuantitas(entries, d, suffix + "-apgar10", "9271-8", "10 minute Apgar Score",
                        "survey", b.apgar10, "{score}", "{score}", patientRef, encounterRef, waktuBayi));
            }
            s.narasi = narasiBayi(b);
            if (!s.refs.isEmpty()) hasil.add(s);   // section tanpa entry akan dilewati Composition
        }
        return hasil;
    }

    /**
     * Identitas bayi yang tidak punya Observation sendiri (anak ke, jenis kelamin, waktu & proses
     * lahir, lingkar kepala) dititipkan sebagai narasi section supaya tidak hilang dari laporan.
     */
    private String narasiBayi(BayiData b) {
        String lahir = tampilTanggalJam(b.tglLahir, b.jamLahir);
        return gabung(
                b.anakke.trim().equals("") ? "" : ("Anak ke-" + b.anakke.trim()),
                b.jenisKelamin.equals("") ? "" : ("Jenis kelamin: " + b.jenisKelamin),
                lahir.equals("") ? "" : ("Lahir: " + lahir),
                b.prosesLahir.equals("") ? "" : ("Proses lahir: " + b.prosesLahir),
                b.lingkarKepala.trim().equals("") ? "" : ("Lingkar kepala: " + b.lingkarKepala.trim() + " cm"));
    }

    /** Tambah Condition diagnosis persalinan (ICD-10); return fullUrl. */
    private String tambahCondition(ArrayNode entries, PersalinanData d, String kode, String nama,
            String patientRef, String encounterRef, String waktu) {
        ObjectNode c = mapper.createObjectNode();
        c.put("resourceType", "Condition");
        ObjectNode clinical = c.putObject("clinicalStatus").putArray("coding").addObject();
        clinical.put("system", "http://terminology.hl7.org/CodeSystem/condition-clinical");
        clinical.put("code", "active");
        clinical.put("display", "Active");
        ObjectNode catCoding = c.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/condition-category");
        catCoding.put("code", "encounter-diagnosis");
        catCoding.put("display", "Encounter Diagnosis");
        ObjectNode code = c.putObject("code");
        if (!kode.equals("")) {
            ObjectNode coding = code.putArray("coding").addObject();
            coding.put("system", "http://hl7.org/fhir/sid/icd-10");
            coding.put("code", kode);
            coding.put("display", nama.equals("") ? kode : nama);
        }
        code.put("text", nama.equals("") ? kode : nama);
        ObjectNode subject = c.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", d.namaIbu);
        c.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) c.put("recordedDate", waktu);
        return tambahEntry(entries, c, "Condition",
                "http://sys-ids.kemkes.go.id/condition/" + d.idOrg, d.noRawat + "-" + normal(kode));
    }

    /** Tambah Procedure tindakan persalinan (ICD-9-CM); return fullUrl. */
    private String tambahProcedure(ArrayNode entries, PersalinanData d, String kode, String nama,
            String patientRef, String encounterRef, String waktu) {
        ObjectNode pr = mapper.createObjectNode();
        pr.put("resourceType", "Procedure");
        pr.put("status", "completed");
        ObjectNode code = pr.putObject("code");
        ObjectNode coding = code.putArray("coding").addObject();
        coding.put("system", "http://hl7.org/fhir/sid/icd-9-cm");
        coding.put("code", kode);
        coding.put("display", nama.equals("") ? kode : nama);
        code.put("text", nama.equals("") ? kode : nama);
        ObjectNode subject = pr.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", d.namaIbu);
        pr.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) pr.put("performedDateTime", waktu);
        if (!performerRef.equals("")) {
            pr.putArray("performer").addObject().putObject("actor").put("reference", performerRef);
        }
        return tambahEntry(entries, pr, "Procedure",
                "http://sys-ids.kemkes.go.id/procedure/" + d.idOrg, d.noRawat + "-" + normal(kode));
    }

    /**
     * Tambah resource ke bundle dengan idempotensi kuat: cari id existing di server lalu PUT (update);
     * kalau belum ada, POST + ifNoneExist. Composition tak bisa dicari by identifier -> dicari lewat
     * encounter. return fullUrl (urn:uuid bila baru, atau "{Type}/{id}" bila update).
     */
    private String tambahEntry(ArrayNode entries, ObjectNode resource, String resourceType,
            String idenSystem, String idenValue) {
        if (!resource.has("identifier")) {
            ArrayNode arr = mapper.createArrayNode();
            ObjectNode iden = arr.addObject();
            iden.put("system", idenSystem);
            iden.put("value", idenValue);
            resource.set("identifier", arr);
        }
        String idLama;
        if (resourceType.equals("Composition")) {
            // Id lokal lebih dulu (murah, tanpa jaringan); baru jatuh ke pencarian lewat encounter
            // untuk kunjungan yang dikirim sebelum pelacakan lokal ini ada.
            idLama = ambilIdLokal(curNoRawat);
            if (idLama.equals("")) {
                String idEnc = resource.path("encounter").path("reference").asText().replace("Encounter/", "");
                idLama = cariIdCompositionServer(idEnc, idenValue);
            }
        } else {
            idLama = cariIdServerByIdentifier(resourceType, idenSystem, idenValue);
        }
        boolean ada = idLama != null && !idLama.equals("");
        String fullUrl = ada ? (resourceType + "/" + idLama) : ("urn:uuid:" + UUID.randomUUID().toString());
        if (ada) resource.put("id", idLama);
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", fullUrl);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        if (ada) {
            request.put("method", "PUT");
            request.put("url", resourceType + "/" + idLama);
        } else {
            request.put("method", "POST");
            request.put("url", resourceType);
            request.put("ifNoneExist", "identifier=" + idenSystem + "|" + idenValue);
        }
        return fullUrl;
    }

    /** GET {Type}?identifier={system|value} -> id pertama (atau "" bila tidak ada / gagal). */
    private String cariIdServerByIdentifier(String resourceType, String system, String value) {
        if (system == null || value == null || system.equals("") || value.equals("")) return "";
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            String token = java.net.URLEncoder.encode(system + "|" + value, "UTF-8");
            java.net.URI uri = java.net.URI.create(link + "/" + resourceType + "?identifier=" + token + "&_count=1");
            JsonNode r = mapper.readTree(api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody());
            if (r.path("total").asInt(0) > 0) {
                JsonNode es = r.path("entry");
                if (es.isArray() && es.size() > 0) {
                    String id = es.get(0).path("resource").path("id").asText();
                    return id == null ? "" : id;
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan cariId (" + resourceType + ") : " + e);
        }
        return "";
    }

    /** Cari Composition Laporan Persalinan existing lewat encounter (identifier PERSALINAN- / type 57057-2). */
    private String cariIdCompositionServer(String idEncounter, String idenValue) {
        if (idEncounter == null || idEncounter.equals("")) return "";
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            java.net.URI uri = java.net.URI.create(link + "/Composition?encounter=" + idEncounter + "&_count=100");
            JsonNode r = mapper.readTree(api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody());
            JsonNode es = r.path("entry");
            if (es.isArray()) {
                for (JsonNode e : es) {
                    JsonNode res = e.path("resource");
                    // identifier bisa object tunggal atau array.
                    JsonNode iden = res.path("identifier");
                    String v = iden.isArray() ? (iden.size() > 0 ? iden.get(0).path("value").asText() : "") : iden.path("value").asText();
                    if (idenValue.equals(v)) return res.path("id").asText();
                    for (JsonNode cd : res.path("type").path("coding")) {
                        if ("57057-2".equals(cd.path("code").asText())) return res.path("id").asText();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan cariIdComposition : " + e);
        }
        return "";
    }

    // ====================== BUILDER ======================

    /** Kerangka Observation: status, category, code LOINC, subject, encounter, effectiveDateTime, performer. */
    private ObjectNode dasarObservation(String loinc, String display, String kategori,
            String patientRef, String namaPasien, String encounterRef, String waktu) {
        ObjectNode o = mapper.createObjectNode();
        o.put("resourceType", "Observation");
        o.put("status", "final");
        ObjectNode catCoding = o.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        catCoding.put("code", kategori);
        catCoding.put("display", kategori.equals("vital-signs") ? "Vital Signs"
                : (kategori.equals("exam") ? "Exam" : "Survey"));
        ObjectNode coding = o.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        ObjectNode subject = o.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        o.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) o.put("effectiveDateTime", waktu);
        if (!performerRef.equals("")) {
            o.putArray("performer").addObject().put("reference", performerRef);
        }
        return o;
    }

    /** Composition Laporan Persalinan (57057-2) yang mengindeks resource via section.entry. */
    private ObjectNode buatComposition(String noRawat, PersalinanData d, String patientRef, String encounterRef,
            String waktu, List<String> refKeadaanIbu, List<String> refPelayanan, List<String> refDiagnosis,
            List<String> refTindakan, List<SectionBayi> refBayi) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "PERSALINAN-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "57057-2");
        typeCoding.put("display", "Labor and delivery summary note");
        comp.putObject("subject").put("reference", patientRef);
        comp.putObject("encounter").put("reference", encounterRef);
        if (!d.idPenolong.equals("")) {
            ObjectNode author = comp.putArray("author").addObject();
            author.put("reference", "Practitioner/" + d.idPenolong);
            author.put("display", d.namaPenolong);
        }
        if (!waktu.equals("")) comp.put("date", waktu);
        comp.put("title", "Laporan Persalinan");
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Laporan Persalinan - "
                + escapeXml(d.namaIbu) + "</div>");

        ArrayNode section = comp.putArray("section");
        // Ringkasan Persalinan (narasi)
        String ringkasan = pilihTeks(d.catatan, "Persalinan " + d.statusLahir);
        buatSectionNarasi(section, "Ringkasan Persalinan", ringkasan);
        // Pelayanan Persalinan (keadaan ibu + penolong + cara + kala)
        List<String> pelayanan = new ArrayList<>();
        pelayanan.addAll(refKeadaanIbu);
        pelayanan.addAll(refPelayanan);
        buatSectionEntry(section, "Pelayanan Persalinan", "11486-8", "Hospital discharge studies summary", pelayanan, null);
        // Diagnosis
        buatSectionEntry(section, "Diagnosis", "29548-5", "Diagnosis", refDiagnosis, null);
        // Tindakan Persalinan
        buatSectionEntry(section, "Tindakan Persalinan", "29554-3", "Procedure Narrative", refTindakan, null);
        // Data Bayi Baru Lahir (satu section per bayi; dinomori hanya bila bayinya memang lebih dari satu)
        int n = 1;
        for (SectionBayi sb : refBayi) {
            String judul = refBayi.size() > 1 ? ("Data Bayi Baru Lahir " + n) : "Data Bayi Baru Lahir";
            buatSectionEntry(section, judul, "57075-4", "Newborn delivery information", sb.refs, sb.narasi);
            n++;
        }
        // Procedure Description Section (narasi)
        ObjectNode secDesc = buatSectionNarasi(section, "Procedure Description Section",
                pilihTeks(d.catatan, "Persalinan " + d.statusLahir + " berlangsung."));
        ObjectNode descCoding = secDesc.putObject("code").putArray("coding").addObject();
        descCoding.put("system", "http://loinc.org");
        descCoding.put("code", "29554-3");
        descCoding.put("display", "Procedure Narrative");
        return comp;
    }

    private ObjectNode buatSectionNarasi(ArrayNode section, String title, String narasi) {
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        String inner = htmlNarasi(narasi);
        if (inner.equals("")) inner = "<p>-</p>";
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + inner + "</div>");
        return sec;
    }

    /**
     * Ubah narasi multi-baris jadi XHTML rapi: tiap blok (dipisah baris kosong) jadi &lt;p&gt;;
     * baris pertama blok yang punya isi lanjutan di-bold sebagai judul bagian; antar baris &lt;br/&gt;.
     * Nilai selalu di-escape. Mengembalikan "" bila kosong/"-".
     */
    private String htmlNarasi(String teks) {
        String t = (teks == null) ? "" : teks.trim();
        if (t.equals("") || t.equals("-")) return "";
        StringBuilder sb = new StringBuilder();
        for (String blok : t.split("\\n[ \\t]*\\n")) {
            java.util.List<String> isi = new java.util.ArrayList<>();
            for (String b : blok.split("\\n")) {
                String x = b.trim();
                if (!x.equals("")) isi.add(x);
            }
            if (isi.isEmpty()) continue;
            boolean adaHeader = isi.size() > 1;   // bold baris pertama hanya bila blok punya isi lanjutan
            sb.append("<p>");
            for (int i = 0; i < isi.size(); i++) {
                String line = escapeXml(isi.get(i));
                if (i == 0 && adaHeader) sb.append("<strong>").append(line).append("</strong>");
                else if (i == 0) sb.append(line);
                else sb.append("<br/>").append(line);
            }
            sb.append("</p>");
        }
        return sb.toString();
    }

    /** Seperti bersihkan() tapi PERTAHANKAN newline (untuk narasi berstruktur): strip tag HTML,
     *  collapse spasi/tab saja, normalisasi akhir baris, batasi baris kosong beruntun jadi satu. */
    private String bersihkanMultiline(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ")
                .replaceAll("\\r\\n?", "\n")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("[ \\t]*\\n[ \\t]*", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private void buatSectionEntry(ArrayNode section, String title, String loinc, String display,
            List<String> refs, String narasi) {
        if (refs == null || refs.isEmpty()) return;   // section tanpa entry dilewati
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        if (loinc != null) {
            ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
            coding.put("system", "http://loinc.org");
            coding.put("code", loinc);
            coding.put("display", display);
        }
        if (narasi != null && !narasi.trim().equals("")) {
            ObjectNode secText = sec.putObject("text");
            secText.put("status", "generated");
            secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(narasi.trim()) + "</div>");
        }
        ArrayNode entry = sec.putArray("entry");
        for (String r : refs) {
            entry.addObject().put("reference", r);
        }
    }

    // ====================== QUERY ======================

    private PersalinanData ambilData(String noRawat) {
        PersalinanData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select cp.kondisi_umum, cp.td, cp.nadi, cp.suhu, cp.rr, cp.status_lahir, cp.kelainan, cp.ketuban, "
                    + "cp.placenta, cp.tali_pusat, cp.insertio, cp.ukuran, cp.kontraksi_uterus, cp.perineum, "
                    + "cp.jahitan_dalam_1, cp.jahitan_dalam_2, cp.jahitan_luar_1, cp.jahitan_luar_2, "
                    + "cp.waktu_persalinan_kala_1, cp.waktu_persalinan_kala_2, cp.waktu_persalinan_kala_3, cp.waktu_persalinan_jumlah, "
                    + "cp.darah_keluar_kala_1, cp.darah_keluar_kala_2, cp.darah_keluar_kala_3, cp.darah_keluar_kala_4, cp.darah_keluar_jumlah, "
                    + "cp.catatan, cp.pengobatan, cp.mulai, cp.selesai, "
                    + "cp.anak, cp.apgar_score, cp.bb, cp.pb, "
                    + "rp.no_rkm_medis, p.no_ktp as ktp_ibu, p.nm_pasien as nama_ibu, "
                    + "ifnull(pdok.no_ktp,'') as ktp_dokter, ifnull(pdok.nama,'') as nama_dokter, "
                    + "ifnull(pbid.no_ktp,'') as ktp_bidan, ifnull(pbid.nama,'') as nama_bidan "
                    + "from catatan_persalinan cp "
                    + "inner join reg_periksa rp on rp.no_rawat=cp.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join pegawai pdok on pdok.nik=cp.kd_dokter "
                    + "left join pegawai pbid on pbid.nik=cp.nip "
                    + "where cp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new PersalinanData();
                d.noRawat = noRawat;
                d.noRkmMedis = nz(r.getString("no_rkm_medis"));
                d.kondisiUmum = nz(r.getString("kondisi_umum"));
                d.td = nz(r.getString("td"));
                d.nadi = nz(r.getString("nadi"));
                d.suhu = nz(r.getString("suhu"));
                d.rr = nz(r.getString("rr"));
                d.statusLahir = nz(r.getString("status_lahir"));
                d.kelainan = bersihkan(r.getString("kelainan"));
                d.ketuban = nz(r.getString("ketuban"));
                d.placenta = nz(r.getString("placenta"));
                d.taliPusat = nz(r.getString("tali_pusat"));
                d.insertio = nz(r.getString("insertio"));
                d.ukuran = nz(r.getString("ukuran"));
                d.kontraksiUterus = nz(r.getString("kontraksi_uterus"));
                d.perineum = nz(r.getString("perineum"));
                d.jahitanDalam1 = nz(r.getString("jahitan_dalam_1"));
                d.jahitanDalam2 = nz(r.getString("jahitan_dalam_2"));
                d.jahitanLuar1 = nz(r.getString("jahitan_luar_1"));
                d.jahitanLuar2 = nz(r.getString("jahitan_luar_2"));
                d.waktuKala1 = nz(r.getString("waktu_persalinan_kala_1"));
                d.waktuKala2 = nz(r.getString("waktu_persalinan_kala_2"));
                d.waktuKala3 = nz(r.getString("waktu_persalinan_kala_3"));
                d.waktuJumlah = nz(r.getString("waktu_persalinan_jumlah"));
                d.darahKala1 = nz(r.getString("darah_keluar_kala_1"));
                d.darahKala2 = nz(r.getString("darah_keluar_kala_2"));
                d.darahKala3 = nz(r.getString("darah_keluar_kala_3"));
                d.darahKala4 = nz(r.getString("darah_keluar_kala_4"));
                d.darahJumlah = nz(r.getString("darah_keluar_jumlah"));
                d.catatan = bersihkanMultiline(r.getString("catatan"));   // PERTAHANKAN newline agar bisa dirapikan jadi paragraf
                d.pengobatan = bersihkan(r.getString("pengobatan"));
                d.anak = nz(r.getString("anak"));
                d.apgarScore = nz(r.getString("apgar_score"));
                d.bbBayi = nz(r.getString("bb"));
                d.pbBayi = nz(r.getString("pb"));
                d.namaIbu = nz(r.getString("nama_ibu"));
                String selesai = nz(r.getString("selesai"));
                d.waktu = formatWaktu(selesai.equals("") ? nz(r.getString("mulai")) : selesai);
                String ktpDok = nz(r.getString("ktp_dokter"));
                String ktpBid = nz(r.getString("ktp_bidan"));
                String ktpPenolong = !ktpDok.equals("") ? ktpDok : ktpBid;
                d.namaPenolong = !nz(r.getString("nama_dokter")).equals("") ? nz(r.getString("nama_dokter"))
                        : nz(r.getString("nama_bidan"));
                d.idPenolong = nz(cek.tampilIDParktisi(ktpPenolong));
                d.idIbu = nz(cek.tampilIDPasien(nz(r.getString("ktp_ibu"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan ambilData : " + e);
        }
        return d;
    }

    /** Diagnosa persalinan (ICD-10) -> list {kode, nama}. */
    private List<String[]> ambilDiagnosa(String noRawat) {
        List<String[]> hasil = new ArrayList<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select dp.kd_penyakit, ifnull(pny.nm_penyakit,'') as nm_penyakit "
                + "from diagnosa_pasien dp left join penyakit pny on pny.kd_penyakit=dp.kd_penyakit "
                + "where dp.no_rawat=? order by dp.prioritas")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String kode = nz(r.getString("kd_penyakit")).trim();
                    if (!kode.equals("")) hasil.add(new String[]{kode, bersihkan(r.getString("nm_penyakit"))});
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan ambilDiagnosa : " + e);
        }
        return hasil;
    }

    /** Prosedur persalinan (ICD-9-CM) -> list {kode, nama}. */
    private List<String[]> ambilProsedur(String noRawat) {
        List<String[]> hasil = new ArrayList<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select pp.kode, ifnull(icd9.deskripsi_panjang,'') as nama "
                + "from prosedur_pasien pp left join icd9 on icd9.kode=pp.kode "
                + "where pp.no_rawat=? order by pp.prioritas")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String kode = nz(r.getString("kode")).trim();
                    if (!kode.equals("")) hasil.add(new String[]{kode, bersihkan(r.getString("nama"))});
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan ambilProsedur : " + e);
        }
        return hasil;
    }

    /**
     * Bayi baru lahir untuk satu persalinan, dua jalur:
     *   1. ranap_gabung -> reg_periksa bayi -> pasien + pasien_bayi. Ini relasi ibu-bayi yang
     *      dicatat petugas lewat menu Ranap Gabung di kamar inap, jadi pasti, bukan tebakan.
     *   2. cadangan: kolom bayi yang menempel di catatan_persalinan (bb/pb/apgar_score/anak),
     *      untuk persalinan yang kunjungan bayinya tidak pernah digabungkan.
     *
     * pasien_bayi.no_rkm_medis adalah RM BAYI, bukan RM ibu, dan tabel itu tidak punya kolom
     * penunjuk ibu sama sekali. Versi lama menyambungkannya ke RM ibu sehingga section bayi
     * tidak pernah berisi satu baris pun.
     */
    private List<BayiData> ambilBayi(PersalinanData d) {
        List<BayiData> hasil = ambilBayiRanapGabung(d.noRawat);
        if (hasil.isEmpty()) {
            BayiData b = bayiDariCatatanPersalinan(d);
            if (b != null) hasil.add(b);
        }
        return hasil;
    }

    /** Jalur utama: bayi yang kunjungannya digabungkan ke kunjungan ibu. APGAR total = f+w+r+t+u per menit. */
    private List<BayiData> ambilBayiRanapGabung(String noRawat) {
        List<BayiData> hasil = new ArrayList<>();
        java.util.Set<String> sudah = new java.util.HashSet<>();   // bayi yang sama bisa tersambung >1 kali
        try (PreparedStatement p = koneksi.prepareStatement(
                "select pb.no_rkm_medis, pb.anakke, pb.berat_badan, pb.panjang_badan, pb.lingkar_kepala, "
                + "pb.jam_lahir, pb.proses_lahir, ps.jk, ps.tgl_lahir, "
                + "pb.f1,pb.w1,pb.r1,pb.t1,pb.u1, pb.f5,pb.w5,pb.r5,pb.t5,pb.u5, pb.f10,pb.w10,pb.r10,pb.t10,pb.u10 "
                + "from ranap_gabung rg "
                + "inner join reg_periksa rb on rb.no_rawat=rg.no_rawat2 "
                + "inner join pasien ps on ps.no_rkm_medis=rb.no_rkm_medis "
                + "inner join pasien_bayi pb on pb.no_rkm_medis=rb.no_rkm_medis "
                + "where rg.no_rawat=? order by pb.anakke, pb.no_rkm_medis")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String rm = nz(r.getString("no_rkm_medis"));
                    if (!sudah.add(rm)) continue;
                    BayiData b = new BayiData();
                    b.rmBayi = rm;
                    b.anakke = nz(r.getString("anakke"));
                    b.beratBadan = nz(r.getString("berat_badan"));
                    b.panjangBadan = nz(r.getString("panjang_badan"));
                    b.lingkarKepala = nz(r.getString("lingkar_kepala"));
                    b.jamLahir = nz(r.getString("jam_lahir"));
                    b.prosesLahir = bersihkan(r.getString("proses_lahir"));
                    b.jenisKelamin = jenisKelamin(r.getString("jk"));
                    b.tglLahir = nz(r.getString("tgl_lahir"));
                    b.apgar1 = sumApgar(r, "1");
                    b.apgar5 = sumApgar(r, "5");
                    b.apgar10 = sumApgar(r, "10");
                    hasil.add(b);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Persalinan ambilBayiRanapGabung : " + e);
        }
        return hasil;
    }

    /**
     * Jalur cadangan dari catatan_persalinan itu sendiri. Hanya bisa memuat satu bayi (PK tabelnya
     * no_rawat), jadi kembar tetap bergantung pada jalur ranap_gabung.
     * null bila tidak ada satu pun nilai bayi yang terisi.
     */
    private BayiData bayiDariCatatanPersalinan(PersalinanData d) {
        Double[] apgar = parseApgar(d.apgarScore);
        BayiData b = new BayiData();
        b.beratBadan = d.bbBayi;
        b.panjangBadan = d.pbBayi;
        b.apgar1 = apgar[0];
        b.apgar5 = apgar[1];
        b.apgar10 = apgar[2];
        b.jenisKelamin = d.anak;
        if (parseAngka(b.beratBadan) == null && parseAngka(b.panjangBadan) == null
                && b.apgar1 == null && b.apgar5 == null && b.apgar10 == null) {
            return null;
        }
        return b;
    }

    /**
     * Total APGAR pada menit ke-n = f+w+r+t+u (masing-masing 0-2). null bila tidak dinilai.
     *
     * Kolom komponen bertipe NOT NULL, jadi baris yang APGAR-nya tidak pernah diisi tetap berisi
     * "0"/"" dan akan menghasilkan total 0. Mengirimkannya berarti melaporkan bayi tanpa tanda
     * kehidupan, padahal di data ini ada bayi hidup yang komponennya nol semua. Total 0 karena itu
     * diperlakukan sebagai "tidak dinilai" -- konsekuensinya APGAR 0 yang memang benar (bayi lahir
     * mati) tidak ikut terkirim, dan itu jauh lebih aman daripada kebalikannya.
     */
    private Double sumApgar(ResultSet r, String menit) {
        try {
            String[] kolom = {"f" + menit, "w" + menit, "r" + menit, "t" + menit, "u" + menit};
            double total = 0;
            boolean ada = false;
            for (String k : kolom) {
                Double v = parseAngka(r.getString(k));
                if (v != null) { total += v; ada = true; }
            }
            return (ada && total > 0) ? total : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ====================== UTIL ======================

    private String jahitanNarasi(PersalinanData d) {
        String dalam = gabungSlash(d.jahitanDalam1, d.jahitanDalam2);
        String luar = gabungSlash(d.jahitanLuar1, d.jahitanLuar2);
        String s = "";
        if (!dalam.equals("")) s = "Jahitan dalam: " + dalam;
        if (!luar.equals("")) s = (s.equals("") ? "" : s + "; ") + "Jahitan luar: " + luar;
        return s;
    }

    private String gabungSlash(String a, String b) {
        a = nz(a).trim(); b = nz(b).trim();
        if (a.equals("") && b.equals("")) return "";
        if (a.equals("")) return b;
        if (b.equals("")) return a;
        return a + "/" + b;
    }

    private String formatWaktu(String dt) {
        if (dt == null || dt.trim().equals("")) return "";
        String t = dt.trim();
        if (t.startsWith("0000-00-00")) return "";
        if (t.contains(" ")) t = t.replace(" ", "T");
        if (t.length() == 10) t = t + "T00:00:00";
        return t + "+07:00";
    }

    /**
     * catatan_persalinan.apgar_score ditulis bebas oleh petugas: "8/9/10", "8.9.10", kadang hanya
     * dua nilai. Angka diambil berurutan sebagai menit 1, 5, lalu 10; nilai di luar 0-10 dianggap
     * salah ketik dan dibuang tanpa menggeser posisi menit berikutnya.
     */
    private Double[] parseApgar(String s) {
        Double[] hasil = new Double[]{null, null, null};
        if (s == null || s.trim().equals("")) return hasil;
        int i = 0;
        for (String bagian : s.trim().split("[^0-9]+")) {
            if (bagian.equals("")) continue;
            if (i >= hasil.length) break;
            try {
                double v = Double.parseDouble(bagian);
                if (v >= 0 && v <= 10) hasil[i] = v;
            } catch (Exception e) {
                // token bukan angka yang wajar, biarkan menit ini kosong
            }
            i++;
        }
        return hasil;
    }

    /** pasien.jk ('L'/'P') -> label yang bisa dibaca; "" bila tak dikenali. */
    private String jenisKelamin(String jk) {
        String v = nz(jk).trim().toUpperCase();
        if (v.startsWith("L")) return "Laki-laki";
        if (v.startsWith("P")) return "Perempuan";
        return "";
    }

    /** "2024-10-24" + "12:45:00" -> "2024-10-24 12:45:00" untuk diteruskan ke formatWaktu(). */
    private String gabungTanggalJam(String tanggal, String jam) {
        String t = nz(tanggal).trim();
        if (t.equals("") || t.startsWith("0000-00-00")) return "";
        if (t.length() > 10) t = t.substring(0, 10);
        String j = nz(jam).trim();
        if (j.equals("") || j.equals("00:00:00")) return t;
        return t + " " + j;
    }

    /** Tampilan waktu lahir untuk narasi: "24-10-2024 12:45"; "" bila tanggalnya tidak ada. */
    private String tampilTanggalJam(String tanggal, String jam) {
        String t = nz(tanggal).trim();
        if (t.equals("") || t.startsWith("0000-00-00") || t.length() < 10) return "";
        String tampil = t.substring(8, 10) + "-" + t.substring(5, 7) + "-" + t.substring(0, 4);
        String j = nz(jam).trim();
        if (j.length() >= 5 && !j.startsWith("00:00")) tampil = tampil + " " + j.substring(0, 5);
        return tampil;
    }

    private Double parseAngka(String s) {
        if (s == null) return null;
        String t = s.replace(",", ".").replaceAll("[^0-9.\\-]", "").trim();
        if (t.equals("") || t.equals(".") || t.equals("-")) return null;
        try {
            return Double.valueOf(t);
        } catch (Exception e) {
            return null;
        }
    }

    private String gabung(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String v = nz(p).trim();
            if (!v.equals("") && !v.equals("-") && !v.endsWith(": ") && !v.endsWith(":")) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(v);
            }
        }
        return sb.toString();
    }

    private String gabungLabel(String l1, String v1, String l2, String v2) {
        StringBuilder sb = new StringBuilder();
        if (!nz(v1).trim().equals("") && !nz(v1).trim().equals("-")) {
            sb.append(l1).append(": ").append(v1.trim());
        }
        if (!nz(v2).trim().equals("") && !nz(v2).trim().equals("-")) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(l2).append(": ").append(v2.trim());
        }
        return sb.toString();
    }

    private String pilihTeks(String... vals) {
        for (String v : vals) {
            if (v != null && !v.trim().equals("") && !v.trim().equals("-")) return v.trim();
        }
        return "";
    }

    private String normal(String s) {
        if (s == null) return "";
        return s.replaceAll("[^A-Za-z0-9]", "");
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
