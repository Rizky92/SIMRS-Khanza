package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class ApiBIOSYS {
    private final String APIURL = koneksiDB.BIOSYSAPIURL();
    private final String APIKEY = koneksiDB.BIOSYSAPIKEY();
    private final String SUBHEADERPREFIX = koneksiDB.LABORATORIUMSUBHEADERPREFIX();
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers;
    private HttpEntity entity;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;

    public int kirimOrder(String noorder) throws BiosysException {
        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select permintaan_lab.noorder, permintaan_lab.no_rawat, permintaan_lab.diagnosa_klinis, permintaan_lab.informasi_tambahan, concat(permintaan_lab.tgl_permintaan, ' ', permintaan_lab.jam_permintaan) " +
                "as tgl_order, if(permintaan_lab.informasi_tambahan like '%cito%', '1', '0') as iscito, if(permintaan_lab.status = 'Ranap', '1', '0') as patienttype, reg_periksa.no_rkm_medis, " +
                "pasien.nm_pasien, pasien.tgl_lahir, if(pasien.jk = 'L', '1', '2') as jk, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab, propinsi.nm_prop) " +
                "as alamat, trim(pasien.no_ktp) as no_ktp, permintaan_lab.dokter_perujuk as kd_dokter, dokter.nm_dokter, (select set_pjlab.kd_dokterlab from set_pjlab limit 1) as kd_dokter_pj, " +
                "(select dokter.nm_dokter from set_pjlab join dokter on set_pjlab.kd_dokterlab = dokter.kd_dokter limit 1) as nm_dokter_pj, reg_periksa.kd_pj, penjab.png_jawab, exists(select * from " +
                "password_asuransi where password_asuransi.kd_pj = reg_periksa.kd_pj) as isbpjs, '' as internal_note from permintaan_lab inner join dokter on permintaan_lab.dokter_perujuk = dokter.kd_dokter " +
                "inner join reg_periksa on permintaan_lab.no_rawat = reg_periksa.no_rawat inner join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis inner join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
                "inner join kelurahan on pasien.kd_kel = kelurahan.kd_kel inner join kecamatan on pasien.kd_kec = kecamatan.kd_kec inner join kabupaten on pasien.kd_kab = kabupaten.kd_kab " +
                "inner join propinsi on pasien.kd_prop = propinsi.kd_prop where permintaan_lab.noorder = ?"
            )) {
                ps.setString(1, noorder);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Order lab tidak ditemukan: " + noorder);
                    }

                    ObjectNode root = mapper.createObjectNode();
                    root.put("PatientCode", rs.getString("no_rkm_medis"));
                    root.put("OrderNumber", rs.getString("noorder"));
                    root.put("PatientName", rs.getString("nm_pasien"));
                    root.put("IdentityNumber", rs.getString("no_ktp"));
                    root.put("PatientDob", rs.getString("tgl_lahir"));
                    root.put("PatientSexCode", rs.getString("jk"));
                    root.put("PatientAddress", rs.getString("alamat"));
                    root.put("DiagnosisName", rs.getString("diagnosa_klinis"));
                    root.put("OrderDateTime", rs.getString("tgl_order"));
                    root.put("DoctorOrderCode", rs.getString("kd_dokter"));
                    root.put("DoctorOrderName", rs.getString("nm_dokter"));
                    root.put("DoctorInChargeCode", rs.getString("kd_dokter_pj"));
                    root.put("DoctorInChargeName", rs.getString("nm_dokter_pj"));
                    if (rs.getString("patienttype").equals("1")) {
                        root.put("ServiceClassCode", Sequel.cariIsiSmc("select kamar.kelas from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar where kamar_inap.no_rawat = ? and kamar_inap.stts_pulang != 'Pindah Kamar' order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc", rs.getString("no_rawat")));
                        root.put("ServiceClassName", root.get("ServiceClassCode").asText(""));
                        root.put("WardRoomCode", Sequel.cariIsiSmc("select kamar.kd_bangsal from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar where kamar_inap.no_rawat = ? and kamar_inap.stts_pulang != 'Pindah Kamar' order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc", rs.getString("no_rawat")));
                        root.put("WardRoomName", Sequel.cariIsiSmc("select bangsal.nm_bangsal from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? and kamar_inap.stts_pulang != 'Pindah Kamar' order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc", rs.getString("no_rawat")));
                    } else if (rs.getString("patienttype").equals("0")) {
                        root.put("ServiceClassCode", "Ralan");
                        root.put("ServiceClassName", "Rawat Jalan");
                        root.put("WardRoomCode", Sequel.cariIsiSmc("select reg_periksa.kd_poli from reg_periksa where reg_periksa.no_rawat = ?", rs.getString("no_rawat")));
                        root.put("WardRoomName", Sequel.cariIsiSmc("select poliklinik.nm_poli from reg_periksa join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli where reg_periksa.no_rawat = ?", rs.getString("no_rawat")));
                    }
                    root.put("GuarantorId", rs.getString("kd_pj"));
                    root.put("GuarantorName", rs.getString("png_jawab"));
                    root.put("PatientType", rs.getString("patienttype"));
                    root.put("IsCito", rs.getString("iscito"));
                    root.put("IsBpjs", rs.getString("isbpjs"));
                    root.put("InternalNote", rs.getString("internal_note"));
                    root.put("PatientNote", rs.getString("informasi_tambahan"));

                    ArrayNode orderedItems = mapper.createArrayNode();
                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                        "select permintaan_pemeriksaan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan from permintaan_pemeriksaan_lab " +
                        "join jns_perawatan_lab on permintaan_pemeriksaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where permintaan_pemeriksaan_lab.noorder = ?"
                    )) {
                        ps2.setString(1, noorder);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                ObjectNode header = mapper.createObjectNode();
                                header.put("HeaderItemCode", rs2.getString("kd_jenis_prw"));
                                header.put("HeaderItemName", rs2.getString("nm_perawatan"));

                                ArrayNode items = mapper.createArrayNode();
                                try (PreparedStatement ps3 = koneksi.prepareStatement(
                                    "select template_laboratorium.id_template, trim(template_laboratorium.Pemeriksaan) from template_laboratorium " +
                                    "where template_laboratorium.kd_jenis_prw = ? and template_laboratorium.Pemeriksaan not like concat(?, '%') order by template_laboratorium.urut asc"
                                )) {
                                    ps3.setString(1, rs2.getString("kd_jenis_prw"));
                                    ps3.setString(2, SUBHEADERPREFIX);
                                    try (ResultSet rs3 = ps3.executeQuery()) {
                                        while (rs3.next()) {
                                            if (rs3.getString(2) != null && !rs3.getString(2).isBlank()) {
                                                ObjectNode item = mapper.createObjectNode();
                                                item.put("ItemCode", rs3.getString(1));
                                                item.put("ItemName", rs3.getString(2));
                                                items.add(item);
                                            }
                                        }
                                    }
                                }
                                header.set("Items", items);
                                orderedItems.add(header);
                            }
                        }
                    }
                    root.set("OrderedItems", orderedItems);

                    String url = APIURL + "/lab_order.php";
                    System.out.println("URL: " + url);
                    System.out.println("JSON: " + root.toString());
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-API-KEY", APIKEY);

                    ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.POST, new HttpEntity(mapper.writeValueAsString(root), headers), String.class);

                    JsonNode response = mapper.readTree(responseEntity.getBody());

                    Sequel.menyimpanSmc("lis_request_response", "vendor, noorder, url, method, request, code, response, pengirim", "biosys", noorder,
                        url, "POST", root.toString(), responseEntity.getStatusCode().toString(), responseEntity.getBody(), akses.getkode());

                    String status = response.path("status").asText();

                    if ("200".equals(status) || "406".equals(status)) {
                        String nolab = response.path("payload").path("nolab").asText();

                        Sequel.executeRawSmc("insert into lis_orderlab (vendor, noorder, nolab) values ('biosys', ?, ?) on duplicate key update nolab = values(nolab)", noorder, nolab);

                        return Integer.parseInt(status);
                    } else {
                        throw new Exception(response.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new BiosysException("Gagal mengirim order ke LIS BIOSYS: " + e.getMessage(), e);
        }
    }

    public Integer updateOrder(String noorder) throws BiosysException {
        if (!Sequel.cariExistsSmc("select * from lis_orderlab where lis_orderlab.vendor = 'biosys' and lis_orderlab.noorder = ?", noorder)) {
            return -1;
        }

        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select permintaan_lab.noorder, permintaan_lab.no_rawat, permintaan_lab.diagnosa_klinis, permintaan_lab.informasi_tambahan, concat(permintaan_lab.tgl_permintaan, ' ', permintaan_lab.jam_permintaan) " +
                "as tgl_order, if(permintaan_lab.informasi_tambahan like '%cito%', '1', '0') as iscito, if(permintaan_lab.status = 'Ranap', '1', '0') as patienttype, reg_periksa.no_rkm_medis, " +
                "pasien.nm_pasien, pasien.tgl_lahir, if(pasien.jk = 'L', '1', '2') as jk, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab, propinsi.nm_prop) " +
                "as alamat, trim(pasien.no_ktp) as no_ktp, permintaan_lab.dokter_perujuk as kd_dokter, dokter.nm_dokter, (select set_pjlab.kd_dokterlab from set_pjlab limit 1) as kd_dokter_pj, " +
                "(select dokter.nm_dokter from set_pjlab join dokter on set_pjlab.kd_dokterlab = dokter.kd_dokter limit 1) as nm_dokter_pj, reg_periksa.kd_pj, penjab.png_jawab, exists(select * from " +
                "password_asuransi where password_asuransi.kd_pj = reg_periksa.kd_pj) as isbpjs, '' as internal_note from permintaan_lab inner join dokter on permintaan_lab.dokter_perujuk = dokter.kd_dokter " +
                "inner join reg_periksa on permintaan_lab.no_rawat = reg_periksa.no_rawat inner join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis inner join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
                "inner join kelurahan on pasien.kd_kel = kelurahan.kd_kel inner join kecamatan on pasien.kd_kec = kecamatan.kd_kec inner join kabupaten on pasien.kd_kab = kabupaten.kd_kab " +
                "inner join propinsi on pasien.kd_prop = propinsi.kd_prop where permintaan_lab.noorder = ?"
            )) {
                ps.setString(1, noorder);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Order lab tidak ditemukan: " + noorder);
                    }

                    ObjectNode root = mapper.createObjectNode();
                    root.put("PatientCode", rs.getString("no_rkm_medis"));
                    root.put("OrderNumber", rs.getString("noorder"));
                    root.put("PatientName", rs.getString("nm_pasien"));
                    root.put("IdentityNumber", rs.getString("no_ktp"));
                    root.put("PatientDob", rs.getString("tgl_lahir"));
                    root.put("PatientSexCode", rs.getString("jk"));
                    root.put("PatientAddress", rs.getString("alamat"));
                    root.put("DiagnosisName", rs.getString("diagnosa_klinis"));
                    root.put("OrderDateTime", rs.getString("tgl_order"));
                    root.put("DoctorOrderCode", rs.getString("kd_dokter"));
                    root.put("DoctorOrderName", rs.getString("nm_dokter"));
                    root.put("DoctorInChargeCode", rs.getString("kd_dokter_pj"));
                    root.put("DoctorInChargeName", rs.getString("nm_dokter_pj"));
                    if (rs.getString("patienttype").equals("1")) {
                        root.put("ServiceClassCode", Sequel.cariIsiSmc("select kamar.kelas from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar where kamar_inap.no_rawat = ? and kamar_inap.stts_pulang != 'Pindah Kamar' order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc", rs.getString("no_rawat")));
                        root.put("ServiceClassName", root.get("ServiceClassCode").asText(""));
                        root.put("WardRoomCode", Sequel.cariIsiSmc("select kamar.kd_bangsal from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar where kamar_inap.no_rawat = ? and kamar_inap.stts_pulang != 'Pindah Kamar' order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc", rs.getString("no_rawat")));
                        root.put("WardRoomName", Sequel.cariIsiSmc("select bangsal.nm_bangsal from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? and kamar_inap.stts_pulang != 'Pindah Kamar' order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc", rs.getString("no_rawat")));
                    } else if (rs.getString("patienttype").equals("0")) {
                        root.put("ServiceClassCode", "Ralan");
                        root.put("ServiceClassName", "Rawat Jalan");
                        root.put("WardRoomCode", Sequel.cariIsiSmc("select reg_periksa.kd_poli from reg_periksa where reg_periksa.no_rawat = ?", rs.getString("no_rawat")));
                        root.put("WardRoomName", Sequel.cariIsiSmc("select poliklinik.nm_poli from reg_periksa join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli where reg_periksa.no_rawat = ?", rs.getString("no_rawat")));
                    }
                    root.put("GuarantorId", rs.getString("kd_pj"));
                    root.put("GuarantorName", rs.getString("png_jawab"));
                    root.put("PatientType", rs.getString("patienttype"));
                    root.put("IsCito", rs.getString("iscito"));
                    root.put("IsBpjs", rs.getString("isbpjs"));
                    root.put("InternalNote", rs.getString("internal_note"));
                    root.put("PatientNote", rs.getString("informasi_tambahan"));
                    root.set("OrderedItems", mapper.createArrayNode().add(mapper.createObjectNode()));

                    String url = APIURL + "/order_update.php";
                    System.out.println("URL: " + url);
                    System.out.println("JSON: " + root.toString());

                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-API-KEY", APIKEY);

                    ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.PUT, new HttpEntity(mapper.writeValueAsString(root), headers), String.class);

                    JsonNode response = mapper.readTree(responseEntity.getBody());

                    Sequel.menyimpanSmc("lis_request_response", "vendor, noorder, url, method, request, code, response, pengirim", "biosys", noorder,
                        url, "PUT", root.toString(), responseEntity.getStatusCode().toString(), responseEntity.getBody(), akses.getkode());

                    String status = response.path("status").asText();

                    if ("200".equals(status) || "204".equals(status) || "406".equals(status)) {
                        return Integer.valueOf(status);
                    } else {
                        throw new Exception(response.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new BiosysException("Gagal mengupdate order ke LIS BIOSYS: " + e.getMessage(), e);
        }
    }

    public void hapusOrder(String noorder) throws BiosysException {
        if (!Sequel.cariExistsSmc("select * from lis_orderlab where lis_orderlab.vendor = 'biosys' and lis_orderlab.noorder = ?", noorder)) {
            return;
        }

        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("OrderNumber", noorder);

            String url = APIURL + "/order_delete.php";
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-API-KEY", APIKEY);

            ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.DELETE, new HttpEntity(mapper.writeValueAsString(root), headers), String.class);
            System.out.println("BIOSYS hapusOrder response: " + responseEntity.getBody());

            Sequel.menyimpanSmc("lis_request_response", "vendor, noorder, url, method, request, code, response, pengirim", "biosys", noorder,
                url, "DELETE", root.toString(), responseEntity.getStatusCode().toString(), responseEntity.getBody(), akses.getkode());
        } catch (Exception e) {
            throw new BiosysException("Gagal menghapus order dari LIS BIOSYS: " + e.getMessage(), e);
        }
    }

    public JsonNode ambilHasil(String noorder) throws BiosysException {
        try {
            String url = APIURL + "/lab_result.php?OrderNumber=" + noorder;
            headers = new HttpHeaders();
            headers.add("X-API-KEY", APIKEY);
            entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.GET, entity, String.class);

            System.out.println("Response: " + responseEntity.getBody());
            Sequel.menyimpanSmc("lis_request_response", "vendor, noorder, url, method, request, code, response, pengirim", "biosys", noorder,
                url, "GET", "", responseEntity.getStatusCode().toString(), responseEntity.getBody(), akses.getkode());

            return mapper.readTree(responseEntity.getBody());
        } catch (Exception e) {
            throw new BiosysException("Gagal mengambil hasil dari LIS BIOSYS: " + e.getMessage(), e);
        }
    }

    public void konfirmasiHasil(String noorder, String norm) throws BiosysException {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("OrderNumber", noorder);
            root.put("PatientCode", norm);
            root.put("Flag", 1);
            root.put("ReceivedDate", dft.format(new Date()));

            String url = APIURL + "/post_flag.php";
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-API-KEY", APIKEY);

            ResponseEntity<String> responseEntity = http().exchange(url, HttpMethod.POST, new HttpEntity(mapper.writeValueAsString(root), headers), String.class);

            Sequel.menyimpanSmc("lis_request_response", "vendor, noorder, url, method, request, code, response, pengirim", "biosys", noorder,
                url, "POST", root.toString(), responseEntity.getStatusCode().toString(), responseEntity.getBody(), akses.getkode());
        } catch (Exception e) {
            throw new BiosysException("Gagal konfirmasi hasil ke LIS BIOSYS: " + e.getMessage(), e);
        }
    }

    private RestTemplate http() throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme = new Scheme("https", 443, sslFactory);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory() {
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new ApiBIOSYS.HttpEntityEnclosingDeleteRequest(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws java.io.IOException {
                return response.getStatusCode().value() != 406 && super.hasError(response);
            }
        });
        return restTemplate;
    }

    static class HttpEntityEnclosingDeleteRequest extends HttpEntityEnclosingRequestBase {
        public HttpEntityEnclosingDeleteRequest(final URI uri) {
            super();
            setURI(uri);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }

    public static class BiosysException extends Exception {
        public BiosysException(String message) {
            super(message);
        }

        public BiosysException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
