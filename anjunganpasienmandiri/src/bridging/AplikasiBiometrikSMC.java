package bridging;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import widget.LoadingOverlaySMC;

/**
 * Menjalankan login otomatis aplikasi sidik jari dan FRISTA BPJS, lalu memasukkan nomor kartu pasien.
 * <p>
 * Setiap langkah menunggu perubahan keadaan aplikasi, bukan jeda waktu tetap: jendela muncul, jendela berada di depan dan siap menerima input,
 * kolom username sudah terisi, serta form login sudah hilang. Keadaan diperiksa setiap {@link #JEDA_PANTAU} milidetik. Form login dikenali dari
 * kolom kata sandi yang tampil di dalam jendela, karena judul jendela aplikasi sidik jari tidak berubah sebelum dan sesudah login. Kontrol
 * Windows bawaan dibaca langsung, sedangkan aplikasi WPF seperti aplikasi sidik jari dibaca melalui UI Automation ({@link PembacaUIASMC}).
 * <p>
 * Bila kontrol di dalam jendela tidak dapat dibaca (aplikasi tidak memakai kontrol Windows bawaan), langkah yang tidak dapat dipantau kembali
 * memakai jeda waktu lama. Setiap perubahan keadaan dicatat ke konsol dengan awalan {@code Biometrik}.
 * <p>
 * Harus dipanggil di dalam {@link LoadingOverlaySMC#runOverScreen}, supaya pasien tidak dapat menyentuh aplikasi selama login berjalan.
 */
public final class AplikasiBiometrikSMC {
    private static final String JUDUL_FINGERPRINT = "Registrasi Sidik Jari";
    private static final String JUDUL_LOGIN_FRISTA = "Login Frista (Face Recognition BPJS Kesehatan)";
    private static final String JUDUL_FRISTA = "face recognition bpjs kesehatan";
    private static final int JEDA_PANTAU = 100;
    private static final int JEDA_TEMPEL = 1000;
    private static final long BATAS_TANPA_KONTROL = 1500;
    private static final long BATAS_ISI_TEKS = 3000;
    private static final int BATAS_PESAN = 1000;
    private static final int WM_SETTEXT = 0x000C;
    private static final int WM_GETTEXT = 0x000D;
    private static final int EM_GETPASSWORDCHAR = 0x00D2;
    private static final int ES_PASSWORD = 0x0020;
    private static final int GWL_STYLE = -16;
    private static final int SMTO_ABORTIFHUNG = 0x0002;
    private static final int SW_RESTORE = 9;
    private static final int SYNCHRONIZE = 0x00100000;
    private static final int PROCESS_QUERY_LIMITED_INFORMATION = 0x1000;
    private static final int WAIT_TIMEOUT = 0x102;
    private static final String KELAS_POPUP = "#32770";
    private static final int JEDA_PANTAU_POPUP = 200;
    private static final int STM_GETICON = 0x0171;
    private static final int SS_TYPEMASK = 0x001F;
    private static final int SS_ICON = 0x0003;
    private static final String IKON_INFORMASI = "informasi";
    private static final int BAGIAN_WARNA_IKON = 20;
    private static final int WM_COMMAND = 0x0111;
    private static final AtomicReference<Thread> PEMANTAU_FRISTA = new AtomicReference<>();

    private enum Status {
        LOGIN, SIAP, TANPA_KONTROL
    }

    private static final class Kolom {
        static final Kolom TIDAK_ADA = new Kolom(null, null, false, false);

        final WinDef.HWND hwnd;
        final String kunci;
        final boolean teks;
        final boolean sandi;

        Kolom(WinDef.HWND hwnd, String kunci, boolean teks, boolean sandi) {
            this.hwnd = hwnd;
            this.kunci = kunci;
            this.teks = teks;
            this.sandi = sandi;
        }

        boolean terbaca() {
            return hwnd != null || kunci != null;
        }

        boolean sama(Kolom lain) {
            if (hwnd != null) {
                return hwnd.equals(lain.hwnd);
            }
            return kunci != null && kunci.equals(lain.kunci);
        }
    }

