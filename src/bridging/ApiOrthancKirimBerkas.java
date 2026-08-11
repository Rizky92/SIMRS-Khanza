//package bridging.satusehat;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import fungsi.koneksiDB;
//import java.io.File;
//import java.nio.file.Files;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
//import org.springframework.web.client.RestTemplate;

///**
// * Klien Orthanc KHUSUS fitur "Kirim DICOM Berkas" (dokumen DOC yg di-wrap DICOM).
// * Dipisah dari {@link ApiOrthanc} agar perubahan alur berkas TIDAK mengganggu fitur lain
// * (radiologi/viewer) yang memakai ApiOrthanc.
// *
// * Alur: baca .dcm (lokal/HTTP) -> upload ke Orthanc (disimpan) -> suntik AdmissionID(0038,0010)=Encounter ID
// * (router menolak DOC DICOM tanpa tag ini) -> C-STORE study bertag ke DICOM Router -> hapus salinan sementara.
// *
// * @author claude
// */
//public class ApiOrthancKirimBerkas {

//    private final ObjectMapper mapper = new ObjectMapper();
//    private SSLContext sslContext;
//    private SSLSocketFactory sslFactory;
//    private Scheme scheme;
//    private HttpComponentsClientHttpRequestFactory factory;
//    private String auth, authEncrypt;
//    private byte[] encodedBytes;

//    public ApiOrthancKirimBerkas() {
//        try {
//            auth = koneksiDB.USERORTHANC() + ":" + koneksiDB.PASSORTHANC();
//            encodedBytes = Base64.encodeBase64(auth.getBytes());
//            authEncrypt = new String(encodedBytes);
//        } catch (Exception ex) {
//            System.out.println("Notifikasi : " + ex);
//        }
//    }

//    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
//        sslContext = SSLContext.getInstance("SSL");
//        TrustManager[] trustManagers = {
//            new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() { return null; }
//                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
//                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
//            }
//        };
//        sslContext.init(null, trustManagers, new SecureRandom());
//        sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        scheme = new Scheme("https", 443, sslFactory);
//        factory = new HttpComponentsClientHttpRequestFactory();
//        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
//        return new RestTemplate(factory);
//    }

//    /**
//     * Upload berkas .dcm ke Orthanc (disimpan), suntik AdmissionID=Encounter ID, lalu C-STORE ke DICOM Router.
//     *
//     * @param dicomFilePath path .dcm (kolom lokasi_file_dicom); dibaca lokal dulu, fallback HTTP hybrid web
//     * @param encounterId   Encounter ID -> disuntik sbg AdmissionID (0038,0010); WAJIB agar router menerima
//     * @param targetAETitle AE Title DICOM Router (AETITLEDICOMROUTER)
//     * @param host          host DICOM Router (URLDICOMROUTER) — dipakai saat mendaftarkan modality
//     * @param port          port DICOM Router (PORTDICOMROUTER)
//     * @return true bila upload DAN store ke router berhasil
//     */
//    public boolean uploadAndSendToRouter(String dicomFilePath, String encounterId, String targetAETitle, String host, int port) {
//        String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
//        String studyTransient = "";   // study hasil modify (dikirim & dibersihkan setelahnya)
//        try {
            // 0) Baca byte berkas: coba disk lokal dulu, kalau tak ada ambil via HTTP hybrid web.
//            if (dicomFilePath == null || dicomFilePath.trim().isEmpty()) {
//                System.out.println("Path berkas DICOM kosong, dilewati.");
//                return false;
//            }
//            byte[] dicomBytes = bacaBytesBerkas(dicomFilePath.trim());
//            if (dicomBytes == null || dicomBytes.length == 0) {
//                System.out.println("Berkas DICOM tidak dapat dibaca (lokal maupun HTTP): " + dicomFilePath.trim());
//                return false;
//            }

            // 1) Upload ke Orthanc: POST /instances. Study asli TETAP disimpan di Orthanc.
