package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.HtmlUtils;
import rekammedis.RMRiwayatPerawatan;
import simrskhanza.DlgCariCaraBayar;

public class BPJSKompilasiBerkasKlaim extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeKelahiran;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final JFXPanel jfxINACBG = new JFXPanel(),
        jfxBerkasDigital = new JFXPanel();
    private final DlgCariCaraBayar penjab = new DlgCariCaraBayar(null, false);
    private final String KOMPILASIBERKASGUNAKANRIWAYATPASIEN = koneksiDB.KOMPILASIBERKASGUNAKANRIWAYATPASIEN(),
        KODEPJBPJS = Sequel.cariIsiSmc("select password_asuransi.kd_pj from password_asuransi"),
        NAMAPJBPJS = Sequel.cariIsiSmc("select penjab.png_jawab from penjab where penjab.kd_pj = ?", KODEPJBPJS),
        KODEPPKBPJS = Sequel.cariIsiSmc("select setting.kode_ppk from setting limit 1") + "%";
    private RMRiwayatPerawatan resume = null;
    private WebEngine engineKlaim, engineBerkasDigital;
    private String finger = "", tanggalExport = "", gunakanTanggalExport = koneksiDB.KOMPILASIBERKASGUNAKANTANGGALEXPORT(),
        aplikasiPDF = koneksiDB.KOMPILASIBERKASAPLIKASIPDF(), kategoriUploadBerkas = "", kamar = "", unit = "",
        asalRujukanDipilih = "", naikKelasDipilih = "", onsetKontraksiDipilih = "";
    private boolean isLoading = false, hapusOtomatisDiagnosaProsedur = false;
    private int flagklaim = -1, flagInacbgTopup = -1, selectedRow = -1;
    private long maxMemory = koneksiDB.KOMPILASIBERKASMAXMEMORY();

    public BPJSKompilasiBerkasKlaim(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        btnInvoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tabMode = new DefaultTableModel(null, new Object[] {
            "P", "No. Rawat", "No. SEP", "No. RM", "Nama Pasien", "Status Rawat",
            "Tgl. SEP", "Tgl. Pulang SEP", "Status Pulang", "Unit/Poli", "DPJP",
            "Status Klaim", "statusklaim"
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 12) {
                    return Integer.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbKompilasi.setModel(tabMode);
        tbKompilasi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKompilasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbKompilasi.getColumnModel().getColumn(0).setPreferredWidth(23);
        tbKompilasi.getColumnModel().getColumn(1).setPreferredWidth(10);
        tbKompilasi.getColumnModel().getColumn(2).setPreferredWidth(130);
        tbKompilasi.getColumnModel().getColumn(3).setPreferredWidth(50);
        tbKompilasi.getColumnModel().getColumn(4).setPreferredWidth(200);
        tbKompilasi.getColumnModel().getColumn(5).setPreferredWidth(50);
        tbKompilasi.getColumnModel().getColumn(6).setPreferredWidth(75);
        tbKompilasi.getColumnModel().getColumn(7).setPreferredWidth(75);
        tbKompilasi.getColumnModel().getColumn(8).setPreferredWidth(80);
        tbKompilasi.getColumnModel().getColumn(9).setPreferredWidth(180);
        tbKompilasi.getColumnModel().getColumn(10).setPreferredWidth(150);
        tbKompilasi.getColumnModel().getColumn(11).setPreferredWidth(100);
        tbKompilasi.getColumnModel().getColumn(12).setMinWidth(0);
        tbKompilasi.getColumnModel().getColumn(12).setMaxWidth(0);
        tbKompilasi.setDefaultRenderer(Object.class, new WarnaTable() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                switch ((Integer) table.getValueAt(row, 12)) {
                    case 1:
                        component.setBackground(new Color(50, 50, 50));
                        component.setForeground(new Color(255, 255, 255));
                        break;
                    case 2:
                    case 3:
                        component.setBackground(new Color(180, 240, 140));
                        component.setForeground(new Color(65, 60, 40));
                        break;
                    case 4:
                    case 5:
                        component.setBackground(new Color(30, 230, 255));
                        component.setForeground(new Color(45, 40, 55));
                        break;
                }
                return component;
            }
        });

        tabModeKelahiran = new DefaultTableModel(null, new String[] {
            "No.", "Cara Kelahiran", "Tgl.", "Jam", "Letak Janin", "Kondisi", "Manual", "Forcep", "Vacuum",
            "Spesimen SHK", "Lokasi Spesimen", "Tgl. Pengambilan", "Jam Pengambilan", "Alasan Tidak Diambil"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        tbKelahiran.setModel(tabModeKelahiran);
        tbKelahiran.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKelahiran.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbKelahiran.getColumnModel().getColumn(i);
            switch (i) {
                case 0: column.setPreferredWidth(30); break;
                case 1: column.setPreferredWidth(90); break;
                case 2: column.setPreferredWidth(60); break;
                case 3: column.setPreferredWidth(60); break;
                case 4: column.setPreferredWidth(80); break;
                case 5: column.setPreferredWidth(80); break;
                case 6: column.setPreferredWidth(50); break;
                case 7: column.setPreferredWidth(50); break;
                case 8: column.setPreferredWidth(50); break;
                case 9: column.setPreferredWidth(90); break;
                case 10: column.setPreferredWidth(70); break;
                case 11: column.setPreferredWidth(60); break;
                case 12: column.setPreferredWidth(60); break;
                case 13: column.setPreferredWidth(90); break;
            }
        }

        // selection is handled via mouseReleased and keyReleased

        Platform.runLater(() -> {
            WebView viewKlaim = new WebView(),
                viewBerkasDigital = new WebView();

            ProgressBar progressBarKlaim = new ProgressBar(0),
                progressBarBerkasDigital = new ProgressBar(0);

            progressBarKlaim.setMaxWidth(Double.MAX_VALUE);
            progressBarKlaim.setPrefHeight(10);
            progressBarBerkasDigital.setMaxWidth(Double.MAX_VALUE);
            progressBarBerkasDigital.setPrefHeight(10);

            engineKlaim = viewKlaim.getEngine();
            engineKlaim.setJavaScriptEnabled(true);
            engineKlaim.setCreatePopupHandler(popup -> viewKlaim.getEngine());
            engineKlaim.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
                if (engineKlaim.getLoadWorker().getState() == FAILED) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, engineKlaim.getLocation() + "\n" + (newValue != null ? newValue.getMessage() : "Unexpected error!"), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            });

            engineKlaim.locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.toLowerCase().contains("action")) {
                    SwingUtilities.invokeLater(() -> {
                        if (selectedRow >= 0) {
                            setFlagKlaim();
                            switch (flagklaim) {
                                case 1:
                                    tabMode.setValueAt("Selesai", selectedRow, 11);
                                    tabMode.setValueAt(1, selectedRow, 12);
                                    tabMode.fireTableRowsUpdated(selectedRow, selectedRow);
                                    break;
                                case 2:
                                    tabMode.setValueAt("INACBG Final", selectedRow, 11);
                                    tabMode.setValueAt(2, selectedRow, 12);
                                    tabMode.fireTableRowsUpdated(selectedRow, selectedRow);
                                    break;
                                case 3:
                                    tabMode.setValueAt("INACBG Grouping", selectedRow, 11);
                                    tabMode.setValueAt(3, selectedRow, 12);
                                    tabMode.fireTableRowsUpdated(selectedRow, selectedRow);
                                    break;
                                case 4:
                                    tabMode.setValueAt("IDRG Final", selectedRow, 11);
                                    tabMode.setValueAt(4, selectedRow, 12);
                                    tabMode.fireTableRowsUpdated(selectedRow, selectedRow);
                                    break;
                                case 5:
                                    tabMode.setValueAt("IDRG Grouping", selectedRow, 11);
                                    tabMode.setValueAt(5, selectedRow, 12);
                                    tabMode.fireTableRowsUpdated(selectedRow, selectedRow);
                                    break;
                                default:
                                    tabMode.setValueAt("Belum", selectedRow, 11);
                                    tabMode.setValueAt(6, selectedRow, 12);
                                    tabMode.fireTableRowsUpdated(selectedRow, selectedRow);
                                    break;
                            }
                        }
                    });
                }
            });

            engineBerkasDigital = viewBerkasDigital.getEngine();
            engineBerkasDigital.setJavaScriptEnabled(true);
            engineBerkasDigital.setCreatePopupHandler(popup -> viewBerkasDigital.getEngine());
            engineBerkasDigital.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
                if (engineBerkasDigital.getLoadWorker().getState() == FAILED) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, engineBerkasDigital.getLocation() + "\n" + (newValue != null ? newValue.getMessage() : "Unexpected error!"), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            });

            engineBerkasDigital.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    try {
                        if (engineBerkasDigital.getLocation().replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/", "").contains("berkasrawat/pages")) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            Valid.panggilUrl(engineBerkasDigital.getLocation().replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/pages/upload/", "berkasrawat/").replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/pages/upload/", "berkasrawat/"));
                            engineBerkasDigital.executeScript("history.back()");
                            setCursor(Cursor.getDefaultCursor());
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                }
            });

            progressBarKlaim.progressProperty().bind(engineKlaim.getLoadWorker().progressProperty());
            progressBarBerkasDigital.progressProperty().bind(engineBerkasDigital.getLoadWorker().progressProperty());

            BorderPane layoutKlaim = new BorderPane(viewKlaim),
                layoutBerkasDigital = new BorderPane(viewBerkasDigital);

            layoutKlaim.setTop(progressBarKlaim);
            layoutBerkasDigital.setTop(progressBarBerkasDigital);

            jfxINACBG.setScene(new Scene(layoutKlaim));
            jfxBerkasDigital.setScene(new Scene(layoutBerkasDigital));
        });
        PanelContentINACBG.add(jfxINACBG, BorderLayout.CENTER);
        PanelBerkasDigital.add(jfxBerkasDigital, BorderLayout.CENTER);

        HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule("body{width:100vw}table{width:100%;border:0px;margin:0px;padding:0px}tr,td{margin:2px 0px 2px 0px;padding:0px}td{font-family:Tahoma;font-size:10px;color:#111111}");
        Document doc = kit.createDefaultDocument();
        loadBillingHTML.setEditorKit(kit);
        loadBillingHTML.setDocument(doc);

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }

        /*
        if (((JTabbedPane) e.getSource()).getSelectedIndex() == 0) {
            if (flagklaim <= 4) {
                BtnSimpanKoding.setEnabled(false);
                BtnHapusKoding.setEnabled(false);
            } else {
                if (panelIdrg.getTabbedPane().getSelectedIndex() == 0) {
                    BtnSimpanKoding.setEnabled(true);
                    BtnHapusKoding.setEnabled(false);
                } else {
                    BtnSimpanKoding.setEnabled(false);
                    BtnHapusKoding.setEnabled(true);
                }
            }
        } else if (((JTabbedPane) e.getSource()).getSelectedIndex() == 1) {
            if (flagklaim <= 2) {
                BtnSimpanKoding.setEnabled(false);
                BtnHapusKoding.setEnabled(false);
            } else {
                if (panelInacbg.getTabbedPane().getSelectedIndex() == 0) {
                    BtnSimpanKoding.setEnabled(true);
                    BtnHapusKoding.setEnabled(false);
                } else {
                    BtnSimpanKoding.setEnabled(false);
                    BtnHapusKoding.setEnabled(true);
                }
            }
        }
        */

        panelIdrg.setNextFocusableComponent(BtnSimpanKoding);
        panelIdrg.addDiagnosaBerubahListener(() -> {
            setFlagKlaim();
            tampilINACBG();
        });
        panelIdrg.addProsedurBerubahListener(() -> {
            setFlagKlaim();
            tampilINACBG();
        });
        panelIdrg.getTabbedPane().addChangeListener(e -> {
            if (flagklaim <= 4) {
                BtnSimpanKoding.setEnabled(false);
                BtnHapusKoding.setEnabled(false);
            } else {
                if (((JTabbedPane) e.getSource()).getSelectedIndex() == 0) {
                    BtnSimpanKoding.setEnabled(true);
                    BtnHapusKoding.setEnabled(false);
                } else {
                    BtnSimpanKoding.setEnabled(false);
                    BtnHapusKoding.setEnabled(true);
                }
            }
        });

        panelInacbg.setNextFocusableComponent(BtnSimpanKoding);
        panelInacbg.addDiagnosaBerubahListener(() -> {
            setFlagKlaim();
            tampilINACBG();
        });
        panelInacbg.addProsedurBerubahListener(() -> {
            setFlagKlaim();
            tampilINACBG();
        });

        panelInacbg.getTabbedPane().addChangeListener(e -> {
            if (flagklaim <= 2) {
                BtnSimpanKoding.setEnabled(false);
                BtnHapusKoding.setEnabled(false);
            } else {
                if (((JTabbedPane) e.getSource()).getSelectedIndex() == 0) {
                    BtnSimpanKoding.setEnabled(true);
                    BtnHapusKoding.setEnabled(false);
                } else {
                    BtnSimpanKoding.setEnabled(false);
                    BtnHapusKoding.setEnabled(true);
                }
            }
        });

        penjab.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (penjab.getTable().getSelectedRow() != -1) {
                    kodePJ.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(), 1).toString());
                    namaPJ.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(), 2).toString());
                    tampil();
                }
                kodePJ.requestFocus();
            }
        });

        penjab.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    penjab.dispose();
                }
            }
        });

        TMaxMemory.setDocument(new batasInput(4).getOnlyAngka(TMaxMemory));

        cekPengaturanKompilasi();

        emptTeks();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppPilihSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ppUpdateTanggalPulangSEP = new javax.swing.JMenuItem();
        WindowUpdatePulang = new javax.swing.JDialog();
        internalFrame11 = new widget.InternalFrame();
        BtnCloseIn8 = new widget.Button();
        BtnSimpan8 = new widget.Button();
        jLabel44 = new widget.Label();
        TanggalPulang = new widget.Tanggal();
        jLabel46 = new widget.Label();
        StatusPulang = new widget.ComboBox();
        jLabel47 = new widget.Label();
        NoSuratKematian = new widget.TextBox();
        jLabel48 = new widget.Label();
        TanggalKematian = new widget.Tanggal();
        jLabel49 = new widget.Label();
        NoLPManual = new widget.TextBox();
        jLabel9 = new widget.Label();
        TNoRwPulang = new widget.TextBox();
        TNoRMPulang = new widget.TextBox();
        TPasienPulang = new widget.TextBox();
        jLabel41 = new widget.Label();
        TNoSEPRanapPulang = new widget.TextBox();
        jLabel10 = new widget.Label();
        WindowPengaturan = new javax.swing.JDialog();
        internalFrame12 = new widget.InternalFrame();
        panelBiasa4 = new widget.PanelBiasa();
        BtnBukaFolderExport = new widget.Button();
        CmbPilihanAplikasiPDF = new widget.ComboBox();
        jLabel12 = new widget.Label();
        TPathAplikasiPDF = new widget.TextBox();
        jLabel42 = new widget.Label();
        CmbPilihanTanggalExport = new widget.ComboBox();
        BtnPilihAplikasiPDF = new widget.Button();
        TMaxMemory = new widget.TextBox();
        jLabel16 = new widget.Label();
        jLabel22 = new widget.Label();
        CekAktifkanHapusOtomatis = new widget.CekBox();
        jLabel18 = new widget.Label();
        CmbPilihanKategoriBerkas = new widget.ComboBox();
        panelBiasa5 = new widget.PanelBiasa();
        BtnSimpanPengaturan = new widget.Button();
        BtnResetPengaturan = new widget.Button();
        BtnTutupPengaturan = new widget.Button();
        fc = new javax.swing.JFileChooser();
        jLabel63 = new widget.Label();
        beratBadanLahir = new widget.TextBox();
        WindowInputKelahiran = new javax.swing.JDialog();
        internalFrame2 = new widget.InternalFrame();
        panelAtasKelahiran = new widget.panelisi();
        jLabel115 = new widget.Label();
        urutanKelahiran = new widget.TextBox();
        jLabel116 = new widget.Label();
        caraLahir = new widget.ComboBox();
        jLabel118 = new widget.Label();
        waktuKelahiran = new widget.Tanggal();
        jLabel117 = new widget.Label();
        letakJanin = new widget.ComboBox();
        jLabel119 = new widget.Label();
        kondisiLahir = new widget.ComboBox();
        jLabel120 = new widget.Label();
        useManual = new widget.ComboBox();
        jLabel121 = new widget.Label();
        useForcep = new widget.ComboBox();
        jLabel122 = new widget.Label();
        useVacuum = new widget.ComboBox();
        jLabel123 = new widget.Label();
        spesimenSHKDiambil = new widget.ComboBox();
        jLabel124 = new widget.Label();
        waktuPengambilanSHK = new widget.Tanggal();
        jLabel125 = new widget.Label();
        lokasiSpesimen = new widget.ComboBox();
        jLabel126 = new widget.Label();
        alasanSpesimenTidakDiambil = new widget.ComboBox();
        cmbJamKelahiran = new widget.ComboBox();
        cmbMenitKelahiran = new widget.ComboBox();
        cmbDetikKelahiran = new widget.ComboBox();
        jLabel127 = new widget.Label();
        jLabel128 = new widget.Label();
        cmbJamSpesimen = new widget.ComboBox();
        cmbMenitSpesimen = new widget.ComboBox();
        cmbDetikSpesimen = new widget.ComboBox();
        scrollPane3 = new widget.ScrollPane();
        tbKelahiran = new widget.Table();
        panelBawahKelahiran = new widget.panelisi();
        btnSimpanKelahiran = new widget.Button();
        btnBatalKelahiran = new widget.Button();
        btnEditKelahiran = new widget.Button();
        btnHapusKelahiran = new widget.Button();
        jLabel13 = new widget.Label();
        LCountKelahiran = new widget.Label();
        btnKeluarKelahiran = new widget.Button();
        BtnSimpanKoding = new widget.Button();
        BtnHapusKoding = new widget.Button();
        internalFrame1 = new widget.InternalFrame();
        jPanel1 = new javax.swing.JPanel();
        Scroll = new widget.ScrollPane();
        tbKompilasi = new widget.Table();
        jPanel5 = new javax.swing.JPanel();
        ChkAccor = new widget.CekBox();
        tabKanan = new widget.TabPane();
        panelInvoices = new widget.panelisi();
        panelBiasa2 = new widget.PanelBiasa();
        panelBiasa1 = new widget.PanelBiasa();
        jLabel17 = new widget.Label();
        btnSEP = new widget.Button();
        jLabel33 = new widget.Label();
        btnHasilKlaim = new widget.Button();
        jLabel34 = new widget.Label();
        btnTriaseIGD = new widget.Button();
        jLabel27 = new widget.Label();
        btnAwalMedisIGD = new widget.Button();
        jLabel25 = new widget.Label();
        btnResumeRanap = new widget.Button();
        jLabel28 = new widget.Label();
        btnHasilLab = new widget.Button();
        jLabel29 = new widget.Label();
        btnHasilRad = new widget.Button();
        jLabel32 = new widget.Label();
        btnRiwayatPasien = new widget.Button();
        jLabel31 = new widget.Label();
        btnSPRI = new widget.Button();
        jLabel30 = new widget.Label();
        btnSurkon = new widget.Button();
        BtnKompilasi = new widget.Button();
        panelBiasa3 = new widget.PanelBiasa();
        scrollPane2 = new widget.ScrollPane();
        loadBillingHTML = new widget.editorpane();
        btnInvoice = new widget.Button();
        PanelBridgingKlaim = new widget.panelisi();
        ScrollPane3 = new widget.ScrollPane();
        panelisi1 = new widget.panelisi();
        panelStatusKlaim = new widget.PanelBiasa();
        panelHeader = new widget.PanelBiasa();
        jLabel14 = new widget.Label();
        lblNoRM = new widget.Label();
        lblNamaPasien = new widget.Label();
        jLabel35 = new widget.Label();
        lblTglLahir = new widget.Label();
        jLabel36 = new widget.Label();
        lblNoKartu = new widget.Label();
        jLabel37 = new widget.Label();
        lblNoKTP = new widget.Label();
        jLabel15 = new widget.Label();
        lblNoRawat = new widget.Label();
        jLabel20 = new widget.Label();
        lblTglRegistrasi = new widget.Label();
        jLabel24 = new widget.Label();
        lblDokterDPJP = new widget.Label();
        jLabel26 = new widget.Label();
        lblStatusRawat = new widget.Label();
        jLabel23 = new widget.Label();
        lblAsalPoli = new widget.Label();
        jLabel50 = new widget.Label();
        lblKamarInap = new widget.Label();
        jLabel38 = new widget.Label();
        lblJenisBayar = new widget.Label();
        jLabel39 = new widget.Label();
        lblTglSEP = new widget.Label();
        jLabel106 = new widget.Label();
        lblTglSEP1 = new widget.Label();
        panelKelengkapanData = new widget.PanelBiasa();
        jLabel40 = new widget.Label();
        tglPulangSEP = new widget.TextBox();
        btnUpdateTglPulang = new widget.Button();
        jLabel54 = new widget.Label();
        statusPulang = new widget.ComboBox();
        jLabel45 = new widget.Label();
        asalRujukan = new widget.ComboBox();
        jLabel43 = new widget.Label();
        kelasRawat = new widget.ComboBox();
        jLabel51 = new widget.Label();
        naikKelas = new widget.ComboBox();
        jLabel52 = new widget.Label();
        jLabel53 = new widget.Label();
        biayaTambahan = new widget.TextBox();
        jLabel55 = new widget.Label();
        sistole = new widget.TextBox();
        jLabel56 = new widget.Label();
        diastole = new widget.TextBox();
        jLabel66 = new widget.Label();
        noRegisterSITB = new widget.TextBox();
        jLabel57 = new widget.Label();
        dializerSingleUse = new widget.ComboBox();
        jLabel67 = new widget.Label();
        kantongDarah = new widget.TextBox();
        losNaikKelas = new widget.TextBox();
        jLabel62 = new widget.Label();
        alteplaseIndikator = new widget.ComboBox();
        panelKelengkapanDataRanap = new widget.PanelBiasa();
        jLabel58 = new widget.Label();
        adlSubAcute = new widget.TextBox();
        jLabel59 = new widget.Label();
        adlChronic = new widget.TextBox();
        jLabel60 = new widget.Label();
        icuIndikator = new widget.ComboBox();
        jLabel61 = new widget.Label();
        icuLOS = new widget.TextBox();
        jLabel64 = new widget.Label();
        icuTotalVentilator = new widget.TextBox();
        jLabel107 = new widget.Label();
        kelahiran = new widget.ComboBox();
        usiaKehamilan = new widget.TextBox();
        jLabel108 = new widget.Label();
        jLabel109 = new widget.Label();
        jLabel110 = new widget.Label();
        gravida = new widget.TextBox();
        jLabel111 = new widget.Label();
        partus = new widget.TextBox();
        jLabel112 = new widget.Label();
        abortus = new widget.TextBox();
        jLabel113 = new widget.Label();
        onsetKontraksi = new widget.ComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel114 = new widget.Label();
        panelRincianBiaya = new widget.PanelBiasa();
        jLabel68 = new widget.Label();
        biayaProsedurNonBedah = new widget.TextBox();
        jLabel69 = new widget.Label();
        diskonProsedurNonBedah = new widget.TextBox();
        jLabel70 = new widget.Label();
        biayaProsedurBedah = new widget.TextBox();
        jLabel71 = new widget.Label();
        diskonProsedurBedah = new widget.TextBox();
        jLabel72 = new widget.Label();
        biayaTenagaAhli = new widget.TextBox();
        jLabel73 = new widget.Label();
        diskonTenagaAhli = new widget.TextBox();
        jLabel74 = new widget.Label();
        biayaKeperawatan = new widget.TextBox();
        jLabel75 = new widget.Label();
        diskonKeperawatan = new widget.TextBox();
        jLabel76 = new widget.Label();
        biayaPenunjang = new widget.TextBox();
        jLabel77 = new widget.Label();
        diskonPenunjang = new widget.TextBox();
        jLabel78 = new widget.Label();
        biayaRadiologi = new widget.TextBox();
        jLabel79 = new widget.Label();
        diskonRadiologi = new widget.TextBox();
        jLabel80 = new widget.Label();
        biayaLaboratorium = new widget.TextBox();
        jLabel81 = new widget.Label();
        diskonLaboratorium = new widget.TextBox();
        jLabel82 = new widget.Label();
        biayaPelayananDarah = new widget.TextBox();
        jLabel83 = new widget.Label();
        diskonPelayananDarah = new widget.TextBox();
        jLabel84 = new widget.Label();
        biayaRehabilitasi = new widget.TextBox();
        jLabel85 = new widget.Label();
        diskonRehabilitasi = new widget.TextBox();
        jLabel86 = new widget.Label();
        biayaKamar = new widget.TextBox();
        jLabel87 = new widget.Label();
        diskonKamar = new widget.TextBox();
        jLabel88 = new widget.Label();
        biayaRawatIntensif = new widget.TextBox();
        jLabel89 = new widget.Label();
        diskonRawatIntensif = new widget.TextBox();
        jLabel90 = new widget.Label();
        biayaObat = new widget.TextBox();
        jLabel91 = new widget.Label();
        diskonObat = new widget.TextBox();
        jLabel92 = new widget.Label();
        biayaObatKronis = new widget.TextBox();
        jLabel93 = new widget.Label();
        diskonObatKronis = new widget.TextBox();
        jLabel94 = new widget.Label();
        biayaObatKemoterapi = new widget.TextBox();
        jLabel95 = new widget.Label();
        diskonObatKemoterapi = new widget.TextBox();
        jLabel96 = new widget.Label();
        biayaAlkes = new widget.TextBox();
        jLabel97 = new widget.Label();
        diskonAlkes = new widget.TextBox();
        jLabel98 = new widget.Label();
        biayaBMHP = new widget.TextBox();
        jLabel99 = new widget.Label();
        diskonBMHP = new widget.TextBox();
        jLabel100 = new widget.Label();
        biayaSewaAlat = new widget.TextBox();
        jLabel101 = new widget.Label();
        diskonSewaAlat = new widget.TextBox();
        jLabel102 = new widget.Label();
        biayaTarifEksekutif = new widget.TextBox();
        jLabel103 = new widget.Label();
        diskonTarifEksekutif = new widget.TextBox();
        jLabel104 = new widget.Label();
        totalRincianBiaya = new widget.TextBox();
        jLabel105 = new widget.Label();
        totalBilling = new widget.TextBox();
        panelGroupingIDRG = new widget.PanelBiasa();
        panelIdrg = new laporan.PanelIdrgSmc();
        panelGroupingINACBG = new widget.PanelBiasa();
        panelInacbg = new laporan.PanelInacbgSmc();
        btnSimpanKlaim = new widget.Button();
        PanelContentINACBG = new widget.panelisi();
        PanelBerkasDigital = new widget.panelisi();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        label19 = new widget.Label();
        kodePJ = new widget.TextBox();
        namaPJ = new widget.TextBox();
        BtnPenjamin = new widget.Button();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnPengaturan = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass10 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel8 = new widget.Label();
        CmbStatusRawat = new widget.ComboBox();
        jLabel11 = new widget.Label();
        CmbStatusKirim = new widget.ComboBox();
        lblCoderNIK = new widget.Label();

        jPopupMenu1.setForeground(new java.awt.Color(50, 50, 50));
        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(200, 26));
        ppPilihSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPilihSemuaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Semua");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 26));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBersihkan);
        jPopupMenu1.add(jSeparator1);

        ppUpdateTanggalPulangSEP.setBackground(new java.awt.Color(255, 255, 254));
        ppUpdateTanggalPulangSEP.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppUpdateTanggalPulangSEP.setForeground(new java.awt.Color(50, 50, 50));
        ppUpdateTanggalPulangSEP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppUpdateTanggalPulangSEP.setText("Update Tanggal Pulang SEP Ranap");
        ppUpdateTanggalPulangSEP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppUpdateTanggalPulangSEP.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppUpdateTanggalPulangSEP.setName("ppUpdateTanggalPulangSEP"); // NOI18N
        ppUpdateTanggalPulangSEP.setPreferredSize(new java.awt.Dimension(200, 26));
        ppUpdateTanggalPulangSEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppUpdateTanggalPulangSEPActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppUpdateTanggalPulangSEP);

        WindowUpdatePulang.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowUpdatePulang.setName("WindowUpdatePulang"); // NOI18N
        WindowUpdatePulang.setUndecorated(true);
        WindowUpdatePulang.setResizable(false);

        internalFrame11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Update Tanggal Pulang ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame11.setName("internalFrame11"); // NOI18N
        internalFrame11.setLayout(null);

        BtnCloseIn8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn8.setMnemonic('U');
        BtnCloseIn8.setText("Tutup");
        BtnCloseIn8.setToolTipText("Alt+U");
        BtnCloseIn8.setName("BtnCloseIn8"); // NOI18N
        BtnCloseIn8.setPreferredSize(new java.awt.Dimension(86, 30));
        BtnCloseIn8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn8ActionPerformed(evt);
            }
        });
        internalFrame11.add(BtnCloseIn8);
        BtnCloseIn8.setBounds(489, 182, 86, 30);

        BtnSimpan8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan8.setMnemonic('S');
        BtnSimpan8.setText("Simpan");
        BtnSimpan8.setToolTipText("Alt+S");
        BtnSimpan8.setName("BtnSimpan8"); // NOI18N
        BtnSimpan8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan8ActionPerformed(evt);
            }
        });
        internalFrame11.add(BtnSimpan8);
        BtnSimpan8.setBounds(10, 182, 86, 30);

        jLabel44.setText("Tgl. Pulang :");
        jLabel44.setName("jLabel44"); // NOI18N
        internalFrame11.add(jLabel44);
        jLabel44.setBounds(0, 92, 78, 23);

        TanggalPulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-03-2026 10:10:44" }));
        TanggalPulang.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalPulang.setName("TanggalPulang"); // NOI18N
        TanggalPulang.setOpaque(false);
        TanggalPulang.setPreferredSize(new java.awt.Dimension(95, 23));
        internalFrame11.add(TanggalPulang);
        TanggalPulang.setBounds(81, 92, 130, 23);

        jLabel46.setText("Status Pulang :");
        jLabel46.setName("jLabel46"); // NOI18N
        internalFrame11.add(jLabel46);
        jLabel46.setBounds(301, 92, 81, 23);

        StatusPulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Atas Persetujuan Dokter", "3. Atas Permintaan Sendiri", "4. Meninggal", "5. Lain-lain" }));
        StatusPulang.setName("StatusPulang"); // NOI18N
        StatusPulang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                StatusPulangItemStateChanged(evt);
            }
        });
        internalFrame11.add(StatusPulang);
        StatusPulang.setBounds(385, 92, 190, 23);

        jLabel47.setText("No. Surat Kematian :");
        jLabel47.setName("jLabel47"); // NOI18N
        jLabel47.setPreferredSize(new java.awt.Dimension(68, 23));
        internalFrame11.add(jLabel47);
        jLabel47.setBounds(0, 122, 120, 23);

        NoSuratKematian.setEditable(false);
        NoSuratKematian.setName("NoSuratKematian"); // NOI18N
        NoSuratKematian.setPreferredSize(new java.awt.Dimension(130, 23));
        internalFrame11.add(NoSuratKematian);
        NoSuratKematian.setBounds(123, 122, 160, 23);

        jLabel48.setText("Tanggal Kematian :");
        jLabel48.setName("jLabel48"); // NOI18N
        internalFrame11.add(jLabel48);
        jLabel48.setBounds(300, 122, 100, 23);

        TanggalKematian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-03-2026" }));
        TanggalKematian.setDisplayFormat("dd-MM-yyyy");
        TanggalKematian.setEnabled(false);
        TanggalKematian.setName("TanggalKematian"); // NOI18N
        TanggalKematian.setOpaque(false);
        TanggalKematian.setPreferredSize(new java.awt.Dimension(95, 23));
        internalFrame11.add(TanggalKematian);
        TanggalKematian.setBounds(404, 122, 171, 23);

        jLabel49.setText("No. LP Manual :");
        jLabel49.setName("jLabel49"); // NOI18N
        jLabel49.setPreferredSize(new java.awt.Dimension(68, 23));
        internalFrame11.add(jLabel49);
        jLabel49.setBounds(0, 152, 120, 23);

        NoLPManual.setName("NoLPManual"); // NOI18N
        NoLPManual.setPreferredSize(new java.awt.Dimension(130, 23));
        internalFrame11.add(NoLPManual);
        NoLPManual.setBounds(123, 152, 160, 23);

        jLabel9.setText("No. Rawat :");
        jLabel9.setName("jLabel9"); // NOI18N
        internalFrame11.add(jLabel9);
        jLabel9.setBounds(0, 32, 78, 23);

        TNoRwPulang.setEditable(false);
        TNoRwPulang.setName("TNoRwPulang"); // NOI18N
        internalFrame11.add(TNoRwPulang);
        TNoRwPulang.setBounds(81, 32, 180, 23);

        TNoRMPulang.setEditable(false);
        TNoRMPulang.setName("TNoRMPulang"); // NOI18N
        internalFrame11.add(TNoRMPulang);
        TNoRMPulang.setBounds(81, 62, 130, 23);

        TPasienPulang.setEditable(false);
        TPasienPulang.setName("TPasienPulang"); // NOI18N
        internalFrame11.add(TPasienPulang);
        TPasienPulang.setBounds(214, 62, 361, 23);

        jLabel41.setText("Pasien :");
        jLabel41.setName("jLabel41"); // NOI18N
        internalFrame11.add(jLabel41);
        jLabel41.setBounds(0, 62, 78, 23);

        TNoSEPRanapPulang.setEditable(false);
        TNoSEPRanapPulang.setName("TNoSEPRanapPulang"); // NOI18N
        internalFrame11.add(TNoSEPRanapPulang);
        TNoSEPRanapPulang.setBounds(395, 32, 180, 23);

        jLabel10.setText("No. SEP Ranap :");
        jLabel10.setName("jLabel10"); // NOI18N
        internalFrame11.add(jLabel10);
        jLabel10.setBounds(310, 32, 82, 23);

        WindowUpdatePulang.getContentPane().add(internalFrame11, java.awt.BorderLayout.CENTER);

        WindowPengaturan.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowPengaturan.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        WindowPengaturan.setName("WindowPengaturan"); // NOI18N
        WindowPengaturan.setUndecorated(true);
        WindowPengaturan.setResizable(false);
        WindowPengaturan.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                WindowPengaturanWindowActivated(evt);
            }
        });

        internalFrame12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)), "::[ Pengaturan Kompilasi Berkas ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame12.setName("internalFrame12"); // NOI18N
        internalFrame12.setPreferredSize(new java.awt.Dimension(610, 228));
        internalFrame12.setLayout(new java.awt.BorderLayout());

        panelBiasa4.setName("panelBiasa4"); // NOI18N
        panelBiasa4.setPreferredSize(new java.awt.Dimension(100, 158));
        panelBiasa4.setLayout(null);

        BtnBukaFolderExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnBukaFolderExport.setMnemonic('S');
        BtnBukaFolderExport.setText("Buka Folder Export");
        BtnBukaFolderExport.setToolTipText("Alt+S");
        BtnBukaFolderExport.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnBukaFolderExport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnBukaFolderExport.setName("BtnBukaFolderExport"); // NOI18N
        BtnBukaFolderExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBukaFolderExportActionPerformed(evt);
            }
        });
        panelBiasa4.add(BtnBukaFolderExport);
        BtnBukaFolderExport.setBounds(324, 40, 136, 23);

        CmbPilihanAplikasiPDF.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "(Aplikasi Default)", "Google Chrome", "Mozilla Firefox", "Microsoft Edge", "Pilih Aplikasi...", "Jangan Buka PDF" }));
        CmbPilihanAplikasiPDF.setName("CmbPilihanAplikasiPDF"); // NOI18N
        CmbPilihanAplikasiPDF.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CmbPilihanAplikasiPDFItemStateChanged(evt);
            }
        });
        panelBiasa4.add(CmbPilihanAplikasiPDF);
        CmbPilihanAplikasiPDF.setBounds(140, 10, 130, 23);

        jLabel12.setText("Buka PDF dengan :");
        jLabel12.setName("jLabel12"); // NOI18N
        panelBiasa4.add(jLabel12);
        jLabel12.setBounds(0, 10, 136, 23);

        TPathAplikasiPDF.setEditable(false);
        TPathAplikasiPDF.setName("TPathAplikasiPDF"); // NOI18N
        panelBiasa4.add(TPathAplikasiPDF);
        TPathAplikasiPDF.setBounds(274, 10, 294, 23);

        jLabel42.setText("Gunakan Tanggal Export :");
        jLabel42.setName("jLabel42"); // NOI18N
        panelBiasa4.add(jLabel42);
        jLabel42.setBounds(0, 40, 136, 23);

        CmbPilihanTanggalExport.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tanggal Kompilasi (Default)", "Tanggal SEP" }));
        CmbPilihanTanggalExport.setName("CmbPilihanTanggalExport"); // NOI18N
        panelBiasa4.add(CmbPilihanTanggalExport);
        CmbPilihanTanggalExport.setBounds(140, 40, 180, 23);

        BtnPilihAplikasiPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihAplikasiPDF.setMnemonic('S');
        BtnPilihAplikasiPDF.setToolTipText("Alt+S");
        BtnPilihAplikasiPDF.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPilihAplikasiPDF.setName("BtnPilihAplikasiPDF"); // NOI18N
        BtnPilihAplikasiPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihAplikasiPDFActionPerformed(evt);
            }
        });
        panelBiasa4.add(BtnPilihAplikasiPDF);
        BtnPilihAplikasiPDF.setBounds(572, 10, 28, 23);

        TMaxMemory.setName("TMaxMemory"); // NOI18N
        panelBiasa4.add(TMaxMemory);
        TMaxMemory.setBounds(140, 100, 80, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jLabel16.setText("MB");
        jLabel16.setName("jLabel16"); // NOI18N
        panelBiasa4.add(jLabel16);
        jLabel16.setBounds(228, 100, 14, 23);

        jLabel22.setText("Max Memory Kompilasi :");
        jLabel22.setName("jLabel22"); // NOI18N
        panelBiasa4.add(jLabel22);
        jLabel22.setBounds(0, 100, 136, 23);

        CekAktifkanHapusOtomatis.setText("Hapus otomatis diagnosa/prosedur yang tersimpan");
        CekAktifkanHapusOtomatis.setName("CekAktifkanHapusOtomatis"); // NOI18N
        CekAktifkanHapusOtomatis.setPreferredSize(new java.awt.Dimension(269, 23));
        panelBiasa4.add(CekAktifkanHapusOtomatis);
        CekAktifkanHapusOtomatis.setBounds(30, 130, 270, 23);

        jLabel18.setText("Kategori upload berkas :");
        jLabel18.setName("jLabel18"); // NOI18N
        panelBiasa4.add(jLabel18);
        jLabel18.setBounds(0, 70, 136, 23);

        CmbPilihanKategoriBerkas.setName("CmbPilihanKategoriBerkas"); // NOI18N
        panelBiasa4.add(CmbPilihanKategoriBerkas);
        CmbPilihanKategoriBerkas.setBounds(140, 70, 428, 23);

        internalFrame12.add(panelBiasa4, java.awt.BorderLayout.CENTER);

        panelBiasa5.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(50, 50, 50)));
        panelBiasa5.setName("panelBiasa5"); // NOI18N
        panelBiasa5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 30, 5));

        BtnSimpanPengaturan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanPengaturan.setMnemonic('S');
        BtnSimpanPengaturan.setText("Simpan");
        BtnSimpanPengaturan.setToolTipText("Alt+S");
        BtnSimpanPengaturan.setName("BtnSimpanPengaturan"); // NOI18N
        BtnSimpanPengaturan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanPengaturanActionPerformed(evt);
            }
        });
        panelBiasa5.add(BtnSimpanPengaturan);

        BtnResetPengaturan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnResetPengaturan.setMnemonic('S');
        BtnResetPengaturan.setText("Reset");
        BtnResetPengaturan.setToolTipText("Alt+S");
        BtnResetPengaturan.setName("BtnResetPengaturan"); // NOI18N
        BtnResetPengaturan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetPengaturanActionPerformed(evt);
            }
        });
        panelBiasa5.add(BtnResetPengaturan);

        BtnTutupPengaturan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnTutupPengaturan.setMnemonic('U');
        BtnTutupPengaturan.setText("Tutup");
        BtnTutupPengaturan.setToolTipText("Alt+U");
        BtnTutupPengaturan.setName("BtnTutupPengaturan"); // NOI18N
        BtnTutupPengaturan.setPreferredSize(new java.awt.Dimension(86, 30));
        BtnTutupPengaturan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTutupPengaturanActionPerformed(evt);
            }
        });
        panelBiasa5.add(BtnTutupPengaturan);

        internalFrame12.add(panelBiasa5, java.awt.BorderLayout.PAGE_END);

        WindowPengaturan.getContentPane().add(internalFrame12, java.awt.BorderLayout.CENTER);

        fc.setCurrentDirectory(null);
        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        fc.setName("fc"); // NOI18N

        jLabel63.setText("BB Saat Lahir :");
        jLabel63.setName("jLabel63"); // NOI18N
        jLabel63.setPreferredSize(new java.awt.Dimension(107, 23));

        beratBadanLahir.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        beratBadanLahir.setText("90");
        beratBadanLahir.setName("beratBadanLahir"); // NOI18N
        beratBadanLahir.setPreferredSize(new java.awt.Dimension(250, 23));

        WindowInputKelahiran.setName("WindowInputKelahiran"); // NOI18N

        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setPreferredSize(new java.awt.Dimension(700, 353));
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        panelAtasKelahiran.setName("panelAtasKelahiran"); // NOI18N
        panelAtasKelahiran.setPreferredSize(new java.awt.Dimension(480, 223));
        panelAtasKelahiran.setLayout(null);

        jLabel115.setText("Urutan Kelahiran :");
        jLabel115.setName("jLabel115"); // NOI18N
        jLabel115.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel115);
        jLabel115.setBounds(0, 10, 107, 23);

        urutanKelahiran.setName("urutanKelahiran"); // NOI18N
        urutanKelahiran.setPreferredSize(new java.awt.Dimension(50, 23));
        panelAtasKelahiran.add(urutanKelahiran);
        urutanKelahiran.setBounds(110, 10, 50, 23);

        jLabel116.setText("Cara Kelahiran :");
        jLabel116.setName("jLabel116"); // NOI18N
        jLabel116.setPreferredSize(new java.awt.Dimension(90, 23));
        panelAtasKelahiran.add(jLabel116);
        jLabel116.setBounds(189, 10, 90, 23);

        caraLahir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Vaginal", "Sectio Caesarean" }));
        caraLahir.setMinimumSize(new java.awt.Dimension(100, 23));
        caraLahir.setName("caraLahir"); // NOI18N
        caraLahir.setPreferredSize(new java.awt.Dimension(180, 23));
        caraLahir.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                caraLahirItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(caraLahir);
        caraLahir.setBounds(282, 10, 180, 23);

        jLabel118.setText("Waktu Kelahiran :");
        jLabel118.setName("jLabel118"); // NOI18N
        jLabel118.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel118);
        jLabel118.setBounds(0, 40, 107, 23);

        waktuKelahiran.setDisplayFormat("dd-MM-yyyy");
        waktuKelahiran.setName("waktuKelahiran"); // NOI18N
        waktuKelahiran.setPreferredSize(new java.awt.Dimension(106, 23));
        panelAtasKelahiran.add(waktuKelahiran);
        waktuKelahiran.setBounds(110, 40, 106, 23);

        jLabel117.setText("Letak Janin :");
        jLabel117.setName("jLabel117"); // NOI18N
        jLabel117.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel117);
        jLabel117.setBounds(0, 70, 107, 23);

        letakJanin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Kepala", "Sungsang", "Lintang / Oblique" }));
        letakJanin.setMinimumSize(new java.awt.Dimension(100, 23));
        letakJanin.setName("letakJanin"); // NOI18N
        letakJanin.setPreferredSize(new java.awt.Dimension(184, 23));
        letakJanin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                letakJaninItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(letakJanin);
        letakJanin.setBounds(110, 70, 184, 23);

        jLabel119.setText("Kondisi :");
        jLabel119.setName("jLabel119"); // NOI18N
        jLabel119.setPreferredSize(new java.awt.Dimension(52, 23));
        panelAtasKelahiran.add(jLabel119);
        jLabel119.setBounds(297, 70, 52, 23);

        kondisiLahir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Hidup", "Meninggal" }));
        kondisiLahir.setMinimumSize(new java.awt.Dimension(100, 23));
        kondisiLahir.setName("kondisiLahir"); // NOI18N
        kondisiLahir.setPreferredSize(new java.awt.Dimension(110, 23));
        kondisiLahir.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                kondisiLahirItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(kondisiLahir);
        kondisiLahir.setBounds(352, 70, 110, 23);

        jLabel120.setText("Bantuan Manual :");
        jLabel120.setName("jLabel120"); // NOI18N
        jLabel120.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel120);
        jLabel120.setBounds(0, 100, 107, 23);

        useManual.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        useManual.setMinimumSize(new java.awt.Dimension(100, 23));
        useManual.setName("useManual"); // NOI18N
        useManual.setPreferredSize(new java.awt.Dimension(80, 23));
        useManual.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useManualItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(useManual);
        useManual.setBounds(110, 100, 80, 23);

        jLabel121.setText("Forcep :");
        jLabel121.setName("jLabel121"); // NOI18N
        jLabel121.setPreferredSize(new java.awt.Dimension(50, 23));
        panelAtasKelahiran.add(jLabel121);
        jLabel121.setBounds(193, 100, 50, 23);

        useForcep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        useForcep.setMinimumSize(new java.awt.Dimension(100, 23));
        useForcep.setName("useForcep"); // NOI18N
        useForcep.setPreferredSize(new java.awt.Dimension(80, 23));
        useForcep.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useForcepItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(useForcep);
        useForcep.setBounds(246, 100, 80, 23);

        jLabel122.setText("Vacuum :");
        jLabel122.setName("jLabel122"); // NOI18N
        jLabel122.setPreferredSize(new java.awt.Dimension(50, 23));
        panelAtasKelahiran.add(jLabel122);
        jLabel122.setBounds(329, 100, 50, 23);

        useVacuum.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        useVacuum.setMinimumSize(new java.awt.Dimension(100, 23));
        useVacuum.setName("useVacuum"); // NOI18N
        useVacuum.setPreferredSize(new java.awt.Dimension(80, 23));
        useVacuum.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useVacuumItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(useVacuum);
        useVacuum.setBounds(382, 100, 80, 23);

        jLabel123.setText("Spesimen SHK :");
        jLabel123.setName("jLabel123"); // NOI18N
        jLabel123.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel123);
        jLabel123.setBounds(0, 130, 107, 23);

        spesimenSHKDiambil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Diambil", "Tidak Diambil" }));
        spesimenSHKDiambil.setMinimumSize(new java.awt.Dimension(100, 23));
        spesimenSHKDiambil.setName("spesimenSHKDiambil"); // NOI18N
        spesimenSHKDiambil.setPreferredSize(new java.awt.Dimension(215, 23));
        spesimenSHKDiambil.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                spesimenSHKDiambilItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(spesimenSHKDiambil);
        spesimenSHKDiambil.setBounds(110, 130, 215, 23);

        jLabel124.setText("Waktu Pengambilan :");
        jLabel124.setName("jLabel124"); // NOI18N
        jLabel124.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel124);
        jLabel124.setBounds(0, 160, 107, 23);

        waktuPengambilanSHK.setDisplayFormat("dd-MM-yyyy");
        waktuPengambilanSHK.setName("waktuPengambilanSHK"); // NOI18N
        waktuPengambilanSHK.setPreferredSize(new java.awt.Dimension(106, 23));
        panelAtasKelahiran.add(waktuPengambilanSHK);
        waktuPengambilanSHK.setBounds(110, 160, 106, 23);

        jLabel125.setText("Lokasi :");
        jLabel125.setName("jLabel125"); // NOI18N
        jLabel125.setPreferredSize(new java.awt.Dimension(47, 23));
        panelAtasKelahiran.add(jLabel125);
        jLabel125.setBounds(328, 130, 47, 23);

        lokasiSpesimen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Tumit", "Vena" }));
        lokasiSpesimen.setMinimumSize(new java.awt.Dimension(100, 23));
        lokasiSpesimen.setName("lokasiSpesimen"); // NOI18N
        lokasiSpesimen.setPreferredSize(new java.awt.Dimension(84, 23));
        lokasiSpesimen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lokasiSpesimenItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(lokasiSpesimen);
        lokasiSpesimen.setBounds(378, 130, 84, 23);

        jLabel126.setText("Alasan Tak Diambil :");
        jLabel126.setName("jLabel126"); // NOI18N
        jLabel126.setPreferredSize(new java.awt.Dimension(107, 23));
        panelAtasKelahiran.add(jLabel126);
        jLabel126.setBounds(0, 190, 107, 23);

        alasanSpesimenTidakDiambil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Tidak Dapat Dilakukan", "Akses Sulit" }));
        alasanSpesimenTidakDiambil.setMinimumSize(new java.awt.Dimension(100, 23));
        alasanSpesimenTidakDiambil.setName("alasanSpesimenTidakDiambil"); // NOI18N
        alasanSpesimenTidakDiambil.setPreferredSize(new java.awt.Dimension(157, 23));
        alasanSpesimenTidakDiambil.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                alasanSpesimenTidakDiambilItemStateChanged(evt);
            }
        });
        panelAtasKelahiran.add(alasanSpesimenTidakDiambil);
        alasanSpesimenTidakDiambil.setBounds(110, 190, 157, 23);

        cmbJamKelahiran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJamKelahiran.setMinimumSize(new java.awt.Dimension(62, 23));
        cmbJamKelahiran.setName("cmbJamKelahiran"); // NOI18N
        cmbJamKelahiran.setPreferredSize(new java.awt.Dimension(62, 23));
        panelAtasKelahiran.add(cmbJamKelahiran);
        cmbJamKelahiran.setBounds(270, 40, 62, 23);

        cmbMenitKelahiran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMenitKelahiran.setMinimumSize(new java.awt.Dimension(62, 23));
        cmbMenitKelahiran.setName("cmbMenitKelahiran"); // NOI18N
        cmbMenitKelahiran.setPreferredSize(new java.awt.Dimension(62, 23));
        panelAtasKelahiran.add(cmbMenitKelahiran);
        cmbMenitKelahiran.setBounds(335, 40, 62, 23);

        cmbDetikKelahiran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDetikKelahiran.setMinimumSize(new java.awt.Dimension(62, 23));
        cmbDetikKelahiran.setName("cmbDetikKelahiran"); // NOI18N
        cmbDetikKelahiran.setPreferredSize(new java.awt.Dimension(62, 23));
        panelAtasKelahiran.add(cmbDetikKelahiran);
        cmbDetikKelahiran.setBounds(400, 40, 62, 23);

        jLabel127.setText("Jam :");
        jLabel127.setName("jLabel127"); // NOI18N
        jLabel127.setPreferredSize(new java.awt.Dimension(48, 23));
        panelAtasKelahiran.add(jLabel127);
        jLabel127.setBounds(219, 40, 48, 23);

        jLabel128.setText("Jam :");
        jLabel128.setName("jLabel128"); // NOI18N
        jLabel128.setPreferredSize(new java.awt.Dimension(48, 23));
        panelAtasKelahiran.add(jLabel128);
        jLabel128.setBounds(219, 160, 48, 23);

        cmbJamSpesimen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJamSpesimen.setMinimumSize(new java.awt.Dimension(62, 23));
        cmbJamSpesimen.setName("cmbJamSpesimen"); // NOI18N
        cmbJamSpesimen.setPreferredSize(new java.awt.Dimension(62, 23));
        panelAtasKelahiran.add(cmbJamSpesimen);
        cmbJamSpesimen.setBounds(270, 160, 62, 23);

        cmbMenitSpesimen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMenitSpesimen.setMinimumSize(new java.awt.Dimension(62, 23));
        cmbMenitSpesimen.setName("cmbMenitSpesimen"); // NOI18N
        cmbMenitSpesimen.setPreferredSize(new java.awt.Dimension(62, 23));
        panelAtasKelahiran.add(cmbMenitSpesimen);
        cmbMenitSpesimen.setBounds(335, 160, 62, 23);

        cmbDetikSpesimen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDetikSpesimen.setMinimumSize(new java.awt.Dimension(62, 23));
        cmbDetikSpesimen.setName("cmbDetikSpesimen"); // NOI18N
        cmbDetikSpesimen.setPreferredSize(new java.awt.Dimension(62, 23));
        panelAtasKelahiran.add(cmbDetikSpesimen);
        cmbDetikSpesimen.setBounds(400, 160, 62, 23);

        internalFrame2.add(panelAtasKelahiran, java.awt.BorderLayout.PAGE_START);

        scrollPane3.setName("scrollPane3"); // NOI18N
        scrollPane3.setViewportView(tbKelahiran);

        tbKelahiran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbKelahiran.setName("tbKelahiran"); // NOI18N
        scrollPane3.setViewportView(tbKelahiran);

        internalFrame2.add(scrollPane3, java.awt.BorderLayout.CENTER);

        panelBawahKelahiran.setName("panelBawahKelahiran"); // NOI18N
        panelBawahKelahiran.setPreferredSize(new java.awt.Dimension(44, 54));
        panelBawahKelahiran.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnSimpanKelahiran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        btnSimpanKelahiran.setMnemonic('S');
        btnSimpanKelahiran.setText("Simpan");
        btnSimpanKelahiran.setToolTipText("Alt+S");
        btnSimpanKelahiran.setName("btnSimpanKelahiran"); // NOI18N
        btnSimpanKelahiran.setPreferredSize(new java.awt.Dimension(100, 30));
        panelBawahKelahiran.add(btnSimpanKelahiran);

        btnBatalKelahiran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        btnBatalKelahiran.setMnemonic('B');
        btnBatalKelahiran.setText("Baru");
        btnBatalKelahiran.setToolTipText("Alt+B");
        btnBatalKelahiran.setName("btnBatalKelahiran"); // NOI18N
        btnBatalKelahiran.setPreferredSize(new java.awt.Dimension(100, 30));
        panelBawahKelahiran.add(btnBatalKelahiran);

        btnEditKelahiran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        btnEditKelahiran.setMnemonic('G');
        btnEditKelahiran.setText("Ganti");
        btnEditKelahiran.setToolTipText("Alt+G");
        btnEditKelahiran.setName("btnEditKelahiran"); // NOI18N
        btnEditKelahiran.setPreferredSize(new java.awt.Dimension(100, 30));
        panelBawahKelahiran.add(btnEditKelahiran);

        btnHapusKelahiran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        btnHapusKelahiran.setMnemonic('H');
        btnHapusKelahiran.setText("Hapus");
        btnHapusKelahiran.setToolTipText("Alt+H");
        btnHapusKelahiran.setName("btnHapusKelahiran"); // NOI18N
        btnHapusKelahiran.setPreferredSize(new java.awt.Dimension(100, 30));
        panelBawahKelahiran.add(btnHapusKelahiran);

        jLabel13.setText("Record :");
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(70, 30));
        panelBawahKelahiran.add(jLabel13);

        LCountKelahiran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountKelahiran.setText("0");
        LCountKelahiran.setName("LCountKelahiran"); // NOI18N
        LCountKelahiran.setPreferredSize(new java.awt.Dimension(72, 30));
        panelBawahKelahiran.add(LCountKelahiran);

        btnKeluarKelahiran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        btnKeluarKelahiran.setMnemonic('K');
        btnKeluarKelahiran.setText("Keluar");
        btnKeluarKelahiran.setToolTipText("Alt+K");
        btnKeluarKelahiran.setName("btnKeluarKelahiran"); // NOI18N
        btnKeluarKelahiran.setPreferredSize(new java.awt.Dimension(100, 30));
        panelBawahKelahiran.add(btnKeluarKelahiran);

        internalFrame2.add(panelBawahKelahiran, java.awt.BorderLayout.PAGE_END);

        WindowInputKelahiran.getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        BtnSimpanKoding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanKoding.setMnemonic('S');
        BtnSimpanKoding.setText("Simpan");
        BtnSimpanKoding.setToolTipText("Alt+S");
        BtnSimpanKoding.setAlignmentY(0.0F);
        BtnSimpanKoding.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnSimpanKoding.setMaximumSize(new java.awt.Dimension(76, 26));
        BtnSimpanKoding.setMinimumSize(new java.awt.Dimension(76, 26));
        BtnSimpanKoding.setName("BtnSimpanKoding"); // NOI18N
        BtnSimpanKoding.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpanKoding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanKodingActionPerformed(evt);
            }
        });
        BtnSimpanKoding.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKodingKeyPressed(evt);
            }
        });

        BtnHapusKoding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapusKoding.setMnemonic('H');
        BtnHapusKoding.setText("Hapus");
        BtnHapusKoding.setToolTipText("Alt+H");
        BtnHapusKoding.setAlignmentY(0.0F);
        BtnHapusKoding.setEnabled(false);
        BtnHapusKoding.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnHapusKoding.setMaximumSize(new java.awt.Dimension(76, 26));
        BtnHapusKoding.setMinimumSize(new java.awt.Dimension(76, 26));
        BtnHapusKoding.setName("BtnHapusKoding"); // NOI18N
        BtnHapusKoding.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapusKoding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusKodingActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Kompilasi Berkas Klaim BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 102));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKompilasi.setAutoCreateRowSorter(true);
        tbKompilasi.setComponentPopupMenu(jPopupMenu1);
        tbKompilasi.setFillsViewportHeight(true);
        tbKompilasi.setMaximumSize(new java.awt.Dimension(32767, 32767));
        tbKompilasi.setName("tbKompilasi"); // NOI18N
        tbKompilasi.setPreferredScrollableViewportSize(null);
        tbKompilasi.setPreferredSize(null);
        tbKompilasi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbKompilasiMouseReleased(evt);
            }
        });
        tbKompilasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbKompilasiKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbKompilasi);

        jPanel1.add(Scroll);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "Detail Billing & Bridging Klaim", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(350, 102));
        jPanel5.setLayout(new java.awt.BorderLayout());

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setSelected(true);
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAccor.setName("ChkAccor"); // NOI18N
        ChkAccor.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        jPanel5.add(ChkAccor, java.awt.BorderLayout.LINE_START);

        tabKanan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        tabKanan.setName("tabKanan"); // NOI18N

        panelInvoices.setName("panelInvoices"); // NOI18N
        panelInvoices.setPreferredSize(new java.awt.Dimension(55, 100));
        panelInvoices.setLayout(new java.awt.BorderLayout());

        panelBiasa2.setName("panelBiasa2"); // NOI18N
        panelBiasa2.setLayout(new java.awt.BorderLayout());

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(220, 223));
        panelBiasa1.setLayout(null);

        jLabel17.setText("No. SEP :");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel17);
        jLabel17.setBounds(0, 10, 105, 16);

        btnSEP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSEP.setText("Tidak ada");
        btnSEP.setEnabled(false);
        btnSEP.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnSEP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSEP.setName("btnSEP"); // NOI18N
        btnSEP.setPreferredSize(new java.awt.Dimension(160, 16));
        btnSEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSEPActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnSEP);
        btnSEP.setBounds(105, 10, 160, 16);

        jLabel33.setText("Berkas Hasil Klaim :");
        jLabel33.setName("jLabel33"); // NOI18N
        jLabel33.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel33);
        jLabel33.setBounds(0, 30, 105, 16);

        btnHasilKlaim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnHasilKlaim.setMnemonic('1');
        btnHasilKlaim.setText("Tidak Ada");
        btnHasilKlaim.setToolTipText("ALt+1");
        btnHasilKlaim.setEnabled(false);
        btnHasilKlaim.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnHasilKlaim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHasilKlaim.setName("btnHasilKlaim"); // NOI18N
        btnHasilKlaim.setPreferredSize(new java.awt.Dimension(160, 16));
        btnHasilKlaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHasilKlaimActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnHasilKlaim);
        btnHasilKlaim.setBounds(105, 30, 160, 16);

        jLabel34.setText("Triase IGD :");
        jLabel34.setName("jLabel34"); // NOI18N
        jLabel34.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel34);
        jLabel34.setBounds(0, 50, 105, 16);

        btnTriaseIGD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnTriaseIGD.setMnemonic('1');
        btnTriaseIGD.setText("Tidak ada");
        btnTriaseIGD.setToolTipText("ALt+1");
        btnTriaseIGD.setEnabled(false);
        btnTriaseIGD.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnTriaseIGD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTriaseIGD.setName("btnTriaseIGD"); // NOI18N
        btnTriaseIGD.setPreferredSize(new java.awt.Dimension(100, 16));
        btnTriaseIGD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTriaseIGDActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnTriaseIGD);
        btnTriaseIGD.setBounds(105, 50, 100, 16);

        jLabel27.setText("Awal Medis IGD :");
        jLabel27.setName("jLabel27"); // NOI18N
        jLabel27.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel27);
        jLabel27.setBounds(0, 70, 105, 16);

        btnAwalMedisIGD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAwalMedisIGD.setMnemonic('1');
        btnAwalMedisIGD.setText("Tidak ada");
        btnAwalMedisIGD.setToolTipText("ALt+1");
        btnAwalMedisIGD.setEnabled(false);
        btnAwalMedisIGD.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnAwalMedisIGD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAwalMedisIGD.setName("btnAwalMedisIGD"); // NOI18N
        btnAwalMedisIGD.setPreferredSize(new java.awt.Dimension(100, 16));
        btnAwalMedisIGD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAwalMedisIGDActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnAwalMedisIGD);
        btnAwalMedisIGD.setBounds(105, 70, 100, 16);

        jLabel25.setText("Resume Ranap :");
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel25);
        jLabel25.setBounds(0, 90, 105, 16);

        btnResumeRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnResumeRanap.setMnemonic('1');
        btnResumeRanap.setText("Tidak ada");
        btnResumeRanap.setToolTipText("ALt+1");
        btnResumeRanap.setEnabled(false);
        btnResumeRanap.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnResumeRanap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnResumeRanap.setName("btnResumeRanap"); // NOI18N
        btnResumeRanap.setPreferredSize(new java.awt.Dimension(100, 16));
        btnResumeRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResumeRanapActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnResumeRanap);
        btnResumeRanap.setBounds(105, 90, 100, 16);

        jLabel28.setText("Hasil Lab :");
        jLabel28.setName("jLabel28"); // NOI18N
        jLabel28.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel28);
        jLabel28.setBounds(0, 110, 105, 16);

        btnHasilLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnHasilLab.setMnemonic('1');
        btnHasilLab.setText("Tidak ada");
        btnHasilLab.setToolTipText("ALt+1");
        btnHasilLab.setEnabled(false);
        btnHasilLab.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnHasilLab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHasilLab.setName("btnHasilLab"); // NOI18N
        btnHasilLab.setPreferredSize(new java.awt.Dimension(100, 16));
        btnHasilLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHasilLabActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnHasilLab);
        btnHasilLab.setBounds(105, 110, 100, 16);

        jLabel29.setText("Hasil Radiologi :");
        jLabel29.setName("jLabel29"); // NOI18N
        jLabel29.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel29);
        jLabel29.setBounds(0, 130, 105, 16);

        btnHasilRad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnHasilRad.setMnemonic('1');
        btnHasilRad.setText("Tidak ada");
        btnHasilRad.setToolTipText("ALt+1");
        btnHasilRad.setEnabled(false);
        btnHasilRad.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnHasilRad.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHasilRad.setName("btnHasilRad"); // NOI18N
        btnHasilRad.setPreferredSize(new java.awt.Dimension(100, 16));
        btnHasilRad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHasilRadActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnHasilRad);
        btnHasilRad.setBounds(105, 130, 100, 16);

        jLabel32.setText("Riwayat :");
        jLabel32.setName("jLabel32"); // NOI18N
        jLabel32.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel32);
        jLabel32.setBounds(0, 150, 105, 16);

        btnRiwayatPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnRiwayatPasien.setMnemonic('1');
        btnRiwayatPasien.setText("Lihat");
        btnRiwayatPasien.setToolTipText("ALt+1");
        btnRiwayatPasien.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnRiwayatPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRiwayatPasien.setName("btnRiwayatPasien"); // NOI18N
        btnRiwayatPasien.setPreferredSize(new java.awt.Dimension(100, 16));
        btnRiwayatPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPasienActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnRiwayatPasien);
        btnRiwayatPasien.setBounds(105, 150, 100, 16);

        jLabel31.setText("SPRI :");
        jLabel31.setName("jLabel31"); // NOI18N
        jLabel31.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel31);
        jLabel31.setBounds(0, 170, 105, 16);

        btnSPRI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSPRI.setMnemonic('1');
        btnSPRI.setText("Tidak ada");
        btnSPRI.setToolTipText("ALt+1");
        btnSPRI.setEnabled(false);
        btnSPRI.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnSPRI.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSPRI.setName("btnSPRI"); // NOI18N
        btnSPRI.setPreferredSize(new java.awt.Dimension(100, 16));
        btnSPRI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSPRIActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnSPRI);
        btnSPRI.setBounds(105, 170, 100, 16);

        jLabel30.setText("Surat Kontrol :");
        jLabel30.setName("jLabel30"); // NOI18N
        jLabel30.setPreferredSize(new java.awt.Dimension(105, 14));
        panelBiasa1.add(jLabel30);
        jLabel30.setBounds(0, 190, 105, 16);

        btnSurkon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSurkon.setMnemonic('1');
        btnSurkon.setText("Tidak ada");
        btnSurkon.setToolTipText("ALt+1");
        btnSurkon.setEnabled(false);
        btnSurkon.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnSurkon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSurkon.setName("btnSurkon"); // NOI18N
        btnSurkon.setPreferredSize(new java.awt.Dimension(100, 16));
        btnSurkon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSurkonActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnSurkon);
        btnSurkon.setBounds(105, 190, 100, 16);

        panelBiasa2.add(panelBiasa1, java.awt.BorderLayout.CENTER);

        BtnKompilasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2.png"))); // NOI18N
        BtnKompilasi.setMnemonic('T');
        BtnKompilasi.setText("Kompilasi");
        BtnKompilasi.setToolTipText("Alt+T");
        BtnKompilasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnKompilasi.setMaximumSize(new java.awt.Dimension(100, 30));
        BtnKompilasi.setMinimumSize(new java.awt.Dimension(100, 30));
        BtnKompilasi.setName("BtnKompilasi"); // NOI18N
        BtnKompilasi.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKompilasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKompilasiActionPerformed(evt);
            }
        });
        panelBiasa2.add(BtnKompilasi, java.awt.BorderLayout.PAGE_END);

        panelInvoices.add(panelBiasa2, java.awt.BorderLayout.PAGE_START);

        panelBiasa3.setName("panelBiasa3"); // NOI18N
        panelBiasa3.setLayout(new java.awt.BorderLayout());

        scrollPane2.setName("scrollPane2"); // NOI18N

        loadBillingHTML.setEditable(false);
        loadBillingHTML.setContentType("text/html"); // NOI18N
        loadBillingHTML.setName("loadBillingHTML"); // NOI18N
        scrollPane2.setViewportView(loadBillingHTML);

        panelBiasa3.add(scrollPane2, java.awt.BorderLayout.CENTER);

        panelInvoices.add(panelBiasa3, java.awt.BorderLayout.CENTER);

        btnInvoice.setMnemonic('1');
        btnInvoice.setText("<html>\n<body>\n<a href=\"#\">Cetak</a>\n</body>\n</html>"); // NOI18N
        btnInvoice.setToolTipText("ALt+1");
        btnInvoice.setEnabled(false);
        btnInvoice.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        btnInvoice.setName("btnInvoice"); // NOI18N
        btnInvoice.setPreferredSize(new java.awt.Dimension(100, 20));
        btnInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvoiceActionPerformed(evt);
            }
        });
        panelInvoices.add(btnInvoice, java.awt.BorderLayout.PAGE_END);

        tabKanan.addTab("Billing & Kelengkapan Data", panelInvoices);
        panelInvoices.getAccessibleContext().setAccessibleName("");

        PanelBridgingKlaim.setName("PanelBridgingKlaim"); // NOI18N
        PanelBridgingKlaim.setPreferredSize(new java.awt.Dimension(55, 55));
        PanelBridgingKlaim.setLayout(new java.awt.BorderLayout());

        ScrollPane3.setName("ScrollPane3"); // NOI18N
        ScrollPane3.setViewportView(panelisi1);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setLayout(new javax.swing.BoxLayout(panelisi1, javax.swing.BoxLayout.PAGE_AXIS));

        panelStatusKlaim.setName("panelStatusKlaim"); // NOI18N

        javax.swing.GroupLayout panelStatusKlaimLayout = new javax.swing.GroupLayout(panelStatusKlaim);
        panelStatusKlaim.setLayout(panelStatusKlaimLayout);
        panelStatusKlaimLayout.setHorizontalGroup(
            panelStatusKlaimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 619, Short.MAX_VALUE)
        );
        panelStatusKlaimLayout.setVerticalGroup(
            panelStatusKlaimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        panelisi1.add(panelStatusKlaim);

        panelHeader.setName("panelHeader"); // NOI18N
        panelHeader.setPreferredSize(new java.awt.Dimension(425, 273));
        panelHeader.setLayout(null);

        jLabel14.setText("Data Pasien :");
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel14);
        jLabel14.setBounds(0, 10, 107, 16);

        lblNoRM.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoRM.setText("123456");
        lblNoRM.setName("lblNoRM"); // NOI18N
        lblNoRM.setPreferredSize(new java.awt.Dimension(42, 16));
        panelHeader.add(lblNoRM);
        lblNoRM.setBounds(110, 10, 42, 16);

        lblNamaPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNamaPasien.setText("MUHAMMAD RIZKY RIZALDI");
        lblNamaPasien.setName("lblNamaPasien"); // NOI18N
        lblNamaPasien.setPreferredSize(new java.awt.Dimension(210, 16));
        panelHeader.add(lblNamaPasien);
        lblNamaPasien.setBounds(156, 10, 210, 16);

        jLabel35.setText("Tgl. Lahir :");
        jLabel35.setName("jLabel35"); // NOI18N
        jLabel35.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel35);
        jLabel35.setBounds(0, 30, 107, 16);

        lblTglLahir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTglLahir.setText("9999-12-31 (Laki-laki / 51 Th)");
        lblTglLahir.setName("lblTglLahir"); // NOI18N
        lblTglLahir.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblTglLahir);
        lblTglLahir.setBounds(110, 30, 250, 16);

        jLabel36.setText("No. Kartu BPJS :");
        jLabel36.setName("jLabel36"); // NOI18N
        jLabel36.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel36);
        jLabel36.setBounds(0, 50, 107, 16);

        lblNoKartu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoKartu.setText("0001122234567");
        lblNoKartu.setName("lblNoKartu"); // NOI18N
        lblNoKartu.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblNoKartu);
        lblNoKartu.setBounds(110, 50, 250, 16);

        jLabel37.setText("No. KTP / Identitas :");
        jLabel37.setName("jLabel37"); // NOI18N
        jLabel37.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel37);
        jLabel37.setBounds(0, 70, 107, 16);

        lblNoKTP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoKTP.setText("6472120000000001");
        lblNoKTP.setName("lblNoKTP"); // NOI18N
        lblNoKTP.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblNoKTP);
        lblNoKTP.setBounds(110, 70, 250, 16);

        jLabel15.setText("No. Rawat :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel15);
        jLabel15.setBounds(0, 90, 107, 16);

        lblNoRawat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoRawat.setText("2026/12/31/000123");
        lblNoRawat.setName("lblNoRawat"); // NOI18N
        lblNoRawat.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblNoRawat);
        lblNoRawat.setBounds(110, 90, 250, 16);

        jLabel20.setText("Tgl. Registrasi :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel20);
        jLabel20.setBounds(0, 110, 107, 16);

        lblTglRegistrasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTglRegistrasi.setText("2026-03-12 09:45:41");
        lblTglRegistrasi.setToolTipText("");
        lblTglRegistrasi.setName("lblTglRegistrasi"); // NOI18N
        lblTglRegistrasi.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblTglRegistrasi);
        lblTglRegistrasi.setBounds(110, 110, 250, 16);

        jLabel24.setText("Dokter DPJP :");
        jLabel24.setName("jLabel24"); // NOI18N
        jLabel24.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel24);
        jLabel24.setBounds(0, 130, 107, 16);

        lblDokterDPJP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDokterDPJP.setText("dr. MUHAMMAD RIZKY RIZALDI");
        lblDokterDPJP.setToolTipText("");
        lblDokterDPJP.setName("lblDokterDPJP"); // NOI18N
        lblDokterDPJP.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblDokterDPJP);
        lblDokterDPJP.setBounds(110, 130, 250, 16);

        jLabel26.setText("Status Rawat :");
        jLabel26.setName("jLabel26"); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel26);
        jLabel26.setBounds(0, 150, 107, 16);

        lblStatusRawat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStatusRawat.setText("Ralan");
        lblStatusRawat.setName("lblStatusRawat"); // NOI18N
        lblStatusRawat.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblStatusRawat);
        lblStatusRawat.setBounds(110, 150, 250, 16);

        jLabel23.setText("Asal Poli :");
        jLabel23.setName("jLabel23"); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel23);
        jLabel23.setBounds(0, 170, 107, 16);

        lblAsalPoli.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAsalPoli.setText("Poli Penyakit Dalam");
        lblAsalPoli.setToolTipText("");
        lblAsalPoli.setName("lblAsalPoli"); // NOI18N
        lblAsalPoli.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblAsalPoli);
        lblAsalPoli.setBounds(110, 170, 250, 16);

        jLabel50.setText("Kamar Inap :");
        jLabel50.setName("jLabel50"); // NOI18N
        jLabel50.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel50);
        jLabel50.setBounds(0, 190, 107, 16);

        lblKamarInap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblKamarInap.setText("382A Ruang Perawatan Umum 3");
        lblKamarInap.setToolTipText("");
        lblKamarInap.setName("lblKamarInap"); // NOI18N
        lblKamarInap.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblKamarInap);
        lblKamarInap.setBounds(110, 190, 250, 16);

        jLabel38.setText("Jenis Bayar :");
        jLabel38.setName("jLabel38"); // NOI18N
        jLabel38.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel38);
        jLabel38.setBounds(0, 210, 107, 16);

        lblJenisBayar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblJenisBayar.setText("(BPJ) BPJS KESEHATAN");
        lblJenisBayar.setName("lblJenisBayar"); // NOI18N
        lblJenisBayar.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblJenisBayar);
        lblJenisBayar.setBounds(110, 210, 250, 16);

        jLabel39.setText("Tgl. SEP :");
        jLabel39.setName("jLabel39"); // NOI18N
        jLabel39.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel39);
        jLabel39.setBounds(0, 250, 107, 16);

        lblTglSEP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTglSEP.setText("2026-03-12");
        lblTglSEP.setToolTipText("");
        lblTglSEP.setName("lblTglSEP"); // NOI18N
        lblTglSEP.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblTglSEP);
        lblTglSEP.setBounds(110, 250, 250, 16);

        jLabel106.setText("No. SEP :");
        jLabel106.setName("jLabel106"); // NOI18N
        jLabel106.setPreferredSize(new java.awt.Dimension(107, 16));
        panelHeader.add(jLabel106);
        jLabel106.setBounds(0, 230, 107, 16);

        lblTglSEP1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTglSEP1.setText("0302R1101299V000123");
        lblTglSEP1.setToolTipText("");
        lblTglSEP1.setName("lblTglSEP1"); // NOI18N
        lblTglSEP1.setPreferredSize(new java.awt.Dimension(250, 16));
        panelHeader.add(lblTglSEP1);
        lblTglSEP1.setBounds(110, 230, 250, 16);

        panelisi1.add(panelHeader);

        panelKelengkapanData.setName("panelKelengkapanData"); // NOI18N
        panelKelengkapanData.setPreferredSize(new java.awt.Dimension(425, 343));
        panelKelengkapanData.setLayout(null);

        jLabel40.setText("Tgl. Pulang SEP :");
        jLabel40.setName("jLabel40"); // NOI18N
        jLabel40.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel40);
        jLabel40.setBounds(0, 10, 107, 23);

        tglPulangSEP.setEditable(false);
        tglPulangSEP.setName("tglPulangSEP"); // NOI18N
        tglPulangSEP.setPreferredSize(new java.awt.Dimension(219, 23));
        panelKelengkapanData.add(tglPulangSEP);
        tglPulangSEP.setBounds(110, 10, 219, 23);

        btnUpdateTglPulang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnUpdateTglPulang.setMnemonic('3');
        btnUpdateTglPulang.setToolTipText("Alt+3");
        btnUpdateTglPulang.setName("btnUpdateTglPulang"); // NOI18N
        btnUpdateTglPulang.setPreferredSize(new java.awt.Dimension(28, 23));
        btnUpdateTglPulang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTglPulangActionPerformed(evt);
            }
        });
        panelKelengkapanData.add(btnUpdateTglPulang);
        btnUpdateTglPulang.setBounds(332, 10, 28, 23);

        jLabel54.setText("Status Pulang :");
        jLabel54.setToolTipText("");
        jLabel54.setName("jLabel54"); // NOI18N
        jLabel54.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel54);
        jLabel54.setBounds(0, 40, 107, 23);

        statusPulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Atas persetujuan dokter", "2. Dirujuk", "3. Atas permintaan sendiri", "4. Meninggal", "5. Lain-lain" }));
        statusPulang.setMinimumSize(new java.awt.Dimension(230, 23));
        statusPulang.setName("statusPulang"); // NOI18N
        statusPulang.setPreferredSize(new java.awt.Dimension(250, 23));
        statusPulang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                statusPulangItemStateChanged(evt);
            }
        });
        panelKelengkapanData.add(statusPulang);
        statusPulang.setBounds(110, 40, 250, 23);

        jLabel45.setText("Asal Rujukan :");
        jLabel45.setToolTipText("");
        jLabel45.setName("jLabel45"); // NOI18N
        jLabel45.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel45);
        jLabel45.setBounds(0, 70, 107, 23);

        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rujukan FKTP", "Rujukan FKRTL", "Rujukan Spesialis", "Dari Rawat Jalan", "Dari Rawat Inap", "Dari Rawat Darurat", "Lahir di RS", "Rujukan Panti Jompo", "Rujukan dari RS Jiwa", "Rujukan Fasilitasi Rehab", "Lain-lain" }));
        asalRujukan.setMinimumSize(new java.awt.Dimension(230, 23));
        asalRujukan.setName("asalRujukan"); // NOI18N
        asalRujukan.setPreferredSize(new java.awt.Dimension(250, 23));
        asalRujukan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                asalRujukanItemStateChanged(evt);
            }
        });
        panelKelengkapanData.add(asalRujukan);
        asalRujukan.setBounds(110, 70, 250, 23);

        jLabel43.setText("Kelas Rawat :");
        jLabel43.setName("jLabel43"); // NOI18N
        jLabel43.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel43);
        jLabel43.setBounds(0, 100, 107, 23);

        kelasRawat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3. Kelas Reguler", "1. Kelas Eksekutif", "1. Kelas 1 (Ranap)", "2. Kelas 2 (Ranap)", "3. Kelas 3 (Ranap)" }));
        kelasRawat.setMinimumSize(new java.awt.Dimension(170, 23));
        kelasRawat.setName("kelasRawat"); // NOI18N
        kelasRawat.setPreferredSize(new java.awt.Dimension(250, 23));
        panelKelengkapanData.add(kelasRawat);
        kelasRawat.setBounds(110, 100, 250, 23);

        jLabel51.setText("Naik ke Kelas :");
        jLabel51.setName("jLabel51"); // NOI18N
        jLabel51.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel51);
        jLabel51.setBounds(0, 130, 107, 23);

        naikKelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Kelas 2", "Kelas 1", "Kelas VIP", "Kelas VVIP" }));
        naikKelas.setMinimumSize(new java.awt.Dimension(100, 23));
        naikKelas.setName("naikKelas"); // NOI18N
        naikKelas.setPreferredSize(new java.awt.Dimension(250, 23));
        naikKelas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                naikKelasItemStateChanged(evt);
            }
        });
        panelKelengkapanData.add(naikKelas);
        naikKelas.setBounds(110, 130, 250, 23);

        jLabel52.setText("Lama Hari Naik Kelas :");
        jLabel52.setName("jLabel52"); // NOI18N
        jLabel52.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel52);
        jLabel52.setBounds(0, 160, 107, 23);

        jLabel53.setText("Biaya Tambahan :");
        jLabel53.setName("jLabel53"); // NOI18N
        jLabel53.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel53);
        jLabel53.setBounds(0, 190, 107, 23);

        biayaTambahan.setName("biayaTambahan"); // NOI18N
        biayaTambahan.setPreferredSize(new java.awt.Dimension(250, 23));
        panelKelengkapanData.add(biayaTambahan);
        biayaTambahan.setBounds(110, 190, 250, 23);

        jLabel55.setText("Tekanan Darah :");
        jLabel55.setName("jLabel55"); // NOI18N
        jLabel55.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel55);
        jLabel55.setBounds(0, 220, 107, 23);

        sistole.setName("sistole"); // NOI18N
        sistole.setPreferredSize(new java.awt.Dimension(50, 23));
        panelKelengkapanData.add(sistole);
        sistole.setBounds(110, 220, 50, 23);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("/");
        jLabel56.setName("jLabel56"); // NOI18N
        jLabel56.setPreferredSize(new java.awt.Dimension(10, 23));
        panelKelengkapanData.add(jLabel56);
        jLabel56.setBounds(163, 220, 10, 23);

        diastole.setName("diastole"); // NOI18N
        diastole.setPreferredSize(new java.awt.Dimension(50, 23));
        panelKelengkapanData.add(diastole);
        diastole.setBounds(176, 220, 50, 23);

        jLabel66.setText("No. Register SITB :");
        jLabel66.setName("jLabel66"); // NOI18N
        jLabel66.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel66);
        jLabel66.setBounds(0, 250, 107, 23);

        noRegisterSITB.setName("noRegisterSITB"); // NOI18N
        noRegisterSITB.setPreferredSize(new java.awt.Dimension(250, 23));
        panelKelengkapanData.add(noRegisterSITB);
        noRegisterSITB.setBounds(110, 250, 250, 23);

        jLabel57.setText("Dializer Single Use :");
        jLabel57.setName("jLabel57"); // NOI18N
        jLabel57.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel57);
        jLabel57.setBounds(0, 280, 107, 23);

        dializerSingleUse.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        dializerSingleUse.setMinimumSize(new java.awt.Dimension(100, 23));
        dializerSingleUse.setName("dializerSingleUse"); // NOI18N
        dializerSingleUse.setPreferredSize(new java.awt.Dimension(90, 23));
        dializerSingleUse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dializerSingleUseItemStateChanged(evt);
            }
        });
        panelKelengkapanData.add(dializerSingleUse);
        dializerSingleUse.setBounds(110, 280, 90, 23);

        jLabel67.setText("Kantong Darah :");
        jLabel67.setName("jLabel67"); // NOI18N
        jLabel67.setPreferredSize(new java.awt.Dimension(104, 23));
        panelKelengkapanData.add(jLabel67);
        jLabel67.setBounds(203, 280, 104, 23);
        jLabel67.getAccessibleContext().setAccessibleName("Kantong Darah :");

        kantongDarah.setName("kantongDarah"); // NOI18N
        kantongDarah.setPreferredSize(new java.awt.Dimension(50, 23));
        panelKelengkapanData.add(kantongDarah);
        kantongDarah.setBounds(310, 280, 50, 23);

        losNaikKelas.setName("losNaikKelas"); // NOI18N
        losNaikKelas.setPreferredSize(new java.awt.Dimension(250, 23));
        panelKelengkapanData.add(losNaikKelas);
        losNaikKelas.setBounds(110, 160, 250, 23);

        jLabel62.setText("Alteplase :");
        jLabel62.setName("jLabel62"); // NOI18N
        jLabel62.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanData.add(jLabel62);
        jLabel62.setBounds(0, 310, 107, 23);

        alteplaseIndikator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        alteplaseIndikator.setMinimumSize(new java.awt.Dimension(100, 23));
        alteplaseIndikator.setName("alteplaseIndikator"); // NOI18N
        alteplaseIndikator.setPreferredSize(new java.awt.Dimension(250, 23));
        alteplaseIndikator.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                alteplaseIndikatorItemStateChanged(evt);
            }
        });
        panelKelengkapanData.add(alteplaseIndikator);
        alteplaseIndikator.setBounds(110, 310, 250, 23);

        panelisi1.add(panelKelengkapanData);

        panelKelengkapanDataRanap.setName("panelKelengkapanDataRanap"); // NOI18N
        panelKelengkapanDataRanap.setPreferredSize(new java.awt.Dimension(425, 550));
        panelKelengkapanDataRanap.setLayout(null);

        jLabel58.setText("ADL Sub Acute :");
        jLabel58.setName("jLabel58"); // NOI18N
        jLabel58.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel58);
        jLabel58.setBounds(0, 10, 107, 23);

        adlSubAcute.setText("120");
        adlSubAcute.setName("adlSubAcute"); // NOI18N
        adlSubAcute.setPreferredSize(new java.awt.Dimension(50, 23));
        panelKelengkapanDataRanap.add(adlSubAcute);
        adlSubAcute.setBounds(110, 10, 50, 23);

        jLabel59.setText("ADL Chronic :");
        jLabel59.setName("jLabel59"); // NOI18N
        jLabel59.setPreferredSize(new java.awt.Dimension(75, 23));
        panelKelengkapanDataRanap.add(jLabel59);
        jLabel59.setBounds(232, 10, 75, 23);

        adlChronic.setText("90");
        adlChronic.setName("adlChronic"); // NOI18N
        adlChronic.setPreferredSize(new java.awt.Dimension(50, 23));
        panelKelengkapanDataRanap.add(adlChronic);
        adlChronic.setBounds(310, 10, 50, 23);

        jLabel60.setText("Masuk ICU :");
        jLabel60.setName("jLabel60"); // NOI18N
        jLabel60.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel60);
        jLabel60.setBounds(0, 40, 107, 23);

        icuIndikator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        icuIndikator.setMinimumSize(new java.awt.Dimension(100, 23));
        icuIndikator.setName("icuIndikator"); // NOI18N
        icuIndikator.setPreferredSize(new java.awt.Dimension(134, 23));
        icuIndikator.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                icuIndikatorItemStateChanged(evt);
            }
        });
        panelKelengkapanDataRanap.add(icuIndikator);
        icuIndikator.setBounds(110, 40, 134, 23);

        jLabel61.setText("Lama Hari :");
        jLabel61.setName("jLabel61"); // NOI18N
        jLabel61.setPreferredSize(new java.awt.Dimension(60, 23));
        panelKelengkapanDataRanap.add(jLabel61);
        jLabel61.setBounds(247, 40, 60, 23);

        icuLOS.setText("90");
        icuLOS.setName("icuLOS"); // NOI18N
        icuLOS.setPreferredSize(new java.awt.Dimension(50, 23));
        panelKelengkapanDataRanap.add(icuLOS);
        icuLOS.setBounds(310, 40, 50, 23);

        jLabel64.setText("Total Jam Ventilator :");
        jLabel64.setName("jLabel64"); // NOI18N
        jLabel64.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel64);
        jLabel64.setBounds(0, 70, 107, 23);

        icuTotalVentilator.setText("90");
        icuTotalVentilator.setName("icuTotalVentilator"); // NOI18N
        icuTotalVentilator.setPreferredSize(new java.awt.Dimension(250, 23));
        panelKelengkapanDataRanap.add(icuTotalVentilator);
        icuTotalVentilator.setBounds(110, 70, 250, 23);

        jLabel107.setText("Kelahiran :");
        jLabel107.setName("jLabel107"); // NOI18N
        jLabel107.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel107);
        jLabel107.setBounds(0, 110, 107, 23);

        kelahiran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "0. Tidak", "1. Ya" }));
        kelahiran.setSelectedIndex(1);
        kelahiran.setMinimumSize(new java.awt.Dimension(100, 23));
        kelahiran.setName("kelahiran"); // NOI18N
        kelahiran.setPreferredSize(new java.awt.Dimension(250, 23));
        kelahiran.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                kelahiranItemStateChanged(evt);
            }
        });
        panelKelengkapanDataRanap.add(kelahiran);
        kelahiran.setBounds(110, 110, 250, 23);

        usiaKehamilan.setText("90");
        usiaKehamilan.setName("usiaKehamilan"); // NOI18N
        usiaKehamilan.setPreferredSize(new java.awt.Dimension(60, 23));
        panelKelengkapanDataRanap.add(usiaKehamilan);
        usiaKehamilan.setBounds(110, 140, 60, 23);

        jLabel108.setText("Usia kehamilan :");
        jLabel108.setName("jLabel108"); // NOI18N
        jLabel108.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel108);
        jLabel108.setBounds(0, 140, 107, 23);

        jLabel109.setText("Riwayat Kehamilan :");
        jLabel109.setName("jLabel109"); // NOI18N
        jLabel109.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel109);
        jLabel109.setBounds(0, 170, 107, 23);

        jLabel110.setText("G :");
        jLabel110.setName("jLabel110"); // NOI18N
        jLabel110.setPreferredSize(new java.awt.Dimension(15, 23));
        panelKelengkapanDataRanap.add(jLabel110);
        jLabel110.setBounds(110, 170, 15, 23);

        gravida.setText("120");
        gravida.setMinimumSize(new java.awt.Dimension(55, 23));
        gravida.setName("gravida"); // NOI18N
        gravida.setPreferredSize(new java.awt.Dimension(60, 23));
        panelKelengkapanDataRanap.add(gravida);
        gravida.setBounds(128, 170, 60, 23);

        jLabel111.setText("P :");
        jLabel111.setName("jLabel111"); // NOI18N
        jLabel111.setPreferredSize(new java.awt.Dimension(20, 23));
        panelKelengkapanDataRanap.add(jLabel111);
        jLabel111.setBounds(191, 170, 20, 23);

        partus.setText("120");
        partus.setMinimumSize(new java.awt.Dimension(55, 23));
        partus.setName("partus"); // NOI18N
        partus.setPreferredSize(new java.awt.Dimension(60, 23));
        panelKelengkapanDataRanap.add(partus);
        partus.setBounds(214, 170, 60, 23);

        jLabel112.setText("A :");
        jLabel112.setName("jLabel112"); // NOI18N
        jLabel112.setPreferredSize(new java.awt.Dimension(20, 23));
        panelKelengkapanDataRanap.add(jLabel112);
        jLabel112.setBounds(277, 170, 20, 23);

        abortus.setText("120");
        abortus.setMinimumSize(new java.awt.Dimension(55, 23));
        abortus.setName("abortus"); // NOI18N
        abortus.setPreferredSize(new java.awt.Dimension(60, 23));
        panelKelengkapanDataRanap.add(abortus);
        abortus.setBounds(300, 170, 60, 23);

        jLabel113.setText("Onset Kontraksi :");
        jLabel113.setName("jLabel113"); // NOI18N
        jLabel113.setPreferredSize(new java.awt.Dimension(107, 23));
        panelKelengkapanDataRanap.add(jLabel113);
        jLabel113.setBounds(0, 200, 107, 23);

        onsetKontraksi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Timbul Spontan", "Dengan Induksi", "SC Tanpa Kontraksi / Induksi" }));
        onsetKontraksi.setMinimumSize(new java.awt.Dimension(100, 23));
        onsetKontraksi.setName("onsetKontraksi"); // NOI18N
        onsetKontraksi.setPreferredSize(new java.awt.Dimension(250, 23));
        onsetKontraksi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                onsetKontraksiItemStateChanged(evt);
            }
        });
        panelKelengkapanDataRanap.add(onsetKontraksi);
        onsetKontraksi.setBounds(110, 200, 250, 23);

        jSeparator2.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator2.setForeground(new java.awt.Color(50, 50, 50));
        jSeparator2.setName("jSeparator2"); // NOI18N
        panelKelengkapanDataRanap.add(jSeparator2);
        jSeparator2.setBounds(0, 100, 370, 5);

        jLabel114.setText("Minggu");
        jLabel114.setName("jLabel114"); // NOI18N
        jLabel114.setPreferredSize(new java.awt.Dimension(40, 23));
        panelKelengkapanDataRanap.add(jLabel114);
        jLabel114.setBounds(173, 140, 40, 23);

        panelisi1.add(panelKelengkapanDataRanap);

        panelRincianBiaya.setName("panelRincianBiaya"); // NOI18N
        panelRincianBiaya.setPreferredSize(new java.awt.Dimension(425, 613));
        panelRincianBiaya.setLayout(null);

        jLabel68.setText("Prosedur Non Bedah :");
        jLabel68.setName("jLabel68"); // NOI18N
        jLabel68.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel68);
        jLabel68.setBounds(0, 10, 106, 23);

        biayaProsedurNonBedah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaProsedurNonBedah.setText("90");
        biayaProsedurNonBedah.setName("biayaProsedurNonBedah"); // NOI18N
        biayaProsedurNonBedah.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaProsedurNonBedah);
        biayaProsedurNonBedah.setBounds(110, 10, 120, 23);

        jLabel69.setText("Disc.");
        jLabel69.setName("jLabel69"); // NOI18N
        jLabel69.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel69);
        jLabel69.setBounds(233, 10, 30, 23);

        diskonProsedurNonBedah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonProsedurNonBedah.setText("0");
        diskonProsedurNonBedah.setName("diskonProsedurNonBedah"); // NOI18N
        diskonProsedurNonBedah.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonProsedurNonBedah);
        diskonProsedurNonBedah.setBounds(266, 10, 94, 23);

        jLabel70.setText("Prosedur Bedah :");
        jLabel70.setName("jLabel70"); // NOI18N
        jLabel70.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel70);
        jLabel70.setBounds(0, 40, 106, 23);

        biayaProsedurBedah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaProsedurBedah.setText("90");
        biayaProsedurBedah.setName("biayaProsedurBedah"); // NOI18N
        biayaProsedurBedah.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaProsedurBedah);
        biayaProsedurBedah.setBounds(110, 40, 120, 23);

        jLabel71.setText("Disc.");
        jLabel71.setName("jLabel71"); // NOI18N
        jLabel71.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel71);
        jLabel71.setBounds(233, 40, 30, 23);

        diskonProsedurBedah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonProsedurBedah.setText("0");
        diskonProsedurBedah.setName("diskonProsedurBedah"); // NOI18N
        diskonProsedurBedah.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonProsedurBedah);
        diskonProsedurBedah.setBounds(266, 40, 94, 23);

        jLabel72.setText("Tenaga Ahli :");
        jLabel72.setName("jLabel72"); // NOI18N
        jLabel72.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel72);
        jLabel72.setBounds(0, 70, 106, 23);

        biayaTenagaAhli.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaTenagaAhli.setText("90");
        biayaTenagaAhli.setName("biayaTenagaAhli"); // NOI18N
        biayaTenagaAhli.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaTenagaAhli);
        biayaTenagaAhli.setBounds(110, 70, 120, 23);

        jLabel73.setText("Disc.");
        jLabel73.setName("jLabel73"); // NOI18N
        jLabel73.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel73);
        jLabel73.setBounds(233, 70, 30, 23);

        diskonTenagaAhli.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonTenagaAhli.setText("0");
        diskonTenagaAhli.setName("diskonTenagaAhli"); // NOI18N
        diskonTenagaAhli.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonTenagaAhli);
        diskonTenagaAhli.setBounds(266, 70, 94, 23);

        jLabel74.setText("Keperawatan :");
        jLabel74.setName("jLabel74"); // NOI18N
        jLabel74.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel74);
        jLabel74.setBounds(0, 100, 106, 23);

        biayaKeperawatan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaKeperawatan.setText("90");
        biayaKeperawatan.setName("biayaKeperawatan"); // NOI18N
        biayaKeperawatan.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaKeperawatan);
        biayaKeperawatan.setBounds(110, 100, 120, 23);

        jLabel75.setText("Disc.");
        jLabel75.setName("jLabel75"); // NOI18N
        jLabel75.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel75);
        jLabel75.setBounds(233, 100, 30, 23);

        diskonKeperawatan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonKeperawatan.setText("0");
        diskonKeperawatan.setName("diskonKeperawatan"); // NOI18N
        diskonKeperawatan.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonKeperawatan);
        diskonKeperawatan.setBounds(266, 100, 94, 23);

        jLabel76.setText("Penunjang :");
        jLabel76.setName("jLabel76"); // NOI18N
        jLabel76.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel76);
        jLabel76.setBounds(0, 130, 106, 23);

        biayaPenunjang.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaPenunjang.setText("90");
        biayaPenunjang.setName("biayaPenunjang"); // NOI18N
        biayaPenunjang.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaPenunjang);
        biayaPenunjang.setBounds(110, 130, 120, 23);

        jLabel77.setText("Disc.");
        jLabel77.setName("jLabel77"); // NOI18N
        jLabel77.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel77);
        jLabel77.setBounds(233, 130, 30, 23);

        diskonPenunjang.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonPenunjang.setText("0");
        diskonPenunjang.setName("diskonPenunjang"); // NOI18N
        diskonPenunjang.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonPenunjang);
        diskonPenunjang.setBounds(266, 130, 94, 23);

        jLabel78.setText("Radiologi :");
        jLabel78.setName("jLabel78"); // NOI18N
        jLabel78.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel78);
        jLabel78.setBounds(0, 160, 106, 23);

        biayaRadiologi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaRadiologi.setText("90");
        biayaRadiologi.setName("biayaRadiologi"); // NOI18N
        biayaRadiologi.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaRadiologi);
        biayaRadiologi.setBounds(110, 160, 120, 23);

        jLabel79.setText("Disc.");
        jLabel79.setName("jLabel79"); // NOI18N
        jLabel79.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel79);
        jLabel79.setBounds(233, 160, 30, 23);

        diskonRadiologi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonRadiologi.setText("0");
        diskonRadiologi.setName("diskonRadiologi"); // NOI18N
        diskonRadiologi.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonRadiologi);
        diskonRadiologi.setBounds(266, 160, 94, 23);

        jLabel80.setText("Laboratorium :");
        jLabel80.setName("jLabel80"); // NOI18N
        jLabel80.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel80);
        jLabel80.setBounds(0, 190, 106, 23);

        biayaLaboratorium.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaLaboratorium.setText("90");
        biayaLaboratorium.setName("biayaLaboratorium"); // NOI18N
        biayaLaboratorium.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaLaboratorium);
        biayaLaboratorium.setBounds(110, 190, 120, 23);

        jLabel81.setText("Disc.");
        jLabel81.setName("jLabel81"); // NOI18N
        jLabel81.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel81);
        jLabel81.setBounds(233, 190, 30, 23);

        diskonLaboratorium.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonLaboratorium.setText("0");
        diskonLaboratorium.setName("diskonLaboratorium"); // NOI18N
        diskonLaboratorium.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonLaboratorium);
        diskonLaboratorium.setBounds(266, 190, 94, 23);

        jLabel82.setText("Pelayanan Darah :");
        jLabel82.setName("jLabel82"); // NOI18N
        jLabel82.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel82);
        jLabel82.setBounds(0, 220, 106, 23);

        biayaPelayananDarah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaPelayananDarah.setText("90");
        biayaPelayananDarah.setName("biayaPelayananDarah"); // NOI18N
        biayaPelayananDarah.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaPelayananDarah);
        biayaPelayananDarah.setBounds(110, 220, 120, 23);

        jLabel83.setText("Disc.");
        jLabel83.setName("jLabel83"); // NOI18N
        jLabel83.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel83);
        jLabel83.setBounds(233, 220, 30, 23);

        diskonPelayananDarah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonPelayananDarah.setText("0");
        diskonPelayananDarah.setName("diskonPelayananDarah"); // NOI18N
        diskonPelayananDarah.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonPelayananDarah);
        diskonPelayananDarah.setBounds(266, 220, 94, 23);

        jLabel84.setText("Rehabilitasi :");
        jLabel84.setName("jLabel84"); // NOI18N
        jLabel84.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel84);
        jLabel84.setBounds(0, 250, 106, 23);

        biayaRehabilitasi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaRehabilitasi.setText("90");
        biayaRehabilitasi.setName("biayaRehabilitasi"); // NOI18N
        biayaRehabilitasi.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaRehabilitasi);
        biayaRehabilitasi.setBounds(110, 250, 120, 23);

        jLabel85.setText("Disc.");
        jLabel85.setName("jLabel85"); // NOI18N
        jLabel85.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel85);
        jLabel85.setBounds(233, 250, 30, 23);

        diskonRehabilitasi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonRehabilitasi.setText("0");
        diskonRehabilitasi.setName("diskonRehabilitasi"); // NOI18N
        diskonRehabilitasi.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonRehabilitasi);
        diskonRehabilitasi.setBounds(266, 250, 94, 23);

        jLabel86.setText("Kamar :");
        jLabel86.setName("jLabel86"); // NOI18N
        jLabel86.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel86);
        jLabel86.setBounds(0, 280, 106, 23);

        biayaKamar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaKamar.setText("90");
        biayaKamar.setName("biayaKamar"); // NOI18N
        biayaKamar.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaKamar);
        biayaKamar.setBounds(110, 280, 120, 23);

        jLabel87.setText("Disc.");
        jLabel87.setName("jLabel87"); // NOI18N
        jLabel87.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel87);
        jLabel87.setBounds(233, 280, 30, 23);

        diskonKamar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonKamar.setText("0");
        diskonKamar.setName("diskonKamar"); // NOI18N
        diskonKamar.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonKamar);
        diskonKamar.setBounds(266, 280, 94, 23);

        jLabel88.setText("Rawat Intensif :");
        jLabel88.setName("jLabel88"); // NOI18N
        jLabel88.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel88);
        jLabel88.setBounds(0, 310, 106, 23);

        biayaRawatIntensif.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaRawatIntensif.setText("90");
        biayaRawatIntensif.setName("biayaRawatIntensif"); // NOI18N
        biayaRawatIntensif.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaRawatIntensif);
        biayaRawatIntensif.setBounds(110, 310, 120, 23);

        jLabel89.setText("Disc.");
        jLabel89.setName("jLabel89"); // NOI18N
        jLabel89.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel89);
        jLabel89.setBounds(233, 310, 30, 23);

        diskonRawatIntensif.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonRawatIntensif.setText("0");
        diskonRawatIntensif.setName("diskonRawatIntensif"); // NOI18N
        diskonRawatIntensif.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonRawatIntensif);
        diskonRawatIntensif.setBounds(266, 310, 94, 23);

        jLabel90.setText("Obat :");
        jLabel90.setName("jLabel90"); // NOI18N
        jLabel90.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel90);
        jLabel90.setBounds(0, 340, 106, 23);

        biayaObat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaObat.setText("90");
        biayaObat.setName("biayaObat"); // NOI18N
        biayaObat.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaObat);
        biayaObat.setBounds(110, 340, 120, 23);

        jLabel91.setText("Disc.");
        jLabel91.setName("jLabel91"); // NOI18N
        jLabel91.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel91);
        jLabel91.setBounds(233, 340, 30, 23);

        diskonObat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonObat.setText("0");
        diskonObat.setName("diskonObat"); // NOI18N
        diskonObat.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonObat);
        diskonObat.setBounds(266, 340, 94, 23);

        jLabel92.setText("Obat Kronis :");
        jLabel92.setName("jLabel92"); // NOI18N
        jLabel92.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel92);
        jLabel92.setBounds(0, 370, 106, 23);

        biayaObatKronis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaObatKronis.setText("90");
        biayaObatKronis.setName("biayaObatKronis"); // NOI18N
        biayaObatKronis.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaObatKronis);
        biayaObatKronis.setBounds(110, 370, 120, 23);

        jLabel93.setText("Disc.");
        jLabel93.setName("jLabel93"); // NOI18N
        jLabel93.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel93);
        jLabel93.setBounds(233, 370, 30, 23);

        diskonObatKronis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonObatKronis.setText("0");
        diskonObatKronis.setName("diskonObatKronis"); // NOI18N
        diskonObatKronis.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonObatKronis);
        diskonObatKronis.setBounds(266, 370, 94, 23);

        jLabel94.setText("Obat Kemoterapi :");
        jLabel94.setName("jLabel94"); // NOI18N
        jLabel94.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel94);
        jLabel94.setBounds(0, 400, 106, 23);

        biayaObatKemoterapi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaObatKemoterapi.setText("90");
        biayaObatKemoterapi.setName("biayaObatKemoterapi"); // NOI18N
        biayaObatKemoterapi.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaObatKemoterapi);
        biayaObatKemoterapi.setBounds(110, 400, 120, 23);

        jLabel95.setText("Disc.");
        jLabel95.setName("jLabel95"); // NOI18N
        jLabel95.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel95);
        jLabel95.setBounds(233, 400, 30, 23);

        diskonObatKemoterapi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonObatKemoterapi.setText("0");
        diskonObatKemoterapi.setName("diskonObatKemoterapi"); // NOI18N
        diskonObatKemoterapi.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonObatKemoterapi);
        diskonObatKemoterapi.setBounds(266, 400, 94, 23);

        jLabel96.setText("Alkes :");
        jLabel96.setName("jLabel96"); // NOI18N
        jLabel96.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel96);
        jLabel96.setBounds(0, 430, 106, 23);

        biayaAlkes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaAlkes.setText("90");
        biayaAlkes.setName("biayaAlkes"); // NOI18N
        biayaAlkes.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaAlkes);
        biayaAlkes.setBounds(110, 430, 120, 23);

        jLabel97.setText("Disc.");
        jLabel97.setName("jLabel97"); // NOI18N
        jLabel97.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel97);
        jLabel97.setBounds(233, 430, 30, 23);

        diskonAlkes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonAlkes.setText("0");
        diskonAlkes.setName("diskonAlkes"); // NOI18N
        diskonAlkes.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonAlkes);
        diskonAlkes.setBounds(266, 430, 94, 23);

        jLabel98.setText("BMHP :");
        jLabel98.setName("jLabel98"); // NOI18N
        jLabel98.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel98);
        jLabel98.setBounds(0, 460, 106, 23);

        biayaBMHP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaBMHP.setText("90");
        biayaBMHP.setName("biayaBMHP"); // NOI18N
        biayaBMHP.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaBMHP);
        biayaBMHP.setBounds(110, 460, 120, 23);

        jLabel99.setText("Disc.");
        jLabel99.setName("jLabel99"); // NOI18N
        jLabel99.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel99);
        jLabel99.setBounds(233, 460, 30, 23);

        diskonBMHP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonBMHP.setText("0");
        diskonBMHP.setName("diskonBMHP"); // NOI18N
        diskonBMHP.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonBMHP);
        diskonBMHP.setBounds(266, 460, 94, 23);

        jLabel100.setText("Sewa Alat :");
        jLabel100.setName("jLabel100"); // NOI18N
        jLabel100.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel100);
        jLabel100.setBounds(0, 490, 106, 23);

        biayaSewaAlat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaSewaAlat.setText("90");
        biayaSewaAlat.setName("biayaSewaAlat"); // NOI18N
        biayaSewaAlat.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaSewaAlat);
        biayaSewaAlat.setBounds(110, 490, 120, 23);

        jLabel101.setText("Disc.");
        jLabel101.setName("jLabel101"); // NOI18N
        jLabel101.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel101);
        jLabel101.setBounds(233, 490, 30, 23);

        diskonSewaAlat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonSewaAlat.setText("0");
        diskonSewaAlat.setName("diskonSewaAlat"); // NOI18N
        diskonSewaAlat.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonSewaAlat);
        diskonSewaAlat.setBounds(266, 490, 94, 23);

        jLabel102.setText("Tarif Eksekutif :");
        jLabel102.setName("jLabel102"); // NOI18N
        jLabel102.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel102);
        jLabel102.setBounds(0, 520, 106, 23);

        biayaTarifEksekutif.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        biayaTarifEksekutif.setText("90");
        biayaTarifEksekutif.setName("biayaTarifEksekutif"); // NOI18N
        biayaTarifEksekutif.setPreferredSize(new java.awt.Dimension(120, 23));
        panelRincianBiaya.add(biayaTarifEksekutif);
        biayaTarifEksekutif.setBounds(110, 520, 120, 23);

        jLabel103.setText("Disc.");
        jLabel103.setName("jLabel103"); // NOI18N
        jLabel103.setPreferredSize(new java.awt.Dimension(30, 23));
        panelRincianBiaya.add(jLabel103);
        jLabel103.setBounds(233, 520, 30, 23);

        diskonTarifEksekutif.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskonTarifEksekutif.setText("0");
        diskonTarifEksekutif.setName("diskonTarifEksekutif"); // NOI18N
        diskonTarifEksekutif.setPreferredSize(new java.awt.Dimension(94, 23));
        panelRincianBiaya.add(diskonTarifEksekutif);
        diskonTarifEksekutif.setBounds(266, 520, 94, 23);

        jLabel104.setText("Total Rincian Biaya :");
        jLabel104.setName("jLabel104"); // NOI18N
        jLabel104.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel104);
        jLabel104.setBounds(0, 550, 106, 23);

        totalRincianBiaya.setEditable(false);
        totalRincianBiaya.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalRincianBiaya.setText("90");
        totalRincianBiaya.setName("totalRincianBiaya"); // NOI18N
        totalRincianBiaya.setPreferredSize(new java.awt.Dimension(250, 23));
        panelRincianBiaya.add(totalRincianBiaya);
        totalRincianBiaya.setBounds(110, 550, 250, 23);

        jLabel105.setText("Total Billing :");
        jLabel105.setName("jLabel105"); // NOI18N
        jLabel105.setPreferredSize(new java.awt.Dimension(107, 23));
        panelRincianBiaya.add(jLabel105);
        jLabel105.setBounds(0, 580, 106, 23);

        totalBilling.setEditable(false);
        totalBilling.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalBilling.setText("90");
        totalBilling.setName("totalBilling"); // NOI18N
        totalBilling.setPreferredSize(new java.awt.Dimension(250, 23));
        panelRincianBiaya.add(totalBilling);
        totalBilling.setBounds(110, 580, 250, 23);

        panelisi1.add(panelRincianBiaya);

        panelGroupingIDRG.setName("panelGroupingIDRG"); // NOI18N
        panelGroupingIDRG.setLayout(null);

        panelIdrg.setName("panelIdrg"); // NOI18N
        panelIdrg.setPreferredSize(new java.awt.Dimension(800, 432));
        panelGroupingIDRG.add(panelIdrg);
        panelIdrg.setBounds(0, 0, 800, 432);

        panelisi1.add(panelGroupingIDRG);

        panelGroupingINACBG.setName("panelGroupingINACBG"); // NOI18N
        panelGroupingINACBG.setLayout(null);

        panelInacbg.setName("panelInacbg"); // NOI18N
        panelGroupingINACBG.add(panelInacbg);
        panelInacbg.setBounds(0, 0, 800, 410);

        panelisi1.add(panelGroupingINACBG);

        ScrollPane3.setViewportView(panelisi1);

        PanelBridgingKlaim.add(ScrollPane3, java.awt.BorderLayout.CENTER);

        btnSimpanKlaim.setBackground(new java.awt.Color(255, 255, 255));
        btnSimpanKlaim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        btnSimpanKlaim.setText("Simpan");
        btnSimpanKlaim.setGlassColor(new java.awt.Color(240, 245, 235));
        btnSimpanKlaim.setName("btnSimpanKlaim"); // NOI18N
        btnSimpanKlaim.setPreferredSize(new java.awt.Dimension(62, 30));
        PanelBridgingKlaim.add(btnSimpanKlaim, java.awt.BorderLayout.PAGE_END);

        tabKanan.addTab("Bridging Klaim", PanelBridgingKlaim);

        PanelContentINACBG.setName("PanelContentINACBG"); // NOI18N
        PanelContentINACBG.setPreferredSize(new java.awt.Dimension(55, 55));
        PanelContentINACBG.setLayout(new java.awt.BorderLayout());
        tabKanan.addTab("Bridging Klaim", PanelContentINACBG);

        PanelBerkasDigital.setName("PanelBerkasDigital"); // NOI18N
        PanelBerkasDigital.setPreferredSize(new java.awt.Dimension(55, 55));
        PanelBerkasDigital.setLayout(new java.awt.BorderLayout());
        tabKanan.addTab("Berkas Digital", PanelBerkasDigital);

        jPanel5.add(tabKanan, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label19.setText("Jenis Bayar :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass8.add(label19);

        kodePJ.setEditable(false);
        kodePJ.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        kodePJ.setName("kodePJ"); // NOI18N
        kodePJ.setPreferredSize(new java.awt.Dimension(41, 23));
        panelGlass8.add(kodePJ);

        namaPJ.setEditable(false);
        namaPJ.setName("namaPJ"); // NOI18N
        namaPJ.setPreferredSize(new java.awt.Dimension(170, 23));
        panelGlass8.add(namaPJ);

        BtnPenjamin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPenjamin.setMnemonic('3');
        BtnPenjamin.setToolTipText("Alt+3");
        BtnPenjamin.setName("BtnPenjamin"); // NOI18N
        BtnPenjamin.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPenjamin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPenjaminActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPenjamin);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(62, 23));
        panelGlass8.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass8.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnAll);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass8.add(LCount);

        BtnPengaturan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        BtnPengaturan.setMnemonic('T');
        BtnPengaturan.setText("Pengaturan");
        BtnPengaturan.setToolTipText("Alt+T");
        BtnPengaturan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnPengaturan.setMaximumSize(new java.awt.Dimension(100, 30));
        BtnPengaturan.setMinimumSize(new java.awt.Dimension(100, 30));
        BtnPengaturan.setName("BtnPengaturan"); // NOI18N
        BtnPengaturan.setPreferredSize(new java.awt.Dimension(120, 28));
        BtnPengaturan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPengaturanActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPengaturan);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        jLabel19.setText("Tgl. SEP :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass10.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-03-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass10.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass10.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-03-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass10.add(DTPCari2);

        jLabel8.setText("Status Rawat :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass10.add(jLabel8);

        CmbStatusRawat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Ralan", "Ranap" }));
        CmbStatusRawat.setLightWeightPopupEnabled(false);
        CmbStatusRawat.setMinimumSize(new java.awt.Dimension(75, 21));
        CmbStatusRawat.setName("CmbStatusRawat"); // NOI18N
        CmbStatusRawat.setPreferredSize(new java.awt.Dimension(76, 23));
        panelGlass10.add(CmbStatusRawat);

        jLabel11.setText("Status Klaim :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass10.add(jLabel11);

        CmbStatusKirim.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Selesai", "INACBG Final", "INACBG Grouping", "IDRG Final", "IDRG Grouping", "Belum" }));
        CmbStatusKirim.setLightWeightPopupEnabled(false);
        CmbStatusKirim.setMinimumSize(new java.awt.Dimension(75, 21));
        CmbStatusKirim.setName("CmbStatusKirim"); // NOI18N
        CmbStatusKirim.setPreferredSize(new java.awt.Dimension(113, 23));
        panelGlass10.add(CmbStatusKirim);

        lblCoderNIK.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCoderNIK.setName("lblCoderNIK"); // NOI18N
        lblCoderNIK.setPreferredSize(new java.awt.Dimension(105, 23));
        panelGlass10.add(lblCoderNIK);

        jPanel3.add(panelGlass10, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        kodePJ.setText(KODEPJBPJS);
        namaPJ.setText(NAMAPJBPJS);
        CmbStatusRawat.setSelectedIndex(0);
        CmbStatusKirim.setSelectedIndex(0);
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnKompilasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKompilasiActionPerformed
        if (tbKompilasi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data masih kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            int j = 0;
            for (int i = 0; i < tbKompilasi.getRowCount(); i++) {
                if ((Boolean) tbKompilasi.getValueAt(i, 0)) {
                    ++j;
                }
            }

            if (j >= 1) {
                bulkKompilasiBerkas(j);
            } else {
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data pasien dahulu..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    getData();
                    gabung();
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }//GEN-LAST:event_BtnKompilasiActionPerformed

    private void btnSEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSEPActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("norawat", lblNoRawat.getText());
            param.put("prb", Sequel.cariIsiSmc("select bpjs_prb.prb from bpjs_prb where bpjs_prb.no_sep = ?", btnSEP.getText()));
            param.put("noreg", Sequel.cariIsiSmc("select reg_periksa.no_reg from reg_periksa where reg_periksa.no_rawat = ?", lblNoRawat.getText()));
            param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
            param.put("parameter", btnSEP.getText());
            param.put("cetakanke", 2);

            String pilihan = (String) JOptionPane.showInputDialog(
                null, "Silahkan pilih model SEP yang mau dilihat", "Pilih Model SEP", JOptionPane.INFORMATION_MESSAGE, null,
                new String[] {"Model 1 (Lembar SEP)", "Model 2 (IGDTL)", "Model 3 (Lembar SEP Alternatif)", "Model 4 (RJTL)"}, "Model 1 (Lembar SEP)"
            );

            if (pilihan == null) {
                return;
            }

            switch (pilihan) {
                case "Model 1 (Lembar SEP)":
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (lblStatusRawat.getText().contains("Ranap")) {
                        Valid.MyReport("rptBridgingSEP.jasper", "report", "::[ Cetak SEP ]::", param);
                    } else {
                        Valid.MyReport("rptBridgingSEP2.jasper", "report", "::[ Cetak SEP ]::", param);
                    }
                    setCursor(Cursor.getDefaultCursor());
                    break;
                case "Model 2 (IGDTL)":
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (lblStatusRawat.getText().contains("Ranap")) {
                        Valid.MyReport("rptBridgingSEP3.jasper", "report", "::[ Cetak SEP ]::", param);
                    } else {
                        Valid.MyReport("rptBridgingSEP4.jasper", "report", "::[ Cetak SEP ]::", param);
                    }
                    setCursor(Cursor.getDefaultCursor());
                    break;
                case "Model 3 (Lembar SEP Alternatif)":
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (lblStatusRawat.getText().contains("Ranap")) {
                        Valid.MyReport("rptBridgingSEP5.jasper", "report", "::[ Cetak SEP ]::", param);
                    } else {
                        Valid.MyReport("rptBridgingSEP6.jasper", "report", "::[ Cetak SEP ]::", param);
                    }
                    setCursor(Cursor.getDefaultCursor());
                    break;
                case "Model 4 (RJTL)":
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (lblStatusRawat.getText().contains("Ranap")) {
                        Valid.MyReport("rptBridgingSEP7.jasper", "report", "::[ Cetak SEP ]::", param);
                    } else {
                        Valid.MyReport("rptBridgingSEP8.jasper", "report", "::[ Cetak SEP ]::", param);
                    }
                    setCursor(Cursor.getDefaultCursor());
                    break;
                default:
                    break;
            }
        }
    }//GEN-LAST:event_btnSEPActionPerformed

    private void btnResumeRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumeRanapActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu...!!!");
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("norawat", lblNoRawat.getText());
            String waktuKeluar = "", tglKeluar = "", jamKeluar = "";
            waktuKeluar = Sequel.cariIsiSmc("select concat(tgl_keluar, ' ', jam_keluar) from kamar_inap where no_rawat = ? and stts_pulang != 'Pindah Kamar' order by concat(tgl_keluar, ' ', jam_keluar) limit 1", lblNoRawat.getText());
            if (!waktuKeluar.isBlank()) {
                tglKeluar = waktuKeluar.substring(0, 10);
                jamKeluar = waktuKeluar.substring(11, 19);
            }
            String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from resume_pasien_ranap where no_rawat = ?", lblNoRawat.getText());
            String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik = ?", kodeDokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + (finger.isBlank() ? kodeDokter : finger) + "\n" + Valid.SetTgl3(tglKeluar));
            param.put("ruang", Sequel.cariIsiSmc(
                "select concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar " +
                "join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? and kamar_inap.tgl_keluar = ? and kamar_inap.jam_keluar = ?",
                lblNoRawat.getText(), tglKeluar, jamKeluar)
            );
            param.put("tanggalkeluar", Valid.SetTgl3(tglKeluar));
            param.put("jamkeluar", jamKeluar);
            try (PreparedStatement ps = koneksi.prepareStatement("select dpjp_ranap.kd_dokter, dokter.nm_dokter from dpjp_ranap join dokter on dpjp_ranap.kd_dokter = dokter.kd_dokter where dpjp_ranap.no_rawat = ? and dpjp_ranap.kd_dokter != ?")) {
                ps.setString(1, lblNoRawat.getText());
                ps.setString(2, kodeDokter);
                try (ResultSet rs = ps.executeQuery()) {
                    int i = 2;
                    while (rs.next()) {
                        if (i == 2) {
                            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + Valid.SetTgl3(tglKeluar));
                            param.put("namadokter2", rs.getString("nm_dokter"));
                        }
                        if (i == 3) {
                            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                            param.put("finger3", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + Valid.SetTgl3(tglKeluar));
                            param.put("namadokter3", rs.getString("nm_dokter"));
                        }
                        i++;
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            Valid.reportSmc("rptLaporanResumeRanapKompilasi.jasper", "report", "::[ Laporan Resume Pasien ]::", param);
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnResumeRanapActionPerformed

    private void btnAwalMedisIGDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAwalMedisIGDActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from penilaian_medis_igd where no_rawat = ?", lblNoRawat.getText());
            String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
            String tgl = Sequel.cariIsiSmc("select date_format(tanggal, '%d-%m-%Y') from penilaian_medis_igd where no_rawat = ?", lblNoRawat.getText());
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            try {
                param.put("lokalis", getClass().getResource("/picture/semua.png").openStream());
            } catch (Exception e) {
            }
            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", kodeDokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + (finger.isBlank() ? kodeDokter : finger) + "\n" + tgl);
            Valid.reportSmc("rptCetakPenilaianAwalMedisIGD.jasper", "report", "::[ Laporan Penilaian Awal Medis IGD ]::", param,
                "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, if (pasien.jk = 'L', 'Laki-Laki', 'Perempuan') as jk, pasien.tgl_lahir, penilaian_medis_igd.tanggal, penilaian_medis_igd.kd_dokter, " +
                "penilaian_medis_igd.anamnesis, penilaian_medis_igd.hubungan, concat_ws(', ', penilaian_medis_igd.anamnesis, nullif(penilaian_medis_igd.hubungan, '')) as hubungan_anemnesis, penilaian_medis_igd.keluhan_utama, " +
                "penilaian_medis_igd.rps, penilaian_medis_igd.rpk, penilaian_medis_igd.rpd, penilaian_medis_igd.rpo, penilaian_medis_igd.alergi, penilaian_medis_igd.keadaan, penilaian_medis_igd.gcs, penilaian_medis_igd.kesadaran, " +
                "penilaian_medis_igd.td, penilaian_medis_igd.nadi, penilaian_medis_igd.rr, penilaian_medis_igd.suhu, penilaian_medis_igd.spo, penilaian_medis_igd.bb, penilaian_medis_igd.tb, penilaian_medis_igd.kepala, penilaian_medis_igd.mata, " +
                "penilaian_medis_igd.gigi, penilaian_medis_igd.leher, penilaian_medis_igd.thoraks, penilaian_medis_igd.abdomen, penilaian_medis_igd.ekstremitas, penilaian_medis_igd.genital, penilaian_medis_igd.ket_fisik, penilaian_medis_igd.ket_lokalis, " +
                "penilaian_medis_igd.ekg, penilaian_medis_igd.rad, penilaian_medis_igd.lab, penilaian_medis_igd.diagnosis, penilaian_medis_igd.tata, dokter.nm_dokter from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "join penilaian_medis_igd on reg_periksa.no_rawat = penilaian_medis_igd.no_rawat join dokter on penilaian_medis_igd.kd_dokter = dokter.kd_dokter where penilaian_medis_igd.no_rawat = ?", lblNoRawat.getText()
            );
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnAwalMedisIGDActionPerformed

    private void btnHasilLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHasilLabActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String namaKamar = "";
            int i = 0;
            Map<String, Object> param = new HashMap<>();

            try {
                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select pasien.jk, pasien.umur, pasien.tgl_lahir, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, " +
                    "pasien.pekerjaan, pasien.no_ktp from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join kelurahan on pasien.kd_kel = kelurahan.kd_kel " +
                    "join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on pasien.kd_kab = kabupaten.kd_kab join propinsi on pasien.kd_prop = propinsi.kd_prop where reg_periksa.no_rawat = ?"
                )) {
                    ps.setString(1, lblNoRawat.getText());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            param.put("noperiksa", lblNoRawat.getText());
                            param.put("norm", lblNoRM.getText());
                            param.put("namapasien", lblNamaPasien.getText());
                            param.put("jkel", rs.getString("jk"));
                            param.put("umur", rs.getString("umur"));
                            param.put("lahir", new SimpleDateFormat("dd-MM-yyyy").format((Date) rs.getDate("tgl_lahir")));
                            param.put("alamat", rs.getString("alamat"));
                            param.put("pekerjaan", rs.getString("pekerjaan"));
                            param.put("noktp", rs.getString("no_ktp"));
                            param.put("namars", akses.getnamars());
                            param.put("alamatrs", akses.getalamatrs());
                            param.put("kotars", akses.getkabupatenrs());
                            param.put("propinsirs", akses.getpropinsirs());
                            param.put("kontakrs", akses.getkontakrs());
                            param.put("emailrs", akses.getemailrs());
                            param.put("userid", akses.getkode());
                            param.put("ipaddress", akses.getalamatip());
                        }
                    }
                }

                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select periksa_lab.no_rawat, periksa_lab.tgl_periksa, periksa_lab.jam, periksa_lab.status, periksa_lab.kategori, periksa_lab.kd_dokter, " +
                    "dokter.nm_dokter, periksa_lab.dokter_perujuk, perujuk.nm_dokter nm_perujuk, periksa_lab.nip, petugas.nama from periksa_lab join dokter " +
                    "on periksa_lab.kd_dokter = dokter.kd_dokter join dokter perujuk on periksa_lab.dokter_perujuk = perujuk.kd_dokter join petugas on " +
                    "periksa_lab.nip = petugas.nip where periksa_lab.no_rawat = ? group by periksa_lab.no_rawat, periksa_lab.tgl_periksa, periksa_lab.jam, " +
                    "periksa_lab.status, periksa_lab.kategori"
                )) {
                    ps.setString(1, lblNoRawat.getText());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Sequel.deleteTemporaryLab();
                            i = 0;
                            if (rs.getString("status").equalsIgnoreCase("ralan")) {
                                kamar = "Poli";
                                namaKamar = Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik join reg_periksa on poliklinik.kd_poli = reg_periksa.kd_poli where reg_periksa.no_rawat = ?", lblNoRawat.getText());
                            } else {
                                kamar = "Kamar";
                                namaKamar = unit;
                            }
                            param.put("kamar", kamar);
                            param.put("namakamar", namaKamar);
                            param.put("pengirim", rs.getString("nm_perujuk"));
                            param.put("tanggal", rs.getString("tgl_periksa"));
                            param.put("jam", rs.getString("jam"));
                            param.put("penjab", rs.getString("nm_dokter"));
                            param.put("petugas", rs.getString("nama"));
                            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + rs.getString("tgl_periksa"));
                            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("nip"));
                            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nama") + "\nID " + (finger.isBlank() ? rs.getString("nip") : finger) + "\n" + rs.getString("tgl_periksa"));
                            param.put("ttd", Sequel.cariGambarSmc("select dokter_ttdbasah.gambar_ttd from dokter_ttdbasah where dokter_ttdbasah.kd_dokter = ?", rs.getString("kd_dokter")));
                            if (rs.getString("kategori").equals("PK")) {
                                try (PreparedStatement ps2 = koneksi.prepareStatement(
                                    "select periksa_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan from periksa_lab join jns_perawatan_lab " +
                                    "on periksa_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where periksa_lab.no_rawat = ? " +
                                    "and periksa_lab.tgl_periksa = ? and periksa_lab.jam = ? and periksa_lab.status = ? and periksa_lab.kategori = ?"
                                )) {
                                    ps2.setString(1, lblNoRawat.getText());
                                    ps2.setString(2, rs.getString("tgl_periksa"));
                                    ps2.setString(3, rs.getString("jam"));
                                    ps2.setString(4, rs.getString("status"));
                                    ps2.setString(5, rs.getString("kategori"));
                                    try (ResultSet rs2 = ps2.executeQuery()) {
                                        while (rs2.next()) {
                                            Sequel.temporaryLab(String.valueOf(++i), rs2.getString("nm_perawatan"));
                                            try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                "select template_laboratorium.Pemeriksaan, detail_periksa_lab.nilai, template_laboratorium.satuan, " +
                                                "detail_periksa_lab.nilai_rujukan, detail_periksa_lab.biaya_item, detail_periksa_lab.keterangan, " +
                                                "detail_periksa_lab.kd_jenis_prw from detail_periksa_lab join template_laboratorium on " +
                                                "detail_periksa_lab.id_template = template_laboratorium.id_template where detail_periksa_lab.no_rawat = ? " +
                                                "and detail_periksa_lab.kd_jenis_prw = ? and detail_periksa_lab.tgl_periksa = ? and detail_periksa_lab.jam = ? " +
                                                "order by template_laboratorium.urut"
                                            )) {
                                                ps3.setString(1, lblNoRawat.getText());
                                                ps3.setString(2, rs2.getString("kd_jenis_prw"));
                                                ps3.setString(3, rs.getString("tgl_periksa"));
                                                ps3.setString(4, rs.getString("jam"));
                                                try (ResultSet rs3 = ps3.executeQuery()) {
                                                    while (rs3.next()) {
                                                        Sequel.temporaryLab(
                                                            String.valueOf(++i), "  " + rs3.getString("Pemeriksaan"), rs3.getString("nilai"),
                                                            rs3.getString("satuan"), rs3.getString("nilai_rujukan"), rs3.getString("keterangan")
                                                        );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                try (PreparedStatement ps2 = koneksi.prepareStatement(
                                    "select noorder, tgl_permintaan, jam_permintaan " +
                                    "from permintaan_lab where no_rawat = ? and tgl_hasil = ? and jam_hasil = ?"
                                )) {
                                    ps2.setString(1, lblNoRawat.getText());
                                    ps2.setString(2, rs.getString("tgl_periksa"));
                                    ps2.setString(3, rs.getString("jam"));
                                    try (ResultSet rs2 = ps2.executeQuery()) {
                                        if (rs2.next()) {
                                            param.put("nopermintaan", rs2.getString("noorder"));
                                            param.put("tanggalpermintaan", rs2.getString("tgl_permintaan"));
                                            param.put("jampermintaan", rs2.getString("jam_permintaan"));
                                            Valid.reportSmc("rptPeriksaLab4PermintaanKompilasi.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                        } else {
                                            Valid.reportSmc("rptPeriksaLab4Kompilasi.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                        }
                                    }
                                }
                            } else if (rs.getString("kategori").equals("PA")) {
                                try (PreparedStatement ps2 = koneksi.prepareStatement(
                                    "select jns_perawatan_lab.nm_perawatan, detail_periksa_labpa.diagnosa_klinik, detail_periksa_labpa.makroskopik, detail_periksa_labpa.mikroskopik, detail_periksa_labpa.kesimpulan, detail_periksa_labpa.kesan " +
                                    "from detail_periksa_labpa join jns_perawatan_lab on detail_periksa_labpa.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where no_rawat = ? and tgl_periksa = ? and jam = ?"
                                )) {
                                    ps2.setString(1, lblNoRawat.getText());
                                    ps2.setString(2, rs.getString("tgl_periksa"));
                                    ps2.setString(3, rs.getString("jam"));
                                    try (ResultSet rs2 = ps2.executeQuery()) {
                                        while (rs2.next()) {
                                            Sequel.temporaryLab(String.valueOf(++i), rs2.getString("nm_perawatan"), rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4), rs2.getString(5));
                                        }
                                    }
                                }
                                try (PreparedStatement ps2 = koneksi.prepareStatement(
                                    "select noorder, tgl_permintaan, jam_permintaan " +
                                    "from permintaan_labpa where no_rawat = ? and tgl_hasil = ? and jam_hasil = ?"
                                )) {
                                    ps2.setString(1, lblNoRawat.getText());
                                    ps2.setString(2, rs.getString("tgl_periksa"));
                                    ps2.setString(3, rs.getString("jam"));
                                    try (ResultSet rs2 = ps2.executeQuery()) {
                                        param.put("ttd", Sequel.cariGambarSmc("select dokter_ttdbasah.gambar_ttd from dokter_ttdbasah where dokter_ttdbasah.kd_dokter = ?", rs.getString("kd_dokter")));
                                        if (rs2.next()) {
                                            param.put("nopermintaan", rs2.getString("noorder"));
                                            param.put("tanggalpermintaan", rs2.getString("tgl_permintaan"));
                                            param.put("jampermintaan", rs2.getString("jam_permintaan"));
                                            Valid.reportSmc("rptPeriksaLabPermintaanPAKompilasi.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                        } else {
                                            Valid.reportSmc("rptPeriksaLabPAKompilasi.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                        }
                                    }
                                }
                            } else if (rs.getString("kategori").equals("MB")) {
                                JOptionPane.showMessageDialog(null, "Maaf, bagian ini belum disupport..!!\nSilahkan hubungi administrator");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mencari hasil pemeriksaan lab!");
            }
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnHasilLabActionPerformed

    private void btnHasilRadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHasilRadActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select pasien.jk, date_format(pasien.tgl_lahir, '%d-%m-%Y') as tgllahir, concat(reg_periksa.umurdaftar, ' ', reg_periksa.sttsumur) as umur, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, periksa_radiologi.dokter_perujuk, " +
                "dokter_perujuk.nm_dokter nm_dokter_perujuk, periksa_radiologi.tgl_periksa, periksa_radiologi.jam, periksa_radiologi.kd_dokter, dokter.nm_dokter, periksa_radiologi.nip, petugas.nama nama_petugas, jns_perawatan_radiologi.nm_perawatan, " +
                "periksa_radiologi.status, periksa_radiologi.proyeksi, periksa_radiologi.kV, periksa_radiologi.mAS, periksa_radiologi.FFD, periksa_radiologi.BSF, periksa_radiologi.inak, periksa_radiologi.jml_penyinaran, periksa_radiologi.dosis " +
                "from periksa_radiologi join reg_periksa on periksa_radiologi.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join dokter dokter_perujuk on periksa_radiologi.dokter_perujuk = dokter_perujuk.kd_dokter " +
                "join dokter on periksa_radiologi.kd_dokter = dokter.kd_dokter join petugas on periksa_radiologi.nip = petugas.nip join jns_perawatan_radiologi on periksa_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw " +
                "left join kelurahan on pasien.kd_kel = kelurahan.kd_kel left join kecamatan on pasien.kd_kec = kecamatan.kd_kec left join kabupaten on pasien.kd_kab = kabupaten.kd_kab where periksa_radiologi.no_rawat = ?"
            )) {
                ps.setString(1, lblNoRawat.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StringBuilder sb = new StringBuilder();

                        sb.append(rs.getString("nm_perawatan"));

                        if (rs.getString("proyeksi") != null && rs.getString("proyeksi").trim().length() > 0) {
                            sb.append(" dengan proyeksi : ").append(rs.getString("proyeksi"));
                        }

                        if (rs.getString("kV") != null && rs.getString("kV").trim().length() > 0) {
                            sb.append(", kV : ").append(rs.getString("kV"));
                        }

                        if (rs.getString("mAS") != null && rs.getString("mAS").trim().length() > 0) {
                            sb.append(", mAS : ").append(rs.getString("mAS"));
                        }

                        if (rs.getString("FFD") != null && rs.getString("FFD").trim().length() > 0) {
                            sb.append(", FFD : ").append(rs.getString("FFD"));
                        }

                        if (rs.getString("BSF") != null && rs.getString("BSF").trim().length() > 0) {
                            sb.append(", BSF : ").append(rs.getString("BSF"));
                        }

                        if (rs.getString("Inak") != null && rs.getString("Inak").trim().length() > 0) {
                            sb.append(", Inak : ").append(rs.getString("Inak"));
                        }

                        if (rs.getString("jml_penyinaran") != null && rs.getString("jml_penyinaran").trim().length() > 0) {
                            sb.append(", Jumlah penyinaran : ").append(rs.getString("jml_penyinaran"));
                        }

                        if (rs.getString("dosis") != null && rs.getString("dosis").trim().length() > 0) {
                            sb.append(", Dosis Radiasi : ").append(rs.getString("dosis"));
                        }

                        Map<String, Object> param = new HashMap<>();
                        param.put("noperiksa", lblNoRawat.getText());
                        param.put("norm", lblNoRM.getText());
                        param.put("namapasien", lblNamaPasien.getText());
                        param.put("jkel", rs.getString("jk"));
                        param.put("umur", rs.getString("umur"));
                        param.put("lahir", rs.getString("tgllahir"));
                        param.put("pengirim", rs.getString("nm_dokter_perujuk"));
                        param.put("tanggal", rs.getString("tgl_periksa"));
                        param.put("penjab", rs.getString("nm_dokter"));
                        param.put("petugas", rs.getString("nama_petugas"));
                        param.put("alamat", rs.getString("alamat"));
                        String kamar = "", kelas = "", namaKamar = "", noRawatIbu = "";
                        if (lblStatusRawat.getText().contains("Ranap")) {
                            noRawatIbu = Sequel.cariIsiSmc("select no_rawat from ranap_gabung where no_rawat2 = ?", lblNoRawat.getText());
                            if (!noRawatIbu.isBlank()) {
                                kamar = Sequel.cariIsiSmc("select ifnull(kd_kamar, '') from kamar_inap where no_rawat = ? order by tgl_masuk desc, jam_masuk desc limit 1", noRawatIbu);
                                kelas = Sequel.cariIsiSmc("select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar = kamar_inap.kd_kamar where no_rawat = ? order by str_to_date(concat(kamar_inap.tgl_masuk, ' ', kamar_inap.jam_masuk), '%Y-%m-%d %H:%i:%s') desc limit 1", noRawatIbu);
                            } else {
                                kamar = Sequel.cariIsiSmc("select ifnull(kd_kamar, '') from kamar_inap where no_rawat = ? order by tgl_masuk desc limit 1", lblNoRawat.getText());
                                kelas = Sequel.cariIsiSmc("select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar = kamar_inap.kd_kamar where no_rawat = ? order by str_to_date(concat(kamar_inap.tgl_masuk, ' ', kamar_inap.jam_masuk), '%Y-%m-%d %H:%i:%s') desc limit 1", lblNoRawat.getText());
                            }
                            namaKamar = kamar + ", " + Sequel.cariIsiSmc("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal = kamar.kd_bangsal where kamar.kd_kamar = ?", kamar);
                            kamar = "Kamar";
                        } else {
                            kelas = "Rawat Jalan";
                            kamar = "Poli";
                            namaKamar = Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli = reg_periksa.kd_poli where reg_periksa.no_rawat = ?", lblNoRawat.getText());
                        }
                        param.put("kamar", kamar);
                        param.put("namakamar", namaKamar);
                        param.put("pemeriksaan", sb.toString());
                        param.put("jam", rs.getString("jam"));
                        param.put("namars", akses.getnamars());
                        param.put("alamatrs", akses.getalamatrs());
                        param.put("kotars", akses.getkabupatenrs());
                        param.put("propinsirs", akses.getpropinsirs());
                        param.put("kontakrs", akses.getkontakrs());
                        param.put("emailrs", akses.getemailrs());
                        param.put("hasil", Sequel.cariIsiSmc("select hasil from hasil_radiologi where no_rawat = ? and tgl_periksa = ? and jam = ?", lblNoRawat.getText(), rs.getString("tgl_periksa"), rs.getString("jam")));
                        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("tgl_periksa")));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("nip"));
                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nama_petugas") + "\nID " + (finger.isBlank() ? rs.getString("nip") : finger) + "\n" + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("tgl_periksa")));
                        param.put("ttd", Sequel.cariGambarSmc("select dokter_ttdbasah.gambar_ttd from dokter_ttdbasah where dokter_ttdbasah.kd_dokter = ?", rs.getString("kd_dokter")));
                        Valid.reportSmc("rptPeriksaRadiologiKompilasi.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mencari hasil pemeriksaan Radiologi!");
            }
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnHasilRadActionPerformed

    private void btnSurkonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSurkonActionPerformed
        if (btnSEP.getText().equals("Tidak ada")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
            String noSurat = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", btnSEP.getText());
            String tglSurat = Sequel.cariIsiSmc("select date_format(tgl_surat, '%d-%m-%Y') from bridging_surat_kontrol_bpjs where no_surat = ?", noSurat);
            String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs = (select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat = ?)", noSurat);
            String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
            param.put("parameter", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", btnSEP.getText()));
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + kodeDokter + "\n" + tglSurat);
            Valid.reportSmc(
                "rptBridgingSuratKontrol2.jasper", "report", "::[ Data Surat Kontrol VClaim ]::", param,
                "select bridging_sep.no_rawat, bridging_sep.no_sep, bridging_sep.no_kartu, bridging_sep.nomr, bridging_sep.nama_pasien, bridging_sep.tanggal_lahir, bridging_sep.jkel, bridging_sep.diagawal, bridging_sep.nmdiagnosaawal, bridging_surat_kontrol_bpjs.tgl_surat, " +
                "bridging_surat_kontrol_bpjs.no_surat, bridging_surat_kontrol_bpjs.tgl_rencana, bridging_surat_kontrol_bpjs.kd_dokter_bpjs, bridging_surat_kontrol_bpjs.nm_dokter_bpjs, bridging_surat_kontrol_bpjs.kd_poli_bpjs, bridging_surat_kontrol_bpjs.nm_poli_bpjs " +
                "from bridging_sep join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", btnSEP.getText())
            );
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSurkonActionPerformed

    private void btnSPRIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSPRIActionPerformed
        if (btnSEP.getText().equals("Tidak ada")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data pasien terlebih dahulu!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
            param.put("parameter", lblNoRawat.getText());
            String noSPRI = Sequel.cariIsiSmc("select no_surat from bridging_surat_pri_bpjs where no_rawat = ? order by no_surat desc", lblNoRawat.getText());
            String kodeDokter = Sequel.cariIsiSmc("Select kd_dokter_bpjs from bridging_surat_pri_bpjs where no_surat = ?", noSPRI);
            String namaDokter = Sequel.cariIsiSmc("select nm_dokter_bpjs from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
            String tglSPRI = Sequel.cariIsiSmc("select date_format(tgl_rencana, '%d-%m-%Y') from bridging_surat_pri_bpjs where no_surat = ?", noSPRI);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + kodeDokter + "\n" + tglSPRI);
            Valid.reportSmc("rptBridgingSuratPRI2.jasper", "report", "::[ Data Surat PRI VClaim ]::", param,
                "select bridging_surat_pri_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk " +
                "from reg_periksa join bridging_surat_pri_bpjs on bridging_surat_pri_bpjs.no_rawat = reg_periksa.no_rawat " +
                "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where bridging_surat_pri_bpjs.no_surat = ?", noSPRI
            );
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSPRIActionPerformed

    private void BtnSimpanKodingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanKodingActionPerformed
        if (flagklaim == 1) {
            JOptionPane.showMessageDialog(null, "Klaim sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!btnSEP.getText().isBlank()) {
            if (flagklaim >= 5) {
                panelIdrg.simpan();
            } else {
                JOptionPane.showMessageDialog(null, "Status grouping IDRG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
            if (flagklaim > 2 && flagklaim < 5) {
                panelInacbg.simpan();
            } else if (flagklaim == 2) {
                JOptionPane.showMessageDialog(null, "Status grouping INACBG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else if (flagklaim >= 5) {
                JOptionPane.showMessageDialog(null, "Hasil grouping IDRG belum tersedia..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
            tampilINACBG();
        }
    }//GEN-LAST:event_BtnSimpanKodingActionPerformed

    private void BtnHapusKodingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusKodingActionPerformed
        if (flagklaim == 1) {
            JOptionPane.showMessageDialog(null, "Klaim sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!btnSEP.getText().isBlank()) {
            if (flagklaim >= 5) {
                panelIdrg.hapus();
            } else {
                JOptionPane.showMessageDialog(null, "Status grouping IDRG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
            if (flagklaim > 2 && flagklaim < 5) {
                panelInacbg.hapus();
            } else if (flagklaim == 2) {
                JOptionPane.showMessageDialog(null, "Status grouping INACBG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else if (flagklaim >= 5) {
                JOptionPane.showMessageDialog(null, "Hasil grouping IDRG belum tersedia..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
            tampilINACBG();
        }
    }//GEN-LAST:event_BtnHapusKodingActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
//        revalidate();
//        Dimension newD = new Dimension(jPanel2.getWidth() - 32, panelBiasa1.getPreferredSize().height);
//        panelIdrg.setPreferredSize(new Dimension(newD.width - 4, panelIdrg.getPreferredSize().height));
//        panelIdrg.setSize(new Dimension(newD.width - 4, panelIdrg.getPreferredSize().height));
//        panelIdrg.revalidate(newD.width - 4);
//        panelInacbg.setPreferredSize(new Dimension(newD.width - 4, panelInacbg.getPreferredSize().height));
//        panelInacbg.setSize(new Dimension(newD.width - 4, panelInacbg.getPreferredSize().height));
//        panelInacbg.revalidate(newD.width - 4);
//        tabPaneKoding.setPreferredSize(new Dimension(newD.width, tabPaneKoding.getPreferredSize().height));
//        tabPaneKoding.setSize(new Dimension(newD.width, tabPaneKoding.getPreferredSize().height));
//        tabPaneKoding.revalidate();
//        panelBiasa1.setPreferredSize(newD);
//        panelBiasa1.setSize(newD);
//        scrollPane1.setPreferredSize(newD);
//        scrollPane1.setSize(newD);
//        BtnHapusKoding.setLocation(panelBiasa1.getWidth() - BtnHapusKoding.getWidth() - 4, BtnHapusKoding.getY());
//        revalidate();
    }//GEN-LAST:event_formWindowOpened

    private void ppUpdateTanggalPulangSEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppUpdateTanggalPulangSEPActionPerformed
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data SEP ranap pasien terlebih dahulu!");
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try (PreparedStatement pspulang = koneksi.prepareStatement(
                "select bridging_sep.no_rawat, bridging_sep.no_sep, pasien.no_rkm_medis, pasien.nm_pasien, if (bridging_sep.tglpulang is null or bridging_sep.tglpulang = '0000-00-00 00:00:00', " +
                "(select if (max(concat(kamar_inap.tgl_keluar, ' ',kamar_inap.jam_keluar)) = '0000-00-00 00:00:00' or max(concat(kamar_inap.tgl_keluar, ' ', kamar_inap.jam_keluar)) is null, now(), max(concat(kamar_inap.tgl_keluar, ' ', kamar_inap.jam_keluar))) " +
                "from kamar_inap where kamar_inap.no_rawat = bridging_sep.no_rawat), bridging_sep.tglpulang) as tglpulang from bridging_sep join reg_periksa on bridging_sep.no_rawat = reg_periksa.no_rawat " +
                "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where bridging_sep.jnspelayanan = '1' and bridging_sep.no_sep = ? order by bridging_sep.tglsep desc limit 1"
            )) {
                pspulang.setString(1, tbKompilasi.getValueAt(selectedRow, 2).toString());
                try (ResultSet rspulang = pspulang.executeQuery()) {
                    if (rspulang.next()) {
                        WindowUpdatePulang.setSize(608, 264);
                        WindowUpdatePulang.setLocationRelativeTo(internalFrame1);
                        TNoRwPulang.setText(rspulang.getString("no_rawat"));
                        TNoSEPRanapPulang.setText(rspulang.getString("no_sep"));
                        TNoRMPulang.setText(rspulang.getString("no_rkm_medis"));
                        TPasienPulang.setText(rspulang.getString("nm_pasien"));
                        TanggalPulang.setDate(rspulang.getTimestamp("tglpulang"));
                        WindowUpdatePulang.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Pasien tersebut belum terbit SEP, silahkan hubungi bagian terkait..!!");
                        TCari.requestFocus();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_ppUpdateTanggalPulangSEPActionPerformed

    private void BtnCloseIn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseIn8ActionPerformed
        WindowUpdatePulang.dispose();
    }//GEN-LAST:event_BtnCloseIn8ActionPerformed

    private void BtnSimpan8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan8ActionPerformed
        if (TNoRwPulang.getText().isBlank() || TNoSEPRanapPulang.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data pasiennya terlebih dahulu..!!");
            return;
        }
        try {
            ApiBPJS api = new ApiBPJS();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            String utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            String URL = koneksiDB.URLAPIBPJS() + "/SEP/2.0/updtglplg";
            String json = "{" +
                "\"request\": {" +
                "\"t_sep\": {" +
                "\"noSep\": \"" + TNoSEPRanapPulang.getText() + "\"," +
                "\"statusPulang\": \"" + StatusPulang.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"noSuratMeninggal\": \"" + NoSuratKematian.getText().trim() + "\"," +
                "\"tglMeninggal\": \"" + (NoSuratKematian.getText().trim().isBlank() ? "" : Valid.SetTgl(TanggalKematian.getSelectedItem().toString())) + "\"," +
                "\"tglPulang\": \"" + Valid.SetTgl(TanggalPulang.getSelectedItem().toString()) + "\"," +
                "\"noLPManual\": \"" + NoLPManual.getText().trim() + "\"," +
                "\"user\": \"" + akses.getkode().trim().substring(0, 9) + "\"" +
                "}" +
                "}" +
                "}";
            System.out.println("JSON : " + json);
            HttpEntity entity = new HttpEntity(json, headers);
            JsonNode root, nameNode;
            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.PUT, entity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                Sequel.mengupdateSmc("bridging_sep", "tglpulang = ?", "no_sep = ?",
                    Valid.SetTgl(TanggalPulang.getSelectedItem().toString()) + " " + TanggalPulang.getSelectedItem().toString().substring(11, 19),
                    TNoSEPRanapPulang.getText());
                JOptionPane.showMessageDialog(null, "Proses update pulang di BPJS selesai!");
                WindowUpdatePulang.dispose();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus..!!");
            }
        }
    }//GEN-LAST:event_BtnSimpan8ActionPerformed

    private void StatusPulangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_StatusPulangItemStateChanged
        if (StatusPulang.getSelectedIndex() == 2) {
            NoSuratKematian.setEditable(true);
            TanggalKematian.setEnabled(true);
        } else {
            NoSuratKematian.setEditable(false);
            TanggalKematian.setEnabled(false);
        }
    }//GEN-LAST:event_StatusPulangItemStateChanged

    private void btnRiwayatPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPasienActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data pasien terlebih dahulu..!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (resume == null) {
                resume = new RMRiwayatPerawatan(null, false);
            }
            resume.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            resume.setLocationRelativeTo(internalFrame1);
            resume.setVisible(true);
            resume.setNoRMKompilasi(lblNoRawat.getText(), lblNoRM.getText());
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnRiwayatPasienActionPerformed

    private void btnInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvoiceActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            Valid.panggilUrl("berkasrawat/loginlihatbilling.php?act=login&norawat=" + lblNoRawat.getText() + "&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB());
        }
    }//GEN-LAST:event_btnInvoiceActionPerformed

    private void btnHasilKlaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHasilKlaimActionPerformed
        if (btnSEP.getText().equals("Tidak ada")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu!");
        } else {
            String file = "inacbg/" + Sequel.cariIsiSmc("select path from inacbg_cetak_klaim where no_sep = ?", btnSEP.getText());
            file = file + "?hash=" + DigestUtils.sha256Hex(btnSEP.getText() + Instant.now().toString());
            Valid.panggilUrl(file);
        }
    }//GEN-LAST:event_btnHasilKlaimActionPerformed

    private void btnTriaseIGDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTriaseIGDActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String detailTriase = "";
            int i = 0;
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Sequel.deleteTemporary();
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select t.no_rawat, " +
                "(select count(*) from data_triase_igddetail_skala1 s1 where s1.no_rawat = t.no_rawat) as cs1, " +
                "(select count(*) from data_triase_igddetail_skala2 s2 where s2.no_rawat = t.no_rawat) as cs2, " +
                "(select count(*) from data_triase_igddetail_skala3 s3 where s3.no_rawat = t.no_rawat) as cs3, " +
                "(select count(*) from data_triase_igddetail_skala4 s4 where s4.no_rawat = t.no_rawat) as cs4, " +
                "(select count(*) from data_triase_igddetail_skala5 s5 where s5.no_rawat = t.no_rawat) as cs5 " +
                "from data_triase_igd t where t.no_rawat = ?"
            )) {
                ps.setString(1, lblNoRawat.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt("cs1") > 0) {
                            try (PreparedStatement ps1 = koneksi.prepareStatement(
                                "select data_triase_igdprimer.keluhan_utama, data_triase_igdprimer.kebutuhan_khusus, data_triase_igdprimer.catatan, data_triase_igdprimer.plan, data_triase_igdprimer.tanggaltriase, " +
                                "data_triase_igdprimer.nik, data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, " +
                                "data_triase_igd.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                                "from data_triase_igdprimer join data_triase_igd on data_triase_igdprimer.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                                "join reg_periksa on data_triase_igdprimer.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdprimer.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                            )) {
                                ps1.setString(1, lblNoRawat.getText());
                                try (ResultSet rs1 = ps1.executeQuery()) {
                                    if (rs1.next()) {
                                        param.put("norawat", rs1.getString("no_rawat"));
                                        param.put("norm", rs1.getString("no_rkm_medis"));
                                        param.put("namapasien", rs1.getString("nm_pasien"));
                                        param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                        param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                        param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                        param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                        param.put("caradatang", rs1.getString("cara_masuk"));
                                        param.put("macamkasus", rs1.getString("macam_kasus"));
                                        param.put("keluhanutama", rs1.getString("keluhan_utama"));
                                        param.put("kebutuhankhusus", rs1.getString("kebutuhan_khusus"));
                                        param.put("plan", rs1.getString("plan"));
                                        param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                        param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                        param.put("pegawai", rs1.getString("nama"));
                                        param.put("catatan", rs1.getString("catatan"));
                                        param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                                            "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                            "join master_triase_skala1 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala1.kode_pemeriksaan " +
                                            "join data_triase_igddetail_skala1 on master_triase_skala1.kode_skala1 = data_triase_igddetail_skala1.kode_skala1 " +
                                            "where data_triase_igddetail_skala1.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                            "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                        )) {
                                            ps2.setString(1, lblNoRawat.getText());
                                            try (ResultSet rs2 = ps2.executeQuery()) {
                                                while (rs2.next()) {
                                                    detailTriase = "";
                                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                        "select master_triase_skala1.pengkajian_skala1 from master_triase_skala1 " +
                                                        "join data_triase_igddetail_skala1 on master_triase_skala1.kode_skala1 = data_triase_igddetail_skala1.kode_skala1 " +
                                                        "where master_triase_skala1.kode_pemeriksaan = ? and data_triase_igddetail_skala1.no_rawat = ? " +
                                                        "order by data_triase_igddetail_skala1.kode_skala1"
                                                    )) {
                                                        ps3.setString(1, rs2.getString(1));
                                                        ps3.setString(2, lblNoRawat.getText());
                                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                                            while (rs3.next()) {
                                                                detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                            }
                                                        }
                                                    }
                                                    detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                    Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                                }
                                            }
                                        }
                                        Valid.reportTempSmc("rptLembarTriaseSkala1.jasper", "report", "::[ Triase Skala 1 ]::", param);
                                    }
                                }
                            }
                        } else if (rs.getInt("cs2") > 0) {
                            try (PreparedStatement ps1 = koneksi.prepareStatement(
                                "select data_triase_igdprimer.keluhan_utama, data_triase_igdprimer.kebutuhan_khusus, data_triase_igdprimer.catatan, data_triase_igdprimer.plan, data_triase_igdprimer.tanggaltriase, " +
                                "data_triase_igdprimer.nik, data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, " +
                                "data_triase_igd.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                                "from data_triase_igdprimer join data_triase_igd on data_triase_igdprimer.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                                "join reg_periksa on data_triase_igdprimer.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdprimer.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                            )) {
                                ps1.setString(1, lblNoRawat.getText());
                                try (ResultSet rs1 = ps1.executeQuery()) {
                                    if (rs1.next()) {
                                        param.put("norawat", rs1.getString("no_rawat"));
                                        param.put("norm", rs1.getString("no_rkm_medis"));
                                        param.put("namapasien", rs1.getString("nm_pasien"));
                                        param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                        param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                        param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                        param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                        param.put("caradatang", rs1.getString("cara_masuk"));
                                        param.put("macamkasus", rs1.getString("macam_kasus"));
                                        param.put("keluhanutama", rs1.getString("keluhan_utama"));
                                        param.put("kebutuhankhusus", rs1.getString("kebutuhan_khusus"));
                                        param.put("plan", rs1.getString("plan"));
                                        param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                        param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                        param.put("pegawai", rs1.getString("nama"));
                                        param.put("catatan", rs1.getString("catatan"));
                                        param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                                            "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                            "join master_triase_skala2 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala2.kode_pemeriksaan " +
                                            "join data_triase_igddetail_skala2 on master_triase_skala2.kode_skala2 = data_triase_igddetail_skala2.kode_skala2 " +
                                            "where data_triase_igddetail_skala2.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                            "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                        )) {
                                            ps2.setString(1, lblNoRawat.getText());
                                            try (ResultSet rs2 = ps2.executeQuery()) {
                                                while (rs2.next()) {
                                                    detailTriase = "";
                                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                        "select master_triase_skala2.pengkajian_skala2 from master_triase_skala2 " +
                                                        "join data_triase_igddetail_skala2 on master_triase_skala2.kode_skala2 = data_triase_igddetail_skala2.kode_skala2 " +
                                                        "where master_triase_skala2.kode_pemeriksaan = ? and data_triase_igddetail_skala2.no_rawat = ? " +
                                                        "order by data_triase_igddetail_skala2.kode_skala2"
                                                    )) {
                                                        ps3.setString(1, rs2.getString(1));
                                                        ps3.setString(2, lblNoRawat.getText());
                                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                                            while (rs3.next()) {
                                                                detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                            }
                                                        }
                                                    }
                                                    detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                    Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                                }
                                            }
                                        }
                                        Valid.reportTempSmc("rptLembarTriaseSkala2.jasper", "report", "::[ Triase Skala 2 ]::", param);
                                    }
                                }
                            }
                        } else if (rs.getInt("cs3") > 0) {
                            try (PreparedStatement ps1 = koneksi.prepareStatement(
                                "select data_triase_igdsekunder.anamnesa_singkat, data_triase_igdsekunder.catatan, data_triase_igdsekunder.plan, data_triase_igdsekunder.tanggaltriase, data_triase_igdsekunder.nik, " +
                                "data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, data_triase_igd.no_rawat, " +
                                "pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                                "from data_triase_igdsekunder join data_triase_igd on data_triase_igdsekunder.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                                "join reg_periksa on data_triase_igd.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdsekunder.nik = pegawai.nik where data_triase_igdsekunder.no_rawat = ?"
                            )) {
                                ps1.setString(1, lblNoRawat.getText());
                                try (ResultSet rs1 = ps1.executeQuery()) {
                                    if (rs1.next()) {
                                        param.put("norawat", rs1.getString("no_rawat"));
                                        param.put("norm", rs1.getString("no_rkm_medis"));
                                        param.put("namapasien", rs1.getString("nm_pasien"));
                                        param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                        param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                        param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                        param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                        param.put("caradatang", rs1.getString("cara_masuk"));
                                        param.put("macamkasus", rs1.getString("macam_kasus"));
                                        param.put("keluhanutama", rs1.getString("anamnesa_singkat"));
                                        param.put("plan", rs1.getString("plan"));
                                        param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                        param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                        param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                        param.put("pegawai", rs1.getString("nama"));
                                        param.put("catatan", rs1.getString("catatan"));
                                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                                            "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                            "join master_triase_skala3 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala3.kode_pemeriksaan " +
                                            "join data_triase_igddetail_skala3 on master_triase_skala3.kode_skala3 = data_triase_igddetail_skala3.kode_skala3 " +
                                            "where data_triase_igddetail_skala3.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                            "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                        )) {
                                            ps2.setString(1, lblNoRawat.getText());
                                            try (ResultSet rs2 = ps2.executeQuery()) {
                                                while (rs2.next()) {
                                                    detailTriase = "";
                                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                        "select master_triase_skala3.pengkajian_skala3 from master_triase_skala3 join data_triase_igddetail_skala3 " +
                                                        "on master_triase_skala3.kode_skala3 = data_triase_igddetail_skala3.kode_skala3 where master_triase_skala3.kode_pemeriksaan = ? " +
                                                        "and data_triase_igddetail_skala3.no_rawat = ? order by data_triase_igddetail_skala3.kode_skala3"
                                                    )) {
                                                        ps3.setString(1, rs2.getString(1));
                                                        ps3.setString(2, lblNoRawat.getText());
                                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                                            while (rs3.next()) {
                                                                detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                            }
                                                        }
                                                    }
                                                    detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                    Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                                }
                                            }
                                        }
                                        Valid.reportTempSmc("rptLembarTriaseSkala3.jasper", "report", "::[ Triase Skala 3 ]::", param);
                                    }
                                }
                            }
                        } else if (rs.getInt("cs4") > 0) {
                            try (PreparedStatement ps1 = koneksi.prepareStatement(
                                "select data_triase_igdsekunder.anamnesa_singkat, data_triase_igdsekunder.catatan, data_triase_igdsekunder.plan, data_triase_igdsekunder.tanggaltriase, data_triase_igdsekunder.nik, " +
                                "data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, data_triase_igd.no_rawat, " +
                                "pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                                "from data_triase_igdsekunder join data_triase_igd on data_triase_igdsekunder.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                                "join reg_periksa on data_triase_igdsekunder.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdsekunder.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                            )) {
                                ps1.setString(1, lblNoRawat.getText());
                                try (ResultSet rs1 = ps1.executeQuery()) {
                                    if (rs1.next()) {
                                        param.put("norawat", rs1.getString("no_rawat"));
                                        param.put("norm", rs1.getString("no_rkm_medis"));
                                        param.put("namapasien", rs1.getString("nm_pasien"));
                                        param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                        param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                        param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                        param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                        param.put("caradatang", rs1.getString("cara_masuk"));
                                        param.put("macamkasus", rs1.getString("macam_kasus"));
                                        param.put("keluhanutama", rs1.getString("anamnesa_singkat"));
                                        param.put("plan", rs1.getString("plan"));
                                        param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                        param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                        param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                        param.put("pegawai", rs1.getString("nama"));
                                        param.put("catatan", rs1.getString("catatan"));
                                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                                            "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                            "join master_triase_skala4 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala4.kode_pemeriksaan " +
                                            "join data_triase_igddetail_skala4 on master_triase_skala4.kode_skala4 = data_triase_igddetail_skala4.kode_skala4 " +
                                            "where data_triase_igddetail_skala4.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                            "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                        )) {
                                            ps2.setString(1, lblNoRawat.getText());
                                            try (ResultSet rs2 = ps2.executeQuery()) {
                                                while (rs2.next()) {
                                                    detailTriase = "";
                                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                        "select master_triase_skala4.pengkajian_skala4 from master_triase_skala4 join data_triase_igddetail_skala4 " +
                                                        "on master_triase_skala4.kode_skala4 = data_triase_igddetail_skala4.kode_skala4 where master_triase_skala4.kode_pemeriksaan = ? " +
                                                        "and data_triase_igddetail_skala4.no_rawat = ? order by data_triase_igddetail_skala4.kode_skala4"
                                                    )) {
                                                        ps3.setString(1, rs2.getString(1));
                                                        ps3.setString(2, lblNoRawat.getText());
                                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                                            while (rs3.next()) {
                                                                detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                            }
                                                        }
                                                    }
                                                    detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                    Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                                }
                                            }
                                        }
                                        Valid.reportTempSmc("rptLembarTriaseSkala4.jasper", "report", "::[ Triase Skala 4 ]::", param);
                                    }
                                }
                            }
                        } else if (rs.getInt("cs5") > 0) {
                            try (PreparedStatement ps1 = koneksi.prepareStatement(
                                "select data_triase_igdsekunder.anamnesa_singkat, data_triase_igdsekunder.catatan, data_triase_igdsekunder.plan, data_triase_igdsekunder.tanggaltriase, data_triase_igdsekunder.nik, " +
                                "data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, data_triase_igd.no_rawat, " +
                                "pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                                "from data_triase_igdsekunder join data_triase_igd on data_triase_igdsekunder.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                                "join reg_periksa on data_triase_igdsekunder.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdsekunder.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                            )) {
                                ps1.setString(1, lblNoRawat.getText());
                                try (ResultSet rs1 = ps1.executeQuery()) {
                                    if (rs1.next()) {
                                        param.put("norawat", rs1.getString("no_rawat"));
                                        param.put("norm", rs1.getString("no_rkm_medis"));
                                        param.put("namapasien", rs1.getString("nm_pasien"));
                                        param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                        param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                        param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                        param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                        param.put("caradatang", rs1.getString("cara_masuk"));
                                        param.put("macamkasus", rs1.getString("macam_kasus"));
                                        param.put("keluhanutama", rs1.getString("anamnesa_singkat"));
                                        param.put("plan", rs1.getString("plan"));
                                        param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                        param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                        param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                        param.put("pegawai", rs1.getString("nama"));
                                        param.put("catatan", rs1.getString("catatan"));
                                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                                            "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                            "join master_triase_skala5 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala5.kode_pemeriksaan " +
                                            "join data_triase_igddetail_skala5 on master_triase_skala5.kode_skala5 = data_triase_igddetail_skala5.kode_skala5 " +
                                            "where data_triase_igddetail_skala5.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                            "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                        )) {
                                            ps2.setString(1, lblNoRawat.getText());
                                            try (ResultSet rs2 = ps2.executeQuery()) {
                                                while (rs2.next()) {
                                                    detailTriase = "";
                                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                        "select master_triase_skala5.pengkajian_skala5 from master_triase_skala5 join data_triase_igddetail_skala5 " +
                                                        "on master_triase_skala5.kode_skala5 = data_triase_igddetail_skala5.kode_skala5 where master_triase_skala5.kode_pemeriksaan = ? " +
                                                        "and data_triase_igddetail_skala5.no_rawat = ? order by data_triase_igddetail_skala5.kode_skala5"
                                                    )) {
                                                        ps3.setString(1, rs2.getString(1));
                                                        ps3.setString(2, lblNoRawat.getText());
                                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                                            while (rs3.next()) {
                                                                detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                            }
                                                        }
                                                    }
                                                    detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                    Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                                }
                                            }
                                        }
                                        Valid.reportTempSmc("rptLembarTriaseSkala5.jasper", "report", "::[ Triase Skala 5 ]::", param);
                                    }
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Triase tidak memiliki skala yang benar!");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnTriaseIGDActionPerformed

    private void BtnPenjaminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPenjaminActionPerformed
        penjab.isCek();
        penjab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        penjab.setLocationRelativeTo(internalFrame1);
        penjab.setAlwaysOnTop(false);
        penjab.setVisible(true);
    }//GEN-LAST:event_BtnPenjaminActionPerformed

    private void BtnPengaturanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPengaturanActionPerformed
        cekPengaturanKompilasi();
        WindowPengaturan.setSize(610, 232);
        WindowPengaturan.setLocationRelativeTo(internalFrame1);
        WindowPengaturan.setVisible(true);
    }//GEN-LAST:event_BtnPengaturanActionPerformed

    private void BtnTutupPengaturanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTutupPengaturanActionPerformed
        WindowPengaturan.dispose();
    }//GEN-LAST:event_BtnTutupPengaturanActionPerformed

    private void BtnBukaFolderExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBukaFolderExportActionPerformed
        try {
            Desktop.getDesktop().open(new File("./berkaspdf"));
        } catch (IOException e) {
            System.out.println("Notif : " + e);
        }
    }//GEN-LAST:event_BtnBukaFolderExportActionPerformed

    private void BtnSimpanPengaturanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanPengaturanActionPerformed
        try {
            String aplikasipdf = "", tanggalexport = "registrasi", maxmemory = TMaxMemory.getText().trim(), kategoriUpload = "";
            boolean hapusotomatis = CekAktifkanHapusOtomatis.isSelected();
            switch (CmbPilihanAplikasiPDF.getSelectedIndex()) {
                case 1:
                    aplikasipdf = "chrome";
                    break;
                case 2:
                    aplikasipdf = "firefox";
                    break;
                case 3:
                    aplikasipdf = "msedge";
                    break;
                case 4:
                    aplikasipdf = TPathAplikasiPDF.getText().trim();
                    break;
                case 5:
                    aplikasipdf = "disable";
                    break;
                default:
                    aplikasipdf = "";
                    break;
            }

            if (CmbPilihanTanggalExport.getSelectedIndex() == 1) {
                tanggalexport = "sep";
            } else {
                tanggalexport = "kompilasi";
            }

            if (CmbPilihanKategoriBerkas.getSelectedIndex() == 0) {
                kategoriUpload = "";
            } else {
                kategoriUpload = CmbPilihanKategoriBerkas.getSelectedItem().toString().substring(0, CmbPilihanKategoriBerkas.getSelectedItem().toString().indexOf("-")).trim();
            }

            File iyem = new File("./cache/pengaturankompilasi.iyem");
            iyem.createNewFile();
            try (FileWriter fw = new FileWriter(iyem)) {
                ObjectMapper mapper = new ObjectMapper();

                ObjectNode pengaturan = mapper.createObjectNode();
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    pengaturan.put("aplikasipdf", aplikasipdf.replace(File.separator, File.separator + File.separator));
                } else {
                    pengaturan.put("aplikasipdf", aplikasipdf);
                }
                pengaturan.put("tanggalexport", tanggalexport);
                pengaturan.put("maxmemory", maxmemory);
                pengaturan.put("hapusotomatis", hapusotomatis);
                pengaturan.put("kategoriuploadberkas", kategoriUpload);

                ObjectNode root = mapper.createObjectNode();
                root.set("pengaturan", pengaturan);
                fw.write(root.toString());
                fw.flush();

                aplikasiPDF = aplikasipdf;
                gunakanTanggalExport = tanggalexport;
                maxMemory = Integer.parseInt(maxmemory);
                hapusOtomatisDiagnosaProsedur = hapusotomatis;
                kategoriUploadBerkas = kategoriUpload;
                if (selectedRow >= 0) {
                    onListSelectionModelValueChanged(null);
                }
                JOptionPane.showMessageDialog(null, "Pengaturan kompilasi berhasil disimpan..!!");
                WindowPengaturan.dispose();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }//GEN-LAST:event_BtnSimpanPengaturanActionPerformed

    private void BtnPilihAplikasiPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihAplikasiPDFActionPerformed
        if (!TPathAplikasiPDF.getText().isBlank()) {
            fc.setCurrentDirectory(new File(TPathAplikasiPDF.getText().substring(0, TPathAplikasiPDF.getText().lastIndexOf(File.separator))));
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            TPathAplikasiPDF.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_BtnPilihAplikasiPDFActionPerformed

    private void BtnResetPengaturanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnResetPengaturanActionPerformed
        try {
            File iyem = new File("./cache/pengaturankompilasi.iyem");
            if (JOptionPane.showConfirmDialog(null, "Eeeiiittsss... Yakin mau reset pengaturan kompilasi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && !iyem.isDirectory()) {
                iyem.delete();
                cekPengaturanKompilasi();
                JOptionPane.showMessageDialog(null, "Pengaturan kompilasi berhasil direset..!!");
                WindowPengaturan.dispose();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mereset pengaturan kompilasi,\nSilahkan coba lagi.");
            System.out.println("Notif : " + e);
        }
    }//GEN-LAST:event_BtnResetPengaturanActionPerformed

    private void CmbPilihanAplikasiPDFItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CmbPilihanAplikasiPDFItemStateChanged
        if (CmbPilihanAplikasiPDF.getSelectedIndex() == 4) {
            TPathAplikasiPDF.setEditable(true);
            BtnPilihAplikasiPDF.setEnabled(true);
        } else {
            TPathAplikasiPDF.setText("");
            TPathAplikasiPDF.setEditable(false);
            BtnPilihAplikasiPDF.setEnabled(false);
        }
    }//GEN-LAST:event_CmbPilihanAplikasiPDFItemStateChanged

    private void tbKompilasiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbKompilasiKeyReleased
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                onListSelectionModelValueChanged(null);
            }
        }
    }//GEN-LAST:event_tbKompilasiKeyReleased

    private void BtnSimpanKodingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKodingKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnSimpanKodingActionPerformed(null);
        }
    }//GEN-LAST:event_BtnSimpanKodingKeyPressed

    private void WindowPengaturanWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_WindowPengaturanWindowActivated
        try (ResultSet rs = koneksi.createStatement().executeQuery("select * from master_berkas_digital order by master_berkas_digital.kode")) {
            if (rs.next()) {
                int selectedIndex = -1;
                CmbPilihanKategoriBerkas.removeAllItems();
                CmbPilihanKategoriBerkas.addItem("");
                do {
                    if (kategoriUploadBerkas.equals(rs.getString("kode"))) {
                        selectedIndex = CmbPilihanKategoriBerkas.getItemCount();
                    }
                    CmbPilihanKategoriBerkas.addItem((rs.getString("kode") + " - " + rs.getString("nama")));
                } while (rs.next());

                if (selectedIndex >= 0) {
                    CmbPilihanKategoriBerkas.setSelectedIndex(selectedIndex);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }//GEN-LAST:event_WindowPengaturanWindowActivated

    private void ppPilihSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPilihSemuaActionPerformed
        for (int i = tbKompilasi.getRowCount() - 1; i >= 0; i--) {
            tbKompilasi.setValueAt(true, i, 0);
        }
    }//GEN-LAST:event_ppPilihSemuaActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        for (int i = tbKompilasi.getRowCount() - 1; i >= 0; i--) {
            tbKompilasi.setValueAt(false, i, 0);
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void tbKompilasiMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbKompilasiMouseReleased
        int row = tbKompilasi.rowAtPoint(evt.getPoint());
        if (row >= 0) {
            tbKompilasi.setRowSelectionInterval(row, row);
            onListSelectionModelValueChanged(null);
        }
    }//GEN-LAST:event_tbKompilasiMouseReleased

    private void asalRujukanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_asalRujukanItemStateChanged
        switch (asalRujukan.getSelectedIndex()) {
            case 0:
                asalRujukanDipilih = "gp";
                break;
            case 1:
                asalRujukanDipilih = "hosp-trans";
                break;
            case 2:
                asalRujukanDipilih = "mp";
                break;
            case 3:
                asalRujukanDipilih = "outp";
                break;
            case 4:
                asalRujukanDipilih = "inp";
                break;
            case 5:
                asalRujukanDipilih = "emd";
                break;
            case 6:
                asalRujukanDipilih = "born";
                break;
            case 7:
                asalRujukanDipilih = "nursing";
                break;
            case 8:
                asalRujukanDipilih = "psych";
                break;
            case 9:
                asalRujukanDipilih = "rehab";
                break;
            default:
                asalRujukanDipilih = "other";
                break;
        }
    }//GEN-LAST:event_asalRujukanItemStateChanged

    private void naikKelasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_naikKelasItemStateChanged
        if (naikKelas.getSelectedIndex() == 0) {
            // indikator upgrade kelas tidak ada
        } else {
            // indikator upgrade kelas ada
            switch (naikKelas.getSelectedIndex()) {
                case 1:
                    naikKelasDipilih = "kelas_2";
                    break;
                case 2:
                    naikKelasDipilih = "kelas_1";
                    break;
                case 3:
                    naikKelasDipilih = "vip";
                    break;
                case 4:
                    naikKelasDipilih = "vvip";
                    break;
            }
        }
    }//GEN-LAST:event_naikKelasItemStateChanged

    private void btnUpdateTglPulangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateTglPulangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateTglPulangActionPerformed

    private void statusPulangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statusPulangItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_statusPulangItemStateChanged

    private void icuIndikatorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_icuIndikatorItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_icuIndikatorItemStateChanged

    private void dializerSingleUseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dializerSingleUseItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_dializerSingleUseItemStateChanged

    private void alteplaseIndikatorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_alteplaseIndikatorItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_alteplaseIndikatorItemStateChanged

    private void kelahiranItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_kelahiranItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_kelahiranItemStateChanged

    private void onsetKontraksiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_onsetKontraksiItemStateChanged
        switch (onsetKontraksi.getSelectedIndex()) {
            case 0:
                onsetKontraksiDipilih = "spontan";
                break;
            case 1:
                onsetKontraksiDipilih = "induksi";
                break;
            case 2:
                onsetKontraksiDipilih = "non_spontan_non_induksi";
                break;
        }
    }//GEN-LAST:event_onsetKontraksiItemStateChanged

    private void caraLahirItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_caraLahirItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_caraLahirItemStateChanged

    private void letakJaninItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_letakJaninItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_letakJaninItemStateChanged

    private void kondisiLahirItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_kondisiLahirItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_kondisiLahirItemStateChanged

    private void useManualItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useManualItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_useManualItemStateChanged

    private void useForcepItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useForcepItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_useForcepItemStateChanged

    private void useVacuumItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useVacuumItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_useVacuumItemStateChanged

    private void spesimenSHKDiambilItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_spesimenSHKDiambilItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spesimenSHKDiambilItemStateChanged

    private void lokasiSpesimenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lokasiSpesimenItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_lokasiSpesimenItemStateChanged

    private void alasanSpesimenTidakDiambilItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_alasanSpesimenTidakDiambilItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_alasanSpesimenTidakDiambilItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBukaFolderExport;
    private widget.Button BtnCari;
    private widget.Button BtnCloseIn8;
    private widget.Button BtnHapusKoding;
    private widget.Button BtnKeluar;
    private widget.Button BtnKompilasi;
    private widget.Button BtnPengaturan;
    private widget.Button BtnPenjamin;
    private widget.Button BtnPilihAplikasiPDF;
    private widget.Button BtnResetPengaturan;
    private widget.Button BtnSimpan8;
    private widget.Button BtnSimpanKoding;
    private widget.Button BtnSimpanPengaturan;
    private widget.Button BtnTutupPengaturan;
    private widget.CekBox CekAktifkanHapusOtomatis;
    private widget.CekBox ChkAccor;
    private widget.ComboBox CmbPilihanAplikasiPDF;
    private widget.ComboBox CmbPilihanKategoriBerkas;
    private widget.ComboBox CmbPilihanTanggalExport;
    private widget.ComboBox CmbStatusKirim;
    private widget.ComboBox CmbStatusRawat;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.Label LCountKelahiran;
    private widget.TextBox NoLPManual;
    private widget.TextBox NoSuratKematian;
    private widget.panelisi PanelBerkasDigital;
    private widget.panelisi PanelBridgingKlaim;
    private widget.panelisi PanelContentINACBG;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane ScrollPane3;
    private widget.ComboBox StatusPulang;
    private widget.TextBox TCari;
    private widget.TextBox TMaxMemory;
    private widget.TextBox TNoRMPulang;
    private widget.TextBox TNoRwPulang;
    private widget.TextBox TNoSEPRanapPulang;
    private widget.TextBox TPasienPulang;
    private widget.TextBox TPathAplikasiPDF;
    private widget.Tanggal TanggalKematian;
    private widget.Tanggal TanggalPulang;
    private javax.swing.JDialog WindowInputKelahiran;
    private javax.swing.JDialog WindowPengaturan;
    private javax.swing.JDialog WindowUpdatePulang;
    private widget.TextBox abortus;
    private widget.TextBox adlChronic;
    private widget.TextBox adlSubAcute;
    private widget.ComboBox alasanSpesimenTidakDiambil;
    private widget.ComboBox alteplaseIndikator;
    private widget.ComboBox asalRujukan;
    private widget.TextBox beratBadanLahir;
    private widget.TextBox biayaAlkes;
    private widget.TextBox biayaBMHP;
    private widget.TextBox biayaKamar;
    private widget.TextBox biayaKeperawatan;
    private widget.TextBox biayaLaboratorium;
    private widget.TextBox biayaObat;
    private widget.TextBox biayaObatKemoterapi;
    private widget.TextBox biayaObatKronis;
    private widget.TextBox biayaPelayananDarah;
    private widget.TextBox biayaPenunjang;
    private widget.TextBox biayaProsedurBedah;
    private widget.TextBox biayaProsedurNonBedah;
    private widget.TextBox biayaRadiologi;
    private widget.TextBox biayaRawatIntensif;
    private widget.TextBox biayaRehabilitasi;
    private widget.TextBox biayaSewaAlat;
    private widget.TextBox biayaTambahan;
    private widget.TextBox biayaTarifEksekutif;
    private widget.TextBox biayaTenagaAhli;
    private widget.Button btnAwalMedisIGD;
    private widget.Button btnBatalKelahiran;
    private widget.Button btnEditKelahiran;
    private widget.Button btnHapusKelahiran;
    private widget.Button btnHasilKlaim;
    private widget.Button btnHasilLab;
    private widget.Button btnHasilRad;
    private widget.Button btnInvoice;
    private widget.Button btnKeluarKelahiran;
    private widget.Button btnResumeRanap;
    private widget.Button btnRiwayatPasien;
    private widget.Button btnSEP;
    private widget.Button btnSPRI;
    private widget.Button btnSimpanKelahiran;
    private widget.Button btnSimpanKlaim;
    private widget.Button btnSurkon;
    private widget.Button btnTriaseIGD;
    private widget.Button btnUpdateTglPulang;
    private widget.ComboBox caraLahir;
    private widget.ComboBox cmbDetikKelahiran;
    private widget.ComboBox cmbDetikSpesimen;
    private widget.ComboBox cmbJamKelahiran;
    private widget.ComboBox cmbJamSpesimen;
    private widget.ComboBox cmbMenitKelahiran;
    private widget.ComboBox cmbMenitSpesimen;
    private widget.ComboBox dializerSingleUse;
    private widget.TextBox diastole;
    private widget.TextBox diskonAlkes;
    private widget.TextBox diskonBMHP;
    private widget.TextBox diskonKamar;
    private widget.TextBox diskonKeperawatan;
    private widget.TextBox diskonLaboratorium;
    private widget.TextBox diskonObat;
    private widget.TextBox diskonObatKemoterapi;
    private widget.TextBox diskonObatKronis;
    private widget.TextBox diskonPelayananDarah;
    private widget.TextBox diskonPenunjang;
    private widget.TextBox diskonProsedurBedah;
    private widget.TextBox diskonProsedurNonBedah;
    private widget.TextBox diskonRadiologi;
    private widget.TextBox diskonRawatIntensif;
    private widget.TextBox diskonRehabilitasi;
    private widget.TextBox diskonSewaAlat;
    private widget.TextBox diskonTarifEksekutif;
    private widget.TextBox diskonTenagaAhli;
    private javax.swing.JFileChooser fc;
    private widget.TextBox gravida;
    private widget.ComboBox icuIndikator;
    private widget.TextBox icuLOS;
    private widget.TextBox icuTotalVentilator;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame11;
    private widget.InternalFrame internalFrame12;
    private widget.InternalFrame internalFrame2;
    private widget.Label jLabel10;
    private widget.Label jLabel100;
    private widget.Label jLabel101;
    private widget.Label jLabel102;
    private widget.Label jLabel103;
    private widget.Label jLabel104;
    private widget.Label jLabel105;
    private widget.Label jLabel106;
    private widget.Label jLabel107;
    private widget.Label jLabel108;
    private widget.Label jLabel109;
    private widget.Label jLabel11;
    private widget.Label jLabel110;
    private widget.Label jLabel111;
    private widget.Label jLabel112;
    private widget.Label jLabel113;
    private widget.Label jLabel114;
    private widget.Label jLabel115;
    private widget.Label jLabel116;
    private widget.Label jLabel117;
    private widget.Label jLabel118;
    private widget.Label jLabel119;
    private widget.Label jLabel12;
    private widget.Label jLabel120;
    private widget.Label jLabel121;
    private widget.Label jLabel122;
    private widget.Label jLabel123;
    private widget.Label jLabel124;
    private widget.Label jLabel125;
    private widget.Label jLabel126;
    private widget.Label jLabel127;
    private widget.Label jLabel128;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel48;
    private widget.Label jLabel49;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel53;
    private widget.Label jLabel54;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel57;
    private widget.Label jLabel58;
    private widget.Label jLabel59;
    private widget.Label jLabel6;
    private widget.Label jLabel60;
    private widget.Label jLabel61;
    private widget.Label jLabel62;
    private widget.Label jLabel63;
    private widget.Label jLabel64;
    private widget.Label jLabel66;
    private widget.Label jLabel67;
    private widget.Label jLabel68;
    private widget.Label jLabel69;
    private widget.Label jLabel7;
    private widget.Label jLabel70;
    private widget.Label jLabel71;
    private widget.Label jLabel72;
    private widget.Label jLabel73;
    private widget.Label jLabel74;
    private widget.Label jLabel75;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel79;
    private widget.Label jLabel8;
    private widget.Label jLabel80;
    private widget.Label jLabel81;
    private widget.Label jLabel82;
    private widget.Label jLabel83;
    private widget.Label jLabel84;
    private widget.Label jLabel85;
    private widget.Label jLabel86;
    private widget.Label jLabel87;
    private widget.Label jLabel88;
    private widget.Label jLabel89;
    private widget.Label jLabel9;
    private widget.Label jLabel90;
    private widget.Label jLabel91;
    private widget.Label jLabel92;
    private widget.Label jLabel93;
    private widget.Label jLabel94;
    private widget.Label jLabel95;
    private widget.Label jLabel96;
    private widget.Label jLabel97;
    private widget.Label jLabel98;
    private widget.Label jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private widget.TextBox kantongDarah;
    private widget.ComboBox kelahiran;
    private widget.ComboBox kelasRawat;
    private widget.TextBox kodePJ;
    private widget.ComboBox kondisiLahir;
    private widget.Label label19;
    private widget.Label lblAsalPoli;
    private widget.Label lblCoderNIK;
    private widget.Label lblDokterDPJP;
    private widget.Label lblJenisBayar;
    private widget.Label lblKamarInap;
    private widget.Label lblNamaPasien;
    private widget.Label lblNoKTP;
    private widget.Label lblNoKartu;
    private widget.Label lblNoRM;
    private widget.Label lblNoRawat;
    private widget.Label lblStatusRawat;
    private widget.Label lblTglLahir;
    private widget.Label lblTglRegistrasi;
    private widget.Label lblTglSEP;
    private widget.Label lblTglSEP1;
    private widget.ComboBox letakJanin;
    private widget.editorpane loadBillingHTML;
    private widget.ComboBox lokasiSpesimen;
    private widget.TextBox losNaikKelas;
    private widget.ComboBox naikKelas;
    private widget.TextBox namaPJ;
    private widget.TextBox noRegisterSITB;
    private widget.ComboBox onsetKontraksi;
    private widget.panelisi panelAtasKelahiran;
    private widget.panelisi panelBawahKelahiran;
    private widget.PanelBiasa panelBiasa1;
    private widget.PanelBiasa panelBiasa2;
    private widget.PanelBiasa panelBiasa3;
    private widget.PanelBiasa panelBiasa4;
    private widget.PanelBiasa panelBiasa5;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.PanelBiasa panelGroupingIDRG;
    private widget.PanelBiasa panelGroupingINACBG;
    private widget.PanelBiasa panelHeader;
    private laporan.PanelIdrgSmc panelIdrg;
    private laporan.PanelInacbgSmc panelInacbg;
    private widget.panelisi panelInvoices;
    private widget.PanelBiasa panelKelengkapanData;
    private widget.PanelBiasa panelKelengkapanDataRanap;
    private widget.PanelBiasa panelRincianBiaya;
    private widget.PanelBiasa panelStatusKlaim;
    private widget.panelisi panelisi1;
    private widget.TextBox partus;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppPilihSemua;
    private javax.swing.JMenuItem ppUpdateTanggalPulangSEP;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.TextBox sistole;
    private widget.ComboBox spesimenSHKDiambil;
    private widget.ComboBox statusPulang;
    private widget.TabPane tabKanan;
    private widget.Table tbKelahiran;
    private widget.Table tbKompilasi;
    private widget.TextBox tglPulangSEP;
    private widget.TextBox totalBilling;
    private widget.TextBox totalRincianBiaya;
    private widget.TextBox urutanKelahiran;
    private widget.ComboBox useForcep;
    private widget.ComboBox useManual;
    private widget.ComboBox useVacuum;
    private widget.TextBox usiaKehamilan;
    private widget.Tanggal waktuKelahiran;
    private widget.Tanggal waktuPengambilanSHK;
    // End of variables declaration//GEN-END:variables

    private void onListSelectionModelValueChanged(ListSelectionEvent evt) {
        if (evt != null && evt.getValueIsAdjusting()) {
            return;
        }

        System.out.println("onListSelectionModelValueChanged fired");
        try {
            selectedRow = tbKompilasi.getSelectionModel().getLeadSelectionIndex();
            tabKanan.setSelectedIndex(0);
//            tabPaneKoding.setSelectedIndex(0);
            panelIdrg.getTabbedPane().setSelectedIndex(0);
            getData();
            setFlagKlaim();
            tampilINACBG();
            tampilBerkasDigitalKeperawatan();
        } catch (java.lang.NullPointerException e) {
        }
    }

    private void tampil() {
        if (!isLoading) {
            isLoading = true;
            Valid.tabelKosong(tabMode);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<Void, Object[]>() {
                @Override
                protected Void doInBackground() throws Exception {
                    String statusklaim = "";
                    switch (CmbStatusKirim.getSelectedIndex()) {
                        case 1:
                            statusklaim = "and inc.no_sep is not null ";
                            break;
                        case 2:
                            statusklaim = "and idg.no_sep is not null and idf.no_sep is not null and ing.no_sep is not null and inf.no_sep is not null and inc.no_sep is null ";
                            break;
                        case 3:
                            statusklaim = "and idg.no_sep is not null and idf.no_sep is not null and ing.no_sep is not null and (left(ing.code_cbg, 1) != 'X') and inf.no_sep is null and inc.no_sep is null ";
                            break;
                        case 4:
                            statusklaim = "and idg.no_sep is not null and idf.no_sep is not null and (ing.no_sep is null or (ing.no_sep is not null and (left(ing.code_cbg, 1) = 'X'))) and inf.no_sep is null and inc.no_sep is null ";
                            break;
                        case 5:
                            statusklaim = "and idg.no_sep is not null and idg.mdc_number != '36' and idf.no_sep is null and inf.no_sep is null and inc.no_sep is null ";
                            break;
                        case 6:
                            statusklaim = "and (idg.no_sep is null or (idg.no_sep is not null and idg.mdc_number = '36')) and idf.no_sep is null and inf.no_sep is null and inc.no_sep is null ";
                            break;
                        default:
                            statusklaim = "";
                            break;
                    }

                    String statusrawat = "";
                    if (CmbStatusRawat.getSelectedIndex() == 1) {
                        statusrawat = "and s.jnspelayanan = '2' ";
                    } else if (CmbStatusRawat.getSelectedIndex() == 2) {
                        statusrawat = "and s.jnspelayanan = '1' ";
                    }

                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select s.no_rawat, s.no_sep, r.no_rkm_medis, px.nm_pasien, r.status_lanjut, s.tglsep, date(s.tglpulang) as tglpulang, ki.stts_pulang, case when " +
                        "r.status_lanjut = 'Ranap' then concat(ki.kd_kamar, ' ', b.nm_bangsal) when r.status_lanjut = 'Ralan' then p.nm_poli end as ruangan, d.nm_dokter, " +
                        "case when inc.no_sep is not null then 1 when idg.no_sep is not null and idf.no_sep is not null and ing.no_sep is not null and inf.no_sep is not null " +
                        "and inc.no_sep is null then 2 when idg.no_sep is not null and idf.no_sep is not null and ing.no_sep is not null and (left(ing.code_cbg, 1) != 'X') and " +
                        "inf.no_sep is null and inc.no_sep is null then 3 when idg.no_sep is not null and idf.no_sep is not null and (ing.no_sep is null or (ing.no_sep is not null " +
                        "and (left(ing.code_cbg, 1) = 'X'))) and inf.no_sep is null and inc.no_sep is null then 4 when idg.no_sep is not null and idg.mdc_number != '36' and " +
                        "idf.no_sep is null and inf.no_sep is null and inc.no_sep is null then 5 when (idg.no_sep is null or (idg.no_sep is not null and idg.mdc_number = '36')) " +
                        "and idf.no_sep is null and inf.no_sep is null and inc.no_sep is null then 6 end as statusklaim from bridging_sep s use index (bridging_sep_ibfk_5) join " +
                        "reg_periksa r on s.no_rawat = r.no_rawat join pasien px on r.no_rkm_medis = px.no_rkm_medis join poliklinik p on r.kd_poli = p.kd_poli left join " +
                        "kamar_inap ki on r.no_rawat = ki.no_rawat and ki.stts_pulang != 'Pindah Kamar' left join kamar k on ki.kd_kamar = k.kd_kamar left join bangsal b " +
                        "on k.kd_bangsal = b.kd_bangsal left join maping_dokter_dpjpvclaim md on s.kddpjp = md.kd_dokter_bpjs left join dokter d on md.kd_dokter = d.kd_dokter " +
                        "left join idrg_grouping_smc idg on s.no_sep = idg.no_sep left join idrg_klaim_final_smc idf on s.no_sep = idf.no_sep left join inacbg_grouping_stage12 " +
                        "ing on s.no_sep = ing.no_sep left join inacbg_klaim_final_smc inf on s.no_sep = inf.no_sep left join inacbg_cetak_klaim inc on s.no_sep = inc.no_sep " +
                        "where s.no_sep like ? and s.tglsep between ? and ? and length(s.no_sep) = 19 " + statusrawat + "and (if(s.jnspelayanan = '1', 'Ranap', 'Ralan')) = r.status_lanjut " +
                        "and r.status_bayar = 'Sudah Bayar' " + (kodePJ.getText().isBlank() ? "" : "and r.kd_pj = ? ") + statusklaim + (TCari.getText().isBlank() ? ""
                        : "and (s.no_sep like ? or s.no_rawat like ? or r.no_rkm_medis like ? or px.nm_pasien like ? or p.nm_poli like ? or concat(ki.kd_kamar, ' ', " +
                        "b.nm_bangsal) like ? or d.nm_dokter like ?) ") + "group by s.no_sep, s.no_rawat, r.no_rkm_medis order by s.no_sep, s.jnspelayanan desc"
                    )) {
                        int p = 0;
                        ps.setString(++p, KODEPPKBPJS);
                        ps.setString(++p, Valid.getTglSmc(DTPCari1));
                        ps.setString(++p, Valid.getTglSmc(DTPCari2));
                        if (!kodePJ.getText().isBlank()) {
                            ps.setString(++p, kodePJ.getText());
                        } else {
                            ps.setString(++p, "%");
                        }
                        if (!TCari.getText().isBlank()) {
                            ps.setString(++p, "%" + TCari.getText() + "%");
                            ps.setString(++p, "%" + TCari.getText() + "%");
                            ps.setString(++p, "%" + TCari.getText() + "%");
                            ps.setString(++p, "%" + TCari.getText() + "%");
                            ps.setString(++p, "%" + TCari.getText() + "%");
                            ps.setString(++p, "%" + TCari.getText() + "%");
                            ps.setString(++p, "%" + TCari.getText() + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            String keterangan = "Belum";
                            while (rs.next()) {
                                switch (rs.getInt("statusklaim")) {
                                    case 1:
                                        keterangan = "Selesai";
                                        break;
                                    case 2:
                                        keterangan = "INACBG Final";
                                        break;
                                    case 3:
                                        keterangan = "INACBG Grouping";
                                        break;
                                    case 4:
                                        keterangan = "IDRG Final";
                                        break;
                                    case 5:
                                        keterangan = "IDRG Grouping";
                                        break;
                                    default:
                                        keterangan = "Belum";
                                        break;
                                }
                                publish(new Object[] {
                                    false, rs.getString("no_rawat"), rs.getString("no_sep"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                                    rs.getString("status_lanjut"), rs.getString("tglsep"), rs.getString("tglpulang"), rs.getString("stts_pulang"),
                                    rs.getString("ruangan"), rs.getString("nm_dokter"), keterangan, rs.getInt("statusklaim")
                                });
                            }
                        }
                    }

                    return null;
                }

                @Override
                protected void process(List<Object[]> chunks) {
                    chunks.forEach(tabMode::addRow);
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    isLoading = false;
                    LCount.setText(String.valueOf(tabMode.getRowCount()));
                    BPJSKompilasiBerkasKlaim.this.setCursor(Cursor.getDefaultCursor());
                }
            }.execute();
        }
    }

    private void emptTeks() {
        lblNamaPasien.setText("");
        lblNoRawat.setText("");
        lblNoRM.setText("");
        lblStatusRawat.setText("");
        lblTglSEP.setText("");
        btnSEP.setText("Tidak Ada");
        btnSEP.setEnabled(false);
        btnResumeRanap.setText("Tidak Ada");
        btnResumeRanap.setEnabled(false);
        btnInvoice.setEnabled(false);
        btnAwalMedisIGD.setText("Tidak Ada");
        btnAwalMedisIGD.setEnabled(false);
        btnHasilLab.setText("Tidak Ada");
        btnHasilLab.setEnabled(false);
        btnHasilRad.setText("Tidak Ada");
        btnHasilRad.setEnabled(false);
        btnSurkon.setText("Tidak Ada");
        btnSurkon.setEnabled(false);
        btnSPRI.setText("Tidak Ada");
        btnSPRI.setEnabled(false);
        btnHasilKlaim.setText("Tidak Ada");
        btnHasilKlaim.setEnabled(false);
//        tabPaneKoding.setEnabledAt(1, false);
        tbKompilasi.clearSelection();
        selectedRow = -1;
    }

    private void flipStatus(JButton button, boolean status) {
        if (status) {
            button.setText("Ada");
            button.setEnabled(true);
        } else {
            button.setText("Tidak Ada");
            button.setEnabled(false);
        }
    }

    private void getData() {
        if (selectedRow >= 0) {
            lblNoRawat.setText(tbKompilasi.getValueAt(selectedRow, 1).toString());
            lblNoRM.setText(tbKompilasi.getValueAt(selectedRow, 3).toString());
            lblNamaPasien.setText(tbKompilasi.getValueAt(selectedRow, 4).toString());
            lblStatusRawat.setText(tbKompilasi.getValueAt(selectedRow, 5).toString());
            lblTglSEP.setText(tbKompilasi.getValueAt(selectedRow, 6).toString());
            btnSEP.setText(tbKompilasi.getValueAt(selectedRow, 2).toString());
            unit = tbKompilasi.getValueAt(selectedRow, 9).toString();
            String noSuratKontrol = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", btnSEP.getText());
            if (noSuratKontrol.isBlank()) {
                noSuratKontrol = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_rawat = ? and noskdp != ''", lblNoRawat.getText());
            }
            btnSEP.setEnabled(true);
            btnInvoice.setEnabled(true);
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select exists(select * from data_triase_igd t where t.no_rawat = s.no_rawat) as ada_triase, " +
                "exists(select * from resume_pasien_ranap r where r.no_rawat = s.no_rawat) as ada_resume_ranap, " +
                "exists(select * from penilaian_medis_igd p where p.no_rawat = s.no_rawat) as ada_awal_medis_igd, " +
                "exists(select * from periksa_lab pl where pl.no_rawat = s.no_rawat) as ada_periksa_lab, " +
                "exists(select * from periksa_radiologi pr where pr.no_rawat = s.no_rawat) as ada_periksa_rad, " +
                "exists(select * from bridging_surat_kontrol_bpjs skdp where skdp.no_surat = ?) as ada_skdp, " +
                "exists(select * from bridging_surat_pri_bpjs spri where spri.no_rawat = s.no_rawat) as ada_spri, " +
                "exists(select * from billing b where b.no_rawat = s.no_rawat) as ada_billing from bridging_sep s " +
                "where s.no_sep = ?"
            )) {
                ps.setString(1, noSuratKontrol);
                ps.setString(2, btnSEP.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        flipStatus(btnTriaseIGD, rs.getBoolean("ada_triase"));
                        flipStatus(btnAwalMedisIGD, rs.getBoolean("ada_awal_medis_igd"));
                        flipStatus(btnResumeRanap, rs.getBoolean("ada_resume_ranap"));
                        flipStatus(btnHasilLab, rs.getBoolean("ada_periksa_lab"));
                        flipStatus(btnHasilRad, rs.getBoolean("ada_periksa_rad"));
                        flipStatus(btnSPRI, rs.getBoolean("ada_spri"));
                        flipStatus(btnSurkon, rs.getBoolean("ada_skdp"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                flipStatus(btnHasilKlaim, false);
                flipStatus(btnTriaseIGD, false);
                flipStatus(btnAwalMedisIGD, false);
                flipStatus(btnResumeRanap, false);
                flipStatus(btnHasilLab, false);
                flipStatus(btnHasilRad, false);
                flipStatus(btnSPRI, false);
                flipStatus(btnSurkon, false);
            }
            panelInacbg.getTabbedPane().setSelectedIndex(0);
            tampilBilling();
        }
    }

    private void setFlagKlaim() {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select case when inc.no_sep is not null then 1 when idg.no_sep is not null and idf.no_sep is not null and ing.no_sep is not null and inf.no_sep is " +
            "not null and inc.no_sep is null then 2 when idg.no_sep is not null and idf.no_sep is not null and ing.no_sep is not null and (left(ing.code_cbg, 1) != 'X') " +
            "and inf.no_sep is null then 3 when idg.no_sep is not null and idf.no_sep is not null and (ing.no_sep is null or (ing.no_sep is not null and " +
            "(left(ing.code_cbg, 1) = 'X'))) then 4 when idg.no_sep is not null and idg.mdc_number != '36' and idf.no_sep is null then 5 when (idg.no_sep is null " +
            "or (idg.no_sep is not null and idg.mdc_number = '36')) then 6 end as statusklaim, (ing.no_sep is not null and ing.top_up = 'Belum') as inacbg_stage2 " +
            "from bridging_sep s left join inacbg_data_terkirim2 ind on s.no_sep = ind.no_sep left join idrg_grouping_smc idg on s.no_sep = idg.no_sep left join " +
            "idrg_klaim_final_smc idf on s.no_sep = idf.no_sep left join inacbg_grouping_stage12 ing on s.no_sep = ing.no_sep left join inacbg_klaim_final_smc inf " +
            "on s.no_sep = inf.no_sep left join inacbg_cetak_klaim inc on s.no_sep = inc.no_sep where s.no_sep = ?"
        )) {
            ps.setString(1, btnSEP.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    flagklaim = rs.getInt("statusklaim");
                    flagInacbgTopup = rs.getInt("inacbg_stage2");
                }
            }
        } catch (Exception e) {
            flagklaim = -1;
            flagInacbgTopup = -1;
            System.out.println("Notif : " + e);
        }
        if (flagklaim <= 4) {
//            tabPaneKoding.setEnabledAt(1, true);
            if (flagklaim == 1) {
                flipStatus(btnHasilKlaim, true);
            } else {
                flipStatus(btnHasilKlaim, false);
            }
            if (flagklaim == 2) {
                BtnSimpanKoding.setEnabled(false);
                BtnHapusKoding.setEnabled(false);
            } else {
                /*
                if (tabPaneKoding.isEnabledAt(1)) {
                    tabPaneKoding.setSelectedIndex(1);
                } else {
                    tabPaneKoding.setSelectedIndex(0);
                }
                if (tabPaneKoding.getSelectedIndex() == 1) {
                */
                    if (panelInacbg.getTabbedPane().getSelectedIndex() > 0) {
                        BtnSimpanKoding.setEnabled(false);
                        BtnHapusKoding.setEnabled(true);
                    } else {
                        BtnSimpanKoding.setEnabled(true);
                        BtnHapusKoding.setEnabled(false);
                    }
                // }
            }
        } else {
            // tabPaneKoding.setEnabledAt(1, false);
            // tabPaneKoding.setSelectedIndex(0);
            if (panelIdrg.getTabbedPane().getSelectedIndex() > 0) {
                BtnSimpanKoding.setEnabled(false);
                BtnHapusKoding.setEnabled(true);
            } else {
                BtnSimpanKoding.setEnabled(true);
                BtnHapusKoding.setEnabled(false);
            }
        }
        panelIdrg.setSEP(btnSEP.getText(), hapusOtomatisDiagnosaProsedur);
        panelIdrg.tampilICD();
        panelInacbg.setSEP(btnSEP.getText(), hapusOtomatisDiagnosaProsedur);
        panelInacbg.tampilICD();
    }

    public void isCek() {
        lblCoderNIK.setText(Sequel.cariIsiSmc("select no_ik from inacbg_coder_nik where nik = ?", akses.getkode()));
        kodePJ.setText(KODEPJBPJS);
        namaPJ.setText(NAMAPJBPJS);
        btnRiwayatPasien.setEnabled(akses.getresume_pasien());
        tabKanan.setEnabledAt(2, akses.getberkas_digital_perawatan());
    }

    public void isCek(String nik) {
        lblCoderNIK.setText(nik);
        kodePJ.setText(KODEPJBPJS);
        namaPJ.setText(NAMAPJBPJS);
        btnRiwayatPasien.setEnabled(akses.getresume_pasien());
    }

    private void tampilINACBG() {
        String corona = "BukanCorona";
        String aksi = "";
        String grouper = "";

        if (Sequel.cariExistsSmc("select * from perawatan_corona where perawatan_corona.no_rawat = ?", lblNoRawat.getText())) {
            corona = "PasienCorona";
        }

        switch (flagklaim) {
            case 1:
                aksi = "&action=selesai";
                grouper = "";
                break;
            case 2:
                aksi = "&action=grouper";
                grouper = "&grouper=final";
                break;
            case 3:
                aksi = "&action=grouper";
                if (flagInacbgTopup == 1) {
                    grouper = "&grouper=inacbg_stage2";
                } else {
                    grouper = "&grouper=inacbg_final";
                }
                break;
            case 4:
                aksi = "&action=grouper";
                grouper = "&grouper=inacbg_stage1";
                break;
            case 5:
                aksi = "&action=grouper";
                grouper = "&grouper=idrg_final";
                break;
            default:
                aksi = "&action=grouper";
                grouper = "&grouper=idrg";
                break;
        }

        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/inacbg/login.php?act=login&usere=" +
            koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&page=DetailKirimSmc&nosep=" + btnSEP.getText() + "&codernik=" +
            lblCoderNIK.getText() + "&corona=" + corona + aksi + grouper;

        Platform.runLater(() -> {
            try {
                engineKlaim.load(url);
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        });
    }

    private void tampilBerkasDigitalKeperawatan() {
        if (tabKanan.getComponentAt(2).isEnabled()) {
            Platform.runLater(() -> {
                try {
                    engineBerkasDigital.load("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" +
                        (akses.gethapus_berkas_digital_perawatan() ? "login2.php" : "login2nonhapus.php") + "?act=login&usere=" + koneksiDB.USERHYBRIDWEB() +
                        "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + lblNoRawat.getText() + "&noexit=1&kodeberkas=" + kategoriUploadBerkas);
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            });
        }
    }

    private void tampilBilling() {
        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select b.no, b.nm_perawatan, b.pemisah, b.biaya, b.jumlah, " +
                "b.tambahan, b.totalbiaya from billing b where b.no_rawat = ?"
            )) {
                ps.setString(1, lblNoRawat.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int row = 0;
                        double total = 0;
                        StringBuilder sb = new StringBuilder();
                        sb.append("<html><body><table cellspacing=\"0\" cellpadding=\"0\">");
                        do {
                            total += rs.getDouble("totalbiaya");
                            if (row++ < 6) {
                                sb.append("<tr><td width=\"20%\">")
                                    .append(rs.getString("no").trim())
                                    .append("</td><td width=\"40%\" colspan=\"5\">")
                                    .append(rs.getString("nm_perawatan").trim())
                                    .append("</td></tr>");
                            } else {
                                if (rs.getString("no").isBlank() && rs.getDouble("biaya") == 0) {
                                    sb.append("<tr><td width=\"20%\">").append(rs.getString("no").trim());
                                    if (rs.getString("nm_perawatan").startsWith("Total")) {
                                        sb.append("</td><td colspan=\"5\" align=\"right\">");
                                    } else {
                                        sb.append("</td><td colspan=\"5\">");
                                    }
                                    sb.append(rs.getString("nm_perawatan").trim()).append("</td></tr>");
                                } else {
                                    sb.append("<tr><td width=\"20%\">").append(rs.getString("no")).append("</td><td width=\"48%\">").append(rs.getString("nm_perawatan"))
                                        .append("</td><td width=\"9%\" align=\"right\">").append(rs.getDouble("biaya") == 0 ? "" : Valid.SetAngka(rs.getDouble("biaya")))
                                        .append("</td><td width=\"2%\" align=\"right\">").append(rs.getDouble("jumlah") == 0 ? "" : Valid.SetAngka(rs.getDouble("jumlah")))
                                        .append("</td><td width=\"9%\" align=\"right\">").append(rs.getDouble("tambahan") == 0 ? "" : Valid.SetAngka(rs.getDouble("tambahan")))
                                        .append("</td><td width=\"10%\" align=\"right\">").append(rs.getDouble("totalbiaya") == 0 ? "" : Valid.SetAngka(rs.getDouble("totalbiaya")))
                                        .append("</td></tr>");
                                }
                            }
                        } while (rs.next());
                        sb.append("<tr><td width=\"20%\" style=\"font-weight: bold\">TOTAL BIAYA</td><td style=\"font-weight: bold\">:</td><td colspan=\"4\" style=\"font-weight: bold; text-align: right\">")
                            .append(Valid.SetAngka(total))
                            .append("</td></tr></table></body></html>");
                        loadBillingHTML.setText(sb.toString());
                    } else {
                        loadBillingHTML.setText("");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void cekPengaturanKompilasi() {
        if (new File("./cache/pengaturankompilasi.iyem").isFile()) {
            try (FileReader fr = new FileReader("./cache/pengaturankompilasi.iyem")) {
                JsonNode root = new ObjectMapper().readTree(fr).path("pengaturan");
                if (root.hasNonNull("aplikasipdf")) {
                    aplikasiPDF = root.path("aplikasipdf").asText();
                }

                if (root.hasNonNull("tanggalexport")) {
                    gunakanTanggalExport = root.path("tanggalexport").asText();
                }

                if (root.hasNonNull("maxmemory")) {
                    maxMemory = root.path("maxmemory").asLong();
                }

                if (root.hasNonNull("hapusotomatis")) {
                    hapusOtomatisDiagnosaProsedur = root.path("hapusotomatis").asBoolean();
                }

                if (root.hasNonNull("kategoriuploadberkas")) {
                    kategoriUploadBerkas = root.path("kategoriuploadberkas").asText("");
                }
            } catch (Exception e) {
                System.out.println("Notif : Tidak bisa membaca pengaturan kompilasi! Menggunakan pengaturan default...");
                System.out.println("Notif : " + e);
                aplikasiPDF = koneksiDB.KOMPILASIBERKASAPLIKASIPDF();
                gunakanTanggalExport = koneksiDB.KOMPILASIBERKASGUNAKANTANGGALEXPORT();
                maxMemory = koneksiDB.KOMPILASIBERKASMAXMEMORY();
                hapusOtomatisDiagnosaProsedur = false;
                kategoriUploadBerkas = "";
            }
        } else {
            aplikasiPDF = koneksiDB.KOMPILASIBERKASAPLIKASIPDF();
            gunakanTanggalExport = koneksiDB.KOMPILASIBERKASGUNAKANTANGGALEXPORT();
            maxMemory = koneksiDB.KOMPILASIBERKASMAXMEMORY();
            hapusOtomatisDiagnosaProsedur = false;
            kategoriUploadBerkas = "";
        }

        switch (aplikasiPDF) {
            case "":
                TPathAplikasiPDF.setText("");
                TPathAplikasiPDF.setEditable(false);
                BtnPilihAplikasiPDF.setEnabled(false);
                CmbPilihanAplikasiPDF.setSelectedIndex(0);
                break;
            case "chrome":
                TPathAplikasiPDF.setText("");
                TPathAplikasiPDF.setEditable(false);
                BtnPilihAplikasiPDF.setEnabled(false);
                CmbPilihanAplikasiPDF.setSelectedIndex(1);
                break;
            case "firefox":
                TPathAplikasiPDF.setText("");
                TPathAplikasiPDF.setEditable(false);
                BtnPilihAplikasiPDF.setEnabled(false);
                CmbPilihanAplikasiPDF.setSelectedIndex(2);
                break;
            case "msedge":
                TPathAplikasiPDF.setText("");
                TPathAplikasiPDF.setEditable(false);
                BtnPilihAplikasiPDF.setEnabled(false);
                CmbPilihanAplikasiPDF.setSelectedIndex(3);
                break;
            case "disable":
                TPathAplikasiPDF.setText("");
                TPathAplikasiPDF.setEditable(false);
                BtnPilihAplikasiPDF.setEnabled(false);
                CmbPilihanAplikasiPDF.setSelectedIndex(5);
                break;
            default:
                TPathAplikasiPDF.setText(aplikasiPDF);
                TPathAplikasiPDF.setEditable(true);
                BtnPilihAplikasiPDF.setEnabled(true);
                CmbPilihanAplikasiPDF.setSelectedIndex(4);
                break;
        }

        if (gunakanTanggalExport.equalsIgnoreCase("sep")) {
            CmbPilihanTanggalExport.setSelectedIndex(1);
        } else {
            CmbPilihanTanggalExport.setSelectedIndex(0);
        }

        TMaxMemory.setText(String.valueOf(maxMemory));

        CekAktifkanHapusOtomatis.setSelected(hapusOtomatisDiagnosaProsedur);

        // CmbPilihanKategoriBerkas.setSelectedItem(kategoriUploadBerkas);
    }

    private void hapusTemporaryPDF(final String containsName) throws Exception {
        File folder = new File("./berkaspdf/" + tanggalExport);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(containsName)) {
                    if (file.delete()) {
                        break;
                    }
                }
            }
        }
    }

    private void simpanPDF(final String noSEP, final String reportName, final String savedFileName, final Map params) throws Exception {
        File dir = new File("./berkaspdf/" + tanggalExport);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            Files.createDirectory(dir.toPath());
        }
        JasperPrint jp = JasperFillManager.fillReport("./report/" + reportName, params, koneksi);
        JasperExportManager.exportReportToPdfFile(jp, "./berkaspdf/" + tanggalExport + "/" + noSEP + "_" + savedFileName.replaceAll(".pdf", "") + ".pdf");
    }

    private void simpanPDF(final String noSEP, final String reportName, final String savedFileName, final Map params, final String sql, final String... values) throws Exception {
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }

            File dir = new File("./berkaspdf/" + tanggalExport);

            if (!dir.isDirectory() && !dir.mkdirs()) {
                Files.createDirectory(dir.toPath());
            }

            JasperExportManager.exportReportToPdfFile(
                JasperFillManager.fillReport("./report/" + reportName, params, new JRResultSetDataSource(ps.executeQuery())),
                "./berkaspdf/" + tanggalExport + "/" + noSEP + "_" + savedFileName.replaceAll(".pdf", "") + ".pdf"
            );
        }
    }

    private void exportSEP(final String urutan, final boolean ada, final int row) throws KompilasiException {
        if (!ada) {
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("norawat", tbKompilasi.getValueAt(row, 1).toString());
        param.put("prb", Sequel.cariIsiSmc("select bpjs_prb.prb from bpjs_prb where bpjs_prb.no_sep = ?", tbKompilasi.getValueAt(row, 2).toString()));
        param.put("noreg", Sequel.cariIsiSmc("select reg_periksa.no_reg from reg_periksa where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString()));
        param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        param.put("parameter", tbKompilasi.getValueAt(row, 2).toString());
        param.put("cetakanke", 2);
        try {
            if (tbKompilasi.getValueAt(row, 5).toString().equals("Ranap")) {
                simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptBridgingSEP.jasper", urutan + "_SEP", param);
            } else {
                simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptBridgingSEP2.jasper", urutan + "_SEP", param);
            }
        } catch (Exception e) {
            throw new KompilasiException("SEP", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportHasilKlaim(final String urutan, final boolean ada, final int row) throws KompilasiException {
        if (!ada) {
            return;
        }

        String filename = Sequel.cariIsiSmc("select inacbg_cetak_klaim.path from inacbg_cetak_klaim where inacbg_cetak_klaim.no_sep = ?", tbKompilasi.getValueAt(row, 2).toString());

        if (filename.isBlank()) {
            return;
        }

        try {
            File dir = new File("./berkaspdf/" + tanggalExport);
            if (!dir.isDirectory() && !dir.mkdirs()) {
                Files.createDirectory(dir.toPath());
            }

            HttpURLConnection http;
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/inacbg/" + filename;
            String exportPath = "./berkaspdf/" + tanggalExport + "/" + tbKompilasi.getValueAt(row, 2).toString() + "_" + urutan + "_KlaimINACBG.pdf";
            if (filename.endsWith(".pdf")) {
                try (FileOutputStream os = new FileOutputStream(exportPath); FileChannel fileChannel = os.getChannel()) {
                    URL fileUrl = new URL(url);
                    http = (HttpURLConnection) fileUrl.openConnection();
                    if (http.getResponseCode() == 200) {
                        fileChannel.transferFrom(Channels.newChannel(fileUrl.openStream()), 0, Long.MAX_VALUE);
                        http.disconnect();
                    } else if (http.getResponseCode() / 100 == 4) {
                        throw new Exception("Terjadi kesalahan pada saat mengakses file klaim INACBG..!! Silahkan hubungi administrator.\nFilename : " + filename);
                    } else {
                        throw new Exception("Sambungan ke server terputus..!!");
                    }
                }
            }
        } catch (Exception e) {
            throw new KompilasiException("Hasil Klaim", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportResumeRanap(final String urutan, final boolean ada, final int row) throws KompilasiException {
        if (!ada) {
            return;
        }

        try {
            final Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("norawat", tbKompilasi.getValueAt(row, 1).toString());
            String waktuKeluar = "", tglKeluar = "", jamKeluar = "";

            waktuKeluar = Sequel.cariIsiSmc("select concat(tgl_keluar, ' ', jam_keluar) from kamar_inap where no_rawat = ? and stts_pulang != 'Pindah Kamar' order by concat(tgl_keluar, ' ', jam_keluar) limit 1", tbKompilasi.getValueAt(row, 1).toString());
            if (!waktuKeluar.isBlank()) {
                tglKeluar = waktuKeluar.substring(0, 10);
                jamKeluar = waktuKeluar.substring(11, 19);
            }

            String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from resume_pasien_ranap where no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString());
            String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik = ?", kodeDokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + (finger.isBlank() ? kodeDokter : finger) + "\n" + Valid.SetTgl3(tglKeluar));
            param.put("ruang", Sequel.cariIsiSmc("select concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar join bangsal on " +
                "kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? and kamar_inap.tgl_keluar = ? and kamar_inap.jam_keluar = ?", tbKompilasi.getValueAt(row, 1).toString(), tglKeluar, jamKeluar));
            param.put("tanggalkeluar", Valid.SetTgl3(tglKeluar));
            param.put("jamkeluar", jamKeluar);

            try (PreparedStatement ps = koneksi.prepareStatement("select dpjp_ranap.kd_dokter, dokter.nm_dokter from dpjp_ranap join dokter on dpjp_ranap.kd_dokter = dokter.kd_dokter where dpjp_ranap.no_rawat = ? and dpjp_ranap.kd_dokter != ?")) {
                ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                ps.setString(2, kodeDokter);
                try (ResultSet rs = ps.executeQuery()) {
                    for (int i = 2; rs.next(); i++) {
                        if (i == 2) {
                            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + Valid.SetTgl3(tglKeluar));
                            param.put("namadokter2", rs.getString("nm_dokter"));
                        }
                        if (i == 3) {
                            finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                            param.put("finger3", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + Valid.SetTgl3(tglKeluar));
                            param.put("namadokter3", rs.getString("nm_dokter"));
                        }
                    }
                }
            }
            simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptLaporanResumeRanapKompilasi.jasper", urutan + "_ResumePasien", param);
        } catch (Exception e) {
            throw new KompilasiException("Resume Ranap Pasien", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportBilling(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        final String norawat = URLEncoder.encode(tbKompilasi.getValueAt(row, 1).toString(), "UTF-8");

        final String link = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/loginlihatbilling.php?act=login&norawat=" + norawat + "&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB();

        try (FileOutputStream os = new FileOutputStream("./berkaspdf/" + tanggalExport + "/" + tbKompilasi.getValueAt(row, 2).toString() + "_" + urutan + "_Billing.pdf")) {
            URL url = new URL(link);
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(url, 30000);
            jsoupDoc.head().appendElement("style").appendText("body { font-family: Arial, sans-serif }");
            org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withW3cDocument(w3cDoc, link);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            throw new KompilasiException("Billing", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportTriaseIGD(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        String detailTriase = "";
        int i = 0;

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Sequel.deleteTemporary();
        try (PreparedStatement ps = koneksi.prepareStatement("select t.no_rawat, " +
            "exists(select * from data_triase_igddetail_skala1 s1 where s1.no_rawat = t.no_rawat) as cs1, " +
            "exists(select * from data_triase_igddetail_skala2 s2 where s2.no_rawat = t.no_rawat) as cs2, " +
            "exists(select * from data_triase_igddetail_skala3 s3 where s3.no_rawat = t.no_rawat) as cs3, " +
            "exists(select * from data_triase_igddetail_skala4 s4 where s4.no_rawat = t.no_rawat) as cs4, " +
            "exists(select * from data_triase_igddetail_skala5 s5 where s5.no_rawat = t.no_rawat) as cs5 " +
            "from data_triase_igd t where t.no_rawat = ?"
        )) {
            ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("cs1")) {
                        try (PreparedStatement ps1 = koneksi.prepareStatement(
                            "select data_triase_igdprimer.keluhan_utama, data_triase_igdprimer.kebutuhan_khusus, data_triase_igdprimer.catatan, data_triase_igdprimer.plan, data_triase_igdprimer.tanggaltriase, " +
                            "data_triase_igdprimer.nik, data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, " +
                            "data_triase_igd.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                            "from data_triase_igdprimer join data_triase_igd on data_triase_igdprimer.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                            "join reg_periksa on data_triase_igdprimer.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdprimer.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                        )) {
                            ps1.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                            try (ResultSet rs1 = ps1.executeQuery()) {
                                if (rs1.next()) {
                                    param.put("norawat", rs1.getString("no_rawat"));
                                    param.put("norm", rs1.getString("no_rkm_medis"));
                                    param.put("namapasien", rs1.getString("nm_pasien"));
                                    param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                    param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                    param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                    param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                    param.put("caradatang", rs1.getString("cara_masuk"));
                                    param.put("macamkasus", rs1.getString("macam_kasus"));
                                    param.put("keluhanutama", rs1.getString("keluhan_utama"));
                                    param.put("kebutuhankhusus", rs1.getString("kebutuhan_khusus"));
                                    param.put("plan", rs1.getString("plan"));
                                    param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                    param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                    param.put("pegawai", rs1.getString("nama"));
                                    param.put("catatan", rs1.getString("catatan"));
                                    param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                    finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                    param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                                        "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                        "join master_triase_skala1 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala1.kode_pemeriksaan " +
                                        "join data_triase_igddetail_skala1 on master_triase_skala1.kode_skala1 = data_triase_igddetail_skala1.kode_skala1 " +
                                        "where data_triase_igddetail_skala1.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                        "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                    )) {
                                        ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                            while (rs2.next()) {
                                                detailTriase = "";
                                                try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                    "select master_triase_skala1.pengkajian_skala1 from master_triase_skala1 " +
                                                    "join data_triase_igddetail_skala1 on master_triase_skala1.kode_skala1 = data_triase_igddetail_skala1.kode_skala1 " +
                                                    "where master_triase_skala1.kode_pemeriksaan = ? and data_triase_igddetail_skala1.no_rawat = ? " +
                                                    "order by data_triase_igddetail_skala1.kode_skala1"
                                                )) {
                                                    ps3.setString(1, rs2.getString(1));
                                                    ps3.setString(2, tbKompilasi.getValueAt(row, 1).toString());
                                                    try (ResultSet rs3 = ps3.executeQuery()) {
                                                        while (rs3.next()) {
                                                            detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                        }
                                                    }
                                                }
                                                detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                            }
                                        }
                                    }
                                    simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptLembarTriaseSkala1.jasper", urutan + "_TriaseSkala1", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    } else if (rs.getBoolean("cs2")) {
                        try (PreparedStatement ps1 = koneksi.prepareStatement(
                            "select data_triase_igdprimer.keluhan_utama, data_triase_igdprimer.kebutuhan_khusus, data_triase_igdprimer.catatan, data_triase_igdprimer.plan, data_triase_igdprimer.tanggaltriase, " +
                            "data_triase_igdprimer.nik, data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, " +
                            "data_triase_igd.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                            "from data_triase_igdprimer join data_triase_igd on data_triase_igdprimer.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                            "join reg_periksa on data_triase_igdprimer.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdprimer.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                        )) {
                            ps1.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                            try (ResultSet rs1 = ps1.executeQuery()) {
                                if (rs1.next()) {
                                    param.put("norawat", rs1.getString("no_rawat"));
                                    param.put("norm", rs1.getString("no_rkm_medis"));
                                    param.put("namapasien", rs1.getString("nm_pasien"));
                                    param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                    param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                    param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                    param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                    param.put("caradatang", rs1.getString("cara_masuk"));
                                    param.put("macamkasus", rs1.getString("macam_kasus"));
                                    param.put("keluhanutama", rs1.getString("keluhan_utama"));
                                    param.put("kebutuhankhusus", rs1.getString("kebutuhan_khusus"));
                                    param.put("plan", rs1.getString("plan"));
                                    param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                    param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                    param.put("pegawai", rs1.getString("nama"));
                                    param.put("catatan", rs1.getString("catatan"));
                                    param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                    finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                    param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                                        "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                        "join master_triase_skala2 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala2.kode_pemeriksaan " +
                                        "join data_triase_igddetail_skala2 on master_triase_skala2.kode_skala2 = data_triase_igddetail_skala2.kode_skala2 " +
                                        "where data_triase_igddetail_skala2.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                        "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                    )) {
                                        ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                            while (rs2.next()) {
                                                detailTriase = "";
                                                try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                    "select master_triase_skala2.pengkajian_skala2 from master_triase_skala2 " +
                                                    "join data_triase_igddetail_skala2 on master_triase_skala2.kode_skala2 = data_triase_igddetail_skala2.kode_skala2 " +
                                                    "where master_triase_skala2.kode_pemeriksaan = ? and data_triase_igddetail_skala2.no_rawat = ? " +
                                                    "order by data_triase_igddetail_skala2.kode_skala2"
                                                )) {
                                                    ps3.setString(1, rs2.getString(1));
                                                    ps3.setString(2, tbKompilasi.getValueAt(row, 1).toString());
                                                    try (ResultSet rs3 = ps3.executeQuery()) {
                                                        while (rs3.next()) {
                                                            detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                        }
                                                    }
                                                }
                                                detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                            }
                                        }
                                    }
                                    simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptLembarTriaseSkala2.jasper", urutan + "_TriaseSkala2", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    } else if (rs.getBoolean("cs3")) {
                        try (PreparedStatement ps1 = koneksi.prepareStatement(
                            "select data_triase_igdsekunder.anamnesa_singkat, data_triase_igdsekunder.catatan, data_triase_igdsekunder.plan, data_triase_igdsekunder.tanggaltriase, data_triase_igdsekunder.nik, " +
                            "data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, data_triase_igd.no_rawat, " +
                            "pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                            "from data_triase_igdsekunder join data_triase_igd on data_triase_igdsekunder.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                            "join reg_periksa on data_triase_igdsekunder.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdsekunder.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                        )) {
                            ps1.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                            try (ResultSet rs1 = ps1.executeQuery()) {
                                if (rs1.next()) {
                                    param.put("norawat", rs1.getString("no_rawat"));
                                    param.put("norm", rs1.getString("no_rkm_medis"));
                                    param.put("namapasien", rs1.getString("nm_pasien"));
                                    param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                    param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                    param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                    param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                    param.put("caradatang", rs1.getString("cara_masuk"));
                                    param.put("macamkasus", rs1.getString("macam_kasus"));
                                    param.put("keluhanutama", rs1.getString("anamnesa_singkat"));
                                    param.put("plan", rs1.getString("plan"));
                                    param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                    param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                    param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                    param.put("pegawai", rs1.getString("nama"));
                                    param.put("catatan", rs1.getString("catatan"));
                                    finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                    param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                                        "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                        "join master_triase_skala3 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala3.kode_pemeriksaan " +
                                        "join data_triase_igddetail_skala3 on master_triase_skala3.kode_skala3 = data_triase_igddetail_skala3.kode_skala3 " +
                                        "where data_triase_igddetail_skala3.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                        "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                    )) {
                                        ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                            while (rs2.next()) {
                                                detailTriase = "";
                                                try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                    "select master_triase_skala3.pengkajian_skala3 from master_triase_skala3 join data_triase_igddetail_skala3 " +
                                                    "on master_triase_skala3.kode_skala3 = data_triase_igddetail_skala3.kode_skala3 where master_triase_skala3.kode_pemeriksaan = ? " +
                                                    "and data_triase_igddetail_skala3.no_rawat = ? order by data_triase_igddetail_skala3.kode_skala3"
                                                )) {
                                                    ps3.setString(1, rs2.getString("kode_pemeriksaan"));
                                                    ps3.setString(2, tbKompilasi.getValueAt(row, 1).toString());
                                                    try (ResultSet rs3 = ps3.executeQuery()) {
                                                        while (rs3.next()) {
                                                            detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                        }
                                                    }
                                                }
                                                detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                            }
                                        }
                                    }
                                    simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptLembarTriaseSkala3.jasper", urutan + "_TriaseSkala3", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    } else if (rs.getBoolean("cs4")) {
                        try (PreparedStatement ps1 = koneksi.prepareStatement(
                            "select data_triase_igdsekunder.anamnesa_singkat, data_triase_igdsekunder.catatan, data_triase_igdsekunder.plan, data_triase_igdsekunder.tanggaltriase, data_triase_igdsekunder.nik, " +
                            "data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, data_triase_igd.no_rawat, " +
                            "pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                            "from data_triase_igdsekunder join data_triase_igd on data_triase_igdsekunder.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                            "join reg_periksa on data_triase_igdsekunder.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdsekunder.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                        )) {
                            ps1.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                            try (ResultSet rs1 = ps1.executeQuery()) {
                                if (rs1.next()) {
                                    param.put("norawat", rs1.getString("no_rawat"));
                                    param.put("norm", rs1.getString("no_rkm_medis"));
                                    param.put("namapasien", rs1.getString("nm_pasien"));
                                    param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                    param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                    param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                    param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                    param.put("caradatang", rs1.getString("cara_masuk"));
                                    param.put("macamkasus", rs1.getString("macam_kasus"));
                                    param.put("keluhanutama", rs1.getString("anamnesa_singkat"));
                                    param.put("plan", rs1.getString("plan"));
                                    param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                    param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                    param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                    param.put("pegawai", rs1.getString("nama"));
                                    param.put("catatan", rs1.getString("catatan"));
                                    finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                    param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                                        "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                        "join master_triase_skala4 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala4.kode_pemeriksaan " +
                                        "join data_triase_igddetail_skala4 on master_triase_skala4.kode_skala4 = data_triase_igddetail_skala4.kode_skala4 " +
                                        "where data_triase_igddetail_skala4.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                        "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                    )) {
                                        ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                            while (rs2.next()) {
                                                detailTriase = "";
                                                try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                    "select master_triase_skala4.pengkajian_skala4 from master_triase_skala4 join data_triase_igddetail_skala4 " +
                                                    "on master_triase_skala4.kode_skala4 = data_triase_igddetail_skala4.kode_skala4 where master_triase_skala4.kode_pemeriksaan = ? " +
                                                    "and data_triase_igddetail_skala4.no_rawat = ? order by data_triase_igddetail_skala4.kode_skala4"
                                                )) {
                                                    ps3.setString(1, rs2.getString(1));
                                                    ps3.setString(2, tbKompilasi.getValueAt(row, 1).toString());
                                                    try (ResultSet rs3 = ps3.executeQuery()) {
                                                        while (rs3.next()) {
                                                            detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                        }
                                                    }
                                                }
                                                detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                            }
                                        }
                                    }
                                    simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptLembarTriaseSkala4.jasper", urutan + "_TriaseSkala4", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    } else if (rs.getBoolean("cs5")) {
                        try (PreparedStatement ps1 = koneksi.prepareStatement(
                            "select data_triase_igdsekunder.anamnesa_singkat, data_triase_igdsekunder.catatan, data_triase_igdsekunder.plan, data_triase_igdsekunder.tanggaltriase, data_triase_igdsekunder.nik, " +
                            "data_triase_igd.tekanan_darah, data_triase_igd.nadi, data_triase_igd.pernapasan, data_triase_igd.suhu, data_triase_igd.saturasi_o2, data_triase_igd.nyeri, data_triase_igd.no_rawat, " +
                            "pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, pegawai.nama, data_triase_igd.tgl_kunjungan, data_triase_igd.cara_masuk, master_triase_macam_kasus.macam_kasus " +
                            "from data_triase_igdsekunder join data_triase_igd on data_triase_igdsekunder.no_rawat = data_triase_igd.no_rawat join master_triase_macam_kasus on data_triase_igd.kode_kasus = master_triase_macam_kasus.kode_kasus " +
                            "join reg_periksa on data_triase_igdsekunder.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join pegawai on data_triase_igdsekunder.nik = pegawai.nik where data_triase_igd.no_rawat = ?"
                        )) {
                            ps1.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                            try (ResultSet rs1 = ps1.executeQuery()) {
                                if (rs1.next()) {
                                    param.put("norawat", rs1.getString("no_rawat"));
                                    param.put("norm", rs1.getString("no_rkm_medis"));
                                    param.put("namapasien", rs1.getString("nm_pasien"));
                                    param.put("tanggallahir", rs1.getDate("tgl_lahir"));
                                    param.put("jk", rs1.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan"));
                                    param.put("tanggalkunjungan", rs1.getDate("tgl_kunjungan"));
                                    param.put("jamkunjungan", rs1.getString("tgl_kunjungan").substring(11, 19));
                                    param.put("caradatang", rs1.getString("cara_masuk"));
                                    param.put("macamkasus", rs1.getString("macam_kasus"));
                                    param.put("keluhanutama", rs1.getString("anamnesa_singkat"));
                                    param.put("plan", rs1.getString("plan"));
                                    param.put("tanggaltriase", rs1.getDate("tanggaltriase"));
                                    param.put("tandavital", "Suhu (C) : " + rs1.getString("suhu") + ", Nyeri : " + rs1.getString("nyeri") + ", Tensi : " + rs1.getString("tekanan_darah") + ", Nadi(/menit) : " + rs1.getString("nadi") + ", Saturasi O²(%) : " + rs1.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs1.getString("pernapasan"));
                                    param.put("jamtriase", rs1.getString("tanggaltriase").substring(11, 19));
                                    param.put("pegawai", rs1.getString("nama"));
                                    param.put("catatan", rs1.getString("catatan"));
                                    finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs1.getString("nik"));
                                    param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs1.getString("nama") + "\nID " + (finger.isBlank() ? rs1.getString("nik") : finger) + "\n" + Valid.SetTgl3(rs1.getString("tanggaltriase")));
                                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                                        "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan from master_triase_pemeriksaan " +
                                        "join master_triase_skala5 on master_triase_pemeriksaan.kode_pemeriksaan = master_triase_skala5.kode_pemeriksaan " +
                                        "join data_triase_igddetail_skala5 on master_triase_skala5.kode_skala5 = data_triase_igddetail_skala5.kode_skala5 " +
                                        "where data_triase_igddetail_skala5.no_rawat = ? group by master_triase_pemeriksaan.kode_pemeriksaan " +
                                        "order by master_triase_pemeriksaan.kode_pemeriksaan"
                                    )) {
                                        ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                            while (rs2.next()) {
                                                detailTriase = "";
                                                try (PreparedStatement ps3 = koneksi.prepareStatement(
                                                    "select master_triase_skala5.pengkajian_skala5 from master_triase_skala5 join data_triase_igddetail_skala5 " +
                                                    "on master_triase_skala5.kode_skala5 = data_triase_igddetail_skala5.kode_skala5 where master_triase_skala5.kode_pemeriksaan = ? " +
                                                    "and data_triase_igddetail_skala5.no_rawat = ? order by data_triase_igddetail_skala5.kode_skala5"
                                                )) {
                                                    ps3.setString(1, rs2.getString(1));
                                                    ps3.setString(2, tbKompilasi.getValueAt(row, 1).toString());
                                                    try (ResultSet rs3 = ps3.executeQuery()) {
                                                        while (rs3.next()) {
                                                            detailTriase = rs3.getString(1) + ", " + detailTriase;
                                                        }
                                                    }
                                                }
                                                detailTriase = detailTriase.substring(0, detailTriase.length() - 2);
                                                Sequel.temporary(String.valueOf(++i), rs2.getString("nama_pemeriksaan"), detailTriase);
                                            }
                                        }
                                    }
                                    simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptLembarTriaseSkala5.jasper", urutan + "_TriaseSkala5", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new KompilasiException("Triase IGD", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportSOAP(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        try {
            StringBuilder htmlContent = new StringBuilder();
            htmlContent
                .append("<html>")
                .append("<head>")
                .append("<style type=\"text/css\">")
                .append(".isi td{border-right: 1px solid #e2e7dd;border-bottom: 1px solid #e2e7dd;font-family: Tahoma;font-size: 8.5px;height: 12px;background-color: #ffffff;color: #323232} .isi a{text-decoration: none;color: #8b9b95;padding: 0 0 0 0px;font-family: Tahoma;font-size: 8.5px;border-color: white}")
                .append("</style>")
                .append("</head>")
                .append("<body>");

            try (PreparedStatement ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tmp_lahir, pasien.tgl_lahir, pasien.agama, bahasa_pasien.nama_bahasa, cacat_fisik.nama_cacat, pasien.gol_darah, " +
                "pasien.nm_ibu, pasien.stts_nikah, pasien.pnd, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, pasien.pekerjaan from reg_periksa " +
                "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join bahasa_pasien on bahasa_pasien.id = pasien.bahasa_pasien join cacat_fisik on cacat_fisik.id = pasien.cacat_fisik " +
                "join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on pasien.kd_kab = kabupaten.kd_kab where reg_periksa.no_rawat = ?"
            )) {
                ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        htmlContent
                            .append("<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"3px\" cellspacing=\"0\" class=\"tbl_form\">")
                            .append("<tbody>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">No.RM</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("no_rkm_medis")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Nama Pasien</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("nm_pasien")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Alamat</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("alamat")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Jenis Kelamin</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Tempat &amp; Tanggal Lahir</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("tmp_lahir")).append(", ").append(new SimpleDateFormat("dd MMMM yyyy", new Locale("id")).format((Date) rs.getDate("tgl_lahir"))).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Ibu Kandung</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("nm_ibu")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Golongan Darah</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("gol_darah")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Status Nikah</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("stts_nikah")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Agama</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("agama")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Pendidikan Terakhir</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("pnd")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Bahasa Dipakai</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("nama_bahasa")).append("</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Cacat Fisik</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">").append(rs.getString("nama_cacat")).append("</td>")
                            .append("</tr>")
                            .append("</tbody>")
                            .append("</table>");
                    }
                }
            }

            htmlContent
                .append("<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"3px\" cellspacing=\"0\" class=\"tbl_form\">")
                .append("<tbody>")
                .append("<tr class=\"isi\">")
                .append("<td valign=\"middle\" bgcolor=\"#FFFAF8\" align=\"center\" width=\"5%\">Tgl. Reg</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFAF8\" align=\"center\" width=\"8%\">No. Rawat</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFAF8\" align=\"center\" width=\"3%\">Status</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFAF8\" align=\"center\" width=\"84%\">S.O.A.P.I.E</td>")
                .append("</tr>")
                .append("<tr class=\"isi\">")
                .append("<td valign=\"top\" align=\"center\">").append(Sequel.cariIsiSmc("select tgl_registrasi from reg_periksa where no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString())).append("</td>")
                .append("<td valign=\"top\" align=\"center\">").append(tbKompilasi.getValueAt(row, 1).toString()).append("</td>")
                .append("<td valign=\"top\" align=\"center\">Ralan</td>")
                .append("<td valign=\"top\" align=\"center\">")
                .append("<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"2px\" cellspacing=\"0\">")
                .append("<tbody>")
                .append("<tr class=\"isi\">")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"7%\">Tanggal</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"13%\">Dokter/Paramedis</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"14%\">Subjek</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"13%\">Objek</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"13%\">Asesmen</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"14%\">Plan</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"14%\">Instruksi</td>")
                .append("<td valign=\"middle\" bgcolor=\"#FFFFF8\" align=\"center\" width=\"14%\">Evaluasi</td>")
                .append("</tr>");

            try (PreparedStatement ps = koneksi.prepareStatement(
                "select pemeriksaan_ralan.*, pegawai.nama, pegawai.jbtn from pemeriksaan_ralan join pegawai on pemeriksaan_ralan.nip = pegawai.nik " +
                "where pemeriksaan_ralan.no_rawat = ? order by concat(pemeriksaan_ralan.tgl_perawatan, ' ', pemeriksaan_ralan.jam_rawat) desc"
            )) {
                ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        htmlContent
                            .append("<tr class=\"isi\">")
                            .append("<td align=\"center\">").append(rs.getString("tgl_perawatan")).append("<br>").append(rs.getString("jam_rawat")).append("</td>")
                            .append("<td align=\"center\">").append(rs.getString("nip")).append("<br>").append(rs.getString("nama")).append("</td>")
                            .append("<td align=\"left\">").append(HtmlUtils.htmlEscape(rs.getString("keluhan")).replaceAll("\\R", "<br>")).append("</td>")
                            .append("<td align=\"left\">").append(HtmlUtils.htmlEscape(rs.getString("pemeriksaan")).replaceAll("\\R", "<br>"))
                            .append((rs.getString("alergi") == null || rs.getString("alergi").isBlank() ? "" : "<br>Alergi : ")).append(rs.getString("alergi"))
                            .append((rs.getString("suhu_tubuh") == null || rs.getString("suhu_tubuh").isBlank() ? "" : "<br>Suhu(C) : ")).append(rs.getString("suhu_tubuh"))
                            .append((rs.getString("tensi") == null || rs.getString("tensi").isBlank() ? "" : "<br>Tensi : ")).append(rs.getString("tensi"))
                            .append((rs.getString("nadi") == null || rs.getString("nadi").isBlank() ? "" : "<br>Nadi(/menit) : ")).append(rs.getString("nadi"))
                            .append((rs.getString("respirasi") == null || rs.getString("respirasi").isBlank() ? "" : "<br>Respirasi(/menit) : ")).append(rs.getString("respirasi"))
                            .append((rs.getString("tinggi") == null || rs.getString("tinggi").isBlank() ? "" : "<br>Tinggi(Cm) : ")).append(rs.getString("tinggi"))
                            .append((rs.getString("berat") == null || rs.getString("berat").isBlank() ? "" : "<br>Berat(Kg) : ")).append(rs.getString("berat"))
                            .append((rs.getString("lingkar_perut") == null || rs.getString("lingkar_perut").isBlank() ? "" : "<br>Lingkar Perut(Cm) : ")).append(rs.getString("lingkar_perut"))
                            .append((rs.getString("spo2") == null || rs.getString("spo2").isBlank() ? "" : "<br>SpO2(%) : ")).append(rs.getString("spo2"))
                            .append((rs.getString("gcs") == null || rs.getString("gcs").isBlank() ? "" : "<br>GCS(E,V,M) : ")).append(rs.getString("gcs"))
                            .append((rs.getString("kesadaran") == null || rs.getString("kesadaran").isBlank() ? "" : "<br>Kesadaran : ")).append(rs.getString("kesadaran"))
                            .append("</td>")
                            .append("<td align=\"left\">").append(HtmlUtils.htmlEscape(rs.getString("penilaian")).replaceAll("\\R", "<br>")).append("</td>")
                            .append("<td align=\"left\">").append(HtmlUtils.htmlEscape(rs.getString("rtl")).replaceAll("\\R", "<br>")).append("</td>")
                            .append("<td align=\"left\">").append(HtmlUtils.htmlEscape(rs.getString("instruksi")).replaceAll("\\R", "<br>")).append("</td>")
                            .append("<td align=\"left\">").append(HtmlUtils.htmlEscape(rs.getString("evaluasi")).replaceAll("\\R", "<br>")).append("</td>")
                            .append("</tr>");
                    }
                }
            }

            GetMethod get = new GetMethod("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/penggajian/generateqrcode.php?kodedokter=" + Sequel.cariIsiSmc("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString()).replace(" ", "_"));
            HttpClient http = new HttpClient();
            http.executeMethod(get);

            htmlContent
                .append("</tbody>")
                .append("</table>")
                .append("</td>")
                .append("</tr>")
                .append("<tr class=\"isi\">")
                .append("<td valign=\"top\" width=\"2%\"></td>")
                .append("<td valign=\"middle\" width=\"18%\"> Tanda Tangan/Verifikasi </td>")
                .append("<td valign=\"middle\" width=\"1%\" align=\"center\"> : </td>")
                .append("<td valign=\"middle\" width=\"79%\" align=\"center\">")
                .append("Dokter Poli")
                .append("<br><img width=\"90\" height=\"90\" src=\"http://").append(koneksiDB.HOSTHYBRIDWEB()).append(":").append(koneksiDB.PORTWEB()).append("/").append(koneksiDB.HYBRIDWEB()).append("/penggajian/temp/").append(Sequel.cariIsiSmc("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString()).replace(" ", "_")).append(".png\"><br>")
                .append(Sequel.cariIsiSmc("select dokter.nm_dokter from reg_periksa join dokter on reg_periksa.kd_dokter = dokter.kd_dokter where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString()))
                .append("</tr>")
                .append("<tr class=\"isi\"><td></td><td colspan=\"3\" align=\"right\">&#160;</td></tr>")
                .append("</body>")
                .append("</html>");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("soap_ralan.html")))) {
                String html = htmlContent.toString().replaceAll(getClass().getResource("/picture/").toString(), "./gambar/");
                bw.write(html);
            }

            try (FileOutputStream os = new FileOutputStream("./berkaspdf/" + tanggalExport + "/" + tbKompilasi.getValueAt(row, 2).toString() + "_" + urutan + "_SOAP.pdf")) {
                org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(new File("soap_ralan.html"));
                org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withW3cDocument(w3cDoc, null);
                builder.toStream(os);
                builder.run();
            }
        } catch (Exception e) {
            throw new KompilasiException("SOAP Ralan", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportAwalMedisIGD(final String urutan, final boolean ada, final int row) throws Exception {
        if (Sequel.cariExistsSmc("select * from reg_periksa where no_rawat = ? and kd_poli != 'IGDK'", tbKompilasi.getValueAt(row, 1).toString())) {
            return;
        }

        if (!ada) {
            return;
        }

        String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from penilaian_medis_igd where no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString());
        String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
        String tgl = Sequel.cariIsiSmc("select date_format(tanggal, '%d-%m-%Y') from penilaian_medis_igd where no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString());

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));

        try {
            param.put("lokalis", getClass().getResource("/picture/semua.png").openStream());
        } catch (Exception e) {
        }

        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", kodeDokter);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + (finger.isBlank() ? kodeDokter : finger) + "\n" + tgl);

        try {
            simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptCetakPenilaianAwalMedisIGD.jasper", urutan + "_AwalMedisIGD", param,
                "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, if (pasien.jk = 'L', 'Laki-Laki', 'Perempuan') as jk, pasien.tgl_lahir, penilaian_medis_igd.tanggal, penilaian_medis_igd.kd_dokter, " +
                "penilaian_medis_igd.anamnesis, penilaian_medis_igd.hubungan, concat_ws(', ', penilaian_medis_igd.anamnesis, nullif(penilaian_medis_igd.hubungan, '')) as hubungan_anemnesis, penilaian_medis_igd.keluhan_utama, " +
                "penilaian_medis_igd.rps, penilaian_medis_igd.rpk, penilaian_medis_igd.rpd, penilaian_medis_igd.rpo, penilaian_medis_igd.alergi, penilaian_medis_igd.keadaan, penilaian_medis_igd.gcs, penilaian_medis_igd.kesadaran, " +
                "penilaian_medis_igd.td, penilaian_medis_igd.nadi, penilaian_medis_igd.rr, penilaian_medis_igd.suhu, penilaian_medis_igd.spo, penilaian_medis_igd.bb, penilaian_medis_igd.tb, penilaian_medis_igd.kepala, penilaian_medis_igd.mata, " +
                "penilaian_medis_igd.gigi, penilaian_medis_igd.leher, penilaian_medis_igd.thoraks, penilaian_medis_igd.abdomen, penilaian_medis_igd.ekstremitas, penilaian_medis_igd.genital, penilaian_medis_igd.ket_fisik, penilaian_medis_igd.ket_lokalis, " +
                "penilaian_medis_igd.ekg, penilaian_medis_igd.rad, penilaian_medis_igd.lab, penilaian_medis_igd.diagnosis, penilaian_medis_igd.tata, dokter.nm_dokter from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "join penilaian_medis_igd on reg_periksa.no_rawat = penilaian_medis_igd.no_rawat join dokter on penilaian_medis_igd.kd_dokter = dokter.kd_dokter where penilaian_medis_igd.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString()
            );
        } catch (Exception e) {
            throw new KompilasiException("Awal Medis IGD", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportHasilLab(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        String kamar = "", namaKamar = "";
        int i = 0;
        Map<String, Object> param = new HashMap<>();

        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.umur, pasien.tgl_lahir, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) " +
                "as alamat, pasien.pekerjaan, pasien.no_ktp from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join kelurahan on pasien.kd_kel = kelurahan.kd_kel join " +
                "kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on pasien.kd_kab = kabupaten.kd_kab join propinsi on pasien.kd_prop = propinsi.kd_prop where reg_periksa.no_rawat = ?"
            )) {
                ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        param.put("noperiksa", tbKompilasi.getValueAt(row, 1).toString());
                        param.put("norm", rs.getString("no_rkm_medis"));
                        param.put("namapasien", rs.getString("nm_pasien"));
                        param.put("jkel", rs.getString("jk"));
                        param.put("umur", rs.getString("umur"));
                        param.put("lahir", new SimpleDateFormat("dd-MM-yyyy").format((Date) rs.getDate("tgl_lahir")));
                        param.put("alamat", rs.getString("alamat"));
                        param.put("diagnosa", tbKompilasi.getValueAt(row, 10).toString());
                        param.put("pekerjaan", rs.getString("pekerjaan"));
                        param.put("noktp", rs.getString("no_ktp"));
                        param.put("namars", akses.getnamars());
                        param.put("alamatrs", akses.getalamatrs());
                        param.put("kotars", akses.getkabupatenrs());
                        param.put("propinsirs", akses.getpropinsirs());
                        param.put("kontakrs", akses.getkontakrs());
                        param.put("emailrs", akses.getemailrs());
                        param.put("userid", akses.getkode());
                        param.put("ipaddress", akses.getalamatip());
                    }
                }
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                "select periksa_lab.no_rawat, periksa_lab.tgl_periksa, periksa_lab.jam, periksa_lab.status, periksa_lab.kategori, periksa_lab.kd_dokter, " +
                "dokter.nm_dokter, periksa_lab.dokter_perujuk, perujuk.nm_dokter nm_perujuk, periksa_lab.nip, petugas.nama from periksa_lab join dokter " +
                "on periksa_lab.kd_dokter = dokter.kd_dokter join dokter perujuk on periksa_lab.dokter_perujuk = perujuk.kd_dokter join petugas on " +
                "periksa_lab.nip = petugas.nip where periksa_lab.no_rawat = ? group by periksa_lab.no_rawat, periksa_lab.tgl_periksa, periksa_lab.jam, " +
                "periksa_lab.status, periksa_lab.kategori"
            )) {
                ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    for (int j = 1; rs.next(); j++) {
                        Sequel.deleteTemporaryLab();
                        i = 0;

                        if (rs.getString("status").equalsIgnoreCase("ralan")) {
                            kamar = "Poli";
                            namaKamar = Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik join reg_periksa on poliklinik.kd_poli = reg_periksa.kd_poli where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString());
                        } else {
                            kamar = "Kamar";
                            namaKamar = tbKompilasi.getValueAt(row, 9).toString();
                        }

                        param.put("kamar", kamar);
                        param.put("namakamar", namaKamar);
                        param.put("pengirim", rs.getString("nm_perujuk"));
                        param.put("tanggal", rs.getString("tgl_periksa"));
                        param.put("jam", rs.getString("jam"));
                        param.put("penjab", rs.getString("nm_dokter"));
                        param.put("petugas", rs.getString("nama"));
                        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + rs.getString("tgl_periksa"));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("nip"));
                        param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nama") + "\nID " + (finger.isBlank() ? rs.getString("nip") : finger) + "\n" + rs.getString("tgl_periksa"));
                        param.put("ttd", Sequel.cariGambarSmc("select dokter_ttdbasah.gambar_ttd from dokter_ttdbasah where dokter_ttdbasah.kd_dokter = ?", rs.getString("kd_dokter")));

                        if (rs.getString("kategori").equals("PK")) {
                            try (PreparedStatement ps2 = koneksi.prepareStatement(
                                "select periksa_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan from periksa_lab join jns_perawatan_lab " +
                                "on periksa_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where periksa_lab.no_rawat = ? " +
                                "and periksa_lab.tgl_periksa = ? and periksa_lab.jam = ? and periksa_lab.status = ? and periksa_lab.kategori = ?"
                            )) {
                                ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                ps2.setString(2, rs.getString("tgl_periksa"));
                                ps2.setString(3, rs.getString("jam"));
                                ps2.setString(4, rs.getString("status"));
                                ps2.setString(5, rs.getString("kategori"));
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    while (rs2.next()) {
                                        Sequel.temporaryLab(String.valueOf(++i), rs2.getString("nm_perawatan"));
                                        try (PreparedStatement ps3 = koneksi.prepareStatement(
                                            "select template_laboratorium.Pemeriksaan, detail_periksa_lab.nilai, template_laboratorium.satuan, detail_periksa_lab.nilai_rujukan, " +
                                            "detail_periksa_lab.biaya_item, detail_periksa_lab.keterangan, detail_periksa_lab.kd_jenis_prw from detail_periksa_lab join template_laboratorium " +
                                            "on detail_periksa_lab.id_template = template_laboratorium.id_template where detail_periksa_lab.no_rawat = ? and detail_periksa_lab.kd_jenis_prw = ? " +
                                            "and detail_periksa_lab.tgl_periksa = ? and detail_periksa_lab.jam = ? order by template_laboratorium.urut"
                                        )) {
                                            ps3.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                            ps3.setString(2, rs2.getString("kd_jenis_prw"));
                                            ps3.setString(3, rs.getString("tgl_periksa"));
                                            ps3.setString(4, rs.getString("jam"));
                                            try (ResultSet rs3 = ps3.executeQuery()) {
                                                while (rs3.next()) {
                                                    Sequel.temporaryLab(
                                                        String.valueOf(++i), "  " + rs3.getString("Pemeriksaan"), rs3.getString("nilai"),
                                                        rs3.getString("satuan"), rs3.getString("nilai_rujukan"), rs3.getString("keterangan")
                                                    );
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            try (PreparedStatement ps2 = koneksi.prepareStatement(
                                "select permintaan_lab.noorder, permintaan_lab.tgl_permintaan, permintaan_lab.jam_permintaan from permintaan_lab " +
                                "where permintaan_lab.no_rawat = ? and permintaan_lab.tgl_hasil = ? and permintaan_lab.jam_hasil = ?"
                            )) {
                                ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                ps2.setString(2, rs.getString("tgl_periksa"));
                                ps2.setString(3, rs.getString("jam"));
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    if (rs2.next()) {
                                        param.put("nopermintaan", rs2.getString("noorder"));
                                        param.put("tanggalpermintaan", rs2.getString("tgl_permintaan"));
                                        param.put("jampermintaan", rs2.getString("jam_permintaan"));
                                        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptPeriksaLab4PermintaanKompilasi.jasper", urutan + "_HasilLab" + String.valueOf(j), param);
                                    } else {
                                        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptPeriksaLab4Kompilasi.jasper", urutan + "_HasilLab" + String.valueOf(j), param);
                                    }
                                }
                            }
                        } else if (rs.getString("kategori").equals("PA")) {
                            try (PreparedStatement ps2 = koneksi.prepareStatement(
                                "select jns_perawatan_lab.nm_perawatan, detail_periksa_labpa.diagnosa_klinik, detail_periksa_labpa.makroskopik, " +
                                "detail_periksa_labpa.mikroskopik, detail_periksa_labpa.kesimpulan, detail_periksa_labpa.kesan from detail_periksa_labpa " +
                                "join jns_perawatan_lab on detail_periksa_labpa.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where detail_periksa_labpa.no_rawat = ? " +
                                "and detail_periksa_labpa.tgl_periksa = ? and detail_periksa_labpa.jam = ?"
                            )) {
                                ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                ps2.setString(2, rs.getString("tgl_periksa"));
                                ps2.setString(3, rs.getString("jam"));
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    while (rs2.next()) {
                                        Sequel.temporaryLab(String.valueOf(++i), rs2.getString("nm_perawatan"), rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4), rs2.getString(5));
                                    }
                                }
                            }

                            try (PreparedStatement ps2 = koneksi.prepareStatement(
                                "select permintaan_labpa.noorder, permintaan_labpa.tgl_permintaan, permintaan_labpa.jam_permintaan from permintaan_labpa " +
                                "where permintaan_labpa.no_rawat = ? and permintaan_labpa.tgl_hasil = ? and permintaan_labpa.jam_hasil = ?"
                            )) {
                                ps2.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                                ps2.setString(2, rs.getString("tgl_periksa"));
                                ps2.setString(3, rs.getString("jam"));
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    param.put("ttd", Sequel.cariGambarSmc("select dokter_ttdbasah.gambar_ttd from dokter_ttdbasah where dokter_ttdbasah.kd_dokter = ?", rs.getString("kd_dokter")));
                                    if (rs2.next()) {
                                        param.put("nopermintaan", rs2.getString("noorder"));
                                        param.put("tanggalpermintaan", rs2.getString("tgl_permintaan"));
                                        param.put("jampermintaan", rs2.getString("jam_permintaan"));
                                        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptPeriksaLabPermintaanPAKompilasi.jasper", urutan + "_HasilLab" + String.valueOf(j), param);
                                    } else {
                                        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptPeriksaLabPAKompilasi.jasper", urutan + "_HasilLab" + String.valueOf(j), param);
                                    }
                                }
                            }
                        } else if (rs.getString("kategori").equals("MB")) {
                            throw new Exception("Maaf, Hasil pemeriksaan laboratorium mikrobiologi (MB) saat ini belum disupport..!!\nSilahkan hubungi administrator");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new KompilasiException("Hasil Lab", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportHasilRadiologi(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        try {
            int j = 1;
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, date_format(pasien.tgl_lahir, '%d-%m-%Y') as tgllahir, concat(reg_periksa.umurdaftar, ' ', reg_periksa.sttsumur) " +
                "as umur, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, periksa_radiologi.dokter_perujuk, dokter_perujuk.nm_dokter nm_dokter_perujuk, " +
                "periksa_radiologi.tgl_periksa, periksa_radiologi.jam, periksa_radiologi.kd_dokter, dokter.nm_dokter, periksa_radiologi.nip, petugas.nama nama_petugas, jns_perawatan_radiologi.nm_perawatan, " +
                "periksa_radiologi.status, periksa_radiologi.proyeksi, periksa_radiologi.kV, periksa_radiologi.mAS, periksa_radiologi.FFD, periksa_radiologi.BSF, periksa_radiologi.inak, periksa_radiologi.jml_penyinaran, " +
                "periksa_radiologi.dosis from periksa_radiologi join reg_periksa on periksa_radiologi.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join dokter dokter_perujuk " +
                "on periksa_radiologi.dokter_perujuk = dokter_perujuk.kd_dokter join dokter on periksa_radiologi.kd_dokter = dokter.kd_dokter join petugas on periksa_radiologi.nip = petugas.nip join jns_perawatan_radiologi " +
                "on periksa_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw left join kelurahan on pasien.kd_kel = kelurahan.kd_kel left join kecamatan on pasien.kd_kec = kecamatan.kd_kec left join kabupaten " +
                "on pasien.kd_kab = kabupaten.kd_kab where periksa_radiologi.no_rawat = ?"
            )) {
                ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StringJoiner sj = new StringJoiner(", ");
                        if (rs.getString("proyeksi") != null && !rs.getString("proyeksi").isBlank()) {
                            sj.add("Proyeksi : " + rs.getString("proyeksi"));
                        }

                        if (rs.getString("kV") != null && !rs.getString("kV").isBlank()) {
                            sj.add("kV : " + rs.getString("kV"));
                        }

                        if (rs.getString("mAS") != null && !rs.getString("mAS").isBlank()) {
                            sj.add("mAS : " + rs.getString("mAS"));
                        }

                        if (rs.getString("FFD") != null && !rs.getString("FFD").isBlank()) {
                            sj.add("FFD : " + rs.getString("FFD"));
                        }

                        if (rs.getString("BSF") != null && !rs.getString("BSF").isBlank()) {
                            sj.add("BSF : " + rs.getString("BSF"));
                        }

                        if (rs.getString("inak") != null && !rs.getString("inak").isBlank()) {
                            sj.add("Inak : " + rs.getString("inak"));
                        }

                        if (rs.getString("jml_penyinaran") != null && !rs.getString("jml_penyinaran").isBlank()) {
                            sj.add("Jumlah Penyinaran : " + rs.getString("jml_penyinaran"));
                        }

                        if (rs.getString("dosis") != null && !rs.getString("dosis").isBlank()) {
                            sj.add("Dosis Radiasi : " + rs.getString("dosis"));
                        }

                        String pemeriksaan = rs.getString("nm_perawatan");
                        if (sj.length() > 0) {
                            pemeriksaan = pemeriksaan.concat(", dengan") + sj.toString();
                        }

                        Map<String, Object> param = new HashMap<>();
                        param.put("noperiksa", tbKompilasi.getValueAt(row, 1).toString());
                        param.put("norm", rs.getString("no_rkm_medis"));
                        param.put("namapasien", rs.getString("nm_pasien"));
                        param.put("jkel", rs.getString("jk"));
                        param.put("umur", rs.getString("umur"));
                        param.put("lahir", rs.getString("tgllahir"));
                        param.put("pengirim", rs.getString("nm_dokter_perujuk"));
                        param.put("tanggal", rs.getString("tgl_periksa"));
                        param.put("penjab", rs.getString("nm_dokter"));
                        param.put("petugas", rs.getString("nama_petugas"));
                        param.put("alamat", rs.getString("alamat"));
                        String kamar = "", kelas = "", namaKamar = "", noRawatIbu = "";

                        if (Sequel.cariIsiSmc("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString()).equals("Ranap")) {
                            noRawatIbu = Sequel.cariIsiSmc("select ranap_gabung.no_rawat from ranap_gabung where ranap_gabung.no_rawat2 = ?", tbKompilasi.getValueAt(row, 1).toString());
                            if (!noRawatIbu.isBlank()) {
                                kamar = Sequel.cariIsiSmc("select ifnull(kamar_inap.kd_kamar, '') from kamar_inap where kamar_inap.no_rawat = ? order by kamar_inap.tgl_masuk desc limit 1", noRawatIbu);
                                kelas = Sequel.cariIsiSmc("select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar = kamar_inap.kd_kamar where kamar_inap.no_rawat = ? order by str_to_date(concat(kamar_inap.tgl_masuk, ' ', kamar_inap.jam_masuk), '%Y-%m-%d %H:%i:%s') desc limit 1", noRawatIbu);
                            } else {
                                kamar = Sequel.cariIsiSmc("select ifnull(kamar_inap.kd_kamar, '') from kamar_inap where kamar_inap.no_rawat = ? order by kamar_inap.tgl_masuk desc limit 1", tbKompilasi.getValueAt(row, 1).toString());
                                kelas = Sequel.cariIsiSmc("select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar = kamar_inap.kd_kamar where kamar_inap.no_rawat = ? order by str_to_date(concat(kamar_inap.tgl_masuk, ' ', kamar_inap.jam_masuk), '%Y-%m-%d %H:%i:%s') desc limit 1", tbKompilasi.getValueAt(row, 1).toString());
                            }
                            namaKamar = kamar + ", " + Sequel.cariIsiSmc("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal = kamar.kd_bangsal where kamar.kd_kamar = ?", kamar);
                            kamar = "Kamar";
                        } else {
                            kelas = "Rawat Jalan";
                            kamar = "Poli";
                            namaKamar = Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli = reg_periksa.kd_poli where reg_periksa.no_rawat = ?", tbKompilasi.getValueAt(row, 1).toString());
                        }

                        param.put("kamar", kamar);
                        param.put("namakamar", namaKamar);
                        param.put("pemeriksaan", pemeriksaan);
                        param.put("jam", rs.getString("jam"));
                        param.put("namars", akses.getnamars());
                        param.put("alamatrs", akses.getalamatrs());
                        param.put("kotars", akses.getkabupatenrs());
                        param.put("propinsirs", akses.getpropinsirs());
                        param.put("kontakrs", akses.getkontakrs());
                        param.put("emailrs", akses.getemailrs());
                        param.put("hasil", Sequel.cariIsiSmc("select hasil_radiologi.hasil from hasil_radiologi where hasil_radiologi.no_rawat = ? and hasil_radiologi.tgl_periksa = ? and hasil_radiologi.jam = ?", tbKompilasi.getValueAt(row, 1).toString(), rs.getString("tgl_periksa"), rs.getString("jam")));
                        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("tgl_periksa")));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("nip"));
                        param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nama_petugas") + "\nID " + (finger.isBlank() ? rs.getString("nip") : finger) + "\n" + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("tgl_periksa")));
                        param.put("ttd", Sequel.cariGambarSmc("select dokter_ttdbasah.gambar_ttd from dokter_ttdbasah where dokter_ttdbasah.kd_dokter = ?", rs.getString("kd_dokter")));
                        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptPeriksaRadiologiKompilasi.jasper", urutan + "_PeriksaRadiologi" + String.valueOf(j++), param);
                    }
                }
            }
        } catch (Exception e) {
            throw new KompilasiException("Hasil Radiologi", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportSKDP(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        String noSurat = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", tbKompilasi.getValueAt(row, 2).toString());
        String tglSurat = Sequel.cariIsiSmc("select date_format(tgl_surat, '%d-%m-%Y') from bridging_surat_kontrol_bpjs where no_surat = ?", noSurat);
        String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs = (select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat = ?)", noSurat);
        String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
        param.put("parameter", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", tbKompilasi.getValueAt(row, 2).toString()));
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + kodeDokter + "\n" + tglSurat);
        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptBridgingSuratKontrol2.jasper", urutan + "_SuratKontrol", param, "select bridging_sep.no_rawat, bridging_sep.no_sep, " +
            "bridging_sep.no_kartu, bridging_sep.nomr, bridging_sep.nama_pasien, bridging_sep.tanggal_lahir, bridging_sep.jkel, bridging_sep.diagawal, bridging_sep.nmdiagnosaawal, " +
            "bridging_surat_kontrol_bpjs.tgl_surat, bridging_surat_kontrol_bpjs.no_surat, bridging_surat_kontrol_bpjs.tgl_rencana, bridging_surat_kontrol_bpjs.kd_dokter_bpjs, " +
            "bridging_surat_kontrol_bpjs.nm_dokter_bpjs, bridging_surat_kontrol_bpjs.kd_poli_bpjs, bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_sep join " +
            "bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?",
            Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", tbKompilasi.getValueAt(row, 2).toString())
        );
    }

    private void exportSPRI(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        param.put("parameter", tbKompilasi.getValueAt(row, 1).toString());
        String noSPRI = Sequel.cariIsiSmc("select no_surat from bridging_surat_pri_bpjs where no_rawat = ? order by no_surat desc", tbKompilasi.getValueAt(row, 1).toString());
        String kodeDokter = Sequel.cariIsiSmc("Select kd_dokter_bpjs from bridging_surat_pri_bpjs where no_surat = ?", noSPRI);
        String namaDokter = Sequel.cariIsiSmc("select nm_dokter_bpjs from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
        String tglSPRI = Sequel.cariIsiSmc("select date_format(tgl_rencana, '%d-%m-%Y') from bridging_surat_pri_bpjs where no_surat = ?", noSPRI);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + kodeDokter + "\n" + tglSPRI);
        simpanPDF(tbKompilasi.getValueAt(row, 2).toString(), "rptBridgingSuratPRI2.jasper", urutan + "_SPRI", param,
            "select bridging_surat_pri_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk " +
            "from reg_periksa join bridging_surat_pri_bpjs on bridging_surat_pri_bpjs.no_rawat = reg_periksa.no_rawat " +
            "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where bridging_surat_pri_bpjs.no_surat = ?", noSPRI
        );
    }

    private void exportRiwayatPasien(final String urutan, final boolean ada, final int row) throws Exception {
        if (resume == null) {
            resume = new RMRiwayatPerawatan(null, false);
        }
        try {
            resume.kompilasiDariRiwayat(tbKompilasi.getValueAt(row, 1).toString(), tbKompilasi.getValueAt(row, 3).toString(), tanggalExport, tbKompilasi.getValueAt(row, 2).toString(), urutan);
        } catch (Exception e) {
            throw new KompilasiException("Riwayat Pasien", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void exportBerkasDigitalPerawatan(final String urutan, final boolean ada, final int row) throws Exception {
        if (!ada) {
            return;
        }

        String filename = "", exportPath = "";
        URL fileUrl;
        HttpURLConnection http;
        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/";
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select berkas_digital_perawatan.lokasi_file, master_berkas_digital.nama from berkas_digital_perawatan join master_berkas_digital " +
            "on berkas_digital_perawatan.kode = master_berkas_digital.kode where berkas_digital_perawatan.no_rawat = ? and berkas_digital_perawatan.lokasi_file like " +
            "'%.pdf' and master_berkas_digital.include_kompilasi_berkas = 1"
        )) {
            ps.setString(1, tbKompilasi.getValueAt(row, 1).toString());
            try (ResultSet rs = ps.executeQuery()) {
                for (int i = 1; rs.next(); i++) {
                    filename = rs.getString("lokasi_file");
                    exportPath = "./berkaspdf/" + tanggalExport + "/" + tbKompilasi.getValueAt(row, 2).toString() + "_" + urutan + "_BerkasDigital" + String.valueOf(i) + ".pdf";
                    if (filename.endsWith(".pdf")) {
                        try (FileOutputStream os = new FileOutputStream(exportPath); FileChannel fileChannel = os.getChannel()) {
                            fileUrl = new URL(url + rs.getString("lokasi_file"));
                            http = (HttpURLConnection) fileUrl.openConnection();
                            if (http.getResponseCode() == 200) {
                                fileChannel.transferFrom(Channels.newChannel(fileUrl.openStream()), 0, Long.MAX_VALUE);
                                http.disconnect();
                            } else {
                                fileChannel.close();
                                os.close();
                                hapusTemporaryPDF(tbKompilasi.getValueAt(row, 2).toString() + "_" + urutan + "_BerkasDigital" + String.valueOf(i));
                                System.out.println("File tidak ditemukan : " + url + rs.getString("lokasi_file"));
                                if (JOptionPane.showConfirmDialog(null, "Berkas " + rs.getString("nama") + " \"" + rs.getString("lokasi_file").substring(rs.getString("lokasi_file").lastIndexOf("/") + 1) + "\" tidak ditemukan, lewati?", "Lewati Berkas", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                                    throw new Exception("Terdapat berkas digital yang tidak bisa ditemukan..!!");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new KompilasiException("Digital", urutan, tbKompilasi.getValueAt(row, 2).toString(), e);
        }
    }

    private void mergePDF(final int row) throws Exception {
        PDFMergerUtility merger = new PDFMergerUtility();
        File folder = new File("./berkaspdf/" + tanggalExport);
        List<File> files = Arrays.asList(folder.listFiles());

        if (!files.isEmpty()) {
            try {
                for (File file : files.stream()
                    .filter(file -> file.isFile() && file.getName().endsWith(".pdf") && file.getName().startsWith(tbKompilasi.getValueAt(row, 2).toString() + "_"))
                    .sorted((file1, file2) -> file1.getName().compareTo(file2.getName()))
                    .collect(Collectors.toList())
                ) {
                    try {
                        merger.addSource(file);
                    } catch (Exception e) {
                        System.err.println("Error menambah file: " + file.getName());
                        throw e;
                    }
                }

                merger.setDestinationFileName("./berkaspdf/" + tanggalExport + "/" + tbKompilasi.getValueAt(row, 2).toString() + ".pdf");
                merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly(maxMemory * 1_000_000));
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                throw new KompilasiException("Kompilasi", "", tbKompilasi.getValueAt(row, 2).toString(), e);
            }
        } else {
            System.out.println("Tidak ada file PDF ditemukan dalam folder : " + folder.getAbsolutePath());
        }
    }

    private void mergePDF() throws Exception {
        if (selectedRow < 0) return;

        mergePDF(selectedRow);
    }

    private void hapusTemporaryPDF(final int row) {
        File folder = new File("./berkaspdf/" + tanggalExport);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(tbKompilasi.getValueAt(row, 2).toString() + "_")) {
                    if (!file.delete()) {
                        System.out.println("Notif : Gagal menghapus file sementara " + file.getName());
                    }
                }
            }
        } else {
            System.out.println("Notif : Tidak ada file sementara ditemukan dalam " + folder.toString());
        }
    }

    private void hapusTemporaryPDF() throws Exception {
        if (selectedRow < 0) return;

        hapusTemporaryPDF(selectedRow);
    }

    private void gabung() {
        // TODO: kasih treatment background worker
        try {
            if (gunakanTanggalExport.equals("sep")) {
                tanggalExport = lblTglSEP.getText();
            } else {
                tanggalExport = LocalDate.now().toString();
            }

            if (tbKompilasi.getValueAt(selectedRow, 5).toString().equals("Ralan")) {
                if (KOMPILASIBERKASGUNAKANRIWAYATPASIEN.contains("ralan")) {
                    exportHasilKlaim("001", btnHasilKlaim.isEnabled(), selectedRow);
                    exportSEP("002", true, selectedRow);
                    exportRiwayatPasien("003", true, selectedRow);
                    exportBerkasDigitalPerawatan("004", true, selectedRow);
                } else {
                    exportHasilKlaim("001", btnHasilKlaim.isEnabled(), selectedRow);
                    exportSEP("002", true, selectedRow);
                    exportTriaseIGD("003", btnTriaseIGD.isEnabled(), selectedRow);
                    exportAwalMedisIGD("004", btnAwalMedisIGD.isEnabled(), selectedRow);
                    exportSOAP("005", true, selectedRow);
                    exportBilling("007", true, selectedRow);
                    exportHasilLab("008", btnHasilLab.isEnabled(), selectedRow);
                    exportHasilRadiologi("009", btnHasilRad.isEnabled(), selectedRow);
                    exportBerkasDigitalPerawatan("010", true, selectedRow);
                    // exportSKDP("009");
                    // exportSPRI("010");
                }
            } else if (tbKompilasi.getValueAt(selectedRow, 5).toString().equals("Ranap")) {
                if (KOMPILASIBERKASGUNAKANRIWAYATPASIEN.contains("ranap")) {
                    exportHasilKlaim("001", btnHasilKlaim.isEnabled(), selectedRow);
                    exportSEP("002", true, selectedRow);
                    exportRiwayatPasien("003", true, selectedRow);
                    exportBerkasDigitalPerawatan("004", true, selectedRow);
                } else {
                    exportHasilKlaim("001", btnHasilKlaim.isEnabled(), selectedRow);
                    exportSEP("002", true, selectedRow);
                    exportTriaseIGD("003", btnTriaseIGD.isEnabled(), selectedRow);
                    exportAwalMedisIGD("004", btnAwalMedisIGD.isEnabled(), selectedRow);
                    exportSOAP("005", true, selectedRow);
                    exportResumeRanap("006", btnResumeRanap.isEnabled(), selectedRow);
                    exportBilling("007", true, selectedRow);
                    exportHasilLab("008", btnHasilLab.isEnabled(), selectedRow);
                    exportHasilRadiologi("009", btnHasilRad.isEnabled(), selectedRow);
                    exportBerkasDigitalPerawatan("010", true, selectedRow);
                    // exportSKDP("009");
                    // exportSPRI("010");
                }
            }

            mergePDF();
            hapusTemporaryPDF();
            JOptionPane.showMessageDialog(null, "Kompilasi berkas PDF berhasil!");
        } catch (KompilasiException e) {
            System.out.println("Notif : " + e.getCause().getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage() + "..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat melakukan kompilasi berkas..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bulkKompilasiBerkas(final int selectedRowCount) {
        JCheckBox lanjutKompilasiApabilaGagal = new JCheckBox("Lewati berkas gagal diproses", false);

        if (JOptionPane.showConfirmDialog(null, new Object[] {"Lakukan kompilasi untuk semua berkas yang dipilih?", lanjutKompilasiApabilaGagal}, "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }

        lanjutKompilasiApabilaGagal.setText("Untuk selanjutnya, " + lanjutKompilasiApabilaGagal.getText().toLowerCase());

        JProgressBar bar = new JProgressBar(0, selectedRowCount);
        bar.setStringPainted(true);

        JButton cancel = new JButton("Batal");
        JLabel judul = new JLabel("Mengkompilasi berkas SEP 123456789012V999999...");

        JOptionPane progressPane = new JOptionPane(new Object[] {judul, bar}, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {cancel});

        JDialog popup = progressPane.createDialog("Mengkompilasi berkas...");
        popup.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        popup.setModal(true);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            private final Object lock = new Object();
            private volatile Boolean lanjut = null;
            private String tglSekarang = LocalDate.now().toString();

            @Override
            protected Void doInBackground() throws Exception {
                String noSEP = "";
                firePropertyChange("setJudul", null, "");

                for (int i = 0, j = 0; i < tbKompilasi.getRowCount(); i++) {
                    if (isCancelled()) return null;

                    if ((Boolean) tbKompilasi.getValueAt(i, 0)) {
                        firePropertyChange("setJudul", noSEP, tbKompilasi.getValueAt(i, 2).toString());
                        String noSuratKontrol = Sequel.cariIsiSmc("select bridging_sep.noskdp from bridging_sep where bridging_sep.no_sep = ?", tbKompilasi.getValueAt(i, 2).toString());
                        if (noSuratKontrol.isBlank()) {
                            noSuratKontrol = Sequel.cariIsiSmc("select bridging_sep.noskdp from bridging_sep where bridging_sep.no_rawat = ? and bridging_sep.noskdp != ''", tbKompilasi.getValueAt(i, 1).toString());
                        }

                        try (PreparedStatement ps = koneksi.prepareStatement(
                            "select exists(select * from data_triase_igd t where t.no_rawat = s.no_rawat) as ada_triase_igd, " +
                            "exists(select * from pemeriksaan_ralan pr where pr.no_rawat = s.no_rawat) as ada_soap, " +
                            "exists(select * from resume_pasien_ranap r where r.no_rawat = s.no_rawat) as ada_resume_ranap, " +
                            "exists(select * from penilaian_medis_igd p where p.no_rawat = s.no_rawat) as ada_awal_medis_igd, " +
                            "exists(select * from periksa_lab pl where pl.no_rawat = s.no_rawat) as ada_periksa_lab, " +
                            "exists(select * from periksa_radiologi pr where pr.no_rawat = s.no_rawat) as ada_periksa_rad, " +
                            "exists(select * from bridging_surat_kontrol_bpjs skdp where skdp.no_surat = ?) as ada_skdp, " +
                            "exists(select * from bridging_surat_pri_bpjs spri where spri.no_rawat = s.no_rawat) as ada_spri, " +
                            "exists(select * from inacbg_cetak_klaim i where i.no_sep = s.no_sep) as ada_hasil_klaim, " +
                            "exists(select * from berkas_digital_perawatan b where b.no_rawat = s.no_rawat) as ada_berkas_digital " +
                            "from bridging_sep s where s.no_sep = ?"
                        )) {
                            ps.setString(1, noSuratKontrol);
                            ps.setString(2, tbKompilasi.getValueAt(i, 2).toString());
                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    if (gunakanTanggalExport.equals("sep")) {
                                        tanggalExport = tbKompilasi.getValueAt(i, 6).toString();
                                    } else {
                                        tanggalExport = tglSekarang;
                                    }

                                    try {
                                        if (tbKompilasi.getValueAt(i, 5).toString().equals("Ralan")) {
                                            if (KOMPILASIBERKASGUNAKANRIWAYATPASIEN.contains("ralan")) {
                                                exportHasilKlaim("001", rs.getBoolean("ada_hasil_klaim"), i);
                                                exportSEP("002", true, i);
                                                exportRiwayatPasien("003", true, i);
                                                exportBerkasDigitalPerawatan("004", rs.getBoolean("ada_berkas_digital"), i);
                                            } else {
                                                exportHasilKlaim("001", rs.getBoolean("ada_hasil_klaim"), i);
                                                exportSEP("002", true, i);
                                                exportTriaseIGD("003", rs.getBoolean("ada_triase_igd"), i);
                                                exportAwalMedisIGD("004", rs.getBoolean("ada_awal_medis_igd"), i);
                                                exportSOAP("005", rs.getBoolean("ada_soap"), i);
                                                exportBilling("007", true, i);
                                                exportHasilLab("008", rs.getBoolean("ada_periksa_lab"), i);
                                                exportHasilRadiologi("009", rs.getBoolean("ada_periksa_rad"), i);
                                                exportBerkasDigitalPerawatan("010", rs.getBoolean("ada_berkas_digital"), i);
                                                // exportSKDP("009");
                                                // exportSPRI("010");
                                            }
                                        } else if (tbKompilasi.getValueAt(i, 5).toString().equals("Ranap")) {
                                            if (KOMPILASIBERKASGUNAKANRIWAYATPASIEN.contains("ranap")) {
                                                exportHasilKlaim("001", rs.getBoolean("ada_hasil_klaim"), i);
                                                exportSEP("002", true, i);
                                                exportRiwayatPasien("003", true, i);
                                                exportBerkasDigitalPerawatan("004", rs.getBoolean("ada_berkas_digital"), i);
                                            } else {
                                                exportHasilKlaim("001", rs.getBoolean("ada_hasil_klaim"), i);
                                                exportSEP("002", true, i);
                                                exportTriaseIGD("003", rs.getBoolean("ada_triase_igd"), i);
                                                exportAwalMedisIGD("004", rs.getBoolean("ada_awal_medis_igd"), i);
                                                exportSOAP("005", rs.getBoolean("ada_soap"), i);
                                                exportResumeRanap("006", rs.getBoolean("ada_resume_ranap"), i);
                                                exportBilling("007", true, i);
                                                exportHasilLab("008", rs.getBoolean("ada_periksa_lab"), i);
                                                exportHasilRadiologi("009", rs.getBoolean("ada_periksa_rad"), i);
                                                exportBerkasDigitalPerawatan("010", rs.getBoolean("ada_berkas_digital"), i);
                                                // exportSKDP("009");
                                                // exportSPRI("010");
                                            }
                                        }

                                        mergePDF(i);
                                        hapusTemporaryPDF(i);
                                    } catch (KompilasiException e) {
                                        hapusTemporaryPDF(i);
                                        publish(e.getMessage());
                                        synchronized (lock) {
                                            while (lanjut == null) {
                                                // perlu timeout
                                                lock.wait();
                                            }

                                            if (!lanjut) {
                                                cancel(true);
                                                return null;
                                            }

                                            lanjut = null;
                                        }
                                    }
                                }
                            }
                        }
                        noSEP = tbKompilasi.getValueAt(i, 2).toString();
                        firePropertyChange("rowCompleted", i - 1, i);
                        firePropertyChange("kompilasiProgress", j, ++j);
                    }
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                int konfirm = JOptionPane.YES_OPTION;
                if (!lanjutKompilasiApabilaGagal.isSelected()) {
                    konfirm = JOptionPane.showConfirmDialog(null, new Object[] {chunks.get(chunks.size() - 1) + ", tetap lanjut?", lanjutKompilasiApabilaGagal}, "Lanjut?", JOptionPane.YES_NO_OPTION);
                }
                synchronized (lock) {
                    lanjut = (konfirm == JOptionPane.YES_OPTION);
                    lock.notify();
                }
            }

            @Override
            protected void done() {
                popup.dispose();
                try {
                    get();
                    JOptionPane.showMessageDialog(null, "Bulk kompilasi selesai..!!");
                } catch (CancellationException e) {
                    JOptionPane.showMessageDialog(null, "Proses dihentikan oleh user..!!");
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat melakukan kompilasi berkas\nProses dibatalkan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("setJudul".equals(evt.getPropertyName())) {
                judul.setText("Memproses SEP " + evt.getNewValue() + "...");
            }

            if ("kompilasiProgress".equals(evt.getPropertyName())) {
                bar.setValue((Integer) evt.getNewValue());
            }

            if ("rowCompleted".equals(evt.getPropertyName())) {
                tbKompilasi.setValueAt(false, (Integer) evt.getNewValue(), 0);
            }
        });

        popup.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Batalkan proses bulk kompilasi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    worker.cancel(false);
                }
            }
        });

        cancel.addActionListener(evt -> {
            if (JOptionPane.showConfirmDialog(null, "Batalkan proses bulk kompilasi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                worker.cancel(false);
            }
        });

        worker.execute();
        popup.setVisible(true);
    }

    static class KompilasiException extends Exception {
        private String namaBerkas, urut, noSEP;

        public KompilasiException(String namaBerkas, String urut, String noSEP, Throwable err) {
            super("Berkas " + namaBerkas + " untuk no. SEP " + noSEP + " tidak dapat diproses", err);
            this.namaBerkas = namaBerkas;
            this.urut = urut;
            this.noSEP = noSEP;
        }

        public String getNamaBerkas() {
            return namaBerkas;
        }

        public String getUrut() {
            return urut;
        }

        public String getNoSEP() {
            return noSEP;
        }
    }
}
