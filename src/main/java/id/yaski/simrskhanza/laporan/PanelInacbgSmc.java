/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.yaski.simrskhanza.laporan;

import id.yaski.simrskhanza.fungsi.WarnaTable;
import id.yaski.simrskhanza.fungsi.batasInput;
import id.yaski.simrskhanza.fungsi.koneksiDB;
import id.yaski.simrskhanza.fungsi.sekuel;
import id.yaski.simrskhanza.fungsi.validasi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author khanzamedia
 */
public class PanelInacbgSmc extends widget.panelisi {
    private final DefaultTableModel tabModeDiagnosaPasien, tabModeICD10, tabModeICD9CM, tabModeProsedurPasien;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ArrayList<DiagnosaINACBGListener> diagnosaListeners = new ArrayList<>();
    private final ArrayList<ProsedurINACBGListener> prosedurListeners = new ArrayList<>();
    private String nosep = "";
    private int dx = 1, px = 1;
    private boolean hapusOtomatis = false;
    private JComponent nextFocusableComponent;

    /**
     * Creates new form panelDiagnosa
     */
    public PanelInacbgSmc() {
        initComponents();
        tabModeICD10 = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Deskripsi", "VALIDCODE", "urut"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return java.lang.Boolean.class;
                }

                if (columnIndex == 4) {
                    return java.lang.Integer.class;
                }

