package bridging;

import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class AccessionRadiologiSMC {
    private final sekuel Sequel=new sekuel();
    private final Connection koneksi = koneksiDB.condb();
    private static final int PANJANG_MAKSIMAL=16;

    public String ambilAccession(String noorder, String kdJenisPrw) {
        if ((null == noorder) || (noorder.isBlank())) {
            System.out.println("Notifikasi : No.Order kosong");
            return "";
        }
        if ((null == kdJenisPrw) || (kdJenisPrw.isBlank())) {
            System.out.println("Notifikasi : Kode jenis perawatan kosong");
            return "";
        }

        String tersimpan = cariAccession(noorder, kdJenisPrw);
        if (!tersimpan.isBlank()) {
            return tersimpan;
        }

        terbitkanAccession(noorder);
        tersimpan = cariAccession(noorder, kdJenisPrw);
        if (tersimpan.isBlank()) {
            System.out.println("Notifikasi : Gagal menerbitkan Accession Number untuk " + noorder + " " + kdJenisPrw);
        }
        return tersimpan;
    }

    public String cariAccession(String noorder, String kdJenisPrw) {
        return Sequel.cariIsiSmc(
                "select ifnull(satu_sehat_accession_radiologi_smc.no_acsn,'') from satu_sehat_accession_radiologi_smc "+
                "where satu_sehat_accession_radiologi_smc.noorder=? and satu_sehat_accession_radiologi_smc.kd_jenis_prw=?",
                noorder, kdJenisPrw);
    }

    public boolean terbitkanAccession(String noorder) {
        if ((null == noorder) || (noorder.isBlank())) {
            System.out.println("Notifikasi : No.Order kosong");
            return false;
        }

        int urutanTerakhir = Sequel.cariIntegerSmc(
                "select ifnull(max(cast(substr(satu_sehat_accession_radiologi_smc.no_acsn, char_length(satu_sehat_accession_radiologi_smc.noorder) - 1) as unsigned)),0) "+
                "from satu_sehat_accession_radiologi_smc where satu_sehat_accession_radiologi_smc.noorder=?",
                0, noorder);

        return Sequel.executeRawSmc(
                "insert into satu_sehat_accession_radiologi_smc (noorder, kd_jenis_prw, no_acsn) "+
                "select permintaan_pemeriksaan_radiologi.noorder, permintaan_pemeriksaan_radiologi.kd_jenis_prw, "+
                "concat(substr(permintaan_pemeriksaan_radiologi.noorder, 3), lpad(cast(? as unsigned) + "+
                "row_number() over (order by permintaan_pemeriksaan_radiologi.kd_jenis_prw), 2, '0')) "+
                "from permintaan_pemeriksaan_radiologi "+
                "left join satu_sehat_accession_radiologi_smc on satu_sehat_accession_radiologi_smc.noorder=permintaan_pemeriksaan_radiologi.noorder "+
                "and satu_sehat_accession_radiologi_smc.kd_jenis_prw=permintaan_pemeriksaan_radiologi.kd_jenis_prw "+
                "where permintaan_pemeriksaan_radiologi.noorder=? and satu_sehat_accession_radiologi_smc.kd_jenis_prw is null",
                String.valueOf(urutanTerakhir), noorder);
    }

    private boolean simpanAccession(String noorder, String kdJenisPrw, String accession) {
        if (Sequel.cariExistsSmc(
                "select 1 from satu_sehat_accession_radiologi_smc where satu_sehat_accession_radiologi_smc.noorder=? "+
                "and satu_sehat_accession_radiologi_smc.kd_jenis_prw=?",
                noorder, kdJenisPrw)) {
            return Sequel.mengupdatetfSmc("satu_sehat_accession_radiologi_smc", "no_acsn=?",
                    "noorder=? and kd_jenis_prw=?", accession, noorder, kdJenisPrw);
        }
        return Sequel.menyimpantfSmc("satu_sehat_accession_radiologi_smc", "noorder, kd_jenis_prw, no_acsn",
                noorder, kdJenisPrw, accession);
    }
}
