/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
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
 * Pengiriman "Laporan USG" (FHIR Composition - LOINC 28570-0, Procedure note) ke SATUSEHAT.
 *
 * Sumber data: hasil_pemeriksaan_usg (USG obstetri: biometri janin, plasenta, ketuban, kesimpulan).
 * Composition murni naratif (section.text.div, XHTML rapi) mengikuti pola SatuSehatLaporanTindakanEcho/Eswl.
 *
 * Idempotensi: id server Composition DISIMPAN LOKAL (satu_sehat_laporan_usg) lalu PUT — karena SATUSEHAT
 * membuang identifier custom Composition & mengabaikan ifNoneExist. Pemulihan bila lokal kosong: cocokkan
 * by TITLE (Echo/Eswl/USG sama-sama type 28570-0, jadi title-lah pembeda agar tak saling menimpa).
 */
public class SatuSehatLaporanUsg {

    /** SNOMED CT untuk event Composition: pemeriksaan USG. 16310003 = "Diagnostic ultrasonography".
     *  Bila DITOLAK subset SNOMED SATUSEHAT (spt kode prosedur ESWL dulu), ganti konstanta ini dgn kode
     *  yang diterima server; verifikasi saat uji staging. */
    private static final String SNOMED_USG_CODE = "16310003";
    private static final String SNOMED_USG_DISPLAY = "Diagnostic ultrasonography";