    private interface User32SMC extends StdCallLibrary {
        User32SMC INSTANCE = (User32SMC) Native.loadLibrary("user32", User32SMC.class, W32APIOptions.DEFAULT_OPTIONS);

        WinDef.LRESULT SendMessageTimeout(WinDef.HWND hwnd, int pesan, WinDef.WPARAM wParam, char[] lParam, int flags, int timeout, LongByReference hasil);

        WinDef.LRESULT SendMessageTimeout(WinDef.HWND hwnd, int pesan, WinDef.WPARAM wParam, WinDef.LPARAM lParam, int flags, int timeout, LongByReference hasil);

        boolean IsIconic(WinDef.HWND hwnd);

        int GetDlgCtrlID(WinDef.HWND hwnd);
    }

    private final String nama;
    private final long batas;
    private final long mulai = System.nanoTime();
    private final Robot robot = new Robot();
    private String langkah = "memulai";
    private boolean sandiDiClipboard = false;

    private AplikasiBiometrikSMC(String nama, int timeoutDetik) throws Exception {
        this.nama = nama;
        this.batas = timeoutDetik > 0 ? mulai + timeoutDetik * 1_000_000_000L : 0;
    }

    /**
     * Membuka aplikasi sidik jari bila belum berjalan, login bila form login tampil, lalu memasukkan nomor kartu.
     *
     * @param path lokasi aplikasi sidik jari
     * @param user username login
     * @param password kata sandi login
     * @param noKartu nomor kartu BPJS pasien
     * @param timeoutDetik batas waktu seluruh proses dalam detik, 0 berarti tanpa batas
     * @throws TimeoutException bila batas waktu habis sebelum nomor kartu dimasukkan
     * @throws Exception bila aplikasi gagal dijalankan
     */
    public static void bukaFingerprint(String path, String user, String password, String noKartu, int timeoutDetik) throws Exception {
        AplikasiBiometrikSMC aplikasi = new AplikasiBiometrikSMC("Fingerprint", timeoutDetik);
        try {
            aplikasi.fingerprint(path, user, password, noKartu);
        } finally {
            aplikasi.bersihkanClipboard();
        }
    }

    /**
     * Membuka aplikasi FRISTA bila belum berjalan, login bila form login tampil, lalu memasukkan nomor kartu.
     *
     * @param path lokasi aplikasi FRISTA
     * @param user username login
     * @param password kata sandi login
     * @param noKartu nomor kartu BPJS pasien
     * @param timeoutDetik batas waktu seluruh proses dalam detik, 0 berarti tanpa batas
     * @throws TimeoutException bila batas waktu habis sebelum nomor kartu dimasukkan
     * @throws Exception bila aplikasi gagal dijalankan
     */
    public static void bukaFrista(String path, String user, String password, String noKartu, int timeoutDetik) throws Exception {
        AplikasiBiometrikSMC aplikasi = new AplikasiBiometrikSMC("FRISTA", timeoutDetik);
        try {
            aplikasi.frista(path, user, password, noKartu);
        } finally {
            aplikasi.bersihkanClipboard();
        }
    }

    private void fingerprint(String path, String user, String password, String noKartu) throws Exception {
        WinDef.HWND jendela = cariFingerprint();
        boolean baruDibuka = jendela == null;
        if (baruDibuka) {
            LoadingOverlaySMC.setMessage("Membuka aplikasi sidik jari...");
            jalankan(path);
            jendela = tunggu("jendela aplikasi muncul", AplikasiBiometrikSMC::cariFingerprint);
        }

        jadikanDepan(jendela);
        Status status = tungguStatus(AplikasiBiometrikSMC::cariFingerprint);

        if (Status.LOGIN == status) {
            LoadingOverlaySMC.setMessage("Login aplikasi sidik jari...");
            jendela = cariFingerprint();
            jadikanDepan(jendela);
            isiLogin(jendela, user, password);
            tekan(KeyEvent.VK_ENTER);
            jendela = tunggu("login selesai", () -> {
                WinDef.HWND j = cariFingerprint();
                return j != null && Status.LOGIN != statusJendela(j) ? j : null;
            });
        } else if (Status.TANPA_KONTROL == status && baruDibuka) {
            LoadingOverlaySMC.setMessage("Login aplikasi sidik jari...");
            tempel(user);
            tekan(KeyEvent.VK_TAB);
            jedaTetap();
            tempelSandi(password);
            tekan(KeyEvent.VK_ENTER);
            jedaTetap();
            jendela = cariFingerprint();
        }

        LoadingOverlaySMC.setMessage("Memasukkan nomor kartu...");
        if (jendela == null) {
            jendela = tunggu("jendela aplikasi muncul kembali", AplikasiBiometrikSMC::cariFingerprint);
        }
        siapKetik(jendela);
        kosongkan(tungguKolomAktif(jendela));
        tempel(noKartu);
        log("nomor kartu dimasukkan");
    }

