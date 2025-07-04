/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPetugas;
import widget.TextBox;


/**
 *
 * @author perpustakaan
 */
public final class RMDataCatatanCairanHemodialisa extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private String dpjp="";
    private StringBuilder htmlContent;
    private String TANGGALMUNDUR="yes",pilihan="";
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMDataCatatanCairanHemodialisa(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.R.M.","Nama Pasien","Umur","JK","Tgl.Lahir","Tanggal","Jam","Minum","Infus",
            "Transfusi","Sisa Priming","Wash Out","Ttl.Input","Urine","Pendarahan","Muntah","UFG","Ttl.Output","Balance","Keterangan","NIP","Nama Petugas"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 23; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(35);
            }else if(i==4){
                column.setPreferredWidth(20);
            }else if(i==5){
                column.setPreferredWidth(65);
            }else if(i==6){
                column.setPreferredWidth(65);
            }else if(i==7){
                column.setPreferredWidth(60);
            }else if(i==8){
                column.setPreferredWidth(50);
            }else if(i==9){
                column.setPreferredWidth(50);
            }else if(i==10){
                column.setPreferredWidth(60);
            }else if(i==11){
                column.setPreferredWidth(70);
            }else if(i==12){
                column.setPreferredWidth(60);
            }else if(i==13){
                column.setPreferredWidth(60);
            }else if(i==14){
                column.setPreferredWidth(50);
            }else if(i==15){
                column.setPreferredWidth(70);
            }else if(i==16){
                column.setPreferredWidth(50);
            }else if(i==17){
                column.setPreferredWidth(50);
            }else if(i==18){
                column.setPreferredWidth(70);
            }else if(i==19){
                column.setPreferredWidth(70);
            }else if(i==20){
                column.setPreferredWidth(150);
            }else if(i==21){
                column.setPreferredWidth(90);
            }else if(i==22){
                column.setPreferredWidth(150);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte)20).getKata(NIP));
        Minum.setDocument(new batasInput(10).getOnlyAngka(Minum));
        Minum.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Infus.setDocument(new batasInput(10).getOnlyAngka(Infus));
        Infus.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Tranfusi.setDocument(new batasInput(10).getOnlyAngka(Tranfusi));
        Tranfusi.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Priming.setDocument(new batasInput(10).getOnlyAngka(Priming));
        Priming.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        WashOut.setDocument(new batasInput(10).getOnlyAngka(WashOut));
        WashOut.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Urine.setDocument(new batasInput(10).getOnlyAngka(Urine));
        Urine.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Pendarahan.setDocument(new batasInput(10).getOnlyAngka(Pendarahan));
        Pendarahan.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Muntah.setDocument(new batasInput(10).getOnlyAngka(Muntah));
        Muntah.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        UFG.setDocument(new batasInput(10).getOnlyAngka(UFG));
        UFG.getDocument().addDocumentListener((widget.TextBox.CustomDocumentListener)e -> hitungBalanceCairan());
        Keterangan.setDocument(new batasInput((byte)100).getKata(Keterangan));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
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
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){                   
                    NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                }  
                NIP.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        }); 
        
        ChkInput.setSelected(false);
        isForm();
        jam();
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
        
        try {
            TANGGALMUNDUR=koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR="yes";
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCatatanCairanHemodialisa = new javax.swing.JMenuItem();
        JK = new widget.TextBox();
        Umur = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
        LoadHTML = new widget.editorpane();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        NIP = new widget.TextBox();
        NamaPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel12 = new widget.Label();
        Minum = new widget.TextBox();
        Tranfusi = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel22 = new widget.Label();
        WashOut = new widget.TextBox();
        jLabel23 = new widget.Label();
        Infus = new widget.TextBox();
        Priming = new widget.TextBox();
        jLabel28 = new widget.Label();
        jLabel29 = new widget.Label();
        Urine = new widget.TextBox();
        jLabel13 = new widget.Label();
        Pendarahan = new widget.TextBox();
        jLabel24 = new widget.Label();
        Muntah = new widget.TextBox();
        jLabel26 = new widget.Label();
        Keterangan = new widget.TextBox();
        jLabel25 = new widget.Label();
        UFG = new widget.TextBox();
        TtlInput = new widget.TextBox();
        jLabel27 = new widget.Label();
        jLabel30 = new widget.Label();
        TtlOutput = new widget.TextBox();
        Balance = new widget.TextBox();
        jLabel14 = new widget.Label();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCatatanCairanHemodialisa.setBackground(new java.awt.Color(255, 255, 254));
        MnCatatanCairanHemodialisa.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCatatanCairanHemodialisa.setForeground(new java.awt.Color(50, 50, 50));
        MnCatatanCairanHemodialisa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCatatanCairanHemodialisa.setText("Formulir Catatan Cairan Hemodialisa");
        MnCatatanCairanHemodialisa.setName("MnCatatanCairanHemodialisa"); // NOI18N
        MnCatatanCairanHemodialisa.setPreferredSize(new java.awt.Dimension(260, 26));
        MnCatatanCairanHemodialisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCatatanCairanHemodialisaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCatatanCairanHemodialisa);

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        Umur.setHighlighter(null);
        Umur.setName("Umur"); // NOI18N

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Catatan Cairan Hemodialisa ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
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

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-06-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-06-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
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

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 184));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 70, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 136, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(326, 10, 295, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-06-2025" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(74, 40, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(212, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 70, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(168, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(233, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(298, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(363, 40, 23, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 70, 23);

        NIP.setEditable(false);
        NIP.setHighlighter(null);
        NIP.setName("NIP"); // NOI18N
        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NIPKeyPressed(evt);
            }
        });
        FormInput.add(NIP);
        NIP.setBounds(474, 40, 94, 23);

        NamaPetugas.setEditable(false);
        NamaPetugas.setName("NamaPetugas"); // NOI18N
        FormInput.add(NamaPetugas);
        NamaPetugas.setBounds(570, 40, 187, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("ALt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasKeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(761, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel12.setText("Minum :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 70, 70, 23);

        Minum.setFocusTraversalPolicyProvider(true);
        Minum.setName("Minum"); // NOI18N
        Minum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MinumKeyPressed(evt);
            }
        });
        FormInput.add(Minum);
        Minum.setBounds(74, 70, 70, 23);

        Tranfusi.setFocusTraversalPolicyProvider(true);
        Tranfusi.setName("Tranfusi"); // NOI18N
        Tranfusi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TranfusiKeyPressed(evt);
            }
        });
        FormInput.add(Tranfusi);
        Tranfusi.setBounds(319, 70, 70, 23);

        jLabel20.setText("Transfusi :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(262, 70, 53, 23);

        jLabel22.setText("Wash Out :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(536, 70, 59, 23);

        WashOut.setFocusTraversalPolicyProvider(true);
        WashOut.setName("WashOut"); // NOI18N
        WashOut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                WashOutKeyPressed(evt);
            }
        });
        FormInput.add(WashOut);
        WashOut.setBounds(599, 70, 70, 23);

        jLabel23.setText("Infus :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(148, 70, 35, 23);

        Infus.setFocusTraversalPolicyProvider(true);
        Infus.setName("Infus"); // NOI18N
        Infus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InfusKeyPressed(evt);
            }
        });
        FormInput.add(Infus);
        Infus.setBounds(187, 70, 70, 23);

        Priming.setFocusTraversalPolicyProvider(true);
        Priming.setName("Priming"); // NOI18N
        Priming.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PrimingKeyPressed(evt);
            }
        });
        FormInput.add(Priming);
        Priming.setBounds(462, 70, 70, 23);

        jLabel28.setText("Sisa Priming :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(393, 70, 65, 23);

        jLabel29.setText("Urine :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(0, 100, 70, 23);

        Urine.setFocusTraversalPolicyProvider(true);
        Urine.setName("Urine"); // NOI18N
        Urine.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UrineKeyPressed(evt);
            }
        });
        FormInput.add(Urine);
        Urine.setBounds(74, 100, 80, 23);

        jLabel13.setText("Pendarahan :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(173, 100, 67, 23);

        Pendarahan.setFocusTraversalPolicyProvider(true);
        Pendarahan.setName("Pendarahan"); // NOI18N
        Pendarahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PendarahanKeyPressed(evt);
            }
        });
        FormInput.add(Pendarahan);
        Pendarahan.setBounds(244, 100, 80, 23);

        jLabel24.setText("Muntah :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(328, 100, 67, 23);

        Muntah.setFocusTraversalPolicyProvider(true);
        Muntah.setName("Muntah"); // NOI18N
        Muntah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MuntahKeyPressed(evt);
            }
        });
        FormInput.add(Muntah);
        Muntah.setBounds(399, 100, 80, 23);

        jLabel26.setText("Balance :");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(0, 130, 70, 23);

        Keterangan.setFocusTraversalPolicyProvider(true);
        Keterangan.setName("Keterangan"); // NOI18N
        Keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeteranganKeyPressed(evt);
            }
        });
        FormInput.add(Keterangan);
        Keterangan.setBounds(229, 130, 560, 23);

        jLabel25.setText("UFG :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(483, 100, 67, 23);

        UFG.setFocusTraversalPolicyProvider(true);
        UFG.setName("UFG"); // NOI18N
        UFG.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UFGKeyPressed(evt);
            }
        });
        FormInput.add(UFG);
        UFG.setBounds(554, 100, 80, 23);

        TtlInput.setEditable(false);
        TtlInput.setFocusTraversalPolicyProvider(true);
        TtlInput.setName("TtlInput"); // NOI18N
        FormInput.add(TtlInput);
        TtlInput.setBounds(714, 70, 75, 23);

        jLabel27.setText("Input :");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(673, 70, 37, 23);

        jLabel30.setText("Output :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(638, 100, 67, 23);

        TtlOutput.setEditable(false);
        TtlOutput.setFocusTraversalPolicyProvider(true);
        TtlOutput.setName("TtlOutput"); // NOI18N
        FormInput.add(TtlOutput);
        TtlOutput.setBounds(709, 100, 80, 23);

        Balance.setFocusTraversalPolicyProvider(true);
        Balance.setName("Balance"); // NOI18N
        FormInput.add(Balance);
        Balance.setBounds(74, 130, 80, 23);

        jLabel14.setText("Keterangan :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(158, 130, 67, 23);

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

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
        }else{            
            Valid.pindah(evt,TCari,Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(NIP.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(NIP,"Petugas");
        }else{
            if(akses.getkode().equals("Admin Utama")){
                simpan();
            }else{
                if(TanggalRegistrasi.getText().equals("")){
                    TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                }
                if(Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem())==true){
                    simpan();
                }
            } 
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,Keterangan,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm(); 
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else{
                if(NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString())){
                    if(Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+" "+tbObat.getValueAt(tbObat.getSelectedRow(),7).toString(),Sequel.ambiltanggalsekarang())==true){
                        hapus();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
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
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(NIP.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(NIP,"Petugas");
        }else{ 
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString())){
                        if(Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+" "+tbObat.getValueAt(tbObat.getSelectedRow(),7).toString(),Sequel.ambiltanggalsekarang())==true){
                            if(TanggalRegistrasi.getText().equals("")){
                                TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                            }
                            if(Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem())==true){
                                ganti();
                            }
                        }
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh petugas yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
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
        petugas.dispose();
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                File g = new File("file2.css");            
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f;            
                BufferedWriter bw;
                
                pilihan =(String) JOptionPane.showInputDialog(null,"Silahkan pilih laporan..!","Pilihan Cetak",JOptionPane.QUESTION_MESSAGE,null,new Object[]{"Laporan 1 (HTML)","Laporan 2 (WPS)","Laporan 3 (CSV)"},"Laporan 1 (HTML)");
                switch (pilihan) {
                    case "Laporan 1 (HTML)":
                            htmlContent = new StringBuilder();
                            htmlContent
                                .append("<tr class='isi'>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.R.M.</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Umur</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>JK</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl.Lahir</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jam</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Minum</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Infus</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Transfusi</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Sisa Priming</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Wash Out</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Ttl.Input</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Urine</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Pendarahan</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Muntah</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>UFG</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Ttl.Output</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Balance</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIP</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Petugas</b></td>")
                                .append("</tr>");
                            for (i = 0; i < tabMode.getRowCount(); i++) {
                                htmlContent
                                    .append("<tr class='isi'>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,0).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,1).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,2).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,3).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,4).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,5).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,6).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,7).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,8).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,9).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,10).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,11).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,12).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,13).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,14).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,15).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,16).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,17).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,18).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,19).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,20).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,21).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,22).toString()).append("</td>")
                                    .append("</tr>");
                            }
                            LoadHTML.setText(
                                "<html>"+
                                  "<table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                                   htmlContent.toString()+
                                  "</table>"+
                                "</html>"
                            );

                            f = new File("DataCatatanCairanHemodialisa.html");            
                            bw = new BufferedWriter(new FileWriter(f));            
                            bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                                        "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                                        "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                            "<tr class='isi2'>"+
                                                "<td valign='top' align='center'>"+
                                                    "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                                    akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                                    akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                                    "<font size='2' face='Tahoma'>DATA CATATAN CAIRAN HEMODIALISA<br><br></font>"+        
                                                "</td>"+
                                           "</tr>"+
                                        "</table>")
                            );
                            bw.close();                         
                            Desktop.getDesktop().browse(f.toURI());
                        break;
                    case "Laporan 2 (WPS)":
                            htmlContent = new StringBuilder();
                            htmlContent
                                .append("<tr class='isi'>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.R.M.</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Umur</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>JK</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl.Lahir</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jam</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Minum</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Infus</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Transfusi</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Sisa Priming</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Wash Out</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Ttl.Input</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Urine</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Pendarahan</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Muntah</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>UFG</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Ttl.Output</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Balance</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIP</b></td>")
                                    .append("<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Petugas</b></td>")
                                .append("</tr>");
                            for (i = 0; i < tabMode.getRowCount(); i++) {
                                htmlContent
                                    .append("<tr class='isi'>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,0).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,1).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,2).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,3).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,4).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,5).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,6).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,7).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,8).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,9).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,10).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,11).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,12).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,13).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,14).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,15).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,16).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,17).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,18).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,19).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,20).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,21).toString()).append("</td>")
                                        .append("<td valign='top'>").append(tbObat.getValueAt(i,22).toString()).append("</td>")
                                    .append("</tr>");
                            }
                            LoadHTML.setText(
                                "<html>"+
                                  "<table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                                   htmlContent.toString()+
                                  "</table>"+
                                "</html>"
                            );

                            f = new File("DataCatatanCairanHemodialisa.wps");            
                            bw = new BufferedWriter(new FileWriter(f));            
                            bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                                        "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                                        "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                            "<tr class='isi2'>"+
                                                "<td valign='top' align='center'>"+
                                                    "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                                    akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                                    akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                                    "<font size='2' face='Tahoma'>DATA CATATAN CAIRAN HEMODIALISA<br><br></font>"+        
                                                "</td>"+
                                           "</tr>"+
                                        "</table>")
                            );
                            bw.close();                         
                            Desktop.getDesktop().browse(f.toURI());
                        break;
                    case "Laporan 3 (CSV)":
                            htmlContent = new StringBuilder();
                            htmlContent.append(                             
                                "\"No.Rawat\";\"No.R.M.\";\"Nama Pasien\";\"Umur\";\"JK\";\"Tgl.Lahir\";\"Tanggal\";\"Jam\";\"Minum\";\"Infus\";\"Transfusi\";\"Sisa Priming\";\"Wash Out\";\"Ttl.Input\";\"Urine\";\"Pendarahan\";\"Muntah\";\"UFG\";\"Ttl.Output\";\"Balance\";\"Keterangan\";\"NIP\";\"Nama Petugas\"\n"
                            ); 
                            for (i = 0; i < tabMode.getRowCount(); i++) {
                                htmlContent.append("\"")
                                    .append(tbObat.getValueAt(i,0).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,1).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,2).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,3).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,4).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,5).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,6).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,7).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,8).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,9).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,10).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,11).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,12).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,13).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,14).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,15).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,16).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,17).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,18).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,19).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,20).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,21).toString()).append("\";\"")
                                    .append(tbObat.getValueAt(i,22).toString()).append("\"\n");
                            }
                            f = new File("DataCatatanCairanHemodialisa.csv");            
                            bw = new BufferedWriter(new FileWriter(f));            
                            bw.write(htmlContent.toString());
                            bw.close();                         
                            Desktop.getDesktop().browse(f.toURI());
                        break; 
                }   
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
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
            tampil();
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        Valid.pindah(evt,TCari,Jam);
}//GEN-LAST:event_TanggalKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void JamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamKeyPressed
        Valid.pindah(evt,Tanggal,Menit);
    }//GEN-LAST:event_JamKeyPressed

    private void MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitKeyPressed
        Valid.pindah(evt,Jam,Detik);
    }//GEN-LAST:event_MenitKeyPressed

    private void DetikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikKeyPressed
        Valid.pindah(evt,Menit,btnPetugas);
    }//GEN-LAST:event_DetikKeyPressed

    private void NIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NIPKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            NamaPetugas.setText(petugas.tampil3(NIP.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Detik.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            Minum.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnPetugasActionPerformed(null);
        }
    }//GEN-LAST:event_NIPKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
        Valid.pindah(evt,Detik,Minum);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void MnCatatanCairanHemodialisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCatatanCairanHemodialisaActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            dpjp=Sequel.cariIsi("select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat=?",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            if(dpjp.equals("")){
                dpjp=Sequel.cariIsi("select dokter.nm_dokter from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat=?",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            }
            param.put("dpjp",dpjp);   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            Valid.MyReportqry("rptFormulirCatatanCairanHemodialisa.jasper","report","::[ Formulir Catatan Cairan Hemodialisa ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,"+
                    "pasien.jk,pasien.tgl_lahir,catatan_cairan_hemodialisa.tgl_perawatan,catatan_cairan_hemodialisa.jam_rawat,catatan_cairan_hemodialisa.minum,"+
                    "catatan_cairan_hemodialisa.infus,catatan_cairan_hemodialisa.tranfusi,catatan_cairan_hemodialisa.sisa_priming,catatan_cairan_hemodialisa.wash_out,"+
                    "catatan_cairan_hemodialisa.urine,catatan_cairan_hemodialisa.pendarahan,catatan_cairan_hemodialisa.muntah,catatan_cairan_hemodialisa.keterangan,"+
                    "catatan_cairan_hemodialisa.nip,petugas.nama from catatan_cairan_hemodialisa inner join reg_periksa on catatan_cairan_hemodialisa.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join petugas on catatan_cairan_hemodialisa.nip=petugas.nip "+
                    "where reg_periksa.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' order by catatan_cairan_hemodialisa.tgl_perawatan,catatan_cairan_hemodialisa.jam_rawat",param);
        }
    }//GEN-LAST:event_MnCatatanCairanHemodialisaActionPerformed

    private void MinumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MinumKeyPressed
        Valid.pindah(evt,btnPetugas,Infus);
    }//GEN-LAST:event_MinumKeyPressed

    private void TranfusiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TranfusiKeyPressed
        Valid.pindah(evt,Infus,Priming);
    }//GEN-LAST:event_TranfusiKeyPressed

    private void WashOutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_WashOutKeyPressed
        Valid.pindah(evt,Priming,Urine);
    }//GEN-LAST:event_WashOutKeyPressed

    private void InfusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InfusKeyPressed
        Valid.pindah(evt,Minum,Tranfusi);
    }//GEN-LAST:event_InfusKeyPressed

    private void PrimingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PrimingKeyPressed
        Valid.pindah(evt,Tranfusi,WashOut);
    }//GEN-LAST:event_PrimingKeyPressed

    private void UrineKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UrineKeyPressed
        Valid.pindah(evt,WashOut,Pendarahan);
    }//GEN-LAST:event_UrineKeyPressed

    private void PendarahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PendarahanKeyPressed
        Valid.pindah(evt,Urine,Muntah);
    }//GEN-LAST:event_PendarahanKeyPressed

    private void MuntahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MuntahKeyPressed
        Valid.pindah(evt,Pendarahan,UFG);
    }//GEN-LAST:event_MuntahKeyPressed

    private void KeteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeteranganKeyPressed
        Valid.pindah(evt,Muntah,BtnSimpan);
    }//GEN-LAST:event_KeteranganKeyPressed

    private void UFGKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UFGKeyPressed
        Valid.pindah(evt,Muntah,Keterangan);
    }//GEN-LAST:event_UFGKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMDataCatatanCairanHemodialisa dialog = new RMDataCatatanCairanHemodialisa(new javax.swing.JFrame(), true);
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
    private widget.TextBox Balance;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.PanelBiasa FormInput;
    private widget.TextBox Infus;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox Keterangan;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ComboBox Menit;
    private widget.TextBox Minum;
    private javax.swing.JMenuItem MnCatatanCairanHemodialisa;
    private widget.TextBox Muntah;
    private widget.TextBox NIP;
    private widget.TextBox NamaPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox Pendarahan;
    private widget.TextBox Priming;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.TextBox Tranfusi;
    private widget.TextBox TtlInput;
    private widget.TextBox TtlOutput;
    private widget.TextBox UFG;
    private widget.TextBox Umur;
    private widget.TextBox Urine;
    private widget.TextBox WashOut;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel16;
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
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
    
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select catatan_cairan_hemodialisa.*, reg_periksa.no_rkm_medis, reg_periksa.umurdaftar, reg_periksa.sttsumur, " +
            "pasien.nm_pasien, pasien.tgl_lahir, pasien.jk, petugas.nama from catatan_cairan_hemodialisa join reg_periksa on " +
            "catatan_cairan_hemodialisa.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
            "join petugas on catatan_cairan_hemodialisa.nip = petugas.nip where catatan_cairan_hemodialisa.tgl_perawatan between ? and ? " +
            (TCari.getText().isBlank() ? "" : "and (catatan_cairan_hemodialisa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or " +
            "pasien.nm_pasien like ? or catatan_cairan_hemodialisa.nip like ? or petugas.nama like ?) ") + "order by " +
            "catatan_cairan_hemodialisa.tgl_perawatan, catatan_cairan_hemodialisa.jam_rawat"
        )) {
            ps.setString(1, Valid.getTglSmc(DTPCari1));
            ps.setString(2, Valid.getTglSmc(DTPCari2));
            if (!TCari.getText().isBlank()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                ps.setString(5, "%" + TCari.getText() + "%");
                ps.setString(6, "%" + TCari.getText() + "%");
                ps.setString(7, "%" + TCari.getText() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[] {
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("umurdaftar") + " " + rs.getString("sttsumur"),
                        rs.getString("jk"), rs.getDate("tgl_lahir"), rs.getDate("tgl_perawatan"), rs.getTime("jam_rawat"), rs.getString("minum"), rs.getString("infus"),
                        rs.getString("tranfusi"), rs.getString("sisa_priming"), rs.getString("wash_out"), rs.getString("ttl_input"), rs.getString("urine"),
                        rs.getString("pendarahan"), rs.getString("muntah"), rs.getString("ufg"), rs.getString("ttl_output"), rs.getString("balance"),
                        rs.getString("keterangan"), rs.getString("nip"), rs.getString("nama")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }
    
    public void emptTeks() {
        Minum.setText("");
        Infus.setText("");
        Tranfusi.setText("");
        Priming.setText("");
        WashOut.setText("");
        TtlInput.setText("");
        Urine.setText("");
        Pendarahan.setText("");
        Muntah.setText("");
        UFG.setText("");
        TtlOutput.setText("");
        Balance.setText("");
        Keterangan.setText("");
        Tanggal.setDate(new Date());
        Minum.requestFocus();
        tbObat.clearSelection();
    } 

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            Umur.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString().substring(0,2));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString().substring(3,5));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString().substring(6,8));
            Minum.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            Infus.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            Tranfusi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            Priming.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            WashOut.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            TtlInput.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            Urine.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            Pendarahan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            Muntah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            UFG.setText(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            TtlOutput.setText(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            Balance.setText(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
            Keterangan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),20).toString());
            Valid.SetTgl(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());  
        }
    }
    
    private void isRawat() {
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi,reg_periksa.umurdaftar,"+
                    "reg_periksa.sttsumur,reg_periksa.jam_reg from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    Umur.setText(rs.getString("umurdaftar")+" "+rs.getString("sttsumur"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi")+" "+rs.getString("jam_reg"));
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
    }
    
    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,184));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getcatatan_cairan_hemodialisa());
        BtnHapus.setEnabled(akses.getcatatan_cairan_hemodialisa());
        BtnEdit.setEnabled(akses.getcatatan_cairan_hemodialisa());
        BtnPrint.setEnabled(akses.getcatatan_cairan_hemodialisa()); 
        if(akses.getjml2()>=1){
            NIP.setEditable(false);
            btnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            NamaPetugas.setText(petugas.tampil3(NIP.getText()));
            if(NamaPetugas.getText().equals("")){
                NIP.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan petugas...!!");
            }
        }   
        
        if(TANGGALMUNDUR.equals("no")){
            if(!akses.getkode().equals("Admin Utama")){
                Tanggal.setEditable(false);
                Tanggal.setEnabled(false);
                ChkKejadian.setEnabled(false);
                Jam.setEnabled(false);
                Menit.setEnabled(false);
                Detik.setEnabled(false);
            }
        }
    }

    private void jam(){
        ActionListener taskPerformer = (ActionEvent e) -> {
            Date now = Calendar.getInstance().getTime();
            if (ChkKejadian.isSelected()) {
                String jam = new java.text.SimpleDateFormat("HH:mm:ss").format(now);
                
                Tanggal.setDate(now);
                Jam.setSelectedItem(jam.substring(0, 2));
                Menit.setSelectedItem(jam.substring(3, 5));
                Detik.setSelectedItem(jam.substring(6, 8));
            }
        };

        new Timer(1000, taskPerformer).start();
    }

    private void ganti() {
        if (Sequel.mengupdatetfSmc("catatan_cairan_hemodialisa",
            "no_rawat = ?, tgl_perawatan = ?, jam_rawat = ?, minum = ?, " +
            "infus = ?, tranfusi = ?, sisa_priming = ?, wash_out = ?, " +
            "urine = ?, pendarahan = ?, muntah = ?, keterangan = ?, " +
            "nip = ?, ttl_input = ?, ufg = ?, ttl_output = ?, balance = ?",
            "no_rawat = ? and tgl_perawatan = ? and jam_rawat = ?",
            TNoRw.getText(), Valid.getTglSmc(Tanggal), Valid.getJamSmc(Jam, Menit, Detik), Minum.getText(), Infus.getText(), Tranfusi.getText(),
            Priming.getText(), WashOut.getText(), Urine.getText(), Pendarahan.getText(), Muntah.getText(), Keterangan.getText(), NIP.getText(),
            TtlInput.getText(), UFG.getText(), TtlOutput.getText(), Balance.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()
        )) {
            tbObat.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 0);
            tbObat.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
            tbObat.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
            tbObat.setValueAt(Umur.getText(), tbObat.getSelectedRow(), 3);
            tbObat.setValueAt(JK.getText(), tbObat.getSelectedRow(), 4);
            tbObat.setValueAt(TglLahir.getText(), tbObat.getSelectedRow(), 5);
            tbObat.setValueAt(Valid.getTglSmc(Tanggal), tbObat.getSelectedRow(), 6);
            tbObat.setValueAt(Valid.getJamSmc(Jam, Menit, Detik), tbObat.getSelectedRow(), 7);
            tbObat.setValueAt(Minum.getText(), tbObat.getSelectedRow(), 8);
            tbObat.setValueAt(Infus.getText(), tbObat.getSelectedRow(), 9);
            tbObat.setValueAt(Tranfusi.getText(), tbObat.getSelectedRow(), 10);
            tbObat.setValueAt(Priming.getText(), tbObat.getSelectedRow(), 11);
            tbObat.setValueAt(WashOut.getText(), tbObat.getSelectedRow(), 12);
            tbObat.setValueAt(TtlInput.getText(), tbObat.getSelectedRow(), 13);
            tbObat.setValueAt(Urine.getText(), tbObat.getSelectedRow(), 14);
            tbObat.setValueAt(Pendarahan.getText(), tbObat.getSelectedRow(), 15);
            tbObat.setValueAt(Muntah.getText(), tbObat.getSelectedRow(), 16);
            tbObat.setValueAt(UFG.getText(), tbObat.getSelectedRow(), 17);
            tbObat.setValueAt(TtlOutput.getText(), tbObat.getSelectedRow(), 18);
            tbObat.setValueAt(Balance.getText(), tbObat.getSelectedRow(), 18);
            tbObat.setValueAt(Keterangan.getText(), tbObat.getSelectedRow(), 20);
            tbObat.setValueAt(NIP.getText(), tbObat.getSelectedRow(), 21);
            tbObat.setValueAt(NamaPetugas.getText(), tbObat.getSelectedRow(), 22);
            emptTeks();
        }
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from catatan_cairan_hemodialisa where tgl_perawatan=? and jam_rawat=? and no_rawat=?",3,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),6).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),7).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(""+tabMode.getRowCount());
            emptTeks();
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }

    private void simpan() {
        if (Sequel.menyimpantfSmc("catatan_cairan_hemodialisa", null,
            TNoRw.getText(), Valid.getTglSmc(Tanggal), Valid.getJamSmc(Jam, Menit, Detik), Minum.getText(), Infus.getText(), Tranfusi.getText(),
            Priming.getText(), WashOut.getText(), Urine.getText(), Pendarahan.getText(), Muntah.getText(), Keterangan.getText(), NIP.getText(),
            TtlInput.getText(), UFG.getText(), TtlOutput.getText(), Balance.getText()
        )) {
            tabMode.addRow(new Object[] {
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), Umur.getText(), JK.getText(), TglLahir.getText(),
                Valid.getTglSmc(Tanggal), Valid.getJamSmc(Jam, Menit, Detik), Minum.getText(), Infus.getText(),
                Tranfusi.getText(), Priming.getText(), WashOut.getText(), TtlInput.getText(), Urine.getText(), Pendarahan.getText(), Muntah.getText(), UFG.getText(), TtlOutput.getText(),
                Balance.getText(), Keterangan.getText(), NIP.getText(), NamaPetugas.getText()
            });
            LCount.setText(String.valueOf(tabMode.getRowCount()));
            emptTeks();
        }
    }

    private void hitungBalanceCairan() {
        TtlInput.setText(String.valueOf(Valid.SetAngka(Minum.getText().trim()) + Valid.SetAngka(Infus.getText().trim()) + Valid.SetAngka(Tranfusi.getText().trim()) + Valid.SetAngka(Priming.getText().trim()) + Valid.SetAngka(WashOut.getText().trim())));
        TtlOutput.setText(String.valueOf(Valid.SetAngka(Urine.getText().trim()) + Valid.SetAngka(Pendarahan.getText().trim()) + Valid.SetAngka(Muntah.getText().trim()) + Valid.SetAngka(UFG.getText().trim())));
        Balance.setText(String.valueOf(Valid.SetAngka(TtlInput.getText().trim()) - Valid.SetAngka(TtlOutput.getText().trim())));
    }
}
