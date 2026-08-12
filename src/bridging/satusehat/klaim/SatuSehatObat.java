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
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * Sub-sender obat untuk flow SatuSehatBundle (dipanggil setelah Encounter).
 *
 * Membangun & mengirim dalam SATU transaction Bundle: MedicationRequest + MedicationStatement (resep)
 * dan MedicationDispense + MedicationAdministration (penyerahan/pemberian). Medication (KFA) dicek dulu
 * keberadaannya di server; resep yang Medication-nya belum ada di SATUSEHAT di-skip.
 *
 * Sumber: resep_obat + resep_dokter (resep) dan detail_pemberian_obat (pemberian), join mapping
 * satu_sehat_mapping_obat + satu_sehat_medication. Logika diadaptasi dari SatuSehatKirimBundle.
 *
 * Idempotent: tiap resource pakai id stabil (deterministik) / hasil lookup server -> PUT (upsert),
 * lalu id response disimpan ke tabel satu_sehat_medication* sehingga kirim ulang tidak menduplikasi.
 */
public class SatuSehatObat {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private static final String ZONA = "+07:00";

    public SatuSehatObat() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Obat : " + e);
        }
    }

    // ====================== Holder ======================

    private static class Konteks {
        String idPasien = "", namaPasien = "", idDokter = "", namaDokter = "", statusLanjut = "Ralan", mulai = "";
    }

    private static class MedikasiData {
        String noResep, kodeBrng, tglPeresepan, jamPeresepan, idMedication, obatDisplay;
        String obatCode, obatSystem, formCode, formSystem, formDisplay;
        String routeCode, routeSystem, routeDisplay, denominatorCode, denominatorSystem, aturanPakai, jml;
        String fallbackAuthoredOn;
    }

    private static class DispenseData {
        String noRawat, noResep, kodeBrng, tglPemberian, jamPemberian, tglPeresepan, jamPeresepan;
        String noBatch, noFaktur, jml, aturanPakai, idMedication, obatDisplay;
        String obatCode, obatSystem, formCode, formSystem, formDisplay;
        String routeCode, routeSystem, routeDisplay, denominatorCode, denominatorSystem, idLokasiDepo, namaDepo;
    }

    private static class EntryMeta {
        String tipe, noResep, kodeBrng, noRawat, tgl, jam, noBatch, noFaktur;
    }

    // ====================== Entry point ======================

    /**
     * Bangun & kirim seluruh resource obat untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter dari server (WAJIB; semua resource mereferensi ini)
     */
    /** PREVIEW: rakit SATU Bundle obat (MedicationRequest/Statement/Dispense/Administration) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (isBlank(idEncounter)) return null;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || isBlank(k.idPasien)) return null;
        if (isBlank(k.idDokter)) return null;

        String encounterReference = "Encounter/" + idEncounter;
        String patientReference = "Patient/" + k.idPasien;
        String idOrg = koneksiDB.IDSATUSEHAT();

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        List<EntryMeta> metas = new ArrayList<>();
        Map<String, Boolean> medAdaCache = new HashMap<>();
        Map<String, String> medReqRefByKey = new HashMap<>();

        Set<String> reqAdded = new HashSet<>();
        Set<String> stmtAdded = new HashSet<>();
        for (MedikasiData m : ambilSemuaResep(noRawat)) {
            if (isBlank(m.obatCode) || isBlank(m.noResep) || isBlank(m.kodeBrng)) {
                continue;
            }
            String resepKey = m.noResep + "|" + m.kodeBrng;
            if (!reqAdded.add(resepKey)) {
                continue;
            }
            m.fallbackAuthoredOn = k.mulai;
            ObjectNode medReq = buatMedicationRequest(m, encounterReference, patientReference, k.namaPasien,
                    k.idDokter, k.namaDokter, k.statusLanjut, idOrg);
            String idReq = cariIdSatuSehat(
                    "select id_medicationrequest from satu_sehat_medicationrequest where no_resep=? and kode_brng=? limit 1",
                    m.noResep, m.kodeBrng);
            if (isBlank(idReq)) {
                idReq = stableResourceId("MedicationRequest", noRawat, m.noResep, m.kodeBrng);
            }
            tambahEntryPut(entries, "MedicationRequest", idReq, medReq);
            medReqRefByKey.put(resepKey, "MedicationRequest/" + idReq);
            metas.add(metaResep("MedicationRequest", m.noResep, m.kodeBrng));
            if (stmtAdded.add(resepKey)) {
                ObjectNode medStmt = buatMedicationStatement(m, encounterReference, patientReference, k.namaPasien,
                        k.statusLanjut, idOrg);
                String idStmt = cariIdSatuSehat(
                        "select id_medicationstatement from satu_sehat_medicationstatement where no_resep=? and kode_brng=? limit 1",
                        m.noResep, m.kodeBrng);
                if (isBlank(idStmt)) {
                    idStmt = stableResourceId("MedicationStatement", noRawat, m.noResep, m.kodeBrng);
                }
                tambahEntryPut(entries, "MedicationStatement", idStmt, medStmt);
                metas.add(metaResep("MedicationStatement", m.noResep, m.kodeBrng));
            }
        }

        Set<String> dispAdded = new HashSet<>();
        for (DispenseData d : ambilSemuaDispense(noRawat)) {
            if (isBlank(d.obatCode)) {
                continue;
            }
            String resepKey = (isBlank(d.noResep) ? "-" : d.noResep) + "|" + (isBlank(d.kodeBrng) ? "-" : d.kodeBrng);
            String medReqRef = medReqRefByKey.get(resepKey);
            if (isBlank(medReqRef) && !isBlank(d.noResep) && !isBlank(d.kodeBrng)) {
                String idReq = cariIdSatuSehat(
                        "select id_medicationrequest from satu_sehat_medicationrequest where no_resep=? and kode_brng=? limit 1",
                        d.noResep, d.kodeBrng);
                if (!isBlank(idReq)) {
                    medReqRef = "MedicationRequest/" + idReq;
                    medReqRefByKey.put(resepKey, medReqRef);
                }
            }
            if (isBlank(medReqRef) && !isBlank(d.noResep) && !isBlank(d.kodeBrng) && reqAdded.add(resepKey)) {
                MedikasiData m = medikasiDariDispense(d);
                m.fallbackAuthoredOn = k.mulai;
                ObjectNode medReq = buatMedicationRequest(m, encounterReference, patientReference, k.namaPasien,
                        k.idDokter, k.namaDokter, k.statusLanjut, idOrg);
                String idReq = cariIdSatuSehat(
                        "select id_medicationrequest from satu_sehat_medicationrequest where no_resep=? and kode_brng=? limit 1",
                        m.noResep, m.kodeBrng);
                if (isBlank(idReq)) idReq = stableResourceId("MedicationRequest", noRawat, m.noResep, m.kodeBrng);
                tambahEntryPut(entries, "MedicationRequest", idReq, medReq);
                metas.add(metaResep("MedicationRequest", m.noResep, m.kodeBrng));
                medReqRef = "MedicationRequest/" + idReq;
                medReqRefByKey.put(resepKey, medReqRef);
                if (stmtAdded.add(resepKey)) {
                    ObjectNode medStmt = buatMedicationStatement(m, encounterReference, patientReference, k.namaPasien,
                            k.statusLanjut, idOrg);
                    String idStmt = cariIdSatuSehat(
                            "select id_medicationstatement from satu_sehat_medicationstatement where no_resep=? and kode_brng=? limit 1",
                            m.noResep, m.kodeBrng);
                    if (isBlank(idStmt)) idStmt = stableResourceId("MedicationStatement", noRawat, m.noResep, m.kodeBrng);
                    tambahEntryPut(entries, "MedicationStatement", idStmt, medStmt);
                    metas.add(metaResep("MedicationStatement", m.noResep, m.kodeBrng));
                }
            }
            if (isBlank(medReqRef)) {
                continue;
            }
            if (isBlank(d.tglPemberian) || d.tglPemberian.startsWith("0000-")) {
                continue;
            }
            String noBatch = isBlank(d.noBatch) ? "" : d.noBatch;
            String noFaktur = isBlank(d.noFaktur) ? "" : d.noFaktur;
            String dispKey = noRawat + "|" + d.tglPemberian + "|" + d.jamPemberian + "|" + d.kodeBrng
                    + "|" + noBatch + "|" + noFaktur + "|" + (isBlank(d.noResep) ? "-" : d.noResep);
            if (!dispAdded.add(dispKey)) {
                continue;
            }
            ObjectNode disp = buatMedicationDispense(d, encounterReference, patientReference, k.namaPasien,
                    k.idDokter, k.namaDokter, k.statusLanjut, idOrg, medReqRef);
            String idDisp = cariIdSatuSehat(
                    "select id_medicationdispanse from satu_sehat_medicationdispense "
                            + "where no_rawat=? and tgl_perawatan=? and jam=? and kode_brng=? and no_batch=? and no_faktur=? limit 1",
                    noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            if (isBlank(idDisp)) {
                idDisp = stableResourceId("MedicationDispense", noRawat, d.noResep, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            }
            tambahEntryPut(entries, "MedicationDispense", idDisp, disp);
            metas.add(metaPemberian("MedicationDispense", noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur));
            ObjectNode adm = buatMedicationAdministration(d, encounterReference, patientReference, k.namaPasien,
                    k.idDokter, k.namaDokter, k.statusLanjut, idOrg, medReqRef);
            String idAdm = cariIdSatuSehat(
                    "select id_medicationadministration from satu_sehat_medicationadministration "
                            + "where no_rawat=? and tgl_perawatan=? and jam=? and kode_brng=? and no_batch=? and no_faktur=? limit 1",
                    noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            if (isBlank(idAdm)) {
                idAdm = stableResourceId("MedicationAdministration", noRawat, d.noResep, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            }
            tambahEntryPut(entries, "MedicationAdministration", idAdm, adm);
            metas.add(metaPemberian("MedicationAdministration", noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur));
        }

        if (entries.size() == 0) return null;
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (isBlank(idEncounter)) {
            return;   // tunggu Encounter dulu
        }
        Konteks k = ambilKonteks(noRawat);
        if (k == null || isBlank(k.idPasien)) {
            System.out.println("Notifikasi Obat : ID pasien SATUSEHAT belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        if (isBlank(k.idDokter)) {
            System.out.println("Notifikasi Obat : ID dokter SATUSEHAT belum ada untuk no_rawat " + noRawat
                    + ". MedicationRequest/Dispense butuh Practitioner. Dilewati.");
            return;
        }

        String encounterReference = "Encounter/" + idEncounter;
        String patientReference = "Patient/" + k.idPasien;
        String idOrg = koneksiDB.IDSATUSEHAT();

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        List<EntryMeta> metas = new ArrayList<>();
        Map<String, Boolean> medAdaCache = new HashMap<>();
        Map<String, String> medReqRefByKey = new HashMap<>();

        // ---------- Resep: MedicationRequest + MedicationStatement ----------
        Set<String> reqAdded = new HashSet<>();
        Set<String> stmtAdded = new HashSet<>();
        for (MedikasiData m : ambilSemuaResep(noRawat)) {
            if (isBlank(m.obatCode) || isBlank(m.noResep) || isBlank(m.kodeBrng)) {
                continue;
            }
            String resepKey = m.noResep + "|" + m.kodeBrng;
            if (!reqAdded.add(resepKey)) {
                continue;
            }
            m.fallbackAuthoredOn = k.mulai;

            // MedicationRequest
            ObjectNode medReq = buatMedicationRequest(m, encounterReference, patientReference, k.namaPasien,
                    k.idDokter, k.namaDokter, k.statusLanjut, idOrg);
            String idReq = cariIdSatuSehat(
                    "select id_medicationrequest from satu_sehat_medicationrequest where no_resep=? and kode_brng=? limit 1",
                    m.noResep, m.kodeBrng);
            if (isBlank(idReq)) {
                idReq = stableResourceId("MedicationRequest", noRawat, m.noResep, m.kodeBrng);
            }
            tambahEntryPut(entries, "MedicationRequest", idReq, medReq);
            medReqRefByKey.put(resepKey, "MedicationRequest/" + idReq);
            metas.add(metaResep("MedicationRequest", m.noResep, m.kodeBrng));

            // MedicationStatement
            if (stmtAdded.add(resepKey)) {
                ObjectNode medStmt = buatMedicationStatement(m, encounterReference, patientReference, k.namaPasien,
                        k.statusLanjut, idOrg);
                String idStmt = cariIdSatuSehat(
                        "select id_medicationstatement from satu_sehat_medicationstatement where no_resep=? and kode_brng=? limit 1",
                        m.noResep, m.kodeBrng);
                if (isBlank(idStmt)) {
                    idStmt = stableResourceId("MedicationStatement", noRawat, m.noResep, m.kodeBrng);
                }
                tambahEntryPut(entries, "MedicationStatement", idStmt, medStmt);
                metas.add(metaResep("MedicationStatement", m.noResep, m.kodeBrng));
            }
        }

        // ---------- Pemberian: MedicationDispense + MedicationAdministration ----------
        Set<String> dispAdded = new HashSet<>();
        for (DispenseData d : ambilSemuaDispense(noRawat)) {
            if (isBlank(d.obatCode)) {
                continue;
            }
            String resepKey = (isBlank(d.noResep) ? "-" : d.noResep) + "|" + (isBlank(d.kodeBrng) ? "-" : d.kodeBrng);
            String medReqRef = medReqRefByKey.get(resepKey);
            if (isBlank(medReqRef) && !isBlank(d.noResep) && !isBlank(d.kodeBrng)) {
                String idReq = cariIdSatuSehat(
                        "select id_medicationrequest from satu_sehat_medicationrequest where no_resep=? and kode_brng=? limit 1",
                        d.noResep, d.kodeBrng);
                if (!isBlank(idReq)) {
                    medReqRef = "MedicationRequest/" + idReq;
                    medReqRefByKey.put(resepKey, medReqRef);
                }
            }
            // Obat diberikan tanpa resep_dokter (mis. obat ranap/floor-stock) -> buat MedicationRequest
            // (+Statement) dari data pemberian agar Dispense/Administration punya authorizingPrescription/request.
            if (isBlank(medReqRef) && !isBlank(d.noResep) && !isBlank(d.kodeBrng) && reqAdded.add(resepKey)) {
                MedikasiData m = medikasiDariDispense(d);
                m.fallbackAuthoredOn = k.mulai;
                ObjectNode medReq = buatMedicationRequest(m, encounterReference, patientReference, k.namaPasien,
                        k.idDokter, k.namaDokter, k.statusLanjut, idOrg);
                String idReq = cariIdSatuSehat(
                        "select id_medicationrequest from satu_sehat_medicationrequest where no_resep=? and kode_brng=? limit 1",
                        m.noResep, m.kodeBrng);
                if (isBlank(idReq)) idReq = stableResourceId("MedicationRequest", noRawat, m.noResep, m.kodeBrng);
                tambahEntryPut(entries, "MedicationRequest", idReq, medReq);
                metas.add(metaResep("MedicationRequest", m.noResep, m.kodeBrng));
                medReqRef = "MedicationRequest/" + idReq;
                medReqRefByKey.put(resepKey, medReqRef);
                if (stmtAdded.add(resepKey)) {
                    ObjectNode medStmt = buatMedicationStatement(m, encounterReference, patientReference, k.namaPasien,
                            k.statusLanjut, idOrg);
                    String idStmt = cariIdSatuSehat(
                            "select id_medicationstatement from satu_sehat_medicationstatement where no_resep=? and kode_brng=? limit 1",
                            m.noResep, m.kodeBrng);
                    if (isBlank(idStmt)) idStmt = stableResourceId("MedicationStatement", noRawat, m.noResep, m.kodeBrng);
                    tambahEntryPut(entries, "MedicationStatement", idStmt, medStmt);
                    metas.add(metaResep("MedicationStatement", m.noResep, m.kodeBrng));
                }
            }
            if (isBlank(medReqRef)) {
                System.out.println("Notifikasi Obat : MedicationDispense " + d.kodeBrng + " (resep=" + d.noResep
                        + ") dilewati. no_resep kosong sehingga MedicationRequest tak bisa dibuat.");
                continue;
            }
            if (isBlank(d.tglPemberian) || d.tglPemberian.startsWith("0000-")) {
                continue;
            }
            String noBatch = isBlank(d.noBatch) ? "" : d.noBatch;
            String noFaktur = isBlank(d.noFaktur) ? "" : d.noFaktur;
            String dispKey = noRawat + "|" + d.tglPemberian + "|" + d.jamPemberian + "|" + d.kodeBrng
                    + "|" + noBatch + "|" + noFaktur + "|" + (isBlank(d.noResep) ? "-" : d.noResep);
            if (!dispAdded.add(dispKey)) {
                continue;
            }

            // MedicationDispense
            ObjectNode disp = buatMedicationDispense(d, encounterReference, patientReference, k.namaPasien,
                    k.idDokter, k.namaDokter, k.statusLanjut, idOrg, medReqRef);
            String idDisp = cariIdSatuSehat(
                    "select id_medicationdispanse from satu_sehat_medicationdispense "
                            + "where no_rawat=? and tgl_perawatan=? and jam=? and kode_brng=? and no_batch=? and no_faktur=? limit 1",
                    noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            if (isBlank(idDisp)) {
                idDisp = stableResourceId("MedicationDispense", noRawat, d.noResep, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            }
            tambahEntryPut(entries, "MedicationDispense", idDisp, disp);
            metas.add(metaPemberian("MedicationDispense", noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur));

            // MedicationAdministration
            ObjectNode adm = buatMedicationAdministration(d, encounterReference, patientReference, k.namaPasien,
                    k.idDokter, k.namaDokter, k.statusLanjut, idOrg, medReqRef);
            String idAdm = cariIdSatuSehat(
                    "select id_medicationadministration from satu_sehat_medicationadministration "
                            + "where no_rawat=? and tgl_perawatan=? and jam=? and kode_brng=? and no_batch=? and no_faktur=? limit 1",
                    noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            if (isBlank(idAdm)) {
                idAdm = stableResourceId("MedicationAdministration", noRawat, d.noResep, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur);
            }
            tambahEntryPut(entries, "MedicationAdministration", idAdm, adm);
            metas.add(metaPemberian("MedicationAdministration", noRawat, d.tglPemberian, d.jamPemberian, d.kodeBrng, noBatch, noFaktur));
        }

        if (entries.size() == 0) {
            System.out.println("Notifikasi Obat : tidak ada resource obat yang bisa dikirim untuk no_rawat " + noRawat + ".");
            return;
        }

        // Pasangkan entry+meta lalu urutkan: MedicationRequest dulu, lalu Statement, Dispense, Administration.
        // Tujuan: Dispense/Administration (mereferensi MedicationRequest by id) selalu di chunk yang sama/
        // setelah MedicationRequest-nya tercipta -> referensi aman walau bundle dipecah.
        List<Object[]> pairs = new ArrayList<>();
        for (int i = 0; i < metas.size(); i++) {
            pairs.add(new Object[]{(ObjectNode) entries.get(i), metas.get(i)});
        }
        pairs.sort((a, b) -> prioritasResource((ObjectNode) a[0]) - prioritasResource((ObjectNode) b[0]));

        // Pecah jadi chunk kecil supaya tidak 502 (gateway timeout untuk bundle besar).
        final int CHUNK = 25;
        int sukses = 0;
        System.out.println("URL Obat : " + link);
        System.out.println("Total resource obat : " + pairs.size() + " -> dikirim per " + CHUNK + " (chunk).");
        for (int start = 0; start < pairs.size(); start += CHUNK) {
            int end = Math.min(start + CHUNK, pairs.size());
            ObjectNode b = mapper.createObjectNode();
            b.put("resourceType", "Bundle");
            b.put("type", "transaction");
            ArrayNode es = b.putArray("entry");
            List<EntryMeta> ms = new ArrayList<>();
            for (int i = start; i < end; i++) {
                es.add((ObjectNode) pairs.get(i)[0]);
                ms.add((EntryMeta) pairs.get(i)[1]);
            }
            int nomor = (start / CHUNK) + 1;
            try {
                sukses += kirimBatch(b, ms, nomor);
            } catch (Exception ex) {
                System.out.println("Notifikasi Obat : chunk " + nomor + " gagal (" + ex + ").");
            }
        }
        System.out.println("Notifikasi Obat : selesai. " + sukses + "/" + pairs.size() + " resource obat berhasil (2xx).");
    }

    /** Kirim satu chunk bundle obat; simpan id response; balikkan jumlah resource 2xx. */
    private int kirimBatch(ObjectNode bundle, List<EntryMeta> metas, int nomor) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Obat chunk " + nomor + " Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Obat OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Obat Body: " + body);
            }
            throw ex;
        }
        int ok = 0;
        JsonNode entryResponse = mapper.readTree(hasil).path("entry");
        for (int x = 0; x < metas.size(); x++) {
            if (!entryResponse.isArray() || entryResponse.size() <= x) {
                break;
            }
            JsonNode resp = entryResponse.get(x).path("response");
            if (resp.path("status").asText().startsWith("2")) ok++;
            String idResource = extractId(resp.path("location").asText());
            if (isBlank(idResource)) idResource = resp.path("resourceID").asText();
            if (isBlank(idResource)) idResource = entryResponse.get(x).path("resource").path("id").asText();
            if (isBlank(idResource)) {
                continue;
            }
            EntryMeta meta = metas.get(x);
            switch (meta.tipe) {
                case "MedicationRequest":
                    simpan3("satu_sehat_medicationrequest", "id_medicationrequest", "no_resep", "kode_brng",
                            meta.noResep, meta.kodeBrng, idResource);
                    break;
                case "MedicationStatement":
                    simpan3("satu_sehat_medicationstatement", "id_medicationstatement", "no_resep", "kode_brng",
                            meta.noResep, meta.kodeBrng, idResource);
                    break;
                case "MedicationDispense":
                    simpanPemberian("satu_sehat_medicationdispense", "id_medicationdispanse",
                            meta.noRawat, meta.tgl, meta.jam, meta.kodeBrng, meta.noBatch, meta.noFaktur, idResource);
                    break;
                case "MedicationAdministration":
                    simpanPemberian("satu_sehat_medicationadministration", "id_medicationadministration",
                            meta.noRawat, meta.tgl, meta.jam, meta.kodeBrng, meta.noBatch, meta.noFaktur, idResource);
                    break;
                default:
            }
        }
        System.out.println("Result Obat chunk " + nomor + " : " + ok + "/" + metas.size() + " resource 2xx.");
        return ok;
    }

    /** Urutan kirim: MedicationRequest(0) -> Statement(1) -> Dispense(2) -> Administration(3). */
    private int prioritasResource(ObjectNode entry) {
        String rt = entry.path("resource").path("resourceType").asText();
        switch (rt) {
            case "MedicationRequest": return 0;
            case "MedicationStatement": return 1;
            case "MedicationDispense": return 2;
            case "MedicationAdministration": return 3;
            default: return 4;
        }
    }

    // ====================== Builder resource ======================

    private ObjectNode buatMedicationRequest(MedikasiData m, String encounterReference, String patientReference,
            String namaPasien, String idDokter, String namaDokter, String statusLanjut, String idOrg) {
        int doseValue = parseSignaFreq(m.aturanPakai, 0);
        int frequency = parseSignaFreq(m.aturanPakai, 1);
        if (doseValue <= 0) doseValue = 1;
        if (frequency <= 0) frequency = 1;
        boolean ranap = "Ranap".equals(statusLanjut);

        ObjectNode req = mapper.createObjectNode();
        req.put("resourceType", "MedicationRequest");
        ArrayNode identifier = req.putArray("identifier");
        ObjectNode iden1 = identifier.addObject();
        iden1.put("system", "http://sys-ids.kemkes.go.id/prescription/" + idOrg);
        iden1.put("use", "official");
        iden1.put("value", isBlank(m.noResep) ? "-" : m.noResep);
        ObjectNode iden2 = identifier.addObject();
        iden2.put("system", "http://sys-ids.kemkes.go.id/prescription-item/" + idOrg);
        iden2.put("use", "official");
        iden2.put("value", isBlank(m.kodeBrng) ? "-" : m.kodeBrng);

        req.put("status", "completed");
        req.put("intent", "order");
        req.put("priority", "routine");
        ObjectNode catCoding = req.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/medicationrequest-category");
        catCoding.put("code", ranap ? "inpatient" : "outpatient");
        catCoding.put("display", ranap ? "Inpatient" : "Outpatient");
        ObjectNode cotCoding = req.putObject("courseOfTherapyType").putArray("coding").addObject();
        cotCoding.put("system", "http://terminology.hl7.org/CodeSystem/medicationrequest-course-of-therapy");
        cotCoding.put("code", "acute");
        cotCoding.put("display", "Short course (acute) therapy");

        lampirkanMedication(req, m);

        ObjectNode subject = req.putObject("subject");
        subject.put("reference", patientReference);
        subject.put("display", namaPasien);
        req.putObject("encounter").put("reference", encounterReference);

        String authoredOn = bangunDateTimeAman(m.tglPeresepan, m.jamPeresepan);
        if (isBlank(authoredOn)) authoredOn = m.fallbackAuthoredOn;
        if (!isBlank(authoredOn)) req.put("authoredOn", authoredOn);

        ObjectNode requester = req.putObject("requester");
        requester.put("reference", "Practitioner/" + idDokter);
        requester.put("display", namaDokter);

        ObjectNode dose = req.putArray("dosageInstruction").addObject();
        dose.put("sequence", 1);
        dose.put("patientInstruction", isBlank(m.aturanPakai) ? "-" : m.aturanPakai);
        ObjectNode addInstr = dose.putArray("additionalInstruction").addObject().putArray("coding").addObject();
        addInstr.put("system", "http://snomed.info/sct");
        addInstr.put("code", "421769005");
        addInstr.put("display", "Follow directions");
        ObjectNode repeat = dose.putObject("timing").putObject("repeat");
        repeat.put("frequency", frequency);
        repeat.put("period", 1);
        repeat.put("periodUnit", "d");
        isiRoute(dose, m.routeCode, m.routeSystem, m.routeDisplay);
        ObjectNode dr = dose.putArray("doseAndRate").addObject();
        ObjectNode drType = dr.putObject("type").putArray("coding").addObject();
        drType.put("system", "http://terminology.hl7.org/CodeSystem/dose-rate-type");
        drType.put("code", "ordered");
        drType.put("display", "Ordered");
        isiQuantityObat(dr.putObject("doseQuantity"), doseValue, m.denominatorCode);

        ObjectNode dispenseRequest = req.putObject("dispenseRequest");
        dispenseRequest.put("numberOfRepeatsAllowed", 0);
        Double jumlah = parseAngka(m.jml);
        isiQuantityObat(dispenseRequest.putObject("quantity"), jumlah == null ? 1 : jumlah, m.denominatorCode);
        dispenseRequest.putObject("performer").put("reference", "Organization/" + idOrg);

        req.putObject("substitution").put("allowedBoolean", false);
        return req;
    }

    private ObjectNode buatMedicationStatement(MedikasiData m, String encounterReference, String patientReference,
            String namaPasien, String statusLanjut, String idOrg) {
        int doseValue = parseSignaFreq(m.aturanPakai, 0);
        int frequency = parseSignaFreq(m.aturanPakai, 1);
        if (doseValue <= 0) doseValue = 1;
        if (frequency <= 0) frequency = 1;
        boolean ranap = "Ranap".equals(statusLanjut);

        ObjectNode stmt = mapper.createObjectNode();
        stmt.put("resourceType", "MedicationStatement");
        ObjectNode iden = stmt.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/medicationstatement/" + idOrg);
        iden.put("use", "official");
        iden.put("value", (isBlank(m.noResep) ? "-" : m.noResep) + "-" + (isBlank(m.kodeBrng) ? "-" : m.kodeBrng));
        stmt.put("status", "completed");
        ObjectNode catCoding = stmt.putObject("category").putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/medication-statement-category");
        catCoding.put("code", ranap ? "inpatient" : "outpatient");
        catCoding.put("display", ranap ? "Inpatient" : "Outpatient");
        lampirkanMedication(stmt, m);
        ObjectNode subject = stmt.putObject("subject");
        subject.put("reference", patientReference);
        subject.put("display", namaPasien);
        ObjectNode dose = stmt.putArray("dosage").addObject();
        dose.put("text", isBlank(m.aturanPakai) ? "-" : m.aturanPakai);
        ObjectNode repeat = dose.putObject("timing").putObject("repeat");
        repeat.put("frequency", frequency);
        repeat.put("period", 1);
        repeat.put("periodUnit", "d");
        isiRoute(dose, m.routeCode, m.routeSystem, m.routeDisplay);
        ObjectNode doseQty = dose.putArray("doseAndRate").addObject().putObject("doseQuantity");
        isiQuantityObat(doseQty, doseValue, m.denominatorCode);
        String dateAsserted = bangunDateTimeAman(m.tglPeresepan, m.jamPeresepan);
        if (isBlank(dateAsserted)) dateAsserted = m.fallbackAuthoredOn;
        if (!isBlank(dateAsserted)) stmt.put("dateAsserted", dateAsserted);
        ObjectNode infoSource = stmt.putObject("informationSource");
        infoSource.put("reference", patientReference);
        infoSource.put("display", namaPasien);
        stmt.putObject("context").put("reference", encounterReference);
        stmt.putArray("note").addObject().put("text",
                "Pasien sudah memahami aturan pakai yang dijelaskan oleh petugas & Obat sudah diserahkan ke pasien");
        return stmt;
    }

    private ObjectNode buatMedicationDispense(DispenseData d, String encounterReference, String patientReference,
            String namaPasien, String idDokter, String namaDokter, String statusLanjut, String idOrg,
            String medicationRequestRef) {
        int doseValue = parseSignaFreq(d.aturanPakai, 0);
        int frequency = parseSignaFreq(d.aturanPakai, 1);
        if (doseValue <= 0) doseValue = 1;
        if (frequency <= 0) frequency = 1;
        boolean ranap = "Ranap".equals(statusLanjut);

        ObjectNode disp = mapper.createObjectNode();
        disp.put("resourceType", "MedicationDispense");
        ArrayNode identifier = disp.putArray("identifier");
        ObjectNode iden1 = identifier.addObject();
        iden1.put("system", "http://sys-ids.kemkes.go.id/prescription/" + idOrg);
        iden1.put("use", "official");
        iden1.put("value", isBlank(d.noResep) ? "-" : d.noResep);
        ObjectNode iden2 = identifier.addObject();
        iden2.put("system", "http://sys-ids.kemkes.go.id/prescription-item/" + idOrg);
        iden2.put("use", "official");
        iden2.put("value", isBlank(d.kodeBrng) ? "-" : d.kodeBrng);
        disp.put("status", "completed");
        ObjectNode catCoding = disp.putObject("category").putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/fhir/CodeSystem/medicationdispense-category");
        catCoding.put("code", ranap ? "inpatient" : "outpatient");
        catCoding.put("display", ranap ? "Inpatient" : "Outpatient");
        lampirkanMedicationD(disp, d);
        ObjectNode subject = disp.putObject("subject");
        subject.put("reference", patientReference);
        subject.put("display", namaPasien);
        disp.putObject("context").put("reference", encounterReference);
        ObjectNode actor = disp.putArray("performer").addObject().putObject("actor");
        actor.put("reference", "Practitioner/" + idDokter);
        actor.put("display", namaDokter);
        ObjectNode location = disp.putObject("location");
        if (!isBlank(d.idLokasiDepo)) {
            location.put("reference", "Location/" + d.idLokasiDepo);
            location.put("display", isBlank(d.namaDepo) ? "Depo Farmasi" : d.namaDepo);
        }
        if (!isBlank(medicationRequestRef)) {
            disp.putArray("authorizingPrescription").addObject().put("reference", medicationRequestRef);
        }
        Double jumlah = parseAngka(d.jml);
        isiQuantityObat(disp.putObject("quantity"), jumlah == null ? 1 : jumlah, d.denominatorCode);
        String preparedDt = bangunDateTimeAman(d.tglPeresepan, d.jamPeresepan);
        String handedDt = bangunDateTimeAman(d.tglPemberian, d.jamPemberian);
        if (isBlank(preparedDt)) preparedDt = handedDt;
        if (isBlank(handedDt)) handedDt = preparedDt;
        if (!isBlank(preparedDt)) disp.put("whenPrepared", preparedDt);
        if (!isBlank(handedDt)) disp.put("whenHandedOver", handedDt);
        ObjectNode dose = disp.putArray("dosageInstruction").addObject();
        dose.put("sequence", 1);
        dose.put("text", isBlank(d.aturanPakai) ? "-" : d.aturanPakai);
        ObjectNode repeat = dose.putObject("timing").putObject("repeat");
        repeat.put("frequency", frequency);
        repeat.put("period", 1);
        repeat.put("periodUnit", "d");
        isiRoute(dose, d.routeCode, d.routeSystem, d.routeDisplay);
        ObjectNode doseQty = dose.putArray("doseAndRate").addObject().putObject("doseQuantity");
        isiQuantityObat(doseQty, doseValue, d.denominatorCode);
        disp.putObject("substitution").put("wasSubstituted", false);
        return disp;
    }

    private ObjectNode buatMedicationAdministration(DispenseData d, String encounterReference, String patientReference,
            String namaPasien, String idDokter, String namaDokter, String statusLanjut, String idOrg,
            String medicationRequestRef) {
        int doseValue = parseSignaFreq(d.aturanPakai, 0);
        if (doseValue <= 0) doseValue = 1;
        boolean ranap = "Ranap".equals(statusLanjut);

        ObjectNode adm = mapper.createObjectNode();
        adm.put("resourceType", "MedicationAdministration");
        ObjectNode iden = adm.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/medicationadministration/" + idOrg);
        iden.put("use", "official");
        iden.put("value", (isBlank(d.noRawat) ? "-" : d.noRawat) + "-" + (isBlank(d.tglPemberian) ? "-" : d.tglPemberian)
                + "-" + (isBlank(d.jamPemberian) ? "-" : d.jamPemberian) + "-" + (isBlank(d.kodeBrng) ? "-" : d.kodeBrng)
                + "-" + (isBlank(d.noBatch) ? "-" : d.noBatch) + "-" + (isBlank(d.noFaktur) ? "-" : d.noFaktur));
        adm.put("status", "completed");
        ObjectNode catCoding = adm.putObject("category").putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/medication-admin-category");
        catCoding.put("code", ranap ? "inpatient" : "outpatient");
        catCoding.put("display", ranap ? "Inpatient" : "Outpatient");
        lampirkanMedicationD(adm, d);
        ObjectNode subject = adm.putObject("subject");
        subject.put("reference", patientReference);
        subject.put("display", namaPasien);
        adm.putObject("context").put("reference", encounterReference);
        String effective = bangunDateTimeAman(d.tglPemberian, d.jamPemberian);
        if (isBlank(effective)) effective = bangunDateTimeAman(d.tglPeresepan, d.jamPeresepan);
        if (!isBlank(effective)) adm.put("effectiveDateTime", effective);
        ObjectNode actor = adm.putArray("performer").addObject().putObject("actor");
        actor.put("reference", "Practitioner/" + idDokter);
        actor.put("display", namaDokter);
        if (!isBlank(medicationRequestRef)) {
            adm.putObject("request").put("reference", medicationRequestRef);
        }
        ObjectNode dosage = adm.putObject("dosage");
        dosage.put("text", isBlank(d.aturanPakai) ? "-" : d.aturanPakai);
        isiRoute(dosage, d.routeCode, d.routeSystem, d.routeDisplay);
        isiQuantityObat(dosage.putObject("dose"), doseValue, d.denominatorCode);
        return adm;
    }

    private void lampirkanMedication(ObjectNode resource, MedikasiData m) {
        lampirkanMedicationInti(resource, m.obatCode, m.obatSystem, m.obatDisplay,
                m.formCode, m.formSystem, m.formDisplay, m.kodeBrng);
    }

    private void lampirkanMedicationD(ObjectNode resource, DispenseData d) {
        lampirkanMedicationInti(resource, d.obatCode, d.obatSystem, d.obatDisplay,
                d.formCode, d.formSystem, d.formDisplay, d.kodeBrng);
    }

    /**
     * Bangun contained Medication (KFA code + form + MedicationType NC, tanpa ingredient — selaras
     * Medication yang didaftarkan SatuSehatKirimMedication) lalu set medicationReference = "#id".
     */
    private void lampirkanMedicationInti(ObjectNode resource, String code, String system, String display,
            String formCode, String formSystem, String formDisplay, String kodeBrng) {
        String containedId = "med-" + (isBlank(kodeBrng) ? "x" : kodeBrng.replaceAll("[^A-Za-z0-9]", ""));
        ObjectNode med = mapper.createObjectNode();
        med.put("resourceType", "Medication");
        med.put("id", containedId);
        ObjectNode iden = med.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/medication/" + koneksiDB.IDSATUSEHAT());
        iden.put("use", "official");
        iden.put("value", isBlank(kodeBrng) ? "-" : kodeBrng);
        ObjectNode coding = med.putObject("code").putArray("coding").addObject();
        coding.put("system", isBlank(system) ? "http://sys-ids.kemkes.go.id/kfa" : system);
        coding.put("code", code);
        coding.put("display", display);
        med.put("status", "active");
        if (!isBlank(formCode)) {
            ObjectNode fc = med.putObject("form").putArray("coding").addObject();
            fc.put("system", isBlank(formSystem) ? "http://terminology.kemkes.go.id/CodeSystem/medication-form" : formSystem);
            fc.put("code", formCode);
            fc.put("display", formDisplay);
        }
        ObjectNode ext = med.putArray("extension").addObject();
        ext.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/MedicationType");
        ObjectNode extCoding = ext.putObject("valueCodeableConcept").putArray("coding").addObject();
        extCoding.put("system", "http://terminology.kemkes.go.id/CodeSystem/medication-type");
        extCoding.put("code", "NC");
        extCoding.put("display", "Non-compound");

        resource.putArray("contained").add(med);
        ObjectNode medRef = resource.putObject("medicationReference");
        medRef.put("reference", "#" + containedId);
        medRef.put("display", isBlank(display) ? kodeBrng : display);
    }

    private void isiRoute(ObjectNode parent, String routeCode, String routeSystem, String routeDisplay) {
        String[] routeMap = normalisasiRouteBundle(routeCode, routeSystem, routeDisplay);
        if (!isBlank(routeMap[1])) {
            ObjectNode rc = parent.putObject("route").putArray("coding").addObject();
            rc.put("system", routeMap[0]);
            rc.put("code", routeMap[1]);
            rc.put("display", routeMap[2]);
        }
    }

    // ====================== Bundle helper ======================

    /** Selalu PUT (upsert) dengan id stabil/looked-up — pola idempotent SatuSehatKirimBundle. */
    private void tambahEntryPut(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        resource.put("id", id);
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    private EntryMeta metaResep(String tipe, String noResep, String kodeBrng) {
        EntryMeta m = new EntryMeta();
        m.tipe = tipe;
        m.noResep = noResep;
        m.kodeBrng = kodeBrng;
        return m;
    }

    private EntryMeta metaPemberian(String tipe, String noRawat, String tgl, String jam, String kodeBrng,
            String noBatch, String noFaktur) {
        EntryMeta m = new EntryMeta();
        m.tipe = tipe;
        m.noRawat = noRawat;
        m.tgl = tgl;
        m.jam = jam;
        m.kodeBrng = kodeBrng;
        m.noBatch = noBatch;
        m.noFaktur = noFaktur;
        return m;
    }

    // ====================== Panggilan SATUSEHAT (lookup) ======================

    private boolean cekMedicationAda(String idMedication, Map<String, Boolean> cache) {
        if (isBlank(idMedication)) return false;
        if (cache.containsKey(idMedication)) return cache.get(idMedication);
        boolean ada;
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            java.net.URI uri = java.net.URI.create(link + "/Medication/" + idMedication);
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            ada = mapper.readTree(hasil).path("resourceType").asText().equals("Medication");
        } catch (HttpClientErrorException e) {
            // 404/410 = benar-benar tidak ada di server -> skip resource ini saja.
            ada = false;
            System.out.println("Notifikasi Obat : Medication " + idMedication + " tidak ada di server => " + e.getStatusCode());
        } catch (Exception e) {
            // 429 ("No matching constant") / jaringan -> JANGAN false-skip; mapping sudah valid, anggap ada.
            ada = true;
            System.out.println("Notifikasi Obat : cek Medication " + idMedication + " error, dianggap ada (" + e + ").");
        }
        cache.put(idMedication, ada);
        return ada;
    }

    private String cariIdResourceServer(String resourceType, String system, String value) {
        if (isBlank(system) || isBlank(value)) return "";
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            String token = java.net.URLEncoder.encode(system + "|" + value, "UTF-8");
            java.net.URI uri = java.net.URI.create(link + "/" + resourceType + "?identifier=" + token + "&_count=1");
            JsonNode r = mapper.readTree(api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody());
            if (r.path("total").asInt(0) > 0) {
                JsonNode es = r.path("entry");
                if (es.isArray() && es.size() > 0) return nz(es.get(0).path("resource").path("id").asText());
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Obat cariIdResourceServer (" + resourceType + ") : " + e);
        }
        return "";
    }

    private String cariIdMedicationDispenseServer(String noResep, String kodeBrng, String idOrg) {
        if (isBlank(noResep) || isBlank(kodeBrng)) return "";
        try {
            String tokenResep = java.net.URLEncoder.encode(
                    "http://sys-ids.kemkes.go.id/prescription/" + idOrg + "|" + noResep, "UTF-8");
            String tokenItem = java.net.URLEncoder.encode(
                    "http://sys-ids.kemkes.go.id/prescription-item/" + idOrg + "|" + kodeBrng, "UTF-8");
            java.net.URI uri = java.net.URI.create(link + "/MedicationDispense?identifier=" + tokenResep
                    + "&identifier=" + tokenItem + "&_sort=-_lastUpdated&_count=1");
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            JsonNode r = mapper.readTree(api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody());
            if (r.path("total").asInt(0) > 0) {
                JsonNode es = r.path("entry");
                if (es.isArray() && es.size() > 0) return nz(es.get(0).path("resource").path("id").asText());
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Obat cariIdMedicationDispenseServer : " + e);
        }
        return "";
    }

    private String cariIdSatuSehat(String sql, String... params) {
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                p.setString(i + 1, params[i]);
            }
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return nz(r.getString(1)).trim();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Obat cariIdSatuSehat : " + e);
        }
        return "";
    }

    // ====================== Simpan id response ======================

    private void simpan3(String tabel, String kolomId, String k1, String k2, String v1, String v2, String idVal) {
        if (isBlank(v1) || isBlank(v2) || isBlank(idVal)) return;
        if (!Sequel.menyimpantf2(tabel, "?,?,?", tabel, 3, new String[]{v1, v2, idVal})) {
            Sequel.queryu2("update " + tabel + " set " + kolomId + "=? where " + k1 + "=? and " + k2 + "=?",
                    3, new String[]{idVal, v1, v2});
        }
    }

    private void simpanPemberian(String tabel, String kolomId, String noRawat, String tgl, String jam,
            String kodeBrng, String noBatch, String noFaktur, String idVal) {
        if (isBlank(idVal)) return;
        // REPLACE INTO dengan kolom eksplisit -> kolom default (mis. tgl_kirim) terisi otomatis,
        // hindari "Column count doesn't match" pada tabel yang punya kolom tambahan.
        Sequel.queryu2("replace into " + tabel
                + " (no_rawat,tgl_perawatan,jam,kode_brng,no_batch,no_faktur," + kolomId + ") values (?,?,?,?,?,?,?)",
                7, new String[]{noRawat, tgl, jam, kodeBrng, noBatch, noFaktur, idVal});
    }

    // ====================== Query data ======================

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = null;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select p.no_ktp as ktp_pasien, p.nm_pasien, rp.status_lanjut, rp.tgl_registrasi, rp.jam_reg, "
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
                    String sl = nz(r.getString("status_lanjut"));
                    k.statusLanjut = sl.toLowerCase().contains("ranap") ? "Ranap" : "Ralan";
                    k.namaDokter = nz(r.getString("nm_dokter"));
                    String ktpDokter = nz(r.getString("ktp_dokter"));
                    if (!isBlank(ktpDokter)) k.idDokter = nz(cek.tampilIDParktisi(ktpDokter));
                    k.mulai = bangunDateTimeAman(nz(r.getString("tgl_registrasi")), nz(r.getString("jam_reg")));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Obat ambilKonteks : " + e);
        }
        return k;
    }

    /** Konversi data pemberian -> data resep (untuk MedicationRequest on-the-fly bila tak ada resep_dokter). */
    private MedikasiData medikasiDariDispense(DispenseData d) {
        MedikasiData m = new MedikasiData();
        m.noResep = d.noResep;
        m.kodeBrng = d.kodeBrng;
        m.jml = d.jml;
        m.aturanPakai = d.aturanPakai;
        m.tglPeresepan = isBlank(d.tglPeresepan) ? d.tglPemberian : d.tglPeresepan;
        m.jamPeresepan = isBlank(d.jamPeresepan) ? d.jamPemberian : d.jamPeresepan;
        m.idMedication = d.idMedication;
        m.obatDisplay = d.obatDisplay;
        m.obatCode = d.obatCode;
        m.obatSystem = d.obatSystem;
        m.formCode = d.formCode;
        m.formSystem = d.formSystem;
        m.formDisplay = d.formDisplay;
        m.routeCode = d.routeCode;
        m.routeSystem = d.routeSystem;
        m.routeDisplay = d.routeDisplay;
        m.denominatorCode = d.denominatorCode;
        m.denominatorSystem = d.denominatorSystem;
        return m;
    }

    private List<MedikasiData> ambilSemuaResep(String noRawat) {
        List<MedikasiData> result = new ArrayList<>();
        String sql = "select resep_dokter.no_resep, resep_dokter.kode_brng, resep_dokter.jml, resep_dokter.aturan_pakai, "
                + "resep_obat.tgl_peresepan, resep_obat.jam_peresepan, "
                + "ifnull(databarang.nama_brng,'') as nama_brng, "
                + "ifnull(satu_sehat_mapping_obat.obat_code,'') as obat_code, "
                + "ifnull(satu_sehat_mapping_obat.obat_system,'') as obat_system, "
                + "ifnull(satu_sehat_mapping_obat.obat_display,'') as obat_display, "
                + "ifnull(satu_sehat_mapping_obat.form_code,'') as form_code, "
                + "ifnull(satu_sehat_mapping_obat.form_system,'') as form_system, "
                + "ifnull(satu_sehat_mapping_obat.form_display,'') as form_display, "
                + "ifnull(satu_sehat_mapping_obat.route_code,'') as route_code, "
                + "ifnull(satu_sehat_mapping_obat.route_system,'') as route_system, "
                + "ifnull(satu_sehat_mapping_obat.route_display,'') as route_display, "
                + "ifnull(satu_sehat_mapping_obat.denominator_code,'') as denominator_code, "
                + "ifnull(satu_sehat_mapping_obat.denominator_system,'') as denominator_system, "
                + "ifnull(satu_sehat_medication.id_medication,'') as id_medication "
                + "from resep_obat "
                + "inner join resep_dokter on resep_dokter.no_resep = resep_obat.no_resep "
                + "left join databarang on databarang.kode_brng = resep_dokter.kode_brng "
                + "left join satu_sehat_mapping_obat on satu_sehat_mapping_obat.kode_brng = resep_dokter.kode_brng "
                + "left join satu_sehat_medication on satu_sehat_medication.kode_brng = resep_dokter.kode_brng "
                + "where resep_obat.no_rawat=? order by resep_obat.tgl_peresepan asc, resep_obat.jam_peresepan asc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    MedikasiData d = new MedikasiData();
                    d.noResep = r.getString("no_resep");
                    d.kodeBrng = r.getString("kode_brng");
                    d.jml = r.getString("jml");
                    d.aturanPakai = r.getString("aturan_pakai");
                    d.tglPeresepan = r.getString("tgl_peresepan");
                    d.jamPeresepan = r.getString("jam_peresepan");
                    String mapDisplay = r.getString("obat_display");
                    String namaBrng = r.getString("nama_brng");
                    d.obatDisplay = !isBlank(mapDisplay) ? mapDisplay : (!isBlank(namaBrng) ? namaBrng : d.kodeBrng);
                    d.obatCode = r.getString("obat_code");
                    d.obatSystem = r.getString("obat_system");
                    d.formCode = r.getString("form_code");
                    d.formSystem = r.getString("form_system");
                    d.formDisplay = r.getString("form_display");
                    d.routeCode = r.getString("route_code");
                    d.routeSystem = r.getString("route_system");
                    d.routeDisplay = r.getString("route_display");
                    d.denominatorCode = r.getString("denominator_code");
                    d.denominatorSystem = r.getString("denominator_system");
                    d.idMedication = r.getString("id_medication");
                    if (isBlank(d.obatCode) || isBhp(d.obatCode, d.formCode)) {
                        continue;   // tanpa KFA valid / BHP (alkes) -> bukan Medication, dilewati
                    }
                    result.add(d);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Obat ambilSemuaResep : " + e);
        }
        return result;
    }

    private List<DispenseData> ambilSemuaDispense(String noRawat) {
        List<DispenseData> result = new ArrayList<>();
        String sql = "select detail_pemberian_obat.no_rawat, detail_pemberian_obat.kode_brng, "
                + "detail_pemberian_obat.tgl_perawatan as tgl_pemberian, detail_pemberian_obat.jam as jam_pemberian, "
                + "detail_pemberian_obat.jml, detail_pemberian_obat.no_batch, detail_pemberian_obat.no_faktur, "
                + "ifnull(resep_obat.tgl_peresepan,'') as tgl_peresepan, ifnull(resep_obat.jam_peresepan,'') as jam_peresepan, "
                + "ifnull(resep_obat.no_resep,'') as no_resep, ifnull(aturan_pakai.aturan,'') as aturan_pakai, "
                + "ifnull(databarang.nama_brng,'') as nama_brng, "
                + "ifnull(satu_sehat_mapping_obat.obat_code,'') as obat_code, "
                + "ifnull(satu_sehat_mapping_obat.obat_system,'') as obat_system, "
                + "ifnull(satu_sehat_mapping_obat.obat_display,'') as obat_display, "
                + "ifnull(satu_sehat_mapping_obat.form_code,'') as form_code, "
                + "ifnull(satu_sehat_mapping_obat.form_system,'') as form_system, "
                + "ifnull(satu_sehat_mapping_obat.form_display,'') as form_display, "
                + "ifnull(satu_sehat_mapping_obat.route_code,'') as route_code, "
                + "ifnull(satu_sehat_mapping_obat.route_system,'') as route_system, "
                + "ifnull(satu_sehat_mapping_obat.route_display,'') as route_display, "
                + "ifnull(satu_sehat_mapping_obat.denominator_code,'') as denominator_code, "
                + "ifnull(satu_sehat_mapping_obat.denominator_system,'') as denominator_system, "
                + "ifnull(satu_sehat_medication.id_medication,'') as id_medication, "
                + "ifnull(satu_sehat_mapping_lokasi_depo_farmasi.id_lokasi_satusehat,'') as id_lokasi_depo, "
                + "ifnull(bangsal.nm_bangsal,'') as nm_bangsal "
                + "from detail_pemberian_obat "
                + "left join resep_obat on resep_obat.no_rawat = detail_pemberian_obat.no_rawat "
                + "  and resep_obat.tgl_perawatan = detail_pemberian_obat.tgl_perawatan and resep_obat.jam = detail_pemberian_obat.jam "
                + "left join aturan_pakai on aturan_pakai.no_rawat = detail_pemberian_obat.no_rawat "
                + "  and aturan_pakai.tgl_perawatan = detail_pemberian_obat.tgl_perawatan and aturan_pakai.jam = detail_pemberian_obat.jam "
                + "  and aturan_pakai.kode_brng = detail_pemberian_obat.kode_brng "
                + "left join databarang on databarang.kode_brng = detail_pemberian_obat.kode_brng "
                + "left join satu_sehat_mapping_obat on satu_sehat_mapping_obat.kode_brng = detail_pemberian_obat.kode_brng "
                + "left join satu_sehat_medication on satu_sehat_medication.kode_brng = detail_pemberian_obat.kode_brng "
                + "left join bangsal on bangsal.kd_bangsal = detail_pemberian_obat.kd_bangsal "
                + "left join satu_sehat_mapping_lokasi_depo_farmasi on satu_sehat_mapping_lokasi_depo_farmasi.kd_bangsal = detail_pemberian_obat.kd_bangsal "
                + "where detail_pemberian_obat.no_rawat=? order by detail_pemberian_obat.tgl_perawatan asc, detail_pemberian_obat.jam asc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    DispenseData d = new DispenseData();
                    d.noRawat = r.getString("no_rawat");
                    d.kodeBrng = r.getString("kode_brng");
                    d.tglPemberian = r.getString("tgl_pemberian");
                    d.jamPemberian = r.getString("jam_pemberian");
                    d.jml = r.getString("jml");
                    d.noBatch = r.getString("no_batch");
                    d.noFaktur = r.getString("no_faktur");
                    d.tglPeresepan = r.getString("tgl_peresepan");
                    d.jamPeresepan = r.getString("jam_peresepan");
                    d.noResep = r.getString("no_resep");
                    d.aturanPakai = r.getString("aturan_pakai");
                    String mapDisplay = r.getString("obat_display");
                    String namaBrng = r.getString("nama_brng");
                    d.obatDisplay = !isBlank(mapDisplay) ? mapDisplay : (!isBlank(namaBrng) ? namaBrng : d.kodeBrng);
                    d.obatCode = r.getString("obat_code");
                    d.obatSystem = r.getString("obat_system");
                    d.formCode = r.getString("form_code");
                    d.formSystem = r.getString("form_system");
                    d.formDisplay = r.getString("form_display");
                    d.routeCode = r.getString("route_code");
                    d.routeSystem = r.getString("route_system");
                    d.routeDisplay = r.getString("route_display");
                    d.denominatorCode = r.getString("denominator_code");
                    d.denominatorSystem = r.getString("denominator_system");
                    d.idMedication = r.getString("id_medication");
                    d.idLokasiDepo = r.getString("id_lokasi_depo");
                    d.namaDepo = r.getString("nm_bangsal");
                    if (isBlank(d.obatCode) || isBhp(d.obatCode, d.formCode)) {
                        continue;   // tanpa KFA valid / BHP (alkes) -> bukan Medication, dilewati
                    }
                    result.add(d);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Obat ambilSemuaDispense : " + e);
        }
        return result;
    }

    // ====================== Util ======================

    private int parseSignaFreq(String aturanPakai, int idx) {
        if (isBlank(aturanPakai)) return 1;
        String teks = aturanPakai.toLowerCase().trim();
        Matcher m = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)").matcher(teks);
        if (m.find()) return Integer.parseInt(m.group(1));
        Matcher satuKali = Pattern.compile("(\\d+)\\s*x").matcher(teks);
        if (satuKali.find()) return Integer.parseInt(satuKali.group(1));
        return 1;
    }

    private String normalisasiUnitObat(String denominatorCode) {
        String kode = denominatorCode == null ? "" : denominatorCode.trim().toUpperCase();
        if (kode.equals("TAB") || kode.equals("TABLET")) return "tablet";
        if (kode.equals("CAP") || kode.equals("CAPS") || kode.equals("KAPSUL") || kode.equals("KAP")) return "capsule";
        if (kode.equals("ML")) return "mL";
        if (kode.equals("MG")) return "mg";
        if (kode.equals("G") || kode.equals("GR") || kode.equals("GRAM")) return "g";
        if (kode.equals("AMP") || kode.equals("AMPUL") || kode.equals("AMPULLE")) return "ampoule";
        if (kode.equals("BTL") || kode.equals("BOTOL") || kode.equals("BOTTLE")) return "bottle";
        if (kode.equals("VIAL") || kode.equals("VL")) return "vial";
        if (kode.equals("PCS") || kode.equals("PC") || kode.equals("PIECE") || kode.equals("BUAH")) return "item";
        if (kode.equals("SACHET") || kode.equals("SCT")) return "sachet";
        if (kode.equals("STRIP")) return "strip";
        return "item";
    }

    /** v3-orderableDrugForm universal (TAB/CAP dipertahankan, sisanya fallback TAB) — valid di SATUSEHAT. */
    private String[] mapSatuSehatQuantityCode(String denominatorCode) {
        String k = denominatorCode == null ? "" : denominatorCode.trim().toUpperCase();
        String V3 = "http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm";
        if (k.equals("TAB") || k.equals("TABLET")) return new String[]{V3, "TAB"};
        if (k.equals("CAP") || k.equals("CAPS") || k.equals("KAPSUL") || k.equals("KAP")) return new String[]{V3, "CAP"};
        return new String[]{V3, "TAB"};
    }

    private void isiQuantityObat(ObjectNode qty, double value, String denominatorCode) {
        String[] sysCode = mapSatuSehatQuantityCode(denominatorCode);
        qty.put("value", value <= 0 ? 1 : value);
        qty.put("unit", normalisasiUnitObat(denominatorCode));
        if (!sysCode[0].isEmpty() && !sysCode[1].isEmpty()) {
            qty.put("system", sysCode[0]);
            qty.put("code", sysCode[1]);
        }
    }

    private String[] normalisasiRouteBundle(String routeCode, String routeSystem, String routeDisplay) {
        String kode = routeCode == null ? "" : routeCode.trim().toUpperCase();
        String system = routeSystem == null ? "" : routeSystem.trim();
        String display = routeDisplay == null ? "" : routeDisplay.trim();
        String dispUpper = display.toUpperCase();
        if (kode.equals("O") || kode.equals("ORAL") || dispUpper.contains("ORAL"))
            return new String[]{"http://www.whocc.no/atc", "O", "Oral"};
        if (kode.equals("P") || kode.equals("INJ") || kode.equals("IV") || kode.equals("INTRAVENOUS")
                || kode.equals("INJ.INTRAVENOUS") || dispUpper.contains("INJEKSI") || dispUpper.contains("INTRAVENA")
                || dispUpper.contains("PARENTERAL"))
            return new String[]{"http://www.whocc.no/atc", "P", "Parenteral"};
        if (kode.equals("SL") || kode.equals("SUBLINGUAL") || dispUpper.contains("SUBLINGUAL"))
            return new String[]{"http://www.whocc.no/atc", "SL", "Sublingual"};
        if (kode.equals("R") || kode.equals("RECTAL") || dispUpper.contains("RECTAL") || dispUpper.contains("REKTAL"))
            return new String[]{"http://www.whocc.no/atc", "R", "Rectal"};
        // External/topikal/kutan & BHP: TIDAK ada kode ATC valid -> route dikosongkan (route opsional).
        if (kode.equals("EXT") || kode.equals("EXTERNAL") || kode.equals("CUTANEOUS") || kode.equals("OINTMENT")
                || kode.equals("TOPICAL") || dispUpper.contains("EXTERNAL") || dispUpper.contains("TOPIKAL")
                || dispUpper.contains("KULIT") || dispUpper.contains("KUTAN"))
            return new String[]{"", "", ""};
        // Hanya teruskan apa adanya bila system rute memang ATC valid; selain itu kosongkan (hindari kode invalid).
        if (system.equals("http://www.whocc.no/atc") && !kode.equals("") && !display.equals(""))
            return new String[]{system, kode, display};
        return new String[]{"", "", ""};
    }

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

    private Double parseAngka(String nilai) {
        if (isBlank(nilai)) return null;
        try {
            return Double.valueOf(nilai.trim().replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(isBlank(resourceType) ? "Resource" : resourceType.trim());
        if (keys != null) {
            for (String key : keys) {
                sb.append("|").append(isBlank(key) ? "-" : key.trim());
            }
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

    /** BHP/alkes (mis. kode "BHP000009", form "BHP") bukan KFA Medication valid -> dilewati dari bundle obat. */
    private boolean isBhp(String obatCode, String formCode) {
        String oc = obatCode == null ? "" : obatCode.trim().toUpperCase();
        String fc = formCode == null ? "" : formCode.trim().toUpperCase();
        return oc.startsWith("BHP") || fc.equals("BHP");
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
