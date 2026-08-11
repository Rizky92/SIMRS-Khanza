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
 * Pengiriman "Laporan Tindakan ESWL" (FHIR Composition - LOINC 28570-0, Procedure note) ke SATUSEHAT.
 *
 * Sumber data: hasil_tindakan_eswl. Composition murni naratif (section.text.div) mengikuti pola
 * SatuSehatLaporanTindakanEcho — TIDAK membuat resource Condition/Procedure/AllergyIntolerance
 * terpisah, karena tabel sumber hanya menyimpan teks bebas (bukan kode ICD/SNOMED terstruktur).
 *
 * Idempotent: cari Composition existing lewat encounter, cocokkan HANYA identifier ESWL-{noRawat}
 * (tidak fallback ke tipe 28570-0 seperti Echo — supaya kiriman ESWL tidak menimpa Composition Echo
 * pada kunjungan yang sama, karena keduanya bertipe 28570-0). Lalu PUT; kirim ulang tak menduplikasi.
 */
public class SatuSehatLaporanTindakanEswl {

    /** SNOMED CT untuk event Composition. Kode prosedur ESWL 313160009 DITOLAK subset SNOMED SATUSEHAT
     *  ("Code not found", RuleNumber 10670) — sudah diuji ke staging. Dipakai 95570007 (Kidney stone),
     *  yakni diagnosis, yang TERBUKTI diterima server (sesuai contoh Composition ESWL dari SATUSEHAT).
     *  Cocok untuk ESWL batu ginjal; bila ada ESWL batu ureter/buli, kode ini tetap perlu ditinjau. */
    private static final String SNOMED_ESWL_CODE = "95570007";
    private static final String SNOMED_ESWL_DISPLAY = "Kidney stone";

