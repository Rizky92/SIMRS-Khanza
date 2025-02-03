/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile 
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */

package fungsi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import uz.ncipro.calendar.JDateTimePicker;

/**
 *
 * @author Owner
 */
public final class sekuel {
    private javax.swing.ImageIcon icon = null;
    private String folder, AKTIFKANTRACKSQL = koneksiDB.AKTIFKANTRACKSQL();
    private final Connection connect=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int angka=0;
    private double angka2=0;
    private String dicari="";
    private Date tanggal=new Date();
    private boolean bool=false;
    private static boolean pemberlakuanBatasEdit = true;
    private final DecimalFormat df2 = new DecimalFormat("####");
    private SimpleDateFormat formattanggal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date waktumulai,kegiatan;
    private long bedawaktu=0;
    
    private String track = "";
    
    public sekuel(){
        super();
    }
    
    public static void nyalakanBatasEdit() {
        try (ResultSet rs = koneksiDB.condb().prepareStatement("select setting.pemberlakuan_2x24_jam from setting").executeQuery()) {
            if (rs.next()) {
                sekuel.pemberlakuanBatasEdit = rs.getString(1).equals("Yes");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            sekuel.pemberlakuanBatasEdit = false;
        }

        System.out.println("\nPemberlakuan Batas Edit : " + (sekuel.pemberlakuanBatasEdit ? "AKTIF" : "TIDAK AKTIF"));
    }

    public boolean cekTanggalRegistrasiSmc(String noRawat, Date tgl) {
        Date tglRegist = cariTglSmc("select concat(tgl_registrasi, ' ', jam_reg) from reg_periksa where no_rawat = ?", noRawat);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (tgl == null) {
            tgl = new Date();
        }

        if ((tgl.getTime() - tglRegist.getTime()) / 1000 < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, jam input data / perubahan data minimal di jam " + sdf.format(tglRegist) + " !");
            return false;
        }

        return true;
    }

    public boolean cekTanggalRegistrasiSmc(String noRawat, String tgl) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tglRegist = cariTglSmc("select concat(tgl_registrasi, ' ', jam_reg) from reg_periksa where no_rawat = ?", noRawat),
            tglKegiatan = null;

        if (tgl == null || tgl.isBlank()) {
            tglKegiatan = new Date();
        } else {
            try {
                tglKegiatan = sdf.parse(tgl);
            } catch (Exception e) {
                tglKegiatan = new Date();
            }
        }

        if ((tglKegiatan.getTime() - tglRegist.getTime()) / 1000 < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, jam input data / perubahan data minimal di jam " + sdf.format(tglRegist) + " !");
            return false;
        }

        return true;
    }

    public boolean cekTanggalRegistrasiSmc(String noRawat) {
        return cekTanggalRegistrasiSmc(noRawat, "");
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public String autonomorSmc(String prefix, String table, int panjang, String pad) {
        try (PreparedStatement ps = connect.prepareStatement("select concat(?, lpad(count(*), ?, ?)) from " + table)) {
            ps.setString(1, prefix);
            ps.setInt(2, panjang);
            ps.setString(3, pad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        if (prefix == null) {
            prefix = "";
        }
        String output = "";
        for (int i = 0; i < panjang - 1; i++) {
            output += pad;
            if (output.length() >= panjang - 1) {
                output = output.substring(0, panjang - 1);
                break;
            }
        }
        return prefix + output + "1";
    }
    
    public String autonomorSmc(String prefix, String separator, String table, String kolom, int panjang, String pad, String tanggal, int next) {
        String sql = 
            "select concat(if(? is null or ? = '', '', concat(?, ?)), date_format(" +
            "?, concat_ws(?, '%Y', '%m', '%d')), ?, lpad(ifnull(max(convert(right(" +
            table + "." + kolom + ", ?), unsigned)), 0) + ?, ?, ?)) from " + table +
            " where " + table + "." + kolom + " like concat(if(? is null or ? = '', " +
            "'', concat(?, ?)), date_format(?, concat_ws(?, '%Y', '%m', '%d')), '%')";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, prefix);
            ps.setString(2, prefix);
            ps.setString(3, prefix);
            ps.setString(4, separator);
            ps.setString(5, tanggal);
            ps.setString(6, separator);
            ps.setString(7, separator);
            ps.setInt(8, panjang);
            ps.setInt(9, next);
            ps.setInt(10, panjang);
            ps.setString(11, pad);
            ps.setString(12, prefix);
            ps.setString(13, prefix);
            ps.setString(14, prefix);
            ps.setString(15, separator);
            ps.setString(16, tanggal);
            ps.setString(17, separator);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return "";
    }
    
    
    public String autonomorSmc(String prefix, String separator, String table, String kolom, int panjang, String pad, String tanggal) {
        return autonomorSmc(prefix, separator, table, kolom, panjang, pad, tanggal, 1);
    }

    public String cariIsiSmc(String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return "";
    }

    public boolean cariExistsSmc(String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement("select exists(" + sql + ")")) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return false;
    }

    public int cariIntegerSmc(String sql, int defaultValue, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return defaultValue;
    }
    
    public int cariIntegerSmc(String sql, String... values) {
        return cariIntegerSmc(sql, 0, values);
    }

    public double cariDoubleSmc(String sql, double defaultValue, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return defaultValue;
    }
    
    public double cariDoubleSmc(String sql, String... values) {
        return cariDoubleSmc(sql, 0, values);
    }

    public Date cariTglSmc(String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return (Date) rs.getTimestamp(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return null;
    }

    public Blob cariBlobSmc(String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBlob(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return null;
    }

    public ByteArrayInputStream cariGambarSmc(String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ByteArrayInputStream(rs.getBytes(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return null;
    }

    public void menyimpanSmc(String table, String kolom, String... values) {
        String sql = "insert into " + table + " (" + kolom + ") values (";
        if (kolom == null || kolom.isBlank()) {
            sql = "insert into " + table + " values (";
        }
        for (int i = 0; i < values.length; i++) {
            sql = sql.concat("?, ");
        }

        try (PreparedStatement ps = connect.prepareStatement(sql.substring(0, sql.length() - 2).concat(")"))) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("insert")));
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data!");
        }
    }

    public boolean menyimpantfSmc(String table, String kolom, String... values) {
        String sql = "insert into " + table + " (" + kolom + ") values (";
        if (kolom == null || kolom.isBlank()) {
            sql = "insert into " + table + " values (";
        }
        for (int i = 0; i < values.length; i++) {
            sql = sql.concat("?, ");
        }

        try (PreparedStatement ps = connect.prepareStatement(sql.substring(0, sql.length() - 2).concat(")"))) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("insert")));
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return false;
    }