    /**
     * Memantau aplikasi FRISTA di latar belakang selama pasien melakukan validasi wajah, lalu menutup otomatis popup konfirmasi. Yang ditutup
     * hanya kotak pesan Windows milik proses FRISTA yang berikon informasi dan bertombol tunggal (OK). Popup peringatan, galat, atau pertanyaan
     * dibiarkan tampil untuk pasien. Setiap popup yang muncul dicatat ke konsol beserta isi pesannya.
     * <p>
     * Pemantauan berhenti bila jendela FRISTA tertutup, {@code pemilik} tidak lagi tampil, atau pemantauan baru dimulai.
     *
     * @param pemilik komponen di jendela anjungan yang memulai validasi
     */
    public static void pantauKonfirmasiFrista(Component pemilik) {
        Thread pemantau = new Thread(() -> pantauFrista(pemilik), "PemantauKonfirmasiFrista");
        pemantau.setDaemon(true);
        Thread lama = PEMANTAU_FRISTA.getAndSet(pemantau);
        if (lama != null) {
            lama.interrupt();
        }
        pemantau.start();
    }

    private static void pantauFrista(Component pemilik) {
        long mulai = System.nanoTime();
        Set<String> selesai = new HashSet<>();
        Map<String, Long> pertamaTerlihat = new HashMap<>();
        try {
            while (Thread.currentThread() == PEMANTAU_FRISTA.get()) {
                WinDef.HWND utama = cariUtamaFrista();
                if (utama == null || !pemilik.isShowing()) {
                    logPemantau(mulai, utama == null ? "jendela FRISTA tertutup, pemantauan berhenti" : "anjungan ditutup, pemantauan berhenti");
                    return;
                }

                WinDef.HWND popup = cariPopup(pid(utama));
                if (popup != null) {
                    String kunci = popup.getPointer().toString();
                    if (!selesai.contains(kunci)) {
                        long terlihat = pertamaTerlihat.computeIfAbsent(kunci, k -> System.nanoTime());
                        boolean menyerah = (System.nanoTime() - terlihat) / 1_000_000 >= BATAS_TANPA_KONTROL;
                        if (tanganiPopup(mulai, popup, menyerah)) {
                            selesai.add(kunci);
                        }
                    }
                }
                Thread.sleep(JEDA_PANTAU_POPUP);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            PEMANTAU_FRISTA.compareAndSet(Thread.currentThread(), null);
        }
    }

    private static WinDef.HWND cariPopup(int pid) {
        WinDef.HWND[] hasil = new WinDef.HWND[1];
        User32.INSTANCE.EnumWindows((hwnd, data) -> {
            if (User32.INSTANCE.IsWindowVisible(hwnd) && KELAS_POPUP.equals(kelas(hwnd)) && pid == pid(hwnd)) {
                hasil[0] = hwnd;
                return false;
            }
            return true;
        }, null);
        return hasil[0];
    }

    private static boolean tanganiPopup(long mulai, WinDef.HWND popup, boolean menyerah) {
        StringBuilder pesan = new StringBuilder();
        WinDef.HWND[] tombol = new WinDef.HWND[1];
        WinDef.HWND[] ikon = new WinDef.HWND[1];
        int[] jumlahTombol = {0};

        User32.INSTANCE.EnumChildWindows(popup, (hwnd, data) -> {
            if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
                return true;
            }
            String kelas = kelas(hwnd);
            if ("Button".equalsIgnoreCase(kelas)) {
                jumlahTombol[0]++;
                tombol[0] = hwnd;
            } else if ("Static".equalsIgnoreCase(kelas)) {
                if ((User32.INSTANCE.GetWindowLong(hwnd, GWL_STYLE) & SS_TYPEMASK) == SS_ICON) {
                    ikon[0] = hwnd;
                } else {
                    String teks = bacaTeks(hwnd).trim();
                    if (!teks.isEmpty()) {
                        pesan.append(pesan.length() == 0 ? "" : " ").append(teks.replaceAll("\\s+", " "));
                    }
                }
            }
            return true;
        }, null);

        String jenis = ikon[0] == null ? "tanpa ikon" : jenisIkon(ikon[0]);
        if (jenis == null && !menyerah) {
            return false;
        }

        String keterangan = "popup '" + judul(popup) + "': " + pesan + " (ikon: " + (jenis == null ? "tidak dikenali" : jenis) + ", tombol: " + jumlahTombol[0] + ")";
        if (IKON_INFORMASI.equals(jenis) && jumlahTombol[0] == 1) {
            int id = User32SMC.INSTANCE.GetDlgCtrlID(tombol[0]);
            User32.INSTANCE.PostMessage(popup, WM_COMMAND, new WinDef.WPARAM(id & 0xFFFF), new WinDef.LPARAM(Pointer.nativeValue(tombol[0].getPointer())));
            logPemantau(mulai, keterangan + ", ditutup otomatis");
        } else {
            logPemantau(mulai, keterangan + ", dibiarkan untuk pasien");
        }
        return true;
    }

