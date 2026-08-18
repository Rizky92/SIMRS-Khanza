/*
  Kontribusi dari Mas Abdul Wahid RSUD Cipayung & Mas Fanji dari RSUD Kramatjati

 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author dosen
 */
public final class DlgValidasiMappingSMC extends javax.swing.JDialog {
    public static final String SUMBER_LAB = "lab";
    public static final String SUMBER_RADIOLOGI = "radiologi";

    private static final String SYSTEM_LOINC = "http://loinc.org";
    private static final String SYSTEM_SNOMED = "http://snomed.info/sct";
    private static final String JENIS_PEMERIKSAAN = "Pemeriksaan";
    private static final String JENIS_SAMPEL = "Sampel";
    private static final int PANJANG_DISPLAY = 80;

    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final String sumber;
    private final String URLAPILOINCSMC = koneksiDB.URLAPILOINCSMC(),
                         USERAPILOINCSMC = koneksiDB.USERAPILOINCSMC(),
                         PASSAPILOINCSMC = koneksiDB.PASSAPILOINCSMC(),
                         URLAPISNOWSTORMSMC = koneksiDB.URLAPISNOWSTORMSMC();
    private final Map<String, String[]> cacheLOINC = new HashMap<>();
    private final Map<String, String[]> cacheSNOMED = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private List<String[]> dataSmc = null;
    private volatile boolean ceksukses = false;

