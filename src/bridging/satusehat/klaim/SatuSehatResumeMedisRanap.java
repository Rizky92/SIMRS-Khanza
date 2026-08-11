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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Pengiriman "Resume Medis Rawat Inap" (FHIR Composition - LOINC 34105-7,
 * Hospital Discharge summary) ke SATUSEHAT.
 *
 * Sumber data: tabel resume_pasien_ranap (mapping nyaris 1:1 ke section dokumen).
 * Composition adalah indeks: section.entry hanya MEREFERENSI resource lain. Maka satu
 * Bundle transaction berisi:
 *   - Observation Keluhan Utama, Keluhan Penyerta, RPS, Riwayat Pengobatan
 *   - Observation Pemeriksaan Fisik, Laboratorium/Penunjang
 *   - Observation Diagnosis Awal & Diagnosis Akhir, Perjalanan Kunjungan (Hospital Course)
 *   - AllergyIntolerance (mapping cache alergisatusehat.iyem)
 *   - Composition yang mengindeks semuanya + section naratif (Tindakan, Kondisi Pulang, RTL)
 *
 * Id tiap resource disimpan ke tabel satu_sehat_resume_ranap; pengiriman ulang memakai
 * PUT (update) sehingga tidak menduplikasi resource di server.
 */
public class SatuSehatResumeMedisRanap {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private String performerRef = "";   // Practitioner/{id} untuk Observation.performer (wajib)
    private String curNoRawat = "";     // konteks kirim() untuk identifier resource
    private String curIdOrg = "";

