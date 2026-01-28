/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgObatPenyakit.java
 *
 * Created on May 23, 2010, 12:40:35 AM
 */
package id.yaski.simrskhanza.setting;

import id.yaski.simrskhanza.fungsi.WarnaTable;
import id.yaski.simrskhanza.fungsi.batasInput;
import id.yaski.simrskhanza.fungsi.koneksiDB;
import id.yaski.simrskhanza.fungsi.sekuel;
import id.yaski.simrskhanza.fungsi.validasi;
import id.yaski.simrskhanza.inventory.DlgCariJenis;
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
import javax.swing.table.DefaultTableModel;
import id.yaski.simrskhanza.simrskhanza.DlgCariBangsal;
import id.yaski.simrskhanza.simrskhanza.DlgCariCaraBayar;
import id.yaski.simrskhanza.simrskhanza.DlgCariPoli;

/**
 *
 * @author dosen
 */
public final class DlgSetTampilJenisObatResep extends javax.swing.JDialog {
    private final DefaultTableModel tabModeRalan, tabModeRanap;
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final DlgCariCaraBayar carabayar = new DlgCariCaraBayar(null, false);
    private final DlgCariJenis jenis = new DlgCariJenis(null, false);
    private final DlgCariPoli poli = new DlgCariPoli(null, false);
    private final DlgCariBangsal bangsal = new DlgCariBangsal(null, false);

    public DlgSetTampilJenisObatResep(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8, 1);
        setSize(885, 674);

        Object[] row = {"Kode Poliklinik", "Nama Poliklinik", "Kode Cara Bayar", "Nama Cara Bayar", "Kode Jenis Obat", "Nama Jenis Obat"},
                 row2 = {"Kode Unit Kamar", "Nama Unit Kamar", "Kode Cara Bayar", "Nama Cara Bayar", "Kode Jenis Obat", "Nama Jenis Obat"};

        tabModeRalan = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbRalan.setModel(tabModeRalan);
        tbRalan.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbRalan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRalan.getColumnModel().getColumn(0).setPreferredWidth(90);
        tbRalan.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbRalan.getColumnModel().getColumn(2).setPreferredWidth(90);
        tbRalan.getColumnModel().getColumn(3).setPreferredWidth(200);
        tbRalan.getColumnModel().getColumn(4).setPreferredWidth(90);
        tbRalan.getColumnModel().getColumn(5).setPreferredWidth(140);
        tbRalan.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRanap = new DefaultTableModel(null, row2) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbRanap.setModel(tabModeRanap);
        tbRanap.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbRanap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRanap.getColumnModel().getColumn(0).setPreferredWidth(90);
        tbRanap.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbRanap.getColumnModel().getColumn(2).setPreferredWidth(90);
        tbRanap.getColumnModel().getColumn(3).setPreferredWidth(200);
        tbRanap.getColumnModel().getColumn(4).setPreferredWidth(90);
        tbRanap.getColumnModel().getColumn(5).setPreferredWidth(140);
        tbRanap.setDefaultRenderer(Object.class, new WarnaTable());

        TCariRalan.setDocument(new batasInput((int) 100).getKata(TCariRalan));
        TCariRanap.setDocument(new batasInput((int) 100).getKata(TCariRanap));

