package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLEditorKit;

public class RMFotoPasienSMC extends javax.swing.JDialog {
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();

    public RMFotoPasienSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        LoadHTML.setEditorKit(new HTMLEditorKit());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        FormInput = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        Scroll = new widget.ScrollPane();
        LoadHTML = new widget.editorpane();
        panelGlass8 = new widget.panelisi();
        BtnAmbil = new widget.Button();
        BtnRefresh = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 50, 50)), "::[ Foto Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(530, 43));
        FormInput.setLayout(null);

        jLabel3.setText("No.Rawat :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 10, 65, 23);

        TNoRw.setEditable(false);
        TNoRw.setName("TNoRw"); // NOI18N
        FormInput.add(TNoRw);
        TNoRw.setBounds(68, 10, 125, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(196, 10, 80, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(279, 10, 245, 23);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        LoadHTML.setEditable(false);
        LoadHTML.setName("LoadHTML"); // NOI18N
        Scroll.setViewportView(LoadHTML);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(100, 56));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnAmbil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnAmbil.setMnemonic('A');
        BtnAmbil.setText("Ambil");
        BtnAmbil.setToolTipText("Alt+A");
        BtnAmbil.setName("BtnAmbil"); // NOI18N
        BtnAmbil.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAmbil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAmbilActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnAmbil);

        BtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnRefresh.setMnemonic('R');
        BtnRefresh.setText("Refresh");
        BtnRefresh.setToolTipText("Alt+R");
        BtnRefresh.setName("BtnRefresh"); // NOI18N
        BtnRefresh.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRefreshActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnRefresh);

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

    private void BtnAmbilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAmbilActionPerformed
        if (TNoRM.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, data pasien tidak ditemukan...!!!!");
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (!Sequel.cariExistsSmc("select 1 from personal_pasien where personal_pasien.no_rkm_medis = ?", TNoRM.getText())) {
            Sequel.menyimpantfSmc("personal_pasien", "", TNoRM.getText(), "", "");
        }
        Valid.panggilUrl("photopasien/login.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&norm=" + TNoRM.getText());
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnAmbilActionPerformed

    private void BtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefreshActionPerformed
        tampilFoto();
    }//GEN-LAST:event_BtnRefreshActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (KeyEvent.VK_SPACE == evt.getKeyCode()) {
            dispose();
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        tampilFoto();
    }//GEN-LAST:event_formWindowActivated

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMFotoPasienSMC dialog = new RMFotoPasienSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnAmbil;
    private widget.Button BtnKeluar;
    private widget.Button BtnRefresh;
    private widget.PanelBiasa FormInput;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel3;
    private widget.panelisi panelGlass8;
    // End of variables declaration//GEN-END:variables

    public void setNoRawat(String norawat) {
        TNoRw.setText(norawat);
        TNoRM.setText(Sequel.cariIsiSmc("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat = ?", norawat));
        TPasien.setText(Sequel.cariIsiSmc("select pasien.nm_pasien from pasien where pasien.no_rkm_medis = ?", TNoRM.getText()));
    }

    private void tampilFoto() {
        String gambar = Sequel.cariIsiSmc("select personal_pasien.gambar from personal_pasien where personal_pasien.no_rkm_medis = ?", TNoRM.getText());
        if (gambar.isBlank() || "-".equals(gambar)) {
            LoadHTML.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
        } else {
            LoadHTML.setText("<html><body><center><img src='http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/photopasien/" + gambar + "?v=" + System.currentTimeMillis() + "' alt='photo' width='490' height='390'/></center></body></html>");
        }
    }
}
