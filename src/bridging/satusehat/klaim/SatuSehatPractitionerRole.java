/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.akses;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sender PractitionerRole SATUSEHAT — MASTER DATA per dokter, bukan sub-sender bundle.
 *
 * PractitionerRole tidak berubah per pasien, jadi sengaja TIDAK dipanggil dari SatuSehatBundle.
 * Dikirim sekali per dokter dari DlgDokter.
 *
 * SEMUA datanya menempel di master `dokter` (tidak ada tabel pendamping): no SIP dari
 * `no_ijn_praktek`, email dari `email`, active dari `status`, sisanya kolom tambahan
 * `jenis_sip`, `sip_mulai/akhir`, `role_mulai/akhir`, `kode_role`, `kode_spesialis`,
 * `id_practitionerrole`, `tgl_kirim_role`, `status_role`.
 *
 * Alurnya mengikuti diagram "Pengecekan SSM Practitioner eligible untuk TTE di RME Faskes":
 * GET Practitioner by NIK -> GET PractitionerRole -> ketemu ? simpan id-nya : POST.
 * PractitionerRole adalah PRASYARAT TTE, entry point pengecekannya {@link #idRoleUntukTte(String)}.
 */
public class SatuSehatPractitionerRole {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    //Dua master memakai sender yang sama: `dokter` (kd_dokter/nm_dokter) dan
    //`petugas` (nip/nama). Nama kolom PractitionerRole-nya sengaja disamakan di kedua tabel.
    private final String tabel, kolomKode, kolomNama;

    /** Sumber master `dokter`. */
    public SatuSehatPractitionerRole() {
        this("dokter");
    }

    /** @param sumber "dokter" atau "petugas". */
    public SatuSehatPractitionerRole(String sumber) {
        if ("petugas".equals(sumber)) {
            tabel = "petugas";
            kolomKode = "nip";
            kolomNama = "nama";
        } else {
            tabel = "dokter";
            kolomKode = "kd_dokter";
            kolomNama = "nm_dokter";
        }
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi PractitionerRole : " + e);
        }
    }

    /** Data satu PractitionerRole, semuanya dari master dokter. */
    public static class RoleData {
        public String kdDokter = "", nmDokter = "", nikDokter = "";
        public String idPractitioner = "", idOrg = "", namaOrg = "";
        public String jenisSip = "sip_reguler", noSip = "";
        public String sipMulai = "", sipAkhir = "", mulai = "", akhir = "";
        public String kodeRole = "", namaRole = "", kodeSpesialis = "", namaSpesialis = "";
        public String email = "", idRole = "";
        public boolean aktif = true;
    }

    /** Hasil satu kali kirim. */
    public static class Hasil {
        public String id = "";
        /** true = id didapat dari GET (role sudah ada), false = baru dibuat lewat POST. */
        public boolean sudahAda = false;
    }

    /**
     * Tombol "Cek (GET)". Hanya melihat ke SATUSEHAT dan menyimpan id-nya kalau ketemu —
     * TIDAK pernah membuat apa pun. "" berarti belum ada.
     */
    public String cek(String kdDokter) throws Exception {
        RoleData d = siap(kdDokter);
        String idServer = cariIdDiServer(d.idPractitioner, d.idOrg);
        if (idServer.equals("")) {
            simpanStatus(kdDokter, "", "Belum ada di SATUSEHAT", false);
        } else {
            simpanStatus(kdDokter, idServer, "Sudah ada di SATUSEHAT", true);
        }
        return idServer;
    }

    /**
     * Tombol "Kirim (POST)". Tetap cek dulu meski tombolnya terpisah — POST tanpa cek adalah
     * cara melahirkan role kembar, karena PractitionerRole tak punya identifier untuk
     * conditional-create. Ketemu -> id disimpan, tidak jadi POST. Belum ada -> baru POST.
     */
    public Hasil kirim(String kdDokter) throws Exception {
        RoleData d = siap(kdDokter);

        String idServer = cariIdDiServer(d.idPractitioner, d.idOrg);

        Hasil h = new Hasil();
        if (!idServer.equals("")) {
            h.id = idServer;
            h.sudahAda = true;
            simpanStatus(kdDokter, idServer, "Sudah ada di SATUSEHAT", true);
            return h;
        }

        // Belum ada, baru dibuat. Di sinilah data wajib harus lengkap.
        String kurang = kurang(d);
        if (!kurang.equals("")) {
            simpanStatus(kdDokter, "", kurang.length() > 60 ? kurang.substring(0, 60) : kurang, false);
            throw new Exception(kurang);
        }
        h.id = kirimBody(kdDokter, d, "", HttpMethod.POST, link + "/PractitionerRole", "Terkirim (baru)");
        h.sudahAda = false;
        return h;
    }

    /** Data dokter yang syarat minimal pencariannya sudah terpenuhi (IHS praktisi + Organization). */
    private RoleData siap(String kdDokter) throws Exception {
        RoleData d = ambilData(kdDokter);
        if (d == null) {
            throw new Exception("Data " + kdDokter + " tidak ditemukan di master " + tabel + ".");
        }
        if (d.idPractitioner.equals("")) {
            throw new Exception("IHS Practitioner " + d.nmDokter + " belum ditemukan"
                    + (d.nikDokter.matches("\\d{16}") ? "."
                            : " (NIK di data pegawai bukan 16 digit: '" + d.nikDokter + "')."));
        }
        if (d.idOrg.equals("")) {
            throw new Exception("IDSATUSEHAT (Organization) belum diset di setting koneksi.");
        }
        return d;
    }

    /**
     * PUT ke role yang sudah ada — dipakai saat SIP diperpanjang atau datanya berubah.
     * Id-nya tetap diambil ulang dari server, bukan dari id lokal.
     */
    public String perbarui(String kdDokter) throws Exception {
        RoleData d = ambilData(kdDokter);
        if (d == null) {
            throw new Exception("Data " + kdDokter + " tidak ditemukan di master " + tabel + ".");
        }
        String kurang = kurang(d);
        if (!kurang.equals("")) {
            throw new Exception(kurang);
        }
        String idServer = cariIdDiServer(d.idPractitioner, d.idOrg);
        if (idServer.equals("")) {
            throw new Exception("PractitionerRole dokter ini belum ada di SATUSEHAT. Kirim dulu.");
        }
        return kirimBody(kdDokter, d, idServer, HttpMethod.PUT,
                link + "/PractitionerRole/" + idServer, "Terkirim (perbarui)");
    }

    private String kirimBody(String kdDokter, RoleData d, String idServer, HttpMethod method,
            String url, String statusSukses) throws Exception {
        String payload = mapper.writeValueAsString(buatBody(d, idServer));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(StandardCharsets.UTF_8), headers);

        System.out.println("URL PractitionerRole : " + method + " " + url);
        System.out.println("Request JSON PractitionerRole : " + payload);

        String hasil;
        try {
            hasil = api.getRest().exchange(url, method, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String bodyErr = ex.getResponseBodyAsString();
            System.out.println("Error PractitionerRole Status Code: " + ex.getStatusCode());
            System.out.println("Error PractitionerRole Body: " + bodyErr);
            simpanStatus(kdDokter, d.idRole, "Gagal " + ex.getStatusCode(), false);
            throw new Exception("Ditolak SATUSEHAT (" + ex.getStatusCode() + ") : " + ringkasError(bodyErr));
        }
        System.out.println("Result JSON PractitionerRole : " + hasil);

        String idBaru = "";
        try {
            idBaru = mapper.readTree(hasil).path("id").asText();
        } catch (Exception e) {
            System.out.println("Notifikasi PractitionerRole baca hasil : " + e);
        }
        if (idBaru.equals("")) {
            idBaru = idServer;
        }
        simpanStatus(kdDokter, idBaru, statusSukses, true);
        return idBaru;
    }

    /**
     * Id PractitionerRole yang dipakai sebagai syarat TTE. HANYA membaca: cache lokal dulu,
     * kalau kosong tanya server. TIDAK pernah membuat role baru — pembuatan hanya lewat
     * {@link #kirim(String)} yang dipicu petugas dari DlgDokter.
     * Mengembalikan "" bila dokter belum punya PractitionerRole.
     */
    public String idRoleUntukTte(String kdDokter) {
        String id = nz(Sequel.cariIsi("select id_practitionerrole from " + tabel
                + " where " + kolomKode + "='" + kdDokter + "'"));
        if (!id.trim().equals("")) {
            return id.trim();
        }
        try {
            RoleData d = ambilData(kdDokter);
            if (d == null || d.idPractitioner.equals("") || d.idOrg.equals("")) {
                return "";
            }
            id = cariIdDiServer(d.idPractitioner, d.idOrg);
            if (!id.equals("")) {
                simpanStatus(kdDokter, id, "Sudah ada di SATUSEHAT", true);
            }
            return id;
        } catch (Exception e) {
            System.out.println("Notifikasi PractitionerRole idRoleUntukTte : " + e);
            return "";
        }
    }

    /**
     * GET PractitionerRole milik seorang praktisi di organisasi ini.
     *
     * URL-nya sengaja menyimpang dari kebiasaan FHIR, mengikuti koleksi resmi Kemkes:
     * ada garis miring sebelum '?', parameter "Organization" berhuruf besar, dan nilainya
     * id TELANJANG (bukan "Organization/xxx"). Jangan "dirapikan" jadi bentuk baku.
     *
     * "" berarti memang belum ada (balasan kosong atau 404) -> pemanggil boleh POST.
     * Kalau GET-nya sendiri yang bermasalah (401/403/5xx/jaringan), melempar Exception dan
     * pengiriman DIBATALKAN — jangan jatuh ke POST, itu cara melahirkan role kembar.
     */
    public String cariIdDiServer(String idPractitioner, String idOrg) throws Exception {
        String url = link + "/PractitionerRole/?Organization=" + idOrg + "&practitioner=" + idPractitioner;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        HttpEntity requestEntity = new HttpEntity(headers);
        System.out.println("URL Cek PractitionerRole : " + url);
        String json;
        try {
            json = api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 404) {
                System.out.println("Cek PractitionerRole : 404, dianggap belum ada.");
                return "";
            }
            System.out.println("Error Cek PractitionerRole : " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
            throw new Exception("Gagal mengecek PractitionerRole di SATUSEHAT (" + ex.getStatusCode()
                    + "). Pengiriman dibatalkan supaya tidak membuat role kembar.");
        } catch (HttpServerErrorException ex) {
            System.out.println("Error Cek PractitionerRole : " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
            throw new Exception("SATUSEHAT sedang bermasalah (" + ex.getStatusCode()
                    + ") saat cek PractitionerRole. Pengiriman dibatalkan.");
        } catch (Exception ex) {
            throw new Exception("Gagal menghubungi SATUSEHAT saat cek PractitionerRole : " + ex.getMessage()
                    + ". Pengiriman dibatalkan.");
        }
        System.out.println("JSON Cek PractitionerRole : " + json);
        try {
            JsonNode root = mapper.readTree(json);
            for (JsonNode entry : root.path("entry")) {
                String id = entry.path("resource").path("id").asText();
                if (!id.equals("")) {
                    return id;
                }
            }
        } catch (Exception e) {
            throw new Exception("Balasan cek PractitionerRole tidak terbaca : " + e.getMessage()
                    + ". Pengiriman dibatalkan.");
        }
        return "";
    }

    /** Pesan data yang belum layak kirim; "" bila sudah lengkap. */
    public String kurang(RoleData d) {
        if (d.idPractitioner.equals("")) {
            return "IHS Practitioner " + d.nmDokter + " belum ditemukan"
                    + (d.nikDokter.matches("\\d{16}") ? "." : " (NIK di data pegawai bukan 16 digit: '" + d.nikDokter + "').");
        }
        if (d.idOrg.equals("")) {
            return "IDSATUSEHAT (Organization) belum diset di setting koneksi.";
        }
        // no_ijn_praktek di master banyak yang berisi teks ("SUDAH KELUAR", "0", "-"), bukan nomor
        // SIP. Syarat 3 digit berurutan mencegah teks itu terkirim sebagai identifier resmi.
        if (d.noSip.trim().equals("") || !d.noSip.matches(".*\\d{3}.*")) {
            return "No.Ijin Praktek '" + d.noSip + "' bukan nomor SIP. Perbaiki dulu di master " + tabel + ".";
        }
        if (d.sipMulai.equals("") || d.sipAkhir.equals("")) {
            return "Periode SIP belum diisi.";
        }
        if (d.mulai.equals("") || d.akhir.equals("")) {
            return "Periode PractitionerRole belum diisi.";
        }
        if (d.sipAkhir.compareTo(d.sipMulai) < 0) {
            return "Tanggal akhir SIP mendahului tanggal mulai.";
        }
        if (d.akhir.compareTo(d.mulai) < 0) {
            return "Tanggal akhir role mendahului tanggal mulai.";
        }
        if (d.kodeRole.equals("") || d.namaRole.equals("")) {
            return "Kode profesi SNOMED belum diisi.";
        }
        if (d.kodeSpesialis.equals("") || d.namaSpesialis.equals("")) {
            return "Kode spesialis SNOMED belum diisi.";
        }
        if (d.email.trim().equals("")) {
            return "Email dokter belum diisi.";
        }
        return "";
    }

    private ObjectNode buatBody(RoleData d, String idServer) {
        ObjectNode role = mapper.createObjectNode();
        role.put("resourceType", "PractitionerRole");
        if (!idServer.equals("")) {
            role.put("id", idServer);
        }
        role.put("active", d.aktif);

        ObjectNode iden = role.putArray("identifier").addObject();
        iden.put("system", "https://fhir.kemkes.go.id/id/sip-number");
        iden.put("use", "official");
        iden.put("value", d.noSip.trim());
        ObjectNode tipe = iden.putObject("type").putArray("coding").addObject();
        tipe.put("system", "http://terminology.kemkes.go.id");
        tipe.put("code", d.jenisSip);
        tipe.put("display", namaJenisSip(d.jenisSip));
        ObjectNode periodeSip = iden.putObject("period");
        periodeSip.put("start", waktu(d.sipMulai));
        periodeSip.put("end", waktu(d.sipAkhir));

        ObjectNode periode = role.putObject("period");
        periode.put("start", waktu(d.mulai));
        periode.put("end", waktu(d.akhir));

        ObjectNode practitioner = role.putObject("practitioner");
        practitioner.put("reference", "Practitioner/" + d.idPractitioner);
        practitioner.put("display", d.nmDokter);

        ObjectNode organization = role.putObject("organization");
        organization.put("reference", "Organization/" + d.idOrg);
        organization.put("display", d.namaOrg);

        // code[] = PROFESI (bukan kualifikasi), specialty[] = spesialisasi.
        ObjectNode kode = role.putArray("code").addObject().putArray("coding").addObject();
        kode.put("system", "http://snomed.info/sct");
        kode.put("code", d.kodeRole);
        kode.put("display", d.namaRole);

        ObjectNode spesialis = role.putArray("specialty").addObject().putArray("coding").addObject();
        spesialis.put("system", "http://snomed.info/sct");
        spesialis.put("code", d.kodeSpesialis);
        spesialis.put("display", d.namaSpesialis);

        ObjectNode telecom = role.putArray("telecom").addObject();
        telecom.put("system", "email");
        telecom.put("use", "work");
        telecom.put("value", d.email.trim());

        return role;
    }

    /** Rakit body tanpa mengirim — untuk pratinjau / debug. */
    public String pratinjau(String kdDokter) throws Exception {
        RoleData d = ambilData(kdDokter);
        if (d == null) {
            return "";
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(buatBody(d, nz(d.idRole)));
    }

    public RoleData ambilData(String kdDokter) {
        RoleData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select dr." + kolomKode + " as kode, dr." + kolomNama + " as nama, "
                    + "ifnull(dr.no_ijn_praktek,'') as no_sip, "
                    + "ifnull(dr.email,'') as email, ifnull(pg.no_ktp,'') as nik, dr.status, "
                    + "ifnull(dr.jenis_sip,'sip_reguler') as jenis_sip, "
                    + "ifnull(dr.sip_mulai,'') as sip_mulai, ifnull(dr.sip_akhir,'') as sip_akhir, "
                    + "ifnull(dr.role_mulai,'') as role_mulai, ifnull(dr.role_akhir,'') as role_akhir, "
                    + "ifnull(dr.kode_role,'') as kode_role, ifnull(dr.nama_role,'') as nama_role, "
                    + "ifnull(dr.kode_spesialis,'') as kode_spesialis, "
                    + "ifnull(dr.nama_spesialis,'') as nama_spesialis, "
                    + "ifnull(dr.id_practitionerrole,'') as id_role "
                    + "from " + tabel + " dr left join pegawai pg on pg.nik=dr." + kolomKode + " "
                    + "where dr." + kolomKode + "=? limit 1");
            p.setString(1, kdDokter);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new RoleData();
                d.kdDokter = nz(r.getString("kode"));
                d.nmDokter = nz(r.getString("nama"));
                d.nikDokter = nz(r.getString("nik")).trim();
                d.noSip = nz(r.getString("no_sip"));
                d.email = nz(r.getString("email")).trim();
                d.jenisSip = nz(r.getString("jenis_sip")).equals("") ? "sip_reguler" : r.getString("jenis_sip");
                d.sipMulai = tanggal(nz(r.getString("sip_mulai")));
                d.sipAkhir = tanggal(nz(r.getString("sip_akhir")));
                d.mulai = tanggal(nz(r.getString("role_mulai")));
                d.akhir = tanggal(nz(r.getString("role_akhir")));
                d.kodeRole = nz(r.getString("kode_role")).trim();
                d.namaRole = nz(r.getString("nama_role")).trim();
                d.kodeSpesialis = nz(r.getString("kode_spesialis")).trim();
                d.namaSpesialis = nz(r.getString("nama_spesialis")).trim();
                d.idRole = nz(r.getString("id_role")).trim();
                // active PractitionerRole = status keaktifan dokter di master, tidak perlu kolom sendiri.
                d.aktif = !nz(r.getString("status")).equals("0");
                d.idOrg = nz(koneksiDB.IDSATUSEHAT()).trim();
                d.namaOrg = nz(akses.getnamars());
                d.idPractitioner = d.nikDokter.equals("") ? "" : nz(cek.tampilIDParktisi(d.nikDokter));
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi PractitionerRole ambilData : " + e);
        }
        return d;
    }

    private void simpanStatus(String kdDokter, String idRole, String status, boolean terkirim) {
        try {
            if (terkirim) {
                Sequel.queryu2("update " + tabel + " set id_practitionerrole=?, status_role=?, "
                        + "tgl_kirim_role=now() where " + kolomKode + "=?",
                        3, new String[]{nz(idRole), status, kdDokter});
            } else {
                Sequel.queryu2("update " + tabel + " set status_role=? where " + kolomKode + "=?",
                        2, new String[]{status, kdDokter});
            }
        } catch (Exception e) {
            System.out.println("Notifikasi PractitionerRole simpanStatus : " + e);
        }
    }

    /** "sip_reguler" (ejaan Indonesia, bukan sip_regular) / "sip_khusus". */
    public static String namaJenisSip(String kode) {
        return "sip_khusus".equals(kode) ? "SIP Khusus" : "SIP Reguler";
    }

    private String ringkasError(String body) {
        if (body == null) {
            return "";
        }
        try {
            JsonNode root = mapper.readTree(body);
            JsonNode issue = root.path("issue");
            if (issue.isArray() && issue.size() > 0) {
                String teks = issue.get(0).path("details").path("text").asText();
                if (teks.equals("")) {
                    teks = issue.get(0).path("diagnostics").asText();
                }
                if (!teks.equals("")) {
                    return teks;
                }
            }
        } catch (Exception e) {
            // biarkan, pakai body mentah
        }
        return body.length() > 300 ? body.substring(0, 300) : body;
    }

    /** yyyy-MM-dd saja; "" bila kosong / 0000-00-00. */
    private String tanggal(String dt) {
        if (dt == null) {
            return "";
        }
        String t = dt.trim();
        if (t.length() < 10) {
            return "";
        }
        t = t.substring(0, 10);
        if (t.startsWith("0000-00-00") || !t.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "";
        }
        return t;
    }

    /** Format waktu periode sesuai koleksi resmi: yyyy-MM-ddT00:00:00+00:00. */
    private String waktu(String tanggal) {
        return tanggal + "T00:00:00+00:00";
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
