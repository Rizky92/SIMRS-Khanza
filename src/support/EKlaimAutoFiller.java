package support;

import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class EKlaimAutoFiller {

    public static class ClaimData {
        public String noRawat = "";
        public String noSep = "";
        public String noRkmMedis = "";
        public String noKartu = "";
        public String nik = "";
        public String nmPasien = "";
        public String umurDaftar = "";
        public String sttsUmur = "";
        public String tglLahir = "";
        public String jenisKelamin = "";
        public String alamat = "";
        public String tglRegistrasi = "";
        public String jamReg = "";
        public String nmPoli = "";
        public String nmDokter = "";
        public String statusLanjut = "";
        public String pngJawab = "";
        public String noSitb = "";
        public String tglKeluar = "";
        public String kelasRawat = "";
        public String asalRujukan = "";
        public String caraMasuk = "";
        public String jenisRawat = "1";
        public String sistole = "120";
        public String diastole = "90";
        public int icuIndikator = 0;
        public int icuLos = 0;
        public String upgradeClassInd = "0";
        public String upgradeClassClass = "";
        public String dischargeStatus = "1";
        public String dializerSingleUse = "0";
        public String gender = "";

        // COVID fields
        public boolean corona = false;
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
    }

    public static ClaimData fill(String noSep) {
        ClaimData data = new ClaimData();
        Connection conn = koneksiDB.condb();

        try (PreparedStatement ps = conn.prepareStatement("select bridging_sep.no_sep, bridging_sep.tglsep, bridging_sep.asal_rujukan, bridging_sep.no_kartu, " +
            "date(bridging_sep.tglpulang) as tglpulang, reg_periksa.*, pasien.nm_pasien, pasien.jk, pasien.umur, pasien.tgl_lahir, pasien.no_ktp, (select " +
            "inacbg_pasien_tb_smc.no_sitb from inacbg_pasien_tb_smc where inacbg_pasien_tb_smc.no_rkm_medis = reg_periksa.no_rkm_medis limit 1) as no_sitb, " +
            "dokter.nm_dokter, poliklinik.nm_poli, penjab.png_jawab from bridging_sep join maping_dokter_dpjpvclaim on bridging_sep.kddpjp = maping_dokter_dpjpvclaim.kd_dokter_bpjs " +
            "join dokter on maping_dokter_dpjpvclaim.kd_dokter = dokter.kd_dokter join reg_periksa on bridging_sep.no_rawat = reg_periksa.no_rawat join pasien on " +
            "reg_periksa.no_rkm_medis = pasien.no_rkm_medis join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
            "where bridging_sep.no_sep = ?"
        )) {
            ps.setString(1, noSep);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return data;
                }

                data.noRawat = safe(rs.getString("no_rawat"));
                data.noSep = noSep;
                data.noRkmMedis = safe(rs.getString("no_rkm_medis"));
                data.noKartu = safe(rs.getString("no_kartu"));
                data.nik = safe(rs.getString("no_ktp"));
                data.nmPasien = safe(rs.getString("nm_pasien"));
                data.umurDaftar = safe(rs.getString("umurdaftar"));
                data.sttsUmur = safe(rs.getString("sttsumur"));
                data.tglLahir = safe(rs.getString("tgl_lahir"));
                data.jenisKelamin = safe(rs.getString("jk"));
                data.alamat = safe(rs.getString("almt_pj"));
                data.tglRegistrasi = safe(rs.getString("tglsep"));
                data.jamReg = safe(rs.getString("jam_reg"));
                data.nmPoli = safe(rs.getString("nm_poli"));
                data.nmDokter = safe(rs.getString("nm_dokter"));
                data.statusLanjut = safe(rs.getString("status_lanjut"));
                data.pngJawab = safe(rs.getString("png_jawab"));
                data.noSitb = safe(rs.getString("no_sitb"));
                data.tglKeluar = safe(rs.getString("tgl_registrasi"));
                data.gender = data.jenisKelamin.equals("L") ? "1" : "2";
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (patient) : " + e);
            return data;
        }

        String noRawat = data.noRawat;

        fillVitalSigns(data, conn, noRawat);
        fillTglKeluar(data, conn, noRawat);
        fillIcuData(data, conn, noRawat);
        fillDischargeStatus(data, conn, noRawat);
        fillUpgradeClass(data, conn, noRawat);
        fillKelasRawat(data, conn, noSep);
        fillCaraMasuk(data, conn, noRawat);
        fillDializer(data, conn, noSep);
        fillCovidData(data, conn, noRawat);

        if (data.statusLanjut.equals("Ranap")) {
            data.jenisRawat = "1";
        } else {
            data.jenisRawat = "2";
        }

        return data;
    }

    private static void fillVitalSigns(ClaimData data, Connection conn, String noRawat) {
        String table = data.statusLanjut.equals("Ranap") ? "pemeriksaan_ranap" : "pemeriksaan_ralan";
        String sql = "(select tensi from " + table + " where no_rawat = ? order by tgl_perawatan desc, jam_rawat desc limit 1) union all (select '120/90' as tensi) limit 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tensi = safe(rs.getString(1));
                    String[] parts = tensi.split("/");
                    if (parts.length >= 2) {
                        if (!parts[0].trim().isEmpty()) {
                            data.sistole = parts[0].trim();
                        }
                        if (!parts[1].trim().isEmpty()) {
                            data.diastole = parts[1].trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (vital) : " + e);
        }
    }

    private static void fillTglKeluar(ClaimData data, Connection conn, String noRawat) {
        if (!data.statusLanjut.equals("Ranap")) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("select tgl_keluar from kamar_inap where no_rawat = ? order by tgl_keluar desc limit 1")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tgl = rs.getString(1);
                    if (tgl != null && !tgl.isEmpty()) {
                        data.tglKeluar = tgl;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (tglKeluar) : " + e);
        }
    }

    private static void fillIcuData(ClaimData data, Connection conn, String noRawat) {
        if (!data.statusLanjut.equals("Ranap")) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("select ifnull(sum(lama), 0) as total_icu from kamar_inap where kd_kamar like '%icu%' and no_rawat = ?")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int icu = rs.getInt("total_icu");
                    data.icuLos = icu;
                    data.icuIndikator = icu > 0 ? 1 : 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (icu) : " + e);
        }
    }

    private static void fillDischargeStatus(ClaimData data, Connection conn, String noRawat) {
        Map<String, String> statusMap = new LinkedHashMap<>();
        statusMap.put("Sembuh", "1");
        statusMap.put("Sehat", "1");
        statusMap.put("Atas Persetujuan Dokter", "1");
        statusMap.put("Rujuk", "2");
        statusMap.put("APS", "3");
        statusMap.put("Pulang Paksa", "3");
        statusMap.put("Atas Permintaan Sendiri", "3");
        statusMap.put("Meninggal", "4");
        statusMap.put("+", "4");
        statusMap.put("Lain-lain", "5");

        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            try (PreparedStatement ps = conn.prepareStatement("select count(*) from kamar_inap where stts_pulang = ? and no_rawat = ?")) {
                ps.setString(1, entry.getKey());
                ps.setString(2, noRawat);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        data.dischargeStatus = entry.getValue();
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif EKlaimAutoFiller (discharge) : " + e);
            }
        }

        data.dischargeStatus = "1";
    }

    private static void fillUpgradeClass(ClaimData data, Connection conn, String noRawat) {
        String naikkelas = "";

        try (PreparedStatement ps = conn.prepareStatement("select klsnaik from bridging_sep where no_rawat = ?")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    naikkelas = safe(rs.getString(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (upgrade) : " + e);
        }

        if (naikkelas.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement("select klsnaik from bridging_sep_internal where no_rawat = ?")) {
                ps.setString(1, noRawat);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        naikkelas = safe(rs.getString(1));
                    }
                }
            } catch (Exception e) {
                // bridging_sep_internal may not exist
            }
        }

        if (!naikkelas.isEmpty()) {
            data.upgradeClassInd = "1";
            switch (naikkelas) {
                case "1": data.upgradeClassClass = "vvip"; break;
                case "2": data.upgradeClassClass = "vip"; break;
                case "3": data.upgradeClassClass = "kelas_1"; break;
                case "4": data.upgradeClassClass = "kelas_2"; break;
                default: data.upgradeClassClass = ""; break;
            }
        }
    }

    private static void fillKelasRawat(ClaimData data, Connection conn, String noSep) {
        try (PreparedStatement ps = conn.prepareStatement("select klsrawat from bridging_sep where no_sep = ?")) {
            ps.setString(1, noSep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.kelasRawat = safe(rs.getString(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (kelas) : " + e);
        }
    }

    private static void fillCaraMasuk(ClaimData data, Connection conn, String noRawat) {
        try (PreparedStatement ps = conn.prepareStatement("select asal_rujukan from bridging_sep where no_rawat = ?")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String asal = safe(rs.getString(1));
                    if (!asal.isEmpty()) {
                        data.asalRujukan = asal.substring(0, 1);
                        switch (data.asalRujukan) {
                            case "1": data.caraMasuk = "gp"; break;
                            case "2": data.caraMasuk = "hosp-trans"; break;
                            default: data.caraMasuk = "other"; break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (cara_masuk) : " + e);
        }
    }

    private static void fillDializer(ClaimData data, Connection conn, String noSep) {
        try (PreparedStatement ps = conn.prepareStatement("select exists(select * from bridging_sep where no_sep = ? and nmpolitujuan like 'hemodial%')")) {
            ps.setString(1, noSep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.dializerSingleUse = rs.getBoolean(1) ? "1" : "0";
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (dializer) : " + e);
        }
    }

    private static void fillCovidData(ClaimData data, Connection conn, String noRawat) {
        String sql = "select " +
                "if(pemulasaraan_jenazah='Ya',1,0) as ytpemulasaraan_jenazah, " +
                "if(kantong_jenazah='Ya',1,0) as ytkantong_jenazah, " +
                "if(peti_jenazah='Ya',1,0) as ytpeti_jenazah, " +
                "if(plastik_erat='Ya',1,0) as ytplastik_erat, " +
                "if(desinfektan_jenazah='Ya',1,0) as ytdesinfektan_jenazah, " +
                "if(mobil_jenazah='Ya',1,0) as ytmobil_jenazah, " +
                "if(desinfektan_mobil_jenazah='Ya',1,0) as ytdesinfektan_mobil_jenazah, " +
                "if(covid19_status_cd='ODP',1,if(covid19_status_cd='PDP',2,3)) as ytcovid19_status_cd, " +
                "if(covid19_cc_ind='Ya',1,0) as ytcovid19_cc_ind, " +
                "nomor_kartu_t, episodes1, episodes2, episodes3, episodes4, episodes5, episodes6 " +
                "from perawatan_corona where no_rawat = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.corona = true;
                    data.pemulasaraanJenazah = rs.getInt("ytpemulasaraan_jenazah");
                    data.kantongJenazah = rs.getInt("ytkantong_jenazah");
                    data.petiJenazah = rs.getInt("ytpeti_jenazah");
                    data.plastikErat = rs.getInt("ytplastik_erat");
                    data.desinfektanJenazah = rs.getInt("ytdesinfektan_jenazah");
                    data.mobilJenazah = rs.getInt("ytmobil_jenazah");
                    data.desinfektanMobilJenazah = rs.getInt("ytdesinfektan_mobil_jenazah");
                    data.covid19StatusCd = rs.getInt("ytcovid19_status_cd");
                    data.covid19CcInd = rs.getInt("ytcovid19_cc_ind");
                    data.nomorKartuT = safe(rs.getString("nomor_kartu_t"));
                    data.episodes1 = rs.getInt("episodes1");
                    data.episodes2 = rs.getInt("episodes2");
                    data.episodes3 = rs.getInt("episodes3");
                    data.episodes4 = rs.getInt("episodes4");
                    data.episodes5 = rs.getInt("episodes5");
                    data.episodes6 = rs.getInt("episodes6");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimAutoFiller (covid) : " + e);
        }
    }

    private static String safe(String val) {
        return val == null ? "" : val;
    }
}
