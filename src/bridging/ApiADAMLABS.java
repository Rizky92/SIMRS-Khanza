package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author RS SMC
 */
public class ApiADAMLABS
{
    private final String APIURL = koneksiDB.ADAMLABSAPIURL(),
                         APIKEY = koneksiDB.ADAMLABSAPIKEY(),
                         APIKODERS = koneksiDB.ADAMLABSAPIKODERS(),
                         KECAMATANID = koneksiDB.ADAMLABSKECAMATANID(),
                         KABUPATENID = koneksiDB.ADAMLABSKABUPATENID(),
                         PROVINSIID = koneksiDB.ADAMLABSPROVINSIID();
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private final ObjectMapper obj = new ObjectMapper();
    
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private JsonNode response;
    private String jsonBuilder;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private String url;
    
    public void registrasi(String kodeRegistrasi) {
        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select permintaan_lab.noorder, permintaan_lab.diagnosa_klinis, pasien.nm_pasien, pasien.no_rkm_medis, pasien.jk, pasien.alamat, pasien.no_tlp, " +
                "pasien.tgl_lahir, trim(pasien.no_ktp) as no_ktp, if (permintaan_lab.status = 'ralan', ifnull ((select pemeriksaan_ralan.berat from pemeriksaan_ralan " +
                "where pemeriksaan_ralan.no_rawat = permintaan_lab.no_rawat order by pemeriksaan_ralan.tgl_perawatan desc limit 1), '-'), ifnull ((select pemeriksaan_ranap.berat " +
                "from pemeriksaan_ranap where pemeriksaan_ranap.no_rawat = permintaan_lab.no_rawat order by pemeriksaan_ranap.tgl_perawatan desc, pemeriksaan_ranap.jam_rawat desc " +
                "limit 1), '-')) as bb, if (permintaan_lab.informasi_tambahan like '%cito%', 'Cito', 'Reguler') as jenis_registrasi, ifnull (if (permintaan_lab.status = 'ralan', " +
                "concat(reg_periksa.kd_poli, '|', poliklinik.nm_poli), (select concat(kamar.kd_bangsal, '|', bangsal.nm_bangsal) from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar " +
                "join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = permintaan_lab.no_rawat order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc limit 1)), " +
                "'-|-') as asal_unit, ifnull ((select concat(diagnosa_pasien.kd_penyakit, '|', penyakit.nm_penyakit) from diagnosa_pasien join penyakit on diagnosa_pasien.kd_penyakit = penyakit.kd_penyakit " +
                "where diagnosa_pasien.no_rawat = permintaan_lab.no_rawat and diagnosa_pasien.status = permintaan_lab.status order by diagnosa_pasien.prioritas asc limit 1), '-|-') as icdt, " +
                "permintaan_lab.dokter_perujuk as kd_dokter_perujuk, dokter_perujuk.nm_dokter as nm_dokter_perujuk, reg_periksa.kd_pj, penjab.png_jawab from permintaan_lab " +
                "join reg_periksa on permintaan_lab.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join poliklinik " +
                "on reg_periksa.kd_poli = poliklinik.kd_poli join penjab on reg_periksa.kd_pj = penjab.kd_pj join dokter dokter_perujuk " +
                "on permintaan_lab.dokter_perujuk = dokter_perujuk.kd_dokter where permintaan_lab.noorder = ?"
            )) {
                ps.setString(1, kodeRegistrasi);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        jsonBuilder = "{"
                            + "\"registrasi\": {"
                            + "\"no_registrasi\": \"" + rs.getString("noorder") + "\","
                            + "\"diagnosa_awal\": \"-\","
                            + "\"keterangan_klinis\": \"" + rs.getString("diagnosa_klinis") + "\","
                            + "\"kode_rs\": \"" + APIKODERS + "\""
                            + "},"
                            + "\"pasien\": {"
                            + "\"nama\": \"" + rs.getString("nm_pasien") + "\","
                            + "\"no_rm\": \"" + rs.getString("no_rkm_medis") + "\","
                            + "\"jenis_kelamin\": \"" + rs.getString("jk") + "\","
                            + "\"alamat\": \"" + rs.getString("alamat") + "\","
                            + "\"no_telphone\": \"" + rs.getString("no_tlp") + "\","
                            + "\"tanggal_lahir\": \"" + df.format(rs.getDate("tgl_lahir")) + "\","
                            + "\"nik\": \"" + rs.getString("no_ktp") + "\","
                            + "\"ras\": \"-\","
                            + "\"berat_badan\": \"" + rs.getString("bb").toLowerCase().trim() + "kg\","
                            + "\"jenis_registrasi\" : \"" + rs.getString("jenis_registrasi") + "\","
                            + "\"m_provinsi_id\": \"" + PROVINSIID + "\","
                            + "\"m_kabupaten_id\": \"" + KABUPATENID + "\","
                            + "\"m_kecamatan_id\": \"" + KECAMATANID + "\""
                            + "},"
                            + "\"kode_dokter_pengirim\": \"" + rs.getString("kd_dokter_perujuk") + "\","
                            + "\"nama_dokter_pengirim\": \"" + rs.getString("nm_dokter_perujuk") + "\","
                            + "\"kode_unit_asal\": \"" + rs.getString("asal_unit").substring(0, rs.getString("asal_unit").indexOf("|")) + "\","
                            + "\"nama_unit_asal\": \"" + rs.getString("asal_unit").substring(rs.getString("asal_unit").indexOf("|") + 1) + "\","
                            + "\"kode_penjamin\": \"" + rs.getString("kd_pj") + "\","
                            + "\"nama_penjamin\": \"" + rs.getString("png_jawab") + "\","
                            + "\"kode_icdt\": \"" + rs.getString("icdt").substring(0, rs.getString("icdt").indexOf("|")) + "\","
                            + "\"nama_icdt\": \"" + rs.getString("icdt").substring(rs.getString("icdt").indexOf("|") + 1) + "\","
                            + "\"tindakan\": [";
                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select permintaan_pemeriksaan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan from permintaan_pemeriksaan_lab " +
                            "join jns_perawatan_lab on permintaan_pemeriksaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where permintaan_pemeriksaan_lab.noorder = ?"
                        )) {
                            ps2.setString(1, kodeRegistrasi);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    jsonBuilder = jsonBuilder + "{" +
                                        "\"kode_tindakan\": \"" + rs2.getString("kd_jenis_prw") + "\"," +
                                        "\"nama_tindakan\": \"" + rs2.getString("nm_perawatan") + "\"" +
                                    "},";
                                }
                                if (jsonBuilder.endsWith("},")) {
                                    jsonBuilder = jsonBuilder.substring(0, jsonBuilder.length() - 1);
                                }
                            }
                        }
                        jsonBuilder = jsonBuilder + "]}";
                    }
                }
            }
            url = APIURL + "/bridging_sim_rs/registrasi";
            System.out.println("URL : " + url);
            System.out.println("JSON : " + jsonBuilder);
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-api-key", APIKEY);
            requestEntity = new HttpEntity(jsonBuilder, headers);
            ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.POST, requestEntity, String.class);
            System.out.println("Response : " + responseEntity.getBody());
            System.out.println("Response : " + responseEntity.getStatusCode());
            response = obj.readTree(responseEntity.getBody());
            Sequel.menyimpanSmc(
                "adamlabs_request_response",
                "noorder, url, method, request, code, response, pengirim",
                kodeRegistrasi, url, "POST", jsonBuilder, String.valueOf(responseEntity.getStatusCode()), responseEntity.getBody(), akses.getkode()
            );
            if (response.path("status").asText().equals("200")) {
                Sequel.menyimpanSmc("adamlabs_orderlab", null, kodeRegistrasi, response.path("payload").path("registrasi").path("no_lab").asText());
                JOptionPane.showMessageDialog(null, "Order lab berhasil dikirim ke LIS ADAMLABS...!!!");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal kirim! Alasan: " + response.path("message").asText());
            }
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.getMessage().contains("HostException")) {
                System.out.println("Sambungan ke server ADAMLABS terputus!");
            }
        }
    }
    
    private RestTemplate http() throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("SSL");
        
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {return null;}
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme = new Scheme("https", 443, sslFactory);
        factory = new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        
        return new RestTemplate(factory);
    }
}
