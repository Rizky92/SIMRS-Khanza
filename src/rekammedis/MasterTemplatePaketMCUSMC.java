package rekammedis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.WarnaTable2;
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
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;

public class MasterTemplatePaketMCUSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeRadiologi, tabModeLabPK, tabModeDetailLabPK, tabModeLabPA, tabModeLabMB, tabModeDetailLabMB, tabModeTindakanDr, tabModeTindakanPr, tabModeTindakanDrPr;
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final ObjectMapper mapper = new ObjectMapper();
    private StringBuilder htmlContent;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    /**
     * Creates new form DlgProgramStudi
     *
     * @param parent
     * @param modal
     */
    public MasterTemplatePaketMCUSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {"No.Template", "Nama Template", "Jenis Bayar", "Tambahan (Rp)", "Diskon (Rp)"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbTemplate.setModel(tabMode);
        tbTemplate.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbTemplate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 9; i++) {
            TableColumn column = tbTemplate.getColumnModel().getColumn(i);
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
        tbTemplate.setDefaultRenderer(Object.class, new WarnaTable());

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
        tbRadiologi.setModel(tabModeRadiologi);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbRadiologi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRadiologi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 3; i++) {
            TableColumn column = tbRadiologi.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(130);
            } else if (i == 2) {
                column.setPreferredWidth(490);
            }
        }
        tbRadiologi.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeLabPK = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
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
        tbLabPK.setModel(tabModeLabPK);

        tbLabPK.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbLabPK.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 3; i++) {
            TableColumn column = tbLabPK.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(520);
            }
        }
        tbLabPK.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailLabPK = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis"}) {
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

        tbDetailLabPK.setModel(tabModeDetailLabPK);
        //tampilPr();

        tbDetailLabPK.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailLabPK.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 6; i++) {
            TableColumn column = tbDetailLabPK.getColumnModel().getColumn(i);
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

        tbDetailLabPK.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeLabPA = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
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
        tbLabPA.setModel(tabModeLabPA);

        tbLabPA.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbLabPA.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 3; i++) {
            TableColumn column = tbLabPA.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(520);
            }
        }
        tbLabPA.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeLabMB = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan"}) {
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
        tbLabMB.setModel(tabModeLabMB);

        tbLabMB.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbLabMB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 3; i++) {
            TableColumn column = tbLabMB.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(520);
            }
        }
        tbLabMB.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailLabMB = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis"}) {
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

        tbDetailLabMB.setModel(tabModeDetailLabMB);
        //tampilPr();

        tbDetailLabMB.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailLabMB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 6; i++) {
            TableColumn column = tbDetailLabMB.getColumnModel().getColumn(i);
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
        tbDetailLabMB.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTindakanDr = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Nama Perawatan/Tindakan", "Kategori", "Dokter Pemberi Tindakan"
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
        tbTindakanDokter.setModel(tabModeTindakanDr);
        tbTindakanDokter.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanDokter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 4; i++) {
            TableColumn column = tbTindakanDokter.getColumnModel().getColumn(i);
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
        tbTindakanDokter.setDefaultRenderer(Object.class, new WarnaTable());

        noTemplate.setDocument(new batasInput((byte) 20).getKata(noTemplate));
        TCariRadiologi.setDocument(new batasInput((byte) 100).getKata(TCariRadiologi));
        TCariLabPK.setDocument(new batasInput((byte) 100).getKata(TCariLabPK));
        TCariDetailLabPK.setDocument(new batasInput((byte) 100).getKata(TCariDetailLabPK));
        TCariLabPA.setDocument(new batasInput((byte) 100).getKata(TCariLabPA));
        TCariLabMB.setDocument(new batasInput((byte) 100).getKata(TCariLabMB));
        TCariDetailLabMB.setDocument(new batasInput((byte) 100).getKata(TCariDetailLabMB));
        TCariTindakanDokter.setDocument(new batasInput((byte) 100).getKata(TCariTindakanDokter));

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        ChkAccor.setSelected(false);
        isDetail();

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
        panelBiasa1 = new widget.PanelBiasa();
        label12 = new widget.Label();
        label13 = new widget.Label();
        namaTemplate = new widget.TextBox();
        noTemplate = new widget.TextBox();
        label14 = new widget.Label();
        kodeJenisBayar = new widget.TextBox();
        namaJenisBayar = new widget.TextBox();
        pilihJenisBayar = new widget.Button();
        diskon = new widget.TextBox();
        label15 = new widget.Label();
        label16 = new widget.Label();
        tambahan = new widget.TextBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        btnCariRadiologi = new widget.Button();
        Scroll3 = new widget.ScrollPane();
        tbRadiologi = new widget.Table();
        TCariRadiologi = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        TCariLabPK = new widget.TextBox();
        btnCariLabPK = new widget.Button();
        Scroll4 = new widget.ScrollPane();
        tbLabPK = new widget.Table();
        Scroll5 = new widget.ScrollPane();
        tbDetailLabPK = new widget.Table();
        TCariDetailLabPK = new widget.TextBox();
        btnCariDetailLabPK = new widget.Button();
        jLabel17 = new widget.Label();
        TCariLabPA = new widget.TextBox();
        btnCariLabPA = new widget.Button();
        Scroll6 = new widget.ScrollPane();
        tbLabPA = new widget.Table();
        jLabel18 = new widget.Label();
        TCariLabMB = new widget.TextBox();
        btnCariLabMB = new widget.Button();
        Scroll7 = new widget.ScrollPane();
        tbLabMB = new widget.Table();
        TCariDetailLabMB = new widget.TextBox();
        btnCariDetailLabMB = new widget.Button();
        Scroll8 = new widget.ScrollPane();
        tbDetailLabMB = new widget.Table();
        jLabel21 = new widget.Label();
        TCariTindakanDokter = new widget.TextBox();
        btnCariTindakanDokter = new widget.Button();
        Scroll12 = new widget.ScrollPane();
        tbTindakanDokter = new widget.Table();
        btnAllRadiologi = new widget.Button();
        btnAllLabPK = new widget.Button();
        btnAllDetailLabPK = new widget.Button();
        btnAllLabPA = new widget.Button();
        btnAllLabMB = new widget.Button();
        btnAllDetailLabMB = new widget.Button();
        btnAllTindakanDokter = new widget.Button();
        jLabel22 = new widget.Label();
        TCariTindakanDokterPetugas = new widget.TextBox();
        btnCariTindakanDokterPetugas = new widget.Button();
        btnAllTindakanDokterPetugas = new widget.Button();
        Scroll14 = new widget.ScrollPane();
        tbTindakanDokterPetugas = new widget.Table();
        jLabel23 = new widget.Label();
        TCariTindakanPetugas = new widget.TextBox();
        btnCariTindakanPetugas = new widget.Button();
        btnAllTindakanPetugas = new widget.Button();
        Scroll15 = new widget.ScrollPane();
        tbTindakanPetugas = new widget.Table();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbTemplate = new widget.Table();
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
        BtnPrint = new widget.Button();
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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout());

        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout());

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(0, 77));
        panelBiasa1.setLayout(null);

        label12.setText("Kode Template :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label12);
        label12.setBounds(0, 10, 90, 23);

        label13.setText("Nama Template :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label13);
        label13.setBounds(234, 10, 90, 23);

        namaTemplate.setName("namaTemplate"); // NOI18N
        namaTemplate.setPreferredSize(new java.awt.Dimension(492, 23));
        namaTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                namaTemplateKeyPressed(evt);
            }
        });
        panelBiasa1.add(namaTemplate);
        namaTemplate.setBounds(327, 10, 492, 23);

        noTemplate.setName("noTemplate"); // NOI18N
        noTemplate.setPreferredSize(new java.awt.Dimension(138, 23));
        noTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noTemplateKeyPressed(evt);
            }
        });
        panelBiasa1.add(noTemplate);
        noTemplate.setBounds(93, 10, 138, 23);

        label14.setText("Jenis Bayar :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label14);
        label14.setBounds(0, 40, 90, 23);

        kodeJenisBayar.setEditable(false);
        kodeJenisBayar.setName("kodeJenisBayar"); // NOI18N
        kodeJenisBayar.setPreferredSize(new java.awt.Dimension(60, 23));
        kodeJenisBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kodeJenisBayarKeyPressed(evt);
            }
        });
        panelBiasa1.add(kodeJenisBayar);
        kodeJenisBayar.setBounds(93, 40, 60, 23);

        namaJenisBayar.setEditable(false);
        namaJenisBayar.setName("namaJenisBayar"); // NOI18N
        namaJenisBayar.setPreferredSize(new java.awt.Dimension(250, 23));
        panelBiasa1.add(namaJenisBayar);
        namaJenisBayar.setBounds(156, 40, 250, 23);

        pilihJenisBayar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        pilihJenisBayar.setMnemonic('2');
        pilihJenisBayar.setToolTipText("Alt+2");
        pilihJenisBayar.setName("pilihJenisBayar"); // NOI18N
        pilihJenisBayar.setPreferredSize(new java.awt.Dimension(28, 23));
        pilihJenisBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihJenisBayarActionPerformed(evt);
            }
        });
        pilihJenisBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihJenisBayarKeyPressed(evt);
            }
        });
        panelBiasa1.add(pilihJenisBayar);
        pilihJenisBayar.setBounds(409, 40, 28, 23);

        diskon.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        diskon.setText("1,300,000");
        diskon.setName("diskon"); // NOI18N
        diskon.setPreferredSize(new java.awt.Dimension(100, 23));
        diskon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                diskonKeyPressed(evt);
            }
        });
        panelBiasa1.add(diskon);
        diskon.setBounds(523, 40, 100, 23);

        label15.setText("Diskon (Rp) :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(80, 23));
        panelBiasa1.add(label15);
        label15.setBounds(440, 40, 80, 23);

        label16.setText("Tambahan (Rp) :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label16);
        label16.setBounds(626, 40, 90, 23);

        tambahan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tambahan.setText("1,300,000");
        tambahan.setName("tambahan"); // NOI18N
        tambahan.setPreferredSize(new java.awt.Dimension(100, 23));
        tambahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tambahanKeyPressed(evt);
            }
        });
        panelBiasa1.add(tambahan);
        tambahan.setBounds(719, 40, 100, 23);

        internalFrame2.add(panelBiasa1, java.awt.BorderLayout.PAGE_START);

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(742, 1800));
        FormInput.setLayout(null);

        btnCariRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariRadiologi.setMnemonic('1');
        btnCariRadiologi.setToolTipText("Alt+1");
        btnCariRadiologi.setName("btnCariRadiologi"); // NOI18N
        btnCariRadiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariRadiologiActionPerformed(evt);
            }
        });
        FormInput.add(btnCariRadiologi);
        btnCariRadiologi.setBounds(658, 570, 28, 23);

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        tbRadiologi.setName("tbRadiologi"); // NOI18N
        Scroll3.setViewportView(tbRadiologi);

        FormInput.add(Scroll3);
        Scroll3.setBounds(16, 600, 700, 123);

        TCariRadiologi.setName("TCariRadiologi"); // NOI18N
        TCariRadiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariRadiologiKeyPressed(evt);
            }
        });
        FormInput.add(TCariRadiologi);
        TCariRadiologi.setBounds(16, 570, 640, 23);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Permintaan Radiologi :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(16, 550, 120, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Permintaan Laborat Patologi Klinis :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(16, 730, 190, 23);

        TCariLabPK.setName("TCariLabPK"); // NOI18N
        TCariLabPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariLabPKKeyPressed(evt);
            }
        });
        FormInput.add(TCariLabPK);
        TCariLabPK.setBounds(16, 750, 640, 23);

        btnCariLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariLabPK.setMnemonic('1');
        btnCariLabPK.setToolTipText("Alt+1");
        btnCariLabPK.setName("btnCariLabPK"); // NOI18N
        btnCariLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnCariLabPK);
        btnCariLabPK.setBounds(658, 750, 28, 23);

        Scroll4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);

        tbLabPK.setName("tbLabPK"); // NOI18N
        tbLabPK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbLabPKMouseClicked(evt);
            }
        });
        Scroll4.setViewportView(tbLabPK);

        FormInput.add(Scroll4);
        Scroll4.setBounds(16, 780, 700, 113);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll5.setComponentPopupMenu(Popup);
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);

        tbDetailLabPK.setComponentPopupMenu(Popup);
        tbDetailLabPK.setName("tbDetailLabPK"); // NOI18N
        Scroll5.setViewportView(tbDetailLabPK);

        FormInput.add(Scroll5);
        Scroll5.setBounds(16, 930, 700, 223);

        TCariDetailLabPK.setName("TCariDetailLabPK"); // NOI18N
        TCariDetailLabPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariDetailLabPKKeyPressed(evt);
            }
        });
        FormInput.add(TCariDetailLabPK);
        TCariDetailLabPK.setBounds(16, 900, 640, 23);

        btnCariDetailLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariDetailLabPK.setMnemonic('1');
        btnCariDetailLabPK.setToolTipText("Alt+1");
        btnCariDetailLabPK.setName("btnCariDetailLabPK"); // NOI18N
        btnCariDetailLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariDetailLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDetailLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnCariDetailLabPK);
        btnCariDetailLabPK.setBounds(658, 900, 28, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Permintaan Laborat Patologi Anatomi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(16, 1160, 250, 23);

        TCariLabPA.setName("TCariLabPA"); // NOI18N
        TCariLabPA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariLabPAKeyPressed(evt);
            }
        });
        FormInput.add(TCariLabPA);
        TCariLabPA.setBounds(16, 1180, 640, 23);

        btnCariLabPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariLabPA.setMnemonic('1');
        btnCariLabPA.setToolTipText("Alt+1");
        btnCariLabPA.setName("btnCariLabPA"); // NOI18N
        btnCariLabPA.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariLabPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariLabPAActionPerformed(evt);
            }
        });
        FormInput.add(btnCariLabPA);
        btnCariLabPA.setBounds(658, 1180, 28, 23);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbLabPA.setName("tbLabPA"); // NOI18N
        Scroll6.setViewportView(tbLabPA);

        FormInput.add(Scroll6);
        Scroll6.setBounds(16, 1210, 700, 123);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Permintaan Laborat Mikrobiologi & Bio Molekuler :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(16, 1340, 270, 23);

        TCariLabMB.setName("TCariLabMB"); // NOI18N
        TCariLabMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariLabMBKeyPressed(evt);
            }
        });
        FormInput.add(TCariLabMB);
        TCariLabMB.setBounds(16, 1360, 640, 23);

        btnCariLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariLabMB.setMnemonic('1');
        btnCariLabMB.setToolTipText("Alt+1");
        btnCariLabMB.setName("btnCariLabMB"); // NOI18N
        btnCariLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnCariLabMB);
        btnCariLabMB.setBounds(658, 1360, 28, 23);

        Scroll7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);

        tbLabMB.setName("tbLabMB"); // NOI18N
        tbLabMB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbLabMBMouseClicked(evt);
            }
        });
        Scroll7.setViewportView(tbLabMB);

        FormInput.add(Scroll7);
        Scroll7.setBounds(16, 1390, 700, 113);

        TCariDetailLabMB.setName("TCariDetailLabMB"); // NOI18N
        TCariDetailLabMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariDetailLabMBKeyPressed(evt);
            }
        });
        FormInput.add(TCariDetailLabMB);
        TCariDetailLabMB.setBounds(16, 1510, 640, 23);

        btnCariDetailLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariDetailLabMB.setMnemonic('1');
        btnCariDetailLabMB.setToolTipText("Alt+1");
        btnCariDetailLabMB.setName("btnCariDetailLabMB"); // NOI18N
        btnCariDetailLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariDetailLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDetailLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnCariDetailLabMB);
        btnCariDetailLabMB.setBounds(658, 1510, 28, 23);

        Scroll8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);

        tbDetailLabMB.setName("tbDetailLabMB"); // NOI18N
        Scroll8.setViewportView(tbDetailLabMB);

        FormInput.add(Scroll8);
        Scroll8.setBounds(16, 1540, 700, 223);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Tindakan Dokter :");
        jLabel21.setName("jLabel21"); // NOI18N
        FormInput.add(jLabel21);
        jLabel21.setBounds(16, 10, 120, 23);

        TCariTindakanDokter.setName("TCariTindakanDokter"); // NOI18N
        TCariTindakanDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariTindakanDokterKeyPressed(evt);
            }
        });
        FormInput.add(TCariTindakanDokter);
        TCariTindakanDokter.setBounds(16, 30, 640, 23);

        btnCariTindakanDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanDokter.setMnemonic('1');
        btnCariTindakanDokter.setToolTipText("Alt+1");
        btnCariTindakanDokter.setName("btnCariTindakanDokter"); // NOI18N
        btnCariTindakanDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanDokterActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanDokter);
        btnCariTindakanDokter.setBounds(658, 30, 28, 23);

        Scroll12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll12.setName("Scroll12"); // NOI18N
        Scroll12.setOpaque(true);

        tbTindakanDokter.setName("tbTindakanDokter"); // NOI18N
        Scroll12.setViewportView(tbTindakanDokter);

        FormInput.add(Scroll12);
        Scroll12.setBounds(16, 60, 700, 123);

        btnAllRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllRadiologi.setMnemonic('2');
        btnAllRadiologi.setToolTipText("Alt+2");
        btnAllRadiologi.setName("btnAllRadiologi"); // NOI18N
        btnAllRadiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllRadiologiActionPerformed(evt);
            }
        });
        FormInput.add(btnAllRadiologi);
        btnAllRadiologi.setBounds(688, 570, 28, 23);

        btnAllLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllLabPK.setMnemonic('2');
        btnAllLabPK.setToolTipText("Alt+2");
        btnAllLabPK.setName("btnAllLabPK"); // NOI18N
        btnAllLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnAllLabPK);
        btnAllLabPK.setBounds(688, 750, 28, 23);

        btnAllDetailLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllDetailLabPK.setMnemonic('2');
        btnAllDetailLabPK.setToolTipText("Alt+2");
        btnAllDetailLabPK.setName("btnAllDetailLabPK"); // NOI18N
        btnAllDetailLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllDetailLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllDetailLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnAllDetailLabPK);
        btnAllDetailLabPK.setBounds(688, 900, 28, 23);

        btnAllLabPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllLabPA.setMnemonic('2');
        btnAllLabPA.setToolTipText("Alt+2");
        btnAllLabPA.setName("btnAllLabPA"); // NOI18N
        btnAllLabPA.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllLabPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllLabPAActionPerformed(evt);
            }
        });
        FormInput.add(btnAllLabPA);
        btnAllLabPA.setBounds(688, 1180, 28, 23);

        btnAllLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllLabMB.setMnemonic('2');
        btnAllLabMB.setToolTipText("Alt+2");
        btnAllLabMB.setName("btnAllLabMB"); // NOI18N
        btnAllLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnAllLabMB);
        btnAllLabMB.setBounds(688, 1360, 28, 23);

        btnAllDetailLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllDetailLabMB.setMnemonic('2');
        btnAllDetailLabMB.setToolTipText("Alt+2");
        btnAllDetailLabMB.setName("btnAllDetailLabMB"); // NOI18N
        btnAllDetailLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllDetailLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllDetailLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnAllDetailLabMB);
        btnAllDetailLabMB.setBounds(688, 1510, 28, 23);

        btnAllTindakanDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanDokter.setMnemonic('2');
        btnAllTindakanDokter.setToolTipText("Alt+2");
        btnAllTindakanDokter.setName("btnAllTindakanDokter"); // NOI18N
        btnAllTindakanDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanDokterActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanDokter);
        btnAllTindakanDokter.setBounds(688, 30, 28, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Tindakan Dokter & Petugas :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(16, 190, 190, 23);

        TCariTindakanDokterPetugas.setName("TCariTindakanDokterPetugas"); // NOI18N
        TCariTindakanDokterPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariTindakanDokterPetugasKeyPressed(evt);
            }
        });
        FormInput.add(TCariTindakanDokterPetugas);
        TCariTindakanDokterPetugas.setBounds(16, 210, 640, 23);

        btnCariTindakanDokterPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanDokterPetugas.setMnemonic('1');
        btnCariTindakanDokterPetugas.setToolTipText("Alt+1");
        btnCariTindakanDokterPetugas.setName("btnCariTindakanDokterPetugas"); // NOI18N
        btnCariTindakanDokterPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanDokterPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanDokterPetugasActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanDokterPetugas);
        btnCariTindakanDokterPetugas.setBounds(658, 210, 28, 23);

        btnAllTindakanDokterPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanDokterPetugas.setMnemonic('2');
        btnAllTindakanDokterPetugas.setToolTipText("Alt+2");
        btnAllTindakanDokterPetugas.setName("btnAllTindakanDokterPetugas"); // NOI18N
        btnAllTindakanDokterPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanDokterPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanDokterPetugasActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanDokterPetugas);
        btnAllTindakanDokterPetugas.setBounds(688, 210, 28, 23);

        Scroll14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll14.setName("Scroll14"); // NOI18N
        Scroll14.setOpaque(true);

        tbTindakanDokterPetugas.setName("tbTindakanDokterPetugas"); // NOI18N
        Scroll14.setViewportView(tbTindakanDokterPetugas);

        FormInput.add(Scroll14);
        Scroll14.setBounds(16, 240, 700, 123);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Tindakan Petugas :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(16, 370, 190, 23);

        TCariTindakanPetugas.setName("TCariTindakanPetugas"); // NOI18N
        TCariTindakanPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariTindakanPetugasKeyPressed(evt);
            }
        });
        FormInput.add(TCariTindakanPetugas);
        TCariTindakanPetugas.setBounds(16, 390, 640, 23);

        btnCariTindakanPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanPetugas.setMnemonic('1');
        btnCariTindakanPetugas.setToolTipText("Alt+1");
        btnCariTindakanPetugas.setName("btnCariTindakanPetugas"); // NOI18N
        btnCariTindakanPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanPetugasActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanPetugas);
        btnCariTindakanPetugas.setBounds(658, 390, 28, 23);

        btnAllTindakanPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanPetugas.setMnemonic('2');
        btnAllTindakanPetugas.setToolTipText("Alt+2");
        btnAllTindakanPetugas.setName("btnAllTindakanPetugas"); // NOI18N
        btnAllTindakanPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanPetugasActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanPetugas);
        btnAllTindakanPetugas.setBounds(688, 390, 28, 23);

        Scroll15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll15.setName("Scroll15"); // NOI18N
        Scroll15.setOpaque(true);

        tbTindakanPetugas.setName("tbTindakanPetugas"); // NOI18N
        Scroll15.setViewportView(tbTindakanPetugas);

        FormInput.add(Scroll15);
        Scroll15.setBounds(16, 420, 700, 123);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Template", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbTemplate.setAutoCreateRowSorter(true);
        tbTemplate.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbTemplate.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbTemplate.setName("tbTemplate"); // NOI18N
        tbTemplate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTemplateMouseClicked(evt);
            }
        });
        tbTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbTemplateKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbTemplate);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(0, 5, 9));

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
        FormDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), null, null, null, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        panelGlass8.setLayout(new java.awt.FlowLayout(0, 5, 9));

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
            tbTemplate.requestFocus();
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

    private void tbTemplateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbTemplateMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            try {
                panggilDetail();
            } catch (java.lang.NullPointerException e) {
            }
            if ((evt.getClickCount() == 2) && (tbTemplate.getSelectedColumn() == 0)) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbTemplateMouseClicked

    private void tbTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbTemplateKeyPressed
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
                        if (tbTemplate.getSelectedRow() != -1) {
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
    }//GEN-LAST:event_tbTemplateKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbTemplate.getSelectedRow() > -1) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (kodeJenisBayar.getText().equals(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 1).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
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
        if (noTemplate.getText().trim().equals("")) {
            Valid.textKosong(noTemplate, "No.Template");
        } else if (kodeJenisBayar.getText().trim().equals("") || namaJenisBayar.getText().trim().equals("")) {
            Valid.textKosong(pilihJenisBayar, "Jenis Bayar");
        } else {
            if (tbTemplate.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (kodeJenisBayar.getText().equals(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 1).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh dokter yang bersangkutan..!!");
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
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        runBackground(() -> tampil());
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
        if (noTemplate.getText().trim().equals("")) {
            Valid.textKosong(noTemplate, "No.Template");
        } else if (kodeJenisBayar.getText().trim().equals("") || namaJenisBayar.getText().trim().equals("")) {
            Valid.textKosong(pilihJenisBayar, "Dokter");
        } else if (Asesmen.getText().trim().equals("")) {
            Valid.textKosong(Asesmen, "Asesmen");
        } else {
            if (Sequel.menyimpantf("template_pemeriksaan_dokter", "?,?,?,?,?,?,?,?", "No.Template", 8, new String[] {
                noTemplate.getText(), kodeJenisBayar.getText(), Subjek.getText(), Objek.getText(), Asesmen.getText(), Plan.getText(), Instruksi.getText(), Evaluasi.getText()
            }) == true) {
                for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                    if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_penyakit", "?,?,?", "ICD X", 3, new String[] {
                            noTemplate.getText(), tbDiagnosa.getValueAt(i, 1).toString(), tbDiagnosa.getValueAt(i, 11).toString()
                        });
                    }
                }
                for (i = 0; i < tbProsedur.getRowCount(); i++) {
                    if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_prosedur", "?,?,?,?", "ICD 9", 4, new String[] {
                            noTemplate.getText(), tbProsedur.getValueAt(i, 1).toString(), tbProsedur.getValueAt(i, 7).toString(), tbProsedur.getValueAt(i, 8).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanRadiologi.getRowCount(); i++) {
                    if (tbPermintaanRadiologi.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_radiologi", "?,?", "Pemeriksaan Radiologi", 2, new String[] {
                            noTemplate.getText(), tbPermintaanRadiologi.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                    if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PK", 2, new String[] {
                            noTemplate.getText(), tbPermintaanPK.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailPK.getRowCount(); i++) {
                    if ((!tbDetailPK.getValueAt(i, 4).toString().equals("")) && tbDetailPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_detail_permintaan_lab", "?,?,?", "Detail Pemeriksaan Laboratorium PK", 3, new String[] {
                            noTemplate.getText(), tbDetailPK.getValueAt(i, 5).toString(), tbDetailPK.getValueAt(i, 4).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanPA.getRowCount(); i++) {
                    if (tbPermintaanPA.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PA", 2, new String[] {
                            noTemplate.getText(), tbPermintaanPA.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                    if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PK", 2, new String[] {
                            noTemplate.getText(), tbPermintaanMB.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailMB.getRowCount(); i++) {
                    if ((!tbDetailMB.getValueAt(i, 4).toString().equals("")) && tbDetailMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_detail_permintaan_lab", "?,?,?", "Detail Pemeriksaan Laboratorium PK", 3, new String[] {
                            noTemplate.getText(), tbDetailMB.getValueAt(i, 5).toString(), tbDetailMB.getValueAt(i, 4).toString()
                        });
                    }
                }
                for (i = 0; i < tbObatNonRacikan.getRowCount(); i++) {
                    if (Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 1).toString()) > 0) {
                        if (tbObatNonRacikan.getValueAt(i, 0).toString().equals("true")) {
                            if (Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 9).toString()) > 0) {
                                Sequel.menyimpan("template_pemeriksaan_dokter_resep", "?,?,?,?", "Obat Non Racikan", 4, new String[] {
                                    noTemplate.getText(), tbObatNonRacikan.getValueAt(i, 2).toString(), "" + (Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString()) / Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 9).toString())), tbObatNonRacikan.getValueAt(i, 7).toString()
                                });
                            } else {
                                Sequel.menyimpan("template_pemeriksaan_dokter_resep", "?,?,?,?", "Obat Non Racikan", 4, new String[] {
                                    noTemplate.getText(), tbObatNonRacikan.getValueAt(i, 2).toString(), "" + Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString()), tbObatNonRacikan.getValueAt(i, 7).toString()
                                });
                            }
                        } else {
                            Sequel.menyimpan("template_pemeriksaan_dokter_resep", "?,?,?,?", "Obat Non Racikan", 4, new String[] {
                                noTemplate.getText(), tbObatNonRacikan.getValueAt(i, 2).toString(), "" + Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString()), tbObatNonRacikan.getValueAt(i, 7).toString()
                            });
                        }
                    }
                }
                for (i = 0; i < tbObatRacikan.getRowCount(); i++) {
                    if (Valid.SetAngka(tbObatRacikan.getValueAt(i, 4).toString()) > 0) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_resep_racikan", "?,?,?,?,?,?,?", "Obat Racikan", 7, new String[] {
                            noTemplate.getText(), tbObatRacikan.getValueAt(i, 0).toString(), tbObatRacikan.getValueAt(i, 1).toString(),
                            tbObatRacikan.getValueAt(i, 2).toString(), tbObatRacikan.getValueAt(i, 4).toString(),
                            tbObatRacikan.getValueAt(i, 5).toString(), tbObatRacikan.getValueAt(i, 6).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                    if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 10).toString()) > 0) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_resep_racikan_detail", "?,?,?,?,?,?,?", "Detail Obat Racikan", 7, new String[] {
                            noTemplate.getText(), tbDetailObatRacikan.getValueAt(i, 0).toString(), tbDetailObatRacikan.getValueAt(i, 1).toString(),
                            tbDetailObatRacikan.getValueAt(i, 6).toString(), tbDetailObatRacikan.getValueAt(i, 8).toString(),
                            tbDetailObatRacikan.getValueAt(i, 9).toString(), tbDetailObatRacikan.getValueAt(i, 10).toString()
                        });
                    }
                }
                for (i = 0; i < tbTindakan.getRowCount(); i++) {
                    if (tbTindakan.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_tindakan", "?,?", "Tindakan Dokter", 2, new String[] {
                            noTemplate.getText(), tbTindakan.getValueAt(i, 1).toString()
                        });
                    }
                }
                tabMode.addRow(new Object[] {
                    noTemplate.getText(), kodeJenisBayar.getText(), namaJenisBayar.getText(), Subjek.getText(), Objek.getText(), Asesmen.getText(), Plan.getText(), Instruksi.getText(), Evaluasi.getText()
                });
                emptTeks();
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, Subjek, BtnBatal);
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

    private void noTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noTemplateKeyPressed
        //Valid.pindah(evt,TCari,Nm,TCari);
    }//GEN-LAST:event_noTemplateKeyPressed

    private void kodeJenisBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kodeJenisBayarKeyPressed

    }//GEN-LAST:event_kodeJenisBayarKeyPressed

    private void pilihJenisBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihJenisBayarActionPerformed
        DlgCariDokter dokter = new DlgCariDokter(null, false);
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    kodeJenisBayar.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    namaJenisBayar.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                }
                kodeJenisBayar.requestFocus();
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
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_pilihJenisBayarActionPerformed

    private void pilihJenisBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihJenisBayarKeyPressed
        //Valid.pindah(evt,Monitoring,BtnSimpan);
    }//GEN-LAST:event_pilihJenisBayarKeyPressed

    private void btnCariRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariRadiologiActionPerformed
        tampilRadiologi2();
    }//GEN-LAST:event_btnCariRadiologiActionPerformed

    private void TCariRadiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariRadiologiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilRadiologi2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariPK.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Prosedur.requestFocus();
        }
    }//GEN-LAST:event_TCariRadiologiKeyPressed

    private void TCariLabPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariLabPKKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilPK2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariDetailPK.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariRadiologi.requestFocus();
        }
    }//GEN-LAST:event_TCariLabPKKeyPressed

    private void btnCariLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabPKActionPerformed
        tampilPK2();
    }//GEN-LAST:event_btnCariLabPKActionPerformed

    private void TCariDetailLabPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariDetailLabPKKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilDetailPK();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariPA.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariPK.requestFocus();
        }
    }//GEN-LAST:event_TCariDetailLabPKKeyPressed

    private void btnCariDetailLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDetailLabPKActionPerformed
        tampilDetailPK();
    }//GEN-LAST:event_btnCariDetailLabPKActionPerformed

    private void TCariLabPAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariLabPAKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilPA2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariMB.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariDetailPK.requestFocus();
        }
    }//GEN-LAST:event_TCariLabPAKeyPressed

    private void btnCariLabPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabPAActionPerformed
        tampilPA2();
    }//GEN-LAST:event_btnCariLabPAActionPerformed

    private void TCariLabMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariLabMBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilMB2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariDetailMB.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariPA.requestFocus();
        }
    }//GEN-LAST:event_TCariLabMBKeyPressed

    private void btnCariLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabMBActionPerformed
        tampilMB2();
    }//GEN-LAST:event_btnCariLabMBActionPerformed

    private void TCariDetailLabMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariDetailLabMBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilDetailMB();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            CariObatNonRacikan.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariMB.requestFocus();
        }
    }//GEN-LAST:event_TCariDetailLabMBKeyPressed

    private void btnCariDetailLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDetailLabMBActionPerformed
        tampilDetailMB();
    }//GEN-LAST:event_btnCariDetailLabMBActionPerformed

    private void TCariTindakanDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariTindakanDokterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilTindakan2();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnSimpan.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            CariObatRacikan.requestFocus();
        }
    }//GEN-LAST:event_TCariTindakanDokterKeyPressed

    private void btnCariTindakanDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanDokterActionPerformed
        tampilTindakan2();
    }//GEN-LAST:event_btnCariTindakanDokterActionPerformed

    private void btnAllRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllRadiologiActionPerformed
        CariRadiologi.setText("");
        tampilRadiologi();
        tampilRadiologi2();
    }//GEN-LAST:event_btnAllRadiologiActionPerformed

    private void btnAllLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabPKActionPerformed
        CariPA.setText("");
        tampilPK();
        tampilPK2();
    }//GEN-LAST:event_btnAllLabPKActionPerformed

    private void btnAllDetailLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllDetailLabPKActionPerformed
        CariDetailPK.setText("");
        tampilDetailPK();
    }//GEN-LAST:event_btnAllDetailLabPKActionPerformed

    private void btnAllLabPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabPAActionPerformed
        CariPA.setText("");
        tampilPA();
        tampilPA2();
    }//GEN-LAST:event_btnAllLabPAActionPerformed

    private void btnAllLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabMBActionPerformed
        CariMB.setText("");
        tampilMB();
        tampilMB2();
    }//GEN-LAST:event_btnAllLabMBActionPerformed

    private void btnAllDetailLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllDetailLabMBActionPerformed
        CariDetailMB.setText("");
        tampilDetailMB();
    }//GEN-LAST:event_btnAllDetailLabMBActionPerformed

    private void btnAllTindakanDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanDokterActionPerformed
        CariTindakan.setText("");
        tampilTindakan();
        tampilTindakan2();
    }//GEN-LAST:event_btnAllTindakanDokterActionPerformed

    private void tbLabPKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLabPKMouseClicked
        if (tabModeLabPK.getRowCount() != 0) {
            try {
                Valid.tabelKosong(tabModeDetailLabPK);
                tampilDetailPK();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbLabPKMouseClicked

    private void tbLabMBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLabMBMouseClicked
        if (tabModeLabMB.getRowCount() != 0) {
            try {
                Valid.tabelKosong(tabModeDetailLabMB);
                tampilDetailMB();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbLabMBMouseClicked

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        if (tbTemplate.getSelectedRow() != -1) {
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
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

    private void namaTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_namaTemplateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaTemplateKeyPressed

    private void diskonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_diskonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_diskonKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if(ceksukses){
            JOptionPane.showMessageDialog(null,"Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("file2.css")))) {
                    bw.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}.isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}");
                    bw.flush();
                }
                String pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih laporan..!", "Pilihan Cetak", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
                    "Laporan 1 (HTML)", "Laporan 2 (WPS)", "Laporan 3 (CSV)", "Laporan 4 (XLSX)"
                }, "Laporan 1 (HTML)");
                switch (pilihan) {
                    case "Laporan 1 (HTML)":
                    Valid.exportHtmlSmc("DataSkriningKankerKolorektal.html", "DATA SKRINING KANKER KOLOREKTAL", tbObat);
                    break;
                    case "Laporan 2 (WPS)":
                    Valid.exportWPSSmc("DataSkriningKankerKolorektal.wps", "DATA SKRINING KANKER KOLOREKTAL", tbObat);
                    break;
                    case "Laporan 3 (CSV)":
                    Valid.exportCSVSmc("DataSkriningKankerKolorektal.csv", tbObat);
                    break;
                    case "Laporan 4 (XLSX)":
                    Valid.exportXlsxSmc("DataSkriningKankerKolorektal.xlsx", tbObat);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void tambahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tambahanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tambahanKeyPressed

    private void TCariTindakanDokterPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariTindakanDokterPetugasKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TCariTindakanDokterPetugasKeyPressed

    private void btnCariTindakanDokterPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanDokterPetugasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariTindakanDokterPetugasActionPerformed

    private void btnAllTindakanDokterPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanDokterPetugasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllTindakanDokterPetugasActionPerformed

    private void TCariTindakanPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariTindakanPetugasKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TCariTindakanPetugasKeyPressed

    private void btnCariTindakanPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanPetugasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariTindakanPetugasActionPerformed

    private void btnAllTindakanPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanPetugasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllTindakanPetugasActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            MasterTemplatePaketMCUSMC dialog = new MasterTemplatePaketMCUSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkAccor;
    private widget.PanelBiasa FormDetail;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll12;
    private widget.ScrollPane Scroll13;
    private widget.ScrollPane Scroll14;
    private widget.ScrollPane Scroll15;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.TextBox TCari;
    public widget.TextBox TCariDetailLabMB;
    public widget.TextBox TCariDetailLabPK;
    public widget.TextBox TCariLabMB;
    public widget.TextBox TCariLabPA;
    public widget.TextBox TCariLabPK;
    public widget.TextBox TCariRadiologi;
    public widget.TextBox TCariTindakanDokter;
    public widget.TextBox TCariTindakanDokterPetugas;
    public widget.TextBox TCariTindakanPetugas;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Button btnAllDetailLabMB;
    private widget.Button btnAllDetailLabPK;
    private widget.Button btnAllLabMB;
    private widget.Button btnAllLabPA;
    private widget.Button btnAllLabPK;
    private widget.Button btnAllRadiologi;
    private widget.Button btnAllTindakanDokter;
    private widget.Button btnAllTindakanDokterPetugas;
    private widget.Button btnAllTindakanPetugas;
    private widget.Button btnCariDetailLabMB;
    private widget.Button btnCariDetailLabPK;
    private widget.Button btnCariLabMB;
    private widget.Button btnCariLabPA;
    private widget.Button btnCariLabPK;
    private widget.Button btnCariRadiologi;
    private widget.Button btnCariTindakanDokter;
    private widget.Button btnCariTindakanDokterPetugas;
    private widget.Button btnCariTindakanPetugas;
    private widget.TextBox diskon;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.TextBox kodeJenisBayar;
    private widget.Label label10;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label9;
    private widget.TextBox namaJenisBayar;
    private widget.TextBox namaTemplate;
    private widget.TextBox noTemplate;
    private widget.PanelBiasa panelBiasa1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Button pilihJenisBayar;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppSemua;
    private widget.ScrollPane scrollInput;
    private widget.TextBox tambahan;
    public widget.Table tbDetailLabMB;
    public widget.Table tbDetailLabPK;
    public widget.Table tbLabMB;
    public widget.Table tbLabPA;
    public widget.Table tbLabPK;
    public widget.Table tbRadiologi;
    private widget.Table tbTemplate;
    public widget.Table tbTindakanDokter;
    public widget.Table tbTindakanDokterPetugas;
    public widget.Table tbTindakanPetugas;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (akses.getkode().equals("Admin Utama")) {
                ps = koneksi.prepareStatement(
                    "select template_pemeriksaan_dokter.no_template,template_pemeriksaan_dokter.kd_dokter,dokter.nm_dokter," +
                    "template_pemeriksaan_dokter.keluhan,template_pemeriksaan_dokter.pemeriksaan,template_pemeriksaan_dokter.penilaian," +
                    "template_pemeriksaan_dokter.rencana,template_pemeriksaan_dokter.instruksi,template_pemeriksaan_dokter.evaluasi " +
                    "from template_pemeriksaan_dokter inner join dokter on dokter.kd_dokter=template_pemeriksaan_dokter.kd_dokter " +
                    (TCari.getText().equals("") ? "" : "where template_pemeriksaan_dokter.no_template like ? or dokter.nm_dokter like ? or " +
                    "template_pemeriksaan_dokter.keluhan like ? or template_pemeriksaan_dokter.pemeriksaan like ? or " +
                    "template_pemeriksaan_dokter.penilaian like ? or template_pemeriksaan_dokter.rencana like ? or " +
                    "template_pemeriksaan_dokter.instruksi like ? or template_pemeriksaan_dokter.evaluasi like ? ") +
                    "order by template_pemeriksaan_dokter.no_template");
            } else {
                ps = koneksi.prepareStatement(
                    "select template_pemeriksaan_dokter.no_template,template_pemeriksaan_dokter.kd_dokter,dokter.nm_dokter," +
                    "template_pemeriksaan_dokter.keluhan,template_pemeriksaan_dokter.pemeriksaan,template_pemeriksaan_dokter.penilaian," +
                    "template_pemeriksaan_dokter.rencana,template_pemeriksaan_dokter.instruksi,template_pemeriksaan_dokter.evaluasi " +
                    "from template_pemeriksaan_dokter inner join dokter on dokter.kd_dokter=template_pemeriksaan_dokter.kd_dokter " +
                    "where template_pemeriksaan_dokter.kd_dokter=? " + (TCari.getText().equals("") ? "" : "and (template_pemeriksaan_dokter.no_template like ? or " +
                    "template_pemeriksaan_dokter.keluhan like ? or template_pemeriksaan_dokter.pemeriksaan like ? or " +
                    "template_pemeriksaan_dokter.penilaian like ? or template_pemeriksaan_dokter.rencana like ? or " +
                    "template_pemeriksaan_dokter.instruksi like ? or template_pemeriksaan_dokter.evaluasi like ?) ") +
                    "order by template_pemeriksaan_dokter.no_template");
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
        noTemplate.setText("");
        Subjek.setText("");
        Objek.setText("");
        Asesmen.setText("");
        Plan.setText("");
        Instruksi.setText("");
        Evaluasi.setText("");
        Diagnosa.setText("");
        Prosedur.setText("");
        CariRadiologi.setText("");
        CariPK.setText("");
        CariDetailPK.setText("");
        CariPA.setText("");
        CariMB.setText("");
        CariDetailMB.setText("");
        CariObatNonRacikan.setText("");
        CariObatRacikan.setText("");
        CariTindakan.setText("");
        Valid.tabelKosong(tabModeDiagnosa);
        Valid.tabelKosong(tabModeProsedur);
        Valid.tabelKosong(tabModeRadiologi);
        Valid.tabelKosong(tabModeLabPK);
        Valid.tabelKosong(tabModeDetailLabPK);
        Valid.tabelKosong(tabModeLabPA);
        Valid.tabelKosong(tabModeLabMB);
        Valid.tabelKosong(tabModeDetailLabMB);
        Valid.tabelKosong(tabModeObatUmum);
        Valid.tabelKosong(tabModeObatRacikan);
        Valid.tabelKosong(tabModeDetailObatRacikan);
        Valid.tabelKosong(tabModeTindakanDr);
        Valid.autoNomer("template_pemeriksaan_dokter", "TPD", 16, noTemplate);
        TabRawat.setSelectedIndex(0);
        Subjek.requestFocus();
    }

    private void getData() {
        if (tbTemplate.getSelectedRow() != -1) {
            noTemplate.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
            Subjek.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 3).toString());
            Objek.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 4).toString());
            Asesmen.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 5).toString());
            Plan.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 6).toString());
            Instruksi.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 7).toString());
            Evaluasi.setText(tabMode.getValueAt(tbTemplate.getSelectedRow(), 8).toString());
        }
    }

    public JTable getTable() {
        return tbTemplate;
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.gettemplate_pemeriksaan());
        BtnHapus.setEnabled(akses.gettemplate_pemeriksaan());
        BtnEdit.setEnabled(akses.gettemplate_pemeriksaan());
        if (akses.getjml2() >= 1) {
            kodeJenisBayar.setEditable(false);
            pilihJenisBayar.setEnabled(false);
            kodeJenisBayar.setText(akses.getkode());
            namaJenisBayar.setText(Sequel.CariDokter(kodeJenisBayar.getText()));
            if (namaJenisBayar.getText().equals("")) {
                kodeJenisBayar.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan Dokter...!!");
            }
        }
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
    }

    private void tampildiagnosa() {
        try {
            jml = 0;
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = new boolean[jml];
            kode = new String[jml];
            nama = new String[jml];
            ciripny = new String[jml];
            keterangan = new String[jml];
            kategori = new String[jml];
            cirium = new String[jml];
            validcode = new String[jml];
            accpdx = new String[jml];
            asterisk = new String[jml];
            im = new String[jml];
            urut = new String[jml];

            index = 0;
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbDiagnosa.getValueAt(i, 1).toString();
                    nama[index] = tbDiagnosa.getValueAt(i, 2).toString();
                    ciripny[index] = tbDiagnosa.getValueAt(i, 3).toString();
                    keterangan[index] = tbDiagnosa.getValueAt(i, 4).toString();
                    kategori[index] = tbDiagnosa.getValueAt(i, 5).toString();
                    cirium[index] = tbDiagnosa.getValueAt(i, 6).toString();
                    validcode[index] = tbDiagnosa.getValueAt(i, 7).toString();
                    accpdx[index] = tbDiagnosa.getValueAt(i, 8).toString();
                    asterisk[index] = tbDiagnosa.getValueAt(i, 9).toString();
                    im[index] = tbDiagnosa.getValueAt(i, 10).toString();
                    urut[index] = tbDiagnosa.getValueAt(i, 11).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeDiagnosa);
            for (i = 0; i < jml; i++) {
                tabModeDiagnosa.addRow(new Object[] {pilih[i], kode[i], nama[i], ciripny[i], keterangan[i], kategori[i], cirium[i], validcode[i], accpdx[i], asterisk[i], im[i], urut[i]});
            }

            pilih = null;
            kode = null;
            nama = null;
            ciripny = null;
            keterangan = null;
            kategori = null;
            cirium = null;
            validcode = null;
            accpdx = null;
            asterisk = null;
            im = null;
            urut = null;

            ps = koneksi.prepareStatement(
                "select penyakit.kd_penyakit,penyakit.nm_penyakit,penyakit.ciri_ciri,penyakit.keterangan,kategori_penyakit.nm_kategori," +
                "kategori_penyakit.ciri_umum,penyakit.validcode,penyakit.accpdx,penyakit.asterisk,penyakit.im from kategori_penyakit " +
                "inner join penyakit on penyakit.kd_ktg=kategori_penyakit.kd_ktg " +
                (Diagnosa.getText().trim().equals("") ? "" : "where penyakit.kd_penyakit like ? or " +
                "penyakit.nm_penyakit like ? or penyakit.ciri_ciri like ? or penyakit.keterangan like ? or " +
                "kategori_penyakit.nm_kategori like ? or kategori_penyakit.ciri_umum like ? ") +
                "order by penyakit.kd_penyakit  LIMIT 1000");
            try {
                if (!Diagnosa.getText().trim().equals("")) {
                    ps.setString(1, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(2, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(3, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(4, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(5, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(6, "%" + Diagnosa.getText().trim() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeDiagnosa.addRow(new Object[] {
                        false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), ""
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
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilprosedure() {
        try {
            jml = 0;
            for (i = 0; i < tbProsedur.getRowCount(); i++) {
                if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = new boolean[jml];
            kode2 = new String[jml];
            panjang = new String[jml];
            pendek = new String[jml];
            validcode = new String[jml];
            accpdx = new String[jml];
            im = new String[jml];
            urut = new String[jml];
            multy = new String[jml];
            index = 0;
            for (i = 0; i < tbProsedur.getRowCount(); i++) {
                if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode2[index] = tbProsedur.getValueAt(i, 1).toString();
                    panjang[index] = tbProsedur.getValueAt(i, 2).toString();
                    pendek[index] = tbProsedur.getValueAt(i, 3).toString();
                    validcode[index] = tbProsedur.getValueAt(i, 4).toString();
                    accpdx[index] = tbProsedur.getValueAt(i, 5).toString();
                    im[index] = tbProsedur.getValueAt(i, 6).toString();
                    urut[index] = tbProsedur.getValueAt(i, 7).toString();
                    multy[index] = tbProsedur.getValueAt(i, 8).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeProsedur);
            for (i = 0; i < jml; i++) {
                tabModeProsedur.addRow(new Object[] {pilih[i], kode2[i], panjang[i], pendek[i], validcode[i], accpdx[i], im[i], urut[i], multy[i]});
            }

            pilih = null;
            kode2 = null;
            panjang = null;
            pendek = null;
            validcode = null;
            accpdx = null;
            im = null;
            urut = null;
            multy = null;

            ps = koneksi.prepareStatement(
                "select * from icd9 " + (Prosedur.getText().trim().equals("") ? "" : "where kode like ? or deskripsi_panjang like ? or  deskripsi_pendek like ?") + " order by kode");
            try {
                if (!Prosedur.getText().trim().equals("")) {
                    ps.setString(1, "%" + Prosedur.getText().trim() + "%");
                    ps.setString(2, "%" + Prosedur.getText().trim() + "%");
                    ps.setString(3, "%" + Prosedur.getText().trim() + "%");
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeProsedur.addRow(new Object[] {
                        false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), "", ""
                    });
                }
            } catch (Exception ex) {
                System.out.println(ex);
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
    }

    private void tampilRadiologi() {
        try {
            file = new File("./cache/permintaanradiologi.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            StringBuilder iyembuilder = new StringBuilder();

            ps = koneksi.prepareStatement(
                "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan from jns_perawatan_radiologi where jns_perawatan_radiologi.status='1' and (jns_perawatan_radiologi.kelas='-' or jns_perawatan_radiologi.kelas='Rawat Jalan') order by jns_perawatan_radiologi.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyembuilder.append("{\"KodePeriksa\":\"").append(rs.getString(1)).append("\",\"NamaPemeriksaan\":\"").append(rs.getString(2).replaceAll("\"", "")).append("\"},");
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
            if (iyembuilder.length() > 0) {
                iyembuilder.setLength(iyembuilder.length() - 1);
                fileWriter.write("{\"permintaanradiologi\":[" + iyembuilder + "]}");
                fileWriter.flush();
            }

            fileWriter.close();
            iyembuilder = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        } finally {
            if (fileWriter != null) try {
                fileWriter.close();
            } catch (Exception e) {
            }
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

            pilih = new boolean[jml];
            kode = new String[jml];
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

            pilih = null;
            kode = null;
            nama = null;

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
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
        }
    }

    private void tampilPK() {
        try {
            file = new File("./cache/permintaanpk.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            StringBuilder iyembuilder = new StringBuilder();

            ps = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where jns_perawatan_lab.status='1' and jns_perawatan_lab.kategori='PK' and (jns_perawatan_lab.kelas='-' or jns_perawatan_lab.kelas='Rawat Jalan') order by jns_perawatan_lab.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyembuilder.append("{\"KodePeriksa\":\"").append(rs.getString(1)).append("\",\"NamaPemeriksaan\":\"").append(rs.getString(2).replaceAll("\"", "")).append("\"},");
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

            if (iyembuilder.length() > 0) {
                iyembuilder.setLength(iyembuilder.length() - 1);
                fileWriter.write("{\"permintaanpk\":[" + iyembuilder + "]}");
                fileWriter.flush();
            }

            fileWriter.close();
            iyembuilder = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        } finally {
            if (fileWriter != null) try {
                fileWriter.close();
            } catch (Exception e) {
            }
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

            pilih = new boolean[jml];
            kode = new String[jml];
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

            Valid.tabelKosong(tabModeLabPK);
            for (i = 0; i < jml; i++) {
                tabModeLabPK.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            pilih = null;
            kode = null;
            nama = null;

            myObj = new FileReader("./cache/permintaanpk.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanpk");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariPK.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariPK.getText().toLowerCase()))) {
                        tabModeLabPK.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
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

            pilih = new boolean[jml];
            nama = new String[jml];
            satuan = new String[jml];
            nilairujukan = new String[jml];
            kode = new String[jml];
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

            Valid.tabelKosong(tabModeDetailLabPK);

            for (i = 0; i < jml; i++) {
                tabModeDetailLabPK.addRow(new Object[] {
                    pilih[i], nama[i], satuan[i], nilairujukan[i], kode[i], kode2[i]
                });
            }

            pilih = null;
            nama = null;
            satuan = null;
            nilairujukan = null;
            kode = null;
            kode2 = null;

            for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                    tabModeDetailLabPK.addRow(new Object[] {false, tbPermintaanPK.getValueAt(i, 2).toString(), "", "", "", ""});
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
                            tabModeDetailLabPK.addRow(new Object[] {
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
            StringBuilder iyembuilder = new StringBuilder();

            ps = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where jns_perawatan_lab.status='1' and jns_perawatan_lab.kategori='PA' and (jns_perawatan_lab.kelas='-' or jns_perawatan_lab.kelas='Rawat Jalan') order by jns_perawatan_lab.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyembuilder.append("{\"KodePeriksa\":\"").append(rs.getString(1)).append("\",\"NamaPemeriksaan\":\"").append(rs.getString(2).replaceAll("\"", "")).append("\"},");
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

            if (iyembuilder.length() > 0) {
                iyembuilder.setLength(iyembuilder.length() - 1);
                fileWriter.write("{\"permintaanpa\":[" + iyembuilder + "]}");
                fileWriter.flush();
            }

            fileWriter.close();
            iyembuilder = null;
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

            pilih = new boolean[jml];
            kode = new String[jml];
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

            Valid.tabelKosong(tabModeLabPA);
            for (i = 0; i < jml; i++) {
                tabModeLabPA.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            pilih = null;
            kode = null;
            nama = null;

            myObj = new FileReader("./cache/permintaanpa.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanpa");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariPA.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariPA.getText().toLowerCase()))) {
                        tabModeLabPA.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
        }
    }

    private void tampilMB() {
        try {
            file = new File("./cache/permintaanmb.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            StringBuilder iyembuilder = new StringBuilder();

            ps = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where jns_perawatan_lab.status='1' and jns_perawatan_lab.kategori='MB' and (jns_perawatan_lab.kelas='-' or jns_perawatan_lab.kelas='Rawat Jalan') order by jns_perawatan_lab.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyembuilder.append("{\"KodePeriksa\":\"").append(rs.getString(1)).append("\",\"NamaPemeriksaan\":\"").append(rs.getString(2).replaceAll("\"", "")).append("\"},");
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

            if (iyembuilder.length() > 0) {
                iyembuilder.setLength(iyembuilder.length() - 1);
                fileWriter.write("{\"permintaanmb\":[" + iyembuilder + "]}");
                fileWriter.flush();
            }

            fileWriter.close();
            iyembuilder = null;
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

            pilih = new boolean[jml];
            kode = new String[jml];
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

            Valid.tabelKosong(tabModeLabMB);
            for (i = 0; i < jml; i++) {
                tabModeLabMB.addRow(new Object[] {pilih[i], kode[i], nama[i]});
            }

            pilih = null;
            kode = null;
            nama = null;

            myObj = new FileReader("./cache/permintaanmb.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanmb");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariMB.getText().toLowerCase()) || list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariMB.getText().toLowerCase()))) {
                        tabModeLabMB.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
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

            pilih = new boolean[jml];
            nama = new String[jml];
            satuan = new String[jml];
            nilairujukan = new String[jml];
            kode = new String[jml];
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

            Valid.tabelKosong(tabModeDetailLabMB);

            for (i = 0; i < jml; i++) {
                tabModeDetailLabMB.addRow(new Object[] {
                    pilih[i], nama[i], satuan[i], nilairujukan[i], kode[i], kode2[i]
                });
            }

            pilih = null;
            nama = null;
            satuan = null;
            nilairujukan = null;
            kode = null;
            kode2 = null;

            for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                    tabModeDetailLabMB.addRow(new Object[] {false, tbPermintaanMB.getValueAt(i, 2).toString(), "", "", "", ""});
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
                            tabModeDetailLabMB.addRow(new Object[] {
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

    private void tampilObatNonRacikan() {
        try {
            file = new File("./cache/permintaanobatnonracikan.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            StringBuilder iyembuilder = new StringBuilder();
            ps = koneksi.prepareStatement(
                "select databarang.kode_brng,databarang.nama_brng,kodesatuan.satuan,databarang.letak_barang,jenis.nama,industrifarmasi.nama_industri,databarang.kapasitas " +
                "from databarang inner join kodesatuan on kodesatuan.kode_sat=databarang.kode_sat inner join jenis on databarang.kdjns=jenis.kdjns " +
                "inner join industrifarmasi on industrifarmasi.kode_industri=databarang.kode_industri where databarang.status='1' order by databarang.nama_brng");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyembuilder.append("{\"KodeBarang\":\"").append(rs.getString(1)).append("\",\"NamaBarang\":\"").append(rs.getString(2).replaceAll("\"", "")).append("\",\"Satuan\":\"").append(rs.getString(3)).append("\",\"Komposisi\":\"").append(rs.getString(4)).append("\",\"JenisObat\":\"").append(rs.getString(5)).append("\",\"Industri\":\"").append(rs.getString(6)).append("\",\"Kapasitas\":\"").append(rs.getString(7)).append("\"},");
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

            if (iyembuilder.length() > 0) {
                iyembuilder.setLength(iyembuilder.length() - 1);
                fileWriter.write("{\"permintaanobatnonracikan\":[" + iyembuilder + "]}");
                fileWriter.flush();
            }

            fileWriter.close();
            iyembuilder = null;
        } catch (Exception e) {
            System.out.println("Notifikasi 2 : " + e);
        }
    }

    private void tampilObatNonRacikan2() {
        try {
            jml = 0;
            for (i = 0; i < tbObatNonRacikan.getRowCount(); i++) {
                if (Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 1).toString()) > 0) {
                    jml++;
                }
            }

            pilih = new boolean[jml];
            jumlah = new double[jml];
            kode = new String[jml];
            nama = new String[jml];
            satuan = new String[jml];
            cirium = new String[jml];
            kategori = new String[jml];
            keterangan = new String[jml];
            ciripny = new String[jml];
            kps = new double[jml];

            index = 0;
            for (i = 0; i < tbObatNonRacikan.getRowCount(); i++) {
                if (Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 1).toString()) > 0) {
                    pilih[index] = Boolean.parseBoolean(tbObatNonRacikan.getValueAt(i, 0).toString());
                    try {
                        jumlah[index] = Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString());
                    } catch (Exception e) {
                        jumlah[index] = 0;
                    }
                    kode[index] = tbObatNonRacikan.getValueAt(i, 2).toString();
                    nama[index] = tbObatNonRacikan.getValueAt(i, 3).toString();
                    satuan[index] = tbObatNonRacikan.getValueAt(i, 4).toString();
                    cirium[index] = tbObatNonRacikan.getValueAt(i, 5).toString();
                    kategori[index] = tbObatNonRacikan.getValueAt(i, 6).toString();
                    keterangan[index] = tbObatNonRacikan.getValueAt(i, 7).toString();
                    ciripny[index] = tbObatNonRacikan.getValueAt(i, 8).toString();
                    try {
                        kps[index] = Double.parseDouble(tbObatNonRacikan.getValueAt(i, 9).toString());
                    } catch (Exception e) {
                        kps[index] = 0;
                    }
                    index++;
                }
            }

            Valid.tabelKosong(tabModeObatUmum);

            for (i = 0; i < jml; i++) {
                tabModeObatUmum.addRow(new Object[] {pilih[i], jumlah[i], kode[i], nama[i], satuan[i], cirium[i], kategori[i], keterangan[i], ciripny[i], kps[i]});
            }

            pilih = null;
            jumlah = null;
            kode = null;
            nama = null;
            satuan = null;
            cirium = null;
            kategori = null;
            keterangan = null;
            ciripny = null;
            kps = null;

            myObj = new FileReader("./cache/permintaanobatnonracikan.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanobatnonracikan");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodeBarang").asText().toLowerCase().contains(CariObatNonRacikan.getText().toLowerCase()) || list.path("NamaBarang").asText().toLowerCase().contains(CariObatNonRacikan.getText().toLowerCase()) ||
                        list.path("Komposisi").asText().toLowerCase().contains(CariObatNonRacikan.getText().toLowerCase()) || list.path("JenisObat").asText().toLowerCase().contains(CariObatNonRacikan.getText().toLowerCase()))) {
                        tabModeObatUmum.addRow(new Object[] {
                            false, "", list.path("KodeBarang").asText(), list.path("NamaBarang").asText(), list.path("Satuan").asText(), list.path("Komposisi").asText(), list.path("JenisObat").asText(), "", list.path("Industri").asText(), list.path("Kapasitas").asDouble()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
        }
    }

    private void tampilDetailObatRacikan() {
        try {
            jml = 0;
            for (i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 10).toString()) > 0) {
                    jml++;
                }
            }

            no = new String[jml];
            kode = new String[jml];
            nama = new String[jml];
            satuan = new String[jml];
            kategori = new String[jml];
            kps = new double[jml];
            p1 = new double[jml];
            p2 = new double[jml];
            keterangan = new String[jml];
            jumlah = new double[jml];
            cirium = new String[jml];
            ciripny = new String[jml];

            index = 0;
            for (i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 10).toString()) > 0) {
                    no[index] = tbDetailObatRacikan.getValueAt(i, 0).toString();
                    kode[index] = tbDetailObatRacikan.getValueAt(i, 1).toString();
                    nama[index] = tbDetailObatRacikan.getValueAt(i, 2).toString();
                    satuan[index] = tbDetailObatRacikan.getValueAt(i, 3).toString();
                    kategori[index] = tbDetailObatRacikan.getValueAt(i, 4).toString();
                    try {
                        kps[index] = Double.parseDouble(tbDetailObatRacikan.getValueAt(i, 5).toString());
                    } catch (Exception e) {
                        kps[index] = 0;
                    }
                    try {
                        p1[index] = Double.parseDouble(tbDetailObatRacikan.getValueAt(i, 6).toString());
                    } catch (Exception e) {
                        p1[index] = 0;
                    }
                    try {
                        p2[index] = Double.parseDouble(tbDetailObatRacikan.getValueAt(i, 8).toString());
                    } catch (Exception e) {
                        p2[index] = 0;
                    }
                    keterangan[index] = tbDetailObatRacikan.getValueAt(i, 9).toString();
                    try {
                        jumlah[index] = Double.parseDouble(tbDetailObatRacikan.getValueAt(i, 10).toString());
                    } catch (Exception e) {
                        jumlah[index] = 0;
                    }
                    cirium[index] = tbDetailObatRacikan.getValueAt(i, 11).toString();
                    ciripny[index] = tbDetailObatRacikan.getValueAt(i, 12).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeDetailObatRacikan);
            for (i = 0; i < index; i++) {
                tabModeDetailObatRacikan.addRow(new Object[] {
                    no[i], kode[i], nama[i], satuan[i], kategori[i], kps[i], p1[i], "/", p2[i], keterangan[i], jumlah[i], cirium[i], ciripny[i]
                });
            }

            no = null;
            kode = null;
            nama = null;
            satuan = null;
            kategori = null;
            kps = null;
            p1 = null;
            p2 = null;
            keterangan = null;
            jumlah = null;
            cirium = null;
            ciripny = null;

            myObj = new FileReader("./cache/permintaanobatnonracikan.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaanobatnonracikan");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodeBarang").asText().toLowerCase().contains(CariObatRacikan.getText().toLowerCase()) || list.path("NamaBarang").asText().toLowerCase().contains(CariObatRacikan.getText().toLowerCase()) ||
                        list.path("Komposisi").asText().toLowerCase().contains(CariObatRacikan.getText().toLowerCase()) || list.path("JenisObat").asText().toLowerCase().contains(CariObatRacikan.getText().toLowerCase()))) {
                        tabModeDetailObatRacikan.addRow(new Object[] {
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(), 0).toString(), list.path("KodeBarang").asText(), list.path("NamaBarang").asText(), list.path("Satuan").asText(),
                            list.path("JenisObat").asText(), list.path("Kapasitas").asDouble(), 1, "/", 1, "", 0, list.path("Industri").asText(), list.path("Komposisi").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
        }
    }

    private void tampilTindakan() {
        try {
            file = new File("./cache/permintaantindakan.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            StringBuilder iyembuilder = new StringBuilder();

            ps = koneksi.prepareStatement(
                "select jns_perawatan.kd_jenis_prw,jns_perawatan.nm_perawatan,kategori_perawatan.nm_kategori from jns_perawatan inner join kategori_perawatan " +
                "on jns_perawatan.kd_kategori=kategori_perawatan.kd_kategori where jns_perawatan.status='1' and jns_perawatan.total_byrdr>0 order by jns_perawatan.kd_jenis_prw");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    iyembuilder.append("{\"KodePeriksa\":\"").append(rs.getString(1)).append("\",\"NamaPemeriksaan\":\"").append(rs.getString(2).replaceAll("\"", "")).append("\",\"Kategori\":\"").append(rs.getString(3).replaceAll("\"", "")).append("\"},");
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
            if (iyembuilder.length() > 0) {
                iyembuilder.setLength(iyembuilder.length() - 1);
                fileWriter.write("{\"permintaantindakan\":[" + iyembuilder + "]}");
                fileWriter.flush();
            }

            fileWriter.close();
            iyembuilder = null;
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

            pilih = new boolean[jml];
            kode = new String[jml];
            nama = new String[jml];
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

            Valid.tabelKosong(tabModeTindakanDr);
            for (i = 0; i < jml; i++) {
                tabModeTindakanDr.addRow(new Object[] {pilih[i], kode[i], nama[i], kategori[i]});
            }

            pilih = null;
            kode = null;
            nama = null;
            kategori = null;

            myObj = new FileReader("./cache/permintaantindakan.iyem");
            root = mapper.readTree(myObj);
            response = root.path("permintaantindakan");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if ((list.path("KodePeriksa").asText().toLowerCase().contains(CariTindakan.getText().toLowerCase()) ||
                        list.path("NamaPemeriksaan").asText().toLowerCase().contains(CariTindakan.getText().toLowerCase()) ||
                        list.path("Kategori").asText().toLowerCase().contains(CariTindakan.getText().toLowerCase()))) {
                        tabModeTindakanDr.addRow(new Object[] {
                            false, list.path("KodePeriksa").asText(), list.path("NamaPemeriksaan").asText(), list.path("Kategori").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (myObj != null) try {
                myObj.close();
            } catch (Exception e) {
            }
            response = null;
            root = null;
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
            if (tbTemplate.getSelectedRow() != -1) {
                try {
                    htmlContent = new StringBuilder();
                    htmlContent.append(
                        "<tr class='isi'>").append(
                            "<td valign='top' align='left' width='100%'>").append(
                            "Subjek : ").append(tabMode.getValueAt(tbTemplate.getSelectedRow(), 3).toString()).append(
                        "</td>").append(
                            "</tr>").append(
                            "<tr class='isi'>").append(
                            "<td valign='top' align='left' width='100%'>").append(
                            "Objek : ").append(tabMode.getValueAt(tbTemplate.getSelectedRow(), 4).toString()).append(
                        "</td>").append(
                            "</tr>").append(
                            "<tr class='isi'>").append(
                            "<td valign='top' align='left' width='100%'>").append(
                            "Asesmen : ").append(tabMode.getValueAt(tbTemplate.getSelectedRow(), 5).toString()).append(
                        "</td>").append(
                            "</tr>").append(
                            "<tr class='isi'>").append(
                            "<td valign='top' align='left' width='100%'>").append(
                            "Plan : ").append(tabMode.getValueAt(tbTemplate.getSelectedRow(), 6).toString()).append(
                        "</td>").append(
                            "</tr>").append(
                            "<tr class='isi'>").append(
                            "<td valign='top' align='left' width='100%'>").append(
                            "Instruksi : ").append(tabMode.getValueAt(tbTemplate.getSelectedRow(), 7).toString()).append(
                        "</td>").append(
                            "</tr>").append(
                            "<tr class='isi'>").append(
                            "<td valign='top' align='left' width='100%'>").append(
                            "Evaluasi : ").append(tabMode.getValueAt(tbTemplate.getSelectedRow(), 8).toString()).append(
                        "</td>").append(
                            "</tr>"
                        );

                    ps = koneksi.prepareStatement(
                        "select template_pemeriksaan_dokter_penyakit.kd_penyakit,penyakit.nm_penyakit,penyakit.ciri_ciri,penyakit.keterangan, " +
                        "kategori_penyakit.nm_kategori,kategori_penyakit.ciri_umum,penyakit.validcode,penyakit.accpdx,penyakit.asterisk,penyakit.im," +
                        "template_pemeriksaan_dokter_penyakit.urut from template_pemeriksaan_dokter_penyakit " +
                        "inner join penyakit on penyakit.kd_penyakit=template_pemeriksaan_dokter_penyakit.kd_penyakit " +
                        "inner join kategori_penyakit on penyakit.kd_ktg=kategori_penyakit.kd_ktg where " +
                        "template_pemeriksaan_dokter_penyakit.no_template=? order by template_pemeriksaan_dokter_penyakit.urut");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Diagnosa : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode Penyakit</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='70%'>Nama Penyakit</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='10%'>Urut</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeDiagnosa);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kd_penyakit")).append("</td>").append(
                                    "<td>").append(rs.getString("nm_penyakit")).append("</td>").append(
                                    "<td>").append(rs.getString("urut")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeDiagnosa.addRow(new Object[] {
                                    true, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11)
                                });
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_prosedur.kode,icd9.deskripsi_panjang,icd9.deskripsi_pendek,icd9.validcode,icd9.accpdx,icd9.im,template_pemeriksaan_dokter_prosedur.urut,template_pemeriksaan_dokter_prosedur.jumlah " +
                        "from template_pemeriksaan_dokter_prosedur inner join icd9 on template_pemeriksaan_dokter_prosedur.kode=icd9.kode where template_pemeriksaan_dokter_prosedur.no_template=? order by template_pemeriksaan_dokter_prosedur.urut");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Prosedur : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode Prosedur</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='60%'>Nama Prosedur</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='10%'>Urut</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='10%'>Jumlah</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeProsedur);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kode")).append("</td>").append(
                                    "<td>").append(rs.getString("deskripsi_panjang")).append("</td>").append(
                                    "<td>").append(rs.getString("urut")).append("</td>").append(
                                    "<td>").append(rs.getString("jumlah")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeProsedur.addRow(new Object[] {
                                    true, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)
                                });
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_permintaan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan from template_pemeriksaan_dokter_permintaan_radiologi " +
                        "inner join jns_perawatan_radiologi on template_pemeriksaan_dokter_permintaan_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw " +
                        "where template_pemeriksaan_dokter_permintaan_radiologi.no_template=?");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Permintaan Radiologi : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode Periksa</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='80%'>Nama Pemeriksaan</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeRadiologi);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kd_jenis_prw")).append("</td>").append(
                                    "<td>").append(rs.getString("nm_perawatan")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeRadiologi.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_permintaan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from template_pemeriksaan_dokter_permintaan_lab " +
                        "inner join jns_perawatan_lab on template_pemeriksaan_dokter_permintaan_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw " +
                        "where template_pemeriksaan_dokter_permintaan_lab.no_template=? and jns_perawatan_lab.kategori='PK'");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Permintaan Laborat Patologi Klinis : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='15%'>Kode Periksa</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='85%'>Nama Pemeriksaan</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeLabPK);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kd_jenis_prw")).append("</td>").append(
                                    "<td>").append(rs.getString("nm_perawatan")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeLabPK.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                                try {
                                    ps2 = koneksi.prepareStatement(
                                        "select template_pemeriksaan_dokter_detail_permintaan_lab.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,template_laboratorium.nilai_rujukan_pa " +
                                        "from template_pemeriksaan_dokter_detail_permintaan_lab inner join template_laboratorium on template_pemeriksaan_dokter_detail_permintaan_lab.id_template=template_laboratorium.id_template where template_pemeriksaan_dokter_detail_permintaan_lab.no_template=? and " +
                                        "template_pemeriksaan_dokter_detail_permintaan_lab.kd_jenis_prw=? order by template_laboratorium.urut");
                                    ps2.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                                    ps2.setString(2, rs.getString("kd_jenis_prw"));
                                    rs2 = ps2.executeQuery();
                                    if (rs2.next()) {
                                        Valid.tabelKosong(tabModeDetailLabPK);
                                        htmlContent.append(
                                            "<tr class='isi'>").append(
                                                "<td align='center' width='15%'></td>").append(
                                                "<td width='85%'>").append(
                                                "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                                "<tr class='isi'>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Pemeriksaan</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Satuan</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Nilai Rujukan</td>").append(
                                                "</tr>"
                                            );
                                        do {
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
                                                "<tr class='isi'>").append(
                                                    "<td>").append(rs2.getString("Pemeriksaan")).append("</td>").append(
                                                "<td align='center'>").append(rs2.getString("satuan")).append("</td>").append(
                                                "<td>").append(ld).append(la).append(pd).append(pa).append("</td>").append(
                                                "</tr>"
                                            );
                                            tabModeDetailLabPK.addRow(new Object[] {
                                                true, "   " + rs2.getString("Pemeriksaan"), rs2.getString("satuan"), ld + la + pd + pa, rs2.getString("id_template"), rs.getString("kd_jenis_prw")
                                            });
                                        } while (rs2.next());
                                        htmlContent.append(
                                            "</table>").append(
                                                "</td>").append(
                                                "</tr>"
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
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_permintaan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from template_pemeriksaan_dokter_permintaan_lab " +
                        "inner join jns_perawatan_lab on template_pemeriksaan_dokter_permintaan_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw " +
                        "where template_pemeriksaan_dokter_permintaan_lab.no_template=? and jns_perawatan_lab.kategori='PA'");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Permintaan Laborat Patologi Anatomi :").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='15%'>Kode Periksa</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='85%'>Nama Pemeriksaan</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeLabPA);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kd_jenis_prw")).append("</td>").append(
                                    "<td>").append(rs.getString("nm_perawatan")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeLabPA.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_permintaan_lab.kd_jenis_prw,jns_perawatan_lab.nm_perawatan from template_pemeriksaan_dokter_permintaan_lab " +
                        "inner join jns_perawatan_lab on template_pemeriksaan_dokter_permintaan_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw " +
                        "where template_pemeriksaan_dokter_permintaan_lab.no_template=? and jns_perawatan_lab.kategori='MB'");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Permintaan Laborat Mikrobiologi & Bio Molekuler : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='15%'>Kode Periksa</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='85%'>Nama Pemeriksaan</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeLabMB);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kd_jenis_prw")).append("</td>").append(
                                    "<td>").append(rs.getString("nm_perawatan")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeLabMB.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan")
                                });
                                try {
                                    ps2 = koneksi.prepareStatement(
                                        "select template_pemeriksaan_dokter_detail_permintaan_lab.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,template_laboratorium.nilai_rujukan_pa " +
                                        "from template_pemeriksaan_dokter_detail_permintaan_lab inner join template_laboratorium on template_pemeriksaan_dokter_detail_permintaan_lab.id_template=template_laboratorium.id_template where template_pemeriksaan_dokter_detail_permintaan_lab.no_template=? and " +
                                        "template_pemeriksaan_dokter_detail_permintaan_lab.kd_jenis_prw=? order by template_laboratorium.urut");
                                    ps2.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                                    ps2.setString(2, rs.getString("kd_jenis_prw"));
                                    rs2 = ps2.executeQuery();
                                    if (rs2.next()) {
                                        htmlContent.append(
                                            "<tr class='isi'>").append(
                                                "<td align='center' width='15%'></td>").append(
                                                "<td width='85%'>").append(
                                                "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                                "<tr class='isi'>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Pemeriksaan</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Satuan</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='40%'>Nilai Rujukan</td>").append(
                                                "</tr>"
                                            );
                                        Valid.tabelKosong(tabModeDetailLabMB);
                                        do {
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
                                                "<tr class='isi'>").append(
                                                    "<td>").append(rs2.getString("Pemeriksaan")).append("</td>").append(
                                                "<td align='center'>").append(rs2.getString("satuan")).append("</td>").append(
                                                "<td>").append(ld).append(la).append(pd).append(pa).append("</td>").append(
                                                "</tr>"
                                            );
                                            tabModeDetailLabMB.addRow(new Object[] {
                                                true, "   " + rs2.getString("Pemeriksaan"), rs2.getString("satuan"), ld + la + pd + pa, rs2.getString("id_template"), rs.getString("kd_jenis_prw")
                                            });
                                        } while (rs2.next());
                                        htmlContent.append(
                                            "</table>").append(
                                                "</td>").append(
                                                "</tr>"
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
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_resep.kode_brng,databarang.nama_brng,kodesatuan.satuan,template_pemeriksaan_dokter_resep.jml,template_pemeriksaan_dokter_resep.aturan_pakai,jenis.nama,industrifarmasi.nama_industri, " +
                        "databarang.kapasitas,databarang.letak_barang from template_pemeriksaan_dokter_resep inner join databarang on template_pemeriksaan_dokter_resep.kode_brng=databarang.kode_brng inner join kodesatuan on kodesatuan.kode_sat=databarang.kode_sat " +
                        "inner join jenis on databarang.kdjns=jenis.kdjns inner join industrifarmasi on industrifarmasi.kode_industri=databarang.kode_industri where template_pemeriksaan_dokter_resep.no_template=? order by databarang.nama_brng");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Obat Non Racikan : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='5%'>Jumlah</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='10%'>Kode Barang</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='45%'>Nama Barang</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='10%'>Satuan</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='30%'>Aturan Pakai</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeObatUmum);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("jml")).append("</td>").append(
                                    "<td align='center'>").append(rs.getString("kode_brng")).append("</td>").append(
                                    "<td>").append(rs.getString("nama_brng")).append("</td>").append(
                                    "<td align='center'>").append(rs.getString("satuan")).append("</td>").append(
                                    "<td>").append(rs.getString("aturan_pakai")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeObatUmum.addRow(new Object[] {
                                    false, rs.getString("jml"), rs.getString("kode_brng"), rs.getString("nama_brng"), rs.getString("satuan"), rs.getString("letak_barang"), rs.getString("nama"), rs.getString("aturan_pakai"), rs.getString("nama_industri"), rs.getDouble("kapasitas")
                                });
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_resep_racikan.no_racik,template_pemeriksaan_dokter_resep_racikan.kd_racik,template_pemeriksaan_dokter_resep_racikan.nama_racik,metode_racik.nm_racik,template_pemeriksaan_dokter_resep_racikan.jml_dr,template_pemeriksaan_dokter_resep_racikan.aturan_pakai," +
                        "template_pemeriksaan_dokter_resep_racikan.keterangan from template_pemeriksaan_dokter_resep_racikan inner join metode_racik on metode_racik.kd_racik=template_pemeriksaan_dokter_resep_racikan.kd_racik where template_pemeriksaan_dokter_resep_racikan.no_template=? " +
                        "order by template_pemeriksaan_dokter_resep_racikan.no_racik");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Obat Racikan : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='3%'>No.</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='33%'>Nama Racikan</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='11%'>Metode Racik</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='6%'>Jml.Racik</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='25%'>Aturan Pakai</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='22%'>Keterangan</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeObatRacikan);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("no_racik")).append("</td>").append(
                                    "<td>").append(rs.getString("nama_racik")).append("</td>").append(
                                    "<td align='center'>").append(rs.getString("nm_racik")).append("</td>").append(
                                    "<td align='center'>").append(rs.getString("jml_dr")).append("</td>").append(
                                    "<td>").append(rs.getString("aturan_pakai")).append("</td>").append(
                                    "<td>").append(rs.getString("keterangan")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeObatRacikan.addRow(new Object[] {
                                    rs.getString("no_racik"), rs.getString("nama_racik"), rs.getString("kd_racik"), rs.getString("nm_racik"), rs.getString("jml_dr"), rs.getString("aturan_pakai"), rs.getString("keterangan")
                                });
                                try {
                                    ps2 = koneksi.prepareStatement(
                                        "select template_pemeriksaan_dokter_resep_racikan_detail.kode_brng,databarang.nama_brng,kodesatuan.satuan,template_pemeriksaan_dokter_resep_racikan_detail.jml,jenis.nama," +
                                        "databarang.kapasitas,template_pemeriksaan_dokter_resep_racikan_detail.p1,template_pemeriksaan_dokter_resep_racikan_detail.p2,template_pemeriksaan_dokter_resep_racikan_detail.kandungan," +
                                        "industrifarmasi.nama_industri,databarang.letak_barang from template_pemeriksaan_dokter_resep_racikan_detail inner join databarang on template_pemeriksaan_dokter_resep_racikan_detail.kode_brng=databarang.kode_brng " +
                                        "inner join jenis on databarang.kdjns=jenis.kdjns inner join kodesatuan on kodesatuan.kode_sat=databarang.kode_sat inner join industrifarmasi on industrifarmasi.kode_industri=databarang.kode_industri " +
                                        "where template_pemeriksaan_dokter_resep_racikan_detail.no_template=? and template_pemeriksaan_dokter_resep_racikan_detail.no_racik=? order by template_pemeriksaan_dokter_resep_racikan_detail.kode_brng");
                                    ps2.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                                    ps2.setString(2, rs.getString("no_racik"));
                                    rs2 = ps2.executeQuery();
                                    if (rs2.next()) {
                                        htmlContent.append(
                                            "<tr class='isi'>").append(
                                                "<td align='center' width='15%'></td>").append(
                                                "<td width='85%' colspan='5'>").append(
                                                "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                                "<tr class='isi'>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='10%'>Jumlah</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Satuan</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode Obat</td>").append(
                                                "<td valign='middle' bgcolor='#FFFAF8' align='center' width='50%'>Nama Obat</td>").append(
                                                "</tr>"
                                            );
                                        Valid.tabelKosong(tabModeDetailObatRacikan);
                                        do {
                                            htmlContent.append(
                                                "<tr class='isi'>").append(
                                                    "<td align='center'>").append(rs2.getString("jml")).append("</td>").append(
                                                "<td align='center'>").append(rs2.getString("satuan")).append("</td>").append(
                                                "<td align='center'>").append(rs2.getString("kode_brng")).append("</td>").append(
                                                "<td align='left'>").append(rs2.getString("nama_brng")).append("</td>").append(
                                                "</tr>"
                                            );
                                            tabModeDetailObatRacikan.addRow(new Object[] {
                                                rs.getString("no_racik"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getString("satuan"),
                                                rs2.getString("nama"), rs2.getDouble("kapasitas"), rs2.getDouble("p1"), "/", rs2.getDouble("p2"), rs2.getString("kandungan"),
                                                rs2.getDouble("jml"), rs2.getString("nama_industri"), rs2.getString("letak_barang")
                                            });
                                        } while (rs2.next());
                                        htmlContent.append(
                                            "</table>").append(
                                                "</td>").append(
                                                "</tr>"
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
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "select template_pemeriksaan_dokter_tindakan.kd_jenis_prw,jns_perawatan.nm_perawatan,kategori_perawatan.nm_kategori from template_pemeriksaan_dokter_tindakan inner join jns_perawatan " +
                        "on template_pemeriksaan_dokter_tindakan.kd_jenis_prw=jns_perawatan.kd_jenis_prw inner join kategori_perawatan on kategori_perawatan.kd_kategori=jns_perawatan.kd_kategori " +
                        "where template_pemeriksaan_dokter_tindakan.no_template=?");
                    try {
                        ps.setString(1, tabMode.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            htmlContent.append(
                                "<tr class='isi'>").append(
                                    "<td valign='top' align='left' width='100%'>").append(
                                    "Tindakan : ").append(
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>").append(
                                    "<tr class='isi'>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='20%'>Kode</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='50%'>Nama Perawatan/Tindakan</td>").append(
                                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='30%'>Kategori</td>").append(
                                    "</tr>"
                                );
                            Valid.tabelKosong(tabModeTindakanDr);
                            do {
                                htmlContent.append(
                                    "<tr class='isi'>").append(
                                        "<td align='center'>").append(rs.getString("kd_jenis_prw")).append("</td>").append(
                                    "<td>").append(rs.getString("nm_perawatan")).append("</td>").append(
                                    "<td align='center'>").append(rs.getString("nm_kategori")).append("</td>").append(
                                    "</tr>"
                                );
                                tabModeTindakanDr.addRow(new Object[] {
                                    true, rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"), rs.getString("nm_kategori")
                                });
                            } while (rs.next());
                            htmlContent.append(
                                "</table>").append(
                                    "</td>").append(
                                    "</tr>"
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
                        "<html>" +
                        "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>" +
                        htmlContent.toString() +
                        "</table>" +
                        "</html>");
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            }
        }
    }

    private void ganti() {
        if (Sequel.queryu2tf("delete from template_pemeriksaan_dokter where no_template=?", 1, new String[] {
            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString()
        }) == true) {
            if (Sequel.menyimpantf("template_pemeriksaan_dokter", "?,?,?,?,?,?,?,?", "No.Template", 8, new String[] {
                tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), kodeJenisBayar.getText(), Subjek.getText(), Objek.getText(), Asesmen.getText(), Plan.getText(), Instruksi.getText(), Evaluasi.getText()
            }) == true) {
                for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                    if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_penyakit", "?,?,?", "ICD X", 3, new String[] {
                            noTemplate.getText(), tbDiagnosa.getValueAt(i, 1).toString(), tbDiagnosa.getValueAt(i, 11).toString()
                        });
                    }
                }
                for (i = 0; i < tbProsedur.getRowCount(); i++) {
                    if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_prosedur", "?,?,?,?", "ICD 9", 4, new String[] {
                            noTemplate.getText(), tbProsedur.getValueAt(i, 1).toString(), tbProsedur.getValueAt(i, 7).toString(), tbProsedur.getValueAt(i, 8).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanRadiologi.getRowCount(); i++) {
                    if (tbPermintaanRadiologi.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_radiologi", "?,?", "Pemeriksaan Radiologi", 2, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbPermintaanRadiologi.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanPK.getRowCount(); i++) {
                    if (tbPermintaanPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PK", 2, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbPermintaanPK.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailPK.getRowCount(); i++) {
                    if ((!tbDetailPK.getValueAt(i, 4).toString().equals("")) && tbDetailPK.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_detail_permintaan_lab", "?,?,?", "Detail Pemeriksaan Laboratorium PK", 3, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbDetailPK.getValueAt(i, 5).toString(), tbDetailPK.getValueAt(i, 4).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanPA.getRowCount(); i++) {
                    if (tbPermintaanPA.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PA", 2, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbPermintaanPA.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbPermintaanMB.getRowCount(); i++) {
                    if (tbPermintaanMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_permintaan_lab", "?,?", "Pemeriksaan Laboratorium PK", 2, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbPermintaanMB.getValueAt(i, 1).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailMB.getRowCount(); i++) {
                    if ((!tbDetailMB.getValueAt(i, 4).toString().equals("")) && tbDetailMB.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_detail_permintaan_lab", "?,?,?", "Detail Pemeriksaan Laboratorium PK", 3, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbDetailMB.getValueAt(i, 5).toString(), tbDetailMB.getValueAt(i, 4).toString()
                        });
                    }
                }
                for (i = 0; i < tbObatNonRacikan.getRowCount(); i++) {
                    if (Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 1).toString()) > 0) {
                        if (tbObatNonRacikan.getValueAt(i, 0).toString().equals("true")) {
                            if (Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 9).toString()) > 0) {
                                Sequel.menyimpan("template_pemeriksaan_dokter_resep", "?,?,?,?", "Obat Non Racikan", 4, new String[] {
                                    tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbObatNonRacikan.getValueAt(i, 2).toString(), "" + (Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString()) / Valid.SetAngka(tbObatNonRacikan.getValueAt(i, 9).toString())), tbObatNonRacikan.getValueAt(i, 7).toString()
                                });
                            } else {
                                Sequel.menyimpan("template_pemeriksaan_dokter_resep", "?,?,?,?", "Obat Non Racikan", 4, new String[] {
                                    tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbObatNonRacikan.getValueAt(i, 2).toString(), "" + Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString()), tbObatNonRacikan.getValueAt(i, 7).toString()
                                });
                            }
                        } else {
                            Sequel.menyimpan("template_pemeriksaan_dokter_resep", "?,?,?,?", "Obat Non Racikan", 4, new String[] {
                                tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbObatNonRacikan.getValueAt(i, 2).toString(), "" + Double.parseDouble(tbObatNonRacikan.getValueAt(i, 1).toString()), tbObatNonRacikan.getValueAt(i, 7).toString()
                            });
                        }
                    }
                }
                for (i = 0; i < tbObatRacikan.getRowCount(); i++) {
                    if (Valid.SetAngka(tbObatRacikan.getValueAt(i, 4).toString()) > 0) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_resep_racikan", "?,?,?,?,?,?,?", "Obat Racikan", 7, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbObatRacikan.getValueAt(i, 0).toString(), tbObatRacikan.getValueAt(i, 1).toString(),
                            tbObatRacikan.getValueAt(i, 2).toString(), tbObatRacikan.getValueAt(i, 4).toString(),
                            tbObatRacikan.getValueAt(i, 5).toString(), tbObatRacikan.getValueAt(i, 6).toString()
                        });
                    }
                }
                for (i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                    if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 10).toString()) > 0) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_resep_racikan_detail", "?,?,?,?,?,?,?", "Detail Obat Racikan", 7, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbDetailObatRacikan.getValueAt(i, 0).toString(), tbDetailObatRacikan.getValueAt(i, 1).toString(),
                            tbDetailObatRacikan.getValueAt(i, 6).toString(), tbDetailObatRacikan.getValueAt(i, 8).toString(),
                            tbDetailObatRacikan.getValueAt(i, 9).toString(), tbDetailObatRacikan.getValueAt(i, 10).toString()
                        });
                    }
                }
                for (i = 0; i < tbTindakan.getRowCount(); i++) {
                    if (tbTindakan.getValueAt(i, 0).toString().equals("true")) {
                        Sequel.menyimpan("template_pemeriksaan_dokter_tindakan", "?,?", "Tindakan Dokter", 2, new String[] {
                            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), tbTindakan.getValueAt(i, 1).toString()
                        });
                    }
                }
                tabMode.addRow(new Object[] {
                    tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString(), kodeJenisBayar.getText(), namaJenisBayar.getText(), Subjek.getText(), Objek.getText(), Asesmen.getText(), Plan.getText(), Instruksi.getText(), Evaluasi.getText()
                });
                tabMode.removeRow(tbTemplate.getSelectedRow());
                ChkAccor.setSelected(false);
                isDetail();
                TabRawat.setSelectedIndex(1);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengganti..!!");
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from template_pemeriksaan_dokter where no_template=?", 1, new String[] {
            tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbTemplate.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            LoadHTML.setText("");
            TabRawat.setSelectedIndex(1);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
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