    private static String jenisIkon(WinDef.HWND ikon) {
        WinDef.RECT kotak = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(ikon, kotak);
        int lebar = kotak.right - kotak.left;
        int tinggi = kotak.bottom - kotak.top;
        if (lebar <= 0 || tinggi <= 0) {
            return null;
        }

        BufferedImage gambar;
        try {
            gambar = new Robot().createScreenCapture(new Rectangle(kotak.left, kotak.top, lebar, tinggi));
        } catch (AWTException | RuntimeException e) {
            return null;
        }

        int biru = 0, merah = 0, kuning = 0;
        for (int x = 0; x < lebar; x++) {
            for (int y = 0; y < tinggi; y++) {
                int piksel = gambar.getRGB(x, y);
                int r = (piksel >> 16) & 0xFF, g = (piksel >> 8) & 0xFF, b = piksel & 0xFF;
                if (b > 150 && r < 110 && b - g > 30) {
                    biru++;
                } else if (r > 170 && g < 100 && b < 100) {
                    merah++;
                } else if (r > 200 && g > 150 && b < 90) {
                    kuning++;
                }
            }
        }

        int minimum = lebar * tinggi * BAGIAN_WARNA_IKON / 100;
        if (biru >= minimum && merah < minimum && kuning < minimum) {
            return IKON_INFORMASI;
        } else if (merah >= minimum) {
            return "galat";
        } else if (kuning >= minimum) {
            return "peringatan";
        }
        return null;
    }

    private static void logPemantau(long mulai, String pesan) {
        System.out.println("Biometrik FRISTA pemantau (" + (System.nanoTime() - mulai) / 1_000_000 + " ms) : " + pesan);
    }

    private void frista(String path, String user, String password, String noKartu) throws Exception {
        WinDef.HWND utama = cariUtamaFrista();
        if (utama == null) {
            WinDef.HWND login = cariLoginFrista();
            if (login == null) {
                LoadingOverlaySMC.setMessage("Membuka aplikasi FRISTA...");
                jalankan(path);
                login = tunggu("jendela login muncul", AplikasiBiometrikSMC::cariLoginFrista);
            }

            LoadingOverlaySMC.setMessage("Login aplikasi FRISTA...");
            siapKetik(login);
            isiLogin(login, user, password);
            tekan(KeyEvent.VK_TAB);
            tekan(KeyEvent.VK_SPACE);
            utama = tunggu("login selesai", () -> null == cariLoginFrista() ? cariUtamaFrista() : null);
        }

        LoadingOverlaySMC.setMessage("Memasukkan nomor kartu...");
        siapKetik(utama);
        kosongkan(tungguKolomAktif(utama));
        tempel(noKartu);
        log("nomor kartu dimasukkan");
    }

