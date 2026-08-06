package kepegawaian;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class PengajuanIzinPegawaiSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabMode2;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private volatile boolean ceksukses = false;
    private String izin = "";
    private String tglTMTKerja = "0000-00-00";
    private String tglTATKerja = "0000-00-00";
    private int hakIzin = 0;
    private int diambil = 0;
    private long menit = 0;
    private long maxmenit = 0;
    private String bidang;
    private String departemen;

    public PengajuanIzinPegawaiSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {
            "No.Pengajuan", "Pengajuan", "Jenis Izin", "Tgl. Izin", "Jam Mulai", "Jam Selesai", "Kepentingan Izin", "Sisa Izin", "Periode Awal", "Periode Akhir", "NIK P.J.", "P.J. Terkait", "Departemen", "Bidang", "Status", "Normatif"
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
                column.setPreferredWidth(95);
            } else if (i == 1) {
                column.setPreferredWidth(75);
            } else if (i == 2) {
                column.setPreferredWidth(110);
            } else if (i == 3) {
                column.setPreferredWidth(75);
            } else if (i == 3) {
                column.setPreferredWidth(75);
            } else if (i == 4) {
                column.setPreferredWidth(75);
            } else if (i == 5) {
                column.setPreferredWidth(75);
            } else if (i == 6) {
                column.setPreferredWidth(210);
            } else if (i == 7) {
                column.setPreferredWidth(70);
            } else if (i == 8) {
                column.setPreferredWidth(75);
            } else if (i == 9) {
                column.setPreferredWidth(75);
            } else if (i == 10) {
                column.setPreferredWidth(110);
            } else if (i == 11) {
                column.setPreferredWidth(180);
            } else if (i == 12) {
                column.setPreferredWidth(135);
            } else if (i == 13) {
                column.setPreferredWidth(135);
            } else if (i == 14) {
                column.setPreferredWidth(110);
            } else if (i == 15) {
                column.setPreferredWidth(70);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode2 = new DefaultTableModel(null, new Object[] {
            "No.Pengajuan", "Pengajuan", "Jenis Izin", "Tgl. Izin", "Jam Mulai", "Jam Selesai", "Kepentingan Izin", "Sisa Izin", "Periode Awal", "Periode Akhir", "NIK Mengajukan", "Pegawai Mengajukan", "Departemen", "Bidang", "Status", "Normatif"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat2.setModel(tabMode2);
        tbObat2.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < tabMode2.getColumnCount(); i++) {
            TableColumn column = tbObat2.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(95);
            } else if (i == 1) {
                column.setPreferredWidth(75);
            } else if (i == 2) {
                column.setPreferredWidth(110);
            } else if (i == 3) {
                column.setPreferredWidth(75);
            } else if (i == 3) {
                column.setPreferredWidth(75);
            } else if (i == 4) {
                column.setPreferredWidth(75);
            } else if (i == 5) {
                column.setPreferredWidth(75);
            } else if (i == 6) {
                column.setPreferredWidth(210);
            } else if (i == 7) {
                column.setPreferredWidth(70);
            } else if (i == 8) {
                column.setPreferredWidth(75);
            } else if (i == 9) {
                column.setPreferredWidth(75);
            } else if (i == 10) {
                column.setPreferredWidth(110);
            } else if (i == 11) {
                column.setPreferredWidth(180);
            } else if (i == 12) {
                column.setPreferredWidth(135);
            } else if (i == 13) {
                column.setPreferredWidth(135);
            } else if (i == 14) {
                column.setPreferredWidth(110);
            } else if (i == 15) {
                column.setPreferredWidth(70);
            }
        }
        tbObat2.setDefaultRenderer(Object.class, new WarnaTable());
        Kepentingan.setDocument(new batasInput((int) 70).getKata(Kepentingan));
        NoPengajuan.setDocument(new batasInput((int) 17).getKata(NoPengajuan));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        ChkInput.setSelected(false);
        isForm();

        TabRawat.addChangeListener(e -> tampil());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppSetujui = new javax.swing.JMenuItem();
        ppSetujuiNormatif = new javax.swing.JMenuItem();
        ppTolak = new javax.swing.JMenuItem();
        Detik1 = new widget.ComboBox();
        Detik2 = new widget.ComboBox();
        internalFrame1 = new widget.InternalFrame();
        TabRawat = new widget.TabPane();
        panelBiasa1 = new widget.PanelBiasa();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel18 = new widget.Label();
        LCount1 = new widget.Label();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelBiasa3 = new widget.PanelBiasa();
        panelGlass11 = new widget.panelisi();
        jLabel26 = new widget.Label();
        DTPCari3 = new widget.Tanggal();
        jLabel27 = new widget.Label();
        DTPCari4 = new widget.Tanggal();
        jLabel10 = new widget.Label();
        TCari2 = new widget.TextBox();
        BtnCari2 = new widget.Button();
        BtnAll2 = new widget.Button();
        jLabel28 = new widget.Label();
        LCount2 = new widget.Label();
        Scroll2 = new widget.ScrollPane();
        tbObat2 = new widget.Table();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel8 = new widget.Label();
        Tanggal = new widget.Tanggal();
        jLabel3 = new widget.Label();
        NoPengajuan = new widget.TextBox();
        jLabel20 = new widget.Label();
        Urgensi = new widget.ComboBox();
        jLabel4 = new widget.Label();
        Kepentingan = new widget.TextBox();
        jLabel17 = new widget.Label();
        KdPetugasPJ = new widget.TextBox();
        NmPetugasPJ = new widget.TextBox();
        btnPetugasPJ = new widget.Button();
        jLabel14 = new widget.Label();
        TglIzin = new widget.Tanggal();
        Jam1 = new widget.ComboBox();
        Menit1 = new widget.ComboBox();
        jLabel16 = new widget.Label();
        jLabel22 = new widget.Label();
        Jam2 = new widget.ComboBox();
        Menit2 = new widget.ComboBox();
        Keterangan = new widget.Label();
        jLabel12 = new widget.Label();
        Sisa = new widget.TextBox();
        ChkInput = new widget.CekBox();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        KdPetugas.setEditable(false);
        KdPetugas.setName("KdPetugas"); // NOI18N
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasKeyPressed(evt);
            }
        });

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        NmPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmPetugasKeyPressed(evt);
            }
        });

        jPopupMenu1.setForeground(new java.awt.Color(50, 50, 50));
        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppSetujui.setBackground(new java.awt.Color(255, 255, 254));
        ppSetujui.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppSetujui.setForeground(new java.awt.Color(50, 50, 50));
        ppSetujui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppSetujui.setText("Setujui (Non-Normatif)");
        ppSetujui.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSetujui.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSetujui.setName("ppSetujui"); // NOI18N
        ppSetujui.setPreferredSize(new java.awt.Dimension(160, 26));
        ppSetujui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppSetujuiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppSetujui);

        ppSetujuiNormatif.setBackground(new java.awt.Color(255, 255, 254));
        ppSetujuiNormatif.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppSetujuiNormatif.setForeground(new java.awt.Color(50, 50, 50));
        ppSetujuiNormatif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppSetujuiNormatif.setText("Setujui (Normatif)");
        ppSetujuiNormatif.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSetujuiNormatif.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSetujuiNormatif.setName("ppSetujuiNormatif"); // NOI18N
        ppSetujuiNormatif.setPreferredSize(new java.awt.Dimension(160, 26));
        ppSetujuiNormatif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppSetujuiNormatifActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppSetujuiNormatif);

        ppTolak.setBackground(new java.awt.Color(255, 255, 254));
        ppTolak.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppTolak.setForeground(new java.awt.Color(50, 50, 50));
        ppTolak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppTolak.setText("Tolak");
        ppTolak.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppTolak.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppTolak.setName("ppTolak"); // NOI18N
        ppTolak.setPreferredSize(new java.awt.Dimension(100, 26));
        ppTolak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppTolakActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppTolak);

        Detik1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik1.setName("Detik1"); // NOI18N

        Detik2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik2.setName("Detik2"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengajuan Izin Pegawai ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setLayout(new java.awt.BorderLayout());

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-08-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-08-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(190, 23));
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
        panelGlass9.add(BtnCari);

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
        panelGlass9.add(BtnAll);

        jLabel18.setText("Pengajuan :");
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel18);

        LCount1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount1.setText("0");
        LCount1.setName("LCount1"); // NOI18N
        LCount1.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount1);

        panelBiasa1.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

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
        Scroll.setViewportView(tbObat);

        panelBiasa1.add(Scroll, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Pengajuan Dibuat", panelBiasa1);

        panelBiasa3.setName("panelBiasa3"); // NOI18N
        panelBiasa3.setLayout(new java.awt.BorderLayout());

        panelGlass11.setName("panelGlass11"); // NOI18N
        panelGlass11.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel26.setText("Tanggal :");
        jLabel26.setName("jLabel26"); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass11.add(jLabel26);

        DTPCari3.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-08-2026" }));
        DTPCari3.setDisplayFormat("dd-MM-yyyy");
        DTPCari3.setName("DTPCari3"); // NOI18N
        DTPCari3.setOpaque(false);
        DTPCari3.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass11.add(DTPCari3);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("s.d.");
        jLabel27.setName("jLabel27"); // NOI18N
        jLabel27.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass11.add(jLabel27);

        DTPCari4.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-08-2026" }));
        DTPCari4.setDisplayFormat("dd-MM-yyyy");
        DTPCari4.setName("DTPCari4"); // NOI18N
        DTPCari4.setOpaque(false);
        DTPCari4.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass11.add(DTPCari4);

        jLabel10.setText("Key Word :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass11.add(jLabel10);

        TCari2.setName("TCari2"); // NOI18N
        TCari2.setPreferredSize(new java.awt.Dimension(190, 23));
        TCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCari2KeyPressed(evt);
            }
        });
        panelGlass11.add(TCari2);

        BtnCari2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari2.setMnemonic('3');
        BtnCari2.setToolTipText("Alt+3");
        BtnCari2.setName("BtnCari2"); // NOI18N
        BtnCari2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari2ActionPerformed(evt);
            }
        });
        panelGlass11.add(BtnCari2);

        BtnAll2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll2.setMnemonic('M');
        BtnAll2.setToolTipText("Alt+M");
        BtnAll2.setName("BtnAll2"); // NOI18N
        BtnAll2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAll2ActionPerformed(evt);
            }
        });
        panelGlass11.add(BtnAll2);

        jLabel28.setText("Pengajuan :");
        jLabel28.setName("jLabel28"); // NOI18N
        jLabel28.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass11.add(jLabel28);

        LCount2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount2.setText("0");
        LCount2.setName("LCount2"); // NOI18N
        LCount2.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass11.add(LCount2);

        panelBiasa3.add(panelGlass11, java.awt.BorderLayout.PAGE_END);

        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);
        Scroll2.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat2.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat2.setComponentPopupMenu(jPopupMenu1);
        tbObat2.setName("tbObat2"); // NOI18N
        tbObat2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObat2MouseClicked(evt);
            }
        });
        tbObat2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObat2KeyReleased(evt);
            }
        });
        Scroll2.setViewportView(tbObat2);

        panelBiasa3.add(Scroll2, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Menunggu Persetujuan Saya", panelBiasa3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(72, 125));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(55, 165));
        FormInput.setLayout(null);

        jLabel8.setText("Tgl. Pengajuan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(218, 10, 108, 23);

        Tanggal.setEditable(false);
        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-08-2026" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TanggalItemStateChanged(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(329, 10, 90, 23);

        jLabel3.setText("No. Pengajuan :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 10, 87, 23);

        NoPengajuan.setEditable(false);
        NoPengajuan.setName("NoPengajuan"); // NOI18N
        FormInput.add(NoPengajuan);
        NoPengajuan.setBounds(90, 10, 125, 23);

        jLabel20.setText("Jenis Izin :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(435, 10, 60, 23);

        Urgensi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Terlambat", "Meninggalkan Kerja", "Tidak Masuk Kerja", "Lainnya", " " }));
        Urgensi.setName("Urgensi"); // NOI18N
        Urgensi.setPreferredSize(new java.awt.Dimension(55, 28));
        Urgensi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                UrgensiItemStateChanged(evt);
            }
        });
        FormInput.add(Urgensi);
        Urgensi.setBounds(498, 10, 127, 23);

        jLabel4.setText("Keperluan :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 70, 87, 23);

        Kepentingan.setName("Kepentingan"); // NOI18N
        Kepentingan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KepentinganKeyPressed(evt);
            }
        });
        FormInput.add(Kepentingan);
        Kepentingan.setBounds(90, 70, 596, 23);

        jLabel17.setText("P.J.Terkait :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(0, 40, 87, 23);

        KdPetugasPJ.setEditable(false);
        KdPetugasPJ.setName("KdPetugasPJ"); // NOI18N
        FormInput.add(KdPetugasPJ);
        KdPetugasPJ.setBounds(90, 40, 110, 23);

        NmPetugasPJ.setEditable(false);
        NmPetugasPJ.setName("NmPetugasPJ"); // NOI18N
        FormInput.add(NmPetugasPJ);
        NmPetugasPJ.setBounds(203, 40, 216, 23);

        btnPetugasPJ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugasPJ.setMnemonic('2');
        btnPetugasPJ.setToolTipText("Alt+2");
        btnPetugasPJ.setName("btnPetugasPJ"); // NOI18N
        btnPetugasPJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasPJActionPerformed(evt);
            }
        });
        FormInput.add(btnPetugasPJ);
        btnPetugasPJ.setBounds(422, 40, 28, 23);

        jLabel14.setText("Tanggal :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(626, 10, 60, 23);

        TglIzin.setEditable(false);
        TglIzin.setForeground(new java.awt.Color(50, 70, 50));
        TglIzin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-08-2026" }));
        TglIzin.setDisplayFormat("dd-MM-yyyy");
        TglIzin.setName("TglIzin"); // NOI18N
        TglIzin.setOpaque(false);
        TglIzin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TglIzinItemStateChanged(evt);
            }
        });
        FormInput.add(TglIzin);
        TglIzin.setBounds(689, 10, 90, 23);

        Jam1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam1.setName("Jam1"); // NOI18N
        Jam1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Jam1ItemStateChanged(evt);
            }
        });
        FormInput.add(Jam1);
        Jam1.setBounds(498, 40, 62, 23);

        Menit1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit1.setName("Menit1"); // NOI18N
        Menit1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Menit1ItemStateChanged(evt);
            }
        });
        FormInput.add(Menit1);
        Menit1.setBounds(563, 40, 62, 23);

        jLabel16.setText("Jam :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(455, 40, 40, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("s.d.");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(630, 40, 19, 23);

        Jam2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam2.setName("Jam2"); // NOI18N
        Jam2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Jam2ItemStateChanged(evt);
            }
        });
        FormInput.add(Jam2);
        Jam2.setBounds(652, 40, 62, 23);

        Menit2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit2.setName("Menit2"); // NOI18N
        Menit2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Menit2ItemStateChanged(evt);
            }
        });
        FormInput.add(Menit2);
        Menit2.setBounds(717, 40, 62, 23);

        Keterangan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Keterangan.setText("(0 jam 0 menit)");
        Keterangan.setName("Keterangan"); // NOI18N
        FormInput.add(Keterangan);
        Keterangan.setBounds(784, 40, 85, 23);

        jLabel12.setText("Sisa izin");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(691, 70, 40, 23);

        Sisa.setEditable(false);
        Sisa.setName("Sisa"); // NOI18N
        Sisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SisaKeyPressed(evt);
            }
        });
        FormInput.add(Sisa);
        Sisa.setBounds(734, 70, 45, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChkInputItemStateChanged(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

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
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
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
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (NoPengajuan.getText().trim().equals("")) {
            Valid.textKosong(NoPengajuan, "No.Pengajuan");
        } else if (NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Yang Mengajukan");
        } else if (Kepentingan.getText().trim().equals("")) {
            Valid.textKosong(Kepentingan, "Kepentingan Izin");
        } else if (NmPetugasPJ.getText().trim().equals("")) {
            Valid.textKosong(KdPetugasPJ, "P.J. terkait pengajuan");
        } else if (KdPetugas.getText().equals(KdPetugasPJ.getText())) {
            JOptionPane.showMessageDialog(null, "Maaf, PJ tidak bisa untuk anda sendiri..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            KdPetugasPJ.setText("");
            NmPetugasPJ.setText("");
        } else if (cekMaxMenit()) {
            long jam = maxmenit / 60;
            long menit = maxmenit % 60;
            String pesan = jam + " jam";
            if (menit > 0) {
                pesan += " " + menit + " menit";
            }
            JOptionPane.showMessageDialog(null, "Maaf, durasi pengambilan izin per hari tidak boleh lebih dari " + pesan + "..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (Sequel.cariExistsSmc("select * from pengajuan_izin_smc where pengajuan_izin_smc.nik = ? and pengajuan_izin_smc.tmt = ? and pengajuan_izin_smc.tat = ? " +
            "and pengajuan_izin_smc.izin = ? and pengajuan_izin_smc.tanggal_izin = ?", KdPetugas.getText(), tglTMTKerja, tglTATKerja, izin, Valid.getTglSmc(TglIzin)
        )) {
            JOptionPane.showMessageDialog(null, "Maaf, sudah ada pengajuan izin di tanggal yang sama..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            if (!tanpaHakIzin() && (Integer.parseInt(Sisa.getText()) - 1) < 0) {
                if (JOptionPane.showConfirmDialog(null, "Eeiittss... hak izin yang mengajukan telah habis, apakah yakin tetap ingin mengambil izin?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (Sequel.menyimpantfSmc("pengajuan_izin_smc", "", NoPengajuan.getText(), Valid.getTglSmc(Tanggal), KdPetugas.getText(), tglTMTKerja, tglTATKerja, izin, Urgensi.getSelectedItem().toString(),
                        Valid.getTglSmc(TglIzin), Valid.getJamSmc(Jam1, Menit1, Detik1), Valid.getJamSmc(Jam2, Menit2, Detik2), Kepentingan.getText(), KdPetugasPJ.getText(), "Proses Pengajuan", "Tidak"
                    )) {
                        switch (Urgensi.getSelectedItem().toString()) {
                            case "Tidak Masuk Kerja":
                                tabMode.addRow(new Object[] {
                                    NoPengajuan.getText(), Valid.getTglSmc(Tanggal), Urgensi.getSelectedItem().toString(), Valid.getTglSmc(TglIzin), "", "", Kepentingan.getText(), Sisa.getText(), tglTMTKerja, tglTATKerja,
                                    KdPetugasPJ.getText(), NmPetugasPJ.getText(), departemen, bidang, "Proses Pengajuan", "Tidak"
                                });
                                break;
                            default:
                                tabMode.addRow(new Object[] {
                                    NoPengajuan.getText(), Valid.getTglSmc(Tanggal), Urgensi.getSelectedItem().toString(), Valid.getTglSmc(TglIzin), Valid.getJamSmc(Jam1, Menit1, Detik1), Valid.getJamSmc(Jam2, Menit2, Detik2),
                                    Kepentingan.getText(), Sisa.getText(), tglTMTKerja, tglTATKerja, KdPetugasPJ.getText(), NmPetugasPJ.getText(), departemen, bidang, "Proses Pengajuan", "Tidak"
                                });
                                break;
                        }
                        emptTeks();
                    }
                }
            } else {
                if (Sequel.menyimpantfSmc("pengajuan_izin_smc", "", NoPengajuan.getText(), Valid.getTglSmc(Tanggal), KdPetugas.getText(), tglTMTKerja, tglTATKerja, izin, Urgensi.getSelectedItem().toString(),
                    Valid.getTglSmc(TglIzin), Valid.getJamSmc(Jam1, Menit1, Detik1), Valid.getJamSmc(Jam2, Menit2, Detik2), Kepentingan.getText(), KdPetugasPJ.getText(), "Proses Pengajuan", "Tidak"
                )) {
                    switch (Urgensi.getSelectedItem().toString()) {
                        case "Tidak Masuk Kerja":
                            tabMode.addRow(new Object[] {
                                NoPengajuan.getText(), Valid.getTglSmc(Tanggal), Urgensi.getSelectedItem().toString(), Valid.getTglSmc(TglIzin), "", "", Kepentingan.getText(), Sisa.getText(), tglTMTKerja, tglTATKerja,
                                KdPetugasPJ.getText(), NmPetugasPJ.getText(), departemen, bidang, "Proses Pengajuan", "Tidak"
                            });
                            break;
                        default:
                            tabMode.addRow(new Object[] {
                                NoPengajuan.getText(), Valid.getTglSmc(Tanggal), Urgensi.getSelectedItem().toString(), Valid.getTglSmc(TglIzin), Valid.getJamSmc(Jam1, Menit1, Detik1), Valid.getJamSmc(Jam2, Menit2, Detik2),
                                Kepentingan.getText(), Sisa.getText(), tglTMTKerja, tglTATKerja, KdPetugasPJ.getText(), NmPetugasPJ.getText(), departemen, bidang, "Proses Pengajuan", "Tidak"
                            });
                            break;
                    }
                    emptTeks();
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm();
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() >= 0) {
            if ("Proses Pengajuan".equals(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString())) {
                if (Sequel.menghapustfSmc("pengajuan_izin_smc", "no_pengajuan = ?", tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString())) {
                    tabMode.removeRow(tbObat.getSelectedRow());
                    emptTeks();
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus pengajuan izin..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, sudah disetujui. Tidak boleh dihapus/dirubah..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih dulu data yang mau dihapus..!!");
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (tbObat.getSelectedRow() >= 0) {
            if (NoPengajuan.getText().trim().equals("")) {
                Valid.textKosong(NoPengajuan, "No.Pengajuan");
            } else if (NmPetugas.getText().trim().equals("")) {
                Valid.textKosong(KdPetugas, "Yang Mengajukan");
            } else if (Kepentingan.getText().trim().equals("")) {
                Valid.textKosong(Kepentingan, "Kepentingan Izin");
            } else if (NmPetugasPJ.getText().trim().equals("")) {
                Valid.textKosong(KdPetugasPJ, "P.J. terkait pengajuan");
            } else if (KdPetugas.getText().equals(KdPetugasPJ.getText())) {
                JOptionPane.showMessageDialog(null, "Maaf, PJ tidak bisa untuk anda sendiri..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                KdPetugasPJ.setText("");
                NmPetugasPJ.setText("");
            } else if (cekMaxMenit()) {
                long jam = maxmenit / 60;
                long menit = maxmenit % 60;
                String pesan = jam + " jam";
                if (menit > 0) {
                    pesan += " " + menit + " menit";
                }
                JOptionPane.showMessageDialog(null, "Maaf, durasi pengambilan izin per hari tidak boleh lebih dari " + pesan + "..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else if (Sequel.cariExistsSmc("select * from pengajuan_izin_smc where pengajuan_izin_smc.nik = ? and pengajuan_izin_smc.tmt = ? and pengajuan_izin_smc.tat = ? " +
                "and pengajuan_izin_smc.izin = ? and pengajuan_izin_smc.tanggal_izin = ? and pengajuan_izin_smc.no_pengajuan != ?", KdPetugas.getText(), tglTMTKerja, tglTATKerja, izin,
                Valid.getTglSmc(TglIzin), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
            )) {
                JOptionPane.showMessageDialog(null, "Maaf, sudah ada pengajuan izin di tanggal yang sama..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                if (tbObat.getSelectedRow() > -1) {
                    if ("Proses Pengajuan".equals(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString())) {
                        if (Sequel.mengupdatetfSmc("pengajuan_izin_smc", "no_pengajuan = ?, tanggal = ?, tmt = ?, tat = ?, izin = ?, urgensi = ?, tanggal_izin = ?, jam_mulai = ?, jam_akhir = ?, kepentingan = ?, nik_pj = ?",
                            "no_pengajuan = ? and status = 'Proses Pengajuan'", NoPengajuan.getText(), Valid.getTglSmc(Tanggal), tglTMTKerja, tglTATKerja, izin, Urgensi.getSelectedItem().toString(), Valid.getTglSmc(TglIzin),
                            Valid.getJamSmc(Jam1, Menit1, Detik1), Valid.getJamSmc(Jam2, Menit2, Detik2), Kepentingan.getText(), KdPetugasPJ.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
                        )) {
                            tbObat.setValueAt(NoPengajuan.getText(), tbObat.getSelectedRow(), 0);
                            tbObat.setValueAt(Valid.getTglSmc(Tanggal), tbObat.getSelectedRow(), 1);
                            tbObat.setValueAt(Urgensi.getSelectedItem().toString(), tbObat.getSelectedRow(), 2);
                            tbObat.setValueAt(Valid.getTglSmc(TglIzin), tbObat.getSelectedRow(), 3);
                            if (Urgensi.getSelectedItem().toString().equals("Tidak Masuk Kerja")) {
                                tbObat.setValueAt("", tbObat.getSelectedRow(), 4);
                                tbObat.setValueAt("", tbObat.getSelectedRow(), 5);
                            } else {
                                tbObat.setValueAt(Valid.getJamSmc(Jam1, Menit1, Detik1), tbObat.getSelectedRow(), 4);
                                tbObat.setValueAt(Valid.getJamSmc(Jam2, Menit2, Detik2), tbObat.getSelectedRow(), 5);
                            }
                            tbObat.setValueAt(Kepentingan.getText(), tbObat.getSelectedRow(), 6);
                            tbObat.setValueAt(String.valueOf(Integer.parseInt(Sisa.getText()) - 1), tbObat.getSelectedRow(), 7);
                            tbObat.setValueAt(tglTMTKerja, tbObat.getSelectedRow(), 8);
                            tbObat.setValueAt(tglTATKerja, tbObat.getSelectedRow(), 9);
                            tbObat.setValueAt(KdPetugasPJ.getText(), tbObat.getSelectedRow(), 10);
                            tbObat.setValueAt(NmPetugasPJ.getText(), tbObat.getSelectedRow(), 11);
                            tbObat.setValueAt(departemen, tbObat.getSelectedRow(), 12);
                            tbObat.setValueAt(bidang, tbObat.getSelectedRow(), 13);
                            emptTeks();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Maaf, sudah divalidasi. Tidak boleh dihapus/dirubah..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih dulu data yang mau diubah..!!");
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("file2.css")))) {
                bw.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.head td{border-right: 1px solid #777777;font: 8.5px tahoma;height:10px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi a{text-decoration:none;color:#8b9b95;padding:0 0 0 0px;font-family: Tahoma;font-size: 8.5px;}.isi2 td{font: 8.5px tahoma;height:12px;background: #ffffff;color:#323232;}.isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}");
                bw.flush();
            }
            String pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih laporan..!", "Pilihan Cetak", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
                "Laporan 1 (HTML)", "Laporan 2 (WPS)", "Laporan 3 (CSV)", "Laporan 4 (XLSX)", "Laporan 5 (Jasper)"
            }, "Laporan 5 (Jasper)");
            switch (TabRawat.getSelectedIndex()) {
                case 0:
                    if (tabMode.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
                        BtnBatal.requestFocus();
                    } else if (tabMode.getRowCount() != 0) {
                        switch (pilihan) {
                            case "Laporan 1 (HTML)":
                                Valid.exportHtmlSmc("PengajuanIzinPegawaiSMC.html", "Data Pengajuan Izin", tbObat);
                                break;
                            case "Laporan 2 (WPS)":
                                Valid.exportWPSSmc("PengajuanIzinPegawaiSMC.wps", "Data Pengajuan Izin", tbObat);
                                break;
                            case "Laporan 3 (CSV)":
                                Valid.exportCSVSmc("PengajuanIzinPegawaiSMC.csv", tbObat);
                                break;
                            case "Laporan 4 (XLSX)":
                                Valid.exportXlsxSmc("PengajuanIzinPegawaiSMC.xlsx", tbObat);
                                break;
                            case "Laporan 5 (Jasper)":
                                Valid.reportSmc("rptPengajuanIzinPegawaiSMC.jasper", "report", "::[ Data Pengajuan Izin ]::", param, "with datapengajuan as (select pengajuan_izin_smc.no_pengajuan, pengajuan_izin_smc.tanggal, " +
                                    "pengajuan_izin_smc.urgensi, pengajuan_izin_smc.tanggal_izin, if(pengajuan_izin_smc.jam_mulai = '00:00:00', '', pengajuan_izin_smc.jam_mulai) as jam_mulai, if(pengajuan_izin_smc.jam_akhir = " +
                                    "'00:00:00', '', pengajuan_izin_smc.jam_akhir) as jam_akhir, pengajuan_izin_smc.kepentingan, pengajuan_izin_smc.tmt, pengajuan_izin_smc.tat, pengajuan_izin_smc.nik_pj, pegawai_pj.nama, " +
                                    "pegawai_pj.departemen, pegawai_pj.bidang, (stts_kerja.hakizin - sum(if(pengajuan_izin_smc.status != 'Ditolak' and pengajuan_izin_smc.normatif = 'Tidak', 1, 0)) over (partition by " +
                                    "pengajuan_izin_smc.nik, pengajuan_izin_smc.tmt, pengajuan_izin_smc.tat, pengajuan_izin_smc.izin, pengajuan_izin_smc.urgensi order by pengajuan_izin_smc.tanggal_izin rows between unbounded " +
                                    "preceding and current row)) as sisa, pengajuan_izin_smc.status, pengajuan_izin_smc.normatif " +
                                    "from pengajuan_izin_smc join pegawai on pengajuan_izin_smc.nik = pegawai.nik join pegawai as pegawai_pj on pengajuan_izin_smc.nik_pj = pegawai_pj.nik join " +
                                    "stts_kerja on pegawai.stts_kerja = stts_kerja.stts where pengajuan_izin_smc.nik = ? order by pengajuan_izin_smc.tanggal_izin) select * from datapengajuan where datapengajuan.tanggal between " +
                                    "? and ? and (if(? is null or trim(?) = '', 1 = 1, (datapengajuan.no_pengajuan like ? or datapengajuan.urgensi like ? or datapengajuan.kepentingan like ? or datapengajuan.nik_pj like ? or " +
                                    "datapengajuan.nama like ? or datapengajuan.departemen like ? or datapengajuan.bidang like ?)))", KdPetugas.getText(), Valid.getTglSmc(DTPCari1), Valid.getTglSmc(DTPCari2), TCari.getText().trim(),
                                    TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim());
                                break;
                        }
                    }
                    break;
                case 1:
                    if (tabMode2.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
                        BtnBatal.requestFocus();
                    } else if (tabMode2.getRowCount() != 0) {
                        switch (pilihan) {
                            case "Laporan 1 (HTML)":
                                Valid.exportHtmlSmc("PengajuanIzinPegawai2SMC.html", "Data Pengajuan Izin", tbObat2);
                                break;
                            case "Laporan 2 (WPS)":
                                Valid.exportWPSSmc("PengajuanIzinPegawai2SMC.wps", "Data Pengajuan Izin", tbObat2);
                                break;
                            case "Laporan 3 (CSV)":
                                Valid.exportCSVSmc("PengajuanIzinPegawai2SMC.csv", tbObat2);
                                break;
                            case "Laporan 4 (XLSX)":
                                Valid.exportXlsxSmc("PengajuanIzinPegawai2SMC.xlsx", tbObat2);
                                break;
                            case "Laporan 5 (Jasper)":
                                Valid.reportSmc("rptPengajuanIzinPegawai2SMC.jasper", "report", "::[ Data Pengajuan Izin ]::", param, "with datapengajuan as (select pengajuan_izin_smc.no_pengajuan, pengajuan_izin_smc.tanggal, " +
                                    "pengajuan_izin_smc.urgensi, pengajuan_izin_smc.tanggal_izin, if(pengajuan_izin_smc.jam_mulai = '00:00:00', '', pengajuan_izin_smc.jam_mulai) as jam_mulai, if(pengajuan_izin_smc.jam_akhir = " +
                                    "'00:00:00', '', pengajuan_izin_smc.jam_akhir) as jam_akhir, pengajuan_izin_smc.kepentingan, pengajuan_izin_smc.tmt, pengajuan_izin_smc.tat, pengajuan_izin_smc.nik_pj, pegawai_pj.nama, " +
                                    "pegawai_pj.departemen, pegawai_pj.bidang, (stts_kerja.hakizin - sum(if(pengajuan_izin_smc.status != 'Ditolak' and pengajuan_izin_smc.normatif = 'Tidak', 1, 0)) over (partition by " +
                                    "pengajuan_izin_smc.nik, pengajuan_izin_smc.tmt, pengajuan_izin_smc.tat, pengajuan_izin_smc.izin, pengajuan_izin_smc.urgensi order by pengajuan_izin_smc.tanggal_izin rows between unbounded " +
                                    "preceding and current row)) as sisa, pengajuan_izin_smc.status, pengajuan_izin_smc.normatif " +
                                    "from pengajuan_izin_smc join pegawai on pengajuan_izin_smc.nik = pegawai.nik join pegawai as pegawai_pj on pengajuan_izin_smc.nik_pj = pegawai_pj.nik join " +
                                    "stts_kerja on pegawai.stts_kerja = stts_kerja.stts where pengajuan_izin_smc.nik = ? order by pengajuan_izin_smc.tanggal_izin) select * from datapengajuan where datapengajuan.tanggal between " +
                                    "? and ? and (if(? is null or trim(?) = '', 1 = 1, (datapengajuan.no_pengajuan like ? or datapengajuan.urgensi like ? or datapengajuan.kepentingan like ? or datapengajuan.nik_pj like ? or " +
                                    "datapengajuan.nama like ? or datapengajuan.departemen like ? or datapengajuan.bidang like ?)))", KdPetugas.getText(), Valid.getTglSmc(DTPCari1), Valid.getTglSmc(DTPCari2), TCari.getText().trim(),
                                    TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim(), TCari.getText().trim());
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

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

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampilSmc();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void KdPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPetugasKeyPressed
        Valid.pindah(evt, TCari, Tanggal);
    }//GEN-LAST:event_KdPetugasKeyPressed

    private void NmPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmPetugasKeyPressed
        //Valid.pindah(evt,TKd,TSpek);
    }//GEN-LAST:event_NmPetugasKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
        emptTeks();
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

            TCari2.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari2.getText().length() > 2) {
                        tampil2Smc();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari2.getText().length() > 2) {
                        tampil2Smc();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari2.getText().length() > 2) {
                        tampil2Smc();
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnPetugasPJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasPJActionPerformed
        DlgCariPegawaiSMC petugas = new DlgCariPegawaiSMC(null, false);
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugasPJ.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugasPJ.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    departemen = petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 6).toString();
                    bidang = petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 7).toString();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        // petugas.setIndexJenjang(KdPetugas.getText());
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasPJActionPerformed

    private void KepentinganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KepentinganKeyPressed
        Valid.pindah(evt, Urgensi, BtnSimpan);
    }//GEN-LAST:event_KepentinganKeyPressed

    private void TglIzinItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TglIzinItemStateChanged
        hitungSisaIzin();
    }//GEN-LAST:event_TglIzinItemStateChanged

    private void ppSetujuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppSetujuiActionPerformed
        if (tbObat2.getSelectedRow() > -1) {
            if ("Proses Pengajuan".equals(tbObat2.getValueAt(tbObat2.getSelectedRow(), 14).toString())) {
                if (Sequel.mengupdatetfSmc("pengajuan_izin_smc", "status = 'Disetujui', normatif = 'Tidak'", "no_pengajuan = ?", tbObat2.getValueAt(tbObat2.getSelectedRow(), 0).toString())) {
                    tabMode2.setValueAt("Disetujui", tbObat2.getSelectedRow(), 14);
                    tabMode2.setValueAt("Tidak", tbObat2.getSelectedRow(), 15);
                    tampil2Smc();
                }
            }
        }
    }//GEN-LAST:event_ppSetujuiActionPerformed

    private void ppSetujuiNormatifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppSetujuiNormatifActionPerformed
        if (tbObat2.getSelectedRow() > -1) {
            if ("Proses Pengajuan".equals(tbObat2.getValueAt(tbObat2.getSelectedRow(), 14).toString())) {
                if (Sequel.mengupdatetfSmc("pengajuan_izin_smc", "status = 'Disetujui', normatif = 'Ya'", "no_pengajuan = ?", tbObat2.getValueAt(tbObat2.getSelectedRow(), 0).toString())) {
                    tabMode2.setValueAt("Disetujui", tbObat2.getSelectedRow(), 14);
                    tabMode2.setValueAt("Ya", tbObat2.getSelectedRow(), 15);
                    tampil2Smc();
                }
            }
        }
    }//GEN-LAST:event_ppSetujuiNormatifActionPerformed

    private void ppTolakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppTolakActionPerformed
        if ("Proses Pengajuan".equals(tbObat2.getValueAt(tbObat2.getSelectedRow(), 14).toString())) {
            if (Sequel.mengupdatetfSmc("pengajuan_izin_smc", "status = 'Ditolak'", "no_pengajuan = ?", tbObat2.getValueAt(tbObat2.getSelectedRow(), 0).toString())) {
                tabMode2.setValueAt("Ditolak", tbObat2.getSelectedRow(), 14);
                tampil2Smc();
            }
        }
    }//GEN-LAST:event_ppTolakActionPerformed

    private void TCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCari2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCari2ActionPerformed(null);
        } else {
            Valid.pindahSmc(evt, DTPCari4, BtnCari2);
        }
    }//GEN-LAST:event_TCari2KeyPressed

    private void BtnCari2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari2ActionPerformed
        tampil2Smc();
    }//GEN-LAST:event_BtnCari2ActionPerformed

    private void BtnAll2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAll2ActionPerformed
        TCari2.setText("");
        BtnCari2ActionPerformed(null);
    }//GEN-LAST:event_BtnAll2ActionPerformed

    private void tbObat2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObat2MouseClicked

    }//GEN-LAST:event_tbObat2MouseClicked

    private void tbObat2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObat2KeyReleased

    }//GEN-LAST:event_tbObat2KeyReleased

    private void TanggalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TanggalItemStateChanged
        autoNomor();
    }//GEN-LAST:event_TanggalItemStateChanged

    private void UrgensiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_UrgensiItemStateChanged
        switch (Urgensi.getSelectedItem().toString()) {
            case "Terlambat":
            case "Meninggalkan Kerja":
            case "Lainnya":
                jLabel16.setEnabled(true);
                Jam1.setEnabled(true);
                Menit1.setEnabled(true);
                jLabel22.setEnabled(true);
                Jam2.setEnabled(true);
                Menit2.setEnabled(true);
                Keterangan.setText("(0 jam 0 menit)");
                break;
            case "Tidak Masuk Kerja":
                jLabel16.setEnabled(false);
                Jam1.setEnabled(false);
                Menit1.setEnabled(false);
                Valid.setJamSmc(Jam1, Menit1, Detik1, "00:00:00");
                jLabel22.setEnabled(false);
                Jam2.setEnabled(false);
                Menit2.setEnabled(false);
                Valid.setJamSmc(Jam2, Menit2, Detik2, "00:00:00");
                Keterangan.setText("(1 hari)");
                break;
            default:
                break;
        }
        hitungSisaIzin();
    }//GEN-LAST:event_UrgensiItemStateChanged

    private void ChkInputItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChkInputItemStateChanged
        isForm();
        if (ChkInput.isSelected()) {
            if (TabRawat.getSelectedIndex() == 1) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_ChkInputItemStateChanged

    private void Jam1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Jam1ItemStateChanged
        hitungLamaIzin();
    }//GEN-LAST:event_Jam1ItemStateChanged

    private void Menit1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Menit1ItemStateChanged
        hitungLamaIzin();
    }//GEN-LAST:event_Menit1ItemStateChanged

    private void Jam2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Jam2ItemStateChanged
        hitungLamaIzin();
    }//GEN-LAST:event_Jam2ItemStateChanged

    private void Menit2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Menit2ItemStateChanged
        hitungLamaIzin();
    }//GEN-LAST:event_Menit2ItemStateChanged

    private void SisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SisaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SisaKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            PengajuanIzinPegawaiSMC dialog = new PengajuanIzinPegawaiSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnAll2;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCari2;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPCari3;
    private widget.Tanggal DTPCari4;
    private widget.ComboBox Detik1;
    private widget.ComboBox Detik2;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Jam1;
    private widget.ComboBox Jam2;
    private widget.TextBox KdPetugas;
    private widget.TextBox KdPetugasPJ;
    private widget.TextBox Kepentingan;
    private widget.Label Keterangan;
    private widget.Label LCount;
    private widget.Label LCount1;
    private widget.Label LCount2;
    private widget.ComboBox Menit1;
    private widget.ComboBox Menit2;
    private widget.TextBox NmPetugas;
    private widget.TextBox NmPetugasPJ;
    private widget.TextBox NoPengajuan;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll2;
    private widget.TextBox Sisa;
    private widget.TextBox TCari;
    private widget.TextBox TCari2;
    private widget.TabPane TabRawat;
    private widget.Tanggal Tanggal;
    private widget.Tanggal TglIzin;
    private widget.ComboBox Urgensi;
    private widget.Button btnPetugasPJ;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel12;
    private widget.Label jLabel14;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.PanelBiasa panelBiasa1;
    private widget.PanelBiasa panelBiasa3;
    private widget.panelisi panelGlass11;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JMenuItem ppSetujui;
    private javax.swing.JMenuItem ppSetujuiNormatif;
    private javax.swing.JMenuItem ppTolak;
    private widget.Table tbObat;
    private widget.Table tbObat2;
    // End of variables declaration//GEN-END:variables

    private void emptTeks() {
        Tanggal.setDate(new Date());
        TglIzin.setDate(new Date());
        Valid.setJamSmc(Jam1, Menit1, Detik1, "00:00:00");
        Valid.setJamSmc(Jam2, Menit2, Detik2, "00:00:00");
        Kepentingan.setText("");
        autoNomor();
        tbObat.clearSelection();
        TabRawat.setSelectedIndex(0);
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            Valid.SetTgl(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            NoPengajuan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            Urgensi.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            Valid.SetTgl(TglIzin, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Valid.setJamSmc(Jam1, Menit1, Detik1, tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            Valid.setJamSmc(Jam2, Menit2, Detik2, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            Kepentingan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Sisa.setText(String.valueOf(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()) + 1));
            tglTMTKerja = tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString();
            tglTATKerja = tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString();
            KdPetugasPJ.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            NmPetugasPJ.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            departemen = tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString();
            bidang = tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString();
        }
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 125));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        if (!akses.getadmin()) {
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(Sequel.CariPegawai(KdPetugas.getText()));
        }
    }

    private void autoNomor() {
        Valid.autonomor1Smc(NoPengajuan, "PI", "pengajuan_izin_smc", "no_pengajuan", 3, "0", Tanggal);
    }

    private void tampil() {
        if (TabRawat.getSelectedIndex() == 0) {
            tampilSmc();
        } else if (TabRawat.getSelectedIndex() == 1) {
            tampil2Smc();
        }
    }

    private void tampilSmc() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode);
            new SwingWorker<Void, Object[]>() {
                final String cari = TCari.getText().trim();
                final String kdpetugas = KdPetugas.getText();
                final String tgl1 = Valid.getTglSmc(DTPCari1);
                final String tgl2 = Valid.getTglSmc(DTPCari2);

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "with datapengajuan as (select pengajuan_izin_smc.no_pengajuan, pengajuan_izin_smc.tanggal, pengajuan_izin_smc.urgensi, pengajuan_izin_smc.tanggal_izin, if(pengajuan_izin_smc.jam_mulai = '00:00:00', '', " +
                        "pengajuan_izin_smc.jam_mulai) as jam_mulai, if(pengajuan_izin_smc.jam_akhir = '00:00:00', '', pengajuan_izin_smc.jam_akhir) as jam_akhir, pengajuan_izin_smc.kepentingan, pengajuan_izin_smc.tmt, " +
                        "pengajuan_izin_smc.tat, pengajuan_izin_smc.nik_pj, pegawai_pj.nama, pegawai_pj.departemen, pegawai_pj.bidang, (stts_kerja.hakizin - sum(if(pengajuan_izin_smc.status != 'Ditolak' and pengajuan_izin_smc.normatif " +
                        "= 'Tidak', 1, 0)) over (partition by pengajuan_izin_smc.nik, pengajuan_izin_smc.tmt, pengajuan_izin_smc.tat, pengajuan_izin_smc.izin, pengajuan_izin_smc.urgensi order by pengajuan_izin_smc.tanggal_izin rows " +
                        "between unbounded preceding and current row)) as sisa, pengajuan_izin_smc.status, pengajuan_izin_smc.normatif from pengajuan_izin_smc join pegawai on pengajuan_izin_smc.nik = pegawai.nik join pegawai as " +
                        "pegawai_pj on pengajuan_izin_smc.nik_pj = pegawai_pj.nik join stts_kerja on pegawai.stts_kerja = stts_kerja.stts where pengajuan_izin_smc.nik = ? order by pengajuan_izin_smc.tanggal_izin) select * from " +
                        "datapengajuan where datapengajuan.tanggal between ? and ? " + (cari.isBlank() ? "" : "and (datapengajuan.no_pengajuan like ? or datapengajuan.urgensi like ? or datapengajuan.kepentingan like ? or " +
                        "datapengajuan.nik_pj like ? or datapengajuan.nama like ? or datapengajuan.departemen like ? or datapengajuan.bidang like ?)")
                    )) {
                        int p = 0;
                        ps.setString(++p, kdpetugas);
                        ps.setString(++p, tgl1);
                        ps.setString(++p, tgl2);
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                publish(new Object[] {
                                    rs.getString("no_pengajuan"), rs.getString("tanggal"), rs.getString("urgensi"), rs.getString("tanggal_izin"), rs.getString("jam_mulai"),
                                    rs.getString("jam_akhir"), rs.getString("kepentingan"), rs.getString("sisa"), rs.getString("tmt"), rs.getString("tat"), rs.getString("nik_pj"),
                                    rs.getString("nama"), rs.getString("departemen"), rs.getString("bidang"), rs.getString("status"), rs.getString("normatif")
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
                    LCount1.setText(tabMode.getRowCount() + "");
                    PengajuanIzinPegawaiSMC.this.setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }

    private void tampil2Smc() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode2);
            new SwingWorker<Void, Object[]>() {
                final String cari = TCari2.getText().trim();
                final String kdpetugas = KdPetugas.getText();
                final String tgl1 = Valid.getTglSmc(DTPCari3);
                final String tgl2 = Valid.getTglSmc(DTPCari4);

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "with datapengajuan as (select pengajuan_izin_smc.no_pengajuan, pengajuan_izin_smc.tanggal, pengajuan_izin_smc.urgensi, pengajuan_izin_smc.tanggal_izin, if(pengajuan_izin_smc.jam_mulai = '00:00:00', " +
                        "'', pengajuan_izin_smc.jam_mulai) as jam_mulai, if(pengajuan_izin_smc.jam_akhir = '00:00:00', '', pengajuan_izin_smc.jam_akhir) as jam_akhir, pengajuan_izin_smc.kepentingan, pengajuan_izin_smc.tmt, " +
                        "pengajuan_izin_smc.tat, pengajuan_izin_smc.nik, pegawai.nama, pegawai.departemen, pegawai.bidang, (stts_kerja.hakizin - sum(if(pengajuan_izin_smc.status != 'Ditolak' and pengajuan_izin_smc.normatif = " +
                        "'Tidak', 1, 0)) over (partition by pengajuan_izin_smc.nik, pengajuan_izin_smc.tmt, pengajuan_izin_smc.tat, pengajuan_izin_smc.izin, pengajuan_izin_smc.urgensi order by pengajuan_izin_smc.tanggal_izin " +
                        "rows between unbounded preceding and current row)) as sisa, pengajuan_izin_smc.status, pengajuan_izin_smc.normatif from pengajuan_izin_smc join pegawai on pengajuan_izin_smc.nik = pegawai.nik join " +
                        "pegawai as pegawai_pj on pengajuan_izin_smc.nik_pj = pegawai_pj.nik join stts_kerja on pegawai.stts_kerja = stts_kerja.stts where pengajuan_izin_smc.nik_pj = ? order by pengajuan_izin_smc.tanggal_izin) " +
                        "select * from datapengajuan where datapengajuan.tanggal between ? and ? " + (cari.isBlank() ? "" : "and (datapengajuan.no_pengajuan like ? or datapengajuan.urgensi like ? or datapengajuan.kepentingan like ? " +
                        "or datapengajuan.nik_pj like ? or datapengajuan.nama like ? or datapengajuan.departemen like ? or datapengajuan.bidang like ?)")
                    )) {
                        int p = 0;
                        ps.setString(++p, kdpetugas);
                        ps.setString(++p, tgl1);
                        ps.setString(++p, tgl2);
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                publish(new Object[] {
                                    rs.getString("no_pengajuan"), rs.getString("tanggal"), rs.getString("urgensi"), rs.getString("tanggal_izin"), rs.getString("jam_mulai"),
                                    rs.getString("jam_akhir"), rs.getString("kepentingan"), rs.getString("sisa"), rs.getString("tmt"), rs.getString("tat"), rs.getString("nik"),
                                    rs.getString("nama"), rs.getString("departemen"), rs.getString("bidang"), rs.getString("status"), rs.getString("normatif")
                                });
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void process(List<Object[]> chunks) {
                    chunks.forEach(tabMode2::addRow);
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    tabMode2.fireTableDataChanged();
                    LCount2.setText(tabMode2.getRowCount() + "");
                    PengajuanIzinPegawaiSMC.this.setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }

    private void hitungLamaIzin() {
        if (!"Tidak Masuk Kerja".equals(Urgensi.getSelectedItem().toString())) {
            try {
                LocalTime awal = LocalTime.parse(Valid.getJamSmc(Jam1, Menit1, Detik1));
                LocalTime akhir = LocalTime.parse(Valid.getJamSmc(Jam2, Menit2, Detik2));

                Duration diff = Duration.between(awal, akhir);
                if (diff.isNegative()) {
                    diff = diff.plusHours(24);
                }

                menit = diff.toMinutes();
                String pesan = menit + " menit";

                if (diff.toHours() > 0) {
                    pesan = diff.toHours() + " jam " + (diff.toMinutes() % 60) + " menit";
                }

                Keterangan.setText("(" + pesan + ")");
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void hitungSisaIzin() {
        if (!KdPetugas.getText().isBlank()) {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "with dpbase as (select pegawai.nik, pegawai.mulai_kerja, ? as tgl_sekarang, stts_kerja.izin, case when stts_kerja.izin like '1 Bulan%' then 1 when stts_kerja.izin like '3 Bulan%' then 3 " +
                "when stts_kerja.izin like '6 Bulan%' then 6 when stts_kerja.izin like '12 Bulan%' then 12 end as periode, stts_kerja.hakizin, stts_kerja.max_menit from pegawai inner join stts_kerja on " +
                "pegawai.stts_kerja = stts_kerja.stts where pegawai.nik = ?), dpaniv as (select dpbase.*, if(datediff(makedate(year(dpbase.tgl_sekarang), dayofyear(dpbase.mulai_kerja)), dpbase.tgl_sekarang) >= 0, " +
                "date_sub(makedate(year(dpbase.tgl_sekarang), dayofyear(dpbase.mulai_kerja)), interval 1 year), makedate(year(dpbase.tgl_sekarang), dayofyear(dpbase.mulai_kerja))) as tgl_aniv from dpbase), " +
                "dptmt as (select dpaniv.*, case when dpaniv.izin like '%tmt%' then date_add(dpaniv.tgl_aniv, interval (timestampdiff(month, dpaniv.tgl_aniv, dpaniv.tgl_sekarang) div dpaniv.periode) * " +
                "dpaniv.periode month) else date_add(makedate(year(dpaniv.tgl_sekarang), 1), interval ((month(dpaniv.tgl_sekarang) - 1) div dpaniv.periode) * dpaniv.periode month) end as tmt from dpaniv), " +
                "datapegawai as (select dptmt.*, case when dptmt.izin like '%tmt%' then date_sub(date_add(dptmt.tmt, interval dptmt.periode month), interval 1 day) else last_day(date_add(dptmt.tmt, interval " +
                "(dptmt.periode - 1) month)) end as tat from dptmt) select datapegawai.nik, datapegawai.izin, datapegawai.hakizin, datapegawai.tmt, datapegawai.tat, datapegawai.max_menit, ifnull((select count(*) " +
                "from pengajuan_izin_smc s where s.nik = datapegawai.nik and s.tmt = datapegawai.tmt and s.tat = datapegawai.tat and s.urgensi = ? and s.status != 'Ditolak' and s.normatif = 'Tidak' " +
                (tbObat.getSelectedRow() < 0 ? "" : "and s.no_pengajuan != ? ") + "), 0) as diambil from datapegawai"
            )) {
                int p = 0;
                ps.setString(++p, Valid.getTglSmc(Tanggal));
                ps.setString(++p, KdPetugas.getText());
                ps.setString(++p, Urgensi.getSelectedItem().toString());
                if (tbObat.getSelectedRow() >= 0) {
                    ps.setString(++p, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        izin = rs.getString("izin") == null ? "" : rs.getString("izin");
                        tglTMTKerja = rs.getString("tmt") == null ? "0000-00-00" : rs.getString("tmt");
                        tglTATKerja = rs.getString("tat") == null ? "0000-00-00" : rs.getString("tat");
                        hakIzin = rs.getInt("hakizin");
                        maxmenit = rs.getLong("max_menit");
                        diambil = rs.getInt("diambil");
                        Sisa.setText(String.valueOf(hakIzin - diambil));
                    } else {
                        izin = "";
                        tglTMTKerja = "0000-00-00";
                        tglTATKerja = "0000-00-00";
                        hakIzin = 0;
                        maxmenit = 0;
                        diambil = 0;
                        Sisa.setText("0");
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                izin = "";
                tglTMTKerja = "0000-00-00";
                tglTATKerja = "0000-00-00";
                hakIzin = 0;
                maxmenit = 0;
                diambil = 0;
                Sisa.setText("0");
            }
        }
    }

    private boolean cekMaxMenit() {
        if (tanpaHakIzin() || "Tidak Masuk Kerja".equals(Urgensi.getSelectedItem().toString())) {
            return false;
        }

        return menit > maxmenit;
    }

    private boolean tanpaHakIzin() {
        return izin == null || izin.isBlank() || "Tidak Ada".equals(izin);
    }
}
