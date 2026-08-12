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
 * Pengiriman "Laporan Operasi" (FHIR Composition - LOINC 11504-8, Surgical operation note) ke SATUSEHAT.
 *
 * Sumber data: laporan_operasi (uraian/diagnosa/jaringan) + operasi (anestesi/operator).
 * Composition naratif (section.text.div) + event (prosedur + periode). Idempotent via business
 * identifier + ifNoneExist (kirim ulang tidak menduplikasi).
 */
public class SatuSehatLaporanOperasi {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatLaporanOperasi() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Laporan Operasi : " + e);
        }
    }

    private static class OperasiData {
        String noRawat="", waktuMulai="", waktuSelesai="";
        String namaOperasi="", diagnosaPreop="", diagnosaPostop="", laporan="";
        String jaringanEksisi="", jaringanEksekusi="", permintaanPa="", jenisAnastesi="";
        String idPasien="", namaPasien="", idOperator="", namaOperator="", idOrg="";
        /** Tindakan ICD-9-CM kunjungan ini: {kode, deskripsi}. Mengisi Composition.event.code.coding. */
        List<String[]> prosedur = new ArrayList<>();
        /** Tim operasi & anestesi: {peran, nama}. Mengisi Surgical Team Section. */
        List<String[]> tim = new ArrayList<>();
    }

    /** Kolom tabel operasi -> peran pada Surgical Team Section, berurutan sesuai urutan tampil. */
    private static final String[][] PERAN_TIM = {
        {"operator1",           "Dokter Bedah Utama"},
        {"operator2",           "Dokter Bedah Tambahan"},
        {"operator3",           "Dokter Bedah Tambahan"},
        {"asisten_operator1",   "Asisten Bedah 1"},
        {"asisten_operator2",   "Asisten Bedah 2"},
        {"asisten_operator3",   "Asisten Bedah 3"},
        {"dokter_anestesi",     "Dokter Anestesi Utama"},
        {"asisten_anestesi",    "Asisten Anestesi"},
        {"asisten_anestesi2",   "Asisten Anestesi Tambahan"},
        {"instrumen",           "Petugas Instrumen (Scrub Nurse)"},
        {"omloop",              "Petugas Sirkuler (Circulating Nurse)"},
        {"omloop2",             "Petugas Sirkuler (Circulating Nurse)"},
        {"omloop3",             "Petugas Sirkuler (Circulating Nurse)"},
        {"omloop4",             "Petugas Sirkuler (Circulating Nurse)"},
        {"omloop5",             "Petugas Sirkuler (Circulating Nurse)"},
        {"dokter_anak",         "Dokter Anak"},
        {"dokter_pjanak",       "Dokter Penanggung Jawab Anak"},
        {"dokter_umum",         "Dokter Umum"},
        {"perawaat_resusitas",  "Perawat Resusitasi"},
        {"bidan",               "Bidan"},
        {"bidan2",              "Bidan"},
        {"bidan3",              "Bidan"},
        {"perawat_luar",        "Perawat Luar"},
    };

    /**
     * Bangun & kirim Laporan Operasi untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter SATUSEHAT (WAJIB — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Laporan Operasi (POST form) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        OperasiData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef, d.waktuMulai);
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", "urn:uuid:" + UUID.randomUUID().toString());
        entry.set("resource", comp);
        ObjectNode request = entry.putObject("request");
        request.put("method", "POST");
        request.put("url", "Composition");
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        OperasiData d = ambilData(noRawat);
        if (d == null) {
            return;   // tidak ada laporan_operasi
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Laporan Operasi : ID pasien belum ada untuk no_rawat " + noRawat
                    + ". Dilewati.");
            return;
        }

        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu = d.waktuMulai;

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef, waktu);
        // SATUSEHAT tidak menghormati ifNoneExist & tidak bisa cari Composition by identifier ->
        // cari Composition existing lewat encounter lalu cocokkan identifier OPERASI-, lalu PUT.
        String idLama = cariIdCompositionServer(idEncounter, "OPERASI-" + noRawat);
        boolean adaId = !idLama.equals("");
        String fullUrl = adaId ? ("Composition/" + idLama) : ("urn:uuid:" + UUID.randomUUID().toString());
        if (adaId) comp.put("id", idLama);
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", fullUrl);
        entry.set("resource", comp);
        ObjectNode request = entry.putObject("request");
        if (adaId) {
            request.put("method", "PUT");
            request.put("url", "Composition/" + idLama);
        } else {
            request.put("method", "POST");
            request.put("url", "Composition");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Laporan Operasi : " + link);
        System.out.println("Request JSON Operasi : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (°, ±, en/em-dash) di narasi laporan operasi rusak jadi "?"/"ï¿½" di server.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Operasi Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Operasi OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Operasi Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Operasi : " + hasil);
    }

    /** Composition Laporan Operasi (11504-8): header + event + section naratif. */
    private ObjectNode buatComposition(String noRawat, OperasiData d, String patientRef, String encounterRef,
            String waktu) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "OPERASI-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "11504-8");
        typeCoding.put("display", "Surgical operation note");
        comp.put("title", "Laporan Operasi" + (d.namaOperasi.equals("") ? "" : " " + d.namaOperasi));
        if (!waktu.equals("")) comp.put("date", waktu);
        comp.putObject("subject").put("reference", patientRef);
        comp.putObject("encounter").put("reference", encounterRef);
        if (!d.idOperator.equals("")) {
            ObjectNode author = comp.putArray("author").addObject();
            author.put("reference", "Practitioner/" + d.idOperator);
            author.put("display", d.namaOperator);
            ObjectNode attester = comp.putArray("attester").addObject();
            attester.put("mode", "legal");
            if (!waktu.equals("")) attester.put("time", waktu);
            attester.putObject("party").put("reference", "Practitioner/" + d.idOperator);
        }
        comp.putObject("custodian").put("reference", "Organization/" + d.idOrg);
        // event: prosedur operasi + periode.
        if (!d.namaOperasi.equals("") || !waktu.equals("") || !d.prosedur.isEmpty()) {
            ObjectNode event = comp.putArray("event").addObject();
            // Tindakan ber-ICD-9-CM lebih diutamakan daripada teks nama paket operasi; teks dipakai
            // hanya bila kunjungan ini memang tidak punya kode prosedur.
            List<String[]> tindakan = prosedurBedah(d.prosedur);
            if (!tindakan.isEmpty()) {
                ArrayNode codeArr = event.putArray("code");
                for (String[] pr : tindakan) {
                    ObjectNode cc = codeArr.addObject();
                    ObjectNode coding = cc.putArray("coding").addObject();
                    coding.put("system", "http://hl7.org/fhir/sid/icd-9-cm");
                    coding.put("code", pr[0]);
                    coding.put("display", pr[1].equals("") ? pr[0] : pr[1]);
                    cc.put("text", pr[1].equals("") ? pr[0] : pr[1]);
                }
            } else if (!d.namaOperasi.equals("")) {
                event.putArray("code").addObject().put("text", d.namaOperasi);
            }
            ObjectNode period = event.putObject("period");
            if (!waktu.equals("")) period.put("start", waktu);
            // end hanya bila >= start (offset sama, ISO comparable). Hindari "start > end" (selesaioperasi
            // kadang cuma tanggal/midnight sehingga lebih kecil dari start).
            if (!d.waktuSelesai.equals("") && (waktu.equals("") || d.waktuSelesai.compareTo(waktu) >= 0)) {
                period.put("end", d.waktuSelesai);
            }
        }

        // Narrative (text.div) WAJIB agar viewer tidak crash.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Laporan Operasi - "
                + escapeXml(d.namaOperasi.equals("") ? d.namaPasien : d.namaOperasi) + "</div>");

        ArrayNode section = comp.putArray("section");
        tambahSectionTim(section, d.tim);
        tambahSection(section, "Anesthesia Section", "59774-0", "Anesthesia", d.jenisAnastesi);
        tambahSection(section, "Preoperative Diagnosis Section", "10219-4",
                "Surgical operation note preoperative diagnosis Narrative", d.diagnosaPreop);
        tambahSection(section, "Operative Note Surgical Procedure Section", "10223-6",
                "Surgical operation note surgical procedure Narrative", d.namaOperasi);
        tambahSection(section, "Procedure Description Section", "29554-3", "Procedure Narrative", d.laporan);
        tambahSection(section, "Procedure Specimens Taken Section", "59773-2",
                "Procedure specimens taken Narrative",
                gabung(d.jaringanEksisi, d.jaringanEksekusi, d.permintaanPa));
        tambahSection(section, "Postoperative Diagnosis Section", "10218-6",
                "Surgical operation note postoperative diagnosis Narrative", d.diagnosaPostop);
        return comp;
    }

    /**
     * Surgical Team Section: tabel peran vs nama tenaga medis, sesuai contoh resmi Modul Operasi.
     * Dilewati bila tidak ada satu pun anggota tim yang terisi.
     */
    private void tambahSectionTim(ArrayNode section, List<String[]> tim) {
        if (tim == null || tim.isEmpty()) return;
        StringBuilder tabel = new StringBuilder(
                "<table><tr><td><b>Peran</b></td><td><b>Nama Tenaga Medis</b></td></tr>");
        for (String[] anggota : tim) {
            tabel.append("<tr><td>").append(escapeXml(anggota[0])).append("</td><td>")
                 .append(escapeXml(anggota[1])).append("</td></tr>");
        }
        tabel.append("</table>");

        ObjectNode sec = section.addObject();
        sec.put("title", "Surgical Team Section");
        ObjectNode code = sec.putObject("code");
        ObjectNode coding = code.putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", "10223-6");
        coding.put("display", "Surgical operation note surgical procedure Narrative");
        code.put("text", "Tim Operasi dan Anestesi");
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + tabel + "</div>");
    }

    /** Tambah section naratif (text.div) bila isi tidak kosong. */
    private void tambahSection(ArrayNode section, String title, String loinc, String display, String narasi) {
        if (narasi == null || narasi.trim().equals("") || narasi.trim().equals("-")) return;
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(narasi.trim()) + "</div>");
    }

    private OperasiData ambilData(String noRawat) {
        OperasiData d = null;
        try {
            // Skema laporan_operasi berbeda-beda antar versi Khanza -> bangun SELECT adaptif (kolom yang
            // tidak ada diisi '' / null) agar tidak error "Unknown column".
            java.util.Set<String> c = kolomTabel("laporan_operasi");
            String fTanggal = c.contains("tanggal") ? "lo.tanggal" : "null";
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(po.nm_perawatan, "
                    + "  ifnull((select group_concat(ifnull(i.deskripsi_panjang, pp.kode) separator ', ') "
                    + "  from prosedur_pasien pp left join icd9 i on i.kode=pp.kode where pp.no_rawat=lo.no_rawat),'')) as nama_operasi, "
                    + sel(c, "diagnosa_preop") + " as diagnosa_preop, "
                    + sel(c, "diagnosa_postop") + " as diagnosa_postop, "
                    + sel(c, "laporan_operasi") + " as laporan, "
                    + sel(c, "jaringan_dieksisi") + " as jaringan_dieksisi, "
                    + sel(c, "jaringan_dieksekusi") + " as jaringan_dieksekusi, "
                    + sel(c, "permintaan_pa") + " as permintaan_pa, "
                    + fTanggal + " as tanggal, "
                    + sel(c, "selesaioperasi") + " as selesaioperasi, "
                    + "ifnull(o.jenis_anasthesi,'') as jenis_anasthesi, ifnull(o.operator1,'') as operator1, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nama_operator, ifnull(pgop.no_ktp,'') as ktp_operator "
                    + "from laporan_operasi lo "
                    + "inner join reg_periksa rp on rp.no_rawat=lo.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join operasi o on o.no_rawat=lo.no_rawat "
                    + "left join paket_operasi po on po.kode_paket=o.kode_paket "
                    + "left join dokter dr on dr.kd_dokter=o.operator1 "
                    + "left join pegawai pgop on pgop.nik=o.operator1 "
                    + "where lo.no_rawat=? " + (c.contains("tanggal") ? "order by lo.tanggal desc " : "") + "limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new OperasiData();
                d.noRawat = noRawat;
                d.namaOperasi = bersihkan(r.getString("nama_operasi"));
                d.diagnosaPreop = bersihkan(r.getString("diagnosa_preop"));
                d.diagnosaPostop = bersihkan(r.getString("diagnosa_postop"));
                d.laporan = bersihkan(r.getString("laporan"));
                d.jaringanEksisi = bersihkan(r.getString("jaringan_dieksisi"));
                d.jaringanEksekusi = bersihkan(r.getString("jaringan_dieksekusi"));
                d.permintaanPa = bersihkan(r.getString("permintaan_pa"));
                d.jenisAnastesi = bersihkan(r.getString("jenis_anasthesi"));
                d.waktuMulai = formatWaktu(nz(r.getString("tanggal")));
                d.waktuSelesai = formatWaktu(nz(r.getString("selesaioperasi")));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaOperator = nz(r.getString("nama_operator"));
                d.idOperator = nz(cek.tampilIDParktisi(nz(r.getString("ktp_operator"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
                d.prosedur = ambilProsedur(noRawat);
                d.tim = ambilTim(noRawat);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Operasi ambilData : " + e);
        }
        return d;
    }

    /**
     * Saring tindakan bedah dari seluruh prosedur kunjungan. `prosedur_pasien` memuat SEMUA tindakan
     * kunjungan, termasuk EKG (89.52) dan foto toraks (87.44) yang bukan isi laporan operasi.
     * ICD-9-CM bab 01-86 adalah tindakan operatif, 87-99 diagnostik/terapeutik non-bedah.
     * Bila penyaringan menyisakan nol (kodenya tidak lazim), seluruh daftar dipakai apa adanya
     * daripada mengirim event tanpa tindakan sama sekali.
     */
    private List<String[]> prosedurBedah(List<String[]> semua) {
        List<String[]> bedah = new ArrayList<>();
        for (String[] pr : semua) {
            String kode = pr[0].trim();
            int titik = kode.indexOf('.');
            String bab = titik > 0 ? kode.substring(0, titik) : kode;
            try {
                if (Integer.parseInt(bab.trim()) <= 86) bedah.add(pr);
            } catch (NumberFormatException e) {
                bedah.add(pr);   // kode tak berbentuk angka: jangan dibuang diam-diam
            }
        }
        return bedah.isEmpty() ? semua : bedah;
    }

    /** Tindakan ICD-9-CM kunjungan ini -> {kode, deskripsi}, urut prioritas. */
    private List<String[]> ambilProsedur(String noRawat) {
        List<String[]> hasil = new ArrayList<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select pp.kode, ifnull(i.deskripsi_panjang,'') as nama "
                + "from prosedur_pasien pp left join icd9 i on i.kode=pp.kode "
                + "where pp.no_rawat=? order by pp.prioritas")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String kode = nz(r.getString("kode")).trim();
                    if (!kode.equals("")) hasil.add(new String[]{kode, bersihkan(r.getString("nama"))});
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Operasi ambilProsedur : " + e);
        }
        return hasil;
    }

    /**
     * Tim operasi & anestesi dari tabel operasi -> {peran, nama}, hanya yang terisi.
     * Satu orang yang mengisi dua peran (mis. operator1 = operator2) hanya ditulis sekali,
     * pada peran yang lebih dulu muncul di PERAN_TIM.
     */
    private List<String[]> ambilTim(String noRawat) {
        List<String[]> hasil = new ArrayList<>();
        java.util.Set<String> kolomAda = kolomTabel("operasi");
        StringBuilder pilih = new StringBuilder();
        List<String> kolomDipakai = new ArrayList<>();
        for (String[] peran : PERAN_TIM) {
            if (!kolomAda.contains(peran[0])) continue;   // skema operasi berbeda antar versi Khanza
            if (pilih.length() > 0) pilih.append(", ");
            pilih.append("ifnull(o.").append(peran[0]).append(",'') as ").append(peran[0]);
            kolomDipakai.add(peran[0]);
        }
        if (kolomDipakai.isEmpty()) return hasil;

        java.util.Set<String> sudah = new java.util.HashSet<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select " + pilih + " from operasi o where o.no_rawat=? order by o.tgl_operasi desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    for (String[] peran : PERAN_TIM) {
                        if (!kolomDipakai.contains(peran[0])) continue;
                        String kode = nz(r.getString(peran[0])).trim();
                        if (kode.equals("") || kode.equals("-") || !sudah.add(kode)) continue;
                        String nama = namaPetugas(kode);
                        if (!nama.equals("")) hasil.add(new String[]{peran[1], nama});
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Operasi ambilTim : " + e);
        }
        return hasil;
    }

    /**
     * Nama tenaga medis dari kode di tabel operasi. Kolom itu berisi nik pegawai, dan untuk dokter
     * nik = kd_dokter, jadi tabel dokter dicoba lebih dulu karena namanya sudah bergelar.
     */
    private String namaPetugas(String kode) {
        String nama = cariSatu("select nm_dokter from dokter where kd_dokter=? limit 1", kode);
        if (nama.equals("")) nama = cariSatu("select nama from pegawai where nik=? limit 1", kode);
        return bersihkan(nama);
    }

    private String cariSatu(String sql, String param) {
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, param);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return nz(r.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Operasi cariSatu : " + e);
        }
        return "";
    }

    /**
     * Cari id Composition existing lewat search by encounter, lalu cocokkan identifier (mis. OPERASI-{noRawat})
     * atau type 11504-8. "" bila tidak ada / gagal. Composition tidak bisa dicari by identifier di SATUSEHAT.
     */
    private String cariIdCompositionServer(String idEncounter, String idenValue) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            java.net.URI uri = java.net.URI.create(link + "/Composition?encounter=" + idEncounter + "&_count=100");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            JsonNode es = r.path("entry");
            if (es.isArray()) {
                for (JsonNode e : es) {
                    JsonNode res = e.path("resource");
                    // Composition.identifier = object tunggal.
                    if (idenValue.equals(nz(res.path("identifier").path("value").asText()))) {
                        return nz(res.path("id").asText());
                    }
                    // fallback: cocok type Operative Note 11504-8.
                    for (JsonNode cd : res.path("type").path("coding")) {
                        if ("11504-8".equals(cd.path("code").asText())) {
                            return nz(res.path("id").asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Operasi cariIdCompositionServer : " + e);
        }
        return "";
    }

    /** Set nama kolom (lowercase) sebuah tabel di database aktif. */
    private java.util.Set<String> kolomTabel(String tabel) {
        java.util.Set<String> set = new java.util.HashSet<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select column_name from information_schema.columns where table_schema=database() and table_name=?")) {
            p.setString(1, tabel);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) set.add(nz(r.getString(1)).toLowerCase());
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Operasi kolomTabel : " + e);
        }
        return set;
    }

    /** Ekspresi SELECT untuk kolom lo.<k>: "ifnull(lo.k,'')" bila ada, "''" bila tidak ada. */
    private String sel(java.util.Set<String> cols, String k) {
        return cols.contains(k) ? ("ifnull(lo." + k + ",'')") : "''";
    }

    // ====================== UTIL ======================

    private String formatWaktu(String dt) {
        if (dt == null || dt.trim().equals("")) return "";
        String t = dt.trim();
        if (t.startsWith("0000-00-00")) return "";
        if (t.contains(".")) t = t.substring(0, t.indexOf('.'));   // buang pecahan detik (mis. "23.0")
        if (t.contains(" ")) t = t.replace(" ", "T");
        if (t.length() == 10) t = t + "T00:00:00";
        return t + "+07:00";
    }

    private String gabung(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String v = nz(p).trim();
            if (!v.equals("") && !v.equals("-")) {
                if (sb.length() > 0) sb.append(". ");
                sb.append(v);
            }
        }
        return sb.toString();
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
