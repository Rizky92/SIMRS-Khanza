/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laporan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author khanzamedia
 */
public class PanelDiagnosaSMC extends widget.panelisi {
    private final DefaultTableModel tabModeDiagnosaPasien, tabModeICD10, tabModeICD9CM, tabModeProsedurPasien;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ObjectMapper mapper = new ObjectMapper();
    public String norawat = "", status = "", norm = "", tanggal1 = "", tanggal2 = "", keyword = "";

    /**
     * Creates new form panelDiagnosa
     */
    public PanelDiagnosaSMC() {
        initComponents();
        tabModeICD10 = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Deskripsi", "VALIDCODE", "ACCPDX", "ASTERISK", "IM"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return columnIndex == 0 ? java.lang.Boolean.class : java.lang.String.class;
            }
        };
        tbICD10.setModel(tabModeICD10);
        tbICD10.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbICD10.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbICD10.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbICD10.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbICD10.getColumnModel().getColumn(2).setPreferredWidth(400);
        tbICD10.getColumnModel().getColumn(3).setMinWidth(0);
        tbICD10.getColumnModel().getColumn(3).setMaxWidth(0);
        tbICD10.getColumnModel().getColumn(4).setMinWidth(0);
        tbICD10.getColumnModel().getColumn(4).setMaxWidth(0);
        tbICD10.getColumnModel().getColumn(5).setMinWidth(0);
        tbICD10.getColumnModel().getColumn(5).setMaxWidth(0);
        tbICD10.getColumnModel().getColumn(6).setMinWidth(0);
        tbICD10.getColumnModel().getColumn(6).setMaxWidth(0);
        tbICD10.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeICD9CM = new DefaultTableModel(null, new Object[] {
            "P", "Mtpx", "Kode", "Deskripsi", "VALIDCODE", "ACCPDX", "ASTERISK", "IM"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0 || colIndex == 1;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return java.lang.Boolean.class;
                }
                
                if (columnIndex == 1) {
                    return java.lang.Integer.class;
                }
                
                return java.lang.String.class;
            }
        };
        tbICD9CM.setModel(tabModeICD9CM);
        tbICD9CM.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbICD9CM.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbICD9CM.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbICD9CM.getColumnModel().getColumn(1).setPreferredWidth(30);
        tbICD9CM.getColumnModel().getColumn(2).setPreferredWidth(50);
        tbICD9CM.getColumnModel().getColumn(3).setPreferredWidth(400);
        tbICD9CM.getColumnModel().getColumn(4).setMinWidth(0);
        tbICD9CM.getColumnModel().getColumn(4).setMaxWidth(0);
        tbICD9CM.getColumnModel().getColumn(5).setMinWidth(0);
        tbICD9CM.getColumnModel().getColumn(5).setMaxWidth(0);
        tbICD9CM.getColumnModel().getColumn(6).setMinWidth(0);
        tbICD9CM.getColumnModel().getColumn(6).setMaxWidth(0);
        tbICD9CM.getColumnModel().getColumn(7).setMinWidth(0);
        tbICD9CM.getColumnModel().getColumn(7).setMaxWidth(0);
        tbICD9CM.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeDiagnosaPasien = new DefaultTableModel(null, new Object[] {
            "P", "Tgl. Rawat", "No. Rawat", "No. RM", "Nama Pasien", "Kode", "Nama Penyakit", "Status", "Kasus"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return columnIndex == 0 ? java.lang.Boolean.class : java.lang.String.class;
            }
        };
        tbDiagnosaPasien.setModel(tabModeDiagnosaPasien);
        tbDiagnosaPasien.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDiagnosaPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDiagnosaPasien.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbDiagnosaPasien.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbDiagnosaPasien.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbDiagnosaPasien.getColumnModel().getColumn(3).setPreferredWidth(70);
        tbDiagnosaPasien.getColumnModel().getColumn(4).setPreferredWidth(160);
        tbDiagnosaPasien.getColumnModel().getColumn(5).setPreferredWidth(50);
        tbDiagnosaPasien.getColumnModel().getColumn(6).setPreferredWidth(350);
        tbDiagnosaPasien.getColumnModel().getColumn(7).setPreferredWidth(50);
        tbDiagnosaPasien.getColumnModel().getColumn(8).setPreferredWidth(50);
        tbDiagnosaPasien.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeProsedurPasien = new DefaultTableModel(null, new Object[] {
            "P", "Tgl. Rawat", "No. Rawat", "No. RM", "Nama Pasien", "Kode", "Nama Prosedur", "Status"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
            
            @Override
            public Class getColumnClass(int columnIndex) {
                return columnIndex == 0 ? java.lang.Boolean.class : java.lang.String.class;
            }
        };
        tbProsedurPasien.setModel(tabModeProsedurPasien);
        tbProsedurPasien.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbProsedurPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbProsedurPasien.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbProsedurPasien.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbProsedurPasien.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbProsedurPasien.getColumnModel().getColumn(3).setPreferredWidth(70);
        tbProsedurPasien.getColumnModel().getColumn(4).setPreferredWidth(160);
        tbProsedurPasien.getColumnModel().getColumn(5).setPreferredWidth(50);
        tbProsedurPasien.getColumnModel().getColumn(6).setPreferredWidth(300);
        tbProsedurPasien.getColumnModel().getColumn(7).setPreferredWidth(50);
        tbProsedurPasien.setDefaultRenderer(Object.class, new WarnaTable());

        Diagnosa.setDocument(new batasInput((byte) 100).getKata(Diagnosa));
        Prosedur.setDocument(new batasInput((byte) 100).getKata(Prosedur));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnStatusBaru = new javax.swing.JMenuItem();
        MnStatusLama = new javax.swing.JMenuItem();
        TabRawat = new javax.swing.JTabbedPane();
        ScrollInput = new widget.ScrollPane();
        FormData = new widget.PanelBiasa();
        jLabel13 = new widget.Label();
        Diagnosa = new widget.TextBox();
        BtnCariPenyakit = new widget.Button();
        Scroll1 = new widget.ScrollPane();
        tbICD10 = new widget.Table();
        jLabel15 = new widget.Label();
        Prosedur = new widget.TextBox();
        BtnCariProsedur = new widget.Button();
        Scroll2 = new widget.ScrollPane();
        tbICD9CM = new widget.Table();
        internalFrame2 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbDiagnosaPasien = new widget.Table();
        internalFrame3 = new widget.InternalFrame();
        Scroll3 = new widget.ScrollPane();
        tbProsedurPasien = new widget.Table();

        MnStatusBaru.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnStatusBaru.setForeground(new java.awt.Color(50, 50, 50));
        MnStatusBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnStatusBaru.setText("Status Penyakit Baru");
        MnStatusBaru.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnStatusBaru.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnStatusBaru.setPreferredSize(new java.awt.Dimension(170, 26));
        MnStatusBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnStatusBaruActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnStatusBaru);

        MnStatusLama.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnStatusLama.setForeground(new java.awt.Color(50, 50, 50));
        MnStatusLama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnStatusLama.setText("Status Penyakit Lama");
        MnStatusLama.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnStatusLama.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnStatusLama.setIconTextGap(5);
        MnStatusLama.setPreferredSize(new java.awt.Dimension(170, 26));
        MnStatusLama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnStatusLamaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnStatusLama);

        setPreferredSize(new java.awt.Dimension(800, 410));
        setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(255, 255, 253));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setPreferredSize(new java.awt.Dimension(800, 410));
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        ScrollInput.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        ScrollInput.setOpaque(true);
        ScrollInput.setPreferredSize(new java.awt.Dimension(800, 410));

        FormData.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        FormData.setPreferredSize(new java.awt.Dimension(790, 410));
        FormData.setLayout(null);

        jLabel13.setText("Diagnosa :");
        FormData.add(jLabel13);
        jLabel13.setBounds(0, 10, 68, 23);

        Diagnosa.setHighlighter(null);
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormData.add(Diagnosa);
        Diagnosa.setBounds(71, 10, 687, 23);

        BtnCariPenyakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariPenyakit.setToolTipText("Alt+1");
        BtnCariPenyakit.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariPenyakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariPenyakitActionPerformed(evt);
            }
        });
        FormData.add(BtnCariPenyakit);
        BtnCariPenyakit.setBounds(761, 10, 28, 23);

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll1.setOpaque(true);
        Scroll1.setViewportView(tbICD10);

        FormData.add(Scroll1);
        Scroll1.setBounds(0, 36, 790, 165);

        jLabel15.setText("Prosedur :");
        FormData.add(jLabel15);
        jLabel15.setBounds(0, 211, 68, 23);

        Prosedur.setHighlighter(null);
        Prosedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurKeyPressed(evt);
            }
        });
        FormData.add(Prosedur);
        Prosedur.setBounds(71, 211, 687, 23);

        BtnCariProsedur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariProsedur.setToolTipText("Alt+1");
        BtnCariProsedur.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariProsedur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariProsedurActionPerformed(evt);
            }
        });
        FormData.add(BtnCariProsedur);
        BtnCariProsedur.setBounds(761, 211, 28, 23);

        Scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll2.setOpaque(true);

        tbICD9CM.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        Scroll2.setViewportView(tbICD9CM);

        FormData.add(Scroll2);
        Scroll2.setBounds(0, 237, 790, 165);

        ScrollInput.setViewportView(FormData);

        TabRawat.addTab("Input Data", ScrollInput);

        internalFrame2.setBorder(null);
        internalFrame2.setPreferredSize(new java.awt.Dimension(800, 410));
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(800, 410));

        tbDiagnosaPasien.setAutoCreateRowSorter(true);
        tbDiagnosaPasien.setComponentPopupMenu(jPopupMenu1);
        tbDiagnosaPasien.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 455));
        Scroll.setViewportView(tbDiagnosaPasien);

        internalFrame2.add(Scroll, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Data Diagnosa", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setPreferredSize(new java.awt.Dimension(800, 410));
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll3.setOpaque(true);
        Scroll3.setPreferredSize(new java.awt.Dimension(800, 455));

        tbProsedurPasien.setAutoCreateRowSorter(true);
        tbProsedurPasien.setComponentPopupMenu(jPopupMenu1);
        tbProsedurPasien.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 455));
        Scroll3.setViewportView(tbProsedurPasien);

        internalFrame3.add(Scroll3, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Data Prosedur", internalFrame3);

        add(TabRawat, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilICD10IDRG(true);
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbICD10.requestFocus();
        }
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void BtnCariPenyakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariPenyakitActionPerformed
        tampilICD10IDRG(false);
    }//GEN-LAST:event_BtnCariPenyakitActionPerformed

    private void ProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilICD9IDRG(true);
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbICD9CM.requestFocus();
        }
    }//GEN-LAST:event_ProsedurKeyPressed

    private void BtnCariProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariProsedurActionPerformed
        tampilICD9IDRG(false);
    }//GEN-LAST:event_BtnCariProsedurActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        pilihTab();
    }//GEN-LAST:event_TabRawatMouseClicked

    private void MnStatusBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnStatusBaruActionPerformed
        if (norawat.equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            Sequel.queryu2("update diagnosa_pasien set status_penyakit='Baru' where no_rawat=? and kd_penyakit=?", 2, new String[] {
                tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(), 2).toString(), tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(), 5).toString()
            });
            tampilDiagnosa();
        }
    }//GEN-LAST:event_MnStatusBaruActionPerformed

    private void MnStatusLamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnStatusLamaActionPerformed
        if (norawat.equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            Sequel.queryu2("update diagnosa_pasien set status_penyakit='Lama' where no_rawat=? and kd_penyakit=?", 2, new String[] {
                tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(), 2).toString(), tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(), 5).toString()
            });
            tampilDiagnosa();
        }
    }//GEN-LAST:event_MnStatusLamaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnCariPenyakit;
    private widget.Button BtnCariProsedur;
    private widget.TextBox Diagnosa;
    private widget.PanelBiasa FormData;
    private javax.swing.JMenuItem MnStatusBaru;
    private javax.swing.JMenuItem MnStatusLama;
    private widget.TextBox Prosedur;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane ScrollInput;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel13;
    private widget.Label jLabel15;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Table tbDiagnosaPasien;
    private widget.Table tbICD10;
    private widget.Table tbICD9CM;
    private widget.Table tbProsedurPasien;
    // End of variables declaration//GEN-END:variables
    
    public void tampilDiagnosa() {
        Valid.tabelKosong(tabModeDiagnosaPasien);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select r.tgl_registrasi, d.no_rawat, r.no_rkm_medis, px.nm_pasien, " +
            "d.kd_penyakit, p.nm_penyakit, d.status, d.status_penyakit from " +
            "diagnosa_pasien d join reg_periksa r on d.no_rawat = r.no_rawat join " +
            "pasien px on r.no_rkm_medis = px.no_rkm_medis join penyakit p on " +
            "d.kd_penyakit = p.kd_penyakit where d.no_rawat = ? " + (keyword.isBlank() ? "" :
            "and (d.no_rawat like ? or r.no_rkm_medis like ? or px.nm_pasien like ? or " +
            "d.kd_penyakit like ? or p.nm_penyakit like ? or d.status_penyakit like ? or " +
            "d.status like ?) ") + "order by d.prioritas"
        )) {
            int p = 0;
            ps.setString(++p, norawat);
            if (!keyword.isBlank()) {
                keyword = keyword.trim();
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeDiagnosaPasien.addRow(new Object[] {
                        false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilICD10IDRG(boolean pilihPertama) {
        try {
            ArrayNode rows = mapper.createArrayNode();
            ObjectNode row;
            ArrayList<String> icd = new ArrayList<>();
            
            if (!Diagnosa.getText().isBlank()) {
                for (int i = 0, p = 0; i < tabModeICD10.getRowCount(); i++) {
                    if ((Boolean) tabModeICD10.getValueAt(i, 0)) {
                        icd.add((String) tabModeICD10.getValueAt(i, 1));
                        row = mapper.createObjectNode();
                        row.put("idx", p++);
                        row.put("code1", (String) tabModeICD10.getValueAt(i, 1));
                        row.put("deskripsi", (String) tabModeICD10.getValueAt(i, 2));
                        row.put("validcode", (String) tabModeICD10.getValueAt(i, 3));
                        row.put("accpdx", (String) tabModeICD10.getValueAt(i, 4));
                        row.put("asterisk", (String) tabModeICD10.getValueAt(i, 5));
                        row.put("im", (String) tabModeICD10.getValueAt(i, 6));
                        rows.add(row);
                    }
                }
            } else {
                pilihPertama = false;
            }
            
            Valid.tabelKosong(tabModeICD10);
            
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from eklaim_icd10 where validcode = ? " + (Diagnosa.getText().isBlank() ? "" :
                "and (code1 like ? or code2 like ? or deskripsi like ?) ") + "order by code1 limit 1000"
            )) {
                int p = 0;
                ps.setString(++p, "1");
                if (!Diagnosa.getText().isBlank()) {
                    ps.setString(++p, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(++p, "%" + Diagnosa.getText().trim() + "%");
                    ps.setString(++p, "%" + Diagnosa.getText().trim() + "%");
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (icd.contains(rs.getString("code1"))) {
                        } else {
                            tabModeICD10.addRow(new Object[] {
                                pilihPertama, rs.getString("code1"), rs.getString("deskripsi"), rs.getString("validcode"),
                                rs.getString("accpdx"), rs.getString("asterisk"), rs.getString("im")
                            });
                            pilihPertama = false;
                        }
                    }
                }
            }
            
            rows.forEach(r -> {
                tabModeICD10.insertRow(r.path("idx").asInt(0), new Object[] {
                    true, r.path("code1").asText(""), r.path("deskripsi").asText(""), r.path("validcode").asText(""),
                    r.path("accpdx").asText(""), r.path("asterisk").asText(""), r.path("im").asText("")
                });
                tabModeICD10.fireTableRowsInserted(r.path("idx").asInt(0), r.path("idx").asInt(0));
            });
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void tampilICD9IDRG(boolean pilihPertama) {
        try {
            ArrayNode rows = mapper.createArrayNode();
            ObjectNode row;
            ArrayList<String> icd = new ArrayList<>();
            
            if (!Prosedur.getText().isBlank()) {
                for (int i = 0, p = 0; i < tabModeICD9CM.getRowCount(); i++) {
                    if ((Boolean) tabModeICD9CM.getValueAt(i, 0)) {
                        icd.add((String) tabModeICD9CM.getValueAt(i, 1));
                        row = mapper.createObjectNode();
                        row.put("idx", p++);
                        row.put("code1", (String) tabModeICD9CM.getValueAt(i, 1));
                        row.put("deskripsi", (String) tabModeICD9CM.getValueAt(i, 2));
                        row.put("validcode", (String) tabModeICD9CM.getValueAt(i, 3));
                        row.put("accpdx", (String) tabModeICD9CM.getValueAt(i, 4));
                        row.put("asterisk", (String) tabModeICD9CM.getValueAt(i, 5));
                        row.put("im", (String) tabModeICD9CM.getValueAt(i, 6));
                        rows.add(row);
                    }
                }
            } else {
                pilihPertama = false;
            }
            
            Valid.tabelKosong(tabModeICD9CM);
            
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from eklaim_icd9 where validcode = ? " + (Prosedur.getText().isBlank() ? "" :
                "and (code1 like ? or code2 like ? or deskripsi like ?) ") + "order by code1 limit 1000"
            )) {
                int p = 0;
                ps.setString(++p, "1");
                if (!Prosedur.getText().isBlank()) {
                    ps.setString(++p, "%" + Prosedur.getText().trim() + "%");
                    ps.setString(++p, "%" + Prosedur.getText().trim() + "%");
                    ps.setString(++p, "%" + Prosedur.getText().trim() + "%");
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tabModeICD9CM.addRow(new Object[] {
                            pilihPertama, rs.getString("code1"), rs.getString("deskripsi"), rs.getString("validcode"),
                            rs.getString("accpdx"), rs.getString("asterisk"), rs.getString("im")
                        });
                        pilihPertama = false;
                    }
                }
            }
            
            rows.forEach(r -> {
                tabModeICD9CM.insertRow(r.path("idx").asInt(0), new Object[] {
                    true, r.path("code1").asText(""), r.path("deskripsi").asText(""), r.path("validcode").asText(""),
                    r.path("accpdx").asText(""), r.path("asterisk").asText(""), r.path("im").asText("")
                });
                tabModeICD9CM.fireTableRowsInserted(r.path("idx").asInt(0), r.path("idx").asInt(0));
            });
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilProsedur() {
        Valid.tabelKosong(tabModeProsedurPasien);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select r.tgl_registrasi, p.no_rawat, r.no_rkm_medis, px.nm_pasien, p.kode, " +
            "i.deskripsi_panjang, p.status from prosedur_pasien p join reg_periksa r on " +
            "p.no_rawat = r.no_rawat join pasien px on r.no_rkm_medis = px.no_rkm_medis join " +
            "icd9 i on p.kode = i.kode where p.no_rawat = ? " + (keyword.isBlank() ? "" :
            "and (p.no_rawat like ? or r.no_rkm_medis like ? or px.nm_pasien like ? or " +
            "p.kode like ? or i.deskripsi_panjang like ? or p.status like ?) ") +
            "order by p.prioritas"
        )) {
            int p = 0;
            ps.setString(++p, norawat);
            if (!keyword.isBlank()) {
                keyword = keyword.trim();
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
                ps.setString(++p, "%" + keyword + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeProsedurPasien.addRow(new Object[] {
                        false, rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void setRM(String norawat, String norm, String tanggal1, String tanggal2, String status) {
        this.norawat = norawat;
        this.norm = norm;
        this.tanggal1 = tanggal1;
        this.tanggal2 = tanggal2;
        this.status = status;
    }

    public void simpan() {
        if (TabRawat.getSelectedIndex() > 0) {
            return;
        }
        /*
        try {
            koneksi.setAutoCommit(false);
            index = 1;
            for (i = 0; i < tbICD10.getRowCount(); i++) {
                if (tbICD10.getValueAt(i, 0).toString().equals("true")) {
                    if (Sequel.cariInteger("select count(diagnosa_pasien.kd_penyakit) from diagnosa_pasien " +
                         "inner join reg_periksa on diagnosa_pasien.no_rawat=reg_periksa.no_rawat " +
                         "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where " +
                         "pasien.no_rkm_medis='" + norm + "' and diagnosa_pasien.kd_penyakit='" + tbICD10.getValueAt(i, 1).toString() + "'") > 0) {
                        Sequel.menyimpan("diagnosa_pasien", "?,?,?,?,?", "Penyakit", 5, new String[] {
                            norawat, tbICD10.getValueAt(i, 1).toString(), status,
                            Sequel.cariIsi("select ifnull(MAX(diagnosa_pasien.prioritas)+1,1) from diagnosa_pasien where diagnosa_pasien.no_rawat=? and diagnosa_pasien.status='" + status + "'", norawat), "Lama"
                        });
                    } else {
                        Sequel.menyimpan("diagnosa_pasien", "?,?,?,?,?", "Penyakit", 5, new String[] {
                            norawat, tbICD10.getValueAt(i, 1).toString(), status,
                            Sequel.cariIsi("select ifnull(MAX(diagnosa_pasien.prioritas)+1,1) from diagnosa_pasien where diagnosa_pasien.no_rawat=? and diagnosa_pasien.status='" + status + "'", norawat), "Baru"
                        });
                    }

                    if (index == 1) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_utama=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_utama=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 2) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 3) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder2=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder2=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 4) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder3=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder3=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 5) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder4=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder4=?", 2, new String[] {
                                tbICD10.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    }

                    index++;
                }
            }
            koneksi.setAutoCommit(true);
            for (i = 0; i < tbICD10.getRowCount(); i++) {
                tbICD10.setValueAt(false, i, 0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan data. Kemungkinan ada data diagnosa yang sama dimasukkan sebelumnya...!");
        }

        try {
            koneksi.setAutoCommit(false);
            index = 1;
            for (i = 0; i < tbICD9CM.getRowCount(); i++) {
                if (tbICD9CM.getValueAt(i, 0).toString().equals("true")) {
                    Sequel.menyimpan("prosedur_pasien", "?,?,?,?", "ICD 9", 4, new String[] {
                        norawat, tbICD9CM.getValueAt(i, 1).toString(), status, Sequel.cariIsi("select ifnull(MAX(prosedur_pasien.prioritas)+1,1) from prosedur_pasien where prosedur_pasien.no_rawat=? and prosedur_pasien.status='" + status + "'", norawat)
                    });

                    if (index == 1) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_utama=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_utama=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 2) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_sekunder=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_sekunder=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 3) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_sekunder2=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_sekunder2=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 4) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_sekunder3=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_sekunder3=?", 2, new String[] {
                                tbICD9CM.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    }

                    index++;
                }
            }
            koneksi.setAutoCommit(true);
            for (i = 0; i < tbICD9CM.getRowCount(); i++) {
                tbICD9CM.setValueAt(false, i, 0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan data. Kemungkinan ada data prosedur/ICD9 yang sama dimasukkan sebelumnya...!");
        }
        */
        pilihTab();
    }

    public void pilihTab(int tab) {
        TabRawat.setSelectedIndex(tab);
        pilihTab();
    }

    public int tabSekarang() {
        return TabRawat.getSelectedIndex();
    }

    public void pilihTab() {
        if (TabRawat.getSelectedIndex() == 0) {
            tampilICD10IDRG(false);
            tampilICD9IDRG(false);
        } else if (TabRawat.getSelectedIndex() == 1) {
            tampilDiagnosa();
        } else if (TabRawat.getSelectedIndex() == 2) {
            tampilProsedur();
        }
    }

    public void hapus() {
        /*
        switch (TabRawat.getSelectedIndex()) {
            case 0:
                return;
            case 1:
                if (tabModeDiagnosaPasien.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
                } else {
                    for (i = 0; i < tbDiagnosaPasien.getRowCount(); i++) {
                        if (tbDiagnosaPasien.getValueAt(i, 0).toString().equals("true")) {
                            Sequel.queryu2("delete from diagnosa_pasien where no_rawat=? and kd_penyakit=?", 2, new String[] {
                                tbDiagnosaPasien.getValueAt(i, 2).toString(), tbDiagnosaPasien.getValueAt(i, 5).toString()
                            });
                        }
                    }
                }
                break;
            case 2:
                if (tabModeProsedurPasien.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
                } else {
                    for (i = 0; i < tbProsedurPasien.getRowCount(); i++) {
                        if (tbProsedurPasien.getValueAt(i, 0).toString().equals("true")) {
                            Sequel.queryu2("delete from prosedur_pasien where no_rawat=? and kode=?", 2, new String[] {
                                tbProsedurPasien.getValueAt(i, 2).toString(), tbProsedurPasien.getValueAt(i, 5).toString()
                            });
                        }
                    }
                }
                break;
            default:
                break;
        }
        */
        pilihTab();
    }

    public void revalidate(int width) {
        Scroll1.setSize(new Dimension(width - 10, Scroll1.getSize().height));
        Scroll1.setPreferredSize(new Dimension(width - 10, Scroll1.getSize().height));
        Diagnosa.setSize(new Dimension(width - Diagnosa.getX() - 39, Diagnosa.getSize().height));
        Diagnosa.setPreferredSize(new Dimension(width - Diagnosa.getX() - 39, Diagnosa.getSize().height));
        BtnCariPenyakit.setLocation(width - BtnCariPenyakit.getSize().width - 11, BtnCariPenyakit.getY());

        Scroll2.setSize(new Dimension(width - 10, Scroll2.getSize().height));
        Scroll2.setPreferredSize(new Dimension(width - 10, Scroll2.getSize().height));
        Prosedur.setSize(new Dimension(width - Prosedur.getX() - 39, Prosedur.getSize().height));
        Prosedur.setPreferredSize(new Dimension(width - Prosedur.getX() - 39, Prosedur.getSize().height));
        BtnCariProsedur.setLocation(width - BtnCariProsedur.getSize().width - 11, BtnCariProsedur.getY());

        FormData.setSize(new Dimension(width - 10, Scroll2.getHeight() + Scroll2.getY()));
        FormData.setPreferredSize(new Dimension(width - 10, Scroll2.getHeight() + Scroll2.getY()));
    }

    public JTabbedPane getTabbedPane() {
        return TabRawat;
    }
}
