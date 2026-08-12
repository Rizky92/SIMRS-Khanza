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
import java.util.ArrayList;
import java.util.HashMap;
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
 * Pengiriman "Dokumen Triase IGD" (FHIR Composition - LOINC 75500-9) ke SATUSEHAT.
 *
 * Composition adalah dokumen/indeks: section.entry hanya MEREFERENSI resource lain.
 * Maka satu Bundle transaction berisi:
 *   - Observation tanda vital (TD, Nadi, Respirasi, Suhu, SpO2) + Nyeri
 *   - Observation Mode of arrival, Kategori triase, Disposisi IGD
 *   - Condition keluhan utama
 *   - Composition yang mengindeks semuanya
 *
 * Id tiap resource disimpan ke tabel satu_sehat_triase_igd; pengiriman ulang
 * memakai PUT (update) sehingga tidak menduplikasi resource di server.
 */
public class SatuSehatTriaseIGD {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private String performerRef = "";   // Practitioner/{id} untuk Observation.performer (wajib)

    public SatuSehatTriaseIGD() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Triase IGD : " + e);
        }
    }

    /** Penampung data triase + header kunjungan. */
    private static class TriaseData {
        String noRawat="", waktu="", statusLanjut="";
        String caraMasuk="", alatTransportasi="", alasanKedatangan="", keteranganKedatangan="";
        String kategori="", tekananDarah="", nadi="", pernapasan="", suhu="", spo2="", nyeri="";
        String keluhanUtama="";
        // ABCDE dari penilaian_medis_igd:
        String gcs="", kesadaran="", medTd="", medNadi="", medRr="", medSpo="", thoraks="", ekstremitas="", ketLokalis="", ketFisik="";
        String rps="";   // Riwayat Penyakit Sekarang (penilaian_medis_igd.rps)
        String rpd="";   // Riwayat Penyakit Dahulu -> Hospital Course (penilaian_medis_igd.rpd)
        String alergi="";   // Alergi (penilaian_medis_igd.alergi)
        String lab="";      // Pemeriksaan Laboratorium (penilaian_medis_igd.lab)
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /**
     * Bangun & kirim Bundle Triase IGD untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan IGD
     * @param idEncounter id Encounter SATUSEHAT (WAJIB sudah ada — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Triase IGD (Observation vital/triase + Composition) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        TriaseData t = ambilData(noRawat);
        if (t == null) return null;
        if (t.idPasien.equals("")) return null;

        String patientRef   = "Patient/" + t.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu        = t.waktu;
        performerRef = t.idDokter.equals("") ? "" : ("Practitioner/" + t.idDokter);

        Map<String,String> idLama = ambilIdLama(noRawat);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        List<String> slotOrder = new ArrayList<>();
        Map<String,String> ref = new HashMap<>();

        String caraDatang = gabung(t.caraMasuk, t.alatTransportasi, t.alasanKedatangan, t.keteranganKedatangan);
        if (!caraDatang.equals("")) {
            ObjectNode o = buatObservationTeks("11459-5", "Mode of arrival", "survey",
                    caraDatang, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_modearrival", "modearrival", o);
        }
        String[] transport = snomedTransport(t.alatTransportasi);
        if (transport != null) {
            ObjectNode o = buatObservationKode("74286-6", "Transport mode to hospital", "survey",
                    transport[0], transport[1], patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_transport", "transport", o);
        }
        Double[] td = parseTD(t.tekananDarah);
        if (td != null) {
            ObjectNode o = buatObservationTekananDarah(td[0], td[1], patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_td", "td", o);
        }
        Double nadi = parseAngka(t.nadi);
        if (nadi != null) {
            ObjectNode o = buatObservationKuantitas("8867-4", "Heart rate", "vital-signs",
                    nadi, "/min", "/min", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_nadi", "nadi", o);
        }
        Double resp = parseAngka(t.pernapasan);
        if (resp != null) {
            ObjectNode o = buatObservationKuantitas("9279-1", "Respiratory rate", "vital-signs",
                    resp, "/min", "/min", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_resp", "resp", o);
        }
        Double suhu = parseAngka(t.suhu);
        if (suhu != null) {
            ObjectNode o = buatObservationKuantitas("8310-5", "Body temperature", "vital-signs",
                    suhu, "Cel", "Cel", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_suhu", "suhu", o);
        }
        Double spo2 = parseAngka(t.spo2);
        if (spo2 != null) {
            ObjectNode o = buatObservationKuantitas("59408-5", "Oxygen saturation in Arterial blood by Pulse oximetry",
                    "vital-signs", spo2, "%", "%", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_spo2", "spo2", o);
        }
        Double nyeri = parseAngka(t.nyeri);
        if (nyeri != null) {
            ObjectNode o = buatObservationKuantitas("72514-3",
                    "Pain severity - 0-10 verbal numeric rating [Score] - Reported", "survey",
                    nyeri, "{score}", "{score}", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_nyeri", "nyeri", o);
        }
        if (!t.kategori.equals("")) {
            ObjectNode o = buatObservationTeks("11283-9", "Acuity assessment", "survey",
                    t.kategori, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_kategori", "kategori", o);
        }
        String disposisi = t.statusLanjut.equalsIgnoreCase("Ranap") ? "Rawat Inap" : "Rawat Jalan / Pulang";
        ObjectNode oDisp = buatObservationTeks("11302-7", "ED disposition", "survey",
                disposisi, patientRef, t.namaPasien, encounterRef, waktu);
        tambahObs(entries, slotOrder, ref, idLama, "id_obs_disposition", "disposition", oDisp);

        ObjectNode oAbcde = buatObservationAbcde(t, patientRef, encounterRef, waktu);
        if (oAbcde != null) {
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_abcde", "abcde", oAbcde);
        }

        int skalaTriase = hitungSkalaTriase(noRawat);
        ObjectNode oTriage = buatObservationTriageIndex(skalaTriase, patientRef, t.namaPasien,
                encounterRef, waktu, ref.get("abcde"));
        if (oTriage != null) {
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_triageindex", "triageindex", oTriage);
        }

        if (!t.keluhanUtama.equals("")) {
            ObjectNode o = buatObservationTeks("10154-3", "Chief complaint", "survey",
                    t.keluhanUtama, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_condition", "condition", o);
        }
        if (!t.rps.equals("")) {
            ObjectNode o = buatObservationTeks("10164-2", "History of Present illness Narrative", "exam",
                    t.rps, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_rps", "rps", o);
        }
        if (!t.rpd.equals("")) {
            ObjectNode o = buatObservationTeks("8648-8", "Hospital course Narrative", "exam",
                    t.rpd, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_rpd", "rpd", o);
        }
        String[] mapAlergi = cariMappingAlergi(t.alergi);
        if (mapAlergi != null && !mapAlergi[0].equals("")) {
            ObjectNode oAllergy = buatAllergyIntolerance(mapAlergi, noRawat, patientRef, t.namaPasien,
                    encounterRef, waktu, t.idOrg);
            tambahEntryResource(entries, slotOrder, ref, idLama, "id_allergy", "allergy", oAllergy,
                    "AllergyIntolerance");
        }
        if (!t.lab.equals("") && !t.lab.equals("-")) {
            ObjectNode o = buatObservationTeks("11502-2", "Laboratory report",
                    "laboratory", t.lab, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_lab", "lab", o);
        }

        ObjectNode comp = buatComposition(noRawat, t, patientRef, encounterRef, waktu, ref);
        tambahEntryResource(entries, slotOrder, ref, idLama, "id_composition", "composition", comp, "Composition");
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            System.out.println("Notifikasi Triase IGD : Encounter belum ada untuk no_rawat " + noRawat
                    + ". Triase dilewati (kirim Encounter dulu).");
            return;
        }
        TriaseData t = ambilData(noRawat);
        if (t == null) {
            System.out.println("Notifikasi Triase IGD : data_triase_igd tidak ada untuk no_rawat " + noRawat
                    + ". Triase dilewati.");
            return;
        }
        if (t.idPasien.equals("")) {
            System.out.println("Notifikasi Triase IGD : ID pasien SATUSEHAT belum ditemukan untuk no_rawat "
                    + noRawat + ". Triase dilewati.");
            return;
        }

        String patientRef   = "Patient/" + t.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu        = t.waktu;
        performerRef = t.idDokter.equals("") ? "" : ("Practitioner/" + t.idDokter);

        Map<String,String> idLama = ambilIdLama(noRawat);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        List<String> slotOrder = new ArrayList<>();   // urutan slot mengikuti urutan entry
        Map<String,String> ref = new HashMap<>();      // slot -> fullUrl (untuk referensi Composition)

        // --- Observation Mode of arrival ---
        Double dummy = null;
        String caraDatang = gabung(t.caraMasuk, t.alatTransportasi, t.alasanKedatangan, t.keteranganKedatangan);
        if (!caraDatang.equals("")) {
            ObjectNode o = buatObservationTeks("11459-5", "Mode of arrival", "survey",
                    caraDatang, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_modearrival", "modearrival", o);
        }
        // --- Observation Transport mode to hospital (74286-6, valueCodeableConcept SNOMED) dari alat_transportasi ---
        String[] transport = snomedTransport(t.alatTransportasi);
        if (transport != null) {
            ObjectNode o = buatObservationKode("74286-6", "Transport mode to hospital", "survey",
                    transport[0], transport[1], patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_transport", "transport", o);
        }
        // --- Observation Tekanan Darah (panel sistol/diastol) ---
        Double[] td = parseTD(t.tekananDarah);
        if (td != null) {
            ObjectNode o = buatObservationTekananDarah(td[0], td[1], patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_td", "td", o);
        }
        // --- Observation Nadi ---
        Double nadi = parseAngka(t.nadi);
        if (nadi != null) {
            ObjectNode o = buatObservationKuantitas("8867-4", "Heart rate", "vital-signs",
                    nadi, "/min", "/min", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_nadi", "nadi", o);
        }
        // --- Observation Respirasi ---
        Double resp = parseAngka(t.pernapasan);
        if (resp != null) {
            ObjectNode o = buatObservationKuantitas("9279-1", "Respiratory rate", "vital-signs",
                    resp, "/min", "/min", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_resp", "resp", o);
        }
        // --- Observation Suhu ---
        Double suhu = parseAngka(t.suhu);
        if (suhu != null) {
            ObjectNode o = buatObservationKuantitas("8310-5", "Body temperature", "vital-signs",
                    suhu, "Cel", "Cel", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_suhu", "suhu", o);
        }
        // --- Observation SpO2 ---
        Double spo2 = parseAngka(t.spo2);
        if (spo2 != null) {
            ObjectNode o = buatObservationKuantitas("59408-5", "Oxygen saturation in Arterial blood by Pulse oximetry",
                    "vital-signs", spo2, "%", "%", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_spo2", "spo2", o);
        }
        // --- Observation Nyeri ---
        Double nyeri = parseAngka(t.nyeri);
        if (nyeri != null) {
            ObjectNode o = buatObservationKuantitas("72514-3",
                    "Pain severity - 0-10 verbal numeric rating [Score] - Reported", "survey",
                    nyeri, "{score}", "{score}", patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_nyeri", "nyeri", o);
        }
        // --- Observation Kategori triase ---
        if (!t.kategori.equals("")) {
            ObjectNode o = buatObservationTeks("11283-9", "Acuity assessment", "survey",
                    t.kategori, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_kategori", "kategori", o);
        }
        // --- Observation Disposisi IGD ---
        String disposisi = t.statusLanjut.equalsIgnoreCase("Ranap") ? "Rawat Inap" : "Rawat Jalan / Pulang";
        ObjectNode oDisp = buatObservationTeks("11302-7", "ED disposition", "survey",
                disposisi, patientRef, t.namaPasien, encounterRef, waktu);
        tambahObs(entries, slotOrder, ref, idLama, "id_obs_disposition", "disposition", oDisp);

        // --- Observation Penilaian ABCDE (LOINC 51848-0, component A-E) dari penilaian_medis_igd ---
        ObjectNode oAbcde = buatObservationAbcde(t, patientRef, encounterRef, waktu);
        if (oAbcde != null) {
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_abcde", "abcde", oAbcde);
        }

        // --- Observation Triage Index (SNOMED 273887006): warna dari skala, derivedFrom ABCDE ---
        int skalaTriase = hitungSkalaTriase(noRawat);
        ObjectNode oTriage = buatObservationTriageIndex(skalaTriase, patientRef, t.namaPasien,
                encounterRef, waktu, ref.get("abcde"));
        if (oTriage != null) {
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_triageindex", "triageindex", oTriage);
        }

        // --- Keluhan Utama sebagai Observation (LOINC 10154-3 Chief complaint, valueString) ---
        // Tidak memakai Condition agar tak perlu code.coding (keluhan berupa teks bebas).
        if (!t.keluhanUtama.equals("")) {
            ObjectNode o = buatObservationTeks("10154-3", "Chief complaint", "survey",
                    t.keluhanUtama, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_condition", "condition", o);
        }

        // --- Observation Riwayat Penyakit Sekarang (LOINC 10164-2, valueString) ---
        if (!t.rps.equals("")) {
            ObjectNode o = buatObservationTeks("10164-2", "History of Present illness Narrative", "exam",
                    t.rps, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_rps", "rps", o);
        }

        // --- Observation Riwayat Perjalanan Perawatan / Hospital Course (LOINC 8648-8) dari RPD ---
        if (!t.rpd.equals("")) {
            ObjectNode o = buatObservationTeks("8648-8", "Hospital course Narrative", "exam",
                    t.rpd, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_rpd", "rpd", o);
        }

        // --- AllergyIntolerance: mapping keyword dari cache alergisatusehat.iyem
        //     (meniru SatuSehatKirimAllergyIntolerance). Cache juga memetakan "-"/"Tidak Ada" ->
        //     SNOMED 716186003 (no known allergy), jadi pasien tanpa alergi pun tetap terkirim. ---
        String[] mapAlergi = cariMappingAlergi(t.alergi);
        if (mapAlergi != null && !mapAlergi[0].equals("")) {
            ObjectNode oAllergy = buatAllergyIntolerance(mapAlergi, noRawat, patientRef, t.namaPasien,
                    encounterRef, waktu, t.idOrg);
            tambahEntryResource(entries, slotOrder, ref, idLama, "id_allergy", "allergy", oAllergy,
                    "AllergyIntolerance");
        }

        // --- Observation Pemeriksaan Laboratorium (LOINC 11502-2 Laboratory report) dari kolom lab ---
        if (!t.lab.equals("") && !t.lab.equals("-")) {
            ObjectNode o = buatObservationTeks("11502-2", "Laboratory report",
                    "laboratory", t.lab, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_lab", "lab", o);
        }

        // --- Composition (indeks dokumen triase) ---
        ObjectNode comp = buatComposition(noRawat, t, patientRef, encounterRef, waktu, ref);
        tambahEntryResource(entries, slotOrder, ref, idLama, "id_composition", "composition", comp, "Composition");

        // === Kirim Bundle ===
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Triase IGD : " + link);
        System.out.println("Request JSON Triase : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (°, ±, dash) rusak jadi "?"/"ï¿½" di server. Bytes UTF-8 = server baca benar.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Triase Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Triase OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Triase Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Triase : " + hasil);

        // === Petakan id hasil response ke tiap slot, lalu simpan ===
        JsonNode entryResponse = mapper.readTree(hasil).path("entry");
        Map<String,String> idBaru = new HashMap<>();
        for (int x = 0; x < slotOrder.size(); x++) {
            String idResource = "";
            if (entryResponse.isArray() && entryResponse.size() > x) {
                JsonNode respNode = entryResponse.get(x).path("response");
                idResource = extractId(respNode.path("location").asText());
                if (idResource.equals("")) idResource = respNode.path("resourceID").asText();
            }
            if (!idResource.equals("")) {
                idBaru.put(slotOrder.get(x), idResource);
            }
        }
        simpanTriase(noRawat, idBaru, idLama);
    }

    // ====================== BUILDER RESOURCE ======================

    /** Observation valueQuantity (UCUM) untuk angka, mis. nadi/suhu/spo2. */
    private ObjectNode buatObservationKuantitas(String loinc, String display, String kategori,
            double nilai, String unit, String ucum, String patientRef, String namaPasien,
            String encounterRef, String waktu) {
        ObjectNode o = dasarObservation(loinc, display, kategori, patientRef, namaPasien, encounterRef, waktu);
        ObjectNode value = o.putObject("valueQuantity");
        value.put("value", nilai);
        value.put("unit", unit);
        value.put("system", "http://unitsofmeasure.org");
        value.put("code", ucum);
        return o;
    }

    /** Observation Tekanan Darah: LOINC panel 85354-9 dengan komponen sistol & diastol. */
    private ObjectNode buatObservationTekananDarah(double sistol, double diastol,
            String patientRef, String namaPasien, String encounterRef, String waktu) {
        ObjectNode o = dasarObservation("85354-9", "Blood pressure panel with all children optional",
                "vital-signs", patientRef, namaPasien, encounterRef, waktu);
        ArrayNode component = o.putArray("component");
        tambahKomponenTD(component, "8480-6", "Systolic blood pressure", sistol);
        tambahKomponenTD(component, "8462-4", "Diastolic blood pressure", diastol);
        return o;
    }

    private void tambahKomponenTD(ArrayNode component, String loinc, String display, double nilai) {
        ObjectNode comp = component.addObject();
        ObjectNode coding = comp.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        ObjectNode value = comp.putObject("valueQuantity");
        value.put("value", nilai);
        value.put("unit", "mmHg");
        value.put("system", "http://unitsofmeasure.org");
        value.put("code", "mm[Hg]");
    }

    /** Observation valueString untuk data non-numerik (mode arrival, kategori, disposisi). */
    private ObjectNode buatObservationTeks(String loinc, String display, String kategori,
            String valueString, String patientRef, String namaPasien, String encounterRef, String waktu) {
        ObjectNode o = dasarObservation(loinc, display, kategori, patientRef, namaPasien, encounterRef, waktu);
        o.put("valueString", valueString);
        return o;
    }

    /** Observation valueCodeableConcept (SNOMED) untuk nilai terkode, mis. moda transportasi (74286-6). */
    private ObjectNode buatObservationKode(String loinc, String display, String kategori,
            String snomedCode, String snomedDisplay, String patientRef, String namaPasien,
            String encounterRef, String waktu) {
        ObjectNode o = dasarObservation(loinc, display, kategori, patientRef, namaPasien, encounterRef, waktu);
        ObjectNode vc = o.putObject("valueCodeableConcept").putArray("coding").addObject();
        vc.put("system", "http://snomed.info/sct");
        vc.put("code", snomedCode);
        vc.put("display", snomedDisplay);
        return o;
    }

    /** Peta enum data_triase_igd.alat_transportasi -> SNOMED [code, display] untuk Observation 74286-6
     *  "Transport mode to hospital". Kode DIVERIFIKASI diterima subset SNOMED SATUSEHAT (18 Juli 2026):
     *  Car 71783008, Land ambulance 698973009 (kode ambulans lain spt 465511002 DITOLAK). null = tak dikirim
     *  (moda '-'/kosong tetap tercermin di teks Observation 11459-5 "Mode of arrival"). */
    private String[] snomedTransport(String alat) {
        String v = nz(alat).trim();
        if (v.equalsIgnoreCase("Sendiri")) return new String[]{"71783008", "Car"};
        if (v.equalsIgnoreCase("AGD") || v.equalsIgnoreCase("Swasta")) return new String[]{"698973009", "Land ambulance"};
        return null;
    }

    /**
     * Observation Penilaian ABCDE (LOINC 51848-0) dengan component A-E (valueString),
     * disusun dari penilaian_medis_igd. Mengembalikan null bila data inti (B/C/D) kosong.
     */
    private ObjectNode buatObservationAbcde(TriaseData t, String patientRef, String encounterRef, String waktu) {
        // A - Airway: tak ada field khusus -> turunkan dari kesadaran.
        String airway = (t.kesadaran.equalsIgnoreCase("Koma") || t.kesadaran.equalsIgnoreCase("Sopor"))
                ? "Perlu proteksi jalan napas" : "Bebas";
        // B - Breathing: RR + SpO2 + thoraks.
        String breathing = "";
        if (!t.medRr.equals("")) breathing = "RR " + t.medRr + " x/menit";
        if (!t.medSpo.equals("")) breathing = tambahKoma(breathing, "SpO2 " + t.medSpo + "%");
        if (!kosongPemeriksaan(t.thoraks)) breathing = tambahKoma(breathing, "Thoraks " + t.thoraks);
        // C - Circulation: nadi + TD + ekstremitas (akral).
        String circulation = "";
        if (!t.medNadi.equals("")) circulation = "Nadi " + t.medNadi + " x/menit";
        if (!t.medTd.equals("")) circulation = tambahKoma(circulation, "TD " + t.medTd + " mmHg");
        if (!kosongPemeriksaan(t.ekstremitas)) circulation = tambahKoma(circulation, "Ekstremitas " + t.ekstremitas);
        // D - Disability: kesadaran + GCS.
        String disability = "";
        if (!t.kesadaran.equals("")) disability = "Kesadaran " + t.kesadaran;
        if (!t.gcs.equals("")) disability = tambahKoma(disability, "GCS " + t.gcs);
        // E - Exposure: ket lokalis / ket fisik / default.
        String exposure = pilihTeks(t.ketLokalis, t.ketFisik);
        if (exposure.equals("")) exposure = "Tidak ada temuan tambahan signifikan";

        if (breathing.equals("") && circulation.equals("") && disability.equals("")) {
            return null;   // tidak ada data penilaian medis IGD -> ABCDE dilewati
        }

        ObjectNode o = dasarObservation("51848-0", "Assessments", "exam", patientRef, t.namaPasien, encounterRef, waktu);
        ((ObjectNode) o.get("code")).put("text", "Penilaian ABCDE (Triase IGD)");
        ArrayNode comp = o.putArray("component");
        tambahKomponenAbcde(comp, "69046-1", "Airway status", airway);
        tambahKomponenAbcde(comp, "103776-1", "Breathing", breathing);
        tambahKomponenAbcde(comp, "28256-6", "Circulation status [OMAHA]", circulation);
        tambahKomponenAbcde(comp, "101720-1", "Disability status", disability);
        tambahKomponenAbcde(comp, "104964-2", "Exposure description Narrative", exposure);
        return o;
    }

    /** Anggap alergi "kosong" bila blank/tanda negasi (-, tidak ada, disangkal, dsb). */
    private boolean alergiKosong(String a) {
        if (a == null) return true;
        String s = a.trim().toLowerCase();
        return s.equals("") || s.equals("-") || s.equals("(-)") || s.equals("nkda") || s.equals("none")
                || s.contains("tidak ada") || s.contains("tdk ada") || s.contains("tak ada")
                || s.contains("disangkal") || s.contains("tidak diketahui");
    }

    /**
     * Cari mapping alergi dari cache ./cache/alergisatusehat.iyem (sama seperti SatuSehatKirimAllergyIntolerance):
     * cocokkan keyword terhadap teks alergi. Return {category, coding_system, coding_code, coding_display, text}
     * atau null bila tidak ada yang cocok / cache tidak tersedia.
     */
    private String[] cariMappingAlergi(String alergiText) {
        if (alergiText == null) return null;
        String dicari = alergiText.replaceAll("(\\r\\n|\\r|\\n)", "").replaceAll("\\t", "").toLowerCase();
        java.io.FileReader fr = null;
        try {
            fr = new java.io.FileReader("./cache/alergisatusehat.iyem");
            JsonNode root = mapper.readTree(fr);
            JsonNode arr = root.path("alergi");
            if (arr.isArray()) {
                for (JsonNode list : arr) {
                    String keyword = list.path("keyword").asText();
                    if (!keyword.equals("") && dicari.contains(keyword.toLowerCase())) {
                        return new String[]{
                            list.path("category").asText(),
                            list.path("coding_system").asText(),
                            list.path("coding_code").asText(),
                            list.path("coding_display").asText(),
                            list.path("text").asText()
                        };
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi cariMappingAlergi : " + e);
        } finally {
            if (fr != null) try { fr.close(); } catch (Exception e) {}
        }
        return null;
    }

    /** Builder AllergyIntolerance dari mapping {category, coding_system, coding_code, coding_display, text}. */
    private ObjectNode buatAllergyIntolerance(String[] m, String noRawat, String patientRef,
            String namaPasien, String encounterRef, String waktu, String idOrg) {
        ObjectNode a = mapper.createObjectNode();
        a.put("resourceType", "AllergyIntolerance");
        ObjectNode iden = a.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/allergy/" + idOrg);
        iden.put("value", noRawat);
        ObjectNode clinical = a.putObject("clinicalStatus").putArray("coding").addObject();
        clinical.put("system", "http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical");
        clinical.put("code", "active");
        clinical.put("display", "Active");
        ObjectNode verify = a.putObject("verificationStatus").putArray("coding").addObject();
        verify.put("system", "http://terminology.hl7.org/CodeSystem/allergyintolerance-verification");
        verify.put("code", "confirmed");
        verify.put("display", "Confirmed");
        a.putArray("category").add(m[0]);
        ObjectNode code = a.putObject("code");
        ObjectNode coding = code.putArray("coding").addObject();
        coding.put("system", m[1]);
        coding.put("code", m[2]);
        coding.put("display", m[3]);
        code.put("text", m[4]);
        ObjectNode patient = a.putObject("patient");
        patient.put("reference", patientRef);
        patient.put("display", namaPasien);
        a.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) {
            a.put("recordedDate", waktu);
        }
        if (!performerRef.equals("")) {
            a.putObject("recorder").put("reference", performerRef);
        }
        return a;
    }

    private void tambahKomponenAbcde(ArrayNode comp, String loinc, String display, String value) {
        if (value == null || value.equals("")) return;
        ObjectNode item = comp.addObject();
        ObjectNode coding = item.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        item.put("valueString", value);
    }

    private String tambahKoma(String base, String tambahan) {
        if (tambahan == null || tambahan.equals("")) return base;
        return base.equals("") ? tambahan : base + ", " + tambahan;
    }

    /** Enum pemeriksaan ('Normal'/'Abnormal'/'Tidak Diperiksa') dianggap kosong bila blank/Tidak Diperiksa. */
    private boolean kosongPemeriksaan(String v) {
        return v == null || v.equals("") || v.equalsIgnoreCase("Tidak Diperiksa");
    }

    private String pilihTeks(String... vals) {
        for (String v : vals) {
            if (v != null && !v.trim().equals("")) return v.trim();
        }
        return "";
    }

    /**
     * Tentukan skala triase (1=paling gawat .. 5=paling ringan) dari data_triase_igddetail_skala1..5:
     * skala dengan nomor TERKECIL yang punya baris untuk no_rawat = level pasien. 0 bila tak ada.
     */
    private int hitungSkalaTriase(String noRawat) {
        for (int n = 1; n <= 5; n++) {
            try {
                PreparedStatement p = koneksi.prepareStatement(
                        "select count(*) from data_triase_igddetail_skala" + n + " where no_rawat=?");
                p.setString(1, noRawat);
                ResultSet r = p.executeQuery();
                int c = 0;
                if (r.next()) c = r.getInt(1);
                r.close();
                p.close();
                if (c > 0) return n;
            } catch (Exception e) {
                System.out.println("Notifikasi hitungSkalaTriase : " + e);
            }
        }
        return 0;
    }

    /** Map skala -> {warna(text), SNOMED code, SNOMED display}. null bila skala tidak diketahui. */
    private String[] mapWarnaTriase(int skala) {
        switch (skala) {
            case 1:
            case 2: return new String[]{"Merah", "371240000", "Red"};
            case 3: return new String[]{"Kuning", "371241001", "Yellow"};
            case 4:
            case 5: return new String[]{"Hijau", "371244009", "Green"};
            default: return null;
        }
    }

    /**
     * Observation Triage Index (SNOMED 273887006) dengan valueCodeableConcept warna triase
     * (Merah/Kuning/Hijau) hasil skala, dan derivedFrom ke Observation ABCDE bila ada.
     */
    private ObjectNode buatObservationTriageIndex(int skala, String patientRef, String namaPasien,
            String encounterRef, String waktu, String abcdeRef) {
        String[] w = mapWarnaTriase(skala);
        if (w == null) return null;
        ObjectNode o = mapper.createObjectNode();
        o.put("resourceType", "Observation");
        o.put("status", "final");
        ObjectNode catCoding = o.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        catCoding.put("code", "exam");
        catCoding.put("display", "Exam");
        ObjectNode codeCoding = o.putObject("code").putArray("coding").addObject();
        codeCoding.put("system", "http://snomed.info/sct");
        codeCoding.put("code", "273887006");
        codeCoding.put("display", "Triage index (assessment scale)");
        ObjectNode subject = o.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        o.putObject("encounter").put("reference", encounterRef);
        if (!performerRef.equals("")) {
            o.putArray("performer").addObject().put("reference", performerRef);
        }
        if (!waktu.equals("")) {
            o.put("effectiveDateTime", waktu);
        }
        ObjectNode value = o.putObject("valueCodeableConcept");
        ObjectNode vCoding = value.putArray("coding").addObject();
        vCoding.put("system", "http://snomed.info/sct");
        vCoding.put("code", w[1]);
        vCoding.put("display", w[2]);
        value.put("text", w[0]);
        if (abcdeRef != null && !abcdeRef.equals("")) {
            o.putArray("derivedFrom").addObject().put("reference", abcdeRef);
        }
        return o;
    }

    /** Kerangka Observation: status, category, code LOINC, subject, encounter, effectiveDateTime. */
    private ObjectNode dasarObservation(String loinc, String display, String kategori,
            String patientRef, String namaPasien, String encounterRef, String waktu) {
        ObjectNode o = mapper.createObjectNode();
        o.put("resourceType", "Observation");
        o.put("status", "final");
        ObjectNode catCoding = o.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        catCoding.put("code", kategori);
        catCoding.put("display", kategori.equals("vital-signs") ? "Vital Signs"
                : (kategori.equals("exam") ? "Exam"
                : (kategori.equals("laboratory") ? "Laboratory" : "Survey")));
        ObjectNode coding = o.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        ObjectNode subject = o.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        o.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) {
            o.put("effectiveDateTime", waktu);
        }
        // performer WAJIB (RuleNumber 10383): Practitioner pelaksana triase.
        if (!performerRef.equals("")) {
            o.putArray("performer").addObject().put("reference", performerRef);
        }
        return o;
    }

    /** Condition keluhan utama (code.text, tanpa coding karena keluhan berupa teks bebas). */
    private ObjectNode buatConditionKeluhan(String keluhan, String patientRef, String namaPasien,
            String encounterRef, String waktu) {
        ObjectNode c = mapper.createObjectNode();
        c.put("resourceType", "Condition");
        ObjectNode clinical = c.putObject("clinicalStatus").putArray("coding").addObject();
        clinical.put("system", "http://terminology.hl7.org/CodeSystem/condition-clinical");
        clinical.put("code", "active");
        clinical.put("display", "Active");
        ObjectNode catCoding = c.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/condition-category");
        catCoding.put("code", "encounter-diagnosis");
        catCoding.put("display", "Encounter Diagnosis");
        c.putObject("code").put("text", keluhan);
        ObjectNode subject = c.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        c.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) {
            c.put("recordedDate", waktu);
        }
        return c;
    }

    /** Composition Triase IGD (LOINC 75500-9) yang mengindeks resource lain via section.entry. */
    private ObjectNode buatComposition(String noRawat, TriaseData t, String patientRef,
            String encounterRef, String waktu, Map<String,String> ref) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + t.idOrg);
        iden.put("value", "TRIASE-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "75500-9");
        typeCoding.put("display", "Triage note");
        comp.putObject("subject").put("reference", patientRef);
        comp.putObject("encounter").put("reference", encounterRef);
        ObjectNode author = comp.putArray("author").addObject();
        author.put("reference", "Practitioner/" + t.idDokter);
        author.put("display", t.namaDokter);
        if (!waktu.equals("")) {
            comp.put("date", waktu);
        }
        comp.put("title", "Dokumen Triase IGD");
        // Narrative (text.div) WAJIB agar viewer (mis. INA-CBG) tidak crash membaca text.div.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Dokumen Triase IGD - "
                + escapeXml(t.namaPasien) + "</div>");

        ArrayNode section = comp.putArray("section");
        // Identitas & Kedatangan
        String narasiDatang = gabung(t.caraMasuk, t.alatTransportasi, t.alasanKedatangan, t.keteranganKedatangan);
        ObjectNode secKedatangan = buatSection(section, "Identitas & Kedatangan", "11459-5", "Mode of arrival",
                narasiDatang);
        ArrayNode eKedatangan = secKedatangan.putArray("entry");
        eKedatangan.addObject().put("reference", patientRef);
        eKedatangan.addObject().put("reference", encounterRef);
        tambahEntrySection(eKedatangan, ref, "modearrival");
        // Keluhan Utama
        if (ref.containsKey("condition")) {
            ObjectNode sec = buatSection(section, "Keluhan Utama", "10154-3", "Chief complaint", t.keluhanUtama);
            tambahEntrySection(sec.putArray("entry"), ref, "condition");
        }
        // Riwayat Penyakit Sekarang (Present Illness) — section terpisah, kode 10164-2.
        if (ref.containsKey("rps")) {
            ObjectNode sec = buatSection(section, "Riwayat Penyakit Sekarang", "10164-2",
                    "History of Present illness Narrative", t.rps);
            tambahEntrySection(sec.putArray("entry"), ref, "rps");
        }
        // Riwayat Perjalanan Perawatan (Hospital Course) — kode 8648-8.
        if (ref.containsKey("rpd")) {
            ObjectNode sec = buatSection(section, "Riwayat Perjalanan Perawatan", "8648-8",
                    "Hospital course Narrative", t.rpd);
            tambahEntrySection(sec.putArray("entry"), ref, "rpd");
        }
        // Alergi (selalu tampil; entry AllergyIntolerance bila ada alergi nyata)
        String narasiAlergi = alergiKosong(t.alergi) ? "Tidak ada alergi" : t.alergi;
        ObjectNode secAlergi = buatSection(section, "Alergi", "48765-2",
                "Allergies and adverse reactions Document", narasiAlergi);
        if (ref.containsKey("allergy")) {
            tambahEntrySection(secAlergi.putArray("entry"), ref, "allergy");
        }
        // Penilaian ABCDE
        if (ref.containsKey("abcde")) {
            ObjectNode sec = buatSection(section, "Penilaian ABCDE", "51848-0", "Assessments", narasiAbcde(t));
            tambahEntrySection(sec.putArray("entry"), ref, "abcde");
        }
        // Tanda Vital
        if (adaSalahSatu(ref, "td", "nadi", "resp", "suhu", "spo2")) {
            String vit = "TD " + t.tekananDarah + ", Nadi " + t.nadi + " x/mnt, RR " + t.pernapasan
                    + " x/mnt, Suhu " + t.suhu + " C, SpO2 " + t.spo2 + " %";
            ObjectNode sec = buatSection(section, "Tanda Vital", "8716-3", "Vital signs", vit);
            ArrayNode e = sec.putArray("entry");
            tambahEntrySection(e, ref, "td");
            tambahEntrySection(e, ref, "nadi");
            tambahEntrySection(e, ref, "resp");
            tambahEntrySection(e, ref, "suhu");
            tambahEntrySection(e, ref, "spo2");
        }
        // Pemeriksaan Laboratorium (LOINC 11502-2, kode yang dikenali viewer untuk section lab)
        if (ref.containsKey("lab")) {
            ObjectNode sec = buatSection(section, "Pemeriksaan Laboratorium", "11502-2",
                    "Laboratory report", t.lab);
            tambahEntrySection(sec.putArray("entry"), ref, "lab");
        }
        // Nyeri & Triase
        if (adaSalahSatu(ref, "nyeri", "kategori", "triageindex")) {
            String[] w = mapWarnaTriase(hitungSkalaTriase(noRawat));
            String narasiTriase = "Skala nyeri: " + (t.nyeri.equals("") ? "-" : t.nyeri)
                    + "; Kategori: " + (t.kategori.equals("") ? "-" : t.kategori)
                    + (w == null ? "" : "; Triase: " + w[0]);
            ObjectNode sec = buatSection(section, "Tingkat Nyeri dan Kategori Triase", "11283-9",
                    "Acuity assessment", narasiTriase);
            ArrayNode e = sec.putArray("entry");
            tambahEntrySection(e, ref, "nyeri");
            tambahEntrySection(e, ref, "kategori");
            tambahEntrySection(e, ref, "triageindex");
        }
        // Disposisi
        String disp = t.statusLanjut.equalsIgnoreCase("Ranap") ? "Rawat Inap" : "Rawat Jalan / Pulang";
        ObjectNode secDisp = buatSection(section, "Rencana / Disposisi IGD", "11302-7", "ED disposition", disp);
        ArrayNode eDisp = secDisp.putArray("entry");
        tambahEntrySection(eDisp, ref, "disposition");
        eDisp.addObject().put("reference", encounterRef);
        return comp;
    }

    private ObjectNode buatSection(ArrayNode section, String title, String loinc, String display, String narasi) {
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
        coding.put("system", "http://loinc.org");
        coding.put("code", loinc);
        coding.put("display", display);
        // text.div WAJIB tiap section (viewer INA-CBG membacanya); diisi NILAI/konten, bukan judul.
        String isi = (narasi == null || narasi.trim().equals("")) ? "-" : narasi.trim();
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(isi) + "</div>");
        return sec;
    }

    /** Ringkasan narasi ABCDE untuk text.div section. */
    private String narasiAbcde(TriaseData t) {
        StringBuilder sb = new StringBuilder();
        String airway = (t.kesadaran.equalsIgnoreCase("Koma") || t.kesadaran.equalsIgnoreCase("Sopor"))
                ? "Perlu proteksi" : "Bebas";
        sb.append("A: ").append(airway);
        if (!t.medRr.equals("")) sb.append("; B: RR ").append(t.medRr).append(" x/mnt");
        if (!t.medNadi.equals("")) sb.append("; C: Nadi ").append(t.medNadi).append(" x/mnt");
        if (!t.medTd.equals("")) sb.append(", TD ").append(t.medTd);
        if (!t.kesadaran.equals("")) sb.append("; D: ").append(t.kesadaran);
        if (!t.gcs.equals("")) sb.append(" GCS ").append(t.gcs);
        return sb.toString();
    }

    /** Escape karakter XML untuk konten di dalam narrative div (&, <, >). */
    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void tambahEntrySection(ArrayNode entry, Map<String,String> ref, String slot) {
        if (ref.containsKey(slot)) {
            entry.addObject().put("reference", ref.get(slot));
        }
    }

    private boolean adaSalahSatu(Map<String,String> ref, String... slots) {
        for (String s : slots) {
            if (ref.containsKey(s)) return true;
        }
        return false;
    }

    // ====================== ENTRY BUNDLE ======================

    /** Tambah Observation ke bundle (PUT bila id lama ada di satu_sehat_triase_igd, else POST). */
    private void tambahObs(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            Map<String,String> idLama, String kolomId, String slot, ObjectNode obs) {
        tambahEntryResource(entries, slotOrder, ref, idLama, kolomId, slot, obs, "Observation");
    }

    /** Tambah resource generik ke bundle dengan kontrol PUT/POST + catat fullUrl untuk referensi. */
    private void tambahEntryResource(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            Map<String,String> idLama, String kolomId, String slot, ObjectNode resource, String resourceType) {
        String idExisting = idLama.get(kolomId);
        boolean adaId = idExisting != null && !idExisting.equals("");
        String fullUrl = adaId ? (resourceType + "/" + idExisting) : ("urn:uuid:" + UUID.randomUUID().toString());

        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", fullUrl);
        if (adaId) {
            resource.put("id", idExisting);
        }
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        if (adaId) {
            request.put("method", "PUT");
            request.put("url", resourceType + "/" + idExisting);
        } else {
            request.put("method", "POST");
            request.put("url", resourceType);
        }
        slotOrder.add(slot);
        ref.put(slot, fullUrl);
    }

    // ====================== QUERY & SIMPAN ======================

    private TriaseData ambilData(String noRawat) {
        TriaseData t = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select dt.tgl_kunjungan, dt.cara_masuk, dt.alat_transportasi, dt.alasan_kedatangan, "
                    + "dt.keterangan_kedatangan, ifnull(mk.macam_kasus,'') as macam_kasus, dt.tekanan_darah, "
                    + "dt.nadi, dt.pernapasan, dt.suhu, dt.saturasi_o2, dt.nyeri, "
                    + "rp.status_lanjut, p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "peg.no_ktp as ktp_dokter, peg.nama as nm_dokter, "
                    + "ifnull(pmi.keluhan_utama,'') as keluhan_utama, "
                    + "ifnull(pmi.gcs,'') as med_gcs, ifnull(pmi.kesadaran,'') as med_kesadaran, "
                    + "ifnull(pmi.td,'') as med_td, ifnull(pmi.nadi,'') as med_nadi, ifnull(pmi.rr,'') as med_rr, "
                    + "ifnull(pmi.spo,'') as med_spo, ifnull(pmi.thoraks,'') as med_thoraks, "
                    + "ifnull(pmi.ekstremitas,'') as med_ekstremitas, ifnull(pmi.ket_lokalis,'') as med_ketlokalis, "
                    + "ifnull(pmi.ket_fisik,'') as med_ketfisik, ifnull(pmi.rps,'') as med_rps, "
                    + "ifnull(pmi.rpd,'') as med_rpd, ifnull(pmi.alergi,'') as med_alergi, "
                    + "ifnull(pmi.lab,'') as med_lab "
                    + "from data_triase_igd dt "
                    + "inner join reg_periksa rp on rp.no_rawat=dt.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "inner join pegawai peg on peg.nik=rp.kd_dokter "
                    + "left join master_triase_macam_kasus mk on mk.kode_kasus=dt.kode_kasus "
                    + "left join penilaian_medis_igd pmi on pmi.no_rawat=dt.no_rawat "
                    + "where dt.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                t = new TriaseData();
                t.noRawat = noRawat;
                t.waktu = formatWaktu(r.getString("tgl_kunjungan"));
                t.caraMasuk = nz(r.getString("cara_masuk"));
                t.alatTransportasi = nz(r.getString("alat_transportasi"));
                t.alasanKedatangan = nz(r.getString("alasan_kedatangan"));
                t.keteranganKedatangan = nz(r.getString("keterangan_kedatangan"));
                t.kategori = nz(r.getString("macam_kasus"));
                t.tekananDarah = nz(r.getString("tekanan_darah"));
                t.nadi = nz(r.getString("nadi"));
                t.pernapasan = nz(r.getString("pernapasan"));
                t.suhu = nz(r.getString("suhu"));
                t.spo2 = nz(r.getString("saturasi_o2"));
                t.nyeri = nz(r.getString("nyeri"));
                t.statusLanjut = nz(r.getString("status_lanjut"));
                t.namaPasien = nz(r.getString("nm_pasien"));
                t.namaDokter = nz(r.getString("nm_dokter"));
                t.keluhanUtama = bersihkan(r.getString("keluhan_utama"));
                t.gcs = nz(r.getString("med_gcs"));
                t.kesadaran = nz(r.getString("med_kesadaran"));
                t.medTd = nz(r.getString("med_td"));
                t.medNadi = nz(r.getString("med_nadi"));
                t.medRr = nz(r.getString("med_rr"));
                t.medSpo = nz(r.getString("med_spo"));
                t.thoraks = nz(r.getString("med_thoraks"));
                t.ekstremitas = nz(r.getString("med_ekstremitas"));
                t.ketLokalis = bersihkan(r.getString("med_ketlokalis"));
                t.ketFisik = bersihkan(r.getString("med_ketfisik"));
                t.rps = bersihkan(r.getString("med_rps"));
                t.rpd = bersihkan(r.getString("med_rpd"));
                t.alergi = bersihkan(r.getString("med_alergi"));
                t.lab = bersihkan(r.getString("med_lab"));
                t.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                t.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                t.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Triase ambilData : " + e);
        }
        return t;
    }

    /** Ambil id resource lama dari satu_sehat_triase_igd (untuk PUT). */
    private Map<String,String> ambilIdLama(String noRawat) {
        Map<String,String> m = new HashMap<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select id_obs_modearrival, id_obs_transport, id_obs_kategori, id_obs_td, id_obs_nadi, id_obs_resp, "
                    + "id_obs_suhu, id_obs_spo2, id_obs_nyeri, id_obs_disposition, id_obs_abcde, id_obs_triageindex, id_obs_rps, id_obs_rpd, id_allergy, id_obs_lab, id_condition, id_composition "
                    + "from satu_sehat_triase_igd where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                String[] kolom = {"id_obs_modearrival","id_obs_transport","id_obs_kategori","id_obs_td","id_obs_nadi","id_obs_resp",
                        "id_obs_suhu","id_obs_spo2","id_obs_nyeri","id_obs_disposition","id_obs_abcde","id_obs_triageindex","id_obs_rps","id_obs_rpd","id_allergy","id_obs_lab","id_condition","id_composition"};
                for (String k : kolom) {
                    m.put(k, nz(r.getString(k)));
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Triase ambilIdLama : " + e);
        }
        return m;
    }

    /** Upsert id resource hasil response ke satu_sehat_triase_igd (pertahankan id lama bila slot tak terkirim). */
    private void simpanTriase(String noRawat, Map<String,String> idBaru, Map<String,String> idLama) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "insert into satu_sehat_triase_igd "
                    + "(no_rawat, id_obs_modearrival, id_obs_transport, id_obs_kategori, id_obs_td, id_obs_nadi, id_obs_resp, "
                    + "id_obs_suhu, id_obs_spo2, id_obs_nyeri, id_obs_disposition, id_obs_abcde, id_obs_triageindex, id_obs_rps, id_obs_rpd, id_allergy, id_obs_lab, id_condition, id_composition) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                    + "on duplicate key update "
                    + "id_obs_modearrival=values(id_obs_modearrival), id_obs_transport=values(id_obs_transport), id_obs_kategori=values(id_obs_kategori), "
                    + "id_obs_td=values(id_obs_td), id_obs_nadi=values(id_obs_nadi), id_obs_resp=values(id_obs_resp), "
                    + "id_obs_suhu=values(id_obs_suhu), id_obs_spo2=values(id_obs_spo2), id_obs_nyeri=values(id_obs_nyeri), "
                    + "id_obs_disposition=values(id_obs_disposition), id_obs_abcde=values(id_obs_abcde), "
                    + "id_obs_triageindex=values(id_obs_triageindex), id_obs_rps=values(id_obs_rps), "
                    + "id_obs_rpd=values(id_obs_rpd), id_allergy=values(id_allergy), id_obs_lab=values(id_obs_lab), "
                    + "id_condition=values(id_condition), id_composition=values(id_composition)");
            p.setString(1, noRawat);
            p.setString(2, pilih(idBaru, "modearrival", idLama, "id_obs_modearrival"));
            p.setString(3, pilih(idBaru, "transport", idLama, "id_obs_transport"));
            p.setString(4, pilih(idBaru, "kategori", idLama, "id_obs_kategori"));
            p.setString(5, pilih(idBaru, "td", idLama, "id_obs_td"));
            p.setString(6, pilih(idBaru, "nadi", idLama, "id_obs_nadi"));
            p.setString(7, pilih(idBaru, "resp", idLama, "id_obs_resp"));
            p.setString(8, pilih(idBaru, "suhu", idLama, "id_obs_suhu"));
            p.setString(9, pilih(idBaru, "spo2", idLama, "id_obs_spo2"));
            p.setString(10, pilih(idBaru, "nyeri", idLama, "id_obs_nyeri"));
            p.setString(11, pilih(idBaru, "disposition", idLama, "id_obs_disposition"));
            p.setString(12, pilih(idBaru, "abcde", idLama, "id_obs_abcde"));
            p.setString(13, pilih(idBaru, "triageindex", idLama, "id_obs_triageindex"));
            p.setString(14, pilih(idBaru, "rps", idLama, "id_obs_rps"));
            p.setString(15, pilih(idBaru, "rpd", idLama, "id_obs_rpd"));
            p.setString(16, pilih(idBaru, "allergy", idLama, "id_allergy"));
            p.setString(17, pilih(idBaru, "lab", idLama, "id_obs_lab"));
            p.setString(18, pilih(idBaru, "condition", idLama, "id_condition"));
            p.setString(19, pilih(idBaru, "composition", idLama, "id_composition"));
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Triase simpanTriase : " + e);
        }
    }

    /** Pakai id baru bila ada; kalau slot ini tidak terkirim, pertahankan id lama. */
    private String pilih(Map<String,String> idBaru, String slot, Map<String,String> idLama, String kolom) {
        String baru = idBaru.get(slot);
        if (baru != null && !baru.equals("")) return baru;
        String lama = idLama.get(kolom);
        return (lama == null) ? "" : lama;
    }

    // ====================== UTIL ======================

    private String extractId(String location) {
        if (location == null || location.equals("")) return "";
        String loc = location;
        int hist = loc.indexOf("/_history");
        if (hist >= 0) loc = loc.substring(0, hist);
        int slash = loc.lastIndexOf("/");
        return slash >= 0 ? loc.substring(slash + 1) : loc;
    }

    private String formatWaktu(String dt) {
        if (dt == null || dt.trim().equals("")) return "";
        String t = dt.trim();
        if (t.contains(" ")) t = t.replace(" ", "T");
        if (t.length() == 10) t = t + "T00:00:00";
        return t + "+07:00";
    }

    private Double parseAngka(String s) {
        if (s == null) return null;
        String t = s.replace(",", ".").replaceAll("[^0-9.\\-]", "").trim();
        if (t.equals("") || t.equals(".") || t.equals("-")) return null;
        try {
            return Double.valueOf(t);
        } catch (Exception e) {
            return null;
        }
    }

    /** Parse "134/78" -> [134.0, 78.0]; null bila tidak lengkap. */
    private Double[] parseTD(String td) {
        if (td == null || !td.contains("/")) return null;
        String[] bagian = td.split("/");
        if (bagian.length < 2) return null;
        Double sis = parseAngka(bagian[0]);
        Double dia = parseAngka(bagian[1]);
        if (sis == null || dia == null) return null;
        return new Double[]{sis, dia};
    }

    private String gabung(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String v = nz(p).trim();
            if (!v.equals("") && !v.equals("-")) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(v);
            }
        }
        return sb.toString();
    }

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
