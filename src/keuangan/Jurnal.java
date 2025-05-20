/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keuangan;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author khanzamedia
 */
public class Jurnal {
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private ResultSet rs, rscek;
    private PreparedStatement ps2, ps, pscek;
    private String nojur = "";
    private boolean sukses = true;
    
    public boolean simpanJurnalRVPBPJS(String nobukti, String jenis, String keterangan) {
        try (ResultSet rscek = koneksi.createStatement().executeQuery(
            "select count(*) as jml, current_date() as tanggal, current_time() as jam, round(sum(debet) - sum(kredit), 0) as selisih from tampjurnal_rvpbpjs"
        )) {
            if (rscek.next()) {
                if (rscek.getInt("jml") > 0) {
                    if (rscek.getInt("selisih") == 0) {
                        nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                        sukses = true;
                        try (PreparedStatement ps = koneksi.prepareStatement("insert into jurnal values(?, ?, ?, ?, ?, ?)")) {
                            ps.setString(1, nojur);
                            ps.setString(2, nobukti);
                            ps.setString(3, rscek.getString("tanggal"));
                            ps.setString(4, rscek.getString("jam"));
                            ps.setString(5, jenis);
                            ps.setString(6, keterangan);
                            ps.executeUpdate();
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            sukses = false;
                        }
                        
                        if (!sukses) {
                            nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                            sukses = true;
                            try (PreparedStatement ps = koneksi.prepareStatement("insert into jurnal values(?, ?, ?, ?, ?, ?)")) {
                                ps.setString(1, nojur);
                                ps.setString(2, nobukti);
                                ps.setString(3, rscek.getString("tanggal"));
                                ps.setString(4, rscek.getString("jam"));
                                ps.setString(5, jenis);
                                ps.setString(6, keterangan);
                                ps.executeUpdate();
                            } catch (Exception e) {
                                System.out.println("Notif : " + e);
                                sukses = false;
                            }
                        }
                        
                        if (!sukses) {
                            nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"), 2);
                            sukses = true;
                            try (PreparedStatement ps = koneksi.prepareStatement("insert into jurnal values(?, ?, ?, ?, ?, ?)")) {
                                ps.setString(1, nojur);
                                ps.setString(2, nobukti);
                                ps.setString(3, rscek.getString("tanggal"));
                                ps.setString(4, rscek.getString("jam"));
                                ps.setString(5, jenis);
                                ps.setString(6, keterangan);
                                ps.executeUpdate();
                            } catch (Exception e) {
                                System.out.println("Notif : " + e);
                                sukses = false;
                            }
                        }
                        
                        if (sukses) {
                            sukses = Sequel.executeRawSmc(
                                "insert into detailjurnal select ? as no_jurnal, tampjurnal_rvpbpjs.kd_rek, " +
                                "tampjurnal_rvpbpjs.debet, tampjurnal_rvpbpjs.kredit from tampjurnal_rvpbpjs",
                                nojur);
                            Sequel.queryu2("delete from tampjurnal_rvpbpjs");
                        }
                    } else {
                        System.out.println("Notif : Debet dan Kredit tidak sama!");
                        sukses = false;
                    }
                } else {
                    System.out.println("Notif : Tidak ada transaksi yang bisa dijurnal!");
                    sukses = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        
        return sukses;
    }

    public boolean simpanJurnal(String nobukti, String jenis, String keterangan) {
        try {
            pscek = koneksi.prepareStatement(
                "select count(*) as jml, current_date() as tanggal, current_time() as jam, round(sum(debet) - sum(kredit), 0) as selisih, " +
                "round(sum(debet), 0) as total_debet, round(sum(kredit), 0) as total_kredit " +
                "from tampjurnal_smc where user_id = ? and ip = ?"
            );
            try {
                pscek.setString(1, akses.getkode());
                pscek.setString(2, akses.getalamatip());
                rscek = pscek.executeQuery();
                if (rscek.next()) {
                    if (rscek.getInt("jml") > 0) {
                        if (rscek.getInt("selisih") == 0) {
                            nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                            try {
                                sukses = true;
                                ps = koneksi.prepareStatement("insert into jurnal values(?, ?, ?, ?, ?, ?)");
                                try {
                                    ps.setString(1, nojur);
                                    ps.setString(2, nobukti);
                                    ps.setString(3, rscek.getString("tanggal"));
                                    ps.setString(4, rscek.getString("jam"));
                                    ps.setString(5, jenis);
                                    ps.setString(6, keterangan);
                                    ps.executeUpdate();
                                } catch (Exception e) {
                                    sukses = false;
                                    System.out.println("Notifikasi : " + e);
                                } finally {
                                    if (ps != null) {
                                        ps.close();
                                    }
                                }

                                if (sukses == false) {
                                    nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                                    sukses = true;
                                    ps = koneksi.prepareStatement("insert into jurnal values(?, ?, ?, ?, ?, ?)");
                                    try {
                                        ps.setString(1, nojur);
                                        ps.setString(2, nobukti);
                                        ps.setString(3, rscek.getString("tanggal"));
                                        ps.setString(4, rscek.getString("jam"));
                                        ps.setString(5, jenis);
                                        ps.setString(6, keterangan);
                                        ps.executeUpdate();
                                    } catch (Exception e) {
                                        sukses = false;
                                        System.out.println("Notifikasi : " + e);
                                    } finally {
                                        if (ps != null) {
                                            ps.close();
                                        }
                                    }
                                }

                                if (sukses == true) {
                                    sukses = Sequel.executeRawSmc(
                                        "insert into detailjurnal " +
                                        "select ? as no_jurnal, tampjurnal_smc.kd_rek, tampjurnal_smc.debet, tampjurnal_smc.kredit " +
                                        "from tampjurnal_smc where user_id = ? and ip = ?",
                                        nojur, akses.getkode(), akses.getalamatip()
                                    );
                                    Sequel.deleteTampJurnal();
                                }
                            } catch (Exception ex) {
                                sukses = false;
                                System.out.println("Notifikasi : " + ex);
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
            } catch (Exception e) {
                sukses = false;
                System.out.println("Notif : " + e);
            } finally {
                if (rscek != null) {
                    rscek.close();
                }
                if (pscek != null) {
                    pscek.close();
                }
            }
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
        }
        return sukses;
    }
}
