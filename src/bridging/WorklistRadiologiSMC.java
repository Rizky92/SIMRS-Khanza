package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

public class WorklistRadiologiSMC {
    private final Connection koneksi = koneksiDB.condb();
    private final ApiOrthanc orthanc = new ApiOrthanc();
    private final sekuel Sequel = new sekuel();
    private final ObjectMapper mapper = new ObjectMapper();
    private String notif = "";

    public String getNotif() {
        return notif;
    }

    public ArrayList<String> daftarModality(String noorder) {
        return Sequel.cariArraySmc(
            "select distinct jns_perawatan_radiologi_modality_smc.modality from permintaan_pemeriksaan_radiologi inner join " +
            "jns_perawatan_radiologi_modality_smc on jns_perawatan_radiologi_modality_smc.kd_jenis_prw = permintaan_pemeriksaan_radiologi.kd_jenis_prw " +
            "where permintaan_pemeriksaan_radiologi.noorder = ? order by jns_perawatan_radiologi_modality_smc.modality", noorder
        );
    }

    public ArrayList<String> daftarBelumDipetakan(String noorder) {
        return Sequel.cariArraySmc(
            "select concat(permintaan_pemeriksaan_radiologi.kd_jenis_prw, ' - ', ifnull(jns_perawatan_radiologi.nm_perawatan, '')) from " +
            "permintaan_pemeriksaan_radiologi inner join jns_perawatan_radiologi on jns_perawatan_radiologi.kd_jenis_prw = " +
            "permintaan_pemeriksaan_radiologi.kd_jenis_prw left join jns_perawatan_radiologi_modality_smc on " +
            "jns_perawatan_radiologi_modality_smc.kd_jenis_prw = permintaan_pemeriksaan_radiologi.kd_jenis_prw where " +
            "permintaan_pemeriksaan_radiologi.noorder = ? and jns_perawatan_radiologi_modality_smc.modality is null " +
            "order by permintaan_pemeriksaan_radiologi.kd_jenis_prw", noorder
        );
    }

    public boolean kirimUlang(String noorder) {
        return kirim(noorder, new LinkedHashMap<>(), true);
    }

    public boolean kirim(String noorder, Map<String, String> aetPerModality) {
        return kirim(noorder, aetPerModality, false);
    }

