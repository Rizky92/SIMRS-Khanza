/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile 
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */

package bridging;

import inventory.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable2;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.Jurnal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import simrskhanza.DlgCariBangsal;
import widget.Button;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSKirimObatEditObat extends javax.swing.JDialog {
    private final DefaultTableModel tabModeobat,tabModeObatRacikan,tabModeDetailObatRacikan;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement psobat,psracikan,psstok,ps2,psbatch,psrekening,psobatracikan,psdiagnosa;
    private ResultSet rsobat,rsracikan,rsstok,rs2,rsbatch,rsrekening,rscariobat,rsdiagnosa,rsobatracikan;
    private double h_belicari=0, hargacari=0, sisacari=0,x=0,y=0,embalase=Sequel.cariIsiAngka("select set_embalase.embalase_per_obat from set_embalase"),
                   tuslah=Sequel.cariIsiAngka("select set_embalase.tuslah_per_obat from set_embalase"),kenaikan=0,stokbarang=0,ttl=0,ppnobat=0,ttlhpp,ttljual,Hari=0;
    private int i=0,z=0,row=0,row2,r,subttl;
    private Jurnal jur=new Jurnal();
    private boolean[] pilih; 
    private double[] jumlah,harga,eb,ts,stok,beli,kapasitas,kandungan;
    private String[] kodebarang,namabarang,kodesatuan,letakbarang,namajenis,aturan,industri,kategori,golongan,no,nobatch,nofaktur,kadaluarsa,keterangan,signa_cari1,signa_cari2,signa_racikan1,signa_racikan2;
    private String signa1="1",utc="",link="",signa2="1",nokunjungan="",kdObatSK="",requestJson="",URL="",otorisasi,sql="",aktifpcare="no",no_batchcari="", tgl_kadaluarsacari="", no_fakturcari="", aktifkanbatch="no",kodedokter="",namadokter="",noresep="",bangsal="",bangsaldefault=Sequel.cariIsi("select set_lokasi.kd_bangsal from set_lokasi limit 1"),tampilkan_ppnobat_ralan="",
        Suspen_Piutang_Obat_Ralan="",Obat_Ralan="",HPP_Obat_Rawat_Jalan="",Persediaan_Obat_Rawat_Jalan="",hppfarmasi="",VALIDASIULANGBERIOBAT="",DEPOAKTIFOBAT="",NORESEPAKTIF="no",diagnosa_pasien, no_claim,ObatRutin,
        sqlpscariobat2="select databarang.nama_brng,jenis.nama,detail_pemberian_obat.biaya_obat, detail_pemberian_obat.jml, detail_pemberian_obat.total, databarang.kode_brng, aturan_pakai.aturan "
                        + "from detail_pemberian_obat inner join databarang inner join jenis on detail_pemberian_obat.kode_brng=databarang.kode_brng and databarang.kdjns=jenis.kdjns LEFT JOIN aturan_pakai ON detail_pemberian_obat.no_rawat=aturan_pakai.no_rawat AND "
                        + "databarang.kode_brng=aturan_pakai.kode_brng and detail_pemberian_obat.jam=aturan_pakai.jam where detail_pemberian_obat.no_rawat=? and detail_pemberian_obat.tgl_perawatan=? AND detail_pemberian_obat.jam=? group by detail_pemberian_obat.kode_brng order by jenis.nama";
    private DlgCariBangsal caribangsal=new DlgCariBangsal(null,false);
    public DlgCariAturanPakai aturanpakai=new DlgCariAturanPakai(null,false);
    private DlgCariMetodeRacik metoderacik=new DlgCariMetodeRacik(null,false);
    private WarnaTable2 warna=new WarnaTable2();
    private WarnaTable2 warna2=new WarnaTable2();
    private WarnaTable2 warna3=new WarnaTable2();
    private riwayatobat Trackobat=new riwayatobat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;
    private String[] arrSplit;
    private boolean sukses=true;
    private ApotekBPJSCekReferensiDPHO refobatbpjs = new ApotekBPJSCekReferensiDPHO(null, false);
    private ApiApotekBPJS api = new ApiApotekBPJS();
    
    /** Creates new form DlgPenyakit
     * @param parent
     * @param modal */
    public ApotekBPJSKirimObatEditObat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(656,250);

        tabModeobat=new DefaultTableModel(null,new Object[]{
                "K","Jumlah","Kode Barang","Nama Barang","Signa 1","Signa 2",
            }){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==1)||(colIndex==4)||(colIndex==5)) {
                    a=true;
                }
                return a;
             }
            
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class
             };
             
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        
        tbObat.setModel(tabModeobat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 6; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                try {
                    ps2=koneksi.prepareStatement("select kode_ppk from setting");

                    try {
                        rs2=ps2.executeQuery();
                        rs2.next();

                        if(rs2.getString("kode_ppk").equals("0090R034")){
                            column.setPreferredWidth(20);
                        }else {
                            column.setMinWidth(0);
                            column.setMaxWidth(0);
                        }

                    } catch (Exception e) {
                        System.out.println("Notif : "+rs2);
                    } finally{
                        rs2.close();  
                    }

                } catch (Exception e) {
                    System.out.println("Notif : "+rs2);
                }
            }else if(i==1){
                column.setPreferredWidth(45);
            }else if(i==2){
                column.setPreferredWidth(75);
            }else if(i==3){
                column.setPreferredWidth(200);
            }else if(i==4){
                column.setPreferredWidth(50);
            }else if(i==5){
                column.setPreferredWidth(50);
            }            
        }
        warna.kolom=1;
        tbObat.setDefaultRenderer(Object.class,warna);
        
        tabModeObatRacikan=new DefaultTableModel(null,new Object[]{
                "No","Nama Racikan","Kode Racik","Metode Racik","Jml.Racik",
                "Aturan Pakai","Keterangan"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = true;
                if ((colIndex==0)||(colIndex==2)||(colIndex==3)) {
                    a=false;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };

        tbObatRacikan.setModel(tabModeObatRacikan);
        tbObatRacikan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObatRacikan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
        
        for (i = 0; i < 7; i++) {
            TableColumn column = tbObatRacikan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(25);
            }else if(i==1){
                column.setPreferredWidth(250);
            }else if(i==2){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==3){
                column.setPreferredWidth(100);
            }else if(i==4){
                column.setPreferredWidth(60);
            }else if(i==5){
                column.setPreferredWidth(200);
            }else if(i==6){
                column.setPreferredWidth(250);
            }
        }

        warna2.kolom=4;
        tbObatRacikan.setDefaultRenderer(Object.class,warna2);
        
        tabModeDetailObatRacikan=new DefaultTableModel(null,new Object[]{
                "No","Jumlah","Kode Barang","Nama Barang","Signa 1","Signa 2",
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==1)||(colIndex==4)||(colIndex==5)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class,
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };

        tbDetailObatRacikan.setModel(tabModeDetailObatRacikan);
        tbDetailObatRacikan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbDetailObatRacikan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
        
        for (i = 0; i < 6; i++) {
            TableColumn column = tbDetailObatRacikan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(25);
            }else if(i==1){
                column.setPreferredWidth(40);
            }else if(i==2){
                column.setPreferredWidth(100);
            }else if(i==3){
                column.setPreferredWidth(200);
            }else if(i==4){
                column.setPreferredWidth(50);
            }else if(i==5){
                column.setPreferredWidth(50);
            }
        }

        warna3.kolom=9;
        tbDetailObatRacikan.setDefaultRenderer(Object.class,warna3);
        
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        BtnCariActionPerformed(null);
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        BtnCariActionPerformed(null);
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        BtnCariActionPerformed(null);
                    }
                }
            });
        }
        
        aturanpakai.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(aturanpakai.getTable().getSelectedRow()!= -1){  
                    if(TabRawat.getSelectedIndex()==0){
                        tbObat.setValueAt(aturanpakai.getTable().getValueAt(aturanpakai.getTable().getSelectedRow(),0).toString(),tbObat.getSelectedRow(),11);
                        tbObat.requestFocus();
                    }else if(TabRawat.getSelectedIndex()==1){
                        tbObatRacikan.setValueAt(aturanpakai.getTable().getValueAt(aturanpakai.getTable().getSelectedRow(),0).toString(),tbObatRacikan.getSelectedRow(),5);
                        tbObatRacikan.requestFocus();
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
        
        metoderacik.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(metoderacik.getTable().getSelectedRow()!= -1){  
                    tbObatRacikan.setValueAt(metoderacik.getTable().getValueAt(metoderacik.getTable().getSelectedRow(),1).toString(),tbObatRacikan.getSelectedRow(),2);
                    tbObatRacikan.setValueAt(metoderacik.getTable().getValueAt(metoderacik.getTable().getSelectedRow(),2).toString(),tbObatRacikan.getSelectedRow(),3);
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
        
        metoderacik.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    metoderacik.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }); 
        
        try {
            aktifkanbatch = koneksiDB.AKTIFKANBATCHOBAT();
            if(aktifkanbatch.equals("no")){
                ppStok.setVisible(true);
            }else{
                ppStok.setVisible(false);
            }
            otorisasi=koneksiDB.USERPCARE()+":"+koneksiDB.PASSPCARE()+":095";
            URL=koneksiDB.URLAPIPCARE();
        } catch (Exception e) {
            System.out.println("E : "+e);
            aktifkanbatch = "no";
            ppStok.setVisible(true);
        }
        
        try {
            hppfarmasi=koneksiDB.HPPFARMASI();
        } catch (Exception e) {
            hppfarmasi="dasar";
        }
        
        try {
            VALIDASIULANGBERIOBAT=koneksiDB.VALIDASIULANGBERIOBAT();
        } catch (Exception e) {
            VALIDASIULANGBERIOBAT="no";
        }
        
        try {
            DEPOAKTIFOBAT = koneksiDB.DEPOAKTIFOBAT();
        } catch (Exception e) {
            System.out.println("E : "+e);
            DEPOAKTIFOBAT = "";
        }
        
        try {
            psrekening=koneksi.prepareStatement("select * from set_akun_ralan");
            try {
                rsrekening=psrekening.executeQuery();
                while(rsrekening.next()){
                    Suspen_Piutang_Obat_Ralan=rsrekening.getString("Suspen_Piutang_Obat_Ralan");
                    Obat_Ralan=rsrekening.getString("Obat_Ralan");
                    HPP_Obat_Rawat_Jalan=rsrekening.getString("HPP_Obat_Rawat_Jalan");
                    Persediaan_Obat_Rawat_Jalan=rsrekening.getString("Persediaan_Obat_Rawat_Jalan");
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
            NORESEPAKTIF=koneksiDB.NORESEPAKTIF();
        } catch (Exception e) {
            NORESEPAKTIF="no";
        }
        
        tampilkan_ppnobat_ralan=Sequel.cariIsi("select set_nota.tampilkan_ppnobat_ralan from set_nota"); 
        jam();
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
        ppStok = new javax.swing.JMenuItem();
        ppStok1 = new javax.swing.JMenuItem();
        Kd2 = new widget.TextBox();
        TNoRw = new widget.TextBox();
        Tanggal = new widget.TextBox();
        Jam = new widget.TextBox();
        KdPj = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnSeek5 = new widget.Button();
        BtnTambah1 = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        label13 = new widget.Label();
        BtnKeluar = new widget.Button();
        FormInput = new widget.PanelBiasa();
        jLabel8 = new widget.Label();
        DTPTgl = new widget.Tanggal();
        cmbJam = new widget.ComboBox();
        cmbMnt = new widget.ComboBox();
        cmbDtk = new widget.ComboBox();
        ChkJln = new widget.CekBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        LblNoRawat = new widget.TextBox();
        TanggalPelayanan = new widget.Tanggal();
        jLabel20 = new widget.Label();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        jLabel13 = new widget.Label();
        jLabel15 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel14 = new widget.Label();
        TResep = new widget.TextBox();
        Iterasi = new javax.swing.JComboBox<>();
        jLabel16 = new widget.Label();
        JnsObat = new javax.swing.JComboBox<>();
        jLabel17 = new widget.Label();
        jLabel4 = new widget.Label();
        NoKartu = new widget.TextBox();
        NoSEP = new widget.TextBox();
        jLabel18 = new widget.Label();
        Lahir = new widget.TextBox();
        jLabel19 = new widget.Label();
        TabRawat = new javax.swing.JTabbedPane();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        Scroll1 = new widget.ScrollPane();
        tbObatRacikan = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbDetailObatRacikan = new widget.Table();

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Jumlah");
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

        ppStok.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppStok.setForeground(new java.awt.Color(50, 50, 50));
        ppStok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppStok.setText("Tampilkan Semua Stok");
        ppStok.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppStok.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppStok.setName("ppStok"); // NOI18N
        ppStok.setPreferredSize(new java.awt.Dimension(200, 25));
        ppStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppStokActionPerformed(evt);
            }
        });
        Popup.add(ppStok);

        ppStok1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppStok1.setForeground(new java.awt.Color(50, 50, 50));
        ppStok1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppStok1.setText("Tampilkan Stok Lokasi Lain");
        ppStok1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppStok1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppStok1.setName("ppStok1"); // NOI18N
        ppStok1.setPreferredSize(new java.awt.Dimension(200, 25));
        ppStok1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppStok1ActionPerformed(evt);
            }
        });
        Popup.add(ppStok1);

        Kd2.setHighlighter(null);
        Kd2.setName("Kd2"); // NOI18N
        Kd2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kd2KeyPressed(evt);
            }
        });

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N

        Tanggal.setHighlighter(null);
        Tanggal.setName("Tanggal"); // NOI18N

        Jam.setHighlighter(null);
        Jam.setName("Jam"); // NOI18N

        KdPj.setHighlighter(null);
        KdPj.setName("KdPj"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Obat Apotek BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Liberation Sans", 0, 15), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setToolTipText("Alt+C");
        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(340, 23));
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
        BtnAll.setToolTipText("Alt+2");
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

        BtnSeek5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/011.png"))); // NOI18N
        BtnSeek5.setMnemonic('4');
        BtnSeek5.setToolTipText("Alt+4");
        BtnSeek5.setName("BtnSeek5"); // NOI18N
        BtnSeek5.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek5ActionPerformed(evt);
            }
        });
        BtnSeek5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek5KeyPressed(evt);
            }
        });
        panelisi3.add(BtnSeek5);

        BtnTambah1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        BtnTambah1.setMnemonic('3');
        BtnTambah1.setToolTipText("Alt+3");
        BtnTambah1.setName("BtnTambah1"); // NOI18N
        BtnTambah1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambah1ActionPerformed(evt);
            }
        });
        panelisi3.add(BtnTambah1);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelisi3.add(BtnHapus);

        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(65, 23));
        panelisi3.add(label13);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('5');
        BtnKeluar.setToolTipText("Alt+5");
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
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        jLabel8.setText("Tanggal :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel8);
        jLabel8.setBounds(4, 40, 65, 23);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-03-2024" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(72, 40, 90, 23);

        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam.setName("cmbJam"); // NOI18N
        cmbJam.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJamKeyPressed(evt);
            }
        });
        FormInput.add(cmbJam);
        cmbJam.setBounds(166, 40, 62, 23);

        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt.setName("cmbMnt"); // NOI18N
        cmbMnt.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbMnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMntKeyPressed(evt);
            }
        });
        FormInput.add(cmbMnt);
        cmbMnt.setBounds(231, 40, 62, 23);

        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDtk.setName("cmbDtk"); // NOI18N
        cmbDtk.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbDtk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbDtkKeyPressed(evt);
            }
        });
        FormInput.add(cmbDtk);
        cmbDtk.setBounds(296, 40, 62, 23);

        ChkJln.setBorder(null);
        ChkJln.setSelected(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkJln.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkJln.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkJln.setName("ChkJln"); // NOI18N
        ChkJln.setPreferredSize(new java.awt.Dimension(22, 23));
        ChkJln.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkJlnActionPerformed(evt);
            }
        });
        FormInput.add(ChkJln);
        ChkJln.setBounds(361, 40, 22, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel10);
        jLabel10.setBounds(4, 10, 65, 23);

        jLabel11.setText("No.RM :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel11);
        jLabel11.setBounds(175, 10, 65, 23);

        jLabel12.setText("Nama Pasien :");
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel12);
        jLabel12.setBounds(330, 10, 80, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(TPasien);
        TPasien.setBounds(410, 10, 237, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.setPreferredSize(new java.awt.Dimension(207, 23));
        TNoRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoRMActionPerformed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(240, 10, 90, 23);

        LblNoRawat.setEditable(false);
        LblNoRawat.setName("LblNoRawat"); // NOI18N
        LblNoRawat.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(LblNoRawat);
        LblNoRawat.setBounds(72, 10, 123, 23);

        TanggalPelayanan.setForeground(new java.awt.Color(50, 70, 50));
        TanggalPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-03-2024" }));
        TanggalPelayanan.setDisplayFormat("dd-MM-yyyy");
        TanggalPelayanan.setName("TanggalPelayanan"); // NOI18N
        TanggalPelayanan.setOpaque(false);
        TanggalPelayanan.setPreferredSize(new java.awt.Dimension(95, 23));
        TanggalPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TanggalPelayananActionPerformed(evt);
            }
        });
        TanggalPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalPelayananKeyPressed(evt);
            }
        });
        FormInput.add(TanggalPelayanan);
        TanggalPelayanan.setBounds(435, 130, 90, 23);

        jLabel20.setText("Tanggal Pelayanan :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(320, 130, 110, 23);

        NmPoli.setEditable(false);
        NmPoli.setHighlighter(null);
        NmPoli.setName("NmPoli"); // NOI18N
        FormInput.add(NmPoli);
        NmPoli.setBounds(150, 130, 170, 23);

        KdPoli.setEditable(false);
        KdPoli.setHighlighter(null);
        KdPoli.setName("KdPoli"); // NOI18N
        FormInput.add(KdPoli);
        KdPoli.setBounds(70, 130, 75, 23);

        jLabel13.setText("Asal Poli :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(10, 130, 60, 23);

        jLabel15.setText("Dokter DPJP :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 100, 70, 23);

        KdDPJP.setEditable(false);
        KdDPJP.setHighlighter(null);
        KdDPJP.setName("KdDPJP"); // NOI18N
        FormInput.add(KdDPJP);
        KdDPJP.setBounds(70, 100, 75, 23);

        NmDPJP.setEditable(false);
        NmDPJP.setHighlighter(null);
        NmDPJP.setName("NmDPJP"); // NOI18N
        FormInput.add(NmDPJP);
        NmDPJP.setBounds(150, 100, 170, 23);

        jLabel14.setText("No Resep :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(300, 100, 80, 23);

        TResep.setHighlighter(null);
        TResep.setName("TResep"); // NOI18N
        TResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TResepKeyPressed(evt);
            }
        });
        FormInput.add(TResep);
        TResep.setBounds(385, 100, 145, 23);

        Iterasi.setBackground(new java.awt.Color(255, 255, 255));
        Iterasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        Iterasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0. Tanpa Iterasi", "1. Dengan Iterasi" }));
        Iterasi.setName("Iterasi"); // NOI18N
        FormInput.add(Iterasi);
        Iterasi.setBounds(610, 100, 110, 24);

        jLabel16.setText("Iterasi :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(555, 100, 50, 23);

        JnsObat.setBackground(new java.awt.Color(255, 255, 255));
        JnsObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        JnsObat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1. Obat PRB", "2. Obat Kronis Belum Stabil", "3. Obat Kemoterapi" }));
        JnsObat.setSelectedIndex(1);
        JnsObat.setName("JnsObat"); // NOI18N
        FormInput.add(JnsObat);
        JnsObat.setBounds(365, 70, 175, 24);

        jLabel17.setText("Jns Obat :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(310, 70, 50, 23);

        jLabel4.setText("No.Kartu :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(535, 70, 70, 23);

        NoKartu.setEditable(false);
        NoKartu.setHighlighter(null);
        NoKartu.setName("NoKartu"); // NOI18N
        NoKartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoKartuActionPerformed(evt);
            }
        });
        FormInput.add(NoKartu);
        NoKartu.setBounds(610, 70, 110, 23);

        NoSEP.setEditable(false);
        NoSEP.setHighlighter(null);
        NoSEP.setName("NoSEP"); // NOI18N
        FormInput.add(NoSEP);
        NoSEP.setBounds(70, 70, 220, 23);

        jLabel18.setText("No.SEP :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(20, 70, 50, 23);

        Lahir.setEditable(false);
        Lahir.setHighlighter(null);
        Lahir.setName("Lahir"); // NOI18N
        Lahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LahirKeyPressed(evt);
            }
        });
        FormInput.add(Lahir);
        Lahir.setBounds(610, 130, 110, 23);

        jLabel19.setText("Tanggal Lahir :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(525, 130, 80, 23);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        TabRawat.setBackground(new java.awt.Color(255, 255, 253));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll.setComponentPopupMenu(Popup);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ScrollMouseClicked(evt);
            }
        });

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(Popup);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbObatPropertyChange(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        TabRawat.addTab("Umum", Scroll);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(300, 102));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);
        Scroll1.setPreferredSize(new java.awt.Dimension(454, 90));

        tbObatRacikan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObatRacikan.setName("tbObatRacikan"); // NOI18N
        tbObatRacikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatRacikanKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbObatRacikan);

        jPanel3.add(Scroll1, java.awt.BorderLayout.PAGE_START);

        Scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbDetailObatRacikan.setAutoCreateRowSorter(true);
        tbDetailObatRacikan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbDetailObatRacikan.setComponentPopupMenu(Popup);
        tbDetailObatRacikan.setName("tbDetailObatRacikan"); // NOI18N
        tbDetailObatRacikan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDetailObatRacikanMouseClicked(evt);
            }
        });
        tbDetailObatRacikan.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDetailObatRacikanPropertyChange(evt);
            }
        });
        tbDetailObatRacikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDetailObatRacikanKeyPressed(evt);
            }
        });
        Scroll2.setViewportView(tbDetailObatRacikan);

        jPanel3.add(Scroll2, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Racikan", jPanel3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbObat.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        if(TabRawat.getSelectedIndex()==0){
            tampilobat();
        }else if(TabRawat.getSelectedIndex()==1){
            if(tbObatRacikan.getRowCount()!=0){
                if(tbObatRacikan.getSelectedRow()!= -1){
                    if(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),0).toString().equals("")||
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),1).toString().equals("")||
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),2).toString().equals("")||
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),3).toString().equals("")||
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),4).toString().equals("")||
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),5).toString().equals("")||
                            tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),6).toString().equals("")){
                        JOptionPane.showMessageDialog(null,"Silahkan lengkapi data racikan..!!");
                    }else{
                        tampildetailracikanobat();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Silahkan pilih racikan..!!");
                }
            }else{
                JOptionPane.showMessageDialog(null,"Silahkan masukkan racikan..!!");
            }
        }   
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
        BtnCariActionPerformed(evt);
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari, TCari);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tbObat.getRowCount()!=0){
            try {
                getDataobat();
            } catch (java.lang.NullPointerException e) {
            }
            
            if(evt.getClickCount()==2){
                if(akses.getform().equals("DlgPemberianObat")){
                    dispose();
                }
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tbObat.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                try {
                    getDataobat();
                    i=tbObat.getSelectedColumn();
                    if(i==8){
                        try {
                            if(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("0")||tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("")||tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("0.0")||tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("0,0")) {
                                tbObat.setValueAt(embalase,tbObat.getSelectedRow(),8);
                            }
                        } catch (Exception e) {
                            tbObat.setValueAt(0,tbObat.getSelectedRow(),8);
                        }
                    }else if(i==9){
                        try {
                            if(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0.0")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0,0")) {
                                tbObat.setValueAt(tuslah,tbObat.getSelectedRow(),9);
                            }
                        } catch (Exception e) {
                            tbObat.setValueAt(0,tbObat.getSelectedRow(),9);
                        }
                            
                        TCari.setText("");
                        TCari.requestFocus();
                    }else if((i==10)||(i==3)){
                        TCari.setText("");
                        TCari.requestFocus();
                    }else if(i==11){
                        TCari.setText("");
                        TCari.requestFocus();
                    }
                } catch (java.lang.NullPointerException e) {
                }
            }else if((evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getDataobat();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_DELETE){
                i=tbObat.getSelectedColumn();
                if((i==1)||(i==11)||(i==8)||(i==9)){
                    if(tbObat.getSelectedRow()!= -1){
                        tbObat.setValueAt("",tbObat.getSelectedRow(),i);
                    }
                }   
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                i=tbObat.getSelectedColumn();
                if(i!=11){
                    TCari.requestFocus();
                }                
            }else if(evt.getKeyCode()==KeyEvent.VK_RIGHT){
                i=tbObat.getSelectedColumn();
                if(i==2){
                    try {
                        getDataobat();
                        
                        try {
                            if(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("0")||tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("")||tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("0.0")||tbObat.getValueAt(tbObat.getSelectedRow(),8).toString().equals("0,0")) {
                                tbObat.setValueAt(embalase,tbObat.getSelectedRow(),8);
                            }
                        } catch (Exception e) {
                            tbObat.setValueAt(0,tbObat.getSelectedRow(),8);
                        }

                        try {
                            if(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0.0")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0,0")) {
                                tbObat.setValueAt(tuslah,tbObat.getSelectedRow(),9);
                            }
                        } catch (Exception e) {
                            tbObat.setValueAt(0,tbObat.getSelectedRow(),9);
                        }  
                    } catch (Exception e) {
                        tbObat.setValueAt(0,tbObat.getSelectedRow(),10);
                    }
                }else if(i==11){
                    akses.setform("DlgCariObat");
                    aturanpakai.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                    aturanpakai.setLocationRelativeTo(internalFrame1);
                    aturanpakai.setVisible(true);
                }
            }   
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void Kd2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kd2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kd2KeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed
    