    public boolean menyimpantfNotifSmc(String judulOnDuplicate, String table, String kolom, String... values) {
        String sql = "insert into " + table + " (" + kolom + ") values (";
        if (kolom == null || kolom.isBlank()) {
            sql = "insert into " + table + " values (";
        }
        for (int i = 0; i < values.length; i++) {
            sql = sql.concat("?, ");
        }

        try (PreparedStatement ps = connect.prepareStatement(sql.substring(0, sql.length() - 2).concat(")"))) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("insert")));
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (judulOnDuplicate != null && !judulOnDuplicate.isBlank()) {
                JOptionPane.showMessageDialog(null, "Tidak bisa menyimpan data, kemungkinan ada " + judulOnDuplicate + " yang sama dimasukkan sebelumnya.");
            }
        }
        return false;
    }

    public void mengupdateSmc(String table, String kolom, String where, String... values) {
        String sql = "update " + table + " set " + kolom + " where " + where;
        if (where == null || where.isBlank()) {
            sql = "update " + table + " set " + kolom;
        }

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("update")));
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Gagal mengupdate data!");
        }
    }

    public boolean mengupdatetfSmc(String table, String kolom, String where, String... values) {
        String sql = "update " + table + " set " + kolom + " where " + where;
        if (where == null || where.isBlank()) {
            sql = "update " + table + " set " + kolom;
        }

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("update")));
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Gagal mengupdate data!");
        }
        return false;
    }

    public void menghapusSmc(String table, String where, String... values) {
        String sql = "delete from " + table + " where " + where;
        if (where == null || where.isBlank()) {
            sql = "delete from " + table;
        }

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("delete")));
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.getMessage().contains("constraint")) {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data, kemungkinan masih digunakan di bagian lainnya!");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data!");
            }
        }
    }

    public void menghapusSmc(String table) {
        menghapusSmc(table, null);
    }

    public boolean menghapustfSmc(String table, String where, String... values) {
        String sql = "delete from " + table + " where " + where;
        if (where == null || where.isBlank()) {
            sql = "delete from " + table;
        }

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("delete")));
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return false;
    }

    public boolean executeRawSmc(String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf(sql.substring(0, 8))));
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return false;
    }

    public void deleteTemporary() {
        try (PreparedStatement ps = connect.prepareStatement("delete from temporary where temporary.temp37 = ?")) {
            ps.setString(1, akses.getalamatip());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void temporary(String... values) {
        String sql = "insert into temporary values (";
        for (int i = 0; i < 37; i++) {
            if (i < values.length) {
                sql = sql.concat("?, ");
            } else {
                sql = sql.concat("'', ");
            }
        }

        try (PreparedStatement ps = connect.prepareStatement(sql.concat("?)"))) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            ps.setString(values.length + 1, akses.getalamatip());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Gagal memproses hasil cetak..!!");
        }
    }

    public void deleteTemporaryLab() {
        try (PreparedStatement ps = connect.prepareStatement("delete from temporary_lab where temporary_lab.temp36 = ? and temporary_lab.temp37 = ?")) {
            ps.setString(1, akses.getkode());
            ps.setString(2, akses.getalamatip());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void temporaryLab(String... values) {
        String sql = "insert into temporary_lab values (";
        for (int i = 0; i < 36; i++) {
            if (i < values.length) {
                sql = sql.concat("?, ");
            } else {
                sql = sql.concat("'', ");
            }
        }

        try (PreparedStatement ps = connect.prepareStatement(sql.concat("?, ?)"))) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            ps.setString(values.length + 1, akses.getkode());
            ps.setString(values.length + 2, akses.getalamatip());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Gagal memproses hasil cetak..!!");
        }
    }

    public void deleteTampJurnal() {
        try (PreparedStatement ps = connect.prepareStatement("delete from tampjurnal_smc where user_id = ? and ip = ?")) {
            ps.setString(1, akses.getkode());
            ps.setString(2, akses.getalamatip());
            ps.executeUpdate();
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("delete")));
        } catch (HeadlessException | SQLException e) {
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null, "Gagal memproses data!");
        }
    }

    public void insertTampJurnal(String kdRek, String nmRek, double d, double k) {
        try (PreparedStatement ps = connect.prepareStatement("insert into tampjurnal_smc (kd_rek, nm_rek, debet, kredit, user_id, ip) values (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, kdRek);
            ps.setString(2, nmRek);
            ps.setDouble(3, d);
            ps.setDouble(4, k);
            ps.setString(5, akses.getkode());
            ps.setString(6, akses.getalamatip());
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("insert")));
            }
        } catch (SQLException e) {
            System.out.println("Notif: " + e);
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data!\nKemungkinan ada rekening yang sama dimasukkan sebelumnya!");
        }
    }

    public void insertTampJurnal(String kdRek, String nmRek, String d, String k) {
        this.insertTampJurnal(kdRek, nmRek, parseDouble(d), parseDouble(k));
    }

    public void insertOrUpdateTampJurnal(String kdRek, String nmRek, double d, double k) {
        if (d == 0 && k == 0) {
            return;
        }

        try (PreparedStatement ps = connect.prepareStatement("insert into tampjurnal_smc (kd_rek, nm_rek, debet, kredit, user_id, ip) values (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, kdRek);
            ps.setString(2, nmRek);
            ps.setDouble(3, d);
            ps.setDouble(4, k);
            ps.setString(5, akses.getkode());
            ps.setString(6, akses.getalamatip());
            if (ps.executeUpdate() > 0) {
                track = ps.toString();
                SimpanTrack(track.substring(track.indexOf("insert")));
            }
        } catch (SQLException e) {
            try (PreparedStatement ps = connect.prepareStatement("update tampjurnal_smc set debet = debet + ?, kredit = kredit + ? where kd_rek = ? and user_id = ? and ip = ?")) {
                ps.setDouble(1, d);
                ps.setDouble(2, k);
                ps.setString(3, kdRek);
                ps.setString(4, akses.getkode());
                ps.setString(5, akses.getalamatip());
                if (ps.executeUpdate() > 0) {
                    track = ps.toString();
                    SimpanTrack(track.substring(track.indexOf("update")));
                }
            } catch (SQLException ex) {
                System.out.println("Notif : " + ex);
                JOptionPane.showMessageDialog(null, "Gagal menyimpan data!\nKemungkinan ada rekening yang sama dimasukkan sebelumnya!");
            }
        }
    }

    public void insertOrUpdateTampJurnal(String kdRek, String nmRek, String d, String k) {
        this.insertOrUpdateTampJurnal(kdRek, nmRek, parseDouble(d), parseDouble(k));
    }

    public void logTaskid(String norawat, String kodebooking, String jenisPasien, String taskid, String request, String code, String message, String response, String wakturs) {
        try (PreparedStatement ps = connect.prepareStatement(
            "insert into referensi_mobilejkn_bpjs_taskid_response2 (no_rawat, kodebooking, jenispasien, taskid, request, code, message, response, waktu, waktu_rs) values (?, ?, ?, ?, ?, ?, ?, ?, now(), ?)"
        )) {
            ps.setString(1, norawat);
            ps.setString(2, kodebooking);
            ps.setString(3, jenisPasien);
            ps.setString(4, taskid);
            ps.setString(5, request);
            ps.setString(6, code);
            ps.setString(7, message);
            ps.setString(8, response);
            ps.setString(9, wakturs);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public Object bacaPengaturan(String group, String key) {
        try (PreparedStatement ps = connect.prepareStatement("select setting_tambahan.payload from setting_tambahan where setting_tambahan.group = ? and setting_tambahan.name = ?")) {
            ps.setString(1, group);
            ps.setString(2, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(rs.getString(1));
                    if (root.isArray()) {
                        return root.fields();
                    } else {
                        Map.Entry<String, JsonNode> json = root.fields().next();
                        if (json.getKey().equals("string")) {
                            return json.getValue().asText();
                        } else if (json.getKey().equals("boolean")) {
                            return json.getValue().asBoolean();
                        } else if (json.getKey().equals("number")) {
                            return json.getValue().asDouble();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return null;
    }

    public void menyimpan(String table,String value,String sama){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{                  
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);            
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada "+sama+" yang sama dimasukkan sebelumnya...!");
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            SimpanTrack("insert into "+table+" values("+value+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e); 
        }            
    }
    
    public void menyimpan2(String table,String value,String sama){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{                  
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);    
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            
            SimpanTrack("insert into "+table+" values("+value+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e); 
        }            
    }
    
    public boolean menyimpantf(String table,String value,String sama){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            ps.executeUpdate();
            if(ps != null){
                ps.close();
            }  
            
            SimpanTrack("insert into "+table+" values("+value+")");
            return true;           
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e); 
            JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada "+sama+" yang sama dimasukkan sebelumnya...!");
            return false;
        }            
    }
    
    public boolean menyimpantf2(String table,String value,String sama){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            ps.executeUpdate();
            if(ps != null){
                ps.close();
            }  
            
            SimpanTrack("insert into "+table+" values("+value+")");
            return true;           
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e); 
            return false;
        }            
    }
    
    public boolean menyimpantf(String table,String value,int i,String[] a,String acuan_field,String update,int j,String[] b){
        bool=false;
        try{ 
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            for(angka=1;angka<=i;angka++){
                ps.setString(angka,a[angka-1]);
            }            
            ps.executeUpdate();
            
            if(ps != null){
                ps.close();
            } 
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
            bool=true;
        }catch(Exception e){
            try {
                ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
                for(angka=1;angka<=j;angka++){
                    ps.setString(angka,b[angka-1]);
                } 
                ps.executeUpdate();   
                
                if(ps != null){
                    ps.close();
                } 
                
                if(AKTIFKANTRACKSQL.equals("yes")){
                    dicari="";
                    for(angka=1;angka<=i;angka++){
                        dicari=dicari+"|"+b[angka-1];
                    }
                }
                SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
                bool=true;
            } catch (Exception e2) {
                bool=false;
                System.out.println("Notifikasi : "+e2);
            }                         
        }
        return bool;
    }
    
    public void menyimpan(String table,String value,String sama,int i,String[] a){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
                
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);            
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada "+sama+" yang sama dimasukkan sebelumnya...!");
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+table+" "+e); 
        }            
    }
    
    public void menyimpan2(String table,String value,String sama,int i,String[] a){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);            
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);            
        }    
    }
    
    public void menyimpan3(String table,String value,String sama,int i,String[] a){
        try {
            ps=connect.prepareStatement("insert ignore into "+table+" values("+value+")");
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);            
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);            
        }    
    }
    
    public boolean menyimpantf(String table,String value,String sama,int i,String[] a){        
        try{             
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            for(angka=1;angka<=i;angka++){
                ps.setString(angka,a[angka-1]);
            }            
            ps.executeUpdate();
            
            if(ps != null){
                ps.close();
            }
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
            return true;
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);  
            if(e.toString().contains("Duplicate")){
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada "+sama+" yang sama dimasukkan sebelumnya...!");
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Ada kesalahan Query...!");
            }
            return false;
        }
    }
    
    public boolean menyimpantf2(String table,String value,String sama,int i,String[] a){
        bool=true;
        try{ 
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try {
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
                bool=true;
            } catch (Exception e) {
                bool=false;
                System.out.println("Notifikasi : "+e);  
            } finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        }catch(Exception e){
            bool=false;
            System.out.println("Notifikasi : "+e);  
        }
        return bool;
    }
    
    public boolean menyimpantf2(String table,String value,int i,String[] a){
        bool=true;
        try{ 
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try {
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
                bool=true;
            } catch (Exception e) {
                bool=false;
                System.out.println("Notifikasi : "+e);  
            } finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        }catch(Exception e){
            bool=false;
            System.out.println("Notifikasi : "+e);  
        }
        return bool;
    }
    
    public void menyimpan(String table,String value,int i,String[] a){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{                 
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);            
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);   
        }            
    }
    
    public void menyimpanignore(String table,String value,int i,String[] a){
        try {
            ps=connect.prepareStatement("insert ignore into "+table+" values("+value+")");
            try{                 
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e); ;            
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);   
        }            
    }
    
    public void menyimpan2(String table,String value,int i,String[] a){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{                 
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                }            
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi "+table+" : "+e);            
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        } catch (Exception e) { 
        }            
    }
    
    public void menyimpan(String table,String value,int i,String[] a,String acuan_field,String update,int j,String[] b){
        try{ 
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            for(angka=1;angka<=i;angka++){
                ps.setString(angka,a[angka-1]);
            }            
            ps.executeUpdate();
            
            if(ps != null){
                ps.close();
            } 
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        }catch(Exception e){
            try {
                ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
                for(angka=1;angka<=j;angka++){
                    ps.setString(angka,b[angka-1]);
                } 
                ps.executeUpdate();   
                
                if(ps != null){
                    ps.close();
                } 
                
                if(AKTIFKANTRACKSQL.equals("yes")){
                    dicari="";
                    for(angka=1;angka<=i;angka++){
                        dicari=dicari+"|"+b[angka-1];
                    }
                }
                SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
            } catch (Exception e2) {
                System.out.println("Notifikasi : "+e2);
            }                         
        }
    }
    
    public void menyimpan2(String table,String value,int i,String[] a,String acuan_field,String update,int j,String[] b){
        try{ 
            dicari="";
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            for(angka=1;angka<=i;angka++){
                dicari=dicari+", "+a[angka-1];
                ps.setString(angka,a[angka-1]);
            }            
            ps.executeUpdate();
            
            if(ps != null){
                  ps.close();
            } 
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        }catch(Exception e){
            try {
                ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
                for(angka=1;angka<=j;angka++){
                    ps.setString(angka,b[angka-1]);
                } 
                ps.executeUpdate(); 
                
                if(ps != null){
                    ps.close();
                } 
                
                if(AKTIFKANTRACKSQL.equals("yes")){
                    dicari="";
                    for(angka=1;angka<=i;angka++){
                        dicari=dicari+"|"+b[angka-1];
                    }
                }
                SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
            } catch (Exception e2) {                
                System.out.println("Notifikasi : "+e2);
            }            
        }
    }
    
    public void menyimpan3(String table,String value,int i,String[] a,String acuan_field,String update,int j,String[] b){
        try{ 
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            for(angka=1;angka<=i;angka++){
                ps.setString(angka,a[angka-1]);
            }            
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null,"Proses simpan berhasil..!!");
            if(ps != null){
                ps.close();
            } 
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("insert into "+table+" values("+dicari+")");
        }catch(Exception e){
            try {
                ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
                for(angka=1;angka<=j;angka++){
                    ps.setString(angka,b[angka-1]);
                } 
                ps.executeUpdate();   
                
                JOptionPane.showMessageDialog(null,"Proses simpan berhasil..!!");
                if(ps != null){
                    ps.close();
                } 
                if(AKTIFKANTRACKSQL.equals("yes")){
                    dicari="";
                    for(angka=1;angka<=i;angka++){
                        dicari=dicari+"|"+b[angka-1];
                    }
                }
                SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
            } catch (Exception e2) {
                System.out.println("Notifikasi : "+e2);
            }                         
        }
    }
    
    public void menyimpan(String table,String value){
        try {
            ps=connect.prepareStatement("insert into "+table+" values("+value+")");
            try{
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);         
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            SimpanTrack("insert into "+table+" values("+value+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);  
        }
    }
    
    public void menyimpanignore(String table,String value){
        try {
            ps=connect.prepareStatement("insert ignore into "+table+" values("+value+")");
            try{
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);         
            }finally{
                if(ps != null){
                    ps.close();
                }                
            }
            SimpanTrack("insert into "+table+" values("+value+")");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);  
        }
    }
    
    public void menyimpan(String table,String isisimpan,String isiedit,String acuan_field){
        try{            
            ps=connect.prepareStatement("insert into "+table+" values("+isisimpan+")");
            ps.executeUpdate();   
            if(ps != null){
                ps.close();
            }  
            SimpanTrack("insert into "+table+" values("+isisimpan+")");
        }catch(Exception e){
            try {
                ps=connect.prepareStatement("update "+table+" set "+isiedit+" where "+acuan_field);
                ps.executeUpdate();
                if(ps != null){
                    ps.close();
                }  
                SimpanTrack("update "+table+" set "+isiedit+" where "+acuan_field);
            } catch (Exception ex) {
                System.out.println("Notifikasi Edit : "+ex);
            }
        }
    }
    
    public boolean menyimpantf(String table,String isisimpan,String isiedit,String acuan_field){
        bool=true;
        try{            
            ps=connect.prepareStatement("insert into "+table+" values("+isisimpan+")");
            ps.executeUpdate();   
            if(ps != null){
                ps.close();
            }  
            SimpanTrack("insert into "+table+" values("+isisimpan+")");
            bool=true;
        }catch(Exception e){
            if(e.toString().toLowerCase().contains("duplicate")){
                try {
                    ps=connect.prepareStatement("update "+table+" set "+isiedit+" where "+acuan_field);
                    ps.executeUpdate();
                    if(ps != null){
                        ps.close();
                    }  
                    SimpanTrack("update "+table+" set "+isiedit+" where "+acuan_field);
                    bool=true;
                } catch (Exception ex) {
                    bool=false;
                    System.out.println("Notifikasi Edit : "+ex);
                }
            }else{
               bool=false; 
            }
        }
        return bool;
    }

    public void menyimpan(String table,String value,String sama,JTextField AlmGb){
        try {
            ps = connect.prepareStatement("insert into "+table+" values("+value+",?)");
            try{                        
                ps.setBinaryStream(1, new FileInputStream(AlmGb.getText()), new File(AlmGb.getText()).length());
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada "+sama+" yang sama dimasukkan sebelumnya...!");
            }finally{
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
    }
    
    public void menyimpan(String table,String value,String sama,JTextField AlmGb,JTextField AlmPhoto){
        try {
            ps = connect.prepareStatement("insert into "+table+" values("+value+",?,?)");
            try{                        
                ps.setBinaryStream(1, new FileInputStream(AlmGb.getText()), new File(AlmGb.getText()).length());
                ps.setBinaryStream(2, new FileInputStream(AlmPhoto.getText()), new File(AlmPhoto.getText()).length());
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada "+sama+" yang sama dimasukkan sebelumnya...!");
            }finally{
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    

    public void meghapus(String table,String field,String nilai_field) {
        try {
            ps=connect.prepareStatement("delete from "+table+" where "+field+"=?");
            try{       
                ps.setString(1,nilai_field);
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, data gagal dihapus. Kemungkinan data tersebut masih dipakai di table lain...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("delete from "+table+" where "+field+"='"+nilai_field+"'");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public boolean meghapustf(String table,String field,String nilai_field) {
        bool=true;
        try {
            ps=connect.prepareStatement("delete from "+table+" where "+field+"=?");
            try{       
                ps.setString(1,nilai_field);
                ps.executeUpdate();
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, data gagal dihapus. Kemungkinan data tersebut masih dipakai di table lain...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("delete from "+table+" where "+field+"='"+nilai_field+"'");
        } catch (Exception e) {
            bool=false;
            System.out.println("Notifikasi : "+e);
        }
        return bool;
    }
    
    public void meghapus(String table,String field,String field2,String nilai_field,String nilai_field2) {
        try {
            ps=connect.prepareStatement("delete from "+table+" where "+field+"=? and "+field2+"=?");
            try{       
                ps.setString(1,nilai_field);
                ps.setString(2,nilai_field2);
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, data gagal dihapus. Kemungkinan data tersebut masih dipakai di table lain...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("delete from "+table+" where "+field+"='"+nilai_field+"' and "+field2+"='"+nilai_field2+"'");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void meghapus2(String table,String field,String nilai_field) {
        try {
            ps=connect.prepareStatement("delete from "+table+" where "+field+"=?");
            try{       
                ps.setString(1,nilai_field);
                ps.executeUpdate(); 
                JOptionPane.showMessageDialog(null,"Proses hapus berhasil...!!!!");
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, data gagal dihapus. Kemungkinan data tersebut masih dipakai di table lain...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("delete from "+table+" where "+field+"='"+nilai_field+"'");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void meghapus3(String table,String field,String nilai_field) {
        try {
            ps=connect.prepareStatement("delete from "+table+" where "+field+"=?");
            try{       
                ps.setString(1,nilai_field);
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("delete from "+table+" where "+field+"='"+nilai_field+"'");
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void mengedit(String table,String acuan_field,String update){
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{                        
                ps.executeUpdate();       
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Gagal Mengedit. Mungkin kode sudah digunakan sebelumnya...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("update "+table+" set "+update+" where "+acuan_field);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public boolean mengedittf(String table,String acuan_field,String update){
        bool=true;
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{                        
                ps.executeUpdate();  
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Gagal Mengedit. Mungkin kode sudah digunakan sebelumnya...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack("update "+table+" set "+update+" where "+acuan_field);
        } catch (Exception e) {
            bool=false;
            System.out.println("Notifikasi : "+e);
        }
        return bool;
    }
    
    public void mengedit(String table,String acuan_field,String update,int i,String[] a){
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate();       
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Gagal Mengedit. Periksa kembali data...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }    
    }
    
    public void mengedit2(String table,String acuan_field,String update,int i,String[] a){
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate();   
                JOptionPane.showMessageDialog(null,"Proses edit berhasil...!!!!");
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Periksa kembali data...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }    
    }
    
    public void mengedit3(String table,String acuan_field,String update,int i,String[] a){
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate();       
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }    
    }
    
    public boolean mengedittf(String table,String acuan_field,String update,int i,String[] a){
        bool=true;
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate();       
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Gagal Mengedit. Periksa kembali data...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
        } catch (Exception e) {
            bool=false;
            System.out.println("Notifikasi : "+e);
        }   
        return bool;
    }
    
    public boolean mengedittf2(String table,String acuan_field,String update,int i,String[] a){
        bool=true;
        try {
            ps=connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate();       
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack("update "+table+" set "+update+" "+dicari+" where "+acuan_field);
        } catch (Exception e) {
            bool=false;
            System.out.println("Notifikasi : "+e);
        }   
        return bool;
    }
    
    public void mengedit(String table,String acuan_field,String update,JTextField AlmGb){
        try {
            ps = connect.prepareStatement("update "+table+" set "+update+" where "+acuan_field);
            try{            
                ps.setBinaryStream(1, new FileInputStream(AlmGb.getText()), new File(AlmGb.getText()).length());
                ps.executeUpdate();
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Pilih dulu data yang mau anda edit...\n Klik data pada table untuk memilih...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void query(String qry){
        try {
            ps=connect.prepareStatement(qry);
            try{
                ps.executeQuery();
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack(qry);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }            
    }
    
    public void queryUpdate(String query, String... values) {
        String track = query;
        
        try {
            ps = connect.prepareStatement(query);
            
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
                track = track.replaceFirst("\\?", "'"+values[i]+"'");
            }
            
            ps.executeUpdate();
            
            ps.close();
            
            SimpanTrack(track);
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses data...!!!");
        }
    }
    
    public void queryUpdate(String query) {
        try {
            ps = connect.prepareStatement(query);
            
            ps.executeUpdate();
            
            ps.close();
            
            SimpanTrack(query);
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses data...!!!");
        }
    }

    public void queryu(String qry){
        try {
            ps=connect.prepareStatement(qry);
            try{                            
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            
            SimpanTrack(qry);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public boolean queryutf(String qry){
        bool=false;
        try {
            ps=connect.prepareStatement(qry);
            try{                            
                ps.executeUpdate(); 
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");                
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack(qry);
        } catch (Exception e) {
            bool=false;
            System.out.println("Notifikasi : "+e);
        }
        return bool;
    }
    
    public boolean queryutf2(String qry){
        bool=false;
        try {
            ps=connect.prepareStatement(qry);
            try{                            
                ps.executeUpdate(); 
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);           
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack(qry);
        } catch (Exception e) {
            bool=false;
            System.out.println("Notifikasi : "+e);
        }
        return bool;
    }
    
    public void queryu(String qry,String parameter){
        String track = qry;
        track = track.replaceFirst("\\?", parameter);
        try {
            ps=connect.prepareStatement(qry);
            try{
                ps.setString(1,parameter);
                ps.executeUpdate();
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack(track);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }    
    }
    
    
    public void queryu2(String qry){
        try {
            ps=connect.prepareStatement(qry);
            try{                            
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            SimpanTrack(qry);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void queryu2(String qry,int i,String[] a){
        try {
            try{            
                ps=connect.prepareStatement(qry);
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack(qry+" "+dicari);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public boolean queryu2tf(String qry,int i,String[] a){
        bool=false;
        try {
            try{            
                ps=connect.prepareStatement(qry);
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate(); 
                bool=true;
             }catch(Exception e){
                bool=false;
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack(qry+" "+dicari);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return bool;
    }
    
    public void queryu3(String qry,int i,String[] a){
        try {
            try{            
                ps=connect.prepareStatement(qry);
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate(); 
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack(qry+" "+dicari);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void queryu4(String qry,int i,String[] a){
        try {
            try{            
                ps=connect.prepareStatement(qry);
                for(angka=1;angka<=i;angka++){
                    ps.setString(angka,a[angka-1]);
                } 
                ps.executeUpdate(); 
             }catch(Exception e){
             }finally{
                if(ps != null){
                    ps.close();
                }
            }
            if(AKTIFKANTRACKSQL.equals("yes")){
                dicari="";
                for(angka=1;angka<=i;angka++){
                    dicari=dicari+"|"+a[angka-1];
                }
            }
            SimpanTrack(qry+" "+dicari);
        } catch (Exception e) {
        }
    }
    
    public void AutoComitFalse(){
        try {
            connect.setAutoCommit(false);
        } catch (Exception e) {
        }
    }
    
    public void AutoComitTrue(){
        try {
            connect.setAutoCommit(true);
        } catch (Exception e) {
        }
    }
    
    public void Commit(){
        try {
            connect.commit();
        } catch (Exception e) {
        }
    }
     
    public void RollBack(){
        try {
            connect.rollback();
        } catch (Exception e) {
            System.out.println("Notif : "+e);
            JOptionPane.showMessageDialog(null,"Gagal melakukan rollback..!");
        }
    }
    
    public void cariIsi(String sql,JComboBox cmb){
        try {
            ps=connect.prepareStatement(sql);
            try{  
                rs=ps.executeQuery();
                if(rs.next()){
                    String dicari=rs.getString(1);
                    cmb.setSelectedItem(dicari);
                }else{
                    cmb.setSelectedItem("");
                }    
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }   
    }

    public void cariIsi(String sql,JDateTimePicker dtp){
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();
                if(rs.next()){
                    try {
                        dtp.setDisplayFormat("yyyy-MM-dd");
                        dtp.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString(1)));
                        dtp.setDisplayFormat("dd-MM-yyyy");
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }       
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void cariIsi(String sql,JTextField txt){
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();
                if(rs.next()){
                    txt.setText(rs.getString(1));
                }else{
                    txt.setText("");
                }  
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public int cariRegistrasi(String norawat){
        angka=0;
        angka=cariInteger("select count(billing.no_rawat) from billing where billing.no_rawat=?",norawat)+
              cariInteger("select count(reg_periksa.no_rawat) from reg_periksa where reg_periksa.no_rawat=? and reg_periksa.stts='Batal'",norawat);
        return angka;
    }
    
    public boolean cekTanggalRegistrasi(String tanggalregistrasi,String tanggalinputdata){
        if (! sekuel.pemberlakuanBatasEdit) {
            return true;
        }
        bool=false;
        try {
            waktumulai = formattanggal.parse(tanggalregistrasi);
            kegiatan = formattanggal.parse(tanggalinputdata);
            bedawaktu = (kegiatan.getTime()-waktumulai.getTime())/1000;
            if(bedawaktu<0){
                bool=false;
                JOptionPane.showMessageDialog(null,"Maaf, jam input data / perubahan data minimal di jam "+tanggalregistrasi+" !");
            }else{
                bool=true;
            }
        } catch (Exception ex) {
            bool=false;
            System.out.println("Notif : "+ex);
        }
        return bool;
    }
    
    public boolean cekTanggal48jam(String tanggalmulai,String tanggalinputdata){
        if (! sekuel.pemberlakuanBatasEdit || akses.getakses_edit_sementara()) {
            return true;
        }
        bool=false;
        try {
            waktumulai = formattanggal.parse(tanggalmulai);
            kegiatan = formattanggal.parse(tanggalinputdata);
            bedawaktu = (kegiatan.getTime()-waktumulai.getTime())/1000;
            if(bedawaktu>172800){
                bool=false;
                JOptionPane.showMessageDialog(null,"Maaf, perubahan data / penghapusan data tidak boleh lebih dari 2 x 24 jam !");
            }else{
                bool=true;
            }
        } catch (Exception ex) {
            bool=false;
            System.out.println("Notif : "+ex);
        }
        return bool;
    }
    
    public String ambiltanggalsekarang(){
        return formattanggal.format(new Date());
    }
    
    public void cariIsi(String sql,JTextField txt,String kunci){
        try {
            ps=connect.prepareStatement(sql);
            try{
                ps.setString(1,kunci);
                rs=ps.executeQuery();
                if(rs.next()){
                    txt.setText(rs.getString(1));
                }else{
                    txt.setText("");
                }   
            }catch(SQLException e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    } 
    
    public void cariIsi(String sql,JTextArea txt,String kunci){
        try {
            ps=connect.prepareStatement(sql);
            try{
                ps.setString(1,kunci);
                rs=ps.executeQuery();
                if(rs.next()){
                    txt.setText(rs.getString(1));
                }else{
                    txt.setText("");
                }   
            }catch(SQLException e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    

    public void cariIsi(String sql,JLabel txt){
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();
                if(rs.next()){
                    txt.setText(rs.getString(1));
                }else{
                    txt.setText("");
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public String cariIsi(String sql){
        dicari="";
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();            
                if(rs.next()){
                    dicari=rs.getString(1);
                }else{
                    dicari="";
                }   
            }catch(Exception e){
                dicari="";
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        
        return dicari;
    }
    
    public ByteArrayInputStream cariGambar(String sql){
        ByteArrayInputStream inputStream=null;
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();            
                if(rs.next()){                
                    inputStream = new ByteArrayInputStream(rs.getBytes(1));
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return inputStream;
    }
    
    public String cariIsi(String sql,String data){
        dicari="";
        try {
            ps=connect.prepareStatement(sql);
            try{                            
                ps.setString(1,data);
                rs=ps.executeQuery();            
                if(rs.next()){
                    dicari=rs.getString(1);
                }else{
                    dicari="";
                }   
            }catch(Exception e){
                dicari="";
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null ){
                    rs.close();
                }

                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return dicari;
    }
    
    public Date cariIsi2(String sql){
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();            
                if(rs.next()){
                    tanggal=rs.getDate(1);
                }else{
                    tanggal=new Date();
                }   
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return tanggal;
    }

    public Integer cariInteger(String sql){
        angka=0;
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();            
                if(rs.next()){
                    angka=rs.getInt(1);
                }else{
                    angka=0;
                } 
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka;
    }
    
    public Integer cariIntegerCount(String sql){
        angka=0;
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();            
                while(rs.next()){
                    angka=angka+rs.getInt(1);
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka;
    }
    
    public Integer cariInteger(String sql,String data){
        angka=0;
        try {
            ps=connect.prepareStatement(sql);
            try{
                ps.setString(1,data);
                rs=ps.executeQuery();            
                if(rs.next()){
                    angka=rs.getInt(1);
                }else{
                    angka=0;
                }  
            }catch(Exception e){
                angka=0;
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka;
    }
    
    public Integer cariInteger(String sql,String data,String data2){
        angka=0;
        try {
            ps=connect.prepareStatement(sql);
            try{
                ps.setString(1,data);
                ps.setString(2,data2);
                rs=ps.executeQuery();            
                if(rs.next()){
                    angka=rs.getInt(1);
                }else{
                    angka=0;
                }  
            }catch(Exception e){
                angka=0;
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka;
    }
    
    public Integer cariInteger(String sql,String data,String data2,String data3){
        angka=0;
        try {
            ps=connect.prepareStatement(sql);
            try{
                ps.setString(1,data);
                ps.setString(2,data2);
                ps.setString(3,data3);
                rs=ps.executeQuery();            
                if(rs.next()){
                    angka=rs.getInt(1);
                }else{
                    angka=0;
                }  
            }catch(Exception e){
                angka=0;
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka;
    }
    
    public Integer cariInteger2(String sql){
        angka=0;
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();            
                rs.last();
                angka=rs.getRow();
                if(angka<1){
                    angka=0;
                }   
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka;
    }

    public void cariIsiAngka(String sql,JTextField txt){
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();
                if(rs.next()){
                    txt.setText(df2.format(rs.getDouble(1)));
                }else{
                    txt.setText("0");
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void cariIsiAngka(String sql,JLabel txt) {
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();
                if(rs.next()){
                    txt.setText(df2.format(rs.getDouble(1)));
                }else{
                    txt.setText("0");
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }            
    }
    
    public double cariIsiAngka(String sql) {
        angka2=0;
        try {
            ps=connect.prepareStatement(sql);
            try{            
                rs=ps.executeQuery();
                if(rs.next()){
                    angka2=rs.getDouble(1);
                }else{
                    angka2=0;
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka2;
    }
    
    public double cariIsiAngka(String sql,String data) {
        angka2=0;
        try {
            ps=connect.prepareStatement(sql);
            try{            
                ps.setString(1,data);
                rs=ps.executeQuery();
                if(rs.next()){
                    angka2=rs.getDouble(1);
                }else{
                    angka2=0;
                }
                //rs.close();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka2;
    }
    
    public double cariIsiAngka2(String sql,String data,String data2) {
        angka2=0;
        try {
            ps=connect.prepareStatement(sql);
            try{            
                ps.setString(1,data);
                ps.setString(2,data2);
                rs=ps.executeQuery();
                if(rs.next()){
                    angka2=rs.getDouble(1);
                }else{
                    angka2=0;
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return angka2;
    }

    public void cariGambar(String sql,JLabel txt){        
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();
                if(rs.next()){
                    icon = new javax.swing.ImageIcon(rs.getBlob(1).getBytes(1L, (int) rs.getBlob(1).length()));
                    createThumbnail();
                    txt.setIcon(icon);
                }else{
                    txt.setText(null);
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void cariGambar(String sql,java.awt.Canvas txt,String text){
        try {
            ps=connect.prepareStatement(sql);
            try {
                rs = ps.executeQuery();
                for (int I = 0; rs.next(); I++) {
                    ((Painter) txt).setImage(gambar(text));
                    Blob blob = rs.getBlob(5);
                    ((Painter) txt).setImageIcon(new javax.swing.ImageIcon(
                        blob.getBytes(1, (int) (blob.length()))));
                }  
            } catch (Exception ex) {
                cetak(ex.toString());
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
    }
    
    private void SimpanTrack(String sql){
        if(AKTIFKANTRACKSQL.equals("yes")){
            try {
                ps=connect.prepareStatement("insert into trackersql values(now(),?,?)");
                try{       
                    ps.setString(1,akses.getalamatip()+" "+sql);
                    ps.setString(2,akses.getkode());
                    ps.executeUpdate(); 
                 }catch(Exception e){
                    System.out.println("Notifikasi : "+e);
                 }finally{
                    if(ps != null){
                        ps.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
    }
    
    public String cariString(String sql){
        dicari="";
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();            
                if(rs.next()){
                    dicari=rs.getString(1);
                }else{
                    dicari="";
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return dicari;
    }

    private String gambar(String id) {
        return folder + File.separator + id.trim() + ".jpg";
    }
    
    public void Tabel(javax.swing.JTable tb,int lebar[]){
      tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      angka=tb.getColumnCount();
      for(int i=0;i < angka;i++){
          javax.swing.table.TableColumn tbc = tb.getColumnModel().getColumn(i);
          tbc.setPreferredWidth(lebar[i]);
          //tb.setRowHeight(17);
      }
  }

    private void createThumbnail() {
        int maxDim = 150;
        try {
            Image inImage = icon.getImage();

            double scale = (double) maxDim / (double) inImage.getHeight(null);
            if (inImage.getWidth(null) > inImage.getHeight(null)) {
                scale = (double) maxDim / (double) inImage.getWidth(null);
            }

            int scaledW = (int) (scale * inImage.getWidth(null));
            int scaledH = (int) (scale * inImage.getHeight(null));

            BufferedImage outImage = new BufferedImage(scaledW, scaledH,
            BufferedImage.TYPE_INT_RGB);

            AffineTransform tx = new AffineTransform();

            if (scale < 1.0d) {
                tx.scale(scale, scale);
            }

            Graphics2D g2d = outImage.createGraphics();
            g2d.drawImage(inImage, tx, null);
            g2d.dispose();

            new javax.swing.ImageIcon(outImage);
        } catch (Exception e) {
        }
    }

    private void cetak(String str) {
        System.out.println(str);
    }

    public class Painter extends Canvas {

        Image image;

        private void setImage(String file) {
            URL url = null;
            try {
                url = new File(file).toURI().toURL();
            } catch (MalformedURLException ex) {
                cetak(ex.toString());
            }
            image = getToolkit().getImage(url);
            repaint();
        }
        private void setImageIcon(ImageIcon file) {
            image = file.getImage();
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            double d = image.getHeight(this) / this.getHeight();
            double w = image.getWidth(this) / d;
            double x = this.getWidth() / 2 - w / 2;
            g.drawImage(image, (int) x, 0, (int) (w), this.getHeight(), this);
        }

        private void cetak(String str) {
            System.out.println(str);
        }
    }

   public class NIOCopier {
        public NIOCopier(String asal, String tujuan) throws IOException {
            FileOutputStream outFile;
            try (FileInputStream inFile = new FileInputStream(asal)) {
                outFile = new FileOutputStream(tujuan);
                FileChannel outChannel;
                try (FileChannel inChannel = inFile.getChannel()) {
                    outChannel = outFile.getChannel();
                    for (ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                            inChannel.read(buffer) != -1;
                            buffer.clear()) {
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            outChannel.write(buffer);
                        }
                    }
                }
            outChannel.close();
            }
            outFile.close();
        }
    }

}