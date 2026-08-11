/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
 * Pengiriman "Laporan Anestesi" (FHIR Composition - LOINC 84062-9, Anesthesiology procedure note) ke SATUSEHAT.
 *
 * Sumber data: laporan_anestesi (pra-anestesi, jenis anestesi, monitoring, pasca-anestesi).
 * Composition naratif (section.text.div). Idempotent: cari Composition existing lewat encounter, cocokkan
 * identifier ANESTESI-{noRawat} / type 84062-9, lalu PUT (kirim ulang tidak menduplikasi).
 */
public class SatuSehatLaporanAnestesi {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatLaporanAnestesi() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Laporan Anestesi : " + e);
        }
        pastikanTabel();
    }

    /**
     * Tabel pelacak id Composition. Id-nya memang deterministik, tapi tanpa catatan lokal tak ada
     * cara membedakan "sudah pernah dikirim" dari "belum" tanpa bertanya ke server — itulah yang
     * dibutuhkan panel status di form RME.
     */
    private void pastikanTabel() {
        try (PreparedStatement p = koneksi.prepareStatement(
                "create table if not exists satu_sehat_laporan_anestesi ("
                + "no_rawat varchar(17) not null, id_composition varchar(50) default '', "
                + "primary key (no_rawat)) engine=InnoDB default charset=latin1")) {
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi pastikanTabel : " + e);
        }
    }

    /** Id Composition tersimpan. "" bila belum pernah dikirim dari instalasi ini. */
    private String ambilIdLokal(String noRawat) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(id_composition,'') as id_composition from satu_sehat_laporan_anestesi "
                + "where no_rawat=? limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return nz(r.getString("id_composition"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi ambilIdLokal : " + e);
        }
        return "";
    }

    /** Upsert id Composition (idempotent per no_rawat). */
    private void simpanIdLokal(String noRawat, String idComposition) {
        if (idComposition == null || idComposition.equals("")) {
            return;
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "replace into satu_sehat_laporan_anestesi (no_rawat, id_composition) values (?,?)")) {
            p.setString(1, noRawat);
            p.setString(2, idComposition);
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi simpanIdLokal : " + e);
        }
    }

    private static class AnestesiData {
        String waktuMulai="", waktuSelesai="", tindakan="", tempatPantau="", asa="";
        String preEval="", medikasi="", anestesiProsedur="", monitoring="", pascaAnestesi="";
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
        /** Waktu penilaian pasca-anestesi (skor pemulihan); "" bila tidak ada penilaian. */
        String waktuPasca="";
        /** Antibiotik profilaksis perioperatif (timeout sebelum insisi) -> MedicationAdministration. */
        String namaObat="", waktuObat="";
    }

    /**
     * Bangun & kirim Laporan Anestesi untuk satu kunjungan.
     * @param noRawat     no_rawat kunjungan
     * @param idEncounter id Encounter SATUSEHAT (WAJIB — Composition mereferensinya)
     */
    /** PREVIEW: rakit Bundle Laporan Anestesi (Observation+Procedure+Composition) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        AnestesiData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("") || d.idDokter.equals("")) return null;
        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu = d.waktuMulai;
        String obsId  = stableResourceId("ObservationPreAnestesiLap", noRawat);
        String procId = stableResourceId("ProcedureAnestesi", noRawat);
        String pascaId = stableResourceId("ObservationPascaAnestesi", noRawat);
        String obatId = stableResourceId("MedAdmAnestesi", noRawat);
        String compId = stableResourceId("CompositionAnestesi", noRawat);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        tambahEntry(entries, "Observation", obsId,
                buatObservationPreEval(noRawat, d, obsId, patientRef, encounterRef, waktu));
        tambahEntry(entries, "Procedure", procId,
                buatProcedureAnestesi(noRawat, d, procId, patientRef, encounterRef, waktu));
        if (!d.namaObat.equals("")) {
            tambahEntry(entries, "MedicationAdministration", obatId,
                    buatMedicationAdministration(noRawat, d, obatId, patientRef, encounterRef, waktu));
        }
        if (!d.pascaAnestesi.equals("")) {
            tambahEntry(entries, "Observation", pascaId,
                    buatObservationPasca(noRawat, d, pascaId, patientRef, encounterRef, waktu));
        }
        tambahEntry(entries, "Composition", compId,
                buatComposition(noRawat, d, compId, patientRef, encounterRef, waktu, obsId, procId, pascaId, obatId));
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        AnestesiData d = ambilData(noRawat);
        if (d == null) {
            return;   // kunjungan ini tidak punya tindakan operasi/anestesi
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi Laporan Anestesi : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        if (d.idDokter.equals("")) {
            System.out.println("Notifikasi Laporan Anestesi : ID dokter anestesi (Practitioner) belum ada untuk no_rawat "
                    + noRawat + ". Dilewati (Composition wajib author).");
            return;
        }

        String patientRef = "Patient/" + d.idPasien;
        String encounterRef = "Encounter/" + idEncounter;
        String waktu = d.waktuMulai;

        // Sub-resource yang direferensikan Composition.section.entry. Id deterministik -> PUT (idempotent, anti-double).
        String obsId  = stableResourceId("ObservationPreAnestesiLap", noRawat);
        String procId = stableResourceId("ProcedureAnestesi", noRawat);
        String pascaId = stableResourceId("ObservationPascaAnestesi", noRawat);
        String obatId = stableResourceId("MedAdmAnestesi", noRawat);
        // Composition: pakai-ulang id yang SUDAH ada di server (encounter ini) supaya menimpa,
        // bukan membuat duplikat. Bila belum ada -> id deterministik.
        String compId = ambilIdLokal(noRawat);
        if (compId.equals("")) compId = cariIdCompositionServer(idEncounter, "ANESTESI-" + noRawat);
        if (compId.equals("")) compId = stableResourceId("CompositionAnestesi", noRawat);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        tambahEntry(entries, "Observation", obsId,
                buatObservationPreEval(noRawat, d, obsId, patientRef, encounterRef, waktu));
        tambahEntry(entries, "Procedure", procId,
                buatProcedureAnestesi(noRawat, d, procId, patientRef, encounterRef, waktu));
        if (!d.namaObat.equals("")) {
            tambahEntry(entries, "MedicationAdministration", obatId,
                    buatMedicationAdministration(noRawat, d, obatId, patientRef, encounterRef, waktu));
        }
        if (!d.pascaAnestesi.equals("")) {
            tambahEntry(entries, "Observation", pascaId,
                    buatObservationPasca(noRawat, d, pascaId, patientRef, encounterRef, waktu));
        }
        tambahEntry(entries, "Composition", compId,
                buatComposition(noRawat, d, compId, patientRef, encounterRef, waktu, obsId, procId, pascaId, obatId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Laporan Anestesi : " + link);
        System.out.println("Request JSON Anestesi : " + payload);
        // Kirim sbg UTF-8 bytes: StringHttpMessageConverter Spring lama default ISO-8859-1, membuat
        // karakter non-ASCII (°, ±, en/em-dash) di narasi rusak jadi "?"/"ï¿½" di server.
        HttpEntity requestEntity = new HttpEntity(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error Anestesi Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error Anestesi OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error Anestesi Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON Anestesi : " + hasil);
        // Catat id agar kiriman berikutnya tak perlu GET dan status "Sudah Kirim" terbaca lokal.
        simpanIdLokal(noRawat, compId);

        // Bersihkan duplikat: hapus Composition 84062-9 lain di encounter ini selain yg barusan dikirim.
        hapusDuplikatComposition(idEncounter, "ANESTESI-" + noRawat, compId);
    }

    /**
     * Hapus semua Composition di encounter ini yang identifier-nya ANESTESI-{noRawat} atau type 84062-9,
     * KECUALI keepId (yang barusan dikirim). Menghilangkan duplikat dari transisi versi POST->PUT.
     */
    private void hapusDuplikatComposition(String idEncounter, String idVal, String keepId) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            java.net.URI uri = java.net.URI.create(link + "/Composition?encounter=" + idEncounter + "&_count=100");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            for (JsonNode e : r.path("entry")) {
                JsonNode res = e.path("resource");
                String id = nz(res.path("id").asText());
                if (id.equals("") || id.equals(keepId)) continue;
                boolean match = idVal.equals(nz(res.path("identifier").path("value").asText()));
                if (!match) {
                    for (JsonNode cd : res.path("type").path("coding")) {
                        if ("84062-9".equals(cd.path("code").asText())) { match = true; break; }
                    }
                }
                if (!match || !res.isObject()) continue;
                // SATUSEHAT melarang DELETE (400) -> RETRACT: set status=entered-in-error via PUT
                // (resource diabaikan/disembunyikan viewer), kirim ulang resource yg sama dgn status itu.
                try {
                    ObjectNode comp = ((ObjectNode) res).deepCopy();
                    comp.remove("meta");
                    comp.put("status", "entered-in-error");
                    HttpHeaders hp = new HttpHeaders();
                    hp.setContentType(MediaType.APPLICATION_JSON);
                    hp.add("Authorization", "Bearer " + api.TokenSatuSehat());
                    HttpEntity reqPut = new HttpEntity(mapper.writeValueAsString(comp), hp);
                    api.getRest().exchange(link + "/Composition/" + id, HttpMethod.PUT, reqPut, String.class);
                    System.out.println("Retract duplikat Composition Anestesi (entered-in-error) : " + id);
                } catch (Exception ex) {
                    System.out.println("Notifikasi retract Composition " + id + " : " + ex);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi hapusDuplikatComposition : " + e);
        }
    }

    /** Composition Laporan Anestesi (84062-9): section.entry -> Observation (pra-eval) + Procedure (prosedur). */
    private ObjectNode buatComposition(String noRawat, AnestesiData d, String id, String patientRef,
            String encounterRef, String waktu, String obsId, String procId, String pascaId, String obatId) {
        ObjectNode comp = mapper.createObjectNode();
        comp.put("resourceType", "Composition");
        comp.put("id", id);
        ObjectNode iden = comp.putObject("identifier");
        iden.put("system", "http://sys-ids.kemkes.go.id/composition/" + d.idOrg);
        iden.put("value", "ANESTESI-" + noRawat);
        comp.put("status", "final");
        ObjectNode typeCoding = comp.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://loinc.org");
        typeCoding.put("code", "84062-9");
        typeCoding.put("display", "Anesthesiology procedure note");
        comp.put("title", "Laporan Anestesi");
        if (!waktu.equals("")) comp.put("date", waktu);
        comp.putObject("subject").put("reference", patientRef);
        comp.putObject("encounter").put("reference", encounterRef);
        ObjectNode author = comp.putArray("author").addObject();
        author.put("reference", "Practitioner/" + d.idDokter);
        author.put("display", d.namaDokter);
        ObjectNode attester = comp.putArray("attester").addObject();
        attester.put("mode", "legal");
        if (!waktu.equals("")) attester.put("time", waktu);
        attester.putObject("party").put("reference", "Practitioner/" + d.idDokter);
        comp.putObject("custodian").put("reference", "Organization/" + d.idOrg);

        // Narrative (text.div) WAJIB.
        ObjectNode compText = comp.putObject("text");
        compText.put("status", "generated");
        compText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">Laporan Anestesi - "
                + escapeXml(d.namaPasien) + "</div>");

        // section.entry -> resource terpisah (sesuai struktur SATUSEHAT).
        ArrayNode section = comp.putArray("section");
        tambahSectionEntry(section, "Pre-Anesthesia Evaluation", "Observation/" + obsId, d.preEval);
        tambahSectionEntry(section, "Anesthesia Procedure", "Procedure/" + procId, d.anestesiProsedur);
        // Obat perioperatif: hanya antibiotik profilaksis yang tercatat terstruktur.
        if (!d.namaObat.equals("")) {
            tambahSectionEntry(section, "Medications", "MedicationAdministration/" + obatId, d.medikasi);
        }
        // Section pasca-anestesi hanya dibuat bila skor pemulihannya memang dinilai petugas.
        if (!d.pascaAnestesi.equals("")) {
            tambahSectionEntry(section, "Post-Anesthesia", "Observation/" + pascaId, d.pascaAnestesi);
        }
        return comp;
    }

    /**
     * MedicationAdministration untuk antibiotik profilaksis perioperatif.
     *
     * medicationCodeableConcept hanya berisi `text` (nama obat apa adanya dari form time-out) karena
     * kolomnya memang teks bebas, tanpa kode KFA. Sudah diuji ke staging: diterima 200/201.
     */
    private ObjectNode buatMedicationAdministration(String noRawat, AnestesiData d, String id,
            String patientRef, String encounterRef, String waktu) {
        ObjectNode ma = mapper.createObjectNode();
        ma.put("resourceType", "MedicationAdministration");
        ma.put("id", id);
        ObjectNode iden = ma.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/medicationadministration/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "ANESOBAT-" + noRawat);
        ma.put("status", "completed");
        ma.putObject("medicationCodeableConcept").put("text", d.namaObat);
        ma.putObject("subject").put("reference", patientRef);
        ma.putObject("context").put("reference", encounterRef);
        String saat = d.waktuObat.equals("") ? waktu : d.waktuObat;
        if (!saat.equals("")) ma.put("effectiveDateTime", saat);
        if (!d.idDokter.equals("")) {
            ObjectNode actor = ma.putArray("performer").addObject().putObject("actor");
            actor.put("reference", "Practitioner/" + d.idDokter);
            actor.put("display", d.namaDokter);
        }
        return ma;
    }

    /**
     * Observation "Post-Anesthesia" — skor pemulihan (Bromage/Aldrete/Steward) beserta kriteria
     * keluar & instruksi, sebagai valueString.
     *
     * Kode LOINC 48767-8 (Annotation comment) dipakai dengan sengaja: skala pemulihan pasca-anestesi
     * di sini gabungan tiga skala berbeda dengan skor yang tidak sebanding satu sama lain, dan kode
     * LOINC spesifik per skala belum diverifikasi diterima SATUSEHAT. 48767-8 sudah terbukti diterima.
     */
    private ObjectNode buatObservationPasca(String noRawat, AnestesiData d, String id, String patientRef,
            String encounterRef, String waktu) {
        ObjectNode o = mapper.createObjectNode();
        o.put("resourceType", "Observation");
        o.put("id", id);
        ObjectNode iden = o.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/observation/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "ANESPASCA-" + noRawat);
        o.put("status", "final");
        ObjectNode cat = o.putArray("category").addObject().putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        cat.put("code", "survey");
        cat.put("display", "Survey");
        ObjectNode code = o.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", "http://loinc.org");
        cc.put("code", "48767-8");
        cc.put("display", "Annotation comment");
        code.put("text", "Penilaian Pemulihan Pasca-Anestesi");
        o.putObject("subject").put("reference", patientRef);
        o.putObject("encounter").put("reference", encounterRef);
        String saat = d.waktuPasca.equals("") ? waktu : d.waktuPasca;
        if (!saat.equals("")) o.put("effectiveDateTime", saat);
        if (!d.idDokter.equals("")) {
            ObjectNode perf = o.putArray("performer").addObject();
            perf.put("reference", "Practitioner/" + d.idDokter);
            perf.put("display", d.namaDokter);
        }
        o.put("valueString", d.pascaAnestesi);
        return o;
    }

    /** Observation "Pre-Anesthesia Evaluation" (LOINC 34751-8) — component ASA + note ringkasan vitals/lab. */
    private ObjectNode buatObservationPreEval(String noRawat, AnestesiData d, String id, String patientRef,
            String encounterRef, String waktu) {
        ObjectNode o = mapper.createObjectNode();
        o.put("resourceType", "Observation");
        o.put("id", id);
        ObjectNode iden = o.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/observation/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "ANESPRE-" + noRawat);
        o.put("status", "final");
        ObjectNode cat = o.putArray("category").addObject().putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        cat.put("code", "exam");
        cat.put("display", "Exam");
        ObjectNode code = o.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", "http://loinc.org");
        cc.put("code", "34751-8");
        cc.put("display", "Anesthesia Preoperative evaluation and management note");
        code.put("text", "Evaluasi Pra-Anestesi");
        o.putObject("subject").put("reference", patientRef);
        o.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) o.put("effectiveDateTime", waktu);
        if (!d.idDokter.equals("")) {
            ObjectNode perf = o.putArray("performer").addObject();
            perf.put("reference", "Practitioner/" + d.idDokter);
            perf.put("display", d.namaDokter);
        }
        String[] asaVal = asaClass(d.asa);
        if (asaVal != null) {
            ObjectNode comp = o.putArray("component").addObject();
            ObjectNode compCode = comp.putObject("code");
            ObjectNode ccCode = compCode.putArray("coding").addObject();
            ccCode.put("system", "http://snomed.info/sct");
            ccCode.put("code", "273270000");
            ccCode.put("display", "American Society of Anesthesiologists physical status classification");
            compCode.put("text", "ASA " + d.asa.trim());
            ObjectNode val = comp.putObject("valueCodeableConcept").putArray("coding").addObject();
            val.put("system", "http://snomed.info/sct");
            val.put("code", asaVal[0]);
            val.put("display", asaVal[1]);
        } else {
            // Observation wajib value/component/dataAbsentReason.
            ObjectNode dar = o.putObject("dataAbsentReason").putArray("coding").addObject();
            dar.put("system", "http://terminology.hl7.org/CodeSystem/data-absent-reason");
            dar.put("code", "unknown");
            dar.put("display", "Unknown");
        }
        if (!d.preEval.equals("")) {
            o.putArray("note").addObject().put("text", d.preEval);
        }
        return o;
    }

    /** Procedure "Anesthesia Procedure" (SNOMED 399097000) — teknik/jenis anestesi di note. */
    private ObjectNode buatProcedureAnestesi(String noRawat, AnestesiData d, String id, String patientRef,
            String encounterRef, String waktu) {
        ObjectNode pr = mapper.createObjectNode();
        pr.put("resourceType", "Procedure");
        pr.put("id", id);
        ObjectNode iden = pr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/procedure/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", "ANESPROC-" + noRawat);
        pr.put("status", "completed");
        ObjectNode code = pr.putObject("code");
        ObjectNode cc = code.putArray("coding").addObject();
        cc.put("system", "http://snomed.info/sct");
        cc.put("code", "399097000");
        cc.put("display", "Administration of anesthesia");
        code.put("text", d.tindakan.equals("") ? "Tindakan Anestesi" : ("Anestesi - " + d.tindakan));
        pr.putObject("subject").put("reference", patientRef);
        pr.putObject("encounter").put("reference", encounterRef);
        if (!waktu.equals("")) pr.put("performedDateTime", waktu);
        if (!d.idDokter.equals("")) {
            ObjectNode perf = pr.putArray("performer").addObject();
            ObjectNode actor = perf.putObject("actor");
            actor.put("reference", "Practitioner/" + d.idDokter);
            actor.put("display", d.namaDokter);
        }
        if (!d.anestesiProsedur.equals("")) {
            pr.putArray("note").addObject().put("text", d.anestesiProsedur);
        }
        return pr;
    }

    /** Tambah 1 entry PUT (id deterministik) ke transaction bundle. */
    private void tambahEntry(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    /** Section dengan entry (referensi resource) + text naratif. */
    private void tambahSectionEntry(ArrayNode section, String title, String ref, String narasi) {
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        String body = (narasi == null || narasi.trim().equals("")) ? title : narasi.trim();
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(body) + "</div>");
        sec.putArray("entry").addObject().put("reference", ref);
    }

    /** {kode, display} SNOMED ASA physical status class; null bila tak terpetakan (selain 1-5). */
    private String[] asaClass(String asa) {
        String a = (asa == null) ? "" : asa.trim();
        if (a.startsWith("1")) return new String[]{"413495001", "American Society of Anesthesiologists physical status class 1"};
        if (a.startsWith("2")) return new String[]{"413496000", "American Society of Anesthesiologists physical status class 2"};
        if (a.startsWith("3")) return new String[]{"413497009", "American Society of Anesthesiologists physical status class 3"};
        if (a.startsWith("4")) return new String[]{"413498004", "American Society of Anesthesiologists physical status class 4"};
        if (a.startsWith("5")) return new String[]{"413499007", "American Society of Anesthesiologists physical status class 5"};
        return null;
    }

    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(resourceType);
        if (keys != null) {
            for (String key : keys) sb.append("|").append(key == null ? "-" : key.trim());
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    /** Tambah section naratif (title + text.div) bila isi tidak kosong. */
    private void tambahSection(ArrayNode section, String title, String narasi) {
        if (narasi == null || narasi.trim().equals("") || narasi.trim().equals("-")) return;
        ObjectNode sec = section.addObject();
        sec.put("title", title);
        ObjectNode secText = sec.putObject("text");
        secText.put("status", "generated");
        secText.put("div", "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + escapeXml(narasi.trim()) + "</div>");
    }

    /**
     * Sumber utama adalah tabel `operasi` (tindakan anestesi selalu tercatat di sana), BUKAN
     * `laporan_anestesi`. Tabel laporan_anestesi praktis tidak pernah diisi di instalasi ini,
     * sehingga versi lama tidak pernah punya data untuk pasien mana pun. laporan_anestesi tetap
     * dibaca sebagai pengaya bila kebetulan terisi -- isinya paling lengkap.
     */
    private AnestesiData ambilData(String noRawat) {
        AnestesiData d = null;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select o.tgl_operasi, ifnull(o.jenis_anasthesi,'') as jenis_anasthesi, "
                + "ifnull(o.dokter_anestesi,'') as dokter_anestesi, "
                + "ifnull(lo.selesaioperasi,'') as selesaioperasi, "
                + "ifnull(lo.diagnosa_preop,'') as diagnosa_preop, ifnull(po.nm_perawatan,'') as paket, "
                + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                + "ifnull(pga.no_ktp,'') as ktp_anestesi, ifnull(pga.nama,'') as nama_pegawai, "
                + "ifnull(dra.nm_dokter,'') as nama_dokter "
                + "from operasi o "
                + "inner join reg_periksa rp on rp.no_rawat=o.no_rawat "
                + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                + "left join laporan_operasi lo on lo.no_rawat=o.no_rawat "
                + "left join paket_operasi po on po.kode_paket=o.kode_paket "
                + "left join pegawai pga on pga.nik=o.dokter_anestesi "
                + "left join dokter dra on dra.kd_dokter=o.dokter_anestesi "
                + "where o.no_rawat=? order by o.tgl_operasi desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    d = new AnestesiData();
                    d.waktuMulai = formatWaktu(nz(r.getString("tgl_operasi")));
                    d.waktuSelesai = formatWaktu(nz(r.getString("selesaioperasi")));
                    d.tindakan = bersihkan(r.getString("paket"));
                    d.namaPasien = nz(r.getString("nm_pasien"));
                    // nm_dokter sudah bergelar; nama pegawai dipakai bila anestesi dijalankan non-dokter.
                    d.namaDokter = !nz(r.getString("nama_dokter")).equals("")
                            ? nz(r.getString("nama_dokter")) : nz(r.getString("nama_pegawai"));
                    d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_anestesi"))));
                    d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                    d.idOrg = koneksiDB.IDSATUSEHAT();
                    String jenis = bersihkan(r.getString("jenis_anasthesi"));
                    if (!jenis.equals("") && !jenis.equals("-")) {
                        d.anestesiProsedur = "Jenis Anestesi: " + jenis;
                    }
                    d.preEval = gabungLabel("Diagnosa Pra-Operasi", bersihkan(r.getString("diagnosa_preop")));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi ambilData : " + e);
        }
        if (d == null) return null;
        lengkapiPreAnestesi(noRawat, d);
        lengkapiSigninChecklist(noRawat, d);
        lengkapiTimeout(noRawat, d);
        lengkapiSignoutChecklistPost(noRawat, d);
        lengkapiPascaAnestesi(noRawat, d);
        lengkapiLaporanAnestesi(noRawat, d);
        return d;
    }

    /**
     * Penilaian pra-anestesi resmi (ASA, rencana teknik, riwayat). Jarang terisi di instalasi ini
     * tapi paling bernilai klinis kalau ada, jadi tetap dibaca.
     */
    private void lengkapiPreAnestesi(String noRawat, AnestesiData d) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(asa,'') as asa, ifnull(rencana_anestesi,'') as rencana_anestesi, "
                + "ifnull(diagnosa,'') as diagnosa, ifnull(rencana_tindakan,'') as rencana_tindakan, "
                + "ifnull(td,'') as td, ifnull(nadi,'') as nadi, ifnull(pernapasan,'') as pernapasan, "
                + "ifnull(suhu,'') as suhu, ifnull(io2,'') as io2, ifnull(bb,'') as bb, ifnull(tb,'') as tb, "
                + "ifnull(riwayat_penyakit_alergiobat,'') as alergi_obat, "
                + "ifnull(riwayat_penyakit_alergilainnya,'') as alergi_lain, "
                + "ifnull(catatan_khusus,'') as catatan_khusus "
                + "from penilaian_pre_anestesi where no_rawat=? order by tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    if (d.asa.equals("")) d.asa = nz(r.getString("asa"));
                    if (d.tindakan.equals("")) d.tindakan = bersihkan(r.getString("rencana_tindakan"));
                    String rencana = nz(r.getString("rencana_anestesi"));
                    if (!rencana.equals("")) {
                        d.anestesiProsedur = gabung(d.anestesiProsedur, "Rencana Anestesi: " + rencana);
                    }
                    d.preEval = gabung(d.preEval, gabungLabel(
                            "Diagnosa", bersihkan(r.getString("diagnosa")),
                            "Status ASA", asaText(nz(r.getString("asa"))),
                            "Berat Badan", lampiranSatuan(r.getString("bb"), "kg"),
                            "Tinggi Badan", lampiranSatuan(r.getString("tb"), "cm"),
                            "Tekanan Darah", lampiranSatuan(r.getString("td"), "mmHg"),
                            "Nadi", lampiranSatuan(r.getString("nadi"), "x/menit"),
                            "Pernapasan", lampiranSatuan(r.getString("pernapasan"), "x/menit"),
                            "Suhu", lampiranSatuan(r.getString("suhu"), "C"),
                            "SpO2", lampiranSatuan(r.getString("io2"), "%"),
                            "Alergi Obat", nz(r.getString("alergi_obat")),
                            "Alergi Lainnya", nz(r.getString("alergi_lain")),
                            "Catatan Khusus", bersihkan(r.getString("catatan_khusus"))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiPreAnestesi : " + e);
        }
    }

    /**
     * Sign-in sebelum anestesi + checklist pra-operasi: bukan evaluasi pra-anestesi lengkap, tapi
     * inilah yang benar-benar diisi petugas dan memuat butir keselamatan anestesi (alergi, risiko
     * aspirasi, risiko kehilangan darah, kesiapan alat & obat anestesi).
     */
    private void lengkapiSigninChecklist(String noRawat, AnestesiData d) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(alergi,'') as alergi, ifnull(resiko_aspirasi,'') as resiko_aspirasi, "
                + "ifnull(resiko_aspirasi_rencana_antisipasi,'') as antisipasi_aspirasi, "
                + "ifnull(resiko_kehilangan_darah,'') as resiko_darah, "
                + "ifnull(resiko_kehilangan_darah_line,'') as line_darah, "
                + "ifnull(resiko_kehilangan_darah_rencana_antisipasi,'') as antisipasi_darah, "
                + "ifnull(kesiapan_alat_obat_anestesi,'') as kesiapan, "
                + "ifnull(kesiapan_alat_obat_anestesi_rencana_antisipasi,'') as antisipasi_alat "
                + "from signin_sebelum_anestesi where no_rawat=? order by tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    d.preEval = gabung(d.preEval, gabungLabel(
                            "Alergi", nz(r.getString("alergi")),
                            "Risiko Aspirasi", nz(r.getString("resiko_aspirasi")),
                            "Antisipasi Aspirasi", nz(r.getString("antisipasi_aspirasi")),
                            "Risiko Kehilangan Darah", nz(r.getString("resiko_darah")),
                            "Akses/Line", nz(r.getString("line_darah")),
                            "Antisipasi Kehilangan Darah", nz(r.getString("antisipasi_darah"))));
                    d.monitoring = gabung(d.monitoring, gabungLabel(
                            "Kesiapan Alat & Obat Anestesi", nz(r.getString("kesiapan")),
                            "Antisipasi Kesiapan Alat", nz(r.getString("antisipasi_alat"))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiSignin : " + e);
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(surat_ijin_anestesi,'') as ijin, ifnull(keadaan_umum,'') as keadaan_umum, "
                + "ifnull(persiapan_darah,'') as persiapan_darah, ifnull(keterangan_persiapan_darah,'') as ket_darah "
                + "from checklist_pre_operasi where no_rawat=? order by tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    d.preEval = gabung(d.preEval, gabungLabel(
                            "Keadaan Umum", nz(r.getString("keadaan_umum")),
                            "Surat Izin Anestesi", nz(r.getString("ijin")),
                            "Persiapan Darah", nz(r.getString("persiapan_darah")),
                            "Keterangan Persiapan Darah", nz(r.getString("ket_darah"))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiChecklistPre : " + e);
        }
    }

    /**
     * Time-out sebelum insisi: antibiotik profilaksis (satu-satunya obat perioperatif yang tercatat
     * terstruktur di instalasi ini), antisipasi kehilangan darah, dan hal khusus yang diperhatikan.
     */
    private void lengkapiTimeout(String noRawat, AnestesiData d) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select tanggal, ifnull(antibiotik_profilaks,'') as ab, ifnull(nama_antibiotik,'') as nama_ab, "
                + "ifnull(jam_pemberian,'') as jam_ab, ifnull(lama_operasi,'') as lama_operasi, "
                + "ifnull(antisipasi_kehilangan_darah,'') as antisipasi_darah, "
                + "ifnull(hal_khusus_diperhatikan,'') as hal_khusus "
                + "from timeout_sebelum_insisi where no_rawat=? order by tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    String namaAb = bersihkan(r.getString("nama_ab"));
                    String jamAb = nz(r.getString("jam_ab")).trim();
                    if (!namaAb.equals("") && !namaAb.equals("-")) {
                        d.namaObat = namaAb;
                        d.waktuObat = waktuDariJam(nz(r.getString("tanggal")), jamAb);
                        d.medikasi = gabung(d.medikasi, gabungLabel(
                                "Antibiotik Profilaksis", namaAb,
                                "Jam Pemberian", jamAb));
                    } else if (nz(r.getString("ab")).equals("Tidak")) {
                        d.medikasi = gabung(d.medikasi, "Antibiotik Profilaksis: Tidak diberikan");
                    }
                    d.preEval = gabung(d.preEval, gabungLabel(
                            "Antisipasi Kehilangan Darah", bersihkan(r.getString("antisipasi_darah")),
                            "Hal Khusus Diperhatikan", bersihkan(r.getString("hal_khusus")),
                            "Perkiraan Lama Operasi", nz(r.getString("lama_operasi"))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiTimeout : " + e);
        }
    }

    /**
     * Sign-out sebelum menutup luka + checklist pasca-operasi: keadaan umum pasien saat keluar kamar
     * operasi dan perhatian utama fase pemulihan -- keduanya isi Post-Anesthesia.
     */
    private void lengkapiSignoutChecklistPost(String noRawat, AnestesiData d) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(perhatian_utama_fase_pemulihan,'') as perhatian, "
                + "ifnull(peninjauan_kegiatan_dokter_anestesi,'') as tinjau_anestesi "
                + "from signout_sebelum_menutup_luka where no_rawat=? order by tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    d.pascaAnestesi = gabung(d.pascaAnestesi, gabungLabel(
                            "Perhatian Utama Fase Pemulihan", bersihkan(r.getString("perhatian"))));
                    d.monitoring = gabung(d.monitoring, gabungLabel(
                            "Peninjauan Kegiatan Dokter Anestesi", nz(r.getString("tinjau_anestesi"))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiSignout : " + e);
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "select tanggal, ifnull(keadaan_umum,'') as keadaan_umum, "
                + "ifnull(jenis_cairan_infus,'') as infus "
                + "from checklist_post_operasi where no_rawat=? order by tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    String isi = gabungLabel(
                            "Keadaan Umum Pasca-Operasi", nz(r.getString("keadaan_umum")),
                            "Cairan Infus", bersihkan(r.getString("infus")));
                    if (!isi.equals("")) {
                        d.pascaAnestesi = gabung(d.pascaAnestesi, isi);
                        if (d.waktuPasca.equals("")) d.waktuPasca = formatWaktu(nz(r.getString("tanggal")));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiChecklistPost : " + e);
        }
    }

    /** Gabung tanggal form dengan jam pemberian gaya "13.00"/"13:00"; jatuh ke tanggal saja bila jam tak jelas. */
    private String waktuDariJam(String tanggal, String jam) {
        String t = nz(tanggal).trim();
        if (t.equals("") || t.startsWith("0000-00-00")) return "";
        if (t.length() > 10) t = t.substring(0, 10);
        String j = nz(jam).trim().replace('.', ':');
        if (!j.matches("^\\d{1,2}:\\d{2}$")) return formatWaktu(t);
        if (j.length() == 4) j = "0" + j;
        return formatWaktu(t + " " + j + ":00");
    }

    /**
     * Skor pemulihan pasca-anestesi. Tiga skala dipakai bergantung jenis anestesi & usia pasien:
     * Bromage (spinal/regional), Aldrete (umum dewasa), Steward (anak). Semua yang ada dikirim.
     */
    private void lengkapiPascaAnestesi(String noRawat, AnestesiData d) {
        String[][] skala = {
            {"skor_bromage_pasca_anestesi",  "Bromage",  "penilaian_skala1"},
            {"skor_aldrette_pasca_anestesi", "Aldrete",  "penilaian_totalnilai"},
            {"skor_steward_pasca_anestesi",  "Steward",  "penilaian_totalnilai"},
        };
        for (String[] s : skala) {
            boolean pakaiTotal = s[2].equals("penilaian_totalnilai");
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select tanggal, " + (pakaiTotal ? "ifnull(penilaian_totalnilai,'')" : "ifnull(penilaian_nilai1,'')")
                    + " as nilai, ifnull(penilaian_skala1,'') as skala1, "
                    + "ifnull(keluar,'') as keluar, ifnull(instruksi,'') as instruksi "
                    + "from " + s[0] + " where no_rawat=? order by tanggal desc limit 1")) {
                p.setString(1, noRawat);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) {
                        String isi = gabungLabel(
                                "Skor " + s[1], nz(r.getString("nilai")),
                                "Penilaian", bersihkan(r.getString("skala1")),
                                "Kriteria Keluar", bersihkan(r.getString("keluar")),
                                "Instruksi", bersihkan(r.getString("instruksi")));
                        if (!isi.equals("")) {
                            d.pascaAnestesi = gabung(d.pascaAnestesi, isi);
                            if (d.waktuPasca.equals("")) d.waktuPasca = formatWaktu(nz(r.getString("tanggal")));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi Anestesi lengkapiPasca (" + s[0] + ") : " + e);
            }
        }
    }

    /** Pengaya dari laporan_anestesi bila kebetulan terisi (paling lengkap, tapi jarang ada). */
    private void lengkapiLaporanAnestesi(String noRawat, AnestesiData d) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select la.* from laporan_anestesi la where la.no_rawat=? order by la.tanggal desc limit 1")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return;
                if (d.asa.equals("")) d.asa = nz(r.getString("asa"));
                if (d.tindakan.equals("")) d.tindakan = bersihkan(r.getString("rencana_tindakan"));

                // Kesadaran (GCS E/M/V + total)
                String e = nz(r.getString("e")), m = nz(r.getString("m")), v = nz(r.getString("v"));
                String gcs = "";
                if (!e.equals("") || !m.equals("") || !v.equals("")) gcs = "E" + e + "M" + m + "V" + v;
                String gcsTotal = nz(r.getString("gcs"));
                if (!gcsTotal.equals("")) gcs = (gcs.equals("") ? "" : gcs + " ") + "(GCS " + gcsTotal + ")";

                // Section 1: Evaluasi Pra-Anestesi
                d.preEval = gabung(d.preEval, gabungLabel(
                        "Diagnosa", bersihkan(r.getString("diagnosa")),
                        "Rencana Tindakan", bersihkan(r.getString("rencana_tindakan")),
                        "Status ASA", asaText(nz(r.getString("asa"))),
                        "Alergi", nz(r.getString("alergi")),
                        "Asma", nz(r.getString("asma")),
                        "Hipertensi", nz(r.getString("hipertensi")),
                        "Diabetes", nz(r.getString("diabetes")),
                        "Penyakit Penyerta", bersihkan(r.getString("penyakit_penyerta")),
                        "Terapi", bersihkan(r.getString("terapi")),
                        "Berat Badan", lampiranSatuan(r.getString("bb"), "kg"),
                        "Tinggi Badan", lampiranSatuan(r.getString("tb"), "cm"),
                        "Kesadaran", gcs,
                        "Tekanan Darah", lampiranSatuan(r.getString("td"), "mmHg"),
                        "Nadi", lampiranSatuan(r.getString("nadi"), "x/menit"),
                        "Pernapasan", lampiranSatuan(r.getString("pernapasan"), "x/menit"),
                        "SpO2/IO2", lampiranSatuan(r.getString("io2"), "%"),
                        "Jantung/Paru", bersihkan(r.getString("jantung_paru")),
                        "Puasa", lampiranSatuan(r.getString("puasa"), "jam"),
                        "Lab", gabungLabel(
                                "Hb", nz(r.getString("hb")), "Ht", nz(r.getString("ht")),
                                "Leko", nz(r.getString("leko")), "Tr", nz(r.getString("tr")),
                                "BT", nz(r.getString("bt")), "CT", nz(r.getString("ct")),
                                "GDS", nz(r.getString("lab_gds"))),
                        "EKG", nz(r.getString("ekg")),
                        "Thorak Foto", nz(r.getString("thorak_foto")),
                        "Pemeriksaan Lain", bersihkan(r.getString("periksa_lain"))));

                // Section 2: Cek List Persiapan Anestesi (Ya/Tidak)
                d.monitoring = gabung(d.monitoring, gabungLabel(
                        "Informed Consent", nz(r.getString("informed_consent")),
                        "Mesin Anestesia", nz(r.getString("mesin_anestesia")),
                        "Obat Anestesia", nz(r.getString("obat_anestesia")),
                        "Monitoring", nz(r.getString("monitoring")),
                        "TL Jalan Nafas", nz(r.getString("tl_jalan_nafas")),
                        "Obat Emergensi", nz(r.getString("obat_emergensi")),
                        "Suction Aparatus", nz(r.getString("suctiaparatus"))));

                // Section 3: Premedikasi
                d.medikasi = gabung(d.medikasi, gabungLabel("Premedikasi", bersihkan(r.getString("premedikasi"))));

                // Section 4: Jenis & Teknik Anestesi
                // Jenis anestesi sudah diambil dari tabel operasi; jangan ulangi labelnya di sini.
                boolean jenisSudahAda = d.anestesiProsedur.contains("Jenis Anestesi");
                d.anestesiProsedur = gabung(d.anestesiProsedur, gabungLabel(
                        "Jenis Anestesi", jenisSudahAda ? "" : nz(r.getString("jenis_anastesi")),
                        "Regional", nz(r.getString("regional_anestesi")),
                        "Lokasi", nz(r.getString("lokasi")),
                        "Jarum", nz(r.getString("jarum")),
                        "Kateter", nz(r.getString("kateter")),
                        "Obat", bersihkan(r.getString("obat")),
                        "Induksi", bersihkan(r.getString("induksi")),
                        "Jalan Nafas", nz(r.getString("jalan_nafas")),
                        "Inhalasi", nz(r.getString("inhalasi")),
                        "Intravena", bersihkan(r.getString("intravena")),
                        "Pernafasan", nz(r.getString("pernafasan"))));

                // Dokter anestesi versi tabel ini hanya dipakai bila tabel operasi tidak memberinya.
                if (d.namaDokter.equals("")) d.namaDokter = nz(r.getString("nm_dokter_anastesi"));
                if (d.idDokter.equals("")) {
                    String ktp = cariSatu("select no_ktp from pegawai where nik=? limit 1",
                            nz(r.getString("kd_dokter_anastesi")));
                    d.idDokter = nz(cek.tampilIDParktisi(ktp));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi lengkapiLaporanAnestesi : " + e);
        }
    }

    private String cariSatu(String sql, String param) {
        if (param == null || param.trim().equals("")) return "";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, param);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return nz(r.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi cariSatu : " + e);
        }
        return "";
    }

    /** Gabung beberapa potong narasi dengan pemisah "; ", melewati yang kosong. */
    private String gabung(String... bagian) {
        StringBuilder sb = new StringBuilder();
        for (String b : bagian) {
            String v = nz(b).trim();
            if (v.equals("") || v.equals("-")) continue;
            if (sb.length() > 0) sb.append("; ");
            sb.append(v);
        }
        return sb.toString();
    }

    /** "ASA {n}" bila terisi, "" bila kosong. */
    private String asaText(String asa) {
        String a = nz(asa).trim();
        return a.equals("") ? "" : ("ASA " + a);
    }

    private String lampiranSatuan(String v, String satuan) {
        String t = nz(v).trim();
        if (t.equals("") || t.equals("0")) return "";
        return t + " " + satuan;
    }

    /**
     * Cari id Composition existing lewat search by encounter, lalu cocokkan identifier ANESTESI-{noRawat}
     * atau type 84062-9. "" bila tidak ada / gagal.
     */
    private String cariIdCompositionServer(String idEncounter, String idenValue) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            java.net.URI uri = java.net.URI.create(link + "/Composition?encounter=" + idEncounter + "&_count=100");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            JsonNode es = r.path("entry");
            if (es.isArray()) {
                for (JsonNode e : es) {
                    JsonNode res = e.path("resource");
                    if (idenValue.equals(nz(res.path("identifier").path("value").asText()))) {
                        return nz(res.path("id").asText());
                    }
                    for (JsonNode cd : res.path("type").path("coding")) {
                        if ("84062-9".equals(cd.path("code").asText())) {
                            return nz(res.path("id").asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Anestesi cariIdCompositionServer : " + e);
        }
        return "";
    }

    // ====================== UTIL ======================

    private String formatWaktu(String dt) {
        if (dt == null || dt.trim().equals("")) return "";
        String t = dt.trim();
        if (t.startsWith("0000-00-00")) return "";
        if (t.contains(".")) t = t.substring(0, t.indexOf('.'));
        if (t.contains(" ")) t = t.replace(" ", "T");
        if (t.length() == 10) t = t + "T00:00:00";
        return t + "+07:00";
    }

    /** Gabung pasangan (label, value) yang value-nya terisi, dipisah ". ". */
    private String gabungLabel(String... labelValue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < labelValue.length; i += 2) {
            String label = nz(labelValue[i]).trim();
            String value = nz(labelValue[i + 1]).trim();
            if (!value.equals("") && !value.equals("-")) {
                if (sb.length() > 0) sb.append(". ");
                sb.append(label).append(": ").append(value);
            }
        }
        return sb.toString();
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String bersihkan(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
