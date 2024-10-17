/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import inventory.DlgBarang;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

/**
 *
 * @author dosen
 */
public final class SatuSehatMapingObatAlkes2 extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final DlgBarang barang = new DlgBarang(null, false);
    private final SatuSehatReferensiObatKFA referensi = new SatuSehatReferensiObatKFA(null, false);
    private final SatuSehatReferensiNumerator numerator = new SatuSehatReferensiNumerator(null, false);
    private final SatuSehatReferensiDenominator denom = new SatuSehatReferensiDenominator(null, false);
    private final SatuSehatReferensiRoute route = new SatuSehatReferensiRoute(null, false);
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private JsonNode root;
    private int i = 0;
    private String link = "", json = "";

    /**
     * Creates new form DlgJnsPerawatanRalan
     *
     * @param parent
     * @param modal
     */
    public SatuSehatMapingObatAlkes2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[] {
            "KFA Code", "KFA System", "Kode Barang", "Nama Obat/Alkes/BHP", "Satuan", "Kategori", "KFA Display",
            "Form Code", "Form System", "Form Display", "Numerator Code", "Numerator System", "Denominator Code",
            "Denominator System", "Route Code", "Route System", "Route Display"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbJnsPerawatan.setModel(tabMode);

        tbJnsPerawatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbJnsPerawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 17; i++) {
            TableColumn column = tbJnsPerawatan.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(80);
                    break;
                case 1:
                    column.setPreferredWidth(158);
                    break;
                case 2:
                    column.setPreferredWidth(80);
                    break;
                case 3:
                    column.setPreferredWidth(200);
                    break;
                case 4:
                    column.setMinWidth(0);
                    column.setMaxWidth(0);
                    break;
                case 5:
                    column.setMinWidth(0);
                    column.setMaxWidth(0);
                    break;
                case 6:
                    column.setPreferredWidth(200);
                    break;
                case 7:
                    column.setPreferredWidth(75);
                    break;
                case 8:
                    column.setPreferredWidth(308);
                    break;
                case 9:
                    column.setPreferredWidth(130);
                    break;
                case 10:
                    column.setPreferredWidth(90);
                    break;
                case 11:
                    column.setPreferredWidth(138);
                    break;
                case 12:
                    column.setPreferredWidth(100);
                    break;
                case 13:
                    column.setPreferredWidth(313);
                    break;
                case 14:
                    column.setPreferredWidth(90);
                    break;
                case 15:
                    column.setPreferredWidth(130);
                    break;
                case 16:
                    column.setPreferredWidth(140);
                    break;
                default:
                    break;
            }
        }
        tbJnsPerawatan.setDefaultRenderer(Object.class, new WarnaTable());

        KFACode.setDocument(new batasInput((byte) 15).getKata(KFACode));
        KodeBarang.setDocument(new batasInput((byte) 15).getKata(KodeBarang));
        KFASystem.setDocument(new batasInput((byte) 100).getKata(KFASystem));
        FormCode.setDocument(new batasInput((byte) 30).getKata(FormCode));
        FormSystem.setDocument(new batasInput((byte) 100).getKata(FormSystem));
        FormDisplay.setDocument(new batasInput((byte) 80).getKata(FormDisplay));
        NumeratorCode.setDocument(new batasInput((byte) 15).getKata(NumeratorCode));
        NumeratorSystem.setDocument(new batasInput((byte) 80).getKata(NumeratorSystem));
        DenominatorCode.setDocument(new batasInput((byte) 15).getKata(DenominatorCode));
        DenominatorSystem.setDocument(new batasInput((byte) 80).getKata(DenominatorSystem));
        RouteCode.setDocument(new batasInput((byte) 30).getKata(RouteCode));
        RouteSystem.setDocument(new batasInput((byte) 100).getKata(RouteSystem));
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

        barang.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (barang.getTable().getSelectedRow() != -1) {
                    KodeBarang.setText(barang.getTable().getValueAt(barang.getTable().getSelectedRow(), 1).toString());
                    NamaBarang.setText(barang.getTable().getValueAt(barang.getTable().getSelectedRow(), 2).toString());
                    Satuan.setText(barang.getTable().getValueAt(barang.getTable().getSelectedRow(), 7).toString());
                    Kategori.setText(barang.getTable().getValueAt(barang.getTable().getSelectedRow(), 29).toString());
                }
                btnBarang.requestFocus();
            }
        });

        barang.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    barang.dispose();
                }
            }
        });

        referensi.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (referensi.getTable().getSelectedRow() != -1) {
                    link = koneksiDB.URLKFAV2SATUSEHAT() + "/products?identifier=kfa&code=" + referensi.getTable().getValueAt(referensi.getTable().getSelectedRow(), 0).toString();
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                        requestEntity = new HttpEntity(headers);
                        json = api.getRest().exchange(link, HttpMethod.GET, requestEntity, String.class).getBody();
                        root = mapper.readTree(json).path("result");
                        System.out.println("URL : " + link);
                        System.out.println("Response : " + root);
                        if (root.isObject()) {
                            KFACode.setText(referensi.getTable().getValueAt(referensi.getTable().getSelectedRow(), 0).toString());
                            KFASystem.setText("http://sys-ids.kemkes.go.id/kfa");
                            KFADisplay.setText(referensi.getTable().getValueAt(referensi.getTable().getSelectedRow(), 1).toString());
                            FormCode.setText(referensi.getTable().getValueAt(referensi.getTable().getSelectedRow(), 5).toString());
                            FormSystem.setText("http://terminology.kemkes.go.id/CodeSystem/medication-form");
                            FormDisplay.setText(referensi.getTable().getValueAt(referensi.getTable().getSelectedRow(), 6).toString());
                            NumeratorCode.setText(root.path("ucum").path("cs_code").asText());
                            NumeratorSystem.setText("http://unitsofmeasure.org");
                            DenominatorCode.setText("");
                            DenominatorSystem.setText("http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm");
                            RouteCode.setText(root.path("rute_pemberian").path("code").asText());
                            RouteSystem.setText("http://www.whocc.no/atc");
                            RouteDisplay.setText(root.path("rute_pemberian").path("name").asText());
                        }
                    } catch (Exception x) {
                        System.out.println("Notif : " + x);
                    }
                }
                KFACode.requestFocus();
            }
        });

        referensi.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    referensi.dispose();
                }
            }
        });

        numerator.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (numerator.getTable().getSelectedRow() != -1) {
                    NumeratorCode.setText(numerator.getTable().getValueAt(numerator.getTable().getSelectedRow(), 0).toString());
                    NumeratorSystem.setText(numerator.getTable().getValueAt(numerator.getTable().getSelectedRow(), 2).toString());
                }
            }
        });

        numerator.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    numerator.dispose();
                }
            }
        });

        denom.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (denom.getTable().getSelectedRow() != -1) {
                    DenominatorCode.setText(denom.getTable().getValueAt(denom.getTable().getSelectedRow(), 0).toString());
                    DenominatorSystem.setText(denom.getTable().getValueAt(denom.getTable().getSelectedRow(), 4).toString());
                }
            }
        });

        denom.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    denom.dispose();
                }
            }
        });

        route.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (route.getTable().getSelectedRow() != -1) {
                    RouteCode.setText(route.getTable().getValueAt(route.getTable().getSelectedRow(), 0).toString());
                    RouteSystem.setText(route.getTable().getValueAt(route.getTable().getSelectedRow(), 3).toString());
                    RouteDisplay.setText(route.getTable().getValueAt(route.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        route.getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    route.dispose();
                }
            }
        });

        ChkInput.setSelected(false);
        isForm();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbJnsPerawatan = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        NamaBarang = new widget.TextBox();
        btnBarang = new widget.Button();
        jLabel5 = new widget.Label();
        FormCode = new widget.TextBox();
        jLabel8 = new widget.Label();
        NumeratorCode = new widget.TextBox();
        KodeBarang = new widget.TextBox();
        jLabel9 = new widget.Label();
        jLabel10 = new widget.Label();
        KFADisplay = new widget.TextBox();
        jLabel11 = new widget.Label();
        FormSystem = new widget.TextBox();
        FormDisplay = new widget.TextBox();
        jLabel12 = new widget.Label();
        jLabel13 = new widget.Label();
        jLabel14 = new widget.Label();
        NumeratorSystem = new widget.TextBox();
        DenominatorCode = new widget.TextBox();
        KFASystem = new widget.TextBox();
        jLabel15 = new widget.Label();
        DenominatorSystem = new widget.TextBox();
        jLabel16 = new widget.Label();
        RouteCode = new widget.TextBox();
        jLabel17 = new widget.Label();
        RouteSystem = new widget.TextBox();
        jLabel18 = new widget.Label();
        RouteDisplay = new widget.TextBox();
        jLabel19 = new widget.Label();
        KFACode = new widget.TextBox();
        btnCariKFA = new widget.Button();
        Satuan = new widget.TextBox();
        Kategori = new widget.TextBox();
        btnNumer = new widget.Button();
        btnDenom = new widget.Button();
        btnRoute = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Mapping Obat/Alkes/BHP Satu Sehat KFA v2 ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJnsPerawatan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbJnsPerawatan.setName("tbJnsPerawatan"); // NOI18N
        tbJnsPerawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbJnsPerawatanMouseClicked(evt);
            }
        });
        tbJnsPerawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbJnsPerawatanKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbJnsPerawatan);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(75, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 275));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 104));
        FormInput.setLayout(null);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("KFA System :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(345, 40, 64, 23);

        NamaBarang.setEditable(false);
        NamaBarang.setHighlighter(null);
        NamaBarang.setName("NamaBarang"); // NOI18N
        FormInput.add(NamaBarang);
        NamaBarang.setBounds(212, 10, 210, 23);

        btnBarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnBarang.setMnemonic('1');
        btnBarang.setToolTipText("Alt+1");
        btnBarang.setName("btnBarang"); // NOI18N
        btnBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarangActionPerformed(evt);
            }
        });
        btnBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnBarangKeyPressed(evt);
            }
        });
        FormInput.add(btnBarang);
        btnBarang.setBounds(696, 10, 28, 23);

        jLabel5.setText("Form Code :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 100, 105, 23);

        FormCode.setHighlighter(null);
        FormCode.setName("FormCode"); // NOI18N
        FormCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FormCodeKeyPressed(evt);
            }
        });
        FormInput.add(FormCode);
        FormCode.setBounds(109, 100, 80, 23);

        jLabel8.setText("Numerator Code :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(0, 160, 105, 23);

        NumeratorCode.setHighlighter(null);
        NumeratorCode.setName("NumeratorCode"); // NOI18N
        NumeratorCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NumeratorCodeKeyPressed(evt);
            }
        });
        FormInput.add(NumeratorCode);
        NumeratorCode.setBounds(109, 160, 70, 23);

        KodeBarang.setHighlighter(null);
        KodeBarang.setName("KodeBarang"); // NOI18N
        KodeBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeBarangKeyPressed(evt);
            }
        });
        FormInput.add(KodeBarang);
        KodeBarang.setBounds(109, 10, 100, 23);

        jLabel9.setText("Obat/Alkes :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 10, 105, 23);

        jLabel10.setText("KFA Display :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 70, 105, 23);

        KFADisplay.setHighlighter(null);
        KFADisplay.setName("KFADisplay"); // NOI18N
        KFADisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KFADisplayKeyPressed(evt);
            }
        });
        FormInput.add(KFADisplay);
        KFADisplay.setBounds(109, 70, 615, 23);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Form System :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(196, 100, 69, 23);

        FormSystem.setHighlighter(null);
        FormSystem.setName("FormSystem"); // NOI18N
        FormSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FormSystemKeyPressed(evt);
            }
        });
        FormInput.add(FormSystem);
        FormSystem.setBounds(269, 100, 455, 23);

        FormDisplay.setHighlighter(null);
        FormDisplay.setName("FormDisplay"); // NOI18N
        FormDisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FormDisplayKeyPressed(evt);
            }
        });
        FormInput.add(FormDisplay);
        FormDisplay.setBounds(109, 130, 615, 23);

        jLabel12.setText("Form Display :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 130, 105, 23);

        jLabel13.setText("Denominator Code :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(0, 190, 105, 23);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Numerator System :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(188, 160, 96, 23);

        NumeratorSystem.setHighlighter(null);
        NumeratorSystem.setName("NumeratorSystem"); // NOI18N
        NumeratorSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NumeratorSystemKeyPressed(evt);
            }
        });
        FormInput.add(NumeratorSystem);
        NumeratorSystem.setBounds(288, 160, 404, 23);

        DenominatorCode.setHighlighter(null);
        DenominatorCode.setName("DenominatorCode"); // NOI18N
        DenominatorCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DenominatorCodeKeyPressed(evt);
            }
        });
        FormInput.add(DenominatorCode);
        DenominatorCode.setBounds(109, 190, 70, 23);

        KFASystem.setHighlighter(null);
        KFASystem.setName("KFASystem"); // NOI18N
        KFASystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KFASystemKeyPressed(evt);
            }
        });
        FormInput.add(KFASystem);
        KFASystem.setBounds(417, 40, 307, 23);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Denominator System :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(188, 190, 106, 23);

        DenominatorSystem.setHighlighter(null);
        DenominatorSystem.setName("DenominatorSystem"); // NOI18N
        DenominatorSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DenominatorSystemKeyPressed(evt);
            }
        });
        FormInput.add(DenominatorSystem);
        DenominatorSystem.setBounds(298, 190, 394, 23);

        jLabel16.setText("Route Code :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 220, 105, 23);

        RouteCode.setHighlighter(null);
        RouteCode.setName("RouteCode"); // NOI18N
        RouteCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RouteCodeKeyPressed(evt);
            }
        });
        FormInput.add(RouteCode);
        RouteCode.setBounds(109, 220, 70, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Route System :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(188, 220, 74, 23);

        RouteSystem.setHighlighter(null);
        RouteSystem.setName("RouteSystem"); // NOI18N
        RouteSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RouteSystemKeyPressed(evt);
            }
        });
        FormInput.add(RouteSystem);
        RouteSystem.setBounds(266, 220, 188, 23);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Route Display :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(462, 220, 73, 23);

        RouteDisplay.setHighlighter(null);
        RouteDisplay.setName("RouteDisplay"); // NOI18N
        RouteDisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RouteDisplayKeyPressed(evt);
            }
        });
        FormInput.add(RouteDisplay);
        RouteDisplay.setBounds(539, 220, 153, 23);

        jLabel19.setText("KFA Code :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 40, 105, 23);

        KFACode.setHighlighter(null);
        KFACode.setName("KFACode"); // NOI18N
        KFACode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KFACodeKeyPressed(evt);
            }
        });
        FormInput.add(KFACode);
        KFACode.setBounds(109, 40, 202, 23);

        btnCariKFA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnCariKFA.setMnemonic('1');
        btnCariKFA.setToolTipText("Alt+1");
        btnCariKFA.setName("btnCariKFA"); // NOI18N
        btnCariKFA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariKFAActionPerformed(evt);
            }
        });
        btnCariKFA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCariKFAKeyPressed(evt);
            }
        });
        FormInput.add(btnCariKFA);
        btnCariKFA.setBounds(315, 40, 28, 23);

        Satuan.setEditable(false);
        Satuan.setHighlighter(null);
        Satuan.setName("Satuan"); // NOI18N
        FormInput.add(Satuan);
        Satuan.setBounds(426, 10, 90, 23);

        Kategori.setEditable(false);
        Kategori.setHighlighter(null);
        Kategori.setName("Kategori"); // NOI18N
        FormInput.add(Kategori);
        Kategori.setBounds(520, 10, 172, 23);

        btnNumer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnNumer.setMnemonic('1');
        btnNumer.setToolTipText("Alt+1");
        btnNumer.setName("btnNumer"); // NOI18N
        btnNumer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumerActionPerformed(evt);
            }
        });
        btnNumer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnNumerKeyPressed(evt);
            }
        });
        FormInput.add(btnNumer);
        btnNumer.setBounds(696, 160, 28, 23);

        btnDenom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDenom.setMnemonic('1');
        btnDenom.setToolTipText("Alt+1");
        btnDenom.setName("btnDenom"); // NOI18N
        btnDenom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDenomActionPerformed(evt);
            }
        });
        btnDenom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDenomKeyPressed(evt);
            }
        });
        FormInput.add(btnDenom);
        btnDenom.setBounds(696, 190, 28, 23);

        btnRoute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnRoute.setMnemonic('1');
        btnRoute.setToolTipText("Alt+1");
        btnRoute.setName("btnRoute"); // NOI18N
        btnRoute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRouteActionPerformed(evt);
            }
        });
        btnRoute.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnRouteKeyPressed(evt);
            }
        });
        FormInput.add(btnRoute);
        btnRoute.setBounds(696, 220, 28, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarangActionPerformed
        akses.setform("SatuSehatMapingObatAlkes2");
        barang.emptTeks();
        barang.isCek();
        barang.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        barang.setLocationRelativeTo(internalFrame1);
        barang.setAlwaysOnTop(false);
        barang.setVisible(true);
    }//GEN-LAST:event_btnBarangActionPerformed

    private void btnBarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBarangKeyPressed
        Valid.pindah(evt, KFASystem, KFADisplay);
    }//GEN-LAST:event_btnBarangKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (KFACode.getText().isBlank()) {
            Valid.textKosong(KFACode, "KFA Code");
        } else if (KFASystem.getText().isBlank()) {
            Valid.textKosong(KFASystem, "KFA System");
        } else if (KodeBarang.getText().isBlank()) {
            Valid.textKosong(KodeBarang, "Obat/Alkes/BHP");
        } else if (KFADisplay.getText().isBlank()) {
            Valid.textKosong(KFADisplay, "KFA Display");
        } else if (FormCode.getText().isBlank()) {
            Valid.textKosong(FormCode, "Form Code");
        } else if (FormSystem.getText().isBlank()) {
            Valid.textKosong(FormSystem, "Form System");
        } else if (FormDisplay.getText().isBlank()) {
            Valid.textKosong(FormDisplay, "Form Display");
        } else if (NumeratorCode.getText().isBlank()) {
            Valid.textKosong(NumeratorCode, "Numorator Code");
        } else if (NumeratorSystem.getText().isBlank()) {
            Valid.textKosong(NumeratorSystem, "Nemerator System");
        } else if (DenominatorCode.getText().isBlank()) {
            Valid.textKosong(DenominatorCode, "Denominator Code");
        } else if (DenominatorSystem.getText().isBlank()) {
            Valid.textKosong(DenominatorSystem, "Denominator System");
        } else if (RouteCode.getText().isBlank()) {
            Valid.textKosong(RouteCode, "Route Code");
        } else if (RouteSystem.getText().isBlank()) {
            Valid.textKosong(RouteSystem, "Route System");
        } else if (RouteDisplay.getText().isBlank()) {
            Valid.textKosong(RouteDisplay, "Route Display");
        } else {
            if (Sequel.menyimpantfNotifSmc("Data Obat", "satu_sehat_mapping_obat", null,
                KodeBarang.getText(), KFACode.getText(), KFASystem.getText(), KFADisplay.getText(), FormCode.getText(), FormSystem.getText(),
                FormDisplay.getText(), NumeratorCode.getText(), NumeratorSystem.getText(), DenominatorCode.getText(), DenominatorSystem.getText(),
                RouteCode.getText(), RouteSystem.getText(), RouteDisplay.getText()
            )) {
                tabMode.addRow(new String[] {
                    KFACode.getText(), KFASystem.getText(), KodeBarang.getText(), NamaBarang.getText(), Satuan.getText(), Kategori.getText(), KFADisplay.getText(),
                    FormCode.getText(), FormSystem.getText(), FormDisplay.getText(), NumeratorCode.getText(), NumeratorSystem.getText(), DenominatorCode.getText(),
                    DenominatorSystem.getText(), RouteCode.getText(), RouteSystem.getText(), RouteDisplay.getText()
                });
                emptTeks();
                LCount.setText(String.valueOf(tabMode.getRowCount()));
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, RouteDisplay, BtnBatal);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (Valid.hapusTabletf(tabMode, NamaBarang, "satu_sehat_mapping_obat", "kode_brng") == true) {
            tabMode.removeRow(tbJnsPerawatan.getSelectedRow());
            emptTeks();
            LCount.setText("" + tabMode.getRowCount());
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (KFACode.getText().isBlank()) {
            Valid.textKosong(KFACode, "KFA Code");
        } else if (KFASystem.getText().isBlank()) {
            Valid.textKosong(KFASystem, "KFA System");
        } else if (KodeBarang.getText().isBlank()) {
            Valid.textKosong(KodeBarang, "Obat/Alkes/BHP");
        } else if (KFADisplay.getText().isBlank()) {
            Valid.textKosong(KFADisplay, "KFA Display");
        } else if (FormCode.getText().isBlank()) {
            Valid.textKosong(FormCode, "Form Code");
        } else if (FormSystem.getText().isBlank()) {
            Valid.textKosong(FormSystem, "Form System");
        } else if (FormDisplay.getText().isBlank()) {
            Valid.textKosong(FormDisplay, "Form Display");
        } else if (NumeratorCode.getText().isBlank()) {
            Valid.textKosong(NumeratorCode, "Numorator Code");
        } else if (NumeratorSystem.getText().isBlank()) {
            Valid.textKosong(NumeratorSystem, "Nemerator System");
        } else if (DenominatorCode.getText().isBlank()) {
            Valid.textKosong(DenominatorCode, "Denominator Code");
        } else if (DenominatorSystem.getText().isBlank()) {
            Valid.textKosong(DenominatorSystem, "Denominator System");
        } else if (RouteCode.getText().isBlank()) {
            Valid.textKosong(RouteCode, "Route Code");
        } else if (RouteSystem.getText().isBlank()) {
            Valid.textKosong(RouteSystem, "Route System");
        } else if (RouteDisplay.getText().isBlank()) {
            Valid.textKosong(RouteDisplay, "Route Display");
        } else {
            if (tbJnsPerawatan.getSelectedRow() > -1) {
                if (Sequel.mengupdatetfSmc("satu_sehat_mapping_obat",
                    "kode_brng = ?, obat_code = ?, obat_system = ?, obat_display = ?, form_code = ?, form_system = ?, form_display = ?, numerator_code = ?, "
                    + "numerator_system = ?, denominator_code = ?, denominator_system = ?, route_code = ?, route_system = ?, route_display = ?",
                    "kode_brng = ?",
                    KodeBarang.getText(), KFACode.getText(), KFASystem.getText(), KFADisplay.getText(), FormCode.getText(), FormSystem.getText(),
                    FormDisplay.getText(), NumeratorCode.getText(), NumeratorSystem.getText(), DenominatorCode.getText(), DenominatorSystem.getText(),
                    RouteCode.getText(), RouteSystem.getText(), RouteDisplay.getText(), tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 2).toString()
                )) {
                    tabMode.setValueAt(KFACode.getText(), tbJnsPerawatan.getSelectedRow(), 0);
                    tabMode.setValueAt(KFASystem.getText(), tbJnsPerawatan.getSelectedRow(), 1);
                    tabMode.setValueAt(KodeBarang.getText(), tbJnsPerawatan.getSelectedRow(), 2);
                    tabMode.setValueAt(NamaBarang.getText(), tbJnsPerawatan.getSelectedRow(), 3);
                    tabMode.setValueAt(Satuan.getText(), tbJnsPerawatan.getSelectedRow(), 4);
                    tabMode.setValueAt(Kategori.getText(), tbJnsPerawatan.getSelectedRow(), 5);
                    tabMode.setValueAt(KFADisplay.getText(), tbJnsPerawatan.getSelectedRow(), 6);
                    tabMode.setValueAt(FormCode.getText(), tbJnsPerawatan.getSelectedRow(), 7);
                    tabMode.setValueAt(FormSystem.getText(), tbJnsPerawatan.getSelectedRow(), 8);
                    tabMode.setValueAt(FormDisplay.getText(), tbJnsPerawatan.getSelectedRow(), 9);
                    tabMode.setValueAt(NumeratorCode.getText(), tbJnsPerawatan.getSelectedRow(), 10);
                    tabMode.setValueAt(NumeratorSystem.getText(), tbJnsPerawatan.getSelectedRow(), 11);
                    tabMode.setValueAt(DenominatorCode.getText(), tbJnsPerawatan.getSelectedRow(), 12);
                    tabMode.setValueAt(DenominatorSystem.getText(), tbJnsPerawatan.getSelectedRow(), 13);
                    tabMode.setValueAt(RouteCode.getText(), tbJnsPerawatan.getSelectedRow(), 14);
                    tabMode.setValueAt(RouteSystem.getText(), tbJnsPerawatan.getSelectedRow(), 15);
                    tabMode.setValueAt(RouteDisplay.getText(), tbJnsPerawatan.getSelectedRow(), 16);
                    emptTeks();
                }
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
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
            Valid.MyReport("rptMapingKFASatuSehat.jasper", "report", "::[ Mapping Obat/Alkes/BHP Satu Sehat Kemenkes ]::", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

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

    private void tbJnsPerawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbJnsPerawatanMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbJnsPerawatanMouseClicked

    private void tbJnsPerawatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJnsPerawatanKeyReleased
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbJnsPerawatanKeyReleased

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void KodeBarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeBarangKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select databarang.nama_brng, kodesatuan.satuan, kategori_barang.nama as nama_kategori from databarang "
                + "join kodesatuan on databarang.kode_sat = kodesatuan.kode_sat join kategori_barang on "
                + "databarang.kode_kategori = kategori_barang.kode where databarang.kode_brng = ?"
            )) {
                ps.setString(1, KodeBarang.getText());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        NamaBarang.setText(rs.getString("nama_brng"));
                        Satuan.setText(rs.getString("satuan"));
                        Kategori.setText(rs.getString("nama_kategori"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        } else {
            Valid.pindah(evt, TCari, KFASystem);
        }
    }//GEN-LAST:event_KodeBarangKeyPressed

    private void KFASystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KFASystemKeyPressed
        Valid.pindah(evt, btnBarang, KFADisplay);
    }//GEN-LAST:event_KFASystemKeyPressed

    private void KFADisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KFADisplayKeyPressed
        Valid.pindah(evt, KFASystem, FormCode);
    }//GEN-LAST:event_KFADisplayKeyPressed

    private void FormCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FormCodeKeyPressed
        Valid.pindah(evt, KFADisplay, FormSystem);
    }//GEN-LAST:event_FormCodeKeyPressed

    private void FormSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FormSystemKeyPressed
        Valid.pindah(evt, FormCode, FormDisplay);
    }//GEN-LAST:event_FormSystemKeyPressed

    private void FormDisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FormDisplayKeyPressed
        Valid.pindah(evt, FormSystem, NumeratorCode);
    }//GEN-LAST:event_FormDisplayKeyPressed

    private void NumeratorCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NumeratorCodeKeyPressed
        Valid.pindah(evt, FormDisplay, NumeratorSystem);
    }//GEN-LAST:event_NumeratorCodeKeyPressed

    private void NumeratorSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NumeratorSystemKeyPressed
        Valid.pindah(evt, NumeratorCode, DenominatorCode);
    }//GEN-LAST:event_NumeratorSystemKeyPressed

    private void DenominatorCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DenominatorCodeKeyPressed
        Valid.pindah(evt, NumeratorSystem, DenominatorSystem);
    }//GEN-LAST:event_DenominatorCodeKeyPressed

    private void DenominatorSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DenominatorSystemKeyPressed
        Valid.pindah(evt, DenominatorCode, RouteCode);
    }//GEN-LAST:event_DenominatorSystemKeyPressed

    private void RouteCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RouteCodeKeyPressed
        Valid.pindah(evt, DenominatorSystem, RouteSystem);
    }//GEN-LAST:event_RouteCodeKeyPressed

    private void RouteSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RouteSystemKeyPressed
        Valid.pindah(evt, RouteCode, RouteDisplay);
    }//GEN-LAST:event_RouteSystemKeyPressed

    private void RouteDisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RouteDisplayKeyPressed
        Valid.pindah(evt, RouteSystem, BtnSimpan);
    }//GEN-LAST:event_RouteDisplayKeyPressed

    private void KFACodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KFACodeKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KFACodeKeyPressed

    private void btnCariKFAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariKFAActionPerformed
        akses.setform("SatuSehatMapingObatAlkes2");
        referensi.emptTeks();
        referensi.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        referensi.setLocationRelativeTo(internalFrame1);
        referensi.setVisible(true);
    }//GEN-LAST:event_btnCariKFAActionPerformed

    private void btnCariKFAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCariKFAKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnCariKFAActionPerformed(null);
        }
    }//GEN-LAST:event_btnCariKFAKeyPressed

    private void btnNumerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumerActionPerformed
        numerator.emptTeks();
        numerator.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        numerator.setLocationRelativeTo(internalFrame1);
        numerator.setVisible(true);
    }//GEN-LAST:event_btnNumerActionPerformed

    private void btnNumerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnNumerKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnNumerKeyPressed

    private void btnDenomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDenomActionPerformed
        denom.emptTeks();
        denom.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        denom.setLocationRelativeTo(internalFrame1);
        denom.setVisible(true);
    }//GEN-LAST:event_btnDenomActionPerformed

    private void btnDenomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDenomKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDenomKeyPressed

    private void btnRouteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRouteActionPerformed
        route.emptTeks();
        route.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        route.setLocationRelativeTo(internalFrame1);
        route.setVisible(true);
    }//GEN-LAST:event_btnRouteActionPerformed

    private void btnRouteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnRouteKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRouteKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatMapingObatAlkes2 dialog = new SatuSehatMapingObatAlkes2(new javax.swing.JFrame(), true);
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
    private widget.CekBox ChkInput;
    private widget.TextBox DenominatorCode;
    private widget.TextBox DenominatorSystem;
    private widget.TextBox FormCode;
    private widget.TextBox FormDisplay;
    private widget.PanelBiasa FormInput;
    private widget.TextBox FormSystem;
    private widget.TextBox KFACode;
    private widget.TextBox KFADisplay;
    private widget.TextBox KFASystem;
    private widget.TextBox Kategori;
    private widget.TextBox KodeBarang;
    private widget.Label LCount;
    private widget.TextBox NamaBarang;
    private widget.TextBox NumeratorCode;
    private widget.TextBox NumeratorSystem;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox RouteCode;
    private widget.TextBox RouteDisplay;
    private widget.TextBox RouteSystem;
    private widget.TextBox Satuan;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Button btnBarang;
    private widget.Button btnCariKFA;
    private widget.Button btnDenom;
    private widget.Button btnNumer;
    private widget.Button btnRoute;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJnsPerawatan;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select satu_sehat_mapping_obat.obat_code, satu_sehat_mapping_obat.obat_system, databarang.kode_brng, databarang.nama_brng, kodesatuan.satuan, "
            + "kategori_barang.nama as nama_kategori, satu_sehat_mapping_obat.obat_display, satu_sehat_mapping_obat.form_code, satu_sehat_mapping_obat.form_system, "
            + "satu_sehat_mapping_obat.form_display, satu_sehat_mapping_obat.numerator_code, satu_sehat_mapping_obat.numerator_system, satu_sehat_mapping_obat.denominator_code, "
            + "satu_sehat_mapping_obat.denominator_system, satu_sehat_mapping_obat.route_code, satu_sehat_mapping_obat.route_system, satu_sehat_mapping_obat.route_display "
            + "from satu_sehat_mapping_obat join databarang on satu_sehat_mapping_obat.kode_brng = databarang.kode_brng join kodesatuan on databarang.kode_sat = kodesatuan.kode_sat "
            + "join kategori_barang on databarang.kode_kategori = kategori_barang.kode where satu_sehat_mapping_obat.obat_code like ? or satu_sehat_mapping_obat.obat_system like ? "
            + "or databarang.kode_brng like ? or databarang.nama_brng like ? or kodesatuan.satuan like ? or kategori_barang.nama like ? or satu_sehat_mapping_obat.obat_display like ? "
            + "or satu_sehat_mapping_obat.form_code like ? or satu_sehat_mapping_obat.form_system like ? or satu_sehat_mapping_obat.form_display like ? or satu_sehat_mapping_obat.numerator_code like ? "
            + "or satu_sehat_mapping_obat.numerator_system like ? or satu_sehat_mapping_obat.denominator_code like ? or satu_sehat_mapping_obat.denominator_system like ? "
            + "or satu_sehat_mapping_obat.route_code like ? or satu_sehat_mapping_obat.route_system like ? or satu_sehat_mapping_obat.route_display like ?"
        )) {
            if (TCari.getText().isBlank()) {
                for (int i = 1; i <= 17; i++) {
                    ps.setString(i, "%");
                }
            } else {
                for (int i = 1; i <= 17; i++) {
                    ps.setString(i, "%" + TCari.getText() + "%");
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new String[] {
                        rs.getString("obat_code"), rs.getString("obat_system"), rs.getString("kode_brng"), rs.getString("nama_brng"), rs.getString("satuan"),
                        rs.getString("nama_kategori"), rs.getString("obat_display"), rs.getString("form_code"), rs.getString("form_system"), rs.getString("form_display"),
                        rs.getString("numerator_code"), rs.getString("numerator_system"), rs.getString("denominator_code"), rs.getString("denominator_system"),
                        rs.getString("route_code"), rs.getString("route_system"), rs.getString("route_display")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }

    public void emptTeks() {
        KFACode.setText("");
        KFASystem.setText("");
        KodeBarang.setText("");
        NamaBarang.setText("");
        Satuan.setText("");
        Kategori.setText("");
        KFADisplay.setText("");
        FormCode.setText("");
        FormSystem.setText("");
        FormDisplay.setText("");
        NumeratorCode.setText("");
        NumeratorSystem.setText("");
        DenominatorCode.setText("");
        DenominatorSystem.setText("");
        RouteCode.setText("");
        RouteSystem.setText("");
        RouteDisplay.setText("");
        ChkInput.setSelected(true);
        isForm();
        KodeBarang.requestFocus();
        tbJnsPerawatan.clearSelection();
    }

    private void getData() {
        if (tbJnsPerawatan.getSelectedRow() != -1) {
            KFACode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 0).toString());
            KFASystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 1).toString());
            KodeBarang.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 2).toString());
            NamaBarang.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 3).toString());
            Satuan.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 4).toString());
            Kategori.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 5).toString());
            KFADisplay.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 6).toString());
            FormCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 7).toString());
            FormSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 8).toString());
            FormDisplay.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 9).toString());
            NumeratorCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 10).toString());
            NumeratorSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 11).toString());
            DenominatorCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 12).toString());
            DenominatorSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 13).toString());
            RouteCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 14).toString());
            RouteSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 15).toString());
            RouteDisplay.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(), 16).toString());
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getsatu_sehat_mapping_obat());
        BtnHapus.setEnabled(akses.getsatu_sehat_mapping_obat());
        BtnEdit.setEnabled(akses.getsatu_sehat_mapping_obat());
        BtnPrint.setEnabled(akses.getsatu_sehat_mapping_obat());
    }

    public JTable getTable() {
        return tbJnsPerawatan;
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 275));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }
}
