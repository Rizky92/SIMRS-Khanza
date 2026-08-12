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
 * Sub-sender Alkes (Device chain) untuk flow SatuSehatBundle.
 *
 * Untuk tiap alkes (databarang jenis alkes) yang dipakai pasien (detail_pemberian_obat) dan Device-nya
 * sudah terkirim (satu_sehat_device.id_device), kirim rantai:
 *   DeviceRequest -> DeviceDispense, DeviceUseStatement, SupplyRequest -> SupplyDelivery
 * dalam satu transaction bundle per alkes. Idempotent: id deterministik (stableResourceId) -> PUT.
 * Prasyarat: jalankan dulu menu "Kirim Device (Alkes Katalog)" agar satu_sehat_device terisi.
 */
public class SatuSehatAlkes {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String link = "";

    /**
     * Kode KFA-alkes DUMMY untuk STAGING (katalog KFA-alkes asli belum lengkap di staging).
     * Dipakai sebagai fallback bila alkes belum punya KFA valid (BHP/kosong) -> chain tetap bisa diuji.
     * PRODUKSI: kosongkan ("") agar alkes tanpa KFA asli di-skip (bukan dikirim sebagai dummy).
     */
    private static final String DUMMY_KFA_STG = "33999999";

    public SatuSehatAlkes() {
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi Alkes : " + e);
        }
    }

    private static class Konteks {
        String idPasien="", namaPasien="", idDokter="", namaDokter="", idOrg="";
    }

    private static class AlkesItem {
        String kodeBrng="", namaAlkes="", waktu="";
        String idDeviceExisting="";          // dari satu_sehat_device (bila sudah pernah dikirim)
        String kfaCode="", kfaSystem="", kfaDisplay="";
    }

    /** PREVIEW: rakit SATU Bundle gabungan rantai Device+Supply untuk semua alkes tanpa mengirim; null bila tak ada data. */
    public JsonNode bangun(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) return null;
        Konteks k = ambilKonteks(noRawat);
        if (k == null || k.idPasien.equals("")) return null;
        List<AlkesItem> daftar = ambilAlkes(noRawat);
        if (daftar.isEmpty()) return null;
        String patientRef = "Patient/" + k.idPasien;
        boolean adaDokter = !k.idDokter.equals("");
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        for (AlkesItem a : daftar) {
            boolean buatDeviceBaru = a.idDeviceExisting.equals("");
            String deviceId = buatDeviceBaru ? stableResourceId("Device", a.kodeBrng) : a.idDeviceExisting;
            String deviceRef = "Device/" + deviceId;
            String drId  = stableResourceId("DeviceRequest", noRawat, a.kodeBrng);
            String dusId = stableResourceId("DeviceUseStatement", noRawat, a.kodeBrng);
            String srId  = stableResourceId("SupplyRequest", noRawat, a.kodeBrng);
            String sdId  = stableResourceId("SupplyDelivery", noRawat, a.kodeBrng);
            if (buatDeviceBaru) {
                tambahEntry(entries, "Device", deviceId, buatDevice(a, k, deviceId));
            }
            if (adaDokter) {
                tambahEntry(entries, "DeviceRequest", drId,
                        buatDeviceRequest(noRawat, a, k, drId, idEncounter, patientRef, deviceRef));
            }
            tambahEntry(entries, "DeviceUseStatement", dusId,
                    buatDeviceUseStatement(noRawat, a, k, dusId, patientRef, deviceRef));
            if (adaDokter) {
                tambahEntry(entries, "SupplyRequest", srId,
                        buatSupplyRequest(noRawat, a, k, srId, patientRef, deviceRef));
                tambahEntry(entries, "SupplyDelivery", sdId,
                        buatSupplyDelivery(noRawat, a, k, sdId, srId, patientRef, deviceRef));
            }
        }
        return bundle;
    }

    public void kirim(String noRawat, String idEncounter) throws Exception {
        if (idEncounter == null || idEncounter.equals("")) {
            return;
        }
        Konteks k = ambilKonteks(noRawat);
        if (k == null || k.idPasien.equals("")) {
            return;   // pasien belum ada ID SATUSEHAT
        }
        List<AlkesItem> daftar = ambilAlkes(noRawat);
        if (daftar.isEmpty()) {
            return;   // tidak ada alkes (atau Device belum dikirim)
        }
        if (k.idDokter.equals("")) {
            System.out.println("Notifikasi Alkes : dokter DPJP belum ter-map IHS (no_rawat " + noRawat
                    + ") -> DeviceRequest/SupplyRequest dilewati, hanya DeviceUseStatement.");
        }

        for (AlkesItem a : daftar) {
            try {
                kirimSatuAlkes(noRawat, idEncounter, k, a);
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                System.out.println("Error Alkes (" + a.kodeBrng + ") Status Code: " + ex.getStatusCode());
                System.out.println("Error Alkes Body: " + ex.getResponseBodyAsString());
            } catch (Exception ex) {
                System.out.println("Notifikasi Alkes kirim (" + a.kodeBrng + ") : " + ex);
            }
        }
    }

    /** Bangun & kirim 1 transaction bundle untuk 1 alkes (rantai Device + Supply). */
    private void kirimSatuAlkes(String noRawat, String idEncounter, Konteks k, AlkesItem a) throws Exception {
        String patientRef = "Patient/" + k.idPasien;
        boolean adaDokter = !k.idDokter.equals("");

        // Device: pakai yang sudah terkirim; bila belum, buat sendiri (text-only bila KFA tidak valid/BHP).
        boolean buatDeviceBaru = a.idDeviceExisting.equals("");
        String deviceId = buatDeviceBaru ? stableResourceId("Device", a.kodeBrng) : a.idDeviceExisting;
        String deviceRef = "Device/" + deviceId;

        String drId  = stableResourceId("DeviceRequest", noRawat, a.kodeBrng);
        String dusId = stableResourceId("DeviceUseStatement", noRawat, a.kodeBrng);
        String srId  = stableResourceId("SupplyRequest", noRawat, a.kodeBrng);
        String sdId  = stableResourceId("SupplyDelivery", noRawat, a.kodeBrng);

        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");

        // Device (alkes katalog) — dibuat di bundle bila belum ada.
        if (buatDeviceBaru) {
            tambahEntry(entries, "Device", deviceId, buatDevice(a, k, deviceId));
        }
        // DeviceRequest (butuh Practitioner di requester). DeviceDispense TIDAK dipakai: resource FHIR R5,
        // tidak dikenali SatuSehat (R4) -> "unknown resource type".
        if (adaDokter) {
            tambahEntry(entries, "DeviceRequest", drId,
                    buatDeviceRequest(noRawat, a, k, drId, idEncounter, patientRef, deviceRef));
        }
        // DeviceUseStatement (tidak butuh Practitioner).
        tambahEntry(entries, "DeviceUseStatement", dusId,
                buatDeviceUseStatement(noRawat, a, k, dusId, patientRef, deviceRef));
        // SupplyRequest -> SupplyDelivery (butuh Practitioner di requester).
        if (adaDokter) {
            tambahEntry(entries, "SupplyRequest", srId,
                    buatSupplyRequest(noRawat, a, k, srId, patientRef, deviceRef));
            tambahEntry(entries, "SupplyDelivery", sdId,
                    buatSupplyDelivery(noRawat, a, k, sdId, srId, patientRef, deviceRef));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        System.out.println("URL Alkes : " + link);
        System.out.println("Request JSON Alkes (" + a.kodeBrng + ") : " + payload);
        HttpEntity requestEntity = new HttpEntity(payload, headers);
        String hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
        System.out.println("Result JSON Alkes : " + hasil);

        // Simpan id ke tabel tracking.
        if (buatDeviceBaru) {
            try {
                Sequel.queryu2("replace into satu_sehat_device (kode_brng,id_device,tgl_kirim) values (?,?,now())",
                        2, new String[]{a.kodeBrng, deviceId});
            } catch (Exception e) {
                System.out.println("Notifikasi Alkes simpan device : " + e);
            }
        }
        if (adaDokter) {
            simpan("satu_sehat_devicerequest", "id_devicerequest", noRawat, a.kodeBrng, drId);
            simpan("satu_sehat_supplyrequest", "id_supplyrequest", noRawat, a.kodeBrng, srId);
            simpan("satu_sehat_supplydelivery", "id_supplydelivery", noRawat, a.kodeBrng, sdId);
        }
        simpan("satu_sehat_deviceusestatement", "id_deviceusestatement", noRawat, a.kodeBrng, dusId);
    }

    /** Device (alkes katalog). KFA coding hanya bila valid (bukan kosong/BHP) agar tidak ditolak "Code not found". */
    private ObjectNode buatDevice(AlkesItem a, Konteks k, String id) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "Device");
        r.put("id", id);
        identifier(r, "device", k.idOrg, a.kodeBrng);
        r.put("status", "active");
        ObjectNode type = r.putObject("type");
        if (kfaValid(a.kfaCode)) {
            ObjectNode c = type.putArray("coding").addObject();
            c.put("system", a.kfaSystem.equals("") ? "http://sys-ids.kemkes.go.id/kfa" : a.kfaSystem);
            c.put("code", a.kfaCode);
            c.put("display", a.kfaDisplay.equals("") ? a.namaAlkes : a.kfaDisplay);
        }
        type.put("text", a.namaAlkes);
        ObjectNode dn = r.putArray("deviceName").addObject();
        dn.put("name", a.namaAlkes);
        dn.put("type", "user-friendly-name");
        r.putObject("owner").put("reference", "Organization/" + k.idOrg);
        return r;
    }

    /** KFA valid bila tidak kosong & bukan kode lokal BHP (BHP bukan KFA resmi -> ditolak "Code not found"). */
    private boolean kfaValid(String code) {
        return code != null && !code.trim().equals("") && !code.trim().toUpperCase().startsWith("BHP");
    }

    private void tambahEntry(ArrayNode entries, String resourceType, String id, ObjectNode resource) {
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", resourceType + "/" + id);
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        request.put("method", "PUT");
        request.put("url", resourceType + "/" + id);
    }

    // ====================== BUILDER RESOURCE ======================

    private ObjectNode buatDeviceRequest(String noRawat, AlkesItem a, Konteks k, String id, String idEncounter,
            String patientRef, String deviceRef) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "DeviceRequest");
        r.put("id", id);
        identifier(r, "devicerequest", k.idOrg, "DR-" + noRawat + "-" + a.kodeBrng);
        r.put("status", "active");
        r.put("intent", "order");
        ObjectNode codeRef = r.putObject("codeReference");
        codeRef.put("reference", deviceRef);
        codeRef.put("display", a.namaAlkes);
        subject(r, "subject", patientRef, k.namaPasien);
        r.putObject("encounter").put("reference", "Encounter/" + idEncounter);
        if (!a.waktu.equals("")) r.put("authoredOn", a.waktu);
        ObjectNode req = r.putObject("requester");
        req.put("reference", "Practitioner/" + k.idDokter);
        req.put("display", k.namaDokter);
        // DeviceDispense disisipkan sebagai extension SATUSEHAT R4 (bukan resource terpisah) ->
        // tampil di "Penggunaan Alkes".
        ObjectNode ext = r.putArray("extension").addObject();
        ext.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/DeviceDispense");
        ArrayNode sub = ext.putArray("extension");
        ObjectNode eIden = sub.addObject();
        eIden.put("url", "identifier");
        ObjectNode vi = eIden.putObject("valueIdentifier");
        vi.put("system", "http://sys-ids.kemkes.go.id/devicedispense/" + k.idOrg);
        vi.put("use", "official");
        vi.put("value", "DD-" + noRawat + "-" + a.kodeBrng);
        ObjectNode eStat = sub.addObject();
        eStat.put("url", "status");
        eStat.put("valueCode", "completed");
        ObjectNode eDev = sub.addObject();
        eDev.put("url", "device");
        ObjectNode vrDev = eDev.putObject("valueReference");
        vrDev.put("reference", deviceRef);
        vrDev.put("display", a.namaAlkes);
        ObjectNode eBased = sub.addObject();
        eBased.put("url", "basedOn");
        eBased.putObject("valueReference").put("reference", "DeviceRequest/" + id);
        ObjectNode eSubj = sub.addObject();
        eSubj.put("url", "subject");
        ObjectNode vrSubj = eSubj.putObject("valueReference");
        vrSubj.put("reference", patientRef);
        vrSubj.put("display", k.namaPasien);
        ObjectNode eRecv = sub.addObject();
        eRecv.put("url", "receiver");
        ObjectNode vrRecv = eRecv.putObject("valueReference");
        vrRecv.put("reference", patientRef);
        vrRecv.put("display", k.namaPasien);
        return r;
    }

    private ObjectNode buatDeviceUseStatement(String noRawat, AlkesItem a, Konteks k, String id,
            String patientRef, String deviceRef) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "DeviceUseStatement");
        r.put("id", id);
        identifier(r, "deviceusestatement", k.idOrg, "DUS-" + noRawat + "-" + a.kodeBrng);
        r.put("status", "active");
        subject(r, "subject", patientRef, k.namaPasien);
        ObjectNode dev = r.putObject("device");
        dev.put("reference", deviceRef);
        dev.put("display", a.namaAlkes);
        if (!a.waktu.equals("")) {
            r.put("timingDateTime", a.waktu);
            r.put("recordedOn", a.waktu);
        }
        return r;
    }

    private ObjectNode buatSupplyRequest(String noRawat, AlkesItem a, Konteks k, String id,
            String patientRef, String deviceRef) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "SupplyRequest");
        r.put("id", id);
        identifier(r, "supplyrequest", k.idOrg, "SR-" + noRawat + "-" + a.kodeBrng);
        r.put("status", "active");
        ObjectNode cat = r.putObject("category").putArray("coding").addObject();
        cat.put("system", "http://terminology.hl7.org/CodeSystem/supply-kind");
        cat.put("code", "central");
        ObjectNode item = r.putObject("itemReference");
        item.put("reference", deviceRef);
        item.put("display", a.namaAlkes);
        r.putObject("quantity").put("value", 1);
        if (!a.waktu.equals("")) r.put("occurrenceDateTime", a.waktu);
        ObjectNode req = r.putObject("requester");
        req.put("reference", "Practitioner/" + k.idDokter);
        req.put("display", k.namaDokter);
        return r;
    }

    private ObjectNode buatSupplyDelivery(String noRawat, AlkesItem a, Konteks k, String id, String srId,
            String patientRef, String deviceRef) {
        ObjectNode r = mapper.createObjectNode();
        r.put("resourceType", "SupplyDelivery");
        r.put("id", id);
        identifier(r, "supplydelivery", k.idOrg, "SD-" + noRawat + "-" + a.kodeBrng);
        r.putArray("basedOn").addObject().put("reference", "SupplyRequest/" + srId);
        r.put("status", "completed");
        subject(r, "patient", patientRef, k.namaPasien);
        ObjectNode typeCoding = r.putObject("type").putArray("coding").addObject();
        typeCoding.put("system", "http://terminology.hl7.org/CodeSystem/supply-item-type");
        typeCoding.put("code", "device");
        ObjectNode supplied = r.putObject("suppliedItem");
        supplied.putObject("quantity").put("value", 1);
        ObjectNode item = supplied.putObject("itemReference");
        item.put("reference", deviceRef);
        item.put("display", a.namaAlkes);
        if (!a.waktu.equals("")) r.put("occurrenceDateTime", a.waktu);
        return r;
    }

    private void identifier(ObjectNode r, String tipe, String idOrg, String value) {
        ObjectNode iden = r.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/" + tipe + "/" + idOrg);
        iden.put("use", "official");
        iden.put("value", value);
    }

    private void subject(ObjectNode r, String field, String ref, String display) {
        ObjectNode s = r.putObject(field);
        s.put("reference", ref);
        s.put("display", display);
    }

    // ====================== DATA ======================

    private Konteks ambilKonteks(String noRawat) {
        Konteks k = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select p.no_ktp as ktp_pasien, p.nm_pasien, "
                    + "ifnull(pg.no_ktp,'') as ktp_dokter, ifnull(pg.nama,'') as nm_dokter "
                    + "from reg_periksa rp "
                    + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                    + "left join pegawai pg on pg.nik=rp.kd_dokter "
                    + "where rp.no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                k = new Konteks();
                k.namaPasien = nz(r.getString("nm_pasien"));
                k.namaDokter = nz(r.getString("nm_dokter"));
                k.idDokter = nz(cek.tampilIDParktisi(nz(r.getString("ktp_dokter"))));
                k.idPasien = nz(cek.tampilIDPasien(nz(r.getString("ktp_pasien"))));
                k.idOrg = koneksiDB.IDSATUSEHAT();
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Alkes ambilKonteks : " + e);
        }
        return k;
    }

    private List<AlkesItem> ambilAlkes(String noRawat) {
        List<AlkesItem> list = new ArrayList<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select dpo.kode_brng, ifnull(db.nama_brng,'') as nama_alkes, "
                    + "min(dpo.tgl_perawatan) as tgl, ifnull(ssd.id_device,'') as id_device, "
                    + "ifnull(ssm.obat_code,'') as kfa_code, ifnull(ssm.obat_system,'') as kfa_system, "
                    + "ifnull(ssm.obat_display,'') as kfa_display "
                    + "from detail_pemberian_obat dpo "
                    + "inner join databarang db on db.kode_brng=dpo.kode_brng "
                    + "inner join jenis j on j.kdjns=db.kdjns and (j.nama like '%alkes%' or j.nama like '%alat kesehatan%') "
                    + "left join satu_sehat_device ssd on ssd.kode_brng=dpo.kode_brng and ssd.id_device<>'' "
                    + "left join satu_sehat_mapping_obat ssm on ssm.kode_brng=dpo.kode_brng "
                    + "where dpo.no_rawat=? group by dpo.kode_brng, db.nama_brng");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                AlkesItem a = new AlkesItem();
                a.kodeBrng = nz(r.getString("kode_brng"));
                a.namaAlkes = nz(r.getString("nama_alkes"));
                a.idDeviceExisting = nz(r.getString("id_device"));
                a.kfaCode = nz(r.getString("kfa_code"));
                a.kfaSystem = nz(r.getString("kfa_system"));
                a.kfaDisplay = nz(r.getString("kfa_display"));
                a.waktu = formatWaktu(nz(r.getString("tgl")));
                if (a.kodeBrng.equals("")) continue;
                // Device.type wajib coding KFA valid. BHP & kosong ditolak server ("Code not found").
                if (a.idDeviceExisting.equals("") && !kfaValid(a.kfaCode)) {
                    if (DUMMY_KFA_STG.equals("")) {
                        System.out.println("Notifikasi Alkes : " + a.kodeBrng + " (" + a.namaAlkes
                                + ") KFA tidak valid (BHP/kosong) -> dilewati. Perlu kode KFA-alkes resmi.");
                        continue;
                    }
                    // Fallback dummy KFA (staging) agar chain tetap terkirim.
                    a.kfaCode = DUMMY_KFA_STG;
                    a.kfaSystem = "http://sys-ids.kemkes.go.id/kfa";
                    if (a.kfaDisplay.equals("")) a.kfaDisplay = a.namaAlkes;
                }
                list.add(a);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Alkes ambilAlkes : " + e);
        }
        return list;
    }

    private void simpan(String tabel, String kolomId, String noRawat, String kodeBrng, String id) {
        try {
            Sequel.queryu2("replace into " + tabel + " (no_rawat,kode_brng," + kolomId + ",tgl_kirim) values (?,?,?,now())",
                    3, new String[]{noRawat, kodeBrng, id});
        } catch (Exception e) {
            System.out.println("Notifikasi Alkes simpan (" + tabel + ") : " + e);
        }
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

    private String stableResourceId(String resourceType, String... keys) {
        StringBuilder sb = new StringBuilder(resourceType);
        if (keys != null) {
            for (String key : keys) sb.append("|").append(key == null ? "-" : key.trim());
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}
