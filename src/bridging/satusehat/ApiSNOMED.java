package bridging.satusehat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.awt.Component;
import java.awt.Cursor;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * SNOMED CT FHIR Terminology Service Handles ValueSet expansion and CodeSystem
 * lookups via FHIR API
 *
 * @author IT Manager - Hospital Information System
 * @version 1.0
 */
public class ApiSNOMED {

    private final ObjectMapper mapper;
    private final String baseUrl;
    private final HttpHeaders defaultHeaders;
    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String FHIR_VS = "http://snomed.info/sct?fhir_vs";
    private Component parentComponent; // Reference to UI component for cursor changes

    /**
     * Constructor - initializes SNOMED FHIR service with base URL from config
     */
    public ApiSNOMED() {
        this.mapper = new ObjectMapper();
        // Retrieve base URL from your configuration
        // Example: this.baseUrl = koneksiDB.SNOMED_BASE_URL();
        this.baseUrl = koneksiDB.URLSNOMEDAPI(); // Replace with config method

        this.defaultHeaders = new HttpHeaders();
        this.defaultHeaders.add("Accept", "application/fhir+json");
        this.defaultHeaders.add("Content-Type", "application/fhir+json");
    }

    /**
     * Constructor with parent component for cursor management
     */
    public ApiSNOMED(Component parentComponent) {
        this();
        this.parentComponent = parentComponent;
    }

