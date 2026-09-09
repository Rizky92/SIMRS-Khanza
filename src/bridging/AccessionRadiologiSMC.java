package bridging;

import fungsi.sekuel;
import java.util.ArrayList;

public class AccessionRadiologiSMC {
    private final sekuel Sequel = new sekuel();

    public String getNoACSN(String acsn, String noorder, String kodeTindakan) {
        if (null == acsn || acsn.isBlank()) {
            return getNoACSN(noorder, kodeTindakan);
        }

        return acsn;
    }

    public String getNoACSN(String noorder, String kodeTindakan) {
        if ((null == noorder) || (noorder.isBlank())) {
            System.out.println("Notifikasi : No.Order kosong");
            return "";
        }

        if ((null == kodeTindakan) || (kodeTindakan.isBlank())) {
            System.out.println("Notifikasi : Kode jenis perawatan kosong");
            return "";
        }

        String noACSN = Sequel.cariIsiSmc(
            "select ifnull(satu_sehat_accession_radiologi_smc.no_acsn,'') from satu_sehat_accession_radiologi_smc " +
            "where satu_sehat_accession_radiologi_smc.noorder = ? and satu_sehat_accession_radiologi_smc.kd_jenis_prw = ?",
            noorder, kodeTindakan
        );

        if ((null == noACSN) || (noACSN.isBlank())) {
            simpanACSN(noorder);
            noACSN = Sequel.cariIsiSmc(
                "select ifnull(satu_sehat_accession_radiologi_smc.no_acsn,'') from satu_sehat_accession_radiologi_smc " +
                "where satu_sehat_accession_radiologi_smc.noorder = ? and satu_sehat_accession_radiologi_smc.kd_jenis_prw = ?",
                noorder, kodeTindakan
            );
        }

        return noACSN;
    }

    public boolean simpanACSN(String noorder) {
        if ((null == noorder) || (noorder.isBlank())) {
            System.out.println("Notifikasi : No.Order kosong");
            return false;
        }

        ArrayList<String> belumTerbit = Sequel.cariArraySmc(
            "select permintaan_pemeriksaan_radiologi.kd_jenis_prw from permintaan_pemeriksaan_radiologi left join satu_sehat_accession_radiologi_smc " +
            "on satu_sehat_accession_radiologi_smc.noorder = permintaan_pemeriksaan_radiologi.noorder and satu_sehat_accession_radiologi_smc.kd_jenis_prw = " +
            "permintaan_pemeriksaan_radiologi.kd_jenis_prw where permintaan_pemeriksaan_radiologi.noorder = ? and satu_sehat_accession_radiologi_smc.no_acsn is null " +
            "order by permintaan_pemeriksaan_radiologi.kd_jenis_prw", noorder
        );

        if (belumTerbit.isEmpty()) {
            return true;
        }

        int urutan = Sequel.cariIntegerSmc(
            "select ifnull(max(cast(right(satu_sehat_accession_radiologi_smc.no_acsn, 2) as unsigned)), 0) from satu_sehat_accession_radiologi_smc " +
            "where satu_sehat_accession_radiologi_smc.noorder = ?", 0, noorder
        );

        boolean berhasil = true;

        for (String kodeTindakan : belumTerbit) {
            urutan++;
            if (!Sequel.menyimpantfSmc("satu_sehat_accession_radiologi_smc", "noorder, kd_jenis_prw, no_acsn",
                    noorder, kodeTindakan, noorder.substring(2) + String.format("%02d", urutan))) {
                System.out.println("Notifikasi : Accession Number gagal diterbitkan untuk " + noorder + " / " + kodeTindakan);
                berhasil = false;
            }
        }

        return berhasil;
    }
}
