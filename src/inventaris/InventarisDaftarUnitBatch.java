package inventaris;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class InventarisDaftarUnitBatch extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private String asalBarang = "Beli";
    private String noFakturBeli = "";

    public InventarisDaftarUnitBatch(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(20, 10);
        setSize(980, 560);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.", "Kode Barang", "Nama Barang", "Harga", "No. Inventaris *", "No. Seri", "Lokasi (id_ruang)", "Tgl. Pengadaan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 4 || colIndex == 5 || colIndex == 6;
            }
        };
        tbBatch.setModel(tabMode);
        tbBatch.setPreferredScrollableViewportSize(new java.awt.Dimension(920, 380));
        tbBatch.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {35, 90, 200, 110, 150, 130, 110, 100};
        for (int idx = 0; idx < widths.length; idx++) {
            TableColumn col = tbBatch.getColumnModel().getColumn(idx);
            col.setPreferredWidth(widths[idx]);
        }
    }

    public void tambahBaris(String kodeBarang, String namaBarang, double hargaUnit,
                            String asalBarangParam, String tglPengadaan, String noFakturBeliParam) {
        this.asalBarang = asalBarangParam;
        this.noFakturBeli = (noFakturBeliParam == null ? "" : noFakturBeliParam);
        int no = tabMode.getRowCount() + 1;
        tabMode.addRow(new Object[]{no, kodeBarang, namaBarang, (long) hargaUnit, "", "", "", tglPengadaan});
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        int rowCount = tabMode.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(rootPane, "Tidak ada data untuk disimpan.");
            return;
        }
        Set<String> seen = new HashSet<>();
        for (int idx = 0; idx < rowCount; idx++) {
            String noInv = tabMode.getValueAt(idx, 4) == null ? "" : tabMode.getValueAt(idx, 4).toString().trim();
            if (noInv.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "No. Inventaris baris ke-" + (idx + 1) + " tidak boleh kosong.");
                tbBatch.setRowSelectionInterval(idx, idx);
                return;
            }
            if (seen.contains(noInv)) {
                JOptionPane.showMessageDialog(rootPane, "No. Inventaris '" + noInv + "' duplikat pada baris ke-" + (idx + 1) + ".");
                tbBatch.setRowSelectionInterval(idx, idx);
                return;
            }
            seen.add(noInv);
        }

        int reply = JOptionPane.showConfirmDialog(rootPane, "Simpan " + rowCount + " unit ke registrasi inventaris?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;

        boolean sukses = true;
        Sequel.AutoComitFalse();
        for (int idx = 0; idx < rowCount; idx++) {
            String noInv = tabMode.getValueAt(idx, 4).toString().trim();
            String kdBar = tabMode.getValueAt(idx, 1).toString();
            String tgl = tabMode.getValueAt(idx, 7).toString();
            String hrg = "" + (long) Valid.SetAngka(tabMode.getValueAt(idx, 3).toString());
            String noSeri = tabMode.getValueAt(idx, 5) == null ? "" : tabMode.getValueAt(idx, 5).toString().trim();
            String idRuang = tabMode.getValueAt(idx, 6) == null ? "" : tabMode.getValueAt(idx, 6).toString().trim();
            if (idRuang.isEmpty()) idRuang = "-";

            if (!Sequel.menyimpantf2("inventaris", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Inventaris", 14, new String[]{
                noInv, kdBar, asalBarang, tgl, hrg, "Ada", idRuang, "-", "-",
                noSeri.isEmpty() ? null : noSeri, null, "Garis Lurus", "0",
                noFakturBeli.isEmpty() ? null : noFakturBeli
            })) {
                sukses = false;
                break;
            }
        }

        if (sukses) {
            Sequel.Commit();
            JOptionPane.showMessageDialog(rootPane, rowCount + " unit berhasil didaftarkan ke inventaris.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan. Registrasi unit dibatalkan.");
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_BtnBatalActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        scrollPane1 = new widget.ScrollPane();
        tbBatch = new widget.Table();
        jPanel1 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        LInfo = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Daftar Unit Batch Pengadaan Inventaris ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbBatch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {
            }
        ));
        tbBatch.setName("tbBatch"); // NOI18N
        scrollPane1.setViewportView(tbBatch);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 50));
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(900, 50));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnSimpan.setText("Simpan");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelisi1.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete.png"))); // NOI18N
        BtnBatal.setText("Batal");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        panelisi1.add(BtnBatal);

        LInfo.setText(" * Isi No. Inventaris untuk setiap unit. No. Seri dan Lokasi opsional.");
        LInfo.setName("LInfo"); // NOI18N
        LInfo.setPreferredSize(new java.awt.Dimension(540, 30));
        panelisi1.add(LInfo);

        jPanel1.add(panelisi1, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnBatal;
    private widget.Button BtnSimpan;
    private widget.Label LInfo;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel1;
    private widget.panelisi panelisi1;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbBatch;
    // End of variables declaration//GEN-END:variables
}
