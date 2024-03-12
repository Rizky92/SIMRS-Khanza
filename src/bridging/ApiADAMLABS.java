package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author RS SMC
 */
public class ApiADAMLABS
{
    private final String APIURL = koneksiDB.ADAMLABSAPIURL(),
                         APIKEY = koneksiDB.ADAMLABSAPIKEY(),
                         APIKODERS = koneksiDB.ADAMLABSAPIKODERS();
    
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private final ObjectMapper obj = new ObjectMapper();
    
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private JsonNode response;
    private String jsonBuilder;
    private PreparedStatement ps, psdetail;
    private ResultSet rs, rsdetail;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private SecretKeySpec secretKey;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private String url;
    
    private boolean sukses = false;
    
    public void registrasi(String kodeRegistrasi)
    {
        System.out.println("No. Order : " + kodeRegistrasi);
        try {
            ps = koneksi.prepareStatement(
                "select " +
                    "permintaan_lab.noorder, " +
                    "permintaan_lab.diagnosa_klinis, " +
                    "pasien.nm_pasien, " +
                    "pasien.no_rkm_medis, " +
                    "pasien.jk, " +
                    "pasien.alamat, " +
                    "pasien.no_tlp, " +
                    "pasien.tgl_lahir, " +
                    "pasien.no_ktp, " +
                    "(select pemeriksaan_ralan.berat from pemeriksaan_ralan where (pemeriksaan_ralan.berat is not null and pemeriksaan_ralan.berat != '' and pemeriksaan_ralan.berat REGEXP '^[0-9,.]+$') " +
                    "and pemeriksaan_ralan.no_rawat in (select r2.no_rawat from reg_periksa r2 where r2.no_rkm_medis = reg_periksa.no_rkm_medis) " +
                    "order by pemeriksaan_ralan.no_rawat desc, pemeriksaan_ralan.tgl_perawatan desc, pemeriksaan_ralan.jam_rawat desc limit 1) as bb, " +
                    "if (permintaan_lab.informasi_tambahan like '%cito%', 'Cito', 'Reguler') as jenis_registrasi, " +
                    "pasien.kd_prop, " +
                    "propinsi.nm_prop, " +
                    "pasien.kd_kab, " +
                    "kabupaten.nm_kab, " +
                    "pasien.kd_kec, " +
                    "kecamatan.nm_kec, " +
                    "permintaan_lab.dokter_perujuk as kd_dokter_perujuk, " +
                    "dokter_perujuk.nm_dokter as nm_dokter_perujuk, " +
                    "reg_periksa.kd_poli, " +
                    "poliklinik.nm_poli, " +
                    "reg_periksa.kd_pj, " +
                    "penjab.png_jawab " +
                "from permintaan_lab " +
                "join reg_periksa on permintaan_lab.no_rawat = reg_periksa.no_rawat " +
                "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "left join propinsi on pasien.kd_prop = propinsi.kd_prop " +
                "left join kabupaten on pasien.kd_kab = kabupaten.kd_kab " +
                "left join kecamatan on pasien.kd_kec = kecamatan.kd_kec " +
                "join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli " +
                "join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
                "join dokter dokter_perujuk on permintaan_lab.dokter_perujuk = dokter_perujuk.kd_dokter " +
                "where permintaan_lab.noorder = ?"
            );
            try {
                ps.setString(1, kodeRegistrasi);
                rs = ps.executeQuery();
                if (rs.next()) {
                    jsonBuilder = "{" +
                            "\"registrasi\": {" +
                                "\"no_registrasi\": \"" + rs.getString("noorder") + "\"," +
                                "\"diagnosa_awal\": \"-\"," +
                                "\"keterangan_klinis\": \"" + rs.getString("diagnosa_klinis") + "\"," +
                                "\"kode_rs\": \"" + APIKODERS + "\"" +
                            "}," +
                            "\"pasien\": {" +
                                "\"nama\": \"" + rs.getString("nm_pasien") + "\"," +
                                "\"no_rm\": \"" + rs.getString("no_rkm_medis") + "\"," +
                                "\"jenis_kelamin\": \"" + rs.getString("jk") + "\"," +
                                "\"alamat\": \"" + rs.getString("alamat") + "\"," +
                                "\"no_telphone\": \"" + rs.getString("no_tlp") + "\"," +
                                "\"tanggal_lahir\": \"" + df.format(rs.getDate("tgl_lahir")) + "\"," +
                                "\"nik\": \"" + rs.getString("no_ktp") +"\"," +
                                "\"ras\": \"-\"," +
                                "\"berat_badan\": \"" + rs.getString("bb").toLowerCase().trim() + "kg\"," +
                                "\"jenis_registrasi\" : \"" + rs.getString("jenis_registrasi") + "\"," +
                                "\"m_provinsi_id\": \"" + rs.getString("nm_prop") + "\"," +
                                "\"m_kabupaten_id\": \"" + rs.getString("nm_kab") + "\"," +
                                "\"m_kecamatan_id\": \"" + rs.getString("nm_kec") + "\"" +
                            "}," +
                            "\"kode_dokter_pengirim\": \"" + rs.getString("kd_dokter_perujuk") + "\"," +
                            "\"nama_dokter_pengirim\": \"" + rs.getString("nm_dokter_perujuk") + "\"," +
                            "\"kode_unit_asal\": \"" + rs.getString("kd_poli") + "\"," +
                            "\"nama_unit_asal\": \"" + rs.getString("nm_poli") + "\"," +
                            "\"kode_penjamin\": \"" + rs.getString("kd_pj") + "\"," +
                            "\"nama_penjamin\": \"" + rs.getString("png_jawab") + "\"," +
                            "\"kode_icdt\": \"-\"," +
                            "\"nama_icdt\": \"-\"," +
                            "\"tindakan\": [";
                    try {
                        psdetail = koneksi.prepareStatement(
                            "select permintaan_pemeriksaan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan " +
                            "from permintaan_pemeriksaan_lab " +
                            "join jns_perawatan_lab on permintaan_pemeriksaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw " +
                            "where permintaan_pemeriksaan_lab.noorder = ?"
                        );
                        try {
                            psdetail.setString(1, kodeRegistrasi);
                            rsdetail = psdetail.executeQuery();
                            while (rsdetail.next()) {
                                jsonBuilder = jsonBuilder + "{" +
                                    "\"kode_tindakan\": \"" + rsdetail.getString("kd_jenis_prw") + "\"," +
                                    "\"nama_tindakan\": \"" + rsdetail.getString("nm_perawatan") + "\"" +
                                "},";
                            }
                            jsonBuilder = jsonBuilder.substring(0, jsonBuilder.length() - 1);
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                        } finally {
                            if (rsdetail != null) {
                                rsdetail.close();
                            }
                            if (psdetail != null) {
                                psdetail.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    jsonBuilder = jsonBuilder + "]}";
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            try {
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
                    "noorder, url, method, request, code, response, user_id",
                    kodeRegistrasi, url, "POST", jsonBuilder, String.valueOf(responseEntity.getStatusCode()), responseEntity.getBody(), akses.getkode()
                );
                if (response.path("code").asText().equals("200")) {
                    Sequel.menyimpanSmc("adamlabs_orderlab", null, kodeRegistrasi, response.path("registrasi").path("no_lab").asText());
                }
            } catch (HttpClientErrorException e) {
                System.out.println(e.getResponseBodyAsString());
            } catch (IOException | KeyManagementException | NoSuchAlgorithmException | RestClientException e) {
                System.out.println("Response : " + e.getMessage());
                System.out.println("Notif : " + e);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void tarikHasilLab()
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void tarikHasilLab(String kodeRegistrasi)
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void updateHasilLab()
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private RestTemplate http() throws NoSuchAlgorithmException, KeyManagementException
    {
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
