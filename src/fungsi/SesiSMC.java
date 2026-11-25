package fungsi;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Timer;

/**
 * Batas waktu sesi berdasarkan aktivitas pengguna (tekan tombol, klik, dan scroll mouse).
 * Sesi tidak berakhir selama transaksi database masih terbuka.
 * Proses panjang menandai dirinya dengan {@link #mulai(String)} agar sesi tidak berakhir selama proses berjalan,
 * dan hasilnya ditampilkan kembali setelah pemilik proses login apabila sesinya berakhir sebelum hasil terlihat.
 */
public final class SesiSMC {
    private static final int INTERVAL_CEK = 5000;
    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final AtomicInteger jumlahSibuk = new AtomicInteger();
    private static final Map<String, List<Hasil>> hasilTertunda = new HashMap<>();
    private static volatile long aktivitasTerakhir = System.currentTimeMillis();
    private static volatile boolean masuk = false;
    private static long batasMilidetik = 0;
    private static int batasMenit = 0;
    private static Runnable aksiBerakhir;
    private static Timer timer;

    private SesiSMC() {}

    /**
     * Dipanggil sekali oleh frmUtama. Menit 0 atau kurang menonaktifkan batas waktu sesi.
     */
    public static void pasang(int menit, Runnable aksi) {
        if (timer != null || menit <= 0) {
            return;
        }

        batasMenit = menit;
        batasMilidetik = menit * 60_000L;
        aksiBerakhir = aksi;
        Toolkit.getDefaultToolkit().addAWTEventListener(SesiSMC::catatAktivitas, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        timer = new Timer(INTERVAL_CEK, e -> cekBatas());
        timer.start();
    }

    public static int getBatasMenit() {
        return batasMenit;
    }

    public static void login() {
        aktivitasTerakhir = System.currentTimeMillis();
        masuk = true;
    }

    public static void logout() {
        masuk = false;
        String kode = akses.getkode();
        synchronized (hasilTertunda) {
            List<Hasil> daftar = hasilTertunda.get(kode);
            if (daftar != null) {
                daftar.removeIf(h -> h.waktu < aktivitasTerakhir);
                if (daftar.isEmpty()) {
                    hasilTertunda.remove(kode);
                }
            }
        }
    }

    /**
     * Mengambil dan menghapus hasil proses milik pengguna yang sedang login.
     */
    public static List<String> ambilHasil() {
        List<String> teks = new ArrayList<>();
        synchronized (hasilTertunda) {
            List<Hasil> daftar = hasilTertunda.remove(akses.getkode());
            if (daftar != null) {
                for (Hasil h : daftar) {
                    teks.add(h.teks);
                }
            }
        }
        return teks;
    }

    public static Sibuk mulai(String nama) {
        return new Sibuk(nama, akses.getkode());
    }

    public static void jalankan(String nama, Runnable tugas) {
        Sibuk sibuk = mulai(nama);
        try {
            tugas.run();
            sibuk.selesai("selesai");
        } catch (RuntimeException | Error e) {
            sibuk.selesai("gagal, " + e);
            throw e;
        }
    }

    /**
     * Aktivitas dicatat saat masukan diterima, dan sekali lagi setelah penanganannya selesai. Penanganan klik yang
     * menyimpan data di EDT bisa berjalan lebih lama dari batas sesi; pencatatan kedua mencegah sesi langsung
     * berakhir begitu penyimpanan selesai.
     */
    private static void catatAktivitas(AWTEvent e) {
        int id = e.getID();
        if (KeyEvent.KEY_PRESSED == id || KeyEvent.KEY_RELEASED == id || MouseEvent.MOUSE_PRESSED == id || MouseEvent.MOUSE_RELEASED == id || MouseEvent.MOUSE_WHEEL == id) {
            aktivitasTerakhir = System.currentTimeMillis();
            EventQueue.invokeLater(() -> aktivitasTerakhir = System.currentTimeMillis());
        }
    }

    private static void cekBatas() {
        if (!masuk || jumlahSibuk.get() > 0) {
            return;
        }

        if (System.currentTimeMillis() - aktivitasTerakhir >= batasMilidetik && !transaksiTerbuka()) {
            masuk = false;
            aksiBerakhir.run();
        }
    }

    private static boolean transaksiTerbuka() {
        try {
            Connection koneksi = koneksiDB.condb();
            return null != koneksi && !koneksi.getAutoCommit();
        } catch (Exception e) {
            return false;
        }
    }

    private static void simpanHasil(String pemilik, String teks) {
        if (pemilik == null || pemilik.isEmpty()) {
            return;
        }

        synchronized (hasilTertunda) {
            hasilTertunda.computeIfAbsent(pemilik, k -> new ArrayList<>()).add(new Hasil(teks, System.currentTimeMillis()));
        }
    }

    private static final class Hasil {
        private final String teks;
        private final long waktu;

        private Hasil(String teks, long waktu) {
            this.teks = teks;
            this.waktu = waktu;
        }
    }

    /**
     * Penanda proses panjang. Selama penanda aktif, sesi tidak berakhir. Panggil {@link #selesai(String)}
     * sebelum menampilkan pesan akhir proses, karena pesan modal yang menunggu pengguna akan menahan sesi.
     */
    public static final class Sibuk implements AutoCloseable {
        private final String nama;
        private final String pemilik;
        private boolean aktif = true;
        private boolean berakhir = false;

        private Sibuk(String nama, String pemilik) {
            this.nama = nama;
            this.pemilik = pemilik;
            jumlahSibuk.incrementAndGet();
        }

        /**
         * Proses sedang menunggu jawaban pengguna, sehingga sesi boleh berakhir.
         */
        public synchronized void tunggu() {
            if (!berakhir && aktif) {
                aktif = false;
                jumlahSibuk.decrementAndGet();
            }
        }

        public synchronized void lanjut() {
            if (!berakhir && !aktif) {
                aktif = true;
                jumlahSibuk.incrementAndGet();
            }
        }

        public synchronized void selesai(String hasil) {
            if (berakhir) {
                return;
            }

            berakhir = true;
            if (aktif) {
                aktif = false;
                jumlahSibuk.decrementAndGet();
            }
            simpanHasil(pemilik, LocalDateTime.now().format(FORMAT_WAKTU) + "  " + nama + ": " + hasil);
        }

        @Override
        public void close() {
            selesai("selesai");
        }
    }
}
