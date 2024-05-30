/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laporan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author khanzamedia
 */
public class PanelDiagnosaSmc extends widget.panelisi {
    private final DefaultTableModel TabModeDiagnosaPasien, tabModeDiagnosa, tabModeProsedur, TabModeTindakanPasien;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private PreparedStatement pspenyakit, psdiagnosapasien, psprosedur, pstindakanpasien;
    private ResultSet rs;
    private int jml = 0, i = 0, index = 0;
    private String[] kode, nama, ciripny, keterangan, kategori, cirium, kode2, panjang, pendek;
    private boolean[] pilih;
    private boolean GUNAKANDIAGNOSAEKLAIM = koneksiDB.GUNAKANDIAGNOSAEKLAIM();
    public String norawat = "", status = "", norm = "", tanggal1 = "", tanggal2 = "", keyword = "";

    /**
     * Creates new form panelDiagnosa
     */
    public PanelDiagnosaSmc() {
        initComponents();
        TabModeDiagnosaPasien = new DefaultTableModel(null, new Object[] {
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
        tbDiagnosaPasien.setModel(TabModeDiagnosaPasien);
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

        tabModeDiagnosa = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Penyakit", "Ciri-ciri", "Keterangan", "Kategori", "Ciri-ciri Umum"
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
        tbDiagnosa.setModel(tabModeDiagnosa);
        tbDiagnosa.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDiagnosa.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDiagnosa.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbDiagnosa.getColumnModel().getColumn(1).setPreferredWidth(40);
        tbDiagnosa.getColumnModel().getColumn(2).setPreferredWidth(280);
        tbDiagnosa.getColumnModel().getColumn(3).setPreferredWidth(285);
        tbDiagnosa.getColumnModel().getColumn(4).setPreferredWidth(75);
        tbDiagnosa.getColumnModel().getColumn(5).setPreferredWidth(75);
        tbDiagnosa.getColumnModel().getColumn(6).setPreferredWidth(75);
        tbDiagnosa.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeProsedur = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Deskripsi Panjang", "Deskripsi Pendek"
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
        tbProsedur.setModel(tabModeProsedur);
        tbProsedur.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbProsedur.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbProsedur.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbProsedur.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbProsedur.getColumnModel().getColumn(2).setPreferredWidth(350);
        tbProsedur.getColumnModel().getColumn(3).setPreferredWidth(350);
        tbProsedur.setDefaultRenderer(Object.class, new WarnaTable());

        TabModeTindakanPasien = new DefaultTableModel(null, new Object[] {
            "P", "Tgl. Rawat", "No. Rawat", "No. RM", "Nama Pasien", "Kode", "Nama Prosedur", "Status"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbTindakanPasien.setModel(TabModeTindakanPasien);
        tbTindakanPasien.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 8; i++) {
            TableColumn column = tbTindakanPasien.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(80);
            } else if (i == 2) {
                column.setPreferredWidth(110);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(160);
            } else if (i == 5) {
                column.setPreferredWidth(50);
            } else if (i == 6) {
                column.setPreferredWidth(300);
            } else if (i == 7) {
                column.setPreferredWidth(50);
            }
        }
        tbTindakanPasien.setDefaultRenderer(Object.class, new WarnaTable());

        Diagnosa.setDocument(new batasInput((byte) 100).getKata(Diagnosa));
        Prosedur.setDocument(new batasInput((byte) 100).getKata(Prosedur));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            Diagnosa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (Diagnosa.getText().length() > 2) {
                        tampildiagnosa();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (Diagnosa.getText().length() > 2) {
                        tampildiagnosa();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (Diagnosa.getText().length() > 2) {
                        tampildiagnosa();
                    }
                }
            });
        }

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            Prosedur.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (Prosedur.getText().length() > 2) {
                        tampilprosedure();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (Prosedur.getText().length() > 2) {
                        tampilprosedure();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (Prosedur.getText().length() > 2) {
                        tampilprosedure();
                    }
                }
            });
        }
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
        tbDiagnosa = new widget.Table();
        jLabel15 = new widget.Label();
        Prosedur = new widget.TextBox();
        BtnCariProsedur = new widget.Button();
        Scroll2 = new widget.ScrollPane();
        tbProsedur = new widget.Table();
        internalFrame2 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbDiagnosaPasien = new widget.Table();
        internalFrame3 = new widget.InternalFrame();
        Scroll3 = new widget.ScrollPane();
        tbTindakanPasien = new widget.Table();

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
        BtnCariPenyakit.setMnemonic('1');
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
        Scroll1.setViewportView(tbDiagnosa);

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
        BtnCariProsedur.setMnemonic('1');
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

        tbProsedur.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        Scroll2.setViewportView(tbProsedur);

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

        tbTindakanPasien.setAutoCreateRowSorter(true);
        tbTindakanPasien.setComponentPopupMenu(jPopupMenu1);
        tbTindakanPasien.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 455));
        Scroll3.setViewportView(tbTindakanPasien);

        internalFrame3.add(Scroll3, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Data Prosedur", internalFrame3);

        add(TabRawat, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampildiagnosa();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbDiagnosa.requestFocus();
        }
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void BtnCariPenyakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariPenyakitActionPerformed
        tampildiagnosa();
    }//GEN-LAST:event_BtnCariPenyakitActionPerformed

    private void ProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilprosedure();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbProsedur.requestFocus();
        }
    }//GEN-LAST:event_ProsedurKeyPressed

    private void BtnCariProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariProsedurActionPerformed
        tampilprosedure();
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
            tampil();
        }
    }//GEN-LAST:event_MnStatusBaruActionPerformed

    private void MnStatusLamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnStatusLamaActionPerformed
        if (norawat.equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            Sequel.queryu2("update diagnosa_pasien set status_penyakit='Lama' where no_rawat=? and kd_penyakit=?", 2, new String[] {
                tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(), 2).toString(), tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(), 5).toString()
            });
            tampil();
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
    private widget.Table tbDiagnosa;
    private widget.Table tbDiagnosaPasien;
    private widget.Table tbProsedur;
    private widget.Table tbTindakanPasien;
    // End of variables declaration//GEN-END:variables
    public void tampil() {
        Valid.tabelKosong(TabModeDiagnosaPasien);
        try {
            psdiagnosapasien = koneksi.prepareStatement("select reg_periksa.tgl_registrasi,diagnosa_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                + "diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit, diagnosa_pasien.status,diagnosa_pasien.status_penyakit "
                + "from diagnosa_pasien inner join reg_periksa on diagnosa_pasien.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                + "where reg_periksa.no_rawat = ? "
                + (keyword.trim().equals("") ? "" : "and (diagnosa_pasien.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                + "pasien.nm_pasien like ? or diagnosa_pasien.kd_penyakit like ? or penyakit.nm_penyakit like ? or "
                + "diagnosa_pasien.status_penyakit like ? or diagnosa_pasien.status like ?)")
                + "order by reg_periksa.tgl_registrasi,diagnosa_pasien.prioritas ");
            try {
                psdiagnosapasien.setString(1, norawat);
                if (!keyword.trim().equals("")) {
                    psdiagnosapasien.setString(2, "%" + keyword + "%");
                    psdiagnosapasien.setString(3, "%" + keyword + "%");
                    psdiagnosapasien.setString(4, "%" + keyword + "%");
                    psdiagnosapasien.setString(5, "%" + keyword + "%");
                    psdiagnosapasien.setString(6, "%" + keyword + "%");
                    psdiagnosapasien.setString(7, "%" + keyword + "%");
                    psdiagnosapasien.setString(8, "%" + keyword + "%");
                }

                rs = psdiagnosapasien.executeQuery();
                while (rs.next()) {
                    TabModeDiagnosaPasien.addRow(new Object[] {
                        false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)
                    });
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (psdiagnosapasien != null) {
                    psdiagnosapasien.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    public int getRecord() {
        if (TabRawat.getSelectedIndex() == 0) {
            i = 0;
        } else if (TabRawat.getSelectedIndex() == 1) {
            i = tbDiagnosaPasien.getRowCount();
        } else if (TabRawat.getSelectedIndex() == 2) {
            i = tbTindakanPasien.getRowCount();
        }
        return i;
    }

    private void tampildiagnosa() {
        try {
            ArrayList<String> kode = new ArrayList();
            ArrayList<String> nama = new ArrayList();
            ArrayList<String> ciripny = new ArrayList();
            ArrayList<String> keterangan = new ArrayList();
            ArrayList<String> kategori = new ArrayList();
            ArrayList<String> cirium = new ArrayList();
            String notIn = "and penyakit.kd_penyakit not in (";

            int jml = 0;
            for (int i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    kode.add(tbDiagnosa.getValueAt(i, 1).toString());
                    nama.add(tbDiagnosa.getValueAt(i, 2).toString());
                    ciripny.add(tbDiagnosa.getValueAt(i, 3).toString());
                    keterangan.add(tbDiagnosa.getValueAt(i, 4).toString());
                    kategori.add(tbDiagnosa.getValueAt(i, 5).toString());
                    cirium.add(tbDiagnosa.getValueAt(i, 6).toString());
                    notIn = notIn.concat("?, ");
                    ++jml;
                }
            }

            Valid.tabelKosong(tabModeDiagnosa);
            for (int i = 0; i < jml; i++) {
                tabModeDiagnosa.addRow(new Object[] {
                    true, kode.get(i), nama.get(i), ciripny.get(i), keterangan.get(i), kategori.get(i), cirium.get(i)
                });
            }
            
            if (jml > 0) {
                notIn = notIn.substring(0, notIn.length() - 2).concat(")");
            } else {
                notIn = "";
            }
            
            String[] keywords = Diagnosa.getText().trim().split("\\s+");
            
            String searchQuery = "";
            
            if (! Diagnosa.getText().trim().isBlank()) {
                for (int i = 0; i < keywords.length; i++) {
                    if (i == 0) {
                        searchQuery = " and (concat_ws(' ', penyakit.kd_penyakit, penyakit.nm_penyakit, penyakit.ciri_ciri, penyakit.keterangan, kategori_penyakit.nm_kategori, kategori_penyakit.ciri_umum) like ?";
                    } else {
                        searchQuery = searchQuery.concat(" and concat_ws(' ', penyakit.kd_penyakit, penyakit.nm_penyakit, penyakit.ciri_ciri, penyakit.keterangan, kategori_penyakit.nm_kategori, kategori_penyakit.ciri_umum) like ?");
                    }
                }
                searchQuery = searchQuery.concat(")");
            }
            
            String sql = "select penyakit.kd_penyakit, penyakit.nm_penyakit, penyakit.ciri_ciri, penyakit.keterangan, kategori_penyakit.nm_kategori, kategori_penyakit.ciri_umum " +
                "from kategori_penyakit join penyakit on penyakit.kd_ktg = kategori_penyakit.kd_ktg where exists(select * from eklaim_icd10 where eklaim_icd10.code = penyakit.kd_penyakit and eklaim_icd10.status = '1') " +
                notIn + searchQuery + " order by penyakit.kd_penyakit limit 1000";
            
            try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                if (jml > 0) {
                    for (int i = 0; i < jml; i++) {
                        ps.setString(i + 1, kode.get(i));
                    }
                }
                if (! Diagnosa.getText().trim().isBlank()) {
                    for (int i = 0; i < keywords.length; i++) {
                        ps.setString(i + jml + 1, "%" + keywords[i] + "%");
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tabModeDiagnosa.addRow(new Object[] {
                            false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)
                        });
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilprosedure() {
        try {
            ArrayList<String> kode = new ArrayList();
            ArrayList<String> panjang = new ArrayList();
            ArrayList<String> pendek = new ArrayList();
            String notIn = "and kode not in (";

            int jml = 0;
            for (int i = 0; i < tbProsedur.getRowCount(); i++) {
                if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                    kode.add(tbProsedur.getValueAt(i, 1).toString());
                    panjang.add(tbProsedur.getValueAt(i, 2).toString());
                    pendek.add(tbProsedur.getValueAt(i, 3).toString());
                    notIn = notIn.concat("?, ");
                    ++jml;
                }
            }

            Valid.tabelKosong(tabModeProsedur);
            for (int i = 0; i < jml; i++) {
                tabModeProsedur.addRow(new Object[] {
                    true, kode.get(i), panjang.get(i), pendek.get(i)
                });
            }
            
            if (jml > 0) {
                notIn = notIn.substring(0, notIn.length() - 2).concat(")");
            } else {
                notIn = "";
            }
            
            String[] keywords = Prosedur.getText().trim().split("\\s+");
            
            String searchQuery = "";
            
            if (! Prosedur.getText().isBlank()) {
                for (int i = 0; i < keywords.length; i++) {
                    if (i == 0) {
                        searchQuery = " and (concat_ws(' ', kode, deskripsi_panjang, deskripsi_pendek) like ?";
                    } else {
                        searchQuery = searchQuery.concat(" and concat_ws(' ', kode, deskripsi_panjang, deskripsi_pendek) like ?");
                    }
                }
                searchQuery = searchQuery.concat(")");
            }
            
            String sql = "select * from icd9 where 1 " + notIn + searchQuery + " order by kode limit 1000";
            
            try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                if (jml > 0) {
                    for (int i = 0; i < jml; i++) {
                        ps.setString(i + 1, kode.get(i));
                    }
                }
                if (! Prosedur.getText().isBlank()) {
                    for (int i = 0; i < keywords.length; i++) {
                        ps.setString(i + jml + 1, "%" + keywords[i] + "%");
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tabModeProsedur.addRow(new Object[] {
                            false, rs.getString(1), rs.getString(2), rs.getString(3)
                        });
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampil2() {
        Valid.tabelKosong(TabModeTindakanPasien);
        try {
            pstindakanpasien = koneksi.prepareStatement("select reg_periksa.tgl_registrasi,prosedur_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                + "prosedur_pasien.kode,icd9.deskripsi_panjang, prosedur_pasien.status "
                + "from prosedur_pasien inner join reg_periksa on prosedur_pasien.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join icd9 on prosedur_pasien.kode=icd9.kode "
                + "where reg_periksa.no_rawat = ? "
                + (keyword.trim().equals("") ? "" : "and (prosedur_pasien.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                + "pasien.nm_pasien like ? or prosedur_pasien.kode like ? or icd9.deskripsi_panjang like ? or "
                + "prosedur_pasien.status like ?) ") + "order by prosedur_pasien.prioritas ");
            try {
                pstindakanpasien.setString(1, norawat);
                if (!keyword.trim().equals("")) {
                    pstindakanpasien.setString(2, "%" + keyword + "%");
                    pstindakanpasien.setString(3, "%" + keyword + "%");
                    pstindakanpasien.setString(4, "%" + keyword + "%");
                    pstindakanpasien.setString(5, "%" + keyword + "%");
                    pstindakanpasien.setString(6, "%" + keyword + "%");
                    pstindakanpasien.setString(7, "%" + keyword + "%");
                }

                rs = pstindakanpasien.executeQuery();
                while (rs.next()) {
                    TabModeTindakanPasien.addRow(new Object[] {false, rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7)});
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstindakanpasien != null) {
                    pstindakanpasien.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
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
        
        try {
            koneksi.setAutoCommit(false);
            index = 1;
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    if (Sequel.cariInteger(
                        "select count(diagnosa_pasien.kd_penyakit) from diagnosa_pasien "
                        + "inner join reg_periksa on diagnosa_pasien.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where "
                        + "pasien.no_rkm_medis='" + norm + "' and diagnosa_pasien.kd_penyakit='" + tbDiagnosa.getValueAt(i, 1).toString() + "'") > 0) {
                        Sequel.menyimpan("diagnosa_pasien", "?,?,?,?,?", "Penyakit", 5, new String[] {
                            norawat, tbDiagnosa.getValueAt(i, 1).toString(), status,
                            Sequel.cariIsi("select ifnull(MAX(diagnosa_pasien.prioritas)+1,1) from diagnosa_pasien where diagnosa_pasien.no_rawat=? and diagnosa_pasien.status='" + status + "'", norawat), "Lama"
                        });
                    } else {
                        Sequel.menyimpan("diagnosa_pasien", "?,?,?,?,?", "Penyakit", 5, new String[] {
                            norawat, tbDiagnosa.getValueAt(i, 1).toString(), status,
                            Sequel.cariIsi("select ifnull(MAX(diagnosa_pasien.prioritas)+1,1) from diagnosa_pasien where diagnosa_pasien.no_rawat=? and diagnosa_pasien.status='" + status + "'", norawat), "Baru"
                        });
                    }

                    if (index == 1) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_utama=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_utama=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 2) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 3) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder2=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder2=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 4) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder3=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder3=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 5) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_diagnosa_sekunder4=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_diagnosa_sekunder4=?", 2, new String[] {
                                tbDiagnosa.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    }

                    index++;
                }
            }
            koneksi.setAutoCommit(true);
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                tbDiagnosa.setValueAt(false, i, 0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan data. Kemungkinan ada data diagnosa yang sama dimasukkan sebelumnya...!");
        }

        try {
            koneksi.setAutoCommit(false);
            index = 1;
            for (i = 0; i < tbProsedur.getRowCount(); i++) {
                if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                    Sequel.menyimpan("prosedur_pasien", "?,?,?,?", "ICD 9", 4, new String[] {
                        norawat, tbProsedur.getValueAt(i, 1).toString(), status, Sequel.cariIsi("select ifnull(MAX(prosedur_pasien.prioritas)+1,1) from prosedur_pasien where prosedur_pasien.no_rawat=? and prosedur_pasien.status='" + status + "'", norawat)
                    });

                    if (index == 1) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_utama=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_utama=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 2) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_sekunder=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_sekunder=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 3) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_sekunder2=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_sekunder2=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    } else if (index == 4) {
                        if (status.equals("Ralan")) {
                            Sequel.mengedit("resume_pasien", "no_rawat=?", "kd_prosedur_sekunder3=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        } else if (status.equals("Ranap")) {
                            Sequel.mengedit("resume_pasien_ranap", "no_rawat=?", "kd_prosedur_sekunder3=?", 2, new String[] {
                                tbProsedur.getValueAt(i, 1).toString(), norawat
                            });
                        }
                    }

                    index++;
                }
            }
            koneksi.setAutoCommit(true);
            for (i = 0; i < tbProsedur.getRowCount(); i++) {
                tbProsedur.setValueAt(false, i, 0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan data. Kemungkinan ada data prosedur/ICD9 yang sama dimasukkan sebelumnya...!");
        }
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
            tampildiagnosa();
            tampilprosedure();
        } else if (TabRawat.getSelectedIndex() == 1) {
            tampil();
        } else if (TabRawat.getSelectedIndex() == 2) {
            tampil2();
        }
    }

    public void batal() {
        Diagnosa.setText("");
        for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
            tbDiagnosa.setValueAt(false, i, 0);
        }
        for (i = 0; i < tbProsedur.getRowCount(); i++) {
            tbProsedur.setValueAt(false, i, 0);
        }
        Prosedur.setText("");
    }

    public void hapus() {
        switch (TabRawat.getSelectedIndex()) {
            case 0: return;
            case 1:
                if (TabModeDiagnosaPasien.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
                } else {
                    for (i = 0; i < tbDiagnosaPasien.getRowCount(); i++) {
                        if (tbDiagnosaPasien.getValueAt(i, 0).toString().equals("true")) {
                            Sequel.queryu2("delete from diagnosa_pasien where no_rawat=? and kd_penyakit=?", 2, new String[] {
                                tbDiagnosaPasien.getValueAt(i, 2).toString(), tbDiagnosaPasien.getValueAt(i, 5).toString()
                            });
                        }
                    }
                }   break;
            case 2:
                if (TabModeTindakanPasien.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
                } else {
                    for (i = 0; i < tbTindakanPasien.getRowCount(); i++) {
                        if (tbTindakanPasien.getValueAt(i, 0).toString().equals("true")) {
                            Sequel.queryu2("delete from prosedur_pasien where no_rawat=? and kode=?", 2, new String[] {
                                tbTindakanPasien.getValueAt(i, 2).toString(), tbTindakanPasien.getValueAt(i, 5).toString()
                            });
                        }
                    }
                }   break;
            default:
                break;
        }
        pilihTab();
    }

    public void cetak() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (TabRawat.getSelectedIndex() == 1) {
            if (TabModeDiagnosaPasien.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            } else if (TabModeDiagnosaPasien.getRowCount() != 0) {
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars());
                param.put("alamatrs", akses.getalamatrs());
                param.put("kotars", akses.getkabupatenrs());
                param.put("propinsirs", akses.getpropinsirs());
                param.put("kontakrs", akses.getkontakrs());
                param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptDiagnosa.jasper", "report", "::[ Data Diagnosa Pasien ]::",
                    "select reg_periksa.tgl_registrasi,diagnosa_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                    + "diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit, diagnosa_pasien.status,diagnosa_pasien.status_penyakit "
                    + "from diagnosa_pasien inner join reg_periksa inner join pasien inner join penyakit "
                    + "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                    + "where reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and reg_periksa.tgl_registrasi like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and diagnosa_pasien.no_rawat like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and reg_periksa.no_rkm_medis like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and pasien.nm_pasien like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and diagnosa_pasien.kd_penyakit like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and penyakit.nm_penyakit like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and diagnosa_pasien.status_penyakit like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and diagnosa_pasien.status like '%" + keyword + "%' "
                    + "order by reg_periksa.tgl_registrasi,diagnosa_pasien.prioritas ", param);
            }
        } else if (TabRawat.getSelectedIndex() == 2) {
            if (TabModeTindakanPasien.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            } else if (TabModeTindakanPasien.getRowCount() != 0) {
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars());
                param.put("alamatrs", akses.getalamatrs());
                param.put("kotars", akses.getkabupatenrs());
                param.put("propinsirs", akses.getpropinsirs());
                param.put("kontakrs", akses.getkontakrs());
                param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptProsedur.jasper", "report", "::[ Data Prosedur Tindakan Pasien ]::",
                    "select reg_periksa.tgl_registrasi,prosedur_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                    + "prosedur_pasien.kode,icd9.deskripsi_panjang, prosedur_pasien.status "
                    + "from prosedur_pasien inner join reg_periksa inner join pasien inner join icd9 "
                    + "on prosedur_pasien.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "and prosedur_pasien.kode=icd9.kode "
                    + "where reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and reg_periksa.tgl_registrasi like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and prosedur_pasien.no_rawat like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and reg_periksa.no_rkm_medis like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and pasien.nm_pasien like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and prosedur_pasien.kode like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and icd9.deskripsi_panjang like '%" + keyword + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + tanggal1 + "' and '" + tanggal2 + "' and reg_periksa.no_rkm_medis like '%" + norm + "%' and prosedur_pasien.status like '%" + keyword + "%' "
                    + "order by reg_periksa.tgl_registrasi,prosedur_pasien.prioritas ", param);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
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
