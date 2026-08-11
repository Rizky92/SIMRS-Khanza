/*
  by Ananda Widitomo,S.Kom.
  IT - SIMRS Hj. Fatimah Sulhan
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Sub-sender Rencana Kontrol / tindak lanjut (ServiceRequest, Follow-up visit) untuk flow
 * SatuSehatBundle — dibuat PER KUNJUNGAN (mencakup pasien BPJS maupun UMUM).
 *
 * Resource: ServiceRequest
 *   - category : SNOMED 3457005 (Patient referral)
 *   - code     : SNOMED 185389009 (Follow-up visit)
 *   - code.text / patientInstruction : disposisi pulang, dipetakan dari reg_periksa.stts
 *       (Meninggal -> "Meninggal", Dirujuk -> "Dirujuk", Pulang Paksa -> "Pulang Paksa",
 *        selain itu -> "Diperbolehkan Pulang").
 *   - subject/encounter   : pasien & Encounter kunjungan ini (dari kolom idEncounter).
 *   - requester/performer : Practitioner DPJP kunjungan (reg_periksa.kd_dokter).
 *   - occurrenceDateTime = authoredOn = datetime kunjungan (tgl_registrasi + jam_reg, WIB).
 *   - reasonCode          : diagnosa kunjungan (ICD-10) sesuai status_lanjut (Ralan/Ranap).
 *   - locationReference   : lokasi/unit (satu_sehat_mapping_lokasi_ralan by kd_poli).
 * Idempotent : id deterministik (stableResourceId per no_rawat) -> PUT; id disimpan ke
 *              satu_sehat_servicerequest_kontrol sehingga kiriman ulang tidak menduplikasi.
 */
public class SatuSehatRencanaKontrol {

