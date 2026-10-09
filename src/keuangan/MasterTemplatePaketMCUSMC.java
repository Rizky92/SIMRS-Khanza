package keuangan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.tarifralan;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariDokter;
import simrskhanza.DlgCariCaraBayar;

public class MasterTemplatePaketMCUSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeRadiologi, tabModeLabPK, tabModeDetailLabPK, tabModeLabPA, tabModeLabMB, tabModeDetailLabMB,
        tabModeTindakanDr, tabModeTindakanDrPr, tabModeTindakanPr, tabModeTambahanBiaya, tabModePotonganBiaya;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final DlgCariCaraBayar penjab = new DlgCariCaraBayar(null, false);
    private final DlgCariDokter dokter = new DlgCariDokter(null, false);
    private final Map<DefaultTableModel, Integer> versiMuat = new HashMap<>();
    private widget.Table tabelDokter = null;
    private int barisDokter = -1;
    private boolean isLoading = false;

    public MasterTemplatePaketMCUSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {
            "No. Template", "Nama Template", "Jenis Bayar", "Tambahan (Rp)", "Diskon (Rp)", "Total (Rp)"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbTemplate.setModel(tabMode);
        tbTemplate.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTemplate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTemplate.getColumnModel().getColumn(0).setPreferredWidth(140);
        tbTemplate.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbTemplate.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbTemplate.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbTemplate.getColumnModel().getColumn(4).setPreferredWidth(100);
        tbTemplate.getColumnModel().getColumn(5).setPreferredWidth(100);
        tbTemplate.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeRadiologi = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 3) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbRadiologi.setModel(tabModeRadiologi);
        tbRadiologi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRadiologi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRadiologi.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbRadiologi.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbRadiologi.getColumnModel().getColumn(2).setPreferredWidth(520);
        tbRadiologi.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbRadiologi.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeLabPK = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 3) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbLabPK.setModel(tabModeLabPK);
        tbLabPK.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbLabPK.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbLabPK.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbLabPK.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbLabPK.getColumnModel().getColumn(2).setPreferredWidth(520);
        tbLabPK.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbLabPK.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailLabPK = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis", "Harga (Rp)"}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 6) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0 && !"".equals(getValueAt(rowIndex, 4));
            }
        };

        tbDetailLabPK.setModel(tabModeDetailLabPK);
        tbDetailLabPK.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailLabPK.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDetailLabPK.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbDetailLabPK.getColumnModel().getColumn(1).setPreferredWidth(356);
        tbDetailLabPK.getColumnModel().getColumn(2).setPreferredWidth(50);
        tbDetailLabPK.getColumnModel().getColumn(3).setPreferredWidth(245);
        tbDetailLabPK.getColumnModel().getColumn(4).setMinWidth(0);
        tbDetailLabPK.getColumnModel().getColumn(4).setMaxWidth(0);
        tbDetailLabPK.getColumnModel().getColumn(5).setMinWidth(0);
        tbDetailLabPK.getColumnModel().getColumn(5).setMaxWidth(0);
        tbDetailLabPK.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbDetailLabPK.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeLabPA = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 3) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbLabPA.setModel(tabModeLabPA);
        tbLabPA.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbLabPA.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbLabPA.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbLabPA.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbLabPA.getColumnModel().getColumn(2).setPreferredWidth(520);
        tbLabPA.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbLabPA.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeLabMB = new DefaultTableModel(null, new Object[] {"P", "Kode Periksa", "Nama Pemeriksaan", "Harga (Rp)"}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 3) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbLabMB.setModel(tabModeLabMB);
        tbLabMB.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbLabMB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbLabMB.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbLabMB.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbLabMB.getColumnModel().getColumn(2).setPreferredWidth(520);
        tbLabMB.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbLabMB.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailLabMB = new DefaultTableModel(null, new Object[] {"P", "Pemeriksaan", "Satuan", "Nilai Rujukan", "id_template", "Kode Jenis", "Harga (Rp)"}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 6) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0 && !"".equals(getValueAt(rowIndex, 4));
            }
        };

        tbDetailLabMB.setModel(tabModeDetailLabMB);
        tbDetailLabMB.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailLabMB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDetailLabMB.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbDetailLabMB.getColumnModel().getColumn(1).setPreferredWidth(356);
        tbDetailLabMB.getColumnModel().getColumn(2).setMinWidth(0);
        tbDetailLabMB.getColumnModel().getColumn(2).setMaxWidth(0);
        tbDetailLabMB.getColumnModel().getColumn(3).setPreferredWidth(295);
        tbDetailLabMB.getColumnModel().getColumn(4).setMinWidth(0);
        tbDetailLabMB.getColumnModel().getColumn(4).setMaxWidth(0);
        tbDetailLabMB.getColumnModel().getColumn(5).setMinWidth(0);
        tbDetailLabMB.getColumnModel().getColumn(5).setMaxWidth(0);
        tbDetailLabMB.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbDetailLabMB.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTindakanDr = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Nama Perawatan/Tindakan", "Kategori", "Kode Dokter", "Dokter Pemberi Tindakan", "Harga (Rp)"
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 6) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbTindakanDr.setModel(tabModeTindakanDr);
        tbTindakanDr.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanDr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTindakanDr.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbTindakanDr.getColumnModel().getColumn(1).setPreferredWidth(90);
        tbTindakanDr.getColumnModel().getColumn(2).setPreferredWidth(280);
        tbTindakanDr.getColumnModel().getColumn(3).setPreferredWidth(110);
        tbTindakanDr.getColumnModel().getColumn(4).setMinWidth(0);
        tbTindakanDr.getColumnModel().getColumn(4).setMaxWidth(0);
        tbTindakanDr.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbTindakanDr.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbTindakanDr.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTindakanDrPr = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Nama Perawatan/Tindakan", "Kategori", "Kode Dokter", "Dokter Pemberi Tindakan", "Harga (Rp)"
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 6) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbTindakanDrPr.setModel(tabModeTindakanDrPr);
        tbTindakanDrPr.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanDrPr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTindakanDrPr.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbTindakanDrPr.getColumnModel().getColumn(1).setPreferredWidth(90);
        tbTindakanDrPr.getColumnModel().getColumn(2).setPreferredWidth(280);
        tbTindakanDrPr.getColumnModel().getColumn(3).setPreferredWidth(110);
        tbTindakanDrPr.getColumnModel().getColumn(4).setMinWidth(0);
        tbTindakanDrPr.getColumnModel().getColumn(4).setMaxWidth(0);
        tbTindakanDrPr.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbTindakanDrPr.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbTindakanDrPr.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTindakanPr = new DefaultTableModel(null, new Object[] {
            "P", "Kode", "Nama Perawatan/Tindakan", "Kategori", "Kode Dokter", "Dokter Pemberi Tindakan", "Harga (Rp)"
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 6) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };

        tbTindakanPr.setModel(tabModeTindakanPr);
        tbTindakanPr.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTindakanPr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTindakanPr.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbTindakanPr.getColumnModel().getColumn(1).setPreferredWidth(90);
        tbTindakanPr.getColumnModel().getColumn(2).setPreferredWidth(280);
        tbTindakanPr.getColumnModel().getColumn(3).setPreferredWidth(110);
        tbTindakanPr.getColumnModel().getColumn(4).setMinWidth(0);
        tbTindakanPr.getColumnModel().getColumn(4).setMaxWidth(0);
        tbTindakanPr.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbTindakanPr.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbTindakanPr.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeTambahanBiaya = new DefaultTableModel(null, new Object[] {"Nama", "Besar Biaya (Rp)", ""}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex != 2;
            }
        };

        tbTambahanBiaya.setModel(tabModeTambahanBiaya);
        tbTambahanBiaya.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTambahanBiaya.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTambahanBiaya.getColumnModel().getColumn(0).setPreferredWidth(520);
        tbTambahanBiaya.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbTambahanBiaya.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbTambahanBiaya.setDefaultRenderer(Object.class, new WarnaTable());

        tabModePotonganBiaya = new DefaultTableModel(null, new Object[] {"Nama", "Besar Potongan (Rp)", ""}) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Double.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex != 2;
            }
        };

        tbTambahanBiaya1.setModel(tabModePotonganBiaya);
        tbTambahanBiaya1.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTambahanBiaya1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTambahanBiaya1.getColumnModel().getColumn(0).setPreferredWidth(520);
        tbTambahanBiaya1.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbTambahanBiaya1.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbTambahanBiaya1.setDefaultRenderer(Object.class, new WarnaTable());

        for (DefaultTableModel model : new DefaultTableModel[] {
            tabModeRadiologi, tabModeLabPK, tabModeDetailLabPK, tabModeLabPA, tabModeLabMB, tabModeDetailLabMB, tabModeTindakanDr, tabModeTindakanDrPr, tabModeTindakanPr
        }) {
            model.addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.UPDATE) {
                    hitungTotal();
                }
            });
        }

        for (DefaultTableModel model : new DefaultTableModel[] {tabModeTambahanBiaya, tabModePotonganBiaya}) {
            model.addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.UPDATE) {
                    tambahBarisKosong(model);
                }
                hitungTotal();
            });
        }

        for (widget.Table tabel : new widget.Table[] {tbTindakanDr, tbTindakanDrPr, tbTindakanPr}) {
            tabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && tabel.convertColumnIndexToModel(tabel.columnAtPoint(e.getPoint())) == 5) {
                        pilihDokter(tabel);
                    }
                }
            });
            tabel.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE && tabel.convertColumnIndexToModel(tabel.getSelectedColumn()) == 5) {
                        pilihDokter(tabel);
                    }
                }
            });
        }

        for (widget.Table tabel : new widget.Table[] {tbTambahanBiaya, tbTambahanBiaya1}) {
            tabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int baris = tabel.rowAtPoint(e.getPoint());
                    if (baris != -1 && tabel.convertColumnIndexToModel(tabel.columnAtPoint(e.getPoint())) == 2) {
                        hapusBarisBiaya((DefaultTableModel) tabel.getModel(), tabel.convertRowIndexToModel(baris));
                    }
                }
            });
        }

        noTemplate.setDocument(new batasInput((byte) 20).getKata(noTemplate));
        namaTemplate.setDocument(new batasInput((byte) 50).getKata(namaTemplate));
        cariRadiologi.setDocument(new batasInput((byte) 100).getKata(cariRadiologi));
        cariLabPK.setDocument(new batasInput((byte) 100).getKata(cariLabPK));
        cariDetailLabPK.setDocument(new batasInput((byte) 100).getKata(cariDetailLabPK));
        cariLabPA.setDocument(new batasInput((byte) 100).getKata(cariLabPA));
        cariLabMB.setDocument(new batasInput((byte) 100).getKata(cariLabMB));
        cariDetailLabMB.setDocument(new batasInput((byte) 100).getKata(cariDetailLabMB));
        cariTindakanDr.setDocument(new batasInput((byte) 100).getKata(cariTindakanDr));
        cariTindakanDrPr.setDocument(new batasInput((byte) 100).getKata(cariTindakanDrPr));
        cariTindakanPr.setDocument(new batasInput((byte) 100).getKata(cariTindakanPr));
        cariTambahanBiaya.setDocument(new batasInput((byte) 60).getKata(cariTambahanBiaya));
        cariPotonganBiaya.setDocument(new batasInput((byte) 60).getKata(cariPotonganBiaya));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        kodeJenisBayar.setText("-");
        namaJenisBayar.setText("-");
        tabModeTambahanBiaya.addRow(new Object[] {"", 0d, "Hapus"});
        tabModePotonganBiaya.addRow(new Object[] {"", 0d, "Hapus"});

        ChkAccor.setSelected(false);
        isDetail();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Popup = new javax.swing.JPopupMenu();
        ppBersihkan = new javax.swing.JMenuItem();
        ppSemua = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        panelBiasa1 = new widget.PanelBiasa();
        label12 = new widget.Label();
        label13 = new widget.Label();
        namaTemplate = new widget.TextBox();
        noTemplate = new widget.TextBox();
        label14 = new widget.Label();
        kodeJenisBayar = new widget.TextBox();
        namaJenisBayar = new widget.TextBox();
        pilihJenisBayar = new widget.Button();
        label16 = new widget.Label();
        totalBiaya = new widget.TextBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        btnCariRadiologi = new widget.Button();
        Scroll3 = new widget.ScrollPane();
        tbRadiologi = new widget.Table();
        cariRadiologi = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        cariLabPK = new widget.TextBox();
        btnCariLabPK = new widget.Button();
        Scroll4 = new widget.ScrollPane();
        tbLabPK = new widget.Table();
        Scroll5 = new widget.ScrollPane();
        tbDetailLabPK = new widget.Table();
        cariDetailLabPK = new widget.TextBox();
        btnCariDetailLabPK = new widget.Button();
        jLabel17 = new widget.Label();
        cariLabPA = new widget.TextBox();
        btnCariLabPA = new widget.Button();
        Scroll6 = new widget.ScrollPane();
        tbLabPA = new widget.Table();
        jLabel18 = new widget.Label();
        cariLabMB = new widget.TextBox();
        btnCariLabMB = new widget.Button();
        Scroll7 = new widget.ScrollPane();
        tbLabMB = new widget.Table();
        cariDetailLabMB = new widget.TextBox();
        btnCariDetailLabMB = new widget.Button();
        Scroll8 = new widget.ScrollPane();
        tbDetailLabMB = new widget.Table();
        jLabel21 = new widget.Label();
        cariTindakanDr = new widget.TextBox();
        btnCariTindakanDr = new widget.Button();
        Scroll12 = new widget.ScrollPane();
        tbTindakanDr = new widget.Table();
        btnAllRadiologi = new widget.Button();
        btnAllLabPK = new widget.Button();
        btnAllDetailLabPK = new widget.Button();
        btnAllLabPA = new widget.Button();
        btnAllLabMB = new widget.Button();
        btnAllDetailLabMB = new widget.Button();
        btnAllTindakanDr = new widget.Button();
        jLabel22 = new widget.Label();
        cariTindakanDrPr = new widget.TextBox();
        btnCariTindakanDrPr = new widget.Button();
        btnAllTindakanDrPr = new widget.Button();
        Scroll14 = new widget.ScrollPane();
        tbTindakanDrPr = new widget.Table();
        jLabel23 = new widget.Label();
        cariTindakanPr = new widget.TextBox();
        btnCariTindakanPr = new widget.Button();
        btnAllTindakanPr = new widget.Button();
        Scroll15 = new widget.ScrollPane();
        tbTindakanPr = new widget.Table();
        jLabel24 = new widget.Label();
        cariTambahanBiaya = new widget.TextBox();
        btnCariTambahanBiaya = new widget.Button();
        btnAllTambahanBiaya = new widget.Button();
        Scroll16 = new widget.ScrollPane();
        tbTambahanBiaya = new widget.Table();
        jLabel25 = new widget.Label();
        cariPotonganBiaya = new widget.TextBox();
        btnCariPotonganBiaya = new widget.Button();
        btnAllPotonganBiaya = new widget.Button();
        Scroll17 = new widget.ScrollPane();
        tbTambahanBiaya1 = new widget.Table();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbTemplate = new widget.Table();
        panelGlass9 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        FormDetail = new widget.PanelBiasa();
        Scroll13 = new widget.ScrollPane();
        LoadHTML = new widget.editorpane();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        Popup.add(ppBersihkan);

        ppSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppSemua.setText("Pilih Semua");
        ppSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSemua.setName("ppSemua"); // NOI18N
        ppSemua.setPreferredSize(new java.awt.Dimension(200, 25));
        ppSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppSemuaActionPerformed(evt);
            }
        });
        Popup.add(ppSemua);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Template Paket MCU ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout());

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(0, 77));
        panelBiasa1.setLayout(null);

        label12.setText("Kode Template :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label12);
        label12.setBounds(0, 10, 90, 23);

        label13.setText("Nama Template :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label13);
        label13.setBounds(234, 10, 90, 23);

        namaTemplate.setName("namaTemplate"); // NOI18N
        namaTemplate.setPreferredSize(new java.awt.Dimension(390, 23));
        namaTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                namaTemplateKeyPressed(evt);
            }
        });
        panelBiasa1.add(namaTemplate);
        namaTemplate.setBounds(327, 10, 390, 23);

        noTemplate.setName("noTemplate"); // NOI18N
        noTemplate.setPreferredSize(new java.awt.Dimension(138, 23));
        noTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noTemplateKeyPressed(evt);
            }
        });
        panelBiasa1.add(noTemplate);
        noTemplate.setBounds(93, 10, 138, 23);

        label14.setText("Jenis Bayar :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label14);
        label14.setBounds(0, 40, 90, 23);

        kodeJenisBayar.setEditable(false);
        kodeJenisBayar.setName("kodeJenisBayar"); // NOI18N
        kodeJenisBayar.setPreferredSize(new java.awt.Dimension(60, 23));
        kodeJenisBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kodeJenisBayarKeyPressed(evt);
            }
        });
        panelBiasa1.add(kodeJenisBayar);
        kodeJenisBayar.setBounds(93, 40, 60, 23);

        namaJenisBayar.setEditable(false);
        namaJenisBayar.setName("namaJenisBayar"); // NOI18N
        namaJenisBayar.setPreferredSize(new java.awt.Dimension(331, 23));
        panelBiasa1.add(namaJenisBayar);
        namaJenisBayar.setBounds(156, 40, 331, 23);

        pilihJenisBayar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        pilihJenisBayar.setMnemonic('2');
        pilihJenisBayar.setToolTipText("Alt+2");
        pilihJenisBayar.setName("pilihJenisBayar"); // NOI18N
        pilihJenisBayar.setPreferredSize(new java.awt.Dimension(28, 23));
        pilihJenisBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihJenisBayarActionPerformed(evt);
            }
        });
        pilihJenisBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pilihJenisBayarKeyPressed(evt);
            }
        });
        panelBiasa1.add(pilihJenisBayar);
        pilihJenisBayar.setBounds(490, 40, 28, 23);

        label16.setText("Total Biaya (Rp) :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label16);
        label16.setBounds(524, 40, 90, 23);

        totalBiaya.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalBiaya.setText("0");
        totalBiaya.setName("totalBiaya"); // NOI18N
        totalBiaya.setPreferredSize(new java.awt.Dimension(100, 23));
        totalBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                totalBiayaKeyPressed(evt);
            }
        });
        panelBiasa1.add(totalBiaya);
        totalBiaya.setBounds(617, 40, 100, 23);

        internalFrame2.add(panelBiasa1, java.awt.BorderLayout.PAGE_START);

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(742, 2140));
        FormInput.setLayout(null);

        btnCariRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariRadiologi.setMnemonic('1');
        btnCariRadiologi.setToolTipText("Alt+1");
        btnCariRadiologi.setName("btnCariRadiologi"); // NOI18N
        btnCariRadiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariRadiologiActionPerformed(evt);
            }
        });
        FormInput.add(btnCariRadiologi);
        btnCariRadiologi.setBounds(757, 570, 28, 23);

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        tbRadiologi.setComponentPopupMenu(Popup);
        tbRadiologi.setName("tbRadiologi"); // NOI18N
        Scroll3.setViewportView(tbRadiologi);

        FormInput.add(Scroll3);
        Scroll3.setBounds(16, 600, 800, 123);

        cariRadiologi.setName("cariRadiologi"); // NOI18N
        cariRadiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariRadiologiKeyPressed(evt);
            }
        });
        FormInput.add(cariRadiologi);
        cariRadiologi.setBounds(16, 570, 738, 23);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Permintaan Radiologi :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(16, 550, 120, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Permintaan Laborat Patologi Klinis :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(16, 730, 190, 23);

        cariLabPK.setName("cariLabPK"); // NOI18N
        cariLabPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariLabPKKeyPressed(evt);
            }
        });
        FormInput.add(cariLabPK);
        cariLabPK.setBounds(16, 750, 738, 23);

        btnCariLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariLabPK.setMnemonic('1');
        btnCariLabPK.setToolTipText("Alt+1");
        btnCariLabPK.setName("btnCariLabPK"); // NOI18N
        btnCariLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnCariLabPK);
        btnCariLabPK.setBounds(757, 750, 28, 23);

        Scroll4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);

        tbLabPK.setComponentPopupMenu(Popup);
        tbLabPK.setName("tbLabPK"); // NOI18N
        tbLabPK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbLabPKMouseClicked(evt);
            }
        });
        Scroll4.setViewportView(tbLabPK);

        FormInput.add(Scroll4);
        Scroll4.setBounds(16, 780, 800, 113);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);

        tbDetailLabPK.setComponentPopupMenu(Popup);
        tbDetailLabPK.setName("tbDetailLabPK"); // NOI18N
        Scroll5.setViewportView(tbDetailLabPK);

        FormInput.add(Scroll5);
        Scroll5.setBounds(16, 930, 800, 223);

        cariDetailLabPK.setName("cariDetailLabPK"); // NOI18N
        cariDetailLabPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariDetailLabPKKeyPressed(evt);
            }
        });
        FormInput.add(cariDetailLabPK);
        cariDetailLabPK.setBounds(16, 900, 738, 23);

        btnCariDetailLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariDetailLabPK.setMnemonic('1');
        btnCariDetailLabPK.setToolTipText("Alt+1");
        btnCariDetailLabPK.setName("btnCariDetailLabPK"); // NOI18N
        btnCariDetailLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariDetailLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDetailLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnCariDetailLabPK);
        btnCariDetailLabPK.setBounds(757, 900, 28, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Permintaan Laborat Patologi Anatomi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(16, 1160, 250, 23);

        cariLabPA.setName("cariLabPA"); // NOI18N
        cariLabPA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariLabPAKeyPressed(evt);
            }
        });
        FormInput.add(cariLabPA);
        cariLabPA.setBounds(16, 1180, 738, 23);

        btnCariLabPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariLabPA.setMnemonic('1');
        btnCariLabPA.setToolTipText("Alt+1");
        btnCariLabPA.setName("btnCariLabPA"); // NOI18N
        btnCariLabPA.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariLabPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariLabPAActionPerformed(evt);
            }
        });
        FormInput.add(btnCariLabPA);
        btnCariLabPA.setBounds(757, 1180, 28, 23);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbLabPA.setComponentPopupMenu(Popup);
        tbLabPA.setName("tbLabPA"); // NOI18N
        Scroll6.setViewportView(tbLabPA);

        FormInput.add(Scroll6);
        Scroll6.setBounds(16, 1210, 800, 123);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Permintaan Laborat Mikrobiologi & Bio Molekuler :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(16, 1340, 270, 23);

        cariLabMB.setName("cariLabMB"); // NOI18N
        cariLabMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariLabMBKeyPressed(evt);
            }
        });
        FormInput.add(cariLabMB);
        cariLabMB.setBounds(16, 1360, 738, 23);

        btnCariLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariLabMB.setMnemonic('1');
        btnCariLabMB.setToolTipText("Alt+1");
        btnCariLabMB.setName("btnCariLabMB"); // NOI18N
        btnCariLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnCariLabMB);
        btnCariLabMB.setBounds(757, 1360, 28, 23);

        Scroll7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);

        tbLabMB.setComponentPopupMenu(Popup);
        tbLabMB.setName("tbLabMB"); // NOI18N
        tbLabMB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbLabMBMouseClicked(evt);
            }
        });
        Scroll7.setViewportView(tbLabMB);

        FormInput.add(Scroll7);
        Scroll7.setBounds(16, 1390, 800, 113);

        cariDetailLabMB.setName("cariDetailLabMB"); // NOI18N
        cariDetailLabMB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariDetailLabMBKeyPressed(evt);
            }
        });
        FormInput.add(cariDetailLabMB);
        cariDetailLabMB.setBounds(16, 1510, 738, 23);

        btnCariDetailLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariDetailLabMB.setMnemonic('1');
        btnCariDetailLabMB.setToolTipText("Alt+1");
        btnCariDetailLabMB.setName("btnCariDetailLabMB"); // NOI18N
        btnCariDetailLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariDetailLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDetailLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnCariDetailLabMB);
        btnCariDetailLabMB.setBounds(757, 1510, 28, 23);

        Scroll8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);

        tbDetailLabMB.setComponentPopupMenu(Popup);
        tbDetailLabMB.setName("tbDetailLabMB"); // NOI18N
        Scroll8.setViewportView(tbDetailLabMB);

        FormInput.add(Scroll8);
        Scroll8.setBounds(16, 1540, 800, 223);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Tindakan Dokter :");
        jLabel21.setName("jLabel21"); // NOI18N
        FormInput.add(jLabel21);
        jLabel21.setBounds(16, 10, 120, 23);

        cariTindakanDr.setName("cariTindakanDr"); // NOI18N
        cariTindakanDr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTindakanDrKeyPressed(evt);
            }
        });
        FormInput.add(cariTindakanDr);
        cariTindakanDr.setBounds(16, 30, 738, 23);

        btnCariTindakanDr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanDr.setMnemonic('1');
        btnCariTindakanDr.setToolTipText("Alt+1");
        btnCariTindakanDr.setName("btnCariTindakanDr"); // NOI18N
        btnCariTindakanDr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanDr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanDrActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanDr);
        btnCariTindakanDr.setBounds(757, 30, 28, 23);

        Scroll12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll12.setName("Scroll12"); // NOI18N
        Scroll12.setOpaque(true);

        tbTindakanDr.setComponentPopupMenu(Popup);
        tbTindakanDr.setName("tbTindakanDr"); // NOI18N
        Scroll12.setViewportView(tbTindakanDr);

        FormInput.add(Scroll12);
        Scroll12.setBounds(16, 60, 800, 123);

        btnAllRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllRadiologi.setMnemonic('2');
        btnAllRadiologi.setToolTipText("Alt+2");
        btnAllRadiologi.setName("btnAllRadiologi"); // NOI18N
        btnAllRadiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllRadiologiActionPerformed(evt);
            }
        });
        FormInput.add(btnAllRadiologi);
        btnAllRadiologi.setBounds(788, 570, 28, 23);

        btnAllLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllLabPK.setMnemonic('2');
        btnAllLabPK.setToolTipText("Alt+2");
        btnAllLabPK.setName("btnAllLabPK"); // NOI18N
        btnAllLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnAllLabPK);
        btnAllLabPK.setBounds(788, 750, 28, 23);

        btnAllDetailLabPK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllDetailLabPK.setMnemonic('2');
        btnAllDetailLabPK.setToolTipText("Alt+2");
        btnAllDetailLabPK.setName("btnAllDetailLabPK"); // NOI18N
        btnAllDetailLabPK.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllDetailLabPK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllDetailLabPKActionPerformed(evt);
            }
        });
        FormInput.add(btnAllDetailLabPK);
        btnAllDetailLabPK.setBounds(788, 900, 28, 23);

        btnAllLabPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllLabPA.setMnemonic('2');
        btnAllLabPA.setToolTipText("Alt+2");
        btnAllLabPA.setName("btnAllLabPA"); // NOI18N
        btnAllLabPA.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllLabPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllLabPAActionPerformed(evt);
            }
        });
        FormInput.add(btnAllLabPA);
        btnAllLabPA.setBounds(788, 1180, 28, 23);

        btnAllLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllLabMB.setMnemonic('2');
        btnAllLabMB.setToolTipText("Alt+2");
        btnAllLabMB.setName("btnAllLabMB"); // NOI18N
        btnAllLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnAllLabMB);
        btnAllLabMB.setBounds(788, 1360, 28, 23);

        btnAllDetailLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllDetailLabMB.setMnemonic('2');
        btnAllDetailLabMB.setToolTipText("Alt+2");
        btnAllDetailLabMB.setName("btnAllDetailLabMB"); // NOI18N
        btnAllDetailLabMB.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllDetailLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllDetailLabMBActionPerformed(evt);
            }
        });
        FormInput.add(btnAllDetailLabMB);
        btnAllDetailLabMB.setBounds(788, 1510, 28, 23);

        btnAllTindakanDr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanDr.setMnemonic('2');
        btnAllTindakanDr.setToolTipText("Alt+2");
        btnAllTindakanDr.setName("btnAllTindakanDr"); // NOI18N
        btnAllTindakanDr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanDr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanDrActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanDr);
        btnAllTindakanDr.setBounds(788, 30, 28, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Tindakan Dokter & Petugas :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(16, 190, 190, 23);

        cariTindakanDrPr.setName("cariTindakanDrPr"); // NOI18N
        cariTindakanDrPr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTindakanDrPrKeyPressed(evt);
            }
        });
        FormInput.add(cariTindakanDrPr);
        cariTindakanDrPr.setBounds(16, 210, 738, 23);

        btnCariTindakanDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanDrPr.setMnemonic('1');
        btnCariTindakanDrPr.setToolTipText("Alt+1");
        btnCariTindakanDrPr.setName("btnCariTindakanDrPr"); // NOI18N
        btnCariTindakanDrPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanDrPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanDrPrActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanDrPr);
        btnCariTindakanDrPr.setBounds(757, 210, 28, 23);

        btnAllTindakanDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanDrPr.setMnemonic('2');
        btnAllTindakanDrPr.setToolTipText("Alt+2");
        btnAllTindakanDrPr.setName("btnAllTindakanDrPr"); // NOI18N
        btnAllTindakanDrPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanDrPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanDrPrActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanDrPr);
        btnAllTindakanDrPr.setBounds(788, 210, 28, 23);

        Scroll14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll14.setName("Scroll14"); // NOI18N
        Scroll14.setOpaque(true);

        tbTindakanDrPr.setComponentPopupMenu(Popup);
        tbTindakanDrPr.setName("tbTindakanDrPr"); // NOI18N
        Scroll14.setViewportView(tbTindakanDrPr);

        FormInput.add(Scroll14);
        Scroll14.setBounds(16, 240, 800, 123);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Tindakan Petugas :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(16, 370, 190, 23);

        cariTindakanPr.setName("cariTindakanPr"); // NOI18N
        cariTindakanPr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTindakanPrKeyPressed(evt);
            }
        });
        FormInput.add(cariTindakanPr);
        cariTindakanPr.setBounds(16, 390, 738, 23);

        btnCariTindakanPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTindakanPr.setMnemonic('1');
        btnCariTindakanPr.setToolTipText("Alt+1");
        btnCariTindakanPr.setName("btnCariTindakanPr"); // NOI18N
        btnCariTindakanPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTindakanPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTindakanPrActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTindakanPr);
        btnCariTindakanPr.setBounds(757, 390, 28, 23);

        btnAllTindakanPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTindakanPr.setMnemonic('2');
        btnAllTindakanPr.setToolTipText("Alt+2");
        btnAllTindakanPr.setName("btnAllTindakanPr"); // NOI18N
        btnAllTindakanPr.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTindakanPr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTindakanPrActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTindakanPr);
        btnAllTindakanPr.setBounds(788, 390, 28, 23);

        Scroll15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll15.setName("Scroll15"); // NOI18N
        Scroll15.setOpaque(true);

        tbTindakanPr.setComponentPopupMenu(Popup);
        tbTindakanPr.setName("tbTindakanPr"); // NOI18N
        Scroll15.setViewportView(tbTindakanPr);

        FormInput.add(Scroll15);
        Scroll15.setBounds(16, 420, 800, 123);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Tambahan Biaya :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(16, 1770, 120, 23);

        cariTambahanBiaya.setName("cariTambahanBiaya"); // NOI18N
        cariTambahanBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariTambahanBiayaKeyPressed(evt);
            }
        });
        FormInput.add(cariTambahanBiaya);
        cariTambahanBiaya.setBounds(16, 1790, 738, 23);

        btnCariTambahanBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariTambahanBiaya.setMnemonic('1');
        btnCariTambahanBiaya.setToolTipText("Alt+1");
        btnCariTambahanBiaya.setName("btnCariTambahanBiaya"); // NOI18N
        btnCariTambahanBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariTambahanBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariTambahanBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnCariTambahanBiaya);
        btnCariTambahanBiaya.setBounds(757, 1790, 28, 23);

        btnAllTambahanBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllTambahanBiaya.setMnemonic('2');
        btnAllTambahanBiaya.setToolTipText("Alt+2");
        btnAllTambahanBiaya.setName("btnAllTambahanBiaya"); // NOI18N
        btnAllTambahanBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllTambahanBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllTambahanBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnAllTambahanBiaya);
        btnAllTambahanBiaya.setBounds(788, 1790, 28, 23);

        Scroll16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll16.setName("Scroll16"); // NOI18N
        Scroll16.setOpaque(true);

        tbTambahanBiaya.setName("tbTambahanBiaya"); // NOI18N
        Scroll16.setViewportView(tbTambahanBiaya);

        FormInput.add(Scroll16);
        Scroll16.setBounds(16, 1820, 800, 123);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Potongan Biaya :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(16, 1950, 120, 23);

        cariPotonganBiaya.setName("cariPotonganBiaya"); // NOI18N
        cariPotonganBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cariPotonganBiayaKeyPressed(evt);
            }
        });
        FormInput.add(cariPotonganBiaya);
        cariPotonganBiaya.setBounds(16, 1970, 738, 23);

        btnCariPotonganBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariPotonganBiaya.setMnemonic('1');
        btnCariPotonganBiaya.setToolTipText("Alt+1");
        btnCariPotonganBiaya.setName("btnCariPotonganBiaya"); // NOI18N
        btnCariPotonganBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariPotonganBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPotonganBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnCariPotonganBiaya);
        btnCariPotonganBiaya.setBounds(757, 1970, 28, 23);

        btnAllPotonganBiaya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        btnAllPotonganBiaya.setMnemonic('2');
        btnAllPotonganBiaya.setToolTipText("Alt+2");
        btnAllPotonganBiaya.setName("btnAllPotonganBiaya"); // NOI18N
        btnAllPotonganBiaya.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAllPotonganBiaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllPotonganBiayaActionPerformed(evt);
            }
        });
        FormInput.add(btnAllPotonganBiaya);
        btnAllPotonganBiaya.setBounds(788, 1970, 28, 23);

        Scroll17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll17.setName("Scroll17"); // NOI18N
        Scroll17.setOpaque(true);

        tbTambahanBiaya1.setName("tbTambahanBiaya1"); // NOI18N
        Scroll17.setViewportView(tbTambahanBiaya1);

        FormInput.add(Scroll17);
        Scroll17.setBounds(16, 2000, 800, 123);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Template", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbTemplate.setAutoCreateRowSorter(true);
        tbTemplate.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbTemplate.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbTemplate.setName("tbTemplate"); // NOI18N
        tbTemplate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTemplateMouseClicked(evt);
            }
        });
        tbTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbTemplateKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbTemplate);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(530, 23));
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
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

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
        panelGlass9.add(BtnAll);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(430, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout(1, 1));

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setSelected(true);
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

        FormDetail.setBackground(new java.awt.Color(255, 255, 255));
        FormDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), " Detail Template Pemeriksaan : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        FormDetail.setName("FormDetail"); // NOI18N
        FormDetail.setPreferredSize(new java.awt.Dimension(115, 73));
        FormDetail.setLayout(new java.awt.BorderLayout());

        Scroll13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll13.setName("Scroll13"); // NOI18N
        Scroll13.setOpaque(true);
        Scroll13.setPreferredSize(new java.awt.Dimension(200, 200));

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N
        Scroll13.setViewportView(LoadHTML);

        FormDetail.add(Scroll13, java.awt.BorderLayout.CENTER);

        PanelAccor.add(FormDetail, java.awt.BorderLayout.CENTER);

        internalFrame3.add(PanelAccor, java.awt.BorderLayout.EAST);

        TabRawat.addTab("Data Template", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16i.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
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

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
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

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
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

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelGlass8.add(BtnPrint);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass8.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass8.add(LCount);

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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void tbTemplateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbTemplateMouseClicked
        if (tbTemplate.getSelectedRow() != -1) {
            getData();
            if (evt.getClickCount() == 2) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbTemplateMouseClicked

    private void tbTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbTemplateKeyPressed
        if (tbTemplate.getSelectedRow() != -1) {
            if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
                getData();
            } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                getData();
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbTemplateKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbTemplate.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih template yang mau dihapus...!!!");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "Yakin akan menghapus template ini..??", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        boolean sukses = true;
        try {
            Sequel.AutoComitFalse();
            sukses = Sequel.menghapustfSmc("template_paket_mcu_smc", "no_template = ?", noTemplate.getText());
            if (sukses) {
                Sequel.Commit();
            } else {
                Sequel.RollBack();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            sukses = false;
            Sequel.RollBack();
        } finally {
            Sequel.AutoComitTrue();
        }

        if (sukses) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus template paket MCU...!!!");
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnSimpan, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (!cekMasukan()) {
            return;
        }

        if (!Sequel.cariExistsSmc("select * from template_paket_mcu_smc where template_paket_mcu_smc.no_template = ?", noTemplate.getText())) {
            JOptionPane.showMessageDialog(null, "Template belum tersimpan, silahkan gunakan tombol Simpan...!!!");
            return;
        }

        boolean sukses = true;
        try {
            Sequel.AutoComitFalse();
            sukses = Sequel.mengupdatetfSmc("template_paket_mcu_smc", "keterangan = ?, kd_pj = ?, tambahan_rp = ?, diskon_rp = ?", "no_template = ?",
                namaTemplate.getText(), kodeJenisBayar.getText(), Valid.setAngkaSmc(jumlahBiaya(tabModeTambahanBiaya), 2),
                Valid.setAngkaSmc(jumlahBiaya(tabModePotonganBiaya), 2), noTemplate.getText()
            ) && simpanDetail(noTemplate.getText());
            if (sukses) {
                Sequel.Commit();
            } else {
                Sequel.RollBack();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            sukses = false;
            Sequel.RollBack();
        } finally {
            Sequel.AutoComitTrue();
        }

        if (sukses) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengganti template paket MCU, perubahan dibatalkan...!!!");
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnBatal);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnAll, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (!cekMasukan()) {
            return;
        }

        if (Sequel.cariExistsSmc("select * from template_paket_mcu_smc where template_paket_mcu_smc.no_template = ?", noTemplate.getText())) {
            JOptionPane.showMessageDialog(null, "No. template sudah dipakai, gunakan tombol Ganti untuk mengubah template atau tombol Baru untuk nomor baru...!!!");
            return;
        }

        boolean sukses = true;
        try {
            Sequel.AutoComitFalse();
            sukses = Sequel.menyimpantfSmc("template_paket_mcu_smc", "no_template, keterangan, kd_pj, tambahan_rp, diskon_rp",
                noTemplate.getText(), namaTemplate.getText(), kodeJenisBayar.getText(), Valid.setAngkaSmc(jumlahBiaya(tabModeTambahanBiaya), 2),
                Valid.setAngkaSmc(jumlahBiaya(tabModePotonganBiaya), 2)
            ) && simpanDetail(noTemplate.getText());
            if (sukses) {
                Sequel.Commit();
            } else {
                Sequel.RollBack();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            sukses = false;
            Sequel.RollBack();
        } finally {
            Sequel.AutoComitTrue();
        }

        if (sukses) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan template paket MCU, perubahan dibatalkan...!!!");
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, namaTemplate, BtnHapus);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnBatalActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnPrint);
        }
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
        Valid.pindah(evt, TCari, namaTemplate);
    }//GEN-LAST:event_TKdKeyPressed

    private void noTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_noTemplateKeyPressed
        Valid.pindah(evt, TCari, namaTemplate);
    }//GEN-LAST:event_noTemplateKeyPressed

    private void kodeJenisBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kodeJenisBayarKeyPressed
        Valid.pindah(evt, namaTemplate, pilihJenisBayar);
    }//GEN-LAST:event_kodeJenisBayarKeyPressed

    private void pilihJenisBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihJenisBayarActionPerformed
        penjab.isCek();
        penjab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        penjab.setLocationRelativeTo(internalFrame1);
        penjab.setAlwaysOnTop(false);
        penjab.setVisible(true);
    }//GEN-LAST:event_pilihJenisBayarActionPerformed

    private void pilihJenisBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pilihJenisBayarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            pilihJenisBayarActionPerformed(null);
        }
    }//GEN-LAST:event_pilihJenisBayarKeyPressed

    private void btnCariRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariRadiologiActionPerformed
        tampilRadiologi(null);
    }//GEN-LAST:event_btnCariRadiologiActionPerformed

    private void cariRadiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariRadiologiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariRadiologiActionPerformed(null);
        }
    }//GEN-LAST:event_cariRadiologiKeyPressed

    private void cariLabPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariLabPKKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariLabPKActionPerformed(null);
        }
    }//GEN-LAST:event_cariLabPKKeyPressed

    private void btnCariLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabPKActionPerformed
        tampilLabPK(null, null);
    }//GEN-LAST:event_btnCariLabPKActionPerformed

    private void cariDetailLabPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariDetailLabPKKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariDetailLabPKActionPerformed(null);
        }
    }//GEN-LAST:event_cariDetailLabPKKeyPressed

    private void btnCariDetailLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDetailLabPKActionPerformed
        tampilDetailLab(tabModeLabPK, tabModeDetailLabPK, cariDetailLabPK.getText().trim(), null);
    }//GEN-LAST:event_btnCariDetailLabPKActionPerformed

    private void cariLabPAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariLabPAKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariLabPAActionPerformed(null);
        }
    }//GEN-LAST:event_cariLabPAKeyPressed

    private void btnCariLabPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabPAActionPerformed
        tampilLab(tabModeLabPA, "PA", cariLabPA.getText().trim(), null, null);
    }//GEN-LAST:event_btnCariLabPAActionPerformed

    private void cariLabMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariLabMBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariLabMBActionPerformed(null);
        }
    }//GEN-LAST:event_cariLabMBKeyPressed

    private void btnCariLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariLabMBActionPerformed
        tampilLabMB(null, null);
    }//GEN-LAST:event_btnCariLabMBActionPerformed

    private void cariDetailLabMBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariDetailLabMBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariDetailLabMBActionPerformed(null);
        }
    }//GEN-LAST:event_cariDetailLabMBKeyPressed

    private void btnCariDetailLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariDetailLabMBActionPerformed
        tampilDetailLab(tabModeLabMB, tabModeDetailLabMB, cariDetailLabMB.getText().trim(), null);
    }//GEN-LAST:event_btnCariDetailLabMBActionPerformed

    private void cariTindakanDrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTindakanDrKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariTindakanDrActionPerformed(null);
        }
    }//GEN-LAST:event_cariTindakanDrKeyPressed

    private void btnCariTindakanDrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanDrActionPerformed
        tampilTindakan(tabModeTindakanDr, "total_byrdr", cariTindakanDr.getText().trim(), null);
    }//GEN-LAST:event_btnCariTindakanDrActionPerformed

    private void btnAllRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllRadiologiActionPerformed
        cariRadiologi.setText("");
        tampilRadiologi(new HashMap<>());
    }//GEN-LAST:event_btnAllRadiologiActionPerformed

    private void btnAllLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabPKActionPerformed
        cariLabPK.setText("");
        cariDetailLabPK.setText("");
        tampilLabPK(new HashMap<>(), new HashSet<>());
    }//GEN-LAST:event_btnAllLabPKActionPerformed

    private void btnAllDetailLabPKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllDetailLabPKActionPerformed
        cariDetailLabPK.setText("");
        tampilDetailLab(tabModeLabPK, tabModeDetailLabPK, "", new HashSet<>());
    }//GEN-LAST:event_btnAllDetailLabPKActionPerformed

    private void btnAllLabPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabPAActionPerformed
        cariLabPA.setText("");
        tampilLab(tabModeLabPA, "PA", "", new HashMap<>(), null);
    }//GEN-LAST:event_btnAllLabPAActionPerformed

    private void btnAllLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllLabMBActionPerformed
        cariLabMB.setText("");
        cariDetailLabMB.setText("");
        tampilLabMB(new HashMap<>(), new HashSet<>());
    }//GEN-LAST:event_btnAllLabMBActionPerformed

    private void btnAllDetailLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllDetailLabMBActionPerformed
        cariDetailLabMB.setText("");
        tampilDetailLab(tabModeLabMB, tabModeDetailLabMB, "", new HashSet<>());
    }//GEN-LAST:event_btnAllDetailLabMBActionPerformed

    private void btnAllTindakanDrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanDrActionPerformed
        cariTindakanDr.setText("");
        tampilTindakan(tabModeTindakanDr, "total_byrdr", "", new HashMap<>());
    }//GEN-LAST:event_btnAllTindakanDrActionPerformed

    private void tbLabPKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLabPKMouseClicked
        if (tbLabPK.getSelectedRow() != -1 && tbLabPK.convertColumnIndexToModel(tbLabPK.getSelectedColumn()) == 0) {
            btnCariDetailLabPKActionPerformed(null);
        }
    }//GEN-LAST:event_tbLabPKMouseClicked

    private void tbLabMBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbLabMBMouseClicked
        if (tbLabMB.getSelectedRow() != -1 && tbLabMB.convertColumnIndexToModel(tbLabMB.getSelectedColumn()) == 0) {
            btnCariDetailLabMBActionPerformed(null);
        }
    }//GEN-LAST:event_tbLabMBMouseClicked

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        isDetail();
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        tandaiSemua(false);
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void ppSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppSemuaActionPerformed
        tandaiSemua(true);
    }//GEN-LAST:event_ppSemuaActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        penjab.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (penjab.getTable().getSelectedRow() != -1) {
                    kodeJenisBayar.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(), 1).toString());
                    namaJenisBayar.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(), 2).toString());
                    tampilTarif();
                }
                pilihJenisBayar.requestFocus();
            }
        });

        penjab.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    penjab.dispose();
                }
            }
        });

        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (null != tabelDokter) {
                    if (dokter.getTable().getSelectedRow() != -1 && barisDokter < tabelDokter.getModel().getRowCount()) {
                        tabelDokter.getModel().setValueAt(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString(), barisDokter, 4);
                        tabelDokter.getModel().setValueAt(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString(), barisDokter, 5);
                        tabelDokter.getModel().setValueAt(true, barisDokter, 0);
                    }
                    tabelDokter.requestFocus();
                }
                tabelDokter = null;
                barisDokter = -1;
            }
        });

        dokter.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    dokter.dispose();
                }
            }
        });

        tarifralan.SetTarifRalan();
        tampil();
        tampilTarif();
    }//GEN-LAST:event_formWindowOpened

    private void namaTemplateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_namaTemplateKeyPressed
        Valid.pindah(evt, noTemplate, BtnSimpan);
    }//GEN-LAST:event_namaTemplateKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        JOptionPane.showMessageDialog(null, "Cetakan belum tersedia untuk menu ini...!!!");
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void totalBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalBiayaKeyPressed
        Valid.pindah(evt, namaTemplate, BtnSimpan);
    }//GEN-LAST:event_totalBiayaKeyPressed

    private void cariTindakanDrPrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTindakanDrPrKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariTindakanDrPrActionPerformed(null);
        }
    }//GEN-LAST:event_cariTindakanDrPrKeyPressed

    private void btnCariTindakanDrPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanDrPrActionPerformed
        tampilTindakan(tabModeTindakanDrPr, "total_byrdrpr", cariTindakanDrPr.getText().trim(), null);
    }//GEN-LAST:event_btnCariTindakanDrPrActionPerformed

    private void btnAllTindakanDrPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanDrPrActionPerformed
        cariTindakanDrPr.setText("");
        tampilTindakan(tabModeTindakanDrPr, "total_byrdrpr", "", new HashMap<>());
    }//GEN-LAST:event_btnAllTindakanDrPrActionPerformed

    private void cariTindakanPrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTindakanPrKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariTindakanPrActionPerformed(null);
        }
    }//GEN-LAST:event_cariTindakanPrKeyPressed

    private void btnCariTindakanPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTindakanPrActionPerformed
        tampilTindakan(tabModeTindakanPr, "total_byrpr", cariTindakanPr.getText().trim(), null);
    }//GEN-LAST:event_btnCariTindakanPrActionPerformed

    private void btnAllTindakanPrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTindakanPrActionPerformed
        cariTindakanPr.setText("");
        tampilTindakan(tabModeTindakanPr, "total_byrpr", "", new HashMap<>());
    }//GEN-LAST:event_btnAllTindakanPrActionPerformed

    private void cariTambahanBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariTambahanBiayaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariTambahanBiayaActionPerformed(null);
        }
    }//GEN-LAST:event_cariTambahanBiayaKeyPressed

    private void btnCariTambahanBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariTambahanBiayaActionPerformed
        tambahBiaya(tabModeTambahanBiaya, cariTambahanBiaya);
    }//GEN-LAST:event_btnCariTambahanBiayaActionPerformed

    private void btnAllTambahanBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllTambahanBiayaActionPerformed
        cariTambahanBiaya.setText("");
        kosongkanBiaya(tabModeTambahanBiaya);
    }//GEN-LAST:event_btnAllTambahanBiayaActionPerformed

    private void cariPotonganBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cariPotonganBiayaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariPotonganBiayaActionPerformed(null);
        }
    }//GEN-LAST:event_cariPotonganBiayaKeyPressed

    private void btnCariPotonganBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPotonganBiayaActionPerformed
        tambahBiaya(tabModePotonganBiaya, cariPotonganBiaya);
    }//GEN-LAST:event_btnCariPotonganBiayaActionPerformed

    private void btnAllPotonganBiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllPotonganBiayaActionPerformed
        cariPotonganBiaya.setText("");
        kosongkanBiaya(tabModePotonganBiaya);
    }//GEN-LAST:event_btnAllPotonganBiayaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            MasterTemplatePaketMCUSMC dialog = new MasterTemplatePaketMCUSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkAccor;
    private widget.PanelBiasa FormDetail;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll12;
    private widget.ScrollPane Scroll13;
    private widget.ScrollPane Scroll14;
    private widget.ScrollPane Scroll15;
    private widget.ScrollPane Scroll16;
    private widget.ScrollPane Scroll17;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.TextBox TCari;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Button btnAllDetailLabMB;
    private widget.Button btnAllDetailLabPK;
    private widget.Button btnAllLabMB;
    private widget.Button btnAllLabPA;
    private widget.Button btnAllLabPK;
    private widget.Button btnAllPotonganBiaya;
    private widget.Button btnAllRadiologi;
    private widget.Button btnAllTambahanBiaya;
    private widget.Button btnAllTindakanDr;
    private widget.Button btnAllTindakanDrPr;
    private widget.Button btnAllTindakanPr;
    private widget.Button btnCariDetailLabMB;
    private widget.Button btnCariDetailLabPK;
    private widget.Button btnCariLabMB;
    private widget.Button btnCariLabPA;
    private widget.Button btnCariLabPK;
    private widget.Button btnCariPotonganBiaya;
    private widget.Button btnCariRadiologi;
    private widget.Button btnCariTambahanBiaya;
    private widget.Button btnCariTindakanDr;
    private widget.Button btnCariTindakanDrPr;
    private widget.Button btnCariTindakanPr;
    private widget.TextBox cariDetailLabMB;
    private widget.TextBox cariDetailLabPK;
    private widget.TextBox cariLabMB;
    private widget.TextBox cariLabPA;
    private widget.TextBox cariLabPK;
    private widget.TextBox cariPotonganBiaya;
    private widget.TextBox cariRadiologi;
    private widget.TextBox cariTambahanBiaya;
    private widget.TextBox cariTindakanDr;
    private widget.TextBox cariTindakanDrPr;
    private widget.TextBox cariTindakanPr;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.TextBox kodeJenisBayar;
    private widget.Label label10;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label16;
    private widget.Label label9;
    private widget.TextBox namaJenisBayar;
    private widget.TextBox namaTemplate;
    private widget.TextBox noTemplate;
    private widget.PanelBiasa panelBiasa1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Button pilihJenisBayar;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppSemua;
    private widget.ScrollPane scrollInput;
    private widget.Table tbDetailLabMB;
    private widget.Table tbDetailLabPK;
    private widget.Table tbLabMB;
    private widget.Table tbLabPA;
    private widget.Table tbLabPK;
    private widget.Table tbRadiologi;
    private widget.Table tbTambahanBiaya;
    private widget.Table tbTambahanBiaya1;
    private widget.Table tbTemplate;
    private widget.Table tbTindakanDr;
    private widget.Table tbTindakanDrPr;
    private widget.Table tbTindakanPr;
    private widget.TextBox totalBiaya;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        if (!isLoading) {
            isLoading = true;
            Valid.tabelKosongSmc(tabMode);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<Void, Object[]>() {
                final String cari = TCari.getText().trim();
                final String kodePJ = kodeJenisBayar.getText();
                final boolean filterPJ = pakaiJenisBayar() && !"-".equals(kodePJ);

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select template_paket_mcu_smc.no_template, template_paket_mcu_smc.keterangan, penjab.png_jawab, template_paket_mcu_smc.tambahan_rp, " +
                        "template_paket_mcu_smc.diskon_rp from template_paket_mcu_smc join penjab on template_paket_mcu_smc.kd_pj = penjab.kd_pj where 1 = 1 " +
                        (filterPJ ? "and (template_paket_mcu_smc.kd_pj = ? or template_paket_mcu_smc.kd_pj = '-') " : "") + (cari.isBlank() ? "" : "and " +
                        "(template_paket_mcu_smc.no_template like ? or template_paket_mcu_smc.keterangan like ? or penjab.png_jawab like ?) ") +
                        "order by template_paket_mcu_smc.keterangan"
                    )) {
                        int p = 0;
                        if (filterPJ) {
                            ps.setString(++p, kodePJ);
                        }
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                publish(new Object[] {
                                    rs.getString("no_template"), rs.getString("keterangan"), rs.getString("png_jawab"), Valid.SetAngka(rs.getDouble("tambahan_rp")),
                                    Valid.SetAngka(rs.getDouble("diskon_rp")), Valid.SetAngka(totalTemplate(rs.getString("no_template")) + rs.getDouble("tambahan_rp") - rs.getDouble("diskon_rp"))
                                });
                            }
                        }
                    }

                    return null;
                }

                @Override
                protected void process(List<Object[]> chunks) {
                    chunks.forEach(tabMode::addRow);
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    tabMode.fireTableDataChanged();
                    LCount.setText(String.valueOf(tabMode.getRowCount()));
                    MasterTemplatePaketMCUSMC.this.setCursor(Cursor.getDefaultCursor());
                    isLoading = false;
                }
            }.execute();
        }
    }

    private double totalTemplate(String noTemplate) {
        return Sequel.cariDoubleSmc(
            "select ifnull((select sum(jns_perawatan_radiologi.total_byr) from template_paket_mcu_smc_permintaan_radiologi join jns_perawatan_radiologi on " +
            "template_paket_mcu_smc_permintaan_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw where template_paket_mcu_smc_permintaan_radiologi.no_template = ?), 0) + " +
            "ifnull((select sum(jns_perawatan_lab.total_byr) from template_paket_mcu_smc_permintaan_lab join jns_perawatan_lab on template_paket_mcu_smc_permintaan_lab.kd_jenis_prw = " +
            "jns_perawatan_lab.kd_jenis_prw where template_paket_mcu_smc_permintaan_lab.no_template = ?), 0) + ifnull((select sum(template_laboratorium.biaya_item) from " +
            "template_paket_mcu_smc_detail_permintaan_lab join template_laboratorium on template_paket_mcu_smc_detail_permintaan_lab.id_template = " +
            "template_laboratorium.id_template where template_paket_mcu_smc_detail_permintaan_lab.no_template = ?), 0) + ifnull((select sum(jns_perawatan.total_byrdr) from " +
            "template_paket_mcu_smc_tindakan_dr join jns_perawatan on template_paket_mcu_smc_tindakan_dr.kd_jenis_prw = jns_perawatan.kd_jenis_prw where " +
            "template_paket_mcu_smc_tindakan_dr.no_template = ?), 0) + ifnull((select sum(jns_perawatan.total_byrdrpr) from template_paket_mcu_smc_tindakan_drpr join jns_perawatan on " +
            "template_paket_mcu_smc_tindakan_drpr.kd_jenis_prw = jns_perawatan.kd_jenis_prw where template_paket_mcu_smc_tindakan_drpr.no_template = ?), 0) + ifnull((select " +
            "sum(jns_perawatan.total_byrpr) from template_paket_mcu_smc_tindakan_pr join jns_perawatan on template_paket_mcu_smc_tindakan_pr.kd_jenis_prw = jns_perawatan.kd_jenis_prw " +
            "where template_paket_mcu_smc_tindakan_pr.no_template = ?), 0)", 0, noTemplate, noTemplate, noTemplate, noTemplate, noTemplate, noTemplate
        );
    }

    private boolean pakaiJenisBayar() {
        return "Yes".equals(tarifralan.getCaraBayarRalan());
    }

    private void tampilTarif() {
        tampilRadiologi(null);
        tampilLabPK(null, null);
        tampilLab(tabModeLabPA, "PA", cariLabPA.getText().trim(), null, null);
        tampilLabMB(null, null);
        tampilTindakan(tabModeTindakanDr, "total_byrdr", cariTindakanDr.getText().trim(), null);
        tampilTindakan(tabModeTindakanDrPr, "total_byrdrpr", cariTindakanDrPr.getText().trim(), null);
        tampilTindakan(tabModeTindakanPr, "total_byrpr", cariTindakanPr.getText().trim(), null);
    }

    private void tampilRadiologi(Map<String, String[]> pilihan) {
        String cari = cariRadiologi.getText().trim();
        List<String> params = new ArrayList<>();
        if (pakaiJenisBayar()) {
            params.add(kodeJenisBayar.getText());
        }
        if (!cari.isBlank()) {
            params.add("%" + cari + "%");
            params.add("%" + cari + "%");
        }

        muatTarif(tabModeRadiologi, pilihan, null,
            "select jns_perawatan_radiologi.kd_jenis_prw, jns_perawatan_radiologi.nm_perawatan, jns_perawatan_radiologi.total_byr from jns_perawatan_radiologi " +
            "where jns_perawatan_radiologi.status = '1' " + (pakaiJenisBayar() ? "and (jns_perawatan_radiologi.kd_pj = ? or jns_perawatan_radiologi.kd_pj = '-') " : "") +
            (cari.isBlank() ? "" : "and (jns_perawatan_radiologi.kd_jenis_prw like ? or jns_perawatan_radiologi.nm_perawatan like ?) ") +
            "order by jns_perawatan_radiologi.nm_perawatan", params
        );
    }

    private void tampilLabPK(Map<String, String[]> pilihan, Set<String> pilihanDetail) {
        tampilLab(tabModeLabPK, "PK", cariLabPK.getText().trim(), pilihan, () -> tampilDetailLab(tabModeLabPK, tabModeDetailLabPK, cariDetailLabPK.getText().trim(), pilihanDetail));
    }

    private void tampilLabMB(Map<String, String[]> pilihan, Set<String> pilihanDetail) {
        tampilLab(tabModeLabMB, "MB", cariLabMB.getText().trim(), pilihan, () -> tampilDetailLab(tabModeLabMB, tabModeDetailLabMB, cariDetailLabMB.getText().trim(), pilihanDetail));
    }

    private void tampilLab(DefaultTableModel model, String kategori, String cari, Map<String, String[]> pilihan, Runnable selesai) {
        List<String> params = new ArrayList<>();
        params.add(kategori);
        if (pakaiJenisBayar()) {
            params.add(kodeJenisBayar.getText());
        }
        if (!cari.isBlank()) {
            params.add("%" + cari + "%");
            params.add("%" + cari + "%");
        }

        muatTarif(model, pilihan, selesai,
            "select jns_perawatan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan, jns_perawatan_lab.total_byr from jns_perawatan_lab where jns_perawatan_lab.status = '1' " +
            "and jns_perawatan_lab.kategori = ? " + (pakaiJenisBayar() ? "and (jns_perawatan_lab.kd_pj = ? or jns_perawatan_lab.kd_pj = '-') " : "") + (cari.isBlank() ? "" :
            "and (jns_perawatan_lab.kd_jenis_prw like ? or jns_perawatan_lab.nm_perawatan like ?) ") + "order by jns_perawatan_lab.nm_perawatan", params
        );
    }

    private void tampilTindakan(DefaultTableModel model, String kolomTarif, String cari, Map<String, String[]> pilihan) {
        List<String> params = new ArrayList<>();
        if (pakaiJenisBayar()) {
            params.add(kodeJenisBayar.getText());
        }
        if (!cari.isBlank()) {
            params.add("%" + cari + "%");
            params.add("%" + cari + "%");
            params.add("%" + cari + "%");
        }

        muatTarif(model, pilihan, null,
            "select jns_perawatan.kd_jenis_prw, jns_perawatan.nm_perawatan, kategori_perawatan.nm_kategori, '' as kd_dokter, '' as nm_dokter, jns_perawatan." + kolomTarif + " " +
            "from jns_perawatan join kategori_perawatan on jns_perawatan.kd_kategori = kategori_perawatan.kd_kategori where jns_perawatan.status = '1' and jns_perawatan." +
            kolomTarif + " > 0 " + (pakaiJenisBayar() ? "and (jns_perawatan.kd_pj = ? or jns_perawatan.kd_pj = '-') " : "") + (cari.isBlank() ? "" : "and " +
            "(jns_perawatan.kd_jenis_prw like ? or jns_perawatan.nm_perawatan like ? or kategori_perawatan.nm_kategori like ?) ") + "order by jns_perawatan.nm_perawatan", params
        );
    }

    private void muatTarif(DefaultTableModel model, Map<String, String[]> pilihan, Runnable selesai, String sql, List<String> params) {
        final int versi = versiMuat.merge(model, 1, Integer::sum);
        final List<Object[]> terpilih = new ArrayList<>();
        final Set<String> kodeTerpilih = new HashSet<>();
        if (null == pilihan) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    Object[] baris = new Object[model.getColumnCount()];
                    for (int j = 0; j < baris.length; j++) {
                        baris[j] = model.getValueAt(i, j);
                    }
                    terpilih.add(baris);
                    kodeTerpilih.add(baris[1].toString());
                }
            }
        }

        Valid.tabelKosongSmc(model);
        terpilih.forEach(model::addRow);
        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                    for (int i = 0; i < params.size(); i++) {
                        ps.setString(i + 1, params.get(i));
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        int jumlahKolom = rs.getMetaData().getColumnCount();
                        while (rs.next()) {
                            if (kodeTerpilih.contains(rs.getString(1))) {
                                continue;
                            }
                            Object[] baris = new Object[jumlahKolom + 1];
                            baris[0] = null != pilihan && pilihan.containsKey(rs.getString(1));
                            for (int i = 1; i <= jumlahKolom; i++) {
                                baris[i] = rs.getObject(i);
                            }
                            if ((Boolean) baris[0] && pilihan.get(rs.getString(1)).length == 2) {
                                baris[4] = pilihan.get(rs.getString(1))[0];
                                baris[5] = pilihan.get(rs.getString(1))[1];
                            }
                            publish(baris);
                        }
                    }
                }

                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                if (versi == versiMuat.get(model)) {
                    chunks.forEach(model::addRow);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
                if (versi == versiMuat.get(model)) {
                    model.fireTableDataChanged();
                    if (null != selesai) {
                        selesai.run();
                    }
                }
            }
        }.execute();
    }

    private void tampilDetailLab(DefaultTableModel tarif, DefaultTableModel model, String cari, Set<String> pilihan) {
        final int versi = versiMuat.merge(model, 1, Integer::sum);
        final List<String[]> pemeriksaan = new ArrayList<>();
        for (int i = 0; i < tarif.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tarif.getValueAt(i, 0))) {
                pemeriksaan.add(new String[] {tarif.getValueAt(i, 1).toString(), tarif.getValueAt(i, 2).toString()});
            }
        }

        final List<Object[]> terpilih = new ArrayList<>();
        final Set<String> idTerpilih = new HashSet<>();
        if (null == pilihan) {
            for (int i = 0; i < model.getRowCount(); i++) {
                final Object kode = model.getValueAt(i, 5);
                if (Boolean.TRUE.equals(model.getValueAt(i, 0)) && pemeriksaan.stream().anyMatch(p -> p[0].equals(kode))) {
                    terpilih.add(new Object[] {
                        true, model.getValueAt(i, 1), model.getValueAt(i, 2), model.getValueAt(i, 3), model.getValueAt(i, 4), model.getValueAt(i, 5), model.getValueAt(i, 6)
                    });
                    idTerpilih.add(model.getValueAt(i, 4).toString());
                }
            }
        }

        Valid.tabelKosongSmc(model);
        terpilih.forEach(model::addRow);
        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select template_laboratorium.id_template, template_laboratorium.Pemeriksaan, template_laboratorium.satuan, template_laboratorium.nilai_rujukan_ld, " +
                    "template_laboratorium.nilai_rujukan_la, template_laboratorium.nilai_rujukan_pd, template_laboratorium.nilai_rujukan_pa, template_laboratorium.biaya_item " +
                    "from template_laboratorium " +
                    "where template_laboratorium.kd_jenis_prw = ? " + (cari.isBlank() ? "" : "and template_laboratorium.Pemeriksaan like ? ") + "order by template_laboratorium.urut"
                )) {
                    for (String[] periksa : pemeriksaan) {
                        publish(new Object[] {false, periksa[1], "", "", "", periksa[0], null});
                        ps.setString(1, periksa[0]);
                        if (!cari.isBlank()) {
                            ps.setString(2, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                if (idTerpilih.contains(rs.getString("id_template"))) {
                                    continue;
                                }
                                StringJoiner rujukan = new StringJoiner(", ");
                                for (String[] kolom : new String[][] {{"LD", "nilai_rujukan_ld"}, {"LA", "nilai_rujukan_la"}, {"PD", "nilai_rujukan_pd"}, {"PA", "nilai_rujukan_pa"}}) {
                                    if (null != rs.getString(kolom[1]) && !rs.getString(kolom[1]).isBlank()) {
                                        rujukan.add(kolom[0] + " : " + rs.getString(kolom[1]));
                                    }
                                }
                                publish(new Object[] {
                                    null != pilihan && pilihan.contains(rs.getString("id_template")), "   " + rs.getString("Pemeriksaan"), rs.getString("satuan"),
                                    rujukan.toString(), rs.getString("id_template"), periksa[0], rs.getDouble("biaya_item")
                                });
                            }
                        }
                    }
                }

                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                if (versi == versiMuat.get(model)) {
                    chunks.forEach(model::addRow);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
                if (versi == versiMuat.get(model)) {
                    model.fireTableDataChanged();
                }
            }
        }.execute();
    }

    private void tampilBiaya(DefaultTableModel model, String sql, String noTemplate) {
        Valid.tabelKosongSmc(model);
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {rs.getString(1), rs.getDouble(2), "Hapus"});
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        model.addRow(new Object[] {"", 0d, "Hapus"});
        model.fireTableDataChanged();
    }

    private Map<String, String[]> pilihanTersimpan(String sql, String noTemplate) {
        Map<String, String[]> pilihan = new HashMap<>();
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                int jumlahKolom = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    String[] isi = new String[jumlahKolom - 1];
                    for (int i = 2; i <= jumlahKolom; i++) {
                        isi[i - 2] = rs.getString(i);
                    }
                    pilihan.put(rs.getString(1), isi);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        return pilihan;
    }

    private void pilihDokter(widget.Table tabel) {
        if (tabel.getSelectedRow() != -1) {
            tabelDokter = tabel;
            barisDokter = tabel.convertRowIndexToModel(tabel.getSelectedRow());
            dokter.isCek();
            dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokter.setLocationRelativeTo(internalFrame1);
            dokter.setAlwaysOnTop(false);
            dokter.setVisible(true);
        }
    }

    private void tandaiSemua(boolean pilih) {
        if (Popup.getInvoker() instanceof widget.Table) {
            widget.Table tabel = (widget.Table) Popup.getInvoker();
            for (int i = 0; i < tabel.getModel().getRowCount(); i++) {
                if (tabel.getModel().isCellEditable(i, 0)) {
                    tabel.getModel().setValueAt(pilih, i, 0);
                }
            }

            if (tabel == tbLabPK) {
                btnCariDetailLabPKActionPerformed(null);
            } else if (tabel == tbLabMB) {
                btnCariDetailLabMBActionPerformed(null);
            }
        }
    }

    private void tambahBiaya(DefaultTableModel model, widget.TextBox nama) {
        if (nama.getText().isBlank()) {
            Valid.textKosong(nama, "Nama biaya");
            return;
        }

        model.insertRow(Math.max(0, model.getRowCount() - 1), new Object[] {nama.getText().trim(), 0d, "Hapus"});
        nama.setText("");
    }

    private void kosongkanBiaya(DefaultTableModel model) {
        Valid.tabelKosongSmc(model);
        model.addRow(new Object[] {"", 0d, "Hapus"});
        model.fireTableDataChanged();
    }

    private void tambahBarisKosong(DefaultTableModel model) {
        if (model.getRowCount() == 0 || !model.getValueAt(model.getRowCount() - 1, 0).toString().isBlank()) {
            model.addRow(new Object[] {"", 0d, "Hapus"});
        }
    }

    private void hapusBarisBiaya(DefaultTableModel model, int baris) {
        if (model.getRowCount() <= 1) {
            kosongkanBiaya(model);
            return;
        }

        model.removeRow(baris);
        tambahBarisKosong(model);
    }

    private double angka(Object nilai) {
        if (nilai instanceof Number) {
            return ((Number) nilai).doubleValue();
        }

        return 0;
    }

    private double jumlahBiaya(DefaultTableModel model) {
        double jumlah = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (!model.getValueAt(i, 0).toString().isBlank()) {
                jumlah += angka(model.getValueAt(i, 1));
            }
        }

        return jumlah;
    }

    private double jumlahTerpilih(DefaultTableModel model, int kolomHarga) {
        double jumlah = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                jumlah += angka(model.getValueAt(i, kolomHarga));
            }
        }

        return jumlah;
    }

    private void hitungTotal() {
        totalBiaya.setText(Valid.SetAngka(
            jumlahTerpilih(tabModeRadiologi, 3) + jumlahTerpilih(tabModeLabPK, 3) + jumlahTerpilih(tabModeDetailLabPK, 6) + jumlahTerpilih(tabModeLabPA, 3) +
            jumlahTerpilih(tabModeLabMB, 3) + jumlahTerpilih(tabModeDetailLabMB, 6) +
            jumlahTerpilih(tabModeTindakanDr, 6) + jumlahTerpilih(tabModeTindakanDrPr, 6) + jumlahTerpilih(tabModeTindakanPr, 6) +
            jumlahBiaya(tabModeTambahanBiaya) - jumlahBiaya(tabModePotonganBiaya)
        ));
    }

    public void emptTeks() {
        Valid.autonomorSmc(noTemplate, "TPM", "template_paket_mcu_smc", "no_template", 5, "0");
        namaTemplate.setText("");
        kodeJenisBayar.setText("-");
        namaJenisBayar.setText("-");
        cariRadiologi.setText("");
        cariLabPK.setText("");
        cariDetailLabPK.setText("");
        cariLabPA.setText("");
        cariLabMB.setText("");
        cariDetailLabMB.setText("");
        cariTindakanDr.setText("");
        cariTindakanDrPr.setText("");
        cariTindakanPr.setText("");
        cariTambahanBiaya.setText("");
        cariPotonganBiaya.setText("");
        tampilRadiologi(new HashMap<>());
        tampilLabPK(new HashMap<>(), new HashSet<>());
        tampilLab(tabModeLabPA, "PA", "", new HashMap<>(), null);
        tampilLabMB(new HashMap<>(), new HashSet<>());
        tampilTindakan(tabModeTindakanDr, "total_byrdr", "", new HashMap<>());
        tampilTindakan(tabModeTindakanDrPr, "total_byrdrpr", "", new HashMap<>());
        tampilTindakan(tabModeTindakanPr, "total_byrpr", "", new HashMap<>());
        kosongkanBiaya(tabModeTambahanBiaya);
        kosongkanBiaya(tabModePotonganBiaya);
        noTemplate.requestFocus();
    }

    private void getData() {
        if (tbTemplate.getSelectedRow() != -1) {
            String no = tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString();
            noTemplate.setText(no);
            namaTemplate.setText(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 1).toString());
            kodeJenisBayar.setText(Sequel.cariIsiSmc("select template_paket_mcu_smc.kd_pj from template_paket_mcu_smc where template_paket_mcu_smc.no_template = ?", no));
            namaJenisBayar.setText(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 2).toString());
            cariRadiologi.setText("");
            cariLabPK.setText("");
            cariDetailLabPK.setText("");
            cariLabPA.setText("");
            cariLabMB.setText("");
            cariDetailLabMB.setText("");
            cariTindakanDr.setText("");
            cariTindakanDrPr.setText("");
            cariTindakanPr.setText("");

            Map<String, String[]> lab = pilihanTersimpan(
                "select template_paket_mcu_smc_permintaan_lab.kd_jenis_prw from template_paket_mcu_smc_permintaan_lab where template_paket_mcu_smc_permintaan_lab.no_template = ?", no
            );
            Set<String> detailLab = pilihanTersimpan(
                "select template_paket_mcu_smc_detail_permintaan_lab.id_template from template_paket_mcu_smc_detail_permintaan_lab where " +
                "template_paket_mcu_smc_detail_permintaan_lab.no_template = ?", no
            ).keySet();

            tampilRadiologi(pilihanTersimpan(
                "select template_paket_mcu_smc_permintaan_radiologi.kd_jenis_prw from template_paket_mcu_smc_permintaan_radiologi where " +
                "template_paket_mcu_smc_permintaan_radiologi.no_template = ?", no
            ));
            tampilLabPK(lab, detailLab);
            tampilLab(tabModeLabPA, "PA", "", lab, null);
            tampilLabMB(lab, detailLab);
            tampilTindakan(tabModeTindakanDr, "total_byrdr", "", pilihanTersimpan(
                "select template_paket_mcu_smc_tindakan_dr.kd_jenis_prw, ifnull(template_paket_mcu_smc_tindakan_dr.kd_dokter, ''), ifnull(dokter.nm_dokter, '') from " +
                "template_paket_mcu_smc_tindakan_dr left join dokter on template_paket_mcu_smc_tindakan_dr.kd_dokter = dokter.kd_dokter where " +
                "template_paket_mcu_smc_tindakan_dr.no_template = ?", no
            ));
            tampilTindakan(tabModeTindakanDrPr, "total_byrdrpr", "", pilihanTersimpan(
                "select template_paket_mcu_smc_tindakan_drpr.kd_jenis_prw, ifnull(template_paket_mcu_smc_tindakan_drpr.kd_dokter, ''), ifnull(dokter.nm_dokter, '') from " +
                "template_paket_mcu_smc_tindakan_drpr left join dokter on template_paket_mcu_smc_tindakan_drpr.kd_dokter = dokter.kd_dokter where " +
                "template_paket_mcu_smc_tindakan_drpr.no_template = ?", no
            ));
            tampilTindakan(tabModeTindakanPr, "total_byrpr", "", pilihanTersimpan(
                "select template_paket_mcu_smc_tindakan_pr.kd_jenis_prw, ifnull(template_paket_mcu_smc_tindakan_pr.kd_dokter, ''), ifnull(dokter.nm_dokter, '') from " +
                "template_paket_mcu_smc_tindakan_pr left join dokter on template_paket_mcu_smc_tindakan_pr.kd_dokter = dokter.kd_dokter where " +
                "template_paket_mcu_smc_tindakan_pr.no_template = ?", no
            ));
            tampilBiaya(tabModeTambahanBiaya,
                "select template_paket_mcu_smc_tambahan_biaya.nama_biaya, template_paket_mcu_smc_tambahan_biaya.besar_biaya from template_paket_mcu_smc_tambahan_biaya " +
                "where template_paket_mcu_smc_tambahan_biaya.no_template = ? order by template_paket_mcu_smc_tambahan_biaya.nama_biaya", no
            );
            tampilBiaya(tabModePotonganBiaya,
                "select template_paket_mcu_smc_pengurangan_biaya.nama_pengurangan, template_paket_mcu_smc_pengurangan_biaya.besar_pengurangan from " +
                "template_paket_mcu_smc_pengurangan_biaya where template_paket_mcu_smc_pengurangan_biaya.no_template = ? order by " +
                "template_paket_mcu_smc_pengurangan_biaya.nama_pengurangan", no
            );
        }
    }

    public JTable getTable() {
        return tbTemplate;
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getmaster_template_paket_mcu_smc());
        BtnEdit.setEnabled(akses.getmaster_template_paket_mcu_smc());
        BtnHapus.setEnabled(akses.getmaster_template_paket_mcu_smc());
    }

    public void setTampil() {
        tampil();
    }

    private void isDetail() {
        if (ChkAccor.isSelected()) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(internalFrame3.getWidth() - 200, HEIGHT));
            FormDetail.setVisible(true);
            ChkAccor.setVisible(true);
        } else {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            FormDetail.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }

    private boolean cekMasukan() {
        if (noTemplate.getText().isBlank()) {
            Valid.textKosong(noTemplate, "No. Template");
            return false;
        }

        if (namaTemplate.getText().isBlank()) {
            Valid.textKosong(namaTemplate, "Nama Template");
            return false;
        }

        return cekBiaya(tabModeTambahanBiaya, "tambahan biaya") && cekBiaya(tabModePotonganBiaya, "potongan biaya");
    }

    private boolean cekBiaya(DefaultTableModel model, String jenis) {
        Set<String> nama = new HashSet<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String isi = model.getValueAt(i, 0).toString().trim();
            if (isi.isEmpty()) {
                continue;
            }

            if (isi.length() > 60) {
                JOptionPane.showMessageDialog(null, "Nama " + jenis + " \"" + isi + "\" melebihi 60 karakter...!!!");
                return false;
            }

            if (!nama.add(isi.toLowerCase())) {
                JOptionPane.showMessageDialog(null, "Nama " + jenis + " \"" + isi + "\" dimasukkan lebih dari sekali...!!!");
                return false;
            }
        }

        return true;
    }

    private boolean simpanDetail(String noTemplate) {
        for (String tabel : new String[] {
            "template_paket_mcu_smc_detail_permintaan_lab", "template_paket_mcu_smc_permintaan_lab", "template_paket_mcu_smc_permintaan_radiologi",
            "template_paket_mcu_smc_tindakan_dr", "template_paket_mcu_smc_tindakan_drpr", "template_paket_mcu_smc_tindakan_pr",
            "template_paket_mcu_smc_tambahan_biaya", "template_paket_mcu_smc_pengurangan_biaya"
        }) {
            if (Sequel.cariExistsSmc("select * from " + tabel + " where " + tabel + ".no_template = ?", noTemplate) && !Sequel.menghapustfSmc(tabel, "no_template = ?", noTemplate)) {
                return false;
            }
        }

        for (int i = 0; i < tabModeRadiologi.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tabModeRadiologi.getValueAt(i, 0)) && !Sequel.menyimpantfSmc("template_paket_mcu_smc_permintaan_radiologi", "no_template, kd_jenis_prw",
                noTemplate, tabModeRadiologi.getValueAt(i, 1).toString()
            )) {
                return false;
            }
        }

        for (DefaultTableModel model : new DefaultTableModel[] {tabModeLabPK, tabModeLabPA, tabModeLabMB}) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0)) && !Sequel.menyimpantfSmc("template_paket_mcu_smc_permintaan_lab", "no_template, kd_jenis_prw",
                    noTemplate, model.getValueAt(i, 1).toString()
                )) {
                    return false;
                }
            }
        }

        for (DefaultTableModel model : new DefaultTableModel[] {tabModeDetailLabPK, tabModeDetailLabMB}) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0)) && !"".equals(model.getValueAt(i, 4)) && !Sequel.menyimpantfSmc(
                    "template_paket_mcu_smc_detail_permintaan_lab", "no_template, kd_jenis_prw, id_template", noTemplate, model.getValueAt(i, 5).toString(),
                    model.getValueAt(i, 4).toString()
                )) {
                    return false;
                }
            }
        }

        return simpanTindakan(tabModeTindakanDr, "template_paket_mcu_smc_tindakan_dr", noTemplate) &&
            simpanTindakan(tabModeTindakanDrPr, "template_paket_mcu_smc_tindakan_drpr", noTemplate) &&
            simpanTindakan(tabModeTindakanPr, "template_paket_mcu_smc_tindakan_pr", noTemplate) &&
            simpanBiaya(tabModeTambahanBiaya, "template_paket_mcu_smc_tambahan_biaya", "no_template, nama_biaya, besar_biaya", noTemplate) &&
            simpanBiaya(tabModePotonganBiaya, "template_paket_mcu_smc_pengurangan_biaya", "no_template, nama_pengurangan, besar_pengurangan", noTemplate);
    }

    private boolean simpanTindakan(DefaultTableModel model, String tabel, String noTemplate) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 0)) && !Sequel.menyimpantfSmc(tabel, "no_template, kd_jenis_prw, kd_dokter", noTemplate,
                model.getValueAt(i, 1).toString(), model.getValueAt(i, 4).toString().isBlank() ? null : model.getValueAt(i, 4).toString()
            )) {
                return false;
            }
        }

        return true;
    }

    private boolean simpanBiaya(DefaultTableModel model, String tabel, String kolom, String noTemplate) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (!model.getValueAt(i, 0).toString().isBlank() && !Sequel.menyimpantfSmc(tabel, kolom, noTemplate, model.getValueAt(i, 0).toString().trim(),
                Valid.setAngkaSmc(angka(model.getValueAt(i, 1)), 2)
            )) {
                return false;
            }
        }

        return true;
    }
}