    private static WinDef.HWND cariFingerprint() {
        return cariJendela(judul -> judul.contains(JUDUL_FINGERPRINT));
    }

    private static WinDef.HWND cariLoginFrista() {
        return cariJendela(JUDUL_LOGIN_FRISTA::equals);
    }

    private static WinDef.HWND cariUtamaFrista() {
        return cariJendela(judul -> !JUDUL_LOGIN_FRISTA.equals(judul) && judul.toLowerCase().contains(JUDUL_FRISTA));
    }

    private static WinDef.HWND cariJendela(Predicate<String> cocok) {
        WinDef.HWND[] hasil = new WinDef.HWND[1];
        User32.INSTANCE.EnumWindows((hwnd, data) -> {
            if (User32.INSTANCE.IsWindowVisible(hwnd)) {
                String judul = judul(hwnd);
                if (!judul.isEmpty() && cocok.test(judul)) {
                    hasil[0] = hwnd;
                    return false;
                }
            }
            return true;
        }, null);
        return hasil[0];
    }

    private static String judul(WinDef.HWND hwnd) {
        char[] teks = new char[512];
        User32.INSTANCE.GetWindowText(hwnd, teks, teks.length);
        return Native.toString(teks);
    }

    private static String kelas(WinDef.HWND hwnd) {
        char[] teks = new char[256];
        User32.INSTANCE.GetClassName(hwnd, teks, teks.length);
        return Native.toString(teks);
    }

    private static boolean kolomTeks(WinDef.HWND hwnd) {
        return null != hwnd && kelas(hwnd).toLowerCase().contains("edit");
    }

    private static boolean kolomSandi(WinDef.HWND hwnd) {
        if ((User32.INSTANCE.GetWindowLong(hwnd, GWL_STYLE) & ES_PASSWORD) != 0) {
            return true;
        }
        LongByReference hasil = new LongByReference();
        WinDef.LRESULT kirim = User32SMC.INSTANCE.SendMessageTimeout(hwnd, EM_GETPASSWORDCHAR, new WinDef.WPARAM(0), new WinDef.LPARAM(0), SMTO_ABORTIFHUNG, BATAS_PESAN, hasil);
        return kirim.longValue() != 0 && hasil.getValue() != 0;
    }

    private static String bacaTeks(WinDef.HWND hwnd) {
        char[] teks = new char[256];
        User32SMC.INSTANCE.SendMessageTimeout(hwnd, WM_GETTEXT, new WinDef.WPARAM(teks.length), teks, SMTO_ABORTIFHUNG, BATAS_PESAN, new LongByReference());
        return Native.toString(teks);
    }

    private static Status statusJendela(WinDef.HWND jendela) {
        Status status = statusKontrolBawaan(jendela);
        if (status != null) {
            return status;
        }

        Integer uia = PembacaUIASMC.status(jendela);
        if (uia == null) {
            return null;
        }
        return PembacaUIASMC.STATUS_LOGIN == uia ? Status.LOGIN : Status.SIAP;
    }

    private static Status statusKontrolBawaan(WinDef.HWND jendela) {
        boolean[] ada = {false, false};
        User32.INSTANCE.EnumChildWindows(jendela, (hwnd, data) -> {
            if (User32.INSTANCE.IsWindowVisible(hwnd) && kolomTeks(hwnd)) {
                ada[0] = true;
                if (kolomSandi(hwnd)) {
                    ada[1] = true;
                }
            }
            return true;
        }, null);
        if (ada[1]) {
            return Status.LOGIN;
        }
        return ada[0] ? Status.SIAP : null;
    }

