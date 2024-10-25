/*
  By Mas Elkhanza
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import simrskhanza.DlgPasien;

/**
 *
 * @author dosen
 */
public final class SatuSehatKirimRencanaKontrol extends javax.swing.JDialog {

    private final DefaultTableModel tabMode, tabMode1, tabMode2, tabMode3;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps, ps1, ps2, ps3;
    private ResultSet rs, rs1, rs2, rs3;
    private int i = 0, j = 0;
    private String link = "", json = "", idpasien = "", iddokter = "", idpasien1 = "", iddokter1 = "", idpasien2 = "", iddokter2 = "", idpasien3 = "", iddokter3 = "";
    private ApiSatuSehat api = new ApiSatuSehat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();
    private DlgPasien cekflagging = new DlgPasien(null, false);

    /**
     * Creates new form DlgKamar
     *
     * @param parent
     * @param modal
     */
    public SatuSehatKirimRencanaKontrol(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10, 2);
        setSize(628, 674);

//        false, rs.getString("tgl_registrasi") + "T" + rs.getString("jam_reg") + "+07:00", rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
//                                rs.getString("no_ktp"), rs.getString("noktpdokter"), rs.getString("nm_dokter"), rs.getString("id_encounter"), rs.getString("kd_penyakit"), rs.getString("nm_penyakit"),
//                                rs.getString("deskripsi_panjang"), rs.getString("id_lokasi_satusehat"),rs.getString("tgl_surat"),rs.getString("tgl_rencana"),rs.getString("id_rtl")
        tabMode = new DefaultTableModel(null, new String[]{
            "P", "Tanggal Registrasi", "No.Rawat", "No.RM", "Nama Pasien", "No.KTP Pasien", "KTP Dokter", "Nama Dokter",
            "Encounter", "Kd Penyakit", "Penyakit", "ID Org", "ID Lokasi", "Nama Poli", "Tgl Surat", "Tgl Rencana", "ID RTL", "SEP"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbKamar.setModel(tabMode);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 18; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(155);
            } else if (i == 2) {
                column.setPreferredWidth(115);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(155);
            } else if (i == 5) {
                column.setPreferredWidth(115);
            } else if (i == 6) {
                column.setPreferredWidth(115);
            } else if (i == 7) {
                column.setPreferredWidth(155);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(21);
            } else if (i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setPreferredWidth(225);
            } else if (i == 12) {
                column.setPreferredWidth(215);
            } else if (i == 13) {
                column.setPreferredWidth(215);
            } else if (i == 14) {
                column.setPreferredWidth(115);
            } else if (i == 15) {
                column.setPreferredWidth(115);
            } else if (i == 16) {
                column.setPreferredWidth(215);
            } else if (i == 17) {
                column.setPreferredWidth(100);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode1 = new DefaultTableModel(null, new String[]{
            "P", "Tanggal Registrasi", "No.Rawat", "No.RM", "Nama Pasien", "No.KTP Pasien", "KTP Dokter", "Nama Dokter",
            "Encounter", "Kd Penyakit", "Penyakit", "ID Org", "ID Lokasi", "Nama Poli", "Tgl Surat", "Tgl Rencana", "ID RTL", "No Surat"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbKamar1.setModel(tabMode1);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbKamar1.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKamar1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 18; i++) {
            TableColumn column = tbKamar1.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(155);
            } else if (i == 2) {
                column.setPreferredWidth(115);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(155);
            } else if (i == 5) {
                column.setPreferredWidth(115);
            } else if (i == 6) {
                column.setPreferredWidth(115);
            } else if (i == 7) {
                column.setPreferredWidth(155);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(21);
            } else if (i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setPreferredWidth(225);
            } else if (i == 12) {
                column.setPreferredWidth(215);
            } else if (i == 13) {
                column.setPreferredWidth(215);
            } else if (i == 14) {
                column.setPreferredWidth(115);
            } else if (i == 15) {
                column.setPreferredWidth(115);
            } else if (i == 16) {
                column.setPreferredWidth(215);
            } else if (i == 17) {
                column.setPreferredWidth(100);
            }
        }
        tbKamar1.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode2 = new DefaultTableModel(null, new String[]{
            "P", "Tanggal Registrasi", "No.Rawat", "No.RM", "Nama Pasien", "No.KTP Pasien", "KTP Dokter", "Nama Dokter",
            "Encounter", "Kd Penyakit", "Penyakit", "ID Org", "ID Lokasi", "Nama Poli", "Tgl Surat", "Tgl Rencana", "ID RTL", "No Surat"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbKamar2.setModel(tabMode2);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbKamar2.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKamar2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 18; i++) {
            TableColumn column = tbKamar2.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(155);
            } else if (i == 2) {
                column.setPreferredWidth(115);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(155);
            } else if (i == 5) {
                column.setPreferredWidth(115);
            } else if (i == 6) {
                column.setPreferredWidth(115);
            } else if (i == 7) {
                column.setPreferredWidth(155);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(21);
            } else if (i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setPreferredWidth(225);
            } else if (i == 12) {
                column.setPreferredWidth(215);
            } else if (i == 13) {
                column.setPreferredWidth(215);
            } else if (i == 14) {
                column.setPreferredWidth(115);
            } else if (i == 15) {
                column.setPreferredWidth(115);
            } else if (i == 16) {
                column.setPreferredWidth(215);
            } else if (i == 17) {
                column.setPreferredWidth(100);
            }
        }
        tbKamar2.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode3 = new DefaultTableModel(null, new String[]{
            "P", "Tanggal Registrasi", "No.Rawat", "No.RM", "Nama Pasien", "No.KTP Pasien", "KTP Dokter", "Nama Dokter",
            "Encounter", "Kd Penyakit", "Penyakit", "Jns Pelayanan", "Di Rujuk ke", "Catatan", "Nama Poli", "ID Org", "No SEP", "ID RTL", "Rencana Kunjungan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbKamar3.setModel(tabMode3);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbKamar3.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKamar3.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 19; i++) {
            TableColumn column = tbKamar3.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(155);
            } else if (i == 2) {
                column.setPreferredWidth(115);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(155);
            } else if (i == 5) {
                column.setPreferredWidth(115);
            } else if (i == 6) {
                column.setPreferredWidth(115);
            } else if (i == 7) {
                column.setPreferredWidth(155);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(21);
            } else if (i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setPreferredWidth(225);
            } else if (i == 12) {
                column.setPreferredWidth(215);
            } else if (i == 13) {
                column.setPreferredWidth(215);
            } else if (i == 14) {
                column.setPreferredWidth(115);
            } else if (i == 15) {
                column.setPreferredWidth(115);
            } else if (i == 16) {
                column.setPreferredWidth(215);
            } else if (i == 17) {
                column.setPreferredWidth(215);
            } else if (i == 18) {
                column.setPreferredWidth(215);
            }
        }
        tbKamar3.setDefaultRenderer(Object.class, new WarnaTable());
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

        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppPilihSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnAll = new widget.Button();
        BtnKirim = new widget.Button();
        BtnUpdate = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel15 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel17 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        ChkBelumTerkirim = new widget.CekBox();
        TabRawat = new javax.swing.JTabbedPane();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        Scroll1 = new widget.ScrollPane();
        tbKamar1 = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbKamar2 = new widget.Table();
        Scroll3 = new widget.ScrollPane();
        tbKamar3 = new widget.Table();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(150, 26));
        ppPilihSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPilihSemuaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Hilangkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(150, 26));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBersihkan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Data Procedure Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        internalFrame1.setMinimumSize(new java.awt.Dimension(774, 263));
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass8.add(LCount);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelGlass8.add(BtnAll);

        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); // NOI18N
        BtnKirim.setMnemonic('K');
        BtnKirim.setText("Kirim");
        BtnKirim.setToolTipText("Alt+K");
        BtnKirim.setName("BtnKirim"); // NOI18N
        BtnKirim.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKirimActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKirim);

        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/edit_f2.png"))); // NOI18N
        BtnUpdate.setMnemonic('U');
        BtnUpdate.setText("Update");
        BtnUpdate.setToolTipText("Alt+U");
        BtnUpdate.setName("BtnUpdate"); // NOI18N
        BtnUpdate.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnUpdate);

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
        panelGlass8.add(BtnPrint);

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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel15.setText("Tgl.Registrasi :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel15);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-10-2024" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("s.d.");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(24, 23));
        panelGlass9.add(jLabel17);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-10-2024" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel16.setText("Key Word :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(210, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

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
        panelGlass9.add(BtnCari);

        ChkBelumTerkirim.setBorder(null);
        ChkBelumTerkirim.setText("Data belum terkirim");
        ChkBelumTerkirim.setBorderPainted(true);
        ChkBelumTerkirim.setBorderPaintedFlat(true);
        ChkBelumTerkirim.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkBelumTerkirim.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkBelumTerkirim.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkBelumTerkirim.setIconTextGap(2);
        ChkBelumTerkirim.setName("ChkBelumTerkirim"); // NOI18N
        ChkBelumTerkirim.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChkBelumTerkirimItemStateChanged(evt);
            }
        });
        ChkBelumTerkirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkBelumTerkirimActionPerformed(evt);
            }
        });
        panelGlass9.add(ChkBelumTerkirim);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setComponentPopupMenu(jPopupMenu1);
        tbKamar.setName("tbKamar"); // NOI18N
        Scroll.setViewportView(tbKamar);

        TabRawat.addTab("Sukon BPJS", Scroll);

        Scroll1.setComponentPopupMenu(jPopupMenu1);
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbKamar1.setComponentPopupMenu(jPopupMenu1);
        tbKamar1.setName("tbKamar1"); // NOI18N
        Scroll1.setViewportView(tbKamar1);

        TabRawat.addTab("Sukon Biasa", Scroll1);

        Scroll2.setComponentPopupMenu(jPopupMenu1);
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbKamar2.setComponentPopupMenu(jPopupMenu1);
        tbKamar2.setName("tbKamar2"); // NOI18N
        Scroll2.setViewportView(tbKamar2);

        TabRawat.addTab("Perintah Rawat Inap", Scroll2);

        Scroll3.setComponentPopupMenu(jPopupMenu1);
        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        tbKamar3.setComponentPopupMenu(jPopupMenu1);
        tbKamar3.setName("tbKamar3"); // NOI18N
        Scroll3.setViewportView(tbKamar3);

        TabRawat.addTab("Rujuk Keluar", Scroll3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.PAGE_START);

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
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TCari.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("parameter", "%" + TCari.getText().trim() + "%");
            param.put("tanggal1", Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            param.put("tanggal2", Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            Valid.MyReport("rptKirimProcedureSatuSehat.jasper", "report", "::[ Kirim Data Procedure Satu Sehat Kemenkes ]::", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbKamar.requestFocus();
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

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKirimActionPerformed
//        System.out.println("hayolo");
        switch (TabRawat.getSelectedIndex()) {
            case 0:
                for (i = 0; i < tbKamar.getRowCount(); i++) {
                    if (tbKamar.getValueAt(i, 0).toString().equals("true") && (!tbKamar.getValueAt(i, 5).toString().equals("")) && (!tbKamar.getValueAt(i, 6).toString().equals("")) && tbKamar.getValueAt(i, 16).toString().equals("")) {
                        try {
                            idpasien = cekViaSatuSehat.tampilIDPasien(tbKamar.getValueAt(i, 5).toString());
                            iddokter = cekViaSatuSehat.tampilIDParktisi(tbKamar.getValueAt(i, 6).toString());
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                                json = "{\n"
                                        + "    \"resourceType\": \"ServiceRequest\",\n"
                                        + "    \"identifier\": [\n"
                                        + "        {\n"
                                        + "            \"system\": \"http://sys-ids.kemkes.go.id/servicerequest/" + koneksiDB.IDSATUSEHAT() + "\",\n"
                                        + "            \"value\": \"" + tbKamar.getValueAt(i, 11).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"status\": \"active\",\n"
                                        + "    \"intent\": \"original-order\",\n"
                                        + "    \"priority\": \"routine\",\n"
                                        + "    \"category\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://snomed.info/sct\",\n"
                                        + "                    \"code\": \"3457005\",\n"
                                        + "                    \"display\": \"Patient referral\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"code\": {\n"
                                        + "        \"coding\": [\n"
                                        + "            {\n"
                                        + "                \"system\": \"http://snomed.info/sct\",\n"
                                        + "                \"code\": \"185389009\",\n"
                                        + "                \"display\": \"Follow-up visit\"\n"
                                        + "            }\n"
                                        + "        ],\n"
                                        + "        \"text\": \"Kontrol kembali ke Rumah Sakit Indriati Boyolali pada tanggal " + tbKamar.getValueAt(i, 15).toString() + " \"\n"
                                        + "    },\n"
                                        + "    \"subject\": {\n"
                                        + "        \"reference\": \"Patient/" + idpasien + "\"\n"
                                        + "    },\n"
                                        + "    \"encounter\": {\n"
                                        + "        \"reference\": \"Encounter/" + tbKamar.getValueAt(i, 8).toString() + "\",\n"
                                        + "        \"display\": \"Kunjungan " + tbKamar.getValueAt(i, 4).toString() + " pada tanggal " + tbKamar.getValueAt(i, 1).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"occurrenceDateTime\": \"" + tbKamar.getValueAt(i, 1).toString() + "\",\n"
                                        + "    \"authoredOn\": \"" + tbKamar.getValueAt(i, 1).toString() + "\",\n"
                                        + "    \"requester\": {\n"
                                        + "        \"reference\": \"Practitioner/" + iddokter + "\",\n"
                                        + "        \"display\": \"" + tbKamar.getValueAt(i, 7).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"performer\": [\n"
                                        + "        {\n"
                                        + "            \"reference\": \"Practitioner/" + iddokter + "\",\n"
                                        + "            \"display\": \"" + tbKamar.getValueAt(i, 7).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"reasonCode\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://hl7.org/fhir/sid/icd-10\",\n"
                                        + "                    \"code\": \"" + tbKamar.getValueAt(i, 9).toString() + "\",\n"
                                        + "                    \"display\": \"" + tbKamar.getValueAt(i, 10).toString() + "\"\n"
                                        + "                }\n"
                                        + "            ],\n"
                                        + "            \"text\": \"Kontrol rutin bulanan\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"locationReference\": [\n"
                                        + "        {\n"
                                        + "            \"reference\": \"Location/" + tbKamar.getValueAt(i, 12).toString() + "\",\n"
                                        + "            \"display\": \"" + tbKamar.getValueAt(i, 13).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"patientInstruction\": \"Kontrol kembali ke Rumah Sakit Indriati Boyolali pada " + tbKamar.getValueAt(i, 15).toString() + ". Dalam keadaan darurat dapat menghubungi hotline (0276) 3280111\"\n"
                                        + "}";
                                System.out.println("URL : " + link + "/ServiceRequest");
                                System.out.println("Request JSON : " + json);
                                requestEntity = new HttpEntity(json, headers);
                                json = api.getRest().exchange(link + "/ServiceRequest", HttpMethod.POST, requestEntity, String.class).getBody();
                                System.out.println("Result JSON : " + json);
                                root = mapper.readTree(json);
                                response = root.path("id");
                                if (!response.asText().equals("")) {
                                    Sequel.menyimpan("satu_sehat_rtl", "?,?,?", "RTL", 3, new String[]{
                                        tbKamar.getValueAt(i, 2).toString(), tbKamar.getValueAt(i, 17).toString(), response.asText()
                                    }
                                    );
                                }
                            } catch (HttpClientErrorException | HttpServerErrorException e) {
                                // Handle client and server errors
                                System.err.println("Error Response Status Code: " + e.getStatusCode());
//                            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
                                // You can further parse the error response body if needed
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode errorResponse = mapper.readTree(e.getResponseBodyAsString());
                                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                                String prettyErrorResponse = writer.writeValueAsString(errorResponse);
                                System.err.println("Error Response JSON: \n" + prettyErrorResponse);
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                        }
                    }
                }
                tampil();
//                System.out.println("kebaca gak?");
                break;
            case 1:
//                System.out.println("tab 1");
                for (i = 0; i < tbKamar1.getRowCount(); i++) {
                    //ceklis, // noktppasien, //noktpdok, //id rtl
                    if (tbKamar1.getValueAt(i, 0).toString().equals("true") && (!tbKamar1.getValueAt(i, 5).toString().equals("")) && (!tbKamar1.getValueAt(i, 6).toString().equals("")) && tbKamar1.getValueAt(i, 16).toString().equals("")) {
                        try {
                            idpasien1 = cekViaSatuSehat.tampilIDPasien(tbKamar1.getValueAt(i, 5).toString());
                            iddokter1 = cekViaSatuSehat.tampilIDParktisi(tbKamar1.getValueAt(i, 6).toString());
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                                json = "{\n"
                                        + "    \"resourceType\": \"ServiceRequest\",\n"
                                        + "    \"identifier\": [\n"
                                        + "        {\n"
                                        + "            \"system\": \"http://sys-ids.kemkes.go.id/servicerequest/" + koneksiDB.IDSATUSEHAT() + "\",\n"
                                        + "            \"value\": \"" + tbKamar1.getValueAt(i, 11).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"status\": \"active\",\n"
                                        + "    \"intent\": \"original-order\",\n"
                                        + "    \"priority\": \"routine\",\n"
                                        + "    \"category\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://snomed.info/sct\",\n"
                                        + "                    \"code\": \"3457005\",\n"
                                        + "                    \"display\": \"Patient referral\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"code\": {\n"
                                        + "        \"coding\": [\n"
                                        + "            {\n"
                                        + "                \"system\": \"http://snomed.info/sct\",\n"
                                        + "                \"code\": \"185389009\",\n"
                                        + "                \"display\": \"Follow-up visit\"\n"
                                        + "            }\n"
                                        + "        ],\n"
                                        + "        \"text\": \"Kontrol kembali ke Rumah Sakit Indriati Boyolali pada tanggal " + tbKamar1.getValueAt(j, 15).toString() + " \"\n"
                                        + "    },\n"
                                        + "    \"subject\": {\n"
                                        + "        \"reference\": \"Patient/" + idpasien1 + "\"\n"
                                        + "    },\n"
                                        + "    \"encounter\": {\n"
                                        + "        \"reference\": \"Encounter/" + tbKamar1.getValueAt(i, 8).toString() + "\",\n"
                                        + "        \"display\": \"Kunjungan " + tbKamar1.getValueAt(i, 4).toString() + " pada tanggal " + tbKamar1.getValueAt(i, 1).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"occurrenceDateTime\": \"" + tbKamar1.getValueAt(i, 1).toString() + "\",\n"
                                        + "    \"authoredOn\": \"" + tbKamar1.getValueAt(i, 1).toString() + "\",\n"
                                        + "    \"requester\": {\n"
                                        + "        \"reference\": \"Practitioner/" + iddokter1 + "\",\n"
                                        + "        \"display\": \"" + tbKamar1.getValueAt(i, 7).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"performer\": [\n"
                                        + "        {\n"
                                        + "            \"reference\": \"Practitioner/" + iddokter1 + "\",\n"
                                        + "            \"display\": \"" + tbKamar1.getValueAt(i, 7).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"reasonCode\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://hl7.org/fhir/sid/icd-10\",\n"
                                        + "                    \"code\": \"" + tbKamar1.getValueAt(i, 9).toString() + "\",\n"
                                        + "                    \"display\": \"" + tbKamar1.getValueAt(i, 10).toString() + "\"\n"
                                        + "                }\n"
                                        + "            ],\n"
                                        + "            \"text\": \"Kontrol rutin bulanan\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"locationReference\": [\n"
                                        + "        {\n"
                                        + "            \"reference\": \"Location/" + tbKamar1.getValueAt(i, 12).toString() + "\",\n"
                                        + "            \"display\": \"" + tbKamar1.getValueAt(i, 13).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"patientInstruction\": \"Kontrol kembali ke Rumah Sakit Indriati Boyolali pada " + tbKamar1.getValueAt(i, 15).toString() + ". Dalam keadaan darurat dapat menghubungi hotline (0276) 3280111\"\n"
                                        + "}";
                                System.out.println("URL : " + link + "/ServiceRequest");
                                System.out.println("Request JSON : " + json);
                                requestEntity = new HttpEntity(json, headers);
                                json
                                        = api.getRest().exchange(link + "/ServiceRequest", HttpMethod.POST, requestEntity, String.class
                                        ).getBody();
                                System.out.println("Result JSON : " + json);
                                root = mapper.readTree(json);
                                response = root.path("id");
                                if (!response.asText().equals("")) {
                                    Sequel.menyimpan("satu_sehat_rtl", "?,?,?", "RTL", 3, new String[]{
                                        tbKamar1.getValueAt(i, 2).toString(), tbKamar1.getValueAt(i, 17).toString(), response.asText()
                                    });
                                }
                            } catch (HttpClientErrorException | HttpServerErrorException e) {
                                // Handle client and server errors
                                System.err.println("Error Response Status Code: " + e.getStatusCode());
//                            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
                                // You can further parse the error response body if needed
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode errorResponse = mapper.readTree(e.getResponseBodyAsString());
                                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                                String prettyErrorResponse = writer.writeValueAsString(errorResponse);
                                System.err.println("Error Response JSON: \n" + prettyErrorResponse);
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                        }
                    }
                }
                tampil();
                break;
            case 2:
                for (i = 0; i < tbKamar2.getRowCount(); i++) {
                    //ceklis, // noktppasien, //noktpdok, //id rtl
                    if (tbKamar2.getValueAt(i, 0).toString().equals("true") && (!tbKamar2.getValueAt(i, 5).toString().equals("")) && (!tbKamar2.getValueAt(i, 6).toString().equals("")) && tbKamar2.getValueAt(i, 16).toString().equals("")) {
                        try {
                            idpasien2 = cekViaSatuSehat.tampilIDPasien(tbKamar2.getValueAt(i, 5).toString());
                            iddokter2 = cekViaSatuSehat.tampilIDParktisi(tbKamar2.getValueAt(i, 6).toString());
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                                json = "{\n"
                                        + "    \"resourceType\": \"ServiceRequest\",\n"
                                        + "    \"identifier\": [\n"
                                        + "        {\n"
                                        + "            \"system\": \"http://sys-ids.kemkes.go.id/servicerequest/" + koneksiDB.IDSATUSEHAT() + "\",\n"
                                        + "            \"value\": \"" + tbKamar2.getValueAt(i, 11).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"status\": \"active\",\n"
                                        + "    \"intent\": \"original-order\",\n"
                                        + "    \"priority\": \"routine\",\n"
                                        + "    \"category\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://snomed.info/sct\",\n"
                                        + "                    \"code\": \"3457005\",\n"
                                        + "                    \"display\": \"Patient referral\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"code\": {\n"
                                        + "        \"coding\": [\n"
                                        + "            {\n"
                                        + "                \"system\": \"http://snomed.info/sct\",\n"
                                        + "                \"code\": \"737481003\",\n"
                                        + "                \"display\": \"Inpatient care management\"\n"
                                        + "            }\n"
                                        + "        ]\n"
                                        + "    },\n"
                                        + "    \"subject\": {\n"
                                        + "        \"reference\": \"Patient/" + idpasien2 + "\"\n"
                                        + "    },\n"
                                        + "    \"encounter\": {\n"
                                        + "        \"reference\": \"Encounter/" + tbKamar2.getValueAt(i, 8).toString() + "\",\n"
                                        + "        \"display\": \"Kunjungan " + tbKamar2.getValueAt(i, 4).toString() + " pada tanggal " + tbKamar2.getValueAt(i, 1).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"occurrenceDateTime\": \"" + tbKamar2.getValueAt(i, 1).toString() + "\",\n"
                                        + "    \"requester\": {\n"
                                        + "        \"reference\": \"Practitioner/" + iddokter2 + "\",\n"
                                        + "        \"display\": \"" + tbKamar2.getValueAt(i, 7).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"performer\": [\n"
                                        + "        {\n"
                                        + "            \"reference\": \"Practitioner/" + iddokter2 + "\",\n"
                                        + "            \"display\": \"" + tbKamar2.getValueAt(i, 7).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"reasonCode\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://hl7.org/fhir/sid/icd-10\",\n"
                                        + "                    \"code\": \"" + tbKamar2.getValueAt(i, 9).toString().trim() + "\",\n"
                                        + "                    \"display\": \"" + tbKamar2.getValueAt(i, 10).toString() + "\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"locationCode\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://terminology.hl7.org/CodeSystem/v3-RoleCode\",\n"
                                        + "                    \"code\": \"HOSP\",\n"
                                        + "                    \"display\": \"Hospital\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"patientInstruction\": \"Rencana Tindak Lanjut Rawat Inap di RS Indriati Boyolali pada " + tbKamar2.getValueAt(i, 15).toString()
                                        + ". Dalam keadaan darurat dapat menghubungi hotline (0276) 3280111\"\n"
                                        + "}";
                                System.out.println("URL : " + link + "/ServiceRequest");
                                System.out.println("Request JSON : " + json);
                                requestEntity = new HttpEntity(json, headers);
                                json
                                        = api.getRest().exchange(link + "/ServiceRequest", HttpMethod.POST, requestEntity, String.class
                                        ).getBody();
                                System.out.println("Result JSON : " + json);
                                root = mapper.readTree(json);
                                response = root.path("id");
                                if (!response.asText().equals("")) {
                                    Sequel.menyimpan("satu_sehat_rtl", "?,?,?", "RTL", 3, new String[]{
                                        tbKamar2.getValueAt(i, 2).toString(), tbKamar2.getValueAt(i, 17).toString(), response.asText()
                                    });
                                }
                            } catch (HttpClientErrorException | HttpServerErrorException e) {
                                // Handle client and server errors
                                System.err.println("Error Response Status Code: " + e.getStatusCode());
//                            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
                                // You can further parse the error response body if needed
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode errorResponse = mapper.readTree(e.getResponseBodyAsString());
                                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                                String prettyErrorResponse = writer.writeValueAsString(errorResponse);
                                System.err.println("Error Response JSON: \n" + prettyErrorResponse);
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                        }
                    }
                }
                tampil();
                break;

            case 3:
                String code,
                 display;
                // Check the value of tbKamar.getValueAt(i, 7).toString()

                for (i = 0; i < tbKamar3.getRowCount(); i++) {
                    //ceklis, // noktppasien, //noktpdok, //id rtl
                    if (tbKamar3.getValueAt(i, 0).toString().equals("true") && (!tbKamar3.getValueAt(i, 5).toString().equals("")) && (!tbKamar3.getValueAt(i, 6).toString().equals("")) && tbKamar3.getValueAt(i, 17).toString().equals("")) {
                        try {
                            idpasien3 = cekViaSatuSehat.tampilIDPasien(tbKamar3.getValueAt(i, 5).toString());
                            iddokter3 = cekViaSatuSehat.tampilIDParktisi(tbKamar3.getValueAt(i, 6).toString());
                            if (tbKamar3.getValueAt(i, 11) != null
                                    && tbKamar3.getValueAt(i, 11).toString().equals("2")) {
                                code = "737492002";
                                display = "Outpatient care plan";
                            } else {
                                code = "737481003";
                                display = "Inpatient care plan";
                            }
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                                json = "{\n"
                                        + "    \"resourceType\": \"ServiceRequest\",\n"
                                        + "    \"identifier\": [\n"
                                        + "        {\n"
                                        + "            \"system\": \"http://sys-ids.kemkes.go.id/servicerequest/" + koneksiDB.IDSATUSEHAT() + "\",\n"
                                        + "            \"value\": \"" + tbKamar3.getValueAt(i, 15).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"status\": \"active\",\n"
                                        + "    \"intent\": \"original-order\",\n"
                                        + "    \"priority\": \"routine\",\n"
                                        + "    \"category\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://snomed.info/sct\",\n"
                                        + "                    \"code\": \"3457005\",\n"
                                        + "                    \"display\": \"Patient referral\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"code\": {\n"
                                        + "        \"coding\": [\n"
                                        + "            {\n"
                                        + "                \"system\": \"http://snomed.info/sct\",\n"
                                        + "                \"code\": \"" + code + "\",\n"
                                        + "                \"display\": \"" + display + "\"\n"
                                        + "            }\n"
                                        + "        ]\n"
                                        + "    },\n"
                                        + "    \"subject\": {\n"
                                        + "        \"reference\": \"Patient/" + idpasien3 + "\"\n"
                                        + "    },\n"
                                        + "    \"encounter\": {\n"
                                        + "        \"reference\": \"Encounter/" + tbKamar3.getValueAt(i, 8).toString() + "\",\n"
                                        + "        \"display\": \"Rencana Kunjungan " + tbKamar3.getValueAt(i, 4).toString() + " pada tanggal " + tbKamar3.getValueAt(i, 1).toString() + " \"\n"
                                        + "    },\n"
                                        + "    \"occurrenceDateTime\": \"" + tbKamar3.getValueAt(i, 1).toString() + "\",\n"
                                        + "    \"requester\": {\n"
                                        + "        \"reference\": \"Practitioner/" + iddokter3 + "\",\n"
                                        + "        \"display\": \"" + tbKamar3.getValueAt(i, 7).toString() + "\"\n"
                                        + "    },\n"
                                        + "    \"performer\": [\n"
                                        + "        {\n"
                                        + "            \"reference\": \"Practitioner/" + iddokter3 + "\",\n"
                                        + "            \"display\": \"" + tbKamar3.getValueAt(i, 7).toString() + "\"\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"reasonCode\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://hl7.org/fhir/sid/icd-10\",\n"
                                        + "                    \"code\": \"" + tbKamar3.getValueAt(i, 9).toString().trim() + "\",\n"
                                        + "                    \"display\": \"" + tbKamar3.getValueAt(i, 10).toString() + "\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"locationCode\": [\n"
                                        + "        {\n"
                                        + "            \"coding\": [\n"
                                        + "                {\n"
                                        + "                    \"system\": \"http://terminology.hl7.org/CodeSystem/v3-RoleCode\",\n"
                                        + "                    \"code\": \"HOSP\",\n"
                                        + "                    \"display\": \"Hospital\"\n"
                                        + "                }\n"
                                        + "            ]\n"
                                        + "        }\n"
                                        + "    ],\n"
                                        + "    \"patientInstruction\": \"Rencana Tindak Lanjut Rujuk Keluar ke " + tbKamar3.getValueAt(i, 12).toString() + " di " + tbKamar3.getValueAt(i, 14).toString() + " dengan catatan " + tbKamar3.getValueAt(i, 13).toString() + ". Rencana Kunjungan pada " + tbKamar3.getValueAt(i, 17).toString() + "\"\n"
                                        + "}";
                                System.out.println("URL : " + link + "/ServiceRequest");
                                System.out.println("Request JSON : " + json);
                                requestEntity = new HttpEntity(json, headers);
                                json
                                        = api.getRest().exchange(link + "/ServiceRequest", HttpMethod.POST, requestEntity, String.class
                                        ).getBody();
                                System.out.println("Result JSON : " + json);
                                root = mapper.readTree(json);
                                response = root.path("id");
                                if (!response.asText().equals("")) {
                                    Sequel.menyimpan("satu_sehat_rtl", "?,?,?", "RTL", 3, new String[]{
                                        tbKamar3.getValueAt(i, 2).toString(), tbKamar3.getValueAt(i, 16).toString(), response.asText()
                                    });
                                }
                            } catch (HttpClientErrorException | HttpServerErrorException e) {
                                // Handle client and server errors
                                System.err.println("Error Response Status Code: " + e.getStatusCode());
//                            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
                                // You can further parse the error response body if needed
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode errorResponse = mapper.readTree(e.getResponseBodyAsString());
                                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                                String prettyErrorResponse = writer.writeValueAsString(errorResponse);
                                System.err.println("Error Response JSON: \n" + prettyErrorResponse);
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                        }
                    }
                }
                tampil();
                break;

        }
    }//GEN-LAST:event_BtnKirimActionPerformed

    private void ppPilihSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPilihSemuaActionPerformed
        if (TabRawat.getSelectedIndex() == 0) {
            for (i = 0; i < tbKamar.getRowCount(); i++) {
                tbKamar.setValueAt(false, i, 0);
            }
        } else if (TabRawat.getSelectedIndex() == 1) {
            for (i = 0; i < tbKamar1.getRowCount(); i++) {
                tbKamar1.setValueAt(false, i, 0);
            }
        } else if (TabRawat.getSelectedIndex() == 2) {
            for (i = 0; i < tbKamar2.getRowCount(); i++) {
                tbKamar2.setValueAt(false, i, 0);
            }
        } else if (TabRawat.getSelectedIndex() == 3) {
            for (i = 0; i < tbKamar3.getRowCount(); i++) {
                tbKamar3.setValueAt(false, i, 0);
            }
        }
    }//GEN-LAST:event_ppPilihSemuaActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        if (TabRawat.getSelectedIndex() == 0) {
            for (i = 0; i < tbKamar.getRowCount(); i++) {
                tbKamar.setValueAt(false, i, 0);
            }
        } else if (TabRawat.getSelectedIndex() == 1) {
            for (i = 0; i < tbKamar1.getRowCount(); i++) {
                tbKamar1.setValueAt(false, i, 0);
            }
        } else if (TabRawat.getSelectedIndex() == 2) {
            for (i = 0; i < tbKamar2.getRowCount(); i++) {
                tbKamar2.setValueAt(false, i, 0);
            }
        } else if (TabRawat.getSelectedIndex() == 3) {
            for (i = 0; i < tbKamar3.getRowCount(); i++) {
                tbKamar3.setValueAt(false, i, 0);
            }
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUpdateActionPerformed
        for (i = 0; i < tbKamar.getRowCount(); i++) {
            if (tbKamar.getValueAt(i, 0).toString().equals("true") && (!tbKamar.getValueAt(i, 5).toString().equals("")) && (!tbKamar.getValueAt(i, 9).toString().equals("")) && (!tbKamar.getValueAt(i, 12).toString().equals(""))) {
                try {
                    idpasien = cekViaSatuSehat.tampilIDPasien(tbKamar.getValueAt(i, 5).toString());
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                        json = "{"
                                + "\"resourceType\": \"Procedure\","
                                + "\"id\": \"" + tbKamar.getValueAt(i, 12).toString() + "\","
                                + "\"status\": \"completed\","
                                + "\"category\": {"
                                + "\"coding\": ["
                                + "{"
                                + "\"system\": \"http://snomed.info/sct\","
                                + "\"code\": \"103693007\","
                                + "\"display\": \"Diagnostic procedure\""
                                + "}"
                                + "],"
                                + "\"text\":\"Diagnostic procedure\""
                                + "},"
                                + "\"code\": {"
                                + "\"coding\": ["
                                + "{"
                                + "\"system\": \"http://hl7.org/fhir/sid/icd-9-cm\","
                                + "\"code\": \"" + tbKamar.getValueAt(i, 10).toString() + "\","
                                + "\"display\": \"" + tbKamar.getValueAt(i, 11).toString() + "\""
                                + "}"
                                + "]"
                                + "},"
                                + "\"subject\": {"
                                + "\"reference\": \"Patient/" + idpasien + "\","
                                + "\"display\": \"" + tbKamar.getValueAt(i, 4).toString() + "\""
                                + "},"
                                + "\"encounter\": {"
                                + "\"reference\": \"Encounter/" + tbKamar.getValueAt(i, 9).toString() + "\","
                                + "\"display\": \"Prosedur " + tbKamar.getValueAt(i, 4).toString() + " selama kunjungan/dirawat dari tanggal " + tbKamar.getValueAt(i, 1).toString() + " sampai " + tbKamar.getValueAt(i, 8).toString() + "\""
                                + "},"
                                + "\"performedPeriod\": {"
                                + "\"start\": \"" + tbKamar.getValueAt(i, 1).toString() + "\","
                                + "\"end\": \"" + tbKamar.getValueAt(i, 8).toString() + "\""
                                + "}"
                                + "}";
                        System.out.println("URL : " + link + "/Procedure/" + tbKamar.getValueAt(i, 12).toString());
                        System.out.println("Request JSON : " + json);
                        requestEntity = new HttpEntity(json, headers);
                        json
                                = api.getRest().exchange(link + "/Procedure/" + tbKamar.getValueAt(i, 12).toString(), HttpMethod.PUT, requestEntity, String.class
                                ).getBody();
                        System.out.println("Result JSON : " + json);
                    } catch (HttpClientErrorException | HttpServerErrorException e) {
                        // Handle client and server errors
                        System.err.println("Error Response Status Code: " + e.getStatusCode());
//                            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
                        // You can further parse the error response body if needed
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode errorResponse = mapper.readTree(e.getResponseBodyAsString());
                        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                        String prettyErrorResponse = writer.writeValueAsString(errorResponse);
                        System.err.println("Error Response JSON: \n" + prettyErrorResponse);
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : " + e);
                }
            }
        }
        tampil();
    }//GEN-LAST:event_BtnUpdateActionPerformed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
        } else {
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void ChkBelumTerkirimItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChkBelumTerkirimItemStateChanged
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tampil();
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_ChkBelumTerkirimItemStateChanged

    private void ChkBelumTerkirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkBelumTerkirimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkBelumTerkirimActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        if (TabRawat.getSelectedIndex() == 0) {
            tampil();
        } else if (TabRawat.getSelectedIndex() == 1) {
            tampil();
        } else if (TabRawat.getSelectedIndex() == 2) {
            tampil();
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimRencanaKontrol dialog = new SatuSehatKirimRencanaKontrol(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnKirim;
    private widget.Button BtnPrint;
    private widget.Button BtnUpdate;
    private widget.CekBox ChkBelumTerkirim;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ScrollPane Scroll3;
    private widget.TextBox TCari;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppPilihSemua;
    private widget.Table tbKamar;
    private widget.Table tbKamar1;
    private widget.Table tbKamar2;
    private widget.Table tbKamar3;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        switch (TabRawat.getSelectedIndex()) {
            case 0:
                String belumterkirim = "";
                if (ChkBelumTerkirim.isSelected() == true) {
                    belumterkirim = " satu_sehat_rtl.id_rtl IS NULL and ";
                } else {
                    belumterkirim = "";
                }
                Valid.tabelKosong(tabMode);
                try {
                    ps = koneksi.prepareStatement(
                            "SELECT\n"
                            + "	pasien.no_ktp,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_organisasi_satusehat,\n"
                            + "	satu_sehat_encounter.id_encounter,reg_periksa.tgl_registrasi,reg_periksa.no_rkm_medis,\n"
                            + "	pasien.nm_pasien,pasien.no_rkm_medis, \n"
                            + "	pegawai.no_ktp AS noktpdokter,\n"
                            + "	dokter.nm_dokter,\n"
                            + "	diagnosa_pasien.kd_penyakit,\n"
                            + "	penyakit.nm_penyakit,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,\n"
                            + "	poliklinik.nm_poli,\n"
                            + "	bridging_surat_kontrol_bpjs.no_surat,\n"
                            + "	bridging_surat_kontrol_bpjs.tgl_surat,\n"
                            + "	bridging_surat_kontrol_bpjs.tgl_rencana,\n"
                            + "	ifnull( satu_sehat_rtl.id_rtl, '' ) AS id_rtl, reg_periksa.jam_reg, reg_periksa.no_rawat \n"
                            + "FROM\n"
                            + "	reg_periksa\n"
                            + "	INNER JOIN satu_sehat_encounter ON reg_periksa.no_rawat = satu_sehat_encounter.no_rawat\n"
                            + "	INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis\n"
                            + "	INNER JOIN dokter ON reg_periksa.kd_dokter = dokter.kd_dokter\n"
                            + "	INNER JOIN satu_sehat_mapping_lokasi_ralan ON reg_periksa.kd_poli = satu_sehat_mapping_lokasi_ralan.kd_poli\n"
                            + "	INNER JOIN bridging_sep ON reg_periksa.no_rawat = bridging_sep.no_rawat\n"
                            + "	INNER JOIN bridging_surat_kontrol_bpjs ON bridging_sep.no_sep = bridging_surat_kontrol_bpjs.no_sep\n"
                            + "	INNER JOIN pegawai ON dokter.kd_dokter = pegawai.nik\n"
                            + "	INNER JOIN diagnosa_pasien ON reg_periksa.no_rawat = diagnosa_pasien.no_rawat\n"
                            + "	INNER JOIN penyakit ON diagnosa_pasien.kd_penyakit = penyakit.kd_penyakit\n"
                            + "	INNER JOIN poliklinik ON satu_sehat_mapping_lokasi_ralan.kd_poli = poliklinik.kd_poli\n"
                            + "	LEFT JOIN satu_sehat_rtl ON reg_periksa.no_rawat = satu_sehat_rtl.no_rawat \n"
                            + "	AND bridging_surat_kontrol_bpjs.no_surat = satu_sehat_rtl.nosurkon \n"
                            + "where " + belumterkirim + " reg_periksa.tgl_registrasi between ? and ? "
                            + (TCari.getText().equals("") ? "" : "and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                            + "pasien.nm_pasien like ? or pasien.no_ktp like ? "
                            + "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)") + " "
                            + " GROUP BY\n"
                            + "	bridging_surat_kontrol_bpjs.no_surat "
                            + "order by reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rawat");
                    try {
                        ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                        ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                        if (!TCari.getText().equals("")) {
                            ps.setString(3, "%" + TCari.getText() + "%");
                            ps.setString(4, "%" + TCari.getText() + "%");
                            ps.setString(5, "%" + TCari.getText() + "%");
                            ps.setString(6, "%" + TCari.getText() + "%");
                            ps.setString(7, "%" + TCari.getText() + "%");
                            ps.setString(8, "%" + TCari.getText() + "%");
                        }
                        rs = ps.executeQuery();
                        while (rs.next()) {
//                            if (cekflagging.GeneralConsentSatuSehat(rs.getString("no_rkm_medis")) == true) {
                                if (rs.getString("id_rtl").equals("")) {
                                    tabMode.addRow(new Object[]{
                                        true, rs.getString("tgl_registrasi") + "T" + rs.getString("jam_reg") + "+07:00",
                                        rs.getString("no_rawat"),
                                        rs.getString("no_rkm_medis"),
                                        rs.getString("nm_pasien"),
                                        rs.getString("no_ktp"),
                                        rs.getString("noktpdokter"),
                                        rs.getString("nm_dokter"),
                                        rs.getString("id_encounter"),
                                        rs.getString("kd_penyakit"),
                                        rs.getString("nm_penyakit"),
                                        rs.getString("id_organisasi_satusehat"),
                                        rs.getString("id_lokasi_satusehat"),
                                        rs.getString("nm_poli"),
                                        rs.getString("tgl_surat"),
                                        rs.getString("tgl_rencana"),
                                        rs.getString("id_rtl"),
                                        rs.getString("no_surat")
                                    });
                                } else {
                                    tabMode.addRow(new Object[]{
                                        false, rs.getString("tgl_registrasi") + "T" + rs.getString("jam_reg") + "+07:00",
                                        rs.getString("no_rawat"),
                                        rs.getString("no_rkm_medis"),
                                        rs.getString("nm_pasien"),
                                        rs.getString("no_ktp"),
                                        rs.getString("noktpdokter"),
                                        rs.getString("nm_dokter"),
                                        rs.getString("id_encounter"),
                                        rs.getString("kd_penyakit"),
                                        rs.getString("nm_penyakit"),
                                        rs.getString("id_organisasi_satusehat"),
                                        rs.getString("id_lokasi_satusehat"),
                                        rs.getString("nm_poli"),
                                        rs.getString("tgl_surat"),
                                        rs.getString("tgl_rencana"),
                                        rs.getString("id_rtl"),
                                        rs.getString("no_surat")
                                    });
                                }

//                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : " + e);
                }
                LCount.setText("" + tabMode.getRowCount());
                break;
            case 1:

                String belumterkirim1 = "";
                if (ChkBelumTerkirim.isSelected() == true) {
                    belumterkirim1 = " satu_sehat_rtl.id_rtl IS NULL and ";
                } else {
                    belumterkirim1 = "";
                }
                Valid.tabelKosong(tabMode1);
                try {
                    ps1 = koneksi.prepareStatement(
                            "SELECT\n"
                            + "	reg_periksa.no_rawat,\n"
                            + "	reg_periksa.tgl_registrasi,\n"
                            + "	reg_periksa.jam_reg,\n"
                            + "	pasien.no_rkm_medis,\n"
                            + "	pasien.nm_pasien,\n"
                            + "	pasien.no_ktp,\n"
                            + "	pegawai.no_ktp noktpdokter,\n"
                            + "	dokter.nm_dokter,\n"
                            + "	skdp_bpjs_new.tanggal_surat,\n"
                            + "	skdp_bpjs_new.tanggal_periksakembali,\n"
                            + "	skdp_bpjs_new.tl,\n"
                            + "	satu_sehat_encounter.id_encounter,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_organisasi_satusehat,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,\n"
                            + "	poliklinik.nm_poli,\n"
                            + "	diagnosa_pasien.kd_penyakit,\n"
                            + "	penyakit.nm_penyakit,\n"
                            + "	ifnull( satu_sehat_rtl.id_rtl, '' ) AS id_rtl \n"
                            + "FROM\n"
                            + "	reg_periksa\n"
                            + "	INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis\n"
                            + "	INNER JOIN skdp_bpjs_new ON pasien.no_rkm_medis = skdp_bpjs_new.no_rkm_medis \n"
                            + "	AND reg_periksa.no_rawat = skdp_bpjs_new.no_rawat\n"
                            + "	INNER JOIN satu_sehat_encounter ON reg_periksa.no_rawat = satu_sehat_encounter.no_rawat\n"
                            + "	INNER JOIN satu_sehat_mapping_lokasi_ralan ON skdp_bpjs_new.kd_poli = satu_sehat_mapping_lokasi_ralan.kd_poli\n"
                            + "	INNER JOIN poliklinik ON satu_sehat_mapping_lokasi_ralan.kd_poli = poliklinik.kd_poli\n"
                            + "	INNER JOIN diagnosa_pasien ON reg_periksa.no_rawat = diagnosa_pasien.no_rawat\n"
                            + "	INNER JOIN penyakit ON diagnosa_pasien.kd_penyakit = penyakit.kd_penyakit\n"
                            + "	LEFT JOIN satu_sehat_rtl ON skdp_bpjs_new.no_rawat = satu_sehat_rtl.no_rawat\n"
                            + "	INNER JOIN dokter ON skdp_bpjs_new.kd_dokter = dokter.kd_dokter\n"
                            + "	INNER JOIN pegawai ON dokter.kd_dokter = pegawai.nik  \n"
                            + "where " + belumterkirim1 + " reg_periksa.tgl_registrasi between ? and ? "
                            + (TCari.getText().equals("") ? "" : "and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                            + "pasien.nm_pasien like ? or pasien.no_ktp like ? "
                            + "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)") + " "
                            + " GROUP BY\n"
                            + "	reg_periksa.no_rawat  "
                            + "order by reg_periksa.tgl_registrasi ASC,reg_periksa.jam_reg ASC,reg_periksa.no_rawat ASC, skdp_bpjs_new.tanggal_surat ASC");
                    try {
                        ps1.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                        ps1.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                        if (!TCari.getText().equals("")) {
                            ps1.setString(3, "%" + TCari.getText() + "%");
                            ps1.setString(4, "%" + TCari.getText() + "%");
                            ps1.setString(5, "%" + TCari.getText() + "%");
                            ps1.setString(6, "%" + TCari.getText() + "%");
                            ps1.setString(7, "%" + TCari.getText() + "%");
                            ps1.setString(8, "%" + TCari.getText() + "%");
                        }
                        rs1 = ps1.executeQuery();
                        while (rs1.next()) {
//                            if (cekflagging.GeneralConsentSatuSehat(rs1.getString("no_rkm_medis")) == true) {
                                if (rs1.getString("id_rtl").equals("")) {
                                    tabMode1.addRow(new Object[]{
                                        true, rs1.getString("tgl_registrasi") + "T" + rs1.getString("jam_reg") + "+07:00",
                                        rs1.getString("no_rawat"),
                                        rs1.getString("no_rkm_medis"),
                                        rs1.getString("nm_pasien"),
                                        rs1.getString("no_ktp"),
                                        rs1.getString("noktpdokter"),
                                        rs1.getString("nm_dokter"),
                                        rs1.getString("id_encounter"),
                                        rs1.getString("kd_penyakit"),
                                        rs1.getString("nm_penyakit"),
                                        rs1.getString("id_organisasi_satusehat"),
                                        rs1.getString("id_lokasi_satusehat"),
                                        rs1.getString("nm_poli"),
                                        rs1.getString("tanggal_surat"),
                                        rs1.getString("tanggal_periksakembali"),
                                        rs1.getString("id_rtl"),
                                        "SUKON" + rs1.getString("no_rawat")
                                    });
                                } else {
                                    tabMode1.addRow(new Object[]{
                                        false, rs1.getString("tgl_registrasi") + "T" + rs1.getString("jam_reg") + "+07:00",
                                        rs1.getString("no_rawat"),
                                        rs1.getString("no_rkm_medis"),
                                        rs1.getString("nm_pasien"),
                                        rs1.getString("no_ktp"),
                                        rs1.getString("noktpdokter"),
                                        rs1.getString("nm_dokter"),
                                        rs1.getString("id_encounter"),
                                        rs1.getString("kd_penyakit"),
                                        rs1.getString("nm_penyakit"),
                                        rs1.getString("id_organisasi_satusehat"),
                                        rs1.getString("id_lokasi_satusehat"),
                                        rs1.getString("nm_poli"),
                                        rs1.getString("tanggal_surat"),
                                        rs1.getString("tanggal_periksakembali"),
                                        rs1.getString("id_rtl"),
                                        "SUKON" + rs1.getString("no_rawat")
                                    });
                                }

//                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs1 != null) {
                            rs1.close();
                        }
                        if (ps1 != null) {
                            ps1.close();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : " + e);
                }
                LCount.setText("" + tabMode1.getRowCount());
                break;

            // SPRI
            case 2:
                String belumterkirim2 = "";
                if (ChkBelumTerkirim.isSelected() == true) {
                    belumterkirim2 = " satu_sehat_rtl.id_rtl IS NULL and ";
                } else {
                    belumterkirim2 = "";
                }
                Valid.tabelKosong(tabMode2);
                try {
                    ps2 = koneksi.prepareStatement(
                            "SELECT\n"
                            + "	reg_periksa.tgl_registrasi,\n"
                            + "	reg_periksa.jam_reg,\n"
                            + "	reg_periksa.no_rawat,\n"
                            + "	pasien.no_rkm_medis,\n"
                            + "	pasien.nm_pasien,\n"
                            + "	pasien.no_ktp,\n"
                            + "	bridging_surat_pri_bpjs.tgl_surat,\n"
                            + "	bridging_surat_pri_bpjs.no_surat,\n"
                            + "	bridging_surat_pri_bpjs.tgl_rencana,\n"
                            + "	satu_sehat_encounter.id_encounter,\n"
                            + "	maping_poli_bpjs.kd_poli_rs,\n"
                            + "	poliklinik.nm_poli,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_organisasi_satusehat,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,\n"
                            + "	pegawai.no_ktp noktpdokter,\n"
                            + "	dokter.nm_dokter,\n"
                            + "	diagnosa_pasien.kd_penyakit,\n"
                            + "	penyakit.nm_penyakit,\n"
                            + "	ifnull( satu_sehat_rtl.id_rtl, '' ) AS id_rtl \n"
                            + "FROM\n"
                            + "	reg_periksa\n"
                            + "	INNER JOIN bridging_surat_pri_bpjs ON reg_periksa.no_rawat = bridging_surat_pri_bpjs.no_rawat\n"
                            + "	INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis\n"
                            + "	INNER JOIN satu_sehat_encounter ON reg_periksa.no_rawat = satu_sehat_encounter.no_rawat\n"
                            + "	INNER JOIN maping_poli_bpjs ON bridging_surat_pri_bpjs.kd_poli_bpjs = maping_poli_bpjs.kd_poli_bpjs\n"
                            + "	INNER JOIN satu_sehat_mapping_lokasi_ralan ON maping_poli_bpjs.kd_poli_rs = satu_sehat_mapping_lokasi_ralan.kd_poli\n"
                            + "	INNER JOIN poliklinik ON satu_sehat_mapping_lokasi_ralan.kd_poli = poliklinik.kd_poli\n"
                            + "	INNER JOIN maping_dokter_dpjpvclaim ON bridging_surat_pri_bpjs.kd_dokter_bpjs = maping_dokter_dpjpvclaim.kd_dokter_bpjs\n"
                            + "	INNER JOIN dokter ON maping_dokter_dpjpvclaim.kd_dokter = dokter.kd_dokter\n"
                            + "	INNER JOIN pegawai ON dokter.kd_dokter = pegawai.nik\n"
                            + "	INNER JOIN diagnosa_pasien ON reg_periksa.no_rawat = diagnosa_pasien.no_rawat\n"
                            + "	INNER JOIN penyakit ON diagnosa_pasien.kd_penyakit = penyakit.kd_penyakit\n"
                            + "	LEFT JOIN satu_sehat_rtl ON reg_periksa.no_rawat = satu_sehat_rtl.no_rawat   \n"
                            + "where " + belumterkirim2 + " reg_periksa.tgl_registrasi between ? and ? "
                            + (TCari.getText().equals("") ? "" : "and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                            + "pasien.nm_pasien like ? or pasien.no_ktp like ? "
                            + "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)") + " "
                            + " GROUP BY\n"
                            + "	bridging_surat_pri_bpjs.no_surat \n"
                            + "ORDER BY\n"
                            + "	reg_periksa.tgl_registrasi ASC,\n"
                            + "	reg_periksa.jam_reg ASC,\n"
                            + "	reg_periksa.no_rawat ASC,\n"
                            + "	bridging_surat_pri_bpjs.tgl_surat ASC");
                    try {
                        ps2.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                        ps2.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                        if (!TCari.getText().equals("")) {
                            ps2.setString(3, "%" + TCari.getText() + "%");
                            ps2.setString(4, "%" + TCari.getText() + "%");
                            ps2.setString(5, "%" + TCari.getText() + "%");
                            ps2.setString(6, "%" + TCari.getText() + "%");
                            ps2.setString(7, "%" + TCari.getText() + "%");
                            ps2.setString(8, "%" + TCari.getText() + "%");
                        }
                        rs2 = ps2.executeQuery();
                        while (rs2.next()) {
//                            if (cekflagging.GeneralConsentSatuSehat(rs2.getString("no_rkm_medis")) == true) {
                                if (rs2.getString("id_rtl").equals("")) {
                                    tabMode2.addRow(new Object[]{
                                        true, rs2.getString("tgl_registrasi") + "T" + rs2.getString("jam_reg") + "+07:00",
                                        rs2.getString("no_rawat"),
                                        rs2.getString("no_rkm_medis"),
                                        rs2.getString("nm_pasien"),
                                        rs2.getString("no_ktp"),
                                        rs2.getString("noktpdokter"),
                                        rs2.getString("nm_dokter"),
                                        rs2.getString("id_encounter"),
                                        rs2.getString("kd_penyakit"),
                                        rs2.getString("nm_penyakit"),
                                        rs2.getString("id_organisasi_satusehat"),
                                        rs2.getString("id_lokasi_satusehat"),
                                        rs2.getString("nm_poli"),
                                        rs2.getString("tgl_surat"),
                                        rs2.getString("tgl_rencana"),
                                        rs2.getString("id_rtl"),
                                        "PRI" + rs2.getString("no_surat")
                                    });
                                } else {
                                    tabMode2.addRow(new Object[]{
                                        false, rs2.getString("tgl_registrasi") + "T" + rs2.getString("jam_reg") + "+07:00",
                                        rs2.getString("no_rawat"),
                                        rs2.getString("no_rkm_medis"),
                                        rs2.getString("nm_pasien"),
                                        rs2.getString("no_ktp"),
                                        rs2.getString("noktpdokter"),
                                        rs2.getString("nm_dokter"),
                                        rs2.getString("id_encounter"),
                                        rs2.getString("kd_penyakit"),
                                        rs2.getString("nm_penyakit"),
                                        rs2.getString("id_organisasi_satusehat"),
                                        rs2.getString("id_lokasi_satusehat"),
                                        rs2.getString("nm_poli"),
                                        rs2.getString("tgl_surat"),
                                        rs2.getString("tgl_rencana"),
                                        rs2.getString("id_rtl"),
                                        "PRI" + rs2.getString("no_surat")
                                    });
                                }

//                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs2 != null) {
                            rs2.close();
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : " + e);
                }
                LCount.setText("" + tabMode2.getRowCount());
                break;
            case 3:
                String belumterkirim3 = "";
                if (ChkBelumTerkirim.isSelected() == true) {
                    belumterkirim3 = " satu_sehat_rtl.id_rtl IS NULL and ";
                } else {
                    belumterkirim3 = "";
                }
                Valid.tabelKosong(tabMode3);
                try {
                    ps3 = koneksi.prepareStatement(
                            "SELECT\n"
                            + "	reg_periksa.tgl_registrasi,\n"
                            + "	reg_periksa.jam_reg,\n"
                            + "	reg_periksa.no_rawat,\n"
                            + "	pasien.no_rkm_medis,\n"
                            + "	pasien.nm_pasien,\n"
                            + "	pasien.no_ktp,\n"
                            + "	bridging_rujukan_bpjs.no_sep,\n"
                            + "	bridging_rujukan_bpjs.tglRujukan,\n"
                            + "	bridging_rujukan_bpjs.tglRencanaKunjungan,\n"
                            + "	bridging_rujukan_bpjs.jnsPelayanan,\n"
                            + "	bridging_rujukan_bpjs.nm_ppkDirujuk,\n"
                            + "	bridging_rujukan_bpjs.catatan,\n"
                            + "	bridging_rujukan_bpjs.diagRujukan,\n"
                            + "	bridging_rujukan_bpjs.nama_diagRujukan,\n"
                            + "	bridging_rujukan_bpjs.poliRujukan,\n"
                            + "	bridging_rujukan_bpjs.nama_poliRujukan,\n"
                            + "	pegawai.no_ktp AS noktpdokter,\n"
                            + "	maping_dokter_dpjpvclaim.kd_dokter,\n"
                            + "	maping_dokter_dpjpvclaim.kd_dokter_bpjs,\n"
                            + "	maping_dokter_dpjpvclaim.nm_dokter_bpjs,\n"
                            + "	satu_sehat_encounter.id_encounter,\n"
                            + "	ifnull( satu_sehat_rtl.id_rtl, '' ) AS id_rtl,\n"
                            + "	satu_sehat_mapping_lokasi_ralan.id_organisasi_satusehat \n"
                            + "FROM\n"
                            + "	reg_periksa\n"
                            + "	INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis\n"
                            + "	INNER JOIN bridging_sep ON reg_periksa.no_rawat = bridging_sep.no_rawat\n"
                            + "	INNER JOIN bridging_rujukan_bpjs ON bridging_sep.no_sep = bridging_rujukan_bpjs.no_sep\n"
                            + "	INNER JOIN maping_dokter_dpjpvclaim ON bridging_sep.kddpjp = maping_dokter_dpjpvclaim.kd_dokter_bpjs\n"
                            + "	INNER JOIN pegawai ON maping_dokter_dpjpvclaim.kd_dokter = pegawai.nik\n"
                            + "	INNER JOIN satu_sehat_encounter ON reg_periksa.no_rawat = satu_sehat_encounter.no_rawat\n"
                            + "	LEFT JOIN satu_sehat_rtl ON reg_periksa.no_rawat = satu_sehat_rtl.no_rawat\n"
                            + "	INNER JOIN satu_sehat_mapping_lokasi_ralan ON reg_periksa.kd_poli = satu_sehat_mapping_lokasi_ralan.kd_poli \n"
                            + "where " + belumterkirim3 + " reg_periksa.tgl_registrasi between ? and ? "
                            + (TCari.getText().equals("") ? "" : "and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "
                            + "pasien.nm_pasien like ? or pasien.no_ktp like ? "
                            + "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)") + " "
                            + " GROUP BY\n"
                            + "	bridging_rujukan_bpjs.no_sep  \n"
                            + "ORDER BY\n"
                            + "	reg_periksa.tgl_registrasi ASC,\n"
                            + "	reg_periksa.jam_reg ASC,\n"
                            + "	reg_periksa.no_rawat ASC");
                    try {
                        ps3.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                        ps3.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                        if (!TCari.getText().equals("")) {
                            ps3.setString(3, "%" + TCari.getText() + "%");
                            ps3.setString(4, "%" + TCari.getText() + "%");
                            ps3.setString(5, "%" + TCari.getText() + "%");
                            ps3.setString(6, "%" + TCari.getText() + "%");
                            ps3.setString(7, "%" + TCari.getText() + "%");
                            ps3.setString(8, "%" + TCari.getText() + "%");
                        }
                        rs3 = ps3.executeQuery();
                        while (rs3.next()) {
//                            if (cekflagging.GeneralConsentSatuSehat(rs3.getString("no_rkm_medis")) == true) {
                                if (rs3.getString("id_rtl").equals("")) {
                                    tabMode3.addRow(new Object[]{
                                        true,
                                        rs3.getString("tglRujukan") + "T" + rs3.getString("jam_reg") + "+07:00",
                                        rs3.getString("no_rawat"),
                                        rs3.getString("no_rkm_medis"),
                                        rs3.getString("nm_pasien"),
                                        rs3.getString("no_ktp"),
                                        rs3.getString("noktpdokter"),
                                        rs3.getString("nm_dokter_bpjs"),
                                        rs3.getString("id_encounter"),
                                        rs3.getString("diagRujukan"),
                                        rs3.getString("nama_diagRujukan"),
                                        rs3.getString("jnsPelayanan"),
                                        rs3.getString("nm_ppkDirujuk"),
                                        rs3.getString("catatan"),
                                        rs3.getString("nama_poliRujukan"),
                                        rs3.getString("id_organisasi_satusehat"),
                                        "RK" + rs3.getString("no_sep"),
                                        rs3.getString("id_rtl"),
                                        rs3.getString("tglRencanaKunjungan")
                                    });
                                } else {
                                    tabMode3.addRow(new Object[]{
                                        false,
                                        rs3.getString("tgl_registrasi") + "T" + rs3.getString("jam_reg") + "+07:00",
                                        rs3.getString("no_rawat"),
                                        rs3.getString("no_rkm_medis"),
                                        rs3.getString("nm_pasien"),
                                        rs3.getString("no_ktp"),
                                        rs3.getString("noktpdokter"),
                                        rs3.getString("nm_dokter_bpjs"),
                                        rs3.getString("id_encounter"),
                                        rs3.getString("diagRujukan"),
                                        rs3.getString("nama_diagRujukan"),
                                        rs3.getString("jnsPelayanan"),
                                        rs3.getString("nm_ppkDirujuk"),
                                        rs3.getString("catatan"),
                                        rs3.getString("nama_poliRujukan"),
                                        rs3.getString("id_organisasi_satusehat"),
                                        "RK" + rs3.getString("no_sep"),
                                        rs3.getString("id_rtl"),
                                        rs3.getString("tglRencanaKunjungan")
                                    });
                                }

//                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs3 != null) {
                            rs3.close();
                        }
                        if (ps3 != null) {
                            ps3.close();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : " + e);
                }
                LCount.setText("" + tabMode3.getRowCount());
                break;
        }
    }

    public void isCek() {
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_procedure());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_procedure());
    }

    public JTable getTable() {
        return tbKamar;
    }
}
