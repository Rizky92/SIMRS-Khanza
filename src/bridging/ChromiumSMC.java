package bridging;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefBuildInfo;
import me.friwi.jcefmaven.EnumPlatform;
import me.friwi.jcefmaven.IProgressHandler;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.step.check.CefInstallationChecker;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefAuthCallback;
import org.cef.handler.CefRequestHandlerAdapter;
import widget.Label;

public class ChromiumSMC {
    private static final boolean OFFSCREEN = false;

    private static CefApp app = null;
    private static String pesan = "";
    private static boolean kaitMatikan = false;

    private CefClient client = null;
    private CefBrowser browser = null;

    public static String pesan() {
        return pesan;
    }

    public static boolean didukung() {
        try {
            EnumPlatform.getCurrentPlatform();
            return true;
        } catch (Exception e) {
            pesan = "Chromium belum mendukung " + System.getProperty("os.name") + " " + System.getProperty("os.arch");
            return false;
        }
    }

    public static boolean siap() {
        return null != app;
    }

    public static File direktoriPemasangan() {
        String induk = System.getenv("ProgramData");
        if ((null == induk) || (induk.isBlank())) {
            induk = System.getProperty("user.home");
        }

        return new File(induk + File.separator + "SIMRSKhanza" + File.separator + "jcef" + File.separator + versi());
    }

    public static File direktoriTembolok() {
        String induk = System.getenv("LOCALAPPDATA");
        if ((null == induk) || (induk.isBlank())) {
            induk = System.getProperty("java.io.tmpdir");
        }

        return new File(induk + File.separator + "SIMRSKhanza" + File.separator + "jcef-cache");
    }

    public static boolean terpasang() {
        try {
            return CefInstallationChecker.checkInstallation(direktoriPemasangan());
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized boolean siapkan(IProgressHandler progres) {
        if (null != app) {
            return true;
        }

        if (!didukung()) {
            return false;
        }

        try {
            File tembolok = direktoriTembolok();
            tembolok.mkdirs();

            CefAppBuilder builder = new CefAppBuilder();
            builder.setInstallDir(direktoriPemasangan());
            builder.getCefSettings().windowless_rendering_enabled = OFFSCREEN;
            builder.getCefSettings().root_cache_path = tembolok.getAbsolutePath();
            builder.getCefSettings().cache_path = tembolok.getAbsolutePath();

            if (null != progres) {
                builder.setProgressHandler(progres);
            }

            List<String> cermin = daftarCermin();
            if (!cermin.isEmpty()) {
                System.out.println("Cermin Chromium : " + cermin);
                builder.setMirrors(cermin);
            }

            app = builder.build();
            pasangKaitMatikan();
            pesan = "";

            return true;
        } catch (Exception e) {
            System.out.println("Notifikasi : Gagal menyiapkan Chromium : " + e);
            pesan = pesanGagal(e);
            app = null;
            bersihkanPemasanganGagal();

            return false;
        }
    }

    public static boolean siapkanDenganDialog(Component induk) {
        if (null != app) {
            return true;
        }

        if (!didukung()) {
            JOptionPane.showMessageDialog(induk, pesan, "Chromium", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!terpasang()) {
            int pilihan = JOptionPane.showConfirmDialog(induk,
                    "Mesin Chromium untuk viewer ini belum terpasang di komputer.\n"
                    + "Unduh sekarang ? Ukuran sekitar 150 MB, hanya sekali per komputer.\n\n"
                    + "Lokasi : " + direktoriPemasangan().getAbsolutePath(),
                    "Pemasangan Chromium", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (JOptionPane.YES_OPTION != pilihan) {
                return false;
            }
        }

        Window pemilik = SwingUtilities.getWindowAncestor(induk);
        JDialog dialog = new JDialog(pemilik, "Menyiapkan Chromium", Dialog.ModalityType.APPLICATION_MODAL);
        JProgressBar bilah = new JProgressBar(0, 100);
        Label keterangan = new Label();
        JPanel isi = new JPanel(new BorderLayout(8, 8));
        final boolean[] hasil = new boolean[]{false};

        bilah.setStringPainted(true);
        keterangan.setText("Memeriksa pemasangan...");
        isi.add(keterangan, BorderLayout.NORTH);
        isi.add(bilah, BorderLayout.CENTER);
        isi.setPreferredSize(new Dimension(420, 70));
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().add(isi, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(induk);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evt) {
                Thread kerja = new Thread(() -> {
                    hasil[0] = siapkan((keadaan, persen) -> SwingUtilities.invokeLater(() -> {
                        keterangan.setText("Chromium : " + keadaan);
                        if (0 > persen) {
                            bilah.setIndeterminate(true);
                        } else {
                            bilah.setIndeterminate(false);
                            bilah.setValue(Math.round(persen));
                        }
                    }));
                    SwingUtilities.invokeLater(() -> dialog.dispose());
                });
                kerja.setDaemon(true);
                kerja.start();
            }
        });

        dialog.setVisible(true);

        if (!hasil[0]) {
            JOptionPane.showMessageDialog(induk, pesan, "Chromium", JOptionPane.WARNING_MESSAGE);
        }

        return hasil[0];
    }

    public Component komponen(String url) {
        client = app.createClient();
        client.addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public boolean getAuthCredentials(CefBrowser peramban, String asal, boolean proksi, String host, int port, String realm, String skema, CefAuthCallback callback) {
                if (proksi) {
                    callback.cancel();
                    return false;
                }

                callback.Continue(koneksiDB.USERORTHANC(), koneksiDB.PASSORTHANC());
                return true;
            }
        });
        browser = client.createBrowser(url, OFFSCREEN, false);

        return browser.getUIComponent();
    }