private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")){
            Valid.textKosong(TCari,"Data");
        }else if(NoSEP.getText().trim().equals("")){
            Valid.textKosong(TCari,"Nomor Sep");                                      
        }else if(KdDPJP.getText().trim().equals("")){
            Valid.textKosong(TCari,"Dokter");                                      
        }else if(KdPoli.getText().trim().equals("")){
            Valid.textKosong(TCari,"Poliklinik");                                      
        }else if(TResep.getText().trim().equals("")){
            Valid.textKosong(TCari,"Nomor Resep");                                      
        }else if(Lahir.getText().trim().equals("")){
            Valid.textKosong(TCari,"Lahir");                                      
        }else{
            int reply = JOptionPane.showConfirmDialog(rootPane,"Eeiiiiiits, udah bener belum data yang mau disimpan..?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {  
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                    requestEntity = new HttpEntity(headers);
                    URL = link + "/sjpresep/v3/insert";
                    System.out.println(URL);
                    requestJson = "{"
                                    + "\"TGLSJP\": \"" + Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+cmbJam.getSelectedItem()+":"+cmbMnt.getSelectedItem()+":"+cmbDtk.getSelectedItem() + "\","
                                    + "\"REFASALSJP\": \"" + NoSEP.getText() + "\","
                                    + "\"POLIRSP\": \"" + KdPoli.getText() + "\","
                                    + "\"KDJNSOBAT\": \"" + JnsObat.getSelectedItem().toString().substring(0, 1) + "\","
                                    + "\"NORESEP\": \"" + TResep.getText()+ "\", "
                                    + "\"IDUSERSJP\": \"USERSIMRS_" + akses.getkode() + "\","
                                    + "\"TGLRSP\": \"" + Valid.SetTgl(TanggalPelayanan.getSelectedItem()+"") + "\", "
                                    + "\"TGLPELRSP\": \"" + Valid.SetTgl(TanggalPelayanan.getSelectedItem()+"") + "\","
                                    + "\"KdDokter\": \"0\","
                                    + "\"iterasi\":\"" + Iterasi.getSelectedItem().toString().substring(0, 1) + "\""
                                + "}  ";
                    requestEntity = new HttpEntity(requestJson, headers);
//                    System.out.println(requestEntity.toString());
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                    nameNode = root.path("metaData");
                    
                    if (nameNode.path("code").asText().equals("200")) {
                        
                    }else{
                        JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                    }
                    
                    

                    ChkJln.setSelected(false);
                    Sequel.AutoComitFalse();
                    sukses=true;
                    ttlhpp=0;ttljual=0;
                    for(i=0;i<tbObat.getRowCount();i++){ 
                        if(Valid.SetAngka(tbObat.getValueAt(i,1).toString())>0){                        
                            
                            if(Sequel.menyimpantf2("detail_pemberian_obat_apotekbpjs","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","data",15,new String[]{
                                Valid.SetTgl(DTPTgl.getSelectedItem()+""),cmbJam.getSelectedItem()+":"+cmbMnt.getSelectedItem()+":"+cmbDtk.getSelectedItem(),TNoRw.getText(),tbObat.getValueAt(i,2).toString(),tbObat.getValueAt(i,13).toString(),
                                tbObat.getValueAt(i,6).toString(),""+Double.parseDouble(tbObat.getValueAt(i,1).toString()),
                                tbObat.getValueAt(i,8).toString(),tbObat.getValueAt(i,9).toString(),""+Math.round(Double.parseDouble(tbObat.getValueAt(i,8).toString())+
                                    Double.parseDouble(tbObat.getValueAt(i,9).toString())+(Double.parseDouble(tbObat.getValueAt(i,6).toString())*
                                    Double.parseDouble(tbObat.getValueAt(i,1).toString()))),
                                "Ralan","",tbObat.getValueAt(i,16).toString(),tbObat.getValueAt(i,17).toString(),tbObat.getValueAt(i,19).toString()
                            })==true){                                     
                                    
                                
//                                    Trackobat.catatRiwayat(tbObat.getValueAt(i,2).toString(),0,Double.parseDouble(tbObat.getValueAt(i,1).toString()),"Pemberian Obat",akses.getkode(),kdgudang.getText(),"Simpan",tbObat.getValueAt(i,16).toString(),tbObat.getValueAt(i,17).toString(),TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText());
//                                    Sequel.menyimpan("gudangbarang","'"+tbObat.getValueAt(i,2).toString()+"','"+""+"','-"+Double.parseDouble(tbObat.getValueAt(i,1).toString())+"','"+tbObat.getValueAt(i,16).toString()+"','"+tbObat.getValueAt(i,17).toString()+"'", 
//                                             "stok=stok-'"+Double.parseDouble(tbObat.getValueAt(i,1).toString())+"'","kode_brng='"+tbObat.getValueAt(i,2).toString()+"' and kd_bangsal='"+kdgudang.getText()+"' and no_batch='"+tbObat.getValueAt(i,16).toString()+"' and no_faktur='"+tbObat.getValueAt(i,17).toString()+"'");   
                                
                            }else{
                                sukses=false;
                            }                    
                        }
                    }  

                    for(i=0;i<tbObatRacikan.getRowCount();i++){ 
                        if(Valid.SetAngka(tbObatRacikan.getValueAt(i,4).toString())>0){ 
                            if(Sequel.menyimpantf2("obat_racikan_apotekbpjs","?,?,?,?,?,?,?,?,?","Obat Racikan",9,new String[]{
                               Valid.SetTgl(DTPTgl.getSelectedItem()+""),cmbJam.getSelectedItem()+":"+cmbMnt.getSelectedItem()+":"+cmbDtk.getSelectedItem(),TNoRw.getText(),
                               tbObatRacikan.getValueAt(i,0).toString(),tbObatRacikan.getValueAt(i,1).toString(),
                               tbObatRacikan.getValueAt(i,2).toString(),tbObatRacikan.getValueAt(i,4).toString(),
                               tbObatRacikan.getValueAt(i,5).toString(),tbObatRacikan.getValueAt(i,6).toString()
                            })==false){
                                sukses=false;
                            }
                        }
                    }

                    for(i=0;i<tbDetailObatRacikan.getRowCount();i++){ 
                        if(Valid.SetAngka(tbDetailObatRacikan.getValueAt(i,10).toString())>0){
                            if(Sequel.menyimpantf2("detail_obat_racikan_apotekbpjs","?,?,?,?,?","Data",5,new String[]{
                               Valid.SetTgl(DTPTgl.getSelectedItem()+""),cmbJam.getSelectedItem()+":"+cmbMnt.getSelectedItem()+":"+cmbDtk.getSelectedItem(),TNoRw.getText(),
                               tbDetailObatRacikan.getValueAt(i,0).toString(),tbDetailObatRacikan.getValueAt(i,1).toString()
                            })==true){
                                if(Sequel.menyimpantf2("detail_pemberian_obat_apotekbpjs","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","data",15,new String[]{
                                    Valid.SetTgl(DTPTgl.getSelectedItem()+""),cmbJam.getSelectedItem()+":"+cmbMnt.getSelectedItem()+":"+cmbDtk.getSelectedItem(),TNoRw.getText(),
                                    tbDetailObatRacikan.getValueAt(i,1).toString(),tbDetailObatRacikan.getValueAt(i,5).toString(),
                                    tbDetailObatRacikan.getValueAt(i,4).toString(),""+Double.parseDouble(tbDetailObatRacikan.getValueAt(i,10).toString()),
                                    tbDetailObatRacikan.getValueAt(i,11).toString(),tbDetailObatRacikan.getValueAt(i,12).toString(),
                                    ""+Math.round(Double.parseDouble(tbDetailObatRacikan.getValueAt(i,11).toString())+                                        
                                        Double.parseDouble(tbDetailObatRacikan.getValueAt(i,12).toString())+
                                        (Double.parseDouble(tbDetailObatRacikan.getValueAt(i,4).toString())*
                                        Double.parseDouble(tbDetailObatRacikan.getValueAt(i,10).toString()))),
                                    "Ralan","",tbDetailObatRacikan.getValueAt(i,16).toString(),tbDetailObatRacikan.getValueAt(i,17).toString(),"-"
                                })==true){
//                                        Trackobat.catatRiwayat(tbDetailObatRacikan.getValueAt(i,1).toString(),0,Double.parseDouble(tbDetailObatRacikan.getValueAt(i,10).toString()),"Pemberian Obat",akses.getkode(),kdgudang.getText(),"Simpan",tbDetailObatRacikan.getValueAt(i,16).toString(),tbDetailObatRacikan.getValueAt(i,17).toString(),TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText());
//                                        Sequel.menyimpan("gudangbarang","'"+tbDetailObatRacikan.getValueAt(i,1).toString()+"','"+kdgudang.getText()+"','-"+Double.parseDouble(tbDetailObatRacikan.getValueAt(i,10).toString())+"','"+tbDetailObatRacikan.getValueAt(i,16).toString()+"','"+tbDetailObatRacikan.getValueAt(i,17).toString()+"'", 
//                                                 "stok=stok-'"+Double.parseDouble(tbDetailObatRacikan.getValueAt(i,10).toString())+"'","kode_brng='"+tbDetailObatRacikan.getValueAt(i,1).toString()+"' and kd_bangsal='"+kdgudang.getText()+"' and no_batch='"+tbDetailObatRacikan.getValueAt(i,16).toString()+"' and no_faktur='"+tbDetailObatRacikan.getValueAt(i,17).toString()+"'");  
                                    
                                }else{
                                    sukses=false;
                                }  
                            }else{
                                sukses=false;
                            }   
                        }
                    }
                    if(sukses==true){
                        Sequel.Commit();
                        for(i=0;i<tbObat.getRowCount();i++){
                            tbObat.setValueAt("",i,1);
                        }
                        Valid.tabelKosong(tabModeObatRacikan);
                        Valid.tabelKosong(tabModeDetailObatRacikan);
                    }else{
                        sukses=false;
                        JOptionPane.showMessageDialog(null,"Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                        Sequel.RollBack();
                    }
                    
                    Sequel.AutoComitTrue();
                    ChkJln.setSelected(true);
                } catch (Exception ex) {
                    System.out.println(ex);  
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }                  
        }
        
        
}//GEN-LAST:event_BtnSimpanActionPerformed

