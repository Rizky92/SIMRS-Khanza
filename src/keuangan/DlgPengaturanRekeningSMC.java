package keuangan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author dosen
 */
public class DlgPengaturanRekeningSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeRalan, tabModeRanap;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Map<String, String> setAkun = new HashMap<>(),
                                      setAkun2 = new HashMap<>(),
                                      setAkunRalan = new HashMap<>(),
                                      setAkunRanap = new HashMap<>(),
                                      setAkunRanap2 = new HashMap<>();
    private final Map<String, Rekening> akunRekening = new HashMap<>();
    private int barisdicopy = -1;
    private String copyakun = "";
    private DlgRekeningTahun rekening = new DlgRekeningTahun(null, false);

    public DlgPengaturanRekeningSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new String[] {"Letak Akun Rekening", "Kode Akun", "Nama Akun", "Tipe", "Balance", "table", "kolom"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbPengaturan.setModel(tabMode);
        tbPengaturan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPengaturan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbPengaturan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(600);
            } else if (i == 1) {
                column.setPreferredWidth(90);
            } else if (i == 2) {
                column.setPreferredWidth(380);
            } else if (i == 3) {
                column.setPreferredWidth(40);
            } else if (i == 4) {
                column.setPreferredWidth(45);
            } else if (i == 5 || i == 6) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }

        tbPengaturan.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRalan = new DefaultTableModel(null, new String[] {
            "Kode Tindakan", "Nama Tnd/Prw/Tagihan", "Kategori", "Jenis Bayar", "Unit/Poli",
            "Kode Akun", "Nama Akun Pendapatan Tindakan Ralan", "Kode Akun", "Nama Akun Beban Jasa Dokter",
            "Kode Akun", "Nama Akun Utang Jasa Dokter", "Kode Akun", "Nama Akun Beban Jasa Paramedis",
            "Kode Akun", "Nama Akun Utang Jasa Paramedis", "Kode Akun", "Nama Akun Beban KSO",
            "Kode Akun", "Nama Akun Utang KSO", "Kode Akun", "Nama Akun HPP Persediaan Ralan",
            "Kode Akun", "Nama Akun Persediaan BHP Ralan", "Kode Akun", "Nama Akun Beban Jasa Sarana",
            "Kode Akun", "Nama Akun Utang Jasa Sarana", "Kode Akun", "Nama Akun Utang Beban Jasa Menejemen",
            "Kode Akun", "Nama Akun Utang Jasa Menejemen"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbPengaturanRalan.setModel(tabModeRalan);
        tbPengaturanRalan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPengaturanRalan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 31; i++) {
            TableColumn column = tbPengaturanRalan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(80);
            } else if (i == 1) {
                column.setPreferredWidth(330);
            } else if (i == 2) {
                column.setPreferredWidth(110);
            } else if (i == 3) {
                column.setPreferredWidth(130);
            } else if (i == 4) {
                column.setPreferredWidth(110);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(300);
            } else if (i == 7) {
                column.setPreferredWidth(80);
            } else if (i == 8) {
                column.setPreferredWidth(300);
            } else if (i == 9) {
                column.setPreferredWidth(80);
            } else if (i == 10) {
                column.setPreferredWidth(300);
            } else if (i == 11) {
                column.setPreferredWidth(80);
            } else if (i == 12) {
                column.setPreferredWidth(300);
            } else if (i == 13) {
                column.setPreferredWidth(80);
            } else if (i == 14) {
                column.setPreferredWidth(300);
            } else if (i == 15) {
                column.setPreferredWidth(80);
            } else if (i == 16) {
                column.setPreferredWidth(300);
            } else if (i == 17) {
                column.setPreferredWidth(80);
            } else if (i == 18) {
                column.setPreferredWidth(300);
            } else if (i == 19) {
                column.setPreferredWidth(80);
            } else if (i == 20) {
                column.setPreferredWidth(300);
            } else if (i == 21) {
                column.setPreferredWidth(80);
            } else if (i == 22) {
                column.setPreferredWidth(300);
            } else if (i == 23) {
                column.setPreferredWidth(80);
            } else if (i == 24) {
                column.setPreferredWidth(300);
            } else if (i == 25) {
                column.setPreferredWidth(80);
            } else if (i == 26) {
                column.setPreferredWidth(300);
            } else if (i == 27) {
                column.setPreferredWidth(80);
            } else if (i == 28) {
                column.setPreferredWidth(300);
            } else if (i == 29) {
                column.setPreferredWidth(80);
            } else if (i == 30) {
                column.setPreferredWidth(300);
            }
        }

        tbPengaturanRalan.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRanap = new DefaultTableModel(null, new String[] {
            "Kode Tindakan", "Nama Tnd/Prw/Tagihan", "Kategori", "Jenis Bayar", "Ruang", "Kelas",
            "Kode Akun", "Nama Akun Pendapatan Tindakan Ranap", "Kode Akun", "Nama Akun Beban Jasa Dokter",
            "Kode Akun", "Nama Akun Utang Jasa Dokter", "Kode Akun", "Nama Akun Beban Jasa Paramedis",
            "Kode Akun", "Nama Akun Utang Jasa Paramedis", "Kode Akun", "Nama Akun Beban KSO",
            "Kode Akun", "Nama Akun Utang KSO", "Kode Akun", "Nama Akun HPP Persediaan Ranap",
            "Kode Akun", "Nama Akun Persediaan BHP Ranap", "Kode Akun", "Nama Akun Beban Jasa Sarana",
            "Kode Akun", "Nama Akun Utang Jasa Sarana", "Kode Akun", "Nama Akun Utang Beban Jasa Menejemen",
            "Kode Akun", "Nama Akun Utang Jasa Menejemen"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbPengaturanRanap.setModel(tabModeRanap);
        tbPengaturanRanap.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPengaturanRanap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 32; i++) {
            TableColumn column = tbPengaturanRanap.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(80);
            } else if (i == 1) {
                column.setPreferredWidth(330);
            } else if (i == 2) {
                column.setPreferredWidth(110);
            } else if (i == 3) {
                column.setPreferredWidth(130);
            } else if (i == 4) {
                column.setPreferredWidth(110);
            } else if (i == 5) {
                column.setPreferredWidth(50);
            } else if (i == 6) {
                column.setPreferredWidth(80);
            } else if (i == 7) {
                column.setPreferredWidth(300);
            } else if (i == 8) {
                column.setPreferredWidth(80);
            } else if (i == 9) {
                column.setPreferredWidth(300);
            } else if (i == 10) {
                column.setPreferredWidth(80);
            } else if (i == 11) {
                column.setPreferredWidth(300);
            } else if (i == 12) {
                column.setPreferredWidth(80);
            } else if (i == 13) {
                column.setPreferredWidth(300);
            } else if (i == 14) {
                column.setPreferredWidth(80);
            } else if (i == 15) {
                column.setPreferredWidth(300);
            } else if (i == 16) {
                column.setPreferredWidth(80);
            } else if (i == 17) {
                column.setPreferredWidth(300);
            } else if (i == 18) {
                column.setPreferredWidth(80);
            } else if (i == 19) {
                column.setPreferredWidth(300);
            } else if (i == 20) {
                column.setPreferredWidth(80);
            } else if (i == 21) {
                column.setPreferredWidth(300);
            } else if (i == 22) {
                column.setPreferredWidth(80);
            } else if (i == 23) {
                column.setPreferredWidth(300);
            } else if (i == 24) {
                column.setPreferredWidth(80);
            } else if (i == 25) {
                column.setPreferredWidth(300);
            } else if (i == 26) {
                column.setPreferredWidth(80);
            } else if (i == 27) {
                column.setPreferredWidth(300);
            } else if (i == 28) {
                column.setPreferredWidth(80);
            } else if (i == 29) {
                column.setPreferredWidth(300);
            } else if (i == 30) {
                column.setPreferredWidth(80);
            } else if (i == 31) {
                column.setPreferredWidth(300);
            }
        }

        tbPengaturanRanap.setDefaultRenderer(Object.class, new WarnaTable());

        rekening.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                if (akses.getform().equals("DlgPengaturanRekening")) {
                    if (tbPengaturan.getSelectedRow() >= 0 && rekening.getTabel().getSelectedRow() >= 0) {
                        setPengaturan(tbPengaturan.getSelectedRow(),
                            rekening.getSelectedRow(1).toString(),
                            rekening.getSelectedRow(2).toString(),
                            rekening.getSelectedRow(3).toString(),
                            rekening.getSelectedRow(4).toString()
                        );
                    }
                    tbPengaturan.requestFocus();
                } else if (akses.getform().equals("DlgPengaturanRekeningRalan")) {
                    if (rekening.getTabel().getSelectedRow() != -1) {
                        if (tbPengaturanRalan.getSelectedColumn() == 5) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 5);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 6);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 7) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 7);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 8);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 9) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 9);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 10);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 11) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 11);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 12);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 13) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 13);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 14);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 15) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 15);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 16);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 17) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 17);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 18);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 19) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 19);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 20);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 21) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 21);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 22);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 23) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 23);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 24);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 25) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 25);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 26);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 27) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 27);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 28);
                        } else if (tbPengaturanRalan.getSelectedColumn() == 29) {
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRalan.getSelectedRow(), 29);
                            tabModeRalan.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRalan.getSelectedRow(), 30);
                        }
                    }
                    tbPengaturanRalan.requestFocus();
                } else if (akses.getform().equals("DlgPengaturanRekeningRanap")) {
                    if (rekening.getTabel().getSelectedRow() != -1) {
                        if (tbPengaturanRanap.getSelectedColumn() == 6) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 6);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 7);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 8) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 8);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 9);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 10) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 10);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 11);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 12) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 12);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 13);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 14) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 14);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 15);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 16) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 16);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 17);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 18) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 18);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 19);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 20) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 20);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 21);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 22) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 22);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 23);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 24) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 24);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 25);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 26) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 26);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 27);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 28) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 28);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 29);
                        } else if (tbPengaturanRanap.getSelectedColumn() == 30) {
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 1).toString(), tbPengaturanRanap.getSelectedRow(), 30);
                            tabModeRanap.setValueAt(rekening.getTabel().getValueAt(rekening.getTabel().getSelectedRow(), 2).toString(), tbPengaturanRanap.getSelectedRow(), 31);
                        }
                    }
                    tbPengaturanRanap.requestFocus();
                }
            }
        });

        rekening.getTabel().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (akses.getform().equals("DlgPengaturanRekening")) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        rekening.setVisible(false);
                    }
                } else if (akses.getform().equals("DlgPengaturanRekeningRalan")) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        rekening.setVisible(false);
                    }
                } else if (akses.getform().equals("DlgPengaturanRekeningRanap")) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        rekening.setVisible(false);
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCopyRekening = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        Scroll = new widget.ScrollPane();
        tbPengaturan = new widget.Table();
        Scroll1 = new widget.ScrollPane();
        tbPengaturanRalan = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbPengaturanRanap = new widget.Table();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCopyRekening.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCopyRekening.setForeground(new java.awt.Color(50, 50, 50));
        MnCopyRekening.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCopyRekening.setText("Copy Rekening");
        MnCopyRekening.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCopyRekening.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCopyRekening.setName("MnCopyRekening"); // NOI18N
        MnCopyRekening.setPreferredSize(new java.awt.Dimension(170, 26));
        MnCopyRekening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCopyRekeningActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCopyRekening);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengaturan Rekening ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 56));
        panelGlass8.setLayout(null);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('U');
        BtnSimpan.setText("Update");
        BtnSimpan.setToolTipText("Alt+U");
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
        BtnKeluar.setBounds(109, 10, 100, 30);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(255, 255, 253));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbPengaturan.setToolTipText("Semua akun harus terisi");
        tbPengaturan.setName("tbPengaturan"); // NOI18N
        tbPengaturan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPengaturanKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbPengaturan);

        TabRawat.addTab("Pengaturan Umum", Scroll);

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbPengaturanRalan.setToolTipText("Semua akun harus terisi");
        tbPengaturanRalan.setComponentPopupMenu(jPopupMenu1);
        tbPengaturanRalan.setName("tbPengaturanRalan"); // NOI18N
        tbPengaturanRalan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPengaturanRalanMouseClicked(evt);
            }
        });
        tbPengaturanRalan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPengaturanRalanKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbPengaturanRalan);

        TabRawat.addTab("Tarif Ralan", Scroll1);

        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbPengaturanRanap.setToolTipText("Semua akun harus terisi");
        tbPengaturanRanap.setComponentPopupMenu(jPopupMenu1);
        tbPengaturanRanap.setName("tbPengaturanRanap"); // NOI18N
        tbPengaturanRanap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPengaturanRanapKeyPressed(evt);
            }
        });
        Scroll2.setViewportView(tbPengaturanRanap);

        TabRawat.addTab("Tarif Ranap", Scroll2);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TabRawat.getSelectedIndex() == 0) {
            try {
                Sequel.AutoComitFalse();
                StringJoiner sjk = new StringJoiner(", ");
                final ArrayList<String> sjv = new ArrayList<>();
                boolean sukses = true;

                for (Map.Entry<String, String> akun : setAkunRalan.entrySet()) {
                    if (akun.getValue() == null || akun.getValue().isBlank()) {
                        sukses = false;
                    }
                    sjk.add(akun.getKey());
                    sjv.add(akun.getValue());
                }
                System.out.println(setAkunRalan.toString());
                if (sukses) {
                    Sequel.menghapustfSmc("set_akun_ralan");
                    sukses = Sequel.menyimpantfSmc("set_akun_ralan", sjk.toString(), sjv.toArray(String[]::new));
                }

                if (sukses) {
                    sjk = new StringJoiner(", ");
                    sjv.clear();
                    for (Map.Entry<String, String> akun : setAkunRanap.entrySet()) {
                        if (akun.getValue() == null || akun.getValue().isBlank()) {
                            sukses = false;
                        }
                        sjk.add(akun.getKey());
                        sjv.add(akun.getValue());
                    }
                    System.out.println(setAkunRanap.toString());
                    if (sukses) {
                        Sequel.menghapustfSmc("set_akun_ranap");
                        sukses = Sequel.menyimpantfSmc("set_akun_ranap", sjk.toString(), sjv.toArray(String[]::new));
                    }
                }

                if (sukses) {
                    sjk = new StringJoiner(", ");
                    sjv.clear();
                    for (Map.Entry<String, String> akun : setAkunRanap2.entrySet()) {
                        if (akun.getValue() == null || akun.getValue().isBlank()) {
                            sukses = false;
                        }
                        sjk.add(akun.getKey());
                        sjv.add(akun.getValue());
                    }
                    System.out.println(setAkunRanap2.toString());
                    if (sukses) {
                        Sequel.menghapustfSmc("set_akun_ranap2");
                        sukses = Sequel.menyimpantfSmc("set_akun_ranap2", sjk.toString(), sjv.toArray(String[]::new));
                    }
                }

                if (sukses) {
                    sjk = new StringJoiner(", ");
                    sjv.clear();
                    for (Map.Entry<String, String> akun : setAkun.entrySet()) {
                        if (akun.getValue() == null || akun.getValue().isBlank()) {
                            sukses = false;
                        }
                        sjk.add(akun.getKey());
                        sjv.add(akun.getValue());
                    }
                    System.out.println(setAkun.toString());
                    if (sukses) {
                        Sequel.menghapustfSmc("set_akun");
                        sukses = Sequel.menyimpantfSmc("set_akun", sjk.toString(), sjv.toArray(String[]::new));
                    }
                }

                if (sukses) {
                    sjk = new StringJoiner(", ");
                    sjv.clear();
                    for (Map.Entry<String, String> akun : setAkun2.entrySet()) {
                        if (akun.getValue() == null || akun.getValue().isBlank()) {
                            sukses = false;
                        }
                        sjk.add(akun.getKey());
                        sjv.add(akun.getValue());
                    }
                    System.out.println(setAkun2.toString());
                    if (sukses) {
                        Sequel.menghapustfSmc("set_akun2");
                        sukses = Sequel.menyimpantfSmc("set_akun2", sjk.toString(), sjv.toArray(String[]::new));
                    }
                }

                if (sukses) {
                    Sequel.Commit();
                } else {
                    Sequel.RollBack();
                }

                Sequel.AutoComitTrue();

                if (!sukses) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengupdate pengaturan akun,\nPastikan semua pengaturan telah terisi..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }

        /*
        if (TabRawat.getSelectedIndex() == 1) {
            for (i = 0; i < tbPengaturanRalan.getRowCount(); i++) {
                if ((!tbPengaturanRalan.getValueAt(i, 5).equals("")) && (!tbPengaturanRalan.getValueAt(i, 6).equals("")) && (!tbPengaturanRalan.getValueAt(i, 7).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 8).equals("")) && (!tbPengaturanRalan.getValueAt(i, 9).equals("")) && (!tbPengaturanRalan.getValueAt(i, 10).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 11).equals("")) && (!tbPengaturanRalan.getValueAt(i, 12).equals("")) && (!tbPengaturanRalan.getValueAt(i, 13).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 14).equals("")) && (!tbPengaturanRalan.getValueAt(i, 15).equals("")) && (!tbPengaturanRalan.getValueAt(i, 16).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 17).equals("")) && (!tbPengaturanRalan.getValueAt(i, 18).equals("")) && (!tbPengaturanRalan.getValueAt(i, 19).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 20).equals("")) && (!tbPengaturanRalan.getValueAt(i, 21).equals("")) && (!tbPengaturanRalan.getValueAt(i, 22).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 23).equals("")) && (!tbPengaturanRalan.getValueAt(i, 24).equals("")) && (!tbPengaturanRalan.getValueAt(i, 25).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 26).equals("")) && (!tbPengaturanRalan.getValueAt(i, 27).equals("")) && (!tbPengaturanRalan.getValueAt(i, 28).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 29).equals("")) && (!tbPengaturanRalan.getValueAt(i, 30).equals(""))) {
                    Sequel.meghapus("matrik_akun_jns_perawatan", "kd_jenis_prw", tbPengaturanRalan.getValueAt(i, 0).toString());
                    Sequel.menyimpan("matrik_akun_jns_perawatan", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", 14, new String[] {
                        tbPengaturanRalan.getValueAt(i, 0).toString(), tbPengaturanRalan.getValueAt(i, 5).toString(),
                        tbPengaturanRalan.getValueAt(i, 7).toString(), tbPengaturanRalan.getValueAt(i, 9).toString(),
                        tbPengaturanRalan.getValueAt(i, 11).toString(), tbPengaturanRalan.getValueAt(i, 13).toString(),
                        tbPengaturanRalan.getValueAt(i, 15).toString(), tbPengaturanRalan.getValueAt(i, 17).toString(),
                        tbPengaturanRalan.getValueAt(i, 19).toString(), tbPengaturanRalan.getValueAt(i, 21).toString(),
                        tbPengaturanRalan.getValueAt(i, 23).toString(), tbPengaturanRalan.getValueAt(i, 25).toString(),
                        tbPengaturanRalan.getValueAt(i, 27).toString(), tbPengaturanRalan.getValueAt(i, 29).toString()
                    });
                }
            }
            // runBackground(() -> tampilralan());
        } else if (TabRawat.getSelectedIndex() == 2) {
            for (i = 0; i < tbPengaturanRanap.getRowCount(); i++) {
                if ((!tbPengaturanRanap.getValueAt(i, 6).equals("")) && (!tbPengaturanRanap.getValueAt(i, 7).equals("")) && (!tbPengaturanRanap.getValueAt(i, 8).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 9).equals("")) && (!tbPengaturanRanap.getValueAt(i, 10).equals("")) && (!tbPengaturanRanap.getValueAt(i, 11).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 12).equals("")) && (!tbPengaturanRanap.getValueAt(i, 13).equals("")) && (!tbPengaturanRanap.getValueAt(i, 14).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 15).equals("")) && (!tbPengaturanRanap.getValueAt(i, 16).equals("")) && (!tbPengaturanRanap.getValueAt(i, 17).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 18).equals("")) && (!tbPengaturanRanap.getValueAt(i, 19).equals("")) && (!tbPengaturanRanap.getValueAt(i, 20).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 21).equals("")) && (!tbPengaturanRanap.getValueAt(i, 22).equals("")) && (!tbPengaturanRanap.getValueAt(i, 23).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 24).equals("")) && (!tbPengaturanRanap.getValueAt(i, 25).equals("")) && (!tbPengaturanRanap.getValueAt(i, 26).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 27).equals("")) && (!tbPengaturanRanap.getValueAt(i, 28).equals("")) && (!tbPengaturanRanap.getValueAt(i, 29).equals("")) &&
                    (!tbPengaturanRanap.getValueAt(i, 30).equals("")) && (!tbPengaturanRanap.getValueAt(i, 31).equals(""))) {
                    Sequel.meghapus("matrik_akun_jns_perawatan_inap", "kd_jenis_prw", tbPengaturanRanap.getValueAt(i, 0).toString());
                    Sequel.menyimpan("matrik_akun_jns_perawatan_inap", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", 14, new String[] {
                        tbPengaturanRanap.getValueAt(i, 0).toString(), tbPengaturanRanap.getValueAt(i, 6).toString(),
                        tbPengaturanRanap.getValueAt(i, 8).toString(), tbPengaturanRanap.getValueAt(i, 10).toString(),
                        tbPengaturanRanap.getValueAt(i, 12).toString(), tbPengaturanRanap.getValueAt(i, 14).toString(),
                        tbPengaturanRanap.getValueAt(i, 16).toString(), tbPengaturanRanap.getValueAt(i, 18).toString(),
                        tbPengaturanRanap.getValueAt(i, 20).toString(), tbPengaturanRanap.getValueAt(i, 22).toString(),
                        tbPengaturanRanap.getValueAt(i, 24).toString(), tbPengaturanRanap.getValueAt(i, 26).toString(),
                        tbPengaturanRanap.getValueAt(i, 28).toString(), tbPengaturanRanap.getValueAt(i, 30).toString()
                    });
                }
            }
            // runBackground(() -> tampilranap());
        }
        */
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void tbPengaturanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPengaturanKeyPressed
        if (tabMode.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                akses.setform("DlgPengaturanRekening");
                rekening.emptTeks();
                rekening.tampil2();
                rekening.isCek();
                rekening.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                rekening.setLocationRelativeTo(internalFrame1);
                rekening.setVisible(true);
            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                setPengaturan(tbPengaturan.getSelectedRow(), "", "", "", "");
            }
        }
    }//GEN-LAST:event_tbPengaturanKeyPressed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        if (TabRawat.getSelectedIndex() == 0) {
            tampil();
        } else if (TabRawat.getSelectedIndex() == 1) {
            // runBackground(() -> tampilralan());
        } else if (TabRawat.getSelectedIndex() == 2) {
            // runBackground(() -> tampilranap());
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void tbPengaturanRalanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPengaturanRalanKeyPressed
        if (tabModeRalan.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                akses.setform("DlgPengaturanRekeningRalan");
                rekening.emptTeks();
                rekening.tampil2();
                rekening.isCek();
                rekening.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                rekening.setLocationRelativeTo(internalFrame1);
                rekening.setVisible(true);
            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 5);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 6);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 7);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 8);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 9);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 10);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 11);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 12);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 13);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 14);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 15);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 16);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 17);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 18);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 19);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 20);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 21);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 22);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 23);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 24);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 25);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 26);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 27);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 28);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 29);
                tabModeRalan.setValueAt("", tbPengaturanRalan.getSelectedRow(), 30);

            }
        }
    }//GEN-LAST:event_tbPengaturanRalanKeyPressed

    private void tbPengaturanRanapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPengaturanRanapKeyPressed
        if (tabModeRanap.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                akses.setform("DlgPengaturanRekeningRanap");
                rekening.emptTeks();
                rekening.tampil2();
                rekening.isCek();
                rekening.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                rekening.setLocationRelativeTo(internalFrame1);
                rekening.setVisible(true);
            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 6);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 7);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 8);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 9);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 10);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 11);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 12);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 13);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 14);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 15);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 16);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 17);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 18);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 19);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 20);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 21);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 22);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 23);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 24);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 25);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 26);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 27);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 28);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 29);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 30);
                tabModeRanap.setValueAt("", tbPengaturanRanap.getSelectedRow(), 31);
            }
        }
    }//GEN-LAST:event_tbPengaturanRanapKeyPressed

    private void MnCopyRekeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCopyRekeningActionPerformed
        if (TabRawat.getSelectedIndex() == 1) {
            if (tbPengaturanRalan.getSelectedRow() != -1) {
                int i = tbPengaturanRalan.getSelectedRow();
                if ((!tbPengaturanRalan.getValueAt(i, 5).equals("")) && (!tbPengaturanRalan.getValueAt(i, 6).equals("")) && (!tbPengaturanRalan.getValueAt(i, 7).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 8).equals("")) && (!tbPengaturanRalan.getValueAt(i, 9).equals("")) && (!tbPengaturanRalan.getValueAt(i, 10).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 11).equals("")) && (!tbPengaturanRalan.getValueAt(i, 12).equals("")) && (!tbPengaturanRalan.getValueAt(i, 13).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 14).equals("")) && (!tbPengaturanRalan.getValueAt(i, 15).equals("")) && (!tbPengaturanRalan.getValueAt(i, 16).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 17).equals("")) && (!tbPengaturanRalan.getValueAt(i, 18).equals("")) && (!tbPengaturanRalan.getValueAt(i, 19).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 20).equals("")) && (!tbPengaturanRalan.getValueAt(i, 21).equals("")) && (!tbPengaturanRalan.getValueAt(i, 22).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 23).equals("")) && (!tbPengaturanRalan.getValueAt(i, 24).equals("")) && (!tbPengaturanRalan.getValueAt(i, 25).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 26).equals("")) && (!tbPengaturanRalan.getValueAt(i, 27).equals("")) && (!tbPengaturanRalan.getValueAt(i, 28).equals("")) &&
                    (!tbPengaturanRalan.getValueAt(i, 29).equals("")) && (!tbPengaturanRalan.getValueAt(i, 30).equals(""))) {
                    copyakun = "copy";
                    barisdicopy = i;
                    JOptionPane.showMessageDialog(null, "Silahkan pilih tindakan tujuan..!!");
                } else {
                    barisdicopy = -1;
                    copyakun = "";
                    JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu data tindakan yang mau dicopy akun rekeningnya...!!!");
                    tbPengaturanRalan.requestFocus();
                }
            }
        }
    }//GEN-LAST:event_MnCopyRekeningActionPerformed

    private void tbPengaturanRalanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPengaturanRalanMouseClicked
        if (tabModeRalan.getRowCount() != 0) {
            if (evt.getClickCount() == 1) {
                if (copyakun.equals("copy")) {
                    int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data copy akun rekeningnya..??", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Sequel.meghapus("matrik_akun_jns_perawatan", "kd_jenis_prw", tbPengaturanRalan.getValueAt(tbPengaturanRalan.getSelectedRow(), 0).toString());
                        Sequel.menyimpan("matrik_akun_jns_perawatan", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", 14, new String[] {
                            tbPengaturanRalan.getValueAt(tbPengaturanRalan.getSelectedRow(), 0).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 5).toString(),
                            tbPengaturanRalan.getValueAt(barisdicopy, 7).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 9).toString(),
                            tbPengaturanRalan.getValueAt(barisdicopy, 11).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 13).toString(),
                            tbPengaturanRalan.getValueAt(barisdicopy, 15).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 17).toString(),
                            tbPengaturanRalan.getValueAt(barisdicopy, 19).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 21).toString(),
                            tbPengaturanRalan.getValueAt(barisdicopy, 23).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 25).toString(),
                            tbPengaturanRalan.getValueAt(barisdicopy, 27).toString(), tbPengaturanRalan.getValueAt(barisdicopy, 29).toString()
                        });
                        // runBackground(() -> tampilralan());
                        barisdicopy = -1;
                        copyakun = "";
                    } else {
                        barisdicopy = -1;
                        copyakun = "";
                    }
                }
            }
        }
    }//GEN-LAST:event_tbPengaturanRalanMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPengaturanRekeningSMC dialog = new DlgPengaturanRekeningSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private javax.swing.JMenuItem MnCopyRekening;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.Table tbPengaturan;
    private widget.Table tbPengaturanRalan;
    private widget.Table tbPengaturanRanap;
    // End of variables declaration//GEN-END:variables

    private void setPengaturan(String table, String menu, String kodeRekening, String kolom) {
        switch (table) {
            case "set_akun": setAkun.put(kolom, kodeRekening); break;
            case "set_akun2": setAkun2.put(kolom, kodeRekening); break;
            case "set_akun_ralan": setAkunRalan.put(kolom, kodeRekening); break;
            case "set_akun_ranap": setAkunRanap.put(kolom, kodeRekening); break;
            case "set_akun_ranap2": setAkunRanap2.put(kolom, kodeRekening); break;
        }
        Rekening r = akunRekening.get(kodeRekening);
        tabMode.addRow(new Object[] {menu, kodeRekening, r.namaRekening, r.tipe, r.balance, table, kolom});
    }

    private void setPengaturan(int row, String kodeRekening, String namaRekening, String tipe, String balance) {
        String table = tbPengaturan.getValueAt(row, 5).toString();
        String kolom = tbPengaturan.getValueAt(row, 6).toString();
        switch (table) {
            case "set_akun":
                setAkun.put(kolom, kodeRekening);
                break;
            case "set_akun2":
                setAkun2.put(kolom, kodeRekening);
                break;
            case "set_akun_ralan":
                setAkunRalan.put(kolom, kodeRekening);
                break;
            case "set_akun_ranap":
                setAkunRanap.put(kolom, kodeRekening);
                break;
            case "set_akun_ranap2":
                setAkunRanap2.put(kolom, kodeRekening);
                break;
        }
        tabMode.setValueAt(kodeRekening, row, 1);
        tabMode.setValueAt(namaRekening, row, 2);
        tabMode.setValueAt(tipe, row, 3);
        tabMode.setValueAt(balance, row, 4);
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);

        if (akunRekening.isEmpty()) {
            try (ResultSet rs = koneksi.createStatement().executeQuery("select rekening.kd_rek, rekening.nm_rek, rekening.tipe, rekening.balance from rekening")) {
                akunRekening.clear();
                akunRekening.put("", new Rekening("", "", ""));
                while (rs.next()) {
                    akunRekening.put(rs.getString("kd_rek"), new Rekening(rs.getString("nm_rek"), rs.getString("tipe"), rs.getString("balance")));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select ifnull(set_akun_ralan.Suspen_Piutang_Tindakan_Ralan, '') as Suspen_Piutang_Tindakan_Ralan, ifnull(set_akun_ralan.Tindakan_Ralan, '') as Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Medik_Dokter_Tindakan_Ralan, '') as Beban_Jasa_Medik_Dokter_Tindakan_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Medik_Dokter_Tindakan_Ralan, '') as Utang_Jasa_Medik_Dokter_Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Medik_Paramedis_Tindakan_Ralan, '') as Beban_Jasa_Medik_Paramedis_Tindakan_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Medik_Paramedis_Tindakan_Ralan, '') as Utang_Jasa_Medik_Paramedis_Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.Beban_KSO_Tindakan_Ralan, '') as Beban_KSO_Tindakan_Ralan, ifnull(set_akun_ralan.Utang_KSO_Tindakan_Ralan, '') as Utang_KSO_Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Sarana_Tindakan_Ralan, '') as Beban_Jasa_Sarana_Tindakan_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Sarana_Tindakan_Ralan, '') as Utang_Jasa_Sarana_Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.HPP_BHP_Tindakan_Ralan, '') as HPP_BHP_Tindakan_Ralan, ifnull(set_akun_ralan.Persediaan_BHP_Tindakan_Ralan, '') as Persediaan_BHP_Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Menejemen_Tindakan_Ralan, '') as Beban_Jasa_Menejemen_Tindakan_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Menejemen_Tindakan_Ralan, '') as Utang_Jasa_Menejemen_Tindakan_Ralan, " +
            "ifnull(set_akun_ralan.Suspen_Piutang_Laborat_Ralan, '') as Suspen_Piutang_Laborat_Ralan, ifnull(set_akun_ralan.Laborat_Ralan, '') as Laborat_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Medik_Dokter_Laborat_Ralan, '') as Beban_Jasa_Medik_Dokter_Laborat_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Medik_Dokter_Laborat_Ralan, '') as Utang_Jasa_Medik_Dokter_Laborat_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Medik_Petugas_Laborat_Ralan, '') as Beban_Jasa_Medik_Petugas_Laborat_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Medik_Petugas_Laborat_Ralan, '') as Utang_Jasa_Medik_Petugas_Laborat_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Kso_Laborat_Ralan, '') as Beban_Kso_Laborat_Ralan, ifnull(set_akun_ralan.Utang_Kso_Laborat_Ralan, '') as Utang_Kso_Laborat_Ralan, " +
            "ifnull(set_akun_ralan.HPP_Persediaan_Laborat_Rawat_Jalan, '') as HPP_Persediaan_Laborat_Rawat_Jalan, ifnull(set_akun_ralan.Persediaan_BHP_Laborat_Rawat_Jalan, '') as Persediaan_BHP_Laborat_Rawat_Jalan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Sarana_Laborat_Ralan, '') as Beban_Jasa_Sarana_Laborat_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Sarana_Laborat_Ralan, '') as Utang_Jasa_Sarana_Laborat_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Perujuk_Laborat_Ralan, '') as Beban_Jasa_Perujuk_Laborat_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Perujuk_Laborat_Ralan, '') as Utang_Jasa_Perujuk_Laborat_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Menejemen_Laborat_Ralan, '') as Beban_Jasa_Menejemen_Laborat_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Menejemen_Laborat_Ralan, '') as Utang_Jasa_Menejemen_Laborat_Ralan, " +
            "ifnull(set_akun_ralan.Suspen_Piutang_Radiologi_Ralan, '') as Suspen_Piutang_Radiologi_Ralan, ifnull(set_akun_ralan.Radiologi_Ralan, '') as Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Medik_Dokter_Radiologi_Ralan, '') as Beban_Jasa_Medik_Dokter_Radiologi_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Medik_Dokter_Radiologi_Ralan, '') as Utang_Jasa_Medik_Dokter_Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Medik_Petugas_Radiologi_Ralan, '') as Beban_Jasa_Medik_Petugas_Radiologi_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Medik_Petugas_Radiologi_Ralan, '') as Utang_Jasa_Medik_Petugas_Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Kso_Radiologi_Ralan, '') as Beban_Kso_Radiologi_Ralan, ifnull(set_akun_ralan.Utang_Kso_Radiologi_Ralan, '') as Utang_Kso_Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.HPP_Persediaan_Radiologi_Rawat_Jalan, '') as HPP_Persediaan_Radiologi_Rawat_Jalan, ifnull(set_akun_ralan.Persediaan_BHP_Radiologi_Rawat_Jalan, '') as Persediaan_BHP_Radiologi_Rawat_Jalan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Sarana_Radiologi_Ralan, '') as Beban_Jasa_Sarana_Radiologi_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Sarana_Radiologi_Ralan, '') as Utang_Jasa_Sarana_Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Perujuk_Radiologi_Ralan, '') as Beban_Jasa_Perujuk_Radiologi_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Perujuk_Radiologi_Ralan, '') as Utang_Jasa_Perujuk_Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.Beban_Jasa_Menejemen_Radiologi_Ralan, '') as Beban_Jasa_Menejemen_Radiologi_Ralan, ifnull(set_akun_ralan.Utang_Jasa_Menejemen_Radiologi_Ralan, '') as Utang_Jasa_Menejemen_Radiologi_Ralan, " +
            "ifnull(set_akun_ralan.Suspen_Piutang_Obat_Ralan, '') as Suspen_Piutang_Obat_Ralan, ifnull(set_akun_ralan.Obat_Ralan, '') as Obat_Ralan, ifnull(set_akun_ralan.HPP_Obat_Rawat_Jalan, '') as HPP_Obat_Rawat_Jalan, " +
            "ifnull(set_akun_ralan.Persediaan_Obat_Rawat_Jalan, '') as Persediaan_Obat_Rawat_Jalan, ifnull(set_akun_ralan.Registrasi_Ralan, '') as Registrasi_Ralan, ifnull(set_akun_ralan.Suspen_Piutang_Operasi_Ralan, '') " +
            "as Suspen_Piutang_Operasi_Ralan, ifnull(set_akun_ralan.Operasi_Ralan, '') as Operasi_Ralan, ifnull(set_akun_ralan.Beban_Jasa_Medik_Dokter_Operasi_Ralan, '') as Beban_Jasa_Medik_Dokter_Operasi_Ralan, " +
            "ifnull(set_akun_ralan.Utang_Jasa_Medik_Dokter_Operasi_Ralan, '') as Utang_Jasa_Medik_Dokter_Operasi_Ralan, ifnull(set_akun_ralan.Beban_Jasa_Medik_Paramedis_Operasi_Ralan, '') as Beban_Jasa_Medik_Paramedis_Operasi_Ralan, " +
            "ifnull(set_akun_ralan.Utang_Jasa_Medik_Paramedis_Operasi_Ralan, '') as Utang_Jasa_Medik_Paramedis_Operasi_Ralan, ifnull(set_akun_ralan.HPP_Obat_Operasi_Ralan, '') as HPP_Obat_Operasi_Ralan, " +
            "ifnull(set_akun_ralan.Persediaan_Obat_Kamar_Operasi_Ralan, '') as Persediaan_Obat_Kamar_Operasi_Ralan, ifnull(set_akun_ralan.Tambahan_Ralan, '') as Tambahan_Ralan, " +
            "ifnull(set_akun_ralan.Potongan_Ralan, '') as Potongan_Ralan from set_akun_ralan"
        )) {
            if (rs.next()) {
                setAkunRalan.clear();
                setPengaturan("set_akun_ralan", " [Debet] Akun Suspen Piutang Tindakan Rawat Jalan", rs.getString("Suspen_Piutang_Tindakan_Ralan"), "Suspen_Piutang_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Tindakan pada menu Billing Rawat Jalan", rs.getString("Tindakan_Ralan"), "Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Dokter Tindakan Rawat Jalan", rs.getString("Beban_Jasa_Medik_Dokter_Tindakan_Ralan"), "Beban_Jasa_Medik_Dokter_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Dokter Tindakan Rawat Jalan", rs.getString("Utang_Jasa_Medik_Dokter_Tindakan_Ralan"), "Utang_Jasa_Medik_Dokter_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Paramedis Tindakan Rawat Jalan", rs.getString("Beban_Jasa_Medik_Paramedis_Tindakan_Ralan"), "Beban_Jasa_Medik_Paramedis_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Paramedis Tindakan Rawat Jalan", rs.getString("Utang_Jasa_Medik_Paramedis_Tindakan_Ralan"), "Utang_Jasa_Medik_Paramedis_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban KSO Tindakan Rawat Jalan", rs.getString("Beban_KSO_Tindakan_Ralan"), "Beban_KSO_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang KSO Tindakan Rawat Jalan", rs.getString("Utang_KSO_Tindakan_Ralan"), "Utang_KSO_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Sarana Tindakan Rawat Jalan", rs.getString("Beban_Jasa_Sarana_Tindakan_Ralan"), "Beban_Jasa_Sarana_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Sarana Tindakan Rawat Jalan", rs.getString("Utang_Jasa_Sarana_Tindakan_Ralan"), "Utang_Jasa_Sarana_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun HPP BHP Tindakan Rawat Jalan", rs.getString("HPP_BHP_Tindakan_Ralan"), "HPP_BHP_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Persediaan BHP Tindakan Rawat Jalan", rs.getString("Persediaan_BHP_Tindakan_Ralan"), "Persediaan_BHP_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Menejemen Tindakan Rawat Jalan", rs.getString("Beban_Jasa_Menejemen_Tindakan_Ralan"), "Beban_Jasa_Menejemen_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Menejemen Tindakan Rawat Jalan", rs.getString("Utang_Jasa_Menejemen_Tindakan_Ralan"), "Utang_Jasa_Menejemen_Tindakan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Suspen Piutang Laborat Ralan", rs.getString("Suspen_Piutang_Laborat_Ralan"), "Suspen_Piutang_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Laborat pada menu Billing Rawat Jalan", rs.getString("Laborat_Ralan"), "Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Dokter Laborat Rawat Jalan", rs.getString("Beban_Jasa_Medik_Dokter_Laborat_Ralan"), "Beban_Jasa_Medik_Dokter_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Dokter Laborat Rawat Jalan", rs.getString("Utang_Jasa_Medik_Dokter_Laborat_Ralan"), "Utang_Jasa_Medik_Dokter_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Petugas Laborat Rawat Jalan", rs.getString("Beban_Jasa_Medik_Petugas_Laborat_Ralan"), "Beban_Jasa_Medik_Petugas_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Petugas Laborat Rawat Jalan", rs.getString("Utang_Jasa_Medik_Petugas_Laborat_Ralan"), "Utang_Jasa_Medik_Petugas_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban KSO Laborat Rawat Jalan", rs.getString("Beban_Kso_Laborat_Ralan"), "Beban_Kso_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang KSO Laborat Rawat Jalan", rs.getString("Utang_Kso_Laborat_Ralan"), "Utang_Kso_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun HPP BHP Laborat Rawat Jalan", rs.getString("HPP_Persediaan_Laborat_Rawat_Jalan"), "HPP_Persediaan_Laborat_Rawat_Jalan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Persediaan BHP Laborat Rawat Jalan", rs.getString("Persediaan_BHP_Laborat_Rawat_Jalan"), "Persediaan_BHP_Laborat_Rawat_Jalan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Sarana Laborat Rawat Jalan", rs.getString("Beban_Jasa_Sarana_Laborat_Ralan"), "Beban_Jasa_Sarana_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Sarana Laborat Rawat Jalan", rs.getString("Utang_Jasa_Sarana_Laborat_Ralan"), "Utang_Jasa_Sarana_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Perujuk Laborat Rawat Jalan", rs.getString("Beban_Jasa_Perujuk_Laborat_Ralan"), "Beban_Jasa_Perujuk_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Perujuk Laborat Rawat Jalan", rs.getString("Utang_Jasa_Perujuk_Laborat_Ralan"), "Utang_Jasa_Perujuk_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Menejemen Laborat Rawat Jalan", rs.getString("Beban_Jasa_Menejemen_Laborat_Ralan"), "Beban_Jasa_Menejemen_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Menejemen Laborat Rawat Jalan", rs.getString("Utang_Jasa_Menejemen_Laborat_Ralan"), "Utang_Jasa_Menejemen_Laborat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Suspen Piutang Radiologi Rawat Jalan", rs.getString("Suspen_Piutang_Radiologi_Ralan"), "Suspen_Piutang_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Radiologi pada menu Billing Rawat Jalan", rs.getString("Radiologi_Ralan"), "Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Dokter Radiologi Rawat Jalan", rs.getString("Beban_Jasa_Medik_Dokter_Radiologi_Ralan"), "Beban_Jasa_Medik_Dokter_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Dokter Radiologi Rawat Jalan", rs.getString("Utang_Jasa_Medik_Dokter_Radiologi_Ralan"), "Utang_Jasa_Medik_Dokter_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Petugas Radiologi Rawat Jalan", rs.getString("Beban_Jasa_Medik_Petugas_Radiologi_Ralan"), "Beban_Jasa_Medik_Petugas_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Petugas Radiologi Rawat Jalan", rs.getString("Utang_Jasa_Medik_Petugas_Radiologi_Ralan"), "Utang_Jasa_Medik_Petugas_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban KSO Radiologi Rawat Jalan", rs.getString("Beban_Kso_Radiologi_Ralan"), "Beban_Kso_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang KSO Radiologi Rawat Jalan", rs.getString("Utang_Kso_Radiologi_Ralan"), "Utang_Kso_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun HPP BHP Radiologi Rawat Jalan", rs.getString("HPP_Persediaan_Radiologi_Rawat_Jalan"), "HPP_Persediaan_Radiologi_Rawat_Jalan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Persediaan BHP Radiologi Rawat Jalan", rs.getString("Persediaan_BHP_Radiologi_Rawat_Jalan"), "Persediaan_BHP_Radiologi_Rawat_Jalan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Sarana Radiologi Rawat Jalan", rs.getString("Beban_Jasa_Sarana_Radiologi_Ralan"), "Beban_Jasa_Sarana_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Sarana Radiologi Rawat Jalan", rs.getString("Utang_Jasa_Sarana_Radiologi_Ralan"), "Utang_Jasa_Sarana_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Perujuk Radiologi Rawat Jalan", rs.getString("Beban_Jasa_Perujuk_Radiologi_Ralan"), "Beban_Jasa_Perujuk_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Perujuk Radiologi Rawat Jalan", rs.getString("Utang_Jasa_Perujuk_Radiologi_Ralan"), "Utang_Jasa_Perujuk_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Menejemen Radiologi Rawat Jalan", rs.getString("Beban_Jasa_Menejemen_Radiologi_Ralan"), "Beban_Jasa_Menejemen_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Menejemen Radiologi Rawat Jalan", rs.getString("Utang_Jasa_Menejemen_Radiologi_Ralan"), "Utang_Jasa_Menejemen_Radiologi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Suspen Piutang Obat Rawat Jalan", rs.getString("Suspen_Piutang_Obat_Ralan"), "Suspen_Piutang_Obat_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Obat pada menu Billing Rawat Jalan", rs.getString("Obat_Ralan"), "Obat_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun HPP Obat Rawat Jalan", rs.getString("HPP_Obat_Rawat_Jalan"), "HPP_Obat_Rawat_Jalan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Persediaan Obat Rawat Jalan", rs.getString("Persediaan_Obat_Rawat_Jalan"), "Persediaan_Obat_Rawat_Jalan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Registrasi pada menu Billing Rawat Jalan", rs.getString("Registrasi_Ralan"), "Registrasi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Suspen Piutang Operasi Rawat Jalan", rs.getString("Suspen_Piutang_Operasi_Ralan"), "Suspen_Piutang_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Operasi pada menu Billing Rawat Jalan", rs.getString("Operasi_Ralan"), "Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Dokter Operasi Ralan", rs.getString("Beban_Jasa_Medik_Dokter_Operasi_Ralan"), "Beban_Jasa_Medik_Dokter_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Dokter Operasi Ralan", rs.getString("Utang_Jasa_Medik_Dokter_Operasi_Ralan"), "Utang_Jasa_Medik_Dokter_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Beban Jasa Medik Paramedis Operasi Ralan", rs.getString("Beban_Jasa_Medik_Paramedis_Operasi_Ralan"), "Beban_Jasa_Medik_Paramedis_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Utang Jasa Medik Paramedis Operasi Ralan", rs.getString("Utang_Jasa_Medik_Paramedis_Operasi_Ralan"), "Utang_Jasa_Medik_Paramedis_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun HPP Obat Operasi Ralan", rs.getString("HPP_Obat_Operasi_Ralan"), "HPP_Obat_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Persediaan Obat Kamar Operasi Ralan", rs.getString("Persediaan_Obat_Kamar_Operasi_Ralan"), "Persediaan_Obat_Kamar_Operasi_Ralan");
                setPengaturan("set_akun_ralan", " [Kredit] Akun Pendapatan Tambahan Biaya pada menu Billing Rawat Jalan", rs.getString("Tambahan_Ralan"), "Tambahan_Ralan");
                setPengaturan("set_akun_ralan", " [Debet] Akun Potongan Biaya pada Billing menu Rawat Jalan", rs.getString("Potongan_Ralan"), "Potongan_Ralan");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select ifnull(set_akun_ranap.Suspen_Piutang_Tindakan_Ranap, '') as Suspen_Piutang_Tindakan_Ranap, ifnull(set_akun_ranap.Tindakan_Ranap, '') as Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Medik_Dokter_Tindakan_Ranap, '') as Beban_Jasa_Medik_Dokter_Tindakan_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Medik_Dokter_Tindakan_Ranap, '') as Utang_Jasa_Medik_Dokter_Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Medik_Paramedis_Tindakan_Ranap, '') as Beban_Jasa_Medik_Paramedis_Tindakan_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Medik_Paramedis_Tindakan_Ranap, '') as Utang_Jasa_Medik_Paramedis_Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.Beban_KSO_Tindakan_Ranap, '') as Beban_KSO_Tindakan_Ranap, ifnull(set_akun_ranap.Utang_KSO_Tindakan_Ranap, '') as Utang_KSO_Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Sarana_Tindakan_Ranap, '') as Beban_Jasa_Sarana_Tindakan_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Sarana_Tindakan_Ranap, '') as Utang_Jasa_Sarana_Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Menejemen_Tindakan_Ranap, '') as Beban_Jasa_Menejemen_Tindakan_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Menejemen_Tindakan_Ranap, '') as Utang_Jasa_Menejemen_Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.HPP_BHP_Tindakan_Ranap, '') as HPP_BHP_Tindakan_Ranap, ifnull(set_akun_ranap.Persediaan_BHP_Tindakan_Ranap, '') as Persediaan_BHP_Tindakan_Ranap, " +
            "ifnull(set_akun_ranap.Suspen_Piutang_Laborat_Ranap, '') as Suspen_Piutang_Laborat_Ranap, ifnull(set_akun_ranap.Laborat_Ranap, '') as Laborat_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Medik_Dokter_Laborat_Ranap, '') as Beban_Jasa_Medik_Dokter_Laborat_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Medik_Dokter_Laborat_Ranap, '') as Utang_Jasa_Medik_Dokter_Laborat_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Medik_Petugas_Laborat_Ranap, '') as Beban_Jasa_Medik_Petugas_Laborat_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Medik_Petugas_Laborat_Ranap, '') as Utang_Jasa_Medik_Petugas_Laborat_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Kso_Laborat_Ranap, '') as Beban_Kso_Laborat_Ranap, ifnull(set_akun_ranap.Utang_Kso_Laborat_Ranap, '') as Utang_Kso_Laborat_Ranap, " +
            "ifnull(set_akun_ranap.HPP_Persediaan_Laborat_Rawat_inap, '') as HPP_Persediaan_Laborat_Rawat_inap, ifnull(set_akun_ranap.Persediaan_BHP_Laborat_Rawat_Inap, '') as Persediaan_BHP_Laborat_Rawat_Inap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Sarana_Laborat_Ranap, '') as Beban_Jasa_Sarana_Laborat_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Sarana_Laborat_Ranap, '') as Utang_Jasa_Sarana_Laborat_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Perujuk_Laborat_Ranap, '') as Beban_Jasa_Perujuk_Laborat_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Perujuk_Laborat_Ranap, '') as Utang_Jasa_Perujuk_Laborat_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Menejemen_Laborat_Ranap, '') as Beban_Jasa_Menejemen_Laborat_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Menejemen_Laborat_Ranap, '') as Utang_Jasa_Menejemen_Laborat_Ranap, " +
            "ifnull(set_akun_ranap.Suspen_Piutang_Radiologi_Ranap, '') as Suspen_Piutang_Radiologi_Ranap, ifnull(set_akun_ranap.Radiologi_Ranap, '') as Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Medik_Dokter_Radiologi_Ranap, '') as Beban_Jasa_Medik_Dokter_Radiologi_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Medik_Dokter_Radiologi_Ranap, '') as Utang_Jasa_Medik_Dokter_Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Medik_Petugas_Radiologi_Ranap, '') as Beban_Jasa_Medik_Petugas_Radiologi_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Medik_Petugas_Radiologi_Ranap, '') as Utang_Jasa_Medik_Petugas_Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Kso_Radiologi_Ranap, '') as Beban_Kso_Radiologi_Ranap, ifnull(set_akun_ranap.Utang_Kso_Radiologi_Ranap, '') as Utang_Kso_Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.HPP_Persediaan_Radiologi_Rawat_Inap, '') as HPP_Persediaan_Radiologi_Rawat_Inap, ifnull(set_akun_ranap.Persediaan_BHP_Radiologi_Rawat_Inap, '') as Persediaan_BHP_Radiologi_Rawat_Inap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Sarana_Radiologi_Ranap, '') as Beban_Jasa_Sarana_Radiologi_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Sarana_Radiologi_Ranap, '') as Utang_Jasa_Sarana_Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Perujuk_Radiologi_Ranap, '') as Beban_Jasa_Perujuk_Radiologi_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Perujuk_Radiologi_Ranap, '') as Utang_Jasa_Perujuk_Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.Beban_Jasa_Menejemen_Radiologi_Ranap, '') as Beban_Jasa_Menejemen_Radiologi_Ranap, ifnull(set_akun_ranap.Utang_Jasa_Menejemen_Radiologi_Ranap, '') as Utang_Jasa_Menejemen_Radiologi_Ranap, " +
            "ifnull(set_akun_ranap.Suspen_Piutang_Obat_Ranap, '') as Suspen_Piutang_Obat_Ranap, ifnull(set_akun_ranap.Obat_Ranap, '') as Obat_Ranap, ifnull(set_akun_ranap.HPP_Obat_Rawat_Inap, '') as HPP_Obat_Rawat_Inap, " +
            "ifnull(set_akun_ranap.Persediaan_Obat_Rawat_Inap, '') as Persediaan_Obat_Rawat_Inap, ifnull(set_akun_ranap.Registrasi_Ranap, '') as Registrasi_Ranap, ifnull(set_akun_ranap.Service_Ranap, '') as Service_Ranap, " +
            "ifnull(set_akun_ranap.Tambahan_Ranap, '') as Tambahan_Ranap, ifnull(set_akun_ranap.Potongan_Ranap, '') as Potongan_Ranap, ifnull(set_akun_ranap.Retur_Obat_Ranap, '') as Retur_Obat_Ranap, " +
            "ifnull(set_akun_ranap.Resep_Pulang_Ranap, '') as Resep_Pulang_Ranap, ifnull(set_akun_ranap.Kamar_Inap, '') as Kamar_Inap, ifnull(set_akun_ranap.Suspen_Piutang_Operasi_Ranap, '') as Suspen_Piutang_Operasi_Ranap, " +
            "ifnull(set_akun_ranap.Operasi_Ranap, '') as Operasi_Ranap, ifnull(set_akun_ranap.Beban_Jasa_Medik_Dokter_Operasi_Ranap, '') as Beban_Jasa_Medik_Dokter_Operasi_Ranap, " +
            "ifnull(set_akun_ranap.Utang_Jasa_Medik_Dokter_Operasi_Ranap, '') as Utang_Jasa_Medik_Dokter_Operasi_Ranap, ifnull(set_akun_ranap.Beban_Jasa_Medik_Paramedis_Operasi_Ranap, '') as Beban_Jasa_Medik_Paramedis_Operasi_Ranap, " +
            "ifnull(set_akun_ranap.Utang_Jasa_Medik_Paramedis_Operasi_Ranap, '') as Utang_Jasa_Medik_Paramedis_Operasi_Ranap, ifnull(set_akun_ranap.HPP_Obat_Operasi_Ranap, '') as HPP_Obat_Operasi_Ranap from set_akun_ranap"
        )) {
            if (rs.next()) {
                setAkunRanap.clear();
                setPengaturan("set_akun_ranap", " [Debet] Akun Suspen Piutang Tindakan Rawat Inap", rs.getString("Suspen_Piutang_Tindakan_Ranap"), "Suspen_Piutang_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Tindakan Rawat Inap", rs.getString("Tindakan_Ranap"), "Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Dokter Tindakan Ranap", rs.getString("Beban_Jasa_Medik_Dokter_Tindakan_Ranap"), "Beban_Jasa_Medik_Dokter_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Dokter Tindakan Ranap", rs.getString("Utang_Jasa_Medik_Dokter_Tindakan_Ranap"), "Utang_Jasa_Medik_Dokter_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Paramedis Tindakan Ranap", rs.getString("Beban_Jasa_Medik_Paramedis_Tindakan_Ranap"), "Beban_Jasa_Medik_Paramedis_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Paramedis Tindakan Ranap", rs.getString("Utang_Jasa_Medik_Paramedis_Tindakan_Ranap"), "Utang_Jasa_Medik_Paramedis_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban KSO Tindakan Ranap", rs.getString("Beban_KSO_Tindakan_Ranap"), "Beban_KSO_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang KSO Tindakan Ranap", rs.getString("Utang_KSO_Tindakan_Ranap"), "Utang_KSO_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Sarana Tindakan Ranap", rs.getString("Beban_Jasa_Sarana_Tindakan_Ranap"), "Beban_Jasa_Sarana_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Sarana Tindakan Ranap", rs.getString("Utang_Jasa_Sarana_Tindakan_Ranap"), "Utang_Jasa_Sarana_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Menejemen Tindakan Ranap", rs.getString("Beban_Jasa_Menejemen_Tindakan_Ranap"), "Beban_Jasa_Menejemen_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Menejemen Tindakan Ranap", rs.getString("Utang_Jasa_Menejemen_Tindakan_Ranap"), "Utang_Jasa_Menejemen_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun HPP BHP Tindakan Ranap", rs.getString("HPP_BHP_Tindakan_Ranap"), "HPP_BHP_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Persediaan BHP Tindakan Ranap", rs.getString("Persediaan_BHP_Tindakan_Ranap"), "Persediaan_BHP_Tindakan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Suspen Piutang Laborat Ranap", rs.getString("Suspen_Piutang_Laborat_Ranap"), "Suspen_Piutang_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Laborat Rawat Inap", rs.getString("Laborat_Ranap"), "Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Dokter Laborat Ranap", rs.getString("Beban_Jasa_Medik_Dokter_Laborat_Ranap"), "Beban_Jasa_Medik_Dokter_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Dokter Laborat Ranap", rs.getString("Utang_Jasa_Medik_Dokter_Laborat_Ranap"), "Utang_Jasa_Medik_Dokter_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Petugas Laborat Ranap", rs.getString("Beban_Jasa_Medik_Petugas_Laborat_Ranap"), "Beban_Jasa_Medik_Petugas_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Petugas Laborat Ranap", rs.getString("Utang_Jasa_Medik_Petugas_Laborat_Ranap"), "Utang_Jasa_Medik_Petugas_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban KSO Laborat Ranap", rs.getString("Beban_Kso_Laborat_Ranap"), "Beban_Kso_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang KSO Laborat Ranap", rs.getString("Utang_Kso_Laborat_Ranap"), "Utang_Kso_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun HPP Persediaan Laborat Rawat Inap", rs.getString("HPP_Persediaan_Laborat_Rawat_inap"), "HPP_Persediaan_Laborat_Rawat_inap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Persediaan BHP Laborat Rawat Inap", rs.getString("Persediaan_BHP_Laborat_Rawat_Inap"), "Persediaan_BHP_Laborat_Rawat_Inap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Sarana Laborat Ranap", rs.getString("Beban_Jasa_Sarana_Laborat_Ranap"), "Beban_Jasa_Sarana_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Sarana Laborat Ranap", rs.getString("Utang_Jasa_Sarana_Laborat_Ranap"), "Utang_Jasa_Sarana_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Perujuk Laborat Ranap", rs.getString("Beban_Jasa_Perujuk_Laborat_Ranap"), "Beban_Jasa_Perujuk_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Perujuk Laborat Ranap", rs.getString("Utang_Jasa_Perujuk_Laborat_Ranap"), "Utang_Jasa_Perujuk_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Menejemen Laborat Ranap", rs.getString("Beban_Jasa_Menejemen_Laborat_Ranap"), "Beban_Jasa_Menejemen_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Menejemen Laborat Ranap", rs.getString("Utang_Jasa_Menejemen_Laborat_Ranap"), "Utang_Jasa_Menejemen_Laborat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Suspen Piutang Radiologi Ranap", rs.getString("Suspen_Piutang_Radiologi_Ranap"), "Suspen_Piutang_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Radiologi Rawat Inap", rs.getString("Radiologi_Ranap"), "Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Dokter Radiologi Ranap", rs.getString("Beban_Jasa_Medik_Dokter_Radiologi_Ranap"), "Beban_Jasa_Medik_Dokter_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Dokter Radiologi Ranap", rs.getString("Utang_Jasa_Medik_Dokter_Radiologi_Ranap"), "Utang_Jasa_Medik_Dokter_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Petugas Radiologi Ranap", rs.getString("Beban_Jasa_Medik_Petugas_Radiologi_Ranap"), "Beban_Jasa_Medik_Petugas_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Petugas Radiologi Ranap", rs.getString("Utang_Jasa_Medik_Petugas_Radiologi_Ranap"), "Utang_Jasa_Medik_Petugas_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban KSO Radiologi Ranap", rs.getString("Beban_Kso_Radiologi_Ranap"), "Beban_Kso_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang KSO Radiologi Ranap", rs.getString("Utang_Kso_Radiologi_Ranap"), "Utang_Kso_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun HPP Persediaan Radiologi Rawat Inap", rs.getString("HPP_Persediaan_Radiologi_Rawat_Inap"), "HPP_Persediaan_Radiologi_Rawat_Inap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Persediaan BHP Radiologi Rawat Inap", rs.getString("Persediaan_BHP_Radiologi_Rawat_Inap"), "Persediaan_BHP_Radiologi_Rawat_Inap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Sarana Radiologi Ranap", rs.getString("Beban_Jasa_Sarana_Radiologi_Ranap"), "Beban_Jasa_Sarana_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Sarana Radiologi Ranap", rs.getString("Utang_Jasa_Sarana_Radiologi_Ranap"), "Utang_Jasa_Sarana_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Perujuk Radiologi Ranap", rs.getString("Beban_Jasa_Perujuk_Radiologi_Ranap"), "Beban_Jasa_Perujuk_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Perujuk Radiologi Ranap", rs.getString("Utang_Jasa_Perujuk_Radiologi_Ranap"), "Utang_Jasa_Perujuk_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Menejemen Radiologi Ranap", rs.getString("Beban_Jasa_Menejemen_Radiologi_Ranap"), "Beban_Jasa_Menejemen_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Menejemen Radiologi Ranap", rs.getString("Utang_Jasa_Menejemen_Radiologi_Ranap"), "Utang_Jasa_Menejemen_Radiologi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Suspen Piutang Obat Ranap", rs.getString("Suspen_Piutang_Obat_Ranap"), "Suspen_Piutang_Obat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Obat pada menu Billing Rawat Inap", rs.getString("Obat_Ranap"), "Obat_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun HPP Obat Rawat Inap", rs.getString("HPP_Obat_Rawat_Inap"), "HPP_Obat_Rawat_Inap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Persediaan Obat Rawat Inap", rs.getString("Persediaan_Obat_Rawat_Inap"), "Persediaan_Obat_Rawat_Inap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Registrasi pada menu Billing Rawat Inap", rs.getString("Registrasi_Ranap"), "Registrasi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Biaya Service pada menu Billing Rawat Inap", rs.getString("Service_Ranap"), "Service_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Tambahan Biaya pada menu Billing Rawat Inap", rs.getString("Tambahan_Ranap"), "Tambahan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Potongan Biaya pada menu Billing Rawat Inap", rs.getString("Potongan_Ranap"), "Potongan_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Retur Obat pada menu Billing Rawat Inap", rs.getString("Retur_Obat_Ranap"), "Retur_Obat_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Resep Pulang pada menu Billing Rawat Inap", rs.getString("Resep_Pulang_Ranap"), "Resep_Pulang_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Kamar Inap pada menu Billing Rawat Inap", rs.getString("Kamar_Inap"), "Kamar_Inap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Suspen Piutang Operasi Ranap", rs.getString("Suspen_Piutang_Operasi_Ranap"), "Suspen_Piutang_Operasi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Pendapatan Operasi Rawat Inap", rs.getString("Operasi_Ranap"), "Operasi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Dokter Operasi Ranap", rs.getString("Beban_Jasa_Medik_Dokter_Operasi_Ranap"), "Beban_Jasa_Medik_Dokter_Operasi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Dokter Operasi Ranap", rs.getString("Utang_Jasa_Medik_Dokter_Operasi_Ranap"), "Utang_Jasa_Medik_Dokter_Operasi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun Beban Jasa Medik Paramedis Operasi Ranap", rs.getString("Beban_Jasa_Medik_Paramedis_Operasi_Ranap"), "Beban_Jasa_Medik_Paramedis_Operasi_Ranap");
                setPengaturan("set_akun_ranap", " [Kredit] Akun Utang Jasa Medik Paramedis Operasi Ranap", rs.getString("Utang_Jasa_Medik_Paramedis_Operasi_Ranap"), "Utang_Jasa_Medik_Paramedis_Operasi_Ranap");
                setPengaturan("set_akun_ranap", " [Debet] Akun HPP Obat Operasi Ranap", rs.getString("HPP_Obat_Operasi_Ranap"), "HPP_Obat_Operasi_Ranap");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select ifnull(set_akun_ranap2.Persediaan_Obat_Kamar_Operasi_Ranap, '') as Persediaan_Obat_Kamar_Operasi_Ranap, ifnull(set_akun_ranap2.Harian_Ranap, '') as Harian_Ranap, ifnull(set_akun_ranap2.Uang_Muka_Ranap, '') " +
            "as Uang_Muka_Ranap, ifnull(set_akun_ranap2.Piutang_Pasien_Ranap, '') as Piutang_Pasien_Ranap, ifnull(set_akun_ranap2.Sisa_Uang_Muka_Ranap, '') as Sisa_Uang_Muka_Ranap from set_akun_ranap2"
        )) {
            if (rs.next()) {
                setAkunRanap2.clear();
                setPengaturan("set_akun_ranap2", " [Kredit] Akun Persediaan Obat Kamar Operasi Ranap", rs.getString("Persediaan_Obat_Kamar_Operasi_Ranap"), "Persediaan_Obat_Kamar_Operasi_Ranap");
                setPengaturan("set_akun_ranap2", " [Kredit] Akun Pendapatan Harian Ranap pada menu Billing Rawat Inap", rs.getString("Harian_Ranap"), "Harian_Ranap");
                setPengaturan("set_akun_ranap2", " [Kredit] Akun Uang Muka Pasien pada Deposit menu Rawat Inap", rs.getString("Uang_Muka_Ranap"), "Uang_Muka_Ranap");
                setPengaturan("set_akun_ranap2", " [Debet] Akun Piutang Pasien pada Billing menu Rawat Inap", rs.getString("Piutang_Pasien_Ranap"), "Piutang_Pasien_Ranap");
                setPengaturan("set_akun_ranap2", " [Kredit] Akun Sisa Uang Muka Pasien pada Billing menu Rawat Inap", rs.getString("Sisa_Uang_Muka_Ranap"), "Sisa_Uang_Muka_Ranap");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select ifnull(set_akun.Pengadaan_Obat, '') as Pengadaan_Obat, ifnull(set_akun.Pemesanan_Obat, '') as Pemesanan_Obat, ifnull(set_akun.Kontra_Pemesanan_Obat, '') as Kontra_Pemesanan_Obat, " +
            "ifnull(set_akun.Bayar_Pemesanan_Obat, '') as Bayar_Pemesanan_Obat, ifnull(set_akun.Penjualan_Obat, '') as Penjualan_Obat, ifnull(set_akun.Piutang_Obat, '') as Piutang_Obat, " +
            "ifnull(set_akun.Kontra_Piutang_Obat, '') as Kontra_Piutang_Obat, ifnull(set_akun.Retur_Ke_Suplayer, '') as Retur_Ke_Suplayer, ifnull(set_akun.Kontra_Retur_Ke_Suplayer, '') as Kontra_Retur_Ke_Suplayer, " +
            "ifnull(set_akun.Retur_Dari_pembeli, '') as Retur_Dari_pembeli, ifnull(set_akun.Kontra_Retur_Dari_Pembeli, '') as Kontra_Retur_Dari_Pembeli, ifnull(set_akun.Retur_Piutang_Obat, '') as Retur_Piutang_Obat, " +
            "ifnull(set_akun.Kontra_Retur_Piutang_Obat, '') as Kontra_Retur_Piutang_Obat, ifnull(set_akun.Pengadaan_Ipsrs, '') as Pengadaan_Ipsrs, ifnull(set_akun.Stok_Keluar_Ipsrs, '') as Stok_Keluar_Ipsrs, " +
            "ifnull(set_akun.Kontra_Stok_Keluar_Ipsrs, '') as Kontra_Stok_Keluar_Ipsrs, ifnull(set_akun.Bayar_Piutang_Pasien, '') as Bayar_Piutang_Pasien, ifnull(set_akun.Pengambilan_Utd, '') as Pengambilan_Utd, " +
            "ifnull(set_akun.Kontra_Pengambilan_Utd, '') as Kontra_Pengambilan_Utd, ifnull(set_akun.Pengambilan_Penunjang_Utd, '') as Pengambilan_Penunjang_Utd, ifnull(set_akun.Kontra_Pengambilan_Penunjang_Utd, '') as Kontra_Pengambilan_Penunjang_Utd, " +
            "ifnull(set_akun.Penyerahan_Darah, '') as Penyerahan_Darah, ifnull(set_akun.Stok_Keluar_Medis, '') as Stok_Keluar_Medis, ifnull(set_akun.Kontra_Stok_Keluar_Medis, '') as Kontra_Stok_Keluar_Medis, " +
            "ifnull(set_akun.HPP_Obat_Jual_Bebas, '') as HPP_Obat_Jual_Bebas, ifnull(set_akun.Persediaan_Obat_Jual_Bebas, '') as Persediaan_Obat_Jual_Bebas, ifnull(set_akun.Penerimaan_NonMedis, '') as Penerimaan_NonMedis, " +
            "ifnull(set_akun.Kontra_Penerimaan_NonMedis, '') as Kontra_Penerimaan_NonMedis, ifnull(set_akun.Bayar_Pemesanan_Non_Medis, '') as Bayar_Pemesanan_Non_Medis, ifnull(set_akun.Hibah_Obat, '') as Hibah_Obat, " +
            "ifnull(set_akun.Kontra_Hibah_Obat, '') as Kontra_Hibah_Obat, ifnull(set_akun.Penerimaan_Toko, '') as Penerimaan_Toko, ifnull(set_akun.Kontra_Penerimaan_Toko, '') as Kontra_Penerimaan_Toko, " +
            "ifnull(set_akun.Pengadaan_Toko, '') as Pengadaan_Toko, ifnull(set_akun.Bayar_Pemesanan_Toko, '') as Bayar_Pemesanan_Toko, ifnull(set_akun.Penjualan_Toko, '') as Penjualan_Toko, " +
            "ifnull(set_akun.HPP_Barang_Toko, '') as HPP_Barang_Toko, ifnull(set_akun.Persediaan_Barang_Toko, '') as Persediaan_Barang_Toko, ifnull(set_akun.Piutang_Toko, '') as Piutang_Toko, " +
            "ifnull(set_akun.Kontra_Piutang_Toko, '') as Kontra_Piutang_Toko, ifnull(set_akun.Retur_Beli_Toko, '') as Retur_Beli_Toko, ifnull(set_akun.Kontra_Retur_Beli_Toko, '') as Kontra_Retur_Beli_Toko, " +
            "ifnull(set_akun.Retur_Beli_Non_Medis, '') as Retur_Beli_Non_Medis, ifnull(set_akun.Kontra_Retur_Beli_Non_Medis, '') as Kontra_Retur_Beli_Non_Medis, ifnull(set_akun.Retur_Jual_Toko, '') as Retur_Jual_Toko, " +
            "ifnull(set_akun.Kontra_Retur_Jual_Toko, '') as Kontra_Retur_Jual_Toko, ifnull(set_akun.Retur_Piutang_Toko, '') as Retur_Piutang_Toko, ifnull(set_akun.Kontra_Retur_Piutang_Toko, '') as Kontra_Retur_Piutang_Toko, " +
            "ifnull(set_akun.Kerugian_Klaim_BPJS_RVP, '') as Kerugian_Klaim_BPJS_RVP, ifnull(set_akun.Lebih_Bayar_Klaim_BPJS_RVP, '') as Lebih_Bayar_Klaim_BPJS_RVP, ifnull(set_akun.Piutang_BPJS_RVP, '') as Piutang_BPJS_RVP, " +
            "ifnull(set_akun.Kontra_Penerimaan_AsetInventaris, '') as Kontra_Penerimaan_AsetInventaris, ifnull(set_akun.Kontra_Hibah_Aset, '') as Kontra_Hibah_Aset, ifnull(set_akun.Hibah_Non_Medis, '') as Hibah_Non_Medis, " +
            "ifnull(set_akun.Kontra_Hibah_Non_Medis, '') as Kontra_Hibah_Non_Medis, ifnull(set_akun.Beban_Hutang_Lain, '') as Beban_Hutang_Lain, ifnull(set_akun.PPN_Masukan, '') as PPN_Masukan, " +
            "ifnull(set_akun.Pengadaan_Dapur, '') as Pengadaan_Dapur, ifnull(set_akun.Stok_Keluar_Dapur, '') as Stok_Keluar_Dapur, ifnull(set_akun.Kontra_Stok_Keluar_Dapur, '') as Kontra_Stok_Keluar_Dapur, " +
            "ifnull(set_akun.PPN_Keluaran, '') as PPN_Keluaran, ifnull(set_akun.Diskon_Piutang, '') as Diskon_Piutang, ifnull(set_akun.Piutang_Tidak_Terbayar, '') as Piutang_Tidak_Terbayar, " +
            "ifnull(set_akun.Lebih_Bayar_Piutang, '') as Lebih_Bayar_Piutang from set_akun"
        )) {
            if (rs.next()) {
                setAkun.clear();
                setPengaturan("set_akun", " [Debet] Akun Pengadaan Obat & BHP pada menu Pengadaan Obat & BHP", rs.getString("Pengadaan_Obat"), "Pengadaan_Obat");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Penerimaan Obat & BHP pada menu Penerimaan Obat & BHP", rs.getString("Kontra_Pemesanan_Obat"), "Kontra_Pemesanan_Obat");
                setPengaturan("set_akun", " [Debet] Akun Bayar Pemesanan Obat/BHP pada menu Bayar Pesan Obat/BHP", rs.getString("Bayar_Pemesanan_Obat"), "Bayar_Pemesanan_Obat");
                setPengaturan("set_akun", " [Kredit] Akun Penjualan Obat & BHP pada menu Penjualan Obat & BHP", rs.getString("Penjualan_Obat"), "Penjualan_Obat");
                setPengaturan("set_akun", " [Debet] Akun Piutang Obat & BHP pada menu Piutang Obat & BHP", rs.getString("Piutang_Obat"), "Piutang_Obat");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Piutang Obat & BHP pada menu Piutang Obat & BHP", rs.getString("Kontra_Piutang_Obat"), "Kontra_Piutang_Obat");
                setPengaturan("set_akun", " [Kredit] Akun Retur Obat & BHP ke Suplier pada menu Retur Ke Suplier", rs.getString("Retur_Ke_Suplayer"), "Retur_Ke_Suplayer");
                setPengaturan("set_akun", " [Debet] Kontra Akun Retur Obat & BHP ke Suplier pada menu Retur Ke Suplier", rs.getString("Kontra_Retur_Ke_Suplayer"), "Kontra_Retur_Ke_Suplayer");
                setPengaturan("set_akun", " [Kredit] Akun Retur Obat & BHP dari Pasien/Pembeli pada menu Retur Dari Pembeli", rs.getString("Retur_Dari_pembeli"), "Retur_Dari_pembeli");
                setPengaturan("set_akun", " [Debet] Kontra Akun Retur Obat & BHP dari Pasien/Pembeli pada menu Retur Dari Pembeli", rs.getString("Kontra_Retur_Dari_Pembeli"), "Kontra_Retur_Dari_Pembeli");
                setPengaturan("set_akun", " [Debet] Akun Retur Piutang Obat & BHP pada menu Retur Piutang Pembeli", rs.getString("Retur_Piutang_Obat"), "Retur_Piutang_Obat");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Retur Piutang Obat & BHP pada menu Retur Piutang Pembeli", rs.getString("Kontra_Retur_Piutang_Obat"), "Kontra_Retur_Piutang_Obat");
                setPengaturan("set_akun", " [Debet] Akun Pengadaan Barang Non Medis dan Penunjang ( Lab & RO ) pada menu Pengadaan Barang", rs.getString("Pengadaan_Ipsrs"), "Pengadaan_Ipsrs");
                setPengaturan("set_akun", " [Debet] Akun Stok Keluar Barang Non Medis dan Penunjang ( Lab & RO ) pada menu Stok Keluar", rs.getString("Stok_Keluar_Ipsrs"), "Stok_Keluar_Ipsrs");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Stok Keluar Barang Non Medis dan Penunjang ( Lab & RO ) pada menu Stok Keluar", rs.getString("Kontra_Stok_Keluar_Ipsrs"), "Kontra_Stok_Keluar_Ipsrs");
                setPengaturan("set_akun", " [Kredit] Akun Pembayaran Piutang Pasien pada menu Piutang Pasien", rs.getString("Bayar_Piutang_Pasien"), "Bayar_Piutang_Pasien");
                setPengaturan("set_akun", " [Debet] Akun Pengambilan BHP Medis UTD pada menu Pengambilan BHP UTD", rs.getString("Pengambilan_Utd"), "Pengambilan_Utd");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Pengambilan BHP Medis UTD pada menu Pengambilan BHP UTD", rs.getString("Kontra_Pengambilan_Utd"), "Kontra_Pengambilan_Utd");
                setPengaturan("set_akun", " [Debet] Akun Pengambilan Barang Penunjang/Non Medis UTD pada menu Pengambilan Non Medis UTD", rs.getString("Pengambilan_Penunjang_Utd"), "Pengambilan_Penunjang_Utd");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Pengambilan Barang Penunjang/Non Medis UTD pada menu Pengambilan Non Medis UTD", rs.getString("Kontra_Pengambilan_Penunjang_Utd"), "Kontra_Pengambilan_Penunjang_Utd");
                setPengaturan("set_akun", " [Kredit] Akun Pendapatan Penjualan Darah pada menu Penyerahan Darah", rs.getString("Penyerahan_Darah"), "Penyerahan_Darah");
                setPengaturan("set_akun", " [Debet] Akun Stok Keluar Barang Medis (Obat, Alkes & BHP) pada menu Stok Keluar Medis", rs.getString("Stok_Keluar_Medis"), "Stok_Keluar_Medis");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Stok Keluar Barang Medis (Obat, Alkes & BHP) pada menu Stok Keluar Medis", rs.getString("Kontra_Stok_Keluar_Medis"), "Kontra_Stok_Keluar_Medis");
                setPengaturan("set_akun", " [Debet] Akun HPP Obat Jual Bebas pada menu Penjualan Obat Bebas", rs.getString("HPP_Obat_Jual_Bebas"), "HPP_Obat_Jual_Bebas");
                setPengaturan("set_akun", " [Kredit] Akun Persediaan Obat Jual Bebas pada menu Penjualan Obat Bebas", rs.getString("Persediaan_Obat_Jual_Bebas"), "Persediaan_Obat_Jual_Bebas");
                setPengaturan("set_akun", " [Debet] Akun Penerimaan Barang Non Medis dan Penunjang ( Lab & RO ) pada menu Penerimaan Barang Non Medis", rs.getString("Penerimaan_NonMedis"), "Penerimaan_NonMedis");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Penerimaan Barang Non Medis dan Penunjang ( Lab & RO ) pada menu Penerimaan Barang Non Medis", rs.getString("Kontra_Penerimaan_NonMedis"), "Kontra_Penerimaan_NonMedis");
                setPengaturan("set_akun", " [Debet] Akun Bayar Pemesanan Barang Non Medis dan Penunjang ( Lab & RO ) pada menu Bayar Pesan Non Medis", rs.getString("Bayar_Pemesanan_Non_Medis"), "Bayar_Pemesanan_Non_Medis");
                setPengaturan("set_akun", " [Debet] Akun Hibah Obat & BHP pada menu Hibah Obat & BHP", rs.getString("Hibah_Obat"), "Hibah_Obat");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Hibah Obat & BHP pada menu Hibah Obat & BHP", rs.getString("Kontra_Hibah_Obat"), "Kontra_Hibah_Obat");
                setPengaturan("set_akun", " [Debet] Akun Penerimaan Barang Toko pada menu Penerimaan Barang Toko", rs.getString("Penerimaan_Toko"), "Penerimaan_Toko");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Penerimaan Barang Toko pada menu Penerimaan Barang Toko", rs.getString("Kontra_Penerimaan_Toko"), "Kontra_Penerimaan_Toko");
                setPengaturan("set_akun", " [Debet] Akun Pengadaan Barang Toko pada menu Pengadaan Barang Toko", rs.getString("Pengadaan_Toko"), "Pengadaan_Toko");
                setPengaturan("set_akun", " [Debet] Akun Bayar Pemesanan Barang Toko pada menu Bayar Pesan Toko", rs.getString("Bayar_Pemesanan_Toko"), "Bayar_Pemesanan_Toko");
                setPengaturan("set_akun", " [Kredit] Akun Penjualan Toko pada menu Penjualan Toko", rs.getString("Penjualan_Toko"), "Penjualan_Toko");
                setPengaturan("set_akun", " [Debet] Akun HPP Barang Toko pada menu Penjualan Toko", rs.getString("HPP_Barang_Toko"), "HPP_Barang_Toko");
                setPengaturan("set_akun", " [Kredit] Akun Persediaan Barang Toko pada menu Penjualan Toko", rs.getString("Persediaan_Barang_Toko"), "Persediaan_Barang_Toko");
                setPengaturan("set_akun", " [Debet] Akun Piutang Toko pada menu Piutang Toko", rs.getString("Piutang_Toko"), "Piutang_Toko");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Piutang Toko pada menu Piutang Toko", rs.getString("Kontra_Piutang_Toko"), "Kontra_Piutang_Toko");
                setPengaturan("set_akun", " [Kredit] Akun Retur Barang Toko ke Suplier pada menu Retur Ke Suplier Toko", rs.getString("Retur_Beli_Toko"), "Retur_Beli_Toko");
                setPengaturan("set_akun", " [Debet] Kontra Akun Retur Barang Toko ke Suplier pada menu Retur Ke Suplier Toko", rs.getString("Kontra_Retur_Beli_Toko"), "Kontra_Retur_Beli_Toko");
                setPengaturan("set_akun", " [Kredit] Akun Retur Barang Non Medis dan Penunjang ( Lab & RO ) Ke Suplier pada menu Retur Ke Suplier Non Medis", rs.getString("Retur_Beli_Non_Medis"), "Retur_Beli_Non_Medis");
                setPengaturan("set_akun", " [Debet] Kontra Akun Retur Barang Non Medis dan Penunjang ( Lab & RO ) ke Suplier pada menu Retur Ke Suplier Non Medis", rs.getString("Kontra_Retur_Beli_Non_Medis"), "Kontra_Retur_Beli_Non_Medis");
                setPengaturan("set_akun", " [Debet] Akun Retur Jual Toko pada menu Retur Jual Toko", rs.getString("Retur_Jual_Toko"), "Retur_Jual_Toko");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Retur Jual Toko pada menu Retur Jual Toko", rs.getString("Kontra_Retur_Jual_Toko"), "Kontra_Retur_Jual_Toko");
                setPengaturan("set_akun", " [Debet] Akun Retur Piutang Toko pada menu Retur Piutang Toko", rs.getString("Retur_Piutang_Toko"), "Retur_Piutang_Toko");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Retur Piutang Toko pada menu Retur Piutang Toko", rs.getString("Kontra_Retur_Piutang_Toko"), "Kontra_Retur_Piutang_Toko");
                setPengaturan("set_akun", " [Debet] Kerugian Klaim BPJS pada menu RVP Piutang BPJS", rs.getString("Kerugian_Klaim_BPJS_RVP"), "Kerugian_Klaim_BPJS_RVP");
                setPengaturan("set_akun", " [Kredit] Lebih Bayar Klaim BPJS pada menu RVP Piutang BPJS", rs.getString("Lebih_Bayar_Klaim_BPJS_RVP"), "Lebih_Bayar_Klaim_BPJS_RVP");
                setPengaturan("set_akun", " [Kredit] Piutang BPJS pada menu RVP Piutang BPJS", rs.getString("Piutang_BPJS_RVP"), "Piutang_BPJS_RVP");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Penerimaan Barang Aset/Inventaris pada menu Penerimaan Barang Aset/Inventaris", rs.getString("Kontra_Penerimaan_AsetInventaris"), "Kontra_Penerimaan_AsetInventaris");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Hibah Aset/Inventaris pada menu Hibah Aset/Inventaris", rs.getString("Kontra_Hibah_Aset"), "Kontra_Hibah_Aset");
                setPengaturan("set_akun", " [Debet] Akun Hibah Non Medis pada menu Hibah Non Medis", rs.getString("Hibah_Non_Medis"), "Hibah_Non_Medis");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Hibah Non Medis pada menu Hibah Non Medis", rs.getString("Kontra_Hibah_Non_Medis"), "Kontra_Hibah_Non_Medis");
                setPengaturan("set_akun", " [Debet] Beban Hutang Lain pada menu Beban Hutang Lain", rs.getString("Beban_Hutang_Lain"), "Beban_Hutang_Lain");
                setPengaturan("set_akun", " [Debet] PPN Masukan Barang/Aset Inventaris/Alkes/BHP/Obat/Farmasi/Dapur", rs.getString("PPN_Masukan"), "PPN_Masukan");
                setPengaturan("set_akun", " [Debet] Akun Pengadaan Barang Dapur Kering & Basah pada menu Pengadaan Barang Dapur", rs.getString("Pengadaan_Dapur"), "Pengadaan_Dapur");
                setPengaturan("set_akun", " [Debet] Akun Stok Keluar Barang Dapur Kering & Basah pada menu Stok Keluar Dapur", rs.getString("Stok_Keluar_Dapur"), "Stok_Keluar_Dapur");
                setPengaturan("set_akun", " [Kredit] Kontra Akun Stok Keluar Barang Dapur Kering & Basah pada menu Stok Keluar Dapur", rs.getString("Kontra_Stok_Keluar_Dapur"), "Kontra_Stok_Keluar_Dapur");
                setPengaturan("set_akun", " [Kredit] PPN Keluaran Barang/Aset Inventaris/Alkes/BHP/Obat/Farmasi", rs.getString("PPN_Keluaran"), "PPN_Keluaran");
                setPengaturan("set_akun", " [Debet] Diskon Piutang pada menu Piutang Belum Lunas", rs.getString("Diskon_Piutang"), "Diskon_Piutang");
                setPengaturan("set_akun", " [Debet] Piutang Tidak Terbayar pada menu Piutang Belum Lunas", rs.getString("Piutang_Tidak_Terbayar"), "Piutang_Tidak_Terbayar");
                setPengaturan("set_akun", " [Kredit] Lebih Bayar Piutang pada menu Piutang Belum Lunas", rs.getString("Lebih_Bayar_Piutang"), "Lebih_Bayar_Piutang");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select ifnull(set_akun2.Penerimaan_Dapur, '') as Penerimaan_Dapur, ifnull(set_akun2.Kontra_Penerimaan_Dapur, '') as Kontra_Penerimaan_Dapur, ifnull(set_akun2.Bayar_Pemesanan_Dapur, '') as Bayar_Pemesanan_Dapur, " +
            "ifnull(set_akun2.Retur_Beli_Dapur, '') as Retur_Beli_Dapur, ifnull(set_akun2.Kontra_Retur_Beli_Dapur, '') as Kontra_Retur_Beli_Dapur, ifnull(set_akun2.Hibah_Dapur, '') as Hibah_Dapur, " +
            "ifnull(set_akun2.Kontra_Hibah_Dapur, '') as Kontra_Hibah_Dapur, ifnull(set_akun2.Piutang_Jasa_Perusahaan, '') as Piutang_Jasa_Perusahaan, ifnull(set_akun2.Pendapatan_Piutang_Jasa_Perusahaan, '') as Pendapatan_Piutang_Jasa_Perusahaan, " +
            "ifnull(set_akun2.Suspen_Piutang_Pelayanan_Lab_Kesling, '') as Suspen_Piutang_Pelayanan_Lab_Kesling, ifnull(set_akun2.Pendapatan_Pelayanan_Lab_Kesling, '') as Pendapatan_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_Jasa_Sarana_Pelayanan_Lab_Kesling, '') as Beban_Jasa_Sarana_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_Jasa_sarana_Pelayanan_Lab_Kesling, '') as Utang_Jasa_sarana_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.HPP_BHP_Pelayanan_Lab_Kesling, '') as HPP_BHP_Pelayanan_Lab_Kesling, ifnull(set_akun2.Persediaan_BHP_Pelayanan_Lab_Kesling, '') as Persediaan_BHP_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_Jasa_PJLab_Pelayanan_Lab_Kesling, '') as Beban_Jasa_PJLab_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_Jasa_PJLab_Pelayanan_Lab_Kesling, '') as Utang_Jasa_PJLab_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_Jasa_PJPengujian_Pelayanan_Lab_Kesling, '') as Beban_Jasa_PJPengujian_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_Jasa_PJPengujian_Pelayanan_Lab_Kesling, '') as Utang_Jasa_PJPengujian_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling, '') as Beban_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling, '') as Utang_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_Jasa_Analis_Pelayanan_Lab_Kesling, '') as Beban_Jasa_Analis_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_Jasa_Analis_Pelayanan_Lab_Kesling, '') as Utang_Jasa_Analis_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_KSO_Pelayanan_Lab_Kesling, '') as Beban_KSO_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_KSO_Pelayanan_Lab_Kesling, '') as Utang_KSO_Pelayanan_Lab_Kesling, " +
            "ifnull(set_akun2.Beban_Jasa_Menejemen_Pelayanan_Lab_Kesling, '') as Beban_Jasa_Menejemen_Pelayanan_Lab_Kesling, ifnull(set_akun2.Utang_Jasa_Menejemen_Pelayanan_Lab_Kesling, '') as Utang_Jasa_Menejemen_Pelayanan_Lab_Kesling " +
            "from set_akun2"
        )) {
            if (rs.next()) {
                setAkun2.clear();
                setPengaturan("set_akun2", " [Debet] Akun Penerimaan Barang Dapur Kering & Basah pada menu Penerimaan Barang Dapur", rs.getString("Penerimaan_Dapur"), "Penerimaan_Dapur");
                setPengaturan("set_akun2", " [Kredit] Kontra Akun Penerimaan Barang Dapur Kering & Basah pada menu Penerimaan Barang Dapur", rs.getString("Kontra_Penerimaan_Dapur"), "Kontra_Penerimaan_Dapur");
                setPengaturan("set_akun2", " [Debet] Akun Bayar Pemesanan Barang Dapur Kering & Basah pada menu Bayar Pesan Barang Dapur", rs.getString("Bayar_Pemesanan_Dapur"), "Bayar_Pemesanan_Dapur");
                setPengaturan("set_akun2", " [Kredit] Akun Retur Barang Dapur Kering & Basah Ke Suplier pada menu Retur Ke Suplier Dapur", rs.getString("Retur_Beli_Dapur"), "Retur_Beli_Dapur");
                setPengaturan("set_akun2", " [Debet] Kontra Akun Retur Barang Dapur Kering & Basah ke Suplier pada menu Retur Ke Suplier Dapur", rs.getString("Kontra_Retur_Beli_Dapur"), "Kontra_Retur_Beli_Dapur");
                setPengaturan("set_akun2", " [Debet] Akun Hibah Barang Dapur Kering & Basah pada menu Hibah Barang Dapur", rs.getString("Hibah_Dapur"), "Hibah_Dapur");
                setPengaturan("set_akun2", " [Kredit] Kontra Akun Hibah Barang Dapur Kering & Basah pada menu Hibah Barang Dapur", rs.getString("Kontra_Hibah_Dapur"), "Kontra_Hibah_Dapur");
                setPengaturan("set_akun2", " [Debit] Piutang Jasa Pelayanan Perusahaan pada menu Piutang Jasa Perusahaan", rs.getString("Piutang_Jasa_Perusahaan"), "Piutang_Jasa_Perusahaan");
                setPengaturan("set_akun2", " [Kredit] Pendapatan Dari Piutang Jasa Pelayanan Perusahaan pada menu Piutang Jasa Perusahaan", rs.getString("Pendapatan_Piutang_Jasa_Perusahaan"), "Pendapatan_Piutang_Jasa_Perusahaan");
                setPengaturan("set_akun2", " [Debet] Akun Suspen Piutang Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Suspen_Piutang_Pelayanan_Lab_Kesling"), "Suspen_Piutang_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Pendapatan Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Pendapatan_Pelayanan_Lab_Kesling"), "Pendapatan_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban Jasa Sarana Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_Jasa_Sarana_Pelayanan_Lab_Kesling"), "Beban_Jasa_Sarana_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang Jasa Sarana Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_Jasa_sarana_Pelayanan_Lab_Kesling"), "Utang_Jasa_sarana_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun HPP BHP Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("HPP_BHP_Pelayanan_Lab_Kesling"), "HPP_BHP_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Persediaan BHP Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Persediaan_BHP_Pelayanan_Lab_Kesling"), "Persediaan_BHP_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban Jasa PJ Laborat Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_Jasa_PJLab_Pelayanan_Lab_Kesling"), "Beban_Jasa_PJLab_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang Jasa PJ Laborat Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_Jasa_PJLab_Pelayanan_Lab_Kesling"), "Utang_Jasa_PJLab_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban Jasa PJ Pengujian Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_Jasa_PJPengujian_Pelayanan_Lab_Kesling"), "Beban_Jasa_PJPengujian_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang Jasa PJ Pengujian Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_Jasa_PJPengujian_Pelayanan_Lab_Kesling"), "Utang_Jasa_PJPengujian_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban Jasa PJ Verifikasi Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling"), "Beban_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang Jasa PJ Verifikasi Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling"), "Utang_Jasa_PJVerifikasi_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban Jasa Analis Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_Jasa_Analis_Pelayanan_Lab_Kesling"), "Beban_Jasa_Analis_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang Jasa Analis Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_Jasa_Analis_Pelayanan_Lab_Kesling"), "Utang_Jasa_Analis_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban KSO Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_KSO_Pelayanan_Lab_Kesling"), "Beban_KSO_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang KSO Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_KSO_Pelayanan_Lab_Kesling"), "Utang_KSO_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Debet] Akun Beban Jasa Menejemen Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Beban_Jasa_Menejemen_Pelayanan_Lab_Kesling"), "Beban_Jasa_Menejemen_Pelayanan_Lab_Kesling");
                setPengaturan("set_akun2", " [Kredit] Akun Utang Jasa Menejemen Pelayanan Lab Kesehatan Lingkungan Pada Menu Billing Lab Kesling", rs.getString("Utang_Jasa_Menejemen_Pelayanan_Lab_Kesling"), "Utang_Jasa_Menejemen_Pelayanan_Lab_Kesling");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpengaturan_rekening());
    }

    private void tampilralan() {

    }

    private void tampilranap() {

    }

    static class Rekening {
        private String namaRekening;
        private String tipe;
        private String balance;

        public Rekening(final String namaRekening, final String tipe, final String balance) {
            this.namaRekening = namaRekening;
            this.tipe = tipe;
            this.balance = balance;
        }
    }
}