    /** Judul Composition. Dipakai untuk MEMBEDAKAN laporan ESWL dari Echo saat lookup idempotensi:
     *  SATUSEHAT MEMBUANG identifier custom (menyimpan identifier "creator"=idOrg), dan Composition
     *  tak bisa dicari by identifier. Yang dipertahankan server adalah `title`. Echo & ESWL sama-sama
     *  type 28570-0, jadi cocokkan lewat title inilah satu-satunya cara aman agar tak saling menimpa. */
    private static final String TITLE = "Laporan Tindakan ESWL";

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatLaporanTindakanEswl() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Laporan ESWL : " + e);
        }
    }

    private static class EswlData {
        String noRawat="", mulaiRaw="", waktuMulai="", waktuSelesai="";   // mulaiRaw = kunci per tindakan (raw datetime)
        String diagnosa="", tindakan="", obatAnalgesik="", obatLain="", uraianTindakan="",
                focus="", rate="", power="", shock="", diintegrasi="", kekurangan="", anjungan="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /**
     * Bangun & kirim Laporan Tindakan ESWL untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter SATUSEHAT (WAJIB — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Laporan ESWL (POST form) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        java.util.List<EswlData> semua = ambilSemuaData(noRawat);
        if (semua.isEmpty()) return null;
        String encounterRef = "Encounter/" + idEncounter;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        boolean adaEntry = false;
        for (EswlData d : semua) {            // SEMUA tindakan -> satu entry Composition masing-masing
            if (d.idPasien.equals("")) continue;
            String patientRef = "Patient/" + d.idPasien;
            ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef);
            ObjectNode entry = entries.addObject();
            entry.put("fullUrl", "urn:uuid:" + UUID.randomUUID().toString());
            entry.set("resource", comp);
            ObjectNode request = entry.putObject("request");
            request.put("method", "POST");
            request.put("url", "Composition");
            adaEntry = true;
        }
        return adaEntry ? bundle : null;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        // MULTI-LAPORAN: kirim SEMUA tindakan ESWL kunjungan ini (bukan hanya yang terbaru), masing-masing
        // sebagai Composition terpisah (keyed no_rawat+mulai). Satu tindakan gagal tak menghentikan yang lain.
        java.util.List<EswlData> semua = ambilSemuaData(noRawat);
        if (semua.isEmpty()) {
            return;   // tidak ada baris hasil_tindakan_eswl
        }
        Exception gagalTerakhir = null;
        for (EswlData d : semua) {
            try {
                kirimSatu(noRawat, idEncounter, d);
            } catch (Exception ex) {
                gagalTerakhir = ex;   // lanjut tindakan lain; galat dilempar ulang di akhir
            }
        }
        if (gagalTerakhir != null) {
            throw gagalTerakhir;
        }
    }

    /** Kirim satu Composition ESWL untuk SATU tindakan (idempoten per no_rawat+mulai). */
    private void kirimSatu(String noRawat, String idEncounter, EswlData d) throws Exception {
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Laporan ESWL : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef);
        // Idempotensi PER TINDAKAN: id server disimpan lokal per (no_rawat, mulai). Ada -> PUT; else POST.
        // (Fallback by-TITLE dihapus: untuk >1 ESWL/kunjungan title-nya sama -> ambigu; andalkan id tersimpan.)
        String idLama = ambilIdLokal(noRawat, d.mulaiRaw);
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
        System.out.println("URL Laporan ESWL : " + link);
        System.out.println("Request JSON ESWL (tindakan " + d.mulaiRaw + ") : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (±, en/em-dash) rusak jadi "?"/"ï¿½" di server. Bytes UTF-8 = server baca benar.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error ESWL Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error ESWL OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error ESWL Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON ESWL : " + hasil);
        // Catat id server Composition ke tabel lokal (per tindakan) agar kiriman berikutnya PUT.
        String idBaru = adaId ? idLama : extractIdComposition(hasil);
        if (!idBaru.equals("")) simpanIdLokal(noRawat, d.mulaiRaw, idBaru);
    }

    /** Ambil id Composition tersimpan untuk SATU tindakan (no_rawat + mulai). "" bila belum ada. */
    private String ambilIdLokal(String noRawat, String mulai) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(id_composition,'') as id_composition from satu_sehat_laporan_eswl "
                    + "where no_rawat=? and mulai=? limit 1");
            p.setString(1, noRawat);
            p.setString(2, mulai);
            ResultSet r = p.executeQuery();
            String id = "";
            if (r.next()) id = nz(r.getString("id_composition"));
            r.close();
            p.close();
            return id;
        } catch (Exception e) {
            System.out.println("Notifikasi ESWL ambilIdLokal : " + e);
            return "";
        }
    }

    /** Upsert id Composition per TINDAKAN (kunci no_rawat + mulai) — mendukung >1 ESWL per kunjungan. */
    private void simpanIdLokal(String noRawat, String mulai, String idComposition) {
        if (idComposition == null || idComposition.equals("")) return;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "insert into satu_sehat_laporan_eswl (no_rawat, mulai, id_composition) values (?,?,?) "
                    + "on duplicate key update id_composition=values(id_composition)");
            p.setString(1, noRawat);
            p.setString(2, mulai);
            p.setString(3, idComposition);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ESWL simpanIdLokal : " + e);
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
            System.out.println("Notifikasi ESWL extractIdComposition : " + e);
        }
        return "";
    }

    /** Composition Laporan Tindakan ESWL (Procedure note 28570-0): header + event + section naratif. */
    private ObjectNode buatComposition(String noRawat, EswlData d, String patientRef, String encounterRef) {
        String waktu = d.waktuMulai;
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "ESWL-" + noRawat + "-" + kunciMulai(d.mulaiRaw));   // unik per TINDAKAN
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
        // event: prosedur ESWL (SNOMED) + periode mulai..selesai.
        ObjectNode event = comp.putArray("event").addObject();
        ObjectNode evCoding = event.putArray("code").addObject().putArray("coding").addObject();
        evCoding.put("system", "http://snomed.info/sct");
        evCoding.put("code", SNOMED_ESWL_CODE);
        evCoding.put("display", SNOMED_ESWL_DISPLAY);
        if (!d.waktuMulai.equals("") || !d.waktuSelesai.equals("")) {
            ObjectNode period = event.putObject("period");
            if (!d.waktuMulai.equals("")) period.put("start", d.waktuMulai);
            if (!d.waktuSelesai.equals("")) period.put("end", d.waktuSelesai);
        }

        // Narrative (text.div) WAJIB.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Laporan Tindakan ESWL - "
                + escapeXml(d.namaPasien) + "</div>");

        // Sections (naratif). Urut mengikuti alur laporan tindakan.
        ArrayNode section = comp.putArray("section");
        // Anestesi/sedasi (59774-0).
        tambahSection(section, "Anesthesia Section", "59774-0", "Anesthesia", d.obatAnalgesik);
        // Uraian tindakan lengkap (29554-3) — XHTML terstruktur agar mudah dibaca di viewer.
        tambahSectionHtml(section, "Procedure Description Section", "29554-3", "Procedure Narrative",
                htmlUraianTindakan(d));
        // Diagnosis pasca tindakan (59769-0) — teks bebas dari kolom diagnosa.
        tambahSection(section, "Postprocedure Diagnosis Section", "59769-0",
                "Postprocedure diagnosis Narrative", d.diagnosa);
        // Nama tindakan (47519-4).
        tambahSection(section, "Procedures Section", "47519-4", "History of Procedures Document", d.tindakan);
        // Evaluasi/kesan pasca tindakan (51847-2).
        tambahSection(section, "Assessment and Plan Section", "51847-2", "Evaluation + Plan note", d.kekurangan);
        // Rencana tindak lanjut / anjuran (18776-5).
        tambahSection(section, "Plan of Treatment Section", "18776-5", "Plan of care note", d.anjungan);
        return comp;
    }

    /** Rakit XHTML terstruktur untuk section Uraian Tindakan (29554-3). Nilai selalu di-escape. */
    private String htmlUraianTindakan(EswlData d) {
        StringBuilder sb = new StringBuilder();
        tambahParagraf(sb, "Uraian Tindakan", d.uraianTindakan);
        // Parameter teknis dalam satu paragraf bila ada yang terisi.
        String param = gabungLabel("Fokus", d.focus, "Rate", d.rate, "Power", d.power, "Shock", d.shock);
        if (!param.equals("")) sb.append("<p>").append(escapeXml(param)).append("</p>");
        tambahParagraf(sb, "Disintegrasi", d.diintegrasi);
        tambahParagraf(sb, "Obat Lain", d.obatLain);
        return sb.toString();
    }

    /** Tambah "<p><strong>label:</strong> nilai</p>" bila nilai terisi (nilai di-escape). */
    private void tambahParagraf(StringBuilder sb, String label, String nilai) {
        String v = nz(nilai).trim();
        if (v.equals("") || v.equals("-")) return;
        sb.append("<p><strong>").append(escapeXml(label)).append(":</strong> ").append(escapeXml(v)).append("</p>");
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

    /** Kunci ringkas dari mulai (raw datetime) untuk identifier per tindakan: hanya digit (mis. 20260625090000). */
    private String kunciMulai(String mulaiRaw) {
        String s = (mulaiRaw == null) ? "" : mulaiRaw.replaceAll("[^0-9]", "");
        return s.equals("") ? "0" : s;
    }

    private java.util.List<EswlData> ambilSemuaData(String noRawat) {
        java.util.List<EswlData> hasil = new java.util.ArrayList<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select te.mulai, te.selesai, "
                    + "ifnull(te.diagnosa,'') as diagnosa, ifnull(te.tindakan,'') as tindakan, "
                    + "ifnull(te.obat_analgesik,'') as obat_analgesik, ifnull(te.obat_lain,'') as obat_lain, "
                    + "ifnull(te.uraian_tindakan,'') as uraian_tindakan, "
                    + "ifnull(te.uraian_tindakan_focus,'') as uraian_tindakan_focus, "
                    + "ifnull(te.uraian_tindakan_rate,'') as uraian_tindakan_rate, "
                    + "ifnull(te.uraian_tindakan_power,'') as uraian_tindakan_power, "
                    + "ifnull(te.uraian_tindakan_shock,'') as uraian_tindakan_shock, "
                    + "ifnull(te.diintegrasi,'') as diintegrasi, ifnull(te.kekurangan,'') as kekurangan, "
                    + "ifnull(te.anjungan,'') as anjungan, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nama_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from hasil_tindakan_eswl te "
                    + "inner join reg_periksa rp on rp.no_rawat=te.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=te.kd_dokter "
                    + "left join pegawai pg on pg.nik=te.kd_dokter "
                    + "where te.no_rawat=? order by te.mulai asc");   // SEMUA tindakan, urut waktu
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                EswlData d = new EswlData();
                d.noRawat = noRawat;
                d.mulaiRaw = nz(r.getString("mulai"));   // kunci per tindakan (raw datetime)
                d.waktuMulai = formatWaktu(nz(r.getString("mulai")));
                d.waktuSelesai = formatWaktu(nz(r.getString("selesai")));
                d.diagnosa = bersihkan(r.getString("diagnosa"));
                d.tindakan = bersihkan(r.getString("tindakan"));
                d.obatAnalgesik = bersihkan(r.getString("obat_analgesik"));
                d.obatLain = bersihkan(r.getString("obat_lain"));
                d.uraianTindakan = bersihkan(r.getString("uraian_tindakan"));
                d.focus = bersihkan(r.getString("uraian_tindakan_focus"));
                d.rate = bersihkan(r.getString("uraian_tindakan_rate"));
                d.power = bersihkan(r.getString("uraian_tindakan_power"));
                d.shock = bersihkan(r.getString("uraian_tindakan_shock"));
                d.diintegrasi = bersihkan(r.getString("diintegrasi"));
                d.kekurangan = bersihkan(r.getString("kekurangan"));
                d.anjungan = bersihkan(r.getString("anjungan"));
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaDokter = nz(r.getString("nama_dokter"));
                d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
                hasil.add(d);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ESWL ambilSemuaData : " + e);
        }
        return hasil;
    }

    /** Cari id Composition existing lewat encounter; cocokkan by TITLE (bukan identifier — dibuang server).
     *  Hanya menerima Composition type 28570-0 dgn title tepat sama, agar tak keliru ambil laporan Echo
     *  yang juga 28570-0. Kembalikan "" bila tak ada -> pemanggil akan POST (dengan ifNoneExist). */
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
            System.out.println("Notifikasi ESWL cariIdCompositionServer : " + e);
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
