/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.UUID;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sub-sender Obat Kronis (MedicationRequest + MedicationDispense) untuk flow SatuSehatBundle.
 *
 * Sumber: bridging_resep_apotek_bpjs (apotek BPJS/PRB) yg terhubung ke no_rawat lewat bridging_sep.no_sep,
 * + nonracikan + satu_sehat_mapping_obat (KFA). Per item obat ber-KFA valid: POST MedicationRequest lalu
 * MedicationDispense (rujuk MR). Idempotent via tabel satu_sehat_obat_kronis (skip yg sudah terkirim).
 * Mengikuti pola SatuSehatKirimObatKronis (menu standalone).
 */
public class SatuSehatObatKronis {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatObatKronis() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi ObatKronis : " + e);
        }
    }

    private static class ObatK {
        String noSepApotek="", kodeBrng="", namaObat="", kfa="", kfaDisplay="";
        String jml="1", signa="1", tgl="";
        String ktpDokter="", namaDokter="";
    }

    /** PREVIEW: rakit SATU Bundle obat kronis (MedicationRequest+MedicationDispense per obat) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        String ktpPasien = "", namaPasien = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select p.no_ktp, p.nm_pasien from reg_periksa rp "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                ktpPasien = nz(r.getString("no_ktp"));
                namaPasien = nz(r.getString("nm_pasien"));
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ObatKronis konteks bangun : " + e);
        }
        if (ktpPasien.equals("")) return null;
        String idPasien = nz(cek.tampilIDPasien(ktpPasien));
        if (idPasien.equals("")) return null;
        List<ObatK> daftar = ambilObatKronis(noRawat);
        if (daftar.isEmpty()) return null;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        for (ObatK o : daftar) {
            String idDokter = nz(cek.tampilIDParktisi(o.ktpDokter));
            if (idDokter.equals("")) continue;
            String mrRefId = "preview-mr-" + o.kodeBrng;
            ObjectNode mr = buatMedicationRequest(o, idPasien, namaPasien, idDokter, idEncounter);
            ObjectNode e1 = entries.addObject();
            e1.put("fullUrl", "urn:uuid:" + UUID.randomUUID().toString());
            e1.set("resource", mr);
            e1.putObject("request").put("method", "POST").put("url", "MedicationRequest");
            ObjectNode md = buatMedicationDispense(o, idPasien, namaPasien, idDokter, idEncounter, mrRefId);
            ObjectNode e2 = entries.addObject();
            e2.put("fullUrl", "urn:uuid:" + UUID.randomUUID().toString());
            e2.set("resource", md);
            e2.putObject("request").put("method", "POST").put("url", "MedicationDispense");
        }
        if (entries.size() == 0) return null;
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return;

        // Konteks pasien (NIK + nama) dari rawat ini.
        String ktpPasien = "", namaPasien = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select p.no_ktp, p.nm_pasien from reg_periksa rp "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                ktpPasien = nz(r.getString("no_ktp"));
                namaPasien = nz(r.getString("nm_pasien"));
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ObatKronis konteks : " + e);
        }
        if (ktpPasien.equals("")) return;
        String idPasien = nz(cek.tampilIDPasien(ktpPasien));
        if (idPasien.equals("")) return;

        List<ObatK> daftar = ambilObatKronis(noRawat);
        if (daftar.isEmpty()) return;   // tak ada obat kronis utk rawat ini -> lewati

        int ok = 0, skip = 0;
        for (ObatK o : daftar) {
            String idDokter = nz(cek.tampilIDParktisi(o.ktpDokter));
            if (idDokter.equals("")) { skip++; continue; }
            try {
                // 1) MedicationRequest
                String mrId = postKronis("MedicationRequest",
                        buatMedicationRequest(o, idPasien, namaPasien, idDokter, idEncounter));
                if (mrId.equals("")) { skip++; continue; }
                // 2) MedicationDispense (rujuk MR)
                String mdId = postKronis("MedicationDispense",
                        buatMedicationDispense(o, idPasien, namaPasien, idDokter, idEncounter, mrId));
                if (mdId.equals("")) { skip++; continue; }
                Sequel.queryu2("replace into satu_sehat_obat_kronis "
                        + "(no_sep_apotek,kode_brng,id_medicationrequest,id_medicationdispense,tgl_kirim) values (?,?,?,?,now())",
                        4, new String[]{o.noSepApotek, o.kodeBrng, mrId, mdId});
                ok++;
            } catch (Exception e) {
                System.out.println("Notifikasi ObatKronis kirim : " + e);
                skip++;
            }
        }
        System.out.println("ObatKronis: " + ok + " terkirim, " + skip + " di-skip.");
    }

    // ====================== DATA ======================

    private List<ObatK> ambilObatKronis(String noRawat) {
        List<ObatK> list = new ArrayList<>();
        // Obat kronis apotek BPJS utk rawat ini: bridging_resep_apotek_bpjs.no_sep -> bridging_sep.no_rawat.
        // Hanya KFA valid (obat_code non-BHP) dan yg BELUM terkirim (left join tracking).
        String sql = "select h.no_sep_apotek, d.kode_brng, ifnull(db.nama_brng,'') as nama_obat, "
                + "ifnull(m.obat_code,'') as kfa, ifnull(m.obat_display,'') as kfa_display, "
                + "ifnull(d.jml_obat,'1') as jml, ifnull(d.signa2,'1') as signa, h.tgl_pelayanan, "
                + "ifnull(pg.no_ktp,'') as ktp_dokter, ifnull(pg.nama,h.nmdpjp) as nama_dokter "
                + "from bridging_resep_apotek_bpjs h "
                + "inner join bridging_sep bs on bs.no_sep=h.no_sep "
                + "inner join bridging_resep_apotek_bpjs_nonracikan d on d.no_sep_apotek=h.no_sep_apotek "
                + "inner join satu_sehat_mapping_obat m on m.kode_brng=d.kode_brng and m.obat_code<>'' and m.obat_code not like 'BHP%' "
                + "left join databarang db on db.kode_brng=d.kode_brng "
                + "left join pegawai pg on pg.nik=h.kodedpjp "
                + "left join satu_sehat_obat_kronis ssok on ssok.no_sep_apotek=h.no_sep_apotek and ssok.kode_brng=d.kode_brng "
                + "where bs.no_rawat=? and (ssok.id_medicationdispense is null or ssok.id_medicationdispense='')";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                ObatK o = new ObatK();
                o.noSepApotek = nz(r.getString("no_sep_apotek"));
                o.kodeBrng = nz(r.getString("kode_brng"));
                o.namaObat = nz(r.getString("nama_obat"));
                o.kfa = nz(r.getString("kfa"));
                o.kfaDisplay = nz(r.getString("kfa_display"));
                o.jml = nzDefault(r.getString("jml"), "1");
                o.signa = nzDefault(r.getString("signa"), "1");
                o.tgl = nz(r.getString("tgl_pelayanan"));
                o.ktpDokter = nz(r.getString("ktp_dokter"));
                o.namaDokter = nz(r.getString("nama_dokter"));
                if (!o.kfa.equals("")) list.add(o);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ObatKronis ambil : " + e);
        }
        return list;
    }

    // ====================== JSON ======================

    /** contained Medication (KFA) + extension MedicationType NC + identifier. */
    private void isiContainedMed(com.fasterxml.jackson.databind.node.ObjectNode parent, ObatK o) {
        com.fasterxml.jackson.databind.node.ObjectNode med = parent.putArray("contained").addObject();
        med.put("resourceType", "Medication");
        med.put("id", "med0");
        med.putObject("meta").putArray("profile")
                .add("https://fhir.kemkes.go.id/r4/StructureDefinition/Medication");
        com.fasterxml.jackson.databind.node.ObjectNode ext = med.putArray("extension").addObject();
        ext.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/MedicationType");
        com.fasterxml.jackson.databind.node.ObjectNode mt = ext.putObject("valueCodeableConcept")
                .putArray("coding").addObject();
        mt.put("system", "http://terminology.kemkes.go.id/CodeSystem/medication-type");
        mt.put("code", "NC");
        mt.put("display", "Non-compound");
        com.fasterxml.jackson.databind.node.ObjectNode iden = med.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/medication/" + koneksiDB.IDSATUSEHAT());
        iden.put("use", "official");
        iden.put("value", o.kfa);
        com.fasterxml.jackson.databind.node.ObjectNode cc = med.putObject("code").putArray("coding").addObject();
        cc.put("system", "http://sys-ids.kemkes.go.id/kfa");
        cc.put("code", o.kfa);
        cc.put("display", o.kfaDisplay);
        med.put("status", "active");
    }

    private com.fasterxml.jackson.databind.node.ObjectNode buatMedicationRequest(ObatK o, String idPasien,
            String namaPasien, String idDokter, String idEncounter) {
        String dtIso = formatWaktu(o.tgl);
        com.fasterxml.jackson.databind.node.ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "MedicationRequest");
        com.fasterxml.jackson.databind.node.ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/prescription/" + koneksiDB.IDSATUSEHAT());
        iden.put("use", "official");
        iden.put("value", "MR-" + o.noSepApotek + "-" + o.kodeBrng);
        r.put("status", "active");
        r.put("intent", "order");
        isiContainedMed(r, o);
        com.fasterxml.jackson.databind.node.ObjectNode medRef = r.putObject("medicationReference");
        medRef.put("reference", "#med0");
        medRef.put("display", o.kfaDisplay);
        com.fasterxml.jackson.databind.node.ObjectNode subj = r.putObject("subject");
        subj.put("reference", "Patient/" + idPasien);
        subj.put("display", namaPasien);
        r.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        r.put("authoredOn", dtIso);
        com.fasterxml.jackson.databind.node.ObjectNode req = r.putObject("requester");
        req.put("reference", "Practitioner/" + idDokter);
        req.put("display", o.namaDokter);
        com.fasterxml.jackson.databind.node.ObjectNode cot = r.putObject("courseOfTherapyType")
                .putArray("coding").addObject();
        cot.put("system", "http://terminology.hl7.org/CodeSystem/medicationrequest-course-of-therapy");
        cot.put("code", "continuous");
        cot.put("display", "Continuous long term therapy");
        com.fasterxml.jackson.databind.node.ObjectNode di = r.putArray("dosageInstruction").addObject();
        di.put("sequence", 1);
        di.put("text", o.signa + "x sehari");
        com.fasterxml.jackson.databind.node.ObjectNode rep = di.putObject("timing").putObject("repeat");
        rep.put("frequency", parseInt(o.signa));
        rep.put("period", 1);
        rep.put("periodUnit", "d");
        com.fasterxml.jackson.databind.node.ObjectNode qty = r.putObject("dispenseRequest").putObject("quantity");
        qty.put("value", parseInt(o.jml));
        qty.put("unit", "Tablet");
        qty.put("system", "http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm");
        qty.put("code", "TAB");
        return r;
    }

    private com.fasterxml.jackson.databind.node.ObjectNode buatMedicationDispense(ObatK o, String idPasien,
            String namaPasien, String idDokter, String idEncounter, String mrId) {
        String dtIso = formatWaktu(o.tgl);
        com.fasterxml.jackson.databind.node.ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "MedicationDispense");
        com.fasterxml.jackson.databind.node.ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/prescription/" + koneksiDB.IDSATUSEHAT());
        iden.put("use", "official");
        iden.put("value", "MD-" + o.noSepApotek + "-" + o.kodeBrng);
        r.put("status", "completed");
        isiContainedMed(r, o);
        com.fasterxml.jackson.databind.node.ObjectNode medRef = r.putObject("medicationReference");
        medRef.put("reference", "#med0");
        medRef.put("display", o.kfaDisplay);
        com.fasterxml.jackson.databind.node.ObjectNode subj = r.putObject("subject");
        subj.put("reference", "Patient/" + idPasien);
        subj.put("display", namaPasien);
        r.putObject("context").put("reference", "Encounter/" + idEncounter);
        com.fasterxml.jackson.databind.node.ObjectNode perf = r.putArray("performer").addObject();
        com.fasterxml.jackson.databind.node.ObjectNode actor = perf.putObject("actor");
        actor.put("reference", "Practitioner/" + idDokter);
        actor.put("display", o.namaDokter);
        r.putArray("authorizingPrescription").addObject().put("reference", "MedicationRequest/" + mrId);
        com.fasterxml.jackson.databind.node.ObjectNode qty = r.putObject("quantity");
        qty.put("value", parseInt(o.jml));
        qty.put("unit", "Tablet");
        qty.put("system", "http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm");
        qty.put("code", "TAB");
        r.put("whenHandedOver", dtIso);
        com.fasterxml.jackson.databind.node.ObjectNode di = r.putArray("dosageInstruction").addObject();
        di.put("sequence", 1);
        di.put("text", o.signa + "x sehari");
        com.fasterxml.jackson.databind.node.ObjectNode rep = di.putObject("timing").putObject("repeat");
        rep.put("frequency", parseInt(o.signa));
        rep.put("period", 1);
        rep.put("periodUnit", "d");
        return r;
    }

    // ====================== KIRIM ======================

    private String postKronis(String resourceType, com.fasterxml.jackson.databind.node.ObjectNode body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String payload = mapper.writeValueAsString(body);
            System.out.println("URL ObatKronis : " + link + "/" + resourceType);
            System.out.println("Request JSON ObatKronis : " + payload);
            String resp = api.getRest().exchange(link + "/" + resourceType, HttpMethod.POST,
                    new HttpEntity(payload, headers), String.class).getBody();
            System.out.println("Result JSON ObatKronis : " + resp);
            JsonNode id = mapper.readTree(resp).path("id");
            return (id != null) ? nz(id.asText()) : "";
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.out.println("Error ObatKronis " + resourceType + " : " + ex.getStatusCode()
                    + " => " + ex.getResponseBodyAsString());
            return "";
        } catch (Exception e) {
            System.out.println("Notifikasi ObatKronis post : " + e);
            return "";
        }
    }

    // ====================== UTIL ======================

    private String nz(String s) { return s == null ? "" : s; }
    private String nzDefault(String s, String def) {
        if (s == null || s.trim().equals("")) return def;
        return s;
    }
    private int parseInt(String s) {
        try { return (int) Double.parseDouble(s.trim()); } catch (Exception e) { return 1; }
    }
    /** Format waktu lokal -> ISO offset +07:00 (fallback waktu sekarang bila kosong). */
    private String formatWaktu(String dt) {
        String t = (dt == null) ? "" : dt.trim();
        if (t.equals("") || t.startsWith("0000-00-00")) {
            t = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new java.util.Date());
            return t + "+07:00";
        }
        if (t.contains(".")) t = t.substring(0, t.indexOf('.'));
        if (t.contains(" ")) t = t.replace(" ", "T");
        if (t.length() == 10) t = t + "T00:00:00";
        return t + "+07:00";
    }
}