//            HttpHeaders upHeaders = new HttpHeaders();
//            upHeaders.add("Authorization", "Basic " + authEncrypt);
//            upHeaders.setContentType(MediaType.parseMediaType("application/dicom"));
//            HttpEntity<byte[]> upEntity = new HttpEntity<byte[]>(dicomBytes, upHeaders);
//            ResponseEntity<String> upResp = getRest().exchange(
//                    orthancUrl + "/instances", HttpMethod.POST, upEntity, String.class);
//            if (upResp.getStatusCode() != HttpStatus.OK) {
//                System.out.println("Gagal upload berkas ke Orthanc: HTTP " + upResp.getStatusCode());
//                return false;
//            }
//            JsonNode up = mapper.readTree(upResp.getBody());
//            String studyId = up.path("ParentStudy").asText();
//            if (studyId.isEmpty()) {
//                System.out.println("Upload OK tapi ParentStudy kosong. Respons: " + upResp.getBody());
//                return false;
//            }
//            System.out.println("Berkas terupload ke Orthanc (status=" + up.path("Status").asText()
//                    + "), study=" + studyId);

            // 2) Suntik AdmissionID (0038,0010) = Encounter ID. Router MENOLAK DOC DICOM tanpa tag ini
            //    ("AdmissionID (Encounter ID) is missing"). Modify menghasilkan study baru bertag -> itu yg dikirim.
//            String studyToSend = studyId;
//            if (encounterId != null && !encounterId.trim().isEmpty()) {
//                studyTransient = suntikAdmissionID(orthancUrl, studyId, encounterId.trim());
//                if (!studyTransient.isEmpty()) {
//                    studyToSend = studyTransient;
//                    System.out.println("AdmissionID(Encounter)=" + encounterId.trim() + " disuntikkan -> study " + studyToSend);
//                } else {
//                    System.out.println("PERINGATAN: gagal suntik AdmissionID; kirim study asli (router mungkin menolak).");
//                }
//            } else {
//                System.out.println("PERINGATAN: Encounter ID kosong; DICOM dikirim tanpa AdmissionID (router akan menolak).");
//            }

            // 3) Pastikan modality router terdaftar dengan benar di Orthanc.
//            if (!ensureModalityConfigured(targetAETitle, host, port)) {
//                System.out.println("Failed to configure modality: " + targetAETitle);
//                return false;
//            }

            // 4) C-STORE study (bertag AdmissionID) ke DICOM Router (Orthanc yang bicara DIMSE ke router).
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
//                System.out.println("Successfully sent study " + studyToSend + " to " + targetAETitle);
//                return true;
//            }
//            System.out.println("Failed to send study " + studyToSend + ": HTTP " + response.getStatusCode()
//                    + " - " + response.getBody());
//            return false;

//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            System.out.println("HTTP error upload/kirim berkas: " + e.getStatusCode()
//                    + " - " + e.getResponseBodyAsString());
//            return false;
//        } catch (ResourceAccessException e) {
//            System.out.println("Network error upload/kirim berkas ke Orthanc/" + host + ":" + port
//                    + " - " + e.getMessage());
//            return false;
//        } catch (Exception e) {
//            System.out.println("Unexpected error uploadAndSendToRouter: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        } finally {
            // Hapus study transient hasil modify supaya Orthanc tak menumpuk salinan tiap Kirim.
//            if (!studyTransient.isEmpty()) {
//                hapusStudyTransient(orthancUrl, studyTransient);
//            }
//        }
//    }

//    /** Salin study dgn AdmissionID(0038,0010)=encounterId via Orthanc modify (KeepSource). Return id study baru, "" bila gagal. */
//    private String suntikAdmissionID(String orthancUrl, String studyId, String encounterId) {
//        try {
//            String body = "{\"Replace\":{\"AdmissionID\":\"" + encounterId + "\"},\"Force\":true}";
//            HttpHeaders h = new HttpHeaders();
//            h.add("Authorization", "Basic " + authEncrypt);
//            h.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> e = new HttpEntity<String>(body, h);
//            ResponseEntity<String> resp = getRest().exchange(
//                    orthancUrl + "/studies/" + studyId + "/modify", HttpMethod.POST, e, String.class);
//            return mapper.readTree(resp.getBody()).path("ID").asText();
//        } catch (Exception ex) {
//            System.out.println("Gagal suntik AdmissionID: " + ex.getMessage());
//            return "";
//        }
//    }

