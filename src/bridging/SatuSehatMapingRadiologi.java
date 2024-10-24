/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bridging;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.DlgJnsPerawatanRadiologi;

/**
 *
 * @author dosen
 */
public final class SatuSehatMapingRadiologi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;    
    private int i=0;
    private final DlgJnsPerawatanRadiologi pemeriksaan=new DlgJnsPerawatanRadiologi(null,false);
    private final SatuSehatReferensiRadiologiLOINC refPeriksa = new SatuSehatReferensiRadiologiLOINC(null, false);
    private final SatuSehatReferensiRadiologiSNOMED refSampel = new SatuSehatReferensiRadiologiSNOMED(null, false);

    /** Creates new form DlgJnsPerawatanRalan
     * @param parent
     * @param modal */
    public SatuSehatMapingRadiologi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
                "Periksa Code","Pemeriksaan System","Kode Periksa","Nama Pemeriksaan","Pemeriksaan Display",
                "Sampel Code","Sampel System","Sampel Display"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbJnsPerawatan.setModel(tabMode);

        tbJnsPerawatan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbJnsPerawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 8; i++) {
            TableColumn column = tbJnsPerawatan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(80);
            }else if(i==1){
                column.setPreferredWidth(200);
            }else if(i==2){
                column.setPreferredWidth(85);
            }else if(i==3){
                column.setPreferredWidth(200);
            }else if(i==4){
                column.setPreferredWidth(200);
            }else if(i==5){
                column.setPreferredWidth(85);
            }else if(i==6){
                column.setPreferredWidth(200);
            }else if(i==7){
                column.setPreferredWidth(200);
            }
        }
        tbJnsPerawatan.setDefaultRenderer(Object.class, new WarnaTable());

        KodePemeriksaan.setDocument(new batasInput((byte)15).getKata(KodePemeriksaan)); 
        PeriksaCode.setDocument(new batasInput((byte)15).getKata(PeriksaCode)); 
        PeriksaSystem.setDocument(new batasInput((byte)100).getKata(PeriksaSystem)); 
        PeriksaDisplay.setDocument(new batasInput((byte)80).getKata(PeriksaDisplay)); 
        SampelCode.setDocument(new batasInput((byte)15).getKata(SampelCode)); 
        SampelSystem.setDocument(new batasInput((byte)100).getKata(SampelSystem)); 
        SampelDisplay.setDocument(new batasInput((byte)80).getKata(SampelDisplay)); 
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));                  
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }  
        
        pemeriksaan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (pemeriksaan.getTable().getSelectedRow() != -1) {
                    KodePemeriksaan.setText(pemeriksaan.getTable().getValueAt(pemeriksaan.getTable().getSelectedRow(), 1).toString());
                    NamaPemeriksaan.setText(pemeriksaan.getTable().getValueAt(pemeriksaan.getTable().getSelectedRow(), 2).toString());
                }
                BtnCariTindakanRadiologi.requestFocus();
            }
        });

        pemeriksaan.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    pemeriksaan.dispose();
                }
            }
        });
        
        refPeriksa.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (refPeriksa.getTable().getSelectedRow() != -1) {
                    PeriksaCode.setText(refPeriksa.getTable().getValueAt(refPeriksa.getTable().getSelectedRow(), 0).toString());
                    PeriksaSystem.setText(refPeriksa.getTable().getValueAt(refPeriksa.getTable().getSelectedRow(), 1).toString());
                    PeriksaDisplay.setText(refPeriksa.getTable().getValueAt(refPeriksa.getTable().getSelectedRow(), 2).toString());
                }
                BtnCariReferensiMapping.requestFocus();
            }
        });

        refPeriksa.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    refPeriksa.dispose();
                }
            }
        });
        
        refSampel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (refSampel.getTable().getSelectedRow() != -1) {
                    SampelCode.setText(refSampel.getTable().getValueAt(refSampel.getTable().getSelectedRow(), 0).toString());
                    SampelSystem.setText(refSampel.getTable().getValueAt(refSampel.getTable().getSelectedRow(), 1).toString());
                    SampelDisplay.setText(refSampel.getTable().getValueAt(refSampel.getTable().getSelectedRow(), 2).toString());
                }
                BtnCariReferensiSampel.requestFocus();
            }
        });

        refSampel.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    refSampel.dispose();
                }
            }
        });
        
        ChkInput.setSelected(false);
        isForm();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        KodePemeriksaan = new widget.TextBox();
        BtnCariTindakanRadiologi = new widget.Button();
        PeriksaCode = new widget.TextBox();
        jLabel9 = new widget.Label();
        jLabel10 = new widget.Label();
        PeriksaDisplay = new widget.TextBox();
        PeriksaSystem = new widget.TextBox();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        SampelCode = new widget.TextBox();
        SampelDisplay = new widget.TextBox();
        jLabel5 = new widget.Label();
        SampelSystem = new widget.TextBox();
        NamaPemeriksaan = new widget.TextBox();
        jLabel13 = new widget.Label();
        BtnCariReferensiMapping = new widget.Button();
        BtnCariReferensiSampel = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Mapping Tindakan Radiologi Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJnsPerawatan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbJnsPerawatan.setName("tbJnsPerawatan"); // NOI18N
        tbJnsPerawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbJnsPerawatanMouseClicked(evt);
            }
        });
        tbJnsPerawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbJnsPerawatanKeyReleased(evt);
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
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 185));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

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

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 104));
        FormInput.setLayout(null);

        jLabel4.setText("System :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(245, 40, 45, 23);

        KodePemeriksaan.setEditable(false);
        KodePemeriksaan.setHighlighter(null);
        KodePemeriksaan.setName("KodePemeriksaan"); // NOI18N
        FormInput.add(KodePemeriksaan);
        KodePemeriksaan.setBounds(99, 10, 110, 23);

        BtnCariTindakanRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnCariTindakanRadiologi.setMnemonic('1');
        BtnCariTindakanRadiologi.setToolTipText("Alt+1");
        BtnCariTindakanRadiologi.setName("BtnCariTindakanRadiologi"); // NOI18N
        BtnCariTindakanRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariTindakanRadiologiActionPerformed(evt);
            }
        });
        BtnCariTindakanRadiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariTindakanRadiologiKeyPressed(evt);
            }
        });
        FormInput.add(BtnCariTindakanRadiologi);
        BtnCariTindakanRadiologi.setBounds(213, 10, 28, 23);

        PeriksaCode.setHighlighter(null);
        PeriksaCode.setName("PeriksaCode"); // NOI18N
        PeriksaCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PeriksaCodeKeyPressed(evt);
            }
        });
        FormInput.add(PeriksaCode);
        PeriksaCode.setBounds(99, 40, 110, 23);

        jLabel9.setText("Code :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 40, 95, 23);

        jLabel10.setText("Display :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 70, 95, 23);

        PeriksaDisplay.setHighlighter(null);
        PeriksaDisplay.setName("PeriksaDisplay"); // NOI18N
        PeriksaDisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PeriksaDisplayKeyPressed(evt);
            }
        });
        FormInput.add(PeriksaDisplay);
        PeriksaDisplay.setBounds(99, 70, 625, 23);

        PeriksaSystem.setHighlighter(null);
        PeriksaSystem.setName("PeriksaSystem"); // NOI18N
        PeriksaSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PeriksaSystemKeyPressed(evt);
            }
        });
        FormInput.add(PeriksaSystem);
        PeriksaSystem.setBounds(294, 40, 430, 23);

        jLabel11.setText("Sampel Code :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 100, 95, 23);

        jLabel12.setText("Sampel Display :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 130, 95, 23);

        SampelCode.setHighlighter(null);
        SampelCode.setName("SampelCode"); // NOI18N
        SampelCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SampelCodeKeyPressed(evt);
            }
        });
        FormInput.add(SampelCode);
        SampelCode.setBounds(99, 100, 160, 23);

        SampelDisplay.setHighlighter(null);
        SampelDisplay.setName("SampelDisplay"); // NOI18N
        SampelDisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SampelDisplayKeyPressed(evt);
            }
        });
        FormInput.add(SampelDisplay);
        SampelDisplay.setBounds(99, 130, 625, 23);

        jLabel5.setText("Sampel System :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(295, 100, 81, 23);

        SampelSystem.setHighlighter(null);
        SampelSystem.setName("SampelSystem"); // NOI18N
        SampelSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SampelSystemKeyPressed(evt);
            }
        });
        FormInput.add(SampelSystem);
        SampelSystem.setBounds(380, 100, 344, 23);

        NamaPemeriksaan.setEditable(false);
        NamaPemeriksaan.setHighlighter(null);
        NamaPemeriksaan.setName("NamaPemeriksaan"); // NOI18N
        FormInput.add(NamaPemeriksaan);
        NamaPemeriksaan.setBounds(245, 10, 479, 23);

        jLabel13.setText("Tindakan :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(0, 10, 95, 23);

        BtnCariReferensiMapping.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnCariReferensiMapping.setMnemonic('1');
        BtnCariReferensiMapping.setToolTipText("Alt+1");
        BtnCariReferensiMapping.setName("BtnCariReferensiMapping"); // NOI18N
        BtnCariReferensiMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariReferensiMappingActionPerformed(evt);
            }
        });
        BtnCariReferensiMapping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariReferensiMappingKeyPressed(evt);
            }
        });
        FormInput.add(BtnCariReferensiMapping);
        BtnCariReferensiMapping.setBounds(213, 40, 28, 23);

        BtnCariReferensiSampel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnCariReferensiSampel.setMnemonic('1');
        BtnCariReferensiSampel.setToolTipText("Alt+1");
        BtnCariReferensiSampel.setName("BtnCariReferensiSampel"); // NOI18N
        BtnCariReferensiSampel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariReferensiSampelActionPerformed(evt);
            }
        });
        BtnCariReferensiSampel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariReferensiSampelKeyPressed(evt);
            }
        });
        FormInput.add(BtnCariReferensiSampel);
        BtnCariReferensiSampel.setBounds(263, 100, 28, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCariTindakanRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariTindakanRadiologiActionPerformed
        pemeriksaan.isCek();
        pemeriksaan.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pemeriksaan.setLocationRelativeTo(internalFrame1);
        pemeriksaan.setVisible(true);
}//GEN-LAST:event_BtnCariTindakanRadiologiActionPerformed

    private void BtnCariTindakanRadiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariTindakanRadiologiKeyPressed
        Valid.pindah(evt, PeriksaSystem, PeriksaDisplay);
}//GEN-LAST:event_BtnCariTindakanRadiologiKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(PeriksaCode.getText().trim().equals("")){
            Valid.textKosong(PeriksaCode,"Periksa Code");
        }else if(PeriksaSystem.getText().trim().equals("")){
            Valid.textKosong(PeriksaSystem,"Pemeriksaan System");
        }else if(NamaPemeriksaan.getText().trim().equals("")){
            Valid.textKosong(NamaPemeriksaan,"Nama Pemeriksaan");
        }else if(PeriksaDisplay.getText().trim().equals("")){
            Valid.textKosong(PeriksaDisplay,"Pemeriksaan Display");
        }else if(SampelCode.getText().trim().equals("")){
            Valid.textKosong(SampelCode,"Sampel Code");
        }else if(SampelSystem.getText().trim().equals("")){
            Valid.textKosong(SampelSystem,"Sampel System");
        }else if(SampelDisplay.getText().trim().equals("")){
            Valid.textKosong(SampelDisplay,"Sampel Display");
        }else{
            if(Sequel.menyimpantf("satu_sehat_mapping_radiologi","?,?,?,?,?,?,?","Mapping Tindakan Radiologi",7,new String[]{
                KodePemeriksaan.getText(),PeriksaCode.getText(),PeriksaSystem.getText(),PeriksaDisplay.getText(),SampelCode.getText(),SampelSystem.getText(),SampelDisplay.getText()
            })==true){
                tabMode.addRow(new String[]{
                    PeriksaCode.getText(),PeriksaSystem.getText(),KodePemeriksaan.getText(),NamaPemeriksaan.getText(),PeriksaDisplay.getText(),SampelCode.getText(),SampelSystem.getText(),SampelDisplay.getText()
                });
                emptTeks();
                LCount.setText(""+tabMode.getRowCount());
            }                
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{Valid.pindah(evt,SampelDisplay, BtnBatal);}
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(Valid.hapusTabletf(tabMode,KodePemeriksaan,"satu_sehat_mapping_radiologi","kd_jenis_prw")==true){
            tabMode.removeRow(tbJnsPerawatan.getSelectedRow());
            emptTeks();
            LCount.setText(""+tabMode.getRowCount());
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(PeriksaCode.getText().trim().equals("")){
            Valid.textKosong(PeriksaCode,"Periksa Code");
        }else if(PeriksaSystem.getText().trim().equals("")){
            Valid.textKosong(PeriksaSystem,"Pemeriksaan System");
        }else if(NamaPemeriksaan.getText().trim().equals("")){
            Valid.textKosong(NamaPemeriksaan,"Nama Pemeriksaan");
        }else if(PeriksaDisplay.getText().trim().equals("")){
            Valid.textKosong(PeriksaDisplay,"Pemeriksaan Display");
        }else if(SampelCode.getText().trim().equals("")){
            Valid.textKosong(SampelCode,"Sampel Code");
        }else if(SampelSystem.getText().trim().equals("")){
            Valid.textKosong(SampelSystem,"Sampel System");
        }else if(SampelDisplay.getText().trim().equals("")){
            Valid.textKosong(SampelDisplay,"Sampel Display");
        }else{
            if(tbJnsPerawatan.getSelectedRow()>-1){
                if(Sequel.mengedittf("satu_sehat_mapping_radiologi","kd_jenis_prw=?","kd_jenis_prw=?,code=?,system=?,display=?,sampel_code=?,sampel_system=?,sampel_display=?",8,new String[]{
                        KodePemeriksaan.getText(),PeriksaCode.getText(),PeriksaSystem.getText(),PeriksaDisplay.getText(),SampelCode.getText(),SampelSystem.getText(),SampelDisplay.getText(),tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),2).toString()
                    })==true){
                    tabMode.setValueAt(PeriksaCode.getText(),tbJnsPerawatan.getSelectedRow(),0);
                    tabMode.setValueAt(PeriksaSystem.getText(),tbJnsPerawatan.getSelectedRow(),1);
                    tabMode.setValueAt(KodePemeriksaan.getText(),tbJnsPerawatan.getSelectedRow(),2);
                    tabMode.setValueAt(NamaPemeriksaan.getText(),tbJnsPerawatan.getSelectedRow(),3);
                    tabMode.setValueAt(PeriksaDisplay.getText(),tbJnsPerawatan.getSelectedRow(),4);
                    tabMode.setValueAt(SampelCode.getText(),tbJnsPerawatan.getSelectedRow(),5);
                    tabMode.setValueAt(SampelSystem.getText(),tbJnsPerawatan.getSelectedRow(),6);
                    tabMode.setValueAt(SampelDisplay.getText(),tbJnsPerawatan.getSelectedRow(),7);
                    emptTeks();
                }
            }                
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){            
                Map<String, Object> param = new HashMap<>();    
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                param.put("parameter","%"+TCari.getText().trim()+"%");
                Valid.MyReport("rptMapingPemeriksaanRadiologiSatuSehat.jasper","report","::[ Mapping Pemeriksaan Radiologi Satu Sehat Kemenkes ]::",param);            
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbJnsPerawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbJnsPerawatanMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbJnsPerawatanMouseClicked

    private void tbJnsPerawatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJnsPerawatanKeyReleased
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbJnsPerawatanKeyReleased

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void PeriksaCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PeriksaCodeKeyPressed
        Valid.pindah(evt, TCari, PeriksaSystem);
    }//GEN-LAST:event_PeriksaCodeKeyPressed

    private void PeriksaSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PeriksaSystemKeyPressed
        Valid.pindah(evt, BtnCariTindakanRadiologi, PeriksaDisplay);
    }//GEN-LAST:event_PeriksaSystemKeyPressed

    private void PeriksaDisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PeriksaDisplayKeyPressed
        Valid.pindah(evt, PeriksaSystem, SampelCode);
    }//GEN-LAST:event_PeriksaDisplayKeyPressed

    private void SampelCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SampelCodeKeyPressed
        Valid.pindah(evt, PeriksaDisplay, SampelSystem);
    }//GEN-LAST:event_SampelCodeKeyPressed

    private void SampelDisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SampelDisplayKeyPressed
        Valid.pindah(evt, SampelSystem, BtnSimpan);
    }//GEN-LAST:event_SampelDisplayKeyPressed

    private void SampelSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SampelSystemKeyPressed
        Valid.pindah(evt, SampelCode, SampelDisplay);
    }//GEN-LAST:event_SampelSystemKeyPressed

    private void BtnCariReferensiMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariReferensiMappingActionPerformed
        refPeriksa.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        refPeriksa.setLocationRelativeTo(internalFrame1);
        refPeriksa.emptTeks();
        refPeriksa.tampil();
        refPeriksa.setVisible(true);
    }//GEN-LAST:event_BtnCariReferensiMappingActionPerformed

    private void BtnCariReferensiMappingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariReferensiMappingKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariReferensiMappingActionPerformed(null);
        }
    }//GEN-LAST:event_BtnCariReferensiMappingKeyPressed

    private void BtnCariReferensiSampelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariReferensiSampelActionPerformed
        refSampel.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        refSampel.setLocationRelativeTo(internalFrame1);
        refSampel.emptTeks();
        refSampel.tampil();
        refSampel.setVisible(true);
    }//GEN-LAST:event_BtnCariReferensiSampelActionPerformed

    private void BtnCariReferensiSampelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariReferensiSampelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariReferensiSampelActionPerformed(null);
        }
    }//GEN-LAST:event_BtnCariReferensiSampelKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatMapingRadiologi dialog = new SatuSehatMapingRadiologi(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCariReferensiMapping;
    private widget.Button BtnCariReferensiSampel;
    private widget.Button BtnCariTindakanRadiologi;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KodePemeriksaan;
    private widget.Label LCount;
    private widget.TextBox NamaPemeriksaan;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox PeriksaCode;
    private widget.TextBox PeriksaDisplay;
    private widget.TextBox PeriksaSystem;
    private widget.TextBox SampelCode;
    private widget.TextBox SampelDisplay;
    private widget.TextBox SampelSystem;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJnsPerawatan;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
           ps=koneksi.prepareStatement(
                   "select satu_sehat_mapping_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,satu_sehat_mapping_radiologi.code,satu_sehat_mapping_radiologi.system,"+
                   "satu_sehat_mapping_radiologi.display,satu_sehat_mapping_radiologi.sampel_code,satu_sehat_mapping_radiologi.sampel_system,satu_sehat_mapping_radiologi.sampel_display "+
                   "from satu_sehat_mapping_radiologi inner join jns_perawatan_radiologi on satu_sehat_mapping_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw "+
                   (TCari.getText().equals("")?"":"where satu_sehat_mapping_radiologi.kd_jenis_prw like ? or jns_perawatan_radiologi.nm_perawatan like ? or "+
                   "satu_sehat_mapping_radiologi.code like ? or satu_sehat_mapping_radiologi.display like ? ")+
                   " order by satu_sehat_mapping_radiologi.code");
            try {
                if(!TCari.getText().equals("")){
                    ps.setString(1,"%"+TCari.getText()+"%");
                    ps.setString(2,"%"+TCari.getText()+"%");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("code"),rs.getString("system"),rs.getString("kd_jenis_prw"),rs.getString("nm_perawatan"),rs.getString("display"),rs.getString("sampel_code"),rs.getString("sampel_system"),rs.getString("sampel_display")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Ketersediaan : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        PeriksaCode.setText("");
        PeriksaSystem.setText("");
        KodePemeriksaan.setText("");
        NamaPemeriksaan.setText("");
        PeriksaDisplay.setText("");
        SampelCode.setText("");
        SampelSystem.setText("");
        SampelDisplay.setText("");
        ChkInput.setSelected(true);
        isForm();
        PeriksaCode.requestFocus();
    }

    private void getData() {
       if(tbJnsPerawatan.getSelectedRow()!= -1){
           PeriksaCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),0).toString());
           PeriksaSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),1).toString());
           KodePemeriksaan.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),2).toString());
           NamaPemeriksaan.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),3).toString());
           PeriksaDisplay.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),4).toString());
           SampelCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),5).toString());
           SampelSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),6).toString());
           SampelDisplay.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),7).toString());
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getsatu_sehat_mapping_radiologi());
        BtnHapus.setEnabled(akses.getsatu_sehat_mapping_radiologi());
        BtnEdit.setEnabled(akses.getsatu_sehat_mapping_radiologi());
        BtnPrint.setEnabled(akses.getsatu_sehat_mapping_radiologi());
    }
    
    public JTable getTable(){
        return tbJnsPerawatan;
    }  
    
    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 185));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }
}
