/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import com.formdev.flatlaf.extras.components.FlatLabel;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;
import widget.Button;
import widget.Tanggal;

/**
 *
 * @author Owner
 */
public final class validasi {
    public static final int POPUPTYPE_KONFIRM = 1;
    public static final int POPUPTYPE_INFORMASI = 2;

    public static final int POPUP_YA = 1;
    public static final int POPUP_TIDAK = 0;
    public static final int POPUP_OKE = -1;

    private final Connection connect = koneksiDB.condb();

    public validasi() {
        super();
    }

    public String getTglSmc(Tanggal tgl) {
        return new SimpleDateFormat("yyyy-MM-dd").format(tgl.getDate());
    }

    public void printReportSmc(String reportName, String reportDirName, String judul, Map reportParams, String printerName, int jumlah, String sql, String... values) {
        try {
            try (PreparedStatement ps = connect.prepareStatement(sql)) {
                for (int i = 0; i < values.length; i++) {
                    ps.setString(i + 1, values[i]);
                }

                JasperPrint jp = JasperFillManager.fillReport("./" + reportDirName + "/" + reportName, reportParams, new JRResultSetDataSource(ps.executeQuery()));

                PrintService printService = null;
                for (PrintService currentPrintService : PrintServiceLookup.lookupPrintServices(null, null)) {
                    if (currentPrintService.getName().equals(printerName)) {
                        printService = currentPrintService;
                        break;
                    }
                }

                if (printService != null) {
                    PrintRequestAttributeSet pra = new HashPrintRequestAttributeSet();
                    pra.add(new Copies(jumlah));

                    SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();

                    config.setPrintService(printService);
                    config.setPrintRequestAttributeSet(pra);
                    config.setPrintServiceAttributeSet(printService.getAttributes());
                    config.setDisplayPageDialog(false);
                    config.setDisplayPrintDialog(false);

                    JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                    exporter.setExporterInput(new SimpleExporterInput(jp));
                    exporter.setConfiguration(config);
                    exporter.exportReport();

                    if (koneksiDB.PREVIEWHASILPRINT()) {
                        JasperViewer jv = new JasperViewer(jp, false);
                        jv.setTitle(judul);
                        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                        jv.setSize(screen.width - 50, screen.height - 50);
                        jv.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                        jv.setLocationRelativeTo(null);
                        jv.setVisible(true);
                    }
                } else {
                    popupInfoDialog("Printer tidak ditemukan!");
                    JasperViewer jv = new JasperViewer(jp, false);
                    jv.setTitle(judul);
                    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                    jv.setSize(screen.width - 50, screen.height - 50);
                    jv.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                    jv.setLocationRelativeTo(null);
                    jv.setVisible(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            popupGagalDialog("Terjadi kesalahan pada saat melakukan proses cetak..!!");
        }
    }

    public void printReportSmc(String reportName, String reportDirName, String judul, Map reportParams, String printerName, int jumlah) {
        try {
            JasperPrint jp = JasperFillManager.fillReport("./" + reportDirName + "/" + reportName, reportParams, connect);

            PrintService printService = null;
            for (PrintService currentPrintService : PrintServiceLookup.lookupPrintServices(null, null)) {
                if (currentPrintService.getName().equals(printerName)) {
                    printService = currentPrintService;
                    break;
                }
            }

            if (printService != null) {
                PrintRequestAttributeSet pra = new HashPrintRequestAttributeSet();
                pra.add(new Copies(jumlah));

                SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();

                config.setPrintService(printService);
                config.setPrintRequestAttributeSet(pra);
                config.setPrintServiceAttributeSet(printService.getAttributes());
                config.setDisplayPageDialog(false);
                config.setDisplayPrintDialog(false);

                JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                exporter.setExporterInput(new SimpleExporterInput(jp));
                exporter.setConfiguration(config);
                exporter.exportReport();

                if (koneksiDB.PREVIEWHASILPRINT()) {
                    JasperViewer jv = new JasperViewer(jp, false);
                    jv.setTitle(judul);
                    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                    jv.setSize(screen.width - 50, screen.height - 50);
                    jv.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                    jv.setLocationRelativeTo(null);
                    jv.setVisible(true);
                }
            } else {
                popupInfoDialog("Printer tidak ditemukan!");
                JasperViewer jv = new JasperViewer(jp, false);
                jv.setTitle(judul);
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                jv.setSize(screen.width - 50, screen.height - 50);
                jv.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                jv.setLocationRelativeTo(null);
                jv.setVisible(true);
            }
        } catch (JRException e) {
            System.out.println("Notif : " + e);
            popupGagalDialog("Terjadi kesalahan pada saat melakukan proses cetak..!!");
        }
    }

    public int popupDialog(String judul, String pesan, int popupType, int messageType, int timeout) {
        FlatLabel message = new FlatLabel();
        message.setForeground(new Color(0, 131, 62));
        message.setFont(new java.awt.Font("Inter Medium", Font.PLAIN, 18));
        message.setText("<html><body>" + pesan.replace("\n", "<br>") + "</body></html>");

        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);
        pane.setInitialValue(POPUP_OKE);

        Button ya = new Button();
        ya.setFont(new java.awt.Font("Inter", Font.BOLD, 18));
        ya.setPreferredSize(new Dimension(100, 35));
        ya.setForeground(new Color(255, 255, 255));
        ya.setBackground(new Color(0, 131, 62));
        ya.setText("Ya");
        ya.addActionListener(e -> pane.setValue(POPUP_YA));

        Button tidak = new Button();
        tidak.setFont(new java.awt.Font("Inter Medium", Font.PLAIN, 18));
        tidak.setPreferredSize(new Dimension(90, 35));
        tidak.setForeground(new Color(255, 30, 0));
        tidak.setBackground(new Color(255, 255, 255));
        tidak.setText("Tidak");
        tidak.addActionListener(e -> pane.setValue(POPUP_TIDAK));

        Button oke = new Button();
        oke.setFont(new java.awt.Font("Inter Medium", Font.PLAIN, 18));
        oke.setPreferredSize(new Dimension(100, 35));
        oke.setForeground(new Color(0, 131, 62));
        oke.setBackground(new Color(255, 255, 255));
        oke.setText("Oke");
        oke.addActionListener(e -> pane.setValue(POPUP_OKE));

        if (popupType == POPUPTYPE_KONFIRM) {
            pane.setOptions(new Object[] {ya, tidak});
        } else if (popupType == POPUPTYPE_INFORMASI) {
            pane.setOptions(new Object[] {oke});
        }

        JDialog popup = pane.createDialog(judul);
        popup.setModal(true);

        if (timeout > 0) {
            Timer timer = new Timer(timeout * 1000, e -> popup.dispose());
            timer.setRepeats(false);
            timer.start();
        }

        popup.setVisible(true);

        return (Integer) (pane.getValue() == null || pane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE) ? -1 : pane.getValue());
    }

    public int popupInfoDialog(String pesan, int timeout) {
        return popupDialog("Informasi", pesan, POPUPTYPE_INFORMASI, JOptionPane.INFORMATION_MESSAGE, timeout);
    }

    public int popupInfoDialog(String pesan) {
        return popupInfoDialog(pesan, 0);
    }

    public int popupPeringatanDialog(String pesan, int timeout) {
        return popupDialog("Peringatan", pesan, POPUPTYPE_INFORMASI, JOptionPane.WARNING_MESSAGE, timeout);
    }

    public int popupPeringatanDialog(String pesan) {
        return popupPeringatanDialog(pesan, 0);
    }

    public int popupGagalDialog(String pesan, int timeout) {
        return popupDialog("Gagal", pesan, POPUPTYPE_INFORMASI, JOptionPane.ERROR_MESSAGE, timeout);
    }

    public int popupGagalDialog(String pesan) {
        return popupGagalDialog(pesan, 0);
    }

    public void pindahSmc(KeyEvent evt, JComponent kiri, JComponent kanan) {
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            kiri.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            kanan.requestFocus();
        }
    }

    public void tabelKosongSmc(DefaultTableModel model, boolean fireEvent) {
        model.getDataVector().removeAllElements();
        if (fireEvent) {
            model.fireTableDataChanged();
        }
    }

    public void tabelKosongSmc(DefaultTableModel model) {
        tabelKosongSmc(model, true);
    }

    public void teksKosongSmc(JComponent component, String nama) {
        popupPeringatanDialog("Maaf, " + nama + " tidak boleh kosong..!!", 3);
        component.requestFocus();
    }
}
