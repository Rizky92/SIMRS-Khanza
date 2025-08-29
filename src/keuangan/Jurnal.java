/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keuangan;

import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author khanzamedia
 */
public class Jurnal {
    private final sekuel Sequel = new sekuel();
    private final Connection koneksi = koneksiDB.condb();
    private String nojur = "", nobukti = "", jenis = "", keterangan = "";
    private final HashMap<String, Double> detaildebet = new HashMap<>(),
                                          detailkredit = new HashMap<>();

    public Jurnal() {}

    public Jurnal(String nobukti, String jenis, String keterangan) {
        this.nobukti = nobukti;
        this.jenis = jenis;
        this.keterangan = keterangan;
    }
    
    public synchronized void bersihkan() {
        detaildebet.clear();
        detailkredit.clear();
    }
    
    public synchronized boolean tampung(String kdrek, String nmrek, double debet, double kredit) {
        // gagalkan apabila debet dan kredit 0
        if (debet <= 0 && kredit <= 0) {
            return false;
        }
        
        if (debet > 0) {
            if (detaildebet.containsKey(kdrek)) {
                detaildebet.put(kdrek, detaildebet.get(kdrek) + debet);
            } else {
                detaildebet.put(kdrek, debet);
            }
        }
        
        if (kredit > 0) {
            if (detailkredit.containsKey(kdrek)) {
                detailkredit.put(kdrek, detailkredit.get(kdrek) + kredit);
            } else {
                detailkredit.put(kdrek, kredit);
            }
        }

        return true;
    }
    
    public synchronized boolean tampung(String kdrek, String nmrek, String debet, String kredit) {
        double d = 0, k = 0;
        try {
            d = Double.parseDouble(debet);
            k = Double.parseDouble(kredit);
        } catch (Exception e) {
            return false;
        }
        return this.tampung(kdrek, nmrek, d, k);
    }
    
    public synchronized boolean tampung(Object kdrek, Object nmrek, Object debet, Object kredit) {
        return this.tampung(kdrek.toString(), nmrek.toString(), debet.toString(), kredit.toString());
    }
    
    public synchronized boolean simpanJurnal() {
        if (detaildebet.isEmpty() || detailkredit.isEmpty()) {
            return false;
        }
        
        int totaldebet = detaildebet.values().stream().reduce(0d, Double::sum).intValue(),
            totalkredit = detailkredit.values().stream().reduce(0d, Double::sum).intValue();
        
        // cek jumlah debet/kredit apabila nominal 0 atau lebih kecil,
        // atau apabila total debet tidak sesuai dengan total kredit
        if (totaldebet <= 0 || totalkredit <= 0 || totaldebet != totalkredit) {
            return false;
        }
        
        int next = 1, retry = 4;
        boolean sukses = true;

        String tgl = Sequel.cariIsiSmc("select current_date()"),
               jam = Sequel.cariIsiSmc("select current_time()");

        // loop proses simpan, pastikan pencatatan jurnal mendapatkan no. jurnal yang sesuai
        do {
            nojur = Sequel.autonomorSmc("JR", "", "jurnal", "no_jurnal", 6, "0", tgl, next);
            sukses = Sequel.menyimpantfSmc("jurnal", null, nojur, nobukti, tgl, jam, jenis, keterangan);
        } while (!sukses && next++ <= retry);

        // return cepat ketika jurnal nggak bisa terbuat
        if (!sukses) return false;
        
        try (PreparedStatement ps = koneksi.prepareStatement("insert into detailjurnal values (?, ?, ?, ?)")) {
            int batches = 0;   // hitung jumlah batch
            for (Map.Entry<String, Double> debet : detaildebet.entrySet()) {
                ps.setString(1, nojur);
                ps.setString(2, debet.getKey());
                ps.setDouble(3, debet.getValue());
                ps.setDouble(4, 0);
                ps.addBatch();
                ++batches;
            }
            
            for (Map.Entry<String, Double> kredit : detailkredit.entrySet()) {
                ps.setString(1, nojur);
                ps.setString(2, kredit.getKey());
                ps.setDouble(3, 0);
                ps.setDouble(4, kredit.getValue());
                ps.addBatch();
                ++batches;
            }
            
            // hitung jumlah statement yang tereksekusi,
            // gagal apabila jummlah batch tidak sesuai statement
            if (batches != Arrays.stream(ps.executeBatch()).reduce(0, Integer::sum)) {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            return false;
        }
        
        return true;
    }

    public boolean simpanJurnal(String nobukti, String jenis, String keterangan) {
        this.nobukti = nobukti;
        this.jenis = jenis;
        this.keterangan = keterangan;
        
        return this.simpanJurnal();
    }
}
