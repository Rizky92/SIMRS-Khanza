/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import bridging.ApiSatuSehat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * PENGIRIM PurificationDecision — langkah 9 "Tindak Lanjut Hasil Purifikasi"
 * pada alur Klaim BPJS-K.
 *
 * Berbeda dari ClaimResponse (langkah 8 & 10) yang dikirim BPJS-K, resource INI
 * dikirim FASYANKES. Buktinya `identifier.system` = `purificationdecision/{Org_RS}`:
 * namespace milik RS. Bandingkan ClaimResponse yang memakai `{Org_BPJS}` — pola yang
 * sama sudah terbukti di SatuSehatBilling, di mana memakai org yang salah pada system
 * URL ditolak "Wrong organization ID provided by system URL" (RuleNumber 10200).
 *
 * CATATAN: `PurificationDecision` BUKAN resource FHIR R4 standar — ini resource khas
 * Kemenkes. Karena itu tidak diasumsikan bisa dibungkus transaction bundle seperti
 * resource lain; dikirim POST tunggal ke endpoint-nya sendiri.
 *
 * Prasyarat: ClaimResponse purifikasi (langkah 8) sudah disinkronkan lebih dulu oleh
 * {@link SatuSehatClaimResponse}, karena `claimResponse.reference` menunjuk ke sana.
 */
public class SatuSehatPurificationDecision {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    /**
     * Satu-satunya kode yang SUDAH TERVERIFIKASI dari contoh resmi. Kode lain
     * (mis. tidak lanjut / perbaikan) belum diketahui — jangan ditebak, minta
     * pemanggil menyediakannya lewat {@link #kirim(String, String, String)}.
     */
    public static final String KODE_LANJUT = "TK000049";
    public static final String DISPLAY_LANJUT = "Lanjut";
    private static final String SYSTEM_KEPUTUSAN = "http://terminology.kemkes.go.id";

