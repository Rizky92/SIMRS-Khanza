package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class ApiSatuSehat {
    public static final int TOO_MANY_REQUESTS_SMC = 429;

    private static volatile boolean dihentikanSmc = false;

    private String key,clientid,urlauth,token;
    private long millis;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private ApiBPJSAesKeySpec mykey;
    private HttpHeaders header ;
    private JsonNode root;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();

    public ApiSatuSehat(){
        try {
            key = koneksiDB.SECRETKEYSATUSEHAT();
            clientid = koneksiDB.CLIENTIDSATUSEHAT();
            urlauth = koneksiDB.URLAUTHSATUSEHAT();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
    }

    public String TokenSatuSehat(){
        if(dihentikanSmc){
            return token;
        }
        try {
            header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            requestEntity = new HttpEntity("client_id="+clientid+"&client_secret="+key,header);
            root = mapper.readTree(getRest().exchange(urlauth+"/accesstoken?grant_type=client_credentials", HttpMethod.POST, requestEntity, String.class).getBody());
            token=root.path("access_token").asText();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
        return token;
    }

    public long GetUTCdatetimeAsString(){
        millis = System.currentTimeMillis();
        return millis/1000;
    }

    public String Decrypt(String data,String utc)throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        System.out.println(data);
        mykey = ApiBPJSEnc.generateKey(clientid+key+utc);
        data=ApiBPJSEnc.decrypt(data, mykey.getKey(), mykey.getIv());
        data=ApiBPJSLZString.decompressFromEncodedURIComponent(data);
        System.out.println(data);
        return data;
    }

    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("TLSv1.2");
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
        RestTemplate rest = new RestTemplate(factory);
        rest.setErrorHandler(new PenanganKesalahanSMC());
        return rest;
    }

    public static boolean isDihentikanSmc() {
        return dihentikanSmc;
    }

    public static void resetDihentikanSmc() {
        dihentikanSmc = false;
    }

    public static boolean isTooManyRequestsSmc(Throwable e) {
        for (Throwable telusur = e; null != telusur; telusur = telusur.getCause()) {
            if (telusur instanceof HttpStatusCodeException) {
                if (TOO_MANY_REQUESTS_SMC == ((HttpStatusCodeException) telusur).getStatusCode().value()) {
                    return true;
                }
            } else if ((telusur instanceof IllegalArgumentException) && (null != telusur.getMessage()) && (telusur.getMessage().contains("[" + TOO_MANY_REQUESTS_SMC + "]"))) {
                return true;
            }
            if (telusur == telusur.getCause()) {
                break;
            }
        }
        return false;
    }

    public static void tandaiJikaTooManyRequestsSmc(Throwable e) {
        if (isTooManyRequestsSmc(e)) {
            hentikanSmc();
        }
    }

    private static void hentikanSmc() {
        dihentikanSmc = true;
        System.out.println("Notifikasi : Permintaan ke Satu Sehat dibatasi (HTTP " + TOO_MANY_REQUESTS_SMC + "), proses pengiriman dihentikan...");
    }

    private static class PenanganKesalahanSMC extends DefaultResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            try {
                return super.hasError(response);
            } catch (IllegalArgumentException e) {
                tandaiJikaTooManyRequestsSmc(e);
                throw e;
            }
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            try {
                if (TOO_MANY_REQUESTS_SMC == response.getStatusCode().value()) {
                    hentikanSmc();
                }
            } catch (IllegalArgumentException e) {
                tandaiJikaTooManyRequestsSmc(e);
                throw e;
            }
            super.handleError(response);
        }
    }
}
