package inventaris;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

public class InventarisSensusBarang extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private String selectedIdRuang = "";
    private boolean sensusStarted = false;

    public InventarisSensusBarang(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(990, 680);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Inventaris","Kode Barang","Nama Barang","Ruang Asal","Status Sensus","Ruang Aktual (id_ruang)","Keterangan"
        }) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return sensusStarted && (c == 4 || c == 5 || c == 6);
            }
        };
        tbSensus.setModel(tabMode);
        tbSensus.setPreferredScrollableViewportSize(new Dimension(920, 330));
        tbSensus.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbSensus.setDefaultRenderer(Object.class, new WarnaTable());
        int[] widths = {120,100,200,120,120,130,200};
        for (int i = 0; i < widths.length; i++) {
            tbSensus.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Sesuai","Tidak Ditemukan","Pindah","Rusak"});
        TableColumn statusCol = tbSensus.getColumnModel().getColumn(4);
        statusCol.setCellEditor(new DefaultCellEditor(statusCombo));

        NoSensus.setDocument(new batasInput((byte)20).getKata(NoSensus));
        nip_pj.setDocument(new batasInput((byte)20).getKata(nip_pj));
        keterangan_header.setDocument(new batasInput((byte)200).getKata(keterangan_header));
        periode.setDocument(new batasInput((byte)7).getKata(periode));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        NoSensus.setEditable(false);
        nm_pj.setEditable(false);
        nm_ruangcari.setEditable(false);

        nip_pj.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) { isCekPetugas(); }
            public void removeUpdate(DocumentEvent e) { isCekPetugas(); }
            public void changedUpdate(DocumentEvent e) { isCekPetugas(); }
        });

        emptTeks();
        runBackground(() -> tampil());
    }

    private void isCekPetugas() {
        if (nip_pj.getText().length() >= 3) {
            nm_pj.setText(Sequel.CariPetugas(nip_pj.getText()));
        }
    }

    private void autoNomor() {
        try {
            Valid.autoNomer3(
                "SELECT IFNULL(MAX(CONVERT(RIGHT(no_sensus,3),signed)),0) FROM sensus_inventaris WHERE no_sensus LIKE 'SNS" +
                TglSensus.getSelectedItem().toString().substring(6, 10) +
                TglSensus.getSelectedItem().toString().substring(3, 5) + "%'",
                "SNS" + TglSensus.getSelectedItem().toString().substring(6, 10) +
                TglSensus.getSelectedItem().toString().substring(3, 5),
                3, NoSensus
            );
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void emptTeks() {
        Valid.tabelKosong(tabMode);
        NoSensus.setEditable(false);
        TglSensus.setEnabled(true);
        periode.setEditable(true);
        nip_pj.setEditable(true);
        keterangan_header.setEditable(true);
        nm_ruangcari.setText("");
        keterangan_header.setText("");
        periode.setText("");
        nip_pj.setText("");
        nm_pj.setText("");
        selectedIdRuang = "";
        TglSensus.setDate(new Date());
        sensusStarted = false;
        BtnSelesai.setEnabled(false);
        BtnMulaiFill.setEnabled(true);
        LStatus.setText("Status: Belum Dimulai");
        autoNomor();
        NoSensus.requestFocus();
    }

    private void tampil() {
        int jml = 0;
        try {
            String cari = "%" + TCari.getText().trim() + "%";
            ps = koneksi.prepareStatement(
                "SELECT si.no_sensus, si.tanggal, si.periode, si.nip_pj, si.keterangan, si.status " +
                "FROM sensus_inventaris si " +
                "WHERE si.no_sensus LIKE ? OR si.periode LIKE ? OR si.nip_pj LIKE ? " +
                "ORDER BY si.no_sensus DESC LIMIT 200"
            );
            ps.setString(1, cari); ps.setString(2, cari); ps.setString(3, cari);
            rs = ps.executeQuery();
            Valid.tabelKosong(tabMode);
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), "", rs.getString(6)
                });
                jml++;
            }
            rs.close(); ps.close();
            final int f = jml;
            SwingUtilities.invokeLater(() -> LCount.setText("Jml Data : " + f));
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
        }
    }

    private void tampilInventaris() {
        int jml = 0;
        try {
            Valid.tabelKosong(tabMode);
            String sql = "SELECT i.no_inventaris, i.kode_barang, ib.nama_barang, ir.nama_ruang " +
                "FROM inventaris i " +
                "INNER JOIN inventaris_barang ib ON ib.kode_barang = i.kode_barang " +
                "INNER JOIN inventaris_ruang ir ON ir.id_ruang = i.id_ruang " +
                "WHERE i.status_barang != 'Dihapus'" +
                (selectedIdRuang.isEmpty() ? "" : " AND i.id_ruang = ?") +
                " ORDER BY i.id_ruang, i.no_inventaris";
            ps = koneksi.prepareStatement(sql);
            if (!selectedIdRuang.isEmpty()) ps.setString(1, selectedIdRuang);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), "Sesuai", "", ""
                });
                jml++;
            }
            rs.close(); ps.close();
            final int f = jml;
            SwingUtilities.invokeLater(() -> LCount.setText("Unit Terdaftar : " + f));
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
        }
    }

    private void BtnMulaiFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnMulaiFillActionPerformed
        if (NoSensus.getText().trim().isEmpty()) {
            Valid.textKosong(NoSensus, "No.Sensus");
            return;
        }
        if (periode.getText().trim().isEmpty() || !periode.getText().matches("\\d{4}-\\d{2}")) {
            Valid.textKosong(periode, "Periode (format YYYY-MM)");
            return;
        }
        if (nip_pj.getText().trim().isEmpty()) {
            Valid.textKosong(nip_pj, "NIP Penanggung Jawab");
            return;
        }
        String statusExisting = Sequel.cariIsiSmc("SELECT status FROM sensus_inventaris WHERE no_sensus = ?", NoSensus.getText());
        if ("Selesai".equals(statusExisting)) {
            JOptionPane.showMessageDialog(rootPane, "Sensus " + NoSensus.getText() + " sudah selesai dan tidak dapat diubah.");
            return;
        }
        if (statusExisting.isEmpty()) {
            if (!Sequel.menyimpantf2("sensus_inventaris", "?,?,?,?,?,?", "No.Sensus", 6, new String[]{
                NoSensus.getText(),
                Valid.SetTgl(TglSensus.getSelectedItem() + ""),
                periode.getText(),
                nip_pj.getText(),
                keterangan_header.getText().trim(),
                "Draft"
            })) {
                JOptionPane.showMessageDialog(rootPane, "Gagal membuat header sensus.");
                return;
            }
        }
        NoSensus.setEditable(false);
        TglSensus.setEnabled(false);
        periode.setEditable(false);
        nip_pj.setEditable(false);
        keterangan_header.setEditable(false);
        sensusStarted = true;
        BtnSelesai.setEnabled(true);
        BtnMulaiFill.setEnabled(false);
        LStatus.setText("Status: Draft — " + tabMode.getRowCount() + " unit dimuat");
        runBackground(this::tampilInventaris);
    }//GEN-LAST:event_BtnMulaiFillActionPerformed

    private void BtnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSelesaiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Tidak ada data unit untuk diselesaikan.");
            return;
        }
        if (tbSensus.isEditing()) tbSensus.getCellEditor().stopCellEditing();

        int reply = JOptionPane.showConfirmDialog(rootPane,
            "Selesaikan sensus " + NoSensus.getText() + "?\nStatus inventaris akan diperbarui sesuai hasil sensus.",
            "Konfirmasi Selesai", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;

        boolean sukses = true;
        Sequel.AutoComitFalse();
        int rowCount = tabMode.getRowCount();
        for (int i = 0; i < rowCount && sukses; i++) {
            String noInv = tabMode.getValueAt(i, 0).toString();
            String statusSensus = tabMode.getValueAt(i, 4) == null ? "Sesuai" : tabMode.getValueAt(i, 4).toString();
            String idRuangAktual = tabMode.getValueAt(i, 5) == null ? "" : tabMode.getValueAt(i, 5).toString().trim();
            String ketRow = tabMode.getValueAt(i, 6) == null ? "" : tabMode.getValueAt(i, 6).toString().trim();

            sukses = Sequel.menyimpantf2("detail_sensus_inventaris", "?,?,?,?,?", "No.Sensus+No.Inventaris", 5, new String[]{
                NoSensus.getText(), noInv, statusSensus,
                idRuangAktual.isEmpty() ? null : idRuangAktual,
                ketRow.isEmpty() ? null : ketRow
            });

            if (sukses) {
                switch (statusSensus) {
                    case "Pindah":
                        if (!idRuangAktual.isEmpty()) {
                            sukses = Sequel.executeRawSmc("UPDATE inventaris SET id_ruang = ? WHERE no_inventaris = ?", idRuangAktual, noInv);
                        }
                        break;
                    case "Rusak":
                        sukses = Sequel.executeRawSmc("UPDATE inventaris SET status_barang = 'Rusak' WHERE no_inventaris = ?", noInv);
                        break;
                    case "Tidak Ditemukan":
                        sukses = Sequel.executeRawSmc("UPDATE inventaris SET status_barang = 'Hilang' WHERE no_inventaris = ?", noInv);
                        break;
                    default:
                        break;
                }
            }
        }

        if (sukses) {
            sukses = Sequel.executeRawSmc("UPDATE sensus_inventaris SET status = 'Selesai' WHERE no_sensus = ?", NoSensus.getText());
        }

        if (sukses) {
            Sequel.Commit();
            JOptionPane.showMessageDialog(rootPane, "Sensus " + NoSensus.getText() + " berhasil diselesaikan.");
            runBackground(() -> tampil());
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan saat menyimpan. Sensus dibatalkan.");
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
    }//GEN-LAST:event_BtnSelesaiActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) runBackground(() -> tampil());
    }//GEN-LAST:event_TCariKeyPressed

    private void btnPtgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPtgActionPerformed
        DlgCariPetugas petugas = new DlgCariPetugas(null, false);
        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    nip_pj.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    nm_pj.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
                nip_pj.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPtgActionPerformed

    private void btnRuangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRuangActionPerformed
        InventarisRuang ruang = new InventarisRuang(null, false);
        ruang.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (ruang.getTable().getSelectedRow() != -1) {
                    selectedIdRuang = ruang.getTable().getValueAt(ruang.getTable().getSelectedRow(), 0).toString();
                    nm_ruangcari.setText(ruang.getTable().getValueAt(ruang.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        ruang.isCek();
        ruang.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ruang.setLocationRelativeTo(internalFrame1);
        ruang.setAlwaysOnTop(false);
        ruang.setVisible(true);
    }//GEN-LAST:event_btnRuangActionPerformed

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
                        if (isDisplayable()) setCursor(Cursor.getDefaultCursor());
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }

    @Override
    public void dispose() {
        koneksi = null;
        executor.shutdownNow();
        super.dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        label1 = new widget.Label();
        NoSensus = new widget.TextBox();
        label2 = new widget.Label();
        TglSensus = new widget.Tanggal();
        label3 = new widget.Label();
        periode = new widget.TextBox();
        label4 = new widget.Label();
        nip_pj = new widget.TextBox();
        btnPtg = new widget.Button();
        nm_pj = new widget.TextBox();
        label5 = new widget.Label();
        nm_ruangcari = new widget.TextBox();
        btnRuang = new widget.Button();
        label6 = new widget.Label();
        keterangan_header = new widget.TextBox();
        LStatus = new widget.Label();
        ChkInput = new widget.CekBox();
        Scroll = new widget.ScrollPane();
        tbSensus = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        label7 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        LCount = new widget.Label();
        BtnMulaiFill = new widget.Button();
        BtnSelesai = new widget.Button();
        BtnBatal = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Sensus Barang Inventaris ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(960, 135));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(960, 115));
        FormInput.setLayout(null);

        label1.setText("No.Sensus :"); label1.setName("label1");
        FormInput.add(label1); label1.setBounds(0, 8, 80, 23);

        NoSensus.setName("NoSensus");
        FormInput.add(NoSensus); NoSensus.setBounds(83, 8, 130, 23);

        label2.setText("Tanggal :"); label2.setName("label2");
        FormInput.add(label2); label2.setBounds(220, 8, 65, 23);

        TglSensus.setDisplayFormat("dd-MM-yyyy"); TglSensus.setName("TglSensus");
        FormInput.add(TglSensus); TglSensus.setBounds(288, 8, 120, 23);

        label3.setText("Periode :"); label3.setName("label3");
        FormInput.add(label3); label3.setBounds(415, 8, 60, 23);

        periode.setName("periode");
        FormInput.add(periode); periode.setBounds(478, 8, 85, 23);

        label4.setText("NIP PJ :"); label4.setName("label4");
        FormInput.add(label4); label4.setBounds(0, 38, 65, 23);

        nip_pj.setName("nip_pj");
        FormInput.add(nip_pj); nip_pj.setBounds(68, 38, 100, 23);

        btnPtg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/find.png"))); // NOI18N
        btnPtg.setName("btnPtg"); btnPtg.setPreferredSize(new java.awt.Dimension(30, 23));
        btnPtg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnPtgActionPerformed(evt); }
        });
        FormInput.add(btnPtg); btnPtg.setBounds(171, 38, 30, 23);

        nm_pj.setName("nm_pj");
        FormInput.add(nm_pj); nm_pj.setBounds(204, 38, 220, 23);

        label5.setText("Filter Ruang :"); label5.setName("label5");
        FormInput.add(label5); label5.setBounds(0, 68, 90, 23);

        nm_ruangcari.setName("nm_ruangcari");
        FormInput.add(nm_ruangcari); nm_ruangcari.setBounds(93, 68, 180, 23);

        btnRuang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/find.png"))); // NOI18N
        btnRuang.setName("btnRuang"); btnRuang.setPreferredSize(new java.awt.Dimension(30, 23));
        btnRuang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnRuangActionPerformed(evt); }
        });
        FormInput.add(btnRuang); btnRuang.setBounds(276, 68, 30, 23);

        label6.setText("Keterangan :"); label6.setName("label6");
        FormInput.add(label6); label6.setBounds(315, 68, 80, 23);

        keterangan_header.setName("keterangan_header");
        FormInput.add(keterangan_header); keterangan_header.setBounds(398, 68, 400, 23);

        LStatus.setText("Status: Belum Dimulai"); LStatus.setName("LStatus");
        LStatus.setForeground(new java.awt.Color(0, 80, 160));
        FormInput.add(LStatus); LStatus.setBounds(0, 95, 700, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setSelected(true); ChkInput.setName("ChkInput");
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FormInput.setVisible(ChkInput.isSelected());
                PanelInput.setPreferredSize(new java.awt.Dimension(960, ChkInput.isSelected() ? 135 : 25));
                internalFrame1.revalidate();
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        Scroll.setName("Scroll");
        tbSensus.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{}));
        tbSensus.setName("tbSensus");
        Scroll.setViewportView(tbSensus);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(960, 50));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1");
        panelisi1.setPreferredSize(new java.awt.Dimension(960, 50));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label7.setText("Cari :"); label7.setName("label7");
        label7.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi1.add(label7);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TCariKeyPressed(evt); }
        });
        panelisi1.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setText("Cari"); BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(80, 30));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnCariActionPerformed(evt); }
        });
        panelisi1.add(BtnCari);

        LCount.setText("Jml Data : 0"); LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(200, 23));
        panelisi1.add(LCount);

        BtnMulaiFill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnMulaiFill.setText("Mulai Isi"); BtnMulaiFill.setName("BtnMulaiFill");
        BtnMulaiFill.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnMulaiFill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnMulaiFillActionPerformed(evt); }
        });
        panelisi1.add(BtnMulaiFill);

        BtnSelesai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnSelesai.setText("Selesaikan"); BtnSelesai.setName("BtnSelesai");
        BtnSelesai.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnSelesai.setEnabled(false);
        BtnSelesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnSelesaiActionPerformed(evt); }
        });
        panelisi1.add(BtnSelesai);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete.png"))); // NOI18N
        BtnBatal.setText("Batal/Reset"); BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnBatalActionPerformed(evt); }
        });
        panelisi1.add(BtnBatal);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cancel.png"))); // NOI18N
        BtnKeluar.setText("Keluar"); BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(95, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnKeluarActionPerformed(evt); }
        });
        panelisi1.add(BtnKeluar);

        jPanel3.add(panelisi1, java.awt.BorderLayout.CENTER);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnMulaiFill;
    private widget.Button BtnSelesai;
    private widget.CekBox ChkInput;
    private widget.Label LCount;
    private widget.Label LStatus;
    private widget.TextBox NoSensus;
    private widget.PanelBiasa FormInput;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.Tanggal TglSensus;
    private widget.TextBox TCari;
    private widget.Button btnPtg;
    private widget.Button btnRuang;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox keterangan_header;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label label4;
    private widget.Label label5;
    private widget.Label label6;
    private widget.Label label7;
    private widget.TextBox nip_pj;
    private widget.TextBox nm_pj;
    private widget.TextBox nm_ruangcari;
    private widget.panelisi panelisi1;
    private widget.TextBox periode;
    private widget.Table tbSensus;
    // End of variables declaration//GEN-END:variables
}
