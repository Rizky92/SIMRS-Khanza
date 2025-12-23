/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPenyakit.java
 *
 * Created on May 23, 2010, 12:57:16 AM
 */

package keuangan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import simrskhanza.DlgKtgPerawatan;

/**
 *
 * @author dosen
 */
public final class DlgCariPerawatanRanap2 extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement psinputrawatdr,psinputrawatpr,psinputrawatdrpr,pstarif,pscari,pscari2,pscari3,pscari4,pscari5,pscari6,pscari7,pscari8,
                              pstindakan,pstindakan2,pstindakan3,pshapustindakan,pshapustindakan2,pshapustindakan3,psrekening;
    private ResultSet rs,rstindakan,rstarif,rsrekening;
    private String pilihtable="",kd_pj="",kd_bangsal="",ruang_ranap="Yes", cara_bayar_ranap="Yes",kelas="",kelas_ranap="Yes";
    private boolean[] pagi,siang,sore,malam;
    private boolean pg=false,sg=false,sr=false,mlm=false;
    private boolean sukses=false;
    private String[] kode,nama,kategori,kelastarif;
    private double[] totaltnd,bagianrs,bhp,jmdokter,jmperawat,kso,menejemen;
    private int jml=0,i=0,index=0;
    public  DlgCariDokter dokter=new DlgCariDokter(null,false);
    public  DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private double ttljmdokter=0,ttljmperawat=0,ttlkso=0,ttlpendapatan=0,ttljasasarana=0,ttlbhp=0,ttlmenejemen=0,
            hapusttljasasarana=0,hapusttlbhp=0,hapusttlmenejemen=0,hapusttljmdokter=0,hapusttljmperawat=0,hapusttlkso=0,hapusttlpendapatan=0;
    private Jurnal jur=new Jurnal();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private String Suspen_Piutang_Tindakan_Ranap="",Tindakan_Ranap="",Beban_Jasa_Medik_Dokter_Tindakan_Ranap="",Utang_Jasa_Medik_Dokter_Tindakan_Ranap="",
            Beban_Jasa_Medik_Paramedis_Tindakan_Ranap="",Utang_Jasa_Medik_Paramedis_Tindakan_Ranap="",Beban_KSO_Tindakan_Ranap="",Utang_KSO_Tindakan_Ranap="",
            Beban_Jasa_Sarana_Tindakan_Ranap="",Utang_Jasa_Sarana_Tindakan_Ranap="",Beban_Jasa_Menejemen_Tindakan_Ranap="",Utang_Jasa_Menejemen_Tindakan_Ranap="",
            HPP_BHP_Tindakan_Ranap="",Persediaan_BHP_Tindakan_Ranap="", where;
    private final ObjectMapper mapper = new ObjectMapper();
    private ObjectNode tindakanTerinput;

    /** Creates new form DlgPenyakit
     * @param parent
     * @param modal */
    public DlgCariPerawatanRanap2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(656,250);

        Object[] row={"Pagi","Siang","Sore","Malam","Kode","Nama Perawatan","Kategori Perawatan",
                      "Tarif/Biaya","Bagian RS","BHP","JM Dokter","JM Perawat","KSO","Menejemen","Kelas"};
        tabMode=new DefaultTableModel(null,row){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==1)||(colIndex==2)||(colIndex==3)) {
                    a=true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class,java.lang.Boolean.class,java.lang.Boolean.class,java.lang.Boolean.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,java.lang.Double.class,
                java.lang.Double.class, java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,
                java.lang.Double.class,java.lang.Double.class, java.lang.Object.class
            };
            /*Class[] types = new Class[] {
            java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
            };*/
            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        };
        tbKamar.setModel(tabMode);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 15; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(35);
            }else if(i==1){
                column.setPreferredWidth(35);
            }else if(i==2){
                column.setPreferredWidth(35);
            }else if(i==3){
                column.setPreferredWidth(35);
            }else if(i==4){
                column.setPreferredWidth(90);
            }else if(i==5){
                column.setPreferredWidth(300);
            }else if(i==6){
                column.setPreferredWidth(150);
            }else if(i==7){
                column.setPreferredWidth(120);
            }else if(i==14){
                column.setPreferredWidth(75);
            }else{
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
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

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    kddokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    nmdokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                }
                kddokter.requestFocus();
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

        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){
                    switch (pilihtable) {
                        case "rawat_inap_pr":
                            kddokter.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                            nmdokter.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                            kddokter.requestFocus();
                            break;
                        case "rawat_inap_drpr":
                            KdPtg2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                            NmPtg2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                            KdPtg2.requestFocus();
                            break;
                    }
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
        
        TCari.requestFocus();
        try {
            psrekening=koneksi.prepareStatement("select * from set_akun_ranap");
            try {
                rsrekening=psrekening.executeQuery();
                while(rsrekening.next()){
                    Suspen_Piutang_Tindakan_Ranap=rsrekening.getString("Suspen_Piutang_Tindakan_Ranap");
                    Tindakan_Ranap=rsrekening.getString("Tindakan_Ranap");
                    Beban_Jasa_Medik_Dokter_Tindakan_Ranap=rsrekening.getString("Beban_Jasa_Medik_Dokter_Tindakan_Ranap");
                    Utang_Jasa_Medik_Dokter_Tindakan_Ranap=rsrekening.getString("Utang_Jasa_Medik_Dokter_Tindakan_Ranap");
                    Beban_Jasa_Medik_Paramedis_Tindakan_Ranap=rsrekening.getString("Beban_Jasa_Medik_Paramedis_Tindakan_Ranap");
                    Utang_Jasa_Medik_Paramedis_Tindakan_Ranap=rsrekening.getString("Utang_Jasa_Medik_Paramedis_Tindakan_Ranap");
                    Beban_KSO_Tindakan_Ranap=rsrekening.getString("Beban_KSO_Tindakan_Ranap");
                    Utang_KSO_Tindakan_Ranap=rsrekening.getString("Utang_KSO_Tindakan_Ranap");
                    Beban_Jasa_Sarana_Tindakan_Ranap=rsrekening.getString("Beban_Jasa_Sarana_Tindakan_Ranap");
                    Utang_Jasa_Sarana_Tindakan_Ranap=rsrekening.getString("Utang_Jasa_Sarana_Tindakan_Ranap");
                    Beban_Jasa_Menejemen_Tindakan_Ranap=rsrekening.getString("Beban_Jasa_Menejemen_Tindakan_Ranap");
                    Utang_Jasa_Menejemen_Tindakan_Ranap=rsrekening.getString("Utang_Jasa_Menejemen_Tindakan_Ranap");
                    HPP_BHP_Tindakan_Ranap=rsrekening.getString("HPP_BHP_Tindakan_Ranap");
                    Persediaan_BHP_Tindakan_Ranap=rsrekening.getString("Persediaan_BHP_Tindakan_Ranap");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : "+e);
            } finally{
                if(rsrekening!=null){
                    rsrekening.close();
                }
                if(psrekening!=null){
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            pstarif=koneksi.prepareStatement("select * from set_tarif");
            try {
                rstarif=pstarif.executeQuery();
                if(rstarif.next()){
                    ruang_ranap=rstarif.getString("ruang_ranap");
                    cara_bayar_ranap=rstarif.getString("cara_bayar_ranap");
                    kelas_ranap=rstarif.getString("kelas_ranap");
                }else{
                    ruang_ranap="Yes";
                    cara_bayar_ranap="Yes";
                    kelas_ranap="Yes";
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rstarif != null){
                    rstarif.close();
                }
                if(pstarif != null){
                    pstarif.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
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

        Popup = new javax.swing.JPopupMenu();
        ppBersihkan = new javax.swing.JMenuItem();
        ppDokter = new javax.swing.JMenuItem();
        ppPetugas = new javax.swing.JMenuItem();
        ppPetugasDokter = new javax.swing.JMenuItem();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelisi3 = new widget.panelisi();
        jLabel4 = new widget.Label();
        KdKtg = new widget.TextBox();
        NmKtg = new widget.TextBox();
        btnKategori = new widget.Button();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnTambah = new widget.Button();
        BtnSimpan = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        FormInput = new widget.PanelBiasa();
        DTPTgl = new widget.Tanggal();
        jLabel5 = new widget.Label();
        kddokter = new widget.TextBox();
        nmdokter = new widget.TextBox();
        btnDokter = new widget.Button();
        jLabel10 = new widget.Label();
        LblPetugas = new widget.Label();
        KdPtg2 = new widget.TextBox();
        NmPtg2 = new widget.TextBox();
        btnPetugas = new widget.Button();

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        ppBersihkan.setText("Hilangkan Centang/Tindakan Terpilih");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(250, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        Popup.add(ppBersihkan);

        ppDokter.setBackground(new java.awt.Color(255, 255, 254));
        ppDokter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppDokter.setForeground(new java.awt.Color(50, 50, 50));
        ppDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        ppDokter.setText("Ubah Ke Tindakan Dokter");
        ppDokter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppDokter.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppDokter.setName("ppDokter"); // NOI18N
        ppDokter.setPreferredSize(new java.awt.Dimension(250, 25));
        ppDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppDokterActionPerformed(evt);
            }
        });
        Popup.add(ppDokter);

        ppPetugas.setBackground(new java.awt.Color(255, 255, 254));
        ppPetugas.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPetugas.setForeground(new java.awt.Color(50, 50, 50));
        ppPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        ppPetugas.setText("Ubah Ke Tindakan Petugas");
        ppPetugas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPetugas.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPetugas.setName("ppPetugas"); // NOI18N
        ppPetugas.setPreferredSize(new java.awt.Dimension(250, 25));
        ppPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPetugasActionPerformed(evt);
            }
        });
        Popup.add(ppPetugas);

        ppPetugasDokter.setBackground(new java.awt.Color(255, 255, 254));
        ppPetugasDokter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPetugasDokter.setForeground(new java.awt.Color(50, 50, 50));
        ppPetugasDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        ppPetugasDokter.setText("Ubah Ke Tindakan Dokter & Petugas");
        ppPetugasDokter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPetugasDokter.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPetugasDokter.setName("ppPetugasDokter"); // NOI18N
        ppPetugasDokter.setPreferredSize(new java.awt.Dimension(250, 25));
        ppPetugasDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPetugasDokterActionPerformed(evt);
            }
        });
        Popup.add(ppPetugasDokter);

        TNoRw.setName("TNoRw"); // NOI18N

        TPasien.setFocusTraversalPolicyProvider(true);
        TPasien.setName("TPasien"); // NOI18N

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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Tarif Tagihan/Perawatan/Tindakan Rawat Inap ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setComponentPopupMenu(Popup);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setAutoCreateRowSorter(true);
        tbKamar.setComponentPopupMenu(Popup);
        tbKamar.setName("tbKamar"); // NOI18N
        tbKamar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbKamarMouseClicked(evt);
            }
        });
        tbKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbKamarKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        jLabel4.setText("Kategori :");
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(55, 23));
        panelisi3.add(jLabel4);

        KdKtg.setEditable(false);
        KdKtg.setName("KdKtg"); // NOI18N
        KdKtg.setPreferredSize(new java.awt.Dimension(50, 23));
        KdKtg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdKtgKeyPressed(evt);
            }
        });
        panelisi3.add(KdKtg);

        NmKtg.setEditable(false);
        NmKtg.setName("NmKtg"); // NOI18N
        NmKtg.setPreferredSize(new java.awt.Dimension(140, 23));
        NmKtg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmKtgKeyPressed(evt);
            }
        });
        panelisi3.add(NmKtg);

        btnKategori.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnKategori.setMnemonic('1');
        btnKategori.setToolTipText("Alt+1");
        btnKategori.setName("btnKategori"); // NOI18N
        btnKategori.setPreferredSize(new java.awt.Dimension(28, 23));
        btnKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKategoriActionPerformed(evt);
            }
        });
        btnKategori.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnKategoriKeyPressed(evt);
            }
        });
        panelisi3.add(btnKategori);

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
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

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(30, 23));
        panelisi3.add(LCount);

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

        FormInput.setBackground(new java.awt.Color(215, 225, 215));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(710, 74));
        FormInput.setLayout(null);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-08-2025" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(685, 10, 120, 23);

        jLabel5.setText("Dokter :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 60, 23);

        kddokter.setName("kddokter"); // NOI18N
        kddokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kddokterKeyPressed(evt);
            }
        });
        FormInput.add(kddokter);
        kddokter.setBounds(63, 10, 100, 23);

        nmdokter.setEditable(false);
        nmdokter.setName("nmdokter"); // NOI18N
        FormInput.add(nmdokter);
        nmdokter.setBounds(165, 10, 367, 23);

        btnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokter.setMnemonic('4');
        btnDokter.setToolTipText("ALt+4");
        btnDokter.setName("btnDokter"); // NOI18N
        btnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterActionPerformed(evt);
            }
        });
        FormInput.add(btnDokter);
        btnDokter.setBounds(536, 10, 28, 23);

        jLabel10.setText("Tanggal :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(602, 10, 80, 23);

        LblPetugas.setText("Petugas :");
        LblPetugas.setName("LblPetugas"); // NOI18N
        FormInput.add(LblPetugas);
        LblPetugas.setBounds(0, 40, 60, 23);

        KdPtg2.setName("KdPtg2"); // NOI18N
        KdPtg2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPtg2KeyPressed(evt);
            }
        });
        FormInput.add(KdPtg2);
        KdPtg2.setBounds(63, 40, 100, 23);

        NmPtg2.setEditable(false);
        NmPtg2.setName("NmPtg2"); // NOI18N
        FormInput.add(NmPtg2);
        NmPtg2.setBounds(165, 40, 367, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('4');
        btnPetugas.setToolTipText("ALt+4");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(536, 40, 28, 23);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbKamar.requestFocus();
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
        emptTeks();
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari, TCari);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void tbKamarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbKamarMouseClicked
        if(tbKamar.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }

            if(evt.getClickCount()==2){
                dispose();
            }
        }
    }//GEN-LAST:event_tbKamarMouseClicked

    private void tbKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbKamarKeyPressed
        if(tbKamar.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                try {
                    if(tbKamar.getSelectedRow()>-1){
                        i=tbKamar.getSelectedColumn();
                        if(i==0){
                            tbKamar.setValueAt(true,tbKamar.getSelectedRow(),0);
                        }else if(i==1){
                            tbKamar.setValueAt(true,tbKamar.getSelectedRow(),1);
                        }else if(i==2){
                            tbKamar.setValueAt(true,tbKamar.getSelectedRow(),2);
                        }else if(i==3){
                            tbKamar.setValueAt(true,tbKamar.getSelectedRow(),3);
                        }
                    }
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if((evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                dispose();
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }
        }
    }//GEN-LAST:event_tbKamarKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgJnsPerawatanRanap perawatan=new DlgJnsPerawatanRanap(null,false);
        perawatan.emptTeks();
        perawatan.isCek();
        perawatan.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        perawatan.setLocationRelativeTo(internalFrame1);
        perawatan.setAlwaysOnTop(false);
        perawatan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahActionPerformed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||kddokter.getText().trim().equals("")){
            Valid.textKosong(TCari,"Pasien & Dokter");
        }else{
            if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
                JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi.\nSilahkan hubungi bagian kasir/keuangan ..!!");
            } else {
                if (akses.getadmin() || Sequel.cekTanggalRegistrasiSmc(TNoRw.getText())) {
                    simpan2();
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        for(i=0;i<tbKamar.getRowCount();i++){
            tbKamar.setValueAt(false,i,0);
            tbKamar.setValueAt(false,i,1);
            tbKamar.setValueAt(false,i,2);
            tbKamar.setValueAt(false,i,3);
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void kddokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kddokterKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            switch (pilihtable) {
                case "rawat_inap_dr":
                    nmdokter.setText(dokter.tampil3(kddokter.getText()));
                    break;
                case "rawat_inap_pr":
                    nmdokter.setText(petugas.tampil3(kddokter.getText()));
                    break;
                case "rawat_inap_drpr":
                    nmdokter.setText(dokter.tampil3(kddokter.getText()));
                    break;
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnDokterActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_DOWN){
            TCari.requestFocus();
        }else{
            Valid.pindah(evt,BtnKeluar,TCari);
        }
    }//GEN-LAST:event_kddokterKeyPressed

    private void btnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterActionPerformed
        switch (pilihtable) {
            case "rawat_inap_dr":
                dokter.isCek();
                dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                dokter.setLocationRelativeTo(internalFrame1);
                dokter.setAlwaysOnTop(false);
                dokter.setVisible(true);
                break;
            case "rawat_inap_pr":
                petugas.isCek();
                petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                petugas.setLocationRelativeTo(internalFrame1);
                petugas.setAlwaysOnTop(false);
                petugas.setVisible(true);
                break;
            case "rawat_inap_drpr":
                dokter.isCek();
                dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                dokter.setLocationRelativeTo(internalFrame1);
                dokter.setAlwaysOnTop(false);
                dokter.setVisible(true);
                break;
        }
    }//GEN-LAST:event_btnDokterActionPerformed

    private void ppDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppDokterActionPerformed
        pilihtable="rawat_inap_dr";
        jLabel5.setText("Dokter :");
        kddokter.setText("");
        nmdokter.setText("");
        LblPetugas.setVisible(false);
        KdPtg2.setVisible(false);
        NmPtg2.setVisible(false);
        btnPetugas.setVisible(false);
        FormInput.setPreferredSize(new Dimension(WIDTH, 44));
        runBackground(() ->tampil());
    }//GEN-LAST:event_ppDokterActionPerformed

    private void ppPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPetugasActionPerformed
        pilihtable="rawat_inap_pr";
        jLabel5.setText("Petugas :");
        kddokter.setText("");
        nmdokter.setText("");
        LblPetugas.setVisible(false);
        KdPtg2.setVisible(false);
        NmPtg2.setVisible(false);
        btnPetugas.setVisible(false);
        FormInput.setPreferredSize(new Dimension(WIDTH, 44));
        runBackground(() ->tampil());
    }//GEN-LAST:event_ppPetugasActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        TCari.requestFocus();
    }//GEN-LAST:event_formWindowActivated

    private void KdPtg2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPtg2KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            NmPtg2.setText(petugas.tampil3(KdPtg2.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnPetugasActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_DOWN){
            TCari.requestFocus();
        }else{
            Valid.pindah(evt,BtnKeluar,TCari);
        }
    }//GEN-LAST:event_KdPtg2KeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void ppPetugasDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPetugasDokterActionPerformed
        pilihtable="rawat_inap_drpr";
        jLabel5.setText("Dokter :");
        kddokter.setText("");
        nmdokter.setText("");
        LblPetugas.setVisible(true);
        KdPtg2.setVisible(true);
        NmPtg2.setVisible(true);
        btnPetugas.setVisible(true);
        FormInput.setPreferredSize(new Dimension(WIDTH, 74));
        runBackground(() ->tampil());
    }//GEN-LAST:event_ppPetugasDokterActionPerformed

    private void KdKtgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdKtgKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select kategori_perawatan.nm_kategori from kategori_perawatan where kategori_perawatan.kd_kategori=? ",NmKtg,KdKtg.getText());
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnKategoriActionPerformed(null);
        }else{
            Valid.pindah(evt,DTPTgl,TCari);
        }
    }//GEN-LAST:event_KdKtgKeyPressed

    private void NmKtgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmKtgKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NmKtgKeyPressed

    private void btnKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKategoriActionPerformed
        DlgKtgPerawatan ktg=new DlgKtgPerawatan(null,false);
        ktg.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(ktg.getTable().getSelectedRow()!= -1){                   
                    KdKtg.setText(ktg.getTable().getValueAt(ktg.getTable().getSelectedRow(),1).toString());
                    NmKtg.setText(ktg.getTable().getValueAt(ktg.getTable().getSelectedRow(),2).toString());
                }     
                KdKtg.requestFocus();
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
        
        ktg.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    ktg.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        ktg.emptTeks();
        ktg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        ktg.setLocationRelativeTo(internalFrame1);
        ktg.setVisible(true);
    }//GEN-LAST:event_btnKategoriActionPerformed

    private void btnKategoriKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnKategoriKeyPressed
        Valid.pindah(evt,KdKtg,TCari);
    }//GEN-LAST:event_btnKategoriKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgCariPerawatanRanap2 dialog = new DlgCariPerawatanRanap2(new javax.swing.JFrame(), true);
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
    private widget.Tanggal DTPTgl;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdKtg;
    private widget.TextBox KdPtg2;
    private widget.Label LCount;
    private widget.Label LblPetugas;
    private widget.TextBox NmKtg;
    private widget.TextBox NmPtg2;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Button btnDokter;
    private widget.Button btnKategori;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.TextBox kddokter;
    private widget.Label label10;
    private widget.Label label9;
    private widget.TextBox nmdokter;
    private widget.panelisi panelisi3;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppDokter;
    private javax.swing.JMenuItem ppPetugas;
    private javax.swing.JMenuItem ppPetugasDokter;
    private widget.Table tbKamar;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        try {
            ArrayNode tindakan = mapper.createArrayNode();
            where = "";
            for (int i = 0; i < tabMode.getRowCount(); i++) {
                if ((Boolean) tbKamar.getValueAt(i, 0)
                    || (Boolean) tbKamar.getValueAt(i, 1)
                    || (Boolean) tbKamar.getValueAt(i, 2)
                    || (Boolean) tbKamar.getValueAt(i, 3)
                ) {
                    ObjectNode row = mapper.createObjectNode();
                    row.put("pg", (Boolean) tbKamar.getValueAt(i, 0));
                    row.put("sg", (Boolean) tbKamar.getValueAt(i, 1));
                    row.put("sr", (Boolean) tbKamar.getValueAt(i, 2));
                    row.put("mlm", (Boolean) tbKamar.getValueAt(i, 3));
                    row.put("kd_jenis_prw", tbKamar.getValueAt(i, 4).toString());
                    row.put("nm_perawatan", tbKamar.getValueAt(i, 5).toString());
                    row.put("nm_kategori", tbKamar.getValueAt(i, 6).toString());
                    row.put("total_bayar", (Double) tbKamar.getValueAt(i, 7));
                    row.put("bagian_rs", (Double) tbKamar.getValueAt(i, 8));
                    row.put("bhp", (Double) tbKamar.getValueAt(i, 9));
                    row.put("tarif_tindakandr", (Double) tbKamar.getValueAt(i, 10));
                    row.put("tarif_tindakanpr", (Double) tbKamar.getValueAt(i, 11));
                    row.put("kso", (Double) tbKamar.getValueAt(i, 12));
                    row.put("menejemen", (Double) tbKamar.getValueAt(i, 13));
                    row.put("kelas", tbKamar.getValueAt(i, 14).toString());
                    tindakan.add(row);
                    where = where.concat("'" + tbKamar.getValueAt(i, 4).toString() + "', ");
                }
            }
            if (!where.isBlank()) {
                where = where.substring(0, where.length() - 2);
            }
            Valid.tabelKosong(tabMode);
            for (JsonNode row : tindakan) {
                tabMode.addRow(new Object[] {
                    row.path("pg").asBoolean(), row.path("sg").asBoolean(), row.path("sr").asBoolean(), row.path("mlm").asBoolean(),
                    row.path("kd_jenis_prw").asText(), row.path("nm_perawatan").asText(), row.path("nm_kategori").asText(),
                    row.path("total_bayar").asDouble(), row.path("bagian_rs").asDouble(), row.path("bhp").asDouble(),
                    row.path("tarif_tindakandr").asDouble(), row.path("tarif_tindakanpr").asDouble(), row.path("kso").asDouble(),
                    row.path("menejemen").asDouble(), row.path("kelas").asText()
                });
            }
            // cekTindakanTerinput();
            boolean isRuangRanap = ruang_ranap.equals("Yes"),
                    isCaraBayarRanap = cara_bayar_ranap.equals("Yes"),
                    isKelasRanap = kelas_ranap.equals("Yes");
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select jns_perawatan_inap.*, kategori_perawatan.nm_kategori from jns_perawatan_inap " +
                "join kategori_perawatan on jns_perawatan_inap.kd_kategori = kategori_perawatan.kd_kategori " +
                "where " + (where.isBlank() ? "" : "jns_perawatan_inap.kd_jenis_prw not in (" + where + ") and ") +
                "(jns_perawatan_inap.status = '1' and jns_perawatan_inap.kd_kategori like ? " +
                (isCaraBayarRanap ? "and (jns_perawatan_inap.kd_pj = ? or jns_perawatan_inap.kd_pj = '-' ) " : "") +
                (isRuangRanap ? "and (jns_perawatan_inap.kd_bangsal = ? or jns_perawatan_inap.kd_bangsal = '-') " : "") +
                (isKelasRanap ? "and (jns_perawatan_inap.kelas = ? or jns_perawatan_inap.kelas = '-') " : "") +
                (TCari.getText().isBlank() ? "" : "and (jns_perawatan_inap.kd_jenis_prw like ? or " +
                "jns_perawatan_inap.nm_perawatan like ? or kategori_perawatan.nm_kategori like ?) ") +
                ") order by " + (where.isBlank() ? "" : "(field(jns_perawatan_inap.kd_jenis_prw, " + where +
                ") = 1), ") + "jns_perawatan_inap.nm_perawatan, jns_perawatan_inap.kd_jenis_prw"
            )) {
                int p = 0;
                ps.setString(++p, KdKtg.getText() + "%");
                if (isCaraBayarRanap) ps.setString(++p, kd_pj.trim());
                if (isRuangRanap) ps.setString(++p, kd_bangsal.trim());
                if (isKelasRanap) ps.setString(++p, kelas.trim());
                if (!TCari.getText().isBlank()) {
                    ps.setString(++p, "%" + TCari.getText().trim() + "%");
                    ps.setString(++p, "%" + TCari.getText().trim() + "%");
                    ps.setString(++p, "%" + TCari.getText().trim() + "%");
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        do {
                            JsonNode cc = tindakanTerinput.path(pilihtable).path(rs.getString("kd_jenis_prw"));
                            switch (pilihtable) {
                                case "rawat_inap_dr":
                                    if (rs.getDouble("total_byrdr") > 0) {
                                        tabMode.addRow(new Object[] {
                                            cc.path("pg").asBoolean(), cc.path("sg").asBoolean(), cc.path("sr").asBoolean(),
                                            cc.path("mlm").asBoolean(), rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"),
                                            rs.getString("nm_kategori"), rs.getDouble("total_byrdr"), rs.getDouble("material"),
                                            rs.getDouble("bhp"), rs.getDouble("tarif_tindakandr"), rs.getDouble("tarif_tindakanpr"),
                                            rs.getDouble("kso"), rs.getDouble("menejemen"), rs.getString("kelas")
                                        });
                                    }
                                    break;
                                case "rawat_inap_pr":
                                    if (rs.getDouble("total_byrpr") > 0) {
                                        tabMode.addRow(new Object[] {
                                            cc.path("pg").asBoolean(), cc.path("sg").asBoolean(), cc.path("sr").asBoolean(),
                                            cc.path("mlm").asBoolean(), rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"),
                                            rs.getString("nm_kategori"), rs.getDouble("total_byrpr"), rs.getDouble("material"),
                                            rs.getDouble("bhp"), rs.getDouble("tarif_tindakandr"), rs.getDouble("tarif_tindakanpr"),
                                            rs.getDouble("kso"), rs.getDouble("menejemen"), rs.getString("kelas")
                                        });
                                    }
                                    break;
                                case "rawat_inap_drpr":
                                    if (rs.getDouble("total_byrdrpr") > 0) {
                                        tabMode.addRow(new Object[] {
                                            cc.path("pg").asBoolean(), cc.path("sg").asBoolean(), cc.path("sr").asBoolean(),
                                            cc.path("mlm").asBoolean(), rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"),
                                            rs.getString("nm_kategori"), rs.getDouble("total_byrdrpr"), rs.getDouble("material"),
                                            rs.getDouble("bhp"), rs.getDouble("tarif_tindakandr"), rs.getDouble("tarif_tindakanpr"),
                                            rs.getDouble("kso"), rs.getDouble("menejemen"), rs.getString("kelas")
                                        });
                                    }
                                    break;
                            }
                        } while (rs.next());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText("" + tbKamar.getRowCount());
    }

    public void tampil2() {
        try {
            Valid.tabelKosong(tabMode);
            cekTindakanTerinput();
            boolean isRuangRanap = ruang_ranap.equals("Yes"),
                    isCaraBayarRanap = cara_bayar_ranap.equals("Yes"),
                    isKelasRanap = kelas_ranap.equals("Yes");
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select jns_perawatan_inap.*, kategori_perawatan.nm_kategori from jns_perawatan_inap join " +
                "kategori_perawatan on jns_perawatan_inap.kd_kategori = kategori_perawatan.kd_kategori " +
                "where jns_perawatan_inap.status = '1' and jns_perawatan_inap.kd_kategori like ? " +
                (isCaraBayarRanap ? "and (jns_perawatan_inap.kd_pj = ? or jns_perawatan_inap.kd_pj = '-' ) " : "") +
                (isRuangRanap ? "and (jns_perawatan_inap.kd_bangsal = ? or jns_perawatan_inap.kd_bangsal = '-') " : "") +
                (isKelasRanap ? "and (jns_perawatan_inap.kelas = ? or jns_perawatan_inap.kelas = '-') " : "") +
                (TCari.getText().isBlank() ? "" : "and (jns_perawatan_inap.kd_jenis_prw like ? or " +
                "jns_perawatan_inap.nm_perawatan like ? or kategori_perawatan.nm_kategori like ?) ") +
                "order by " + (where.isBlank() ? "" : "(field(jns_perawatan_inap.kd_jenis_prw, " + where + ") = 0), ") +
                "jns_perawatan_inap.nm_perawatan, jns_perawatan_inap.kd_jenis_prw"
            )) {
                int p = 0;
                ps.setString(++p, KdKtg.getText() + "%");
                if (isCaraBayarRanap) ps.setString(++p, kd_pj.trim());
                if (isRuangRanap) ps.setString(++p, kd_bangsal.trim());
                if (isKelasRanap) ps.setString(++p, kelas.trim());
                if (!TCari.getText().isBlank()) {
                    ps.setString(++p, "%" + TCari.getText().trim() + "%");
                    ps.setString(++p, "%" + TCari.getText().trim() + "%");
                    ps.setString(++p, "%" + TCari.getText().trim() + "%");
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        do {
                            JsonNode cc = tindakanTerinput.path(pilihtable).path(rs.getString("kd_jenis_prw"));
                            switch (pilihtable) {
                                case "rawat_inap_dr":
                                    if (rs.getDouble("total_byrdr") > 0) {
                                        tabMode.addRow(new Object[] {
                                            cc.path("pg").asBoolean(), cc.path("sg").asBoolean(), cc.path("sr").asBoolean(),
                                            cc.path("mlm").asBoolean(), rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"),
                                            rs.getString("nm_kategori"), rs.getDouble("total_byrdr"), rs.getDouble("material"),
                                            rs.getDouble("bhp"), rs.getDouble("tarif_tindakandr"), rs.getDouble("tarif_tindakanpr"),
                                            rs.getDouble("kso"), rs.getDouble("menejemen"), rs.getString("kelas")
                                        });
                                    }
                                    break;
                                case "rawat_inap_pr":
                                    if (rs.getDouble("total_byrpr") > 0) {
                                        tabMode.addRow(new Object[] {
                                            cc.path("pg").asBoolean(), cc.path("sg").asBoolean(), cc.path("sr").asBoolean(),
                                            cc.path("mlm").asBoolean(), rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"),
                                            rs.getString("nm_kategori"), rs.getDouble("total_byrpr"), rs.getDouble("material"),
                                            rs.getDouble("bhp"), rs.getDouble("tarif_tindakandr"), rs.getDouble("tarif_tindakanpr"),
                                            rs.getDouble("kso"), rs.getDouble("menejemen"), rs.getString("kelas")
                                        });
                                    }
                                    break;
                                case "rawat_inap_drpr":
                                    if (rs.getDouble("total_byrdrpr") > 0) {
                                        tabMode.addRow(new Object[] {
                                            cc.path("pg").asBoolean(), cc.path("sg").asBoolean(), cc.path("sr").asBoolean(),
                                            cc.path("mlm").asBoolean(), rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"),
                                            rs.getString("nm_kategori"), rs.getDouble("total_byrdrpr"), rs.getDouble("material"),
                                            rs.getDouble("bhp"), rs.getDouble("tarif_tindakandr"), rs.getDouble("tarif_tindakanpr"),
                                            rs.getDouble("kso"), rs.getDouble("menejemen"), rs.getString("kelas")
                                        });
                                    }
                                    break;
                            }
                        } while (rs.next());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText("" + tbKamar.getRowCount());
    }

    private void cekTindakanTerinput() {
        tindakanTerinput = mapper.createObjectNode();
        where = "";
        try {
            switch (pilihtable) {
                case "rawat_inap_dr":
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select rawat_inap_dr.kd_jenis_prw, " +
                        "(rawat_inap_dr.jam_rawat between '00:00:01' and '10:00:00') as pg, " +
                        "(rawat_inap_dr.jam_rawat between '10:00:01' and '15:00:00') as sg, " +
                        "(rawat_inap_dr.jam_rawat between '15:00:01' and '19:00:00') as sr, " +
                        "(rawat_inap_dr.jam_rawat between '19:00:01' and '23:59:59') as mlm " +
                        "from rawat_inap_dr where rawat_inap_dr.no_rawat = ? and rawat_inap_dr.tgl_perawatan = ? " +
                        "group by rawat_inap_dr.kd_jenis_prw"
                    )) {
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, Valid.getTglSmc(DTPTgl));
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                ObjectNode dr = mapper.createObjectNode();
                                do {
                                    ObjectNode cc = mapper.createObjectNode();
                                    cc.put("pg", rs.getBoolean("pg"));
                                    cc.put("sg", rs.getBoolean("sg"));
                                    cc.put("sr", rs.getBoolean("sr"));
                                    cc.put("mlm", rs.getBoolean("mlm"));
                                    dr.set(rs.getString("kd_jenis_prw"), cc);
                                    where = where.concat("'").concat(rs.getString("kd_jenis_prw")).concat("', ");
                                } while (rs.next());
                                tindakanTerinput.set("rawat_inap_dr", dr);
                            }
                        }
                    }
                    break;
                case "rawat_inap_pr":
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select rawat_inap_pr.kd_jenis_prw, " +
                        "(rawat_inap_pr.jam_rawat between '00:00:01' and '10:00:00') as pg, " +
                        "(rawat_inap_pr.jam_rawat between '10:00:01' and '15:00:00') as sg, " +
                        "(rawat_inap_pr.jam_rawat between '15:00:01' and '19:00:00') as sr, " +
                        "(rawat_inap_pr.jam_rawat between '19:00:01' and '23:59:59') as mlm " +
                        "from rawat_inap_pr where rawat_inap_pr.no_rawat = ? and rawat_inap_pr.tgl_perawatan = ? " +
                        "group by rawat_inap_pr.kd_jenis_prw"
                    )) {
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, Valid.getTglSmc(DTPTgl));
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                ObjectNode pr = mapper.createObjectNode();
                                do {
                                    ObjectNode cc = mapper.createObjectNode();
                                    cc.put("pg", rs.getBoolean("pg"));
                                    cc.put("sg", rs.getBoolean("sg"));
                                    cc.put("sr", rs.getBoolean("sr"));
                                    cc.put("mlm", rs.getBoolean("mlm"));
                                    pr.set(rs.getString("kd_jenis_prw"), cc);
                                } while (rs.next());
                                tindakanTerinput.set("rawat_inap_pr", pr);
                            }
                        }
                    }
                    break;
                case "rawat_inap_drpr":
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select rawat_inap_drpr.kd_jenis_prw, " +
                        "(rawat_inap_drpr.jam_rawat between '00:00:01' and '10:00:00') as pg, " +
                        "(rawat_inap_drpr.jam_rawat between '10:00:01' and '15:00:00') as sg, " +
                        "(rawat_inap_drpr.jam_rawat between '15:00:01' and '19:00:00') as sr, " +
                        "(rawat_inap_drpr.jam_rawat between '19:00:01' and '23:59:59') as mlm " +
                        "from rawat_inap_drpr where rawat_inap_drpr.no_rawat = ? and rawat_inap_drpr.tgl_perawatan = ? " +
                        "group by rawat_inap_drpr.kd_jenis_prw"
                    )) {
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, Valid.getTglSmc(DTPTgl));
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                ObjectNode drpr = mapper.createObjectNode();
                                do {
                                    ObjectNode cc = mapper.createObjectNode();
                                    cc.put("pg", rs.getBoolean("pg"));
                                    cc.put("sg", rs.getBoolean("sg"));
                                    cc.put("sr", rs.getBoolean("sr"));
                                    cc.put("mlm", rs.getBoolean("mlm"));
                                    drpr.set(rs.getString("kd_jenis_prw"), cc);
                                } while (rs.next());
                                tindakanTerinput.set("rawat_inap_drpr", drpr);
                            }
                        }
                    }
                    break;
            }
            where = where.substring(0, where.length() - 2);
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void emptTeks() {
        TCari.setText("");
         for(i=0;i<tbKamar.getRowCount();i++){
            tbKamar.setValueAt(false,i,0);
            tbKamar.setValueAt(false,i,1);
            tbKamar.setValueAt(false,i,2);
            tbKamar.setValueAt(false,i,3);
         }
        TCari.requestFocus();
    }

    private void getData() {
        if(tbKamar.getSelectedRow()!= -1){
            if(TNoRw.getText().trim().equals("")||kddokter.getText().trim().equals("")){
                Valid.textKosong(TCari,"Dokter/Paramedis");
                for(i=0;i<tbKamar.getRowCount();i++){
                    tbKamar.setValueAt(false,i,0);
                    tbKamar.setValueAt(false,i,1);
                    tbKamar.setValueAt(false,i,2);
                    tbKamar.setValueAt(false,i,3);
                }
            }
        }
    }

    public JTable getTable(){
        return tbKamar;
    }

    public void isCek(){
        BtnTambah.setEnabled(akses.gettarif_ranap());
        TCari.requestFocus();
    }

    public void setNoRm(String norwt,String pilihtable,Date tanggal,String jam,String menit,String detik,boolean status,String pasien) {
        TNoRw.setText(norwt);
        kddokter.setText("");
        TPasien.setText(pasien);
        this.kd_pj=Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText());
        this.kd_bangsal=Sequel.cariIsi(
                "select bangsal.kd_bangsal from bangsal inner join kamar inner join kamar_inap "+
                "on bangsal.kd_bangsal=kamar.kd_bangsal and kamar.kd_kamar=kamar_inap.kd_kamar "+
                "where kamar_inap.no_rawat=? and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",TNoRw.getText());
        this.pilihtable=pilihtable;
        this.kelas=Sequel.cariIsi(
                "select kamar.kelas from kamar inner join kamar_inap "+
                "on kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? "+
                "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",TNoRw.getText());
        switch (pilihtable) {
            case "rawat_inap_dr":
                jLabel5.setText("Dokter :");
                LblPetugas.setVisible(false);
                KdPtg2.setVisible(false);
                NmPtg2.setVisible(false);
                btnPetugas.setVisible(false);
                FormInput.setPreferredSize(new Dimension(WIDTH, 44));
                break;
            case "rawat_inap_pr":
                jLabel5.setText("Perawat :");
                LblPetugas.setVisible(false);
                KdPtg2.setVisible(false);
                NmPtg2.setVisible(false);
                btnPetugas.setVisible(false);
                FormInput.setPreferredSize(new Dimension(WIDTH, 44));
                break;
            case "rawat_inap_drpr":
                jLabel5.setText(" Dokter :");
                LblPetugas.setVisible(true);
                KdPtg2.setVisible(true);
                NmPtg2.setVisible(true);
                btnPetugas.setVisible(true);
                FormInput.setPreferredSize(new Dimension(WIDTH, 74));
                break;
        }
        DTPTgl.setDate(tanggal);
        TCari.requestFocus();

        for(i=0;i<tbKamar.getRowCount();i++){
            tbKamar.setValueAt(false,i,0);
            tbKamar.setValueAt(false,i,1);
            tbKamar.setValueAt(false,i,2);
            tbKamar.setValueAt(false,i,3);
        }
    }

    public void setPetugas(String kode, String nama,String suhu,String tensi, String Hasil,
            String perkembangan, String kode2, String nama2,String berat,
            String tinggi,String nadi,String respirasi,String gcs,String alergi){
        kddokter.setText(kode);
        nmdokter.setText(nama);
        KdPtg2.setText(kode2);
        NmPtg2.setText(nama2);
    }

    public void setPetugas2(String kodeDokter, String namaDokter, String kodePetugas, String namaPetugas) {
        kddokter.setText(kodeDokter);
        nmdokter.setText(namaDokter);
        KdPtg2.setText(kodePetugas);
        NmPtg2.setText(namaPetugas);
    }

    private void simpan2() {
        if (pilihtable.equals("rawat_inap_dr")|| pilihtable.equals("rawat_inap_pr") || pilihtable.equals("rawat_inap_drpr")) {
            try {
                Sequel.AutoComitFalse();
                sukses = true;
                ttljmdokter = 0; ttljmperawat = 0; ttlkso = 0; ttlpendapatan = 0; ttljasasarana = 0; ttlbhp = 0; ttlmenejemen = 0;
                hapusttljmdokter = 0; hapusttljmperawat = 0; hapusttlkso = 0; hapusttlpendapatan = 0; hapusttljasasarana = 0; hapusttlbhp = 0; hapusttlmenejemen = 0;
                for (i = 0; i < tbKamar.getRowCount(); i++) {
                    pg = false; sg = false; sr = false; mlm = false;
                    if (tindakanTerinput.path(pilihtable).has(tbKamar.getValueAt(i, 4).toString())) {
                        pg = tindakanTerinput.path(pilihtable).path(tbKamar.getValueAt(i, 4).toString()).path("pg").asBoolean();
                        sg = tindakanTerinput.path(pilihtable).path(tbKamar.getValueAt(i, 4).toString()).path("sg").asBoolean();
                        sr = tindakanTerinput.path(pilihtable).path(tbKamar.getValueAt(i, 4).toString()).path("sr").asBoolean();
                        mlm = tindakanTerinput.path(pilihtable).path(tbKamar.getValueAt(i, 4).toString()).path("mlm").asBoolean();
                    }
                    switch (pilihtable) {
                        case "rawat_inap_dr":
                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 0).toString()) && !pg) {
                                if (Sequel.menyimpantfSmc("rawat_inap_dr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "07:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 0).toString()) && pg) {
                                if (Sequel.menghapustfSmc("rawat_inap_dr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "07:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 1).toString()) && !sg) {
                                if (Sequel.menyimpantfSmc("rawat_inap_dr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "12:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 1).toString()) && sg) {
                                if (Sequel.menghapustfSmc("rawat_inap_dr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "12:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 2).toString()) && !sr) {
                                if (Sequel.menyimpantfSmc("rawat_inap_dr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "16:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 2).toString()) && sr) {
                                if (Sequel.menghapustfSmc("rawat_inap_dr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "16:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 3).toString()) && !mlm) {
                                if (Sequel.menyimpantfSmc("rawat_inap_dr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "20:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 3).toString()) && mlm) {
                                if (Sequel.menghapustfSmc("rawat_inap_dr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "20:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }
                            break;
                        case "rawat_inap_pr":
                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 0).toString()) && !pg) {
                                if (Sequel.menyimpantfSmc("rawat_inap_pr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "07:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 0).toString()) && pg) {
                                if (Sequel.menghapustfSmc("rawat_inap_pr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "07:00:00"
                                )) {
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 1).toString()) && !sg) {
                                if (Sequel.menyimpantfSmc("rawat_inap_pr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "12:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 1).toString()) && sg) {
                                if (Sequel.menghapustfSmc("rawat_inap_pr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "12:00:00"
                                )) {
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 2).toString()) && !sr) {
                                if (Sequel.menyimpantfSmc("rawat_inap_pr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "16:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 2).toString()) && sr) {
                                if (Sequel.menghapustfSmc("rawat_inap_pr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "16:00:00"
                                )) {
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 3).toString()) && !mlm) {
                                if (Sequel.menyimpantfSmc("rawat_inap_pr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), Valid.getTglSmc(DTPTgl), "20:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 3).toString()) && mlm) {
                                if (Sequel.menghapustfSmc("rawat_inap_pr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "20:00:00"
                                )) {
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }
                            break;
                        case "rawat_inap_drpr":
                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 0).toString()) && !pg) {
                                if (Sequel.menyimpantfSmc("rawat_inap_drpr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), KdPtg2.getText(), Valid.getTglSmc(DTPTgl), "07:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 0).toString()) && pg) {
                                if (Sequel.menghapustfSmc("rawat_inap_drpr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "07:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 1).toString()) && !sg) {
                                if (Sequel.menyimpantfSmc("rawat_inap_drpr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), KdPtg2.getText(), Valid.getTglSmc(DTPTgl), "12:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 1).toString()) && sg) {
                                if (Sequel.menghapustfSmc("rawat_inap_drpr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "12:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 2).toString()) && !sr) {
                                if (Sequel.menyimpantfSmc("rawat_inap_drpr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), KdPtg2.getText(), Valid.getTglSmc(DTPTgl), "16:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 2).toString()) && sr) {
                                if (Sequel.menghapustfSmc("rawat_inap_drpr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "16:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }

                            if (Boolean.parseBoolean(tbKamar.getValueAt(i, 3).toString()) && !mlm) {
                                if (Sequel.menyimpantfSmc("rawat_inap_drpr", null,
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), kddokter.getText(), KdPtg2.getText(), Valid.getTglSmc(DTPTgl), "20:00:00",
                                    tbKamar.getValueAt(i, 8).toString(), tbKamar.getValueAt(i, 9).toString(), tbKamar.getValueAt(i, 10).toString(), tbKamar.getValueAt(i, 11).toString(),
                                    tbKamar.getValueAt(i, 12).toString(), tbKamar.getValueAt(i, 13).toString(), tbKamar.getValueAt(i, 7).toString()
                                )) {
                                    ttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    ttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    ttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    ttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    ttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    ttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    ttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            } else if (!Boolean.parseBoolean(tbKamar.getValueAt(i, 3).toString()) && mlm) {
                                if (Sequel.menghapustfSmc("rawat_inap_drpr", "no_rawat = ? and kd_jenis_prw = ? and tgl_perawatan = ? and jam_rawat = ?",
                                    TNoRw.getText(), tbKamar.getValueAt(i, 4).toString(), Valid.getTglSmc(DTPTgl), "20:00:00"
                                )) {
                                    hapusttljmdokter += Double.parseDouble(tbKamar.getValueAt(i, 10).toString());
                                    hapusttljmperawat += Double.parseDouble(tbKamar.getValueAt(i, 11).toString());
                                    hapusttlkso += Double.parseDouble(tbKamar.getValueAt(i, 12).toString());
                                    hapusttlpendapatan += Double.parseDouble(tbKamar.getValueAt(i, 7).toString());
                                    hapusttlmenejemen += Double.parseDouble(tbKamar.getValueAt(i, 13).toString());
                                    hapusttljasasarana += Double.parseDouble(tbKamar.getValueAt(i, 8).toString());
                                    hapusttlbhp += Double.parseDouble(tbKamar.getValueAt(i, 9).toString());
                                } else {
                                    sukses = false;
                                }
                            }
                            break;
                    }
                }
                if (sukses) {
                    Sequel.deleteTampJurnal();
                    if (hapusttlpendapatan > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Suspen_Piutang_Tindakan_Ranap, "Suspen Piutang Tindakan Ranap", 0, hapusttlpendapatan);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Tindakan_Ranap, "Pendapatan Tindakan Ranap", hapusttlpendapatan, 0);
                    }
                    if (hapusttljmdokter > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Medik_Dokter_Tindakan_Ranap, "Beban Jasa Medik Dokter Tindakan Ranap", 0, hapusttljmdokter);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Medik_Dokter_Tindakan_Ranap, "Utang Jasa Medik Dokter Tindakan Ranap", hapusttljmdokter, 0);
                    }
                    if (hapusttljmperawat > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Medik_Paramedis_Tindakan_Ranap, "Beban Jasa Medik Paramedis Tindakan Ranap", 0, hapusttljmperawat);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Medik_Paramedis_Tindakan_Ranap, "Utang Jasa Medik Paramedis Tindakan Ranap", hapusttljmperawat, 0);
                    }
                    if (hapusttlkso > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_KSO_Tindakan_Ranap, "Beban KSO Tindakan Ranap", 0, hapusttlkso);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_KSO_Tindakan_Ranap, "Utang KSO Tindakan Ranap", hapusttlkso, 0);
                    }
                    if (hapusttlmenejemen > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Menejemen_Tindakan_Ranap, "Beban Jasa Menejemen Tindakan Ranap", 0, hapusttlmenejemen);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Menejemen_Tindakan_Ranap, "Utang Jasa Menejemen Tindakan Ranap", hapusttlmenejemen, 0);
                    }
                    if (hapusttljasasarana > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Sarana_Tindakan_Ranap, "Beban Jasa Sarana Tindakan Ranap", 0, hapusttljasasarana);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Sarana_Tindakan_Ranap, "Utang Jasa Sarana Tindakan Ranap", hapusttljasasarana, 0);
                    }
                    if (hapusttlbhp > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(HPP_BHP_Tindakan_Ranap, "HPP BHP Tindakan Ranap", 0, hapusttlbhp);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Persediaan_BHP_Tindakan_Ranap, "Persediaan BHP Tindakan Ranap", hapusttlbhp, 0);
                    }
                    sukses = jur.simpanJurnal(TNoRw.getText(), "U", "PEMBATALAN TINDAKAN RAWAT INAP PASIEN OLEH " + akses.getkode());
                }

                if (sukses) {
                    Sequel.deleteTampJurnal();
                    if (ttlpendapatan > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Suspen_Piutang_Tindakan_Ranap, "Suspen Piutang Tindakan Ranap", ttlpendapatan, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Tindakan_Ranap, "Pendapatan Tindakan Ranap", 0, ttlpendapatan);
                    }
                    if (ttljmdokter > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Medik_Dokter_Tindakan_Ranap, "Beban Jasa Medik Dokter Tindakan Ranap", ttljmdokter, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Medik_Dokter_Tindakan_Ranap, "Utang Jasa Medik Dokter Tindakan Ranap", 0, ttljmdokter);
                    }
                    if (ttljmperawat > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Medik_Paramedis_Tindakan_Ranap, "Beban Jasa Medik Paramedis Tindakan Ranap", ttljmperawat, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Medik_Paramedis_Tindakan_Ranap, "Utang Jasa Medik Paramedis Tindakan Ranap", 0, ttljmperawat);
                    }
                    if (ttlkso > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_KSO_Tindakan_Ranap, "Beban KSO Tindakan Ranap", ttlkso, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_KSO_Tindakan_Ranap, "Utang KSO Tindakan Ranap", 0, ttlkso);
                    }
                    if (ttljasasarana > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Sarana_Tindakan_Ranap, "Beban Jasa Sarana Tindakan Ranap", ttljasasarana, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Sarana_Tindakan_Ranap, "Utang Jasa Sarana Tindakan Ranap", 0, ttljasasarana);
                    }
                    if (ttlbhp > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(HPP_BHP_Tindakan_Ranap, "HPP BHP Tindakan Ranap", ttlbhp, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Persediaan_BHP_Tindakan_Ranap, "Persediaan BHP Tindakan Ranap", 0, ttlbhp);
                    }
                    if (ttlmenejemen > 0) {
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Beban_Jasa_Menejemen_Tindakan_Ranap, "Beban Jasa Menejemen Tindakan Ranap", ttlmenejemen, 0);
                        if (sukses) sukses = Sequel.insertOrUpdateTampJurnal(Utang_Jasa_Menejemen_Tindakan_Ranap, "Utang Jasa Menejemen Tindakan Ranap", 0, ttlmenejemen);
                    }
                    sukses = jur.simpanJurnal(TNoRw.getText(), "U", "TINDAKAN RAWAT INAP PASIEN " + TPasien.getText() + " DIPOSTING OLEH " + akses.getkode());
                }

                if (sukses) {
                    Sequel.Commit();
                } else {
                    Sequel.RollBack();
                }
                Sequel.AutoComitTrue();

                if (sukses) {
                    tampil2();
                    JOptionPane.showMessageDialog(null, "Proses simpan selesai...!");
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan data. Kemungkinan ada data yang sama dimasukkan sebelumnya...!");
            }
        }
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        ceksukses = true;

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        executor.submit(() -> {
            try {
                task.run();
            } finally {
                ceksukses = false;
                SwingUtilities.invokeLater(() -> {
                    this.setCursor(Cursor.getDefaultCursor());
                });
            }
        });
    }
}