//    /**
//     * Daftarkan/perbaiki modality router di Orthanc. Host DIMSE WAJIB IP polos (strip http:// —
//     * kalau ada skema Orthanc gagal "unknown host"). AET = targetAETitle. SELALU PUT (idempotent)
//     * agar config lama yang salah ikut diperbaiki.
//     */
//    private boolean ensureModalityConfigured(String targetAETitle, String host, int port) {
//        try {
//            String orthancUrl = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC();
//            String modalityUrl = orthancUrl + "/modalities/" + targetAETitle;
//            String dimseHost = (host == null ? "" : host).replaceFirst("^(?i)https?://", "").trim();
//            String modalityConfig = String.format("[\"%s\", \"%s\", %d]", targetAETitle, dimseHost, port);
//            HttpHeaders configHeaders = new HttpHeaders();
//            configHeaders.add("Authorization", "Basic " + authEncrypt);
//            configHeaders.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> configEntity = new HttpEntity<String>(modalityConfig, configHeaders);
//            getRest().exchange(modalityUrl, HttpMethod.PUT, configEntity, String.class);
//            System.out.println("Modality " + targetAETitle + " diset -> AET=" + targetAETitle
//                    + " host=" + dimseHost + " port=" + port);
//            return true;
//        } catch (Exception e) {
//            System.out.println("Error configuring modality " + targetAETitle + ": " + e.getMessage());
//            return false;
//        }
//    }

//    /** Hapus study transient (best-effort) dari Orthanc. */
//    private void hapusStudyTransient(String orthancUrl, String studyId) {
//        try {
//            HttpHeaders h = new HttpHeaders();
//            h.add("Authorization", "Basic " + authEncrypt);
//            getRest().exchange(orthancUrl + "/studies/" + studyId, HttpMethod.DELETE,
//                    new HttpEntity<Void>(h), String.class);
//        } catch (Exception ex) {
//            System.out.println("Gagal hapus study transient " + studyId + ": " + ex.getMessage());
//        }
//    }

//    /**
//     * Baca byte berkas DICOM. Coba disk lokal dulu (path relatif thd working dir SIMRS);
//     * bila tak ada, ambil via HTTP dari hybrid web (pola sama berkas rawat lain:
//     * http://{HOSTHYBRIDWEB}:{PORTWEB}/{HYBRIDWEB}/berkasrawat/{pathRelatif}). null bila dua-duanya gagal.
//     */
//    private byte[] bacaBytesBerkas(String pathRelatif) {
        // 1) Disk lokal.
//        try {
//            File f = new File(pathRelatif);
//            if (f.isFile()) {
//                byte[] b = Files.readAllBytes(f.toPath());
//                System.out.println("Berkas DICOM dibaca dari disk lokal: " + pathRelatif);
//                return b;
//            }
//        } catch (Exception e) {
//            System.out.println("Gagal baca berkas lokal (" + pathRelatif + "): " + e.getMessage());
//        }
        // 2) HTTP hybrid web.
//        java.io.InputStream is = null;
//        try {
//            String urlStr = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB()
//                    + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + pathRelatif;
//            System.out.println("Berkas DICOM diambil via HTTP: " + urlStr);
//            java.net.URL url = new java.net.URL(urlStr);
//            is = url.openStream();
//            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
//            byte[] buf = new byte[8192];
//            int n;
//            while ((n = is.read(buf)) != -1) {
//                bos.write(buf, 0, n);
//            }
//            return bos.toByteArray();
//        } catch (Exception e) {
//            System.out.println("Gagal ambil berkas via HTTP (" + pathRelatif + "): " + e.getMessage());
//            return null;
//        } finally {
//            if (is != null) {
//                try { is.close(); } catch (Exception ign) {}
//            }
//        }
//    }
//}
