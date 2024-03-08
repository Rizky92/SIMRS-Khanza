/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 *
 * @author USER
 */
public class ApiADAMLABS
{
    private final String URL = koneksiDB.ADAMLABSAPIURL(),
                         KEY = koneksiDB.ADAMLABSAPIKEY(),
                         KODERS = koneksiDB.ADAMLABSAPIKODERS();
    
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper obj = new ObjectMapper();
    
    private HttpEntity requestEntity;
    private JsonNode response;
    private StringBuilder jsonBuilder;
    private PreparedStatement ps, psdetail;
    private ResultSet rs, rsdetail;
    
    public void registrasi(String kodeRegistrasi)
    {
        try {
            ps = koneksi.prepareStatement(
                "select " +
                    "permintaan_lab.noorder, " +
                    "permintaan_lab.diagnosa_klinis, " +
                    "pasien.nm_pasien, " +
                    "pasien.no_rkm_medis, " +
                    "pasien.jk, " +
                    "pasien.alamat, " +
                    "pasien.no_tlp, " +
                    "pasien.tgl_lahir, " +
                    "pasien.no_ktp, " +
                    "(select pemeriksaan_ralan.berat from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat = permintaan_lab.no_rawat and pemeriksaan_ralan.berat is not null and pemeriksaan_ralan.berat != '') as bb, " +
                    "if (permintaan_lab.informasi_tambahan like '%cito%', 'Cito', 'Reguler') as jenis_registrasi, " +
                    "pasien.kd_prop, " +
                    "propinsi.nm_prop, " +
                    "pasien.kd_kab, " +
                    "kabupaten.nm_kab, " +
                    "pasien.kd_kec, " +
                    "kecamatan.kd_kec, " +
                    "permintaan_lab.dokter_perujuk as kd_dokter_perujuk, " +
                    "dokter_perujuk.nm_dokter as nm_dokter_perujuk, " +
                    "reg_periksa.kd_poli, " +
                    "poliklinik.nm_poli, " +
                    "reg_periksa.kd_pj, " +
                    "penjab.png_jawab " +
                "from permintaan_lab " +
                "join reg_periksa on permintaan_lab.no_rawat = reg_periksa.no_rawat " +
                "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "left join propinsi on pasien.kd_prop = propinsi.kd_prop " +
                "left join kabupaten on pasien.kd_kab = kabupaten.kd_kab " +
                "left join kecamatan on pasien.kd_kec = kecamatan.kd_kec " +
                "join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli " +
                "join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
                "join dokter dokter_perujuk on permintaan_lab.dokter_perujuk = dokter_perujuk.kd_dokter " +
                "where permintaan_lab.noorder = ?"
            );
            try {
                ps.setString(1, kodeRegistrasi);
                rs = ps.executeQuery();
                if (rs.next()) {
                    jsonBuilder = new StringBuilder(
                        "{" +
                            "\"registrasi\": {" +
                                "\"no_registrasi\": \"" + rs.getString("noorder") + "\"," +
                                "\"diagnosa_awal\": \"-\"," +
                                "\"keterangan_klinis\": \"" + rs.getString("diagnosa_klinis") + "\"," +
                                "\"kode_rs\": \"" + KODERS + "\"" +
                            "}," +
                            "\"pasien\": {" +
                                "\"nama\": \"" + rs.getString("nm_pasien") + "\"," +
                                "\"no_rm\": \"" + rs.getString("no_rkm_medis") + "\"," +
                                "\"jenis_kelamin\": \"" + rs.getString("jk") + "\"," +
                                "\"alamat\": \"" + rs.getString("alamat") + "\"," +
                                "\"no_telphone\": \"" + rs.getString("no_tlp") + "\"," +
                                "\"tanggal_lahir\": \"" + df.format(rs.getDate("tgl_lahir")) + "\"," +
                                "\"nik\": \"" + rs.getString("no_ktp") +"\"," +
                                "\"ras\": \"-\"," +
                                "\"berat_badan\": \"" + rs.getString("bb") + "kg\"," +
                                "\"jenis_registrasi\" : \"" + rs.getString("jenis_registrasi") + "\"," +
                                "\"m_provinsi_id\": \"Dki Jakarta\"," +
                                "\"m_kabupaten_id\": \"Kota Jakarta Barat\"," +
                                "\"m_kecamatan_id\": \"Tambora\"" +
                            "}," +
                            "\"kode_dokter_pengirim\": \"" + rs.getString("kd_dokter_perujuk") + "\"," +
                            "\"nama_dokter_pengirim\": \"" + rs.getString("nm_dokter_perujuk") + "\"," +
                            "\"kode_unit_asal\": \"" + rs.getString("kd_poli") + "\"," +
                            "\"nama_unit_asal\": \"" + rs.getString("nm_poli") + "\"," +
                            "\"kode_penjamin\": \"" + rs.getString("kd_pj") + "\"," +
                            "\"nama_penjamin\": \"" + rs.getString("png_jawab") + "\"," +
                            "\"kode_icdt\": \"-\"," +
                            "\"nama_icdt\": \"-\"," +
                            "\"tindakan\": ["
                    );
                    
                    try {
                        psdetail = koneksi.prepareStatement(
                            "select permintaan_pemeriksaan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan " +
                            "from permintaan_pemeriksaan_lab " +
                            "join jns_perawatan_lab on permintaan_pemeriksaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw " +
                            "where permintaan_pemeriksaan_lab.noorder = ?"
                        );
                        try {
                            psdetail.setString(1, kodeRegistrasi);
                            rsdetail = psdetail.executeQuery();
                            while (rsdetail.next()) {
                                jsonBuilder.append("{" +
                                    "\"kode_tindakan\": \"" + rs.getString("kd_jenis_prw") + "\"," +
                                    "\"nama_tindakan\": \"" + rs.getString("nm_perawatan") + "\"" +
                                "},");
                            }
                            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                        } finally {
                            if (rsdetail != null) {
                                rsdetail.close();
                            }
                            if (psdetail != null) {
                                psdetail.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    jsonBuilder.append("]}");
                }
                
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void tarikHasilLab()
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void tarikHasilLab(String kodeRegistrasi)
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void updateHasilLab()
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
}