    public void muat(String url) {
        if (null != browser) {
            browser.loadURL(url);
        }
    }

    public void tutup() {
        try {
            if (null != browser) {
                browser.close(true);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        try {
            if (null != client) {
                client.dispose();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        browser = null;
        client = null;
    }

    public static synchronized void matikan() {
        try {
            if (null != app) {
                app.dispose();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        app = null;
    }

    private static synchronized void pasangKaitMatikan() {
        if (kaitMatikan) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> matikan()));
        kaitMatikan = true;
    }

    private static String pesanGagal(Exception e) {
        StringBuilder rincian = new StringBuilder();
        for (Throwable t = e; null != t; t = t.getCause()) {
            rincian.append(t.getClass().getName()).append(" ").append((null == t.getMessage()) ? "" : t.getMessage()).append(" ");
        }
        final String jejak = rincian.toString().toLowerCase();

        if (e instanceof UnsupportedPlatformException) {
            return "Maaf, Chromium belum mendukung " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + ".";
        }

        if ((jejak.contains("mirror")) || (jejak.contains("unknownhost")) || (jejak.contains("connectexception"))
                || (jejak.contains("sockettimeout")) || (jejak.contains("sslexception")) || (jejak.contains("connection reset"))
                || (jejak.contains("no route to host")) || (jejak.contains("proxy"))) {
            return "Maaf, mesin Chromium tidak bisa diunduh saat ini.\n"
                    + "Silahkan periksa koneksi jaringan lalu coba lagi, atau hubungi administrator.";
        }

        if ((jejak.contains("accessdenied")) || (jejak.contains("access is denied")) || (jejak.contains("permission denied"))
                || (jejak.contains("read-only"))) {
            return "Maaf, tidak bisa menulis ke folder " + direktoriPemasangan().getAbsolutePath() + ".\n"
                    + "Silahkan hubungi administrator untuk hak akses folder tersebut.";
        }

        if ((jejak.contains("no space")) || (jejak.contains("not enough space")) || (jejak.contains("enospc"))) {
            return "Maaf, ruang penyimpanan tidak cukup untuk memasang Chromium.\n"
                    + "Mesin Chromium memerlukan sekitar 500 MB ruang kosong.";
        }

        return "Maaf, mesin Chromium gagal disiapkan.\nSilahkan coba lagi atau hubungi administrator.";
    }

    private static void bersihkanPemasanganGagal() {
        File direktori = direktoriPemasangan();

        if (!direktori.isDirectory()) {
            return;
        }

        if (terpasang()) {
            System.out.println("Notifikasi : Pemasangan Chromium masih utuh, tidak dibersihkan");
            return;
        }

        if (!amanDihapus(direktori)) {
            System.out.println("Notifikasi : Lokasi tidak aman untuk dibersihkan : " + direktori.getAbsolutePath());
            return;
        }

        System.out.println("Notifikasi : Membersihkan pemasangan Chromium yang gagal : " + direktori.getAbsolutePath());
        hapus(direktori);
    }

    private static boolean amanDihapus(File direktori) {
        String jalur = direktori.getAbsolutePath().replace('\\', '/');
        return (jalur.contains("/SIMRSKhanza/jcef/")) && (!jalur.endsWith("/jcef/"));
    }

    private static void hapus(File berkas) {
        File[] isi = berkas.listFiles();

        if (null != isi) {
            for (File anak : isi) {
                hapus(anak);
            }
        }

        if (!berkas.delete()) {
            System.out.println("Notifikasi : Gagal menghapus " + berkas.getAbsolutePath());
        }
    }

    private static String versi() {
        try {
            return CefBuildInfo.fromClasspath().getReleaseTag().replaceAll("[^A-Za-z0-9.\\-]", "_");
        } catch (Exception e) {
            return "bawaan";
        }
    }

    private static List<String> daftarCermin() {
        List<String> cermin = new ArrayList<>();
        String isi = koneksiDB.ORTHANCVIEWERMIRRORSMC();

        if (!isi.isBlank()) {
            for (String baris : isi.split(",")) {
                if (!baris.isBlank()) {
                    cermin.add(baris.trim());
                }
            }
        }

        return cermin;
    }
}
