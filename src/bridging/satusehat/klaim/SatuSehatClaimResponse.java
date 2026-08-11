/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

//import bridging.SatuSehatWebhookEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * PEMBACA ClaimResponse (langkah 8 "Purifikasi" & 10 "Verifikasi" pada alur Klaim BPJS-K).
 *
 * ClaimResponse DIKIRIM OLEH BPJS-K, bukan oleh fasyankes — RS hanya menerimanya.
 * Karena itu class ini murni GET + simpan; TIDAK ADA method kirim di sini. Bukti bahwa
 * resource ini milik BPJS: `identifier.system` memakai `claim-number/{Org_BPJS}`. Ketika
 * RS yang mengirim (CoverageEligibilityResponse), namespace itu DITOLAK server dengan
 * "Wrong organization ID provided by system URL" (RuleNumber 10200) dan harus diganti
 * Organization RS — lih. SatuSehatBilling.buatCoverageEligibilityResponse().
 *
 * Prinsip sama dengan TTE: webhook = PEMICU saja, GET = SUMBER KEBENARAN. Payload webhook
 * bisa mengaku sesuatu yang tidak sesuai keadaan server, jadi status yang disimpan selalu
 * hasil GET.
 *
 * Kunci pencarian = No_SEP (dari bridging_sep), sehingga RS tidak perlu tahu Claim_id
 * milik e-Klaim.
 */
public class SatuSehatClaimResponse {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    /** Jeda antar-request pada sinkronisasi massal: SATUSEHAT membalas 429 bila diberondong. */
    private static final long JEDA_MS = 350;

