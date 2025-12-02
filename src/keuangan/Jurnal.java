/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keuangan;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author khanzamedia
 */
public class Jurnal {
    private final sekuel Sequel = new sekuel();
    private final Connection koneksi = koneksiDB.condb();

    public synchronized boolean simpanJurnal(String nobukti, String jenis, String keterangan) {
        boolean sukses = true;
        try {
            try (PreparedStatement pscek = koneksi.prepareStatement(
                "select count(*) as jml, current_date() as tanggal, current_time() as jam, round(sum(debet) - sum(kredit), 0) as selisih, " +
                "round(sum(debet), 0) as total_debet, round(sum(kredit), 0) as total_kredit " +
                "from tampjurnal_smc where user_id = ? and ip = ?"
            )) {
                pscek.setString(1, akses.getkode());
                pscek.setString(2, akses.getalamatip());
                try (ResultSet rscek = pscek.executeQuery()) {
                    if (rscek.getInt("jml") > 0) {
                        if (rscek.getInt("selisih") == 0) {
                            String nojur = "";
                            int i = 0;
                            do {
                                nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                                sukses = Sequel.menyimpantfSmc("jurnal", "", nojur, nobukti, rscek.getString("tanggal"), rscek.getString("jam"), jenis, keterangan);
                            } while (!sukses && i++ < 3);

                            if (sukses && !nojur.isBlank()) {
                                sukses = Sequel.executeRawSmc("insert into detailjurnal select ? as no_jurnal, tampjurnal_smc.kd_rek, tampjurnal_smc.debet, " +
                                    "tampjurnal_smc.kredit from tampjurnal_smc where user_id = ? and ip = ?", nojur, akses.getkode(), akses.getalamatip()
                                );
                                Sequel.deleteTampJurnal();
                            }
                        } else {
                            System.out.println("Notif : Debet dan Kredit tidak sama!");
                            System.out.println("Total Debet  : " + rscek.getBigDecimal("total_debet").toPlainString());
                            System.out.println("Total Kredit : " + rscek.getBigDecimal("total_kredit").toPlainString());
                            sukses = false;
                        }
                    } else {
                        System.out.println("Notif : Tidak ada transaksi yang bisa dijurnal!");
                    }
                }
            }
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
        }

        return sukses;
    }

    public synchronized boolean simpanJurnalRVPBPJS(String nobukti, String jenis, String keterangan) {
        boolean sukses = true;
        try {
            try (ResultSet rscek = koneksi.createStatement().executeQuery(
                "select count(*) as jml, current_date() as tanggal, current_time() as jam, round(sum(debet) - sum(kredit), 0) " +
                "as selisih, round(sum(debet), 0) as total_debet, round(sum(kredit), 0) as total_kredit from tampjurnal_smc"
            )) {
                if (rscek.next()) {
                    if (rscek.getInt("jml") > 0) {
                        if (rscek.getInt("selisih") == 0) {
                            String nojur = "";
                            int i = 0;
                            do {
                                nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                                sukses = Sequel.menyimpantfSmc("jurnal", "", nojur, nobukti, rscek.getString("tanggal"), rscek.getString("jam"), jenis, keterangan);
                            } while (!sukses && i++ < 3);

                            if (sukses && !nojur.isBlank()) {
                                sukses = Sequel.executeRawSmc("insert into detailjurnal select ? as no_jurnal, tampjurnal_rvpbpjs.kd_rek, " +
                                    "tampjurnal_rvpbpjs.debet, tampjurnal_rvpbpjs.kredit from tampjurnal_rvpbpjs", nojur);
                                Sequel.menghapustfSmc("tampjurnal_rvpbpjs");
                            }
                        } else {
                            System.out.println("Notif : Debet dan Kredit tidak sama!");
                            System.out.println("Total Debet  : " + rscek.getBigDecimal("total_debet").toPlainString());
                            System.out.println("Total Kredit : " + rscek.getBigDecimal("total_kredit").toPlainString());
                            sukses = false;
                        }
                    } else {
                        sukses = false;
                        System.out.println("Notif : Tidak ada transaksi yang bisa dijurnal!");
                    }
                }
            }
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
        }

        return sukses;
    }
}
