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

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Owner
 */
public final class sekuel {
    private final Connection connect = koneksiDB.condb();
    private final validasi Valid = new validasi();
    private final boolean AKTIFKANTRACKSQL = koneksiDB.AKTIFKANTRACKSQL();
    private String track = "";

    public sekuel() {
        super();
    }

    public void logTaskid(String norawat, String kodebooking, String jenisPasien, String taskid, String request, String code, String message, String response, String wakturs) {
        try (PreparedStatement ps = connect.prepareStatement(
            "insert into referensi_mobilejkn_bpjs_taskid_response2 " +
            "(no_rawat, kodebooking, jenispasien, taskid, request, code, message, response, waktu, waktu_rs) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, now(), ?)"
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

    public ArrayList<String> cariArraySmc(String sql, String... values) {
        ArrayList<String> list = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return list;
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
            ps.executeUpdate();
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("insert")));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            Valid.popupInfoDialog("Gagal menyimpan data!");
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
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("insert")));
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
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
            ps.executeUpdate();
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("update")));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            Valid.popupInfoDialog("Gagal mengupdate data!");
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
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("update")));
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
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
            ps.executeUpdate();
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("delete")));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.getMessage().contains("constraint")) {
                Valid.popupInfoDialog("Gagal menghapus data, kemungkinan masih digunakan di bagian lainnya!");
            } else {
                Valid.popupInfoDialog("Gagal menghapus data!");
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
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf("delete")));
            if (ps.executeUpdate() > 0) {
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
            track = ps.toString();
            SimpanTrack(track.substring(track.indexOf(sql.substring(0, 8))));
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return false;
    }

    private void SimpanTrack(String sql) {
        if (!AKTIFKANTRACKSQL) {
            return;
        }

        try (PreparedStatement ps = connect.prepareStatement("insert into trackersql values(now(), ?, ?)")) {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ps.setString(1, inetAddress.getHostAddress() + " " + sql);
            ps.setString(2, "APM");
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
}
