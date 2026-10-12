package smc.updater;

import fungsi.koneksiDB;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

final class ClientUpdaterSMC {
    private final HttpClient http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();

    HttpResponse<String> fetchManifest(String channel, String etag) throws IOException, InterruptedException {
        HttpRequest.Builder request = createHTTPRequest(channel, "manifest", Duration.ofSeconds(5));

        if (etag != null && !etag.isEmpty()) {
            request.header("If-None-Match", etag);
        }

        return http.send(request.GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    void download(String channel, String path, Path tujuan) throws IOException, InterruptedException {
        HttpRequest request = createHTTPRequest(channel, "berkas", Duration.ofMinutes(10)).header("X-Berkas", path).GET().build();

        HttpResponse<Path> response = http.send(request, HttpResponse.BodyHandlers.ofFile(tujuan));

        if (200 != response.statusCode()) {
            throw new IOException("HTTP " + response.statusCode() + " saat mengunduh " + path);
        }
    }

    private HttpRequest.Builder createHTTPRequest(String channel, String action, Duration timeout) {
        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/pembaruansmc/index.php?kanal=" + channel + "&aksi=" + action;

        return HttpRequest.newBuilder(URI.create(url))
            .timeout(timeout)
            .header("X-Usere", koneksiDB.USERHYBRIDWEB())
            .header("X-Passwordte", koneksiDB.PASHYBRIDWEB());
    }
}
