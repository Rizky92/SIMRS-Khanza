/*
 * Form mapping jenis peserta BPJS (bridging_sep.peserta) -> segmentasi
 * Coverage.class[type=group] untuk SatuSehat.
 *
 * Dipakai oleh : SatuSehatBilling (contained Coverage di Account dan
 *                CoverageEligibilityResponse).
 * Hasil mapping: satu_sehat_mapping_segmen_peserta (PK peserta).
 *
 * Daftar peserta di tabel diambil dari bridging_sep (bukan dari tabel
 * mapping) supaya nilai yang BELUM dipetakan ikut terlihat.
 */
package bridging.satusehat.klaim;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class SatuSehatMappingSegmenPeserta extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;

    /**
     * 19 kode segmentasi resmi + label-nya. Sumber:
     * satusehat.kemkes.go.id/platform/docs/id/terminology/lampiran-terminologi/klaim-bpjs/
     *
     * Server MEMVALIDASI kode ini (di luar daftar => "Code not found ... in
     * system: http://terminology.kemkes.go.id/..."). Seluruh kode di bawah
     * sudah diuji ke staging dan diterima.
     *
     * Dokumentasi resmi menulis "pppu-pegawai-swasta" (3 huruf p); itu TYPO,
     * server hanya menerima "ppu-pegawai-swasta".
     */
    private static final String[][] LABEL_SEGMEN = {
        {"pbi-apbn",                  "PBI APBN"},
        {"pbi-apbd",                  "PBI APBD"},
        {"ppu-pejabat-negara",        "Pejabat Negara"},
        {"ppu-dprd",                  "Pimpinan dan Anggota Dewan Perwakilan Rakyat Daerah"},
        {"ppu-pns",                   "PNS"},
        {"ppu-prajurit",              "Prajurit"},
        {"ppu-polri",                 "Anggota Polri"},
        {"ppu-kades-dan-perangkat",   "Kepala Desa dan Perangkat Desa"},
        {"ppu-pegawai-swasta",        "Pegawai Swasta"},
        {"ppu-pekerja-lainnya",       "Pekerja lainnya yang menerima Gaji atau Upah"},
        {"pbpu-pekerja-mandiri",      "Pekerja di luar hubungan kerja atau Pekerja mandiri"},
        {"pbpu-pekerja-lainnya",      "Pekerja lainnya yang bukan penerima Gaji atau Upah"},
        {"bp-investor",               "Investor"},
        {"bp-pemberi-kerja",          "Pemberi Kerja"},
        {"bp-penerima-pensiun",       "Penerima Pensiun"},
        {"bp-veteran",                "Veteran"},
        {"bp-perintis-kemerdekaan",   "Perintis Kemerdekaan"},
        {"bp-janda-duda-yatim-piatu", "Janda, Duda, atau Anak Yatim dan/atau Piatu dari Veteran atau Perintis Kemerdekaan"},
        {"bp-lainnya",                "Bukan pekerja lainnya yang mampu membayar iuran"}
    };

    public SatuSehatMappingSegmenPeserta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(900,640);

        tabMode=new DefaultTableModel(null,new Object[]{
                "Jenis Peserta (bridging_sep)","Jml SEP","Segmen","Nama Segmen"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
             @Override public Class getColumnClass(int columnIndex){
                 return columnIndex==1 ? Integer.class : String.class;
             }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w={330,80,180,280};
        for (i=0; i<w.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(w[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        // Isi pilihan segmen dari LABEL_SEGMEN supaya daftar kode hanya ada di
        // SATU tempat (yang di initComponents cuma bawaan designer).
        javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel();
        for (String[] baris : LABEL_SEGMEN) model.addElement(baris[0]);
        CmbSegmen.setModel(model);
        CmbSegmen.setSelectedItem("");

        TNama.setDocument(new batasInput((byte)100).getKata(TNama));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        ChkInput.setSelected(false);
        isForm();

        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
            });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBaru = new widget.Button();
        BtnHapus = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabelS = new widget.Label();
        CmbStatus = new widget.ComboBox();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel1 = new widget.Label();
        TPeserta = new widget.TextBox();
        jLabel2 = new widget.Label();
        CmbSegmen = new widget.ComboBox();
        jLabel3 = new widget.Label();
        TNama = new widget.TextBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Mapping Jenis Peserta BPJS ke Segmentasi Coverage (Satu Sehat) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 80));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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
        panelGlass8.add(BtnSimpan);

        BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBaru.setMnemonic('B');
        BtnBaru.setText("Baru");
        BtnBaru.setToolTipText("Alt+B");
        BtnBaru.setName("BtnBaru"); // NOI18N
        BtnBaru.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBaruActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBaru);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnAll);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Cari Peserta :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(260, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

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
        panelGlass9.add(BtnCari);

        jLabelS.setText("Status :");
        jLabelS.setName("jLabelS"); // NOI18N
        jLabelS.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabelS);

        CmbStatus.setName("CmbStatus"); // NOI18N
        CmbStatus.setPreferredSize(new java.awt.Dimension(140, 23));
        CmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass9.add(CmbStatus);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabel7);

        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 125));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 105));
        FormInput.setLayout(null);

        jLabel1.setText("Jenis Peserta :");
        jLabel1.setName("jLabel1"); // NOI18N
        FormInput.add(jLabel1);
        jLabel1.setBounds(0, 10, 105, 23);

        TPeserta.setEditable(false);
        TPeserta.setName("TPeserta"); // NOI18N
        FormInput.add(TPeserta);
        TPeserta.setBounds(109, 10, 615, 23);

        jLabel2.setText("Segmen :");
        jLabel2.setToolTipText("Coverage.class[type=group].value");
        jLabel2.setName("jLabel2"); // NOI18N
        FormInput.add(jLabel2);
        jLabel2.setBounds(0, 40, 105, 23);

        CmbSegmen.setEditable(true);
        CmbSegmen.setName("CmbSegmen"); // NOI18N
        CmbSegmen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbSegmenActionPerformed(evt);
            }
        });
        FormInput.add(CmbSegmen);
        CmbSegmen.setBounds(109, 40, 240, 23);

        jLabel3.setText("Nama Segmen :");
        jLabel3.setToolTipText("Coverage.class[type=group].name");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 70, 105, 23);

        TNama.setName("TNama"); // NOI18N
        FormInput.add(TNama);
        TNama.setBounds(109, 70, 380, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        int r = tbObat.getSelectedRow();
        if(r<0) return;
        r = tbObat.convertRowIndexToModel(r);
        TPeserta.setText(tabMode.getValueAt(r,0).toString());
        CmbSegmen.setSelectedItem(tabMode.getValueAt(r,2).toString());
        TNama.setText(tabMode.getValueAt(r,3).toString());
        if(!FormInput.isVisible()){
            ChkInput.setSelected(true);
            isForm();
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        CmbStatus.setSelectedItem("Semua");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) tampil();
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBaruActionPerformed
        TPeserta.setText("");
        CmbSegmen.setSelectedItem("");
        TNama.setText("");
    }//GEN-LAST:event_BtnBaruActionPerformed

    /** Isi otomatis Nama Segmen dari slug yang dipilih, bila nama masih kosong. */
    private void CmbSegmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbSegmenActionPerformed
        if(!TNama.getText().trim().equals("")) return;
        String segmen = segmenTerpilih();
        for(String[] baris : LABEL_SEGMEN){
            if(baris[0].equalsIgnoreCase(segmen)){
                TNama.setText(baris[1]);
                return;
            }
        }
    }//GEN-LAST:event_CmbSegmenActionPerformed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TPeserta.getText().trim().equals("")){
            Valid.textKosong(TPeserta,"Jenis Peserta (pilih baris tabel)");
            return;
        }
        if(segmenTerpilih().equals("")){
            Valid.textKosong(TNama,"Segmen");
            return;
        }
        if(TNama.getText().trim().equals("")){
            Valid.textKosong(TNama,"Nama Segmen");
            return;
        }
        try {
            ps = koneksi.prepareStatement("replace into satu_sehat_mapping_segmen_peserta (peserta,segmen,nama) values (?,?,?)");
            ps.setString(1,TPeserta.getText().trim());
            ps.setString(2,segmenTerpilih());
            ps.setString(3,TNama.getText().trim());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Gagal simpan : "+e);
            return;
        }
        tampil();
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(TPeserta.getText().trim().equals("")){
            Valid.textKosong(TPeserta,"Jenis Peserta (pilih baris tabel)");
            return;
        }
        try {
            ps = koneksi.prepareStatement("delete from satu_sehat_mapping_segmen_peserta where peserta=?");
            ps.setString(1,TPeserta.getText().trim());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Gagal hapus : "+e);
            return;
        }
        CmbSegmen.setSelectedItem("");
        TNama.setText("");
        tampil();
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    /** Nilai CmbSegmen (editable) sebagai String, sudah di-trim. */
    private String segmenTerpilih(){
        Object o = CmbSegmen.getSelectedItem();
        return o==null ? "" : o.toString().trim();
    }

    /**
     * Daftar jenis peserta diambil dari bridging_sep (DISTINCT + jumlah SEP),
     * di-LEFT JOIN ke tabel mapping supaya yang belum dipetakan ikut tampil.
     * UNION dengan tabel mapping agar entri manual yang belum pernah muncul
     * di bridging_sep tidak hilang dari daftar.
     */
    public void tampil() {
        try {
            while(tabMode.getRowCount()>0) tabMode.removeRow(0);
            String c = "%"+TCari.getText().trim()+"%";
            String status = String.valueOf(CmbStatus.getSelectedItem());
            String kondisi = "";
            if(status.equals("Sudah Dipetakan"))  kondisi = "and x.segmen<>'' ";
            if(status.equals("Belum Dipetakan"))  kondisi = "and x.segmen='' ";
            String sql =
                "select x.peserta, sum(x.jml) jml, max(x.segmen) segmen, max(x.nama) nama from ("+
                "  select s.peserta, count(*) jml, "+
                "         ifnull(m.segmen,'') segmen, ifnull(m.nama,'') nama "+
                "  from bridging_sep s "+
                "  left join satu_sehat_mapping_segmen_peserta m on m.peserta=s.peserta "+
                "  where ifnull(s.peserta,'')<>'' "+
                "  group by s.peserta, m.segmen, m.nama "+
                "  union all "+
                "  select m2.peserta, 0 jml, m2.segmen, m2.nama "+
                "  from satu_sehat_mapping_segmen_peserta m2 "+
                ") x where x.peserta like ? "+kondisi+
                "group by x.peserta order by jml desc, x.peserta limit 5000";
            ps = koneksi.prepareStatement(sql);
            ps.setString(1,c);
            rs = ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new Object[]{
                    rs.getString("peserta"), rs.getInt("jml"),
                    rs.getString("segmen"), rs.getString("nama")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif tampil mapping segmen peserta : "+e);
        } finally {
            try { if(rs!=null) rs.close(); if(ps!=null) ps.close(); } catch(Exception x){}
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek() {
        boolean bisa = akses.getsatu_sehat_mapping_lab();
        BtnSimpan.setEnabled(bisa);
        BtnHapus.setEnabled(bisa);
    }

    public JTable getTable(){ return tbObat; }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 125));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
        PanelInput.revalidate();
        internalFrame1.revalidate();
        internalFrame1.repaint();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatMappingSegmenPeserta d = new SatuSehatMappingSegmenPeserta(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            d.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBaru;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.ComboBox CmbSegmen;
    private widget.ComboBox CmbStatus;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNama;
    private widget.TextBox TPeserta;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel2;
    private widget.Label jLabel3;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabelS;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
}
