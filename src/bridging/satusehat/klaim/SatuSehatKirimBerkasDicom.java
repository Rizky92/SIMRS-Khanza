package bridging.satusehat.klaim;

import bridging.satusehat.ApiOrthancKirimBerkas;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Sub-sender bundle: kirim Berkas DICOM (dokumen DOC yang di-wrap DICOM) untuk satu kunjungan,
 * sebagai bagian orkestrasi SatuSehatBundle (BUKAN entry FHIR — DICOM dikirim via DIMSE ke router).
 *
 * Dipanggil SETELAH Encounter terkirim karena Encounter ID disuntik sebagai AdmissionID (0038,0010),
 * yang diwajibkan router untuk membuat ImagingStudy di SATUSEHAT. Delegasi mekanis ke
 * {@link ApiOrthancKirimBerkas}: upload ke Orthanc -> suntik AdmissionID -> C-STORE ke DICOM Router.
 * Self-skip bila tak ada berkas untuk no_rawat (aman dipanggil untuk semua kunjungan).
 */
public class SatuSehatKirimBerkasDicom {

    /** SATUSEHAT membuat ImagingStudy secara async setelah router forward -> poll id beberapa kali. */
    private static final int MAX_COBA_IMAGING = 4;      // jumlah percobaan ambil ImagingStudy
    private static final long JEDA_IMAGING_MS = 3000;   // jeda antar percobaan (ms)

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ApiOrthancKirimBerkas orthanc = new ApiOrthancKirimBerkas();

    /** Kirim semua berkas DICOM milik no_rawat ke DICOM Router. idEncounter -> AdmissionID. */
    public void kirim(String noRawat, String idEncounter) {
        // Butuh Encounter ID (disuntik sbg AdmissionID); tanpa itu router menolak "AdmissionID missing".
        if (idEncounter == null || idEncounter.trim().isEmpty()) {
            return;
        }
        siapkanTabel();
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "select kode, accession_number, lokasi_file_dicom from berkas_digital_perawatan_dicom "
                    + "where no_rawat=? and ifnull(lokasi_file_dicom,'')<>'' and ifnull(accession_number,'')<>''");
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String kode = rs.getString("kode");
                String accession = rs.getString("accession_number");
                String lokasi = rs.getString("lokasi_file_dicom");
//                boolean ok = orthanc.uploadAndSendToRouter(
//                        lokasi, idEncounter,
//                        koneksiDB.AETITLEDICOMROUTER(),
//                        koneksiDB.URLDICOMROUTER(),
//                        Integer.parseInt(koneksiDB.PORTDICOMROUTER()));
//                if (ok) {
                    // Baca-balik ImagingStudy dari SATUSEHAT dengan retry (dibuat async oleh router).
                    // Bila tetap kosong dlm rentang retry -> terisi kiriman berikutnya (upsert tak menimpa id yg ada).
//                    String idImaging = ambilIdImaging(accession);
//                    simpanTracking(noRawat, kode, accession, idImaging);
//                    System.out.println("Bundle: berkas DICOM " + noRawat + "/" + kode
//                            + " terkirim (id_imaging=" + (idImaging.isEmpty() ? "(belum terindeks)" : idImaging) + ")");
//                } else {
//                    System.out.println("Bundle: berkas DICOM " + noRawat + "/" + kode + " GAGAL dikirim ke router.");
//                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Notifikasi kirim berkas DICOM bundle : " + e);
        }
    }

    /**
     * Ambil id ImagingStudy dari SATUSEHAT dgn retry. Router mem-forward DICOM ke SATUSEHAT secara
     * async ("push deferred to scheduler"), jadi id belum tentu ada tepat setelah store -> coba beberapa
     * kali dgn jeda. Mengembalikan "" bila tetap belum terindeks dalam rentang retry (akan terisi kiriman berikut).
     */
    private String ambilIdImaging(String accession) {
        String id = "";
        for (int coba = 1; coba <= MAX_COBA_IMAGING; coba++) {
            id = cek.getImagingStudyID(accession);
            if (id != null && !id.trim().isEmpty()) {
                return id;
            }
            System.out.println("ImagingStudy belum terindeks (coba " + coba + "/" + MAX_COBA_IMAGING
                    + ") accession=" + accession);
            if (coba < MAX_COBA_IMAGING) {
                try {
                    Thread.sleep(JEDA_IMAGING_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return id == null ? "" : id;
    }

    private void simpanTracking(String noRawat, String kode, String accession, String idImaging) {
        try {
            Sequel.queryu2(
                    "insert into satu_sehat_imagingstudy_berkas(no_rawat,kode,accession_number,id_imaging) "
                    + "values(?,?,?,?) on duplicate key update "
                    + "id_imaging=ifnull(nullif(values(id_imaging),''),id_imaging)",
                    4, new String[]{noRawat, kode, accession, idImaging});
        } catch (Exception e) {
            System.out.println("Notifikasi simpan berkas DICOM bundle : " + e);
        }
    }

    private void siapkanTabel() {
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "create table if not exists satu_sehat_imagingstudy_berkas ("
                    + "no_rawat varchar(17) not null, kode varchar(10) not null,"
                    + "accession_number varchar(20) not null, id_imaging varchar(40) default null,"
                    + "primary key (no_rawat,kode,accession_number)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.out.println("Notifikasi siapkan tabel berkas DICOM bundle : " + e);
        }
    }
}
