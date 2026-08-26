package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author windiartonugroho
 */
public class ApiOrthanc {
    private HttpHeaders headers ;
    private JsonNode root;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private String auth,authEncrypt,requestJson;
    private byte[] encodedBytes;
    private int i=1;
    private final sekuel Sequel = new sekuel();

    public ApiOrthanc(){
        try {
            auth=koneksiDB.USERORTHANC()+":"+koneksiDB.PASSORTHANC();
            encodedBytes = Base64.encodeBase64(auth.getBytes());
            authEncrypt= new String(encodedBytes);
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
    }

    public String Auth(){
        return authEncrypt;
    }

    public JsonNode AmbilSeries(String Norm,String Tanggal1,String Tanggal2){
        System.out.println("Percobaan Mengambil Photo Pasien : "+Norm);
        try{
            headers = new HttpHeaders();
            //System.out.println("Auth : "+authEncrypt);
            headers.add("Authorization", "Basic "+authEncrypt);
            requestJson = "{"+
                              "\"Level\": \"Study\","+
                              "\"Expand\": true,"+
                              "\"Query\": {"+
                                   "\"StudyDate\": \""+Tanggal1+"-"+Tanggal2+"\","+
                                   "\"PatientID\": \""+Norm+"\""+
                              "}"+
                          "}";
            System.out.println("Request JSON : "+requestJson);
            requestEntity = new HttpEntity(requestJson,headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/tools/find");
            requestJson=getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/tools/find", HttpMethod.POST, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+requestJson);
            root = mapper.readTree(requestJson);
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil data dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    public JsonNode AmbilPng(String NoRawat,String Series){
        System.out.println("Percobaan Mengambil Gambar PNG : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            //System.out.println("Auth : "+authEncrypt);
            headers.add("Authorization", "Basic "+authEncrypt);
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series);
            requestJson=getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+requestJson);
            root = mapper.readTree(requestJson);
            i=1;
            for(JsonNode list:root.path("Instances")){
                 System.out.println("Mengambil Gambar PNG "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview");
                 headers = new HttpHeaders();
                 headers.add("Authorization", "Basic "+authEncrypt);
                 headers.add("Accept","image/png");
                 headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                 headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
                 HttpEntity<String> entity = new HttpEntity<>(headers);
                 ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview", HttpMethod.GET, entity, byte[].class);
                 Files.write(Paths.get("./gambarradiologi/"+NoRawat+i+".png"),response.getBody());
                 i++;
            }
            JOptionPane.showMessageDialog(null,"Pengambilan Gambar PNG dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar PNG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    public JsonNode AmbilJpg(String NoRawat,String Series){
        System.out.println("Percobaan Mengambil Gambar JPG : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            //System.out.println("Auth : "+authEncrypt);
            headers.add("Authorization", "Basic "+authEncrypt);
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series);
            requestJson=getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+requestJson);
            root = mapper.readTree(requestJson);
            i=1;
            for(JsonNode list:root.path("Instances")){
                 System.out.println("Mengambil Gambar JPG "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview");
                 headers = new HttpHeaders();
                 headers.add("Authorization", "Basic "+authEncrypt);
                 headers.add("Accept","image/jpeg");
                 headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                 headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
                 HttpEntity<String> entity = new HttpEntity<>(headers);
                 ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview", HttpMethod.GET, entity, byte[].class);
                 Files.write(Paths.get("./gambarradiologi/"+NoRawat+i+".jpg"),response.getBody());
                 i++;
            }
            JOptionPane.showMessageDialog(null,"Pengambilan Gambar JPG dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar JPG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    public JsonNode AmbilJpg2(String Series){
        System.out.println("Percobaan Mengambil Gambar JPG : "+Series+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            //System.out.println("Auth : "+authEncrypt);
            headers.add("Authorization", "Basic "+authEncrypt);
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series);
            requestJson=getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+requestJson);
            root = mapper.readTree(requestJson);
            for(JsonNode list:root.path("Instances")){
                 headers = new HttpHeaders();
                 headers.add("Authorization", "Basic "+authEncrypt);
                 headers.add("Accept","image/jpeg");
                 headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                 headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
                 HttpEntity<String> entity = new HttpEntity<>(headers);
                 ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview", HttpMethod.GET, entity, byte[].class);
                 Files.write(Paths.get("./gambarradiologi/"+Series+".jpg"),response.getBody());
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar JPG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    public JsonNode AmbilBmp(String NoRawat,String Series){
        System.out.println("Percobaan Mengambil Gambar BMP : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            //System.out.println("Auth : "+authEncrypt);
            headers.add("Authorization", "Basic "+authEncrypt);
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series);
            requestJson=getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+requestJson);
            root = mapper.readTree(requestJson);
            i=1;
            for(JsonNode list:root.path("Instances")){
                 System.out.println("Mengambil Gambar BMP "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview");
                 headers = new HttpHeaders();
                 headers.add("Authorization", "Basic "+authEncrypt);
                 headers.add("Accept","image/bmp");
                 headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                 headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
                 HttpEntity<String> entity = new HttpEntity<>(headers);
                 ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/preview", HttpMethod.GET, entity, byte[].class);
                 Files.write(Paths.get("./gambarradiologi/"+NoRawat+i+".bmp"),response.getBody());
                 i++;
            }
            JOptionPane.showMessageDialog(null,"Pengambilan Gambar BMP dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar BMP dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    public JsonNode AmbilDcm(String NoRawat,String Series){
        System.out.println("Percobaan Mengambil Gambar DCM : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            //System.out.println("Auth : "+authEncrypt);
            headers.add("Authorization", "Basic "+authEncrypt);
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series);
            requestJson=getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/series/"+Series, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+requestJson);
            root = mapper.readTree(requestJson);
            i=1;
            for(JsonNode list:root.path("Instances")){
                 System.out.println("Mengambil Gambar DCM "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/file");
                 headers = new HttpHeaders();
                 headers.add("Authorization", "Basic "+authEncrypt);
                 headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                 headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
                 HttpEntity<String> entity = new HttpEntity<>(headers);
                 ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/instances/"+list.asText()+"/file", HttpMethod.GET, entity, byte[].class);
                 Files.write(Paths.get("./gambarradiologi/"+NoRawat+i+".dcm"),response.getBody());
                 i++;
            }
            JOptionPane.showMessageDialog(null,"Pengambilan Gambar DCM dari Orthanc berhasil, silahkan lihat di dalam folder Aplikasi..!!");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar DCM dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    public boolean UbahAccession(String studyId, String accessionBaru){
        System.out.println("Modify AccessionNumber Study : " + studyId);
        try{
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);
            requestJson = "{" +
                                "\"Replace\": {" +
                                    "\"AccessionNumber\": \""+accessionBaru+"\"" +
                                "}," +
                                "\"KeepSource\": false"+
                            "}";
            System.out.println("Request JSON : " + requestJson);
            requestEntity = new HttpEntity(requestJson, headers);
            System.out.println("URL : "+koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/studies/"+studyId+"/modify");
            String response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/studies/"+studyId+"/modify",HttpMethod.POST,requestEntity, String.class).getBody();
            System.out.println("Response : " + response);
            return true;
        }catch(Exception e){
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null,"Gagal mengubah Accession Number di Orthanc..!!");
            return false;
        }
    }

    public boolean kirimKeModality(String studyId){
        System.out.println("Kirim Study ke Modality : " + studyId);
        try{
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);
            requestJson = "[\"" + studyId + "\"]";
            requestEntity = new HttpEntity(requestJson, headers);
            System.out.println("URL : " + koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/modalities/DICOMROUTER/store");
            System.out.println("Request JSON : " + requestJson);
            String response = getRest().exchange(koneksiDB.URLORTHANC()+":"+koneksiDB.PORTORTHANC()+"/modalities/DICOMROUTER/store",HttpMethod.POST,requestEntity,String.class).getBody();
            System.out.println("Response : " + response);
            JOptionPane.showMessageDialog(null,"Proses kirim ke Modality selesai..!!");
            return true;
        }catch(Exception e){
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null,"Gagal kirim ke Modality..!!");
            return false;
        }
    }

    public boolean kirimKeModalitySmc(String studyID, String modality) {
        String notifKirim = "";
        final String study = (null == studyID) ? "" : studyID.trim();
        final String tujuan = (null == modality) ? "" : modality.trim();

        boolean sukses = true;

        if (study.isBlank()) {
            notifKirim = "Study ID kosong";
            System.out.println("Notifikasi : " + notifKirim);
            sukses = false;
        } else if (!study.matches("[A-Za-z0-9._-]{1,128}")) {
            notifKirim = "Study ID tidak valid : " + study;
            System.out.println("Notifikasi : " + notifKirim);
            sukses = false;
        } else if (tujuan.isBlank()) {
            notifKirim = "Nama modality kosong";
            System.out.println("Notifikasi : " + notifKirim);
            sukses = false;
        } else if (!tujuan.matches("[A-Za-z0-9._-]{1,64}")) {
            notifKirim = "Nama modality tidak valid : " + tujuan;
            System.out.println("Notifikasi : " + notifKirim);
            sukses = false;
        }

        if (sukses) {
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/modalities/" + tujuan + "/store";
            System.out.println("Kirim Study ke Modality " + tujuan + " : " + study);
            try {
                HttpHeaders header = new HttpHeaders();
                header.add("Authorization", "Basic " + authEncrypt);
                header.setContentType(MediaType.APPLICATION_JSON);

                String json = "[\"" + study + "\"]";
                System.out.println("URL : " + url);
                System.out.println("Request JSON : " + json);

                ResponseEntity<String> response = getRest().exchange(url, HttpMethod.POST, new HttpEntity(json, header), String.class);
                HttpStatus status = response.getStatusCode();
                String body = response.getBody();
                System.out.println("Response : " + status.value() + " " + body);

                if (HttpStatus.Series.SUCCESSFUL != status.series()) {
                    notifKirim = "Orthanc membalas HTTP " + status.value() + " " + status.getReasonPhrase();
                    sukses = false;
                } else if ((null == body) || (body.isBlank())) {
                    notifKirim = "Balasan Orthanc kosong";
                    sukses = false;
                } else {
                    JsonNode hasil = mapper.readTree(body);
                    int terkirim = hasil.path("InstancesCount").asInt(-1), gagal = hasil.path("FailedInstancesCount").asInt(-1);
                    if ((0 > terkirim) || (0 > gagal)) {
                        notifKirim = "Balasan Orthanc tidak dikenali : " + body;
                        sukses = false;
                    } else if (0 < gagal) {
                        notifKirim = gagal + " instance gagal dikirim ke " + tujuan;
                        sukses = false;
                    } else if (0 == terkirim) {
                        notifKirim = "Tidak ada instance yang dikirim, Study ID " + study + " tidak ditemukan di Orthanc";
                        sukses = false;
                    } else {
                        System.out.println("Berhasil kirim " + terkirim + " instance ke Modality " + tujuan);
                    }
                }

                if (!sukses) {
                    System.out.println("Notifikasi : " + notifKirim);
                }
            } catch (HttpStatusCodeException e) {
                if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
                    notifKirim = "Modality " + tujuan + " atau Study ID " + study + " tidak terdaftar di Orthanc";
                } else if (HttpStatus.UNAUTHORIZED == e.getStatusCode() || HttpStatus.FORBIDDEN == e.getStatusCode()) {
                    notifKirim = "Autentikasi ke Orthanc ditolak, periksa user dan password Orthanc";
                } else {
                    notifKirim = "Orthanc membalas HTTP " + e.getStatusCode().value() + " " + e.getStatusText() + " : " + e.getResponseBodyAsString();
                }

                System.out.println("Notifikasi : " + notifKirim);
                sukses = false;
            } catch (ResourceAccessException e) {
                notifKirim = "Server Orthanc di " + url + " tidak bisa dihubungi : " + e.getMessage();
                System.out.println("Notifikasi : " + notifKirim);
                sukses = false;
            } catch (Exception e) {
                notifKirim = "Gagal kirim ke Modality " + tujuan + " : " + e;
                System.out.println("Notifikasi : " + notifKirim);
                sukses = false;
            }
        }

        if (!notifKirim.isBlank()) {
            JOptionPane.showMessageDialog(null, notifKirim, "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

        return sukses;
    }

    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers= {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
            }
        };
        sslContext.init(null,trustManagers , new SecureRandom());
        sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme=new Scheme("https",443,sslFactory);
        factory=new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }
}
