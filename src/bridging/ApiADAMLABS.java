package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.ArrayList;
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
import org.springframework.web.client.HttpServerErrorException;
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
    private final ObjectMapper mapper = new ObjectMapper();
    
    private HttpHeaders headers;
    private HttpEntity entity;
    private ObjectNode root;
    private JsonNode response;
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
                        root = mapper.createObjectNode();
                        
                        ObjectNode registrasi = mapper.createObjectNode();
                        registrasi.put("no_registrasi", rs.getString("noorder"));
                        registrasi.put("diagnosa_awal", "-");
                        registrasi.put("keterangan_klinis", rs.getString("diagnosa_klinis").trim());
                        registrasi.put("kode_rs", APIKODERS);
                        root.set("registrasi", registrasi);
                        
                        ObjectNode pasien = mapper.createObjectNode();
                        pasien.put("nama", rs.getString("nm_pasien").trim());
                        pasien.put("no_rm", rs.getString("no_rkm_medis"));
                        pasien.put("jenis_kelamin", rs.getString("jk"));
                        pasien.put("alamat", rs.getString("alamat").trim());
                        pasien.put("no_telphone", rs.getString("no_tlp").trim());
                        pasien.put("tanggal_lahir", rs.getString("tgl_lahir"));
                        pasien.put("nik", rs.getString("no_ktp").trim());
                        pasien.put("ras", "-");
                        pasien.put("berat_badan", rs.getString("bb").toLowerCase().trim() + "kg");
                        pasien.put("jenis_registrasi", rs.getString("jenis_registrasi"));
                        pasien.put("m_provinsi_id", PROVINSIID);
                        pasien.put("m_kabupaten_id", KABUPATENID);
                        pasien.put("m_kecamatan_id", KECAMATANID);
                        root.set("pasien", pasien);
                        
                        root.put("kode_dokter_pengirim", rs.getString("kd_dokter_perujuk"));
                        root.put("nama_dokter_pengirim", rs.getString("nm_dokter_perujuk"));
                        root.put("kode_unit_asal", rs.getString("asal_unit").substring(0, rs.getString("asal_unit").indexOf("|")));
                        root.put("nama_unit_asal", rs.getString("asal_unit").substring(rs.getString("asal_unit").indexOf("|") + 1));
                        root.put("kode_penjamin", rs.getString("kd_pj"));
                        root.put("nama_penjamin", rs.getString("png_jawab"));
                        root.put("kode_icdt", rs.getString("icdt").substring(0, rs.getString("icdt").indexOf("|")));
                        root.put("nama_icdt", rs.getString("icdt").substring(rs.getString("icdt").indexOf("|") + 1));
                        
                        ArrayNode tindakanArray = mapper.createArrayNode();
                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select permintaan_pemeriksaan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan from permintaan_pemeriksaan_lab " +
                            "join jns_perawatan_lab on permintaan_pemeriksaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where permintaan_pemeriksaan_lab.noorder = ?"
                        )) {
                            ps2.setString(1, kodeRegistrasi);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    ObjectNode tindakan = mapper.createObjectNode();
                                    tindakan.put("kode_tindakan", rs2.getString("kd_jenis_prw"));
                                    tindakan.put("nama_tindakan", rs2.getString("nm_perawatan"));
                                    tindakanArray.add(tindakan);
                                }
                            }
                        }
                        root.set("tindakan", tindakanArray);
                    }
                }
            }
            url = APIURL + "/bridging_sim_rs/registrasi";
            System.out.println("URL : " + url);
            System.out.println("JSON : " + root.toString());
            System.out.print("Mengirim order ke LIS : ");
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-api-key", APIKEY);
            entity = new HttpEntity(mapper.writeValueAsString(root), headers);
            ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Response : " + responseEntity.getBody());
            System.out.println("Response : " + responseEntity.getStatusCode());
            response = mapper.readTree(responseEntity.getBody());
            System.out.println(responseEntity.getStatusCode() + " " + response.path("message").asText());
            Sequel.menyimpanSmc(
                "adamlabs_request_response",
                "noorder, url, method, request, code, response, pengirim",
                kodeRegistrasi, url, "POST", root.toString(), String.valueOf(responseEntity.getStatusCode()), responseEntity.getBody(), akses.getkode()
            );
            if (response.path("status").asText().equals("200")) {
                Sequel.menyimpanSmc("adamlabs_orderlab", null, kodeRegistrasi, response.path("payload").path("registrasi").path("no_lab").asText());
                JOptionPane.showMessageDialog(null, "Order lab berhasil dikirim ke LIS ADAMLABS...!!!");
            } else {
                // StringBuilder concat = new StringBuilder();
                ArrayList<String> messages = new ArrayList<>();
                if (response.path("message").isArray()) {
                    for (JsonNode error : response.path("message")) {
                        messages.add(new StringBuilder("- ").append(error.path("msg").asText()).toString());
                    }
                }
                if (messages.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses order lab..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, messages.toArray(), "Gagal", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            if (e.getResponseBodyAsString().contains("<title>Error</title>")) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses order lab..!!");
            }
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
