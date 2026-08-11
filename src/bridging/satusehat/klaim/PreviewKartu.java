/*
  by Ananda Widitomo,S.Kom.
  IT - SIMRS Hj. Fatimah Sulhan
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Renderer kartu Swing untuk PREVIEW KLAIM di {@link SatuSehatBundle}, meniru tampilan viewer
 * web "Dokumen Klaim Elektronik" SATUSEHAT: judul hijau + chip ID + meta (dibuat oleh / tanggal /
 * status / badge TTE), body field berlabel abu-abu, chip kode (LOINC/ICD/KFA/SNOMED), dan untuk
 * Composition ada SECTION (sub-judul hijau + chip LOINC + nilai). Encounter menampilkan blok
 * Informasi Kunjungan + Histori Lokasi berdurasi.
 *
 * Data-driven: field per resource diambil dari registri {@link #REG} (hasil pemetaan builder tiap
 * sender). Murni presentasi — tanpa I/O/DB/jaringan. Preferensi UI: Font PLAIN, lihat [[font-jangan-tebal]].
 */
public final class PreviewKartu {

    private PreviewKartu() { }

    // ===== Palet mengikuti viewer hijau "Dokumen Klaim Elektronik" =====
    private static final Color HIJAU      = new Color(0x2F9E63);
    private static final Color TEKS       = new Color(0x2B2B2B);
    private static final Color LABEL      = new Color(0x8A8F98);
    private static final Color GARIS      = new Color(0xE1E6E1);
    private static final Color CHIP_BG    = new Color(0xEDEFF2);
    private static final Color CHIP_FG    = new Color(0x40464E);
    private static final Color BADGE_BG   = new Color(0xE9ECEF);
    private static final Color BADGE_FG   = new Color(0x495057);
    private static final Color TTE_BG     = new Color(0xFCEBD3);
    private static final Color TTE_FG     = new Color(0xB9770E);
    private static final Color TTE_OK_BG  = new Color(0xDDF3E4);
    private static final Color TTE_OK_FG  = new Color(0x2E7D32);
    private static final Color DATE_BG    = new Color(0xF6F8F6);

    private static final Font F_JUDUL   = new Font("Tahoma", Font.PLAIN, 15);
    private static final Font F_SECTION = new Font("Tahoma", Font.PLAIN, 12);
    private static final Font F_LABEL   = new Font("Tahoma", Font.PLAIN, 10);
    private static final Font F_NILAI   = new Font("Tahoma", Font.PLAIN, 12);
    private static final Font F_CHIP    = new Font("Tahoma", Font.PLAIN, 11);

    private static final int LEBAR = 330;   // acuan lebar wrap teks di panel preview

    // ============================ ENTRY POINT ============================

    /**
     * Render satu resource (atau Bundle) sebagai kartu bergaya viewer.
     * @param judul     judul fallback (label tombol) bila resource tak punya judul sendiri.
     * @param statusTte status TTE untuk badge; kosong -> default "Belum TTE" (preview belum ditandatangani).
     * @param resource  JsonNode resource FHIR; bila Bundle, tiap entry dirender jadi kartu terpisah.
     */
    public static JPanel render(String judul, String statusTte, JsonNode resource) {
        JPanel wadah = new JPanel();
        wadah.setLayout(new BoxLayout(wadah, BoxLayout.Y_AXIS));
        wadah.setOpaque(false);
        wadah.setAlignmentX(Component.LEFT_ALIGNMENT);
        String tte = (statusTte == null || statusTte.trim().equals("")) ? "Belum TTE" : statusTte.trim();
        if (resource == null) {
            wadah.add(kartuPesan(judul, "Tidak ada data untuk dipreview."));
            return wadah;
        }
        if ("Bundle".equals(teks(resource, "resourceType"))) {
            Map<String, JsonNode> idx = indeksBundle(resource);
            JsonNode entry = resource.path("entry");
            if (entry.isArray() && entry.size() > 0) {
                for (JsonNode e : entry) {
                    JsonNode r = e.path("resource");
                    if (r.isMissingNode()) continue;
                    JPanel k = kartuResource(r, idx, judul, tte);
                    k.setAlignmentX(Component.LEFT_ALIGNMENT);
                    wadah.add(k);
                    wadah.add(Box.createVerticalStrut(10));
                }
            } else {
                wadah.add(kartuPesan(judul, "Bundle kosong."));
            }
        } else {
            JPanel k = kartuResource(resource, new HashMap<>(), judul, tte);
            k.setAlignmentX(Component.LEFT_ALIGNMENT);
            wadah.add(k);
        }
        return wadah;
    }

    // ============================ KARTU RESOURCE ============================

