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
import java.util.HashMap;
import java.util.LinkedHashSet;
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
 * Sub-sender Radiologi untuk flow SatuSehatBundle (dipanggil setelah Encounter).
 *
 * Per pemeriksaan radiologi: ServiceRequest (permintaan) -> bila ada hasil: Observation +
 * DiagnosticReport. Sumber: permintaan_radiologi + jns_perawatan_radiologi + satu_sehat_mapping_radiologi
 * (mapping SNOMED) + periksa_radiologi/hasil_radiologi (hasil). Pola idempotent + chunked seperti SatuSehatLab.
 *
 * Idempotent: id dari tabel lokal (satu_sehat_*_radiologi) / stableResourceId -> PUT. Bundle dipecah per-chunk.
 */
public class SatuSehatRadiologi {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";
    private static final String ZONA = "+07:00";

    public SatuSehatRadiologi() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Radiologi : " + e);
        }
    }

    private static class Konteks {
        String idPasien = "", namaPasien = "", idDokter = "", namaDokter = "";
    }

    private static class RadiologiData {
        String noOrder, kdJenisPrw, tglPermintaan, jamPermintaan, tglHasil, jamHasil;
        String diagnosaKlinis, pemeriksaanNama, code, system, display, hasil;
        String idDokterSatusehat, idServiceRequestLama, idObservationLama, idDiagnosticReportLama;
        String idImagingStudy;   // dari satu_sehat_imagingstudy_radiologi; "" bila citra belum terkirim
        boolean adaHasil;
    }

    private static class EntryMeta {
        String tipe, noOrder, kdJenisPrw;
        EntryMeta(String tipe, String noOrder, String kdJenisPrw) {
            this.tipe = tipe; this.noOrder = noOrder; this.kdJenisPrw = kdJenisPrw;
        }
    }

    /** PREVIEW: rakit SATU Bundle radiologi (ServiceRequest/Observation/DiagnosticReport) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (isBlank(idEncounter)) return null;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || isBlank(k.idPasien)) return null;
        List<RadiologiData> daftar = ambilSemuaRadiologi(noRawat);
        if (daftar.isEmpty()) return null;
        if (isBlank(k.idDokter)) return null;
        String encounterRef = "Encounter/" + idEncounter;
        String patientRef = "Patient/" + k.idPasien;
        String idOrg = koneksiDB.IDSATUSEHAT();
        String idOrgRad = ambilIdOrganisasiRadiologi();
        ArrayNode entries = mapper.createArrayNode();
        List<EntryMeta> metas = new ArrayList<>();
        for (RadiologiData rad : daftar) {
            String idSr = isBlank(rad.idServiceRequestLama)
                    ? stableResourceId("ServiceRequestRadiologi", noRawat, rad.noOrder, rad.kdJenisPrw)
                    : rad.idServiceRequestLama;
            tambahEntryPut(entries, "ServiceRequest", idSr,
                    buatRadiologiServiceRequest(rad, encounterRef, patientRef, k.namaPasien, k.idDokter, k.namaDokter, idOrg, idOrgRad));
            metas.add(new EntryMeta("ServiceRequestRadiologi", rad.noOrder, rad.kdJenisPrw));
            String srRef = "ServiceRequest/" + idSr;
            if (!rad.adaHasil) {
                continue;
            }
            String idObs = isBlank(rad.idObservationLama)
                    ? stableResourceId("ObservationRadiologi", noRawat, rad.noOrder, rad.kdJenisPrw)
                    : rad.idObservationLama;
            tambahEntryPut(entries, "Observation", idObs,
                    buatRadiologiObservation(rad, encounterRef, patientRef, k.namaPasien, k.idDokter, srRef, idOrg));
            metas.add(new EntryMeta("ObservationRadiologi", rad.noOrder, rad.kdJenisPrw));
            String obsRef = "Observation/" + idObs;
            String idDr = isBlank(rad.idDiagnosticReportLama)
                    ? stableResourceId("DiagnosticReportRadiologi", noRawat, rad.noOrder, rad.kdJenisPrw)
                    : rad.idDiagnosticReportLama;
            tambahEntryPut(entries, "DiagnosticReport", idDr,
                    buatRadiologiDiagnosticReport(rad, encounterRef, patientRef, k.idDokter, idOrgRad, srRef, obsRef, idOrg));
            metas.add(new EntryMeta("DiagnosticReportRadiologi", rad.noOrder, rad.kdJenisPrw));
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
            System.out.println("Notifikasi Radiologi : ID pasien SATUSEHAT belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        List<RadiologiData> daftar = ambilSemuaRadiologi(noRawat);
        if (daftar.isEmpty()) {
            return;
        }
        if (isBlank(k.idDokter)) {
            System.out.println("Notifikasi Radiologi : ID dokter SATUSEHAT belum ada untuk no_rawat " + noRawat
                    + ". ServiceRequest butuh Practitioner. Dilewati.");
            return;
        }

        String encounterRef = "Encounter/" + idEncounter;
        String patientRef = "Patient/" + k.idPasien;
        String idOrg = koneksiDB.IDSATUSEHAT();
        String idOrgRad = ambilIdOrganisasiRadiologi();

        ArrayNode entries = mapper.createArrayNode();
        List<EntryMeta> metas = new ArrayList<>();

        for (RadiologiData rad : daftar) {
            // ServiceRequest
            String idSr = isBlank(rad.idServiceRequestLama)
                    ? stableResourceId("ServiceRequestRadiologi", noRawat, rad.noOrder, rad.kdJenisPrw)
                    : rad.idServiceRequestLama;
            tambahEntryPut(entries, "ServiceRequest", idSr,
                    buatRadiologiServiceRequest(rad, encounterRef, patientRef, k.namaPasien, k.idDokter, k.namaDokter, idOrg, idOrgRad));
            metas.add(new EntryMeta("ServiceRequestRadiologi", rad.noOrder, rad.kdJenisPrw));
            String srRef = "ServiceRequest/" + idSr;

            if (!rad.adaHasil) {
                continue;
            }

            // Observation
            String idObs = isBlank(rad.idObservationLama)
                    ? stableResourceId("ObservationRadiologi", noRawat, rad.noOrder, rad.kdJenisPrw)
                    : rad.idObservationLama;
            tambahEntryPut(entries, "Observation", idObs,
                    buatRadiologiObservation(rad, encounterRef, patientRef, k.namaPasien, k.idDokter, srRef, idOrg));
            metas.add(new EntryMeta("ObservationRadiologi", rad.noOrder, rad.kdJenisPrw));
            String obsRef = "Observation/" + idObs;

            // DiagnosticReport
            String idDr = isBlank(rad.idDiagnosticReportLama)
                    ? stableResourceId("DiagnosticReportRadiologi", noRawat, rad.noOrder, rad.kdJenisPrw)
                    : rad.idDiagnosticReportLama;
            tambahEntryPut(entries, "DiagnosticReport", idDr,
                    buatRadiologiDiagnosticReport(rad, encounterRef, patientRef, k.idDokter, idOrgRad, srRef, obsRef, idOrg));
            metas.add(new EntryMeta("DiagnosticReportRadiologi", rad.noOrder, rad.kdJenisPrw));
        }

        if (entries.size() == 0) {
            System.out.println("Notifikasi Radiologi : tidak ada resource yang bisa dikirim untuk no_rawat " + noRawat + ".");
            return;
        }

        List<Object[]> pairs = new ArrayList<>();
        for (int i = 0; i < metas.size(); i++) {
            pairs.add(new Object[]{(ObjectNode) entries.get(i), metas.get(i)});
        }
        pairs.sort((a, b) -> prioritasResource((ObjectNode) a[0]) - prioritasResource((ObjectNode) b[0]));

        final int CHUNK = 25;
        int sukses = 0;
        System.out.println("URL Radiologi : " + link);
        System.out.println("Total resource radiologi : " + pairs.size() + " -> dikirim per " + CHUNK + " (chunk).");
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
                System.out.println("Notifikasi Radiologi : chunk " + nomor + " gagal (" + ex + ").");
            }
        }
        System.out.println("Notifikasi Radiologi : selesai. " + sukses + "/" + pairs.size() + " resource radiologi berhasil (2xx).");
    }

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
            System.out.println("Error Radiologi chunk " + nomor + " Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Radiologi OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Radiologi Body: " + body);
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
                case "ServiceRequestRadiologi":
                    simpanRad("satu_sehat_servicerequest_radiologi", "id_servicerequest", meta, idResource); break;
                case "ObservationRadiologi":
                    simpanRad("satu_sehat_observation_radiologi", "id_observation", meta, idResource); break;
                case "DiagnosticReportRadiologi":
                    simpanRad("satu_sehat_diagnosticreport_radiologi", "id_diagnosticreport", meta, idResource); break;
                default:
            }
        }
        System.out.println("Result Radiologi chunk " + nomor + " : " + ok + "/" + metas.size() + " resource 2xx.");
        return ok;
    }

    private int prioritasResource(ObjectNode entry) {
        switch (entry.path("resource").path("resourceType").asText()) {
            case "ServiceRequest": return 0;
            case "Observation": return 1;
            case "DiagnosticReport": return 2;
            default: return 3;
        }
    }

    // ====================== Builder resource ======================

    private ObjectNode buatRadiologiServiceRequest(RadiologiData rad, String encounterRef, String patientRef,
            String namaPasien, String idDokter, String namaDokter, String idOrg, String idOrgRad) {
        ObjectNode sr = mapper.createObjectNode();
        sr.put("resourceType", "ServiceRequest");
        ArrayNode identifier = sr.putArray("identifier");
        ObjectNode idenOrder = identifier.addObject();
        idenOrder.put("system", "http://sys-ids.kemkes.go.id/servicerequest/" + idOrg);
        idenOrder.put("value", buildRadiologiOrderIdentifier(rad));
        ObjectNode idenAcsn = identifier.addObject();
        idenAcsn.put("use", "usual");
        idenAcsn.putObject("type").putArray("coding").addObject()
                .put("system", "http://terminology.hl7.org/CodeSystem/v2-0203").put("code", "ACSN");
        idenAcsn.put("system", "http://sys-ids.kemkes.go.id/acsn/" + idOrg);
        idenAcsn.put("value", rad.noOrder);
        sr.put("status", "active");
        sr.put("intent", "original-order");
        sr.put("priority", "routine");
        ObjectNode catCoding = sr.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://snomed.info/sct");
        catCoding.put("code", "363679005");
        catCoding.put("display", "Imaging");
        ObjectNode code = sr.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        codeCoding.put("system", rad.system);
        codeCoding.put("code", rad.code);
        codeCoding.put("display", isBlank(rad.display) ? rad.pemeriksaanNama : rad.display);
        code.put("text", isBlank(rad.pemeriksaanNama) ? rad.display : rad.pemeriksaanNama);
        ObjectNode subject = sr.putObject("subject");
        subject.put("reference", patientRef);
        subject.put("display", namaPasien);
        ObjectNode enc = sr.putObject("encounter");
        enc.put("reference", encounterRef);
        enc.put("display", "Permintaan " + rad.pemeriksaanNama + " " + namaPasien);
        String occurrence = bangunDateTimeAman(rad.tglPermintaan, rad.jamPermintaan);
        if (!isBlank(occurrence)) {
            sr.put("occurrenceDateTime", occurrence);
            sr.put("authoredOn", occurrence);
        }
        ObjectNode requester = sr.putObject("requester");
        requester.put("reference", "Practitioner/" + idDokter);
        requester.put("display", namaDokter);
        ObjectNode perf = sr.putArray("performer").addObject();
        perf.put("reference", "Organization/" + idOrgRad);
        perf.put("display", "Ruang Radiologi/Petugas Radiologi");
        if (!isBlank(rad.diagnosaKlinis)) {
            sr.putArray("reasonCode").addObject().put("text", bersihkanTeksPlain(rad.diagnosaKlinis));
        }
        return sr;
    }

    private ObjectNode buatRadiologiObservation(RadiologiData rad, String encounterRef, String patientRef,
            String namaPasien, String idDokterFallback, String serviceRequestRef, String idOrg) {
        ObjectNode obs = mapper.createObjectNode();
        obs.put("resourceType", "Observation");
        ObjectNode iden = obs.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/observation/" + idOrg);
        iden.put("value", buildRadiologiOrderIdentifier(rad));
        obs.put("status", "final");
        ObjectNode catCoding = obs.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        catCoding.put("code", "imaging");
        catCoding.put("display", "Imaging");
        ObjectNode code = obs.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        codeCoding.put("system", rad.system);
        codeCoding.put("code", rad.code);
        codeCoding.put("display", isBlank(rad.display) ? rad.pemeriksaanNama : rad.display);
        obs.putObject("subject").put("reference", patientRef);
        String idPerformer = !isBlank(rad.idDokterSatusehat) ? rad.idDokterSatusehat : idDokterFallback;
        obs.putArray("performer").addObject().put("reference", "Practitioner/" + idPerformer);
        ObjectNode enc = obs.putObject("encounter");
        enc.put("reference", encounterRef);
        enc.put("display", "Hasil Pemeriksaan Radiologi " + rad.pemeriksaanNama + " " + namaPasien);
        if (!isBlank(serviceRequestRef)) {
            obs.putArray("basedOn").addObject().put("reference", serviceRequestRef);
        }
        String effective = bangunDateTimeAman(rad.tglHasil, rad.jamHasil);
        if (isBlank(effective)) effective = bangunDateTimeAman(rad.tglPermintaan, rad.jamPermintaan);
        if (!isBlank(effective)) obs.put("effectiveDateTime", effective);
        obs.put("valueString", isBlank(rad.hasil) ? "-" : bersihkanTeksPlain(rad.hasil));
        return obs;
    }

    private ObjectNode buatRadiologiDiagnosticReport(RadiologiData rad, String encounterRef, String patientRef,
            String idDokterFallback, String idOrgRad, String serviceRequestRef, String observationRef, String idOrg) {
        ObjectNode dr = mapper.createObjectNode();
        dr.put("resourceType", "DiagnosticReport");
        ObjectNode iden = dr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/diagnostic/" + idOrg + "/rad");
        iden.put("use", "official");
        iden.put("value", buildRadiologiOrderIdentifier(rad));
        dr.put("status", "final");
        ObjectNode catCoding = dr.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.hl7.org/CodeSystem/v2-0074");
        catCoding.put("code", "RAD");
        catCoding.put("display", "Radiology");
        ObjectNode code = dr.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        codeCoding.put("system", rad.system);
        codeCoding.put("code", rad.code);
        codeCoding.put("display", isBlank(rad.display) ? rad.pemeriksaanNama : rad.display);
        dr.putObject("subject").put("reference", patientRef);
        dr.putObject("encounter").put("reference", encounterRef);
        String effective = bangunDateTimeAman(rad.tglHasil, rad.jamHasil);
        if (isBlank(effective)) effective = bangunDateTimeAman(rad.tglPermintaan, rad.jamPermintaan);
        if (!isBlank(effective)) {
            dr.put("effectiveDateTime", effective);
            dr.put("issued", effective);
        }
        ArrayNode performer = dr.putArray("performer");
        String idPerformer = !isBlank(rad.idDokterSatusehat) ? rad.idDokterSatusehat : idDokterFallback;
        performer.addObject().put("reference", "Practitioner/" + idPerformer);
        performer.addObject().put("reference", "Organization/" + idOrgRad);
        if (!isBlank(observationRef)) dr.putArray("result").addObject().put("reference", observationRef);
        if (!isBlank(serviceRequestRef)) dr.putArray("basedOn").addObject().put("reference", serviceRequestRef);
        // Penunjuk ke citra: hanya ada setelah SatuSehatKirimDicomRadiologi berhasil dan router selesai
        // mengindeks. Karena itu langkah DICOM memicu pengiriman ulang resource radiologi (PUT idempotent)
        // supaya laporan yang sudah ada ikut membawa referensi ini.
        if (!isBlank(rad.idImagingStudy)) {
            dr.putArray("imagingStudy").addObject().put("reference", "ImagingStudy/" + rad.idImagingStudy);
        }
        if (!isBlank(rad.hasil)) dr.put("conclusion", bersihkanTeksPlain(rad.hasil));
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

    private void simpanRad(String tabel, String kolomId, EntryMeta m, String idVal) {
        if (isBlank(idVal) || isBlank(m.noOrder)) return;
        Sequel.queryu2("replace into " + tabel + " (noorder,kd_jenis_prw," + kolomId + ") values (?,?,?)",
                3, new String[]{m.noOrder, m.kdJenisPrw, idVal});
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
            System.out.println("Notifikasi Radiologi ambilKonteks : " + e);
        }
        return k;
    }

    private String ambilIdOrganisasiRadiologi() {
        String idOrg = Sequel.cariIsi("select ifnull(id_organisasi_satusehat,'') from satu_sehat_mapping_lokasi_ruangrad limit 1");
        return isBlank(idOrg) ? koneksiDB.IDSATUSEHAT() : idOrg;
    }

    private List<RadiologiData> ambilSemuaRadiologi(String noRawat) {
        List<RadiologiData> result = new ArrayList<>();
        LinkedHashSet<String> unik = new LinkedHashSet<>();
        Map<String, String> cacheDokter = new HashMap<>();
        String sql = "select pr.noorder, ppr.kd_jenis_prw, pr.tgl_permintaan, pr.jam_permintaan, pr.tgl_hasil, pr.jam_hasil, "
                + "ifnull(pr.diagnosa_klinis,'') as diagnosa_klinis, ifnull(jpr.nm_perawatan,'') as nm_perawatan, "
                + "ifnull(maprad.code,'') as code, ifnull(maprad.system,'') as `system`, ifnull(maprad.display,'') as display, "
                + "ifnull(hr.hasil,'') as hasil, ifnull(pg.no_ktp,'') as nik_dokter, "
                + "ifnull(ssr.id_servicerequest,'') as id_servicerequest, "
                + "ifnull(sor.id_observation,'') as id_observation, "
                + "ifnull(sdr.id_diagnosticreport,'') as id_diagnosticreport, "
                + "ifnull(ssi.id_imaging,'') as id_imaging "
                + "from permintaan_radiologi pr "
                + "inner join permintaan_pemeriksaan_radiologi ppr on ppr.noorder = pr.noorder "
                + "inner join jns_perawatan_radiologi jpr on jpr.kd_jenis_prw = ppr.kd_jenis_prw "
                + "left join satu_sehat_mapping_radiologi maprad on maprad.kd_jenis_prw = ppr.kd_jenis_prw "
                + "left join periksa_radiologi prx on prx.no_rawat = pr.no_rawat and prx.kd_jenis_prw = ppr.kd_jenis_prw "
                + "  and prx.tgl_periksa = pr.tgl_hasil and prx.jam = pr.jam_hasil and prx.dokter_perujuk = pr.dokter_perujuk "
                + "left join hasil_radiologi hr on hr.no_rawat = prx.no_rawat and hr.tgl_periksa = prx.tgl_periksa and hr.jam = prx.jam "
                + "left join pegawai pg on pg.nik = prx.kd_dokter "
                + "left join satu_sehat_servicerequest_radiologi ssr on ssr.noorder = ppr.noorder and ssr.kd_jenis_prw = ppr.kd_jenis_prw "
                + "left join satu_sehat_observation_radiologi sor on sor.noorder = ppr.noorder and sor.kd_jenis_prw = ppr.kd_jenis_prw "
                + "left join satu_sehat_diagnosticreport_radiologi sdr on sdr.noorder = ppr.noorder and sdr.kd_jenis_prw = ppr.kd_jenis_prw "
                + "left join satu_sehat_imagingstudy_radiologi ssi on ssi.noorder = ppr.noorder and ssi.kd_jenis_prw = ppr.kd_jenis_prw "
                + "where pr.no_rawat = ? "
                + "order by pr.tgl_permintaan asc, pr.jam_permintaan asc, ppr.kd_jenis_prw asc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    RadiologiData d = new RadiologiData();
                    d.noOrder = r.getString("noorder");
                    d.kdJenisPrw = r.getString("kd_jenis_prw");
                    d.tglPermintaan = r.getString("tgl_permintaan");
                    d.jamPermintaan = r.getString("jam_permintaan");
                    d.tglHasil = r.getString("tgl_hasil");
                    d.jamHasil = r.getString("jam_hasil");
                    d.diagnosaKlinis = r.getString("diagnosa_klinis");
                    d.pemeriksaanNama = r.getString("nm_perawatan");
                    d.code = r.getString("code");
                    d.system = r.getString("system");
                    d.display = r.getString("display");
                    d.hasil = r.getString("hasil");
                    String nikDokter = nz(r.getString("nik_dokter"));
                    if (!isBlank(nikDokter)) {
                        String idDok = cacheDokter.get(nikDokter);
                        if (idDok == null) {
                            idDok = nz(cek.tampilIDParktisi(nikDokter));
                            cacheDokter.put(nikDokter, idDok);
                        }
                        d.idDokterSatusehat = idDok;
                    }
                    d.idServiceRequestLama = r.getString("id_servicerequest");
                    d.idObservationLama = r.getString("id_observation");
                    d.idDiagnosticReportLama = r.getString("id_diagnosticreport");
                    d.idImagingStudy = r.getString("id_imaging");
                    d.adaHasil = !isBlank(d.tglHasil) && !d.tglHasil.startsWith("0000-") && !isBlank(d.hasil);
                    if (isBlank(d.code) || isBlank(d.system)) {
                        System.out.println("Notifikasi Radiologi : order=" + d.noOrder + " kd_jenis_prw=" + d.kdJenisPrw
                                + " (" + d.pemeriksaanNama + ") dilewati. satu_sehat_mapping_radiologi belum dipetakan.");
                        continue;
                    }
                    String key = nz(d.noOrder) + "|" + nz(d.kdJenisPrw);
                    if (!unik.add(key)) continue;
                    result.add(d);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Radiologi ambilSemuaRadiologi : " + e);
        }
        return result;
    }

    // ====================== Util ======================

    private String buildRadiologiOrderIdentifier(RadiologiData rad) {
        return keyBagian(rad.noOrder) + "." + keyBagian(rad.kdJenisPrw);
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
