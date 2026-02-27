/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.batasInput;
import inventory.DlgPiutang;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import widget.Button;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSKirimResepObatSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabModeObat, tabModeRacikan, tabModeDetailRacikanObat;
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final String URLAPIAPOTEKBPJS = koneksiDB.URLAPIAPOTEKBPJS();
    private final ApiApotekBPJS api = new ApiApotekBPJS();
    private final ApotekBPJSDaftarPelayananObat2SMC pelayananobat = new ApotekBPJSDaftarPelayananObat2SMC(null, false);
    private final ObjectMapper mapper = new ObjectMapper();
    private String nosjp = "", utc = "", URL = "";
    private HttpHeaders headers;
    private HttpEntity entity;
    private JsonNode root, metadata, response;
    private boolean sukses = true, baruDibuat = true;

    public ApotekBPJSKirimResepObatSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabModeObat = new DefaultTableModel(null, new String[] {
            "Jml.", "Kode Barang", "Nama Barang", "Restriksi", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kode Obat BPJS", "Nama Obat BPJS"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0 ||
                    colIndex == 4 ||
                    colIndex == 5 ||
                    colIndex == 6;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        return Double.class;
                    default:
                        return String.class;
                }
            }
        };

        tbObat.setModel(tabModeObat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        for (int i = 0; i < tabModeObat.getColumnCount(); i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(40);
            } else if (i == 1) {
                column.setPreferredWidth(75);
            } else if (i == 2) {
                column.setPreferredWidth(200);
            } else if (i == 3) {
                column.setPreferredWidth(200);
            } else if (i == 4) {
                column.setPreferredWidth(50);
            } else if (i == 5) {
                column.setPreferredWidth(50);
            } else if (i == 6) {
                column.setPreferredWidth(80);
            } else if (i == 7) {
                column.setPreferredWidth(80);
            } else if (i == 8) {
                column.setPreferredWidth(80);
            } else if (i == 9) {
                column.setPreferredWidth(90);
            } else if (i == 10) {
                column.setPreferredWidth(200);
            }
        }

        ListSelectionListener lObat = e -> {
            if (e.getValueIsAdjusting()) return;

            int col = tbObat.getColumnModel().getSelectionModel().getLeadSelectionIndex();

            if (col == 3) {
                LabelCatatan.setText("<html><body width=\"250px\">" + tbObat.getValueAt(tbObat.getSelectionModel().getLeadSelectionIndex(), col).toString() + "</body></html>");
                DlgRestriksi.setLocationRelativeTo(internalFrame1);
                DlgRestriksi.setVisible(true);
            }
        };

        tbObat.getSelectionModel().addListSelectionListener(lObat);
        tbObat.getColumnModel().getSelectionModel().addListSelectionListener(lObat);

        tabModeRacikan = new DefaultTableModel(null, new String[] {
            "No", "Nama Racikan", "Kode Racik", "Metode Racik", "Jml. Racik", "Aturan Pakai", "Keterangan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Double.class;
                }
                return String.class;
            }
        };

        tbRacikan.setModel(tabModeRacikan);
        tbRacikan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRacikan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRacikan.setDefaultRenderer(Object.class, new WarnaTable());

        for (int i = 0; i < tabModeRacikan.getColumnCount(); i++) {
            TableColumn column = tbRacikan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(25);
            } else if (i == 1) {
                column.setPreferredWidth(250);
            } else if (i == 2) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 3) {
                column.setPreferredWidth(100);
            } else if (i == 4) {
                column.setPreferredWidth(60);
            } else if (i == 5) {
                column.setPreferredWidth(200);
            } else if (i == 6) {
                column.setPreferredWidth(250);
            }
        }

        tabModeDetailRacikanObat = new DefaultTableModel(null, new Object[] {
            "No", "Jml.", "Kode Barang", "Nama Barang", "Restriksi", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kandungan", "Kode Obat BPJS", "Nama Obat BPJS"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 1 || colIndex == 5 || colIndex == 6 || colIndex == 7;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 1:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        return Double.class;
                    default:
                        return String.class;
                }
            }
        };

        tbDetailRacikanObat.setModel(tabModeDetailRacikanObat);
        tbDetailRacikanObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailRacikanObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDetailRacikanObat.setDefaultRenderer(Object.class, new WarnaTable());

        for (int i = 0; i < tabModeDetailRacikanObat.getColumnCount(); i++) {
            TableColumn column = tbDetailRacikanObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(25);
            } else if (i == 1) {
                column.setPreferredWidth(40);
            } else if (i == 2) {
                column.setPreferredWidth(100);
            } else if (i == 3) {
                column.setPreferredWidth(200);
            } else if (i == 4) {
                column.setPreferredWidth(200);
            } else if (i == 5) {
                column.setPreferredWidth(50);
            } else if (i == 6) {
                column.setPreferredWidth(50);
            } else if (i == 7) {
                column.setPreferredWidth(80);
            } else if (i == 8) {
                column.setPreferredWidth(80);
            } else if (i == 9) {
                column.setPreferredWidth(80);
            } else if (i == 10) {
                column.setPreferredWidth(100);
            } else if (i == 11) {
                column.setPreferredWidth(90);
            } else if (i == 12) {
                column.setPreferredWidth(200);
            }
        }

        ListSelectionListener lObatRacikan = e -> {
            if (e.getValueIsAdjusting()) return;

            int col = tbDetailRacikanObat.getColumnModel().getSelectionModel().getLeadSelectionIndex();

            if (col == 4) {
                LabelCatatan.setText("<html><body width=\"250px\">" + tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getSelectionModel().getLeadSelectionIndex(), col).toString() + "</body></html>");
                DlgRestriksi.setLocationRelativeTo(internalFrame1);
                DlgRestriksi.setVisible(true);
            }
        };

        tbDetailRacikanObat.getSelectionModel().addListSelectionListener(lObatRacikan);
        tbDetailRacikanObat.getColumnModel().getSelectionModel().addListSelectionListener(lObatRacikan);

        NoResep.setDocument(new batasInput(5).getKata(NoResep));

        InputMap input = DlgRestriksi.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap action = DlgRestriksi.getRootPane().getActionMap();

        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        action.put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DlgRestriksi.dispose();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        DlgRestriksi = new javax.swing.JDialog();
        internalFrame6 = new widget.InternalFrame();
        LabelCatatan = new widget.Label();
        internalFrame1 = new widget.InternalFrame();
        panelisi3 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnSimpanResepKosong = new widget.Button();
        BtnInsertObat = new widget.Button();
        BtnInsertObatRacikan = new widget.Button();
        BtnHapus = new widget.Button();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();
        FormInput = new widget.PanelBiasa();
        jLabel8 = new widget.Label();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TNoRw = new widget.TextBox();
        TglResep = new widget.Tanggal();
        jLabel20 = new widget.Label();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        jLabel13 = new widget.Label();
        jLabel15 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel14 = new widget.Label();
        NoResep = new widget.TextBox();
        jLabel16 = new widget.Label();
        jLabel17 = new widget.Label();
        jLabel4 = new widget.Label();
        NoKartu = new widget.TextBox();
        NoSEP = new widget.TextBox();
        jLabel18 = new widget.Label();
        Lahir = new widget.TextBox();
        jLabel19 = new widget.Label();
        jLabel21 = new widget.Label();
        Iterasi = new widget.ComboBox();
        JenisObat = new widget.ComboBox();
        TglPelayanan = new widget.Tanggal();
        JadikanPiutang = new widget.CekBox();
        jLabel22 = new widget.Label();
        LTotal = new widget.Label();
        TabRawat = new widget.TabPane();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        Scroll1 = new widget.ScrollPane();
        tbRacikan = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbDetailRacikanObat = new widget.Table();

        DlgRestriksi.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        DlgRestriksi.setMinimumSize(new java.awt.Dimension(400, 300));
        DlgRestriksi.setName("DlgRestriksi"); // NOI18N
        DlgRestriksi.setUndecorated(true);
        DlgRestriksi.setResizable(false);
        DlgRestriksi.setType(java.awt.Window.Type.POPUP);

        internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), "Restriksi Obat", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 255, 255))); // NOI18N
        internalFrame6.setName("internalFrame6"); // NOI18N
        internalFrame6.setWarnaAtas(new java.awt.Color(100, 100, 10));
        internalFrame6.setWarnaBawah(new java.awt.Color(100, 100, 10));
        internalFrame6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                internalFrame6MouseClicked(evt);
            }
        });
        internalFrame6.setLayout(new java.awt.BorderLayout(0, 10));

        LabelCatatan.setForeground(new java.awt.Color(255, 255, 255));
        LabelCatatan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelCatatan.setText("Catatan");
        LabelCatatan.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        LabelCatatan.setName("LabelCatatan"); // NOI18N
        LabelCatatan.setPreferredSize(null);
        LabelCatatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LabelCatatanMouseClicked(evt);
            }
        });
        internalFrame6.add(LabelCatatan, java.awt.BorderLayout.CENTER);

        DlgRestriksi.getContentPane().add(internalFrame6, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Obat Apotek BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
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
        panelisi3.add(BtnSimpan);

        BtnSimpanResepKosong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanResepKosong.setText("Simpan Resep Kosong");
        BtnSimpanResepKosong.setToolTipText("Alt+S");
        BtnSimpanResepKosong.setName("BtnSimpanResepKosong"); // NOI18N
        BtnSimpanResepKosong.setPreferredSize(new java.awt.Dimension(180, 30));
        BtnSimpanResepKosong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanResepKosongActionPerformed(evt);
            }
        });
        BtnSimpanResepKosong.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanResepKosongKeyPressed(evt);
            }
        });
        panelisi3.add(BtnSimpanResepKosong);

        BtnInsertObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnInsertObat.setText("Masukkan Obat Umum");
        BtnInsertObat.setToolTipText("Alt+S");
        BtnInsertObat.setName("BtnInsertObat"); // NOI18N
        BtnInsertObat.setPreferredSize(new java.awt.Dimension(180, 30));
        BtnInsertObat.setRolloverEnabled(false);
        BtnInsertObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnInsertObatActionPerformed(evt);
            }
        });
        BtnInsertObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnInsertObatKeyPressed(evt);
            }
        });
        panelisi3.add(BtnInsertObat);

        BtnInsertObatRacikan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnInsertObatRacikan.setText("Masukkan Obat Racikan");
        BtnInsertObatRacikan.setToolTipText("Alt+S");
        BtnInsertObatRacikan.setName("BtnInsertObatRacikan"); // NOI18N
        BtnInsertObatRacikan.setRolloverEnabled(false);
        BtnInsertObatRacikan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnInsertObatRacikanActionPerformed(evt);
            }
        });
        BtnInsertObatRacikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnInsertObatRacikanKeyPressed(evt);
            }
        });
        panelisi3.add(BtnInsertObatRacikan);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
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
        panelisi3.add(BtnHapus);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi3.add(BtnCari);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+5");
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
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        FormInput.setBackground(new java.awt.Color(215, 225, 215));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 195));
        FormInput.setLayout(null);

        jLabel8.setText("Tgl. Pelayanan :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel8);
        jLabel8.setBounds(212, 130, 85, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 74, 23);

        jLabel11.setText("No.RM :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel11);
        jLabel11.setBounds(205, 10, 45, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(207, 23));
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(427, 10, 237, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.setPreferredSize(new java.awt.Dimension(207, 23));
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(254, 10, 90, 23);

        TNoRw.setEditable(false);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.setPreferredSize(new java.awt.Dimension(207, 23));
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(78, 10, 123, 23);

        TglResep.setEditable(false);
        TglResep.setForeground(new java.awt.Color(50, 70, 50));
        TglResep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-12-2025 09:51:29" }));
        TglResep.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglResep.setName("TglResep"); // NOI18N
        TglResep.setOpaque(false);
        TglResep.setPreferredSize(new java.awt.Dimension(95, 23));
        TglResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglResepKeyPressed(evt);
            }
        });
        FormInput.add(TglResep);
        TglResep.setBounds(78, 130, 130, 23);

        jLabel20.setText("Tgl. Resep :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 130, 74, 23);

        NmPoli.setEditable(false);
        NmPoli.setHighlighter(null);
        NmPoli.setName("NmPoli"); // NOI18N
        NmPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmPoliKeyPressed(evt);
            }
        });
        FormInput.add(NmPoli);
        NmPoli.setBounds(481, 70, 183, 23);

        KdPoli.setEditable(false);
        KdPoli.setHighlighter(null);
        KdPoli.setName("KdPoli"); // NOI18N
        KdPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPoliKeyPressed(evt);
            }
        });
        FormInput.add(KdPoli);
        KdPoli.setBounds(402, 70, 75, 23);

        jLabel13.setText("Asal Poli :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(344, 70, 54, 23);

        jLabel15.setText("Dokter DPJP :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 70, 74, 23);

        KdDPJP.setEditable(false);
        KdDPJP.setHighlighter(null);
        KdDPJP.setName("KdDPJP"); // NOI18N
        KdDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDPJPKeyPressed(evt);
            }
        });
        FormInput.add(KdDPJP);
        KdDPJP.setBounds(78, 70, 75, 23);

        NmDPJP.setEditable(false);
        NmDPJP.setHighlighter(null);
        NmDPJP.setName("NmDPJP"); // NOI18N
        NmDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmDPJPKeyPressed(evt);
            }
        });
        FormInput.add(NmDPJP);
        NmDPJP.setBounds(157, 70, 183, 23);

        jLabel14.setText("No. Resep :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 100, 74, 23);

        NoResep.setHighlighter(null);
        NoResep.setName("NoResep"); // NOI18N
        NoResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoResepKeyPressed(evt);
            }
        });
        FormInput.add(NoResep);
        NoResep.setBounds(78, 100, 90, 23);

        jLabel16.setText("Iterasi :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(172, 100, 50, 23);

        jLabel17.setText("Jenis Obat :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(369, 100, 66, 23);

        jLabel4.setText("No.Kartu :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(172, 40, 60, 23);

        NoKartu.setEditable(false);
        NoKartu.setHighlighter(null);
        NoKartu.setName("NoKartu"); // NOI18N
        NoKartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoKartuKeyPressed(evt);
            }
        });
        FormInput.add(NoKartu);
        NoKartu.setBounds(236, 40, 130, 23);

        NoSEP.setEditable(false);
        NoSEP.setHighlighter(null);
        NoSEP.setName("NoSEP"); // NOI18N
        NoSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSEPKeyPressed(evt);
            }
        });
        FormInput.add(NoSEP);
        NoSEP.setBounds(424, 40, 240, 23);

        jLabel18.setText("No.SEP :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(370, 40, 50, 23);

        Lahir.setEditable(false);
        Lahir.setHighlighter(null);
        Lahir.setName("Lahir"); // NOI18N
        Lahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LahirKeyPressed(evt);
            }
        });
        FormInput.add(Lahir);
        Lahir.setBounds(78, 40, 90, 23);

        jLabel19.setText("Tgl.Lahir :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 40, 74, 23);

        jLabel21.setText("Nama Pasien :");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel21);
        jLabel21.setBounds(348, 10, 75, 23);

        Iterasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tanpa Iterasi", "1. Iterasi 1", "2. Iterasi 2" }));
        Iterasi.setName("Iterasi"); // NOI18N
        Iterasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IterasiKeyPressed(evt);
            }
        });
        FormInput.add(Iterasi);
        Iterasi.setBounds(225, 100, 140, 23);

        JenisObat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Obat PRB", "2. Obat Kronis Belum Stabil", "3. Obat Kemoterapi" }));
        JenisObat.setSelectedIndex(1);
        JenisObat.setName("JenisObat"); // NOI18N
        JenisObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisObatKeyPressed(evt);
            }
        });
        FormInput.add(JenisObat);
        JenisObat.setBounds(439, 100, 225, 23);

        TglPelayanan.setEditable(false);
        TglPelayanan.setForeground(new java.awt.Color(50, 70, 50));
        TglPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-12-2025 09:51:29" }));
        TglPelayanan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglPelayanan.setName("TglPelayanan"); // NOI18N
        TglPelayanan.setOpaque(false);
        TglPelayanan.setPreferredSize(new java.awt.Dimension(95, 23));
        TglPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglPelayananKeyPressed(evt);
            }
        });
        FormInput.add(TglPelayanan);
        TglPelayanan.setBounds(301, 130, 130, 23);

        JadikanPiutang.setText("Jadikan Piutang Obat");
        JadikanPiutang.setToolTipText("Apabila jadi piutang obat, maka hasil pengiriman resep ini akan diteruskan ke menu piutang obat. Digunakan untuk resep iterasi selanjutnya.");
        JadikanPiutang.setName("JadikanPiutang"); // NOI18N
        JadikanPiutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JadikanPiutangKeyPressed(evt);
            }
        });
        FormInput.add(JadikanPiutang);
        JadikanPiutang.setBounds(439, 130, 130, 23);

        jLabel22.setText("Total (Rp) :");
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel22);
        jLabel22.setBounds(0, 160, 74, 23);

        LTotal.setText("0");
        LTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LTotal.setName("LTotal"); // NOI18N
        LTotal.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(LTotal);
        LTotal.setBounds(78, 160, 120, 23);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TabRawatKeyPressed(evt);
            }
        });

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbObatPropertyChange(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        TabRawat.addTab("Umum", Scroll);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(300, 102));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);
        Scroll1.setPreferredSize(new java.awt.Dimension(454, 90));

        tbRacikan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRacikan.setName("tbRacikan"); // NOI18N
        tbRacikan.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbRacikanPropertyChange(evt);
            }
        });
        Scroll1.setViewportView(tbRacikan);

        jPanel3.add(Scroll1, java.awt.BorderLayout.PAGE_START);

        Scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbDetailRacikanObat.setAutoCreateRowSorter(true);
        tbDetailRacikanObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbDetailRacikanObat.setName("tbDetailRacikanObat"); // NOI18N
        tbDetailRacikanObat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDetailRacikanObatPropertyChange(evt);
            }
        });
        Scroll2.setViewportView(tbDetailRacikanObat);

        jPanel3.add(Scroll2, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Racikan", jPanel3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        DlgRestriksi.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (NoSEP.getText().trim().equals("")) {
            Valid.textKosong(NoSEP, "Nomor Sep");
        } else if (KdDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "Dokter");
        } else if (KdPoli.getText().trim().equals("")) {
            Valid.textKosong(KdPoli, "Poliklinik");
        } else if (NoResep.getText().trim().equals("")) {
            Valid.textKosong(NoResep, "Nomor Resep");
        } else if (Lahir.getText().trim().equals("")) {
            Valid.textKosong(Lahir, "Lahir");
        } else {
            int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                sukses = true;
                simpanResep();
                if (!nosjp.isBlank()) {
                    if (tbObat.getRowCount() > 0) {
                        insertObat();
                    }
                    if (tbDetailRacikanObat.getRowCount() > 0) {
                        insertObatRacikan();
                    }
                }
                if (sukses) {
                    JOptionPane.showMessageDialog(null, "Simpan resep berhasil..!!");
                    if (JadikanPiutang.isSelected()) {
                        DlgPiutang piutang = new DlgPiutang(null, false);
                        piutang.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                        piutang.setLocationRelativeTo(internalFrame1);
                        piutang.isCek();
                        piutang.tampilResepApotekBPJSSmc(nosjp, NoResep.getText());
                        piutang.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        pelayananobat.setCari(NoSEP.getText());
        pelayananobat.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        pelayananobat.setLocationRelativeTo(internalFrame1);
        pelayananobat.setVisible(true);
        pelayananobat.tampil();
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            tabModeObat.removeRow(tbObat.getSelectedRow());
        }

        if (tbDetailRacikanObat.getSelectedRow() != -1) {
            tabModeDetailRacikanObat.removeRow(tbDetailRacikanObat.getSelectedRow());
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnSimpanResepKosongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanResepKosongActionPerformed
        int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            sukses = true;
            simpanResep();
        }
    }//GEN-LAST:event_BtnSimpanResepKosongActionPerformed

    private void BtnInsertObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnInsertObatActionPerformed
        if (tbObat.getRowCount() > 0) {
            nosjp = Sequel.cariIsiSmc("select bridging_apotek_bpjs.no_sjp from bridging_apotek_bpjs where bridging_apotek_bpjs.no_sep = ? and bridging_apotek_bpjs.no_resep = ?", NoSEP.getText(), NoResep.getText());
            if (!nosjp.isBlank()) {
                int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    if (cekStatusKlaim()) {
                        JOptionPane.showMessageDialog(null, "No. SEP Apotek sudah dilakukan verifikasi klaim..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    } else {
                        sukses = true;
                        insertObat();
                        if (sukses) {
                            JOptionPane.showMessageDialog(null, "Simpan obat umum berhasil..!!");
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No. SEP Apotek tidak ditemukan..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_BtnInsertObatActionPerformed

    private void BtnInsertObatRacikanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnInsertObatRacikanActionPerformed
        if (tbDetailRacikanObat.getRowCount() > 0) {
            nosjp = Sequel.cariIsiSmc("select bridging_apotek_bpjs.no_sjp from bridging_apotek_bpjs where bridging_apotek_bpjs.no_sep = ? and bridging_apotek_bpjs.no_resep = ?", NoSEP.getText(), NoResep.getText());
            if (!nosjp.isBlank()) {
                int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    if (cekStatusKlaim()) {
                        JOptionPane.showMessageDialog(null, "No. SEP Apotek sudah dilakukan verifikasi klaim..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    } else {
                        sukses = true;
                        insertObatRacikan();
                        if (sukses) {
                            JOptionPane.showMessageDialog(null, "Simpan obat umum berhasil..!!");
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No. SEP Apotek tidak ditemukan..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_BtnInsertObatRacikanActionPerformed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        Valid.pindahSmc(evt, TNoRM, BtnKeluar);
    }//GEN-LAST:event_TNoRwKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        Valid.pindahSmc(evt, TNoRw, TPasien);
    }//GEN-LAST:event_TNoRMKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindahSmc(evt, TNoRM, Lahir);
    }//GEN-LAST:event_TPasienKeyPressed

    private void LahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LahirKeyPressed
        Valid.pindahSmc(evt, TPasien, NoKartu);
    }//GEN-LAST:event_LahirKeyPressed

    private void NoKartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoKartuKeyPressed
        Valid.pindahSmc(evt, Lahir, NoSEP);
    }//GEN-LAST:event_NoKartuKeyPressed

    private void NoSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSEPKeyPressed
        Valid.pindahSmc(evt, NoKartu, KdDPJP);
    }//GEN-LAST:event_NoSEPKeyPressed

    private void KdDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDPJPKeyPressed
        Valid.pindahSmc(evt, NoSEP, NmDPJP);
    }//GEN-LAST:event_KdDPJPKeyPressed

    private void NmDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmDPJPKeyPressed
        Valid.pindahSmc(evt, KdDPJP, KdPoli);
    }//GEN-LAST:event_NmDPJPKeyPressed

    private void KdPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPoliKeyPressed
        Valid.pindahSmc(evt, NmDPJP, NmPoli);
    }//GEN-LAST:event_KdPoliKeyPressed

    private void NmPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmPoliKeyPressed
        Valid.pindahSmc(evt, KdPoli, NoResep);
    }//GEN-LAST:event_NmPoliKeyPressed

    private void NoResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoResepKeyPressed
        Valid.pindahSmc(evt, NmPoli, Iterasi);
    }//GEN-LAST:event_NoResepKeyPressed

    private void IterasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IterasiKeyPressed
        Valid.pindahSmc(evt, NoResep, JenisObat);
    }//GEN-LAST:event_IterasiKeyPressed

    private void JenisObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JenisObatKeyPressed
        Valid.pindahSmc(evt, Iterasi, TglResep);
    }//GEN-LAST:event_JenisObatKeyPressed

    private void TglResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglResepKeyPressed
        Valid.pindahSmc(evt, JenisObat, TglPelayanan);
    }//GEN-LAST:event_TglResepKeyPressed

    private void TabRawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TabRawatKeyPressed
        Valid.pindahSmc(evt, JadikanPiutang, BtnSimpan);
    }//GEN-LAST:event_TabRawatKeyPressed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        Valid.pindahSmc(evt, TabRawat, BtnSimpanResepKosong);
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnSimpanResepKosongKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanResepKosongKeyPressed
        Valid.pindahSmc(evt, BtnSimpan, BtnInsertObat);
    }//GEN-LAST:event_BtnSimpanResepKosongKeyPressed

    private void BtnInsertObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnInsertObatKeyPressed
        Valid.pindahSmc(evt, BtnSimpanResepKosong, BtnInsertObatRacikan);
    }//GEN-LAST:event_BtnInsertObatKeyPressed

    private void BtnInsertObatRacikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnInsertObatRacikanKeyPressed
        Valid.pindahSmc(evt, BtnInsertObat, BtnHapus);
    }//GEN-LAST:event_BtnInsertObatRacikanKeyPressed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        Valid.pindahSmc(evt, BtnInsertObatRacikan, BtnCari);
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        Valid.pindahSmc(evt, BtnHapus, BtnKeluar);
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        Valid.pindahSmc(evt, BtnCari, TNoRw);
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void TglPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglPelayananKeyPressed
        Valid.pindahSmc(evt, TglResep, JadikanPiutang);
    }//GEN-LAST:event_TglPelayananKeyPressed

    private void JadikanPiutangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JadikanPiutangKeyPressed
        Valid.pindahSmc(evt, TglPelayanan, TabRawat);
    }//GEN-LAST:event_JadikanPiutangKeyPressed

    private void tbObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbObatPropertyChange
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            if (tbObat.getEditingColumn() == 0) {
                try {
                    tbObat.setValueAt(
                        Double.parseDouble(tbObat.getValueAt(tbObat.getEditingRow(), 7).toString()) *
                        Double.parseDouble(tbObat.getValueAt(tbObat.getEditingRow(), 0).toString()),
                        tbObat.getEditingRow(), 8
                    );
                    tbObat.setValueAt(
                        Double.parseDouble(tbObat.getValueAt(tbObat.getEditingRow(), 0).toString()) /
                        Double.parseDouble(tbObat.getValueAt(tbObat.getEditingRow(), 6).toString()) /
                        Double.parseDouble(tbObat.getValueAt(tbObat.getEditingRow(), 5).toString()),
                        tbObat.getEditingRow(), 4
                    );
                    updateTotal();
                } catch (Exception e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatPropertyChange

    private void tbRacikanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbRacikanPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbRacikanPropertyChange

    private void tbDetailRacikanObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDetailRacikanObatPropertyChange
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            if (tbDetailRacikanObat.getEditingColumn() == 1) {
                try {
                    tbDetailRacikanObat.setValueAt(
                        Double.parseDouble(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 8).toString()) *
                        Double.parseDouble(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 1).toString()),
                        tbDetailRacikanObat.getEditingRow(), 9
                    );
                    tbDetailRacikanObat.setValueAt(
                        Double.parseDouble(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 1).toString()) /
                        Double.parseDouble(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 7).toString()) /
                        Double.parseDouble(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 6).toString()),
                        tbDetailRacikanObat.getEditingRow(), 5
                    );
                    updateTotal();
                } catch (Exception e) {
                }
            }
        }
    }//GEN-LAST:event_tbDetailRacikanObatPropertyChange

    private void LabelCatatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LabelCatatanMouseClicked
        DlgRestriksi.dispose();
    }//GEN-LAST:event_LabelCatatanMouseClicked

    private void internalFrame6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_internalFrame6MouseClicked
        DlgRestriksi.dispose();
    }//GEN-LAST:event_internalFrame6MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnInsertObat;
    private widget.Button BtnInsertObatRacikan;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpanResepKosong;
    private javax.swing.JDialog DlgRestriksi;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Iterasi;
    private widget.CekBox JadikanPiutang;
    private widget.ComboBox JenisObat;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdPoli;
    private widget.Label LTotal;
    private widget.Label LabelCatatan;
    private widget.TextBox Lahir;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmPoli;
    private widget.TextBox NoKartu;
    private widget.TextBox NoResep;
    private widget.TextBox NoSEP;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TabPane TabRawat;
    private widget.Tanggal TglPelayanan;
    private widget.Tanggal TglResep;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame6;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel4;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelisi3;
    private widget.Table tbDetailRacikanObat;
    private widget.Table tbObat;
    private widget.Table tbRacikan;
    // End of variables declaration//GEN-END:variables

    public void tampilSmc(String noresep) {
        try {
            JadikanPiutang.setSelected(false);
            Valid.tabelKosong(tabModeObat);
            Valid.tabelKosong(tabModeRacikan);
            Valid.tabelKosong(tabModeDetailRacikanObat);
            double total = 0;
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select rx.no_rawat, rx.tgl_perawatan, rx.jam, concat(rx.tgl_perawatan, ' ', rx.jam) as tgl_pelayanan, concat(rx.tgl_peresepan, ' ', rx.jam_peresepan) as tgl_peresepan, r.no_rkm_medis, " +
                "px.nm_pasien, px.tgl_lahir, s.no_kartu, s.no_sep, s.kddpjp, s.nmdpdjp, s.kdpolitujuan, s.nmpolitujuan from resep_obat rx join reg_periksa r on rx.no_rawat = r.no_rawat " +
                "join bridging_sep s on r.no_rawat = s.no_rawat and r.status_lanjut = (if(s.jnspelayanan = '1', 'Ranap', 'Ralan')) join pasien px on r.no_rkm_medis = px.no_rkm_medis " +
                "where rx.no_resep = ?"
            )) {
                ps.setString(1, noresep);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        TNoRw.setText(rs.getString("no_rawat"));
                        TNoRM.setText(rs.getString("no_rkm_medis"));
                        TPasien.setText(rs.getString("nm_pasien"));
                        Lahir.setText(rs.getString("tgl_lahir"));
                        NoKartu.setText(rs.getString("no_kartu"));
                        NoSEP.setText(rs.getString("no_sep"));
                        NoResep.setText(NoSEP.getText().substring(NoSEP.getText().length() - 5, NoSEP.getText().length()));
                        KdDPJP.setText(rs.getString("kddpjp"));
                        NmDPJP.setText(rs.getString("nmdpdjp"));
                        KdPoli.setText(rs.getString("kdpolitujuan"));
                        NmPoli.setText(rs.getString("nmpolitujuan"));
                        TglResep.setDate(rs.getTimestamp("tgl_peresepan"));
                        TglPelayanan.setDate(rs.getTimestamp("tgl_pelayanan"));
                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select m.kode_brng_apotek_bpjs, m.nama_brng_apotek_bpjs, m.harga, m.restriksi, dpo.jml, o.kode_brng, " +
                            "o.nama_brng from detail_pemberian_obat dpo join maping_obat_apotek_bpjs m on dpo.kode_brng = m.kode_brng " +
                            "join databarang o on dpo.kode_brng = o.kode_brng where dpo.no_rawat = ? and dpo.tgl_perawatan = ? " +
                            "and dpo.jam = ? and not exists(select * from detail_obat_racikan dor where " +
                            "dor.no_rawat = dpo.no_rawat and dor.tgl_perawatan = dpo.tgl_perawatan and " +
                            "dor.jam = dpo.jam and dor.kode_brng = dpo.kode_brng) order by o.kode_brng"
                        )) {
                            ps2.setString(1, rs.getString("no_rawat"));
                            ps2.setString(2, rs.getString("tgl_perawatan"));
                            ps2.setString(3, rs.getString("jam"));
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    total += (rs2.getDouble("harga") * rs2.getDouble("jml"));
                                    switch (rs2.getInt("jml")) {
                                        case 15:
                                            tabModeObat.addRow(new Object[] {
                                                rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getString("restriksi"),
                                                1.0d, 0.5d, 30.0d, rs2.getDouble("harga"), rs2.getDouble("harga") * rs2.getDouble("jml"),
                                                rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        case 45:
                                            tabModeObat.addRow(new Object[] {
                                                rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getString("restriksi"),
                                                1.0d, 1.5d, 30.0d, rs2.getDouble("harga"), rs2.getDouble("harga") * rs2.getDouble("jml"),
                                                rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        default:
                                            tabModeObat.addRow(new Object[] {
                                                rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getString("restriksi"),
                                                rs2.getDouble("jml") / 30, 1.0d, 30.0d, rs2.getDouble("harga"), rs2.getDouble("harga") * rs2.getDouble("jml"),
                                                rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                    }
                                }
                            }
                        }

                        // racikan
                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select o.no_racik, o.nama_racik, o.kd_racik, m.nm_racik as metode, o.jml_dr, " +
                            "o.aturan_pakai, o.keterangan from obat_racikan o join metode_racik m on " +
                            "o.kd_racik = m.kd_racik where o.tgl_perawatan = ? and o.jam = ? and o.no_rawat = ? " +
                            "order by o.no_racik"
                        )) {
                            ps2.setString(1, rs.getString("tgl_perawatan"));
                            ps2.setString(2, rs.getString("jam"));
                            ps2.setString(3, rs.getString("no_rawat"));
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    tabModeRacikan.addRow(new Object[] {
                                        rs2.getString("no_racik"), rs2.getString("nama_racik"), rs2.getString("kd_racik"),
                                        rs2.getString("metode"), rs2.getDouble("jml_dr"), rs2.getString("aturan_pakai"),
                                        rs2.getString("keterangan")
                                    });
                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                        "select dpo.kode_brng, o.nama_brng, dpo.jml, ifnull(o.kapasitas, 1) as kapasitas, (select d.kandungan from resep_dokter_racikan_detail d where " +
                                        "d.no_resep = ? and d.no_racik = ? and d.kode_brng = dpo.kode_brng) as kandungan, m.kode_brng_apotek_bpjs, m.nama_brng_apotek_bpjs, m.harga, m.restriksi " +
                                        "from detail_pemberian_obat dpo join databarang o on dpo.kode_brng = o.kode_brng join maping_obat_apotek_bpjs m on dpo.kode_brng = m.kode_brng where " +
                                        "dpo.tgl_perawatan = ? and dpo.jam = ? and dpo.no_rawat = ? and exists(select * from detail_obat_racikan r where dpo.tgl_perawatan = r.tgl_perawatan " +
                                        "and dpo.jam = r.jam and dpo.no_rawat = r.no_rawat and dpo.kode_brng = r.kode_brng) order by o.kode_brng"
                                    )) {
                                        ps3.setString(1, noresep);
                                        ps3.setString(2, rs2.getString("no_racik"));
                                        ps3.setString(3, rs.getString("tgl_perawatan"));
                                        ps3.setString(4, rs.getString("jam"));
                                        ps3.setString(5, rs.getString("no_rawat"));
                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                            while (rs3.next()) {
                                                total += (rs3.getDouble("harga") * rs3.getDouble("jml"));
                                                switch (rs3.getInt("jml")) {
                                                    case 15:
                                                        tabModeDetailRacikanObat.addRow(new Object[] {
                                                            rs2.getString("no_racik"), rs3.getDouble("jml"), rs3.getString("kode_brng"), rs3.getString("nama_brng"), rs3.getString("restriksi"),
                                                            1.0d, 0.5d, 30.0d, rs3.getDouble("harga"), rs3.getDouble("jml") * rs3.getDouble("harga"), rs3.getString("kandungan"),
                                                            rs3.getString("kode_brng_apotek_bpjs"), rs3.getString("nama_brng_apotek_bpjs")
                                                        });
                                                        break;
                                                    case 45:
                                                        tabModeDetailRacikanObat.addRow(new Object[] {
                                                            rs2.getString("no_racik"), rs3.getDouble("jml"), rs3.getString("kode_brng"), rs3.getString("nama_brng"), rs3.getString("restriksi"),
                                                            1.0d, 1.5d, 30.0d, rs3.getDouble("harga"), rs3.getDouble("jml") * rs3.getDouble("harga"), rs3.getString("kandungan"),
                                                            rs3.getString("kode_brng_apotek_bpjs"), rs3.getString("nama_brng_apotek_bpjs")
                                                        });
                                                        break;
                                                    default:
                                                        tabModeDetailRacikanObat.addRow(new Object[] {
                                                            rs2.getString("no_racik"), rs3.getDouble("jml"), rs3.getString("kode_brng"), rs3.getString("nama_brng"), rs3.getString("restriksi"),
                                                            rs3.getDouble("jml") / 30, 1.0d, 30.0d, rs3.getDouble("harga"), rs3.getDouble("jml") * rs3.getDouble("harga"), rs3.getString("kandungan"),
                                                            rs3.getString("kode_brng_apotek_bpjs"), rs3.getString("nama_brng_apotek_bpjs")
                                                        });
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LTotal.setText(Valid.SetAngka(total));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampiCopyResepSmc(String nosjp, String noresep) {
        try {
            JadikanPiutang.setSelected(true);
            Valid.tabelKosong(tabModeObat);
            Valid.tabelKosong(tabModeRacikan);
            Valid.tabelKosong(tabModeDetailRacikanObat);
            double total = 0;
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select a.*, s.no_kartu, s.no_rawat, r.no_rkm_medis, px.nm_pasien, px.tgl_lahir from bridging_apotek_bpjs a join bridging_sep s on a.no_sep = s.no_sep " +
                "join reg_periksa r on s.no_rawat = r.no_rawat join pasien px on r.no_rkm_medis = px.no_rkm_medis where a.no_sjp = ? and a.no_resep = ?"
            )) {
                ps.setString(1, nosjp);
                ps.setString(2, noresep);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        TNoRw.setText(rs.getString("no_rawat"));
                        TNoRM.setText(rs.getString("no_rkm_medis"));
                        TPasien.setText(rs.getString("nm_pasien"));
                        Lahir.setText(rs.getString("tgl_lahir"));
                        NoKartu.setText(rs.getString("no_kartu"));
                        NoSEP.setText(rs.getString("no_sep"));
                        KdDPJP.setText(rs.getString("kodedpjp"));
                        NmDPJP.setText(rs.getString("nmdpjp"));
                        KdPoli.setText(rs.getString("kd_poli"));
                        NmPoli.setText(rs.getString("nm_poli"));
                        NoResep.setText(noresep);
                        switch (rs.getString("jenis_obat")) {
                            case "1":
                                JenisObat.setSelectedIndex(0);
                                break;
                            case "2":
                                JenisObat.setSelectedIndex(1);
                                break;
                            case "3":
                                JenisObat.setSelectedIndex(2);
                                break;
                        }
                        TglResep.setDate(new Date());
                        TglPelayanan.setDate(new Date());

                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select a.jumlah, m.kode_brng, o.nama_brng, a.signa1, a.signa2, a.jml_hari, a.kandungan, a.no_racik, " +
                            "a.kode_brng_apotek_bpjs, a.nama_brng_apotek_bpjs, m.harga, m.restriksi from bridging_apotek_bpjs_obat a " +
                            "left join maping_obat_apotek_bpjs m on a.kode_brng_apotek_bpjs = m.kode_brng_apotek_bpjs " +
                            "left join databarang o on m.kode_brng = o.kode_brng where a.no_sjp = ?"
                        )) {
                            ps2.setString(1, nosjp);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    total += rs2.getDouble("jumlah") * rs2.getDouble("harga");
                                    if (rs2.getString("no_racik") != null) {
                                        tabModeDetailRacikanObat.addRow(new Object[] {
                                            rs2.getString("no_racik"), rs2.getDouble("jumlah"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getString("restriksi"),
                                            rs2.getDouble("signa1"), rs2.getDouble("signa2"), rs2.getDouble("jml_hari"), rs2.getDouble("harga"), rs2.getDouble("jumlah") * rs2.getDouble("harga"),
                                            rs2.getString("kandungan"), rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                        });
                                    } else {
                                        tabModeObat.addRow(new Object[] {
                                            rs2.getDouble("jumlah"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getString("restriksi"), rs2.getDouble("signa1"),
                                            rs2.getDouble("signa2"), rs2.getDouble("jml_hari"), rs2.getDouble("harga"), rs2.getDouble("jumlah") * rs2.getDouble("harga"),
                                            rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                        });
                                    }
                                }
                            }
                        }

                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select r.no_racik, r.nama_racik, m.nm_racik, r.jml_dr, r.aturan_pakai, r.keterangan from " +
                            "bridging_apotek_bpjs_racikan r join metode_racik m on r.kd_racik = m.kd_racik where r.no_sjp = ? " +
                            "order by r.no_racik"
                        )) {
                            ps2.setString(1, nosjp);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    tabModeRacikan.addRow(new Object[] {
                                        rs2.getString("no_racik"), rs2.getString("nama_racik"), rs2.getString("nm_racik"),
                                        rs2.getDouble("jml_dr"), rs2.getString("aturan_pakai"), rs2.getString("keterangan")
                                    });
                                }
                            }
                        }
                    }
                }
            }
            LTotal.setText(Valid.SetAngka(total));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public JTable getTable() {
        return tbObat;
    }

    public Button getButton() {
        return BtnSimpan;
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getbpjs_kirim_obat_smc());
        BtnSimpanResepKosong.setEnabled(akses.getbpjs_kirim_obat_smc());
        BtnInsertObat.setEnabled(akses.getbpjs_kirim_obat_smc());
        BtnInsertObatRacikan.setEnabled(akses.getbpjs_kirim_obat_smc());
        BtnCari.setEnabled(akses.getbpjs_riwayat_pelayanan_obat_smc());
    }

    private void simpanResep() {
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            entity = new HttpEntity(headers);
            URL = URLAPIAPOTEKBPJS + "/sjpresep/v3/insert";
            System.out.println(URL);
            ObjectNode json = mapper.createObjectNode();
            json.put("TGLSJP", Sequel.cariIsiSmc("select now()"));
            json.put("REFASALSJP", NoSEP.getText());
            json.put("POLIRSP", KdPoli.getText());
            json.put("KDJNSOBAT", JenisObat.getSelectedItem().toString().substring(0, 1));
            json.put("NORESEP", NoResep.getText());
            json.put("IDUSERSJP", "RS_" + akses.getkode());
            json.put("TGLRSP", Valid.getTglSmc(TglResep) + " 00:00:00");
            json.put("TGLPELRSP", Valid.getTglSmc(TglPelayanan) + " 00:00:00");
            json.put("KdDokter", Integer.parseInt(KdDPJP.getText()));
            json.put("iterasi", Iterasi.getSelectedItem().toString().substring(0, 1));
            System.out.println(json.toString());
            entity = new HttpEntity(json.toString(), headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println("Simpan resep : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                System.out.println("Response : " + response);
                nosjp = response.path("noApotik").asText();
                if (Sequel.menyimpantfSmc("bridging_apotek_bpjs", null,
                    nosjp, NoSEP.getText(), NoResep.getText(), Valid.getTglJamSmc(TglResep),
                    Valid.getTglJamSmc(TglPelayanan), JenisObat.getSelectedItem().toString().substring(0, 1),
                    Iterasi.getSelectedItem().toString().substring(0, 1), KdPoli.getText(),
                    NmPoli.getText(), KdDPJP.getText(), NmDPJP.getText(), akses.getkode()
                )) {
                    JOptionPane.showMessageDialog(null, "Resep apotek " + nosjp + " berhasil disimpan..!!");
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan resep obat..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                sukses = false;
                JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            sukses = false;
            System.out.println(ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void insertObat() {
        if (sukses) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("x-timestamp", utc);
                headers.add("x-signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                entity = new HttpEntity(headers);
                URL = URLAPIAPOTEKBPJS + "/obatnonracikan/v3/insert";
                System.out.println(URL);
                ObjectNode json;
                for (int i = 0; i < tbObat.getRowCount(); i++) {
                    if (Valid.SetAngka(tbObat.getValueAt(i, 0).toString()) > 0) {
                        json = mapper.createObjectNode();
                        json.put("NOSJP", nosjp);
                        json.put("NORESEP", NoResep.getText());
                        json.put("KDOBT", tbObat.getValueAt(i, 9).toString());
                        json.put("NMOBAT", tbObat.getValueAt(i, 10).toString());
                        json.put("SIGNA1OBT", (Double) tbObat.getValueAt(i, 4));
                        json.put("SIGNA2OBT", (Double) tbObat.getValueAt(i, 5));
                        json.put("JHO", (Double) tbObat.getValueAt(i, 6));
                        json.put("JMLOBT", (Double) tbObat.getValueAt(i, 0));
                        json.put("CatKhsObt", "Non racikan");
                        System.out.println(mapper.writeValueAsString(json));
                        entity = new HttpEntity(mapper.writeValueAsString(json), headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("Insert obat " + tbObat.getValueAt(i, 9).toString() + " " + tbObat.getValueAt(i, 10).toString() + " : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                        if (metadata.path("code").asText().equals("200")) {
                            if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null, nosjp, tbObat.getValueAt(i, 9).toString(),
                                tbObat.getValueAt(i, 10).toString(), tbObat.getValueAt(i, 0).toString(), tbObat.getValueAt(i, 4).toString(),
                                tbObat.getValueAt(i, 5).toString(), tbObat.getValueAt(i, 6).toString(), tbObat.getValueAt(i, 7).toString(),
                                tbObat.getValueAt(i, 8).toString(), null, null
                            )) {
                                sukses = false;
                                JOptionPane.showMessageDialog(null, "Gagal menyimpan " + tbObat.getValueAt(i, 1).toString() + " - " + tbObat.getValueAt(i, 2).toString() + " ke resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Obat " + tbObat.getValueAt(i, 1).toString() + " - " + tbObat.getValueAt(i, 2).toString() + " diresepkan kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                sukses = false;
                System.out.println(ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                }
            }
        }
    }

    private void insertObatRacikan() {
        if (sukses) {
            try {
                for (int i = 0; i < tbRacikan.getRowCount(); i++) {
                    if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_racikan", null, nosjp, tbRacikan.getValueAt(i, 1).toString(),
                        tbRacikan.getValueAt(i, 2).toString(), tbRacikan.getValueAt(i, 4).toString(), tbRacikan.getValueAt(i, 5).toString(),
                        tbRacikan.getValueAt(i, 6).toString(), tbRacikan.getValueAt(i, 0).toString()
                    )) {
                        sukses = false;
                    }
                }

                if (sukses) {
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                    entity = new HttpEntity(headers);
                    URL = URLAPIAPOTEKBPJS + "/obatracikan/v3/insert";
                    System.out.println(URL);
                    for (int i = 0; i < tbDetailRacikanObat.getRowCount(); i++) {
                        if (Valid.SetAngka(tbDetailRacikanObat.getValueAt(i, 1).toString()) > 0) {
                            ObjectNode json = mapper.createObjectNode();
                            json.put("NOSJP", nosjp);
                            json.put("NORESEP", NoResep.getText());
                            json.put("JNSROBT", "R." + Valid.padleftSmc(tbDetailRacikanObat.getValueAt(i, 0).toString(), 2, '0'));
                            json.put("KDOBT", tbDetailRacikanObat.getValueAt(i, 11).toString());
                            json.put("NMOBAT", tbDetailRacikanObat.getValueAt(i, 12).toString());
                            json.put("SIGNA1OBT", tbDetailRacikanObat.getValueAt(i, 5).toString());
                            json.put("SIGNA2OBT", tbDetailRacikanObat.getValueAt(i, 6).toString());
                            json.put("PERMINTAAN", tbDetailRacikanObat.getValueAt(i, 10).toString());
                            json.put("JMLOBT", tbDetailRacikanObat.getValueAt(i, 1).toString());
                            json.put("JHO", tbDetailRacikanObat.getValueAt(i, 7).toString());
                            json.put("CatKhsObt", "RACIKAN " + (i + 1));
                            System.out.println(json.toString());
                            entity = new HttpEntity(json.toString(), headers);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println("Insert obat racikan " + tbDetailRacikanObat.getValueAt(i, 11).toString() + " " + tbDetailRacikanObat.getValueAt(i, 12).toString() + " : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                            if (metadata.path("code").asText().equals("200")) {
                                if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null, nosjp, tbDetailRacikanObat.getValueAt(i, 11).toString(),
                                    tbDetailRacikanObat.getValueAt(i, 12).toString(), tbDetailRacikanObat.getValueAt(i, 1).toString(),
                                    tbDetailRacikanObat.getValueAt(i, 5).toString(), tbDetailRacikanObat.getValueAt(i, 6).toString(),
                                    tbDetailRacikanObat.getValueAt(i, 7).toString(), tbDetailRacikanObat.getValueAt(i, 8).toString(),
                                    tbDetailRacikanObat.getValueAt(i, 9).toString(), tbDetailRacikanObat.getValueAt(i, 10).toString(),
                                    tbDetailRacikanObat.getValueAt(i, 0).toString()
                                )) {
                                    sukses = false;
                                    JOptionPane.showMessageDialog(null, "Gagal menyimpan " + tbDetailRacikanObat.getValueAt(i, 2).toString() + " - " + tbDetailRacikanObat.getValueAt(i, 3).toString() + " ke resep racikan..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                sukses = false;
                System.out.println(ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                }
            }
        }
    }

    private boolean cekStatusKlaim() {
        if (nosjp.isBlank()) return false;

        if (baruDibuat) return false;

        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            HttpEntity requestEntity = new HttpEntity(headers);
            Calendar cal = Calendar.getInstance();
            cal.setTime(TglPelayanan.getDate());
            URL = URLAPIAPOTEKBPJS + "/monitoring/klaim/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "/" + JenisObat.getSelectedItem().toString().substring(0, 1) + "/1";
            System.out.println(URL);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            JsonNode metadata = root.path("metaData");
            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                if (response.path("listsep").isArray()) {
                    String nosepapotek = "";
                    for (JsonNode list : response.path("listsep")) {
                        nosepapotek = list.path("nosepapotek").asText("");
                        if (nosepapotek != null && !nosepapotek.isBlank() && nosepapotek.equals(nosjp)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }

            if (JOptionPane.showConfirmDialog(null, "Tidak dapat mengecek status verifikasi resep, tetap lanjutkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return true;
            }
        }
        return false;
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tabModeObat.getRowCount(); i++) {
            total += (Double) tabModeObat.getValueAt(i, 8);
        }
        
        for (int i = 0; i < tabModeDetailRacikanObat.getRowCount(); i++) {
            total += (Double) tabModeDetailRacikanObat.getValueAt(i, 9);
        }
        LTotal.setText(Valid.SetAngka(total));
    }
}
