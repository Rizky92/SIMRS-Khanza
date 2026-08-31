package rekammedis;

import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;

public class MasterTemplatePaketMCUSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeRadiologi, tabModeLabPK, tabModeDetailLabPK, tabModeLabPA, tabModeLabMB, tabModeDetailLabMB, tabModeTindakanDr, tabModeTindakanPr, tabModeTindakanDrPr;
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final ObjectMapper mapper = new ObjectMapper();
    private volatile boolean ceksukses = false;
    private double total = -1;

    public MasterTemplatePaketMCUSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {"No.Template", "Nama Template", "Jenis Bayar", "Tambahan (Rp)", "Diskon (Rp)", "Total (Rp)"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbTemplate.setModel(tabMode);
        tbTemplate.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbTemplate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
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
        tbTindakanDr.setModel(tabModeTindakanDr);
        tbTindakanDr.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanDr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 4; i++) {
            TableColumn column = tbTindakanDr.getColumnModel().getColumn(i);
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
        tbTindakanDr.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTindakanDrPr = new DefaultTableModel(null, new Object[] {
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
        tbTindakanDrPr.setModel(tabModeTindakanDrPr);
        tbTindakanDrPr.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanDrPr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 4; i++) {
            TableColumn column = tbTindakanDrPr.getColumnModel().getColumn(i);
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
        tbTindakanDrPr.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTindakanPr = new DefaultTableModel(null, new Object[] {
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
        tbTindakanPr.setModel(tabModeTindakanPr);
        tbTindakanPr.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanPr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 4; i++) {
            TableColumn column = tbTindakanPr.getColumnModel().getColumn(i);
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
        tbTindakanPr.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRadiologi = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 3) {
                    return Double.class;
                }

                return String.class;
            }
        };
        tbRadiologi.setModel(tabModeRadiologi);
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

        tabModeLabPK = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 3) {
                    return Double.class;
                }

                return String.class;
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

        tabModeDetailLabPK = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis", "Harga (Rp)"}) {
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

        tabModeLabPA = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
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

        tabModeLabMB = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
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

        tabModeDetailLabMB = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis", "Harga (Rp)"}) {
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

        noTemplate.setDocument(new batasInput((byte) 20).getKata(noTemplate));
        cariRadiologi.setDocument(new batasInput((byte) 100).getKata(cariRadiologi));
        cariLabPK.setDocument(new batasInput((byte) 100).getKata(cariLabPK));
        cariDetailLabPK.setDocument(new batasInput((byte) 100).getKata(cariDetailLabPK));
        cariLabPA.setDocument(new batasInput((byte) 100).getKata(cariLabPA));
        cariLabMB.setDocument(new batasInput((byte) 100).getKata(cariLabMB));
        cariDetailLabMB.setDocument(new batasInput((byte) 100).getKata(cariDetailLabMB));
        cariTindakanDr.setDocument(new batasInput((byte) 100).getKata(cariTindakanDr));

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        ChkAccor.setSelected(false);
        isDetail();
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
        label16 = new widget.Label();
        tambahan = new widget.TextBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        btnCariRadiologi = new widget.Button();
        Scroll3 = new widget.ScrollPane();
        tbRadiologi = new widget.Table();
        cariRadiologi = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        cariLabPK = new widget.TextBox();
        btnCariLabPK = new widget.Button();
        Scroll4 = new widget.ScrollPane();
        tbLabPK = new widget.Table();
        Scroll5 = new widget.ScrollPane();
        tbDetailLabPK = new widget.Table();
        cariDetailLabPK = new widget.TextBox();
        btnCariDetailLabPK = new widget.Button();
        jLabel17 = new widget.Label();
        cariLabPA = new widget.TextBox();
        btnCariLabPA = new widget.Button();
        Scroll6 = new widget.ScrollPane();
        tbLabPA = new widget.Table();
        jLabel18 = new widget.Label();
        cariLabMB = new widget.TextBox();
        btnCariLabMB = new widget.Button();
        Scroll7 = new widget.ScrollPane();
        tbLabMB = new widget.Table();
        cariDetailLabMB = new widget.TextBox();
        btnCariDetailLabMB = new widget.Button();
        Scroll8 = new widget.ScrollPane();
        tbDetailLabMB = new widget.Table();
        jLabel21 = new widget.Label();
        cariTindakanDr = new widget.TextBox();
        btnCariTindakanDr = new widget.Button();
        Scroll12 = new widget.ScrollPane();
        tbTindakanDr = new widget.Table();
        btnAllRadiologi = new widget.Button();
        btnAllLabPK = new widget.Button();
        btnAllDetailLabPK = new widget.Button();
        btnAllLabPA = new widget.Button();
        btnAllLabMB = new widget.Button();
        btnAllDetailLabMB = new widget.Button();
        btnAllTindakanDr = new widget.Button();
        jLabel22 = new widget.Label();
        cariTindakanDrPr = new widget.TextBox();
        btnCariTindakanDrPr = new widget.Button();
        btnAllTindakanDrPr = new widget.Button();
        Scroll14 = new widget.ScrollPane();
        tbTindakanDrPr = new widget.Table();
        jLabel23 = new widget.Label();
        cariTindakanPr = new widget.TextBox();
        btnCariTindakanPr = new widget.Button();
        btnAllTindakanPr = new widget.Button();
        Scroll15 = new widget.ScrollPane();
        tbTindakanPr = new widget.Table();
        jLabel24 = new widget.Label();
        cariTambahanBiaya = new widget.TextBox();
        btnCariTambahanBiaya = new widget.Button();
        btnAllTambahanBiaya = new widget.Button();
        Scroll16 = new widget.ScrollPane();
        tbTambahanBiaya = new widget.Table();
        jLabel25 = new widget.Label();
        cariPotonganBiaya = new widget.TextBox();
        btnCariPotonganBiaya = new widget.Button();
        btnAllPotonganBiaya = new widget.Button();
        Scroll17 = new widget.ScrollPane();
        tbTambahanBiaya1 = new widget.Table();
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Template Paket MCU ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        namaTemplate.setPreferredSize(new java.awt.Dimension(390, 23));
        namaTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                namaTemplateKeyPressed(evt);
            }
        });
        panelBiasa1.add(namaTemplate);
        namaTemplate.setBounds(327, 10, 390, 23);

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
        namaJenisBayar.setPreferredSize(new java.awt.Dimension(331, 23));
        panelBiasa1.add(namaJenisBayar);
        namaJenisBayar.setBounds(156, 40, 331, 23);

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
        pilihJenisBayar.setBounds(490, 40, 28, 23);

        label16.setText("Total Biaya (Rp) :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label16);
        label16.setBounds(524, 40, 90, 23);

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
        tambahan.setBounds(617, 40, 100, 23);

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

        cariRadiologi.setName("cariRadiologi"); // NOI18N
        cariRadiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariRadiologiKeyPressed(evt);
            }
        });
        FormInput.add(cariRadiologi);
        cariRadiologi.setBounds(16, 570, 640, 23);

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

        cariLabPK.setName("cariLabPK"); // NOI18N
        cariLabPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariLabPKKeyPressed(evt);
            }
        });
        FormInput.add(cariLabPK);
        cariLabPK.setBounds(16, 750, 640, 23);

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

        cariDetailLabPK.setName("cariDetailLabPK"); // NOI18N
        cariDetailLabPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariDetailLabPKKeyPressed(evt);
            }
        });
        FormInput.add(cariDetailLabPK);
        cariDetailLabPK.setBounds(16, 900, 640, 23);

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

        cariLabPA.setName("cariLabPA"); // NOI18N
        cariLabPA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariLabPAKeyPressed(evt);
            }
        });
        FormInput.add(cariLabPA);
        cariLabPA.setBounds(16, 1180, 640, 23);

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

        cariLabMB.setName("cariLabMB"); // NOI18N
        cariLabMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariLabMBKeyPressed(evt);
            }
        });
        FormInput.add(cariLabMB);
        cariLabMB.setBounds(16, 1360, 640, 23);

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

        cariDetailLabMB.setName("cariDetailLabMB"); // NOI18N
        cariDetailLabMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariDetailLabMBKeyPressed(evt);
            }
        });
        FormInput.add(cariDetailLabMB);
        cariDetailLabMB.setBounds(16, 1510, 640, 23);

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

        cariTindakanDr.setName("cariTindakanDr"); // NOI18N
        cariTindakanDr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTindakanDrKeyPressed(evt);
            }
        });
        FormInput.add(cariTindakanDr);
        cariTindakanDr.setBounds(16, 30, 640, 23);

        btnCariTindakanDr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanDr.setMnemonic('1');
        btnCariTindakanDr.setToolTipText("Alt+1");
        btnCariTindakanDr.setName("btnCariTindakanDr"); // NOI18N
        btnCariTindakanDr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanDr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanDrActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanDr);
        btnCariTindakanDr.setBounds(658, 30, 28, 23);

        Scroll12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll12.setName("Scroll12"); // NOI18N
        Scroll12.setOpaque(true);

        tbTindakanDr.setName("tbTindakanDr"); // NOI18N
        Scroll12.setViewportView(tbTindakanDr);

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

        btnAllTindakanDr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanDr.setMnemonic('2');
        btnAllTindakanDr.setToolTipText("Alt+2");
        btnAllTindakanDr.setName("btnAllTindakanDr"); // NOI18N
        btnAllTindakanDr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanDr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanDrActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanDr);
        btnAllTindakanDr.setBounds(688, 30, 28, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Tindakan Dokter & Petugas :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(16, 190, 190, 23);

        cariTindakanDrPr.setName("cariTindakanDrPr"); // NOI18N
        cariTindakanDrPr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTindakanDrPrKeyPressed(evt);
            }
        });
        FormInput.add(cariTindakanDrPr);
        cariTindakanDrPr.setBounds(16, 210, 640, 23);

        btnCariTindakanDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanDrPr.setMnemonic('1');
        btnCariTindakanDrPr.setToolTipText("Alt+1");
        btnCariTindakanDrPr.setName("btnCariTindakanDrPr"); // NOI18N
        btnCariTindakanDrPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanDrPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanDrPrActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanDrPr);
        btnCariTindakanDrPr.setBounds(658, 210, 28, 23);

        btnAllTindakanDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanDrPr.setMnemonic('2');
        btnAllTindakanDrPr.setToolTipText("Alt+2");
        btnAllTindakanDrPr.setName("btnAllTindakanDrPr"); // NOI18N
        btnAllTindakanDrPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanDrPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanDrPrActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanDrPr);
        btnAllTindakanDrPr.setBounds(688, 210, 28, 23);

        Scroll14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll14.setName("Scroll14"); // NOI18N
        Scroll14.setOpaque(true);

        tbTindakanDrPr.setName("tbTindakanDrPr"); // NOI18N
        Scroll14.setViewportView(tbTindakanDrPr);

        FormInput.add(Scroll14);
        Scroll14.setBounds(16, 240, 700, 123);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Tindakan Petugas :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(16, 370, 190, 23);

        cariTindakanPr.setName("cariTindakanPr"); // NOI18N
        cariTindakanPr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTindakanPrKeyPressed(evt);
            }
        });
        FormInput.add(cariTindakanPr);
        cariTindakanPr.setBounds(16, 390, 640, 23);

        btnCariTindakanPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanPr.setMnemonic('1');
        btnCariTindakanPr.setToolTipText("Alt+1");
        btnCariTindakanPr.setName("btnCariTindakanPr"); // NOI18N
        btnCariTindakanPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanPrActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanPr);
        btnCariTindakanPr.setBounds(658, 390, 28, 23);

        btnAllTindakanPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanPr.setMnemonic('2');
        btnAllTindakanPr.setToolTipText("Alt+2");
        btnAllTindakanPr.setName("btnAllTindakanPr"); // NOI18N
        btnAllTindakanPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanPrActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanPr);
        btnAllTindakanPr.setBounds(688, 390, 28, 23);

        Scroll15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll15.setName("Scroll15"); // NOI18N
        Scroll15.setOpaque(true);

        tbTindakanPr.setName("tbTindakanPr"); // NOI18N
        Scroll15.setViewportView(tbTindakanPr);

        FormInput.add(Scroll15);
        Scroll15.setBounds(16, 420, 700, 123);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Tambahan Biaya :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(16, 1770, 120, 23);

        cariTambahanBiaya.setName("cariTambahanBiaya"); // NOI18N
        cariTambahanBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTambahanBiayaKeyPressed(evt);
            }
        });
        FormInput.add(cariTambahanBiaya);
        cariTambahanBiaya.setBounds(16, 1790, 640, 23);

        btnCariTambahanBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTambahanBiaya.setMnemonic('1');
        btnCariTambahanBiaya.setToolTipText("Alt+1");
        btnCariTambahanBiaya.setName("btnCariTambahanBiaya"); // NOI18N
        btnCariTambahanBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTambahanBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTambahanBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTambahanBiaya);
        btnCariTambahanBiaya.setBounds(658, 1790, 28, 23);

        btnAllTambahanBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTambahanBiaya.setMnemonic('2');
        btnAllTambahanBiaya.setToolTipText("Alt+2");
        btnAllTambahanBiaya.setName("btnAllTambahanBiaya"); // NOI18N
        btnAllTambahanBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTambahanBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTambahanBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTambahanBiaya);
        btnAllTambahanBiaya.setBounds(688, 1790, 28, 23);

        Scroll16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll16.setName("Scroll16"); // NOI18N
        Scroll16.setOpaque(true);

        tbTambahanBiaya.setName("tbTambahanBiaya"); // NOI18N
        Scroll16.setViewportView(tbTambahanBiaya);

        FormInput.add(Scroll16);
        Scroll16.setBounds(16, 1820, 700, 123);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Potongan Biaya :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(16, 1950, 120, 23);

        cariPotonganBiaya.setName("cariPotonganBiaya"); // NOI18N
        cariPotonganBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariPotonganBiayaKeyPressed(evt);
            }
        });
        FormInput.add(cariPotonganBiaya);
        cariPotonganBiaya.setBounds(16, 1970, 640, 23);

        btnCariPotonganBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariPotonganBiaya.setMnemonic('1');
        btnCariPotonganBiaya.setToolTipText("Alt+1");
        btnCariPotonganBiaya.setName("btnCariPotonganBiaya"); // NOI18N
        btnCariPotonganBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariPotonganBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPotonganBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnCariPotonganBiaya);
        btnCariPotonganBiaya.setBounds(658, 1970, 28, 23);

        btnAllPotonganBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllPotonganBiaya.setMnemonic('2');
        btnAllPotonganBiaya.setToolTipText("Alt+2");
        btnAllPotonganBiaya.setName("btnAllPotonganBiaya"); // NOI18N
        btnAllPotonganBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllPotonganBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllPotonganBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnAllPotonganBiaya);
        btnAllPotonganBiaya.setBounds(688, 1970, 28, 23);

        Scroll17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll17.setName("Scroll17"); // NOI18N
        Scroll17.setOpaque(true);

        tbTambahanBiaya1.setName("tbTambahanBiaya1"); // NOI18N
        Scroll17.setViewportView(tbTambahanBiaya1);

        FormInput.add(Scroll17);
        Scroll17.setBounds(16, 2000, 700, 123);

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
        
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        
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
        
    }//GEN-LAST:event_btnCariRadiologiActionPerformed

    private void cariRadiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariRadiologiKeyPressed
        
    }//GEN-LAST:event_cariRadiologiKeyPressed

    private void cariLabPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariLabPKKeyPressed
        
    }//GEN-LAST:event_cariLabPKKeyPressed

    private void btnCariLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabPKActionPerformed
        
    }//GEN-LAST:event_btnCariLabPKActionPerformed

    private void cariDetailLabPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariDetailLabPKKeyPressed
        
    }//GEN-LAST:event_cariDetailLabPKKeyPressed

    private void btnCariDetailLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDetailLabPKActionPerformed
        
    }//GEN-LAST:event_btnCariDetailLabPKActionPerformed

    private void cariLabPAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariLabPAKeyPressed
        
    }//GEN-LAST:event_cariLabPAKeyPressed

    private void btnCariLabPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabPAActionPerformed
        
    }//GEN-LAST:event_btnCariLabPAActionPerformed

    private void cariLabMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariLabMBKeyPressed
        
    }//GEN-LAST:event_cariLabMBKeyPressed

    private void btnCariLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabMBActionPerformed
        
    }//GEN-LAST:event_btnCariLabMBActionPerformed

    private void cariDetailLabMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariDetailLabMBKeyPressed
        
    }//GEN-LAST:event_cariDetailLabMBKeyPressed

    private void btnCariDetailLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDetailLabMBActionPerformed
        
    }//GEN-LAST:event_btnCariDetailLabMBActionPerformed

    private void cariTindakanDrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTindakanDrKeyPressed
        
    }//GEN-LAST:event_cariTindakanDrKeyPressed

    private void btnCariTindakanDrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanDrActionPerformed
        
    }//GEN-LAST:event_btnCariTindakanDrActionPerformed

    private void btnAllRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllRadiologiActionPerformed
        
    }//GEN-LAST:event_btnAllRadiologiActionPerformed

    private void btnAllLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabPKActionPerformed
        
    }//GEN-LAST:event_btnAllLabPKActionPerformed

    private void btnAllDetailLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllDetailLabPKActionPerformed
        
    }//GEN-LAST:event_btnAllDetailLabPKActionPerformed

    private void btnAllLabPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabPAActionPerformed
        
    }//GEN-LAST:event_btnAllLabPAActionPerformed

    private void btnAllLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabMBActionPerformed
        
    }//GEN-LAST:event_btnAllLabMBActionPerformed

    private void btnAllDetailLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllDetailLabMBActionPerformed
        
    }//GEN-LAST:event_btnAllDetailLabMBActionPerformed

    private void btnAllTindakanDrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanDrActionPerformed
        
    }//GEN-LAST:event_btnAllTindakanDrActionPerformed

    private void tbLabPKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLabPKMouseClicked
        
    }//GEN-LAST:event_tbLabPKMouseClicked

    private void tbLabMBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLabMBMouseClicked
        
    }//GEN-LAST:event_tbLabMBMouseClicked

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void ppSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppSemuaActionPerformed
        
    }//GEN-LAST:event_ppSemuaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
    }//GEN-LAST:event_formWindowOpened

    private void namaTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_namaTemplateKeyPressed
        
    }//GEN-LAST:event_namaTemplateKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void tambahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tambahanKeyPressed
        
    }//GEN-LAST:event_tambahanKeyPressed

    private void cariTindakanDrPrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTindakanDrPrKeyPressed
        
    }//GEN-LAST:event_cariTindakanDrPrKeyPressed

    private void btnCariTindakanDrPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanDrPrActionPerformed
        
    }//GEN-LAST:event_btnCariTindakanDrPrActionPerformed

    private void btnAllTindakanDrPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanDrPrActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllTindakanDrPrActionPerformed

    private void cariTindakanPrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTindakanPrKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariTindakanPrKeyPressed

    private void btnCariTindakanPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanPrActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariTindakanPrActionPerformed

    private void btnAllTindakanPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanPrActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllTindakanPrActionPerformed

    private void cariTambahanBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTambahanBiayaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariTambahanBiayaKeyPressed

    private void btnCariTambahanBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTambahanBiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariTambahanBiayaActionPerformed

    private void btnAllTambahanBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTambahanBiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllTambahanBiayaActionPerformed

    private void cariPotonganBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariPotonganBiayaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariPotonganBiayaKeyPressed

    private void btnCariPotonganBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPotonganBiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariPotonganBiayaActionPerformed

    private void btnAllPotonganBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllPotonganBiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllPotonganBiayaActionPerformed

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
    private widget.ScrollPane Scroll16;
    private widget.ScrollPane Scroll17;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.TextBox TCari;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Button btnAllDetailLabMB;
    private widget.Button btnAllDetailLabPK;
    private widget.Button btnAllLabMB;
    private widget.Button btnAllLabPA;
    private widget.Button btnAllLabPK;
    private widget.Button btnAllPotonganBiaya;
    private widget.Button btnAllRadiologi;
    private widget.Button btnAllTambahanBiaya;
    private widget.Button btnAllTindakanDr;
    private widget.Button btnAllTindakanDrPr;
    private widget.Button btnAllTindakanPr;
    private widget.Button btnCariDetailLabMB;
    private widget.Button btnCariDetailLabPK;
    private widget.Button btnCariLabMB;
    private widget.Button btnCariLabPA;
    private widget.Button btnCariLabPK;
    private widget.Button btnCariPotonganBiaya;
    private widget.Button btnCariRadiologi;
    private widget.Button btnCariTambahanBiaya;
    private widget.Button btnCariTindakanDr;
    private widget.Button btnCariTindakanDrPr;
    private widget.Button btnCariTindakanPr;
    public widget.TextBox cariDetailLabMB;
    public widget.TextBox cariDetailLabPK;
    public widget.TextBox cariLabMB;
    public widget.TextBox cariLabPA;
    public widget.TextBox cariLabPK;
    public widget.TextBox cariPotonganBiaya;
    public widget.TextBox cariRadiologi;
    public widget.TextBox cariTambahanBiaya;
    public widget.TextBox cariTindakanDr;
    public widget.TextBox cariTindakanDrPr;
    public widget.TextBox cariTindakanPr;
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
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.TextBox kodeJenisBayar;
    private widget.Label label10;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
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
    public widget.Table tbTambahanBiaya;
    public widget.Table tbTambahanBiaya1;
    private widget.Table tbTemplate;
    public widget.Table tbTindakanDr;
    public widget.Table tbTindakanDrPr;
    public widget.Table tbTindakanPr;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        
    }

    public void emptTeks() {
        
    }

    private void getData() {
        
    }

    public JTable getTable() {
        return tbTemplate;
    }

    public void isCek() {
        
    }

    public void setTampil() {
        
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
        
    }

    private void ganti() {
        
    }

    private void hapus() {
        
    }
}