    public SatuSehatResumeMedisRanap() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Resume Ranap : " + e);
        }
        pastikanTabel();
    }

    /** Penampung data resume_pasien_ranap + header kunjungan. */
    private static class ResumeData {
        String noRawat="", waktu="";
        String keluhanUtama="", alasan="", diagnosaAwal="", pemeriksaanFisik="";
        String jalannyaPenyakit="", pemeriksaanPenunjang="", hasilLaborat="", labBelum="";
        /** Sumber Hospital Course (8648-8): penilaian_medis_igd.keluhan_utama. Kolom rps di tabel
         *  penilaian nyaris selalu berisi "-", sedangkan narasi perjalanan pasien ditulis petugas
         *  di keluhan_utama IGD. Terisi pada 4.548 dari 4.692 resume ranap (96,9%). */
        String keluhanIgd="";
        String tindakanDanOperasi="", obatDiRs="", obatPulang="";
        String diagnosaUtama="", kdDiagnosaUtama="";
        String diagnosaSekunder="", diagnosaSekunder2="", diagnosaSekunder3="", diagnosaSekunder4="";
        String prosedurUtama="", prosedurSekunder="", prosedurSekunder2="", prosedurSekunder3="";
        String kdProsedurUtama="", kdProsedurSekunder="", kdProsedurSekunder2="", kdProsedurSekunder3="";
        String alergi="", diet="", edukasi="";
        String caraKeluar="", ketKeluar="", keadaan="", ketKeadaan="";
        String dilanjutkan="", ketDilanjutkan="", kontrol="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    /** Penampung satu baris obat pemberian rawat inap (untuk MedicationRequest ber-KFA). */
    private static class ObatData {
        String kodeBrng="", idMedication="", obatDisplay="", aturanPakai="", jml="";
        String noResep="", tglPeresepan="", jamPeresepan="";
        String routeCode="", routeSystem="", routeDisplay="";
        String denominatorCode="", denominatorSystem="";
    }

    /**
     * Bangun & kirim Bundle Resume Medis Rawat Inap untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan rawat inap
     * @param idEncounter id Encounter SATUSEHAT (WAJIB sudah ada — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Resume Medis Rawat Inap (Composition + Observation/Procedure) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        ResumeData t = ambilData(noRawat);
        if (t == null) return null;
        if (t.idPasien.equals("")) return null;

        String patientRef   = "Patient/" + t.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu        = t.waktu;
        performerRef = t.idDokter.equals("") ? "" : ("Practitioner/" + t.idDokter);
        curNoRawat = noRawat;
        curIdOrg = t.idOrg;

        Map<String,String> idLama = ambilIdLama(noRawat);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        List<String> slotOrder = new ArrayList<>();
        Map<String,String> ref = new HashMap<>();
        List<String> medRefs = new ArrayList<>();
        Map<String,String[]> medKeyBySlot = new HashMap<>();

        if (!t.keluhanUtama.equals("")) {
            ObjectNode o = buatObservationTeks("10154-3", "Chief complaint", "survey",
                    t.keluhanUtama, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_keluhan", "keluhan", o);
        }
        String penyerta = pilihTeks(t.alasan, t.diagnosaAwal);
        if (!penyerta.equals("")) {
            ObjectNode o = buatObservationTeks("11450-4", "Problem list - Reported", "survey",
                    penyerta, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_penyerta", "penyerta", o);
        }
        if (!t.jalannyaPenyakit.equals("") && !t.jalannyaPenyakit.equals("-")) {
            ObjectNode o = buatObservationTeks("10164-2", "History of Present illness Narrative", "exam",
                    t.jalannyaPenyakit, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_rps", "rps", o);
        }
        if (!t.obatDiRs.equals("")) {
            ObjectNode o = buatObservationTeks("10160-0", "History of Medication use Narrative", "survey",
                    t.obatDiRs, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_medreq_rs", "medrs", o);
        }
        if (!t.obatPulang.equals("")) {
            ObjectNode o = buatObservationTeks("10183-2", "Hospital discharge medications Narrative", "survey",
                    t.obatPulang, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_medreq_pulang", "medpulang", o);
        }
        List<String> procRefs = new ArrayList<>();
        int procIdx = 0;
        for (String[] pr : ambilProsedurPasien(noRawat)) {
            ObjectNode oProc = buatProcedure(pr[0], pr[1], t, patientRef, t.namaPasien, encounterRef, waktu);
            if (oProc == null) continue;
            String procSys = "http://sys-ids.kemkes.go.id/procedure/" + t.idOrg;
            String idenVal = noRawat + "-" + pr[0].replaceAll("[^A-Za-z0-9]", "");
            String idProc = cariIdServerByIdentifier("Procedure", procSys, idenVal);
            String slot = "proc" + (procIdx++);
            tambahResourceDenganId(entries, slotOrder, ref, idProc, slot, oProc, "Procedure",
                    "identifier=" + procSys + "|" + idenVal);
            procRefs.add(ref.get(slot));
        }
        String edukasiRef = "";
        ObjectNode oEdukasi = buatProcedureEdukasi(t.edukasi, t, patientRef, t.namaPasien, encounterRef, waktu);
        if (oEdukasi != null) {
            String eduSys = "http://sys-ids.kemkes.go.id/procedure/" + t.idOrg;
            String eduVal = noRawat + "-EDUKASI";
            String idEdu = cariIdServerByIdentifier("Procedure", eduSys, eduVal);
            tambahResourceDenganId(entries, slotOrder, ref, idEdu, "edukasi", oEdukasi, "Procedure",
                    "identifier=" + eduSys + "|" + eduVal);
            edukasiRef = ref.get("edukasi");
        }
        if (!t.pemeriksaanFisik.equals("")) {
            ObjectNode o = buatObservationTeks("51848-0", "Assessments", "exam",
                    t.pemeriksaanFisik, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_fisik", "fisik", o);
        }
        if (!t.pemeriksaanFisik.equals("")) {
            ObjectNode o = buatObservationTeks("8716-3", "Vital signs", "vital-signs",
                    t.pemeriksaanFisik, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_vital", "vital", o);
        }
        String lab = gabungLabel("Pemeriksaan Penunjang", t.pemeriksaanPenunjang,
                "Hasil Laboratorium", gabung(t.hasilLaborat, t.labBelum));
        if (!lab.equals("")) {
            ObjectNode o = buatObservationTeks("11502-2", "Laboratory report", "laboratory",
                    lab, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_lab", "lab", o);
        }
        if (!t.diagnosaAwal.equals("")) {
            ObjectNode o = buatObservationTeks("42347-5", "Admission diagnosis", "exam",
                    t.diagnosaAwal, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_dxawal", "dxawal", o);
        }
        String dxAkhir = narasiDiagnosaAkhir(t);
        if (!dxAkhir.equals("")) {
            ObjectNode o = buatObservationTeks("78375-3", "Discharge diagnosis", "exam",
                    dxAkhir, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_dxakhir", "dxakhir", o);
        }
        String course = t.keluhanIgd;
        if (!course.equals("") && !course.equals("-")) {
            ObjectNode o = buatObservationTeks("8648-8", "Hospital course Narrative", "exam",
                    course, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_course", "course", o);
        }
        String alergiUntukCari = alergiKosong(t.alergi) ? "tidak ada" : t.alergi;
        String[] mapAlergi = cariMappingAlergi(alergiUntukCari);
        if (mapAlergi != null && !mapAlergi[0].equals("")) {
            ObjectNode oAllergy = buatAllergyIntolerance(mapAlergi, noRawat, patientRef, t.namaPasien,
                    encounterRef, waktu, t.idOrg);
            tambahEntryResource(entries, slotOrder, ref, idLama, "id_allergy", "allergy", oAllergy,
                    "AllergyIntolerance");
        }

        ObjectNode comp = buatComposition(noRawat, t, patientRef, encounterRef, waktu, ref, procRefs, edukasiRef);
        tambahEntryResource(entries, slotOrder, ref, idLama, "id_composition", "composition", comp, "Composition");
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            System.out.println("Notifikasi Resume Ranap : Encounter belum ada untuk no_rawat " + noRawat
                    + ". Resume dilewati (kirim Encounter dulu).");
            return;
        }
        ResumeData t = ambilData(noRawat);
        if (t == null) {
            System.out.println("Notifikasi Resume Ranap : resume_pasien_ranap tidak ada untuk no_rawat " + noRawat
                    + ". Resume dilewati.");
            return;
        }
        if (t.idPasien.equals("")) {
            System.out.println("Notifikasi Resume Ranap : ID pasien SATUSEHAT belum ditemukan untuk no_rawat "
                    + noRawat + ". Resume dilewati.");
            return;
        }

        String patientRef   = "Patient/" + t.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu        = t.waktu;
        performerRef = t.idDokter.equals("") ? "" : ("Practitioner/" + t.idDokter);
        curNoRawat = noRawat;
        curIdOrg = t.idOrg;

        Map<String,String> idLama = ambilIdLama(noRawat);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        List<String> slotOrder = new ArrayList<>();   // urutan slot mengikuti urutan entry
        Map<String,String> ref = new HashMap<>();      // slot -> fullUrl (untuk referensi Composition)
        List<String> medRefs = new ArrayList<>();      // fullUrl tiap MedicationRequest (untuk section Obat)
        // slot MedicationRequest -> {no_resep, kode_brng} untuk disimpan ke satu_sehat_medicationrequest
        Map<String,String[]> medKeyBySlot = new HashMap<>();

        // --- Keluhan Utama (10154-3) ---
        if (!t.keluhanUtama.equals("")) {
            ObjectNode o = buatObservationTeks("10154-3", "Chief complaint", "survey",
                    t.keluhanUtama, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_keluhan", "keluhan", o);
        }
        // --- Keluhan Penyerta / Alasan Masuk (11450-4) ---
        String penyerta = pilihTeks(t.alasan, t.diagnosaAwal);
        if (!penyerta.equals("")) {
            ObjectNode o = buatObservationTeks("11450-4", "Problem list - Reported", "survey",
                    penyerta, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_penyerta", "penyerta", o);
        }
        // --- Riwayat Penyakit Sekarang (10164-2) dari resume_pasien_ranap.jalannya_penyakit ---
        if (!t.jalannyaPenyakit.equals("") && !t.jalannyaPenyakit.equals("-")) {
            ObjectNode o = buatObservationTeks("10164-2", "History of Present illness Narrative", "exam",
                    t.jalannyaPenyakit, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_rps", "rps", o);
        }
        // --- Obat saat Kunjungan (10160-0) dari resume_pasien_ranap.obat_di_rs (teks/narasi) ---
        if (!t.obatDiRs.equals("")) {
            ObjectNode o = buatObservationTeks("10160-0", "History of Medication use Narrative", "survey",
                    t.obatDiRs, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_medreq_rs", "medrs", o);
        }
        // --- Obat Pulang (10183-2) dari obat_pulang (teks bebas -> Observation narasi) ---
        if (!t.obatPulang.equals("")) {
            ObjectNode o = buatObservationTeks("10183-2", "Hospital discharge medications Narrative", "survey",
                    t.obatPulang, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_medreq_pulang", "medpulang", o);
        }
        // --- Tindakan Medis (Procedures): satu Procedure (ICD-9-CM) per baris prosedur_pasien.
        //     Idempotent via identifier {noRawat}-{kode} + lookup server + ifNoneExist. ---
        List<String> procRefs = new ArrayList<>();
        int procIdx = 0;
        for (String[] pr : ambilProsedurPasien(noRawat)) {
            ObjectNode oProc = buatProcedure(pr[0], pr[1], t, patientRef, t.namaPasien, encounterRef, waktu);
            if (oProc == null) continue;
            String procSys = "http://sys-ids.kemkes.go.id/procedure/" + t.idOrg;
            String idenVal = noRawat + "-" + pr[0].replaceAll("[^A-Za-z0-9]", "");
            String idProc = cariIdServerByIdentifier("Procedure", procSys, idenVal);
            String slot = "proc" + (procIdx++);
            tambahResourceDenganId(entries, slotOrder, ref, idProc, slot, oProc, "Procedure",
                    "identifier=" + procSys + "|" + idenVal);
            procRefs.add(ref.get(slot));
        }
        // --- Edukasi: Procedure (SNOMED 311401005) dari resume_pasien_ranap.edukasi -> section Plan ---
        String edukasiRef = "";
        ObjectNode oEdukasi = buatProcedureEdukasi(t.edukasi, t, patientRef, t.namaPasien, encounterRef, waktu);
        if (oEdukasi != null) {
            String eduSys = "http://sys-ids.kemkes.go.id/procedure/" + t.idOrg;
            String eduVal = noRawat + "-EDUKASI";
            String idEdu = cariIdServerByIdentifier("Procedure", eduSys, eduVal);
            tambahResourceDenganId(entries, slotOrder, ref, idEdu, "edukasi", oEdukasi, "Procedure",
                    "identifier=" + eduSys + "|" + eduVal);
            edukasiRef = ref.get("edukasi");
        }
        // --- Assessment / Pemeriksaan Fisik (51848-0) dari pemeriksaan_fisik ---
        if (!t.pemeriksaanFisik.equals("")) {
            ObjectNode o = buatObservationTeks("51848-0", "Assessments", "exam",
                    t.pemeriksaanFisik, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_fisik", "fisik", o);
        }
        // --- Tanda-tanda Vital / Vital Sign (8716-3) dari pemeriksaan_fisik ---
        if (!t.pemeriksaanFisik.equals("")) {
            ObjectNode o = buatObservationTeks("8716-3", "Vital signs", "vital-signs",
                    t.pemeriksaanFisik, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_vital", "vital", o);
        }
        // --- Pemeriksaan Laboratorium / Penunjang (11502-2) ---
        String lab = gabungLabel("Pemeriksaan Penunjang", t.pemeriksaanPenunjang,
                "Hasil Laboratorium", gabung(t.hasilLaborat, t.labBelum));
        if (!lab.equals("")) {
            ObjectNode o = buatObservationTeks("11502-2", "Laboratory report", "laboratory",
                    lab, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_lab", "lab", o);
        }
        // --- Diagnosis Awal (42347-5) ---
        if (!t.diagnosaAwal.equals("")) {
            ObjectNode o = buatObservationTeks("42347-5", "Admission diagnosis", "exam",
                    t.diagnosaAwal, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_dxawal", "dxawal", o);
        }
        // --- Diagnosis Akhir (78375-3): diagnosa utama + sekunder ---
        String dxAkhir = narasiDiagnosaAkhir(t);
        if (!dxAkhir.equals("")) {
            ObjectNode o = buatObservationTeks("78375-3", "Discharge diagnosis", "exam",
                    dxAkhir, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_dxakhir", "dxakhir", o);
        }
        // --- Perjalanan Kunjungan / Hospital Course (8648-8) dari penilaian_medis_igd.keluhan_utama.
        //     hasil_laborat & pemeriksaan_penunjang sengaja TIDAK ikut: keduanya sudah dikirim utuh
        //     sebagai Observation Laboratory report (11502-2) di atas. ---
        String course = t.keluhanIgd;
        if (!course.equals("") && !course.equals("-")) {
            ObjectNode o = buatObservationTeks("8648-8", "Hospital course Narrative", "exam",
                    course, patientRef, t.namaPasien, encounterRef, waktu);
            tambahObs(entries, slotOrder, ref, idLama, "id_obs_course", "course", o);
        }
        // --- AllergyIntolerance: mapping keyword dari cache alergisatusehat.iyem
        //     (meniru SatuSehatKirimAllergyIntolerance). Bila alergi kosong/negasi, paksa cari
        //     "tidak ada" agar cocok dengan mapping no-known-allergy (SNOMED 716186003) -> resource
        //     AllergyIntolerance tetap dibuat sehingga viewer tidak menampilkan "Tidak ada data". ---
        String alergiUntukCari = alergiKosong(t.alergi) ? "tidak ada" : t.alergi;
        String[] mapAlergi = cariMappingAlergi(alergiUntukCari);
        if (mapAlergi != null && !mapAlergi[0].equals("")) {
            ObjectNode oAllergy = buatAllergyIntolerance(mapAlergi, noRawat, patientRef, t.namaPasien,
                    encounterRef, waktu, t.idOrg);
            tambahEntryResource(entries, slotOrder, ref, idLama, "id_allergy", "allergy", oAllergy,
                    "AllergyIntolerance");
        }

        // --- Composition (indeks dokumen resume) ---
        ObjectNode comp = buatComposition(noRawat, t, patientRef, encounterRef, waktu, ref, procRefs, edukasiRef);
        tambahEntryResource(entries, slotOrder, ref, idLama, "id_composition", "composition", comp, "Composition");

        // === Kirim Bundle ===
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Resume Ranap : " + link);
        System.out.println("Request JSON Resume : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Resume Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Resume OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Resume Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Resume : " + hasil);

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
        simpanResume(noRawat, idBaru, idLama);
        // Simpan id tiap MedicationRequest ke satu_sehat_resume_medreq (idempotent per resep+obat).
        for (Map.Entry<String,String[]> e : medKeyBySlot.entrySet()) {
            String idMr = idBaru.get(e.getKey());
            if (idMr != null && !idMr.equals("")) {
                simpanMedicationRequest(e.getValue()[0], e.getValue()[1], idMr);
            }
        }
    }

    // ====================== BUILDER RESOURCE ======================

    /** Observation valueString untuk narasi resume (kategori survey/exam/laboratory). */
    private ObjectNode buatObservationTeks(String loinc, String display, String kategori,
            String valueString, String patientRef, String namaPasien, String encounterRef, String waktu) {
        ObjectNode o = dasarObservation(loinc, display, kategori, patientRef, namaPasien, encounterRef, waktu);
        o.put("valueString", valueString);
        return o;
    }

    /** Kerangka Observation: status, category, code LOINC, subject, encounter, effectiveDateTime, performer. */
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
        // performer WAJIB (RuleNumber 10383): Practitioner penanggung jawab.
        if (!performerRef.equals("")) {
            o.putArray("performer").addObject().put("reference", performerRef);
        }
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

    /**
     * Procedure (Tindakan Medis) untuk panel "Procedures" di viewer.
     * code.coding ICD-9-CM diambil dari kd_prosedur_utama (display = prosedur_utama); bila kode kosong,
     * code hanya berisi text dari tindakan_dan_operasi. tindakan_dan_operasi + daftar prosedur sekunder
     * dimasukkan sebagai code.text/note agar narasi bebas tetap tampil. null bila tak ada data tindakan.
     */
    private ObjectNode buatProcedure(String kode, String nama, ResumeData t, String patientRef, String namaPasien,
            String encounterRef, String waktu) {
        // SATUSEHAT mewajibkan Procedure.code.coding ICD-9-CM (RuleNumber 10015). Tanpa kode -> null.
        if (kode == null || kode.trim().equals("")) {
            return null;
        }
        kode = kode.trim();
        String teks = gabung(t.tindakanDanOperasi, nama);
        ObjectNode pr = mapper.createObjectNode();
        pr.put("resourceType", "Procedure");
        ObjectNode idenProc = pr.putArray("identifier").addObject();
        idenProc.put("system", "http://sys-ids.kemkes.go.id/procedure/" + t.idOrg);
        idenProc.put("value", curNoRawat + "-" + kode.replaceAll("[^A-Za-z0-9]", ""));
        pr.put("status", "completed");
        ObjectNode cat = pr.putObject("category").putArray("coding").addObject();
        cat.put("system", "http://snomed.info/sct");
        cat.put("code", "387713003");
        cat.put("display", "Surgical procedure");
        ((ObjectNode) pr.get("category")).put("text", "Tindakan/Prosedur Medis");
        ObjectNode code = pr.putObject("code");
        ObjectNode coding = code.putArray("coding").addObject();
        coding.put("system", "http://hl7.org/fhir/sid/icd-9-cm");
        coding.put("code", kode);
        coding.put("display", nama.equals("") ? kode : nama);
        code.put("text", !teks.equals("") ? teks : (nama.equals("") ? kode : nama));
        ObjectNode subject = pr.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        pr.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) {
            pr.put("performedDateTime", waktu);
        }
        if (!performerRef.equals("")) {
            pr.putArray("performer").addObject().putObject("actor").put("reference", performerRef);
        }
        if (!t.tindakanDanOperasi.equals("")) {
            pr.putArray("note").addObject().put("text", t.tindakanDanOperasi);
        }
        return pr;
    }

    /**
     * Procedure Edukasi pasien (SNOMED 311401005 "Patient education") dari resume_pasien_ranap.edukasi.
     * Direferensikan di section Plan (Rencana Tindak Lanjut). null bila edukasi kosong.
     */
    private ObjectNode buatProcedureEdukasi(String edukasi, ResumeData t, String patientRef, String namaPasien,
            String encounterRef, String waktu) {
        if (edukasi == null || edukasi.trim().equals("")) {
            return null;
        }
        ObjectNode pr = mapper.createObjectNode();
        pr.put("resourceType", "Procedure");
        ObjectNode iden = pr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/procedure/" + t.idOrg);
        iden.put("value", curNoRawat + "-EDUKASI");
        pr.put("status", "completed");
        ObjectNode cat = pr.putObject("category").putArray("coding").addObject();
        cat.put("system", "http://snomed.info/sct");
        cat.put("code", "409063005");
        cat.put("display", "Counselling");
        ((ObjectNode) pr.get("category")).put("text", "Edukasi/Konseling Pasien");
        ObjectNode code = pr.putObject("code");
        ObjectNode coding = code.putArray("coding").addObject();
        coding.put("system", "http://snomed.info/sct");
        coding.put("code", "311401005");
        coding.put("display", "Patient education");
        code.put("text", "Edukasi: " + edukasi);
        ObjectNode subject = pr.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        pr.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) {
            pr.put("performedDateTime", waktu);
        }
        if (!performerRef.equals("")) {
            pr.putArray("performer").addObject().putObject("actor").put("reference", performerRef);
        }
        pr.putArray("note").addObject().put("text", edukasi);
        return pr;
    }

    /** Composition Resume Medis Rawat Inap (LOINC 34105-7) yang mengindeks resource lain via section.entry. */
    private ObjectNode buatComposition(String noRawat, ResumeData t, String patientRef,
            String encounterRef, String waktu, Map<String,String> ref, List<String> procRefs, String edukasiRef) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + t.idOrg);
        iden.put("value", "RESUME-RANAP-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "34105-7");
        typeCoding.put("display", "Hospital Discharge summary");
        comp.putObject("subject").put("reference", patientRef);
        comp.putObject("encounter").put("reference", encounterRef);
        ObjectNode author = comp.putArray("author").addObject();
        author.put("reference", "Practitioner/" + t.idDokter);
        author.put("display", t.namaDokter);
        if (!waktu.equals("")) {
            comp.put("date", waktu);
        }
        comp.put("title", "Resume Medis Rawat Inap");
        // Narrative (text.div) WAJIB agar viewer (mis. INA-CBG) tidak crash membaca text.div.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Resume Medis Rawat Inap - "
                + escapeXml(t.namaPasien) + "</div>");

        ArrayNode section = comp.putArray("section");

        // Keluhan Utama / Chief Complaint (10154-3)
        tambahSectionLoinc(section, ref, "Keluhan Utama (Chief Complaint)", "10154-3", "Chief complaint",
                t.keluhanUtama, "keluhan");
        // Keluhan Penyerta / Alasan Masuk (11450-4)
        tambahSectionLoinc(section, ref, "Keluhan Penyerta", "11450-4", "Problem list - Reported",
                pilihTeks(t.alasan, t.diagnosaAwal), "penyerta");
        // Riwayat Alergi (48765-2) — selalu tampil; entry AllergyIntolerance bila ada
        String narasiAlergi = alergiKosong(t.alergi) ? "Tidak ada alergi" : t.alergi;
        ObjectNode secAlergi = buatSection(section, "Riwayat Alergi", "http://loinc.org", "48765-2",
                "Allergies and adverse reactions Document", narasiAlergi);
        if (ref.containsKey("allergy")) {
            tambahEntrySection(secAlergi.putArray("entry"), ref, "allergy");
        }
        // Riwayat Penyakit Terdahulu (11348-0) — naratif dari diagnosa sekunder
        String terdahulu = narasiDiagnosaSekunder(t);
        if (!terdahulu.equals("")) {
            buatSection(section, "Riwayat Penyakit Terdahulu", "http://loinc.org", "11348-0",
                    "History of Past illness Narrative", terdahulu);
        }
        // Riwayat Penyakit Sekarang (10164-2) — sumber: resume_pasien_ranap.jalannya_penyakit
        tambahSectionLoinc(section, ref, "Riwayat Penyakit Sekarang", "10164-2",
                "History of Present illness Narrative", t.jalannyaPenyakit, "rps");
        // Obat saat Kunjungan (10160-0) — narasi dari obat_di_rs (Observation slot "medrs")
        tambahSectionLoinc(section, ref, "Obat saat Kunjungan", "10160-0", "History of Medication use Narrative",
                t.obatDiRs, "medrs");
        // Obat Pulang (10183-2) — entry MedicationRequest discharge
        tambahSectionLoinc(section, ref, "Obat Pulang", "10183-2", "Hospital discharge medications Narrative",
                t.obatPulang, "medpulang");
        // Assessment / Pemeriksaan Fisik (51848-0)
        tambahSectionLoinc(section, ref, "Assessment", "51848-0", "Assessments", t.pemeriksaanFisik, "fisik");
        // Tanda-tanda Vital (Vital Sign) (8716-3)
        tambahSectionLoinc(section, ref, "Tanda-tanda Vital (Vital Sign)", "8716-3", "Vital signs",
                t.pemeriksaanFisik, "vital");
        // Pemeriksaan Laboratorium / Penunjang (11502-2)
        tambahSectionLoinc(section, ref, "Pemeriksaan Laboratorium", "11502-2", "Laboratory report",
                gabungLabel("Pemeriksaan Penunjang", t.pemeriksaanPenunjang,
                        "Hasil Laboratorium", gabung(t.hasilLaborat, t.labBelum)), "lab");
        // Diagnosis Awal (42347-5)
        tambahSectionLoinc(section, ref, "Diagnosis Awal", "42347-5", "Admission diagnosis", t.diagnosaAwal, "dxawal");
        // Diagnosis Akhir (78375-3)
        tambahSectionLoinc(section, ref, "Diagnosis Akhir", "78375-3", "Discharge diagnosis",
                narasiDiagnosaAkhir(t), "dxakhir");
        // Tindakan Medis (Procedures) — entry semua Procedure ICD-9 (prosedur_pasien); narasi = tindakan_dan_operasi
        ObjectNode secTindakan = buatSection(section, "Tindakan Medis (Procedures)", "http://loinc.org", "29554-3",
                "Procedure Narrative", gabung(t.tindakanDanOperasi, narasiProsedur(t)));
        if (!procRefs.isEmpty()) {
            ArrayNode eTindakan = secTindakan.putArray("entry");
            for (String pref : procRefs) {
                eTindakan.addObject().put("reference", pref);
            }
        }
        // Kondisi Saat Meninggalkan RS (10184-0) — naratif
        String kondisi = gabungLabel("Keadaan Pulang", gabung(t.keadaan, t.ketKeadaan),
                "Cara Keluar", gabung(t.caraKeluar, t.ketKeluar));
        if (!kondisi.equals("")) {
            buatSection(section, "Kondisi Saat Meninggalkan RS", "http://loinc.org", "10184-0",
                    "Hospital discharge physical findings Narrative", kondisi);
        }
        // Rencana Tindak Lanjut (8653-8) — naratif
        String rtl = gabungLabel("Tindak Lanjut", gabung(t.dilanjutkan, t.ketDilanjutkan),
                "Kontrol", t.kontrol);
        rtl = gabung(rtl, t.diet.equals("") ? "" : ("Diet: " + t.diet),
                t.edukasi.equals("") ? "" : ("Edukasi: " + t.edukasi));
        if (!rtl.equals("") || !edukasiRef.equals("")) {
            ObjectNode secPlan = buatSection(section, "Rencana Tindak Lanjut", "http://loinc.org", "8653-8",
                    "Hospital discharge instructions", rtl.equals("") ? "-" : rtl);
            // Edukasi pasien sebagai entry Procedure di bagian Plan (SOAP).
            if (!edukasiRef.equals("")) {
                secPlan.putArray("entry").addObject().put("reference", edukasiRef);
            }
        }
        // Perjalanan Kunjungan / Hospital Course (8648-8) — sumber: penilaian_medis_igd.keluhan_utama.
        // WAJIB pakai text.div: konsol INA-CBG (integrated-viewer) CRASH bila suatu section tak punya
        // text.div ("Cannot read properties of undefined (reading 'div')"). Div wrapper tak terhindarkan.
        tambahSectionLoinc(section, ref, "Perjalanan Kunjungan", "8648-8", "Hospital course Narrative",
                t.keluhanIgd, "course");
        return comp;
    }

    /** Tambah section LOINC dengan text.div (konten) + entry ke Observation slot (bila ada). */
    private void tambahSectionLoinc(ArrayNode section, Map<String,String> ref, String title,
            String loinc, String display, String narasi, String slot) {
        ObjectNode sec = buatSection(section, title, "http://loinc.org", loinc, display, narasi);
        if (ref.containsKey(slot)) {
            tambahEntrySection(sec.putArray("entry"), ref, slot);
        }
    }

    private ObjectNode buatSection(ArrayNode section, String title, String system, String code,
            String display, String narasi) {
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode coding = sec.putObject("code").putArray("coding").addObject();
        coding.put("system", system);
        coding.put("code", code);
        coding.put("display", display);
        // text.div WAJIB tiap section (viewer INA-CBG membacanya); diisi NILAI/konten, bukan judul.
        String isi = (narasi == null || narasi.trim().equals("")) ? "-" : narasi.trim();
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(isi) + "</div>");
        return sec;
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

    /** Narasi diagnosis akhir: utama (kode) + daftar sekunder. */
    private String narasiDiagnosaAkhir(ResumeData t) {
        StringBuilder sb = new StringBuilder();
        String utama = gabungKodeNama(t.kdDiagnosaUtama, t.diagnosaUtama);
        if (!utama.equals("")) sb.append("Diagnosa Utama: ").append(utama);
        String sek = narasiDiagnosaSekunder(t);
        if (!sek.equals("")) {
            if (sb.length() > 0) sb.append("; ");
            sb.append("Diagnosa Sekunder: ").append(sek);
        }
        return sb.toString();
    }

    private String narasiDiagnosaSekunder(ResumeData t) {
        return gabung(t.diagnosaSekunder, t.diagnosaSekunder2, t.diagnosaSekunder3, t.diagnosaSekunder4);
    }

    private String narasiProsedur(ResumeData t) {
        return gabung(t.prosedurUtama, t.prosedurSekunder, t.prosedurSekunder2, t.prosedurSekunder3);
    }

    /** Prosedur ICD-9-CM dari tabel prosedur_pasien (untuk Procedure) -> list {kode, nama}. */
    private List<String[]> ambilProsedurPasien(String noRawat) {
        List<String[]> hasil = new ArrayList<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select pp.kode, ifnull(icd9.deskripsi_panjang,'') as nama "
                + "from prosedur_pasien pp left join icd9 on icd9.kode=pp.kode "
                + "where pp.no_rawat=? order by pp.prioritas")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String kode = nz(r.getString("kode")).trim();
                    if (!kode.equals("")) hasil.add(new String[]{kode, bersihkan(r.getString("nama"))});
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Resume ambilProsedurPasien : " + e);
        }
        return hasil;
    }

    private String gabungKodeNama(String kode, String nama) {
        String k = nz(kode).trim(), n = nz(nama).trim();
        if (k.equals("") && n.equals("")) return "";
        if (k.equals("")) return n;
        if (n.equals("")) return k;
        return k + " - " + n;
    }

    // ====================== ENTRY BUNDLE ======================

    /**
     * Tambah Observation ke bundle. Setiap Observation diberi business identifier
     * (system .../observation/{idOrg}, value {noRawat}-{slot}); saat POST dipakai ifNoneExist
     * agar server idempotent (no-op bila identifier sudah ada) dan tidak menolak "Found duplicate".
     */
    private void tambahObs(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            Map<String,String> idLama, String kolomId, String slot, ObjectNode obs) {
        String system = "http://sys-ids.kemkes.go.id/observation/" + curIdOrg;
        String value = curNoRawat + "-" + slot;
        if (!obs.has("identifier")) {
            ArrayNode arr = mapper.createArrayNode();
            ObjectNode iden = arr.addObject();
            iden.put("system", system);
            iden.put("value", value);
            obs.set("identifier", arr);
        }
        // Tentukan id existing: id lokal dulu, kalau kosong cari di server by identifier (agar Update -> PUT).
        String idExisting = idLama.get(kolomId);
        if (idExisting == null || idExisting.equals("")) {
            idExisting = cariIdServerByIdentifier("Observation", system, value);
        }
        String ifNoneExist = "identifier=" + system + "|" + value;
        tambahResourceDenganId(entries, slotOrder, ref, idExisting, slot, obs, "Observation", ifNoneExist);
    }

    /** GET resource di server by identifier -> kembalikan id (atau "" bila tidak ada). Agar Update jadi PUT. */
    private String cariIdServerByIdentifier(String resourceType, String identifierSystem, String identifierValue) {
        if (resourceType == null || resourceType.equals("") || identifierValue == null || identifierValue.equals("")) {
            return "";
        }
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            String token = java.net.URLEncoder.encode(identifierSystem + "|" + identifierValue, "UTF-8");
            // URI object (bukan String) supaya RestTemplate tidak meng-encode ulang '%' (double-encoding).
            java.net.URI uri = java.net.URI.create(link + "/" + resourceType + "?identifier=" + token + "&_count=1");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            if (r.path("total").asInt(0) > 0) {
                JsonNode es = r.path("entry");
                if (es.isArray() && es.size() > 0) {
                    String id = es.get(0).path("resource").path("id").asText();
                    return id == null ? "" : id;
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Resume cariIdServerByIdentifier : " + e);
        }
        return "";
    }

    /** Tambah resource generik ke bundle dengan kontrol PUT/POST + catat fullUrl untuk referensi. */
    private void tambahEntryResource(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            Map<String,String> idLama, String kolomId, String slot, ObjectNode resource, String resourceType) {
        tambahEntryResource(entries, slotOrder, ref, idLama, kolomId, slot, resource, resourceType, null);
    }

    private void tambahEntryResource(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            Map<String,String> idLama, String kolomId, String slot, ObjectNode resource, String resourceType,
            String ifNoneExist) {
        tambahResourceDenganId(entries, slotOrder, ref, idLama.get(kolomId), slot, resource, resourceType, ifNoneExist);
    }

    private void tambahResourceDenganId(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            String idExisting, String slot, ObjectNode resource, String resourceType) {
        tambahResourceDenganId(entries, slotOrder, ref, idExisting, slot, resource, resourceType, null);
    }

    /** Tambah resource ke bundle dengan id existing eksplisit (PUT bila ada, else POST + urn:uuid + ifNoneExist). */
    private void tambahResourceDenganId(ArrayNode entries, List<String> slotOrder, Map<String,String> ref,
            String idExisting, String slot, ObjectNode resource, String resourceType, String ifNoneExist) {
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
            if (ifNoneExist != null && !ifNoneExist.equals("")) {
                request.put("ifNoneExist", ifNoneExist);
            }
        }
        slotOrder.add(slot);
        ref.put(slot, fullUrl);
    }

    // ====================== OBAT (MEDICATIONREQUEST KFA) ======================

    /**
     * Ambil obat pemberian rawat inap (detail_pemberian_obat) lengkap dengan mapping KFA
     * (id_medication), aturan pakai, route, dan satuan. Hanya obat yang punya id_medication
     * yang dikembalikan (tanpa KFA tidak bisa jadi MedicationRequest valid).
     */
    private List<ObatData> ambilObatRanap(String noRawat) {
        List<ObatData> hasil = new ArrayList<>();
        String sql = "select dpo.kode_brng, dpo.jml, "
                + "ifnull(ro.tgl_peresepan,'') as tgl_peresepan, ifnull(ro.jam_peresepan,'') as jam_peresepan, "
                + "ifnull(ro.no_resep,'') as no_resep, ifnull(ap.aturan,'') as aturan_pakai, "
                + "ifnull(db.nama_brng,'') as nama_brng, ifnull(smo.obat_display,'') as obat_display, "
                + "ifnull(smo.route_code,'') as route_code, ifnull(smo.route_system,'') as route_system, "
                + "ifnull(smo.route_display,'') as route_display, ifnull(smo.denominator_code,'') as denominator_code, "
                + "ifnull(smo.denominator_system,'') as denominator_system, ifnull(sm.id_medication,'') as id_medication "
                + "from detail_pemberian_obat dpo "
                + "left join resep_obat ro on ro.no_rawat=dpo.no_rawat and ro.tgl_perawatan=dpo.tgl_perawatan and ro.jam=dpo.jam "
                + "left join aturan_pakai ap on ap.no_rawat=dpo.no_rawat and ap.tgl_perawatan=dpo.tgl_perawatan "
                + "  and ap.jam=dpo.jam and ap.kode_brng=dpo.kode_brng "
                + "left join databarang db on db.kode_brng=dpo.kode_brng "
                + "left join satu_sehat_mapping_obat smo on smo.kode_brng=dpo.kode_brng "
                + "left join satu_sehat_medication sm on sm.kode_brng=dpo.kode_brng "
                + "where dpo.no_rawat=? order by dpo.tgl_perawatan asc, dpo.jam asc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String idMed = nz(r.getString("id_medication"));
                    if (idMed.equals("")) {
                        continue;   // tanpa KFA tidak bisa dikirim sebagai MedicationRequest
                    }
                    ObatData m = new ObatData();
                    m.kodeBrng = nz(r.getString("kode_brng")).trim();
                    m.jml = nz(r.getString("jml"));
                    m.tglPeresepan = nz(r.getString("tgl_peresepan"));
                    m.jamPeresepan = nz(r.getString("jam_peresepan"));
                    m.noResep = nz(r.getString("no_resep")).trim();
                    m.aturanPakai = nz(r.getString("aturan_pakai"));
                    String disp = nz(r.getString("obat_display"));
                    m.obatDisplay = !disp.equals("") ? disp : nz(r.getString("nama_brng"));
                    m.routeCode = nz(r.getString("route_code"));
                    m.routeSystem = nz(r.getString("route_system"));
                    m.routeDisplay = nz(r.getString("route_display"));
                    m.denominatorCode = nz(r.getString("denominator_code"));
                    m.denominatorSystem = nz(r.getString("denominator_system"));
                    m.idMedication = idMed;
                    // no_resep wajib untuk idempotency; fallback pakai kode+no_rawat bila kosong.
                    if (m.noResep.equals("")) {
                        m.noResep = noRawat + "-" + m.kodeBrng;
                    }
                    hasil.add(m);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Resume ambilObatRanap : " + e);
        }
        return hasil;
    }

    /** MedicationRequest valid ber-KFA (medicationReference) untuk obat rawat inap. */
    private ObjectNode buatMedicationRequestKfa(ObatData m, String patientRef, String namaPasien,
            String encounterRef, String idDokter, String namaDokter, String idOrg, String fallbackWaktu) {
        int frequency = parseSignaFreq(m.aturanPakai);
        if (frequency <= 0) frequency = 1;

        ObjectNode req = mapper.createObjectNode();
        req.put("resourceType", "MedicationRequest");
        ArrayNode identifier = req.putArray("identifier");
        ObjectNode iden1 = identifier.addObject();
        iden1.put("system", "http://sys-ids.kemkes.go.id/prescription/" + idOrg);
        iden1.put("use", "official");
        iden1.put("value", m.noResep.equals("") ? "-" : m.noResep);
        ObjectNode iden2 = identifier.addObject();
        iden2.put("system", "http://sys-ids.kemkes.go.id/prescription-item/" + idOrg);
        iden2.put("use", "official");
        iden2.put("value", m.kodeBrng.equals("") ? "-" : m.kodeBrng);

        req.put("status", "completed");
        req.put("intent", "order");
        ObjectNode catCoding = req.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/medicationrequest-category");
        catCoding.put("code", "inpatient");
        catCoding.put("display", "Inpatient");

        ObjectNode medRef = req.putObject("medicationReference");
        medRef.put("reference", "Medication/" + m.idMedication);
        medRef.put("display", m.obatDisplay.equals("") ? m.kodeBrng : m.obatDisplay);

        ObjectNode subject = req.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        req.putObject("encounter").put("reference", encounterRef);

        String authoredOn = fallbackWaktu;
        // Hindari tanggal kosong/0000-00-00 (dateTime invalid) -> pakai tanggal dokumen.
        if (!m.tglPeresepan.equals("") && !m.tglPeresepan.startsWith("0000")) {
            authoredOn = formatWaktu(m.tglPeresepan + " " + (m.jamPeresepan.equals("") ? "00:00:00" : m.jamPeresepan));
        }
        if (!authoredOn.equals("")) {
            req.put("authoredOn", authoredOn);
        }
        if (!idDokter.equals("")) {
            ObjectNode requester = req.putObject("requester");
            requester.put("reference", "Practitioner/" + idDokter);
            requester.put("display", namaDokter);
        }

        ArrayNode dosage = req.putArray("dosageInstruction");
        ObjectNode dose = dosage.addObject();
        dose.put("sequence", 1);
        dose.put("patientInstruction", m.aturanPakai.equals("") ? "-" : m.aturanPakai);
        ObjectNode repeat = dose.putObject("timing").putObject("repeat");
        repeat.put("frequency", frequency);
        repeat.put("period", 1);
        repeat.put("periodUnit", "d");
        String[] route = normalisasiRoute(m.routeCode, m.routeDisplay);
        if (!route[1].equals("")) {
            ObjectNode routeCoding = dose.putObject("route").putArray("coding").addObject();
            routeCoding.put("system", route[0]);
            routeCoding.put("code", route[1]);
            routeCoding.put("display", route[2]);
        }
        ObjectNode doseQty = dose.putArray("doseAndRate").addObject().putObject("doseQuantity");
        isiQuantity(doseQty, 1, m.denominatorCode);

        ObjectNode dispenseRequest = req.putObject("dispenseRequest");
        Double jumlah = parseAngka(m.jml);
        isiQuantity(dispenseRequest.putObject("quantity"), jumlah == null ? 1 : jumlah, m.denominatorCode);
        dispenseRequest.putObject("performer").put("reference", "Organization/" + idOrg);
        return req;
    }

    /** Quantity obat: value + unit natural + v3-orderableDrugForm (universal valid di SATUSEHAT). */
    private void isiQuantity(ObjectNode qty, double value, String denominatorCode) {
        String k = denominatorCode == null ? "" : denominatorCode.trim().toUpperCase();
        String unit, code;
        if (k.equals("TAB") || k.equals("TABLET")) { unit = "tablet"; code = "TAB"; }
        else if (k.equals("CAP") || k.equals("CAPS") || k.equals("KAPSUL") || k.equals("KAP")) { unit = "capsule"; code = "CAP"; }
        else if (k.equals("AMP") || k.equals("AMPUL")) { unit = "ampoule"; code = "TAB"; }
        else if (k.equals("BTL") || k.equals("BOTOL")) { unit = "bottle"; code = "TAB"; }
        else if (k.equals("VL") || k.equals("VIAL")) { unit = "vial"; code = "TAB"; }
        else if (k.equals("ML")) { unit = "mL"; code = "TAB"; }
        else { unit = "item"; code = "TAB"; }
        qty.put("value", value <= 0 ? 1 : value);
        qty.put("unit", unit);
        qty.put("system", "http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm");
        qty.put("code", code);
    }

    /** Frekuensi per hari dari signa "3x1"/"2x" dst; default 1. */
    private int parseSignaFreq(String aturanPakai) {
        if (aturanPakai == null || aturanPakai.trim().equals("")) return 1;
        String teks = aturanPakai.toLowerCase().trim();
        Matcher m = Pattern.compile("(\\d+)\\s*x").matcher(teks);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception e) { return 1; }
        }
        return 1;
    }

    /** Route ke (system, code, display) ATC; default Oral. */
    private String[] normalisasiRoute(String routeCode, String routeDisplay) {
        String k = routeCode == null ? "" : routeCode.trim().toUpperCase();
        String d = routeDisplay == null ? "" : routeDisplay.trim().toUpperCase();
        String ATC = "http://www.whocc.no/atc";
        if (k.equals("P") || k.equals("INJ") || k.equals("IV") || d.contains("INJEKSI") || d.contains("INTRAVENA") || d.contains("PARENTERAL")) {
            return new String[]{ATC, "P", "Parenteral"};
        }
        if (k.equals("SL") || d.contains("SUBLINGUAL")) return new String[]{ATC, "SL", "Sublingual"};
        if (k.equals("R") || d.contains("RECTAL") || d.contains("REKTAL")) return new String[]{ATC, "R", "Rectal"};
        return new String[]{ATC, "O", "Oral"};
    }

    /**
     * Cari id MedicationRequest di server by identifier resep (prescription) lalu cocokkan
     * item (prescription-item = kode_brng). Dipakai sebagai fallback agar Update -> PUT
     * walau id lokal hilang/belum tersimpan.
     */
    private String cariIdMedReqServer(String noResep, String kodeBrng, String idOrg) {
        if (noResep == null || noResep.equals("") || kodeBrng == null || kodeBrng.equals("")) {
            return "";
        }
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            String sysPresc = "http://sys-ids.kemkes.go.id/prescription/" + idOrg;
            String token = java.net.URLEncoder.encode(sysPresc + "|" + noResep, "UTF-8");
            java.net.URI uri = java.net.URI.create(link + "/MedicationRequest?identifier=" + token + "&_count=50");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            JsonNode entries = r.path("entry");
            if (entries.isArray()) {
                for (JsonNode e : entries) {
                    JsonNode res = e.path("resource");
                    for (JsonNode iden : res.path("identifier")) {
                        if (iden.path("system").asText().contains("prescription-item")
                                && iden.path("value").asText().equals(kodeBrng)) {
                            return res.path("id").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Resume cariIdMedReqServer : " + e);
        }
        return "";
    }

    /** Ambil id MedicationRequest lama dari satu_sehat_resume_medreq (untuk PUT). */
    private String cariIdMedicationRequest(String noResep, String kodeBrng) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select id_medicationrequest from satu_sehat_resume_medreq "
                    + "where no_resep=? and kode_brng=? limit 1");
            p.setString(1, noResep);
            p.setString(2, kodeBrng);
            ResultSet r = p.executeQuery();
            String id = "";
            if (r.next()) id = nz(r.getString(1));
            r.close();
            p.close();
            return id;
        } catch (Exception e) {
            System.out.println("Notifikasi Resume cariIdMedicationRequest : " + e);
            return "";
        }
    }

    /** Upsert id MedicationRequest ke satu_sehat_resume_medreq (update dulu, insert bila belum ada). */
    private void simpanMedicationRequest(String noResep, String kodeBrng, String idMedReq) {
        if (noResep == null || noResep.equals("") || kodeBrng == null || kodeBrng.equals("")
                || idMedReq == null || idMedReq.equals("")) {
            return;
        }
        try {
            PreparedStatement up = koneksi.prepareStatement(
                    "update satu_sehat_resume_medreq set id_medicationrequest=? where no_resep=? and kode_brng=?");
            up.setString(1, idMedReq);
            up.setString(2, noResep);
            up.setString(3, kodeBrng);
            int n = up.executeUpdate();
            up.close();
            if (n == 0) {
                PreparedStatement ins = koneksi.prepareStatement(
                        "insert into satu_sehat_resume_medreq (no_resep, kode_brng, id_medicationrequest) values (?,?,?)");
                ins.setString(1, noResep);
                ins.setString(2, kodeBrng);
                ins.setString(3, idMedReq);
                ins.executeUpdate();
                ins.close();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Resume simpanMedicationRequest : " + e);
        }
    }

    // ====================== QUERY & SIMPAN ======================

    private ResumeData ambilData(String noRawat) {
        ResumeData t = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(rpr.keluhan_utama,'') as keluhan_utama, ifnull(rpr.alasan,'') as alasan, "
                    + "ifnull(rpr.diagnosa_awal,'') as diagnosa_awal, ifnull(rpr.pemeriksaan_fisik,'') as pemeriksaan_fisik, "
                    + "ifnull(rpr.jalannya_penyakit,'') as jalannya_penyakit, ifnull(rpr.pemeriksaan_penunjang,'') as pemeriksaan_penunjang, "
                    + "ifnull(rpr.hasil_laborat,'') as hasil_laborat, ifnull(rpr.lab_belum,'') as lab_belum, "
                    + "ifnull(rpr.tindakan_dan_operasi,'') as tindakan_dan_operasi, ifnull(rpr.obat_di_rs,'') as obat_di_rs, "
                    + "ifnull(rpr.obat_pulang,'') as obat_pulang, ifnull(rpr.diagnosa_utama,'') as diagnosa_utama, "
                    + "ifnull(rpr.kd_diagnosa_utama,'') as kd_diagnosa_utama, ifnull(rpr.diagnosa_sekunder,'') as diagnosa_sekunder, "
                    + "ifnull(rpr.diagnosa_sekunder2,'') as diagnosa_sekunder2, ifnull(rpr.diagnosa_sekunder3,'') as diagnosa_sekunder3, "
                    + "ifnull(rpr.diagnosa_sekunder4,'') as diagnosa_sekunder4, ifnull(rpr.prosedur_utama,'') as prosedur_utama, "
                    + "ifnull(rpr.prosedur_sekunder,'') as prosedur_sekunder, ifnull(rpr.prosedur_sekunder2,'') as prosedur_sekunder2, "
                    + "ifnull(rpr.prosedur_sekunder3,'') as prosedur_sekunder3, "
                    + "ifnull(rpr.kd_prosedur_utama,'') as kd_prosedur_utama, ifnull(rpr.kd_prosedur_sekunder,'') as kd_prosedur_sekunder, "
                    + "ifnull(rpr.kd_prosedur_sekunder2,'') as kd_prosedur_sekunder2, ifnull(rpr.kd_prosedur_sekunder3,'') as kd_prosedur_sekunder3, "
                    + "ifnull(rpr.alergi,'') as alergi, "
                    + "ifnull(rpr.diet,'') as diet, ifnull(rpr.edukasi,'') as edukasi, ifnull(rpr.cara_keluar,'') as cara_keluar, "
                    + "ifnull(rpr.ket_keluar,'') as ket_keluar, ifnull(rpr.keadaan,'') as keadaan, ifnull(rpr.ket_keadaan,'') as ket_keadaan, "
                    + "ifnull(rpr.dilanjutkan,'') as dilanjutkan, ifnull(rpr.ket_dilanjutkan,'') as ket_dilanjutkan, "
                    + "ifnull(rpr.kontrol,'') as kontrol, "
                    + "rp.tgl_registrasi, rp.jam_reg, "
                    + "(select ki.tgl_keluar from kamar_inap ki where ki.no_rawat=rpr.no_rawat order by ki.tgl_keluar desc limit 1) as tgl_keluar, "
                    + "(select ki.jam_keluar from kamar_inap ki where ki.no_rawat=rpr.no_rawat order by ki.tgl_keluar desc limit 1) as jam_keluar, "
                    + "ifnull(pmi.keluhan_utama,'') as keluhan_igd, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, ifnull(peg.no_ktp,'') as nik_dokter, ifnull(dr.nm_dokter,'') as nm_dokter "
                    + "from resume_pasien_ranap rpr "
                    + "inner join reg_periksa rp on rp.no_rawat=rpr.no_rawat "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=rpr.kd_dokter "
                    + "left join pegawai peg on peg.nik=rpr.kd_dokter "
                    + "left join penilaian_medis_igd pmi on pmi.no_rawat=rpr.no_rawat "
                    + "where rpr.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                t = new ResumeData();
                t.noRawat = noRawat;
                t.keluhanUtama = bersihkan(r.getString("keluhan_utama"));
                t.alasan = bersihkan(r.getString("alasan"));
                t.diagnosaAwal = bersihkan(r.getString("diagnosa_awal"));
                t.pemeriksaanFisik = bersihkan(r.getString("pemeriksaan_fisik"));
                t.jalannyaPenyakit = bersihkan(r.getString("jalannya_penyakit"));
                t.keluhanIgd = bersihkan(r.getString("keluhan_igd"));
                t.pemeriksaanPenunjang = bersihkan(r.getString("pemeriksaan_penunjang"));
                t.hasilLaborat = bersihkan(r.getString("hasil_laborat"));
                t.labBelum = bersihkan(r.getString("lab_belum"));
                t.tindakanDanOperasi = bersihkan(r.getString("tindakan_dan_operasi"));
                t.obatDiRs = bersihkan(r.getString("obat_di_rs"));
                t.obatPulang = bersihkan(r.getString("obat_pulang"));
                t.diagnosaUtama = bersihkan(r.getString("diagnosa_utama"));
                t.kdDiagnosaUtama = nz(r.getString("kd_diagnosa_utama"));
                t.diagnosaSekunder = bersihkan(r.getString("diagnosa_sekunder"));
                t.diagnosaSekunder2 = bersihkan(r.getString("diagnosa_sekunder2"));
                t.diagnosaSekunder3 = bersihkan(r.getString("diagnosa_sekunder3"));
                t.diagnosaSekunder4 = bersihkan(r.getString("diagnosa_sekunder4"));
                t.prosedurUtama = bersihkan(r.getString("prosedur_utama"));
                t.prosedurSekunder = bersihkan(r.getString("prosedur_sekunder"));
                t.prosedurSekunder2 = bersihkan(r.getString("prosedur_sekunder2"));
                t.prosedurSekunder3 = bersihkan(r.getString("prosedur_sekunder3"));
                t.kdProsedurUtama = nz(r.getString("kd_prosedur_utama"));
                t.kdProsedurSekunder = nz(r.getString("kd_prosedur_sekunder"));
                t.kdProsedurSekunder2 = nz(r.getString("kd_prosedur_sekunder2"));
                t.kdProsedurSekunder3 = nz(r.getString("kd_prosedur_sekunder3"));
                t.alergi = bersihkan(r.getString("alergi"));
                t.diet = bersihkan(r.getString("diet"));
                t.edukasi = bersihkan(r.getString("edukasi"));
                t.caraKeluar = bersihkan(r.getString("cara_keluar"));
                t.ketKeluar = bersihkan(r.getString("ket_keluar"));
                t.keadaan = bersihkan(r.getString("keadaan"));
                t.ketKeadaan = bersihkan(r.getString("ket_keadaan"));
                t.dilanjutkan = bersihkan(r.getString("dilanjutkan"));
                t.ketDilanjutkan = bersihkan(r.getString("ket_dilanjutkan"));
                t.kontrol = bersihkan(r.getString("kontrol"));
                // Tanggal dokumen: utamakan tgl/jam keluar bangsal, fallback registrasi.
                String tglKeluar = nz(r.getString("tgl_keluar"));
                String jamKeluar = nz(r.getString("jam_keluar"));
                if (!tglKeluar.equals("") && !tglKeluar.startsWith("0000")) {
                    t.waktu = formatWaktu(tglKeluar + " " + (jamKeluar.equals("") ? "00:00:00" : jamKeluar));
                } else {
                    t.waktu = formatWaktu(nz(r.getString("tgl_registrasi")) + " "
                            + nz(r.getString("jam_reg")));
                }
                t.namaPasien = nz(r.getString("nm_pasien"));
                t.namaDokter = nz(r.getString("nm_dokter"));
                t.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                t.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("nik_dokter"))));
                t.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Resume ambilData : " + e);
        }
        return t;
    }

    /** Ambil id resource lama dari satu_sehat_resume_ranap (untuk PUT). */
    private Map<String,String> ambilIdLama(String noRawat) {
        Map<String,String> m = new HashMap<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select id_obs_keluhan, id_obs_penyerta, id_obs_rps, id_medreq_rs, id_medreq_pulang, "
                    + "id_procedure, id_obs_fisik, id_obs_vital, id_obs_lab, id_obs_dxawal, id_obs_dxakhir, id_obs_course, "
                    + "id_allergy, id_composition "
                    + "from satu_sehat_resume_ranap where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                String[] kolom = {"id_obs_keluhan","id_obs_penyerta","id_obs_rps","id_medreq_rs","id_medreq_pulang",
                        "id_procedure","id_obs_fisik","id_obs_vital","id_obs_lab","id_obs_dxawal","id_obs_dxakhir","id_obs_course",
                        "id_allergy","id_composition"};
                for (String k : kolom) {
                    m.put(k, nz(r.getString(k)));
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Resume ambilIdLama : " + e);
        }
        return m;
    }

    /** Upsert id resource hasil response ke satu_sehat_resume_ranap (pertahankan id lama bila slot tak terkirim). */
    private void simpanResume(String noRawat, Map<String,String> idBaru, Map<String,String> idLama) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "insert into satu_sehat_resume_ranap "
                    + "(no_rawat, id_obs_keluhan, id_obs_penyerta, id_obs_rps, id_medreq_rs, id_medreq_pulang, "
                    + "id_procedure, id_obs_fisik, id_obs_vital, id_obs_lab, id_obs_dxawal, id_obs_dxakhir, id_obs_course, "
                    + "id_allergy, id_composition) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                    + "on duplicate key update "
                    + "id_obs_keluhan=values(id_obs_keluhan), id_obs_penyerta=values(id_obs_penyerta), "
                    + "id_obs_rps=values(id_obs_rps), id_medreq_rs=values(id_medreq_rs), "
                    + "id_medreq_pulang=values(id_medreq_pulang), id_procedure=values(id_procedure), "
                    + "id_obs_fisik=values(id_obs_fisik), id_obs_vital=values(id_obs_vital), id_obs_lab=values(id_obs_lab), "
                    + "id_obs_dxawal=values(id_obs_dxawal), id_obs_dxakhir=values(id_obs_dxakhir), "
                    + "id_obs_course=values(id_obs_course), id_allergy=values(id_allergy), "
                    + "id_composition=values(id_composition)");
            p.setString(1, noRawat);
            p.setString(2, pilih(idBaru, "keluhan", idLama, "id_obs_keluhan"));
            p.setString(3, pilih(idBaru, "penyerta", idLama, "id_obs_penyerta"));
            p.setString(4, pilih(idBaru, "rps", idLama, "id_obs_rps"));
            p.setString(5, pilih(idBaru, "medrs", idLama, "id_medreq_rs"));
            p.setString(6, pilih(idBaru, "medpulang", idLama, "id_medreq_pulang"));
            p.setString(7, pilih(idBaru, "procedure", idLama, "id_procedure"));
            p.setString(8, pilih(idBaru, "fisik", idLama, "id_obs_fisik"));
            p.setString(9, pilih(idBaru, "vital", idLama, "id_obs_vital"));
            p.setString(10, pilih(idBaru, "lab", idLama, "id_obs_lab"));
            p.setString(11, pilih(idBaru, "dxawal", idLama, "id_obs_dxawal"));
            p.setString(12, pilih(idBaru, "dxakhir", idLama, "id_obs_dxakhir"));
            p.setString(13, pilih(idBaru, "course", idLama, "id_obs_course"));
            p.setString(14, pilih(idBaru, "allergy", idLama, "id_allergy"));
            p.setString(15, pilih(idBaru, "composition", idLama, "id_composition"));
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Resume simpanResume : " + e);
        }
    }

    /** Buat tabel mapping id bila belum ada (tanpa FK, agar pengiriman ulang idempotent). */
    private void pastikanTabel() {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "create table if not exists satu_sehat_resume_ranap ("
                    + "no_rawat varchar(17) not null, "
                    + "id_obs_keluhan varchar(50) default '', id_obs_penyerta varchar(50) default '', "
                    + "id_obs_rps varchar(50) default '', id_medreq_rs varchar(50) default '', "
                    + "id_medreq_pulang varchar(50) default '', id_procedure varchar(50) default '', "
                    + "id_obs_fisik varchar(50) default '', id_obs_vital varchar(50) default '', id_obs_lab varchar(50) default '', "
                    + "id_obs_dxawal varchar(50) default '', id_obs_dxakhir varchar(50) default '', "
                    + "id_obs_course varchar(50) default '', id_allergy varchar(50) default '', "
                    + "id_composition varchar(50) default '', "
                    + "primary key (no_rawat)) engine=InnoDB default charset=latin1");
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Resume pastikanTabel : " + e);
        }
        // Tambah kolom baru bila tabel sudah terlanjur dibuat versi lama (abaikan error "duplicate column").
        String[] kolomBaru = {"id_medreq_rs", "id_medreq_pulang", "id_procedure", "id_obs_vital"};
        for (String k : kolomBaru) {
            try (PreparedStatement pa = koneksi.prepareStatement(
                    "alter table satu_sehat_resume_ranap add column " + k + " varchar(50) default ''")) {
                pa.executeUpdate();
            } catch (Exception e) {
                // kolom sudah ada -> aman diabaikan
            }
        }
        // Tabel mapping MedicationRequest KHUSUS resume (TANPA foreign key), supaya no_resep fallback
        // (noRawat-kodeBrng untuk obat tanpa resep) tidak melanggar FK no_resep->resep_obat seperti
        // pada tabel satu_sehat_medicationrequest milik modul farmasi.
        try (PreparedStatement pm = koneksi.prepareStatement(
                "create table if not exists satu_sehat_resume_medreq ("
                + "no_resep varchar(50) not null, kode_brng varchar(20) not null, "
                + "id_medicationrequest varchar(50) default '', "
                + "primary key (no_resep, kode_brng)) engine=InnoDB default charset=latin1")) {
            pm.executeUpdate();
        } catch (Exception e) {
            // tabel sudah ada -> aman diabaikan
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

    /** Pilih nilai non-kosong pertama. */
    private String pilihTeks(String... vals) {
        for (String v : vals) {
            if (v != null && !v.trim().equals("") && !v.trim().equals("-")) return v.trim();
        }
        return "";
    }

    /** Gabung beberapa narasi dengan pemisah ". " (skip yang kosong/"-"). */
    private String gabung(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String v = nz(p).trim();
            if (!v.equals("") && !v.equals("-")) {
                if (sb.length() > 0) sb.append(". ");
                sb.append(v);
            }
        }
        return sb.toString();
    }

    /** Gabung pasangan label-nilai: "Label1: nilai1. Label2: nilai2" (skip yang kosong). */
    private String gabungLabel(String l1, String v1, String l2, String v2) {
        StringBuilder sb = new StringBuilder();
        if (!nz(v1).trim().equals("") && !nz(v1).trim().equals("-")) {
            sb.append(l1).append(": ").append(v1.trim());
        }
        if (!nz(v2).trim().equals("") && !nz(v2).trim().equals("-")) {
            if (sb.length() > 0) sb.append(". ");
            sb.append(l2).append(": ").append(v2.trim());
        }
        return sb.toString();
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

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