    /**
     * Creates new form DlgValidasiMappingSMC
     *
     * @param parent
     * @param modal
     * @param sumber
     */
    public DlgValidasiMappingSMC(java.awt.Frame parent, boolean modal, String sumber) {
        super(parent, modal);
        this.sumber = SUMBER_RADIOLOGI.equals(sumber) ? SUMBER_RADIOLOGI : SUMBER_LAB;
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {
                "Terima", "Jenis", "Kunci", "Nama Pemeriksaan", "Code", "System Tersimpan", "Display Tersimpan",
                "System Seharusnya", "Display Seharusnya", "Masalah"
            }) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return 0 == columnIndex ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return 0 == colIndex && perluKoreksi(rowIndex);
            }
        };

        tbValidasi.setModel(tabMode);
        tbValidasi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbValidasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbValidasi.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(55);
            } else if (i == 1) {
                column.setPreferredWidth(90);
            } else if (i == 2) {
                column.setPreferredWidth(90);
            } else if (i == 3) {
                column.setPreferredWidth(230);
            } else if (i == 4) {
                column.setPreferredWidth(100);
            } else if (i == 5) {
                column.setPreferredWidth(160);
            } else if (i == 6) {
                column.setPreferredWidth(260);
            } else if (i == 7) {
                column.setPreferredWidth(160);
            } else if (i == 8) {
                column.setPreferredWidth(260);
            } else if (i == 9) {
                column.setPreferredWidth(230);
            }
        }
        tbValidasi.setDefaultRenderer(Object.class, new WarnaTable());

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            SUMBER_RADIOLOGI.equals(this.sumber) ? "::[ Validasi Mapping Kode SatuSehat Radiologi ]::" : "::[ Validasi Mapping Kode SatuSehat Laboratorium ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Popup = new javax.swing.JPopupMenu();
        MnTandaiSemua = new javax.swing.JMenuItem();
        MnTandaiDisplay = new javax.swing.JMenuItem();
        MnHapusTanda = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbValidasi = new widget.Table();
        panelGlass6 = new widget.panelisi();
        ChkKosong = new widget.CekBox();
        BtnValidasi = new widget.Button();
        BtnTerima = new widget.Button();
        jLabel6 = new widget.Label();
        LProgres = new widget.Label();
        jLabel8 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        Popup.setName("Popup"); // NOI18N

        MnTandaiSemua.setBackground(new java.awt.Color(255, 255, 254));
        MnTandaiSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTandaiSemua.setForeground(new java.awt.Color(50, 50, 50));
        MnTandaiSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/checked.png"))); // NOI18N
        MnTandaiSemua.setText("Tandai Semua Koreksi");
        MnTandaiSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTandaiSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTandaiSemua.setName("MnTandaiSemua"); // NOI18N
        MnTandaiSemua.setPreferredSize(new java.awt.Dimension(240, 30));
        MnTandaiSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTandaiSemuaActionPerformed(evt);
            }
        });
        Popup.add(MnTandaiSemua);

        MnTandaiDisplay.setBackground(new java.awt.Color(255, 255, 254));
        MnTandaiDisplay.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTandaiDisplay.setForeground(new java.awt.Color(50, 50, 50));
        MnTandaiDisplay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/checked.png"))); // NOI18N
        MnTandaiDisplay.setText("Tandai Koreksi Display Saja");
        MnTandaiDisplay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTandaiDisplay.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTandaiDisplay.setName("MnTandaiDisplay"); // NOI18N
        MnTandaiDisplay.setPreferredSize(new java.awt.Dimension(240, 30));
        MnTandaiDisplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTandaiDisplayActionPerformed(evt);
            }
        });
        Popup.add(MnTandaiDisplay);

        MnHapusTanda.setBackground(new java.awt.Color(255, 255, 254));
        MnHapusTanda.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnHapusTanda.setForeground(new java.awt.Color(50, 50, 50));
        MnHapusTanda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cancel.png"))); // NOI18N
        MnHapusTanda.setText("Hapus Semua Tanda");
        MnHapusTanda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnHapusTanda.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnHapusTanda.setName("MnHapusTanda"); // NOI18N
        MnHapusTanda.setPreferredSize(new java.awt.Dimension(240, 30));
        MnHapusTanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnHapusTandaActionPerformed(evt);
            }
        });
        Popup.add(MnHapusTanda);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Validasi Mapping Kode SatuSehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout());

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbValidasi.setComponentPopupMenu(Popup);
        tbValidasi.setName("tbValidasi"); // NOI18N
        Scroll.setViewportView(tbValidasi);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        ChkKosong.setText("Sertakan Belum Dimaping");
        ChkKosong.setName("ChkKosong"); // NOI18N
        ChkKosong.setPreferredSize(new java.awt.Dimension(180, 23));
        panelGlass6.add(ChkKosong);

        BtnValidasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnValidasi.setMnemonic('V');
        BtnValidasi.setText("Validasi");
        BtnValidasi.setToolTipText("Alt+V");
        BtnValidasi.setName("BtnValidasi"); // NOI18N
        BtnValidasi.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnValidasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnValidasiActionPerformed(evt);
            }
        });
        BtnValidasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnValidasiKeyPressed(evt);
            }
        });
        panelGlass6.add(BtnValidasi);

        BtnTerima.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnTerima.setMnemonic('P');
        BtnTerima.setText("Terapkan");
        BtnTerima.setToolTipText("Alt+P");
        BtnTerima.setName("BtnTerima"); // NOI18N
        BtnTerima.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnTerima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTerimaActionPerformed(evt);
            }
        });
        BtnTerima.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnTerimaKeyPressed(evt);
            }
        });
        panelGlass6.add(BtnTerima);

        jLabel6.setText("Progres :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass6.add(jLabel6);

        LProgres.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LProgres.setText("0 / 0");
        LProgres.setName("LProgres"); // NOI18N
        LProgres.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass6.add(LProgres);

        jLabel8.setText("Bermasalah :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass6.add(jLabel8);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass6.add(LCount);

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

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        BtnValidasi.requestFocus();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        isCek();
    }//GEN-LAST:event_formWindowOpened

    private void BtnValidasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnValidasiActionPerformed
        validasi();
    }//GEN-LAST:event_BtnValidasiActionPerformed

    private void BtnValidasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnValidasiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnValidasiActionPerformed(null);
        }
    }//GEN-LAST:event_BtnValidasiKeyPressed

    private void MnTandaiSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTandaiSemuaActionPerformed
        tandai(true, false);
    }//GEN-LAST:event_MnTandaiSemuaActionPerformed

    private void MnTandaiDisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTandaiDisplayActionPerformed
        tandai(true, true);
    }//GEN-LAST:event_MnTandaiDisplayActionPerformed

    private void MnHapusTandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnHapusTandaActionPerformed
        tandai(false, false);
    }//GEN-LAST:event_MnHapusTandaActionPerformed

    private void BtnTerimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTerimaActionPerformed
        terapkan();
    }//GEN-LAST:event_BtnTerimaActionPerformed

    private void BtnTerimaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnTerimaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnTerimaActionPerformed(null);
        }
    }//GEN-LAST:event_BtnTerimaKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgValidasiMappingSMC dialog = new DlgValidasiMappingSMC(new javax.swing.JFrame(), true, SUMBER_LAB);
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
    private widget.Button BtnTerima;
    private widget.Button BtnValidasi;
    private widget.CekBox ChkKosong;
    private widget.Label LCount;
    private widget.Label LProgres;
    private javax.swing.JMenuItem MnHapusTanda;
    private javax.swing.JMenuItem MnTandaiDisplay;
    private javax.swing.JMenuItem MnTandaiSemua;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel6;
    private widget.Label jLabel8;
    private widget.panelisi panelGlass6;
    private widget.Table tbValidasi;
    // End of variables declaration//GEN-END:variables

    public void isCek() {
        BtnTerima.setEnabled(SUMBER_RADIOLOGI.equals(sumber) ? akses.getsatu_sehat_mapping_radiologi() : akses.getsatu_sehat_mapping_lab());
    }

    public JTable getTable() {
        return tbValidasi;
    }

    public void setData(JTable tabel, int[] urutan) {
        List<String[]> data = new ArrayList<>();

        for (int i = 0; i < tabel.getRowCount(); i++) {
            String[] baris = new String[urutan.length];

            for (int j = 0; j < urutan.length; j++) {
                baris[j] = bersihkan(null == tabel.getValueAt(i, urutan[j]) ? "" : tabel.getValueAt(i, urutan[j]).toString());
            }

            data.add(baris);
        }

        dataSmc = data;
    }

    @Override
    public void dispose() {
        ceksukses = false;
        super.dispose();
    }

    private boolean perluKoreksi(int baris) {
        return !tabMode.getValueAt(baris, 7).toString().isBlank() || !tabMode.getValueAt(baris, 8).toString().isBlank();
    }

    private void validasi() {
        if (ceksukses) {
            return;
        }

        ceksukses = true;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Valid.tabelKosongSmc(tabMode);
        LCount.setText("0");
        LProgres.setText("0 / 0");
        BtnValidasi.setEnabled(false);
        BtnTerima.setEnabled(false);


        new SwingWorker<Void, Object[]>() {
            final boolean sertakanKosong = ChkKosong.isSelected();
            private int total = 0;
            private int diperiksa = 0;

            @Override
            protected Void doInBackground() throws Exception {
                List<String[]> daftar = null == dataSmc ? ambilMapping() : dataSmc;
                total = daftar.size();
                laporProgress(0, total);

                for (String[] baris : daftar) {
                    if (!ceksukses) {
                        break;
                    }

                    validasiKode(baris, JENIS_PEMERIKSAAN);
                    validasiKode(baris, JENIS_SAMPEL);
                    diperiksa++;
                    laporProgress(diperiksa, total);
                }

                return null;
            }

            private void validasiKode(String[] baris, String jenis) {
                boolean sampel = JENIS_SAMPEL.equals(jenis);
                String kunci = baris[0], nama = baris[1];
                String code = bersihkan(sampel ? baris[5] : baris[2]);
                String system = bersihkan(sampel ? baris[6] : baris[3]);
                String display = bersihkan(sampel ? baris[7] : baris[4]);
                String systemBenar = sampel ? SYSTEM_SNOMED : SYSTEM_LOINC;

                if (code.isBlank()) {
                    if (sertakanKosong) {
                        publish(new Object[] {false, jenis, kunci, nama, "", system, display, "", "", sampel ? "Kode SNOMED CT belum dipetakan" : "Kode LOINC belum dipetakan"});
                    }
                    return;
                }

                String[] referensi = sampel ? cariSNOMED(code) : cariLOINC(code);

                if (null == referensi) {
                    publish(new Object[] {false, jenis, kunci, nama, code, system, display, "", "", sampel ? "Gagal menghubungi API SNOMED CT" : "Gagal menghubungi API LOINC"});
                    return;
                }

                if (0 == referensi.length) {
                    publish(new Object[] {false, jenis, kunci, nama, code, system, display, "", "", sampel ? "Kode SNOMED CT tidak ditemukan" : "Kode LOINC tidak ditemukan"});
                    return;
                }

                List<String> masalah = new ArrayList<>();
                String systemKoreksi = "", displayKoreksi = "";
                String displayBenar = potong(referensi[0], PANJANG_DISPLAY);

                if (!systemBenar.equals(system)) {
                    systemKoreksi = systemBenar;
                    masalah.add("System bukan " + systemBenar);
                }

                if (!displayBenar.equalsIgnoreCase(display)) {
                    displayKoreksi = displayBenar;
                    masalah.add("Display tidak sesuai referensi");

                    if (referensi[0].trim().length() > PANJANG_DISPLAY) {
                        masalah.add("Display dipotong " + PANJANG_DISPLAY + " karakter");
                    }
                }

                if (sampel) {
                    if ("true".equalsIgnoreCase(referensi[1])) {
                        masalah.add("Konsep SNOMED CT tidak aktif");
                    }
                } else if (!referensi[1].isBlank() && !"ACTIVE".equalsIgnoreCase(referensi[1])) {
                    masalah.add("Status LOINC " + referensi[1]);
                }

                if (masalah.isEmpty()) {
                    return;
                }

                publish(new Object[] {!systemKoreksi.isBlank() || !displayKoreksi.isBlank(), jenis, kunci, nama, code, system, display, systemKoreksi, displayKoreksi, String.join(", ", masalah)});
            }

            @Override
            protected void process(List<Object[]> chunks) {
                chunks.forEach(tabMode::addRow);
                LCount.setText(tabMode.getRowCount() + "");
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
                ceksukses = false;

                if (!isDisplayable()) {
                    return;
                }

                tabMode.fireTableDataChanged();
                LCount.setText(tabMode.getRowCount() + "");
                BtnValidasi.setEnabled(true);
                isCek();
                DlgValidasiMappingSMC.this.setCursor(Cursor.getDefaultCursor());

                if (0 == tabMode.getRowCount()) {
                    JOptionPane.showMessageDialog(null, "Seluruh mapping sudah sesuai referensi.");
                }
            }
        }.execute();
    }

    private void laporProgress(int diperiksa, int total) {
        SwingUtilities.invokeLater(() -> LProgres.setText(diperiksa + " / " + total));
    }

    private List<String[]> ambilMapping() throws Exception {
        List<String[]> daftar = new ArrayList<>();
        String sql = SUMBER_RADIOLOGI.equals(sumber)
            ? "select satu_sehat_mapping_radiologi.kd_jenis_prw as kunci, jns_perawatan_radiologi.nm_perawatan as nama, satu_sehat_mapping_radiologi.code, " +
              "satu_sehat_mapping_radiologi.system, satu_sehat_mapping_radiologi.display, satu_sehat_mapping_radiologi.sampel_code, satu_sehat_mapping_radiologi.sampel_system, " +
              "satu_sehat_mapping_radiologi.sampel_display from satu_sehat_mapping_radiologi inner join jns_perawatan_radiologi on " +
              "satu_sehat_mapping_radiologi.kd_jenis_prw = jns_perawatan_radiologi.kd_jenis_prw order by satu_sehat_mapping_radiologi.kd_jenis_prw"
            : "select satu_sehat_mapping_lab.id_template as kunci, template_laboratorium.Pemeriksaan as nama, satu_sehat_mapping_lab.code, satu_sehat_mapping_lab.system, " +
              "satu_sehat_mapping_lab.display, satu_sehat_mapping_lab.sampel_code, satu_sehat_mapping_lab.sampel_system, satu_sehat_mapping_lab.sampel_display " +
              "from satu_sehat_mapping_lab inner join template_laboratorium on satu_sehat_mapping_lab.id_template = template_laboratorium.id_template " +
              "order by satu_sehat_mapping_lab.id_template";

        try (ResultSet rs = koneksi.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                daftar.add(new String[] {
                    rs.getString("kunci"), rs.getString("nama"), rs.getString("code"), rs.getString("system"),
                    rs.getString("display"), rs.getString("sampel_code"), rs.getString("sampel_system"), rs.getString("sampel_display")
                });
            }
        }

        return daftar;
    }

    private String[] cariLOINC(String code) {
        if (cacheLOINC.containsKey(code)) {
            return cacheLOINC.get(code);
        }

        String[] hasil = getLOINC(code, "LOINC_NUM:\"" + code + "\"");

        if (null == hasil || 0 == hasil.length) {
            String[] cadangan = getLOINC(code, code);
            if (null != cadangan) {
                hasil = cadangan;
            }
        }

        if (null == hasil) {
            return null;
        }

        cacheLOINC.put(code, hasil);
        return hasil;
    }

    private String[] getLOINC(String code, String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Basic " + Base64.encodeBase64String((USERAPILOINCSMC + ":" + PASSAPILOINCSMC).getBytes()));

            URIBuilder builder = new URIBuilder(URLAPILOINCSMC + "/loincs");
            builder.addParameter("rows", "20");
            builder.addParameter("query", query);

            JsonNode root = mapper.readTree(http().exchange(builder.build(), HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody());

            for (JsonNode node : root.withArray("Results")) {
                if (code.equalsIgnoreCase(node.path("LOINC_NUM").asText(""))) {
                    return new String[] {node.path("LONG_COMMON_NAME").asText(""), node.path("STATUS").asText("")};
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            return null;
        }

        return new String[0];
    }

    private String[] cariSNOMED(String code) {
        if (cacheSNOMED.containsKey(code)) {
            return cacheSNOMED.get(code);
        }

        String[] hasil = new String[0];

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            URIBuilder uri = new URIBuilder(URLAPISNOWSTORMSMC + "/CodeSystem/$lookup");
            uri.addParameter("system", SYSTEM_SNOMED);
            uri.addParameter("code", code);
            uri.addParameter("property", "*");

            JsonNode root = mapper.readTree(http().exchange(uri.build(), HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody());
            String display = "", inactive = "";

            for (JsonNode param : root.path("parameter")) {
                if ("display".equals(param.path("name").asText())) {
                    display = param.path("valueString").asText("");
                    continue;
                }

                if (!"property".equals(param.path("name").asText())) {
                    continue;
                }

                String kode = "", nilai = "";
                for (JsonNode part : param.path("part")) {
                    if ("code".equals(part.path("name").asText())) {
                        kode = part.path("valueCode").asText("");
                    } else if ("value".equals(part.path("name").asText())) {
                        nilai = part.path("valueBoolean").asText(part.path("valueString").asText(part.path("valueCode").asText("")));
                    }
                }

                if ("inactive".equals(kode)) {
                    inactive = nilai;
                }
            }

            if (!display.isBlank()) {
                hasil = new String[] {display, inactive};
            }
        } catch (HttpStatusCodeException e) {
            System.out.println("Notif : " + e);
            if (404 != e.getStatusCode().value()) {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            return null;
        }

        cacheSNOMED.put(code, hasil);
        return hasil;
    }

    private void tandai(boolean tandai, boolean hanyaDisplay) {
        for (int i = 0; i < tabMode.getRowCount(); i++) {
            if (!perluKoreksi(i)) {
                continue;
            }

            tabMode.setValueAt(tandai && (!hanyaDisplay || !tabMode.getValueAt(i, 8).toString().isBlank()), i, 0);
        }
    }

    private void terapkan() {
        if (ceksukses) {
            return;
        }

        int terpilih = 0;
        for (int i = 0; i < tabMode.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tabMode.getValueAt(i, 0)) && perluKoreksi(i)) {
                terpilih++;
            }
        }

        if (0 == terpilih) {
            JOptionPane.showMessageDialog(null, "Silahkan tandai koreksi yang ingin diterapkan..!!");
            return;
        }

        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Terapkan " + terpilih + " koreksi mapping..?", "Konfirmasi", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        String tabel = SUMBER_RADIOLOGI.equals(sumber) ? "satu_sehat_mapping_radiologi" : "satu_sehat_mapping_lab";
        String kolomKunci = SUMBER_RADIOLOGI.equals(sumber) ? "kd_jenis_prw" : "id_template";
        int berhasil = 0;

        for (int i = tabMode.getRowCount() - 1; i >= 0; i--) {
            if (!Boolean.TRUE.equals(tabMode.getValueAt(i, 0)) || !perluKoreksi(i)) {
                continue;
            }

            boolean sampel = JENIS_SAMPEL.equals(tabMode.getValueAt(i, 1).toString());
            String systemKoreksi = tabMode.getValueAt(i, 7).toString();
            String displayKoreksi = tabMode.getValueAt(i, 8).toString();
            List<String> kolom = new ArrayList<>();
            List<String> nilai = new ArrayList<>();

            if (!systemKoreksi.isBlank()) {
                kolom.add((sampel ? "sampel_system" : "system") + "=?");
                nilai.add(systemKoreksi);
            }

            if (!displayKoreksi.isBlank()) {
                kolom.add((sampel ? "sampel_display" : "display") + "=?");
                nilai.add(displayKoreksi);
            }

            nilai.add(tabMode.getValueAt(i, 2).toString());

            if (Sequel.mengupdatetfSmc(tabel, String.join(", ", kolom), kolomKunci + "=?", nilai.toArray(String[]::new))) {
                berhasil++;
                tabMode.removeRow(i);
            }
        }

        tabMode.fireTableDataChanged();
        LCount.setText(tabMode.getRowCount() + "");
        JOptionPane.showMessageDialog(null, berhasil + " dari " + terpilih + " koreksi berhasil diterapkan.");
    }

    private String bersihkan(String value) {
        return null == value ? "" : value.trim();
    }

    private String potong(String value, int panjang) {
        String hasil = bersihkan(value);
        return hasil.length() > panjang ? hasil.substring(0, panjang).trim() : hasil;
    }

    private RestTemplate http() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            }
        };

        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }
}
