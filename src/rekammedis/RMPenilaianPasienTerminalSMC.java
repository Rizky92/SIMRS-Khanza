package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import simrskhanza.DlgRujuk;
import surat.SuratPermintaanBinrohtal;
import surat.SuratPersetujuanPenundaanPelayanan;
import widget.CekBox;
import widget.ComboBox;

/**
 *
 * @author perpustakaan
 */
public final class RMPenilaianPasienTerminalSMC extends javax.swing.JDialog {
    private static final int KOLOM_SMC = 9;
    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final JComponent[] isian;
    private final String[] kolom;
    private volatile boolean ceksukses = false;
    private DlgCariPetugas petugas;
    private DlgCariDokter dokter;

    /**
     * Creates new form RMPenilaianPasienTerminalSMC
     *
     * @param parent
     * @param modal
     */
    public RMPenilaianPasienTerminalSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        isian = new JComponent[] {
            Ruangan, ChkDyspneu, ChkNafasCepatDangkal, ChkNafasTidakTeratur, ChkNafasMelaluiMulut,
            ChkAdaSekret, ChkSpO2Kurang90, ChkNafasLambat, ChkMukosaOralKering, ChkTonusMual, ChkDistendedAbdomen,
            ChkSulitMenelan, ChkSulitBicara, TonusLainnya, Nyeri, NyeriKeterangan, NyeriManajemen,
            ChkBercakSianosis, ChkTDMenurun, ChkGelisah, ChkLemas, ChkKulitDingin, ChkAktivitasFisik,
            ChkPindahPosisi, FaktorLainnya, ChkMasalahMual, ChkPolaNafas, ChkPersepsiSensori, ChkBersihanJalanNafas,
            ChkNyeriAkut, ChkDefisitPerawatanDiri, ChkKonstipasi, ChkNyeriKronis, PelayananSpiritual, PelayananSpiritualOleh,
            PerluDidoakan, PerluBimbinganRohani, PerluPendampinganRohani, Hubungi, HubungiNama, HubungiHubungan,
            HubungiAlamat, HubungiTelp, RencanaPerawatan, LingkunganSiap, MampuMerawat, MampuMerawatOleh,
            PerluHomeCare, ChkPasienMenyangkal, ChkPasienSedih, ChkPasienMarah, ChkPasienRasaBersalah, ChkPasienTakut,
            ChkPasienTidakBerdaya, ChkAnxietas, ChkDistresSpiritual, ChkKeluargaGangguanTidur, ChkKeluargaTidakMampuPeran, ChkKeluargaMarah,
            ChkKeluargaKurangKomunikasi, ChkKeluargaLetih, ChkKeluargaRasaBersalah, BantuanPenundaan, BantuanPenundaanAlasan, LayananAlternatif,
            LayananAlternatifTujuan, ChkDapatMenerima, ChkTidakMenerima, ChkPasrah, RisikoLainnya, Rekomendasi
        };

        kolom = new String[] {
            "ruangan", "gejala_dyspneu", "gejala_nafas_cepat_dangkal",
            "gejala_nafas_tidak_teratur", "gejala_nafas_melalui_mulut", "gejala_ada_sekret", "gejala_spo2_kurang_90",
            "gejala_nafas_lambat", "gejala_mukosa_oral_kering", "tonus_mual", "tonus_distended_abdomen",
            "tonus_sulit_menelan", "tonus_sulit_bicara", "tonus_lainnya", "nyeri",
            "nyeri_keterangan", "nyeri_manajemen", "sirkulasi_bercak_sianosis", "sirkulasi_td_menurun",
            "sirkulasi_gelisah", "sirkulasi_lemas", "sirkulasi_kulit_dingin", "faktor_aktivitas_fisik",
            "faktor_pindah_posisi", "faktor_lainnya", "masalah_mual", "masalah_pola_nafas",
            "masalah_persepsi_sensori", "masalah_bersihan_jalan_nafas", "masalah_nyeri_akut", "masalah_defisit_perawatan_diri",
            "masalah_konstipasi", "masalah_nyeri_kronis", "pelayanan_spiritual", "pelayanan_spiritual_oleh",
            "perlu_didoakan", "perlu_bimbingan_rohani", "perlu_pendampingan_rohani", "hubungi",
            "hubungi_nama", "hubungi_hubungan", "hubungi_alamat", "hubungi_telp",
            "rencana_perawatan", "lingkungan_siap", "mampu_merawat", "mampu_merawat_oleh",
            "perlu_home_care", "reaksi_pasien_menyangkal", "reaksi_pasien_sedih", "reaksi_pasien_marah",
            "reaksi_pasien_rasa_bersalah", "reaksi_pasien_takut", "reaksi_pasien_tidak_berdaya", "masalah_anxietas",
            "masalah_distres_spiritual", "reaksi_keluarga_gangguan_tidur", "reaksi_keluarga_tidak_mampu_peran", "reaksi_keluarga_marah",
            "reaksi_keluarga_kurang_komunikasi", "reaksi_keluarga_letih", "reaksi_keluarga_rasa_bersalah", "bantuan_penundaan",
            "bantuan_penundaan_alasan", "layanan_alternatif", "layanan_alternatif_tujuan", "risiko_dapat_menerima",
            "risiko_tidak_menerima", "risiko_pasrah", "risiko_lainnya", "rekomendasi"
        };

