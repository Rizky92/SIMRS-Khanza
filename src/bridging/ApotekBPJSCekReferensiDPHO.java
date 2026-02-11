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
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import javax.swing.SwingUtilities;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSCekReferensiDPHO extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final validasi Valid = new validasi();
    private final sekuel Sequel = new sekuel();
    private final ApiApotekBPJS api = new ApiApotekBPJS();
    private final ObjectMapper mapper = new ObjectMapper();
    private String URL="",link="",utc="";
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    /** Creates new form DlgKamar
     * @param parent
     * @param modal
     */
    public ApotekBPJSCekReferensiDPHO(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10, 2);
        setSize(628, 674);

        tabMode=new DefaultTableModel(null,new String[]{
                "Kode Obat","Nama Obat","PRB","Kronis","Kemo","Harga","Restriksi","Generik","Aktif","Sedia","Stok"
            }){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
            Class[] types = new Class[]{
                java.lang.Object.class,java.lang.Object.class,java.lang.Boolean.class,java.lang.Boolean.class,java.lang.Boolean.class,
                java.lang.Double.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class
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

        for (int i = 0; i < 11; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(90);
            } else if (i == 1) {
                column.setPreferredWidth(170);
            } else if (i == 2) {
                column.setPreferredWidth(50);
            } else if (i == 3) {
                column.setPreferredWidth(50);
            } else if (i == 4) {
                column.setPreferredWidth(50);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(200);
            } else if (i == 7) {
                column.setPreferredWidth(150);
            } else if (i == 8) {
                column.setPreferredWidth(60);
            }else if(i==9){
                column.setPreferredWidth(40);
            }else if(i==10){
                column.setPreferredWidth(40);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());

        Poli.setDocument(new batasInput((byte)100).getKata(Poli));

        try {
            link=koneksiDB.URLAPIAPOTEKBPJS();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }

    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelGlass6 = new widget.panelisi();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel17 = new widget.Label();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pencarian Data Referensi DPHO Apotek BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setAutoCreateRowSorter(true);
        tbKamar.setName("tbKamar"); // NOI18N
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel16.setText("Key Word :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass6.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(300, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass6.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
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

        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass6.add(jLabel17);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
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
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
        } else if (tabMode.getRowCount() != 0) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.deleteTemporary();
            for (int r = 0; r < tabMode.getRowCount(); r++) {
                Sequel.temporary(String.valueOf(r + 1),
                    (String) tabMode.getValueAt(r, 0),
                    (String) tabMode.getValueAt(r, 1),
                    (String) tabMode.getValueAt(r, 2),
                    (String) tabMode.getValueAt(r, 3),
                    (String) tabMode.getValueAt(r, 4),
                    Valid.SetAngka((Double) tabMode.getValueAt(r, 5)),
                    (String) tabMode.getValueAt(r, 6),
                    (String) tabMode.getValueAt(r, 7),
                    (String) tabMode.getValueAt(r, 8)
                );
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
            Valid.reportTempSmc("rptCariBPJSReferensiDPHOApotek.jasper", "report", "::[ Pencarian Referensi DPHO Apotek BPJS ]::", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            runBackground(() ->tampil());
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            runBackground(() ->tampil());
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            BtnCariActionPerformed(null);
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(koneksiDB.CARICEPAT().equals("aktif")){
            Poli.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(Poli.getText().length()>2){
                        runBackground(() ->tampil(Poli.getText()));
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(Poli.getText().length()>2){
                        runBackground(() ->tampil(Poli.getText()));
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(Poli.getText().length()>2){
                        runBackground(() ->tampil(Poli.getText()));
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.panelisi panelGlass6;
    private widget.Table tbKamar;
    // End of variables declaration//GEN-END:variables

    public void emptTeks() {
        TCari.requestFocus();
    }

    private void tampil() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String utc = api.getUTCDateTime();
            headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            HttpEntity entity = new HttpEntity(headers);
            String url = koneksiDB.URLAPIAPOTEKBPJS() + "/referensi/dpho";
            System.out.println(url);
            JsonNode root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            JsonNode metadata = root.path("metaData");
            if (metadata.path("code").asText().equals("200")) {
                Valid.tabelKosong(tabMode);
                JsonNode response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                if (response.path("list").isArray()) {
                    if (TCari.getText().isBlank()) {
                        response.path("list").forEach((JsonNode item) -> {
                            tabMode.addRow(new Object[] {
                                item.path("kodeobat").asText(), item.path("namaobat").asText(),
                                item.path("prb").asBoolean(), item.path("kronis").asBoolean(),
                                item.path("kemo").asBoolean(), item.path("harga").asDouble(),
                                item.path("restriksi").asText(), item.path("generik").asText(),
                                item.path("aktif").isNull() ? "" : item.path("aktif").asText(),
                                item.path("sedia").asText(), item.path("stok").asText()
                            });
                        });
                    } else {
                        response.path("list").forEach(item -> {
                            if (item.path("kodeobat").asText().toLowerCase().contains(TCari.getText().toLowerCase().trim()) ||
                                item.path("namaobat").asText().toLowerCase().contains(TCari.getText().toLowerCase().trim()) ||
                                item.path("restriksi").asText().toLowerCase().contains(TCari.getText().toLowerCase().trim()) ||
                                item.path("generik").asText().toLowerCase().contains(TCari.getText().toLowerCase().trim())
                            ) {
                                tabMode.addRow(new Object[] {
                                    item.path("kodeobat").asText(), item.path("namaobat").asText(),
                                    item.path("prb").asBoolean(), item.path("kronis").asBoolean(),
                                    item.path("kemo").asBoolean(), item.path("harga").asDouble(),
                                    item.path("restriksi").asText(), item.path("generik").asText(),
                                    item.path("aktif").isNull() ? "" : item.path("aktif").asText(),
                                    item.path("sedia").asText(), item.path("stok").asText()
                                });
                            }
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, metadata.path("message").asText());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Notif : " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public JTable getTable() {
        return tbKamar;
    }

    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }

    /*
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
    */
}
