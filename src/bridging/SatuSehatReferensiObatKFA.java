/*
  Kontribusi dari Mas Abdul Wahid RSUD Cipayung & Mas Fanji dari RSUD Kramatjati 
  
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoundedRangeModel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author dosen
 */
public final class SatuSehatReferensiObatKFA extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final validasi Valid = new validasi();
    private int i = 0, page = 1;
    private String link = "", json = "";
    private ApiSatuSehat api = new ApiSatuSehat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    
    private String lastKeyword = "", lastProductType = "farmasi";
    private List<String> codeKFALists = new ArrayList<String>();

    /**
     * Creates new form DlgKamar
     *
     * @param parent
     * @param modal
     */
    public SatuSehatReferensiObatKFA(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 2);
        setSize(628, 674);
        
        tabMode = new DefaultTableModel(null, new String[] {
            "KFA Code", "KFA System", "KFA Display", "Form Code", "Form System",
            "Form Display", "Num. Code", "Num. System", "Denom. Code",
            "Denom. System", "Route Code", "Route System", "Route Display"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        
        tbKamar.setModel(tabMode);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn column;
        for (i = 0; i < 13; i++) {
            column = tbKamar.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(80);
            } else if (i == 1) {
                column.setPreferredWidth(160);
            } else if (i == 2) {
                column.setPreferredWidth(380);
            } else if (i == 3) {
                column.setPreferredWidth(65);
            } else if (i == 4) {
                column.setPreferredWidth(210);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(70);
            } else if (i == 7) {
                column.setPreferredWidth(130);
            } else if (i == 8) {
                column.setPreferredWidth(70);
            } else if (i == 9) {
                column.setPreferredWidth(100);
            } else if (i == 10) {
                column.setPreferredWidth(70);
            } else if (i == 11) {
                column.setPreferredWidth(110);
            } else if (i == 12) {
                column.setPreferredWidth(100);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil(TCari.getText());
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil(TCari.getText());
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil(TCari.getText());
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
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelGlass6 = new widget.panelisi();
        jLabel7 = new widget.Label();
        ProductType = new widget.ComboBox();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel6 = new widget.Label();
        LimitData = new widget.ComboBox();
        jLabel8 = new widget.Label();
        LCount = new widget.Label();
        jLabel17 = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pencarian Data Referensi Praktisi Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setName("tbKamar"); // NOI18N
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel7.setText("Product Type :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(84, 23));
        panelGlass6.add(jLabel7);

        ProductType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "farmasi", "alkes" }));
        ProductType.setName("ProductType"); // NOI18N
        ProductType.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass6.add(ProductType);

        jLabel16.setText("Keyword :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass6.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(210, 23));
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

        jLabel6.setText("Limit Data :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass6.add(jLabel6);

        LimitData.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100", "200", "300", "500", "1000" }));
        LimitData.setName("LimitData"); // NOI18N
        LimitData.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass6.add(LimitData);

        jLabel8.setText("Record :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass6.add(jLabel8);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass6.add(LCount);

        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass6.add(jLabel17);

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
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

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
        if (TCari.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan masukkan NIK/ID parktisi");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tampil(TCari.getText());
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatReferensiObatKFA dialog = new SatuSehatReferensiObatKFA(new javax.swing.JFrame(), true);
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
    private widget.Button BtnKeluar;
    private widget.Label LCount;
    private widget.ComboBox LimitData;
    private widget.ComboBox ProductType;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.panelisi panelGlass6;
    private widget.Table tbKamar;
    // End of variables declaration//GEN-END:variables
    public void tampil(String search) {
        TCari.setText(search);
        if (page == 1) {
            Valid.tabelKosong(tabMode);
        }
        
        getAllProducts();
        
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }
    
    public void tampil() {
        if (page == 1) {
            Valid.tabelKosong(tabMode);
        }
        
        getAllProducts();
        
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }

    public JTable getTable() {
        return tbKamar;
    }
    
    private void getAllProducts() {
        if (!TCari.getText().trim().equals(lastKeyword)) {
            lastKeyword = TCari.getText().trim();
            codeKFALists.clear();
            page = 1;
        }
        
        if (!ProductType.getSelectedItem().toString().equals(lastProductType)) {
            lastProductType = ProductType.getSelectedItem().toString();
            codeKFALists.clear();
            page = 1;
        }
        
        link = koneksiDB.URLKFAV2SATUSEHAT() + "/products/all?page=" + page + "&size=" + LimitData.getSelectedItem().toString() + "&product_type=" + ProductType.getSelectedItem().toString();
        if (!TCari.getText().isBlank()) {
            link = link + "&keyword=" + TCari.getText().trim();
        }
        
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            json = api.getRest().exchange(link, HttpMethod.GET, requestEntity, String.class).getBody();
            
            root = mapper.readTree(json);
            System.out.println("Total : " + root.path("total").asText());
            System.out.println("Page : " + root.path("page").asText());
            System.out.println("Size : " + root.path("page").asText());
            response = root.path("items").path("data");
            if (response.isArray()) {
                for (JsonNode obj : response) {
                    if (obj.path("active").asBoolean() || obj.path("state").asText().equals("valid")) {
                        codeKFALists.add(obj.path("kfa_code").asText());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        
        getEachProductsDetail();
    }
    
    private void getEachProductsDetail() {
        if (codeKFALists.isEmpty()) return;
        try {
            for (String kfaCode : codeKFALists) {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                requestEntity = new HttpEntity(headers);
                link = koneksiDB.URLKFAV2SATUSEHAT() + "/products?identifier=kfa&code=" + kfaCode;
                json = api.getRest().exchange(link, HttpMethod.GET, requestEntity, String.class).getBody();
                root = mapper.readTree(json);
                response = root.path("result");
                tabMode.addRow(new String[] {
                    kfaCode, "http://sys-ids.kemkes.go.id/kfa", response.path("name").asText(),
                    response.path("dosage_form").path("code").asText(), "http://terminology.kemkes.go.id/CodeSystem/medication-form",
                    response.path("dosage_form").path("name").asText(), response.path("ucum").path("cs_code").asText(),
                    "http://unitsofmeasure.org", "-", "-", response.path("rute_pemberian").path("code").asText(),
                    "http://www.whocc.no/atc", response.path("rute_pemberian").path("name").asText()
                });
            }
        } catch (Exception e) {
            System.out.println("Notif detail : " + e);
        }
    }
    
    private void emptTeks() {
        
    }
}