        tabMode = new DefaultTableModel(null, new Object[] {
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Skala Nyeri", "NIP", "Petugas", "Ruangan", "Kode Dokter", "Nama Dokter",
            "Dyspneu", "Nafas cepat dan dangkal", "Nafas tidak teratur", "Nafas melalui mulut", "Ada sekret",
            "SpO2 < 90 %", "Nafas lambat", "Mukosa oral kering", "Mual", "Distended abdomen",
            "Sulit menelan", "Sulit bicara", "Tonus Otot Lainnya", "Nyeri", "Ket. Nyeri",
            "Manajemen Nyeri", "Bercak dan sianosis pada ekstremitas", "TD menurun, disertai nadi lambat dan lemah", "Gelisah", "Lemas",
            "Kulit dingin dan berkeringat", "Melakukan aktivitas fisik", "Pindah posisi", "Faktor Lainnya", "Mual",
            "Pola nafas tidak efektif", "Perubahan persepsi sensori", "Bersihan jalan nafas tidak efektif", "Nyeri akut", "Defisit perawatan diri",
            "Konstipasi", "Nyeri kronis", "Perlu Pelayanan Spiritual", "Pelayanan Spiritual Oleh", "Perlu Didoakan",
            "Perlu Bimbingan Rohani", "Perlu Pendampingan Rohani", "Ada Yang Ingin Dihubungi", "Nama Yang Dihubungi", "Hubungan Dengan Pasien",
            "Alamat Yang Dihubungi", "No.HP/Telp", "Rencana Perawatan", "Lingkungan Rumah Siap", "Mampu Merawat di Rumah",
            "Dirawat Oleh", "Perlu Home Care", "Menyangkal", "Sedih", "Marah",
            "Rasa bersalah", "Takut", "Tidak berdaya", "Anxietas", "Distres spiritual",
            "Gangguan tidur", "Ketidakmampuan memenuhi peran yang diharapkan", "Marah", "Keluarga kurang berkomunikasi dengan pasien", "Letih",
            "Rasa bersalah", "Perlu Bantuan/Penundaan Pelayanan", "Alasan Bantuan/Penundaan", "Perlu Layanan Alternatif (Rujuk)", "Tujuan Rujuk",
            "Dapat menerima", "Tidak menerima", "Pasrah", "Faktor Risiko Lainnya", "Rekomendasi Hasil Asesmen"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(65);
            } else if (i == 2) {
                column.setPreferredWidth(160);
            } else if (i == 3) {
                column.setPreferredWidth(65);
            } else if (i == 4) {
                column.setPreferredWidth(25);
            } else if (i == 5) {
                column.setPreferredWidth(124);
            } else if (i == 6) {
                column.setPreferredWidth(60);
            } else if (i == 7 || i == 10) {
                column.setPreferredWidth(85);
            } else if (i == 8 || i == 11) {
                column.setPreferredWidth(150);
            } else {
                column.setPreferredWidth(110);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
        Ruangan.setDocument(new batasInput((int) 100).getKata(Ruangan));
        TonusLainnya.setDocument(new batasInput((int) 100).getKata(TonusLainnya));
        NyeriKeterangan.setDocument(new batasInput((int) 100).getKata(NyeriKeterangan));
        NyeriManajemen.setDocument(new batasInput((int) 200).getKata(NyeriManajemen));
        FaktorLainnya.setDocument(new batasInput((int) 100).getKata(FaktorLainnya));
        PelayananSpiritualOleh.setDocument(new batasInput((int) 100).getKata(PelayananSpiritualOleh));
        HubungiNama.setDocument(new batasInput((int) 50).getKata(HubungiNama));
        HubungiHubungan.setDocument(new batasInput((int) 30).getKata(HubungiHubungan));
        HubungiAlamat.setDocument(new batasInput((int) 100).getKata(HubungiAlamat));
        HubungiTelp.setDocument(new batasInput((int) 20).getKata(HubungiTelp));
        MampuMerawatOleh.setDocument(new batasInput((int) 50).getKata(MampuMerawatOleh));
        BantuanPenundaanAlasan.setDocument(new batasInput((int) 200).getKata(BantuanPenundaanAlasan));
        LayananAlternatifTujuan.setDocument(new batasInput((int) 100).getKata(LayananAlternatifTujuan));
        RisikoLainnya.setDocument(new batasInput((int) 100).getKata(RisikoLainnya));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        jam();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnPenilaianPasienTerminal = new javax.swing.JMenuItem();
        MnPenilaianUlangNyeri = new javax.swing.JMenuItem();
        MnPermintaanBinrohtal = new javax.swing.JMenuItem();
        MnPersetujuanPenundaanPelayanan = new javax.swing.JMenuItem();
        MnRujuk = new javax.swing.JMenuItem();
        JK = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel16 = new widget.Label();
        Tanggal = new widget.Tanggal();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel102 = new widget.Label();
        Ruangan = new widget.TextBox();
        jLabel18 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        BtnPetugas = new widget.Button();
        jLabel103 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel106 = new widget.Label();
        jLabel107 = new widget.Label();
        ChkDyspneu = new widget.CekBox();
        ChkNafasCepatDangkal = new widget.CekBox();
        ChkNafasTidakTeratur = new widget.CekBox();
        ChkNafasMelaluiMulut = new widget.CekBox();
        ChkAdaSekret = new widget.CekBox();
        ChkSpO2Kurang90 = new widget.CekBox();
        ChkNafasLambat = new widget.CekBox();
        ChkMukosaOralKering = new widget.CekBox();
        jLabel108 = new widget.Label();
        ChkTonusMual = new widget.CekBox();
        ChkDistendedAbdomen = new widget.CekBox();
        ChkSulitMenelan = new widget.CekBox();
        ChkSulitBicara = new widget.CekBox();
        jLabel109 = new widget.Label();
        TonusLainnya = new widget.TextBox();
        jLabel110 = new widget.Label();
        Nyeri = new widget.ComboBox();
        jLabel111 = new widget.Label();
        NyeriKeterangan = new widget.TextBox();
        jLabel112 = new widget.Label();
        SkalaNyeri = new widget.ComboBox();
        jLabel113 = new widget.Label();
        NyeriManajemen = new widget.TextBox();
        jLabel114 = new widget.Label();
        ChkBercakSianosis = new widget.CekBox();
        ChkTDMenurun = new widget.CekBox();
        ChkGelisah = new widget.CekBox();
        ChkLemas = new widget.CekBox();
        ChkKulitDingin = new widget.CekBox();
        jLabel115 = new widget.Label();
        ChkAktivitasFisik = new widget.CekBox();
        ChkPindahPosisi = new widget.CekBox();
        jLabel116 = new widget.Label();
        FaktorLainnya = new widget.TextBox();
        jLabel117 = new widget.Label();
        jLabel118 = new widget.Label();
        ChkMasalahMual = new widget.CekBox();
        ChkPolaNafas = new widget.CekBox();
        ChkPersepsiSensori = new widget.CekBox();
        ChkBersihanJalanNafas = new widget.CekBox();
        ChkNyeriAkut = new widget.CekBox();
        ChkDefisitPerawatanDiri = new widget.CekBox();
        ChkKonstipasi = new widget.CekBox();
        ChkNyeriKronis = new widget.CekBox();
        jLabel119 = new widget.Label();
        jLabel120 = new widget.Label();
        PelayananSpiritual = new widget.ComboBox();
        jLabel121 = new widget.Label();
        PelayananSpiritualOleh = new widget.TextBox();
        jLabel122 = new widget.Label();
        jLabel123 = new widget.Label();
        PerluDidoakan = new widget.ComboBox();
        jLabel124 = new widget.Label();
        PerluBimbinganRohani = new widget.ComboBox();
        jLabel125 = new widget.Label();
        PerluPendampinganRohani = new widget.ComboBox();
        jLabel126 = new widget.Label();
        jLabel127 = new widget.Label();
        Hubungi = new widget.ComboBox();
        jLabel128 = new widget.Label();
        HubungiNama = new widget.TextBox();
        jLabel129 = new widget.Label();
        HubungiHubungan = new widget.TextBox();
        jLabel130 = new widget.Label();
        HubungiTelp = new widget.TextBox();
        jLabel131 = new widget.Label();
        HubungiAlamat = new widget.TextBox();
        jLabel132 = new widget.Label();
        RencanaPerawatan = new widget.ComboBox();
        jLabel133 = new widget.Label();
        LingkunganSiap = new widget.ComboBox();
        jLabel134 = new widget.Label();
        MampuMerawat = new widget.ComboBox();
        jLabel135 = new widget.Label();
        MampuMerawatOleh = new widget.TextBox();
        jLabel136 = new widget.Label();
        PerluHomeCare = new widget.ComboBox();
        jLabel137 = new widget.Label();
        ChkPasienMenyangkal = new widget.CekBox();
        ChkPasienSedih = new widget.CekBox();
        ChkPasienMarah = new widget.CekBox();
        ChkPasienRasaBersalah = new widget.CekBox();
        ChkPasienTakut = new widget.CekBox();
        ChkPasienTidakBerdaya = new widget.CekBox();
        jLabel138 = new widget.Label();
        ChkAnxietas = new widget.CekBox();
        ChkDistresSpiritual = new widget.CekBox();
        jLabel139 = new widget.Label();
        ChkKeluargaGangguanTidur = new widget.CekBox();
        ChkKeluargaTidakMampuPeran = new widget.CekBox();
        ChkKeluargaMarah = new widget.CekBox();
        ChkKeluargaKurangKomunikasi = new widget.CekBox();
        ChkKeluargaLetih = new widget.CekBox();
        ChkKeluargaRasaBersalah = new widget.CekBox();
        jLabel140 = new widget.Label();
        BantuanPenundaan = new widget.ComboBox();
        jLabel141 = new widget.Label();
        BantuanPenundaanAlasan = new widget.TextBox();
        jLabel142 = new widget.Label();
        LayananAlternatif = new widget.ComboBox();
        jLabel143 = new widget.Label();
        LayananAlternatifTujuan = new widget.TextBox();
        jLabel144 = new widget.Label();
        ChkDapatMenerima = new widget.CekBox();
        ChkTidakMenerima = new widget.CekBox();
        ChkPasrah = new widget.CekBox();
        jLabel145 = new widget.Label();
        RisikoLainnya = new widget.TextBox();
        jLabel146 = new widget.Label();
        scrollPane25 = new widget.ScrollPane();
        Rekomendasi = new widget.TextArea();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnPenilaianPasienTerminal.setBackground(new java.awt.Color(255, 255, 254));
        MnPenilaianPasienTerminal.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPenilaianPasienTerminal.setForeground(new java.awt.Color(50, 50, 50));
        MnPenilaianPasienTerminal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPenilaianPasienTerminal.setText("Formulir Asesmen Pasien Terminal");
        MnPenilaianPasienTerminal.setName("MnPenilaianPasienTerminal"); // NOI18N
        MnPenilaianPasienTerminal.setPreferredSize(new java.awt.Dimension(290, 26));
        MnPenilaianPasienTerminal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPenilaianPasienTerminalActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPenilaianPasienTerminal);

        MnPenilaianUlangNyeri.setBackground(new java.awt.Color(255, 255, 254));
        MnPenilaianUlangNyeri.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPenilaianUlangNyeri.setForeground(new java.awt.Color(50, 50, 50));
        MnPenilaianUlangNyeri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPenilaianUlangNyeri.setText("Pengkajian Ulang Nyeri");
        MnPenilaianUlangNyeri.setName("MnPenilaianUlangNyeri"); // NOI18N
        MnPenilaianUlangNyeri.setPreferredSize(new java.awt.Dimension(290, 26));
        MnPenilaianUlangNyeri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPenilaianUlangNyeriActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPenilaianUlangNyeri);

