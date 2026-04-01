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

public class InventarisPenyusutan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Jurnal jur = new Jurnal();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private double harga = 0, akm = 0, nilaiResidu = 0;
    private int masaManfaat = 0;
    private String metode = "Garis Lurus";

    public InventarisPenyusutan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(990, 680);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Penyusutan","No.Inventaris","Nama Barang","Periode","Tanggal","Beban Penyusutan","Akm.Penyusutan","Nilai Buku","Keterangan"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbSusut.setModel(tabMode);
        tbSusut.setPreferredScrollableViewportSize(new Dimension(900, 300));
        tbSusut.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbSusut.setDefaultRenderer(Object.class, new WarnaTable());
        int[] widths = {110,110,200,80,100,120,120,120,200};
        for (int i = 0; i < widths.length; i++) {
            tbSusut.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        NoSusut.setDocument(new batasInput((byte)20).getKata(NoSusut));
        NoInv.setDocument(new batasInput((byte)30).getKata(NoInv));
        beban_penyusutan.setDocument(new batasInput((byte)20).getOnlyAngka(beban_penyusutan));
        keterangan.setDocument(new batasInput((byte)200).getKata(keterangan));
        periode.setDocument(new batasInput((byte)7).getKata(periode));
        nip.setDocument(new batasInput((byte)20).getKata(nip));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        NoSusut.setEditable(false);
        nm_petugas.setEditable(false);

        nip.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) { isCekPetugas(); }
            public void removeUpdate(DocumentEvent e) { isCekPetugas(); }
            public void changedUpdate(DocumentEvent e) { isCekPetugas(); }
        });

        NoInv.addActionListener(evt -> isiBarang());

        tbSusut.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tbSusut.getSelectedRow();
                if (sel != -1) {
                    LCount.setText("No." + tabMode.getValueAt(sel, 0) + " | " + tabMode.getValueAt(sel, 1) + " | " + tabMode.getValueAt(sel, 2));
                }
            }
        });

        emptTeks();
        runBackground(() -> tampil());
    }

    private void isCekPetugas() {
        if (nip.getText().length() >= 3) {
            nm_petugas.setText(Sequel.CariPetugas(nip.getText()));
        }
    }

    private void autoNomor() {
        try {
            Valid.autoNomer3(
                "SELECT IFNULL(MAX(CONVERT(RIGHT(no_penyusutan,3),signed)),0) FROM penyusutan_inventaris WHERE no_penyusutan LIKE 'PYS" +
                TglSusut.getSelectedItem().toString().substring(6, 10) +
                TglSusut.getSelectedItem().toString().substring(3, 5) +
                TglSusut.getSelectedItem().toString().substring(0, 2) + "%'",
                "PYS" + TglSusut.getSelectedItem().toString().substring(6, 10) +
                TglSusut.getSelectedItem().toString().substring(3, 5) +
                TglSusut.getSelectedItem().toString().substring(0, 2),
                3, NoSusut
            );
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void emptTeks() {
        NoInv.setText("");
        LNamaBarang.setText("-");
        LHarga.setText("0");
        LAkmSebelumnya.setText("0");
        LNilaiBuku.setText("0");
        LMasaManfaat.setText("0");
        LMetode.setText("Garis Lurus");
        LNilaiResidu.setText("0");
        LSuggested.setText("0");
        beban_penyusutan.setText("");
        keterangan.setText("");
        TglSusut.setDate(new Date());
        periode.setText("");
        nip.setText("");
        nm_petugas.setText("");
        harga = 0; akm = 0; nilaiResidu = 0; masaManfaat = 0; metode = "Garis Lurus";
        autoNomor();
        NoInv.requestFocus();
    }

    private void isiBarang() {
        if (NoInv.getText().trim().isEmpty()) return;
        try {
            ps = koneksi.prepareStatement(
                "SELECT i.harga, i.masa_manfaat, i.metode_penyusutan, i.nilai_residu, i.status_barang, ib.nama_barang " +
                "FROM inventaris i INNER JOIN inventaris_barang ib ON ib.kode_barang = i.kode_barang " +
                "WHERE i.no_inventaris = ?"
            );
            ps.setString(1, NoInv.getText().trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                if ("Dihapus".equals(rs.getString("status_barang"))) {
                    JOptionPane.showMessageDialog(rootPane, "Aset ini sudah dihapuskan dan tidak dapat disusutkan lagi.");
                    NoInv.setText("");
                    LNamaBarang.setText("-");
                    rs.close(); ps.close();
                    return;
                }
                harga = rs.getDouble("harga");
                masaManfaat = rs.getInt("masa_manfaat");
                metode = rs.getString("metode_penyusutan") == null ? "Garis Lurus" : rs.getString("metode_penyusutan");
                nilaiResidu = rs.getDouble("nilai_residu");
                LNamaBarang.setText(NoInv.getText().trim() + " | " + rs.getString("nama_barang"));
                LHarga.setText(Valid.SetAngka(harga));
                LMasaManfaat.setText("" + masaManfaat);
                LMetode.setText(metode);
                LNilaiResidu.setText(Valid.SetAngka(nilaiResidu));
            } else {
                JOptionPane.showMessageDialog(rootPane, "No. Inventaris tidak ditemukan.");
                NoInv.setText("");
                LNamaBarang.setText("-");
                rs.close(); ps.close();
                return;
            }
            rs.close(); ps.close();

            ps = koneksi.prepareStatement("SELECT IFNULL(SUM(beban_penyusutan),0) FROM penyusutan_inventaris WHERE no_inventaris = ?");
            ps.setString(1, NoInv.getText().trim());
            rs = ps.executeQuery();
            if (rs.next()) akm = rs.getDouble(1);
            rs.close(); ps.close();

            double nilaiBuku = harga - akm;
            LAkmSebelumnya.setText(Valid.SetAngka(akm));
            LNilaiBuku.setText(Valid.SetAngka(nilaiBuku));

            double suggested = 0;
            if (masaManfaat > 0) {
                if ("Saldo Menurun".equals(metode)) {
                    suggested = nilaiBuku * (2.0 / masaManfaat);
                } else {
                    suggested = (harga - nilaiResidu) / masaManfaat;
                }
            }
            LSuggested.setText(Valid.SetAngka(suggested));
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampil() {
        int jml = 0;
        try {
            Valid.tabelKosong(tabMode);
            ps = koneksi.prepareStatement(
                "SELECT pi.no_penyusutan, pi.no_inventaris, ib.nama_barang, pi.periode, pi.tanggal, " +
                "pi.beban_penyusutan, pi.akm_penyusutan_sd_ini, pi.nilai_buku, pi.keterangan " +
                "FROM penyusutan_inventaris pi " +
                "INNER JOIN inventaris i ON i.no_inventaris = pi.no_inventaris " +
                "INNER JOIN inventaris_barang ib ON ib.kode_barang = i.kode_barang " +
                "WHERE pi.no_penyusutan LIKE ? OR pi.no_inventaris LIKE ? OR ib.nama_barang LIKE ? " +
                "ORDER BY pi.no_penyusutan DESC LIMIT 500"
            );
            String cari = "%" + TCari.getText().trim() + "%";
            ps.setString(1, cari); ps.setString(2, cari); ps.setString(3, cari);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                    Valid.SetAngka(rs.getDouble(6)), Valid.SetAngka(rs.getDouble(7)),
                    Valid.SetAngka(rs.getDouble(8)), rs.getString(9)
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

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (NoInv.getText().trim().isEmpty()) {
            Valid.textKosong(NoInv, "No.Inventaris");
        } else if (LNamaBarang.getText().equals("-") || LNamaBarang.getText().isEmpty()) {
            Valid.textKosong(NoInv, "No.Inventaris (barang tidak ditemukan)");
        } else if (periode.getText().trim().isEmpty() || !periode.getText().matches("\\d{4}-\\d{2}")) {
            Valid.textKosong(periode, "Periode (format YYYY-MM)");
        } else if (beban_penyusutan.getText().trim().isEmpty() || Valid.SetAngka(beban_penyusutan.getText()) <= 0) {
            Valid.textKosong(beban_penyusutan, "Beban Penyusutan");
        } else if (nip.getText().trim().isEmpty()) {
            Valid.textKosong(nip, "NIP Petugas");
        } else {
            String kdAkm = Sequel.cariIsiSmc(
                "SELECT aa.kd_rek_akm_penyusutan FROM akun_aset_inventaris aa " +
                "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
                "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
                "WHERE i.no_inventaris = ?", NoInv.getText()
            );
            String kdBeban = Sequel.cariIsiSmc(
                "SELECT aa.kd_rek_beban_penyusutan FROM akun_aset_inventaris aa " +
                "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
                "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
                "WHERE i.no_inventaris = ?", NoInv.getText()
            );
            if (kdAkm.isEmpty() || kdBeban.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "Akun penyusutan belum dikonfigurasi untuk jenis barang ini.\nSilakan isi akun pada menu Akun Aset Inventaris.");
                return;
            }
            double beban = Valid.SetAngka(beban_penyusutan.getText());
            double akmBaru = akm + beban;
            double nilaiBukuBaru = harga - akmBaru;

            int reply = JOptionPane.showConfirmDialog(rootPane, "Simpan data penyusutan inventaris?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply != JOptionPane.YES_OPTION) return;

            boolean sukses;
            Sequel.AutoComitFalse();
            sukses = Sequel.menyimpantf2("penyusutan_inventaris", "?,?,?,?,?,?,?,?,?,?", "No.Penyusutan", 10, new String[]{
                NoSusut.getText(), NoInv.getText(), periode.getText(),
                Valid.SetTgl(TglSusut.getSelectedItem() + ""),
                "" + beban, "" + akmBaru, "" + nilaiBukuBaru,
                nip.getText(), keterangan.getText().trim(), null
            });
            if (sukses) {
                Sequel.deleteTampJurnal();
                sukses = Sequel.insertTampJurnal(kdBeban, "BEBAN PENYUSUTAN INVENTARIS " + NoInv.getText(), beban, 0);
                if (sukses) sukses = Sequel.insertTampJurnal(kdAkm, "AKUMULASI PENYUSUTAN INVENTARIS " + NoInv.getText(), 0, beban);
                if (sukses) sukses = jur.simpanJurnal(NoSusut.getText(), "U", "PENYUSUTAN INVENTARIS " + NoInv.getText() + " PERIODE " + periode.getText());
                if (sukses) {
                    String noJurnal = Sequel.cariIsiSmc("SELECT no_jurnal FROM jurnal WHERE no_ref = ?", NoSusut.getText());
                    if (!noJurnal.isEmpty()) {
                        Sequel.executeRawSmc("UPDATE penyusutan_inventaris SET no_jurnal = ? WHERE no_penyusutan = ?", noJurnal, NoSusut.getText());
                    }
                }
            }
            if (sukses) {
                Sequel.Commit();
                JOptionPane.showMessageDialog(rootPane, "Penyusutan inventaris berhasil disimpan.");
                runBackground(() -> tampil());
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan saat menyimpan. Transaksi dibatalkan.");
                Sequel.RollBack();
            }
            Sequel.AutoComitTrue();
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            runBackground(() -> tampil());
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void btnCariInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariInvActionPerformed
        InventarisCariKoleksi inv = new InventarisCariKoleksi(null, false);
        inv.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (inv.getTable().getSelectedRow() != -1) {
                    NoInv.setText(inv.getTable().getValueAt(inv.getTable().getSelectedRow(), 0).toString());
                    isiBarang();
                }
                NoInv.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        inv.isCek();
        inv.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        inv.setLocationRelativeTo(internalFrame1);
        inv.setAlwaysOnTop(false);
        inv.setVisible(true);
    }//GEN-LAST:event_btnCariInvActionPerformed

    private void btnPtgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPtgActionPerformed
        DlgCariPetugas petugas = new DlgCariPetugas(null, false);
        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    nip.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    nm_petugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
                nip.requestFocus();
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
        NoSusut = new widget.TextBox();
        label2 = new widget.Label();
        TglSusut = new widget.Tanggal();
        label3 = new widget.Label();
        periode = new widget.TextBox();
        label4 = new widget.Label();
        NoInv = new widget.TextBox();
        btnCariInv = new widget.Button();
        LNamaBarang = new widget.Label();
        label5 = new widget.Label();
        LHarga = new widget.Label();
        label6 = new widget.Label();
        LMasaManfaat = new widget.Label();
        label7 = new widget.Label();
        LMetode = new widget.Label();
        label8 = new widget.Label();
        LNilaiResidu = new widget.Label();
        label9 = new widget.Label();
        LAkmSebelumnya = new widget.Label();
        label10 = new widget.Label();
        LNilaiBuku = new widget.Label();
        label11 = new widget.Label();
        LSuggested = new widget.Label();
        label12 = new widget.Label();
        beban_penyusutan = new widget.TextBox();
        label13 = new widget.Label();
        keterangan = new widget.TextBox();
        label14 = new widget.Label();
        nip = new widget.TextBox();
        btnPtg = new widget.Button();
        nm_petugas = new widget.TextBox();
        ChkInput = new widget.CekBox();
        Scroll = new widget.ScrollPane();
        tbSusut = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        label15 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        LCount = new widget.Label();
        BtnBatal = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Penyusutan Inventaris ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(960, 205));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(960, 185));
        FormInput.setLayout(null);

        label1.setText("No.Penyusutan :");
        label1.setName("label1"); // NOI18N
        FormInput.add(label1);
        label1.setBounds(0, 8, 115, 23);

        NoSusut.setName("NoSusut"); // NOI18N
        FormInput.add(NoSusut);
        NoSusut.setBounds(118, 8, 140, 23);

        label2.setText("Tanggal :");
        label2.setName("label2"); // NOI18N
        FormInput.add(label2);
        label2.setBounds(265, 8, 60, 23);

        TglSusut.setName("TglSusut"); // NOI18N
        FormInput.add(TglSusut);
        TglSusut.setBounds(328, 8, 120, 23);

        label3.setText("Periode :");
        label3.setName("label3"); // NOI18N
        FormInput.add(label3);
        label3.setBounds(455, 8, 60, 23);

        periode.setName("periode"); // NOI18N
        FormInput.add(periode);
        periode.setBounds(518, 8, 85, 23);

        label4.setText("No.Inventaris :");
        label4.setName("label4"); // NOI18N
        FormInput.add(label4);
        label4.setBounds(0, 38, 100, 23);

        NoInv.setName("NoInv"); // NOI18N
        FormInput.add(NoInv);
        NoInv.setBounds(103, 38, 160, 23);

        btnCariInv.setName("btnCariInv"); // NOI18N
        btnCariInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariInvActionPerformed(evt);
            }
        });
        FormInput.add(btnCariInv);
        btnCariInv.setBounds(266, 38, 30, 23);

        LNamaBarang.setText("-");
        LNamaBarang.setName("LNamaBarang"); // NOI18N
        FormInput.add(LNamaBarang);
        LNamaBarang.setBounds(302, 38, 580, 23);

        label5.setText("Harga Awal :");
        label5.setName("label5"); // NOI18N
        FormInput.add(label5);
        label5.setBounds(0, 68, 100, 23);

        LHarga.setText("0");
        LHarga.setName("LHarga"); // NOI18N
        FormInput.add(LHarga);
        LHarga.setBounds(103, 68, 120, 23);

        label6.setText("Masa Manfaat :");
        label6.setName("label6"); // NOI18N
        FormInput.add(label6);
        label6.setBounds(228, 68, 100, 23);

        LMasaManfaat.setText("0");
        LMasaManfaat.setName("LMasaManfaat"); // NOI18N
        FormInput.add(LMasaManfaat);
        LMasaManfaat.setBounds(331, 68, 55, 23);

        label7.setText("Metode :");
        label7.setName("label7"); // NOI18N
        FormInput.add(label7);
        label7.setBounds(393, 68, 60, 23);

        LMetode.setText("Garis Lurus");
        LMetode.setName("LMetode"); // NOI18N
        FormInput.add(LMetode);
        LMetode.setBounds(456, 68, 140, 23);

        label8.setText("Nilai Residu :");
        label8.setName("label8"); // NOI18N
        FormInput.add(label8);
        label8.setBounds(602, 68, 90, 23);

        LNilaiResidu.setText("0");
        LNilaiResidu.setName("LNilaiResidu"); // NOI18N
        FormInput.add(LNilaiResidu);
        LNilaiResidu.setBounds(695, 68, 120, 23);

        label9.setText("Akm.Penyusutan :");
        label9.setName("label9"); // NOI18N
        FormInput.add(label9);
        label9.setBounds(0, 98, 120, 23);

        LAkmSebelumnya.setText("0");
        LAkmSebelumnya.setName("LAkmSebelumnya"); // NOI18N
        FormInput.add(LAkmSebelumnya);
        LAkmSebelumnya.setBounds(123, 98, 130, 23);

        label10.setText("Nilai Buku :");
        label10.setName("label10"); // NOI18N
        FormInput.add(label10);
        label10.setBounds(260, 98, 80, 23);

        LNilaiBuku.setText("0");
        LNilaiBuku.setName("LNilaiBuku"); // NOI18N
        FormInput.add(LNilaiBuku);
        LNilaiBuku.setBounds(343, 98, 130, 23);

        label11.setText("Saran Beban :");
        label11.setName("label11"); // NOI18N
        FormInput.add(label11);
        label11.setBounds(480, 98, 90, 23);

        LSuggested.setText("0");
        LSuggested.setName("LSuggested"); // NOI18N
        FormInput.add(LSuggested);
        LSuggested.setBounds(573, 98, 130, 23);

        label12.setText("Beban Penyusutan *:");
        label12.setName("label12"); // NOI18N
        FormInput.add(label12);
        label12.setBounds(0, 128, 130, 23);

        beban_penyusutan.setName("beban_penyusutan"); // NOI18N
        FormInput.add(beban_penyusutan);
        beban_penyusutan.setBounds(133, 128, 150, 23);

        label13.setText("Keterangan :");
        label13.setName("label13"); // NOI18N
        FormInput.add(label13);
        label13.setBounds(292, 128, 80, 23);

        keterangan.setName("keterangan"); // NOI18N
        FormInput.add(keterangan);
        keterangan.setBounds(375, 128, 440, 23);

        label14.setText("NIP :");
        label14.setName("label14"); // NOI18N
        FormInput.add(label14);
        label14.setBounds(0, 158, 35, 23);

        nip.setName("nip"); // NOI18N
        FormInput.add(nip);
        nip.setBounds(38, 158, 100, 23);

        btnPtg.setName("btnPtg"); // NOI18N
        btnPtg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPtgActionPerformed(evt);
            }
        });
        FormInput.add(btnPtg);
        btnPtg.setBounds(141, 158, 30, 23);

        nm_petugas.setName("nm_petugas"); // NOI18N
        FormInput.add(nm_petugas);
        nm_petugas.setBounds(174, 158, 250, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setSelected(true);
        ChkInput.setName("ChkInput"); // NOI18N
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.LINE_START);

        Scroll.setName("Scroll"); // NOI18N

        tbSusut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbSusut.setName("tbSusut"); // NOI18N
        Scroll.setViewportView(tbSusut);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(960, 50));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(960, 50));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label15.setText("Cari :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi1.add(label15);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setText("Cari");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(85, 30));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelisi1.add(BtnCari);

        LCount.setText("Jml Data : 0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(300, 23));
        panelisi1.add(LCount);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete.png"))); // NOI18N
        BtnBatal.setText("Batal/Reset");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        panelisi1.add(BtnBatal);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelisi1.add(BtnSimpan);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setText("Keluar");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(95, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
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
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.PanelBiasa FormInput;
    private widget.Label LAkmSebelumnya;
    private widget.Label LCount;
    private widget.Label LHarga;
    private widget.Label LMasaManfaat;
    private widget.Label LMetode;
    private widget.Label LNamaBarang;
    private widget.Label LNilaiBuku;
    private widget.Label LNilaiResidu;
    private widget.Label LSuggested;
    private widget.TextBox NoInv;
    private widget.TextBox NoSusut;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Tanggal TglSusut;
    private widget.TextBox beban_penyusutan;
    private widget.Button btnCariInv;
    private widget.Button btnPtg;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox keterangan;
    private widget.Label label1;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label label4;
    private widget.Label label5;
    private widget.Label label6;
    private widget.Label label7;
    private widget.Label label8;
    private widget.Label label9;
    private widget.TextBox nip;
    private widget.TextBox nm_petugas;
    private widget.panelisi panelisi1;
    private widget.TextBox periode;
    private widget.Table tbSusut;
    // End of variables declaration//GEN-END:variables
}