private void BtnSeek5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek5ActionPerformed
    DlgCariKonversi carikonversi=new DlgCariKonversi(null,false);
    carikonversi.setLocationRelativeTo(internalFrame1);
    carikonversi.setAlwaysOnTop(false);
    carikonversi.setVisible(true);
}//GEN-LAST:event_BtnSeek5ActionPerformed

private void BtnSeek5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek5KeyPressed
// TODO add your handling code here:
}//GEN-LAST:event_BtnSeek5KeyPressed

private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
    if(TabRawat.getSelectedIndex()==0){
        for(i=0;i<tbObat.getRowCount();i++){ 
            tbObat.setValueAt("",i,1);
            tbObat.setValueAt(0,i,10);
            tbObat.setValueAt(0,i,9);
            tbObat.setValueAt(0,i,8);
        }
    }else if(TabRawat.getSelectedIndex()==1){
        for(i=0;i<tbDetailObatRacikan.getRowCount();i++){ 
            tbDetailObatRacikan.setValueAt("",i,9);
            tbDetailObatRacikan.setValueAt(0,i,10);
            tbDetailObatRacikan.setValueAt(0,i,11);
            tbDetailObatRacikan.setValueAt(0,i,12);
        }
    }       
}//GEN-LAST:event_ppBersihkanActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        if(TabRawat.getSelectedIndex()==0){
            BtnTambah1.setVisible(false);
            BtnHapus.setVisible(false);
            label13.setPreferredSize(new Dimension(65, 23));
        }else if(TabRawat.getSelectedIndex()==1){
            BtnTambah1.setVisible(true);
            BtnHapus.setVisible(true);
            label13.setPreferredSize(new Dimension(1, 23));
        }      
    }//GEN-LAST:event_formWindowActivated

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(noresep.equals("")){
            tampilobat();
        }            
    }//GEN-LAST:event_formWindowOpened

    private void ppStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppStokActionPerformed
        
            if(TabRawat.getSelectedIndex()==0){
                for(i=0;i<tbObat.getRowCount();i++){ 
                    getDataobat(i); 
                }   
            }else if(TabRawat.getSelectedIndex()==1){
                for(i=0;i<tbDetailObatRacikan.getRowCount();i++){ 
                    getDatadetailobatracikan(i);
                }
            }
        
    }//GEN-LAST:event_ppStokActionPerformed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        Valid.pindah(evt,BtnKeluar,cmbJam);
    }//GEN-LAST:event_DTPTglKeyPressed

    private void cmbJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJamKeyPressed
        Valid.pindah(evt,DTPTgl,cmbMnt);
    }//GEN-LAST:event_cmbJamKeyPressed

    private void cmbMntKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMntKeyPressed
        Valid.pindah(evt,cmbJam,cmbDtk);
    }//GEN-LAST:event_cmbMntKeyPressed

    private void cmbDtkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbDtkKeyPressed
    }//GEN-LAST:event_cmbDtkKeyPressed

    private void ChkJlnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkJlnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkJlnActionPerformed

    private void tbObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbObatPropertyChange
        if(this.isVisible()==true){
            getDataobat();
        }
    }//GEN-LAST:event_tbObatPropertyChange

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        if(TabRawat.getSelectedIndex()==0){
            BtnTambah1.setVisible(false);
            BtnHapus.setVisible(false);
            label13.setPreferredSize(new Dimension(65, 23));
        }else if(TabRawat.getSelectedIndex()==1){
            BtnTambah1.setVisible(true);
            BtnHapus.setVisible(true);
            label13.setPreferredSize(new Dimension(1, 23));
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void BtnTambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambah1ActionPerformed
        i=tabModeObatRacikan.getRowCount()+1;
        if(i==99){
            JOptionPane.showMessageDialog(null,"Maksimal 98 Racikan..!!");
        }else{
            tabModeObatRacikan.addRow(new Object[]{""+i,"","","","","",""});
        }            
    }//GEN-LAST:event_BtnTambah1ActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),1).equals("")&&tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),4).equals("")&&tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),5).equals("")&&tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),6).equals("")){
            tabModeObatRacikan.removeRow(tbObatRacikan.getSelectedRow());
        }else{
            JOptionPane.showMessageDialog(null,"Maaf sudah terisi, gak boleh dihapus..!!");
        }
        
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void tbObatRacikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatRacikanKeyPressed
        if(tbObatRacikan.getRowCount()!=0){
            i=tbObatRacikan.getSelectedColumn();
            if(evt.getKeyCode()==KeyEvent.VK_RIGHT){                
                if(i==5){
                    akses.setform("DlgCariObat");
                    aturanpakai.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                    aturanpakai.setLocationRelativeTo(internalFrame1);
                    aturanpakai.setVisible(true);
                }else if(i==3){
                    if(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),1).equals("")){
                        JOptionPane.showMessageDialog(null,"Silahkan masukkan nama racikan..!!");
                        tbObatRacikan.requestFocus();
                    }else{
                        metoderacik.isCek();
                        metoderacik.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                        metoderacik.setLocationRelativeTo(internalFrame1);
                        metoderacik.setVisible(true);
                    }                        
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                if((i==4)){
                    TCari.requestFocus();
                } 
            }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                if((i==6)){
                    if(tbObatRacikan.getSelectedRow()!= -1){
                        if(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),0).toString().equals("")||
                                tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),1).toString().equals("")||
                                tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),2).toString().equals("")||
                                tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),3).toString().equals("")||
                                tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),4).toString().equals("")||
                                tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),5).toString().equals("")){
                            JOptionPane.showMessageDialog(null,"Silahkan lengkapi data racikan..!!");
                        }else{
                            tampildetailracikanobat();
                            TCari.requestFocus();
                        }
                    }else{
                        JOptionPane.showMessageDialog(null,"Silahkan pilih racikan..!!");
                    }
                }
            }
        }
    }//GEN-LAST:event_tbObatRacikanKeyPressed

    private void tbDetailObatRacikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDetailObatRacikanKeyPressed
        if(tbObatRacikan.getSelectedRow()!= -1){
            if(tbDetailObatRacikan.getRowCount()!=0){
                i=tbDetailObatRacikan.getSelectedColumn();
                if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                    try {
                        if(i==11){
                            try {
                                if(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("0.0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("0,0")) {
                                    tbDetailObatRacikan.setValueAt(embalase,tbDetailObatRacikan.getSelectedRow(),11);
                                }
                            } catch (Exception e) {
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),11);
                            }
                        }else if(i==12){
                            try {
                                if(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("0.0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("0,0")) {
                                    tbDetailObatRacikan.setValueAt(tuslah,tbDetailObatRacikan.getSelectedRow(),12);
                                }
                            } catch (Exception e) {
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),12);
                            }
                            TCari.setText("");
                            TCari.requestFocus();
                        }else if((i==9)||(i==10)){
                            try {
                                if(!tabModeDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),9).toString().equals("")){
                                    tbDetailObatRacikan.setValueAt(
                                            Valid.SetAngka8((Double.parseDouble(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),4).toString())
                                            *Double.parseDouble(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),9).toString()))
                                            /Double.parseDouble(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),8).toString()),1)
                                            ,tbDetailObatRacikan.getSelectedRow(),10);
                                    getDatadetailobatracikan();
                                }   
                            } catch (Exception e) {
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),10);
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),11);
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),12);
                            }
                            TCari.setText("");
                            TCari.requestFocus();
                        }else if(i==11){
                            TCari.setText("");
                            TCari.requestFocus();
                        }
                    } catch (java.lang.NullPointerException e) {
                    }
                }else if((evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                    try {
                        if((i==9)||(i==10)){
                            if(!tabModeDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),9).toString().equals("")){
                                tbDetailObatRacikan.setValueAt(
                                        Valid.SetAngka8((Double.parseDouble(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),4).toString())
                                        *Double.parseDouble(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),9).toString()))
                                        /Double.parseDouble(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),8).toString()),1)
                                        ,tbDetailObatRacikan.getSelectedRow(),10);
                                getDatadetailobatracikan();
                            } 
                        }                              
                    } catch (java.lang.NullPointerException e) {
                    }
                }else if(evt.getKeyCode()==KeyEvent.VK_DELETE){
                    if((i==9)||(i==10)){
                        tbDetailObatRacikan.setValueAt("",tbDetailObatRacikan.getSelectedRow(),9);
                        tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),10);
                        tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),11);
                        tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),12);
                    }
                }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                    if(i!=9){
                        TCari.requestFocus();
                    }                
                }else if(evt.getKeyCode()==KeyEvent.VK_RIGHT){
                    if((i==9)||(i==10)||(i==11)||(i==12)){
                        try {
                            if(!tabModeDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),9).toString().equals("")){
                                tbDetailObatRacikan.setValueAt(
                                        Valid.SetAngka8((Double.parseDouble(tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),4).toString())
                                        *Double.parseDouble(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),9).toString()))
                                        /Double.parseDouble(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),8).toString()),1)
                                        ,tbDetailObatRacikan.getSelectedRow(),10);
                            } 
                            
                            try {
                                if(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("0.0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),11).toString().equals("0,0")) {
                                    tbDetailObatRacikan.setValueAt(embalase,tbDetailObatRacikan.getSelectedRow(),11);
                                }
                            } catch (Exception e) {
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),11);
                            }

                            try {
                                if(tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("0.0")||tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(),12).toString().equals("0,0")) {
                                    tbDetailObatRacikan.setValueAt(tuslah,tbDetailObatRacikan.getSelectedRow(),12);
                                }
                            } catch (Exception e) {
                                tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),12);
                            } 
                            getDatadetailobatracikan();
                        } catch (Exception e) {
                            tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),10);
                            tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),11);
                            tbDetailObatRacikan.setValueAt(0,tbDetailObatRacikan.getSelectedRow(),12);
                        }
                    }
                }   
            }
        }else{
            JOptionPane.showMessageDialog(null,"Silahkan pilih No.Racikan terlebih dahulu");
        } 
    }//GEN-LAST:event_tbDetailObatRacikanKeyPressed

    private void tbDetailObatRacikanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDetailObatRacikanPropertyChange
        if(this.isVisible()==true){
            getDatadetailobatracikan();
        }
    }//GEN-LAST:event_tbDetailObatRacikanPropertyChange

    private void tbDetailObatRacikanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDetailObatRacikanMouseClicked
        if(tbObat.getRowCount()!=0){
            try {
                getDatadetailobatracikan();
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_tbDetailObatRacikanMouseClicked

    private void ppStok1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppStok1ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgCekStok ceksetok=new DlgCekStok(null,false);
        ceksetok.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        ceksetok.setLocationRelativeTo(internalFrame1);
        ceksetok.setAlwaysOnTop(false);
        ceksetok.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());  
    }//GEN-LAST:event_ppStok1ActionPerformed

    private void ScrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ScrollMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_ScrollMouseClicked

    private void TanggalPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalPelayananKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalPelayananKeyPressed

    private void TResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TResepKeyPressed

    }//GEN-LAST:event_TResepKeyPressed

    private void TNoRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRMActionPerformed

    private void LahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LahirKeyPressed

    }//GEN-LAST:event_LahirKeyPressed

    private void NoKartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoKartuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoKartuActionPerformed

    private void TanggalPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TanggalPelayananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalPelayananActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ApotekBPJSKirimObatEditObat dialog = new ApotekBPJSKirimObatEditObat(new javax.swing.JFrame(), true);
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
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSeek5;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambah1;
    private widget.CekBox ChkJln;
    private widget.Tanggal DTPTgl;
    private widget.PanelBiasa FormInput;
    private javax.swing.JComboBox<String> Iterasi;
    private widget.TextBox Jam;
    private javax.swing.JComboBox<String> JnsObat;
    private widget.TextBox Kd2;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdPj;
    private widget.TextBox KdPoli;
    private widget.TextBox Lahir;
    private widget.TextBox LblNoRawat;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmPoli;
    private widget.TextBox NoKartu;
    private widget.TextBox NoSEP;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TResep;
    private javax.swing.JTabbedPane TabRawat;
    private widget.TextBox Tanggal;
    private widget.Tanggal TanggalPelayanan;
    private widget.ComboBox cmbDtk;
    private widget.ComboBox cmbJam;
    private widget.ComboBox cmbMnt;
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
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel4;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private widget.Label label13;
    private widget.Label label9;
    private widget.panelisi panelisi3;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppStok;
    private javax.swing.JMenuItem ppStok1;
    private widget.Table tbDetailObatRacikan;
    private widget.Table tbObat;
    private widget.Table tbObatRacikan;
    // End of variables declaration//GEN-END:variables
    public void tampilobat() {        
        z=0;
        for(i=0;i<tbObat.getRowCount();i++){
            if(!tbObat.getValueAt(i,0).toString().equals("")){
                z++;
            }
        }    
        
        pilih=null;
        pilih=new boolean[z]; 
        jumlah=null;
        jumlah=new double[z];
        kodebarang=null;
        kodebarang=new String[z];
        namabarang=null;
        namabarang=new String[z];
        signa_cari1=new String[z];
        signa_cari2=new String[z];
        z=0;        
        for(i=0;i<tbObat.getRowCount();i++){
            if(!tbObat.getValueAt(i,1).toString().equals("")){
                pilih[z]=Boolean.parseBoolean(tbObat.getValueAt(i,0).toString());                
                try {
                    jumlah[z]=Double.parseDouble(tbObat.getValueAt(i,1).toString());
                } catch (Exception e) {
                    jumlah[z]=0;
                }  
                kodebarang[z]=tbObat.getValueAt(i,2).toString();
                namabarang[z]=tbObat.getValueAt(i,3).toString();
                
                if (tbObat.getValueAt(i,1).toString().equals("60")) {
                    signa_cari1[z]="2";
                    signa_cari2[z]="1";
                } else if (tbObat.getValueAt(i,1).toString().equals("90")) {
                    signa_cari1[z]="3";
                    signa_cari2[z]="1";  
                } else if (tbObat.getValueAt(i,1).toString().equals("120")) {
                    signa_cari1[z]="4";
                    signa_cari2[z]="1";
                } else {
                    signa_cari1[z]="1";
                    signa_cari2[z]="1";
                }  
                    
                z++;
            }
        }
        
        Valid.tabelKosong(tabModeobat);             
        
        for(i=0;i<z;i++){
            tabModeobat.addRow(new Object[] {
                pilih[i],jumlah[i],kodebarang[i],namabarang[i],signa_cari1[i],signa_cari2[i]
            });
        }
        
        try {
            psobat=koneksi.prepareStatement(
                "select databarang.kode_brng, databarang.nama_brng from databarang inner join maping_obat_apotek_bpjs ON databarang.kode_brng=maping_obat_apotek_bpjs.kode_brng WHERE databarang.nama_brng LIKE ? ");
            try{
                    
                psobat.setString(1,"%"+TCari.getText().trim()+"%");
                
                rsobat=psobat.executeQuery();
                    while(rsobat.next()){
                            tabModeobat.addRow(new Object[] {false,"",rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),"",""
                        });
                    }   
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rsobat != null){
                    rsobat.close();
                }
                if(psobat != null){
                    psobat.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }            
    }
    
    public void tampildetailracikanobat() {        
        z=0;
        for(i=0;i<tbDetailObatRacikan.getRowCount();i++){
            if(Valid.SetAngka(tbDetailObatRacikan.getValueAt(i,1).toString())>0){
                z++;
            }
        }    
        
        pilih=null;
        pilih=new boolean[z]; 
        jumlah=null;
        jumlah=new double[z];
        kodebarang=null;
        kodebarang=new String[z];
        namabarang=null;
        namabarang=new String[z];
        signa_racikan1=null;
        signa_racikan1=new String[z];
        signa_racikan2=null;
        signa_racikan2=new String[z];
        no=null;
        no=new String[z];
        
        z=0;    
        
        for(i=0;i<tbDetailObatRacikan.getRowCount();i++){
            if(Valid.SetAngka(tbDetailObatRacikan.getValueAt(i,1).toString())>0){
                no[z]=tbDetailObatRacikan.getValueAt(i,0).toString();
                jumlah[z]=Double.parseDouble(tbDetailObatRacikan.getValueAt(i,1).toString());
                kodebarang[z]=tbDetailObatRacikan.getValueAt(i,2).toString();
                namabarang[z]=tbDetailObatRacikan.getValueAt(i,3).toString();
                if (tbDetailObatRacikan.getValueAt(i,1).equals("60")) {
                    signa_racikan1[z]="2";
                    signa_racikan2[z]="1";
                } else if (tbDetailObatRacikan.getValueAt(i,1).equals("90")) {
                    signa_racikan1[z]="3";
                    signa_racikan2[z]="1";
                } else if (tbDetailObatRacikan.getValueAt(i,1).equals("120")) {
                    signa_racikan1[z]="4";
                    signa_racikan2[z]="1";
                } else {
                   signa_racikan1[z]="1";
                   signa_racikan2[z]="1"; 
                }
                
                z++;
            }
        }
        
        Valid.tabelKosong(tabModeDetailObatRacikan);             
        
        for(i=0;i<z;i++){
            tabModeDetailObatRacikan.addRow(new Object[] {
                no[i],jumlah[i],kodebarang[i],namabarang[i],signa_racikan1[i],signa_racikan2[i]
            });
        }
        
        try{
            psobat=koneksi.prepareStatement(
                "select databarang.kode_brng, databarang.nama_brng from databarang inner join maping_obat_apotek_bpjs ON databarang.kode_brng=maping_obat_apotek_bpjs.kode_brng WHERE databarang.nama_brng LIKE ? ");

            try{
                psobat.setString(1,"%"+TCari.getText().trim()+"%");

                rsobat=psobat.executeQuery();
                    while(rsobat.next()){
                        if (rootPaneCheckingEnabled) {
                            
                        }
                        tabModeDetailObatRacikan.addRow(new Object[] {tbObatRacikan.getValueAt(tbObatRacikan.getSelectedRow(),0).toString(),"",rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),"",""});
                    }   
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rsobat != null){
                    rsobat.close();
                }
                if(psobat != null){
                    psobat.close();
                }
            } 
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        } 
    }
    
    public void tampilobat2(String no_resep) {     
        this.noresep=no_resep; 
        try {
            Valid.tabelKosong(tabModeobat);
            Valid.tabelKosong(tabModeObatRacikan);
            Valid.tabelKosong(tabModeDetailObatRacikan);
            
            ps2=koneksi.prepareStatement(
                "select resep_obat.no_resep,resep_obat.tgl_perawatan,resep_obat.jam, resep_obat.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,resep_obat.kd_dokter,dokter.nm_dokter from resep_obat inner join reg_periksa inner join pasien inner join dokter on "+
                "resep_obat.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis and resep_obat.kd_dokter=dokter.kd_dokter where resep_obat.no_resep=?");

            try {
                ps2.setString(1,no_resep);
                rs2=ps2.executeQuery();
                
                while (rs2.next()) { 
                    
                    psobat=koneksi.prepareStatement(
                        "select databarang.kode_brng,databarang.nama_brng,detail_pemberian_obat.jml from detail_pemberian_obat inner join databarang on detail_pemberian_obat.kode_brng=databarang.kode_brng where detail_pemberian_obat.tgl_perawatan=? and detail_pemberian_obat.jam=? "+
                        "and detail_pemberian_obat.no_rawat=? and databarang.kode_brng not in (select detail_obat_racikan.kode_brng from detail_obat_racikan where detail_obat_racikan.tgl_perawatan=? and detail_obat_racikan.jam=? and detail_obat_racikan.no_rawat=?) order by databarang.kode_brng");
                    try{
                        psobat.setString(1,rs2.getString("tgl_perawatan"));
                        psobat.setString(2,rs2.getString("jam"));
                        psobat.setString(3,rs2.getString("no_rawat"));
                        psobat.setString(4,rs2.getString("tgl_perawatan"));
                        psobat.setString(5,rs2.getString("jam"));
                        psobat.setString(6,rs2.getString("no_rawat"));
                        rsobat=psobat.executeQuery();
                        while(rsobat.next()){
                            if (rsobat.getString("jml").equals("60")) {
                                tabModeobat.addRow(new Object[] {false,rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),2,1}); 
                            } else if (rsobat.getString("jml").equals("90")) {
                                tabModeobat.addRow(new Object[] {false,rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),3,1}); 
                            } else if (rsobat.getString("jml").equals("120")) {
                                tabModeobat.addRow(new Object[] {false,rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),4,1}); 
                            } else {
                                tabModeobat.addRow(new Object[] {false,rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),1,1}); 
                            }            
                        }
                    }catch(Exception e){
                        System.out.println("Notifikasi : "+e);
                    }finally{
                        if(rsobat != null){
                            rsobat.close();
                        }

                        if(psobat != null){
                            psobat.close();
                        }
                    }

                    for(i=0;i<tbObat.getRowCount();i++){
                        getDataobat(i);
                    }
                    