    private static final String SYS_SNOMED = "http://snomed.info/sct";
    private static final String SYS_ICD10  = "http://hl7.org/fhir/sid/icd-10";
    private static final String OFFSET_WIB = "+07:00";

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    public SatuSehatRencanaKontrol() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi RencanaKontrol : " + e);
        }
    }

    /** Penampung data kunjungan untuk membangun ServiceRequest tindak lanjut. */
    private static class KontrolData {
        String waktuKunjungan = "", disposisi = "", statusLanjut = "";
        String idPasien = "", namaPasien = "", idDokter = "", namaDokter = "", idOrg = "", idLokasi = "";
        List<String[]> diagnosa = new ArrayList<>(); // tiap elemen: {kode, nama}
    }

    /**
     * Kirim ServiceRequest tindak lanjut untuk satu kunjungan. Dipanggil dari SatuSehatBundle
     * setelah Encounter tersimpan. Self-skip bila Encounter/pasien/dokter belum ter-mapping.
     */
    /** PREVIEW: rakit Bundle Rencana Kontrol (ServiceRequest) tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        KontrolData d = ambilData(noRawat);
        if (d == null || d.idPasien.equals("") || d.idDokter.equals("")) return null;
        String id = stableResourceId("ServiceRequest", noRawat);
        ObjectNode sr = buatServiceRequest(noRawat, d, id, idEncounter);
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "ServiceRequest/" + id);
        entry.set("resource", sr);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "ServiceRequest/" + id);
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return; // Encounter belum terkirim -> tidak bisa mereferensi
        }
        KontrolData d = ambilData(noRawat);
        if (d == null) {
            return; // kunjungan tidak ditemukan
        }
        if (d.idPasien.equals("")) {
            System.out.println("Notifikasi RencanaKontrol : ID pasien belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }
        if (d.idDokter.equals("")) {
            System.out.println("Notifikasi RencanaKontrol : ID dokter (Practitioner) belum ada untuk no_rawat " + noRawat + ". Dilewati.");
            return;
        }

        String id = stableResourceId("ServiceRequest", noRawat);
        ObjectNode sr = buatServiceRequest(noRawat, d, id, idEncounter);

        // Bundle transaction berisi satu ServiceRequest, PUT by id (idempotent).
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ObjectNode entry = bundle.putArray("entry").addObject();
        entry.put("fullUrl", "ServiceRequest/" + id);
        entry.set("resource", sr);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", "ServiceRequest/" + id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL RencanaKontrol : " + link);
        System.out.println("Request JSON RencanaKontrol : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil;
        try {
            hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            System.out.println("Error RencanaKontrol Status Code: " + ex.getStatusCode());
            try {
                JsonNode err = mapper.readTree(body);
                System.out.println("Error RencanaKontrol OperationOutcome:\n"
                        + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(err));
            } catch (Exception e2) {
                System.out.println("Error RencanaKontrol Body: " + body);
            }
            throw ex;
        }
        System.out.println("Result JSON RencanaKontrol : " + hasil);
        // Simpan id ke tabel mapping (idempotensi kiriman berikutnya).
        Sequel.queryu2("replace into satu_sehat_servicerequest_kontrol (no_rawat,id_servicerequest) values (?,?)",
                2, new String[]{noRawat, id});
    }

    /** Bangun resource FHIR ServiceRequest (Follow-up visit) untuk satu kunjungan. */
    private ObjectNode buatServiceRequest(String noRawat, KontrolData d, String id, String idEncounter) {
        ObjectNode sr = mapper.createObjectNode();
        sr.put("resourceType", "ServiceRequest");
        sr.put("id", id);

        ObjectNode iden = sr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/servicerequest/" + d.idOrg);
        iden.put("use", "official");
        iden.put("value", normalisasi(noRawat));

        sr.put("status", "active");
        sr.put("intent", "original-order");
        sr.put("priority", "routine");

        // category : Patient referral.
        ObjectNode catCoding = sr.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", SYS_SNOMED);
        catCoding.put("code", "3457005");
        catCoding.put("display", "Patient referral");

        // code : Follow-up visit. text = disposisi pulang.
        ObjectNode code = sr.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        codeCoding.put("system", SYS_SNOMED);
        codeCoding.put("code", "185389009");
        codeCoding.put("display", "Follow-up visit");
        code.put("text", d.disposisi);

        // subject : pasien.
        ObjectNode subject = sr.putObject("subject");
        subject.put("reference", "Patient/" + d.idPasien);
        subject.put("display", d.namaPasien);

        // encounter : kunjungan ini.
        sr.putObject("encounter").put("reference", "Encounter/" + idEncounter);

        // occurrenceDateTime = authoredOn = datetime kunjungan.
        if (!d.waktuKunjungan.equals("")) {
            sr.put("occurrenceDateTime", d.waktuKunjungan);
            sr.put("authoredOn", d.waktuKunjungan);
        }

        // requester : dokter DPJP kunjungan.
        ObjectNode requester = sr.putObject("requester");
        requester.put("reference", "Practitioner/" + d.idDokter);
        requester.put("display", d.namaDokter);

        // performer : dokter DPJP kunjungan.
        ObjectNode performer = sr.putArray("performer").addObject();
        performer.put("reference", "Practitioner/" + d.idDokter);
        performer.put("display", d.namaDokter);

        // reasonCode : diagnosa kunjungan (ICD-10).
        if (!d.diagnosa.isEmpty()) {
            ArrayNode reasonCode = sr.putArray("reasonCode");
            for (String[] dx : d.diagnosa) {
                if (dx[0] == null || dx[0].equals("")) {
                    continue;
                }
                ObjectNode rc = reasonCode.addObject().putArray("coding").addObject();
                rc.put("system", SYS_ICD10);
                rc.put("code", dx[0]);
                rc.put("display", dx[1]);
            }
        }

        // locationReference : lokasi/unit (bila termapping).
        if (!d.idLokasi.equals("")) {
            ObjectNode loc = sr.putArray("locationReference").addObject();
            loc.put("reference", "Location/" + d.idLokasi);
            loc.put("display", "Location");
        }

        // patientInstruction : disposisi pulang untuk pasien.
        sr.put("patientInstruction", d.disposisi);
        return sr;
    }

    /**
     * Pemetaan disposisi pulang dari reg_periksa.stts ke teks instruksi tindak lanjut.
     * enum stts: Belum, Sudah, Batal, Berkas Diterima, Dirujuk, Meninggal, Dirawat, Pulang Paksa, TTV.
     */
    private String disposisiPulang(String stts) {
        if (stts == null) {
            return "Diperbolehkan Pulang";
        }
        switch (stts.trim()) {
            case "Meninggal":    return "Meninggal";
            case "Dirujuk":      return "Dirujuk";
            case "Pulang Paksa": return "Pulang Paksa";
            case "Dirawat":      return "Dirawat";
            default:             return "Diperbolehkan Pulang"; // Sudah, dll.
        }
    }

    /** Ambil data kunjungan + relasi (pasien/dokter IHS, lokasi, diagnosa). null bila tidak ada. */
    private KontrolData ambilData(String noRawat) {
        KontrolData d = null;
        String statusLanjut = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select rp.tgl_registrasi, rp.jam_reg, rp.stts, rp.status_lanjut, "
                    + "p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(dr.nm_dokter,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktp_dokter, "
                    + "ifnull(loc.id_lokasi_satusehat,'') as id_lokasi "
                    + "from reg_periksa rp "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter "
                    + "left join satu_sehat_mapping_lokasi_ralan loc on loc.kd_poli=rp.kd_poli "
                    + "where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                d = new KontrolData();
                d.waktuKunjungan = gabungWaktu(nz(r.getString("tgl_registrasi")), nz(r.getString("jam_reg")));
                d.disposisi = disposisiPulang(r.getString("stts"));
                d.statusLanjut = nz(r.getString("status_lanjut"));
                statusLanjut = d.statusLanjut;
                d.namaPasien = nz(r.getString("nm_pasien"));
                d.namaDokter = nz(r.getString("nm_dokter"));
                d.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                d.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                d.idLokasi = nz(r.getString("id_lokasi"));
                d.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi RencanaKontrol ambilData : " + e);
        }
        if (d != null) {
            d.diagnosa = ambilDiagnosa(noRawat, statusLanjut);
        }
        return d;
    }

    /** Ambil diagnosa kunjungan (sesuai status_lanjut; fallback semua status) untuk reasonCode. */
    private List<String[]> ambilDiagnosa(String noRawat, String statusLanjut) {
        List<String[]> hasil = ambilDiagnosaStatus(noRawat, statusLanjut);
        if (hasil.isEmpty()) {
            hasil = ambilDiagnosaStatus(noRawat, null);
        }
        return hasil;
    }

    private List<String[]> ambilDiagnosaStatus(String noRawat, String status) {
        List<String[]> hasil = new ArrayList<>();
        boolean pakaiStatus = status != null && !status.equals("");
        String sql = "select diagnosa_pasien.kd_penyakit, ifnull(penyakit.nm_penyakit,'') as nm_penyakit "
                + "from diagnosa_pasien inner join penyakit on penyakit.kd_penyakit=diagnosa_pasien.kd_penyakit "
                + "where diagnosa_pasien.no_rawat=? "
                + (pakaiStatus ? "and diagnosa_pasien.status=? " : "")
                + "order by diagnosa_pasien.prioritas asc";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            if (pakaiStatus) {
                p.setString(2, status);
            }
            ResultSet r = p.executeQuery();
            while (r.next()) {
                hasil.add(new String[]{nz(r.getString("kd_penyakit")), nz(r.getString("nm_penyakit"))});
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi RencanaKontrol ambilDiagnosa : " + e);
        }
        return hasil;
    }

    /** Gabung tanggal (YYYY-MM-DD) + jam (HH:MM:SS) -> dateTime FHIR berzona WIB; "" bila tidak valid. */
    private String gabungWaktu(String tgl, String jam) {
        String t = tanggalSaja(tgl);
        if (t.equals("")) {
            return "";
        }
        String j = (jam == null || jam.trim().equals("")) ? "00:00:00" : jam.trim();
        if (j.length() == 5) {
            j = j + ":00"; // HH:MM -> HH:MM:SS
        }
        return t + "T" + j + OFFSET_WIB;
    }

    /** Ambil bagian tanggal (YYYY-MM-DD) saja; "" bila tidak valid. */
    private String tanggalSaja(String dt) {
        if (dt == null) {
            return "";
        }
        String t = dt.trim();
        if (t.length() < 10) {
            return "";
        }
        t = t.substring(0, 10);
        if (t.startsWith("0000-00-00") || !t.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "";
        }
        return t;
    }

    /** id resource deterministik (UUID v3) agar kiriman ulang memakai id yang sama -> PUT idempotent. */
    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(resourceType);
        if (keys != null) {
            for (String key : keys) {
                sb.append("|").append(key == null ? "-" : key.trim());
            }
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    /** Buang karakter non-alfanumerik dari nilai identifier (hindari '/' yang merusak pencarian). */
    private String normalisasi(String data) {
        if (data == null) {
            return "";
        }
        return data.replaceAll("[^A-Za-z0-9]", "");
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
