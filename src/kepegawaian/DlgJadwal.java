/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgJadwal.java
 *
 * Created on May 22, 2010, 10:25:16 PM
 */

package kepegawaian;
import bridging.ApiMobileJKN;
import bridging.BPJSCekReferensiPoliHFIS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import simrskhanza.DlgCariPoli;

/**
 *
 * @author dosen
 */
public class DlgJadwal extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final DefaultTableModel tabModeBPJS;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private ApiMobileJKN api=new ApiMobileJKN();
    private String URL="",link="",utc="",requestJson="";
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;

    /** Creates new form DlgJadwal
     * @param parent
     * @param modal */
    public DlgJadwal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8,1);
        setSize(628,674);

        Object[] row={"P","Kode Dokter","Nama Dokter","Hari Kerja","Jam Mulai","Jam Selesai","Poliklinik","Kuota", "Status", "kdpoli", "kddokterbpjs", "nmdokterbpjs", "kdpolibpjs", "nmpolibpjs", "kdpolispesialisbpjs", "nmpolispesialisbpjs"};
        tabMode=new DefaultTableModel(null,row){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                 java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbJadwal.setModel(tabMode);

        tbJadwal.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbJadwal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 16; i++) {
            TableColumn column = tbJadwal.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(90);
            }else if(i==2){
                column.setPreferredWidth(200);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(70);
            }else if(i==5){
                column.setPreferredWidth(70);
            }else if(i==6){
                column.setPreferredWidth(200);
            }else if(i==7){
                column.setPreferredWidth(50);
            } else if (i == 8) {
                column.setPreferredWidth(120);
            } else {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbJadwal.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        kddokter.setDocument(new batasInput((byte)20).getKata(kddokter));
        KdPoli.setDocument(new batasInput((byte)5).getKata(KdPoli));
        Kuota.setDocument(new batasInput((byte)4).getOnlyAngka(Kuota));
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
        
        poli.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(poli.getTable().getSelectedRow()!= -1){
                    KdPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),0).toString());
                    TPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),1).toString());
                    KdPoliBPJS.setText(Sequel.cariIsiSmc("select kd_poli_bpjs from maping_poli_bpjs where kd_poli_rs = ?", KdPoli.getText()));
                    NmPoliBPJS.setText(Sequel.cariIsiSmc("select nm_poli_bpjs from maping_poli_bpjs where kd_poli_rs = ?", KdPoli.getText()));
                } 
                KdPoli.requestFocus();
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
        isForm();
        
        tabModeBPJS=new DefaultTableModel(null,new String[]{
            "No.","Kode Subspesialis","Nama Subspesialis","Kode Poli","Nama Poli","Kode Dokter","Nama Dokter","Hari","Nama Hari","Libur","Jadwal","Kapasitas"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbKamar.setModel(tabModeBPJS);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 12; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(35);
            }else if(i==1){
                column.setPreferredWidth(100);
            }else if(i==2){
                column.setPreferredWidth(180);
            }else if(i==3){
                column.setPreferredWidth(80);
            }else if(i==4){
                column.setPreferredWidth(180);
            }else if(i==5){
                column.setPreferredWidth(90);
            }else if(i==6){
                column.setPreferredWidth(180);
            }else if(i==7){
                column.setPreferredWidth(30);
            }else if(i==8){
                column.setPreferredWidth(70);
            }else if(i==9){
                column.setPreferredWidth(35);
            }else if(i==10){
                column.setPreferredWidth(70);
            }else if(i==11){
                column.setPreferredWidth(60);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());
    }
    DlgCariDokter dokter=new DlgCariDokter(null,false);
    DlgCariPoli poli=new DlgCariPoli(null,false);

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WindowUpdate = new javax.swing.JDialog();
        internalFrame6 = new widget.InternalFrame();
        BtnCloseIn5 = new widget.Button();
        BtnSimpanUpdate = new widget.Button();
        jLabel21 = new widget.Label();
        KodePoliUpdate = new widget.TextBox();
        NmPoliUpdate = new widget.TextBox();
        jLabel22 = new widget.Label();
        KodeDokterUpdate = new widget.TextBox();
        NmDokterUpdate = new widget.TextBox();
        KodeSubspesialis = new widget.TextBox();
        NmSubspesialis = new widget.TextBox();
        jLabel23 = new widget.Label();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbJadwal = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelBiasa1 = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        nmdokter = new widget.TextBox();
        jLabel4 = new widget.Label();
        jLabel9 = new widget.Label();
        jLabel10 = new widget.Label();
        TPoli = new widget.TextBox();
        cmbHari = new widget.ComboBox();
        cmbJam1 = new widget.ComboBox();
        cmbMnt1 = new widget.ComboBox();
        jLabel11 = new widget.Label();
        cmbJam2 = new widget.ComboBox();
        cmbMnt2 = new widget.ComboBox();
        btnDokter = new widget.Button();
        kddokter = new widget.TextBox();
        KdPoli = new widget.TextBox();
        BtnPoli = new widget.Button();
        jLabel12 = new widget.Label();
        Kuota = new widget.TextBox();
        jLabel5 = new widget.Label();
        cmbStatus = new widget.ComboBox();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        panelBiasa2 = new widget.PanelBiasa();
        Scroll1 = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelGlass6 = new widget.panelisi();
        jLabel19 = new widget.Label();
        KdPoliBPJS = new widget.TextBox();
        NmPoliBPJS = new widget.TextBox();
        BtnPoliBPJS = new widget.Button();
        jLabel20 = new widget.Label();
        Tanggal = new widget.Tanggal();
        BtnCariBPJS = new widget.Button();
        BtnEdit1 = new widget.Button();

        WindowUpdate.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowUpdate.setAlwaysOnTop(true);
        WindowUpdate.setModal(true);
        WindowUpdate.setName("WindowUpdate"); // NOI18N
        WindowUpdate.setUndecorated(true);
        WindowUpdate.setResizable(false);

        internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 50, 50)), "::[ Update/Tambahkan Jadwal HFIS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame6.setName("internalFrame6"); // NOI18N
        internalFrame6.setLayout(null);

        BtnCloseIn5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn5.setMnemonic('U');
        BtnCloseIn5.setText("Tutup");
        BtnCloseIn5.setToolTipText("Alt+U");
        BtnCloseIn5.setName("BtnCloseIn5"); // NOI18N
        BtnCloseIn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn5ActionPerformed(evt);
            }
        });
        internalFrame6.add(BtnCloseIn5);
        BtnCloseIn5.setBounds(610, 120, 100, 30);

        BtnSimpanUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanUpdate.setMnemonic('S');
        BtnSimpanUpdate.setText("Simpan");
        BtnSimpanUpdate.setToolTipText("Alt+S");
        BtnSimpanUpdate.setName("BtnSimpanUpdate"); // NOI18N
        BtnSimpanUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanUpdateActionPerformed(evt);
            }
        });
        internalFrame6.add(BtnSimpanUpdate);
        BtnSimpanUpdate.setBounds(20, 120, 100, 30);

        jLabel21.setText("Poliklinik :");
        jLabel21.setName("jLabel21"); // NOI18N
        internalFrame6.add(jLabel21);
        jLabel21.setBounds(0, 30, 75, 23);

        KodePoliUpdate.setEditable(false);
        KodePoliUpdate.setHighlighter(null);
        KodePoliUpdate.setName("KodePoliUpdate"); // NOI18N
        internalFrame6.add(KodePoliUpdate);
        KodePoliUpdate.setBounds(79, 30, 65, 23);

        NmPoliUpdate.setEditable(false);
        NmPoliUpdate.setName("NmPoliUpdate"); // NOI18N
        internalFrame6.add(NmPoliUpdate);
        NmPoliUpdate.setBounds(146, 30, 181, 23);

        jLabel22.setText("Dokter :");
        jLabel22.setName("jLabel22"); // NOI18N
        internalFrame6.add(jLabel22);
        jLabel22.setBounds(352, 30, 60, 23);

        KodeDokterUpdate.setEditable(false);
        KodeDokterUpdate.setHighlighter(null);
        KodeDokterUpdate.setName("KodeDokterUpdate"); // NOI18N
        internalFrame6.add(KodeDokterUpdate);
        KodeDokterUpdate.setBounds(416, 30, 72, 23);

        NmDokterUpdate.setEditable(false);
        NmDokterUpdate.setName("NmDokterUpdate"); // NOI18N
        internalFrame6.add(NmDokterUpdate);
        NmDokterUpdate.setBounds(490, 30, 215, 23);

        KodeSubspesialis.setEditable(false);
        KodeSubspesialis.setHighlighter(null);
        KodeSubspesialis.setName("KodeSubspesialis"); // NOI18N
        internalFrame6.add(KodeSubspesialis);
        KodeSubspesialis.setBounds(79, 60, 65, 23);

        NmSubspesialis.setEditable(false);
        NmSubspesialis.setName("NmSubspesialis"); // NOI18N
        internalFrame6.add(NmSubspesialis);
        NmSubspesialis.setBounds(146, 60, 181, 23);

        jLabel23.setText("Spesialis :");
        jLabel23.setName("jLabel23"); // NOI18N
        internalFrame6.add(jLabel23);
        jLabel23.setBounds(0, 60, 75, 23);

        WindowUpdate.getContentPane().add(internalFrame6, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Jadwal Praktek ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJadwal.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbJadwal.setName("tbJadwal"); // NOI18N
        tbJadwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbJadwalMouseClicked(evt);
            }
        });
        tbJadwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbJadwalKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbJadwal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(null);

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
        BtnSimpan.setBounds(6, 10, 100, 30);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
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
        BtnBatal.setBounds(108, 10, 100, 30);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
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
        BtnHapus.setBounds(210, 10, 100, 30);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
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
        BtnEdit.setBounds(312, 10, 100, 30);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
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
        BtnPrint.setBounds(414, 10, 100, 30);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
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
        BtnKeluar.setBounds(518, 10, 100, 30);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(340, 23));
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
        BtnAll.setMnemonic('4');
        BtnAll.setToolTipText("Alt+4");
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(1023, 107));
        panelBiasa1.setLayout(null);

        jLabel3.setText("Dokter :");
        jLabel3.setName("jLabel3"); // NOI18N
        panelBiasa1.add(jLabel3);
        jLabel3.setBounds(0, 12, 70, 23);

        nmdokter.setEditable(false);
        nmdokter.setHighlighter(null);
        nmdokter.setName("nmdokter"); // NOI18N
        nmdokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nmdokterKeyPressed(evt);
            }
        });
        panelBiasa1.add(nmdokter);
        nmdokter.setBounds(191, 12, 443, 23);

        jLabel4.setText("Hari Kerja :");
        jLabel4.setName("jLabel4"); // NOI18N
        panelBiasa1.add(jLabel4);
        jLabel4.setBounds(0, 42, 70, 23);

        jLabel9.setText("Jam :");
        jLabel9.setName("jLabel9"); // NOI18N
        panelBiasa1.add(jLabel9);
        jLabel9.setBounds(165, 42, 36, 23);

        jLabel10.setText("Poliklinik :");
        jLabel10.setName("jLabel10"); // NOI18N
        panelBiasa1.add(jLabel10);
        jLabel10.setBounds(127, 71, 56, 23);

        TPoli.setEditable(false);
        TPoli.setHighlighter(null);
        TPoli.setName("TPoli"); // NOI18N
        TPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPoliKeyPressed(evt);
            }
        });
        panelBiasa1.add(TPoli);
        TPoli.setBounds(289, 72, 345, 23);

        cmbHari.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU", "AKHAD" }));
        cmbHari.setName("cmbHari"); // NOI18N
        cmbHari.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbHariItemStateChanged(evt);
            }
        });
        cmbHari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbHariKeyPressed(evt);
            }
        });
        panelBiasa1.add(cmbHari);
        cmbHari.setBounds(74, 42, 90, 23);

        cmbJam1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam1.setName("cmbJam1"); // NOI18N
        cmbJam1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJam1KeyPressed(evt);
            }
        });
        panelBiasa1.add(cmbJam1);
        cmbJam1.setBounds(204, 42, 62, 23);

        cmbMnt1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt1.setName("cmbMnt1"); // NOI18N
        cmbMnt1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMnt1KeyPressed(evt);
            }
        });
        panelBiasa1.add(cmbMnt1);
        cmbMnt1.setBounds(269, 42, 62, 23);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("s.d.");
        jLabel11.setName("jLabel11"); // NOI18N
        panelBiasa1.add(jLabel11);
        jLabel11.setBounds(334, 42, 25, 23);

        cmbJam2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam2.setName("cmbJam2"); // NOI18N
        cmbJam2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJam2KeyPressed(evt);
            }
        });
        panelBiasa1.add(cmbJam2);
        cmbJam2.setBounds(362, 42, 62, 23);

        cmbMnt2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt2.setName("cmbMnt2"); // NOI18N
        cmbMnt2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMnt2KeyPressed(evt);
            }
        });
        panelBiasa1.add(cmbMnt2);
        cmbMnt2.setBounds(427, 42, 62, 23);

        btnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokter.setMnemonic('1');
        btnDokter.setToolTipText("ALt+1");
        btnDokter.setName("btnDokter"); // NOI18N
        btnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnDokter);
        btnDokter.setBounds(637, 12, 28, 23);

        kddokter.setEditable(false);
        kddokter.setHighlighter(null);
        kddokter.setName("kddokter"); // NOI18N
        kddokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kddokterKeyPressed(evt);
            }
        });
        panelBiasa1.add(kddokter);
        kddokter.setBounds(74, 12, 115, 23);

        KdPoli.setEditable(false);
        KdPoli.setHighlighter(null);
        KdPoli.setName("KdPoli"); // NOI18N
        KdPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPoliKeyPressed(evt);
            }
        });
        panelBiasa1.add(KdPoli);
        KdPoli.setBounds(186, 72, 100, 23);

        BtnPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPoli.setMnemonic('2');
        BtnPoli.setToolTipText("ALt+2");
        BtnPoli.setName("BtnPoli"); // NOI18N
        BtnPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPoliActionPerformed(evt);
            }
        });
        panelBiasa1.add(BtnPoli);
        BtnPoli.setBounds(637, 72, 28, 23);

        jLabel12.setText("Kuota :");
        jLabel12.setName("jLabel12"); // NOI18N
        panelBiasa1.add(jLabel12);
        jLabel12.setBounds(0, 72, 70, 23);

        Kuota.setHighlighter(null);
        Kuota.setName("Kuota"); // NOI18N
        Kuota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KuotaKeyPressed(evt);
            }
        });
        panelBiasa1.add(Kuota);
        Kuota.setBounds(74, 72, 50, 23);

        jLabel5.setText("Status :");
        jLabel5.setName("jLabel5"); // NOI18N
        panelBiasa1.add(jLabel5);
        jLabel5.setBounds(492, 42, 50, 23);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "LIBUR NASIONAL", "TAP" }));
        cmbStatus.setSelectedIndex(1);
        cmbStatus.setName("cmbStatus"); // NOI18N
        cmbStatus.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbStatusItemStateChanged(evt);
            }
        });
        cmbStatus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbStatusKeyPressed(evt);
            }
        });
        panelBiasa1.add(cmbStatus);
        cmbStatus.setBounds(545, 42, 120, 23);

        internalFrame1.add(panelBiasa1, java.awt.BorderLayout.PAGE_START);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(670, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout(1, 1));

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAccor.setName("ChkAccor"); // NOI18N
        ChkAccor.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccorActionPerformed(evt);
            }
        });
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.WEST);

        panelBiasa2.setName("panelBiasa2"); // NOI18N
        panelBiasa2.setLayout(new java.awt.BorderLayout());

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbKamar.setAutoCreateRowSorter(true);
        tbKamar.setName("tbKamar"); // NOI18N
        Scroll1.setViewportView(tbKamar);

        panelBiasa2.add(Scroll1, java.awt.BorderLayout.CENTER);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 5, 9));

        jLabel19.setText("Poli BPJS :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(49, 23));
        panelGlass6.add(jLabel19);

        KdPoliBPJS.setEditable(false);
        KdPoliBPJS.setHighlighter(null);
        KdPoliBPJS.setName("KdPoliBPJS"); // NOI18N
        KdPoliBPJS.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass6.add(KdPoliBPJS);

        NmPoliBPJS.setEditable(false);
        NmPoliBPJS.setName("NmPoliBPJS"); // NOI18N
        NmPoliBPJS.setPreferredSize(new java.awt.Dimension(150, 23));
        panelGlass6.add(NmPoliBPJS);

        BtnPoliBPJS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPoliBPJS.setMnemonic('3');
        BtnPoliBPJS.setToolTipText("ALt+3");
        BtnPoliBPJS.setName("BtnPoliBPJS"); // NOI18N
        BtnPoliBPJS.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPoliBPJS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPoliBPJSActionPerformed(evt);
            }
        });
        panelGlass6.add(BtnPoliBPJS);

        jLabel20.setText("Tanggal :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass6.add(jLabel20);

        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass6.add(Tanggal);

        BtnCariBPJS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariBPJS.setMnemonic('6');
        BtnCariBPJS.setToolTipText("Alt+6");
        BtnCariBPJS.setName("BtnCariBPJS"); // NOI18N
        BtnCariBPJS.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariBPJS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariBPJSActionPerformed(evt);
            }
        });
        BtnCariBPJS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariBPJSKeyPressed(evt);
            }
        });
        panelGlass6.add(BtnCariBPJS);

        BtnEdit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/add-file-16x16.png"))); // NOI18N
        BtnEdit1.setMnemonic('G');
        BtnEdit1.setText("Update");
        BtnEdit1.setToolTipText("Alt+G");
        BtnEdit1.setName("BtnEdit1"); // NOI18N
        BtnEdit1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEdit1ActionPerformed(evt);
            }
        });
        BtnEdit1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEdit1KeyPressed(evt);
            }
        });
        panelGlass6.add(BtnEdit1);

        panelBiasa2.add(panelGlass6, java.awt.BorderLayout.PAGE_END);

        PanelAccor.add(panelBiasa2, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.EAST);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nmdokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nmdokterKeyPressed
        //Valid.pindah(evt,TKd,TSpek);
}//GEN-LAST:event_nmdokterKeyPressed

    private void TPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPoliKeyPressed
        //Valid.pindah(evt,TJns,BtnSimpan);
}//GEN-LAST:event_TPoliKeyPressed

    private void cmbHariItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbHariItemStateChanged
        // TODO add your handling code here:
}//GEN-LAST:event_cmbHariItemStateChanged

    private void cmbHariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbHariKeyPressed
        Valid.pindah(evt,kddokter,cmbJam1);
}//GEN-LAST:event_cmbHariKeyPressed

    private void cmbJam1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJam1KeyPressed
        Valid.pindah(evt,cmbHari,cmbMnt1);
}//GEN-LAST:event_cmbJam1KeyPressed

    private void cmbMnt1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMnt1KeyPressed
        Valid.pindah(evt,cmbJam1,cmbJam2);
}//GEN-LAST:event_cmbMnt1KeyPressed

    private void cmbJam2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJam2KeyPressed
        Valid.pindah(evt,cmbMnt1,cmbMnt2);
}//GEN-LAST:event_cmbJam2KeyPressed

    private void cmbMnt2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMnt2KeyPressed
        Valid.pindah(evt,cmbJam2,cmbStatus);
}//GEN-LAST:event_cmbMnt2KeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(nmdokter.getText().trim().equals("")){
            Valid.textKosong(kddokter,"Dokter");
        }else if(TPoli.getText().trim().equals("")){
            Valid.textKosong(KdPoli,"Poliklinik");
        }else if(Kuota.getText().trim().equals("")){
            Valid.textKosong(Kuota,"Kuota");
        }else{
            if(Sequel.menyimpantf("jadwal","'"+kddokter.getText()+"','"+cmbHari.getSelectedItem()+"','"+
                cmbJam1.getSelectedItem()+":"+cmbMnt1.getSelectedItem()+":00','"+
                cmbJam2.getSelectedItem()+":"+cmbMnt2.getSelectedItem()+":00','"+
                KdPoli.getText()+"','"+Kuota.getText()+"'","Jadwal")==true){
                tabMode.addRow(new Object[]{
                    false,kddokter.getText(),nmdokter.getText(),cmbHari.getSelectedItem().toString(),cmbJam1.getSelectedItem()+":"+cmbMnt1.getSelectedItem()+":00",
                    cmbJam2.getSelectedItem()+":"+cmbMnt2.getSelectedItem()+":00",TPoli.getText(),Double.parseDouble(Kuota.getText())
                });
                emptTeks();
                LCount.setText(""+tabMode.getRowCount());
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,KdPoli,BtnBatal);
        }
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
        for(int i=0;i<tbJadwal.getRowCount();i++){ 
            if(tbJadwal.getValueAt(i,0).toString().equals("true")){
                if(Sequel.queryutf("delete from jadwal where kd_dokter='"+tbJadwal.getValueAt(i,1).toString()+"' and hari_kerja='"+tbJadwal.getValueAt(i,3).toString()+"' "+
                              "and jam_mulai='"+tbJadwal.getValueAt(i,4).toString()+"'")==true){
                    tabMode.removeRow(i);
                    i--;
                }
            }
        } 
        emptTeks();
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(nmdokter.getText().trim().equals("")){
            Valid.textKosong(kddokter,"Dokter");
        }else if(TPoli.getText().trim().equals("")){
            Valid.textKosong(KdPoli,"Poliklinik");
        }else if(Kuota.getText().trim().equals("")){
            Valid.textKosong(Kuota,"Kuota");
        }else{
            if(tbJadwal.getSelectedRow()!= -1){
                if(Sequel.queryutf("update jadwal set jam_mulai='"+cmbJam1.getSelectedItem()+":"+cmbMnt1.getSelectedItem()+":00',"+
                        "jam_selesai='"+cmbJam2.getSelectedItem()+":"+cmbMnt2.getSelectedItem()+":00',"+
                        "kd_poli='"+KdPoli.getText()+"',kd_dokter='"+kddokter.getText()+"',hari_kerja='"+cmbHari.getSelectedItem()+"',kuota='"+Kuota.getText()+"' where "+
                        "kd_dokter='"+tbJadwal.getValueAt(tbJadwal.getSelectedRow(),1).toString()+"' "+
                        "and hari_kerja='"+tbJadwal.getValueAt(tbJadwal.getSelectedRow(),3).toString()+"' "+
                        "and jam_mulai='"+tbJadwal.getValueAt(tbJadwal.getSelectedRow(),4).toString()+"' "+
                        "and jam_selesai='"+tbJadwal.getValueAt(tbJadwal.getSelectedRow(),5).toString()+"'")==true){
                    tbJadwal.setValueAt(false,tbJadwal.getSelectedRow(),0);
                    tbJadwal.setValueAt(kddokter.getText(),tbJadwal.getSelectedRow(),1);
                    tbJadwal.setValueAt(nmdokter.getText(),tbJadwal.getSelectedRow(),2);
                    tbJadwal.setValueAt(cmbHari.getSelectedItem().toString(),tbJadwal.getSelectedRow(),3);
                    tbJadwal.setValueAt(cmbJam1.getSelectedItem()+":"+cmbMnt1.getSelectedItem()+":00",tbJadwal.getSelectedRow(),4);
                    tbJadwal.setValueAt(cmbJam2.getSelectedItem()+":"+cmbMnt2.getSelectedItem()+":00",tbJadwal.getSelectedRow(),5);
                    tbJadwal.setValueAt(TPoli.getText(),tbJadwal.getSelectedRow(),6);
                    tbJadwal.setValueAt(Double.parseDouble(Kuota.getText()),tbJadwal.getSelectedRow(),7);
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
        }else{Valid.pindah(evt,BtnPrint,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(! TCari.getText().trim().equals("")){
            BtnCariActionPerformed(evt);
        }
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
                Valid.MyReportqry("rptJadwal.jasper","report","::[ Jadwal Praktek Dokter ]::",
                        "select jadwal.kd_dokter,dokter.nm_dokter,jadwal.hari_kerja, "+
                        "jadwal.jam_mulai,jadwal.jam_selesai,poliklinik.nm_poli,jadwal.kuota "+
                        "from jadwal inner join poliklinik inner join dokter "+
                        "on jadwal.kd_dokter=dokter.kd_dokter "+
                        "and jadwal.kd_poli=poliklinik.kd_poli "+
                        "where jadwal.kd_dokter like '%"+TCari.getText()+"%' or "+
                        "dokter.nm_dokter like '%"+TCari.getText()+"%' or "+
                        "jadwal.hari_kerja like '%"+TCari.getText()+"%' or "+
                        "poliklinik.nm_poli like '%"+TCari.getText()+"%' "+
                        "order by jadwal.kd_dokter",param);
            
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
            Valid.pindah(evt, BtnCari,kddokter);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbJadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbJadwalMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbJadwalMouseClicked

    private void tbJadwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJadwalKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbJadwalKeyPressed

private void btnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
}//GEN-LAST:event_btnDokterActionPerformed

private void kddokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kddokterKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            nmdokter.setText(dokter.tampil3(kddokter.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnDokterActionPerformed(null);
        }else{            
            Valid.pindah(evt,TCari,cmbHari);
        }
}//GEN-LAST:event_kddokterKeyPressed

private void KdPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPoliKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnPoliActionPerformed(null);
        }else{            
            Valid.pindah(evt,Kuota,BtnSimpan);
        }
}//GEN-LAST:event_KdPoliKeyPressed

private void BtnPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPoliActionPerformed
        poli.isCek();
        poli.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        poli.setLocationRelativeTo(internalFrame1);
        poli.setVisible(true);
}//GEN-LAST:event_BtnPoliActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void KuotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KuotaKeyPressed
        Valid.pindah(evt,cmbStatus,KdPoli);
    }//GEN-LAST:event_KuotaKeyPressed

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        isForm();
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void BtnPoliBPJSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPoliBPJSActionPerformed
        BPJSCekReferensiPoliHFIS form=new BPJSCekReferensiPoliHFIS(null,false);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if(form.getTable().getSelectedRow()!= -1){   
                    KdPoliBPJS.setText(form.getTable().getValueAt(form.getTable().getSelectedRow(),1).toString());
                    NmPoliBPJS.setText(form.getTable().getValueAt(form.getTable().getSelectedRow(),2).toString());
                    KdPoliBPJS.requestFocus();
                }                  
            }
        });
        form.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    form.dispose();
                }
            }
        }); 
        form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
    }//GEN-LAST:event_BtnPoliBPJSActionPerformed

    private void BtnCariBPJSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariBPJSActionPerformed
        if(KdPoliBPJS.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Silahkan pilih poli terlebih dahulu...!!");
            BtnPoliBPJS.requestFocus();
        }else{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tampilBPJS();
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnCariBPJSActionPerformed

    private void BtnCariBPJSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariBPJSKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariBPJSActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnKeluar,BtnPrint);
        }
    }//GEN-LAST:event_BtnCariBPJSKeyPressed

    private void BtnEdit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEdit1ActionPerformed
        KodeSubspesialis.setText(KdPoliBPJS.getText());
        NmSubspesialis.setText(NmPoliBPJS.getText());
        KodePoliUpdate.setText(Sequel.cariIsiSmc("select maping_poli_bpjs.kd_poli_spesialis_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs = ?", KdPoliBPJS.getText()));
        NmPoliUpdate.setText(Sequel.cariIsiSmc("select maping_poli_bpjs.nm_poli_spesialis_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs = ?", KdPoliBPJS.getText()));
        KodeDokterUpdate.setText(Sequel.cariIsiSmc("select maping_dokter_dpjpvclaim.kd_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter = ?", kddokter.getText()));
        NmDokterUpdate.setText(Sequel.cariIsiSmc("select maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter = ?", kddokter.getText()));
        WindowUpdate.setSize(734, 166);
        WindowUpdate.setLocationRelativeTo(internalFrame1);
        WindowUpdate.setAlwaysOnTop(true);
        WindowUpdate.setVisible(true);
    }//GEN-LAST:event_BtnEdit1ActionPerformed

    private void BtnEdit1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEdit1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEdit1KeyPressed

    private void BtnCloseIn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseIn5ActionPerformed
        WindowUpdate.dispose();
    }//GEN-LAST:event_BtnCloseIn5ActionPerformed

    private void BtnSimpanUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanUpdateActionPerformed
        if(NmDokterUpdate.getText().trim().equals("")){
            Valid.textKosong(KodeDokterUpdate,"Dokter");
        }else if(NmPoliUpdate.getText().trim().equals("")){
            Valid.textKosong(KodePoliUpdate,"Poliklinik");
        }else{
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
                utc=String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("x-timestamp",utc);
                headers.add("x-signature",api.getHmac(utc));
                headers.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());
                requestJson ="{" +
                "\"kodepoli\": \""+KodePoliUpdate.getText()+"\"," +
                "\"kodesubspesialis\": \""+KodeSubspesialis.getText()+"\"," +
                "\"kodedokter\": "+KodeDokterUpdate.getText()+"," +
                "\"jadwal\": [" +
                "{" +
                "\"hari\": \""+cmbHari.getSelectedItem().toString().substring(0,1)+"\"," +
                "\"buka\": \""+cmbJam1.getSelectedItem().toString()+":"+cmbMnt1.getSelectedItem().toString()+"\"," +
                "\"tutup\": \""+cmbJam2.getSelectedItem().toString()+":"+cmbMnt2.getSelectedItem().toString()+"\"" +
                "}" +
                "]" +
                "}";
                requestEntity = new HttpEntity(requestJson,headers);
                URL = link+"/jadwaldokter/updatejadwaldokter";
                System.out.println(URL);
                //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                nameNode = root.path("metadata");
                if(nameNode.path("code").asText().equals("200")){
                    tampil();
                }else {
                    JOptionPane.showMessageDialog(null,nameNode.path("message").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi : "+ex);
                if(ex.toString().contains("UnknownHostException")){
                    JOptionPane.showMessageDialog(rootPane,"Koneksi ke server BPJS terputus...!");
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanUpdateActionPerformed

    private void cmbStatusItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStatusItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusItemStateChanged

    private void cmbStatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbStatusKeyPressed
        Valid.pindah(evt, cmbMnt2,Kuota);
    }//GEN-LAST:event_cmbStatusKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgJadwal dialog = new DlgJadwal(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCariBPJS;
    private widget.Button BtnCloseIn5;
    private widget.Button BtnEdit;
    private widget.Button BtnEdit1;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPoli;
    private widget.Button BtnPoliBPJS;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpanUpdate;
    private widget.CekBox ChkAccor;
    private widget.TextBox KdPoli;
    private widget.TextBox KdPoliBPJS;
    private widget.TextBox KodeDokterUpdate;
    private widget.TextBox KodePoliUpdate;
    private widget.TextBox KodeSubspesialis;
    private widget.TextBox Kuota;
    private widget.Label LCount;
    private widget.TextBox NmDokterUpdate;
    private widget.TextBox NmPoliBPJS;
    private widget.TextBox NmPoliUpdate;
    private widget.TextBox NmSubspesialis;
    private widget.PanelBiasa PanelAccor;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.TextBox TCari;
    private widget.TextBox TPoli;
    private widget.Tanggal Tanggal;
    private javax.swing.JDialog WindowUpdate;
    private widget.Button btnDokter;
    private widget.ComboBox cmbHari;
    private widget.ComboBox cmbJam1;
    private widget.ComboBox cmbJam2;
    private widget.ComboBox cmbMnt1;
    private widget.ComboBox cmbMnt2;
    private widget.ComboBox cmbStatus;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame6;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox kddokter;
    private widget.TextBox nmdokter;
    private widget.PanelBiasa panelBiasa1;
    private widget.PanelBiasa panelBiasa2;
    private widget.panelisi panelGlass6;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJadwal;
    private widget.Table tbKamar;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select jadwal.kd_dokter, dokter.nm_dokter, jadwal.hari_kerja, jadwal.jam_mulai, jadwal.jam_selesai, " +
            "poliklinik.nm_poli, jadwal.kuota, jadwal.flag, jadwal.kd_poli, maping_dokter_dpjpvclaim.kd_dokter_bpjs, " +
            "maping_dokter_dpjpvclaim.nm_dokter_bpjs, maping_poli_bpjs.kd_poli_bpjs, maping_poli_bpjs.nm_poli_bpjs, " +
            "maping_poli_bpjs.kd_poli_spesialis_bpjs, maping_poli_bpjs.nm_poli_spesialis_bpjs from jadwal join " +
            "poliklinik on jadwal.kd_poli = poliklinik.kd_poli join dokter on jadwal.kd_dokter = dokter.kd_dokter " +
            "left join maping_dokter_dpjpvclaim on jadwal.kd_dokter = maping_dokter_dpjpvclaim.kd_dokter " +
            "left join maping_poli_bpjs on jadwal.kd_poli = maping_poli_bpjs.kd_poli_rs where " +
            "jadwal.kd_dokter like ? or jadwal.kd_poli like ? or jadwal.hari_kerja like ? or " +
            "dokter.nm_dokter like ? or poliklinik.nm_poli like ? order by jadwal.kd_dokter"
        )) {
            ps.setString(1, "%" + TCari.getText().trim() + "%");
            ps.setString(2, "%" + TCari.getText().trim() + "%");
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[] {
                        false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getDouble(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
                        rs.getString(13), rs.getString(14), rs.getString(15)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        kddokter.setText("");
        nmdokter.setText("");
        KdPoli.setText("");
        TPoli.setText("");
        Kuota.setText("0");
        KdPoliBPJS.setText("");
        NmPoliBPJS.setText("");
        KodeSubspesialis.setText("");
        NmSubspesialis.setText("");
        KodePoliUpdate.setText("");
        NmPoliUpdate.setText("");
        KodeDokterUpdate.setText("");
        NmDokterUpdate.setText("");
        cmbHari.setSelectedItem("SENIN");
        cmbStatus.setSelectedItem("-");
        cmbJam1.setSelectedItem("00");
        cmbJam2.setSelectedItem("00");
        cmbMnt1.setSelectedItem("00");
        cmbMnt2.setSelectedItem("00");
        kddokter.requestFocus();
    }

    private void getData() {
        int row=tbJadwal.getSelectedRow();
        if(row!= -1){
            kddokter.setText(tabMode.getValueAt(row,1).toString());
            nmdokter.setText(tabMode.getValueAt(row,2).toString());
            cmbHari.setSelectedItem(tabMode.getValueAt(row,3).toString());
            cmbJam1.setSelectedItem(tabMode.getValueAt(row,4).toString().substring(0,2));
            cmbMnt1.setSelectedItem(tabMode.getValueAt(row,4).toString().substring(3,5));
            cmbJam2.setSelectedItem(tabMode.getValueAt(row,5).toString().substring(0,2));
            cmbMnt2.setSelectedItem(tabMode.getValueAt(row,5).toString().substring(3,5));
            KdPoli.setText(tabMode.getValueAt(row, 9).toString());
            TPoli.setText(tabMode.getValueAt(row,6).toString());
            Kuota.setText(Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(row,7).toString())));
            cmbStatus.setSelectedItem(tabMode.getValueAt(row, 8).toString());
            KodeDokterUpdate.setText(tabMode.getValueAt(row, 10).toString());
            NmDokterUpdate.setText(tabMode.getValueAt(row, 11).toString());
            KdPoliBPJS.setText(tabMode.getValueAt(row,12).toString());
            NmPoliBPJS.setText(tabMode.getValueAt(row,13).toString());
            KodeSubspesialis.setText(tabMode.getValueAt(row, 12).toString());
            NmSubspesialis.setText(tabMode.getValueAt(row, 13).toString());
            KodePoliUpdate.setText(tabMode.getValueAt(row, 14).toString());
            NmPoliUpdate.setText(tabMode.getValueAt(row, 15).toString());
        }
    }

    private void isForm(){
        if(ChkAccor.isSelected()==true){
            ChkAccor.setVisible(false);
            panelBiasa2.setVisible(true);
            PanelAccor.setPreferredSize(new Dimension(700,HEIGHT));
            ChkAccor.setVisible(true);
        }else if(ChkAccor.isSelected()==false){    
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15,HEIGHT));
            panelBiasa2.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }    
    
    private void tampilBPJS() {
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
            requestEntity = new HttpEntity(headers);
            URL = link + "/jadwaldokter/kodepoli/" + KdPoli.getText() + "/tanggal/" + Valid.SetTgl(Tanggal.getSelectedItem() + "");
            System.out.println(URL);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metadata");
            if (nameNode.path("code").asText().equals("200")) {
                Valid.tabelKosong(tabMode);
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                //response = root.path("response");
                if (response.isArray()) {
                    int i = 1;
                    for (JsonNode list : response) {
                        tabMode.addRow(new Object[] {
                            i + ".", list.path("kodesubspesialis").asText(), list.path("namasubspesialis").asText(),
                            list.path("kodepoli").asText(), list.path("namapoli").asText(),
                            list.path("kodedokter").asText(), list.path("namadokter").asText(),
                            list.path("hari").asText(), list.path("namahari").asText(),
                            list.path("libur").asText(), list.path("jadwal").asText(),
                            list.path("kapasitaspasien").asText()
                        });
                        i++;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }
}
