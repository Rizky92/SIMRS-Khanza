package inventaris;

import fungsi.WarnaTable;
import fungsi.akses;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import keuangan.Jurnal;

public class InventarisPenghapusan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Jurnal jur = new Jurnal();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private double harga = 0, akm = 0, nilaiBuku = 0;

    public InventarisPenghapusan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(990, 680);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Penghapusan","No.Inventaris","Nama Barang","Tanggal","Alasan","Nilai Jual","Nilai Buku","Akm.Penyusutan","Untung/Rugi","Keterangan"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbHapus.setModel(tabMode);
        tbHapus.setPreferredScrollableViewportSize(new Dimension(900, 300));
        tbHapus.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbHapus.setDefaultRenderer(Object.class, new WarnaTable());
        int[] widths = {120,110,200,100,100,110,110,110,100,200};
        for (int i = 0; i < widths.length; i++) {
            tbHapus.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        NoPenghapusan.setDocument(new batasInput((byte)20).getKata(NoPenghapusan));
        NoInv.setDocument(new batasInput((byte)30).getKata(NoInv));
        nilai_jual.setDocument(new batasInput((byte)20).getAngka(nilai_jual));
        nip.setDocument(new batasInput((byte)20).getKata(nip));
        keterangan.setDocument(new batasInput((byte)200).getKata(keterangan));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        NoPenghapusan.setEditable(false);
        LNamaBarang.setText("-");
        LHarga.setText("0");
        LAkmPenyusutan.setText("0");
        LNilaiBuku.setText("0");
        LUntungRugi.setText("0");
        nm_petugas.setEditable(false);

        nip.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) { isCekPetugas(); }
            public void removeUpdate(DocumentEvent e) { isCekPetugas(); }
            public void changedUpdate(DocumentEvent e) { isCekPetugas(); }
        });

        NoInv.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) isiBarang();
            }
        });

        nilai_jual.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) { hitungUntungRugi(); }
            public void removeUpdate(DocumentEvent e) { hitungUntungRugi(); }
            public void changedUpdate(DocumentEvent e) { hitungUntungRugi(); }
        });

        TglHapus.addPropertyChangeListener("date", evt -> autoNomor());

        addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {
                runBackground(() -> {
                    tampilAkun();
                    SwingUtilities.invokeLater(() -> {
                        autoNomor();
                        tampil();
                    });
                });
            }
            public void windowClosing(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });
    }

    private void autoNomor() {
        Valid.autonomorSmc(NoPenghapusan, "PHI", "", "penghapusan_inventaris", "no_penghapusan", 3, "0", TglHapus);
    }

    private void isCekPetugas() {
        String nama = Sequel.CariPetugas(nip.getText().trim());
        nm_petugas.setText(nama);
    }

    private void isiBarang() {
        String noInv = NoInv.getText().trim();
        if (noInv.isEmpty()) return;
        runBackground(() -> {
            try {
                ps = koneksi.prepareStatement(
                    "SELECT ib.nm_barang, i.harga, i.status_barang " +
                    "FROM inventaris i " +
                    "INNER JOIN inventaris_barang ib ON ib.kode_barang = i.kode_barang " +
                    "WHERE i.no_inventaris = ? LIMIT 1"
                );
                ps.setString(1, noInv);
                rs = ps.executeQuery();
                if (rs.next()) {
                    final String nama = rs.getString("nm_barang");
                    final double hargaDb = rs.getDouble("harga");
                    final String statusDb = rs.getString("status_barang");
                    if ("Dihapus".equals(statusDb)) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Inventaris ini sudah dihapus/dipensiunkan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            NoInv.setText("");
                            LNamaBarang.setText("-");
                            LHarga.setText("0");
                            LAkmPenyusutan.setText("0");
                            LNilaiBuku.setText("0");
                            LUntungRugi.setText("0");
                            harga = 0; akm = 0; nilaiBuku = 0;
                        });
                        return;
                    }
                    // Get accumulated depreciation
                    ps = koneksi.prepareStatement(
                        "SELECT IFNULL(SUM(beban_penyusutan),0) FROM penyusutan_inventaris WHERE no_inventaris = ?"
                    );
                    ps.setString(1, noInv);
                    rs = ps.executeQuery();
                    double akmDb = rs.next() ? rs.getDouble(1) : 0;
                    double nbDb = hargaDb - akmDb;
                    harga = hargaDb; akm = akmDb; nilaiBuku = nbDb;
                    SwingUtilities.invokeLater(() -> {
                        LNamaBarang.setText(nama);
                        LHarga.setText(Valid.SetRpInt(hargaDb));
                        LAkmPenyusutan.setText(Valid.SetRpInt(akmDb));
                        LNilaiBuku.setText(Valid.SetRpInt(nbDb));
                        hitungUntungRugi();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "No. Inventaris tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        LNamaBarang.setText("-");
                        LHarga.setText("0"); LAkmPenyusutan.setText("0"); LNilaiBuku.setText("0"); LUntungRugi.setText("0");
                        harga = 0; akm = 0; nilaiBuku = 0;
                    });
                }
            } catch (Exception e) {
                System.out.println("InventarisPenghapusan.isiBarang: " + e);
            }
        });
    }

    private void hitungUntungRugi() {
        double nj = Valid.SetAngka(nilai_jual.getText());
        double ur = nj - nilaiBuku;
        LUntungRugi.setText(Valid.SetRpInt(ur));
        LUntungRugi.setForeground(ur >= 0 ? new java.awt.Color(0, 120, 0) : new java.awt.Color(180, 0, 0));
    }

    private void tampilAkun() {
        try {
            ps = koneksi.prepareStatement("SELECT nama_bayar FROM akun_bayar ORDER BY nama_bayar");
            rs = ps.executeQuery();
            SwingUtilities.invokeLater(() -> {
                AkunKas.removeAllItems();
                AkunKas.addItem("");
            });
            while (rs.next()) {
                final String item = rs.getString(1);
                SwingUtilities.invokeLater(() -> AkunKas.addItem(item));
            }
        } catch (Exception e) {
            System.out.println("InventarisPenghapusan.tampilAkun: " + e);
        }
    }

    private void tampil() {
        String cari = TCari.getText().trim();
        runBackground(() -> {
            try {
                String sql =
                    "SELECT p.no_penghapusan, p.no_inventaris, ib.nm_barang, p.tanggal, p.alasan, " +
                    "p.nilai_jual, p.nilai_buku, p.akm_penyusutan, p.untung_rugi, p.keterangan " +
                    "FROM penghapusan_inventaris p " +
                    "INNER JOIN inventaris i ON i.no_inventaris = p.no_inventaris " +
                    "INNER JOIN inventaris_barang ib ON ib.kode_barang = i.kode_barang " +
                    "WHERE p.no_penghapusan LIKE ? OR p.no_inventaris LIKE ? OR ib.nm_barang LIKE ? " +
                    "ORDER BY p.tanggal DESC, p.no_penghapusan DESC";
                String q = "%" + cari + "%";
                ps = koneksi.prepareStatement(sql);
                ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
                rs = ps.executeQuery();
                SwingUtilities.invokeLater(() -> Valid.tabelKosong(tabMode));
                int[] count = {0};
                while (rs.next()) {
                    final Object[] row = {
                        rs.getString("no_penghapusan"),
                        rs.getString("no_inventaris"),
                        rs.getString("nm_barang"),
                        rs.getString("tanggal"),
                        rs.getString("alasan"),
                        Valid.SetRpInt(rs.getDouble("nilai_jual")),
                        Valid.SetRpInt(rs.getDouble("nilai_buku")),
                        Valid.SetRpInt(rs.getDouble("akm_penyusutan")),
                        Valid.SetRpInt(rs.getDouble("untung_rugi")),
                        rs.getString("keterangan")
                    };
                    SwingUtilities.invokeLater(() -> tabMode.addRow(row));
                    count[0]++;
                }
                final int total = count[0];
                SwingUtilities.invokeLater(() -> LCount.setText("Jml Data : " + total));
            } catch (Exception e) {
                System.out.println("InventarisPenghapusan.tampil: " + e);
            }
        });
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (NoPenghapusan.getText().trim().isEmpty()) { Valid.textKosong(NoPenghapusan, "No.Penghapusan"); return; }
        if (NoInv.getText().trim().isEmpty()) { Valid.textKosong(NoInv, "No.Inventaris"); return; }
        if (LNamaBarang.getText().equals("-") || LNamaBarang.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih No. Inventaris yang valid terlebih dahulu.");
            return;
        }
        if (alasan.getSelectedItem() == null || alasan.getSelectedItem().toString().trim().isEmpty()) {
            Valid.textKosong(alasan, "Alasan"); return;
        }
        if (nip.getText().trim().isEmpty()) { Valid.textKosong(nip, "NIP"); return; }

        int confirm = JOptionPane.showConfirmDialog(rootPane,
            "Penghapusan inventaris ini akan bersifat permanen dan tidak dapat dibatalkan.\nLanjutkan?",
            "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Resolve account codes
        String noInvVal = NoInv.getText().trim();
        String kdAkmPenyusutan = Sequel.cariIsiSmc(
            "SELECT aa.kd_rek_akm_penyusutan FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);
        String kdAkunAset = Sequel.cariIsiSmc(
            "SELECT aa.kd_rek FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);
        String kdKeuntungan = Sequel.cariIsiSmc(
            "SELECT IFNULL(aa.kd_rek_keuntungan, (SELECT sa.Keuntungan_Penghapusan_Aset FROM set_akun sa LIMIT 1)) " +
            "FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);
        String kdKerugian = Sequel.cariIsiSmc(
            "SELECT IFNULL(aa.kd_rek_kerugian, (SELECT sa.Kerugian_Penghapusan_Aset FROM set_akun sa LIMIT 1)) " +
            "FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);

        if (kdAkmPenyusutan == null || kdAkmPenyusutan.isEmpty() || kdAkunAset == null || kdAkunAset.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Akun aset/akumulasi penyusutan belum dikonfigurasi untuk jenis barang ini.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (kdKeuntungan == null || kdKeuntungan.isEmpty() || kdKerugian == null || kdKerugian.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Akun keuntungan/kerugian penghapusan aset belum dikonfigurasi.\nSilakan isi di Akun Aset Inventaris atau Set Akun.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double nilaiJualVal = Valid.SetAngka(nilai_jual.getText());
        double untungRugiVal = nilaiJualVal - nilaiBuku;
        String tglVal = Valid.SetTgl(TglHapus.getSelectedItem() + "");
        String noPenghapusanVal = NoPenghapusan.getText().trim();
        String alasanVal = alasan.getSelectedItem().toString();
        String nipVal = nip.getText().trim();
        String ketVal = keterangan.getText().trim();

        String kdKasVal = "";
        if (nilaiJualVal > 0) {
            if (AkunKas.getSelectedItem() == null || AkunKas.getSelectedItem().toString().trim().isEmpty()) {
                Valid.textKosong(AkunKas, "Akun Kas"); return;
            }
            kdKasVal = Sequel.cariIsiSmc("SELECT kd_rek FROM akun_bayar WHERE nama_bayar = ? LIMIT 1",
                AkunKas.getSelectedItem().toString());
            if (kdKasVal == null || kdKasVal.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Akun Kas tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        final String kdKasFinal = kdKasVal;
        Sequel.AutoComitFalse();
        boolean sukses = Sequel.menyimpantf2("penghapusan_inventaris",
            "?,?,?,?,?,?,?,?,?,?,?", "No.Penghapusan", 11, new String[]{
                noPenghapusanVal, noInvVal, tglVal, alasanVal,
                "" + nilaiJualVal, "" + akm, "" + nilaiBuku, "" + untungRugiVal,
                nipVal, ketVal.isEmpty() ? null : ketVal, null
            });

        if (sukses) {
            sukses = Sequel.executeRawSmc(
                "UPDATE inventaris SET status_barang = 'Dihapus' WHERE no_inventaris = ?", noInvVal);
        }

        if (sukses) {
            Sequel.deleteTampJurnal();
            // Dr. Akumulasi Penyusutan
            if (akm > 0) {
                sukses = Sequel.insertTampJurnal(kdAkmPenyusutan, "AKUMULASI PENYUSUTAN INVENTARIS " + noInvVal, akm, 0);
            }
            // Dr. Kas (if sold)
            if (sukses && nilaiJualVal > 0) {
                sukses = Sequel.insertTampJurnal(kdKasFinal, "PENERIMAAN PENGHAPUSAN INVENTARIS " + noInvVal, nilaiJualVal, 0);
            }
            // Dr. Kerugian (if loss)
            if (sukses && untungRugiVal < 0) {
                sukses = Sequel.insertTampJurnal(kdKerugian, "KERUGIAN PENGHAPUSAN INVENTARIS " + noInvVal, Math.abs(untungRugiVal), 0);
            }
            // Cr. Keuntungan (if gain)
            if (sukses && untungRugiVal > 0) {
                sukses = Sequel.insertTampJurnal(kdKeuntungan, "KEUNTUNGAN PENGHAPUSAN INVENTARIS " + noInvVal, 0, untungRugiVal);
            }
            // Cr. Aset Inventaris
            if (sukses) {
                sukses = Sequel.insertTampJurnal(kdAkunAset, "PENGHAPUSAN ASET INVENTARIS " + noInvVal, 0, harga);
            }

            if (sukses) {
                sukses = jur.simpanJurnal(noPenghapusanVal, "U", "PENGHAPUSAN INVENTARIS " + noInvVal);
            }

            if (sukses) {
                String noJurnal = Sequel.cariIsiSmc("SELECT no_jurnal FROM jurnal WHERE no_ref = ?", noPenghapusanVal);
                if (noJurnal != null && !noJurnal.isEmpty()) {
                    Sequel.executeRawSmc("UPDATE penghapusan_inventaris SET no_jurnal = ? WHERE no_penghapusan = ?",
                        noJurnal, noPenghapusanVal);
                }
            }
        }

        if (sukses) {
            Sequel.Commit();
            JOptionPane.showMessageDialog(null, "Penghapusan inventaris berhasil disimpan.");
            emptTeks();
            tampil();
        } else {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
        autoNomor();
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        autoNomor();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil();
    }//GEN-LAST:event_TCariKeyPressed

    private void btnCariInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariInvActionPerformed
        InventarisCariKoleksi inv = new InventarisCariKoleksi(null, true);
        inv.isCek();
        inv.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        inv.setLocationRelativeTo(internalFrame1);
        inv.setAlwaysOnTop(false);
        inv.setVisible(true);
        if (inv.getTable() != null && inv.getTable().getSelectedRow() >= 0) {
            JTable t = inv.getTable();
            NoInv.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
            isiBarang();
        }
    }//GEN-LAST:event_btnCariInvActionPerformed

    private void btnPtgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPtgActionPerformed
        DlgCariPetugas ptg = new DlgCariPetugas(null, true);
        ptg.isCek();
        ptg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ptg.setLocationRelativeTo(internalFrame1);
        ptg.setAlwaysOnTop(false);
        ptg.setVisible(true);
        if (ptg.getTable() != null && ptg.getTable().getSelectedRow() >= 0) {
            JTable t = ptg.getTable();
            nip.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
            nm_petugas.setText(t.getValueAt(t.getSelectedRow(), 1).toString());
        }
    }//GEN-LAST:event_btnPtgActionPerformed

    private void emptTeks() {
        NoInv.setText("");
        LNamaBarang.setText("-");
        LHarga.setText("0");
        LAkmPenyusutan.setText("0");
        LNilaiBuku.setText("0");
        LUntungRugi.setText("0");
        nilai_jual.setText("");
        alasan.setSelectedIndex(0);
        AkunKas.setSelectedIndex(0);
        nip.setText("");
        nm_petugas.setText("");
        keterangan.setText("");
        harga = 0; akm = 0; nilaiBuku = 0;
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
        NoPenghapusan = new widget.TextBox();
        label2 = new widget.Label();
        TglHapus = new widget.Tanggal();
        label3 = new widget.Label();
        NoInv = new widget.TextBox();
        btnCariInv = new widget.Button();
        LNamaBarang = new widget.Label();
        label4 = new widget.Label();
        LHarga = new widget.Label();
        label5 = new widget.Label();
        LAkmPenyusutan = new widget.Label();
        label6 = new widget.Label();
        LNilaiBuku = new widget.Label();
        label7 = new widget.Label();
        alasan = new widget.ComboBox();
        label8 = new widget.Label();
        nilai_jual = new widget.TextBox();
        label9 = new widget.Label();
        LUntungRugi = new widget.Label();
        label10 = new widget.Label();
        AkunKas = new widget.ComboBox();
        label11 = new widget.Label();
        nip = new widget.TextBox();
        btnPtg = new widget.Button();
        nm_petugas = new widget.TextBox();
        label12 = new widget.Label();
        keterangan = new widget.TextBox();
        ChkInput = new widget.CekBox();
        Scroll = new widget.ScrollPane();
        tbHapus = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        label13 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        LCount = new widget.Label();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Penghapusan Inventaris ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(960, 215));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(960, 195));
        FormInput.setLayout(null);

        label1.setText("No.Penghapusan :"); label1.setName("label1");
        FormInput.add(label1); label1.setBounds(0, 8, 115, 23);

        NoPenghapusan.setName("NoPenghapusan");
        FormInput.add(NoPenghapusan); NoPenghapusan.setBounds(118, 8, 130, 23);

        label2.setText("Tanggal :"); label2.setName("label2");
        FormInput.add(label2); label2.setBounds(255, 8, 65, 23);

        TglHapus.setDisplayFormat("dd-MM-yyyy"); TglHapus.setName("TglHapus");
        FormInput.add(TglHapus); TglHapus.setBounds(323, 8, 120, 23);

        label3.setText("No.Inventaris :"); label3.setName("label3");
        FormInput.add(label3); label3.setBounds(0, 38, 105, 23);

        NoInv.setName("NoInv");
        FormInput.add(NoInv); NoInv.setBounds(108, 38, 160, 23);

        btnCariInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/find.png"))); // NOI18N
        btnCariInv.setName("btnCariInv"); btnCariInv.setPreferredSize(new java.awt.Dimension(30, 23));
        btnCariInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnCariInvActionPerformed(evt); }
        });
        FormInput.add(btnCariInv); btnCariInv.setBounds(271, 38, 30, 23);

        LNamaBarang.setText("-"); LNamaBarang.setName("LNamaBarang");
        FormInput.add(LNamaBarang); LNamaBarang.setBounds(305, 38, 590, 23);

        label4.setText("Harga Perolehan :"); label4.setName("label4");
        FormInput.add(label4); label4.setBounds(0, 68, 115, 23);

        LHarga.setText("0"); LHarga.setName("LHarga");
        FormInput.add(LHarga); LHarga.setBounds(118, 68, 130, 23);

        label5.setText("Akm. Penyusutan :"); label5.setName("label5");
        FormInput.add(label5); label5.setBounds(255, 68, 120, 23);

        LAkmPenyusutan.setText("0"); LAkmPenyusutan.setName("LAkmPenyusutan");
        FormInput.add(LAkmPenyusutan); LAkmPenyusutan.setBounds(378, 68, 130, 23);

        label6.setText("Nilai Buku :"); label6.setName("label6");
        FormInput.add(label6); label6.setBounds(515, 68, 80, 23);

        LNilaiBuku.setText("0"); LNilaiBuku.setName("LNilaiBuku");
        FormInput.add(LNilaiBuku); LNilaiBuku.setBounds(598, 68, 160, 23);

        label7.setText("Alasan :"); label7.setName("label7");
        FormInput.add(label7); label7.setBounds(0, 98, 60, 23);

        alasan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Rusak Berat","Hilang","Dijual","Hibah Keluar","Lainnya"}));
        alasan.setName("alasan");
        FormInput.add(alasan); alasan.setBounds(63, 98, 160, 23);

        label8.setText("Nilai Jual (Rp) :"); label8.setName("label8");
        FormInput.add(label8); label8.setBounds(230, 98, 110, 23);

        nilai_jual.setName("nilai_jual");
        FormInput.add(nilai_jual); nilai_jual.setBounds(343, 98, 130, 23);

        label9.setText("Untung/Rugi :"); label9.setName("label9");
        FormInput.add(label9); label9.setBounds(480, 98, 90, 23);

        LUntungRugi.setText("0"); LUntungRugi.setName("LUntungRugi");
        FormInput.add(LUntungRugi); LUntungRugi.setBounds(573, 98, 200, 23);

        label10.setText("Akun Kas :"); label10.setName("label10");
        FormInput.add(label10); label10.setBounds(0, 128, 70, 23);

        AkunKas.setName("AkunKas");
        FormInput.add(AkunKas); AkunKas.setBounds(73, 128, 250, 23);

        label11.setText("NIP :"); label11.setName("label11");
        FormInput.add(label11); label11.setBounds(0, 158, 40, 23);

        nip.setName("nip");
        FormInput.add(nip); nip.setBounds(43, 158, 100, 23);

        btnPtg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/find.png"))); // NOI18N
        btnPtg.setName("btnPtg"); btnPtg.setPreferredSize(new java.awt.Dimension(30, 23));
        btnPtg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnPtgActionPerformed(evt); }
        });
        FormInput.add(btnPtg); btnPtg.setBounds(146, 158, 30, 23);

        nm_petugas.setName("nm_petugas");
        FormInput.add(nm_petugas); nm_petugas.setBounds(179, 158, 250, 23);

        label12.setText("Keterangan :"); label12.setName("label12");
        FormInput.add(label12); label12.setBounds(436, 158, 85, 23);

        keterangan.setName("keterangan");
        FormInput.add(keterangan); keterangan.setBounds(524, 158, 365, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setSelected(true); ChkInput.setName("ChkInput");
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FormInput.setVisible(ChkInput.isSelected());
                PanelInput.setPreferredSize(new java.awt.Dimension(960, ChkInput.isSelected() ? 215 : 25));
                internalFrame1.revalidate();
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        Scroll.setName("Scroll");
        tbHapus.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{}));
        tbHapus.setName("tbHapus");
        Scroll.setViewportView(tbHapus);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(960, 50));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1");
        panelisi1.setPreferredSize(new java.awt.Dimension(960, 50));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label13.setText("Cari :"); label13.setName("label13");
        label13.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi1.add(label13);

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
        LCount.setPreferredSize(new java.awt.Dimension(150, 23));
        panelisi1.add(LCount);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnSimpan.setText("Simpan"); BtnSimpan.setName("BtnSimpan");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(90, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnSimpanActionPerformed(evt); }
        });
        panelisi1.add(BtnSimpan);

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
    private widget.ComboBox AkunKas;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.PanelBiasa FormInput;
    private widget.Label LAkmPenyusutan;
    private widget.Label LCount;
    private widget.Label LHarga;
    private widget.Label LNamaBarang;
    private widget.Label LNilaiBuku;
    private widget.Label LUntungRugi;
    private widget.TextBox NoPenghapusan;
    private widget.TextBox NoInv;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Tanggal TglHapus;
    private widget.ComboBox alasan;
    private widget.Button btnCariInv;
    private widget.Button btnPtg;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox keterangan;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label label4;
    private widget.Label label5;
    private widget.Label label6;
    private widget.Label label7;
    private widget.Label label8;
    private widget.Label label9;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.TextBox nip;
    private widget.TextBox nm_petugas;
    private widget.TextBox nilai_jual;
    private widget.panelisi panelisi1;
    private widget.Table tbHapus;
    // End of variables declaration//GEN-END:variables
}
