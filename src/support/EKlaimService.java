package support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class EKlaimService {

    public static class CovidData {
        public int pemulasaraanJenazah = 0;
        public int kantongJenazah = 0;
        public int petiJenazah = 0;
        public int plastikErat = 0;
        public int desinfektanJenazah = 0;
        public int mobilJenazah = 0;
        public int desinfektanMobilJenazah = 0;
        public int covid19StatusCd = 1;
        public int covid19CcInd = 0;
        public String nomorKartuT = "";
        public int episodes1 = 0;
        public int episodes2 = 0;
        public int episodes3 = 0;
        public int episodes4 = 0;
        public int episodes5 = 0;
        public int episodes6 = 0;

        public String buildEpisodesString() {
            StringBuilder sb = new StringBuilder();
            if (episodes1 != 0) sb.append("1;").append(episodes1).append("#");
            if (episodes2 != 0) sb.append("2;").append(episodes2).append("#");
            if (episodes3 != 0) sb.append("3;").append(episodes3).append("#");
            if (episodes4 != 0) sb.append("4;").append(episodes4).append("#");
            if (episodes5 != 0) sb.append("5;").append(episodes5).append("#");
            if (episodes6 != 0) sb.append("6;").append(episodes6).append("#");
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); // remove trailing #
            }
            return sb.toString();
        }
    }

    public static class EKlaimResult {
        public final boolean success;
        public final String data;
        public final String error;

        public EKlaimResult(boolean success, String data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
    }

    private final EKlaimApiClient apiClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final sekuel Sequel;
    private final Connection conn;

    public EKlaimService(String wsUrl, String hexKey) {
        this.apiClient = new EKlaimApiClient(wsUrl, hexKey);
        this.Sequel = new sekuel();
        this.conn = koneksiDB.condb();
    }

    public EKlaimResult buatKlaimBaru(String nomorKartu, String nomorSep, String nomorRm, String namaPasien, String tglLahir, String gender, String noRawat) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "new_claim");
            ObjectNode data = request.putObject("data");
            data.put("nomor_kartu", nomorKartu);
            data.put("nomor_sep", nomorSep);
            data.put("nomor_rm", nomorRm);
            data.put("nama_pasien", namaPasien);
            data.put("tgl_lahir", tglLahir);
            data.put("gender", gender);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                String error = formatError(msg, "new_claim");
                return getDataKlaim(nomorSep, noRawat, nomorKartu, nomorRm, namaPasien, tglLahir, gender, error);
            }

            boolean saved = Sequel.menyimpantfSmc("inacbg_klaim_baru2", null,
                    noRawat, nomorSep,
                    msg.path("response").path("patient_id").asText(),
                    msg.path("response").path("admission_id").asText(),
                    msg.path("response").path("hospital_admission_id").asText());
            System.out.println("[EKlaim] buatKlaimBaru DB save inacbg_klaim_baru2: " + saved);

            return new EKlaimResult(true, "Klaim berhasil disimpan", null);
        } catch (Exception e) {
            System.out.println("[EKlaim] buatKlaimBaru exception: " + e.getMessage());
            e.printStackTrace();
            return new EKlaimResult(false, null, "Error buatKlaimBaru: " + e.getMessage());
        }
    }

    public EKlaimResult getDataKlaim(String nomorSep, String noRawat, String nomorKartu, String nomorRm, String namaPasien, String tglLahir, String gender, String errorKlaimBaru) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "get_claim_data");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.get(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                String error = formatError(msg, "get_claim_data");
                String errorNo = msg.path("metadata").path("error_no").asText();

                if ("E2004".equals(errorNo) && errorKlaimBaru != null && errorKlaimBaru.contains("E2043")) {
                    error = "Tidak dapat mengambil status data klaim, kemungkinan no. SEP dihapus dari eklaim! Lakukan pembuatan klaim baru dari menggunakan no. SEP di eklaim!\n" + errorKlaimBaru + "\n" + error;
                } else {
                    error = errorKlaimBaru + "\n" + error;
                }

                return new EKlaimResult(false, null, error);
            }

            Sequel.menghapusSmc("inacbg_klaim_baru2", "no_rawat = ?", noRawat);
            System.out.println("[EKlaim] getDataKlaim DB delete inacbg_klaim_baru2 done");
            boolean saved = Sequel.menyimpantfSmc("inacbg_klaim_baru2", null,
                    noRawat, nomorSep,
                    msg.path("response").path("data").path("patient_id").asText(),
                    msg.path("response").path("data").path("admission_id").asText(),
                    msg.path("response").path("data").path("hospital_admission_id").asText());
            System.out.println("[EKlaim] getDataKlaim DB save inacbg_klaim_baru2: " + saved);

            updateDataPasien(nomorKartu, nomorRm, namaPasien, tglLahir, gender);

            String klaimStatus = msg.path("response").path("data").path("klaim_status_cd").asText();
            if ("final".equals(klaimStatus)) {
                EKlaimResult reeditResult = reeditKlaim(nomorSep, noRawat);
                if (reeditResult.success) {
                    reeditIdrg(nomorSep);
                }
            }

            return new EKlaimResult(true, "Klaim berhasil disimpan", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error getDataKlaim: " + e.getMessage());
        }
    }

    public EKlaimResult updateDataPasien(String nomorKartu, String nomorRm, String namaPasien, String tglLahir, String gender) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "update_patient");
            metadata.put("nomor_rm", nomorRm);
            ObjectNode data = request.putObject("data");
            data.put("nomor_kartu", nomorKartu);
            data.put("nomor_rm", nomorRm);
            data.put("nama_pasien", namaPasien);
            data.put("tgl_lahir", tglLahir);
            data.put("gender", gender);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "update_patient"));
            }

            return new EKlaimResult(true, "Data pasien berhasil diupdate", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error updateDataPasien: " + e.getMessage());
        }
    }

    public EKlaimResult reeditKlaim(String nomorSep, String noRawat) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "reedit_claim");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                String error = formatError(msg, "reedit_claim");

                Sequel.menghapusSmc("inacbg_klaim_baru2", "no_rawat = ?", noRawat);
                Sequel.menghapusSmc("inacbg_data_terkirim2", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("idrg_grouping_smc", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("idrg_klaim_final_smc", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("inacbg_diagnosa_pasien_smc", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("inacbg_prosedur_pasien_smc", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("inacbg_grouping_stage12", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("inacbg_grouping_stage2_smc", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("inacbg_klaim_final_smc", "no_sep = ?", nomorSep);
                Sequel.menghapusSmc("inacbg_cetak_klaim", "no_sep = ?", nomorSep);

                return new EKlaimResult(false, "grouper", error);
            }

            Sequel.menghapusSmc("inacbg_cetak_klaim", "no_sep = ?", nomorSep);

            return new EKlaimResult(true, "Klaim berhasil diedit!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error reeditKlaim: " + e.getMessage());
        }
    }

    public EKlaimResult reeditIdrg(String nomorSep) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "idrg_grouper_reedit");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                System.out.println("Notif reeditIdrg: " + formatError(msg, "idrg_grouper_reedit"));
            }

            Sequel.menghapusSmc("idrg_grouping_smc", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("idrg_klaim_final_smc", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_diagnosa_pasien_smc", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_prosedur_pasien_smc", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_grouping_stage12", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_grouping_stage2_smc", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_klaim_final_smc", "no_sep = ?", nomorSep);

            return new EKlaimResult(true, "Klaim IDRG berhasil diedit", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error reeditIdrg: " + e.getMessage());
        }
    }

    public EKlaimResult reeditInacbg(String nomorSep) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "inacbg_grouper_reedit");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "inacbg_grouper_reedit"));
            }

            Sequel.menghapusSmc("inacbg_grouping_stage2_smc", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_grouping_stage12", "no_sep = ?", nomorSep);
            Sequel.menghapusSmc("inacbg_klaim_final_smc", "no_sep = ?", nomorSep);

            return new EKlaimResult(true, "Klaim INACBG berhasil diedit", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error reeditInacbg: " + e.getMessage());
        }
    }

    public EKlaimResult updateDataKlaim(String nomorSep, Map<String, String> form, CovidData covidData) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "set_claim_data");
            metadata.put("nomor_sep", nomorSep);

            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);
            data.put("nomor_kartu", form.getOrDefault("nomorKartu", ""));
            data.put("tgl_masuk", form.getOrDefault("tglMasuk", "") + " 00:00:01");
            data.put("tgl_pulang", form.getOrDefault("tglPulang", "") + " 23:59:59");
            data.put("cara_masuk", form.getOrDefault("caraMasuk", ""));
            data.put("jenis_rawat", form.getOrDefault("jenisRawat", ""));
            data.put("kelas_rawat", form.getOrDefault("kelasRawat", ""));
            data.put("adl_sub_acute", form.getOrDefault("adlSubAcute", "0"));
            data.put("adl_chronic", form.getOrDefault("adlChronic", "0"));
            data.put("icu_indikator", form.getOrDefault("icuIndikator", "0"));
            data.put("icu_los", form.getOrDefault("icuLos", "0"));
            data.put("ventilator_hour", form.getOrDefault("ventilatorHour", "0"));
            data.put("upgrade_class_ind", form.getOrDefault("upgradeClassInd", "0"));
            data.put("upgrade_class_class", form.getOrDefault("upgradeClassClass", ""));
            data.put("upgrade_class_los", form.getOrDefault("upgradeClassLos", "0"));
            data.put("add_payment_pct", form.getOrDefault("addPaymentPct", "0"));
            data.put("birth_weight", form.getOrDefault("birthWeight", "0"));
            data.put("sistole", form.getOrDefault("sistole", "120"));
            data.put("diastole", form.getOrDefault("diastole", "90"));
            data.put("discharge_status", form.getOrDefault("dischargeStatus", "1"));
            data.put("dializer_single_use", form.getOrDefault("dializerSingleUse", "0"));

            ObjectNode tarifRs = data.putObject("tarif_rs");
            tarifRs.put("prosedur_non_bedah", form.getOrDefault("prosedurNonBedah", "0"));
            tarifRs.put("prosedur_bedah", form.getOrDefault("prosedurBedah", "0"));
            tarifRs.put("konsultasi", form.getOrDefault("konsultasi", "0"));
            tarifRs.put("tenaga_ahli", form.getOrDefault("tenagaAhli", "0"));
            tarifRs.put("keperawatan", form.getOrDefault("keperawatan", "0"));
            tarifRs.put("penunjang", form.getOrDefault("penunjang", "0"));
            tarifRs.put("radiologi", form.getOrDefault("radiologi", "0"));
            tarifRs.put("laboratorium", form.getOrDefault("laboratorium", "0"));
            tarifRs.put("pelayanan_darah", form.getOrDefault("pelayananDarah", "0"));
            tarifRs.put("rehabilitasi", form.getOrDefault("rehabilitasi", "0"));
            tarifRs.put("kamar", form.getOrDefault("kamar", "0"));
            tarifRs.put("rawat_intensif", form.getOrDefault("rawatIntensif", "0"));
            tarifRs.put("obat", form.getOrDefault("obat", "0"));
            tarifRs.put("obat_kronis", form.getOrDefault("obatKronis", "0"));
            tarifRs.put("obat_kemoterapi", form.getOrDefault("obatKemoterapi", "0"));
            tarifRs.put("alkes", form.getOrDefault("alkes", "0"));
            tarifRs.put("bmhp", form.getOrDefault("bmhp", "0"));
            tarifRs.put("sewa_alat", form.getOrDefault("sewaAlat", "0"));

            data.put("tarif_poli_eks", form.getOrDefault("tarifPoliEks", "0"));
            data.put("nama_dokter", form.getOrDefault("namaDokter", ""));
            data.put("kode_tarif", form.getOrDefault("kodeTarif", ""));
            data.put("payor_id", form.getOrDefault("payorId", "3"));
            data.put("payor_cd", form.getOrDefault("payorCd", "JKN"));
            data.put("cob_cd", form.getOrDefault("cobCd", "#"));
            data.put("coder_nik", form.getOrDefault("coderNik", ""));
            System.out.println("[EKlaim] updateDataKlaim coder_nik='" + form.getOrDefault("coderNik", "") + "'");

            // Ventilator
            String ventilatorUseInd = form.getOrDefault("ventilatorUseInd", "0");
            if ("1".equals(ventilatorUseInd)) {
                ObjectNode ventilator = data.putObject("ventilator");
                ventilator.put("use_ind", ventilatorUseInd);
                ventilator.put("start_dttm", form.getOrDefault("ventilatorStartDttm", ""));
                ventilator.put("stop_dttm", form.getOrDefault("ventilatorStopDttm", ""));
            }

            // Upgrade class payor
            String upgradeClassPayor = form.getOrDefault("upgradeClassPayor", "");
            if (!upgradeClassPayor.isEmpty() && "1".equals(form.getOrDefault("upgradeClassInd", "0"))) {
                data.put("upgrade_class_payor", upgradeClassPayor);
            }

            // Bayi lahir status
            String bayiLahirStatusCd = form.getOrDefault("bayiLahirStatusCd", "");
            if (!bayiLahirStatusCd.isEmpty()) {
                data.put("bayi_lahir_status_cd", bayiLahirStatusCd);
            }

            // Kantong darah
            String kantongDarah = form.getOrDefault("kantongDarah", "0");
            if (!"0".equals(kantongDarah) && !kantongDarah.isEmpty()) {
                data.put("kantong_darah", kantongDarah);
            }

            // Alteplase
            String alteplaseInd = form.getOrDefault("alteplaseInd", "0");
            if ("1".equals(alteplaseInd)) {
                data.put("alteplase_ind", alteplaseInd);
            }

            // APGAR
            boolean hasApgar = false;
            String[] apgarFields = {"apgarMenit1Appearance", "apgarMenit1Pulse", "apgarMenit1Grimace", "apgarMenit1Activity", "apgarMenit1Respiration",
                    "apgarMenit5Appearance", "apgarMenit5Pulse", "apgarMenit5Grimace", "apgarMenit5Activity", "apgarMenit5Respiration"};
            for (String f : apgarFields) {
                String val = form.getOrDefault(f, "0");
                if (!"0".equals(val) && !val.isEmpty()) { hasApgar = true; break; }
            }
            if (hasApgar) {
                ObjectNode apgar = data.putObject("apgar");
                ObjectNode menit1 = apgar.putObject("menit_1");
                menit1.put("appearance", form.getOrDefault("apgarMenit1Appearance", "0"));
                menit1.put("pulse", form.getOrDefault("apgarMenit1Pulse", "0"));
                menit1.put("grimace", form.getOrDefault("apgarMenit1Grimace", "0"));
                menit1.put("activity", form.getOrDefault("apgarMenit1Activity", "0"));
                menit1.put("respiration", form.getOrDefault("apgarMenit1Respiration", "0"));
                ObjectNode menit5 = apgar.putObject("menit_5");
                menit5.put("appearance", form.getOrDefault("apgarMenit5Appearance", "0"));
                menit5.put("pulse", form.getOrDefault("apgarMenit5Pulse", "0"));
                menit5.put("grimace", form.getOrDefault("apgarMenit5Grimace", "0"));
                menit5.put("activity", form.getOrDefault("apgarMenit5Activity", "0"));
                menit5.put("respiration", form.getOrDefault("apgarMenit5Respiration", "0"));
            }

            // Persalinan
            String usiaKehamilan = form.getOrDefault("usiaKehamilan", "");
            if (!usiaKehamilan.isEmpty()) {
                ObjectNode persalinan = data.putObject("persalinan");
                persalinan.put("usia_kehamilan", usiaKehamilan);
                persalinan.put("gravida", form.getOrDefault("gravida", "0"));
                persalinan.put("partus", form.getOrDefault("partus", "0"));
                persalinan.put("abortus", form.getOrDefault("abortus", "0"));
                persalinan.put("onset_kontraksi", form.getOrDefault("onsetKontraksi", ""));

                String deliveryJson = form.getOrDefault("deliveryList", "");
                if (!deliveryJson.isEmpty()) {
                    try {
                        JsonNode deliveryArr = mapper.readTree(deliveryJson);
                        if (deliveryArr.isArray() && deliveryArr.size() > 0) {
                            ArrayNode deliveryNode = persalinan.putArray("delivery");
                            for (JsonNode d : deliveryArr) {
                                ObjectNode dObj = deliveryNode.addObject();
                                dObj.put("delivery_sequence", d.path("deliverySequence").asText());
                                dObj.put("delivery_method", d.path("deliveryMethod").asText());
                                dObj.put("delivery_dttm", d.path("deliveryDttm").asText());
                                dObj.put("letak_janin", d.path("letakJanin").asText());
                                dObj.put("kondisi", d.path("kondisi").asText());
                                dObj.put("use_manual", d.path("useManual").asText());
                                dObj.put("use_forcep", d.path("useForcep").asText());
                                dObj.put("use_vacuum", d.path("useVacuum").asText());
                                dObj.put("shk_spesimen_ambil", d.path("shkSpesimenAmbil").asText());
                                dObj.put("shk_lokasi", d.path("shkLokasi").asText());
                                dObj.put("shk_alasan", d.path("shkAlasan").asText());
                                dObj.put("shk_spesimen_dttm", d.path("shkSpesimenDttm").asText());
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("[EKlaim] Error parsing delivery JSON: " + ex.getMessage());
                    }
                }
            }

            // COVID
            if (covidData != null) {
                data.put("pemulasaraan_jenazah", String.valueOf(covidData.pemulasaraanJenazah));
                data.put("kantong_jenazah", String.valueOf(covidData.kantongJenazah));
                data.put("peti_jenazah", String.valueOf(covidData.petiJenazah));
                data.put("plastik_erat", String.valueOf(covidData.plastikErat));
                data.put("desinfektan_jenazah", String.valueOf(covidData.desinfektanJenazah));
                data.put("mobil_jenazah", String.valueOf(covidData.mobilJenazah));
                data.put("desinfektan_mobil_jenazah", String.valueOf(covidData.desinfektanMobilJenazah));
                data.put("covid19_status_cd", String.valueOf(covidData.covid19StatusCd));
                data.put("nomor_kartu_t", covidData.nomorKartuT);
                data.put("episodes", covidData.buildEpisodesString());
                data.put("covid19_cc_ind", String.valueOf(covidData.covid19CcInd));
            }

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "set_claim_data"));
            }

            Sequel.menghapusSmc("inacbg_data_terkirim2", "no_sep = ?", nomorSep);
            System.out.println("[EKlaim] updateDataKlaim DB delete inacbg_data_terkirim2 done");
            String coderNik = form.getOrDefault("coderNik", "");
            boolean saved = Sequel.menyimpantfSmc("inacbg_data_terkirim2", null, nomorSep, coderNik);
            System.out.println("[EKlaim] updateDataKlaim DB save inacbg_data_terkirim2: " + saved);

            return new EKlaimResult(true, "Data klaim berhasil disimpan!", null);
        } catch (Exception e) {
            System.out.println("[EKlaim] updateDataKlaim exception: " + e.getMessage());
            e.printStackTrace();
            return new EKlaimResult(false, null, "Error updateDataKlaim: " + e.getMessage());
        }
    }

    public EKlaimResult validasiSITB(String nomorSep, String nomorRm, String nomorSitb) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "sitb_validate");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);
            data.put("nomor_register_sitb", nomorSitb);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "sitb_validate"));
            }

            String statusDetail = msg.path("response").path("status").asText() + " - " + msg.path("response").path("detail").asText();

            Sequel.executeRawSmc("insert into inacbg_pasien_tb_smc values (?, ?, ?, ?) on duplicate key update no_rkm_medis = values(no_rkm_medis), no_sitb = values(no_sitb), status_validasi = values(status_validasi)",
                    nomorSep, nomorRm, nomorSitb, statusDetail);

            return new EKlaimResult(true, statusDetail, null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error validasiSITB: " + e.getMessage());
        }
    }

    public EKlaimResult setDiagnosaIdrg(String nomorSep, String diagnosa) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "idrg_diagnosa_set");
            metadata.put("nomor_sep", nomorSep);
            ObjectNode data = request.putObject("data");
            data.put("diagnosa", diagnosa);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "idrg_diagnosa_set"));
            }

            return new EKlaimResult(true, "Diagnosa IDRG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error setDiagnosaIdrg: " + e.getMessage());
        }
    }

    public EKlaimResult setProsedurIdrg(String nomorSep, String prosedur) {
        try {
            if (prosedur == null || prosedur.isEmpty()) {
                prosedur = "#";
            }

            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "idrg_procedure_set");
            metadata.put("nomor_sep", nomorSep);
            ObjectNode data = request.putObject("data");
            data.put("procedure", prosedur);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "idrg_procedure_set"));
            }

            return new EKlaimResult(true, "Prosedur IDRG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error setProsedurIdrg: " + e.getMessage());
        }
    }

    public EKlaimResult groupingStage1Idrg(String nomorSep, String noRawat, String statusRawat, String coderNik) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "grouper");
            metadata.put("stage", "1");
            metadata.put("grouper", "idrg");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "grouper idrg"));
            }

            JsonNode responseIdrg = msg.path("response_idrg");
            String mdcNumber = responseIdrg.path("mdc_number").asText();
            String mdcDescription = responseIdrg.path("mdc_description").asText();
            String drgCode = responseIdrg.path("drg_code").asText();
            String drgDescription = responseIdrg.path("drg_description").asText();

            System.out.println("[EKlaim] groupingStage1Idrg response: mdc=" + mdcNumber + ", drg=" + drgCode + ", desc=" + drgDescription);
            boolean saved = Sequel.executeRawSmc(
                    "insert into idrg_grouping_smc values (?, ?, ?, ?, ?) on duplicate key update mdc_number = values(mdc_number), mdc_description = values(mdc_description), drg_code = values(drg_code), drg_description = values(drg_description)",
                    nomorSep, mdcNumber, mdcDescription, drgCode, drgDescription);
            System.out.println("[EKlaim] groupingStage1Idrg DB save idrg_grouping_smc: " + saved);

            if ("36".equals(mdcNumber)) {
                return new EKlaimResult(false, null, drgDescription);
            }

            return new EKlaimResult(true, "Grouping IDRG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error groupingStage1Idrg: " + e.getMessage());
        }
    }

    public EKlaimResult finalIdrg(String nomorSep, String coderNik) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "idrg_grouper_final");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "idrg_grouper_final"));
            }

            Sequel.menyimpantfSmc("idrg_klaim_final_smc", null, nomorSep, coderNik);

            return new EKlaimResult(true, "Grouping IDRG berhasil final!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error finalIdrg: " + e.getMessage());
        }
    }

    public EKlaimResult importIdrgToInacbg(String nomorSep) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "idrg_to_inacbg_import");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "idrg_to_inacbg_import"));
            }

            JsonNode dataDiagnosa = msg.path("data").path("diagnosa").path("expanded");
            JsonNode dataProsedur = msg.path("data").path("procedure").path("expanded");

            List<JsonNode> diagList = sortByNo(dataDiagnosa);
            List<JsonNode> procList = sortByNo(dataProsedur);

            Sequel.menghapusSmc("inacbg_diagnosa_pasien_smc", "no_sep = ?", nomorSep);
            for (JsonNode dx : diagList) {
                String keterangan = "";
                String locked = "1";
                if (!"200".equals(dx.path("metadata").path("code").asText())) {
                    keterangan = dx.path("metadata").path("error_no").asText() + " - " + dx.path("metadata").path("message").asText();
                    locked = "0";
                }
                try {
                    Sequel.executeRawSmc(
                            "insert into inacbg_diagnosa_pasien_smc values (?, ?, ?, ?, ?, ?)",
                            nomorSep, dx.path("code").asText(), cleanKar(dx.path("display").asText()),
                            dx.path("no").asText(), keterangan, locked);
                } catch (Exception e2) {
                    // continue on duplicate
                }
            }

            Sequel.menghapusSmc("inacbg_prosedur_pasien_smc", "no_sep = ?", nomorSep);
            for (JsonNode p : procList) {
                String keterangan = "";
                String locked = "1";
                if (!"200".equals(p.path("metadata").path("code").asText())) {
                    keterangan = p.path("metadata").path("error_no").asText() + " - " + p.path("metadata").path("message").asText();
                    locked = "0";
                }
                try {
                    Sequel.executeRawSmc(
                            "insert into inacbg_prosedur_pasien_smc values (?, ?, ?, ?, ?, ?)",
                            nomorSep, p.path("code").asText(), cleanKar(p.path("display").asText()),
                            p.path("no").asText(), keterangan, locked);
                } catch (Exception e2) {
                    // continue on duplicate
                }
            }

            return new EKlaimResult(true, "Import koding ke INACBG berhasil!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error importIdrgToInacbg: " + e.getMessage());
        }
    }

    public EKlaimResult setDiagnosaInacbg(String nomorSep, String diagnosa) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "inacbg_diagnosa_set");
            metadata.put("nomor_sep", nomorSep);
            ObjectNode data = request.putObject("data");
            data.put("diagnosa", diagnosa);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "inacbg_diagnosa_set"));
            }

            return new EKlaimResult(true, "Diagnosa INACBG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error setDiagnosaInacbg: " + e.getMessage());
        }
    }

    public EKlaimResult setProsedurInacbg(String nomorSep, String prosedur) {
        try {
            if (prosedur == null || prosedur.isEmpty()) {
                prosedur = "#";
            }

            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "inacbg_procedure_set");
            metadata.put("nomor_sep", nomorSep);
            ObjectNode data = request.putObject("data");
            data.put("procedure", prosedur);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "inacbg_procedure_set"));
            }

            return new EKlaimResult(true, "Prosedur INACBG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error setProsedurInacbg: " + e.getMessage());
        }
    }

    public EKlaimResult groupingStage1Inacbg(String nomorSep, String coderNik) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "grouper");
            metadata.put("stage", "1");
            metadata.put("grouper", "inacbg");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "grouper stage 1"));
            }

            JsonNode responseInacbg = msg.path("response_inacbg");
            String cbgCode = responseInacbg.path("cbg").path("code").asText();
            String cbgDescription = responseInacbg.path("cbg").path("description").asText();
            String tariff = "0";
            if (responseInacbg.has("tariff")) {
                tariff = responseInacbg.path("tariff").asText("0");
            }

            Sequel.executeRawSmc(
                    "insert into inacbg_grouping_stage12 values (?, ?, ?, ?, ?) on duplicate key update code_cbg = values(code_cbg), deskripsi = values(deskripsi), tarif = values(tarif), top_up = values(top_up)",
                    nomorSep, cbgCode, cbgDescription, tariff, "Tidak Ada");

            if (cbgCode.startsWith("X")) {
                return new EKlaimResult(false, cbgDescription, cbgCode);
            }

            JsonNode specialCmg = msg.path("special_cmg_option");
            if (specialCmg.isArray() && specialCmg.size() > 0) {
                Sequel.menghapusSmc("tempinacbg", "coder_nik = ?", coderNik);
                for (JsonNode item : specialCmg) {
                    Sequel.menyimpantfSmc("tempinacbg", null,
                            coderNik, item.path("code").asText(), item.path("description").asText(), item.path("type").asText());
                }

                Sequel.mengupdateSmc("inacbg_grouping_stage12", "top_up = ?", "no_sep = ?", "Belum", nomorSep);

                return new EKlaimResult(true, "inacbg_stage2", null);
            }

            return new EKlaimResult(true, "Grouping INACBG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error groupingStage1Inacbg: " + e.getMessage());
        }
    }

    public EKlaimResult groupingStage2Inacbg(String nomorSep, String coderNik, String specialCmg) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "grouper");
            metadata.put("stage", 2);
            metadata.put("grouper", "inacbg");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);
            data.put("special_cmg", specialCmg);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "grouper stage 2"));
            }

            Sequel.menghapusSmc("tempinacbg", "coder_nik = ?", coderNik);

            String topUp = "Tidak Ada";
            JsonNode responseInacbg = msg.path("response_inacbg");
            JsonNode specialCmgArr = responseInacbg.path("special_cmg");

            if (specialCmgArr.isArray() && specialCmgArr.size() > 0) {
                topUp = "Sudah";
                for (JsonNode sc : specialCmgArr) {
                    Sequel.menyimpantfSmc("inacbg_grouping_stage2_smc", null,
                            nomorSep, sc.path("code").asText(), sc.path("description").asText(),
                            sc.path("type").asText(), sc.path("tariff").asText());
                }
            }

            String cbgCode = responseInacbg.path("cbg").path("code").asText();
            String cbgDescription = responseInacbg.path("cbg").path("description").asText();
            String tariff = responseInacbg.path("tariff").asText("0");

            Sequel.mengupdateSmc("inacbg_grouping_stage12",
                    "code_cbg = ?, deskripsi = ?, tarif = ?, top_up = ?",
                    "no_sep = ?",
                    cbgCode, cbgDescription, tariff, topUp, nomorSep);

            return new EKlaimResult(true, "Top Up INACBG berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error groupingStage2Inacbg: " + e.getMessage());
        }
    }

    public EKlaimResult finalInacbg(String nomorSep, String coderNik) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "inacbg_grouper_final");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "inacbg_grouper_final"));
            }

            Sequel.menyimpantfSmc("inacbg_klaim_final_smc", null, nomorSep, coderNik);

            return new EKlaimResult(true, "Grouping INACBG sudah final dan berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error finalInacbg: " + e.getMessage());
        }
    }

    public EKlaimResult finalisasiKlaim(String nomorSep, String coderNik) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "claim_final");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);
            data.put("coder_nik", coderNik);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "claim_final"));
            }

            String importKoding = Sequel.cariIsiSmc("select setting.sistem_import_koding from setting");
            String noRawat = Sequel.cariIsiSmc("select bridging_sep.no_rawat from bridging_sep where bridging_sep.no_sep = ?", nomorSep);
            String statusLanjut = Sequel.cariIsiSmc("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat = ?", noRawat);

            if ("IDRG".equals(importKoding.trim())) {
                Sequel.menghapusSmc("diagnosa_pasien", "no_rawat = ? and status = ?", noRawat, statusLanjut);
                Sequel.menghapusSmc("prosedur_pasien", "no_rawat = ? and status = ?", noRawat, statusLanjut);

                Sequel.executeRawSmc(
                        "insert into diagnosa_pasien (no_rawat, kd_penyakit, status, prioritas, status_penyakit) " +
                        "select * from (select s.no_rawat, i.kode_icd10 as kd_penyakit, r.status_lanjut as status, i.urut as prioritas, " +
                        "if(exists(select * from diagnosa_pasien d join reg_periksa r2 on d.no_rawat = r2.no_rawat where r2.no_rkm_medis = r.no_rkm_medis), 'Lama', 'Baru') as status_penyakit " +
                        "from idrg_diagnosa_pasien_smc i join bridging_sep s on i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat " +
                        "where i.no_sep = ?) t",
                        nomorSep);

                Sequel.executeRawSmc(
                        "insert into prosedur_pasien (no_rawat, kode, status, prioritas, jumlah) " +
                        "select * from (select s.no_rawat, i.kode_icd9 as kode, r.status_lanjut as status, i.urut as prioritas, i.multiplicity as jumlah " +
                        "from idrg_prosedur_pasien_smc i join bridging_sep s on i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat " +
                        "where i.no_sep = ?) t",
                        nomorSep);
            } else if ("INA-CBG".equals(importKoding.trim())) {
                Sequel.menghapusSmc("diagnosa_pasien", "no_rawat = ? and status = ?", noRawat, statusLanjut);
                Sequel.menghapusSmc("prosedur_pasien", "no_rawat = ? and status = ?", noRawat, statusLanjut);

                Sequel.executeRawSmc(
                        "insert into diagnosa_pasien (no_rawat, kd_penyakit, status, prioritas, status_penyakit) " +
                        "select * from (select s.no_rawat, i.kode_icd10 as kd_penyakit, r.status_lanjut as status, i.urut as prioritas, " +
                        "if(exists(select * from diagnosa_pasien d join reg_periksa r2 on d.no_rawat = r2.no_rawat where r2.no_rkm_medis = r.no_rkm_medis), 'Lama', 'Baru') as status_penyakit " +
                        "from inacbg_diagnosa_pasien_smc i join bridging_sep s on i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat " +
                        "where i.no_sep = ?) t",
                        nomorSep);

                Sequel.executeRawSmc(
                        "insert into prosedur_pasien (no_rawat, kode, status, prioritas, jumlah) " +
                        "select * from (select s.no_rawat, i.kode_icd9 as kode, r.status_lanjut as status, i.urut as prioritas, 1 as jumlah " +
                        "from inacbg_prosedur_pasien_smc i join bridging_sep s on i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat " +
                        "where i.no_sep = ?) t",
                        nomorSep);
            }

            return cetakKlaim(nomorSep);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error finalisasiKlaim: " + e.getMessage());
        }
    }

    public EKlaimResult cetakKlaim(String nomorSep) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "claim_print");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "claim_print"));
            }

            String encodedPDF = msg.path("data").asText();
            String filename = "pages/pdf/" + nomorSep + ".pdf";

            File pdfFile = new File(filename);
            pdfFile.getParentFile().mkdirs();

            if (pdfFile.exists()) {
                pdfFile.delete();
            }

            Files.write(pdfFile.toPath(), Base64.getDecoder().decode(encodedPDF));

            boolean exists = Sequel.cariExistsSmc("select * from inacbg_cetak_klaim where no_sep = ?", nomorSep);
            if (!exists) {
                Sequel.menyimpantfSmc("inacbg_cetak_klaim", null, nomorSep, "pages/pdf/" + nomorSep + ".pdf", null);
            }

            return new EKlaimResult(true, "Klaim berhasil disimpan!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error cetakKlaim: " + e.getMessage());
        }
    }

    public EKlaimResult kirimKlaimIndividual(String nomorSep) {
        try {
            ObjectNode request = mapper.createObjectNode();
            ObjectNode metadata = request.putObject("metadata");
            metadata.put("method", "send_claim_individual");
            ObjectNode data = request.putObject("data");
            data.put("nomor_sep", nomorSep);

            JsonNode msg = apiClient.request(mapper.writeValueAsString(request));

            if (!isSuccess(msg)) {
                return new EKlaimResult(false, null, formatError(msg, "send_claim_individual"));
            }

            Sequel.executeRawSmc("update inacbg_cetak_klaim set kirim_ke_dc = now() where no_sep = ?", nomorSep);

            return new EKlaimResult(true, "Klaim berhasil dikirim online!", null);
        } catch (Exception e) {
            return new EKlaimResult(false, null, "Error kirimKlaimIndividual: " + e.getMessage());
        }
    }

    public String getDiagnosaIdrg(String nomorSep) {
        StringBuilder diagnosa = new StringBuilder();
        try (PreparedStatement ps = conn.prepareStatement(
                "select kode_icd10, urut from idrg_diagnosa_pasien_smc where no_sep = ? order by urut asc")) {
            ps.setString(1, nomorSep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (diagnosa.length() > 0) {
                        diagnosa.append("#");
                    }
                    diagnosa.append(rs.getString("kode_icd10"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif getDiagnosaIdrg: " + e);
        }
        return diagnosa.toString();
    }

    public String getProsedurIdrg(String nomorSep) {
        StringBuilder prosedur = new StringBuilder();
        try (PreparedStatement ps = conn.prepareStatement(
                "select kode_icd9, multiplicity, urut from idrg_prosedur_pasien_smc where no_sep = ? order by urut asc")) {
            ps.setString(1, nomorSep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (prosedur.length() > 0) {
                        prosedur.append("#");
                    }
                    prosedur.append(rs.getString("kode_icd9")).append("+").append(rs.getString("multiplicity"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif getProsedurIdrg: " + e);
        }
        return prosedur.toString();
    }

    public String getDiagnosaInacbg(String nomorSep) {
        StringBuilder diagnosa = new StringBuilder();
        try (PreparedStatement ps = conn.prepareStatement(
                "select kode_icd10, urut from inacbg_diagnosa_pasien_smc where no_sep = ? order by urut asc")) {
            ps.setString(1, nomorSep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (diagnosa.length() > 0) {
                        diagnosa.append("#");
                    }
                    diagnosa.append(rs.getString("kode_icd10"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif getDiagnosaInacbg: " + e);
        }
        return diagnosa.toString();
    }

    public String getProsedurInacbg(String nomorSep) {
        StringBuilder prosedur = new StringBuilder();
        try (PreparedStatement ps = conn.prepareStatement(
                "select kode_icd9, urut from inacbg_prosedur_pasien_smc where no_sep = ? order by urut asc")) {
            ps.setString(1, nomorSep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (prosedur.length() > 0) {
                        prosedur.append("#");
                    }
                    prosedur.append(rs.getString("kode_icd9"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif getProsedurInacbg: " + e);
        }
        return prosedur.toString();
    }

    private boolean isSuccess(JsonNode msg) {
        return "200".equals(msg.path("metadata").path("code").asText());
    }

    private String formatError(JsonNode msg, String method) {
        return String.format("[%s] method \"%s\": %s - %s",
                msg.path("metadata").path("code").asText(),
                method,
                msg.path("metadata").path("error_no").asText(),
                msg.path("metadata").path("message").asText());
    }

    private List<JsonNode> sortByNo(JsonNode arrayNode) {
        List<JsonNode> list = new ArrayList<>();
        if (arrayNode.isArray()) {
            for (JsonNode node : arrayNode) {
                list.add(node);
            }
        }
        list.sort((a, b) -> Integer.compare(a.path("no").asInt(), b.path("no").asInt()));
        return list;
    }

    private String cleanKar(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "").replace("\"", "");
    }
}
