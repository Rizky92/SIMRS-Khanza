package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import widget.TanggalCellEditorSMC;

public final class MobileJKNDaftarUlangBookingSMC extends javax.swing.JDialog {
    private static final int KOLOM_PILIH = 0, KOLOM_NOBOOKING = 1, KOLOM_REFERENSI = 8, KOLOM_TGLRENCANA = 9, KOLOM_TGLPERIKSA = 10, KOLOM_JAMPRAKTEK = 11,
        KOLOM_KETERANGAN = 12, KOLOM_KDPOLI = 13, KOLOM_KDDOKTER = 14, KOLOM_TGLMINIMAL = 15, KOLOM_LAYAK = 16;
    private static final String HARI = "elt(dayofweek(?), 'AKHAD', 'SENIN', 'SELASA', 'RABU', 'KAMIS', 'JUMAT', 'SABTU')";
    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ApiBPJS api = new ApiBPJS();
    private final ObjectMapper mapper = new ObjectMapper();

    public MobileJKNDaftarUlangBookingSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {
            "P", "No.Booking Lama", "No.Rawat Lama", "No.RM", "Nama Pasien", "Poliklinik", "Dokter", "Jenis Kunjungan", "No.Referensi", "Tgl.Rencana SKDP",
            "Tgl.Periksa", "Jam Praktek", "Keterangan", "Kode Poli", "Kode Dokter", "Tgl.Minimal", "Layak"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return (KOLOM_PILIH == colIndex || KOLOM_TGLPERIKSA == colIndex || KOLOM_JAMPRAKTEK == colIndex) && Boolean.TRUE.equals(getValueAt(rowIndex, KOLOM_LAYAK));
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (KOLOM_PILIH == columnIndex || KOLOM_LAYAK == columnIndex) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        tbBooking.setModel(tabMode);
        tbBooking.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbBooking.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] lebar = {20, 110, 110, 60, 160, 140, 160, 110, 140, 95, 80, 85, 300};
        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbBooking.getColumnModel().getColumn(i);
            if (i < lebar.length) {
                column.setPreferredWidth(lebar[i]);
            } else {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbBooking.getColumnModel().getColumn(KOLOM_TGLPERIKSA).setCellEditor(new TanggalCellEditorSMC());
        tbBooking.getColumnModel().getColumn(KOLOM_JAMPRAKTEK).setCellEditor(new JamPraktekEditor());
        tbBooking.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode.addTableModelListener(e -> {
            if (TableModelEvent.UPDATE == e.getType() && KOLOM_TGLPERIKSA == e.getColumn() && e.getFirstRow() >= 0) {
                for (int row = e.getFirstRow(); row <= e.getLastRow() && row < tabMode.getRowCount(); row++) {
                    String jam = tabMode.getValueAt(row, KOLOM_JAMPRAKTEK).toString();
                    if (!jam.isBlank() && !jamPraktek(row).contains(jam)) {
                        tabMode.setValueAt("", row, KOLOM_JAMPRAKTEK);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBooking = new widget.Table();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Daftar Ulang Booking Mobile JKN ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbBooking.setName("tbBooking"); // NOI18N
        Scroll.setViewportView(tbBooking);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(52, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass8.add(LCount);

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

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (tbBooking.isEditing()) {
            tbBooking.getCellEditor().stopCellEditing();
        }
        boolean adaPilihan = false;
        for (int i = 0; i < tabMode.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tabMode.getValueAt(i, KOLOM_PILIH)) && Boolean.TRUE.equals(tabMode.getValueAt(i, KOLOM_LAYAK))) {
                adaPilihan = true;
                break;
            }
        }
        if (!adaPilihan) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih booking yang akan didaftarkan ulang..!!");
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String hariIni = Sequel.cariIsiSmc("select current_date()"), batasAkhir = Sequel.cariIsiSmc("select date_add(current_date(), interval 7 day)");
            for (int i = 0; i < tabMode.getRowCount(); i++) {
                if (Boolean.TRUE.equals(tabMode.getValueAt(i, KOLOM_PILIH)) && Boolean.TRUE.equals(tabMode.getValueAt(i, KOLOM_LAYAK))) {
                    String tglPeriksa = tabMode.getValueAt(i, KOLOM_TGLPERIKSA).toString(), jamPraktek = tabMode.getValueAt(i, KOLOM_JAMPRAKTEK).toString(),
                        tglMinimal = tabMode.getValueAt(i, KOLOM_TGLMINIMAL).toString().compareTo(hariIni) > 0 ? tabMode.getValueAt(i, KOLOM_TGLMINIMAL).toString() : hariIni;
                    String keterangan = tglPeriksa.isBlank() ? "Tgl.Periksa masih kosong"
                        : (tglPeriksa.compareTo(tglMinimal) < 0 || tglPeriksa.compareTo(batasAkhir) > 0) ? "Tgl.Periksa harus antara " + tglMinimal + " s.d. " + batasAkhir
                        : jamPraktek.isBlank() ? "Jam praktek masih kosong"
                        : !jamPraktek(i).contains(jamPraktek) ? "Jam praktek " + jamPraktek + " tidak ada di jadwal dokter pada " + tglPeriksa
                        : daftarUlang(tabMode.getValueAt(i, KOLOM_NOBOOKING).toString(), tglPeriksa, jamPraktek);
                    if (keterangan.startsWith("Berhasil")) {
                        tabMode.setValueAt(false, i, KOLOM_LAYAK);
                        tabMode.setValueAt(false, i, KOLOM_PILIH);
                    }
                    tabMode.setValueAt(keterangan, i, KOLOM_KETERANGAN);
                }
            }
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        JOptionPane.showMessageDialog(null, "Proses daftar ulang selesai, silahkan cek kolom keterangan..!!");
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (KeyEvent.VK_SPACE == evt.getKeyCode()) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnKeluar, BtnKeluar);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (KeyEvent.VK_SPACE == evt.getKeyCode()) {
            dispose();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnSimpan);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            MobileJKNDaftarUlangBookingSMC dialog = new MobileJKNDaftarUlangBookingSMC(new javax.swing.JFrame(), true);
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
    private widget.Button BtnSimpan;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel7;
    private widget.panelisi panelGlass8;
    private widget.Table tbBooking;
    // End of variables declaration//GEN-END:variables

    public void setBooking(List<String> daftarBooking) {
        tabMode.setRowCount(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String hariIni = Sequel.cariIsiSmc("select current_date()"), batasAkhir = Sequel.cariIsiSmc("select date_add(current_date(), interval 7 day)");
            for (String nobooking : daftarBooking) {
                DataBooking data = periksaBooking(nobooking);
                String tglRencana = "", tglMinimal = hariIni, tglPeriksa = "", jamPraktek = "", keterangan = data.keterangan;
                if (keterangan.isBlank() && data.jenisKunjungan.startsWith("3")) {
                    try {
                        tglRencana = tglRencanaKontrol(data.noReferensi);
                        Sequel.mengupdatetfSmc("bridging_surat_kontrol_bpjs", "tgl_rencana = ?", "no_surat = ?", tglRencana, data.noReferensi);
                        tglMinimal = tglRencana.compareTo(hariIni) > 0 ? tglRencana : hariIni;
                        keterangan = tglMinimal.compareTo(batasAkhir) > 0 ? "Tgl.rencana kontrol " + tglRencana + " melebihi batas " + batasAkhir : "";
                        tglPeriksa = tglRencana.compareTo(hariIni) >= 0 && tglRencana.compareTo(batasAkhir) <= 0 ? tglRencana : "";
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                        keterangan = "Gagal cek tgl.rencana SKDP di BPJS : " + e.getMessage();
                    }
                } else if (keterangan.isBlank()) {
                    tglPeriksa = data.tglPeriksaLama.compareTo(hariIni) >= 0 && data.tglPeriksaLama.compareTo(batasAkhir) <= 0 ? data.tglPeriksaLama : "";
                }
                tabMode.addRow(new Object[] {
                    false, nobooking, data.noRawatLama, data.noRM, data.namaPasien, data.namaPoli, data.namaDokter, data.jenisKunjungan, data.noReferensi, tglRencana,
                    tglPeriksa, "", keterangan, data.kodePoli, data.kodeDokter, tglMinimal, keterangan.isBlank()
                });
                int row = tabMode.getRowCount() - 1;
                if (!tglPeriksa.isBlank() && jamPraktek(row).contains(data.jamPraktekLama)) {
                    jamPraktek = data.jamPraktekLama;
                }
                tabMode.setValueAt(jamPraktek, row, KOLOM_JAMPRAKTEK);
            }
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        LCount.setText(String.valueOf(tabMode.getRowCount()));
    }

    private DataBooking periksaBooking(String nobooking) {
        DataBooking data = new DataBooking();
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select b.no_rawat, b.norm, ifnull(px.nm_pasien, '') as nm_pasien, b.tanggalperiksa, b.jampraktek, b.jeniskunjungan, b.nomorreferensi, r.no_rawat as no_rawat_reg, " +
            "r.stts, r.status_lanjut, r.status_bayar, exists(select * from bridging_sep s where s.no_rawat = r.no_rawat) as ada_sep, coalesce(r.kd_dokter, (select md.kd_dokter " +
            "from maping_dokter_dpjpvclaim md where md.kd_dokter_bpjs = b.kodedokter limit 1)) as kd_dokter, coalesce(r.kd_poli, (select mp.kd_poli_rs from maping_poli_bpjs mp " +
            "join maping_dokter_dpjpvclaim md on md.kd_dokter_bpjs = b.kodedokter join jadwal j on j.kd_poli = mp.kd_poli_rs and j.kd_dokter = md.kd_dokter and j.hari_kerja = " +
            "elt(dayofweek(b.tanggalperiksa), 'AKHAD', 'SENIN', 'SELASA', 'RABU', 'KAMIS', 'JUMAT', 'SABTU') and j.jam_mulai = concat(left(b.jampraktek, 5), ':00') and " +
            "j.jam_selesai = concat(right(b.jampraktek, 5), ':00') where mp.kd_poli_bpjs = b.kodepoli limit 1), (select if(count(distinct mp.kd_poli_rs) = 1, min(mp.kd_poli_rs), " +
            "null) from maping_poli_bpjs mp join maping_dokter_dpjpvclaim md on md.kd_dokter_bpjs = b.kodedokter join jadwal j on j.kd_poli = mp.kd_poli_rs and j.kd_dokter = " +
            "md.kd_dokter where mp.kd_poli_bpjs = b.kodepoli)) as kd_poli, (select a.nobooking from referensi_mobilejkn_bpjs a " +
            "where a.nomorreferensi = b.nomorreferensi and a.status in ('Belum', 'Checkin') limit 1) as booking_aktif, exists(select * from referensi_mobilejkn_bpjs_batal bt " +
            "where bt.nobooking = b.nobooking and bt.statuskirim = 'Belum') as batal_belum_terkirim from referensi_mobilejkn_bpjs b left join pasien px on " +
            "b.norm = px.no_rkm_medis left join reg_periksa r on b.no_rawat = r.no_rawat where b.nobooking = ? and b.status = 'Batal'"
        )) {
            ps.setString(1, nobooking);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    data.keterangan = "Booking tidak ditemukan atau statusnya bukan Batal";
                    return data;
                }
                data.noRawatLama = rs.getString("no_rawat");
                data.noRM = rs.getString("norm");
                data.namaPasien = rs.getString("nm_pasien");
                data.tglPeriksaLama = rs.getString("tanggalperiksa");
                data.jamPraktekLama = null == rs.getString("jampraktek") ? "" : rs.getString("jampraktek");
                data.jenisKunjungan = null == rs.getString("jeniskunjungan") ? "" : rs.getString("jeniskunjungan");
                data.noReferensi = rs.getString("nomorreferensi");
                data.kodePoli = null == rs.getString("kd_poli") ? "" : rs.getString("kd_poli");
                data.kodeDokter = null == rs.getString("kd_dokter") ? "" : rs.getString("kd_dokter");
                data.namaPoli = data.kodePoli + " - " + Sequel.cariIsiSmc("select poliklinik.nm_poli from poliklinik where poliklinik.kd_poli = ?", data.kodePoli);
                data.namaDokter = Sequel.cariIsiSmc("select dokter.nm_dokter from dokter where dokter.kd_dokter = ?", data.kodeDokter);
                data.batalkanRegLama = null != rs.getString("no_rawat_reg") && !"Batal".equals(rs.getString("stts"));
                data.keterangan = rs.getBoolean("batal_belum_terkirim") ? "Pembatalan belum dikonfirmasi BPJS, tunggu service mengirim ulang pembatalan"
                    : null != rs.getString("booking_aktif") ? "No.Referensi sudah dipakai booking aktif " + rs.getString("booking_aktif")
                    : (data.kodePoli.isBlank() || data.kodeDokter.isBlank()) ? "Mapping poli/dokter atau jadwal praktek tidak ditemukan"
                    : (data.batalkanRegLama && !("Belum".equals(rs.getString("stts")) && "Ralan".equals(rs.getString("status_lanjut")) && "Belum Bayar".equals(rs.getString("status_bayar"))
                        && !rs.getBoolean("ada_sep"))) ? "No.Rawat lama " + data.noRawatLama + " sudah menerima pelayanan, batalkan registrasi tersebut terlebih dahulu"
                    : "";
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            data.keterangan = "Gagal membaca data booking";
        }
        return data;
    }

    private String tglRencanaKontrol(String noSurat) throws Exception {
        String utc = api.getCurrentTimestampAsString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
        headers.add("X-Timestamp", utc);
        headers.add("X-Signature", api.getHmac(utc));
        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
        String url = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/noSuratKontrol/" + noSurat;
        System.out.println("URL : " + url);
        JsonNode root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, new HttpEntity(headers), String.class).getBody());
        JsonNode metadata = root.path("metaData");
        if (!"200".equals(metadata.path("code").asText())) {
            throw new IllegalStateException(metadata.path("message").asText());
        }
        String tglRencana = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("tglRencanaKontrol").asText("");
        if (tglRencana.isBlank()) {
            throw new IllegalStateException("tgl.rencana kontrol tidak ditemukan");
        }
        return tglRencana;
    }

