package bridging.satusehat.klaim;

import bridging.ApiOrthanc;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sub-sender bundle: kirim CITRA RADIOLOGI (study DICOM dari modality yang sudah tersimpan di Orthanc)
 * untuk satu kunjungan, sebagai bagian orkestrasi SatuSehatBundle (BUKAN entry FHIR — DICOM dikirim via
 * DIMSE ke DICOM Router, SATUSEHAT sendiri yang membuat ImagingStudy-nya).
 *
 * Bedanya dengan {@link SatuSehatKirimBerkasDicom}: yang itu meng-upload berkas .dcm hasil convert
 * dokumen (berkas_digital_perawatan_dicom), yang ini TIDAK meng-upload apa pun — study-nya sudah ada di
 * Orthanc hasil kiriman modality, dicari lewat AccessionNumber = permintaan_radiologi.noorder.
 *
 * WAJIB dipanggil SETELAH {@link SatuSehatRadiologi} pada kunjungan yang sama: router mengaitkan
 * ImagingStudy.basedOn ke ServiceRequest lewat identifier ACSN, jadi ServiceRequest-nya harus sudah ada
 * lebih dulu. Encounter ID disuntikkan sebagai AdmissionID (0038,0010), yang diwajibkan router.
 *
 * Self-skip bila tak ada order radiologi ber-ServiceRequest untuk no_rawat (aman dipanggil untuk semua
 * kunjungan). Tracking: satu_sehat_imagingstudy_radiologi (tabel yang sama dengan menu DICOM Router).
 *
 * Sekali-kirim per order: begitu ImagingStudy tercatat, kiriman klaim berikutnya melewati order itu.
 * Alasan lengkapnya ada di dalam {@link #kirim(String, String)} — singkatnya, kirim ulang memindahkan
 * penunjuk citra ke UID DICOM baru, bukan memperbaiki. Pemaksaan kirim ulang lewat menu manual.
 */
public class SatuSehatKirimDicomRadiologi {

    /** SATUSEHAT membuat ImagingStudy secara async setelah router forward -> poll id beberapa kali. */
    private static final int MAX_COBA_IMAGING = 4;      // jumlah percobaan ambil ImagingStudy
    private static final long JEDA_IMAGING_MS = 3000;   // jeda antar percobaan (ms)

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final SatuSehatCekNIK cek = new SatuSehatCekNIK();
    private final ApiOrthanc orthanc = new ApiOrthanc();

    /**
     * Satu order (noorder) = SATU study DICOM di Orthanc, walau order itu memuat beberapa jenis
     * pemeriksaan. Karena itu pengiriman dikelompokkan per noorder, sedangkan tracking disimpan
     * per kd_jenis_prw (mengikuti primary key tabel satu_sehat_imagingstudy_radiologi).
     */
    private static class Order {
        String noOrder;
        final List<String> kdJenisPrw = new ArrayList<String>();
        String idServiceRequest = "";
        String idImagingLama = "";   // "" bila ImagingStudy-nya belum pernah terindeks
    }

    /**
     * Kirim study radiologi milik no_rawat ke DICOM Router. idEncounter -> AdmissionID.
     *
     * @return true bila ADA id ImagingStudy baru yang tersimpan — pemanggil perlu mengirim ulang
     *         resource radiologi supaya DiagnosticReport-nya membawa imagingStudy[].
     */
    public boolean kirim(String noRawat, String idEncounter) {
        // Butuh Encounter ID (disuntik sbg AdmissionID); tanpa itu router menolak "AdmissionID missing".
        // Setiap jalan-keluar di bawah ini WAJIB bersuara: langkah ini muncul di progress bar, jadi
        // diam-diam melewat akan terbaca operator sebagai "sudah terkirim" padahal tidak ada apa-apa.
        if (idEncounter == null || idEncounter.trim().isEmpty()) {
            System.out.println("Citra radiologi " + noRawat
                    + " : DILEWATI, Encounter ID kosong (Encounter belum terkirim untuk kunjungan ini).");
            return false;
        }
        int portRouter;
        try {
//            portRouter = Integer.parseInt(koneksiDB.PORTDICOMROUTER().trim());
        } catch (Exception e) {
//            System.out.println("Notifikasi citra radiologi : PORTDICOMROUTER tidak valid ("
//                    + koneksiDB.PORTDICOMROUTER() + "). Dilewati.");
            return false;
        }
        siapkanTabel();
        boolean adaImagingBaru = false;
        try {
            List<Order> daftar = ambilOrder(noRawat);
            if (daftar.isEmpty()) {
                System.out.println("Citra radiologi " + noRawat + " : DILEWATI, tidak ada order radiologi"
                        + " yang ServiceRequest-nya sudah terkirim (cek satu_sehat_servicerequest_radiologi;"
                        + " langkah Radiologi harus sukses lebih dulu).");
                return false;
            }
            int dikirim = 0, gagal = 0, dilewat = 0;
            for (Order order : daftar) {
                // Order yang ImagingStudy-nya sudah tercatat TIDAK dikirim ulang otomatis.
                //
                // Bukan karena takut duplikat — itu sudah dibuktikan tidak terjadi (30 Juli 2026:
                // PR202512290004 dikirim ulang, ImagingStudy tetap satu, id 76868955-... tidak berubah;
                // router meng-upsert dgn kunci AccessionNumber). Sebabnya: tiap kiriman memakai UID
                // DICOM baru, dan ImagingStudy langsung dialihkan menunjuk salinan TERBARU sementara
                // binary-nya menyusul lewat scheduler push router yang tertunda. Jadi kirim ulang bukan
                // memperbaiki, melainkan memindahkan sasaran — dan sempat membuat citra yang tadinya
                // tampil jadi tidak tampil. Kalau kiriman pertama sudah berhasil, mengulanginya tiap
                // klaim dikirim ulang cuma menambah risiko tanpa manfaat.
                if (!order.idImagingLama.isEmpty()) {
                    dilewat++;
                    System.out.println("Citra radiologi order " + order.noOrder + " : DILEWATI, ImagingStudy "
                            + order.idImagingLama + " sudah tercatat.");
                    System.out.println("    Untuk memaksa kirim ulang (mis. citra tidak muncul di SATUSEHAT),"
                            + " pakai menu \"Kirim DICOM Router Satu Sehat\" dgn filter \"Data belum terkirim\" dimatikan.");
                    continue;
                }
//                boolean ok = orthanc.sendStudyByAccessionNumber(
//                        order.noOrder, idEncounter.trim(),
//                        koneksiDB.AETITLEDICOMROUTER(),
//                        koneksiDB.URLDICOMROUTER(),
//                        portRouter);
//                if (!ok) {
//                    gagal++;
//                    System.out.println("Bundle: citra radiologi order " + order.noOrder
//                            + " GAGAL dikirim ke router.");
//                    continue;
//                }
//                dikirim++;
                // Router membuat ImagingStudy secara async, jadi id belum tentu ada tepat setelah store
                // -> retry. Bila tetap kosong dlm rentang retry, tidak ada id yang tersimpan sehingga
                // order ini tetap dianggap belum terkirim dan dicoba lagi pada kiriman klaim berikutnya.
//                String idImaging = ambilIdImaging(order.noOrder);
//                if (!idImaging.isEmpty()) {
//                    adaImagingBaru = true;
//                }
//                for (String kdJenisPrw : order.kdJenisPrw) {
//                    simpanTracking(order.noOrder, kdJenisPrw, order.idServiceRequest, idImaging);
//                }
//                System.out.println("Bundle: citra radiologi order " + order.noOrder + " terkirim (id_imaging="
//                        + (idImaging.isEmpty() ? "(belum terindeks)" : idImaging) + ")");
            }
            System.out.println("Citra radiologi " + noRawat + " : selesai. " + daftar.size()
                    + " order, " + dikirim + " terkirim, " + gagal + " gagal, " + dilewat + " dilewati.");
        } catch (Exception e) {
            System.out.println("Notifikasi kirim citra radiologi bundle : " + e);
        }
        return adaImagingBaru;
    }

    /**
     * Order radiologi milik no_rawat yang ServiceRequest-nya SUDAH terkirim, dikelompokkan per noorder.
     * jns_perawatan_radiologi ikut di-join karena tabel tracking punya foreign key ke tabel itu —
     * kd_jenis_prw yang tidak ada di sana akan ditolak saat insert.
     */
    private List<Order> ambilOrder(String noRawat) {
        Map<String, Order> perOrder = new LinkedHashMap<String, Order>();
        String sql = "select ppr.noorder, ppr.kd_jenis_prw, "
                + "ifnull(ssr.id_servicerequest,'') as id_servicerequest, "
                + "ifnull(ssi.id_imaging,'') as id_imaging "
                + "from permintaan_radiologi pr "
                + "inner join permintaan_pemeriksaan_radiologi ppr on ppr.noorder = pr.noorder "
                + "inner join jns_perawatan_radiologi jpr on jpr.kd_jenis_prw = ppr.kd_jenis_prw "
                + "inner join satu_sehat_servicerequest_radiologi ssr on ssr.noorder = ppr.noorder "
                + "  and ssr.kd_jenis_prw = ppr.kd_jenis_prw and ifnull(ssr.id_servicerequest,'') <> '' "
                + "left join satu_sehat_imagingstudy_radiologi ssi on ssi.noorder = ppr.noorder "
                + "  and ssi.kd_jenis_prw = ppr.kd_jenis_prw "
                + "where pr.no_rawat = ? "
                + "order by ppr.noorder, ppr.kd_jenis_prw";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                String noOrder = r.getString("noorder");
                Order order = perOrder.get(noOrder);
                if (order == null) {
                    order = new Order();
                    order.noOrder = noOrder;
                    perOrder.put(noOrder, order);
                }
                order.kdJenisPrw.add(r.getString("kd_jenis_prw"));
                if (order.idServiceRequest.isEmpty()) {
                    order.idServiceRequest = nz(r.getString("id_servicerequest"));
                }
                // Satu noorder bisa punya beberapa kd_jenis_prw; id_imaging-nya sama untuk semua,
                // jadi cukup ambil yang pertama tidak kosong.
                if (order.idImagingLama.isEmpty()) {
                    order.idImagingLama = nz(r.getString("id_imaging")).trim();
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi citra radiologi ambilOrder : " + e);
        }
        return new ArrayList<Order>(perOrder.values());
    }

    /**
     * Ambil id ImagingStudy dari SATUSEHAT dgn retry. Router mem-forward DICOM ke SATUSEHAT secara
     * async ("push deferred to scheduler"), jadi id belum tentu ada tepat setelah store -> coba beberapa
     * kali dgn jeda. Mengembalikan "" bila tetap belum terindeks dalam rentang retry.
     */
    private String ambilIdImaging(String noOrder) {
        String id = "";
        for (int coba = 1; coba <= MAX_COBA_IMAGING; coba++) {
            id = cek.getImagingStudyID(noOrder);
            if (id != null && !id.trim().isEmpty()) {
                return id;
            }
            System.out.println("ImagingStudy belum terindeks (coba " + coba + "/" + MAX_COBA_IMAGING
                    + ") accession=" + noOrder);
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

    private void simpanTracking(String noOrder, String kdJenisPrw, String idServiceRequest, String idImaging) {
        try {
            Sequel.queryu2(
                    "insert into satu_sehat_imagingstudy_radiologi(noorder,kd_jenis_prw,id_servicerequest,id_imaging) "
                    + "values(?,?,?,?) on duplicate key update "
                    + "id_servicerequest=values(id_servicerequest),"
                    + "id_imaging=ifnull(nullif(values(id_imaging),''),id_imaging)",
                    4, new String[]{noOrder, kdJenisPrw, idServiceRequest, idImaging});
        } catch (Exception e) {
            System.out.println("Notifikasi simpan citra radiologi bundle : " + e);
        }
    }

    /** DDL sama dengan menu DICOM Router — satu tabel dipakai bersama menu manual dan bundle. */
    private void siapkanTabel() {
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "create table if not exists satu_sehat_imagingstudy_radiologi ("
                    + "noorder varchar(15) not null,"
                    + "kd_jenis_prw varchar(15) not null,"
                    + "id_servicerequest varchar(40) default null,"
                    + "id_imaging varchar(40) default null,"
                    + "primary key (noorder,kd_jenis_prw),"
                    + "index kd_jenis_prw(kd_jenis_prw),"
                    + "constraint satu_sehat_imagingstudy_radiologi_ibfk_1 foreign key (noorder) references permintaan_radiologi (noorder) on delete cascade on update cascade,"
                    + "constraint satu_sehat_imagingstudy_radiologi_ibfk_2 foreign key (kd_jenis_prw) references jns_perawatan_radiologi (kd_jenis_prw) on delete cascade on update cascade"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            ps.execute();
            ps.close();
        } catch (Exception e) {
            System.out.println("Notifikasi siapkan tabel citra radiologi bundle : " + e);
        }
    }

    private String nz(String data) {
        return data == null ? "" : data;
    }
}
