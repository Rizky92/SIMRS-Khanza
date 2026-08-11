package bridging.satusehat;

import bridging.ApiBPJS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
    private sekuel Sequel=new sekuel();
    public String statusakses="";
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private String auth,authEncrypt,requestJson;
    private static final String ENCRYPTION_KEY = "HospitalPACS2025"; // 16 bytes for AES
    private byte[] encodedBytes;
    private int i=1;
    
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
            System.out.println("Auth : "+authEncrypt);
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
            System.out.println("Auth : "+authEncrypt);
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
            System.out.println("Auth : "+authEncrypt);
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
    
    public JsonNode AmbilBmp(String NoRawat,String Series){
        System.out.println("Percobaan Mengambil Gambar BMP : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            System.out.println("Auth : "+authEncrypt);
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
            System.out.println("Auth : "+authEncrypt);
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
    
    
//    tambahan abdul
    
    
    /**
     * Generate secure encrypted URL for PACS viewer access
     *
     * @param patientId Patient identifier
     * @param accessionNumber Radiology accession number
     * @param userId User accessing the images
     * @return Encrypted PACS URL
     */
    public String generateEncryptedURLOrthanc(String patientId, String accessionNumber, String userId) {
        String urlwithtoken = "";
        if (isStudyExistsByAccessionNumber(accessionNumber) == true) {
            try {
                // 3 months expiry (90 days in milliseconds)
                long expiry = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000);
                String data = String.format("%s|%s|%s|%d", patientId, accessionNumber, userId, expiry);

                String encrypted = encrypt(data, ENCRYPTION_KEY);
                urlwithtoken = String.format("https://pacs.pkudemak.id?token=%s",
                        urlEncode(encrypted));
                return urlwithtoken;

            } catch (Exception e) {
                urlwithtoken = "";
                throw new RuntimeException("PACS URL encryption failed", e);
            }
        } else {
            urlwithtoken = "";
        }

        return urlwithtoken;
    }

    /**
     * Encrypt data using AES encryption
     *
     * @param data Data to encrypt
     * @param key Encryption key (16 bytes)
     * @return Base64 encoded encrypted string
     */
    private static String encrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        return base64Encode(encrypted);
    }

    /**
     * Base64 encode for Java 7 compatibility
     *
     * @param data Byte array to encode
     * @return Base64 encoded string
     */
    private static String base64Encode(byte[] data) {
        // Java 7 compatible Base64 encoding
        StringBuilder result = new StringBuilder();
        String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

        for (int i = 0; i < data.length; i += 3) {
            int b1 = data[i] & 0xFF;
            int b2 = (i + 1 < data.length) ? data[i + 1] & 0xFF : 0;
            int b3 = (i + 2 < data.length) ? data[i + 2] & 0xFF : 0;

            int bitmap = (b1 << 16) | (b2 << 8) | b3;

            result.append(base64Chars.charAt((bitmap >> 18) & 63));
            result.append(base64Chars.charAt((bitmap >> 12) & 63));

            if (i + 1 < data.length) {
                result.append(base64Chars.charAt((bitmap >> 6) & 63));
            } else {
                result.append('=');
            }

            if (i + 2 < data.length) {
                result.append(base64Chars.charAt(bitmap & 63));
            } else {
                result.append('=');
            }
        }

        return result.toString();
    }

    /**
     * URL encode for safe transmission
     *
     * @param value String to encode
     * @return URL encoded string
     */
    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should always be supported
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * Generate audit log entry for HIPAA compliance
     *
     * @param patientId Patient ID
     * @param userId User ID
     * @param action Action performed
     */
    public static void logImageAccess(String patientId, String userId, String action) {
        // HIPAA audit logging
        String logEntry = String.format("[%s] User: %s, Patient: %s, Action: %s, IP: %s",
                new java.util.Date().toString(),
                userId,
                patientId,
                action,
                "SYSTEM" // Replace with actual IP if available
        );

        System.out.println("AUDIT: " + logEntry);
        // In production: write to secure audit log file or database
    }
    
    public boolean isStudyExistsByAccessionNumber(String orderid) {
        try {
            // Input validation - ensure we have valid search criteria
            boolean hasOrderId = orderid != null && !orderid.trim().isEmpty();

            if (!hasOrderId) {
                System.out.println("Invalid search criteria: No AccessionNumber provided");
                return false;
            }

            // Set up headers for Orthanc API authentication
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
            String findUrl = orthancUrl + "/tools/find";
            JsonNode studyIds = null;

            // Search by AccessionNumber
            requestJson = "{\n"
                    + "  \"Level\": \"Study\",\n"
                    + "  \"Query\": {\n"
                    + "    \"AccessionNumber\": \"" + orderid.trim() + "\"\n"
                    + "  }\n"
                    + "}";

//            System.out.println("Searching for AccessionNumber: " + orderid.trim());
            requestEntity = new HttpEntity(requestJson, headers);

            ResponseEntity<String> response = getRest().exchange(
                    findUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String searchResult = response.getBody();
            studyIds = mapper.readTree(searchResult);

            // Check if any studies were found
            if (studyIds == null || studyIds.size() == 0) {
                System.out.println("No study found for AccessionNumber: " + orderid.trim());
                return false;
            }

            // Get the first study UUID (Orthanc internal ID)
            String studyUuid = studyIds.get(0).asText();

            // Validate UUID format (basic check)
            if (studyUuid == null || studyUuid.trim().isEmpty()) {
                System.out.println("Invalid study UUID from Orthanc server");
                return false;
            }

            System.out.println("Study found for AccessionNumber: " + orderid.trim());
            return true;

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error accessing Orthanc: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (ResourceAccessException e) {
            System.err.println("Network Error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error retrieving study: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
    
    
    public String getStudyID(String orderid) {

        String studyID = "";
        try {
            // Input validation - ensure we have valid search criteria
            boolean hasOrderId = orderid != null && !orderid.trim().isEmpty();

            if (!hasOrderId) {
                studyID = "";
                System.out.println("Invalid search criteria: No AccessionNumber provided");
                return studyID;
            }

            // Set up headers for Orthanc API authentication
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
            String findUrl = orthancUrl + "/tools/find";
            JsonNode studyIds = null;

            // Search by AccessionNumber
            requestJson = "{\n"
                    + "  \"Level\": \"Study\",\n"
                    + "  \"Query\": {\n"
                    + "    \"AccessionNumber\": \"" + orderid.trim() + "\"\n"
                    + "  }\n"
                    + "}";

//            System.out.println("Searching for AccessionNumber: " + orderid.trim());
            requestEntity = new HttpEntity(requestJson, headers);

            ResponseEntity<String> response = getRest().exchange(
                    findUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String searchResult = response.getBody();
            studyIds = mapper.readTree(searchResult);

            // Check if any studies were found
            if (studyIds == null || studyIds.size() == 0) {
                studyID = "";
                System.out.println("No study found for AccessionNumber: " + orderid.trim());
                return studyID;
            }

            // Get the first study UUID (Orthanc internal ID)
            String studyUuid = studyIds.get(0).asText();

            // Validate UUID format (basic check)
            if (studyUuid == null || studyUuid.trim().isEmpty()) {
                studyID = "";
                System.out.println("Invalid study UUID from Orthanc server");
                return studyID;
            }

            System.out.println("Study found for AccessionNumber: " + orderid.trim());
            studyID = studyUuid;
            return studyID;

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error accessing Orthanc: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (ResourceAccessException e) {
            System.err.println("Network Error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error retrieving study: " + e.getMessage());
            e.printStackTrace();
        }

        return studyID;
    }

    /**
     * Helper method to show error dialogs (extract to avoid code duplication)
     */
    private void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    
    public List<byte[]> getStudyImages(String studyId) {
        List<byte[]> images = new ArrayList<>();
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            requestEntity = new HttpEntity(headers);

            String seriesUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId + "/series";
            String seriesJson = getRest().exchange(seriesUrl, HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode seriesArray = mapper.readTree(seriesJson);

            System.out.println("Fetching series from: " + seriesUrl);
            System.out.println("Found " + seriesArray.size() + " series");

            for (JsonNode series : seriesArray) {
                String seriesId = series.path("ID").asText();
                System.out.println("Processing series: " + seriesId);

                if (shouldProcessSeries(seriesId)) {
                    //System.out.println("Series approved for processing: " + seriesId);
                    List<byte[]> seriesImages = getSeriesImages(seriesId);
                    images.addAll(seriesImages);
                    //System.out.println("Added " + seriesImages.size() + " images from series: " + seriesId);
                } else {
                    System.out.println("Series skipped: " + seriesId);
                }
            }

//            System.out.println("Total images retrieved: " + images.size());
        } catch (Exception e) {
            System.out.println("Error in getStudyImages: " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }

    /**
     * Mengambil SELURUH gambar (rendered JPEG) dari sebuah study di Orthanc,
     * dari semua series tanpa filter modalitas. Dipakai untuk fitur "Lihat
     * Gambar" pada daftar DICOM server.
     *
     * @param studyId Orthanc internal study ID (UUID)
     * @return daftar gambar dalam bentuk byte[] (JPEG)
     */
    public List<byte[]> getAllStudyImages(String studyId) {
        List<byte[]> images = new ArrayList<>();
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            requestEntity = new HttpEntity(headers);

            String seriesUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId + "/series";
            String seriesJson = getRest().exchange(seriesUrl, HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode seriesArray = mapper.readTree(seriesJson);

            for (JsonNode series : seriesArray) {
                String seriesId = series.path("ID").asText();
                images.addAll(getSeriesImages(seriesId));
            }
        } catch (Exception e) {
            System.out.println("Error in getAllStudyImages: " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }

    /**
     * Check if series should be processed based on modality and description
     */
    private boolean shouldProcessSeries(String seriesId) {
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            requestEntity = new HttpEntity(headers);

            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + seriesId;
            String seriesInfo = getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode seriesJson = mapper.readTree(seriesInfo);

            // Get modality and series description
            JsonNode mainTags = seriesJson.path("MainDicomTags");
            String modality = mainTags.path("Modality").asText();
            String seriesDesc = mainTags.path("SeriesDescription").asText().toUpperCase();

            System.out.println("Series " + seriesId + " - Modality: " + modality + ", Description: " + seriesDesc);

            // If CT, only process series containing "ELECTRONIC FILM"
            if ("CT".equalsIgnoreCase(modality)) {
                boolean shouldProcess = seriesDesc.contains("ELECTRONIC FILM");
                //System.out.println("CT series evaluation: " + shouldProcess);
                return shouldProcess;
            }

            // For other modalities, process all
            //System.out.println("Non-CT modality, processing all");
            return true;

        } catch (Exception e) {
            System.out.println("Error checking series " + seriesId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all images from a series as byte arrays
     */
    private List<byte[]> getSeriesImages(String seriesId) {
        List<byte[]> images = new ArrayList<>();
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            requestEntity = new HttpEntity(headers);

            // Get series details including instances
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + seriesId;
            String seriesJson = getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode seriesInfo = mapper.readTree(seriesJson);

            JsonNode instances = seriesInfo.path("Instances");
            System.out.println("Found " + instances.size() + " instances in series " + seriesId);

            // Process each instance
            for (JsonNode instance : instances) {
                String instanceId = instance.asText();
                try {
                    String imageUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + instanceId + "/rendered";

                    // Create new headers for image request
                    HttpHeaders imageHeaders = new HttpHeaders();
                    imageHeaders.add("Authorization", "Basic " + authEncrypt);
                    imageHeaders.add("Accept", "image/jpeg");
                    HttpEntity<String> entity = new HttpEntity<>(imageHeaders);

                    //System.out.println("Fetching instance image: " + imageUrl);
                    ResponseEntity<byte[]> response = getRest().exchange(
                            imageUrl,
                            HttpMethod.GET,
                            entity,
                            byte[].class
                    );

                    if (response.getStatusCode() == HttpStatus.OK
                            && response.getBody() != null
                            && response.getBody().length > 0) {

                        // Verify it's a valid JPEG image
                        if (isValidJpeg(response.getBody())) {
                            images.add(response.getBody());
                            //System.out.println("Successfully retrieved valid JPEG image for instance: " + instanceId+ " (size: " + response.getBody().length + " bytes)");
                        } else {
                            System.out.println("Invalid JPEG data for instance: " + instanceId);
                        }
                    } else {
                        System.out.println("Empty or invalid response for instance: " + instanceId);
                    }
                } catch (Exception e) {
                    System.out.println("Error fetching instance " + instanceId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Retrieved " + images.size() + " valid images from series " + seriesId);
        } catch (Exception e) {
            System.out.println("Error in getSeriesImages for series " + seriesId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }

// Helper method to check if byte array is a valid JPEG
    private boolean isValidJpeg(byte[] data) {
        return data != null
                && data.length > 2
                && (data[0] & 0xFF) == 0xFF
                && (data[1] & 0xFF) == 0xD8; // JPEG magic number
    }
    
    /**
     * Retrieves Osimis viewer URL for a DICOM study
     *
     * @param orderid AccessionNumber for primary search
     * @param patientId Patient ID for fallback search
     * @param studyDate Study date for fallback search (format: YYYY-MM-DD or
     * YYYYMMDD)
     * @return Osimis viewer URL or null if not found
     */
    public String getOsimisViewerUrl(String orderid, String patientId, String studyDate) {
        try {
            statusakses = "";

            // Input validation - ensure we have valid search criteria
            boolean hasOrderId = orderid != null && !orderid.trim().isEmpty();
            boolean hasPatientInfo = (patientId != null && !patientId.trim().isEmpty())
                    && (studyDate != null && !studyDate.trim().isEmpty());

            if (!hasOrderId && !hasPatientInfo) {
                System.out.println("Kriteria pencarian tidak valid: Tidak ada AccessionNumber atau PatientID/StudyDate");
                statusakses = "notfound";
                return null;
            }

            // Set up headers for Orthanc API authentication
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
            String findUrl = orthancUrl + "/tools/find";
            JsonNode studyIds = null;

            // Primary search by AccessionNumber (if available)
            if (hasOrderId) {
                requestJson = "{\n"
                        + "  \"Level\": \"Study\",\n"
                        + "  \"Query\": {\n"
                        + "    \"AccessionNumber\": \"" + orderid.trim() + "\"\n"
                        + "  }\n"
                        + "}";

//                System.out.println("Searching for AccessionNumber: " + orderid.trim());
                requestEntity = new HttpEntity(requestJson, headers);

                ResponseEntity<String> response = getRest().exchange(
                        findUrl,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                String searchResult = response.getBody();
                studyIds = mapper.readTree(searchResult);
            }

            // Fallback search by PatientID and StudyDate (only if primary search failed and we have patient info)
            if ((studyIds == null || studyIds.size() == 0) && hasPatientInfo) {
                System.out.println("Falling back to PatientID and StudyDate search");

                // Format study date to DICOM format (YYYYMMDD)
                String formattedStudyDate = studyDate.trim().replaceAll("[^0-9]", "");

                // Validate formatted date length
                if (formattedStudyDate.length() != 8) {
                    System.out.println("Format tanggal study tidak valid: " + studyDate + " (harap gunakan format YYYYMMDD)");
                    statusakses = "notfound";
                    return null;
                }

                requestJson = "{\n"
                        + "  \"Level\": \"Study\",\n"
                        + "  \"Query\": {\n"
                        + "    \"PatientID\": \"" + patientId.trim() + "\",\n"
                        + "    \"StudyDate\": \"" + formattedStudyDate + "\"\n"
                        + "  }\n"
                        + "}";

                requestEntity = new HttpEntity(requestJson, headers);
                ResponseEntity<String> response = getRest().exchange(
                        findUrl,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                String searchResult = response.getBody();
                studyIds = mapper.readTree(searchResult);
            }

            // Check if any studies were found
            if (studyIds == null || studyIds.size() == 0) {
                if (hasOrderId) {
                    System.out.println("Tidak ditemukan study untuk AccessionNumber: " + orderid.trim());
                } else {
                    System.out.println("Tidak ditemukan study untuk PatientID: " + patientId.trim()
                            + " dan StudyDate: " + studyDate.trim());
                }
                statusakses = "notfound";
                return null;
            }

            // Get the first study UUID (Orthanc internal ID)
            String studyUuid = studyIds.get(0).asText();

            // Validate UUID format (basic check)
            if (studyUuid == null || studyUuid.trim().isEmpty()) {
                System.out.println("UUID study tidak valid dari server Orthanc");
                statusakses = "notfound";
                return null;
            }

            statusakses = "ok";

            // Construct Osimis viewer URL
            String osimisViewerUrl = koneksiDB.URLORTHANC() + "/osimis-viewer/app/index.html?study=" + studyUuid;

//            System.out.println("Generated Osimis URL: " + osimisViewerUrl);
            return osimisViewerUrl;

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error accessing Orthanc: " + e.getStatusCode() + " - " + e.getMessage());
            statusakses = "error";
            showErrorDialog("Kesalahan mengakses server PACS: " + e.getStatusCode()
                    + "\nSilakan hubungi administrator.", "Kesalahan Koneksi PACS");

        } catch (ResourceAccessException e) {
            System.err.println("Network Error: " + e.getMessage());
            statusakses = "error";
            showErrorDialog("Tidak dapat terhubung ke server PACS.\nSilakan periksa koneksi jaringan dan hubungi administrator.",
                    "Kesalahan Jaringan");

        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            statusakses = "error";
            showErrorDialog("Respons tidak valid dari server PACS.\nSilakan hubungi administrator.",
                    "Kesalahan Format Data");

        } catch (Exception e) {
            System.err.println("Unexpected error retrieving study: " + e.getMessage());
            e.printStackTrace();
            statusakses = "error";
            showErrorDialog("Gagal mengambil data study dari PACS.\nSilakan hubungi administrator.",
                    "Kesalahan Sistem");
        }

        return null;
    }
    
    public String getStoneUrlViewer(String orderid, String patientId, String studyDate) {
        String studyInstanceUID = "";
        String urlOHIF = "";
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);

            // First try with AccessionNumber
            String findUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/tools/find";
            requestJson = "{\"Level\":\"Study\",\"Query\":{\"AccessionNumber\":\"" + orderid + "\"}}";
            System.out.println("Searching with AccessionNumber: " + orderid);

            requestEntity = new HttpEntity(requestJson, headers);
            String response = getRest().exchange(findUrl, HttpMethod.POST, requestEntity, String.class).getBody();
            root = mapper.readTree(response);

            // If no results with AccessionNumber, try with PatientID and StudyDate
            if (root.size() == 0 && patientId != null && !patientId.isEmpty()
                    && studyDate != null && !studyDate.isEmpty()) {

                // Format study date to DICOM format (YYYYMMDD)
                String formattedDate = studyDate.replaceAll("[^0-9]", "");

                requestJson = "{\"Level\":\"Study\",\"Query\":{"
                        + "\"PatientID\":\"" + patientId + "\","
                        + "\"StudyDate\":\"" + formattedDate + "\"}}";

                System.out.println("Falling back to PatientID/StudyDate search: " + patientId + "/" + formattedDate);
                requestEntity = new HttpEntity(requestJson, headers);
                response = getRest().exchange(findUrl, HttpMethod.POST, requestEntity, String.class).getBody();
                root = mapper.readTree(response);

                if (root.size() == 0) {
                    System.out.println("No studies found for PatientID: " + patientId + " and StudyDate: " + studyDate);
                    return null;
                }
            } else if (root.size() == 0) {
                System.out.println("No studies found for AccessionNumber: " + orderid);
                return null;
            }

            // Get study details for the first matching study
            String studyId = root.get(0).asText();
            String studyUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId;
            String studyResponse = getRest().exchange(studyUrl, HttpMethod.GET, requestEntity, String.class).getBody();

            // Parse study details to get StudyInstanceUID
            JsonNode studyDetails = mapper.readTree(studyResponse);
            studyInstanceUID = studyDetails.path("MainDicomTags").path("StudyInstanceUID").asText();

            // Build OHIF viewer URL
            urlOHIF = koneksiDB.URLORTHANC() + "/stone-webviewer/index.html?study=" + studyInstanceUID;
            System.out.println("Generated Stone Viewer URL: " + urlOHIF);

            return urlOHIF;

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error: " + e.getStatusCode() + " - " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error accessing PACS server: " + e.getStatusCode(),
                    "PACS Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException e) {
            System.err.println("Network Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to PACS server. Please check network connection.",
                    "Network Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error retrieving study: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to retrieve study data from PACS.",
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
     public String getOHIFUrlViewer(String orderid, String patientId, String studyDate) {
        String studyInstanceUID = "";
        String urlOHIF = "";
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);

            // First try with AccessionNumber
            String findUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/tools/find";
            requestJson = "{\"Level\":\"Study\",\"Query\":{\"AccessionNumber\":\"" + orderid + "\"}}";
            System.out.println("Searching with AccessionNumber: " + orderid);

            requestEntity = new HttpEntity(requestJson, headers);
            String response = getRest().exchange(findUrl, HttpMethod.POST, requestEntity, String.class).getBody();
            root = mapper.readTree(response);

            // If no results with AccessionNumber, try with PatientID and StudyDate
            if (root.size() == 0 && patientId != null && !patientId.isEmpty()
                    && studyDate != null && !studyDate.isEmpty()) {

                // Format study date to DICOM format (YYYYMMDD)
                String formattedDate = studyDate.replaceAll("[^0-9]", "");

                requestJson = "{\"Level\":\"Study\",\"Query\":{"
                        + "\"PatientID\":\"" + patientId + "\","
                        + "\"StudyDate\":\"" + formattedDate + "\"}}";

                System.out.println("Falling back to PatientID/StudyDate search: " + patientId + "/" + formattedDate);
                requestEntity = new HttpEntity(requestJson, headers);
                response = getRest().exchange(findUrl, HttpMethod.POST, requestEntity, String.class).getBody();
                root = mapper.readTree(response);

                if (root.size() == 0) {
                    System.out.println("No studies found for PatientID: " + patientId + " and StudyDate: " + studyDate);
                    return null;
                }
            } else if (root.size() == 0) {
                System.out.println("No studies found for AccessionNumber: " + orderid);
                return null;
            }

            // Get study details for the first matching study
            String studyId = root.get(0).asText();
            String studyUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId;
            String studyResponse = getRest().exchange(studyUrl, HttpMethod.GET, requestEntity, String.class).getBody();

            // Parse study details to get StudyInstanceUID
            JsonNode studyDetails = mapper.readTree(studyResponse);
            studyInstanceUID = studyDetails.path("MainDicomTags").path("StudyInstanceUID").asText();

            // Build OHIF viewer URL
            urlOHIF = koneksiDB.URLORTHANC() + "/ohif/viewer?StudyInstanceUIDs=" + studyInstanceUID;
            System.out.println("Generated OHIF Viewer URL: " + urlOHIF);

            return urlOHIF;

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error: " + e.getStatusCode() + " - " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error accessing PACS server: " + e.getStatusCode(),
                    "PACS Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException e) {
            System.err.println("Network Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to PACS server. Please check network connection.",
                    "Network Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error retrieving study: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to retrieve study data from PACS.",
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
     
     public boolean sendStudyByAccessionNumber(String accessionNumber, String targetAETitle, String host, int port) {
//        try {
            // 1. Find the study by accession number
//            String studyId = getStudyID(accessionNumber);
//            if (studyId == null || studyId.isEmpty()) {
//                System.out.println("No study found with accession number: " + accessionNumber);
//                return false;
//            }

            // 2. Get all instances in the study
//            headers = new HttpHeaders();
//            headers.add("Authorization", "Basic " + authEncrypt);
//            requestEntity = new HttpEntity(headers);

//            String studyUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId;
//            ResponseEntity<String> studyResponse = getRest().exchange(studyUrl, HttpMethod.GET, requestEntity, String.class);

//            if (studyResponse.getStatusCode() != HttpStatus.OK) {
//                System.out.println("Failed to retrieve study " + studyId + ": HTTP " + studyResponse.getStatusCode());
//                return false;
//            }

//            String studyJson = studyResponse.getBody();
//            JsonNode studyNode = mapper.readTree(studyJson);
//            JsonNode instances = studyNode.path("Instances");


            // 3. First, ensure the modality is configured in Orthanc
//            if (!ensureModalityConfigured(targetAETitle, host, port)) {
//                System.out.println("Failed to configure modality: " + targetAETitle);
//                return false;
//            }

            // 4. Send entire study at once (matches Orthanc GUI behavior)
//            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();

//            try {
                // Send complete study (same as pressing Store button in Orthanc GUI)
//                String requestBody = String.format(
//                        "{\"Resources\": [\"%s\"], \"TargetAet\": \"%s\", \"LocalAet\": \"%s\", \"Timeout\": 30, \"StorageCommitment\": false}",
//                        studyId, // Send entire study ID - matches GUI behavior
//                        targetAETitle,
//                        koneksiDB.AETITLEORTHANC()
//                );

//                HttpHeaders storeHeaders = new HttpHeaders();
//                storeHeaders.add("Authorization", "Basic " + authEncrypt);
//                storeHeaders.setContentType(MediaType.APPLICATION_JSON);
//                HttpEntity<String> storeEntity = new HttpEntity<>(requestBody, storeHeaders);

//                String storeUrl = orthancUrl + "/modalities/" + targetAETitle + "/store";
//                ResponseEntity<String> response = getRest().exchange(
//                        storeUrl,
//                        HttpMethod.POST,
//                        storeEntity,
//                        String.class
//                );

//                if (response.getStatusCode() == HttpStatus.OK) {
//                    System.out.println("Successfully sent complete study " + studyId + " (accession: " + accessionNumber + ") with " + instances.size() + " instances to " + targetAETitle);
//                    return true;
//                } else {
//                    System.out.println("Failed to send study " + studyId + ": HTTP " + response.getStatusCode() + " - " + response.getBody());
//                    return false;
//                }

//            } catch (HttpClientErrorException | HttpServerErrorException e) {
//                System.out.println("HTTP error sending study " + studyId + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
//                return false;
//            } catch (ResourceAccessException e) {
//                System.out.println("Network error sending study " + studyId + " to " + host + ":" + port + " - " + e.getMessage());
//                return false;
//            }

//        } catch (JsonProcessingException e) {
//            System.out.println("JSON parsing error for accession " + accessionNumber + ": " + e.getMessage());
//            return false;
//        } catch (Exception e) {
//            System.out.println("Unexpected error in sendStudyByAccessionNumber for accession " + accessionNumber + ": " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
        return false;
    }

    /**
     * Kirim study yang SUDAH ada di Orthanc (dicari lewat AccessionNumber) ke DICOM Router, dengan
     * AdmissionID (0038,0010) = Encounter ID disuntikkan lebih dulu. Router SATUSEHAT memakai tag itu
     * untuk mengaitkan ImagingStudy ke Encounter; tanpa tag itu kiriman ditolak
     * ("AdmissionID (Encounter ID) is missing") — itu bedanya dengan overload 4-argumen di atas, yang
     * mengirim study apa adanya.
     *
     * Yang dikirim SELALU salinan ber-UID baru hasil Orthanc modify, bukan study asli — lihat alasannya
     * di {@link #salinStudyDenganAdmissionID}. Salinan dihapus lagi setelah terkirim.
     *
     * @param accessionNumber AccessionNumber study (untuk radiologi = permintaan_radiologi.noorder)
     * @param encounterId     Encounter ID SATUSEHAT; wajib, kiriman dibatalkan bila kosong
     * @param targetAETitle   AE Title DICOM Router (AETITLEDICOMROUTER)
     * @param host            host DICOM Router (URLDICOMROUTER) — dipakai saat mendaftarkan modality
     * @param port            port DICOM Router (PORTDICOMROUTER)
     * @return true bila C-STORE ke router berhasil
     */
    public boolean sendStudyByAccessionNumber(String accessionNumber, String encounterId,
            String targetAETitle, String host, int port) {
//        if (encounterId == null || encounterId.trim().isEmpty()) {
//            System.out.println("Encounter ID kosong; kiriman DICOM accession " + accessionNumber
//                    + " dibatalkan (router mewajibkan AdmissionID).");
//            return false;
//        }
//        String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
//        String studyTransient = "";   // salinan hasil modify: dikirim, lalu dibersihkan di finally
//        try {
//            String studyId = getStudyID(accessionNumber);
//            if (studyId == null || studyId.isEmpty()) {
//                System.out.println("Tidak ada study di Orthanc dgn AccessionNumber " + accessionNumber + ", dilewati.");
//                return false;
//            }

            // Jejak PatientID apa adanya. Bila router menolak karena identitas pasien (mis. menuntut
            // nomor IHS, bukan no.RM dari modality), penyebabnya langsung terbaca di log tanpa menebak.
//            System.out.println("Study " + studyId + " accession=" + accessionNumber
//                    + " PatientID=" + bacaTagStudy(orthancUrl, studyId, "PatientID"));

//            studyTransient = salinStudyDenganAdmissionID(orthancUrl, studyId, encounterId.trim());
//            if (studyTransient.isEmpty()) {
//                System.out.println("Gagal menyuntik AdmissionID pada study " + studyId
//                        + "; kiriman dibatalkan (router pasti menolak tanpa tag itu).");
//                return false;
//            }
//            String studyToSend = studyTransient;
//            System.out.println("AdmissionID(Encounter)=" + encounterId.trim()
//                    + " disuntikkan -> salinan study " + studyToSend);

//            if (!pastikanModalityRouter(targetAETitle, host, port)) {
//                return false;
//            }

//            String requestBody = String.format(
//                    "{\"Resources\": [\"%s\"], \"TargetAet\": \"%s\", \"LocalAet\": \"%s\", \"Timeout\": 30, \"StorageCommitment\": false}",
//                    studyToSend, targetAETitle, koneksiDB.AETITLEORTHANC());
//            HttpHeaders storeHeaders = new HttpHeaders();
//            storeHeaders.add("Authorization", "Basic " + authEncrypt);
//            storeHeaders.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> storeEntity = new HttpEntity<String>(requestBody, storeHeaders);
//            ResponseEntity<String> response = getRest().exchange(
//                    orthancUrl + "/modalities/" + targetAETitle + "/store",
//                    HttpMethod.POST, storeEntity, String.class);
//            if (response.getStatusCode() == HttpStatus.OK) {
//                System.out.println("Study " + studyToSend + " (accession " + accessionNumber
//                        + ") terkirim ke " + targetAETitle + ".");
//                return true;
//            }
//            System.out.println("Gagal kirim study " + studyToSend + ": HTTP " + response.getStatusCode()
//                    + " - " + response.getBody());
//            return false;

//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            System.out.println("HTTP error kirim study accession " + accessionNumber + ": "
//                    + e.getStatusCode() + " - " + e.getResponseBodyAsString());
//            return false;
//        } catch (ResourceAccessException e) {
//            System.out.println("Network error kirim study accession " + accessionNumber
//                    + " ke " + host + ":" + port + " - " + e.getMessage());
//            return false;
//        } catch (Exception e) {
//            System.out.println("Unexpected error kirim study accession " + accessionNumber + ": " + e.getMessage());
//            return false;
//        } finally {
            // Salinan hasil modify tidak boleh menumpuk di Orthanc tiap kali tombol Kirim ditekan.
//            if (!studyTransient.isEmpty()) {
//                hapusStudyTransient(orthancUrl, studyTransient);
//            }
//        }
        return false;
    }

    /**
     * Baca satu tag DICOM dari instance pertama sebuah study (mis. AdmissionID/PatientID). Dibaca dari
     * instance, BUKAN dari /studies/{id}, karena AdmissionID (0038,0010) bukan main-tag study di Orthanc.
     * Best-effort: "" bila study kosong, tag absen, atau Orthanc gagal dihubungi.
     */
    private String bacaTagStudy(String orthancUrl, String studyId, String namaTag) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Basic " + authEncrypt);
            HttpEntity<Void> e = new HttpEntity<Void>(h);
            JsonNode instances = mapper.readTree(getRest().exchange(
                    orthancUrl + "/studies/" + studyId + "/instances",
                    HttpMethod.GET, e, String.class).getBody());
            if (!instances.isArray() || instances.size() == 0) {
                return "";
            }
            String instanceId = instances.get(0).path("ID").asText();
            if (instanceId.isEmpty()) {
                return "";
            }
            JsonNode tags = mapper.readTree(getRest().exchange(
                    orthancUrl + "/instances/" + instanceId + "/tags?simplify",
                    HttpMethod.GET, e, String.class).getBody());
            JsonNode nilai = tags.path(namaTag);
            return nilai.isMissingNode() || nilai.isNull() ? "" : nilai.asText();
        } catch (Exception ex) {
            System.out.println("Gagal baca tag " + namaTag + " study " + studyId + " : " + ex.getMessage());
            return "";
        }
    }

    /**
     * Buat SALINAN study dgn AdmissionID(0038,0010)=encounterId via Orthanc modify.
     *
     * Klausa Keep SENGAJA tidak dipakai, jadi salinan ber-Study/Series/SOP UID baru. Itu bukan efek
     * samping, tapi yang diinginkan: DICOM Router meng-upsert ImagingStudy dengan kunci AccessionNumber
     * (bukan StudyInstanceUID), sehingga UID baru TIDAK melahirkan ImagingStudy ganda — sementara
     * me-store ulang study ber-UID sama akan di-dedup router dan tidak pernah di-requeue oleh scheduler
     * push binary-nya, alias citranya tak akan pernah sampai ke PACS SATUSEHAT.
     *
     * KeepSource ditulis EKSPLISIT true, tidak diandalkan defaultnya: study asli di sini adalah citra
     * PACS pasien, tidak boleh ikut terhapus.
     *
     * @return id study salinan, "" bila gagal
     */
    private String salinStudyDenganAdmissionID(String orthancUrl, String studyId, String encounterId) {
        try {
            ObjectNode replace = mapper.createObjectNode();
            replace.put("AdmissionID", encounterId);
            ObjectNode permintaan = mapper.createObjectNode();
            permintaan.set("Replace", replace);
            permintaan.put("KeepSource", true);
            permintaan.put("Force", true);
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Basic " + authEncrypt);
            h.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> e = new HttpEntity<String>(mapper.writeValueAsString(permintaan), h);
            ResponseEntity<String> resp = getRest().exchange(
                    orthancUrl + "/studies/" + studyId + "/modify", HttpMethod.POST, e, String.class);
            return mapper.readTree(resp.getBody()).path("ID").asText();
        } catch (Exception ex) {
            System.out.println("Gagal menyuntik AdmissionID study " + studyId + " : " + ex.getMessage());
            return "";
        }
    }

    /** Hapus salinan study transient dari Orthanc (best-effort). */
    private void hapusStudyTransient(String orthancUrl, String studyId) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Basic " + authEncrypt);
            getRest().exchange(orthancUrl + "/studies/" + studyId, HttpMethod.DELETE,
                    new HttpEntity<Void>(h), String.class);
        } catch (Exception ex) {
            System.out.println("Gagal hapus salinan study " + studyId + " : " + ex.getMessage());
        }
    }

    /**
     * Daftarkan/perbaiki modality router di Orthanc. Beda dari {@link #ensureModalityConfigured}:
     * AET yang didaftarkan = targetAETitle (bukan konstanta "DICOM"), host DIMSE di-strip skema
     * http(s):// — kalau skema ikut terbawa Orthanc gagal "unknown host" — dan SELALU PUT supaya
     * konfigurasi lama yang salah ikut terkoreksi, bukan dibiarkan hanya karena entrinya sudah ada.
     */
    private boolean pastikanModalityRouter(String targetAETitle, String host, int port) {
        try {
            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
            String dimseHost = (host == null ? "" : host).replaceFirst("^(?i)https?://", "").trim();
            String modalityConfig = String.format("[\"%s\", \"%s\", %d]", targetAETitle, dimseHost, port);
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Basic " + authEncrypt);
            h.setContentType(MediaType.APPLICATION_JSON);
            getRest().exchange(orthancUrl + "/modalities/" + targetAETitle, HttpMethod.PUT,
                    new HttpEntity<String>(modalityConfig, h), String.class);
            System.out.println("Modality " + targetAETitle + " diset -> host=" + dimseHost + " port=" + port);
            return true;
        } catch (Exception e) {
            System.out.println("Gagal set modality " + targetAETitle + " : " + e.getMessage());
            return false;
        }
    }

     /**
     * Ensures the target modality is properly configured in Orthanc Critical
     * for healthcare DICOM routing reliability
     */
    private boolean ensureModalityConfigured(String targetAETitle, String host, int port) {
        try {
            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
            String modalityUrl = orthancUrl + "/modalities/" + targetAETitle;

            // Check if modality exists
            try {
                headers = new HttpHeaders();
                headers.add("Authorization", "Basic " + authEncrypt);
                requestEntity = new HttpEntity(headers);

                getRest().exchange(modalityUrl, HttpMethod.GET, requestEntity, String.class);
                return true; // Modality already configured

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    // Modality doesn't exist, create it
                    String modalityConfig = String.format("[\"DICOM\", \"%s\", %d]", host, port);

                    HttpHeaders configHeaders = new HttpHeaders();
                    configHeaders.add("Authorization", "Basic " + authEncrypt);
                    configHeaders.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> configEntity = new HttpEntity<>(modalityConfig, configHeaders);

                    ResponseEntity<String> configResponse = getRest().exchange(
                            modalityUrl, HttpMethod.PUT, configEntity, String.class);

                    if (configResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("Successfully configured modality: " + targetAETitle + " -> " + host + ":" + port);
                        return true;
                    } else {
                        System.out.println("Failed to configure modality " + targetAETitle + ": HTTP " + configResponse.getStatusCode());
                        return false;
                    }
                }
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error configuring modality " + targetAETitle + ": " + e.getMessage());
            return false;
        }
    }
    
   public JsonNode AmbilPngUsg(String NoRawat,String Series,String norawatslash){
        System.out.println("Percobaan Mengambil Gambar PNG : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            System.out.println("Auth : "+authEncrypt);
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
       //        Menambahkan fitur simpan gambar radiologi dari orthanc
                 uploadImageUsg(NoRawat+i+".png","pages/upload");     
                 Sequel.menyimpantf("hasil_pemeriksaan_usg_gambar","?,?","No.Rawat",2,new String[]{
                                norawatslash,"pages/upload/"+NoRawat+i+".png"
                            });
                i++; 
            } 
            JOptionPane.showMessageDialog(null,"Penyimpanan Gambar PNG dari Orthanc ke Webapps berhasil");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar PNG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }
 
 public JsonNode AmbilJpgUsg(String NoRawat,String Series,String norawatslash){
     
     System.out.println("Percobaan Mengambil Gambar JPG : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            System.out.println("Auth : "+authEncrypt);
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
                 
                 uploadImageUsg(NoRawat+i+".jpg","pages/upload");     
                 Sequel.menyimpantf("hasil_pemeriksaan_usg_gambar","?,?","No.Rawat",2,new String[]{
                                norawatslash,"pages/upload/"+NoRawat+i+".jpg"
                            });
                i++; 
            }
            JOptionPane.showMessageDialog(null,"Penyimpanan Gambar JPG dari Orthanc berhasil");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar JPG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }
 
 void uploadImageUsg(String FileName,String docpath){
    try{
        File file =new File("gambarradiologi/"+FileName);
        byte[] data = new byte[(int) file.length()];
        data = FileUtils.readFileToByteArray(file);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/hasilpemeriksaanusg/upload.php?doc="+docpath);
        ByteArrayBody fileData = new ByteArrayBody(data, FileName);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("file", fileData); 
        postRequest.setEntity(reqEntity);
        httpClient.execute(postRequest); 
       //deleteFile();        
        }catch (Exception e){
            System.out.println("Upload error"+e);
        }
    }
    
    public JsonNode AmbilJpgUsg2(String NoRawat,String Series,String norawatslash){
     
     System.out.println("Percobaan Mengambil Gambar JPG : "+NoRawat+", Series : "+Series);
        try{
            headers = new HttpHeaders();
            System.out.println("Auth : "+authEncrypt);
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
                 
                 uploadImageUsg2(NoRawat+i+".jpg","pages/upload");     
                 Sequel.menyimpantf("hasil_pemeriksaan_usg_gynecologi_gambar","?,?","No.Rawat",2,new String[]{
                                norawatslash,"pages/upload/"+NoRawat+i+".jpg"
                            });
                i++; 
            }
            JOptionPane.showMessageDialog(null,"Penyimpanan Gambar JPG dari Orthanc berhasil");
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal mengambil Gambar JPG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }
    
    public JsonNode AmbilJpg2(String Series) {
        System.out.println("Percobaan Mengambil Gambar JPG : " + Series + ", Series : " + Series);
        try {
            headers = new HttpHeaders();
            System.out.println("Auth : " + authEncrypt);
            headers.add("Authorization", "Basic " + authEncrypt);
            requestEntity = new HttpEntity(headers);
            System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + Series);
            requestJson = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/series/" + Series, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : " + requestJson);
            root = mapper.readTree(requestJson);
            for (JsonNode list : root.path("Instances")) {
                headers = new HttpHeaders();
                headers.add("Authorization", "Basic " + authEncrypt);
                headers.add("Accept", "image/jpeg");
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<byte[]> response = getRest().exchange(koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances/" + list.asText() + "/preview", HttpMethod.GET, entity, byte[].class);
                Files.write(Paths.get("./gambarradiologi/" + Series + ".jpg"), response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null, "Gagal mengambil Gambar JPG dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }
    
 
 void uploadImageUsg2(String FileName,String docpath){
    try{
        File file =new File("gambarradiologi/"+FileName);
        byte[] data = new byte[(int) file.length()];
        data = FileUtils.readFileToByteArray(file);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/hasilpemeriksaanusggynecologi/upload.php?doc="+docpath);
        ByteArrayBody fileData = new ByteArrayBody(data, FileName);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("file", fileData); 
        postRequest.setEntity(reqEntity);
        httpClient.execute(postRequest); 
       //deleteFile();        
        }catch (Exception e){
            System.out.println("Upload error"+e);
        }
    }
 
 
    void deleteFile(){
       File file = new File("gambarradiologi");
        String[] myFiles;
        if (file.isDirectory()) {
            myFiles = file.list();
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
                myFile.delete();
            }
        }
   }

    /**
     * Upload satu file DICOM ke Orthanc PACS (POST /instances).
     * Menampilkan dialog hasil ke pengguna.
     *
     * @param filePath path lengkap file DICOM (.dcm) yang akan diunggah
     * @return JsonNode respon Orthanc (berisi ID, ParentStudy, ParentSeries,
     * ParentPatient, Status), atau null bila gagal
     */
    public JsonNode uploadDicom(String filePath) {
        System.out.println("Percobaan Upload DICOM ke Orthanc : " + filePath);
        root = null;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                JOptionPane.showMessageDialog(null, "File DICOM tidak ditemukan : " + filePath);
                return null;
            }
            byte[] data = FileUtils.readFileToByteArray(file);
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.parseMediaType("application/dicom"));
            HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances";
            System.out.println("URL : " + url);
            requestJson = getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody();
            System.out.println("Result JSON : " + requestJson);
            root = mapper.readTree(requestJson);
            String status = root.path("Status").asText();
            JOptionPane.showMessageDialog(null, "Upload DICOM ke Orthanc berhasil (Status: " + status + ") ..!!");
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error upload DICOM ke Orthanc: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            JOptionPane.showMessageDialog(null, "Gagal upload DICOM ke Orthanc (HTTP " + e.getStatusCode() + "). Pastikan file DICOM valid ..!!");
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null, "Gagal upload DICOM ke Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    /**
     * Versi senyap (tanpa dialog) untuk upload satu file DICOM, dipakai bila
     * dipanggil dalam perulangan atau proses batch.
     *
     * @param filePath path lengkap file DICOM
     * @return true bila berhasil tersimpan di Orthanc
     */
    public boolean uploadDicomSilent(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                System.out.println("File DICOM tidak ditemukan : " + filePath);
                return false;
            }
            byte[] data = FileUtils.readFileToByteArray(file);
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.parseMediaType("application/dicom"));
            HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/instances";
            ResponseEntity<String> response = getRest().exchange(url, HttpMethod.POST, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error upload DICOM (" + filePath + "): " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.out.println("Error upload DICOM (" + filePath + ") : " + e);
            return false;
        }
    }

    /**
     * Upload seluruh file DICOM (.dcm) di dalam sebuah folder ke Orthanc PACS.
     * Menampilkan ringkasan jumlah berhasil/gagal ke pengguna.
     *
     * @param folderPath path folder berisi file .dcm
     * @return jumlah file yang berhasil diunggah
     */
    public int uploadDicomFolder(String folderPath) {
        int berhasil = 0, gagal = 0;
        System.out.println("Percobaan Upload Folder DICOM ke Orthanc : " + folderPath);
        try {
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                JOptionPane.showMessageDialog(null, "Folder tidak ditemukan : " + folderPath);
                return 0;
            }
            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(null, "Tidak ada file di dalam folder : " + folderPath);
                return 0;
            }
            for (File f : files) {
                if (f.isFile() && f.getName().toLowerCase().endsWith(".dcm")) {
                    if (uploadDicomSilent(f.getAbsolutePath())) {
                        berhasil++;
                    } else {
                        gagal++;
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Upload Folder DICOM ke Orthanc selesai.\nBerhasil : " + berhasil + " file\nGagal : " + gagal + " file");
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null, "Gagal upload folder DICOM ke Orthanc, silahkan hubungi administrator ..!!");
        }
        return berhasil;
    }

    /**
     * Mengubah (edit) AccessionNumber sebuah study di Orthanc PACS via
     * POST /studies/{id}/modify. Memakai KeepSource=false sehingga study lama
     * digantikan study baru dengan AccessionNumber yang baru (edit di tempat).
     *
     * Catatan: operasi modify akan menghasilkan StudyInstanceUID baru. Pencarian
     * di SIMRS dilakukan berbasis AccessionNumber sehingga tetap konsisten.
     *
     * @param studyId Orthanc internal study ID (UUID), mis. dari kolom "ID Studies"
     * @param newAccessionNumber AccessionNumber baru (mis. noorder radiologi)
     * @return true bila berhasil
     */
    public boolean editAccessionNumber(String studyId, String newAccessionNumber) {
        try {
            if (studyId == null || studyId.trim().isEmpty()) {
                System.out.println("Study ID tidak valid untuk edit AccessionNumber");
                return false;
            }
            if (newAccessionNumber == null) {
                newAccessionNumber = "";
            }
            // Escape karakter khusus JSON agar payload aman
            String safeAcc = newAccessionNumber.trim().replace("\\", "\\\\").replace("\"", "\\\"");

            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{"
                    + "\"Replace\": {\"AccessionNumber\": \"" + safeAcc + "\"},"
                    + "\"Force\": true,"
                    + "\"KeepSource\": false"
                    + "}";

            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId + "/modify";
            System.out.println("URL : " + url + " - Body : " + body);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = getRest().exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                root = mapper.readTree(response.getBody());
                System.out.println("AccessionNumber berhasil diubah menjadi '" + newAccessionNumber.trim() + "'. Study baru : " + root.path("ID").asText());
                return true;
            }
            System.out.println("Gagal ubah AccessionNumber: HTTP " + response.getStatusCode() + " - " + response.getBody());
            return false;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP Error ubah AccessionNumber: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (ResourceAccessException e) {
            System.err.println("Network Error ubah AccessionNumber: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error ubah AccessionNumber : " + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengambil detail sebuah study (GET /studies/{id}) termasuk MainDicomTags
     * dan PatientMainDicomTags. Dipakai untuk memuat nilai tag saat edit.
     *
     * @param studyId Orthanc internal study ID (UUID)
     * @return JsonNode detail study, atau null bila gagal
     */
    public JsonNode getStudyDetail(String studyId) {
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            requestEntity = new HttpEntity(headers);
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId;
            String json = getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
            return mapper.readTree(json);
        } catch (Exception e) {
            System.out.println("Error getStudyDetail : " + e);
            return null;
        }
    }

    /**
     * Mengubah (edit) beberapa tag DICOM sekaligus pada sebuah study di Orthanc
     * via POST /studies/{id}/modify (Replace + Force + KeepSource=false).
     * Operasi ini menghasilkan StudyInstanceUID baru (modify default Orthanc).
     *
     * @param studyId Orthanc internal study ID (UUID)
     * @param replace map nama tag DICOM -> nilai baru
     * @return true bila berhasil
     */
    public boolean modifyStudyTags(String studyId, java.util.Map<String, String> replace) {
        try {
            if (studyId == null || studyId.trim().isEmpty()) {
                System.out.println("Study ID tidak valid untuk edit DICOM tag");
                return false;
            }
            if (replace == null || replace.isEmpty()) {
                System.out.println("Tidak ada tag yang diubah");
                return false;
            }
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            StringBuilder rep = new StringBuilder();
            boolean first = true;
            for (java.util.Map.Entry<String, String> e : replace.entrySet()) {
                if (e.getKey() == null || e.getKey().trim().isEmpty()) {
                    continue;
                }
                if (!first) {
                    rep.append(",");
                }
                rep.append("\"").append(escJson(e.getKey().trim())).append("\": \"")
                        .append(escJson(e.getValue() == null ? "" : e.getValue())).append("\"");
                first = false;
            }

            String body = "{\"Replace\": {" + rep.toString() + "}, \"Force\": true, \"KeepSource\": false}";
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/studies/" + studyId + "/modify";
            System.out.println("URL : " + url + " - Body : " + body);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = getRest().exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                root = mapper.readTree(response.getBody());
                System.out.println("DICOM tag berhasil diubah. Study baru : " + root.path("ID").asText());
                return true;
            }
            System.out.println("Gagal ubah DICOM tag: HTTP " + response.getStatusCode() + " - " + response.getBody());
            return false;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP Error ubah DICOM tag: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (ResourceAccessException e) {
            System.err.println("Network Error ubah DICOM tag: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error ubah DICOM tag : " + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengambil daftar study DICOM yang ada di server Orthanc (POST /tools/find,
     * Level=Study, Expand=true). Semua parameter filter opsional; bila kosong
     * maka mengembalikan seluruh study yang ada di server.
     *
     * @param patientId filter PatientID (partial, otomatis dibungkus wildcard)
     * @param patientName filter Nama Pasien (partial)
     * @param accessionNumber filter AccessionNumber (partial)
     * @param studyDate filter Tanggal Study (format DICOM YYYYMMDD atau range
     * YYYYMMDD-YYYYMMDD)
     * @return JsonNode berupa array study lengkap dengan MainDicomTags &
     * PatientMainDicomTags, atau null bila gagal
     */
    public JsonNode listStudies(String patientId, String patientName, String accessionNumber, String studyDate) {
        root = null;
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<String> parts = new ArrayList<>();
            if (patientId != null && !patientId.trim().isEmpty()) {
                parts.add("\"PatientID\": \"*" + escJson(patientId.trim()) + "*\"");
            }
            if (patientName != null && !patientName.trim().isEmpty()) {
                parts.add("\"PatientName\": \"*" + escJson(patientName.trim()) + "*\"");
            }
            if (accessionNumber != null && !accessionNumber.trim().isEmpty()) {
                parts.add("\"AccessionNumber\": \"*" + escJson(accessionNumber.trim()) + "*\"");
            }
            if (studyDate != null && !studyDate.trim().isEmpty()) {
                parts.add("\"StudyDate\": \"" + escJson(studyDate.trim().replaceAll("[^0-9-]", "")) + "\"");
            }

            String body = "{"
                    + "\"Level\": \"Study\","
                    + "\"Expand\": true,"
                    + "\"Query\": {" + String.join(",", parts) + "}"
                    + "}";

            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/tools/find";
            System.out.println("URL : " + url + " - Body : " + body);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            requestJson = getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody();
            root = mapper.readTree(requestJson);
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null, "Gagal mengambil daftar DICOM dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return root;
    }

    /** Menyusun bagian "Query" JSON untuk tools/find &amp; count-resources. */
    private String buildStudyQueryJson(String patientId, String patientName, String accessionNumber, String studyDate) {
        List<String> parts = new ArrayList<>();
        if (patientId != null && !patientId.trim().isEmpty()) {
            parts.add("\"PatientID\": \"*" + escJson(patientId.trim()) + "*\"");
        }
        if (patientName != null && !patientName.trim().isEmpty()) {
            parts.add("\"PatientName\": \"*" + escJson(patientName.trim()) + "*\"");
        }
        if (accessionNumber != null && !accessionNumber.trim().isEmpty()) {
            parts.add("\"AccessionNumber\": \"*" + escJson(accessionNumber.trim()) + "*\"");
        }
        if (studyDate != null && !studyDate.trim().isEmpty()) {
            parts.add("\"StudyDate\": \"" + escJson(studyDate.trim().replaceAll("[^0-9-]", "")) + "\"");
        }
        return "{" + String.join(",", parts) + "}";
    }

    /**
     * Menghitung jumlah study yang cocok dengan filter (POST
     * /tools/count-resources). Ringan, hanya mengembalikan angka.
     *
     * @return jumlah study, atau -1 bila gagal
     */
    public int countStudies(String patientId, String patientName, String accessionNumber, String studyDate) {
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{\"Level\": \"Study\", \"Query\": "
                    + buildStudyQueryJson(patientId, patientName, accessionNumber, studyDate) + "}";
            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/tools/count-resources";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            String resp = getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody();
            return mapper.readTree(resp).path("Count").asInt(-1);
        } catch (Exception e) {
            System.out.println("Error countStudies : " + e);
            return -1;
        }
    }

    /**
     * Mengambil daftar study DICOM per halaman (paginasi sisi-server) dengan
     * Limit + Since, terurut terbaru dulu (OrderBy StudyDate/StudyTime DESC).
     * Bila server tidak mendukung OrderBy, otomatis fallback tanpa OrderBy.
     *
     * @param since offset awal (mis. halaman * limit)
     * @param limit jumlah maksimal per halaman (mis. 50)
     * @return JsonNode array study untuk halaman tsb, atau null bila gagal
     */
    public JsonNode listStudiesPaged(String patientId, String patientName, String accessionNumber, String studyDate, int since, int limit) {
        JsonNode r = doFindPaged(patientId, patientName, accessionNumber, studyDate, since, limit, true);
        if (r == null) {
            r = doFindPaged(patientId, patientName, accessionNumber, studyDate, since, limit, false);
        }
        if (r == null) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil daftar DICOM dari Orthanc, silahkan hubungi administrator ..!!");
        }
        return r;
    }

    private JsonNode doFindPaged(String patientId, String patientName, String accessionNumber, String studyDate, int since, int limit, boolean withOrderBy) {
        try {
            headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + authEncrypt);
            headers.setContentType(MediaType.APPLICATION_JSON);

            StringBuilder body = new StringBuilder();
            body.append("{\"Level\": \"Study\", \"Expand\": true, \"Query\": ")
                    .append(buildStudyQueryJson(patientId, patientName, accessionNumber, studyDate))
                    .append(", \"Since\": ").append(since)
                    .append(", \"Limit\": ").append(limit);
            if (withOrderBy) {
                body.append(", \"OrderBy\": [")
                        .append("{\"Type\":\"DicomTag\",\"Key\":\"StudyDate\",\"Direction\":\"DESC\"},")
                        .append("{\"Type\":\"DicomTag\",\"Key\":\"StudyTime\",\"Direction\":\"DESC\"}]");
            }
            body.append("}");

            String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/tools/find";
            System.out.println("URL : " + url + " - Body : " + body);

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            String resp = getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody();
            return mapper.readTree(resp);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("tools/find paged error (orderBy=" + withOrderBy + "): " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println("Error doFindPaged (orderBy=" + withOrderBy + ") : " + e);
            return null;
        }
    }

    /** Escape karakter khusus JSON (backslash & double-quote). */
    private String escJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }


}
