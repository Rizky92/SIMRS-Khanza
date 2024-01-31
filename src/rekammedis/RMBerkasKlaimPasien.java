/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgLhtBiaya.java
 *
 * Created on 12 Jul 10, 16:21:34
 */

package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import simrskhanza.DlgCariPasien;

/**
 *
 * @author windiarto
 */
public final class RMBerkasKlaimPasien extends javax.swing.JDialog {    
    private validasi Valid=new validasi();    
    private final sekuel Sequel=new sekuel();
    private final DefaultTableModel tabModeRegistrasi;
    private PreparedStatement ps,ps2;
    private ResultSet rs,rs2,rs3,rs4;
    private Connection koneksi=koneksiDB.condb();
    private int i=0,urut=0,w=0,s=0,urutdpjp=0;
    private double biayaperawatan=0;
    private String kddpjp="",dpjp="",dokterrujukan="",polirujukan="",keputusan="",ke1="",ke2="",ke3="",ke4="",ke5="",ke6="",file="", TAMPILANDEFAULTRIWAYATPASIEN=koneksiDB.TAMPILANDEFAULTRIWAYATPASIEN();
    private StringBuilder htmlContent;
    private HttpClient http = new HttpClient();
    private GetMethod get;
    private DlgCariPasien pasien=new DlgCariPasien(null,true);

