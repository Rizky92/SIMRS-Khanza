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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sub-sender Laboratorium untuk flow SatuSehatBundle (dipanggil setelah Encounter).
 *
 * Per pemeriksaan lab: ServiceRequest (permintaan) -> bila ada hasil: Specimen + Observation +
 * DiagnosticReport. Sumber: permintaan_lab + template_laboratorium + satu_sehat_mapping_lab
 * (mapping LOINC/spesimen) + periksa_lab/detail_periksa_lab (hasil). Logika diadaptasi dari
 * SatuSehatKirimBundle.tambahResourceLab, dengan pola idempotent + chunked seperti SatuSehatObat.
 *
 * Idempotent: tiap resource pakai id stabil (deterministik) / hasil lookup tabel lokal -> PUT (upsert),
 * id response disimpan ke satu_sehat_*_lab. Bundle dipecah per-chunk agar tidak 502.
 */
public class SatuSehatLab {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private static final String ZONA = "+07:00";

    public SatuSehatLab() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Lab : " + e);
        }
    }

    // ====================== Holder ======================

    private static class Konteks {
        String idPasien = "", namaPasien = "", idDokter = "", namaDokter = "";
    }

    private static class LabData {
        String noOrder, idTemplate, kdJenisPrw;
        String tglPermintaan, jamPermintaan, diagnosaKlinis;
        String pemeriksaanNama, satuan, code, system, display;
        String sampleCode, sampleSystem, sampleDisplay;
        String tglPeriksa, jamPeriksa, idDokterLabSatusehat;
        String nilai, nilaiRujukan, keterangan, kesimpulan;
        boolean adaHasil;
    }

    private static class EntryMeta {
        String tipe, noOrder, idTemplate, kdJenisPrw;
        EntryMeta(String tipe, String noOrder, String idTemplate, String kdJenisPrw) {
            this.tipe = tipe; this.noOrder = noOrder; this.idTemplate = idTemplate; this.kdJenisPrw = kdJenisPrw;
        }
    }

    // ====================== Entry point ======================

    /** PREVIEW: rakit SATU Bundle lab (ServiceRequest/Specimen/Observation/DiagnosticReport) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (isBlank(idEncounter)) return null;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || isBlank(k.idPasien)) return null;
        List<LabData> daftar = ambilSemuaLab(noRawat);
        if (daftar.isEmpty()) return null;
        if (isBlank(k.idDokter)) return null;
        String encounterRef = "Encounter/" + idEncounter;
        String patientRef = "Patient/" + k.idPasien;
        String idOrg = koneksiDB.IDSATUSEHAT();
        ArrayNode entries = mapper.createArrayNode();
        List<EntryMeta> metas = new ArrayList<>();
        for (LabData lab : daftar) {
            String idSr = idAtauStabil(
                    "select id_servicerequest from satu_sehat_servicerequest_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "ServiceRequestLab", noRawat);
            tambahEntryPut(entries, "ServiceRequest", idSr,
                    buatLabServiceRequest(lab, encounterRef, patientRef, k.namaPasien, k.idDokter, k.namaDokter, idOrg));
            metas.add(new EntryMeta("ServiceRequestLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
            String srRef = "ServiceRequest/" + idSr;
            if (!lab.adaHasil) {
                continue;
            }
            String idSpec = idAtauStabil(
                    "select id_specimen from satu_sehat_specimen_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "SpecimenLab", noRawat);
            tambahEntryPut(entries, "Specimen", idSpec,
                    buatLabSpecimen(lab, patientRef, k.namaPasien, srRef, idOrg));
            metas.add(new EntryMeta("SpecimenLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
            String specRef = "Specimen/" + idSpec;
            String idObs = idAtauStabil(
                    "select id_observation from satu_sehat_observation_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "ObservationLab", noRawat);
            tambahEntryPut(entries, "Observation", idObs,
                    buatLabObservation(lab, encounterRef, patientRef, k.idDokter, srRef, specRef, idOrg));
            metas.add(new EntryMeta("ObservationLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
            String obsRef = "Observation/" + idObs;
            String idDr = idAtauStabil(
                    "select id_diagnosticreport from satu_sehat_diagnosticreport_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "DiagnosticReportLab", noRawat);
            tambahEntryPut(entries, "DiagnosticReport", idDr,
                    buatLabDiagnosticReport(lab, encounterRef, patientRef, k.idDokter, srRef, specRef, obsRef, idOrg));
            metas.add(new EntryMeta("DiagnosticReportLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
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
            System.out.println("Notifikasi Lab : ID pasien SATUSEHAT belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        List<LabData> daftar = ambilSemuaLab(noRawat);
        if (daftar.isEmpty()) {
            return;
        }
        if (isBlank(k.idDokter)) {
            System.out.println("Notifikasi Lab : ID dokter SATUSEHAT belum ada untuk no_rawat " + noRawat
                    + ". ServiceRequest butuh Practitioner. Dilewati.");
            return;
        }

        String encounterRef = "Encounter/" + idEncounter;
        String patientRef = "Patient/" + k.idPasien;
        String idOrg = koneksiDB.IDSATUSEHAT();

        ArrayNode entries = mapper.createArrayNode();
        List<EntryMeta> metas = new ArrayList<>();

        for (LabData lab : daftar) {
            // ServiceRequest
            String idSr = idAtauStabil(
                    "select id_servicerequest from satu_sehat_servicerequest_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "ServiceRequestLab", noRawat);
            tambahEntryPut(entries, "ServiceRequest", idSr,
                    buatLabServiceRequest(lab, encounterRef, patientRef, k.namaPasien, k.idDokter, k.namaDokter, idOrg));
            metas.add(new EntryMeta("ServiceRequestLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
            String srRef = "ServiceRequest/" + idSr;

            if (!lab.adaHasil) {
                continue;   // belum ada hasil -> cukup ServiceRequest
            }

            // Specimen
            String idSpec = idAtauStabil(
                    "select id_specimen from satu_sehat_specimen_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "SpecimenLab", noRawat);
            tambahEntryPut(entries, "Specimen", idSpec,
                    buatLabSpecimen(lab, patientRef, k.namaPasien, srRef, idOrg));
            metas.add(new EntryMeta("SpecimenLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
            String specRef = "Specimen/" + idSpec;

            // Observation
            String idObs = idAtauStabil(
                    "select id_observation from satu_sehat_observation_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "ObservationLab", noRawat);
            tambahEntryPut(entries, "Observation", idObs,
                    buatLabObservation(lab, encounterRef, patientRef, k.idDokter, srRef, specRef, idOrg));
            metas.add(new EntryMeta("ObservationLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
            String obsRef = "Observation/" + idObs;

            // DiagnosticReport
            String idDr = idAtauStabil(
                    "select id_diagnosticreport from satu_sehat_diagnosticreport_lab where noorder=? and id_template=? and kd_jenis_prw=? limit 1",
                    lab, "DiagnosticReportLab", noRawat);
            tambahEntryPut(entries, "DiagnosticReport", idDr,
                    buatLabDiagnosticReport(lab, encounterRef, patientRef, k.idDokter, srRef, specRef, obsRef, idOrg));
            metas.add(new EntryMeta("DiagnosticReportLab", lab.noOrder, lab.idTemplate, lab.kdJenisPrw));
        }

        if (entries.size() == 0) {
            System.out.println("Notifikasi Lab : tidak ada resource lab yang bisa dikirim untuk no_rawat " + noRawat + ".");
            return;
        }

        // Urutkan: ServiceRequest -> Specimen -> Observation -> DiagnosticReport (referensi aman lintas-chunk).
        List<Object[]> pairs = new ArrayList<>();
        for (int i = 0; i < metas.size(); i++) {
            pairs.add(new Object[]{(ObjectNode) entries.get(i), metas.get(i)});
        }
        pairs.sort((a, b) -> prioritasResource((ObjectNode) a[0]) - prioritasResource((ObjectNode) b[0]));

        final int CHUNK = 25;
        int sukses = 0;
        System.out.println("URL Lab : " + link);
        System.out.println("Total resource lab : " + pairs.size() + " -> dikirim per " + CHUNK + " (chunk).");
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
                System.out.println("Notifikasi Lab : chunk " + nomor + " gagal (" + ex + ").");
            }
        }
        System.out.println("Notifikasi Lab : selesai. " + sukses + "/" + pairs.size() + " resource lab berhasil (2xx).");
    }

    /** Lookup id di tabel lokal; kalau kosong -> id deterministik. */
    private String idAtauStabil(String sql, LabData lab, String tipe, String noRawat) {
        String idLama = cariIdSatuSehat(sql, lab.noOrder, lab.idTemplate, lab.kdJenisPrw);
        if (!isBlank(idLama)) return idLama;
        return stableResourceId(tipe, noRawat, lab.noOrder, lab.kdJenisPrw, lab.idTemplate);
    }

    /** Kirim satu chunk; simpan id response; balikkan jumlah 2xx. */
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
            System.out.println("Error Lab chunk " + nomor + " Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Lab OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Lab Body: " + body);
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
            EntryMeta meta = metas.get(x);
            switch (meta.tipe) {
                case "ServiceRequestLab":
                    simpanLab("satu_sehat_servicerequest_lab", "id_servicerequest", meta, idResource); break;
                case "SpecimenLab":
                    simpanLab("satu_sehat_specimen_lab", "id_specimen", meta, idResource); break;
                case "ObservationLab":
                    simpanLab("satu_sehat_observation_lab", "id_observation", meta, idResource); break;
                case "DiagnosticReportLab":
                    simpanLab("satu_sehat_diagnosticreport_lab", "id_diagnosticreport", meta, idResource); break;
                default:
            }
        }
        System.out.println("Result Lab chunk " + nomor + " : " + ok + "/" + metas.size() + " resource 2xx.");
        return ok;
    }

    private int prioritasResource(ObjectNode entry) {
        switch (entry.path("resource").path("resourceType").asText()) {
            case "ServiceRequest": return 0;
            case "Specimen": return 1;
            case "Observation": return 2;
            case "DiagnosticReport": return 3;
            default: return 4;
        }
    }

    // ====================== Builder resource ======================

    private ObjectNode buatLabServiceRequest(LabData lab, String encounterRef, String patientRef,
            String namaPasien, String idDokter, String namaDokter, String idOrg) {
        ObjectNode sr = mapper.createObjectNode();
        sr.put("resourceType", "ServiceRequest");
        ObjectNode iden = sr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/servicerequest/" + idOrg);
        iden.put("value", buildLabOrderIdentifier(lab));
        sr.put("status", "active");
        sr.put("intent", "order");
        ObjectNode catCoding = sr.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://snomed.info/sct");
        catCoding.put("code", "108252007");
        catCoding.put("display", "Laboratory procedure");
        ObjectNode code = sr.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        String systemLab = normalisasiCodeSystemLab(lab.system);
        String codeLab = isLoincSystem(systemLab) ? normalisasiLoincCode(lab.code) : lab.code;
        codeCoding.put("system", systemLab);
        codeCoding.put("code", codeLab);
        codeCoding.put("display", isBlank(lab.display) ? lab.pemeriksaanNama : lab.display);
        code.put("text", isBlank(lab.pemeriksaanNama) ? lab.display : lab.pemeriksaanNama);
        ObjectNode subject = sr.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        ObjectNode enc = sr.putObject("encounter");
        enc.put("reference", encounterRef);
        enc.put("display", "Permintaan " + lab.pemeriksaanNama + " " + namaPasien);
        String authoredOn = bangunDateTimeAman(lab.tglPermintaan, lab.jamPermintaan);
        if (!isBlank(authoredOn)) sr.put("authoredOn", authoredOn);
        ObjectNode requester = sr.putObject("requester");
        requester.put("reference", "Practitioner/" + idDokter);
        requester.put("display", namaDokter);
        ObjectNode perf = sr.putArray("performer").addObject();
        perf.put("reference", "Organization/" + idOrg);
        perf.put("display", "Laboratorium");
        if (!isBlank(lab.diagnosaKlinis)) {
            sr.putArray("reasonCode").addObject().put("text", bersihkanTeksPlain(lab.diagnosaKlinis));
        }
        return sr;
    }

    private ObjectNode buatLabSpecimen(LabData lab, String patientRef, String namaPasien,
            String serviceRequestRef, String idOrg) {
        ObjectNode spec = mapper.createObjectNode();
        spec.put("resourceType", "Specimen");
        ObjectNode iden = spec.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/specimen/" + idOrg);
        iden.put("value", buildLabOrderIdentifier(lab));
        spec.put("status", "available");
        String[] mapSpec = mapSpesimenLab(lab.sampleCode, lab.sampleDisplay);
        ObjectNode type = spec.putObject("type");
        ObjectNode typeCoding = type.putArray("coding").addObject();
        typeCoding.put("system", "http://snomed.info/sct");
        typeCoding.put("code", mapSpec[0]);
        typeCoding.put("display", mapSpec[1]);
        if (!isBlank(lab.sampleDisplay)) type.put("text", lab.sampleDisplay);
        ObjectNode subject = spec.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        if (!isBlank(serviceRequestRef)) {
            spec.putArray("request").addObject().put("reference", serviceRequestRef);
        }
        String received = bangunDateTimeAman(lab.tglPeriksa, lab.jamPeriksa);
        if (isBlank(received)) received = bangunDateTimeAman(lab.tglPermintaan, lab.jamPermintaan);
        if (!isBlank(received)) spec.put("receivedTime", received);
        return spec;
    }

    private ObjectNode buatLabObservation(LabData lab, String encounterRef, String patientRef,
            String idDokterFallback, String serviceRequestRef, String specimenRef, String idOrg) {
        ObjectNode obs = mapper.createObjectNode();
        obs.put("resourceType", "Observation");
        ObjectNode iden = obs.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/observation/" + idOrg);
        iden.put("value", lab.noOrder + "." + (isBlank(lab.kdJenisPrw) ? "-" : lab.kdJenisPrw)
                + "." + (isBlank(lab.idTemplate) ? "-" : lab.idTemplate));
        obs.put("status", "final");
        ObjectNode catCoding = obs.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        catCoding.put("code", "laboratory");
        catCoding.put("display", "Laboratory");
        ObjectNode code = obs.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        String systemLab = normalisasiCodeSystemLab(lab.system);
        String codeLab = isLoincSystem(systemLab) ? normalisasiLoincCode(lab.code) : lab.code;
        codeCoding.put("system", systemLab);
        codeCoding.put("code", codeLab);
        codeCoding.put("display", isBlank(lab.display) ? lab.pemeriksaanNama : lab.display);
        obs.putObject("subject").put("reference", patientRef);
        String idPerformer = !isBlank(lab.idDokterLabSatusehat) ? lab.idDokterLabSatusehat : idDokterFallback;
        obs.putArray("performer").addObject().put("reference", "Practitioner/" + idPerformer);
        ObjectNode enc = obs.putObject("encounter");
        enc.put("reference", encounterRef);
        enc.put("display", "Hasil " + lab.pemeriksaanNama);
        if (!isBlank(serviceRequestRef)) {
            obs.putArray("basedOn").addObject().put("reference", serviceRequestRef);
        }
        if (!isBlank(specimenRef)) {
            obs.putObject("specimen").put("reference", specimenRef);
        }
        String effective = bangunDateTimeAman(lab.tglPeriksa, lab.jamPeriksa);
        if (!isBlank(effective)) {
            obs.put("effectiveDateTime", effective);
            obs.put("issued", effective);
        }
        Double angka = parseAngka(lab.nilai);
        String ucumCode = mapUcumLab(lab.satuan);
        if (angka != null && !isBlank(ucumCode)) {
            ObjectNode valueQty = obs.putObject("valueQuantity");
            valueQty.put("value", angka);
            valueQty.put("unit", lab.satuan);
            valueQty.put("system", "http://unitsofmeasure.org");
            valueQty.put("code", ucumCode);
        } else {
            String teksNilai = bersihkanTeksPlain(lab.nilai);
            if (!isBlank(lab.satuan) && angka != null) teksNilai = teksNilai + " " + lab.satuan;
            obs.put("valueString", isBlank(teksNilai) ? "-" : teksNilai);
        }
        if (!isBlank(lab.nilaiRujukan)) {
            obs.putArray("referenceRange").addObject().put("text", bersihkanTeksPlain(lab.nilaiRujukan));
        }
        if (!isBlank(lab.keterangan)) {
            obs.putArray("note").addObject().put("text", bersihkanTeksPlain(lab.keterangan));
        }
        return obs;
    }

    private ObjectNode buatLabDiagnosticReport(LabData lab, String encounterRef, String patientRef,
            String idDokterFallback, String serviceRequestRef, String specimenRef, String observationRef, String idOrg) {
        ObjectNode dr = mapper.createObjectNode();
        dr.put("resourceType", "DiagnosticReport");
        ObjectNode iden = dr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/diagnostic/" + idOrg + "/lab");
        iden.put("use", "official");
        iden.put("value", buildLabOrderIdentifier(lab));
        dr.put("status", "final");
        ObjectNode catCoding = dr.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/v2-0074");
        catCoding.put("code", "LAB");
        catCoding.put("display", "Laboratory");
        ObjectNode code = dr.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        String systemLab = normalisasiCodeSystemLab(lab.system);
        String codeLab = isLoincSystem(systemLab) ? normalisasiLoincCode(lab.code) : lab.code;
        codeCoding.put("system", systemLab);
        codeCoding.put("code", codeLab);
        codeCoding.put("display", isBlank(lab.display) ? lab.pemeriksaanNama : lab.display);
        dr.putObject("subject").put("reference", patientRef);
        dr.putObject("encounter").put("reference", encounterRef);
        String effective = bangunDateTimeAman(lab.tglPeriksa, lab.jamPeriksa);
        if (!isBlank(effective)) {
            dr.put("effectiveDateTime", effective);
            dr.put("issued", effective);
        }
        String idPerformer = !isBlank(lab.idDokterLabSatusehat) ? lab.idDokterLabSatusehat : idDokterFallback;
        dr.putArray("performer").addObject().put("reference", "Practitioner/" + idPerformer);
        if (!isBlank(specimenRef)) dr.putArray("specimen").addObject().put("reference", specimenRef);
        if (!isBlank(observationRef)) dr.putArray("result").addObject().put("reference", observationRef);
        if (!isBlank(serviceRequestRef)) dr.putArray("basedOn").addObject().put("reference", serviceRequestRef);
        if (!isBlank(lab.kesimpulan)) dr.put("conclusion", bersihkanTeksPlain(lab.kesimpulan));
        return dr;
    }

    // ====================== Bundle helper ======================

    private void tambahEntryPut(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        resource.put("id", id);
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    // ====================== Simpan id response ======================

    private void simpanLab(String tabel, String kolomId, EntryMeta m, String idVal) {
        if (isBlank(idVal) || isBlank(m.noOrder)) return;
        // Kolom tabel: noorder, kd_jenis_prw, id_template, {kolomId}.
        if (!Sequel.menyimpantf2(tabel, "?,?,?,?", tabel, 4,
                new String[]{m.noOrder, m.kdJenisPrw, m.idTemplate, idVal})) {
            Sequel.queryu2("update " + tabel + " set " + kolomId + "=? where noorder=? and id_template=? and kd_jenis_prw=?",
                    4, new String[]{idVal, m.noOrder, m.idTemplate, m.kdJenisPrw});
        }
    }

    private String cariIdSatuSehat(String sql, String... params) {
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                p.setString(i + 1, params[i]);
            }
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return nz(r.getString(1)).trim();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Lab cariIdSatuSehat : " + e);
        }
        return "";
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
            System.out.println("Notifikasi Lab ambilKonteks : " + e);
        }
        return k;
    }

    private List<LabData> ambilSemuaLab(String noRawat) {
        LinkedHashMap<String, LabData> unik = new LinkedHashMap<>();
        Map<String, String> cacheDokter = new HashMap<>();
        String sql = "select permintaan_lab.noorder, permintaan_lab.tgl_permintaan, permintaan_lab.jam_permintaan, "
                + "ifnull(permintaan_lab.diagnosa_klinis,'') as diagnosa_klinis, "
                + "pdpl.id_template, pdpl.kd_jenis_prw, "
                + "ifnull(template_laboratorium.Pemeriksaan,'') as nm_pemeriksaan, "
                + "ifnull(template_laboratorium.satuan,'') as satuan, "
                + "ifnull(satu_sehat_mapping_lab.code,'') as code, "
                + "ifnull(satu_sehat_mapping_lab.system,'') as `system`, "
                + "ifnull(satu_sehat_mapping_lab.display,'') as display, "
                + "ifnull(satu_sehat_mapping_lab.sampel_code,'') as sampel_code, "
                + "ifnull(satu_sehat_mapping_lab.sampel_system,'') as sampel_system, "
                + "ifnull(satu_sehat_mapping_lab.sampel_display,'') as sampel_display, "
                + "ifnull(periksa_lab.tgl_periksa,'') as tgl_periksa, "
                + "ifnull(periksa_lab.jam,'') as jam_periksa, "
                + "ifnull(pegawai_lab.no_ktp,'') as nik_dokter_lab, "
                + "ifnull(detail_periksa_lab.nilai,'') as nilai, "
                + "ifnull(detail_periksa_lab.nilai_rujukan,'') as nilai_rujukan, "
                + "ifnull(detail_periksa_lab.keterangan,'') as keterangan, "
                + "ifnull(saran_kesan_lab.kesan,'') as kesimpulan "
                + "from permintaan_lab "
                + "inner join permintaan_detail_permintaan_lab pdpl on pdpl.noorder = permintaan_lab.noorder "
                + "inner join template_laboratorium on template_laboratorium.id_template = pdpl.id_template "
                + "left join satu_sehat_mapping_lab on satu_sehat_mapping_lab.id_template = template_laboratorium.id_template "
                + "left join periksa_lab on periksa_lab.no_rawat = permintaan_lab.no_rawat "
                + "  and periksa_lab.tgl_periksa = permintaan_lab.tgl_hasil and periksa_lab.jam = permintaan_lab.jam_hasil "
                + "left join pegawai pegawai_lab on pegawai_lab.nik = periksa_lab.kd_dokter "
                + "left join detail_periksa_lab on detail_periksa_lab.no_rawat = periksa_lab.no_rawat "
                + "  and detail_periksa_lab.tgl_periksa = periksa_lab.tgl_periksa and detail_periksa_lab.jam = periksa_lab.jam "
                + "  and detail_periksa_lab.kd_jenis_prw = pdpl.kd_jenis_prw and detail_periksa_lab.id_template = pdpl.id_template "
                + "left join saran_kesan_lab on saran_kesan_lab.no_rawat = periksa_lab.no_rawat "
                + "  and saran_kesan_lab.tgl_periksa = periksa_lab.tgl_periksa and saran_kesan_lab.jam = periksa_lab.jam "
                + "where permintaan_lab.no_rawat = ? "
                + "order by permintaan_lab.tgl_permintaan asc, permintaan_lab.jam_permintaan asc, pdpl.kd_jenis_prw asc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    LabData d = new LabData();
                    d.noOrder = r.getString("noorder");
                    d.idTemplate = r.getString("id_template");
                    d.kdJenisPrw = r.getString("kd_jenis_prw");
                    d.tglPermintaan = r.getString("tgl_permintaan");
                    d.jamPermintaan = r.getString("jam_permintaan");
                    d.diagnosaKlinis = r.getString("diagnosa_klinis");
                    d.pemeriksaanNama = r.getString("nm_pemeriksaan");
                    d.satuan = r.getString("satuan");
                    d.code = r.getString("code");
                    d.system = r.getString("system");
                    d.display = r.getString("display");
                    if (isLoincSystem(d.system)) d.code = normalisasiLoincCode(d.code);
                    d.sampleCode = r.getString("sampel_code");
                    d.sampleSystem = r.getString("sampel_system");
                    d.sampleDisplay = r.getString("sampel_display");
                    d.tglPeriksa = r.getString("tgl_periksa");
                    d.jamPeriksa = r.getString("jam_periksa");
                    String nikLab = nz(r.getString("nik_dokter_lab"));
                    if (!isBlank(nikLab)) {
                        String idDok = cacheDokter.get(nikLab);
                        if (idDok == null) {
                            idDok = nz(cek.tampilIDParktisi(nikLab));
                            cacheDokter.put(nikLab, idDok);
                        }
                        d.idDokterLabSatusehat = idDok;
                    }
                    d.nilai = r.getString("nilai");
                    d.nilaiRujukan = r.getString("nilai_rujukan");
                    d.keterangan = r.getString("keterangan");
                    d.kesimpulan = r.getString("kesimpulan");
                    d.adaHasil = !isBlank(d.tglPeriksa) && !d.tglPeriksa.startsWith("0000-") && !isBlank(d.nilai);
                    if (isBlank(d.code) || isBlank(d.system)) {
                        System.out.println("Notifikasi Lab : order=" + d.noOrder + " template=" + d.idTemplate
                                + " (" + d.pemeriksaanNama + ") dilewati. satu_sehat_mapping_lab belum dipetakan.");
                        continue;
                    }
                    String key = nz(d.noOrder) + "|" + nz(d.idTemplate) + "|" + nz(d.kdJenisPrw);
                    LabData lama = unik.get(key);
                    if (lama == null || (!lama.adaHasil && d.adaHasil)) {
                        unik.put(key, d);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Lab ambilSemuaLab : " + e);
        }
        return new ArrayList<>(unik.values());
    }

    // ====================== Util (port dari SatuSehatKirimBundle) ======================

    private boolean isLoincSystem(String system) {
        if (isBlank(system)) return false;
        String s = system.trim().toLowerCase();
        return s.contains("loinc.org");
    }

    private String normalisasiCodeSystemLab(String system) {
        if (isLoincSystem(system)) return "http://loinc.org";
        return isBlank(system) ? "" : system.trim();
    }

    private String normalisasiLoincCode(String code) {
        if (isBlank(code)) return "";
        String c = code.trim().replaceAll("\\s+", "");
        if (c.matches("\\d{1,7}")) {
            int cd = hitungCheckDigitLoinc(c);
            return cd < 0 ? c : (c + "-" + cd);
        }
        if (!c.matches("\\d{1,7}-\\d")) return c;
        String[] split = c.split("-", 2);
        int cd = hitungCheckDigitLoinc(split[0]);
        if (cd < 0) return c;
        return split[0] + "-" + cd;
    }

    private int hitungCheckDigitLoinc(String angka) {
        if (isBlank(angka) || !angka.matches("\\d+")) return -1;
        int sum = 0;
        boolean kaliDua = true;
        for (int i = angka.length() - 1; i >= 0; i--) {
            int n = Character.digit(angka.charAt(i), 10);
            if (kaliDua) {
                n = n * 2;
                if (n > 9) n = n - 9;
            }
            sum += n;
            kaliDua = !kaliDua;
        }
        return (10 - (sum % 10)) % 10;
    }

    private String[] mapSpesimenLab(String snomedCode, String display) {
        String code = snomedCode == null ? "" : snomedCode.trim();
        switch (code) {
            case "119297000": return new String[]{"119297000", "Blood specimen"};
            case "119295008": return new String[]{"119295008", "Specimen from skin"};
            case "122575003": return new String[]{"122575003", "Urine specimen"};
            case "119339001": return new String[]{"119339001", "Stool specimen"};
            case "119334006": return new String[]{"119334006", "Sputum specimen"};
            case "119342007": return new String[]{"119342007", "Saliva specimen"};
            case "119364003": return new String[]{"119364003", "Serum specimen"};
            case "119361006": return new String[]{"119361006", "Plasma specimen"};
            case "257261003": return new String[]{"257261003", "Swab"};
            case "119376003": return new String[]{"119376003", "Tissue specimen"};
            default:
        }
        String d = display == null ? "" : display.toLowerCase();
        if (d.contains("urin") || d.contains("urine")) return new String[]{"122575003", "Urine specimen"};
        if (d.contains("feses") || d.contains("tinja") || d.contains("stool")) return new String[]{"119339001", "Stool specimen"};
        if (d.contains("sputum") || d.contains("dahak")) return new String[]{"119334006", "Sputum specimen"};
        if (d.contains("saliva") || d.contains("ludah")) return new String[]{"119342007", "Saliva specimen"};
        if (d.contains("serum")) return new String[]{"119364003", "Serum specimen"};
        if (d.contains("plasma")) return new String[]{"119361006", "Plasma specimen"};
        if (d.contains("swab") || d.contains("usap")) return new String[]{"257261003", "Swab"};
        if (d.contains("jaringan") || d.contains("tissue") || d.contains("biopsi")) return new String[]{"119376003", "Tissue specimen"};
        return new String[]{"119297000", "Blood specimen"};
    }

    private String mapUcumLab(String satuan) {
        if (isBlank(satuan)) return null;
        String s = satuan.trim().toLowerCase().replaceAll("\\s+", " ");
        switch (s) {
            case "g/dl": case "gr/dl": return "g/dL";
            case "mg/dl": return "mg/dL";
            case "mg/l": return "mg/L";
            case "ng/dl": return "ng/dL";
            case "ng/ml": return "ng/mL";
            case "pg/ml": return "pg/mL";
            case "ug/dl": case "mcg/dl": return "ug/dL";
            case "ug/ml": case "mcg/ml": return "ug/mL";
            case "%": return "%";
            case "ribu/mm3": case "ribu/ul": case "k/ul": case "x10^3/ul": case "10^3/ul": return "10*3/uL";
            case "juta/mm3": case "juta/ul": case "m/ul": case "x10^6/ul": case "10^6/ul": return "10*6/uL";
            case "mikro m3": case "fl": return "fL";
            case "pg": return "pg";
            case "menit": case "min": return "min";
            case "detik": case "det": case "sec": return "s";
            case "jam": case "hour": case "h": return "h";
            case "hari": case "day": case "d": return "d";
            case "meq/l": return "meq/L";
            case "iu/l": case "ui/l": return "[iU]/L";
            case "iu/ml": return "[iU]/mL";
            case "u/l": return "U/L";
            case "u/ml": return "U/mL";
            case "mu/ml": case "miu/ml": return "mU/mL";
            case "mmhg": return "mm[Hg]";
            case "mmol/l": return "mmol/L";
            case "umol/l": case "mikromol/l": return "umol/L";
            case "mosmol/l": case "mosm/l": return "mosm/L";
            case "ppm": return "10*-6";
            case "g": case "gram": return "g";
            case "mg": return "mg";
            case "mcg": case "ug": return "ug";
            case "kg": return "kg";
            case "ml": return "mL";
            case "l": return "L";
            case "cm": return "cm";
            case "mm": return "mm";
            case "m": return "m";
            case "celsius": case "c": return "Cel";
            case "rasio": case "ratio": case "1": return "1";
            default: return null;
        }
    }

    private String buildLabOrderIdentifier(LabData lab) {
        return keyBagian(lab.noOrder) + "." + keyBagian(lab.kdJenisPrw) + "." + keyBagian(lab.idTemplate);
    }

    private String keyBagian(String data) {
        return isBlank(data) ? "-" : data.trim();
    }

    private String bersihkanTeksPlain(String data) {
        if (isBlank(data)) return "";
        String s = data.replaceAll("<br\\s*/?>", " ");
        s = s.replaceAll("<[^>]+>", "");
        s = s.replaceAll("(\\r\\n|\\r|\\n|\\t)+", " ");
        s = s.replaceAll("\\s{2,}", " ").trim();
        return s;
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