    public SatuSehatClaimResponse() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse : " + e);
        }
    }

    /** Hasil satu kali sinkronisasi, supaya pemanggil (menu) bisa menampilkan ringkasan. */
    public static class Hasil {
        public String noRawat = "", noSep = "", noBatch = "";
        public boolean adaPurifikasi = false, adaVerifikasi = false;
        public String statusPurifikasi = "", statusVerifikasi = "", kodeVerifikasi = "";
        // Hanya terisi pada fase verifikasi — purifikasi tidak membawa angka.
        public double nilaiDiajukan = 0, nilaiDisetujui = 0, nilaiCopay = 0, nilaiBayar = 0;
        public String tglBayar = "";
        public String catatan = "";

        public boolean adaRespons() {
            return adaPurifikasi || adaVerifikasi;
        }

        /** Selisih pengajuan vs persetujuan BPJS (positif = tidak semua disetujui). */
        public double selisih() {
            return nilaiDiajukan - nilaiDisetujui;
        }
    }

    // ====================== API PUBLIK ======================

    /**
     * Tarik ClaimResponse untuk satu kunjungan lalu simpan ke `satu_sehat_claimresponse`.
     * Aman dipanggil berulang (upsert per fase). Tidak melempar exception.
     */
    public Hasil sinkron(String noRawat) {
        Hasil h = new Hasil();
        h.noRawat = noRawat;
//        String orgBpjs = nz(koneksiDB.IDORGBPJSSATUSEHAT()).trim();
//        if (orgBpjs.equals("")) {
//            h.catatan = "IDORGBPJSSATUSEHAT kosong di setting/database.xml";
//            System.out.println("ClaimResponse " + noRawat + " : " + h.catatan);
//            return h;
//        }
        h.noSep = ambilNoSep(noRawat);
        if (h.noSep.equals("")) {
            h.catatan = "SEP tidak ditemukan";
            return h;
        }
//        JsonNode hasil = cariClaimResponse(h.noSep, orgBpjs);
//        if (hasil == null) {
//            h.catatan = "gagal/kosong dari server";
//            return h;
//        }
//        for (JsonNode cr : kumpulkanResource(hasil)) {
//            String subType = nz(cr.path("subType").path("coding").path(0).path("code").asText()).trim();
//            String id = nz(cr.path("id").asText()).trim();
//            String status = bacaStatus(cr);
//            String disposition = nz(cr.path("disposition").asText()).trim();
//            String raw = cr.toString();
//            String batch = bacaIdentifier(cr, "claim-batch-number");
//            if (!batch.equals("")) h.noBatch = batch;
//            if (fasaVerifikasi(subType, cr)) {
//                h.kodeVerifikasi = bacaKodeStatus(cr);
//                h.nilaiDiajukan = bacaTotal(cr, "submitted");
//                h.nilaiDisetujui = bacaTotal(cr, "benefit");
//                h.nilaiCopay = bacaCopay(cr);
//                h.nilaiBayar = cr.path("payment").path("amount").path("value").asDouble(0);
//                h.tglBayar = nz(cr.path("payment").path("date").asText()).trim();
//                simpanVerifikasi(noRawat, id, status, disposition, raw, batch, h);
//                h.adaVerifikasi = true;
//                h.statusVerifikasi = status;
//            } else {
                // Default purifikasi: subType kosong pada langkah 8 tetap diperlakukan purifikasi.
                // Fase ini tidak membawa angka — kolom nilai_* sengaja tidak disentuh.
                // (Lihat fasaVerifikasi: justru ketiadaan angka itulah pembeda strukturalnya.)
//                simpanPurifikasi(noRawat, id, status, disposition, raw, batch);
//                h.adaPurifikasi = true;
//                h.statusPurifikasi = status;
//            }
//        }
        if (!h.adaRespons()) h.catatan = "belum ada ClaimResponse untuk SEP " + h.noSep;
        StringBuilder log = new StringBuilder("ClaimResponse " + noRawat + " (SEP " + h.noSep + ") : "
                + "purifikasi=" + (h.adaPurifikasi ? h.statusPurifikasi : "-")
                + " verifikasi=" + (h.adaVerifikasi ? h.statusVerifikasi : "-"));
        if (h.adaVerifikasi) {
            log.append(String.format(" | diajukan=%.0f disetujui=%.0f selisih=%.0f copay=%.0f bayar=%.0f (%s)",
                    h.nilaiDiajukan, h.nilaiDisetujui, h.selisih(), h.nilaiCopay, h.nilaiBayar,
                    h.tglBayar.equals("") ? "-" : h.tglBayar));
        }
        if (!h.catatan.equals("")) log.append(" [").append(h.catatan).append("]");
        System.out.println(log.toString());
        return h;
    }

    /**
     * Sinkronisasi massal berdasar rentang tanggal SEP. Dipakai menu/penjadwalan:
     * hasil purifikasi & verifikasi datang berhari-hari setelah klaim diajukan, jadi
     * tidak masuk akal menempelkannya di alur kirim Encounter.
     */
    public List<Hasil> sinkronPeriode(String tglAwal, String tglAkhir) {
        List<Hasil> semua = new ArrayList<>();
        List<String> daftar = new ArrayList<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select distinct no_rawat from bridging_sep "
                    + "where tglsep between ? and ? order by tglsep");
            p.setString(1, tglAwal);
            p.setString(2, tglAkhir);
            ResultSet r = p.executeQuery();
            while (r.next()) daftar.add(nz(r.getString("no_rawat")));
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse sinkronPeriode : " + e);
            return semua;
        }
        System.out.println("ClaimResponse: sinkronisasi " + daftar.size() + " SEP (" + tglAwal + " s/d " + tglAkhir + ")");
        for (String noRawat : daftar) {
            semua.add(sinkron(noRawat));
            try {
                Thread.sleep(JEDA_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return semua;
    }

    // ====================== PEMICU WEBHOOK ======================

    /**
     * Ringkasan satu putaran {@link #sinkronDariWebhook(int)}.
     *
     * `gema` dipisah dari `dipicu` karena dua-duanya "berhasil" tapi artinya beda jauh:
     * gema = pantulan kiriman KITA sendiri (mis. coverageEligibilityResponseSubmission),
     * tidak ada yang perlu ditarik; dipicu = benar-benar ada kabar dari BPJS-K.
     */
    public static class HasilWebhook {
        public int sinyal = 0, gema = 0, dipicu = 0, adaRespons = 0;
        public int tanpaSep = 0, sepAsing = 0, takDikenal = 0;
        public final List<Hasil> hasil = new ArrayList<>();

        public String ringkas() {
            return "webhook: " + sinyal + " sinyal, " + dipicu + " klaim ditarik ("
                    + adaRespons + " punya ClaimResponse), " + gema + " gema kiriman sendiri"
                    + (tanpaSep > 0 ? ", " + tanpaSep + " tanpa No.SEP" : "")
                    + (sepAsing > 0 ? ", " + sepAsing + " SEP tak dikenal" : "")
                    + (takDikenal > 0 ? ", " + takDikenal + " bentuk payload tak dikenali (cek log)" : "") + ".";
        }
    }

    /**
     * Tindak lanjuti sinyal webhook Klaim BPJS-K (langkah 8 &amp; 10 pada diagram).
     *
     * Webhook = PEMICU SAJA. Payloadnya memang membawa resource lengkap, tapi yang disimpan
     * tetap hasil GET — persis prinsip yang dipakai TTE, dan alasannya sama: payload bisa
     * mendahului/berbeda dari keadaan server. Yang diambil dari payload cuma satu: No.SEP,
     * sekadar untuk tahu klaim mana yang perlu ditarik.
     *
     * Baris ditandai `diproses=1` SETELAH sinkron selesai, sehingga kegagalan di tengah
     * jalan aman diulang pada putaran berikutnya. Gema kiriman sendiri ikut ditandai selesai
     * supaya antrean tidak menumpuk — hanya notifikasi TTE yang dibiarkan utuh untuk
     * DlgTTESatuSehat (lihat SatuSehatWebhookEvent).
     *
     * @param maksimal batas baris per putaran (jaga-jaga bila webhook membanjir)
     */
    public HasilWebhook sinkronDariWebhook(int maksimal) {
        HasilWebhook w = new HasilWebhook();
        if (maksimal <= 0) return w;

        List<String> payload = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id, ifnull(payload,'') as payload from satu_sehat_task_webhook "
//                + "where diproses=0 and " + SatuSehatWebhookEvent.klausaBukanTte("payload") + " "
                + "where diproses=0 "
                + "order by id asc limit ?")) {
            p.setInt(1, maksimal);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    ids.add(r.getLong("id"));
                    payload.add(nz(r.getString("payload")));
                }
            }
        } catch (Exception e) {
            // Lazimnya: tabel belum ada karena penerima webhook belum dipasang.
            System.out.println("Notifikasi ClaimResponse ambil sinyal webhook : " + e);
            return w;
        }
        w.sinyal = ids.size();
        if (w.sinyal == 0) return w;

        List<Long> selesai = new ArrayList<>();
        java.util.Set<String> sudah = new java.util.HashSet<>();   // 1 kunjungan cukup 1 GET per putaran
        for (int i = 0; i < ids.size(); i++) {
            long id = ids.get(i);
            try {
//                if (SatuSehatWebhookEvent.milikTte(payload.get(i))) {
                    // Jaring pengaman bila penyaring SQL meleset: JANGAN ditandai selesai,
                    // itu jatah DlgTTESatuSehat.
//                    continue;
//                }
                JsonNode akar = mapper.readTree(payload.get(i));
                JsonNode fhir = cariFhir(akar);
                String jenis = nz(fhir.path("resourceType").asText()).trim();
                String metode = metodeWebhook(akar);
                String noSep = bacaIdentifier(fhir, "claim-number");

                // Bentuk payload di luar dugaan (resourceType & method dua-duanya tak terbaca).
                // Sampai 27 Juli 2026 ClaimResponse asli dari BPJS-K BELUM pernah masuk, jadi
                // bentuknya cuma dugaan. Kalau nanti berbeda, kasus ini TIDAK BOLEH diam-diam
                // terhitung gema — dicatat keras, dan tetap ditarik bila membawa No.SEP.
                boolean takDikenal = jenis.equals("") && metode.equals("");
                if (takDikenal) {
                    w.takDikenal++;
                    System.out.println("Webhook klaim #" + id + " : bentuk payload tak dikenali"
                            + (noSep.equals("")
                                    ? " dan tanpa claim-number — periksa manual di satu_sehat_task_webhook."
                                    : " — tetap ditarik karena membawa SEP " + noSep + "."));
                } else if (!perluDitarik(jenis, metode)) {
                    w.gema++;
                    selesai.add(id);
                    continue;
                }
                if (noSep.equals("")) {
                    w.tanpaSep++;
                    System.out.println("Webhook klaim #" + id + " (" + jenis + "/" + metode
                            + ") tanpa identifier claim-number — dilewati, payload tersimpan di tabel.");
                    selesai.add(id);
                    continue;
                }
                String noRawat = noRawatDariSep(noSep);
                if (noRawat.equals("")) {
                    w.sepAsing++;
                    System.out.println("Webhook klaim #" + id + " : SEP " + noSep
                            + " tidak ada di bridging_sep — bukan pasien RS ini?");
                    selesai.add(id);
                    continue;
                }
                if (sudah.add(noRawat)) {
                    Hasil h = sinkron(noRawat);            // GET = sumber kebenaran
                    w.hasil.add(h);
                    w.dipicu++;
                    if (h.adaRespons()) w.adaRespons++;
                    try {
                        Thread.sleep(JEDA_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                selesai.add(id);
            } catch (Exception e) {
                // JANGAN ditandai selesai -> dicoba lagi putaran berikutnya.
                System.out.println("Notifikasi ClaimResponse webhook #" + id + " : " + e);
            }
        }
        tandaiSinyalDiproses(selesai);
        System.out.println("ClaimResponse " + w.ringkas());
        return w;
    }

    /**
     * Perlu GET atau tidak.
     *
     * Server memantulkan kiriman kita sendiri lewat kanal webhook yang sama (nyata:
     * `coverageEligibilityResponseSubmission`, langkah 1). Pantulan itu tidak membawa
     * kabar baru dari BPJS-K, jadi menariknya cuma menghabiskan kuota permintaan.
     * Yang ditarik hanya bila resource-nya ClaimResponse atau metodenya menyebut
     * claimResponse/purification.
     */
    private boolean perluDitarik(String resourceType, String metode) {
        if (resourceType.equalsIgnoreCase("ClaimResponse")) return true;
        String m = metode.toLowerCase();
        return m.contains("claimresponse") || m.contains("purification");
    }

    /** `data.meta.method` — penanda operasi, mis. coverageEligibilityResponseSubmission. */
    private String metodeWebhook(JsonNode root) {
        String m = nz(root.path("data").path("meta").path("method").asText()).trim();
        if (m.equals("")) m = nz(root.path("meta").path("method").asText()).trim();
        return m;
    }

    /**
     * Cari node resource FHIR di dalam payload webhook. Bentuknya berlapis dan tidak
     * dijamin sama antar-event, jadi dicoba beberapa jalur — sama seperti penerima PHP.
     */
    private JsonNode cariFhir(JsonNode root) {
        String[][] jalur = {{"data", "data", "fhir"}, {"data", "fhir"}, {"fhir"}, {}};
        for (String[] j : jalur) {
            JsonNode n = root;
            for (String bagian : j) {
                n = n.path(bagian);
            }
            if (n.isObject() && n.has("resourceType")) return n;
        }
        return mapper.createObjectNode();
    }

    private String noRawatDariSep(String noSep) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select no_rawat from bridging_sep where no_sep=? limit 1")) {
            p.setString(1, noSep);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return nz(r.getString("no_rawat")).trim();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse noRawatDariSep : " + e);
        }
        return "";
    }

    private void tandaiSinyalDiproses(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        StringBuilder tanya = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            tanya.append(i == 0 ? "?" : ",?");
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "update satu_sehat_task_webhook set diproses=1 where id in (" + tanya + ")")) {
            for (int i = 0; i < ids.size(); i++) {
                p.setLong(i + 1, ids.get(i));
            }
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse tandaiSinyalDiproses : " + e);
        }
    }

    /** Berapa sinyal klaim yang menunggu — untuk memberi tahu operator tanpa menarik apa pun. */
    public int jumlahSinyalMenunggu() {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select count(*) from satu_sehat_task_webhook "
//                + "where diproses=0 and " + SatuSehatWebhookEvent.klausaBukanTte("payload"));
                + "where diproses=0 ");
                ResultSet r = p.executeQuery()) {
            if (r.next()) return r.getInt(1);
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse jumlahSinyalMenunggu : " + e);
        }
        return 0;
    }

    // ====================== AMBIL DARI SERVER ======================

    /** GET /ClaimResponse?identifier={claim-number/{orgBPJS}}|{noSep}. null bila gagal. */
    private JsonNode cariClaimResponse(String noSep, String orgBpjs) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity<String> req = new HttpEntity<>(h);
            // String URL (bukan URI.create) supaya '|' di-encode RestTemplate, bukan ditolak.
            String url = link + "/ClaimResponse?identifier="
                    + "http://sys-ids.kemkes.go.id/claim-number/" + orgBpjs + "|" + noSep;
            String body = api.getRest().exchange(url, HttpMethod.GET, req, String.class).getBody();
            return mapper.readTree(body);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("ClaimResponse GET " + noSep + " : " + ex.getStatusCode()
                    + " => " + ex.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse GET : " + e);
            return null;
        }
    }

    /** Ratakan hasil GET jadi daftar ClaimResponse, baik dibungkus searchset Bundle maupun telanjang. */
    private List<JsonNode> kumpulkanResource(JsonNode root) {
        List<JsonNode> out = new ArrayList<>();
        if (nz(root.path("resourceType").asText()).equals("ClaimResponse")) {
            out.add(root);
            return out;
        }
        for (JsonNode e : root.path("entry")) {
            JsonNode res = e.path("resource");
            if (nz(res.path("resourceType").asText()).equals("ClaimResponse")) out.add(res);
        }
        return out;
    }

    /**
     * Status purifikasi/verifikasi ada di `adjudication[].reason.coding.display`
     * (purifikasi: TK000047 "Lolos Purifikasi"; verifikasi: CRA000001 "Layak"),
     * BUKAN di field `status`.
     *
     * `status` bernilai "active" — itu status resource FHIR, bukan hasil purifikasi.
     * Mengisi kolom status_* dari sana membuat kolomnya tak berguna.
     *
     * Kedua fase memakai CodeSystem BERBEDA (purifikasi `terminology.kemkes.go.id`,
     * verifikasi `.../CodeSystem/claimresponse-adjudication`) dan category berbeda pula
     * (TK000046 vs SNOMED 309010003), jadi pembacaan sengaja tidak mengunci system/category
     * mana pun — cukup ambil reason pertama yang terisi.
     */
    private String bacaStatus(JsonNode cr) {
        for (JsonNode adj : cr.path("adjudication")) {
            String display = nz(adj.path("reason").path("coding").path(0).path("display").asText()).trim();
            if (!display.equals("")) return display;
            String code = nz(adj.path("reason").path("coding").path(0).path("code").asText()).trim();
            if (!code.equals("")) return code;
        }
        // Cadangan: outcome (queued/complete/error/partial) bila adjudication tak memuat reason.
        return nz(cr.path("outcome").asText()).trim();
    }

    /**
     * Fase VERIFIKASI (langkah 10) atau PURIFIKASI (langkah 8)?
     *
     * `subType` tidak boleh jadi satu-satunya andalan. Diagram alur resmi menamai resource
     * langkah 10 "ClaimResponse (BAHV)" — Berita Acara Hasil Verifikasi — sedangkan kode ini
     * semula hanya menerima persis "verifikasi". Kalau BPJS-K memakai istilah BAHV, hasil
     * verifikasi akan tergolong purifikasi dan kolom nilai_* TIDAK PERNAH terisi; padahal
     * angka itulah inti langkah 10 (yang diajukan, disetujui, copay, dibayar).
     *
     * Dua lapis, dan lapis kedua yang menentukan:
     *   1. nama fase — "verifikasi" atau "bahv" (istilah, bisa berganti);
     *   2. bukti struktural — HANYA fase verifikasi yang membawa uang. Purifikasi tidak
     *      pernah memuat payment/benefit/copay (lih. satu_sehat_claimresponse_nilai.sql).
     * Lapis kedua tidak bergantung pada istilah sama sekali, jadi ia yang bikin pembacaan
     * ini tahan banting terhadap penamaan yang belum pernah kita lihat.
     *
     * Aman dari salah tangkap: "purifikasi" bukan mengandung kata "verifikasi".
     */
    private boolean fasaVerifikasi(String subType, JsonNode cr) {
        String s = nz(subType).toLowerCase();
        if (s.contains("verifikasi") || s.contains("bahv")) return true;
        if (cr.path("payment").path("amount").has("value")) return true;
        if (bacaTotal(cr, "benefit") > 0) return true;
        return bacaCopay(cr) > 0;
    }

    /** Kode adjudikasi yang bisa dibaca mesin (mis. CRA000001), pendamping teks status. */
    private String bacaKodeStatus(JsonNode cr) {
        for (JsonNode adj : cr.path("adjudication")) {
            String code = nz(adj.path("reason").path("coding").path(0).path("code").asText()).trim();
            if (!code.equals("")) return code;
        }
        return "";
    }

    /** Nilai identifier yang system-nya memuat potongan tertentu (claim-number / claim-batch-number). */
    private String bacaIdentifier(JsonNode cr, String potonganSystem) {
        for (JsonNode iden : cr.path("identifier")) {
            if (nz(iden.path("system").asText()).contains(potonganSystem)) {
                return nz(iden.path("value").asText()).trim();
            }
        }
        return "";
    }

    /** `total[]` dengan category.coding.code tertentu: "submitted" (diajukan) / "benefit" (disetujui). */
    private double bacaTotal(JsonNode cr, String kodeKategori) {
        for (JsonNode t : cr.path("total")) {
            for (JsonNode c : t.path("category").path("coding")) {
                if (nz(c.path("code").asText()).equalsIgnoreCase(kodeKategori)) {
                    return t.path("amount").path("value").asDouble(0);
                }
            }
        }
        return 0;
    }

    /**
     * Total excess = jumlah `item[].adjudication[]` bercategory `copay`
     * (mis. selisih akibat naik kelas). Dijumlah lintas item karena satu klaim
     * bisa punya banyak item.
     */
    private double bacaCopay(JsonNode cr) {
        double total = 0;
        for (JsonNode item : cr.path("item")) {
            for (JsonNode adj : item.path("adjudication")) {
                for (JsonNode c : adj.path("category").path("coding")) {
                    if (nz(c.path("code").asText()).equalsIgnoreCase("copay")) {
                        total += adj.path("amount").path("value").asDouble(0);
                    }
                }
            }
        }
        return total;
    }

    // ====================== DATA ======================

    private String ambilNoSep(String noRawat) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select no_sep from bridging_sep where no_rawat=? order by tglsep desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            String hasil = "";
            if (r.next()) hasil = nz(r.getString("no_sep")).trim();
            r.close();
            p.close();
            return hasil;
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse ambilNoSep : " + e);
            return "";
        }
    }

    /**
     * Purifikasi (langkah 8) — status saja, tanpa angka.
     *
     * WAJIB `insert ... on duplicate key update`, BUKAN `replace into`: satu baris menampung
     * KEDUA fase, dan replace akan mengosongkan kolom fase yang lain beserta nilai_*.
     */
    private void simpanPurifikasi(String noRawat, String id, String status,
            String disposition, String raw, String batch) {
        String sql = "insert into satu_sehat_claimresponse "
                + "(no_rawat,id_claimresponse_purifikasi,status_purifikasi,disposition_purifikasi,"
                + "raw_response_purifikasi,no_batch,tgl_sync) values (?,?,?,?,?,?,now()) "
                + "on duplicate key update "
                + "id_claimresponse_purifikasi=values(id_claimresponse_purifikasi), "
                + "status_purifikasi=values(status_purifikasi), "
                + "disposition_purifikasi=values(disposition_purifikasi), "
                + "raw_response_purifikasi=values(raw_response_purifikasi), "
                // no_batch dipakai bersama dua fase: jangan timpa dengan nilai kosong.
                + "no_batch=if(values(no_batch)='' or values(no_batch) is null, no_batch, values(no_batch)), "
                + "tgl_sync=now()";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            p.setString(2, id);
            p.setString(3, status);
            p.setString(4, disposition);
            p.setString(5, raw);
            p.setString(6, batch);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse simpan (purifikasi) : " + e);
        }
    }

    /** Verifikasi (langkah 10) — status + seluruh angka klaim. */
    private void simpanVerifikasi(String noRawat, String id, String status,
            String disposition, String raw, String batch, Hasil h) {
        String sql = "insert into satu_sehat_claimresponse "
                + "(no_rawat,id_claimresponse_verifikasi,status_verifikasi,disposition_verifikasi,"
                + "raw_response_verifikasi,no_batch,kode_verifikasi,nilai_diajukan,nilai_disetujui,"
                + "nilai_copay,nilai_bayar,tgl_bayar,tgl_sync) values (?,?,?,?,?,?,?,?,?,?,?,?,now()) "
                + "on duplicate key update "
                + "id_claimresponse_verifikasi=values(id_claimresponse_verifikasi), "
                + "status_verifikasi=values(status_verifikasi), "
                + "disposition_verifikasi=values(disposition_verifikasi), "
                + "raw_response_verifikasi=values(raw_response_verifikasi), "
                + "no_batch=if(values(no_batch)='' or values(no_batch) is null, no_batch, values(no_batch)), "
                + "kode_verifikasi=values(kode_verifikasi), "
                + "nilai_diajukan=values(nilai_diajukan), nilai_disetujui=values(nilai_disetujui), "
                + "nilai_copay=values(nilai_copay), nilai_bayar=values(nilai_bayar), "
                + "tgl_bayar=values(tgl_bayar), tgl_sync=now()";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            p.setString(2, id);
            p.setString(3, status);
            p.setString(4, disposition);
            p.setString(5, raw);
            p.setString(6, batch);
            p.setString(7, h.kodeVerifikasi);
            p.setDouble(8, h.nilaiDiajukan);
            p.setDouble(9, h.nilaiDisetujui);
            p.setDouble(10, h.nilaiCopay);
            p.setDouble(11, h.nilaiBayar);
            if (h.tglBayar.equals("")) p.setNull(12, java.sql.Types.DATE);
            else p.setString(12, h.tglBayar.length() >= 10 ? h.tglBayar.substring(0, 10) : h.tglBayar);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ClaimResponse simpan (verifikasi) : " + e);
        }
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
