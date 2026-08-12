package bridging.satusehat;

import fungsi.koneksiDB;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ApiINACBG {

    private static final String KEY_RS = koneksiDB.KEYEKLAIM();
    private static final String URL_WS = koneksiDB.WSEKLAIM();
    private static final String KELAS_RS = koneksiDB.KELASRSEKLAIM();

    private RestTemplate restTemplate;

    public static String getKey() {
        return KEY_RS;
    }

    public static String getUrlWS() {
        return URL_WS;
    }

    public static String getKelasRS() {
        return KELAS_RS;
    }

    public static String mcEncrypt(String data, String strKey) throws Exception {
        byte[] key = hexStringToByteArray(strKey);
        if (key.length != 32) {
            throw new IllegalArgumentException("Needs a 256-bit key!");
        }

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);  // Use SecureRandom for cryptographic randomness
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);

        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] signature = Arrays.copyOf(hmacSha256(encrypted, key), 10);

        byte[] combined = new byte[signature.length + iv.length + encrypted.length];
        System.arraycopy(signature, 0, combined, 0, signature.length);
        System.arraycopy(iv, 0, combined, signature.length, iv.length);
        System.arraycopy(encrypted, 0, combined, signature.length + iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String mcDecrypt(String str, String strKey) throws Exception {
        if (!isBase64(str)) {
            throw new IllegalArgumentException("Invalid Base64 input");
        }

        // Convert the hex string key to byte array
        byte[] key = hexStringToByteArray(strKey);
        if (key.length != 32) {
            throw new IllegalArgumentException("Needs a 256-bit key!");
        }

        // Decode the Base64 encoded input
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(str);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to decode Base64 string", e);
        }

        // Extract signature (first 10 bytes), IV (next 16 bytes), and encrypted data
        byte[] signature = Arrays.copyOfRange(decoded, 0, 10); // First 10 bytes
        byte[] iv = Arrays.copyOfRange(decoded, 10, 26);       // Next 16 bytes (IV size is 16 for AES-256-CBC)
        byte[] encrypted = Arrays.copyOfRange(decoded, 26, decoded.length); // The remaining bytes are the encrypted data

        // Calculate the HMAC-SHA256 signature on the encrypted data
        byte[] calcSignature = Arrays.copyOf(hmacSha256(encrypted, key), 10);

        // Compare the signatures
        if (!mcCompare(signature, calcSignature)) {
            return "SIGNATURE_NOT_MATCH";
        }

        // Decrypt the encrypted data using AES-256-CBC
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Perform the decryption
        byte[] decrypted;
        try {
            decrypted = cipher.doFinal(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new Exception("Decryption error", e);
        }

        // Convert the decrypted byte array to a string (UTF-8)
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static byte[] hmacSha256(byte[] data, byte[] key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(data);
    }

    private static boolean mcCompare(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i]; // Constant-time comparison
        }
        return result == 0;
    }

    public JSONObject request(String request) throws Exception {
        

        // Encrypt the request
        String encryptedRequest = mcEncrypt(request, getKey());
//        System.out.println("Encrypted Request (Base64): " + encryptedRequest);

        // Check if the encrypted request is valid Base64
        if (!isBase64(encryptedRequest)) {
            throw new IllegalArgumentException("Encrypted request is not valid Base64");
        } else {
            System.out.println("Valid base64");
        }

        // Set headers for the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the request entity with the encrypted request
        HttpEntity<String> requestEntity = new HttpEntity<>(encryptedRequest, headers);

        // Use the custom RestTemplate configured with SSL
        RestTemplate restTemplate = getRest();

        // Execute the request
        String responseBody = restTemplate.exchange(getUrlWS(), HttpMethod.POST, requestEntity, String.class).getBody();

        // Handle response
        if (responseBody == null || responseBody.isEmpty()) {
            throw new Exception("Empty response from server");
        }

        // Clean the response
        String trimmedResponse = cleanResponse(responseBody);
//        System.out.println("Trimmed Response: " + trimmedResponse);

        // Check if the trimmed response is valid Base64 before decrypting
        if (!isBase64(trimmedResponse)) {
            throw new IllegalArgumentException("Response is not valid Base64");
        } else {
            System.out.println("Response is Valid base64");
        }

        // Decrypt the response
        String decryptedResponse = mcDecrypt(trimmedResponse, getKey());

        // Convert decrypted response to JSONObject and return
        return new JSONObject(decryptedResponse);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext;
        SSLSocketFactory sslFactory;
        Scheme scheme;
        HttpComponentsClientHttpRequestFactory factory;
        sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme = new Scheme("https", 443, sslFactory);
        factory = new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }

    public static String cleanResponse(String response) throws Exception {
        // Remove the markers "----BEGIN ENCRYPTED DATA----" and "----END ENCRYPTED DATA----"
        int firstNewlineIndex = response.indexOf('\n') + 1;
        int lastNewlineIndex = response.lastIndexOf('\n');

        if (firstNewlineIndex <= 0 || lastNewlineIndex <= 0 || firstNewlineIndex >= lastNewlineIndex) {
            throw new Exception("Malformed response from server");
        }

        String cleanedResponse = response.substring(firstNewlineIndex, lastNewlineIndex).trim();

        // Ensure it is only Base64 characters and pad if necessary
        cleanedResponse = cleanedResponse
                .replace("----BEGIN ENCRYPTED DATA----", "")
                .replace("----END ENCRYPTED DATA----", "")
                .trim();

        // Remove any non-Base64 characters (Base64 characters are A-Z, a-z, 0-9, +, /)
        cleanedResponse = cleanedResponse.replaceAll("[^A-Za-z0-9+/=]", "");

        // Add padding if needed
        int mod4 = cleanedResponse.length() % 4;
        if (mod4 > 0) {
            cleanedResponse += "====".substring(mod4);
        }

        return cleanedResponse;
    }

    public static boolean isBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // Check if the string length is a multiple of 4
        if (str.length() % 4 != 0) {
            return false;
        }

        // Remove padding characters to validate Base64 content
        String cleanedStr = str.replaceAll("[^A-Za-z0-9+/=]", "");

        try {
            Base64.getDecoder().decode(cleanedStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