    private Status tungguStatus(Callable<WinDef.HWND> cari) throws Exception {
        langkah = "membaca form aplikasi";
        long tanpaKontrolSejak = 0;
        while (true) {
            WinDef.HWND jendela = cari.call();
            if (jendela != null) {
                Status status = statusJendela(jendela);
                if (status != null) {
                    log(Status.LOGIN == status ? "form login tampil" : "sudah login");
                    return status;
                }
                if (tanpaKontrolSejak == 0) {
                    tanpaKontrolSejak = System.nanoTime();
                } else if ((System.nanoTime() - tanpaKontrolSejak) / 1_000_000 >= BATAS_TANPA_KONTROL) {
                    log("kontrol di dalam jendela tidak dapat dibaca (kelas " + kelas(jendela) + "), memakai jeda waktu");
                    return Status.TANPA_KONTROL;
                }
            }
            jeda();
        }
    }

    private <T> T tunggu(String apa, Callable<T> cek) throws Exception {
        langkah = apa;
        while (true) {
            T hasil = cek.call();
            if (hasil != null) {
                log(apa);
                return hasil;
            }
            jeda();
        }
    }

    private void jadikanDepan(WinDef.HWND jendela) throws Exception {
        langkah = "menampilkan jendela aplikasi";
        if (User32SMC.INSTANCE.IsIconic(jendela)) {
            User32.INSTANCE.ShowWindow(jendela, SW_RESTORE);
        }
        while (true) {
            WinDef.HWND depan = User32.INSTANCE.GetForegroundWindow();
            if (depan != null && prosesSama(depan, jendela) && siapInput(jendela)) {
                return;
            }
            User32.INSTANCE.SetForegroundWindow(jendela);
            jeda();
        }
    }

    private void siapKetik(WinDef.HWND jendela) throws Exception {
        jadikanDepan(jendela);
        if (null == statusJendela(jendela)) {
            jedaTetap();
        }
    }

    private static boolean prosesSama(WinDef.HWND a, WinDef.HWND b) {
        IntByReference pidA = new IntByReference();
        IntByReference pidB = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(a, pidA);
        User32.INSTANCE.GetWindowThreadProcessId(b, pidB);
        return pidA.getValue() != 0 && pidA.getValue() == pidB.getValue();
    }

    private static boolean siapInput(WinDef.HWND jendela) {
        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(jendela, pid);
        WinNT.HANDLE proses = Kernel32.INSTANCE.OpenProcess(SYNCHRONIZE | PROCESS_QUERY_LIMITED_INFORMATION, false, pid.getValue());
        if (proses == null) {
            return true;
        }
        try {
            return WAIT_TIMEOUT != User32.INSTANCE.WaitForInputIdle(proses, new WinDef.DWORD(0)).intValue();
        } finally {
            Kernel32.INSTANCE.CloseHandle(proses);
        }
    }

    private static int pid(WinDef.HWND jendela) {
        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(jendela, pid);
        return pid.getValue();
    }

    private static Kolom kolomFokus(WinDef.HWND jendela) {
        int thread = User32.INSTANCE.GetWindowThreadProcessId(jendela, null);
        WinUser.GUITHREADINFO info = new WinUser.GUITHREADINFO();
        info.cbSize = info.size();
        if (User32.INSTANCE.GetGUIThreadInfo(thread, info) && kolomTeks(info.hwndFocus)) {
            return new Kolom(info.hwndFocus, null, true, kolomSandi(info.hwndFocus));
        }

        PembacaUIASMC.Fokus fokus = PembacaUIASMC.fokus();
        if (fokus != null && fokus.pid == pid(jendela)) {
            return new Kolom(null, fokus.kunci, fokus.teks, fokus.sandi);
        }
        return Kolom.TIDAK_ADA;
    }

    private void isiLogin(WinDef.HWND jendela, String user, String password) throws Exception {
        Kolom kolomUser = kolomFokus(jendela);
        kosongkan(kolomUser);
        tempel(user);
        tungguTerisi(kolomUser, user);
        tekan(KeyEvent.VK_TAB);
        kosongkan(tungguFokusPindah(jendela, kolomUser));
        tempelSandi(password);
    }

