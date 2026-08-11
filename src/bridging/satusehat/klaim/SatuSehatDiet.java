/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sub-sender Diet untuk flow SatuSehatBundle: mengirim pemberian diet pasien sebagai FHIR NutritionOrder.
 *
 * Per (kd_kamar, tanggal, waktu) -> satu NutritionOrder dengan oralDiet.type (SNOMED dari
 * satu_sehat_mapping_diet) untuk semua diet pada waktu makan itu. Sumber: detail_beri_diet + diet +
 * jam_diet_pasien + satu_sehat_mapping_diet. Idempotent (stableResourceId -> PUT), chunked.
 */
public class SatuSehatDiet {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private static final String ZONA = "+07:00";

    public SatuSehatDiet() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Diet : " + e);
        }
    }

    private static class Konteks {
        String idPasien = "", namaPasien = "", idDokter = "", namaDokter = "";
    }

    private static class DietItem {
        String kdDiet, namaDiet, snomedCode, snomedDisplay;
        DietItem(String kdDiet, String namaDiet, String snomedCode, String snomedDisplay) {
            this.kdDiet = kdDiet; this.namaDiet = namaDiet; this.snomedCode = snomedCode; this.snomedDisplay = snomedDisplay;
        }
    }

    private static class DietGroup {
        String kdKamar, namaKamar, tanggal, waktu, jam;
        List<DietItem> diets = new ArrayList<>();
    }

    /** PREVIEW: rakit Bundle Diet (NutritionOrder per waktu makan) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (isBlank(idEncounter)) return null;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || isBlank(k.idPasien)) return null;
        List<DietGroup> daftar = ambilDiet(noRawat);
        if (daftar.isEmpty()) return null;
        String patientRef = "Patient/" + k.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String idOrg = koneksiDB.IDSATUSEHAT();
        ArrayNode entries = mapper.createArrayNode();
        for (DietGroup g : daftar) {
            List<DietItem> mapped = new ArrayList<>();
            for (DietItem it : g.diets) {
                if (!isBlank(it.snomedCode) && !isBlank(it.snomedDisplay)) mapped.add(it);
            }
            if (mapped.isEmpty()) continue;
            String id = stableResourceId("NutritionOrder", noRawat, g.tanggal, g.waktu, g.kdKamar);
            ObjectNode no = buatNutritionOrder(g, mapped, noRawat, patientRef, encounterRef, k, idOrg);
            tambahEntryPut(entries, "NutritionOrder", id, no);
        }
        if (entries.size() == 0) return null;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        bundle.set("entry", entries);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (isBlank(idEncounter)) {
            return;
        }
        Konteks k = ambilKonteks(noRawat);
        if (k == null || isBlank(k.idPasien)) {
            System.out.println("Notifikasi Diet : ID pasien SATUSEHAT belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        List<DietGroup> daftar = ambilDiet(noRawat);
        if (daftar.isEmpty()) {
            return;
        }

        String patientRef = "Patient/" + k.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String idOrg = koneksiDB.IDSATUSEHAT();

        ArrayNode entries = mapper.createArrayNode();
        List<String[]> metas = new ArrayList<>();   // {tanggal, waktu, kdKamar, id}

        for (DietGroup g : daftar) {
            // Hanya diet yang sudah dimapping SNOMED yang dipakai sebagai oralDiet.type.
            List<DietItem> mapped = new ArrayList<>();
            for (DietItem it : g.diets) {
                if (!isBlank(it.snomedCode) && !isBlank(it.snomedDisplay)) mapped.add(it);
            }
            if (mapped.isEmpty()) {
                System.out.println("Notifikasi Diet : no_rawat " + noRawat + " " + g.tanggal + " " + g.waktu
                        + " dilewati. Diet belum dimapping SNOMED (satu_sehat_mapping_diet).");
                continue;
            }
            String id = stableResourceId("NutritionOrder", noRawat, g.tanggal, g.waktu, g.kdKamar);
            ObjectNode no = buatNutritionOrder(g, mapped, noRawat, patientRef, encounterRef, k, idOrg);
            tambahEntryPut(entries, "NutritionOrder", id, no);
            metas.add(new String[]{g.tanggal, g.waktu, g.kdKamar, id});
        }

        if (entries.size() == 0) {
            System.out.println("Notifikasi Diet : tidak ada NutritionOrder yang bisa dikirim untuk no_rawat " + noRawat + ".");
            return;
        }

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        bundle.set("entry", entries);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Diet : " + link);
        System.out.println("Total NutritionOrder : " + metas.size() + " (1 bundle).");
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Diet Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Diet OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Diet Body: " + body);
            }
            throw ex;
        }
        int ok = 0;
        JsonNode entryResponse = mapper.readTree(hasil).path("entry");
        for (int x = 0; x < metas.size(); x++) {
            if (!entryResponse.isArray() || entryResponse.size() <= x) break;
            JsonNode resp = entryResponse.get(x).path("response");
            if (resp.path("status").asText().startsWith("2")) ok++;
            String idResource = extractId(resp.path("location").asText());
            if (isBlank(idResource)) idResource = resp.path("resourceID").asText();
            if (isBlank(idResource)) idResource = entryResponse.get(x).path("resource").path("id").asText();
            if (isBlank(idResource)) continue;
            String[] m = metas.get(x);
            simpanDiet(noRawat, m[0], m[1], m[2], idResource);
        }
        System.out.println("Notifikasi Diet : selesai. " + ok + "/" + metas.size() + " NutritionOrder berhasil (2xx).");
    }

    /** NutritionOrder (oralDiet) untuk satu waktu makan. */
    private ObjectNode buatNutritionOrder(DietGroup g, List<DietItem> diets, String noRawat,
            String patientRef, String encounterRef, Konteks k, String idOrg) {
        ObjectNode resource = mapper.createObjectNode();
        resource.put("resourceType", "NutritionOrder");
        ObjectNode iden = resource.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/nutrition-order/" + idOrg);
        iden.put("value", noRawat.replace("/", "-") + "-" + g.tanggal + "-" + g.waktu + "-" + g.kdKamar);
        resource.put("status", "active");
        resource.put("intent", "order");
        ObjectNode patient = resource.putObject("patient");
        patient.put("reference", patientRef);
        patient.put("display", k.namaPasien);
        resource.putObject("encounter").put("reference", encounterRef);
        String dateTime = bangunDateTimeAman(g.tanggal, g.jam);
        if (!isBlank(dateTime)) resource.put("dateTime", dateTime);
        if (!isBlank(k.idDokter)) {
            ObjectNode orderer = resource.putObject("orderer");
            orderer.put("reference", "Practitioner/" + k.idDokter);
            orderer.put("display", k.namaDokter);
        }
        ObjectNode oralDiet = resource.putObject("oralDiet");
        ArrayNode types = oralDiet.putArray("type");
        StringBuilder namaDiets = new StringBuilder();
        for (DietItem it : diets) {
            ObjectNode type = types.addObject();
            ObjectNode coding = type.putArray("coding").addObject();
            coding.put("system", "http://snomed.info/sct");
            coding.put("code", it.snomedCode);
            coding.put("display", it.snomedDisplay);
            type.put("text", it.namaDiet);
            if (namaDiets.length() > 0) namaDiets.append(", ");
            namaDiets.append(nz(it.namaDiet));
        }
        ObjectNode schedule = oralDiet.putArray("schedule").addObject();
        if (!isBlank(dateTime)) schedule.putArray("event").add(dateTime);
        String jam = isBlank(g.jam) ? "00:00" : g.jam;
        schedule.putObject("repeat").putArray("timeOfDay").add(jam + ":00");
        oralDiet.put("instruction", "Diet " + namaDiets + " untuk " + g.waktu + " jam " + jam
                + (isBlank(g.namaKamar) ? "" : (" di " + g.namaKamar)));
        return resource;
    }

    private void tambahEntryPut(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        resource.put("id", id);
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    /** Simpan id ke satu_sehat_diet. tanggal datetime dipakai sebagai (tanggal + penanda waktu/kamar). */
    private void simpanDiet(String noRawat, String tanggal, String waktu, String kdKamar, String idVal) {
        if (isBlank(idVal)) return;
        // satu_sehat_diet: (no_rawat, tanggal[datetime], id_diet). Pakai datetime unik per waktu makan
        // dengan menyandikan jam dari indeks waktu (cukup untuk membedakan baris secara lokal).
        String tglDt = tanggal + " 00:00:00";
        Sequel.queryu2("replace into satu_sehat_diet (no_rawat,tanggal,id_diet) values (?,?,?)",
                3, new String[]{noRawat, tglDt, idVal});
    }

    // ====================== Query data ======================

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = null;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select p.no_ktp as ktp_pasien, p.nm_pasien, "
                + "ifnull(d.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dokter "
                + "from reg_periksa rp inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                + "left join dokter d on d.kd_dokter=rp.kd_dokter "
                + "left join pegawai pg on pg.nik=rp.kd_dokter where rp.no_rawat=? limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    k = new Konteks();
                    k.namaPasien = nz(r.getString("nm_pasien"));
                    k.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                    k.namaDokter = nz(r.getString("nm_dokter"));
                    String ktpDokter = nz(r.getString("ktp_dokter"));
                    if (!isBlank(ktpDokter)) k.idDokter = nz(cek.tampilIDParktisi(ktpDokter));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Diet ambilKonteks : " + e);
        }
        return k;
    }

    private List<DietGroup> ambilDiet(String noRawat) {
        LinkedHashMap<String, DietGroup> groups = new LinkedHashMap<>();
        String sql = "select detail.kd_kamar, detail.tanggal, detail.waktu, ifnull(jam.jam,'00:00') as jam, "
                + "ifnull(bangsal.nm_bangsal,'') as nm_kamar, "
                + "diet.kd_diet, diet.nama_diet, "
                + "ifnull(mapping.snomed_code,'') as snomed_code, ifnull(mapping.snomed_display,'') as snomed_display "
                + "from detail_beri_diet detail "
                + "inner join diet on diet.kd_diet=detail.kd_diet "
                + "left join satu_sehat_mapping_diet mapping on mapping.kd_diet=diet.kd_diet "
                + "left join jam_diet_pasien jam on jam.waktu=detail.waktu "
                + "left join kamar on kamar.kd_kamar=detail.kd_kamar "
                + "left join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal "
                + "where detail.no_rawat=? "
                + "order by detail.tanggal asc, detail.waktu asc, detail.kd_kamar asc, diet.nama_diet asc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String tanggal = nz(r.getString("tanggal"));
                    String waktu = nz(r.getString("waktu"));
                    String kdKamar = nz(r.getString("kd_kamar"));
                    String key = tanggal + "|" + waktu + "|" + kdKamar;
                    DietGroup g = groups.get(key);
                    if (g == null) {
                        g = new DietGroup();
                        g.tanggal = tanggal;
                        g.waktu = waktu;
                        g.kdKamar = kdKamar;
                        g.jam = nz(r.getString("jam"));
                        g.namaKamar = nz(r.getString("nm_kamar"));
                        groups.put(key, g);
                    }
                    g.diets.add(new DietItem(nz(r.getString("kd_diet")), nz(r.getString("nama_diet")),
                            nz(r.getString("snomed_code")), nz(r.getString("snomed_display"))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Diet ambilDiet : " + e);
        }
        return new ArrayList<>(groups.values());
    }

    // ====================== Util ======================

    private String bangunDateTimeAman(String tanggal, String jam) {
        if (isBlank(tanggal) || tanggal.startsWith("0000-")) return "";
        String t = tanggal.trim();
        if (t.contains("T")) t = t.substring(0, t.indexOf('T'));
        if (t.contains(" ")) t = t.substring(0, t.indexOf(' '));
        String j = isBlank(jam) ? "00:00:00" : jam.trim();
        if (j.contains(".")) j = j.substring(0, j.indexOf('.'));
        if (j.length() == 5) j = j + ":00";
        try {
            OffsetDateTime dt = OffsetDateTime.parse(t + "T" + j + ZONA, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return dt.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (Exception e) {
            return "";
        }
    }

    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(isBlank(resourceType) ? "Resource" : resourceType.trim());
        if (keys != null) {
            for (String key : keys) sb.append("|").append(isBlank(key) ? "-" : key.trim());
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String extractId(String location) {
        if (isBlank(location)) return "";
        String loc = location;
        int hist = loc.indexOf("/_history");
        if (hist >= 0) loc = loc.substring(0, hist);
        int slash = loc.lastIndexOf("/");
        return slash >= 0 ? loc.substring(slash + 1) : loc;
    }

    private boolean isBlank(String data) {
        return data == null || data.trim().equals("") || data.trim().equals("-");
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