    private List<String> jamPraktek(int row) {
        return Sequel.cariArraySmc(
            "select concat(left(jadwal.jam_mulai, 5), '-', left(jadwal.jam_selesai, 5)) from jadwal where jadwal.kd_dokter = ? and jadwal.kd_poli = ? and jadwal.hari_kerja = " + HARI +
            " order by jadwal.jam_mulai", tabMode.getValueAt(row, KOLOM_KDDOKTER).toString(), tabMode.getValueAt(row, KOLOM_KDPOLI).toString(), tabMode.getValueAt(row, KOLOM_TGLPERIKSA).toString()
        );
    }

    private String daftarUlang(String nobookingLama, String tglPeriksa, String jamPraktek) {
        DataBooking data = periksaBooking(nobookingLama);
        if (!data.keterangan.isBlank()) {
            return data.keterangan;
        }
        String noReg = "", noRawat = "", nobooking = "";
        boolean sukses = true;
        Sequel.AutoComitFalse();
        try {
            if (data.batalkanRegLama) {
                sukses = Sequel.mengupdatetfSmc("reg_periksa", "stts = 'Batal'", "no_rawat = ? and stts = 'Belum' and status_bayar = 'Belum Bayar' and not exists(select * from " +
                    "bridging_sep where bridging_sep.no_rawat = reg_periksa.no_rawat)", data.noRawatLama);
            }
            if (sukses) {
                noReg = Sequel.cariIsiSmc(
                    "select lpad(greatest((select ifnull(max(convert(booking_registrasi.no_reg, signed)), 0) from booking_registrasi where booking_registrasi.kd_poli = ? and " +
                    "booking_registrasi.kd_dokter = ? and booking_registrasi.tanggal_periksa = ?), (select ifnull(max(convert(reg_periksa.no_reg, signed)), 0) from reg_periksa " +
                    "where reg_periksa.kd_poli = ? and reg_periksa.kd_dokter = ? and reg_periksa.tgl_registrasi = ?)) + 1, 3, '0')",
                    data.kodePoli, data.kodeDokter, tglPeriksa, data.kodePoli, data.kodeDokter, tglPeriksa
                );
                noRawat = Sequel.cariIsiSmc(
                    "select concat(date_format(?, '%Y/%m/%d/'), lpad(greatest((select ifnull(max(convert(right(referensi_mobilejkn_bpjs.no_rawat, 6), signed)), 0) from " +
                    "referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.no_rawat like concat(date_format(?, '%Y/%m/%d/'), '%')), (select ifnull(max(convert(right(" +
                    "reg_periksa.no_rawat, 6), signed)), 0) from reg_periksa where reg_periksa.no_rawat like concat(date_format(?, '%Y/%m/%d/'), '%'))) + 1, 6, '0'))",
                    tglPeriksa, tglPeriksa, tglPeriksa
                );
                sukses = !noReg.isBlank() && !noRawat.isBlank() && Sequel.executeRawSmc(
                    "insert into reg_periksa (no_reg, no_rawat, tgl_registrasi, jam_reg, kd_dokter, no_rkm_medis, kd_poli, p_jawab, almt_pj, hubunganpj, biaya_reg, stts, " +
                    "stts_daftar, status_lanjut, kd_pj, umurdaftar, sttsumur, status_bayar, status_poli) select ?, ?, t.tgl, concat(left(t.jam, 5), ':00'), ?, b.norm, ?, " +
                    "p.namakeluarga, concat_ws(', ', p.alamatpj, p.kelurahanpj, p.kecamatanpj, p.kabupatenpj, p.propinsipj), p.keluarga, (select poliklinik.registrasilama " +
                    "from poliklinik where poliklinik.kd_poli = ?), 'Belum', if(b.pasienbaru = '1', 'Baru', 'Lama'), 'Ralan', (select password_asuransi.kd_pj from password_asuransi " +
                    "limit 1), case when timestampdiff(year, p.tgl_lahir, t.tgl) > 0 then timestampdiff(year, p.tgl_lahir, t.tgl) when timestampdiff(month, p.tgl_lahir, t.tgl) > 0 " +
                    "then timestampdiff(month, p.tgl_lahir, t.tgl) else timestampdiff(day, p.tgl_lahir, t.tgl) end, case when timestampdiff(year, p.tgl_lahir, t.tgl) > 0 then 'Th' " +
                    "when timestampdiff(month, p.tgl_lahir, t.tgl) > 0 then 'Bl' else 'Hr' end, 'Belum Bayar', if(b.pasienbaru = '1' or not exists(select * from reg_periksa r where " +
                    "r.no_rkm_medis = b.norm and r.kd_poli = ? and r.no_rawat != ifnull(b.no_rawat, '')), 'Baru', 'Lama') from referensi_mobilejkn_bpjs b join pasien p on " +
                    "b.norm = p.no_rkm_medis cross join (select cast(? as date) as tgl, ? as jam) t where b.nobooking = ?",
                    noReg, noRawat, data.kodeDokter, data.kodePoli, data.kodePoli, data.kodePoli, tglPeriksa, jamPraktek, nobookingLama
                );
            }
            if (sukses) {
                nobooking = Sequel.autonomorSmc("", "", "referensi_mobilejkn_bpjs", "nobooking", 6, "0", tglPeriksa);
                sukses = !nobooking.isBlank() && Sequel.executeRawSmc(
                    "insert into referensi_mobilejkn_bpjs (nobooking, no_rawat, nomorkartu, nik, nohp, kodepoli, pasienbaru, norm, tanggalperiksa, kodedokter, jampraktek, " +
                    "jeniskunjungan, nomorreferensi, nomorantrean, angkaantrean, estimasidilayani, sisakuotajkn, kuotajkn, sisakuotanonjkn, kuotanonjkn, status, validasi, statuskirim) " +
                    "select ?, ?, b.nomorkartu, b.nik, b.nohp, b.kodepoli, b.pasienbaru, b.norm, t.tgl, b.kodedokter, t.jam, b.jeniskunjungan, b.nomorreferensi, concat(?, '-', ?), ?, " +
                    "cast(unix_timestamp(timestamp(t.tgl, concat(left(t.jam, 5), ':00')) + interval (cast(? as signed) * 5) minute) * 1000 as unsigned), greatest(b.kuotajkn - " +
                    "t.terpakai, 0), b.kuotajkn, greatest(b.kuotanonjkn - t.terpakai, 0), b.kuotanonjkn, 'Belum', '0000-00-00 00:00:00', 'Belum' from referensi_mobilejkn_bpjs b " +
                    "cross join (select cast(? as date) as tgl, ? as jam, (select count(*) from reg_periksa where reg_periksa.kd_poli = ? and reg_periksa.kd_dokter = ? and " +
                    "reg_periksa.tgl_registrasi = ?) as terpakai) t where b.nobooking = ?",
                    nobooking, noRawat, data.kodePoli, noReg, noReg, noReg, tglPeriksa, jamPraktek, data.kodePoli, data.kodeDokter, tglPeriksa, nobookingLama
                );
            }
            if (sukses) {
                Sequel.Commit();
                return "Berhasil, No.Booking " + nobooking + ", No.Rawat " + noRawat + ", No.Antrian " + data.kodePoli + "-" + noReg;
            }
            Sequel.RollBack();
            return "Gagal mendaftarkan ulang, silahkan coba lagi";
        } finally {
            Sequel.AutoComitTrue();
        }
    }

    private static final class DataBooking {
        private String noRawatLama = "", noRM = "", namaPasien = "", tglPeriksaLama = "", jamPraktekLama = "", jenisKunjungan = "", noReferensi = "", kodePoli = "", kodeDokter = "",
            namaPoli = "", namaDokter = "", keterangan = "";
        private boolean batalkanRegLama = false;
    }

    private final class JamPraktekEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
        private final widget.ComboBox combo;

        private JamPraktekEditor() {
            super(new widget.ComboBox());
            combo = (widget.ComboBox) getComponent();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            combo.removeAllItems();
            combo.addItem("");
            jamPraktek(table.convertRowIndexToModel(row)).forEach(combo::addItem);
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }
}
