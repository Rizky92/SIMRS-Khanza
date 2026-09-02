package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.akuntindakanralan;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.tarifralan;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import keuangan.Jurnal;
import org.apache.commons.lang3.StringUtils;

public final class MasterCariTemplatePaketMCUSMC extends javax.swing.JDialog {
    private static final int KOL_TINDAKAN_KODE = 0;
    private static final int KOL_TINDAKAN_KATEGORI = 2;
    private static final int KOL_TINDAKAN_TARIF = 3;
    private static final int KOL_TINDAKAN_BAGIAN_RS = 4;
    private static final int KOL_TINDAKAN_BHP = 5;
    private static final int KOL_TINDAKAN_JM = 6;
    private static final int KOL_TINDAKAN_KSO = 7;
    private static final int KOL_TINDAKAN_MENEJEMEN = 8;
    private static final int KOL_TINDAKAN_JENIS = 9;
    private static final int KOL_TINDAKAN_KODE_DOKTER = 10;
    private static final int KOL_TINDAKAN_NAMA_DOKTER = 11;

    private final DefaultTableModel tabMode, tabModeRadiologi, tabModePK, tabModeDetailPK, tabModePA, tabModeMB, tabModeDetailMB,
            TabModeTindakan, tabModeTambahanBiaya, tabModePotonganBiaya;
    private final validasi Valid = new validasi();
    private final sekuel Sequel = new sekuel();
    private final Connection koneksi = koneksiDB.condb();
    private int i = 0;
    private String kodedokter = "", tanggaldilakukan = "", jamdilakukan = "", noperawatan = "", norm = "", nomor = "";
    private boolean sukses = true;
    private double ttljmdokter = 0, ttlkso = 0, ttljasasarana = 0, ttlbhp = 0, ttlmenejemen = 0, ttlpendapatan = 0, ttljmperawat = 0;
    private final Jurnal jur = new Jurnal();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private DlgCariDokter dokter;

    public MasterCariTemplatePaketMCUSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 2);
        setSize(656, 250);

        Object[] row = {"No.Template", "Nama Template", "Jenis Bayar", "Tambahan (Rp)", "Potongan (Rp)", "Total (Rp)"};
        tabMode = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbDokter.setModel(tabMode);
        tbDokter.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbDokter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbDokter.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(120);
            } else if (i == 1) {
                column.setPreferredWidth(230);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else {
                column.setPreferredWidth(110);
            }
        }
        tbDokter.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRadiologi = modelPemeriksaan();
        siapkanTabelPemeriksaan(tbPermintaanRadiologi, tabModeRadiologi);

        tabModePK = modelPemeriksaan();
        siapkanTabelPemeriksaan(tbPermintaanPK, tabModePK);

        tabModePA = modelPemeriksaan();
        siapkanTabelPemeriksaan(tbPermintaanPA, tabModePA);

        tabModeMB = modelPemeriksaan();
        siapkanTabelPemeriksaan(tbPermintaanMB, tabModeMB);

        tabModeDetailPK = modelDetailLab();
        siapkanTabelDetailLab(tbDetailPK, tabModeDetailPK);

        tabModeDetailMB = modelDetailLab();
        siapkanTabelDetailLab(tbDetailMB, tabModeDetailMB);

        TabModeTindakan = new DefaultTableModel(null, new Object[] {
            "Kode", "Nama Perawatan/Tindakan", "Kategori", "Tarif/Biaya", "Bagian RS", "BHP", "JM Dokter", "KSO",
            "Menejemen", "Jenis", "Kode Dokter", "Dokter Pemberi Tindakan"
        }) {
            private final Class[] types = new Class[] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class,
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class,
                java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbTindakan.setModel(TabModeTindakan);
        tbTindakan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < TabModeTindakan.getColumnCount(); i++) {
            TableColumn column = tbTindakan.getColumnModel().getColumn(i);
            if (i == KOL_TINDAKAN_KODE) {
                column.setPreferredWidth(90);
            } else if (i == 1) {
                column.setPreferredWidth(240);
            } else if (i == KOL_TINDAKAN_KATEGORI) {
                column.setPreferredWidth(110);
            } else if (i == KOL_TINDAKAN_TARIF) {
                column.setPreferredWidth(90);
            } else if (i == KOL_TINDAKAN_NAMA_DOKTER) {
                column.setPreferredWidth(180);
            } else {
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setPreferredWidth(0);
            }
        }
        tbTindakan.setDefaultRenderer(Object.class, new WarnaTable());
        tbTindakan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int baris = tbTindakan.rowAtPoint(evt.getPoint());
                int kolom = tbTindakan.columnAtPoint(evt.getPoint());
                if (baris < 0 || kolom < 0) {
                    return;
                }
                if (tbTindakan.convertColumnIndexToModel(kolom) == KOL_TINDAKAN_NAMA_DOKTER) {
                    gantiDokter(tbTindakan.convertRowIndexToModel(baris));
                }
            }
        });

        tabModeTambahanBiaya = modelBiaya();
        siapkanTabelBiaya(tbTambahanBiaya, tabModeTambahanBiaya);

        tabModePotonganBiaya = modelBiaya();
        siapkanTabelBiaya(tbPotonganBiaya, tabModePotonganBiaya);

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
    }

    private DefaultTableModel modelPemeriksaan() {
        return new DefaultTableModel(null, new Object[] {"Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            private final Class[] types = new Class[] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
    }

    private DefaultTableModel modelDetailLab() {
        return new DefaultTableModel(null, new Object[] {"Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
    }

    private DefaultTableModel modelBiaya() {
        return new DefaultTableModel(null, new Object[] {"Nama", "Besar Biaya (Rp)"}) {
            private final Class[] types = new Class[] {
                java.lang.String.class, java.lang.Double.class
            };

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
    }

    private void siapkanTabelPemeriksaan(widget.Table tabel, DefaultTableModel model) {
        tabel.setModel(model);
        tabel.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int k = 0; k < model.getColumnCount(); k++) {
            TableColumn column = tabel.getColumnModel().getColumn(k);
            if (k == 0) {
                column.setPreferredWidth(130);
            } else if (k == 1) {
                column.setPreferredWidth(440);
            } else {
                column.setPreferredWidth(110);
            }
        }
        tabel.setDefaultRenderer(Object.class, new WarnaTable());
    }

    private void siapkanTabelDetailLab(widget.Table tabel, DefaultTableModel model) {
        tabel.setModel(model);
        tabel.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int k = 0; k < model.getColumnCount(); k++) {
            TableColumn column = tabel.getColumnModel().getColumn(k);
            if (k == 0) {
                column.setPreferredWidth(330);
            } else if (k == 1) {
                column.setPreferredWidth(60);
            } else if (k == 2) {
                column.setPreferredWidth(290);
            } else {
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setPreferredWidth(0);
            }
        }
        tabel.setDefaultRenderer(Object.class, new WarnaTable());
    }

    private void siapkanTabelBiaya(widget.Table tabel, DefaultTableModel model) {
        tabel.setModel(model);
        tabel.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int k = 0; k < model.getColumnCount(); k++) {
            TableColumn column = tabel.getColumnModel().getColumn(k);
            if (k == 0) {
                column.setPreferredWidth(520);
            } else {
                column.setPreferredWidth(160);
            }
        }
        tabel.setDefaultRenderer(Object.class, new WarnaTable());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbDokter = new widget.Table();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnSimpan = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnTambah = new widget.Button();
        BtnKeluar = new widget.Button();
        scrollPane2 = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel15 = new widget.Label();
        Scroll3 = new widget.ScrollPane();
        tbPermintaanRadiologi = new widget.Table();
        jLabel16 = new widget.Label();
        Scroll4 = new widget.ScrollPane();
        tbPermintaanPK = new widget.Table();
        Scroll5 = new widget.ScrollPane();
        tbDetailPK = new widget.Table();
        jLabel17 = new widget.Label();
        Scroll6 = new widget.ScrollPane();
        tbPermintaanPA = new widget.Table();
        jLabel18 = new widget.Label();
        Scroll7 = new widget.ScrollPane();
        tbPermintaanMB = new widget.Table();
        Scroll8 = new widget.ScrollPane();
        tbDetailMB = new widget.Table();
        jLabel21 = new widget.Label();
        Scroll12 = new widget.ScrollPane();
        tbTindakan = new widget.Table();
        jLabel22 = new widget.Label();
        Scroll13 = new widget.ScrollPane();
        tbTambahanBiaya = new widget.Table();
        jLabel23 = new widget.Label();
        Scroll14 = new widget.ScrollPane();
        tbPotonganBiaya = new widget.Table();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Cari Template Paket MCU ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(310, 402));

        tbDokter.setAutoCreateRowSorter(true);
        tbDokter.setName("tbDokter"); // NOI18N
        tbDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDokterMouseClicked(evt);
            }
        });
        Scroll.setViewportView(tbDokter);

        internalFrame1.add(Scroll, java.awt.BorderLayout.WEST);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(312, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi3.add(TCari);

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
        panelisi3.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("2Alt+2");
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
        panelisi3.add(BtnAll);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16i.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(28, 23));
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
        panelisi3.add(BtnSimpan);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi3.add(LCount);

        BtnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambah.setMnemonic('3');
        BtnTambah.setToolTipText("Alt+3");
        BtnTambah.setName("BtnTambah"); // NOI18N
        BtnTambah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });
        panelisi3.add(BtnTambah);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('4');
        BtnKeluar.setToolTipText("Alt+4");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        scrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "Detail Template :", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        scrollPane2.setName("scrollPane2"); // NOI18N

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(730, 1520));
        FormInput.setLayout(null);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Permintaan Radiologi :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(16, 10, 120, 23);

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        tbPermintaanRadiologi.setName("tbPermintaanRadiologi"); // NOI18N
        Scroll3.setViewportView(tbPermintaanRadiologi);

        FormInput.add(Scroll3);
        Scroll3.setBounds(16, 30, 700, 123);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Permintaan Laborat Patologi Klinis :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(16, 160, 190, 23);

        Scroll4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);

        tbPermintaanPK.setName("tbPermintaanPK"); // NOI18N
        Scroll4.setViewportView(tbPermintaanPK);

        FormInput.add(Scroll4);
        Scroll4.setBounds(16, 180, 700, 123);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);

        tbDetailPK.setName("tbDetailPK"); // NOI18N
        Scroll5.setViewportView(tbDetailPK);

        FormInput.add(Scroll5);
        Scroll5.setBounds(16, 310, 700, 223);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Permintaan Laborat Patologi Anatomi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(16, 540, 190, 23);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbPermintaanPA.setName("tbPermintaanPA"); // NOI18N
        Scroll6.setViewportView(tbPermintaanPA);

        FormInput.add(Scroll6);
        Scroll6.setBounds(16, 560, 700, 133);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Permintaan Laborat Mikrobiologi & Bio Molekuler :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(16, 700, 260, 23);

        Scroll7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);

        tbPermintaanMB.setName("tbPermintaanMB"); // NOI18N
        Scroll7.setViewportView(tbPermintaanMB);

        FormInput.add(Scroll7);
        Scroll7.setBounds(16, 720, 700, 113);

        Scroll8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);

        tbDetailMB.setName("tbDetailMB"); // NOI18N
        Scroll8.setViewportView(tbDetailMB);

        FormInput.add(Scroll8);
        Scroll8.setBounds(16, 840, 700, 223);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Tindakan :");
        jLabel21.setName("jLabel21"); // NOI18N
        FormInput.add(jLabel21);
        jLabel21.setBounds(16, 1070, 120, 23);

        Scroll12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll12.setName("Scroll12"); // NOI18N
        Scroll12.setOpaque(true);

        tbTindakan.setName("tbTindakan"); // NOI18N
        Scroll12.setViewportView(tbTindakan);

        FormInput.add(Scroll12);
        Scroll12.setBounds(16, 1090, 700, 123);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Tambahan Biaya :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(16, 1220, 120, 23);

        Scroll13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll13.setName("Scroll13"); // NOI18N
        Scroll13.setOpaque(true);

        tbTambahanBiaya.setName("tbTambahanBiaya"); // NOI18N
        Scroll13.setViewportView(tbTambahanBiaya);

        FormInput.add(Scroll13);
        Scroll13.setBounds(16, 1240, 700, 123);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Potongan Biaya :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(16, 1370, 120, 23);

        Scroll14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll14.setName("Scroll14"); // NOI18N
        Scroll14.setOpaque(true);

        tbPotonganBiaya.setName("tbPotonganBiaya"); // NOI18N
        Scroll14.setViewportView(tbPotonganBiaya);

        FormInput.add(Scroll14);
        Scroll14.setBounds(16, 1390, 700, 123);

        scrollPane2.setViewportView(FormInput);

        internalFrame1.add(scrollPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbDokter.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() -> tampil());
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
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, BtnSimpan);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        MasterTemplatePaketMCUSMC form = new MasterTemplatePaketMCUSMC(null, false);
        form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        form.setLocationRelativeTo(internalFrame1);
        form.setAlwaysOnTop(false);
        form.isCek();
        form.emptTeks();
        form.setTampil();
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        emptTeks();
    }//GEN-LAST:event_formWindowActivated

    private void tbDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDokterMouseClicked
        runBackground(() -> tampilDetailTemplate());
    }//GEN-LAST:event_tbDokterMouseClicked

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        terapkanPaket();
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnAll, BtnTambah);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tarifralan.SetTarifRalan();
        if (akuntindakanralan.getSuspen_Piutang_Tindakan_Ralan().equals("")) {
            akuntindakanralan.SetAkunTindakanRalan();
        }
        runBackground(() -> tampil());
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        runBackground(() -> tampil());
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        runBackground(() -> tampil());
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        runBackground(() -> tampil());
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
            MasterCariTemplatePaketMCUSMC dialog = new MasterCariTemplatePaketMCUSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambah;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll12;
    private widget.ScrollPane Scroll13;
    private widget.ScrollPane Scroll14;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label label10;
    private widget.Label label9;
    private widget.panelisi panelisi3;
    private widget.ScrollPane scrollPane2;
    public widget.Table tbDetailMB;
    public widget.Table tbDetailPK;
    private widget.Table tbDokter;
    public widget.Table tbPermintaanMB;
    public widget.Table tbPermintaanPA;
    public widget.Table tbPermintaanPK;
    public widget.Table tbPermintaanRadiologi;
    public widget.Table tbPotonganBiaya;
    public widget.Table tbTambahanBiaya;
    public widget.Table tbTindakan;
    // End of variables declaration//GEN-END:variables

    private String kdPjPasien() {
        if (noperawatan.isEmpty()) {
            return "";
        }
        String kdPj = Sequel.cariIsiSmc("select kd_pj from reg_periksa where no_rawat = ?", noperawatan);
        return null == kdPj ? "" : kdPj;
    }

    private void tampil() {
        List<String> kata = new ArrayList<>();
        if (!TCari.getText().trim().isEmpty()) {
            kata.addAll(Arrays.asList(StringUtils.split(TCari.getText().trim())));
        }

        StringBuilder sb = new StringBuilder(
                "select template_paket_mcu_smc.no_template, template_paket_mcu_smc.keterangan, penjab.png_jawab, "
                + "template_paket_mcu_smc.tambahan_rp, template_paket_mcu_smc.diskon_rp from template_paket_mcu_smc "
                + "inner join penjab on template_paket_mcu_smc.kd_pj = penjab.kd_pj where 1 = 1 ");

        List<String> paramAwal = new ArrayList<>();
        String kdPj = kdPjPasien();
        if ("Yes".equals(tarifralan.getCaraBayarRalan()) && !kdPj.isEmpty()) {
            sb.append("and (template_paket_mcu_smc.kd_pj = ? or template_paket_mcu_smc.kd_pj = '-') ");
            paramAwal.add(kdPj);
        }
        for (int k = 0; k < kata.size(); k++) {
            sb.append("and (template_paket_mcu_smc.no_template like ? or template_paket_mcu_smc.keterangan like ? or penjab.png_jawab like ?) ");
        }
        sb.append("order by template_paket_mcu_smc.keterangan");

        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps = koneksi.prepareStatement(sb.toString())) {
            int p = 0;
            for (String awal : paramAwal) {
                ps.setString(++p, awal);
            }
            for (String q : kata) {
                ps.setString(++p, "%" + q + "%");
                ps.setString(++p, "%" + q + "%");
                ps.setString(++p, "%" + q + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double tambahanRp = rs.getDouble("tambahan_rp");
                    double diskonRp = rs.getDouble("diskon_rp");
                    tabMode.addRow(new Object[] {
                        rs.getString("no_template"), rs.getString("keterangan"), rs.getString("png_jawab"),
                        Valid.SetAngka(tambahanRp), Valid.SetAngka(diskonRp),
                        Valid.SetAngka(totalTemplate(rs.getString("no_template")) + tambahanRp - diskonRp)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    private double totalTemplate(String noTemplate) {
        String sql =
                "select ifnull((select sum(jns_perawatan_radiologi.total_byr) from template_paket_mcu_smc_permintaan_radiologi "
                + "inner join jns_perawatan_radiologi on template_paket_mcu_smc_permintaan_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw "
                + "where template_paket_mcu_smc_permintaan_radiologi.no_template = ?), 0) + "
                + "ifnull((select sum(jns_perawatan_lab.total_byr) from template_paket_mcu_smc_permintaan_lab "
                + "inner join jns_perawatan_lab on template_paket_mcu_smc_permintaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw "
                + "where template_paket_mcu_smc_permintaan_lab.no_template = ?), 0) + "
                + "ifnull((select sum(jns_perawatan.total_byrdr) from template_paket_mcu_smc_tindakan_dr "
                + "inner join jns_perawatan on template_paket_mcu_smc_tindakan_dr.kd_jenis_prw = jns_perawatan.kd_jenis_prw "
                + "where template_paket_mcu_smc_tindakan_dr.no_template = ?), 0) + "
                + "ifnull((select sum(jns_perawatan.total_byrdrpr) from template_paket_mcu_smc_tindakan_drpr "
                + "inner join jns_perawatan on template_paket_mcu_smc_tindakan_drpr.kd_jenis_prw = jns_perawatan.kd_jenis_prw "
                + "where template_paket_mcu_smc_tindakan_drpr.no_template = ?), 0) + "
                + "ifnull((select sum(jns_perawatan.total_byrpr) from template_paket_mcu_smc_tindakan_pr "
                + "inner join jns_perawatan on template_paket_mcu_smc_tindakan_pr.kd_jenis_prw = jns_perawatan.kd_jenis_prw "
                + "where template_paket_mcu_smc_tindakan_pr.no_template = ?), 0)";
        return Sequel.cariDoubleSmc(sql, 0, noTemplate, noTemplate, noTemplate, noTemplate, noTemplate);
    }

    public void tampil2() {
        runBackground(() -> tampil());
    }

    public void emptTeks() {
        TCari.requestFocus();
    }

    public JTable getTable() {
        return tbDokter;
    }

    public void setDokter(String kode, String tanggal, String jam, String norawat, String nomorrm) {
        this.kodedokter = kode;
        this.tanggaldilakukan = tanggal;
        this.jamdilakukan = jam;
        this.noperawatan = norawat;
        this.norm = nomorrm;
    }

    public void isCek() {
        BtnTambah.setEnabled(akses.getmaster_template_paket_mcu_smc());
    }

    private String noTemplateTerpilih() {
        if (tbDokter.getSelectedRow() == -1) {
            return "";
        }
        return tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString();
    }

    private void tampilDetailTemplate() {
        String no = noTemplateTerpilih();

        Valid.tabelKosong(tabModeRadiologi);
        Valid.tabelKosong(tabModePK);
        Valid.tabelKosong(tabModePA);
        Valid.tabelKosong(tabModeMB);
        Valid.tabelKosong(tabModeDetailPK);
        Valid.tabelKosong(tabModeDetailMB);
        Valid.tabelKosong(TabModeTindakan);
        Valid.tabelKosong(tabModeTambahanBiaya);
        Valid.tabelKosong(tabModePotonganBiaya);

        if (no.isEmpty()) {
            return;
        }

        muatRadiologi(no);
        muatLab(no, "PK", tabModePK, tabModeDetailPK);
        muatLab(no, "PA", tabModePA, null);
        muatLab(no, "MB", tabModeMB, tabModeDetailMB);
        muatTindakan(no);
        muatBiaya(no, "template_paket_mcu_smc_tambahan_biaya", tabModeTambahanBiaya);
        muatBiaya(no, "template_paket_mcu_smc_potongan_biaya", tabModePotonganBiaya);
    }

    private void muatRadiologi(String no) {
        String sql = "select template_paket_mcu_smc_permintaan_radiologi.kd_jenis_prw, jns_perawatan_radiologi.nm_perawatan, "
                + "jns_perawatan_radiologi.total_byr from template_paket_mcu_smc_permintaan_radiologi "
                + "inner join jns_perawatan_radiologi on template_paket_mcu_smc_permintaan_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw "
                + "where template_paket_mcu_smc_permintaan_radiologi.no_template = ? order by jns_perawatan_radiologi.nm_perawatan";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, no);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeRadiologi.addRow(new Object[] {
                        rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"), rs.getDouble("total_byr")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void muatLab(String no, String kategori, DefaultTableModel model, DefaultTableModel detail) {
        String sql = "select template_paket_mcu_smc_permintaan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan, "
                + "jns_perawatan_lab.total_byr from template_paket_mcu_smc_permintaan_lab "
                + "inner join jns_perawatan_lab on template_paket_mcu_smc_permintaan_lab.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw "
                + "where template_paket_mcu_smc_permintaan_lab.no_template = ? and jns_perawatan_lab.kategori = ? "
                + "order by jns_perawatan_lab.nm_perawatan";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, no);
            ps.setString(2, kategori);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"), rs.getDouble("total_byr")
                    });
                    if (null != detail) {
                        muatDetailLab(no, rs.getString("kd_jenis_prw"), detail);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void muatDetailLab(String no, String kdJenisPrw, DefaultTableModel detail) {
        String sql = "select template_paket_mcu_smc_detail_permintaan_lab.id_template, template_laboratorium.Pemeriksaan, "
                + "template_laboratorium.satuan, template_laboratorium.nilai_rujukan_ld, template_laboratorium.nilai_rujukan_la, "
                + "template_laboratorium.nilai_rujukan_pd, template_laboratorium.nilai_rujukan_pa "
                + "from template_paket_mcu_smc_detail_permintaan_lab inner join template_laboratorium "
                + "on template_paket_mcu_smc_detail_permintaan_lab.id_template = template_laboratorium.id_template "
                + "where template_paket_mcu_smc_detail_permintaan_lab.no_template = ? "
                + "and template_paket_mcu_smc_detail_permintaan_lab.kd_jenis_prw = ? order by template_laboratorium.urut";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, no);
            ps.setString(2, kdJenisPrw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ld = "", la = "", pd = "", pa = "";
                    if (!"".equals(rs.getString("nilai_rujukan_ld"))) {
                        ld = "LD : " + rs.getString("nilai_rujukan_ld");
                    }
                    if (!"".equals(rs.getString("nilai_rujukan_la"))) {
                        la = ", LA : " + rs.getString("nilai_rujukan_la");
                    }
                    if (!"".equals(rs.getString("nilai_rujukan_pd"))) {
                        pd = ", PD : " + rs.getString("nilai_rujukan_pd");
                    }
                    if (!"".equals(rs.getString("nilai_rujukan_pa"))) {
                        pa = ", PA : " + rs.getString("nilai_rujukan_pa");
                    }
                    detail.addRow(new Object[] {
                        "   " + rs.getString("Pemeriksaan"), rs.getString("satuan"), ld + la + pd + pa,
                        rs.getString("id_template"), kdJenisPrw
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void muatTindakan(String no) {
        muatTindakanJenis(no, "template_paket_mcu_smc_tindakan_dr", "dr", "total_byrdr", "tarif_tindakandr");
        muatTindakanJenis(no, "template_paket_mcu_smc_tindakan_drpr", "drpr", "total_byrdrpr", "tarif_tindakandr");
        muatTindakanJenis(no, "template_paket_mcu_smc_tindakan_pr", "pr", "total_byrpr", "tarif_tindakanpr");
    }

    private void muatTindakanJenis(String no, String tabel, String jenis, String kolomTotal, String kolomJm) {
        String sql = "select " + tabel + ".kd_jenis_prw, jns_perawatan.nm_perawatan, kategori_perawatan.nm_kategori, "
                + "jns_perawatan." + kolomTotal + " as tarif, jns_perawatan.material, jns_perawatan.bhp, "
                + "jns_perawatan." + kolomJm + " as jm, jns_perawatan.kso, jns_perawatan.menejemen, "
                + tabel + ".kd_dokter, dokter.nm_dokter from " + tabel + " "
                + "inner join jns_perawatan on " + tabel + ".kd_jenis_prw = jns_perawatan.kd_jenis_prw "
                + "inner join kategori_perawatan on jns_perawatan.kd_kategori = kategori_perawatan.kd_kategori "
                + "left join dokter on " + tabel + ".kd_dokter = dokter.kd_dokter "
                + "where " + tabel + ".no_template = ? order by jns_perawatan.nm_perawatan";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, no);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String kdDokter = rs.getString("kd_dokter");
                    String nmDokter = rs.getString("nm_dokter");
                    if (null == kdDokter || kdDokter.trim().isEmpty()) {
                        kdDokter = kodedokter;
                        nmDokter = Sequel.cariIsiSmc("select nm_dokter from dokter where kd_dokter = ?", kodedokter);
                    }
                    TabModeTindakan.addRow(new Object[] {
                        rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"), rs.getString("nm_kategori"),
                        rs.getDouble("tarif"), rs.getDouble("material"), rs.getDouble("bhp"), rs.getDouble("jm"),
                        rs.getDouble("kso"), rs.getDouble("menejemen"), jenis,
                        null == kdDokter ? "" : kdDokter, null == nmDokter ? "" : nmDokter
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void muatBiaya(String no, String tabel, DefaultTableModel model) {
        try (PreparedStatement ps = koneksi.prepareStatement("select nama, besar_biaya from " + tabel + " where no_template = ? order by nama")) {
            ps.setString(1, no);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {rs.getString("nama"), rs.getDouble("besar_biaya")});
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void gantiDokter(int baris) {
        if (baris < 0) {
            return;
        }
        if (dokter == null || !dokter.isDisplayable()) {
            dokter = new DlgCariDokter(null, true);
            dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokter.setLocationRelativeTo(internalFrame1);
        }
        JTable tabelDokter = dokter.getTable();
        dokter.isCek();
        dokter.setVisible(true);
        if (tabelDokter.getSelectedRow() != -1) {
            TabModeTindakan.setValueAt(tabelDokter.getValueAt(tabelDokter.getSelectedRow(), 0).toString(), baris, KOL_TINDAKAN_KODE_DOKTER);
            TabModeTindakan.setValueAt(tabelDokter.getValueAt(tabelDokter.getSelectedRow(), 1).toString(), baris, KOL_TINDAKAN_NAMA_DOKTER);
        }
        dokter = null;
    }

    private String angkaTeks(int baris, int kolom) {
        Object nilai = TabModeTindakan.getValueAt(baris, kolom);
        if (null == nilai) {
            return "0";
        }
        return nilai.toString();
    }

    private boolean butuhPetugas() {
        for (i = 0; i < TabModeTindakan.getRowCount(); i++) {
            String jenis = String.valueOf(TabModeTindakan.getValueAt(i, KOL_TINDAKAN_JENIS));
            if ("pr".equals(jenis) || "drpr".equals(jenis)) {
                return true;
            }
        }
        return false;
    }

    private void terapkanPaket() {
        if (tbDokter.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih paket MCU terlebih dahulu...!!!");
            return;
        }
        if (noperawatan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No.Rawat masih kosong, paket hanya bisa diterapkan dari data pasien...!!!");
            return;
        }
        if (butuhPetugas() && !Sequel.cariExistsSmc("select nip from petugas where nip = ?", akses.getkode())) {
            JOptionPane.showMessageDialog(null, "Paket ini memuat tindakan petugas, namun akun anda tidak terdaftar sebagai petugas...!!!");
            return;
        }

        String keterangan = tabMode.getValueAt(tbDokter.getSelectedRow(), 1).toString();

        Sequel.AutoComitFalse();
        sukses = true;

        simpanPermintaanRadiologi(keterangan);
        simpanPermintaanPK(keterangan);
        simpanPermintaanPA(keterangan);
        simpanPermintaanMB(keterangan);
        simpanTindakan();
        simpanBiaya();

        if (sukses == true) {
            Sequel.Commit();
            JOptionPane.showMessageDialog(null, "Paket MCU berhasil diterapkan ke pasien.");
            dispose();
        } else {
            Sequel.RollBack();
            JOptionPane.showMessageDialog(null, "Gagal menerapkan paket MCU, perubahan dibatalkan...!!!");
        }
    }

    private void simpanPermintaanRadiologi(String keterangan) {
        if (tabModeRadiologi.getRowCount() == 0) {
            return;
        }
        nomor = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(permintaan_radiologi.noorder,4),signed)),0) from permintaan_radiologi where permintaan_radiologi.tgl_permintaan='" + tanggaldilakukan + "'", "PR" + tanggaldilakukan.replaceAll("-", ""), 4);
        if (Sequel.menyimpantf2("permintaan_radiologi", "?,?,?,?,?,?,?,?,?,?,?,?", "No.Permintaan Radiologi", 12, new String[] {
            nomor, noperawatan, tanggaldilakukan, jamdilakukan, "0000-00-00", "00:00:00", "0000-00-00", "00:00:00", kodedokter, "ralan", "-", keterangan
        }) == true) {
            for (i = 0; i < tabModeRadiologi.getRowCount(); i++) {
                if (Sequel.menyimpantf2("permintaan_pemeriksaan_radiologi", "?,?,?", "Permintaan Radiologi " + tabModeRadiologi.getValueAt(i, 1).toString(), 3, new String[] {
                    nomor, tabModeRadiologi.getValueAt(i, 0).toString(), "Belum"
                }) == false) {
                    sukses = false;
                }
            }
        } else {
            sukses = false;
        }
    }

    private void simpanPermintaanPK(String keterangan) {
        if (tabModePK.getRowCount() == 0) {
            return;
        }
        nomor = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(permintaan_lab.noorder,4),signed)),0) from permintaan_lab where permintaan_lab.tgl_permintaan='" + tanggaldilakukan + "' ", "PK" + tanggaldilakukan.replaceAll("-", ""), 4);
        if (Sequel.menyimpantf2("permintaan_lab", "?,?,?,?,?,?,?,?,?,?,?,?", "No.Permintaan", 12, new String[] {
            nomor, noperawatan, tanggaldilakukan, jamdilakukan, "0000-00-00", "00:00:00", "0000-00-00", "00:00:00", kodedokter, "ralan", "-", keterangan
        }) == true) {
            for (i = 0; i < tabModePK.getRowCount(); i++) {
                if (Sequel.menyimpantf2("permintaan_pemeriksaan_lab", "?,?,?", "Permintaan Lab " + tabModePK.getValueAt(i, 1).toString(), 3, new String[] {
                    nomor, tabModePK.getValueAt(i, 0).toString(), "Belum"
                }) == false) {
                    sukses = false;
                }
            }
            for (i = 0; i < tabModeDetailPK.getRowCount(); i++) {
                if (!tabModeDetailPK.getValueAt(i, 3).toString().equals("")) {
                    if (Sequel.menyimpantf2("permintaan_detail_permintaan_lab", "?,?,?,?", "Detail Permintaan Lab " + tabModeDetailPK.getValueAt(i, 0).toString().replaceAll("   ", ""), 4, new String[] {
                        nomor, tabModeDetailPK.getValueAt(i, 4).toString(), tabModeDetailPK.getValueAt(i, 3).toString(), "Belum"
                    }) == false) {
                        sukses = false;
                    }
                }
            }
        } else {
            sukses = false;
        }
    }

    private void simpanPermintaanPA(String keterangan) {
        if (tabModePA.getRowCount() == 0) {
            return;
        }
        nomor = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(permintaan_labpa.noorder,4),signed)),0) from permintaan_labpa where permintaan_labpa.tgl_permintaan='" + tanggaldilakukan + "' ", "PA" + tanggaldilakukan.replaceAll("-", ""), 4);
        if (Sequel.menyimpantf2("permintaan_labpa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Permintaan", 20, new String[] {
            nomor, noperawatan, tanggaldilakukan, jamdilakukan, "0000-00-00", "00:00:00", "0000-00-00", "00:00:00", kodedokter, "ralan", "-", keterangan, tanggaldilakukan, "-", "-", "-", "-", "0000-00-00", "-", "-"
        }) == true) {
            for (i = 0; i < tabModePA.getRowCount(); i++) {
                if (Sequel.menyimpantf2("permintaan_pemeriksaan_labpa", "?,?,?", "Pemeriksaan Lab PA " + tabModePA.getValueAt(i, 1).toString(), 3, new String[] {
                    nomor, tabModePA.getValueAt(i, 0).toString(), "Belum"
                }) == false) {
                    sukses = false;
                }
            }
        } else {
            sukses = false;
        }
    }

    private void simpanPermintaanMB(String keterangan) {
        if (tabModeMB.getRowCount() == 0) {
            return;
        }
        nomor = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(permintaan_labmb.noorder,4),signed)),0) from permintaan_labmb where permintaan_labmb.tgl_permintaan='" + tanggaldilakukan + "' ", "MB" + tanggaldilakukan.replaceAll("-", ""), 4);
        if (Sequel.menyimpantf2("permintaan_labmb", "?,?,?,?,?,?,?,?,?,?,?,?", "No.Permintaan", 12, new String[] {
            nomor, noperawatan, tanggaldilakukan, jamdilakukan, "0000-00-00", "00:00:00", "0000-00-00", "00:00:00", kodedokter, "ralan", "-", keterangan
        }) == true) {
            for (i = 0; i < tabModeMB.getRowCount(); i++) {
                if (Sequel.menyimpantf2("permintaan_pemeriksaan_labmb", "?,?,?", "Permintaan Lab MB " + tabModeMB.getValueAt(i, 1).toString(), 3, new String[] {
                    nomor, tabModeMB.getValueAt(i, 0).toString(), "Belum"
                }) == false) {
                    sukses = false;
                }
            }
            for (i = 0; i < tabModeDetailMB.getRowCount(); i++) {
                if (!tabModeDetailMB.getValueAt(i, 3).toString().equals("")) {
                    if (Sequel.menyimpantf2("permintaan_detail_permintaan_labmb", "?,?,?,?", "Detail Permintaan Lab MB " + tabModeDetailMB.getValueAt(i, 0).toString().replaceAll("   ", ""), 4, new String[] {
                        nomor, tabModeDetailMB.getValueAt(i, 4).toString(), tabModeDetailMB.getValueAt(i, 3).toString(), "Belum"
                    }) == false) {
                        sukses = false;
                    }
                }
            }
        } else {
            sukses = false;
        }
    }

    private void simpanTindakan() {
        if (TabModeTindakan.getRowCount() == 0) {
            return;
        }

        ttljmdokter = 0;
        ttljmperawat = 0;
        ttlkso = 0;
        ttlpendapatan = 0;
        ttljasasarana = 0;
        ttlbhp = 0;
        ttlmenejemen = 0;

        for (i = 0; i < TabModeTindakan.getRowCount(); i++) {
            String jenis = String.valueOf(TabModeTindakan.getValueAt(i, KOL_TINDAKAN_JENIS));
            String kdDokter = String.valueOf(TabModeTindakan.getValueAt(i, KOL_TINDAKAN_KODE_DOKTER));
            if (kdDokter.trim().isEmpty() || "null".equals(kdDokter)) {
                kdDokter = kodedokter;
            }

            boolean tersimpan;
            if ("pr".equals(jenis)) {
                tersimpan = Sequel.menyimpantf2("rawat_jl_pr", "?,?,?,?,?,?,?,?,?,?,?,?", "Tindakan Petugas " + TabModeTindakan.getValueAt(i, 1).toString(), 12, new String[] {
                    noperawatan, angkaTeks(i, KOL_TINDAKAN_KODE), akses.getkode(), tanggaldilakukan, jamdilakukan,
                    angkaTeks(i, KOL_TINDAKAN_BAGIAN_RS), angkaTeks(i, KOL_TINDAKAN_BHP), angkaTeks(i, KOL_TINDAKAN_JM),
                    angkaTeks(i, KOL_TINDAKAN_KSO), angkaTeks(i, KOL_TINDAKAN_MENEJEMEN), angkaTeks(i, KOL_TINDAKAN_TARIF), "Belum"
                });
                if (tersimpan) {
                    ttljmperawat = ttljmperawat + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_JM));
                }
            } else if ("drpr".equals(jenis)) {
                tersimpan = Sequel.menyimpantf2("rawat_jl_drpr", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Tindakan Dokter & Petugas " + TabModeTindakan.getValueAt(i, 1).toString(), 14, new String[] {
                    noperawatan, angkaTeks(i, KOL_TINDAKAN_KODE), kdDokter, akses.getkode(), tanggaldilakukan, jamdilakukan,
                    angkaTeks(i, KOL_TINDAKAN_BAGIAN_RS), angkaTeks(i, KOL_TINDAKAN_BHP), angkaTeks(i, KOL_TINDAKAN_JM), "0",
                    angkaTeks(i, KOL_TINDAKAN_KSO), angkaTeks(i, KOL_TINDAKAN_MENEJEMEN), angkaTeks(i, KOL_TINDAKAN_TARIF), "Belum"
                });
                if (tersimpan) {
                    ttljmdokter = ttljmdokter + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_JM));
                }
            } else {
                tersimpan = Sequel.menyimpantf2("rawat_jl_dr", "?,?,?,?,?,?,?,?,?,?,?,?", "Tindakan Dokter " + TabModeTindakan.getValueAt(i, 1).toString(), 12, new String[] {
                    noperawatan, angkaTeks(i, KOL_TINDAKAN_KODE), kdDokter, tanggaldilakukan, jamdilakukan,
                    angkaTeks(i, KOL_TINDAKAN_BAGIAN_RS), angkaTeks(i, KOL_TINDAKAN_BHP), angkaTeks(i, KOL_TINDAKAN_JM),
                    angkaTeks(i, KOL_TINDAKAN_KSO), angkaTeks(i, KOL_TINDAKAN_MENEJEMEN), angkaTeks(i, KOL_TINDAKAN_TARIF), "Belum"
                });
                if (tersimpan) {
                    ttljmdokter = ttljmdokter + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_JM));
                }
            }

            if (tersimpan) {
                ttlpendapatan = ttlpendapatan + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_TARIF));
                ttljasasarana = ttljasasarana + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_BAGIAN_RS));
                ttlbhp = ttlbhp + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_BHP));
                ttlkso = ttlkso + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_KSO));
                ttlmenejemen = ttlmenejemen + Double.parseDouble(angkaTeks(i, KOL_TINDAKAN_MENEJEMEN));
            } else {
                sukses = false;
            }
        }

        if (sukses == true) {
            simpanJurnalTindakan();
        }
    }

    private void simpanJurnalTindakan() {
        Sequel.deleteTampJurnal();
        if (ttlpendapatan > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getSuspen_Piutang_Tindakan_Ralan(), "Suspen Piutang Tindakan Ralan", ttlpendapatan, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getTindakan_Ralan(), "Pendapatan Tindakan Rawat Jalan", 0, ttlpendapatan) == false) {
                sukses = false;
            }
        }
        if (ttljmdokter > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getBeban_Jasa_Medik_Dokter_Tindakan_Ralan(), "Beban Jasa Medik Dokter Tindakan Ralan", ttljmdokter, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getUtang_Jasa_Medik_Dokter_Tindakan_Ralan(), "Utang Jasa Medik Dokter Tindakan Ralan", 0, ttljmdokter) == false) {
                sukses = false;
            }
        }
        if (ttljmperawat > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getBeban_Jasa_Medik_Paramedis_Tindakan_Ralan(), "Beban Jasa Medik Paramedis Tindakan Ralan", ttljmperawat, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getUtang_Jasa_Medik_Paramedis_Tindakan_Ralan(), "Utang Jasa Medik Paramedis Tindakan Ralan", 0, ttljmperawat) == false) {
                sukses = false;
            }
        }
        if (ttlkso > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getBeban_KSO_Tindakan_Ralan(), "Beban KSO Tindakan Ralan", ttlkso, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getUtang_KSO_Tindakan_Ralan(), "Utang KSO Tindakan Ralan", 0, ttlkso) == false) {
                sukses = false;
            }
        }
        if (ttljasasarana > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getBeban_Jasa_Sarana_Tindakan_Ralan(), "Beban Jasa Sarana Tindakan Ralan", ttljasasarana, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getUtang_Jasa_Sarana_Tindakan_Ralan(), "Utang Jasa Sarana Tindakan Ralan", 0, ttljasasarana) == false) {
                sukses = false;
            }
        }
        if (ttlbhp > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getHPP_BHP_Tindakan_Ralan(), "HPP BHP Tindakan Ralan", ttlbhp, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getPersediaan_BHP_Tindakan_Ralan(), "Persediaan BHP Tindakan Ralan", 0, ttlbhp) == false) {
                sukses = false;
            }
        }
        if (ttlmenejemen > 0) {
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getBeban_Jasa_Menejemen_Tindakan_Ralan(), "Beban Jasa Manajemen Tindakan Ralan", ttlmenejemen, 0) == false) {
                sukses = false;
            }
            if (Sequel.insertOrUpdateTampJurnal(akuntindakanralan.getUtang_Jasa_Menejemen_Tindakan_Ralan(), "Utang Jasa Manajemen Tindakan Ralan", 0, ttlmenejemen) == false) {
                sukses = false;
            }
        }
        if (sukses == true) {
            sukses = jur.simpanJurnal(noperawatan, "U", "TINDAKAN RAWAT JALAN PASIEN " + noperawatan + " DIPOSTING OLEH " + akses.getkode());
        }
    }

    private void simpanBiaya() {
        for (i = 0; i < tabModeTambahanBiaya.getRowCount(); i++) {
            if (Sequel.menyimpantf2("tambahan_biaya", "?,?,?", "Tambahan Biaya " + tabModeTambahanBiaya.getValueAt(i, 0).toString(), 3, new String[] {
                noperawatan, tabModeTambahanBiaya.getValueAt(i, 0).toString(), tabModeTambahanBiaya.getValueAt(i, 1).toString()
            }) == false) {
                sukses = false;
            }
        }
        for (i = 0; i < tabModePotonganBiaya.getRowCount(); i++) {
            double besar = Double.parseDouble(tabModePotonganBiaya.getValueAt(i, 1).toString());
            if (Sequel.menyimpantf2("tambahan_biaya", "?,?,?", "Potongan Biaya " + tabModePotonganBiaya.getValueAt(i, 0).toString(), 3, new String[] {
                noperawatan, tabModePotonganBiaya.getValueAt(i, 0).toString(), "" + (besar * -1)
            }) == false) {
                sukses = false;
            }
        }
    }

    private void runBackground(Runnable task) {
        if (ceksukses) {
            return;
        }
        if (executor.isShutdown() || executor.isTerminated()) {
            return;
        }
        if (!isDisplayable()) {
            return;
        }

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }

    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