    public SatuSehatPurificationDecision() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi PurificationDecision : " + e);
        }
    }

    /** Data pendukung yang harus ada sebelum keputusan bisa dikirim. */
    private static class Konteks {
        String idOrg = "", idOrgBpjs = "", idClaimResponse = "";
        String idTerkirim = "";      // sudah pernah dikirim? (idempotensi)
    }

    // ====================== API PUBLIK ======================

    /** Kirim keputusan "Lanjut" (TK000049) — kasus paling umum. */
    public boolean kirimLanjut(String noRawat) {
        return kirim(noRawat, KODE_LANJUT, DISPLAY_LANJUT);
    }

    /**
     * Kirim keputusan tindak lanjut purifikasi.
     * Aman dipanggil berulang: bila sudah pernah terkirim, dilewati (lihat {@link #kirimUlang}).
     *
     * @param kode    status.coding.code, mis. TK000049
     * @param display status.coding.display, mis. "Lanjut"
     * @return true bila terkirim (atau memang sudah terkirim sebelumnya)
     */
    public boolean kirim(String noRawat, String kode, String display) {
        return kirim(noRawat, kode, display, false);
    }

    /** Paksa kirim ulang walau sudah pernah terkirim (mis. keputusan diralat). */
    public boolean kirimUlang(String noRawat, String kode, String display) {
        return kirim(noRawat, kode, display, true);
    }

    private boolean kirim(String noRawat, String kode, String display, boolean paksa) {
        if (nz(kode).trim().equals("")) {
            System.out.println("PurificationDecision " + noRawat + " : kode keputusan kosong, dibatalkan.");
            return false;
        }
        Konteks k = ambilKonteks(noRawat);
        if (k == null) return false;
        if (k.idClaimResponse.equals("")) {
            System.out.println("PurificationDecision " + noRawat + " : ClaimResponse purifikasi belum ada. "
                    + "Jalankan sinkronisasi ClaimResponse (langkah 8) lebih dulu.");
            return false;
        }
        if (!paksa && !k.idTerkirim.equals("")) {
            System.out.println("PurificationDecision " + noRawat + " : sudah pernah dikirim ("
                    + k.idTerkirim + "), dilewati. Pakai kirimUlang() bila keputusan diralat.");
            return true;
        }
        if (k.idOrg.equals("") || k.idOrgBpjs.equals("")) {
            System.out.println("PurificationDecision " + noRawat + " : IDSATUSEHAT / IDORGBPJSSATUSEHAT kosong.");
            return false;
        }

        String id = stableResourceId(noRawat);
        ObjectNode body = bangun(noRawat, k, id, kode, display);
        String hasil = posting(body);
        if (hasil == null) return false;

        // Server boleh menetapkan id sendiri; pakai yang dikembalikan bila ada.
        String idFinal = id;
        try {
            JsonNode r = mapper.readTree(hasil);
            String idServer = nz(r.path("id").asText()).trim();
            if (!idServer.equals("")) idFinal = idServer;
        } catch (Exception ign) {
            // Balasan bukan JSON — tetap catat dengan id lokal.
        }
        simpan(noRawat, idFinal, kode, display);
        System.out.println("PurificationDecision " + noRawat + " : terkirim " + idFinal
                + " (" + kode + " " + display + ")");
        return true;
    }

    /** PREVIEW: rakit JSON tanpa mengirim — dipakai panel pratinjau / uji. */
    public JsonNode bangun(String noRawat, String kode, String display) {
        Konteks k = ambilKonteks(noRawat);
        if (k == null) return null;
        return bangun(noRawat, k, stableResourceId(noRawat), kode, display);
    }

    // ====================== PAYLOAD ======================

    private ObjectNode bangun(String noRawat, Konteks k, String id, String kode, String display) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "PurificationDecision");
        r.put("id", id);
        ObjectNode iden = r.putArray("identifier").addObject();
        // Namespace milik RS (Org_id), BUKAN Org BPJS — RS yang mengirim resource ini.
        iden.put("system", "http://sys-ids.kemkes.go.id/purificationdecision/" + k.idOrg);
        iden.put("value", noRawat);
        r.put("created", waktuSekarang());
        // PERHATIAN: `status` di sini CodeableConcept, bukan kode string seperti
        // resource FHIR pada umumnya.
        ObjectNode c = r.putObject("status").putArray("coding").addObject();
        c.put("system", SYSTEM_KEPUTUSAN);
        c.put("code", kode);
        c.put("display", display);
        r.putObject("insurer").put("reference", "Organization/" + k.idOrgBpjs);
        r.putObject("provider").put("reference", "Organization/" + k.idOrg);
        r.putObject("claimResponse").put("reference", "ClaimResponse/" + k.idClaimResponse);
        return r;
    }

    /**
     * POST tunggal ke /PurificationDecision.
     *
     * TIDAK dibungkus transaction bundle: resource ini di luar FHIR R4 standar, jadi
     * tak bisa diasumsikan didukung mesin transaction. Sekaligus sesuai sifat langkah 9
     * yang dikirim per-keputusan, bukan serentak sepaket — satu keputusan ditolak tidak
     * boleh menggagalkan keputusan klaim lain.
     */
    private String posting(ObjectNode body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String payload = mapper.writeValueAsString(body);
            System.out.println("Request JSON PurificationDecision : " + payload);
            // getBytes(UTF_8): RestTemplate lama mengirim String sebagai ISO-8859-1
            // sehingga karakter non-ASCII rusak.
            HttpEntity requestEntity = new HttpEntity(payload.getBytes(StandardCharsets.UTF_8), headers);
            String hasil = api.getRest()
                    .exchange(link + "/PurificationDecision", HttpMethod.POST, requestEntity, String.class)
                    .getBody();
            System.out.println("Result JSON PurificationDecision : " + hasil);
            return hasil;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("Error PurificationDecision " + ex.getStatusCode()
                    + " => " + ex.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.out.println("Notifikasi PurificationDecision kirim : " + e);
            return null;
        }
    }

    // ====================== DATA ======================

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = new Konteks();
        k.idOrg = nz(koneksiDB.IDSATUSEHAT()).trim();
        k.idOrgBpjs = nz(koneksiDB.IDORGBPJSSATUSEHAT()).trim();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(id_claimresponse_purifikasi,'') as id_cr, "
                    + "ifnull(id_purificationdecision,'') as id_pd "
                    + "from satu_sehat_claimresponse where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                k.idClaimResponse = nz(r.getString("id_cr")).trim();
                k.idTerkirim = nz(r.getString("id_pd")).trim();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi PurificationDecision ambilKonteks : " + e);
            return null;
        }
        return k;
    }

    /**
     * Simpan jejak keputusan. `insert … on duplicate key update` (BUKAN replace):
     * satu baris `satu_sehat_claimresponse` juga menampung hasil purifikasi &
     * verifikasi — replace akan menghapusnya.
     */
    private void simpan(String noRawat, String id, String kode, String display) {
        String sql = "insert into satu_sehat_claimresponse "
                + "(no_rawat,id_purificationdecision,keputusan_kode,keputusan_display,tgl_keputusan,tgl_sync) "
                + "values (?,?,?,?,now(),now()) "
                + "on duplicate key update id_purificationdecision=values(id_purificationdecision), "
                + "keputusan_kode=values(keputusan_kode), keputusan_display=values(keputusan_display), "
                + "tgl_keputusan=now(), tgl_sync=now()";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            p.setString(2, id);
            p.setString(3, kode);
            p.setString(4, display);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi PurificationDecision simpan : " + e);
        }
    }

    // ====================== UTIL ======================

    private String stableResourceId(String noRawat) {
        return UUID.nameUUIDFromBytes(("PurificationDecision|" + nz(noRawat).trim())
                .getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String waktuSekarang() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new java.util.Date());
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