    private Kolom tungguKolomAktif(WinDef.HWND jendela) throws Exception {
        if (null == statusJendela(jendela)) {
            return Kolom.TIDAK_ADA;
        }

        langkah = "menunggu kolom nomor kartu aktif";
        long sejak = System.nanoTime();
        while (true) {
            Kolom fokus = kolomFokus(jendela);
            if (fokus.teks) {
                return fokus;
            }
            if ((System.nanoTime() - sejak) / 1_000_000 >= BATAS_ISI_TEKS) {
                log("kolom nomor kartu tidak dapat dipastikan, melanjutkan");
                return fokus;
            }
            jeda();
        }
    }

    private void kosongkan(Kolom kolom) {
        if (kolom.hwnd != null) {
            User32SMC.INSTANCE.SendMessageTimeout(kolom.hwnd, WM_SETTEXT, new WinDef.WPARAM(0), new char[] {0}, SMTO_ABORTIFHUNG, BATAS_PESAN, new LongByReference());
        } else {
            pilihSemua();
        }
    }

    private Kolom tungguFokusPindah(WinDef.HWND jendela, Kolom dari) throws Exception {
        if (!dari.teks) {
            return Kolom.TIDAK_ADA;
        }

        langkah = "berpindah ke kolom kata sandi";
        long sejak = System.nanoTime();
        while (true) {
            Kolom fokus = kolomFokus(jendela);
            if (fokus.terbaca() && !fokus.sama(dari)) {
                return fokus;
            }
            if ((System.nanoTime() - sejak) / 1_000_000 >= BATAS_ISI_TEKS) {
                log("kolom kata sandi tidak menerima fokus, login dibatalkan");
                throw new TimeoutException(nama + ": kolom kata sandi tidak menerima fokus");
            }
            jeda();
        }
    }

    private void tungguTerisi(Kolom kolom, String teks) throws Exception {
        if (!kolom.teks || kolom.sandi) {
            jedaTetap();
            return;
        }

        langkah = "mengisi username";
        long sejak = System.nanoTime();
        while (true) {
            String isi = kolom.hwnd != null ? bacaTeks(kolom.hwnd) : PembacaUIASMC.isiFokus();
            if (isi == null) {
                jedaTetap();
                return;
            }
            if (teks.equals(isi)) {
                return;
            }
            if ((System.nanoTime() - sejak) / 1_000_000 >= BATAS_ISI_TEKS) {
                log("isi kolom username tidak dapat dipastikan, melanjutkan");
                return;
            }
            jeda();
        }
    }

    private void jalankan(String path) throws Exception {
        log("menjalankan " + path);
        Runtime.getRuntime().exec("\"" + path + "\"");
    }

    private void jeda() throws Exception {
        if (batas != 0 && System.nanoTime() > batas) {
            log("batas waktu habis saat " + langkah);
            throw new TimeoutException(nama + ": batas waktu habis saat " + langkah);
        }
        LoadingOverlaySMC.sleep(JEDA_PANTAU);
    }

    private void jedaTetap() throws Exception {
        long selesai = System.nanoTime() + JEDA_TEMPEL * 1_000_000L;
        while (System.nanoTime() < selesai) {
            jeda();
        }
    }

    private void tekan(int tombol) {
        robot.keyPress(tombol);
        robot.keyRelease(tombol);
    }

    private void pilihSemua() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        tekan(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    private void tempel(String teks) {
        StringSelection isi = new StringSelection(teks);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(isi, isi);
        sandiDiClipboard = false;
        robot.keyPress(KeyEvent.VK_CONTROL);
        tekan(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    private void tempelSandi(String sandi) {
        tempel(sandi);
        sandiDiClipboard = true;
    }

    private void bersihkanClipboard() {
        if (sandiDiClipboard) {
            StringSelection kosong = new StringSelection("");
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(kosong, kosong);
            sandiDiClipboard = false;
        }
    }

    private void log(String pesan) {
        System.out.println("Biometrik " + nama + " (" + (System.nanoTime() - mulai) / 1_000_000 + " ms) : " + pesan);
    }
}