                return java.lang.String.class;
            }
        };
        tbICD10.setModel(tabModeICD10);
        tbICD10.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbICD10.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbICD10.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbICD10.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbICD10.getColumnModel().getColumn(2).setPreferredWidth(480);
        tbICD10.getColumnModel().getColumn(3).setMinWidth(0);
        tbICD10.getColumnModel().getColumn(3).setMaxWidth(0);
        tbICD10.getColumnModel().getColumn(4).setMinWidth(0);
        tbICD10.getColumnModel().getColumn(4).setMaxWidth(0);
        tbICD10.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeICD9CM = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Deskripsi", "VALIDCODE", "urut"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return java.lang.Boolean.class;
                }

                if (columnIndex == 4) {
                    return java.lang.Integer.class;
                }

                return java.lang.String.class;
            }
        };
        tbICD9CM.setModel(tabModeICD9CM);
        tbICD9CM.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbICD9CM.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbICD9CM.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbICD9CM.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbICD9CM.getColumnModel().getColumn(2).setPreferredWidth(480);
        tbICD9CM.getColumnModel().getColumn(3).setMinWidth(0);
        tbICD9CM.getColumnModel().getColumn(3).setMaxWidth(0);
        tbICD9CM.getColumnModel().getColumn(4).setMinWidth(0);
        tbICD9CM.getColumnModel().getColumn(4).setMaxWidth(0);
        tbICD9CM.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDiagnosaPasien = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Deskripsi", "Status", "urut", "error", "locked"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return java.lang.Boolean.class;
                }

                if (columnIndex == 4) {
                    return java.lang.Integer.class;
                }

                return java.lang.String.class;
            }
        };
        tbDiagnosaPasien.setModel(tabModeDiagnosaPasien);
        tbDiagnosaPasien.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDiagnosaPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDiagnosaPasien.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbDiagnosaPasien.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbDiagnosaPasien.getColumnModel().getColumn(2).setPreferredWidth(430);
        tbDiagnosaPasien.getColumnModel().getColumn(3).setPreferredWidth(50);
        tbDiagnosaPasien.getColumnModel().getColumn(4).setMinWidth(0);
        tbDiagnosaPasien.getColumnModel().getColumn(4).setMaxWidth(0);
        tbDiagnosaPasien.getColumnModel().getColumn(5).setMinWidth(0);
        tbDiagnosaPasien.getColumnModel().getColumn(5).setMaxWidth(0);
        tbDiagnosaPasien.getColumnModel().getColumn(6).setMinWidth(0);
        tbDiagnosaPasien.getColumnModel().getColumn(6).setMaxWidth(0);
        tbDiagnosaPasien.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 1) {
                    component.setBackground(new Color(255, 244, 244));
                    component.setForeground(new Color(50, 50, 50));
                } else {
                    component.setBackground(new Color(255, 255, 255));
                    component.setForeground(new Color(50, 50, 50));
                }
                if (table.getValueAt(row, 5).toString().equals("1")) {
                    component.setBackground(new Color(200, 0, 0));
                    component.setForeground(new Color(255, 255, 255));
                }
                return component;
            }
        });

        tabModeProsedurPasien = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Deskripsi", "Status", "urut", "error", "locked"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return java.lang.Boolean.class;
                }

                if (columnIndex == 4) {
                    return java.lang.Integer.class;
                }

                return java.lang.String.class;
            }
        };
        tbProsedurPasien.setModel(tabModeProsedurPasien);
        tbProsedurPasien.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbProsedurPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbProsedurPasien.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbProsedurPasien.getColumnModel().getColumn(1).setPreferredWidth(50);
        tbProsedurPasien.getColumnModel().getColumn(2).setPreferredWidth(430);
        tbProsedurPasien.getColumnModel().getColumn(3).setPreferredWidth(50);
        tbProsedurPasien.getColumnModel().getColumn(4).setMinWidth(0);
        tbProsedurPasien.getColumnModel().getColumn(4).setMaxWidth(0);
        tbProsedurPasien.getColumnModel().getColumn(5).setMinWidth(0);
        tbProsedurPasien.getColumnModel().getColumn(5).setMaxWidth(0);
        tbProsedurPasien.getColumnModel().getColumn(6).setMinWidth(0);
        tbProsedurPasien.getColumnModel().getColumn(6).setMaxWidth(0);
        tbProsedurPasien.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 1) {
                    component.setBackground(new Color(255, 244, 244));
                    component.setForeground(new Color(50, 50, 50));
                } else {
                    component.setBackground(new Color(255, 255, 255));
                    component.setForeground(new Color(50, 50, 50));
                }
                if (table.getValueAt(row, 5).toString().equals("1")) {
                    component.setBackground(new Color(200, 0, 0));
                    component.setForeground(new Color(255, 230, 230));
                }
                return component;
            }
        });

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
        ppJadikanDiagnosaUtama = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        ppJadikanProsedurUtama = new javax.swing.JMenuItem();
        TabRawat = new javax.swing.JTabbedPane();
        ScrollInput = new id.yaski.simrskhanza.widget.ScrollPane();
        FormData = new id.yaski.simrskhanza.widget.PanelBiasa();
        jLabel13 = new id.yaski.simrskhanza.widget.Label();
        Diagnosa = new id.yaski.simrskhanza.widget.TextBox();
        BtnCariPenyakit = new id.yaski.simrskhanza.widget.Button();
        Scroll1 = new id.yaski.simrskhanza.widget.ScrollPane();
        tbICD10 = new id.yaski.simrskhanza.widget.Table();
        jLabel15 = new id.yaski.simrskhanza.widget.Label();
        Prosedur = new id.yaski.simrskhanza.widget.TextBox();
        BtnCariProsedur = new id.yaski.simrskhanza.widget.Button();
        Scroll2 = new id.yaski.simrskhanza.widget.ScrollPane();
        tbICD9CM = new id.yaski.simrskhanza.widget.Table();
        internalFrame2 = new id.yaski.simrskhanza.widget.InternalFrame();
        Scroll = new id.yaski.simrskhanza.widget.ScrollPane();
        tbDiagnosaPasien = new id.yaski.simrskhanza.widget.Table();
        internalFrame3 = new id.yaski.simrskhanza.widget.InternalFrame();
        Scroll3 = new id.yaski.simrskhanza.widget.ScrollPane();
        tbProsedurPasien = new id.yaski.simrskhanza.widget.Table();

        ppJadikanDiagnosaUtama.setBackground(new java.awt.Color(255, 255, 254));
        ppJadikanDiagnosaUtama.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppJadikanDiagnosaUtama.setForeground(new java.awt.Color(50, 50, 50));
        ppJadikanDiagnosaUtama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppJadikanDiagnosaUtama.setText("Jadikan Diagnosa Utama");
        ppJadikanDiagnosaUtama.setPreferredSize(new java.awt.Dimension(175, 26));
        ppJadikanDiagnosaUtama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppJadikanDiagnosaUtamaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppJadikanDiagnosaUtama);

        ppJadikanProsedurUtama.setBackground(new java.awt.Color(255, 255, 254));
        ppJadikanProsedurUtama.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppJadikanProsedurUtama.setForeground(new java.awt.Color(50, 50, 50));
        ppJadikanProsedurUtama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppJadikanProsedurUtama.setText("Jadikan Prosedur Utama");
        ppJadikanProsedurUtama.setToolTipText("");
        ppJadikanProsedurUtama.setPreferredSize(new java.awt.Dimension(175, 26));
        ppJadikanProsedurUtama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppJadikanProsedurUtamaActionPerformed(evt);
            }
        });
        jPopupMenu2.add(ppJadikanProsedurUtama);

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

        Diagnosa.setNextFocusableComponent(Prosedur);
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

        tbICD10.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbICD10PropertyChange(evt);
            }
        });
        Scroll1.setViewportView(tbICD10);

        FormData.add(Scroll1);
        Scroll1.setBounds(0, 36, 790, 165);

        jLabel15.setText("Prosedur :");
        FormData.add(jLabel15);
        jLabel15.setBounds(0, 211, 68, 23);

        Prosedur.setNextFocusableComponent(this.nextFocusableComponent);
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

        tbICD9CM.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbICD9CM.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbICD9CMPropertyChange(evt);
            }
        });
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

        tbDiagnosaPasien.setComponentPopupMenu(jPopupMenu1);
        tbDiagnosaPasien.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 455));
        tbDiagnosaPasien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbDiagnosaPasienMousePressed(evt);
            }
        });
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
        tbProsedurPasien.setComponentPopupMenu(jPopupMenu2);
        tbProsedurPasien.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 455));
        tbProsedurPasien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbProsedurPasienMousePressed(evt);
            }
        });
        Scroll3.setViewportView(tbProsedurPasien);

        internalFrame3.add(Scroll3, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Data Prosedur", internalFrame3);

        add(TabRawat, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilICD10(true);
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbICD10.requestFocus();
        }
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void BtnCariPenyakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariPenyakitActionPerformed
        tampilICD10(false);
    }//GEN-LAST:event_BtnCariPenyakitActionPerformed

    private void ProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilICD9CM(true);
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbICD9CM.requestFocus();
        }
    }//GEN-LAST:event_ProsedurKeyPressed

    private void BtnCariProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariProsedurActionPerformed
        tampilICD9CM(false);
    }//GEN-LAST:event_BtnCariProsedurActionPerformed

    private void tbICD9CMPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbICD9CMPropertyChange
        if (tabModeICD9CM.getRowCount() > 0 &&
            evt.getPropertyName().equals("tableCellEditor") &&
            (!tbICD9CM.isEditing()) &&
            (tbICD9CM.getSelectedColumn() == 0)) {
            if (tbICD9CM.getSelectedColumn() == 0) {
                if ((Boolean) tabModeICD9CM.getValueAt(tbICD9CM.getSelectedRow(), 0) &&
                    !cekValiditasICD9CM(tbICD9CM.getSelectedRow())) {
                    tabModeICD9CM.setValueAt(1, tbICD9CM.getSelectedRow(), 1);
                }
            }
        }
    }//GEN-LAST:event_tbICD9CMPropertyChange

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        pilihTab();
    }//GEN-LAST:event_TabRawatMouseClicked

    private void tbICD10PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbICD10PropertyChange
        if (tabModeICD10.getRowCount() > 0 &&
            evt.getPropertyName().equals("tableCellEditor") &&
            (!tbICD10.isEditing()) &&
            tbICD10.getSelectedColumn() == 0) {
            if ((Boolean) tabModeICD10.getValueAt(tbICD10.getSelectedRow(), 0) &&
                !cekValiditasICD10(tbICD10.getSelectedRow())) {
                tabModeICD10.setValueAt(false, tbICD10.getSelectedRow(), 0);
            }
        }
    }//GEN-LAST:event_tbICD10PropertyChange

    private void ppJadikanDiagnosaUtamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppJadikanDiagnosaUtamaActionPerformed
        if (tbDiagnosaPasien.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih diagnosa yang mau diangkat jadi diagnosa utama..!!");
        } else if (Sequel.cariExistsSmc("select * from inacbg_klaim_final_smc i where i.no_sep = ?", nosep)) {
            JOptionPane.showMessageDialog(null, "Status grouping INACBG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            int selected = tbDiagnosaPasien.getSelectedRow();
            if (tabModeDiagnosaPasien.getValueAt(selected, 5).toString().equals("1")) {
                JOptionPane.showMessageDialog(null, "Diagnosa tidak valid..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                tabModeDiagnosaPasien.moveRow(selected, selected, 0);
                tabModeDiagnosaPasien.fireTableDataChanged();

                try {
                    boolean sukses = true;
                    Sequel.AutoComitFalse();

                    for (int i = 0; i < tabModeDiagnosaPasien.getRowCount(); i++) {
                        if (!Sequel.mengupdatetfSmc("inacbg_diagnosa_pasien_smc", "urut = ?", "no_sep = ? and kode_icd10 = ? and urut = ? and keterangan = ''",
                            String.valueOf(i + 1), nosep, tabModeDiagnosaPasien.getValueAt(i, 1).toString(),
                            tabModeDiagnosaPasien.getValueAt(i, 4).toString()
                        )) {
                            sukses = false;
                        }
                    }

                    if (sukses) {
                        Sequel.Commit();
                    } else {
                        Sequel.RollBack();
                    }

                    Sequel.AutoComitTrue();

                    if (!sukses) {
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengangkat status diagnosa menjadi utama..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }

                pilihTab();

                fireUrutanDiagnosaBerubahEvent();
            }
        }
    }//GEN-LAST:event_ppJadikanDiagnosaUtamaActionPerformed

    private void tbDiagnosaPasienMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDiagnosaPasienMousePressed
        if (tabModeDiagnosaPasien.getRowCount() > 0) {
            int r = tbDiagnosaPasien.rowAtPoint(evt.getPoint());
            int c = tbDiagnosaPasien.columnAtPoint(evt.getPoint());
            if (!tbDiagnosaPasien.isRowSelected(r)) {
                tbDiagnosaPasien.changeSelection(r, c, false, false);
            }
        }
    }//GEN-LAST:event_tbDiagnosaPasienMousePressed

    private void tbProsedurPasienMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProsedurPasienMousePressed
        if (tabModeProsedurPasien.getRowCount() > 0) {
            int r = tbProsedurPasien.rowAtPoint(evt.getPoint());
            int c = tbProsedurPasien.columnAtPoint(evt.getPoint());
            if (!tbProsedurPasien.isRowSelected(r)) {
                tbProsedurPasien.changeSelection(r, c, false, false);
            }
        }
    }//GEN-LAST:event_tbProsedurPasienMousePressed

    private void ppJadikanProsedurUtamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppJadikanProsedurUtamaActionPerformed
        if (tbProsedurPasien.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih prosedur yang mau diangkat jadi prosedur utama..!!");
        } else if (Sequel.cariExistsSmc("select * from inacbg_klaim_final_smc i where i.no_sep = ?", nosep)) {
            JOptionPane.showMessageDialog(null, "Status grouping INACBG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            int selected = tbProsedurPasien.getSelectedRow();
            if (tabModeProsedurPasien.getValueAt(selected, 5).toString().equals("1")) {
                JOptionPane.showMessageDialog(null, "Prosedur tidak valid..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                tabModeProsedurPasien.moveRow(selected, selected, 0);
                tabModeProsedurPasien.fireTableDataChanged();

                try {
                    boolean sukses = true;
                    Sequel.AutoComitFalse();

                    for (int i = 0; i < tabModeProsedurPasien.getRowCount(); i++) {
                        if (!Sequel.mengupdatetfSmc("inacbg_prosedur_pasien_smc", "urut = ?", "no_sep = ? and kode_icd9 = ? and urut = ? and keterangan = ''",
                            String.valueOf(i + 1), nosep, tabModeProsedurPasien.getValueAt(i, 1).toString(),
                            tabModeProsedurPasien.getValueAt(i, 4).toString()
                        )) {
                            sukses = false;
                        }
                    }

                    if (sukses) {
                        Sequel.Commit();
                    } else {
                        Sequel.RollBack();
                    }

                    Sequel.AutoComitTrue();

                    if (!sukses) {
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengangkat status prosedur menjadi utama..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }

                pilihTab();

                fireUrutanProsedurBerubahEvent();
            }
        }
    }//GEN-LAST:event_ppJadikanProsedurUtamaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private id.yaski.simrskhanza.widget.Button BtnCariPenyakit;
    private id.yaski.simrskhanza.widget.Button BtnCariProsedur;
    private id.yaski.simrskhanza.widget.TextBox Diagnosa;
    private id.yaski.simrskhanza.widget.PanelBiasa FormData;
    private id.yaski.simrskhanza.widget.TextBox Prosedur;
    private id.yaski.simrskhanza.widget.ScrollPane Scroll;
    private id.yaski.simrskhanza.widget.ScrollPane Scroll1;
    private id.yaski.simrskhanza.widget.ScrollPane Scroll2;
    private id.yaski.simrskhanza.widget.ScrollPane Scroll3;
    private id.yaski.simrskhanza.widget.ScrollPane ScrollInput;
    private javax.swing.JTabbedPane TabRawat;
    private id.yaski.simrskhanza.widget.InternalFrame internalFrame2;
    private id.yaski.simrskhanza.widget.InternalFrame internalFrame3;
    private id.yaski.simrskhanza.widget.Label jLabel13;
    private id.yaski.simrskhanza.widget.Label jLabel15;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JMenuItem ppJadikanDiagnosaUtama;
    private javax.swing.JMenuItem ppJadikanProsedurUtama;
    private id.yaski.simrskhanza.widget.Table tbDiagnosaPasien;
    private id.yaski.simrskhanza.widget.Table tbICD10;
    private id.yaski.simrskhanza.widget.Table tbICD9CM;
    private id.yaski.simrskhanza.widget.Table tbProsedurPasien;
    // End of variables declaration//GEN-END:variables

    public void addDiagnosaBerubahListener(DiagnosaINACBGListener listener) {
        diagnosaListeners.add(listener);
    }

    public void addProsedurBerubahListener(ProsedurINACBGListener listener) {
        prosedurListeners.add(listener);
    }

    public void removeDiagnosaBerubahListener(DiagnosaINACBGListener listener) {
        diagnosaListeners.remove(listener);
    }

    public void removeProsedurBerubahListener(ProsedurINACBGListener listener) {
        prosedurListeners.remove(listener);
    }

    private void fireUrutanDiagnosaBerubahEvent() {
        diagnosaListeners.forEach(DiagnosaINACBGListener::urutanDiagnosaBerubah);
    }

    private void fireUrutanProsedurBerubahEvent() {
        prosedurListeners.forEach(ProsedurINACBGListener::urutanProsedurBerubah);
    }

    private boolean cekValiditasICD10(int selectedRow) {
        if (tabModeICD10.getValueAt(selectedRow, 3).toString().equals("0")) {
            JOptionPane.showMessageDialog(null, "Kode diagnosa tidak bisa digunakan untuk coding..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean cekValiditasICD9CM(int selectedRow) {
        if (tabModeICD9CM.getValueAt(selectedRow, 4).toString().equals("0")) {
            JOptionPane.showMessageDialog(null, "Kode prosedur tidak bisa digunakan untuk coding..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public void setNextFocusableComponent(JComponent component) {
        this.nextFocusableComponent = component;
        Prosedur.setNextFocusableComponent(this.nextFocusableComponent);
    }

    public void setSEP(String nosep, boolean hapusOtomatis) {
        this.nosep = nosep;
        this.hapusOtomatis = hapusOtomatis;
    }

    public void tampilDiagnosa() {
        Valid.tabelKosong(tabModeDiagnosaPasien);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select dx.*, if (dx.urut = 1, 'Utama', '') as stts, " +
            "if(ifnull(dx.keterangan, '') = '', 0, 1) as is_err from " +
            "inacbg_diagnosa_pasien_smc dx where dx.no_sep = ? order by dx.urut"
        )) {
            ps.setString(1, nosep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeDiagnosaPasien.addRow(new Object[] {
                        false, rs.getString("kode_icd10"), rs.getString("deskripsi"), rs.getString("stts"),
                        rs.getInt("urut"), rs.getString("is_err"), rs.getString("locked")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilProsedur() {
        Valid.tabelKosong(tabModeProsedurPasien);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select p.*, if(p.urut = 1, 'Utama', '') as stts, " +
            "if(ifnull(p.keterangan, '') = '', 0, 1) as is_err from " +
            "inacbg_prosedur_pasien_smc p where p.no_sep = ? order by p.urut"
        )) {
            ps.setString(1, nosep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabModeProsedurPasien.addRow(new Object[] {
                        false, rs.getString("kode_icd9"), rs.getString("deskripsi"), rs.getString("stts"),
                        rs.getInt("urut"), rs.getString("is_err"), rs.getString("locked")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilICD10(boolean pilihPertama) {
        try {
            ArrayList<String> icd = new ArrayList<>();
            ArrayList<Map<String, Object>> rows = new ArrayList<>();

            dx = 1;
            if (!hapusOtomatis) {
                dx = Sequel.cariIntegerSmc("select max(inacbg_diagnosa_pasien_smc.urut) from inacbg_diagnosa_pasien_smc where inacbg_diagnosa_pasien_smc.no_sep = ?", nosep) + 1;
            }

            if (!Diagnosa.getText().isBlank()) {
                for (int i = 0; i < tabModeICD10.getRowCount(); i++) {
                    if ((Boolean) tabModeICD10.getValueAt(i, 0)) {
                        icd.add((String) tabModeICD10.getValueAt(i, 1));
                        Map<String, Object> row = new HashMap<>();
                        row.put("p", true);
                        row.put("code1", (String) tabModeICD10.getValueAt(i, 1));
                        row.put("deskripsi", (String) tabModeICD10.getValueAt(i, 2));
                        row.put("validcode", (String) tabModeICD10.getValueAt(i, 3));
                        row.put("urut", dx++);
                        rows.add(row);
                    }
                }
            } else {
                pilihPertama = false;
            }

            Valid.tabelKosong(tabModeICD10);

            rows.forEach(r -> tabModeICD10.addRow(new Object[] {
                r.get("p"), r.get("code1"), r.get("deskripsi"),
                r.get("validcode"), r.get("urut")
            }));

            StringBuilder sb = new StringBuilder("select * from inacbg_referensi_icd10_smc ");

            String query = "(code1 like ? or code2 like ? or deskripsi like ?) ";

            List<String> lists = null;

            if (!Diagnosa.getText().isBlank()) {
                lists = Arrays.asList(StringUtils.split(Diagnosa.getText().trim()));
                int idx = 0, size = lists.size();
                sb.append("where ");
                for (String q : lists) {
                    sb.append(query);
                    if (++idx < size) {
                        sb.append("and ");
                    }
                }
            }

            try (PreparedStatement ps = koneksi.prepareStatement(sb.append("order by code1 limit 100").toString())) {
                int p = 0;
                if (lists != null) {
                    for (String q : lists) {
                        ps.setString(++p, "%" + q + "%");
                        ps.setString(++p, "%" + q + "%");
                        ps.setString(++p, "%" + q + "%");
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        SwingUtilities.invokeLater(() -> {
                            if (!Diagnosa.getText().isBlank()) {
                                Diagnosa.requestFocusInWindow();
                                Diagnosa.selectAll();
                            }
                        });
                        do {
                            if (icd.contains(rs.getString("code1"))) {
                                continue;
                            }

                            tabModeICD10.addRow(new Object[] {
                                pilihPertama, rs.getString("code1"), rs.getString("deskripsi"), rs.getString("validcode"), dx
                            });

                            if (pilihPertama) {
                                tabModeICD10.setValueAt(cekValiditasICD10(tabModeICD10.getRowCount() - 1), tabModeICD10.getRowCount() - 1, 0);
                                pilihPertama = false;
                            }
                        } while (rs.next());
                    } else {
                        if (!Diagnosa.getText().isBlank()) {
                            JOptionPane.showMessageDialog(null, "Hasil pencarian kosong..!!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilICD9CM(boolean pilihPertama) {
        try {
            ArrayList<String> icd = new ArrayList<>();
            ArrayList<Map<String, Object>> rows = new ArrayList<>();

            px = 1;
            if (!hapusOtomatis) {
                px = Sequel.cariIntegerSmc("select max(inacbg_prosedur_pasien_smc.urut) from inacbg_prosedur_pasien_smc where inacbg_prosedur_pasien_smc.no_sep = ?", nosep) + 1;
            }

            if (pilihPertama && !Prosedur.getText().isBlank()) {
                for (int i = 0; i < tabModeICD9CM.getRowCount(); i++) {
                    if ((Boolean) tabModeICD9CM.getValueAt(i, 0)) {
                        icd.add((String) tabModeICD9CM.getValueAt(i, 1));
                        Map<String, Object> row = new HashMap<>();
                        row.put("p", true);
                        row.put("code1", tabModeICD9CM.getValueAt(i, 1));
                        row.put("deskripsi", tabModeICD9CM.getValueAt(i, 2));
                        row.put("validcode", tabModeICD9CM.getValueAt(i, 3));
                        row.put("urut", px++);
                        rows.add(row);
                    }
                }
            } else {
                pilihPertama = false;
            }

            Valid.tabelKosong(tabModeICD9CM);

            rows.forEach(r -> tabModeICD9CM.addRow(new Object[] {
                r.get("p"), r.get("code1"), r.get("deskripsi"),
                r.get("validcode"), r.get("urut")
            }));

            StringBuilder sb = new StringBuilder("select * from inacbg_referensi_icd9cm_smc ");

            String query = "(code1 like ? or code2 like ? or deskripsi like ?) ";

            List<String> lists = null;

            if (!Prosedur.getText().isBlank()) {
                lists = Arrays.asList(StringUtils.split(Prosedur.getText().trim()));
                int idx = 0, size = lists.size();
                sb.append("where ");
                for (String q : lists) {
                    sb.append(query);
                    if (++idx < size) {
                        sb.append("and ");
                    }
                }
            }

            try (PreparedStatement ps = koneksi.prepareStatement(sb.append("order by code1 limit 100").toString())) {
                int p = 0;
                if (lists != null) {
                    for (String q : lists) {
                        ps.setString(++p, "%" + q + "%");
                        ps.setString(++p, "%" + q + "%");
                        ps.setString(++p, "%" + q + "%");
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        SwingUtilities.invokeLater(() -> {
                            if (!Prosedur.getText().isBlank()) {
                                Prosedur.requestFocusInWindow();
                                Prosedur.selectAll();
                            }
                        });
                        do {
                            if (icd.contains(rs.getString("code1"))) {
                                continue;
                            }

                            tabModeICD9CM.addRow(new Object[] {
                                pilihPertama, rs.getString("code1"), rs.getString("deskripsi"), rs.getString("validcode"), px
                            });

                            if (pilihPertama) {
                                tabModeICD9CM.setValueAt(cekValiditasICD9CM(tabModeICD9CM.getRowCount() - 1), tabModeICD9CM.getRowCount() - 1, 0);
                                pilihPertama = false;
                            }
                        } while (rs.next());
                    } else {
                        if (!Prosedur.getText().isBlank()) {
                            JOptionPane.showMessageDialog(null, "Hasil pencarian kosong..!!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilICD() {
        Diagnosa.setText("");
        Prosedur.setText("");
        tbICD10.clearSelection();
        tbICD9CM.clearSelection();
        tbDiagnosaPasien.clearSelection();
        tbProsedurPasien.clearSelection();
        tampilICD10(false);
        tampilDiagnosa();
        tampilICD9CM(false);
        tampilProsedur();
        Diagnosa.requestFocus();
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
            tampilICD();
        } else if (TabRawat.getSelectedIndex() == 1) {
            tampilDiagnosa();
        } else if (TabRawat.getSelectedIndex() == 2) {
            tampilProsedur();
        }
    }

    public void simpan() {
        if (TabRawat.getSelectedIndex() > 0) {
            return;
        }

        if (Sequel.cariExistsSmc("select * from inacbg_klaim_final_smc where no_sep = ?", nosep)) {
            JOptionPane.showMessageDialog(null, "Klaim INACBG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tabModeICD10.getRowCount() == 0 && tabModeICD9CM.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada diagnosa/prosedur dipilih untuk disimpan..!!");
            return;
        }

        boolean updateDiagnosa = false, updateProsedur = false;

        int konfirmasiHapus = -1;

        if (Sequel.cariExistsSmc("select * from inacbg_grouping_stage12 where no_sep = ?", nosep)) {
            konfirmasiHapus = JOptionPane.showConfirmDialog(null, "Mengubah diagnosa/prosedur akan membatalkan status grouping INACBG, lanjutkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        } else {
            konfirmasiHapus = JOptionPane.YES_OPTION;
        }

        if (konfirmasiHapus == JOptionPane.YES_OPTION) {
            Sequel.menghapustfSmc("inacbg_grouping_stage2_smc", "no_sep = ?", nosep);
            Sequel.menghapustfSmc("inacbg_grouping_stage12", "no_sep = ?", nosep);

            if (tabModeICD10.getRowCount() > 0) {
                try {
                    Sequel.AutoComitFalse();
                    boolean sukses = true;

                    if (hapusOtomatis) {
                        for (int i = 0; i < tabModeICD10.getRowCount(); i++) {
                            if ((Boolean) tabModeICD10.getValueAt(i, 0)) {
                                updateDiagnosa = true;
                                break;
                            }
                        }

                        if (updateDiagnosa) {
                            Sequel.menghapusSmc("inacbg_diagnosa_pasien_smc", "no_sep = ? and locked = 0", nosep);
                        }
                    }

                    for (int i = 0; i < tabModeICD10.getRowCount(); i++) {
                        if (!(Boolean) tabModeICD10.getValueAt(i, 0)) {
                            continue;
                        }

                        if (!Sequel.menyimpantfSmc("inacbg_diagnosa_pasien_smc", null, nosep,
                            tabModeICD10.getValueAt(i, 1).toString(), tabModeICD10.getValueAt(i, 2).toString(),
                            String.valueOf((Integer) tabModeICD10.getValueAt(i, 4) + 100), "", "0"
                        )) {
                            sukses = false;
                        }
                    }

                    if (sukses) {
                        sukses = Sequel.executeRawSmc("update inacbg_diagnosa_pasien_smc i join (with t as (select row_number() over (partition by " +
                            "dx.no_sep order by dx.urut) as rn, dx.* from inacbg_diagnosa_pasien_smc dx where dx.no_sep = ?) select * from t) " +
                            "rownum on i.no_sep = rownum.no_sep and i.kode_icd10 = rownum.kode_icd10 set i.urut = rownum.rn", nosep
                        );
                    }

                    if (sukses) {
                        Sequel.Commit();
                    } else {
                        Sequel.RollBack();
                    }

                    Sequel.AutoComitTrue();

                    if (!sukses) {
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan diagnosa..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan diagnosa..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }

            if (tabModeICD9CM.getRowCount() > 0) {
                try {
                    Sequel.AutoComitFalse();
                    boolean sukses = true;

                    if (hapusOtomatis) {
                        for (int i = 0; i < tabModeICD9CM.getRowCount(); i++) {
                            if ((Boolean) tabModeICD9CM.getValueAt(i, 0)) {
                                updateProsedur = true;
                                break;
                            }
                        }

                        if (updateProsedur) {
                            Sequel.menghapusSmc("inacbg_prosedur_pasien_smc", "no_sep = ? and locked = 0", nosep);
                        }
                    }

                    for (int i = 0; i < tabModeICD9CM.getRowCount(); i++) {
                        if (!(Boolean) tabModeICD9CM.getValueAt(i, 0)) {
                            continue;
                        }

                        if (!Sequel.menyimpantfSmc("inacbg_prosedur_pasien_smc", null, nosep,
                            tabModeICD9CM.getValueAt(i, 1).toString(), tabModeICD9CM.getValueAt(i, 2).toString(),
                            String.valueOf((Integer) tabModeICD9CM.getValueAt(i, 4) + 100), "", "0"
                        )) {
                            sukses = false;
                        }
                    }

                    if (sukses) {
                        sukses = Sequel.executeRawSmc("update inacbg_prosedur_pasien_smc i join (with t as (select row_number() over (partition by " +
                            "p.no_sep order by p.urut) as rn, p.* from inacbg_prosedur_pasien_smc p where p.no_sep = ?) select * from t) " +
                            "rownum on i.no_sep = rownum.no_sep and i.kode_icd9 = rownum.kode_icd9 set i.urut = rownum.rn", nosep
                        );
                    }

                    if (sukses) {
                        Sequel.Commit();
                    } else {
                        Sequel.RollBack();
                    }

                    Sequel.AutoComitTrue();

                    if (!sukses) {
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan diagnosa..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan diagnosa..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }

            pilihTab(0);
        }
    }

    public void hapus() {
        if (Sequel.cariExistsSmc("select * from inacbg_klaim_final_smc where no_sep = ?", nosep)) {
            JOptionPane.showMessageDialog(null, "Klaim INACBG sudah final..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasiHapus = -1;

        if (Sequel.cariExistsSmc("select * from inacbg_grouping_stage12 where no_sep = ?", nosep)) {
            konfirmasiHapus = JOptionPane.showConfirmDialog(null, "Mengubah diagnosa/prosedur akan membatalkan status grouping INACBG, lanjutkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        } else {
            konfirmasiHapus = JOptionPane.YES_OPTION;
        }

        if (konfirmasiHapus == JOptionPane.YES_OPTION) {
            Sequel.menghapustfSmc("inacbg_grouping_stage2_smc", "no_sep = ?", nosep);
            Sequel.menghapustfSmc("inacbg_grouping_stage12", "no_sep = ?", nosep);
            switch (TabRawat.getSelectedIndex()) {
                case 0:
                    return;
                case 1:
                    if (tabModeDiagnosaPasien.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
                    } else {
                        try {
                            Sequel.AutoComitFalse();
                            boolean sukses = true;

                            for (int i = tabModeDiagnosaPasien.getRowCount() - 1; i >= 0; i--) {
                                if (!(Boolean) tabModeDiagnosaPasien.getValueAt(i, 0)) {
                                    continue;
                                }

                                if (tabModeDiagnosaPasien.getValueAt(i, 6).toString().equals("1")) {
                                    tabModeDiagnosaPasien.setValueAt(false, i, 0);
                                    JOptionPane.showMessageDialog(null, "Diagnosa \"" + tabModeDiagnosaPasien.getValueAt(i, 1).toString() + " " + tabModeDiagnosaPasien.getValueAt(i, 2).toString() + "\" sudah valid..!!");
                                    continue;
                                }

                                if (Sequel.menghapustfSmc("inacbg_diagnosa_pasien_smc", "no_sep = ? and kode_icd10 = ? and locked = 0", nosep, (String) tabModeDiagnosaPasien.getValueAt(i, 1))) {
                                    tabModeDiagnosaPasien.removeRow(i);
                                }
                            }

                            if (sukses) {
                                Sequel.executeRawSmc("update inacbg_diagnosa_pasien_smc i join (with t as (select row_number() over (partition by " +
                                    "dx.no_sep order by dx.urut) as rn, dx.* from inacbg_diagnosa_pasien_smc dx where dx.no_sep = ?) select * from t) " +
                                    "rownum on i.no_sep = rownum.no_sep and i.kode_icd10 = rownum.kode_icd10 set i.urut = rownum.rn", nosep
                                );
                            }

                            if (sukses) {
                                Sequel.Commit();
                            } else {
                                Sequel.RollBack();

                            }

                            Sequel.AutoComitTrue();

                            if (!sukses) {
                                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus data diagnosa INACBG..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus data diagnosa INACBG..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                case 2:
                    if (tabModeProsedurPasien.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
                    } else {
                        try {
                            boolean sukses = true;
                            Sequel.AutoComitFalse();
                            for (int i = tabModeProsedurPasien.getRowCount() - 1; i >= 0; i--) {
                                if (!(Boolean) tabModeProsedurPasien.getValueAt(i, 0)) {
                                    continue;
                                }

                                if (tabModeProsedurPasien.getValueAt(i, 6).toString().equals("1")) {
                                    tabModeProsedurPasien.setValueAt(false, i, 0);
                                    JOptionPane.showMessageDialog(null, "Prosedur \"" + tabModeProsedurPasien.getValueAt(i, 1).toString() + " " + tabModeProsedurPasien.getValueAt(i, 2).toString() + "\" sudah valid..!!");
                                    continue;
                                }

                                if (Sequel.menghapustfSmc("inacbg_prosedur_pasien_smc", "no_sep = ? and kode_icd9 = ? and locked = 0", nosep, (String) tabModeProsedurPasien.getValueAt(i, 1))) {
                                    tabModeProsedurPasien.removeRow(i);
                                }
                            }

                            if (sukses) {
                                Sequel.executeRawSmc("update inacbg_prosedur_pasien_smc i join (with t as (select row_number() over (partition by " +
                                    "p.no_sep order by p.urut) as rn, p.* from inacbg_prosedur_pasien_smc p where p.no_sep = ?) select * from t) " +
                                    "rownum on i.no_sep = rownum.no_sep and i.kode_icd9 = rownum.kode_icd9 set i.urut = rownum.rn", nosep
                                );
                            }

                            if (sukses) {
                                Sequel.Commit();
                            } else {
                                Sequel.RollBack();
                            }

                            Sequel.AutoComitTrue();

                            if (!sukses) {
                                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus data prosedur INACBG..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus data prosedur INACBG..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                default:
                    return;
            }
        }

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

    @FunctionalInterface
    public static interface DiagnosaINACBGListener {
        void urutanDiagnosaBerubah();
    }

    @FunctionalInterface
    public static interface ProsedurINACBGListener {
        void urutanProsedurBerubah();
    }
}
