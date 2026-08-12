/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Pembuat Encounter FASE AWAL: dikirim saat pendaftaran disimpan, jauh sebelum diagnosa ada.
 *
 * Encounter di sini sengaja berstatus "arrived" tanpa period.end dan TANPA diagnosis. Diuji ke
 * staging 1 Agustus 2026: SATUSEHAT menerimanya (HTTP 201) — RuleNumber 10457 (diagnosis wajib)
 * hanya berlaku untuk Encounter yang sudah "finished". Yang WAJIB ada sejak awal adalah
 * statusHistory (RuleNumber 10122) dan serviceProvider = IDSATUSEHAT milik RS (RuleNumber 10124);
 * classHistory tidak wajib.
 *
 * Fase akhir (status finished + period.end + diagnosis + Condition dkk) TETAP milik
 * satusehatklaim.SatuSehatBundle, yang memakai PUT ke id yang disimpan di sini. Kelas ini
 * sengaja TIDAK pernah mengubah Encounter yang sudah ada — ia hanya membuat yang belum ada.
 */
public class SatuSehatEncounterReg {

    /** Status Encounter saat pendaftaran: pasien sudah datang & terdaftar, belum dilayani. */
    private static final String STATUS_AWAL = "arrived";

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatEncounterReg() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi EncounterAwal : " + e);
        }
    }

    /** Hasil pengiriman, supaya pemanggil bisa memberi pesan yang tepat tanpa menebak. */
    public static class Hasil {
        public boolean berhasil = false;
        /** true bila Encounter memang sudah ada sebelumnya (bukan kegagalan). */
        public boolean sudahAda = false;
        public String idEncounter = "";
        public String pesan = "";
    }

    private static class Konteks {
        String ktpPasien = "", namaPasien = "", ktpDokter = "", namaDokter = "";
        String kdPoli = "", namaPoli = "", idLokasi = "", statusLanjut = "", mulai = "";
    }

    /**
     * Buat Encounter fase awal untuk satu No.Rawat. Aman dipanggil berkali-kali: bila id sudah
     * tercatat di satu_sehat_encounter, pengiriman dilewati.
     */
    public Hasil kirim(String noRawat) {
        Hasil hasil = new Hasil();
        noRawat = (noRawat == null) ? "" : noRawat.trim();
        if (noRawat.equals("")) {
            hasil.pesan = "No.Rawat kosong.";
            return hasil;
        }

        String idLama = nz(Sequel.cariIsi(
                "select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat)).trim();
        if (!idLama.equals("")) {
            hasil.berhasil = true;
            hasil.sudahAda = true;
            hasil.idEncounter = idLama;
            hasil.pesan = "Encounter sudah ada (" + idLama + "), tidak dikirim ulang.";
            return hasil;
        }

        Konteks k = ambilKonteks(noRawat);
        if (k == null) {
            hasil.pesan = "Data kunjungan No.Rawat " + noRawat + " tidak ditemukan.";
            return hasil;
        }
        if (k.idLokasi.equals("")) {
            hasil.pesan = "Poli " + k.kdPoli + " belum dipetakan ke Location SATUSEHAT "
                    + "(menu Mapping Lokasi).";
            return hasil;
        }
        if (k.mulai.equals("")) {
            hasil.pesan = "Tanggal/jam registrasi kosong.";
            return hasil;
        }

        String idPasien = nz(cek.tampilIDPasien(k.ktpPasien));
        if (idPasien.equals("")) {
            hasil.pesan = "IHS pasien belum ditemukan untuk NIK " + k.ktpPasien
                    + ". Cek lewat tombol Cek IHS di data pasien.";
            return hasil;
        }
        String idDokter = nz(cek.tampilIDParktisi(k.ktpDokter));
        if (idDokter.equals("")) {
            hasil.pesan = "IHS praktisi belum ditemukan untuk NIK dokter " + k.ktpDokter + ".";
            return hasil;
        }

        String idOrg = nz(koneksiDB.IDSATUSEHAT()).trim();
        if (idOrg.equals("")) {
            hasil.pesan = "IDSATUSEHAT kosong di setting/database.xml.";
            return hasil;
        }

        try {
            HasilPost post = postEncounter(buatEncounter(noRawat, k, idPasien, idDokter, idOrg),
                    idOrg, noRawat);
            if (post.id.equals("")) {
                hasil.pesan = "Encounter terkirim tetapi id tidak terbaca dari respons server.";
                return hasil;
            }
            simpanId(noRawat, post.id);
            hasil.berhasil = true;
            hasil.sudahAda = post.diadopsi;
            hasil.idEncounter = post.id;
            hasil.pesan = post.diadopsi
                    ? "Encounter sudah ada di SATUSEHAT (" + post.id + "), id-nya dicatat lokal."
                    : "Encounter dibuat (" + post.id + ").";
        } catch (HttpClientErrorException e) {
            // Body respons ikut dibawa: tanpa itu petugas hanya melihat "400 Bad Request" dan
            // tidak tahu aturan mana yang dilanggar.
            String detail = nz(e.getResponseBodyAsString());
            hasil.pesan = "Gagal kirim Encounter (HTTP " + e.getStatusCode().value() + ") : "
                    + (detail.equals("") ? e.toString() : detail);
            System.out.println("Notifikasi EncounterReg " + noRawat + " : " + hasil.pesan);
        } catch (Exception e) {
            hasil.pesan = "Gagal kirim Encounter : " + e;
            System.out.println("Notifikasi EncounterReg " + noRawat + " : " + e);
        }
        return hasil;
    }

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = null;
        PreparedStatement p = null;
        ResultSet r = null;
        try {
            p = koneksi.prepareStatement(
                    "select ifnull(ps.no_ktp,'') as ktp_pasien, ps.nm_pasien, "
                    + "ifnull(pg.no_ktp,'') as ktp_dokter, ifnull(pg.nama,'') as nama_dokter, "
                    + "rp.kd_poli, ifnull(pk.nm_poli,'') as nm_poli, rp.status_lanjut, "
                    + "ifnull(ml.id_lokasi_satusehat,'') as id_lokasi, "
                    + "concat(rp.tgl_registrasi,'T',rp.jam_reg,'+07:00') as mulai "
                    + "from reg_periksa rp "
                    + "inner join pasien ps on ps.no_rkm_medis=rp.no_rkm_medis "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter "
                    + "left join poliklinik pk on pk.kd_poli=rp.kd_poli "
                    + "left join satu_sehat_mapping_lokasi_ralan ml on ml.kd_poli=rp.kd_poli "
                    + "where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            r = p.executeQuery();
            if (r.next()) {
                k = new Konteks();
                k.ktpPasien = nz(r.getString("ktp_pasien")).trim();
                k.namaPasien = nz(r.getString("nm_pasien"));
                k.ktpDokter = nz(r.getString("ktp_dokter")).trim();
                k.namaDokter = nz(r.getString("nama_dokter"));
                k.kdPoli = nz(r.getString("kd_poli"));
                k.namaPoli = nz(r.getString("nm_poli"));
                k.statusLanjut = nz(r.getString("status_lanjut"));
                k.idLokasi = nz(r.getString("id_lokasi")).trim();
                k.mulai = nz(r.getString("mulai"));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi EncounterAwal ambilKonteks : " + e);
        } finally {
            tutup(r, p);
        }
        return k;
    }

    /**
     * Encounter fase awal. Strukturnya sengaja dijaga sama dengan
     * SatuSehatBundle.buatEncounter() supaya update di akhir kunjungan hanya menambah/mengganti
     * nilai, bukan mengubah bentuk resource.
     */
    private ObjectNode buatEncounter(String noRawat, Konteks k, String idPasien, String idDokter,
            String idOrg) {
        boolean igd = isPoliIgd(k.kdPoli, k.namaPoli);
        boolean ranap = k.statusLanjut.equalsIgnoreCase("Ranap");
        String classCode = ranap ? "IMP" : (igd ? "EMER" : "AMB");
        String classDisplay = ranap ? "inpatient encounter" : (igd ? "emergency" : "ambulatory");

        ObjectNode e = mapper.createObjectNode();
        e.put("resourceType", "Encounter");
        e.put("status", STATUS_AWAL);

        ObjectNode kelas = e.putObject("class");
        kelas.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
        kelas.put("code", classCode);
        kelas.put("display", classDisplay);

        ObjectNode subject = e.putObject("subject");
        subject.put("reference", "Patient/" + idPasien);
        subject.put("display", k.namaPasien);

        ObjectNode participant = e.putArray("participant").addObject();
        ObjectNode pCoding = participant.putArray("type").addObject().putArray("coding").addObject();
        pCoding.put("system", "http://terminology.hl7.org/CodeSystem/v3-ParticipationType");
        pCoding.put("code", "ATND");
        pCoding.put("display", "attender");
        ObjectNode individual = participant.putObject("individual");
        individual.put("reference", "Practitioner/" + idDokter);
        individual.put("display", k.namaDokter);

        // period.end sengaja tidak diisi: kunjungan masih berjalan.
        e.putObject("period").put("start", k.mulai);

        ObjectNode loc = e.putArray("location").addObject().putObject("location");
        loc.put("reference", "Location/" + k.idLokasi);
        loc.put("display", k.namaPoli);

        // statusHistory WAJIB (RuleNumber 10122), walau baru satu tahap.
        ArrayNode statusHistory = e.putArray("statusHistory");
        ObjectNode s0 = statusHistory.addObject();
        s0.put("status", STATUS_AWAL);
        ObjectNode per = s0.putObject("period");
        per.put("start", k.mulai);
        per.put("end", k.mulai);

        e.putObject("serviceProvider").put("reference", "Organization/" + idOrg);

        ObjectNode iden = e.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/encounter/" + idOrg);
        iden.put("value", noRawat);
        return e;
    }

    /**
     * POST Encounter dengan header If-None-Exist: bila Encounter ber-identifier No.Rawat ini
     * sudah ada di server (mis. kiriman sebelumnya gagal tercatat lokal), server mengembalikan
     * yang lama alih-alih membuat duplikat (RuleNumber 20002).
     */
    /** Hasil POST: id Encounter, plus penanda apakah id itu milik resource yang sudah ada. */
    private static class HasilPost {
        String id = "";
        boolean diadopsi = false;
    }

    private HasilPost postEncounter(ObjectNode body, String idOrg, String noRawat) throws Exception {
        String kriteria = "identifier=" + identifierEncounter(idOrg, noRawat);
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + api.TokenSatuSehat());
        h.add("If-None-Exist", kriteria);
        // Body dikirim sebagai byte UTF-8: Spring versi lama memakai ISO-8859-1 untuk String,
        // yang merusak nama ber-aksen menjadi tanda tanya.
        HttpEntity<byte[]> req = new HttpEntity<>(
                mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8), h);
        HasilPost hasil = new HasilPost();
        try {
            ResponseEntity<String> respon = api.getRest()
                    .exchange(link + "/Encounter", HttpMethod.POST, req, String.class);
            hasil.id = nz(mapper.readTree(respon.getBody()).path("id").asText());
            // 201 = benar-benar dibuat, 200 = If-None-Exist cocok ke satu Encounter yang sudah ada
            // dan server mengembalikannya tanpa membuat duplikat.
            hasil.diadopsi = respon.getStatusCode().value() != 201;
            return hasil;
        } catch (HttpClientErrorException e) {
            // SATUSEHAT tidak mengikuti spec FHIR untuk conditional create. Diuji 1 Agt 2026:
            //   0 kecocokan  -> 201 Created
            //   1 kecocokan  -> 400 "Found duplicate resource: Encounter" (RuleNumber 20002)
            //   >1 kecocokan -> 412 Precondition Failed
            // Dua kasus terakhir bukan kegagalan kirim: kunjungan ini memang sudah ada di
            // SATUSEHAT. Ambil id yang sudah ada supaya tercatat lokal dan fase akhir bisa PUT.
            // Bila memang tidak ada, error aslinya dilempar lagi apa adanya.
            hasil.id = cariEncounterByIdentifier(idOrg, noRawat);
            if (hasil.id.equals("")) {
                throw e;
            }
            hasil.diadopsi = true;
            return hasil;
        }
    }

    private String identifierEncounter(String idOrg, String noRawat) {
        return "http://sys-ids.kemkes.go.id/encounter/" + idOrg + "|" + noRawat;
    }

    /**
     * Cari Encounter yang sudah ada di server berdasarkan identifier No.Rawat. Mengembalikan id
     * pertama, dan mencatat peringatan bila ternyata lebih dari satu (duplikat di SATUSEHAT
     * perlu dibereskan manual — kelas ini tidak boleh menghapus data di server).
     */
    private String cariEncounterByIdentifier(String idOrg, String noRawat) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            // Identifier TIDAK di-encode manual: RestTemplate sudah meng-encode URL-nya sendiri,
            // sehingga pre-encoding menghasilkan double-encoding dan pencarian selalu kosong.
            String url = link + "/Encounter?identifier=" + identifierEncounter(idOrg, noRawat);
            String respon = api.getRest()
                    .exchange(url, HttpMethod.GET, new HttpEntity<>(h), String.class).getBody();
            JsonNode r = mapper.readTree(respon);
            int total = r.path("total").asInt(0);
            if (total > 1) {
                System.out.println("Notifikasi EncounterAwal : No.Rawat " + noRawat + " punya "
                        + total + " Encounter di SATUSEHAT (duplikat). Dipakai yang pertama; "
                        + "duplikatnya perlu dibereskan manual.");
            }
            for (JsonNode e : r.path("entry")) {
                String id = nz(e.path("resource").path("id").asText());
                if (!id.equals("")) {
                    return id;
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi EncounterAwal cariEncounterByIdentifier " + noRawat + " : " + e);
        }
        return "";
    }

    private void simpanId(String noRawat, String idEncounter) {
        Sequel.queryu2("replace into satu_sehat_encounter (no_rawat,id_encounter) values (?,?)",
                2, new String[]{noRawat, idEncounter});
    }

    /** Sama persis dengan SatuSehatBundle.isPoliIgd supaya kelas Encounter tidak berbeda antar fase. */
    private boolean isPoliIgd(String kodePoli, String namaPoli) {
        String kode = (kodePoli == null) ? "" : kodePoli.trim().toUpperCase();
        String nama = (namaPoli == null) ? "" : namaPoli.trim().toUpperCase();
        return kode.contains("IGD") || kode.contains("UGD") || kode.equals("ER") || kode.equals("EMER")
                || nama.contains("IGD") || nama.contains("UGD")
                || nama.contains("GAWAT DARURAT") || nama.contains("EMERGENCY");
    }

    private void tutup(ResultSet r, PreparedStatement p) {
        try {
            if (r != null) r.close();
        } catch (Exception e) {
            System.out.println("Notifikasi EncounterAwal tutup rs : " + e);
        }
        try {
            if (p != null) p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi EncounterAwal tutup ps : " + e);
        }
    }

    private String nz(String s) {
        return (s == null) ? "" : s;
    }
}
