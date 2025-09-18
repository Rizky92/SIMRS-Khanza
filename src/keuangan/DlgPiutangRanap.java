/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgLhtBiaya.java
 *
 * Created on 12 Jul 10, 16:21:34
 */

package keuangan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import simrskhanza.DlgCariBangsal;
import simrskhanza.DlgCariCaraBayar;

/**
 *
 * @author perpustakaan
 */
public final class DlgPiutangRanap extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private final DlgCariCaraBayar penjab=new DlgCariCaraBayar(null,false);
    private final DlgCariBangsal bangsal=new DlgCariBangsal(null,false);
    private String pilihan="";
    private StringBuilder htmlContent;
    private boolean ceksukses = false;

    /** Creates new form DlgLhtBiaya
     * @param parent
     * @param modal */
    public DlgPiutangRanap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);

        Object[] rowRwJlDr={"Tgl.Pulang","No.Rawat","No.Nota","No.RM","Nama Pasien","Jenis Bayar","Perujuk","Registrasi","Tindakan","Obt+Emb+Tsl","Retur Obat","Resep Pulang",
                            "Laborat","Radiologi","Potongan","Tambahan","Kamar+Service","Operasi","Harian","Total","Ekses","Sudah Dibayar","Diskon Bayar",
                            "Tidak Terbayar","Sisa","Nama Kamar"};
        tabMode=new DefaultTableModel(null,rowRwJlDr){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbBangsal.setModel(tabMode);
        //tbBangsal.setDefaultRenderer(Object.class, new WarnaTable(jPanel2.getBackground(),tbBangsal.getBackground()));
        tbBangsal.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbBangsal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 26; i++) {
            TableColumn column = tbBangsal.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(70);
            }else if(i==1){
                column.setPreferredWidth(103);
            }else if(i==2){
                column.setPreferredWidth(110);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(100);
            }else if(i==6){
                column.setPreferredWidth(100);
            }else if(i==19){
                column.setPreferredWidth(100);
            }else if(i==25){
                column.setPreferredWidth(220);
            }else{
                column.setPreferredWidth(85);
            }
        }
        tbBangsal.setDefaultRenderer(Object.class, new WarnaTable() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column > 6 && column < 25) {
                    c.setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    c.setHorizontalAlignment(JLabel.LEFT);
                }

                return c;
            }
        });

        TKd.setDocument(new batasInput((byte)20).getKata(TKd));

        penjab.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if(penjab.getTable().getSelectedRow()!= -1){
                    kdpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),1).toString());
                    nmpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),2).toString());
                    tampil();
                }
            }

            @Override
            public void windowActivated(WindowEvent e) {
                penjab.emptTeks();
            }
        });

        penjab.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    penjab.dispose();
                }
            }
        });

        bangsal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (bangsal.getTable().getSelectedRow() > -1) {
                    KdKamar.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(), 0).toString());
                    NmKamar.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(), 1).toString());
                    tampil();
                }
            }

            @Override
            public void windowActivated(WindowEvent e) {
                bangsal.emptTeks();
            }
        });

        bangsal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    bangsal.dispose();
                }
            }
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
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnBilling = new javax.swing.JMenuItem();
        kdpenjab = new widget.TextBox();
        KdKamar = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBangsal = new widget.Table();
        panelGlass5 = new widget.panelisi();
        label11 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        label18 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        BtnCari1 = new widget.Button();
        BtnAll = new widget.Button();
        label10 = new widget.Label();
        LCount2 = new widget.Label();
        jLabel10 = new javax.swing.JLabel();
        LCount = new javax.swing.JLabel();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelisi4 = new widget.panelisi();
        label19 = new widget.Label();
        StatusLunas = new widget.ComboBox();
        label17 = new widget.Label();
        nmpenjab = new widget.TextBox();
        BtnSeek2 = new widget.Button();
        label20 = new widget.Label();
        NmKamar = new widget.TextBox();
        BtnKamar = new widget.Button();

        TKd.setForeground(new java.awt.Color(255, 255, 255));
        TKd.setName("TKd"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnBilling.setBackground(new java.awt.Color(255, 255, 254));
        MnBilling.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnBilling.setForeground(new java.awt.Color(50, 50, 50));
        MnBilling.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnBilling.setText("Billing/Pembayaran Pasien");
        MnBilling.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnBilling.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnBilling.setName("MnBilling"); // NOI18N
        MnBilling.setPreferredSize(new java.awt.Dimension(250, 28));
        MnBilling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnBillingActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnBilling);

        kdpenjab.setName("kdpenjab"); // NOI18N
        kdpenjab.setPreferredSize(new java.awt.Dimension(70, 23));
        kdpenjab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpenjabKeyPressed(evt);
            }
        });

        KdKamar.setEditable(false);
        KdKamar.setName("KdKamar"); // NOI18N
        KdKamar.setPreferredSize(new java.awt.Dimension(60, 23));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Piutang Pasien Ranap ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbBangsal.setComponentPopupMenu(jPopupMenu1);
        tbBangsal.setName("tbBangsal"); // NOI18N
        tbBangsal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbBangsalMousePressed(evt);
            }
        });
        Scroll.setViewportView(tbBangsal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label11.setText("Tgl.Pulang :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass5.add(label11);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(Tgl1);

        label18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label18.setText("s.d.");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass5.add(label18);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(Tgl2);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari1.setMnemonic('2');
        BtnCari1.setToolTipText("Alt+2");
        BtnCari1.setName("BtnCari1"); // NOI18N
        BtnCari1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari1ActionPerformed(evt);
            }
        });
        BtnCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari1KeyPressed(evt);
            }
        });
        panelGlass5.add(BtnCari1);

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

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(label10);

        LCount2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount2.setText("0");
        LCount2.setName("LCount2"); // NOI18N
        LCount2.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass5.add(LCount2);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(50, 50, 50));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Piutang :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(jLabel10);

        LCount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        LCount.setForeground(new java.awt.Color(50, 50, 50));
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(115, 23));
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

        label19.setText("Status :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(47, 23));
        panelisi4.add(label19);

        StatusLunas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Sudah Lunas", "Belum Lunas" }));
        StatusLunas.setName("StatusLunas"); // NOI18N
        StatusLunas.setPreferredSize(new java.awt.Dimension(119, 23));
        panelisi4.add(StatusLunas);

        label17.setText("Jenis Bayar :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi4.add(label17);

        nmpenjab.setEditable(false);
        nmpenjab.setName("nmpenjab"); // NOI18N
        nmpenjab.setPreferredSize(new java.awt.Dimension(195, 23));
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

        label20.setText("Kamar/Unit :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(85, 23));
        panelisi4.add(label20);

        NmKamar.setEditable(false);
        NmKamar.setName("NmKamar"); // NOI18N
        NmKamar.setPreferredSize(new java.awt.Dimension(195, 23));
        panelisi4.add(NmKamar);

        BtnKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnKamar.setMnemonic('3');
        BtnKamar.setToolTipText("Alt+3");
        BtnKamar.setName("BtnKamar"); // NOI18N
        BtnKamar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKamarActionPerformed(evt);
            }
        });
        panelisi4.add(BtnKamar);

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
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tgl.Pulang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.Nota</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.RM</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Nama Pasien</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Jenis Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Perujuk</td>"+
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
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Total</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Ekses</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Sudah Dibayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Diskon Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tidak Terbayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Sisa</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Nama Kamar</td>"+
                                "</tr>"
                            );
                            for(int i=0;i<tabMode.getRowCount();i++){
                                htmlContent.append(
                                    "<tr class='isi'>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,0)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,1)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,2)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,3)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,4)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,5)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,6)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,7)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,8)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,9)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,10)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,11)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,12)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,13)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,14)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,15)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,16)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,17)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,18)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,19)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,20)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,21)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,22)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,23)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,24)+"</td>"+
                                    "</tr>"
                                );
                            }

                            f = new File("PembayaranRanap.html");
                            bw = new BufferedWriter(new FileWriter(f));
                            bw.write("<html>"+
                                        "<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /></head>"+
                                        "<body>"+
                                            "<table width='2050px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                                "<tr class='isi2'>"+
                                                    "<td valign='top' align='center'>"+
                                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                                        "<font size='2' face='Tahoma'>REKAP PEMBAYARAN RAWAT JALAN PERIODE "+Tgl1.getSelectedItem()+" s.d. "+Tgl2.getSelectedItem()+"<br><br></font>"+
                                                    "</td>"+
                                               "</tr>"+
                                            "</table>"+
                                            "<table width='2050px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
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
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tgl.Pulang</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.Nota</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>No.RM</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Nama Pasien</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Jenis Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Perujuk</td>"+
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
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Total</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Ekses</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Sudah Dibayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Diskon Bayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Tidak Terbayar</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Sisa</td>"+
                                    "<td valign='middle' bgcolor='#FFFAFA' align='center'>Nama Kamar</td>"+
                                "</tr>"
                            );
                            for(int i=0;i<tabMode.getRowCount();i++){
                                htmlContent.append(
                                    "<tr class='isi'>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,0)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,1)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,2)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,3)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,4)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,5)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,6)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,7)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,8)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,9)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,10)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,11)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,12)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,13)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,14)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,15)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,16)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,17)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,18)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,19)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,20)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,21)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,22)+"</td>"+
                                        "<td valign='top' align='right'>"+tabMode.getValueAt(i,23)+"</td>"+
                                        "<td valign='top'>"+tabMode.getValueAt(i,24)+"</td>"+
                                    "</tr>"
                                );
                            }

                            f = new File("PembayaranRanap.wps");
                            bw = new BufferedWriter(new FileWriter(f));
                            bw.write("<html>"+
                                        "<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /></head>"+
                                        "<body>"+
                                            "<table width='2050px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                                "<tr class='isi2'>"+
                                                    "<td valign='top' align='center'>"+
                                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                                        "<font size='2' face='Tahoma'>DETAIL JM DOKTER PERIODE "+Tgl1.getSelectedItem()+" s.d. "+Tgl2.getSelectedItem()+"<br><br></font>"+
                                                    "</td>"+
                                               "</tr>"+
                                            "</table>"+
                                            "<table width='2050px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
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
                                "\"Tgl.Pulang\";\"No.Nota\";\"No.RM\";\"Nama Pasien\";\"Jenis Bayar\";\"Perujuk\";\"Registrasi\";\"Tindakan\";\"Obt+Emb+Tsl\";\"Retur Obat\";\"Resep Pulang\";\"Laborat\";\"Radiologi\";\"Potongan\";\"Tambahan\";\"Kamar+Service\";\"Operasi\";\"Harian\";\"Total\";\"Ekses\";\"Sudah Dibayar\";\"Diskon Bayar\";\"Tidak Terbayar\";\"Sisa\";\"Nama Kamar\"\n"
                            );
                            for(int i=0;i<tabMode.getRowCount();i++){
                                htmlContent.append(
                                    "\""+tabMode.getValueAt(i,0)+"\";\""+tabMode.getValueAt(i,1)+"\";\""+tabMode.getValueAt(i,2)+"\";\""+tabMode.getValueAt(i,3)+"\";\""+tabMode.getValueAt(i,4)+"\";\""+tabMode.getValueAt(i,5)+"\";\""+tabMode.getValueAt(i,6)+"\";\""+tabMode.getValueAt(i,7)+"\";\""+tabMode.getValueAt(i,8)+"\";\""+tabMode.getValueAt(i,9)+"\";\""+tabMode.getValueAt(i,10)+"\";\""+tabMode.getValueAt(i,11)+"\";\""+tabMode.getValueAt(i,12)+"\";\""+tabMode.getValueAt(i,13)+"\";\""+tabMode.getValueAt(i,14)+"\";\""+tabMode.getValueAt(i,15)+"\";\""+tabMode.getValueAt(i,16)+"\";\""+tabMode.getValueAt(i,17)+"\";\""+tabMode.getValueAt(i,18)+"\";\""+tabMode.getValueAt(i,19)+"\";\""+tabMode.getValueAt(i,20)+"\";\""+tabMode.getValueAt(i,21)+"\";\""+tabMode.getValueAt(i,22)+"\";\""+tabMode.getValueAt(i,23)+"\";\""+tabMode.getValueAt(i,24)+"\"\n"
                                );
                            }

                            f = new File("PembayaranRanap.csv");
                            bw = new BufferedWriter(new FileWriter(f));
                            bw.write(htmlContent.toString());

                            bw.close();
                            Desktop.getDesktop().browse(f.toURI());
                        break;
                    case "Laporan 4 (Jasper)":
                        Sequel.deleteTemporary();
                        for (int i = 0; i < tabMode.getRowCount(); i++) {
                            Sequel.temporary(String.valueOf(i + 1),
                                (String) tabMode.getValueAt(i, 0), (String) tabMode.getValueAt(i, 1), (String) tabMode.getValueAt(i, 2),
                                (String) tabMode.getValueAt(i, 3), (String) tabMode.getValueAt(i, 4), (String) tabMode.getValueAt(i, 5),
                                (String) tabMode.getValueAt(i, 6), (String) tabMode.getValueAt(i, 7), (String) tabMode.getValueAt(i, 8),
                                (String) tabMode.getValueAt(i, 9), (String) tabMode.getValueAt(i, 10), (String) tabMode.getValueAt(i, 11),
                                (String) tabMode.getValueAt(i, 12), (String) tabMode.getValueAt(i, 13), (String) tabMode.getValueAt(i, 14),
                                (String) tabMode.getValueAt(i, 15), (String) tabMode.getValueAt(i, 16), (String) tabMode.getValueAt(i, 17),
                                (String) tabMode.getValueAt(i, 18), (String) tabMode.getValueAt(i, 19), (String) tabMode.getValueAt(i, 20),
                                (String) tabMode.getValueAt(i, 21), (String) tabMode.getValueAt(i, 22), (String) tabMode.getValueAt(i, 23),
                                (String) tabMode.getValueAt(i, 24), (String) tabMode.getValueAt(i, 25)
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
                        param.put("ipaddress", akses.getalamatip());
                        Valid.reportTempSmc("rptRPiutangRanap.jasper", "report", "::[ Rekap Tagihan Ranap Masuk ]::", param);
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
            //Valid.pindah(evt, BtnHapus, BtnAll);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnKeluar,TKd);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari1ActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCari1ActionPerformed

    private void BtnCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tampil();
            this.setCursor(Cursor.getDefaultCursor());
        }else{
            Valid.pindah(evt, TKd, BtnPrint);
        }
    }//GEN-LAST:event_BtnCari1KeyPressed

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

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        kdpenjab.setText("");
        nmpenjab.setText("");
        StatusLunas.setSelectedIndex(0);
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, kdpenjab, BtnPrint);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void MnBillingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnBillingActionPerformed
        if (tbBangsal.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
        }else{
            DlgBilingRanap billing=new DlgBilingRanap(null,false);
            billing.TNoRw.setText(Sequel.cariIsi("select nota_inap.no_rawat from nota_inap where nota_inap.no_nota=?",TKd.getText()));
            billing.isCek();
            billing.isRawat();
            billing.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            billing.setLocationRelativeTo(internalFrame1);
            billing.setVisible(true);
        }
    }//GEN-LAST:event_MnBillingActionPerformed

    private void BtnKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKamarActionPerformed
        akses.setform("DlgPiutangRanap");
        bangsal.isCek();
        bangsal.emptTeks();
        bangsal.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        bangsal.setLocationRelativeTo(internalFrame1);
        bangsal.setVisible(true);
    }//GEN-LAST:event_BtnKamarActionPerformed

    private void tbBangsalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBangsalMousePressed
        int r = tbBangsal.rowAtPoint(evt.getPoint());
        int c = tbBangsal.columnAtPoint(evt.getPoint());
        if (!tbBangsal.isRowSelected(r)) {
            tbBangsal.changeSelection(r, c, false, false);
        }
    }//GEN-LAST:event_tbBangsalMousePressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPiutangRanap dialog = new DlgPiutangRanap(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari1;
    private widget.Button BtnKamar;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSeek2;
    private widget.TextBox KdKamar;
    private javax.swing.JLabel LCount;
    private widget.Label LCount2;
    private javax.swing.JMenuItem MnBilling;
    private widget.TextBox NmKamar;
    private widget.ScrollPane Scroll;
    private widget.ComboBox StatusLunas;
    private widget.TextBox TKd;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox kdpenjab;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.TextBox nmpenjab;
    private widget.panelisi panelGlass5;
    private widget.panelisi panelisi4;
    private widget.Table tbBangsal;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        if (!ceksukses) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ceksukses = true;
            Valid.tabelKosong(tabMode);
            LCount.setText("0");
            LCount2.setText("0");
            new SwingWorker<Void, Object[]>() {
                private double all = 0, ttlLaborat = 0, ttlRadiologi = 0, ttlOperasi = 0, ttlObat = 0, ttlRanap_Dokter = 0,
                    ttlRanap_Paramedis = 0, ttlRalan_Dokter = 0, ttlRalan_Paramedis = 0, ttlTambahan = 0, ttlPotongan = 0,
                    ttlKamar = 0, ttlRegistrasi = 0, ttlHarian = 0, ttlRetur_Obat = 0, ttlResep_Pulang = 0, ttlService = 0,
                    ttlekses = 0, ttldibayar = 0, ttldiskon = 0, ttltidakdibayar = 0, ttlsisa = 0;
                private int count = 0;

                @Override
                protected Void doInBackground() {
                    String statuspiutang = "";
                    switch (StatusLunas.getSelectedIndex()) {
                        case 1: statuspiutang = "and piutang_pasien.status = 'Lunas' "; break;
                        case 2: statuspiutang = "and piutang_pasien.status = 'Belum Lunas' "; break;
                        default: statuspiutang = ""; break;
                    }

                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select nota_inap.no_nota, reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, kamar_inap.tgl_keluar, " +
                        "kamar_inap.kd_kamar, bangsal.nm_bangsal, reg_periksa.kd_pj, penjab.png_jawab, piutang_pasien.uangmuka, piutang_pasien.totalpiutang, " +
                        "ifnull((select rujuk_masuk.perujuk from rujuk_masuk where rujuk_masuk.no_rawat = reg_periksa.no_rawat limit 1), '') as asal_rujuk, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Laborat') as laborat, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Radiologi') as radiologi, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Operasi') as operasi, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Obat') as obat, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Ranap Dokter') as ranap_dr, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Ranap Dokter Paramedis') as ranap_drpr, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Ranap Paramedis') as ranap_pr, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Ralan Dokter') as ralan_dr, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Ralan Dokter Paramedis') as ralan_drpr, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Ralan Paramedis') as ralan_pr, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Tambahan') as tambahan, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Potongan') as potongan, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Kamar') as kamar, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Registrasi') as registrasi, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Harian') as harian, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Retur Obat') as retur_obat, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Resep Pulang') as resep_pulang, " +
                        "(select ifnull(sum(billing.totalbiaya), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Service') as service, " +
                        "(select ifnull(sum(bayar_piutang.besar_cicilan), 0) from bayar_piutang where bayar_piutang.no_rawat = reg_periksa.no_rawat) as besar_cicilan, " +
                        "(select ifnull(sum(bayar_piutang.diskon_piutang), 0) from bayar_piutang where bayar_piutang.no_rawat = reg_periksa.no_rawat) as diskon_piutang, " +
                        "(select ifnull(sum(bayar_piutang.tidak_terbayar), 0) from bayar_piutang where bayar_piutang.no_rawat = reg_periksa.no_rawat) as tidak_terbayar " +
                        "from reg_periksa join kamar_inap on reg_periksa.no_rawat = kamar_inap.no_rawat join kamar on kamar_inap.kd_kamar = kamar.kd_kamar join bangsal on " +
                        "kamar.kd_bangsal = bangsal.kd_bangsal join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join penjab on reg_periksa.kd_pj = penjab.kd_pj " +
                        "join piutang_pasien on piutang_pasien.no_rawat = reg_periksa.no_rawat join nota_inap on reg_periksa.no_rawat = nota_inap.no_rawat where " +
                        "reg_periksa.status_lanjut = 'Ranap' " + (kdpenjab.getText().isBlank() ? "" : "and reg_periksa.kd_pj = ? ") + "and kamar_inap.tgl_keluar between ? and ? and " +
                        "kamar_inap.stts_pulang not in ('-', 'Pindah Kamar') " + (KdKamar.getText().isBlank() ? "" : "and kamar.kd_bangsal = ? ") + statuspiutang +
                        "order by kamar_inap.tgl_keluar, kamar_inap.jam_keluar"
                    )) {
                        int p = 0;
                        if (!kdpenjab.getText().isBlank()) {
                            ps.setString(++p, kdpenjab.getText());
                        }
                        ps.setString(++p, Valid.getTglSmc(Tgl1));
                        ps.setString(++p, Valid.getTglSmc(Tgl2));
                        if (!KdKamar.getText().isBlank()) {
                            ps.setString(++p, KdKamar.getText());
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                ttlLaborat += rs.getDouble("laborat");
                                ttlRadiologi += rs.getDouble("radiologi");
                                ttlOperasi += rs.getDouble("operasi");
                                ttlObat += rs.getDouble("obat");
                                ttlRanap_Dokter += rs.getDouble("ranap_dr") + rs.getDouble("ranap_drpr");
                                ttlRanap_Paramedis += rs.getDouble("ranap_pr");
                                ttlRalan_Dokter += rs.getDouble("ralan_dr") + rs.getDouble("ralan_drpr");
                                ttlRalan_Paramedis += rs.getDouble("ralan_pr");
                                ttlTambahan += rs.getDouble("tambahan");
                                ttlPotongan += rs.getDouble("potongan");
                                ttlKamar += rs.getDouble("kamar");
                                ttlRegistrasi += rs.getDouble("registrasi");
                                ttlHarian += rs.getDouble("harian");
                                ttlRetur_Obat += rs.getDouble("retur_obat");
                                ttlResep_Pulang += rs.getDouble("resep_pulang");
                                ttlService += rs.getDouble("service");
                                all += rs.getDouble("laborat") + rs.getDouble("radiologi") + rs.getDouble("operasi") + rs.getDouble("obat") +
                                       rs.getDouble("ranap_dr") + rs.getDouble("ranap_drpr") + rs.getDouble("ranap_pr") + rs.getDouble("ralan_dr") +
                                       rs.getDouble("ralan_drpr") + rs.getDouble("ralan_pr") + rs.getDouble("tambahan") + rs.getDouble("potongan") +
                                       rs.getDouble("kamar") + rs.getDouble("registrasi") + rs.getDouble("harian") + rs.getDouble("retur_obat") +
                                       rs.getDouble("resep_pulang") + rs.getDouble("service");

                                ttlekses += rs.getDouble("uangmuka");
                                ttldibayar += rs.getDouble("besar_cicilan");
                                ttldiskon += rs.getDouble("diskon_piutang");
                                ttltidakdibayar += rs.getDouble("tidak_terbayar");
                                ttlsisa += rs.getDouble("totalpiutang") - rs.getDouble("uangmuka") - rs.getDouble("besar_cicilan") -
                                           rs.getDouble("diskon_piutang") - rs.getDouble("tidak_terbayar");

                                ++count;
                                publish(new Object[] {
                                    rs.getString("tgl_keluar"), rs.getString("no_rawat"), rs.getString("no_nota"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                                    rs.getString("png_jawab") + " (" + rs.getString("kd_pj") + ")", rs.getString("asal_rujuk"), Valid.SetAngka(rs.getDouble("registrasi")),
                                    Valid.SetAngka(rs.getDouble("ranap_dr") + rs.getDouble("ranap_drpr") + rs.getDouble("ranap_pr") + rs.getDouble("ralan_dr") +
                                    rs.getDouble("ralan_drpr") + rs.getDouble("ralan_pr")), Valid.SetAngka(rs.getDouble("obat")), Valid.SetAngka(rs.getDouble("retur_obat")),
                                    Valid.SetAngka(rs.getDouble("resep_pulang")), Valid.SetAngka(rs.getDouble("laborat")), Valid.SetAngka(rs.getDouble("radiologi")),
                                    Valid.SetAngka(rs.getDouble("potongan")), Valid.SetAngka(rs.getDouble("tambahan")), Valid.SetAngka(rs.getDouble("kamar") + rs.getDouble("service")),
                                    Valid.SetAngka(rs.getDouble("operasi")), Valid.SetAngka(rs.getDouble("harian")), Valid.SetAngka(rs.getDouble("laborat") + rs.getDouble("radiologi") +
                                    rs.getDouble("operasi") + rs.getDouble("obat") + rs.getDouble("ranap_dr") + rs.getDouble("ranap_drpr") + rs.getDouble("ranap_pr") +
                                    rs.getDouble("ralan_dr") + rs.getDouble("ralan_drpr") + rs.getDouble("ralan_pr") + rs.getDouble("tambahan") + rs.getDouble("potongan") +
                                    rs.getDouble("kamar") + rs.getDouble("registrasi") + rs.getDouble("harian") + rs.getDouble("retur_obat") + rs.getDouble("resep_pulang") +
                                    rs.getDouble("service")), Valid.SetAngka(rs.getDouble("uangmuka")), Valid.SetAngka(rs.getDouble("besar_cicilan")),
                                    Valid.SetAngka(rs.getDouble("diskon_piutang")), Valid.SetAngka(rs.getDouble("tidak_terbayar")), Valid.SetAngka(rs.getDouble("totalpiutang") -
                                    rs.getDouble("uangmuka") - rs.getDouble("besar_cicilan") - rs.getDouble("diskon_piutang") - rs.getDouble("tidak_terbayar")),
                                    rs.getString("kd_kamar") + " " + rs.getString("nm_bangsal")
                                });
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }

                    return null;
                }

                @Override
                protected void process(List<Object[]> chunks) {
                    chunks.forEach(tabMode::addRow);
                }

                @Override
                protected void done() {
                    tabMode.addRow(new Object[] {
                        ">> Total", ":", "", "", "", "", "", Valid.SetAngka(ttlRegistrasi), Valid.SetAngka(ttlRanap_Dokter + ttlRanap_Paramedis + ttlRalan_Dokter + ttlRalan_Paramedis),
                        Valid.SetAngka(ttlObat), Valid.SetAngka(ttlRetur_Obat), Valid.SetAngka(ttlResep_Pulang), Valid.SetAngka(ttlLaborat), Valid.SetAngka(ttlRadiologi),
                        Valid.SetAngka(ttlPotongan), Valid.SetAngka(ttlTambahan), Valid.SetAngka(ttlKamar + ttlService), Valid.SetAngka(ttlOperasi), Valid.SetAngka(ttlHarian),
                        Valid.SetAngka(all), Valid.SetAngka(ttlekses), Valid.SetAngka(ttldibayar), Valid.SetAngka(ttldiskon), Valid.SetAngka(ttltidakdibayar), Valid.SetAngka(ttlsisa), ""
                    });
                    tabMode.fireTableDataChanged();
                    LCount.setText(Valid.SetAngka(all));
                    LCount2.setText("" + count);
                    setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }
}
