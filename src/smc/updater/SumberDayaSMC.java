package smc.updater;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

final class SumberDayaSMC {
    private static final long DELETION_TIMEOUT = 10 * 60 * 1000;
    private static final String[] EXCLUDE_DIR = {"berkaspdf/", "cache/", "lib/", "pembaruan/"};
    private static final String[] EXCLUDE_EXT = {".bat", ".cmd", ".com", ".dll", ".exe", ".jar", ".msi", ".ps1", ".scr", ".vbs"};

    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final ClientUpdaterSMC client;
    private final Path installDir;
    private final Path stagingDir;
    private final Path fileStatus;

    SumberDayaSMC(ClientUpdaterSMC client, Path installDir) {
        this.client = client;
        this.installDir = installDir;
        this.stagingDir = installDir.resolve("pembaruan").resolve("staging");
        this.fileStatus = installDir.resolve("pembaruan").resolve("sumberdaya.json");
    }

    synchronized void sync(boolean isMajorUpdate) throws IOException, InterruptedException {
        Status status = readStatus();
        HttpResponse<String> response = client.fetchManifest("sumberdaya", isMajorUpdate ? null : status.etag);

        if (304 == response.statusCode()) {
            return;
        }

        if (200 != response.statusCode()) {
            throw new IOException("HTTP " + response.statusCode() + " saat mengambil manifest " + "sumberdaya");
        }

        Manifest manifest = mapper.readValue(response.body(), Manifest.class);
        cleanStaging();

        boolean needsRestart = false;
        int updated = 0;
        int deleted = 0;
        Set<String> serverPath = new HashSet<>();

        for (ManifestEntry entry : manifest.files) {
            Path localDir = getLocalDir(entry.path);

            if (null == localDir || null == entry.sha256 || !entry.sha256.matches("[0-9a-fA-F]{64}")) {
                System.out.println("Pembaruan : berkas " + entry.path + " ditolak");

                continue;
            }

            serverPath.add(entry.path);

            try {
                String sha256Lokal = sha256Lokal(localDir, status.files.get(entry.path));

                if (entry.sha256.equalsIgnoreCase(sha256Lokal)) {
                    status.files.put(entry.path, catat(localDir, sha256Lokal));

                    continue;
                }

                install(entry, localDir);

                status.files.put(entry.path, catat(localDir, entry.sha256.toLowerCase(Locale.ROOT)));
                updated++;
            } catch (IOException e) {
                needsRestart = true;
                System.out.println("Pembaruan : gagal memperbarui " + entry.path + " : " + e);
            }
        }

        if (!serverPath.isEmpty()) {
            long sekarang = System.currentTimeMillis();
            Iterator<Map.Entry<String, FileMetadata>> iterator = status.files.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, FileMetadata> catatan = iterator.next();

                if (serverPath.contains(catatan.getKey())) {
                    continue;
                }

                Path lokal = getLocalDir(catatan.getKey());

                if (lokal == null || !Files.isRegularFile(lokal)) {
                    iterator.remove();

                    continue;
                }

                if (0 == catatan.getValue().hilangSejak) {
                    catatan.getValue().hilangSejak = sekarang;
                }

                if (sekarang - catatan.getValue().hilangSejak < DELETION_TIMEOUT) {
                    needsRestart = true;

                    continue;
                }

                try {
                    if (!catatan.getValue().sha256.equals(sha256Lokal(lokal, catatan.getValue()))) {
                        iterator.remove();

                        continue;
                    }

                    Files.delete(lokal);
                    iterator.remove();
                    deleted++;
                } catch (IOException e) {
                    needsRestart = true;
                    System.out.println("Pembaruan : gagal menghapus " + catatan.getKey() + " : " + e);
                }
            }
        }

        status.etag = needsRestart ? null : response.headers().firstValue("ETag").orElse(null);

        simpanStatus(status);

