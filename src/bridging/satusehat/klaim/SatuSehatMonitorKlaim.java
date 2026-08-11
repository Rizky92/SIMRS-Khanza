/*
  by Ananda Widitomo,S.Kom.
 */
package bridging.satusehat.klaim;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Monitoring Klaim SATUSEHAT — langkah 8/9/10 alur Klaim BPJS-K dalam satu layar.
 *
 * Tiga aksi:
 *   1. Tampilkan  — baca `satu_sehat_claimresponse` (tanpa jaringan)
 *   2. Sinkron    — GET ClaimResponse purifikasi & verifikasi (SatuSehatClaimResponse)
 *   3. Keputusan  — kirim PurificationDecision "Lanjut" (SatuSehatPurificationDecision)
 *
 * Dialog ini ditulis TANPA berkas .form (UI dirakit tangan). Menu SATUSEHAT di
 * frmUtama memang sudah dipelihara manual, dan .form buatan tangan gampang tidak
 * sinkron dengan initComponents() hasil generate.
 *
 * Sinkronisasi memanggil jaringan per SEP (jeda 350 ms) sehingga WAJIB di luar EDT;
 * dijalankan lewat executor, hasil dikembalikan ke EDT dengan invokeLater.
 */
public final class SatuSehatMonitorKlaim extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DecimalFormat rupiah = new DecimalFormat("#,##0");
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Tanggal dibaca lewat {@code Tanggal.getDate()} lalu diformat sendiri.
     *
     * Pola lazim di repo `Valid.SetTgl(DTP.getSelectedItem()+"")` sebenarnya SAH: dia
     * memotong string di posisi tetap, dan itu aman karena setiap pemakai widget.Tanggal
     * SELALU memanggil `setDisplayFormat("dd-MM-yyyy")` lebih dulu (dicek: 885 berkas
     * memakai widget.Tanggal, 885 memanggil setDisplayFormat). Format tampilan itulah
     * kontraknya — bukan locale JVM.
     *
     * Di sini tetap dipakai getDate() karena tidak bergantung pada format tampilan sama
     * sekali, jadi mengubah tampilan tak akan diam-diam merusak query.
     */
    private final java.text.SimpleDateFormat fmtTgl = new java.text.SimpleDateFormat("yyyy-MM-dd");

    private final DefaultTableModel tabMode;
    private final widget.Table tbKlaim = new widget.Table();
    private final widget.Tanggal DTPCari1 = new widget.Tanggal();
    private final widget.Tanggal DTPCari2 = new widget.Tanggal();
    private final widget.TextBox TCari = new widget.TextBox();
    private final widget.CekBox ChkBelumDiputuskan = new widget.CekBox();
    private final widget.Button BtnTampil = new widget.Button();
    private final widget.Button BtnSinkron = new widget.Button();
    private final widget.Button BtnWebhook = new widget.Button();
    private final widget.Button BtnKeputusan = new widget.Button();
    private final widget.Button BtnDetail = new widget.Button();
    private final widget.Button BtnKeluar = new widget.Button();
    private final widget.ProgressBar Progres = new widget.ProgressBar();
    private final widget.Label LabelStatus = new widget.Label();
    private final widget.Label LabelTotal = new widget.Label();

    /**
     * Batas sinyal webhook yang ditindaklanjuti sekali tekan. Tiap sinyal bisa berujung
     * satu GET + jeda 350 ms, jadi angkanya dipatok supaya operator tidak menunggu
     * bermenit-menit bila antrean menumpuk; sisanya tinggal ditekan lagi.
     */
    private static final int MAKS_SINYAL_PER_PUTARAN = 100;

    // Kolom 6..10 = uang (disimpan Double supaya urut & rata kanan benar).
    private static final int KOL_UANG_AWAL = 6, KOL_UANG_AKHIR = 10;
    private static final int KOL_ST_PURIF = 3, KOL_KEPUTUSAN = 4, KOL_ST_VERIF = 5;
    private static final int KOL_SELISIH = 8, KOL_TGL_BAYAR = 11, KOL_TGL_SYNC = 12;
    private static final String[] KOLOM = {
        "No.Rawat", "No.SEP", "No.Batch", "Status Purifikasi", "Keputusan",
        "Status Verifikasi", "Diajukan", "Disetujui", "Selisih", "Copay",
        "Dibayar", "Tgl Bayar", "Sinkron Terakhir"
    };
    /**
     * Lebar disetel dari isi TERPANJANG yang mungkin, bukan dari nama kolom:
     * No.Rawat "2026/05/05/000022" (17 huruf), No.SEP "0161R0040526V000098" (19),
     * Status Purifikasi "Tidak Lolos Purifikasi" (22). Kalau kurang, teksnya dipotong
     * jadi "2026/05/05/00..." — ketahuan saat memeriksa render, 26 Juli 2026.
     */
    private static final int[] LEBAR = {125, 150, 95, 150, 75, 115, 85, 85, 85, 75, 85, 85, 110};

    private static final Color HIJAU = new Color(0, 130, 60);
    private static final Color JINGGA = new Color(190, 100, 0);
    private static final Color MERAH = new Color(180, 0, 0);
    private static final Color ABU = new Color(140, 140, 140);
    private static final Color BARIS_GANJIL = new Color(207, 226, 243);   // sama dgn fungsi.WarnaTable

    public SatuSehatMonitorKlaim(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("::[ Monitoring Klaim Satu Sehat ]::");
        // Dipatok seperti DlgTTESatuSehat & dialog SatuSehat lainnya: tanpa title bar OS
        // (jadi tak bisa digeser-geser dan tak terkesan "aplikasi di dalam aplikasi") dan
        // ukurannya tetap — frmUtama sudah menyetelnya sepas PanelUtama.
        // Menutup tetap bisa lewat tombol Keluar atau Esc.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        tabMode = new DefaultTableModel(null, KOLOM) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;   // tabel monitoring: baca saja
            }

            @Override
            public Class<?> getColumnClass(int col) {
                // Kolom uang bertipe Double agar pengurutan numerik, bukan alfabetis.
                return (col >= KOL_UANG_AWAL && col <= KOL_UANG_AKHIR) ? Double.class : String.class;
            }
        };
        siapkanTabel();
        rakitTampilan();

        // Default rentang: 30 hari terakhir — layar monitoring, bukan entri harian.
        java.util.Calendar cal = java.util.Calendar.getInstance();
        DTPCari2.setDate(cal.getTime());
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
        DTPCari1.setDate(cal.getTime());

        setSize(1080, 640);
        setLocationRelativeTo(parent);
    }

    // ====================== UI ======================

    private void siapkanTabel() {
        tbKlaim.setModel(tabMode);
        // ALL_COLUMNS: dialog dibuka selebar PanelUtama (±1900 px). Dengan OFF tersisa
        // kolom kosong menganga di kanan; dengan LAST_COLUMN seluruh sisa (±600 px)
        // menumpuk di satu kolom terakhir. ALL_COLUMNS membagi sisa itu proporsional
        // sesuai lebar di LEBAR[], jadi tak ada yang terpotong maupun menganga.
        tbKlaim.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbKlaim.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbKlaim.setRowHeight(21);
        tbKlaim.setAutoCreateRowSorter(true);          // klik header untuk mengurutkan
        tbKlaim.setShowGrid(false);
        tbKlaim.setIntercellSpacing(new Dimension(0, 0));
        tbKlaim.getTableHeader().setFont(new Font("Tahoma", 0, 11));
        // Kolom DIKUNCI: tak bisa digeser lebarnya maupun ditukar urutannya.
        // LEBAR[] dihitung dari isi terpanjang yang mungkin (lih. catatan di konstanta itu);
        // sekali batasnya digeser tangan, teksnya terpotong jadi "2026/05/05/00..." dan
        // pemakai mengira datanya yang salah. Menukar urutan kolom lebih berbahaya lagi:
        // renderer mewarnai BERDASARKAN NOMOR kolom (KOL_ST_PURIF dst), jadi kolom yang
        // dipindah akan membawa warna kolom lain — klaim ditolak bisa tampil hijau.
        tbKlaim.getTableHeader().setResizingAllowed(false);
        tbKlaim.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < KOLOM.length; i++) {
            tbKlaim.getColumnModel().getColumn(i).setPreferredWidth(LEBAR[i]);
            tbKlaim.getColumnModel().getColumn(i).setCellRenderer(new RendererKlaim());
        }
        // Klik ganda pada baris = lihat JSON mentah dari SATUSEHAT.
        tbKlaim.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) lihatDetail();
            }
        });
    }

    /**
     * Satu renderer untuk semua kolom: belang baris ala fungsi.WarnaTable, uang rata
     * kanan berformat ribuan, dan status diberi warna supaya baris bermasalah langsung
     * kelihatan tanpa harus dibaca satu per satu.
     */
    private final class RendererKlaim extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai, boolean terpilih,
                boolean fokus, int baris, int kolom) {
            String teks = nilai == null ? "" : nilai.toString();
            boolean uang = kolom >= KOL_UANG_AWAL && kolom <= KOL_UANG_AKHIR;
            if (uang && nilai instanceof Double) {
                double v = (Double) nilai;
                teks = v == 0 ? "-" : rupiah.format(v);
            }
            Component c = super.getTableCellRendererComponent(tabel, teks, terpilih, fokus, baris, kolom);
            // Uang rata kanan; dua kolom tanggal di tengah supaya tidak menempel pada
            // angka "Dibayar" yang rata kanan tepat di sebelahnya.
            boolean tanggal = kolom == KOL_TGL_BAYAR || kolom == KOL_TGL_SYNC;
            setHorizontalAlignment(uang ? RIGHT : (tanggal ? CENTER : LEFT));
            if (!terpilih) {
                c.setBackground(baris % 2 == 1 ? BARIS_GANJIL : Color.WHITE);
                c.setForeground(warnaTeks(kolom, teks));
            }
            return c;
        }

        private Color warnaTeks(int kolom, String teks) {
            String t = teks.toLowerCase().trim();
            if (kolom == KOL_ST_PURIF || kolom == KOL_ST_VERIF) {
                if (t.isEmpty()) return ABU;                             // belum ada hasil
                // NEGASI DICEK DULU: "Tidak Lolos Purifikasi" mengandung "lolos" dan
                // "Tidak Layak" mengandung "layak" — kalau dibalik urutannya, klaim yang
                // DITOLAK justru tampil hijau. (Ketahuan saat memeriksa render, 26 Jul 2026.)
                if (t.startsWith("tidak") || t.contains("gagal") || t.contains("batal")
                        || t.contains("ditolak")) {
                    return MERAH;
                }
                if (t.contains("lolos") || t.contains("layak")) return HIJAU;
                return JINGGA;                                           // ada hasil, tapi tak dikenali
            }
            if (kolom == KOL_KEPUTUSAN) return t.isEmpty() ? ABU : HIJAU;
            if (kolom == KOL_SELISIH && !t.equals("-") && !t.isEmpty()) return MERAH;
            return Color.DARK_GRAY;
        }
    }

    private void rakitTampilan() {
        // Font style 0 (plain) — bukan bold, mengikuti gaya tampilan aplikasi.
        Font f = new Font("Tahoma", 0, 11);

        // --- Baris 1: penyaring saja ----------------------------------------------
        // Format tampilan WAJIB dipatok: tanpa ini picker memakai format locale JVM
        // ("7/26/26, 6:02 PM") yang kepanjangan untuk kolomnya dan menampilkan jam
        // yang tak relevan untuk penyaring tanggal.
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new Dimension(105, 25));
        DTPCari2.setPreferredSize(new Dimension(105, 25));

        widget.panelisi filter = new widget.panelisi();
        filter.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 8));
        filter.setPreferredSize(new Dimension(100, 42));
        filter.add(label("Tanggal SEP :", f));
        filter.add(DTPCari1);
        filter.add(label("s/d", f));
        filter.add(DTPCari2);
        filter.add(label("      Cari :", f));
        TCari.setPreferredSize(new Dimension(240, 25));
        TCari.setFont(f);
        TCari.setToolTipText("No.Rawat / No.SEP / No.Batch / status — tekan Enter untuk menampilkan");
        filter.add(TCari);
        ChkBelumDiputuskan.setText("Hanya yang belum diputuskan");
        ChkBelumDiputuskan.setFont(f);
        ChkBelumDiputuskan.setToolTipText("Sudah lolos purifikasi tapi PurificationDecision belum dikirim");
        filter.add(ChkBelumDiputuskan);

        // --- Baris 2: semua aksi + progres ----------------------------------------
        widget.panelisi aksi = new widget.panelisi();
        aksi.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        aksi.setPreferredSize(new Dimension(100, 44));
        aksi.add(tombol(BtnTampil, "Tampilkan", "Search-16x16.png", 'T', f, 130,
                "Alt+T — baca dari database, tanpa menghubungi SATUSEHAT"));
        aksi.add(tombol(BtnSinkron, "Sinkron ClaimResponse", "refresh.png", 'S', f, 215,
                "Alt+S — langkah 8 & 10, tarik hasil purifikasi & verifikasi dari SATUSEHAT"));
        aksi.add(tombol(BtnWebhook, "Sinkron Webhook", "refresh.png", 'W', f, 165,
                "Alt+W — tarik hanya klaim yang diberitakan webhook (jauh lebih hemat daripada per rentang tanggal)"));
        aksi.add(tombol(BtnKeputusan, "Kirim Keputusan Lanjut", "accept.png", 'K', f, 215,
                "Alt+K — langkah 9, kirim PurificationDecision TK000049 untuk baris terpilih"));
        aksi.add(tombol(BtnDetail, "Lihat JSON", "Edit.png", 'J', f, 140,
                "Alt+J — balasan mentah SATUSEHAT untuk baris terpilih (klik ganda juga bisa)"));
        Progres.setPreferredSize(new Dimension(200, 22));
        Progres.setStringPainted(true);
        Progres.setVisible(false);
        aksi.add(Progres);
        LabelStatus.setFont(f);
        LabelStatus.setText(" ");
        aksi.add(LabelStatus);

        widget.panelisi atas = new widget.panelisi();
        atas.setLayout(new BorderLayout());
        atas.setPreferredSize(new Dimension(100, 88));
        atas.add(filter, BorderLayout.NORTH);
        atas.add(aksi, BorderLayout.CENTER);

        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbKlaim);

        // --- Bawah: ringkasan + keluar --------------------------------------------
        widget.panelisi bawah = new widget.panelisi();
        bawah.setLayout(new BorderLayout());
        bawah.setPreferredSize(new Dimension(100, 32));
        LabelTotal.setFont(f);
        LabelTotal.setText("Belum ada data. Tekan Tampilkan.");
        widget.panelisi kiri = new widget.panelisi();
        kiri.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        kiri.add(LabelTotal);
        widget.panelisi kanan = new widget.panelisi();
        kanan.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        kanan.add(tombol(BtnKeluar, "Keluar", "exit.png", 'U', f, 110, "Alt+U / Esc — tutup"));
        bawah.add(kiri, BorderLayout.CENTER);
        bawah.add(kanan, BorderLayout.EAST);

        widget.InternalFrame isi = new widget.InternalFrame();
        // Judul pindah ke TitledBorder karena title bar OS sudah dilepas (setUndecorated).
        // Pola sama dengan DlgTTESatuSehat & SatuSehatBundle. Font plain, bukan bold.
        isi.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
                "::[ Monitoring Klaim Satu Sehat ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                f, new Color(50, 50, 50)));
        isi.setFont(f);
        isi.setLayout(new BorderLayout(1, 1));
        isi.add(atas, BorderLayout.NORTH);
        isi.add(scroll, BorderLayout.CENTER);
        isi.add(bawah, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(isi, BorderLayout.CENTER);

        BtnTampil.addActionListener(e -> tampilkan());
        BtnSinkron.addActionListener(e -> sinkron());
        BtnWebhook.addActionListener(e -> sinkronWebhook());
        BtnKeputusan.addActionListener(e -> kirimKeputusan());
        BtnDetail.addActionListener(e -> lihatDetail());
        BtnKeluar.addActionListener(e -> dispose());
        TCari.addActionListener(e -> tampilkan());
        ChkBelumDiputuskan.addActionListener(e -> tampilkan());

        // Esc menutup dialog.
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private widget.Label label(String teks, Font f) {
        widget.Label l = new widget.Label();
        l.setText(teks);
        l.setFont(f);
        return l;
    }

    /**
     * Tombol bergaya rumahan: ikon + mnemonic + tinggi 30 (samakan dengan dialog lain).
     *
     * Lebar WAJIB dipatok cukup: widget.Button (usu.widget.ButtonGlass) MEMOTONG teks
     * bila preferredSize kurang, bukan melebar mengikuti isinya — gejalanya label jadi
     * "Sinkron ClaimRespo...". Patokan aman ±9 px per karakter + 30 px untuk ikon.
     */
    private widget.Button tombol(widget.Button b, String teks, String ikon, char mnemonic,
            Font f, int lebar, String tip) {
        b.setText(teks);
        b.setFont(f);
        b.setMnemonic(mnemonic);
        b.setPreferredSize(new Dimension(lebar, 30));
        b.setToolTipText(tip);
        try {
            b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/" + ikon)));
        } catch (Exception e) {
            // Ikon hilang tak boleh menggagalkan dialog — teksnya sudah cukup jelas.
        }
        return b;
    }

    /** Dipanggil frmUtama setelah konstruktor, mengikuti pola dialog lain. */
    public void isCek() {
        tampilkan();
    }

    // ====================== AKSI ======================

    /**
     * Baca dari DB saja — tidak menyentuh jaringan.
     * Rentang tanggal memakai TANGGAL SEP (bukan tgl_sync) supaya artinya sama dengan
     * rentang yang dipakai tombol Sinkron; satu kontrol, satu makna.
     */
    private void tampilkan() {
        tabMode.setRowCount(0);
        double tDiajukan = 0, tDisetujui = 0, tCopay = 0, tBayar = 0;
        int belumDiputuskan = 0;
        String cari = "%" + TCari.getText().trim() + "%";
        String sql = "select c.no_rawat, max(ifnull(s.no_sep,'')) as no_sep, ifnull(c.no_batch,'') as no_batch, "
                + "ifnull(c.status_purifikasi,'') as st_purif, ifnull(c.keputusan_display,'') as keputusan, "
                + "ifnull(c.status_verifikasi,'') as st_verif, ifnull(c.nilai_diajukan,0) as diajukan, "
                + "ifnull(c.nilai_disetujui,0) as disetujui, ifnull(c.nilai_copay,0) as copay, "
                + "ifnull(c.nilai_bayar,0) as bayar, c.tgl_bayar, c.tgl_sync, "
                + "ifnull(c.id_claimresponse_purifikasi,'') as id_purif, "
                + "ifnull(c.id_purificationdecision,'') as id_pd "
                + "from satu_sehat_claimresponse c "
                + "inner join bridging_sep s on s.no_rawat=c.no_rawat "
                + "where s.tglsep between ? and ? "
                + "and (c.no_rawat like ? or ifnull(s.no_sep,'') like ? or ifnull(c.no_batch,'') like ? "
                + "     or ifnull(c.status_purifikasi,'') like ? or ifnull(c.status_verifikasi,'') like ?) "
                + (ChkBelumDiputuskan.isSelected()
                        ? "and ifnull(c.id_claimresponse_purifikasi,'')<>'' and ifnull(c.id_purificationdecision,'')='' "
                        : "")
                + "group by c.no_rawat order by max(s.tglsep) desc";
        try {
            PreparedStatement p = koneksi.prepareStatement(sql);
            p.setString(1, tgl(DTPCari1));
            p.setString(2, tgl(DTPCari2));
            for (int i = 3; i <= 7; i++) p.setString(i, cari);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                double diajukan = r.getDouble("diajukan"), disetujui = r.getDouble("disetujui");
                double copay = r.getDouble("copay"), bayar = r.getDouble("bayar");
                tabMode.addRow(new Object[]{
                    r.getString("no_rawat"), r.getString("no_sep"), r.getString("no_batch"),
                    r.getString("st_purif"), r.getString("keputusan"), r.getString("st_verif"),
                    diajukan, disetujui, diajukan - disetujui, copay, bayar,
                    nz(r.getString("tgl_bayar")), potongJam(nz(r.getString("tgl_sync")))
                });
                tDiajukan += diajukan;
                tDisetujui += disetujui;
                tCopay += copay;
                tBayar += bayar;
                if (!nz(r.getString("id_purif")).equals("") && nz(r.getString("id_pd")).equals("")) {
                    belumDiputuskan++;
                }
            }
            r.close();
            p.close();
        } catch (Exception e) {
            System.out.println("Notifikasi Monitor Klaim tampilkan : " + e);
            LabelStatus.setText("Gagal membaca data — cek log.");
        }
        LabelTotal.setText(tabMode.getRowCount() + " klaim      Diajukan " + rp(tDiajukan)
                + "      Disetujui " + rp(tDisetujui) + "      Selisih " + rp(tDiajukan - tDisetujui)
                + "      Copay " + rp(tCopay) + "      Dibayar " + rp(tBayar)
                + "      |  belum diputuskan: " + belumDiputuskan);
        if (tabMode.getRowCount() == 0) {
            LabelStatus.setText("Tidak ada data pada rentang ini. Tekan Sinkron untuk menariknya dari SATUSEHAT.");
        } else {
            LabelStatus.setText(" ");
        }
    }

    /**
     * Tarik ClaimResponse dari SATUSEHAT untuk rentang tanggal SEP.
     * Berjalan di executor: satu GET per SEP + jeda 350 ms, tak boleh membekukan EDT.
     */
    private void sinkron() {
        final String awal = tgl(DTPCari1), akhir = tgl(DTPCari2);
        int jml = hitungSep(awal, akhir);
        if (jml == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada SEP pada " + awal + " s/d " + akhir + ".");
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Tarik ClaimResponse dari SATUSEHAT untuk " + jml + " SEP (" + awal + " s/d " + akhir + ")?\n"
                + "Satu permintaan per SEP, perkiraan waktu " + perkiraan(jml) + ".",
                "Sinkron ClaimResponse", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        setSibuk(true, "Menyinkronkan " + jml + " SEP ...");
        Progres.setIndeterminate(true);
        Progres.setString("memproses " + jml + " SEP");
        executor.submit(() -> {
            int ada = 0, total = 0;
            String pesan;
            try {
                SatuSehatClaimResponse api = new SatuSehatClaimResponse();
                List<SatuSehatClaimResponse.Hasil> hasil = api.sinkronPeriode(awal, akhir);
                total = hasil.size();
                for (SatuSehatClaimResponse.Hasil h : hasil) {
                    if (h != null && h.adaRespons()) ada++;
                }
                pesan = "Sinkron selesai: " + ada + " dari " + total + " SEP punya ClaimResponse.";
            } catch (Exception e) {
                pesan = "Sinkron gagal: " + e;
                System.out.println("Notifikasi Monitor Klaim sinkron : " + e);
            }
            final String akhirPesan = pesan;
            SwingUtilities.invokeLater(() -> {
                setSibuk(false, akhirPesan);
                tampilkan();
                JOptionPane.showMessageDialog(this, akhirPesan);
            });
        });
    }

    /**
     * Tarik hanya klaim yang DIBERITAKAN webhook (tabel satu_sehat_task_webhook).
     *
     * Beda dengan tombol Sinkron: yang itu menyapu semua SEP pada rentang tanggal —
     * satu GET per SEP, sebagian besar mubazir karena hasil purifikasi/verifikasi
     * datang berhari-hari kemudian dan tidak serentak. Yang ini hanya menyentuh klaim
     * yang server-nya bilang "ada kabar", jadi jumlah permintaannya sekecil kenyataan.
     *
     * Payload webhook TIDAK dipercaya sebagai status: yang disimpan tetap hasil GET.
     */
    private void sinkronWebhook() {
        final int menunggu;
        try {
            menunggu = new SatuSehatClaimResponse().jumlahSinyalMenunggu();
        } catch (Exception e) {
            System.out.println("Notifikasi Monitor Klaim hitung sinyal : " + e);
            JOptionPane.showMessageDialog(this, "Gagal membaca antrean webhook. Cek log aplikasi.");
            return;
        }
        if (menunggu == 0) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada sinyal webhook klaim yang menunggu.\n\n"
                    + "Bila hasil purifikasi/verifikasi sudah keluar tapi webhook tak pernah masuk,\n"
                    + "pakai tombol Sinkron ClaimResponse untuk menariknya per rentang tanggal SEP.");
            return;
        }
        setSibuk(true, "Menindaklanjuti " + menunggu + " sinyal webhook ...");
        Progres.setIndeterminate(true);
        Progres.setString(menunggu + " sinyal");
        executor.submit(() -> {
            String pesan;
            try {
                SatuSehatClaimResponse api = new SatuSehatClaimResponse();
                SatuSehatClaimResponse.HasilWebhook w = api.sinkronDariWebhook(MAKS_SINYAL_PER_PUTARAN);
                pesan = w.ringkas();
            } catch (Exception e) {
                pesan = "Sinkron webhook gagal: " + e;
                System.out.println("Notifikasi Monitor Klaim sinkronWebhook : " + e);
            }
            final String akhirPesan = pesan;
            SwingUtilities.invokeLater(() -> {
                setSibuk(false, akhirPesan);
                tampilkan();
                JOptionPane.showMessageDialog(this, akhirPesan);
            });
        });
    }

    /** Langkah 9: kirim PurificationDecision "Lanjut" untuk baris terpilih. */
    private void kirimKeputusan() {
        int row = barisTerpilih();
        if (row < 0) return;
        final String noRawat = tabMode.getValueAt(row, 0).toString();
        if (nz(tabMode.getValueAt(row, KOL_ST_PURIF) + "").trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Klaim " + noRawat + " belum punya hasil purifikasi.\n"
                    + "Jalankan Sinkron ClaimResponse (langkah 8) lebih dulu.");
            return;
        }
        String sudah = nz(tabMode.getValueAt(row, KOL_KEPUTUSAN) + "").trim();
        String tanya = "Kirim keputusan \"Lanjut\" (TK000049) untuk " + noRawat + "?"
                + (sudah.isEmpty() ? "" : "\n\nKeputusan sebelumnya: " + sudah + " — akan dikirim ulang.");
        if (JOptionPane.showConfirmDialog(this, tanya, "Tindak Lanjut Purifikasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        final boolean ulang = !sudah.isEmpty();
        setSibuk(true, "Mengirim keputusan " + noRawat + " ...");
        Progres.setIndeterminate(true);
        Progres.setString("mengirim");
        executor.submit(() -> {
            boolean ok;
            try {
                SatuSehatPurificationDecision api = new SatuSehatPurificationDecision();
                ok = ulang
                        ? api.kirimUlang(noRawat, SatuSehatPurificationDecision.KODE_LANJUT,
                                SatuSehatPurificationDecision.DISPLAY_LANJUT)
                        : api.kirimLanjut(noRawat);
            } catch (Exception e) {
                ok = false;
                System.out.println("Notifikasi Monitor Klaim keputusan : " + e);
            }
            final boolean hasil = ok;
            SwingUtilities.invokeLater(() -> {
                setSibuk(false, hasil ? "Keputusan terkirim." : "Keputusan GAGAL — cek log.");
                tampilkan();
                JOptionPane.showMessageDialog(this, hasil
                        ? "Keputusan terkirim untuk " + noRawat + "."
                        : "Gagal mengirim keputusan untuk " + noRawat + ". Cek log aplikasi.");
            });
        });
    }

    /** Balasan mentah SATUSEHAT — satu-satunya cara memastikan apa yang benar-benar diterima. */
    private void lihatDetail() {
        int row = barisTerpilih();
        if (row < 0) return;
        String noRawat = tabMode.getValueAt(row, 0).toString();
        StringBuilder sb = new StringBuilder();
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(raw_response_purifikasi,'') as p, ifnull(raw_response_verifikasi,'') as v "
                    + "from satu_sehat_claimresponse where no_rawat=? limit 1");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                sb.append("===== PURIFIKASI (langkah 8) =====\n")
                        .append(cantik(r.getString("p")))
                        .append("\n\n===== VERIFIKASI (langkah 10) =====\n")
                        .append(cantik(r.getString("v")));
            }
            r.close();
            p.close();
        } catch (Exception e) {
            sb.append("Gagal membaca: ").append(e);
        }
        javax.swing.JTextArea area = new javax.swing.JTextArea(sb.toString(), 26, 88);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", 0, 11));
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new javax.swing.JScrollPane(area),
                "Balasan SATUSEHAT — " + noRawat, JOptionPane.PLAIN_MESSAGE);
    }

    // ====================== UTIL ======================

    /** Index baris pada MODEL (bukan tampilan) — penting karena tabel bisa diurutkan. */
    private int barisTerpilih() {
        int lihat = tbKlaim.getSelectedRow();
        if (lihat < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris klaimnya.");
            return -1;
        }
        return tbKlaim.convertRowIndexToModel(lihat);
    }

    private void setSibuk(boolean sibuk, String status) {
        setCursor(Cursor.getPredefinedCursor(sibuk ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
        BtnTampil.setEnabled(!sibuk);
        BtnSinkron.setEnabled(!sibuk);
        BtnWebhook.setEnabled(!sibuk);
        BtnKeputusan.setEnabled(!sibuk);
        BtnDetail.setEnabled(!sibuk);
        Progres.setVisible(sibuk);
        if (!sibuk) Progres.setIndeterminate(false);
        LabelStatus.setText(status);
    }

    private int hitungSep(String awal, String akhir) {
        try {
            PreparedStatement p = koneksi.prepareStatement(
                    "select count(distinct no_rawat) as n from bridging_sep where tglsep between ? and ?");
            p.setString(1, awal);
            p.setString(2, akhir);
            ResultSet r = p.executeQuery();
            int n = r.next() ? r.getInt("n") : 0;
            r.close();
            p.close();
            return n;
        } catch (Exception e) {
            System.out.println("Notifikasi Monitor Klaim hitungSep : " + e);
            return 0;
        }
    }

    /** Perkiraan kasar: 1 permintaan + jeda 350 ms per SEP. */
    private String perkiraan(int jml) {
        long detik = Math.round(jml * 0.6);
        if (detik < 60) return "sekitar " + detik + " detik";
        return "sekitar " + (detik / 60) + " menit";
    }

    private String cantik(String json) {
        if (json == null || json.trim().isEmpty()) return "(belum ada)";
        try {
            com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
            return m.writerWithDefaultPrettyPrinter().writeValueAsString(m.readTree(json));
        } catch (Exception e) {
            return json;   // bukan JSON valid: tampilkan apa adanya
        }
    }

    /** yyyy-MM-dd dari widget tanggal; bebas locale (lihat catatan pada fmtTgl). */
    private String tgl(widget.Tanggal t) {
        java.util.Date d = t.getDate();
        return fmtTgl.format(d == null ? new java.util.Date() : d);
    }

    /** "2026-07-26 17:46:20.0" -> "2026-07-26 17:46" (kolom sempit). */
    private String potongJam(String s) {
        return s.length() >= 16 ? s.substring(0, 16) : s;
    }

    private String rp(double v) {
        return rupiah.format(v);
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    @Override
    public void dispose() {
        executor.shutdown();
        super.dispose();
    }
}
