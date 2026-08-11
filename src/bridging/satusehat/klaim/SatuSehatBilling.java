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
import java.nio.charset.StandardCharsets;
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
 * Sub-sender Billing (Coverage, Account, ChargeItem, Invoice) untuk flow SatuSehatBundle.
 *
 * Per kunjungan: Coverage (bila BPJS) + Account + ChargeItem (tiap tindakan) + Invoice (rekap),
 * lalu PATCH Encounter.account. Account/ChargeItem/Invoice dikirim dalam 1 transaction bundle
 * (cross-ref via id deterministik). Idempotent: stableResourceId -> PUT.
 */
public class SatuSehatBilling {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    private static final String DEFAULT_KPTL_SYSTEM = "http://terminology.kemkes.go.id/CodeSystem/kptl";

    /** Master KPTL (kode -> display) dari ./cache/kptl.iyem; dimuat sekali, dipakai bersama. */
    private static java.util.Map<String, String> masterKptl;

    public SatuSehatBilling() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing : " + e);
        }
    }

    private static class Konteks {
        String idPasien="", namaPasien="", namaPasienSehat="", idOrg="", noPeserta="", waktuReg="";
        String namaRs="";
        // Organization BPJS di SATUSEHAT (payor Coverage / insurer CoverageEligibilityResponse).
        String idOrgBpjs="";
        // Data SEP (bridging_sep) -> Coverage.class + CoverageEligibilityResponse.
        String segmen="", namaSegmen="", kelas="", noSep="", tglSep="", tglPulangSep="", jnsPelayanan="";
        // Wajib untuk CoverageEligibilityRequest: enterer (Practitioner) & facility (Location).
        String idPraktisi="", idLokasi="";
    }

    private static class Tindakan {
        String source="", kdJenis="", namaPerawatan="", ktpPraktisi="", namaPraktisi="", role="";
        String tgl="", jam="", biaya="0";
        String idPraktisi="";
        // Performer kedua: dipakai tindakan drpr (dokter + perawat mengerjakan bersama).
        String ktpPraktisi2="", namaPraktisi2="", role2="", idPraktisi2="";
        // KPTL yang sudah ditentukan di reader (dipakai registrasi: kodenya turunan enum
        // stts_daftar, bukan hasil lookup tabel). Bila kosong -> lookupKptl() seperti biasa.
        String kptlCode="", kptlSystem="", kptlDisplay="";
    }

    private static class Obat {
        // sumber = "obat" (detail_pemberian_obat) / "resep_pulang". Ikut jadi kunci id
        // supaya obat pulang tidak bertabrakan dengan pemberian obat pada tgl+jam yang sama.
        String sumber="obat";
        String kodeBrng="", namaBrng="", kfaCode="", kfaSystem="", kfaDisplay="";
        String tgl="", jam="";
        double jml=1, total=0;
    }

    /** Baris potongan/pengurangan biaya -> Invoice.lineItem.priceComponent type=discount. */
    private static class Potongan {
        String nama="";
        double besar=0;
    }

    /** PREVIEW: rakit SATU Bundle billing (Coverage+Account+ChargeItem+Invoice) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || k.idPasien.equals("")) return null;

        List<Tindakan> tindakan = ambilTindakan(noRawat);
        List<Obat> obat = ambilObat(noRawat);
        String endPeriod = k.waktuReg;
        for (Tindakan t : tindakan) {
            String w = formatWaktu(t.tgl + " " + t.jam);
            if (!w.equals("") && (endPeriod.equals("") || w.compareTo(endPeriod) > 0)) endPeriod = w;
        }
        for (Obat o : obat) {
            String w = formatWaktu(o.tgl + " " + o.jam);
            if (!w.equals("") && (endPeriod.equals("") || w.compareTo(endPeriod) > 0)) endPeriod = w;
        }

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        // SEP: CoverageEligibilityRequest + Response — assembly sama dengan kirim().
        // Coverage tidak lagi jadi entry sendiri: ia contained di keduanya & di Account.
        if (adaKepesertaan(k) && !k.noSep.equals("")) {
            lengkapiKonteksSep(noRawat, idEncounter, k);
            String cerId = stableResourceId("CoverageEligibilityResponse", k.noSep);
            if (bisaKirimSep(k)) {
                tambahEntry(entries, "CoverageEligibilityRequest", idCerRequest(cerId),
                        buatCoverageEligibilityRequest(k, idCerRequest(cerId)));
                tambahEntry(entries, "CoverageEligibilityResponse", cerId,
                        buatCoverageEligibilityResponse(k, cerId));
            }
        }

        String accId = stableResourceId("Account", noRawat);
        tambahEntry(entries, "Account", accId, buatAccount(noRawat, k, accId, endPeriod));

        double total = 0;
        List<String> ciIds = new ArrayList<>();
        List<Double> ciBiaya = new ArrayList<>();
        for (Tindakan t : tindakan) {
            String[] kptl = resolveKptl(t);
            if (kptl == null) continue;                 // tak dipetakan / kode tak sah -> dilewati
            String ciId = stableResourceId("ChargeItem", noRawat, t.source, t.kdJenis, t.ktpPraktisi, t.tgl, t.jam);
            tambahEntry(entries, "ChargeItem", ciId, buatChargeItem(noRawat, k, t, ciId, idEncounter, accId, kptl));
            ciIds.add(ciId);
            double b = parseDouble(t.biaya);
            ciBiaya.add(b);
            total += b;
        }
        for (Obat o : obat) {
            String ciId = stableResourceId("ChargeItem", noRawat, o.sumber, o.kodeBrng, o.tgl, o.jam);
            tambahEntry(entries, "ChargeItem", ciId, buatChargeItemObat(noRawat, k, o, ciId, idEncounter, accId));
            ciIds.add(ciId);
            ciBiaya.add(o.total);
            total += o.total;
        }
        if (!ciIds.isEmpty()) {
            String invId = stableResourceId("Invoice", noRawat);
            tambahEntry(entries, "Invoice", invId,
                    buatInvoice(noRawat, k, invId, accId, ciIds, ciBiaya, total, ambilPotongan(noRawat), endPeriod));
        }
        if (entries.size() == 0) return null;
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        Konteks k = ambilKonteks(noRawat);
        if (k == null || k.idPasien.equals("")) {
            return;
        }

        // 1) Data kepesertaan (SEP) — independen dari billing, lihat kirimSep().
        kirimSep(noRawat, idEncounter, k);

        // 2) Account + ChargeItem + Invoice (1 transaction).
        List<Tindakan> tindakan = ambilTindakan(noRawat);
        List<Obat> obat = ambilObat(noRawat);
        // servicePeriod.end = waktu tindakan/obat terakhir (fallback waktu registrasi).
        String endPeriod = k.waktuReg;
        for (Tindakan t : tindakan) {
            String w = formatWaktu(t.tgl + " " + t.jam);
            if (!w.equals("") && (endPeriod.equals("") || w.compareTo(endPeriod) > 0)) endPeriod = w;
        }
        for (Obat o : obat) {
            String w = formatWaktu(o.tgl + " " + o.jam);
            if (!w.equals("") && (endPeriod.equals("") || w.compareTo(endPeriod) > 0)) endPeriod = w;
        }
        String accId = stableResourceId("Account", noRawat);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        tambahEntry(entries, "Account", accId, buatAccount(noRawat, k, accId, endPeriod));

        double total = 0;
        List<String> ciIds = new ArrayList<>();
        List<Double> ciBiaya = new ArrayList<>();
        List<String> ciKodeItem = new ArrayList<>();
        int dilewati = 0;
        for (Tindakan t : tindakan) {
            String[] kptl = resolveKptl(t);
            if (kptl == null) {                         // tak dipetakan / kode tak sah -> dilewati
                dilewati++;
                continue;
            }
            String ciId = stableResourceId("ChargeItem", noRawat, t.source, t.kdJenis, t.ktpPraktisi, t.tgl, t.jam);
            tambahEntry(entries, "ChargeItem", ciId, buatChargeItem(noRawat, k, t, ciId, idEncounter, accId, kptl));
            ciIds.add(ciId);
            double b = parseDouble(t.biaya);
            ciBiaya.add(b);
            total += b;
            ciKodeItem.add(t.source + "-" + t.kdJenis + "-" + t.ktpPraktisi + "-" + t.tgl + "-" + t.jam);
        }
        if (dilewati > 0) {
            System.out.println("Billing " + noRawat + " : " + dilewati + " dari " + tindakan.size()
                    + " tindakan DILEWATI (KPTL belum dipetakan / kode tak sah). "
                    + "Nilainya tidak masuk Invoice — perbaiki mapping-nya lalu kirim ulang.");
        }
        // 2a) Transaksi INTI (atomik): Account + ChargeItem tindakan. Terbukti stabil.
        String hasil = kirimBundle(bundle);
        if (hasil == null) return;   // gagal (sudah dilog)
        simpan("satu_sehat_account", "no_rawat,id_account", noRawat, accId);
        for (int i = 0; i < ciIds.size(); i++) {
            Sequel.queryu2("replace into satu_sehat_chargeitem (no_rawat,kode_item,id_chargeitem) values (?,?,?)",
                    3, new String[]{noRawat, ciKodeItem.get(i), ciIds.get(i)});
        }

        // 2b) ChargeItem OBAT: dikirim TERPISAH dari transaksi inti (1 KFA invalid tak merusak Account+tindakan),
        //     tapi semua obat dalam 1 transaksi (hindari rate-limit 429 dari banyak PUT beruntun).
        if (!obat.isEmpty()) {
            ObjectNode obatBundle = mapper.createObjectNode();
            obatBundle.put("resourceType", "Bundle");
            obatBundle.put("type", "transaction");
            ArrayNode obatEntries = obatBundle.putArray("entry");
            List<String> obatIds = new ArrayList<>(), obatKode = new ArrayList<>();
            List<Double> obatBiaya = new ArrayList<>();
            for (Obat o : obat) {
                String ciId = stableResourceId("ChargeItem", noRawat, o.sumber, o.kodeBrng, o.tgl, o.jam);
                tambahEntry(obatEntries, "ChargeItem", ciId, buatChargeItemObat(noRawat, k, o, ciId, idEncounter, accId));
                obatIds.add(ciId);
                obatKode.add(o.sumber + "-" + o.kodeBrng + "-" + o.tgl + "-" + o.jam);
                obatBiaya.add(o.total);
            }
            if (kirimBundle(obatBundle) != null) {
                for (int i = 0; i < obatIds.size(); i++) {
                    ciIds.add(obatIds.get(i));
                    ciBiaya.add(obatBiaya.get(i));
                    total += obatBiaya.get(i);
                    ciKodeItem.add(obatKode.get(i));
                    Sequel.queryu2("replace into satu_sehat_chargeitem (no_rawat,kode_item,id_chargeitem) values (?,?,?)",
                            3, new String[]{noRawat, obatKode.get(i), obatIds.get(i)});
                }
                System.out.println("Billing obat: " + obatIds.size() + " terkirim (1 transaksi).");
            } else {
                System.out.println("Billing obat: gagal (dilewati, billing inti aman). Cek error KFA/quantity di atas.");
            }
        }

        // 2c) Invoice (tindakan + obat yg berhasil) — PUT terpisah.
        String invId = "";
        List<Potongan> potongan = ambilPotongan(noRawat);
        if (!ciIds.isEmpty()) {
            invId = stableResourceId("Invoice", noRawat);
            if (putResource("Invoice", invId,
                    buatInvoice(noRawat, k, invId, accId, ciIds, ciBiaya, total, potongan, endPeriod))) {
                simpan("satu_sehat_invoice", "no_rawat,id_invoice", noRawat, invId);
            }
        }

        // 4) PATCH Encounter.account.
        try {
            patchEncounterAccount(noRawat, idEncounter, accId);
        } catch (Exception e) {
            System.out.println("Notifikasi Billing PATCH : " + e);
        }

        // 5) Verifikasi: cek apakah SATUSEHAT mengindeks ChargeItem (cara viewer mencari).
        cekChargeItemTerindeks(idEncounter, accId);

        // 6) Rekonsiliasi vs nota kasir: berapa persen tagihan yang benar-benar terkirim.
        double totalPotongan = 0;
        for (Potongan g : potongan) totalPotongan += g.besar;
        rekonsiliasiBilling(noRawat, total, totalPotongan);
    }

    // ====================== SEP / DATA KEPESERTAAN ======================

    /** Hasil pengiriman data kepesertaan, supaya pemanggil bisa memberi pesan yang tepat. */
    public static class HasilSep {
        public boolean berhasil = false;
        /** true bila kunjungan ini memang tidak punya data kepesertaan BPJS (bukan kegagalan). */
        public boolean dilewati = false;
        public String pesan = "";
    }

    /**
     * Kirim data kepesertaan satu kunjungan ke SATUSEHAT: CoverageEligibilityRequest +
     * CoverageEligibilityResponse, keduanya membawa Coverage sebagai contained.
     *
     * Dipakai dua pemicu: sub-sender billing di flow SatuSehatBundle, dan menu Data SEP begitu
     * SEP terbit. Sengaja SATU builder untuk dua pemicu — menyalin buildernya ke pemanggil kedua
     * adalah pola yang berulang kali membuat dua sender di repo ini saling drift.
     *
     * Idempotent: id diturunkan dari no SEP lalu dikirim dengan PUT.
     */
    public HasilSep kirimSep(String noRawat, String idEncounter) {
        HasilSep hasil = new HasilSep();
        if (noRawat == null || noRawat.trim().equals("")) {
            hasil.pesan = "No.Rawat kosong.";
            return hasil;
        }
        if (idEncounter == null || idEncounter.trim().equals("")) {
            hasil.pesan = "Encounter SATUSEHAT belum ada untuk kunjungan ini, "
                    + "sehingga Location kunjungan belum bisa ditentukan.";
            return hasil;
        }
        Konteks k = ambilKonteks(noRawat.trim());
        if (k == null || k.idPasien.equals("")) {
            hasil.pesan = "Data pasien/IHS untuk No.Rawat " + noRawat + " tidak lengkap.";
            return hasil;
        }
        return kirimSep(noRawat.trim(), idEncounter.trim(), k);
    }

    /**
     * Versi internal yang memakai Konteks yang sudah dibaca pemanggil, supaya flow bundle tidak
     * membaca ulang data yang sama dari database.
     *
     * Coverage TIDAK dikirim sebagai resource standalone; ia menumpang di CER dan (dulu) di
     * Account sebagai contained "#data-kepesertaan-bpjs".
     */
    private HasilSep kirimSep(String noRawat, String idEncounter, Konteks k) {
        HasilSep hasil = new HasilSep();
        if (!adaKepesertaan(k) || k.noSep.equals("")) {
            hasil.dilewati = true;
            hasil.berhasil = true;
            hasil.pesan = k.noSep.equals("")
                    ? "Kunjungan ini belum punya SEP, data kepesertaan tidak dikirim."
                    : "Data kepesertaan belum lengkap (no peserta / Organization BPJS kosong).";
            return hasil;
        }
        try {
            lengkapiKonteksSep(noRawat, idEncounter, k);
            String cerId = stableResourceId("CoverageEligibilityResponse", k.noSep);
            // Urutan WAJIB: Request dulu (CER.request menunjuk ke sana), baru Response.
            if (!bisaKirimSep(k)) {
                hasil.pesan = "Data pelengkap belum ada (Practitioner='" + k.idPraktisi
                        + "', Location='" + k.idLokasi + "').";
                System.out.println("Billing SEP dilewati: " + hasil.pesan);
                return hasil;
            }
            if (!putResource("CoverageEligibilityRequest", idCerRequest(cerId),
                    buatCoverageEligibilityRequest(k, idCerRequest(cerId)))) {
                hasil.pesan = "CoverageEligibilityRequest ditolak server, lihat satu_sehat_log.";
                System.out.println("Billing SEP dilewati: " + hasil.pesan);
                return hasil;
            }
            if (!putResource("CoverageEligibilityResponse", cerId,
                    buatCoverageEligibilityResponse(k, cerId))) {
                hasil.pesan = "CoverageEligibilityResponse ditolak server, lihat satu_sehat_log.";
                return hasil;
            }
            simpanJejakKepesertaan(noRawat, k.noPeserta, k.noSep, cerId);
            hasil.berhasil = true;
            hasil.pesan = "Data kepesertaan SEP " + k.noSep + " terkirim.";
        } catch (Exception e) {
            hasil.pesan = "Gagal kirim data kepesertaan : " + e;
            System.out.println("Notifikasi Billing CoverageEligibilityResponse : " + e);
        }
        return hasil;
    }

    /**
     * Simpan jejak lokal pengiriman data kepesertaan.
     *
     * Yang disimpan di id_coverage adalah id CoverageEligibilityResponse, BUKAN
     * "#data-kepesertaan-bpjs". Coverage tidak punya id sendiri di server karena ia contained,
     * sehingga penanda itu sama untuk semua baris dan tidak menyimpan informasi apa pun.
     */
    private void simpanJejakKepesertaan(String noRawat, String noKartu, String noSep, String cerId) {
        if (adaKolomNoSep()) {
            Sequel.queryu2("replace into satu_sehat_coverage (no_rawat,no_kartu,no_sep,id_coverage) "
                    + "values (?,?,?,?)", 4, new String[]{noRawat, noKartu, noSep, cerId});
        } else {
            Sequel.queryu2("replace into satu_sehat_coverage (no_rawat,no_kartu,id_coverage) "
                    + "values (?,?,?)", 3, new String[]{noRawat, noKartu, cerId});
        }
    }

    /**
     * True bila kolom no_sep sudah ada di satu_sehat_coverage. Dicek sekali per instans supaya
     * pengiriman tidak gagal total di database yang belum menjalankan
     * satu_sehat_coverage_no_sep.sql.
     */
    private Boolean adaKolomNoSep = null;

    private boolean adaKolomNoSep() {
        if (adaKolomNoSep == null) {
            adaKolomNoSep = Sequel.cariInteger(
                    "select count(*) from information_schema.columns where table_schema=database() "
                    + "and table_name='satu_sehat_coverage' and column_name='no_sep'") > 0;
            if (!adaKolomNoSep) {
                System.out.println("Notifikasi Billing : kolom satu_sehat_coverage.no_sep belum ada, "
                        + "No.SEP tidak dicatat. Jalankan satu_sehat_coverage_no_sep.sql.");
            }
        }
        return adaKolomNoSep;
    }

    /**
     * Id CoverageEligibilityResponse untuk sebuah No.SEP. Deterministik (turunan no SEP), jadi
     * bisa dihitung tanpa memanggil server — dipakai baik saat mengirim maupun saat memperbaiki
     * jejak lama.
     */
    public String idKepesertaan(String noSep) {
        if (noSep == null || noSep.trim().equals("")) {
            return "";
        }
        return stableResourceId("CoverageEligibilityResponse", noSep.trim());
    }

    /**
     * Perbaiki baris lama satu_sehat_coverage yang id_coverage-nya masih berisi penanda contained
     * ("#data-kepesertaan-bpjs") alih-alih id resource. Nilai penggantinya dihitung dari no SEP,
     * jadi TIDAK ada panggilan ke SATUSEHAT dan tidak ada data yang dikirim ulang.
     *
     * Aman diulang: baris yang sudah berisi id dibiarkan, baris tanpa SEP dilewati.
     *
     * @return jumlah baris yang diperbaiki
     */
    public int perbaikiJejakKepesertaan() {
        int jumlah = 0;
        PreparedStatement p = null;
        ResultSet r = null;
        try {
            p = koneksi.prepareStatement(
                    "select c.no_rawat, ifnull(s.no_sep,'') as no_sep from satu_sehat_coverage c "
                    + "left join bridging_sep s on s.no_rawat=c.no_rawat "
                    + "where c.id_coverage like '#%'");
            r = p.executeQuery();
            List<String[]> perbaikan = new ArrayList<>();
            while (r.next()) {
                String noRawat = nz(r.getString("no_rawat"));
                String noSep = nz(r.getString("no_sep")).trim();
                if (noSep.equals("")) {
                    System.out.println("Perbaikan jejak kepesertaan dilewati: No.Rawat " + noRawat
                            + " tidak punya SEP di bridging_sep.");
                    continue;
                }
                perbaikan.add(new String[]{idKepesertaan(noSep), noSep, noRawat});
            }
            // Update dilakukan setelah ResultSet ditutup supaya tidak memakai koneksi yang sama
            // sambil membaca.
            r.close();
            r = null;
            p.close();
            p = null;
            for (String[] baris : perbaikan) {
                if (adaKolomNoSep()) {
                    Sequel.queryu2("update satu_sehat_coverage set id_coverage=?, no_sep=? where no_rawat=?",
                            3, baris);
                } else {
                    Sequel.queryu2("update satu_sehat_coverage set id_coverage=? where no_rawat=?",
                            2, new String[]{baris[0], baris[2]});
                }
                jumlah++;
            }
        } catch (Exception e) {
            System.out.println("Notifikasi perbaikiJejakKepesertaan : " + e);
        } finally {
            try {
                if (r != null) r.close();
            } catch (Exception e) {
                System.out.println("Notifikasi perbaikiJejakKepesertaan tutup rs : " + e);
            }
            try {
                if (p != null) p.close();
            } catch (Exception e) {
                System.out.println("Notifikasi perbaikiJejakKepesertaan tutup ps : " + e);
            }
        }
        return jumlah;
    }

    /** Verifikasi: ChargeItem terindeks SATUSEHAT dicari lewat param ?encounter= (cara viewer "Rincian Biaya"). */
    private void cekChargeItemTerindeks(String idEncounter, String idAccount) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity<String> req = new HttpEntity<>(h);
            String url = link + "/ChargeItem?encounter=" + idEncounter;
            String body = api.getRest().exchange(url, HttpMethod.GET, req, String.class).getBody();
            int total = -1;
            try { total = mapper.readTree(body).path("total").asInt(); } catch (Exception ign) {}
            System.out.println("Cek ChargeItem terindeks (encounter=" + idEncounter + ") total=" + total);
        } catch (Throwable e) {
            System.out.println("Cek ChargeItem gagal : " + e);
        }
    }

    // ====================== COVERAGE ======================

    /** Id contained Coverage di dalam Account / CoverageEligibilityResponse. */
    private static final String ID_COVERAGE_CONTAINED = "data-kepesertaan-bpjs";

    /** True bila data kepesertaan BPJS cukup lengkap untuk dikirim. */
    private boolean adaKepesertaan(Konteks k) {
        return !k.noPeserta.equals("") && !k.idOrgBpjs.equals("");
    }

    /**
     * Coverage sebagai resource `contained` (bukan resource berdiri sendiri).
     * Direferensi lewat "#data-kepesertaan-bpjs" oleh Account.coverage dan
     * CoverageEligibilityResponse.insurance.
     *
     * Catatan: sampai 22 Juli 2026 Coverage dikirim sebagai resource standalone
     * ke {system}/coverage/{OrgRS} dengan payor Organization RS — keliru; payor
     * harus Organization BPJS. Skema contained ini yang menggantikannya.
     */
    private ObjectNode buatCoverageContained(Konteks k) {
        ObjectNode cov = mapper.createObjectNode();
        cov.put("id", ID_COVERAGE_CONTAINED);
        cov.put("resourceType", "Coverage");
        ObjectNode iden = cov.putArray("identifier").addObject();
        iden.put("system", "https://sys-ids.kemkes.go.id/insurance-subscriber/" + k.idOrgBpjs);
        iden.put("value", k.noPeserta);
        cov.put("status", "active");
        ObjectNode tc = cov.putObject("type").putArray("coding").addObject();
        tc.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
        tc.put("code", "PUBLICPOL");
        tc.put("display", "public healthcare");
        cov.put("subscriberId", k.noPeserta);
        cov.putObject("beneficiary").put("reference", "Patient/" + k.idPasien);   // tanpa display (Rule 10226)
        ObjectNode payor = cov.putArray("payor").addObject();
        payor.put("reference", "Organization/" + k.idOrgBpjs);
        payor.put("display", "BPJS Kesehatan");

        ArrayNode kelasArr = cov.putArray("class");
        // class[group] = segmentasi peserta (hasil mapping; dilewati bila belum dipetakan).
        if (!k.segmen.equals("")) {
            tambahCoverageClass(kelasArr, "group", "Group", k.segmen,
                    k.namaSegmen.equals("") ? k.segmen : k.namaSegmen);
        }
        // class[class] = kelas rawat SEP (1/2/3).
        if (!k.kelas.equals("")) {
            tambahCoverageClass(kelasArr, "class", "Class", k.kelas, "Kelas " + k.kelas);
        }
        if (kelasArr.size() == 0) cov.remove("class");
        return cov;
    }

    private void tambahCoverageClass(ArrayNode kelasArr, String tipeKode, String tipeDisplay,
            String value, String name) {
        ObjectNode item = kelasArr.addObject();
        ObjectNode c = item.putObject("type").putArray("coding").addObject();
        c.put("system", "http://terminology.hl7.org/CodeSystem/coverage-class");
        c.put("code", tipeKode);
        c.put("display", tipeDisplay);
        item.put("value", value);
        item.put("name", name);
    }

    /** Id CoverageEligibilityRequest pasangan dari sebuah CER. */
    private String idCerRequest(String idCer) {
        return stableResourceId("CoverageEligibilityRequest", idCer);
    }

    /** True bila data pelengkap CoverageEligibilityRequest tersedia. */
    private boolean bisaKirimSep(Konteks k) {
        return adaKepesertaan(k) && !k.noSep.equals("")
                && !k.idPraktisi.equals("") && !k.idLokasi.equals("");
    }

    /**
     * CoverageEligibilityRequest — WAJIB ada karena CoverageEligibilityResponse.request
     * bersifat 1..1 di FHIR R4. Template Postman resmi TIDAK memuat resource ini
     * sama sekali; tanpa dia server menolak CER dengan `missing required field "request"`.
     *
     * Field wajib versi server (ditemukan satu per satu lewat uji staging):
     * identifier(insurance-number/{orgBPJS}), servicedDate, enterer(Practitioner),
     * provider(Organization), insurer(Organization BPJS), facility(Location).
     */
    private ObjectNode buatCoverageEligibilityRequest(Konteks k, String id) {
        ObjectNode r = mapper.createObjectNode();
        r.putArray("contained").add(buatCoverageContained(k));
        r.put("resourceType", "CoverageEligibilityRequest");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("use", "official");
        iden.put("system", "http://sys-ids.kemkes.go.id/insurance-number/" + k.idOrgBpjs);
        iden.put("value", k.noPeserta);
        r.put("status", "active");
        r.putArray("purpose").add("validation");
        r.putObject("patient").put("reference", "Patient/" + k.idPasien);
        if (!k.tglSep.equals("")) {
            r.put("servicedDate", k.tglSep.length() >= 10 ? k.tglSep.substring(0, 10) : k.tglSep);
            r.put("created", k.tglSep);
        }
        r.putObject("enterer").put("reference", "Practitioner/" + k.idPraktisi);
        r.putObject("provider").put("reference", "Organization/" + k.idOrg);
        r.putObject("insurer").put("reference", "Organization/" + k.idOrgBpjs);
        r.putObject("facility").put("reference", "Location/" + k.idLokasi);
        ObjectNode ins = r.putArray("insurance").addObject();
        ins.put("focal", true);
        ins.putObject("coverage").put("reference", "#" + ID_COVERAGE_CONTAINED);
        return r;
    }

    /**
     * CoverageEligibilityResponse = representasi SEP di SATUSEHAT.
     * Memakai contained Coverage yang sama dengan Account (satu builder).
     * Skip bila tidak ada SEP.
     */
    private ObjectNode buatCoverageEligibilityResponse(Konteks k, String id) {
        ObjectNode r = mapper.createObjectNode();
        r.putArray("contained").add(buatCoverageContained(k));
        r.put("resourceType", "CoverageEligibilityResponse");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("use", "official");
        // claim-number memakai Organization RS (pemilik namespace identifier), BUKAN
        // Organization BPJS. Template Postman menulis {{Org_BPJS}} di sini, tapi server
        // menolaknya: "Wrong organization ID provided by system URL" (RuleNumber 10200).
        // Bandingkan insurance-subscriber di Coverage yang justru WAJIB pakai org BPJS.
        iden.put("system", "http://sys-ids.kemkes.go.id/claim-number/" + k.idOrg);
        iden.put("value", k.noSep);
        ObjectNode itc = iden.putObject("type").putArray("coding").addObject();
        itc.put("system", "http://terminology.hl7.org/CodeSystem/v2-0203");
        itc.put("code", "NIIP");
        itc.put("display", "National Insurance Payor Identifier (Payor)");
        // Ranap (jnspelayanan=1) berakhir saat pulang; ralan (2) berakhir di tanggal SEP.
        String akhir = k.jnsPelayanan.equals("1") && !k.tglPulangSep.equals("")
                ? k.tglPulangSep : k.tglSep;
        if (!k.tglSep.equals("")) {
            ObjectNode period = iden.putObject("period");
            period.put("start", k.tglSep);
            period.put("end", akhir);
        }
        r.put("status", "active");
        r.putArray("purpose").add("validation");
        r.putObject("patient").put("reference", "Patient/" + k.idPasien);
        if (!k.tglSep.equals("")) r.put("created", k.tglSep);
        r.putObject("requestor").put("reference", "Organization/" + k.idOrg);
        r.put("outcome", "complete");
        r.put("disposition", "Peserta aktif");
        r.putObject("insurer").put("reference", "Organization/" + k.idOrgBpjs);
        // request WAJIB (1..1) — lihat buatCoverageEligibilityRequest.
        r.putObject("request").put("reference", "CoverageEligibilityRequest/" + idCerRequest(id));
        ObjectNode ins = r.putArray("insurance").addObject();
        ins.putObject("coverage").put("reference", "#" + ID_COVERAGE_CONTAINED);
        ins.put("inforce", true);
        ObjectNode cat = ins.putArray("item").addObject().putObject("category").putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/ex-benefitcategory");
        cat.put("code", "30");
        cat.put("display", "Health Benefit Plan Coverage");
        return r;
    }

    // ====================== ACCOUNT / CHARGEITEM / INVOICE ======================

    private ObjectNode buatAccount(String noRawat, Konteks k, String id, String endPeriod) {
        ObjectNode r = mapper.createObjectNode();
        // CATATAN PENTING (diuji 22 Juli 2026):
        // Account SENGAJA TIDAK memuat contained Coverage maupun coverage[], walau
        // template Postman resmi mencontohkannya. Bila Account punya contained
        // Coverage, setiap ChargeItem yang mereferensi Account itu GAGAL 500
        // (Apigee "SC-PostProcessor failed") -> rincian biaya & Invoice ikut hancur.
        // Dibuktikan dengan 2 Account kembar:
        //   ChargeItem -> Account TANPA contained  = 200 OK
        //   ChargeItem -> Account DENGAN contained = 500
        // Data kepesertaan tetap terkirim lengkap lewat CoverageEligibilityRequest
        // dan CoverageEligibilityResponse yang memakai builder contained yang sama.
        r.put("resourceType", "Account");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/account/" + k.idOrg);
        iden.put("use", "official");
        iden.put("value", noRawat);
        r.put("status", "active");
        ObjectNode type = r.putObject("type");
        ObjectNode tc = type.putArray("coding").addObject();
        tc.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
        tc.put("code", "PBILLACCT");
        tc.put("display", "patient billing account");
        type.put("text", "Tagihan Pasien");
        r.put("name", "Tagihan " + k.namaPasienSehat + " - " + noRawat);
        // subject.display WAJIB == nama Patient di SATUSEHAT (Rule 10226).
        ObjectNode subj = r.putArray("subject").addObject();
        subj.put("reference", "Patient/" + k.idPasien);
        subj.put("display", k.namaPasienSehat);
        // servicePeriod WAJIB start+end (Rule 10227/10289).
        String startP = k.waktuReg.equals("") ? endPeriod : k.waktuReg;
        String endP = (endPeriod.equals("") || endPeriod.compareTo(startP) < 0) ? startP : endPeriod;
        if (!startP.equals("")) {
            ObjectNode sp = r.putObject("servicePeriod");
            sp.put("start", startP);
            sp.put("end", endP);
        }
        // coverage[] juga diomit: referensinya "#data-kepesertaan-bpjs" hanya sah
        // bila contained-nya ada, dan contained itulah yang memicu 500 di ChargeItem.
        ObjectNode owner = r.putObject("owner");
        owner.put("reference", "Organization/" + k.idOrg);
        // display hanya bila nama RS terisi: display kosong berisiko ditolak
        // (lih. Rule 10226 utk subject) dan tak menambah informasi apa pun.
        if (!k.namaRs.trim().equals("")) owner.put("display", k.namaRs);
        r.put("description", "Akun Billing Pasien untuk Tarif, Pembiayaan, dan Penyesuaian");
        return r;
    }

    private ObjectNode buatChargeItem(String noRawat, Konteks k, Tindakan t, String id, String idEncounter,
            String accId, String[] kptl) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "ChargeItem");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/chargeitem/" + k.idOrg);
        iden.put("use", "official");
        iden.put("value", t.source + "-" + noRawat + "-" + t.kdJenis + "-" + t.ktpPraktisi + "-" + t.tgl + "-" + t.jam);
        r.put("status", "billable");
        // KPTL sudah diresolve & divalidasi pemanggil (resolveKptl) — tindakan tak sah tak sampai ke sini.
        ObjectNode code = r.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", kptl[2]);
        cc.put("code", kptl[0]);
        cc.put("display", kptl[1]);
        code.put("text", t.namaPerawatan);
        r.putObject("subject").put("reference", "Patient/" + k.idPasien);   // tanpa display (Rule 10226)
        r.putObject("context").put("reference", "Encounter/" + idEncounter);
        String waktu = formatWaktu(t.tgl + " " + t.jam);
        if (!waktu.equals("")) {
            ObjectNode period = r.putObject("occurrencePeriod");
            period.put("start", waktu);
            period.put("end", waktu);
        }
        if (!t.idPraktisi.equals("") || !t.idPraktisi2.equals("")) {
            ArrayNode perf = r.putArray("performer");
            tambahPerformer(perf, t.idPraktisi, t.namaPraktisi, t.role);
            // Tindakan drpr dikerjakan dokter + perawat: keduanya jadi performer.
            // Lewati bila praktisi kedua ternyata orang yang sama (data drpr kadang begitu).
            if (!t.idPraktisi2.equals("") && !t.idPraktisi2.equals(t.idPraktisi)) {
                tambahPerformer(perf, t.idPraktisi2, t.namaPraktisi2, t.role2);
            }
            if (perf.size() == 0) r.remove("performer");
        }
        r.putObject("quantity").put("value", 1);
        r.putArray("account").addObject().put("reference", "Account/" + accId);
        // extension unitPrice + totalPrice (sesuai contoh SATUSEHAT ChargeItem).
        double biaya = parseDouble(t.biaya);
        ArrayNode ext = r.putArray("extension");
        ObjectNode e1 = ext.addObject();
        e1.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/unitPrice");
        ObjectNode m1 = e1.putObject("valueMoney");
        m1.put("value", biaya);
        m1.put("currency", "IDR");
        ObjectNode e2 = ext.addObject();
        e2.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/totalPrice");
        ObjectNode m2 = e2.putObject("valueMoney");
        m2.put("value", biaya);
        m2.put("currency", "IDR");
        return r;
    }

    /** Satu entri ChargeItem.performer; dilewati bila id praktisi kosong. */
    private void tambahPerformer(ArrayNode perf, String idPraktisi, String namaPraktisi, String role) {
        if (idPraktisi == null || idPraktisi.equals("")) return;
        ObjectNode item = perf.addObject();
        ObjectNode fn = item.putObject("function").putArray("coding").addObject();
        fn.put("system", "http://snomed.info/sct");
        if (role != null && role.equalsIgnoreCase("perawat")) {
            fn.put("code", "224535009");
            fn.put("display", "Registered nurse");
        } else {
            fn.put("code", "158965000");
            fn.put("display", "Medical practitioner");
        }
        ObjectNode actor = item.putObject("actor");
        actor.put("reference", "Practitioner/" + idPraktisi);
        actor.put("display", namaPraktisi);
    }

    private ObjectNode buatInvoice(String noRawat, Konteks k, String id, String accId, List<String> ciIds,
            List<Double> ciBiaya, double total, List<Potongan> potongan, String endPeriod) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "Invoice");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/invoice/" + k.idOrg);
        iden.put("use", "official");
        iden.put("value", noRawat);
        r.put("status", "issued");
        r.putObject("subject").put("reference", "Patient/" + k.idPasien);     // tanpa display (Rule 10226)
        r.putObject("recipient").put("reference", "Patient/" + k.idPasien);
        // Invoice.date = "date the invoice was issued". Prioritas: tanggal nota terbit ->
        // waktu item billing terakhir -> waktu registrasi. Tanggal registrasi dipakai
        // paling akhir saja: untuk ranap jaraknya ke nota rata-rata 7 hari.
        String tglInvoice = ambilTanggalNota(noRawat);
        if (tglInvoice.equals("")) tglInvoice = nz(endPeriod);
        if (tglInvoice.equals("")) tglInvoice = k.waktuReg;
        if (!tglInvoice.equals("")) r.put("date", tglInvoice);
        ObjectNode issuer = r.putObject("issuer");
        issuer.put("reference", "Organization/" + k.idOrg);
        issuer.put("display", k.namaRs);
        r.putObject("account").put("reference", "Account/" + accId);
        ArrayNode line = r.putArray("lineItem");
        for (int i = 0; i < ciIds.size(); i++) {
            ObjectNode li = line.addObject();
            li.put("sequence", i + 1);
            li.putObject("chargeItemReference").put("reference", "ChargeItem/" + ciIds.get(i));
            ObjectNode pc = li.putArray("priceComponent").addObject();
            pc.put("type", "base");
            ObjectNode amt = pc.putObject("amount");
            amt.put("value", ciBiaya.get(i));
            amt.put("currency", "IDR");
        }
        // Potongan (pengurangan_biaya) berlaku untuk seluruh tagihan, bukan per tindakan,
        // jadi tempatnya di totalPriceComponent — bukan lineItem.priceComponent.
        double totalPotongan = 0;
        if (potongan != null && !potongan.isEmpty()) {
            ArrayNode tpc = r.putArray("totalPriceComponent");
            ObjectNode base = tpc.addObject();
            base.put("type", "base");
            ObjectNode baseAmt = base.putObject("amount");
            baseAmt.put("value", total);
            baseAmt.put("currency", "IDR");
            for (Potongan g : potongan) {
                ObjectNode d = tpc.addObject();
                d.put("type", "discount");
                if (!g.nama.equals("")) d.putObject("code").put("text", g.nama);
                ObjectNode amt = d.putObject("amount");
                amt.put("value", -g.besar);           // negatif: mengurangi tagihan
                amt.put("currency", "IDR");
                totalPotongan += g.besar;
            }
        }
        double bersih = total - totalPotongan;
        if (bersih < 0) bersih = 0;
        // gross = tagihan kotor (sebelum potongan), net = yang benar-benar ditagihkan.
        // Tanpa potongan keduanya sama, jadi perilaku lama tidak berubah.
        ObjectNode net = r.putObject("totalNet");
        net.put("value", bersih);
        net.put("currency", "IDR");
        ObjectNode gross = r.putObject("totalGross");
        gross.put("value", total);
        gross.put("currency", "IDR");
        return r;
    }

    // ====================== KIRIM / PATCH ======================

    /**
     * Kirim 1 resource via transaction bundle 1-entry (PUT -> create idempotent).
     * SATUSEHAT menolak PUT individual ke id yg belum ada (RuleNumber 20007 "Resource not found");
     * di dalam transaction, PUT membuat resource. Per-resource -> 1 KFA invalid tak menggagalkan lainnya.
     * true bila 2xx; false bila ditolak (di-skip).
     */
    private boolean putResource(String resourceType, String id, ObjectNode resource) {
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        tambahEntry(entries, resourceType, id, resource);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String payload = mapper.writeValueAsString(bundle);
            HttpEntity requestEntity = new HttpEntity(payload, headers);
            api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class);
            return true;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("Skip " + resourceType + "/" + id + " : " + ex.getStatusCode()
                    + " => " + ex.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.out.println("Notifikasi put " + resourceType + " : " + e);
            return false;
        }
    }

    private String kirimBundle(ObjectNode bundle) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String payload = mapper.writeValueAsString(bundle);
            System.out.println("URL Billing : " + link);
            System.out.println("Request JSON Billing : " + payload);
            HttpEntity requestEntity = new HttpEntity(payload, headers);
            String hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
            System.out.println("Result JSON Billing : " + hasil);
            return hasil;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("Error Billing Status Code: " + ex.getStatusCode());
            System.out.println("Error Billing Body: " + ex.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println("Notifikasi Billing kirim : " + e);
            return null;
        }
    }

    /**
     * PATCH Encounter.account = [Account/{idAccount}] via Apache HttpClient (HttpPatch).
     * RestTemplate Spring di runtime ini tidak punya HttpMethod.PATCH, jadi pakai HttpClient langsung.
     *
     * HttpClient-nya diminta ke ApiSatuSehat, BUKAN di-cast dari getRest().getRequestFactory():
     * sejak RestTemplate dibungkus Buffering + interceptor pencatat, getRequestFactory()
     * mengembalikan InterceptingClientHttpRequestFactory sehingga cast langsungnya melempar
     * ClassCastException dan PATCH ini tidak pernah benar-benar jalan.
     *
     * Karena tidak lewat RestTemplate, panggilan ini juga tidak tersentuh interceptor pencatat,
     * jadi jejak auditnya ditulis sendiri ke satu_sehat_log — termasuk saat gagal, sebab justru
     * kegagalanlah yang perlu terlihat saat audit.
     */
    private void patchEncounterAccount(String noRawat, String idEncounter, String idAccount) {
        String url = link + "/Encounter/" + idEncounter;
        String body = "[{\"op\":\"add\",\"path\":\"/account\",\"value\":[{\"reference\":\"Account/" + idAccount + "\"}]}]";
        long mulai = System.currentTimeMillis();
        try {
            // Pakai HttpClient yang SUDAH dikonfigurasi ApiSatuSehat (hindari build baru -> konflik versi httpclient).
            org.apache.http.client.HttpClient httpClient = api.getHttpClient();
            org.apache.http.client.methods.HttpPatch patch =
                    new org.apache.http.client.methods.HttpPatch(url);
            patch.setHeader("Authorization", "Bearer " + api.TokenSatuSehat());
            patch.setHeader("Content-Type", "application/json-patch+json");
            patch.setEntity(new org.apache.http.entity.StringEntity(body, "UTF-8"));
            System.out.println("URL PATCH Encounter : " + url);
            org.apache.http.HttpResponse resp = httpClient.execute(patch);
            int code = resp.getStatusLine().getStatusCode();
            String respBody = org.apache.http.util.EntityUtils.toString(resp.getEntity(), "UTF-8");
            // 409 = account sudah tertaut sebelumnya; bukan kegagalan, tapi tetap dicatat apa adanya.
            boolean sukses = (code >= 200 && code < 300) || code == 409;
            catatPatch(noRawat, url, body, code, respBody, sukses ? "SUCCESS" : "FAILED", null, mulai);
            if (code >= 200 && code < 300) {
                System.out.println("Billing PATCH Encounter.account OK (" + code + ") : " + idEncounter);
            } else if (code == 409) {
                System.out.println("Billing PATCH Encounter.account : sudah ada (409, dilewati).");
            } else {
                System.out.println("Notifikasi Billing PATCH : status " + code + " => " + respBody);
            }
        } catch (Throwable e) {
            // Throwable, bukan Exception: kegagalan sekelas ClassCastException/NoSuchMethodError
            // pernah membuat langkah ini diam-diam tidak jalan tanpa jejak apa pun.
            System.out.println("Notifikasi Billing PATCH : " + e);
            catatPatch(noRawat, url, body, null, "", "FAILED", String.valueOf(e), mulai);
        }
    }

    /** Tulis satu baris audit PATCH ke satu_sehat_log; tidak boleh jadi sumber kegagalan baru. */
    private void catatPatch(String noRawat, String url, String body, Integer kode, String respon,
                            String status, String error, long mulai) {
        try {
            bridging.satusehat.klaim.SatuSehatHttpLogger.catatManual("Billing PATCH", noRawat, url, body,
                    kode, respon, status, error, (int) (System.currentTimeMillis() - mulai));
        } catch (Throwable abaikan) {
            System.out.println("Notifikasi catat Billing PATCH : " + abaikan);
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

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select p.no_ktp as ktp_pasien, p.nm_pasien, ifnull(p.no_peserta,'') as no_peserta, "
                    + "concat(rp.tgl_registrasi,' ',rp.jam_reg) as waktu_reg "
                    + "from reg_periksa rp inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                k = new Konteks();
                k.namaPasien = nz(r.getString("nm_pasien"));
                k.noPeserta = nz(r.getString("no_peserta")).trim();
                k.waktuReg = formatWaktu(nz(r.getString("waktu_reg")));
                k.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                // Account.subject.display WAJIB == nama Patient di SATUSEHAT (Rule 10226).
                // GUNAKAN NAMA LOKAL (pasien.nm_pasien dibersihkan), BUKAN nama dari API: API mengembalikan
                // nama BERTOPENG ("Th** El**") utk pasien yg didaftarkan org lain -> tak akan cocok.
                // Untuk pasien yg DIDAFTARKAN RS sendiri, nama lokal = nama terdaftar -> cocok.
                k.namaPasienSehat = bersihNama(k.namaPasien);
                System.out.println("Billing nama pasien (lokal dipakai) : '" + k.namaPasienSehat + "'");
                k.idOrg = koneksiDB.IDSATUSEHAT();
                k.namaRs = nz(fungsi.akses.getnamars());
//                k.idOrgBpjs = nz(koneksiDB.IDORGBPJSSATUSEHAT()).trim();
                if (k.idOrgBpjs.equals("")) {
                    System.out.println("Notifikasi Billing : IDORGBPJSSATUSEHAT kosong di setting/database.xml "
                            + "=> data kepesertaan (Coverage) & SEP tidak dikirim. "
                            + "Isi dengan hasil GET {URLFHIRSATUSEHAT}/Organization?type=137");
                }
            }
            r.close();
            p.close();
            if (k != null) ambilDataSep(noRawat, k);
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilKonteks : " + e);
        }
        return k;
    }

    /**
     * Lengkapi konteks dengan data yang HANYA dibutuhkan CoverageEligibilityRequest:
     * enterer (Practitioner DPJP) dan facility (Location). Location diambil dari
     * Encounter yang sudah terkirim supaya persis sama dengan lokasi kunjungan.
     * Dipanggil terpisah karena butuh idEncounter (tak tersedia di ambilKonteks).
     */
    private void lengkapiKonteksSep(String noRawat, String idEncounter, Konteks k) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(pg.no_ktp,'') as ktp_dokter from reg_periksa rp "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) k.idPraktisi = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing praktisi SEP : " + e);
        }
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String body = api.getRest().exchange(link + "/Encounter/" + idEncounter,
                    HttpMethod.GET, new HttpEntity<>(h), String.class).getBody();
            String ref = nz(mapper.readTree(body).path("location").path(0).path("location")
                    .path("reference").asText());
            if (ref.startsWith("Location/")) k.idLokasi = ref.substring("Location/".length());
        } catch (Exception e) {
            System.out.println("Notifikasi Billing lokasi SEP : " + e);
        }
    }

    /**
     * Data kepesertaan dari SEP terakhir + segmentasi peserta hasil mapping
     * (menu "Mapping Segmen Peserta Satu Sehat" -> satu_sehat_mapping_segmen_peserta).
     * Kelas: pakai klsnaik bila pasien naik kelas, selain itu klsrawat.
     */
    private void ambilDataSep(String noRawat, Konteks k) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select s.no_sep, s.tglsep, s.tglpulang, s.jnspelayanan, s.no_kartu, "
                    + "ifnull(nullif(s.klsnaik,''), s.klsrawat) as kelas, "
                    + "ifnull(m.segmen,'') as segmen, ifnull(m.nama,'') as nama_segmen "
                    + "from bridging_sep s "
                    + "left join satu_sehat_mapping_segmen_peserta m on m.peserta=s.peserta "
                    + "where s.no_rawat=? order by s.tglsep desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                k.noSep = nz(r.getString("no_sep")).trim();
                k.tglSep = formatWaktu(nz(r.getString("tglsep")));
                k.tglPulangSep = formatWaktu(nz(r.getString("tglpulang")));
                k.jnsPelayanan = nz(r.getString("jnspelayanan")).trim();
                k.kelas = nz(r.getString("kelas")).trim();
                k.segmen = nz(r.getString("segmen")).trim();
                k.namaSegmen = nz(r.getString("nama_segmen")).trim();
                // no_peserta pasien kadang kosong; no_kartu di SEP lebih tepercaya.
                String noKartu = nz(r.getString("no_kartu")).trim();
                if (!noKartu.equals("")) k.noPeserta = noKartu;
                if (k.segmen.equals("")) {
                    System.out.println("Notifikasi Billing : jenis peserta SEP " + k.noSep
                            + " belum dipetakan => Coverage.class[group] dilewati. "
                            + "Petakan lewat menu Mapping Segmen Peserta Satu Sehat.");
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilDataSep : " + e);
        }
    }

    /**
     * Kumpulkan tindakan (chargeable): perawatan dokter/perawat ralan/ranap + penunjang lab & radiologi.
     * Routing KPTL per kategori ditentukan oleh t.source (lih. lookupKptl).
     */
    private List<Tindakan> ambilTindakan(String noRawat) {
        List<Tindakan> list = new ArrayList<>();
        ambilSumber(list, noRawat, "rawat_jl_dr", "dokter");
        ambilSumber(list, noRawat, "rawat_inap_dr", "dokter");
        ambilSumber(list, noRawat, "rawat_jl_pr", "perawat");
        ambilSumber(list, noRawat, "rawat_inap_pr", "perawat");
        ambilSumber(list, noRawat, "rawat_jl_drpr", "drpr");
        ambilSumber(list, noRawat, "rawat_inap_drpr", "drpr");
        ambilPenunjang(list, noRawat, "periksa_lab", "jns_perawatan_lab", "lab");
        ambilPenunjang(list, noRawat, "periksa_radiologi", "jns_perawatan_radiologi", "radiologi");
        ambilKamar(list, noRawat);
        ambilOperasi(list, noRawat);
        ambilRegistrasi(list, noRawat);
        // CATATAN: `tambahan_biaya` SENGAJA TIDAK dibaca (diuji 25 Juli 2026).
        // Isinya didominasi MCU (Rp 562 jt dari Rp 599 jt), dan MCU sudah punya jalur
        // resmi sebagai tindakan ralan: kode MCU01..MCU05 / RJ00091 di jns_perawatan,
        // sudah dipetakan ke KPTL 15198 di satu_sehat_mapping_tindakanralan_kptl, dan
        // benar-benar dipakai (550 baris di rawat_jl_pr). Dari 807 kunjungan ber-MCU,
        // 278 punya MCU di rawat_jl_pr DAN di tambahan_biaya sekaligus -> membaca
        // keduanya akan menagih dobel. Sisanya (registrasi, cetak kartu, legalisir)
        // biaya administratif tanpa padanan KPTL. Selisih yang belum tertagih adalah
        // masalah entri data (MCU diketik sebagai biaya tambahan alih-alih tindakan),
        // bukan masalah sender.
        return list;
    }

    /**
     * Tindakan operasi -> 1 ChargeItem per baris `operasi` (PK no_rawat+tgl_operasi+kode_paket).
     * Tarif = jumlah SELURUH komponen biaya di tabel operasi (jasa tiap peran + alat + sewa OK +
     * akomodasi + sarpras), sesuai cara DlgBilingRanap merekap operasi ke nota.
     * KPTL dipetakan PER PAKET (kode_paket) via satu_sehat_mapping_tindakanoperasi_kptl -> INNER JOIN,
     * jadi paket yang belum dipetakan dilewati (bukan dikirim dengan kode default yang salah).
     * Performer = operator1.
     */
    private void ambilOperasi(List<Tindakan> list, String noRawat) {
        String biaya = "ifnull(o.biayaoperator1,0)+ifnull(o.biayaoperator2,0)+ifnull(o.biayaoperator3,0)"
                + "+ifnull(o.biayaasisten_operator1,0)+ifnull(o.biayaasisten_operator2,0)+ifnull(o.biayaasisten_operator3,0)"
                + "+ifnull(o.biayainstrumen,0)+ifnull(o.biayadokter_anak,0)+ifnull(o.biayaperawaat_resusitas,0)"
                + "+ifnull(o.biayadokter_anestesi,0)+ifnull(o.biayaasisten_anestesi,0)+ifnull(o.biayaasisten_anestesi2,0)"
                + "+ifnull(o.biayabidan,0)+ifnull(o.biayabidan2,0)+ifnull(o.biayabidan3,0)+ifnull(o.biayaperawat_luar,0)"
                + "+ifnull(o.biayaalat,0)+ifnull(o.biayasewaok,0)+ifnull(o.akomodasi,0)"
                + "+ifnull(o.biaya_omloop,0)+ifnull(o.biaya_omloop2,0)+ifnull(o.biaya_omloop3,0)"
                + "+ifnull(o.biaya_omloop4,0)+ifnull(o.biaya_omloop5,0)"
                + "+ifnull(o.biayasarpras,0)+ifnull(o.biaya_dokter_pjanak,0)+ifnull(o.biaya_dokter_umum,0)";
        String sql = "select o.kode_paket, ifnull(po.nm_perawatan,'') as nm_perawatan, "
                + "date(o.tgl_operasi) as tgl, time(o.tgl_operasi) as jam, (" + biaya + ") as biaya, "
                + "ifnull(pg.no_ktp,'') as ktp_praktisi, ifnull(d.nm_dokter,'') as nama_praktisi "
                + "from operasi o "
                + "inner join satu_sehat_mapping_tindakanoperasi_kptl m on m.kode_paket=o.kode_paket "
                + "left join paket_operasi po on po.kode_paket=o.kode_paket "
                + "left join dokter d on d.kd_dokter=o.operator1 "
                + "left join pegawai pg on pg.nik=o.operator1 "
                + "where o.no_rawat=?";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Tindakan t = new Tindakan();
                t.source = "operasi";                            // routing KPTL -> mapping by kode_paket
                t.role = "dokter";
                t.kdJenis = nz(r.getString("kode_paket")).trim();
                t.namaPerawatan = nz(r.getString("nm_perawatan")).trim();
                if (t.namaPerawatan.equals("")) t.namaPerawatan = "Operasi " + t.kdJenis;
                t.tgl = nz(r.getString("tgl"));
                t.jam = nz(r.getString("jam"));
                t.biaya = nz(r.getString("biaya"));
                t.ktpPraktisi = nz(r.getString("ktp_praktisi"));
                t.namaPraktisi = nz(r.getString("nama_praktisi"));
                t.idPraktisi = nz(cek.tampilIDParktisi(t.ktpPraktisi));
                if (parseDouble(t.biaya) > 0) list.add(t);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilOperasi : " + e);
        }
    }

    /**
     * Biaya registrasi (`reg_periksa.biaya_reg`) -> 1 ChargeItem per kunjungan, tanpa performer.
     *
     * TIDAK memakai tabel mapping: kodenya turunan langsung dari enum `stts_daftar`
     * (Baru/Lama), jadi cukup konstanta — KPTL sudah menyediakan pasangannya persis.
     * Beda dengan biaya administrasi ADM.001..ADM.011 yang sudah masuk lewat jalur
     * tindakan ralan biasa (10.674 baris di rawat_jl_pr, sudah dipetakan KPTL);
     * `biaya_reg` adalah baris nota tersendiri (billing.status='Registrasi').
     */
    private void ambilRegistrasi(List<Tindakan> list, String noRawat) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(biaya_reg,0) as biaya_reg, stts_daftar, tgl_registrasi, jam_reg "
                    + "from reg_periksa where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                double biaya = parseDouble(nz(r.getString("biaya_reg")));
                String stts = nz(r.getString("stts_daftar")).trim();
                // '-' (status tak jelas) dilewati: tak bisa ditentukan Baru atau Lama.
                if (biaya > 0 && (stts.equalsIgnoreCase("Baru") || stts.equalsIgnoreCase("Lama"))) {
                    boolean baru = stts.equalsIgnoreCase("Baru");
                    Tindakan t = new Tindakan();
                    t.source = "registrasi";
                    t.role = "";                                  // tanpa performer
                    t.kdJenis = baru ? "Baru" : "Lama";
                    t.namaPerawatan = "Pendaftaran Pasien " + t.kdJenis;
                    t.kptlCode = baru ? "15229.PS001" : "15229.PS002";
                    t.kptlSystem = DEFAULT_KPTL_SYSTEM;
                    t.kptlDisplay = baru ? "Pendaftaran Pasien, Baru" : "Pendaftaran Pasien, Lama";
                    t.tgl = nz(r.getString("tgl_registrasi"));
                    t.jam = nz(r.getString("jam_reg"));
                    t.biaya = String.valueOf(biaya);
                    list.add(t);
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilRegistrasi : " + e);
        }
    }

    /**
     * Tanggal Invoice = tanggal NOTA terbit (`nota_inap`/`nota_jalan`), bukan tanggal
     * registrasi: FHIR mendefinisikan `Invoice.date` sebagai "date the invoice was issued".
     * Untuk ranap jeda registrasi→nota rata-rata 7 hari, jadi memakai tanggal registrasi
     * membuat Invoice bertanggal jauh sebelum tagihannya ada. "" bila nota belum terbit
     * (pemanggil jatuh ke endPeriod).
     */
    private String ambilTanggalNota(String noRawat) {
        String[] tabel = {"nota_inap", "nota_jalan"};
        for (String tbl : tabel) {
            try {
                PreparedStatement p = koneksi.prepareStatement(
                        "select tanggal, jam from " + tbl + " where no_rawat=? limit 1");
                p.setString(1, noRawat);
                ResultSet r = p.executeQuery();
                String hasil = "";
                if (r.next()) hasil = formatWaktu(nz(r.getString("tanggal")) + " " + nz(r.getString("jam")));
                r.close();
                p.close();
                if (!hasil.equals("")) return hasil;
            } catch (Exception e) {
                System.out.println("Notifikasi Billing ambilTanggalNota (" + tbl + ") : " + e);
            }
        }
        return "";
    }

    /** Semua pengurang tagihan -> baris diskon di Invoice (bukan ChargeItem: nilainya negatif). */
    private List<Potongan> ambilPotongan(String noRawat) {
        List<Potongan> list = new ArrayList<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select nama_pengurangan, besar_pengurangan from pengurangan_biaya "
                    + "where no_rawat=? and ifnull(besar_pengurangan,0)>0");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Potongan g = new Potongan();
                g.nama = nz(r.getString("nama_pengurangan")).trim();
                g.besar = parseDouble(nz(r.getString("besar_pengurangan")));
                if (g.besar > 0) list.add(g);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilPotongan : " + e);
        }
        ambilReturObat(list, noRawat);
        return list;
    }

    /**
     * Retur obat -> baris pengurang, BUKAN koreksi kuantitas ChargeItem obat.
     *
     * Alasannya harga retur ≠ harga jual (diuji 25 Juli 2026: B000008668 dijual
     * Rp 39.600/pcs, diretur Rp 33.000/pcs — retur tak memuat tuslah/embalase).
     * Mengurangi `quantity` ChargeItem akan membuat kuantitas dan rupiah tidak lagi
     * konsisten. Nota RS sendiri memperlakukan retur sebagai baris negatif terpisah
     * (`billing.status='Retur Obat'`, lih. DlgBilingRanap), jadi pola ini mengikutinya.
     *
     * Link ke kunjungan lewat prefix: `no_retur_jual` = no_rawat + sufiks urut
     * (mis. no_rawat 2026/05/04/000032 -> no_retur_jual 2026/05/04/00003201) — sama
     * seperti DlgBilingRanap, tapi memakai `like 'no_rawat%'` (prefix) alih-alih
     * `like '%no_rawat%'` agar tidak menyapu kunjungan lain.
     */
    private void ambilReturObat(List<Potongan> list, String noRawat) {
        String sql = "select ifnull(db.nama_brng, drj.kode_brng) as nama, sum(drj.subtotal) as subtotal "
                + "from returjual rj "
                + "inner join detreturjual drj on drj.no_retur_jual=rj.no_retur_jual "
                + "left join databarang db on db.kode_brng=drj.kode_brng "
                + "where rj.no_retur_jual like ? "
                + "group by drj.kode_brng, db.nama_brng having sum(drj.subtotal)>0";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat + "%");
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Potongan g = new Potongan();
                g.nama = "Retur Obat: " + nz(r.getString("nama")).trim();
                g.besar = parseDouble(nz(r.getString("subtotal")));
                if (g.besar > 0) list.add(g);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilReturObat : " + e);
        }
    }

    /**
     * Rekonsiliasi: bandingkan total ChargeItem yang dikirim dengan total nota di tabel
     * `billing` (nota yang benar-benar dicetak kasir). Selisihnya = item yang belum
     * dipetakan/terkirim. Hanya melaporkan — tidak mengubah apa pun.
     *
     * Baris `Ttl*`/separator dikecualikan: itu subtotal tampilan, bukan komponen biaya.
     * `billing` baru terisi setelah nota diterbitkan (91% kunjungan), jadi bila kosong
     * rekonsiliasi dilewati, bukan dilaporkan sebagai selisih.
     */
    private void rekonsiliasiBilling(String noRawat, double totalTerkirim, double totalPotongan) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select count(*) as baris, ifnull(sum(totalbiaya),0) as nota from billing "
                    + "where no_rawat=? and status not like 'Ttl%' "
                    + "and status not in ('-','Dokter','Perawat','Tagihan')");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                int baris = r.getInt("baris");
                double nota = r.getDouble("nota");
                if (baris == 0) {
                    System.out.println("Rekonsiliasi billing " + noRawat + " : nota belum terbit, dilewati.");
                } else {
                    double bersih = totalTerkirim - totalPotongan;
                    double selisih = nota - bersih;
                    double persen = nota != 0 ? (bersih / nota) * 100 : 0;
                    System.out.printf("Rekonsiliasi billing %s : nota=%.0f terkirim=%.0f selisih=%.0f (%.1f%% tercakup)%n",
                            noRawat, nota, bersih, selisih, persen);
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing rekonsiliasi : " + e);
        }
    }

    /**
     * Akomodasi rawat inap -> ChargeItem. Sumber kamar_inap (keyed kd_kamar), tarif = ttl_biaya,
     * TANPA performer (akomodasi bukan tindakan praktisi). KPTL dipetakan PER KAMAR (kd_kamar) via
     * satu_sehat_mapping_kamar_kptl -> INNER JOIN, jadi hanya kamar yang sudah dipetakan yang
     * ditagihkan (menghindari kode KPTL default yang salah untuk akomodasi).
     */
    private void ambilKamar(List<Tindakan> list, String noRawat) {
        String sql = "select ki.kd_kamar, concat(ifnull(b.nm_bangsal,''),' ',ifnull(km.kelas,'')) as nama, "
                + "ki.ttl_biaya, ki.tgl_masuk, ki.jam_masuk "
                + "from kamar_inap ki "
                + "inner join satu_sehat_mapping_kamar_kptl m on m.kd_kamar=ki.kd_kamar "
                + "left join kamar km on km.kd_kamar=ki.kd_kamar "
                + "left join bangsal b on b.kd_bangsal=km.kd_bangsal "
                + "where ki.no_rawat=? and ifnull(ki.ttl_biaya,0)>0";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Tindakan t = new Tindakan();
                t.source = "kamar";                             // routing KPTL -> satu_sehat_mapping_kamar_kptl (by kd_kamar)
                t.role = "";                                    // akomodasi: tanpa performer
                t.kdJenis = nz(r.getString("kd_kamar")).trim(); // key KPTL = kd_kamar
                t.namaPerawatan = "Akomodasi " + nz(r.getString("nama")).trim() + " (" + nz(r.getString("kd_kamar")) + ")";
                t.tgl = nz(r.getString("tgl_masuk"));
                t.jam = nz(r.getString("jam_masuk"));
                t.biaya = nz(r.getString("ttl_biaya"));
                if (parseDouble(t.biaya) > 0) list.add(t);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilKamar : " + e);
        }
    }

    /**
     * Penunjang (lab/radiologi) -> ChargeItem. Sumber periksa_lab / periksa_radiologi, keyed
     * kd_jenis_prw (di-trim; periksa_radiologi kerap punya spasi), nama dari jns_perawatan_lab/_radiologi,
     * tarif = kolom biaya, praktisi = kd_dokter. KPTL diambil dari satu_sehat_mapping_tindakan{lab,radiologi}_kptl.
     */
    private void ambilPenunjang(List<Tindakan> list, String noRawat, String tbl, String jnsTbl, String kategori) {
        String sql = "select trim(t.kd_jenis_prw) as kd_jenis_prw, jp.nm_perawatan, t.tgl_periksa, t.jam, t.biaya, "
                + "ifnull(pg.no_ktp,'') as ktp_praktisi, ifnull(d.nm_dokter,'') as nama_praktisi "
                + "from " + tbl + " t "
                + "inner join " + jnsTbl + " jp on trim(jp.kd_jenis_prw)=trim(t.kd_jenis_prw) "
                + "left join dokter d on d.kd_dokter=t.kd_dokter "
                + "left join pegawai pg on pg.nik=t.kd_dokter "
                + "where t.no_rawat=?";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Tindakan t = new Tindakan();
                t.source = kategori;                 // "lab" / "radiologi" -> routing KPTL di lookupKptl
                t.role = "dokter";
                t.kdJenis = nz(r.getString("kd_jenis_prw")).trim();
                t.namaPerawatan = nz(r.getString("nm_perawatan"));
                t.tgl = nz(r.getString("tgl_periksa"));
                t.jam = nz(r.getString("jam"));
                t.biaya = nz(r.getString("biaya"));
                t.ktpPraktisi = nz(r.getString("ktp_praktisi"));
                t.namaPraktisi = nz(r.getString("nama_praktisi"));
                t.idPraktisi = nz(cek.tampilIDParktisi(t.ktpPraktisi));
                if (parseDouble(t.biaya) > 0) list.add(t);   // skip tindakan gratis/0
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilPenunjang (" + tbl + ") : " + e);
        }
    }

    private void ambilSumber(List<Tindakan> list, String noRawat, String source, String role) {
        String jnsTbl = source.contains("inap") ? "jns_perawatan_inap" : "jns_perawatan";
        String sql;
        if (role.equals("drpr")) {
            // Tindakan dikerjakan dokter DAN perawat bersama -> satu ChargeItem, dua performer.
            sql = "select t.kd_jenis_prw, jp.nm_perawatan, t.tgl_perawatan, t.jam_rawat, t.biaya_rawat, "
                + "ifnull(pgd.no_ktp,'') as ktp_praktisi, ifnull(d.nm_dokter,'') as nama_praktisi, "
                + "ifnull(pgp.no_ktp,'') as ktp_praktisi2, ifnull(pt.nama,'') as nama_praktisi2 "
                + "from " + source + " t "
                + "inner join " + jnsTbl + " jp on jp.kd_jenis_prw=t.kd_jenis_prw "
                + "left join dokter d on d.kd_dokter=t.kd_dokter "
                + "left join pegawai pgd on pgd.nik=t.kd_dokter "
                + "left join petugas pt on pt.nip=t.nip "
                + "left join pegawai pgp on pgp.nik=t.nip "
                + "where t.no_rawat=?";
        } else if (role.equals("dokter")) {
            sql = "select t.kd_jenis_prw, jp.nm_perawatan, t.tgl_perawatan, t.jam_rawat, t.biaya_rawat, "
                + "ifnull(pg.no_ktp,'') as ktp_praktisi, ifnull(d.nm_dokter,'') as nama_praktisi "
                + "from " + source + " t "
                + "inner join " + jnsTbl + " jp on jp.kd_jenis_prw=t.kd_jenis_prw "
                + "left join dokter d on d.kd_dokter=t.kd_dokter "
                + "left join pegawai pg on pg.nik=t.kd_dokter "
                + "where t.no_rawat=?";
        } else {
            sql = "select t.kd_jenis_prw, jp.nm_perawatan, t.tgl_perawatan, t.jam_rawat, t.biaya_rawat, "
                + "ifnull(pg.no_ktp,'') as ktp_praktisi, ifnull(pt.nama,'') as nama_praktisi "
                + "from " + source + " t "
                + "inner join " + jnsTbl + " jp on jp.kd_jenis_prw=t.kd_jenis_prw "
                + "left join petugas pt on pt.nip=t.nip "
                + "left join pegawai pg on pg.nik=t.nip "
                + "where t.no_rawat=?";
        }
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Tindakan t = new Tindakan();
                t.source = source;
                t.role = role;
                t.kdJenis = nz(r.getString("kd_jenis_prw"));
                t.namaPerawatan = nz(r.getString("nm_perawatan"));
                t.tgl = nz(r.getString("tgl_perawatan"));
                t.jam = nz(r.getString("jam_rawat"));
                t.biaya = nz(r.getString("biaya_rawat"));
                t.ktpPraktisi = nz(r.getString("ktp_praktisi"));
                t.namaPraktisi = nz(r.getString("nama_praktisi"));
                t.idPraktisi = nz(cek.tampilIDParktisi(t.ktpPraktisi));
                if (role.equals("drpr")) {
                    t.role = "dokter";                          // performer[0] = dokter
                    t.role2 = "perawat";                        // performer[1] = perawat
                    t.ktpPraktisi2 = nz(r.getString("ktp_praktisi2"));
                    t.namaPraktisi2 = nz(r.getString("nama_praktisi2"));
                    t.idPraktisi2 = nz(cek.tampilIDParktisi(t.ktpPraktisi2));
                }
                if (parseDouble(t.biaya) > 0) list.add(t);   // skip tindakan gratis/0
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilSumber (" + source + ") : " + e);
        }
    }

    /**
     * Obat yg punya KFA valid (non-BHP) -> ChargeItem obat, dari DUA sumber:
     * `detail_pemberian_obat` (obat selama dirawat) dan `resep_pulang` (obat pulang).
     * Keduanya ikut nota di DlgBilingRanap/DlgBilingRalan, jadi keduanya harus ditagihkan.
     */
    private List<Obat> ambilObat(String noRawat) {
        List<Obat> list = new ArrayList<>();
        ambilObatDari(list, noRawat, "obat", "detail_pemberian_obat", "jml", "tgl_perawatan", "jam");
        ambilObatDari(list, noRawat, "resep_pulang", "resep_pulang", "jml_barang", "tanggal", "jam");
        return list;
    }

    /**
     * Pembaca obat generik: tabel sumber berbeda kolom (jml vs jml_barang, tgl_perawatan vs tanggal)
     * tapi pola join KFA-nya identik. `sumber` ikut jadi kunci id resource supaya obat pulang
     * tidak bertabrakan dengan pemberian obat pada kode+tanggal+jam yang sama.
     */
    private void ambilObatDari(List<Obat> list, String noRawat, String sumber, String tbl,
            String kolJml, String kolTgl, String kolJam) {
        String sql = "select dpo.kode_brng, ifnull(db.nama_brng,'') as nama_brng, "
                + "dpo." + kolJml + " as jml, dpo.total, "
                + "dpo." + kolTgl + " as tgl_perawatan, dpo." + kolJam + " as jam, "
                + "m.obat_code, m.obat_system, m.obat_display "
                + "from " + tbl + " dpo "
                + "inner join satu_sehat_mapping_obat m on m.kode_brng=dpo.kode_brng "
                + "left join databarang db on db.kode_brng=dpo.kode_brng "
                + "where dpo.no_rawat=? and m.obat_code<>'' and m.obat_code not like 'BHP%'";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                Obat o = new Obat();
                o.sumber = sumber;
                o.kodeBrng = nz(r.getString("kode_brng"));
                o.namaBrng = nz(r.getString("nama_brng"));
                o.kfaCode = nz(r.getString("obat_code"));
                o.kfaSystem = nz(r.getString("obat_system"));
                o.kfaDisplay = nz(r.getString("obat_display"));
                o.jml = parseDouble(nz(r.getString("jml")));
                if (o.jml <= 0) o.jml = 1;
                o.total = parseDouble(nz(r.getString("total")));
                o.tgl = nz(r.getString("tgl_perawatan"));
                o.jam = nz(r.getString("jam"));
                if (o.total > 0 && !o.kfaCode.equals("")) list.add(o);   // skip obat gratis/0
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing ambilObat (" + tbl + ") : " + e);
        }
    }

    /** ChargeItem obat: code KFA, quantity = jml, tanpa performer (sesuai contoh SATUSEHAT). */
    private ObjectNode buatChargeItemObat(String noRawat, Konteks k, Obat o, String id, String idEncounter,
            String accId) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "ChargeItem");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/chargeitem/" + k.idOrg);
        iden.put("use", "official");
        iden.put("value", o.sumber + "-" + noRawat + "-" + o.kodeBrng + "-" + o.tgl + "-" + o.jam);
        r.put("status", "billable");
        ObjectNode code = r.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", o.kfaSystem.equals("") ? "http://sys-ids.kemkes.go.id/kfa" : o.kfaSystem);
        cc.put("code", o.kfaCode);
        cc.put("display", o.kfaDisplay);
        code.put("text", o.namaBrng.equals("") ? o.kfaDisplay : o.namaBrng);
        r.putObject("subject").put("reference", "Patient/" + k.idPasien);   // tanpa display (Rule 10226)
        r.putObject("context").put("reference", "Encounter/" + idEncounter);
        String waktu = formatWaktu(o.tgl + " " + o.jam);
        if (!waktu.equals("")) {
            ObjectNode period = r.putObject("occurrencePeriod");
            period.put("start", waktu);
            period.put("end", waktu);
        }
        int qty = (int) Math.round(o.jml);   // SATUSEHAT tolak quantity desimal (1.0) -> pakai integer
        if (qty <= 0) qty = 1;
        r.putObject("quantity").put("value", qty);
        r.putArray("account").addObject().put("reference", "Account/" + accId);
        double unit = o.jml > 0 ? o.total / o.jml : o.total;
        ArrayNode ext = r.putArray("extension");
        ObjectNode e1 = ext.addObject();
        e1.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/unitPrice");
        ObjectNode m1 = e1.putObject("valueMoney");
        m1.put("value", unit);
        m1.put("currency", "IDR");
        ObjectNode e2 = ext.addObject();
        e2.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/totalPrice");
        ObjectNode m2 = e2.putObject("valueMoney");
        m2.put("value", o.total);
        m2.put("currency", "IDR");
        return r;
    }

    /**
     * Ambil nama pasien terdaftar di SATUSEHAT lewat SEARCH by NIK
     * (GET Patient?identifier=nik|{nik}) -> entry[0].resource.name[0].text.
     * GET Patient/{id} tidak mengembalikan name (minimal), maka pakai search. "" bila gagal.
     */
    private String cariNamaPasien(String nik) {
        if (nik == null || nik.trim().equals("")) return "";
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            // String URL (bukan URI.create) supaya karakter '|' di-encode oleh RestTemplate, bukan ditolak.
            String url = link + "/Patient?identifier=https://fhir.kemkes.go.id/id/nik|" + nik.trim();
            String hasil = api.getRest().exchange(url, HttpMethod.GET, req, String.class).getBody();
            System.out.println("Billing search Patient by NIK raw : " + hasil);
            JsonNode r = mapper.readTree(hasil);
            for (JsonNode e : r.path("entry")) {
                JsonNode names = e.path("resource").path("name");
                if (names.isArray() && names.size() > 0) {
                    JsonNode n0 = names.get(0);
                    String text = nz(n0.path("text").asText());
                    if (!text.equals("")) return text;
                    StringBuilder sb = new StringBuilder();
                    for (JsonNode g : n0.path("given")) {
                        if (sb.length() > 0) sb.append(" ");
                        sb.append(g.asText());
                    }
                    String family = nz(n0.path("family").asText());
                    if (!family.equals("")) {
                        if (sb.length() > 0) sb.append(" ");
                        sb.append(family);
                    }
                    if (sb.length() > 0) return sb.toString().trim();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Billing cariNamaPasien : " + e);
        }
        return "";
    }

    /** Bersihkan nama lokal: buang gelar setelah koma + rapikan spasi (mendekati nama terdaftar SATUSEHAT). */
    private String bersihNama(String nama) {
        if (nama == null) return "";
        String t = nama;
        int koma = t.indexOf(',');
        if (koma > 0) t = t.substring(0, koma);
        return t.replaceAll("\\s+", " ").trim();
    }

    /**
     * Cari kode KPTL (code/display/system) untuk sebuah tindakan billing dari tabel mapping
     * per-kategori. Tindakan billing bersumber dari rawat_jl_* (ralan) & rawat_inap_* (ranap),
     * keyed kd_jenis_prw, sehingga referensinya diambil dari:
     *   - satu_sehat_mapping_tindakanranap_kptl  (bila source rawat_inap_*),
     *   - satu_sehat_mapping_tindakanralan_kptl   (selainnya / rawat_jl_*).
     * null bila kd_jenis_prw belum dipetakan atau kolom code-nya kosong — pemanggil
     * WAJIB melewati tindakannya (lihat resolveKptl).
     */
    private String[] lookupKptl(String kdJenis, String source) {
        String tabel, kolom = "kd_jenis_prw";
        switch (source) {
            case "lab":       tabel = "satu_sehat_mapping_tindakanlab_kptl"; break;
            case "radiologi": tabel = "satu_sehat_mapping_tindakanradiologi_kptl"; break;
            case "kamar":     tabel = "satu_sehat_mapping_kamar_kptl"; kolom = "kd_kamar"; break;   // key = kd_kamar
            case "operasi":   tabel = "satu_sehat_mapping_tindakanoperasi_kptl"; kolom = "kode_paket"; break;
            default:          tabel = source.contains("inap")
                                    ? "satu_sehat_mapping_tindakanranap_kptl"
                                    : "satu_sehat_mapping_tindakanralan_kptl";
        }
        String[] out = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select code, display, system from " + tabel + " where trim(" + kolom + ")=?");
            p.setString(1, kdJenis.trim());
            ResultSet r = p.executeQuery();
            if (r.next()) {
                String kode = nz(r.getString("code")).trim();
                if (!kode.equals("")) {
                    String d = nz(r.getString("display")).trim();
                    String s = nz(r.getString("system")).trim();
                    out = new String[]{kode, d, s.equals("") ? DEFAULT_KPTL_SYSTEM : s};
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Billing lookupKptl : " + e);
        }
        return out;
    }

    /**
     * Master KPTL (kode -> display) dari berkas cache/kptl.iyem, dimuat sekali per proses.
     *
     * Berkasnya dicari lewat cariBerkasCache(), BUKAN dengan path relatif "./cache/...":
     * working directory aplikasi tidak selalu folder Khanza, dan itulah yang bikin master
     * gagal dimuat lalu validasi diam-diam mati.
     *
     * Map kosong (berkas tak ketemu / tak terbaca) = validasi kode DIMATIKAN, bukan menolak
     * semuanya: lebih baik kembali ke perilaku lama daripada seluruh tindakan ikut terlewat.
     */
    private static synchronized java.util.Map<String, String> masterKptl() {
        if (masterKptl != null) return masterKptl;
        java.util.Map<String, String> peta = new java.util.HashMap<>();
        java.io.File berkas = cariBerkasCache("kptl.iyem");
        if (berkas == null) {
            System.out.println("Notifikasi Billing masterKptl : cache/kptl.iyem tak ditemukan "
                    + "(dicari dari working dir, folder jar & induknya, serta 1 level di bawah working dir) "
                    + "=> validasi kode KPTL DIMATIKAN. Salin folder cache ke folder aplikasi "
                    + "supaya kode tak sah tidak ikut terkirim.");
        } else {
            java.io.FileReader baca = null;
            try {
                baca = new java.io.FileReader(berkas);
                for (JsonNode n : new ObjectMapper().readTree(baca).path("kptl")) {
                    String kode = n.path("Code").asText() == null ? "" : n.path("Code").asText().trim();
                    if (kode.equals("")) continue;
                    String tampil = n.path("Display").asText() == null ? "" : n.path("Display").asText().trim();
                    peta.put(kode, tampil);
                }
                System.out.println("Billing master KPTL dari " + berkas.getPath()
                        + " : " + peta.size() + " kode.");
            } catch (Exception e) {
                System.out.println("Notifikasi Billing masterKptl : " + e
                        + " => validasi kode KPTL DIMATIKAN.");
            } finally {
                if (baca != null) try { baca.close(); } catch (Exception abaikan) {}
            }
        }
        masterKptl = peta;
        return masterKptl;
    }

    /**
     * Cari berkas di folder cache tanpa bergantung working directory: dicoba dari
     * working dir, folder tempat jar/kelas berada beserta induknya, lalu satu level
     * ke bawah dari working dir (pola yang sama dipakai validasi.MyReport untuk report/).
     * null bila tak ketemu di mana pun.
     */
    private static java.io.File cariBerkasCache(String namaBerkas) {
        List<java.io.File> kandidat = new ArrayList<>();
        kandidat.add(new java.io.File("./cache/" + namaBerkas));
        try {
            java.io.File asal = new java.io.File(SatuSehatBilling.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            java.io.File basis = asal.isFile() ? asal.getParentFile() : asal;   // dist/khanza.jar -> dist/
            for (int i = 0; basis != null && i < 3; i++) {
                kandidat.add(new java.io.File(basis, "cache/" + namaBerkas));
                basis = basis.getParentFile();
            }
        } catch (Exception abaikan) {
            // lokasi jar tak terbaca (mis. dijalankan lewat classloader khusus) -> cukup kandidat lain
        }
        java.io.File[] isi = new java.io.File(System.getProperty("user.dir")).listFiles();
        if (isi != null) {
            for (java.io.File d : isi) {
                if (d.isDirectory()) kandidat.add(new java.io.File(d, "cache/" + namaBerkas));
            }
        }
        for (java.io.File f : kandidat) {
            if (f.isFile()) return f;
        }
        return null;
    }

    /**
     * KPTL siap pakai untuk satu tindakan, atau null bila tindakan itu HARUS dilewati:
     * belum dipetakan, atau kodenya tidak ada di master KPTL.
     *
     * Alasan melewati, bukan memaksakan kode default: Account + seluruh ChargeItem tindakan
     * dikirim dalam SATU transaction yang atomik, jadi satu kode tak sah membuat SATUSEHAT
     * menolak seluruh billing kunjungan itu (RuleNumber 10216 lalu 20013 beruntun) — termasuk
     * obat & Invoice yang batal terkirim. Melewati 1 item jauh lebih murah daripada kehilangan
     * semuanya.
     */
    private String[] resolveKptl(Tindakan t) {
        String[] kptl = t.kptlCode.trim().equals("")
                ? lookupKptl(t.kdJenis, t.source)
                : new String[]{t.kptlCode.trim(), t.kptlDisplay, t.kptlSystem};
        if (kptl == null) {
            System.out.println("Billing LEWATI (belum dipetakan KPTL) : " + t.source + " / "
                    + t.kdJenis + " - " + t.namaPerawatan);
            return null;
        }
        java.util.Map<String, String> master = masterKptl();
        if (!master.isEmpty() && !master.containsKey(kptl[0])) {
            System.out.println("Billing LEWATI (kode KPTL '" + kptl[0] + "' tidak ada di master) : "
                    + t.source + " / " + t.kdJenis + " - " + t.namaPerawatan);
            return null;
        }
        // display kosong di tabel mapping -> pakai display resmi master supaya tidak terkirim kosong.
        if (kptl[1] == null || kptl[1].trim().equals("")) kptl[1] = nz(master.get(kptl[0]));
        if (kptl[2] == null || kptl[2].trim().equals("")) kptl[2] = DEFAULT_KPTL_SYSTEM;
        return kptl;
    }

    private void simpan(String tabel, String kolom, String noRawat, String id) {
        try {
            Sequel.queryu2("replace into " + tabel + " (" + kolom + ") values (?,?)", 2, new String[]{noRawat, id});
        } catch (Exception e) {
            System.out.println("Notifikasi Billing simpan (" + tabel + ") : " + e);
        }
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

    private double parseDouble(String s) {
        if (s == null) return 0;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
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