        if (updated > 0 || deleted > 0) {
            System.out.println("Pembaruan : " + updated + " berkas sumber daya diperbarui, " + deleted + " berkas dihapus");
        }
    }

    private Path getLocalDir(String path) {
        if (null == path || path.isEmpty() || path.indexOf('\\') >= 0 || path.indexOf(':') >= 0 || path.startsWith("/") || path.endsWith("/")) {
            return null;
        }

        String[] segments = path.split("/");

        if (segments.length < 2) {
            return null;
        }

        for (String dir : segments) {
            if (dir.isEmpty() || ".".equals(dir) || "..".equals(dir)) {
                return null;
            }
        }

        String kecil = path.toLowerCase(Locale.ROOT);

        for (String prefix : EXCLUDE_DIR) {
            if (kecil.startsWith(prefix)) {
                return null;
            }
        }

        for (String extenstion : EXCLUDE_EXT) {
            if (kecil.endsWith(extenstion)) {
                return null;
            }
        }

        if (kecil.startsWith("setting/") && (kecil.endsWith(".xml") || kecil.endsWith(".ini"))) {
            return null;
        }

        Path localPath = installDir.resolve(path).normalize();

        return localPath.startsWith(installDir) ? localPath : null;
    }

    private void install(ManifestEntry entry, Path localPath) throws IOException, InterruptedException {
        Files.createDirectories(stagingDir);
        Path temp = Files.createTempFile(stagingDir, "sumberdaya-", ".tmp");

        try {
            client.download("sumberdaya", entry.path, temp);

            if (Files.size(temp) != entry.ukuran || !entry.sha256.equalsIgnoreCase(sha256(temp))) {
                throw new IOException("isi berkas tidak sesuai manifest");
            }

            Files.createDirectories(localPath.getParent());
            Files.move(temp, localPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private void cleanStaging() {
        if (!Files.isDirectory(stagingDir)) {
            return;
        }

        try (DirectoryStream<Path> sisa = Files.newDirectoryStream(stagingDir, "sumberdaya-*.tmp")) {
            for (Path berkas : sisa) {
                Files.deleteIfExists(berkas);
            }
        } catch (IOException e) {
            System.out.println("Pembaruan : gagal membersihkan staging : " + e);
        }
    }

    private static String sha256Lokal(Path localPath, FileMetadata fileMetadata) throws IOException {
        if (!Files.isRegularFile(localPath)) {
            return null;
        }

        if (null != fileMetadata && fileMetadata.ukuran == Files.size(localPath) && fileMetadata.diubah == Files.getLastModifiedTime(localPath).toMillis()) {
            return fileMetadata.sha256;
        }

        return sha256(localPath);
    }

    private static FileMetadata catat(Path localPath, String sha256) throws IOException {
        FileMetadata catatan = new FileMetadata();
        catatan.ukuran = Files.size(localPath);
        catatan.diubah = Files.getLastModifiedTime(localPath).toMillis();
        catatan.sha256 = sha256;

        return catatan;
    }

    private static String sha256(Path file) throws IOException {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        try (InputStream is = Files.newInputStream(file)) {
            byte[] chunks = new byte[65536];
            int read;

            while ((read = is.read(chunks)) > 0) {
                digest.update(chunks, 0, read);
            }
        }

        StringBuilder hex = new StringBuilder(64);

        for (byte b : digest.digest()) {
            hex.append(Character.forDigit((b >> 4) & 0xf, 16)).append(Character.forDigit(b & 0xf, 16));
        }

        return hex.toString();
    }

    private Status readStatus() {
        if (Files.isRegularFile(fileStatus)) {
            try {
                Status status = mapper.readValue(fileStatus.toFile(), Status.class);

                if (null != status.files) {
                    return status;
                }
            } catch (IOException e) {
                System.out.println("Pembaruan : status " + "sumberdaya rusak, dibuat ulang : " + e);
            }
        }

        return new Status();
    }

    private void simpanStatus(Status status) throws IOException {
        Files.createDirectories(fileStatus.getParent());
        Path temp = Files.createTempFile(fileStatus.getParent(), "sumberdaya-", ".tmp");

        try {
            mapper.writeValue(temp.toFile(), status);
            Files.move(temp, fileStatus, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    static final class Manifest {
        public List<ManifestEntry> files = new ArrayList<>();
    }

    static final class ManifestEntry {
        public String path;
        public long ukuran;
        public String sha256;
    }

    static final class Status {
        public String etag;
        public Map<String, FileMetadata> files = new TreeMap<>();
    }

    static final class FileMetadata {
        public long ukuran;
        public long diubah;
        public String sha256;
        public long hilangSejak;
    }
}
