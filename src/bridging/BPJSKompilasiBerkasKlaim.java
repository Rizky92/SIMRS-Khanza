package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import laporan.DlgBerkasRawat;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.codec.digest.DigestUtils;
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
    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final JFXPanel jfxPanelicare = new JFXPanel();
    private final JFXPanel jfxinvoices = new JFXPanel();
    private final RMRiwayatPerawatan resume = new RMRiwayatPerawatan(null, true);
    private final DlgCariCaraBayar penjab = new DlgCariCaraBayar(null, false);
    private WebEngine engine;
    private String finger = "", tanggalExport = "";
    private boolean exportSukses = true;

    public BPJSKompilasiBerkasKlaim(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tabMode = new DefaultTableModel(null, new Object[] {
            "No. Rawat", "No. SEP", "No. RM", "Nama Pasien", "Status",
            "Tgl. Registrasi", "Tgl. Pulang", "Stts. Pulang", "Ruangan", "DPJP",
            "Diagnosa", "Status INACBG", "KirimINACBG"
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                return java.lang.String.class;
            }
            
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbKompilasi.setModel(tabMode);
        tbKompilasi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKompilasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        tbKompilasi.getColumnModel().getColumn(0).setPreferredWidth(10);
        tbKompilasi.getColumnModel().getColumn(1).setPreferredWidth(126);
        tbKompilasi.getColumnModel().getColumn(2).setPreferredWidth(46);
        tbKompilasi.getColumnModel().getColumn(3).setPreferredWidth(200);
        tbKompilasi.getColumnModel().getColumn(4).setPreferredWidth(50);
        tbKompilasi.getColumnModel().getColumn(5).setPreferredWidth(80);
        tbKompilasi.getColumnModel().getColumn(6).setPreferredWidth(70);
        tbKompilasi.getColumnModel().getColumn(7).setPreferredWidth(90);
        tbKompilasi.getColumnModel().getColumn(8).setPreferredWidth(180);
        tbKompilasi.getColumnModel().getColumn(9).setPreferredWidth(140);
        tbKompilasi.getColumnModel().getColumn(10).setPreferredWidth(70);
        tbKompilasi.getColumnModel().getColumn(11).setPreferredWidth(100);
        tbKompilasi.getColumnModel().getColumn(12).setMinWidth(0);
        tbKompilasi.getColumnModel().getColumn(12).setMaxWidth(0);
        
        tbKompilasi.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 1) {
                    component.setBackground(new Color(255, 244, 244));
                    component.setForeground(new Color(50, 50, 50));
                } else {
                    component.setBackground(new Color(255, 255, 255));
                    component.setForeground(new Color(50, 50, 50));
                }
                if (table.getValueAt(row, 12).toString().equals("1")) {
                    component.setBackground(new Color(50, 50, 50));
                    component.setForeground(new Color(255, 255, 255));
                }
                return component;
            }
        });

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
        
        panelDiagnosaSmc.getTabbedPane().addChangeListener((ChangeEvent e) -> {
            JTabbedPane tab = (JTabbedPane) e.getSource();
            if (tab.getSelectedIndex() == 0) {
                BtnSimpanDiagnosa.setEnabled(true);
                BtnHapusDiagnosa.setEnabled(false);
            } else {
                BtnSimpanDiagnosa.setEnabled(false);
                BtnHapusDiagnosa.setEnabled(true);
            }
        });
        
        penjab.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (penjab.getTable().getSelectedRow() != -1) {
                    KdPj.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(), 1).toString());
                    NamaPj.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(), 2).toString());
                    tampil();
                }
                KdPj.requestFocus();
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

    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppBerkasDigital = new javax.swing.JMenuItem();
        ppRiwayatPerawatan = new javax.swing.JMenuItem();
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
        internalFrame1 = new widget.InternalFrame();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        label19 = new widget.Label();
        KdPj = new widget.TextBox();
        NamaPj = new widget.TextBox();
        BtnPenjamin = new widget.Button();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
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
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        panelGlass11 = new widget.panelisi();
        Scroll = new widget.ScrollPane();
        tbKompilasi = new widget.Table();
        jPanel2 = new javax.swing.JPanel();
        scrollPane1 = new widget.ScrollPane();
        panelBiasa1 = new widget.PanelBiasa();
        jLabel14 = new widget.Label();
        lblNamaPasien = new widget.Label();
        jLabel15 = new widget.Label();
        lblNoRawat = new widget.Label();
        lblNoRM = new widget.Label();
        jLabel17 = new widget.Label();
        lblNoSEP = new widget.Label();
        jLabel18 = new widget.Label();
        btnSEP = new widget.Button();
        jLabel25 = new widget.Label();
        btnResumeRanap = new widget.Button();
        jLabel26 = new widget.Label();
        btnInvoice = new widget.Button();
        jLabel27 = new widget.Label();
        btnAwalMedisIGD = new widget.Button();
        jLabel28 = new widget.Label();
        btnHasilLab = new widget.Button();
        jLabel29 = new widget.Label();
        btnHasilRad = new widget.Button();
        jLabel30 = new widget.Label();
        btnSurkon = new widget.Button();
        jLabel31 = new widget.Label();
        btnSPRI = new widget.Button();
        jLabel20 = new widget.Label();
        lblStatusRawat = new widget.Label();
        BtnSimpanDiagnosa = new widget.Button();
        BtnHapusDiagnosa = new widget.Button();
        jLabel32 = new widget.Label();
        btnRiwayatPasien = new widget.Button();
        panelDiagnosaSmc = new laporan.PanelDiagnosaSmc();
        jLabel33 = new widget.Label();
        btnPDFKlaimINACBG = new widget.Button();
        jLabel34 = new widget.Label();
        btnTriaseIGD = new widget.Button();
        panelBiasa2 = new widget.PanelBiasa();
        BtnValidasiQR = new widget.Button();
        jPanel5 = new javax.swing.JPanel();
        tabPane1 = new widget.TabPane();
        panelInvoices = new widget.panelisi();
        PanelContentINACBG = new widget.panelisi();

        jPopupMenu1.setForeground(new java.awt.Color(50, 50, 50));
        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppBerkasDigital.setBackground(new java.awt.Color(255, 255, 254));
        ppBerkasDigital.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBerkasDigital.setForeground(new java.awt.Color(50, 50, 50));
        ppBerkasDigital.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBerkasDigital.setText("Berkas Perawatan Digital");
        ppBerkasDigital.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBerkasDigital.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBerkasDigital.setName("ppBerkasDigital"); // NOI18N
        ppBerkasDigital.setPreferredSize(new java.awt.Dimension(200, 26));
        ppBerkasDigital.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBerkasDigitalActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBerkasDigital);

        ppRiwayatPerawatan.setBackground(new java.awt.Color(255, 255, 254));
        ppRiwayatPerawatan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppRiwayatPerawatan.setForeground(new java.awt.Color(50, 50, 50));
        ppRiwayatPerawatan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppRiwayatPerawatan.setText("Riwayat Perawatan");
        ppRiwayatPerawatan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppRiwayatPerawatan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppRiwayatPerawatan.setName("ppRiwayatPerawatan"); // NOI18N
        ppRiwayatPerawatan.setPreferredSize(new java.awt.Dimension(200, 26));
        ppRiwayatPerawatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppRiwayatPerawatanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppRiwayatPerawatan);

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

        TanggalPulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-07-2024 10:35:22" }));
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
        NoSuratKematian.setHighlighter(null);
        NoSuratKematian.setName("NoSuratKematian"); // NOI18N
        NoSuratKematian.setPreferredSize(new java.awt.Dimension(130, 23));
        internalFrame11.add(NoSuratKematian);
        NoSuratKematian.setBounds(123, 122, 160, 23);

        jLabel48.setText("Tanggal Kematian :");
        jLabel48.setName("jLabel48"); // NOI18N
        internalFrame11.add(jLabel48);
        jLabel48.setBounds(300, 122, 100, 23);

        TanggalKematian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-07-2024" }));
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

        NoLPManual.setHighlighter(null);
        NoLPManual.setName("NoLPManual"); // NOI18N
        NoLPManual.setPreferredSize(new java.awt.Dimension(130, 23));
        internalFrame11.add(NoLPManual);
        NoLPManual.setBounds(123, 152, 160, 23);

        jLabel9.setText("No. Rawat :");
        jLabel9.setName("jLabel9"); // NOI18N
        internalFrame11.add(jLabel9);
        jLabel9.setBounds(0, 32, 78, 23);

        TNoRwPulang.setEditable(false);
        TNoRwPulang.setHighlighter(null);
        TNoRwPulang.setName("TNoRwPulang"); // NOI18N
        internalFrame11.add(TNoRwPulang);
        TNoRwPulang.setBounds(81, 32, 180, 23);

        TNoRMPulang.setEditable(false);
        TNoRMPulang.setHighlighter(null);
        TNoRMPulang.setName("TNoRMPulang"); // NOI18N
        internalFrame11.add(TNoRMPulang);
        TNoRMPulang.setBounds(81, 62, 130, 23);

        TPasienPulang.setEditable(false);
        TPasienPulang.setHighlighter(null);
        TPasienPulang.setName("TPasienPulang"); // NOI18N
        internalFrame11.add(TPasienPulang);
        TPasienPulang.setBounds(214, 62, 361, 23);

        jLabel41.setText("Pasien :");
        jLabel41.setName("jLabel41"); // NOI18N
        internalFrame11.add(jLabel41);
        jLabel41.setBounds(0, 62, 78, 23);

        TNoSEPRanapPulang.setEditable(false);
        TNoSEPRanapPulang.setHighlighter(null);
        TNoSEPRanapPulang.setName("TNoSEPRanapPulang"); // NOI18N
        internalFrame11.add(TNoSEPRanapPulang);
        TNoSEPRanapPulang.setBounds(395, 32, 180, 23);

        jLabel10.setText("No. SEP Ranap :");
        jLabel10.setName("jLabel10"); // NOI18N
        internalFrame11.add(jLabel10);
        jLabel10.setBounds(310, 32, 82, 23);

        WindowUpdatePulang.getContentPane().add(internalFrame11, java.awt.BorderLayout.CENTER);

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

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label19.setText("Jenis Bayar :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass8.add(label19);

        KdPj.setEditable(false);
        KdPj.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        KdPj.setName("KdPj"); // NOI18N
        KdPj.setPreferredSize(new java.awt.Dimension(41, 23));
        panelGlass8.add(KdPj);

        NamaPj.setEditable(false);
        NamaPj.setName("NamaPj"); // NOI18N
        NamaPj.setPreferredSize(new java.awt.Dimension(170, 23));
        panelGlass8.add(NamaPj);

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
        panelGlass10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tgl. Rawat :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass10.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-07-2024" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-07-2024" }));
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

        jLabel11.setText("Status Kirim :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass10.add(jLabel11);

        CmbStatusKirim.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Terkirim", "Belum Terkirim" }));
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

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 102));
        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "Data Pasien", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setOpaque(false);

        panelGlass11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        panelGlass11.setName("panelGlass11"); // NOI18N
        panelGlass11.setLayout(new java.awt.BorderLayout());

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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbKompilasiMouseClicked(evt);
            }
        });
        tbKompilasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbKompilasiKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbKompilasi);

        panelGlass11.add(Scroll, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGlass11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGlass11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "Kelengkapan Data", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(800, 820));
        jPanel2.setLayout(new java.awt.BorderLayout());

        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setPreferredSize(new java.awt.Dimension(800, 820));

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(800, 790));
        panelBiasa1.setLayout(null);

        jLabel14.setText("Data Pasien : ");
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel14);
        jLabel14.setBounds(0, 10, 120, 14);

        lblNamaPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNamaPasien.setName("lblNamaPasien"); // NOI18N
        lblNamaPasien.setPreferredSize(new java.awt.Dimension(300, 14));
        panelBiasa1.add(lblNamaPasien);
        lblNamaPasien.setBounds(160, 10, 200, 14);

        jLabel15.setText("No Rawat : ");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel15);
        jLabel15.setBounds(0, 30, 120, 14);

        lblNoRawat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoRawat.setName("lblNoRawat"); // NOI18N
        lblNoRawat.setPreferredSize(new java.awt.Dimension(300, 14));
        panelBiasa1.add(lblNoRawat);
        lblNoRawat.setBounds(120, 30, 240, 14);

        lblNoRM.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoRM.setName("lblNoRM"); // NOI18N
        lblNoRM.setPreferredSize(new java.awt.Dimension(300, 14));
        panelBiasa1.add(lblNoRM);
        lblNoRM.setBounds(120, 10, 36, 14);

        jLabel17.setText("No SEP : ");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel17);
        jLabel17.setBounds(0, 70, 120, 14);

        lblNoSEP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNoSEP.setName("lblNoSEP"); // NOI18N
        lblNoSEP.setPreferredSize(new java.awt.Dimension(300, 14));
        panelBiasa1.add(lblNoSEP);
        lblNoSEP.setBounds(120, 70, 240, 14);

        jLabel18.setText("Data SEP : ");
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel18);
        jLabel18.setBounds(0, 110, 120, 14);

        btnSEP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSEP.setMnemonic('1');
        btnSEP.setText("Lihat");
        btnSEP.setToolTipText("ALt+1");
        btnSEP.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSEP.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSEP.setName("btnSEP"); // NOI18N
        btnSEP.setPreferredSize(new java.awt.Dimension(100, 14));
        btnSEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSEPActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnSEP);
        btnSEP.setBounds(120, 110, 100, 14);

        jLabel25.setText("Resume Ranap : ");
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel25);
        jLabel25.setBounds(0, 190, 120, 14);

        btnResumeRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnResumeRanap.setMnemonic('1');
        btnResumeRanap.setText("Lihat");
        btnResumeRanap.setToolTipText("ALt+1");
        btnResumeRanap.setEnabled(false);
        btnResumeRanap.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnResumeRanap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnResumeRanap.setName("btnResumeRanap"); // NOI18N
        btnResumeRanap.setPreferredSize(new java.awt.Dimension(100, 14));
        btnResumeRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResumeRanapActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnResumeRanap);
        btnResumeRanap.setBounds(120, 190, 100, 14);

        jLabel26.setText("Billing : ");
        jLabel26.setName("jLabel26"); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel26);
        jLabel26.setBounds(0, 130, 120, 14);

        btnInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnInvoice.setMnemonic('1');
        btnInvoice.setText("Lihat");
        btnInvoice.setToolTipText("ALt+1");
        btnInvoice.setEnabled(false);
        btnInvoice.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnInvoice.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInvoice.setName("btnInvoice"); // NOI18N
        btnInvoice.setPreferredSize(new java.awt.Dimension(100, 14));
        btnInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvoiceActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnInvoice);
        btnInvoice.setBounds(120, 130, 100, 14);

        jLabel27.setText("Awal Medis IGD : ");
        jLabel27.setName("jLabel27"); // NOI18N
        jLabel27.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel27);
        jLabel27.setBounds(0, 170, 120, 14);

        btnAwalMedisIGD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAwalMedisIGD.setMnemonic('1');
        btnAwalMedisIGD.setText("Lihat");
        btnAwalMedisIGD.setToolTipText("ALt+1");
        btnAwalMedisIGD.setEnabled(false);
        btnAwalMedisIGD.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnAwalMedisIGD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAwalMedisIGD.setName("btnAwalMedisIGD"); // NOI18N
        btnAwalMedisIGD.setPreferredSize(new java.awt.Dimension(100, 14));
        btnAwalMedisIGD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAwalMedisIGDActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnAwalMedisIGD);
        btnAwalMedisIGD.setBounds(120, 170, 100, 14);

        jLabel28.setText("Hasil Lab : ");
        jLabel28.setName("jLabel28"); // NOI18N
        jLabel28.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel28);
        jLabel28.setBounds(0, 210, 120, 14);

        btnHasilLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnHasilLab.setMnemonic('1');
        btnHasilLab.setText("Lihat");
        btnHasilLab.setToolTipText("ALt+1");
        btnHasilLab.setEnabled(false);
        btnHasilLab.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnHasilLab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHasilLab.setName("btnHasilLab"); // NOI18N
        btnHasilLab.setPreferredSize(new java.awt.Dimension(100, 14));
        btnHasilLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHasilLabActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnHasilLab);
        btnHasilLab.setBounds(120, 210, 100, 14);

        jLabel29.setText("Hasil Radiologi : ");
        jLabel29.setName("jLabel29"); // NOI18N
        jLabel29.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel29);
        jLabel29.setBounds(0, 230, 120, 14);

        btnHasilRad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnHasilRad.setMnemonic('1');
        btnHasilRad.setText("Lihat");
        btnHasilRad.setToolTipText("ALt+1");
        btnHasilRad.setEnabled(false);
        btnHasilRad.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnHasilRad.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHasilRad.setName("btnHasilRad"); // NOI18N
        btnHasilRad.setPreferredSize(new java.awt.Dimension(100, 14));
        btnHasilRad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHasilRadActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnHasilRad);
        btnHasilRad.setBounds(120, 230, 100, 14);

        jLabel30.setText("Surat Kontrol : ");
        jLabel30.setName("jLabel30"); // NOI18N
        jLabel30.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel30);
        jLabel30.setBounds(0, 290, 120, 14);

        btnSurkon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSurkon.setMnemonic('1');
        btnSurkon.setText("Lihat");
        btnSurkon.setToolTipText("ALt+1");
        btnSurkon.setEnabled(false);
        btnSurkon.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSurkon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSurkon.setName("btnSurkon"); // NOI18N
        btnSurkon.setPreferredSize(new java.awt.Dimension(100, 14));
        btnSurkon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSurkonActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnSurkon);
        btnSurkon.setBounds(120, 290, 100, 14);

        jLabel31.setText("SPRI : ");
        jLabel31.setName("jLabel31"); // NOI18N
        jLabel31.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel31);
        jLabel31.setBounds(0, 270, 120, 14);

        btnSPRI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSPRI.setMnemonic('1');
        btnSPRI.setText("Lihat");
        btnSPRI.setToolTipText("ALt+1");
        btnSPRI.setEnabled(false);
        btnSPRI.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSPRI.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSPRI.setName("btnSPRI"); // NOI18N
        btnSPRI.setPreferredSize(new java.awt.Dimension(100, 14));
        btnSPRI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSPRIActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnSPRI);
        btnSPRI.setBounds(120, 270, 100, 14);

        jLabel20.setText("Status Rawat : ");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel20);
        jLabel20.setBounds(0, 50, 120, 14);

        lblStatusRawat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStatusRawat.setName("lblStatusRawat"); // NOI18N
        lblStatusRawat.setPreferredSize(new java.awt.Dimension(300, 14));
        panelBiasa1.add(lblStatusRawat);
        lblStatusRawat.setBounds(120, 50, 240, 14);

        BtnSimpanDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanDiagnosa.setMnemonic('S');
        BtnSimpanDiagnosa.setText("Simpan");
        BtnSimpanDiagnosa.setToolTipText("Alt+S");
        BtnSimpanDiagnosa.setAlignmentY(0.0F);
        BtnSimpanDiagnosa.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnSimpanDiagnosa.setMaximumSize(new java.awt.Dimension(76, 26));
        BtnSimpanDiagnosa.setMinimumSize(new java.awt.Dimension(76, 26));
        BtnSimpanDiagnosa.setName("BtnSimpanDiagnosa"); // NOI18N
        BtnSimpanDiagnosa.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpanDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanDiagnosaActionPerformed(evt);
            }
        });
        panelBiasa1.add(BtnSimpanDiagnosa);
        BtnSimpanDiagnosa.setBounds(0, 750, 100, 30);

        BtnHapusDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapusDiagnosa.setMnemonic('H');
        BtnHapusDiagnosa.setText("Hapus");
        BtnHapusDiagnosa.setToolTipText("Alt+H");
        BtnHapusDiagnosa.setAlignmentY(0.0F);
        BtnHapusDiagnosa.setEnabled(false);
        BtnHapusDiagnosa.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnHapusDiagnosa.setMaximumSize(new java.awt.Dimension(76, 26));
        BtnHapusDiagnosa.setMinimumSize(new java.awt.Dimension(76, 26));
        BtnHapusDiagnosa.setName("BtnHapusDiagnosa"); // NOI18N
        BtnHapusDiagnosa.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapusDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusDiagnosaActionPerformed(evt);
            }
        });
        panelBiasa1.add(BtnHapusDiagnosa);
        BtnHapusDiagnosa.setBounds(530, 750, 100, 30);

        jLabel32.setText("Riwayat : ");
        jLabel32.setName("jLabel32"); // NOI18N
        jLabel32.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel32);
        jLabel32.setBounds(0, 250, 120, 14);

        btnRiwayatPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnRiwayatPasien.setMnemonic('1');
        btnRiwayatPasien.setText("Lihat");
        btnRiwayatPasien.setToolTipText("ALt+1");
        btnRiwayatPasien.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnRiwayatPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRiwayatPasien.setName("btnRiwayatPasien"); // NOI18N
        btnRiwayatPasien.setPreferredSize(new java.awt.Dimension(100, 14));
        btnRiwayatPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPasienActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnRiwayatPasien);
        btnRiwayatPasien.setBounds(120, 250, 100, 14);

        panelDiagnosaSmc.setName("panelDiagnosaSmc"); // NOI18N
        panelDiagnosaSmc.setPreferredSize(new java.awt.Dimension(800, 432));
        panelBiasa1.add(panelDiagnosaSmc);
        panelDiagnosaSmc.setBounds(0, 310, 800, 432);

        jLabel33.setText("Hasil Klaim INACBG : ");
        jLabel33.setName("jLabel33"); // NOI18N
        jLabel33.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel33);
        jLabel33.setBounds(0, 90, 120, 14);

        btnPDFKlaimINACBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPDFKlaimINACBG.setMnemonic('1');
        btnPDFKlaimINACBG.setText("Lihat");
        btnPDFKlaimINACBG.setToolTipText("ALt+1");
        btnPDFKlaimINACBG.setEnabled(false);
        btnPDFKlaimINACBG.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnPDFKlaimINACBG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPDFKlaimINACBG.setName("btnPDFKlaimINACBG"); // NOI18N
        btnPDFKlaimINACBG.setPreferredSize(new java.awt.Dimension(100, 14));
        btnPDFKlaimINACBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFKlaimINACBGActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnPDFKlaimINACBG);
        btnPDFKlaimINACBG.setBounds(120, 90, 100, 14);

        jLabel34.setText("Triase IGD : ");
        jLabel34.setName("jLabel34"); // NOI18N
        jLabel34.setPreferredSize(new java.awt.Dimension(120, 14));
        panelBiasa1.add(jLabel34);
        jLabel34.setBounds(0, 150, 120, 14);

        btnTriaseIGD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnTriaseIGD.setMnemonic('1');
        btnTriaseIGD.setText("Lihat");
        btnTriaseIGD.setToolTipText("ALt+1");
        btnTriaseIGD.setEnabled(false);
        btnTriaseIGD.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnTriaseIGD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTriaseIGD.setName("btnTriaseIGD"); // NOI18N
        btnTriaseIGD.setPreferredSize(new java.awt.Dimension(100, 14));
        btnTriaseIGD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTriaseIGDActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnTriaseIGD);
        btnTriaseIGD.setBounds(120, 150, 100, 14);

        scrollPane1.setViewportView(panelBiasa1);

        jPanel2.add(scrollPane1, java.awt.BorderLayout.CENTER);

        panelBiasa2.setName("panelBiasa2"); // NOI18N
        panelBiasa2.setLayout(new java.awt.GridLayout(1, 0));

        BtnValidasiQR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2.png"))); // NOI18N
        BtnValidasiQR.setMnemonic('T');
        BtnValidasiQR.setText("Kompilasi");
        BtnValidasiQR.setToolTipText("Alt+T");
        BtnValidasiQR.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnValidasiQR.setMaximumSize(new java.awt.Dimension(100, 30));
        BtnValidasiQR.setMinimumSize(new java.awt.Dimension(100, 30));
        BtnValidasiQR.setName("BtnValidasiQR"); // NOI18N
        BtnValidasiQR.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnValidasiQR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnValidasiQRActionPerformed(evt);
            }
        });
        panelBiasa2.add(BtnValidasiQR);

        jPanel2.add(panelBiasa2, java.awt.BorderLayout.PAGE_END);

        jPanel1.add(jPanel2);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "Detail billing Pembayaran", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(350, 102));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        tabPane1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        tabPane1.setName("tabPane1"); // NOI18N

        panelInvoices.setName("panelInvoices"); // NOI18N
        panelInvoices.setPreferredSize(new java.awt.Dimension(55, 100));
        panelInvoices.setLayout(new java.awt.BorderLayout());
        tabPane1.addTab("Billing", panelInvoices);
        panelInvoices.getAccessibleContext().setAccessibleName("");

        PanelContentINACBG.setName("PanelContentINACBG"); // NOI18N
        PanelContentINACBG.setPreferredSize(new java.awt.Dimension(55, 55));
        PanelContentINACBG.setLayout(new java.awt.BorderLayout());
        tabPane1.addTab("Klaim INACBG", PanelContentINACBG);

        jPanel5.add(tabPane1);

        jPanel1.add(jPanel5);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.CENTER);

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
        KdPj.setText("BPJ");
        NamaPj.setText("BPJS KESEHATAN");
        emptTeks();
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void tbKompilasiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbKompilasiMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (tabMode.getRowCount() != 0) {
                try {
                    getData();
                    tabPane1.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbKompilasiMouseClicked

    private void tbKompilasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbKompilasiKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                    tabPane1.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbKompilasiKeyPressed

    private void BtnValidasiQRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnValidasiQRActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            gabung();
            getData();
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnValidasiQRActionPerformed

    private void btnSEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSEPActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("norawat", lblNoRawat.getText());
            param.put("prb", Sequel.cariIsiSmc("select bpjs_prb.prb from bpjs_prb where bpjs_prb.no_sep=?", lblNoSEP.getText()));
            param.put("noreg", Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat=?", lblNoRawat.getText()));
            param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
            param.put("parameter", lblNoSEP.getText());
            if (lblStatusRawat.getText().contains("Ranap")) {
                Valid.MyReport("rptBridgingSEP5.jasper", "report", "::[ Cetak SEP ]::", param);
            } else {
                Valid.MyReport("rptBridgingSEP6.jasper", "report", "::[ Cetak SEP ]::", param);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSEPActionPerformed

    private void btnResumeRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResumeRanapActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
                "select concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar "
                + "join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? and kamar_inap.tgl_keluar = ? and kamar_inap.jam_keluar = ?",
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
            Valid.reportSmc("rptLaporanResumeRanap.jasper", "report", "::[ Laporan Resume Pasien ]::", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnResumeRanapActionPerformed

    private void btnAwalMedisIGDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAwalMedisIGDActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnAwalMedisIGDActionPerformed

    private void btnHasilLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHasilLabActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String kamar = "", namaKamar = "";
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
                        param.put("lahir", rs.getString("tgl_lahir"));
                        param.put("alamat", rs.getString("alamat"));
                        param.put("diagnosa", tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 9).toString());
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
                "select periksa_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan, periksa_lab.kategori, periksa_lab.tgl_periksa, periksa_lab.jam, periksa_lab.nip, " +
                "periksa_lab.dokter_perujuk, periksa_lab.status, periksa_lab.kd_dokter, periksa_lab.biaya, petugas.nama, perujuk.nm_dokter as nm_perujuk, dokter.nm_dokter " +
                "from periksa_lab join jns_perawatan_lab on periksa_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw join petugas on periksa_lab.nip = petugas.nip " +
                "join dokter on periksa_lab.kd_dokter = dokter.kd_dokter join dokter perujuk on periksa_lab.dokter_perujuk = perujuk.kd_dokter where periksa_lab.no_rawat = ?"
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
                            namaKamar = tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 8).toString();
                            kamar = "Kamar";
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
                        if (rs.getString("kategori").equals("PK")) {
                            Sequel.temporaryLab(String.valueOf(++i), rs.getString("nm_perawatan"));
                            try (PreparedStatement ps2 = koneksi.prepareStatement(
                                "select template_laboratorium.Pemeriksaan, detail_periksa_lab.nilai, template_laboratorium.satuan, detail_periksa_lab.nilai_rujukan, detail_periksa_lab.biaya_item, " +
                                "detail_periksa_lab.keterangan, detail_periksa_lab.kd_jenis_prw from detail_periksa_lab join template_laboratorium on detail_periksa_lab.id_template = template_laboratorium.id_template " +
                                "where detail_periksa_lab.no_rawat = ? and detail_periksa_lab.kd_jenis_prw = ? and detail_periksa_lab.tgl_periksa = ? and detail_periksa_lab.jam = ? order by template_laboratorium.urut"
                            )) {
                                ps2.setString(1, lblNoRawat.getText());
                                ps2.setString(2, rs.getString("kd_jenis_prw"));
                                ps2.setString(3, rs.getString("tgl_periksa"));
                                ps2.setString(4, rs.getString("jam"));
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    while (rs2.next()) {
                                        Sequel.temporaryLab(String.valueOf(++i), "  " + rs2.getString("Pemeriksaan"), rs2.getString("nilai"),
                                            rs2.getString("satuan"), rs2.getString("nilai_rujukan"), rs2.getString("keterangan"));
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
                                        Valid.reportSmc("rptPeriksaLab4Permintaan.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                    } else {
                                        Valid.reportSmc("rptPeriksaLab4.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
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
                                    if (rs2.next()) {
                                        param.put("nopermintaan", rs2.getString("noorder"));
                                        param.put("tanggalpermintaan", rs2.getString("tgl_permintaan"));
                                        param.put("jampermintaan", rs2.getString("jam_permintaan"));
                                        Valid.reportSmc("rptPeriksaLabPermintaanPA.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                    } else {
                                        Valid.reportSmc("rptPeriksaLabPA.jasper", "report", "::[ Pemeriksaan Laboratorium ]::", param);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mencari hasil pemeriksaan lab!");
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnHasilLabActionPerformed

    private void btnHasilRadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHasilRadActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select pasien.jk, date_format(pasien.tgl_lahir, '%d-%m-%Y') as tgllahir, concat(reg_periksa.umurdaftar, ' ', reg_periksa.sttsumur) as umur, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, periksa_radiologi.dokter_perujuk, "
                + "dokter_perujuk.nm_dokter nm_dokter_perujuk, periksa_radiologi.tgl_periksa, periksa_radiologi.jam, dokter.nm_dokter, periksa_radiologi.nip, petugas.nama nama_petugas, jns_perawatan_radiologi.nm_perawatan, "
                + "periksa_radiologi.status, periksa_radiologi.proyeksi, periksa_radiologi.kV, periksa_radiologi.mAS, periksa_radiologi.FFD, periksa_radiologi.BSF, periksa_radiologi.inak, periksa_radiologi.jml_penyinaran, periksa_radiologi.dosis "
                + "from periksa_radiologi join reg_periksa on periksa_radiologi.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join dokter dokter_perujuk on periksa_radiologi.dokter_perujuk = dokter_perujuk.kd_dokter "
                + "join dokter on periksa_radiologi.kd_dokter = dokter.kd_dokter join petugas on periksa_radiologi.nip = petugas.nip join jns_perawatan_radiologi on periksa_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw "
                + "left join kelurahan on pasien.kd_kel = kelurahan.kd_kel left join kecamatan on pasien.kd_kec = kecamatan.kd_kec left join kabupaten on pasien.kd_kab = kabupaten.kd_kab where periksa_radiologi.no_rawat = ?"
            )) {
                ps.setString(1, lblNoRawat.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String pemeriksaan = rs.getString("nm_perawatan")
                            + " dengan Proyeksi : " + rs.getString("proyeksi")
                            + ", kV : " + rs.getString("kV")
                            + ", mAS : " + rs.getString("mAS")
                            + ", FFD : " + rs.getString("FFD")
                            + ", BSF : " + rs.getString("BSF")
                            + ", Inak : " + rs.getString("Inak")
                            + ", Jumlah penyinaran : " + rs.getString("jml_penyinaran")
                            + ", Dosis Radiasi : " + rs.getString("dosis");
                        Map<String, Object> param = new HashMap<>();
                        param.put("noperiksa", lblNoRawat.getText());
                        param.put("norm", lblNoRM.getText());
                        param.put("namapasien", lblNamaPasien.getText());
                        param.put("jkel", rs.getString("jk"));
                        param.put("umur", rs.getString("umur"));
                        param.put("lahir", rs.getString("tgllahir"));
                        param.put("pengirim", rs.getString("nm_dokter"));
                        param.put("tanggal", rs.getString("tgl_periksa"));
                        param.put("penjab", rs.getString("nm_dokter_perujuk"));
                        param.put("petugas", rs.getString("nama_petugas"));
                        param.put("alamat", rs.getString("alamat"));
                        String kamar = "", kelas = "", namaKamar = "", noRawatIbu = "";
                        if (lblStatusRawat.getText().contains("Ranap")) {
                            noRawatIbu = Sequel.cariIsiSmc("select no_rawat from ranap_gabung where no_rawat2 = ?", lblNoRawat.getText());
                            if (!noRawatIbu.isBlank()) {
                                kamar = Sequel.cariIsiSmc("select ifnull(kd_kamar, '') from kamar_inap where no_rawat = ? order by tgl_masuk desc limit 1", noRawatIbu);
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
                        param.put("pemeriksaan", pemeriksaan);
                        param.put("jam", rs.getString("jam"));
                        param.put("namars", akses.getnamars());
                        param.put("alamatrs", akses.getalamatrs());
                        param.put("kotars", akses.getkabupatenrs());
                        param.put("propinsirs", akses.getpropinsirs());
                        param.put("kontakrs", akses.getkontakrs());
                        param.put("emailrs", akses.getemailrs());
                        param.put("hasil", Sequel.cariIsiSmc("select hasil from hasil_radiologi where no_rawat = ? and tgl_periksa = ? and jam = ?", lblNoRawat.getText(), rs.getString("tgl_periksa"), rs.getString("jam")));
                        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("dokter_perujuk"));
                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter_perujuk") + "\nID " + (finger.isBlank() ? rs.getString("dokter_perujuk") : finger) + "\n" + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("tgl_periksa")));
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", rs.getString("nip"));
                        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nama_petugas") + "\nID " + (finger.isBlank() ? rs.getString("nip") : finger) + "\n" + new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("tgl_periksa")));
                        Valid.reportSmc("rptPeriksaRadiologi.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mencari hasil pemeriksaan Radiologi!");
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnHasilRadActionPerformed

    private void btnSurkonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSurkonActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
            String noSurat = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText());
            String tglSurat = Sequel.cariIsiSmc("select date_format(tgl_surat, '%d-%m-%Y') from bridging_surat_kontrol_bpjs where no_surat = ?", noSurat);
            String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs = (select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat = ?)", noSurat);
            String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
            param.put("parameter", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText()));
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + kodeDokter + "\n" + tglSurat);
            Valid.reportSmc(
                "rptBridgingSuratKontrol2.jasper", "report", "::[ Data Surat Kontrol VClaim ]::", param,
                "select bridging_sep.no_rawat, bridging_sep.no_sep, bridging_sep.no_kartu, bridging_sep.nomr, bridging_sep.nama_pasien, bridging_sep.tanggal_lahir, bridging_sep.jkel, bridging_sep.diagawal, bridging_sep.nmdiagnosaawal, bridging_surat_kontrol_bpjs.tgl_surat, "
                + "bridging_surat_kontrol_bpjs.no_surat, bridging_surat_kontrol_bpjs.tgl_rencana, bridging_surat_kontrol_bpjs.kd_dokter_bpjs, bridging_surat_kontrol_bpjs.nm_dokter_bpjs, bridging_surat_kontrol_bpjs.kd_poli_bpjs, bridging_surat_kontrol_bpjs.nm_poli_bpjs "
                + "from bridging_sep join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText())
            );
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSurkonActionPerformed

    private void btnSPRIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSPRIActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih data pasien terlebih dahulu!");
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
                "select bridging_surat_pri_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk "
                + "from reg_periksa join bridging_surat_pri_bpjs on bridging_surat_pri_bpjs.no_rawat = reg_periksa.no_rawat "
                + "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where bridging_surat_pri_bpjs.no_surat = ?", noSPRI
            );
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSPRIActionPerformed

    private void BtnSimpanDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanDiagnosaActionPerformed
        if (!lblNoRawat.getText().isBlank()) {
            panelDiagnosaSmc.simpan();
            tampilINACBG();
        }
    }//GEN-LAST:event_BtnSimpanDiagnosaActionPerformed

    private void BtnHapusDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusDiagnosaActionPerformed
        if (!lblNoRawat.getText().isBlank()) {
            panelDiagnosaSmc.hapus();
            tampilINACBG();
        }
    }//GEN-LAST:event_BtnHapusDiagnosaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        revalidate();
        Dimension newD = new Dimension(jPanel2.getWidth() - 32, panelBiasa1.getPreferredSize().height);
        panelDiagnosaSmc.setPreferredSize(new Dimension(newD.width, panelDiagnosaSmc.getPreferredSize().height));
        panelDiagnosaSmc.setSize(new Dimension(newD.width, panelDiagnosaSmc.getPreferredSize().height));
        panelDiagnosaSmc.revalidate(newD.width);
        panelBiasa1.setPreferredSize(newD);
        panelBiasa1.setSize(newD);
        scrollPane1.setPreferredSize(newD);
        scrollPane1.setSize(newD);
        BtnHapusDiagnosa.setLocation(panelBiasa1.getWidth() - BtnHapusDiagnosa.getWidth() - 4, BtnHapusDiagnosa.getY());
        revalidate();
    }//GEN-LAST:event_formWindowOpened

    private void ppUpdateTanggalPulangSEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppUpdateTanggalPulangSEPActionPerformed
        if (tbKompilasi.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data SEP ranap pasien terlebih dahulu!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try (PreparedStatement pspulang = koneksi.prepareStatement(
                "select bridging_sep.no_rawat, bridging_sep.no_sep, pasien.no_rkm_medis, pasien.nm_pasien, if (bridging_sep.tglpulang is null or bridging_sep.tglpulang = '0000-00-00 00:00:00', " +
                "(select if (max(concat(kamar_inap.tgl_keluar, ' ',kamar_inap.jam_keluar)) = '0000-00-00 00:00:00' or max(concat(kamar_inap.tgl_keluar, ' ', kamar_inap.jam_keluar)) is null, now(), max(concat(kamar_inap.tgl_keluar, ' ', kamar_inap.jam_keluar))) " +
                "from kamar_inap where kamar_inap.no_rawat = bridging_sep.no_rawat), bridging_sep.tglpulang) as tglpulang from bridging_sep join reg_periksa on bridging_sep.no_rawat = reg_periksa.no_rawat " +
                "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where bridging_sep.jnspelayanan = '1' and bridging_sep.no_sep = ? order by bridging_sep.tglsep desc limit 1"
            )) {
                pspulang.setString(1, tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 1).toString());
                try (ResultSet rspulang = pspulang.executeQuery()) {
                    if (rspulang.next()) {
                        WindowUpdatePulang.setSize(608, 264);
                        WindowUpdatePulang.setLocationRelativeTo(internalFrame1);
                        TNoRwPulang.setText(rspulang.getString("no_rawat"));
                        TNoSEPRanapPulang.setText(rspulang.getString("no_sep"));
                        TNoRMPulang.setText(rspulang.getString("no_rkm_medis"));
                        TPasienPulang.setText(rspulang.getString("nm_pasien"));
                        TanggalPulang.setSelectedItem(
                            rspulang.getString("tglpulang").substring(8, 10) + "-"
                            + rspulang.getString("tglpulang").substring(5, 7) + "-"
                            + rspulang.getString("tglpulang").substring(0, 4) + " "
                            + rspulang.getString("tglpulang").substring(11, 19)
                        );
                        WindowUpdatePulang.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Pasien tersebut belum terbit SEP, silahkan hubungi bagian terkait..!!");
                        TCari.requestFocus();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_ppUpdateTanggalPulangSEPActionPerformed

    private void BtnCloseIn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseIn8ActionPerformed
        WindowUpdatePulang.dispose();
    }//GEN-LAST:event_BtnCloseIn8ActionPerformed

    private void BtnSimpan8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan8ActionPerformed
        if (TNoRwPulang.getText().isBlank() || TNoSEPRanapPulang.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data pasiennya terlebih dahulu..!!");
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
            String json = "{"
            + "\"request\": {"
            + "\"t_sep\": {"
            + "\"noSep\": \"" + TNoSEPRanapPulang.getText() + "\","
            + "\"statusPulang\": \"" + StatusPulang.getSelectedItem().toString().substring(0, 1) + "\","
            + "\"noSuratMeninggal\": \"" + NoSuratKematian.getText().trim() + "\","
            + "\"tglMeninggal\": \"" + (NoSuratKematian.getText().trim().isBlank() ? "" : Valid.SetTgl(TanggalKematian.getSelectedItem().toString())) + "\","
            + "\"tglPulang\": \"" + Valid.SetTgl(TanggalPulang.getSelectedItem().toString()) + "\","
            + "\"noLPManual\": \"" + NoLPManual.getText().trim() + "\","
            + "\"user\": \"" + akses.getkode().trim().substring(0, 9) + "\""
            + "}"
            + "}"
            + "}";
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
                JOptionPane.showMessageDialog(rootPane, "Proses update pulang di BPJS selesai!");
                WindowUpdatePulang.dispose();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus..!!");
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

    private void ppBerkasDigitalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBerkasDigitalActionPerformed
        if (tbKompilasi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbKompilasi.getSelectedRow() > -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DlgBerkasRawat berkas = new DlgBerkasRawat(null, true);
                berkas.setJudul("::[ Berkas Digital Perawatan ]::", "berkasrawat/pages");
                try {
                    if (akses.gethapus_berkas_digital_perawatan() == true) {
                        berkas.loadURL("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + "berkasrawat/login2.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + lblNoRawat.getText());
                    } else {
                        berkas.loadURL("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + "berkasrawat/login2nonhapus.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + lblNoRawat.getText());
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi : " + ex);
                }
                berkas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                berkas.setLocationRelativeTo(internalFrame1);
                berkas.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }//GEN-LAST:event_ppBerkasDigitalActionPerformed

    private void ppRiwayatPerawatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppRiwayatPerawatanActionPerformed
        if (tbKompilasi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data pasien sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbKompilasi.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu data kamar inap pada table...!!!");
            TCari.requestFocus();
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            resume.setNoRMKompilasi(tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 0).toString(), tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 2).toString());
            resume.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            resume.setLocationRelativeTo(internalFrame1);
            resume.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_ppRiwayatPerawatanActionPerformed

    private void btnRiwayatPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPasienActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        resume.setNoRMKompilasi(lblNoRawat.getText(), lblNoRM.getText());
        resume.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        resume.setLocationRelativeTo(internalFrame1);
        resume.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnRiwayatPasienActionPerformed

    private void btnInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvoiceActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu");
        } else {
            Valid.panggilUrl("berkasrawat/loginlihatbilling.php?act=login&norawat=" + lblNoRawat.getText() + "&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB());
        }
    }//GEN-LAST:event_btnInvoiceActionPerformed

    private void btnPDFKlaimINACBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFKlaimINACBGActionPerformed
        if (lblNoSEP.getText().isBlank()) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih pasien terlebih dahulu!");
        } else {
            String file = "inacbg/" + Sequel.cariIsiSmc("select path from inacbg_cetak_klaim where no_sep = ?", lblNoSEP.getText());
            file = file + "?hash=" + DigestUtils.sha256Hex(lblNoSEP.getText() + Instant.now().toString());
            Valid.panggilUrl(file);
        }
    }//GEN-LAST:event_btnPDFKlaimINACBGActionPerformed

    private void btnTriaseIGDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTriaseIGDActionPerformed
        if (lblNoRawat.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih pasien terlebih dahulu!");
            return;
        }
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
    }//GEN-LAST:event_btnTriaseIGDActionPerformed

    private void BtnPenjaminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPenjaminActionPerformed
        penjab.isCek();
        penjab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        penjab.setLocationRelativeTo(internalFrame1);
        penjab.setAlwaysOnTop(false);
        penjab.setVisible(true);
    }//GEN-LAST:event_BtnPenjaminActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            BPJSKompilasiBerkasKlaim dialog = new BPJSKompilasiBerkasKlaim(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnCloseIn8;
    private widget.Button BtnHapusDiagnosa;
    private widget.Button BtnKeluar;
    private widget.Button BtnPenjamin;
    private widget.Button BtnSimpan8;
    private widget.Button BtnSimpanDiagnosa;
    private widget.Button BtnValidasiQR;
    private widget.ComboBox CmbStatusKirim;
    private widget.ComboBox CmbStatusRawat;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox KdPj;
    private widget.Label LCount;
    private widget.TextBox NamaPj;
    private widget.TextBox NoLPManual;
    private widget.TextBox NoSuratKematian;
    private widget.panelisi PanelContentINACBG;
    private widget.ScrollPane Scroll;
    private widget.ComboBox StatusPulang;
    private widget.TextBox TCari;
    private widget.TextBox TNoRMPulang;
    private widget.TextBox TNoRwPulang;
    private widget.TextBox TNoSEPRanapPulang;
    private widget.TextBox TPasienPulang;
    private widget.Tanggal TanggalKematian;
    private widget.Tanggal TanggalPulang;
    private javax.swing.JDialog WindowUpdatePulang;
    private widget.Button btnAwalMedisIGD;
    private widget.Button btnHasilLab;
    private widget.Button btnHasilRad;
    private widget.Button btnInvoice;
    private widget.Button btnPDFKlaimINACBG;
    private widget.Button btnResumeRanap;
    private widget.Button btnRiwayatPasien;
    private widget.Button btnSEP;
    private widget.Button btnSPRI;
    private widget.Button btnSurkon;
    private widget.Button btnTriaseIGD;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame11;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
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
    private widget.Label jLabel41;
    private widget.Label jLabel44;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel48;
    private widget.Label jLabel49;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label label19;
    private widget.Label lblCoderNIK;
    private widget.Label lblNamaPasien;
    private widget.Label lblNoRM;
    private widget.Label lblNoRawat;
    private widget.Label lblNoSEP;
    private widget.Label lblStatusRawat;
    private widget.PanelBiasa panelBiasa1;
    private widget.PanelBiasa panelBiasa2;
    private laporan.PanelDiagnosaSmc panelDiagnosaSmc;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass11;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelInvoices;
    private javax.swing.JMenuItem ppBerkasDigital;
    private javax.swing.JMenuItem ppRiwayatPerawatan;
    private javax.swing.JMenuItem ppUpdateTanggalPulangSEP;
    private widget.ScrollPane scrollPane1;
    private widget.TabPane tabPane1;
    private widget.Table tbKompilasi;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        String statusKirim = "";
        if (CmbStatusKirim.getSelectedItem().toString().equals("Terkirim")) {
            statusKirim = "and inacbg_cetak_klaim.no_sep is not null";
        } else if (CmbStatusKirim.getSelectedItem().toString().equals("Belum Terkirim")) {
            statusKirim = "and inacbg_cetak_klaim.no_sep is null";
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select reg_periksa.no_rawat, bridging_sep.no_sep, reg_periksa.no_rkm_medis, pasien.nm_pasien, reg_periksa.status_lanjut, reg_periksa.tgl_registrasi, " +
            "date(bridging_sep.tglpulang) as tglpulang, kamar_inap.stts_pulang, case when reg_periksa.status_lanjut = 'Ranap' then concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) " +
            "when reg_periksa.status_lanjut = 'Ralan' then poliklinik.nm_poli end as ruangan, dokter.nm_dokter, diagnosa_pasien.kd_penyakit, (inacbg_cetak_klaim.no_sep is not null) as inacbg_terkirim " +
            "from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli join bridging_sep " +
            "on reg_periksa.no_rawat = bridging_sep.no_rawat and (if(reg_periksa.status_lanjut = 'Ranap', '1', '2')) = bridging_sep.jnspelayanan left join maping_dokter_dpjpvclaim " +
            "on bridging_sep.kddpjp = maping_dokter_dpjpvclaim.kd_dokter_bpjs left join dokter on maping_dokter_dpjpvclaim.kd_dokter = dokter.kd_dokter left join diagnosa_pasien " +
            "on reg_periksa.no_rawat = diagnosa_pasien.no_rawat and reg_periksa.status_lanjut = diagnosa_pasien.status and diagnosa_pasien.prioritas = '1' left join inacbg_cetak_klaim " +
            "on bridging_sep.no_sep = inacbg_cetak_klaim.no_sep left join kamar_inap on reg_periksa.no_rawat = kamar_inap.no_rawat and kamar_inap.stts_pulang != 'Pindah Kamar' " +
            "left join kamar on kamar_inap.kd_kamar = kamar.kd_kamar left join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where reg_periksa.status_bayar = 'Sudah Bayar' " +
            "and reg_periksa.tgl_registrasi between ? and ? and reg_periksa.status_lanjut like ? and reg_periksa.kd_pj like ? and bridging_sep.no_sep like ? " +
            "and length(bridging_sep.no_sep) = 19 and ( reg_periksa.no_rawat like ? or bridging_sep.no_sep like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? " +
            "or poliklinik.nm_poli like ? or concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) like ? or dokter.nm_dokter like ?) " + statusKirim + " group by reg_periksa.no_rawat, bridging_sep.no_sep, " +
            "reg_periksa.no_rkm_medis, reg_periksa.status_lanjut order by reg_periksa.no_rawat"
        )) {
            ps.setString(1, Valid.getTglSmc(DTPCari1));
            ps.setString(2, Valid.getTglSmc(DTPCari2));
            if (CmbStatusRawat.getSelectedIndex() == 0) {
                ps.setString(3, "%");
            } else {
                ps.setString(3, CmbStatusRawat.getSelectedItem().toString());
            }
            if (! KdPj.getText().isBlank()) {
                ps.setString(4, KdPj.getText());
            } else {
                ps.setString(4, "%");
            }
            ps.setString(5, Sequel.cariIsiSmc("select kode_ppk from setting limit 1") + "%");
            ps.setString(6, "%" + TCari.getText() + "%");
            ps.setString(7, "%" + TCari.getText() + "%");
            ps.setString(8, "%" + TCari.getText() + "%");
            ps.setString(9, "%" + TCari.getText() + "%");
            ps.setString(10, "%" + TCari.getText() + "%");
            ps.setString(11, "%" + TCari.getText() + "%");
            ps.setString(12, "%" + TCari.getText() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[] {
                        rs.getString("no_rawat"),
                        rs.getString("no_sep"),
                        rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"),
                        rs.getString("status_lanjut"),
                        rs.getString("tgl_registrasi"),
                        rs.getString("tglpulang"),
                        rs.getString("stts_pulang"),
                        rs.getString("ruangan"),
                        rs.getString("nm_dokter"),
                        rs.getString("kd_penyakit"),
                        (rs.getBoolean("inacbg_terkirim") ? "Terkirim" : "Belum Terkirim"),
                        rs.getString("inacbg_terkirim")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }

    public void emptTeks() {
        TCari.setText("");
        lblNamaPasien.setText("");
        lblNoRawat.setText("");
        lblNoRM.setText("");
        lblStatusRawat.setText("");
        lblNoSEP.setText("");
        btnSEP.setText("Tidak Ada");
        btnSEP.setEnabled(false);
        btnResumeRanap.setText("Tidak Ada");
        btnResumeRanap.setEnabled(false);
        btnInvoice.setText("Tidak Ada");
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
        btnPDFKlaimINACBG.setText("Tidak Ada");
        btnPDFKlaimINACBG.setEnabled(false);
        tbKompilasi.clearSelection();
    }

    private void getData() {
        if (tbKompilasi.getSelectedRow() != -1) {
            lblNamaPasien.setText(tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 3).toString());
            lblNoRawat.setText(tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 0).toString());
            lblNoRM.setText(tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 2).toString());
            lblStatusRawat.setText(tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 4).toString());
            lblNoSEP.setText(tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 1).toString());
            String noSuratKontrol = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText());
            if (noSuratKontrol.isBlank()) {
                noSuratKontrol = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_rawat = ? and noskdp != ''", lblNoRawat.getText());
            }
            btnSEP.setText("Ada");
            btnSEP.setEnabled(true);
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select exists(select * from data_triase_igd where data_triase_igd.no_rawat = bridging_sep.no_rawat) as ada_triase, " +
                "exists(select * from resume_pasien_ranap where resume_pasien_ranap.no_rawat = bridging_sep.no_rawat) as ada_resume_ranap, " +
                "exists(select * from inacbg_cetak_klaim where inacbg_cetak_klaim.no_sep = bridging_sep.no_sep) as ada_cetak_klaim, " +
                "exists(select * from penilaian_medis_igd where penilaian_medis_igd.no_rawat = bridging_sep.no_rawat) as ada_awal_medis_igd, " +
                "exists(select * from periksa_lab where periksa_lab.no_rawat = bridging_sep.no_rawat) as ada_periksa_lab, " +
                "exists(select * from periksa_radiologi where periksa_radiologi.no_rawat = bridging_sep.no_rawat) as ada_periksa_rad, " +
                "exists(select * from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat = ?) as ada_skdp, " +
                "exists(select * from bridging_surat_pri_bpjs where bridging_surat_pri_bpjs.no_rawat = bridging_sep.no_rawat) as ada_spri, " +
                "exists(select * from billing where billing.no_rawat = bridging_sep.no_rawat) as ada_billing from bridging_sep where bridging_sep.no_sep = ?"
            )) {
                ps.setString(1, noSuratKontrol);
                ps.setString(2, lblNoSEP.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getBoolean("ada_triase")) {
                            btnTriaseIGD.setText("Ada");
                            btnTriaseIGD.setEnabled(true);
                        } else {
                            btnTriaseIGD.setText("Tidak Ada");
                            btnTriaseIGD.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_resume_ranap")) {
                            btnResumeRanap.setText("Ada");
                            btnResumeRanap.setEnabled(true);
                        } else {
                            btnResumeRanap.setText("Tidak Ada");
                            btnResumeRanap.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_billing")) {
                            btnInvoice.setText("Ada");
                            btnInvoice.setEnabled(true);
                        } else {
                            btnInvoice.setText("Tidak Ada");
                            btnInvoice.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_awal_medis_igd")) {
                            btnAwalMedisIGD.setText("Ada");
                            btnAwalMedisIGD.setEnabled(true);
                        } else {
                            btnAwalMedisIGD.setText("Tidak Ada");
                            btnAwalMedisIGD.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_periksa_lab")) {
                            btnHasilLab.setText("Ada");
                            btnHasilLab.setEnabled(true);
                        } else {
                            btnHasilLab.setText("Tidak Ada");
                            btnHasilLab.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_periksa_rad")) {
                            btnHasilRad.setText("Ada");
                            btnHasilRad.setEnabled(true);
                        } else {
                            btnHasilRad.setText("Tidak Ada");
                            btnHasilRad.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_skdp")) {
                            btnSurkon.setText("Ada");
                            btnSurkon.setEnabled(true);
                        } else {
                            btnSurkon.setText("Tidak Ada");
                            btnSurkon.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_spri")) {
                            btnSPRI.setText("Ada");
                            btnSPRI.setEnabled(true);
                        } else {
                            btnSPRI.setText("Tidak Ada");
                            btnSPRI.setEnabled(false);
                        }
                        if (rs.getBoolean("ada_cetak_klaim")) {
                            btnPDFKlaimINACBG.setText("Ada");
                            btnPDFKlaimINACBG.setEnabled(true);
                        } else {
                            btnPDFKlaimINACBG.setText("Tidak Ada");
                            btnPDFKlaimINACBG.setEnabled(false);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                btnTriaseIGD.setText("Tidak Ada");
                btnTriaseIGD.setEnabled(false);
                btnResumeRanap.setText("Tidak Ada");
                btnResumeRanap.setEnabled(false);
                btnInvoice.setText("Tidak Ada");
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
                btnPDFKlaimINACBG.setText("Tidak Ada");
                btnPDFKlaimINACBG.setEnabled(false);
            }
            panelDiagnosaSmc.setRM(lblNoRawat.getText(), lblNoRM.getText(), Valid.getTglSmc(DTPCari1), Valid.getTglSmc(DTPCari2), lblStatusRawat.getText());
            panelDiagnosaSmc.batal();
            panelDiagnosaSmc.pilihTab(0);
            tampilINACBG();
            tampilBilling();
        }
    }

    public void isCek() {
        lblCoderNIK.setText(Sequel.cariIsiSmc("select no_ik from inacbg_coder_nik where nik = ?", akses.getkode()));
        KdPj.setText("BPJ");
        NamaPj.setText("BPJS KESEHATAN");
        tampil();
    }

    public void isCek(String nik) {
        lblCoderNIK.setText(nik);
        KdPj.setText("BPJ");
        NamaPj.setText("BPJS KESEHATAN");
        tampil();
    }

    public void tampilINACBG() {
        String corona = "BukanCorona";
        String aksi = "";
        
        if (Sequel.cariBooleanSmc("select * from perawatan_corona where perawatan_corona.no_rawat = ?", lblNoRawat.getText())) {
            corona = "PasienCorona";
        }
        
        if (btnPDFKlaimINACBG.isEnabled()) {
            aksi = "&sukses=true&action=selesai";
        }
        
        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() 
            + "/inacbg/login.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB()
            + "&page=DetailKirimSmc&nosep=" + lblNoSEP.getText() + "&codernik=" + lblCoderNIK.getText() + "&corona=" + corona + aksi;
        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();
            engine.setJavaScriptEnabled(true);
            engine.setCreatePopupHandler((PopupFeatures p) -> view.getEngine());
            engine.getLoadWorker().exceptionProperty().addListener((ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) -> {
                if (engine.getLoadWorker().getState() == FAILED) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(PanelContentINACBG,
                            engine.getLocation() + "\n" + (value != null ? value.getMessage() : "Unexpected error!"),
                            "Loading Catatan...", JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            
            engine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    try {
                        if (engine.getLocation().replaceAll(url, "").contains("action=selesai")) {
                            getData();
                        }
                    } catch (Exception ex) {
                        System.out.println("Notifikasi : " + ex);
                    }
                }
            });
            
            jfxPanelicare.setScene(new Scene(view));
            try {
                engine.load(url);
            } catch (Exception exception) {
                engine.load(url);
            }
        });
        PanelContentINACBG.add(jfxPanelicare, BorderLayout.CENTER);
    }

    public void tampilBilling() {
        String norawat = lblNoRawat.getText();
        try {
            norawat = URLEncoder.encode(norawat, "UTF-8");
        } catch (Exception e) {
            norawat = lblNoRawat.getText();
        }

        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() 
            + "/berkasrawat/loginlihatbilling.php?act=login&norawat=" + norawat + "&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB();
        
        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();
            engine.setJavaScriptEnabled(true);
            engine.setCreatePopupHandler((PopupFeatures p) -> view.getEngine());
            engine.getLoadWorker().exceptionProperty()
                .addListener((ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) -> {
                    if (engine.getLoadWorker().getState() == FAILED) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(panelInvoices,
                                engine.getLocation() + "\n" + (value != null ? value.getMessage() : "Unexpected error!"),
                                "Loading Catatan...", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                });
            jfxinvoices.setScene(new Scene(view));
            try {
                engine.load(url);
            } catch (Exception exception) {
                engine.load(url);
            }
        });
        panelInvoices.add(jfxinvoices, BorderLayout.CENTER);
    }

    private void exportPDF(String reportName, String savedFileName, Map reportParams) {
        try {
            File dir = new File("./berkaspdf/" + tanggalExport);
            if (!dir.isDirectory() && !dir.mkdirs()) {
                Files.createDirectory(dir.toPath());
            }
            JasperPrint jp = JasperFillManager.fillReport("./report/" + reportName, reportParams, koneksi);
            
            JasperExportManager.exportReportToPdfFile(jp, "./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + "_" + savedFileName.replaceAll(".pdf", "") + ".pdf");
        } catch (Exception e) {
            exportSukses = false;
            System.out.println("Notif : " + e);
        }
    }

    private void exportPDF(String reportName, String savedFileName, Map reportParams, String sql, String... values) {
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            File dir = new File("./berkaspdf/" + tanggalExport);
            if (!dir.isDirectory() && !dir.mkdirs()) {
                Files.createDirectory(dir.toPath());
            }
            JasperExportManager.exportReportToPdfFile(
                JasperFillManager.fillReport("./report/" + reportName, reportParams, new JRResultSetDataSource(ps.executeQuery())),
                "./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + "_" + savedFileName.replaceAll(".pdf", "") + ".pdf"
            );
        } catch (JRException e) {
            exportSukses = false;
            System.out.println("Notif : " + e);
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void exportSEP(String urutan) {
        if (lblNoSEP.getText().equals("Tidak Ada")) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("namars", akses.getnamars());
        params.put("alamatrs", akses.getalamatrs());
        params.put("kotars", akses.getkabupatenrs());
        params.put("propinsirs", akses.getpropinsirs());
        params.put("kontakrs", akses.getkontakrs());
        params.put("norawat", lblNoRawat.getText());
        params.put("prb", Sequel.cariIsiSmc("select bpjs_prb.prb from bpjs_prb where bpjs_prb.no_sep=?", lblNoSEP.getText()));
        params.put("noreg", Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat=?", lblNoRawat.getText()));
        params.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        params.put("parameter", lblNoSEP.getText());
        if (lblStatusRawat.getText().contains("Ranap")) {
            exportPDF("rptBridgingSEP5.jasper", urutan + "_SEP", params);
        } else {
            exportPDF("rptBridgingSEP6.jasper", urutan + "_SEP", params);
        }
    }
    
    private void exportKlaimINACBG(String urutan) {
        if (! btnPDFKlaimINACBG.isEnabled()) {
            return;
        }
        
        String filename = Sequel.cariIsiSmc("select path from inacbg_cetak_klaim where no_sep = ?", lblNoSEP.getText());
        if (filename.isBlank()) {
            return;
        }
        
        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/inacbg/" + filename;
        String exportPath = "./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + "_" + urutan + "_KlaimINACBG.pdf";
        if (filename.endsWith(".pdf")) {
            try (FileOutputStream os = new FileOutputStream(exportPath); FileChannel fileChannel = os.getChannel()) {
                fileChannel.transferFrom(Channels.newChannel(new URL(url).openStream()), 0, Long.MAX_VALUE);
            } catch (Exception e) {
                exportSukses = false;
                System.out.println("Notif : " + e);
            }
        }
    }

    private void exportResumeRanap(String urutan) {
        if (!btnResumeRanap.isEnabled()) {
            return;
        }
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
            "select concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar "
            + "join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? and kamar_inap.tgl_keluar = ? and kamar_inap.jam_keluar = ?",
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
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                        param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + Valid.SetTgl3(tglKeluar));
                        param.put("namadokter2", rs.getString("nm_dokter"));
                    }
                    if (i == 3) {
                        finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik = ?", rs.getString("kd_dokter"));
                        param.put("finger3", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + rs.getString("nm_dokter") + "\nID " + (finger.isBlank() ? rs.getString("kd_dokter") : finger) + "\n" + Valid.SetTgl3(tglKeluar));
                        param.put("namadokter3", rs.getString("nm_dokter"));
                    }
                    ++i;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        exportPDF("rptLaporanResumeRanap.jasper", urutan + "_ResumePasien", param);
    }

    private void exportBilling(String urutan) {
        if (! btnInvoice.isEnabled()) return;
        
        String norawat = lblNoRawat.getText();
        try {
            norawat = URLEncoder.encode(norawat, "UTF-8");
        } catch (Exception e) {
            norawat = lblNoRawat.getText();
        }
        String link = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() 
            + "/berkasrawat/loginlihatbilling.php?act=login&norawat=" + norawat + "&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB();
        try (FileOutputStream os = new FileOutputStream("./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + "_" + urutan + "_Billing.pdf")) {
            URL url = new URL(link);
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(url, 30000);
            org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withW3cDocument(w3cDoc, link);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            exportSukses = false;
            System.out.println("Notif : " + e);
            cleanupSingleFile(lblNoSEP.getText() + "_" + urutan + "_Billing.pdf");
        }
    }
    
    private void exportTriaseIGD(String urutan) {
        if (! btnTriaseIGD.isEnabled()) {
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
                                    exportPDF("rptLembarTriaseSkala1.jasper", urutan + "_TriaseSkala1", param, "select * from temporary where temp37 = ?", akses.getalamatip());
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
                                    exportPDF("rptLembarTriaseSkala2.jasper", urutan + "_TriaseSkala2", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    } else if (rs.getInt("cs3") > 0) {
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
                                                    ps3.setString(1, rs2.getString("kode_pemeriksaan"));
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
                                    exportPDF("rptLembarTriaseSkala3.jasper", urutan + "_TriaseSkala3", param, "select * from temporary where temp37 = ?", akses.getalamatip());
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
                                    exportPDF("rptLembarTriaseSkala4.jasper", urutan + "_TriaseSkala4", param, "select * from temporary where temp37 = ?", akses.getalamatip());
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
                                    exportPDF("rptLembarTriaseSkala5.jasper", urutan + "_TriaseSkala5", param, "select * from temporary where temp37 = ?", akses.getalamatip());
                                }
                            }
                        }
                    } else {
                        exportSukses = false;
                    }
                }
            }
        } catch (Exception e) {
            exportSukses = false;
            System.out.println("Notif : " + e);
        }
    }
    
    private void exportSOAP(String urutan) {
        if (Sequel.cariBooleanSmc("select * from reg_periksa where no_rawat = ? and (kd_poli = 'IGDK' or status_lanjut = 'Ranap')", lblNoRawat.getText())) {
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
                "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tmp_lahir, pasien.tgl_lahir, pasien.agama, bahasa_pasien.nama_bahasa, cacat_fisik.nama_cacat, pasien.gol_darah, " +
                "pasien.nm_ibu, pasien.stts_nikah, pasien.pnd, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, pasien.pekerjaan from pasien " +
                "join bahasa_pasien on bahasa_pasien.id = pasien.bahasa_pasien join cacat_fisik on cacat_fisik.id = pasien.cacat_fisik join kelurahan on pasien.kd_kel = kelurahan.kd_kel " +
                "join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on pasien.kd_kab = kabupaten.kd_kab where pasien.no_rkm_medis = ?"
            )) {
                ps.setString(1, lblNoRM.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        htmlContent
                            .append("<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"3px\" cellspacing=\"0\" class=\"tbl_form\">")
                            .append("<tbody>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">No.RM</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("no_rkm_medis") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Nama Pasien</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("nm_pasien") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Alamat</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("alamat") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Jenis Kelamin</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("jk").replaceAll("L", "Laki-Laki").replaceAll("P", "Perempuan") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Tempat &amp; Tanggal Lahir</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("tmp_lahir") + ", " + new SimpleDateFormat("dd MMMM yyyy", new Locale("id")).format((Date) rs.getDate("tgl_lahir")) + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Ibu Kandung</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("nm_ibu") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Golongan Darah</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("gol_darah") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Status Nikah</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("stts_nikah") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Agama</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("agama") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Pendidikan Terakhir</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("pnd") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Bahasa Dipakai</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("nama_bahasa") + "</td>")
                            .append("</tr>")
                            .append("<tr class=\"isi\">")
                            .append("<td valign=\"top\" width=\"20%\">Cacat Fisik</td>")
                            .append("<td valign=\"top\" width=\"1%\" align=\"center\">:</td>")
                            .append("<td valign=\"top\" width=\"79%\">" + rs.getString("nama_cacat") + "</td>")
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
                .append("<td valign=\"top\" align=\"center\">" + Sequel.cariIsiSmc("select tgl_registrasi from reg_periksa where no_rawat = ?", lblNoRawat.getText()) + "</td>")
                .append("<td valign=\"top\" align=\"center\">" + lblNoRawat.getText() + "</td>")
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
                ps.setString(1, lblNoRawat.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        htmlContent
                            .append("<tr class=\"isi\">")
                            .append("<td align=\"center\">" + rs.getString("tgl_perawatan") + "<br>" + rs.getString("jam_rawat") + "</td>")
                            .append("<td align=\"center\">" + rs.getString("nip") + "<br>" + rs.getString("nama") + "</td>")
                            .append("<td align=\"left\">" + HtmlUtils.htmlEscape(rs.getString("keluhan")).replaceAll("\\R", "<br>") + "</td>")
                            .append("<td align=\"left\">" + HtmlUtils.htmlEscape(rs.getString("pemeriksaan")).replaceAll("\\R", "<br>"))
                            .append((rs.getString("alergi") == null || rs.getString("alergi").isBlank() ? "" : "<br>Alergi : " + rs.getString("alergi")))
                            .append((rs.getString("suhu_tubuh") == null || rs.getString("suhu_tubuh").isBlank() ? "" : "<br>Suhu(C) : " + rs.getString("suhu_tubuh")))
                            .append((rs.getString("tensi") == null || rs.getString("tensi").isBlank() ? "" : "<br>Tensi : " + rs.getString("tensi")))
                            .append((rs.getString("nadi") == null || rs.getString("nadi").isBlank() ? "" : "<br>Nadi(/menit) : " + rs.getString("nadi")))
                            .append((rs.getString("respirasi") == null || rs.getString("respirasi").isBlank() ? "" : "<br>Respirasi(/menit) : " + rs.getString("respirasi")))
                            .append((rs.getString("tinggi") == null || rs.getString("tinggi").isBlank() ? "" : "<br>Tinggi(Cm) : " + rs.getString("tinggi")))
                            .append((rs.getString("berat") == null || rs.getString("berat").isBlank() ? "" : "<br>Berat(Kg) : " + rs.getString("berat")))
                            .append((rs.getString("lingkar_perut") == null || rs.getString("lingkar_perut").isBlank() ? "" : "<br>Lingkar Perut(Cm) : " + rs.getString("lingkar_perut")))
                            .append((rs.getString("spo2") == null || rs.getString("spo2").isBlank() ? "" : "<br>SpO2(%) : " + rs.getString("spo2")))
                            .append((rs.getString("gcs") == null || rs.getString("gcs").isBlank() ? "" : "<br>GCS(E,V,M) : " + rs.getString("gcs")))
                            .append((rs.getString("kesadaran") == null || rs.getString("kesadaran").isBlank() ? "" : "<br>Kesadaran : " + rs.getString("kesadaran")))
                            .append("</td>")
                            .append("<td align=\"left\">" + HtmlUtils.htmlEscape(rs.getString("penilaian")).replaceAll("\\R", "<br>") + "</td>")
                            .append("<td align=\"left\">" + HtmlUtils.htmlEscape(rs.getString("rtl")).replaceAll("\\R", "<br>") + "</td>")
                            .append("<td align=\"left\">" + HtmlUtils.htmlEscape(rs.getString("instruksi")).replaceAll("\\R", "<br>") + "</td>")
                            .append("<td align=\"left\">" + HtmlUtils.htmlEscape(rs.getString("evaluasi")).replaceAll("\\R", "<br>") + "</td>")
                            .append("</tr>");
                    }
                }
            }

            htmlContent
                .append("</tbody>")
                .append("</table>")
                .append("</td>")
                .append("</tr>")
                .append("</body>")
                .append("</html>");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("soap_ralan.html")))) {
                String html = htmlContent.toString().replaceAll(getClass().getResource("/picture/").toString(), "./gambar/");
                bw.write(html);
            }

            try (FileOutputStream os = new FileOutputStream("./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + "_" + urutan + "_SOAP.pdf")) {
                org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(new File("soap_ralan.html"));
                org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withW3cDocument(w3cDoc, null);
                builder.toStream(os);
                builder.run();
            }
        } catch (Exception e) {
            exportSukses = false;
            System.out.println("Notif : " + e);
        }
    }

    private void exportAwalMedisIGD(String urutan) {
        if (Sequel.cariBooleanSmc("select * from reg_periksa where no_rawat = ? and kd_poli != 'IGDK'", lblNoRawat.getText())) return;
        
        if (!btnAwalMedisIGD.isEnabled()) return;
        
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
        exportPDF("rptCetakPenilaianAwalMedisIGD.jasper", urutan + "_AwalMedisIGD", param,
            "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, if (pasien.jk = 'L', 'Laki-Laki', 'Perempuan') as jk, pasien.tgl_lahir, penilaian_medis_igd.tanggal, penilaian_medis_igd.kd_dokter, "
            + "penilaian_medis_igd.anamnesis, penilaian_medis_igd.hubungan, concat_ws(', ', penilaian_medis_igd.anamnesis, nullif(penilaian_medis_igd.hubungan, '')) as hubungan_anemnesis, penilaian_medis_igd.keluhan_utama, "
            + "penilaian_medis_igd.rps, penilaian_medis_igd.rpk, penilaian_medis_igd.rpd, penilaian_medis_igd.rpo, penilaian_medis_igd.alergi, penilaian_medis_igd.keadaan, penilaian_medis_igd.gcs, penilaian_medis_igd.kesadaran, "
            + "penilaian_medis_igd.td, penilaian_medis_igd.nadi, penilaian_medis_igd.rr, penilaian_medis_igd.suhu, penilaian_medis_igd.spo, penilaian_medis_igd.bb, penilaian_medis_igd.tb, penilaian_medis_igd.kepala, penilaian_medis_igd.mata, "
            + "penilaian_medis_igd.gigi, penilaian_medis_igd.leher, penilaian_medis_igd.thoraks, penilaian_medis_igd.abdomen, penilaian_medis_igd.ekstremitas, penilaian_medis_igd.genital, penilaian_medis_igd.ket_fisik, penilaian_medis_igd.ket_lokalis, "
            + "penilaian_medis_igd.ekg, penilaian_medis_igd.rad, penilaian_medis_igd.lab, penilaian_medis_igd.diagnosis, penilaian_medis_igd.tata, dokter.nm_dokter from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis "
            + "join penilaian_medis_igd on reg_periksa.no_rawat = penilaian_medis_igd.no_rawat join dokter on penilaian_medis_igd.kd_dokter = dokter.kd_dokter where penilaian_medis_igd.no_rawat = ?", lblNoRawat.getText()
        );
    }

    private void exportHasilLab(String urutan) {
        if (!btnHasilLab.isEnabled()) return;
        String kamar = "", namaKamar = "";
        int i = 0, j = 0;
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
                        param.put("lahir", rs.getString("tgl_lahir"));
                        param.put("alamat", rs.getString("alamat"));
                        param.put("diagnosa", tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 9).toString());
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
                "select periksa_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan, periksa_lab.kategori, periksa_lab.tgl_periksa, periksa_lab.jam, periksa_lab.nip, " +
                "periksa_lab.dokter_perujuk, periksa_lab.status, periksa_lab.kd_dokter, periksa_lab.biaya, petugas.nama, perujuk.nm_dokter as nm_perujuk, dokter.nm_dokter " +
                "from periksa_lab join jns_perawatan_lab on periksa_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw join petugas on periksa_lab.nip = petugas.nip " +
                "join dokter on periksa_lab.kd_dokter = dokter.kd_dokter join dokter perujuk on periksa_lab.dokter_perujuk = perujuk.kd_dokter where periksa_lab.no_rawat = ?"
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
                            namaKamar = tbKompilasi.getValueAt(tbKompilasi.getSelectedRow(), 8).toString();
                            kamar = "Kamar";
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
                        if (rs.getString("kategori").equals("PK")) {
                            Sequel.temporaryLab(String.valueOf(++i), rs.getString("nm_perawatan"));
                            try (PreparedStatement ps2 = koneksi.prepareStatement(
                                "select template_laboratorium.Pemeriksaan, detail_periksa_lab.nilai, template_laboratorium.satuan, detail_periksa_lab.nilai_rujukan, detail_periksa_lab.biaya_item, " +
                                "detail_periksa_lab.keterangan, detail_periksa_lab.kd_jenis_prw from detail_periksa_lab join template_laboratorium on detail_periksa_lab.id_template = template_laboratorium.id_template " +
                                "where detail_periksa_lab.no_rawat = ? and detail_periksa_lab.kd_jenis_prw = ? and detail_periksa_lab.tgl_periksa = ? and detail_periksa_lab.jam = ? order by template_laboratorium.urut"
                            )) {
                                ps2.setString(1, lblNoRawat.getText());
                                ps2.setString(2, rs.getString("kd_jenis_prw"));
                                ps2.setString(3, rs.getString("tgl_periksa"));
                                ps2.setString(4, rs.getString("jam"));
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    while (rs2.next()) {
                                        Sequel.temporaryLab(String.valueOf(++i), "  " + rs2.getString("Pemeriksaan"), rs2.getString("nilai"),
                                            rs2.getString("satuan"), rs2.getString("nilai_rujukan"), rs2.getString("keterangan"));
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
                                        exportPDF("rptPeriksaLab4Permintaan.jasper", urutan + "_HasilLab" + String.valueOf(++j), param);
                                    } else {
                                        exportPDF("rptPeriksaLab4.jasper", urutan + "_HasilLab" + String.valueOf(++j), param);
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
                                    if (rs2.next()) {
                                        param.put("nopermintaan", rs2.getString("noorder"));
                                        param.put("tanggalpermintaan", rs2.getString("tgl_permintaan"));
                                        param.put("jampermintaan", rs2.getString("jam_permintaan"));
                                        exportPDF("rptPeriksaLabPermintaanPA.jasper", urutan + "_HasilLab" + String.valueOf(++j), param);
                                    } else {
                                        exportPDF("rptPeriksaLabPA.jasper", urutan + "_HasilLab" + String.valueOf(++j), param);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void exportHasilRadiologi(String urutan) {
        if (!btnHasilRad.isEnabled()) {
            return;
        }
        int j = 1;
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select pasien.jk, date_format(pasien.tgl_lahir, '%d-%m-%Y') as tgllahir, concat(reg_periksa.umurdaftar, ' ', reg_periksa.sttsumur) as umur, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, periksa_radiologi.dokter_perujuk, "
            + "dokter_perujuk.nm_dokter nm_dokter_perujuk, periksa_radiologi.tgl_periksa, periksa_radiologi.jam, periksa_radiologi.kd_dokter, dokter.nm_dokter, periksa_radiologi.nip, petugas.nama nama_petugas, jns_perawatan_radiologi.nm_perawatan, "
            + "periksa_radiologi.status, periksa_radiologi.proyeksi, periksa_radiologi.kV, periksa_radiologi.mAS, periksa_radiologi.FFD, periksa_radiologi.BSF, periksa_radiologi.inak, periksa_radiologi.jml_penyinaran, periksa_radiologi.dosis "
            + "from periksa_radiologi join reg_periksa on periksa_radiologi.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join dokter dokter_perujuk on periksa_radiologi.dokter_perujuk = dokter_perujuk.kd_dokter "
            + "join dokter on periksa_radiologi.kd_dokter = dokter.kd_dokter join petugas on periksa_radiologi.nip = petugas.nip join jns_perawatan_radiologi on periksa_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw "
            + "left join kelurahan on pasien.kd_kel = kelurahan.kd_kel left join kecamatan on pasien.kd_kec = kecamatan.kd_kec left join kabupaten on pasien.kd_kab = kabupaten.kd_kab where periksa_radiologi.no_rawat = ?"
        )) {
            ps.setString(1, lblNoRawat.getText());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pemeriksaan = rs.getString("nm_perawatan")
                        + (rs.getString("proyeksi").isBlank() || rs.getString("proyeksi") == null ? "" : " dengan Proyeksi : " + rs.getString("proyeksi"))
                        + (rs.getString("kV").isBlank() || rs.getString("kV") == null ? "" : ", kV : " + rs.getString("kV"))
                        + (rs.getString("mAS").isBlank() || rs.getString("mAS") == null ? "" : ", mAS : " + rs.getString("mAS"))
                        + (rs.getString("FFD").isBlank() || rs.getString("FFD") == null ? "" : ", FFD : " + rs.getString("FFD"))
                        + (rs.getString("BSF").isBlank() || rs.getString("BSF") == null ? "" : ", BSF : " + rs.getString("BSF"))
                        + (rs.getString("Inak").isBlank() || rs.getString("Inak") == null ? "" : ", Inak : " + rs.getString("Inak"))
                        + (rs.getString("jml_penyinaran").isBlank() || rs.getString("jml_penyinaran") == null ? "" : ", Jumlah penyinaran : " + rs.getString("jml_penyinaran"))
                        + (rs.getString("dosis").isBlank() || rs.getString("dosis") == null ? "" : ", Dosis Radiasi : " + rs.getString("dosis"));
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
                            kamar = Sequel.cariIsiSmc("select ifnull(kd_kamar, '') from kamar_inap where no_rawat = ? order by tgl_masuk desc limit 1", noRawatIbu);
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
                    param.put("pemeriksaan", pemeriksaan);
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
                    exportPDF("rptPeriksaRadiologi.jasper", urutan + "_PeriksaRadiologi" + String.valueOf(j++), param);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void exportSKDP(String urutan) {
        if (!btnSurkon.isEnabled()) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        String noSurat = Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText());
        String tglSurat = Sequel.cariIsiSmc("select date_format(tgl_surat, '%d-%m-%Y') from bridging_surat_kontrol_bpjs where no_surat = ?", noSurat);
        String kodeDokter = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs = (select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat = ?)", noSurat);
        String namaDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodeDokter);
        param.put("parameter", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText()));
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokter + "\nID " + kodeDokter + "\n" + tglSurat);
        exportPDF("rptBridgingSuratKontrol2.jasper", urutan + "_SuratKontrol", param,
            "select bridging_sep.no_rawat, bridging_sep.no_sep, bridging_sep.no_kartu, bridging_sep.nomr, bridging_sep.nama_pasien, bridging_sep.tanggal_lahir, bridging_sep.jkel, bridging_sep.diagawal, bridging_sep.nmdiagnosaawal, bridging_surat_kontrol_bpjs.tgl_surat, "
            + "bridging_surat_kontrol_bpjs.no_surat, bridging_surat_kontrol_bpjs.tgl_rencana, bridging_surat_kontrol_bpjs.kd_dokter_bpjs, bridging_surat_kontrol_bpjs.nm_dokter_bpjs, bridging_surat_kontrol_bpjs.kd_poli_bpjs, bridging_surat_kontrol_bpjs.nm_poli_bpjs "
            + "from bridging_sep join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?", Sequel.cariIsiSmc("select noskdp from bridging_sep where no_sep = ?", lblNoSEP.getText())
        );
    }

    private void exportSPRI(String urutan) {
        if (!btnSPRI.isEnabled()) {
            return;
        }
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
        exportPDF("rptBridgingSuratPRI2.jasper", urutan + "_SPRI", param,
            "select bridging_surat_pri_bpjs.*, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk "
            + "from reg_periksa join bridging_surat_pri_bpjs on bridging_surat_pri_bpjs.no_rawat = reg_periksa.no_rawat "
            + "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where bridging_surat_pri_bpjs.no_surat = ?", noSPRI
        );
    }
    
    private void exportBerkasDigitalPerawatan(String urutan) {
        if (Sequel.cariBooleanSmc(
            "select * from berkas_digital_perawatan join master_berkas_digital on berkas_digital_perawatan.kode = master_berkas_digital.kode " +
            "where no_rawat = ? and lokasi_file like '%.pdf' and master_berkas_digital.include_kompilasi_berkas = 0", lblNoRawat.getText()
        )) return;
        
        int i = 1;
        String filename = "";
        HttpURLConnection http;
        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/";
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select lokasi_file from berkas_digital_perawatan join master_berkas_digital on berkas_digital_perawatan.kode = master_berkas_digital.kode " +
            "where no_rawat = ? and lokasi_file like '%.pdf' and master_berkas_digital.include_kompilasi_berkas = 1"
        )) {
            ps.setString(1, lblNoRawat.getText());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filename = rs.getString("lokasi_file");
                    String exportPath = "./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + "_" + urutan + "_BerkasDigital" + String.valueOf(i++) + ".pdf";
                    if (filename.endsWith(".pdf")) {
                        try (FileOutputStream os = new FileOutputStream(exportPath); FileChannel fileChannel = os.getChannel()) {
                            URL fileUrl = new URL(url + rs.getString("lokasi_file"));
                            http = (HttpURLConnection) fileUrl.openConnection();
                            if (http.getResponseCode() == 200) {
                                fileChannel.transferFrom(Channels.newChannel(fileUrl.openStream()), 0, Long.MAX_VALUE);
                                http.disconnect();
                            } else {
                                System.out.println("File not found : " + url + rs.getString("lokasi_file"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            exportSukses = false;
            System.out.println("Notif : " + e);
            cleanupSingleFile(lblNoSEP.getText() + "_" + urutan + "_BerkasDigital");
        }
    }

    private void gabung() {
        tanggalExport = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        exportKlaimINACBG("001");
        exportSEP("002");
        exportTriaseIGD("003");
        exportAwalMedisIGD("004");
        exportSOAP("005");
        exportResumeRanap("006");
        exportBilling("007");
        exportHasilLab("008");
        exportHasilRadiologi("009");
        exportBerkasDigitalPerawatan("010");
        // exportSKDP("009");
        // exportSPRI("010");
        if (exportSukses) {
            exportSukses = mergePDF();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Tidak bisa mengekspor sebagai PDF!");
        }
        
        if (exportSukses) {
            cleanupAfterMerge(true);
            JOptionPane.showMessageDialog(rootPane, "Export PDF berhasil!");
        } else {
            exportSukses = true;
        }
    }

    private boolean mergePDF() {
        boolean sukses = true;
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        File folder = new File("./berkaspdf/" + tanggalExport);
        File[] files = folder.listFiles();
        if (files != null) {
            try {
                Arrays.sort(files, (file1, file2) -> file1.getName().compareTo(file2.getName()));
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".pdf") && file.getName().startsWith(lblNoSEP.getText() + "_")) {
                        try {
                            pdfMerger.addSource(file);
                        } catch (Exception e) {
                            System.err.println("Error adding file: " + file.getName());
                            sukses = false;
                            throw e;
                        }
                    }
                }
                pdfMerger.setDestinationFileName("./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + ".pdf");
                pdfMerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly(80 * 1_000_000));
                System.out.println("PDFs merged successfully!");
                File f = new File("./berkaspdf/" + tanggalExport + "/" + lblNoSEP.getText() + ".pdf");
                Desktop.getDesktop().open(f);
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                sukses = false;
                e.printStackTrace();
            }
        } else {
            System.out.println("No PDF files found in the folder: ./berkaspdf/" + tanggalExport);
        }
        return sukses;
    }
    
    private void cleanupSingleFile(String containsName) {
        File folder = new File("./berkaspdf/" + tanggalExport);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(containsName)) {
                    file.delete();
                }
            }
        }
    }

    private void cleanupAfterMerge(boolean doCleanup) {
        if (! doCleanup) {
            return;
        }
        File folder = new File("./berkaspdf/" + tanggalExport);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(lblNoSEP.getText() + "_")) {
                    if (file.delete()) {
                        System.out.println("Deleting file: " + file.getName());
                    } else {
                        System.out.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        } else {
            System.out.println("No files found in the folder: ./berkaspdf/" + tanggalExport);
        }
    }
}