    private static JPanel kartuResource(JsonNode r, Map<String, JsonNode> idx, String judulFallback, String tte) {
        String tipe = teks(r, "resourceType");
        JPanel kartu = kartuKosong();

        // --- Header: judul hijau + ID chip ---
        kartu.add(labelJudul(judulKartu(r, tipe, judulFallback)));
        String id = idResource(r);
        if (!id.equals("")) {
            kartu.add(barisChip("ID", id));
        }

        // --- Meta (dibuat oleh / tanggal / status / status TTE) ---
        ResSpec spec = REG.get(specKey(r));
        List<String[]> header = (spec != null) ? spec.header : headerDefault(r);
        for (String[] f : header) addField(kartu, r, f, idx);
        kartu.add(badgeRow("STATUS TTE", tte, tte.toLowerCase().contains("sudah") ? TTE_OK_BG : TTE_BG,
                tte.toLowerCase().contains("sudah") ? TTE_OK_FG : TTE_FG));

        kartu.add(pemisah());

        // --- Body ---
        if ("Encounter".equals(tipe)) {
            bodyEncounter(kartu, r);
        } else if ("Composition".equals(tipe)) {
            bodyComposition(kartu, r, idx);
        } else {
            List<String[]> body = (spec != null) ? spec.body : bodyDefault(r);
            for (String[] f : body) addField(kartu, r, f, idx);
        }
        return kartu;
    }

    /** Encounter: blok Informasi Kunjungan + Histori Lokasi (dengan durasi). */
    private static void bodyEncounter(JPanel kartu, JsonNode r) {
        kartu.add(labelSection("Informasi Kunjungan"));
        addField(kartu, r, f("JENIS RAWAT", "class.display", "badge"), null);
        addField(kartu, r, f("NO. REGISTER FASKES", "identifier[0].value", "text"), null);
        addField(kartu, r, f("PASIEN", "subject.display", "text"), null);
        addField(kartu, r, f("WAKTU MULAI", "period.start", "dateChip"), null);
        addField(kartu, r, f("WAKTU SELESAI", "period.end", "dateChip"), null);
        String durasi = formatDurasi(teks(r, "period.start"), teks(r, "period.end"));
        if (!durasi.equals("")) kartu.add(fieldTeks("TOTAL DURASI", durasi));
        addField(kartu, r, f("DOKTER PENANGGUNG JAWAB", "participant[0].individual.display", "text"), null);
        addField(kartu, r, f("FASILITAS KESEHATAN", "serviceProvider.reference", "text"), null);

        JsonNode lok = r.path("location");
        if (lok.isArray() && lok.size() > 0) {
            kartu.add(labelSection("Histori Lokasi"));
            for (JsonNode l : lok) {
                kartu.add(kartuLokasi(l));
            }
        }
    }

