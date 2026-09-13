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

    /*
    public synchronized boolean simpanJurnal(String nobukti, String jenis, String keterangan) {
        boolean sukses = true;
        try {
            pscek=koneksi.prepareStatement("select count(*) as jml,current_date() as tanggal,current_time() as jam,sum(tampjurnal.debet) as debet,sum(tampjurnal.kredit) as kredit from tampjurnal");
            try {
                rscek=pscek.executeQuery();
                if(rscek.next()){
                    if(rscek.getInt("debet")==rscek.getInt("kredit")){
                        if(rscek.getInt("jml")>0){
                            nojur=Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(jurnal.no_jurnal,6),signed)),0) from jurnal where jurnal.tgl_jurnal='"+rscek.getString("tanggal")+"' ","JR"+rscek.getString("tanggal").replaceAll("-",""),6);
                            try {
                                 sukses=true;
                                 ps=koneksi.prepareStatement("insert into jurnal values(?,?,?,?,?,?)");
                                 try {
                                    ps.setString(1,nojur);
                                    ps.setString(2,nobukti);
                                    ps.setString(3,rscek.getString("tanggal"));
                                    ps.setString(4,rscek.getString("jam"));
                                    ps.setString(5,jenis);
                                    ps.setString(6,keterangan);
                                    ps.executeUpdate();
                                    Sequel.SimpanTrack("insert into jurnal values('"+nojur+"','"+nobukti+"','"+rscek.getString("tanggal")+"','"+rscek.getString("jam")+"','"+jenis+"','"+keterangan+"')");
                                 } catch (Exception e) {
                                    sukses=false;
                                    System.out.println("Notifikasi : "+e);
                                 } finally{
                                    if(ps!=null){
                                        ps.close();
                                    }
                                 }

                                 if(sukses==false){
                                     nojur=Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(jurnal.no_jurnal,6),signed)),0) from jurnal where jurnal.tgl_jurnal='"+rscek.getString("tanggal")+"' ","JR"+rscek.getString("tanggal").replaceAll("-",""),6);
                                     sukses=true;
                                     ps=koneksi.prepareStatement("insert into jurnal values(?,?,?,?,?,?)");
                                     try {
                                        ps.setString(1,nojur);
                                        ps.setString(2,nobukti);
                                        ps.setString(3,rscek.getString("tanggal"));
                                        ps.setString(4,rscek.getString("jam"));
                                        ps.setString(5,jenis);
                                        ps.setString(6,keterangan);
                                        ps.executeUpdate();
                                        Sequel.SimpanTrack("insert into jurnal values('"+nojur+"','"+nobukti+"','"+rscek.getString("tanggal")+"','"+rscek.getString("jam")+"','"+jenis+"','"+keterangan+"')");
                                     } catch (Exception e) {
                                        sukses=false;
                                        System.out.println("Notifikasi : "+e);
                                     } finally{
                                        if(ps!=null){
                                           ps.close();
                                        }
                                     }
                                 }

                                 if(sukses==true){
                                    try {
                                       ps2=koneksi.prepareStatement("insert into detailjurnal values(?,?,?,?)");
                                       rs=koneksi.prepareStatement("select tampjurnal.kd_rek,tampjurnal.nm_rek,tampjurnal.debet,tampjurnal.kredit from tampjurnal").executeQuery();
                                       while(rs.next()){
                                           try {
                                               ps2.setString(1,nojur);
                                               ps2.setString(2,rs.getString(1));
                                               ps2.setString(3,rs.getString(3));
                                               ps2.setString(4,rs.getString(4));
                                               ps2.addBatch();
                                               Sequel.SimpanTrack("insert into detailjurnal values('"+nojur+"','"+rs.getString(1)+"','"+rs.getString(3)+"','"+rs.getString(4)+"')");
                                           } catch (Exception e) {
                                               sukses=false;
                                               System.out.println("Notifikasi sub : "+e);
                                           }
                                       }
                                       ps2.executeBatch();
                                       ps2.close();
                                    } catch (Exception e) {
                                        sukses=false;
                                        System.out.println("Notif Temp Rek : "+e);
                                    } finally{
                                        if(rs!=null){
                                            rs.close();
                                        }
                                    }
                                    if(sukses==true){
                                        try {
                                            rs=koneksi.prepareStatement("select sum(detailjurnal.debet),sum(detailjurnal.kredit) from detailjurnal where detailjurnal.no_jurnal='"+nojur+"'").executeQuery();
                                            while(rs.next()){
                                                if(rs.getInt(1)!=rs.getInt(2)){
                                                    sukses=false;
                                                    System.out.println("Notif : Debet dan Kredit tidak sama");
                                                }
                                            }
                                        } catch (Exception e) {
                                             sukses=false;
                                             System.out.println("Notifikasi : "+e);
                                        } finally{
                                             if(rs!=null){
                                                 rs.close();
                                             }
                                        }
                                        Sequel.queryu2("delete from tampjurnal");
                                    }
                                 }
                            } catch (Exception ex) {
                                sukses=false;
                                System.out.println("Notifikasi : "+ex);
                            }
                        }
                    }else{
                        System.out.println("Notif : Debet dan Kredit tidak sama");
                        sukses=false;
                    }
                }
            } catch (Exception e) {
                sukses=false;
                System.out.println("Notif : "+e);
            } finally{
                if(rscek!=null){
                    rscek.close();
                }
                if(pscek!=null){
                    pscek.close();
                }
            }
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
        }

        return sukses;
    }
    */

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
                    if (rscek.next()) {
                        if (rscek.getInt("jml") > 0) {
                            if (rscek.getInt("selisih") == 0) {
                                if (rscek.getLong("total_debet") >= 0 && rscek.getLong("total_kredit") >= 0) {
                                    String nojur = "";
                                    int i = 0;
                                    do {
                                        nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"), max(1, i));
                                        sukses = Sequel.menyimpantfSmc("jurnal", "", nojur, nobukti, rscek.getString("tanggal"), rscek.getString("jam"), jenis, keterangan);
                                    } while (!sukses && i++ < 3);

                                    if (sukses && !nojur.isBlank()) {
                                        sukses = Sequel.executeRawSmc("insert into detailjurnal select ? as no_jurnal, tampjurnal_smc.kd_rek, tampjurnal_smc.debet, " +
                                            "tampjurnal_smc.kredit from tampjurnal_smc where user_id = ? and ip = ?", nojur, akses.getkode(), akses.getalamatip()
                                        );
                                        Sequel.deleteTampJurnal();
                                    }
                                } else {
                                    sukses = false;
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
                "as selisih, round(sum(debet), 0) as total_debet, round(sum(kredit), 0) as total_kredit from tampjurnal_rvpbpjs"
            )) {
                if (rscek.next()) {
                    if (rscek.getInt("jml") > 0) {
                        if (rscek.getInt("selisih") == 0) {
                            if (rscek.getLong("total_debet") >= 0 && rscek.getLong("total_kredit") >= 0) {
                                String nojur = "";
                                int i = 0;
                                do {
                                    nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", rscek.getString("tanggal"), max(1, i));
                                    sukses = Sequel.menyimpantfSmc("jurnal", "", nojur, nobukti, rscek.getString("tanggal"), rscek.getString("jam"), jenis, keterangan);
                                } while (!sukses && i++ < 3);

                                if (sukses && !nojur.isBlank()) {
                                    sukses = Sequel.executeRawSmc("insert into detailjurnal select ? as no_jurnal, tampjurnal_rvpbpjs.kd_rek, " +
                                        "tampjurnal_rvpbpjs.debet, tampjurnal_rvpbpjs.kredit from tampjurnal_rvpbpjs", nojur);
                                    Sequel.menghapustfSmc("tampjurnal_rvpbpjs");
                                }
                            } else {
                                sukses = false;
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

    private int max(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }
}
