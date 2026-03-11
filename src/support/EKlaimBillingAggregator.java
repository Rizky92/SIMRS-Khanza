package support;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EKlaimBillingAggregator {

    public static class BillingData {
        public long prosedurNonBedah;
        public long prosedurBedah;
        public long konsultasi;
        public long tenagaAhli;
        public long keperawatan;
        public long penunjang;
        public long radiologi;
        public long laboratorium;
        public long pelayananDarah;
        public long kamar;
        public long rawatIntensif;
        public long obatKronis;
        public long obatKemoterapi;
        public long obat;
        public long alkes;
        public long bmhp;
        public long sewaAlat;
        public long rehabilitasi;
        public long totalBilling;
    }

    public static BillingData aggregate(String noRawat) {
        BillingData data = new BillingData();
        Connection conn = koneksiDB.condb();

        try (PreparedStatement ps = conn.prepareStatement("select " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Dokter Paramedis', 'Ranap Dokter Paramedis') and billing.nm_perawatan not like '%terapi%') as prosedur_non_bedah, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Operasi') as prosedur_bedah, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Dokter', 'Ranap Dokter')) as konsultasi, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Paramedis', 'Ranap Paramedis')) as keperawatan, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Radiologi') as radiologi, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Laborat') as laboratorium, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Registrasi', 'Kamar')) as kamar, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Obat' and billing.nm_perawatan like '%kronis%') as obat_kronis, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Obat' and billing.nm_perawatan like '%kemo%') as obat_kemoterapi, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Obat', 'Retur Obat', 'Resep Pulang')) as obat, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Tambahan') as bmhp, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Harian', 'Service')) as sewa_alat, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Dokter Paramedis', 'Ranap Dokter Paramedis') and billing.nm_perawatan like '%terapi%') as rehabilitasi, " +
            "(select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat) as totalbilling " +
            "from reg_periksa where reg_periksa.no_rawat = ?")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.prosedurNonBedah = rs.getLong("prosedur_non_bedah");
                    data.prosedurBedah = rs.getLong("prosedur_bedah");
                    data.konsultasi = rs.getLong("konsultasi");
                    data.keperawatan = rs.getLong("keperawatan");
                    data.radiologi = rs.getLong("radiologi");
                    data.laboratorium = rs.getLong("laboratorium");
                    data.kamar = rs.getLong("kamar");
                    data.obatKronis = rs.getLong("obat_kronis");
                    data.obatKemoterapi = rs.getLong("obat_kemoterapi");
                    long obatTotal = rs.getLong("obat");
                    data.obat = obatTotal - data.obatKronis - data.obatKemoterapi;
                    data.bmhp = rs.getLong("bmhp");
                    data.sewaAlat = rs.getLong("sewa_alat");
                    data.rehabilitasi = rs.getLong("rehabilitasi");
                    data.totalBilling = rs.getLong("totalbilling");
                }
            }
        } catch (Exception e) {
            System.out.println("Notif EKlaimBillingAggregator : " + e);
        }

        return data;
    }
}
