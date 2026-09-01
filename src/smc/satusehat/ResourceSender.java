package smc.satusehat;

import bridging.ApiSatuSehat;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CancellationException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ResourceSender extends SwingWorker<Void, Void> implements ResourceStatusWatcher {
    private final TugasSMC tugas;
    private final JProgressBar bar;
    private final JLabel keterangan;
    private final JDialog popup;
    private int nilai;
    private int total;

    private ResourceSender(Component induk, String judul, TugasSMC tugas) {
        this.tugas = tugas;

        bar = new JProgressBar(0, 1);
        bar.setStringPainted(true);

        keterangan = new JLabel("Menyiapkan data...");

        JButton batal = new JButton("Batal");

        JOptionPane panel = new JOptionPane(new Object[] {keterangan, bar}, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {batal});

        popup = panel.createDialog(induk, judul);
        popup.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        popup.setModal(true);

        batal.addActionListener(evt -> batalkan());

        popup.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                batalkan();
            }
        });

        addPropertyChangeListener(evt -> {
            if ("keterangan".equals(evt.getPropertyName())) {
                keterangan.setText((String) evt.getNewValue());
            }

            if ("total".equals(evt.getPropertyName())) {
                bar.setValue(0);
                bar.setMaximum(Math.max(1, (Integer) evt.getNewValue()));
            }

            if ("nilai".equals(evt.getPropertyName())) {
                bar.setValue((Integer) evt.getNewValue());
            }
        });
    }

    public static void run(Component induk, String judul, TugasSMC tugas) {
        ResourceSender pengirim = new ResourceSender(induk, judul, tugas);
        ApiSatuSehat.resetStoppedSmc();
        ApiSatuSehat.setResourceStatusWatcherSmc(pengirim);
        pengirim.execute();
        pengirim.popup.setVisible(true);
    }

    public void setTotal(int total) {
        nilai = 0;
        this.total = total;
        firePropertyChange("total", -1, total);
    }

    public static int countSelected(JTable tabel) {
        int terpilih = 0;
        for (int baris = 0; baris < tabel.getRowCount(); baris++) {
            if (isSelected(tabel, baris)) {
                terpilih++;
            }
        }
        return terpilih;
    }

    public static boolean isSelected(JTable tabel, int baris) {
        return "true".equals(tabel.getValueAt(baris, 0).toString());
    }

    public void increment() {
        firePropertyChange("nilai", nilai, ++nilai);
        firePropertyChange("keterangan", null, "Memproses data ke-" + nilai + " dari " + total + "...");
    }

    public void incrementIfSelected(JTable tabel, int baris) {
        if (isSelected(tabel, baris)) {
            increment();
        }
    }

    public void setValueAt(JTable tabel, Object nilai, int baris, int kolom) {
        SwingUtilities.invokeLater(() -> tabel.setValueAt(nilai, baris, kolom));
    }

    @Override
    public boolean isProcessStopped() {
        return isCancelled();
    }

    @Override
    public void retryUntil(int percobaan, long sisaDetik) {
        firePropertyChange("keterangan", null, "Permintaan dibatasi Satu Sehat, menunggu " + sisaDetik + " detik untuk percobaan ke-" + (percobaan + 1) + "...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            tugas.jalankanSmc(this);
        } finally {
            removeListeners();
        }
        return null;
    }

    @Override
    protected void done() {
        popup.dispose();
        try {
            get();
            if (ApiSatuSehat.isStoppedSmc()) {
                JOptionPane.showMessageDialog(null, "Permintaan ke server Satu Sehat masih dibatasi (kode " + ApiSatuSehat.TOO_MANY_REQUESTS_SMC + ") setelah beberapa kali menunggu. Sisa data belum terkirim dan bisa dilanjutkan lagi nanti...!!!!");
            } else {
                JOptionPane.showMessageDialog(null, "Proses pengiriman selesai...!!!!");
            }
        } catch (CancellationException e) {
            JOptionPane.showMessageDialog(null, "Proses pengiriman dihentikan...!!!!");
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengirim data ke Satu Sehat\nProses dibatalkan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeListeners() {
        if (this == ApiSatuSehat.getResourceStatusWatcherSmc()) {
            ApiSatuSehat.setResourceStatusWatcherSmc(null);
        }
    }

    private void batalkan() {
        if (JOptionPane.showConfirmDialog(null, "Batalkan proses pengiriman?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            cancel(false);
        }
    }

    public interface TugasSMC {
        void jalankanSmc(ResourceSender pengirim) throws Exception;
    }
}
