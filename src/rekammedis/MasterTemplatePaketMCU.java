package rekammedis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.WarnaTable2;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import inventory.DlgCariAturanPakai;
import inventory.DlgCariMetodeRacik;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;

public class MasterTemplatePaketMCU extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeRadiologi, tabModePK, tabModeDetailPK,
        tabModePA, tabModeMB, tabModeDetailMB, TabModeTindakan;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps, ps2;
    private ResultSet rs, rs2;
    private int i, index = 0, jml = 0, r = 0;
    private String[] kode, nama, ciripny, keterangan, kategori, cirium, kode2, panjang, pendek, satuan, nilairujukan, no;
    private boolean[] pilih;
    private double[] jumlah, p1, p2, kps;
    private WarnaTable2 warna = new WarnaTable2();
    private WarnaTable2 warna2 = new WarnaTable2();
    private WarnaTable2 warna3 = new WarnaTable2();
    private File file;
    private FileWriter fileWriter;
    private String iyem, la = "", ld = "", pa = "", pd = "", noracik = "";
    private double jumlahracik = 0, persenracik = 0, kapasitasracik = 0;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private FileReader myObj;
    private DlgCariMetodeRacik metoderacik = new DlgCariMetodeRacik(null, false);
    private DlgCariAturanPakai aturanpakai = new DlgCariAturanPakai(null, false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private StringBuilder htmlContent;

    /**
     * Creates new form DlgProgramStudi
     *
     * @param parent
     * @param modal
     */
    public MasterTemplatePaketMCU(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row = {"Kode Paket", "Nama Paket", "Nama Dokter", "Subjek", "Objek", "Asesmen", "Plan", "Instruksi", "Evaluasi"};
        tabMode = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbDokter.setModel(tabMode);

        tbDokter.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbDokter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 9; i++) {
            TableColumn column = tbDokter.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(120);
            } else if (i == 1) {
                column.setPreferredWidth(90);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else {
                column.setPreferredWidth(200);
            }
        }
        tbDokter.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRadiologi = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPermintaanRadiologi.setModel(tabModeRadiologi);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbPermintaanRadiologi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPermintaanRadiologi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbPermintaanRadiologi.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(130);
            } else if (i == 2) {
                column.setPreferredWidth(490);
            }
        }
        tbPermintaanRadiologi.setDefaultRenderer(Object.class, new WarnaTable());

        tabModePK = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPermintaanPK.setModel(tabModePK);

        tbPermintaanPK.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPermintaanPK.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbPermintaanPK.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(520);
            }
        }
        tbPermintaanPK.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailPK = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        tbDetailPK.setModel(tabModeDetailPK);
        //tampilPr();

        tbDetailPK.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailPK.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 6; i++) {
            TableColumn column = tbDetailPK.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(326);
            } else if (i == 2) {
                column.setPreferredWidth(50);
            } else if (i == 3) {
                column.setPreferredWidth(315);
            } else if (i == 4) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 5) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }

        tbDetailPK.setDefaultRenderer(Object.class, new WarnaTable());

        tabModePA = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPermintaanPA.setModel(tabModePA);

        tbPermintaanPA.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPermintaanPA.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbPermintaanPA.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(520);
            }
        }
        tbPermintaanPA.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeMB = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPermintaanMB.setModel(tabModeMB);

        tbPermintaanMB.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPermintaanMB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 3; i++) {
            TableColumn column = tbPermintaanMB.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(520);
            }
        }
        tbPermintaanMB.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailMB = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        tbDetailMB.setModel(tabModeDetailMB);
        //tampilPr();

        tbDetailMB.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailMB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 6; i++) {
            TableColumn column = tbDetailMB.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(326);
            } else if (i == 2) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 3) {
                column.setPreferredWidth(315);
            } else if (i == 4) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 5) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbDetailMB.setDefaultRenderer(Object.class, new WarnaTable());

        TabModeTindakan = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Nama Perawatan/Tindakan", "Kategori"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbTindakan.setModel(TabModeTindakan);
        tbTindakan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 4; i++) {
            TableColumn column = tbTindakan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(90);
            } else if (i == 2) {
                column.setPreferredWidth(380);
            } else if (i == 3) {
                column.setPreferredWidth(150);
            }
        }
        tbTindakan.setDefaultRenderer(Object.class, new WarnaTable());

        Kd.setDocument(new batasInput((byte) 20).getKata(Kd));
        CariRadiologi.setDocument(new batasInput((byte) 100).getKata(CariRadiologi));
        CariPK.setDocument(new batasInput((byte) 100).getKata(CariPK));
        CariDetailPK.setDocument(new batasInput((byte) 100).getKata(CariDetailPK));
        CariPA.setDocument(new batasInput((byte) 100).getKata(CariPA));
        CariMB.setDocument(new batasInput((byte) 100).getKata(CariMB));
        CariDetailMB.setDocument(new batasInput((byte) 100).getKata(CariDetailMB));
        CariTindakan.setDocument(new batasInput((byte) 100).getKata(CariTindakan));

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
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

        ChkAccor.setSelected(false);
        isDetail();

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
            ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
            + ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
            + ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
            + ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
            + ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"
            + ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"
            + ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"
            + ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"
            + ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Popup = new javax.swing.JPopupMenu();
        ppBersihkan = new javax.swing.JMenuItem();
        ppSemua = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        label12 = new widget.Label();
        Kd = new widget.TextBox();
        label14 = new widget.Label();
        Nama = new widget.TextBox();
        BtnCariRadiologi = new widget.Button();
        Scroll3 = new widget.ScrollPane();
        tbPermintaanRadiologi = new widget.Table();
        CariRadiologi = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        CariPK = new widget.TextBox();
        BtnCariLaboratoriumPK = new widget.Button();
        Scroll4 = new widget.ScrollPane();
        tbPermintaanPK = new widget.Table();
        Scroll5 = new widget.ScrollPane();
        tbDetailPK = new widget.Table();
        CariDetailPK = new widget.TextBox();
        BtnDetailLaboratPK = new widget.Button();
        jLabel17 = new widget.Label();
        CariPA = new widget.TextBox();
        BtnCariPA = new widget.Button();
        Scroll6 = new widget.ScrollPane();
        tbPermintaanPA = new widget.Table();
        jLabel18 = new widget.Label();
        CariMB = new widget.TextBox();
        BtnCariMB = new widget.Button();
        Scroll7 = new widget.ScrollPane();
        tbPermintaanMB = new widget.Table();
        CariDetailMB = new widget.TextBox();
        BtnCariDetailMB = new widget.Button();
        Scroll8 = new widget.ScrollPane();
        tbDetailMB = new widget.Table();
        jLabel21 = new widget.Label();
        CariTindakan = new widget.TextBox();
        BtnCariTindakan = new widget.Button();
        Scroll12 = new widget.ScrollPane();
        tbTindakan = new widget.Table();
        BtnAllRadiologi = new widget.Button();
        BtnAllPatologiKlinis = new widget.Button();
        BtnAllDetailLaboratPK = new widget.Button();
        BtnAllPA = new widget.Button();
        BtnAllMB = new widget.Button();
        BtnAllDetailMB = new widget.Button();
        BtnAllTindakan = new widget.Button();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbDokter = new widget.Table();
        panelGlass9 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        FormDetail = new widget.PanelBiasa();
        Scroll13 = new widget.ScrollPane();
        LoadHTML = new widget.editorpane();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        Popup.add(ppBersihkan);

        ppSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppSemua.setText("Pilih Semua");
        ppSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSemua.setName("ppSemua"); // NOI18N
        ppSemua.setPreferredSize(new java.awt.Dimension(200, 25));
        ppSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppSemuaActionPerformed(evt);
            }
        });
        Popup.add(ppSemua);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Master Template Pemeriksaan Dokter ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(730, 2966));
        FormInput.setLayout(null);

        label12.setText("Kode Paket :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(75, 23));
        FormInput.add(label12);
        label12.setBounds(0, 10, 85, 23);

        Kd.setName("Kd"); // NOI18N
        Kd.setPreferredSize(new java.awt.Dimension(207, 23));
        Kd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdKeyPressed(evt);
            }
        });
        FormInput.add(Kd);
        Kd.setBounds(89, 10, 150, 23);

        label14.setText("Nama :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(230, 10, 70, 23);

        Nama.setName("Nama"); // NOI18N
        Nama.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(Nama);
        Nama.setBounds(304, 10, 412, 23);

        BtnCariRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariRadiologi.setMnemonic('1');
        BtnCariRadiologi.setToolTipText("Alt+1");
        BtnCariRadiologi.setName("BtnCariRadiologi"); // NOI18N
        BtnCariRadiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariRadiologiActionPerformed(evt);
            }
        });
        FormInput.add(BtnCariRadiologi);
        BtnCariRadiologi.setBounds(658, 660, 28, 23);

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        tbPermintaanRadiologi.setName("tbPermintaanRadiologi"); // NOI18N
        Scroll3.setViewportView(tbPermintaanRadiologi);

        FormInput.add(Scroll3);
        Scroll3.setBounds(16, 687, 700, 126);

        CariRadiologi.setHighlighter(null);
        CariRadiologi.setName("CariRadiologi"); // NOI18N
        CariRadiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariRadiologiKeyPressed(evt);
            }
        });
        FormInput.add(CariRadiologi);
        CariRadiologi.setBounds(16, 660, 640, 23);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Permintaan Radiologi :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(16, 640, 120, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Permintaan Laborat Patologi Klinis :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(16, 820, 190, 23);

        CariPK.setHighlighter(null);
        CariPK.setName("CariPK"); // NOI18N
        CariPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariPKKeyPressed(evt);
            }
        });
        FormInput.add(CariPK);
        CariPK.setBounds(16, 840, 640, 23);

        BtnCariLaboratoriumPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariLaboratoriumPK.setMnemonic('1');
        BtnCariLaboratoriumPK.setToolTipText("Alt+1");
        BtnCariLaboratoriumPK.setName("BtnCariLaboratoriumPK"); // NOI18N
        BtnCariLaboratoriumPK.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariLaboratoriumPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariLaboratoriumPKActionPerformed(evt);
            }
        });
        FormInput.add(BtnCariLaboratoriumPK);
        BtnCariLaboratoriumPK.setBounds(658, 840, 28, 23);

        Scroll4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);

        tbPermintaanPK.setName("tbPermintaanPK"); // NOI18N
        tbPermintaanPK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPermintaanPKMouseClicked(evt);
            }
        });
        Scroll4.setViewportView(tbPermintaanPK);

        FormInput.add(Scroll4);
        Scroll4.setBounds(16, 867, 700, 120);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll5.setComponentPopupMenu(Popup);
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);

        tbDetailPK.setComponentPopupMenu(Popup);
        tbDetailPK.setName("tbDetailPK"); // NOI18N
        Scroll5.setViewportView(tbDetailPK);

        FormInput.add(Scroll5);
        Scroll5.setBounds(16, 1017, 700, 216);

        CariDetailPK.setHighlighter(null);
        CariDetailPK.setName("CariDetailPK"); // NOI18N
        CariDetailPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariDetailPKKeyPressed(evt);
            }
        });
        FormInput.add(CariDetailPK);
        CariDetailPK.setBounds(16, 990, 640, 23);

        BtnDetailLaboratPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnDetailLaboratPK.setMnemonic('1');
        BtnDetailLaboratPK.setToolTipText("Alt+1");
        BtnDetailLaboratPK.setName("BtnDetailLaboratPK"); // NOI18N
        BtnDetailLaboratPK.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDetailLaboratPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDetailLaboratPKActionPerformed(evt);
            }
        });
        FormInput.add(BtnDetailLaboratPK);
        BtnDetailLaboratPK.setBounds(658, 990, 28, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Permintaan Laborat Patologi Anatomi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(16, 1240, 250, 23);

        CariPA.setHighlighter(null);
        CariPA.setName("CariPA"); // NOI18N
        CariPA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariPAKeyPressed(evt);
            }
        });
        FormInput.add(CariPA);
        CariPA.setBounds(16, 1260, 640, 23);

        BtnCariPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariPA.setMnemonic('1');
        BtnCariPA.setToolTipText("Alt+1");
        BtnCariPA.setName("BtnCariPA"); // NOI18N
        BtnCariPA.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariPAActionPerformed(evt);
            }
        });
        FormInput.add(BtnCariPA);
        BtnCariPA.setBounds(658, 1260, 28, 23);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbPermintaanPA.setName("tbPermintaanPA"); // NOI18N
        Scroll6.setViewportView(tbPermintaanPA);

        FormInput.add(Scroll6);
        Scroll6.setBounds(16, 1287, 700, 126);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Permintaan Laborat Mikrobiologi & Bio Molekuler :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(16, 1420, 270, 23);

        CariMB.setHighlighter(null);
        CariMB.setName("CariMB"); // NOI18N
        CariMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariMBKeyPressed(evt);
            }
        });
        FormInput.add(CariMB);
        CariMB.setBounds(16, 1440, 640, 23);

        BtnCariMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariMB.setMnemonic('1');
        BtnCariMB.setToolTipText("Alt+1");
        BtnCariMB.setName("BtnCariMB"); // NOI18N
        BtnCariMB.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariMBActionPerformed(evt);
            }
        });
        FormInput.add(BtnCariMB);
        BtnCariMB.setBounds(658, 1440, 28, 23);

        Scroll7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);

        tbPermintaanMB.setName("tbPermintaanMB"); // NOI18N
        tbPermintaanMB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPermintaanMBMouseClicked(evt);
            }
        });
        Scroll7.setViewportView(tbPermintaanMB);

        FormInput.add(Scroll7);
        Scroll7.setBounds(16, 1467, 700, 120);

        CariDetailMB.setHighlighter(null);
        CariDetailMB.setName("CariDetailMB"); // NOI18N
        CariDetailMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariDetailMBKeyPressed(evt);
            }
        });
        FormInput.add(CariDetailMB);
        CariDetailMB.setBounds(16, 1590, 640, 23);

        BtnCariDetailMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariDetailMB.setMnemonic('1');
        BtnCariDetailMB.setToolTipText("Alt+1");
        BtnCariDetailMB.setName("BtnCariDetailMB"); // NOI18N
        BtnCariDetailMB.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariDetailMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariDetailMBActionPerformed(evt);
            }
        });
        FormInput.add(BtnCariDetailMB);
        BtnCariDetailMB.setBounds(658, 1590, 28, 23);

        Scroll8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);

        tbDetailMB.setName("tbDetailMB"); // NOI18N
        Scroll8.setViewportView(tbDetailMB);

        FormInput.add(Scroll8);
        Scroll8.setBounds(16, 1617, 700, 216);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Tindakan :");
        jLabel21.setName("jLabel21"); // NOI18N
        FormInput.add(jLabel21);
        jLabel21.setBounds(16, 1840, 120, 23);

        CariTindakan.setHighlighter(null);
        CariTindakan.setName("CariTindakan"); // NOI18N
        CariTindakan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariTindakanKeyPressed(evt);
            }
        });
        FormInput.add(CariTindakan);
        CariTindakan.setBounds(16, 1860, 640, 23);

        BtnCariTindakan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariTindakan.setMnemonic('1');
        BtnCariTindakan.setToolTipText("Alt+1");
        BtnCariTindakan.setName("BtnCariTindakan"); // NOI18N
        BtnCariTindakan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariTindakan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariTindakanActionPerformed(evt);
            }
        });
        FormInput.add(BtnCariTindakan);
        BtnCariTindakan.setBounds(658, 1860, 28, 23);

        Scroll12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll12.setName("Scroll12"); // NOI18N
        Scroll12.setOpaque(true);

        tbTindakan.setName("tbTindakan"); // NOI18N
        Scroll12.setViewportView(tbTindakan);

        FormInput.add(Scroll12);
        Scroll12.setBounds(16, 1887, 700, 116);

        BtnAllRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllRadiologi.setMnemonic('2');
        BtnAllRadiologi.setToolTipText("Alt+2");
        BtnAllRadiologi.setName("BtnAllRadiologi"); // NOI18N
        BtnAllRadiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllRadiologiActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllRadiologi);
        BtnAllRadiologi.setBounds(688, 660, 28, 23);

        BtnAllPatologiKlinis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllPatologiKlinis.setMnemonic('2');
        BtnAllPatologiKlinis.setToolTipText("Alt+2");
        BtnAllPatologiKlinis.setName("BtnAllPatologiKlinis"); // NOI18N
        BtnAllPatologiKlinis.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllPatologiKlinis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllPatologiKlinisActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllPatologiKlinis);
        BtnAllPatologiKlinis.setBounds(688, 840, 28, 23);

        BtnAllDetailLaboratPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllDetailLaboratPK.setMnemonic('2');
        BtnAllDetailLaboratPK.setToolTipText("Alt+2");
        BtnAllDetailLaboratPK.setName("BtnAllDetailLaboratPK"); // NOI18N
        BtnAllDetailLaboratPK.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllDetailLaboratPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllDetailLaboratPKActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllDetailLaboratPK);
        BtnAllDetailLaboratPK.setBounds(688, 990, 28, 23);

        BtnAllPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllPA.setMnemonic('2');
        BtnAllPA.setToolTipText("Alt+2");
        BtnAllPA.setName("BtnAllPA"); // NOI18N
        BtnAllPA.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllPAActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllPA);
        BtnAllPA.setBounds(688, 1260, 28, 23);

        BtnAllMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllMB.setMnemonic('2');
        BtnAllMB.setToolTipText("Alt+2");
        BtnAllMB.setName("BtnAllMB"); // NOI18N
        BtnAllMB.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllMBActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllMB);
        BtnAllMB.setBounds(688, 1440, 28, 23);

        BtnAllDetailMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllDetailMB.setMnemonic('2');
        BtnAllDetailMB.setToolTipText("Alt+2");
        BtnAllDetailMB.setName("BtnAllDetailMB"); // NOI18N
        BtnAllDetailMB.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllDetailMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllDetailMBActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllDetailMB);
        BtnAllDetailMB.setBounds(688, 1590, 28, 23);

        BtnAllTindakan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllTindakan.setMnemonic('2');
        BtnAllTindakan.setToolTipText("Alt+2");
        BtnAllTindakan.setName("BtnAllTindakan"); // NOI18N
        BtnAllTindakan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllTindakan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllTindakanActionPerformed(evt);
            }
        });
        FormInput.add(BtnAllTindakan);
        BtnAllTindakan.setBounds(688, 1860, 28, 23);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Template", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbDokter.setAutoCreateRowSorter(true);
        tbDokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbDokter.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbDokter.setName("tbDokter"); // NOI18N
        tbDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDokterMouseClicked(evt);
            }
        });
        tbDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDokterKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbDokter);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(530, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

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
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(430, 43));
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

        FormDetail.setBackground(new java.awt.Color(255, 255, 255));
        FormDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), " Detail Template Pemeriksaan : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        FormDetail.setName("FormDetail"); // NOI18N
        FormDetail.setPreferredSize(new java.awt.Dimension(115, 73));
        FormDetail.setLayout(new java.awt.BorderLayout());

        Scroll13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll13.setName("Scroll13"); // NOI18N
        Scroll13.setOpaque(true);
        Scroll13.setPreferredSize(new java.awt.Dimension(200, 200));

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N
        Scroll13.setViewportView(LoadHTML);

        FormDetail.add(Scroll13, java.awt.BorderLayout.CENTER);

        PanelAccor.add(FormDetail, java.awt.BorderLayout.CENTER);

        internalFrame3.add(PanelAccor, java.awt.BorderLayout.EAST);

        TabRawat.addTab("Data Template", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16i.png"))); // NOI18N
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

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass8.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(90, 23));
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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

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
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void tbDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDokterMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            try {
                panggilDetail();
            } catch (java.lang.NullPointerException e) {
            }
            if ((evt.getClickCount() == 2) && (tbDokter.getSelectedColumn() == 0)) {
                TabRawat.setSelectedIndex(0);
            }
        }
}//GEN-LAST:event_tbDokterMouseClicked

    private void tbDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDokterKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    getData();
                    if (ChkAccor.isSelected() == false) {
                        if (tbDokter.getSelectedRow() != -1) {
                            ChkAccor.setSelected(true);
                            isDetail();
                            panggilDetail();
                            ChkAccor.setSelected(false);
                            isDetail();
                        }
                    }
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbDokterKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbDokter.getSelectedRow() > -1) {
            hapus();
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
        if (Kd.getText().trim().equals("")) {
            Valid.textKosong(Kd, "No.Template");
        } else if (Nama.getText().trim().equals("") || Nama.getText().trim().equals("")) {
            Valid.textKosong(Nama, "Dokter");
        } else {
            if (tbDokter.getSelectedRow() > -1) {
                ganti();
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, BtnKeluar);
        }
}//GEN-LAST:event_BtnAllKeyPressed

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

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (Kd.getText().trim().equals("")) {
            Valid.textKosong(Kd, "No.Template");
        } else if (Nama.getText().trim().equals("")) {
            Valid.textKosong(Nama, "Nama Paket");
        } else {
            if (Sequel.menyimpantfSmc("paket_mcu", null, Kd.getText(), "-", Nama.getText(), "")) {
                index = 1;
                for (i = 0; i < tbPermintaanRadiologi.getRowCount(); i++) {
                    if (tbPermintaanRadiologi.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_permintaan_radiologi", null, Kd.getText(), tbPermintaanRadiologi.getValueAt(i, 1).toString());
                    }
                }
                for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                    if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_permintaan_lab", null, Kd.getText(), tbPermintaanPK.getValueAt(i, 1).toString());
                    }
                }
                for (i = 0; i < tbDetailPK.getRowCount(); i++) {
                    if ((!tbDetailPK.getValueAt(i, 4).toString().equals("")) && tbDetailPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_detail_permintaan_lab", null, Kd.getText(), tbDetailPK.getValueAt(i, 5).toString(), tbDetailPK.getValueAt(i, 4).toString());
                    }
                }
                for (i = 0; i < tbPermintaanPA.getRowCount(); i++) {
                    if (tbPermintaanPA.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_permintaan_lab", null, Kd.getText(), tbPermintaanPA.getValueAt(i, 1).toString());
                    }
                }
                for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                    if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_permintaan_lab", null, Kd.getText(), tbPermintaanMB.getValueAt(i, 1).toString());
                    }
                }
                for (i = 0; i < tbDetailMB.getRowCount(); i++) {
                    if ((!tbDetailMB.getValueAt(i, 4).toString().equals("")) && tbDetailMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_detail_permintaan_lab", null, Kd.getText(), tbDetailMB.getValueAt(i, 5).toString(), tbDetailMB.getValueAt(i, 4).toString());
                    }
                }
                for (i = 0; i < tbTindakan.getRowCount(); i++) {
                    if (tbTindakan.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpanSmc("paket_mcu_tindakan", null, Kd.getText(), tbTindakan.getValueAt(i, 1).toString());
                    }
                }
                tabMode.addRow(new String[] {
                    Kd.getText(), "-", Nama.getText(), ""
                });
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
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
    /*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        if (tbDokter.getSelectedRow() != -1) {
            isDetail();
            panggilDetail();
        } else {
            ChkAccor.setSelected(false);
            JOptionPane.showMessageDialog(null, "Silahkan pilih No.Pernyataan..!!!");
        }
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        for (i = 0; i < tbDetailPK.getRowCount(); i++) {
            tbDetailPK.setValueAt(false, i, 0);
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void ppSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppSemuaActionPerformed
        for (i = 0; i < tbDetailPK.getRowCount(); i++) {
            tbDetailPK.setValueAt(true, i, 0);
        }
    }//GEN-LAST:event_ppSemuaActionPerformed

    private void BtnAllTindakanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllTindakanActionPerformed
        CariTindakan.setText("");
        tampilTindakan();
        tampilTindakan2();
    }//GEN-LAST:event_BtnAllTindakanActionPerformed

    private void BtnAllDetailMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllDetailMBActionPerformed
        CariDetailMB.setText("");
        tampilDetailMB();
    }//GEN-LAST:event_BtnAllDetailMBActionPerformed

    private void BtnAllMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllMBActionPerformed
        CariMB.setText("");
        tampilMB();
        tampilMB2();
    }//GEN-LAST:event_BtnAllMBActionPerformed

    private void BtnAllPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllPAActionPerformed
        CariPA.setText("");
        tampilPA();
        tampilPA2();
    }//GEN-LAST:event_BtnAllPAActionPerformed

    private void BtnAllDetailLaboratPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllDetailLaboratPKActionPerformed
        CariDetailPK.setText("");
        tampilDetailPK();
    }//GEN-LAST:event_BtnAllDetailLaboratPKActionPerformed

    private void BtnAllPatologiKlinisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllPatologiKlinisActionPerformed
        CariPA.setText("");
        tampilPK();
        tampilPK2();
    }//GEN-LAST:event_BtnAllPatologiKlinisActionPerformed

    private void BtnAllRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllRadiologiActionPerformed
        CariRadiologi.setText("");
        tampilRadiologi();
        tampilRadiologi2();
    }//GEN-LAST:event_BtnAllRadiologiActionPerformed

    private void BtnCariTindakanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariTindakanActionPerformed
        tampilTindakan2();
    }//GEN-LAST:event_BtnCariTindakanActionPerformed

    private void CariTindakanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariTindakanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilTindakan2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnSimpan.requestFocus();
        }
    }//GEN-LAST:event_CariTindakanKeyPressed

    private void BtnCariDetailMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariDetailMBActionPerformed
        tampilDetailMB();
    }//GEN-LAST:event_BtnCariDetailMBActionPerformed

    private void CariDetailMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariDetailMBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilDetailMB();
        }
    }//GEN-LAST:event_CariDetailMBKeyPressed

    private void tbPermintaanMBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPermintaanMBMouseClicked
        if (tabModeMB.getRowCount() != 0) {
            try {
                Valid.tabelKosong(tabModeDetailMB);
                tampilDetailMB();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbPermintaanMBMouseClicked

    private void BtnCariMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariMBActionPerformed
        tampilMB2();
    }//GEN-LAST:event_BtnCariMBActionPerformed

    private void CariMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariMBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilMB2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariDetailMB.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariPA.requestFocus();
        }
    }//GEN-LAST:event_CariMBKeyPressed

    private void BtnCariPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariPAActionPerformed
        tampilPA2();
    }//GEN-LAST:event_BtnCariPAActionPerformed

    private void CariPAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariPAKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilPA2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariMB.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariDetailPK.requestFocus();
        }
    }//GEN-LAST:event_CariPAKeyPressed

    private void BtnDetailLaboratPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDetailLaboratPKActionPerformed
        tampilDetailPK();
    }//GEN-LAST:event_BtnDetailLaboratPKActionPerformed

    private void CariDetailPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariDetailPKKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilDetailPK();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariPA.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariPK.requestFocus();
        }
    }//GEN-LAST:event_CariDetailPKKeyPressed

    private void tbPermintaanPKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPermintaanPKMouseClicked
        if (tabModePK.getRowCount() != 0) {
            try {
                Valid.tabelKosong(tabModeDetailPK);
                tampilDetailPK();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbPermintaanPKMouseClicked

    private void BtnCariLaboratoriumPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariLaboratoriumPKActionPerformed
        tampilPK2();
    }//GEN-LAST:event_BtnCariLaboratoriumPKActionPerformed

    private void CariPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariPKKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilPK2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariDetailPK.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariRadiologi.requestFocus();
        }
    }//GEN-LAST:event_CariPKKeyPressed

    private void CariRadiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariRadiologiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilRadiologi2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariPK.requestFocus();
        }
    }//GEN-LAST:event_CariRadiologiKeyPressed

    private void BtnCariRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariRadiologiActionPerformed
        tampilRadiologi2();
    }//GEN-LAST:event_BtnCariRadiologiActionPerformed

    private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdKeyPressed
        //Valid.pindah(evt,TCari,Nm,TCari);
    }//GEN-LAST:event_KdKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            MasterTemplatePaketMCU dialog = new MasterTemplatePaketMCU(new javax.swing.JFrame(), true);
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
    private widget.Button BtnAllDetailLaboratPK;
    private widget.Button BtnAllDetailMB;
    private widget.Button BtnAllMB;
    private widget.Button BtnAllPA;
    private widget.Button BtnAllPatologiKlinis;
    private widget.Button BtnAllRadiologi;
    private widget.Button BtnAllTindakan;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCariDetailMB;
    private widget.Button BtnCariLaboratoriumPK;
    private widget.Button BtnCariMB;
    private widget.Button BtnCariPA;
    private widget.Button BtnCariRadiologi;
    private widget.Button BtnCariTindakan;
    private widget.Button BtnDetailLaboratPK;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.TextBox CariDetailMB;
    private widget.TextBox CariDetailPK;
    private widget.TextBox CariMB;
    private widget.TextBox CariPA;
    private widget.TextBox CariPK;
    private widget.TextBox CariRadiologi;
    private widget.TextBox CariTindakan;
    private widget.CekBox ChkAccor;
    private widget.PanelBiasa FormDetail;
    private widget.PanelBiasa FormInput;
    private widget.TextBox Kd;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.TextBox Nama;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll12;
    private widget.ScrollPane Scroll13;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.TextBox TCari;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel21;
    private widget.Label label10;
    private widget.Label label12;
    private widget.Label label14;
    private widget.Label label9;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppSemua;
    private widget.ScrollPane scrollInput;
    private widget.Table tbDetailMB;
    private widget.Table tbDetailPK;
    private widget.Table tbDokter;
    private widget.Table tbPermintaanMB;
    private widget.Table tbPermintaanPA;
    private widget.Table tbPermintaanPK;
    private widget.Table tbPermintaanRadiologi;
    private widget.Table tbTindakan;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (akses.getkode().equals("Admin Utama")) {
                ps = koneksi.prepareStatement(
                    "select paket_mcu.no_template,paket_mcu.kd_dokter,dokter.nm_dokter,"
                    + "paket_mcu.keluhan,paket_mcu.pemeriksaan,paket_mcu.penilaian,"
                    + "paket_mcu.rencana,paket_mcu.instruksi,paket_mcu.evaluasi "
                    + "from paket_mcu inner join dokter on dokter.kd_dokter=paket_mcu.kd_dokter "
                    + (TCari.getText().equals("") ? "" : "where paket_mcu.no_template like ? or paket_mcu.nm_dokter like ? or "
                    + "paket_mcu.keluhan like ? or paket_mcu.pemeriksaan like ? or "
                    + "paket_mcu.penilaian like ? or paket_mcu.rencana like ? or "
                    + "paket_mcu.instruksi like ? or paket_mcu.evaluasi like ? ")
                    + "order by paket_mcu.no_template");
            } else {
                ps = koneksi.prepareStatement(
                    "select paket_mcu.no_template,paket_mcu.kd_dokter,dokter.nm_dokter,"
                    + "paket_mcu.keluhan,paket_mcu.pemeriksaan,paket_mcu.penilaian,"
                    + "paket_mcu.rencana,paket_mcu.instruksi,paket_mcu.evaluasi "
                    + "from paket_mcu inner join dokter on dokter.kd_dokter=paket_mcu.kd_dokter "
                    + "where paket_mcu.kd_dokter=? " + (TCari.getText().equals("") ? "" : "and (paket_mcu.no_template like ? or "
                    + "paket_mcu.keluhan like ? or paket_mcu.pemeriksaan like ? or "
                    + "paket_mcu.penilaian like ? or paket_mcu.rencana like ? or "
                    + "paket_mcu.instruksi like ? or paket_mcu.evaluasi like ?) ")
                    + "order by paket_mcu.no_template");
            }

            try {
                if (akses.getkode().equals("Admin Utama")) {
                    if (!TCari.getText().equals("")) {
                        ps.setString(1, "%" + TCari.getText().trim() + "%");
                        ps.setString(2, "%" + TCari.getText().trim() + "%");
                        ps.setString(3, "%" + TCari.getText().trim() + "%");
                        ps.setString(4, "%" + TCari.getText().trim() + "%");
                        ps.setString(5, "%" + TCari.getText().trim() + "%");
                        ps.setString(6, "%" + TCari.getText().trim() + "%");
                        ps.setString(7, "%" + TCari.getText().trim() + "%");
                        ps.setString(8, "%" + TCari.getText().trim() + "%");
                    }
                } else {
                    ps.setString(1, akses.getkode());
                    if (!TCari.getText().equals("")) {
                        ps.setString(2, "%" + TCari.getText().trim() + "%");
                        ps.setString(3, "%" + TCari.getText().trim() + "%");
                        ps.setString(4, "%" + TCari.getText().trim() + "%");
                        ps.setString(5, "%" + TCari.getText().trim() + "%");
                        ps.setString(6, "%" + TCari.getText().trim() + "%");
                        ps.setString(7, "%" + TCari.getText().trim() + "%");
                        ps.setString(8, "%" + TCari.getText().trim() + "%");
                    }
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[] {
                        rs.getString("no_template"), rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("keluhan"), rs.getString("pemeriksaan"), rs.getString("penilaian"), rs.getString("rencana"), rs.getString("instruksi"), rs.getString("evaluasi")
                    });
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        Kd.setText("");
        CariRadiologi.setText("");
        CariPK.setText("");
        CariDetailPK.setText("");
        CariPA.setText("");
        CariMB.setText("");
        CariDetailMB.setText("");
        CariTindakan.setText("");
        Valid.tabelKosong(tabModeRadiologi);
        Valid.tabelKosong(tabModePK);
        Valid.tabelKosong(tabModeDetailPK);
        Valid.tabelKosong(tabModePA);
        Valid.tabelKosong(tabModeMB);
        Valid.tabelKosong(tabModeDetailMB);
        Valid.tabelKosong(TabModeTindakan);
        Valid.autoNomer("paket_mcu", "TPD", 16, Kd);
        TabRawat.setSelectedIndex(0);
    }

    private void getData() {
        if (tbDokter.getSelectedRow() != -1) {
            Kd.setText(tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
        }
    }

    public JTable getTable() {
        return tbDokter;
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getmaster_paket_mcu());
        BtnHapus.setEnabled(akses.getmaster_paket_mcu());
        BtnEdit.setEnabled(akses.getmaster_paket_mcu());
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
    }

    private void tampilRadiologi() {
        try {
            file = new File("./cache/permintaanradiologi.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            iyem = "";

            ps = koneksi.prepareStatement(
                "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan from jns_perawatan_radiologi where jns_perawatan_radiologi.status='1' and (jns_perawatan_radiologi.kelas='-' or jns_perawatan_radiologi.kelas='Rawat Jalan') order by jns_perawatan_radiologi.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyem = iyem + "{\"KodePeriksa\":\"" + rs.getString(1) + "\",\"NamaPemeriksaan\":\"" + rs.getString(2).replaceAll("\"", "") + "\"},";
                }
            } catch (Exception e) {
                System.out.println("Notifikasi 1 : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            fileWriter.write("{\"permintaanradiologi\":[" + iyem.substring(0, iyem.length() - 1) + "]}");
            fileWriter.flush();
            fileWriter.close();
            iyem = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        }
    }

    private void tampilRadiologi2() {
        try {
            jml = 0;
            for (i = 0; i < tbPermintaanRadiologi.getRowCount(); i++) {
                if (tbPermintaanRadiologi.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            nama = null;
            nama = new String[jml];

            index = 0;
            for (i = 0; i < tbPermintaanRadiologi.getRowCount(); i++) {
                if (tbPermintaanRadiologi.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbPermintaanRadiologi.getValueAt(i, 1).toString();
                    nama[index] = tbPermintaanRadiologi.getValueAt(i, 2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeRadiologi);
            for (i = 0; i < jml; i++) {
                tabModeRadiologi.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            myObj = new FileReader("./cache/permintaanradiologi.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanradiologi");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariRadiologi.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariRadiologi.getText().toLowerCase()))) {
                        tabModeRadiologi.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilPK() {
        try {
            file = new File("./cache/permintaanpk.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            iyem = "";

            ps = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where jns_perawatan_lab.status='1' and jns_perawatan_lab.kategori='PK' and (jns_perawatan_lab.kelas='-' or jns_perawatan_lab.kelas='Rawat Jalan') order by jns_perawatan_lab.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyem = iyem + "{\"KodePeriksa\":\"" + rs.getString(1) + "\",\"NamaPemeriksaan\":\"" + rs.getString(2).replaceAll("\"", "") + "\"},";
                }
            } catch (Exception e) {
                System.out.println("Notifikasi 1 : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            fileWriter.write("{\"permintaanpk\":[" + iyem.substring(0, iyem.length() - 1) + "]}");
            fileWriter.flush();
            fileWriter.close();
            iyem = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        }
    }

    private void tampilPK2() {
        try {
            jml = 0;
            for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            nama = null;
            nama = new String[jml];

            index = 0;
            for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbPermintaanPK.getValueAt(i, 1).toString();
                    nama[index] = tbPermintaanPK.getValueAt(i, 2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModePK);
            for (i = 0; i < jml; i++) {
                tabModePK.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            myObj = new FileReader("./cache/permintaanpk.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanpk");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariPK.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariPK.getText().toLowerCase()))) {
                        tabModePK.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilDetailPK() {
        try {
            jml = 0;
            for (i = 0; i < tbDetailPK.getRowCount(); i++) {
                if (tbDetailPK.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            nama = null;
            nama = new String[jml];
            satuan = null;
            satuan = new String[jml];
            nilairujukan = null;
            nilairujukan = new String[jml];
            kode = null;
            kode = new String[jml];
            kode2 = null;
            kode2 = new String[jml];

            index = 0;
            for (i = 0; i < tbDetailPK.getRowCount(); i++) {
                if (tbDetailPK.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    nama[index] = tbDetailPK.getValueAt(i, 1).toString();
                    satuan[index] = tbDetailPK.getValueAt(i, 2).toString();
                    nilairujukan[index] = tbDetailPK.getValueAt(i, 3).toString();
                    kode[index] = tbDetailPK.getValueAt(i, 4).toString();
                    kode2[index] = tbDetailPK.getValueAt(i, 5).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeDetailPK);

            for (i = 0; i < jml; i++) {
                tabModeDetailPK.addRow(new Object[] {
                    pilih[i], nama[i], satuan[i], nilairujukan[i], kode[i], kode2[i]
                });
            }

            for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                    tabModeDetailPK.addRow(new Object[] {false, tbPermintaanPK.getValueAt(i, 2).toString(), "", "", "", ""});
                    ps = koneksi.prepareStatement("select template_laboratorium.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,template_laboratorium.nilai_rujukan_pa from template_laboratorium where template_laboratorium.kd_jenis_prw=? and template_laboratorium.Pemeriksaan like ? order by template_laboratorium.urut");
                    try {
                        ps.setString(1, tbPermintaanPK.getValueAt(i, 1).toString());
                        ps.setString(2, "%" + CariDetailPK.getText().trim() + "%");
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            la = "";
                            ld = "";
                            pa = "";
                            pd = "";
                            if (!rs.getString("nilai_rujukan_ld").equals("")) {
                                ld = "LD : " + rs.getString("nilai_rujukan_ld");
                            }
                            if (!rs.getString("nilai_rujukan_la").equals("")) {
                                la = ", LA : " + rs.getString("nilai_rujukan_la");
                            }
                            if (!rs.getString("nilai_rujukan_pa").equals("")) {
                                pd = ", PD : " + rs.getString("nilai_rujukan_pd");
                            }
                            if (!rs.getString("nilai_rujukan_pd").equals("")) {
                                pa = " PA : " + rs.getString("nilai_rujukan_pa");
                            }
                            tabModeDetailPK.addRow(new Object[] {
                                false, "   " + rs.getString("Pemeriksaan"), rs.getString("satuan"), ld + la + pd + pa, rs.getString("id_template"), tbPermintaanPK.getValueAt(i, 1).toString()
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Notifikasi : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error Detail : " + e);
        }
    }

    private void tampilPA() {
        try {
            file = new File("./cache/permintaanpa.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            iyem = "";

            ps = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where jns_perawatan_lab.status='1' and jns_perawatan_lab.kategori='PA' and (jns_perawatan_lab.kelas='-' or jns_perawatan_lab.kelas='Rawat Jalan') order by jns_perawatan_lab.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyem = iyem + "{\"KodePeriksa\":\"" + rs.getString(1) + "\",\"NamaPemeriksaan\":\"" + rs.getString(2).replaceAll("\"", "") + "\"},";
                }
            } catch (Exception e) {
                System.out.println("Notifikasi 1 : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            fileWriter.write("{\"permintaanpa\":[" + iyem.substring(0, iyem.length() - 1) + "]}");
            fileWriter.flush();
            fileWriter.close();
            iyem = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        }
    }

    private void tampilPA2() {
        try {
            jml = 0;
            for (i = 0; i < tbPermintaanPA.getRowCount(); i++) {
                if (tbPermintaanPA.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            nama = null;
            nama = new String[jml];

            index = 0;
            for (i = 0; i < tbPermintaanPA.getRowCount(); i++) {
                if (tbPermintaanPA.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbPermintaanPA.getValueAt(i, 1).toString();
                    nama[index] = tbPermintaanPA.getValueAt(i, 2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModePA);
            for (i = 0; i < jml; i++) {
                tabModePA.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            myObj = new FileReader("./cache/permintaanpa.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanpa");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariPA.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariPA.getText().toLowerCase()))) {
                        tabModePA.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilMB() {
        try {
            file = new File("./cache/permintaanmb.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            iyem = "";

            ps = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where jns_perawatan_lab.status='1' and jns_perawatan_lab.kategori='MB' and (jns_perawatan_lab.kelas='-' or jns_perawatan_lab.kelas='Rawat Jalan') order by jns_perawatan_lab.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyem = iyem + "{\"KodePeriksa\":\"" + rs.getString(1) + "\",\"NamaPemeriksaan\":\"" + rs.getString(2).replaceAll("\"", "") + "\"},";
                }
            } catch (Exception e) {
                System.out.println("Notifikasi 1 : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            fileWriter.write("{\"permintaanmb\":[" + iyem.substring(0, iyem.length() - 1) + "]}");
            fileWriter.flush();
            fileWriter.close();
            iyem = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        }
    }

    private void tampilMB2() {
        try {
            jml = 0;
            for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            nama = null;
            nama = new String[jml];

            index = 0;
            for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbPermintaanMB.getValueAt(i, 1).toString();
                    nama[index] = tbPermintaanMB.getValueAt(i, 2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeMB);
            for (i = 0; i < jml; i++) {
                tabModeMB.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            myObj = new FileReader("./cache/permintaanmb.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanmb");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariMB.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariMB.getText().toLowerCase()))) {
                        tabModeMB.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilDetailMB() {
        try {
            jml = 0;
            for (i = 0; i < tbDetailMB.getRowCount(); i++) {
                if (tbDetailMB.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            nama = null;
            nama = new String[jml];
            satuan = null;
            satuan = new String[jml];
            nilairujukan = null;
            nilairujukan = new String[jml];
            kode = null;
            kode = new String[jml];
            kode2 = null;
            kode2 = new String[jml];

            index = 0;
            for (i = 0; i < tbDetailMB.getRowCount(); i++) {
                if (tbDetailMB.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    nama[index] = tbDetailMB.getValueAt(i, 1).toString();
                    satuan[index] = tbDetailMB.getValueAt(i, 2).toString();
                    nilairujukan[index] = tbDetailMB.getValueAt(i, 3).toString();
                    kode[index] = tbDetailMB.getValueAt(i, 4).toString();
                    kode2[index] = tbDetailMB.getValueAt(i, 5).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeDetailMB);

            for (i = 0; i < jml; i++) {
                tabModeDetailMB.addRow(new Object[] {
                    pilih[i], nama[i], satuan[i], nilairujukan[i], kode[i], kode2[i]
                });
            }

            for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                    tabModeDetailMB.addRow(new Object[] {false, tbPermintaanMB.getValueAt(i, 2).toString(), "", "", "", ""});
                    ps = koneksi.prepareStatement("select template_laboratorium.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,template_laboratorium.nilai_rujukan_pa from template_laboratorium where template_laboratorium.kd_jenis_prw=? and template_laboratorium.Pemeriksaan like ? order by template_laboratorium.urut");
                    try {
                        ps.setString(1, tbPermintaanMB.getValueAt(i, 1).toString());
                        ps.setString(2, "%" + CariDetailMB.getText().trim() + "%");
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            la = "";
                            ld = "";
                            pa = "";
                            pd = "";
                            if (!rs.getString("nilai_rujukan_ld").equals("")) {
                                ld = "LD : " + rs.getString("nilai_rujukan_ld");
                            }
                            if (!rs.getString("nilai_rujukan_la").equals("")) {
                                la = ", LA : " + rs.getString("nilai_rujukan_la");
                            }
                            if (!rs.getString("nilai_rujukan_pa").equals("")) {
                                pd = ", PD : " + rs.getString("nilai_rujukan_pd");
                            }
                            if (!rs.getString("nilai_rujukan_pd").equals("")) {
                                pa = " PA : " + rs.getString("nilai_rujukan_pa");
                            }
                            tabModeDetailMB.addRow(new Object[] {
                                false, "   " + rs.getString("Pemeriksaan"), rs.getString("satuan"), ld + la + pd + pa, rs.getString("id_template"), tbPermintaanMB.getValueAt(i, 1).toString()
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Notifikasi : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Error Detail : " + e);
        }
    }

    private void tampilTindakan() {
        try {
            file = new File("./cache/permintaantindakan.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            iyem = "";

            ps = koneksi.prepareStatement(
                "select jns_perawatan.kd_jenis_prw,jns_perawatan.nm_perawatan,kategori_perawatan.nm_kategori from jns_perawatan inner join kategori_perawatan "
                + "on jns_perawatan.kd_kategori=kategori_perawatan.kd_kategori where jns_perawatan.status='1' and jns_perawatan.total_byrdr>0 order by jns_perawatan.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyem = iyem + "{\"KodePeriksa\":\"" + rs.getString(1) + "\",\"NamaPemeriksaan\":\"" + rs.getString(2).replaceAll("\"", "") + "\",\"Kategori\":\"" + rs.getString(3).replaceAll("\"", "") + "\"},";
                }
            } catch (Exception e) {
                System.out.println("Notifikasi 1 : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            fileWriter.write("{\"permintaantindakan\":[" + iyem.substring(0, iyem.length() - 1) + "]}");
            fileWriter.flush();
            fileWriter.close();
            iyem = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        }
    }

    private void tampilTindakan2() {
        try {
            jml = 0;
            for (i = 0; i < tbTindakan.getRowCount(); i++) {
                if (tbTindakan.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            nama = null;
            nama = new String[jml];
            kategori = null;
            kategori = new String[jml];

            index = 0;
            for (i = 0; i < tbTindakan.getRowCount(); i++) {
                if (tbTindakan.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbTindakan.getValueAt(i, 1).toString();
                    nama[index] = tbTindakan.getValueAt(i, 2).toString();
                    kategori[index] = tbTindakan.getValueAt(i, 3).toString();
                    index++;
                }
            }

            Valid.tabelKosong(TabModeTindakan);
            for (i = 0; i < jml; i++) {
                TabModeTindakan.addRow(new Object[] {pilih[i], kode[i], nama[i], kategori[i]});
            }

            myObj = new FileReader("./cache/permintaantindakan.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaantindakan");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariTindakan.getText().toLowerCase())
                        || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariTindakan.getText().toLowerCase())
                        || list.path("Kategori").asText().toLowerCase().contains(CariTindakan.getText().toLowerCase()))) {
                        TabModeTindakan.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText(), list.path("Kategori").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void isDetail() {
        if (ChkAccor.isSelected() == true) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(internalFrame3.getWidth() - 200, HEIGHT));
            FormDetail.setVisible(true);
            ChkAccor.setVisible(true);
        } else if (ChkAccor.isSelected() == false) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            FormDetail.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }

    private void panggilDetail() {
        if (FormDetail.isVisible() == true) {
            if (tbDokter.getSelectedRow() != -1) {
                try {
                    htmlContent = new StringBuilder();
                    htmlContent.append(
                        "<tr class='isi'>"
                        + "<td valign='top' align='left' width='100%'>"
                        + "Subjek : " + tabMode.getValueAt(tbDokter.getSelectedRow(), 3).toString()
                        + "</td>"
                        + "</tr>"
                        + "<tr class='isi'>"
                        + "<td valign='top' align='left' width='100%'>"
                        + "Objek : " + tabMode.getValueAt(tbDokter.getSelectedRow(), 4).toString()
                        + "</td>"
                        + "</tr>"
                        + "<tr class='isi'>"
                        + "<td valign='top' align='left' width='100%'>"
                        + "Asesmen : " + tabMode.getValueAt(tbDokter.getSelectedRow(), 5).toString()
                        + "</td>"
                        + "</tr>"
                        + "<tr class='isi'>"
                        + "<td valign='top' align='left' width='100%'>"
                        + "Plan : " + tabMode.getValueAt(tbDokter.getSelectedRow(), 6).toString()
                        + "</td>"
                        + "</tr>"
                        + "<tr class='isi'>"
                        + "<td valign='top' align='left' width='100%'>"
                        + "Instruksi : " + tabMode.getValueAt(tbDokter.getSelectedRow(), 7).toString()
                        + "</td>"
                        + "</tr>"
                        + "<tr class='isi'>"
                        + "<td valign='top' align='left' width='100%'>"
                        + "Evaluasi : " + tabMode.getValueAt(tbDokter.getSelectedRow(), 8).toString()
                        + "</td>"
                        + "</tr>"
                    );

                    ps = koneksi.prepareStatement(
                        "select paket_mcu_permintaan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan from paket_mcu_permintaan_radiologi "
                        + "inner join jns_perawatan_radiologi on paket_mcu_permintaan_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw "
                        + "where paket_mcu_permintaan_radiologi.no_template=?");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>"
                                + "<td valign='top' align='left' width='100%'>"
                                + "Permintaan Radiologi : "
                                + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                + "<tr class='isi'>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode Periksa</td>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='80%'>Nama Pemeriksaan</td>"
                                + "</tr>"
                            );
                            Valid.tabelKosong(tabModeRadiologi);
                            rs.beforeFirst();
                            while (rs.next()) {
                                htmlContent.append(
                                    "<tr class='isi'>"
                                    + "<td align='center'>" + rs.getString("kd_jenis_prw") + "</td>"
                                    + "<td>" + rs.getString("nm_perawatan") + "</td>"
                                    + "</tr>"
                                );
                                tabModeRadiologi.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                            }
                            htmlContent.append(
                                "</table>"
                                + "</td>"
                                + "</tr>"
                            );

                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }

                    ps = koneksi.prepareStatement(
                        "select paket_mcu_permintaan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from paket_mcu_permintaan_lab "
                        + "inner join jns_perawatan_lab on paket_mcu_permintaan_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "
                        + "where paket_mcu_permintaan_lab.no_template=? and jns_perawatan_lab.kategori='PK'");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>"
                                + "<td valign='top' align='left' width='100%'>"
                                + "Permintaan Laborat Patologi Klinis : "
                                + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                + "<tr class='isi'>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='15%'>Kode Periksa</td>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='85%'>Nama Pemeriksaan</td>"
                                + "</tr>"
                            );
                            Valid.tabelKosong(tabModePK);
                            rs.beforeFirst();
                            while (rs.next()) {
                                htmlContent.append(
                                    "<tr class='isi'>"
                                    + "<td align='center'>" + rs.getString("kd_jenis_prw") + "</td>"
                                    + "<td>" + rs.getString("nm_perawatan") + "</td>"
                                    + "</tr>"
                                );
                                tabModePK.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                                try {
                                    ps2 = koneksi.prepareStatement(
                                        "select paket_mcu_detail_permintaan_lab.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,template_laboratorium.nilai_rujukan_pa "
                                        + "from paket_mcu_detail_permintaan_lab inner join template_laboratorium on paket_mcu_detail_permintaan_lab.id_template=template_laboratorium.id_template where paket_mcu_detail_permintaan_lab.no_template=? and "
                                        + "paket_mcu_detail_permintaan_lab.kd_jenis_prw=? order by template_laboratorium.urut");
                                    ps2.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                                    ps2.setString(2, rs.getString("kd_jenis_prw"));
                                    rs2 = ps2.executeQuery();
                                    if (rs2.next()) {
                                        Valid.tabelKosong(tabModeDetailPK);
                                        htmlContent.append(
                                            "<tr class='isi'>"
                                            + "<td align='center' width='15%'></td>"
                                            + "<td width='85%'>"
                                            + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                            + "<tr class='isi'>"
                                            + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Pemeriksaan</td>"
                                            + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Satuan</td>"
                                            + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Nilai Rujukan</td>"
                                            + "</tr>"
                                        );
                                        rs2.beforeFirst();
                                        while (rs2.next()) {
                                            la = "";
                                            ld = "";
                                            pa = "";
                                            pd = "";
                                            if (!rs2.getString("nilai_rujukan_ld").equals("")) {
                                                ld = "LD : " + rs2.getString("nilai_rujukan_ld");
                                            }
                                            if (!rs2.getString("nilai_rujukan_la").equals("")) {
                                                la = ", LA : " + rs2.getString("nilai_rujukan_la");
                                            }
                                            if (!rs2.getString("nilai_rujukan_pa").equals("")) {
                                                pd = ", PD : " + rs2.getString("nilai_rujukan_pd");
                                            }
                                            if (!rs2.getString("nilai_rujukan_pd").equals("")) {
                                                pa = " PA : " + rs2.getString("nilai_rujukan_pa");
                                            }
                                            htmlContent.append(
                                                "<tr class='isi'>"
                                                + "<td>" + rs2.getString("Pemeriksaan") + "</td>"
                                                + "<td align='center'>" + rs2.getString("satuan") + "</td>"
                                                + "<td>" + ld + la + pd + pa + "</td>"
                                                + "</tr>"
                                            );
                                            tabModeDetailPK.addRow(new Object[] {
                                                true, "   " + rs2.getString("Pemeriksaan"), rs2.getString("satuan"), ld + la + pd + pa, rs2.getString("id_template"), rs.getString("kd_jenis_prw")
                                            });
                                        }
                                        htmlContent.append(
                                            "</table>"
                                            + "</td>"
                                            + "</tr>"
                                        );
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notif : " + e);
                                } finally {
                                    if (rs2 != null) {
                                        rs2.close();
                                    }
                                    if (ps2 != null) {
                                        ps2.close();
                                    }
                                }
                            }
                            htmlContent.append(
                                "</table>"
                                + "</td>"
                                + "</tr>"
                            );
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }

                    ps = koneksi.prepareStatement(
                        "select paket_mcu_permintaan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from paket_mcu_permintaan_lab "
                        + "inner join jns_perawatan_lab on paket_mcu_permintaan_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "
                        + "where paket_mcu_permintaan_lab.no_template=? and jns_perawatan_lab.kategori='PA'");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>"
                                + "<td valign='top' align='left' width='100%'>"
                                + "Permintaan Laborat Patologi Anatomi :"
                                + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                + "<tr class='isi'>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='15%'>Kode Periksa</td>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='85%'>Nama Pemeriksaan</td>"
                                + "</tr>"
                            );
                            Valid.tabelKosong(tabModePA);
                            rs.beforeFirst();
                            while (rs.next()) {
                                htmlContent.append(
                                    "<tr class='isi'>"
                                    + "<td align='center'>" + rs.getString("kd_jenis_prw") + "</td>"
                                    + "<td>" + rs.getString("nm_perawatan") + "</td>"
                                    + "</tr>"
                                );
                                tabModePA.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                            }
                            htmlContent.append(
                                "</table>"
                                + "</td>"
                                + "</tr>"
                            );
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }

                    ps = koneksi.prepareStatement(
                        "select paket_mcu_permintaan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from paket_mcu_permintaan_lab "
                        + "inner join jns_perawatan_lab on paket_mcu_permintaan_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "
                        + "where paket_mcu_permintaan_lab.no_template=? and jns_perawatan_lab.kategori='MB'");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>"
                                + "<td valign='top' align='left' width='100%'>"
                                + "Permintaan Laborat Mikrobiologi & Bio Molekuler : "
                                + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                + "<tr class='isi'>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='15%'>Kode Periksa</td>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='85%'>Nama Pemeriksaan</td>"
                                + "</tr>"
                            );
                            Valid.tabelKosong(tabModeMB);
                            rs.beforeFirst();
                            while (rs.next()) {
                                htmlContent.append(
                                    "<tr class='isi'>"
                                    + "<td align='center'>" + rs.getString("kd_jenis_prw") + "</td>"
                                    + "<td>" + rs.getString("nm_perawatan") + "</td>"
                                    + "</tr>"
                                );
                                tabModeMB.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                                try {
                                    ps2 = koneksi.prepareStatement(
                                        "select paket_mcu_detail_permintaan_lab.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,template_laboratorium.nilai_rujukan_pa "
                                        + "from paket_mcu_detail_permintaan_lab inner join template_laboratorium on paket_mcu_detail_permintaan_lab.id_template=template_laboratorium.id_template where paket_mcu_detail_permintaan_lab.no_template=? and "
                                        + "paket_mcu_detail_permintaan_lab.kd_jenis_prw=? order by template_laboratorium.urut");
                                    ps2.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                                    ps2.setString(2, rs.getString("kd_jenis_prw"));
                                    rs2 = ps2.executeQuery();
                                    if (rs2.next()) {
                                        htmlContent.append(
                                            "<tr class='isi'>"
                                            + "<td align='center' width='15%'></td>"
                                            + "<td width='85%'>"
                                            + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                            + "<tr class='isi'>"
                                            + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Pemeriksaan</td>"
                                            + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Satuan</td>"
                                            + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Nilai Rujukan</td>"
                                            + "</tr>"
                                        );
                                        Valid.tabelKosong(tabModeDetailMB);
                                        rs2.beforeFirst();
                                        while (rs2.next()) {
                                            la = "";
                                            ld = "";
                                            pa = "";
                                            pd = "";
                                            if (!rs2.getString("nilai_rujukan_ld").equals("")) {
                                                ld = "LD : " + rs2.getString("nilai_rujukan_ld");
                                            }
                                            if (!rs2.getString("nilai_rujukan_la").equals("")) {
                                                la = ", LA : " + rs2.getString("nilai_rujukan_la");
                                            }
                                            if (!rs2.getString("nilai_rujukan_pa").equals("")) {
                                                pd = ", PD : " + rs2.getString("nilai_rujukan_pd");
                                            }
                                            if (!rs2.getString("nilai_rujukan_pd").equals("")) {
                                                pa = " PA : " + rs2.getString("nilai_rujukan_pa");
                                            }
                                            htmlContent.append(
                                                "<tr class='isi'>"
                                                + "<td>" + rs2.getString("Pemeriksaan") + "</td>"
                                                + "<td align='center'>" + rs2.getString("satuan") + "</td>"
                                                + "<td>" + ld + la + pd + pa + "</td>"
                                                + "</tr>"
                                            );
                                            tabModeDetailMB.addRow(new Object[] {
                                                true, "   " + rs2.getString("Pemeriksaan"), rs2.getString("satuan"), ld + la + pd + pa, rs2.getString("id_template"), rs.getString("kd_jenis_prw")
                                            });
                                        }
                                        htmlContent.append(
                                            "</table>"
                                            + "</td>"
                                            + "</tr>"
                                        );
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notif : " + e);
                                } finally {
                                    if (rs2 != null) {
                                        rs2.close();
                                    }
                                    if (ps2 != null) {
                                        ps2.close();
                                    }
                                }
                            }
                            htmlContent.append(
                                "</table>"
                                + "</td>"
                                + "</tr>"
                            );
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }

                    ps = koneksi.prepareStatement(
                        "select paket_mcu_tindakan.kd_jenis_prw,jns_perawatan.nm_perawatan,kategori_perawatan.nm_kategori from paket_mcu_tindakan inner join jns_perawatan "
                        + "on paket_mcu_tindakan.kd_jenis_prw=jns_perawatan.kd_jenis_prw inner join kategori_perawatan on kategori_perawatan.kd_kategori=jns_perawatan.kd_kategori "
                        + "where paket_mcu_tindakan.no_template=?");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>"
                                + "<td valign='top' align='left' width='100%'>"
                                + "Tindakan : "
                                + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                                + "<tr class='isi'>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode</td>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='50%'>Nama Perawatan/Tindakan</td>"
                                + "<td valign='middle' bgcolor='#FFFAF8' align='center' width='30%'>Kategori</td>"
                                + "</tr>"
                            );
                            Valid.tabelKosong(TabModeTindakan);
                            rs.beforeFirst();
                            while (rs.next()) {
                                htmlContent.append(
                                    "<tr class='isi'>"
                                    + "<td align='center'>" + rs.getString("kd_jenis_prw") + "</td>"
                                    + "<td>" + rs.getString("nm_perawatan") + "</td>"
                                    + "<td align='center'>" + rs.getString("nm_kategori") + "</td>"
                                    + "</tr>"
                                );
                                TabModeTindakan.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"), rs.getString("nm_kategori")
                                });
                            }
                            htmlContent.append(
                                "</table>"
                                + "</td>"
                                + "</tr>"
                            );

                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }

                    LoadHTML.setText(
                        "<html>"
                        + "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                        + htmlContent.toString()
                        + "</table>"
                        + "</html>");
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            }
        }
    }

    private void ganti() {
        if (Sequel.menghapustfSmc("paket_mcu", "kode_paket = ?", tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString())) {
            if (Sequel.menyimpantfSmc("paket_mcu", null, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), "-", tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString())) {
                index = 1;
                for (i = 0; i < tbPermintaanRadiologi.getRowCount(); i++) {
                    if (tbPermintaanRadiologi.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_permintaan_radiologi", "?,?", "Pemeriksaan Radiologi", 2, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbPermintaanRadiologi.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                    if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PK", 2, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbPermintaanPK.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailPK.getRowCount(); i++) {
                    if ((!tbDetailPK.getValueAt(i, 4).toString().equals("")) && tbDetailPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_detail_permintaan_lab", "?,?,?", "Detail Pemeriksaan Laboratorium PK", 3, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDetailPK.getValueAt(i, 5).toString(), tbDetailPK.getValueAt(i, 4).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanPA.getRowCount(); i++) {
                    if (tbPermintaanPA.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PA", 2, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbPermintaanPA.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                    if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PK", 2, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbPermintaanMB.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailMB.getRowCount(); i++) {
                    if ((!tbDetailMB.getValueAt(i, 4).toString().equals("")) && tbDetailMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_detail_permintaan_lab", "?,?,?", "Detail Pemeriksaan Laboratorium PK", 3, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDetailMB.getValueAt(i, 5).toString(), tbDetailMB.getValueAt(i, 4).toString()
                        });
                    }
                }
                for (i = 0; i < tbTindakan.getRowCount(); i++) {
                    if (tbTindakan.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("paket_mcu_tindakan", "?,?", "Tindakan Dokter", 2, new String[] {
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbTindakan.getValueAt(i, 1).toString()
                        });
                    }
                }
                tabMode.addRow(new String[] {
                    tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), "-", Nama.getText(), ""
                });
                tabMode.removeRow(tbDokter.getSelectedRow());
                ChkAccor.setSelected(false);
                isDetail();
                TabRawat.setSelectedIndex(1);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengganti..!!");
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from paket_mcu where no_template=?", 1, new String[] {
            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbDokter.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            LoadHTML.setText("");
            TabRawat.setSelectedIndex(1);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }
}