        MnPermintaanBinrohtal.setBackground(new java.awt.Color(255, 255, 254));
        MnPermintaanBinrohtal.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPermintaanBinrohtal.setForeground(new java.awt.Color(50, 50, 50));
        MnPermintaanBinrohtal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPermintaanBinrohtal.setText("Permintaan Bimbingan Rohani & Mental");
        MnPermintaanBinrohtal.setName("MnPermintaanBinrohtal"); // NOI18N
        MnPermintaanBinrohtal.setPreferredSize(new java.awt.Dimension(290, 26));
        MnPermintaanBinrohtal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPermintaanBinrohtalActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPermintaanBinrohtal);

        MnPersetujuanPenundaanPelayanan.setBackground(new java.awt.Color(255, 255, 254));
        MnPersetujuanPenundaanPelayanan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPersetujuanPenundaanPelayanan.setForeground(new java.awt.Color(50, 50, 50));
        MnPersetujuanPenundaanPelayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPersetujuanPenundaanPelayanan.setText("Persetujuan Penundaan Pelayanan");
        MnPersetujuanPenundaanPelayanan.setName("MnPersetujuanPenundaanPelayanan"); // NOI18N
        MnPersetujuanPenundaanPelayanan.setPreferredSize(new java.awt.Dimension(290, 26));
        MnPersetujuanPenundaanPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPersetujuanPenundaanPelayananActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPersetujuanPenundaanPelayanan);

        MnRujuk.setBackground(new java.awt.Color(255, 255, 254));
        MnRujuk.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnRujuk.setForeground(new java.awt.Color(50, 50, 50));
        MnRujuk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnRujuk.setText("Rujukan Keluar");
        MnRujuk.setName("MnRujuk"); // NOI18N
        MnRujuk.setPreferredSize(new java.awt.Dimension(290, 26));
        MnRujuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnRujukActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnRujuk);

        JK.setName("JK"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Asesmen Awal dan Ulang Pasien Terminal dan Keluarganya ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
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
        panelGlass8.add(BtnBatal);

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
        panelGlass8.add(BtnHapus);

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
        panelGlass8.add(BtnEdit);

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
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
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

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.setPreferredSize(new java.awt.Dimension(457, 480));

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setPreferredSize(new java.awt.Dimension(102, 480));
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(810, 1783));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 80, 23);

        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 136, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 80, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-10-2026" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setOpaque(false);
        Tanggal.setName("Tanggal"); // NOI18N
        FormInput.add(Tanggal);
        Tanggal.setBounds(84, 40, 90, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        FormInput.add(Jam);
        Jam.setBounds(178, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        FormInput.add(Menit);
        Menit.setBounds(243, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        FormInput.add(Detik);
        Detik.setBounds(308, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(373, 40, 23, 23);

        jLabel102.setText("Ruangan :");
        jLabel102.setName("jLabel102"); // NOI18N
        FormInput.add(jLabel102);
        jLabel102.setBounds(400, 40, 70, 23);

        Ruangan.setName("Ruangan"); // NOI18N
        FormInput.add(Ruangan);
        Ruangan.setBounds(474, 40, 315, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(0, 70, 80, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setName("KdPetugas"); // NOI18N
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasKeyPressed(evt);
            }
        });
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(84, 70, 94, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(180, 70, 187, 23);

        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPetugas.setMnemonic('2');
        BtnPetugas.setToolTipText("Alt+2");
        BtnPetugas.setName("BtnPetugas"); // NOI18N
        BtnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPetugasActionPerformed(evt);
            }
        });
        FormInput.add(BtnPetugas);
        BtnPetugas.setBounds(370, 70, 28, 23);

        jLabel103.setText("Dokter :");
        jLabel103.setName("jLabel103"); // NOI18N
        FormInput.add(jLabel103);
        jLabel103.setBounds(400, 70, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter"); // NOI18N
        FormInput.add(KdDokter);
        KdDokter.setBounds(474, 70, 94, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        FormInput.add(NmDokter);
        NmDokter.setBounds(570, 70, 187, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('3');
        BtnDokter.setToolTipText("Alt+3");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(761, 70, 28, 23);

        jSeparator11.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator11.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator11.setName("jSeparator11"); // NOI18N
        FormInput.add(jSeparator11);
        jSeparator11.setBounds(0, 100, 810, 1);

        jLabel106.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel106.setText("1. Gejala seperti muntah dan kesulitan bernafas & nyeri :");
        jLabel106.setName("jLabel106"); // NOI18N
        FormInput.add(jLabel106);
        jLabel106.setBounds(10, 100, 500, 23);

        jLabel107.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel107.setText("1.1 Kegawatan pernafasan :");
        jLabel107.setName("jLabel107"); // NOI18N
        FormInput.add(jLabel107);
        jLabel107.setBounds(30, 130, 400, 23);

        ChkDyspneu.setText("Dyspneu");
        ChkDyspneu.setOpaque(false);
        ChkDyspneu.setName("ChkDyspneu"); // NOI18N
        FormInput.add(ChkDyspneu);
        ChkDyspneu.setBounds(50, 160, 360, 23);

        ChkNafasCepatDangkal.setText("Nafas cepat dan dangkal");
        ChkNafasCepatDangkal.setName("ChkNafasCepatDangkal"); // NOI18N
        ChkNafasCepatDangkal.setOpaque(false);
        FormInput.add(ChkNafasCepatDangkal);
        ChkNafasCepatDangkal.setBounds(430, 160, 360, 23);

        ChkNafasTidakTeratur.setText("Nafas tidak teratur");
        ChkNafasTidakTeratur.setOpaque(false);
        ChkNafasTidakTeratur.setName("ChkNafasTidakTeratur"); // NOI18N
        FormInput.add(ChkNafasTidakTeratur);
        ChkNafasTidakTeratur.setBounds(50, 190, 360, 23);

        ChkNafasMelaluiMulut.setText("Nafas melalui mulut");
        ChkNafasMelaluiMulut.setOpaque(false);
        ChkNafasMelaluiMulut.setName("ChkNafasMelaluiMulut"); // NOI18N
        FormInput.add(ChkNafasMelaluiMulut);
        ChkNafasMelaluiMulut.setBounds(430, 190, 360, 23);

        ChkAdaSekret.setText("Ada sekret");
        ChkAdaSekret.setOpaque(false);
        ChkAdaSekret.setName("ChkAdaSekret"); // NOI18N
        FormInput.add(ChkAdaSekret);
        ChkAdaSekret.setBounds(50, 220, 360, 23);

        ChkSpO2Kurang90.setText("SpO2 < 90 %");
        ChkSpO2Kurang90.setOpaque(false);
        ChkSpO2Kurang90.setName("ChkSpO2Kurang90"); // NOI18N
        FormInput.add(ChkSpO2Kurang90);
        ChkSpO2Kurang90.setBounds(430, 220, 360, 23);

        ChkNafasLambat.setText("Nafas lambat");
        ChkNafasLambat.setOpaque(false);
        ChkNafasLambat.setName("ChkNafasLambat"); // NOI18N
        FormInput.add(ChkNafasLambat);
        ChkNafasLambat.setBounds(50, 250, 360, 23);

        ChkMukosaOralKering.setText("Mukosa oral kering");
        ChkMukosaOralKering.setOpaque(false);
        ChkMukosaOralKering.setName("ChkMukosaOralKering"); // NOI18N
        FormInput.add(ChkMukosaOralKering);
        ChkMukosaOralKering.setBounds(430, 250, 360, 23);

        jLabel108.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel108.setText("1.2 Kehilangan tonus otot :");
        jLabel108.setName("jLabel108"); // NOI18N
        FormInput.add(jLabel108);
        jLabel108.setBounds(30, 280, 400, 23);

        ChkTonusMual.setText("Mual");
        ChkTonusMual.setOpaque(false);
        ChkTonusMual.setName("ChkTonusMual"); // NOI18N
        FormInput.add(ChkTonusMual);
        ChkTonusMual.setBounds(50, 310, 360, 23);

        ChkDistendedAbdomen.setText("Distended abdomen");
        ChkDistendedAbdomen.setOpaque(false);
        ChkDistendedAbdomen.setName("ChkDistendedAbdomen"); // NOI18N
        FormInput.add(ChkDistendedAbdomen);
        ChkDistendedAbdomen.setBounds(430, 310, 360, 23);

        ChkSulitMenelan.setText("Sulit menelan");
        ChkSulitMenelan.setOpaque(false);
        ChkSulitMenelan.setName("ChkSulitMenelan"); // NOI18N
        FormInput.add(ChkSulitMenelan);
        ChkSulitMenelan.setBounds(50, 340, 360, 23);

        ChkSulitBicara.setText("Sulit bicara");
        ChkSulitBicara.setOpaque(false);
        ChkSulitBicara.setName("ChkSulitBicara"); // NOI18N
        FormInput.add(ChkSulitBicara);
        ChkSulitBicara.setBounds(430, 340, 360, 23);

        jLabel109.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel109.setText("Lainnya :");
        jLabel109.setName("jLabel109"); // NOI18N
        FormInput.add(jLabel109);
        jLabel109.setBounds(50, 370, 60, 23);

        TonusLainnya.setName("TonusLainnya"); // NOI18N
        FormInput.add(TonusLainnya);
        TonusLainnya.setBounds(114, 370, 676, 23);

        jLabel110.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel110.setText("1.3 Nyeri :");
        jLabel110.setName("jLabel110"); // NOI18N
        FormInput.add(jLabel110);
        jLabel110.setBounds(30, 400, 400, 23);

        Nyeri.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Nyeri.setName("Nyeri"); // NOI18N
        FormInput.add(Nyeri);
        Nyeri.setBounds(50, 430, 80, 23);

        jLabel111.setText("Keterangan :");
        jLabel111.setName("jLabel111"); // NOI18N
        FormInput.add(jLabel111);
        jLabel111.setBounds(134, 430, 70, 23);

        NyeriKeterangan.setName("NyeriKeterangan"); // NOI18N
        FormInput.add(NyeriKeterangan);
        NyeriKeterangan.setBounds(208, 430, 200, 23);

        jLabel112.setText("Skala :");
        jLabel112.setName("jLabel112"); // NOI18N
        FormInput.add(jLabel112);
        jLabel112.setBounds(412, 430, 50, 23);

        SkalaNyeri.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        SkalaNyeri.setName("SkalaNyeri"); // NOI18N
        FormInput.add(SkalaNyeri);
        SkalaNyeri.setBounds(466, 430, 60, 23);

        jLabel113.setText("Manajemen :");
        jLabel113.setName("jLabel113"); // NOI18N
        FormInput.add(jLabel113);
        jLabel113.setBounds(530, 430, 70, 23);

        NyeriManajemen.setName("NyeriManajemen"); // NOI18N
        FormInput.add(NyeriManajemen);
        NyeriManajemen.setBounds(604, 430, 186, 23);

        jLabel114.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel114.setText("1.4 Perlambatan sirkulasi :");
        jLabel114.setName("jLabel114"); // NOI18N
        FormInput.add(jLabel114);
        jLabel114.setBounds(30, 460, 400, 23);

        ChkBercakSianosis.setText("Bercak dan sianosis pada ekstremitas");
        ChkBercakSianosis.setOpaque(false);
        ChkBercakSianosis.setName("ChkBercakSianosis"); // NOI18N
        FormInput.add(ChkBercakSianosis);
        ChkBercakSianosis.setBounds(50, 490, 360, 23);

        ChkTDMenurun.setText("TD menurun, disertai nadi lambat dan lemah");
        ChkTDMenurun.setOpaque(false);
        ChkTDMenurun.setName("ChkTDMenurun"); // NOI18N
        FormInput.add(ChkTDMenurun);
        ChkTDMenurun.setBounds(430, 490, 360, 23);

        ChkGelisah.setText("Gelisah");
        ChkGelisah.setOpaque(false);
        ChkGelisah.setName("ChkGelisah"); // NOI18N
        FormInput.add(ChkGelisah);
        ChkGelisah.setBounds(50, 520, 360, 23);

        ChkLemas.setText("Lemas");
        ChkLemas.setOpaque(false);
        ChkLemas.setName("ChkLemas"); // NOI18N
        FormInput.add(ChkLemas);
        ChkLemas.setBounds(430, 520, 360, 23);

        ChkKulitDingin.setText("Kulit dingin dan berkeringat");
        ChkKulitDingin.setOpaque(false);
        ChkKulitDingin.setName("ChkKulitDingin"); // NOI18N
        FormInput.add(ChkKulitDingin);
        ChkKulitDingin.setBounds(50, 550, 360, 23);

        jLabel115.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel115.setText("2. Faktor yang meningkatkan dan membangkitkan gejala fisik :");
        jLabel115.setName("jLabel115"); // NOI18N
        FormInput.add(jLabel115);
        jLabel115.setBounds(10, 580, 500, 23);

        ChkAktivitasFisik.setText("Melakukan aktivitas fisik");
        ChkAktivitasFisik.setOpaque(false);
        ChkAktivitasFisik.setName("ChkAktivitasFisik"); // NOI18N
        FormInput.add(ChkAktivitasFisik);
        ChkAktivitasFisik.setBounds(50, 610, 360, 23);

        ChkPindahPosisi.setText("Pindah posisi");
        ChkPindahPosisi.setOpaque(false);
        ChkPindahPosisi.setName("ChkPindahPosisi"); // NOI18N
        FormInput.add(ChkPindahPosisi);
        ChkPindahPosisi.setBounds(430, 610, 360, 23);

        jLabel116.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel116.setText("Lainnya :");
        jLabel116.setName("jLabel116"); // NOI18N
        FormInput.add(jLabel116);
        jLabel116.setBounds(50, 640, 60, 23);

        FaktorLainnya.setName("FaktorLainnya"); // NOI18N
        FormInput.add(FaktorLainnya);
        FaktorLainnya.setBounds(114, 640, 676, 23);

        jLabel117.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel117.setText("3. Manajemen gejala saat ini dan respons pasien :");
        jLabel117.setName("jLabel117"); // NOI18N
        FormInput.add(jLabel117);
        jLabel117.setBounds(10, 670, 500, 23);

        jLabel118.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel118.setText("3.1 Masalah Keperawatan :");
        jLabel118.setName("jLabel118"); // NOI18N
        FormInput.add(jLabel118);
        jLabel118.setBounds(30, 700, 400, 23);

        ChkMasalahMual.setText("Mual");
        ChkMasalahMual.setOpaque(false);
        ChkMasalahMual.setName("ChkMasalahMual"); // NOI18N
        FormInput.add(ChkMasalahMual);
        ChkMasalahMual.setBounds(50, 730, 360, 23);

        ChkPolaNafas.setText("Pola nafas tidak efektif");
        ChkPolaNafas.setOpaque(false);
        ChkPolaNafas.setName("ChkPolaNafas"); // NOI18N
        FormInput.add(ChkPolaNafas);
        ChkPolaNafas.setBounds(430, 730, 360, 23);

        ChkPersepsiSensori.setText("Perubahan persepsi sensori");
        ChkPersepsiSensori.setOpaque(false);
        ChkPersepsiSensori.setName("ChkPersepsiSensori"); // NOI18N
        FormInput.add(ChkPersepsiSensori);
        ChkPersepsiSensori.setBounds(50, 760, 360, 23);

        ChkBersihanJalanNafas.setText("Bersihan jalan nafas tidak efektif");
        ChkBersihanJalanNafas.setOpaque(false);
        ChkBersihanJalanNafas.setName("ChkBersihanJalanNafas"); // NOI18N
        FormInput.add(ChkBersihanJalanNafas);
        ChkBersihanJalanNafas.setBounds(430, 760, 360, 23);

        ChkNyeriAkut.setText("Nyeri akut");
        ChkNyeriAkut.setOpaque(false);
        ChkNyeriAkut.setName("ChkNyeriAkut"); // NOI18N
        FormInput.add(ChkNyeriAkut);
        ChkNyeriAkut.setBounds(50, 790, 360, 23);

        ChkDefisitPerawatanDiri.setText("Defisit perawatan diri");
        ChkDefisitPerawatanDiri.setOpaque(false);
        ChkDefisitPerawatanDiri.setName("ChkDefisitPerawatanDiri"); // NOI18N
        FormInput.add(ChkDefisitPerawatanDiri);
        ChkDefisitPerawatanDiri.setBounds(430, 790, 360, 23);

        ChkKonstipasi.setText("Konstipasi");
        ChkKonstipasi.setOpaque(false);
        ChkKonstipasi.setName("ChkKonstipasi"); // NOI18N
        FormInput.add(ChkKonstipasi);
        ChkKonstipasi.setBounds(50, 820, 360, 23);

        ChkNyeriKronis.setText("Nyeri kronis");
        ChkNyeriKronis.setOpaque(false);
        ChkNyeriKronis.setName("ChkNyeriKronis"); // NOI18N
        FormInput.add(ChkNyeriKronis);
        ChkNyeriKronis.setBounds(430, 820, 360, 23);

        jLabel119.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel119.setText("4. Orientasi spiritual pasien dan keluarga :");
        jLabel119.setName("jLabel119"); // NOI18N
        FormInput.add(jLabel119);
        jLabel119.setBounds(10, 850, 500, 23);

        jLabel120.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel120.setText("4.1 Apakah perlu pelayanan spiritual :");
        jLabel120.setName("jLabel120"); // NOI18N
        FormInput.add(jLabel120);
        jLabel120.setBounds(30, 880, 250, 23);

        PelayananSpiritual.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        PelayananSpiritual.setName("PelayananSpiritual"); // NOI18N
        FormInput.add(PelayananSpiritual);
        PelayananSpiritual.setBounds(284, 880, 80, 23);

        jLabel121.setText("Oleh :");
        jLabel121.setName("jLabel121"); // NOI18N
        FormInput.add(jLabel121);
        jLabel121.setBounds(368, 880, 50, 23);

        PelayananSpiritualOleh.setName("PelayananSpiritualOleh"); // NOI18N
        FormInput.add(PelayananSpiritualOleh);
        PelayananSpiritualOleh.setBounds(422, 880, 368, 23);

        jLabel122.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel122.setText("5. Urusan dan kebutuhan spiritual pasien dan keluarga seperti putus asa, penderitaan, rasa bersalah atau pengampunan :");
        jLabel122.setName("jLabel122"); // NOI18N
        FormInput.add(jLabel122);
        jLabel122.setBounds(10, 910, 780, 23);

        jLabel123.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel123.setText("5.1 Perlu didoakan :");
        jLabel123.setName("jLabel123"); // NOI18N
        FormInput.add(jLabel123);
        jLabel123.setBounds(30, 940, 140, 23);

        PerluDidoakan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        PerluDidoakan.setName("PerluDidoakan"); // NOI18N
        FormInput.add(PerluDidoakan);
        PerluDidoakan.setBounds(174, 940, 70, 23);

        jLabel124.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel124.setText("5.2 Perlu bimbingan rohani :");
        jLabel124.setName("jLabel124"); // NOI18N
        FormInput.add(jLabel124);
        jLabel124.setBounds(270, 940, 165, 23);

        PerluBimbinganRohani.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        PerluBimbinganRohani.setName("PerluBimbinganRohani"); // NOI18N
        FormInput.add(PerluBimbinganRohani);
        PerluBimbinganRohani.setBounds(439, 940, 70, 23);

        jLabel125.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel125.setText("5.3 Perlu pendampingan rohani :");
        jLabel125.setName("jLabel125"); // NOI18N
        FormInput.add(jLabel125);
        jLabel125.setBounds(535, 940, 185, 23);

        PerluPendampinganRohani.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        PerluPendampinganRohani.setName("PerluPendampinganRohani"); // NOI18N
        FormInput.add(PerluPendampinganRohani);
        PerluPendampinganRohani.setBounds(720, 940, 70, 23);

        jLabel126.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel126.setText("6. Status psikososial dan keluarga :");
        jLabel126.setName("jLabel126"); // NOI18N
        FormInput.add(jLabel126);
        jLabel126.setBounds(10, 970, 500, 23);

        jLabel127.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel127.setText("6.1 Apakah ada orang yang ingin dihubungi saat ini :");
        jLabel127.setName("jLabel127"); // NOI18N
        FormInput.add(jLabel127);
        jLabel127.setBounds(30, 1000, 290, 23);

        Hubungi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Hubungi.setName("Hubungi"); // NOI18N
        FormInput.add(Hubungi);
        Hubungi.setBounds(324, 1000, 80, 23);

        jLabel128.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel128.setText("Siapa :");
        jLabel128.setName("jLabel128"); // NOI18N
        FormInput.add(jLabel128);
        jLabel128.setBounds(50, 1030, 50, 23);

        HubungiNama.setName("HubungiNama"); // NOI18N
        FormInput.add(HubungiNama);
        HubungiNama.setBounds(104, 1030, 180, 23);

        jLabel129.setText("Hubungan dengan pasien :");
        jLabel129.setName("jLabel129"); // NOI18N
        FormInput.add(jLabel129);
        jLabel129.setBounds(288, 1030, 160, 23);

        HubungiHubungan.setName("HubungiHubungan"); // NOI18N
        FormInput.add(HubungiHubungan);
        HubungiHubungan.setBounds(452, 1030, 120, 23);

        jLabel130.setText("No.HP/Telp :");
        jLabel130.setName("jLabel130"); // NOI18N
        FormInput.add(jLabel130);
        jLabel130.setBounds(576, 1030, 70, 23);

        HubungiTelp.setName("HubungiTelp"); // NOI18N
        FormInput.add(HubungiTelp);
        HubungiTelp.setBounds(650, 1030, 140, 23);

        jLabel131.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel131.setText("Di mana :");
        jLabel131.setName("jLabel131"); // NOI18N
        FormInput.add(jLabel131);
        jLabel131.setBounds(50, 1060, 50, 23);

        HubungiAlamat.setName("HubungiAlamat"); // NOI18N
        FormInput.add(HubungiAlamat);
        HubungiAlamat.setBounds(104, 1060, 686, 23);

        jLabel132.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel132.setText("6.2 Bagaimana rencana perawatan selanjutnya :");
        jLabel132.setName("jLabel132"); // NOI18N
        FormInput.add(jLabel132);
        jLabel132.setBounds(30, 1090, 260, 23);

        RencanaPerawatan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tetap Dirawat di Rumah Sakit", "Dirawat di Rumah" }));
        RencanaPerawatan.setName("RencanaPerawatan"); // NOI18N
        FormInput.add(RencanaPerawatan);
        RencanaPerawatan.setBounds(294, 1090, 200, 23);

        jLabel133.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel133.setText("6.3 Bila pasien dirawat di rumah, apakah lingkungan sudah dipersiapkan :");
        jLabel133.setName("jLabel133"); // NOI18N
        FormInput.add(jLabel133);
        jLabel133.setBounds(30, 1120, 400, 23);

        LingkunganSiap.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Ya", "Tidak" }));
        LingkunganSiap.setName("LingkunganSiap"); // NOI18N
        FormInput.add(LingkunganSiap);
        LingkunganSiap.setBounds(434, 1120, 80, 23);

        jLabel134.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel134.setText("Jika Ya, apakah mampu merawat pasien di rumah :");
        jLabel134.setName("jLabel134"); // NOI18N
        FormInput.add(jLabel134);
        jLabel134.setBounds(50, 1150, 290, 23);

        MampuMerawat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Ya", "Tidak" }));
        MampuMerawat.setName("MampuMerawat"); // NOI18N
        FormInput.add(MampuMerawat);
        MampuMerawat.setBounds(344, 1150, 80, 23);

        jLabel135.setText("Oleh :");
        jLabel135.setName("jLabel135"); // NOI18N
        FormInput.add(jLabel135);
        jLabel135.setBounds(428, 1150, 50, 23);

        MampuMerawatOleh.setName("MampuMerawatOleh"); // NOI18N
        FormInput.add(MampuMerawatOleh);
        MampuMerawatOleh.setBounds(482, 1150, 308, 23);

        jLabel136.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel136.setText("Jika Tidak, apakah perlu difasilitasi RS (Home Care) :");
        jLabel136.setName("jLabel136"); // NOI18N
        FormInput.add(jLabel136);
        jLabel136.setBounds(50, 1180, 290, 23);

        PerluHomeCare.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Ya", "Tidak" }));
        PerluHomeCare.setName("PerluHomeCare"); // NOI18N
        FormInput.add(PerluHomeCare);
        PerluHomeCare.setBounds(344, 1180, 80, 23);

        jLabel137.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel137.setText("6.4 Reaksi pasien atas penyakitnya :");
        jLabel137.setName("jLabel137"); // NOI18N
        FormInput.add(jLabel137);
        jLabel137.setBounds(30, 1210, 400, 23);

        ChkPasienMenyangkal.setText("Menyangkal");
        ChkPasienMenyangkal.setOpaque(false);
        ChkPasienMenyangkal.setName("ChkPasienMenyangkal"); // NOI18N
        FormInput.add(ChkPasienMenyangkal);
        ChkPasienMenyangkal.setBounds(50, 1240, 360, 23);

        ChkPasienSedih.setText("Sedih");
        ChkPasienSedih.setOpaque(false);
        ChkPasienSedih.setName("ChkPasienSedih"); // NOI18N
        FormInput.add(ChkPasienSedih);
        ChkPasienSedih.setBounds(430, 1240, 360, 23);

        ChkPasienMarah.setText("Marah");
        ChkPasienMarah.setOpaque(false);
        ChkPasienMarah.setName("ChkPasienMarah"); // NOI18N
        FormInput.add(ChkPasienMarah);
        ChkPasienMarah.setBounds(50, 1270, 360, 23);

        ChkPasienRasaBersalah.setText("Rasa bersalah");
        ChkPasienRasaBersalah.setOpaque(false);
        ChkPasienRasaBersalah.setName("ChkPasienRasaBersalah"); // NOI18N
        FormInput.add(ChkPasienRasaBersalah);
        ChkPasienRasaBersalah.setBounds(430, 1270, 360, 23);

        ChkPasienTakut.setText("Takut");
        ChkPasienTakut.setOpaque(false);
        ChkPasienTakut.setName("ChkPasienTakut"); // NOI18N
        FormInput.add(ChkPasienTakut);
        ChkPasienTakut.setBounds(50, 1300, 360, 23);

        ChkPasienTidakBerdaya.setText("Tidak berdaya");
        ChkPasienTidakBerdaya.setOpaque(false);
        ChkPasienTidakBerdaya.setName("ChkPasienTidakBerdaya"); // NOI18N
        FormInput.add(ChkPasienTidakBerdaya);
        ChkPasienTidakBerdaya.setBounds(430, 1300, 360, 23);

        jLabel138.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel138.setText("Masalah Keperawatan :");
        jLabel138.setName("jLabel138"); // NOI18N
        FormInput.add(jLabel138);
        jLabel138.setBounds(50, 1330, 300, 23);

        ChkAnxietas.setText("Anxietas");
        ChkAnxietas.setOpaque(false);
        ChkAnxietas.setName("ChkAnxietas"); // NOI18N
        FormInput.add(ChkAnxietas);
        ChkAnxietas.setBounds(50, 1360, 360, 23);

        ChkDistresSpiritual.setText("Distres spiritual");
        ChkDistresSpiritual.setOpaque(false);
        ChkDistresSpiritual.setName("ChkDistresSpiritual"); // NOI18N
        FormInput.add(ChkDistresSpiritual);
        ChkDistresSpiritual.setBounds(430, 1360, 360, 23);

        jLabel139.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel139.setText("6.5 Reaksi keluarga atas penyakit pasien :");
        jLabel139.setName("jLabel139"); // NOI18N
        FormInput.add(jLabel139);
        jLabel139.setBounds(30, 1390, 400, 23);

        ChkKeluargaGangguanTidur.setText("Gangguan tidur");
        ChkKeluargaGangguanTidur.setOpaque(false);
        ChkKeluargaGangguanTidur.setName("ChkKeluargaGangguanTidur"); // NOI18N
        FormInput.add(ChkKeluargaGangguanTidur);
        ChkKeluargaGangguanTidur.setBounds(50, 1420, 360, 23);

        ChkKeluargaTidakMampuPeran.setText("Ketidakmampuan memenuhi peran yang diharapkan");
        ChkKeluargaTidakMampuPeran.setOpaque(false);
        ChkKeluargaTidakMampuPeran.setName("ChkKeluargaTidakMampuPeran"); // NOI18N
        FormInput.add(ChkKeluargaTidakMampuPeran);
        ChkKeluargaTidakMampuPeran.setBounds(430, 1420, 360, 23);

        ChkKeluargaMarah.setText("Marah");
        ChkKeluargaMarah.setOpaque(false);
        ChkKeluargaMarah.setName("ChkKeluargaMarah"); // NOI18N
        FormInput.add(ChkKeluargaMarah);
        ChkKeluargaMarah.setBounds(50, 1450, 360, 23);

        ChkKeluargaKurangKomunikasi.setText("Keluarga kurang berkomunikasi dengan pasien");
        ChkKeluargaKurangKomunikasi.setOpaque(false);
        ChkKeluargaKurangKomunikasi.setName("ChkKeluargaKurangKomunikasi"); // NOI18N
        FormInput.add(ChkKeluargaKurangKomunikasi);
        ChkKeluargaKurangKomunikasi.setBounds(430, 1450, 360, 23);

        ChkKeluargaLetih.setText("Letih");
        ChkKeluargaLetih.setOpaque(false);
        ChkKeluargaLetih.setName("ChkKeluargaLetih"); // NOI18N
        FormInput.add(ChkKeluargaLetih);
        ChkKeluargaLetih.setBounds(50, 1480, 360, 23);

        ChkKeluargaRasaBersalah.setText("Rasa bersalah");
        ChkKeluargaRasaBersalah.setOpaque(false);
        ChkKeluargaRasaBersalah.setName("ChkKeluargaRasaBersalah"); // NOI18N
        FormInput.add(ChkKeluargaRasaBersalah);
        ChkKeluargaRasaBersalah.setBounds(430, 1480, 360, 23);

        jLabel140.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel140.setText("7. Kebutuhan untuk bantuan atau penundaan pelayanan :");
        jLabel140.setName("jLabel140"); // NOI18N
        FormInput.add(jLabel140);
        jLabel140.setBounds(10, 1510, 330, 23);

        BantuanPenundaan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        BantuanPenundaan.setName("BantuanPenundaan"); // NOI18N
        FormInput.add(BantuanPenundaan);
        BantuanPenundaan.setBounds(344, 1510, 80, 23);

        jLabel141.setText("Alasan :");
        jLabel141.setName("jLabel141"); // NOI18N
        FormInput.add(jLabel141);
        jLabel141.setBounds(428, 1510, 50, 23);

        BantuanPenundaanAlasan.setName("BantuanPenundaanAlasan"); // NOI18N
        FormInput.add(BantuanPenundaanAlasan);
        BantuanPenundaanAlasan.setBounds(482, 1510, 308, 23);

        jLabel142.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel142.setText("8. Kebutuhan layanan alternatif (Rujuk) :");
        jLabel142.setName("jLabel142"); // NOI18N
        FormInput.add(jLabel142);
        jLabel142.setBounds(10, 1540, 330, 23);

        LayananAlternatif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        LayananAlternatif.setName("LayananAlternatif"); // NOI18N
        FormInput.add(LayananAlternatif);
        LayananAlternatif.setBounds(344, 1540, 80, 23);

        jLabel143.setText("Kemana :");
        jLabel143.setName("jLabel143"); // NOI18N
        FormInput.add(jLabel143);
        jLabel143.setBounds(428, 1540, 50, 23);

        LayananAlternatifTujuan.setName("LayananAlternatifTujuan"); // NOI18N
        FormInput.add(LayananAlternatifTujuan);
        LayananAlternatifTujuan.setBounds(482, 1540, 308, 23);

        jLabel144.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel144.setText("9. Faktor risiko bagi yang ditinggalkan dalam hal cara mengatasi dan potensi reaksi patologis atas kesedihan :");
        jLabel144.setName("jLabel144"); // NOI18N
        FormInput.add(jLabel144);
        jLabel144.setBounds(10, 1570, 780, 23);

        ChkDapatMenerima.setText("Dapat menerima");
        ChkDapatMenerima.setOpaque(false);
        ChkDapatMenerima.setName("ChkDapatMenerima"); // NOI18N
        FormInput.add(ChkDapatMenerima);
        ChkDapatMenerima.setBounds(50, 1600, 360, 23);

        ChkTidakMenerima.setText("Tidak menerima");
        ChkTidakMenerima.setOpaque(false);
        ChkTidakMenerima.setName("ChkTidakMenerima"); // NOI18N
        FormInput.add(ChkTidakMenerima);
        ChkTidakMenerima.setBounds(430, 1600, 360, 23);

        ChkPasrah.setText("Pasrah");
        ChkPasrah.setOpaque(false);
        ChkPasrah.setName("ChkPasrah"); // NOI18N
        FormInput.add(ChkPasrah);
        ChkPasrah.setBounds(50, 1630, 360, 23);

        jLabel145.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel145.setText("Lainnya :");
        jLabel145.setName("jLabel145"); // NOI18N
        FormInput.add(jLabel145);
        jLabel145.setBounds(50, 1660, 60, 23);

        RisikoLainnya.setName("RisikoLainnya"); // NOI18N
        FormInput.add(RisikoLainnya);
        RisikoLainnya.setBounds(114, 1660, 676, 23);

        jLabel146.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel146.setText("10. Rekomendasi hasil asesmen :");
        jLabel146.setName("jLabel146"); // NOI18N
        FormInput.add(jLabel146);
        jLabel146.setBounds(10, 1690, 500, 23);

        scrollPane25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane25.setName("scrollPane25"); // NOI18N

        Rekomendasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Rekomendasi.setColumns(20);
        Rekomendasi.setRows(5);
        Rekomendasi.setName("Rekomendasi"); // NOI18N
        scrollPane25.setViewportView(Rekomendasi);

        FormInput.add(scrollPane25);
        scrollPane25.setBounds(30, 1710, 760, 63);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Asesmen", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-10-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-10-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
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
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
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
        panelGlass9.add(BtnAll);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Data Asesmen", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, Tanggal);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (isValidSmc()) {
            simpan();
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, Rekomendasi, BtnBatal);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        TabRawat.setSelectedIndex(0);
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnBatalActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            if (bolehUbah("dihapus")) {
                if (JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener mau dihapus..??", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    hapus();
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (isValidSmc()) {
            if (tbObat.getSelectedRow() > -1) {
                if (bolehUbah("diganti")) {
                    ganti();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("file2.css")))) {
                    bw.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}.isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}");
                    bw.flush();
                }
                String pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih laporan..!", "Pilihan Cetak", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
                    "Laporan 1 (HTML)", "Laporan 2 (WPS)", "Laporan 3 (CSV)", "Laporan 4 (XLSX)"
                }, "Laporan 1 (HTML)");
                if (pilihan != null) {
                    switch (pilihan) {
                        case "Laporan 1 (HTML)":
                            Valid.exportHtmlSmc("DataAsesmenPasienTerminal.html", "DATA ASESMEN AWAL DAN ULANG PASIEN TERMINAL DAN KELUARGANYA", tbObat);
                            break;
                        case "Laporan 2 (WPS)":
                            Valid.exportWPSSmc("DataAsesmenPasienTerminal.wps", "DATA ASESMEN AWAL DAN ULANG PASIEN TERMINAL DAN KELUARGANYA", tbObat);
                            break;
                        case "Laporan 3 (CSV)":
                            Valid.exportCSVSmc("DataAsesmenPasienTerminal.csv", tbObat);
                            break;
                        case "Laporan 4 (XLSX)":
                            Valid.exportXlsxSmc("DataAsesmenPasienTerminal.xlsx", tbObat);
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() -> tampil());
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
        runBackground(() -> tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            if (evt.getClickCount() == 2) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tbObatKeyPressed

    private void KdPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPetugasKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NmPetugas.setText(Sequel.CariPetugas(KdPetugas.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Detik.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            BtnPetugasActionPerformed(null);
        }
    }//GEN-LAST:event_KdPetugasKeyPressed

    private void BtnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPetugasActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas = new DlgCariPetugas(null, false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (petugas.getTable().getSelectedRow() != -1) {
                        KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    }
                    BtnPetugas.requestFocus();
                    petugas = null;
                }
            });
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
        if (petugas == null) {
            return;
        }
        if (!petugas.isVisible()) {
            petugas.isCek();
            petugas.emptTeks();
        }
        if (petugas.isVisible()) {
            petugas.toFront();
            return;
        }
        petugas.setVisible(true);
    }//GEN-LAST:event_BtnPetugasActionPerformed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        if (dokter == null || !dokter.isDisplayable()) {
            dokter = new DlgCariDokter(null, false);
            dokter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dokter.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (dokter.getTable().getSelectedRow() != -1) {
                        KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    }
                    BtnDokter.requestFocus();
                    dokter = null;
                }
            });
            dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokter.setLocationRelativeTo(internalFrame1);
        }
        if (dokter == null) {
            return;
        }
        if (!dokter.isVisible()) {
            dokter.isCek();
            dokter.emptTeks();
        }
        if (dokter.isVisible()) {
            dokter.toFront();
            return;
        }
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void MnPenilaianPasienTerminalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPenilaianPasienTerminalActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            String norawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
            String tanggal = tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString();
            String nip = tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString();
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            String finger = Sequel.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", nip);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString() + "\nID " + (finger.isBlank() ? nip : finger) + "\n" + tanggal);
            Object kddokter = tbObat.getValueAt(tbObat.getSelectedRow(), KOLOM_SMC + 1);
            param.put("fingerdokter", (kddokter == null || kddokter.toString().isBlank()) ? "" : Valid.fingerSmc(kddokter.toString(), tanggal));
            Valid.reportSmc("rptFormulirPenilaianPasienTerminalSMC.jasper", "report", "::[ Formulir Asesmen Awal dan Ulang Pasien Terminal dan Keluarganya ]::", param,
                querySmc() + "where penilaian_pasien_terminal.no_rawat = ? and penilaian_pasien_terminal.tanggal = ?", norawat, tanggal);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_MnPenilaianPasienTerminalActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if ("aktif".equals(koneksiDB.CARICEPAT())) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        runBackground(() -> tampil());
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        runBackground(() -> tampil());
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        runBackground(() -> tampil());
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    private void MnPenilaianUlangNyeriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPenilaianUlangNyeriActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            RMPenilaianUlangNyeri form = new RMPenilaianUlangNyeri(null, false);
            form.isCek();
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            form.setVisible(true);
            form.setNoRm(teks(tbObat.getSelectedRow(), 0), DTPCari2.getDate());
            form.tampil();
            setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_MnPenilaianUlangNyeriActionPerformed

    private void MnPermintaanBinrohtalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPermintaanBinrohtalActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SuratPermintaanBinrohtal form = new SuratPermintaanBinrohtal(null, false);
            form.isCek();
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            form.setVisible(true);
            form.emptTeks();
            form.setNoRm(teks(tbObat.getSelectedRow(), 0), DTPCari2.getDate());
            setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_MnPermintaanBinrohtalActionPerformed

    private void MnPersetujuanPenundaanPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPersetujuanPenundaanPelayananActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SuratPersetujuanPenundaanPelayanan form = new SuratPersetujuanPenundaanPelayanan(null, false);
            form.isCek();
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            form.setVisible(true);
            form.emptTeks();
            form.setNoRm(teks(tbObat.getSelectedRow(), 0), DTPCari2.getDate());
            setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_MnPersetujuanPenundaanPelayananActionPerformed

    private void MnRujukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnRujukActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            DlgRujuk form = new DlgRujuk(null, false);
            form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            form.setLocationRelativeTo(internalFrame1);
            form.emptTeks();
            form.isCek();
            form.setNoRm(teks(tbObat.getSelectedRow(), 0), DTPCari1.getDate(), DTPCari2.getDate());
            form.setVisible(true);
            setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_MnRujukActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMPenilaianPasienTerminalSMC dialog = new RMPenilaianPasienTerminalSMC(new javax.swing.JFrame(), true);
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
    private widget.ComboBox BantuanPenundaan;
    private widget.TextBox BantuanPenundaanAlasan;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPetugas;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkAdaSekret;
    private widget.CekBox ChkAktivitasFisik;
    private widget.CekBox ChkAnxietas;
    private widget.CekBox ChkBercakSianosis;
    private widget.CekBox ChkBersihanJalanNafas;
    private widget.CekBox ChkDapatMenerima;
    private widget.CekBox ChkDefisitPerawatanDiri;
    private widget.CekBox ChkDistendedAbdomen;
    private widget.CekBox ChkDistresSpiritual;
    private widget.CekBox ChkDyspneu;
    private widget.CekBox ChkGelisah;
    private widget.CekBox ChkKejadian;
    private widget.CekBox ChkKeluargaGangguanTidur;
    private widget.CekBox ChkKeluargaKurangKomunikasi;
    private widget.CekBox ChkKeluargaLetih;
    private widget.CekBox ChkKeluargaMarah;
    private widget.CekBox ChkKeluargaRasaBersalah;
    private widget.CekBox ChkKeluargaTidakMampuPeran;
    private widget.CekBox ChkKonstipasi;
    private widget.CekBox ChkKulitDingin;
    private widget.CekBox ChkLemas;
    private widget.CekBox ChkMasalahMual;
    private widget.CekBox ChkMukosaOralKering;
    private widget.CekBox ChkNafasCepatDangkal;
    private widget.CekBox ChkNafasLambat;
    private widget.CekBox ChkNafasMelaluiMulut;
    private widget.CekBox ChkNafasTidakTeratur;
    private widget.CekBox ChkNyeriAkut;
    private widget.CekBox ChkNyeriKronis;
    private widget.CekBox ChkPasienMarah;
    private widget.CekBox ChkPasienMenyangkal;
    private widget.CekBox ChkPasienRasaBersalah;
    private widget.CekBox ChkPasienSedih;
    private widget.CekBox ChkPasienTakut;
    private widget.CekBox ChkPasienTidakBerdaya;
    private widget.CekBox ChkPasrah;
    private widget.CekBox ChkPersepsiSensori;
    private widget.CekBox ChkPindahPosisi;
    private widget.CekBox ChkPolaNafas;
    private widget.CekBox ChkSpO2Kurang90;
    private widget.CekBox ChkSulitBicara;
    private widget.CekBox ChkSulitMenelan;
    private widget.CekBox ChkTDMenurun;
    private widget.CekBox ChkTidakMenerima;
    private widget.CekBox ChkTonusMual;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.TextBox FaktorLainnya;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Hubungi;
    private widget.TextBox HubungiAlamat;
    private widget.TextBox HubungiHubungan;
    private widget.TextBox HubungiNama;
    private widget.TextBox HubungiTelp;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox KdDokter;
    private widget.TextBox KdPetugas;
    private widget.Label LCount;
    private widget.ComboBox LayananAlternatif;
    private widget.TextBox LayananAlternatifTujuan;
    private widget.ComboBox LingkunganSiap;
    private widget.ComboBox MampuMerawat;
    private widget.TextBox MampuMerawatOleh;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnPenilaianPasienTerminal;
    private javax.swing.JMenuItem MnPenilaianUlangNyeri;
    private javax.swing.JMenuItem MnPermintaanBinrohtal;
    private javax.swing.JMenuItem MnPersetujuanPenundaanPelayanan;
    private javax.swing.JMenuItem MnRujuk;
    private widget.TextBox NmDokter;
    private widget.TextBox NmPetugas;
    private widget.ComboBox Nyeri;
    private widget.TextBox NyeriKeterangan;
    private widget.TextBox NyeriManajemen;
    private widget.ComboBox PelayananSpiritual;
    private widget.TextBox PelayananSpiritualOleh;
    private widget.ComboBox PerluBimbinganRohani;
    private widget.ComboBox PerluDidoakan;
    private widget.ComboBox PerluHomeCare;
    private widget.ComboBox PerluPendampinganRohani;
    private widget.TextArea Rekomendasi;
    private widget.ComboBox RencanaPerawatan;
    private widget.TextBox RisikoLainnya;
    private widget.TextBox Ruangan;
    private widget.ScrollPane Scroll;
    private widget.ComboBox SkalaNyeri;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Tanggal Tanggal;
    private widget.TextBox TglLahir;
    private widget.TextBox TonusLainnya;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel102;
    private widget.Label jLabel103;
    private widget.Label jLabel106;
    private widget.Label jLabel107;
    private widget.Label jLabel108;
    private widget.Label jLabel109;
    private widget.Label jLabel110;
    private widget.Label jLabel111;
    private widget.Label jLabel112;
    private widget.Label jLabel113;
    private widget.Label jLabel114;
    private widget.Label jLabel115;
    private widget.Label jLabel116;
    private widget.Label jLabel117;
    private widget.Label jLabel118;
    private widget.Label jLabel119;
    private widget.Label jLabel120;
    private widget.Label jLabel121;
    private widget.Label jLabel122;
    private widget.Label jLabel123;
    private widget.Label jLabel124;
    private widget.Label jLabel125;
    private widget.Label jLabel126;
    private widget.Label jLabel127;
    private widget.Label jLabel128;
    private widget.Label jLabel129;
    private widget.Label jLabel130;
    private widget.Label jLabel131;
    private widget.Label jLabel132;
    private widget.Label jLabel133;
    private widget.Label jLabel134;
    private widget.Label jLabel135;
    private widget.Label jLabel136;
    private widget.Label jLabel137;
    private widget.Label jLabel138;
    private widget.Label jLabel139;
    private widget.Label jLabel140;
    private widget.Label jLabel141;
    private widget.Label jLabel142;
    private widget.Label jLabel143;
    private widget.Label jLabel144;
    private widget.Label jLabel145;
    private widget.Label jLabel146;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator11;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane25;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        SwingUtilities.invokeLater(() -> Valid.tabelKosongSmc(tabMode));
        StringBuilder sql = new StringBuilder(querySmc()).append("where penilaian_pasien_terminal.tanggal between ? and ? ");
        if (!TCari.getText().isBlank()) {
            sql.append("and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or penilaian_pasien_terminal.nip like ? or petugas.nama like ? or dokter.nm_dokter like ?) ");
        }
        sql.append("order by penilaian_pasien_terminal.tanggal");
        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            ps.setString(1, Valid.getTglSmc(DTPCari1) + " 00:00:00");
            ps.setString(2, Valid.getTglSmc(DTPCari2) + " 23:59:59");
            if (!TCari.getText().isBlank()) {
                for (int i = 3; i <= 8; i++) {
                    ps.setString(i, "%" + TCari.getText() + "%");
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[tabMode.getColumnCount()];
                    row[0] = rs.getString("no_rawat");
                    row[1] = rs.getString("no_rkm_medis");
                    row[2] = rs.getString("nm_pasien");
                    row[3] = rs.getDate("tgl_lahir");
                    row[4] = rs.getString("jk");
                    row[5] = rs.getString("tanggal");
                    row[6] = rs.getString("skala_nyeri");
                    row[7] = rs.getString("nip");
                    row[8] = rs.getString("nama");
                    for (int i = 0; i < kolom.length; i++) {
                        row[kolomTabel(i)] = rs.getString(kolom[i]);
                    }
                    row[KOLOM_SMC + 1] = rs.getString("kd_dokter");
                    row[KOLOM_SMC + 2] = rs.getString("nm_dokter");
                    SwingUtilities.invokeLater(() -> tabMode.addRow(row));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        SwingUtilities.invokeLater(() -> LCount.setText(String.valueOf(tabMode.getRowCount())));
    }

    private String querySmc() {
        StringBuilder sql = new StringBuilder("select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, penilaian_pasien_terminal.tanggal, ")
            .append("penilaian_pasien_terminal.skala_nyeri, penilaian_pasien_terminal.nip, petugas.nama, penilaian_pasien_terminal_smc.kd_dokter, dokter.nm_dokter");
        for (String k : kolom) {
            sql.append(", penilaian_pasien_terminal_smc.").append(k);
        }
        return sql.append(" from penilaian_pasien_terminal join reg_periksa on penilaian_pasien_terminal.no_rawat = reg_periksa.no_rawat ")
            .append("join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis join petugas on penilaian_pasien_terminal.nip = petugas.nip ")
            .append("left join penilaian_pasien_terminal_smc on penilaian_pasien_terminal.no_rawat = penilaian_pasien_terminal_smc.no_rawat ")
            .append("and penilaian_pasien_terminal.tanggal = penilaian_pasien_terminal_smc.tanggal ")
            .append("left join dokter on penilaian_pasien_terminal_smc.kd_dokter = dokter.kd_dokter ").toString();
    }

    private int kolomTabel(int i) {
        return i < 1 ? KOLOM_SMC + i : KOLOM_SMC + i + 2;
    }

    public void emptTeks() {
        Tanggal.setDate(new Date());
        SkalaNyeri.setSelectedIndex(0);
        for (JComponent c : isian) {
            isi(c, null);
        }
        Ruangan.setText(ruanganSmc(TNoRw.getText()));
        setDokterDefaultSmc(TNoRw.getText());
        ChkKejadian.setSelected(true);
        TabRawat.setSelectedIndex(0);
        ChkDyspneu.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            int row = tbObat.getSelectedRow();
            ChkKejadian.setSelected(false);
            TNoRw.setText(teks(row, 0));
            TNoRM.setText(teks(row, 1));
            TPasien.setText(teks(row, 2));
            TglLahir.setText(teks(row, 3));
            JK.setText(teks(row, 4));
            Valid.setTglJamSmc(Tanggal, Jam, Menit, Detik, teks(row, 5));
            SkalaNyeri.setSelectedItem(teks(row, 6));
            KdPetugas.setText(teks(row, 7));
            NmPetugas.setText(teks(row, 8));
            for (int i = 0; i < isian.length; i++) {
                isi(isian[i], tbObat.getValueAt(row, kolomTabel(i)));
            }
            KdDokter.setText(teks(row, KOLOM_SMC + 1));
            NmDokter.setText(teks(row, KOLOM_SMC + 2));
            if (null == tbObat.getValueAt(row, KOLOM_SMC)) {
                Ruangan.setText(ruanganSmc(teks(row, 0)));
                setDokterDefaultSmc(teks(row, 0));
                Nyeri.setSelectedItem("0".equals(teks(row, 6)) ? "Tidak" : "Ya");
            }
        }
    }

    private String teks(int row, int col) {
        Object o = tbObat.getValueAt(row, col);
        return o == null ? "" : o.toString();
    }

    private String nilai(JComponent c) {
        if (c instanceof CekBox) {
            return ((CekBox) c).isSelected() ? "Ya" : "Tidak";
        } else if (c instanceof ComboBox) {
            return ((ComboBox) c).getSelectedItem().toString();
        }
        return ((JTextComponent) c).getText();
    }

    private void isi(JComponent c, Object o) {
        if (c instanceof CekBox) {
            ((CekBox) c).setSelected("Ya".equals(o));
        } else if (c instanceof ComboBox) {
            if (o == null) {
                ((ComboBox) c).setSelectedIndex(0);
            } else {
                ((ComboBox) c).setSelectedItem(o.toString());
            }
        } else {
            ((JTextComponent) c).setText(o == null ? "" : o.toString());
        }
    }

    private String ruanganSmc(String norawat) {
        if (norawat.isBlank()) {
            return "";
        }
        String ruangan = Sequel.cariIsiSmc("select concat(kamar_inap.kd_kamar, ' ', bangsal.nm_bangsal) from kamar_inap join kamar on kamar_inap.kd_kamar = kamar.kd_kamar " +
            "join bangsal on kamar.kd_bangsal = bangsal.kd_bangsal where kamar_inap.no_rawat = ? order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc limit 1", norawat);
        if (ruangan.isBlank()) {
            ruangan = Sequel.cariIsiSmc("select poliklinik.nm_poli from reg_periksa join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli where reg_periksa.no_rawat = ?", norawat);
        }
        return ruangan;
    }

    private void isRawat() {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, reg_periksa.tgl_registrasi from reg_periksa " +
            "join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where reg_periksa.no_rawat = ?")) {
            ps.setString(1, TNoRw.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        Ruangan.setText(ruanganSmc(TNoRw.getText()));
        setDokterDefaultSmc(TNoRw.getText());
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        TabRawat.setSelectedIndex(0);
        runBackground(() -> tampil());
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
        runBackground(() -> tampil());
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpenilaian_pasien_terminal());
        BtnHapus.setEnabled(akses.getpenilaian_pasien_terminal());
        BtnEdit.setEnabled(akses.getpenilaian_pasien_terminal());
        BtnPrint.setEnabled(akses.getpenilaian_pasien_terminal());
        MnPenilaianPasienTerminal.setEnabled(akses.getpenilaian_pasien_terminal());
        MnPenilaianUlangNyeri.setEnabled(akses.getpenilaian_pasien_terminal());
        MnPermintaanBinrohtal.setEnabled(akses.getpenilaian_pasien_terminal());
        MnPersetujuanPenundaanPelayanan.setEnabled(akses.getpenilaian_pasien_terminal());
        MnRujuk.setEnabled(akses.getpenilaian_pasien_terminal());
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false);
            BtnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(Sequel.CariPetugas(KdPetugas.getText()));
            if (NmPetugas.getText().isBlank()) {
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
    }

    private void setDokterDefaultSmc(String norawat) {
        String kddokter = "";
        if (!norawat.isBlank()) {
            if ("Ranap".equals(Sequel.cariIsiSmc("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat = ?", norawat))) {
                kddokter = Sequel.cariIsiSmc("select dpjp_ranap.kd_dokter from dpjp_ranap where dpjp_ranap.no_rawat = ? limit 1", norawat);
            }
            if (kddokter.isBlank()) {
                kddokter = Sequel.cariIsiSmc("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat = ?", norawat);
            }
        }
        KdDokter.setText(kddokter);
        NmDokter.setText(kddokter.isBlank() ? "" : Sequel.cariIsiSmc("select dokter.nm_dokter from dokter where dokter.kd_dokter = ?", kddokter));
    }

    private boolean isValidSmc() {
        if (TNoRw.getText().isBlank() || TPasien.getText().isBlank()) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (KdPetugas.getText().isBlank() || NmPetugas.getText().isBlank()) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            return true;
        }
        return false;
    }

    private boolean bolehUbah(String aksi) {
        if ("Admin Utama".equals(akses.getkode())) {
            return true;
        }
        if (!KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString())) {
            JOptionPane.showMessageDialog(null, "Hanya bisa " + aksi + " oleh petugas yang bersangkutan..!!");
            return false;
        }
        return Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString(), Sequel.ambiltanggalsekarang());
    }

    private boolean simpanDetailSmc(String norawat, String tanggal) {
        String[] values = new String[isian.length + 3];
        values[0] = norawat;
        values[1] = tanggal;
        for (int i = 0; i < isian.length; i++) {
            values[i + 2] = nilai(isian[i]);
        }
        values[isian.length + 2] = KdDokter.getText().isBlank() ? null : KdDokter.getText();
        return Sequel.menyimpantfSmc("penilaian_pasien_terminal_smc", "no_rawat, tanggal, " + String.join(", ", kolom) + ", kd_dokter", values);
    }

    private void simpan() {
        String tanggal = Valid.getTglJamSmc(Tanggal, Jam, Menit, Detik);
        Sequel.AutoComitFalse();
        boolean sukses = Sequel.menyimpantfSmc("penilaian_pasien_terminal", "no_rawat, tanggal, diagnosa, rps, rpd, rr, spo2, skala_nyeri, nip",
            TNoRw.getText(), tanggal, "", "", "", "", "", SkalaNyeri.getSelectedItem().toString(), KdPetugas.getText());
        if (sukses) {
            sukses = simpanDetailSmc(TNoRw.getText(), tanggal);
        }
        if (sukses) {
            Sequel.Commit();
        } else {
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
        if (sukses) {
            emptTeks();
            TabRawat.setSelectedIndex(1);
            runBackground(() -> tampil());
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data, kemungkinan sudah ada asesmen pada tanggal & jam yang sama..!!");
        }
    }

    private void ganti() {
        String norawatlama = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        String tanggallama = tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString();
        String tanggal = Valid.getTglJamSmc(Tanggal, Jam, Menit, Detik);
        Sequel.AutoComitFalse();
        boolean sukses = Sequel.mengupdatetfSmc("penilaian_pasien_terminal", "no_rawat = ?, tanggal = ?, skala_nyeri = ?, nip = ?", "no_rawat = ? and tanggal = ?",
            TNoRw.getText(), tanggal, SkalaNyeri.getSelectedItem().toString(), KdPetugas.getText(), norawatlama, tanggallama);
        if (sukses) {
            Sequel.menghapustfSmc("penilaian_pasien_terminal_smc", "no_rawat = ? and tanggal = ?", TNoRw.getText(), tanggal);
            sukses = simpanDetailSmc(TNoRw.getText(), tanggal);
        }
        if (sukses) {
            Sequel.Commit();
        } else {
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
        if (sukses) {
            emptTeks();
            TabRawat.setSelectedIndex(1);
            runBackground(() -> tampil());
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengganti data..!!");
        }
    }

    private void hapus() {
        if (Sequel.menghapustfSmc("penilaian_pasien_terminal", "no_rawat = ? and tanggal = ?", tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(String.valueOf(tabMode.getRowCount()));
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ChkKejadian.isSelected()) {
                    Calendar now = Calendar.getInstance();
                    Jam.setSelectedIndex(now.get(Calendar.HOUR_OF_DAY));
                    Menit.setSelectedIndex(now.get(Calendar.MINUTE));
                    Detik.setSelectedIndex(now.get(Calendar.SECOND));
                }
            }
        };
        new Timer(1000, taskPerformer).start();
    }

    private void runBackground(Runnable task) {
        if (ceksukses) {
            return;
        }
        if (executor.isShutdown() || executor.isTerminated()) {
            return;
        }
        if (!isDisplayable()) {
            return;
        }

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

    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