    /** Creates new form DlgLhtBiaya
     * @param parent
     * @param modal */
    public RMBerkasKlaimPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);
        
        tabModeRegistrasi=new DefaultTableModel(null,new Object[]{
                "No.","No.Rawat","Tanggal","Jam","Kd.Dokter","Dokter Dituju/DPJP","Umur","Poliklinik/Kamar","Jenis Bayar"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbRegistrasi.setModel(tabModeRegistrasi);

        tbRegistrasi.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbRegistrasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 9; i++) {
            TableColumn column = tbRegistrasi.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(25);
            }else if(i==1){
                column.setPreferredWidth(110);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(60);
            }else if(i==4){
                column.setPreferredWidth(70);
            }else if(i==5){
                column.setPreferredWidth(250);   
            }else if(i==6){
                column.setPreferredWidth(40);
            }else if(i==7){
                column.setPreferredWidth(200);
            }else if(i==8){
                column.setPreferredWidth(110);
            }
        }
        tbRegistrasi.setDefaultRenderer(Object.class, new WarnaTable());
        
        NoRM.setDocument(new batasInput((byte)20).getKata(NoRM));
        NoRawat.setDocument(new batasInput((byte)20).getKata(NoRawat));
        
        pasien.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(pasien.getTable().getSelectedRow()!= -1){                   
                    NoRM.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),0).toString());
                    NmPasien.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),1).toString());
                    Jk.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),3).toString());
                    TempatLahir.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),4).toString());
                    TanggalLahir.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),5).toString());
                    IbuKandung.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),6).toString());
                    Alamat.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),7).toString());
                    GD.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),8).toString());
                    StatusNikah.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),10).toString());
                    Agama.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),11).toString());
                    Pendidikan.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),15).toString());
                    Bahasa.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),26).toString());
                    CacatFisik.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),32).toString());
                }    
                NoRM.requestFocus();
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
        
        pasien.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    pasien.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTMLSOAPI.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi a{text-decoration:none;color:#8b9b95;padding:0 0 0 0px;font-family: Tahoma;font-size: 8.5px;border: white;}");
        Document doc = kit.createDefaultDocument();
        
        LoadHTMLSOAPI.setDocument(doc);
        LoadHTMLSOAPI.setEditable(false);
        LoadHTMLSOAPI.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                   desktop.browse(e.getURL().toURI());
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
            }
        });
        
        isMenu();
    }    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        buttonGroup1 = new javax.swing.ButtonGroup();
        Pekerjaan = new widget.TextBox();
        ppMenuRiwayat = new javax.swing.JPopupMenu();
        menuCekKelengkapanBerkas = new javax.swing.JMenuItem();
        menuRiwayatPerawatan = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass5 = new widget.panelisi();
        RSemua = new widget.RadioButton();
        RTanggal = new widget.RadioButton();
        Tgl1 = new widget.Tanggal();
        label18 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        RNoRw = new widget.RadioButton();
        NoRawat = new widget.TextBox();
        BtnCari1 = new widget.Button();
        label19 = new widget.Label();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        Scroll1 = new widget.ScrollPane();
        tbRegistrasi = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        LoadHTMLSOAPI = new widget.editorpane();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.panelisi();
        label17 = new widget.Label();
        NoRM = new widget.TextBox();
        NmPasien = new widget.TextBox();
        BtnPasien = new widget.Button();
        label20 = new widget.Label();
        Jk = new widget.TextBox();
        label21 = new widget.Label();
        TempatLahir = new widget.TextBox();
        label22 = new widget.Label();
        Alamat = new widget.TextBox();
        label23 = new widget.Label();
        GD = new widget.TextBox();
        label24 = new widget.Label();
        IbuKandung = new widget.TextBox();
        TanggalLahir = new widget.TextBox();
        label25 = new widget.Label();
        Agama = new widget.TextBox();
        StatusNikah = new widget.TextBox();
        label26 = new widget.Label();
        Pendidikan = new widget.TextBox();
        label27 = new widget.Label();
        label28 = new widget.Label();
        Bahasa = new widget.TextBox();
        label29 = new widget.Label();
        CacatFisik = new widget.TextBox();

        Pekerjaan.setEditable(false);
        Pekerjaan.setName("Pekerjaan"); // NOI18N
        Pekerjaan.setPreferredSize(new java.awt.Dimension(100, 23));

        ppMenuRiwayat.setName("ppMenuRiwayat"); // NOI18N

        menuCekKelengkapanBerkas.setText("jMenuItem1");
        menuCekKelengkapanBerkas.setName("menuCekKelengkapanBerkas"); // NOI18N
        ppMenuRiwayat.add(menuCekKelengkapanBerkas);

        menuRiwayatPerawatan.setText("jMenuItem2");
        menuRiwayatPerawatan.setName("menuRiwayatPerawatan"); // NOI18N
        ppMenuRiwayat.add(menuRiwayatPerawatan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Kelengkapan Berkas Pasien Untuk Klaim BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        RSemua.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(RSemua);
        RSemua.setSelected(true);
        RSemua.setText("Semua Riwayat");
        RSemua.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        RSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        RSemua.setName("RSemua"); // NOI18N
        RSemua.setPreferredSize(new java.awt.Dimension(104, 23));
        panelGlass5.add(RSemua);

        RTanggal.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(RTanggal);
        RTanggal.setText("Tanggal :");
        RTanggal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        RTanggal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        RTanggal.setName("RTanggal"); // NOI18N
        RTanggal.setPreferredSize(new java.awt.Dimension(75, 23));
        panelGlass5.add(RTanggal);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setPreferredSize(new java.awt.Dimension(90, 23));
        Tgl1.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                Tgl1KeyPressed(evt);
            }
        });
        panelGlass5.add(Tgl1);

        label18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label18.setText("s.d.");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass5.add(label18);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setPreferredSize(new java.awt.Dimension(90, 23));
        Tgl2.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                Tgl2KeyPressed(evt);
            }
        });
        panelGlass5.add(Tgl2);

        RNoRw.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(RNoRw);
        RNoRw.setText("Nomor :");
        RNoRw.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        RNoRw.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        RNoRw.setName("RNoRw"); // NOI18N
        RNoRw.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass5.add(RNoRw);

        NoRawat.setName("NoRawat"); // NOI18N
        NoRawat.setPreferredSize(new java.awt.Dimension(135, 23));
        NoRawat.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                NoRawatKeyPressed(evt);
            }
        });
        panelGlass5.add(NoRawat);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari1.setMnemonic('2');
        BtnCari1.setToolTipText("Alt+2");
        BtnCari1.setName("BtnCari1"); // NOI18N
        BtnCari1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BtnCari1ActionPerformed(evt);
            }
        });
        panelGlass5.add(BtnCari1);

        label19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(15, 23));
        panelGlass5.add(label19);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPrint.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BtnPrintActionPerformed(evt);
            }
        });
        panelGlass5.add(BtnPrint);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnKeluar);

        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(255, 255, 254));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                TabRawatMouseClicked(evt);
            }
        });

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbRegistrasi.setName("tbRegistrasi"); // NOI18N
        Scroll1.setViewportView(tbRegistrasi);

        TabRawat.addTab("Riwayat Kunjungan", Scroll1);

        Scroll2.setBorder(null);
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        LoadHTMLSOAPI.setBorder(null);
        LoadHTMLSOAPI.setName("LoadHTMLSOAPI"); // NOI18N
        Scroll2.setViewportView(LoadHTMLSOAPI);

        TabRawat.addTab("Kelengkapan Berkas Klaim", Scroll2);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        PanelInput.setBackground(new java.awt.Color(255, 255, 255));
        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('M');
        ChkInput.setSelected(true);
        ChkInput.setText(".: Tampilkan/Sembunyikan Data Pasien");
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
        ChkInput.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 104));
        FormInput.setLayout(null);

        label17.setText("Pasien :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label17);
        label17.setBounds(5, 10, 55, 23);

        NoRM.setName("NoRM"); // NOI18N
        NoRM.setPreferredSize(new java.awt.Dimension(100, 23));
        NoRM.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                NoRMKeyPressed(evt);
            }
        });
        FormInput.add(NoRM);
        NoRM.setBounds(64, 10, 100, 23);

        NmPasien.setEditable(false);
        NmPasien.setName("NmPasien"); // NOI18N
        NmPasien.setPreferredSize(new java.awt.Dimension(220, 23));
        FormInput.add(NmPasien);
        NmPasien.setBounds(167, 10, 220, 23);

        BtnPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPasien.setMnemonic('3');
        BtnPasien.setToolTipText("Alt+3");
        BtnPasien.setName("BtnPasien"); // NOI18N
        BtnPasien.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPasien.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BtnPasienActionPerformed(evt);
            }
        });
        BtnPasien.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                BtnPasienKeyPressed(evt);
            }
        });
        FormInput.add(BtnPasien);
        BtnPasien.setBounds(390, 10, 28, 23);

        label20.setText("J.K. :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label20);
        label20.setBounds(436, 10, 30, 23);

        Jk.setEditable(false);
        Jk.setName("Jk"); // NOI18N
        Jk.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(Jk);
        Jk.setBounds(470, 10, 40, 23);

        label21.setText("Tempat & Tgl.Lahir :");
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label21);
        label21.setBounds(523, 10, 110, 23);

        TempatLahir.setEditable(false);
        TempatLahir.setName("TempatLahir"); // NOI18N
        TempatLahir.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(TempatLahir);
        TempatLahir.setBounds(637, 10, 140, 23);

        label22.setText("Alamat :");
        label22.setName("label22"); // NOI18N
        label22.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label22);
        label22.setBounds(5, 40, 55, 23);

        Alamat.setEditable(false);
        Alamat.setName("Alamat"); // NOI18N
        Alamat.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(Alamat);
        Alamat.setBounds(64, 40, 354, 23);

        label23.setText("G.D. :");
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label23);
        label23.setBounds(436, 40, 30, 23);

        GD.setEditable(false);
        GD.setName("GD"); // NOI18N
        GD.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(GD);
        GD.setBounds(470, 40, 40, 23);

        label24.setText("Nama Ibu Kandung :");
        label24.setName("label24"); // NOI18N
        label24.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label24);
        label24.setBounds(523, 40, 110, 23);

        IbuKandung.setEditable(false);
        IbuKandung.setName("IbuKandung"); // NOI18N
        IbuKandung.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(IbuKandung);
        IbuKandung.setBounds(637, 40, 225, 23);

        TanggalLahir.setEditable(false);
        TanggalLahir.setName("TanggalLahir"); // NOI18N
        TanggalLahir.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(TanggalLahir);
        TanggalLahir.setBounds(779, 10, 83, 23);

        label25.setText("Agama :");
        label25.setName("label25"); // NOI18N
        label25.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label25);
        label25.setBounds(5, 70, 55, 23);

        Agama.setEditable(false);
        Agama.setName("Agama"); // NOI18N
        Agama.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(Agama);
        Agama.setBounds(64, 70, 100, 23);

        StatusNikah.setEditable(false);
        StatusNikah.setName("StatusNikah"); // NOI18N
        StatusNikah.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(StatusNikah);
        StatusNikah.setBounds(245, 70, 100, 23);

        label26.setText("Stts.Nikah :");
        label26.setName("label26"); // NOI18N
        label26.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label26);
        label26.setBounds(176, 70, 65, 23);

        Pendidikan.setEditable(false);
        Pendidikan.setName("Pendidikan"); // NOI18N
        Pendidikan.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(Pendidikan);
        Pendidikan.setBounds(429, 70, 80, 23);

        label27.setText("Pendidikan :");
        label27.setName("label27"); // NOI18N
        label27.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label27);
        label27.setBounds(355, 70, 70, 23);

        label28.setText("Bahasa :");
        label28.setName("label28"); // NOI18N
        label28.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label28);
        label28.setBounds(520, 70, 50, 23);

        Bahasa.setEditable(false);
        Bahasa.setName("Bahasa"); // NOI18N
        Bahasa.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(Bahasa);
        Bahasa.setBounds(574, 70, 100, 23);

        label29.setText("Cacat Fisik :");
        label29.setName("label29"); // NOI18N
        label29.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label29);
        label29.setBounds(683, 70, 70, 23);

        CacatFisik.setEditable(false);
        CacatFisik.setName("CacatFisik"); // NOI18N
        CacatFisik.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(CacatFisik);
        CacatFisik.setBounds(757, 70, 105, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,Tgl1,NoRM);}
}//GEN-LAST:event_BtnKeluarKeyPressed