    public boolean kirim(String noorder, Map<String, String> aetPerModality, boolean ulangi) {
        notif = "";

        if ((null == noorder) || (noorder.isBlank())) {
            notif = "No.Order kosong";
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        boolean sukses = true;
        int diproses = 0;

        try (PreparedStatement ps = koneksi.prepareStatement(
                "select permintaan_radiologi.noorder, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk, " +
                "permintaan_pemeriksaan_radiologi.kd_jenis_prw, jns_perawatan_radiologi.nm_perawatan, permintaan_radiologi.tgl_permintaan, " +
                "ifnull(jns_perawatan_radiologi_modality_smc.modality, '') as modality, " +
                "permintaan_radiologi.jam_permintaan, dokter.nm_dokter, ifnull(poliklinik.nm_poli, '') as nm_poli, permintaan_radiologi.diagnosa_klinis, " +
                "ifnull(satu_sehat_accession_radiologi_smc.no_acsn, '') as no_acsn, ifnull(satu_sehat_accession_radiologi_smc.study_iuid, '') as study_iuid, " +
                "ifnull(satu_sehat_accession_radiologi_smc.worklist_id, '') as worklist_id, " +
                "ifnull(satu_sehat_accession_radiologi_smc.aet_tujuan, '') as aet_tujuan from permintaan_radiologi " +
                "inner join reg_periksa on permintaan_radiologi.no_rawat = reg_periksa.no_rawat " +
                "inner join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "inner join permintaan_pemeriksaan_radiologi on permintaan_radiologi.noorder = permintaan_pemeriksaan_radiologi.noorder " +
                "inner join jns_perawatan_radiologi on jns_perawatan_radiologi.kd_jenis_prw = permintaan_pemeriksaan_radiologi.kd_jenis_prw " +
                "left join jns_perawatan_radiologi_modality_smc on jns_perawatan_radiologi_modality_smc.kd_jenis_prw = permintaan_pemeriksaan_radiologi.kd_jenis_prw " +
                "inner join dokter on permintaan_radiologi.dokter_perujuk = dokter.kd_dokter " +
                "left join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli " +
                "left join satu_sehat_accession_radiologi_smc on satu_sehat_accession_radiologi_smc.noorder = permintaan_pemeriksaan_radiologi.noorder " +
                "and satu_sehat_accession_radiologi_smc.kd_jenis_prw = permintaan_pemeriksaan_radiologi.kd_jenis_prw " +
                "where permintaan_radiologi.noorder = ? order by permintaan_pemeriksaan_radiologi.kd_jenis_prw")) {
            ps.setString(1, noorder);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    diproses++;
                    if (!kirimPemeriksaan(rs, aetPerModality, ulangi)) {
                        sukses = false;
                    }
                }
            }
        } catch (Exception e) {
            notif = "Gagal membaca permintaan radiologi " + noorder + " : " + e;
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        if (0 == diproses) {
            notif = "Tidak ada pemeriksaan pada No.Order " + noorder;
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        return sukses;
    }

    public boolean hapus(String noorder) {
        notif = "";

        if ((null == noorder) || (noorder.isBlank())) {
            notif = "No.Order kosong";
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        ArrayList<String> daftar = Sequel.cariArraySmc(
            "select satu_sehat_accession_radiologi_smc.kd_jenis_prw from satu_sehat_accession_radiologi_smc where " +
            "satu_sehat_accession_radiologi_smc.noorder = ? and ifnull(satu_sehat_accession_radiologi_smc.worklist_id, '') <> ''", noorder
        );

        boolean sukses = true;

        for (String kodeTindakan : daftar) {
            String worklistId = Sequel.cariIsiSmc(
                "select ifnull(satu_sehat_accession_radiologi_smc.worklist_id, '') from satu_sehat_accession_radiologi_smc where " +
                "satu_sehat_accession_radiologi_smc.noorder = ? and satu_sehat_accession_radiologi_smc.kd_jenis_prw = ?", noorder, kodeTindakan
            );

            if (worklistId.isBlank()) {
                continue;
            }

            if (hapusWorklist(worklistId)) {
                Sequel.mengupdatetfSmc("satu_sehat_accession_radiologi_smc", "worklist_id = null, tgl_kirim_worklist = null",
                    "noorder = ? and kd_jenis_prw = ?", noorder, kodeTindakan);
            } else {
                sukses = false;
            }
        }

        return sukses;
    }

    private boolean kirimPemeriksaan(ResultSet rs, Map<String, String> aetPerModality, boolean ulangi) throws Exception {
        String noorder = rs.getString("noorder"), kodeTindakan = rs.getString("kd_jenis_prw"), noACSN = rs.getString("no_acsn"),
               worklistId = rs.getString("worklist_id"), studyIUID = rs.getString("study_iuid"), nmPerawatan = rs.getString("nm_perawatan");

        if (noACSN.isBlank()) {
            notif = "Accession Number belum terbit untuk " + noorder + " / " + kodeTindakan;
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        if ((!worklistId.isBlank()) && (!ulangi)) {
            System.out.println("Worklist " + noACSN + " sudah terkirim, dilewati");
            return true;
        }

        String modality = rs.getString("modality");

        if (modality.isBlank()) {
            notif = "Jenis pemeriksaan " + kodeTindakan + " belum dipetakan ke modality DICOM";
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        String aetitle = (null == aetPerModality) ? "" : aetPerModality.getOrDefault(modality, "");

        if (aetitle.isBlank()) {
            aetitle = rs.getString("aet_tujuan");
        }

        if (aetitle.isBlank()) {
            notif = "Stasiun tujuan untuk modality " + modality + " belum dipilih";
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        if (studyIUID.isBlank()) {
            studyIUID = buatStudyInstanceUID();
            if (!Sequel.mengupdatetfSmc("satu_sehat_accession_radiologi_smc", "study_iuid = ?", "noorder = ? and kd_jenis_prw = ?",
                    studyIUID, noorder, kodeTindakan)) {
                notif = "Gagal menyimpan Study Instance UID untuk " + noACSN;
                System.out.println("Notifikasi : " + notif);
                return false;
            }
        }

        if (!worklistId.isBlank()) {
            hapusWorklist(worklistId);
        }

        ObjectNode langkah = mapper.createObjectNode();
        langkah.put("Modality", modality);
        langkah.put("ScheduledStationAETitle", aetitle);
        langkah.put("ScheduledProcedureStepStartDate", tanggalDICOM(rs.getString("tgl_permintaan")));
        langkah.put("ScheduledProcedureStepStartTime", jamDICOM(rs.getString("jam_permintaan")));
        langkah.put("ScheduledPerformingPhysicianName", namaDICOM(rs.getString("nm_dokter"), 64));
        langkah.put("ScheduledProcedureStepDescription", teksDICOM(nmPerawatan, 64));
        langkah.put("ScheduledProcedureStepID", noACSN);
        langkah.put("ScheduledStationName", teksDICOM(rs.getString("nm_poli"), 16));

        ArrayNode urutan = mapper.createArrayNode();
        urutan.add(langkah);

        ObjectNode tags = mapper.createObjectNode();
        tags.put("SpecificCharacterSet", "ISO_IR 6");
        tags.put("AccessionNumber", noACSN);
        tags.put("PatientID", teksDICOM(rs.getString("no_rkm_medis"), 64));
        tags.put("PatientName", namaDICOM(rs.getString("nm_pasien"), 64));
        tags.put("PatientBirthDate", tanggalDICOM(rs.getString("tgl_lahir")));
        tags.put("PatientSex", jenisKelaminDICOM(rs.getString("jk")));
        tags.put("StudyInstanceUID", studyIUID);
        tags.put("ReferringPhysicianName", namaDICOM(rs.getString("nm_dokter"), 64));
        tags.put("RequestedProcedureID", noACSN);
        tags.put("RequestedProcedureDescription", teksDICOM(nmPerawatan, 64));
        tags.put("ReasonForRequestedProcedure", teksDICOM(rs.getString("diagnosa_klinis"), 64));
        tags.set("ScheduledProcedureStepSequence", urutan);

        ObjectNode permintaan = mapper.createObjectNode();
        permintaan.set("Tags", tags);

        String idBaru = buatWorklist(permintaan, noACSN);

        if (idBaru.isBlank()) {
            return false;
        }

        if (!Sequel.mengupdatetfSmc("satu_sehat_accession_radiologi_smc", "worklist_id = ?, aet_tujuan = ?, tgl_kirim_worklist = now()",
                "noorder = ? and kd_jenis_prw = ?", idBaru, aetitle, noorder, kodeTindakan)) {
            notif = "Worklist " + noACSN + " terkirim tetapi gagal dicatat, ID " + idBaru;
            System.out.println("Notifikasi : " + notif);
            return false;
        }

        System.out.println("Berhasil kirim worklist " + noACSN + " modality " + modality + " ke " + aetitle + ", ID " + idBaru);
        return true;
    }

    private String buatWorklist(ObjectNode permintaan, String noACSN) {
        String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/worklists/create";

        try {
            HttpHeaders header = new HttpHeaders();
            header.add("Authorization", "Basic " + orthanc.Auth());
            header.setContentType(MediaType.APPLICATION_JSON);

            String json = mapper.writeValueAsString(permintaan);
            System.out.println("URL : " + url);
            System.out.println("Request JSON : " + json);

            ResponseEntity<String> response = orthanc.getRest().exchange(url, HttpMethod.POST, new HttpEntity(json, header), String.class);
            HttpStatus status = response.getStatusCode();
            String body = response.getBody();
            System.out.println("Response : " + status.value() + " " + body);

            if (HttpStatus.Series.SUCCESSFUL != status.series()) {
                notif = "Orthanc membalas HTTP " + status.value() + " " + status.getReasonPhrase();
            } else if ((null == body) || (body.isBlank())) {
                notif = "Balasan Orthanc kosong";
            } else {
                JsonNode hasil = mapper.readTree(body);
                String idBaru = hasil.path("ID").asText("");

                if (idBaru.isBlank()) {
                    notif = "Balasan Orthanc tidak memuat ID worklist : " + body;
                } else {
                    return idBaru;
                }
            }
        } catch (HttpStatusCodeException e) {
            if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
                notif = "Endpoint " + url + " tidak ditemukan, pastikan plugin Worklists Orthanc sudah aktif";
            } else if ((HttpStatus.UNAUTHORIZED == e.getStatusCode()) || (HttpStatus.FORBIDDEN == e.getStatusCode())) {
                notif = "Autentikasi ke Orthanc ditolak, periksa user dan password Orthanc";
            } else {
                notif = "Orthanc membalas HTTP " + e.getStatusCode().value() + " " + e.getStatusText() + " : " + e.getResponseBodyAsString();
            }
        } catch (ResourceAccessException e) {
            notif = "Server Orthanc di " + url + " tidak bisa dihubungi : " + e.getMessage();
        } catch (Exception e) {
            notif = "Gagal kirim worklist " + noACSN + " : " + e;
        }

        System.out.println("Notifikasi : " + notif);
        return "";
    }

    private boolean hapusWorklist(String worklistId) {
        String url = koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/worklists/" + worklistId;

        try {
            HttpHeaders header = new HttpHeaders();
            header.add("Authorization", "Basic " + orthanc.Auth());
            header.setContentType(MediaType.APPLICATION_JSON);

            System.out.println("URL : " + url);
            ResponseEntity<String> response = orthanc.getRest().exchange(url, HttpMethod.DELETE, new HttpEntity(header), String.class);
            System.out.println("Response : " + response.getStatusCode().value());

            return HttpStatus.Series.SUCCESSFUL == response.getStatusCode().series();
        } catch (HttpStatusCodeException e) {
            if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
                System.out.println("Worklist " + worklistId + " sudah tidak ada di Orthanc");
                return true;
            }

            notif = "Gagal hapus worklist " + worklistId + " : HTTP " + e.getStatusCode().value() + " " + e.getStatusText();
        } catch (Exception e) {
            notif = "Gagal hapus worklist " + worklistId + " : " + e;
        }

        System.out.println("Notifikasi : " + notif);
        return false;
    }

    private String buatStudyInstanceUID() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer penampung = ByteBuffer.wrap(new byte[16]);
        penampung.putLong(uuid.getMostSignificantBits());
        penampung.putLong(uuid.getLeastSignificantBits());

        return "2.25." + new BigInteger(1, penampung.array()).toString();
    }

    private String teksDICOM(String teks, int panjang) {
        if (null == teks) {
            return "";
        }

        String hasil = Normalizer.normalize(teks, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").replaceAll("[^\\x20-\\x7E]", "").trim();

        if (hasil.length() > panjang) {
            hasil = hasil.substring(0, panjang).trim();
        }

        return hasil;
    }

    private String namaDICOM(String nama, int panjang) {
        if (null == nama) {
            return "";
        }

        return teksDICOM(nama.toUpperCase().replace("\\", " ").replace("^", " "), panjang);
    }

    private String jenisKelaminDICOM(String jk) {
        if ("L".equalsIgnoreCase(jk)) {
            return "M";
        }

        if ("P".equalsIgnoreCase(jk)) {
            return "F";
        }

        return "";
    }

    private String tanggalDICOM(String tanggal) {
        if ((null == tanggal) || (tanggal.isBlank()) || (tanggal.startsWith("0000"))) {
            return "";
        }

        return tanggal.replace("-", "");
    }

    private String jamDICOM(String jam) {
        if ((null == jam) || (jam.isBlank())) {
            return "000000";
        }

        String hasil = jam.replace(":", "");

        if (hasil.length() > 6) {
            hasil = hasil.substring(0, 6);
        }

        while (hasil.length() < 6) {
            hasil = hasil + "0";
        }

        return hasil;
    }
}
