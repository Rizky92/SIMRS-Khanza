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
import fungsi.batasInput;
import fungsi.koneksiDB;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import inventory.DlgPemberianObat;
import inventory.DlgPiutang;
import inventory.riwayatobat;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import keuangan.Jurnal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSDaftarPelayananObat2SMC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabModeObat, tabModeDetailRacikanObat;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String URLAPIAPOTEKBPJS = koneksiDB.URLAPIAPOTEKBPJS();
    private final ApiApotekBPJS api = new ApiApotekBPJS();
    private final Jurnal jur = new Jurnal();
    private final riwayatobat Trackobat = new riwayatobat();
    private final boolean AKTIFKANBATCHOBAT = koneksiDB.AKTIFKANBATCHOBAT().trim().toLowerCase().equals("yes"),
        AKTIFKANBILLINGPARSIAL = koneksiDB.AKTIFKANBILLINGPARSIAL().trim().toLowerCase().equals("yes"),
        BRIDGINGAPOTEKBPJSPROSESKEUANGANDANSTOKOBAT = koneksiDB.BRIDGINGAPOTEKBPJSPROSESKEUANGANDANSTOKOBAT();
    private String URL = "", utc = "", Suspen_Piutang_Obat_Ranap = "", Obat_Ranap = "", HPP_Obat_Rawat_Inap = "", Persediaan_Obat_Rawat_Inap = "",
        Suspen_Piutang_Obat_Ralan = "", Obat_Ralan = "", HPP_Obat_Rawat_Jalan = "", Persediaan_Obat_Rawat_Jalan = "", Piutang_Obat = "", Kontra_Piutang_Obat = "";
    private HttpHeaders headers;
    private HttpEntity entity;
    private JsonNode root, metadata, response;
    private ObjectNode json;
    private Set<String> kodeobat = new HashSet<>();
    private boolean ispiutang = false, sukses = true;

    /**
     * Creates new form DlgKamar
     *
     * @param parent
     * @param modal
     */
    public ApotekBPJSDaftarPelayananObat2SMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new String[] {
            "No. SEP Apotek", "No. SEP Asal", "No. Resep", "No. RM", "No. Peserta", "Nama Peserta", "Iterasi", "Jenis Obat", "Tgl. Resep", "Tgl. Pelayanan", "Ptg."
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 10) {
                    return Boolean.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        tbKamar.setModel(tabMode);
        tbKamar.getColumnModel().getColumn(0).setPreferredWidth(126);
        tbKamar.getColumnModel().getColumn(1).setPreferredWidth(128);
        tbKamar.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbKamar.getColumnModel().getColumn(3).setPreferredWidth(70);
        tbKamar.getColumnModel().getColumn(4).setPreferredWidth(90);
        tbKamar.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbKamar.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbKamar.getColumnModel().getColumn(7).setPreferredWidth(140);
        tbKamar.getColumnModel().getColumn(8).setPreferredWidth(116);
        tbKamar.getColumnModel().getColumn(9).setPreferredWidth(116);
        tbKamar.getColumnModel().getColumn(10).setMinWidth(25);
        tbKamar.getColumnModel().getColumn(10).setMaxWidth(25);
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeObat = new DefaultTableModel(null, new String[] {
            "P", "Jml.", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Kode Obat BPJS", "Nama Obat BPJS"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 1 || columnIndex == 4 || columnIndex == 5 || columnIndex == 6 || columnIndex == 7 || columnIndex == 8) {
                    return Double.class;
                }

                return String.class;
            }
        };

        tbObat.setModel(tabModeObat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        tbObat.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbObat.getColumnModel().getColumn(1).setPreferredWidth(40);
        tbObat.getColumnModel().getColumn(2).setPreferredWidth(75);
        tbObat.getColumnModel().getColumn(3).setPreferredWidth(250);
        tbObat.getColumnModel().getColumn(4).setPreferredWidth(50);
        tbObat.getColumnModel().getColumn(5).setPreferredWidth(50);
        tbObat.getColumnModel().getColumn(6).setPreferredWidth(70);
        tbObat.getColumnModel().getColumn(7).setPreferredWidth(70);
        tbObat.getColumnModel().getColumn(8).setPreferredWidth(70);
        tbObat.getColumnModel().getColumn(9).setPreferredWidth(90);
        tbObat.getColumnModel().getColumn(10).setPreferredWidth(250);

        tabModeDetailRacikanObat = new DefaultTableModel(null, new Object[] {
            "P", "No. Racik", "Jml.", "Kode Barang", "Nama Barang", "Signa 1", "Signa 2", "Jml. Hari", "Harga", "Subtotal", "Dosis/Sediaan", "Kode Obat BPJS", "Nama Obat BPJS"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 2 || columnIndex == 5 || columnIndex == 6 || columnIndex == 7 || columnIndex == 8 || columnIndex == 9) {
                    return Double.class;
                }

                return String.class;
            }
        };

        tbDetailRacikanObat.setModel(tabModeDetailRacikanObat);
        tbDetailRacikanObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDetailRacikanObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbDetailRacikanObat.setDefaultRenderer(Object.class, new WarnaTable());
        tbDetailRacikanObat.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbDetailRacikanObat.getColumnModel().getColumn(1).setPreferredWidth(60);
        tbDetailRacikanObat.getColumnModel().getColumn(2).setPreferredWidth(40);
        tbDetailRacikanObat.getColumnModel().getColumn(3).setPreferredWidth(75);
        tbDetailRacikanObat.getColumnModel().getColumn(4).setPreferredWidth(200);
        tbDetailRacikanObat.getColumnModel().getColumn(5).setPreferredWidth(50);
        tbDetailRacikanObat.getColumnModel().getColumn(6).setPreferredWidth(50);
        tbDetailRacikanObat.getColumnModel().getColumn(7).setPreferredWidth(70);
        tbDetailRacikanObat.getColumnModel().getColumn(8).setPreferredWidth(70);
        tbDetailRacikanObat.getColumnModel().getColumn(9).setPreferredWidth(70);
        tbDetailRacikanObat.getColumnModel().getColumn(10).setPreferredWidth(100);
        tbDetailRacikanObat.getColumnModel().getColumn(11).setPreferredWidth(90);
        tbDetailRacikanObat.getColumnModel().getColumn(12).setPreferredWidth(250);

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }

        try (ResultSet rs = koneksi.createStatement().executeQuery(
            "select set_akun_ralan.Suspen_Piutang_Obat_Ralan, set_akun_ralan.Obat_Ralan, " +
            "set_akun_ralan.HPP_Obat_Rawat_Jalan, set_akun_ralan.Persediaan_Obat_Rawat_Jalan " +
            "from set_akun_ralan"
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
            "set_akun_ranap.HPP_Obat_Rawat_Inap, set_akun_ranap.Persediaan_Obat_Rawat_Inap " +
            "from set_akun_ranap"
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

        try (ResultSet rs = koneksi.createStatement().executeQuery("select set_akun.Piutang_Obat, set_akun.Kontra_Piutang_obat from set_akun")) {
            if (rs.next()) {
                Piutang_Obat = rs.getString("Piutang_Obat");
                Kontra_Piutang_Obat = rs.getString("Kontra_Piutang_Obat");
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        isForm();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppMenu = new javax.swing.JPopupMenu();
        MnUbahResep = new javax.swing.JMenuItem();
        MnCopyResep = new javax.swing.JMenuItem();
        MnPemberianObat = new javax.swing.JMenuItem();
        MnPiutangObat = new javax.swing.JMenuItem();
        ppCek = new javax.swing.JPopupMenu();
        ppPilihSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        PanelAccor = new widget.panelisi();
        ChkAccor = new widget.CekBox();
        FormMenu = new widget.panelisi();
        scrollPane1 = new widget.ScrollPane();
        tbObat = new widget.Table();
        scrollPane2 = new widget.ScrollPane();
        tbDetailRacikanObat = new widget.Table();
        panelGlass6 = new widget.panelisi();
        jLabel18 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        jLabel17 = new widget.Label();
        BtnHapus = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();

        ppMenu.setForeground(new java.awt.Color(50, 50, 50));
        ppMenu.setName("ppMenu"); // NOI18N

        MnUbahResep.setBackground(new java.awt.Color(255, 255, 254));
        MnUbahResep.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnUbahResep.setForeground(new java.awt.Color(50, 50, 50));
        MnUbahResep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnUbahResep.setText("Ubah Resep");
        MnUbahResep.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnUbahResep.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnUbahResep.setName("MnUbahResep"); // NOI18N
        MnUbahResep.setPreferredSize(new java.awt.Dimension(200, 26));
        MnUbahResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnUbahResepActionPerformed(evt);
            }
        });
        ppMenu.add(MnUbahResep);

        MnCopyResep.setBackground(new java.awt.Color(255, 255, 254));
        MnCopyResep.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCopyResep.setForeground(new java.awt.Color(50, 50, 50));
        MnCopyResep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCopyResep.setText("Copy Resep");
        MnCopyResep.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCopyResep.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCopyResep.setName("MnCopyResep"); // NOI18N
        MnCopyResep.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCopyResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCopyResepActionPerformed(evt);
            }
        });
        ppMenu.add(MnCopyResep);

        MnPemberianObat.setBackground(new java.awt.Color(255, 255, 254));
        MnPemberianObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPemberianObat.setForeground(new java.awt.Color(50, 50, 50));
        MnPemberianObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPemberianObat.setText("Piutang Obat");
        MnPemberianObat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPemberianObat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPemberianObat.setName("MnPemberianObat"); // NOI18N
        MnPemberianObat.setPreferredSize(new java.awt.Dimension(200, 26));
        MnPemberianObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPemberianObatActionPerformed(evt);
            }
        });
        ppMenu.add(MnPemberianObat);

        MnPiutangObat.setBackground(new java.awt.Color(255, 255, 254));
        MnPiutangObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPiutangObat.setForeground(new java.awt.Color(50, 50, 50));
        MnPiutangObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPiutangObat.setText("Piutang Obat");
        MnPiutangObat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPiutangObat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPiutangObat.setName("MnPiutangObat"); // NOI18N
        MnPiutangObat.setPreferredSize(new java.awt.Dimension(200, 26));
        MnPiutangObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPiutangObatActionPerformed(evt);
            }
        });
        ppMenu.add(MnPiutangObat);

        ppCek.setForeground(new java.awt.Color(50, 50, 50));
        ppCek.setName("ppCek"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(200, 26));
        ppPilihSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPilihSemuaActionPerformed(evt);
            }
        });
        ppCek.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Semua");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 26));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        ppCek.add(ppBersihkan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Obat Apotek BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbKamar.setComponentPopupMenu(ppMenu);
        tbKamar.setName("tbKamar"); // NOI18N
        tbKamar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbKamarMouseReleased(evt);
            }
        });
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(815, 808));
        PanelAccor.setLayout(new java.awt.BorderLayout());

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(250, 255, 248)));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
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
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.LINE_START);

        FormMenu.setName("FormMenu"); // NOI18N
        FormMenu.setLayout(new javax.swing.BoxLayout(FormMenu, javax.swing.BoxLayout.PAGE_AXIS));

        scrollPane1.setName("scrollPane1"); // NOI18N

        tbObat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbObat.setComponentPopupMenu(ppCek);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.getTableHeader().setReorderingAllowed(false);
        scrollPane1.setViewportView(tbObat);

        FormMenu.add(scrollPane1);

        scrollPane2.setName("scrollPane2"); // NOI18N

        tbDetailRacikanObat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbDetailRacikanObat.setComponentPopupMenu(ppCek);
        tbDetailRacikanObat.setName("tbDetailRacikanObat"); // NOI18N
        tbDetailRacikanObat.getTableHeader().setReorderingAllowed(false);
        scrollPane2.setViewportView(tbDetailRacikanObat);

        FormMenu.add(scrollPane2);

        PanelAccor.add(FormMenu, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.LINE_END);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel18.setText("Tgl. Pelayanan :");
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(79, 23));
        panelGlass6.add(jLabel18);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-12-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass6.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass6.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-12-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass6.add(DTPCari2);

        jLabel16.setText("Keyword :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass6.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass6.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
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
        panelGlass6.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass6.add(jLabel7);

        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass6.add(LCount);

        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass6.add(jLabel17);

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
        panelGlass6.add(BtnHapus);

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
        panelGlass6.add(BtnPrint);

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
        panelGlass6.add(BtnKeluar);

        internalFrame1.add(panelGlass6, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        JOptionPane.showMessageDialog(null, "Maaf, fitur ini masih belum didukung..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        /*
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            //TCari.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.queryu("delete from temporary where temp37='" + akses.getalamatip() + "'");
            for (int i = 0; i < tabMode.getRowCount(); i++) {
                Sequel.menyimpan("temporary", "'" + i + "','" +
                    tabMode.getValueAt(i, 0).toString() + "','" +
                    tabMode.getValueAt(i, 1).toString() + "','" +
                    tabMode.getValueAt(i, 2).toString() + "','" +
                    tabMode.getValueAt(i, 3).toString() + "','" +
                    tabMode.getValueAt(i, 4).toString() + "','" +
                    tabMode.getValueAt(i, 5).toString() + "','" +
                    tabMode.getValueAt(i, 6).toString() + "','" +
                    tabMode.getValueAt(i, 7).toString() + "','" +
                    tabMode.getValueAt(i, 8).toString() + "','" +
                    tabMode.getValueAt(i, 9).toString() + "','" +
                    tabMode.getValueAt(i, 10).toString() + "','" +
                    tabMode.getValueAt(i, 11).toString() + "','" +
                    tabMode.getValueAt(i, 12).toString() + "','" +
                    tabMode.getValueAt(i, 14).toString() + "','" +
                    tabMode.getValueAt(i, 15).toString() + "','" +
                    tabMode.getValueAt(i, 16).toString() + "','','','','','','','','','','','','','','','','','','','','','" + akses.getalamatip() + "'", "Daftar Pelayanan Obat Apotek BPJS");
            }

            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            //param.put("peserta","No.Peserta : "+NoKartu.getText()+" Nama Peserta : "+NamaPasien.getText());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptApotekBPJSDaftarPelayananKlaim.jasper", "report", "[ Daftar Pelayanan Apotek BPJS ]", "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
        */
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
            BtnPrint.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            tampil();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            BtnCariActionPerformed(null);
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tampil();
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbKamar.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tabel masih kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (tbKamar.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih dulu data resep yang mau dihapus..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            if (cekStatusKlaim()) {
                JOptionPane.showMessageDialog(null, "No. SEP Apotek sudah dilakukan verifikasi klaim..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else if (Boolean.parseBoolean(tbKamar.getValueAt(tbKamar.getSelectedRow(), 10).toString()) && Sequel.cariExistsSmc("select * from bayar_piutang where bayar_piutang.no_rawat = ? and bayar_piutang.no_rkm_medis = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbKamar.getValueAt(tbKamar.getSelectedRow(), 3).toString())) {
                JOptionPane.showMessageDialog(null, "Piutang obat sudah terverifikasi..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else if (Sequel.cariRegistrasi(Sequel.cariIsiSmc("select bridging_sep.no_rawat from bridging_sep where bridging_sep.no_sep = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 1).toString())) > 0) {
                JOptionPane.showMessageDialog(null, "Billing sudah terverifikasi..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                hapus();
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void MnUbahResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnUbahResepActionPerformed
        if (tbKamar.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tabel masih kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (tbKamar.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih dulu data yang mau dilakukan ubah resep..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ApotekBPJSEditResepObatSMC form = new ApotekBPJSEditResepObatSMC(null, false);
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            form.tampilresep(tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
            form.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnUbahResepActionPerformed

    private void MnCopyResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCopyResepActionPerformed
        if (tbKamar.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tabel masih kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (tbKamar.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih dulu data yang mau dilakukan copy resep..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ApotekBPJSKirimResepObatSMC apol = new ApotekBPJSKirimResepObatSMC(null, false);
            apol.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            apol.setLocationRelativeTo(internalFrame1);
            apol.tampiCopyResepSmc(tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbKamar.getValueAt(tbKamar.getSelectedRow(), 2).toString());
            apol.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCopyResepActionPerformed

    private void MnPiutangObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPiutangObatActionPerformed
        if (tbKamar.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tabel masih kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            DlgPiutang form = new DlgPiutang(null, false);
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            form.isCek();
            form.tampilResepApotekBPJSSmc(tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbKamar.getValueAt(tbKamar.getSelectedRow(), 2).toString());
            form.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnPiutangObatActionPerformed

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        isForm();
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void MnPemberianObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPemberianObatActionPerformed
        if (tbKamar.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tabel masih kosong..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if ((Boolean) tbKamar.getValueAt(tbKamar.getSelectedRow(), 10)) {
            JOptionPane.showMessageDialog(null, "Maaf, resep ini sudah menjadi piutang obat..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            DlgPemberianObat form = new DlgPemberianObat(null, false);
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            String norawat = Sequel.cariIsiSmc("select bridging_sep.no_rawat from bridging_sep where bridging_sep.no_sep = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 1).toString());
            Date tglawal = Sequel.cariTglSmc("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat = ?", norawat);
            form.isCek();
            form.setNoRm3(norawat, tglawal, new Date(), Sequel.cariIsiSmc("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat = ?", norawat));
            form.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnPemberianObatActionPerformed

    private void ppPilihSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPilihSemuaActionPerformed
        JTable invoker = (JTable) ppCek.getInvoker();
        if (invoker == tbObat) {
            for (int i = 0; i < tabModeObat.getRowCount(); i++) {
                tabModeObat.setValueAt(true, i, 0);
            }
        } else if (invoker == tbDetailRacikanObat) {
            for (int i = 0; i < tabModeDetailRacikanObat.getRowCount(); i++) {
                tabModeDetailRacikanObat.setValueAt(true, i, 0);
            }
        }
    }//GEN-LAST:event_ppPilihSemuaActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        JTable invoker = (JTable) ppCek.getInvoker();
        if (invoker == tbObat) {
            for (int i = 0; i < tabModeObat.getRowCount(); i++) {
                tabModeObat.setValueAt(false, i, 0);
            }
        } else if (invoker == tbDetailRacikanObat) {
            for (int i = 0; i < tabModeDetailRacikanObat.getRowCount(); i++) {
                tabModeDetailRacikanObat.setValueAt(false, i, 0);
            }
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void tbKamarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbKamarMouseReleased
        if (tbKamar.getRowCount() > 0) {
            tbKamar.changeSelection(tbKamar.rowAtPoint(evt.getPoint()), tbKamar.columnAtPoint(evt.getPoint()), false, false);
            getData();
            ChkAccor.setSelected(true);
            isForm();
        }
    }//GEN-LAST:event_tbKamarMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ApotekBPJSDaftarPelayananObat2SMC dialog = new ApotekBPJSDaftarPelayananObat2SMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.CekBox ChkAccor;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.panelisi FormMenu;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnCopyResep;
    private javax.swing.JMenuItem MnPemberianObat;
    private javax.swing.JMenuItem MnPiutangObat;
    private javax.swing.JMenuItem MnUbahResep;
    private widget.panelisi PanelAccor;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel21;
    private widget.Label jLabel7;
    private widget.panelisi panelGlass6;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JPopupMenu ppCek;
    private javax.swing.JPopupMenu ppMenu;
    private javax.swing.JMenuItem ppPilihSemua;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.Table tbDetailRacikanObat;
    private widget.Table tbKamar;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select a.no_sjp, a.no_sep, a.no_resep, s.nomr, s.no_kartu, s.nama_pasien, a.iterasi, a.jenis_obat, a.tgl_resep, " +
            "a.tgl_pelayanan, exists(select * from piutang p where p.nota_piutang = a.no_sjp) as is_piutang from " +
            "bridging_apotek_bpjs a join bridging_sep s on a.no_sep = s.no_sep where a.tgl_pelayanan between ? and ? " +
            (TCari.getText().isBlank() ? "" : "and (a.no_sjp like ? or a.no_sep like ? or a.no_resep like ? or " +
            "s.nomr like ? or s.no_kartu like ? or s.nama_pasien like ?) ") + "order by a.tgl_pelayanan"
        )) {
            int p = 0;
            ps.setString(++p, Valid.getTglSmc(DTPCari1) + " 00:00:00.000");
            ps.setString(++p, Valid.getTglSmc(DTPCari2) + " 23:59:59.999");
            if (!TCari.getText().isBlank()) {
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
                ps.setString(++p, "%" + TCari.getText().trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                String iterasi = "", jenisobat = "";
                while (rs.next()) {
                    switch (rs.getString("iterasi")) {
                        case "0":
                            iterasi = "0. Tanpa Iterasi";
                            break;
                        case "1":
                            iterasi = "1. Iterasi 1";
                            break;
                        case "2":
                            iterasi = "2. Iterasi 2";
                            break;
                    }
                    switch (rs.getString("jenis_obat")) {
                        case "1":
                            jenisobat = "1. Obat PRB";
                            break;
                        case "2":
                            jenisobat = "2. Obat Kronis Belum Stabil";
                            break;
                        case "3":
                            jenisobat = "3. Obat Kemoterapi";
                            break;
                    }
                    tabMode.addRow(new Object[] {
                        rs.getString("no_sjp"), rs.getString("no_sep"), rs.getString("no_resep"), rs.getString("nomr"),
                        rs.getString("no_kartu"), rs.getString("nama_pasien"), iterasi, jenisobat, rs.getString("tgl_resep"),
                        rs.getString("tgl_pelayanan"), rs.getBoolean("is_piutang")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public JTable getTable() {
        return tbKamar;
    }

    public void setTgl(String tgl) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DTPCari1.setDate(sdf.parse(tgl));
            DTPCari2.setDate(new Date());
        } catch (Exception e) {
        }
    }

    public void setCari(String nosep) {
        TCari.setText(nosep);
        tampil();
    }

    public void isCek() {
        MnUbahResep.setEnabled(akses.getbpjs_edit_kirim_obat_smc());
        MnCopyResep.setEnabled(akses.getbpjs_kirim_obat_smc());
        MnPemberianObat.setEnabled(akses.getberi_obat());
        MnPiutangObat.setEnabled(akses.getpiutang_obat());
        BtnHapus.setEnabled(akses.getbpjs_edit_kirim_obat_smc());
    }

    private void getData() {
        if (tbKamar.getSelectedRow() >= 0) {
            Valid.tabelKosong(tabModeObat);
            Valid.tabelKosong(tabModeDetailRacikanObat);
            kodeobat.clear();
            try {
                try (PreparedStatement ps = koneksi.prepareStatement(
                    "select a.no_racik, a.jumlah, m.kode_brng, o.nama_brng, a.signa1, a.signa2, a.jml_hari, a.harga, a.subtotal, " +
                    "a.kandungan, a.kode_brng_apotek_bpjs, a.nama_brng_apotek_bpjs from bridging_apotek_bpjs_obat a left join " +
                    "maping_obat_apotek_bpjs m on a.kode_brng_apotek_bpjs = m.kode_brng_apotek_bpjs left join databarang o on " +
                    "m.kode_brng = o.kode_brng where a.no_sjp = ?"
                )) {
                    ps.setString(1, tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            if (rs.getString("no_racik") == null) {
                                tabModeObat.addRow(new Object[] {
                                    false, rs.getDouble("jumlah"), rs.getString("kode_brng"), rs.getString("nama_brng"), rs.getDouble("signa1"),
                                    rs.getDouble("signa2"), rs.getDouble("jml_hari"), rs.getDouble("harga"), rs.getDouble("subtotal"),
                                    rs.getString("kode_brng_apotek_bpjs"), rs.getString("nama_brng_apotek_bpjs")
                                });
                            } else {
                                tabModeDetailRacikanObat.addRow(new Object[] {
                                    false, rs.getString("no_racik"), rs.getDouble("jumlah"), rs.getString("kode_brng"), rs.getString("nama_brng"),
                                    rs.getDouble("signa1"), rs.getDouble("signa2"), rs.getDouble("jml_hari"), rs.getDouble("harga"), rs.getDouble("subtotal"),
                                    rs.getString("kandungan"), rs.getString("kode_brng_apotek_bpjs"), rs.getString("nama_brng_apotek_bpjs")
                                });
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private boolean cekStatusKlaim() {
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = api.getUTCDateTime();
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            entity = new HttpEntity(headers);

            String bln = tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(5, 7),
                   thn = tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(0, 4);
            if (bln.startsWith("0")) {
                bln = bln.substring(1, 2);
            }

            URL = URLAPIAPOTEKBPJS + "/monitoring/klaim/" + bln + "/" + thn + "/" + tbKamar.getValueAt(tbKamar.getSelectedRow(), 8).toString().substring(0, 1) + "/1";
            System.out.println(URL);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println(metadata.toString());
            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                if (response.path("listsep").isArray()) {
                    String nosepapotek = "";
                    for (JsonNode list : response.path("listsep")) {
                        nosepapotek = list.path("nosepapotek").asText("");
                        if (nosepapotek != null && !nosepapotek.isBlank() && nosepapotek.equals(tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }

            if (JOptionPane.showConfirmDialog(null, "Tidak dapat mengecek status verifikasi resep, tetap lanjutkan?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return true;
            }
        }

        return false;
    }

    private void hapus() {
        ispiutang = Boolean.parseBoolean(tbKamar.getValueAt(tbKamar.getSelectedRow(), 10).toString());
        sukses = true;
        if (tabModeObat.getRowCount() > 0 || tabModeDetailRacikanObat.getRowCount() > 0) {
            if (JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus obat-obat yang dipilih?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                hapusObat();
                hapusObatRacikan();
            }
        }

        if (sukses) {
            if (tabModeObat.getRowCount() == 0 && tabModeDetailRacikanObat.getRowCount() == 0) {
                hapusResep();
            }
        }
    }

    private void hapusResep() {
        if (JOptionPane.showConfirmDialog(null, "Hapus no. apotek " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString() + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIAPOTEKBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
                URL = URLAPIAPOTEKBPJS + "/hapusresep";
                json = mapper.createObjectNode();
                json.put("nosjp", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
                json.put("refasalsjp", tbKamar.getValueAt(tbKamar.getSelectedRow(), 1).toString());
                json.put("noresep", tbKamar.getValueAt(tbKamar.getSelectedRow(), 2).toString());
                System.out.println(json.toString());
                entity = new HttpEntity(json.toString(), headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.DELETE, entity, String.class).getBody());
                metadata = root.path("metaData");
                System.out.println("code : " + metadata.path("code").asText());
                System.out.println("message : " + metadata.path("message").asText());
                if (metadata.path("code").asText().equals("200")) {
                    Sequel.menghapusSmc("bridging_apotek_bpjs", "no_sjp = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
                    JOptionPane.showMessageDialog(null, "Resep " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString() + " Berhasil diHapus");
                    tampil();
                } else {
                    JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                if (e.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus obat racikan..!!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void hapusObat() {
        if (tbObat.getRowCount() > 0) {
            for (int i = tbObat.getRowCount() - 1; i >= 0; i--) {
                if ((Boolean) tbObat.getValueAt(i, 0)) {
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
                        json = mapper.createObjectNode();
                        json.put("nosepapotek", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
                        json.put("noresep", tbKamar.getValueAt(tbKamar.getSelectedRow(), 2).toString());
                        json.put("kodeobat", tbObat.getValueAt(i, 9).toString());
                        json.put("tipeobat", "N");
                        System.out.println(mapper.writeValueAsString(json));
                        entity = new HttpEntity(mapper.writeValueAsString(json), headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.DELETE, entity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("Hapus obat " + tbObat.getValueAt(i, 9).toString() + " " + tbObat.getValueAt(i, 10).toString() + " : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                        if (metadata.path("code").asText().equals("200")) {
                            if (Sequel.menghapustfSmc("bridging_apotek_bpjs_obat", "no_sjp = ? and kode_brng_apotek_bpjs = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbObat.getValueAt(i, 9).toString())) {
                                kodeobat.add(tabModeObat.getValueAt(i, 2).toString());
                                tabModeObat.removeRow(i);
                            } else {
                                JOptionPane.showMessageDialog(null, "Gagal menghapus " + tbObat.getValueAt(i, 2).toString() + " - " + tbObat.getValueAt(i, 3).toString() + " dari resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            if (metadata.path("message").asText().contains("tidak ditemukan")) {
                                if (JOptionPane.showConfirmDialog(null, "Obat " + tbObat.getValueAt(i, 2).toString() + " - " + tbObat.getValueAt(i, 3).toString() + " " + metadata.path("message").asText() + ",\napakah anda ingin menghapus obat ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                    if (Sequel.menghapustfSmc("bridging_apotek_bpjs_obat", "no_sjp = ? and kode_brng_apotek_bpjs = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbObat.getValueAt(i, 9).toString())) {
                                        kodeobat.add(tabModeObat.getValueAt(i, 2).toString());
                                        tabModeObat.removeRow(i);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Gagal menghapus " + tbObat.getValueAt(i, 2).toString() + " - " + tbObat.getValueAt(i, 3).toString() + " dari resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                        if (e.toString().contains("UnknownHostException")) {
                            JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus obat racikan..!!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }

    private void hapusObatRacikan() {
        if (tbDetailRacikanObat.getRowCount() > 0) {
            for (int i = tbDetailRacikanObat.getRowCount() - 1; i >= 0; i--) {
                if ((Boolean) tbDetailRacikanObat.getValueAt(i, 0)) {
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
                        json = mapper.createObjectNode();
                        json.put("nosepapotek", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
                        json.put("noresep", tbKamar.getValueAt(tbKamar.getSelectedRow(), 2).toString());
                        json.put("kodeobat", tbDetailRacikanObat.getValueAt(i, 11).toString());
                        json.put("tipeobat", "R." + Valid.padleftSmc(tbDetailRacikanObat.getValueAt(i, 1).toString(), 2, '0'));
                        System.out.println(mapper.writeValueAsString(json));
                        entity = new HttpEntity(mapper.writeValueAsString(json), headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.DELETE, entity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println("Hapus obat " + tbDetailRacikanObat.getValueAt(i, 11).toString() + " " + tbDetailRacikanObat.getValueAt(i, 12).toString() + " : " + metadata.path("code").asText() + " " + metadata.path("message").asText());
                        if (metadata.path("code").asText().equals("200")) {
                            if (Sequel.menghapustfSmc("bridging_apotek_bpjs_obat", "no_sjp = ? and kode_brng_apotek_bpjs = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbDetailRacikanObat.getValueAt(i, 11).toString())) {
                                kodeobat.add(tabModeDetailRacikanObat.getValueAt(i, 3).toString());
                                tabModeDetailRacikanObat.removeRow(i);
                            } else {
                                JOptionPane.showMessageDialog(null, "Gagal menghapus " + tbDetailRacikanObat.getValueAt(i, 3).toString() + " - " + tbDetailRacikanObat.getValueAt(i, 4).toString() + " dari resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            if (metadata.path("message").asText().contains("tidak ditemukan")) {
                                if (JOptionPane.showConfirmDialog(null, "Obat " + tbDetailRacikanObat.getValueAt(i, 3).toString() + " - " + tbDetailRacikanObat.getValueAt(i, 4).toString() + " " + metadata.path("message").asText() + ",\napakah anda ingin menghapus obat ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                    if (Sequel.menghapustfSmc("bridging_apotek_bpjs_obat", "no_sjp = ? and kode_brng_apotek_bpjs = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString(), tbDetailRacikanObat.getValueAt(i, 11).toString())) {
                                        kodeobat.add(tabModeDetailRacikanObat.getValueAt(i, 3).toString());
                                        tabModeDetailRacikanObat.removeRow(i);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Gagal menghapus " + tbDetailRacikanObat.getValueAt(i, 3).toString() + " - " + tbDetailRacikanObat.getValueAt(i, 4).toString() + " dari resep..!!", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                        if (e.toString().contains("UnknownHostException")) {
                            JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat menghapus obat racikan..!!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }

    private void prosesHapusPemberianObat() {
        if (this.ispiutang) return;
        
        String obat = kodeobat.stream().map(it -> "'" + it.replaceAll("'", "''") + "'").collect(Collectors.joining(", "));

        if (!obat.isBlank()) {
            obat = "and detail_pemberian_obat.kode_brng in (" + obat + ") ";
        } else {
            sukses = false;
        }

        if (sukses) {
            String norawat = Sequel.cariIsiSmc("select bridging_sep.no_rawat from bridging_sep where bridging_sep.no_sep = ?", tbKamar.getValueAt(tbKamar.getSelectedRow(), 1).toString());
            String statusberi = "";
            double ttlhpp = 0, ttljual = 0;

            try (PreparedStatement ps = koneksi.prepareStatement(
                "select detail_pemberian_obat.kode_brng, detail_pemberian_obat.no_batch, detail_pemberian_obat.no_faktur, detail_pemberian_obat.status as statusberi, detail_pemberian_obat.jml, " +
                "ifnull(detail_pemberian_obat.h_beli * detail_pemberian_obat.jml, 0) as hpp, detail_pemberian_obat.total as jual, detail_pemberian_obat.kd_bangsal from detail_pemberian_obat " +
                "where detail_pemberian_obat.no_rawat = ? " + obat + "and detail_pemberian_obat.tgl_perawatan = ? and detail_pemberian_obat.jam = ?"
            )) {
                int p = 0;
                ps.setString(++p, norawat);
                ps.setString(++p, tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(0, 10));
                ps.setString(++p, tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(11, 19));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Sequel.AutoComitFalse();
                        statusberi = rs.getString("statusberi");
                        do {
                            if (Sequel.menghapustfSmc("detail_pemberian_obat", "no_rawat = ? and kode_brng = ? and tgl_perawatan = ? and jam = ? and no_batch = ? " +
                                "and no_faktur = ?", norawat, rs.getString("kode_brng"), tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(0, 10),
                                tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(11, 19), rs.getString("no_batch"), rs.getString("no_faktur")
                            )) {
                                ttlhpp += rs.getDouble("hpp");
                                ttljual += rs.getDouble("jual");
                                Sequel.menghapusSmc("aturan_pakai", "no_rawat = ? and kode_brng = ? and tgl_perawatan = ? and jam = ? and not exists(select * from detail_pemberian_obat " +
                                    "where detail_pemberian_obat.no_rawat = aturan_pakai.no_rawat and detail_pemberian_obat.kode_brng = aturan_pakai.kode_brng and " +
                                    "detail_pemberian_obat.tgl_perawatan = aturan_pakai.tgl_perawatan and detail_pemberian_obat.jam = aturan_pakai.jam)", norawat,
                                    rs.getString("kode_brng"), tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(0, 10),
                                    tbKamar.getValueAt(tbKamar.getSelectedRow(), 9).toString().substring(11, 19));

                                if (AKTIFKANBATCHOBAT) {
                                    Sequel.mengupdateSmc("data_batch", "sisa = sisa + ?", "no_batch = ? and kode_brng = ? and no_faktur = ?",
                                        rs.getString("jml"), rs.getString("no_batch"), rs.getString("kode_brng"), rs.getString("no_faktur")
                                    );

                                    Trackobat.catatRiwayat(rs.getString("kode_brng"), rs.getDouble("jml"), 0, "Pemberian Obat", akses.getkode(), rs.getString("kd_bangsal"), "Hapus",
                                        rs.getString("no_batch"), rs.getString("no_faktur"), norawat + " " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 3).toString() + " " +
                                        tbKamar.getValueAt(tbKamar.getSelectedRow(), 4).toString());

                                    if (!Sequel.menyimpantfSmc("gudangbarang", null, rs.getString("kode_brng"), rs.getString("kd_bangsal"),
                                        rs.getString("jml"), rs.getString("no_batch"), rs.getString("no_faktur")
                                    )) {
                                        if (!Sequel.mengupdatetfSmc("gudangbarang", "stok = stok + ?", "kode_brng = ? and kd_bangsal = ? and no_batch = ? and no_faktur = ?",
                                            rs.getString("jml"), rs.getString("kode_brng"), rs.getString("kd_bangsal"), rs.getString("no_batch"), rs.getString("no_faktur")
                                        )) {
                                            sukses = false;
                                        }
                                    }
                                } else {
                                    Trackobat.catatRiwayat(rs.getString("kode_brng"), rs.getDouble("jml"), 0, "Pemberian Obat", akses.getkode(), rs.getString("kd_bangsal"), "Hapus", "", "",
                                        norawat + " " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 3).toString() + " " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 4).toString());

                                    if (!Sequel.menyimpantfSmc("gudangbarang", null, rs.getString("kode_brng"), rs.getString("kd_bangsal"), rs.getString("jml"), "", "")) {
                                        if (!Sequel.mengupdatetfSmc("gudangbarang", "stok = stok + ?", "kode_brng = ? and kd_bangsal = ? and no_batch = '' and no_faktur = ''",
                                            rs.getString("jml"), rs.getString("kode_brng"), rs.getString("kd_bangsal")
                                        )) {
                                            sukses = false;
                                        }
                                    }
                                }
                            } else {
                                sukses = false;
                            }
                        } while (sukses && rs.next());

                        if (sukses) {
                            if (statusberi.equals("Ranap")) {
                                Sequel.deleteTampJurnal();
                                if (ttljual > 0) {
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(Suspen_Piutang_Obat_Ranap, "Suspen Piutang Obat Ranap", 0, ttljual);
                                    }
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(Obat_Ranap, "Pendapatan Obat Rawat Inap", ttljual, 0);
                                    }
                                }
                                if (ttlhpp > 0) {
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(HPP_Obat_Rawat_Inap, "HPP Persediaan Obat Rawat Inap", 0, ttlhpp);
                                    }
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(Persediaan_Obat_Rawat_Inap, "Persediaan Obat Rawat Inap", ttlhpp, 0);
                                    }
                                }
                                if (sukses) {
                                    sukses = jur.simpanJurnal(norawat, "U", "PEMBATALAN PEMBERIAN OBAT RAWAT INAP PASIEN " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 3).toString() + " " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 4).toString() + " OLEH " + akses.getkode());
                                }
                            } else if (statusberi.equals("Ralan")) {
                                Sequel.deleteTampJurnal();
                                if (ttljual > 0) {
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(Suspen_Piutang_Obat_Ralan, "Suspen Piutang Obat Ralan", 0, ttljual);
                                    }
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(Obat_Ralan, "Pendapatan Obat Rawat Jalan", ttljual, 0);
                                    }
                                }
                                if (ttlhpp > 0) {
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(HPP_Obat_Rawat_Jalan, "HPP Persediaan Obat Rawat Jalan", 0, ttlhpp);
                                    }
                                    if (sukses) {
                                        sukses = Sequel.insertTampJurnal(Persediaan_Obat_Rawat_Jalan, "Persediaan Obat Rawat Jalan", ttlhpp, 0);
                                    }
                                }
                                if (sukses) {
                                    sukses = jur.simpanJurnal(norawat, "U", "PEMBATALAN PEMBERIAN OBAT RAWAT JALAN PASIEN " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 3).toString() + " " + tbKamar.getValueAt(tbKamar.getSelectedRow(), 4).toString() + " OLEH " + akses.getkode());
                                }
                            } else {
                                sukses = false;
                            }
                        }
                        Sequel.AutoComitTrue();
                    } else {
                        JOptionPane.showMessageDialog(null, "Tidak ada pemberian obat ke pasien yang mau dihapus..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception e) {
                sukses = false;
                System.out.println("Notif : " + e);
            }
        }
    }

    private void prosesHapusDetailPiutangObat() {
        if (!this.ispiutang) return;

        String obat = kodeobat.stream().map(it -> "'" + it.replaceAll("'", "''") + "'").collect(Collectors.joining(", "));

        if (!obat.isBlank()) {
            obat = " and detailpiutang.kode_brng in (" + obat + ") ";
        } else {
            sukses = false;
        }

        double sisapiutang = 0, ttlpiutang = 0, ppn = 0, psr_ppn = 0, kembali = 0;
        
        // detailpiutang hpp pakai h_jual
        if (sukses) {
            try (PreparedStatement ps = koneksi.prepareStatement("select piutang.nota_piutang, round(piutang.ppn / (piutang.sisapiutang - piutang.ppn), 2) as psr_ppn, piutang.ppn, piutang.ongkir, piutang.uangmuka, piutang.sisapiutang, piutang.kd_bangsal from piutang where piutang.nota_piutang = ?")) {
                ps.setString(1, tbKamar.getValueAt(tbKamar.getSelectedRow(), 0).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ppn = rs.getDouble("ppn");
                        psr_ppn = rs.getDouble("psr_ppn");
                        sisapiutang = rs.getDouble("sisapiutang");
                        ttlpiutang = sisapiutang - psr_ppn;
                        
                        Sequel.AutoComitFalse();
                        try (PreparedStatement ps2 = koneksi.prepareStatement(
                            "select detailpiutang.kode_brng, detailpiutang.h_jual, detailpiutang.jumlah, detailpiutang.subtotal, detailpiutang.dis, " +
                            "detailpiutang.bsr_dis, detailpiutang.total, detailpiutang.no_batch, detailpiutang.no_faktur from detailpiutang where " +
                            "detailpiutang.nota_piutang = ?" + obat
                        )) {
                            ps2.setString(1, rs.getString("nota_piutang"));
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) {
                                    do {
                                        if (Sequel.menghapustfSmc("detailpiutang", "nota_piutang = ? and kode_brng = ? and no_batch = ? and no_faktur = ?",
                                            rs.getString("nota_piutang"), rs2.getString("kode_brng"), rs2.getString("no_batch"), rs2.getString("no_faktur")
                                        )) {
                                            kembali += rs.getDouble("total");
                                            if (AKTIFKANBATCHOBAT) {
                                                Sequel.mengupdateSmc("data_batch", "sisa = sisa + ?", "no_batch = ? and kode_brng = ? and no_faktur = ?",
                                                    rs2.getString("jumlah"), rs2.getString("no_batch"), rs2.getString("kode_brng"), rs2.getString("no_faktur")
                                                );

                                                Trackobat.catatRiwayat(rs2.getString("kode_brng"), rs2.getDouble("jumlah"), 0, "Piutang", akses.getkode(), rs.getString("kd_bangsal"),
                                                    "Hapus", rs2.getString("no_batch"), rs2.getString("no_faktur"), rs.getString("nota_piutang"));

                                                if (!Sequel.menyimpantfSmc("gudangbarang", "", rs2.getString("kode_brng"), rs.getString("kd_bangsal"), rs2.getString("jumlah"), rs2.getString("no_batch"), rs2.getString("no_faktur"))) {
                                                    if (!Sequel.mengupdatetfSmc("gudangbarang", "stok = stok + ?", "kode_brng = ? and kd_bangsal = ? and no_batch = ? and no_faktur = ?",
                                                        rs2.getString("jumlah"), rs2.getString("kode_brng"), rs.getString("kd_bangsal"), rs2.getString("no_batch"), rs2.getString("no_faktur")
                                                    )) {
                                                        sukses = false;
                                                    }
                                                }
                                            } else {
                                                Trackobat.catatRiwayat(rs2.getString("kode_brng"), rs2.getDouble("jumlah"), 0, "Piutang",
                                                    akses.getkode(), rs.getString("kd_bangsal"), "Hapus", "", "", rs.getString("nota_piutang"));

                                                if (!Sequel.menyimpantfSmc("gudangbarang", "", rs2.getString("kode_brng"), rs.getString("kd_bangsal"), rs2.getString("jumlah"), "", "")) {
                                                    if (!Sequel.mengupdatetfSmc("gudangbarang", "stok = stok + ?", "kode_brng = ? and kd_bangsal = ? and no_batch = '' and no_faktur = ''",
                                                        rs2.getString("jumlah"), rs2.getString("kode_brng"), rs.getString("kd_bangsal")
                                                    )) {
                                                        sukses = false;
                                                    }
                                                }
                                            }
                                        } else {
                                            sukses = false;
                                        }
                                    } while (sukses && rs2.next());

                                    if (sukses) {
                                        Sequel.deleteTampJurnal();
                                        if (sukses) {
                                            sukses = Sequel.insertTampJurnal(Piutang_Obat, "PIUTANG PASIEN", 0, kembali);
                                        }
                                        if (sukses) {
                                            sukses = Sequel.insertTampJurnal(Kontra_Piutang_Obat, "KAS DI TANGAN", kembali, 0);
                                        }
                                        if (sukses) {
                                            sukses = jur.simpanJurnal(rs.getString("nota_piutang"), "U", "BATAL PIUTANG OBAT DI " + Sequel.cariIsiSmc("select bangsal.nm_bangsal from bangsal where bangsal.kd_bangsal = ?", rs.getString("kd_bangsal")).toUpperCase() + ", OLEH " + akses.getkode());
                                        }
                                    }
                                    
                                    if (sukses) {
                                        
                                    }
                                }
                            }
                        }
                        Sequel.AutoComitTrue();
                    } else {
                        JOptionPane.showMessageDialog(null, "Tidak ada piutang obat pasien yang mau dihapus..!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    private void isForm() {
        if (ChkAccor.isSelected()) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(internalFrame1.getWidth() - 300, HEIGHT));
            FormMenu.setVisible(true);
            ChkAccor.setVisible(true);
        } else {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            FormMenu.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }
}
