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

import inventory.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.Jurnal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import widget.Button;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSEditResepObatSMC extends javax.swing.JDialog {
    private final DefaultTableModel tabModeObat, tabModeRacikan, tabModeDetailRacikanObat;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final DlgCariAturanPakai aturanpakai = new DlgCariAturanPakai(null, false);
    private final DlgCariMetodeRacik metoderacik = new DlgCariMetodeRacik(null, false);
    private final ObjectMapper mapper = new ObjectMapper();
    private final ApiApotekBPJS api = new ApiApotekBPJS();
    private final String URLAPIAPOTEKBPJS = koneksiDB.URLAPIAPOTEKBPJS();
    private String Suspen_Piutang_Obat_Ranap = "", Obat_Ranap = "", HPP_Obat_Rawat_Inap = "", Persediaan_Obat_Rawat_Inap = "",
        Suspen_Piutang_Obat_Ralan = "", Obat_Ralan = "", HPP_Obat_Rawat_Jalan = "", Persediaan_Obat_Rawat_Jalan = "";
    private final boolean AKTIFKANBATCHOBAT = koneksiDB.AKTIFKANBATCHOBAT().trim().toLowerCase().equals("yes"),
        AKTIFKANBILLINGPARSIAL = koneksiDB.AKTIFKANBILLINGPARSIAL().trim().toLowerCase().equals("yes"),
        BRIDGINGAPOTEKBPJSPROSESKEUANGANDANSTOKOBAT = koneksiDB.BRIDGINGAPOTEKBPJSPROSESKEUANGANDANSTOKOBAT();
    private final riwayatobat Trackobat = new riwayatobat();
    private final Jurnal jur = new Jurnal();
    private String utc = "", URL = "", nosjp = "";
    private HttpHeaders headers;
    private HttpEntity entity;
    private JsonNode root, metadata;
    private boolean sukses = true, ispiutang = false;

    /**
     * Creates new form DlgPenyakit
     *
     * @param parent
     * @param modal
     */
    public ApotekBPJSEditResepObatSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 2);
        setSize(656, 250);

        tabModeObat = new DefaultTableModel(null, new String[] {
            "Jml.", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kode Obat BPJS", "Nama Obat BPJS"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0 ||
                    colIndex == 3 ||
                    colIndex == 4 ||
                    colIndex == 5;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5) {
                    return Double.class;
                }

                return String.class;
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
                column.setPreferredWidth(50);
            } else if (i == 4) {
                column.setPreferredWidth(50);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(80);
            } else if (i == 7) {
                column.setPreferredWidth(80);
            } else if (i == 8) {
                column.setPreferredWidth(75);
            } else if (i == 9) {
                column.setPreferredWidth(200);
            }
        }

        tabModeRacikan = new DefaultTableModel(null, new String[] {
            "No", "Nama Racikan", "Kode Racik", "Metode Racik", "Jml. Racik", "Aturan Pakai", "Keterangan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 1 ||
                    colIndex == 4 ||
                    colIndex == 5 ||
                    colIndex == 6;
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
            "No", "Jml.", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kandungan", "Kode Obat BPJS", "Nama Obat BPJS"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 1 || colIndex == 4 || colIndex == 5 || colIndex == 6 || colIndex == 9;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 4 || columnIndex == 5 || columnIndex == 6) {
                    return Double.class;
                }

                return String.class;
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
                column.setPreferredWidth(100);
            } else if (i == 10) {
                column.setPreferredWidth(75);
            } else if (i == 11) {
                column.setPreferredWidth(200);
            }
        }

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        BtnCariActionPerformed(null);
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        BtnCariActionPerformed(null);
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        BtnCariActionPerformed(null);
                    }
                }
            });
        }

        aturanpakai.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (aturanpakai.getTable().getSelectedRow() != -1) {
                    if (TabRawat.getSelectedIndex() == 0) {
                        tbObat.setValueAt(aturanpakai.getTable().getValueAt(aturanpakai.getTable().getSelectedRow(), 0).toString(), tbObat.getSelectedRow(), 11);
                        tbObat.requestFocus();
                    } else if (TabRawat.getSelectedIndex() == 1) {
                        tbRacikan.setValueAt(aturanpakai.getTable().getValueAt(aturanpakai.getTable().getSelectedRow(), 0).toString(), tbRacikan.getSelectedRow(), 5);
                        tbRacikan.requestFocus();
                    }
                }
            }
        });

        metoderacik.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (metoderacik.hasSelection()) {
                    tbRacikan.setValueAt(metoderacik.getSelectedValue(1).toString(), tbRacikan.getSelectedRow(), 2);
                    tbRacikan.setValueAt(metoderacik.getSelectedValue(2).toString(), tbRacikan.getSelectedRow(), 3);
                }
            }
        });

        metoderacik.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    metoderacik.dispose();
                }
            }
        });

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select set_akun_ralan.Suspen_Piutang_Obat_Ralan, set_akun_ralan.Obat_Ralan, " +
            "set_akun_ralan.HPP_Obat_Rawat_Jalan, set_akun_ralan.Persediaan_Obat_Rawat_Jalan from set_akun_ralan"
        )) {
            if (rs.next()) {
                Suspen_Piutang_Obat_Ralan = rs.getString("Suspen_Piutang_Obat_Ralan");
                Obat_Ralan = rs.getString("Obat_Ralan");
                HPP_Obat_Rawat_Jalan = rs.getString("HPP_Obat_Rawat_Jalan");
                Persediaan_Obat_Rawat_Jalan = rs.getString("Persediaan_Obat_Rawat_Jalan");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select set_akun_ranap.Suspen_Piutang_Obat_Ranap, set_akun_ranap.Obat_Ranap, " +
            "set_akun_ranap.HPP_Obat_Rawat_Inap, set_akun_ranap.Persediaan_Obat_Rawat_Inap from set_akun_ranap"
        )) {
            if (rs.next()) {
                Suspen_Piutang_Obat_Ranap = rs.getString("Suspen_Piutang_Obat_Ranap");
                Obat_Ranap = rs.getString("Obat_Ranap");
                HPP_Obat_Rawat_Inap = rs.getString("HPP_Obat_Rawat_Inap");
                Persediaan_Obat_Rawat_Inap = rs.getString("Persediaan_Obat_Rawat_Inap");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnSeek5 = new widget.Button();
        BtnTambah1 = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        label13 = new widget.Label();
        BtnKeluar = new widget.Button();
        FormInput = new widget.PanelBiasa();
        jLabel10 = new widget.Label();
        TNoRw = new widget.TextBox();
        jLabel19 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel4 = new widget.Label();
        NoKartu = new widget.TextBox();
        jLabel11 = new widget.Label();
        TNoRM = new widget.TextBox();
        jLabel21 = new widget.Label();
        TPasien = new widget.TextBox();
        jLabel18 = new widget.Label();
        NoSEP = new widget.TextBox();
        jLabel15 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel13 = new widget.Label();
        KdPoli = new widget.TextBox();
        NmPoli = new widget.TextBox();
        jLabel14 = new widget.Label();
        NoResep = new widget.TextBox();
        jLabel16 = new widget.Label();
        CmbIterasi = new widget.ComboBox();
        jLabel17 = new widget.Label();
        CmbJnsObat = new widget.ComboBox();
        jLabel20 = new widget.Label();
        TglResep = new widget.Tanggal();
        jLabel8 = new widget.Label();
        TglPelayanan = new widget.Tanggal();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Obat Apotek BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi3.setMinimumSize(new java.awt.Dimension(421, 43));
        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(44, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setToolTipText("Alt+C");
        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(330, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi3.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
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
        panelisi3.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setToolTipText("Alt+2");
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
        panelisi3.add(BtnAll);

        BtnSeek5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/011.png"))); // NOI18N
        BtnSeek5.setToolTipText("Alt+4");
        BtnSeek5.setName("BtnSeek5"); // NOI18N
        BtnSeek5.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek5ActionPerformed(evt);
            }
        });
        BtnSeek5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek5KeyPressed(evt);
            }
        });
        panelisi3.add(BtnSeek5);

        BtnTambah1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        BtnTambah1.setToolTipText("Alt+3");
        BtnTambah1.setName("BtnTambah1"); // NOI18N
        BtnTambah1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambah1ActionPerformed(evt);
            }
        });
        BtnTambah1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnTambah1KeyPressed(evt);
            }
        });
        panelisi3.add(BtnTambah1);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(28, 23));
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

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(28, 23));
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

        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi3.add(label13);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setToolTipText("Alt+5");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
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

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 74, 23);

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

        jLabel19.setText("Tgl.Lahir :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 40, 74, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        TglLahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglLahirKeyPressed(evt);
            }
        });
        FormInput.add(TglLahir);
        TglLahir.setBounds(78, 40, 90, 23);

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

        jLabel11.setText("No.RM :");
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel11);
        jLabel11.setBounds(205, 10, 45, 23);

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

        jLabel21.setText("Nama Pasien :");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel21);
        jLabel21.setBounds(348, 10, 75, 23);

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

        jLabel18.setText("No.SEP :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(370, 40, 50, 23);

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

        jLabel13.setText("Asal Poli :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(344, 70, 54, 23);

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

        jLabel14.setText("No. Resep :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 100, 74, 23);

        NoResep.setEditable(false);
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

        CmbIterasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tanpa Iterasi", "1. Iterasi 1", "2. Iterasi 2" }));
        CmbIterasi.setName("CmbIterasi"); // NOI18N
        CmbIterasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbIterasiKeyPressed(evt);
            }
        });
        FormInput.add(CmbIterasi);
        CmbIterasi.setBounds(225, 100, 140, 23);

        jLabel17.setText("Jenis Obat :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(369, 100, 66, 23);

        CmbJnsObat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Obat PRB", "2. Obat Kronis Belum Stabil", "3. Obat Kemoterapi" }));
        CmbJnsObat.setName("CmbJnsObat"); // NOI18N
        CmbJnsObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbJnsObatKeyPressed(evt);
            }
        });
        FormInput.add(CmbJnsObat);
        CmbJnsObat.setBounds(439, 100, 225, 23);

        jLabel20.setText("Tgl. Resep :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 130, 74, 23);

        TglResep.setEditable(false);
        TglResep.setForeground(new java.awt.Color(50, 70, 50));
        TglResep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-12-2025 14:27:51" }));
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

        jLabel8.setText("Tgl. Pelayanan :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel8);
        jLabel8.setBounds(212, 130, 85, 23);

        TglPelayanan.setEditable(false);
        TglPelayanan.setForeground(new java.awt.Color(50, 70, 50));
        TglPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-12-2025 14:27:51" }));
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
        tbRacikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbRacikanKeyPressed(evt);
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


    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        if (TabRawat.getSelectedIndex() == 0) {
            tampilobat();
        } else if (TabRawat.getSelectedIndex() == 1) {
            if (tbRacikan.getRowCount() != 0) {
                if (tbRacikan.getSelectedRow() != -1) {
                    if (tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 0).toString().equals("") ||
                        tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 1).toString().equals("") ||
                        tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 2).toString().equals("") ||
                        tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 3).toString().equals("") ||
                        tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 4).toString().equals("") ||
                        tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 5).toString().equals("") ||
                        tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 6).toString().equals("")) {
                        JOptionPane.showMessageDialog(null, "Silahkan lengkapi data racikan..!!");
                    } else {
                        tampildetailracikanobat();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Silahkan pilih racikan..!!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Silahkan masukkan racikan..!!");
            }
        }
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        buatcacheresep();
        BtnCariActionPerformed(evt);
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().isBlank()) {
            Valid.textKosong(TNoRw, "No. Rawat");
        } else if (TNoRM.getText().isBlank() || TPasien.getText().isBlank()) {
            Valid.textKosong(TNoRM, "Pasien");
        } else if (TglLahir.getText().isBlank()) {
            Valid.textKosong(TglLahir, "Tgl. Lahir");
        } else if (NoKartu.getText().isBlank()) {
            Valid.textKosong(NoKartu, "Nomor Kartu");
        } else if (NoSEP.getText().isBlank()) {
            Valid.textKosong(NoSEP, "Nomor Sep");
        } else if (KdDPJP.getText().isBlank() || NmDPJP.getText().isBlank()) {
            Valid.textKosong(KdDPJP, "Dokter");
        } else if (KdPoli.getText().isBlank() || NmPoli.getText().isBlank()) {
            Valid.textKosong(KdPoli, "Poliklinik");
        } else if (NoResep.getText().isBlank()) {
            Valid.textKosong(NoResep, "Nomor Resep");
        } else {
            if (ispiutang) {
                if (Sequel.cariExistsSmc("select * from bayar_piutang where bayar_piutang.no_rawat = ? and bayar_piutang.no_rkm_medis = ?", nosjp, TNoRM.getText())) {
                    JOptionPane.showMessageDialog(null, "Piutang obat sudah terverifikasi..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                } else {
                    if (akses.getadmin()) {
                        int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            sukses = true;

                            hapusObat();
                            if (sukses) {
                                if (tbObat.getRowCount() > 0) {
                                    insertObat();
                                }
                                if (tbDetailRacikanObat.getRowCount() > 0) {
                                    insertObatRacikan();
                                }
                            }

                            if (sukses) {
                                emptTeks();
                                JOptionPane.showMessageDialog(null, "Ubah resep berhasil..!!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        if (Sequel.cekTanggal48jam(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TglPelayanan.getDate()), Sequel.ambiltanggalsekarang())) {
                            int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                sukses = true;

                                hapusObat();
                                if (sukses) {
                                    if (tbObat.getRowCount() > 0) {
                                        insertObat();
                                    }
                                    if (tbDetailRacikanObat.getRowCount() > 0) {
                                        insertObatRacikan();
                                    }
                                }

                                if (sukses) {
                                    emptTeks();
                                    JOptionPane.showMessageDialog(null, "Ubah resep berhasil..!!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                }
            } else {
                if (AKTIFKANBILLINGPARSIAL && Sequel.cariExistsSmc("select * from reg_periksa join set_input_parsial " +
                    "on reg_periksa.kd_pj = set_input_parsial.kd_pj where reg_periksa.no_rawat = ?", TNoRw.getText()
                )) {
                    if (akses.getadmin()) {
                        int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            sukses = true;

                            hapusObat();
                            if (sukses) {
                                if (tbObat.getRowCount() > 0) {
                                    insertObat();
                                }
                                if (tbDetailRacikanObat.getRowCount() > 0) {
                                    insertObatRacikan();
                                }
                            }

                            if (sukses) {
                                JOptionPane.showMessageDialog(null, "Ubah resep berhasil..!!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        if (Sequel.cekTanggal48jam(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TglPelayanan.getDate()), Sequel.ambiltanggalsekarang())) {
                            int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                sukses = true;

                                hapusObat();
                                if (sukses) {
                                    if (tbObat.getRowCount() > 0) {
                                        insertObat();
                                    }
                                    if (tbDetailRacikanObat.getRowCount() > 0) {
                                        insertObatRacikan();
                                    }
                                }

                                if (sukses) {
                                    JOptionPane.showMessageDialog(null, "Ubah resep berhasil..!!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                } else {
                    if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
                        JOptionPane.showMessageDialog(null, "Billing sudah terverifikasi..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (akses.getadmin()) {
                            int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                sukses = true;

                                hapusObat();
                                if (sukses) {
                                    if (tbObat.getRowCount() > 0) {
                                        insertObat();
                                    }
                                    if (tbDetailRacikanObat.getRowCount() > 0) {
                                        insertObatRacikan();
                                    }
                                }

                                if (sukses) {
                                    JOptionPane.showMessageDialog(null, "Ubah resep berhasil..!!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            if (Sequel.cekTanggal48jam(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TglPelayanan.getDate()), Sequel.ambiltanggalsekarang())) {
                                int reply = JOptionPane.showConfirmDialog(null, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                                if (reply == JOptionPane.YES_OPTION) {
                                    sukses = true;

                                    hapusObat();
                                    if (sukses) {
                                        if (tbObat.getRowCount() > 0) {
                                            insertObat();
                                        }
                                        if (tbDetailRacikanObat.getRowCount() > 0) {
                                            insertObatRacikan();
                                        }
                                    }

                                    if (sukses) {
                                        JOptionPane.showMessageDialog(null, "Ubah resep berhasil..!!");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat mengubah resep obat,\nPeriksa kembali data obat yang mau disimpan..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSeek5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek5ActionPerformed
        DlgCariKonversi carikonversi = new DlgCariKonversi(null, false);
        carikonversi.setLocationRelativeTo(internalFrame1);
        carikonversi.setAlwaysOnTop(false);
        carikonversi.setVisible(true);
    }//GEN-LAST:event_BtnSeek5ActionPerformed

    private void BtnTambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambah1ActionPerformed
        if (tabModeRacikan.getRowCount() >= 99) {
            JOptionPane.showMessageDialog(null, "Maksimal 98 Racikan..!!");
        } else {
            // "No", "Nama Racikan", "Kode Racik", "Metode Racik", "Jml. Racik", "Aturan Pakai", "Keterangan"
            //  0     1               2             3               4             5               6
            tabModeRacikan.addRow(new Object[] {
                String.valueOf(tabModeRacikan.getRowCount() + 1), "", "", "", 0, "", ""
            });
        }
    }//GEN-LAST:event_BtnTambah1ActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbRacikan.getSelectedRow() >= 0) {
            if (JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus resep racikan ini?", "Hapus Racikan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                for (int i = tbDetailRacikanObat.getRowCount() - 1; i >= 0; i--) {
                    if (tbDetailRacikanObat.getValueAt(i, 0).toString().equals(tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 0).toString())) {
                        tabModeDetailRacikanObat.removeRow(i);
                    }
                }
                tabModeRacikan.removeRow(tbRacikan.getSelectedRow());
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        Valid.pindahSmc(evt, BtnKeluar, TNoRM);
    }//GEN-LAST:event_TNoRwKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        Valid.pindahSmc(evt, TNoRw, TPasien);
    }//GEN-LAST:event_TNoRMKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindahSmc(evt, TNoRM, TglLahir);
    }//GEN-LAST:event_TPasienKeyPressed

    private void TglLahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglLahirKeyPressed
        Valid.pindahSmc(evt, TPasien, NoKartu);
    }//GEN-LAST:event_TglLahirKeyPressed

    private void NoKartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoKartuKeyPressed
        Valid.pindahSmc(evt, TglLahir, NoSEP);
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
        Valid.pindahSmc(evt, NmPoli, CmbIterasi);
    }//GEN-LAST:event_NoResepKeyPressed

    private void CmbIterasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbIterasiKeyPressed
        Valid.pindahSmc(evt, NoResep, CmbJnsObat);
    }//GEN-LAST:event_CmbIterasiKeyPressed

    private void CmbJnsObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbJnsObatKeyPressed
        Valid.pindahSmc(evt, CmbIterasi, TglResep);
    }//GEN-LAST:event_CmbJnsObatKeyPressed

    private void TglResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglResepKeyPressed
        Valid.pindahSmc(evt, CmbJnsObat, TglPelayanan);
    }//GEN-LAST:event_TglResepKeyPressed

    private void TabRawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TabRawatKeyPressed
        Valid.pindahSmc(evt, TglPelayanan, TCari);
    }//GEN-LAST:event_TabRawatKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindahSmc(evt, TabRawat, BtnCari);
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        Valid.pindahSmc(evt, TCari, BtnAll);
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        Valid.pindahSmc(evt, BtnCari, BtnSeek5);
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnSeek5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek5KeyPressed
        Valid.pindahSmc(evt, BtnAll, BtnTambah1);
    }//GEN-LAST:event_BtnSeek5KeyPressed

    private void BtnTambah1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnTambah1KeyPressed
        Valid.pindahSmc(evt, BtnSeek5, BtnSimpan);
    }//GEN-LAST:event_BtnTambah1KeyPressed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        Valid.pindahSmc(evt, BtnTambah1, BtnHapus);
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        Valid.pindahSmc(evt, BtnSimpan, BtnKeluar);
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        Valid.pindahSmc(evt, BtnHapus, TNoRw);
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void TglPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglPelayananKeyPressed
        Valid.pindahSmc(evt, TglResep, TabRawat);
    }//GEN-LAST:event_TglPelayananKeyPressed

    private void tbObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbObatPropertyChange
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            // "Jml.", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kode Obat BPJS", "Nama Obat BPJS"
            //  0       1              2              3          4          5            6        7           8                 9
            if (tbObat.getEditingRow() >= 0) {
                if (tbObat.getEditingColumn() == 0) {
                    try {
                        tbObat.setValueAt(
                            (Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 0).toString()) /
                            Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 3).toString())) *
                            Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 4).toString()),
                            tbObat.getEditingRow(), 5
                        );
                    } catch (Exception e) {
                    }
                } else if (tbObat.getEditingColumn() == 3 || tbObat.getEditingColumn() == 4 || tbObat.getEditingColumn() == 5) {
                    try {
                        if (Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 5).toString()) > 0) {
                            tbObat.setValueAt(
                                (Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 5).toString()) *
                                Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 3).toString())) /
                                Valid.SetAngka(tbObat.getValueAt(tbObat.getEditingRow(), 4).toString()),
                                tbObat.getEditingRow(), 0
                            );
                        }
                    } catch (Exception e) {
                    }
                }
                updateTotal();
            }
        }
    }//GEN-LAST:event_tbObatPropertyChange

    private void tbRacikanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbRacikanPropertyChange

    }//GEN-LAST:event_tbRacikanPropertyChange

    private void tbDetailRacikanObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDetailRacikanObatPropertyChange
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            if (tbDetailRacikanObat.getEditingRow() >= 0) {
                if (tbDetailRacikanObat.getEditingColumn() == 1) {
                    try {
                        tbDetailRacikanObat.setValueAt(
                            (Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 1).toString()) /
                            Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 4).toString())) *
                            Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 5).toString()),
                            tbDetailRacikanObat.getEditingRow(), 6
                        );
                    } catch (Exception e) {
                    }
                } else if (tbDetailRacikanObat.getEditingColumn() == 4 || tbDetailRacikanObat.getEditingColumn() == 5 || tbDetailRacikanObat.getEditingColumn() == 6) {
                    try {
                        if (Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 6).toString()) > 0) {
                            tbDetailRacikanObat.setValueAt(
                                (Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 6).toString()) *
                                Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 4).toString())) /
                                Valid.SetAngka(tbDetailRacikanObat.getValueAt(tbDetailRacikanObat.getEditingRow(), 5).toString()),
                                tbDetailRacikanObat.getEditingRow(), 1
                            );
                        }
                    } catch (Exception e) {
                    }
                }
                updateTotal();
            }
        }
    }//GEN-LAST:event_tbDetailRacikanObatPropertyChange

    private void tbRacikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbRacikanKeyPressed
        if (tbRacikan.getSelectedRow() >= 0) {
            if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (tbRacikan.getSelectedColumn() == 3) {
                    if (tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 1).toString().isBlank()) {
                        JOptionPane.showMessageDialog(null, "Silahkan masukkan nama racikan..!!");
                        tbRacikan.requestFocus();
                    } else {
                        metoderacik.isCek();
                        metoderacik.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
                        metoderacik.setLocationRelativeTo(internalFrame1);
                        metoderacik.setVisible(true);
                    }
                } else if (tbRacikan.getSelectedColumn() == 5) {
                    aturanpakai.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
                    aturanpakai.setLocationRelativeTo(internalFrame1);
                    aturanpakai.setVisible(true);
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if (tbRacikan.getSelectedColumn() == 6) {
                    if (tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 1).toString().isBlank()
                        || tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 2).toString().isBlank()
                        || tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 3).toString().isBlank()
                        || tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 4).toString().isBlank()
                        || tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 5).toString().isBlank()
                    ) {
                        JOptionPane.showMessageDialog(null,"Silahkan lengkapi data racikan..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    } else {
                        tampildetailracikanobat();
                        TCari.requestFocus();
                    }
                }
            }
        }
    }//GEN-LAST:event_tbRacikanKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSeek5;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambah1;
    private widget.ComboBox CmbIterasi;
    private widget.ComboBox CmbJnsObat;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdPoli;
    private widget.Label LTotal;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmPoli;
    private widget.TextBox NoKartu;
    private widget.TextBox NoResep;
    private widget.TextBox NoSEP;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TabPane TabRawat;
    private widget.TextBox TglLahir;
    private widget.Tanggal TglPelayanan;
    private widget.Tanggal TglResep;
    private widget.InternalFrame internalFrame1;
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
    private widget.Label label13;
    private widget.Label label9;
    private widget.panelisi panelisi3;
    private widget.Table tbDetailRacikanObat;
    private widget.Table tbObat;
    private widget.Table tbRacikan;
    // End of variables declaration//GEN-END:variables

    private void buatcacheresep() {
        try {
            File file = new File("./cache/mapingobatapotekbpjs.iyem");
            file.createNewFile();
            try (FileWriter fw = new FileWriter(file); ResultSet rs = koneksi.createStatement().executeQuery(
                "select o.kode_brng, o.nama_brng, m.kode_brng_apotek_bpjs, m.nama_brng_apotek_bpjs, m.harga " +
                "from databarang o join maping_obat_apotek_bpjs m on o.kode_brng = m.kode_brng order by o.nama_brng"
            )) {
                ArrayNode array = mapper.createArrayNode();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("kode_brng", rs.getString("kode_brng"));
                    row.put("nama_brng", rs.getString("nama_brng"));
                    row.put("kode_brng_apotek_bpjs", rs.getString("kode_brng_apotek_bpjs"));
                    row.put("nama_brng_apotek_bpjs", rs.getString("nama_brng_apotek_bpjs"));
                    row.put("harga", rs.getDouble("harga"));
                    array.add(mapper.valueToTree(row));
                }
                ObjectNode root = mapper.createObjectNode();
                root.set("mapingobatapotekbpjs", array);
                fw.write(mapper.writeValueAsString(root));
                fw.flush();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilobat() {
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < tbObat.getRowCount(); i++) {
            if (Valid.SetAngka(tbObat.getValueAt(i, 0).toString()) > 0) {
                Map<String, Object> row = new HashMap<>();
                row.put("jml", tbObat.getValueAt(i, 0));
                row.put("kode_brng", tbObat.getValueAt(i, 1));
                row.put("nama_brng", tbObat.getValueAt(i, 2));
                row.put("signa1", tbObat.getValueAt(i, 3));
                row.put("signa2", tbObat.getValueAt(i, 4));
                row.put("jml_hari", tbObat.getValueAt(i, 5));
                row.put("harga", tbObat.getValueAt(i, 6));
                row.put("subtotal", tbObat.getValueAt(i, 7));
                row.put("kode_brng_apotek_bpjs", tbObat.getValueAt(i, 8));
                row.put("nama_brng_apotek_bpjs", tbObat.getValueAt(i, 9));
                rows.add(row);
                keys.add(tbObat.getValueAt(i, 1).toString());
            }
        }

        Valid.tabelKosong(tabModeObat);

        rows.forEach(row -> {
            tabModeObat.addRow(new Object[] {
                row.get("jml"), row.get("kode_brng"), row.get("nama_brng"), row.get("signa1"), row.get("signa2"), row.get("jml_hari"),
                row.get("harga"), row.get("subtotal"), row.get("kode_brng_apotek_bpjs"), row.get("nama_brng_apotek_bpjs")
            });
        });

        if (Valid.umurcacheSmc("./cache/mapingobatapotekbpjs.iyem", 1)) {
            buatcacheresep();
        }

        try (FileReader fr = new FileReader("./cache/mapingobatapotekbpjs.iyem")) {
            JsonNode response = mapper.readTree(fr).path("mapingobatapotekbpjs");
            if (response.isArray()) {
                if (TCari.getText().isBlank()) {
                    for (JsonNode item : response) {
                        if (keys.contains(item.path("kode_brng").asText())) {
                            continue;
                        }

                        tabModeObat.addRow(new Object[] {
                            0d, item.path("kode_brng").asText(), item.path("nama_brng").asText(), 1d, 1d, 0d, item.path("harga").asDouble(0),
                            0d, item.path("kode_brng_apotek_bpjs").asText(), item.path("nama_brng_apotek_bpjs").asText()
                        });
                    }
                } else {
                    for (JsonNode item : response) {
                        if (keys.contains(item.path("kode_brng").asText())) {
                            continue;
                        }

                        if (item.toString().toLowerCase().contains(TCari.getText().toLowerCase().trim())) {
                            tabModeObat.addRow(new Object[] {
                                0d, item.path("kode_brng").asText(), item.path("nama_brng").asText(), 1d, 1d, 0d, item.path("harga").asDouble(0),
                                0d, item.path("kode_brng_apotek_bpjs").asText(), item.path("nama_brng_apotek_bpjs").asText()
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampildetailracikanobat() {
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < tbDetailRacikanObat.getRowCount(); i++) {
            if (Valid.SetAngka(tbDetailRacikanObat.getValueAt(i, 1).toString()) > 0) {
                Map<String, Object> row = new HashMap<>();
                row.put("no", tbDetailRacikanObat.getValueAt(i, 0));
                row.put("jml", tbDetailRacikanObat.getValueAt(i, 1));
                row.put("kode_brng", tbDetailRacikanObat.getValueAt(i, 2));
                row.put("nama_brng", tbDetailRacikanObat.getValueAt(i, 3));
                row.put("signa1", tbDetailRacikanObat.getValueAt(i, 4));
                row.put("signa2", tbDetailRacikanObat.getValueAt(i, 5));
                row.put("jml_hari", tbDetailRacikanObat.getValueAt(i, 6));
                row.put("harga", tbDetailRacikanObat.getValueAt(i, 7));
                row.put("subtotal", tbDetailRacikanObat.getValueAt(i, 8));
                row.put("kandungan", tbDetailRacikanObat.getValueAt(i, 9));
                row.put("kode_brng_apotek_bpjs", tbDetailRacikanObat.getValueAt(i, 10));
                row.put("nama_brng_apotek_bpjs", tbDetailRacikanObat.getValueAt(i, 11));
                rows.add(row);
                keys.add(tbDetailRacikanObat.getValueAt(i, 2).toString());
            }
        }

        Valid.tabelKosong(tabModeDetailRacikanObat);

        rows.forEach(row -> {
            tabModeDetailRacikanObat.addRow(new Object[] {
                row.get("no"), row.get("jml"), row.get("kode_brng"), row.get("nama_brng"), row.get("signa1"),
                row.get("signa2"), row.get("jml_hari"), row.get("harga"), row.get("subtotal"), row.get("kandungan"),
                row.get("kode_brng_apotek_bpjs"), row.get("nama_brng_apotek_bpjs")
            });
        });

        if (Valid.umurcacheSmc("./cache/mapingobatapotekbpjs.iyem", 1)) {
            buatcacheresep();
        }

        try (FileReader fr = new FileReader("./cache/mapingobatapotekbpjs.iyem")) {
            JsonNode response = mapper.readTree(fr).path("mapingobatapotekbpjs");
            if (response.isArray()) {
                if (TCari.getText().isBlank()) {
                    for (JsonNode item : response) {
                        if (keys.contains(item.path("kode_brng").asText())) {
                            continue;
                        }

                        tabModeDetailRacikanObat.addRow(new Object[] {
                            tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 0).toString(), 0, item.path("kode_brng").asText(), item.path("nama_brng").asText(),
                            1, 1, 0, item.path("harga").asDouble(0), 0, "", item.path("kode_brng_apotek_bpjs").asText(), item.path("nama_brng_apotek_bpjs").asText()
                        });
                    }
                } else {
                    for (JsonNode item : response) {
                        if (keys.contains(item.path("kode_brng").asText())) {
                            continue;
                        }

                        if (item.toString().toLowerCase().contains(TCari.getText().toLowerCase().trim())) {
                            tabModeDetailRacikanObat.addRow(new Object[] {
                                tbRacikan.getValueAt(tbRacikan.getSelectedRow(), 0).toString(), 0, item.path("kode_brng").asText(), item.path("nama_brng").asText(),
                                1, 1, 0, item.path("harga").asDouble(0), "", item.path("kode_brng_apotek_bpjs").asText(), item.path("nama_brng_apotek_bpjs").asText()
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void tampilresep(String nosjp) {
        this.nosjp = nosjp;
        this.ispiutang = Sequel.cariExistsSmc("select * from piutang where piutang.nota_piutang = ?", nosjp);
        try {
            Valid.tabelKosong(tabModeObat);
            Valid.tabelKosong(tabModeRacikan);
            Valid.tabelKosong(tabModeDetailRacikanObat);
            double total = 0;
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select a.*, s.no_kartu, s.no_rawat, r.no_rkm_medis, px.nm_pasien, px.tgl_lahir from bridging_apotek_bpjs a join bridging_sep s on a.no_sep = s.no_sep " +
                "join reg_periksa r on s.no_rawat = r.no_rawat join pasien px on r.no_rkm_medis = px.no_rkm_medis where a.no_sjp = ?"
            )) {
                ps.setString(1, nosjp);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        TNoRw.setText(rs.getString("no_rawat"));
                        TNoRM.setText(rs.getString("no_rkm_medis"));
                        TPasien.setText(rs.getString("nm_pasien"));
                        TglLahir.setText(rs.getString("tgl_lahir"));
                        NoKartu.setText(rs.getString("no_kartu"));
                        NoSEP.setText(rs.getString("no_sep"));
                        KdDPJP.setText(rs.getString("kodedpjp"));
                        NmDPJP.setText(rs.getString("nmdpjp"));
                        KdPoli.setText(rs.getString("kd_poli"));
                        NmPoli.setText(rs.getString("nm_poli"));
                        NoResep.setText(rs.getString("no_resep"));
                        switch (rs.getString("iterasi")) {
                            case "0":
                                CmbIterasi.setSelectedIndex(0);
                                break;
                            case "1":
                                CmbIterasi.setSelectedIndex(1);
                                break;
                            case "2":
                                CmbIterasi.setSelectedIndex(2);
                                break;
                        }
                        switch (rs.getString("jenis_obat")) {
                            case "1":
                                CmbJnsObat.setSelectedIndex(0);
                                break;
                            case "2":
                                CmbJnsObat.setSelectedIndex(1);
                                break;
                            case "3":
                                CmbJnsObat.setSelectedIndex(2);
                                break;
                        }
                        TglResep.setDate(rs.getTimestamp("tgl_resep"));
                        TglPelayanan.setDate(rs.getTimestamp("tgl_pelayanan"));

                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select a.no_racik, a.jumlah, m.kode_brng, o.nama_brng, a.signa1, a.signa2, a.jml_hari, a.harga, a.subtotal, " +
                            "a.kandungan, a.kode_brng_apotek_bpjs, a.nama_brng_apotek_bpjs from bridging_apotek_bpjs_obat a left join " +
                            "maping_obat_apotek_bpjs m on a.kode_brng_apotek_bpjs = m.kode_brng_apotek_bpjs left join databarang o on " +
                            "m.kode_brng = o.kode_brng where a.no_sjp = ?"
                        )) {
                            ps2.setString(1, nosjp);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    total += rs2.getDouble("subtotal");
                                    if (rs2.getString("no_racik") != null) {
                                        tabModeDetailRacikanObat.addRow(new Object[] {
                                            rs2.getString("no_racik"), rs2.getDouble("jumlah"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getDouble("signa1"),
                                            rs2.getDouble("signa2"), rs2.getDouble("jml_hari"), rs2.getDouble("harga"), rs2.getDouble("subtotal"),
                                            rs2.getString("kandungan"), rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                        });
                                    } else {
                                        tabModeObat.addRow(new Object[] {
                                            rs2.getDouble("jumlah"), rs2.getString("kode_brng"), rs2.getString("nama_brng"), rs2.getDouble("signa1"), rs2.getDouble("signa2"),
                                            rs2.getDouble("jml_hari"), rs2.getDouble("harga"), rs2.getDouble("subtotal"),
                                            rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                        });
                                    }
                                }
                            }
                        }

                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select r.nama_racik, r.kd_racik, m.nm_racik, r.jml_dr, r.aturan_pakai, r.keterangan, " +
                            "r.no_racik from bridging_apotek_bpjs_racikan r join metode_racik m on r.kd_racik = m.kd_racik " +
                            "where r.no_sjp = ? order by r.no_racik"
                        )) {
                            ps2.setString(1, nosjp);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    tabModeRacikan.addRow(new Object[] {
                                        rs2.getString("no_racik"), rs2.getString("nama_racik"), rs2.getString("kd_racik"), rs2.getString("nm_racik"),
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

    public void emptTeksobat() {
        TCari.setText("");
        TCari.requestFocus();
    }

    public JTable getTable() {
        return tbObat;
    }

    public Button getButton() {
        return BtnSimpan;
    }

    private void hapusObat() {
        try {
            Sequel.AutoComitFalse();

            try (PreparedStatement ps = koneksi.prepareStatement(
                "select a.no_racik, a.jumlah, m.kode_brng, o.nama_brng, a.signa1, a.signa2, a.jml_hari, " +
                "a.kandungan, a.kode_brng_apotek_bpjs, a.nama_brng_apotek_bpjs from bridging_apotek_bpjs_obat a " +
                "left join maping_obat_apotek_bpjs m on a.kode_brng_apotek_bpjs = m.kode_brng_apotek_bpjs " +
                "left join databarang o on m.kode_brng = o.kode_brng where a.no_sjp = ? order by a.no_racik"
            )) {
                ps.setString(1, nosjp);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
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
                                URL = URLAPIAPOTEKBPJS + "/pelayanan/obat/hapus";
                                System.out.println(URL);
                                ObjectNode json = mapper.createObjectNode();
                                json.put("nosepapotek", nosjp);
                                json.put("noresep", NoResep.getText());
                                json.put("kodeobat", rs.getString("kode_brng_apotek_bpjs"));
                                if (rs.getString("no_racik") != null) {
                                    json.put("tipeobat", "R." + Valid.padleftSmc(rs.getString("no_racik"), 2, '0'));
                                } else {
                                    json.put("tipeobat", "N");
                                }
                                System.out.println(mapper.writeValueAsString(json));
                                entity = new HttpEntity(mapper.writeValueAsString(json), headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.DELETE, entity, String.class).getBody());
                                metadata = root.path("metaData");
                                System.out.println("Hapus obat [" + rs.getString("kode_brng_apotek_bpjs") + " " + rs.getString("nama_brng_apotek_bpjs") + "] : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                                if (metadata.path("code").asText().equals("200")) {
                                    if (!Sequel.menghapustfSmc("bridging_apotek_bpjs_obat", "no_sjp = ? and kode_brng_apotek_bpjs = ?", nosjp, rs.getString("kode_brng_apotek_bpjs"))) {
                                        sukses = false;
                                        JOptionPane.showMessageDialog(null, "Gagal menghapus " + rs.getString("kode_brng") + " - " + rs.getString("nama_brng") + " dari resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                                }
                            } catch (HttpClientErrorException | HttpServerErrorException e) {
                                System.out.println("Notif : " + e.getResponseBodyAsString());
                                JOptionPane.showMessageDialog(null, "Tidak dapat mengedit obat", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }

            if (sukses) {
                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select r.no_racik, (select count(*) from bridging_apotek_bpjs_obat o where o.no_sjp = r.no_sjp and " +
                    "o.no_racik = r.no_racik) as jml from bridging_apotek_bpjs_racikan r where r.no_sjp = ? order by r.no_racik"
                )) {
                    ps.setString(1, nosjp);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            if (rs.getInt("jml") == 0) {
                                if (!Sequel.menghapustfSmc("bridging_apotek_bpjs_racikan", "no_sjp = ? and no_racik = ?", nosjp, rs.getString("no_racik"))) {
                                    sukses = false;
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    sukses = false;
                    System.out.println("Notif : " + e);
                }
            }

            Sequel.AutoComitTrue();
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
        }
    }

    private void insertObat() {
        try {
            Sequel.AutoComitFalse();

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
                    json.put("KDOBT", tbObat.getValueAt(i, 8).toString());
                    json.put("NMOBAT", tbObat.getValueAt(i, 9).toString());
                    json.put("JMLOBT", (Double) tbObat.getValueAt(i, 0));
                    json.put("SIGNA1OBT", (Double) tbObat.getValueAt(i, 3));
                    json.put("SIGNA2OBT", (Double) tbObat.getValueAt(i, 4));
                    json.put("JHO", (Double) tbObat.getValueAt(i, 5));
                    json.put("CatKhsObt", "Non racikan");
                    System.out.println(mapper.writeValueAsString(json));
                    entity = new HttpEntity(mapper.writeValueAsString(json), headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
                    metadata = root.path("metaData");
                    System.out.println("Insert obat [" + tbObat.getValueAt(i, 8).toString() + " " + tbObat.getValueAt(i, 9).toString() + "] : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                    if (metadata.path("code").asText().equals("200")) {
                        if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null, nosjp, tbObat.getValueAt(i, 8).toString(),
                            tbObat.getValueAt(i, 9).toString(), tbObat.getValueAt(i, 0).toString(), tbObat.getValueAt(i, 3).toString(),
                            tbObat.getValueAt(i, 4).toString(), tbObat.getValueAt(i, 5).toString(), tbObat.getValueAt(i, 6).toString(),
                            tbObat.getValueAt(i, 7).toString(), null, null
                        )) {
                            sukses = false;
                            JOptionPane.showMessageDialog(null, "Gagal menyimpan " + tbObat.getValueAt(i, 1).toString() + " - " + tbObat.getValueAt(i, 2).toString() + " ke resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            Sequel.AutoComitTrue();
        } catch (Exception e) {
            sukses = false;
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void insertObatRacikan() {
        try {
            if (sukses) {
                for (int i = 0; i < tbRacikan.getRowCount(); i++) {
                    if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_racikan", null, nosjp, tbRacikan.getValueAt(i, 1).toString(),
                        tbRacikan.getValueAt(i, 2).toString(), tbRacikan.getValueAt(i, 4).toString(), tbRacikan.getValueAt(i, 5).toString(),
                        tbRacikan.getValueAt(i, 6).toString(), tbRacikan.getValueAt(i, 0).toString()
                    )) {
                        sukses = false;
                    }
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
                        // "No", "Jml.", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kandungan", "Kode Obat BPJS", "Nama Obat BPJS"
                        //  0     1       2              3              4          5          6            7        8           9            10                11
                        json.put("NOSJP", nosjp);
                        json.put("NORESEP", NoResep.getText());
                        json.put("JNSROBT", "R." + Valid.padleftSmc(tbDetailRacikanObat.getValueAt(i, 0).toString(), 2, '0'));
                        json.put("KDOBT", tbDetailRacikanObat.getValueAt(i, 10).toString());
                        json.put("NMOBAT", tbDetailRacikanObat.getValueAt(i, 11).toString());
                        json.put("SIGNA1OBT", Valid.SetAngka(tbDetailRacikanObat.getValueAt(i, 4).toString()));
                        json.put("SIGNA2OBT", Valid.SetAngka(tbDetailRacikanObat.getValueAt(i, 5).toString()));
                        json.put("PERMINTAAN", tbDetailRacikanObat.getValueAt(i, 9).toString());
                        json.put("JMLOBT", Valid.SetAngka(tbDetailRacikanObat.getValueAt(i, 1).toString()));
                        json.put("JHO", Valid.SetAngka(tbDetailRacikanObat.getValueAt(i, 6).toString()));
                        json.put("CatKhsObt", "RACIKAN " + tbDetailRacikanObat.getValueAt(i, 0).toString());
                        System.out.println(json.toString());
                        entity = new HttpEntity(json.toString(), headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, entity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("Insert obat racikan [" + tbDetailRacikanObat.getValueAt(i, 10).toString() + " " + tbDetailRacikanObat.getValueAt(i, 11).toString() + "] : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                        if (metadata.path("code").asText().equals("200")) {
                            // (no_sjp, kode_brng_apotek_bpjs, nama_brng_apotek_bpjs, jumlah, signa1, signa2, jml_hari, harga, subtotal, kandungan, no_racik)
                            if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null, nosjp, tbDetailRacikanObat.getValueAt(i, 10).toString(),
                                tbDetailRacikanObat.getValueAt(i, 11).toString(), tbDetailRacikanObat.getValueAt(i, 1).toString(),
                                tbDetailRacikanObat.getValueAt(i, 4).toString(), tbDetailRacikanObat.getValueAt(i, 5).toString(),
                                tbDetailRacikanObat.getValueAt(i, 6).toString(), tbDetailRacikanObat.getValueAt(i, 7).toString(),
                                tbDetailRacikanObat.getValueAt(i, 8).toString(), tbDetailRacikanObat.getValueAt(i, 9).toString(),
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
            } else {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan resep racikan..!!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void emptTeks() {
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        TglLahir.setText("");
        NoKartu.setText("");
        NoSEP.setText("");
        KdDPJP.setText("");
        NmDPJP.setText("");
        KdPoli.setText("");
        NmPoli.setText("");
        NoResep.setText("");
        CmbIterasi.setSelectedIndex(0);
        CmbJnsObat.setSelectedIndex(0);
        TglResep.setDate(new Date());
        TglPelayanan.setDate(new Date());
        LTotal.setText("0");
        Valid.tabelKosong(tabModeObat);
        Valid.tabelKosong(tabModeRacikan);
        Valid.tabelKosong(tabModeDetailRacikanObat);
        TCari.setText("");
        ispiutang = false;
        nosjp = "";
    }

    private void updateTotal() {
        double subtotal = 0, total = 0;

        for (int i = 0; i < tabModeObat.getRowCount(); i++) {
            subtotal = Valid.SetAngka(tabModeObat.getValueAt(i, 6).toString()) * Valid.SetAngka(tabModeObat.getValueAt(i, 0).toString());
            total += subtotal;
            tabModeObat.setValueAt(subtotal, i, 7);
        }

        for (int i = 0; i < tabModeDetailRacikanObat.getRowCount(); i++) {
            subtotal = Valid.SetAngka(tabModeDetailRacikanObat.getValueAt(i, 7).toString()) * Valid.SetAngka(tabModeDetailRacikanObat.getValueAt(i, 1).toString());
            total += subtotal;
            tabModeDetailRacikanObat.setValueAt(subtotal, i, 8);
        }

        LTotal.setText(Valid.SetAngka(total));
    }
}
