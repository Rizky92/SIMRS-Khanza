package kepegawaian;

import smc.utils.ExcelSMC;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class DlgKehadiranSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeImpor, tabModeScan;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final JFileChooser chooser = new JFileChooser();
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMAT_KOLOM = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMAT_SQL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter[] FORMAT_TANGGAL_SCANLOG = {
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("d-M-yyyy"),
        DateTimeFormatter.ofPattern("yyyy-M-d")
    };

    private static final DateTimeFormatter FORMAT_JAM_SCANLOG = DateTimeFormatter.ofPattern("H:mm[:ss]");
    private static final String MODE_MASUK = "Scan Masuk", MODE_PULANG = "Scan Pulang";
    private static final int KOLOM_TANGGAL_AWAL = 3, KOLOM_MODE = 3;
    private static final Color WARNA_TANPA_KETERANGAN = new Color(139, 0, 0), WARNA_IZIN = new Color(30, 90, 200), WARNA_NORMATIF = new Color(0, 0, 0);
    private volatile boolean ceksukses = false;
    private int tglCutoff = 0, toleransi = 0, terlambat1 = 0, terlambat2 = 0;
    private LocalDate periodeAwal, periodeAkhir, imporAwal, imporAkhir, tanggalDetail;
    private final List<ImportDataPegawai> listImport = new ArrayList<>();
    private final Set<LocalDate> hariLibur = new HashSet<>();
    private final List<ScanFinger> scanDetail = new ArrayList<>();
    private final List<ImportShift> shiftDetail = new ArrayList<>();
    private ImportDataPegawai pegawaiDetail;
    private boolean loadingDetail = false;

    /**
     * Creates new form DlgBangsal
     *
     * @param parent
     * @param modal
     */
    public DlgKehadiranSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tabMode = new DefaultTableModel(null, new Object[] {
            "NIP", "Nama", "Departemen", "Kehadiran", "Pagi", "Siang", "Malam", "Tepat Waktu", "Toleransi", "Terlambat I", "Terlambat II", "Keterlambatan", "Durasi", "Wajib Masuk", "% Hadir"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbBangsal.setModel(tabMode);
        tbBangsal.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbBangsal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbBangsal.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            } else if (i == 1) {
                column.setPreferredWidth(200);
            } else if (i == 2) {
                column.setPreferredWidth(100);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(40);
            } else if (i == 5) {
                column.setPreferredWidth(40);
            } else if (i == 6) {
                column.setPreferredWidth(40);
            } else if (i == 7) {
                column.setPreferredWidth(70);
            } else if (i == 8) {
                column.setPreferredWidth(60);
            } else if (i == 9) {
                column.setPreferredWidth(70);
            } else if (i == 10) {
                column.setPreferredWidth(70);
            } else if (i == 11) {
                column.setPreferredWidth(85);
            } else if (i == 12) {
                column.setPreferredWidth(85);
            } else if (i == 13) {
                column.setPreferredWidth(75);
            } else if (i == 14) {
                column.setPreferredWidth(55);
            }
        }
        tbBangsal.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));
        Valid.LoadTahun(ThnCari);
        BlnCari.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        tabModeImpor = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbRekapFinger.setModel(tabModeImpor);
        tbRekapFinger.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRekapFinger.setCellSelectionEnabled(true);
        tbRekapFinger.getTableHeader().setReorderingAllowed(false);
        tbRekapFinger.setDefaultRenderer(Object.class, new WarnaRekapFinger());

        tabModeScan = new DefaultTableModel(null, new Object[] {
            "Tanggal", "Jam", "Mode Mesin", "Mode", "Dipakai Untuk"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return KOLOM_MODE == colIndex;
            }
        };
        tbScanLog.setModel(tabModeScan);
        tbScanLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbScanLog.getTableHeader().setReorderingAllowed(false);
        int[] lebarScan = {80, 50, 85, 95, 200};
        for (int i = 0; i < lebarScan.length; i++) {
            tbScanLog.getColumnModel().getColumn(i).setPreferredWidth(lebarScan[i]);
        }
        tbScanLog.getColumnModel().getColumn(KOLOM_MODE).setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] {MODE_MASUK, MODE_PULANG})));
        tbScanLog.setDefaultRenderer(Object.class, new WarnaTable());
        tabModeScan.addTableModelListener(e -> {
            if (!loadingDetail && TableModelEvent.UPDATE == e.getType() && KOLOM_MODE == e.getColumn() && 0 <= e.getFirstRow()) {
                ubahModeScan(e.getFirstRow());
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WindowImportScanlogFingerspot = new javax.swing.JDialog();
        internalFrame2 = new widget.InternalFrame();
        panelatas1 = new widget.PanelBiasa();
        label14 = new widget.Label();
        LPeriodeImpor = new widget.Label();
        label15 = new widget.Label();
        LBerkas = new widget.Label();
        Scroll1 = new widget.ScrollPane();
        tbRekapFinger = new widget.Table();
        panelbawah1 = new widget.panelisi();
        BtnSimpanImpor = new widget.Button();
        BtnBaruImpor = new widget.Button();
        BtnImporScanlog = new widget.Button();
        label16 = new widget.Label();
        LCountImpor = new widget.Label();
        BtnKeluarImpor = new widget.Button();
        WindowDetailLogPresensi = new javax.swing.JDialog();
        internalFrame3 = new widget.InternalFrame();
        panelatas2 = new widget.PanelBiasa();
        label17 = new widget.Label();
        LPegawaiDetail = new widget.Label();
        label18 = new widget.Label();
        ShiftDetail = new widget.ComboBox();
        label19 = new widget.Label();
        LStatusDetail = new widget.Label();
        label20 = new widget.Label();
        TglDatangDetail = new widget.Tanggal();
        cmbJamDatang = new widget.ComboBox();
        cmbMenitDatang = new widget.ComboBox();
        CekDatangDetail = new widget.CekBox();
        label21 = new widget.Label();
        TglPulangDetail = new widget.Tanggal();
        cmbJamPulang = new widget.ComboBox();
        cmbMenitPulang = new widget.ComboBox();
        CekPulangDetail = new widget.CekBox();
        Scroll2 = new widget.ScrollPane();
        tbScanLog = new widget.Table();
        panelbawah2 = new widget.panelisi();
        BtnTerapkan = new widget.Button();
        BtnKeluarDetail = new widget.Button();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBangsal = new widget.Table();
        jPanel1 = new javax.swing.JPanel();
        panelGlass7 = new widget.panelisi();
        label11 = new widget.Label();
        ThnCari = new widget.ComboBox();
        BlnCari = new widget.ComboBox();
        LPeriode = new widget.Label();
        label12 = new widget.Label();
        Departemen = new widget.ComboBoxSMC();
        label13 = new widget.Label();
        StatusKerja = new widget.ComboBoxSMC();
        panelGlass5 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnImport = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();

        WindowImportScanlogFingerspot.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        WindowImportScanlogFingerspot.setName("WindowImportScanlogFingerspot"); // NOI18N
        WindowImportScanlogFingerspot.setUndecorated(true);
        WindowImportScanlogFingerspot.setResizable(false);

        internalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Import Scanlog Fingerspot ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout());

        panelatas1.setName("panelatas1"); // NOI18N
        panelatas1.setPreferredSize(new java.awt.Dimension(44, 43));
        panelatas1.setLayout(null);

        label14.setText("Periode :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(60, 23));
        panelatas1.add(label14);
        label14.setBounds(0, 10, 60, 23);

        LPeriodeImpor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LPeriodeImpor.setText("-");
        LPeriodeImpor.setName("LPeriodeImpor"); // NOI18N
        LPeriodeImpor.setPreferredSize(new java.awt.Dimension(190, 23));
        panelatas1.add(LPeriodeImpor);
        LPeriodeImpor.setBounds(63, 10, 190, 23);

        label15.setText("Berkas :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(50, 23));
        panelatas1.add(label15);
        label15.setBounds(256, 10, 50, 23);

        LBerkas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LBerkas.setText("-");
        LBerkas.setName("LBerkas"); // NOI18N
        LBerkas.setPreferredSize(new java.awt.Dimension(450, 23));
        panelatas1.add(LBerkas);
        LBerkas.setBounds(309, 10, 450, 23);

        internalFrame2.add(panelatas1, java.awt.BorderLayout.PAGE_START);

        Scroll1.setName("Scroll1"); // NOI18N

        tbRekapFinger.setToolTipText("<html>\nKlik 2x/tekan spasi pada kolom tanggal untuk melihat detail scan log<br />\nIsi kolom tanggal: scan masuk | scan pulang, tanda - bila tidak terdeteksi<br /><br />\nKeterangan kolom berwarna<br />\n- Merah gelap: Tanpa keterangan atau pengajuan izin/cuti/sakit tidak disetujui<br />\n- Biru: Izin<br />\n- Kuning: Cuti/sakit<br />\n- Hitam: Izin/cuti/sakit normatif<br />\n- Cyan: Tidak ada jadwal dinas<br />\n- Merah muda: Hari minggu/libur<br />\n- Oranye: Scan tidak lengkap/perlu dikoreksi<br />\n</html>"); // NOI18N
        tbRekapFinger.setName("tbRekapFinger"); // NOI18N
        tbRekapFinger.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRekapFingerMouseClicked(evt);
            }
        });
        tbRekapFinger.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbRekapFingerKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbRekapFinger);

        internalFrame2.add(Scroll1, java.awt.BorderLayout.CENTER);

        panelbawah1.setName("panelbawah1"); // NOI18N
        panelbawah1.setPreferredSize(new java.awt.Dimension(44, 55));
        panelbawah1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpanImpor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanImpor.setMnemonic('S');
        BtnSimpanImpor.setText("Simpan");
        BtnSimpanImpor.setToolTipText("Alt+S");
        BtnSimpanImpor.setName("BtnSimpanImpor"); // NOI18N
        BtnSimpanImpor.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpanImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanImporActionPerformed(evt);
            }
        });
        panelbawah1.add(BtnSimpanImpor);

        BtnBaruImpor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBaruImpor.setMnemonic('B');
        BtnBaruImpor.setText("Baru");
        BtnBaruImpor.setToolTipText("Alt+B");
        BtnBaruImpor.setName("BtnBaruImpor"); // NOI18N
        BtnBaruImpor.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBaruImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBaruImporActionPerformed(evt);
            }
        });
        panelbawah1.add(BtnBaruImpor);

        BtnImporScanlog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
        BtnImporScanlog.setMnemonic('I');
        BtnImporScanlog.setText("Impor");
        BtnImporScanlog.setToolTipText("Alt+I");
        BtnImporScanlog.setName("BtnImporScanlog"); // NOI18N
        BtnImporScanlog.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnImporScanlog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnImporScanlogActionPerformed(evt);
            }
        });
        panelbawah1.add(BtnImporScanlog);

        label16.setText("Record :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(57, 23));
        panelbawah1.add(label16);

        LCountImpor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountImpor.setText("0");
        LCountImpor.setName("LCountImpor"); // NOI18N
        LCountImpor.setPreferredSize(new java.awt.Dimension(55, 23));
        panelbawah1.add(LCountImpor);

        BtnKeluarImpor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluarImpor.setMnemonic('K');
        BtnKeluarImpor.setText("Keluar");
        BtnKeluarImpor.setToolTipText("Alt+K");
        BtnKeluarImpor.setName("BtnKeluarImpor"); // NOI18N
        BtnKeluarImpor.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluarImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarImporActionPerformed(evt);
            }
        });
        panelbawah1.add(BtnKeluarImpor);

        internalFrame2.add(panelbawah1, java.awt.BorderLayout.PAGE_END);

        WindowImportScanlogFingerspot.getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        WindowDetailLogPresensi.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        WindowDetailLogPresensi.setName("WindowDetailLogPresensi"); // NOI18N
        WindowDetailLogPresensi.setUndecorated(true);
        WindowDetailLogPresensi.setResizable(false);

        internalFrame3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 50, 50)), "::[ Detail Scan Log ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout());

        panelatas2.setName("panelatas2"); // NOI18N
        panelatas2.setPreferredSize(new java.awt.Dimension(44, 133));
        panelatas2.setLayout(null);

        label17.setText("Pegawai :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(75, 23));
        panelatas2.add(label17);
        label17.setBounds(0, 10, 75, 23);

        LPegawaiDetail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LPegawaiDetail.setText("-");
        LPegawaiDetail.setName("LPegawaiDetail"); // NOI18N
        LPegawaiDetail.setPreferredSize(new java.awt.Dimension(470, 23));
        panelatas2.add(LPegawaiDetail);
        LPegawaiDetail.setBounds(78, 10, 470, 23);

        label18.setText("Shift :");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(75, 23));
        panelatas2.add(label18);
        label18.setBounds(0, 40, 75, 23);

        ShiftDetail.setName("ShiftDetail"); // NOI18N
        ShiftDetail.setPreferredSize(new java.awt.Dimension(250, 23));
        ShiftDetail.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ShiftDetailItemStateChanged(evt);
            }
        });
        panelatas2.add(ShiftDetail);
        ShiftDetail.setBounds(78, 40, 250, 23);

        label19.setText("Status :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(50, 23));
        panelatas2.add(label19);
        label19.setBounds(331, 40, 50, 23);

        LStatusDetail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LStatusDetail.setText("-");
        LStatusDetail.setName("LStatusDetail"); // NOI18N
        LStatusDetail.setPreferredSize(new java.awt.Dimension(164, 23));
        panelatas2.add(LStatusDetail);
        LStatusDetail.setBounds(384, 40, 164, 23);

        label20.setText("Jam Datang :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(75, 23));
        panelatas2.add(label20);
        label20.setBounds(0, 70, 75, 23);

        TglDatangDetail.setForeground(new java.awt.Color(50, 70, 50));
        TglDatangDetail.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-09-2026" }));
        TglDatangDetail.setDisplayFormat("dd-MM-yyyy");
        TglDatangDetail.setName("TglDatangDetail"); // NOI18N
        TglDatangDetail.setOpaque(false);
        panelatas2.add(TglDatangDetail);
        TglDatangDetail.setBounds(78, 70, 90, 23);

        cmbJamDatang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJamDatang.setToolTipText("Jam koreksi");
        cmbJamDatang.setName("cmbJamDatang"); // NOI18N
        cmbJamDatang.setPreferredSize(new java.awt.Dimension(56, 23));
        panelatas2.add(cmbJamDatang);
        cmbJamDatang.setBounds(171, 70, 56, 23);

        cmbMenitDatang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMenitDatang.setToolTipText("Menit koreksi");
        cmbMenitDatang.setName("cmbMenitDatang"); // NOI18N
        cmbMenitDatang.setPreferredSize(new java.awt.Dimension(56, 23));
        panelatas2.add(cmbMenitDatang);
        cmbMenitDatang.setBounds(230, 70, 56, 23);

        CekDatangDetail.setText("Koreksi");
        CekDatangDetail.setToolTipText("Centang untuk mengoreksi jam datang, biarkan kosong untuk mengikuti scan log");
        CekDatangDetail.setName("CekDatangDetail"); // NOI18N
        CekDatangDetail.setPreferredSize(new java.awt.Dimension(80, 23));
        CekDatangDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CekDatangDetailActionPerformed(evt);
            }
        });
        panelatas2.add(CekDatangDetail);
        CekDatangDetail.setBounds(289, 70, 80, 23);

        label21.setText("Jam Pulang :");
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(75, 23));
        panelatas2.add(label21);
        label21.setBounds(0, 100, 75, 23);

        TglPulangDetail.setForeground(new java.awt.Color(50, 70, 50));
        TglPulangDetail.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-09-2026" }));
        TglPulangDetail.setDisplayFormat("dd-MM-yyyy");
        TglPulangDetail.setName("TglPulangDetail"); // NOI18N
        TglPulangDetail.setOpaque(false);
        panelatas2.add(TglPulangDetail);
        TglPulangDetail.setBounds(78, 100, 90, 23);

        cmbJamPulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJamPulang.setToolTipText("Jam koreksi");
        cmbJamPulang.setName("cmbJamPulang"); // NOI18N
        cmbJamPulang.setPreferredSize(new java.awt.Dimension(56, 23));
        panelatas2.add(cmbJamPulang);
        cmbJamPulang.setBounds(171, 100, 56, 23);

        cmbMenitPulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMenitPulang.setToolTipText("Menit koreksi");
        cmbMenitPulang.setName("cmbMenitPulang"); // NOI18N
        cmbMenitPulang.setPreferredSize(new java.awt.Dimension(56, 23));
        panelatas2.add(cmbMenitPulang);
        cmbMenitPulang.setBounds(230, 100, 56, 23);

        CekPulangDetail.setText("Koreksi");
        CekPulangDetail.setToolTipText("Centang untuk mengoreksi jam pulang, biarkan kosong untuk mengikuti scan log");
        CekPulangDetail.setName("CekPulangDetail"); // NOI18N
        CekPulangDetail.setPreferredSize(new java.awt.Dimension(80, 23));
        CekPulangDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CekPulangDetailActionPerformed(evt);
            }
        });
        panelatas2.add(CekPulangDetail);
        CekPulangDetail.setBounds(289, 100, 80, 23);

        internalFrame3.add(panelatas2, java.awt.BorderLayout.PAGE_START);

        Scroll2.setName("Scroll2"); // NOI18N

        tbScanLog.setToolTipText("Ubah kolom mode untuk mengoreksi scan masuk/pulang"); // NOI18N
        tbScanLog.setName("tbScanLog"); // NOI18N
        Scroll2.setViewportView(tbScanLog);

        internalFrame3.add(Scroll2, java.awt.BorderLayout.CENTER);

        panelbawah2.setName("panelbawah2"); // NOI18N
        panelbawah2.setPreferredSize(new java.awt.Dimension(44, 55));
        panelbawah2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnTerapkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnTerapkan.setMnemonic('S');
        BtnTerapkan.setText("Terapkan");
        BtnTerapkan.setToolTipText("Alt+S");
        BtnTerapkan.setName("BtnTerapkan"); // NOI18N
        BtnTerapkan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnTerapkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTerapkanActionPerformed(evt);
            }
        });
        panelbawah2.add(BtnTerapkan);

        BtnKeluarDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluarDetail.setMnemonic('K');
        BtnKeluarDetail.setText("Keluar");
        BtnKeluarDetail.setToolTipText("Alt+K");
        BtnKeluarDetail.setName("BtnKeluarDetail"); // NOI18N
        BtnKeluarDetail.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluarDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarDetailActionPerformed(evt);
            }
        });
        panelbawah2.add(BtnKeluarDetail);

        internalFrame3.add(panelbawah2, java.awt.BorderLayout.PAGE_END);

        WindowDetailLogPresensi.getContentPane().add(internalFrame3, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Rekap Kehadiran ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbBangsal.setAutoCreateRowSorter(true);
        tbBangsal.setName("tbBangsal"); // NOI18N
        Scroll.setViewportView(tbBangsal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass7.setName("panelGlass7"); // NOI18N
        panelGlass7.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label11.setText("Tahun & Bulan :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass7.add(label11);

        ThnCari.setName("ThnCari"); // NOI18N
        ThnCari.setPreferredSize(new java.awt.Dimension(80, 23));
        ThnCari.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ThnCariItemStateChanged(evt);
            }
        });
        panelGlass7.add(ThnCari);

        BlnCari.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        BlnCari.setName("BlnCari"); // NOI18N
        BlnCari.setPreferredSize(new java.awt.Dimension(62, 23));
        BlnCari.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                BlnCariItemStateChanged(evt);
            }
        });
        panelGlass7.add(BlnCari);

        LPeriode.setText("Periode : -");
        LPeriode.setName("LPeriode"); // NOI18N
        LPeriode.setPreferredSize(new java.awt.Dimension(190, 23));
        panelGlass7.add(LPeriode);

        label12.setText("Departemen :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass7.add(label12);

        Departemen.setName("Departemen"); // NOI18N
        panelGlass7.add(Departemen);

        label13.setText("Status Kerja :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass7.add(label13);

        StatusKerja.setName("StatusKerja"); // NOI18N
        panelGlass7.add(StatusKerja);

        jPanel1.add(panelGlass7, java.awt.BorderLayout.PAGE_START);

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(66, 23));
        jLabel6.setRequestFocusEnabled(false);
        panelGlass5.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(195, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass5.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
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
        panelGlass5.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(57, 23));
        panelGlass5.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass5.add(LCount);

        BtnImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
        BtnImport.setMnemonic('I');
        BtnImport.setText("Impor");
        BtnImport.setToolTipText("Alt+I");
        BtnImport.setName("BtnImport"); // NOI18N
        BtnImport.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnImportActionPerformed(evt);
            }
        });
        BtnImport.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnImportKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnImport);

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
        panelGlass5.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('m');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+m");
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
        panelGlass5.add(BtnAll);

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
        panelGlass5.add(BtnKeluar);

        jPanel1.add(panelGlass5, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnAll, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

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
        TCari.setText("");
        Departemen.setSelectedItem("Semua");
        StatusKerja.setSelectedItem("Semua");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TCari.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("file2.css")))) {
                    bw.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.head td{border-right: 1px solid #777777;font: 8.5px tahoma;height:10px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi a{text-decoration:none;color:#8b9b95;padding:0 0 0 0px;font-family: Tahoma;font-size: 8.5px;}.isi2 td{font: 8.5px tahoma;height:12px;background: #ffffff;color:#323232;}.isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}");
                    bw.flush();
                }
                String pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih laporan..!", "Pilihan Cetak", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
                    "Laporan 1 (HTML)", "Laporan 2 (WPS)", "Laporan 3 (CSV)", "Laporan 4 (XLSX)"/*, "Laporan 5 (Jasper)"*/
                }, "Laporan 1 (HTML)");
                switch (pilihan) {
                    case "Laporan 1 (HTML)":
                        Valid.exportHtmlSmc("Hadir.html", "Rekap Kehadiran Pegawai " + LPeriode.getText(), tbBangsal);
                        break;
                    case "Laporan 2 (WPS)":
                        Valid.exportWPSSmc("Hadir.wps", "Rekap Kehadiran Pegawai " + LPeriode.getText(), tbBangsal);
                        break;
                    case "Laporan 3 (CSV)":
                        Valid.exportCSVSmc("Hadir.csv", tbBangsal);
                        break;
                    case "Laporan 4 (XLSX)":
                        Valid.exportXlsxSmc("Hadir.xlsx", tbBangsal);
                        break;
                    /*
                    case "Laporan 5 (Jasper)":
                        Sequel.queryu("delete from temporary where temp37='" + akses.getalamatip() + "'");
                        for (int r = 0; r < tbBangsal.getRowCount(); r++) {
                            Sequel.menyimpan("temporary", "'" + r + "','" +
                                tbBangsal.getValueAt(r, 0).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 1).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 2).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 3).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 4).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 5).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 6).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 7).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 8).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 9).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 10).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 11).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 12).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 13).toString().replaceAll("'", "`") + "','" +
                                tbBangsal.getValueAt(r, 14).toString().replaceAll("'", "`") + "','','','','','','','','','','','','','','','','','','','','','','" + akses.getalamatip() + "'", "Rekap Nota Pembayaran");
                        }
                        Map<String, Object> param = new HashMap<>();
                        param.put("namars", akses.getnamars());
                        param.put("alamatrs", akses.getalamatrs());
                        param.put("kotars", akses.getkabupatenrs());
                        param.put("propinsirs", akses.getpropinsirs());
                        param.put("kontakrs", akses.getkontakrs());
                        param.put("emailrs", akses.getemailrs());
                        param.put("tahun", "BULAN " + BlnCari.getSelectedItem() + " TAHUN " + ThnCari.getSelectedItem());
                        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                        Valid.MyReportqry("rptHadir.jasper", "report", "::[ Rekap Kehadiran Pegawai ]::", "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
                        break;
                    */
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnPrint, BtnAll);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        loadCombo();
        muatCutoff();
        tampilPeriode();
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
    }//GEN-LAST:event_formWindowOpened

    private void ThnCariItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ThnCariItemStateChanged
        tampilPeriode();
    }//GEN-LAST:event_ThnCariItemStateChanged

    private void BlnCariItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_BlnCariItemStateChanged
        tampilPeriode();
    }//GEN-LAST:event_BlnCariItemStateChanged

    private void BtnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnImportActionPerformed
        muatCutoff();
        tampilPeriode();
        if (null == periodeAwal) {
            return;
        }
        if (!periodeAwal.equals(imporAwal) || !periodeAkhir.equals(imporAkhir)) {
            imporAwal = periodeAwal;
            imporAkhir = periodeAkhir;
            prepareImport();
        }
        LPeriodeImpor.setText(imporAwal.format(FORMAT_TANGGAL) + " s.d. " + imporAkhir.format(FORMAT_TANGGAL));
        WindowImportScanlogFingerspot.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        WindowImportScanlogFingerspot.setLocationRelativeTo(internalFrame1);
        WindowImportScanlogFingerspot.setVisible(true);
    }//GEN-LAST:event_BtnImportActionPerformed

    private void BtnImportKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnImportKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnImporActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnImportKeyPressed

    private void BtnImporScanlogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnImporScanlogActionPerformed
        chooser.setDialogTitle("Pilih berkas scanlog fingerspot");
        chooser.setFileFilter(new FileNameExtensionFilter("Berkas Excel (*.xlsx, *.xls)", "xlsx", "xls"));
        if (chooser.showOpenDialog(WindowImportScanlogFingerspot) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        importScanlog(chooser.getSelectedFile());
    }//GEN-LAST:event_BtnImporScanlogActionPerformed

    private void BtnSimpanImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanImporActionPerformed
        simpan();
    }//GEN-LAST:event_BtnSimpanImporActionPerformed

    private void BtnBaruImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBaruImporActionPerformed
        if (!listImport.isEmpty() && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(WindowImportScanlogFingerspot,
            "Hasil impor yang belum disimpan akan dibuang, lanjutkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION)) {
            return;
        }
        prepareImport();
    }//GEN-LAST:event_BtnBaruImporActionPerformed

    private void BtnKeluarImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarImporActionPerformed
        WindowImportScanlogFingerspot.dispose();
    }//GEN-LAST:event_BtnKeluarImporActionPerformed

    private void tbRekapFingerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRekapFingerMouseClicked
        if (2 == evt.getClickCount()) {
            tampilDetail();
        }
    }//GEN-LAST:event_tbRekapFingerMouseClicked

    private void tbRekapFingerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbRekapFingerKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            tampilDetail();
        }
    }//GEN-LAST:event_tbRekapFingerKeyPressed

    private void ShiftDetailItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ShiftDetailItemStateChanged
        if (!loadingDetail) {
            tampilShiftDetail();
        }
    }//GEN-LAST:event_ShiftDetailItemStateChanged

    private void BtnTerapkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTerapkanActionPerformed
        updateScan();
    }//GEN-LAST:event_BtnTerapkanActionPerformed

    private void CekDatangDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CekDatangDetailActionPerformed
        aktifkanWaktuDetail();
    }//GEN-LAST:event_CekDatangDetailActionPerformed

    private void CekPulangDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CekPulangDetailActionPerformed
        aktifkanWaktuDetail();
    }//GEN-LAST:event_CekPulangDetailActionPerformed

    private void BtnKeluarDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarDetailActionPerformed
        if (null != tbScanLog.getCellEditor()) {
            tbScanLog.getCellEditor().stopCellEditing();
        }
        WindowDetailLogPresensi.dispose();
    }//GEN-LAST:event_BtnKeluarDetailActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgKehadiranSMC dialog = new DlgKehadiranSMC(new javax.swing.JFrame(), true);
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
    private widget.ComboBox BlnCari;
    private widget.Button BtnAll;
    private widget.Button BtnBaruImpor;
    private widget.Button BtnCari;
    private widget.Button BtnImporScanlog;
    private widget.Button BtnImport;
    private widget.Button BtnKeluar;
    private widget.Button BtnKeluarDetail;
    private widget.Button BtnKeluarImpor;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpanImpor;
    private widget.Button BtnTerapkan;
    private widget.CekBox CekDatangDetail;
    private widget.CekBox CekPulangDetail;
    private widget.ComboBoxSMC Departemen;
    private widget.Label LBerkas;
    private widget.Label LCount;
    private widget.Label LCountImpor;
    private widget.Label LPegawaiDetail;
    private widget.Label LPeriode;
    private widget.Label LPeriodeImpor;
    private widget.Label LStatusDetail;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ComboBox ShiftDetail;
    private widget.ComboBoxSMC StatusKerja;
    private widget.TextBox TCari;
    private widget.Tanggal TglDatangDetail;
    private widget.Tanggal TglPulangDetail;
    private widget.ComboBox ThnCari;
    private javax.swing.JDialog WindowDetailLogPresensi;
    private javax.swing.JDialog WindowImportScanlogFingerspot;
    private widget.ComboBox cmbJamDatang;
    private widget.ComboBox cmbJamPulang;
    private widget.ComboBox cmbMenitDatang;
    private widget.ComboBox cmbMenitPulang;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel1;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label21;
    private widget.panelisi panelGlass5;
    private widget.panelisi panelGlass7;
    private widget.PanelBiasa panelatas1;
    private widget.PanelBiasa panelatas2;
    private widget.panelisi panelbawah1;
    private widget.panelisi panelbawah2;
    private widget.Table tbBangsal;
    private widget.Table tbRekapFinger;
    private widget.Table tbScanLog;
    // End of variables declaration//GEN-END:variables

    public void isCek() {
        BtnImport.setEnabled(akses.getrekap_kehadiran_smc());
    }

    private void tampil() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode);
            muatCutoff();
            tampilPeriode();
            new SwingWorker<Void, Object[]>() {
                final LocalDate awal = periodeAwal;
                final LocalDate akhir = periodeAkhir;
                final String cari = TCari.getText().trim();
                final String departemen = null == Departemen.getSelectedKey() || "semua".equals(Departemen.getSelectedKey()) ? "" : Departemen.getSelectedKey().toString();
                final String statuskerja = null == StatusKerja.getSelectedKey() || "semua".equals(StatusKerja.getSelectedKey()) ? "" : StatusKerja.getSelectedKey().toString();

                @Override
                protected Void doInBackground() throws Exception {
                    final int jumlahHari = (int) ChronoUnit.DAYS.between(awal, akhir) + 1;
                    final int liburNasional = Sequel.cariIntegerSmc("select count(*) from set_hari_libur where set_hari_libur.tanggal between ? and ?", awal.toString(), akhir.toString());
                    int hariMinggu = 0;
                    for (LocalDate tgl = awal; !tgl.isAfter(akhir); tgl = tgl.plusDays(1)) {
                        if (DayOfWeek.SUNDAY == tgl.getDayOfWeek()) {
                            hariMinggu++;
                        }
                    }

                    List<String> paramJadwal = new ArrayList<>();
                    StringBuilder slotJadwal = new StringBuilder();
                    for (YearMonth bulan = YearMonth.from(awal); !bulan.isAfter(YearMonth.from(akhir)); bulan = bulan.plusMonths(1)) {
                        int hariAwal = bulan.equals(YearMonth.from(awal)) ? awal.getDayOfMonth() : 1;
                        int hariAkhir = bulan.equals(YearMonth.from(akhir)) ? akhir.getDayOfMonth() : bulan.lengthOfMonth();
                        slotJadwal.append("when jadwal_pegawai.tahun = ? and jadwal_pegawai.bulan = ? then ");
                        for (int h = hariAwal; h <= hariAkhir; h++) {
                            slotJadwal.append(h == hariAwal ? "" : " + ").append("if(jadwal_pegawai.h").append(h).append(" = '', 0, 1)");
                        }
                        slotJadwal.append(" ");
                        paramJadwal.add(String.valueOf(bulan.getYear()));
                        paramJadwal.add(String.format("%02d", bulan.getMonthValue()));
                    }
                    final String sqlJadwal = "select ifnull(sum(case " + slotJadwal + "else 0 end), 0) from jadwal_pegawai where jadwal_pegawai.id = ?";

                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select pegawai.nik, pegawai.nama, departemen.nama, pegawai.id, pegawai.wajibmasuk, count(rekap_presensi.id) as hadir, " +
                        "count(if(rekap_presensi.shift like '%Pagi%', 1, null)) as pagi, count(if(rekap_presensi.shift like '%Siang%', 1, null)) as siang, " +
                        "count(if(rekap_presensi.shift like '%Malam%', 1, null)) as malam, count(if(rekap_presensi.status like '%Tepat Waktu%', 1, null)) as tepatwaktu, " +
                        "count(if(rekap_presensi.status like '%Terlambat Toleransi%', 1, null)) as toleransi, count(if(rekap_presensi.status like '%Terlambat I%', 1, null)) as terlambat1, " +
                        "count(if(rekap_presensi.status like '%Terlambat II%', 1, null)) as terlambat2, " +
                        "ifnull(concat(round((sum(time_to_sec(rekap_presensi.keterlambatan)) - mod(sum(time_to_sec(rekap_presensi.keterlambatan)), 3600)) / 3600), ':', " +
                        "round((mod(sum(time_to_sec(rekap_presensi.keterlambatan)), 3600) - mod(mod(sum(time_to_sec(rekap_presensi.keterlambatan)), 3600), 60)) / 60), ':', " +
                        "round(mod(mod(sum(time_to_sec(rekap_presensi.keterlambatan)), 3600), 60))), '00:00:00') as keterlambatan, " +
                        "ifnull(concat(round((sum(time_to_sec(rekap_presensi.durasi)) - mod(sum(time_to_sec(rekap_presensi.durasi)), 3600)) / 3600), ':', " +
                        "round((mod(sum(time_to_sec(rekap_presensi.durasi)), 3600) - mod(mod(sum(time_to_sec(rekap_presensi.durasi)), 3600), 60)) / 60), ':', " +
                        "round(mod(mod(sum(time_to_sec(rekap_presensi.durasi)), 3600), 60))), '00:00:00') as durasi " +
                        "from pegawai inner join departemen on pegawai.departemen = departemen.dep_id inner join stts_kerja on stts_kerja.stts = pegawai.stts_kerja " +
                        "left join rekap_presensi on rekap_presensi.id = pegawai.id and rekap_presensi.jam_datang between ? and ? " +
                        "where pegawai.stts_aktif != 'KELUAR' " + (departemen.isBlank() ? "" : "and pegawai.departemen = ? ") + (statuskerja.isBlank() ? "" : "and pegawai.stts_kerja = ? ") +
                        (cari.isBlank() ? "" : "and (pegawai.nik like ? or pegawai.nama like ?) ") +
                        "group by pegawai.id, pegawai.nik, pegawai.nama, departemen.nama, pegawai.wajibmasuk order by pegawai.nik"
                    )) {
                        int p = 0;
                        ps.setString(++p, awal + " 00:00:00");
                        ps.setString(++p, akhir + " 23:59:59");
                        if (!departemen.isBlank()) {
                            ps.setString(++p, departemen);
                        }
                        if (!statuskerja.isBlank()) {
                            ps.setString(++p, statuskerja);
                        }
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                int wajibmasuk;
                                switch (rs.getInt("wajibmasuk")) {
                                    case -1:
                                        wajibmasuk = 0;
                                        break;
                                    case -2:
                                        wajibmasuk = jumlahHari - 4;
                                        break;
                                    case -3:
                                        wajibmasuk = jumlahHari - 2 - liburNasional;
                                        break;
                                    case -4:
                                        wajibmasuk = jumlahHari - hariMinggu;
                                        break;
                                    case -5:
                                        List<String> param = new ArrayList<>(paramJadwal);
                                        param.add(rs.getString("id"));
                                        wajibmasuk = Sequel.cariIntegerSmc(sqlJadwal, param.toArray(String[]::new));
                                        break;
                                    case 0:
                                        wajibmasuk = jumlahHari - hariMinggu - liburNasional;
                                        break;
                                    default:
                                        wajibmasuk = rs.getInt("wajibmasuk");
                                        break;
                                }
                                int hadir = rs.getInt("hadir");
                                publish(new Object[] {
                                    rs.getString(1), rs.getString(2), rs.getString(3), hadir, rs.getInt("pagi"), rs.getInt("siang"), rs.getInt("malam"), rs.getInt("tepatwaktu"),
                                    rs.getInt("toleransi"), rs.getInt("terlambat1") - rs.getInt("terlambat2"), rs.getInt("terlambat2"), rs.getString("keterlambatan"), rs.getString("durasi"),
                                    wajibmasuk, 0 < wajibmasuk ? Math.round((double) hadir / wajibmasuk * 100) + " %" : "-"
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
                    DlgKehadiranSMC.this.setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }

    private void loadCombo() {
        Departemen.removeAllItems();
        StatusKerja.removeAllItems();

        try (ResultSet rs = koneksi.createStatement().executeQuery("select departemen.dep_id, departemen.nama from departemen order by departemen.nama")) {
            Departemen.addItem("semua", "Semua");
            while (rs.next()) {
                Departemen.addItem(rs.getString(1), rs.getString(2));
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery("select stts_kerja.stts, stts_kerja.ktg from stts_kerja order by stts_kerja.ktg")) {
            StatusKerja.addItem("semua", "Semua");
            while (rs.next()) {
                StatusKerja.addItem(rs.getString(1), rs.getString(2));
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void muatCutoff() {
        tglCutoff = Sequel.cariIntegerSmc("select ifnull(setting.tgl_cutoff_gaji, 0) from setting");
    }

    private void tampilPeriode() {
        if (null == ThnCari.getSelectedItem() || null == BlnCari.getSelectedItem()) {
            return;
        }
        YearMonth bulan = YearMonth.of(Integer.parseInt(ThnCari.getSelectedItem().toString()), Integer.parseInt(BlnCari.getSelectedItem().toString()));
        periodeAwal = akhirPeriode(bulan.minusMonths(1)).plusDays(1);
        periodeAkhir = akhirPeriode(bulan);
        LPeriode.setText("Periode : " + periodeAwal.format(FORMAT_TANGGAL) + " s.d. " + periodeAkhir.format(FORMAT_TANGGAL));
    }

    private LocalDate akhirPeriode(YearMonth bulan) {
        return 0 < tglCutoff && tglCutoff < bulan.lengthOfMonth() ? bulan.atDay(tglCutoff) : bulan.atEndOfMonth();
    }

    private void prepareImport() {
        listImport.clear();
        LBerkas.setText("-");
        List<Object> kolom = new ArrayList<>(List.of("NIP", "Nama", "Departemen"));
        if (null != imporAwal) {
            for (LocalDate tgl = imporAwal; !tgl.isAfter(imporAkhir); tgl = tgl.plusDays(1)) {
                kolom.add(tgl.format(FORMAT_KOLOM));
            }
        }

        tabModeImpor.setDataVector(new Object[0][], kolom.toArray());
        for (int i = 0; i < tabModeImpor.getColumnCount(); i++) {
            tbRekapFinger.getColumnModel().getColumn(i).setPreferredWidth(0 == i ? 90 : 1 == i ? 180 : 2 == i ? 110 : 115);
        }

        LCountImpor.setText("0");
    }

    private void tampilImpor() {
        tabModeImpor.setRowCount(0);
        for (ImportDataPegawai pegawai : listImport) {
            List<Object> baris = new ArrayList<>(List.of(pegawai.nik, pegawai.nama, pegawai.departemen));
            for (LocalDate tgl = imporAwal; !tgl.isAfter(imporAkhir); tgl = tgl.plusDays(1)) {
                baris.add(teksSel(pegawai, tgl));
            }
            tabModeImpor.addRow(baris.toArray());
        }
        LCountImpor.setText(String.valueOf(listImport.size()));
    }

    private void segarkanBaris(ImportDataPegawai pegawai) {
        int baris = listImport.indexOf(pegawai);
        if (0 > baris) {
            return;
        }
        int kolom = KOLOM_TANGGAL_AWAL;
        for (LocalDate tgl = imporAwal; !tgl.isAfter(imporAkhir); tgl = tgl.plusDays(1)) {
            tabModeImpor.setValueAt(teksSel(pegawai, tgl), baris, kolom++);
        }
    }

    private void importScanlog(File file) {
        WindowImportScanlogFingerspot.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Map<String, ImportDataPegawai> pegawaiPerPin = new HashMap<>();
        Set<String> pinTerdaftar = new HashSet<>();
        Set<String> pinAsing = new LinkedHashSet<>();
        Set<String> scanUnik = new HashSet<>();

        int jumlahScan = 0,
            modeLain = 0,
            barisGagal = 0,
            luarPeriode = 0;

        try {
            Map<String, ImportDataPegawai> pegawaiPerID = loadPegawai(pegawaiPerPin, pinTerdaftar);

            try (Workbook workbook = ExcelSMC.openExcel(file)) {
                Sheet sheet = workbook.getSheetAt(0);
                Map<String, Integer> col = new HashMap<>();
                int barisJudul = cariBarisJudul(sheet, col);

                LocalDateTime upperLimit = imporAwal.minusDays(1).atStartOfDay();
                LocalDateTime lowerLimit = imporAkhir.plusDays(2).atStartOfDay();

                for (int i = barisJudul + 1; i <= sheet.getLastRowNum(); i++) {
                    Row baris = sheet.getRow(i);
                    if (null == baris) {
                        continue;
                    }

                    String pin = bacaTeks(baris.getCell(col.get("PIN")));
                    if (pin.isEmpty()) {
                        continue;
                    }

                    String mode = bacaTeks(baris.getCell(col.get("MODE")));
                    if (!MODE_MASUK.equalsIgnoreCase(mode) && !MODE_PULANG.equalsIgnoreCase(mode)) {
                        modeLain++;
                        continue;
                    }

                    LocalDate tanggal = bacaTanggal(baris.getCell(col.get("TANGGAL")));
                    LocalTime jam = bacaJam(baris.getCell(col.get("JAM")));
                    if (null == tanggal || null == jam) {
                        barisGagal++;
                        continue;
                    }

                    LocalDateTime waktu = LocalDateTime.of(tanggal, jam);
                    if (waktu.isBefore(upperLimit) || !waktu.isBefore(lowerLimit)) {
                        luarPeriode++;
                        continue;
                    }

                    if (!pinTerdaftar.contains(pin)) {
                        pinAsing.add(pin);
                        continue;
                    }

                    ImportDataPegawai pegawai = pegawaiPerPin.get(pin);
                    if (null == pegawai || !scanUnik.add(pin + "|" + waktu + "|" + mode.toUpperCase())) {
                        continue;
                    }

                    pegawai.scan.add(new ScanFinger(waktu, MODE_MASUK.equalsIgnoreCase(mode)));

                    jumlahScan++;
                }
            }

            loadJadwal(pegawaiPerID);
            loadKeterangan(pegawaiPerID);

            toleransi = Sequel.cariIntegerSmc("select set_keterlambatan.toleransi from set_keterlambatan");
            terlambat1 = Sequel.cariIntegerSmc("select set_keterlambatan.terlambat1 from set_keterlambatan");
            terlambat2 = Sequel.cariIntegerSmc("select set_keterlambatan.terlambat2 from set_keterlambatan");

            listImport.clear();

            for (ImportDataPegawai pegawai : pegawaiPerID.values()) {
                pegawai.scan.sort(Comparator.comparing(scan -> scan.waktu));
                pegawai.jadwal.sort(Comparator.comparing(shift -> shift.jadwalMasuk));

                attachScan(pegawai);

                boolean adaJadwal = pegawai.jadwal.stream().anyMatch(shift -> !shift.tanggal.isBefore(imporAwal));
                boolean adaScan = pegawai.scan.stream().anyMatch(scan -> !scan.waktu.toLocalDate().isBefore(imporAwal) && !scan.waktu.toLocalDate().isAfter(imporAkhir));

                if (adaJadwal || adaScan) {
                    listImport.add(pegawai);
                }
            }

            listImport.sort(Comparator.comparing(pegawai -> pegawai.nik));
        } catch (Exception e) {
            System.out.println("Notif : " + e);

            prepareImport();

            WindowImportScanlogFingerspot.setCursor(Cursor.getDefaultCursor());

            JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, "Gagal membaca berkas, pastikan formatnya sesuai ekspor scanlog fingerspot..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LBerkas.setText(file.getName());

        tampilImpor();

        WindowImportScanlogFingerspot.setCursor(Cursor.getDefaultCursor());

        JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, "Proses import scanlog selesai..!!");
    }

    private Map<String, ImportDataPegawai> loadPegawai(Map<String, ImportDataPegawai> pegawaiPerPin, Set<String> pinTerdaftar) throws Exception {
        final String departemen = null == Departemen.getSelectedKey() || "semua".equals(Departemen.getSelectedKey()) ? "" : Departemen.getSelectedKey().toString();
        final String statuskerja = null == StatusKerja.getSelectedKey() || "semua".equals(StatusKerja.getSelectedKey()) ? "" : StatusKerja.getSelectedKey().toString();

        Map<String, ImportDataPegawai> pegawaiPerId = new LinkedHashMap<>();

        try (PreparedStatement ps = koneksi.prepareStatement(
            "select mapping_pin_pegawai_smc.pin, pegawai.id, pegawai.nik, pegawai.nama, departemen.nama as departemen, pegawai.departemen as dep_id, pegawai.stts_kerja, pegawai.stts_aktif " +
            "from mapping_pin_pegawai_smc inner join pegawai on pegawai.id = mapping_pin_pegawai_smc.id inner join departemen on departemen.dep_id = pegawai.departemen"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pinTerdaftar.add(rs.getString("pin"));
                    if ("KELUAR".equals(rs.getString("stts_aktif")) || (!departemen.isBlank() && !departemen.equals(rs.getString("dep_id")))
                        || (!statuskerja.isBlank() && !statuskerja.equals(rs.getString("stts_kerja")))) {
                        continue;
                    }

                    ImportDataPegawai pegawai = pegawaiPerId.get(rs.getString("id"));
                    if (null == pegawai) {
                        pegawai = new ImportDataPegawai(rs.getString("id"), rs.getString("nik"), rs.getString("nama"), rs.getString("departemen"));
                        pegawaiPerId.put(pegawai.id, pegawai);
                    }

                    pegawaiPerPin.put(rs.getString("pin"), pegawai);
                }
            }
        }

        return pegawaiPerId;
    }

    private void loadJadwal(Map<String, ImportDataPegawai> pegawaiPerID) throws Exception {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select jadwal.id, jadwal.tanggal, jadwal.kode_shift, jam_masuk_smc.nama_shift, jam_masuk_smc.jam_masuk, jam_masuk_smc.jam_pulang, set_kode_shift_smc.shift from (" +
            "select jadwal_pegawai_smc.id, jadwal_pegawai_smc.tanggal, jadwal_pegawai_smc.kode_shift from jadwal_pegawai_smc where jadwal_pegawai_smc.tanggal between ? and ? union all " +
            "select jadwal_tambahan_smc.id, jadwal_tambahan_smc.tanggal, jadwal_tambahan_smc.kode_shift from jadwal_tambahan_smc where jadwal_tambahan_smc.tanggal between ? and ?) as jadwal " +
            "inner join jam_masuk_smc on jam_masuk_smc.kode_shift = jadwal.kode_shift left join set_kode_shift_smc on set_kode_shift_smc.kode_shift = jadwal.kode_shift"
        )) {
            ps.setString(1, imporAwal.minusDays(1).toString());
            ps.setString(2, imporAkhir.toString());
            ps.setString(3, imporAwal.minusDays(1).toString());
            ps.setString(4, imporAkhir.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImportDataPegawai pegawai = pegawaiPerID.get(rs.getString("id"));
                    if (null == pegawai) {
                        continue;
                    }

                    LocalDate tanggal = rs.getDate("tanggal").toLocalDate();
                    LocalTime masuk = rs.getTime("jam_masuk").toLocalTime(), pulang = rs.getTime("jam_pulang").toLocalTime();
                    pegawai.jadwal.add(new ImportShift(tanggal, rs.getString("kode_shift"), rs.getString("nama_shift"), rs.getString("shift"),
                        tanggal.atTime(masuk), pulang.isAfter(masuk) ? tanggal.atTime(pulang) : tanggal.plusDays(1).atTime(pulang)));
                }
            }
        }
    }

    private void loadKeterangan(Map<String, ImportDataPegawai> pegawaiPerID) throws Exception {
        Map<String, ImportDataPegawai> pegawaiPerNIK = new HashMap<>();
        pegawaiPerID.values().forEach(pegawai -> pegawaiPerNIK.put(pegawai.nik, pegawai));

        try (PreparedStatement ps = koneksi.prepareStatement(
            "select pengajuan_izin_smc.nik, pengajuan_izin_smc.tanggal_izin, pengajuan_izin_smc.normatif from pengajuan_izin_smc " +
            "where pengajuan_izin_smc.status = 'Disetujui' and pengajuan_izin_smc.urgensi = 'Tidak Masuk Kerja' and pengajuan_izin_smc.tanggal_izin between ? and ?"
        )) {
            ps.setString(1, imporAwal.toString());
            ps.setString(2, imporAkhir.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImportDataPegawai pegawai = pegawaiPerNIK.get(rs.getString("nik"));
                    if (null != pegawai) {
                        tambahKeterangan(pegawai, rs.getDate("tanggal_izin").toLocalDate(), "Ya".equals(rs.getString("normatif")) ? "N" : "I");
                    }
                }
            }
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
            "select pengajuan_cuti.nik, pengajuan_cuti.tanggal_awal, pengajuan_cuti.tanggal_akhir from pengajuan_cuti " +
            "where pengajuan_cuti.status = 'Disetujui' and pengajuan_cuti.tanggal_awal <= ? and pengajuan_cuti.tanggal_akhir >= ?"
        )) {
            ps.setString(1, imporAkhir.toString());
            ps.setString(2, imporAwal.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImportDataPegawai pegawai = pegawaiPerNIK.get(rs.getString("nik"));
                    if (null == pegawai) {
                        continue;
                    }
                    for (LocalDate tgl = rs.getDate("tanggal_awal").toLocalDate(); !tgl.isAfter(rs.getDate("tanggal_akhir").toLocalDate()); tgl = tgl.plusDays(1)) {
                        tambahKeterangan(pegawai, tgl, "C");
                    }
                }
            }
        }

        hariLibur.clear();
        try (PreparedStatement ps = koneksi.prepareStatement("select set_hari_libur.tanggal from set_hari_libur where set_hari_libur.tanggal between ? and ?")) {
            ps.setString(1, imporAwal.toString());
            ps.setString(2, imporAkhir.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hariLibur.add(rs.getDate(1).toLocalDate());
                }
            }
        }
    }

    private void tambahKeterangan(ImportDataPegawai pegawai, LocalDate tanggal, String kode) {
        pegawai.keterangan.merge(tanggal, kode, (lama, baru) -> "NCI".indexOf(lama) <= "NCI".indexOf(baru) ? lama : baru);
    }

    private void attachScan(ImportDataPegawai pegawai) {
        pegawai.scan.forEach(scan -> scan.dipakai = "");
        for (ImportShift shift : pegawai.jadwal) {
            shift.datang = null;
            shift.pulang = null;

            if (null == shift.datangManual) {
                ScanFinger scan = cariScan(pegawai, true, shift.windowSebelumMasuk(), shift.jadwalPulang, false);
                if (null != scan) {
                    scan.dipakai = "Datang " + namaShiftScan(shift);
                    shift.datang = scan.waktu;
                }
            }
        }

        for (int i = 0; i < pegawai.jadwal.size(); i++) {
            ImportShift shift = pegawai.jadwal.get(i);
            if (null != shift.pulangManual) {
                continue;
            }

            LocalDateTime dasar = null == shift.getDatang() ? shift.jadwalMasuk : shift.getDatang();
            LocalDateTime batas = shift.windowSetelahPulang();

            ImportShift berikutnya = i + 1 < pegawai.jadwal.size() ? pegawai.jadwal.get(i + 1) : null;
            if (null != berikutnya && null != berikutnya.getDatang() && berikutnya.getDatang().isBefore(batas)) {
                batas = berikutnya.getDatang();
            }

            ScanFinger scan = cariScan(pegawai, false, dasar.plusSeconds(1), batas, true);
            if (null != scan) {
                scan.dipakai = "Pulang " + namaShiftScan(shift);
                shift.pulang = scan.waktu;
            }
        }
    }

    private String namaShiftScan(ImportShift shift) {
        return shift.kodeShift + " " + shift.tanggal.format(FORMAT_KOLOM);
    }

    private ScanFinger cariScan(ImportDataPegawai pegawai, boolean masuk, LocalDateTime dari, LocalDateTime sampai, boolean terakhir) {
        ScanFinger hasil = null;
        for (ScanFinger scan : pegawai.scan) {
            if (masuk == scan.masuk && scan.dipakai.isEmpty() && !scan.waktu.isBefore(dari) && scan.waktu.isBefore(sampai)) {
                hasil = scan;
                if (!terakhir) {
                    return hasil;
                }
            }
        }
        return hasil;
    }

    private String masalahShift(ImportShift shift) {
        if (shift.kosong()) {
            return "";
        }

        if (null == shift.getDatang()) {
            return "Tidak ada scan masuk";
        }

        if (null == shift.getPulang()) {
            return "Tidak ada scan pulang";
        }

        if (!shift.getPulang().isAfter(shift.getDatang())) {
            return "Jam pulang tidak setelah jam datang";
        }

        if (null == shift.shift || shift.shift.isBlank()) {
            return "Kode shift " + shift.kodeShift + " belum dipetakan ke shift presensi";
        }

        return "";
    }

    private long detikTerlambat(ImportShift shift) {
        return Duration.between(shift.jadwalMasuk, shift.getDatang()).getSeconds();
    }

    private String statusShift(ImportShift shift) {
        long terlambat = detikTerlambat(shift);
        String status = terlambat > terlambat2 * 60L ? "Terlambat II" : terlambat > terlambat1 * 60L ? "Terlambat I" : terlambat > toleransi * 60L ? "Terlambat Toleransi" : "Tepat Waktu";
        return status + (shift.getPulang().isBefore(shift.jadwalPulang) ? " & PSW" : "");
    }

    private String keterlambatanShift(ImportShift shift) {
        long terlambat = detikTerlambat(shift);
        return terlambat > toleransi * 60L ? formatDurasi(terlambat) : "";
    }

    private static String formatDurasi(long detik) {
        return String.format("%02d:%02d:%02d", detik / 3600, detik % 3600 / 60, detik % 60);
    }

    private static String formatJam(LocalDateTime waktu) {
        return null == waktu ? "-" : waktu.format(FORMAT_JAM);
    }

    private static String rentangJam(LocalDateTime datang, LocalDateTime pulang) {
        return formatJam(datang) + " | " + formatJam(pulang);
    }

    private String teksSel(ImportDataPegawai pegawai, LocalDate tanggal) {
        List<ImportShift> jadwal = pegawai.jadwalPada(tanggal);
        if (jadwal.isEmpty()) {
            List<ScanFinger> sisa = pegawai.scan.stream().filter(scan -> scan.dipakai.isEmpty() && scan.waktu.toLocalDate().equals(tanggal)).collect(Collectors.toList());
            LocalDateTime masuk = sisa.stream().filter(scan -> scan.masuk).map(scan -> scan.waktu).findFirst().orElse(null);
            LocalDateTime pulang = sisa.stream().filter(scan -> !scan.masuk).map(scan -> scan.waktu).reduce((awal, akhir) -> akhir).orElse(null);
            return null == masuk && null == pulang ? "" : rentangJam(masuk, pulang);
        }

        String keterangan = pegawai.keterangan.get(tanggal);
        return jadwal.stream().map(shift -> shift.kodeShift + " " + (shift.kosong() && null != keterangan ? labelKeterangan(keterangan)
            : rentangJam(shift.getDatang(), shift.getPulang())) + (shift.dikoreksi() ? "*" : "")).collect(Collectors.joining(", "));
    }

    private String labelKeterangan(String kode) {
        switch (kode) {
            case "N":
                return "Normatif";
            case "C":
                return "Cuti";
            case "I":
                return "Izin";
            default:
                return "-";
        }
    }

    private Color warnaCell(ImportDataPegawai pegawai, LocalDate tanggal) {
        List<ImportShift> jadwal = pegawai.jadwalPada(tanggal);
        if (jadwal.isEmpty()) {
            return hariLibur.contains(tanggal) || DayOfWeek.SUNDAY == tanggal.getDayOfWeek() ? new Color(255, 190, 205) : new Color(170, 235, 240);
        }

        boolean absen = false;

        for (ImportShift shift : jadwal) {
            if (!masalahShift(shift).isEmpty()) {
                return new Color(255, 160, 40);
            }
            if (shift.kosong()) {
                absen = true;
            }
        }

        if (!absen) {
            return null;
        }

        switch (pegawai.keterangan.getOrDefault(tanggal, "")) {
            case "N":
                return WARNA_NORMATIF;
            case "C":
                return new Color(255, 225, 90);
            case "I":
                return WARNA_IZIN;
            default:
                return WARNA_TANPA_KETERANGAN;
        }
    }

    private void tampilDetail() {
        int baris = tbRekapFinger.getSelectedRow(), kolom = tbRekapFinger.getSelectedColumn();
        if (0 > baris || baris >= listImport.size() || KOLOM_TANGGAL_AWAL > kolom) {
            return;
        }

        pegawaiDetail = listImport.get(baris);
        tanggalDetail = imporAwal.plusDays(kolom - KOLOM_TANGGAL_AWAL);
        LPegawaiDetail.setText(pegawaiDetail.nik + " - " + pegawaiDetail.nama + ", " + tanggalDetail.format(FORMAT_TANGGAL));

        shiftDetail.clear();
        shiftDetail.addAll(pegawaiDetail.jadwalPada(tanggalDetail));

        LocalDateTime dari = tanggalDetail.atStartOfDay(), sampai = tanggalDetail.plusDays(1).atStartOfDay();
        for (ImportShift shift : shiftDetail) {
            dari = shift.windowSebelumMasuk().isBefore(dari) ? shift.windowSebelumMasuk() : dari;
            sampai = shift.windowSetelahPulang().isAfter(sampai) ? shift.windowSetelahPulang() : sampai;
        }

        scanDetail.clear();

        for (ScanFinger scan : pegawaiDetail.scan) {
            if (!scan.waktu.isBefore(dari) && scan.waktu.isBefore(sampai)) {
                scanDetail.add(scan);
            }
        }

        loadingDetail = true;

        ShiftDetail.removeAllItems();
        if (shiftDetail.isEmpty()) {
            ShiftDetail.addItem("Tidak ada jadwal dinas");
        }

        for (ImportShift shift : shiftDetail) {
            ShiftDetail.addItem(shift.kodeShift + " - " + shift.namaShift + " (" + shift.jadwalMasuk.format(FORMAT_JAM) + " - " + shift.jadwalPulang.format(FORMAT_JAM) + ")");
        }
        ShiftDetail.setSelectedIndex(0);

        Valid.tabelKosongSmc(tabModeScan);
        for (ScanFinger scan : scanDetail) {
            tabModeScan.addRow(new Object[] {
                scan.waktu.format(FORMAT_TANGGAL), scan.waktu.format(FORMAT_JAM), scan.masukMesin ? MODE_MASUK : MODE_PULANG, scan.masuk ? MODE_MASUK : MODE_PULANG, scan.dipakai
            });
        }

        loadingDetail = false;
        tampilShiftDetail();

        WindowDetailLogPresensi.setSize(570, 450);
        WindowDetailLogPresensi.setLocationRelativeTo(WindowImportScanlogFingerspot);
        WindowDetailLogPresensi.setVisible(true);
    }

    private void tampilShiftDetail() {
        int i = ShiftDetail.getSelectedIndex();
        boolean ada = 0 <= i && i < shiftDetail.size();

        CekDatangDetail.setEnabled(ada);
        CekPulangDetail.setEnabled(ada);
        BtnTerapkan.setEnabled(ada);
        if (!ada) {
            CekDatangDetail.setSelected(false);
            CekPulangDetail.setSelected(false);
            setWaktuDetail(TglDatangDetail, cmbJamDatang, cmbMenitDatang, tanggalDetail.atStartOfDay());
            setWaktuDetail(TglPulangDetail, cmbJamPulang, cmbMenitPulang, tanggalDetail.atStartOfDay());
            aktifkanWaktuDetail();
            LStatusDetail.setText("-");
            return;
        }

        ImportShift shift = shiftDetail.get(i);
        CekDatangDetail.setSelected(null != shift.datangManual);
        CekPulangDetail.setSelected(null != shift.pulangManual);

        setWaktuDetail(TglDatangDetail, cmbJamDatang, cmbMenitDatang, null == shift.getDatang() ? shift.jadwalMasuk : shift.getDatang());
        setWaktuDetail(TglPulangDetail, cmbJamPulang, cmbMenitPulang, null == shift.getPulang() ? shift.jadwalPulang : shift.getPulang());

        aktifkanWaktuDetail();
        String masalah = masalahShift(shift);
        LStatusDetail.setText(shift.kosong() ? "Tidak hadir" : !masalah.isEmpty() ? masalah : statusShift(shift) + (shift.dikoreksi() ? " (dikoreksi)" : ""));
    }

    private void setWaktuDetail(widget.Tanggal tanggal, widget.ComboBox jam, widget.ComboBox menit, LocalDateTime waktu) {
        tanggal.setDate(Date.from(waktu.atZone(ZoneId.systemDefault()).toInstant()));
        jam.setSelectedIndex(waktu.getHour());
        menit.setSelectedIndex(waktu.getMinute());
    }

    private void aktifkanWaktuDetail() {
        boolean datang = CekDatangDetail.isEnabled() && CekDatangDetail.isSelected(), pulang = CekPulangDetail.isEnabled() && CekPulangDetail.isSelected();
        TglDatangDetail.setEnabled(datang);
        cmbJamDatang.setEnabled(datang);
        cmbMenitDatang.setEnabled(datang);
        TglPulangDetail.setEnabled(pulang);
        cmbJamPulang.setEnabled(pulang);
        cmbMenitPulang.setEnabled(pulang);
    }

    private LocalDateTime waktuDetail(widget.Tanggal tanggal, widget.ComboBox jam, widget.ComboBox menit) {
        return tanggal.getLocalDate().atTime(jam.getSelectedIndex(), menit.getSelectedIndex());
    }

    private void ubahModeScan(int baris) {
        if (baris >= scanDetail.size()) {
            return;
        }

        scanDetail.get(baris).masuk = MODE_MASUK.equals(tabModeScan.getValueAt(baris, KOLOM_MODE));
        attachScan(pegawaiDetail);
        refreshDetail();
    }

    private void refreshDetail() {
        loadingDetail = true;
        for (int i = 0; i < scanDetail.size(); i++) {
            tabModeScan.setValueAt(scanDetail.get(i).dipakai, i, 4);
        }
        loadingDetail = false;

        tampilShiftDetail();
        segarkanBaris(pegawaiDetail);
    }

    private void updateScan() {
        int i = ShiftDetail.getSelectedIndex();
        if (0 > i || i >= shiftDetail.size()) {
            return;
        }

        LocalDateTime datang = !CekDatangDetail.isSelected() ? null : waktuDetail(TglDatangDetail, cmbJamDatang, cmbMenitDatang);
        LocalDateTime pulang = !CekPulangDetail.isSelected() ? null : waktuDetail(TglPulangDetail, cmbJamPulang, cmbMenitPulang);

        ImportShift shift = shiftDetail.get(i);
        shift.datangManual = null;
        shift.pulangManual = null;

        attachScan(pegawaiDetail);
        shift.datangManual = null == datang || datang.equals(shift.datang) ? null : datang;
        shift.pulangManual = null == pulang || pulang.equals(shift.pulang) ? null : pulang;

        attachScan(pegawaiDetail);

        refreshDetail();
    }

    private void simpan() {
        if (listImport.isEmpty()) {
            JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, "Belum ada scanlog yang diimpor...!!!!");
            return;
        }

        List<String> masalah = new ArrayList<>();
        Map<ImportShift, ImportDataPegawai> simpan = new LinkedHashMap<>();

        for (ImportDataPegawai pegawai : listImport) {
            for (ImportShift shift : pegawai.jadwal) {
                if (shift.tanggal.isBefore(imporAwal) || shift.tanggal.isAfter(imporAkhir)) {
                    continue;
                }

                String teks = masalahShift(shift);
                if (!teks.isEmpty()) {
                    masalah.add(pegawai.nik + " " + pegawai.nama + ", " + shift.tanggal.format(FORMAT_TANGGAL) + " " + shift.kodeShift + " : " + teks);
                } else if (!shift.kosong()) {
                    simpan.put(shift, pegawai);
                }
            }
        }

        if (!masalah.isEmpty()) {
            JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, "Masih ada " + masalah.size() + " jadwal yang perlu dikoreksi sebelum disimpan :\n" +
                masalah.stream().limit(15).collect(Collectors.joining("\n")) + (15 < masalah.size() ? "\n... dan " + (masalah.size() - 15) + " lainnya" : ""));
            return;
        }

        if (simpan.isEmpty()) {
            JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, "Tidak ada presensi yang bisa disimpan...!!!!");
            return;
        }

        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(WindowImportScanlogFingerspot, "Simpan " + simpan.size() + " presensi?\n" +
            "Presensi yang sudah tercatat pada jam shift yang sama akan diganti.", "Konfirmasi", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        WindowImportScanlogFingerspot.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        boolean sukses = false;
        try {
            Sequel.AutoComitFalse();
            for (Map.Entry<ImportShift, ImportDataPegawai> data : simpan.entrySet()) {
                ImportShift shift = data.getKey();
                Sequel.menghapustfSmc("rekap_presensi", "rekap_presensi.id = ? and rekap_presensi.jam_datang between ? and ?", data.getValue().id,
                    (shift.getDatang().isBefore(shift.windowSebelumMasuk()) ? shift.getDatang() : shift.windowSebelumMasuk()).format(FORMAT_SQL),
                    (shift.getDatang().isAfter(shift.jadwalPulang) ? shift.getDatang() : shift.jadwalPulang).format(FORMAT_SQL));
            }

            boolean gagal = false;
            for (Map.Entry<ImportShift, ImportDataPegawai> data : simpan.entrySet()) {
                ImportShift shift = data.getKey();
                gagal = !Sequel.menyimpantfSmc("rekap_presensi", "id, shift, jam_datang, jam_pulang, status, keterlambatan, durasi, keterangan, photo",
                    data.getValue().id, shift.shift, shift.getDatang().format(FORMAT_SQL), shift.getPulang().format(FORMAT_SQL), statusShift(shift),
                    keterlambatanShift(shift), formatDurasi(Duration.between(shift.getDatang(), shift.getPulang()).getSeconds()),
                    "Impor scanlog fingerspot" + (shift.dikoreksi() ? ", dikoreksi" : ""), "");
                if (gagal) {
                    break;
                }
            }

            if (gagal) {
                Sequel.RollBack();
            } else {
                Sequel.Commit();
                sukses = true;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            Sequel.RollBack();
        } finally {
            Sequel.AutoComitTrue();
            WindowImportScanlogFingerspot.setCursor(Cursor.getDefaultCursor());
        }

        if (!sukses) {
            JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, "Gagal menyimpan presensi, seluruh perubahan dibatalkan...!!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(WindowImportScanlogFingerspot, simpan.size() + " presensi berhasil disimpan...!!!!");

        prepareImport();
        WindowImportScanlogFingerspot.dispose();
        tampil();
    }

    private int cariBarisJudul(Sheet sheet, Map<String, Integer> kolom) {
        int batas = Math.min(sheet.getLastRowNum(), 20);
        for (int nomor = sheet.getFirstRowNum(); nomor <= batas; nomor++) {
            Row baris = sheet.getRow(nomor);
            if (null == baris) {
                continue;
            }

            kolom.clear();

            for (Cell sel : baris) {
                String judul = bacaTeks(sel).toUpperCase().replaceAll("[^A-Z0-9]", "");
                if (!judul.isEmpty() && !kolom.containsKey(judul)) {
                    kolom.put(judul, sel.getColumnIndex());
                }
            }

            if (kolom.keySet().containsAll(List.of("PIN", "TANGGAL", "JAM", "MODE"))) {
                return nomor;
            }
        }

        kolom.clear();

        throw new IllegalArgumentException("Kolom PIN, Tanggal, Jam, dan Mode tidak ditemukan pada " + (batas + 1) + " baris pertama dari " + (sheet.getLastRowNum() + 1) + " baris yang terbaca.\n\n" + contohBaris(sheet, batas));
    }

    private String contohBaris(Sheet sheet, int batas) {
        StringBuilder contoh = new StringBuilder();
        int dibaca = 0;

        for (int nomor = sheet.getFirstRowNum(); nomor <= batas && 3 > dibaca; nomor++) {
            Row baris = sheet.getRow(nomor);
            if (null == baris) {
                continue;
            }

            StringBuilder isi = new StringBuilder();
            for (Cell sel : baris) {
                isi.append(0 == isi.length() ? "" : " | ").append(bacaTeks(sel));
            }

            if (isi.toString().isBlank()) {
                continue;
            }

            contoh.append(0 == contoh.length() ? "" : "\n").append("Baris ").append(nomor + 1).append(" : ")
                .append(120 < isi.length() ? isi.substring(0, 120) + "..." : isi.toString());

            dibaca++;
        }

        return 0 == contoh.length() ? "Berkas tidak berisi data." : contoh.toString();
    }

    private String bacaTeks(Cell sel) {
        if (null == sel) {
            return "";
        }

        if (CellType.NUMERIC == sel.getCellType()) {
            double nilai = sel.getNumericCellValue();
            return nilai == Math.floor(nilai) ? String.valueOf((long) nilai) : String.valueOf(nilai);
        }

        if (CellType.STRING == sel.getCellType()) {
            return sel.getStringCellValue().trim();
        }

        return "";
    }

    private LocalDate bacaTanggal(Cell sel) {
        if (null == sel) {
            return null;
        }

        if (CellType.NUMERIC == sel.getCellType()) {
            return sel.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String teks = bacaTeks(sel);

        for (DateTimeFormatter format : FORMAT_TANGGAL_SCANLOG) {
            try {
                return LocalDate.parse(teks, format);
            } catch (DateTimeParseException e) {
            }
        }

        return null;
    }

    private LocalTime bacaJam(Cell sel) {
        if (null == sel) {
            return null;
        }

        if (CellType.NUMERIC == sel.getCellType()) {
            double nilai = sel.getNumericCellValue();
            return LocalTime.ofSecondOfDay(Math.round((nilai - Math.floor(nilai)) * 86400) % 86400);
        }

        try {
            return LocalTime.parse(bacaTeks(sel), FORMAT_JAM_SCANLOG);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private final class WarnaRekapFinger extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color latar = KOLOM_TANGGAL_AWAL <= column && row < listImport.size() ? warnaCell(listImport.get(row), imporAwal.plusDays(column - KOLOM_TANGGAL_AWAL)) : null;

            if (null == latar) {
                latar = 1 == row % 2 ? new Color(255, 244, 244) : new Color(255, 255, 255);
            }

            component.setBackground(latar);

            boolean gelap = WARNA_TANPA_KETERANGAN.equals(latar) || WARNA_IZIN.equals(latar) || WARNA_NORMATIF.equals(latar);

            component.setForeground(gelap ? new Color(255, 255, 255) : isSelected ? new Color(255, 0, 0) : new Color(50, 50, 50));
            component.setFont(component.getFont().deriveFont(isSelected ? Font.BOLD : Font.PLAIN));

            return component;
        }
    }

    private static final class ScanFinger {
        private final LocalDateTime waktu;
        private final boolean masukMesin;
        private boolean masuk;
        private String dipakai = "";

        private ScanFinger(LocalDateTime waktu, boolean masuk) {
            this.waktu = waktu;
            this.masukMesin = masuk;
            this.masuk = masuk;
        }
    }

    private static final class ImportShift {
        private final LocalDate tanggal;
        private final String kodeShift, namaShift, shift;
        private final LocalDateTime jadwalMasuk, jadwalPulang;
        private LocalDateTime datang, pulang, datangManual, pulangManual;

        private ImportShift(LocalDate tanggal, String kodeShift, String namaShift, String shift, LocalDateTime jadwalMasuk, LocalDateTime jadwalPulang) {
            this.tanggal = tanggal;
            this.kodeShift = kodeShift;
            this.namaShift = namaShift;
            this.shift = shift;
            this.jadwalMasuk = jadwalMasuk;
            this.jadwalPulang = jadwalPulang;
        }

        private LocalDateTime getDatang() {
            return null == datangManual ? datang : datangManual;
        }

        private LocalDateTime getPulang() {
            return null == pulangManual ? pulang : pulangManual;
        }

        private boolean kosong() {
            return null == getDatang() && null == getPulang();
        }

        private boolean dikoreksi() {
            return null != datangManual || null != pulangManual;
        }

        private LocalDateTime windowSebelumMasuk() {
            return jadwalMasuk.minusHours(3);
        }

        private LocalDateTime windowSetelahPulang() {
            return jadwalPulang.plusHours(4);
        }
    }

    private static final class ImportDataPegawai {
        private final String id, nik, nama, departemen;
        private final List<ScanFinger> scan = new ArrayList<>();
        private final List<ImportShift> jadwal = new ArrayList<>();
        private final Map<LocalDate, String> keterangan = new HashMap<>();

        private ImportDataPegawai(String id, String nik, String nama, String departemen) {
            this.id = id;
            this.nik = nik;
            this.nama = nama;
            this.departemen = departemen;
        }

        private List<ImportShift> jadwalPada(LocalDate tanggal) {
            return jadwal.stream().filter(shift -> shift.tanggal.equals(tanggal)).collect(Collectors.toList());
        }
    }
}
