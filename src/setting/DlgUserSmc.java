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
package setting;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public class DlgUserSmc extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final DlgUpdateUser personal = new DlgUpdateUser(null, false);
    private final DlgCariDokter dlgdokter = new DlgCariDokter(null, false);
    private final DlgCariPetugas dlgpetugas = new DlgCariPetugas(null, false);
    private String copyhakakses = "", userdicopy = "";

    /**
     * Creates new form DlgUser
     *
     * @param parent
     * @param modal
     */
    public DlgUserSmc(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row = {"User ID", "Nama User", "Jabatan", "Password", "pw"};

        tabMode = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return java.lang.String.class;
            }
        };

        tbUser.setModel(tabMode);
        tbUser.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbUser.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbUser.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbUser.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbUser.getColumnModel().getColumn(2).setPreferredWidth(120);
        tbUser.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbUser.getColumnModel().getColumn(4).setMinWidth(0);
        tbUser.getColumnModel().getColumn(4).setMaxWidth(0);
        tbUser.setDefaultRenderer(Object.class, new WarnaTable());

        TKd.setDocument(new batasInput((byte) 30).getKata(TKd));
        TPass.setDocument(new batasInput((byte) 50).getKata(TPass));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        dlgdokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dlgdokter.getTable().getSelectedRow() != -1) {
                    TKd.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(), 0).toString());
                    TNmUser.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(), 1).toString());
                    TPass.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(), 0).toString());
                    Jabatan.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(), 10).toString());
                }
                TKd.requestFocus();
            }
        });

        dlgpetugas.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dlgpetugas.getTable().getSelectedRow() != -1) {
                    TKd.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(), 0).toString());
                    TNmUser.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(), 1).toString());
                    TPass.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(), 0).toString());
                    Jabatan.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(), 9).toString());
                }
                TKd.requestFocus();
            }
        });

        personal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                tampil();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do falseT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCopyHakAkses = new javax.swing.JMenuItem();
        MnSetUser = new javax.swing.JMenuItem();
        Jabatan = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbUser = new widget.Table();
        panelGlass5 = new widget.panelisi();
        jLabel3 = new widget.Label();
        jLabel4 = new widget.Label();
        TKd = new widget.TextBox();
        TPass = new widget.TextBox();
        BtnSeek = new widget.Button();
        BtnSeek1 = new widget.Button();
        TNmUser = new widget.TextBox();
        jPanel1 = new javax.swing.JPanel();
        panelGlass7 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelGlass6 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCopyHakAkses.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCopyHakAkses.setForeground(new java.awt.Color(50, 50, 50));
        MnCopyHakAkses.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCopyHakAkses.setText("Copy Hak Akses");
        MnCopyHakAkses.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCopyHakAkses.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCopyHakAkses.setName("MnCopyHakAkses"); // NOI18N
        MnCopyHakAkses.setPreferredSize(new java.awt.Dimension(150, 26));
        MnCopyHakAkses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCopyHakAksesActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCopyHakAkses);

        MnSetUser.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSetUser.setForeground(new java.awt.Color(50, 50, 50));
        MnSetUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSetUser.setText("Set Personal");
        MnSetUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSetUser.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSetUser.setName("MnSetUser"); // NOI18N
        MnSetUser.setPreferredSize(new java.awt.Dimension(150, 26));
        MnSetUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSetUserActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSetUser);

        Jabatan.setEditable(false);
        Jabatan.setName("Jabatan"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Setup User ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbUser.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbUser.setComponentPopupMenu(jPopupMenu1);
        tbUser.setName("tbUser"); // NOI18N
        tbUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbUserMouseClicked(evt);
            }
        });
        tbUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbUserKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbUser);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 47));
        panelGlass5.setLayout(null);

        jLabel3.setText("Dokter/Petugas :");
        jLabel3.setName("jLabel3"); // NOI18N
        panelGlass5.add(jLabel3);
        jLabel3.setBounds(5, 12, 94, 23);

        jLabel4.setText("Password :");
        jLabel4.setName("jLabel4"); // NOI18N
        panelGlass5.add(jLabel4);
        jLabel4.setBounds(449, 12, 60, 23);

        TKd.setEditable(false);
        TKd.setHighlighter(null);
        TKd.setName("TKd"); // NOI18N
        TKd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TKdKeyPressed(evt);
            }
        });
        panelGlass5.add(TKd);
        TKd.setBounds(102, 12, 112, 23);

        TPass.setName("TPass"); // NOI18N
        TPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPassKeyPressed(evt);
            }
        });
        panelGlass5.add(TPass);
        TPass.setBounds(512, 12, 180, 23);

        BtnSeek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek.setMnemonic('1');
        BtnSeek.setToolTipText("Alt+1");
        BtnSeek.setName("BtnSeek"); // NOI18N
        BtnSeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeekActionPerformed(evt);
            }
        });
        BtnSeek.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeekKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnSeek);
        BtnSeek.setBounds(216, 12, 28, 23);

        BtnSeek1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek1.setMnemonic('2');
        BtnSeek1.setToolTipText("Alt+2");
        BtnSeek1.setName("BtnSeek1"); // NOI18N
        BtnSeek1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek1ActionPerformed(evt);
            }
        });
        BtnSeek1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek1KeyPressed(evt);
            }
        });
        panelGlass5.add(BtnSeek1);
        BtnSeek1.setBounds(245, 12, 28, 23);

        TNmUser.setEditable(false);
        TNmUser.setName("TNmUser"); // NOI18N
        panelGlass5.add(TNmUser);
        TNmUser.setBounds(275, 12, 160, 23);

        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_START);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        panelGlass7.setName("panelGlass7"); // NOI18N
        panelGlass7.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(65, 23));
        jLabel6.setRequestFocusEnabled(false);
        panelGlass7.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(355, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass7.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('1');
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
        panelGlass7.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('2');
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
        panelGlass7.add(BtnAll);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass7.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass7.add(LCount);

        jPanel1.add(panelGlass7, java.awt.BorderLayout.PAGE_START);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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
        panelGlass6.add(BtnSimpan);

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
        panelGlass6.add(BtnBatal);

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
        panelGlass6.add(BtnEdit);

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

        jPanel1.add(panelGlass6, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TKd.getText().trim().isBlank() || TNmUser.getText().trim().isBlank()) {
            Valid.textKosong(TKd, "User");
        } else if (TPass.getText().trim().isBlank()) {
            Valid.textKosong(TPass, "Password");
        } else {
            if (Sequel.executeRawSmc("insert into user (id_user, password) values (aes_encrypt(?, 'nur'), aes_encrypt(?, 'windi'))", TKd.getText(), TPass.getText())) {
                tabMode.addRow(new Object[] {
                    TKd.getText(), TNmUser.getText(), Jabatan.getText(), "**********", TPass.getText()
                });
                emptTeks();
                LCount.setText(String.valueOf(tabMode.getRowCount()));
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, TPass, BtnHapus);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TKd.requestFocus();
        } else if (TPass.getText().trim().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        } else {
            if (tbUser.getSelectedRow() != -1) {
                if (Sequel.executeRawSmc("delete from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi')", TKd.getText(), TPass.getText())) {
                    tabMode.removeRow(tbUser.getSelectedRow());
                    emptTeks();
                    LCount.setText(String.valueOf(tabMode.getRowCount()));
                }
            }
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnSimpan, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TKd.getText().trim().isBlank() || TNmUser.getText().trim().isBlank()) {
            Valid.textKosong(TKd, "User");
        } else if (TPass.getText().trim().isBlank()) {
            Valid.textKosong(TPass, "Password");
        } else {
            if (tbUser.getSelectedRow() != -1) {
                if (Sequel.executeRawSmc("update user set id_user = aes_encrypt(?, 'nur'), password = aes_encrypt(?, 'windi') where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi')",
                    TKd.getText(), TPass.getText(), tbUser.getValueAt(tbUser.getSelectedRow(), 0).toString(), tbUser.getValueAt(tbUser.getSelectedRow(), 4).toString()
                )) {
                    tabMode.setValueAt(TKd.getText(), tbUser.getSelectedRow(), 0);
                    tabMode.setValueAt(TPass.getText(), tbUser.getSelectedRow(), 4);
                    emptTeks();
                }
            }
        }

}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            dispose();
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void TKdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
        Valid.pindah(evt, BtnSimpan, TPass);
}//GEN-LAST:event_TKdKeyPressed

    private void TPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPassKeyPressed
        Valid.pindah(evt, TKd, BtnSimpan);
}//GEN-LAST:event_TPassKeyPressed

    private void BtnSeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekActionPerformed
        dlgdokter.emptTeks();
        dlgdokter.isCek();
        dlgdokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dlgdokter.setLocationRelativeTo(internalFrame1);
        dlgdokter.setVisible(true);
}//GEN-LAST:event_BtnSeekActionPerformed

    private void BtnSeekKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeekKeyPressed
        Valid.pindah(evt, TKd, TPass);
}//GEN-LAST:event_BtnSeekKeyPressed

    private void BtnSeek1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek1ActionPerformed
        dlgpetugas.emptTeks();
        dlgpetugas.isCek();
        dlgpetugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dlgpetugas.setLocationRelativeTo(internalFrame1);
        dlgpetugas.setVisible(true);
}//GEN-LAST:event_BtnSeek1ActionPerformed

    private void BtnSeek1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek1KeyPressed
        Valid.pindah(evt, TKd, TPass);
}//GEN-LAST:event_BtnSeek1KeyPressed

