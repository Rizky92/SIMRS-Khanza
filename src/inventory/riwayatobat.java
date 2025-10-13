/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package inventory;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author khanzamedia
 */
public class riwayatobat {
    private final Connection koneksi = koneksiDB.condb();

    public synchronized boolean catatRiwayat(
        String kodebarang,
        double masuk,
        double keluar,
        String posisi,
        String petugas,
        String kdbangsal,
        String status,
        String nobatch,
        String nofaktur,
        String keterangan
    ) {
        try {
            double stokakhir = 0;
            double stokawal = 0;
            try (PreparedStatement ps = koneksi.prepareStatement("select g.stok from gudangbarang g where g.kode_brng = ? and g.kd_bangsal = ? and g.no_batch = ? and g.no_faktur = ?")) {
                ps.setString(1, kodebarang);
                ps.setString(2, kdbangsal);
                ps.setString(3, nobatch);
                ps.setString(4, nofaktur);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        stokawal = rs.getDouble("stok");
                        stokakhir = stokawal + masuk - keluar;
                    } else {
                        stokawal = 0;
                        stokakhir = stokawal + masuk - keluar;
                    }
                }
            }
            
            try (PreparedStatement ps = koneksi.prepareStatement("insert into riwayat_barang_medis values(?, ?, ?, ?, ?, ?, current_date(), current_time(), ?, ?, ?, ?, ?, ?)")) {
                int p = 0;
                ps.setString(++p, kodebarang);
                ps.setDouble(++p, stokawal);
                ps.setDouble(++p, masuk);
                if (posisi.equals("Opname")) {
                    ps.setDouble(++p, 0);
                    ps.setDouble(++p, masuk);
                } else {
                    ps.setDouble(++p, keluar);
                    ps.setDouble(++p, stokakhir);
                }
                ps.setString(++p, posisi);
                ps.setString(++p, petugas);
                ps.setString(++p, kdbangsal);
                ps.setString(++p, status);
                ps.setString(++p, nobatch);
                ps.setString(++p, nofaktur);
                ps.setString(++p, keterangan);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
        }
        return false;
    }
}
