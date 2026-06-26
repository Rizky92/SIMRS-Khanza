/*
 * By Mas Elkhanza
 */
package rekammedis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMPenilaianTindakanInvasifNonBedahSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeMasalah, tabModeDetailMasalah, tabModeRencana, tabModeDetailRencana;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private DlgCariPetugas petugas;
    private volatile boolean ceksukses = false;
    private StringBuilder htmlContent;
    private String finger = "";
    private ObjectMapper mapper = new ObjectMapper();
    private String TANGGALMUNDUR = "yes";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMPenilaianTindakanInvasifNonBedahSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "NIP", "Nama Petugas", "Tanggal", "Diagnosa", "Rencana Tindakan", "Status Fungsional",
            "Keluhan Utama", "Status Psikologis", "Ket. Psikologis", "RPD", "Sistem Pernapasan", "Ket. Pernapasan", "Muntah Darah", "BAB", "Urine", "Antiplatelet",
            "Lama Antiplatelet", "Beta Blocker", "Lama Beta Blocker", "Simarc", "Lama Simarc", "Riwayat Alergi", "TB (cm)", "BB (Kg)", "TD (mmHg)", "Saturasi O2 (%)",
            "Nadi (x/menit)", "Suhu (°C)", "Pernapasan (x/menit)", "Radialis Kanan", "Radialis Kiri", "Pedis Kanan", "Pedis Kiri", "Nyeri", "Pencetus Nyeri",
            "Kualitas Nyeri", "Lokasi Nyeri", "Nyeri Menyebar", "Skala Nyeri", "Durasi Nyeri", "Kebutuhan Edukasi", "Hematokrit", "Hemoglobin", "Leukosit", "PT/INR", "Kalium",
            "Natrium", "Ureum", "HbsAg", "Anti HCV", "GDS", "PT/APTT", "Kreatinin", "Skrining Jatuh", "Skor Jatuh", "Echo", "Rencana Keperawatan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(70);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else if (i == 3) {
                column.setPreferredWidth(65);
            } else if (i == 4) {
                column.setPreferredWidth(65);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(150);
            } else if (i == 7) {
                column.setPreferredWidth(115);
            } else if (i == 8) {
                column.setPreferredWidth(170);
            } else if (i == 9) {
                column.setPreferredWidth(170);
            } else if (i == 10) {
                column.setPreferredWidth(170);
            } else if (i == 26) {
                column.setPreferredWidth(35);
            } else if (i == 27) {
                column.setPreferredWidth(35);
            } else if (i == 28) {
                column.setPreferredWidth(50);
            } else if (i == 30) {
                column.setPreferredWidth(35);
            } else if (i == 31) {
                column.setPreferredWidth(35);
            } else if (i == 32) {
                column.setPreferredWidth(55);
            } else {
                column.setPreferredWidth(90);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        Diagnosa.setDocument(new batasInput((byte) 100).getKata(Diagnosa));
        RencanaTindakan.setDocument(new batasInput((byte) 100).getKata(RencanaTindakan));
        StatusFungsional.setDocument(new batasInput((int) 200).getKata(StatusFungsional));
        TB.setDocument(new batasInput((byte) 5).getKata(TB));
        BB.setDocument(new batasInput((byte) 5).getKata(BB));
        TD.setDocument(new batasInput((byte) 8).getKata(TD));
        IO2.setDocument(new batasInput((byte) 5).getKata(IO2));
        Nadi.setDocument(new batasInput((byte) 5).getKata(Nadi));
        Suhu.setDocument(new batasInput((byte) 5).getKata(Suhu));
        Urine24Jam.setDocument(new batasInput((byte) 5).getKata(Urine24Jam));
        LamaAntiPlatelet.setDocument(new batasInput((int) 50).getKata(LamaAntiPlatelet));
        LamaBetaBlocker.setDocument(new batasInput((int) 50).getKata(LamaBetaBlocker));
        LamaSimarc.setDocument(new batasInput((int) 50).getKata(LamaSimarc));
        AlergiKeterangan.setDocument(new batasInput((int) 200).getKata(AlergiKeterangan));
        Pernapasan.setDocument(new batasInput((byte) 5).getKata(Pernapasan));
        NyeriLokasi.setDocument(new batasInput((int) 200).getKata(NyeriLokasi));
        NyeriLama.setDocument(new batasInput((int) 50).getKata(NyeriLama));
        NyeriPencetus.setDocument(new batasInput((int) 200).getKata(NyeriPencetus));
        NyeriKualitas.setDocument(new batasInput((int) 200).getKata(NyeriKualitas));
        NyeriPenjalaran.setDocument(new batasInput((int) 200).getKata(NyeriPenjalaran));
        KebutuhanEdukasi.setDocument(new batasInput((int) 200).getKata(KebutuhanEdukasi));
        LabHb.setDocument(new batasInput((int) 10).getKata(LabHb));
        LabHt.setDocument(new batasInput((int) 10).getKata(LabHt));
        LabLeukosit.setDocument(new batasInput((int) 10).getKata(LabLeukosit));
        LabNa.setDocument(new batasInput((int) 10).getKata(LabNa));
        LabUr.setDocument(new batasInput((int) 10).getKata(LabUr));
        LabCr.setDocument(new batasInput((int) 10).getKata(LabCr));
        LabK.setDocument(new batasInput((int) 10).getKata(LabK));
        LabPtIr.setDocument(new batasInput((int) 10).getKata(LabPtIr));
        LabPtAptt.setDocument(new batasInput((int) 10).getKata(LabPtAptt));
        LabGds.setDocument(new batasInput((int) 10).getKata(LabGds));
        SkriningSkor.setDocument(new batasInput((int) 10).getKata(SkriningSkor));
        EchoKesan.setDocument(new batasInput((int) 200).getKata(EchoKesan));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        tabModeMasalah = new DefaultTableModel(null, new Object[] {
            "P", "KODE", "MASALAH KEPERAWATAN"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                return String.class;
            }
        };
        tbMasalahKeperawatan.setModel(tabModeMasalah);
        tbMasalahKeperawatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMasalahKeperawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < tabModeMasalah.getColumnCount(); i++) {
            TableColumn column = tbMasalahKeperawatan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            }
        }
        tbMasalahKeperawatan.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRencana = new DefaultTableModel(null, new Object[] {
            "P", "KODE", "RENCANA KEPERAWATAN", "kode_masalah"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        tbRencanaKeperawatan.setModel(tabModeRencana);
        tbRencanaKeperawatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRencanaKeperawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 3; i++) {
            TableColumn column = tbRencanaKeperawatan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            }
        }
        tbRencanaKeperawatan.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailMasalah = new DefaultTableModel(null, new Object[] {"Kode", "Masalah Keperawatan"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbMasalahDetail.setModel(tabModeDetailMasalah);
        tbMasalahDetail.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMasalahDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 2; i++) {
            TableColumn column = tbMasalahDetail.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 1) {
                column.setPreferredWidth(420);
            }
        }
        tbMasalahDetail.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailRencana = new DefaultTableModel(null, new Object[] {"Kode", "Rencana Keperawatan"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbRencanaDetail.setModel(tabModeDetailRencana);
        tbRencanaDetail.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRencanaDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 2; i++) {
            TableColumn column = tbRencanaDetail.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 1) {
                column.setPreferredWidth(420);
            }
        }
        tbRencanaDetail.setDefaultRenderer(Object.class, new WarnaTable());

        ChkAccor.setSelected(false);
        isMenu();

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
            ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
            ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}" +
            ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
            ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
            ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}" +
            ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}" +
            ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}" +
            ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}" +
            ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);

        try {
            TANGGALMUNDUR = koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR = "yes";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoadHTML = new widget.editorpane();
        TanggalRegistrasi = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        label14 = new widget.Label();
        kdptg = new widget.TextBox();
        nmptg = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        jSeparator1 = new javax.swing.JSeparator();
        label11 = new widget.Label();
        jLabel12 = new widget.Label();
        Diagnosa = new widget.TextBox();
        jLabel13 = new widget.Label();
        RencanaTindakan = new widget.TextBox();
        jLabel14_StatusFungsional = new widget.Label();
        StatusFungsional = new widget.TextBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel15 = new widget.Label();
        TB = new widget.TextBox();
        jLabel24 = new widget.Label();
        jLabel16 = new widget.Label();
        BB = new widget.TextBox();
        jLabel17 = new widget.Label();
        jLabel22 = new widget.Label();
        TD = new widget.TextBox();
        jLabel23 = new widget.Label();
        jLabel18 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel25 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel26 = new widget.Label();
        jLabel29 = new widget.Label();
        IO2 = new widget.TextBox();
        jLabel35 = new widget.Label();
        jLabel27 = new widget.Label();
        Pernapasan = new widget.TextBox();
        jLabel28 = new widget.Label();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel112 = new widget.Label();
        jLabel49 = new widget.Label();
        jLabel50 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        KeluhanUtama = new widget.TextArea();
        jLabel9 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        RPD = new widget.TextArea();
        jLabel51 = new widget.Label();
        jLabel30 = new widget.Label();
        LabHt = new widget.TextBox();
        jLabel31 = new widget.Label();
        LabHb = new widget.TextBox();
        jLabel32 = new widget.Label();
        LabLeukosit = new widget.TextBox();
        LabPtIr = new widget.TextBox();
        jLabel34 = new widget.Label();
        jLabel33 = new widget.Label();
        LabK = new widget.TextBox();
        jLabel36 = new widget.Label();
        LabNa = new widget.TextBox();
        jLabel37 = new widget.Label();
        jLabel84 = new widget.Label();
        LabUr = new widget.TextBox();
        jLabel86 = new widget.Label();
        LabHbsAg = new widget.ComboBox();
        jLabel43 = new widget.Label();
        LabAntiHCV = new widget.ComboBox();
        LabGds = new widget.TextBox();
        LabPtAptt = new widget.TextBox();
        jLabel96 = new widget.Label();
        LabCr = new widget.TextBox();
        jLabel83 = new widget.Label();
        jLabel39 = new widget.Label();
        jLabel132 = new widget.Label();
        StatusPsiko = new widget.ComboBox();
        AlergiKeterangan = new widget.TextBox();
        jLabel133 = new widget.Label();
        BAB = new widget.ComboBox();
        jLabel14 = new widget.Label();
        jLabel38 = new widget.Label();
        jLabel40 = new widget.Label();
        Antiplatelet = new widget.ComboBox();
        LamaAntiPlatelet = new widget.TextBox();
        BetaBlocker = new widget.ComboBox();
        LamaBetaBlocker = new widget.TextBox();
        Simarc = new widget.ComboBox();
        LamaSimarc = new widget.TextBox();
        jLabel52 = new widget.Label();
        scrollPane6 = new widget.ScrollPane();
        EchoKesan = new widget.TextArea();
        jLabel41 = new widget.Label();
        SkriningJatuh = new widget.ComboBox();
        SkriningSkor = new widget.TextBox();
        jLabel42 = new widget.Label();
        jLabel134 = new widget.Label();
        jLabel135 = new widget.Label();
        KetPsiko = new widget.TextBox();
        Urine24Jam = new widget.TextBox();
        jLabel45 = new widget.Label();
        jLabel46 = new widget.Label();
        jLabel47 = new widget.Label();
        jLabel48 = new widget.Label();
        jLabel53 = new widget.Label();
        jLabel54 = new widget.Label();
        RadialisKanan = new widget.ComboBox();
        jLabel55 = new widget.Label();
        RadialisKiri = new widget.ComboBox();
        jLabel56 = new widget.Label();
        jLabel57 = new widget.Label();
        jLabel58 = new widget.Label();
        PedisKiri = new widget.ComboBox();
        PedisKanan = new widget.ComboBox();
        PanelWall = new usu.widget.glass.PanelGlass();
        Nyeri = new widget.ComboBox();
        jLabel87 = new widget.Label();
        jLabel85 = new widget.Label();
        NyeriLama = new widget.TextBox();
        NyeriSkala = new widget.ComboBox();
        jLabel90 = new widget.Label();
        NyeriLokasi = new widget.TextBox();
        KetSistemPernapasan = new widget.TextBox();
        jLabel59 = new widget.Label();
        jLabel60 = new widget.Label();
        jLabel61 = new widget.Label();
        SistemPernapasan = new widget.ComboBox();
        MuntahDarah = new widget.ComboBox();
        jLabel62 = new widget.Label();
        jLabel63 = new widget.Label();
        jLabel65 = new widget.Label();
        jLabel44 = new widget.Label();
        jLabel92 = new widget.Label();
        NyeriPencetus = new widget.TextBox();
        jLabel91 = new widget.Label();
        NyeriKualitas = new widget.TextBox();
        jLabel93 = new widget.Label();
        NyeriPenjalaran = new widget.TextBox();
        jLabel64 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        KebutuhanEdukasi = new widget.TextArea();
        Scroll6 = new widget.ScrollPane();
        tbMasalahKeperawatan = new widget.Table();
        label12 = new widget.Label();
        BtnTambahMasalah = new widget.Button();
        jSeparator10 = new javax.swing.JSeparator();
        BtnCariMasalah = new widget.Button();
        BtnAllMasalah = new widget.Button();
        TCariMasalah = new widget.TextBox();
        TabRencanaKeperawatan = new javax.swing.JTabbedPane();
        panelBiasa1 = new widget.PanelBiasa();
        Scroll8 = new widget.ScrollPane();
        tbRencanaKeperawatan = new widget.Table();
        scrollPane5 = new widget.ScrollPane();
        Rencana = new widget.TextArea();
        BtnTambahRencana = new widget.Button();
        BtnAllRencana = new widget.Button();
        BtnCariRencana = new widget.Button();
        label13 = new widget.Label();
        TCariRencana = new widget.TextBox();
        TglAsuhan = new widget.Tanggal();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        FormMenu = new widget.PanelBiasa();
        jLabel1 = new widget.Label();
        TNoRM1 = new widget.TextBox();
        TPasien1 = new widget.TextBox();
        BtnPrint1 = new widget.Button();
        FormMasalahRencana = new widget.PanelBiasa();
        Scroll7 = new widget.ScrollPane();
        tbMasalahDetail = new widget.Table();
        Scroll9 = new widget.ScrollPane();
        tbRencanaDetail = new widget.Table();
        ScrollRencana = new widget.ScrollPane();
        DetailRencana = new widget.TextArea();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Penilaian Tindakan Invasif Non Bedah ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setPreferredSize(new java.awt.Dimension(467, 500));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.setPreferredSize(new java.awt.Dimension(457, 480));

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setPreferredSize(new java.awt.Dimension(102, 480));
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(800, 1700));
        FormInput.setLayout(null);

        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 131, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(309, 10, 260, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(207, 10, 100, 23);

        label14.setText("Petugas :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(166, 40, 50, 23);

        kdptg.setEditable(false);
        kdptg.setName("kdptg"); // NOI18N
        kdptg.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(kdptg);
        kdptg.setBounds(220, 40, 90, 23);

        nmptg.setEditable(false);
        nmptg.setName("nmptg"); // NOI18N
        nmptg.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmptg);
        nmptg.setBounds(312, 40, 180, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(494, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(580, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(644, 10, 80, 23);

        Jk.setEditable(false);
        Jk.setName("Jk"); // NOI18N
        FormInput.add(Jk);
        Jk.setBounds(74, 40, 80, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 40, 70, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(538, 40, 52, 23);

        jLabel12.setText("Diagnosa :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 80, 62, 23);

        Diagnosa.setName("Diagnosa"); // NOI18N
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa);
        Diagnosa.setBounds(66, 80, 139, 23);

        jLabel13.setText("Rencana Tindakan :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(208, 80, 100, 23);

        RencanaTindakan.setName("RencanaTindakan"); // NOI18N
        RencanaTindakan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RencanaTindakanKeyPressed(evt);
            }
        });
        FormInput.add(RencanaTindakan);
        RencanaTindakan.setBounds(312, 80, 210, 23);

        jLabel14_StatusFungsional.setText("Status Fungsional :");
        jLabel14_StatusFungsional.setName("jLabel14_StatusFungsional"); // NOI18N
        FormInput.add(jLabel14_StatusFungsional);
        jLabel14_StatusFungsional.setBounds(525, 80, 110, 23);

        StatusFungsional.setName("StatusFungsional"); // NOI18N
        FormInput.add(StatusFungsional);
        StatusFungsional.setBounds(640, 80, 200, 23);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator2.setName("jSeparator2"); // NOI18N
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(0, 70, 750, 1);

        jLabel15.setText("TB :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 710, 70, 23);

        TB.setFocusTraversalPolicyProvider(true);
        TB.setName("TB"); // NOI18N
        FormInput.add(TB);
        TB.setBounds(75, 710, 55, 23);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText(" Cm");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(135, 710, 30, 23);

        jLabel16.setText("BB :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(180, 710, 40, 23);

        BB.setFocusTraversalPolicyProvider(true);
        BB.setName("BB"); // NOI18N
        FormInput.add(BB);
        BB.setBounds(225, 710, 55, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Kg");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(285, 710, 30, 23);

        jLabel22.setText("TD :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(315, 710, 40, 23);

        TD.setFocusTraversalPolicyProvider(true);
        TD.setName("TD"); // NOI18N
        FormInput.add(TD);
        TD.setBounds(360, 710, 76, 23);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("mmHg");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(440, 710, 50, 23);

        jLabel18.setText("Nadi :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(0, 740, 70, 23);

        Nadi.setFocusTraversalPolicyProvider(true);
        Nadi.setName("Nadi"); // NOI18N
        FormInput.add(Nadi);
        Nadi.setBounds(75, 740, 55, 23);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("x/menit");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(135, 740, 50, 23);

        jLabel25.setText("Suhu :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(180, 740, 40, 23);

        Suhu.setFocusTraversalPolicyProvider(true);
        Suhu.setName("Suhu"); // NOI18N
        FormInput.add(Suhu);
        Suhu.setBounds(225, 740, 55, 23);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("°C");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(285, 740, 30, 23);

        jLabel29.setText("IO2 :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(520, 710, 40, 23);

        IO2.setFocusTraversalPolicyProvider(true);
        IO2.setName("IO2"); // NOI18N
        FormInput.add(IO2);
        IO2.setBounds(565, 710, 55, 23);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("%");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(625, 710, 30, 23);

        jLabel27.setText("Pernapasan :");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(310, 740, 90, 23);

        Pernapasan.setFocusTraversalPolicyProvider(true);
        Pernapasan.setName("Pernapasan"); // NOI18N
        FormInput.add(Pernapasan);
        Pernapasan.setBounds(405, 740, 55, 23);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("x/menit");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(465, 740, 50, 23);

        jSeparator3.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator3.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator3.setName("jSeparator3"); // NOI18N
        FormInput.add(jSeparator3);
        jSeparator3.setBounds(0, 110, 880, 1);

        jLabel112.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel112.setText("A. ASSESMEN PRA TINDAKAN");
        jLabel112.setName("jLabel112"); // NOI18N
        FormInput.add(jLabel112);
        jLabel112.setBounds(10, 110, 150, 23);

        jLabel49.setText("1. Keluhan Utama :");
        jLabel49.setName("jLabel49"); // NOI18N
        FormInput.add(jLabel49);
        jLabel49.setBounds(0, 140, 120, 20);

        jLabel50.setText("3. Riwayat Penyakit Dahulu :");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(0, 250, 170, 23);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        KeluhanUtama.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KeluhanUtama.setColumns(20);
        KeluhanUtama.setRows(5);
        KeluhanUtama.setName("KeluhanUtama"); // NOI18N
        scrollPane3.setViewportView(KeluhanUtama);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(60, 165, 750, 50);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("c. Simarc                      :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(50, 600, 120, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        RPD.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        RPD.setColumns(20);
        RPD.setRows(5);
        RPD.setName("RPD"); // NOI18N
        scrollPane2.setViewportView(RPD);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(60, 275, 750, 50);

        jLabel51.setText("a. Muntah Darah :");
        jLabel51.setName("jLabel51"); // NOI18N
        FormInput.add(jLabel51);
        jLabel51.setBounds(0, 390, 160, 23);

        jLabel30.setText("Hematokrit :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(0, 1250, 135, 23);

        LabHt.setFocusTraversalPolicyProvider(true);
        LabHt.setName("LabHt"); // NOI18N
        FormInput.add(LabHt);
        LabHt.setBounds(140, 1250, 110, 23);

        jLabel31.setText("Hemoglobin :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(0, 1280, 135, 23);

        LabHb.setFocusTraversalPolicyProvider(true);
        LabHb.setName("LabHb"); // NOI18N
        FormInput.add(LabHb);
        LabHb.setBounds(140, 1280, 110, 23);

        jLabel32.setText("Leukosit :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(0, 1310, 135, 23);

        LabLeukosit.setFocusTraversalPolicyProvider(true);
        LabLeukosit.setName("LabLeukosit"); // NOI18N
        FormInput.add(LabLeukosit);
        LabLeukosit.setBounds(140, 1310, 110, 23);

        LabPtIr.setFocusTraversalPolicyProvider(true);
        LabPtIr.setName("LabPtIr"); // NOI18N
        FormInput.add(LabPtIr);
        LabPtIr.setBounds(330, 1340, 110, 23);

        jLabel34.setText("PT/IR :");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(250, 1340, 75, 23);

        jLabel33.setText("Kreatinin :");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(250, 1280, 75, 23);

        LabK.setFocusTraversalPolicyProvider(true);
        LabK.setName("LabK"); // NOI18N
        FormInput.add(LabK);
        LabK.setBounds(330, 1310, 110, 23);

        jLabel36.setText("Natrium :");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(0, 1340, 135, 23);

        LabNa.setFocusTraversalPolicyProvider(true);
        LabNa.setName("LabNa"); // NOI18N
        FormInput.add(LabNa);
        LabNa.setBounds(140, 1340, 110, 23);

        jLabel37.setText("Ureum :");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(250, 1250, 75, 23);

        jLabel84.setText("Kalium :");
        jLabel84.setName("jLabel84"); // NOI18N
        FormInput.add(jLabel84);
        jLabel84.setBounds(250, 1310, 75, 23);

        LabUr.setFocusTraversalPolicyProvider(true);
        LabUr.setName("LabUr"); // NOI18N
        FormInput.add(LabUr);
        LabUr.setBounds(330, 1250, 110, 23);

        jLabel86.setText("HbsAg :");
        jLabel86.setName("jLabel86"); // NOI18N
        FormInput.add(jLabel86);
        jLabel86.setBounds(430, 1310, 75, 23);

        LabHbsAg.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Non Reaktif", "Reaktif" }));
        LabHbsAg.setName("LabHbsAg"); // NOI18N
        FormInput.add(LabHbsAg);
        LabHbsAg.setBounds(510, 1310, 110, 23);

        jLabel43.setText("Anti HCV :");
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(430, 1340, 75, 23);

        LabAntiHCV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Non Reaktif", "Reaktif" }));
        LabAntiHCV.setName("LabAntiHCV"); // NOI18N
        FormInput.add(LabAntiHCV);
        LabAntiHCV.setBounds(510, 1340, 110, 23);

        LabGds.setFocusTraversalPolicyProvider(true);
        LabGds.setName("LabGds"); // NOI18N
        FormInput.add(LabGds);
        LabGds.setBounds(510, 1280, 110, 23);

        LabPtAptt.setFocusTraversalPolicyProvider(true);
        LabPtAptt.setName("LabPtAptt"); // NOI18N
        FormInput.add(LabPtAptt);
        LabPtAptt.setBounds(510, 1250, 110, 23);

        jLabel96.setText("14. Hasil Pemeriksaan Laboratorium :");
        jLabel96.setName("jLabel96"); // NOI18N
        FormInput.add(jLabel96);
        jLabel96.setBounds(0, 1220, 210, 23);

        LabCr.setFocusTraversalPolicyProvider(true);
        LabCr.setName("LabCr"); // NOI18N
        FormInput.add(LabCr);
        LabCr.setBounds(330, 1280, 110, 23);

        jLabel83.setText("PT/APTT :");
        jLabel83.setName("jLabel83"); // NOI18N
        FormInput.add(jLabel83);
        jLabel83.setBounds(430, 1250, 75, 23);

        jLabel39.setText("GDS :");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(430, 1280, 75, 23);

        jLabel132.setText("Lainnya :");
        jLabel132.setName("jLabel132"); // NOI18N
        FormInput.add(jLabel132);
        jLabel132.setBounds(300, 335, 50, 23);

        StatusPsiko.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tenang", "Takut", "Tempertantrum", "Cemas", "Depresi", "Lain-lain" }));
        StatusPsiko.setName("StatusPsiko"); // NOI18N
        FormInput.add(StatusPsiko);
        StatusPsiko.setBounds(140, 225, 140, 23);

        AlergiKeterangan.setFocusTraversalPolicyProvider(true);
        AlergiKeterangan.setName("AlergiKeterangan"); // NOI18N
        FormInput.add(AlergiKeterangan);
        AlergiKeterangan.setBounds(130, 640, 250, 23);

        jLabel133.setText("4. Sistem Pernapasan :");
        jLabel133.setName("jLabel133"); // NOI18N
        FormInput.add(jLabel133);
        jLabel133.setBounds(0, 335, 140, 23);

        BAB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Hitam", "Darah Segar" }));
        BAB.setName("BAB"); // NOI18N
        FormInput.add(BAB);
        BAB.setBounds(165, 420, 140, 23);

        jLabel14.setText("11. Arteri Dorsalis Pedis :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 880, 150, 23);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel38.setText("a. Double Antiplatelet :");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(50, 540, 120, 23);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("b. Beta Blocker            :");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(50, 570, 120, 23);

        Antiplatelet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Antiplatelet.setName("Antiplatelet"); // NOI18N
        FormInput.add(Antiplatelet);
        Antiplatelet.setBounds(170, 540, 80, 23);

        LamaAntiPlatelet.setFocusTraversalPolicyProvider(true);
        LamaAntiPlatelet.setName("LamaAntiPlatelet"); // NOI18N
        FormInput.add(LamaAntiPlatelet);
        LamaAntiPlatelet.setBounds(370, 540, 200, 23);

        BetaBlocker.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        BetaBlocker.setName("BetaBlocker"); // NOI18N
        FormInput.add(BetaBlocker);
        BetaBlocker.setBounds(170, 570, 80, 23);

        LamaBetaBlocker.setFocusTraversalPolicyProvider(true);
        LamaBetaBlocker.setName("LamaBetaBlocker"); // NOI18N
        FormInput.add(LamaBetaBlocker);
        LamaBetaBlocker.setBounds(370, 570, 200, 23);

        Simarc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Simarc.setName("Simarc"); // NOI18N
        FormInput.add(Simarc);
        Simarc.setBounds(170, 600, 80, 23);

        LamaSimarc.setFocusTraversalPolicyProvider(true);
        LamaSimarc.setName("LamaSimarc"); // NOI18N
        FormInput.add(LamaSimarc);
        LamaSimarc.setBounds(370, 600, 200, 23);

        jLabel52.setText("16. Hasil Echo :");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(0, 1420, 110, 23);

        scrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane6.setName("scrollPane6"); // NOI18N

        EchoKesan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        EchoKesan.setColumns(20);
        EchoKesan.setRows(5);
        EchoKesan.setName("EchoKesan"); // NOI18N
        scrollPane6.setViewportView(EchoKesan);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(60, 1450, 750, 50);

        jLabel41.setText("15. Skrining Jatuh :");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(0, 1390, 130, 23);

        SkriningJatuh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Resiko Rendah", "Resiko Sedang", "Resiko Tinggi" }));
        SkriningJatuh.setName("SkriningJatuh"); // NOI18N
        FormInput.add(SkriningJatuh);
        SkriningJatuh.setBounds(135, 1390, 135, 23);

        SkriningSkor.setFocusTraversalPolicyProvider(true);
        SkriningSkor.setName("SkriningSkor"); // NOI18N
        FormInput.add(SkriningSkor);
        SkriningSkor.setBounds(330, 1390, 50, 23);

        jLabel42.setText("Skor :");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(275, 1390, 50, 23);

        jLabel134.setText("2. Status Psikologis :");
        jLabel134.setName("jLabel134"); // NOI18N
        FormInput.add(jLabel134);
        jLabel134.setBounds(10, 225, 120, 23);

        jLabel135.setText("Lainnya :");
        jLabel135.setName("jLabel135"); // NOI18N
        FormInput.add(jLabel135);
        jLabel135.setBounds(290, 225, 50, 23);

        KetPsiko.setFocusTraversalPolicyProvider(true);
        KetPsiko.setName("KetPsiko"); // NOI18N
        FormInput.add(KetPsiko);
        KetPsiko.setBounds(350, 225, 142, 23);

        Urine24Jam.setFocusTraversalPolicyProvider(true);
        Urine24Jam.setName("Urine24Jam"); // NOI18N
        FormInput.add(Urine24Jam);
        Urine24Jam.setBounds(165, 480, 55, 23);

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setText("Cc");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(225, 480, 50, 23);

        jLabel46.setText("Lama Penggunaan :");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(255, 540, 110, 23);

        jLabel47.setText("Lama Penggunaan :");
        jLabel47.setName("jLabel47"); // NOI18N
        FormInput.add(jLabel47);
        jLabel47.setBounds(255, 570, 110, 23);

        jLabel48.setText("Lama Penggunaan :");
        jLabel48.setName("jLabel48"); // NOI18N
        FormInput.add(jLabel48);
        jLabel48.setBounds(255, 600, 110, 23);

        jLabel53.setText("7. Riwayat Pengobatan :");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(0, 510, 150, 23);

        jLabel54.setText("Radialis Kanan :");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(0, 810, 150, 23);

        RadialisKanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Adekuat", "Adekuat" }));
        RadialisKanan.setName("RadialisKanan"); // NOI18N
        FormInput.add(RadialisKanan);
        RadialisKanan.setBounds(160, 810, 110, 23);

        jLabel55.setText("Radialis Kiri :");
        jLabel55.setName("jLabel55"); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(0, 840, 150, 23);

        RadialisKiri.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Adekuat", "Adekuat" }));
        RadialisKiri.setName("RadialisKiri"); // NOI18N
        FormInput.add(RadialisKiri);
        RadialisKiri.setBounds(160, 840, 110, 23);

        jLabel56.setText("10. Tes Allen :");
        jLabel56.setName("jLabel56"); // NOI18N
        FormInput.add(jLabel56);
        jLabel56.setBounds(0, 780, 100, 23);

        jLabel57.setText("Pedis Kanan :");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput.add(jLabel57);
        jLabel57.setBounds(0, 910, 150, 23);

        jLabel58.setText("Pedis Kiri :");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(0, 940, 150, 23);

        PedisKiri.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Adekuat", "Adekuat" }));
        PedisKiri.setName("PedisKiri"); // NOI18N
        FormInput.add(PedisKiri);
        PedisKiri.setBounds(160, 940, 110, 23);

        PedisKanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Adekuat", "Adekuat" }));
        PedisKanan.setName("PedisKanan"); // NOI18N
        FormInput.add(PedisKanan);
        PedisKanan.setBounds(160, 910, 110, 23);

        PanelWall.setBackground(new java.awt.Color(29, 29, 29));
        PanelWall.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/picture/nyeri.png"))); // NOI18N
        PanelWall.setBackgroundImageType(usu.widget.constan.BackgroundConstan.BACKGROUND_IMAGE_STRECT);
        PanelWall.setPreferredSize(new java.awt.Dimension(200, 200));
        PanelWall.setRound(false);
        PanelWall.setWarna(new java.awt.Color(110, 110, 110));
        PanelWall.setLayout(null);
        FormInput.add(PanelWall);
        PanelWall.setBounds(40, 1010, 320, 115);

        Nyeri.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Ada Nyeri", "Nyeri Akut", "Nyeri Kronis" }));
        Nyeri.setName("Nyeri"); // NOI18N
        FormInput.add(Nyeri);
        Nyeri.setBounds(380, 1010, 130, 23);

        jLabel87.setText("Durasi :");
        jLabel87.setName("jLabel87"); // NOI18N
        FormInput.add(jLabel87);
        jLabel87.setBounds(370, 1040, 45, 23);

        jLabel85.setText("Skala Nyeri :");
        jLabel85.setName("jLabel85"); // NOI18N
        FormInput.add(jLabel85);
        jLabel85.setBounds(510, 1010, 69, 23);

        NyeriLama.setFocusTraversalPolicyProvider(true);
        NyeriLama.setName("NyeriLama"); // NOI18N
        FormInput.add(NyeriLama);
        NyeriLama.setBounds(420, 1040, 150, 23);

        NyeriSkala.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        NyeriSkala.setName("NyeriSkala"); // NOI18N
        FormInput.add(NyeriSkala);
        NyeriSkala.setBounds(580, 1010, 70, 23);

        jLabel90.setText("Lokasi :");
        jLabel90.setName("jLabel90"); // NOI18N
        FormInput.add(jLabel90);
        jLabel90.setBounds(370, 1070, 46, 23);

        NyeriLokasi.setFocusTraversalPolicyProvider(true);
        NyeriLokasi.setName("NyeriLokasi"); // NOI18N
        FormInput.add(NyeriLokasi);
        NyeriLokasi.setBounds(420, 1070, 150, 23);

        KetSistemPernapasan.setFocusTraversalPolicyProvider(true);
        KetSistemPernapasan.setName("KetSistemPernapasan"); // NOI18N
        FormInput.add(KetSistemPernapasan);
        KetSistemPernapasan.setBounds(360, 335, 142, 23);

        jLabel59.setText("8. Riwayat Alergi :");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(0, 640, 120, 23);

        jLabel60.setText("5. Sistem Pencernaan :");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput.add(jLabel60);
        jLabel60.setBounds(0, 365, 140, 23);

        jLabel61.setText("b. BAB                 :");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput.add(jLabel61);
        jLabel61.setBounds(0, 420, 160, 23);

        SistemPernapasan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sesak", "Cuping Hidung", "Normal" }));
        SistemPernapasan.setName("SistemPernapasan"); // NOI18N
        FormInput.add(SistemPernapasan);
        SistemPernapasan.setBounds(150, 335, 140, 23);

        MuntahDarah.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        MuntahDarah.setName("MuntahDarah"); // NOI18N
        FormInput.add(MuntahDarah);
        MuntahDarah.setBounds(165, 390, 140, 23);

        jLabel62.setText("6. Sistem Perkemihan :");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(0, 455, 140, 23);

        jLabel63.setText("a. Urine /24 Jam :");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(0, 480, 160, 23);

        jLabel65.setText("9. Tanda - tanda vital :");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(0, 675, 140, 23);

        jLabel44.setText("12. Keluhan Nyeri :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(0, 980, 120, 23);

        jLabel92.setText("Pencetus :");
        jLabel92.setName("jLabel92"); // NOI18N
        FormInput.add(jLabel92);
        jLabel92.setBounds(600, 1040, 100, 23);

        NyeriPencetus.setFocusTraversalPolicyProvider(true);
        NyeriPencetus.setName("NyeriPencetus"); // NOI18N
        FormInput.add(NyeriPencetus);
        NyeriPencetus.setBounds(710, 1040, 150, 23);

        jLabel91.setText("Kualitas :");
        jLabel91.setName("jLabel91"); // NOI18N
        FormInput.add(jLabel91);
        jLabel91.setBounds(370, 1100, 46, 23);

        NyeriKualitas.setFocusTraversalPolicyProvider(true);
        NyeriKualitas.setName("NyeriKualitas"); // NOI18N
        FormInput.add(NyeriKualitas);
        NyeriKualitas.setBounds(420, 1100, 150, 23);

        jLabel93.setText("Penjalaran :");
        jLabel93.setName("jLabel93"); // NOI18N
        FormInput.add(jLabel93);
        jLabel93.setBounds(600, 1070, 100, 23);

        NyeriPenjalaran.setFocusTraversalPolicyProvider(true);
        NyeriPenjalaran.setName("NyeriPenjalaran"); // NOI18N
        FormInput.add(NyeriPenjalaran);
        NyeriPenjalaran.setBounds(710, 1070, 150, 23);

        jLabel64.setText("13. Kebutuhan Edukasi :");
        jLabel64.setName("jLabel64"); // NOI18N
        FormInput.add(jLabel64);
        jLabel64.setBounds(0, 1135, 150, 23);

        scrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane4.setName("scrollPane4"); // NOI18N

        KebutuhanEdukasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KebutuhanEdukasi.setColumns(20);
        KebutuhanEdukasi.setRows(5);
        KebutuhanEdukasi.setName("KebutuhanEdukasi"); // NOI18N
        scrollPane4.setViewportView(KebutuhanEdukasi);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(50, 1160, 750, 50);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 253)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbMasalahKeperawatan.setName("tbMasalahKeperawatan"); // NOI18N
        tbMasalahKeperawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMasalahKeperawatanMouseClicked(evt);
            }
        });
        tbMasalahKeperawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbMasalahKeperawatanKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbMasalahKeperawatanKeyReleased(evt);
            }
        });
        Scroll6.setViewportView(tbMasalahKeperawatan);

        FormInput.add(Scroll6);
        Scroll6.setBounds(10, 1520, 400, 143);

        label12.setText("Key Word :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(60, 23));
        FormInput.add(label12);
        label12.setBounds(20, 1670, 60, 23);

        BtnTambahMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambahMasalah.setMnemonic('3');
        BtnTambahMasalah.setToolTipText("Alt+3");
        BtnTambahMasalah.setName("BtnTambahMasalah"); // NOI18N
        BtnTambahMasalah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambahMasalah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahMasalahActionPerformed(evt);
            }
        });
        FormInput.add(BtnTambahMasalah);
        BtnTambahMasalah.setBounds(375, 1670, 28, 23);

        jSeparator10.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator10.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator10.setName("jSeparator10"); // NOI18N
        FormInput.add(jSeparator10);
        jSeparator10.setBounds(0, 1510, 880, 1);

        BtnCariMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariMasalah.setMnemonic('1');
        BtnCariMasalah.setToolTipText("Alt+1");
        BtnCariMasalah.setName("BtnCariMasalah"); // NOI18N
        BtnCariMasalah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariMasalah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariMasalahActionPerformed(evt);
            }
        });
        BtnCariMasalah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariMasalahKeyPressed(evt);
            }
        });
        FormInput.add(BtnCariMasalah);
        BtnCariMasalah.setBounds(305, 1670, 28, 23);

        BtnAllMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllMasalah.setMnemonic('2');
        BtnAllMasalah.setToolTipText("2Alt+2");
        BtnAllMasalah.setName("BtnAllMasalah"); // NOI18N
        BtnAllMasalah.setPreferredSize(new java.awt.Dimension(28, 23));
        FormInput.add(BtnAllMasalah);
        BtnAllMasalah.setBounds(340, 1670, 28, 23);

        TCariMasalah.setToolTipText("Alt+C");
        TCariMasalah.setName("TCariMasalah"); // NOI18N
        TCariMasalah.setPreferredSize(new java.awt.Dimension(140, 23));
        TCariMasalah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariMasalahKeyPressed(evt);
            }
        });
        FormInput.add(TCariMasalah);
        TCariMasalah.setBounds(85, 1670, 215, 23);

        TabRencanaKeperawatan.setBackground(new java.awt.Color(255, 255, 254));
        TabRencanaKeperawatan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TabRencanaKeperawatan.setForeground(new java.awt.Color(50, 50, 50));
        TabRencanaKeperawatan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRencanaKeperawatan.setName("TabRencanaKeperawatan"); // NOI18N

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setLayout(new java.awt.BorderLayout());

        Scroll8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 253)));
        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);

        tbRencanaKeperawatan.setName("tbRencanaKeperawatan"); // NOI18N
        Scroll8.setViewportView(tbRencanaKeperawatan);

        panelBiasa1.add(Scroll8, java.awt.BorderLayout.CENTER);

        TabRencanaKeperawatan.addTab("Rencana Keperawatan", panelBiasa1);

        scrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane5.setName("scrollPane5"); // NOI18N

        Rencana.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Rencana.setColumns(20);
        Rencana.setRows(5);
        Rencana.setName("Rencana"); // NOI18N
        Rencana.setOpaque(true);
        scrollPane5.setViewportView(Rencana);

        TabRencanaKeperawatan.addTab("Rencana Keperawatan Lainnya", scrollPane5);

        FormInput.add(TabRencanaKeperawatan);
        TabRencanaKeperawatan.setBounds(440, 1520, 420, 143);

        BtnTambahRencana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambahRencana.setMnemonic('3');
        BtnTambahRencana.setToolTipText("Alt+3");
        BtnTambahRencana.setName("BtnTambahRencana"); // NOI18N
        BtnTambahRencana.setPreferredSize(new java.awt.Dimension(28, 23));
        FormInput.add(BtnTambahRencana);
        BtnTambahRencana.setBounds(815, 1670, 28, 23);

        BtnAllRencana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllRencana.setMnemonic('2');
        BtnAllRencana.setToolTipText("2Alt+2");
        BtnAllRencana.setName("BtnAllRencana"); // NOI18N
        BtnAllRencana.setPreferredSize(new java.awt.Dimension(28, 23));
        FormInput.add(BtnAllRencana);
        BtnAllRencana.setBounds(780, 1670, 28, 23);

        BtnCariRencana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariRencana.setMnemonic('1');
        BtnCariRencana.setToolTipText("Alt+1");
        BtnCariRencana.setName("BtnCariRencana"); // NOI18N
        BtnCariRencana.setPreferredSize(new java.awt.Dimension(28, 23));
        FormInput.add(BtnCariRencana);
        BtnCariRencana.setBounds(745, 1670, 28, 23);

        label13.setText("Key Word :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(60, 23));
        FormInput.add(label13);
        label13.setBounds(440, 1670, 60, 23);

        TCariRencana.setToolTipText("Alt+C");
        TCariRencana.setName("TCariRencana"); // NOI18N
        TCariRencana.setPreferredSize(new java.awt.Dimension(215, 23));
        FormInput.add(TCariRencana);
        TCariRencana.setBounds(505, 1670, 235, 23);

        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setName("TglAsuhan"); // NOI18N
        FormInput.add(TglAsuhan);
        TglAsuhan.setBounds(595, 40, 129, 23);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Pengkajian", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tgl.Asuhan :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(195, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
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
        panelGlass9.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(470, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout(1, 1));

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
        ChkAccor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccorActionPerformed(evt);
            }
        });
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.WEST);

        FormMenu.setBackground(new java.awt.Color(255, 255, 255));
        FormMenu.setBorder(null);
        FormMenu.setName("FormMenu"); // NOI18N
        FormMenu.setPreferredSize(new java.awt.Dimension(115, 43));
        FormMenu.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        jLabel1.setText("Pasien :");
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(55, 23));
        FormMenu.add(jLabel1);

        TNoRM1.setEditable(false);
        TNoRM1.setName("TNoRM1"); // NOI18N
        TNoRM1.setPreferredSize(new java.awt.Dimension(100, 23));
        FormMenu.add(TNoRM1);

        TPasien1.setEditable(false);
        TPasien1.setBackground(new java.awt.Color(245, 250, 240));
        TPasien1.setName("TPasien1"); // NOI18N
        TPasien1.setPreferredSize(new java.awt.Dimension(250, 23));
        FormMenu.add(TPasien1);

        BtnPrint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item (copy).png"))); // NOI18N
        BtnPrint1.setMnemonic('P');
        BtnPrint1.setToolTipText("Alt+P");
        BtnPrint1.setName("BtnPrint1"); // NOI18N
        BtnPrint1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPrint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrint1ActionPerformed(evt);
            }
        });
        FormMenu.add(BtnPrint1);

        PanelAccor.add(FormMenu, java.awt.BorderLayout.NORTH);

        FormMasalahRencana.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 254)));
        FormMasalahRencana.setName("FormMasalahRencana"); // NOI18N
        FormMasalahRencana.setLayout(new java.awt.GridLayout(3, 0, 1, 1));

        Scroll7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 254)));
        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);

        tbMasalahDetail.setName("tbMasalahDetail"); // NOI18N
        Scroll7.setViewportView(tbMasalahDetail);

        FormMasalahRencana.add(Scroll7);

        Scroll9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 254)));
        Scroll9.setName("Scroll9"); // NOI18N
        Scroll9.setOpaque(true);

        tbRencanaDetail.setName("tbRencanaDetail"); // NOI18N
        Scroll9.setViewportView(tbRencanaDetail);

        FormMasalahRencana.add(Scroll9);

        ScrollRencana.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 254)), "Rencana Keperawatan Lainnya :", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        ScrollRencana.setName("ScrollRencana"); // NOI18N

        DetailRencana.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        DetailRencana.setColumns(20);
        DetailRencana.setRows(5);
        DetailRencana.setName("DetailRencana"); // NOI18N
        ScrollRencana.setViewportView(DetailRencana);

        FormMasalahRencana.add(ScrollRencana);

        PanelAccor.add(FormMasalahRencana, java.awt.BorderLayout.CENTER);

        internalFrame3.add(PanelAccor, java.awt.BorderLayout.EAST);

        TabRawat.addTab("Data Pengkajian", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, BtnDokter);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Nama Pasien");
        } else if (nmptg.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else if (RencanaTindakan.getText().trim().equals("")) {
            Valid.textKosong(RencanaTindakan, "Rencana Tindakan");
        } else {
            if (akses.getkode().equals("Admin Utama")) {
                simpan();
            } else {
                if (Sequel.cekTanggalRegistrasiSmc(TNoRw.getText(), TglAsuhan.getDate())) {
                    simpan();
                }
            }
        }

    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnTambahRencana, BtnBatal);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() >= 0) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (kdptg.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
                    if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(), Sequel.ambiltanggalsekarang()) == true) {
                        hapus();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }

    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Nama Pasien");
        } else if (nmptg.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else if (RencanaTindakan.getText().trim().equals("")) {
            Valid.textKosong(RencanaTindakan, "Rencana Tindakan");
        } else {
            if (tbObat.getSelectedRow() >= 0) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (kdptg.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
                        if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(), Sequel.ambiltanggalsekarang())) {
                            ganti();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            try {
                htmlContent = new StringBuilder();
                htmlContent.append(
                    "<tr class='isi'>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.Rawat</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.RM</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Pasien</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tgl.Lahir</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>J.K.</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>NIP</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Petugas</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tgl.Operasi</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Diagnosa</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Rencana Tindakan</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TB</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>BB</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TD</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>IO2</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nadi</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Pernapasan</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Suhu</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Cardiovasculer</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Paru</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Abdomen</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Extrimitas</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Endokrin</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Ginjal</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Obat-obatan</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Laborat</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asesmen Fisik Penunjang</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Penyakit Alergi Obat</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Penyakit Alergi Lainnya</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Penyakit Terapi</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Kebiasaan Merokok</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Jml.Rokok</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Kebiasaan Alkohol</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Jml.Alko</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Penggunaan Obat</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Obat Dikonsumsi</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Medis Cardiovasculer</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Medis Respiratory</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Medis Endocrine</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat Medis Lainnya</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Angka ASA</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Mulai Puasa</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Rencana Anestesi</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Rencana Perawatan</b></td>" +
                    "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Catatan Khusus</b></td>" +
                    "</tr>"
                );

                for (int i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 0).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 1).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 2).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 3).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 4).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 5).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 6).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 7).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 8).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 9).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 10).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 11).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 12).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 13).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 14).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 15).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 16).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 17).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 18).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 19).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 20).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 21).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 22).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 23).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 24).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 25).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 26).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 27).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 28).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 29).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 30).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 31).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 32).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 33).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 34).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 35).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 36).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 37).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 38).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 39).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 40).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 41).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 42).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 43).toString() + "</td>" +
                        "<td valign='top'>" + tbObat.getValueAt(i, 44).toString() + "</td>" +
                        "</tr>");
                }

                LoadHTML.setText(
                    "<html>" +
                    "<table width='4500px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>" +
                    htmlContent.toString() +
                    "</table>" +
                    "</html>"
                );

                File g = new File("file2.css");
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}" +
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}" +
                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}" +
                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}" +
                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}" +
                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataPenilaianPreAnestesi.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>", "<head>" +
                    "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />" +
                    "<table width='4500px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>" +
                    "<tr class='isi2'>" +
                    "<td valign='top' align='center'>" +
                    "<font size='4' face='Tahoma'>" + akses.getnamars() + "</font><br>" +
                    akses.getalamatrs() + ", " + akses.getkabupatenrs() + ", " + akses.getpropinsirs() + "<br>" +
                    akses.getkontakrs() + ", E-mail : " + akses.getemailrs() + "<br><br>" +
                    "<font size='2' face='Tahoma'>DATA PENGKAJIAN PRE ANESTESI<br><br></font>" +
                    "</td>" +
                    "</tr>" +
                    "</table>")
                );
                bw.close();
                Desktop.getDesktop().browse(f.toURI());

            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

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
        tampilSmc();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampilSmc();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampilSmc();
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                ChkAccor.setSelected(true);
                isMenu();
                getMasalah();
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            if ((evt.getClickCount() == 2) && (tbObat.getSelectedColumn() == 0)) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    ChkAccor.setSelected(true);
                    isMenu();
                    getMasalah();
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    getData();
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas = new DlgCariPetugas(null, false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (petugas.getTable().getSelectedRow() != -1) {
                        kdptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        nmptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    }
                    BtnDokter.requestFocus();
                    petugas = null;
                }
            });
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
        if (petugas == null) {
            return;
        }
        petugas.isCek();
        if (petugas.isVisible()) {
            petugas.toFront();
            return;
        }
        petugas.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        //Valid.pindah(evt,Edukasi,Hubungan);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void TglAsuhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglAsuhanKeyPressed
        Valid.pindah(evt, BtnDokter, Diagnosa);
    }//GEN-LAST:event_TglAsuhanKeyPressed

    private void DetailRencanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetailRencanaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DetailRencanaKeyPressed

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            isMenu();
        } else {
            ChkAccor.setSelected(false);
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau ditampilkan...!!!!");
        }
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void BtnPrint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrint1ActionPerformed
        MnPenilaianMedisActionPerformed(evt);
    }//GEN-LAST:event_BtnPrint1ActionPerformed

    private void MnPenilaianMedisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPenilaianMedisActionPerformed
        if (tbObat.getSelectedRow() >= 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() : finger) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()));

            String no_rawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
            String tanggal = tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString();
            String masalah = Sequel.cariIsi("select group_concat(smc_master_masalah_keperawatan.nama_masalah separator '; ') from smc_pengkajian_tindakan_invasif_non_bedah_masalah inner join smc_master_masalah_keperawatan on smc_master_masalah_keperawatan.kode_masalah=smc_pengkajian_tindakan_invasif_non_bedah_masalah.kode_masalah where smc_pengkajian_tindakan_invasif_non_bedah_masalah.no_rawat=?", no_rawat);
            String rencana_kep = Sequel.cariIsi("select group_concat(smc_master_rencana_keperawatan.rencana_keperawatan separator '; ') from smc_pengkajian_tindakan_invasif_non_bedah_rencana inner join smc_master_rencana_keperawatan on smc_master_rencana_keperawatan.kode_rencana=smc_pengkajian_tindakan_invasif_non_bedah_rencana.kode_rencana where smc_pengkajian_tindakan_invasif_non_bedah_rencana.no_rawat=?", no_rawat);
            param.put("masalah", masalah == null ? "" : masalah);
            param.put("rencana_kep", rencana_kep == null ? "" : rencana_kep);
            param.put("nyeri", Sequel.cariGambar("select gambar.nyeri from gambar"));

            Valid.MyReportqry("rptCetakPenilaianTindakanInvasifNonBedah.jasper", "report", "::[ Laporan Penilaian Tindakan Invasif Non Bedah ]::",
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir," +
                "smc_pengkajian_tindakan_invasif_non_bedah.tanggal,smc_pengkajian_tindakan_invasif_non_bedah.nip,smc_pengkajian_tindakan_invasif_non_bedah.diagnosa," +
                "smc_pengkajian_tindakan_invasif_non_bedah.rencana_tindakan,smc_pengkajian_tindakan_invasif_non_bedah.status_fungsional,smc_pengkajian_tindakan_invasif_non_bedah.keluhan_utama," +
                "smc_pengkajian_tindakan_invasif_non_bedah.status_psiko,smc_pengkajian_tindakan_invasif_non_bedah.ket_psiko," +
                "smc_pengkajian_tindakan_invasif_non_bedah.rpd," +
                "smc_pengkajian_tindakan_invasif_non_bedah.sistem_pernapasan,smc_pengkajian_tindakan_invasif_non_bedah.ket_sistem_pernapasan," +
                "smc_pengkajian_tindakan_invasif_non_bedah.muntah_darah,smc_pengkajian_tindakan_invasif_non_bedah.bab," +
                "smc_pengkajian_tindakan_invasif_non_bedah.urine," +
                "smc_pengkajian_tindakan_invasif_non_bedah.antiplatelet,smc_pengkajian_tindakan_invasif_non_bedah.lama_antiplatelet," +
                "smc_pengkajian_tindakan_invasif_non_bedah.beta_blocker,smc_pengkajian_tindakan_invasif_non_bedah.lama_beta_blocker," +
                "smc_pengkajian_tindakan_invasif_non_bedah.simarc,smc_pengkajian_tindakan_invasif_non_bedah.lama_simarc," +
                "smc_pengkajian_tindakan_invasif_non_bedah.riwayat_alergi," +
                "smc_pengkajian_tindakan_invasif_non_bedah.tb,smc_pengkajian_tindakan_invasif_non_bedah.bb,smc_pengkajian_tindakan_invasif_non_bedah.td," +
                "smc_pengkajian_tindakan_invasif_non_bedah.io2,smc_pengkajian_tindakan_invasif_non_bedah.nadi,smc_pengkajian_tindakan_invasif_non_bedah.suhu," +
                "smc_pengkajian_tindakan_invasif_non_bedah.pernapasan," +
                "smc_pengkajian_tindakan_invasif_non_bedah.radialis_kanan,smc_pengkajian_tindakan_invasif_non_bedah.radialis_kiri," +
                "smc_pengkajian_tindakan_invasif_non_bedah.pedis_kanan,smc_pengkajian_tindakan_invasif_non_bedah.pedis_kiri," +
                "smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri,smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri_pencetus," +
                "smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri_kualitas,smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri_lokasi," +
                "smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri_penjalaran,smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri_skala," +
                "smc_pengkajian_tindakan_invasif_non_bedah.penilaian_nyeri_durasi,smc_pengkajian_tindakan_invasif_non_bedah.kebutuhan_edukasi," +
                "smc_pengkajian_tindakan_invasif_non_bedah.hematokrit,smc_pengkajian_tindakan_invasif_non_bedah.hemoglobin,smc_pengkajian_tindakan_invasif_non_bedah.leukosit," +
                "smc_pengkajian_tindakan_invasif_non_bedah.pt_ir,smc_pengkajian_tindakan_invasif_non_bedah.kalium,smc_pengkajian_tindakan_invasif_non_bedah.natrium," +
                "smc_pengkajian_tindakan_invasif_non_bedah.ureum,smc_pengkajian_tindakan_invasif_non_bedah.hbsag,smc_pengkajian_tindakan_invasif_non_bedah.anti_hcv," +
                "smc_pengkajian_tindakan_invasif_non_bedah.gds,smc_pengkajian_tindakan_invasif_non_bedah.pt_aptt,smc_pengkajian_tindakan_invasif_non_bedah.kreatinin," +
                "smc_pengkajian_tindakan_invasif_non_bedah.skrining_jatuh,smc_pengkajian_tindakan_invasif_non_bedah.skor_resiko_jatuh," +
                "smc_pengkajian_tindakan_invasif_non_bedah.hasil_echo,smc_pengkajian_tindakan_invasif_non_bedah.rencana," +
                "petugas.nama " +
                "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                "inner join smc_pengkajian_tindakan_invasif_non_bedah on reg_periksa.no_rawat=smc_pengkajian_tindakan_invasif_non_bedah.no_rawat " +
                "inner join petugas on smc_pengkajian_tindakan_invasif_non_bedah.nip=petugas.nip where smc_pengkajian_tindakan_invasif_non_bedah.no_rawat='" + no_rawat + "' " +
                "and smc_pengkajian_tindakan_invasif_non_bedah.tanggal='" + tanggal + "'", param);
        }
    }//GEN-LAST:event_MnPenilaianMedisActionPerformed

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        Valid.pindah(evt, TglLahir, RencanaTindakan);
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void RencanaTindakanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RencanaTindakanKeyPressed
        Valid.pindah(evt, Diagnosa, TB);
    }//GEN-LAST:event_RencanaTindakanKeyPressed

    private void tbMasalahKeperawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMasalahKeperawatanMouseClicked
        if (tabModeMasalah.getRowCount() != 0) {
            try {
                tampilRencana();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbMasalahKeperawatanMouseClicked

    private void tbMasalahKeperawatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbMasalahKeperawatanKeyPressed
        if (tabModeMasalah.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
                TCariMasalah.setText("");
                TCariMasalah.requestFocus();
            }
        }
    }//GEN-LAST:event_tbMasalahKeperawatanKeyPressed

    private void tbMasalahKeperawatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbMasalahKeperawatanKeyReleased
        if (tabModeMasalah.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    tampilRencana();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbMasalahKeperawatanKeyReleased

    private void BtnTambahMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahMasalahActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        MasterMasalahKeperawatanSMC form = new MasterMasalahKeperawatanSMC(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahMasalahActionPerformed

    private void BtnAllMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllMasalahActionPerformed
        TCari.setText("");
        tampilMasalah();
    }//GEN-LAST:event_BtnAllMasalahActionPerformed

    private void BtnAllMasalahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllMasalahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllMasalahActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCariMasalah, TCariMasalah);
        }
    }//GEN-LAST:event_BtnAllMasalahKeyPressed

    private void BtnCariMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariMasalahActionPerformed
        tampilMasalah();
    }//GEN-LAST:event_BtnCariMasalahActionPerformed

    private void BtnCariMasalahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariMasalahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilMasalah();
        } else if ((evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) || (evt.getKeyCode() == KeyEvent.VK_TAB)) {
            Rencana.requestFocus();
        }
    }//GEN-LAST:event_BtnCariMasalahKeyPressed

    private void TCariMasalahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariMasalahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilMasalah();
        } else if ((evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) || (evt.getKeyCode() == KeyEvent.VK_TAB)) {
            Rencana.requestFocus();
        }
    }//GEN-LAST:event_TCariMasalahKeyPressed

    private void BtnTambahRencanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahRencanaActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        MasterRencanaKeperawatanSMC form = new MasterRencanaKeperawatanSMC(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahRencanaActionPerformed

    private void BtnAllRencanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllRencanaActionPerformed
        TCariRencana.setText("");
        tampilRencana();
    }//GEN-LAST:event_BtnAllRencanaActionPerformed

    private void BtnAllRencanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllRencanaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllRencanaActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCariRencana, TCariRencana);
        }
    }//GEN-LAST:event_BtnAllRencanaKeyPressed

    private void BtnCariRencanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariRencanaActionPerformed
        tampilRencana();
    }//GEN-LAST:event_BtnCariRencanaActionPerformed

    private void BtnCariRencanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariRencanaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilRencana();
        } else if ((evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) || (evt.getKeyCode() == KeyEvent.VK_TAB)) {
            BtnSimpan.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            TCariRencana.requestFocus();
        }
    }//GEN-LAST:event_BtnCariRencanaKeyPressed

    private void TCariRencanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariRencanaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilRencana();
        } else if ((evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) || (evt.getKeyCode() == KeyEvent.VK_TAB)) {
            BtnCariRencana.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            TCariMasalah.requestFocus();
        }
    }//GEN-LAST:event_TCariRencanaKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampilMasalah();
        tampilRencana();
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilSmc();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilSmc();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilSmc();
                    }
                }
            });

            TCariMasalah.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCariMasalah.getText().length() > 2) {
                        tampilMasalah();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCariMasalah.getText().length() > 2) {
                        tampilMasalah();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCariMasalah.getText().length() > 2) {
                        tampilMasalah();
                    }
                }
            });

            TCariRencana.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCariRencana.getText().length() > 2) {
                        tampilRencana();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCariRencana.getText().length() > 2) {
                        tampilRencana();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCariRencana.getText().length() > 2) {
                        tampilRencana();
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMPenilaianPreAnastesi dialog = new RMPenilaianPreAnastesi(new javax.swing.JFrame(), true);
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
    private widget.TextBox AlergiKeterangan;
    private widget.ComboBox Antiplatelet;
    private widget.ComboBox BAB;
    private widget.TextBox BB;
    private widget.ComboBox BetaBlocker;
    private widget.Button BtnAll;
    private widget.Button BtnAllMasalah;
    private widget.Button BtnAllRencana;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCariMasalah;
    private widget.Button BtnCariRencana;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnPrint1;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambahMasalah;
    private widget.Button BtnTambahRencana;
    private widget.CekBox ChkAccor;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextArea DetailRencana;
    private widget.TextBox Diagnosa;
    private widget.TextArea EchoKesan;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormMasalahRencana;
    private widget.PanelBiasa FormMenu;
    private widget.TextBox IO2;
    private widget.TextBox Jk;
    private widget.TextArea KebutuhanEdukasi;
    private widget.TextArea KeluhanUtama;
    private widget.TextBox KetPsiko;
    private widget.TextBox KetSistemPernapasan;
    private widget.Label LCount;
    private widget.ComboBox LabAntiHCV;
    private widget.TextBox LabCr;
    private widget.TextBox LabGds;
    private widget.TextBox LabHb;
    private widget.ComboBox LabHbsAg;
    private widget.TextBox LabHt;
    private widget.TextBox LabK;
    private widget.TextBox LabLeukosit;
    private widget.TextBox LabNa;
    private widget.TextBox LabPtAptt;
    private widget.TextBox LabPtIr;
    private widget.TextBox LabUr;
    private widget.TextBox LamaAntiPlatelet;
    private widget.TextBox LamaBetaBlocker;
    private widget.TextBox LamaSimarc;
    private widget.editorpane LoadHTML;
    private widget.ComboBox MuntahDarah;
    private widget.TextBox Nadi;
    private widget.ComboBox Nyeri;
    private widget.TextBox NyeriKualitas;
    private widget.TextBox NyeriLama;
    private widget.TextBox NyeriLokasi;
    private widget.TextBox NyeriPencetus;
    private widget.TextBox NyeriPenjalaran;
    private widget.ComboBox NyeriSkala;
    private widget.PanelBiasa PanelAccor;
    private usu.widget.glass.PanelGlass PanelWall;
    private widget.ComboBox PedisKanan;
    private widget.ComboBox PedisKiri;
    private widget.TextBox Pernapasan;
    private widget.TextArea RPD;
    private widget.ComboBox RadialisKanan;
    private widget.ComboBox RadialisKiri;
    private widget.TextArea Rencana;
    private widget.TextBox RencanaTindakan;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.ScrollPane Scroll9;
    private widget.ScrollPane ScrollRencana;
    private widget.ComboBox Simarc;
    private widget.ComboBox SistemPernapasan;
    private widget.ComboBox SkriningJatuh;
    private widget.TextBox SkriningSkor;
    private widget.TextBox StatusFungsional;
    private widget.ComboBox StatusPsiko;
    private widget.TextBox Suhu;
    private widget.TextBox TB;
    private widget.TextBox TCari;
    private widget.TextBox TCariMasalah;
    private widget.TextBox TCariRencana;
    private widget.TextBox TD;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRM1;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TPasien1;
    private javax.swing.JTabbedPane TabRawat;
    private javax.swing.JTabbedPane TabRencanaKeperawatan;
    private widget.TextBox TanggalRegistrasi;
    private widget.Tanggal TglAsuhan;
    private widget.TextBox TglLahir;
    private widget.TextBox Urine24Jam;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel112;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel132;
    private widget.Label jLabel133;
    private widget.Label jLabel134;
    private widget.Label jLabel135;
    private widget.Label jLabel14;
    private widget.Label jLabel14_StatusFungsional;
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
    private widget.Label jLabel65;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel83;
    private widget.Label jLabel84;
    private widget.Label jLabel85;
    private widget.Label jLabel86;
    private widget.Label jLabel87;
    private widget.Label jLabel9;
    private widget.Label jLabel90;
    private widget.Label jLabel91;
    private widget.Label jLabel92;
    private widget.Label jLabel93;
    private widget.Label jLabel96;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private widget.TextBox kdptg;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.TextBox nmptg;
    private widget.PanelBiasa panelBiasa1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.Table tbMasalahDetail;
    private widget.Table tbMasalahKeperawatan;
    private widget.Table tbObat;
    private widget.Table tbRencanaDetail;
    private widget.Table tbRencanaKeperawatan;
    // End of variables declaration//GEN-END:variables

    private void tampilSmc() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode);
            new SwingWorker<Void, Object[]>() {
                final String cari = TCari.getText().trim();
                final String tgl1 = Valid.getTglSmc(DTPCari1);
                final String tgl2 = Valid.getTglSmc(DTPCari2);

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select s.no_rawat, s.tanggal, s.nip, s.diagnosa, s.rencana_tindakan, s.status_fungsional, s.keluhan_utama, s.status_psiko, s.ket_psiko, s.rpd, s.sistem_pernapasan, " +
                        "s.ket_sistem_pernapasan, s.muntah_darah, s.bab, s.urine, s.antiplatelet, s.lama_antiplatelet, s.beta_blocker, s.lama_beta_blocker, s.simarc, s.lama_simarc, s.riwayat_alergi, " +
                        "s.tb, s.bb, s.td, s.io2, s.nadi, s.suhu, s.pernapasan, s.radialis_kanan, s.radialis_kiri, s.pedis_kanan, s.pedis_kiri, s.penilaian_nyeri, s.penilaian_nyeri_pencetus, " +
                        "s.penilaian_nyeri_kualitas, s.penilaian_nyeri_lokasi, s.penilaian_nyeri_penjalaran, s.penilaian_nyeri_skala, s.penilaian_nyeri_durasi, s.kebutuhan_edukasi, s.hematokrit, " +
                        "s.hemoglobin, s.leukosit, s.pt_ir, s.kalium, s.natrium, s.ureum, s.hbsag, s.anti_hcv, s.gds, s.pt_aptt, s.kreatinin, s.skrining_jatuh, s.skor_resiko_jatuh, s.hasil_echo, " +
                        "s.rencana, px.tgl_lahir, px.jk, petugas.nama, r.no_rkm_medis, px.nm_pasien, px.agama, px.pnd, pj.png_jawab, b.nama_bahasa from reg_periksa r inner join pasien px on " +
                        "r.no_rkm_medis = px.no_rkm_medis inner join smc_pengkajian_tindakan_invasif_non_bedah s on r.no_rawat = s.no_rawat inner join petugas p on s.nip = petugas.nip " +
                        "inner join bahasa_pasien b on px.bahasa_pasien = b.id inner join penjab pj on r.kd_pj = pj.kd_pj where s.tanggal between ? and ? " + (cari.isBlank() ? "" :
                        "and (r.no_rawat like ? or px.no_rkm_medis like ? or px.nm_pasien like ? or s.nip like ? or petugas.nama like ?) ") + "order by s.tanggal"
                    )) {
                        int p = 0;
                        ps.setString(++p, tgl1);
                        ps.setString(++p, tgl2);
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                publish(new Object[] {
                                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("tgl_lahir"), rs.getString("jk"), rs.getString("nip"), rs.getString("nama"),
                                    rs.getString("tanggal"), rs.getString("diagnosa"), rs.getString("rencana_tindakan"), rs.getString("status_fungsional"), rs.getString("keluhan_utama"), rs.getString("status_psiko"),
                                    rs.getString("ket_psiko"), rs.getString("rpd"), rs.getString("sistem_pernapasan"), rs.getString("ket_sistem_pernapasan"), rs.getString("muntah_darah"), rs.getString("bab"),
                                    rs.getString("urine"), rs.getString("antiplatelet"), rs.getString("lama_antiplatelet"), rs.getString("beta_blocker"), rs.getString("lama_beta_blocker"), rs.getString("simarc"),
                                    rs.getString("lama_simarc"), rs.getString("riwayat_alergi"), rs.getString("tb"), rs.getString("bb"), rs.getString("td"), rs.getString("io2"), rs.getString("nadi"), rs.getString("suhu"),
                                    rs.getString("pernapasan"), rs.getString("radialis_kanan"), rs.getString("radialis_kiri"), rs.getString("pedis_kanan"), rs.getString("pedis_kiri"), rs.getString("penilaian_nyeri"),
                                    rs.getString("penilaian_nyeri_pencetus"), rs.getString("penilaian_nyeri_kualitas"), rs.getString("penilaian_nyeri_lokasi"), rs.getString("penilaian_nyeri_penjalaran"),
                                    rs.getString("penilaian_nyeri_skala"), rs.getString("penilaian_nyeri_durasi"), rs.getString("kebutuhan_edukasi"), rs.getString("hematokrit"), rs.getString("hemoglobin"),
                                    rs.getString("leukosit"), rs.getString("pt_ir"), rs.getString("kalium"), rs.getString("natrium"), rs.getString("ureum"), rs.getString("hbsag"), rs.getString("anti_hcv"),
                                    rs.getString("gds"), rs.getString("pt_aptt"), rs.getString("kreatinin"), rs.getString("skrining_jatuh"), rs.getString("skor_resiko_jatuh"), rs.getString("hasil_echo"),
                                    rs.getString("rencana")
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
                    tabMode.fireTableDataChanged();
                    LCount.setText(tabMode.getRowCount() + "");
                    RMPenilaianTindakanInvasifNonBedahSMC.this.setCursor(Cursor.getDefaultCursor());
                }
            }.execute();
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        Diagnosa.setText("");
        RencanaTindakan.setText("");
        StatusFungsional.setText("");
        KeluhanUtama.setText("");
        StatusPsiko.setSelectedIndex(0);
        KetPsiko.setText("");
        RPD.setText("");
        SistemPernapasan.setSelectedIndex(0);
        KetSistemPernapasan.setText("");
        MuntahDarah.setSelectedIndex(0);
        BAB.setSelectedIndex(0);
        Urine24Jam.setText("");
        Antiplatelet.setSelectedIndex(0);
        LamaAntiPlatelet.setText("");
        BetaBlocker.setSelectedIndex(0);
        LamaBetaBlocker.setText("");
        Simarc.setSelectedIndex(0);
        LamaSimarc.setText("");
        AlergiKeterangan.setText("");
        TB.setText("");
        BB.setText("");
        TD.setText("");
        IO2.setText("");
        Nadi.setText("");
        Suhu.setText("");
        Pernapasan.setText("");
        RadialisKanan.setSelectedIndex(0);
        RadialisKiri.setSelectedIndex(0);
        PedisKanan.setSelectedIndex(0);
        PedisKiri.setSelectedIndex(0);
        Nyeri.setSelectedIndex(0);
        NyeriPencetus.setText("");
        NyeriKualitas.setText("");
        NyeriLokasi.setText("");
        NyeriPenjalaran.setText("");
        NyeriSkala.setSelectedIndex(0);
        NyeriLama.setText("");
        KebutuhanEdukasi.setText("");
        LabHb.setText("");
        LabHt.setText("");
        LabLeukosit.setText("");
        LabPtIr.setText("");
        LabK.setText("");
        LabNa.setText("");
        LabHbsAg.setSelectedIndex(0);
        LabAntiHCV.setSelectedIndex(0);
        LabGds.setText("");
        LabPtAptt.setText("");
        LabUr.setText("");
        LabCr.setText("");
        SkriningJatuh.setSelectedIndex(0);
        SkriningSkor.setText("");
        EchoKesan.setText("");
        Rencana.setText("");
        DetailRencana.setText("");
        TabRawat.setSelectedIndex(0);
        Diagnosa.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() >= 0) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            kdptg.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            nmptg.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Valid.SetTgl(TglAsuhan, tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Diagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            RencanaTindakan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            StatusFungsional.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            KeluhanUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            StatusPsiko.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            KetPsiko.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            RPD.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            SistemPernapasan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            KetSistemPernapasan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            MuntahDarah.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            BAB.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            Urine24Jam.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            Antiplatelet.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            LamaAntiPlatelet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            BetaBlocker.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            LamaBetaBlocker.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());
            Simarc.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 24).toString());
            LamaSimarc.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 25).toString());
            AlergiKeterangan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 26).toString());
            TB.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
            BB.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 28).toString());
            TD.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 29).toString());
            IO2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 30).toString());
            Nadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 31).toString());
            Suhu.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString());
            Pernapasan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 33).toString());
            RadialisKanan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 34).toString());
            RadialisKiri.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 35).toString());
            PedisKanan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString());
            PedisKiri.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 37).toString());
            Nyeri.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 38).toString());
            NyeriPencetus.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 39).toString());
            NyeriKualitas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 40).toString());
            NyeriLokasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 41).toString());
            NyeriPenjalaran.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 42).toString());
            NyeriSkala.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 43).toString());
            NyeriLama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 44).toString());
            KebutuhanEdukasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 45).toString());
            LabHt.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 46).toString());
            LabHb.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 47).toString());
            LabLeukosit.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 48).toString());
            LabPtIr.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 49).toString());
            LabK.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 50).toString());
            LabNa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 51).toString());
            LabUr.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 52).toString());
            LabHbsAg.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 53).toString());
            LabAntiHCV.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 54).toString());
            LabGds.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 55).toString());
            LabPtAptt.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 56).toString());
            LabCr.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 57).toString());
            SkriningJatuh.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 58).toString());
            SkriningSkor.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 59).toString());
            EchoKesan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 60).toString());
            Rencana.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 61).toString());
            Valid.tabelKosong(tabModeMasalah);
            Valid.tabelKosong(tabModeRencana);
            for (int i = 0; i < tbMasalahDetail.getRowCount(); i++) {
                tabModeMasalah.addRow(new Object[] {
                    true, tbMasalahDetail.getValueAt(i, 0).toString(), tbMasalahDetail.getValueAt(i, 1).toString()
                });
            }
            for (int i = 0; i < tbRencanaDetail.getRowCount(); i++) {
                tabModeRencana.addRow(new Object[] {
                    true, tbRencanaDetail.getValueAt(i, 0).toString(), tbRencanaDetail.getValueAt(i, 1).toString()
                });
            }
        }
    }

    private void isRawat() {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select reg_periksa.no_rkm_medis, pasien.nm_pasien, if(pasien.jk = 'L', 'Laki-Laki', 'Perempuan') as jk, pasien.tgl_lahir, reg_periksa.tgl_registrasi, " +
            "reg_periksa.jam_reg from reg_periksa inner join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where reg_periksa.no_rawat = ?"
        )) {
            ps.setString(1, TNoRw.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi") + " " + rs.getString("jam_reg"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        tampilSmc();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpengkajian_invasif_non_bedah_smc());
        BtnHapus.setEnabled(akses.getpengkajian_invasif_non_bedah_smc());
        BtnEdit.setEnabled(akses.getpengkajian_invasif_non_bedah_smc());
        if (akses.getjml2() >= 1) {
            kdptg.setEditable(false);
            BtnDokter.setEnabled(false);
            kdptg.setText(akses.getkode());
            nmptg.setText(Sequel.CariPetugas(kdptg.getText()));
            if (nmptg.getText().equals("")) {
                kdptg.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }

        if (TANGGALMUNDUR.equals("no")) {
            if (!akses.getkode().equals("Admin Utama")) {
                TglAsuhan.setEditable(false);
                TglAsuhan.setEnabled(false);
            }
        }
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
    }

    private void isMenu() {
        int h = internalFrame3.getHeight() > 0 ? internalFrame3.getHeight() : 300;
        if (ChkAccor.isSelected()) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(470, h));
            FormMenu.setVisible(true);
            FormMasalahRencana.setVisible(true);
            ChkAccor.setVisible(true);
        } else {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, h));
            FormMenu.setVisible(false);
            FormMasalahRencana.setVisible(false);
            ChkAccor.setVisible(true);
        }
        internalFrame3.invalidate();
        internalFrame3.validate();
    }

    private void getMasalah() {
        if (tbObat.getSelectedRow() >= 0) {
            try {
                TNoRM1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                TPasien1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
                DetailRencana.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 61).toString());
                Valid.tabelKosong(tabModeDetailMasalah);
                Valid.tabelKosong(tabModeDetailRencana);

                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select m.kode_masalah, m.nama_masalah from smc_master_masalah_keperawatan m inner join " +
                    "smc_pengkajian_tindakan_invasif_non_bedah_masalah s on m.kode_masalah = s.kode_masalah " +
                    "where s.no_rawat = ? order by s.kode_masalah"
                )) {
                    ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            tabModeDetailMasalah.addRow(new Object[] {rs.getString(1), rs.getString(2)});
                        }
                    }
                }

                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select m.kode_rencana, m.rencana_keperawatan from smc_master_rencana_keperawatan m inner join " +
                    "smc_pengkajian_tindakan_invasif_non_bedah_rencana s on m.kode_rencana = s.kode_rencana where " +
                    "s.no_rawat = ? order by s.kode_rencana"
                )) {
                    ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            tabModeDetailRencana.addRow(new Object[] {rs.getString(1), rs.getString(2)});
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void tampilMasalah() {
        try {
            final String cari = TCariMasalah.getText().toLowerCase().trim();
            File file = new File("./cache/masalahkeperawatansmc.iyem");

            Map<String, String> dipilih = new LinkedHashMap<>();
            for (int i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbMasalahKeperawatan.getValueAt(i, 0)) {
                    dipilih.put(tbMasalahKeperawatan.getValueAt(i, 1).toString(), tbMasalahKeperawatan.getValueAt(i, 2).toString());
                }
            }

            Valid.tabelKosong(tabModeMasalah);

            for (Map.Entry<String, String> item : dipilih.entrySet()) {
                tabModeMasalah.addRow(new Object[] {true, item.getKey(), item.getValue()});
            }

            if (!Valid.umurcacheSmc(file, 7)) {
                try (FileReader fr = new FileReader(file)) {
                    ArrayNode array = mapper.readTree(fr).path("masalahkeperawatan").withArray("pengkajian_tindakan_invasif_non_bedah");
                    for (JsonNode item : array) {
                        if (dipilih.containsKey(item.path("KodeMasalah").asText(""))) {
                            continue;
                        }
                        if (!cari.isBlank() && !(item.path("KodeMasalah").asText("").toLowerCase().contains(cari) || item.path("NamaMasalah").asText("").toLowerCase().contains(cari))) {
                            continue;
                        }

                        tabModeMasalah.addRow(new Object[] {false, item.path("KodeMasalah").asText(""), item.path("NamaMasalah").asText("")});
                    }
                }
            } else {
                try (FileWriter fr = new FileWriter(file); ResultSet rs = koneksi.createStatement().executeQuery(
                    "select smc_master_masalah_keperawatan.menu, smc_master_masalah_keperawatan.kode_masalah, smc_master_masalah_keperawatan.nama_masalah " +
                    "from smc_master_masalah_keperawatan order by smc_master_masalah_keperawatan.menu, smc_master_masalah_keperawatan.kode_masalah"
                )) {
                    Map<String, ArrayNode> menu = new HashMap<>();
                    while (rs.next()) {
                        ObjectNode item = mapper.createObjectNode();
                        item.put("KodeMasalah", rs.getString("kode_masalah"));
                        item.put("NamaMasalah", rs.getString("nama_masalah"));

                        menu.computeIfAbsent(rs.getString("menu"), k -> mapper.createArrayNode()).add(item);

                        if ("pengkajian_tindakan_invasif_non_bedah".equals(rs.getString("menu"))) {
                            if (dipilih.containsKey(rs.getString("kode_masalah"))) {
                                continue;
                            }
                            if (!cari.isBlank() && !(rs.getString("kode_masalah").toLowerCase().contains(cari) || rs.getString("nama_masalah").toLowerCase().contains(cari))) {
                                continue;
                            }

                            tabModeMasalah.addRow(new Object[] {false, rs.getString("kode_masalah"), rs.getString("nama_masalah")});
                        }
                    }
                    fr.write(mapper.writeValueAsString(mapper.createObjectNode().set("masalahkeperawatan", mapper.valueToTree(menu))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilRencana() {
        try {
            final String cari = TCariRencana.getText().toLowerCase().trim();
            File file = new File("./cache/rencanakeperawatansmc.iyem");

            Set<String> masalah = new HashSet<>();
            Map<String, Object[]> dipilih = new LinkedHashMap<>();
            for (int i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbMasalahKeperawatan.getValueAt(i, 0)) {
                    masalah.add(tbMasalahKeperawatan.getValueAt(i, 1).toString());
                }
            }

            for (int i = 0; i < tbRencanaKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbRencanaKeperawatan.getValueAt(i, 0)) {
                    dipilih.put(tbRencanaKeperawatan.getValueAt(i, 1).toString(), new Object[] {
                        tbRencanaKeperawatan.getValueAt(i, 2).toString(), tbRencanaKeperawatan.getValueAt(i, 3).toString()
                    });
                }
            }

            Valid.tabelKosong(tabModeRencana);

            for (Map.Entry<String, Object[]> item : dipilih.entrySet()) {
                tabModeRencana.addRow(new Object[] {true, item.getKey(), item.getValue()[0], item.getValue()[1]});
            }

            if (!Valid.umurcacheSmc(file, 7)) {
                try (FileReader fr = new FileReader(file)) {
                    ArrayNode array = mapper.readTree(fr).path("rencanakeperawatan").withArray("pengkajian_tindakan_invasif_non_bedah");
                    for (JsonNode item : array) {
                        if (!masalah.contains(item.path("KodeMasalah").asText(""))) {
                            continue;
                        }
                        if (dipilih.containsKey(item.path("KodeRencana").asText(""))) {
                            continue;
                        }
                        if (!cari.isBlank() && !(item.path("KodeRencana").asText("").toLowerCase().contains(cari) || item.path("NamaRencana").asText("").toLowerCase().contains(cari))) {
                            continue;
                        }

                        tabModeRencana.addRow(new Object[] {false, item.path("KodeRencana").asText(""), item.path("NamaRencana").asText(""), item.path("KodeMasalah").asText("")});
                    }
                }
            } else {
                try (FileWriter fr = new FileWriter(file); ResultSet rs = koneksi.createStatement().executeQuery(
                    "select smc_master_rencana_keperawatan.menu, smc_master_rencana_keperawatan.kode_masalah, smc_master_rencana_keperawatan.kode_rencana, smc_master_rencana_keperawatan.nama_rencana " +
                    "from smc_master_rencana_keperawatan order by smc_master_rencana_keperawatan.menu, smc_master_rencana_keperawatan.kode_masalah, smc_master_rencana_keperawatan.kode_rencana"
                )) {
                    Map<String, ArrayNode> menu = new HashMap<>();
                    while (rs.next()) {
                        ObjectNode item = mapper.createObjectNode();
                        item.put("KodeMasalah", rs.getString("kode_masalah"));
                        item.put("KodeRencana", rs.getString("kode_rencana"));
                        item.put("NamaRencana", rs.getString("nama_rencana"));

                        menu.computeIfAbsent(rs.getString("menu"), k -> mapper.createArrayNode()).add(item);

                        if ("pengkajian_tindakan_invasif_non_bedah".equals(rs.getString("menu"))) {
                            if (!masalah.contains(rs.getString("kode_masalah"))) {
                                continue;
                            }
                            if (dipilih.containsKey(rs.getString("kode_rencana"))) {
                                continue;
                            }
                            if (!cari.isBlank() && !(rs.getString("kode_rencana").toLowerCase().contains(cari) || rs.getString("nama_rencana").toLowerCase().contains(cari))) {
                                continue;
                            }

                            tabModeRencana.addRow(new Object[] {false, rs.getString("kode_rencana"), rs.getString("nama_rencana"), rs.getString("kode_masalah")});
                        }
                    }
                    fr.write(mapper.writeValueAsString(mapper.createObjectNode().set("rencanakeperawatan", mapper.valueToTree(menu))));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void simpan() {
        if (Sequel.menyimpantfSmc("smc_pengkajian_tindakan_invasif_non_bedah", "", TNoRw.getText(), Valid.getTglJamSmc(TglAsuhan), kdptg.getText(), Diagnosa.getText(), RencanaTindakan.getText(),
            KetPsiko.getText(), RPD.getText(), SistemPernapasan.getSelectedItem().toString(), KetSistemPernapasan.getText(), MuntahDarah.getSelectedItem().toString(), BAB.getSelectedItem().toString(),
            Urine24Jam.getText(), Antiplatelet.getSelectedItem().toString(), LamaAntiPlatelet.getText(), BetaBlocker.getSelectedItem().toString(), LamaBetaBlocker.getText(), Simarc.getSelectedItem().toString(),
            LamaSimarc.getText(), AlergiKeterangan.getText(), TB.getText(), BB.getText(), TD.getText(), IO2.getText(), Nadi.getText(), Suhu.getText(), Pernapasan.getText(), RadialisKanan.getSelectedItem().toString(),
            RadialisKiri.getSelectedItem().toString(), PedisKanan.getSelectedItem().toString(), PedisKiri.getSelectedItem().toString(), Nyeri.getSelectedItem().toString(), NyeriPencetus.getText(), NyeriKualitas.getText(),
            NyeriLokasi.getText(), NyeriPenjalaran.getText(), NyeriSkala.getSelectedItem().toString(), NyeriLama.getText(), KebutuhanEdukasi.getText(), LabHt.getText(), LabHb.getText(), LabLeukosit.getText(),
            LabPtIr.getText(), LabK.getText(), LabNa.getText(), LabUr.getText(), LabHbsAg.getSelectedItem().toString(), LabAntiHCV.getSelectedItem().toString(), LabGds.getText(), LabPtAptt.getText(), LabCr.getText(),
            SkriningJatuh.getSelectedItem().toString(), SkriningSkor.getText(), EchoKesan.getText(), Rencana.getText()
        )) {
            for (int i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbMasalahKeperawatan.getValueAt(i, 0)) {
                    Sequel.menyimpanSmc("smc_pengkajian_tindakan_invasif_non_bedah_masalah", "", TNoRw.getText(),
                        "pengkajian_tindakan_invasif_non_bedah", tbMasalahKeperawatan.getValueAt(i, 1).toString()
                    );
                }
            }

            for (int i = 0; i < tbRencanaKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbRencanaKeperawatan.getValueAt(i, 0)) {
                    Sequel.menyimpanSmc("smc_pengkajian_tindakan_invasif_non_bedah_rencana", "", TNoRw.getText(),
                        "pengkajian_tindakan_invasif_non_bedah", tbRencanaKeperawatan.getValueAt(i, 3).toString(), tbRencanaKeperawatan.getValueAt(i, 1).toString()
                    );
                }
            }

            tabMode.addRow(new Object[] {
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), kdptg.getText(), nmptg.getText(), Valid.getTglJamSmc(TglAsuhan), Diagnosa.getText(), RencanaTindakan.getText(),
                StatusFungsional.getText(), KeluhanUtama.getText(), StatusPsiko.getSelectedItem().toString(), KetPsiko.getText(), RPD.getText(), SistemPernapasan.getSelectedItem().toString(), KetSistemPernapasan.getText(),
                MuntahDarah.getSelectedItem().toString(), BAB.getSelectedItem().toString(), Urine24Jam.getText(), Antiplatelet.getSelectedItem().toString(), LamaAntiPlatelet.getText(), BetaBlocker.getSelectedItem().toString(),
                LamaBetaBlocker.getText(), Simarc.getSelectedItem().toString(), LamaSimarc.getText(), AlergiKeterangan.getText(), TB.getText(), BB.getText(), TD.getText(), IO2.getText(), Nadi.getText(), Suhu.getText(),
                Pernapasan.getText(), RadialisKanan.getSelectedItem().toString(), RadialisKiri.getSelectedItem().toString(), PedisKanan.getSelectedItem().toString(), PedisKiri.getSelectedItem().toString(),
                Nyeri.getSelectedItem().toString(), NyeriPencetus.getText(), NyeriKualitas.getText(), NyeriLokasi.getText(), NyeriPenjalaran.getText(), NyeriSkala.getSelectedItem().toString(), NyeriLama.getText(),
                KebutuhanEdukasi.getText(), LabHt.getText(), LabHb.getText(), LabLeukosit.getText(), LabPtIr.getText(), LabK.getText(), LabNa.getText(), LabUr.getText(), LabHbsAg.getSelectedItem().toString(),
                LabAntiHCV.getSelectedItem().toString(), LabGds.getText(), LabPtAptt.getText(), LabCr.getText(), SkriningJatuh.getSelectedItem().toString(), SkriningSkor.getText(), EchoKesan.getText(), Rencana.getText()
            });

            emptTeks();
            LCount.setText(tabMode.getRowCount() + "");
        }
    }

    private void hapus() {
        if (Sequel.menghapustfSmc("smc_pengkajian_tindakan_invasif_non_bedah", "no_rawat = ? and tanggal = ?",
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()
        )) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void ganti() {
        if (Sequel.mengupdatetfSmc("smc_pengkajian_tindakan_invasif_non_bedah", "no_rawat = ?, tanggal = ?, nip = ?, diagnosa = ?, rencana_tindakan = ?, status_fungsional = ?, keluhan_utama = ?, " +
            "status_psiko = ?, ket_psiko = ?, rpd = ?, sistem_pernapasan = ?, ket_sistem_pernapasan = ?, muntah_darah = ?, bab = ?, urine = ?, antiplatelet = ?, lama_antiplatelet = ?, beta_blocker = ?, " +
            "lama_beta_blocker = ?, simarc = ?, lama_simarc = ?, riwayat_alergi = ?, tb = ?, bb = ?, td = ?, io2 = ?, nadi = ?, suhu = ?, pernapasan = ?, radialis_kanan = ?, radialis_kiri = ?, " +
            "pedis_kanan = ?, pedis_kiri = ?, penilaian_nyeri = ?, penilaian_nyeri_pencetus = ?, penilaian_nyeri_kualitas = ?, penilaian_nyeri_lokasi = ?, penilaian_nyeri_penjalaran = ?, " +
            "penilaian_nyeri_skala = ?, penilaian_nyeri_durasi = ?, kebutuhan_edukasi = ?, hematokrit = ?, hemoglobin = ?, leukosit = ?, pt_ir = ?, kalium = ?, natrium = ?, ureum = ?, hbsag = ?, " +
            "anti_hcv = ?, gds = ?, pt_aptt = ?, kreatinin = ?, skrining_jatuh = ?, skor_resiko_jatuh = ?, hasil_echo = ?, rencana = ?", "no_rawat = ?", TNoRw.getText(), Valid.getTglJamSmc(TglAsuhan),
            kdptg.getText(), Diagnosa.getText(), RencanaTindakan.getText(), StatusFungsional.getText(), KeluhanUtama.getText(), StatusPsiko.getSelectedItem().toString(), KetPsiko.getText(), RPD.getText(),
            SistemPernapasan.getSelectedItem().toString(), KetSistemPernapasan.getText(), MuntahDarah.getSelectedItem().toString(), BAB.getSelectedItem().toString(), Urine24Jam.getText(),
            Antiplatelet.getSelectedItem().toString(), LamaAntiPlatelet.getText(), BetaBlocker.getSelectedItem().toString(), LamaBetaBlocker.getText(), Simarc.getSelectedItem().toString(),
            LamaSimarc.getText(), AlergiKeterangan.getText(), TB.getText(), BB.getText(), TD.getText(), IO2.getText(), Nadi.getText(), Suhu.getText(), Pernapasan.getText(), RadialisKanan.getSelectedItem().toString(),
            RadialisKiri.getSelectedItem().toString(), PedisKanan.getSelectedItem().toString(), PedisKiri.getSelectedItem().toString(), Nyeri.getSelectedItem().toString(), NyeriPencetus.getText(), NyeriKualitas.getText(),
            NyeriLokasi.getText(), NyeriPenjalaran.getText(), NyeriSkala.getSelectedItem().toString(), NyeriLama.getText(), KebutuhanEdukasi.getText(), LabHt.getText(), LabHb.getText(), LabLeukosit.getText(),
            LabPtIr.getText(), LabK.getText(), LabNa.getText(), LabUr.getText(), LabHbsAg.getSelectedItem().toString(), LabAntiHCV.getSelectedItem().toString(), LabGds.getText(), LabPtAptt.getText(), LabCr.getText(),
            SkriningJatuh.getSelectedItem().toString(), SkriningSkor.getText(), EchoKesan.getText(), Rencana.getText(), tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString()
        )) {
            Sequel.menghapusSmc("smc_pengkajian_tindakan_invasif_non_bedah_masalah", "no_rawat = ?", TNoRw.getText());
            for (int i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbMasalahKeperawatan.getValueAt(i, 0)) {
                    Sequel.menyimpanSmc("smc_pengkajian_tindakan_invasif_non_bedah_masalah", "", TNoRw.getText(),
                        "pengkajian_tindakan_invasif_non_bedah", tbMasalahKeperawatan.getValueAt(i, 1).toString()
                    );
                }
            }

            Sequel.menghapusSmc("smc_pengkajian_tindakan_invasif_non_bedah_rencana", "no_rawat = ?", TNoRw.getText());
            for (int i = 0; i < tbRencanaKeperawatan.getRowCount(); i++) {
                if ((Boolean) tbRencanaKeperawatan.getValueAt(i, 0)) {
                    Sequel.menyimpanSmc("smc_pengkajian_tindakan_invasif_non_bedah_rencana", "", TNoRw.getText(),
                        "pengkajian_tindakan_invasif_non_bedah", tbRencanaKeperawatan.getValueAt(i, 3).toString(), tbRencanaKeperawatan.getValueAt(i, 1).toString()
                    );
                }
            }
            tabMode.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 0);
            tabMode.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
            tabMode.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
            tabMode.setValueAt(TglLahir.getText(), tbObat.getSelectedRow(), 3);
            tabMode.setValueAt(Jk.getText(), tbObat.getSelectedRow(), 4);
            tabMode.setValueAt(kdptg.getText(), tbObat.getSelectedRow(), 5);
            tabMode.setValueAt(nmptg.getText(), tbObat.getSelectedRow(), 6);
            tabMode.setValueAt(Valid.getTglJamSmc(TglAsuhan), tbObat.getSelectedRow(), 7);
            tabMode.setValueAt(Diagnosa.getText(), tbObat.getSelectedRow(), 8);
            tabMode.setValueAt(RencanaTindakan.getText(), tbObat.getSelectedRow(), 9);
            tabMode.setValueAt(StatusFungsional.getText(), tbObat.getSelectedRow(), 10);
            tabMode.setValueAt(KeluhanUtama.getText(), tbObat.getSelectedRow(), 11);
            tabMode.setValueAt(StatusPsiko.getSelectedItem().toString(), tbObat.getSelectedRow(), 12);
            tabMode.setValueAt(KetPsiko.getText(), tbObat.getSelectedRow(), 13);
            tabMode.setValueAt(RPD.getText(), tbObat.getSelectedRow(), 14);
            tabMode.setValueAt(SistemPernapasan.getSelectedItem().toString(), tbObat.getSelectedRow(), 15);
            tabMode.setValueAt(KetSistemPernapasan.getText(), tbObat.getSelectedRow(), 16);
            tabMode.setValueAt(MuntahDarah.getSelectedItem().toString(), tbObat.getSelectedRow(), 17);
            tabMode.setValueAt(BAB.getSelectedItem().toString(), tbObat.getSelectedRow(), 18);
            tabMode.setValueAt(Urine24Jam.getText(), tbObat.getSelectedRow(), 19);
            tabMode.setValueAt(Antiplatelet.getSelectedItem().toString(), tbObat.getSelectedRow(), 20);
            tabMode.setValueAt(LamaAntiPlatelet.getText(), tbObat.getSelectedRow(), 21);
            tabMode.setValueAt(BetaBlocker.getSelectedItem().toString(), tbObat.getSelectedRow(), 22);
            tabMode.setValueAt(LamaBetaBlocker.getText(), tbObat.getSelectedRow(), 23);
            tabMode.setValueAt(Simarc.getSelectedItem().toString(), tbObat.getSelectedRow(), 24);
            tabMode.setValueAt(LamaSimarc.getText(), tbObat.getSelectedRow(), 25);
            tabMode.setValueAt(AlergiKeterangan.getText(), tbObat.getSelectedRow(), 26);
            tabMode.setValueAt(TB.getText(), tbObat.getSelectedRow(), 27);
            tabMode.setValueAt(BB.getText(), tbObat.getSelectedRow(), 28);
            tabMode.setValueAt(TD.getText(), tbObat.getSelectedRow(), 29);
            tabMode.setValueAt(IO2.getText(), tbObat.getSelectedRow(), 30);
            tabMode.setValueAt(Nadi.getText(), tbObat.getSelectedRow(), 31);
            tabMode.setValueAt(Suhu.getText(), tbObat.getSelectedRow(), 32);
            tabMode.setValueAt(Pernapasan.getText(), tbObat.getSelectedRow(), 33);
            tabMode.setValueAt(RadialisKanan.getSelectedItem().toString(), tbObat.getSelectedRow(), 34);
            tabMode.setValueAt(RadialisKiri.getSelectedItem().toString(), tbObat.getSelectedRow(), 35);
            tabMode.setValueAt(PedisKanan.getSelectedItem().toString(), tbObat.getSelectedRow(), 36);
            tabMode.setValueAt(PedisKiri.getSelectedItem().toString(), tbObat.getSelectedRow(), 37);
            tabMode.setValueAt(Nyeri.getSelectedItem().toString(), tbObat.getSelectedRow(), 38);
            tabMode.setValueAt(NyeriPencetus.getText(), tbObat.getSelectedRow(), 39);
            tabMode.setValueAt(NyeriKualitas.getText(), tbObat.getSelectedRow(), 40);
            tabMode.setValueAt(Integer.valueOf(NyeriLokasi.getText()), tbObat.getSelectedRow(), 41);
            tabMode.setValueAt(NyeriPenjalaran.getText(), tbObat.getSelectedRow(), 42);
            tabMode.setValueAt(NyeriSkala.getSelectedItem().toString(), tbObat.getSelectedRow(), 43);
            tabMode.setValueAt(NyeriLama.getText(), tbObat.getSelectedRow(), 44);
            tabMode.setValueAt(KebutuhanEdukasi.getText(), tbObat.getSelectedRow(), 45);
            tabMode.setValueAt(LabHt.getText(), tbObat.getSelectedRow(), 46);
            tabMode.setValueAt(LabHb.getText(), tbObat.getSelectedRow(), 47);
            tabMode.setValueAt(LabLeukosit.getText(), tbObat.getSelectedRow(), 48);
            tabMode.setValueAt(LabPtIr.getText(), tbObat.getSelectedRow(), 49);
            tabMode.setValueAt(LabK.getText(), tbObat.getSelectedRow(), 50);
            tabMode.setValueAt(LabNa.getText(), tbObat.getSelectedRow(), 51);
            tabMode.setValueAt(LabUr.getText(), tbObat.getSelectedRow(), 52);
            tabMode.setValueAt(LabHbsAg.getSelectedItem().toString(), tbObat.getSelectedRow(), 53);
            tabMode.setValueAt(LabAntiHCV.getSelectedItem().toString(), tbObat.getSelectedRow(), 54);
            tabMode.setValueAt(LabGds.getText(), tbObat.getSelectedRow(), 55);
            tabMode.setValueAt(LabPtAptt.getText(), tbObat.getSelectedRow(), 56);
            tabMode.setValueAt(LabCr.getText(), tbObat.getSelectedRow(), 57);
            tabMode.setValueAt(SkriningJatuh.getSelectedItem().toString(), tbObat.getSelectedRow(), 58);
            tabMode.setValueAt(SkriningSkor.getText(), tbObat.getSelectedRow(), 59);
            tabMode.setValueAt(EchoKesan.getText(), tbObat.getSelectedRow(), 60);
            tabMode.setValueAt(Rencana.getText(), tbObat.getSelectedRow(), 61);
            emptTeks();
        }
    }
}