    /**
     * Set parent component for cursor management
     */
    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    /**
     * Search SNOMED CT concepts by keyword with pagination
     *
     * @param keyword Search term (e.g., "diabetes", "allergy")
     * @param limit Maximum number of results (default: 10)
     * @param offset Pagination offset (default: 0)
     * @return SnomedSearchResult containing code, system, and display for each
     * match
     */
    public SnomedSearchResult searchSnomedConcepts(String keyword, Integer limit, Integer offset) {
        // Validate inputs
        if (keyword == null || keyword.trim().isEmpty()) {
            return createErrorResult("Keyword cannot be empty");
        }

        int count = (limit != null && limit > 0) ? limit : 10;
        int off = (offset != null && offset >= 0) ? offset : 0;

        try {
            // Set wait cursor if parent component is available
            if (parentComponent != null) {
                parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            // Build URL with query parameters
            String url = baseUrl + "/fhir/ValueSet/$expand?url=" + FHIR_VS
                    + "&filter=" + URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8)
                    + "&count=" + count
                    + "&offset=" + off;

            System.out.println("SNOMED Search URL: " + url);

            // Execute request
            HttpEntity<String> requestEntity = new HttpEntity<>(defaultHeaders);
            ResponseEntity<String> response = getRest().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // Parse response
            if (response.getStatusCode().value() >= 200 && response.getStatusCode().value() < 300
                    && response.getBody() != null) {
                return parseValueSetExpansion(response.getBody(), keyword, count, off);
            } else {
                return createErrorResult("Server returned status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error searching SNOMED concepts: " + e.getMessage());
            e.printStackTrace();
            return createErrorResult("Search failed: " + e.getMessage());
        } finally {
            // Always restore default cursor
            if (parentComponent != null) {
                parentComponent.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    /**
     * Lookup specific SNOMED CT code details
     *
     * @param snomedCode SNOMED CT concept code (e.g., "73211009")
     * @return SnomedSearchResult with single concept details
     */
    public SnomedSearchResult lookupSnomedCode(String snomedCode) {
        if (snomedCode == null || snomedCode.trim().isEmpty()) {
            return createErrorResult("SNOMED code cannot be empty");
        }

        try {
            // Set wait cursor if parent component is available
            if (parentComponent != null) {
                parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            String url = baseUrl + "/fhir/CodeSystem/$lookup?system=" + SNOMED_SYSTEM
                    + "&code=" + URLEncoder.encode(snomedCode.trim(), StandardCharsets.UTF_8);

            System.out.println("SNOMED Lookup URL: " + url);

            HttpEntity<String> requestEntity = new HttpEntity<>(defaultHeaders);
            ResponseEntity<String> response = getRest().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().value() >= 200 && response.getStatusCode().value() < 300
                    && response.getBody() != null) {
                return parseCodeSystemLookup(response.getBody(), snomedCode);
            } else {
                return createErrorResult("Lookup failed: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error looking up SNOMED code: " + e.getMessage());
            e.printStackTrace();
            return createErrorResult("Lookup failed: " + e.getMessage());
        } finally {
            // Always restore default cursor
            if (parentComponent != null) {
                parentComponent.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    /**
     * Parse FHIR ValueSet expansion response
     */
    private SnomedSearchResult parseValueSetExpansion(String jsonResponse, String keyword, int count, int offset) {
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            SnomedSearchResult result = new SnomedSearchResult();
            result.setSuccess(true);
            result.setKeyword(keyword);
            result.setLimit(count);
            result.setOffset(offset);

            // Check if expansion exists
            if (!root.has("expansion") || !root.get("expansion").has("contains")) {
                result.setTotalResults(0);
                return result;
            }

            JsonNode contains = root.get("expansion").get("contains");
            result.setTotalResults(contains.size());

            ArrayNode concepts = mapper.createArrayNode();

            for (JsonNode concept : contains) {
                ObjectNode conceptNode = mapper.createObjectNode();

                // Extract code, system, and display
                conceptNode.put("code", concept.has("code") ? concept.get("code").asText() : "");
                conceptNode.put("system", concept.has("system") ? concept.get("system").asText() : SNOMED_SYSTEM);
                conceptNode.put("display", concept.has("display") ? concept.get("display").asText() : "");

                concepts.add(conceptNode);
            }

            result.setConcepts(concepts);
            return result;

        } catch (Exception e) {
            System.err.println("Error parsing ValueSet response: " + e.getMessage());
            return createErrorResult("Failed to parse response");
        }
    }

    /**
     * Parse FHIR CodeSystem lookup response
     */
    private SnomedSearchResult parseCodeSystemLookup(String jsonResponse, String code) {
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            SnomedSearchResult result = new SnomedSearchResult();
            result.setSuccess(true);
            result.setTotalResults(1);

            ArrayNode concepts = mapper.createArrayNode();
            ObjectNode conceptNode = mapper.createObjectNode();

            // Extract parameters from lookup result
            conceptNode.put("code", code);
            conceptNode.put("system", SNOMED_SYSTEM);

            // Find display name in parameters
            if (root.has("parameter")) {
                for (JsonNode param : root.get("parameter")) {
                    if (param.has("name") && "display".equals(param.get("name").asText())) {
                        conceptNode.put("display", param.get("valueString").asText());
                        break;
                    }
                }
            }

            concepts.add(conceptNode);
            result.setConcepts(concepts);
            return result;

        } catch (Exception e) {
            System.err.println("Error parsing CodeSystem lookup: " + e.getMessage());
            return createErrorResult("Failed to parse lookup response");
        }
    }

    /**
     * Create error result object
     */
    private SnomedSearchResult createErrorResult(String errorMessage) {
        SnomedSearchResult result = new SnomedSearchResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setTotalResults(0);
        result.setConcepts(mapper.createArrayNode());
        return result;
    }

    /**
     * Get configured RestTemplate with SSL bypass (for development) PRODUCTION:
     * Replace with proper SSL certificate validation
     */
    private RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                }
            }
        };

        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);

        return new RestTemplate(factory);
    }

    /**
     * Result wrapper class for SNOMED searches
     */
    public static class SnomedSearchResult {

        private boolean success;
        private String errorMessage;
        private String keyword;
        private int totalResults;
        private int limit;
        private int offset;
        private ArrayNode concepts;

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public ArrayNode getConcepts() {
            return concepts;
        }

        public void setConcepts(ArrayNode concepts) {
            this.concepts = concepts;
        }

        /**
         * Get concepts as formatted string for display
         */
        public String getConceptsAsString() {
            if (concepts == null || concepts.size() == 0) {
                return "No results found";
            }

            StringBuilder sb = new StringBuilder();
            for (JsonNode concept : concepts) {
                sb.append(String.format("Code: %s | System: %s | Display: %s%n",
                        concept.get("code").asText(),
                        concept.get("system").asText(),
                        concept.get("display").asText()));
            }
            return sb.toString();
        }
    }
}