//                    racikan
                    psracikan=koneksi.prepareStatement(
                            "select obat_racikan.no_racik,obat_racikan.nama_racik,"+
                            "obat_racikan.kd_racik,metode_racik.nm_racik as metode,"+
                            "obat_racikan.jml_dr,obat_racikan.aturan_pakai,"+
                            "obat_racikan.keterangan from obat_racikan inner join metode_racik "+
                            "on obat_racikan.kd_racik=metode_racik.kd_racik where "+
                            "obat_racikan.tgl_perawatan=? and obat_racikan.jam=? "+
                            "and obat_racikan.no_rawat=? ");
                    try {
                        psracikan.setString(1,rs2.getString("tgl_perawatan"));
                        psracikan.setString(2,rs2.getString("jam"));
                        psracikan.setString(3,rs2.getString("no_rawat"));
                        rsracikan=psracikan.executeQuery();
                        while(rsracikan.next()){
                            tabModeObatRacikan.addRow(new String[]{
                                rsracikan.getString("no_racik"),rsracikan.getString("nama_racik"),rsracikan.getString("kd_racik"),
                                rsracikan.getString("metode"),rsracikan.getString("jml_dr"),rsracikan.getString("aturan_pakai"),
                                rsracikan.getString("keterangan")
                            });
                            
                            psobat=koneksi.prepareStatement(
                                "select databarang.kode_brng,databarang.nama_brng,detail_pemberian_obat.jml from "+
                                "detail_pemberian_obat inner join databarang inner join detail_obat_racikan "+
                                "on detail_pemberian_obat.kode_brng=databarang.kode_brng and "+
                                "detail_pemberian_obat.kode_brng=detail_obat_racikan.kode_brng and "+
                                "detail_pemberian_obat.tgl_perawatan=detail_obat_racikan.tgl_perawatan and "+
                                "detail_pemberian_obat.jam=detail_obat_racikan.jam and "+
                                "detail_pemberian_obat.no_rawat=detail_obat_racikan.no_rawat "+
                                "where detail_pemberian_obat.tgl_perawatan=? and detail_pemberian_obat.jam=? and "+
                                "detail_pemberian_obat.no_rawat=? and detail_obat_racikan.no_racik=? order by databarang.kode_brng");
                            try {
                                psobat.setString(1,rs2.getString("tgl_perawatan"));
                                psobat.setString(2,rs2.getString("jam"));
                                psobat.setString(3,rs2.getString("no_rawat"));
                                psobat.setString(4,rsracikan.getString("no_racik"));
                                rsobat=psobat.executeQuery();
                                while(rsobat.next()){
                                    if (rsobat.getString("jml").equals("60")) {
                                        tabModeDetailObatRacikan.addRow(new Object[] {rsracikan.getString("no_racik"),rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),2,1}); 
                                    } else if (rs2.getString("jml").equals("90")) {
                                        tabModeDetailObatRacikan.addRow(new Object[] {rsracikan.getString("no_racik"),rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),3,1}); 
                                    } else if (rs2.getString("jml").equals("120")) {
                                        tabModeDetailObatRacikan.addRow(new Object[] {rsracikan.getString("no_racik"),rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),4,1}); 
                                    } else {
                                        tabModeDetailObatRacikan.addRow(new Object[] {rsracikan.getString("no_racik"),rsobat.getString("jml"),rsobat.getString("kode_brng"),rsobat.getString("nama_brng"),1,1}); 
                                    }  
                                }                                
                            } catch (Exception e) {
                                System.out.println("Notifikasi Detail Racikan : "+e);
                            } finally{
                                if(rs2!=null){
                                    rs2.close();
                                }
                                if(ps2!=null){
                                    ps2.close();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif Racikan : "+e);
                    } finally{
                        if(rsracikan!=null){
                            rsracikan.close();
                        }
                        if(psracikan!=null){
                            psracikan.close();
                        }
                    }

                    for(i=0;i<tbDetailObatRacikan.getRowCount();i++){
                        getDatadetailobatracikan(i);
                    }
                }
            } catch (Exception e) {
                 System.out.println("Notifikasi racikan : "+e);
            }
            
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }            
    }

    public void emptTeksobat() {
        Kd2.setText(""); 
        TCari.setText("");
        TCari.requestFocus();
    }

    private void getDataobat() {
        if(tbObat.getSelectedRow()!= -1){
            row=tbObat.getSelectedRow();
            if(!tbObat.getValueAt(row,1).toString().equals("")){
                if(Double.parseDouble(tbObat.getValueAt(row,1).toString())>0){
                    Double.parseDouble(tbObat.getValueAt(row,1).toString());
                } 
            }
        }            
    }
    
    private void getDataobat(int data) {        
        Double.parseDouble(tbObat.getValueAt(data,1).toString());
    }

    public JTextField getTextField(){
        return Kd2;
    }

    public JTable getTable(){
        return tbObat;
    }
    
    public Button getButton(){
        return BtnSimpan;
    }
    
    public void isCek(){           
        if(NORESEPAKTIF.equals("yes")){
            DTPTgl.setEnabled(true);
        }else{
            DTPTgl.setEnabled(false);
        }
                    
        TCari.requestFocus();
    }
    
    public void setNoRm(String norwt,String norm,String nama,String tanggal, String jam, String Resep) {        
        aktifpcare="no";
        TNoRw.setText(norwt);
        LblNoRawat.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(nama);
        noresep="";
        Valid.SetTgl(DTPTgl,tanggal);
        Jam.setText(jam);  
        KdPj.setText(Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?",norwt));
        kenaikan=Sequel.cariIsiAngka("select (set_harga_obat_ralan.hargajual/100) from set_harga_obat_ralan where set_harga_obat_ralan.kd_pj=?",KdPj.getText());
        TCari.requestFocus();
        TResep.setText(Resep);
        
        try{     
            ps2=koneksi.prepareStatement("SELECT no_sep, tanggal_lahir, no_kartu, tglsep, kdpolitujuan, nmpolitujuan, kddpjp, nmdpdjp from bridging_sep where no_rawat = ?");
            try {
                ps2.setString(1,norwt);
                rs2=ps2.executeQuery();
                while(rs2.next()){
                    NoSEP.setText(rs2.getString("no_sep"));
                    KdDPJP.setText(rs2.getString("kddpjp"));
                    NmDPJP.setText(rs2.getString("nmdpdjp"));
                    NoKartu.setText(rs2.getString("no_kartu"));
                    KdPoli.setText(rs2.getString("kdpolitujuan"));
                    NmPoli.setText(rs2.getString("nmpolitujuan"));
                    Lahir.setText(rs2.getString("tanggal_lahir"));
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs2!=null){
                    rs2.close();
                }
                if(ps2!=null){
                    ps2.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void jam(){
        ActionListener taskPerformer = new ActionListener(){
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            @Override
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                // Membuat Date
                //Date dt = new Date();
                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if(ChkJln.isSelected()==true){
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                }else if(ChkJln.isSelected()==false){
                    nilai_jam =cmbJam.getSelectedIndex();
                    nilai_menit =cmbMnt.getSelectedIndex();
                    nilai_detik =cmbDtk.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                cmbJam.setSelectedItem(jam);
                cmbMnt.setSelectedItem(menit);
                cmbDtk.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }
    
    public void setDokter(String kodedokter,String namadokter){
        this.kodedokter=kodedokter;
        this.namadokter=namadokter;
    }
    
    private void getDatadetailobatracikan() {
        if(tbDetailObatRacikan.getSelectedRow()!= -1){
            row=tbDetailObatRacikan.getSelectedRow();
            try {
                if(Double.parseDouble(tbDetailObatRacikan.getValueAt(row,1).toString())>0){
                    Double.parseDouble(tbDetailObatRacikan.getValueAt(row,1).toString());
                } 
            } catch (Exception e) {
//                System.out.println("Notif Racikan : "+e);
            }   
        }
    }
    
    private void getDatadetailobatracikan(int data) {       
       Double.parseDouble(tbDetailObatRacikan.getValueAt(data,1).toString());
    }
}
