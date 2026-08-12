package bridging.satusehat;

import fungsi.koneksiDB;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * Pencatat SEMUA lalu lintas HTTP ke SATUSEHAT ke tabel satu_sehat_log.
 *
 * Dipasang sekali sebagai interceptor di {@link ApiSatuSehat#getRest()}, sehingga seluruh
 * kelas pengirim (Encounter, Lab, Radiologi, Obat, Alkes, Billing, Resume, dst) otomatis
 * terekam TANPA perlu menyunting satu per satu. Sebelum ini hanya SatuSehatBundle yang
 * mencatat, sehingga penolakan pada sub-pengirim hilang tanpa jejak — persis yang terjadi
 * pada billing 28 Juli 2026 (Rule 10226).
 *
 * Nama layanan & no_rawat diambil dari {@link Konteks} yang di-set pemanggil (SatuSehatBundle
 * mengisinya di tiap langkah). Bila tidak di-set, tetap dicatat dengan nama seadanya —
 * lebih baik ada jejak daripada tidak sama sekali.
 *
 * Permintaan token SENGAJA tidak dicatat karena membawa client id/secret.
 */
public class SatuSehatHttpLogger implements ClientHttpRequestInterceptor {

    /**
     * Konteks per-thread: siapa yang sedang mengirim. Di-set oleh pemanggil sebelum menembak
     * API, dipakai interceptor untuk mengisi kolom service_name & no_rawat.
     */
    public static class Konteks {
        private static final ThreadLocal<String[]> DATA = new ThreadLocal<>();

        /** @param namaLayanan mis. "Lab", "Billing"; @param noRawat boleh kosong */
        public static void set(String namaLayanan, String noRawat) {
            DATA.set(new String[]{namaLayanan == null ? "" : namaLayanan,
                                  noRawat == null ? "" : noRawat});
        }

        public static void bersihkan() {
            DATA.remove();
        }

        static String namaLayanan() {
            String[] d = DATA.get();
            return d == null ? "" : d[0];
        }

        static String noRawat() {
            String[] d = DATA.get();
            return d == null ? "" : d[1];
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        String url = request.getURI().toString();
        // Jangan pernah mencatat permintaan token: badannya memuat client id & secret.
        boolean lewati = url.contains("accesstoken") || url.contains("oauth");

        long mulai = System.currentTimeMillis();
        ClientHttpResponse resp;
        try {
            resp = execution.execute(request, body);
        } catch (IOException | RuntimeException e) {
            if (!lewati) {
                catat(namaService(url, request), Konteks.noRawat(), url, utuh(new String(body, StandardCharsets.UTF_8)),
                        null, "", "FAILED", String.valueOf(e), (int) (System.currentTimeMillis() - mulai));
            }
            throw e;
        }
        if (lewati) {
            return resp;
        }

        int kode = 0;
        String isiRespon = "";
        try {
            kode = resp.getStatusCode().value();
            // Aman dibaca karena RestTemplate memakai BufferingClientHttpRequestFactory:
            // stream-nya sudah ditampung sehingga pemanggil tetap bisa membacanya lagi.
            isiRespon = StreamUtils.copyToString(resp.getBody(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            isiRespon = "(gagal membaca body : " + e + ")";
        }
        catat(namaService(url, request), Konteks.noRawat(), url,
                utuh(new String(body, StandardCharsets.UTF_8)), kode, utuh(isiRespon),
                kode >= 200 && kode < 300 ? "SUCCESS" : "FAILED", null,
                (int) (System.currentTimeMillis() - mulai));
        return resp;
    }

    /** "Lab", "Billing", dst dari konteks; bila kosong pakai potongan URL agar tetap terlacak. */
    private String namaService(String url, HttpRequest request) {
        String nama = Konteks.namaLayanan();
        if (nama.equals("")) {
            int p = url.lastIndexOf('/');
            nama = p >= 0 && p < url.length() - 1 ? url.substring(p + 1) : "SATUSEHAT";
            if (nama.length() > 30) nama = nama.substring(0, 30);
        }
        String metode = request.getMethod() == null ? "" : request.getMethod().name();
        String hasil = nama + (metode.equals("") ? "" : " " + metode);
        return hasil.length() > 50 ? hasil.substring(0, 50) : hasil;
    }

    /**
     * Payload & respons disimpan UTUH, tidak dipotong: log ini wajib disimpan sebagai bukti
     * audit sehingga isinya tidak boleh berkurang. Kolomnya longtext (sampai 4 GB) dan
     * max_allowed_packet server 256 MB, jauh di atas ukuran bundle FHIR mana pun.
     */
    private String utuh(String s) {
        return s == null ? "" : s;
    }

    /**
     * Catat panggilan HTTP yang TIDAK lewat RestTemplate, sehingga tidak tersentuh interceptor
     * ini — satu-satunya sekarang adalah PATCH Encounter.account di SatuSehatBilling, yang
     * terpaksa memakai Apache HttpClient langsung karena HttpMethod.PATCH belum ada di
     * spring-web yang menang di classpath project ini.
     *
     * Dibuat di sini, bukan sebagai insert sendiri di kelas pengirim, supaya skema kolom
     * satu_sehat_log tetap satu pintu dan sifat "gagal mencatat tidak boleh mengganggu
     * pengiriman" ikut terbawa.
     *
     * @param noRawat boleh dikosongkan; bila kosong diisi dari {@link Konteks} yang sedang aktif
     */
    public static void catatManual(String serviceName, String noRawat, String resourceId, String payload,
                                   Integer kode, String respon, String status, String error, Integer durasi) {
        catat(serviceName, (noRawat == null || noRawat.equals("")) ? Konteks.noRawat() : noRawat,
                resourceId, payload, kode, respon, status, error, durasi);
    }

    /** Kegagalan mencatat TIDAK BOLEH mengganggu pengiriman. */
    private static void catat(String serviceName, String noRawat, String resourceId, String payload,
                       Integer kode, String respon, String status, String error, Integer durasi) {
        PreparedStatement ps = null;
        try {
            Connection koneksi = koneksiDB.condb();
            if (koneksi == null) return;
            ps = koneksi.prepareStatement(
                    "insert into satu_sehat_log "
                    + "(service_name,no_rawat,resource_id,request_payload,response_code,response_body,status,error_message,duration_ms) "
                    + "values (?,?,?,?,?,?,?,?,?)");
            ps.setString(1, serviceName);
            ps.setString(2, noRawat);
            ps.setString(3, resourceId == null ? "" : (resourceId.length() > 100 ? resourceId.substring(0, 100) : resourceId));
            ps.setString(4, payload);
            if (kode == null) ps.setNull(5, java.sql.Types.INTEGER); else ps.setInt(5, kode);
            ps.setString(6, respon);
            ps.setString(7, status);
            ps.setString(8, error);
            if (durasi == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9, durasi);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi SatuSehatHttpLogger : " + e);
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ex) {}
        }
    }
}
