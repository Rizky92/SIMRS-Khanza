/*
 * Kontribusi dari Mas Haris, RS Bhayangkara nganjuk
 */

/*
 * DlgRujuk.java
 *
 * Created on 31 Mei 10, 20:19:56
 */

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


/**
 *
 * @author perpustakaan
 */
public final class PengajuanCutiPegawai extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabMode2;
    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private String tglTMTKerja = "", tglTATkerja = "";
    private long hakCuti = 0;

    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public PengajuanCutiPegawai(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Pengajuan","Pengajuan","Tgl Awal","Tgl Akhir","Jenis Cuti","Alamat Tujuan",
            "Jml Cuti","Kepentingan Cuti","NIK P.J.","P.J. Terkait", "Status"
        }){
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 11; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(85);
            }else if(i==1){
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(65);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(110);
            }else if(i==5){
                column.setPreferredWidth(210);
            }else if(i==6){
                column.setPreferredWidth(80);
            }else if(i==7){
                column.setPreferredWidth(210);
            }else if(i==8){
                column.setPreferredWidth(85);
            }else if(i==9){
                column.setPreferredWidth(170);
            }else if(i==10){
                column.setPreferredWidth(100);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode2=new DefaultTableModel(null,new Object[]{
            "No.Pengajuan", "Pengajuan", "Tgl Awal", "Tgl Akhir", "Jenis Cuti", "Alamat Tujuan",
            "Jml Cuti", "Kepentingan Cuti", "NIK Mengajukan", "Pegawai Mengajukan", "Status"
        }){
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tbObat2.setModel(tabMode2);

        //tbObat2.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat2.getBackground()));
        tbObat2.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 11; i++) {
            TableColumn column = tbObat2.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(85);
            }else if(i==1){
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(65);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(110);
            }else if(i==5){
                column.setPreferredWidth(210);
            }else if(i==6){
                column.setPreferredWidth(80);
            }else if(i==7){
                column.setPreferredWidth(210);
            }else if(i==8){
                column.setPreferredWidth(85);
            }else if(i==9){
                column.setPreferredWidth(170);
            }else if(i==10){
                column.setPreferredWidth(100);
            }
        }
        tbObat2.setDefaultRenderer(Object.class, new WarnaTable());

        Kepentingan.setDocument(new batasInput((int)70).getKata(Kepentingan));
        NoPengajuan.setDocument(new batasInput((int)17).getKata(NoPengajuan));
        Alamat.setDocument(new batasInput((int)100).getKata(Alamat));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        Jumlah.setDocument(new batasInput((byte)3).getOnlyAngka(Jumlah));
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

        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppSetujui = new javax.swing.JMenuItem();
        ppTolak = new javax.swing.JMenuItem();
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
        jLabel5 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        Alamat = new widget.TextArea();
        jLabel3 = new widget.Label();
        NoPengajuan = new widget.TextBox();
        jLabel20 = new widget.Label();
        Urgensi = new widget.ComboBox();
        jLabel4 = new widget.Label();
        Kepentingan = new widget.TextBox();
        jLabel11 = new widget.Label();
        Jumlah = new widget.TextBox();
        jLabel17 = new widget.Label();
        KdPetugasPJ = new widget.TextBox();
        NmPetugasPJ = new widget.TextBox();
        btnPetugasPJ = new widget.Button();
        jLabel14 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        jLabel22 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        jLabel12 = new widget.Label();
        Sisa = new widget.TextBox();
        jLabel13 = new widget.Label();
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
        ppSetujui.setText("Setujui");
        ppSetujui.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSetujui.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSetujui.setName("ppSetujui"); // NOI18N
        ppSetujui.setPreferredSize(new java.awt.Dimension(100, 26));
        ppSetujui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppSetujuiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppSetujui);

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengajuan Cuti Pegawai ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
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
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObatKeyReleased(evt);
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
        DTPCari3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
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
        DTPCari4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
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
        BtnCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari2KeyPressed(evt);
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
        BtnAll2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAll2KeyPressed(evt);
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
        PanelInput.setPreferredSize(new java.awt.Dimension(72, 155));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(55, 165));
        FormInput.setLayout(null);

        jLabel8.setText("Tgl. Pengajuan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(220, 10, 108, 23);

        Tanggal.setEditable(false);
        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TanggalItemStateChanged(evt);
            }
        });
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(332, 10, 90, 23);

        jLabel5.setText("Alamat Tujuan :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(435, 10, 95, 23);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        Alamat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Alamat.setColumns(20);
        Alamat.setRows(5);
        Alamat.setName("Alamat"); // NOI18N
        Alamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlamatKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(Alamat);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(534, 10, 235, 52);

        jLabel3.setText("No. Pengajuan :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 10, 86, 23);

        NoPengajuan.setName("NoPengajuan"); // NOI18N
        FormInput.add(NoPengajuan);
        NoPengajuan.setBounds(90, 10, 125, 23);

        jLabel20.setText("Jenis Cuti :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(561, 70, 85, 23);

        Urgensi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tahunan", "Besar", "Sakit", "Bersalin", "Alasan Penting", "Lainnya" }));
        Urgensi.setName("Urgensi"); // NOI18N
        Urgensi.setPreferredSize(new java.awt.Dimension(55, 28));
        Urgensi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UrgensiKeyPressed(evt);
            }
        });
        FormInput.add(Urgensi);
        Urgensi.setBounds(649, 70, 120, 23);

        jLabel4.setText("Keperluan :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 100, 86, 23);

        Kepentingan.setName("Kepentingan"); // NOI18N
        Kepentingan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KepentinganKeyPressed(evt);
            }
        });
        FormInput.add(Kepentingan);
        Kepentingan.setBounds(90, 100, 679, 23);

        jLabel11.setText(", mengambil");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(300, 70, 57, 23);

        Jumlah.setEditable(false);
        Jumlah.setName("Jumlah"); // NOI18N
        Jumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JumlahKeyPressed(evt);
            }
        });
        FormInput.add(Jumlah);
        Jumlah.setBounds(360, 70, 45, 23);

        jLabel17.setText("P.J.Terkait :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(0, 40, 86, 23);

        KdPetugasPJ.setEditable(false);
        KdPetugasPJ.setName("KdPetugasPJ"); // NOI18N
        KdPetugasPJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasPJKeyPressed(evt);
            }
        });
        FormInput.add(KdPetugasPJ);
        KdPetugasPJ.setBounds(90, 40, 110, 23);

        NmPetugasPJ.setEditable(false);
        NmPetugasPJ.setName("NmPetugasPJ"); // NOI18N
        NmPetugasPJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmPetugasPJKeyPressed(evt);
            }
        });
        FormInput.add(NmPetugasPJ);
        NmPetugasPJ.setBounds(202, 40, 220, 23);

        btnPetugasPJ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugasPJ.setMnemonic('2');
        btnPetugasPJ.setToolTipText("Alt+2");
        btnPetugasPJ.setName("btnPetugasPJ"); // NOI18N
        btnPetugasPJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasPJActionPerformed(evt);
            }
        });
        btnPetugasPJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasPJKeyPressed(evt);
            }
        });
        FormInput.add(btnPetugasPJ);
        btnPetugasPJ.setBounds(425, 40, 28, 23);

        jLabel14.setText("Dari Tanggal :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 70, 86, 23);

        Tgl1.setEditable(false);
        Tgl1.setForeground(new java.awt.Color(50, 70, 50));
        Tgl1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setOpaque(false);
        Tgl1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Tgl1ItemStateChanged(evt);
            }
        });
        Tgl1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Tgl1KeyPressed(evt);
            }
        });
        FormInput.add(Tgl1);
        Tgl1.setBounds(90, 70, 90, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("s.d.");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(185, 70, 19, 23);

        Tgl2.setEditable(false);
        Tgl2.setForeground(new java.awt.Color(50, 70, 50));
        Tgl2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "22-06-2026" }));
        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setOpaque(false);
        Tgl2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Tgl2ItemStateChanged(evt);
            }
        });
        Tgl2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Tgl2KeyPressed(evt);
            }
        });
        FormInput.add(Tgl2);
        Tgl2.setBounds(207, 70, 90, 23);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("dari sisa cuti");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(408, 70, 60, 23);

        Sisa.setEditable(false);
        Sisa.setName("Sisa"); // NOI18N
        Sisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SisaKeyPressed(evt);
            }
        });
        FormInput.add(Sisa);
        Sisa.setBounds(471, 70, 45, 23);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("hari");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(520, 70, 20, 23);

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

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(NoPengajuan.getText().trim().equals("")){
            Valid.textKosong(NoPengajuan,"No.Pengajuan");
        }else if(NmPetugas.getText().trim().equals("")){
            Valid.textKosong(KdPetugas,"Yang Mengajukan");
        }else if(Jumlah.getText().trim().equals("")||Jumlah.getText().trim().equals("0")){
            Valid.textKosong(Jumlah,"Jml Cuti");
        }else if(Alamat.getText().trim().equals("")){
            Valid.textKosong(Alamat,"Alamat Tujuan");
        }else if(Kepentingan.getText().trim().equals("")){
            Valid.textKosong(Kepentingan,"Kepentingan Cuti");
        }else if(NmPetugasPJ.getText().trim().equals("")){
            Valid.textKosong(KdPetugasPJ,"P.J. terkait pengajuan");
        } else if (Integer.parseInt(Jumlah.getText().trim()) >= Integer.parseInt(Sisa.getText().trim())) {
            JOptionPane.showMessageDialog(null, "Maaf, sisa cuti anda sudah habis..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (Local) {
            JOptionPane.showMessageDialog(null, "Maaf, tanggal awal cuti sebelum tanggal TMT masa kerja (" + tglTMTkerja + ")!!");
        } else if () {
            JOptionPane.showMessageDialog(null, "Maaf, tanggal akhir cuti melewati tanggal TAT masa kerja (" + tglTATkerja + ")!!");
        }else{
            if (Sequel.menyimpantfSmc("pengajuan_cuti", "", NoPengajuan.getText().trim(), Valid.getTglSmc(Tanggal), Valid.getTglSmc(Tgl1),
                Valid.getTglSmc(Tgl2), tglTMTKerja, tglTATkerja, KdPetugas.getText(), Urgensi.getSelectedItem().toString(), Alamat.getText(),
                Jumlah.getText(), Sisa.getText(), Kepentingan.getText().trim(), KdPetugasPJ.getText(), "Proses Pengajuan"
            )) {
                TabRawat.setSelectedIndex(0);
                tampilSmc();
                emptTeks();
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,Kepentingan,BtnBatal);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm();
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()> -1){
            if(!tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals("Disetujui")){
                if (Sequel.menghapustfSmc("pengajuan_cuti", "no_pengajuan = ?", tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString())) {
                    tampilSmc();
                    emptTeks();
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus pengajuan cuti..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, sudah disetujui. Tidak boleh dihapus/dirubah... !");
            }
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
        if(NoPengajuan.getText().trim().equals("")){
            Valid.textKosong(NoPengajuan,"No.Pengajuan");
        }else if(NmPetugas.getText().trim().equals("")){
            Valid.textKosong(KdPetugas,"Yang Mengajukan");
        }else if(Alamat.getText().trim().equals("")){
            Valid.textKosong(Alamat,"Alamat Tujuan");
        }else if(Jumlah.getText().trim().equals("")||Jumlah.getText().trim().equals("0")){
            Valid.textKosong(Jumlah,"Jml Cuti");
        }else if(Kepentingan.getText().trim().equals("")){
            Valid.textKosong(Kepentingan,"Kepentingan Cuti");
        }else if(NmPetugasPJ.getText().trim().equals("")){
            Valid.textKosong(KdPetugasPJ,"P.J. terkait pengajuan");
        } else if (Integer.parseInt(Jumlah.getText().trim()) >= Integer.parseInt(Sisa.getText().trim())) {
            JOptionPane.showMessageDialog(null, "Maaf, sisa cuti anda sudah habis..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }else{
            if(tbObat.getSelectedRow()> -1){
                if(!tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals("Disetujui")){
                    if (Sequel.mengupdatetfSmc("pengajuan_cuti", "no_pengajuan = ?, tanggal = ?, tanggal_awal = ?, tanggal_akhir = ?, tmt_awal = ?, tmt_akhir = ?, nik = ?, urgensi = ?, " +
                        "alamat = ?, jumlah = ?, sisa_cuti = ?, kepentingan = ?, nik_pj = ?", "no_pengajuan = ?", NoPengajuan.getText(), Valid.getTglSmc(Tanggal), Valid.getTglSmc(Tgl1),
                        Valid.getTglSmc(Tgl2), KdPetugas.getText(), Urgensi.getSelectedItem().toString(), Alamat.getText().trim(), Jumlah.getText().trim(), Kepentingan.getText().trim(),
                        KdPetugasPJ.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
                    )) {
                        tampilSmc();
                        emptTeks();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Maaf, sudah disetujui. Tidak boleh dihapus/dirubah... !");
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
                    bw.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.head td{border-right: 1px solid #777777;font: 8.5px tahoma;height:10px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi a{text-decoration:none;color:#8b9b95;padding:0 0 0 0px;font-family: Tahoma;font-size: 8.5px;}.isi2 td{font: 8.5px tahoma;height:12px;background: #ffffff;color:#323232;}.isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}");
                    bw.flush();
                }
                String pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih laporan..!", "Pilihan Cetak", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
                    "Laporan 1 (HTML)", "Laporan 2 (WPS)", "Laporan 3 (CSV)", "Laporan 4 (XLSX)", "Laporan 5 (Jasper)"
                }, "Laporan 5 (Jasper)");
                switch (pilihan) {
                    case "Laporan 1 (HTML)":
                        Valid.exportHtmlSmc("PengajuanCutiPegawai.html", "Data Pengajuan Cuti", tbObat);
                        break;
                    case "Laporan 2 (WPS)":
                        Valid.exportWPSSmc("PengajuanCutiPegawai.wps", "Data Pengajuan Cuti", tbObat);
                        break;
                    case "Laporan 3 (CSV)":
                        Valid.exportCSVSmc("PengajuanCutiPegawai.csv", tbObat);
                        break;
                    case "Laporan 4 (XLSX)":
                        Valid.exportXlsxSmc("PengajuanCutiPegawai.xlsx", tbObat);
                        break;
                    case "Laporan 5 (Jasper)":
                        Map<String, Object> param = new HashMap<>();
                        param.put("namars",akses.getnamars());
                        param.put("alamatrs",akses.getalamatrs());
                        param.put("kotars",akses.getkabupatenrs());
                        param.put("propinsirs",akses.getpropinsirs());
                        param.put("kontakrs",akses.getkontakrs());
                        param.put("emailrs",akses.getemailrs());
                        param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
                        if(TCari.getText().equals("")){
                            Valid.MyReportqry("rptPengajuanCutiPegawai.jasper","report","::[ Data Pengajuan Cuti ]::",
                                    "select pengajuan_cuti.no_pengajuan,pengajuan_cuti.tanggal,pengajuan_cuti.tanggal_awal,pengajuan_cuti.tanggal_akhir,"+
                                    "pengajuan_cuti.urgensi,pengajuan_cuti.alamat,pengajuan_cuti.jumlah,"+
                                    "pengajuan_cuti.kepentingan,pengajuan_cuti.nik_pj,pegawai.nama,pengajuan_cuti.status "+
                                    "from pengajuan_cuti inner join pegawai on pengajuan_cuti.nik_pj=pegawai.nik where "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' order by pengajuan_cuti.tanggal",param);
                        }else{
                            Valid.MyReportqry("rptPengajuanCutiPegawai.jasper","report","::[ Data Pengajuan Cuti ]::",
                                    "select pengajuan_cuti.no_pengajuan,pengajuan_cuti.tanggal,pengajuan_cuti.tanggal_awal,pengajuan_cuti.tanggal_akhir,"+
                                    "pengajuan_cuti.urgensi,pengajuan_cuti.alamat,pengajuan_cuti.jumlah,"+
                                    "pengajuan_cuti.kepentingan,pengajuan_cuti.nik_pj,pegawai.nama,pengajuan_cuti.status "+
                                    "from pengajuan_cuti inner join pegawai on pengajuan_cuti.nik_pj=pegawai.nik where "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pengajuan_cuti.no_pengajuan like '%"+TCari.getText().trim()+"%' or "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pengajuan_cuti.nik_pj like '%"+TCari.getText().trim()+"%' or "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pegawai.nama like '%"+TCari.getText().trim()+"%' or "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pengajuan_cuti.urgensi like '%"+TCari.getText().trim()+"%' or "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pengajuan_cuti.alamat like '%"+TCari.getText().trim()+"%' or "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pengajuan_cuti.kepentingan like '%"+TCari.getText().trim()+"%' or "+
                                    "pengajuan_cuti.nik='"+KdPetugas.getText()+"' and pengajuan_cuti.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' and pengajuan_cuti.status like '%"+TCari.getText().trim()+"%' "+
                                    "order by pengajuan_cuti.tanggal",param);
                        }
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
        tampilSmc();
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
        tampilSmc();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampilSmc();
        }else{
            Valid.pindah(evt, BtnCari,BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        Valid.pindah(evt,TCari,Tgl1);
    }//GEN-LAST:event_TanggalKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void KdPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPetugasKeyPressed
        Valid.pindah(evt,TCari,Tanggal);
    }//GEN-LAST:event_KdPetugasKeyPressed

    private void NmPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmPetugasKeyPressed
        //Valid.pindah(evt,TKd,TSpek);
    }//GEN-LAST:event_NmPetugasKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        emptTeks();
        hitungHari();
        tampilSmc();
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampilSmc();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampilSmc();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampilSmc();
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    private void KdPetugasPJKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPetugasPJKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdPetugasPJKeyPressed

    private void NmPetugasPJKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmPetugasPJKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NmPetugasPJKeyPressed

    private void btnPetugasPJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasPJActionPerformed
        DlgCariPegawai petugas=new DlgCariPegawai(null,false);
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){
                    KdPetugasPJ.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    NmPetugasPJ.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
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
        petugas.setDepartemen(akses.getkode());
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasPJActionPerformed

    private void KepentinganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KepentinganKeyPressed
        Valid.pindah(evt,Urgensi,BtnSimpan);
    }//GEN-LAST:event_KepentinganKeyPressed

    private void UrgensiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UrgensiKeyPressed
        Valid.pindah(evt,Jumlah,Kepentingan);
    }//GEN-LAST:event_UrgensiKeyPressed

    private void JumlahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JumlahKeyPressed
        Valid.pindah(evt,Tgl2,Urgensi);
    }//GEN-LAST:event_JumlahKeyPressed

    private void AlamatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlamatKeyPressed
        Valid.pindah(evt,Tanggal,Tgl1);
    }//GEN-LAST:event_AlamatKeyPressed

    private void btnPetugasPJKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasPJKeyPressed
        Valid.pindah(evt,Tgl2,Urgensi);
    }//GEN-LAST:event_btnPetugasPJKeyPressed

    private void Tgl1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tgl1KeyPressed
        Valid.pindah(evt,Alamat,Tgl2);
    }//GEN-LAST:event_Tgl1KeyPressed

    private void Tgl2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tgl2KeyPressed
        Valid.pindah(evt,Tgl1,Jumlah);
    }//GEN-LAST:event_Tgl2KeyPressed

    private void Tgl2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Tgl2ItemStateChanged
        hitungHari();
    }//GEN-LAST:event_Tgl2ItemStateChanged

    private void Tgl1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Tgl1ItemStateChanged
        hitungHari();
    }//GEN-LAST:event_Tgl1ItemStateChanged

    private void ppSetujuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppSetujuiActionPerformed
        if (!"Disetujui".equals(tbObat2.getValueAt(tbObat2.getSelectedRow(), 10).toString())) {
            if (Sequel.mengupdatetfSmc("pengajuan_cuti", "status = 'Disetujui'", "no_pengajuan = ?", tbObat2.getValueAt(tbObat2.getSelectedRow(), 0).toString())) {
                tabMode2.setValueAt("Disetujui", tbObat2.getSelectedRow(), 10);
            }
        }
    }//GEN-LAST:event_ppSetujuiActionPerformed

    private void ppTolakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppTolakActionPerformed
        if (!"Ditolak".equals(tbObat2.getValueAt(tbObat2.getSelectedRow(), 10).toString())) {
            if (Sequel.mengupdatetfSmc("pengajuan_cuti", "status = 'Ditolak'", "no_pengajuan = ?", tbObat2.getValueAt(tbObat2.getSelectedRow(), 0).toString())) {
                tabMode2.setValueAt("Ditolak", tbObat2.getSelectedRow(), 10);
            }
        }
    }//GEN-LAST:event_ppTolakActionPerformed

    private void SisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SisaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SisaKeyPressed

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

    private void BtnCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCari2ActionPerformed(null);
        } else {
            Valid.pindahSmc(evt, TCari2, BtnAll2);
        }
    }//GEN-LAST:event_BtnCari2KeyPressed

    private void BtnAll2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAll2ActionPerformed
        TCari2.setText("");
        BtnCari2ActionPerformed(null);
    }//GEN-LAST:event_BtnAll2ActionPerformed

    private void BtnAll2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAll2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnAll2ActionPerformed(null);
        } else {
            Valid.pindahSmc(evt, BtnCari2, TabRawat);
        }
    }//GEN-LAST:event_BtnAll2KeyPressed

    private void tbObat2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObat2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbObat2MouseClicked

    private void tbObat2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObat2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbObat2KeyReleased

    private void TanggalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TanggalItemStateChanged
        autoNomor();
        if (LocalDate.ofInstant(Tgl1.getDate().toInstant(), ZoneId.systemDefault()).getYear() != LocalDate.ofInstant(Tanggal.getDate().toInstant(), ZoneId.systemDefault()).getYear()) {
            hitungSisaCuti();
        }
    }//GEN-LAST:event_TanggalItemStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            PengajuanCutiPegawai dialog = new PengajuanCutiPegawai(new javax.swing.JFrame(), true);
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
    private widget.TextArea Alamat;
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
    private widget.PanelBiasa FormInput;
    private widget.TextBox Jumlah;
    private widget.TextBox KdPetugas;
    private widget.TextBox KdPetugasPJ;
    private widget.TextBox Kepentingan;
    private widget.Label LCount;
    private widget.Label LCount1;
    private widget.Label LCount2;
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
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.ComboBox Urgensi;
    private widget.Button btnPetugasPJ;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
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
    private widget.Label jLabel5;
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
    private javax.swing.JMenuItem ppTolak;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbObat;
    private widget.Table tbObat2;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        int i = 0;
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().equals("")){
                ps=koneksi.prepareStatement(
                   "select pengajuan_cuti.no_pengajuan,pengajuan_cuti.tanggal,pengajuan_cuti.tanggal_awal,pengajuan_cuti.tanggal_akhir,"+
                   "pengajuan_cuti.urgensi,pengajuan_cuti.alamat,pengajuan_cuti.jumlah,"+
                   "pengajuan_cuti.kepentingan,pengajuan_cuti.nik_pj,pegawai.nama,pengajuan_cuti.status "+
                   "from pengajuan_cuti inner join pegawai on pengajuan_cuti.nik_pj=pegawai.nik where "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? order by pengajuan_cuti.tanggal");
            }else{
                ps=koneksi.prepareStatement(
                   "select pengajuan_cuti.no_pengajuan,pengajuan_cuti.tanggal,pengajuan_cuti.tanggal_awal,pengajuan_cuti.tanggal_akhir,"+
                   "pengajuan_cuti.urgensi,pengajuan_cuti.alamat,pengajuan_cuti.jumlah,"+
                   "pengajuan_cuti.kepentingan,pengajuan_cuti.nik_pj,pegawai.nama,pengajuan_cuti.status "+
                   "from pengajuan_cuti inner join pegawai on pengajuan_cuti.nik_pj=pegawai.nik where "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pengajuan_cuti.no_pengajuan like ? or "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pengajuan_cuti.nik_pj like ? or "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pegawai.nama like ? or "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pengajuan_cuti.urgensi like ? or "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pengajuan_cuti.alamat like ? or "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pengajuan_cuti.kepentingan like ? or "+
                   "pengajuan_cuti.nik=? and pengajuan_cuti.tanggal between ? and ? and pengajuan_cuti.status like ? "+
                   "order by pengajuan_cuti.tanggal");
            }

            try {
                if(TCari.getText().equals("")){
                    ps.setString(1,KdPetugas.getText());
                    ps.setString(2,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(3,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                }else{
                    ps.setString(1,KdPetugas.getText());
                    ps.setString(2,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(3,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(4,"%"+TCari.getText().trim()+"%");
                    ps.setString(5,KdPetugas.getText());
                    ps.setString(6,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(7,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(8,"%"+TCari.getText().trim()+"%");
                    ps.setString(9,KdPetugas.getText());
                    ps.setString(10,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(11,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(12,"%"+TCari.getText().trim()+"%");
                    ps.setString(13,KdPetugas.getText());
                    ps.setString(14,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(15,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(16,"%"+TCari.getText().trim()+"%");
                    ps.setString(17,KdPetugas.getText());
                    ps.setString(18,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(19,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(20,"%"+TCari.getText().trim()+"%");
                    ps.setString(21,KdPetugas.getText());
                    ps.setString(22,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(23,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(24,"%"+TCari.getText().trim()+"%");
                    ps.setString(25,KdPetugas.getText());
                    ps.setString(26,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(27,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    ps.setString(28,"%"+TCari.getText().trim()+"%");
                }

                rs=ps.executeQuery();
                i=0;
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_pengajuan"),rs.getString("tanggal"),rs.getString("tanggal_awal"),rs.getString("tanggal_akhir"),
                        rs.getString("urgensi"),rs.getString("alamat"),rs.getString("jumlah"),rs.getString("kepentingan"),rs.getString("nik_pj"),
                        rs.getString("nama"),rs.getString("status")
                    });
                    i=i+rs.getInt("jumlah");
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
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
        LCount1.setText(Valid.SetAngka(i));
    }

    private void emptTeks() {
        Tanggal.setDate(new Date());
        Tgl1.setDate(new Date());
        Tgl2.setDate(new Date());
        Alamat.setText("");
        Jumlah.setText("0");
        Kepentingan.setText("");
        autoNomor();
        Alamat.requestFocus();
    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            NoPengajuan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            Valid.SetTgl(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            Valid.SetTgl(Tgl1,tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            Valid.SetTgl(Tgl2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Urgensi.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Alamat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            Jumlah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            Kepentingan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            KdPetugasPJ.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            NmPetugasPJ.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
        }
    }

    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,155));
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
        if (!akses.getadmin()) {
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(Sequel.CariPetugas(KdPetugas.getText()));
            hitungSisaCuti();
        }
    }

    private void autoNomor() {
        /*
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(pengajuan_cuti.no_pengajuan,3),signed)),0) from pengajuan_cuti where pengajuan_cuti.tanggal='"+Valid.SetTgl(Tanggal.getSelectedItem()+"")+"' ",
                "PC"+Tanggal.getSelectedItem().toString().substring(6,10)+Tanggal.getSelectedItem().toString().substring(3,5)+Tanggal.getSelectedItem().toString().substring(0,2),3,NoPengajuan);
        */
        Valid.autonomor1Smc(NoPengajuan, "PC", "pengajuan_cuti", "no_pengajuan", 3, "0", Tanggal);
    }

    private void tampilSmc() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode);
            new SwingWorker<Void, Object[]>() {
                final String cari = TCari.getText().trim();
                int jumlahCuti = 0;

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select pengajuan_cuti.no_pengajuan, pengajuan_cuti.tanggal, pengajuan_cuti.tanggal_awal, pengajuan_cuti.tanggal_akhir, pengajuan_cuti.tmt_awal, " +
                        "pengajuan_cuti.tmt_akhir, pengajuan_cuti.urgensi, pengajuan_cuti.alamat, pengajuan_cuti.jumlah, pengajuan_cuti.sisa_cuti, pengajuan_cuti.kepentingan, " +
                        "pengajuan_cuti.nik_pj, pegawai.nama, pengajuan_cuti.status from pengajuan_cuti inner join pegawai on pengajuan_cuti.nik_pj = pegawai.nik where " +
                        "pengajuan_cuti.nik = ? and pengajuan_cuti.tanggal between ? and ? " + (cari.isBlank() ? "" : "and (pengajuan_cuti.no_pengajuan like ? or " +
                        "pengajuan_cuti.nik_pj like ? or pegawai.nama like ? or pengajuan_cuti.urgensi like ? or pengajuan_cuti.alamat like ? or " +
                        "pengajuan_cuti.kepentingan like ? or pengajuan_cuti.status like ?) ") + "order by pengajuan_cuti.tanggal"
                    )) {
                        int p = 0;
                        ps.setString(++p, KdPetugas.getText());
                        ps.setString(++p, Valid.getTglSmc(DTPCari1));
                        ps.setString(++p, Valid.getTglSmc(DTPCari2));
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
                                jumlahCuti += rs.getInt("jumlah");
                                publish(new Object[] {
                                    rs.getString("no_pengajuan"), rs.getString("tanggal"), rs.getString("tanggal_awal"), rs.getString("tanggal_akhir"),
                                    rs.getString("urgensi"), rs.getString("alamat"), rs.getString("jumlah"), rs.getString("kepentingan"),
                                    rs.getString("nik_pj"), rs.getString("nama"), rs.getString("status")
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
                    LCount1.setText(jumlahCuti + "");
                    PengajuanCutiPegawai.this.setCursor(Cursor.getDefaultCursor());
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
                final String cari = TCari.getText().trim();
                int jumlahCuti = 0;

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select pengajuan_cuti.no_pengajuan, pengajuan_cuti.tanggal, pengajuan_cuti.tanggal_awal, pengajuan_cuti.tanggal_akhir, pengajuan_cuti.urgensi, " +
                        "pengajuan_cuti.alamat, pengajuan_cuti.jumlah, pengajuan_cuti.kepentingan, pengajuan_cuti.nik, pegawai.nama, pengajuan_cuti.status from " +
                        "pengajuan_cuti inner join pegawai on pengajuan_cuti.nik = pegawai.nik where pengajuan_cuti.nik_pj = ? and pengajuan_cuti.tanggal between ? and ? " +
                        (cari.isBlank() ? "" : "and (pengajuan_cuti.no_pengajuan like ? or pengajuan_cuti.nik_pj like ? or pegawai.nama like ? or pengajuan_cuti.urgensi " +
                        "like ? or pengajuan_cuti.alamat like ? or pengajuan_cuti.kepentingan like ? or pengajuan_cuti.status like ?) ") + "order by pengajuan_cuti.tanggal"
                    )) {
                        int p = 0;
                        ps.setString(++p, KdPetugas.getText());
                        ps.setString(++p, Valid.getTglSmc(DTPCari3));
                        ps.setString(++p, Valid.getTglSmc(DTPCari4));
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
                                jumlahCuti += rs.getInt("jumlah");
                                publish(new Object[] {
                                    rs.getString("no_pengajuan"), rs.getString("tanggal"), rs.getString("tanggal_awal"), rs.getString("tanggal_akhir"),
                                    rs.getString("urgensi"), rs.getString("alamat"), rs.getString("jumlah"), rs.getString("kepentingan"),
                                    rs.getString("nik"), rs.getString("nama"), rs.getString("status")
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
                    LCount2.setText(jumlahCuti + "");
                    PengajuanCutiPegawai.this.setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }

    private void hitungSisaCuti() {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "with datapegawai as (select pegawai.nik, stts_kerja.hakcuti, date_sub(makedate(year(?), dayofyear(pegawai.mulai_kerja)), interval 1 year) " +
            "as tmt_awal, date_sub(makedate(year(?), dayofyear(pegawai.mulai_kerja)), interval 1 day) as tmt_akhir from pegawai inner join stts_kerja " +
            "on pegawai.stts_kerja = stts_kerja.stts where pegawai.nik = ?) select datapegawai.*, ifnull(sum(pengajuan_cuti.jumlah), 0) as diambil " +
            "from datapegawai left join pengajuan_cuti on pengajuan_cuti.nik = datapegawai.nik and pengajuan_cuti.tmt_awal = datapegawai.tmt_awal and " +
            "pengajuan_cuti.tmt_akhir = datapegawai.tmt_akhir and pengajuan_cuti.status != 'Ditolak' " +
            (tbObat.getSelectedRow() < 0 ? "" : "and pengajuan_cuti.no_pengajuan != ?")
        )) {
            int p = 0;
            ps.setString(++p, Valid.getTglSmc(Tanggal));
            ps.setString(++p, Valid.getTglSmc(Tanggal));
            ps.setString(++p, KdPetugas.getText());
            if (tbObat.getSelectedRow() >= 0) {
                ps.setString(++p, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tglTMTKerja = rs.getString("tmt_awal");
                    tglTATkerja = rs.getString("tmt_akhir");
                    hakCuti = rs.getLong("hakcuti") - rs.getLong("diambil");
                    Sisa.setText(String.valueOf(hakCuti));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
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

    private void cekCutoffTAT() {
        LocalDate awal = LocalDate.ofInstant(Tgl1.getDate().toInstant(), ZoneId.systemDefault());
        LocalDate tat = LocalDate.parse(tglTATkerja);
    }

    public void hitungHari() {
        long hari = 1;
        if ((Tgl1.getDate().getTime() / 1000) >= (Tgl2.getDate().getTime() / 1000)) {
            hari = 1;
        } else {
            hari = 1 + TimeUnit.DAYS.convert(Tgl2.getDate().getTime() - Tgl1.getDate().getTime(), TimeUnit.MILLISECONDS);
        }
        Jumlah.setText(hari + "");
    }

    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