        carabayar.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                if (carabayar.getTable().getSelectedRow() != -1) {
                    if (tabPane1.getSelectedIndex() == 0) {
                        kdpjRalan.setText(carabayar.getTable().getValueAt(carabayar.getTable().getSelectedRow(), 1).toString());
                        pngjawabRalan.setText(carabayar.getTable().getValueAt(carabayar.getTable().getSelectedRow(), 2).toString());
                    } else if (tabPane1.getSelectedIndex() == 1) {
                        kdpjRanap.setText(carabayar.getTable().getValueAt(carabayar.getTable().getSelectedRow(), 1).toString());
                        pngjawabRanap.setText(carabayar.getTable().getValueAt(carabayar.getTable().getSelectedRow(), 2).toString());
                    }
                }
            }
        });

        carabayar.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    carabayar.dispose();
                }
            }
        });

        jenis.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                if (jenis.getTable().getSelectedRow() != -1) {
                    if (tabPane1.getSelectedIndex() == 0) {
                        kdjnsRalan.setText(jenis.getTable().getValueAt(jenis.getTable().getSelectedRow(), 0).toString());
                        nmjnsRalan.setText(jenis.getTable().getValueAt(jenis.getTable().getSelectedRow(), 1).toString());
                    } else if (tabPane1.getSelectedIndex() == 1) {
                        kdjnsRanap.setText(jenis.getTable().getValueAt(jenis.getTable().getSelectedRow(), 0).toString());
                        nmjnsRanap.setText(jenis.getTable().getValueAt(jenis.getTable().getSelectedRow(), 1).toString());
                    }
                }
            }
        });

        jenis.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jenis.dispose();
                }
            }
        });

        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                if (poli.getTable().getSelectedRow() != -1) {
                    if (tabPane1.getSelectedIndex() == 0) {
                        kdpoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 0).toString());
                        nmpoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 1).toString());
                    }
                }
            }
        });

        poli.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    poli.dispose();
                }
            }
        });

        bangsal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                if (bangsal.getTable().getSelectedRow() != -1) {
                    if (tabPane1.getSelectedIndex() == 1) {
                        kdbangsal.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(), 0).toString());
                        nmbangsal.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(), 1).toString());
                    }
                }
            }
        });

        bangsal.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    bangsal.dispose();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new id.yaski.simrskhanza.widget.InternalFrame();
        tabPane1 = new id.yaski.simrskhanza.widget.TabPane();
        jPanel6 = new javax.swing.JPanel();
        panelGlass4 = new id.yaski.simrskhanza.widget.panelisi();
        jLabel8 = new id.yaski.simrskhanza.widget.Label();
        jLabel12 = new id.yaski.simrskhanza.widget.Label();
        pngjawabRalan = new id.yaski.simrskhanza.widget.TextBox();
        BtnPilihCaraBayarRalan = new id.yaski.simrskhanza.widget.Button();
        kdpjRalan = new id.yaski.simrskhanza.widget.TextBox();
        kdjnsRalan = new id.yaski.simrskhanza.widget.TextBox();
        nmjnsRalan = new id.yaski.simrskhanza.widget.TextBox();
        BtnPilihJenisObatRalan = new id.yaski.simrskhanza.widget.Button();
        jLabel13 = new id.yaski.simrskhanza.widget.Label();
        kdpoli = new id.yaski.simrskhanza.widget.TextBox();
        nmpoli = new id.yaski.simrskhanza.widget.TextBox();
        BtnPilihPoli = new id.yaski.simrskhanza.widget.Button();
        Scroll1 = new id.yaski.simrskhanza.widget.ScrollPane();
        tbRalan = new id.yaski.simrskhanza.widget.Table();
        panelGlass13 = new id.yaski.simrskhanza.widget.panelisi();
        jLabel9 = new id.yaski.simrskhanza.widget.Label();
        TCariRalan = new id.yaski.simrskhanza.widget.TextBox();
        BtnCariRalan = new id.yaski.simrskhanza.widget.Button();
        BtnAllRalan = new id.yaski.simrskhanza.widget.Button();
        jLabel11 = new id.yaski.simrskhanza.widget.Label();
        LCount1 = new id.yaski.simrskhanza.widget.Label();
        jPanel4 = new javax.swing.JPanel();
        panelGlass2 = new id.yaski.simrskhanza.widget.panelisi();
        jLabel3 = new id.yaski.simrskhanza.widget.Label();
        jLabel4 = new id.yaski.simrskhanza.widget.Label();
        pngjawabRanap = new id.yaski.simrskhanza.widget.TextBox();
        BtnPilihCaraBayarRanap = new id.yaski.simrskhanza.widget.Button();
        kdpjRanap = new id.yaski.simrskhanza.widget.TextBox();
        kdjnsRanap = new id.yaski.simrskhanza.widget.TextBox();
        nmjnsRanap = new id.yaski.simrskhanza.widget.TextBox();
        BtnPilihJenisObatRanap = new id.yaski.simrskhanza.widget.Button();
        jLabel14 = new id.yaski.simrskhanza.widget.Label();
        kdbangsal = new id.yaski.simrskhanza.widget.TextBox();
        nmbangsal = new id.yaski.simrskhanza.widget.TextBox();
        BtnPilihUnitKamar = new id.yaski.simrskhanza.widget.Button();
        Scroll2 = new id.yaski.simrskhanza.widget.ScrollPane();
        tbRanap = new id.yaski.simrskhanza.widget.Table();
        panelGlass11 = new id.yaski.simrskhanza.widget.panelisi();
        jLabel7 = new id.yaski.simrskhanza.widget.Label();
        TCariRanap = new id.yaski.simrskhanza.widget.TextBox();
        BtnCariRanap = new id.yaski.simrskhanza.widget.Button();
        BtnAllRanap = new id.yaski.simrskhanza.widget.Button();
        jLabel10 = new id.yaski.simrskhanza.widget.Label();
        LCountRanap = new id.yaski.simrskhanza.widget.Label();
        panelGlass8 = new id.yaski.simrskhanza.widget.panelisi();
        BtnSimpan = new id.yaski.simrskhanza.widget.Button();
        BtnBatal = new id.yaski.simrskhanza.widget.Button();
        BtnHapus = new id.yaski.simrskhanza.widget.Button();
        BtnEdit = new id.yaski.simrskhanza.widget.Button();
        BtnKeluar = new id.yaski.simrskhanza.widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModalExclusionType(null);
        setModalityType(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Setup Tampil Resep Obat, Alkes & BHP Medis per Jenis Obat per Poliklinik / Unit Rawat Inap]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        tabPane1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        tabPane1.setName("tabPane1"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setNextFocusableComponent(kdpoli);
        jPanel6.setOpaque(false);
        jPanel6.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel6.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass4.setName("panelGlass4"); // NOI18N
        panelGlass4.setPreferredSize(new java.awt.Dimension(711, 107));
        panelGlass4.setLayout(null);

        jLabel8.setText("Cara Bayar :");
        jLabel8.setName("jLabel8"); // NOI18N
        panelGlass4.add(jLabel8);
        jLabel8.setBounds(0, 42, 80, 23);

        jLabel12.setText("Jenis Obat :");
        jLabel12.setName("jLabel12"); // NOI18N
        panelGlass4.add(jLabel12);
        jLabel12.setBounds(0, 72, 80, 23);

        pngjawabRalan.setEditable(false);
        pngjawabRalan.setName("pngjawabRalan"); // NOI18N
        pngjawabRalan.setNextFocusableComponent(BtnPilihCaraBayarRalan);
        panelGlass4.add(pngjawabRalan);
        pngjawabRalan.setBounds(187, 42, 302, 23);

        BtnPilihCaraBayarRalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihCaraBayarRalan.setMnemonic('1');
        BtnPilihCaraBayarRalan.setToolTipText("Alt+1");
        BtnPilihCaraBayarRalan.setName("BtnPilihCaraBayarRalan"); // NOI18N
        BtnPilihCaraBayarRalan.setNextFocusableComponent(kdjnsRalan);
        BtnPilihCaraBayarRalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihCaraBayarRalanActionPerformed(evt);
            }
        });
        panelGlass4.add(BtnPilihCaraBayarRalan);
        BtnPilihCaraBayarRalan.setBounds(493, 42, 28, 23);

        kdpjRalan.setEditable(false);
        kdpjRalan.setName("kdpjRalan"); // NOI18N
        kdpjRalan.setNextFocusableComponent(pngjawabRalan);
        panelGlass4.add(kdpjRalan);
        kdpjRalan.setBounds(83, 42, 100, 23);

        kdjnsRalan.setEditable(false);
        kdjnsRalan.setName("kdjnsRalan"); // NOI18N
        kdjnsRalan.setNextFocusableComponent(nmjnsRalan);
        panelGlass4.add(kdjnsRalan);
        kdjnsRalan.setBounds(83, 72, 100, 23);

        nmjnsRalan.setEditable(false);
        nmjnsRalan.setName("nmjnsRalan"); // NOI18N
        nmjnsRalan.setNextFocusableComponent(BtnPilihJenisObatRalan);
        panelGlass4.add(nmjnsRalan);
        nmjnsRalan.setBounds(187, 72, 182, 23);

        BtnPilihJenisObatRalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihJenisObatRalan.setMnemonic('1');
        BtnPilihJenisObatRalan.setToolTipText("Alt+1");
        BtnPilihJenisObatRalan.setName("BtnPilihJenisObatRalan"); // NOI18N
        BtnPilihJenisObatRalan.setNextFocusableComponent(BtnSimpan);
        BtnPilihJenisObatRalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihJenisObatRalanActionPerformed(evt);
            }
        });
        panelGlass4.add(BtnPilihJenisObatRalan);
        BtnPilihJenisObatRalan.setBounds(373, 72, 28, 23);

        jLabel13.setText("Poliklinik :");
        jLabel13.setName("jLabel13"); // NOI18N
        panelGlass4.add(jLabel13);
        jLabel13.setBounds(0, 12, 80, 23);

        kdpoli.setEditable(false);
        kdpoli.setName("kdpoli"); // NOI18N
        kdpoli.setNextFocusableComponent(nmpoli);
        panelGlass4.add(kdpoli);
        kdpoli.setBounds(83, 12, 100, 23);

        nmpoli.setEditable(false);
        nmpoli.setName("nmpoli"); // NOI18N
        nmpoli.setNextFocusableComponent(BtnPilihPoli);
        panelGlass4.add(nmpoli);
        nmpoli.setBounds(187, 12, 302, 23);

        BtnPilihPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihPoli.setMnemonic('1');
        BtnPilihPoli.setToolTipText("Alt+1");
        BtnPilihPoli.setName("BtnPilihPoli"); // NOI18N
        BtnPilihPoli.setNextFocusableComponent(kdpjRalan);
        BtnPilihPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihPoliActionPerformed(evt);
            }
        });
        BtnPilihPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPilihPoliKeyPressed(evt);
            }
        });
        panelGlass4.add(BtnPilihPoli);
        BtnPilihPoli.setBounds(493, 12, 28, 23);

        jPanel6.add(panelGlass4, java.awt.BorderLayout.PAGE_START);

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbRalan.setAutoCreateRowSorter(true);
        tbRalan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRalan.setName("tbRalan"); // NOI18N
        tbRalan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRalanMouseClicked(evt);
            }
        });
        Scroll1.setViewportView(tbRalan);

        jPanel6.add(Scroll1, java.awt.BorderLayout.CENTER);

        panelGlass13.setName("panelGlass13"); // NOI18N
        panelGlass13.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jLabel9.setText("Key Word :");
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass13.add(jLabel9);

        TCariRalan.setName("TCariRalan"); // NOI18N
        TCariRalan.setNextFocusableComponent(BtnCariRalan);
        TCariRalan.setPreferredSize(new java.awt.Dimension(360, 23));
        TCariRalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariRalanKeyPressed(evt);
            }
        });
        panelGlass13.add(TCariRalan);

        BtnCariRalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariRalan.setMnemonic('5');
        BtnCariRalan.setToolTipText("Alt+5");
        BtnCariRalan.setName("BtnCariRalan"); // NOI18N
        BtnCariRalan.setNextFocusableComponent(BtnAllRalan);
        BtnCariRalan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariRalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariRalanActionPerformed(evt);
            }
        });
        BtnCariRalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariRalanKeyPressed(evt);
            }
        });
        panelGlass13.add(BtnCariRalan);

        BtnAllRalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllRalan.setMnemonic('M');
        BtnAllRalan.setToolTipText("Alt+M");
        BtnAllRalan.setName("BtnAllRalan"); // NOI18N
        BtnAllRalan.setNextFocusableComponent(jPanel6);
        BtnAllRalan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllRalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllRalanActionPerformed(evt);
            }
        });
        BtnAllRalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllRalanKeyPressed(evt);
            }
        });
        panelGlass13.add(BtnAllRalan);

        jLabel11.setText("Record :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass13.add(jLabel11);

        LCount1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount1.setText("0");
        LCount1.setName("LCount1"); // NOI18N
        LCount1.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass13.add(LCount1);

        jPanel6.add(panelGlass13, java.awt.BorderLayout.PAGE_END);

        tabPane1.addTab("Rawat Jalan", jPanel6);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setNextFocusableComponent(kdbangsal);
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel4.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass2.setName("panelGlass2"); // NOI18N
        panelGlass2.setPreferredSize(new java.awt.Dimension(711, 107));
        panelGlass2.setLayout(null);

        jLabel3.setText("Cara Bayar :");
        jLabel3.setName("jLabel3"); // NOI18N
        panelGlass2.add(jLabel3);
        jLabel3.setBounds(0, 42, 80, 23);

        jLabel4.setText("Jenis Obat :");
        jLabel4.setName("jLabel4"); // NOI18N
        panelGlass2.add(jLabel4);
        jLabel4.setBounds(0, 72, 80, 23);

        pngjawabRanap.setEditable(false);
        pngjawabRanap.setName("pngjawabRanap"); // NOI18N
        pngjawabRanap.setNextFocusableComponent(BtnPilihCaraBayarRanap);
        panelGlass2.add(pngjawabRanap);
        pngjawabRanap.setBounds(187, 42, 302, 23);

        BtnPilihCaraBayarRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihCaraBayarRanap.setMnemonic('1');
        BtnPilihCaraBayarRanap.setToolTipText("Alt+1");
        BtnPilihCaraBayarRanap.setName("BtnPilihCaraBayarRanap"); // NOI18N
        BtnPilihCaraBayarRanap.setNextFocusableComponent(kdjnsRanap);
        BtnPilihCaraBayarRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihCaraBayarRanapActionPerformed(evt);
            }
        });
        panelGlass2.add(BtnPilihCaraBayarRanap);
        BtnPilihCaraBayarRanap.setBounds(493, 42, 28, 23);

        kdpjRanap.setEditable(false);
        kdpjRanap.setName("kdpjRanap"); // NOI18N
        kdpjRanap.setNextFocusableComponent(pngjawabRanap);
        panelGlass2.add(kdpjRanap);
        kdpjRanap.setBounds(83, 42, 100, 23);

        kdjnsRanap.setEditable(false);
        kdjnsRanap.setName("kdjnsRanap"); // NOI18N
        kdjnsRanap.setNextFocusableComponent(nmjnsRanap);
        panelGlass2.add(kdjnsRanap);
        kdjnsRanap.setBounds(83, 72, 100, 23);

        nmjnsRanap.setEditable(false);
        nmjnsRanap.setName("nmjnsRanap"); // NOI18N
        nmjnsRanap.setNextFocusableComponent(BtnPilihJenisObatRanap);
        panelGlass2.add(nmjnsRanap);
        nmjnsRanap.setBounds(187, 72, 182, 23);

        BtnPilihJenisObatRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihJenisObatRanap.setMnemonic('1');
        BtnPilihJenisObatRanap.setToolTipText("Alt+1");
        BtnPilihJenisObatRanap.setName("BtnPilihJenisObatRanap"); // NOI18N
        BtnPilihJenisObatRanap.setNextFocusableComponent(BtnSimpan);
        BtnPilihJenisObatRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihJenisObatRanapActionPerformed(evt);
            }
        });
        panelGlass2.add(BtnPilihJenisObatRanap);
        BtnPilihJenisObatRanap.setBounds(373, 72, 28, 23);

        jLabel14.setText("Unit Kamar :");
        jLabel14.setName("jLabel14"); // NOI18N
        panelGlass2.add(jLabel14);
        jLabel14.setBounds(0, 12, 80, 23);

        kdbangsal.setEditable(false);
        kdbangsal.setName("kdbangsal"); // NOI18N
        kdbangsal.setNextFocusableComponent(nmbangsal);
        panelGlass2.add(kdbangsal);
        kdbangsal.setBounds(83, 12, 100, 23);

        nmbangsal.setEditable(false);
        nmbangsal.setName("nmbangsal"); // NOI18N
        nmbangsal.setNextFocusableComponent(BtnPilihUnitKamar);
        panelGlass2.add(nmbangsal);
        nmbangsal.setBounds(187, 12, 302, 23);

        BtnPilihUnitKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihUnitKamar.setMnemonic('1');
        BtnPilihUnitKamar.setToolTipText("Alt+1");
        BtnPilihUnitKamar.setName("BtnPilihUnitKamar"); // NOI18N
        BtnPilihUnitKamar.setNextFocusableComponent(kdpjRanap);
        BtnPilihUnitKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihUnitKamarActionPerformed(evt);
            }
        });
        BtnPilihUnitKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPilihUnitKamarKeyPressed(evt);
            }
        });
        panelGlass2.add(BtnPilihUnitKamar);
        BtnPilihUnitKamar.setBounds(493, 12, 28, 23);

        jPanel4.add(panelGlass2, java.awt.BorderLayout.PAGE_START);

        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbRanap.setAutoCreateRowSorter(true);
        tbRanap.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRanap.setName("tbRanap"); // NOI18N
        tbRanap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRanapMouseClicked(evt);
            }
        });
        Scroll2.setViewportView(tbRanap);

        jPanel4.add(Scroll2, java.awt.BorderLayout.CENTER);

        panelGlass11.setName("panelGlass11"); // NOI18N
        panelGlass11.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jLabel7.setText("Key Word :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass11.add(jLabel7);

        TCariRanap.setName("TCariRanap"); // NOI18N
        TCariRanap.setPreferredSize(new java.awt.Dimension(360, 23));
        TCariRanap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariRanapKeyPressed(evt);
            }
        });
        panelGlass11.add(TCariRanap);

        BtnCariRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariRanap.setMnemonic('5');
        BtnCariRanap.setToolTipText("Alt+5");
        BtnCariRanap.setName("BtnCariRanap"); // NOI18N
        BtnCariRanap.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariRanapActionPerformed(evt);
            }
        });
        BtnCariRanap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariRanapKeyPressed(evt);
            }
        });
        panelGlass11.add(BtnCariRanap);

        BtnAllRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllRanap.setMnemonic('M');
        BtnAllRanap.setToolTipText("Alt+M");
        BtnAllRanap.setName("BtnAllRanap"); // NOI18N
        BtnAllRanap.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllRanapActionPerformed(evt);
            }
        });
        BtnAllRanap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllRanapKeyPressed(evt);
            }
        });
        panelGlass11.add(BtnAllRanap);

        jLabel10.setText("Record :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass11.add(jLabel10);

        LCountRanap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCountRanap.setText("0");
        LCountRanap.setName("LCountRanap"); // NOI18N
        LCountRanap.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass11.add(LCountRanap);

        jPanel4.add(panelGlass11, java.awt.BorderLayout.PAGE_END);

        tabPane1.addTab("Rawat Inap", jPanel4);

        internalFrame1.add(tabPane1, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setNextFocusableComponent(BtnBatal);
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
        BtnBatal.setNextFocusableComponent(BtnHapus);
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
        BtnHapus.setNextFocusableComponent(BtnKeluar);
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

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setNextFocusableComponent(tabPane1);
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyReleased(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (tabPane1.getSelectedIndex() == 0) {
            if (kdpoli.getText().isBlank() || nmpoli.getText().isBlank()) {
                Valid.textKosong(kdpoli, "Poliklinik");
            } else if (kdpjRalan.getText().isBlank() || pngjawabRalan.getText().isBlank()) {
                Valid.textKosong(kdpjRalan, "Cara Bayar");
            } else if (kdjnsRalan.getText().isBlank() || nmjnsRalan.getText().isBlank()) {
                Valid.textKosong(kdjnsRalan, "Jenis Obat");
            } else {
                if (Sequel.menyimpantfSmc("set_filter_jenis_resep_obat_ralan", "", kdpoli.getText(), kdpjRalan.getText(), kdjnsRalan.getText())) {
                    tabModeRalan.addRow(new String[] {
                        kdpoli.getText(), nmpoli.getText(), kdpjRalan.getText(), pngjawabRalan.getText(), kdjnsRalan.getText(), nmjnsRalan.getText()
                    });
                    emptTeks();
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan Filter Jenis Obat Rawat Jalan..!!");
                }
            }
        } else if (tabPane1.getSelectedIndex() == 1) {
            if (kdbangsal.getText().isBlank() || nmbangsal.getText().isBlank()) {
                Valid.textKosong(kdbangsal, "Unit Kamar Inap");
            } else if (kdpjRanap.getText().isBlank() || pngjawabRanap.getText().isBlank()) {
                Valid.textKosong(kdpjRanap, "Cara Bayar");
            } else if (kdjnsRanap.getText().isBlank() || nmjnsRanap.getText().isBlank()) {
                Valid.textKosong(kdjnsRanap, "Jenis Obat");
            } else {
                if (Sequel.menyimpantfSmc("set_filter_jenis_resep_obat_ranap", "", kdbangsal.getText(), kdpjRanap.getText(), kdjnsRanap.getText())) {
                    tabModeRanap.addRow(new String[] {
                        kdbangsal.getText(), nmbangsal.getText(), kdpjRanap.getText(), pngjawabRanap.getText(), kdjnsRanap.getText(), nmjnsRanap.getText()
                    });
                    emptTeks();
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan Filter Jenis Obat Rawat Inap..!!");
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tabPane1.getSelectedIndex() == 0) {
            if (tbRalan.getSelectedRow() != -1) {
                if (Sequel.menghapustfSmc("set_filter_jenis_resep_obat_ralan", "kd_poli = ? and kd_pj = ? and kdjns = ?",
                    tbRalan.getValueAt(tbRalan.getSelectedRow(), 0).toString(),
                    tbRalan.getValueAt(tbRalan.getSelectedRow(), 2).toString(),
                    tbRalan.getValueAt(tbRalan.getSelectedRow(), 4).toString())
                ) {
                    tabModeRalan.removeRow(tbRalan.getSelectedRow());
                    emptTeks();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Silahkan pilih data yang mau dihapus..!!");
            }
        } else if (tabPane1.getSelectedIndex() == 1) {
            if (tbRanap.getSelectedRow() != -1) {
                if (Sequel.menghapustfSmc("set_filter_jenis_resep_obat_ranap", "kd_bangsal = ? and kd_pj = ? and kdjns = ?",
                    tbRanap.getValueAt(tbRanap.getSelectedRow(), 0).toString(),
                    tbRanap.getValueAt(tbRanap.getSelectedRow(), 2).toString(),
                    tbRanap.getValueAt(tbRanap.getSelectedRow(), 4).toString())
                ) {
                    tabModeRanap.removeRow(tbRanap.getSelectedRow());
                    emptTeks();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Silahkan pilih data yang mau dihapus..!!");
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnPilihCaraBayarRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihCaraBayarRanapActionPerformed
        tampilCaraBayar();
    }//GEN-LAST:event_BtnPilihCaraBayarRanapActionPerformed

    private void tbRanapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRanapMouseClicked
        if (tabModeRanap.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {}
        }
    }//GEN-LAST:event_tbRanapMouseClicked

    private void tbRalanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRalanMouseClicked
        if (tabModeRalan.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {}
        }
    }//GEN-LAST:event_tbRalanMouseClicked

    private void BtnPilihJenisObatRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihJenisObatRanapActionPerformed
        tampilJenis();
    }//GEN-LAST:event_BtnPilihJenisObatRanapActionPerformed

    private void TCariRanapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariRanapKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariRanapActionPerformed(null);
        }
    }//GEN-LAST:event_TCariRanapKeyPressed

    private void BtnCariRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariRanapActionPerformed
        tampil2();
    }//GEN-LAST:event_BtnCariRanapActionPerformed

    private void BtnCariRanapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariRanapKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariRanapActionPerformed(null);
        }
    }//GEN-LAST:event_BtnCariRanapKeyPressed

    private void BtnAllRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllRanapActionPerformed
        emptTeks();
        BtnCariRanapActionPerformed(null);
    }//GEN-LAST:event_BtnAllRanapActionPerformed

    private void BtnAllRanapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllRanapKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE || evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnAllRanapActionPerformed(null);
        }
    }//GEN-LAST:event_BtnAllRanapKeyPressed

    private void TCariRalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariRalanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariRalanActionPerformed(null);
        }
    }//GEN-LAST:event_TCariRalanKeyPressed

    private void BtnCariRalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariRalanActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariRalanActionPerformed

    private void BtnCariRalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariRalanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE || evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariRalanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnCariRalanKeyPressed

    private void BtnAllRalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllRalanActionPerformed
        emptTeks();
        BtnCariRalanActionPerformed(null);
    }//GEN-LAST:event_BtnAllRalanActionPerformed

    private void BtnAllRalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllRalanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE || evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnAllRalanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnAllRalanKeyPressed

    private void BtnPilihCaraBayarRalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihCaraBayarRalanActionPerformed
        tampilCaraBayar();
    }//GEN-LAST:event_BtnPilihCaraBayarRalanActionPerformed

    private void BtnPilihJenisObatRalanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihJenisObatRalanActionPerformed
        tampilJenis();
    }//GEN-LAST:event_BtnPilihJenisObatRalanActionPerformed

    private void BtnPilihPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihPoliActionPerformed
        poli.isCek();
        poli.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        poli.setLocationRelativeTo(internalFrame1);
        poli.setAlwaysOnTop(false);
        poli.setVisible(true);
    }//GEN-LAST:event_BtnPilihPoliActionPerformed

    private void BtnPilihUnitKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPilihUnitKamarActionPerformed
        bangsal.isCek();
        bangsal.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        bangsal.setLocationRelativeTo(internalFrame1);
        bangsal.setAlwaysOnTop(false);
        bangsal.setVisible(true);
    }//GEN-LAST:event_BtnPilihUnitKamarActionPerformed

    private void BtnKeluarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyReleased
        carabayar.dispose();
        jenis.dispose();
        poli.dispose();
        bangsal.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarKeyReleased

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnBatalActionPerformed(null);
        }
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnPilihPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPilihPoliKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPilihPoliActionPerformed(null);
        }
    }//GEN-LAST:event_BtnPilihPoliKeyPressed

    private void BtnPilihUnitKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPilihUnitKamarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPilihPoliActionPerformed(null);
        }
    }//GEN-LAST:event_BtnPilihUnitKamarKeyPressed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (tabPane1.getSelectedIndex() == 0) {
            if (kdpoli.getText().isBlank() || nmpoli.getText().isBlank()) {
                Valid.textKosong(kdpoli, "Poliklinik");
            } else if (kdpjRalan.getText().isBlank() || pngjawabRalan.getText().isBlank()) {
                Valid.textKosong(kdpjRalan, "Cara Bayar");
            } else if (kdjnsRalan.getText().isBlank() || nmjnsRalan.getText().isBlank()) {
                Valid.textKosong(kdjnsRalan, "Jenis Obat");
            } else {
                if (tbRalan.getSelectedRow() != -1) {
                    if (Sequel.mengupdatetfSmc("set_filter_jenis_resep_obat_ralan",
                        "kd_poli = ?, kd_pj = ?, kdjns = ?", "kd_poli = ? and kd_pj = ? and kdjns = ?",
                        kdpoli.getText(), kdpjRalan.getText(), kdjnsRalan.getText(),
                        tbRalan.getValueAt(tbRalan.getSelectedRow(), 0).toString(),
                        tbRalan.getValueAt(tbRalan.getSelectedRow(), 2).toString(),
                        tbRalan.getValueAt(tbRalan.getSelectedRow(), 4).toString()
                    )) {
                        tbRalan.setValueAt(kdpoli.getText(), tbRalan.getSelectedRow(), 0);
                        tbRalan.setValueAt(nmpoli.getText(), tbRalan.getSelectedRow(), 1);
                        tbRalan.setValueAt(kdpjRalan.getText(), tbRalan.getSelectedRow(), 2);
                        tbRalan.setValueAt(pngjawabRalan.getText(), tbRalan.getSelectedRow(), 3);
                        tbRalan.setValueAt(kdjnsRalan.getText(), tbRalan.getSelectedRow(), 4);
                        tbRalan.setValueAt(nmjnsRalan.getText(), tbRalan.getSelectedRow(), 5);
                        emptTeks();
                    } else {
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah data..!!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Maat, silahkan pilih data yang mau diubah terlebih dahulu..!!");
                }
            }
        } else if (tabPane1.getSelectedIndex() == 1) {
            if (kdbangsal.getText().isBlank() || nmbangsal.getText().isBlank()) {
                Valid.textKosong(kdbangsal, "Unit Kamar Inap");
            } else if (kdpjRanap.getText().isBlank() || pngjawabRanap.getText().isBlank()) {
                Valid.textKosong(kdpjRanap, "Cara Bayar");
            } else if (kdjnsRanap.getText().isBlank() || nmjnsRanap.getText().isBlank()) {
                Valid.textKosong(kdjnsRanap, "Jenis Obat");
            } else {
                if (tbRanap.getSelectedRow() != -1) {
                    if (Sequel.mengupdatetfSmc("set_filter_jenis_resep_obat_ranap",
                        "kd_bangsal = ?, kd_pj = ?, kdjns = ?", "kd_bangsal = ? and kd_pj = ? and kdjns = ?",
                        kdbangsal.getText(), kdpjRanap.getText(), kdjnsRanap.getText(),
                        tbRanap.getValueAt(tbRanap.getSelectedRow(), 0).toString(),
                        tbRanap.getValueAt(tbRanap.getSelectedRow(), 2).toString(),
                        tbRanap.getValueAt(tbRanap.getSelectedRow(), 4).toString()
                    )) {
                        tbRanap.setValueAt(kdbangsal.getText(), tbRanap.getSelectedRow(), 0);
                        tbRanap.setValueAt(nmbangsal.getText(), tbRanap.getSelectedRow(), 1);
                        tbRanap.setValueAt(kdpjRanap.getText(), tbRanap.getSelectedRow(), 2);
                        tbRanap.setValueAt(pngjawabRanap.getText(), tbRanap.getSelectedRow(), 3);
                        tbRanap.setValueAt(kdjnsRanap.getText(), tbRanap.getSelectedRow(), 4);
                        tbRanap.setValueAt(nmjnsRanap.getText(), tbRanap.getSelectedRow(), 5);
                        emptTeks();
                    } else {
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah data..!!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Maat, silahkan pilih data yang mau diubah terlebih dahulu..!!");
                }
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgSetTampilJenisObatResep dialog = new DlgSetTampilJenisObatResep(new javax.swing.JFrame(), true);
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
    private id.yaski.simrskhanza.widget.Button BtnAllRalan;
    private id.yaski.simrskhanza.widget.Button BtnAllRanap;
    private id.yaski.simrskhanza.widget.Button BtnBatal;
    private id.yaski.simrskhanza.widget.Button BtnCariRalan;
    private id.yaski.simrskhanza.widget.Button BtnCariRanap;
    private id.yaski.simrskhanza.widget.Button BtnEdit;
    private id.yaski.simrskhanza.widget.Button BtnHapus;
    private id.yaski.simrskhanza.widget.Button BtnKeluar;
    private id.yaski.simrskhanza.widget.Button BtnPilihCaraBayarRalan;
    private id.yaski.simrskhanza.widget.Button BtnPilihCaraBayarRanap;
    private id.yaski.simrskhanza.widget.Button BtnPilihJenisObatRalan;
    private id.yaski.simrskhanza.widget.Button BtnPilihJenisObatRanap;
    private id.yaski.simrskhanza.widget.Button BtnPilihPoli;
    private id.yaski.simrskhanza.widget.Button BtnPilihUnitKamar;
    private id.yaski.simrskhanza.widget.Button BtnSimpan;
    private id.yaski.simrskhanza.widget.Label LCount1;
    private id.yaski.simrskhanza.widget.Label LCountRanap;
    private id.yaski.simrskhanza.widget.ScrollPane Scroll1;
    private id.yaski.simrskhanza.widget.ScrollPane Scroll2;
    private id.yaski.simrskhanza.widget.TextBox TCariRalan;
    private id.yaski.simrskhanza.widget.TextBox TCariRanap;
    private id.yaski.simrskhanza.widget.InternalFrame internalFrame1;
    private id.yaski.simrskhanza.widget.Label jLabel10;
    private id.yaski.simrskhanza.widget.Label jLabel11;
    private id.yaski.simrskhanza.widget.Label jLabel12;
    private id.yaski.simrskhanza.widget.Label jLabel13;
    private id.yaski.simrskhanza.widget.Label jLabel14;
    private id.yaski.simrskhanza.widget.Label jLabel3;
    private id.yaski.simrskhanza.widget.Label jLabel4;
    private id.yaski.simrskhanza.widget.Label jLabel7;
    private id.yaski.simrskhanza.widget.Label jLabel8;
    private id.yaski.simrskhanza.widget.Label jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private id.yaski.simrskhanza.widget.TextBox kdbangsal;
    private id.yaski.simrskhanza.widget.TextBox kdjnsRalan;
    private id.yaski.simrskhanza.widget.TextBox kdjnsRanap;
    private id.yaski.simrskhanza.widget.TextBox kdpjRalan;
    private id.yaski.simrskhanza.widget.TextBox kdpjRanap;
    private id.yaski.simrskhanza.widget.TextBox kdpoli;
    private id.yaski.simrskhanza.widget.TextBox nmbangsal;
    private id.yaski.simrskhanza.widget.TextBox nmjnsRalan;
    private id.yaski.simrskhanza.widget.TextBox nmjnsRanap;
    private id.yaski.simrskhanza.widget.TextBox nmpoli;
    private id.yaski.simrskhanza.widget.panelisi panelGlass11;
    private id.yaski.simrskhanza.widget.panelisi panelGlass13;
    private id.yaski.simrskhanza.widget.panelisi panelGlass2;
    private id.yaski.simrskhanza.widget.panelisi panelGlass4;
    private id.yaski.simrskhanza.widget.panelisi panelGlass8;
    private id.yaski.simrskhanza.widget.TextBox pngjawabRalan;
    private id.yaski.simrskhanza.widget.TextBox pngjawabRanap;
    private id.yaski.simrskhanza.widget.TabPane tabPane1;
    private id.yaski.simrskhanza.widget.Table tbRalan;
    private id.yaski.simrskhanza.widget.Table tbRanap;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabModeRalan);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select set_filter_jenis_resep_obat_ralan.kd_poli, poliklinik.nm_poli, set_filter_jenis_resep_obat_ralan.kd_pj, penjab.png_jawab, " +
            "set_filter_jenis_resep_obat_ralan.kdjns, jenis.nama from set_filter_jenis_resep_obat_ralan join poliklinik on " +
            "set_filter_jenis_resep_obat_ralan.kd_poli = poliklinik.kd_poli join penjab on set_filter_jenis_resep_obat_ralan.kd_pj = " +
            "penjab.kd_pj join jenis on set_filter_jenis_resep_obat_ralan.kdjns = jenis.kdjns " + (TCariRalan.getText().isBlank() ? "" :
            "where set_filter_jenis_resep_obat_ralan.kd_poli like ? or poliklinik.nm_poli like ? or set_filter_jenis_resep_obat_ralan.kd_pj " +
            "like ? or penjab.png_jawab like ? or set_filter_jenis_resep_obat_ralan.kdjns like ? or jenis.nama like ?")
        )) {
            if (!TCariRalan.getText().isBlank()) {
                ps.setString(1, "%" + TCariRalan.getText() + "%");
                ps.setString(2, "%" + TCariRalan.getText() + "%");
                ps.setString(3, "%" + TCariRalan.getText() + "%");
                ps.setString(4, "%" + TCariRalan.getText() + "%");
                ps.setString(5, "%" + TCariRalan.getText() + "%");
                ps.setString(6, "%" + TCariRalan.getText() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeRalan.addRow(new String[] {
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampil2() {
        Valid.tabelKosong(tabModeRanap);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select set_filter_jenis_resep_obat_ranap.kd_bangsal, bangsal.nm_bangsal, set_filter_jenis_resep_obat_ranap.kd_pj, penjab.png_jawab, " +
            "set_filter_jenis_resep_obat_ranap.kdjns, jenis.nama from set_filter_jenis_resep_obat_ranap join bangsal on " +
            "set_filter_jenis_resep_obat_ranap.kd_bangsal = bangsal.kd_bangsal join penjab on set_filter_jenis_resep_obat_ranap.kd_pj = " +
            "penjab.kd_pj join jenis on set_filter_jenis_resep_obat_ranap.kdjns = jenis.kdjns " + (TCariRalan.getText().isBlank() ? "" :
            "where set_filter_jenis_resep_obat_ranap.kd_bangsal like ? or bangsal.nm_bangsal like ? or set_filter_jenis_resep_obat_ranap.kd_pj " +
            "like ? or penjab.png_jawab like ? or set_filter_jenis_resep_obat_ranap.kdjns like ? or jenis.nama like ?")
        )) {
            if (!TCariRanap.getText().isBlank()) {
                ps.setString(1, "%" + TCariRanap.getText() + "%");
                ps.setString(2, "%" + TCariRanap.getText() + "%");
                ps.setString(3, "%" + TCariRanap.getText() + "%");
                ps.setString(4, "%" + TCariRanap.getText() + "%");
                ps.setString(5, "%" + TCariRanap.getText() + "%");
                ps.setString(6, "%" + TCariRanap.getText() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeRanap.addRow(new String[] {
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void emptTeks() {
        TCariRalan.setText("");
        TCariRanap.setText("");
        kdpoli.setText("");
        nmpoli.setText("");
        kdpjRalan.setText("");
        pngjawabRalan.setText("");
        kdjnsRalan.setText("");
        nmjnsRalan.setText("");
        kdbangsal.setText("");
        nmbangsal.setText("");
        kdpjRanap.setText("");
        pngjawabRanap.setText("");
        kdjnsRanap.setText("");
        nmjnsRanap.setText("");
        tbRalan.clearSelection();
        tbRanap.clearSelection();
    }

    private void getData() {
        if (tabPane1.getSelectedIndex() == 0) {
            if (tbRalan.getSelectedRow() != -1) {
                kdpoli.setText(tbRalan.getValueAt(tbRalan.getSelectedRow(), 0).toString());
                nmpoli.setText(tbRalan.getValueAt(tbRalan.getSelectedRow(), 1).toString());
                kdpjRalan.setText(tbRalan.getValueAt(tbRalan.getSelectedRow(), 2).toString());
                pngjawabRalan.setText(tbRalan.getValueAt(tbRalan.getSelectedRow(), 3).toString());
                kdjnsRalan.setText(tbRalan.getValueAt(tbRalan.getSelectedRow(), 4).toString());
                nmjnsRalan.setText(tbRalan.getValueAt(tbRalan.getSelectedRow(), 5).toString());
            }
        } else if (tabPane1.getSelectedIndex() == 1) {
            if (tbRanap.getSelectedRow() != -1) {
                kdbangsal.setText(tbRanap.getValueAt(tbRanap.getSelectedRow(), 0).toString());
                nmbangsal.setText(tbRanap.getValueAt(tbRanap.getSelectedRow(), 1).toString());
                kdpjRanap.setText(tbRanap.getValueAt(tbRanap.getSelectedRow(), 2).toString());
                pngjawabRanap.setText(tbRanap.getValueAt(tbRanap.getSelectedRow(), 3).toString());
                kdjnsRanap.setText(tbRanap.getValueAt(tbRanap.getSelectedRow(), 4).toString());
                nmjnsRanap.setText(tbRanap.getValueAt(tbRanap.getSelectedRow(), 5).toString());
            }
        }
    }

    private void tampilCaraBayar() {
        carabayar.isCek();
        carabayar.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        carabayar.setLocationRelativeTo(internalFrame1);
        carabayar.setAlwaysOnTop(false);
        carabayar.setVisible(true);
    }

    private void tampilJenis() {
        jenis.isCek();
        jenis.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        jenis.setLocationRelativeTo(internalFrame1);
        jenis.setAlwaysOnTop(false);
        jenis.setVisible(true);
    }
}
