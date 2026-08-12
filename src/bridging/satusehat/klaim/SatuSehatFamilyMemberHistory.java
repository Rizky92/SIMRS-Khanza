/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;
import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
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
 * Pengiriman resource FamilyMemberHistory (riwayat penyakit keluarga) ke SATUSEHAT.
 *
 * Sumber: kolom "hubungan" (relasi keluarga) + "rpk" (riwayat penyakit keluarga) pada
 * penilaian_medis_igd / penilaian_medis_ralan / penilaian_medis_ranap / penilaian_medis_ranap_kandungan.
 *
 * FamilyMemberHistory mereferensi Patient (bukan Encounter). Idempotent via business identifier +
 * ifNoneExist (kirim ulang tidak menduplikasi resource di server).
 */
public class SatuSehatFamilyMemberHistory {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatFamilyMemberHistory() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi FamilyMemberHistory : " + e);
        }
    }

    private static class FmhData {
        String noRawat="", hubungan="", rpk="";
        String idPasien="", namaPasien="", idOrg="";
    }

    /**
     * Bangun & kirim FamilyMemberHistory untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter (dipakai sebagai gate; FamilyMemberHistory sendiri tak mereferensi Encounter)
     */
    /** PREVIEW: rakit Bundle FamilyMemberHistory (POST form) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        FmhData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) return null;
        if (d.hubungan.equals("") && d.rpk.equals("")) return null;
        String hl = d.hubungan.toLowerCase();
        if (hl.contains("diri sendiri") || hl.contains("diri saya")) return null;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        String[] relCoding = relationshipCoding(d.hubungan);
        ObjectNode fmh = buatFamilyMemberHistory(d, relCoding);
        tambahEntry(entries, fmh, "FamilyMemberHistory", "");
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;   // tunggu Encounter dulu
        }
        FmhData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("")) {
            return;
        }
        // Tidak ada data riwayat keluarga -> dilewati.
        if (d.hubungan.equals("") && d.rpk.equals("")) {
            return;
        }
        // "Diri Sendiri/Diri Saya" bukan anggota keluarga -> dilewati.
        String hl = d.hubungan.toLowerCase();
        if (hl.contains("diri sendiri") || hl.contains("diri saya")) {
            System.out.println("Notifikasi FamilyMemberHistory : hubungan 'Diri Sendiri' untuk no_rawat "
                    + noRawat + ". Dilewati (bukan riwayat keluarga).");
            return;
        }

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        String[] relCoding = relationshipCoding(d.hubungan);
        ObjectNode fmh = buatFamilyMemberHistory(d, relCoding);
        // SATUSEHAT tidak menghormati ifNoneExist untuk FamilyMemberHistory (tetap "Found duplicate").
        // -> cari id existing by patient+relationship, lalu PUT (update); kalau tak ada, POST.
        String idLama = cariIdServer("FamilyMemberHistory",
                "patient=" + d.idPasien + "&relationship=" + relCoding[1]);
        tambahEntry(entries, fmh, "FamilyMemberHistory", idLama);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL FamilyMemberHistory : " + link);
        System.out.println("Request JSON FMH : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error FMH Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error FMH OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error FMH Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON FMH : " + hasil);
    }

    /** Bangun resource FamilyMemberHistory dari hubungan + rpk. */
    private ObjectNode buatFamilyMemberHistory(FmhData d, String[] relCoding) {
        ObjectNode fmh = mapper.createObjectNode();
        fmh.put("resourceType", "FamilyMemberHistory");
        ObjectNode iden = fmh.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/family-member-history/" + d.idOrg);
        iden.put("value", d.noRawat + "-" + normal(d.hubungan));
        // status: ada penyakit -> completed; tanpa penyakit -> health-unknown.
        fmh.put("status", d.rpk.equals("") ? "health-unknown" : "completed");
        fmh.putObject("patient").put("reference", "Patient/" + d.idPasien);
        // relationship WAJIB punya coding (RuleNumber 10114).
        ObjectNode rel = fmh.putObject("relationship");
        ObjectNode coding = rel.putArray("coding").addObject();
        coding.put("system", relCoding[0]);
        coding.put("code", relCoding[1]);
        coding.put("display", relCoding[2]);
        rel.put("text", d.hubungan.equals("") ? "Anggota keluarga" : d.hubungan);
        // condition: penyakit keluarga (code.text dari rpk).
        if (!d.rpk.equals("")) {
            ObjectNode cond = fmh.putArray("condition").addObject();
            cond.putObject("code").put("text", d.rpk);
        }
        return fmh;
    }

    /** {system, code, display} relationship: hubungan dikenal -> SNOMED; lainnya -> FAMMEMB (v3-RoleCode). */
    private String[] relationshipCoding(String hubungan) {
        String[] m = mapHubungan(hubungan);
        if (m != null) return new String[]{"http://snomed.info/sct", m[0], m[1]};
        return new String[]{"http://terminology.hl7.org/CodeSystem/v3-RoleCode", "FAMMEMB", "family member"};
    }

    /**
     * Map nilai hubungan (combobox Khanza) -> {kode SNOMED, display}.
     * Hanya beberapa yang pasti (Ibu dikonfirmasi 72705000 di dokumentasi SATUSEHAT); sisanya best-effort,
     * PERLU diverifikasi terhadap value set FamilyMemberHistory.relationship SATUSEHAT.
     */
    private String[] mapHubungan(String h) {
        if (h == null) return null;
        String s = h.trim().toLowerCase();
        if (s.contains("ayah") || s.contains("bapak")) return new String[]{"9947008", "Father"};
        if (s.contains("ibu")) return new String[]{"72705000", "Mother"};
        if (s.contains("saudara") || s.contains("kakak") || s.contains("adik")) return new String[]{"375005", "Sibling"};
        if (s.contains("anak")) return new String[]{"408097001", "Child"};
        if (s.contains("suami")) return new String[]{"127849001", "Husband"};
        if (s.contains("istri")) return new String[]{"127848009", "Wife"};
        if (s.contains("kakek")) return new String[]{"113159008", "Grandfather"};
        if (s.contains("nenek")) return new String[]{"113160008", "Grandmother"};
        if (s.contains("keponakan")) return new String[]{"60614009", "Nephew"};
        if (s.contains("paman")) return new String[]{"38048003", "Uncle"};
        if (s.contains("bibi") || s.contains("tante")) return new String[]{"5992002", "Aunt"};
        return null;   // tak dikenali -> kirim text saja
    }

    /** Tambah resource ke bundle: PUT bila id existing ada (update), else POST. */
    private void tambahEntry(ArrayNode entries, ObjectNode resource, String resourceType, String idLama) {
        boolean adaId = idLama != null && !idLama.equals("");
        String fullUrl = adaId ? (resourceType + "/" + idLama) : ("urn:uuid:" + UUID.randomUUID().toString());
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", fullUrl);
        if (adaId) resource.put("id", idLama);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        if (adaId) {
            request.put("method", "PUT");
            request.put("url", resourceType + "/" + idLama);
        } else {
            request.put("method", "POST");
            request.put("url", resourceType);
        }
    }

    /** GET search resource -> id resource pertama (atau "" bila tidak ada / gagal). */
    private String cariIdServer(String resourceType, String query) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            java.net.URI uri = java.net.URI.create(link + "/" + resourceType + "?" + query + "&_count=1");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            if (r.path("total").asInt(0) > 0) {
                JsonNode es = r.path("entry");
                if (es.isArray() && es.size() > 0) {
                    return nz(es.get(0).path("resource").path("id").asText());
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi FMH cariIdServer : " + e);
        }
        return "";
    }

    /** Ambil hubungan + rpk dari salah satu tabel penilaian_medis (igd/ralan/ranap/kandungan). */
    private FmhData ambilData(String noRawat) {
        FmhData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull((select hubungan from penilaian_medis_igd m where m.no_rawat=rp.no_rawat limit 1),'') as h_igd, "
                    + "ifnull((select rpk from penilaian_medis_igd m where m.no_rawat=rp.no_rawat limit 1),'') as r_igd, "
                    + "ifnull((select hubungan from penilaian_medis_ralan m where m.no_rawat=rp.no_rawat limit 1),'') as h_ralan, "
                    + "ifnull((select rpk from penilaian_medis_ralan m where m.no_rawat=rp.no_rawat limit 1),'') as r_ralan, "
                    + "ifnull((select hubungan from penilaian_medis_ranap m where m.no_rawat=rp.no_rawat limit 1),'') as h_ranap, "
                    + "ifnull((select rpk from penilaian_medis_ranap m where m.no_rawat=rp.no_rawat limit 1),'') as r_ranap, "
                    + "ifnull((select hubungan from penilaian_medis_ranap_kandungan m where m.no_rawat=rp.no_rawat limit 1),'') as h_kand, "
                    + "ifnull((select rpk from penilaian_medis_ranap_kandungan m where m.no_rawat=rp.no_rawat limit 1),'') as r_kand "
                    + "from reg_periksa rp inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new FmhData();
                d.noRawat = noRawat;
                // Pilih sumber yang ada datanya (urut: igd, ralan, ranap, kandungan).
                String[][] sumber = {
                    {nz(r.getString("h_igd")), nz(r.getString("r_igd"))},
                    {nz(r.getString("h_ralan")), nz(r.getString("r_ralan"))},
                    {nz(r.getString("h_ranap")), nz(r.getString("r_ranap"))},
                    {nz(r.getString("h_kand")), nz(r.getString("r_kand"))}
                };
                for (String[] sm : sumber) {
                    String h = bersihkan(sm[0]);
                    String rpk = bersihkan(sm[1]);
                    if (!h.equals("") || !rpk.equals("")) {
                        d.hubungan = h;
                        d.rpk = rpk;
                        break;
                    }
                }
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi FamilyMemberHistory ambilData : " + e);
        }
        return d;
    }

    private String normal(String s) {
        if (s == null) return "";
        String n = s.replaceAll("[^A-Za-z0-9]", "");
        return n.equals("") ? "keluarga" : n;
    }

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
