/*
  by Ananda Widitomo,S.Kom.
  IT - SIMRS Hj. Fatimah Sulhan 
 */

package bridging.satusehat.klaim;
import bridging.satusehat.ApiINACBG;
import bridging.satusehat.ApiSatuSehat;
import bridging.satusehat.SatuSehatHttpLogger;
import bridging.satusehat.tte.DlgTTESatuSehat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author dosen
 */
public final class SatuSehatBundle extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;   
    private int i=0;
    private String link="",json="",iddokter="",idpasien="",idepisode="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat=new SatuSehatCekNIK();
    private SatuSehatTriaseIGD triaseIGD=new SatuSehatTriaseIGD();
    private SatuSehatResumeMedisRanap resumeRanap=new SatuSehatResumeMedisRanap();
    private SatuSehatLaporanPersalinan laporanPersalinan=new SatuSehatLaporanPersalinan();
    private SatuSehatFamilyMemberHistory familyHistory=new SatuSehatFamilyMemberHistory();
    private SatuSehatLaporanOperasi laporanOperasi=new SatuSehatLaporanOperasi();
    private SatuSehatSkriningTBC skriningTBC=new SatuSehatSkriningTBC();
    private SatuSehatObat obat=new SatuSehatObat();
    private SatuSehatLab lab=new SatuSehatLab();
    private SatuSehatRadiologi radiologi=new SatuSehatRadiologi();
    private SatuSehatLaporanTindakanEcho laporanEcho=new SatuSehatLaporanTindakanEcho();
    private SatuSehatLaporanTindakanEswl laporanEswl=new SatuSehatLaporanTindakanEswl();
    private SatuSehatLaporanUsg laporanUsg=new SatuSehatLaporanUsg();
    private SatuSehatLaporanEkg laporanEkg=new SatuSehatLaporanEkg();
    private SatuSehatDiet diet=new SatuSehatDiet();
    private SatuSehatPrognosa prognosa=new SatuSehatPrognosa();
    private SatuSehatTelaahFarmasi telaahFarmasi=new SatuSehatTelaahFarmasi();
    private SatuSehatGoal goal=new SatuSehatGoal();
    private SatuSehatRiskAssessment riskAssessment=new SatuSehatRiskAssessment();
    private SatuSehatLaporanAnestesi laporanAnestesi=new SatuSehatLaporanAnestesi();
    private SatuSehatAlkes alkes=new SatuSehatAlkes();
    private SatuSehatBilling billing=new SatuSehatBilling();
    private SatuSehatObatKronis obatKronis=new SatuSehatObatKronis();
    private SatuSehatPreAnestesi preAnestesi=new SatuSehatPreAnestesi();
    private SatuSehatEpisodeOfCare episodeOfCare=new SatuSehatEpisodeOfCare();
    private SatuSehatResumeMedisRajal resumeRajal=new SatuSehatResumeMedisRajal();
    private SatuSehatRencanaKontrol rencanaKontrol=new SatuSehatRencanaKontrol();
    private SatuSehatKirimBerkasDicom berkasDicom=new SatuSehatKirimBerkasDicom();
    private SatuSehatKirimDicomRadiologi dicomRadiologi=new SatuSehatKirimDicomRadiologi();
    private StringBuilder htmlContent;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    // ====== Fitur PREVIEW KLAIM (panel split kanan) ======
    // Komponen panelnya (splitUtama, panelPreview, panelKartu, scrollKartu, taLog,
    // barProgres, lblProgresPasien, dll.) sekarang dideklarasikan di berkas .form
    // sehingga bisa dilihat & diedit lewat NetBeans Form Designer.
    private final java.util.Map<String, JButton> tombolResource = new java.util.LinkedHashMap<>();
    private int barisPreview = -1;      // baris pasien yang sedang dipreview
    private String filterJenisRawat = "SEMUA";   // SEMUA | RANAP | RALAN (filter panel preview)

    /**
     * Kanal progres untuk pemanggil dari luar form (mis. tombol Kirim/Update Satu Sehat Klaim
     * di INACBGData) supaya bisa menampilkan langkah demi langkah tanpa membuka form ini.
     * Dipanggil dari thread yang menjalankan pengiriman — penerima wajib memindahkan sendiri
     * pembaruan UI-nya ke EDT.
     */
    public interface ProgresListener {
        void onProgres(String level, String pesan);
    }

    /** Jumlah sub-pengirim yang dijalankan setelah bundle Encounter (dipakai untuk "x/y"). */
    private static final int TOTAL_LANGKAH = 29;
    private volatile ProgresListener progresListener;
    private int langkahKe = 0;
    /** No.Rawat yang sedang diproses; dipakai mengisi konteks pencatat HTTP. */
    private String noRawatBerjalan = "";
    /** Pesan balasan eKlaim pada langkah terakhir (satusehat_encounter_set) kunjungan terakhir. */
    private String pesanEklaimTerakhir = "";

    public void setProgresListener(ProgresListener listener) {
        this.progresListener = listener;
    }

    /** Creates new form DlgKamar
     * @param parent
     * @param modal */
    public SatuSehatBundle(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","Tanggal Registrasi","No.Rawat","No.RM","Nama Pasien","No.KTP Pasien","Kode Dokter","Nama Dokter",
                "No.KTP Dokter","Kode Poli","Nama Poli/Unit","ID Lokasi Unit","Stts Rawat","Stts Lanjut",
                "Tanggal Pulang","ID Encounter","No SEP","Diagnosa"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                 java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                 java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                 java.lang.String.class, java.lang.String.class, java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 18; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(150);
            }else if(i==2){
                column.setPreferredWidth(105);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(110);
            }else if(i==6){
                column.setPreferredWidth(80);
            }else if(i==7){
                column.setPreferredWidth(150);
            }else if(i==8){
                column.setPreferredWidth(110);
            }else if(i==9){
                column.setPreferredWidth(80);
            }else if(i==10){
                column.setPreferredWidth(140);
            }else if(i==11){
                column.setPreferredWidth(210);
            }else if(i==12){
                column.setPreferredWidth(63);
            }else if(i==13){
                column.setPreferredWidth(63);
            }else if(i==14){
                column.setPreferredWidth(150);
            }else if(i==15){
                column.setPreferredWidth(215);
            }else if(i==16){
                column.setPreferredWidth(160);
            }else if(i==17){
                column.setPreferredWidth(260);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() -> tampil());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() -> tampil());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() -> tampil());
                    }
                }
            });
        } 
        
        try {
            link=koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
        
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

        siapkanPreview();
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
        ppPilihSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        LoadHTML = new widget.editorpane();
        splitUtama = new javax.swing.JSplitPane();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnAll = new widget.Button();
        BtnKirim = new widget.Button();
        BtnUpdate = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel15 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel17 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        ChkBelumTerkirim = new widget.CekBox();
        panelPreview = new javax.swing.JPanel();
        panelHeaderPreview = new javax.swing.JPanel();
        lblJudulPreview = new javax.swing.JLabel();
        lblJenisRawat = new javax.swing.JLabel();
        cmbJenisRawat = new javax.swing.JComboBox<>();
        BtnTte = new javax.swing.JButton();
        panelIsiPreview = new javax.swing.JPanel();
        scrollRail = new javax.swing.JScrollPane();
        railWrap = new javax.swing.JPanel();
        panelRail = new javax.swing.JPanel();
        BtnSemuaResource = new javax.swing.JButton();
        BtnResEncounter = new javax.swing.JButton();
        BtnResCondition = new javax.swing.JButton();
        BtnResSpri = new javax.swing.JButton();
        BtnResEpisodeOfCare = new javax.swing.JButton();
        BtnResTriaseIgd = new javax.swing.JButton();
        BtnResResumeRanap = new javax.swing.JButton();
        BtnResResumeRajal = new javax.swing.JButton();
        BtnResPersalinan = new javax.swing.JButton();
        BtnResFamilyHistory = new javax.swing.JButton();
        BtnResOperasi = new javax.swing.JButton();
        BtnResAnestesi = new javax.swing.JButton();
        BtnResPreAnestesi = new javax.swing.JButton();
        BtnResSkriningTbc = new javax.swing.JButton();
        BtnResObat = new javax.swing.JButton();
        BtnResLab = new javax.swing.JButton();
        BtnResRadiologi = new javax.swing.JButton();
        BtnResEcho = new javax.swing.JButton();
        BtnResEswl = new javax.swing.JButton();
        BtnResUsg = new javax.swing.JButton();
        BtnResEkg = new javax.swing.JButton();
        BtnResDiet = new javax.swing.JButton();
        BtnResPrognosa = new javax.swing.JButton();
        BtnResTelaahFarmasi = new javax.swing.JButton();
        BtnResGoal = new javax.swing.JButton();
        BtnResRiskAssessment = new javax.swing.JButton();
        BtnResAlkes = new javax.swing.JButton();
        BtnResBilling = new javax.swing.JButton();
        BtnResObatKronis = new javax.swing.JButton();
        BtnResRencanaKontrol = new javax.swing.JButton();
        splitTengah = new javax.swing.JSplitPane();
        scrollKartu = new javax.swing.JScrollPane();
        panelKartu = new javax.swing.JPanel();
        panelLog = new javax.swing.JPanel();
        panelProgres = new javax.swing.JPanel();
        lblProgresPasien = new javax.swing.JLabel();
        barProgres = new javax.swing.JProgressBar();
        scrollLog = new javax.swing.JScrollPane();
        taLog = new javax.swing.JTextArea();
        panelAksiPreview = new javax.swing.JPanel();
        BtnRefreshPreview = new javax.swing.JButton();
        BtnUpdateBundle = new javax.swing.JButton();
        BtnKirimBundle = new javax.swing.JButton();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(150, 26));
        ppPilihSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPilihSemuaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Hilangkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(150, 26));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBersihkan);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        splitUtama.setDividerSize(6);
        splitUtama.setResizeWeight(1.0);
        splitUtama.setName("splitUtama"); // NOI18N

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Satu Sehat Kirim Bundle ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        internalFrame1.setMinimumSize(new java.awt.Dimension(840, 100));
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setPreferredSize(new java.awt.Dimension(840, 100));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass8.add(LCount);

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

        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); // NOI18N
        BtnKirim.setMnemonic('K');
        BtnKirim.setText("Kirim");
        BtnKirim.setToolTipText("Alt+K");
        BtnKirim.setName("BtnKirim"); // NOI18N
        BtnKirim.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKirimActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKirim);

        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/edit_f2.png"))); // NOI18N
        BtnUpdate.setMnemonic('U');
        BtnUpdate.setText("Update");
        BtnUpdate.setToolTipText("Alt+U");
        BtnUpdate.setName("BtnUpdate"); // NOI18N
        BtnUpdate.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnUpdate);

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
        panelGlass8.add(BtnPrint);

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

        jLabel15.setText("Tgl.Registrasi :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel15);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-06-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("s.d.");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(24, 23));
        panelGlass9.add(jLabel17);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-06-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel16.setText("Key Word :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(210, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
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

        ChkBelumTerkirim.setBorder(null);
        ChkBelumTerkirim.setText("Data belum terkirim");
        ChkBelumTerkirim.setBorderPainted(true);
        ChkBelumTerkirim.setBorderPaintedFlat(true);
        ChkBelumTerkirim.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkBelumTerkirim.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkBelumTerkirim.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkBelumTerkirim.setIconTextGap(2);
        ChkBelumTerkirim.setName("ChkBelumTerkirim"); // NOI18N
        ChkBelumTerkirim.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChkBelumTerkirimItemStateChanged(evt);
            }
        });
        ChkBelumTerkirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkBelumTerkirimActionPerformed(evt);
            }
        });
        panelGlass9.add(ChkBelumTerkirim);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        splitUtama.setLeftComponent(internalFrame1);

        panelPreview.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelPreview.setMinimumSize(new java.awt.Dimension(360, 100));
        panelPreview.setName("panelPreview"); // NOI18N
        panelPreview.setPreferredSize(new java.awt.Dimension(450, 100));
        panelPreview.setLayout(new java.awt.BorderLayout(4, 4));

        panelHeaderPreview.setName("panelHeaderPreview"); // NOI18N
        panelHeaderPreview.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 2));

        lblJudulPreview.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        lblJudulPreview.setForeground(new java.awt.Color(46, 125, 50));
        lblJudulPreview.setText("PREVIEW KLAIM ELEKTRONIK");
        lblJudulPreview.setName("lblJudulPreview"); // NOI18N
        panelHeaderPreview.add(lblJudulPreview);

        lblJenisRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        lblJenisRawat.setText("   Jenis:");
        lblJenisRawat.setName("lblJenisRawat"); // NOI18N
        panelHeaderPreview.add(lblJenisRawat);

        cmbJenisRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        cmbJenisRawat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Rawat Inap", "Rawat Jalan" }));
        cmbJenisRawat.setName("cmbJenisRawat"); // NOI18N
        cmbJenisRawat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbJenisRawatActionPerformed(evt);
            }
        });
        panelHeaderPreview.add(cmbJenisRawat);

        BtnTte.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnTte.setForeground(new java.awt.Color(21, 101, 192));
        BtnTte.setText("TTE Satu Sehat");
        BtnTte.setToolTipText("Buka penandatanganan elektronik (TTE) untuk pasien terpilih");
        BtnTte.setFocusable(false);
        BtnTte.setName("BtnTte"); // NOI18N
        BtnTte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTteActionPerformed(evt);
            }
        });
        panelHeaderPreview.add(BtnTte);

        panelPreview.add(panelHeaderPreview, java.awt.BorderLayout.PAGE_START);

        panelIsiPreview.setName("panelIsiPreview"); // NOI18N
        panelIsiPreview.setLayout(new java.awt.BorderLayout(4, 4));

        scrollRail.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRail.setBorder(javax.swing.BorderFactory.createTitledBorder("Resource"));
        scrollRail.setName("scrollRail"); // NOI18N
        scrollRail.setPreferredSize(new java.awt.Dimension(158, 100));

        railWrap.setName("railWrap"); // NOI18N
        railWrap.setOpaque(false);
        railWrap.setLayout(new java.awt.BorderLayout());

        panelRail.setName("panelRail"); // NOI18N
        panelRail.setOpaque(false);
        panelRail.setLayout(new java.awt.GridLayout(0, 1, 0, 3));

        BtnSemuaResource.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSemuaResource.setForeground(new java.awt.Color(47, 158, 99));
        BtnSemuaResource.setText("★ Tampilkan Semua");
        BtnSemuaResource.setToolTipText("Bangun & tampilkan SEMUA resource pasien terpilih (seperti Dokumen Klaim)");
        BtnSemuaResource.setFocusable(false);
        BtnSemuaResource.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSemuaResource.setName("BtnSemuaResource"); // NOI18N
        BtnSemuaResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSemuaResourceActionPerformed(evt);
            }
        });
        panelRail.add(BtnSemuaResource);

        BtnResEncounter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResEncounter.setText("Encounter");
        BtnResEncounter.setToolTipText("Preview Encounter (build lokal, tanpa kirim)");
        BtnResEncounter.setFocusable(false);
        BtnResEncounter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResEncounter.setName("BtnResEncounter"); // NOI18N
        panelRail.add(BtnResEncounter);

        BtnResCondition.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResCondition.setText("Condition (Diagnosa)");
        BtnResCondition.setToolTipText("Preview Condition (Diagnosa) (build lokal, tanpa kirim)");
        BtnResCondition.setFocusable(false);
        BtnResCondition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResCondition.setName("BtnResCondition"); // NOI18N
        panelRail.add(BtnResCondition);

        BtnResSpri.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResSpri.setText("ServiceRequest SPRI");
        BtnResSpri.setToolTipText("Preview ServiceRequest SPRI (build lokal, tanpa kirim)");
        BtnResSpri.setFocusable(false);
        BtnResSpri.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResSpri.setName("BtnResSpri"); // NOI18N
        panelRail.add(BtnResSpri);

        BtnResEpisodeOfCare.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResEpisodeOfCare.setText("EpisodeOfCare");
        BtnResEpisodeOfCare.setToolTipText("Preview EpisodeOfCare (build lokal, tanpa kirim)");
        BtnResEpisodeOfCare.setFocusable(false);
        BtnResEpisodeOfCare.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResEpisodeOfCare.setName("BtnResEpisodeOfCare"); // NOI18N
        panelRail.add(BtnResEpisodeOfCare);

        BtnResTriaseIgd.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResTriaseIgd.setText("Triase IGD");
        BtnResTriaseIgd.setToolTipText("Preview Triase IGD (build lokal, tanpa kirim)");
        BtnResTriaseIgd.setFocusable(false);
        BtnResTriaseIgd.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResTriaseIgd.setName("BtnResTriaseIgd"); // NOI18N
        panelRail.add(BtnResTriaseIgd);

        BtnResResumeRanap.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResResumeRanap.setText("Resume Medis Ranap");
        BtnResResumeRanap.setToolTipText("Preview Resume Medis Ranap (build lokal, tanpa kirim)");
        BtnResResumeRanap.setFocusable(false);
        BtnResResumeRanap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResResumeRanap.setName("BtnResResumeRanap"); // NOI18N
        panelRail.add(BtnResResumeRanap);

        BtnResResumeRajal.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResResumeRajal.setText("Resume Medis Rajal");
        BtnResResumeRajal.setToolTipText("Preview Resume Medis Rajal (build lokal, tanpa kirim)");
        BtnResResumeRajal.setFocusable(false);
        BtnResResumeRajal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResResumeRajal.setName("BtnResResumeRajal"); // NOI18N
        panelRail.add(BtnResResumeRajal);

        BtnResPersalinan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResPersalinan.setText("Laporan Persalinan");
        BtnResPersalinan.setToolTipText("Preview Laporan Persalinan (build lokal, tanpa kirim)");
        BtnResPersalinan.setFocusable(false);
        BtnResPersalinan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResPersalinan.setName("BtnResPersalinan"); // NOI18N
        panelRail.add(BtnResPersalinan);

        BtnResFamilyHistory.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResFamilyHistory.setText("Family History");
        BtnResFamilyHistory.setToolTipText("Preview Family History (build lokal, tanpa kirim)");
        BtnResFamilyHistory.setFocusable(false);
        BtnResFamilyHistory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResFamilyHistory.setName("BtnResFamilyHistory"); // NOI18N
        panelRail.add(BtnResFamilyHistory);

        BtnResOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResOperasi.setText("Laporan Operasi");
        BtnResOperasi.setToolTipText("Preview Laporan Operasi (build lokal, tanpa kirim)");
        BtnResOperasi.setFocusable(false);
        BtnResOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResOperasi.setName("BtnResOperasi"); // NOI18N
        panelRail.add(BtnResOperasi);

        BtnResAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResAnestesi.setText("Laporan Anestesi");
        BtnResAnestesi.setToolTipText("Preview Laporan Anestesi (build lokal, tanpa kirim)");
        BtnResAnestesi.setFocusable(false);
        BtnResAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResAnestesi.setName("BtnResAnestesi"); // NOI18N
        panelRail.add(BtnResAnestesi);

        BtnResPreAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResPreAnestesi.setText("Pre-Anestesi");
        BtnResPreAnestesi.setToolTipText("Preview Pre-Anestesi (build lokal, tanpa kirim)");
        BtnResPreAnestesi.setFocusable(false);
        BtnResPreAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResPreAnestesi.setName("BtnResPreAnestesi"); // NOI18N
        panelRail.add(BtnResPreAnestesi);

        BtnResSkriningTbc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResSkriningTbc.setText("Skrining TBC");
        BtnResSkriningTbc.setToolTipText("Preview Skrining TBC (build lokal, tanpa kirim)");
        BtnResSkriningTbc.setFocusable(false);
        BtnResSkriningTbc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResSkriningTbc.setName("BtnResSkriningTbc"); // NOI18N
        panelRail.add(BtnResSkriningTbc);

        BtnResObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResObat.setText("Obat");
        BtnResObat.setToolTipText("Preview Obat (build lokal, tanpa kirim)");
        BtnResObat.setFocusable(false);
        BtnResObat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResObat.setName("BtnResObat"); // NOI18N
        panelRail.add(BtnResObat);

        BtnResLab.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResLab.setText("Lab");
        BtnResLab.setToolTipText("Preview Lab (build lokal, tanpa kirim)");
        BtnResLab.setFocusable(false);
        BtnResLab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResLab.setName("BtnResLab"); // NOI18N
        panelRail.add(BtnResLab);

        BtnResRadiologi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResRadiologi.setText("Radiologi");
        BtnResRadiologi.setToolTipText("Preview Radiologi (build lokal, tanpa kirim)");
        BtnResRadiologi.setFocusable(false);
        BtnResRadiologi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResRadiologi.setName("BtnResRadiologi"); // NOI18N
        panelRail.add(BtnResRadiologi);

        BtnResEcho.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResEcho.setText("Laporan Echo");
        BtnResEcho.setToolTipText("Preview Laporan Echo (build lokal, tanpa kirim)");
        BtnResEcho.setFocusable(false);
        BtnResEcho.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResEcho.setName("BtnResEcho"); // NOI18N
        panelRail.add(BtnResEcho);

        BtnResEswl.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResEswl.setText("Laporan ESWL");
        BtnResEswl.setToolTipText("Preview Laporan ESWL (build lokal, tanpa kirim)");
        BtnResEswl.setFocusable(false);
        BtnResEswl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResEswl.setName("BtnResEswl"); // NOI18N
        panelRail.add(BtnResEswl);

        BtnResUsg.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResUsg.setText("Laporan USG");
        BtnResUsg.setToolTipText("Preview Laporan USG (build lokal, tanpa kirim)");
        BtnResUsg.setFocusable(false);
        BtnResUsg.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResUsg.setName("BtnResUsg"); // NOI18N
        panelRail.add(BtnResUsg);

        BtnResEkg.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResEkg.setText("Laporan EKG");
        BtnResEkg.setToolTipText("Preview Laporan EKG (build lokal, tanpa kirim)");
        BtnResEkg.setFocusable(false);
        BtnResEkg.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResEkg.setName("BtnResEkg"); // NOI18N
        panelRail.add(BtnResEkg);

        BtnResDiet.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResDiet.setText("Diet");
        BtnResDiet.setToolTipText("Preview Diet (build lokal, tanpa kirim)");
        BtnResDiet.setFocusable(false);
        BtnResDiet.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResDiet.setName("BtnResDiet"); // NOI18N
        panelRail.add(BtnResDiet);

        BtnResPrognosa.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResPrognosa.setText("Prognosa");
        BtnResPrognosa.setToolTipText("Preview Prognosa (build lokal, tanpa kirim)");
        BtnResPrognosa.setFocusable(false);
        BtnResPrognosa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResPrognosa.setName("BtnResPrognosa"); // NOI18N
        panelRail.add(BtnResPrognosa);

        BtnResTelaahFarmasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResTelaahFarmasi.setText("Telaah Farmasi");
        BtnResTelaahFarmasi.setToolTipText("Preview Telaah Farmasi (build lokal, tanpa kirim)");
        BtnResTelaahFarmasi.setFocusable(false);
        BtnResTelaahFarmasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResTelaahFarmasi.setName("BtnResTelaahFarmasi"); // NOI18N
        panelRail.add(BtnResTelaahFarmasi);

        BtnResGoal.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResGoal.setText("Goal");
        BtnResGoal.setToolTipText("Preview Goal (build lokal, tanpa kirim)");
        BtnResGoal.setFocusable(false);
        BtnResGoal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResGoal.setName("BtnResGoal"); // NOI18N
        panelRail.add(BtnResGoal);

        BtnResRiskAssessment.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResRiskAssessment.setText("Risk Assessment");
        BtnResRiskAssessment.setToolTipText("Preview Risk Assessment (build lokal, tanpa kirim)");
        BtnResRiskAssessment.setFocusable(false);
        BtnResRiskAssessment.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResRiskAssessment.setName("BtnResRiskAssessment"); // NOI18N
        panelRail.add(BtnResRiskAssessment);

        BtnResAlkes.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResAlkes.setText("Alkes");
        BtnResAlkes.setToolTipText("Preview Alkes (build lokal, tanpa kirim)");
        BtnResAlkes.setFocusable(false);
        BtnResAlkes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResAlkes.setName("BtnResAlkes"); // NOI18N
        panelRail.add(BtnResAlkes);

        BtnResBilling.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResBilling.setText("Billing");
        BtnResBilling.setToolTipText("Preview Billing (build lokal, tanpa kirim)");
        BtnResBilling.setFocusable(false);
        BtnResBilling.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResBilling.setName("BtnResBilling"); // NOI18N
        panelRail.add(BtnResBilling);

        BtnResObatKronis.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResObatKronis.setText("Obat Kronis");
        BtnResObatKronis.setToolTipText("Preview Obat Kronis (build lokal, tanpa kirim)");
        BtnResObatKronis.setFocusable(false);
        BtnResObatKronis.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResObatKronis.setName("BtnResObatKronis"); // NOI18N
        panelRail.add(BtnResObatKronis);

        BtnResRencanaKontrol.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnResRencanaKontrol.setText("Rencana Kontrol");
        BtnResRencanaKontrol.setToolTipText("Preview Rencana Kontrol (build lokal, tanpa kirim)");
        BtnResRencanaKontrol.setFocusable(false);
        BtnResRencanaKontrol.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnResRencanaKontrol.setName("BtnResRencanaKontrol"); // NOI18N
        panelRail.add(BtnResRencanaKontrol);

        railWrap.add(panelRail, java.awt.BorderLayout.PAGE_START);

        scrollRail.setViewportView(railWrap);

        panelIsiPreview.add(scrollRail, java.awt.BorderLayout.LINE_START);

        splitTengah.setDividerLocation(430);
        splitTengah.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitTengah.setResizeWeight(0.72);
        splitTengah.setName("splitTengah"); // NOI18N

        scrollKartu.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview Klaim"));
        scrollKartu.setName("scrollKartu"); // NOI18N

        panelKartu.setBackground(new java.awt.Color(255, 255, 255));
        panelKartu.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panelKartu.setName("panelKartu"); // NOI18N
        panelKartu.setLayout(new javax.swing.BoxLayout(panelKartu, javax.swing.BoxLayout.Y_AXIS));
        scrollKartu.setViewportView(panelKartu);

        splitTengah.setTopComponent(scrollKartu);

        panelLog.setName("panelLog"); // NOI18N
        panelLog.setLayout(new java.awt.BorderLayout(4, 4));

        panelProgres.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panelProgres.setName("panelProgres"); // NOI18N
        panelProgres.setLayout(new java.awt.BorderLayout(3, 3));

        lblProgresPasien.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        lblProgresPasien.setForeground(new java.awt.Color(55, 71, 79));
        lblProgresPasien.setText("Siap. Pilih pasien lalu tekan Kirim/Update Bundle.");
        lblProgresPasien.setName("lblProgresPasien"); // NOI18N
        panelProgres.add(lblProgresPasien, java.awt.BorderLayout.PAGE_START);

        barProgres.setMaximum(29);
        barProgres.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        barProgres.setName("barProgres"); // NOI18N
        barProgres.setPreferredSize(new java.awt.Dimension(100, 18));
        barProgres.setString("Belum ada pengiriman");
        barProgres.setStringPainted(true);
        panelProgres.add(barProgres, java.awt.BorderLayout.CENTER);

        panelLog.add(panelProgres, java.awt.BorderLayout.PAGE_START);

        scrollLog.setBorder(javax.swing.BorderFactory.createTitledBorder("Log Pengiriman"));
        scrollLog.setName("scrollLog"); // NOI18N
        scrollLog.setPreferredSize(new java.awt.Dimension(100, 150));

        taLog.setEditable(false);
        taLog.setColumns(20);
        taLog.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        taLog.setRows(5);
        taLog.setName("taLog"); // NOI18N
        scrollLog.setViewportView(taLog);

        panelLog.add(scrollLog, java.awt.BorderLayout.CENTER);

        splitTengah.setBottomComponent(panelLog);

        panelIsiPreview.add(splitTengah, java.awt.BorderLayout.CENTER);

        panelPreview.add(panelIsiPreview, java.awt.BorderLayout.CENTER);

        panelAksiPreview.setName("panelAksiPreview"); // NOI18N
        panelAksiPreview.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 4));

        BtnRefreshPreview.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnRefreshPreview.setText("Refresh Preview");
        BtnRefreshPreview.setName("BtnRefreshPreview"); // NOI18N
        BtnRefreshPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRefreshPreviewActionPerformed(evt);
            }
        });
        panelAksiPreview.add(BtnRefreshPreview);

        BtnUpdateBundle.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnUpdateBundle.setText("Update Bundle");
        BtnUpdateBundle.setName("BtnUpdateBundle"); // NOI18N
        BtnUpdateBundle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateBundleActionPerformed(evt);
            }
        });
        panelAksiPreview.add(BtnUpdateBundle);

        BtnKirimBundle.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnKirimBundle.setText("Kirim Bundle");
        BtnKirimBundle.setName("BtnKirimBundle"); // NOI18N
        BtnKirimBundle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKirimBundleActionPerformed(evt);
            }
        });
        panelAksiPreview.add(BtnKirimBundle);

        panelPreview.add(panelAksiPreview, java.awt.BorderLayout.PAGE_END);

        splitUtama.setRightComponent(panelPreview);

        getContentPane().add(splitUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnPrint,BtnKeluar);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            htmlContent = new StringBuilder();
            htmlContent.append(                             
                "<tr class='isi'>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal Registrasi</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.KTP Pasien</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kode Dokter</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Dokter</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.KTP Dokter</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kode Poli</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Poli/Unit</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Lokasi Unit</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Stts Rawat</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Stts Lanjut</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal Pulang</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Encounter</b></td>"+
                "</tr>"
            );
            for (i = 0; i < tabMode.getRowCount(); i++) {
                htmlContent.append(
                    "<tr class='isi'>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,12).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,15).toString()+"</td>"+
                    "</tr>");
            }
            LoadHTML.setText(
                "<html>"+
                  "<table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                   htmlContent.toString()+
                  "</table>"+
                "</html>"
            );
            htmlContent=null;

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

            File f = new File("DataSatuSehatEncounter.html");            
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
            bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                        "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                        "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                            "<tr class='isi2'>"+
                                "<td valign='top' align='center'>"+
                                    "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                    akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                    akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                    "<font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT ENCOUNTER<br><br></font>"+        
                                "</td>"+
                           "</tr>"+
                        "</table>")
            );
            bw.close();                         
            Desktop.getDesktop().browse(f.toURI());
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        this.setCursor(Cursor.getDefaultCursor());       
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbObat.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt,TCari,BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKirimActionPerformed
        // Dijalankan di thread latar: satu pasien saja bisa memakan puluhan panggilan HTTP, kalau
        // dikerjakan di EDT panel log & bar progres tidak akan pernah sempat melukis.
        if (sedangSibuk("KIRIM")) return;
        runBackground(() -> prosesBatch(false));
    }//GEN-LAST:event_BtnKirimActionPerformed

    /**
     * Kirim/update bundle untuk semua baris yang dicentang, sambil memperbarui bar progres.
     *
     * @param update false = KIRIM (baris yang belum punya id Encounter, POST),
     *               true  = UPDATE (baris yang sudah punya id Encounter, PUT)
     */
    private void prosesBatch(boolean update) {
        String mode = update ? "UPDATE" : "KIRIM";
        java.util.List<Integer> antrian = new ArrayList<>();
        for(int b=0;b<tbObat.getRowCount();b++){
            boolean sudahPunyaId = !tbObat.getValueAt(b,15).toString().equals("");
            if(tbObat.getValueAt(b,0).toString().equals("true")
                    && !tbObat.getValueAt(b,5).toString().equals("")
                    && !tbObat.getValueAt(b,8).toString().equals("")
                    && sudahPunyaId==update){
                antrian.add(b);
            }
        }
        if(antrian.isEmpty()){
            logWarn("=== " + mode + " dibatalkan : tidak ada baris tercentang yang memenuhi syarat "
                    + (update ? "(NIK pasien & dokter terisi, dan sudah punya id Encounter)"
                              : "(NIK pasien & dokter terisi, dan belum punya id Encounter)") + " ===");
            selesaiProgres("Tidak ada baris yang diproses.");
            return;
        }

        int berhasil=0, gagal=0, ke=0;
        logInfo("=== Mulai " + mode + " bundle ke SATUSEHAT : " + antrian.size() + " pasien ===");
        for(int row : antrian){
            ke++;
            String noRawat = tbObat.getValueAt(row,2).toString();
            tampilProgresPasien("Pasien " + ke + "/" + antrian.size()
                    + "   |   No.Rawat " + noRawat
                    + "   |   " + tbObat.getValueAt(row,4).toString());
            try {
                if(kirimBundle(row, update ? tbObat.getValueAt(row,15).toString() : "")) berhasil++; else gagal++;
            } catch (Exception e) {
                gagal++;
                logError("[" + mode + "] No.Rawat " + noRawat + " gagal " + (update?"di-update":"dikirim") + " : " + e);
                simpanLog("Bundle (" + mode + ")", noRawat, "", "", null, "", "FAILED", String.valueOf(e), null);
            }
        }
        logInfo("=== Selesai " + mode + " : " + antrian.size() + " diproses, " + berhasil
                + " berhasil, " + gagal + " gagal/dilewati ===");
        selesaiProgres(mode + " selesai : " + antrian.size() + " pasien diproses, "
                + berhasil + " berhasil, " + gagal + " gagal/dilewati.");
        SatuSehatHttpLogger.Konteks.bersihkan();
    }

    /** Hasil pengiriman satu no_rawat lewat {@link #kirimSatuNoRawat}. */
    public static class HasilKirim {
        public boolean berhasil = false;
        public String idEncounter = "";
        public String pesan = "";
        /** Balasan eKlaim atas langkah terakhir satusehat_encounter_set (kosong bila tak dijalankan). */
        public String pesanEklaim = "";
    }

    /**
     * Kirim/update bundle SATUSEHAT untuk SATU no_rawat, bisa dipanggil dari form lain
     * (dipakai tombol "Kirim/Update Satu Sehat Klaim" di INACBGData) tanpa form ini perlu tampil.
     *
     * Memakai jalur yang sama persis dengan tombol Kirim/Update di form ini, jadi tidak ada
     * logika pengiriman yang diduplikasi :
     *   update=false -> kirimBundle(row,"")        : entry Encounter dkk memakai POST (buat baru)
     *   update=true  -> kirimBundle(row,idLama)    : entry memakai PUT ke id lama (anti-duplikat)
     *
     * Penjagaan anti-duplikat: menolak POST bila Encounter sudah ada, dan menolak PUT bila
     * Encounter belum pernah dibuat.
     *
     * Catatan: pemanggilan ini menjalankan seluruh pipeline klaim (Encounter, Condition,
     * lalu semua sub-pengirim: billing, lab, radiologi, resume, dst) sehingga bisa memakan
     * waktu. Panggil dari EDT dengan kursor WAIT, seperti tombol Kirim/Update di form ini.
     *
     * @param noRawat no_rawat yang akan dikirim
     * @param update  false = kirim baru (POST), true = update (PUT)
     */
    public HasilKirim kirimSatuNoRawat(String noRawat, boolean update) {
        HasilKirim hasil = new HasilKirim();
        langkahKe = 0;   // penomoran "x/27" dihitung ulang tiap pasien
        if (noRawat == null || noRawat.trim().equals("")) {
            hasil.pesan = "No.Rawat kosong.";
            return hasil;
        }
        noRawat = noRawat.trim();

        String idLama = nzs(Sequel.cariIsi(
                "select id_encounter from satu_sehat_encounter where no_rawat='" + noRawat + "'"));
        if (update && idLama.equals("")) {
            hasil.pesan = "Encounter SATUSEHAT belum pernah dibuat untuk No.Rawat " + noRawat
                    + ". Pakai tombol Kirim lebih dulu, bukan Update.";
            return hasil;
        }
        if (!update && !idLama.equals("")) {
            // Encounter sudah ada -> lanjutkan sebagai UPDATE (PUT id yang sama), bukan dilewati.
            //
            // Dulu di sini pengiriman diskip demi mencegah duplikasi. Itu tidak lagi memadai sejak
            // Encounter dibuat lebih awal saat simpan pendaftaran (SatuSehatEncounterReg): kalau
            // diskip, Condition/lab/obat/billing ikut tidak terkirim DAN Encounter mandek di status
            // "arrived" selamanya, karena POST ber-ifNoneExist hanya mengembalikan resource lama
            // tanpa menerapkan payload baru.
            //
            // Anti-duplikasi tetap terjaga: mode update memakai PUT ke id yang sudah ada.
            update = true;
            logProgres("Encounter " + idLama + " sudah ada, pengiriman dijalankan sebagai update.");
        }

        // Muat baris pasien ini ke tabel dengan memakai query tampil() yang sudah ada.
        // State filter form disimpan & dikembalikan supaya form tidak rusak bila sedang dibuka.
        String cariLama = TCari.getText();
        String jenisLama = filterJenisRawat;
        boolean belumTerkirimLama = ChkBelumTerkirim.isSelected();
        java.util.Date tgl1Lama = DTPCari1.getDate();
        java.util.Date tgl2Lama = DTPCari2.getDate();
        try {
            String statusLanjut = nzs(Sequel.cariIsi(
                    "select status_lanjut from reg_periksa where no_rawat='" + noRawat + "'"));
            if (statusLanjut.equals("")) {
                hasil.pesan = "No.Rawat " + noRawat + " tidak ditemukan di reg_periksa.";
                return hasil;
            }
            // Rentang tanggal dipatok ke tanggal nota pasien ini saja: query tampil() menyaring
            // berdasarkan tanggal nota (nota_jalan/nota_inap), bukan tanggal registrasi.
            String tglNota = nzs(Sequel.cariIsi(statusLanjut.equalsIgnoreCase("Ranap")
                    ? "select tanggal from nota_inap where no_rawat='" + noRawat + "' limit 1"
                    : "select tanggal from nota_jalan where no_rawat='" + noRawat + "' limit 1"));
            if (tglNota.equals("")) {
                hasil.pesan = "Nota untuk No.Rawat " + noRawat + " belum ada, sehingga kunjungan ini "
                        + "belum muncul di daftar pengiriman SATUSEHAT.";
                return hasil;
            }

            filterJenisRawat = statusLanjut.equalsIgnoreCase("Ranap") ? "RANAP" : "RALAN";
            ChkBelumTerkirim.setSelected(false);   // Update perlu baris yang sudah punya id
            TCari.setText(noRawat);
            DTPCari1.setDate(Valid.SetTgl2(tglNota));
            DTPCari2.setDate(Valid.SetTgl2(tglNota));
            tampil();

            int row = -1;
            for (int b = 0; b < tabMode.getRowCount(); b++) {
                if (nzs(tabMode.getValueAt(b, 2)).trim().equals(noRawat)) {
                    row = b;
                    break;
                }
            }
            if (row < 0) {
                hasil.pesan = "No.Rawat " + noRawat + " tidak muncul di daftar pengiriman "
                        + "(cek mapping lokasi poli / data dokter / nota).";
                return hasil;
            }
            if (nzs(tabMode.getValueAt(row, 5)).trim().equals("")) {
                hasil.pesan = "NIK pasien kosong, ID pasien SATUSEHAT tidak bisa dicari.";
                return hasil;
            }
            if (nzs(tabMode.getValueAt(row, 8)).trim().equals("")) {
                hasil.pesan = "NIK dokter kosong, Practitioner SATUSEHAT tidak bisa dicari.";
                return hasil;
            }

            pesanEklaimTerakhir = "";
            hasil.berhasil = kirimBundle(row, update ? idLama : "");
            hasil.pesanEklaim = pesanEklaimTerakhir;
            hasil.idEncounter = nzs(Sequel.cariIsi(
                    "select id_encounter from satu_sehat_encounter where no_rawat='" + noRawat + "'"));
            if (!hasil.berhasil) {
                hasil.pesan = "Pengiriman bundle ditolak/gagal. Lihat panel log SATUSEHAT "
                        + "atau tabel satu_sehat_log untuk detailnya.";
            } else if (hasil.idEncounter.equals("")) {
                hasil.berhasil = false;
                hasil.pesan = "Bundle terkirim tetapi id Encounter tidak terbaca dari respons server.";
            }
        } catch (Exception e) {
            hasil.berhasil = false;
            hasil.pesan = "Error : " + e;
            logError("[" + (update ? "UPDATE" : "KIRIM") + "] No.Rawat " + noRawat + " : " + e);
        } finally {
            TCari.setText(cariLama);
            filterJenisRawat = jenisLama;
            ChkBelumTerkirim.setSelected(belumTerkirimLama);
            if (tgl1Lama != null) DTPCari1.setDate(tgl1Lama);
            if (tgl2Lama != null) DTPCari2.setDate(tgl2Lama);
            SatuSehatHttpLogger.Konteks.bersihkan();
        }
        return hasil;
    }

    private String nzs(Object o) {
        return o == null ? "" : o.toString();
    }

    // ====================== KIRIM BERKAS RME (REALTIME) ======================
    // Dipakai tombol "Satu Sehat" di form-form RME (rekammedis.RM*) supaya satu berkas bisa
    // dikirim begitu dokter selesai menulisnya, tanpa menunggu batch kirim bundle klaim.
    // Sengaja static: pemanggil TIDAK perlu membuat dialog SatuSehatBundle ini.

    /** Jenis berkas RME; nilainya dipakai apa adanya sebagai service_name di satu_sehat_log. */
    public static final String RME_RESUME_RANAP       = "Resume Medis Rawat Inap";
    public static final String RME_RESUME_RALAN       = "Resume Medis Rawat Jalan";
    public static final String RME_TRIASE_IGD         = "Resume Triase IGD";
    public static final String RME_LAPORAN_EKG        = "Laporan EKG";
    public static final String RME_LAPORAN_USG        = "Laporan USG";
    public static final String RME_LAPORAN_ESWL       = "Laporan Tindakan ESWL";
    public static final String RME_LAPORAN_ECHO       = "Laporan Tindakan Echo";
    public static final String RME_LAPORAN_ANESTESI   = "Laporan Anestesi";
    public static final String RME_LAPORAN_PERSALINAN = "Laporan Persalinan";

    /**
     * Kirim SATU berkas RME kunjungan terpilih ke SATUSEHAT saat itu juga, lalu laporkan hasilnya
     * lewat dialog. Aman dipanggil dari thread Swing: bagian jaringannya dikerjakan thread pekerja.
     *
     * Urutan penjagaan — semuanya berhenti dengan pesan yang bisa ditindaklanjuti, bukan diam:
     * <ol>
     *   <li>No.Rawat kosong.</li>
     *   <li>Data sumbernya belum tersimpan (mis. belum ada baris hasil_pemeriksaan_ekg). Tanpa cek
     *       ini sender akan {@code return} diam-diam dan tombolnya berbohong "terkirim".</li>
     *   <li>Encounter kunjungan belum ada — semua Composition/Observation mereferensinya.</li>
     * </ol>
     *
     * Anti-duplikat BUKAN urusan method ini melainkan tiap sender: id resource yang sudah pernah
     * dibuat dibaca dulu dari tabel lokal (atau dicari ke server lewat identifier), ketemu -&gt; PUT,
     * tidak ketemu -&gt; POST. Karena itu tombolnya aman ditekan berulang kali.
     *
     * @param pemilik komponen pemanggil; dipakai sebagai induk dialog & pemilik kursor tunggu
     * @param tombol  tombol yang ditekan; dinonaktifkan selama pengiriman agar tak terkirim ganda
     * @param jenis   salah satu konstanta {@code RME_*} di kelas ini
     * @param noRawat no_rawat kunjungan yang sedang dibuka di form
     */
    public static void kirimBerkasRME(final java.awt.Component pemilik, final java.awt.Component tombol,
                                      final String jenis, final String noRawat) {
        final String norwt = (noRawat == null) ? "" : noRawat.trim();
        if (norwt.equals("")) {
            javax.swing.JOptionPane.showMessageDialog(pemilik, "No.Rawat belum terisi. "
                    + "Pilih dulu kunjungan pasiennya.");
            return;
        }
        final String tabelSumber = tabelSumberRME(jenis);
        if (tabelSumber.equals("")) {
            javax.swing.JOptionPane.showMessageDialog(pemilik, "Jenis berkas \"" + jenis
                    + "\" belum dikenali pengirim Satu Sehat.");
            return;
        }

        // Semua pembacaan basis data lokal dikerjakan di thread Swing, sebelum thread pekerja jalan.
        sekuel sql = new sekuel();
        // cariIsi menelan galat dan mengembalikan "" -> tabel yang tidak ada TIDAK boleh dibaca
        // sebagai "ada datanya". Bedakan bertingkat: -1 = tak terbaca, 0 = kosong, >0 = ada.
        int jumlahBaris;
        try {
            jumlahBaris = Integer.parseInt(nzt(sql.cariIsi(
                    "select count(*) from " + tabelSumber + " where no_rawat=?", norwt)).trim());
        } catch (Exception e) {
            jumlahBaris = -1;
        }
        if (jumlahBaris < 0) {
            javax.swing.JOptionPane.showMessageDialog(pemilik, "Tabel " + tabelSumber
                    + " tidak bisa dibaca, jadi " + jenis + " tidak dikirim.\n"
                    + "Cek apakah tabelnya ada di database ini.");
            return;
        }
        if (jumlahBaris == 0) {
            javax.swing.JOptionPane.showMessageDialog(pemilik, jenis + " untuk No.Rawat " + norwt
                    + " belum tersimpan.\nSimpan datanya lebih dulu, baru kirim ke Satu Sehat.");
            return;
        }
        final String idEncounter = nzt(sql.cariIsi(
                "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=?", norwt)).trim();
        if (idEncounter.equals("")) {
            javax.swing.JOptionPane.showMessageDialog(pemilik, "Encounter Satu Sehat untuk No.Rawat "
                    + norwt + " belum ada.\nKirim Encounter-nya lebih dulu lewat pendaftaran"
                    + " atau menu Satu Sehat Klaim Bundle.");
            return;
        }

        tombol.setEnabled(false);
        pemilik.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Pengiriman = ambil token + panggilan HTTP; di thread Swing seluruh tampilan SIMRS membeku.
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pesan;
                try {
                    // Supaya baris di satu_sehat_log jelas berasal dari form RME, bukan batch bundle.
                    SatuSehatHttpLogger.Konteks.set(jenis + " (RME)", norwt);
                    kirimSenderRME(jenis, norwt, idEncounter);
                    pesan = jenis + " terkirim ke Satu Sehat.\n\n"
                            + "No.Rawat  : " + norwt + "\n"
                            + "Encounter : " + idEncounter;
                } catch (Exception ex) {
                    pesan = "Gagal mengirim " + jenis + " :\n" + ex
                            + "\n\nDetail penolakan server ada di konsol dan tabel satu_sehat_log.";
                    System.out.println("Notifikasi kirim " + jenis + " (RME) : " + ex);
                } finally {
                    SatuSehatHttpLogger.Konteks.bersihkan();
                }
                final String hasil = pesan;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        pemilik.setCursor(Cursor.getDefaultCursor());
                        tombol.setEnabled(true);
                        javax.swing.JOptionPane.showMessageDialog(pemilik, hasil);
                    }
                });
            }
        }, "kirim-berkas-rme-satusehat").start();
    }

    /**
     * Tabel sumber tiap jenis berkas, dipakai untuk memastikan datanya memang sudah tersimpan
     * sebelum jaringan disentuh. "" bila jenisnya tidak dikenali.
     */
    private static String tabelSumberRME(String jenis) {
        String j = (jenis == null) ? "" : jenis;
        if (j.equals(RME_RESUME_RANAP))       return "resume_pasien_ranap";
        if (j.equals(RME_RESUME_RALAN))       return "resume_pasien";
        if (j.equals(RME_TRIASE_IGD))         return "data_triase_igd";
        if (j.equals(RME_LAPORAN_EKG))        return "hasil_pemeriksaan_ekg";
        if (j.equals(RME_LAPORAN_USG))        return "hasil_pemeriksaan_usg";
        if (j.equals(RME_LAPORAN_ESWL))       return "hasil_tindakan_eswl";
        if (j.equals(RME_LAPORAN_ECHO))       return "hasil_pemeriksaan_echo";
        if (j.equals(RME_LAPORAN_ANESTESI))   return "laporan_anestesi";
        if (j.equals(RME_LAPORAN_PERSALINAN)) return "catatan_persalinan";
        return "";
    }

    /** Panggil sender yang sesuai. Sender dibuat baru tiap kirim supaya tidak ada state antar-kiriman. */
    private static void kirimSenderRME(String jenis, String noRawat, String idEncounter) throws Exception {
        if (jenis.equals(RME_RESUME_RANAP))            { new SatuSehatResumeMedisRanap().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_RESUME_RALAN))            { new SatuSehatResumeMedisRajal().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_TRIASE_IGD))              { new SatuSehatTriaseIGD().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_LAPORAN_EKG))             { new SatuSehatLaporanEkg().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_LAPORAN_USG))             { new SatuSehatLaporanUsg().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_LAPORAN_ESWL))            { new SatuSehatLaporanTindakanEswl().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_LAPORAN_ECHO))            { new SatuSehatLaporanTindakanEcho().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_LAPORAN_ANESTESI))        { new SatuSehatLaporanAnestesi().kirim(noRawat, idEncounter); return; }
        if (jenis.equals(RME_LAPORAN_PERSALINAN))      { new SatuSehatLaporanPersalinan().kirim(noRawat, idEncounter); return; }
        throw new IllegalArgumentException("Jenis berkas RME tidak dikenali : " + jenis);
    }

    /** Balasan sekuel bisa null bila kolomnya NULL; jadikan string kosong supaya aman dibandingkan. */
    private static String nzt(String s) {
        return s == null ? "" : s;
    }

    // ---------------------- STATUS UNTUK PANEL FORM RME ----------------------

    /** Ditampilkan bila jenis berkasnya tidak menyimpan id Composition secara lokal. */
    public static final String STATUS_TAK_TERLACAK = "-";

    /**
     * Tabel penyimpan id Composition tiap jenis. Tabel Echo/Anestesi/Persalinan dibuat otomatis
     * oleh sender masing-masing saat pertama kali dipakai ({@code pastikanTabel}), jadi instalasi
     * lama tidak perlu menjalankan SQL apa pun — hanya saja kunjungan yang sudah terkirim SEBELUM
     * pelacakan ini ada akan terbaca "Belum Kirim" sampai dikirim ulang sekali.
     */
    private static String tabelIdRME(String jenis) {
        String j = (jenis == null) ? "" : jenis;
        if (j.equals(RME_RESUME_RANAP))       return "satu_sehat_resume_ranap";
        if (j.equals(RME_RESUME_RALAN))       return "satu_sehat_resume_ralan";
        if (j.equals(RME_TRIASE_IGD))         return "satu_sehat_triase_igd";
        if (j.equals(RME_LAPORAN_EKG))        return "satu_sehat_laporan_ekg";
        if (j.equals(RME_LAPORAN_USG))        return "satu_sehat_laporan_usg";
        if (j.equals(RME_LAPORAN_ESWL))       return "satu_sehat_laporan_eswl";
        if (j.equals(RME_LAPORAN_ECHO))       return "satu_sehat_laporan_echo";
        if (j.equals(RME_LAPORAN_ANESTESI))   return "satu_sehat_laporan_anestesi";
        if (j.equals(RME_LAPORAN_PERSALINAN)) return "satu_sehat_laporan_persalinan";
        return "";
    }

    /** "Sudah Kirim" / "Belum Kirim", atau {@link #STATUS_TAK_TERLACAK} bila tak dilacak lokal. */
    public static String statusBerkasRME(String jenis, String noRawat) {
        String norwt = nzt(noRawat).trim();
        String tabel = tabelIdRME(jenis);
        if (norwt.equals("") || tabel.equals("")) {
            return STATUS_TAK_TERLACAK;
        }
        String id = nzt(new sekuel().cariIsi(
                "select ifnull(id_composition,'') from " + tabel
                + " where no_rawat=? and ifnull(id_composition,'')<>'' limit 1", norwt)).trim();
        return id.equals("") ? "Belum Kirim" : "Sudah Kirim";
    }

    /**
     * Isi kedua label status di panel form RME sekaligus, dan atur tampilan tombol Preview TTE:
     * abu-abu bila belum ada TTE. Tombolnya sengaja TIDAK dinonaktifkan — tombol yang benar-benar
     * disabled tak menerima klik, sehingga notifikasi "TTE Belum Dikirim" tak akan pernah muncul.
     *
     * Semua pembacaan di sini lokal & murah (dua query kecil), jadi aman dipanggil tiap kali
     * baris tabel dipilih.
     */
    public static void tampilStatusRME(String jenis, String noRawat, javax.swing.JLabel lblBerkas,
                                       javax.swing.JLabel lblTte, javax.swing.JComponent tombolPreview) {
        String norwt = nzt(noRawat).trim();
        if (norwt.equals("")) {
            if (lblBerkas != null) lblBerkas.setText("Status Berkas : -");
            if (lblTte != null)    lblTte.setText("Status TTE : -");
            if (tombolPreview != null) tombolPreview.setForeground(WARNA_NONAKTIF);
            return;
        }
        if (lblBerkas != null) {
            lblBerkas.setText("Status Berkas : " + statusBerkasRME(jenis, norwt));
        }
        DlgTTESatuSehat.StatusTte st = DlgTTESatuSehat.statusTteRME(jenis, norwt);
        if (lblTte != null) {
            lblTte.setText("Status TTE : " + st.label);
        }
        if (tombolPreview != null) {
            tombolPreview.setForeground(st.adaTte ? WARNA_AKTIF : WARNA_NONAKTIF);
        }
    }

    /** Abu-abu = tombol Preview TTE belum ada gunanya (berkas belum pernah di-TTE). */
    private static final Color WARNA_NONAKTIF = new Color(150, 150, 150);
    private static final Color WARNA_AKTIF = new Color(40, 40, 40);

    /**
     * Membangun FHIR Bundle (type=transaction) untuk satu kunjungan, lalu mengirimnya ke
     * SATUSEHAT dalam SATU kali POST (bukan per-resource). Bila poli ANC, resource
     * EpisodeOfCare dan Encounter dikirim bersama dalam satu bundle dengan referensi silang
     * memakai fullUrl urn:uuid, sehingga Encounter dapat mereferensi EpisodeOfCare tanpa
     * perlu menunggu id dari server lebih dulu.
     */
    private boolean kirimBundle(int row, String idEncounterLama) throws Exception {
        langkahKe = 0;   // penomoran "x/28" dihitung ulang tiap pasien, termasuk saat batch
        tampilProgres("Menyiapkan data ...", 0);
        iddokter = cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(row,8).toString());
        idpasien = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(row,5).toString());

        // ID pasien IHS wajib ada — kalau belum, Patient/ akan invalid di server, bundle dibatalkan.
        if(idpasien==null || idpasien.equals("")){
            logWarn("No.Rawat " + tbObat.getValueAt(row,2).toString()
                    + " dilewati : ID pasien SATUSEHAT belum ditemukan.");
            return false;
        }

        // ID dokter IHS wajib ada — Encounter.participant, Condition.recorder, ServiceRequest.requester
        // mereferensi Practitioner. Kalau kosong, server tolak "Wrong reference ID format: Practitioner/".
        if(iddokter==null || iddokter.equals("")){
            logWarn("No.Rawat " + tbObat.getValueAt(row,2).toString()
                    + " dilewati : ID dokter SATUSEHAT (Practitioner) belum ditemukan untuk '"
                    + tbObat.getValueAt(row,7).toString()
                    + "'. Pastikan NIK dokter benar & sudah ter-mapping SATUSEHAT.");
            return false;
        }

        String noRawat      = tbObat.getValueAt(row,2).toString();
        noRawatBerjalan     = noRawat;   // dipakai konteks pencatat HTTP
        String namaPasien   = tbObat.getValueAt(row,4).toString();
        String namaDokter   = tbObat.getValueAt(row,7).toString();
        String mulai        = tbObat.getValueAt(row,1).toString();
        String pulang       = tbObat.getValueAt(row,14).toString();
        String statusLanjut = tbObat.getValueAt(row,13).toString();
        String idLokasi     = tbObat.getValueAt(row,11).toString();
        String kodePoli     = tbObat.getValueAt(row,9).toString();
        String namaPoli     = tbObat.getValueAt(row,10).toString();
        String idOrg        = koneksiDB.IDSATUSEHAT();
        boolean anc         = namaPoli.toLowerCase().contains("anc");
        boolean igd         = isPoliIgd(kodePoli, namaPoli);
        boolean update      = idEncounterLama!=null && !idEncounterLama.equals("");

        // === Bundle root (resourceType=Bundle, type=transaction) ===
        // type WAJIB "transaction" karena Encounter mereferensi EpisodeOfCare di bundle yang sama;
        // pada mode "batch" SATUSEHAT menolak referensi silang antar-entry (invalid_static_reference).
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        bundle.put("type", "transaction");
        ArrayNode entries = bundle.putArray("entry");
        List<String> metas = new ArrayList<>();

        // === Diagnosa WAJIB: server SATUSEHAT memandatkan Encounter.diagnosis (RuleNumber 10457) ===
        // Tiap diagnosa dibuat resource Condition; Encounter.diagnosis mereferensinya via urn:uuid.
        List<DiagnosaData> daftarDiagnosa = ambilSemuaDiagnosa(noRawat, statusLanjut);
        if(daftarDiagnosa.isEmpty()){
            logWarn("No.Rawat " + noRawat
                    + " dilewati : diagnosa tidak ditemukan (Encounter.diagnosis wajib diisi).");
            return false;
        }
        // Referensi Condition: pakai id lama (PUT) bila sudah pernah dikirim, kalau tidak urn:uuid (POST).
        List<String> conditionRefs = new ArrayList<>();
        List<String> conditionIds  = new ArrayList<>();
        for(int k=0;k<daftarDiagnosa.size();k++){
            String idCondLama = cariIdCondition(noRawat, daftarDiagnosa.get(k).kode, statusLanjut);
            conditionIds.add(idCondLama);
            conditionRefs.add((idCondLama==null || idCondLama.equals(""))
                    ? "urn:uuid:" + UUID.randomUUID().toString()
                    : "Condition/" + idCondLama);
        }

        // === Entry EpisodeOfCare (khusus ANC) — PUT bila id lama ada ===
        String episodeRef = "";
        if(anc){
            String idEpisodeLama = cariIdEpisode(noRawat);
            String episodeFullUrl = (idEpisodeLama==null || idEpisodeLama.equals(""))
                    ? "urn:uuid:" + UUID.randomUUID().toString()
                    : "EpisodeOfCare/" + idEpisodeLama;
            ObjectNode episode = buatEpisodeOfCare(noRawat, mulai, namaPasien, idOrg);
            tambahEntry(entries, episodeFullUrl, episode, "EpisodeOfCare", idEpisodeLama);
            metas.add("EpisodeOfCare");
            episodeRef = episodeFullUrl;
        }

        // === Entry Encounter — PUT bila update (id lama dari kolom ID Encounter), kalau tidak POST ===
        String encounterFullUrl = update
                ? "Encounter/" + idEncounterLama
                : "urn:uuid:" + UUID.randomUUID().toString();
        ObjectNode encounter = buatEncounter(noRawat, mulai, pulang, namaPasien, namaDokter,
                statusLanjut, idLokasi, kodePoli, namaPoli, idOrg, anc, episodeRef,
                daftarDiagnosa, conditionRefs);
        // POST Encounter pakai ifNoneExist (identifier) supaya idempotent: bila Encounter dgn identifier
        // ini sudah ada di server (mis. kiriman sebelumnya tak tersimpan lokal), server mengembalikan yang
        // lama -> tidak error "Found duplicate resource: Encounter" (RuleNumber 20002).
        tambahEntry(entries, encounterFullUrl, encounter, "Encounter", update ? idEncounterLama : "",
                update ? "" : ("identifier=http://sys-ids.kemkes.go.id/encounter/" + idOrg + "|" + noRawat));
        metas.add("Encounter");

        // === Entry Condition (diagnosa) — PUT bila id lama ada, kalau tidak POST ===
        for(int k=0;k<daftarDiagnosa.size();k++){
            ObjectNode condition = buatConditionDiagnosa(daftarDiagnosa.get(k), namaPasien, namaDokter,
                    encounterFullUrl, mulai);
            tambahEntry(entries, conditionRefs.get(k), condition, "Condition", conditionIds.get(k));
            metas.add("Condition:" + daftarDiagnosa.get(k).kode);
        }

        // === Entry ServiceRequest SPRI (khusus IGD: Surat Perintah Rawat Inap) ===
        // Dibuat hanya bila pasien lewat IGD dan ada record di surat_perintah_rawat_inap.
        if(igd){
            SpriData spri = ambilSpri(noRawat);
            if(spri!=null && spri.noSurat!=null && !spri.noSurat.equals("")){
                String idRequester = ambilIhsDokterByKodeDokter(spri.kdDokter);
                if(idRequester==null || idRequester.equals("")){
                    idRequester = iddokter;
                }
                String namaRequester = (spri.nmDokter==null || spri.nmDokter.equals("")) ? namaDokter : spri.nmDokter;
                // Identifier dinormalkan (buang non-alfanumerik) agar tidak mengandung "/" yang
                // merusak pencarian identifier & memicu "Found duplicate". Mis. SPRI/20260623/001 -> SPRI20260623001.
                String spriValue = normalisasiIdentifier(spri.noSurat);
                ObjectNode spriSr = buatSpriServiceRequest(spri, encounterFullUrl, namaPasien,
                        idRequester, namaRequester, idOrg,
                        daftarDiagnosa.get(0), conditionRefs.get(0), mulai, spriValue);
                // Cari id SPRI: utamakan mapping lokal (satu_sehat_spri) yang deterministik;
                // kalau kosong, fallback cek ke server via identifier. PUT bila ketemu (hindari
                // "Found duplicate"), POST bila benar-benar baru.
                String spriSystem = "http://sys-ids.kemkes.go.id/servicerequest/" + idOrg;
                String idSpriLama = cariIdSpriLokal(spri.noSurat);
                if(idSpriLama.equals("")){
                    idSpriLama = cariIdServerByIdentifier("ServiceRequest", spriSystem, spriValue);
                }
                String spriFullUrl = (idSpriLama==null || idSpriLama.equals(""))
                        ? "urn:uuid:" + UUID.randomUUID().toString()
                        : "ServiceRequest/" + idSpriLama;
                tambahEntry(entries, spriFullUrl, spriSr, "ServiceRequest", idSpriLama);
                metas.add("ServiceRequest:" + spri.noSurat);
            }else{
                logInfo("No.Rawat " + noRawat + " : SPRI tidak ditemukan, ServiceRequest SPRI dilewati.");
            }
        }

        // === Kirim Bundle (HTTP POST ke base URL FHIR) ===
        String mode = update ? "UPDATE" : "KIRIM";
        SatuSehatHttpLogger.Konteks.set("Bundle (" + mode + ")", noRawat);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String payload = mapper.writeValueAsString(bundle);
        logInfo("[" + mode + "] No.Rawat " + noRawat + " | Pasien : " + namaPasien + " | Dokter : " + namaDokter);
        logInfo("[" + mode + "] Mengirim " + metas.size() + " resource (" + String.join(", ", metas) + ") ke " + link);
        logInfo("[" + mode + "] Request body :\n" + formatJson(payload));
        requestEntity = new HttpEntity(payload, headers);

        String serviceName = "Bundle (" + mode + ")";
        // Bundle adalah SATU POST berisi banyak resource, jadi tidak ada kemajuan yang bisa
        // dicacah sampai balasan server datang. Bar dibuat bergerak terus, keterangannya
        // menyebut berapa resource yang sedang ditunggu supaya operator tahu ini wajar lama.
        logProgresTanpaUkuran(serviceName,
                "Mengirim Bundle " + metas.size() + " resource, menunggu balasan server ...");
        long mulaiMs = System.currentTimeMillis();
        int responseCode;
        try {
            org.springframework.http.ResponseEntity<String> resp =
                    api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class);
            json = resp.getBody();
            responseCode = resp.getStatusCode().value();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            int durasi = (int)(System.currentTimeMillis() - mulaiMs);
            logError("[" + mode + "] No.Rawat " + noRawat + " ditolak server (HTTP " + e.getStatusCode() + ", " + durasi + " ms).");
            logError("[" + mode + "] Detail dari server :\n" + formatJson(e.getResponseBodyAsString()));
            simpanLog(serviceName, noRawat, "", payload, e.getStatusCode().value(),
                    e.getResponseBodyAsString(), "FAILED", e.getMessage(), durasi);
            return false;
        }
        int durasi = (int)(System.currentTimeMillis() - mulaiMs);
        logSukses("[" + mode + "] Response diterima dari server (HTTP " + responseCode + ", " + durasi + " ms) :\n" + formatJson(json));

        // === Simpan id hasil response ke tabel mapping (urutan entry response = urutan entry kirim) ===
        root = mapper.readTree(json);
        JsonNode entryResponse = root.path("entry");
        for(int x=0; x<metas.size(); x++){
            String tipe = metas.get(x);
            String idResource = "";
            if(entryResponse.isArray() && entryResponse.size()>x){
                JsonNode responseNode = entryResponse.get(x).path("response");
                idResource = extractIdResource(responseNode.path("location").asText());
                if(idResource.equals("")){
                    idResource = responseNode.path("resourceID").asText();
                }
                if(idResource.equals("")){
                    idResource = entryResponse.get(x).path("resource").path("id").asText();
                }
            }
            if(idResource.equals("")){
                continue;
            }
            if(tipe.equals("EpisodeOfCare")){
                Sequel.menyimpantf2("satu_sehat_episodeofcare","?,?","No.Rawat",2,new String[]{noRawat,idResource});
            }else if(tipe.equals("Encounter")){
                Sequel.menyimpantf2("satu_sehat_encounter","?,?","No.Rawat",2,new String[]{noRawat,idResource});
                tbObat.setValueAt(idResource,row,15);
                tbObat.setValueAt(false,row,0);
            }else if(tipe.startsWith("Condition:")){
                simpanCondition(noRawat, tipe.substring("Condition:".length()), statusLanjut, idResource);
            }else if(tipe.startsWith("ServiceRequest:")){
                simpanSpri(tipe.substring("ServiceRequest:".length()), noRawat, idResource);
            }
        }
        String encounterId = tbObat.getValueAt(row,15).toString();
        logSukses("[" + mode + "] No.Rawat " + noRawat + " : Encounter & resource utama tersimpan (Encounter id = "
                + encounterId + ").");
        simpanLog(serviceName, noRawat, encounterId, payload, responseCode, json, "SUCCESS", null, durasi);

        // Setelah Encounter IGD tersimpan, kirim Dokumen Triase IGD sebagai bundle terpisah
        // (Composition mereferensi Encounter ini). Dibungkus try/catch agar tidak mengganggu alur Encounter.
        if(igd){
            logProgres("Triase IGD");
            try {
                triaseIGD.kirim(noRawat, tbObat.getValueAt(row,15).toString());
            } catch (Exception e) {
                logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Triase IGD : " + e);
            }
        }

        // Untuk kunjungan rawat inap, kirim Resume Medis Rawat Inap (Composition LOINC 34105-7)
        // sebagai bundle terpisah dari resume_pasien_ranap. Dibungkus try/catch agar tidak
        // mengganggu alur Encounter (resume mungkin belum diisi saat Encounter dikirim).
        if(statusLanjut.equals("Ranap")){
            logProgres("Resume Ranap");
            try {
                resumeRanap.kirim(noRawat, tbObat.getValueAt(row,15).toString());
            } catch (Exception e) {
                logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Resume Ranap : " + e);
            }
        }

        // Laporan Persalinan (Composition INC, LOINC 57057-2). Self-skip bila tidak ada catatan_persalinan.
        logProgres("Laporan Persalinan");
        try {
            laporanPersalinan.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan Persalinan : " + e);
        }

        // FamilyMemberHistory (riwayat penyakit keluarga). Self-skip bila hubungan & rpk kosong.
        logProgres("FamilyMemberHistory");
        try {
            familyHistory.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim FamilyMemberHistory : " + e);
        }

        // Laporan Operasi (Composition LOINC 11504-8). Self-skip bila tidak ada laporan_operasi.
        logProgres("Laporan Operasi");
        try {
            laporanOperasi.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan Operasi : " + e);
        }

        // Laporan Tindakan Echo (Composition LOINC 28570-0). Self-skip bila tidak ada hasil_pemeriksaan_echo.
        logProgres("Laporan Echo");
        try {
            laporanEcho.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan Echo : " + e);
        }

        // Laporan Tindakan ESWL (Composition LOINC 28570-0). Self-skip bila tidak ada hasil_tindakan_eswl.
        logProgres("Laporan ESWL");
        try {
            laporanEswl.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan ESWL : " + e);
        }

        // Laporan USG (Composition LOINC 28570-0). Self-skip bila tidak ada hasil_pemeriksaan_usg.
        logProgres("Laporan USG");
        try {
            laporanUsg.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan USG : " + e);
        }

        // Laporan EKG (Composition LOINC 28570-0). Self-skip bila tidak ada hasil_pemeriksaan_ekg.
        logProgres("Laporan EKG");
        try {
            laporanEkg.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan EKG : " + e);
        }

        // Skrining TBC (Observation antropometri + kesimpulan). Self-skip bila tidak ada baris skrining_tbc.
        logProgres("Skrining TBC");
        try {
            skriningTBC.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Skrining TBC : " + e);
        }

        // Obat: MedicationRequest + MedicationStatement + MedicationDispense + MedicationAdministration
        // (satu bundle). Self-skip bila tidak ada resep/pemberian ber-KFA.
        logProgres("Obat");
        try {
            obat.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Obat : " + e);
        }

        // Laboratorium: ServiceRequest + Specimen + Observation + DiagnosticReport (chunked).
        // Self-skip bila tidak ada permintaan_lab / belum dipetakan satu_sehat_mapping_lab.
        logProgres("Lab");
        try {
            lab.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Lab : " + e);
        }

        // Radiologi: ServiceRequest + Observation + DiagnosticReport (chunked).
        // Self-skip bila tidak ada permintaan_radiologi / belum dipetakan satu_sehat_mapping_radiologi.
        logProgres("Radiologi");
        try {
            radiologi.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Radiologi : " + e);
        }

        // Citra Radiologi (study DICOM dari modality yg sudah ada di Orthanc -> ImagingStudy via DICOM
        // Router). BUKAN entry FHIR. HARUS setelah Radiologi: router mengaitkan ImagingStudy.basedOn ke
        // ServiceRequest lewat identifier ACSN, jadi ServiceRequest wajib sudah ada. Bila lahir
        // ImagingStudy baru, resource radiologi dikirim ulang (PUT idempotent) supaya DiagnosticReport
        // ikut membawa imagingStudy[] — inilah yang menyambungkan laporan ke citranya.
        logProgres("DICOM Radiologi");
        try {
            if (dicomRadiologi.kirim(noRawat, tbObat.getValueAt(row,15).toString())) {
                radiologi.kirim(noRawat, tbObat.getValueAt(row,15).toString());
            }
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim DICOM Radiologi : " + e);
        }

        // Berkas DICOM (dokumen DOC di-wrap DICOM -> ImagingStudy via DICOM Router). BUKAN entry FHIR:
        // upload Orthanc -> suntik AdmissionID=Encounter ID -> C-STORE ke router. Self-skip bila tak ada berkas.
        logProgres("Berkas DICOM");
        try {
            berkasDicom.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Berkas DICOM : " + e);
        }

        // Diet: NutritionOrder per waktu makan (chunked).
        // Self-skip bila tidak ada detail_beri_diet / belum dipetakan satu_sehat_mapping_diet.
        logProgres("Diet");
        try {
            diet.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Diet : " + e);
        }

        // Prognosis (ClinicalImpression.prognosisCodeableConcept SNOMED).
        // Self-skip bila tidak ada/tak terpetakan prognosis.
        logProgres("Prognosa");
        try {
            prognosa.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Prognosa : " + e);
        }

        // Telaah/Pengkajian Resep farmasi (QuestionnaireResponse Q0007).
        // Self-skip bila tidak ada telaah_farmasi. (Setelah Obat, agar MedicationRequest sudah ada.)
        logProgres("Telaah Farmasi");
        try {
            telaahFarmasi.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Telaah Farmasi : " + e);
        }

        // Goal (tujuan/rencana tindak lanjut) dari resume_pasien_ranap.dilanjutkan + ket_dilanjutkan.
        logProgres("Goal");
        try {
            goal.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Goal : " + e);
        }

        // RiskAssessment (penilaian risiko jatuh: Morse/Humpty/Edmonson).
        logProgres("RiskAssessment");
        try {
            riskAssessment.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim RiskAssessment : " + e);
        }

        // Laporan Anestesi (Composition 84062-9) dari laporan_anestesi.
        logProgres("Laporan Anestesi");
        try {
            laporanAnestesi.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Laporan Anestesi : " + e);
        }

        // Alkes (Device chain): DeviceRequest/Dispense/UseStatement + SupplyRequest/Delivery.
        logProgres("Alkes");
        try {
            alkes.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Alkes : " + e);
        }

        // Billing: Coverage + Account + ChargeItem + Invoice + PATCH Encounter.account.
        logProgres("Billing");
        try {
            billing.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Billing : " + e);
        }

        // Obat Kronis: MedicationRequest + MedicationDispense (apotek BPJS/PRB).
        logProgres("ObatKronis");
        try {
            obatKronis.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim ObatKronis : " + e);
        }

        // Pre-Anestesi: Observation LOINC 34751-8 (ASA + rencana anestesi).
        logProgres("PreAnestesi");
        try {
            preAnestesi.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim PreAnestesi : " + e);
        }

        // EpisodeOfCare (Plan): Condition + EpisodeOfCare (episode/rencana perawatan).
        logProgres("EpisodeOfCare");
        try {
            episodeOfCare.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim EpisodeOfCare : " + e);
        }

        // Resume Medis Rawat Jalan: Composition LOINC 88645-7 (resume_pasien / pemeriksaan_ralan terakhir).
        logProgres("ResumeRajal");
        try {
            resumeRajal.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim ResumeRajal : " + e);
        }

        // Surat Rencana Kontrol: ServiceRequest (Follow-up visit) dari bridging_surat_kontrol_bpjs.
        // Self-skip bila tidak ada surat kontrol untuk kunjungan ini.
        logProgres("Rencana Kontrol");
        try {
            rencanaKontrol.kirim(noRawat, tbObat.getValueAt(row,15).toString());
        } catch (Exception e) {
            logError("[" + mode + "] No.Rawat " + noRawat + " : gagal kirim Rencana Kontrol : " + e);
        }

        // === Langkah terakhir: hubungkan Encounter ini ke klaim di eKlaim ===
        // Sama seperti tombol "Kirim/Update Satu Sehat Klaim" di INACBGData: setelah seluruh
        // payload SATUSEHAT sukses, id Encounter didaftarkan ke klaim SEP lewat method
        // satusehat_encounter_set. Kegagalan di sini TIDAK membatalkan bundle yang sudah terkirim.
        logProgres("Set Encounter E-Klaim");
        pesanEklaimTerakhir = setEncounterKlaimEklaim(
                noRawat, nzs(tbObat.getValueAt(row,16)), tbObat.getValueAt(row,15).toString());
        return true;
    }

    /**
     * Daftarkan id Encounter SATUSEHAT ke klaim eKlaim (method satusehat_encounter_set) supaya
     * kunjungan ini terhubung ke klaimnya. Dipanggil sebagai langkah paling akhir kirimBundle,
     * jadi hanya jalan bila bundle SATUSEHAT-nya sudah sukses.
     *
     * Semua kegagalan ditelan (dicatat sebagai WARN/ERROR + baris satu_sehat_log) karena resource
     * SATUSEHAT sudah terlanjur terkirim — pasien tidak boleh dianggap gagal hanya karena WS
     * eKlaim tidak bisa dihubungi atau klaimnya belum dibuat di eKlaim.
     *
     * @return pesan balasan eKlaim, atau alasan kenapa langkah ini dilewati.
     */
    private String setEncounterKlaimEklaim(String noRawat, String noSep, String idEncounter) {
        if (noSep == null || noSep.trim().equals("")) {
            logInfo("[E-Klaim] No.Rawat " + noRawat + " : tidak punya No.SEP (bukan pasien BPJS / SEP "
                    + "belum dibuat), langkah satusehat_encounter_set dilewati.");
            return "Dilewati : No.SEP kosong.";
        }
        if (idEncounter == null || idEncounter.trim().equals("")) {
            logWarn("[E-Klaim] No.Rawat " + noRawat + " : id Encounter kosong, langkah "
                    + "satusehat_encounter_set dilewati.");
            return "Dilewati : id Encounter kosong.";
        }
        noSep = noSep.trim();
        idEncounter = idEncounter.trim();

        String requestJson = "{\n"
                + "    \"metadata\": {\n"
                + "        \"method\": \"satusehat_encounter_set\",\n"
                + "        \"nomor_sep\": \"" + noSep + "\"\n"
                + "    },\n"
                + "    \"data\": {\n"
                + "        \"encounters\": [\n"
                + "            \"" + idEncounter + "\"\n"
                + "        ]\n"
                + "    }\n"
                + "}";
        long mulaiMs = System.currentTimeMillis();
        try {
            String encryptedRequest = ApiINACBG.mcEncrypt(requestJson, ApiINACBG.getKey());
            HttpHeaders headerEklaim = new HttpHeaders();
            headerEklaim.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String responseBody = new ApiINACBG().getRest().exchange(
                    ApiINACBG.getUrlWS(), HttpMethod.POST,
                    new HttpEntity<>(encryptedRequest, headerEklaim), String.class).getBody();
            int durasi = (int)(System.currentTimeMillis() - mulaiMs);

            if (responseBody == null || responseBody.isEmpty()) {
                logWarn("[E-Klaim] No.Rawat " + noRawat + " : respon kosong dari WS eKlaim.");
                simpanLog("Set Encounter E-Klaim", noRawat, idEncounter, requestJson, null, "",
                        "FAILED", "Response kosong dari server eKlaim.", durasi);
                return "Response kosong dari server eKlaim.";
            }
            String decrypted = ApiINACBG.mcDecrypt(
                    ApiINACBG.cleanResponse(responseBody), ApiINACBG.getKey()).trim();
            String pesan = bacaPesanEklaim(decrypted);
            logSukses("[E-Klaim] No.Rawat " + noRawat + " | SEP " + noSep + " | Encounter "
                    + idEncounter + " -> " + pesan);
            simpanLog("Set Encounter E-Klaim", noRawat, idEncounter, requestJson, null, decrypted,
                    "SUCCESS", null, durasi);
            return pesan;
        } catch (Exception e) {
            int durasi = (int)(System.currentTimeMillis() - mulaiMs);
            logError("[E-Klaim] No.Rawat " + noRawat + " : gagal kirim satusehat_encounter_set : " + e);
            simpanLog("Set Encounter E-Klaim", noRawat, idEncounter, requestJson, null, null,
                    "FAILED", String.valueOf(e), durasi);
            return "Gagal : " + e;
        }
    }

    /** Ambil metadata.message dari balasan eKlaim (objek maupun array); apa adanya bila bukan JSON. */
    private String bacaPesanEklaim(String decrypted) {
        try {
            JsonNode node = mapper.readTree(decrypted);
            if (node.isArray() && node.size() > 0) {
                node = node.get(0);
            }
            if (node.has("metadata") && node.path("metadata").has("message")) {
                return node.path("metadata").path("message").asText();
            }
        } catch (Exception e) {
            // balasan bukan JSON -> kembalikan teks aslinya
        }
        return decrypted;
    }

    /** Bangun resource EpisodeOfCare (dipakai untuk kunjungan ANC). */
    private ObjectNode buatEpisodeOfCare(String noRawat, String mulai, String namaPasien, String idOrg){
        ObjectNode episode = mapper.createObjectNode();
        episode.put("resourceType", "EpisodeOfCare");

        ObjectNode identifier = episode.putArray("identifier").addObject();
        identifier.put("system", "http://sys-ids.kemkes.go.id/episode-of-care/" + idOrg);
        identifier.put("value", noRawat);

        episode.put("status", "active");

        ObjectNode statusHistory = episode.putArray("statusHistory").addObject();
        statusHistory.put("status", "active");
        statusHistory.putObject("period").put("start", mulai);

        ObjectNode coding = episode.putArray("type").addObject().putArray("coding").addObject();
        coding.put("system", "http://terminology.kemkes.go.id/CodeSystem/episodeofcare-type");
        coding.put("code", "ANC");
        coding.put("display", "Antenatal Care");

        ObjectNode patient = episode.putObject("patient");
        patient.put("reference", "Patient/" + idpasien);
        patient.put("display", namaPasien);

        episode.putObject("period").put("start", mulai);
        episode.putObject("managingOrganization").put("reference", "Organization/" + idOrg);
        return episode;
    }

    /** Bangun resource Encounter; bila anc=true ditambah referensi episodeOfCare (urn:uuid) + identifier ANC. */
    private ObjectNode buatEncounter(String noRawat, String mulai, String pulang, String namaPasien,
            String namaDokter, String statusLanjut, String idLokasi, String kodePoli, String namaPoli,
            String idOrg, boolean anc, String episodeRef,
            List<DiagnosaData> daftarDiagnosa, List<String> conditionRefs){
        boolean igd   = isPoliIgd(kodePoli, namaPoli);
        boolean ranap = statusLanjut.equals("Ranap");
        // Class utama Encounter: Ranap->IMP, IGD (rawat jalan)->EMER, selain itu->AMB.
        String classCode    = ranap ? "IMP" : (igd ? "EMER" : "AMB");
        String classDisplay = ranap ? "inpatient encounter" : (igd ? "emergency" : "ambulatory");
        // Data diambil dari kunjungan yang SUDAH punya tanggal pulang -> status akhir "finished".
        String selesai = (pulang==null || pulang.equals("")) ? mulai : pulang;
        // Ranap: akhiri Encounter (period.end, classHistory IMP, statusHistory finished) pada waktu
        // KELUAR KAMAR terakhir (kamar_inap), bukan waktu nota billing (nota_inap) -> konsisten dgn
        // Histori Lokasi. Fallback ke pulang/mulai bila belum ada jam keluar kamar (mis. masih dirawat).
        if (ranap) {
            String keluarBangsal = ambilJamKeluarBangsal(noRawat);
            if (!keluarBangsal.equals("")) selesai = keluarBangsal;
        }
        ObjectNode encounter = mapper.createObjectNode();
        encounter.put("resourceType", "Encounter");
        encounter.put("status", "finished");

        ObjectNode kelas = encounter.putObject("class");
        kelas.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
        kelas.put("code", classCode);
        kelas.put("display", classDisplay);

        ObjectNode subject = encounter.putObject("subject");
        subject.put("reference", "Patient/" + idpasien);
        subject.put("display", namaPasien);

        ObjectNode participant = encounter.putArray("participant").addObject();
        ObjectNode pCoding = participant.putArray("type").addObject().putArray("coding").addObject();
        pCoding.put("system", "http://terminology.hl7.org/CodeSystem/v3-ParticipationType");
        pCoding.put("code", "ATND");
        pCoding.put("display", "attender");
        ObjectNode individual = participant.putObject("individual");
        individual.put("reference", "Practitioner/" + iddokter);
        individual.put("display", namaDokter);

        ObjectNode period = encounter.putObject("period");
        period.put("start", mulai);
        period.put("end", selesai);

        // Batas periode EMER->IMP (pasien lewat IGD lalu rawat inap) = jam masuk bangsal (kamar_inap).
        String masukBangsal = ranap ? ambilJamMasukBangsal(noRawat) : "";
        boolean adaPeriodeIgd = ranap && igd
                && !masukBangsal.equals("") && !masukBangsal.equals(mulai);

        // location: lokasi IGD (bila lewat IGD) LALU lokasi bangsal rawat inap (bila Ranap).
        // Pasien IGD->Ranap memunculkan kedua lokasi + periodenya dalam SATU Encounter.
        ArrayNode location = encounter.putArray("location");
        boolean adaLokasi = false;
        if (igd && !idLokasi.equals("")) {
            ObjectNode item = location.addObject();
            tambahServiceClassOutpatient(item);   // IGD/poli -> kelas Reguler (outpatient)
            ObjectNode loc = item.putObject("location");
            loc.put("reference", "Location/" + idLokasi);
            loc.put("display", namaPoli);
            if (adaPeriodeIgd) {
                ObjectNode per = item.putObject("period");
                per.put("start", mulai);
                per.put("end", masukBangsal);
            }
            adaLokasi = true;
        }
        if (ranap) {
            for (LokasiRanap w : ambilLokasiRanap(noRawat)) {
                if (w.idLokasi.equals("")) continue;
                ObjectNode item = location.addObject();
                tambahServiceClassInpatient(item, w.kelas);   // kelas perawatan per kamar (deteksi naik/turun kelas)
                ObjectNode loc = item.putObject("location");
                loc.put("reference", "Location/" + w.idLokasi);
                loc.put("display", w.display);
                if (!w.start.equals("") || !w.end.equals("")) {
                    ObjectNode per = item.putObject("period");
                    if (!w.start.equals("")) per.put("start", w.start);
                    if (!w.end.equals("")) per.put("end", w.end);
                }
                adaLokasi = true;
            }
        }
        // Fallback: Ralan biasa, atau Ranap tanpa data kamar/mapping -> pakai lokasi poli registrasi.
        if (!adaLokasi && !idLokasi.equals("")) {
            ObjectNode loc = location.addObject().putObject("location");
            loc.put("reference", "Location/" + idLokasi);
            loc.put("display", namaPoli);
        }

        // statusHistory siklus penuh sesuai katalog. Lewat IGD ditambah tahap "triaged".
        ArrayNode statusHistory = encounter.putArray("statusHistory");
        tambahStatusHistory(statusHistory, "arrived", mulai, mulai);
        if (igd) {
            tambahStatusHistory(statusHistory, "triaged", mulai, mulai);
        }
        tambahStatusHistory(statusHistory, "in-progress", mulai, selesai);
        tambahStatusHistory(statusHistory, "finished", selesai, selesai);

        // classHistory: rekam kelas sepanjang kunjungan. IGD->Ranap = EMER lalu IMP.
        ArrayNode classHistory = encounter.putArray("classHistory");
        if (adaPeriodeIgd) {
            tambahClassHistory(classHistory, "EMER", "emergency", mulai, masukBangsal);
            tambahClassHistory(classHistory, "IMP", "inpatient encounter", masukBangsal, selesai);
        } else {
            tambahClassHistory(classHistory, classCode, classDisplay, mulai, selesai);
        }

        encounter.putObject("serviceProvider").put("reference", "Organization/" + idOrg);

        if(anc && !episodeRef.equals("")){
            encounter.putArray("episodeOfCare").addObject().put("reference", episodeRef);
        }

        ArrayNode identifier = encounter.putArray("identifier");
        ObjectNode id0 = identifier.addObject();
        id0.put("system", "http://sys-ids.kemkes.go.id/encounter/" + idOrg);
        id0.put("value", noRawat);
        if(anc){
            ObjectNode id1 = identifier.addObject();
            id1.put("system", "http://terminology.kemkes.go.id/CodeSystem/episodeofcare/ANC");
            id1.put("value", "K1A");
        }

        // diagnosis (WAJIB): referensi ke tiap Condition di bundle, use=AD, rank sesuai prioritas.
        ArrayNode diagnosis = encounter.putArray("diagnosis");
        for(int k=0;k<daftarDiagnosa.size();k++){
            DiagnosaData d = daftarDiagnosa.get(k);
            ObjectNode diagItem = diagnosis.addObject();
            ObjectNode condRef = diagItem.putObject("condition");
            condRef.put("reference", conditionRefs.get(k));
            condRef.put("display", d.nama);
            ObjectNode useCoding = diagItem.putObject("use").putArray("coding").addObject();
            useCoding.put("system", "http://terminology.hl7.org/CodeSystem/diagnosis-role");
            useCoding.put("code", "AD");
            useCoding.put("display", "Admission diagnosis");
            diagItem.put("rank", k + 1);
        }
        return encounter;
    }

    /** Helper: tambah satu entri statusHistory (status + period start/end) ke Encounter. */
    private void tambahStatusHistory(ArrayNode statusHistory, String status, String start, String end){
        ObjectNode item = statusHistory.addObject();
        item.put("status", status);
        ObjectNode period = item.putObject("period");
        period.put("start", start);
        period.put("end", end);
    }

    /** Lokasi rawat jalan/IGD: ServiceClass outpatient "reguler" (Kelas Reguler), kelas tetap. */
    private void tambahServiceClassOutpatient(ObjectNode locationItem){
        tambahServiceClass(locationItem,
                "http://terminology.kemkes.go.id/CodeSystem/locationServiceClass-Outpatient",
                "reguler", "Kelas Reguler", "kelas-tetap", "Kelas Tetap Perawatan");
    }

    /** Lokasi rawat inap: ServiceClass inpatient sesuai kelas kamar (1/2/3/vip/vvip). Skip bila kelas tak dikenal. */
    private void tambahServiceClassInpatient(ObjectNode locationItem, String kelas){
        String[] kd = kodeKelasInap(kelas);
        if (kd == null) return;   // kelas tak terpetakan -> lokasi tetap dikirim tanpa ServiceClass
        tambahServiceClass(locationItem,
                "http://terminology.kemkes.go.id/CodeSystem/locationServiceClass-Inpatient",
                kd[0], kd[1], "kelas-tetap", "Kelas Tetap Perawatan");
    }

    /** {code,display} ServiceClass inpatient dari teks kelas Khanza; null bila tak dikenal (ICU/intensif dll). */
    private String[] kodeKelasInap(String kelas){
        String k = (kelas==null) ? "" : kelas.trim().toLowerCase();
        if (k.equals("")) return null;
        if (k.contains("vvip")) return new String[]{"vvip", "Kelas VVIP"};
        if (k.contains("vip"))  return new String[]{"vip", "Kelas VIP"};
        if (k.contains("1"))    return new String[]{"1", "Kelas 1"};
        if (k.contains("2"))    return new String[]{"2", "Kelas 2"};
        if (k.contains("3"))    return new String[]{"3", "Kelas 3"};
        return null;
    }

    /** Bangun extension ServiceClass (value + upgradeClassIndicator) pada satu entri Encounter.location. */
    private void tambahServiceClass(ObjectNode locationItem, String svcSystem, String svcCode, String svcDisplay,
            String upgradeCode, String upgradeDisplay){
        ObjectNode wrap = locationItem.putArray("extension").addObject();
        wrap.put("url", "https://fhir.kemkes.go.id/r4/StructureDefinition/ServiceClass");
        ArrayNode inner = wrap.putArray("extension");
        ObjectNode v = inner.addObject();
        v.put("url", "value");
        ObjectNode vc = v.putObject("valueCodeableConcept").putArray("coding").addObject();
        vc.put("system", svcSystem);
        vc.put("code", svcCode);
        vc.put("display", svcDisplay);
        ObjectNode u = inner.addObject();
        u.put("url", "upgradeClassIndicator");
        ObjectNode uc = u.putObject("valueCodeableConcept").putArray("coding").addObject();
        uc.put("system", "http://terminology.kemkes.go.id/CodeSystem/locationUpgradeClass");
        uc.put("code", upgradeCode);
        uc.put("display", upgradeDisplay);
    }

    /** Deteksi apakah kunjungan lewat IGD/UGD berdasarkan kode/nama poli registrasi. */
    private boolean isPoliIgd(String kodePoli, String namaPoli){
        String kode = (kodePoli==null) ? "" : kodePoli.trim().toUpperCase();
        String nama = (namaPoli==null) ? "" : namaPoli.trim().toUpperCase();
        return kode.contains("IGD") || kode.contains("UGD") || kode.equals("ER") || kode.equals("EMER")
                || nama.contains("IGD") || nama.contains("UGD")
                || nama.contains("GAWAT DARURAT") || nama.contains("EMERGENCY");
    }

    /** Ambil waktu masuk bangsal pertama (kamar_inap) sebagai "tgl T jam +07:00", atau "" bila tidak ada. */
    private String ambilJamMasukBangsal(String noRawat){
        String hasil = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select tgl_masuk, jam_masuk from kamar_inap where no_rawat=? "
                    + "order by tgl_masuk asc, jam_masuk asc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                String tgl = r.getString("tgl_masuk");
                String jam = r.getString("jam_masuk");
                if (tgl!=null && !tgl.equals("") && jam!=null && !jam.equals("")) {
                    hasil = tgl + "T" + jam + "+07:00";
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ambilJamMasukBangsal : " + e);
        }
        return hasil;
    }

    /** Ambil waktu KELUAR kamar terakhir (kamar_inap) sebagai "tgl T jam +07:00", atau "" bila belum ada
     *  tgl/jam keluar (mis. pasien masih dirawat). Dipakai untuk mengakhiri Encounter ranap pada waktu
     *  keluar kamar, bukan waktu nota billing. */
    private String ambilJamKeluarBangsal(String noRawat){
        String hasil = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select tgl_keluar, jam_keluar from kamar_inap where no_rawat=? "
                    + "and tgl_keluar is not null and tgl_keluar not in ('0000-00-00','') "
                    + "order by tgl_keluar desc, jam_keluar desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                String tgl = r.getString("tgl_keluar");
                String jam = r.getString("jam_keluar");
                if (tgl!=null && !tgl.equals("") && !tgl.startsWith("0000") && jam!=null && !jam.equals("")) {
                    hasil = tgl + "T" + jam + "+07:00";
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ambilJamKeluarBangsal : " + e);
        }
        return hasil;
    }

    /** Penampung satu lokasi bangsal rawat inap (id lokasi SATUSEHAT + periode masuk/keluar). */
    private static class LokasiRanap {
        String idLokasi="", display="", start="", end="", kelas="";
    }

    /** Ambil daftar lokasi bangsal rawat inap (kamar_inap -> mapping_lokasi_ranap) urut waktu masuk. */
    private List<LokasiRanap> ambilLokasiRanap(String noRawat){
        List<LokasiRanap> hasil = new ArrayList<>();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    // id_lokasi: pakai mapping kd_kamar persis; bila kosong, fallback ke kamar lain
                    // di BANGSAL yg sama (Location SATUSEHAT didaftarkan per ruangan/bangsal, bukan per-bed).
                    "select ifnull(nullif(sm.id_lokasi_satusehat,''), ifnull(("
                    + "  select sm2.id_lokasi_satusehat from satu_sehat_mapping_lokasi_ranap sm2 "
                    + "  inner join kamar k2 on k2.kd_kamar=sm2.kd_kamar "
                    + "  where k2.kd_bangsal=kamar.kd_bangsal and ifnull(sm2.id_lokasi_satusehat,'')<>'' limit 1"
                    + "),'')) as id_lokasi, "
                    + "ifnull(bangsal.nm_bangsal, kamar_inap.kd_kamar) as nm_bangsal, "
                    + "ifnull(kamar.kelas,'') as kelas, "
                    + "kamar_inap.tgl_masuk, kamar_inap.jam_masuk, kamar_inap.tgl_keluar, kamar_inap.jam_keluar "
                    + "from kamar_inap "
                    + "left join kamar on kamar.kd_kamar=kamar_inap.kd_kamar "
                    + "left join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal "
                    + "left join satu_sehat_mapping_lokasi_ranap sm on sm.kd_kamar=kamar_inap.kd_kamar "
                    + "where kamar_inap.no_rawat=? order by kamar_inap.tgl_masuk asc, kamar_inap.jam_masuk asc");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                LokasiRanap l = new LokasiRanap();
                l.idLokasi = nz(r.getString("id_lokasi"));
                String bangsal = nz(r.getString("nm_bangsal"));
                String kelas = nz(r.getString("kelas"));
                l.kelas = kelas;
                l.display = bangsal + (kelas.equals("") ? "" : " Kelas " + kelas);
                l.start = gabungWaktu(r.getString("tgl_masuk"), r.getString("jam_masuk"));
                l.end = gabungWaktu(r.getString("tgl_keluar"), r.getString("jam_keluar"));
                hasil.add(l);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ambilLokasiRanap : " + e);
        }
        return hasil;
    }

    /** Gabung tanggal+jam jadi "yyyy-MM-ddTHH:mm:ss+07:00"; "" bila tanggal kosong. */
    private String gabungWaktu(String tgl, String jam){
        if (tgl==null || tgl.equals("") || tgl.startsWith("0000")) {
            return "";
        }
        String j = (jam==null || jam.equals("")) ? "00:00:00" : jam;
        return tgl + "T" + j + "+07:00";
    }

    /** Null-safe: kembalikan "" bila null. */
    private String nz(String s){
        return s==null ? "" : s;
    }

    /** Helper: tambah satu entri classHistory (class v3-ActCode + period start/end) ke Encounter. */
    private void tambahClassHistory(ArrayNode classHistory, String code, String display, String start, String end){
        if (code==null||code.equals("")||start==null||start.equals("")||end==null||end.equals("")) {
            return;
        }
        ObjectNode item = classHistory.addObject();
        ObjectNode classObj = item.putObject("class");
        classObj.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
        classObj.put("code", code);
        if (display!=null && !display.equals("")) {
            classObj.put("display", display);
        }
        ObjectNode period = item.putObject("period");
        period.put("start", start);
        period.put("end", end);
    }

    /** Penampung data diagnosa dari tabel diagnosa_pasien. */
    private static class DiagnosaData {
        String kode="", nama="";
    }

    /** Query: ambil semua diagnosa (urut prioritas) untuk no_rawat+status; fallback semua status. */
    private List<DiagnosaData> ambilSemuaDiagnosa(String noRawat, String status){
        List<DiagnosaData> hasil = new ArrayList<>();
        String sql = "select diagnosa_pasien.kd_penyakit, ifnull(penyakit.nm_penyakit,'') as nm_penyakit "
                + "from diagnosa_pasien inner join penyakit on penyakit.kd_penyakit=diagnosa_pasien.kd_penyakit "
                + "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.status=? order by diagnosa_pasien.prioritas asc";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, noRawat);
            p.setString(2, status);
            ResultSet r = p.executeQuery();
            while(r.next()){
                DiagnosaData d = new DiagnosaData();
                d.kode = r.getString("kd_penyakit");
                d.nama = r.getString("nm_penyakit");
                hasil.add(d);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ambilSemuaDiagnosa : " + e);
        }
        if(hasil.isEmpty()){
            String sql2 = "select diagnosa_pasien.kd_penyakit, ifnull(penyakit.nm_penyakit,'') as nm_penyakit "
                    + "from diagnosa_pasien inner join penyakit on penyakit.kd_penyakit=diagnosa_pasien.kd_penyakit "
                    + "where diagnosa_pasien.no_rawat=? order by diagnosa_pasien.prioritas asc";
            try {
                PreparedStatement p = koneksi.prepareStatement(sql2);
                p.setString(1, noRawat);
                ResultSet r = p.executeQuery();
                while(r.next()){
                    DiagnosaData d = new DiagnosaData();
                    d.kode = r.getString("kd_penyakit");
                    d.nama = r.getString("nm_penyakit");
                    hasil.add(d);
                }
                r.close();
                p.close();
            } catch (Exception e) {
                System.out.println("Notifikasi ambilSemuaDiagnosa fallback : " + e);
            }
        }
        return hasil;
    }

    /** Builder Condition (diagnosa, ICD-10) — direferensi oleh Encounter.diagnosis. */
    private ObjectNode buatConditionDiagnosa(DiagnosaData d, String namaPasien, String namaDokter,
        String urnEncounter, String mulai){
        ObjectNode condition = mapper.createObjectNode();
        condition.put("resourceType", "Condition");

        ObjectNode clinicalCoding = condition.putObject("clinicalStatus").putArray("coding").addObject();
        clinicalCoding.put("system", "http://terminology.hl7.org/CodeSystem/condition-clinical");
        clinicalCoding.put("code", "active");
        clinicalCoding.put("display", "Active");

        ObjectNode verifyCoding = condition.putObject("verificationStatus").putArray("coding").addObject();
        verifyCoding.put("system", "http://terminology.hl7.org/CodeSystem/condition-ver-status");
        verifyCoding.put("code", "confirmed");
        verifyCoding.put("display", "Confirmed");

        ObjectNode categoryCoding = condition.putArray("category").addObject().putArray("coding").addObject();
        categoryCoding.put("system", "http://terminology.hl7.org/CodeSystem/condition-category");
        categoryCoding.put("code", "encounter-diagnosis");
        categoryCoding.put("display", "Encounter Diagnosis");

        ObjectNode codeCoding = condition.putObject("code").putArray("coding").addObject();
        codeCoding.put("system", "http://hl7.org/fhir/sid/icd-10");
        codeCoding.put("code", d.kode);
        codeCoding.put("display", d.nama);

        ObjectNode subject = condition.putObject("subject");
        subject.put("reference", "Patient/" + idpasien);
        subject.put("display", namaPasien);

        ObjectNode encounter = condition.putObject("encounter");
        encounter.put("reference", urnEncounter);
        encounter.put("display", "Diagnosa " + namaPasien);

        condition.put("recordedDate", mulai);
        condition.put("onsetDateTime", mulai);

        ObjectNode recorder = condition.putObject("recorder");
        recorder.put("reference", "Practitioner/" + iddokter);
        recorder.put("display", namaDokter);

        ObjectNode asserter = condition.putObject("asserter");
        asserter.put("reference", "Patient/" + idpasien);
        asserter.put("display", namaPasien);
        return condition;
    }

    /** Penampung data SPRI dari tabel surat_perintah_rawat_inap. */
    private static class SpriData {
        String noSurat="", tanggal="", kdDokter="", diagnosa="", catatan="", nmDokter="";
    }

    /** Query: ambil SPRI terakhir untuk no_rawat (atau null bila tidak ada). */
    private SpriData ambilSpri(String noRawat){
        SpriData d = null;
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select spri.no_surat, spri.tanggal, spri.kd_dokter, spri.diagnosa, spri.catatan, "
                    + "ifnull(dokter.nm_dokter,'') as nm_dokter "
                    + "from surat_perintah_rawat_inap spri "
                    + "left join dokter on dokter.kd_dokter=spri.kd_dokter "
                    + "where spri.no_rawat=? order by spri.tanggal desc, spri.no_surat desc limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if(r.next()){
                d = new SpriData();
                d.noSurat  = r.getString("no_surat");
                d.tanggal  = r.getString("tanggal");
                d.kdDokter = r.getString("kd_dokter");
                d.diagnosa = r.getString("diagnosa");
                d.catatan  = r.getString("catatan");
                d.nmDokter = r.getString("nm_dokter");
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi ambilSpri : " + e);
        }
        return d;
    }

    /** Resolve ID IHS Practitioner dari kd_dokter (lewat pegawai.no_ktp). "" bila gagal. */
    private String ambilIhsDokterByKodeDokter(String kdDokter){
        if(kdDokter==null || kdDokter.equals("")){
            return "";
        }
        try {
            String nik = Sequel.cariIsi(
                    "select ifnull(pegawai.no_ktp,'') from dokter "
                    + "left join pegawai on pegawai.nik=dokter.kd_dokter where dokter.kd_dokter=? limit 1",
                    kdDokter);
            if(nik==null || nik.equals("")){
                nik = kdDokter;
            }
            return cekViaSatuSehat.tampilIDParktisi(nik);
        } catch (Exception e) {
            System.out.println("Notifikasi ambilIhsDokterByKodeDokter : " + e);
            return "";
        }
    }

    /**
     * Builder ServiceRequest SPRI (Surat Perintah Rawat Inap) untuk pasien IGD->Rawat Inap.
     * - reasonCode  : indikasi rawat inap, diambil dari kolom diagnosa SPRI.
     * - orderDetail : narasi rencana tindakan, diambil dari "no_surat - catatan".
     * - encounter   : referensi silang ke Encounter di bundle yang sama (urn:uuid).
     */
    private ObjectNode buatSpriServiceRequest(SpriData spri, String urnEncounter, String namaPasien,
            String idRequester, String namaRequester, String idOrg,
            DiagnosaData diagnosaMasuk, String conditionRef, String occurrenceStart, String identifierValue){
        ObjectNode sr = mapper.createObjectNode();
        sr.put("resourceType", "ServiceRequest");

        ObjectNode iden = sr.putArray("identifier").addObject();
        iden.put("system", "http://sys-ids.kemkes.go.id/servicerequest/" + idOrg);
        iden.put("value", identifierValue);

        sr.put("status", "active");
        sr.put("intent", "order");

        ObjectNode catCoding = sr.putArray("category").addObject().putArray("coding").addObject();
        catCoding.put("system", "http://terminology.kemkes.go.id");
        catCoding.put("code", "inpatient-admission");
        catCoding.put("display", "Admisi Rawat Inap");

        // code SPRI (coding + text).
        ObjectNode code = sr.putObject("code");
        ObjectNode codeCoding = code.putArray("coding").addObject();
        codeCoding.put("system", "http://terminology.kemkes.go.id");
        codeCoding.put("code", "spri");
        codeCoding.put("display", "Surat Perintah Rawat Inap");
        code.put("text", "Surat Perintah Rawat Inap");

        // subject: pasien.
        ObjectNode subject = sr.putObject("subject");
        subject.put("reference", "Patient/" + idpasien);
        subject.put("display", namaPasien);

        // encounter.
        sr.putObject("encounter").put("reference", urnEncounter);

        // performer: perintah ditujukan ke faskes (Organization).
        String namaRs = akses.getnamars();
        ObjectNode performer = sr.putArray("performer").addObject();
        performer.put("reference", "Organization/" + idOrg);
        performer.put("display", (namaRs==null || namaRs.equals("")) ? "Rumah Sakit" : namaRs);

        // authoredOn dari tanggal SPRI.
        String authored = (spri.tanggal==null || spri.tanggal.equals("")) ? "" : spri.tanggal + "T00:00:00+07:00";
        if(!authored.equals("")){
            sr.put("authoredOn", authored);
        }

        // requester: dokter yang memerintahkan.
        ObjectNode requester = sr.putObject("requester");
        requester.put("reference", "Practitioner/" + idRequester);
        requester.put("display", namaRequester);

        // reasonCode: indikasi rawat inap. text = indikasi (kolom diagnosa SPRI),
        // coding = ICD-10 diagnosa masuk (diagnosa prioritas utama).
        ObjectNode reason = sr.putArray("reasonCode").addObject();
        if(diagnosaMasuk!=null && diagnosaMasuk.kode!=null && !diagnosaMasuk.kode.equals("")){
            ObjectNode rcCoding = reason.putArray("coding").addObject();
            rcCoding.put("system", "http://hl7.org/fhir/sid/icd-10");
            rcCoding.put("code", diagnosaMasuk.kode);
            rcCoding.put("display", diagnosaMasuk.nama);
        }
        String indikasi = (spri.diagnosa!=null && !spri.diagnosa.trim().equals(""))
                ? spri.diagnosa.trim()
                : (diagnosaMasuk!=null ? diagnosaMasuk.nama : "");
        reason.put("text", indikasi);

        // reasonReference: Condition diagnosa masuk.
        if(conditionRef!=null && !conditionRef.equals("")){
            sr.putArray("reasonReference").addObject().put("reference", conditionRef);
        }

        // orderDetail: rencana tindakan yang akan dilakukan ("no_surat - catatan").
        String catatan = (spri.catatan==null) ? "" : spri.catatan.trim();
        String narasi  = catatan.equals("") ? spri.noSurat : (spri.noSurat + " - " + catatan);
        sr.putArray("orderDetail").addObject().put("text", narasi);

        // occurrencePeriod: kapan rawat inap direncanakan/dimulai.
        if(occurrenceStart!=null && !occurrenceStart.equals("")){
            sr.putObject("occurrencePeriod").put("start", occurrenceStart);
        }
        return sr;
    }

    /** Helper: tambah entry ke Bundle dengan request method POST (create). */
    private void tambahEntry(ArrayNode entries, String fullUrl, ObjectNode resource, String resourceType){
        tambahEntry(entries, fullUrl, resource, resourceType, "", "");
    }

    /** Helper: existingId terisi -> PUT (update by id); kosong -> POST (create). */
    private void tambahEntry(ArrayNode entries, String fullUrl, ObjectNode resource, String resourceType,
            String existingId){
        tambahEntry(entries, fullUrl, resource, resourceType, existingId, "");
    }

    /**
     * Helper inti: tambah entry ke Bundle dengan kontrol method.
     * - existingId terisi -> PUT (update by id), id ditempel ke resource.
     * - existingId kosong -> POST. Bila ifNoneExist diisi, server no-op kalau resource
     *   dengan kriteria itu sudah ada (idempotent create by identifier).
     */
    private void tambahEntry(ArrayNode entries, String fullUrl, ObjectNode resource, String resourceType,
            String existingId, String ifNoneExist){
        ObjectNode entry = entries.addObject();
        entry.put("fullUrl", fullUrl);
        boolean adaId = existingId!=null && !existingId.equals("");
        if(adaId){
            resource.put("id", existingId);
        }
        entry.set("resource", resource);
        ObjectNode request = entry.putObject("request");
        if(adaId){
            request.put("method", "PUT");
            request.put("url", resourceType + "/" + existingId);
        }else{
            request.put("method", "POST");
            request.put("url", resourceType);
            if(ifNoneExist!=null && !ifNoneExist.equals("")){
                request.put("ifNoneExist", ifNoneExist);
            }
        }
    }

    /** Cari id_condition lama untuk satu diagnosa (atau "" bila belum ada). */
    private String cariIdCondition(String noRawat, String kdPenyakit, String status){
        String hasil = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(id_condition,'') from satu_sehat_condition "
                    + "where no_rawat=? and kd_penyakit=? and status=? limit 1");
            p.setString(1, noRawat);
            p.setString(2, kdPenyakit);
            p.setString(3, status);
            ResultSet r = p.executeQuery();
            if(r.next()){
                hasil = r.getString(1);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi cariIdCondition : " + e);
        }
        return hasil==null ? "" : hasil;
    }

    /** Simpan/update mapping id_condition lokal (upsert). */
    private void simpanCondition(String noRawat, String kdPenyakit, String status, String idCondition){
        if(kdPenyakit==null || kdPenyakit.equals("")){
            return;
        }
        if(!Sequel.menyimpantf2("satu_sehat_condition","?,?,?,?","Diagnosa",4,
                new String[]{noRawat, kdPenyakit, status, idCondition})){
            Sequel.queryu2("update satu_sehat_condition set id_condition=? where no_rawat=? and kd_penyakit=? and status=?",
                    4, new String[]{idCondition, noRawat, kdPenyakit, status});
        }
    }

    /**
     * Lookup id resource di server SATUSEHAT lewat identifier (GET ?identifier=system|value).
     * Dipakai untuk resource yang tidak punya tabel mapping lokal (mis. ServiceRequest SPRI),
     * agar re-send memakai PUT (update) dan tidak kena error "Found duplicate".
     */
    private String cariIdServerByIdentifier(String resourceType, String identifierSystem, String identifierValue){
        if(resourceType==null || resourceType.equals("") || identifierValue==null || identifierValue.equals("")){
            return "";
        }
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity req = new HttpEntity(h);
            String token = java.net.URLEncoder.encode(identifierSystem + "|" + identifierValue, "UTF-8");
            // Pakai URI object (bukan String) agar RestTemplate tidak meng-encode ulang '%'
            // (double-encoding) yang membuat pencarian identifier gagal -> selalu return kosong.
            java.net.URI uri = java.net.URI.create(link + "/" + resourceType + "?identifier=" + token + "&_count=1");
            String hasil = api.getRest().exchange(uri, HttpMethod.GET, req, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            if(r.path("total").asInt(0) > 0){
                JsonNode es = r.path("entry");
                if(es.isArray() && es.size() > 0){
                    String id = es.get(0).path("resource").path("id").asText();
                    return id==null ? "" : id;
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi cariIdServerByIdentifier : " + e);
        }
        return "";
    }

    /** Cari id ServiceRequest SPRI dari mapping lokal satu_sehat_spri (atau "" bila belum ada). */
    private String cariIdSpriLokal(String noSurat){
        String hasil = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(id_servicerequest,'') from satu_sehat_servicerequest_spri where no_surat=? limit 1");
            p.setString(1, noSurat);
            ResultSet r = p.executeQuery();
            if(r.next()){
                hasil = r.getString(1);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi cariIdSpriLokal : " + e);
        }
        return hasil==null ? "" : hasil;
    }

    /** Upsert id ServiceRequest SPRI ke satu_sehat_spri (key no_surat). */
    private void simpanSpri(String noSurat, String noRawat, String idServiceRequest){
        if(noSurat==null || noSurat.equals("") || idServiceRequest==null || idServiceRequest.equals("")){
            return;
        }
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "insert into satu_sehat_servicerequest_spri (no_surat, no_rawat, id_servicerequest) values (?,?,?) "
                    + "on duplicate key update no_rawat=values(no_rawat), id_servicerequest=values(id_servicerequest)");
            p.setString(1, noSurat);
            p.setString(2, noRawat);
            p.setString(3, idServiceRequest);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi simpanSpri : " + e);
        }
    }

    /** Normalisasi identifier lokal: buang semua karakter non-alfanumerik (mis. "/"). */
    private String normalisasiIdentifier(String data){
        if(data==null){
            return "";
        }
        return data.replaceAll("[^A-Za-z0-9]", "");
    }

    /** Cari id EpisodeOfCare lama (kolom id_encounter di satu_sehat_episodeofcare) atau "". */
    private String cariIdEpisode(String noRawat){
        String hasil = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(id_encounter,'') from satu_sehat_episodeofcare where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if(r.next()){
                hasil = r.getString(1);
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi cariIdEpisode : " + e);
        }
        return hasil==null ? "" : hasil;
    }

    /** Ambil id resource dari nilai response.location (mis. "Encounter/{id}/_history/1"). */
    private String extractIdResource(String location){
        if(location==null || location.equals("")){
            return "";
        }
        String loc = location;
        int hist = loc.indexOf("/_history");
        if(hist>=0){
            loc = loc.substring(0, hist);
        }
        int slash = loc.lastIndexOf("/");
        return slash>=0 ? loc.substring(slash+1) : loc;
    }

    private void ppPilihSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPilihSemuaActionPerformed
        for(i=0;i<tbObat.getRowCount();i++){
            tbObat.setValueAt(true,i,0);
        }
    }//GEN-LAST:event_ppPilihSemuaActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        for(i=0;i<tbObat.getRowCount();i++){
            tbObat.setValueAt(false,i,0);
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUpdateActionPerformed
        // Update = kirim Bundle (transaction) yang sama seperti tombol Kirim, tetapi Encounter
        // (dan Condition/EpisodeOfCare yang sudah punya id) memakai method PUT, bukan POST.
        if (sedangSibuk("UPDATE")) return;
        runBackground(() -> prosesBatch(true));
    }//GEN-LAST:event_BtnUpdateActionPerformed

    /**
     * runBackground() menolak diam-diam bila masih ada pekerjaan lain (mis. tampil ulang tabel
     * atau batch sebelumnya). Tanpa pemberitahuan, tombol terasa "tidak berfungsi", jadi
     * penolakannya dibuat kelihatan.
     */
    private boolean sedangSibuk(String mode) {
        if (!ceksukses) {
            return false;
        }
        logWarn("=== " + mode + " ditunda : masih ada proses yang berjalan, tunggu sampai selesai ===");
        javax.swing.JOptionPane.showMessageDialog(this,
                "Masih ada proses yang berjalan.\nTunggu sampai bar progres di bawah selesai.");
        return true;
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            runBackground(() -> tampil());
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void ChkBelumTerkirimItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChkBelumTerkirimItemStateChanged
        runBackground(() -> tampil());
    }//GEN-LAST:event_ChkBelumTerkirimItemStateChanged

    private void ChkBelumTerkirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkBelumTerkirimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkBelumTerkirimActionPerformed

    private void cmbJenisRawatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbJenisRawatActionPerformed
        String s = String.valueOf(cmbJenisRawat.getSelectedItem());
        filterJenisRawat = s.equals("Rawat Inap") ? "RANAP" : (s.equals("Rawat Jalan") ? "RALAN" : "SEMUA");
        runBackground(() -> tampil());
    }//GEN-LAST:event_cmbJenisRawatActionPerformed

    private void BtnTteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTteActionPerformed
        bukaTte();
    }//GEN-LAST:event_BtnTteActionPerformed

    private void BtnSemuaResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSemuaResourceActionPerformed
        tampilkanSemua();
    }//GEN-LAST:event_BtnSemuaResourceActionPerformed

    private void BtnRefreshPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefreshPreviewActionPerformed
        perbaruiPreviewDariSeleksi();
    }//GEN-LAST:event_BtnRefreshPreviewActionPerformed

    private void BtnUpdateBundleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUpdateBundleActionPerformed
        BtnUpdateActionPerformed(null);
    }//GEN-LAST:event_BtnUpdateBundleActionPerformed

    private void BtnKirimBundleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKirimBundleActionPerformed
        BtnKirimActionPerformed(null);
    }//GEN-LAST:event_BtnKirimBundleActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatBundle dialog = new SatuSehatBundle(new javax.swing.JFrame(), true);
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
    private widget.Button BtnKirim;
    private javax.swing.JButton BtnKirimBundle;
    private widget.Button BtnPrint;
    private javax.swing.JButton BtnRefreshPreview;
    private javax.swing.JButton BtnResAlkes;
    private javax.swing.JButton BtnResAnestesi;
    private javax.swing.JButton BtnResBilling;
    private javax.swing.JButton BtnResCondition;
    private javax.swing.JButton BtnResDiet;
    private javax.swing.JButton BtnResEcho;
    private javax.swing.JButton BtnResEkg;
    private javax.swing.JButton BtnResEncounter;
    private javax.swing.JButton BtnResEpisodeOfCare;
    private javax.swing.JButton BtnResEswl;
    private javax.swing.JButton BtnResFamilyHistory;
    private javax.swing.JButton BtnResGoal;
    private javax.swing.JButton BtnResLab;
    private javax.swing.JButton BtnResObat;
    private javax.swing.JButton BtnResObatKronis;
    private javax.swing.JButton BtnResOperasi;
    private javax.swing.JButton BtnResPersalinan;
    private javax.swing.JButton BtnResPreAnestesi;
    private javax.swing.JButton BtnResPrognosa;
    private javax.swing.JButton BtnResRadiologi;
    private javax.swing.JButton BtnResRencanaKontrol;
    private javax.swing.JButton BtnResResumeRajal;
    private javax.swing.JButton BtnResResumeRanap;
    private javax.swing.JButton BtnResRiskAssessment;
    private javax.swing.JButton BtnResSkriningTbc;
    private javax.swing.JButton BtnResSpri;
    private javax.swing.JButton BtnResTelaahFarmasi;
    private javax.swing.JButton BtnResTriaseIgd;
    private javax.swing.JButton BtnResUsg;
    private javax.swing.JButton BtnSemuaResource;
    private javax.swing.JButton BtnTte;
    private widget.Button BtnUpdate;
    private javax.swing.JButton BtnUpdateBundle;
    private widget.CekBox ChkBelumTerkirim;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private javax.swing.JProgressBar barProgres;
    private javax.swing.JComboBox<String> cmbJenisRawat;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JLabel lblJenisRawat;
    private javax.swing.JLabel lblJudulPreview;
    private javax.swing.JLabel lblProgresPasien;
    private javax.swing.JPanel panelAksiPreview;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JPanel panelHeaderPreview;
    private javax.swing.JPanel panelIsiPreview;
    private javax.swing.JPanel panelKartu;
    private javax.swing.JPanel panelLog;
    private javax.swing.JPanel panelPreview;
    private javax.swing.JPanel panelProgres;
    private javax.swing.JPanel panelRail;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppPilihSemua;
    private javax.swing.JPanel railWrap;
    private javax.swing.JScrollPane scrollKartu;
    private javax.swing.JScrollPane scrollLog;
    private javax.swing.JScrollPane scrollRail;
    private javax.swing.JSplitPane splitTengah;
    private javax.swing.JSplitPane splitUtama;
    private javax.swing.JTextArea taLog;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
    private void tampil() {
        Valid.tabelKosong(tabMode);
        String belumterkirim = "";
        if (ChkBelumTerkirim.isSelected() == true) {
            belumterkirim = " satu_sehat_encounter.id_encounter IS NULL and ";
        } else {
            belumterkirim = "";
        }
        try {
          if(!filterJenisRawat.equals("RANAP")){
            ps = koneksi.prepareStatement(
                   "select reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,reg_periksa.kd_dokter,"
                   + "pegawai.nama,pegawai.no_ktp as ktpdokter,reg_periksa.kd_poli,poliklinik.nm_poli,satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,reg_periksa.stts,"
                   + "reg_periksa.status_lanjut,concat(nota_jalan.tanggal,'T',nota_jalan.jam,'+07:00') as pulang,ifnull(satu_sehat_encounter.id_encounter,'') as id_encounter,"
                   + "ifnull((select bs.no_sep from bridging_sep bs where bs.no_rawat=reg_periksa.no_rawat limit 1),'') as no_sep,"
                   + "ifnull((select group_concat(distinct concat(dp.kd_penyakit,' - ',ifnull(pny.nm_penyakit,'')) order by dp.prioritas separator '; ') from diagnosa_pasien dp left join penyakit pny on pny.kd_penyakit=dp.kd_penyakit where dp.no_rawat=reg_periksa.no_rawat and dp.status='Ralan'),'') as diagnosa "
                   + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join pegawai on pegawai.nik=reg_periksa.kd_dokter "
                   + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli inner join satu_sehat_mapping_lokasi_ralan on satu_sehat_mapping_lokasi_ralan.kd_poli=poliklinik.kd_poli "
                   + "inner join nota_jalan on nota_jalan.no_rawat=reg_periksa.no_rawat left join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "
                   + "where " + belumterkirim + " nota_jalan.tanggal between ? and ? "
                   + (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                   + "pasien.nm_pasien like ? or pasien.no_ktp like ? or pegawai.nama like ? or poliklinik.nm_poli like ? or "
                   + "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)"));
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    ps.setString(9,"%"+TCari.getText()+"%");
                    ps.setString(10,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("tgl_registrasi")+"T"+rs.getString("jam_reg")+"+07:00",rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),rs.getString("kd_dokter"),rs.getString("nama"),rs.getString("ktpdokter"),rs.getString("kd_poli"),rs.getString("nm_poli"),
                        rs.getString("id_lokasi_satusehat"),rs.getString("stts"),rs.getString("status_lanjut"),rs.getString("pulang"),rs.getString("id_encounter"),
                        rs.getString("no_sep"),rs.getString("diagnosa")
                    });
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
            
          }
          if(!filterJenisRawat.equals("RALAN")){
            ps=koneksi.prepareStatement(
                   "select reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,reg_periksa.kd_dokter,"
                   + "pegawai.nama,pegawai.no_ktp as ktpdokter,reg_periksa.kd_poli,poliklinik.nm_poli,satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,reg_periksa.stts,"
                   + "reg_periksa.status_lanjut,concat(nota_inap.tanggal,'T',nota_inap.jam,'+07:00') as pulang,ifnull(satu_sehat_encounter.id_encounter,'') as id_encounter,"
                   + "ifnull((select bs.no_sep from bridging_sep bs where bs.no_rawat=reg_periksa.no_rawat limit 1),'') as no_sep,"
                   + "ifnull((select group_concat(distinct concat(dp.kd_penyakit,' - ',ifnull(pny.nm_penyakit,'')) order by dp.prioritas separator '; ') from diagnosa_pasien dp left join penyakit pny on pny.kd_penyakit=dp.kd_penyakit where dp.no_rawat=reg_periksa.no_rawat and dp.status='Ranap'),'') as diagnosa "
                   + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join pegawai on pegawai.nik=reg_periksa.kd_dokter "
                   + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli inner join satu_sehat_mapping_lokasi_ralan on satu_sehat_mapping_lokasi_ralan.kd_poli=poliklinik.kd_poli "
                   + "inner join nota_inap on nota_inap.no_rawat=reg_periksa.no_rawat left join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "
                   + "where " + belumterkirim + " nota_inap.tanggal between ? and ? "
                   + (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                   + "pasien.nm_pasien like ? or pasien.no_ktp like ? or pegawai.nama like ? or poliklinik.nm_poli like ? or "
                   + "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)"));
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    ps.setString(9,"%"+TCari.getText()+"%");
                    ps.setString(10,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("tgl_registrasi")+"T"+rs.getString("jam_reg")+"+07:00",rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),rs.getString("kd_dokter"),rs.getString("nama"),rs.getString("ktpdokter"),rs.getString("kd_poli"),rs.getString("nm_poli"),
                        rs.getString("id_lokasi_satusehat"),rs.getString("stts"),rs.getString("status_lanjut"),rs.getString("pulang"),rs.getString("id_encounter"),
                        rs.getString("no_sep"),rs.getString("diagnosa")
                    });
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
          }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnUpdate.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }
    
    public JTable getTable(){
        return tbObat;
    }

    // ============================================================================
    //  FITUR PREVIEW KLAIM — panel split kanan (build lokal tanpa kirim).
    //  Kiri: daftar pasien. Klik toggle "<" -> layar terbelah, kanan menampilkan
    //  tombol per-resource, PREVIEW KLAIM (kartu Swing), dan LOG PENGIRIMAN.
    // ============================================================================

    /** Daftar tetap jenis resource (label tombol) sesuai urutan pengiriman bundle. */
    private static final String[] JENIS_RESOURCE = {
        "Encounter", "Condition (Diagnosa)", "ServiceRequest SPRI", "EpisodeOfCare",
        "Triase IGD", "Resume Medis Ranap", "Resume Medis Rajal", "Laporan Persalinan",
        "Family History", "Laporan Operasi", "Laporan Anestesi", "Pre-Anestesi",
        "Skrining TBC", "Obat", "Lab", "Radiologi", "Laporan Echo", "Laporan ESWL",
        "Laporan USG", "Laporan EKG", "Diet", "Prognosa", "Telaah Farmasi", "Goal",
        "Risk Assessment", "Alkes", "Billing", "Obat Kronis", "Rencana Kontrol"
    };

    /**
     * Lengkapi panel preview yang sudah dibangun Form Designer (lihat SatuSehatBundle.form):
     * isi rail dengan tombol per-resource, atur lebar split, lalu pasang pendengar seleksi tabel.
     * Dipanggil sekali dari konstruktor.
     */
    private void siapkanPreview() {
        // Rail tombol resource: tombolnya SUDAH ada dari Form Designer (BtnRes*), di sini
        // tinggal dipasangi aksi. Pemetaan label->tombol dipakai kalau nanti perlu menandai
        // status per resource. Label tombol WAJIB sama persis dengan entri JENIS_RESOURCE
        // karena bangunResourcePreview() men-dispatch berdasarkan label itu.
        for (java.awt.Component c : panelRail.getComponents()) {
            if (c == BtnSemuaResource || !(c instanceof JButton)) {
                continue;
            }
            JButton b = (JButton) c;
            final String jenis = b.getText();
            tombolResource.put(jenis, b);
            b.addActionListener(e -> tampilkanPreview(jenis));
        }
        for (String jenis : JENIS_RESOURCE) {
            if (!tombolResource.containsKey(jenis)) {
                logWarn("[PREVIEW] Tombol resource \"" + jenis + "\" belum ada di SatuSehatBundle.form");
            }
        }
        scrollRail.getVerticalScrollBar().setUnitIncrement(16);
        scrollKartu.getVerticalScrollBar().setUnitIncrement(16);
        barProgres.setMaximum(TOTAL_LANGKAH);   // jaga sinkron bila TOTAL_LANGKAH berubah

        // Panel preview tampil PERMANEN (tanpa tombol toggle): window melebar & panel kanan selalu ada.
        // Panel kiri dijaga cukup lebar (min 840px) agar baris filter — termasuk checkbox
        // "Data belum terkirim" (item terakhir panelGlass9) — TIDAK terpotong divider.
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int wWin = Math.min(1320, scr.width - 20);
        setSize(wWin, 674);
        setLocation(6, 2);
        splitUtama.setDividerLocation(Math.max(840, wWin - 450));
        // Info pasien di panel ikut ter-update otomatis saat baris dipilih di tabel kiri.
        tbObat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) perbaruiPreviewDariSeleksi();
        });
        perbaruiPreviewDariSeleksi();
    }

    /**
     * Perbarui bar langkah pada form ini.
     *
     * @param teks    keterangan langkah yang sedang dikerjakan
     * @param langkah nomor langkah 1..TOTAL_LANGKAH; pakai -1 bila lamanya tidak bisa diukur
     *                (mis. satu POST Bundle besar) sehingga bar dibuat bergerak terus.
     */
    private void tampilProgres(String teks, int langkah) {
        if (barProgres == null) {
            return;   // dipakai headless dari INACBGData: panel preview memang tidak dibangun
        }
        SwingUtilities.invokeLater(() -> {
            if (langkah < 0) {
                barProgres.setIndeterminate(true);
                barProgres.setString(teks);
            } else {
                barProgres.setIndeterminate(false);
                barProgres.setValue(Math.min(langkah, TOTAL_LANGKAH));
                barProgres.setString("(" + langkah + "/" + TOTAL_LANGKAH + ") " + teks);
            }
        });
    }

    /** Tulis identitas pasien yang sedang diproses di atas bar langkah. */
    private void tampilProgresPasien(String teks) {
        if (lblProgresPasien == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> lblProgresPasien.setText(teks));
    }

    /** Kembalikan bar ke keadaan diam setelah satu batch pengiriman selesai. */
    private void selesaiProgres(String ringkasan) {
        tampilProgresPasien(ringkasan);
        if (barProgres == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            barProgres.setIndeterminate(false);
            barProgres.setValue(0);
            barProgres.setString("Selesai");
        });
    }

    /** Muat ulang preview berdasarkan baris yang sedang terpilih di tabel kiri. */
    private void perbaruiPreviewDariSeleksi() {
        int row = tbObat.getSelectedRow();
        barisPreview = row;
        if (row < 0) {
            tampilkanPesanKartu("Pilih satu baris pasien di tabel kiri untuk menampilkan preview klaim.");
            return;
        }
        // Default: langsung render SEMUA resource pasien terpilih (rapi, seperti Dokumen Klaim).
        tampilkanSemua();
    }

    /** Bangun resource terpilih (lokal, tanpa kirim) lalu render sebagai kartu. */
    private void tampilkanPreview(String jenis) {
        int row = tbObat.getSelectedRow();
        if (row < 0) {
            tampilkanPesanKartu("Pilih dulu satu baris pasien di tabel kiri.");
            return;
        }
        barisPreview = row;
        panelKartu.removeAll();
        try {
            KonteksPreview k = kumpulkanKonteksPreview(row);
            JsonNode resource = bangunResourcePreview(jenis, k);
            if (resource == null) {
                tampilkanPesanKartu("Tidak ada data \"" + jenis + "\" untuk kunjungan ini "
                        + "(belum terisi / tidak berlaku untuk jenis kunjungan ini).");
            } else {
                JPanel kartu = PreviewKartu.render(jenis, statusTtePreview(jenis, k), resource);
                kartu.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                panelKartu.add(kartu);
                panelKartu.revalidate();
                panelKartu.repaint();
                scrollKartu.getVerticalScrollBar().setValue(0);
            }
        } catch (Exception e) {
            tampilkanPesanKartu("Gagal membangun preview \"" + jenis + "\" : " + e);
            logError("[PREVIEW] " + jenis + " : " + e);
        }
    }

    /** Buka dialog TTE (penandatanganan elektronik) ter-filter ke no_rawat pasien terpilih. */
    private void bukaTte() {
        java.awt.Frame owner = (getOwner() instanceof java.awt.Frame) ? (java.awt.Frame) getOwner() : null;
        DlgTTESatuSehat dlg = new DlgTTESatuSehat(owner, false);
        int row = tbObat.getSelectedRow();
        if (row >= 0) {
            try {
                dlg.praFilter(tbObat.getValueAt(row, 2).toString());   // no_rawat
            } catch (Exception e) {
                logError("[TTE] praFilter : " + e);
            }
        }
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /** Bangun & tampilkan SEMUA resource pasien terpilih, bertumpuk (seperti Dokumen Klaim Elektronik). */
    private void tampilkanSemua() {
        final int row = tbObat.getSelectedRow();
        if (row < 0) {
            tampilkanPesanKartu("Pilih dulu satu baris pasien di tabel kiri.");
            return;
        }
        barisPreview = row;
        tampilkanPesanKartu("Memuat semua resource untuk pasien ini... (mohon tunggu)");
        runBackground(() -> {
            final java.util.List<Object[]> hasil = new java.util.ArrayList<>();   // {jenis, JsonNode}
            try {
                KonteksPreview k = kumpulkanKonteksPreview(row);
                for (String jenis : JENIS_RESOURCE) {
                    try {
                        JsonNode res = bangunResourcePreview(jenis, k);
                        if (res != null) hasil.add(new Object[]{jenis, res});
                    } catch (Exception e) {
                        logError("[PREVIEW] " + jenis + " : " + e);
                    }
                }
            } catch (Exception e) {
                logError("[PREVIEW] Tampilkan Semua : " + e);
            }
            SwingUtilities.invokeLater(() -> {
                panelKartu.removeAll();
                if (hasil.isEmpty()) {
                    tampilkanPesanKartu("Tidak ada resource yang bisa dibangun untuk pasien ini.");
                    return;
                }
                for (Object[] h : hasil) {
                    JPanel kartu = PreviewKartu.render((String) h[0], "Belum TTE", (JsonNode) h[1]);
                    kartu.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                    panelKartu.add(kartu);
                    panelKartu.add(javax.swing.Box.createVerticalStrut(12));
                }
                panelKartu.revalidate();
                panelKartu.repaint();
                scrollKartu.getVerticalScrollBar().setValue(0);
            });
        });
    }

    /** Tampilkan teks bebas (info/pesan) di area kartu preview. */
    private void tampilkanPesanKartu(String pesan) {
        panelKartu.removeAll();
        JTextArea t = new JTextArea(pesan);
        t.setEditable(false);
        t.setOpaque(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setFont(new Font("Tahoma", Font.PLAIN, 12));
        t.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panelKartu.add(t);
        panelKartu.revalidate();
        panelKartu.repaint();
    }

    /** Dispatch pembangun resource preview per label tombol. */
    private JsonNode bangunResourcePreview(String jenis, KonteksPreview k) throws Exception {
        String enc = k.idEncounterPreview;
        switch (jenis) {
            case "Encounter":            return previewEncounter(k);
            case "Condition (Diagnosa)": return previewCondition(k);
            case "ServiceRequest SPRI":  return previewSpri(k);
            case "EpisodeOfCare":        return previewEpisode(k);
            case "Triase IGD":           return triaseIGD.bangun(k.noRawat, enc);
            case "Resume Medis Ranap":   return resumeRanap.bangun(k.noRawat, enc);
            case "Resume Medis Rajal":   return resumeRajal.bangun(k.noRawat, enc);
            case "Laporan Persalinan":   return laporanPersalinan.bangun(k.noRawat, enc);
            case "Family History":       return familyHistory.bangun(k.noRawat, enc);
            case "Laporan Operasi":      return laporanOperasi.bangun(k.noRawat, enc);
            case "Laporan Anestesi":     return laporanAnestesi.bangun(k.noRawat, enc);
            case "Pre-Anestesi":         return preAnestesi.bangun(k.noRawat, enc);
            case "Skrining TBC":         return skriningTBC.bangun(k.noRawat, enc);
            case "Obat":                 return obat.bangun(k.noRawat, enc);
            case "Lab":                  return lab.bangun(k.noRawat, enc);
            case "Radiologi":            return radiologi.bangun(k.noRawat, enc);
            case "Laporan Echo":         return laporanEcho.bangun(k.noRawat, enc);
            case "Laporan ESWL":         return laporanEswl.bangun(k.noRawat, enc);
            case "Laporan USG":          return laporanUsg.bangun(k.noRawat, enc);
            case "Laporan EKG":          return laporanEkg.bangun(k.noRawat, enc);
            case "Diet":                 return diet.bangun(k.noRawat, enc);
            case "Prognosa":             return prognosa.bangun(k.noRawat, enc);
            case "Telaah Farmasi":       return telaahFarmasi.bangun(k.noRawat, enc);
            case "Goal":                 return goal.bangun(k.noRawat, enc);
            case "Risk Assessment":      return riskAssessment.bangun(k.noRawat, enc);
            case "Alkes":                return alkes.bangun(k.noRawat, enc);
            case "Billing":              return billing.bangun(k.noRawat, enc);
            case "Obat Kronis":          return obatKronis.bangun(k.noRawat, enc);
            case "Rencana Kontrol":      return rencanaKontrol.bangun(k.noRawat, enc);
            default:                     return null;
        }
    }

    /** Status TTE untuk badge; preview belum ditandatangani -> "Belum TTE" (sama seperti viewer). */
    private String statusTtePreview(String jenis, KonteksPreview k) {
        return "Belum TTE";
    }

    /** Konteks satu baris pasien untuk preview — mirror data-gathering kirimBundle, TANPA POST. */
    private static class KonteksPreview {
        String noRawat = "", namaPasien = "", namaDokter = "", mulai = "", pulang = "",
               statusLanjut = "", idLokasi = "", kodePoli = "", namaPoli = "", idOrg = "", idEncounter = "";
        boolean anc = false, igd = false, ranap = false;
        List<DiagnosaData> daftarDiagnosa = new ArrayList<>();
        List<String> conditionRefs = new ArrayList<>();
        String encounterFullUrl = "";
        String idEncounterPreview = "";   // id encounter asli, atau placeholder bila belum dikirim
    }

    /** Kumpulkan data satu baris untuk preview (memakai ulang helper & builder kirimBundle). */
    private KonteksPreview kumpulkanKonteksPreview(int row) {
        KonteksPreview k = new KonteksPreview();
        iddokter = cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(row, 8).toString());
        idpasien = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(row, 5).toString());
        k.noRawat      = tbObat.getValueAt(row, 2).toString();
        k.namaPasien   = tbObat.getValueAt(row, 4).toString();
        k.namaDokter   = tbObat.getValueAt(row, 7).toString();
        k.mulai        = tbObat.getValueAt(row, 1).toString();
        k.pulang       = tbObat.getValueAt(row, 14).toString();
        k.statusLanjut = tbObat.getValueAt(row, 13).toString();
        k.idLokasi     = tbObat.getValueAt(row, 11).toString();
        k.kodePoli     = tbObat.getValueAt(row, 9).toString();
        k.namaPoli     = tbObat.getValueAt(row, 10).toString();
        k.idEncounter  = tbObat.getValueAt(row, 15).toString();
        try { k.idOrg = koneksiDB.IDSATUSEHAT(); } catch (Exception e) { k.idOrg = ""; }
        k.anc   = k.namaPoli.toLowerCase().contains("anc");
        k.igd   = isPoliIgd(k.kodePoli, k.namaPoli);
        k.ranap = k.statusLanjut.equals("Ranap");
        k.daftarDiagnosa = ambilSemuaDiagnosa(k.noRawat, k.statusLanjut);
        for (DiagnosaData d : k.daftarDiagnosa) {
            String idc = cariIdCondition(k.noRawat, d.kode, k.statusLanjut);
            k.conditionRefs.add((idc == null || idc.equals(""))
                    ? "urn:uuid:" + UUID.randomUUID().toString()
                    : "Condition/" + idc);
        }
        boolean update = k.idEncounter != null && !k.idEncounter.equals("");
        k.encounterFullUrl = update ? "Encounter/" + k.idEncounter : "urn:uuid:" + UUID.randomUUID().toString();
        // Sub-sender butuh id Encounter non-kosong. Bila kunjungan belum dikirim, pakai placeholder
        // agar preview tetap bisa dibangun (referensi Encounter/<placeholder> tak ditampilkan di kartu).
        k.idEncounterPreview = update ? k.idEncounter : "PREVIEW-BELUM-KIRIM";
        return k;
    }

    private JsonNode previewEncounter(KonteksPreview k) {
        return buatEncounter(k.noRawat, k.mulai, k.pulang, k.namaPasien, k.namaDokter, k.statusLanjut,
                k.idLokasi, k.kodePoli, k.namaPoli, k.idOrg, k.anc, "",
                k.daftarDiagnosa, k.conditionRefs);
    }

    private JsonNode previewCondition(KonteksPreview k) {
        if (k.daftarDiagnosa.isEmpty()) return null;
        ObjectNode bundle = mapper.createObjectNode();
        bundle.put("resourceType", "Bundle");
        ArrayNode entry = bundle.putArray("entry");
        for (int idx = 0; idx < k.daftarDiagnosa.size(); idx++) {
            ObjectNode c = buatConditionDiagnosa(k.daftarDiagnosa.get(idx), k.namaPasien, k.namaDokter,
                    k.encounterFullUrl, k.mulai);
            entry.addObject().set("resource", c);
        }
        return bundle;
    }

    private JsonNode previewSpri(KonteksPreview k) {
        SpriData spri = ambilSpri(k.noRawat);
        if (spri == null || spri.noSurat == null || spri.noSurat.equals("")) return null;
        String idRequester = ambilIhsDokterByKodeDokter(spri.kdDokter);
        if (idRequester == null || idRequester.equals("")) idRequester = iddokter;
        String namaRequester = (spri.nmDokter == null || spri.nmDokter.equals("")) ? k.namaDokter : spri.nmDokter;
        String spriValue = normalisasiIdentifier(spri.noSurat);
        DiagnosaData d0  = k.daftarDiagnosa.isEmpty() ? null : k.daftarDiagnosa.get(0);
        String condRef0  = k.conditionRefs.isEmpty() ? ("urn:uuid:" + UUID.randomUUID().toString()) : k.conditionRefs.get(0);
        return buatSpriServiceRequest(spri, k.encounterFullUrl, k.namaPasien, idRequester, namaRequester,
                k.idOrg, d0, condRef0, k.mulai, spriValue);
    }

    private JsonNode previewEpisode(KonteksPreview k) {
        if (!k.anc) return null;   // EpisodeOfCare (ANC) hanya untuk kunjungan ANC
        return buatEpisodeOfCare(k.noRawat, k.mulai, k.namaPasien, k.idOrg);
    }

    private String nzTampil(Object o) {
        String s = (o == null) ? "" : o.toString();
        return s.equals("") ? "(belum dikirim)" : s;
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

    // ====================== LOG SATUSEHAT BUNDLE ======================
    // Format konsisten: "yyyy-MM-dd HH:mm:ss  SATUSEHAT-BUNDLE  LEVEL  pesan"
    // supaya log mudah dibaca, di-grep, dan terlihat profesional di konsol.
    private static final java.time.format.DateTimeFormatter LOG_TS =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void log(String level, String pesan) {
        String baris = java.time.LocalDateTime.now().format(LOG_TS)
                + "  SATUSEHAT-BUNDLE  " + String.format("%-5s", level) + "  " + pesan;
        System.out.println(baris);
        if (taLog != null) {
            SwingUtilities.invokeLater(() -> {
                taLog.append(baris + "\n");
                taLog.setCaretPosition(taLog.getDocument().getLength());
            });
        }
        ProgresListener l = progresListener;
        if (l != null) {
            try {
                l.onProgres(level, pesan);
            } catch (Exception e) {
                System.out.println("Notifikasi progres listener : " + e);
            }
        }
    }

    /**
     * Penanda langkah yang sedang dikerjakan, dipakai untuk mengisi panel progres
     * pemanggil (mis. tombol Kirim/Update Satu Sehat Klaim di INACBGData) supaya
     * kelihatan resource apa yang sedang dikirim.
     */
    private void logProgres(String namaLangkah) {
        langkahKe++;
        // Beritahu pencatat HTTP (SatuSehatHttpLogger) siapa yang sedang mengirim, supaya
        // tiap baris di satu_sehat_log punya service_name & no_rawat yang jelas.
        SatuSehatHttpLogger.Konteks.set(namaLangkah, noRawatBerjalan);
        tampilProgres("Mengirim " + namaLangkah + " ...", langkahKe);
        log("STEP", "(" + langkahKe + "/" + TOTAL_LANGKAH + ") Mengirim " + namaLangkah + " ...");
    }

    /**
     * Langkah yang lamanya tidak bisa dipecah — dipakai untuk POST Bundle utama, yang satu kali
     * jalan mengirim Encounter + semua Condition (+EpisodeOfCare) sekaligus. Bar dibuat bergerak
     * terus ("indeterminate") karena sampai balasan server datang tidak ada kemajuan yang bisa
     * diukur; nomor langkah sengaja tidak ditambah supaya hitungan x/28 tetap konsisten.
     */
    private void logProgresTanpaUkuran(String namaLangkah, String keterangan) {
        SatuSehatHttpLogger.Konteks.set(namaLangkah, noRawatBerjalan);
        tampilProgres(keterangan, -1);
        log("STEP", keterangan);
    }
    private void logInfo(String pesan)   { log("INFO", pesan); }
    private void logSukses(String pesan) { log("OK", pesan); }
    private void logWarn(String pesan)   { log("WARN", pesan); }
    private void logError(String pesan)  { log("ERROR", pesan); }

    /**
     * Simpan satu baris audit pengiriman ke tabel satu_sehat_log (skema terstruktur).
     * Dipakai untuk pelacakan di production tanpa perlu akses konsol.
     */
    private void simpanLog(String serviceName, String noRawat, String resourceId, String requestPayload,
                           Integer responseCode, String responseBody, String status,
                           String errorMessage, Integer durationMs) {
        PreparedStatement psLog = null;
        try {
            psLog = koneksi.prepareStatement(
                    "insert into satu_sehat_log "
                    + "(service_name,no_rawat,resource_id,request_payload,response_code,response_body,status,error_message,duration_ms) "
                    + "values (?,?,?,?,?,?,?,?,?)");
            psLog.setString(1, serviceName);
            psLog.setString(2, noRawat);
            psLog.setString(3, resourceId);
            psLog.setString(4, requestPayload);
            if (responseCode == null) psLog.setNull(5, java.sql.Types.INTEGER); else psLog.setInt(5, responseCode);
            psLog.setString(6, responseBody);
            psLog.setString(7, status);
            psLog.setString(8, errorMessage);
            if (durationMs == null) psLog.setNull(9, java.sql.Types.INTEGER); else psLog.setInt(9, durationMs);
            psLog.executeUpdate();
        } catch (Exception e) {
            // Kegagalan menyimpan log tidak boleh mengganggu alur pengiriman bundle
            System.out.println("Notifikasi simpanLog : " + e);
        } finally {
            try { if (psLog != null) psLog.close(); } catch (Exception ex) {}
        }
    }

    /** Pretty-print JSON agar mudah dibaca di log; bila gagal parse, kembalikan apa adanya. */
    private String formatJson(String json) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(json));
        } catch (Exception e) {
            return json;
        }
    }
}
