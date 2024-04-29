/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keuangan;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
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
    private String nojur = "", track = "";
    private boolean sukses = true;
    
    public boolean simpanJurnalRVPBPJS(String nobukti, String jenis, String keterangan) {
        try {
            pscek = koneksi.prepareStatement(
                "select count(*) as jml, current_date() as tanggal, current_time() as jam, " +
                "round(sum(debet) - sum(kredit), 0) as selisih " +
                "from tampjurnal_rvpbpjs"
            );
            try {
                rscek = pscek.executeQuery();
                if (rscek.next()) {
                    if (rscek.getInt("jml") > 0) {
                        if (rscek.getInt("selisih") == 0) {
                            nojur = Sequel.autoNomorSmc("JR", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                            try {
                                sukses = true;
                                ps = koneksi.prepareStatement("insert into jurnal values (?, ?, ?, ?, ?, ?)");
                                track = "insert into jurnal values ('" + nojur + "', '" + nobukti + "', '" + rscek.getString("tanggal") + "', '" + rscek.getString("jam") + "', '" + jenis + "', '" + keterangan + "')";
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
                                    sukses = true;
                                    nojur = Sequel.autoNomorSmc("JR", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                                    ps = koneksi.prepareStatement("insert into jurnal values(?, ?, ?, ?, ?, ?)");
                                    track = "insert into jurnal values ('" + nojur + "', '" + nobukti + "', '" + rscek.getString("tanggal") + "', '" + rscek.getString("jam") + "', '" + jenis + "', '" + keterangan + "')";
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
                                    Sequel.simpanTrackerSQL(track);
                                    try (ResultSet rsdetail = koneksi.prepareStatement("select * from tampjurnal_rvpbpjs").executeQuery()) {
                                        while (rsdetail.next()) {
                                            try (PreparedStatement psinsert = koneksi.prepareStatement("insert into detailjurnal values (?, ?, ?, ?)")) {
                                                track = "insert into detailjurnal values ('" + nojur + "', '" + rsdetail.getString(1) + "', " + rsdetail.getString(3) + ", " + rsdetail.getString(4) + ")";
                                                psinsert.setString(1, nojur);
                                                psinsert.setString(2, rsdetail.getString(1));
                                                psinsert.setString(3, rsdetail.getString(3));
                                                psinsert.setString(4, rsdetail.getString(4));
                                                psinsert.executeUpdate();
                                                Sequel.simpanTrackerSQL(track);
                                            } catch (Exception e) {
                                                sukses = false;
                                                System.out.println("Notif : " + e);
                                            }
                                        }
                                    } catch (Exception e) {
                                        sukses = false;
                                        System.out.println("Notif : " + e);
                                    }
                                    Sequel.queryu2("delete from tampjurnal_rvpbpjs");
                                }
                            } catch (Exception ex) {
                                sukses = false;
                                System.out.println("Notifikasi : " + ex);
                            }
                        } else {
                            sukses = false;
                            System.out.println("Notif : Debet dan Kredit tidak sama");
                        }
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

    public boolean simpanJurnal(String nobukti, String jenis, String keterangan) {
        try {
            pscek = koneksi.prepareStatement(
                "select count(*) as jml, current_date() as tanggal, current_time() as jam, " +
                "round(sum(debet) - sum(kredit), 0) as selisih " +
                "from tampjurnal_smc where user_id = ? and ip = ?"
            );
            try {
                pscek.setString(1, akses.getkode());
                pscek.setString(2, akses.getalamatip());
                rscek = pscek.executeQuery();
                if (rscek.next()) {
                    if (rscek.getInt("jml") > 0) {
                        if (rscek.getInt("selisih") == 0) {
                            nojur = Sequel.autoNomorSmc("JR", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
                            try {
                                track = "insert into jurnal values ('" + nojur + "', '" + nobukti + "', '" + rscek.getString("tanggal") + "', '" + rscek.getString("jam") + "', '" + jenis + "', '" + keterangan + "')";
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
                                    sukses = true;
                                    track = "insert into jurnal values ('" + nojur + "', '" + nobukti + "', '" + rscek.getString("tanggal") + "', '" + rscek.getString("jam") + "', '" + jenis + "', '" + keterangan + "')";
                                    nojur = Sequel.autoNomorSmc("JR", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"));
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
                                    Sequel.simpanTrackerSQL(track);
                                    try (PreparedStatement psdetail = koneksi.prepareStatement("select * from tampjurnal_smc where user_id = ? and ip = ?")) {
                                        psdetail.setString(1, akses.getkode());
                                        psdetail.setString(2, akses.getalamatip());
                                        try (ResultSet rsdetail = psdetail.executeQuery()) {
                                            while (rsdetail.next()) {
                                                try (PreparedStatement psinsert = koneksi.prepareStatement("insert into detailjurnal values (?, ?, ?, ?)")) {
                                                    track = "insert into detailjurnal values ('" + nojur + "', '" + rsdetail.getString(1) + "', " + rsdetail.getString(3) + ", " + rsdetail.getString(4) + ")";
                                                    psinsert.setString(1, nojur);
                                                    psinsert.setString(2, rsdetail.getString(1));
                                                    psinsert.setString(3, rsdetail.getString(3));
                                                    psinsert.setString(4, rsdetail.getString(4));
                                                    psinsert.executeUpdate();
                                                    Sequel.simpanTrackerSQL(track);
                                                } catch (Exception e) {
                                                    sukses = false;
                                                    System.out.println("Notif : " + e);
                                                }
                                            }
                                        } catch (Exception e) {
                                            sukses = false;
                                            System.out.println("Notif : " + e);
                                        }
                                    } catch (Exception e) {
                                        sukses = false;
                                        System.out.println("Notif : " + e);
                                    }
                                    Sequel.deleteTampJurnal();
                                }
                            } catch (Exception ex) {
                                sukses = false;
                                System.out.println("Notifikasi : " + ex);
                            }
                        } else {
                            sukses = false;
                            System.out.println("Notif : Debet dan Kredit tidak sama");
                        }
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