private void NoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRMKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isPasien();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            isPasien();
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnPasienActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            isPasien();
            BtnPrint.requestFocus();
        }
}//GEN-LAST:event_NoRMKeyPressed

private void BtnPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPasienActionPerformed
    if(akses.getpasien()==true){
        pasien.isCek();
        pasien.emptTeks();
        pasien.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pasien.setLocationRelativeTo(internalFrame1);
        pasien.setVisible(true);
    }   
}//GEN-LAST:event_BtnPasienActionPerformed

private void BtnPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPasienKeyPressed
    //Valid.pindah(evt,Tgl2,TKd);
}//GEN-LAST:event_BtnPasienKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if(NoRM.getText().trim().equals("")||NmPasien.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Pasien masih kosong...!!!");
        }else{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            switch (TabRawat.getSelectedIndex()) {
                case 0:
                    if(tabModeRegistrasi.getRowCount()==0){
                        JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
                    }else if(tabModeRegistrasi.getRowCount()!=0){
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
                        Sequel.queryu("delete from temporary_resume");

                        for(int i=0;i<tabModeRegistrasi.getRowCount();i++){  
                            Sequel.menyimpan("temporary_resume","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",38,new String[]{
                                "0",tabModeRegistrasi.getValueAt(i,0).toString(),tabModeRegistrasi.getValueAt(i,1).toString(),tabModeRegistrasi.getValueAt(i,2).toString(),
                                tabModeRegistrasi.getValueAt(i,3).toString(),tabModeRegistrasi.getValueAt(i,4).toString(),tabModeRegistrasi.getValueAt(i,5).toString(),
                                tabModeRegistrasi.getValueAt(i,6).toString(),tabModeRegistrasi.getValueAt(i,7).toString(),tabModeRegistrasi.getValueAt(i,8).toString(),
                                "","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
                            });
                        }

                        Map<String, Object> param = new HashMap<>();  
                            param.put("namars",akses.getnamars());
                            param.put("alamatrs",akses.getalamatrs());
                            param.put("kotars",akses.getkabupatenrs());
                            param.put("propinsirs",akses.getpropinsirs());
                            param.put("kontakrs",akses.getkontakrs());
                            param.put("emailrs",akses.getemailrs());   
                            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                        Valid.MyReport2("rptRiwayatRegistrasi.jasper","report","::[ Riwayat Registrasi ]::",param);
                        this.setCursor(Cursor.getDefaultCursor());
                    }
                    break;
                case 1:
                    panggilLaporan(LoadHTMLSOAPI.getText()); 
                    break;
                default:
                    break;
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void Tgl1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tgl1KeyPressed
        Valid.pindah(evt, BtnKeluar, Tgl2);
    }//GEN-LAST:event_Tgl1KeyPressed

    private void Tgl2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tgl2KeyPressed
        Valid.pindah(evt, Tgl1,NoRM);
    }//GEN-LAST:event_Tgl2KeyPressed

    private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari1ActionPerformed
        if(NoRM.getText().trim().equals("")||NmPasien.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Pasien masih kosong...!!!");
        }else{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            switch (TabRawat.getSelectedIndex()) {
                case 0:
                    tampilKunjungan();
                    break;
                case 1:
                    tampilSoapi();
                    break;
                default:
                    break;
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnCari1ActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        BtnCari1ActionPerformed(null);
    }//GEN-LAST:event_TabRawatMouseClicked

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void NoRawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRawatKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCari1ActionPerformed(null);
        }
    }//GEN-LAST:event_NoRawatKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMBerkasKlaimPasien dialog = new RMBerkasKlaimPasien(new javax.swing.JFrame(), true);
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
    private widget.TextBox Agama;
    private widget.TextBox Alamat;
    private widget.TextBox Bahasa;
    private widget.Button BtnCari1;
    private widget.Button BtnKeluar;
    private widget.Button BtnPasien;
    private widget.Button BtnPrint;
    private widget.TextBox CacatFisik;
    private widget.CekBox ChkInput;
    private widget.panelisi FormInput;
    private widget.TextBox GD;
    private widget.TextBox IbuKandung;
    private widget.TextBox Jk;
    private widget.editorpane LoadHTMLSOAPI;
    private widget.TextBox NmPasien;
    private widget.TextBox NoRM;
    private widget.TextBox NoRawat;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox Pekerjaan;
    private widget.TextBox Pendidikan;
    private widget.RadioButton RNoRw;
    private widget.RadioButton RSemua;
    private widget.RadioButton RTanggal;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.TextBox StatusNikah;
    private javax.swing.JTabbedPane TabRawat;
    private widget.TextBox TanggalLahir;
    private widget.TextBox TempatLahir;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label21;
    private widget.Label label22;
    private widget.Label label23;
    private widget.Label label24;
    private widget.Label label25;
    private widget.Label label26;
    private widget.Label label27;
    private widget.Label label28;
    private widget.Label label29;
    private javax.swing.JMenuItem menuCekKelengkapanBerkas;
    private javax.swing.JMenuItem menuRiwayatPerawatan;
    private widget.panelisi panelGlass5;
    private javax.swing.JPopupMenu ppMenuRiwayat;
    private widget.Table tbRegistrasi;
    // End of variables declaration//GEN-END:variables

    public void setNoRM(String noRM, String nama, String tanggalDari)
    {
        NoRM.setText(noRM);
        NmPasien.setText(nama);
        RTanggal.setSelected(true);
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        
            Tgl1.setSelectedItem(dateFormat.parse(tanggalDari));
        } catch (Exception e) {
            Tgl1.setSelectedItem(new Date());
        }
        
        isPasien();
        BtnCari1ActionPerformed(null);
    }
    
    private void isPasien() {
        try{
            ps=koneksi.prepareStatement(
                    "select pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tmp_lahir,pasien.tgl_lahir,pasien.agama,"+
                    "bahasa_pasien.nama_bahasa,cacat_fisik.nama_cacat,pasien.gol_darah,pasien.nm_ibu,pasien.stts_nikah,pasien.pnd, "+
                    "concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat,pasien.pekerjaan "+
                    "from pasien inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien "+
                    "inner join cacat_fisik on cacat_fisik.id=pasien.cacat_fisik "+
                    "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel "+
                    "inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "+
                    "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab "+
                    "where pasien.no_rkm_medis=?");
            try {
                ps.setString(1,NoRM.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    NoRM.setText(rs.getString("no_rkm_medis"));
                    NmPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TempatLahir.setText(rs.getString("tmp_lahir"));
                    TanggalLahir.setText(rs.getString("tgl_lahir"));
                    Alamat.setText(rs.getString("alamat"));
                    GD.setText(rs.getString("gol_darah"));
                    IbuKandung.setText(rs.getString("nm_ibu"));
                    Agama.setText(rs.getString("agama"));
                    StatusNikah.setText(rs.getString("stts_nikah"));
                    Pendidikan.setText(rs.getString("pnd"));
                    Bahasa.setText(rs.getString("nama_bahasa"));
                    CacatFisik.setText(rs.getString("nama_cacat"));
                    Pekerjaan.setText(rs.getString("pekerjaan"));
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
    
    private void isMenu(){
        
    }
    
    private void tampilKunjungan() {
        Valid.tabelKosong(tabModeRegistrasi);
        try {
            if (RSemua.isSelected()) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.status_lanjut,"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                    + "poliklinik.kd_poli,poliklinik.nm_poli,penjab.png_jawab from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli inner join penjab  on reg_periksa.kd_pj=penjab.kd_pj  "
                    + "where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and reg_periksa.kd_pj = (select password_asuransi.kd_pj from password_asuransi limit 1) order by reg_periksa.tgl_registrasi");
            } else if (RTanggal.isSelected()) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.status_lanjut,"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                    + "poliklinik.kd_poli,poliklinik.nm_poli,penjab.png_jawab from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli inner join penjab on reg_periksa.kd_pj=penjab.kd_pj  "
                    + "where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and "
                    + "reg_periksa.tgl_registrasi between ? and ? and reg_periksa.kd_pj = (select password_asuransi.kd_pj from password_asuransi limit 1) order by reg_periksa.tgl_registrasi");
            } else if (RNoRw.isSelected()) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.status_lanjut,"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                    + "poliklinik.kd_poli,poliklinik.nm_poli,penjab.png_jawab from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli inner join penjab on reg_periksa.kd_pj=penjab.kd_pj  "
                    + "where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and reg_periksa.no_rawat=?");
            }

            try {
                i = 0;
                if (RSemua.isSelected()) {
                    ps.setString(1, NoRM.getText().trim());
                } else if (RTanggal.isSelected()) {
                    ps.setString(1, NoRM.getText().trim());
                    ps.setString(2, Valid.SetTgl(Tgl1.getSelectedItem() + ""));
                    ps.setString(3, Valid.SetTgl(Tgl2.getSelectedItem() + ""));
                } else if (RNoRw.isSelected()) {
                    ps.setString(1, NoRM.getText().trim());
                    ps.setString(2, NoRawat.getText().trim());
                }
                
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeRegistrasi.addRow(new String[] {
                        String.valueOf(++i), rs.getString("no_rawat"), rs.getString("tgl_registrasi"), rs.getString("jam_reg"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("umurdaftar") + " " + rs.getString("sttsumur"),
                        rs.getString("kd_poli") + " " + rs.getString("nm_poli"), rs.getString("png_jawab")
                    });
                    
                    ps2 = koneksi.prepareStatement(
                        "select rujukan_internal_poli.kd_dokter,dokter.nm_dokter,"
                        + "rujukan_internal_poli.kd_poli,poliklinik.nm_poli from rujukan_internal_poli "
                        + "inner join dokter inner join poliklinik on rujukan_internal_poli.kd_dokter=dokter.kd_dokter "
                        + "and rujukan_internal_poli.kd_poli=poliklinik.kd_poli where rujukan_internal_poli.no_rawat=?");
                    try {
                        ps2.setString(1, rs.getString("no_rawat"));
                        rs2 = ps2.executeQuery();
                        while (rs2.next()) {
                            tabModeRegistrasi.addRow(new String[] {
                                "", rs.getString("no_rawat"), rs.getString("tgl_registrasi"), "",
                                rs2.getString("kd_dokter"), rs2.getString("nm_dokter"), rs.getString("umurdaftar") + " " + rs.getString("sttsumur"),
                                rs2.getString("kd_poli") + " " + rs2.getString("nm_poli"), rs.getString("png_jawab")
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Notifikasi 3 : " + e);
                    } finally {
                        if (rs2 != null) {
                            rs2.close();
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                    
                    kddpjp = "";
                    dpjp = "";
                    
                    if (rs.getString("status_lanjut").equals("Ranap")) {
                        kddpjp = Sequel.cariIsi("select dpjp_ranap.kd_dokter from dpjp_ranap where dpjp_ranap.no_rawat=?", rs.getString("no_rawat"));
                        if (!kddpjp.equals("")) {
                            dpjp = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kddpjp);
                        } else {
                            kddpjp = rs.getString("kd_dokter");
                            dpjp = rs.getString("nm_dokter");
                        }
                    }
                    ps2 = koneksi.prepareStatement(
                        "select kamar_inap.tgl_masuk,kamar_inap.jam_masuk,kamar_inap.kd_kamar,bangsal.nm_bangsal "
                        + "from kamar_inap inner join kamar on kamar_inap.kd_kamar=kamar.kd_kamar inner join bangsal  "
                        + "on kamar.kd_bangsal=bangsal.kd_bangsal where kamar_inap.no_rawat=?");
                    try {
                        ps2.setString(1, rs.getString("no_rawat"));
                        rs2 = ps2.executeQuery();
                        while (rs2.next()) {
                            tabModeRegistrasi.addRow(new String[] {
                                "", rs.getString("no_rawat"), rs2.getString("tgl_masuk"), rs2.getString("jam_masuk"),
                                kddpjp, dpjp, rs.getString("umurdaftar") + " " + rs.getString("sttsumur"),
                                rs2.getString("kd_kamar") + " " + rs2.getString("nm_bangsal"), rs.getString("png_jawab")
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Notifikasi 2 : " + e);
                    } finally {
                        if (rs2 != null) {
                            rs2.close();
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi 1 : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }
    
    private void tampilBerkasKlaimBPJS()
    {
        try {
            htmlContent = new StringBuilder();
            if (RSemua.isSelected()) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,poliklinik.nm_poli,reg_periksa.p_jawab,reg_periksa.almt_pj,"
                    + "reg_periksa.hubunganpj,reg_periksa.biaya_reg,reg_periksa.status_lanjut,penjab.png_jawab,"
                    + "reg_periksa.umurdaftar,reg_periksa.sttsumur "
                    + "from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "
                    + "where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? order by reg_periksa.tgl_registrasi");
            } else if (RTanggal.isSelected()) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,poliklinik.nm_poli,reg_periksa.p_jawab,reg_periksa.almt_pj,"
                    + "reg_periksa.hubunganpj,reg_periksa.biaya_reg,reg_periksa.status_lanjut,penjab.png_jawab,"
                    + "reg_periksa.umurdaftar,reg_periksa.sttsumur "
                    + "from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "
                    + "where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and "
                    + "reg_periksa.tgl_registrasi between ? and ? order by reg_periksa.tgl_registrasi");
            } else if (RNoRw.isSelected()) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,"
                    + "reg_periksa.kd_dokter,dokter.nm_dokter,poliklinik.nm_poli,reg_periksa.p_jawab,reg_periksa.almt_pj,"
                    + "reg_periksa.hubunganpj,reg_periksa.biaya_reg,reg_periksa.status_lanjut,penjab.png_jawab,"
                    + "reg_periksa.umurdaftar,reg_periksa.sttsumur "
                    + "from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "
                    + "where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and reg_periksa.no_rawat=?");
            }
            
            try {
                if (RSemua.isSelected()) {
                    ps.setString(1, NoRM.getText().trim());
                } else if (RTanggal.isSelected()) {
                    ps.setString(1, NoRM.getText().trim());
                    ps.setString(2, Valid.SetTgl(Tgl1.getSelectedItem().toString()));
                    ps.setString(3, Valid.SetTgl(Tgl2.getSelectedItem().toString()));
                } else if (RNoRw.isSelected()) {
                    ps.setString(1, NoRM.getText().trim());
                    ps.setString(2, NoRawat.getText().trim());
                }
                
                urut = 1;
                
                rs = ps.executeQuery();
                
                while (rs.next()) {
                    htmlContent.append(
                        "<tr class=\"isi\">"
                        + "<td valign=\"top\" width=\"2%\">" + urut + "</td>"
                        + "<td valign=\"top\" width=\"18%\">No.Rawat</td>"
                        + "<td valign=\"top\" width=\"1%\" align=\"center\">:</td>"
                        + "<td valign=\"top\" width=\"79%\">" + rs.getString("no_rawat") + "</td>"
                        + "</tr>"
                        + "<tr class=\"isi\">"
                        + "<td valign=\"top\" width=\"2%\"></td>"
                        + "<td valign=\"top\" width=\"18%\">No.Registrasi</td>"
                        + "<td valign=\"top\" width=\"1%\" align=\"center\">:</td>"
                        + "<td valign=\"top\" width=\"79%\">" + rs.getString("no_reg") + "</td>"
                        + "</tr>"
                        + "<tr class=\"isi\">"
                        + "<td valign=\"top\" width=\"2%\"></td>"
                        + "<td valign=\"top\" width=\"18%\">Tanggal Registrasi</td>"
                        + "<td valign=\"top\" width=\"1%\" align=\"center\">:</td>"
                        + "<td valign=\"top\" width=\"79%\">" + rs.getString("tgl_registrasi") + " " + rs.getString("jam_reg") + "</td>"
                        + "</tr>"
                        + "<tr class=\"isi\">"
                        + "<td valign=\"top\" width=\"2%\"></td>"
                        + "<td valign=\"top\" width=\"18%\">Umur Saat Daftar</td>"
                        + "<td valign=\"top\" width=\"1%\" align=\"center\">:</td>"
                        + "<td valign=\"top\" width=\"79%\">" + rs.getString("umurdaftar") + " " + rs.getString("sttsumur") + "</td>"
                        + "</tr>"
                        + "<tr class=\"isi\">"
                        + "<td valign=\"top\" width=\"2%\"></td>"
                        + "<td valign=\"top\" width=\"18%\">Unit/Poliklinik</td>"
                        + "<td valign=\"top\" width=\"1%\" align=\"center\">:</td>"
                        + "<td valign=\"top\" width=\"79%\">" + rs.getString("nm_poli") + polirujukan + "</td>"
                        + "</tr>"
                        + "<tr class=\"isi\">"
                        + "<td valign=\"top\" width=\"2%\"></td>"
                        + "<td valign=\"top\" width=\"18%\">Dokter Poli</td>"
                        + "<td valign=\"top\" width=\"1%\" align=\"center\">:</td>"
                        + "<td valign=\"top\" width=\"79%\">" + rs.getString("nm_dokter") + dokterrujukan + "</td>"
                        + "</tr>"
                    );
                    
                    // SEP ralan
                    
                    if (rs.getString("status_lanjut") == "Ranap") {
                        
                    }
                    
                    // SEP ranap
                    
                    // SPRI
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                
                if (ps != null) {
                    ps.close();
                }
            }
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilSoapi() {
        try {
            htmlContent = new StringBuilder();
            htmlContent.append(                             
                "<tr class='isi'>"+
                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='5%'>Tgl.Reg</td>"+
                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='8%'>No.Rawat</td>"+
                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='3%'>Status</td>"+
                    "<td valign='middle' bgcolor='#FFFAF8' align='center' width='84%'>S.O.A.P.I.E</td>"+
                "</tr>"
            );     
            if(RSemua.isSelected()==true){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.status_lanjut "+
                    "from reg_periksa where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? order by reg_periksa.tgl_registrasi");
            }else if(RTanggal.isSelected()==true){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.status_lanjut "+
                    "from reg_periksa where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and "+
                    "reg_periksa.tgl_registrasi between ? and ? order by reg_periksa.tgl_registrasi");
            }else if(RNoRw.isSelected()==true){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.status_lanjut "+
                    "from reg_periksa where reg_periksa.stts<>'Batal' and reg_periksa.no_rkm_medis=? and reg_periksa.no_rawat=?");
            }
            try {
                if(RSemua.isSelected()==true){
                    ps.setString(1,NoRM.getText().trim());
                }else if(RTanggal.isSelected()==true){
                    ps.setString(1,NoRM.getText().trim());
                    ps.setString(2,Valid.SetTgl(Tgl1.getSelectedItem()+""));
                    ps.setString(3,Valid.SetTgl(Tgl2.getSelectedItem()+""));
                }else if(RNoRw.isSelected()==true){
                    ps.setString(1,NoRM.getText().trim());
                    ps.setString(2,NoRawat.getText().trim());
                }
                
                rs=ps.executeQuery();
                while(rs.next()){
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top' align='center'>"+rs.getString("tgl_registrasi")+"</td>"+
                            "<td valign='top' align='center'>"+rs.getString("no_rawat")+"</td>"+
                            "<td valign='top' align='center'>"+rs.getString("status_lanjut")+"</td>"+
                            "<td valign='top' align='center'>"+
                                "<table width='100%' border='0' align='center' cellpadding='2px' cellspacing='0'>");
                    try {
                        rs2=koneksi.prepareStatement(
                                "select pemeriksaan_ralan.tgl_perawatan,pemeriksaan_ralan.jam_rawat,pemeriksaan_ralan.suhu_tubuh,pemeriksaan_ralan.tensi,pemeriksaan_ralan.nadi,pemeriksaan_ralan.respirasi,"+
                                "pemeriksaan_ralan.tinggi,pemeriksaan_ralan.berat,pemeriksaan_ralan.gcs,pemeriksaan_ralan.spo2,pemeriksaan_ralan.kesadaran,pemeriksaan_ralan.keluhan, "+
                                "pemeriksaan_ralan.pemeriksaan,pemeriksaan_ralan.alergi,pemeriksaan_ralan.lingkar_perut,pemeriksaan_ralan.rtl,pemeriksaan_ralan.penilaian,"+
                                "pemeriksaan_ralan.instruksi,pemeriksaan_ralan.evaluasi,pemeriksaan_ralan.nip,pegawai.nama,pegawai.jbtn from pemeriksaan_ralan inner join pegawai on pemeriksaan_ralan.nip=pegawai.nik where "+
                                "pemeriksaan_ralan.no_rawat='"+rs.getString("no_rawat")+"' "+
                                "order by pemeriksaan_ralan.tgl_perawatan,pemeriksaan_ralan.jam_rawat").executeQuery();
                        if(rs2.next()){
                            htmlContent.append(
                                    "<tr class='isi'>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='7%'>Tanggal</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='13%'>Dokter/Paramedis</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Subjek</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='13%'>Objek</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='13%'>Asesmen</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Plan</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Instruksi</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Evaluasi</td>"+
                                    "</tr>");
                            rs2.beforeFirst();
                            while(rs2.next()){
                                 htmlContent.append(                             
                                    "<tr class='isi'>"+
                                        "<td align='center'>"+rs2.getString("tgl_perawatan")+"<br>"+rs2.getString("jam_rawat")+"</td>"+
                                        "<td align='center'>"+rs2.getString("nip")+"<br>"+rs2.getString("nama")+"</td>"+
                                        "<td align='left'>"+rs2.getString("keluhan").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("pemeriksaan").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+
                                        (rs2.getString("alergi").equals("")?"":"<br>Alergi : "+rs2.getString("alergi"))+
                                        (rs2.getString("suhu_tubuh").equals("")?"":"<br>Suhu(C) : "+rs2.getString("suhu_tubuh"))+
                                        (rs2.getString("tensi").equals("")?"":"<br>Tensi : "+rs2.getString("tensi"))+
                                        (rs2.getString("nadi").equals("")?"":"<br>Nadi(/menit) : "+rs2.getString("nadi"))+
                                        (rs2.getString("respirasi").equals("")?"":"<br>Respirasi(/menit) : "+rs2.getString("respirasi"))+
                                        (rs2.getString("tinggi").equals("")?"":"<br>Tinggi(Cm) : "+rs2.getString("tinggi"))+
                                        (rs2.getString("berat").equals("")?"":"<br>Berat(Kg) : "+rs2.getString("berat"))+
                                        (rs2.getString("lingkar_perut").equals("")?"":"<br>Lingkar Perut(Cm) : "+rs2.getString("lingkar_perut"))+
                                        (rs2.getString("spo2").equals("")?"":"<br>SpO2(%) : "+rs2.getString("spo2"))+
                                        (rs2.getString("gcs").equals("")?"":"<br>GCS(E,V,M) : "+rs2.getString("gcs"))+
                                        (rs2.getString("kesadaran").equals("")?"":"<br>Kesadaran : "+rs2.getString("kesadaran"))+
                                        "</td>"+
                                        "<td align='left'>"+rs2.getString("penilaian").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("rtl").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("instruksi").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("evaluasi").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                    "</tr>"
                                 );
                            } 
                        }       
                    } catch (Exception e) {
                        System.out.println("Notifikasi : "+e);
                    } finally{
                        if(rs2!=null){
                            rs2.close();
                        }
                    }
                    
                    try {
                        rs2=koneksi.prepareStatement(
                                "select pemeriksaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                                "pemeriksaan_ranap.tgl_perawatan,pemeriksaan_ranap.jam_rawat,pemeriksaan_ranap.suhu_tubuh,pemeriksaan_ranap.tensi, " +
                                "pemeriksaan_ranap.nadi,pemeriksaan_ranap.respirasi,pemeriksaan_ranap.tinggi, " +
                                "pemeriksaan_ranap.berat,pemeriksaan_ranap.spo2,pemeriksaan_ranap.gcs,pemeriksaan_ranap.kesadaran,pemeriksaan_ranap.keluhan, " +
                                "pemeriksaan_ranap.pemeriksaan,pemeriksaan_ranap.alergi,pemeriksaan_ranap.penilaian,pemeriksaan_ranap.rtl,"+
                                "pemeriksaan_ranap.instruksi,pemeriksaan_ranap.evaluasi,pemeriksaan_ranap.nip,pegawai.nama,pegawai.jbtn "+
                                "from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                                "inner join pemeriksaan_ranap on pemeriksaan_ranap.no_rawat=reg_periksa.no_rawat "+
                                "inner join pegawai on pemeriksaan_ranap.nip=pegawai.nik where pemeriksaan_ranap.no_rawat='"+rs.getString("no_rawat")+"' "+
                                "order by pemeriksaan_ranap.tgl_perawatan,pemeriksaan_ranap.jam_rawat").executeQuery();
                        if(rs2.next()){
                            htmlContent.append(
                                    "<tr class='isi'>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='7%'>Tanggal</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='13%'>Dokter/Paramedis</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Subjek</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='13%'>Objek</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='13%'>Asesmen</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Plan</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Instruksi</td>"+
                                        "<td valign='middle' bgcolor='#FFFFF8' align='center' width='14%'>Evaluasi</td>"+
                                    "</tr>");
                            rs2.beforeFirst();
                            while(rs2.next()){
                                 htmlContent.append(                             
                                    "<tr class='isi'>"+
                                        "<td align='center'>"+rs2.getString("tgl_perawatan")+"<br>"+rs2.getString("jam_rawat")+"</td>"+
                                        "<td align='center'>"+rs2.getString("nip")+"<br>"+rs2.getString("nama")+"</td>"+
                                        "<td align='left'>"+rs2.getString("keluhan").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("pemeriksaan").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+
                                        (rs2.getString("alergi").equals("")?"":"<br>Alergi : "+rs2.getString("alergi"))+
                                        (rs2.getString("suhu_tubuh").equals("")?"":"<br>Suhu(C) : "+rs2.getString("suhu_tubuh"))+
                                        (rs2.getString("tensi").equals("")?"":"<br>Tensi : "+rs2.getString("tensi"))+
                                        (rs2.getString("nadi").equals("")?"":"<br>Nadi(/menit) : "+rs2.getString("nadi"))+
                                        (rs2.getString("respirasi").equals("")?"":"<br>Respirasi(/menit) : "+rs2.getString("respirasi"))+
                                        (rs2.getString("tinggi").equals("")?"":"<br>Tinggi(Cm) : "+rs2.getString("tinggi"))+
                                        (rs2.getString("berat").equals("")?"":"<br>Berat(Kg) : "+rs2.getString("berat"))+
                                        (rs2.getString("spo2").equals("")?"":"<br>SpO2(%) : "+rs2.getString("spo2"))+
                                        (rs2.getString("gcs").equals("")?"":"<br>GCS(E,V,M) : "+rs2.getString("gcs"))+
                                        (rs2.getString("kesadaran").equals("")?"":"<br>Kesadaran : "+rs2.getString("kesadaran"))+
                                        "</td>"+
                                        "<td align='left'>"+rs2.getString("penilaian").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("rtl").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("instruksi").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                        "<td align='left'>"+rs2.getString("evaluasi").replaceAll("(\r\n|\r|\n|\n\r)","<br>")+"</td>"+
                                    "</tr>"
                                 ); 
                            } 
                        }       
                    } catch (Exception e) {
                        System.out.println("Notifikasi : "+e);
                    } finally{
                        if(rs2!=null){
                            rs2.close();
                        }
                    }
                    htmlContent.append(
                                "</table>"+
                            "</td>"+
                        "</tr>"
                    );
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
            
            LoadHTMLSOAPI.setText(
                    "<html>"+
                      "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>");
        } catch (Exception e) {
            System.out.println("laporan.DlgRL4A.prosesCari() 5 : "+e);
        } 
    }

    private void panggilLaporan(String teks) {
        try{
            File g = new File("file.css");            
            BufferedWriter bg = new BufferedWriter(new FileWriter(g));
            bg.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi a{text-decoration:none;color:#8b9b95;padding:0 0 0 0px;font-family: Tahoma;font-size: 8.5px;border: white;}");
            bg.close();

            File f = new File("riwayat.html");            
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(
                 teks.replaceAll("<head>","<head><link href=\"file.css\" rel=\"stylesheet\" type=\"text/css\" />").
                      replaceAll("<body>",
                                 "<body>"+
                                    "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>No.RM</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+NoRM.getText().trim()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Nama Pasien</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+NmPasien.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Alamat</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+Alamat.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Jenis Kelamin</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+Jk.getText().replaceAll("L","Laki-Laki").replaceAll("P","Perempuan")+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Tempat & Tanggal Lahir</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+TempatLahir.getText()+" "+TanggalLahir.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Ibu Kandung</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+IbuKandung.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Golongan Darah</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+GD.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Status Nikah</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+StatusNikah.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Agama</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+Agama.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Pendidikan Terakhir</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+Pendidikan.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Bahasa Dipakai</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+Bahasa.getText()+"</td>"+
                                       "</tr>"+
                                       "<tr class='isi'>"+ 
                                         "<td valign='top' width='20%'>Cacat Fisik</td>"+
                                         "<td valign='top' width='1%' align='center'>:</td>"+
                                         "<td valign='top' width='79%'>"+CacatFisik.getText()+"</td>"+
                                       "</tr>"+
                                    "</table>"            
                      ).
                      replaceAll((getClass().getResource("/picture/"))+"","./gambar/")
            );  
            bw.close();
            Desktop.getDesktop().browse(f.toURI());
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }   
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,126));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
}
