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
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.Jurnal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import simrskhanza.DlgCariBangsal;
import widget.Button;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSKirimObat extends javax.swing.JDialog {
    private final DefaultTableModel tabModeobat, tabModeObatRacikan, tabModeDetailObatRacikan;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private double h_belicari = 0, hargacari = 0, sisacari = 0, x = 0, y = 0, embalase = Sequel.cariIsiAngka("select set_embalase.embalase_per_obat from set_embalase"),
        tuslah = Sequel.cariIsiAngka("select set_embalase.tuslah_per_obat from set_embalase"), kenaikan = 0, stokbarang = 0, ttl = 0, ppnobat = 0, ttlhpp, ttljual, Hari = 0;
    private int row = 0, row2, r, subttl;
    private Jurnal jur = new Jurnal();
    private boolean[] pilih;
    private double[] jumlah, harga, eb, ts, stok, beli, kapasitas, kandungan;
    private String[] kodebarang, namabarang, kodesatuan, letakbarang, namajenis, aturan, industri, kategori, golongan, no, nobatch, nofaktur, kadaluarsa, keterangan, signa_cari1, signa_cari2, signa_racikan1, signa_racikan2;
    private String no_apotek = "", signa1 = "1", utc = "", pesan = "", link = koneksiDB.URLAPIAPOTEKBPJS(), signa2 = "1", nokunjungan = "", kdObatSK = "", requestJson = "", URL = "", otorisasi, sql = "", aktifpcare = "no", no_batchcari = "", tgl_kadaluarsacari = "", no_fakturcari = "", aktifkanbatch = "no", kodedokter = "", namadokter = "", noresep = "", bangsal = "", bangsaldefault = Sequel.cariIsiSmc("select set_lokasi.kd_bangsal from set_lokasi limit 1"), tampilkan_ppnobat_ralan = "",
        Suspen_Piutang_Obat_Ralan = "", Obat_Ralan = "", HPP_Obat_Rawat_Jalan = "", Persediaan_Obat_Rawat_Jalan = "", hppfarmasi = "", VALIDASIULANGBERIOBAT = "", DEPOAKTIFOBAT = "", NORESEPAKTIF = "no", diagnosa_pasien, no_claim, ObatRutin,
        sqlpscariobat2 = "select databarang.nama_brng,jenis.nama,detail_pemberian_obat.biaya_obat, detail_pemberian_obat.jml, detail_pemberian_obat.total, databarang.kode_brng, aturan_pakai.aturan " +
        "from detail_pemberian_obat inner join databarang inner join jenis on detail_pemberian_obat.kode_brng=databarang.kode_brng and databarang.kdjns=jenis.kdjns LEFT JOIN aturan_pakai ON detail_pemberian_obat.no_rawat=aturan_pakai.no_rawat AND " +
        "databarang.kode_brng=aturan_pakai.kode_brng and detail_pemberian_obat.jam=aturan_pakai.jam where detail_pemberian_obat.no_rawat=? and detail_pemberian_obat.tgl_perawatan=? AND detail_pemberian_obat.jam=? group by detail_pemberian_obat.kode_brng order by jenis.nama";
    private DlgCariBangsal caribangsal = new DlgCariBangsal(null, false);
    public DlgCariAturanPakai aturanpakai = new DlgCariAturanPakai(null, false);
    private DlgCariMetodeRacik metoderacik = new DlgCariMetodeRacik(null, false);
    private riwayatobat Trackobat = new riwayatobat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode root, metadata, response;
    private String[] arrSplit;
    private boolean sukses = true;
    private ApotekBPJSCekReferensiDPHO refobatbpjs = new ApotekBPJSCekReferensiDPHO(null, false);
    private ApiApotekBPJS api = new ApiApotekBPJS();

    /**
     * Creates new form DlgPenyakit
     *
     * @param parent
     * @param modal
     */
    public ApotekBPJSKirimObat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabModeobat = new DefaultTableModel(null, new Object[] {
            "K", "Jumlah", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jumlah Hari", "kdobatbpjs", "nmobatbpjs" 
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
                if (columnIndex == 0) {
                    return java.lang.Boolean.class;
                }

                if (columnIndex == 1 || columnIndex == 4 || columnIndex == 5 || columnIndex == 6) {
                    return java.lang.Double.class;
                }

                return java.lang.String.class;
            }
        };

        tbObat.setModel(tabModeobat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 9; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(45);
            } else if (i == 2) {
                column.setPreferredWidth(75);
            } else if (i == 3) {
                column.setPreferredWidth(200);
            } else if (i == 4) {
                column.setPreferredWidth(50);
            } else if (i == 5) {
                column.setPreferredWidth(50);
            } else if (i == 6) {
                column.setPreferredWidth(100);
            } else if (i == 7 || i == 8) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeObatRacikan = new DefaultTableModel(null, new Object[] {
            "No", "Nama Racikan", "Kode Racik", "Metode Racik", "Jml.Racik", "Aturan Pakai", "Keterangan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return java.lang.Double.class;
                }
                
                return java.lang.String.class;
            }
        };

        tbObatRacikan.setModel(tabModeObatRacikan);
        tbObatRacikan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObatRacikan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 7; i++) {
            TableColumn column = tbObatRacikan.getColumnModel().getColumn(i);
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

        tbObatRacikan.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDetailObatRacikan = new DefaultTableModel(null, new Object[] {
            "No", "Jumlah", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jumlah Hari", "kdobatbpjs", "nmobatbpjs"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 1 || colIndex == 4 || colIndex == 5 || colIndex == 6;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 4 || columnIndex == 5 || columnIndex == 6) {
                    return java.lang.Double.class;
                }

                return java.lang.String.class;
            }
        };

        tbDetailObatRacikan.setModel(tabModeDetailObatRacikan);
        tbDetailObatRacikan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailObatRacikan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 9; i++) {
            TableColumn column = tbDetailObatRacikan.getColumnModel().getColumn(i);
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
                column.setPreferredWidth(100);
            } else if (i == 7 || i == 8) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }

        tbDetailObatRacikan.setDefaultRenderer(Object.class, new WarnaTable());

        aturanpakai.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (aturanpakai.getTable().getSelectedRow() != -1) {
                    if (TabRawat.getSelectedIndex() == 0) {
                        tbObat.setValueAt(aturanpakai.getTable().getValueAt(aturanpakai.getTable().getSelectedRow(), 0).toString(), tbObat.getSelectedRow(), 11);
                        tbObat.requestFocus();
                    } else if (TabRawat.getSelectedIndex() == 1) {
                        tbObatRacikan.setValueAt(aturanpakai.getTable().getValueAt(aturanpakai.getTable().getSelectedRow(), 0).toString(), tbObatRacikan.getSelectedRow(), 5);
                        tbObatRacikan.requestFocus();
                    }
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        metoderacik.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (metoderacik.getTable().getSelectedRow() != -1) {
                    tbObatRacikan.setValueAt(metoderacik.getTable().getValueAt(metoderacik.getTable().getSelectedRow(), 1).toString(), tbObatRacikan.getSelectedRow(), 2);
                    tbObatRacikan.setValueAt(metoderacik.getTable().getValueAt(metoderacik.getTable().getSelectedRow(), 2).toString(), tbObatRacikan.getSelectedRow(), 3);
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        metoderacik.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    metoderacik.dispose();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        try {
            hppfarmasi = koneksiDB.HPPFARMASI();
        } catch (Exception e) {
            hppfarmasi = "dasar";
        }

        try {
            VALIDASIULANGBERIOBAT = koneksiDB.VALIDASIULANGBERIOBAT();
        } catch (Exception e) {
            VALIDASIULANGBERIOBAT = "no";
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery("select Suspen_Piutang_Obat_Ralan, Obat_Ralan, HPP_Obat_Rawat_Jalan, Persediaan_Obat_Rawat_Jalan from set_akun_ralan")) {
            if (rs.next()) {
                Suspen_Piutang_Obat_Ralan = rs.getString(1);
                Obat_Ralan = rs.getString(2);
                HPP_Obat_Rawat_Jalan = rs.getString(3);
                Persediaan_Obat_Rawat_Jalan = rs.getString(4);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
//            NORESEPAKTIF=koneksiDB.NORESEPAKTIF();
        } catch (Exception e) {
            NORESEPAKTIF = "yes";
        }

        tampilkan_ppnobat_ralan = Sequel.cariIsiSmc("select set_nota.tampilkan_ppnobat_ralan from set_nota");
        jam();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Kd2 = new widget.TextBox();
        TNoRw = new widget.TextBox();
        Tanggal = new widget.TextBox();
        KdPj = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        panelisi3 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnSimpan1 = new widget.Button();
        BtnSimpan2 = new widget.Button();
        BtnSimpan3 = new widget.Button();
        BtnHapus = new widget.Button();
        CariDataObat = new widget.Button();
        BtnKeluar = new widget.Button();
        FormInput = new widget.PanelBiasa();
        jLabel8 = new widget.Label();
        DTPTgl = new widget.Tanggal();
        cmbJam = new widget.ComboBox();
        cmbMnt = new widget.ComboBox();
        cmbDtk = new widget.ComboBox();
        ChkJln = new widget.CekBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        LblNoRawat = new widget.TextBox();
        TglResep = new widget.Tanggal();
        jLabel20 = new widget.Label();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        jLabel13 = new widget.Label();
        jLabel15 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel14 = new widget.Label();
        TResep = new widget.TextBox();
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
        JnsObat = new widget.ComboBox();
        TabRawat = new widget.TabPane();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        Scroll1 = new widget.ScrollPane();
        tbObatRacikan = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbDetailObatRacikan = new widget.Table();

        Kd2.setHighlighter(null);
        Kd2.setName("Kd2"); // NOI18N
        Kd2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kd2KeyPressed(evt);
            }
        });

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N

        Tanggal.setHighlighter(null);
        Tanggal.setName("Tanggal"); // NOI18N

        KdPj.setHighlighter(null);
        KdPj.setName("KdPj"); // NOI18N

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
        panelisi3.add(BtnSimpan);

        BtnSimpan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan1.setMnemonic('S');
        BtnSimpan1.setText("Simpan Resep Kosong");
        BtnSimpan1.setToolTipText("Alt+S");
        BtnSimpan1.setName("BtnSimpan1"); // NOI18N
        BtnSimpan1.setPreferredSize(new java.awt.Dimension(180, 30));
        BtnSimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan1ActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan1);

        BtnSimpan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan2.setMnemonic('S');
        BtnSimpan2.setText("INSERT OBAT NON RACIKAN");
        BtnSimpan2.setToolTipText("Alt+S");
        BtnSimpan2.setName("BtnSimpan2"); // NOI18N
        BtnSimpan2.setPreferredSize(new java.awt.Dimension(210, 30));
        BtnSimpan2.setRolloverEnabled(false);
        BtnSimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan2ActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan2);

        BtnSimpan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan3.setMnemonic('S');
        BtnSimpan3.setText("INSERT OBAT RACIKAN");
        BtnSimpan3.setToolTipText("Alt+S");
        BtnSimpan3.setName("BtnSimpan3"); // NOI18N
        BtnSimpan3.setPreferredSize(new java.awt.Dimension(180, 30));
        BtnSimpan3.setRolloverEnabled(false);
        BtnSimpan3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan3ActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan3);

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
        panelisi3.add(BtnHapus);

        CariDataObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        CariDataObat.setText("Cari");
        CariDataObat.setToolTipText("");
        CariDataObat.setName("CariDataObat"); // NOI18N
        CariDataObat.setPreferredSize(new java.awt.Dimension(100, 30));
        CariDataObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CariDataObatActionPerformed(evt);
            }
        });
        CariDataObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CariDataObatKeyPressed(evt);
            }
        });
        panelisi3.add(CariDataObat);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('5');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+5");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        FormInput.setBackground(new java.awt.Color(215, 225, 215));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        jLabel8.setText("Tanggal :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(68, 23));
        FormInput.add(jLabel8);
        jLabel8.setBounds(212, 130, 60, 23);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "09-10-2025" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(276, 130, 90, 23);

        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam.setName("cmbJam"); // NOI18N
        cmbJam.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJamKeyPressed(evt);
            }
        });
        FormInput.add(cmbJam);
        cmbJam.setBounds(370, 130, 62, 23);

        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt.setName("cmbMnt"); // NOI18N
        cmbMnt.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbMnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMntKeyPressed(evt);
            }
        });
        FormInput.add(cmbMnt);
        cmbMnt.setBounds(436, 130, 62, 23);

        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDtk.setName("cmbDtk"); // NOI18N
        cmbDtk.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbDtk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbDtkKeyPressed(evt);
            }
        });
        FormInput.add(cmbDtk);
        cmbDtk.setBounds(502, 130, 62, 23);

        ChkJln.setBorder(null);
        ChkJln.setSelected(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkJln.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkJln.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkJln.setName("ChkJln"); // NOI18N
        ChkJln.setPreferredSize(new java.awt.Dimension(22, 23));
        ChkJln.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkJlnActionPerformed(evt);
            }
        });
        FormInput.add(ChkJln);
        ChkJln.setBounds(568, 130, 22, 23);

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
        FormInput.add(TPasien);
        TPasien.setBounds(427, 10, 237, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.setPreferredSize(new java.awt.Dimension(207, 23));
        TNoRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoRMActionPerformed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(254, 10, 90, 23);

        LblNoRawat.setEditable(false);
        LblNoRawat.setName("LblNoRawat"); // NOI18N
        LblNoRawat.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(LblNoRawat);
        LblNoRawat.setBounds(78, 10, 123, 23);

        TglResep.setForeground(new java.awt.Color(50, 70, 50));
        TglResep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "09-10-2025 09:27:16" }));
        TglResep.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglResep.setName("TglResep"); // NOI18N
        TglResep.setOpaque(false);
        TglResep.setPreferredSize(new java.awt.Dimension(95, 23));
        TglResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TglResepActionPerformed(evt);
            }
        });
        TglResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglResepKeyPressed(evt);
            }
        });
        FormInput.add(TglResep);
        TglResep.setBounds(78, 130, 130, 23);

        jLabel20.setText("Tgl.Resep :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 130, 74, 23);

        NmPoli.setEditable(false);
        NmPoli.setHighlighter(null);
        NmPoli.setName("NmPoli"); // NOI18N
        FormInput.add(NmPoli);
        NmPoli.setBounds(481, 70, 183, 23);

        KdPoli.setEditable(false);
        KdPoli.setHighlighter(null);
        KdPoli.setName("KdPoli"); // NOI18N
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
        FormInput.add(KdDPJP);
        KdDPJP.setBounds(78, 70, 75, 23);

        NmDPJP.setEditable(false);
        NmDPJP.setHighlighter(null);
        NmDPJP.setName("NmDPJP"); // NOI18N
        FormInput.add(NmDPJP);
        NmDPJP.setBounds(157, 70, 183, 23);

        jLabel14.setText("No.Resep :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 100, 74, 23);

        TResep.setHighlighter(null);
        TResep.setName("TResep"); // NOI18N
        TResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TResepKeyPressed(evt);
            }
        });
        FormInput.add(TResep);
        TResep.setBounds(78, 100, 90, 23);

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
        NoKartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoKartuActionPerformed(evt);
            }
        });
        FormInput.add(NoKartu);
        NoKartu.setBounds(236, 40, 130, 23);

        NoSEP.setEditable(false);
        NoSEP.setHighlighter(null);
        NoSEP.setName("NoSEP"); // NOI18N
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
        FormInput.add(Iterasi);
        Iterasi.setBounds(225, 100, 140, 23);

        JnsObat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Obat PRB", "2. Obat Kronis Belum Stabil", "3. Obat Kemoterapi" }));
        JnsObat.setName("JnsObat"); // NOI18N
        FormInput.add(JnsObat);
        JnsObat.setBounds(439, 100, 225, 23);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ScrollMouseClicked(evt);
            }
        });

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbObatPropertyChange(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
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

        tbObatRacikan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObatRacikan.setName("tbObatRacikan"); // NOI18N
        tbObatRacikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatRacikanKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbObatRacikan);

        jPanel3.add(Scroll1, java.awt.BorderLayout.PAGE_START);

        Scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbDetailObatRacikan.setAutoCreateRowSorter(true);
        tbDetailObatRacikan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbDetailObatRacikan.setName("tbDetailObatRacikan"); // NOI18N
        tbDetailObatRacikan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDetailObatRacikanMouseClicked(evt);
            }
        });
        tbDetailObatRacikan.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDetailObatRacikanPropertyChange(evt);
            }
        });
        tbDetailObatRacikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDetailObatRacikanKeyPressed(evt);
            }
        });
        Scroll2.setViewportView(tbDetailObatRacikan);

        jPanel3.add(Scroll2, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Racikan", jPanel3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tbObat.getRowCount() != 0) {
            try {
                getDataobat();
            } catch (java.lang.NullPointerException e) {
            }

            if (evt.getClickCount() == 2) {
                if (akses.getform().equals("DlgPemberianObat")) {
                    dispose();
                }
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed

    }//GEN-LAST:event_tbObatKeyPressed

    private void Kd2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kd2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kd2KeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
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
        } else if (TResep.getText().trim().equals("")) {
            Valid.textKosong(TResep, "Nomor Resep");
        } else if (Lahir.getText().trim().equals("")) {
            Valid.textKosong(Lahir, "Lahir");
        } else {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                    requestEntity = new HttpEntity(headers);
                    URL = link + "/sjpresep/v3/insert";
                    System.out.println(URL);
                    ObjectNode json = mapper.createObjectNode();
                    json.put("TGLSJP", Valid.getTglJamSmc(DTPTgl, cmbJam, cmbMnt, cmbDtk));
                    json.put("REFASALSJP", NoSEP.getText());
                    json.put("POLIRSP", KdPoli.getText());
                    json.put("KDJNSOBAT", JnsObat.getSelectedItem().toString().substring(0, 1));
                    json.put("NORESEP", TResep.getText());
                    json.put("IDUSERSJP", "RS_" + akses.getkode());
                    json.put("TGLRSP", Valid.getTglSmc(TglResep) + " 00:00:00");
                    json.put("TGLPELRSP", Valid.getTglJamSmc(TglResep));
                    json.put("KdDokter", "0");
                    json.put("iterasi", Iterasi.getSelectedItem().toString().substring(0, 1));
                    requestJson = json.toString();
                    System.out.println(requestJson);
                    requestEntity = new HttpEntity(requestJson, headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                    metadata = root.path("metaData");
                    System.out.println("Kirim resep obat : " + metadata.path("code") + " - " + metadata.path("message"));
                    if (!metadata.path("code").asText().equals("200")) {
                        JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                    } else {
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                        System.out.println(response);
                        no_apotek = response.path("noApotik").asText();
                        if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs", null,
                            NoSEP.getText(), no_apotek, TResep.getText(), Valid.getTglSmc(TglResep) + " 00:00:00", Valid.getTglJamSmc(TglResep),
                            JnsObat.getSelectedItem().toString().substring(0, 1), Iterasi.getSelectedItem().toString().substring(0, 1),
                            KdPoli.getText(), NmPoli.getText(), KdDPJP.getText(), NmDPJP.getText(), akses.getkode()
                        )) {
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menyimpan resep..!!", "Gagal", JOptionPane.ERROR_MESSAGE);
                        } else {
                            URL = link + "/obatnonracikan/v3/insert";
                            System.out.println(URL);
                            for (int i = 0; i < tabModeobat.getRowCount(); i++) {
                                if (Valid.SetAngka(tabModeobat.getValueAt(i, 1).toString()) > 0) {
                                    json = mapper.createObjectNode();
                                    json.put("NOSJP", no_apotek);
                                    json.put("NORESEP", TResep.getText());
                                    json.put("KDOBT", tabModeobat.getValueAt(i, 7).toString());
                                    json.put("NMOBAT", tabModeobat.getValueAt(i, 8).toString());
                                    json.put("SIGNA1OBT", (Double) tabModeobat.getValueAt(i, 4));
                                    json.put("SIGNA2OBT", (Double) tabModeobat.getValueAt(i, 5));
                                    json.put("JMLOBT", (Double) tabModeobat.getValueAt(i, 1));
                                    json.put("JHO", (Double) tabModeobat.getValueAt(i, 6));
                                    json.put("CatKhsObt", "Non racikan");
                                    requestJson = json.toString();
                                    System.out.println(requestJson);
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("Obat nonracikan ke resep : " + metadata.path("code").asText() + " - " + metadata.path("message").asText());
                                    if (metadata.path("code").asText().equals("200")) {
                                        if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null,
                                            NoSEP.getText(), TResep.getText(),
                                            tabModeobat.getValueAt(i, 2).toString(),
                                            tabModeobat.getValueAt(i, 3).toString(),
                                            tabModeobat.getValueAt(i, 1).toString(),
                                            tabModeobat.getValueAt(i, 4).toString(),
                                            tabModeobat.getValueAt(i, 5).toString(),
                                            "0", no_apotek
                                        )) {
                                            JOptionPane.showMessageDialog(null, "Gagal menyimpan " + tabModeobat.getValueAt(i, 2).toString() + " - " + tabModeobat.getValueAt(i, 3).toString() + " ke resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Obat " + tabModeobat.getValueAt(i, 2).toString() + " - " + tabModeobat.getValueAt(i, 3).toString() + " diresepkan kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                                }
                            }

                            for (int i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                                if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 1).toString()) > 0) {
                                    json = mapper.createObjectNode();
                                    json.put("NOSJP", no_apotek);
                                    json.put("NORESEP", TResep.getText());
                                    json.put("KDOBT", tabModeDetailObatRacikan.getValueAt(i, 8).toString());
                                    json.put("NMOBAT", tabModeDetailObatRacikan.getValueAt(i, 9).toString());
                                    json.put("SIGNA1OBT", (Double) tabModeDetailObatRacikan.getValueAt(i, 4));
                                    json.put("SIGNA2OBT", (Double) tabModeDetailObatRacikan.getValueAt(i, 5));
                                    json.put("JMLOBT", (Double) tabModeDetailObatRacikan.getValueAt(i, 1));
                                    json.put("JHO", (Double) tabModeDetailObatRacikan.getValueAt(i, 6));
                                    json.put("CatKhsObt", "RACIKAN " + (i + 1));
                                    requestJson = json.toString();
                                    System.out.println(requestJson);
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("Obat racikan ke resep : " + metadata.path("code").asText() + " - " + metadata.path("message").asText());
                                    if (metadata.path("code").asText().equals("200")) {
                                        if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null,
                                            NoSEP.getText(), TResep.getText(),
                                            tabModeDetailObatRacikan.getValueAt(i, 2).toString(),
                                            tabModeDetailObatRacikan.getValueAt(i, 3).toString(),
                                            tabModeDetailObatRacikan.getValueAt(i, 1).toString(),
                                            tabModeDetailObatRacikan.getValueAt(i, 4).toString(),
                                            tabModeDetailObatRacikan.getValueAt(i, 5).toString(),
                                            "1", no_apotek
                                        )) {
                                            JOptionPane.showMessageDialog(null, "Gagal menyimpan " + tabModeDetailObatRacikan.getValueAt(i, 2).toString() + " - " + tabModeDetailObatRacikan.getValueAt(i, 3).toString() + " ke resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Obat " + tabModeDetailObatRacikan.getValueAt(i, 2).toString() + " - " + tabModeDetailObatRacikan.getValueAt(i, 3).toString() + " diresepkan kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        }
                    }

                    /*
                    if (Iterasi.getSelectedIndex() == 2) {
                        // TODO: Implementasi ITERASI 2 secara proper
                    }
                    if (Iterasi.getSelectedIndex() == 0) {
                        System.out.println("Tanpa iterasi");
                        URL = link + "/sjpresep/v3/insert";
                        System.out.println(URL);
                        requestJson = "{" //                                    + "\"TGLSJP\": \"" + Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+Jam.getText()+ "\","+
                            +
                             "\"TGLSJP\": \"" + Valid.getTglSmc(TglResep) + " 00:00:00\"," +
                            "\"REFASALSJP\": \"" + NoSEP.getText() + "\"," +
                            "\"POLIRSP\": \"" + KdPoli.getText() + "\"," +
                            "\"KDJNSOBAT\": \"" + JnsObat.getSelectedItem().toString().substring(0, 1) + "\"," +
                            "\"NORESEP\": \"" + TResep.getText() + "\", " +
                            "\"IDUSERSJP\": \"RS_" + akses.getkode() + "\"," +
                            "\"TGLRSP\": \"" + Valid.getTglJamSmc(TglResep) + "\", " +
                            "\"TGLPELRSP\": \"" + Valid.getTglJamSmc(DTPTgl, cmbJam, cmbMnt, cmbDtk) + "\"," +
                            "\"KdDokter\": \"0\"," +
                            "\"iterasi\":\"" + Iterasi.getSelectedItem().toString().substring(0, 1) + "\"" +
                            "}  ";
                        System.out.println("Resep : " + requestJson);
                        requestEntity = new HttpEntity(requestJson, headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("data = " + metadata);
                        System.out.println("error = " + metadata.path("message").asText());
                        if (!metadata.path("code").asText().equals("200")) {
                            JOptionPane.showMessageDialog(null, "ERROR : " + metadata.path("message").asText());
                        }
                        if (metadata.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                            System.out.println("Response : " + response);
                            if (Sequel.menyimpantf2("bridging_apotek_bpjs", "?,?,?,?,?,?,?,?,?,?,?,?", "data", 12,
                                new String[] {
                                    response.path("noSep_Kunjungan").asText(),
                                    response.path("noApotik").asText(),
                                    TResep.getText(),
                                    Valid.getTglJamSmc(TglResep),
                                    Valid.getTglSmc(TglResep),
                                    JnsObat.getSelectedItem().toString().substring(0, 1),
                                    Iterasi.getSelectedItem().toString().substring(0, 1),
                                    KdPoli.getText(),
                                    NmPoli.getText(),
                                    KdDPJP.getText(),
                                    NmDPJP.getText(),
                                    akses.getkode(),}) == true) {
                                System.out.println("Simpan No Resep Selesai");
                                JOptionPane.showMessageDialog(null, "Resep Apotek " + response.path("noApotik").asText() + " Berhasil disimpan ");
                                no_apotek = response.path("noApotik").asText();
                                URL = link + "/obatnonracikan/v3/insert";
                                System.out.println(URL);
                                for (int i = 0; i < tbObat.getRowCount(); i++) {
                                    if (Valid.SetAngka(tbObat.getValueAt(i, 1).toString()) > 0) {
                                        try {

                                            requestJson = "{\n" +
                                                "            \"NOSJP\": \"" + response.path("noApotik").asText() + "\",\n" +
                                                "            \"NORESEP\": \"" + TResep.getText() + "\",\n" +
                                                "            \"KDOBT\": \"" + Sequel.cariIsiSmc("SELECT kode_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbObat.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"NMOBAT\": \"" + Sequel.cariIsiSmc("SELECT nama_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbObat.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"SIGNA1OBT\": " + tbObat.getValueAt(i, 4).toString() + ",\n" +
                                                "            \"SIGNA2OBT\": " + tbObat.getValueAt(i, 5).toString() + ",\n" +
                                                "            \"JMLOBT\": " + tbObat.getValueAt(i, 1).toString() + ",\n" +
                                                "            \"JHO\": " + tbObat.getValueAt(i, 6).toString() + ",\n" +
                                                "            \"CatKhsObt\": \"non racikan\"\n" +
                                                "        }     ";
                                            System.out.println("Detail Obat : " + requestJson);
                                            requestEntity = new HttpEntity(requestJson, headers);
                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                            metadata = root.path("metaData");
                                            System.out.println("data = " + metadata);
                                            if (metadata.path("code").asText().equals("200")) {
                                                if (Sequel.menyimpantf("bridging_apotek_bpjs_obat", "?,?,?,?,?,?,?,?,?", "Simpan Obat Apotek BPJS", 9, new String[] {
                                                    response.path("noSep_Kunjungan").asText(),
                                                    TResep.getText(),
                                                    tbObat.getValueAt(i, 2).toString(),
                                                    tbObat.getValueAt(i, 3).toString(),
                                                    tbObat.getValueAt(i, 1).toString(),
                                                    tbObat.getValueAt(i, 4).toString(),
                                                    tbObat.getValueAt(i, 5).toString(),
                                                    "0",
                                                    no_apotek
                                                }) == true) {
                                                    System.out.println("Obat " + tbObat.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                    JOptionPane.showMessageDialog(null, "Obat " + tbObat.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                }
                                            } else {
                                                System.out.println("Obat Gagal Simpan, " + metadata.path("message").asText());
                                                JOptionPane.showMessageDialog(null, "Obat Gagal Simpan, " + metadata.path("message").asText());
                                            }
                                            System.out.println("non racikan = \n\n" + requestJson);
                                        } catch (Exception ex) {
                                            System.out.println("Notifikasi : " + ex);
                                            if (ex.toString().contains("UnknownHostException")) {
                                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                            }
                                        }
                                    }
                                }

                                //                            racikan
                                URL = link + "/obatracikan/v3/insert";
                                System.out.println(URL);
                                for (int i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                                    if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 1).toString()) > 0) {
                                        try {
                                            requestJson = "{\n" +
                                                "            \"NOSJP\": \"" + no_apotek + "\",\n" +
                                                "            \"NORESEP\": \"" + TResep.getText() + "\",\n" +
                                                "            \"JNSROBT\": \"R.0" + (tbDetailObatRacikan.getValueAt(i, 0).toString()) + "\",\n" +
                                                "            \"KDOBT\": \"" + Sequel.cariIsiSmc("SELECT kode_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"NMOBAT\": \"" + Sequel.cariIsiSmc("SELECT nama_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"SIGNA1OBT\": " + tbDetailObatRacikan.getValueAt(i, 4).toString() + ",\n" +
                                                "            \"SIGNA2OBT\": " + tbDetailObatRacikan.getValueAt(i, 5).toString() + ",\n" +
                                                "            \"PERMINTAAN\": " + tbDetailObatRacikan.getValueAt(i, 7).toString() + ",\n" +
                                                "            \"JMLOBT\": " + tbDetailObatRacikan.getValueAt(i, 1).toString() + ",\n" +
                                                "            \"JHO\": " + tbDetailObatRacikan.getValueAt(i, 6).toString() + ",\n" +
                                                "            \"CatKhsObt\": \"RACIKAN " + (i + 1) + "\"\n" +
                                                "        }     ";
                                            requestEntity = new HttpEntity(requestJson, headers);
                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                            metadata = root.path("metaData");
                                            System.out.println("data = " + metadata);
                                            if (metadata.path("code").asText().equals("200")) {
                                                if (Sequel.menyimpantf("bridging_apotek_bpjs_obat", "?,?,?,?,?,?,?,?,?", "Simpan Obat Apotek BPJS Racikan", 9, new String[] {
                                                    response.path("noSep_Kunjungan").asText(),
                                                    TResep.getText(),
                                                    tbDetailObatRacikan.getValueAt(i, 2).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 3).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 1).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 4).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 5).toString(),
                                                    "1",
                                                    no_apotek
                                                }) == true) {
                                                    System.out.println("Obat " + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                    JOptionPane.showMessageDialog(null, "Obat racikan" + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                }
                                            } else {
                                                System.out.println("Obat Gagal Simpan, " + metadata.path("message").asText());
                                                JOptionPane.showMessageDialog(null, "Obat Gagal Simpan, " + metadata.path("message").asText());
                                            }

                                            System.out.println("racikan = \n\n" + requestJson);
                                        } catch (Exception ex) {
                                            System.out.println("Notifikasi : " + ex);
                                            if (ex.toString().contains("UnknownHostException")) {
                                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (Iterasi.getSelectedIndex() == 1) {
                        System.out.println("Dengan iterasi:");
                        URL = link + "/sjpresep/v3/insert";
                        System.out.println(URL);
                        requestJson = "{" //                                    + "\"TGLSJP\": \"" + Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+Jam.getText()+ "\","+
                            +
                             "\"TGLSJP\": \"" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() + "\"," +
                            "\"REFASALSJP\": \"" + NoSEP.getText() + "\"," +
                            "\"POLIRSP\": \"" + KdPoli.getText() + "\"," +
                            "\"KDJNSOBAT\": \"" + JnsObat.getSelectedItem().toString().substring(0, 1) + "\"," +
                            "\"NORESEP\": \"" + TResep.getText() + "\", " +
                            "\"IDUSERSJP\": \"RS_" + akses.getkode() + "\"," +
                            "\"TGLRSP\": \"" + Valid.SetTgl(TglResep.getSelectedItem() + "") + " 00:00:00\", " +
                            "\"TGLPELRSP\": \"" + Valid.SetTgl(TglResep.getSelectedItem() + "") + " 00:00:00\"," +
                            "\"KdDokter\": \"0\"," +
                            "\"iterasi\":\"" + Iterasi.getSelectedItem().toString().substring(0, 1) + "\"" +
                            "}  ";
                        System.out.println("Resep : " + requestJson);
                        requestEntity = new HttpEntity(requestJson, headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("data = " + metadata);
                        System.out.println("error = " + metadata.path("message").asText());
                        if (metadata.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                            System.out.println("Response : " + response);
                            if (Sequel.menyimpantf2("bridging_apotek_bpjs", "?,?,?,?,?,?,?,?,?,?,?,?", "data", 12,
                                new String[] {
                                    response.path("noSep_Kunjungan").asText(),
                                    response.path("noApotik").asText(),
                                    TResep.getText(),
                                    Valid.getTglJamSmc(TglResep),
                                    Valid.getTglSmc(TglResep),
                                    JnsObat.getSelectedItem().toString().substring(0, 1),
                                    Iterasi.getSelectedItem().toString().substring(0, 1),
                                    KdPoli.getText(),
                                    NmPoli.getText(),
                                    KdDPJP.getText(),
                                    NmDPJP.getText(),
                                    akses.getkode(),}) == true) {
                                System.out.println("Simpan No Resep Selesai");
                                JOptionPane.showMessageDialog(null, "Resep Apotek " + response.path("noApotik").asText() + " Berhasil disimpan ");
                                no_apotek = response.path("noApotik").asText();
                                URL = link + "/obatnonracikan/v3/insert";
                                System.out.println(URL);
                                for (int i = 0; i < tbObat.getRowCount(); i++) {
                                    if (Valid.SetAngka(tbObat.getValueAt(i, 1).toString()) > 0) {
                                        try {

                                            requestJson = "{\n" +
                                                "            \"NOSJP\": \"" + response.path("noApotik").asText() + "\",\n" +
                                                "            \"NORESEP\": \"" + TResep.getText() + "\",\n" +
                                                "            \"KDOBT\": \"" + Sequel.cariIsiSmc("SELECT kode_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbObat.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"NMOBAT\": \"" + Sequel.cariIsiSmc("SELECT nama_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbObat.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"SIGNA1OBT\": " + tbObat.getValueAt(i, 4).toString() + ",\n" +
                                                "            \"SIGNA2OBT\": " + tbObat.getValueAt(i, 5).toString() + ",\n" +
                                                "            \"JMLOBT\": " + tbObat.getValueAt(i, 1).toString() + ",\n" +
                                                "            \"JHO\": " + tbObat.getValueAt(i, 6).toString() + ",\n" +
                                                "            \"CatKhsObt\": \"non racikan\"\n" +
                                                "        }     ";
                                            System.out.println("Detail Obat : " + requestJson);
                                            requestEntity = new HttpEntity(requestJson, headers);
                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                            metadata = root.path("metaData");
                                            System.out.println("data = " + metadata);
                                            if (metadata.path("code").asText().equals("200")) {
                                                if (Sequel.menyimpantf("bridging_apotek_bpjs_obat", "?,?,?,?,?,?,?,?,?", "Simpan Obat Apotek BPJS", 9, new String[] {
                                                    response.path("noSep_Kunjungan").asText(),
                                                    TResep.getText(),
                                                    tbObat.getValueAt(i, 2).toString(),
                                                    tbObat.getValueAt(i, 3).toString(),
                                                    tbObat.getValueAt(i, 1).toString(),
                                                    tbObat.getValueAt(i, 4).toString(),
                                                    tbObat.getValueAt(i, 5).toString(),
                                                    "0",
                                                    no_apotek
                                                }) == true) {
                                                    System.out.println("Obat " + tbObat.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                    JOptionPane.showMessageDialog(null, "Obat " + tbObat.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                }
                                            } else {
                                                System.out.println("Obat Gagal Simpan, " + metadata.path("message").asText());
                                                JOptionPane.showMessageDialog(null, "Obat Gagal Simpan, " + metadata.path("message").asText());
                                            }

                                            System.out.println("non racikan = \n\n" + requestJson);
                                        } catch (Exception ex) {
                                            System.out.println("Notifikasi : " + ex);
                                            if (ex.toString().contains("UnknownHostException")) {
                                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                            }
                                        }
                                    }
                                }

                                //                            racikan
                                URL = link + "/obatracikan/v3/insert";
                                System.out.println(URL);
                                for (int i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                                    if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 1).toString()) > 0) {
                                        try {
                                            requestJson = "{\n" +
                                                "            \"NOSJP\": \"" + no_apotek + "\",\n" +
                                                "            \"NORESEP\": \"" + TResep.getText() + "\",\n" +
                                                "            \"JNSROBT\": \"R.0" + (tbDetailObatRacikan.getValueAt(i, 0).toString()) + "\",\n" +
                                                "            \"KDOBT\": \"" + Sequel.cariIsiSmc("SELECT kode_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"NMOBAT\": \"" + Sequel.cariIsiSmc("SELECT nama_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                                "            \"SIGNA1OBT\": " + tbDetailObatRacikan.getValueAt(i, 4).toString() + ",\n" +
                                                "            \"SIGNA2OBT\": " + tbDetailObatRacikan.getValueAt(i, 5).toString() + ",\n" +
                                                "            \"PERMINTAAN\": " + tbDetailObatRacikan.getValueAt(i, 7).toString() + ",\n" +
                                                "            \"JMLOBT\": " + tbDetailObatRacikan.getValueAt(i, 1).toString() + ",\n" +
                                                "            \"JHO\": " + tbDetailObatRacikan.getValueAt(i, 6).toString() + ",\n" +
                                                "            \"CatKhsObt\": \"RACIKAN " + (i + 1) + "\"\n" +
                                                "        }     ";
                                            requestEntity = new HttpEntity(requestJson, headers);
                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                            metadata = root.path("metaData");
                                            System.out.println("data = " + metadata);
                                            if (metadata.path("code").asText().equals("200")) {
                                                if (Sequel.menyimpantf("bridging_apotek_bpjs_obat", "?,?,?,?,?,?,?,?,?", "Simpan Obat Apotek BPJS Racikan", 9, new String[] {
                                                    response.path("noSep_Kunjungan").asText(),
                                                    TResep.getText(),
                                                    tbDetailObatRacikan.getValueAt(i, 2).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 3).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 1).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 4).toString(),
                                                    tbDetailObatRacikan.getValueAt(i, 5).toString(),
                                                    "1",
                                                    no_apotek
                                                }) == true) {
                                                    System.out.println("Obat " + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                    JOptionPane.showMessageDialog(null, "Obat racikan" + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                                }
                                            } else {
                                                System.out.println("Obat Gagal Simpan, " + metadata.path("message").asText());
                                                JOptionPane.showMessageDialog(null, "Obat Gagal Simpan, " + metadata.path("message").asText());
                                            }

                                            System.out.println("racikan = \n\n" + requestJson);
                                        } catch (Exception ex) {
                                            System.out.println("Notifikasi : " + ex);
                                            if (ex.toString().contains("UnknownHostException")) {
                                                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                            }
                                        }
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(rootPane, "Gagal menyimpan resep apotek BPJS !!!!!");
                            }

                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(rootPane, metadata.path("message").asText());
                        }
                    }
                     */
                } catch (Exception ex) {
                    System.out.println(ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void CekObatApotekBPJS(String kode_obat) {
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            requestEntity = new HttpEntity(headers);

            URL = link + "/referensi/obat/" + JnsObat.getSelectedItem().toString().substring(0, 1) + "/" + Valid.SetTgl(TglResep.getSelectedItem() + "") + "/" + kode_obat;

            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            metadata = root.path("metaData");

            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));

            } else {
                JOptionPane.showMessageDialog(rootPane, metadata.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println(ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        Valid.pindah(evt, BtnKeluar, cmbJam);
    }//GEN-LAST:event_DTPTglKeyPressed

    private void cmbJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJamKeyPressed
        Valid.pindah(evt, DTPTgl, cmbMnt);
    }//GEN-LAST:event_cmbJamKeyPressed

    private void cmbMntKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMntKeyPressed
        Valid.pindah(evt, cmbJam, cmbDtk);
    }//GEN-LAST:event_cmbMntKeyPressed

    private void cmbDtkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbDtkKeyPressed
    }//GEN-LAST:event_cmbDtkKeyPressed

    private void ChkJlnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkJlnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkJlnActionPerformed

    private void tbObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbObatPropertyChange
        if (this.isVisible() == true) {
            getDataobat();
        }
    }//GEN-LAST:event_tbObatPropertyChange

    private void tbObatRacikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatRacikanKeyPressed

    }//GEN-LAST:event_tbObatRacikanKeyPressed

    private void tbDetailObatRacikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDetailObatRacikanKeyPressed

    }//GEN-LAST:event_tbDetailObatRacikanKeyPressed

    private void tbDetailObatRacikanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDetailObatRacikanPropertyChange
        if (this.isVisible() == true) {
            getDatadetailobatracikan();
        }
    }//GEN-LAST:event_tbDetailObatRacikanPropertyChange

    private void tbDetailObatRacikanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDetailObatRacikanMouseClicked
        if (tbObat.getRowCount() != 0) {
            try {
                getDatadetailobatracikan();
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_tbDetailObatRacikanMouseClicked

    private void ScrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ScrollMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_ScrollMouseClicked

    private void TglResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglResepKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglResepKeyPressed

    private void TResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TResepKeyPressed

    }//GEN-LAST:event_TResepKeyPressed

    private void TNoRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRMActionPerformed

    private void LahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LahirKeyPressed

    }//GEN-LAST:event_LahirKeyPressed

    private void NoKartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoKartuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoKartuActionPerformed

    private void TglResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TglResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglResepActionPerformed

    private void CariDataObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CariDataObatActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ApotekBPJSDaftarPelayananObat2 resume = new ApotekBPJSDaftarPelayananObat2(null, true);
        resume.setNoRm(NoSEP.getText());
        resume.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        resume.setLocationRelativeTo(internalFrame1);
        resume.setVisible(true);
        resume.tampil();
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_CariDataObatActionPerformed

    private void CariDataObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CariDataObatKeyPressed

    }//GEN-LAST:event_CariDataObatKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Yakin mau dihapus obat " + tbObat.getValueAt(tbObat.getSelectedRow(), 3) + " (" + tbObat.getValueAt(tbObat.getSelectedRow(), 1) + ") ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                tabModeobat.removeRow(tbObat.getSelectedRow());
            }
        }

        if (tbDetailObatRacikan.getSelectedRow() != -1) {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Yakin mau dihapus obat RACIKAN " + tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(), 3) + " (" + tbDetailObatRacikan.getValueAt(tbDetailObatRacikan.getSelectedRow(), 1) + ") ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                tabModeDetailObatRacikan.removeRow(tbDetailObatRacikan.getSelectedRow());
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnKeluar, CariDataObat);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnSimpan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan1ActionPerformed
        // TODO add your handling code here:
        KirimResepKosong();
    }//GEN-LAST:event_BtnSimpan1ActionPerformed

    private void BtnSimpan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan2ActionPerformed
        // TODO add your handling code here:
        InsertObatNonRacikan();
    }//GEN-LAST:event_BtnSimpan2ActionPerformed

    private void BtnSimpan3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan3ActionPerformed
        // TODO add your handling code here:
        InsertObatRacikan();
    }//GEN-LAST:event_BtnSimpan3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ApotekBPJSKirimObat dialog = new ApotekBPJSKirimObat(new javax.swing.JFrame(), true);
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
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpan1;
    private widget.Button BtnSimpan2;
    private widget.Button BtnSimpan3;
    private widget.Button CariDataObat;
    private widget.CekBox ChkJln;
    private widget.Tanggal DTPTgl;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Iterasi;
    private widget.ComboBox JnsObat;
    private widget.TextBox Kd2;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdPj;
    private widget.TextBox KdPoli;
    private widget.TextBox Lahir;
    private widget.TextBox LblNoRawat;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmPoli;
    private widget.TextBox NoKartu;
    private widget.TextBox NoSEP;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TResep;
    private widget.TabPane TabRawat;
    private widget.TextBox Tanggal;
    private widget.Tanggal TglResep;
    private widget.ComboBox cmbDtk;
    private widget.ComboBox cmbJam;
    private widget.ComboBox cmbMnt;
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
    private widget.Label jLabel4;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelisi3;
    private widget.Table tbDetailObatRacikan;
    private widget.Table tbObat;
    private widget.Table tbObatRacikan;
    // End of variables declaration//GEN-END:variables

    public void tampilobatSmc(String noresep) {
        this.noresep = noresep;

        try {
            Valid.tabelKosong(tabModeobat);
            Valid.tabelKosong(tabModeObatRacikan);
            Valid.tabelKosong(tabModeDetailObatRacikan);

            try (PreparedStatement ps = koneksi.prepareStatement(
                "select resep_obat.no_resep, resep_obat.tgl_perawatan, resep_obat.jam, resep_obat.no_rawat, pasien.no_rkm_medis, " +
                "pasien.nm_pasien, resep_obat.kd_dokter, dokter.nm_dokter from resep_obat join reg_periksa on " +
                "resep_obat.no_rawat = reg_periksa.no_rawat join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "join dokter on resep_obat.kd_dokter = dokter.kd_dokter where resep_obat.no_resep = ?"
            )) {
                ps.setString(1, noresep);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select m.kode_brng_apotek_bpjs, m.nama_brng_apotek_bpjs, dpo.jml, o.kode_brng, o.nama_brng " +
                            "from detail_pemberian_obat dpo join maping_obat_apotek_bpjs m on dpo.kode_brng = m.kode_brng " +
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
                                    switch (rs2.getString("jml")) {
                                        case "15":
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                1.0d, 0.5d, 30.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        case "45":
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                1.0d, 1.5d, 30.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        case "60":
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                2.0d, 1.0d, 30.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        case "90":
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                3.0d, 1.0d, 30.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        case "120":
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                4.0d, 1.0d, 30.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        case "3":
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                1.0d, 1.0d, 3.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
                                            });
                                            break;
                                        default:
                                            tabModeobat.addRow(new Object[] {
                                                false, rs2.getDouble("jml"), rs2.getString("kode_brng"), rs2.getString("nama_brng"),
                                                1.0d, 1.0d, 30.0d, rs2.getString("kode_brng_apotek_bpjs"), rs2.getString("nama_brng_apotek_bpjs")
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
                                    tabModeObatRacikan.addRow(new Object[] {
                                        rs2.getString("no_racik"), rs2.getString("nama_racik"), rs2.getString("kd_racik"),
                                        rs2.getString("metode"), rs2.getDouble("jml_dr"), rs2.getString("aturan_pakai"),
                                        rs2.getString("keterangan")
                                    });
                                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                                        "select o.kode_brng, o.nama_brng, dpo.jml, o.kapasitas, m.kode_brng_apotek_bpjs, m.nama_brng_apotek_bpjs from " +
                                        "detail_pemberian_obat dpo join databarang o on dpo.kode_brng = o.kode_brng join maping_obat_apotek_bpjs m on " +
                                        "dpo.kode_brng = m.kode_brng where dpo.tgl_perawatan = ? and dpo.jam = ? and dpo.no_rawat = ? and r.no_racik = ? " +
                                        "and exists(select * from  detail_obat_racikan r where dpo.tgl_perawatan = r.tgl_perawatan and dpo.jam = r.jam " +
                                        "and dpo.no_rawat = r.no_rawat and dpo.kode_brng = r.kode_brng) order by o.kode_brng"
                                    )) {
                                        ps3.setString(1, rs.getString("tgl_perawatan"));
                                        ps3.setString(2, rs.getString("jam"));
                                        ps3.setString(3, rs.getString("no_rawat"));
                                        ps3.setString(4, rs2.getString("no_racik"));
                                        try (ResultSet rs3 = ps3.executeQuery()) {
                                            while (rs3.next()) {
                                                if (rs3.getString("jml").equals("15")) {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        1.0d, 0.5d, 30.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
                                                } else if (rs3.getString("jml").equals("45")) {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        1.0d, 1.5d, 30.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
                                                } else if (rs3.getString("jml").equals("60")) {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        2.0d, 1.0d, 30.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
                                                } else if (rs3.getString("jml").equals("90")) {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        3.0d, 1.0d, 30.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
                                                } else if (rs3.getString("jml").equals("120")) {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        4.0d, 1.0d, 30.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
                                                } else if (rs3.getString("jml").equals("2")) {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        1.0d, 1.0d, 2.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
                                                } else {
                                                    tabModeDetailObatRacikan.addRow(new Object[] {
                                                        rs2.getString("no_racik"), rs3.getDouble("jml"),
                                                        rs3.getString("kode_brng"), rs3.getString("nama_brng"),
                                                        1.0d, 1.0d, 30.0d, rs3.getString("kode_brng_apotek_bpjs"),
                                                        rs3.getString("nama_brng_apotek_bpjs")
                                                    });
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
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void emptTeksobat() {
        Kd2.setText("");
    }

    private void getDataobat() {
        if (tbObat.getSelectedRow() != -1) {
            row = tbObat.getSelectedRow();
            if (!tbObat.getValueAt(row, 1).toString().equals("")) {
                if (Double.parseDouble(tbObat.getValueAt(row, 1).toString()) > 0) {
                    Double.parseDouble(tbObat.getValueAt(row, 1).toString());
                }
            }
        }
    }

    private void getDataobat(int data) {
        Double.valueOf(tbObat.getValueAt(data, 1).toString());
    }

    public JTextField getTextField() {
        return Kd2;
    }

    public JTable getTable() {
        return tbObat;
    }

    public Button getButton() {
        return BtnSimpan;
    }

    public void setNoRm(String norwt, String norm, String nama, String tgljam, String Resep) {
        aktifpcare = "no";
        TNoRw.setText(norwt);
        LblNoRawat.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(nama);
        noresep = "";
        try {
            TglResep.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tgljam));
        } catch (Exception e) {
            TglResep.setDate(new Date());
        }
        KdPj.setText(Sequel.cariIsiSmc("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?", norwt));
        kenaikan = Sequel.cariIsiAngka("select (set_harga_obat_ralan.hargajual/100) from set_harga_obat_ralan where set_harga_obat_ralan.kd_pj=?", KdPj.getText());
        TResep.setText(Resep);

        try (PreparedStatement ps = koneksi.prepareStatement("SELECT no_sep, tanggal_lahir, no_kartu, tglsep, kdpolitujuan, nmpolitujuan, kddpjp, nmdpdjp from bridging_sep where no_rawat = ?")) {
            ps.setString(1, norwt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NoSEP.setText(rs.getString("no_sep"));
                    KdDPJP.setText(rs.getString("kddpjp"));
                    NmDPJP.setText(rs.getString("nmdpdjp"));
                    NoKartu.setText(rs.getString("no_kartu"));
                    KdPoli.setText(rs.getString("kdpolitujuan"));
                    NmPoli.setText(rs.getString("nmpolitujuan"));
                    Lahir.setText(rs.getString("tanggal_lahir"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;

            @Override
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                // Membuat Date
                //Date dt = new Date();
                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if (ChkJln.isSelected() == true) {
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                } else if (ChkJln.isSelected() == false) {
                    nilai_jam = cmbJam.getSelectedIndex();
                    nilai_menit = cmbMnt.getSelectedIndex();
                    nilai_detik = cmbDtk.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                cmbJam.setSelectedItem(jam);
                cmbMnt.setSelectedItem(menit);
                cmbDtk.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }

    public void setDokter(String kodedokter, String namadokter) {
        this.kodedokter = kodedokter;
        this.namadokter = namadokter;
    }

    private void getDatadetailobatracikan() {
        if (tbDetailObatRacikan.getSelectedRow() != -1) {
            row = tbDetailObatRacikan.getSelectedRow();
            try {
                if (Double.parseDouble(tbDetailObatRacikan.getValueAt(row, 1).toString()) > 0) {
                    Double.parseDouble(tbDetailObatRacikan.getValueAt(row, 1).toString());
                }
            } catch (Exception e) {
//                System.out.println("Notif Racikan : "+e);
            }
        }
    }

    private void getDatadetailobatracikan(int data) {
        Double.parseDouble(tbDetailObatRacikan.getValueAt(data, 1).toString());
    }

    private void KirimResepKosong() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (NoSEP.getText().trim().equals("")) {
            Valid.textKosong(NoSEP, "Nomor Sep");
        } else if (KdDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "Dokter");
        } else if (KdPoli.getText().trim().equals("")) {
            Valid.textKosong(KdPoli, "Poliklinik");
        } else if (TResep.getText().trim().equals("")) {
            Valid.textKosong(TResep, "Nomor Resep");
        } else if (Lahir.getText().trim().equals("")) {
            Valid.textKosong(Lahir, "Lahir");
        } else {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                    requestEntity = new HttpEntity(headers);

//                    System.out.println("Tanpa iterasi");
                    URL = link + "/sjpresep/v3/insert";
                    System.out.println(URL);
                    requestJson = "{" //                                    + "\"TGLSJP\": \"" + Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+Jam.getText()+ "\","+
                        +
                         "\"TGLSJP\": \"" + Valid.getTglJamSmc(DTPTgl, cmbJam, cmbMnt, cmbDtk) + "\"," +
                        "\"REFASALSJP\": \"" + NoSEP.getText() + "\"," +
                        "\"POLIRSP\": \"" + KdPoli.getText() + "\"," +
                        "\"KDJNSOBAT\": \"" + JnsObat.getSelectedItem().toString().substring(0, 1) + "\"," +
                        "\"NORESEP\": \"" + TResep.getText() + "\", " +
                        "\"IDUSERSJP\": \"RS_" + akses.getkode() + "\"," +
                        "\"TGLRSP\": \"" + Valid.getTglSmc(TglResep) + " 00:00:00\", " +
                        "\"TGLPELRSP\": \"" + Valid.getTglSmc(TglResep) + " 00:00:00\"," +
                        "\"KdDokter\": \"0\"," +
                        "\"iterasi\":\"" + Iterasi.getSelectedItem().toString().substring(0, 1) + "\"" +
                        "}  ";
                    System.out.println("Resep : " + requestJson);
                    requestEntity = new HttpEntity(requestJson, headers);
                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                    metadata = root.path("metaData");
                    System.out.println("data = " + metadata);
                    System.out.println("error = " + metadata.path("message").asText());
                    if (metadata.path("code").asText().equals("200")) {
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                        System.out.println("Response : " + response);
                        if (Sequel.menyimpantf2("bridging_apotek_bpjs", "?,?,?,?,?,?,?,?,?,?,?,?", "data", 12,
                            new String[] {
                                response.path("noSep_Kunjungan").asText(),
                                response.path("noApotik").asText(),
                                TResep.getText(),
                                Valid.getTglJamSmc(TglResep),
                                Valid.getTglSmc(TglResep),
                                JnsObat.getSelectedItem().toString().substring(0, 1),
                                Iterasi.getSelectedItem().toString().substring(0, 1),
                                KdPoli.getText(),
                                NmPoli.getText(),
                                KdDPJP.getText(),
                                NmDPJP.getText(),
                                akses.getkode(),}) == true) {
                            System.out.println("Simpan No Resep Selesai");
                            JOptionPane.showMessageDialog(null, "Resep Apotek " + response.path("noApotik").asText() + " Berhasil disimpan ");
                            no_apotek = response.path("noApotik").asText();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, " ERROR : " + metadata.path("message").asText());
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        }
    }

    private void InsertObatNonRacikan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (NoSEP.getText().trim().equals("")) {
            Valid.textKosong(NoSEP, "Nomor Sep");
        } else if (KdDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "Dokter");
        } else if (KdPoli.getText().trim().equals("")) {
            Valid.textKosong(KdPoli, "Poliklinik");
        } else if (TResep.getText().trim().equals("")) {
            Valid.textKosong(TResep, "Nomor Resep");
        } else if (Lahir.getText().trim().equals("")) {
            Valid.textKosong(Lahir, "Lahir");
        } else {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                    requestEntity = new HttpEntity(headers);
                    if (Sequel.cariExistsSmc("select * from bridging_apotek_bpjs where bridging_apotek_bpjs.no_sep = ? and bridging_apotek_bpjs.no_resep = ?", NoSEP.getText(), TResep.getText())) {
                        no_apotek = Sequel.cariIsiSmc("select bridging_apotek_bpjs.no_apotek from bridging_apotek_bpjs where bridging_apotek_bpjs.no_sep = ? and bridging_apotek_bpjs.no_resep = ?", NoSEP.getText(), TResep.getText());
                        URL = link + "/obatnonracikan/v3/insert";
                        System.out.println(URL);
                        ObjectNode json;
                        for (int i = 0; i < tabModeobat.getRowCount(); i++) {
                            if (Valid.SetAngka(tabModeobat.getValueAt(i, 1).toString()) > 0) {
                                json = mapper.createObjectNode();
                                json.put("NOSJP", no_apotek);
                                json.put("NORESEP", TResep.getText());
                                json.put("KDOBT", tabModeobat.getValueAt(i, 7).toString());
                                json.put("NMOBAT", tabModeobat.getValueAt(i, 8).toString());
                                json.put("SIGNA1OBT", (Double) tabModeobat.getValueAt(i, 4));
                                json.put("SIGNA2OBT", (Double) tabModeobat.getValueAt(i, 5));
                                json.put("JMLOBT", (Double) tabModeobat.getValueAt(i, 1));
                                json.put("JHO", (Double) tabModeobat.getValueAt(i, 6));
                                json.put("CatKhsObt", "Non racikan");
                                requestJson = json.toString();
                                System.out.println(requestJson);
                                requestEntity = new HttpEntity(requestJson, headers);
                                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                metadata = root.path("metaData");
                                System.out.println("Obat nonracikan ke resep : " + metadata.path("code").asText() + " - " + metadata.path("message").asText());
                                if (metadata.path("code").asText().equals("200")) {
                                    if (!Sequel.menyimpantfSmc("bridging_apotek_bpjs_obat", null,
                                        NoSEP.getText(), TResep.getText(),
                                        tabModeobat.getValueAt(i, 2).toString(),
                                        tabModeobat.getValueAt(i, 3).toString(),
                                        tabModeobat.getValueAt(i, 1).toString(),
                                        tabModeobat.getValueAt(i, 4).toString(),
                                        tabModeobat.getValueAt(i, 5).toString(),
                                        "0", no_apotek
                                    )) {
                                        JOptionPane.showMessageDialog(null, "Gagal menyimpan " + tabModeobat.getValueAt(i, 2).toString() + " - " + tabModeobat.getValueAt(i, 3).toString() + " ke resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Obat " + tabModeobat.getValueAt(i, 2).toString() + " - " + tabModeobat.getValueAt(i, 3).toString() + " diresepkan kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        }
    }

    private void InsertObatRacikan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (NoSEP.getText().trim().equals("")) {
            Valid.textKosong(NoSEP, "Nomor Sep");
        } else if (KdDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "Dokter");
        } else if (KdPoli.getText().trim().equals("")) {
            Valid.textKosong(KdPoli, "Poliklinik");
        } else if (TResep.getText().trim().equals("")) {
            Valid.textKosong(TResep, "Nomor Resep");
        } else if (Lahir.getText().trim().equals("")) {
            Valid.textKosong(Lahir, "Lahir");
        } else {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data yang mau disimpan..?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
                    utc = String.valueOf(api.GetUTCdatetimeAsString());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                    requestEntity = new HttpEntity(headers);

//                cek resep yang sdh ada
                    if (!Sequel.cariIsiSmc("select no_apotek from bridging_apotek_bpjs where no_resep='" + TResep.getText() + "'").isEmpty() && Iterasi.getSelectedIndex() == 0) {
                        URL = link + "/obatracikan/v3/insert";
                        System.out.println(URL);
                        for (int i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                            if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 1).toString()) > 0) {
                                try {
                                    requestJson = "{\n" +
                                        "            \"NOSJP\": \"" + Sequel.cariIsiSmc("select no_apotek from bridging_apotek_bpjs where no_resep='" + TResep.getText() + "'") + "\",\n" +
                                        "            \"NORESEP\": \"" + TResep.getText() + "\",\n" +
                                        "            \"JNSROBT\": \"R.0" + (tbDetailObatRacikan.getValueAt(i, 0).toString()) + "\",\n" +
                                        "            \"KDOBT\": \"" + Sequel.cariIsiSmc("SELECT kode_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                        "            \"NMOBAT\": \"" + Sequel.cariIsiSmc("SELECT nama_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                        "            \"SIGNA1OBT\": " + tbDetailObatRacikan.getValueAt(i, 4).toString() + ",\n" +
                                        "            \"SIGNA2OBT\": " + tbDetailObatRacikan.getValueAt(i, 5).toString() + ",\n" +
                                        "            \"PERMINTAAN\": " + tbDetailObatRacikan.getValueAt(i, 7).toString() + ",\n" +
                                        "            \"JMLOBT\": " + tbDetailObatRacikan.getValueAt(i, 1).toString() + ",\n" +
                                        "            \"JHO\": " + tbDetailObatRacikan.getValueAt(i, 6).toString() + ",\n" +
                                        "            \"CatKhsObt\": \"RACIKAN " + (i + 1) + "\"\n" +
                                        "        }     ";
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("data = " + metadata);
                                    if (metadata.path("code").asText().equals("200")) {
                                        if (Sequel.menyimpantf("bridging_apotek_bpjs_obat", "?,?,?,?,?,?,?,?,?", "Simpan Obat Apotek BPJS Racikan", 9, new String[] {
                                            //                                        response.path("noSep_Kunjungan").asText(),
                                            NoSEP.getText(),
                                            TResep.getText(),
                                            tbDetailObatRacikan.getValueAt(i, 2).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 3).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 1).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 4).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 5).toString(),
                                            "1",
                                            no_apotek = Sequel.cariIsiSmc("select no_apotek from bridging_apotek_bpjs where no_resep='" + TResep.getText() + "'")
                                        }) == true) {
                                            System.out.println("Obat " + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                            JOptionPane.showMessageDialog(null, "Obat racikan" + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                        }
                                    } else {
                                        System.out.println("Obat Gagal Simpan, " + metadata.path("message").asText());
                                        JOptionPane.showMessageDialog(null, "Obat Gagal Simpan, " + metadata.path("message").asText());
                                    }

                                    System.out.println("racikan = \n\n" + requestJson);
                                } catch (Exception ex) {
                                    System.out.println("Notifikasi : " + ex);
                                    if (ex.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                            }
                        }
                    } else if (!Sequel.cariIsiSmc("select no_apotek from bridging_apotek_bpjs where no_resep='" + TResep.getText() + "'").isEmpty() && Iterasi.getSelectedIndex() == 1) {
                        URL = link + "/obatracikan/v3/insert";
                        System.out.println(URL);
                        for (int i = 0; i < tbDetailObatRacikan.getRowCount(); i++) {
                            if (Valid.SetAngka(tbDetailObatRacikan.getValueAt(i, 1).toString()) > 0) {
                                try {
                                    requestJson = "{\n" +
                                        "            \"NOSJP\": \"" + Sequel.cariIsiSmc("select no_apotek from bridging_apotek_bpjs where no_resep='" + TResep.getText() + "'") + "\",\n" +
                                        "            \"NORESEP\": \"" + TResep.getText() + "\",\n" +
                                        "            \"JNSROBT\": \"R.0" + (tbDetailObatRacikan.getValueAt(i, 0).toString()) + "\",\n" +
                                        "            \"KDOBT\": \"" + Sequel.cariIsiSmc("SELECT kode_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                        "            \"NMOBAT\": \"" + Sequel.cariIsiSmc("SELECT nama_brng_apotek_bpjs FROM maping_obat_apotek_bpjs WHERE kode_brng=?", tbDetailObatRacikan.getValueAt(i, 2).toString()) + "\",\n" +
                                        "            \"SIGNA1OBT\": " + tbDetailObatRacikan.getValueAt(i, 4).toString() + ",\n" +
                                        "            \"SIGNA2OBT\": " + tbDetailObatRacikan.getValueAt(i, 5).toString() + ",\n" +
                                        "            \"PERMINTAAN\": " + tbDetailObatRacikan.getValueAt(i, 7).toString() + ",\n" +
                                        "            \"JMLOBT\": " + tbDetailObatRacikan.getValueAt(i, 1).toString() + ",\n" +
                                        "            \"JHO\": " + tbDetailObatRacikan.getValueAt(i, 6).toString() + ",\n" +
                                        "            \"CatKhsObt\": \"RACIKAN " + (i + 1) + "\"\n" +
                                        "        }     ";
                                    requestEntity = new HttpEntity(requestJson, headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println("data = " + metadata);
                                    if (metadata.path("code").asText().equals("200")) {
                                        if (Sequel.menyimpantf("bridging_apotek_bpjs_obat", "?,?,?,?,?,?,?,?,?", "Simpan Obat Apotek BPJS Racikan", 9, new String[] {
                                            //                                        response.path("noSep_Kunjungan").asText(),
                                            NoSEP.getText(),
                                            TResep.getText(),
                                            tbDetailObatRacikan.getValueAt(i, 2).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 3).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 1).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 4).toString(),
                                            tbDetailObatRacikan.getValueAt(i, 5).toString(),
                                            "1",
                                            no_apotek = Sequel.cariIsiSmc("select no_apotek from bridging_apotek_bpjs where no_resep = ?", TResep.getText())
                                        }) == true) {
                                            System.out.println("Obat " + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                            JOptionPane.showMessageDialog(null, "Obat racikan" + tbDetailObatRacikan.getValueAt(i, 3).toString() + " Berhasil disimpan");
                                        }
                                    } else {
                                        System.out.println("Obat Gagal Simpan, " + metadata.path("message").asText());
                                        JOptionPane.showMessageDialog(null, "Obat Gagal Simpan, " + metadata.path("message").asText());
                                    }

                                    System.out.println("racikan = \n\n" + requestJson);
                                } catch (Exception ex) {
                                    System.out.println("Notifikasi : " + ex);
                                    if (ex.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                    }
                }
            }
        }
    }
}
