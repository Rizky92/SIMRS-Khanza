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
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Pengiriman "Laporan Tindakan Echo" (FHIR Composition - LOINC 28570-0, Procedure note) ke SATUSEHAT.
 *
 * Sumber data: hasil_pemeriksaan_echo. Composition naratif (section.text.div) + event (Echocardiography).
 * Idempotent: cari Composition existing lewat encounter -> cocokkan identifier ECHO-{noRawat} / type 28570-0,
 * lalu PUT (kirim ulang tidak menduplikasi).
 */
public class SatuSehatLaporanTindakanEcho {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatLaporanTindakanEcho() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Laporan Echo : " + e);
        }
        pastikanTabel();
    }

    /**
     * Tabel pelacak id Composition. Dulu id Echo hanya hidup di server dan dicari ulang lewat
     * GET tiap kirim; akibatnya status "sudah/belum kirim" tak bisa dijawab dari basis data lokal
     * (panel status di form RME). Disimpan lokal juga membuat pengiriman berikutnya tak perlu GET.
     */
    private void pastikanTabel() {
        try (PreparedStatement p = koneksi.prepareStatement(
                "create table if not exists satu_sehat_laporan_echo ("
                + "no_rawat varchar(17) not null, id_composition varchar(50) default '', "
                + "primary key (no_rawat)) engine=InnoDB default charset=latin1")) {
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi Echo pastikanTabel : " + e);
        }
    }

    /** Id Composition tersimpan. "" bila belum pernah dikirim dari instalasi ini. */
    private String ambilIdLokal(String noRawat) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(id_composition,'') as id_composition from satu_sehat_laporan_echo "
                + "where no_rawat=? limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return nz(r.getString("id_composition"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Echo ambilIdLokal : " + e);
        }
        return "";
    }

    /** Upsert id Composition (idempotent per no_rawat). */
    private void simpanIdLokal(String noRawat, String idComposition) {
        if (idComposition == null || idComposition.equals("")) {
            return;
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "replace into satu_sehat_laporan_echo (no_rawat, id_composition) values (?,?)")) {
            p.setString(1, noRawat);
            p.setString(2, idComposition);
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi Echo simpanIdLokal : " + e);
        }
    }

    /** Ekstrak id Composition dari transaction-response (entry[0].response.resourceID / location). */
    private String idDariRespons(String responseBody) {
        try {
            JsonNode r = mapper.readTree(responseBody);
            JsonNode resp = r.path("entry").path(0).path("response");
            String id = nz(resp.path("resourceID").asText());
            if (!id.equals("")) {
                return id;
            }
            String loc = nz(resp.path("location").asText());   // .../Composition/{id}/_history/{v}
            if (!loc.equals("")) {
                String[] bagian = loc.split("/");
                for (int i = 0; i < bagian.length - 1; i++) {
                    if (bagian[i].equals("Composition")) {
                        return bagian[i + 1];
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Echo idDariRespons : " + e);
        }
        return "";
    }

    private static class EchoData {
        String noRawat="", waktu="";
        String sistolik="", diastolic="", kontraktilitas="", dimensiRuang="", katup="",
                analisaSegmental="", erap="", lainLain="", kesimpulan="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /**
     * Bangun & kirim Laporan Tindakan Echo untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter SATUSEHAT (WAJIB — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Laporan Echo (POST form) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        EchoData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef, d.waktu);
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
        EchoData d = ambilData(noRawat);
        if (d == null) {
            return;   // tidak ada hasil_pemeriksaan_echo
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Laporan Echo : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        ObjectNode comp = buatComposition(noRawat, d, patientRef, encounterRef, d.waktu);
        // Id lokal lebih dulu (murah, tanpa jaringan). Bila kosong — instalasi lama atau dokumen
        // yang dikirim sebelum pelacakan ini ada — baru cari ke server by TITLE (identifier dibuang
        // server; type 28570-0 dipakai Echo & ESWL, jadi title-lah pembedanya agar tak menimpa ESWL).
        String idLama = ambilIdLokal(noRawat);
        if (idLama.equals("")) {
            idLama = cariIdCompositionServer(idEncounter, "Laporan Tindakan Echo");
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
        System.out.println("URL Laporan Echo : " + link);
        System.out.println("Request JSON Echo : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (±, en/em-dash) rusak jadi "?"/"ï¿½" di server. Bytes UTF-8 = server baca benar.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Echo Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Echo OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Echo Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Echo : " + hasil);
        // Catat id server agar kiriman berikutnya langsung PUT dan status "Sudah Kirim" terbaca lokal.
        simpanIdLokal(noRawat, adaId ? idLama : idDariRespons(hasil));
    }

    /** Composition Laporan Tindakan Echo (Procedure note 28570-0): header + event + section naratif. */
    private ObjectNode buatComposition(String noRawat, EchoData d, String patientRef, String encounterRef, String waktu) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "ECHO-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "28570-0");
        typeCoding.put("display", "Procedure note");
        comp.put("title", "Laporan Tindakan Echo");
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
        // event: tindakan Echocardiography (SNOMED 40701008) + waktu.
        ObjectNode event = comp.putArray("event").addObject();
        ObjectNode evCoding = event.putArray("code").addObject().putArray("coding").addObject();
        evCoding.put("system", "http://snomed.info/sct");
        evCoding.put("code", "40701008");
        evCoding.put("display", "Echocardiography");
        if (!waktu.equals("")) {
            event.putObject("period").put("start", waktu);
        }

        // Narrative (text.div) WAJIB.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Laporan Tindakan Echo - "
                + escapeXml(d.namaPasien) + "</div>");

        // Sections. Ditata per-baris (paragraf) agar mudah dibaca di viewer, bukan satu blok "a. b. c.".
        ArrayNode section = comp.putArray("section");
        String temuanHtml = htmlTemuan(d);
        tambahSectionHtml(section, "Procedure Findings Section", "59776-5", "Procedure findings Narrative", temuanHtml);
        // Laporan LENGKAP (temuan + kesimpulan) -> Postprocedure diagnosis (59769-0): slot yang
        // ditampilkan viewer "Laporan Tindakan", jadi seluruh hasil echo tampil utuh & rapi.
        String laporanHtml = temuanHtml;
        if (!nz(d.kesimpulan).trim().equals("") && !nz(d.kesimpulan).trim().equals("-")) {
            laporanHtml += "<p><strong>Kesimpulan:</strong> " + escapeXml(d.kesimpulan.trim()) + "</p>";
        }
        tambahSectionHtml(section, "Postprocedure Diagnosis Section", "59769-0",
                "Postprocedure diagnosis Narrative", laporanHtml);
        return comp;
    }

    /** Rakit XHTML temuan echo: satu paragraf per field yang terisi (nilai di-escape). */
    private String htmlTemuan(EchoData d) {
        StringBuilder sb = new StringBuilder();
        tambahParagraf(sb, "Tekanan Darah Sistolik", d.sistolik);
        tambahParagraf(sb, "Tekanan Darah Diastolik", d.diastolic);
        tambahParagraf(sb, "Kontraktilitas", d.kontraktilitas);
        tambahParagraf(sb, "Dimensi Ruang Jantung", d.dimensiRuang);
        tambahParagraf(sb, "Katup", d.katup);
        tambahParagraf(sb, "Analisa Segmental", d.analisaSegmental);
        tambahParagraf(sb, "eRAP", d.erap);
        tambahParagraf(sb, "Lain-lain", d.lainLain);
        return sb.toString();
    }

    /** Tambah "&lt;p&gt;&lt;strong&gt;label:&lt;/strong&gt; nilai&lt;/p&gt;" bila nilai terisi (nilai di-escape). */
    private void tambahParagraf(StringBuilder sb, String label, String nilai) {
        String v = nz(nilai).trim();
        if (v.equals("") || v.equals("-")) return;
        sb.append("<p><strong>").append(escapeXml(label)).append(":</strong> ").append(escapeXml(v)).append("</p>");
    }

    /** Tambah section dengan konten XHTML sudah jadi (dalamnya sudah di-escape oleh pemanggil). */
    private void tambahSectionHtml(ArrayNode section, String title, String loinc, String display, String innerXhtml) {
        if (innerXhtml == null || innerXhtml.trim().equals("")) return;
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + innerXhtml + "</div>");
    }

    private EchoData ambilData(String noRawat) {
        EchoData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select he.tanggal, ifnull(he.sistolik,'') as sistolik, ifnull(he.diastolic,'') as diastolic, "
                    + "ifnull(he.kontraktilitas,'') as kontraktilitas, ifnull(he.dimensi_ruang,'') as dimensi_ruang, "
                    + "ifnull(he.katup,'') as katup, ifnull(he.analisa_segmental,'') as analisa_segmental, "
                    + "ifnull(he.erap,'') as erap, ifnull(he.lain_lain,'') as lain_lain, ifnull(he.kesimpulan,'') as kesimpulan, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nama_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from hasil_pemeriksaan_echo he "
                    + "inner join reg_periksa rp on rp.no_rawat=he.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=he.kd_dokter "
                    + "left join pegawai pg on pg.nik=he.kd_dokter "
                    + "where he.no_rawat=? order by he.tanggal desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new EchoData();
                d.noRawat = noRawat;
                d.waktu = formatWaktu(nz(r.getString("tanggal")));
                d.sistolik = bersihkan(r.getString("sistolik"));
                d.diastolic = bersihkan(r.getString("diastolic"));
                d.kontraktilitas = bersihkan(r.getString("kontraktilitas"));
                d.dimensiRuang = bersihkan(r.getString("dimensi_ruang"));
                d.katup = bersihkan(r.getString("katup"));
                d.analisaSegmental = bersihkan(r.getString("analisa_segmental"));
                d.erap = bersihkan(r.getString("erap"));
                d.lainLain = bersihkan(r.getString("lain_lain"));
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
            System.out.println("Notifikasi Echo ambilData : " + e);
        }
        return d;
    }

    /** Cari id Composition existing lewat encounter; cocokkan by TITLE (identifier dibuang server).
     *  Hanya terima Composition type 28570-0 dgn title tepat sama, agar tak keliru ambil laporan ESWL
     *  yang juga 28570-0 (yang sebelumnya membuat Echo & ESWL saling menimpa). */
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
            System.out.println("Notifikasi Echo cariIdCompositionServer : " + e);
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