private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    if (tabMode.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
        TKd.requestFocus();
    } else if (tabMode.getRowCount() != 0) {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        Sequel.deleteTemporary();
        for (int i = 0; i < tabMode.getRowCount(); i++) {
            Sequel.temporary(String.valueOf(i),
                tabMode.getValueAt(i, 0).toString(),
                tabMode.getValueAt(i, 1).toString(),
                tabMode.getValueAt(i, 3).toString()
            );
        }
        Valid.MyReportqry("rptUser.jasper", "report", "::[ Data User ]::", "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
    }
    this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
        BtnPrintActionPerformed(null);
    } else {
        Valid.pindah(evt, BtnHapus, BtnKeluar);
    }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbUser.requestFocus();
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
            Valid.pindah(evt, BtnCari, TKd);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

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

    private void MnCopyHakAksesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCopyHakAksesActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data user...!!!!");
            TCari.requestFocus();
        } else if (TKd.getText().trim().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu data user yang mau dicopy hak aksesnya...!!!");
            tbUser.requestFocus();
        } else {
            copyhakakses = "copy";
            userdicopy = TKd.getText();
            JOptionPane.showMessageDialog(null, "Silahkan pilih user tujuan..!!");
        }
    }//GEN-LAST:event_MnCopyHakAksesActionPerformed

    private void MnSetUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSetUserActionPerformed
        if (tbUser.getSelectedRow() != -1) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            personal.isUser(TKd.getText(), TNmUser.getText(), TPass.getText());
            personal.setSize(460, this.getHeight() - 50);
            personal.setLocationRelativeTo(internalFrame1);
            personal.setAlwaysOnTop(false);
            personal.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih dulu nama user..!!");
        }
    }//GEN-LAST:event_MnSetUserActionPerformed

    private void tbUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbUserMouseClicked
        if (tabMode.getRowCount() != 0) {
            if (copyhakakses.equals("copy")) {
                if (userdicopy.equals(TKd.getText())) {
                    JOptionPane.showMessageDialog(null, "Copy hak akses gagal karena user dicopy dan user tujuan yang dipilih sama..!!");
                    userdicopy = "";
                    copyhakakses = "";
                } else {
                    int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data copy hak aksesnya..??", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        String sqlupdate = "";
                        try (PreparedStatement ps = koneksi.prepareStatement("select * from user where aes_decrypt(id_user, 'nur') = ?")) {
                            ps.setString(1, userdicopy);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    try (PreparedStatement ps2 = koneksi.prepareStatement("select column_name as `column_name` from information_schema.columns where table_schema = ? and table_name = 'user' and column_name not in ('id_user', 'password')")) {
                                        ps2.setString(1, koneksiDB.DATABASE());
                                        try (ResultSet rs2 = ps2.executeQuery()) {
                                            while (rs2.next()) {
                                                rs.getString(rs2.getString("column_name"));
                                                if (rs.wasNull()) {
                                                    sqlupdate = sqlupdate + rs2.getString("column_name") + " = null, ";
                                                } else {
                                                    sqlupdate = sqlupdate + rs2.getString("column_name") + " = '" + rs.getString(rs2.getString("column_name")) + "', ";
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Notif : " + e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                        }
                        sqlupdate = sqlupdate.substring(0, sqlupdate.length() - 2);
                        if (Sequel.mengupdatetfSmc("user", sqlupdate, "aes_decrypt(id_user, 'nur') = ?", tbUser.getValueAt(tbUser.getSelectedRow(), 0).toString())) {
                            JOptionPane.showMessageDialog(null, "Copy hak akses user berhasil!");
                            userdicopy = "";
                            copyhakakses = "";
                        }
                    } else {
                        userdicopy = "";
                        copyhakakses = "";
                    }
                }
            } else {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbUserMouseClicked

    private void tbUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbUserKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_V) {
                if (tbUser.getSelectedColumn() > 4) {
                    if (tbUser.getSelectedRow() != -1) {
                        if (tbUser.getValueAt(tbUser.getSelectedRow(), tbUser.getSelectedColumn()).toString().equals("false")) {
                            tbUser.setValueAt(true, tbUser.getSelectedRow(), tbUser.getSelectedColumn());
                        } else {
                            tbUser.setValueAt(false, tbUser.getSelectedRow(), tbUser.getSelectedColumn());
                        }

                    }
                }
            }
        }
    }//GEN-LAST:event_tbUserKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgUserSmc dialog = new DlgUserSmc(new javax.swing.JFrame(), true);
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
    private widget.Button BtnSeek;
    private widget.Button BtnSeek1;
    private widget.Button BtnSimpan;
    private widget.TextBox Jabatan;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnCopyHakAkses;
    private javax.swing.JMenuItem MnSetUser;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TKd;
    private widget.TextBox TNmUser;
    private widget.TextBox TPass;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass5;
    private widget.panelisi panelGlass6;
    private widget.panelisi panelGlass7;
    private widget.Table tbUser;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        try (PreparedStatement ps = koneksi.prepareStatement("select aes_decrypt(user.id_user, 'nur'), pegawai.nama, coalesce(spesialis.nm_sps, jabatan.nm_jbtn, '-') as jabatan, " +
            "aes_decrypt(user.password, 'windi') from user left join pegawai on aes_decrypt(user.id_user, 'nur') = pegawai.nik left join dokter on pegawai.nik = dokter.kd_dokter " +
            "left join petugas on pegawai.nik = petugas.nip left join spesialis on dokter.kd_sps = spesialis.kd_sps left join jabatan on petugas.kd_jbtn = jabatan.kd_jbtn " +
            "where pegawai.nik like ? or pegawai.nama like ? or coalesce(spesialis.nm_sps, jabatan.nm_jbtn, '-') like ? order by aes_decrypt(user.id_user, 'nur')"
        )) {
            Valid.tabelKosong(tabMode);
            if (TCari.getText().isBlank()) {
                ps.setString(1, "%%");
                ps.setString(2, "%%");
                ps.setString(3, "%%");
            } else {
                ps.setString(1, "%" + TCari.getText() + "%");
                ps.setString(2, "%" + TCari.getText() + "%");
                ps.setString(3, "%" + TCari.getText() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[] {rs.getString(1), rs.getString(2), rs.getString(3), "**********", rs.getString(4)});
                }
            }
            LCount.setText(String.valueOf(tabMode.getRowCount()));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void emptTeks() {
        TKd.setText("");
        TNmUser.setText("");
        TPass.setText("");
        tbUser.clearSelection();
        TKd.requestFocus();
    }

    private void getData() {
        if (tbUser.getSelectedRow() != -1) {
            TKd.setText(tbUser.getValueAt(tbUser.getSelectedRow(), 0).toString());
            TNmUser.setText(tbUser.getValueAt(tbUser.getSelectedRow(), 1).toString());
            TPass.setText(tbUser.getValueAt(tbUser.getSelectedRow(), 4).toString());
        }
    }
}
