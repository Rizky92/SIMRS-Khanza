package support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EKlaimApiClient {

    private final String wsUrl;
    private final EKlaimCrypto crypto;
    private final ObjectMapper mapper = new ObjectMapper();

    public EKlaimApiClient(String wsUrl, String hexKey) {
        this.wsUrl = wsUrl;
        this.crypto = new EKlaimCrypto(hexKey);
    }

    public JsonNode request(String json) throws Exception {
        System.out.println("[EKlaim API] POST request: " + json);
        String encrypted = crypto.encrypt(json);

        HttpURLConnection conn = (HttpURLConnection) new URL(wsUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(encrypted.getBytes(StandardCharsets.UTF_8));
        }

        String response = readResponse(conn);
        System.out.println("[EKlaim API] POST raw response length: " + response.length());
        String decrypted = decryptResponse(response);
        System.out.println("[EKlaim API] POST decrypted: " + decrypted);

        return mapper.readTree(decrypted);
    }

    public JsonNode get(String json) throws Exception {
        System.out.println("[EKlaim API] GET request: " + json);
        String encrypted = crypto.encrypt(json);

        HttpURLConnection conn = (HttpURLConnection) new URL(wsUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(encrypted.getBytes(StandardCharsets.UTF_8));
        }

        String response = readResponse(conn);
        System.out.println("[EKlaim API] GET raw response length: " + response.length());
        String decrypted = decryptResponse(response);
        System.out.println("[EKlaim API] GET decrypted: " + decrypted);

        return mapper.readTree(decrypted);
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private String decryptResponse(String response) {
        String payload = stripEncryptionMarkers(response);
        System.out.println("[EKlaim API] payload after stripping markers, length: " + payload.length());
        return crypto.decrypt(payload);
    }

    private String stripEncryptionMarkers(String response) {
        // Response is wrapped with ----BEGIN ENCRYPTED DATA---- and ----END ENCRYPTED DATA---- markers
        String result = response;
        int beginIdx = result.indexOf("----BEGIN ENCRYPTED DATA----");
        if (beginIdx >= 0) {
            result = result.substring(beginIdx + "----BEGIN ENCRYPTED DATA----".length());
        }
        int endIdx = result.indexOf("----END ENCRYPTED DATA----");
        if (endIdx >= 0) {
            result = result.substring(0, endIdx);
        }
        return result.trim();
    }
}
