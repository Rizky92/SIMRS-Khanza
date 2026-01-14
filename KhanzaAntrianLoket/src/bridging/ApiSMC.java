package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.util.ArrayList;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class ApiSMC
{
    private final String APIURL = koneksiDB.SMCAPIURL();
    private final String APIKEY = koneksiDB.SMCAPIKEY();
    private final Connection koneksi = koneksiDB.condb();
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate http = new RestTemplate();
    private HttpHeaders headers;
    private HttpEntity entity;
    private ObjectNode root;
    private JsonNode response;
    private String url;

    public void panggilAntrean(String action, String nomor, String loket) {
        try {
            root = mapper.createObjectNode();
            root.put("action", action);
            root.put("nomor", nomor);
            root.put("loket", loket);

            url = APIURL + "/panggil-antrean-loket-smc";
            System.out.println("URL : " + url);
            System.out.println("JSON : " + root.toString());
            System.out.println("Mengirim panggilan antrean ke SMC Internal App...");
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-api-key", APIKEY);
            entity = new HttpEntity(mapper.writeValueAsString(root), headers);
            ResponseEntity<String> responseEntity = http.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Response : " + responseEntity.getBody());
            System.out.println("Response : " + responseEntity.getStatusCode());
            response = mapper.readTree(responseEntity.getBody());
            System.out.println(responseEntity.getStatusCode() + " : " + response.path("message").asText());
            if (response.path("status").asText().equals("200")) {
                response.path("payload").asText();
                System.out.println("Berhasil mengirim panggilan antrean ke SMC Internal App.");
            } else {
                ArrayList<String> messages = new ArrayList<>();

                if (response.path("message").isArray()) {
                    for (JsonNode msgNode : response.path("message")) {
                        messages.add("- " + msgNode.path("msg").asText());
                    }
                } else {
                    messages.add(response.path("message").asText());
                }

                if (messages.isEmpty()) {
                    System.out.println("Gagal mengirim panggilan antrean ke SMC Internal App.");
                } else {
                    System.out.println("Gagal mengirim panggilan antrean ke SMC Internal App. Pesan : " + String.join(", ", messages));
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            System.out.println("Terjadi kesalahan saat mengirim panggilan antrean ke SMC Internal App..!!");
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.getMessage().contains("HostException")) {
                System.out.println("Sambungan ke server SMC Internal App terputus..!!");
            }
        }
    }

    public void stopAntrean(String action) {
        try {
            root = mapper.createObjectNode();
            root.put("action", action);

            url = APIURL + "/stop-antrean-loket-smc";
            System.out.println("URL : " + url);
            System.out.println("JSON : " + root.toString());
            System.out.println("Mengirim stop panggilan antrean ke SMC Internal App...");
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-api-key", APIKEY);
            entity = new HttpEntity(mapper.writeValueAsString(root), headers);
            ResponseEntity<String> responseEntity = http.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Response : " + responseEntity.getBody());
            System.out.println("Response : " + responseEntity.getStatusCode());
            response = mapper.readTree(responseEntity.getBody());
            System.out.println(responseEntity.getStatusCode() + " : " + response.path("message").asText());
            if (response.path("status").asText().equals("200")) {
                response.path("payload").asText();
                System.out.println("Berhasil mengirim stop panggilan antrean ke SMC Internal App.");
            } else {
                ArrayList<String> messages = new ArrayList<>();

                if (response.path("message").isArray()) {
                    for (JsonNode msgNode : response.path("message")) {
                        messages.add("- " + msgNode.path("msg").asText());
                    }
                } else {
                    messages.add(response.path("message").asText());
                }

                if (messages.isEmpty()) {
                    System.out.println("Gagal mengirim stop panggilan antrean ke SMC Internal App.");
                } else {
                    System.out.println("Gagal mengirim stop panggilan antrean ke SMC Internal App. Pesan : " + String.join(", ", messages));
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            System.out.println("Terjadi kesalahan saat mengirim stop panggilan antrean ke SMC Internal App..!!");
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.getMessage().contains("HostException")) {
                System.out.println("Sambungan ke server SMC Internal App terputus..!!");
            }
        }
    }
}