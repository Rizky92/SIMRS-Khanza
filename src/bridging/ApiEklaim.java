package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ApiEklaim {
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int IV_SIZE = 16;
    private static final int SIGNATURE_SIZE = 10;

    private String url;
    private String key;

    private String noSEP;

    public ApiEklaim() {
        try {
            url = koneksiDB.URLWEBSERVICEEKLAIM();
            key = koneksiDB.KEYWEBSERVICEEKLAIM();
        } catch (Exception e) {
            url = "";
            key = "";
        }
    }

    private byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("[\\s-]", "");

        if (s.length() % 2 != 0) {
            s = "0" + s;
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    private String encrypt(String data) throws Exception {
        byte[] bytes = hexStringToByteArray(key);

        if (bytes.length != 32) {
            throw new Exception("Needs a 256-bit key!");
        }

        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(bytes, 0, bytes.length, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec hmacKeySpec = new SecretKeySpec(bytes, HMAC_ALGORITHM);
        hmac.init(hmacKeySpec);
        byte[] fullHmac = hmac.doFinal(encrypted);
        byte[] signature = new byte[SIGNATURE_SIZE];
        System.arraycopy(fullHmac, 0, signature, 0, SIGNATURE_SIZE);

        byte[] combined = new byte[signature.length + iv.length + encrypted.length];
        System.arraycopy(signature, 0, combined, 0, signature.length);
        System.arraycopy(iv, 0, combined, signature.length, iv.length);
        System.arraycopy(encrypted, 0, combined, signature.length + iv.length, encrypted.length);

        String encoded = Base64.getEncoder().encodeToString(combined);
        return encoded.replaceAll("(.{64})", "$1\n");
    }

    private String decrypt(String data) throws Exception {
        byte[] bytes = hexStringToByteArray(key);

        if (bytes.length != 32) {
            throw new Exception("Needs a 256-bit key!");
        }

        String cleaned = data.replaceAll("\n", "").replaceAll("\r", "");
        byte[] decoded = Base64.getDecoder().decode(cleaned);

        byte[] signature = new byte[SIGNATURE_SIZE];
        System.arraycopy(decoded, 0, signature, 0, SIGNATURE_SIZE);

        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(decoded, SIGNATURE_SIZE, iv, 0, IV_SIZE);

        byte[] encrypted = new byte[decoded.length - SIGNATURE_SIZE - IV_SIZE];
        System.arraycopy(decoded, SIGNATURE_SIZE + IV_SIZE, encrypted, 0, encrypted.length);

        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec hmacKeySpec = new SecretKeySpec(bytes, HMAC_ALGORITHM);
        hmac.init(hmacKeySpec);
        byte[] fullHmac = hmac.doFinal(encrypted);
        byte[] calcSignature = new byte[SIGNATURE_SIZE];
        System.arraycopy(fullHmac, 0, calcSignature, 0, SIGNATURE_SIZE);

        if (!compare(signature, calcSignature)) {
            throw new Exception ("Signature not match!");
        }

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(bytes, 0, bytes.length, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private boolean compare(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }

        return result == 0;
    }

    private JsonNode request(JsonNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String encrypted = encrypt(mapper.writeValueAsString(payload));

        URL url = new URL(this.url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        try (var os = conn.getOutputStream()) {
            byte[] input = encrypted.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        String cleanResponse = response.toString()
            .replaceAll("----BEGIN ENCRYPTED DATA----", "")
            .replaceAll("----END ENCRYPTED DATA----", "")
            .trim();

        String decrypted = decrypt(cleanResponse);

        return mapper.readTree(decrypted);
    }

    private JsonNode get(JsonNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String encrypted = encrypt(mapper.writeValueAsString(payload));

        URL url = new URL(this.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (var os = conn.getOutputStream()) {
            byte[] input = encrypted.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        String cleanResponse = response.toString()
            .replaceAll("----BEGIN ENCRYPTED DATA----", "")
            .replaceAll("----END ENCRYPTED DATA----", "")
            .trim();

        String decrypted = decrypt(cleanResponse);

        return mapper.readTree(decrypted);
    }

    public void setSEP(String noSEP) {
        this.noSEP = noSEP;
    }

    public void klaimBaru() {
        //
    }

    public void updateDataPasien(String noRM) {

    }

    public void setDataKlaim(Map params) {

    }

    public void validasiSITB(String noSITB) {

    }

    public void setDiagnosaIDRG(Set diagnosa) {

    }

    public void setProsedurIDRG(Map prosedur) {

    }

    public void groupingIDRG() {

    }

    public void finalIDRG() {

    }

    public void importIDRGtoINACBG() {

    }

    public void setDiagnosaINACBG(Set diagnosa) {

    }

    public void setProsedurINACBG(Set prosedur) {

    }

    public void groupingINACBG() {

    }

    public void groupingStage2INACBG(Map params) {

    }

    public void finalINACBG() {

    }

    public void finalKlaim() {

    }

    public File printKlaim() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();

        body.withObject("metadata").put("method", "claim_print");
        body.withObject("data").put("nomor_sep", noSEP);

        JsonNode response = request(body);
        System.out.println("Response: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));

        String base64PDF = response.path("data").asText();

        byte[] pdfBytes = Base64.getDecoder().decode(base64PDF);

        String basedir = System.getProperty("user.dir");
        Path path = Paths.get(basedir + "/klaim.pdf");
        Files.write(path, pdfBytes);
        System.out.println("Output: " + basedir + "/klaim.pdf");

        return path.toFile();
    }

    public void sendToDC() {

    }

    public void getDataKlaim() {

    }

    public void reeditKlaim() {

    }

    public void reeditIDRG() {

    }

    public void reeditINACBG() {

    }

    public static void main(String[] args) {
        try {
            ApiEklaim api = new ApiEklaim();
            api.setSEP("0302R1100126V007197");
            File pdf = api.printKlaim();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
