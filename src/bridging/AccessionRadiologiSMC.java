package bridging;

import fungsi.sekuel;

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

        Sequel.menghapustfSmc("satu_sehat_accession_radiologi_smc", "noorder = ?", noorder);

        return Sequel.executeRawSmc(
            "insert into satu_sehat_accession_radiologi_smc (noorder, kd_jenis_prw, no_acsn) select permintaan_pemeriksaan_radiologi.noorder, permintaan_pemeriksaan_radiologi.kd_jenis_prw, " +
            "concat(substr(permintaan_pemeriksaan_radiologi.noorder, 3), lpad(row_number() over (order by permintaan_pemeriksaan_radiologi.kd_jenis_prw), 2, '0')) as no_acsn from " +
            "permintaan_pemeriksaan_radiologi where permintaan_pemeriksaan_radiologi.noorder = ?", noorder
        );
    }
}
