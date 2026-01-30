package khanzahmsanjungan;

import bridging.ApiBPJS;
import bridging.BPJSReferensiDiagnosa;
import bridging.BPJSReferensiDokter;
import bridging.BPJSRiwayatPelayananPasien;
import bridging.BPJSRiwayatRujukanPasien;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import fungsi.BatasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class DlgRegistrasiBPJS extends widget.Dialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final ApiBPJS api = new ApiBPJS();
    private final BPJSReferensiDokter dokter;
    private final BPJSReferensiDiagnosa diagnosa;
    private final DlgCariPoliBPJS poli;
    private final BPJSRiwayatRujukanPasien riwayatRujukan;
    private final BPJSRiwayatPelayananPasien riwayatPelayanan;
    private final boolean ADDANTRIANAPIMOBILEJKN = koneksiDB.ADDANTRIANAPIMOBILEJKN(), REGISTRASISATUJAMSEBELUMJAMPRAKTEK = koneksiDB.REGISTRASISATUJAMSEBELUMJAMPRAKTEK();
    private final Set<String> VALIDASIBIOMETRIKAKTIF = new HashSet<>(Arrays.asList(koneksiDB.VALIDASIBIOMETRIKAKTIF()));
    private String hari = "",
        tglkll = "0000-00-00",
        datajam = "",
        url = "",
        payload = "",
        utc = "",
        aksi = "",
        kodeBPJS = "",
        instansiNama = "",
        instansiKota = "",
        noSEP = "",
        noBooking = "",
        noReferensi = "",
        jenisKunjungan = "",
        estimasiDilayani = "",
        jamPraktek = "",
        kodePoli = "",
        kodeDokter = "",
        prb = "",
        tglRencanaKontrol = "",
        noReg = "",
        noRawat = "",
        kodeDokterReg = "",
        kodePoliReg = "",
        namaPJ = "",
        alamatPJ = "",
        hubunganPJ = "",
        biayaReg = "",
        statusPoli = "Baru",
        statusDaftar = "",
        umurDaftar = "0",
        statusUmur = "Th",
        umurPasien = "",
        noTelpBPJS = "";
    private int kuota = 0, sisaKuota = 0;
    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode root, response, metadata;
    private HttpHeaders headers;
    private HttpEntity entity;
    private boolean statusFinger = false, fingerprintAktif = false, fristaAktif = false, isMobileJKN = false, bisaTampilkanNumpad = false;
    private Date parsedDate;

    public DlgRegistrasiBPJS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        btnFrista.setVisible(VALIDASIBIOMETRIKAKTIF.contains("frista"));
        btnFingerprint.setVisible(VALIDASIBIOMETRIKAKTIF.contains("fingerprint"));

        kodeBPJS = Sequel.cariIsiSmc("select kd_pj from password_asuransi");
        try (ResultSet rs = koneksi.createStatement().executeQuery("select kode_ppk, nama_instansi, kabupaten from setting")) {
            if (rs.next()) {
                instansiNama = rs.getString("nama_instansi");
                instansiKota = rs.getString("kabupaten");
                kodePPK.setText(rs.getString("kode_ppk"));
                namaPPK.setText(instansiNama);
                catatan.setText("Anjungan Pasien Mandiri " + instansiNama);
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        dokter = new BPJSReferensiDokter(parent, modal);
        dokter.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.hasSelection()) {
                    kodeDokter = dokter.getSelectedRow(1).toString();
                    namaDokter.setText(dokter.getSelectedRow(2).toString());
                    kodeDPJPLayanan.setText(dokter.getSelectedRow(1).toString());
                    namaDPJPLayanan.setText(dokter.getSelectedRow(2).toString());
                    kodeDokterReg = Sequel.cariIsiSmc("select maping_dokter_dpjpvclaim.kd_dokter from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs = ?", kodeDokter);
                }
                namaDokter.requestFocus();
            }
        });

        poli = new DlgCariPoliBPJS(parent, modal);
        poli.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.hasSelection()) {
                    kodePoli = poli.getSelectedRow(0).toString();
                    namaPoli.setText(poli.getSelectedRow(1).toString());
                    kodePoliReg = poli.getSelectedRow(2).toString();
                    // jamPraktek = poli.getSelectedRow(3).toString();
                }
                namaPoli.requestFocus();
            }
        });

        diagnosa = new BPJSReferensiDiagnosa(parent, modal);
        diagnosa.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (diagnosa.hasSelection()) {
                    kodeDiagnosa.setText(diagnosa.getSelectedRow(1).toString());
                    namaDiagnosa.setText(diagnosa.getSelectedRow(2).toString());
                }
                kodeDiagnosa.requestFocus();
            }
        });

        riwayatRujukan = new BPJSRiwayatRujukanPasien(parent, modal);
        riwayatRujukan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (riwayatRujukan.hasSelection()) {
                    noRujukan.setText(riwayatRujukan.getSelectedRow(2).toString());
                    kodePPKRujukan.setText(riwayatRujukan.getSelectedRow(6).toString());
                    namaPPKRujukan.setText(riwayatRujukan.getSelectedRow(7).toString());
                    kodeDiagnosa.setText(riwayatRujukan.getSelectedRow(0).toString());
                    namaDiagnosa.setText(riwayatRujukan.getSelectedRow(1).toString());
                    kodePoli = riwayatRujukan.getSelectedRow(3).toString();
                    namaPoli.setText(riwayatRujukan.getSelectedRow(4).toString());
                    tglRujukan.setText(riwayatRujukan.getSelectedRow(5).toString());
                }
                catatan.requestFocus();
            }
        });

        riwayatPelayanan = new BPJSRiwayatPelayananPasien(parent, modal);
        riwayatPelayanan.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (riwayatPelayanan.getTable().getSelectedRow() != -1) {
                    if ((riwayatPelayanan.getTable().getSelectedColumn() == 6) || (riwayatPelayanan.getTable().getSelectedColumn() == 7)) {
                        noRujukan.setText(riwayatPelayanan.getTable().getValueAt(riwayatPelayanan.getTable().getSelectedRow(), riwayatPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }
                noRujukan.requestFocus();
            }
        });

        barcode.setDocument(new BatasInput((byte) 3).getOnlyAngka(barcode));
        emptTeks();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgAksiFP = new widget.Dialog();
        judulAksi = new widget.Label();
        panelTengahAksi = new widget.Panel();
        userAksi = new widget.PasswordField();
        passAksi = new widget.PasswordField();
        label1 = new widget.Label();
        label2 = new widget.Label();
        panelBawahAksi = new widget.Panel();
        btnAksiKonfirmasi = new widget.Button();
        btnAksiBatal = new widget.Button();
        panelAtas = new widget.Panel();
        label4 = new widget.Label();
        emptyKiri = new widget.Panel();
        panelTengah = new widget.Panel();
        panelUtama = new widget.Panel();
        namaPasien = new widget.TextField();
        noRM = new widget.TextField();
        noPeserta = new widget.TextField();
        jLabel20 = new widget.Label();
        jLabel22 = new widget.Label();
        jLabel23 = new widget.Label();
        noRujukan = new widget.TextField();
        jLabel10 = new widget.Label();
        kodePPKRujukan = new widget.TextField();
        namaPPKRujukan = new widget.TextField();
        jLabel11 = new widget.Label();
        kodeDiagnosa = new widget.TextField();
        namaDiagnosa = new widget.TextField();
        namaPoli = new widget.TextField();
        LabelPoli = new widget.Label();
        LabelKelas = new widget.Label();
        kelas = new widget.ComboBox();
        jLabel8 = new widget.Label();
        tglLahir = new widget.TextField();
        jLabel18 = new widget.Label();
        jk = new widget.TextField();
        jLabel24 = new widget.Label();
        jenisPeserta = new widget.TextField();
        jLabel25 = new widget.Label();
        statusPeserta = new widget.TextField();
        jLabel27 = new widget.Label();
        asalRujukan = new widget.ComboBox();
        noTelp = new widget.TextField();
        katarak = new widget.ComboBox();
        jLabel37 = new widget.Label();
        LabelPoli2 = new widget.Label();
        namaDokter = new widget.TextField();
        cariDokter = new widget.Button();
        jLabel56 = new widget.Label();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        noSKDP = new widget.TextField();
        jLabel26 = new widget.Label();
        nik = new widget.TextField();
        jLabel7 = new widget.Label();
        cariPoli = new widget.Button();
        cariDiagnosa = new widget.Button();
        cariNoRujukan = new widget.Button();
        btnRiwayatPelayanan = new widget.Button();
        btnFingerprint = new widget.Button();
        btnFrista = new widget.Button();
        panelNumpad = new widget.Numpad();
        tglRujukan = new widget.TextField();
        tglSEP = new widget.TextField();
        labelValidasi = new widget.Label();
        panelTambahan = new widget.Panel();
        jLabel13 = new widget.Label();
        jLabel42 = new widget.Label();
        tujuanKunjungan = new widget.ComboBox();
        jLabel43 = new widget.Label();
        flagProsedur = new widget.ComboBox();
        jLabel44 = new widget.Label();
        penunjang = new widget.ComboBox();
        jLabel45 = new widget.Label();
        asesmenPelayanan = new widget.ComboBox();
        LabelPoli7 = new widget.Label();
        kodeDPJPLayanan = new widget.TextField();
        namaDPJPLayanan = new widget.TextField();
        jLabel9 = new widget.Label();
        kodePPK = new widget.TextField();
        namaPPK = new widget.TextField();
        jLabel55 = new widget.Label();
        lakaLantas = new widget.ComboBox();
        tglKLL = new widget.Tanggal();
        jLabel36 = new widget.Label();
        keterangan = new widget.TextField();
        jLabel40 = new widget.Label();
        suplesi = new widget.ComboBox();
        jLabel41 = new widget.Label();
        noSEPSuplesi = new widget.TextField();
        LabelPoli3 = new widget.Label();
        kdPropKLL = new widget.TextField();
        nmPropKLL = new widget.TextField();
        LabelPoli4 = new widget.Label();
        kdKabKLL = new widget.TextField();
        nmKabKLL = new widget.TextField();
        LabelPoli5 = new widget.Label();
        kdKecKLL = new widget.TextField();
        nmKecKLL = new widget.TextField();
        jLabel14 = new widget.Label();
        catatan = new widget.TextField();
        btnApprovalFP = new widget.Button();
        btnPengajuanFP = new widget.Button();
        jLabel15 = new widget.Label();
        barcode = new widget.TextField();
        jenisPelayanan = new widget.TextField();
        emptyKanan = new widget.Panel();
        panelBawah = new widget.Panel();
        toggleInfoTambahan = new widget.PaneToggle();
        btnKonfirmasi = new widget.Button();
        btnBatal = new widget.Button();

        dlgAksiFP.setUndecorated(false);

        judulAksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        judulAksi.setText("KONFIRMASI AKSI");
        judulAksi.setFont(new java.awt.Font("Inter", 1, 18)); // NOI18N
        judulAksi.setPreferredSize(new java.awt.Dimension(400, 30));
        dlgAksiFP.getContentPane().add(judulAksi, java.awt.BorderLayout.PAGE_START);

        panelTengahAksi.setOpaque(false);
        panelTengahAksi.setLayout(null);

        userAksi.setForeground(new java.awt.Color(40, 40, 40));
        userAksi.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        userAksi.setPreferredSize(new java.awt.Dimension(270, 30));
        userAksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                userAksiKeyPressed(evt);
            }
        });
        panelTengahAksi.add(userAksi);
        userAksi.setBounds(120, 30, 270, 30);

        passAksi.setForeground(new java.awt.Color(40, 40, 40));
        passAksi.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        passAksi.setPreferredSize(new java.awt.Dimension(270, 30));
        passAksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passAksiKeyPressed(evt);
            }
        });
        panelTengahAksi.add(passAksi);
        passAksi.setBounds(120, 70, 270, 30);

        label1.setText("User ID :");
        label1.setFocusable(false);
        label1.setPreferredSize(new java.awt.Dimension(60, 30));
        panelTengahAksi.add(label1);
        label1.setBounds(0, 30, 110, 30);

        label2.setText("Password :");
        label2.setFocusable(false);
        label2.setPreferredSize(new java.awt.Dimension(60, 30));
        panelTengahAksi.add(label2);
        label2.setBounds(0, 70, 110, 30);

        dlgAksiFP.getContentPane().add(panelTengahAksi, java.awt.BorderLayout.CENTER);

        panelBawahAksi.setOpaque(false);
        panelBawahAksi.setPreferredSize(new java.awt.Dimension(50, 50));

        btnAksiKonfirmasi.setText("KONFIRMASI");
        btnAksiKonfirmasi.setFont(new java.awt.Font("Inter", 1, 14)); // NOI18N
        btnAksiKonfirmasi.setPreferredSize(new java.awt.Dimension(140, 35));
        btnAksiKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiKonfirmasiActionPerformed(evt);
            }
        });
        panelBawahAksi.add(btnAksiKonfirmasi);

        btnAksiBatal.setBackground(new java.awt.Color(255, 255, 255));
        btnAksiBatal.setForeground(new java.awt.Color(255, 23, 26));
        btnAksiBatal.setText("Batal");
        btnAksiBatal.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        btnAksiBatal.setPreferredSize(new java.awt.Dimension(90, 35));
        btnAksiBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAksiBatalActionPerformed(evt);
            }
        });
        panelBawahAksi.add(btnAksiBatal);

        dlgAksiFP.getContentPane().add(panelBawahAksi, java.awt.BorderLayout.PAGE_END);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelAtas.setMaximumSize(new java.awt.Dimension(32767, 25));
        panelAtas.setMinimumSize(new java.awt.Dimension(1280, 25));
        panelAtas.setPreferredSize(new java.awt.Dimension(1280, 25));
        panelAtas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        label4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label4.setText("DATA ELIGIBILITAS PESERTA JKN");
        label4.setFocusable(false);
        label4.setMaximumSize(new java.awt.Dimension(32767, 25));
        label4.setMinimumSize(new java.awt.Dimension(340, 25));
        label4.setPreferredSize(new java.awt.Dimension(340, 25));
        panelAtas.add(label4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(panelAtas, gridBagConstraints);

        emptyKiri.setMinimumSize(new java.awt.Dimension(0, 0));
        emptyKiri.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(emptyKiri, gridBagConstraints);

        panelTengah.setMinimumSize(new java.awt.Dimension(1280, 620));
        panelTengah.setPreferredSize(new java.awt.Dimension(1280, 620));
        panelTengah.setLayout(new java.awt.GridBagLayout());

        panelUtama.setMinimumSize(new java.awt.Dimension(1280, 560));
        panelUtama.setPreferredSize(new java.awt.Dimension(1280, 560));
        panelUtama.setLayout(null);

        namaPasien.setEditable(false);
        panelUtama.add(namaPasien);
        namaPasien.setBounds(380, 10, 895, 40);

        noRM.setEditable(false);
        panelUtama.add(noRM);
        noRM.setBounds(235, 10, 140, 40);

        noPeserta.setEditable(false);
        panelUtama.add(noPeserta);
        noPeserta.setBounds(880, 145, 220, 40);

        jLabel20.setText("Tgl. SEP :");
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel20);
        jLabel20.setBounds(745, 280, 130, 40);

        jLabel22.setText("Tgl. Rujukan :");
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel22);
        jLabel22.setBounds(745, 235, 130, 40);

        jLabel23.setText("No. SKDP / Surat Kontrol :");
        panelUtama.add(jLabel23);
        jLabel23.setBounds(5, 100, 225, 40);

        noRujukan.setEditable(false);
        panelUtama.add(noRujukan);
        noRujukan.setBounds(235, 145, 455, 40);

        jLabel10.setText("PPK Rujukan :");
        panelUtama.add(jLabel10);
        jLabel10.setBounds(5, 190, 225, 40);

        kodePPKRujukan.setEditable(false);
        panelUtama.add(kodePPKRujukan);
        kodePPKRujukan.setBounds(235, 190, 115, 40);

        namaPPKRujukan.setEditable(false);
        panelUtama.add(namaPPKRujukan);
        namaPPKRujukan.setBounds(355, 190, 335, 40);

        jLabel11.setText("Diagnosa Awal :");
        panelUtama.add(jLabel11);
        jLabel11.setBounds(5, 235, 225, 40);

        kodeDiagnosa.setEditable(false);
        panelUtama.add(kodeDiagnosa);
        kodeDiagnosa.setBounds(235, 235, 115, 40);

        namaDiagnosa.setEditable(false);
        panelUtama.add(namaDiagnosa);
        namaDiagnosa.setBounds(355, 235, 335, 40);

        namaPoli.setEditable(false);
        panelUtama.add(namaPoli);
        namaPoli.setBounds(235, 280, 455, 40);

        LabelPoli.setText("Poli Tujuan :");
        panelUtama.add(LabelPoli);
        LabelPoli.setBounds(5, 280, 225, 40);

        LabelKelas.setText("Kelas :");
        panelUtama.add(LabelKelas);
        LabelKelas.setBounds(5, 370, 225, 40);

        kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        kelas.setSelectedIndex(2);
        panelUtama.add(kelas);
        kelas.setBounds(235, 370, 150, 40);

        jLabel8.setText("Data Pasien :");
        panelUtama.add(jLabel8);
        jLabel8.setBounds(5, 10, 225, 40);

        tglLahir.setEditable(false);
        panelUtama.add(tglLahir);
        tglLahir.setBounds(235, 55, 140, 40);

        jLabel18.setText("L / P :");
        panelUtama.add(jLabel18);
        jLabel18.setBounds(1160, 100, 55, 40);

        jk.setEditable(false);
        panelUtama.add(jk);
        jk.setBounds(1220, 100, 55, 40);

        jLabel24.setText("Jenis Peserta :");
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel24);
        jLabel24.setBounds(745, 55, 130, 40);

        jenisPeserta.setEditable(false);
        panelUtama.add(jenisPeserta);
        jenisPeserta.setBounds(880, 55, 395, 40);

        jLabel25.setText("Status :");
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel25);
        jLabel25.setBounds(385, 55, 70, 40);

        statusPeserta.setEditable(false);
        panelUtama.add(statusPeserta);
        statusPeserta.setBounds(460, 55, 230, 40);

        jLabel27.setText("Asal Rujukan :");
        panelUtama.add(jLabel27);
        jLabel27.setBounds(745, 190, 130, 40);

        asalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        panelUtama.add(asalRujukan);
        asalRujukan.setBounds(880, 190, 220, 40);

        noTelp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                noTelpFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                noTelpFocusLost(evt);
            }
        });
        noTelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                noTelpMouseClicked(evt);
            }
        });
        panelUtama.add(noTelp);
        noTelp.setBounds(880, 370, 220, 40);

        katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        panelUtama.add(katarak);
        katarak.setBounds(880, 325, 220, 40);

        jLabel37.setText("Katarak :");
        panelUtama.add(jLabel37);
        jLabel37.setBounds(745, 325, 130, 40);

        LabelPoli2.setText("Dokter Tujuan :");
        panelUtama.add(LabelPoli2);
        LabelPoli2.setBounds(5, 325, 225, 40);

        namaDokter.setEditable(false);
        panelUtama.add(namaDokter);
        namaDokter.setBounds(235, 325, 455, 40);

        cariDokter.setBackground(new java.awt.Color(240, 249, 255));
        cariDokter.setBorder(null);
        cariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDokter.setToolTipText("Referensi Dokter");
        cariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDokterActionPerformed(evt);
            }
        });
        panelUtama.add(cariDokter);
        cariDokter.setBounds(695, 325, 45, 40);

        jLabel56.setText("No. Telp :");
        jLabel56.setPreferredSize(new java.awt.Dimension(55, 23));
        panelUtama.add(jLabel56);
        jLabel56.setBounds(745, 370, 130, 40);

        jLabel12.setText("Tgl. Lahir :");
        panelUtama.add(jLabel12);
        jLabel12.setBounds(5, 55, 225, 40);

        jLabel6.setText("NIK :");
        panelUtama.add(jLabel6);
        jLabel6.setBounds(745, 100, 130, 40);

        noSKDP.setEditable(false);
        panelUtama.add(noSKDP);
        noSKDP.setBounds(235, 100, 455, 40);

        jLabel26.setText("No. Rujukan :");
        panelUtama.add(jLabel26);
        jLabel26.setBounds(5, 145, 225, 40);

        nik.setEditable(false);
        panelUtama.add(nik);
        nik.setBounds(880, 100, 275, 40);

        jLabel7.setText("No. Peserta :");
        panelUtama.add(jLabel7);
        jLabel7.setBounds(745, 145, 130, 40);

        cariPoli.setBackground(new java.awt.Color(240, 249, 255));
        cariPoli.setBorder(null);
        cariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariPoli.setToolTipText("Referensi Poli BPJS");
        cariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariPoliActionPerformed(evt);
            }
        });
        panelUtama.add(cariPoli);
        cariPoli.setBounds(695, 280, 45, 40);

        cariDiagnosa.setBackground(new java.awt.Color(240, 249, 255));
        cariDiagnosa.setBorder(null);
        cariDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariDiagnosa.setToolTipText("Referensi Diagnosa BPJS");
        cariDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariDiagnosaActionPerformed(evt);
            }
        });
        panelUtama.add(cariDiagnosa);
        cariDiagnosa.setBounds(695, 235, 45, 40);

        cariNoRujukan.setBackground(new java.awt.Color(240, 249, 255));
        cariNoRujukan.setBorder(null);
        cariNoRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        cariNoRujukan.setToolTipText("Daftar Rujukan Pasien");
        cariNoRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariNoRujukanActionPerformed(evt);
            }
        });
        panelUtama.add(cariNoRujukan);
        cariNoRujukan.setBounds(695, 145, 45, 40);

        btnRiwayatPelayanan.setBackground(new java.awt.Color(240, 249, 255));
        btnRiwayatPelayanan.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnRiwayatPelayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnRiwayatPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatPelayananActionPerformed(evt);
            }
        });
        panelUtama.add(btnRiwayatPelayanan);
        btnRiwayatPelayanan.setBounds(1105, 145, 45, 40);

        btnFingerprint.setBackground(new java.awt.Color(255, 255, 255));
        btnFingerprint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerprint.setText("<html><body style=\"text-align: center\">\nREKAM<br>SIDIK JARI\n</body></html>"); // NOI18N
        btnFingerprint.setFont(new java.awt.Font("Inter", 1, 24)); // NOI18N
        btnFingerprint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFingerprint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFingerprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerprintActionPerformed(evt);
            }
        });
        panelUtama.add(btnFingerprint);
        btnFingerprint.setBounds(645, 480, 160, 120);

        btnFrista.setBackground(new java.awt.Color(255, 255, 255));
        btnFrista.setForeground(new java.awt.Color(0, 131, 62));
        btnFrista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/face-scan.png"))); // NOI18N
        btnFrista.setText("<html><body style=\"text-align: center\">\nREKAM<br>WAJAH\n</body></html>"); // NOI18N
        btnFrista.setFont(new java.awt.Font("Inter", 1, 24)); // NOI18N
        btnFrista.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFrista.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFrista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFristaActionPerformed(evt);
            }
        });
        panelUtama.add(btnFrista);
        btnFrista.setBounds(475, 480, 160, 120);

        panelNumpad.setFocusable(false);
        panelNumpad.setFontSize(36);
        panelNumpad.setTextBox(noTelp);
        panelUtama.add(panelNumpad);
        panelNumpad.setBounds(880, 415, 255, 340);

        tglRujukan.setEditable(false);
        panelUtama.add(tglRujukan);
        tglRujukan.setBounds(880, 235, 220, 40);

        tglSEP.setEditable(false);
        panelUtama.add(tglSEP);
        tglSEP.setBounds(880, 280, 220, 40);

        labelValidasi.setForeground(new java.awt.Color(255, 30, 0));
        labelValidasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelValidasi.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelValidasi.setFont(new java.awt.Font("Inter", 1, 24)); // NOI18N
        panelUtama.add(labelValidasi);
        labelValidasi.setBounds(235, 415, 520, 90);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panelTengah.add(panelUtama, gridBagConstraints);

        panelTambahan.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(0, 131, 62)));
        panelTambahan.setMinimumSize(new java.awt.Dimension(1280, 570));
        panelTambahan.setOpaque(false);
        panelTambahan.setPreferredSize(new java.awt.Dimension(1280, 570));
        panelTambahan.setLayout(null);

        jLabel13.setText("Jenis Pelayanan :");
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel13);
        jLabel13.setBounds(5, 10, 225, 40);

        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel42);
        jLabel42.setBounds(5, 55, 225, 40);

        tujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        tujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tujuanKunjunganItemStateChanged(evt);
            }
        });
        panelTambahan.add(tujuanKunjungan);
        tujuanKunjungan.setBounds(235, 55, 455, 40);

        jLabel43.setText("Flag Prosedur :");
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel43);
        jLabel43.setBounds(5, 100, 225, 40);

        flagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        flagProsedur.setEnabled(false);
        panelTambahan.add(flagProsedur);
        flagProsedur.setBounds(235, 100, 455, 40);

        jLabel44.setText("Penunjang :");
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel44);
        jLabel44.setBounds(5, 145, 225, 40);

        penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        penunjang.setEnabled(false);
        panelTambahan.add(penunjang);
        penunjang.setBounds(235, 145, 455, 40);

        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel45);
        jLabel45.setBounds(5, 190, 225, 40);

        asesmenPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        panelTambahan.add(asesmenPelayanan);
        asesmenPelayanan.setBounds(235, 190, 455, 40);

        LabelPoli7.setText("DPJP Layanan :");
        LabelPoli7.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(LabelPoli7);
        LabelPoli7.setBounds(5, 235, 225, 40);

        kodeDPJPLayanan.setEditable(false);
        panelTambahan.add(kodeDPJPLayanan);
        kodeDPJPLayanan.setBounds(235, 235, 115, 40);

        namaDPJPLayanan.setEditable(false);
        panelTambahan.add(namaDPJPLayanan);
        namaDPJPLayanan.setBounds(355, 235, 335, 40);

        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel9);
        jLabel9.setBounds(5, 280, 225, 40);

        kodePPK.setEditable(false);
        panelTambahan.add(kodePPK);
        kodePPK.setBounds(235, 280, 115, 40);

        namaPPK.setEditable(false);
        panelTambahan.add(namaPPK);
        namaPPK.setBounds(355, 280, 335, 40);

        jLabel55.setText("Laka Lantas :");
        panelTambahan.add(jLabel55);
        jLabel55.setBounds(725, 10, 150, 40);

        lakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        lakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        lakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lakaLantasItemStateChanged(evt);
            }
        });
        panelTambahan.add(lakaLantas);
        lakaLantas.setBounds(880, 10, 220, 40);

        tglKLL.setEditable(false);
        tglKLL.setEnabled(false);
        tglKLL.setPreferredSize(new java.awt.Dimension(64, 25));
        panelTambahan.add(tglKLL);
        tglKLL.setBounds(1105, 10, 170, 40);

        jLabel36.setText("Keterangan :");
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel36);
        jLabel36.setBounds(725, 55, 150, 40);

        keterangan.setEditable(false);
        panelTambahan.add(keterangan);
        keterangan.setBounds(880, 55, 395, 40);

        jLabel40.setText("Suplesi :");
        panelTambahan.add(jLabel40);
        jLabel40.setBounds(725, 100, 150, 40);

        suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        suplesi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                suplesiItemStateChanged(evt);
            }
        });
        panelTambahan.add(suplesi);
        suplesi.setBounds(880, 100, 140, 40);

        jLabel41.setText("No. SEP :");
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        panelTambahan.add(jLabel41);
        jLabel41.setBounds(725, 145, 150, 40);

        noSEPSuplesi.setEditable(false);
        panelTambahan.add(noSEPSuplesi);
        noSEPSuplesi.setBounds(880, 145, 395, 40);

        LabelPoli3.setText("Propinsi KLL :");
        panelTambahan.add(LabelPoli3);
        LabelPoli3.setBounds(725, 190, 150, 40);

        kdPropKLL.setEditable(false);
        panelTambahan.add(kdPropKLL);
        kdPropKLL.setBounds(880, 190, 115, 40);

        nmPropKLL.setEditable(false);
        panelTambahan.add(nmPropKLL);
        nmPropKLL.setBounds(1000, 190, 275, 40);

        LabelPoli4.setText("Kabupaten KLL :");
        panelTambahan.add(LabelPoli4);
        LabelPoli4.setBounds(725, 235, 150, 40);

        kdKabKLL.setEditable(false);
        panelTambahan.add(kdKabKLL);
        kdKabKLL.setBounds(880, 235, 115, 40);

        nmKabKLL.setEditable(false);
        panelTambahan.add(nmKabKLL);
        nmKabKLL.setBounds(1000, 235, 275, 40);

        LabelPoli5.setText("Kecamatan KLL :");
        panelTambahan.add(LabelPoli5);
        LabelPoli5.setBounds(725, 280, 150, 40);

        kdKecKLL.setEditable(false);
        panelTambahan.add(kdKecKLL);
        kdKecKLL.setBounds(880, 280, 115, 40);

        nmKecKLL.setEditable(false);
        panelTambahan.add(nmKecKLL);
        nmKecKLL.setBounds(1000, 280, 275, 40);

        jLabel14.setText("Catatan :");
        panelTambahan.add(jLabel14);
        jLabel14.setBounds(725, 325, 150, 40);

        catatan.setText("Anjungan Pasien Mandiri RS Samarinda Medika Citra");
        panelTambahan.add(catatan);
        catatan.setBounds(880, 325, 395, 40);

        btnApprovalFP.setBackground(new java.awt.Color(255, 255, 255));
        btnApprovalFP.setForeground(new java.awt.Color(0, 131, 62));
        btnApprovalFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/approvalfp.png"))); // NOI18N
        btnApprovalFP.setText("Approval FP");
        btnApprovalFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnApprovalFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnApprovalFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApprovalFPActionPerformed(evt);
            }
        });
        panelTambahan.add(btnApprovalFP);
        btnApprovalFP.setBounds(645, 380, 160, 90);

        btnPengajuanFP.setBackground(new java.awt.Color(255, 255, 255));
        btnPengajuanFP.setForeground(new java.awt.Color(0, 131, 62));
        btnPengajuanFP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pengajuan.png"))); // NOI18N
        btnPengajuanFP.setText("Pengajuan FP");
        btnPengajuanFP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPengajuanFP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPengajuanFP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPengajuanFPActionPerformed(evt);
            }
        });
        panelTambahan.add(btnPengajuanFP);
        btnPengajuanFP.setBounds(475, 380, 160, 90);

        jLabel15.setText("Jumlah Barcode :");
        panelTambahan.add(jLabel15);
        jLabel15.setBounds(5, 325, 225, 40);

        barcode.setText("3");
        panelTambahan.add(barcode);
        barcode.setBounds(235, 325, 50, 40);

        jenisPelayanan.setEditable(false);
        jenisPelayanan.setText("2. Ralan");
        jenisPelayanan.setToolTipText("");
        panelTambahan.add(jenisPelayanan);
        jenisPelayanan.setBounds(235, 10, 150, 40);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTengah.add(panelTambahan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panelTengah, gridBagConstraints);

        emptyKanan.setMinimumSize(new java.awt.Dimension(0, 0));
        emptyKanan.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(emptyKanan, gridBagConstraints);

        panelBawah.setMinimumSize(new java.awt.Dimension(1280, 120));
        panelBawah.setPreferredSize(new java.awt.Dimension(1280, 120));
        panelBawah.setLayout(new java.awt.GridBagLayout());

        toggleInfoTambahan.setForeground(new java.awt.Color(150, 155, 159));
        toggleInfoTambahan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        toggleInfoTambahan.setToolTipText("Alt+I");
        toggleInfoTambahan.setMaximumSize(new java.awt.Dimension(32767, 30));
        toggleInfoTambahan.setMinimumSize(new java.awt.Dimension(140, 30));
        toggleInfoTambahan.setPreferredSize(new java.awt.Dimension(140, 30));
        toggleInfoTambahan.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        toggleInfoTambahan.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        toggleInfoTambahan.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        toggleInfoTambahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleInfoTambahanActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        panelBawah.add(toggleInfoTambahan, gridBagConstraints);

        btnKonfirmasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnKonfirmasi.setText("KONFIRMASI");
        btnKonfirmasi.setToolTipText("Alt+S");
        btnKonfirmasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKonfirmasi.setMinimumSize(new java.awt.Dimension(300, 60));
        btnKonfirmasi.setMinimumWidth(300);
        btnKonfirmasi.setPreferredSize(new java.awt.Dimension(300, 60));
        btnKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKonfirmasiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 5);
        panelBawah.add(btnKonfirmasi, gridBagConstraints);

        btnBatal.setBackground(new java.awt.Color(255, 255, 255));
        btnBatal.setForeground(new java.awt.Color(255, 33, 32));
        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/exit.png"))); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.setToolTipText("Alt+K");
        btnBatal.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        btnBatal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnBatal.setMinimumSize(new java.awt.Dimension(300, 60));
        btnBatal.setMinimumWidth(300);
        btnBatal.setPreferredSize(new java.awt.Dimension(300, 60));
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        panelBawah.add(btnBatal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(panelBawah, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKonfirmasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekStatusFinger();
        if (namaPasien.getText().isBlank()) {
            Valid.textKosong(namaPasien, "Pasien");
        } else if (noPeserta.getText().isBlank()) {
            Valid.textKosong(noPeserta, "Nomor Kartu");
        } else if (!Sequel.cariExistsSmc("select * from pasien where no_rkm_medis = ?", noRM.getText())) {
            Valid.popupPeringatanDialog("Maaf, No. RM. tidak sesuai..!!");
        } else if (kodePPKRujukan.getText().isBlank() || namaPPKRujukan.getText().isBlank()) {
            Valid.textKosong(kodePPKRujukan, "PPK Rujukan");
        } else if (kodePPK.getText().isBlank() || namaPPK.getText().isBlank()) {
            Valid.textKosong(kodePPK, "PPK Pelayanan");
        } else if (kodeDiagnosa.getText().isBlank() || namaDiagnosa.getText().isBlank()) {
            Valid.textKosong(kodeDiagnosa, "Diagnosa");
        } else if (catatan.getText().isBlank()) {
            Valid.textKosong(catatan, "Catatan");
        } else if (namaPoli.getText().isBlank()) {
            Valid.textKosong(namaPoli, "Poli Tujuan");
        } else if ((lakaLantas.getSelectedIndex() == 1) && keterangan.getText().isBlank()) {
            Valid.textKosong(keterangan, "Keterangan");
        } else if (kodeDokter.isBlank() || namaDokter.getText().isBlank()) {
            Valid.textKosong(namaDokter, "DPJP");
        } else if (!statusFinger && Sequel.cariIntegerSmc("select timestampdiff(year, ?, CURRENT_DATE())", tglLahir.getText()) >= 17 && !namaPoli.getText().toLowerCase().contains("darurat")) {
            Valid.popupPeringatanDialog("Silahkan lakukan validasi biometrik dahulu..!!", 3);
        } else {
            if (kodePoliReg.isBlank()) {
                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli);
            }
            if (kodeDokterReg.isBlank()) {
                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
            }
            if (kodePoliReg.isBlank() || kodeDokterReg.isBlank()) {
                Valid.popupPeringatanDialog("Mapping Poliklinik atau Dokter tidak ditemukan..!!", 5);
            } else {
                if (!registerPasien()) {
                    Valid.popupGagalDialog("Terjadi kesalahan pada saat pendaftaran pasien!");
                } else {
                    if (namaPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = '2' and tglsep = ? and nmpolitujuan like '%darurat%'", noPeserta.getText(), tglSEP.getText()) >= 3) {
                            Valid.popupPeringatanDialog("Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP\ndi jenis pelayanan yang sama..!!", 5);
                        } else {
                            if (kirimAntrianOnsite()) {
                                insertSEP();
                            }
                        }
                    } else {
                        if (Sequel.cariIntegerSmc("select count(*) from bridging_sep where no_kartu = ? and jnspelayanan = '2' and tglsep = ? and nmpolitujuan not like '%darurat%'", noPeserta.getText(), tglSEP.getText()) >= 1) {
                            Valid.popupPeringatanDialog("Maaf, sebelumnya sudah dilakukan pembuatan SEP\ndi jenis pelayanan yang sama..!!", 5);
                        } else {
                            if (kirimAntrianOnsite()) {
                                insertSEP();
                            }
                        }
                    }
                }
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnKonfirmasiActionPerformed

    private void cariDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDokterActionPerformed
        dokter.setSize(getContentPane().getSize());
        dokter.setLocationRelativeTo(getContentPane());
        dokter.carinamadokter(kodePoli, namaPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_cariDokterActionPerformed

    private void tujuanKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tujuanKunjunganItemStateChanged
        if (tujuanKunjungan.getSelectedIndex() == 0) {
            flagProsedur.setEnabled(false);
            flagProsedur.setSelectedIndex(0);
            penunjang.setEnabled(false);
            penunjang.setSelectedIndex(0);
            asesmenPelayanan.setEnabled(true);
        } else {
            if (tujuanKunjungan.getSelectedIndex() == 1) {
                asesmenPelayanan.setSelectedIndex(0);
                asesmenPelayanan.setEnabled(false);
            } else {
                asesmenPelayanan.setEnabled(true);
            }
            if (flagProsedur.getSelectedIndex() == 0) {
                flagProsedur.setSelectedIndex(2);
            }
            flagProsedur.setEnabled(true);
            if (penunjang.getSelectedIndex() == 0) {
                penunjang.setSelectedIndex(10);
            }
            penunjang.setEnabled(true);
        }
    }//GEN-LAST:event_tujuanKunjunganItemStateChanged

    private void lakaLantasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lakaLantasItemStateChanged
        if (lakaLantas.getSelectedIndex() == 0) {
            tglKLL.setEnabled(false);
            keterangan.setEditable(false);
            keterangan.setText("");
        } else {
            tglKLL.setEnabled(true);
            keterangan.setEditable(true);
        }
    }//GEN-LAST:event_lakaLantasItemStateChanged

    private void cariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariPoliActionPerformed
        if (REGISTRASISATUJAMSEBELUMJAMPRAKTEK) {
            poli.setHari(tglSEP.getText());
            poli.setDokter(Sequel.cariIsiSmc("select maping_dokter_dpjpvclaim.kd_dokter from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs = ?", kodeDokter));
        }
        poli.setSize(getContentPane().getSize());
        poli.setLocationRelativeTo(getContentPane());
        poli.setVisible(true);
    }//GEN-LAST:event_cariPoliActionPerformed

    private void cariDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariDiagnosaActionPerformed
        diagnosa.setSize(getContentPane().getSize());
        diagnosa.setLocationRelativeTo(getContentPane());
        diagnosa.setVisible(true);
    }//GEN-LAST:event_cariDiagnosaActionPerformed

    private void cariNoRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariNoRujukanActionPerformed
        if (noPeserta.getText().isBlank()) {
            Valid.popupPeringatanDialog("No. Kartu masih kosong..!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            riwayatRujukan.setSize(getContentPane().getSize());
            riwayatRujukan.setLocationRelativeTo(getContentPane());
            riwayatRujukan.tampil(noPeserta.getText(), namaPasien.getText());
            riwayatRujukan.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_cariNoRujukanActionPerformed

    private void btnRiwayatPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatPelayananActionPerformed
        if (noPeserta.getText().isBlank()) {
            Valid.popupPeringatanDialog("No. Kartu masih kosong..!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            riwayatPelayanan.setSize(getContentPane().getSize());
            riwayatPelayanan.setLocationRelativeTo(getContentPane());
            riwayatPelayanan.setKartu(noPeserta.getText());
            riwayatPelayanan.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnRiwayatPelayananActionPerformed

    private void btnApprovalFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApprovalFPActionPerformed
        resetAksi();
        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            aksi = "Approval";
            dlgAksiFP.setSize(415, 250);
            dlgAksiFP.setLocationRelativeTo(null);
            dlgAksiFP.setVisible(true);
        }
    }//GEN-LAST:event_btnApprovalFPActionPerformed

    private void btnPengajuanFPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengajuanFPActionPerformed
        resetAksi();
        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            aksi = "Pengajuan";
            dlgAksiFP.setSize(415, 250);
            dlgAksiFP.setLocationRelativeTo(null);
            dlgAksiFP.setVisible(true);
        }
    }//GEN-LAST:event_btnPengajuanFPActionPerformed

    private void btnAksiKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiKonfirmasiActionPerformed
        if (noPeserta.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, No. Kartu Peserta tidak ada...!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try (PreparedStatement ps = koneksi.prepareStatement("select id_user from user where id_user = aes_encrypt(?, 'nur') and password = aes_encrypt(?, 'windi') limit 1")) {
                ps.setString(1, new String(userAksi.getPassword()));
                ps.setString(2, new String(passAksi.getPassword()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        switch (aksi) {
                            case "Pengajuan":
                                try {
                                    url = koneksiDB.URLAPIBPJS() + "/Sep/pengajuanSEP";
                                    System.out.println("URL : " + url);
                                    System.out.print("Pengajuan FP : ");
                                    utc = api.getUTCDateTimeAsString();
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                    payload = " {" +
                                        "\"request\": {" +
                                        "\"t_sep\": {" +
                                        "\"noKartu\": \"" + noPeserta.getText() + "\"," +
                                        "\"tglSep\": \"" + tglSEP.getText() + "\"," +
                                        "\"jnsPelayanan\": \"2\"," +
                                        "\"jnsPengajuan\": \"2\"," +
                                        "\"keterangan\": \"Pengajuan SEP Finger oleh Anjungan Pasien Mandiri RS Samarinda Medika Citra\"," +
                                        "\"user\": \"NoRM:" + noRM.getText() + "\"" +
                                        "}" +
                                        "}" +
                                        "}";
                                    entity = new HttpEntity(payload, headers);
                                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                    if (metadata.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Pengajuan Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Notif : " + ex);
                                    if (ex.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                                break;
                            case "Approval":
                                try {
                                    url = koneksiDB.URLAPIBPJS() + "/Sep/aprovalSEP";
                                    System.out.println("URL : " + url);
                                    System.out.print("Approval FP : ");
                                    utc = api.getUTCDateTimeAsString();
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                    headers.add("X-Timestamp", utc);
                                    headers.add("X-Signature", api.getHmac(utc));
                                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                    payload = " {" +
                                        "\"request\": {" +
                                        "\"t_sep\": {" +
                                        "\"noKartu\": \"" + noPeserta.getText() + "\"," +
                                        "\"tglSep\": \"" + tglSEP.getText() + "\"," +
                                        "\"jnsPelayanan\": \"2\"," +
                                        "\"jnsPengajuan\": \"2\"," +
                                        "\"keterangan\": \"Approval FingerPrint karena Gagal FP melalui Anjungan Pasien Mandiri\"," +
                                        "\"user\": \"NoRM:" + noRM.getText() + "\"" +
                                        "}" +
                                        "}" +
                                        "}";
                                    entity = new HttpEntity(payload, headers);
                                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                                    metadata = root.path("metaData");
                                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                    if (metadata.path("code").asText().equals("200")) {
                                        JOptionPane.showMessageDialog(null, "Approval Berhasil");
                                    } else {
                                        JOptionPane.showMessageDialog(null, metadata.path("message").asText());
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Notif : " + ex);
                                    if (ex.toString().contains("UnknownHostException")) {
                                        JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
                                    }
                                }
                                break;
                            default:
                                JOptionPane.showMessageDialog(null, "Anda tidak diizinkan untuk melakukan aksi ini...!!!");
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada saat memproses aksi...!!!");
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnAksiKonfirmasiActionPerformed

    private void userAksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userAksiKeyPressed
        Valid.pindah(evt, btnAksiBatal, passAksi);
    }//GEN-LAST:event_userAksiKeyPressed

    private void passAksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passAksiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAksiKonfirmasiActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            userAksi.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            btnAksiKonfirmasi.requestFocus();
        }
    }//GEN-LAST:event_passAksiKeyPressed

    private void btnAksiBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAksiBatalActionPerformed
        resetAksi();
        dlgAksiFP.dispose();
    }//GEN-LAST:event_btnAksiBatalActionPerformed

    private void btnFingerprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerprintActionPerformed
        if (noPeserta.getText().isBlank()) {
            Valid.popupPeringatanDialog("No. Kartu masih kosong..!!");
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    fingerprintAktif = false;
                    User32 u32 = User32.INSTANCE;

                    u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                        char[] windowText = new char[512];
                        u32.GetWindowText(hwnd, windowText, 512);
                        String wText = Native.toString(windowText);

                        if (wText.isEmpty()) {
                            return true;
                        }

                        if (wText.contains("Registrasi Sidik Jari")) {
                            DlgRegistrasiBPJS.this.fingerprintAktif = true;
                            u32.SetForegroundWindow(hwnd);
                        }

                        return true;
                    }, Pointer.NULL);

                    Robot r = new Robot();
                    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection ss;

                    if (fingerprintAktif) {
                        Thread.sleep(1000);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_A);
                        r.keyRelease(KeyEvent.VK_A);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                        Thread.sleep(500);

                        ss = new StringSelection(noPeserta.getText().trim());
                        c.setContents(ss, ss);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                    } else {
                        Runtime.getRuntime().exec("\"" + koneksiDB.URLAPLIKASIFINGERPRINTBPJS() + "\"");
                        Thread.sleep(2000);
                        ss = new StringSelection(koneksiDB.USERFINGERPRINTBPJS());
                        c.setContents(ss, ss);

                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_TAB);
                        r.keyRelease(KeyEvent.VK_TAB);
                        Thread.sleep(1000);

                        ss = new StringSelection(koneksiDB.PASSWORDFINGERPRINTBPJS());
                        c.setContents(ss, ss);

                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_ENTER);
                        r.keyRelease(KeyEvent.VK_ENTER);
                        Thread.sleep(1000);

                        ss = new StringSelection(noPeserta.getText().trim());
                        c.setContents(ss, ss);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            });
        }
    }//GEN-LAST:event_btnFingerprintActionPerformed

    private void btnFristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFristaActionPerformed
        if (noPeserta.getText().isBlank()) {
            Valid.popupPeringatanDialog("No. Kartu masih kosong..!!");
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    fristaAktif = false;
                    User32 u32 = User32.INSTANCE;

                    u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                        char[] windowText = new char[512];
                        u32.GetWindowText(hwnd, windowText, 512);
                        String wText = Native.toString(windowText);

                        if (wText.isEmpty()) {
                            return true;
                        }

                        if (wText.toLowerCase().contains("face recognition bpjs kesehatan")) {
                            DlgRegistrasiBPJS.this.fristaAktif = true;
                            u32.SetForegroundWindow(hwnd);
                            u32.ShowWindow(hwnd, User32.SW_MAXIMIZE);
                            u32.ShowWindow(hwnd, User32.SW_RESTORE);
                            u32.SetFocus(hwnd);
                        }

                        return true;
                    }, Pointer.NULL);

                    Robot r = new Robot();
                    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection ss;

                    if (fristaAktif) {
                        Thread.sleep(1000);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_A);
                        r.keyRelease(KeyEvent.VK_A);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                        Thread.sleep(200);

                        ss = new StringSelection(noPeserta.getText());
                        c.setContents(ss, ss);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                    } else {
                        Runtime.getRuntime().exec("\"" + koneksiDB.URLAPLIKASIFRISTABPJS() + "\"");
                        while (true) {
                            if (u32.IsWindowVisible(u32.FindWindow(null, "Login Frista (Face Recognition BPJS Kesehatan)"))) {
                                fristaAktif = true;
                                break;
                            }
                        }
                        Thread.sleep(1000);

                        ss = new StringSelection(koneksiDB.USERFINGERPRINTBPJS());
                        c.setContents(ss, ss);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_TAB);
                        r.keyRelease(KeyEvent.VK_TAB);
                        Thread.sleep(1000);

                        ss = new StringSelection(koneksiDB.PASSWORDFINGERPRINTBPJS());
                        c.setContents(ss, ss);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_TAB);
                        r.keyRelease(KeyEvent.VK_TAB);
                        r.keyPress(KeyEvent.VK_SPACE);
                        r.keyRelease(KeyEvent.VK_SPACE);
                        Thread.sleep(1000);

                        u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
                            char[] windowText = new char[512];
                            u32.GetWindowText(hwnd, windowText, 512);
                            String wText = Native.toString(windowText);

                            if (wText.isEmpty()) {
                                return true;
                            }

                            if (wText.toLowerCase().contains("face recognition bpjs kesehatan")) {
                                u32.SetForegroundWindow(hwnd);
                                u32.ShowWindow(hwnd, User32.SW_MAXIMIZE);
                                u32.ShowWindow(hwnd, User32.SW_RESTORE);
                                u32.SetFocus(hwnd);
                            }

                            return true;
                        }, Pointer.NULL);
                        Thread.sleep(1000);

                        ss = new StringSelection(noPeserta.getText());
                        c.setContents(ss, ss);
                        r.keyPress(KeyEvent.VK_CONTROL);
                        r.keyPress(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_V);
                        r.keyRelease(KeyEvent.VK_CONTROL);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            });
        }
    }//GEN-LAST:event_btnFristaActionPerformed

    private void toggleInfoTambahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleInfoTambahanActionPerformed
        isForm();
    }//GEN-LAST:event_toggleInfoTambahanActionPerformed

    private void noTelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_noTelpMouseClicked
        if (!toggleInfoTambahan.isSelected() && bisaTampilkanNumpad) {
            panelNumpad.setVisible(true);
        }
    }//GEN-LAST:event_noTelpMouseClicked

    private void suplesiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_suplesiItemStateChanged
        if (suplesi.getSelectedIndex() == 1) {
            noSEPSuplesi.setEditable(true);
        } else {
            noSEPSuplesi.setEditable(false);
            noSEPSuplesi.setText("");
        }
    }//GEN-LAST:event_suplesiItemStateChanged

    private void noTelpFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusLost
        panelNumpad.setVisible(false);
    }//GEN-LAST:event_noTelpFocusLost

    private void noTelpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noTelpFocusGained
        EventQueue.invokeLater(() -> {
            noTelp.setCaretPosition(noTelp.getText().length());
        });
    }//GEN-LAST:event_noTelpFocusGained

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        isForm();
    }//GEN-LAST:event_formWindowActivated

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli7;
    private widget.ComboBox asalRujukan;
    private widget.ComboBox asesmenPelayanan;
    private widget.TextField barcode;
    private widget.Button btnAksiBatal;
    private widget.Button btnAksiKonfirmasi;
    private widget.Button btnApprovalFP;
    private widget.Button btnBatal;
    private widget.Button btnFingerprint;
    private widget.Button btnFrista;
    private widget.Button btnKonfirmasi;
    private widget.Button btnPengajuanFP;
    private widget.Button btnRiwayatPelayanan;
    private widget.Button cariDiagnosa;
    private widget.Button cariDokter;
    private widget.Button cariNoRujukan;
    private widget.Button cariPoli;
    private widget.TextField catatan;
    private widget.Dialog dlgAksiFP;
    private widget.Panel emptyKanan;
    private widget.Panel emptyKiri;
    private widget.ComboBox flagProsedur;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel18;
    private widget.Label jLabel20;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.TextField jenisPelayanan;
    private widget.TextField jenisPeserta;
    private widget.TextField jk;
    private widget.Label judulAksi;
    private widget.ComboBox katarak;
    private widget.TextField kdKabKLL;
    private widget.TextField kdKecKLL;
    private widget.TextField kdPropKLL;
    private widget.ComboBox kelas;
    private widget.TextField keterangan;
    private widget.TextField kodeDPJPLayanan;
    private widget.TextField kodeDiagnosa;
    private widget.TextField kodePPK;
    private widget.TextField kodePPKRujukan;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label4;
    private widget.Label labelValidasi;
    private widget.ComboBox lakaLantas;
    private widget.TextField namaDPJPLayanan;
    private widget.TextField namaDiagnosa;
    private widget.TextField namaDokter;
    private widget.TextField namaPPK;
    private widget.TextField namaPPKRujukan;
    private widget.TextField namaPasien;
    private widget.TextField namaPoli;
    private widget.TextField nik;
    private widget.TextField nmKabKLL;
    private widget.TextField nmKecKLL;
    private widget.TextField nmPropKLL;
    private widget.TextField noPeserta;
    private widget.TextField noRM;
    private widget.TextField noRujukan;
    private widget.TextField noSEPSuplesi;
    private widget.TextField noSKDP;
    private widget.TextField noTelp;
    private widget.Panel panelAtas;
    private widget.Panel panelBawah;
    private widget.Panel panelBawahAksi;
    private widget.Numpad panelNumpad;
    private widget.Panel panelTambahan;
    private widget.Panel panelTengah;
    private widget.Panel panelTengahAksi;
    private widget.Panel panelUtama;
    private widget.PasswordField passAksi;
    private widget.ComboBox penunjang;
    private widget.TextField statusPeserta;
    private widget.ComboBox suplesi;
    private widget.Tanggal tglKLL;
    private widget.TextField tglLahir;
    private widget.TextField tglRujukan;
    private widget.TextField tglSEP;
    private widget.PaneToggle toggleInfoTambahan;
    private widget.ComboBox tujuanKunjungan;
    private widget.PasswordField userAksi;
    // End of variables declaration//GEN-END:variables

    private void setNomorRegistrasi() {
        switch (koneksiDB.URUTNOREG()) {
            case "poli":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and tgl_registrasi = ?", kodePoliReg, tglSEP.getText());
                break;
            case "dokter":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_dokter = ? and tgl_registrasi = ?", kodeDokterReg, tglSEP.getText());
                break;
            case "dokter + poli":
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?", kodePoliReg, kodeDokterReg, tglSEP.getText());
                break;
            default:
                noReg = Sequel.cariIsiSmc("select lpad(ifnull(max(convert(no_reg, signed)), 0) + 1, 3, '0') from reg_periksa where kd_poli = ? and kd_dokter = ? and tgl_registrasi = ?", kodePoliReg, kodeDokterReg, tglSEP.getText());
                break;
        }

        noRawat = Sequel.cariIsiSmc("select concat(date_format(tgl_registrasi, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(no_rawat, 6), signed)), 0) + 1, 6, '0')) from reg_periksa where tgl_registrasi = ?", tglSEP.getText());
    }

    private void tentukanHari() {
        try {
            Calendar.getInstance().setTime(new SimpleDateFormat("yyyy-MM-dd").parse(tglSEP.getText()));
            switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    hari = "AKHAD";
                    break;
                case 2:
                    hari = "SENIN";
                    break;
                case 3:
                    hari = "SELASA";
                    break;
                case 4:
                    hari = "RABU";
                    break;
                case 5:
                    hari = "KAMIS";
                    break;
                case 6:
                    hari = "JUMAT";
                    break;
                case 7:
                    hari = "SABTU";
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void isCekPasien() {
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select pasien.namakeluarga, concat_ws(', ', pasien.alamat, kelurahan.nm_kel, kecamatan.nm_kec, kabupaten.nm_kab) as alamat, pasien.keluarga, " +
            "if(pasien.tgl_daftar = ?, 'baru', 'lama') as daftar, timestampdiff(year, pasien.tgl_lahir, current_date()) as tahun, timestampdiff(month, " +
            "pasien.tgl_lahir, current_date()) - ((timestampdiff(month, pasien.tgl_lahir, current_date()) div 12) * 12) as bulan, timestampdiff(day, " +
            "date_add(date_add(pasien.tgl_lahir, interval timestampdiff(year, pasien.tgl_lahir, current_date()) year), interval timestampdiff(month, " +
            "pasien.tgl_lahir, current_date()) - ((timestampdiff(month, pasien.tgl_lahir, current_date()) div 12) * 12) month), current_date()) as hari " +
            "from pasien join kelurahan on pasien.kd_kel = kelurahan.kd_kel join kecamatan on pasien.kd_kec = kecamatan.kd_kec join kabupaten on " +
            "pasien.kd_kab = kabupaten.kd_kab where pasien.no_rkm_medis = ?"
        )) {
            int p = 0;
            ps.setString(++p, tglSEP.getText());
            ps.setString(++p, noRM.getText());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    namaPJ = rs.getString("namakeluarga");
                    alamatPJ = rs.getString("alamat");
                    hubunganPJ = rs.getString("keluarga");
                    statusDaftar = rs.getString("daftar");
                    umurDaftar = "0";
                    statusUmur = "Th";
                    if (rs.getInt("tahun") > 0) {
                        umurDaftar = rs.getString("tahun");
                        statusUmur = "Th";
                    } else if ((rs.getInt("tahun") <= 0) && (rs.getInt("bulan") > 0)) {
                        umurDaftar = rs.getString("bulan");
                        statusUmur = "Bl";
                    } else if ((rs.getInt("tahun") <= 0) && (rs.getInt("bulan") <= 0) && (rs.getInt("hari") > 0)) {
                        umurDaftar = rs.getString("hari");
                        statusUmur = "Hr";
                    }
                    umurPasien = rs.getString("tahun") + " Th " + rs.getString("bulan") + " Bl " + rs.getString("hari") + " Hr";
                }
            }
        } catch (SQLException e) {
            System.out.println("Notif : " + e);
        }

        if (Sequel.cariExistsSmc("select * from reg_periksa where no_rkm_medis = ? and kd_poli = ?", noRM.getText(), kodePoliReg)) {
            statusPoli = "Lama";
        }
    }

    private void cetakRegistrasi() {
        Map<String, Object> param = new HashMap<>();
        param.put("norawat", noRawat);
        param.put("parameter", noSEP);
        param.put("namars", instansiNama);
        param.put("kotars", instansiKota);
        Valid.printReportSmc("rptBridgingSEPAPM2.jasper", "report", "::[ Cetak SEP Model 4 ]::", param, koneksiDB.PRINTER_REGISTRASI(), 1);
        Valid.printReportSmc("rptBarcodeRawatAPM.jasper", "report", "::[ Barcode Perawatan ]::", param, koneksiDB.PRINTER_BARCODE(), Integer.parseInt(barcode.getText().trim()));
    }

    private void insertSEP() {
        try {
            tglkll = "0000-00-00";
            if (lakaLantas.getSelectedIndex() > 0) {
                tglkll = Valid.SetTgl(tglKLL.getSelectedItem() + "");
            }

            url = koneksiDB.URLAPIBPJS() + "/SEP/2.0/insert";
            System.out.println("URL : " + url);
            System.out.print("Menerbitkan SEP untuk [" + noRawat + "] : ");
            utc = api.getUTCDateTimeAsString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            payload = "{" +
                "\"request\":{" +
                "\"t_sep\":{" +
                "\"noKartu\":\"" + noPeserta.getText() + "\"," +
                "\"tglSep\":\"" + tglSEP.getText() + "\"," +
                "\"ppkPelayanan\":\"" + kodePPK.getText() + "\"," +
                "\"jnsPelayanan\":\"" + "2" + "\"," +
                "\"klsRawat\":{" +
                "\"klsRawatHak\":\"" + kelas.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"klsRawatNaik\":\"\"," +
                "\"pembiayaan\":\"\"," +
                "\"penanggungJawab\":\"\"" +
                "}," +
                "\"noMR\":\"" + noRM.getText() + "\"," +
                "\"rujukan\": {" +
                "\"asalRujukan\":\"" + asalRujukan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"tglRujukan\":\"" + tglRujukan.getText() + "\"," +
                "\"noRujukan\":\"" + noRujukan.getText() + "\"," +
                "\"ppkRujukan\":\"" + kodePPKRujukan.getText() + "\"" +
                "}," +
                "\"catatan\":\"" + catatan.getText() + "\"," +
                "\"diagAwal\":\"" + kodeDiagnosa.getText() + "\"," +
                "\"poli\": {" +
                "\"tujuan\": \"" + kodePoli + "\"," +
                "\"eksekutif\": \"0\"" +
                "}," +
                "\"cob\": {" +
                "\"cob\": \"0\"" +
                "}," +
                "\"katarak\": {" +
                "\"katarak\": \"" + katarak.getSelectedItem().toString().substring(0, 1) + "\"" +
                "}," +
                "\"jaminan\": {" +
                "\"lakaLantas\":\"" + lakaLantas.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"penjamin\": {" +
                "\"tglKejadian\": \"" + tglkll.replaceAll("0000-00-00", "") + "\"," +
                "\"keterangan\": \"" + keterangan.getText() + "\"," +
                "\"suplesi\": {" +
                "\"suplesi\": \"" + suplesi.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"noSepSuplesi\": \"" + noSEPSuplesi.getText() + "\"," +
                "\"lokasiLaka\": {" +
                "\"kdPropinsi\": \"" + kdPropKLL.getText() + "\"," +
                "\"kdKabupaten\": \"" + kdKabKLL.getText() + "\"," +
                "\"kdKecamatan\": \"" + kdKecKLL.getText() + "\"" +
                "}" +
                "}" +
                "}" +
                "}," +
                "\"tujuanKunj\": \"" + tujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\"," +
                "\"flagProcedure\": \"" + (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\"," +
                "\"kdPenunjang\": \"" + (penunjang.getSelectedIndex() > 0 ? penunjang.getSelectedIndex() + "" : "") + "\"," +
                "\"assesmentPel\": \"" + (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : "") + "\"," +
                "\"skdp\": {" +
                "\"noSurat\": \"" + noSKDP.getText() + "\"," +
                "\"kodeDPJP\": \"" + kodeDokter + "\"" +
                "}," +
                "\"dpjpLayan\": \"" + (kodeDPJPLayanan.getText().isBlank() ? "" : kodeDPJPLayanan.getText()) + "\"," +
                "\"noTelp\": \"" + noTelp.getText() + "\"," +
                "\"user\":\"" + noPeserta.getText() + "\"" +
                "}" +
                "}" +
                "}";

            entity = new HttpEntity(payload, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
            Valid.popupInfoDialog(metadata.path("message").asText(), 5);
            if (metadata.path("code").asText().equals("200")) {
                noSEP = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep").asText();
                System.out.println("No. SEP: " + noSEP);

                if (!isMobileJKN) {
                    String isNoRawat = Sequel.cariIsiSmc("select no_rawat from reg_periksa where tgl_registrasi = ? and no_rkm_medis = ? and kd_poli = ? and kd_dokter = ?", tglSEP.getText(), noRM.getText(), kodePoliReg, kodeDokterReg);
                    if (isNoRawat == null || (!isNoRawat.equals(noRawat))) {
                        System.out.println("======================================================");
                        System.out.println("Tidak dapat mendaftarkan pasien dengan detail berikut:");
                        System.out.println("No. Rawat: " + noRawat);
                        System.out.println("Tgl. Registrasi: " + tglSEP.getText());
                        System.out.println("No. Antrian: " + noReg + " (Ditemukan: " + Sequel.cariIsiSmc("select no_reg from reg_periksa where no_rawat = ?", noRawat) + ")");
                        System.out.println("No. RM: " + noRM.getText() + " (Ditemukan: " + Sequel.cariIsiSmc("select no_rkm_medis from reg_periksa where no_rawat = ?", noRawat) + ")");
                        System.out.println("Kode Dokter: " + kodeDokterReg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_dokter from reg_periksa where no_rawat = ?", noRawat) + ")");
                        System.out.println("Kode Poli: " + kodePoliReg + " (Ditemukan: " + Sequel.cariIsiSmc("select kd_poli from reg_periksa where no_rawat = ?", noRawat) + ")");
                        System.out.println("======================================================");
                        return;
                    }
                }

                Sequel.menyimpanSmc("bridging_sep", null, noSEP, noRawat, tglSEP.getText(), tglRujukan.getText(), noRujukan.getText(), kodePPKRujukan.getText(),
                    namaPPKRujukan.getText(), kodePPK.getText(), namaPPK.getText(), "2", catatan.getText(), kodeDiagnosa.getText(), namaDiagnosa.getText(), kodePoli,
                    namaPoli.getText(), kelas.getSelectedItem().toString().substring(0, 1), "", "", "", lakaLantas.getSelectedItem().toString().substring(0, 1),
                    noRM.getText(), noRM.getText(), namaPasien.getText(), tglLahir.getText(), jenisPeserta.getText(), jk.getText(), noPeserta.getText(),
                    tglSEP.getText() + " 00:00:00.000", asalRujukan.getSelectedItem().toString(), "0. Tidak", "0. Tidak", noTelp.getText(), katarak.getSelectedItem().toString(),
                    tglkll, keterangan.getText(), suplesi.getSelectedItem().toString(), noSEPSuplesi.getText(), kdPropKLL.getText(), nmPropKLL.getText(),
                    kdKabKLL.getText(), nmKabKLL.getText(), kdKecKLL.getText(), nmKecKLL.getText(), noSKDP.getText(), kodeDokter, namaDokter.getText(),
                    tujuanKunjungan.getSelectedItem().toString().substring(0, 1), (flagProsedur.getSelectedIndex() > 0 ? flagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                    (penunjang.getSelectedIndex() > 0 ? String.valueOf(penunjang.getSelectedIndex()) : ""), (asesmenPelayanan.getSelectedIndex() > 0 ? asesmenPelayanan.getSelectedItem().toString().substring(0, 1) : ""),
                    kodeDPJPLayanan.getText(), namaDPJPLayanan.getText()
                );

                Sequel.executeRawSmc("insert into mutasi_berkas values(?, 'Sudah Dikirim', now(), '0000-00-00 00:00:00', '0000-00-00 00:00:00', '0000-00-00 00:00:00', '0000-00-00 00:00:00') on duplicate key update dikirim = values(dikirim)", noRawat);

                if (!simpanRujukan()) {
                    System.out.println("Terjadi kesalahan pada saat proses rujukan masuk pasien!");
                }

                if (!prb.isBlank()) {
                    Sequel.menyimpanSmc("bpjs_prb", null, noSEP, prb);
                }

                if (Sequel.cariExistsSmc("select * from booking_registrasi where no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ? and status != 'Terdaftar'", noRM.getText(), tglSEP.getText(), kodeDokterReg, kodePoliReg)) {
                    Sequel.mengupdateSmc("booking_registrasi", "status = 'Terdaftar', waktu_kunjungan = now()", "no_rkm_medis = ? and tanggal_periksa = ? and kd_dokter = ? and kd_poli = ?", noRM.getText(), tglSEP.getText(), kodeDokterReg, kodePoliReg);
                }

                cetakRegistrasi();
                emptTeks();
                dispose();
            }
        } catch (Exception ex) {
            System.out.println("Notif : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
            }
        }
    }

    private void cekStatusFinger() {
        statusFinger = false;
        String keteranganValidasi = "";
        if (noPeserta.getText().isBlank()) {
            Valid.popupPeringatanDialog("No. Kartu masih kosong..!!");
        } else {
            if (Sequel.cariIntegerSmc("select timestampdiff(year, ?, current_date())", tglLahir.getText()) >= 17 && !namaPoli.getText().toLowerCase().contains("darurat")) {
                try {
                    url = koneksiDB.URLAPIBPJS() + "/SEP/FingerPrint/Peserta/" + noPeserta.getText() + "/TglPelayanan/" + tglSEP.getText();
                    System.out.println("URL : " + url);
                    System.out.print("Cek status FP tgl. " + tglSEP.getText() + " [" + noRM.getText() + "] : ");
                    utc = api.getUTCDateTimeAsString();
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    entity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                    metadata = root.path("metaData");
                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                    if (metadata.path("code").asText().equals("200")) {
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                        if (response.path("kode").asText().equals("1")) {
                            statusFinger = true;
                            keteranganValidasi = "";
                        } else {
                            statusFinger = false;
                            StringJoiner sj = new StringJoiner(" atau ");

                            if (btnFrista.isVisible()) {
                                sj.add("REKAM WAJAH");
                            }
                            if (btnFingerprint.isVisible()) {
                                sj.add("REKAM SIDIK JADI");
                            }

                            if (sj.length() == 0) {
                                keteranganValidasi = "<html><body>Silahkan lakukan proses VALIDASI BIOMETRIK ke petugas admisi dahulu</body></html>";
                            } else {
                                keteranganValidasi = "<html><body>Silahkan lakukan proses " + sj.toString() + "<br>terlebih dahulu</body></html>";
                            }

                            System.out.println("Notif : " + response.path("status").asText());
                        }
                    } else {
                        keteranganValidasi = response.path("status").asText();
                        System.out.println("Notif : " + response.path("status").asText());
                    }
                } catch (Exception ex) {
                    System.out.println("Notif : " + ex);
                    if (ex.toString().contains("UnknownHostException")) {
                        Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                    }
                }
            }
        }

        labelValidasi.setText(keteranganValidasi);
    }

    public void tampilKunjunganPertama(String noKartu) {
        emptTeks();
        tentukanHari();
        try {
            url = koneksiDB.URLAPIBPJS() + "/Rujukan/Peserta/" + noKartu;
            System.out.println("URL : " + url);
            System.out.print("Cek rujukan pertama aktif peserta : ");
            utc = api.getUTCDateTimeAsString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            entity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
            if (metadata.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                noPeserta.setText(response.path("peserta").path("noKartu").asText());
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                noRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noPeserta.getText()));
                namaPasien.setText(response.path("peserta").path("nama").asText());
                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                noRujukan.setText(response.path("noKunjungan").asText());
                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                kodePoli = response.path("poliRujukan").path("kode").asText();
                namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                kodePoliReg = Sequel.cariIsiSmc("select maping_poli_bpjs.kd_poli_rs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                try (PreparedStatement ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?")) {
                    ps.setString(1, kodePoliReg);
                    ps.setString(2, hari);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            kodeDokterReg = rs.getString("kd_dokter");
                            kodeDokter = rs.getString("kd_dokter_bpjs");
                            namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                            kodeDPJPLayanan.setText(kodeDokter);
                            namaDPJPLayanan.setText(namaDokter.getText());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                    case "1":
                        kelas.setSelectedIndex(0);
                        break;
                    case "2":
                        kelas.setSelectedIndex(1);
                        break;
                    case "3":
                        kelas.setSelectedIndex(2);
                        break;
                    default:
                        break;
                }
                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                nik.setText(response.path("peserta").path("nik").asText());
                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                }
                jk.setText(response.path("peserta").path("sex").asText());
                asalRujukan.setSelectedIndex(0);
                tglRujukan.setText(response.path("tglKunjungan").asText());
                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                    noTelp.setText(noTelpBPJS);
                }
                noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                setNomorRegistrasi();
                cekStatusFinger();
            } else {
                try {
                    url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/Peserta/" + noKartu;
                    System.out.println("URL : " + url);
                    System.out.print("Cek rujukan FKTL aktif peserta : ");
                    utc = api.getUTCDateTimeAsString();
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                    headers.add("X-Timestamp", utc);
                    headers.add("X-Signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                    entity = new HttpEntity(headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                    metadata = root.path("metaData");
                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                    if (metadata.path("code").asText().equals("200")) {
                        response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                        noPeserta.setText(response.path("peserta").path("noKartu").asText());
                        prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                        noRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noPeserta.getText()));
                        namaPasien.setText(response.path("peserta").path("nama").asText());
                        tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                        statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                        noRujukan.setText(response.path("noKunjungan").asText());
                        kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                        namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                        kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                        namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                        kodePoli = response.path("poliRujukan").path("kode").asText();
                        namaPoli.setText(response.path("poliRujukan").path("nama").asText());
                        kodePoliReg = Sequel.cariIsiSmc("select maping_poli_bpjs.kd_poli_rs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs = ?", response.path("poliRujukan").path("kode").asText());
                        try (PreparedStatement ps = koneksi.prepareStatement("select maping_dokter_dpjpvclaim.kd_dokter, maping_dokter_dpjpvclaim.kd_dokter_bpjs, maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal on maping_dokter_dpjpvclaim.kd_dokter = jadwal.kd_dokter where jadwal.kd_poli = ? and jadwal.hari_kerja = ?")) {
                            ps.setString(1, kodePoliReg);
                            ps.setString(2, hari);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    kodeDokterReg = rs.getString("kd_dokter");
                                    kodeDokter = rs.getString("kd_dokter_bpjs");
                                    namaDokter.setText(rs.getString("nm_dokter_bpjs"));
                                    kodeDPJPLayanan.setText(kodeDokter);
                                    namaDPJPLayanan.setText(namaDokter.getText());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                        }
                        switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                            case "1":
                                kelas.setSelectedIndex(0);
                                break;
                            case "2":
                                kelas.setSelectedIndex(1);
                                break;
                            case "3":
                                kelas.setSelectedIndex(2);
                                break;
                            default:
                                break;
                        }
                        jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                        nik.setText(response.path("peserta").path("nik").asText());
                        if (nik.getText().contains("null") || nik.getText().isBlank()) {
                            nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                        }
                        jk.setText(response.path("peserta").path("sex").asText());
                        asalRujukan.setSelectedIndex(1);
                        tglRujukan.setText(response.path("tglKunjungan").asText());
                        noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                        if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                            noTelp.setText(noTelpBPJS);
                        }
                        noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                        setNomorRegistrasi();
                        cekStatusFinger();
                    } else {
                        emptTeks();
                        Valid.popupPeringatanDialog("Rujukan faskes pertama atau FKTL tidak ada..!!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                    if (e.toString().contains("UnknownHostException")) {
                        Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
            }
        }
    }

    public void tampilKontrolBedaPoli(String noKartu) {
        tampilKunjunganPertama(noKartu);
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        penunjang.setSelectedIndex(0);
        asesmenPelayanan.setSelectedIndex(1);
    }

    public void tampilKontrol(String noSurat) {
        emptTeks();
        tentukanHari();
        try (PreparedStatement pskontrol = koneksi.prepareStatement(
            "select bridging_surat_kontrol_bpjs.*, bridging_sep.no_kartu, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat " +
            "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
        )) {
            pskontrol.setString(1, noSurat);
            try (ResultSet rskontrol = pskontrol.executeQuery()) {
                if (rskontrol.next()) {
                    tglRencanaKontrol = rskontrol.getString("tgl_rencana");
                    if (rskontrol.getString("jnspelayanan").equals("1")) {
                        try {
                            url = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rskontrol.getString("no_kartu") + "/tglSEP/" + tglSEP.getText();
                            System.out.println("URL : " + url);
                            System.out.print("Cek status peserta : ");
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            entity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                            if (metadata.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                                noPeserta.setText(response.path("noKartu").asText());
                                prb = response.path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                                noRM.setText(Sequel.cariIsiSmc("select no_rkm_medis from pasien where no_peserta = ?", noPeserta.getText()));
                                namaPasien.setText(response.path("nama").asText());
                                tglLahir.setText(response.path("tglLahir").asText());
                                statusPeserta.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                                noSKDP.setText(rskontrol.getString("no_surat"));
                                noRujukan.setText(rskontrol.getString("no_sep"));
                                kodePPKRujukan.setText(Sequel.cariIsiSmc("select kode_ppk from setting"));
                                namaPPKRujukan.setText(Sequel.cariIsiSmc("select nama_instansi from setting"));
                                kodeDiagnosa.setText("Z09.8");
                                namaDiagnosa.setText("Follow-up examination after other treatment for other conditions");
                                kodePoli = rskontrol.getString("kd_poli_bpjs");
                                namaPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli);
                                kodeDokter = rskontrol.getString("kd_dokter_bpjs");
                                namaDokter.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
                                kodeDPJPLayanan.setText(kodeDokter);
                                namaDPJPLayanan.setText(namaDokter.getText());
                                switch (rskontrol.getString("klsrawat")) {
                                    case "1":
                                        kelas.setSelectedIndex(0);
                                        break;
                                    case "2":
                                        kelas.setSelectedIndex(1);
                                        break;
                                    case "3":
                                        kelas.setSelectedIndex(2);
                                        break;
                                    default:
                                        break;
                                }
                                jenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                                nik.setText(response.path("nik").asText());
                                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                }
                                jk.setText(response.path("sex").asText());
                                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                                noTelpBPJS = response.path("mr").path("noTelepon").asText();
                                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                                    noTelp.setText(noTelpBPJS);
                                }
                                tujuanKunjungan.setSelectedIndex(0);
                                flagProsedur.setSelectedIndex(0);
                                penunjang.setSelectedIndex(0);
                                asesmenPelayanan.setSelectedIndex(0);
                                asalRujukan.setSelectedIndex(1);
                                setNomorRegistrasi();
                                cekStatusFinger();
                            } else {
                                emptTeks();
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            if (e.toString().contains("UnknownHostException")) {
                                Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                            }
                        }
                    } else {
                        try {
                            if (rskontrol.getString("asal_rujukan").equals("1")) {
                                url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                                System.out.println("URL : " + url);
                                System.out.print("Cek rujukan pertama aktif untuk kontrol [" + rskontrol.getString("no_surat") + "] : ");
                                asalRujukan.setSelectedIndex(0);
                            } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                                System.out.println("URL : " + url);
                                System.out.print("Cek rujukan FKTL aktif untuk kontrol [" + rskontrol.getString("no_surat") + "] : ");
                                asalRujukan.setSelectedIndex(1);
                            }
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                            headers.add("X-Timestamp", utc);
                            headers.add("X-Signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                            entity = new HttpEntity(headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                            metadata = root.path("metaData");
                            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                            if (metadata.path("code").asText().equals("200")) {
                                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                                noPeserta.setText(response.path("peserta").path("noKartu").asText());
                                noRM.setText(Sequel.cariIsiSmc("select pasien.no_rkm_medis from pasien where pasien.no_peserta = ?", noPeserta.getText()));
                                namaPasien.setText(response.path("peserta").path("nama").asText());
                                tglLahir.setText(response.path("peserta").path("tglLahir").asText());
                                statusPeserta.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                                noSKDP.setText(rskontrol.getString("no_surat"));
                                noRujukan.setText(response.path("noKunjungan").asText());
                                kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                kodePoli = rskontrol.getString("kd_poli_bpjs");
                                namaPoli.setText(rskontrol.getString("nm_poli_bpjs"));
                                kodePoliReg = Sequel.cariIsiSmc("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs = ?", kodePoli);
                                kodeDokter = rskontrol.getString("kd_dokter_bpjs");
                                namaDokter.setText(rskontrol.getString("nm_dokter_bpjs"));
                                kodeDokterReg = Sequel.cariIsiSmc("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs = ?", kodeDokter);
                                switch (response.path("peserta").path("hakKelas").path("kode").asText()) {
                                    case "1":
                                        kelas.setSelectedIndex(0);
                                        break;
                                    case "2":
                                        kelas.setSelectedIndex(1);
                                        break;
                                    case "3":
                                        kelas.setSelectedIndex(2);
                                        break;
                                    default:
                                        break;
                                }
                                jenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                                nik.setText(response.path("peserta").path("nik").asText());
                                if (nik.getText().contains("null") || nik.getText().isBlank()) {
                                    nik.setText(Sequel.cariIsiSmc("select no_ktp from pasien where no_rkm_medis = ?", noRM.getText()));
                                }
                                jk.setText(response.path("peserta").path("sex").asText());
                                tglRujukan.setText(response.path("tglKunjungan").asText());
                                noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                                if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                                    noTelp.setText(noTelpBPJS);
                                }
                                noTelpBPJS = response.path("peserta").path("mr").path("noTelepon").asText();
                                kodeDPJPLayanan.setText(kodeDokter);
                                namaDPJPLayanan.setText(namaDokter.getText());
                                tujuanKunjungan.setSelectedIndex(2);
                                flagProsedur.setSelectedIndex(0);
                                penunjang.setSelectedIndex(0);
                                asesmenPelayanan.setSelectedIndex(5);
                                setNomorRegistrasi();
                                cekStatusFinger();
                            } else {
                                emptTeks();
                            }
                        } catch (Exception ex) {
                            System.out.println("Notif : " + ex);
                            if (ex.toString().contains("UnknownHostException")) {
                                Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            Valid.popupPeringatanDialog("Maaf, Data surat kontrol tidak ditemukan...!!!");
        }
    }

    public void tampilMobileJKN(String noKartu) {
        toggleInfoTambahan.setSelected(false);
        emptTeks();
        isMobileJKN = true;
        try (PreparedStatement psjkn = koneksi.prepareStatement(
            "select referensi_mobilejkn_bpjs.*, maping_poli_bpjs.nm_poli_bpjs, maping_poli_bpjs.kd_poli_rs, maping_dokter_dpjpvclaim.nm_dokter_bpjs, maping_dokter_dpjpvclaim.kd_dokter, pasien.jk, pasien.tgl_lahir from " +
            "referensi_mobilejkn_bpjs join maping_poli_bpjs on referensi_mobilejkn_bpjs.kodepoli = maping_poli_bpjs.kd_poli_bpjs join maping_dokter_dpjpvclaim on referensi_mobilejkn_bpjs.kodedokter = maping_dokter_dpjpvclaim.kd_dokter_bpjs " +
            "join pasien on referensi_mobilejkn_bpjs.norm = pasien.no_rkm_medis where referensi_mobilejkn_bpjs.nomorkartu = ? and referensi_mobilejkn_bpjs.tanggalperiksa = current_date() and referensi_mobilejkn_bpjs.status in " +
            "('Belum', 'Checkin') and tanggalperiksa = current_date() and not exists(select * from bridging_sep where bridging_sep.no_rawat = referensi_mobilejkn_bpjs.no_rawat)"
        )) {
            psjkn.setString(1, noKartu);
            try (ResultSet rsjkn = psjkn.executeQuery()) {
                if (rsjkn.next()) {
                    jamPraktek = rsjkn.getString("jampraktek");
                    if (!cekWaktuRegistrasi()) {
                        emptTeks();
                        Valid.popupPeringatanDialog("Waktu cekin anda masih harus menunggu lagi..!!");
                        return;
                    }
                    noBooking = rsjkn.getString("nobooking");
                    noRawat = rsjkn.getString("no_rawat");
                    noTelpBPJS = rsjkn.getString("nohp");
                    statusDaftar = rsjkn.getString("pasienbaru");
                    jenisKunjungan = rsjkn.getString("jeniskunjungan").substring(0, 1);
                    noReferensi = rsjkn.getString("nomorreferensi");
                    noReg = rsjkn.getString("angkaantrean");
                    estimasiDilayani = rsjkn.getString("estimasidilayani");
                    kuota = rsjkn.getInt("kuotajkn");
                    sisaKuota = rsjkn.getInt("sisakuotajkn");
                    noRM.setText(rsjkn.getString("norm"));
                    tglLahir.setText(rsjkn.getString("tgl_lahir"));
                    kodePoli = rsjkn.getString("kodepoli");
                    namaPoli.setText(rsjkn.getString("nm_poli_bpjs"));
                    kodePoliReg = rsjkn.getString("kd_poli_rs");
                    kodeDokter = rsjkn.getString("kodedokter");
                    namaDokter.setText(rsjkn.getString("nm_dokter_bpjs"));
                    kodeDokterReg = rsjkn.getString("kd_dokter");
                    kodeDPJPLayanan.setText(kodeDokter);
                    namaDPJPLayanan.setText(namaDokter.getText());
                    nik.setText(rsjkn.getString("nik"));
                    noPeserta.setText(rsjkn.getString("nomorkartu"));
                    jk.setText(rsjkn.getString("jk"));
                    noTelp.setText(Sequel.cariIsiSmc("select no_tlp from pasien where no_rkm_medis = ?", noRM.getText()));
                    if (noTelp.getText().contains("null") || noTelp.getText().isBlank()) {
                        noTelp.setText(noTelpBPJS);
                    }
                    // CEK STATUS PASIEN
                    try {
                        url = koneksiDB.URLAPIBPJS() + "/Peserta/nokartu/" + rsjkn.getString("nomorkartu") + "/tglSEP/" + tglSEP.getText();
                        System.out.println("URL : " + url);
                        System.out.print("Cek status peserta mobileJKN : ");
                        utc = api.getUTCDateTimeAsString();
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                        headers.add("X-Timestamp", utc);
                        headers.add("X-Signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                        entity = new HttpEntity(headers);
                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                        metadata = root.path("metaData");
                        System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                        if (metadata.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                            prb = response.path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                            switch (response.path("hakKelas").path("kode").asText()) {
                                case "1":
                                    kelas.setSelectedIndex(0);
                                    break;
                                case "2":
                                    kelas.setSelectedIndex(1);
                                    break;
                                case "3":
                                    kelas.setSelectedIndex(2);
                                    break;
                                default:
                                    break;
                            }
                            namaPasien.setText(response.path("nama").asText());
                            statusPeserta.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                            jenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                            switch (jenisKunjungan) {
                                case "1":
                                    // RUJUKAN FKTP
                                    asalRujukan.setSelectedIndex(0);
                                    try {
                                        url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rsjkn.getString("nomorreferensi");
                                        System.out.println("URL : " + url);
                                        System.out.print("Cek status rujukan fakses pertama pasien mobileJKN : ");
                                        utc = api.getUTCDateTimeAsString();
                                        headers = new HttpHeaders();
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                        headers.add("X-Timestamp", utc);
                                        headers.add("X-Signature", api.getHmac(utc));
                                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                        entity = new HttpEntity(headers);
                                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                                        metadata = root.path("metaData");
                                        System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                        if (metadata.path("code").asText().equals("200")) {
                                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                            noRujukan.setText(response.path("noKunjungan").asText());
                                            kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                            namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                            kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                            namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                            tglRujukan.setText(response.path("tglKunjungan").asText());
                                        } else {
                                            System.out.println("Notif : " + metadata.path("message").asText());
                                            Valid.popupPeringatanDialog(metadata.path("message").asText());
                                            emptTeks();
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Notif : " + e);
                                        if (e.toString().contains("UnknownHostException")) {
                                            Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                                        }
                                        emptTeks();
                                    }
                                    break;
                                case "4":
                                    // RUJUKAN FKTL
                                    asalRujukan.setSelectedIndex(1);
                                    try {
                                        url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rsjkn.getString("nomorreferensi");
                                        System.out.println("URL : " + url);
                                        System.out.print("Cek status rujukan FKTL pasien mobileJKN : ");
                                        utc = api.getUTCDateTimeAsString();
                                        headers = new HttpHeaders();
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                        headers.add("X-Timestamp", utc);
                                        headers.add("X-Signature", api.getHmac(utc));
                                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                        entity = new HttpEntity(headers);
                                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                                        metadata = root.path("metaData");
                                        System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                        if (metadata.path("code").asText().equals("200")) {
                                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                            noRujukan.setText(response.path("noKunjungan").asText());
                                            kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                            namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                            kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                            namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                            tglRujukan.setText(response.path("tglKunjungan").asText());
                                        } else {
                                            System.out.println("Notif : " + metadata.path("message").asText());
                                            Valid.popupPeringatanDialog(metadata.path("message").asText());
                                            emptTeks();
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Notif : " + e);
                                        if (e.toString().contains("UnknownHostException")) {
                                            Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                                        }
                                        emptTeks();
                                    }
                                    break;
                                case "3":
                                    // CEK JENIS KONTROL DULU
                                    try (PreparedStatement pskontrol = koneksi.prepareStatement(
                                        "select bridging_surat_kontrol_bpjs.*, left(bridging_sep.asal_rujukan, 1) as asal_rujukan, bridging_sep.jnspelayanan, bridging_sep.no_rujukan, bridging_sep.klsrawat " +
                                        "from bridging_surat_kontrol_bpjs join bridging_sep on bridging_surat_kontrol_bpjs.no_sep = bridging_sep.no_sep where bridging_surat_kontrol_bpjs.no_surat = ?"
                                    )) {
                                        pskontrol.setString(1, rsjkn.getString("nomorreferensi"));
                                        try (ResultSet rskontrol = pskontrol.executeQuery()) {
                                            if (rskontrol.next()) {
                                                tglRencanaKontrol = rskontrol.getString("tgl_rencana");
                                                if (rskontrol.getString("jnspelayanan").equals("1")) {
                                                    // KONTROL POST RANAP
                                                    noSKDP.setText(rskontrol.getString("no_surat"));
                                                    noRujukan.setText(rskontrol.getString("no_sep"));
                                                    kodePPKRujukan.setText(kodePPK.getText());
                                                    namaPPKRujukan.setText(namaPPK.getText());
                                                    kodeDiagnosa.setText("Z09.8");
                                                    namaDiagnosa.setText("Follow-up examination after other treatment for other conditions");
                                                    tujuanKunjungan.setSelectedIndex(0);
                                                    flagProsedur.setSelectedIndex(0);
                                                    penunjang.setSelectedIndex(0);
                                                    asesmenPelayanan.setSelectedIndex(0);
                                                    asalRujukan.setSelectedIndex(1);
                                                } else {
                                                    // KONTROL POLI
                                                    try {
                                                        if (rskontrol.getString("asal_rujukan").equals("1")) {
                                                            url = koneksiDB.URLAPIBPJS() + "/Rujukan/" + rskontrol.getString("no_rujukan");
                                                            System.out.println("URL : " + url);
                                                            System.out.print("Cek status rujukan fakses pertama pasien mobileJKN untuk kontrol [" + rskontrol.getString("no_surat") + "] : ");
                                                            asalRujukan.setSelectedIndex(0);
                                                        } else if (rskontrol.getString("asal_rujukan").equals("2")) {
                                                            url = koneksiDB.URLAPIBPJS() + "/Rujukan/RS/" + rskontrol.getString("no_rujukan");
                                                            System.out.println("URL : " + url);
                                                            System.out.print("Cek status rujukan FKTL pasien mobileJKN untuk kontrol [" + rskontrol.getString("no_surat") + "] : ");
                                                            asalRujukan.setSelectedIndex(1);
                                                        }
                                                        utc = api.getUTCDateTimeAsString();
                                                        headers = new HttpHeaders();
                                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                                                        headers.add("X-Timestamp", utc);
                                                        headers.add("X-Signature", api.getHmac(utc));
                                                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                                                        entity = new HttpEntity(headers);
                                                        root = mapper.readTree(api.getRest().exchange(url, HttpMethod.GET, entity, String.class).getBody());
                                                        metadata = root.path("metaData");
                                                        System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                                        if (metadata.path("code").asText().equals("200")) {
                                                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                                                            noSKDP.setText(rskontrol.getString("no_surat"));
                                                            noRujukan.setText(response.path("noKunjungan").asText());
                                                            kodePPKRujukan.setText(response.path("provPerujuk").path("kode").asText());
                                                            namaPPKRujukan.setText(response.path("provPerujuk").path("nama").asText());
                                                            kodeDiagnosa.setText(response.path("diagnosa").path("kode").asText());
                                                            namaDiagnosa.setText(response.path("diagnosa").path("nama").asText());
                                                            tglRujukan.setText(response.path("tglKunjungan").asText());
                                                            tujuanKunjungan.setSelectedIndex(2);
                                                            flagProsedur.setSelectedIndex(0);
                                                            penunjang.setSelectedIndex(0);
                                                            asesmenPelayanan.setSelectedIndex(5);
                                                        } else {
                                                            System.out.println("Notif : " + metadata.path("message").asText());
                                                            Valid.popupPeringatanDialog(metadata.path("message").asText());
                                                            emptTeks();
                                                        }
                                                    } catch (Exception e) {
                                                        System.out.println("Notif : " + e);
                                                        if (e.toString().contains("UnknownHostException")) {
                                                            Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                                                        }
                                                        emptTeks();
                                                    }
                                                }
                                            } else {
                                                emptTeks();
                                                Valid.popupPeringatanDialog("Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                            }
                                        }
                                    } catch (Exception e) {
                                        emptTeks();
                                        Valid.popupPeringatanDialog("Maaf, rujukan kontrol pasien tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    }
                                    break;
                                default:
                                    emptTeks();
                                    Valid.popupPeringatanDialog("Maaf, antrian JKN tidak ditemukan!\nSilahkan hubungi administrasi.");
                                    break;
                            }
                            cekStatusFinger();
                        } else {
                            emptTeks();
                            System.out.println("Notif : " + metadata.path("message").asText());
                            Valid.popupPeringatanDialog(metadata.path("message").asText());
                        }
                    } catch (Exception e) {
                        emptTeks();
                        System.out.println("Notif : " + e);
                        if (e.toString().contains("UnknownHostException")) {
                            Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
                        }
                    }
                } else {
                    emptTeks();
                    Valid.popupPeringatanDialog("Maaf, pasien membatalkan antrian MobileJKN, atau telah menerima pelayanan!\nSilahkan hubungi administrasi.");
                }
            }
        } catch (Exception e) {
            emptTeks();
            System.out.println("Notif : " + e);
            Valid.popupGagalDialog("Maaf, terjadi kesalahan pada saat mencari rujukan di MobileJKN!\nSilahkan hubungi administrasi.");
        }
    }

    private boolean kirimAntrianOnsite() {
        boolean sukses = true;

        if (!ADDANTRIANAPIMOBILEJKN) {
            return sukses;
        }

        if (isMobileJKN) {
            if (Sequel.cariExistsSmc("select * from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Belum'", noBooking)) {
                Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "validasi = now(), status = 'Checkin'", "nobooking = ? and status = 'Belum'", noBooking);
                Sequel.mengupdateSmc("reg_periksa", "jam_reg = current_time()", "no_rawat = ? and stts != 'Batal'", noRawat);
            }

            if (jenisKunjungan.equals("3") && !tglRencanaKontrol.equals(tglSEP.getText())) {
                updateSuratKontrol();
            }

            if (Sequel.cariExistsSmc("select * from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Checkin' and referensi_mobilejkn_bpjs.statuskirim = 'Belum'", noBooking)) {
                datajam = Sequel.cariIsiSmc("select referensi_mobilejkn_bpjs.validasi from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.nobooking = ? and referensi_mobilejkn_bpjs.status = 'Checkin' and referensi_mobilejkn_bpjs.statuskirim = 'Belum'", noBooking);
                try {
                    url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                    System.out.println("URL : " + url);
                    System.out.print("JSON : ");
                    utc = String.valueOf(api.getUTCDateTime());
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                    headers.add("x-timestamp", utc);
                    headers.add("x-signature", api.getHmac(utc));
                    headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                    payload = "{" +
                        "\"kodebooking\": \"" + noBooking + "\"," +
                        "\"jenispasien\": \"JKN\"," +
                        "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                        "\"nik\": \"" + nik.getText() + "\"," +
                        "\"nohp\": \"" + noTelp.getText().trim() + "\"," +
                        "\"kodepoli\": \"" + kodePoli + "\"," +
                        "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                        "\"pasienbaru\": " + statusDaftar + "," +
                        "\"norm\": \"" + noRM.getText() + "\"," +
                        "\"tanggalperiksa\": \"" + tglSEP.getText() + "\"," +
                        "\"kodedokter\": " + kodeDokter + "," +
                        "\"namadokter\": \"" + namaDokter.getText() + "\"," +
                        "\"jampraktek\": \"" + jamPraktek + "\"," +
                        "\"jeniskunjungan\": " + jenisKunjungan + "," +
                        "\"nomorreferensi\": \"" + noReferensi + "\"," +
                        "\"nomorantrean\": \"" + kodePoliReg + "-" + noReg + "\"," +
                        "\"angkaantrean\": " + noReg + "," +
                        "\"estimasidilayani\": " + estimasiDilayani + "," +
                        "\"sisakuotajkn\": " + sisaKuota + "," +
                        "\"kuotajkn\": " + kuota + "," +
                        "\"sisakuotanonjkn\": " + sisaKuota + "," +
                        "\"kuotanonjkn\": " + kuota + "," +
                        "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                        "}";
                    System.out.println(payload);
                    System.out.print("Addantrean mobileJKN onsite [" + noBooking + "] : ");
                    entity = new HttpEntity(payload, headers);
                    root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                    metadata = root.path("metadata");
                    System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                    Sequel.logTaskid(noRawat, noBooking, "Onsite", "addantrean", payload, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                    if (metadata.path("code").asText().equals("200") || metadata.path("code").asText().equals("208") || metadata.path("message").asText().equals("Ok")) {
                        Sequel.mengupdateSmc("referensi_mobilejkn_bpjs", "statuskirim = 'Sudah'", "nobooking = ?", noBooking);
                    } else {
                        sukses = false;
                        JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (HttpClientErrorException e) {
                    sukses = false;
                    System.out.println(e.getStatusCode().toString() + " " + e.getMessage());
                    Sequel.logTaskid(noRawat, noBooking, "Onsite", "addantrean", payload, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                } catch (HttpServerErrorException e) {
                    sukses = false;
                    System.out.println(e.getStatusCode().toString() + " " + e.getMessage());
                    Sequel.logTaskid(noRawat, noBooking, "Onsite", "addantrean", payload, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    sukses = false;
                    System.out.println("Notif : " + e);
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas", "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            int angkaantrean = Integer.parseInt(noReg);
            String jamMulai = "";
            kuota = 0;
            jenisKunjungan = "";
            noReferensi = noRujukan.getText();
            if ((!noRujukan.getText().isBlank()) || (!noSKDP.getText().isBlank())) {
                if (tujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().isBlank()) {
                    if (asalRujukan.getSelectedIndex() == 0) {
                        jenisKunjungan = "1";
                        noReferensi = noRujukan.getText();
                    } else {
                        if (!noSKDP.getText().isBlank()) {
                            jenisKunjungan = "3";
                            noReferensi = noSKDP.getText();
                        } else {
                            jenisKunjungan = "4";
                            noReferensi = noRujukan.getText();
                        }
                    }
                } else if (tujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")) {
                    jenisKunjungan = "3";
                    noReferensi = noSKDP.getText();
                } else if (tujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal") && flagProsedur.getSelectedItem().toString().isBlank() && penunjang.getSelectedItem().toString().isBlank() && asesmenPelayanan.getSelectedItem().toString().trim().equals("4. Atas Instruksi RS")) {
                    jenisKunjungan = "2";
                    noReferensi = noRujukan.getText();
                } else {
                    if (tujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter") && asesmenPelayanan.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")) {
                        jenisKunjungan = "3";
                        noReferensi = noSKDP.getText();
                    } else {
                        jenisKunjungan = "2";
                        noReferensi = noRujukan.getText();
                    }
                }

                if (jenisKunjungan.equals("3") && !noSKDP.getText().isBlank() && !tglRencanaKontrol.equals(tglSEP.getText())) {
                    updateSuratKontrol();
                }

                try {
                    switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                        case 1:
                            hari = "AKHAD";
                            break;
                        case 2:
                            hari = "SENIN";
                            break;
                        case 3:
                            hari = "SELASA";
                            break;
                        case 4:
                            hari = "RABU";
                            break;
                        case 5:
                            hari = "KAMIS";
                            break;
                        case 6:
                            hari = "JUMAT";
                            break;
                        case 7:
                            hari = "SABTU";
                            break;
                        default:
                            break;
                    }

                    try (PreparedStatement ps = koneksi.prepareStatement("select jam_mulai, jam_selesai, kuota from jadwal where hari_kerja = ? and kd_poli = ? and kd_dokter = ?")) {
                        ps.setString(1, hari);
                        ps.setString(2, kodePoliReg);
                        ps.setString(3, kodeDokterReg);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                jamPraktek = rs.getString("jam_mulai").substring(0, 5) + "-" + rs.getString("jam_selesai").substring(0, 5);
                                jamMulai = rs.getString("jam_mulai");
                                kuota = rs.getInt("kuota");
                                datajam = Sequel.cariIsiSmc("select date_add(concat(?, ' ', ?), interval ? minute)", tglSEP.getText(), jamMulai, String.valueOf(angkaantrean * 5));
                                parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datajam);
                            } else {
                                sukses = false;
                                System.out.println("Jadwal praktek tidak ditemukan...!!!");
                                JOptionPane.showMessageDialog(null, "Jadwal praktek tidak ditemukan...!!!", "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        sukses = false;
                        System.out.println("Notif : " + e);
                    }

                    if (sukses) {
                        if (!jenisKunjungan.isBlank() && !noReferensi.isBlank()) {
                            try {
                                url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                                System.out.println("URL : " + url);
                                payload = "{" +
                                    "\"kodebooking\": \"" + noRawat + "\"," +
                                    "\"jenispasien\": \"JKN\"," +
                                    "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                                    "\"nik\": \"" + nik.getText() + "\"," +
                                    "\"nohp\": \"" + noTelp.getText().trim() + "\"," +
                                    "\"kodepoli\": \"" + kodePoli + "\"," +
                                    "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                                    "\"pasienbaru\": 0," +
                                    "\"norm\": \"" + noRM.getText() + "\"," +
                                    "\"tanggalperiksa\": \"" + tglSEP.getText() + "\"," +
                                    "\"kodedokter\": " + kodeDokter + "," +
                                    "\"namadokter\": \"" + namaDokter.getText() + "\"," +
                                    "\"jampraktek\": \"" + jamPraktek + "\"," +
                                    "\"jeniskunjungan\": " + jenisKunjungan + "," +
                                    "\"nomorreferensi\": \"" + noReferensi + "\"," +
                                    "\"nomorantrean\": \"" + noReg + "\"," +
                                    "\"angkaantrean\": " + angkaantrean + "," +
                                    "\"estimasidilayani\": " + parsedDate.getTime() + "," +
                                    "\"sisakuotajkn\": " + (kuota - angkaantrean) + "," +
                                    "\"kuotajkn\": " + kuota + "," +
                                    "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + "," +
                                    "\"kuotanonjkn\": " + kuota + "," +
                                    "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                    "}";
                                System.out.println("JSON : " + payload);
                                System.out.print("Addantrean onsite [" + noRawat + "] : ");
                                utc = api.getUTCDateTimeAsString();
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                                headers.add("x-timestamp", utc);
                                headers.add("x-signature", api.getHmac(utc));
                                headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                                entity = new HttpEntity(payload, headers);
                                root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                                metadata = root.path("metadata");
                                Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", payload, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                                System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                                if (!metadata.path("code").asText().equals("200")) {
                                    sukses = false;
                                }
                            } catch (HttpClientErrorException e) {
                                sukses = false;
                                System.out.println(e.getStatusCode().toString() + " " + e.getMessage());
                                Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", payload, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                            } catch (HttpServerErrorException e) {
                                sukses = false;
                                System.out.println(e.getStatusCode().toString() + " " + e.getMessage());
                                Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", payload, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                            } catch (Exception e) {
                                sukses = false;
                                System.out.println("Notif : " + e);
                            }
                        } else {
                            sukses = false;
                        }
                    }
                    if (!sukses) {
                        try {
                            sukses = true;
                            url = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                            System.out.println("URL : " + url);
                            payload = "{" +
                                "\"kodebooking\": \"" + noRawat + "\"," +
                                "\"jenispasien\": \"JKN\"," +
                                "\"nomorkartu\": \"" + noPeserta.getText() + "\"," +
                                "\"nik\": \"" + nik.getText() + "\"," +
                                "\"nohp\": \"" + noTelpBPJS + "\"," +
                                "\"kodepoli\": \"" + kodePoli + "\"," +
                                "\"namapoli\": \"" + namaPoli.getText() + "\"," +
                                "\"pasienbaru\": 0," +
                                "\"norm\": \"" + noRM.getText() + "\"," +
                                "\"tanggalperiksa\": \"" + tglSEP.getText() + "\"," +
                                "\"kodedokter\": " + kodeDokter + "," +
                                "\"namadokter\": \"" + namaDokter.getText() + "\"," +
                                "\"jampraktek\": \"" + jamPraktek + "\"," +
                                "\"jeniskunjungan\": " + jenisKunjungan + "," +
                                "\"nomorreferensi\": \"" + noReferensi + "\"," +
                                "\"nomorantrean\": \"" + noReg + "\"," +
                                "\"angkaantrean\": " + angkaantrean + "," +
                                "\"estimasidilayani\": " + parsedDate.getTime() + "," +
                                "\"sisakuotajkn\": " + (kuota - angkaantrean) + "," +
                                "\"kuotajkn\": " + kuota + "," +
                                "\"sisakuotanonjkn\": " + (kuota - angkaantrean) + "," +
                                "\"kuotanonjkn\": " + kuota + "," +
                                "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                "}";
                            System.out.println("JSON : " + payload);
                            System.out.print("Addantrean onsite [" + noRawat + "] : ");
                            utc = api.getUTCDateTimeAsString();
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                            headers.add("x-timestamp", utc);
                            headers.add("x-signature", api.getHmac(utc));
                            headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
                            entity = new HttpEntity(payload, headers);
                            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.POST, entity, String.class).getBody());
                            metadata = root.path("metadata");
                            Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", payload, metadata.path("code").asText(), metadata.path("message").asText(), root.toString(), datajam);
                            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
                            if (!metadata.path("code").asText().equals("200")) {
                                sukses = false;
                                JOptionPane.showMessageDialog(null, metadata.path("message").asText(), "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (HttpClientErrorException e) {
                            sukses = false;
                            System.out.println(e.getStatusCode().toString() + " " + e.getMessage());
                            Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", payload, e.getStatusCode().toString(), e.getMessage(), e.getResponseBodyAsString(), datajam);
                            JOptionPane.showMessageDialog(null, e.getMessage(), "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                        } catch (HttpServerErrorException e) {
                            sukses = false;
                            System.out.println(e.getStatusCode().toString() + " " + e.getMessage());
                            Sequel.logTaskid(noRawat, noRawat, "Onsite", "addantrean", payload, e.getStatusCode().toString(), e.getMessage(), "", datajam);
                            JOptionPane.showMessageDialog(null, e.getMessage(), "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception e) {
                            sukses = false;
                            System.out.println("Notif : " + e);
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas", "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    sukses = false;
                    System.out.println("Notif : " + e);
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan..!!\nSilahkan hubungi petugas", "Gagal Kirim Antrian", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        return sukses;
    }

    private void emptTeks() {
        noRM.setText("");
        namaPasien.setText("");
        tglLahir.setText("");
        statusPeserta.setText("");
        noSKDP.setText("");
        noRujukan.setText("");
        kodePPKRujukan.setText("");
        namaPPKRujukan.setText("");
        kodeDiagnosa.setText("");
        namaDiagnosa.setText("");
        kodePoli = "";
        namaPoli.setText("");
        kodeDokter = "";
        namaDokter.setText("");
        kelas.setSelectedIndex(2);
        jenisPeserta.setText("");
        jk.setText("");
        nik.setText("");
        noPeserta.setText("");
        asalRujukan.setSelectedIndex(0);
        tglRujukan.setText(LocalDate.now().toString());
        tglSEP.setText(LocalDate.now().toString());
        katarak.setSelectedIndex(0);
        noTelp.setText("");

        jenisPelayanan.setText("2. Ralan");
        tujuanKunjungan.setSelectedIndex(0);
        flagProsedur.setSelectedIndex(0);
        flagProsedur.setEnabled(false);
        penunjang.setSelectedIndex(0);
        penunjang.setEnabled(false);
        asesmenPelayanan.setSelectedIndex(0);
        asesmenPelayanan.setEnabled(true);
        kodeDPJPLayanan.setText("");
        namaDPJPLayanan.setText("");
        barcode.setText(String.valueOf(koneksiDB.PRINTJUMLAHBARCODE()));
        lakaLantas.setSelectedIndex(0);
        lakaLantasItemStateChanged(null);
        suplesi.setSelectedIndex(0);
        suplesiItemStateChanged(null);
        kdPropKLL.setText("");
        nmPropKLL.setText("");
        kdKabKLL.setText("");
        nmKabKLL.setText("");
        kdKecKLL.setText("");
        nmKecKLL.setText("");
        catatan.setText("Anjungan Pasien Mandiri " + namaPPK.getText());

        toggleInfoTambahan.setSelected(false);
        panelNumpad.setVisible(false);
        resetAksi();

        statusFinger = false;
        isMobileJKN = false;

        hari = "";
        tglkll = "0000-00-00";
        datajam = "";
        url = "";
        payload = "";
        utc = "";
        noSEP = "";
        noBooking = "";
        noReferensi = "";
        jenisKunjungan = "";
        estimasiDilayani = "";
        jamPraktek = "";
        kodePoli = "";
        kodeDokter = "";
        prb = "";
        tglRencanaKontrol = "";
        noReg = "";
        noRawat = "";
        kodeDokterReg = "";
        kodePoliReg = "";
        namaPJ = "";
        alamatPJ = "";
        hubunganPJ = "";
        biayaReg = "";
        statusPoli = "Baru";
        statusDaftar = "";
        umurDaftar = "0";
        statusUmur = "Th";
        umurPasien = "";
        noTelpBPJS = "";
    }

    private void updateSuratKontrol() {
        if (noSKDP.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Maaf, surat kontrol masih kosong..!!");
            return;
        }

        try {
            url = koneksiDB.URLAPIBPJS() + "/RencanaKontrol/Update";
            System.out.println("URL : " + url);
            System.out.print("Update rencana kontrol [" + noSKDP.getText() + "] : ");
            utc = api.getUTCDateTimeAsString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            payload = "{" +
                "\"request\": {" +
                "\"noSuratKontrol\":\"" + noSKDP.getText() + "\"," +
                "\"noSEP\":\"" + Sequel.cariIsiSmc("select no_sep from bridging_surat_kontrol_bpjs where no_surat = ?", noSKDP.getText()) + "\"," +
                "\"kodeDokter\":\"" + kodeDokter + "\"," +
                "\"poliKontrol\":\"" + kodePoli + "\"," +
                "\"tglRencanaKontrol\":\"" + tglSEP.getText() + "\"," +
                "\"user\":\"" + noPeserta.getText() + "\"" +
                "}" +
                "}";
            entity = new HttpEntity(payload, headers);
            root = mapper.readTree(api.getRest().exchange(url, HttpMethod.PUT, entity, String.class).getBody());
            metadata = root.path("metaData");
            System.out.println(metadata.path("code").asText() + " " + metadata.path("message").asText());
            if (metadata.path("code").asText().equals("200")) {
                Sequel.mengupdateSmc("bridging_surat_kontrol_bpjs",
                    "tgl_rencana = ?, kd_dokter_bpjs = ?, nm_dokter_bpjs = ?, kd_poli_bpjs = ?, nm_poli_bpjs = ?", "no_surat = ?",
                    tglSEP.getText(), kodeDokter, namaDokter.getText(), kodePoli, namaPoli.getText(), noSKDP.getText()
                );
                tglRencanaKontrol = tglSEP.getText();
            } else {
                JOptionPane.showMessageDialog(null, metadata.path("message").asText());
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                Valid.popupPeringatanDialog("Koneksi ke server BPJS terputus..!!");
            }
        }
    }

    private boolean registerPasien() {
        if (isMobileJKN) {
            return true;
        }

        int next = 0, retries = 5;
        boolean sukses = false;

        isCekPasien();

        if (Sequel.cariExistsSmc("select * from reg_periksa where no_rkm_medis = ? and tgl_registrasi = ? and kd_poli = ? and kd_dokter = ? and kd_pj = ?", noRM.getText(), tglSEP.getText(), kodePoliReg, kodeDokterReg, kodeBPJS)) {
            JOptionPane.showMessageDialog(null, "Maaf, Telah terdaftar pemeriksaan hari ini\nSilahkan hubungi bagian pendaftaran..!!");
            return false;
        }

        biayaReg = Sequel.cariIsiSmc("select if(? = 'Lama', poliklinik.registrasi, poliklinik.registrasilama) from poliklinik where poliklinik.kd_poli = ?", statusPoli, kodePoliReg);

        do {
            setNomorRegistrasi();

            System.out.print("Mencoba mendaftarkan pasien dengan no. rawat [" + noRawat + "] : ");

            sukses = Sequel.menyimpantfSmc("reg_periksa", null,
                noReg, noRawat, tglSEP.getText(), Sequel.cariIsiSmc("select current_time()"),
                kodeDokterReg, noRM.getText(), kodePoliReg, namaPJ, alamatPJ, hubunganPJ, biayaReg, "Belum",
                statusDaftar, "Ralan", kodeBPJS, umurDaftar, statusUmur, "Belum Bayar", statusPoli
            );

            System.out.println(sukses ? "Sukses!" : "Gagal!");
        } while (next++ < retries && !sukses);

        if (sukses) {
            Sequel.mengupdateSmc("pasien", "no_tlp = ?, no_ktp = ?, umur = ?", "no_rkm_medis = ?", noTelp.getText(), nik.getText(), umurPasien, noRM.getText());
        }

        return sukses;
    }

    private boolean simpanRujukan() {
        int next = 0, retries = 5;
        boolean sukses = false;

        do {
            String noRujukMasuk = Sequel.cariIsiSmc("select concat('BR/', date_format(?, '%Y/%m/%d'), '/', lpad(ifnull(max(convert(right(rujuk_masuk.no_balasan, 4), signed)), 0) + 1, 4, '0')) from rujuk_masuk where rujuk_masuk.no_balasan like concat('BR/', date_format(?, '%Y/%m/%d/'), '%')", tglSEP.getText(), tglSEP.getText());

            System.out.print("Mencoba memproses rujukan masuk pasien dengan no. surat [" + noRujukMasuk + "]: ");

            sukses = Sequel.menyimpantfSmc("rujuk_masuk", null,
                noRawat, namaPPKRujukan.getText(), "-", noRujukan.getText(), "0",
                namaPPKRujukan.getText(), kodeDiagnosa.getText(), "-", "-", noRujukMasuk
            );

            System.out.println(sukses ? "Sukses!" : "Gagal!");
        } while (next++ < retries && !sukses);

        return sukses;
    }

    private void resetAksi() {
        userAksi.setText("");
        passAksi.setText("");
        aksi = "";
    }

    private void isForm() {
        int preferredWidth = 1280,
            height = panelTengah.getHeight();

        bisaTampilkanNumpad = height > 770;

        if (toggleInfoTambahan.isSelected()) {
            panelUtama.setMinimumSize(new Dimension(preferredWidth, height - 490));
            panelUtama.setPreferredSize(new Dimension(preferredWidth, height - 490));
            panelNumpad.setVisible(false);
        } else {
            panelUtama.setMinimumSize(new Dimension(preferredWidth, height));
            panelUtama.setPreferredSize(new Dimension(preferredWidth, height));
        }

        revalidate();

        panelTambahan.setVisible(toggleInfoTambahan.isSelected());
    }

    private boolean cekWaktuRegistrasi() {
        if (!REGISTRASISATUJAMSEBELUMJAMPRAKTEK) {
            return true;
        }

        Calendar cal = Calendar.getInstance();
        Instant now = cal.toInstant();

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(jamPraktek.substring(0, 2)));
        cal.set(Calendar.MINUTE, Integer.parseInt(jamPraktek.substring(3, 5)));
        cal.set(Calendar.SECOND, 0);
        Instant jamMasuk = cal.toInstant();

        return now.isAfter(jamMasuk.minus(1, ChronoUnit.HOURS));
    }
}