    private static JPanel kartuLokasi(JsonNode l) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(DATE_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS),
                BorderFactory.createEmptyBorder(5, 8, 6, 8)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        String nama = teks(l, "location.display");
        p.add(kiri(labelNilai(nama.equals("") ? "-" : nama)));
        String kelas = kelasLokasi(l);
        if (!kelas.equals("")) p.add(kiri(new Pil(kelas, BADGE_BG, BADGE_FG)));
        String start = teks(l, "period.start"), end = teks(l, "period.end");
        String waktu = formatTanggal(start) + (end.equals("") ? " — (berlangsung)" : " — " + formatTanggal(end));
        if (!start.equals("") || !end.equals("")) p.add(kiri(labelKecil(waktu)));
        String durasi = formatDurasi(start, end);
        if (!durasi.equals("")) p.add(kiri(labelKecil("Durasi: " + durasi)));
        return p;
    }

    /** Composition: meta DOC TYPE + section (sub-judul hijau + chip LOINC + nilai). */
    private static void bodyComposition(JPanel kartu, JsonNode r, Map<String, JsonNode> idx) {
        String docType = teks(r, "type.coding[0].display");
        String docCode = teks(r, "type.coding[0].code");
        if (!docType.equals("") || !docCode.equals("")) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.add(kiri(labelKecil("TIPE DOKUMEN")));
            JPanel line = barisFlow();
            if (!docCode.equals("")) line.add(new Pil(docCode, CHIP_BG, CHIP_FG));
            if (!docType.equals("")) line.add(labelNilai(docType));
            row.add(kiri(line));
            kartu.add(Box.createVerticalStrut(3));
            kartu.add(row);
        }
        addField(kartu, r, f("DIBUAT OLEH", "author[0].display", "text"), null);
        addField(kartu, r, f("TANGGAL DOKUMEN", "date", "dateChip"), null);

        JsonNode section = r.path("section");
        if (section.isArray() && section.size() > 0) {
            for (JsonNode s : section) tampilSection(kartu, s, idx);
        }
    }

    private static void tampilSection(JPanel kartu, JsonNode s, Map<String, JsonNode> idx) {
        String judul = teks(s, "title");
        if (!judul.equals("")) kartu.add(labelSection(judul));
        String kode = teks(s, "code.coding[0].code");
        String disp = teks(s, "code.coding[0].display");
        if (!kode.equals("") || !disp.equals("")) {
            JPanel line = barisFlow();
            if (!kode.equals("")) line.add(new Pil(kode, CHIP_BG, CHIP_FG));
            if (!disp.equals("")) line.add(labelKecil(disp));
            kartu.add(kiri(line));
        }
        String nilai = nilaiSection(s, idx);
        if (!nilai.equals("")) kartu.add(kiri(labelNilai(nilai)));
    }

    /** Nilai section: utamakan section.text.div (di-strip); fallback resolve section.entry ke resource bundle. */
    private static String nilaiSection(JsonNode s, Map<String, JsonNode> idx) {
        String div = stripDiv(teks(s, "text.div"));
        if (!div.equals("") && !div.equals("-")) return div;
        JsonNode entry = s.path("entry");
        if (entry.isArray() && idx != null) {
            List<String> out = new ArrayList<>();
            for (JsonNode e : entry) {
                JsonNode res = resolveRef(idx, teks(e, "reference"));
                if (res != null) {
                    String v = nilaiDariResource(res);
                    if (!v.equals("")) out.add(v);
                }
            }
            if (!out.isEmpty()) return String.join("; ", out);
        }
        return (div.equals("") ? "" : div);
    }

    /** Ambil nilai representatif dari sebuah resource yang dirujuk section.entry. */
    private static String nilaiDariResource(JsonNode res) {
        String t = teks(res, "resourceType");
        if ("Observation".equals(t)) {
            String vs = teks(res, "valueString");
            if (!vs.equals("")) return vs;
            String vq = quantity(res.path("valueQuantity"));
            if (!vq.equals("")) return vq;
            String vc = teks(res, "valueCodeableConcept.coding[0].display");
            if (!vc.equals("")) return vc;
            // component (mis. TD sistol/diastol, ABCDE)
            JsonNode comp = res.path("component");
            if (comp.isArray() && comp.size() > 0) {
                List<String> cs = new ArrayList<>();
                for (JsonNode c : comp) {
                    String cn = teks(c, "code.text");
                    if (cn.equals("")) cn = teks(c, "code.coding[0].display");
                    String cv = teks(c, "valueString");
                    if (cv.equals("")) cv = quantity(c.path("valueQuantity"));
                    if (cv.equals("")) cv = teks(c, "valueCodeableConcept.coding[0].display");
                    if (!cv.equals("")) cs.add((cn.equals("") ? "" : cn + ": ") + cv);
                }
                if (!cs.isEmpty()) return String.join(", ", cs);
            }
            return "";
        }
        // Condition / Procedure / AllergyIntolerance
        String ct = teks(res, "code.text");
        if (!ct.equals("")) return ct;
        return teks(res, "code.coding[0].display");
    }

    // ============================ FIELD RENDER ============================

    private static void addField(JPanel kartu, JsonNode r, String[] fld, Map<String, JsonNode> idx) {
        String label = fld[0], path = fld[1], kind = fld[2];
        String nilai;
        if (path.startsWith("__const:")) {
            nilai = path.substring("__const:".length());
        } else if ("quantity".equals(kind)) {
            JsonNode n = di(r, path);
            nilai = n.isValueNode() ? angka(n.asText()) : quantity(n);
        } else {
            nilai = teks(r, path);
        }
        if (nilai == null || nilai.trim().equals("")) return;
        if ("FASILITAS KESEHATAN".equals(label) || label.startsWith("KUNJUNGAN")
                || label.equals("PASIEN") || label.equals("RESEP TERKAIT")) {
            nilai = stripRef(nilai);
        }
        if ("dateChip".equals(kind)) {
            kartu.add(fieldKomponen(label, new Pil(formatTanggal(nilai), DATE_BG, TEKS, GARIS)));
        } else if ("chip".equals(kind)) {
            kartu.add(fieldKomponen(label, new Pil(nilai, CHIP_BG, CHIP_FG)));
        } else if ("badge".equals(kind)) {
            kartu.add(fieldKomponen(label, new Pil(kapitalAwal(nilai), BADGE_BG, BADGE_FG)));
        } else {
            kartu.add(fieldTeks(label, nilai));
        }
    }

    private static JPanel fieldTeks(String label, String nilai) {
        return fieldKomponen(label, labelNilai(nilai));
    }

    private static JPanel fieldKomponen(String label, Component nilai) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        p.add(kiri(labelKecil(label)));
        p.add(kiri(nilai));
        return p;
    }

    private static JPanel badgeRow(String label, String nilai, Color bg, Color fg) {
        return fieldKomponen(label, new Pil(nilai, bg, fg));
    }

    private static JPanel barisChip(String label, String nilai) {
        JPanel line = barisFlow();
        line.add(labelKecil(label + ":"));
        line.add(new Pil(nilai, CHIP_BG, CHIP_FG));
        return kiri(line);
    }

    // ============================ JUDUL / DISKRIMINATOR ============================

    private static String judulKartu(JsonNode r, String tipe, String fallback) {
        if ("Composition".equals(tipe)) {
            String t = teks(r, "title");
            return t.equals("") ? (fallback == null ? "Composition" : fallback) : t;
        }
        if ("Encounter".equals(tipe)) return "Encounter";
        if ("ServiceRequest".equals(tipe)) {
            String c = teks(r, "code.coding[0].code");
            if (c.equals("spri")) return "Surat Perintah Rawat Inap";
            if (c.equals("185389009")) return "Rencana Kontrol";
            String t = teks(r, "code.text");
            return t.equals("") ? "Permintaan Pemeriksaan" : t;
        }
        ResSpec s = REG.get(specKey(r));
        if (s != null && !s.title.equals("")) return s.title;
        return tipe.equals("") ? (fallback == null ? "Resource" : fallback) : tipe;
    }

    private static String specKey(JsonNode r) {
        String t = teks(r, "resourceType");
        if ("ServiceRequest".equals(t)) {
            String c = teks(r, "code.coding[0].code");
            if (c.equals("spri")) return "SR_SPRI";
            if (c.equals("185389009")) return "SR_KONTROL";
            return "SR_DIAG";
        }
        return t;
    }

    private static String idResource(JsonNode r) {
        String id = teks(r, "id");
        if (!id.equals("")) return id;
        String iv = teks(r, "identifier.value");
        if (!iv.equals("")) return iv;
        return teks(r, "identifier[0].value");
    }

    // ============================ REGISTRI SPEC ============================

    private static final class ResSpec {
        final String title;
        final List<String[]> header;
        final List<String[]> body;
        ResSpec(String title, List<String[]> header, List<String[]> body) {
            this.title = title; this.header = header; this.body = body;
        }
    }

    private static final Map<String, ResSpec> REG = new HashMap<>();

    private static String[] f(String label, String path, String kind) { return new String[]{label, path, kind}; }

    @SafeVarargs
    private static List<String[]> L(String[]... items) {
        List<String[]> l = new ArrayList<>();
        for (String[] i : items) l.add(i);
        return l;
    }

    private static void reg(String key, String title, List<String[]> header, List<String[]> body) {
        REG.put(key, new ResSpec(title, header, body));
    }

    static {
        List<String[]> HDR = L(
                f("DIBUAT OLEH", "recorder.display", "text"),
                f("TANGGAL DOKUMEN", "recordedDate", "dateChip"),
                f("STATUS", "clinicalStatus.coding[0].display", "badge"));
        reg("Condition", "Diagnosa", HDR, L(
                f("DIAGNOSA", "code.coding[0].display", "text"),
                f("KODE ICD-10", "code.coding[0].code", "chip"),
                f("KATEGORI", "category[0].coding[0].display", "text"),
                f("PASIEN", "subject.display", "text"),
                f("TANGGAL DICATAT", "recordedDate", "dateChip")));

        reg("EpisodeOfCare", "Episode Perawatan (ANC)",
                L(f("TANGGAL DOKUMEN", "period.start", "dateChip"), f("STATUS", "status", "badge")),
                L(f("JENIS EPISODE", "type[0].coding[0].display", "text"),
                  f("KODE JENIS", "type[0].coding[0].code", "chip"),
                  f("PASIEN", "patient.display", "text"),
                  f("NOMOR RAWAT", "identifier[0].value", "text"),
                  f("WAKTU MULAI", "period.start", "dateChip")));

        List<String[]> HSR = L(
                f("DIBUAT OLEH", "requester.display", "text"),
                f("TANGGAL DOKUMEN", "authoredOn", "dateChip"),
                f("STATUS", "status", "badge"));
        reg("SR_SPRI", "Surat Perintah Rawat Inap", HSR, L(
                f("JENIS PERMINTAAN", "code.text", "text"),
                f("KATEGORI", "category[0].coding[0].display", "chip"),
                f("INDIKASI RAWAT INAP", "reasonCode[0].text", "text"),
                f("CODING DIAGNOSA MASUK", "reasonCode[0].coding[0].display", "text"),
                f("KODE ICD-10", "reasonCode[0].coding[0].code", "chip"),
                f("NARASI RENCANA TINDAKAN", "orderDetail[0].text", "text"),
                f("PASIEN", "subject.display", "text"),
                f("DOKTER PEMERINTAH", "requester.display", "text"),
                f("RENCANA MULAI RAWAT", "occurrencePeriod.start", "dateChip")));
        reg("SR_KONTROL", "Rencana Kontrol", HSR, L(
                f("JENIS PERMINTAAN", "code.coding[0].display", "text"),
                f("KODE JENIS", "code.coding[0].code", "chip"),
                f("DISPOSISI PULANG", "code.text", "text"),
                f("DIAGNOSA", "reasonCode[0].coding[0].display", "text"),
                f("KODE ICD-10", "reasonCode[0].coding[0].code", "chip"),
                f("PASIEN", "subject.display", "text"),
                f("WAKTU KUNJUNGAN", "occurrenceDateTime", "dateChip"),
                f("INSTRUKSI PASIEN", "patientInstruction", "text")));
        reg("SR_DIAG", "Permintaan Pemeriksaan", HSR, L(
                f("PEMERIKSAAN", "code.coding[0].code", "chip"),
                f("NAMA PEMERIKSAAN", "code.coding[0].display", "text"),
                f("DESKRIPSI", "code.text", "text"),
                f("KATEGORI", "category[0].coding[0].display", "text"),
                f("PRIORITAS", "priority", "badge"),
                f("PASIEN", "subject.display", "text"),
                f("PELAKSANA", "performer[0].display", "text"),
                f("DIAGNOSIS KLINIS", "reasonCode[0].text", "text")));

        List<String[]> HOBAT = L(
                f("DIBUAT OLEH", "requester.display", "text"),
                f("TANGGAL DOKUMEN", "authoredOn", "dateChip"),
                f("STATUS", "status", "badge"));
        reg("MedicationRequest", "Resep Obat", HOBAT, L(
                f("NAMA OBAT", "medicationReference.display", "text"),
                f("KODE KFA", "contained[0].code.coding[0].code", "chip"),
                f("JENIS TERAPI", "courseOfTherapyType.coding[0].display", "badge"),
                f("JENIS RAWAT", "category[0].coding[0].display", "badge"),
                f("ATURAN PAKAI", "dosageInstruction[0].text", "text"),
                f("ATURAN PAKAI (2)", "dosageInstruction[0].patientInstruction", "text"),
                f("RUTE", "dosageInstruction[0].route.coding[0].display", "text"),
                f("DOSIS", "dosageInstruction[0].doseAndRate[0].doseQuantity", "quantity"),
                f("JUMLAH", "dispenseRequest.quantity", "quantity"),
                f("DOKTER PERESEP", "requester.display", "text"),
                f("PASIEN", "subject.display", "text")));
        reg("MedicationStatement", "Pernyataan Penggunaan Obat",
                L(f("DIBUAT OLEH", "informationSource.display", "text"),
                  f("TANGGAL DOKUMEN", "dateAsserted", "dateChip"), f("STATUS", "status", "badge")),
                L(f("NAMA OBAT", "medicationReference.display", "text"),
                  f("KODE KFA", "contained[0].code.coding[0].code", "chip"),
                  f("JENIS RAWAT", "category.coding[0].display", "badge"),
                  f("ATURAN PAKAI", "dosage[0].text", "text"),
                  f("DOSIS", "dosage[0].doseAndRate[0].doseQuantity", "quantity"),
                  f("PASIEN", "subject.display", "text"),
                  f("CATATAN", "note[0].text", "text")));
        reg("MedicationDispense", "Penyerahan Obat",
                L(f("DIBUAT OLEH", "performer[0].actor.display", "text"),
                  f("TANGGAL DOKUMEN", "whenHandedOver", "dateChip"), f("STATUS", "status", "badge")),
                L(f("NAMA OBAT", "medicationReference.display", "text"),
                  f("KODE KFA", "contained[0].code.coding[0].code", "chip"),
                  f("JENIS RAWAT", "category.coding[0].display", "badge"),
                  f("ATURAN PAKAI", "dosageInstruction[0].text", "text"),
                  f("JUMLAH DISERAHKAN", "quantity", "quantity"),
                  f("LOKASI DEPO", "location.display", "text"),
                  f("PETUGAS", "performer[0].actor.display", "text"),
                  f("PASIEN", "subject.display", "text")));
        reg("MedicationAdministration", "Pemberian Obat",
                L(f("DIBUAT OLEH", "performer[0].actor.display", "text"),
                  f("TANGGAL DOKUMEN", "effectiveDateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("NAMA OBAT", "medicationReference.display", "text"),
                  f("KODE KFA", "contained[0].code.coding[0].code", "chip"),
                  f("JENIS RAWAT", "category.coding[0].display", "badge"),
                  f("ATURAN PAKAI", "dosage.text", "text"),
                  f("DOSIS", "dosage.dose", "quantity"),
                  f("PETUGAS PEMBERI", "performer[0].actor.display", "text"),
                  f("PASIEN", "subject.display", "text")));

        reg("Specimen", "Spesimen Laboratorium",
                L(f("TANGGAL DOKUMEN", "receivedTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("JENIS SPESIMEN", "type.coding[0].code", "chip"),
                  f("NAMA SPESIMEN", "type.coding[0].display", "text"),
                  f("KETERANGAN", "type.text", "text"),
                  f("PASIEN", "subject.display", "text")));
        reg("Observation", "Hasil Pemeriksaan (Observation)",
                L(f("DIBUAT OLEH", "performer[0].display", "text"),
                  f("TANGGAL DOKUMEN", "effectiveDateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PEMERIKSAAN", "code.coding[0].code", "chip"),
                  f("NAMA PEMERIKSAAN", "code.coding[0].display", "text"),
                  f("NILAI (TEKS)", "valueString", "text"),
                  f("NILAI (NUMERIK)", "valueQuantity", "quantity"),
                  f("NILAI (KODE)", "valueCodeableConcept.coding[0].display", "text"),
                  f("STATUS FISIK ASA", "component[0].valueCodeableConcept.coding[0].code", "chip"),
                  f("HASIL ASA", "component[0].valueCodeableConcept.coding[0].display", "text"),
                  f("NILAI RUJUKAN", "referenceRange[0].text", "text"),
                  f("CATATAN", "note[0].text", "text"),
                  f("PASIEN", "subject.display", "text")));
        reg("DiagnosticReport", "Laporan Hasil Diagnostik",
                L(f("TANGGAL DOKUMEN", "effectiveDateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PEMERIKSAAN", "code.coding[0].code", "chip"),
                  f("NAMA PEMERIKSAAN", "code.coding[0].display", "text"),
                  f("KATEGORI", "category[0].coding[0].display", "text"),
                  f("KESIMPULAN", "conclusion", "text"),
                  f("WAKTU TERBIT", "issued", "dateChip")));

        reg("QuestionnaireResponse", "Kuesioner / Telaah",
                L(f("DIBUAT OLEH", "author.display", "text"),
                  f("TANGGAL DOKUMEN", "authored", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "subject.display", "text"),
                  f("KRITERIA TERDUGA TB", "item[0].item[0].answer[0].valueCoding.code", "chip"),
                  f("HASIL KLASIFIKASI", "item[0].item[0].answer[0].valueCoding.display", "text"),
                  f("IDENTIFIKASI PASIEN", "item[0].item[0].answer[0].valueCoding.display", "text"),
                  f("KUESIONER", "questionnaire", "text")));
        reg("ClinicalImpression", "Penilaian Klinis",
                L(f("DIBUAT OLEH", "assessor.display", "text"),
                  f("TANGGAL DOKUMEN", "date", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "subject.display", "text"),
                  f("DESKRIPSI", "description", "text"),
                  f("RINGKASAN", "summary", "text"),
                  f("PROGNOSIS (SNOMED)", "prognosisCodeableConcept[0].coding[0].code", "chip"),
                  f("HASIL PROGNOSIS", "prognosisCodeableConcept[0].coding[0].display", "text"),
                  f("PROGNOSIS/RISIKO", "prognosisCodeableConcept[0].text", "text")));
        reg("RiskAssessment", "Penilaian Risiko Jatuh",
                L(f("TANGGAL DOKUMEN", "occurrenceDateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "subject.display", "text"),
                  f("JENIS PENILAIAN", "code.coding[0].code", "chip"),
                  f("METODE", "method.text", "text"),
                  f("TINGKAT RISIKO", "prediction[0].qualitativeRisk.coding[0].display", "text"),
                  f("SKOR", "prediction[0].rationale", "text")));
        reg("Goal", "Tujuan / Rencana Tindak Lanjut",
                L(f("DIBUAT OLEH", "expressedBy.display", "text"),
                  f("TANGGAL DOKUMEN", "startDate", "dateChip"), f("STATUS", "lifecycleStatus", "badge")),
                L(f("PASIEN", "subject.display", "text"),
                  f("TUJUAN / DESKRIPSI", "description.text", "text"),
                  f("STATUS PENCAPAIAN", "achievementStatus.coding[0].display", "chip")));
        reg("NutritionOrder", "Pemberian Diet",
                L(f("DIBUAT OLEH", "orderer.display", "text"),
                  f("TANGGAL DOKUMEN", "dateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "patient.display", "text"),
                  f("JENIS DIET (SNOMED)", "oralDiet.type[0].coding[0].code", "chip"),
                  f("NAMA DIET", "oralDiet.type[0].text", "text"),
                  f("WAKTU MAKAN", "oralDiet.schedule[0].repeat.timeOfDay[0]", "text"),
                  f("INSTRUKSI", "oralDiet.instruction", "text")));
        reg("FamilyMemberHistory", "Riwayat Penyakit Keluarga",
                L(f("STATUS", "status", "badge")),
                L(f("HUBUNGAN (SNOMED)", "relationship.coding[0].code", "chip"),
                  f("HUBUNGAN KELUARGA", "relationship.text", "text"),
                  f("RIWAYAT PENYAKIT KELUARGA", "condition[0].code.text", "text")));

        reg("Device", "Alat Kesehatan (Device)",
                L(f("STATUS", "status", "badge")),
                L(f("NAMA ALAT", "type.text", "text"),
                  f("KODE KFA", "type.coding[0].code", "chip"),
                  f("NAMA PERANGKAT", "deviceName[0].name", "text")));
        reg("DeviceRequest", "Permintaan Alat Kesehatan",
                L(f("DIBUAT OLEH", "requester.display", "text"),
                  f("TANGGAL DOKUMEN", "authoredOn", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "subject.display", "text"),
                  f("ALAT DIMINTA", "codeReference.display", "text")));
        reg("DeviceUseStatement", "Penggunaan Alat Kesehatan",
                L(f("TANGGAL DOKUMEN", "recordedOn", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "subject.display", "text"),
                  f("ALAT DIGUNAKAN", "device.display", "text"),
                  f("WAKTU PENGGUNAAN", "timingDateTime", "dateChip")));
        reg("SupplyRequest", "Permintaan Suplai Alkes",
                L(f("DIBUAT OLEH", "requester.display", "text"),
                  f("TANGGAL DOKUMEN", "occurrenceDateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("ITEM", "itemReference.display", "text"),
                  f("JUMLAH", "quantity.value", "quantity")));
        reg("SupplyDelivery", "Pengiriman Suplai Alkes",
                L(f("TANGGAL DOKUMEN", "occurrenceDateTime", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "patient.display", "text"),
                  f("ITEM DIKIRIM", "suppliedItem.itemReference.display", "text"),
                  f("JUMLAH", "suppliedItem.quantity.value", "quantity")));

        reg("Coverage", "Jaminan / Penjamin (BPJS)",
                L(f("STATUS", "status", "badge")),
                L(f("NO PESERTA", "subscriberId", "text"),
                  f("PENJAMIN", "payor[0].display", "badge")));
        reg("Account", "Akun Tagihan Pasien",
                L(f("DIBUAT OLEH", "owner.display", "text"),
                  f("TANGGAL DOKUMEN", "servicePeriod.start", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PASIEN", "subject[0].display", "text"),
                  f("NAMA TAGIHAN", "name", "text"),
                  f("JENIS AKUN", "type.coding[0].code", "chip"),
                  f("PERIODE LAYANAN (SELESAI)", "servicePeriod.end", "dateChip")));
        reg("ChargeItem", "Rincian Biaya",
                L(f("DIBUAT OLEH", "performer[0].actor.display", "text"),
                  f("TANGGAL DOKUMEN", "occurrencePeriod.start", "dateChip"), f("STATUS", "status", "badge")),
                L(f("TINDAKAN / OBAT", "code.text", "text"),
                  f("KODE", "code.coding[0].code", "chip"),
                  f("JUMLAH", "quantity.value", "quantity"),
                  f("BIAYA (IDR)", "extension[1].valueMoney.value", "quantity")));
        reg("Invoice", "Rekap Tagihan (Invoice)",
                L(f("DIBUAT OLEH", "issuer.display", "text"),
                  f("TANGGAL DOKUMEN", "date", "dateChip"), f("STATUS", "status", "badge")),
                L(f("PENERBIT", "issuer.display", "text"),
                  f("TOTAL BERSIH (IDR)", "totalNet.value", "quantity"),
                  f("TOTAL KOTOR (IDR)", "totalGross.value", "quantity")));
    }

    private static List<String[]> headerDefault(JsonNode r) {
        return L(f("STATUS", "status", "badge"));
    }

    private static List<String[]> bodyDefault(JsonNode r) {
        return L(f("KODE", "code.text", "text"),
                 f("PASIEN", "subject.display", "text"));
    }

    // ============================ UTIL JSON ============================

    /** Traversal path "a.b[0].c" aman (mengembalikan MissingNode bila tak ada). */
    private static JsonNode di(JsonNode n, String path) {
        if (n == null || path == null || path.equals("")) return com.fasterxml.jackson.databind.node.MissingNode.getInstance();
        JsonNode cur = n;
        for (String tokRaw : path.split("\\.")) {
            String tok = tokRaw;
            // nama field sebelum kurung siku
            int lb = tok.indexOf('[');
            String name = (lb < 0) ? tok : tok.substring(0, lb);
            if (!name.equals("")) cur = cur.path(name);
            // indeks berturut [i][j]
            while (lb >= 0) {
                int rb = tok.indexOf(']', lb);
                if (rb < 0) break;
                String idxs = tok.substring(lb + 1, rb);
                try { cur = cur.path(Integer.parseInt(idxs.trim())); } catch (Exception e) { cur = com.fasterxml.jackson.databind.node.MissingNode.getInstance(); }
                lb = tok.indexOf('[', rb);
            }
        }
        return cur;
    }

    private static String teks(JsonNode n, String path) {
        JsonNode v = di(n, path);
        return (v == null || v.isMissingNode() || v.isNull()) ? "" : v.asText();
    }

    private static String quantity(JsonNode q) {
        if (q == null || q.isMissingNode() || q.isNull()) return "";
        String v = q.path("value").isMissingNode() ? "" : q.path("value").asText();
        String u = q.path("unit").isMissingNode() ? "" : q.path("unit").asText();
        if (v.equals("")) return "";
        return angka(v) + (u.equals("") ? "" : " " + u);
    }

    /** Index Bundle.entry -> resource, keyed fullUrl DAN "Type/id" (untuk resolusi referensi section). */
    private static Map<String, JsonNode> indeksBundle(JsonNode bundle) {
        Map<String, JsonNode> idx = new HashMap<>();
        JsonNode entry = bundle.path("entry");
        if (entry.isArray()) {
            for (JsonNode e : entry) {
                JsonNode res = e.path("resource");
                if (res.isMissingNode()) continue;
                String full = teks(e, "fullUrl");
                if (!full.equals("")) idx.put(full, res);
                String tipe = teks(res, "resourceType");
                String id = teks(res, "id");
                if (!tipe.equals("") && !id.equals("")) idx.put(tipe + "/" + id, res);
            }
        }
        return idx;
    }

    private static JsonNode resolveRef(Map<String, JsonNode> idx, String ref) {
        if (idx == null || ref == null || ref.equals("")) return null;
        return idx.get(ref);
    }

    // ============================ FORMAT ============================

    private static final String[] BULAN = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    /** ISO -> "13 Apr 2026 11:20" (atau tanpa jam bila sumber date-only). */
    private static String formatTanggal(String iso) {
        if (iso == null || iso.trim().equals("")) return "";
        String s = iso.trim();
        try {
            String tgl = s.length() >= 10 ? s.substring(0, 10) : s;
            String[] p = tgl.split("-");
            if (p.length < 3) return s;
            int th = Integer.parseInt(p[0]);
            int bl = Integer.parseInt(p[1]);
            int hr = Integer.parseInt(p[2]);
            String out = hr + " " + BULAN[Math.max(0, Math.min(11, bl - 1))] + " " + th;
            int ti = s.indexOf('T');
            if (ti > 0 && s.length() >= ti + 6) {
                out += " " + s.substring(ti + 1, ti + 6);   // HH:mm
            }
            return out;
        } catch (Exception e) {
            return s;
        }
    }

    /** Selisih dua ISO datetime -> "X hari Y jam" / "Y jam Z menit" / "Z menit". */
    private static String formatDurasi(String start, String end) {
        java.time.OffsetDateTime a = parseDt(start), b = parseDt(end);
        if (a == null || b == null) return "";
        long menit = java.time.Duration.between(a, b).toMinutes();
        if (menit < 0) menit = 0;
        long hari = menit / (60 * 24);
        long sisa = menit % (60 * 24);
        long jam = sisa / 60;
        long mnt = sisa % 60;
        if (hari > 0) return hari + " hari " + jam + " jam";
        if (jam > 0) return jam + " jam " + mnt + " menit";
        return mnt + " menit";
    }

    private static java.time.OffsetDateTime parseDt(String s) {
        if (s == null || s.trim().equals("")) return null;
        String t = s.trim();
        try {
            return java.time.OffsetDateTime.parse(t);
        } catch (Exception ignore) { }
        try {
            return java.time.LocalDateTime.parse(t).atOffset(java.time.ZoneOffset.ofHours(7));
        } catch (Exception ignore) { }
        try {
            return java.time.LocalDate.parse(t.length() >= 10 ? t.substring(0, 10) : t)
                    .atStartOfDay().atOffset(java.time.ZoneOffset.ofHours(7));
        } catch (Exception ignore) { }
        return null;
    }

    /** Format angka: bilangan bulat pakai pemisah ribuan (untuk rupiah/kuantitas). */
    private static String angka(String v) {
        if (v == null || v.equals("")) return "";
        try {
            double d = Double.parseDouble(v);
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.format("%,d", (long) d);
            }
            return String.format("%,.2f", d);
        } catch (Exception e) {
            return v;
        }
    }

    /** Buang "Type/" dari sebuah reference agar tampil ringkas. */
    private static String stripRef(String s) {
        if (s == null) return "";
        int i = s.indexOf('/');
        return (i >= 0 && s.startsWith("urn:") == false && s.indexOf(' ') < 0 && i < 40) ? s.substring(i + 1) : s;
    }

    /** Buang wrapper &lt;div ...&gt; ... &lt;/div&gt; + unescape entitas dasar. */
    private static String stripDiv(String s) {
        if (s == null) return "";
        String t = s.replaceAll("(?is)<div[^>]*>", "").replaceAll("(?is)</div>", "")
                .replaceAll("(?is)<br\\s*/?>", " ").replaceAll("(?is)<[^>]+>", " ").trim();
        t = t.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ");
        return t.replaceAll("\\s+", " ").trim();
    }

    private static String kapitalAwal(String s) {
        if (s == null || s.equals("")) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** KELAS LAYANAN dari extension ServiceClass pada satu Encounter.location. */
    private static String kelasLokasi(JsonNode locItem) {
        JsonNode ext = locItem.path("extension");
        if (!ext.isArray()) return "";
        for (JsonNode e : ext) {
            if (teks(e, "url").endsWith("ServiceClass")) {
                JsonNode inner = e.path("extension");
                if (inner.isArray()) {
                    for (JsonNode i : inner) {
                        if ("value".equals(teks(i, "url"))) {
                            return teks(i, "valueCodeableConcept.coding[0].display");
                        }
                    }
                }
            }
        }
        return "";
    }

    // ============================ UI PRIMITIF ============================

    private static JPanel kartuKosong() {
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        kartu.setAlignmentX(Component.LEFT_ALIGNMENT);
        kartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return kartu;
    }

    private static JPanel kartuPesan(String judul, String pesan) {
        JPanel kartu = kartuKosong();
        kartu.add(labelJudul(judul == null ? "Preview" : judul));
        kartu.add(kiri(labelNilai(pesan)));
        return kartu;
    }

    private static JLabel labelJudul(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_JUDUL);
        l.setForeground(HIJAU);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private static JLabel labelSection(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_SECTION);
        l.setForeground(HIJAU);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 3, 0));
        return l;
    }

    private static JLabel labelKecil(String t) {
        JLabel l = new JLabel(t.toUpperCase());
        l.setFont(F_LABEL);
        l.setForeground(LABEL);
        return l;
    }

    private static JLabel labelNilai(String t) {
        JLabel l = new JLabel("<html><div style='width:" + LEBAR + "px'>" + escape(t) + "</div></html>");
        l.setFont(F_NILAI);
        l.setForeground(TEKS);
        return l;
    }

    private static JPanel pemisah() {
        JPanel p = new JPanel();
        p.setBackground(GARIS);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(10, 1));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        return p;
    }

    private static JPanel barisFlow() {
        JPanel p = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 1));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    /** Bungkus komponen agar rata kiri di dalam BoxLayout Y. */
    private static JPanel kiri(Component c) {
        JPanel p = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(c);
        return p;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Chip/badge rounded (pil). */
    private static final class Pil extends JLabel {
        private final Color bg;
        private final Color garis;
        Pil(String t, Color bg, Color fg) { this(t, bg, fg, null); }
        Pil(String t, Color bg, Color fg, Color garis) {
            super(t == null ? "" : t);
            this.bg = bg;
            this.garis = garis;
            setForeground(fg);
            setFont(F_CHIP);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 8, 3, 8));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = getHeight();
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (garis != null) {
                g2.setColor(garis);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