    /** Judul Composition — pembeda idempotensi dari laporan lain bertype 28570-0 (Echo/ESWL). */
    private static final String TITLE = "Laporan USG";

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatLaporanUsg() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Laporan USG : " + e);
        }
    }

    private static class UsgData {
        String noRawat="", waktu="";
        String diagnosaKlinis="", kirimanDari="", hta="";
        String kantongGestasi="", crl="", jenisPresentasi="", bpd="", femur="", abdomen="", tbj="", usiaKehamilan="";
        String plasenta="", derajatMaturitas="", airKetuban="", afi="", kelainanKongenital="", jenisKelamin="", kesimpulan="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /**
     * Bangun & kirim Laporan USG untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter SATUSEHAT (WAJIB — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Laporan USG (POST form) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        UsgData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef);
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
        UsgData d = ambilData(noRawat);
        if (d == null) {
            return;   // tidak ada baris hasil_pemeriksaan_usg
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Laporan USG : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef);
        // Ada id lokal -> PUT (update in-place, tak memicu "Found duplicate"); else pulihkan by title; else POST.
        String idLama = ambilIdLokal(noRawat);
        if (idLama.equals("")) {
            idLama = cariIdCompositionServer(idEncounter, TITLE);
        }
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
        System.out.println("URL Laporan USG : " + link);
        System.out.println("Request JSON USG : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (°, ±, dash) rusak jadi "?"/"ï¿½" di server. Bytes UTF-8 = server baca benar.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error USG Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error USG OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error USG Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON USG : " + hasil);
        // Catat id server Composition ke tabel lokal agar kiriman berikutnya PUT (bukan POST duplikat).
        String idBaru = adaId ? idLama : extractIdComposition(hasil);
        if (!idBaru.equals("")) simpanIdLokal(noRawat, idBaru);
    }

    /** Ambil id Composition tersimpan dari satu_sehat_laporan_usg (untuk PUT). "" bila belum ada. */
    private String ambilIdLokal(String noRawat) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(id_composition,'') as id_composition from satu_sehat_laporan_usg where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            String id = "";
            if (r.next()) id = nz(r.getString("id_composition"));
            r.close();
            p.close();
            return id;
        } catch (Exception e) {
            System.out.println("Notifikasi USG ambilIdLokal : " + e);
            return "";
        }
    }

    /** Upsert id Composition ke satu_sehat_laporan_usg (idempotent per no_rawat). */
    private void simpanIdLokal(String noRawat, String idComposition) {
        if (idComposition == null || idComposition.equals("")) return;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "replace into satu_sehat_laporan_usg (no_rawat, id_composition) values (?,?)");
            p.setString(1, noRawat);
            p.setString(2, idComposition);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi USG simpanIdLokal : " + e);
        }
    }

    /** Ekstrak id Composition dari transaction-response (entry[0].response.resourceID / location). */
    private String extractIdComposition(String responseBody) {
        try {
            JsonNode r = mapper.readTree(responseBody);
            JsonNode resp = r.path("entry").path(0).path("response");
            String id = nz(resp.path("resourceID").asText());
            if (!id.equals("")) return id;
            String loc = nz(resp.path("location").asText());   // .../Composition/{id}/_history/{ver}
            if (!loc.equals("")) {
                String[] seg = loc.split("/");
                for (int i = 0; i < seg.length - 1; i++) {
                    if (seg[i].equals("Composition")) return seg[i + 1];
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi USG extractIdComposition : " + e);
        }
        return "";
    }

    /** Composition Laporan USG (Procedure note 28570-0): header + event + section naratif. */
    private ObjectNode buatComposition(String noRawat, UsgData d, String patientRef, String encounterRef) {
        String waktu = d.waktu;
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "USG-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "28570-0");
        typeCoding.put("display", "Procedure note");
        comp.put("title", TITLE);
        if (!waktu.equals("")) comp.put("date", waktu);
        comp.putObject("subject").put("reference", patientRef);
        comp.putObject("encounter").put("reference", encounterRef);
        if (!d.idDokter.equals("")) {
            ObjectNode author = comp.putArray("author").addObject();
            author.put("reference", "Practitioner/" + d.idDokter);
            author.put("display", d.namaDokter);
            ObjectNode attester = comp.putArray("attester").addObject();
            attester.put("mode", "legal");
            if (!waktu.equals("")) attester.put("time", waktu);
            attester.putObject("party").put("reference", "Practitioner/" + d.idDokter);
        }
        comp.putObject("custodian").put("reference", "Organization/" + d.idOrg);
        // event: pemeriksaan USG (SNOMED) + waktu.
        ObjectNode event = comp.putArray("event").addObject();
        ObjectNode evCoding = event.putArray("code").addObject().putArray("coding").addObject();
        evCoding.put("system", "http://snomed.info/sct");
        evCoding.put("code", SNOMED_USG_CODE);
        evCoding.put("display", SNOMED_USG_DISPLAY);
        if (!waktu.equals("")) {
            event.putObject("period").put("start", waktu);
        }

        // Narrative (text.div) WAJIB.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Laporan USG - "
                + escapeXml(d.namaPasien) + "</div>");

        // Sections (naratif XHTML rapi).
        ArrayNode section = comp.putArray("section");
        // Hasil pemeriksaan USG (29554-3).
        tambahSectionHtml(section, "Procedure Description Section", "29554-3", "Procedure Narrative",
                htmlHasilUsg(d));
        // Kesimpulan (59769-0).
        tambahSection(section, "Postprocedure Diagnosis Section", "59769-0",
                "Postprocedure diagnosis Narrative", d.kesimpulan);
        return comp;
    }

    /** Rakit XHTML terstruktur hasil USG obstetri: paragraf per aspek, biometri/plasenta/ketuban dikelompokkan. */
    private String htmlHasilUsg(UsgData d) {
        StringBuilder sb = new StringBuilder();
        tambahParagraf(sb, "Diagnosa Klinis", d.diagnosaKlinis);
        tambahParagraf(sb, "Kiriman Dari", d.kirimanDari);
        tambahParagraf(sb, "Riwayat/HTA", d.hta);
        tambahGrup(sb, "Biometri Janin",
                new String[]{"Kantong Gestasi", "Ukuran Bokong-Kepala (CRL)", "Diameter Biparietal (BPD)",
                        "Panjang Femur (FL)", "Lingkar Abdomen (AC)", "Tafsiran Berat Janin (TBJ)", "Usia Kehamilan"},
                new String[]{d.kantongGestasi, d.crl, d.bpd, d.femur, d.abdomen, d.tbj, d.usiaKehamilan});
        tambahParagraf(sb, "Presentasi Janin", d.jenisPresentasi);
        tambahGrup(sb, "Plasenta",
                new String[]{"Implantasi", "Derajat Maturitas"},
                new String[]{d.plasenta, d.derajatMaturitas});
        tambahGrup(sb, "Cairan Ketuban",
                new String[]{"Jumlah", "Indeks Cairan Ketuban (AFI)"},
                new String[]{d.airKetuban, d.afi});
        tambahParagraf(sb, "Kelainan Kongenital", d.kelainanKongenital);
        tambahParagraf(sb, "Perkiraan Jenis Kelamin", d.jenisKelamin);
        return sb.toString();
    }

    /** Tambah "<p><strong>label:</strong> nilai</p>" bila nilai terisi (nilai di-escape). */
    private void tambahParagraf(StringBuilder sb, String label, String nilai) {
        String v = nz(nilai).trim();
        if (v.equals("") || v.equals("-")) return;
        sb.append("<p><strong>").append(escapeXml(label)).append(":</strong> ").append(escapeXml(v)).append("</p>");
    }

    /** Tambah paragraf grup: "<p><strong>header:</strong><br/>label: nilai<br/>...</p>" utk nilai yg terisi. */
    private void tambahGrup(StringBuilder sb, String header, String[] label, String[] nilai) {
        StringBuilder isi = new StringBuilder();
        for (int i = 0; i < label.length && i < nilai.length; i++) {
            String v = nz(nilai[i]).trim();
            if (v.equals("") || v.equals("-")) continue;
            if (isi.length() > 0) isi.append("<br/>");
            isi.append(escapeXml(label[i])).append(": ").append(escapeXml(v));
        }
        if (isi.length() == 0) return;
        sb.append("<p><strong>").append(escapeXml(header)).append(":</strong><br/>").append(isi).append("</p>");
    }

    /** Tambah section naratif (text.div, isi di-escape) bila isi tidak kosong. */
    private void tambahSection(ArrayNode section, String title, String loinc, String display, String narasi) {
        if (narasi == null || narasi.trim().equals("") || narasi.trim().equals("-")) return;
        ObjectNode sec = buatKerangkaSection(section, title, loinc, display);
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(narasi.trim()) + "</div>");
    }

    /** Tambah section dengan konten XHTML sudah jadi (dalamnya sudah di-escape oleh pemanggil). */
    private void tambahSectionHtml(ArrayNode section, String title, String loinc, String display, String innerXhtml) {
        if (innerXhtml == null || innerXhtml.trim().equals("")) return;
        ObjectNode sec = buatKerangkaSection(section, title, loinc, display);
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + innerXhtml + "</div>");
    }

    private ObjectNode buatKerangkaSection(ArrayNode section, String title, String loinc, String display) {
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        return sec;
    }

    private UsgData ambilData(String noRawat) {
        UsgData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select hu.tanggal, "
                    + "ifnull(hu.diagnosa_klinis,'') as diagnosa_klinis, ifnull(hu.kiriman_dari,'') as kiriman_dari, "
                    + "ifnull(hu.hta,'') as hta, ifnull(hu.kantong_gestasi,'') as kantong_gestasi, "
                    + "ifnull(hu.ukuran_bokongkepala,'') as ukuran_bokongkepala, ifnull(hu.jenis_prestasi,'') as jenis_prestasi, "
                    + "ifnull(hu.diameter_biparietal,'') as diameter_biparietal, ifnull(hu.panjang_femur,'') as panjang_femur, "
                    + "ifnull(hu.lingkar_abdomen,'') as lingkar_abdomen, ifnull(hu.tafsiran_berat_janin,'') as tafsiran_berat_janin, "
                    + "ifnull(hu.usia_kehamilan,'') as usia_kehamilan, ifnull(hu.plasenta_berimplatansi,'') as plasenta_berimplatansi, "
                    + "ifnull(hu.derajat_maturitas,'') as derajat_maturitas, ifnull(hu.jumlah_air_ketuban,'') as jumlah_air_ketuban, "
                    + "ifnull(hu.indek_cairan_ketuban,'') as indek_cairan_ketuban, ifnull(hu.kelainan_kongenital,'') as kelainan_kongenital, "
                    + "ifnull(hu.peluang_sex,'') as peluang_sex, ifnull(hu.kesimpulan,'') as kesimpulan, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nama_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from hasil_pemeriksaan_usg hu "
                    + "inner join reg_periksa rp on rp.no_rawat=hu.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=hu.kd_dokter "
                    + "left join pegawai pg on pg.nik=hu.kd_dokter "
                    + "where hu.no_rawat=? order by hu.tanggal desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new UsgData();
                d.noRawat = noRawat;
                d.waktu = formatWaktu(nz(r.getString("tanggal")));
                d.diagnosaKlinis = bersihkan(r.getString("diagnosa_klinis"));
                d.kirimanDari = bersihkan(r.getString("kiriman_dari"));
                d.hta = bersihkan(r.getString("hta"));
                d.kantongGestasi = bersihkan(r.getString("kantong_gestasi"));
                d.crl = bersihkan(r.getString("ukuran_bokongkepala"));
                d.jenisPresentasi = bersihkan(r.getString("jenis_prestasi"));
                d.bpd = bersihkan(r.getString("diameter_biparietal"));
                d.femur = bersihkan(r.getString("panjang_femur"));
                d.abdomen = bersihkan(r.getString("lingkar_abdomen"));
                d.tbj = bersihkan(r.getString("tafsiran_berat_janin"));
                d.usiaKehamilan = bersihkan(r.getString("usia_kehamilan"));
                d.plasenta = bersihkan(r.getString("plasenta_berimplatansi"));
                d.derajatMaturitas = bersihkan(r.getString("derajat_maturitas"));
                d.airKetuban = bersihkan(r.getString("jumlah_air_ketuban"));
                d.afi = bersihkan(r.getString("indek_cairan_ketuban"));
                d.kelainanKongenital = bersihkan(r.getString("kelainan_kongenital"));
                d.jenisKelamin = bersihkan(r.getString("peluang_sex"));
                d.kesimpulan = bersihkan(r.getString("kesimpulan"));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaDokter = nz(r.getString("nama_dokter"));
                d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi USG ambilData : " + e);
        }
        return d;
    }

    /** Cari id Composition existing lewat encounter; cocokkan by TITLE (identifier dibuang server).
     *  Hanya terima Composition type 28570-0 dgn title tepat sama, agar tak keliru ambil laporan lain. */
    private String cariIdCompositionServer(String idEncounter, String title) {
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
                    if (!title.equals(nz(res.path("title").asText()))) continue;
                    for (JsonNode cd : res.path("type").path("coding")) {
                        if ("28570-0".equals(cd.path("code").asText())) {
                            return nz(res.path("id").asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi USG cariIdCompositionServer : " + e);
        }
        return "";
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
