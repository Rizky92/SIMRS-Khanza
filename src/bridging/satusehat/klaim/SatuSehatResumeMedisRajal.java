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
 * Sub-sender Resume Medis Rawat Jalan (Composition LOINC 88645-7) untuk flow SatuSehatBundle.
 *
 * Sumber data (2 case):
 *   1) resume_pasien (bila ada baris utk no_rawat) -> diagnosa/prosedur/obat/kondisi pulang + keluhan/lab.
 *   2) bila tidak ada -> pemeriksaan_ralan SOAP TERAKHIR (order tgl_perawatan, jam_rawat desc) -> keluhan,
 *      pemeriksaan fisik, penilaian, rtl, instruksi, evaluasi. Vitals/alergi selalu diambil dari ralan terakhir.
 * Section naratif (text.div) meniru Resume Medis Rawat Inap. Idempotent: id deterministik + PUT.
 */
public class SatuSehatResumeMedisRajal {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    private static final String NA = "Section not available";

    public SatuSehatResumeMedisRajal() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi ResumeRajal : " + e);
        }
    }

    private static class Data {
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="", waktu="";
        String keluhanUtama="", riwayatSekarang="", riwayatAlergi="", vitals="", fisik="";
        String lab="", prosedur="", perencanaan="", diagnosisAkhir="", obatPulang="", kondisiPulang="";
        String edukasi="", tindakLanjut="", perjalanan="";
    }

    /** PREVIEW: rakit Bundle Resume Medis Rawat Jalan tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        Data d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("") || d.idDokter.equals("")) return null;
        String compId = stableResourceId("CompositionResumeRajal", noRawat);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        ObjectNode comp = buatComposition(entries, noRawat, d, compId, idEncounter);
        tambahEntry(entries, "Composition", compId, comp);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return;
        Data d = ambilData(noRawat);
        if (d == null) {
            System.out.println("Notifikasi ResumeRajal : tidak ada resume_pasien/pemeriksaan_ralan utk " + noRawat + ". Dilewati.");
            return;
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi ResumeRajal : ID pasien belum ada utk " + noRawat + ". Dilewati.");
            return;
        }
        if (d.idDokter.equals("")) {
            System.out.println("Notifikasi ResumeRajal : ID dokter (author) belum ada utk " + noRawat + ". Dilewati.");
            return;
        }

        String compId = stableResourceId("CompositionResumeRajal", noRawat);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        // buatComposition menambahkan Observation tiap section ke entries (dirujuk via section.entry),
        // lalu Composition ditambahkan TERAKHIR.
        ObjectNode comp = buatComposition(entries, noRawat, d, compId, idEncounter);
        tambahEntry(entries, "Composition", compId, comp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL ResumeRajal : " + link);
        System.out.println("Request JSON ResumeRajal : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("Error ResumeRajal Status Code: " + ex.getStatusCode());
            System.out.println("Error ResumeRajal Body: " + ex.getResponseBodyAsString());
            throw ex;
        }
        System.out.println("Result JSON ResumeRajal : " + hasil);
        Sequel.queryu2("replace into satu_sehat_resume_ralan (no_rawat,id_composition) values (?,?)",
                2, new String[]{noRawat, compId});
    }

    // ====================== COMPOSITION ======================

    private ObjectNode buatComposition(ArrayNode entries, String noRawat, Data d, String id, String idEncounter) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        comp.put("id", id);
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "RESRJL-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "88645-7");
        typeCoding.put("display", "Outpatient hospital Discharge summary");
        ObjectNode catCoding = comp.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://loinc.org");
        catCoding.put("code", "LP173421-1");
        catCoding.put("display", "Report");
        comp.put("title", "Resume Medis Rawat Jalan");
        if (!d.waktu.equals("")) comp.put("date", d.waktu);
        ObjectNode subject = comp.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);
        comp.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        ObjectNode author = comp.putArray("author").addObject();
        author.put("reference", "Practitioner/" + d.idDokter);
        author.put("display", d.namaDokter);
        comp.putObject("custodian").put("reference", "Organization/" + d.idOrg);

        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Resume Medis Rawat Jalan - "
                + escapeXml(d.namaPasien) + "</div>");

        String perjalanan = d.perjalanan.equals("")
                ? "Pasien datang, dilakukan pemeriksaan dan penanganan sesuai indikasi." : d.perjalanan;
        ArrayNode section = comp.putArray("section");

        // Anamnesis (group)
        ArrayNode anamnesis = grup(section, "Anamnesis");
        leafObs(entries, anamnesis, noRawat, d, idEncounter, "Keluhan Utama", "10154-3", "Chief complaint Narrative - Reported", "survey", d.keluhanUtama, "keluhan");
        leafObs(entries, anamnesis, noRawat, d, idEncounter, "Riwayat Penyakit Pribadi Sekarang", "10164-2", "History of Present illness Narrative", "exam", d.riwayatSekarang, "rps");
        leafObs(entries, anamnesis, noRawat, d, idEncounter, "Riwayat Penyakit Keluarga", "10157-6", "History of family member diseases Narrative", "social-history", "", "rpk");
        leafObs(entries, anamnesis, noRawat, d, idEncounter, "Riwayat Alergi", "48765-2", "Allergies and adverse reactions Document", "survey", d.riwayatAlergi, "alergi");
        leafObs(entries, anamnesis, noRawat, d, idEncounter, "Riwayat Pengobatan", "10160-0", "History of Medication use Narrative", "survey", "", "rwobat");

        // Pemeriksaan Fisik (group)
        ArrayNode fisik = grup(section, "Pemeriksaan Fisik");
        leafObs(entries, fisik, noRawat, d, idEncounter, "Tanda Vital", "8716-3", "Vital signs", "vital-signs", d.vitals, "vital");
        leafObs(entries, fisik, noRawat, d, idEncounter, "Pemeriksaan Fisik Head to Toe", "10187-3", "Review of systems Narrative - Reported", "exam", d.fisik, "fisik");

        // Pemeriksaan Fungsional
        leafObs(entries, section, noRawat, d, idEncounter, "Pemeriksaan Fungsional", "47420-5", "Functional status assessment note", "exam", "", "fungsional");

        // Tindakan / Prosedur
        leafObs(entries, section, noRawat, d, idEncounter, "Tindakan / Prosedur (ICD-9)", "47519-4", "Procedures and interventions", "exam", d.prosedur, "prosedur");

        // Perencanaan Perawatan
        leafObs(entries, section, noRawat, d, idEncounter, "Perencanaan Perawatan", "18776-5", "Plan of treatment (narrative)", "exam", d.perencanaan, "perencanaan");

        // Pemeriksaan Penunjang (group)
        ArrayNode penunjang = grup(section, "Pemeriksaan Penunjang");
        leafObs(entries, penunjang, noRawat, d, idEncounter, "Hasil Pemeriksaan Laboratorium", "11502-2", "Laboratory report", "laboratory", d.lab, "lab");
        leafObs(entries, penunjang, noRawat, d, idEncounter, "Hasil Pemeriksaan Radiologi", "18782-3", "Radiology Study observation (narrative)", "imaging", "", "radiologi");

        // Diagnosis (group)
        ArrayNode diagnosis = grup(section, "Diagnosis");
        leafObs(entries, diagnosis, noRawat, d, idEncounter, "Diagnosis Akhir", "78375-3", "Discharge diagnosis Narrative", "exam", d.diagnosisAkhir, "dxakhir");

        // Imunisasi
        leafObs(entries, section, noRawat, d, idEncounter, "Imunisasi", "11369-6", "History of Immunization Narrative", "survey", "", "imunisasi");

        // Farmasi (group)
        ArrayNode farmasi = grup(section, "Farmasi");
        leafObs(entries, farmasi, noRawat, d, idEncounter, "Obat Saat Kunjungan", "42346-7", "Medications on admission (narrative)", "survey", "", "obatkunjungan");
        leafObs(entries, farmasi, noRawat, d, idEncounter, "Obat Pulang", "75311-1", "Discharge medications Narrative", "survey", d.obatPulang, "obatpulang");

        // Diet (group)
        ArrayNode diet = grup(section, "Diet");
        leafObs(entries, diet, noRawat, d, idEncounter, "Rekomendasi Diet", "42344-2", "Discharge diet (narrative)", "exam", "", "diet");

        // Edukasi
        leafObs(entries, section, noRawat, d, idEncounter, "Edukasi", "34895-3", "Education note", "exam", d.edukasi, "edukasi");

        // Kondisi Saat Meninggalkan RS
        leafObs(entries, section, noRawat, d, idEncounter, "Kondisi Saat Meninggalkan Rumah Sakit", "10184-0", "Hospital discharge physical findings Narrative", "exam", d.kondisiPulang, "kondisi");

        // Rencana Tindak Lanjut
        leafObs(entries, section, noRawat, d, idEncounter, "Rencana Tindak Lanjut", "8653-8", "Hospital Discharge instructions", "exam", d.tindakLanjut, "rtl");

        // Perjalanan Kunjungan
        leafObs(entries, section, noRawat, d, idEncounter, "Perjalanan Kunjungan Pasien", "8648-8", "Hospital course Narrative", "exam", perjalanan, "course");

        return comp;
    }

    /** Group section (punya sub-section). text.div WAJIB juga (viewer INA-CBG membacanya). */
    private ArrayNode grup(ArrayNode parent, String title) {
        ObjectNode g = parent.addObject();
        g.put("title", title);
        ObjectNode t = g.putObject("text");
        t.put("status", "additional");
        t.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(title) + "</div>");
        return g.putArray("section");
    }

    /**
     * Leaf section. Bila narasi terisi: buat Observation (valueString) -> tambah ke bundle -> section.entry
     * merujuknya (viewer SATUSEHAT merender via entry). Bila kosong: text "Section not available".
     */
    private void leafObs(ArrayNode entries, ArrayNode arr, String noRawat, Data d, String idEncounter,
            String title, String loinc, String display, String kategori, String narasi, String slot) {
        ObjectNode sec = arr.addObject();
        sec.put("title", title);
        if (loinc != null) {
            ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
            coding.put("system", "http://loinc.org");
            coding.put("code", loinc);
            coding.put("display", display);
        }
        String isi = (narasi == null) ? "" : narasi.trim();
        boolean ada = !isi.equals("") && !isi.equals("-");
        // text.div WAJIB tiap section (viewer INA-CBG membaca section.text.div). Selalu ada.
        ObjectNode secText = sec.putObject("text");
        secText.put("status", ada ? "generated" : "additional");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + (ada ? escapeXml(isi) : NA) + "</div>");
        if (!ada) return;
        // + Observation (viewer SATUSEHAT merender via entry).
        String obsId = stableResourceId("ObsResRajal|" + slot, noRawat);
        ObjectNode o = mapper.createObjectNode();
        o.put("resourceType", "Observation");
        o.put("id", obsId);
        o.put("status", "final");
        ObjectNode cat = o.putArray("category").addObject().putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        cat.put("code", kategori);
        cat.put("display", catDisplay(kategori));
        ObjectNode code = o.putObject("code").putArray("coding").addObject();
        code.put("system", "http://loinc.org");
        code.put("code", loinc);
        code.put("display", display);
        ObjectNode subj = o.putObject("subject");
        subj.put("reference", "Patient/" + d.idPasien);
        subj.put("display", d.namaPasien);
        o.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!d.waktu.equals("")) o.put("effectiveDateTime", d.waktu);
        o.putArray("performer").addObject().put("reference", "Practitioner/" + d.idDokter);   // performer WAJIB
        o.put("valueString", isi);
        tambahEntry(entries, "Observation", obsId, o);
        sec.putArray("entry").addObject().put("reference", "Observation/" + obsId);
    }

    private String catDisplay(String kategori) {
        switch (kategori) {
            case "vital-signs": return "Vital Signs";
            case "exam": return "Exam";
            case "laboratory": return "Laboratory";
            case "imaging": return "Imaging";
            case "social-history": return "Social History";
            default: return "Survey";
        }
    }

    private void tambahEntry(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    // ====================== DATA ======================

    private Data ambilData(String noRawat) {
        Data d = null;
        try {
            // Konteks pasien + DPJP (author).
            String ktpPasien="", namaPasien="", namaDokter="", ktpDokter="", waktu="";
            PreparedStatement pk = koneksi.prepareStatement(
                    "select p.no_ktp as ktp_pasien, p.nm_pasien, concat(rp.tgl_registrasi,' ',rp.jam_reg) as waktu_reg, "
                    + "ifnull(dr.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                    + "from reg_periksa rp inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter where rp.no_rawat=? limit 1");
            pk.setString(1, noRawat);
            ResultSet rk = pk.executeQuery();
            if (rk.next()) {
                ktpPasien = nz(rk.getString("ktp_pasien"));
                namaPasien = nz(rk.getString("nm_pasien"));
                namaDokter = nz(rk.getString("nm_dokter"));
                ktpDokter = nz(rk.getString("ktp_dokter"));
                waktu = formatWaktu(nz(rk.getString("waktu_reg")));
            }
            rk.close();
            pk.close();
            if (ktpPasien.equals("")) return null;

            d = new Data();
            d.namaPasien = namaPasien;
            d.namaDokter = namaDokter;
            d.waktu = waktu;
            d.idPasien = nz(cek.tampilIDPasien(ktpPasien));
            d.idDokter = nz(cek.tampilIDParktisi(ktpDokter));
            d.idOrg = koneksiDB.IDSATUSEHAT();

            // Vitals/alergi/fisik dari pemeriksaan_ralan SOAP terakhir (selalu diambil bila ada).
            boolean adaRalan = isiDariRalan(noRawat, d);
            // resume_pasien (case 1) menimpa konten utama bila ada.
            boolean adaResume = isiDariResume(noRawat, d);

            if (!adaResume && !adaRalan) return null;
        } catch (Exception e) {
            System.out.println("Notifikasi ResumeRajal ambilData : " + e);
        }
        return d;
    }

    /** Isi dari pemeriksaan_ralan SOAP terakhir. true bila ada baris. */
    private boolean isiDariRalan(String noRawat, Data d) {
        boolean ada = false;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select keluhan, pemeriksaan, alergi, penilaian, rtl, instruksi, evaluasi, "
                    + "tensi, nadi, respirasi, suhu_tubuh, spo2, tinggi, berat, gcs, kesadaran "
                    + "from pemeriksaan_ralan where no_rawat=? order by tgl_perawatan desc, jam_rawat desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                ada = true;
                d.keluhanUtama = bersih(r.getString("keluhan"));
                d.riwayatSekarang = bersih(r.getString("keluhan"));
                d.fisik = bersih(r.getString("pemeriksaan"));
                d.riwayatAlergi = bersih(r.getString("alergi"));
                d.diagnosisAkhir = bersih(r.getString("penilaian"));
                d.perencanaan = bersih(r.getString("rtl"));
                d.tindakLanjut = bersih(r.getString("rtl"));
                d.edukasi = bersih(r.getString("instruksi"));
                d.perjalanan = bersih(r.getString("evaluasi"));
                d.vitals = gabung(
                        "Tekanan Darah", satuan(r.getString("tensi"), "mmHg"),
                        "Nadi", satuan(r.getString("nadi"), "x/menit"),
                        "Pernapasan", satuan(r.getString("respirasi"), "x/menit"),
                        "Suhu", satuan(r.getString("suhu_tubuh"), "C"),
                        "SpO2", satuan(r.getString("spo2"), "%"),
                        "Tinggi Badan", satuan(r.getString("tinggi"), "cm"),
                        "Berat Badan", satuan(r.getString("berat"), "kg"),
                        "GCS", nz(r.getString("gcs")),
                        "Kesadaran", nz(r.getString("kesadaran")));
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ResumeRajal ralan : " + e);
        }
        return ada;
    }

    /** Isi dari resume_pasien (menimpa konten utama). true bila ada baris. */
    private boolean isiDariResume(String noRawat, Data d) {
        boolean ada = false;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select keluhan_utama, jalannya_penyakit, pemeriksaan_penunjang, hasil_laborat, "
                    + "diagnosa_utama, diagnosa_sekunder, diagnosa_sekunder2, diagnosa_sekunder3, diagnosa_sekunder4, "
                    + "prosedur_utama, prosedur_sekunder, prosedur_sekunder2, prosedur_sekunder3, "
                    + "kondisi_pulang, obat_pulang from resume_pasien where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                ada = true;
                setIfAda(d, "keluhanUtama", bersih(r.getString("keluhan_utama")));
                setIfAda(d, "riwayatSekarang", bersih(r.getString("jalannya_penyakit")));
                d.lab = gabungBaris(bersih(r.getString("hasil_laborat")), bersih(r.getString("pemeriksaan_penunjang")));
                d.diagnosisAkhir = gabungList(
                        bersih(r.getString("diagnosa_utama")), bersih(r.getString("diagnosa_sekunder")),
                        bersih(r.getString("diagnosa_sekunder2")), bersih(r.getString("diagnosa_sekunder3")),
                        bersih(r.getString("diagnosa_sekunder4")));
                d.prosedur = gabungList(
                        bersih(r.getString("prosedur_utama")), bersih(r.getString("prosedur_sekunder")),
                        bersih(r.getString("prosedur_sekunder2")), bersih(r.getString("prosedur_sekunder3")));
                d.kondisiPulang = bersih(r.getString("kondisi_pulang"));
                d.obatPulang = bersih(r.getString("obat_pulang"));
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ResumeRajal resume : " + e);
        }
        return ada;
    }

    private void setIfAda(Data d, String field, String val) {
        if (val == null || val.trim().equals("")) return;
        if (field.equals("keluhanUtama")) d.keluhanUtama = val;
        else if (field.equals("riwayatSekarang")) d.riwayatSekarang = val;
    }

    // ====================== UTIL ======================

    private String gabung(String... lv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < lv.length; i += 2) {
            String v = nz(lv[i + 1]).trim();
            if (v.equals("")) continue;
            if (sb.length() > 0) sb.append(". ");
            sb.append(lv[i]).append(": ").append(v);
        }
        return sb.toString();
    }

    private String gabungList(String... items) {
        StringBuilder sb = new StringBuilder();
        for (String it : items) {
            String v = nz(it).trim();
            if (v.equals("")) continue;
            if (sb.length() > 0) sb.append("; ");
            sb.append(v);
        }
        return sb.toString();
    }

    private String gabungBaris(String... items) {
        StringBuilder sb = new StringBuilder();
        for (String it : items) {
            String v = nz(it).trim();
            if (v.equals("")) continue;
            if (sb.length() > 0) sb.append(". ");
            sb.append(v);
        }
        return sb.toString();
    }

    private String satuan(String v, String s) {
        String t = nz(v).trim();
        if (t.equals("") || t.equals("0")) return "";
        return t + " " + s;
    }

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

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String bersih(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
