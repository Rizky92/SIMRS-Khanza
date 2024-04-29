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
        try {
            pscek = koneksi.prepareStatement("select count(*) as jml,current_date() as tanggal,current_time() as jam,sum(tampjurnal_rvpbpjs.debet) as debet,sum(tampjurnal_rvpbpjs.kredit) as kredit from tampjurnal_rvpbpjs");
            try {
                rscek = pscek.executeQuery();
                if (rscek.next()) {
                    if (rscek.getInt("debet") == rscek.getInt("kredit")) {
                        if (rscek.getInt("jml") > 0) {
                            nojur = Valid.autoNomer3(
                                    "select ifnull(MAX(CONVERT(RIGHT(jurnal.no_jurnal,6),signed)),0) from jurnal where jurnal.tgl_jurnal='"
                                            + rscek.getString("tanggal") + "' ",
                                    "JR" + rscek.getString("tanggal").replaceAll("-", ""), 6);
                            try {
                                sukses = true;
                                ps = koneksi.prepareStatement("insert into jurnal values(?,?,?,?,?,?)");
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
                                    nojur = Valid.autoNomer3(
                                            "select ifnull(MAX(CONVERT(RIGHT(jurnal.no_jurnal,6),signed)),0) from jurnal where jurnal.tgl_jurnal='" + rscek.getString("tanggal") + "' ",
                                            "JR" + rscek.getString("tanggal").replaceAll("-", ""), 6);
                                    sukses = true;
                                    ps = koneksi.prepareStatement("insert into jurnal values(?,?,?,?,?,?)");
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
                                    try {
                                        rs = koneksi.prepareStatement(
                                                "select tampjurnal_rvpbpjs.kd_rek,tampjurnal_rvpbpjs.nm_rek,tampjurnal_rvpbpjs.debet,tampjurnal_rvpbpjs.kredit from tampjurnal_rvpbpjs")
                                                .executeQuery();
                                        while (rs.next()) {
                                            ps2 = koneksi.prepareStatement("insert into detailjurnal values(?,?,?,?)");
                                            try {
                                                ps2.setString(1, nojur);
                                                ps2.setString(2, rs.getString(1));
                                                ps2.setString(3, rs.getString(3));
                                                ps2.setString(4, rs.getString(4));
                                                ps2.executeUpdate();
                                            } catch (Exception e) {
                                                sukses = false;
                                                System.out.println("Notifikasi sub : " + e);
                                            } finally {
                                                if (ps2 != null) {
                                                    ps2.close();
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        sukses = false;
                                        System.out.println("Notif Temp Rek : " + e);
                                    } finally {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    Sequel.queryu2("delete from tampjurnal_rvpbpjs");
                                }
                            } catch (Exception ex) {
                                sukses = false;
                                System.out.println("Notifikasi : " + ex);
                            }
                        }
                    } else {
                        BigDecimal totalDebit = new BigDecimal(rscek.getFloat("debet"));
                        BigDecimal totalKredit = new BigDecimal(rscek.getFloat("kredit"));
                        System.out.println("Notif : Debet dan Kredit tidak sama");
                        System.out.println("Notif : Total Debit = " + totalDebit.toString());
                        System.out.println("Notif : Total Kredit = " + totalKredit.toString());
                        sukses = false;
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
                    "select count(*) as jml,current_date() as tanggal,current_time() as jam,sum(tampjurnal_smc.debet) as debet,sum(tampjurnal_smc.kredit) as kredit from tampjurnal_smc where user_id = '" + akses.getkode() + "' and ip = '" + akses.getalamatip() + "'");
            try {
                rscek = pscek.executeQuery();
                if (rscek.next()) {
                    if (rscek.getInt("debet") == rscek.getInt("kredit")) {
                        if (rscek.getInt("jml") > 0) {
                            nojur = Valid.autoNomer3(
                                    "select ifnull(MAX(CONVERT(RIGHT(jurnal.no_jurnal,6),signed)),0) from jurnal where jurnal.tgl_jurnal='"
                                            + rscek.getString("tanggal") + "' ",
                                    "JR" + rscek.getString("tanggal").replaceAll("-", ""), 6);
                            try {
                                sukses = true;
                                ps = koneksi.prepareStatement("insert into jurnal values(?,?,?,?,?,?)");
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
                                    nojur = Valid.autoNomer3(
                                            "select ifnull(MAX(CONVERT(RIGHT(jurnal.no_jurnal,6),signed)),0) from jurnal where jurnal.tgl_jurnal='" + rscek.getString("tanggal") + "' ",
                                            "JR" + rscek.getString("tanggal").replaceAll("-", ""), 6);
                                    sukses = true;
                                    ps = koneksi.prepareStatement("insert into jurnal values(?,?,?,?,?,?)");
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
                                    try {
                                        rs = koneksi.prepareStatement(
                                                "select tampjurnal_smc.kd_rek,tampjurnal_smc.nm_rek,tampjurnal_smc.debet,tampjurnal_smc.kredit from tampjurnal_smc where user_id = '" + akses.getkode() + "' and ip = '" + akses.getalamatip() + "'")
                                                .executeQuery();
                                        while (rs.next()) {
                                            ps2 = koneksi.prepareStatement("insert into detailjurnal values(?,?,?,?)");
                                            try {
                                                ps2.setString(1, nojur);
                                                ps2.setString(2, rs.getString(1));
                                                ps2.setString(3, rs.getString(3));
                                                ps2.setString(4, rs.getString(4));
                                                ps2.executeUpdate();
                                            } catch (Exception e) {
                                                sukses = false;
                                                System.out.println("Notifikasi sub : " + e);
                                            } finally {
                                                if (ps2 != null) {
                                                    ps2.close();
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        sukses = false;
                                        System.out.println("Notif Temp Rek : " + e);
                                    } finally {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    Sequel.deleteTampJurnal();
                                }
                            } catch (Exception ex) {
                                sukses = false;
                                System.out.println("Notifikasi : " + ex);
                            }
                        }
                    }else{
                        BigDecimal totalDebit = new BigDecimal(rscek.getFloat("debet"));
                        BigDecimal totalKredit = new BigDecimal(rscek.getFloat("kredit"));
                        System.out.println("Notif : Debet dan Kredit tidak sama");
                        System.out.println("Notif : Total Debit = " + totalDebit.toString());
                        System.out.println("Notif : Total Kredit = " + totalKredit.toString());
                        sukses=false;
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
