

package keuangan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import simrskhanza.DlgCariCaraBayar;

/**
 *
 * @author perpustakaan
 */
public final class KeuanganRincianPiutangPasien extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgCariCaraBayar penjab=new DlgCariCaraBayar(null,false);
    private double ttlSisaPiutang=0,ttlPiutang=0,ttlLaborat=0,ttlRadiologi=0,ttlOperasi=0,ttlObat=0,ttlTambahan=0,ttlPotongan=0,
        ttlKamar_Service=0,ttlRegistrasi=0,ttlHarian=0,ttlRetur_Obat=0,ttlResep_Pulang=0,ttlUangMuka=0,ttlCicilan=0,ttldiskon=0,
        ttltidakdibayar=0,ttlJasa_Tindakan=0;
    private String status="",pilihan="";
    private StringBuilder htmlContent;
    private int i=0;

    /** Creates new form DlgLhtBiaya
     * @param parent
     * @param modal */
    public KeuanganRincianPiutangPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat/Tagihan","Tgl.Piutang","No.RM","Pasien","Status","Jatuh Tempo","Cara Bayar","Registrasi","Tindakan","Obt+Emb+Tsl","Retur Obat",
            "Resep Pulang","Laborat","Radiologi","Potongan","Tambahan","Kamar+Service","Operasi","Harian","Total Piutang","Uang Muka","Cicilan",
            "Diskon Bayar","Tidak Terbayar","Sisa Piutang"
        }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
             Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, 
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                java.lang.Double.class
                
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbBangsal.setModel(tabMode);
        //tbBangsal.setDefaultRenderer(Object.class, new WarnaTable(jPanel2.getBackground(),tbBangsal.getBackground()));
        tbBangsal.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbBangsal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 25; i++) {
            TableColumn column = tbBangsal.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(80);
            }else if(i==5){
                column.setPreferredWidth(70);
            }else if(i==6){
                column.setPreferredWidth(130);
            }else{
                column.setPreferredWidth(80);
            }
        }
        tbBangsal.setDefaultRenderer(Object.class, new WarnaTable());

        TKd.setDocument(new batasInput((byte)20).getKata(TKd));
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
        
        penjab.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(penjab.getTable().getSelectedRow()!= -1){
                    kdpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),1).toString());
                    nmpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),2).toString());
                    tampil();
                }      
                kdpenjab.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {penjab.emptTeks();}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });   
        
        penjab.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    penjab.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TKd = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBangsal = new widget.Table();
        panelGlass5 = new widget.panelisi();
        label17 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel10 = new javax.swing.JLabel();
        LCount = new javax.swing.JLabel();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelisi4 = new widget.panelisi();
        label11 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        label18 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        label19 = new widget.Label();
        kdpenjab = new widget.TextBox();
        nmpenjab = new widget.TextBox();
        BtnSeek2 = new widget.Button();

        TKd.setForeground(new java.awt.Color(255, 255, 255));
        TKd.setName("TKd"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Rincian Piutang Pasien Per Cara Bayar Di Pendaftaran ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbBangsal.setToolTipText("");
        tbBangsal.setName("tbBangsal"); // NOI18N
        tbBangsal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBangsalMouseClicked(evt);
            }
        });
        tbBangsal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbBangsalKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbBangsal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label17.setText("Key Word :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass5.add(label17);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass5.add(TCari);

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
        panelGlass5.add(BtnCari);

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
        panelGlass5.add(BtnAll);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(50, 50, 50));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Belum Dibayar :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass5.add(jLabel10);

        LCount.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        LCount.setForeground(new java.awt.Color(50, 50, 50));
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(180, 23));
        panelGlass5.add(LCount);

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
        panelGlass5.add(BtnPrint);

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
        panelGlass5.add(BtnKeluar);

        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_END);

        panelisi4.setName("panelisi4"); // NOI18N
        panelisi4.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label11.setText("Tanggal Tagihan :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(100, 23));
        panelisi4.add(label11);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi4.add(Tgl1);

        label18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label18.setText("s.d.");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(30, 23));
        panelisi4.add(label18);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi4.add(Tgl2);

        label19.setText("Cara Bayar :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(75, 23));
        panelisi4.add(label19);

        kdpenjab.setName("kdpenjab"); // NOI18N
        kdpenjab.setPreferredSize(new java.awt.Dimension(80, 23));
        kdpenjab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpenjabKeyPressed(evt);
            }
        });
        panelisi4.add(kdpenjab);

        nmpenjab.setEditable(false);
        nmpenjab.setName("nmpenjab"); // NOI18N
        nmpenjab.setPreferredSize(new java.awt.Dimension(295, 23));
        panelisi4.add(nmpenjab);

        BtnSeek2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek2.setMnemonic('3');
        BtnSeek2.setToolTipText("Alt+3");
        BtnSeek2.setName("BtnSeek2"); // NOI18N
        BtnSeek2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek2ActionPerformed(evt);
            }
        });
        BtnSeek2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek2KeyPressed(evt);
            }
        });
        panelisi4.add(BtnSeek2);

        internalFrame1.add(panelisi4, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnPrint.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {            
                File g = new File("file2.css");            
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                        ".isi td{border-right: 1px solid #e2e7dd;font: 11px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                        ".isi2 td{font: 11px tahoma;height:12px;background: #ffffff;color:#323232;}"+                    
                        ".isi3 td{border-right: 1px solid #e2e7dd;font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                        ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                );
                bg.close();

                File f;            
                BufferedWriter bw; 

                pilihan = (String)JOptionPane.showInputDialog(null,"Silahkan pilih laporan..!","Pilihan Cetak",JOptionPane.QUESTION_MESSAGE,null,new Object[]{"Laporan 1 (HTML)","Laporan 2 (WPS)","Laporan 3 (CSV)","Laporan 4 (Jasper)"},"Laporan 1 (HTML)");
                switch (pilihan) {
                    case "Laporan 1 (HTML)":
                            htmlContent = new StringBuilder();
                            htmlContent.append(                             
                                "<tr class='isi'>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.Rawat/Tagihan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tgl.Piutang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No. RM</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Pasien</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Status</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Jatuh Tempo</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Cara Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Registrasi</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tindakan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Obt+Emb+Tsl</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Retur Obat</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Resep Pulang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Laborat</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Radiologi</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Potongan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tambahan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Kamar+Service</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Operasi</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Harian</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Total Piutang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Uang Muka</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Cicilan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Diskon Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tidak Terbayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Sisa Piutang</td>"+
                                "</tr>"
                            ); 
                            for(i=0;i<tabMode.getRowCount();i++){  
                                htmlContent.append(                             
                                    "<tr class='isi'>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,0)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,1)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,2)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,3)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,4)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,5)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,6)+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,7).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,8).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,9).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,10).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,11).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,12).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,13).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,14).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,15).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,16).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,17).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,18).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,19).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,20).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,21).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,22).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,23).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,24).toString()))+"</td>"+
                                    "</tr>"
                                ); 
                            }            

                            f = new File("PiutangRalan.html");            
                            bw = new BufferedWriter(new FileWriter(f));            
                            bw.write("<html>"+
                                        "<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /></head>"+
                                        "<body>"+
                                            "<table width='1950px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                                "<tr class='isi2'>"+
                                                    "<td valign='top' align='center'>"+
                                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                                        "<font size='2' face='Tahoma'>REKAP PEMBAYARAN RAWAT JALAN PERIODE "+Tgl1.getSelectedItem()+" s.d. "+Tgl2.getSelectedItem()+"<br><br></font>"+        
                                                    "</td>"+
                                               "</tr>"+
                                            "</table>"+
                                            "<table width='1950px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                                htmlContent.toString()+
                                            "</table>"+
                                        "</body>"+                   
                                     "</html>"
                            );

                            bw.close();                         
                            Desktop.getDesktop().browse(f.toURI());
                        break;
                    case "Laporan 2 (WPS)":
                            htmlContent = new StringBuilder();
                            htmlContent.append(                             
                                "<tr class='isi'>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.Rawat/Tagihan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tgl.Piutang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.RM</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Pasien</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Status</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Jatuh Tempo</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Cara Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Registrasi</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tindakan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Obt+Emb+Tsl</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Retur Obat</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Resep Pulang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Laborat</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Radiologi</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Potongan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tambahan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Kamar+Service</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Operasi</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Harian</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Total Piutang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Uang Muka</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Cicilan</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Diskon Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tidak Terbayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Sisa Piutang</td>"+
                                "</tr>"
                            ); 
                            for(i=0;i<tabMode.getRowCount();i++){  
                                htmlContent.append(                             
                                    "<tr class='isi'>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,0)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,1)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,2)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,3)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,4)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,5)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,6)+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,7).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,8).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,9).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,10).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,11).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,12).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,13).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,14).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,15).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,16).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,17).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,18).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,19).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,20).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,21).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,22).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,23).toString()))+"</td>"+
                                        "<td valign='top' align='right'>"+Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i,24).toString()))+"</td>"+
                                    "</tr>"
                                ); 
                            }            

                            f = new File("PiutangRalan.wps");            
                            bw = new BufferedWriter(new FileWriter(f));            
                            bw.write("<html>"+
                                        "<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /></head>"+
                                        "<body>"+
                                            "<table width='1950px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                                "<tr class='isi2'>"+
                                                    "<td valign='top' align='center'>"+
                                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                                        "<font size='2' face='Tahoma'>DETAIL JM DOKTER PERIODE "+Tgl1.getSelectedItem()+" s.d. "+Tgl2.getSelectedItem()+"<br><br></font>"+        
                                                    "</td>"+
                                               "</tr>"+
                                            "</table>"+
                                            "<table width='1950px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                                htmlContent.toString()+
                                            "</table>"+
                                        "</body>"+                   
                                     "</html>"
                            );

                            bw.close();                         
                            Desktop.getDesktop().browse(f.toURI());
                        break;
                    case "Laporan 3 (CSV)":
                            htmlContent = new StringBuilder();
                            htmlContent.append(                             
                                "\"No.Rawat/Tagihan\";\"Tgl.Piutang\";\"No.RM\";\"Pasien\";\"Status\";\"Jatuh Tempo\";\"Cara Bayar\";\"Registrasi\";\"Tindakan\";\"Obt+Emb+Tsl\";\"Retur Obat\";\"Resep Pulang\";\"Laborat\";\"Radiologi\";\"Potongan\";\"Tambahan\";\"Kamar+Service\";\"Operasi\";\"Harian\";\"Total Piutang\";\"Uang Muka\";\"Cicilan\";\"Diskon Bayar\";\"Tidak Terbayar\";\"Sisa Piutang\"\n"
                            ); 
                            for(i=0;i<tabMode.getRowCount();i++){  
                                htmlContent.append(                             
                                    "\""+tabMode.getValueAt(i,0)+"\";\""+tabMode.getValueAt(i,1)+"\";\""+tabMode.getValueAt(i,2)+"\";\""+tabMode.getValueAt(i,3)+"\";\""+tabMode.getValueAt(i,4)+"\";\""+tabMode.getValueAt(i,5)+"\";\""+tabMode.getValueAt(i,6)+"\";\""+tabMode.getValueAt(i,7)+"\";\""+tabMode.getValueAt(i,8)+"\";\""+tabMode.getValueAt(i,9)+"\";\""+tabMode.getValueAt(i,10)+"\";\""+tabMode.getValueAt(i,11)+"\";\""+tabMode.getValueAt(i,12)+"\";\""+tabMode.getValueAt(i,13)+"\";\""+tabMode.getValueAt(i,14)+"\";\""+tabMode.getValueAt(i,15)+"\";\""+tabMode.getValueAt(i,16)+"\";\""+tabMode.getValueAt(i,17)+"\";\""+tabMode.getValueAt(i,18)+"\";\""+tabMode.getValueAt(i,19)+"\";\""+tabMode.getValueAt(i,20)+"\";\""+tabMode.getValueAt(i,21)+"\";\""+tabMode.getValueAt(i,22)+"\";\""+tabMode.getValueAt(i,23)+"\";\""+tabMode.getValueAt(i,24)+"\"\n"
                                ); 
                            }            

                            f = new File("PiutangRalan.csv");            
                            bw = new BufferedWriter(new FileWriter(f));            
                            bw.write(htmlContent.toString());

                            bw.close();                         
                            Desktop.getDesktop().browse(f.toURI());
                        break; 
                    case "Laporan 4 (Jasper)":
                        Sequel.deleteTemporary();
                        int row = tabMode.getRowCount();
                        for (int i = 0; i < row; i++) {
                            Sequel.temporary(
                                String.valueOf(i + 1),
                                tabMode.getValueAt(i, 0).toString(),
                                tabMode.getValueAt(i, 1).toString(),
                                tabMode.getValueAt(i, 2).toString(),
                                tabMode.getValueAt(i, 3).toString(),
                                tabMode.getValueAt(i, 4).toString(),
                                tabMode.getValueAt(i, 5).toString(),
                                tabMode.getValueAt(i, 6).toString(),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 7).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 8).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 9).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 10).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 11).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 12).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 13).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 14).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 15).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 16).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 17).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 18).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 19).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 20).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 21).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 22).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 23).toString())),
                                Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(i, 24).toString()))
                            );
                        }

                        Map<String, Object> param = new HashMap<>();
                        param.put("namars", akses.getnamars());
                        param.put("alamatrs", akses.getalamatrs());
                        param.put("kotars", akses.getkabupatenrs());
                        param.put("propinsirs", akses.getpropinsirs());
                        param.put("kontakrs", akses.getkontakrs());
                        param.put("emailrs", akses.getemailrs());
                        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                        Valid.MyReportqry("rptRincianPiutangPasien.jasper", "report", "::[ Rekap Piutang Masuk ]::", "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
                        break; 
                }                 
            } catch (Exception e) {
            }     
            this.setCursor(Cursor.getDefaultCursor());
        }
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnAll,TKd);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        kdpenjab.setText("");
        nmpenjab.setText("");
        tampil();

}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbBangsalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBangsalMouseClicked
        if(tabMode.getRowCount()!=0){
            if(evt.getClickCount()==2){
                int kolom=tbBangsal.getSelectedColumn();
                if(kolom==1){
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if(akses.getbayar_piutang()==true){
                        DlgBayarPiutang bayarpiutang=new DlgBayarPiutang(null,false);
                        bayarpiutang.emptTeks();
                        String norm=Sequel.cariIsi("select piutang_pasien.no_rkm_medis from piutang_pasien where piutang_pasien.no_rawat='"+tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString() +"'");
                        String nama=Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='"+norm+"'"); 
                        bayarpiutang.setData(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString(),norm,nama);                   
                        bayarpiutang.tampil();  
                        bayarpiutang.setSize(this.getWidth()-20,this.getHeight()-20);
                        bayarpiutang.setLocationRelativeTo(this);
                        bayarpiutang.setVisible(true);
                        this.setCursor(Cursor.getDefaultCursor());
                    }                        
                }else if(kolom==0){
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    status=Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?",tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString());   
                    if(status.equals("Ralan")){
                        DlgBilingRalan billing=new DlgBilingRalan(null,false);
                        billing.TNoRw.setText(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString());
                        billing.isCek();
                        billing.isRawat();
                        if(Sequel.cariInteger("select count(piutang_pasien.no_rawat) from piutang_pasien where piutang_pasien.no_rawat=?",billing.TNoRw.getText())>0){
                            billing.setPiutang();
                        }
                        billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                        billing.setLocationRelativeTo(internalFrame1);
                        billing.setVisible(true); 
                    }else if(status.equals("Ranap")){
                        DlgBilingRanap billing=new DlgBilingRanap(null,false);
                        billing.TNoRw.setText(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString());            
                        billing.isCek();
                        billing.isRawat();
                        billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                        billing.setLocationRelativeTo(internalFrame1);
                        billing.setVisible(true); 
                    }
                    this.setCursor(Cursor.getDefaultCursor());
                }
            }
        }
}//GEN-LAST:event_tbBangsalMouseClicked

    private void tbBangsalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbBangsalKeyPressed
        if(tabMode.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                int kolom=tbBangsal.getSelectedColumn();
                if(kolom==1){
                    if(akses.getbayar_piutang()==true){
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        DlgBayarPiutang bayarpiutang=new DlgBayarPiutang(null,false);
                        bayarpiutang.emptTeks();
                        String norm=Sequel.cariIsi("select piutang_pasien.no_rkm_medis from piutang_pasien where piutang_pasien.no_rawat='"+tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString()+"'");
                        String nama=Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='"+norm+"'");
                        bayarpiutang.setData(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString(),norm,nama);
                        bayarpiutang.tampil();  
                        bayarpiutang.setSize(this.getWidth()-20,this.getHeight()-20);
                        bayarpiutang.setLocationRelativeTo(this);
                        bayarpiutang.setVisible(true);
                        this.setCursor(Cursor.getDefaultCursor());
                    }                        
                }else if(kolom==0){
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    status=Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?",tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString());   
                    if(status.equals("Ralan")){
                        DlgBilingRalan billing=new DlgBilingRalan(null,false);
                        billing.TNoRw.setText(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString());
                        billing.isCek();
                        billing.isRawat();
                        if(Sequel.cariInteger("select count(piutang_pasien.no_rawat) from piutang_pasien where piutang_pasien.no_rawat=?",billing.TNoRw.getText())>0){
                            billing.setPiutang();
                        }
                        billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                        billing.setLocationRelativeTo(internalFrame1);
                        billing.setVisible(true); 
                    }else if(status.equals("Ranap")){
                        DlgBilingRanap billing=new DlgBilingRanap(null,false);
                        billing.TNoRw.setText(tbBangsal.getValueAt(tbBangsal.getSelectedRow(),0).toString());            
                        billing.isCek();
                        billing.isRawat();
                        billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                        billing.setLocationRelativeTo(internalFrame1);
                        billing.setVisible(true); 
                    }
                    this.setCursor(Cursor.getDefaultCursor());
                }               
            }
        }
}//GEN-LAST:event_tbBangsalKeyPressed

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
            tampil();
        }else{
            Valid.pindah(evt, TKd, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void kdpenjabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpenjabKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select penjab.png_jawab from penjab where penjab.kd_pj=?", nmpenjab,kdpenjab.getText());
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnAll.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Tgl2.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnSeek2ActionPerformed(null);
        }
    }//GEN-LAST:event_kdpenjabKeyPressed

    private void BtnSeek2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek2ActionPerformed
        penjab.isCek();
        penjab.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penjab.setLocationRelativeTo(internalFrame1);
        penjab.setAlwaysOnTop(false);
        penjab.setVisible(true);
    }//GEN-LAST:event_BtnSeek2ActionPerformed

    private void BtnSeek2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek2KeyPressed
        //Valid.pindah(evt,DTPCari2,TCari);
    }//GEN-LAST:event_BtnSeek2KeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            KeuanganRincianPiutangPasien dialog = new KeuanganRincianPiutangPasien(new javax.swing.JFrame(), true);
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
    private widget.Button BtnPrint;
    private widget.Button BtnSeek2;
    private javax.swing.JLabel LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TKd;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel10;
    private widget.TextBox kdpenjab;
    private widget.Label label11;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.TextBox nmpenjab;
    private widget.panelisi panelGlass5;
    private widget.panelisi panelisi4;
    private widget.Table tbBangsal;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select piutang_pasien.no_rawat, piutang_pasien.tgl_piutang, piutang_pasien.no_rkm_medis, " +
                "pasien.nm_pasien, piutang_pasien.status, piutang_pasien.tgltempo, penjab.png_jawab, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Registrasi') as billing_registrasi, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status in ('Ranap Dokter', 'Ranap Dokter Paramedis', 'Ranap Paramedis', 'Ralan Dokter', 'Ralan Dokter Paramedis', 'Ralan Paramedis')) as billing_jasa, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Obat') as billing_obat, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Retur Obat') as billing_obat_retur, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Resep Pulang') as billing_obat_pulang, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Laborat') as billing_laborat, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Radiologi') as billing_radiologi, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Potongan') as billing_potongan, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Tambahan') as billing_tambahan, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status in ('Kamar', 'Service')) as billing_kamar_service, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Operasi') as billing_operasi, " +
                "(select ifnull(round(sum(billing.totalbiaya), 2), 0) from billing where billing.no_rawat = piutang_pasien.no_rawat and billing.status = 'Harian') as billing_harian, " +
                "round(piutang_pasien.totalpiutang, 2) as totalpiutang, " +
                "round(piutang_pasien.uangmuka, 2) as uangmuka, " +
                "(select ifnull(round(sum(bayar_piutang.besar_cicilan), 2), 0) from bayar_piutang where bayar_piutang.no_rawat = piutang_pasien.no_rawat) as besar_cicilan, " +
                "(select ifnull(round(sum(bayar_piutang.diskon_piutang), 2), 0) from bayar_piutang where bayar_piutang.no_rawat = piutang_pasien.no_rawat) as diskon_piutang, " +
                "(select ifnull(round(sum(bayar_piutang.tidak_terbayar), 2), 0) from bayar_piutang where bayar_piutang.no_rawat = piutang_pasien.no_rawat) as tidak_terbayar, " +
                "round(piutang_pasien.sisapiutang, 2) as sisapiutang " +
                "from piutang_pasien " +
                "inner join pasien on piutang_pasien.no_rkm_medis = pasien.no_rkm_medis " +
                "inner join reg_periksa on piutang_pasien.no_rawat = reg_periksa.no_rawat " +
                "inner join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
                "where piutang_pasien.tgl_piutang between ? and ? " +
                "and penjab.png_jawab like ? " +
                (TCari.getText().isBlank() ? "" : 
                    "and (piutang_pasien.no_rawat like ? or piutang_pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or piutang_pasien.status like ?) ") +
                "order by piutang_pasien.tgl_piutang"
            );
            
            try {
                ps.setString(1, Valid.SetTgl(Tgl1.getSelectedItem().toString()));
                ps.setString(2, Valid.SetTgl(Tgl2.getSelectedItem().toString()));
                ps.setString(3, "%" + nmpenjab.getText() + "%");
                if (! TCari.getText().isBlank()) {
                    ps.setString(4, TCari.getText());
                    ps.setString(5, TCari.getText());
                    ps.setString(6, TCari.getText());
                    ps.setString(7, TCari.getText());
                }
                ttlRegistrasi = 0;
                ttlJasa_Tindakan = 0;
                ttlObat = 0;
                ttlRetur_Obat = 0;
                ttlResep_Pulang = 0;
                ttlLaborat = 0;
                ttlRadiologi = 0;
                ttlPotongan = 0;
                ttlTambahan = 0;
                ttlKamar_Service = 0;
                ttlOperasi = 0;
                ttlHarian = 0;
                ttlPiutang = 0;
                ttlUangMuka = 0;
                ttlCicilan = 0;
                ttldiskon = 0;
                ttltidakdibayar = 0;
                ttlSisaPiutang = 0;
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[] {
                        rs.getString("no_rawat"),
                        rs.getString("tgl_piutang"),
                        rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"),
                        rs.getString("status"),
                        rs.getString("tgltempo"),
                        rs.getString("png_jawab"),
                        rs.getDouble("billing_registrasi"),
                        rs.getDouble("billing_jasa"),
                        rs.getDouble("billing_obat"),
                        rs.getDouble("billing_obat_retur"),
                        rs.getDouble("billing_obat_pulang"),
                        rs.getDouble("billing_laborat"),
                        rs.getDouble("billing_radiologi"),
                        rs.getDouble("billing_potongan"),
                        rs.getDouble("billing_tambahan"),
                        rs.getDouble("billing_kamar_service"),
                        rs.getDouble("billing_operasi"),
                        rs.getDouble("billing_harian"),
                        rs.getDouble("totalpiutang"),
                        rs.getDouble("uangmuka"),
                        rs.getDouble("besar_cicilan"),
                        rs.getDouble("diskon_piutang"),
                        rs.getDouble("tidak_terbayar"),
                        (rs.getDouble("sisapiutang") - rs.getDouble("besar_cicilan") - rs.getDouble("diskon_piutang") - rs.getDouble("tidak_terbayar"))
                    });
                    ttlRegistrasi += rs.getDouble("billing_registrasi");
                    ttlJasa_Tindakan += rs.getDouble("billing_jasa");
                    ttlObat += rs.getDouble("billing_obat");
                    ttlRetur_Obat += rs.getDouble("billing_obat_retur");
                    ttlResep_Pulang += rs.getDouble("billing_obat_pulang");
                    ttlLaborat += rs.getDouble("billing_laborat");
                    ttlRadiologi += rs.getDouble("billing_radiologi");
                    ttlPotongan += rs.getDouble("billing_potongan");
                    ttlTambahan += rs.getDouble("billing_tambahan");
                    ttlKamar_Service += rs.getDouble("billing_kamar_service");
                    ttlOperasi += rs.getDouble("billing_operasi");
                    ttlHarian += rs.getDouble("billing_harian");
                    ttlPiutang += rs.getDouble("totalpiutang");
                    ttlUangMuka += rs.getDouble("uangmuka");
                    ttlCicilan += rs.getDouble("besar_cicilan");
                    ttldiskon += rs.getDouble("diskon_piutang");
                    ttltidakdibayar += rs.getDouble("tidak_terbayar");
                    ttlSisaPiutang += (rs.getDouble("sisapiutang") - rs.getDouble("besar_cicilan") - rs.getDouble("diskon_piutang") - rs.getDouble("tidak_terbayar"));
                }
                tabMode.addRow(new Object[] {
                    ">>Total", ":", "", "", "", "", "",
                    ttlRegistrasi,
                    ttlJasa_Tindakan,
                    ttlObat,
                    ttlRetur_Obat,
                    ttlResep_Pulang,
                    ttlLaborat,
                    ttlRadiologi,
                    ttlPotongan,
                    ttlTambahan,
                    ttlKamar_Service,
                    ttlOperasi,
                    ttlHarian,
                    ttlPiutang,
                    ttlUangMuka,
                    ttlCicilan,
                    ttldiskon,
                    ttltidakdibayar,
                    ttlSisaPiutang
                });
                
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
            
            LCount.setText(Valid.SetAngka(ttlSisaPiutang));
        } catch(Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void setNoRm(String norm, Date tgl) {
        TCari.setText(norm);
        Tgl1.setDate(tgl);
    }
}
