package surat;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import kepegawaian.DlgCariDokter2;
import laporan.DlgCariPenyakit;
import simrskhanza.DlgCariPoli;
import simrskhanza.DlgCariPoli2;

/**
 *
 * @author dosen
 */
public class SuratKontrol extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps,ps2;
    private ResultSet rs,rs2;
    private int i=0,kuota=0;
    private String status="",kdpoli="",nmpoli="",noantri="";
    private String finger="", norawat = "";
    private final String URUTNOREG = koneksiDB.URUTNOREG(), JADWALDOKTERDIREGISTRASI = koneksiDB.JADWALDOKTERDIREGISTRASI();
    private final boolean JADIKANBOOKINGSURATKONTROL = koneksiDB.JADIKANBOOKINGSURATKONTROL().equals("yes"),
        BOOKINGLANGSUNGREGISTRASI = koneksiDB.BOOKINGLANGSUNGREGISTRASI();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;


    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public SuratKontrol(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode=new DefaultTableModel(null,new Object[]{
                "Tahun","No.RM","Nama Pasien","Diagnosa","Terapi","Alasan 1","Alasan 2",
                "Rencana Tindak Lanjut 1","Rencana Tindak Lanjut 2","Periksa Kembali",
                "Tanggal Surat","No.Surat","No.Reg","Kode Dokter","Nama Dokter",
                "Kode Poli","Nama Poli","Status"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 18; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(150);
            }else if(i==6){
                column.setPreferredWidth(150);
            }else if(i==7){
                column.setPreferredWidth(150);
            }else if(i==8){
                column.setPreferredWidth(150);
            }else if(i==9){
                column.setPreferredWidth(115);
            }else if(i==10){
                column.setPreferredWidth(115);
            }else if(i==11){
                column.setPreferredWidth(52);
            }else if(i==12){
                column.setPreferredWidth(45);
            }else if(i==13){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==14){
                column.setPreferredWidth(150);
            }else if(i==15){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==16){
                column.setPreferredWidth(150);
            }else if(i==17){
                column.setPreferredWidth(90);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());


        TNoRM.setDocument(new batasInput((byte)15).getKata(TNoRM));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        Diagnosa.setDocument(new batasInput((int)50).getKata(Diagnosa));
        Terapi.setDocument(new batasInput((int)200).getKata(Terapi));
        Alasan1.setDocument(new batasInput((int)50).getKata(Alasan1));
        Alasan2.setDocument(new batasInput((int)50).getKata(Alasan2));
        Rtl1.setDocument(new batasInput((int)50).getKata(Rtl1));
        Rtl2.setDocument(new batasInput((int)50).getKata(Rtl2));
        NoReg.setDocument(new batasInput((byte)6).getKata(NoReg));
        KdDokter.setDocument(new batasInput((byte)20).getKata(KdDokter));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
            });
        }

        ChkInput.setSelected(false);
        isForm();

        TanggalPeriksa.setDate(new Date());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnSurat = new javax.swing.JMenuItem();
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
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass10 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelCari = new widget.panelisi();
        R1 = new widget.RadioButton();
        R2 = new widget.RadioButton();
        DTPCari1 = new widget.Tanggal();
        jLabel22 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        R3 = new widget.RadioButton();
        DTPCari3 = new widget.Tanggal();
        jLabel25 = new widget.Label();
        DTPCari4 = new widget.Tanggal();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRM = new widget.TextBox();
        jLabel9 = new widget.Label();
        NmDokter = new widget.TextBox();
        TPasien = new widget.TextBox();
        TanggalSurat = new widget.Tanggal();
        Status = new widget.ComboBox();
        jLabel10 = new widget.Label();
        KdDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel37 = new widget.Label();
        jLabel11 = new widget.Label();
        KdPoli = new widget.TextBox();
        NmPoli = new widget.TextBox();
        BtnPoli = new widget.Button();
        jLabel5 = new widget.Label();
        Alasan1 = new widget.TextBox();
        Alasan2 = new widget.TextBox();
        jLabel8 = new widget.Label();
        jLabel12 = new widget.Label();
        Rtl1 = new widget.TextBox();
        jLabel13 = new widget.Label();
        Rtl2 = new widget.TextBox();
        jLabel14 = new widget.Label();
        TanggalPeriksa = new widget.Tanggal();
        jLabel15 = new widget.Label();
        NoAntrian = new widget.TextBox();
        Diagnosa = new widget.TextBox();
        jLabel16 = new widget.Label();
        jLabel17 = new widget.Label();
        Terapi = new widget.TextBox();
        NoReg = new widget.TextBox();
        jLabel18 = new widget.Label();
        btnDiagnosa = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnSurat.setBackground(new java.awt.Color(255, 255, 254));
        MnSurat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSurat.setForeground(new java.awt.Color(50, 50, 50));
        MnSurat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSurat.setText("Surat Kontrol");
        MnSurat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSurat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSurat.setName("MnSurat"); // NOI18N
        MnSurat.setPreferredSize(new java.awt.Dimension(180, 26));
        MnSurat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSuratActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSurat);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Surat Kontrol ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObatKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 144));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass10.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass10.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
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
        panelGlass10.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass10.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass10.add(LCount);

        jPanel3.add(panelGlass10, java.awt.BorderLayout.CENTER);

        panelCari.setName("panelCari"); // NOI18N
        panelCari.setPreferredSize(new java.awt.Dimension(44, 43));
        panelCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 9));

        R1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(R1);
        R1.setSelected(true);
        R1.setText("Menunggu");
        R1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R1.setName("R1"); // NOI18N
        R1.setPreferredSize(new java.awt.Dimension(85, 23));
        panelCari.add(R1);

        R2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(R2);
        R2.setText("Tanggal :");
        R2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R2.setName("R2"); // NOI18N
        R2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelCari.add(R2);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-10-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPCari1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari1ItemStateChanged(evt);
            }
        });
        DTPCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari1KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari1);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("s.d");
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(25, 23));
        panelCari.add(jLabel22);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-10-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari2KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari2);

        R3.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(R3);
        R3.setText("Selesai :");
        R3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R3.setName("R3"); // NOI18N
        R3.setPreferredSize(new java.awt.Dimension(85, 23));
        panelCari.add(R3);

        DTPCari3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-10-2025" }));
        DTPCari3.setDisplayFormat("dd-MM-yyyy");
        DTPCari3.setName("DTPCari3"); // NOI18N
        DTPCari3.setOpaque(false);
        DTPCari3.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPCari3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari3ItemStateChanged(evt);
            }
        });
        DTPCari3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari3KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari3);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("s.d");
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(25, 23));
        panelCari.add(jLabel25);

        DTPCari4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-10-2025" }));
        DTPCari4.setDisplayFormat("dd-MM-yyyy");
        DTPCari4.setName("DTPCari4"); // NOI18N
        DTPCari4.setOpaque(false);
        DTPCari4.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPCari4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari4ItemStateChanged(evt);
            }
        });
        DTPCari4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari4KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari4);

        jPanel3.add(panelCari, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 216));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setText(".: Input Data");
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
        FormInput.setPreferredSize(new java.awt.Dimension(190, 107));
        FormInput.setLayout(null);

        jLabel4.setText("Pasien :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 92, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(96, 10, 87, 23);

        jLabel9.setText("Dokter :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 130, 92, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        FormInput.add(NmDokter);
        NmDokter.setBounds(185, 130, 160, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(185, 10, 190, 23);

        TanggalSurat.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSurat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-10-2025 11:10:26" }));
        TanggalSurat.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalSurat.setName("TanggalSurat"); // NOI18N
        TanggalSurat.setOpaque(false);
        TanggalSurat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalSuratKeyPressed(evt);
            }
        });
        FormInput.add(TanggalSurat);
        TanggalSurat.setBounds(474, 10, 132, 23);

        Status.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Menunggu", "Sudah Periksa", "Batal Periksa" }));
        Status.setName("Status"); // NOI18N
        Status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StatusKeyPressed(evt);
            }
        });
        FormInput.add(Status);
        Status.setBounds(610, 160, 130, 23);

        jLabel10.setText("Tanggal Surat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(375, 10, 95, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter"); // NOI18N
        FormInput.add(KdDokter);
        KdDokter.setBounds(96, 130, 87, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setToolTipText("Alt+X");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(347, 130, 28, 23);

        jLabel37.setText("Status :");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(546, 160, 60, 23);

        jLabel11.setText("Unit/Poli :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(375, 130, 95, 23);

        KdPoli.setEditable(false);
        KdPoli.setName("KdPoli"); // NOI18N
        FormInput.add(KdPoli);
        KdPoli.setBounds(474, 130, 70, 23);

        NmPoli.setEditable(false);
        NmPoli.setName("NmPoli"); // NOI18N
        FormInput.add(NmPoli);
        NmPoli.setBounds(546, 130, 165, 23);

        BtnPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPoli.setToolTipText("Alt+X");
        BtnPoli.setName("BtnPoli"); // NOI18N
        BtnPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPoliActionPerformed(evt);
            }
        });
        BtnPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPoliKeyPressed(evt);
            }
        });
        FormInput.add(BtnPoli);
        BtnPoli.setBounds(712, 130, 28, 23);

        jLabel5.setText("Alasan 1 :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 70, 92, 23);

        Alasan1.setName("Alasan1"); // NOI18N
        Alasan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Alasan1KeyPressed(evt);
            }
        });
        FormInput.add(Alasan1);
        Alasan1.setBounds(96, 70, 279, 23);

        Alasan2.setName("Alasan2"); // NOI18N
        Alasan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Alasan2KeyPressed(evt);
            }
        });
        FormInput.add(Alasan2);
        Alasan2.setBounds(474, 70, 266, 23);

        jLabel8.setText("Alasan 2 :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(375, 70, 95, 23);

        jLabel12.setText("Tindak Lanjut 1 :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 100, 92, 23);

        Rtl1.setName("Rtl1"); // NOI18N
        Rtl1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Rtl1KeyPressed(evt);
            }
        });
        FormInput.add(Rtl1);
        Rtl1.setBounds(96, 100, 279, 23);

        jLabel13.setText("Tindak Lanjut 2 :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(375, 100, 95, 23);

        Rtl2.setName("Rtl2"); // NOI18N
        Rtl2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Rtl2KeyPressed(evt);
            }
        });
        FormInput.add(Rtl2);
        Rtl2.setBounds(474, 100, 266, 23);

        jLabel14.setText("Periksa Kembali :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 160, 92, 23);

        TanggalPeriksa.setForeground(new java.awt.Color(50, 70, 50));
        TanggalPeriksa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-10-2025 11:10:26" }));
        TanggalPeriksa.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalPeriksa.setName("TanggalPeriksa"); // NOI18N
        TanggalPeriksa.setOpaque(false);
        TanggalPeriksa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalPeriksaKeyPressed(evt);
            }
        });
        FormInput.add(TanggalPeriksa);
        TanggalPeriksa.setBounds(96, 160, 132, 23);

        jLabel15.setText("No.Surat :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(226, 160, 60, 23);

        NoAntrian.setName("NoAntrian"); // NOI18N
        NoAntrian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoAntrianKeyPressed(evt);
            }
        });
        FormInput.add(NoAntrian);
        NoAntrian.setBounds(290, 160, 85, 23);

        Diagnosa.setName("Diagnosa"); // NOI18N
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa);
        Diagnosa.setBounds(96, 40, 249, 23);

        jLabel16.setText("Diagnosa :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 92, 23);

        jLabel17.setText("Terapi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(375, 40, 95, 23);

        Terapi.setName("Terapi"); // NOI18N
        Terapi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TerapiKeyPressed(evt);
            }
        });
        FormInput.add(Terapi);
        Terapi.setBounds(474, 40, 266, 23);

        NoReg.setName("NoReg"); // NOI18N
        NoReg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRegKeyPressed(evt);
            }
        });
        FormInput.add(NoReg);
        NoReg.setBounds(474, 160, 70, 23);

        jLabel18.setText("No.Reg :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(375, 160, 95, 23);

        btnDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDiagnosa.setToolTipText("Alt+3");
        btnDiagnosa.setName("btnDiagnosa"); // NOI18N
        btnDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaActionPerformed(evt);
            }
        });
        btnDiagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosa);
        btnDiagnosa.setBounds(347, 40, 28, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        Valid.pindah(evt,Status,KdDokter);

    }//GEN-LAST:event_TNoRMKeyPressed

    private void TanggalSuratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalSuratKeyPressed
        Valid.pindah(evt,TCari,Status);
    }//GEN-LAST:event_TanggalSuratKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRM.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRM,"pasien");
        }else if(NmDokter.getText().trim().equals("")||KdDokter.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"Dokter");
        }else if(NmPoli.getText().trim().equals("")||NmPoli.getText().trim().equals("")){
            Valid.textKosong(KdPoli,"Poli");
        }else if(NoAntrian.getText().trim().equals("")){
            Valid.textKosong(NoAntrian,"No.Surat");
        }else if(NoReg.getText().trim().equals("")){
            Valid.textKosong(NoReg,"No.Antri");
        }else if(Terapi.getText().trim().equals("")){
            Valid.textKosong(Terapi,"Terapi");
        }else if(Diagnosa.getText().trim().equals("")){
            Valid.textKosong(Diagnosa,"Diagnosa");
        }else{
             if(akses.getkode().equals("Admin Utama")){
                isBooking();
            }else{
                if(JADWALDOKTERDIREGISTRASI.equals("aktif")){
                    if(Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.kd_dokter='"+KdDokter.getText()+"' and booking_registrasi.tanggal_periksa='"+Valid.SetTgl(TanggalPeriksa.getSelectedItem()+"")+"' ")>=kuota){
                        JOptionPane.showMessageDialog(null,"Eiiits, Kuota registrasi penuh..!!!");
                        TCari.requestFocus();
                    }else{
                        isBooking();
                    }
                }else{
                    isBooking();
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,NoReg,BtnBatal);
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
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            TanggalSurat.requestFocus();
        }else if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(!(TPasien.getText().trim().equals(""))){
            if(tbObat.getSelectedRow()!= -1){
                if (Sequel.menghapustfSmc("skdp_bpjs", "tahun = ? and no_antrian = ?",
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString()
                )) {
                    if (JADIKANBOOKINGSURATKONTROL) {
                        if (BOOKINGLANGSUNGREGISTRASI) {
                            norawat = Sequel.cariIsiSmc("select booking_registrasi.no_rawat from booking_registrasi where " +
                                "booking_registrasi.no_rkm_medis = ? and booking_registrasi.tanggal_periksa = ?",
                                tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(),
                                tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                            );

                            if (!norawat.isBlank()) {
                                Sequel.menghapustfSmc("booking_registrasi", "no_rkm_medis = ? and tanggal_periksa = ?",
                                    tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(),
                                    tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                                );
                                Sequel.menghapustfSmc("reg_periksa", "no_rawat = ? and no_rkm_medis = ? and tgl_registrasi = ? and status_lanjut = 'Ralan' " +
                                    "and stts = 'Belum' and not exists(select * from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat = reg_periksa.no_rawat)",
                                    norawat, tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                                );
                            }
                        } else {
                            Sequel.menghapusSmc("booking_registrasi", "no_rkm_medis = ? and tanggal_periksa = ?",
                                tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                            );
                        }
                    }
                    tabMode.removeRow(tbObat.getSelectedRow());
                    LCount.setText("" + tabMode.getRowCount());
                    emptTeks();
                }
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnPrint);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnPrint,TCari);}
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

            Sequel.queryu("delete from temporary_booking_registrasi");
            for(i=0;i<tabMode.getRowCount();i++){
                Sequel.menyimpan("temporary_booking_registrasi","'0','"+
                    tabMode.getValueAt(i,0).toString()+"','"+
                    tabMode.getValueAt(i,1).toString()+"','"+
                    tabMode.getValueAt(i,2).toString()+"','"+
                    tabMode.getValueAt(i,3).toString()+"','"+
                    tabMode.getValueAt(i,4).toString()+"','"+
                    tabMode.getValueAt(i,5).toString()+"','"+
                    tabMode.getValueAt(i,6).toString()+"','"+
                    tabMode.getValueAt(i,7).toString()+"','"+
                    tabMode.getValueAt(i,8).toString()+"','"+
                    tabMode.getValueAt(i,9).toString()+"','"+
                    tabMode.getValueAt(i,10).toString()+"','"+
                    tabMode.getValueAt(i,11).toString()+"','"+
                    tabMode.getValueAt(i,12).toString()+"','"+
                    tabMode.getValueAt(i,13).toString()+"','"+
                    tabMode.getValueAt(i,14).toString()+"','"+
                    tabMode.getValueAt(i,15).toString()+"','"+
                    tabMode.getValueAt(i,16).toString()+"','"+
                    tabMode.getValueAt(i,17).toString()+"','','','','','','','','','','','','','','','','','','',''","Rekap Nota Pembayaran");
            }

            Valid.MyReport("rptSKDPBPJS.jasper","report","::[ Laporan Daftar Surat Kontrol ]::",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
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
        runBackground(() ->tampil());
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
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            runBackground(() ->tampil());
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
    if(JADWALDOKTERDIREGISTRASI.equals("aktif")){
        if(akses.getkode().equals("Admin Utama")){
            DlgCariDokter dokter=new DlgCariDokter(null,false);
            dokter.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {;}
                @Override
                public void windowClosing(WindowEvent e) {}
                @Override
                public void windowClosed(WindowEvent e) {
                    if(dokter.getTable().getSelectedRow()!= -1){
                        KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        isNomer();
                    }
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
            dokter.isCek();
            dokter.TCari.requestFocus();
            dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dokter.setLocationRelativeTo(internalFrame1);
            dokter.setVisible(true);
        }else{
            DlgCariDokter2 dokter2=new DlgCariDokter2(null,false);
            dokter2.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {;}
                @Override
                public void windowClosing(WindowEvent e) {}
                @Override
                public void windowClosed(WindowEvent e) {
                    if(dokter2.getTable().getSelectedRow()!= -1){
                        KdDokter.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),0).toString());
                        NmDokter.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),1).toString());
                        if(JADWALDOKTERDIREGISTRASI.equals("aktif")){
                            kuota=Integer.parseInt(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),13).toString());
                        }
                        isNomer();
                    }
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
            dokter2.setPoli(NmPoli.getText());
            dokter2.isCek();
            dokter2.SetHari(TanggalPeriksa.getDate());
            dokter2.tampil3();
            dokter2.TCari.requestFocus();
            dokter2.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dokter2.setLocationRelativeTo(internalFrame1);
            dokter2.setVisible(true);
        }
    }else{
        DlgCariDokter dokter=new DlgCariDokter(null,false);
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    isNomer();
                }
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
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
    if(evt.getKeyCode()==KeyEvent.VK_SPACE){
        BtnDokterActionPerformed(null);
    }else{
        Valid.pindah(evt,Rtl2,BtnPoli);
    }
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
  isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void DTPCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari1KeyPressed

    }//GEN-LAST:event_DTPCari1KeyPressed

    private void DTPCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari2KeyPressed
        R2.setSelected(true);
    }//GEN-LAST:event_DTPCari2KeyPressed

    private void DTPCari3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari3KeyPressed

    private void DTPCari4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari4KeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRM.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRM,"pasien");
        }else if(NmDokter.getText().trim().equals("")||KdDokter.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"Dokter");
        }else if(NmPoli.getText().trim().equals("")||NmPoli.getText().trim().equals("")){
            Valid.textKosong(KdPoli,"Poli");
        }else if(NoAntrian.getText().trim().equals("")){
            Valid.textKosong(NoAntrian,"No.Surat");
        }else if(NoReg.getText().trim().equals("")){
            Valid.textKosong(NoReg,"No.Antri");
        }else if(Terapi.getText().trim().equals("")){
            Valid.textKosong(Terapi,"Terapi");
        }else if(Diagnosa.getText().trim().equals("")){
            Valid.textKosong(Diagnosa,"Diagnosa");
        }else{
            if (tbObat.getSelectedRow() >= 0) {
                if (Sequel.mengupdatetfSmc("skdp_bpjs", "tahun = ?, no_rkm_medis = ?, diagnosa = ?, terapi = ?, alasan1 = ?, alasan2 = ?, rtl1 = ?, " +
                    "rtl2 = ?, tanggal_datang = ?, tanggal_rujukan = ?, no_antrian = ?, kd_dokter = ?, status = ?", "tahun = ? and no_antrian = ?",
                    TanggalPeriksa.getSelectedItem().toString().substring(6, 10), TNoRM.getText(), Diagnosa.getText(), Terapi.getText(), Alasan1.getText(),
                    Alasan2.getText(), Rtl1.getText(), Rtl2.getText(), Valid.getTglSmc(TanggalPeriksa), Valid.getTglSmc(TanggalSurat), NoAntrian.getText(),
                    KdDokter.getText(), Status.getSelectedItem().toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString()
                )) {
                    if (JADIKANBOOKINGSURATKONTROL) {
                        if (BOOKINGLANGSUNGREGISTRASI) {
                            norawat = Sequel.cariIsiSmc("select booking_registrasi.no_rawat from booking_registrasi where " +
                                "booking_registrasi.no_rkm_medis = ? and booking_registrasi.tanggal_periksa = ?",
                                tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(),
                                tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                            );

                            if (!norawat.isBlank()) {
                                Sequel.menghapustfSmc("booking_registrasi", "no_rkm_medis = ? and tanggal_periksa = ?",
                                    tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(),
                                    tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                                );
                                Sequel.menghapustfSmc("reg_periksa", "no_rawat = ? and no_rkm_medis = ? and tgl_registrasi = ? and status_lanjut = 'Ralan' " +
                                    "and stts = 'Belum' and not exists(select * from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat = reg_periksa.no_rawat)",
                                    norawat, tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                                );
                            }

                            String kodePJ = Sequel.cariIsiSmc("select pasien.kd_pj from pasien where pasien.no_rkm_medis = ?", TNoRM.getText()), namaPJ = "", alamatPJ = "",
                                hubunganPJ = "", biayaReg = Sequel.cariIsiSmc("select poliklinik.registrasilama from poliklinik where poliklinik.kd_poli = ?", KdPoli.getText()),
                                statusDaftar = "", umurDaftar = "0", statusUmur = "Th", statusPoli = "", umurPasienRM = "";

                            try (PreparedStatement ps = koneksi.prepareStatement(
                                "select pasien.namakeluarga, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as almt, pasien.keluarga, " +
                                "timestampdiff(year, pasien.tgl_lahir, ?) as tahun, timestampdiff(month, pasien.tgl_lahir, ?) - ((timestampdiff(month, pasien.tgl_lahir, ?) " +
                                "div 12) * 12) as bulan, timestampdiff(day, date_add(date_add(pasien.tgl_lahir, interval timestampdiff(year, pasien.tgl_lahir, ?) year), " +
                                "interval timestampdiff(month, pasien.tgl_lahir, ?) - ((timestampdiff(month, pasien.tgl_lahir, ?) div 12) * 12) month), ?) as hari, " +
                                "if(exists(select * from reg_periksa where reg_periksa.no_rkm_medis = pasien.no_rkm_medis), 'Lama', 'Baru') as stts_daftar from " +
                                "pasien join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on " +
                                "pasien.kd_kab = kabupaten.kd_kab where pasien.no_rkm_medis = ?"
                            )) {
                                ps.setString(1, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(2, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(3, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(4, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(5, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(6, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(7, Valid.getTglSmc(TanggalPeriksa));
                                ps.setString(8, TNoRM.getText());
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) {
                                        namaPJ = rs.getString("namakeluarga");
                                        alamatPJ = rs.getString("almt");
                                        hubunganPJ = rs.getString("keluarga");
                                        statusDaftar = rs.getString("stts_daftar");
                                        statusPoli = statusDaftar.equals("Baru") ? "Baru" : Sequel.cariIsiSmc("select if(exists(select * from reg_periksa where reg_periksa.no_rkm_medis = ? and reg_periksa.kd_poli = ?), 'Lama', 'Baru')", TNoRM.getText(), KdPoli.getText());
                                        if (rs.getInt("tahun") > 0) {
                                            umurDaftar = rs.getString("tahun");
                                            statusUmur = "Th";
                                        } else if (rs.getInt("tahun") == 0 && rs.getInt("bulan") > 0) {
                                            umurDaftar = rs.getString("bulan");
                                            statusUmur = "Bl";
                                        } else if (rs.getInt("tahun") == 0 && rs.getInt("bulan") == 0 && rs.getInt("hari") > 0) {
                                            umurDaftar = rs.getString("hari");
                                            statusUmur = "Hr";
                                        }
                                        umurPasienRM = rs.getString("tahun") + " Th " + rs.getString("bulan") + " Bl " + rs.getString("hari") + " Hr";
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Notif : " + e);
                            }

                            boolean sukses = false;
                            int r = 0;
                            do {
                                isNomorBooking();
                                sukses = Sequel.menyimpantfSmc("reg_periksa", null, NoReg.getText(), norawat, Valid.getTglSmc(TanggalPeriksa), Valid.getJamSmc(TanggalSurat),
                                    KdDokter.getText(), TNoRM.getText(), KdPoli.getText(), namaPJ, alamatPJ, hubunganPJ, biayaReg, "Belum", statusDaftar,
                                    "Ralan", kodePJ, umurDaftar, statusUmur, "Belum bayar", statusPoli
                                );
                            } while (!sukses && r++ < 5);

                            if (sukses) {
                                Sequel.mengupdateSmc("pasien", "umur = ?", "no_rkm_medis = ?", umurPasienRM, TNoRM.getText());
                                Sequel.menyimpanSmc("booking_registrasi", null, Valid.getTglSmc(TanggalSurat),
                                    Valid.getJamSmc(TanggalSurat), TNoRM.getText(), Valid.getTglSmc(TanggalPeriksa),
                                    KdDokter.getText(), KdPoli.getText(), NoReg.getText(), kodePJ, "0", null, "Belum", norawat
                                );
                            } else {
                                JOptionPane.showMessageDialog(null, "Tidak dapat membuat booking registrasi pasien..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            Sequel.mengupdateSmc("booking_registrasi", "tanggal_booking = ?, no_rkm_medis = ?, tanggal_periksa = ?, kd_dokter = ?, kd_poli = ?, no_reg = ?",
                                "no_rkm_medis = ? and tanggal_periksa = ?", Valid.getTglSmc(TanggalSurat), TNoRM.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(),
                                KdPoli.getText(), NoReg.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()
                            );
                        }
                    }
                    tbObat.setValueAt(TanggalPeriksa.getSelectedItem().toString().substring(6, 10), tbObat.getSelectedRow(), 0);
                    tbObat.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
                    tbObat.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
                    tbObat.setValueAt(Diagnosa.getText(), tbObat.getSelectedRow(), 3);
                    tbObat.setValueAt(Terapi.getText(), tbObat.getSelectedRow(), 4);
                    tbObat.setValueAt(Alasan1.getText(), tbObat.getSelectedRow(), 5);
                    tbObat.setValueAt(Alasan2.getText(), tbObat.getSelectedRow(), 6);
                    tbObat.setValueAt(Rtl1.getText(), tbObat.getSelectedRow(), 7);
                    tbObat.setValueAt(Rtl2.getText(), tbObat.getSelectedRow(), 8);
                    tbObat.setValueAt(Valid.getTglSmc(TanggalPeriksa), tbObat.getSelectedRow(), 9);
                    tbObat.setValueAt(Valid.getTglSmc(TanggalSurat), tbObat.getSelectedRow(), 10);
                    tbObat.setValueAt(NoAntrian.getText(), tbObat.getSelectedRow(), 11);
                    tbObat.setValueAt(NoReg.getText(), tbObat.getSelectedRow(), 12);
                    tbObat.setValueAt(KdDokter.getText(), tbObat.getSelectedRow(), 13);
                    tbObat.setValueAt(NmDokter.getText(), tbObat.getSelectedRow(), 14);
                    tbObat.setValueAt(KdPoli.getText(), tbObat.getSelectedRow(), 15);
                    tbObat.setValueAt(NmPoli.getText(), tbObat.getSelectedRow(), 16);
                    tbObat.setValueAt(Status.getSelectedItem().toString(), tbObat.getSelectedRow(), 17);
                    emptTeks();
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengupdate surat kontrol..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda ganti...\nKlik data pada table untuk memilih data...!!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void StatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StatusKeyPressed
        Valid.pindah(evt,TanggalSurat,Diagnosa);
    }//GEN-LAST:event_StatusKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        runBackground(() ->tampil());
    }//GEN-LAST:event_formWindowOpened

    private void DTPCari1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari1ItemStateChanged
        R2.setSelected(true);
    }//GEN-LAST:event_DTPCari1ItemStateChanged

    private void DTPCari3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari3ItemStateChanged
        R3.setSelected(true);
    }//GEN-LAST:event_DTPCari3ItemStateChanged

    private void DTPCari4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari4ItemStateChanged
        R3.setSelected(true);
    }//GEN-LAST:event_DTPCari4ItemStateChanged

    private void Alasan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Alasan1KeyPressed
        Valid.pindah(evt,Terapi,Alasan2);
    }//GEN-LAST:event_Alasan1KeyPressed

    private void Alasan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Alasan2KeyPressed
        Valid.pindah(evt,Alasan1,Rtl1);
    }//GEN-LAST:event_Alasan2KeyPressed

    private void Rtl1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Rtl1KeyPressed
        Valid.pindah(evt,Alasan2,Rtl2);
    }//GEN-LAST:event_Rtl1KeyPressed

    private void Rtl2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Rtl2KeyPressed
        Valid.pindah(evt,Rtl1,BtnDokter);
    }//GEN-LAST:event_Rtl2KeyPressed

    private void TanggalPeriksaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalPeriksaKeyPressed
        Valid.pindah(evt,BtnPoli,NoAntrian);
    }//GEN-LAST:event_TanggalPeriksaKeyPressed

    private void NoAntrianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoAntrianKeyPressed
        Valid.pindah(evt,TanggalPeriksa,NoReg);
    }//GEN-LAST:event_NoAntrianKeyPressed

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        Valid.pindah(evt,Status,Terapi);
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void TerapiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TerapiKeyPressed
        Valid.pindah(evt,Diagnosa,Alasan1);
    }//GEN-LAST:event_TerapiKeyPressed

    private void BtnPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPoliKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPoliActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnDokter,TanggalPeriksa);
        }
    }//GEN-LAST:event_BtnPoliKeyPressed

    private void BtnPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPoliActionPerformed
        if(JADWALDOKTERDIREGISTRASI.equals("aktif")){
            if(akses.getkode().equals("Admin Utama")){
                DlgCariPoli poli=new DlgCariPoli(null,false);
                poli.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}
                    @Override
                    public void windowClosing(WindowEvent e) {}
                    @Override
                    public void windowClosed(WindowEvent e) {
                        if(poli.getTable().getSelectedRow()!= -1){
                            KdPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),0).toString());
                            NmPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),1).toString());
                            isNomer();
                        }
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
                poli.isCek();
                poli.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                poli.setLocationRelativeTo(internalFrame1);
                poli.setVisible(true);
            }else{
                DlgCariPoli2 poli2=new DlgCariPoli2(null,false);
                poli2.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}
                    @Override
                    public void windowClosing(WindowEvent e) {}
                    @Override
                    public void windowClosed(WindowEvent e) {
                        if(poli2.getTable().getSelectedRow()!= -1){
                            KdPoli.setText(poli2.getTable().getValueAt(poli2.getTable().getSelectedRow(),0).toString());
                            NmPoli.setText(poli2.getTable().getValueAt(poli2.getTable().getSelectedRow(),1).toString());
                            isNomer();
                        }
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
                poli2.isCek();
                poli2.SetHari(TanggalPeriksa.getDate());
                poli2.tampil3();
                poli2.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                poli2.setLocationRelativeTo(internalFrame1);
                poli2.setVisible(true);
            }
        }else{
            DlgCariPoli poli=new DlgCariPoli(null,false);
            poli.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {}
                @Override
                public void windowClosed(WindowEvent e) {
                    if(poli.getTable().getSelectedRow()!= -1){
                        KdPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),0).toString());
                        NmPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),1).toString());
                        isNomer();
                    }
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
            poli.isCek();
            poli.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            poli.setLocationRelativeTo(internalFrame1);
            poli.setVisible(true);
        }
    }//GEN-LAST:event_BtnPoliActionPerformed

    private void NoRegKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRegKeyPressed
        Valid.pindah(evt,NoAntrian,BtnSimpan);
    }//GEN-LAST:event_NoRegKeyPressed

    private void MnSuratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSuratActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            TanggalSurat.requestFocus();
        }else if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(!(TPasien.getText().trim().equals(""))){
            if(tbObat.getSelectedRow()!= -1){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Map<String, Object> param = new HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());
                param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
                finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
                param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),14).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),13).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString()));
                Sequel.queryu("delete from temporary_booking_registrasi");
                Sequel.menyimpan("temporary_booking_registrasi","'0','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),0).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),1).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),2).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),3).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),4).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),5).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),6).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),7).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),8).toString()+"','"+
                    TanggalPeriksa.getSelectedItem()+"','"+
                    TanggalSurat.getSelectedItem()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),11).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),12).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),13).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),14).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),15).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),16).toString()+"','"+
                    tabMode.getValueAt(tbObat.getSelectedRow(),17).toString()+"','','','','','','','','','','','','','','','','','','',''","Surat Kotrol");

                Valid.MyReport("rptSuratSKDPBPJS.jasper","report","::[ Surat Kontrol ]::",param);
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
            }
        }
    }//GEN-LAST:event_MnSuratActionPerformed

    private void tbObatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyReleased
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyReleased

    private void btnDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaActionPerformed
        DlgCariPenyakit penyakit=new DlgCariPenyakit(null,false);
        penyakit.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if( penyakit.getTable().getSelectedRow()!= -1){
                    if((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString()).length()<50){
                        Diagnosa.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString());
                    }else{
                        Diagnosa.setText((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString()).substring(0,50));
                    }
                }
                Diagnosa.requestFocus();
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
        penyakit.isCek();
        penyakit.emptTeks();
        penyakit.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penyakit.setLocationRelativeTo(internalFrame1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaActionPerformed

    private void btnDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaKeyPressed
        Valid.pindah(evt,Status,Terapi);
    }//GEN-LAST:event_btnDiagnosaKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SuratKontrol dialog = new SuratKontrol(new javax.swing.JFrame(), true);
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
    private widget.TextBox Alasan1;
    private widget.TextBox Alasan2;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPoli;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPCari3;
    private widget.Tanggal DTPCari4;
    private widget.TextBox Diagnosa;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDokter;
    private widget.TextBox KdPoli;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnSurat;
    private widget.TextBox NmDokter;
    private widget.TextBox NmPoli;
    private widget.TextBox NoAntrian;
    private widget.TextBox NoReg;
    private javax.swing.JPanel PanelInput;
    private widget.RadioButton R1;
    private widget.RadioButton R2;
    private widget.RadioButton R3;
    private widget.TextBox Rtl1;
    private widget.TextBox Rtl2;
    private widget.ScrollPane Scroll;
    private widget.ComboBox Status;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalPeriksa;
    private widget.Tanggal TanggalSurat;
    private widget.TextBox Terapi;
    private widget.Button btnDiagnosa;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel22;
    private widget.Label jLabel25;
    private widget.Label jLabel37;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelCari;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        String status = "";
        if (R1.isSelected()) {
            status = "skdp_bpjs.status = 'Menunggu' ";
        } else if (R2.isSelected()) {
            status = "skdp_bpjs.tanggal_rujukan between ? and ? ";
        } else if (R3.isSelected()) {
            status = "skdp_bpjs.status != 'Menunggu' and skdp_bpjs.tanggal_rujukan between ? and ? ";
        }

        try (PreparedStatement ps = koneksi.prepareStatement(
            "select skdp_bpjs.tahun, skdp_bpjs.no_rkm_medis, pasien.nm_pasien, skdp_bpjs.diagnosa, skdp_bpjs.terapi, skdp_bpjs.alasan1, skdp_bpjs.alasan2, skdp_bpjs.rtl1, " +
            "skdp_bpjs.rtl2, date(skdp_bpjs.tanggal_datang) as tanggal_datang, skdp_bpjs.tanggal_rujukan, skdp_bpjs.no_antrian, skdp_bpjs.kd_dokter, dokter.nm_dokter, " +
            "skdp_bpjs.status from skdp_bpjs join pasien on skdp_bpjs.no_rkm_medis = pasien.no_rkm_medis join dokter on skdp_bpjs.kd_dokter = dokter.kd_dokter " +
            "where " + status + (TCari.getText().isBlank() ? "" : "and (skdp_bpjs.no_rkm_medis like ? or pasien.nm_pasien like ? or skdp_bpjs.diagnosa like ? or " +
            "skdp_bpjs.terapi like ? or skdp_bpjs.no_antrian like ? or skdp_bpjs.kd_dokter like ? or dokter.nm_dokter like ?) ") +
            "order by skdp_bpjs.tanggal_rujukan, skdp_bpjs.no_antrian"
        )) {
            int p = 0;
            if (R2.isSelected()) {
                ps.setString(++p, Valid.getTglSmc(DTPCari1));
                ps.setString(++p, Valid.getTglSmc(DTPCari2));
            } else if (R3.isSelected()) {
                ps.setString(++p, Valid.getTglSmc(DTPCari3));
                ps.setString(++p, Valid.getTglSmc(DTPCari4));
            }
            if (!TCari.getText().isBlank()) {
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kdpoli = "";
                    nmpoli = "";
                    noantri = "";
                    try (PreparedStatement ps2 = koneksi.prepareStatement(
                        "select booking_registrasi.kd_poli, poliklinik.nm_poli, booking_registrasi.no_reg from " +
                        "booking_registrasi join poliklinik on booking_registrasi.kd_poli = poliklinik.kd_poli " +
                        "where booking_registrasi.kd_dokter = ? and booking_registrasi.tanggal_periksa = ? and " +
                        "booking_registrasi.no_rkm_medis = ?"
                    )) {
                        ps2.setString(1, rs.getString("kd_dokter"));
                        ps2.setString(2, rs.getString("tanggal_datang"));
                        ps2.setString(3, rs.getString("no_rkm_medis"));
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            if (rs2.next()) {
                                kdpoli = rs2.getString("kd_poli");
                                nmpoli = rs2.getString("nm_poli");
                                noantri = rs2.getString("no_reg");
                            }
                        }
                    }
                    tabMode.addRow(new Object[] {
                        rs.getInt("tahun"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("diagnosa"), rs.getString("terapi"), rs.getString("alasan1"),
                        rs.getString("alasan2"), rs.getString("rtl1"), rs.getString("rtl2"),
                        rs.getString("tanggal_datang"), rs.getString("tanggal_rujukan"),
                        rs.getString("no_antrian"), noantri, rs.getString("kd_dokter"),
                        rs.getString("nm_dokter"), kdpoli, nmpoli, rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }


    public void emptTeks() {
        KdDokter.setText("");
        NmDokter.setText("");
        KdPoli.setText("");
        NmPoli.setText("");
        Alasan1.setText("");
        Alasan2.setText("");
        Rtl1.setText("");
        Rtl2.setText("");
        Terapi.setText("");
        Diagnosa.setText("");
        TanggalSurat.setDate(new Date());
        TanggalSurat.requestFocus();
        isNomer();
    }

    private void isNomer(){
        switch (URUTNOREG) {
            case "poli":
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_poli = ?), " +
                    "(select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from reg_periksa where " +
                    "reg_periksa.tgl_registrasi = ? and reg_periksa.kd_poli = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdPoli.getText(), Valid.getTglSmc(TanggalPeriksa), KdPoli.getText()));
                break;
            case "dokter":
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_dokter = ?), " +
                    "(select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from reg_periksa where " +
                    "reg_periksa.tgl_registrasi = ? and reg_periksa.kd_dokter = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText()));
                break;
            case "dokter + poli":
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_dokter = ? " +
                    "and booking_registrasi.kd_poli = ?), (select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from " +
                    "reg_periksa where reg_periksa.tgl_registrasi = ? and reg_periksa.kd_dokter = ? and reg_periksa.kd_poli = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText()
                ));
                break;
            default:
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_dokter = ? " +
                    "and booking_registrasi.kd_poli = ?), (select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from " +
                    "reg_periksa where reg_periksa.tgl_registrasi = ? and reg_periksa.kd_dokter = ? and reg_periksa.kd_poli = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText()
                ));
                break;
        }
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(skdp_bpjs.no_antrian,6),signed)),0) from skdp_bpjs where skdp_bpjs.tahun='"+TanggalPeriksa.getSelectedItem().toString().substring(6,10)+"' ","",6,NoAntrian);
    }

    private void isNomorBooking() {
        switch (URUTNOREG) {
            case "poli":
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_poli = ?), " +
                    "(select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from reg_periksa where " +
                    "reg_periksa.tgl_registrasi = ? and reg_periksa.kd_poli = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdPoli.getText(), Valid.getTglSmc(TanggalPeriksa), KdPoli.getText()));
                break;
            case "dokter":
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_dokter = ?), " +
                    "(select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from reg_periksa where " +
                    "reg_periksa.tgl_registrasi = ? and reg_periksa.kd_dokter = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText()));
                break;
            case "dokter + poli":
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_dokter = ? " +
                    "and booking_registrasi.kd_poli = ?), (select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from " +
                    "reg_periksa where reg_periksa.tgl_registrasi = ? and reg_periksa.kd_dokter = ? and reg_periksa.kd_poli = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText()
                ));
                break;
            default:
                NoReg.setText(Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from " +
                    "booking_registrasi where booking_registrasi.tanggal_periksa = ? and booking_registrasi.kd_dokter = ? " +
                    "and booking_registrasi.kd_poli = ?), (select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from " +
                    "reg_periksa where reg_periksa.tgl_registrasi = ? and reg_periksa.kd_dokter = ? and reg_periksa.kd_poli = ?)) + 1, 3, '0')",
                    Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText()
                ));
                break;
        }
        norawat = Sequel.autonomorSmc("", "/", "reg_periksa", "no_rawat", 6, "0", Valid.getTglSmc(TanggalPeriksa));
    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            Diagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Terapi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Alasan1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            Alasan2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            Rtl1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            Rtl2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                TanggalPeriksa.setDate(df.parse(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString()));
                TanggalSurat.setDate(df.parse(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString()));
            } catch (Exception e) {
                Valid.SetTgl2(TanggalPeriksa, tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
                Valid.SetTgl2(TanggalSurat, tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            }
            NoAntrian.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            NoReg.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            KdPoli.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            NmPoli.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            Status.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
        }
    }

    public void setNoRm(String norm,String nama) {
        TNoRM.setText(norm);
        TPasien.setText(nama);
        TCari.setText(norm);
        ChkInput.setSelected(true);
        isForm();
        runBackground(() ->tampil());
    }

    public void setNoRm(String norm,String nama,String kodepoli,String namapoli,String kodedokter,String namadokter) {
        TNoRM.setText(norm);
        TPasien.setText(nama);
        KdPoli.setText(kodepoli);
        NmPoli.setText(namapoli);
        KdDokter.setText(kodedokter);
        NmDokter.setText(namadokter);
        TCari.setText(norm);
        ChkInput.setSelected(true);
        isForm();
        runBackground(() ->tampil());
    }

    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,216));
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
        BtnSimpan.setEnabled(akses.getskdp_bpjs());
        BtnHapus.setEnabled(akses.getskdp_bpjs());
        BtnPrint.setEnabled(akses.getskdp_bpjs());
        BtnEdit.setEnabled(akses.getskdp_bpjs());
    }

    public JTable getTable(){
        return tbObat;
    }

    private void isBooking() {
        isNomer();
        if (Sequel.menyimpantfSmc("skdp_bpjs", null, TanggalPeriksa.getSelectedItem().toString().substring(6, 10), TNoRM.getText(),
            Diagnosa.getText(), Terapi.getText(), Alasan1.getText(), Alasan2.getText(), Rtl1.getText(), Rtl2.getText(), Valid.getTglJamSmc(TanggalPeriksa),
            Valid.getTglJamSmc(TanggalSurat), NoAntrian.getText(), KdDokter.getText(), Status.getSelectedItem().toString()
        )) {
            if (JADIKANBOOKINGSURATKONTROL) {
                if (BOOKINGLANGSUNGREGISTRASI) {
                    String kodePJ = Sequel.cariIsiSmc("select pasien.kd_pj from pasien where pasien.no_rkm_medis = ?", TNoRM.getText()), namaPJ = "", alamatPJ = "",
                        hubunganPJ = "", biayaReg = Sequel.cariIsiSmc("select poliklinik.registrasilama from poliklinik where poliklinik.kd_poli = ?", KdPoli.getText()),
                        statusDaftar = "", umurDaftar = "0", statusUmur = "Th", statusPoli = "", umurPasienRM = "";
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select pasien.namakeluarga, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as almt, pasien.keluarga, " +
                        "timestampdiff(year, pasien.tgl_lahir, ?) as tahun, timestampdiff(month, pasien.tgl_lahir, ?) - ((timestampdiff(month, pasien.tgl_lahir, ?) " +
                        "div 12) * 12) as bulan, timestampdiff(day, date_add(date_add(pasien.tgl_lahir, interval timestampdiff(year, pasien.tgl_lahir, ?) year), " +
                        "interval timestampdiff(month, pasien.tgl_lahir, ?) - ((timestampdiff(month, pasien.tgl_lahir, ?) div 12) * 12) month), ?) as hari, " +
                        "if(exists(select * from reg_periksa where reg_periksa.no_rkm_medis = pasien.no_rkm_medis), 'Lama', 'Baru') as stts_daftar from " +
                        "pasien join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on " +
                        "pasien.kd_kab = kabupaten.kd_kab where pasien.no_rkm_medis = ?"
                    )) {
                        ps.setString(1, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(2, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(3, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(4, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(5, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(6, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(7, Valid.getTglSmc(TanggalPeriksa));
                        ps.setString(8, TNoRM.getText());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                namaPJ = rs.getString("namakeluarga");
                                alamatPJ = rs.getString("almt");
                                hubunganPJ = rs.getString("keluarga");
                                statusDaftar = rs.getString("stts_daftar");
                                statusPoli = statusDaftar.equals("Baru") ? "Baru" : Sequel.cariIsiSmc(
                                    "select if(exists(select * from reg_periksa where reg_periksa.no_rkm_medis = ? and reg_periksa.kd_poli = ?), 'Lama', 'Baru')", TNoRM.getText(), KdPoli.getText()
                                );
                                if (rs.getInt("tahun") > 0) {
                                    umurDaftar = rs.getString("tahun");
                                    statusUmur = "Th";
                                } else if (rs.getInt("tahun") == 0 && rs.getInt("bulan") > 0) {
                                    umurDaftar = rs.getString("bulan");
                                    statusUmur = "Bl";
                                } else if (rs.getInt("tahun") == 0 && rs.getInt("bulan") == 0 && rs.getInt("hari") > 0) {
                                    umurDaftar = rs.getString("hari");
                                    statusUmur = "Hr";
                                }
                                umurPasienRM = rs.getString("tahun") + " Th " + rs.getString("bulan") + " Bl " + rs.getString("hari") + " Hr";
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    boolean sukses = false;
                    int r = 0;
                    do {
                        isNomorBooking();
                        sukses = Sequel.menyimpantfSmc("reg_periksa", null, NoReg.getText(), norawat, Valid.getTglSmc(TanggalPeriksa), Valid.getJamSmc(TanggalSurat),
                            KdDokter.getText(), TNoRM.getText(), KdPoli.getText(), namaPJ, alamatPJ, hubunganPJ, biayaReg, "Belum", statusDaftar,
                            "Ralan", kodePJ, umurDaftar, statusUmur, "Belum bayar", statusPoli
                        );
                    } while (!sukses && r++ < 5);
                    if (sukses) {
                        Sequel.mengupdateSmc("pasien", "umur = ?", "no_rkm_medis = ?", umurPasienRM, TNoRM.getText());
                        Sequel.menyimpanSmc("booking_registrasi", null, Valid.getTglSmc(TanggalSurat), Valid.getJamSmc(TanggalSurat),
                            TNoRM.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText(), NoReg.getText(),
                            kodePJ, "0", null, "Belum", norawat
                        );
                    } else {
                        JOptionPane.showMessageDialog(null, "Tidak dapat membuat booking registrasi pasien..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    Sequel.menyimpanSmc("booking_registrasi", null, Valid.getTglSmc(TanggalSurat), Valid.getJamSmc(TanggalSurat),
                        TNoRM.getText(), Valid.getTglSmc(TanggalPeriksa), KdDokter.getText(), KdPoli.getText(), NoReg.getText(),
                        Sequel.cariIsiSmc("select pasien.kd_pj from pasien where pasien.no_rkm_medis = ?", TNoRM.getText()), "0",
                        null, "Belum", null
                    );
                }
            }
            tabMode.addRow(new Object[] {
                TanggalPeriksa.getSelectedItem().toString().substring(6, 10), TNoRM.getText(), TPasien.getText(), Diagnosa.getText(), Terapi.getText(),
                Alasan1.getText(), Alasan2.getText(), Rtl1.getText(), Rtl2.getText(), Valid.getTglJamSmc(TanggalPeriksa), Valid.getTglJamSmc(TanggalSurat),
                NoAntrian.getText(), NoReg.getText(), KdDokter.getText(), NmDokter.getText(), KdPoli.getText(), NmPoli.getText(), Status.getSelectedItem().toString()
            });
            emptTeks();
            LCount.setText("" + tabMode.getRowCount());
        } else {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat membuat surat kontrol..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

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
