package bridginglis;

import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;

public final class DlgMappingPemeriksaanLab extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final DlgCariTindakanLab tindakanLab = new DlgCariTindakanLab(null, false);
    private final DlgCariTemplateLaboratorium templateLab = new DlgCariTemplateLaboratorium(null, false);
    private final DlgCariDataPemeriksaanLabLIS periksaLabLIS = new DlgCariDataPemeriksaanLabLIS(null, false);
    private int id_pemeriksaan = -1;
    private String satuan = "", vendor = "";

    /**
     * Creates new form DlgJnsPerawatanRalan
     *
     * @param parent
     * @param modal
     */
    public DlgMappingPemeriksaanLab(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        Object[] row = {"Kode Tindakan", "Nama Tindakan", "ID Template", "Nama Template", "ID", "Kode Periksa", "Nama Periksa", "Satuan", "Ktg. Periksa", "Vendor"};

        tabMode = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 4) {
                    return java.lang.Integer.class;
                }
                return java.lang.String.class;
            }
        };

        tbJnsPerawatan.setModel(tabMode);
        tbJnsPerawatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbJnsPerawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tbJnsPerawatan.getColumnModel().getColumn(0).setPreferredWidth(113);
        tbJnsPerawatan.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbJnsPerawatan.getColumnModel().getColumn(2).setPreferredWidth(66);
        tbJnsPerawatan.getColumnModel().getColumn(3).setPreferredWidth(114);
        tbJnsPerawatan.getColumnModel().getColumn(4).setMinWidth(0);
        tbJnsPerawatan.getColumnModel().getColumn(4).setMaxWidth(0);
        tbJnsPerawatan.getColumnModel().getColumn(5).setPreferredWidth(87);
        tbJnsPerawatan.getColumnModel().getColumn(6).setPreferredWidth(122);
        tbJnsPerawatan.getColumnModel().getColumn(7).setPreferredWidth(61);
        tbJnsPerawatan.getColumnModel().getColumn(8).setPreferredWidth(104);
        tbJnsPerawatan.getColumnModel().getColumn(9).setPreferredWidth(85);
        
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        
        tindakanLab.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    tindakanLab.dispose();
                }
            }
        });
        
        tindakanLab.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (tindakanLab.getTable().getSelectedRow() != -1) {
                    TKdJenisPrw.setText(tindakanLab.getTable().getValueAt(tindakanLab.getTable().getSelectedRow(), 0).toString());
                    TNmPerawatan.setText(tindakanLab.getTable().getValueAt(tindakanLab.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        
        templateLab.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    templateLab.dispose();
                }
            }
        });
        
        templateLab.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (templateLab.getTable().getSelectedRow() != -1) {
                    TIdTemplate.setText(templateLab.getTable().getValueAt(templateLab.getTable().getSelectedRow(), 0).toString());
                    TNamaTemplate.setText(templateLab.getTable().getValueAt(templateLab.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        
        periksaLabLIS.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    periksaLabLIS.dispose();
                }
            }
        });
        
        periksaLabLIS.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (periksaLabLIS.getTable().getSelectedRow() != -1) {
                    id_pemeriksaan = Integer.parseInt(periksaLabLIS.getTable().getValueAt(periksaLabLIS.getTable().getSelectedRow(), 0).toString());
                    TKodeLIS.setText(periksaLabLIS.getTable().getValueAt(periksaLabLIS.getTable().getSelectedRow(), 1).toString());
                    TNamaLIS.setText(periksaLabLIS.getTable().getValueAt(periksaLabLIS.getTable().getSelectedRow(), 2).toString());
                    TKtgLIS.setText(periksaLabLIS.getTable().getValueAt(periksaLabLIS.getTable().getSelectedRow(), 5).toString());
                    satuan = periksaLabLIS.getTable().getValueAt(periksaLabLIS.getTable().getSelectedRow(), 3).toString();
                    vendor = periksaLabLIS.getTable().getValueAt(periksaLabLIS.getTable().getSelectedRow(), 7).toString();
                }
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
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbJnsPerawatan = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        TNmPerawatan = new widget.TextBox();
        jLabel8 = new widget.Label();
        TNamaTemplate = new widget.TextBox();
        TIdTemplate = new widget.TextBox();
        TNamaLIS = new widget.TextBox();
        BtnJnsPerawatanLab = new widget.Button();
        TKodeLIS = new widget.TextBox();
        TKdJenisPrw = new widget.TextBox();
        BtnTemplateLab = new widget.Button();
        BtnPemeriksaanLabpk = new widget.Button();
        TKtgLIS = new widget.TextBox();
        ChkInput = new widget.CekBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Mapping Pemeriksaan LIS Adamlabs ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJnsPerawatan.setAutoCreateRowSorter(true);
        tbJnsPerawatan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbJnsPerawatan.setName("tbJnsPerawatan"); // NOI18N
        tbJnsPerawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbJnsPerawatanMouseClicked(evt);
            }
        });
        tbJnsPerawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbJnsPerawatanKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbJnsPerawatan);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
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
        jLabel7.setPreferredSize(new java.awt.Dimension(75, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 130));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 137));
        FormInput.setLayout(null);

        jLabel3.setText("Template Pemeriksaan SIMRS :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 12, 165, 23);

        TNmPerawatan.setEditable(false);
        TNmPerawatan.setHighlighter(null);
        TNmPerawatan.setName("TNmPerawatan"); // NOI18N
        FormInput.add(TNmPerawatan);
        TNmPerawatan.setBounds(320, 12, 381, 23);

        jLabel8.setText("Nama Pemeriksaan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(0, 72, 165, 23);

        TNamaTemplate.setEditable(false);
        TNamaTemplate.setHighlighter(null);
        TNamaTemplate.setName("TNamaTemplate"); // NOI18N
        FormInput.add(TNamaTemplate);
        TNamaTemplate.setBounds(267, 42, 434, 23);

        TIdTemplate.setEditable(false);
        TIdTemplate.setHighlighter(null);
        TIdTemplate.setName("TIdTemplate"); // NOI18N
        FormInput.add(TIdTemplate);
        TIdTemplate.setBounds(169, 42, 94, 23);

        TNamaLIS.setEditable(false);
        TNamaLIS.setName("TNamaLIS"); // NOI18N
        FormInput.add(TNamaLIS);
        TNamaLIS.setBounds(485, 72, 216, 23);

        BtnJnsPerawatanLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnJnsPerawatanLab.setMnemonic('2');
        BtnJnsPerawatanLab.setToolTipText("ALt+2");
        BtnJnsPerawatanLab.setName("BtnJnsPerawatanLab"); // NOI18N
        BtnJnsPerawatanLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnJnsPerawatanLabActionPerformed(evt);
            }
        });
        BtnJnsPerawatanLab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnJnsPerawatanLabKeyPressed(evt);
            }
        });
        FormInput.add(BtnJnsPerawatanLab);
        BtnJnsPerawatanLab.setBounds(705, 12, 28, 23);

        TKodeLIS.setEditable(false);
        TKodeLIS.setHighlighter(null);
        TKodeLIS.setName("TKodeLIS"); // NOI18N
        FormInput.add(TKodeLIS);
        TKodeLIS.setBounds(169, 72, 154, 23);

        TKdJenisPrw.setEditable(false);
        TKdJenisPrw.setHighlighter(null);
        TKdJenisPrw.setName("TKdJenisPrw"); // NOI18N
        FormInput.add(TKdJenisPrw);
        TKdJenisPrw.setBounds(169, 12, 147, 23);

        BtnTemplateLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnTemplateLab.setMnemonic('2');
        BtnTemplateLab.setToolTipText("ALt+2");
        BtnTemplateLab.setName("BtnTemplateLab"); // NOI18N
        BtnTemplateLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTemplateLabActionPerformed(evt);
            }
        });
        BtnTemplateLab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnTemplateLabKeyPressed(evt);
            }
        });
        FormInput.add(BtnTemplateLab);
        BtnTemplateLab.setBounds(705, 42, 28, 23);

        BtnPemeriksaanLabpk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPemeriksaanLabpk.setMnemonic('2');
        BtnPemeriksaanLabpk.setToolTipText("ALt+2");
        BtnPemeriksaanLabpk.setName("BtnPemeriksaanLabpk"); // NOI18N
        BtnPemeriksaanLabpk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPemeriksaanLabpkActionPerformed(evt);
            }
        });
        BtnPemeriksaanLabpk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPemeriksaanLabpkKeyPressed(evt);
            }
        });
        FormInput.add(BtnPemeriksaanLabpk);
        BtnPemeriksaanLabpk.setBounds(705, 72, 28, 23);

        TKtgLIS.setEditable(false);
        TKtgLIS.setHighlighter(null);
        TKtgLIS.setName("TKtgLIS"); // NOI18N
        FormInput.add(TKtgLIS);
        TKtgLIS.setBounds(327, 72, 154, 23);

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
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TKdJenisPrw.getText().isBlank() || TNmPerawatan.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Tindakan Lab terlebih dahulu...!!!");
            BtnJnsPerawatanLab.requestFocus();
        } else if (TIdTemplate.getText().isBlank() || TNamaTemplate.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Item Pemeriksaan terlebih dahulu...!!!");
            BtnTemplateLab.requestFocus();
        } else if (TKodeLIS.getText().isBlank() || TNamaLIS.getText().isBlank() || satuan.isBlank() || TKtgLIS.getText().isBlank() || id_pemeriksaan < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Item Pemeriksaan dari LIS terlebih dahulu...!!!");
            BtnPemeriksaanLabpk.requestFocus();
        } else {
            if (Sequel.menyimpantfSmc("mapping_pemeriksaan_labpk", null, String.valueOf(id_pemeriksaan), TIdTemplate.getText())) {
                tabMode.addRow(new Object[] {
                    TKdJenisPrw.getText(), TNmPerawatan.getText(), TIdTemplate.getText(), TNamaTemplate.getText(),
                    id_pemeriksaan, TKodeLIS.getText(), TNamaLIS.getText(), satuan, TKtgLIS.getText(), vendor
                });
                LCount.setText(String.valueOf(tabMode.getRowCount()));
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan Mapping Pemeriksaan Lab...!!!");
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (Valid.pindahSmc(evt, BtnBatal, BtnCari)) {
            BtnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (Valid.pindahSmc(evt, BtnHapus, BtnSimpan)) {
            emptTeks();
        }
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbJnsPerawatan.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tidak ada data yang bisa dihapus...!!!");
        } else if (tbJnsPerawatan.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!");
        } else {
            if (Sequel.menghapustfSmc(
                "mapping_pemeriksaan_labpk", "id_pemeriksaan = ? and id_template = ?",
                tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 4).toString(),
                tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 2).toString()
            )) {
                tabMode.removeRow(tbJnsPerawatan.getSelectedRow());
                LCount.setText(String.valueOf(tabMode.getRowCount()));
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus Mapping Pemeriksaan Lab!");
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (Valid.pindahSmc(evt, BtnEdit, BtnBatal)) {
            BtnHapusActionPerformed(null);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TKdJenisPrw.getText().isBlank() || TNmPerawatan.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Tindakan Lab terlebih dahulu...!!!");
            BtnJnsPerawatanLab.requestFocus();
        } else if (TIdTemplate.getText().isBlank() || TNamaTemplate.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Item Pemeriksaan terlebih dahulu...!!!");
            BtnTemplateLab.requestFocus();
        } else if (TKodeLIS.getText().isBlank() || TNamaLIS.getText().isBlank() || satuan.isBlank() || TKtgLIS.getText().isBlank() || id_pemeriksaan < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Item Pemeriksaan dari LIS terlebih dahulu...!!!");
            BtnPemeriksaanLabpk.requestFocus();
        } else {
            boolean sukses = true;
            if (Sequel.menghapustfSmc(
                "mapping_pemeriksaan_labpk", "id_pemeriksaan = ? and id_template = ?",
                tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 4).toString(),
                tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 2).toString()
            )) {
                tabMode.removeRow(tbJnsPerawatan.getSelectedRow());
                if (Sequel.menyimpantfSmc("mapping_pemeriksaan_labpk", null, String.valueOf(id_pemeriksaan), TIdTemplate.getText())) {
                    tabMode.addRow(new Object[] {
                        TKdJenisPrw.getText(), TNmPerawatan.getText(), TIdTemplate.getText(), TNamaTemplate.getText(),
                        id_pemeriksaan, TKodeLIS.getText(), TNamaLIS.getText(), satuan, TKtgLIS.getText(), vendor
                    });
                    emptTeks();
                } else {
                    sukses = false;
                }
            } else {
                sukses = false;
            }
            
            if (! sukses) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengganti Mapping Pemeriksaan Lab...!!!");
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (Valid.pindahSmc(evt, BtnPrint, BtnHapus)) {
            emptTeks();
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        templateLab.dispose();
        tindakanLab.dispose();
        periksaLabLIS.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (Valid.pindahSmc(evt, BtnJnsPerawatanLab, BtnAll)) {
            BtnKeluarActionPerformed(null);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (Valid.pindahSmc(evt, BtnAll, BtnEdit)) {
            BtnPrintActionPerformed(null);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (Valid.pindahSmc(evt, BtnCari, BtnPemeriksaanLabpk)) {
            tampil();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (Valid.pindahSmc(evt, BtnSimpan, TCari)) {
            tampil();
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (Valid.pindahSmc(evt, BtnKeluar, BtnPrint)) {
            BtnAllActionPerformed(null);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void tbJnsPerawatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJnsPerawatanKeyPressed

    }//GEN-LAST:event_tbJnsPerawatanKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void BtnJnsPerawatanLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnJnsPerawatanLabActionPerformed
        tindakanLab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        tindakanLab.setLocationRelativeTo(internalFrame1);
        tindakanLab.setVisible(true);
    }//GEN-LAST:event_BtnJnsPerawatanLabActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        templateLab.dispose();
        tindakanLab.dispose();
        periksaLabLIS.dispose();
    }//GEN-LAST:event_formWindowClosed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void BtnTemplateLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTemplateLabActionPerformed
        if (TKdJenisPrw.getText().isBlank() || TNmPerawatan.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih Tindakan Lab terlebih dahulu...!!!");
            BtnJnsPerawatanLab.requestFocus();
        } else {
            templateLab.setKodeTindakan(TKdJenisPrw.getText());
            templateLab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            templateLab.setLocationRelativeTo(internalFrame1);
            templateLab.setVisible(true);
        }
    }//GEN-LAST:event_BtnTemplateLabActionPerformed

    private void BtnPemeriksaanLabpkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPemeriksaanLabpkActionPerformed
        periksaLabLIS.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        periksaLabLIS.setLocationRelativeTo(internalFrame1);
        periksaLabLIS.setVisible(true);
    }//GEN-LAST:event_BtnPemeriksaanLabpkActionPerformed

    private void tbJnsPerawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbJnsPerawatanMouseClicked
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tidak ada data yang bisa dipilih!");
            tbJnsPerawatan.requestFocus();
            return;
        }
        getData();
    }//GEN-LAST:event_tbJnsPerawatanMouseClicked

    private void BtnPemeriksaanLabpkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPemeriksaanLabpkKeyPressed
        if (Valid.pindahSmc(evt, TCari, BtnTemplateLab)) {
            BtnPemeriksaanLabpkActionPerformed(null);
        }
    }//GEN-LAST:event_BtnPemeriksaanLabpkKeyPressed

    private void BtnTemplateLabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnTemplateLabKeyPressed
        if (Valid.pindahSmc(evt, BtnPemeriksaanLabpk, BtnJnsPerawatanLab)) {
            BtnTemplateLabActionPerformed(null);
        }
    }//GEN-LAST:event_BtnTemplateLabKeyPressed

    private void BtnJnsPerawatanLabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnJnsPerawatanLabKeyPressed
        if (Valid.pindahSmc(evt, BtnTemplateLab, BtnKeluar)) {
            BtnJnsPerawatanLabActionPerformed(null);
        }
    }//GEN-LAST:event_BtnJnsPerawatanLabKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgMappingPemeriksaanLab dialog = new DlgMappingPemeriksaanLab(new javax.swing.JFrame(), true);
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
    private widget.Button BtnJnsPerawatanLab;
    private widget.Button BtnKeluar;
    private widget.Button BtnPemeriksaanLabpk;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnTemplateLab;
    private widget.CekBox ChkInput;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TIdTemplate;
    private widget.TextBox TKdJenisPrw;
    private widget.TextBox TKodeLIS;
    private widget.TextBox TKtgLIS;
    private widget.TextBox TNamaLIS;
    private widget.TextBox TNamaTemplate;
    private widget.TextBox TNmPerawatan;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel3;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJnsPerawatan;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select template_laboratorium.kd_jenis_prw, jns_perawatan_lab.nm_perawatan, mapping_pemeriksaan_labpk.id_template, template_laboratorium.pemeriksaan, "
            + "pemeriksaan_labpk.id, pemeriksaan_labpk.kode_pemeriksaan, pemeriksaan_labpk.nama_pemeriksaan, pemeriksaan_labpk.satuan, pemeriksaan_labpk.kategori, "
            + "pemeriksaan_labpk.vendor from pemeriksaan_labpk join pemeriksaan_labpk_kategori on pemeriksaan_labpk.kategori = pemeriksaan_labpk_kategori.nama "
            + "join mapping_pemeriksaan_labpk on pemeriksaan_labpk.id = mapping_pemeriksaan_labpk.id_pemeriksaan join template_laboratorium on mapping_pemeriksaan_labpk.id_template = template_laboratorium.id_template "
            + "join jns_perawatan_lab on template_laboratorium.kd_jenis_prw = jns_perawatan_lab.kd_jenis_prw where jns_perawatan_lab.status = '1' and jns_perawatan_lab.kategori = 'PK' "
            + "and (pemeriksaan_labpk.vendor like ? or pemeriksaan_labpk.kode_pemeriksaan like ? or pemeriksaan_labpk.nama_pemeriksaan like ? or pemeriksaan_labpk.kategori like ? "
            + "or mapping_pemeriksaan_labpk.id_template like ? or template_laboratorium.pemeriksaan like ? or template_laboratorium.kd_jenis_prw like ? "
            + "or jns_perawatan_lab.nm_perawatan like ?) order by pemeriksaan_labpk_kategori.urut asc, pemeriksaan_labpk.urut asc"
        )) {
            ps.setString(1, "%" + TCari.getText() + "%");
            ps.setString(2, "%" + TCari.getText() + "%");
            ps.setString(3, "%" + TCari.getText() + "%");
            ps.setString(4, "%" + TCari.getText() + "%");
            ps.setString(5, "%" + TCari.getText() + "%");
            ps.setString(6, "%" + TCari.getText() + "%");
            ps.setString(7, "%" + TCari.getText() + "%");
            ps.setString(8, "%" + TCari.getText() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[] {
                        rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif " + e);
        }
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }

    public void emptTeks() {
        TKdJenisPrw.setText("");
        TNmPerawatan.setText("");
        TIdTemplate.setText("");
        TNamaTemplate.setText("");
        id_pemeriksaan = -1;
        TKodeLIS.setText("");
        TKtgLIS.setText("");
        TNamaLIS.setText("");
        satuan = "";
        vendor = "";
    }

    private void getData() {
        if (tbJnsPerawatan.getSelectedRow() != -1) {
            try {
                TKdJenisPrw.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 0).toString());
                TNmPerawatan.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 1).toString());
                TIdTemplate.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 2).toString());
                TNamaTemplate.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 3).toString());
                id_pemeriksaan = Integer.parseInt(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 4).toString());
                TKodeLIS.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 5).toString());
                TKtgLIS.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 8).toString());
                TNamaLIS.setText(tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 6).toString());
                satuan = tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 7).toString();
                vendor = tabMode.getValueAt(tbJnsPerawatan.getSelectedRow(), 9).toString();
            } catch (Exception e) {
            }
        }
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 130));
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
        
    }
}
