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
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public class DlgUser extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private String user="",jabatan="",copyhakakses="",userdicopy="";
    private int i=0,barisdicopy=-1;
    private boolean ceksukses=false;
    private DlgCariDokter dlgdokter;
    private DlgCariPetugas dlgpetugas;
    private DlgUpdateUser personal;

    /** Creates new form DlgUser
     * @param parent
     * @param modal */
    public DlgUser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row={"ID User","Nama User","Jabatan","Password","[J]ICD 10","[J]Obat Penyakit","[C]Dokter","[A]Jadwal Praktek","[C]Petugas","[M]Pasien","[A]Registrasi","[A]Tindakan Ralan",
                "[A]Rawat Inap","[A]Tindakan Ranap","[A]Operasi","[A]Rujukan Keluar","[A]Rujukan Masuk","[A]Beri Obat, Alkes & BHP","[A]Resep Pulang",
                "[M]Pasien Meninggal","[A]Diet Pasien","[M]Kelahiran Bayi","[A]Periksa Lab PK","[A]Periksa Radiologi","[A]Rawat Jalan",
                "[K]Deposit Pasien","[K]Piutang Pasien","[M]Peminjaman Berkas RM","[C]Barcode Presensi","[C]Presensi Harian","[C]Presensi Bulanan",
                "[C]Pegawai Admin","[C]Pegawai User","[D]Suplier Obat/Alkes/BHP","[D]Satuan Barang","[D]Konversi Satuan","[D]Jenis Obat/Alkes/BHP","[D]Obat, Alkes & BHP",
                "[D]Stok Opname Apotek","[D]Stok Obat Pasien","[D]Pengadaan Obat, Alkes & BHP","[D]Penerimaan Obat, Alkes & BHP","[D]Penjualan Obat, Alkes & BHP","[D]Piutang Obat, Alkes & BHP",
                "[D]Retur Ke Suplier","[D]Retur Dari Pembeli","[D]Retur Obat, Alkes & BHP Ranap","[D]Retur Piutang Pembeli","[D]Keuntungan Penjualan",
                "[D]Keuntungan Beri Obat, Alkes & BHP","[D]Sirkulasi Obat, Alkes & BHP","[E]Barang Non Medis","[E]Pengadaan Barang Nonmedis","[E]Stok Keluar Non Medis",
                "[E]Rekap Pengadaan Non Medis","[E]Rekap Stok Keluar Non Medis","[E]Biaya Pengadaan Non Medis","[G]Jenis Inventaris",
                "[G]Kategori Inventaris","[G]Merk Inventaris","[G]Ruang Inventaris","[G]Produsen Inventaris","[G]Koleksi Inventaris",
                "[G]Inventaris","[G]Sirkulasi Inventaris","[H]Jenis Parkir","[H]Parkir Masuk","[H]Parkir Keluar","[H]Rekap Parkir Harian",
                "[H]Rekap Parkir Bulanan","[A]Informasi Kamar","[I]Harian Dokter Poli","[I]Obat Per Poli","[I]Obat Per Kamar",
                "[I]Obat Per Dokter Ralan","[I]Obat Per Dokter Ranap","[I]Harian Dokter","[I]Bulanan Dokter","[I]Harian Paramedis",
                "[I]Bulanan Paramedis","[I]Pembayaran Ralan","[I]Pembayaran Ranap","[I]Rekap Pembayaran Ralan","[I]Rekap Pembayaran Ranap",
                "[I]Tagihan Masuk","[I]Tambahan Biaya","[I]Potongan Biaya","[A]No.Resep","[M]Riwayat Perawatan","[J]Frekuensi Penyakit Ralan","[J]Frekuensi Penyakit Ranap",
                "[K]Kamar","[K]Tarif Ralan","[K]Tarif Ranap","[K]Tarif Lab","[K]Tarif Radiologi","[K]Tarif Operasi","[K]Akun Rekening","[K]Rekening Tahun",
                "[K]Posting Jurnal","[K]Buku Besar","[K]Cash Flow","[K]Keuangan","[K]Pengeluaran Harian","[U]Set P.J. Unit Penunjang","[U]Set Oto Lokasi","[U]Set Kamar Inap",
                "[U]Set Embalase & Tuslah","[U]Tracer Login","[U]Display Antrian Registrasi & Poli","[U]Set Harga Obat","[U]Set Penggunaan Tarif","[U]Set Oto Ralan","[U]Biaya Harian Kamar",
                "[U]Biaya Masuk Sekali","[U]Set RM","[A]Billing Ralan","[A]Billing Ranap","[I]Detail JM Dokter","[A]IGD","[B]Barcode Ralan","[B]Barcode Ranap",
                "[U]Set Harga Obat Ralan","[U]Set Harga Obat Ranap","[J]Penyakit AFP & PD3I","[J]Surveilans AFP & PD3I","[J]Surveilans Ralan","[M]Diagnosa Pasien",
                "[J]Surveilans Ranap","[J]Pny.Tdk Menular Ranap","[J]Pny.Tdk Menular Ralan","[J]Kunjungan Ralan","[J]RL 3.2 Rawat Darurat","[J]RL 3.3 Gigi dan Mulut","[J]RL 3.7 Radiologi","[J]RL 3.8 Laboratorium","[I]Harian Dokter Ralan",
                "[C]SMS Gateway","[C]Sidik Jari","[C]Jam Presensi","[C]Jadwal Pegawai","[H]Barcode Parkir","[U]Set Billing","[A]DPJP Ranap","[D]Mutasi Obat/Alkes/BHP","[J]RL 3.4 Kebidanan","[J]RL 3.6 Pembedahan",
                "[I]Fee Visit Dokter","[I]Fee Bacaan EKG","[I]Fee Rujukan Rontgen","[I]Fee Rujukan Ranap","[I]Fee Periksa Ralan","[K]Akun Bayar","[K]Bayar Pesan Obat",
                "[I]Obat Per Dokter Peresep","[E]Jenis Barang Non Medis","[K]Pemasukkan Lain-Lain","[K]Pengaturan Rekening","[U]Closing Kasir","[U]Set Keterlambatan Presensi",
                "[U]Set Harga Kamar","[I]Rekap Per Shift","[L]Cek NIK","[L]Cek No.Kartu","[L]Riwayat Rujukan PCare di VClaim","[I]Obat Per Cara Bayar","[J]Kunjungan Ranap",
                "[K]Bayar Piutang","[I]Payment Point","[L]Cek No.Rujukan PCare di VClaim","[J]ICD 9","[D]Darurat Stok","[M]Retensi Data R.M.","[C]Temporary Presensi",
                "[K]Jurnal Harian","[D]Sirkulasi Obat, Alkes & BHP 2","[A]Edit Registrasi","[L]Referensi Diagnosa VClaim","[L]Referensi Poli VClaim","[D]Industri Farmasi",
                "[I]Harian J.S.","[I]Bulanan J.S.","[I]Harian BHP Medis/Paket Obat","[I]Bulanan BHP Medis/Paket Obat","[K]Piutang Belum Lunas","[L]Referensi Faskes VClaim",
                "[L]Data Bridging SEP VClaim","[D]Pengambilan BHP UTD","[K]Tarif UTD","[N]Pengambilan BHP Medis","[N]BHP Medis Rusak","[E]Pengambilan UTD","[N]Pengambilan BHP Non Medis",
                "[N]BHP Non Medis Rusak","[E]Suplier Non Medis","[N]Donor Darah","[L]Monitoring Verifikasi Klaim","[N]Cekal Darah","[N]Komponen Darah","[N]Stok Darah","[N]Pemisahan Darah",
                "[I]Harian Kamar","[K]Rincian Piutang Pasien","[D]Keuntungan Beri Obat, Alkes & BHP 2","[L]Reklasifikasi Ralan","[L]Reklasifikasi Ranap","[N]Penyerahan Darah",
                "[K]Hutang Obat & BHP","[D]Riwayat Obat, Alkes & BHP","[J]Sensus Harian Poli","[J]RL 4A Sebab Morbiditas Ranap","[L]Referensi Kamar Aplicare","[L]Ketersediaan Kamar Aplicare",
                "[L]Klaim Baru Otomatis INACBG","[L]Klaim Baru Manual INACBG","[L]Coder NIK INACBG","[M]Mutasi Berkas RM","[K]Akun Piutang","[I]Harian KSO","[I]Bulanan KSO",
                "[I]Harian Menejemen","[I]Bulanan Menejemen","[L]Cek Eligibilitas Inhealth","[L]Referensi Ruang Rawat Inhealth","[L]Referensi Poli Inhealth","[L]Referensi Faskes Inhealth",
                "[L]Data Bridging SJP Inhealth","[I]Piutang Ralan","[I]Piutang Ranap","[K]Piutang Per Cara Bayar","[J]Lama Pelayanan Ralan","[M]Catatan Pasien","[J]RL 4B Sebab Morbiditas Ralan",
                "[J]RL 4A Morbiditas Ralan","[J]RL 4B Morbiditas Ralan","[M]Data HAIs","[J]Harian HAIs","[J]Bulanan HAIs","[J]Hitung BOR","[M]Instansi/Perusahaan Pasien","[D]Resep Dokter",
                "[J]Lama Pelayanan Apotek","[J]Hitung ALOS","[I]Detail Tindakan","[A]Rujukan Poli Internal","[I]Rekap Poli Anak","[O]Registrasi Per Poli","[O]Registrasi Per Dokter",
                "[O]Registrasi Per Pekerjaan","[O]Registrasi Per Pendidikan","[O]Registrasi Per Tahun","[M]Berkas Digital Perawatan","[J]Pny Menular Ranap","[J]Pny Menular Ralan",
                "[O]Registrasi Per Bulan","[O]Registrasi Per Tanggal","[O]Demografi Registrasi","[O]Reg Lama Per Tahun","[O]Reg Baru Per Tahun","[O]Reg Lama Per Bulan","[O]Reg Baru Per Bulan",
                "[O]Reg Lama Per Tanggal","[O]Reg Baru Per Tanggal","[O]Batal Periksa Per Tahun","[O]Batal Periksa Per Bulan","[L]Referensi Diagnosa Pcare","[O]Batal Periksa Per Tanggal",
                "[D]Kategori Obat/Alkes/BHP","[D]Golongan Obat/Alkes/BHP","[D]Obat/Alkes/BHP Per Tanggal","[D]Penjualan Bebas Per Tanggal","[L]Referensi Kesadaran Pcare","[J]Pembatalan Periksa Per Dokter",
                "[I]Pembayaran Per Unit","[I]Rekap Pembayaran Per Unit","[O]Registrasi Per Cara Bayar","[E]Pengadaan Non Medis Per Tanggal","[E]Stok Keluar Non Medis Per Tanggal",
                "[O]Kunjungan Ranap Per Tahun","[L]Cek Rujukan PCare","[O]Kunjungan Lab Ralan Per Tahun","[O]Kunjungan Rad Ralan Per Tahun","[J]Cek Entry Ralan","[L]Klaim Baru Manual INACBG 2",
                "[D]Permintaan Obat & BHP","[D]Ringkasan Permintaan Obat & BHP","[D]Surat Pemesanan Obat & BHP","[E]Permintaan Barang Non Medis","[E]Ringkasan Permintaan Barang Non Medis",
                "[E]Surat Pemesanan Barang Non Medis","[O]Registrasi Per Perujuk","[L]Referensi Prosedur VClaim","[L]Referensi Kelas Rawat VClaim","[L]Referensi Dokter VClaim",
                "[L]Referensi Spesialistik VClaim","[L]Referensi Ruang Rawat VClaim","[L]Referensi Cara Keluar VClaim","[L]Referensi Pasca Pulang VClaim","[I]Detail VK/OK","[A]Billing Parsial",
                "[L]Cek No.Rujukan RS di VClaim","[L]Cek Rujukan Kartu PCare di VClaim","[L]Cek Rujukan Kartu RS di VClaim","[A]Akses Depo Obat/BHP","[L]Pembuatan Rujukan VClaim",
                "[O]Kunjungan Lab Ralan Per Bulan","[D]Stok Keluar Medis","[O]Kunjungan Rad Ralan Per Bulan","[I]Detail JM Dokter 2","[M]Pengaduan/Chat","[O]Kunjungan Lab Ralan Per Tanggal",
                "[O]Kunjungan Rad Ralan Per Tanggal","[J]Sensus Harian Ralan","[D]Metode Racik","[I]Pembayaran Per Akun Bayar","[D]Pengguna Obat/Alkes/BHP Resep","[D]Rekap Penerimaan Obat & BHP",
                "[C]Master Berkas Pegawai","[C]Berkas Kepegawaian","[C]Riwayat Jabatan","[C]Riwayat Pendidikan","[C]Riwayat Naik Gaji","[C]Kegiatan Ilmiah & Pelatihan","[C]Riwayat Penghargaan",
                "[C]Riwayat Penelitian","[E]Penerimaan Barang Non Medis","[K]Bayar Pesan Non Medis","[K]Hutang Barang Non Medis","[E]Rekap Penerimaan Non Medis","[J]Insiden Keselamatan",
                "[M]Insiden Keselamatan Pasien","[O]Kejadian IKP Per Tahun","[O]Kejadian IKP Per Bulan","[O]Kejadian IKP Per Tanggal","[D]Riwayat Batch","[O]Kejadian IKP Per Jenis",
                "[O]Kejadian IKP Per Dampak","[I]Piutang Per Akun Piutang","[O]Registrasi Per Agama","[O]Registrasi Per Umur","[M]Suku/Bangsa Pasien","[M]Bahasa Pasien","[M]Golongan TNI",
                "[M]Satuan TNI","[M]Jabatan TNI","[M]Pangkat TNI","[M]Golongan POLRI","[M]Satuan POLRI","[M]Jabatan POLRI","[M]Pangkat POLRI","[M]Cacat Fisik","[O]Registrasi Per Suku/Bangsa",
                "[O]Registrasi Per Bahasa","[A]Jadwal Operasi","[L]Mapping Poli VClaim","[O]Registrasi Per Cacat Fisik","[G]Barang CSSD","[P]Surat Kontrol","[A]Booking Registrasi",
                "[L]Referensi Propinsi VClaim","[L]Referensi Kabupaten VClaim","[L]Referensi Kecamatan VClaim","[L]Referensi Dokter DPJP VClaim","[L]Riwayat Rujukan RS di VClaim",
                "[L]Tanggal Rujukan di VClaim","[A]Permintaan Lab","[A]Permintaan Radiologi","[P]Indeks Surat","[P]Map Surat","[P]Almari Surat","[P]Rak Surat","[P]Ruang Surat",
                "[P]Klasifikasi Surat","[P]Status Surat","[P]Sifat Surat","[P]Stts Balas Surat","[P]Surat Masuk","[L]Referensi Dokter PCare","[L]Referensi Poli PCare",
                "[L]Referensi Provider PCare","[L]Referensi Stts Pulang PCare","[L]Referensi Spesialis PCare","[L]Referensi Subspesialis PCare","[L]Referensi Sarana PCare",
                "[L]Referensi Khusus PCare","[L]Referensi Obat PCare","[L]Referensi Tindakan PCare","[L]Faskes Subspesialis PCare","[L]Faskes Alih Rawat PCare",
                "[L]Faskes Thalasemia & Hemofili PCare","[L]Mapping Obat PCare","[L]Tarif Ralan RS & PCare","[L]Club Prolanis PCare","[L]Mapping Poli PCare",
                "[L]Kegiatan Kelompok PCare","[L]Tarif Ranap RS & PCare","[L]Peserta Keg Kelompok PCare","[D]Sirkulasi Obat, Alkes & BHP 3","[L]Data Pendafataran PCare",
                "[L]Mapping Dokter PCare","[J]Ranap Per Ruang","[J]Penyakit Ranap Per Cara Bayar","[J]Anggota TNI Dirawat","[U]Set Input Parsial",
                "[J]Lama Pelayanan Radiologi","[J]Lama Pelayanan Lab","[L]Cek Nomor SEP","[A]Catatan Dokter","[P]Surat Keluar","[D]Kegiatan Farmasi",
                "[E]Stok Opname Non Medis","[E]Sirkulasi Non Medis","[J]Rekap Lab Per Tahun","[J]Perujuk Lab Per Tahun","[J]Rekap Radiologi Per Tahun",
                "[J]Perujuk Radiologi Per Tahun","[J]Rekap Bulanan Porsi Diet","[J]Rekap Bulanan Macam Diet","[I]Payment Point 2","[I]Pembayaran Per Akun Bayar 2",
                "[I]Hapus Nota Salah","[A]Asesmen Awal Rawat Inap","[J]HAIs Per Kamar/Bangsal","[D]PPN Obat","[K]Saldo Akun Per Bulan","[U]Display Antrian Apotek",
                "[L]Referensi Faskes Sisrute","[L]Referensi Alasan Rujuk Sisrute","[L]Referensi Diagnosa Sisrute","[L]Rujukan Masuk Sisrute","[L]Rujukan Keluar Sisrute",
                "[L]Cek SKDP VClaim","[D]Data Batch","[J]Kunjungan Lab Ralan","[J]Kunjungan Lab Ranap","[J]Kunjungan Radiologi Ralan","[J]Kunjungan Radiologi Ranap",
                "[L]Pemberian Obat PCare","[L]Pemberian Tindakan PCare","[I]Pembayaran Per Akun Bayar 3","[U]Password BPJS","[J]Data TB","[L]Ketersediaan Kamar SIRANAP",
                "[O]Periode Laporan TB","[O]Rujukan TB","[O]Riwayat TB","[O]Tipe Diagnosis TB","[O]Status HIV TB","[O]Skoring Anak TB","[O]Konfirmasi Skoring 5 TB",
                "[O]Konfirmasi Skoring 6 TB","[O]Sumber Obat TB","[O]Hasil Akhir Pengobatan TB","[O]Hasil Tes HIV TB","[D]Kadaluarsa Batch","[D]Sisa Stok",
                "[D]Obat Per Resep","[G]Pemakaian Air PDAM","[G]Limbah Padat B3 Medis","[O]Pemakaian Air PDAM Per Tanggal","[O]Pemakaian Air PDAM Per Bulan",
                "[O]Limbah B3 Padat Per Tanggal","[O]Limbah B3 Padat Per Bulan","[G]Limbah Padat Domestik","[O]Limbah Padat Domestik Per Tanggal",
                "[O]Limbah Padat Domestik Per Bulan","[G]Mutu Air Limbah","[G]Pest Control","[Q]Ruang Perpustakaan","[Q]Kategori Koleksi","[Q]Jenis Koleksi",
                "[Q]Pengarang/Penulis","[Q]Penerbit Koleksi","[Q]Koleksi Perpustakaan","[Q]Inventaris Perpustakaan","[Q]Pengaturan Peminjaman","[Q]Denda Perpustakaan",
                "[Q]Anggota Perpustakaan","[Q]Peminjaman Koleksi Perpustakaan","[Q]Bayar Denda Perpustakaan","[Q]Data Koleksi Ebook","[C]Jenis Cidera K3",
                "[C]Penyebab Kecelakaan K3","[C]Jenis Luka K3","[C]Lokasi Kejadian K3","[C]Dampak Cidera K3","[C]Jenis Pekerjaan K3","[C]Bagian Tubuh K3",
                "[C]Peristiwa K3","[O]K3 Per Tahun","[O]K3 Per Bulan","[O]K3 Per Tanggal","[O]K3 Per Jenis Cidera","[O]K3 Per Penyebab Kecelakaan","[O]K3 Per Jenis Luka",
                "[O]K3 Per Lokasi Kejadian","[O]K3 Per Dampak Cidera","[O]K3 Per Jenis Pekerjaan","[O]K3 Per Bagian Tubuh","[C]Jenis Cidera K3 Per Tahun",
                "[C]Penyebab Kecelakaan K3 Per Tahun","[C]Jenis Luka K3 Per Tahun","[C]Lokasi Kejadian K3 Per Tahun","[C]Dampak Cidera K3 Per Tahun",
                "[C]Jenis Pekerjaan K3 Per Tahun","[C]Bagian Tubuh K3 Per Tahun","[A]Skrining Rawat Jalan","[L]Histori Pelayanan BPJS","[J]Rekap Mutasi Berkas",
                "[J]Skrining Pernapasan Ralan Per Tahun","[D]Pengajuan Obat & BHP","[E]Pengajuan Barang Non Medis","[O]Kunjungan Ranap Per Bulan","[O]Kunjungan Ranap Per Tanggal",
                "[O]Kunjungan Ranap Per Ruang","[J]Masuk Ruang Per Tahun","[O]Pegawai Per Jenjang Jabatan","[O]Pegawai Per Bidang/Bagian","[O]Pegawai Per Departemen",
                "[O]Pegawai Per Pendidikan","[O]Pegawai Per Status WP","[O]Pegawai Per Status Kerja","[O]Status Pulang Ranap","[J]KIP Pasien Ranap","[J]KIP Pasien Ralan",
                "[L]Mapping Dokter DPJP VClaim","[M]Data Triase IGD","[M]Master Triase Skala 1","[M]Master Triase Skala 2","[M]Master Triase Skala 3","[M]Master Triase Skala 4",
                "[M]Master Triase Skala 5","[M]Master Triase Pemeriksaan","[M]Master Triase Macam Kasus","[J]Rekap Permintaan Diet","[J]Daftar Pasien Ranap",
                "[J]Daftar Pasien Ranap TNI","[G]Pengajuan Aset/Inventaris","[O]Item Apotek Per Jenis","[O]Item Apotek Per Kategori","[O]Item Apotek Per Golongan",
                "[O]Item Apotek Per Industri Farmasi","[D]10 Obat Terbanyak Poli","[O]Pengajuan Aset Per Urgensi","[O]Pengajuan Aset Per Status",
                "[O]Pengajuan Aset Per Departemen","[G]Rekap Pengajuan Aset Departemen","[O]Pegawai Per Kelompok Jabatan","[O]Pegawai Per Risiko Kerja",
                "[O]Pegawai Per Emergency Index","[O]Jumlah Inventaris Per Ruang","[J]Harian HAIs 2","[O]Jumlah Inventaris Per Jenis","[M]Resume Pasien",
                "[A]Perkiraan Biaya Ranap","[D]Rekap Obat Per Poli","[D]Rekap Obat Per Pasien","[G]Permintaan Perbaikan Inventaris","[O]Pasien HAIs Per Ruang",
                "[O]Pasien HAIs Per Bulan","[O]Laju HAIs VAP Per Ruang","[O]Laju HAIs IAD Per Ruang","[O]Laju HAIs Plebitis Per Ruang","[O]Laju HAIs ISK Per Ruang",
                "[O]Laju HAIs ILO Per Ruang","[O]Laju HAIs HAP Per Ruang","[L]Mapping Poli Inhealth","[L]Mapping Dokter Inhealth","[L]Tarif Ralan Inhealth",
                "[L]Tarif Ranap Inhealth","[L]Tarif Radiologi Inhealth","[L]Tarif Laborat Inhealth","[L]Tarif Operasi Inhealth","[D]Hibah Obat & BHP","[G]Asal Hibah",
                "[M]Asuhan Gizi","[L]Tagihan Inhealth","[D]Sirkulasi Obat, Alkes & BHP 4","[D]Sirkulasi Obat, Alkes & BHP 5","[E]Sirkulasi Non Medis 2",
                "[M]Monitoring Asuhan Gizi","[O]Penerimaan Obat, Alkes & BHP Per Bulan","[J]Rekap Kunjungan","[P]Surat Keterangan Sakit","[M]Pengkajian Awal Keperawatan Ralan Umum",
                "[A]Permintaan Diet","[M]Master Masalah Keperawatan","[C]Pengajuan Cuti","[J]Kedatangan Pasien Per Jam","[N]Data Pendonor","[S]Suplier Toko",
                "[S]Jenis Barang Toko","[U]Set Harga Toko","[S]Barang Toko","[K]Penagihan Piutang Pasien","[K]Akun Penagihan Piutang","[S]Stok Opname Toko",
                "[S]Riwayat Barang Toko","[S]Surat Pemesanan Toko","[S]Pengajuan Barang Toko","[S]Penerimaan Barang Toko","[S]Pengadaan Barang Toko","[S]Hutang Toko",
                "[S]Bayar Pesan Toko","[S]Member Toko","[S]Penjualan Toko","[J]Registrasi Poli Per Tanggal","[S]Piutang Toko","[S]Retur Ke Suplier Toko",
                "[E]Retur Ke Suplier Non Medis","[E]Riwayat Barang Non Medis","[L]Pasien Corona","[S]Pendapatan Harian Toko","[L]Diagnosa Pasien Corona",
                "[L]Perawatan Pasien Corona","[M]Pengkajian Awal Keperawatan Gigi","[M]Master Masalah Keperawatan Gigi","[S]Bayar Piutang Toko","[S]Piutang Harian Toko",
                "[S]Penjualan Harian Toko","[A]Deteksi Dini Corona","[M]Pengkajian Awal Keperawatan Ralan Kebidanan","[P]Pengumuman E-Pasien","[P]Surat Hamil","[K]Set Tarif Online",
                "[A]Booking Periksa","[S]Sirkulasi Barang Toko","[S]Retur Jual Toko","[S]Retur Jual Piutang","[S]Sirkulasi Barang Toko 2","[S]Keuntungan Barang Toko",
                "[T]Ket Pengeluaran Penerima Dankes","[T]Ket Penghasilan Penerima Dankes","[T]Ukuran Rumah Penerima Dankes","[T]Dinding Rumah Penerima Dankes",
                "[T]Lantai Rumah Penerima Dankes","[T]Atap Rumah Penerima Dankes","[T]Kepemilikan Rumah Penerima Dankes","[T]Kamar Mandi Penerima Dankes",
                "[T]Dapur Rumah Penerima Dankes","[T]Kursi Rumah Penerima Dankes","[T]Kategori PHBS Penerima Dankes","[T]Elektronik Penerima Dankes",
                "[T]Ternak Penerima Dankes","[T]Jenis Simpanan Penerima Dankes","[M]Pengkajian Awal Keperawatan Ralan Bayi/Anak","[T]Kategori Asnaf Penerima Dankes",
                "[M]Master Masalah Keperawatan Bayi/Anak","[M]Master Imunisasi","[T]Patologis Penerima Dankes","[L]Cek No.Kartu PCare","[P]Surat Bebas Narkoba",
                "[P]Surat Keterangan Covid","[G]Pemakaian Air Tanah","[O]Pemakaian Air Tanah Per Tanggal","[O]Pemakaian Air Tanah Per Bulan",
                "[J]Lama Pelayanan Poli","[M]Hemodialisa","[J]Laporan Tahunan IRJ","[O]Hemodialisa Per Tanggal","[O]Hemodialisa Per Bulan","[O]Hemodialisa Per Tahun",
                "[O]Pasien Meninggal Per Bulan","[G]Perbaikan Inventaris","[P]Surat Cuti Hamil","[D]Permintaan Stok Obat Pasien","[G]Pemeliharaan Inventaris",
                "[M]Klasifikasi Pasien Ranap","[J]Bulanan Klasifikasi Pasien Ranap","[J]Harian Klasifikasi Pasien Ranap","[J]Klasifikasi Pasien Per Ruang",
                "[M]SOAP Perawatan","[K]Klaim Rawat Jalan","[M]Skrining Gizi Lanjut","[J]Lama Penyiapan RM","[J]Dosis Radiologi","[J]Demografi Umur Kunjungan",
                "[U]Jam Diet Pasien","[K]RVP Piutang BPJS","[D]Verifikasi Penerimaan Obat/Alkes/BHP","[E]Verifikasi Penerimaan Non Medis","[A]Periksa Lab PA",
                "[D]Ringkasan Pengajuan Obat & BHP","[D]Ringkasan Pemesanan Obat & BHP","[D]Ringkasan Pengadaan Obat & BHP","[D]Ringkasan Penerimaan Obat & BHP",
                "[D]Ringkasan Hibah Obat & BHP","[D]Ringkasan Penjualan Obat & BHP","[D]Ringkasan Beri Obat & BHP","[D]Ringkasan Piutang Obat & BHP",
                "[D]Ringkasan Stok Keluar Obat & BHP","[D]Ringkasan Retur Suplier Obat & BHP","[D]Ringkasan Retur Pembeli Obat & BHP","[M]Pengkajian Awal Keperawatan Ranap Kebidanan",
                "[E]Ringkasan Pengajuan Non Medis","[E]Ringkasan Pemesanan Non Medis","[E]Ringkasan Pengadaan Non Medis","[E]Ringkasan Penerimaan Non Medis",
                "[E]Ringkasan Stok Keluar Non Medis","[E]Ringkasan Retur Suplier Non Medis","[K]Penerimaan/Omset/Kas Masuk","[K]Validasi Penagihan Piutang",
                "[A]Permintaan Rawat Inap","[L]Referensi Diagnosa PRB VClaim","[L]Referensi Obat PRB VClaim","[L]Surat Kontrol VClaim","[D]Penggunaan BHP OK/VK",
                "[P]Surat Keterangan Rawat Inap","[P]Surat Keterangan Sehat","[K]Pendapatan Per Cara Bayar","[L]Host To Host Bank Jateng","[K]Pembayaran Bank Jateng",
                "[L]Surat PRI VClaim","[I]Ringkasan Tindakan","[J]Lama Pelayanan Pasien","[P]Surat Keterangan Sakit Pihak 2","[K]Titip Faktur/Tagihan Obat & BHP",
                "[L]Referensi Pendaftaran Mobile JKN","[L]Batal Pendaftaran Mobile JKN","[J]Lama Operasi","[O]Jumlah Inventaris Per Kategori","[O]Jumlah Inventaris Per Merk",
                "[O]Jumlah Inventaris Per Produsen","[K]Pengembalian Deposit Pasien","[K]Validasi Titip Faktur/Tagihan Obat & BHP","[K]Piutang Obat & BHP Belum Lunas",
                "[L]Integrasi BRI API","[G]Pengadaan Aset/Inventaris","[K]Akun Jenis Aset/Inventaris","[G]Suplier Aset/Inventaris","[G]Penerimaan Aset/Inventaris",
                "[K]Bayar Pesan Aset/Inventaris","[K]Hutang Aset/Inventaris","[G]Hibah Aset/Inventaris","[K]Titip Faktur/Tagihan Non Medis","[K]Validasi Titip Faktur/Tagihan Non Medis",
                "[K]Titip Faktur/Tagihan Aset/Inventaris","[K]Validasi Titip Faktur/Tagihan Aset/Inventaris","[E]Hibah Non Medis","[L]Referensi TACC PCare","[D]Resep Luar",
                "[P]Surat Bebas TBC","[P]Surat Keterangan Buta Warna","[P]Surat Bebas Tato","[P]Surat Kewaspadaan Kesehatan","[O]Porsi Diet Per Tanggal","[O]Porsi Diet Per Bulan",
                "[O]Porsi Diet Per Tahun","[O]Porsi Diet Per Ruang","[M]Pengkajian Awal Medis Ralan Umum","[M]Master Masalah Keperawatan Mata","[M]Pengkajian Awal Keperawatan Ralan Mata",
                "[M]Pengkajian Awal Medis Ranap Umum","[M]Pengkajian Awal Medis Ranap Kandungan","[M]Pengkajian Awal Medis Ralan Kandungan","[M]Pengkajian Awal Medis IGD",
                "[M]Pengkajian Awal Medis Ralan Bayi/Anak","[L]Referensi Poli HFIS","[L]Referensi Dokter HFIS","[L]Referensi Jadwal HFIS","[M]Pengkajian Awal Fisioterapi",
                "[L]Program PRB di VClaim","[L]Suplesi Jasa Raharja di VClaim","[L]Data Induk Kecelakaan VClaim","[L]Data SEP Internal VClaim","[L]Klaim Jaminan Jasa Raharja VClaim",
                "[L]Pasien Finger Print VClaim","[L]Rujukan Khusus VClaim","[G]Pemeliharaan Gedung","[O]Perbaikan Inventaris Per Tanggal","[O]Perbaikan Inventaris Per Bulan",
                "[O]Perbaikan Inventaris Per Tahun","[O]Perbaikan Inventaris Per Pelaksana & Status","[M]Pengkajian MCU","[K]Peminjam Piutang","[K]Piutang Peminjaman Uang",
                "[K]Asuransi/Askes/Jenis Bayar","[C]Audit Kepatuhan APD","[L]Task ID Mobile JKN","[K]Bayar Piutang Peminjaman Uang","[I]Pembayaran Per Akun Bayar 4",
                "[D]Stok Akhir Farmasi Per Tanggal","[M]Riwayat Kamar Pasien","[M]Uji Fungsi/Prosedur KFR","[M]Hapus Berkas Digital Perawatan","[K]Kategori Pengeluaran Harian",
                "[K]Kategori Pemasukan Lain-lain","[I]Pembayaran Per Akun Bayar 5","[U]Ruang Operasi","[D]Pengkajian Resep & Obat","[I]Jasa Tindakan Pasien","[D]Permintaan Resep Pulang",
                "[I]Rekap JM Dokter","[J]Status Data RM","[A]Ubah Petugas Lab PK","[A]Ubah Petugas Lab PA","[A]Ubah Petugas Radiologi","[A]Gabung Nomor Rawat","[M]Gabungkan Data RM",
                "[D]Ringkasan Biaya Obat Pasien Per Tanggal","[M]Master Masalah Keperawatan IGD","[M]Pengkajian Awal Keperawatan IGD","[L]Referensi DPHO Apotek BPJS",
                "[L]Referensi Poli Apotek BPJS","[K]Bayar JM Dokter","[L]Referensi Faskes Apotek BPJS","[L]Referensi Spesialistik Apotek BPJS","[K]Pembayaran BRIVA",
                "[M]Pengkajian Awal Keperawatan Ranap Umum","[D]Nilai Penerimaan Vendor Farmasi Per Bulan","[K]Akun Bayar Hutang","[M]Master Rencana Keperawatan",
                "[J]Laporan Tahunan IGD","[D]Obat/Alkes/BHP Tidak Bergerak","[K]Ringkasan Hutang Vendor Farmasi","[E]Nilai Penerimaan Vendor Non Medis Per Bulan",
                "[K]Ringkasan Hutang Vendor Non Medis","[M]Master Rencana Keperawatan Bayi/Anak","[J]Anggota POLRI Dirawat","[J]Daftar Pasien Ranap POLRI","[M]SOAP Ralan Anggota POLRI",
                "[M]SOAP Ranap Anggota POLRI","[J]Laporan Penyakit POLRI","[J]Jumlah Pengunjung Ralan POLRI","[M]Catatan Observasi IGD","[M]Catatan Observasi Ranap",
                "[M]Catatan Observasi Ranap Kebidanan","[M]Catatan Observasi Ranap Post Partum","[M]Pengkajian Awal Medis Ralan THT","[M]Pengkajian Psikologi",
                "[C]Audit Cuci Tangan Medis","[C]Audit Pembuangan Limbah","[C]Ruang/Unit Audit Kepatuhan","[C]Audit Pembuangan Benda Tajam & Jarum",
                "[C]Audit Penanganan Darah","[C]Audit Pengelolaan Linen Kotor","[C]Audit Penempatan Pasien","[C]Audit Kamar Jenazah","[C]Audit Bundle IADP",
                "[C]Audit Bundle IDO","[C]Audit Fasilitas Kebersihan Tangan","[C]Audit Fasilitas APD","[C]Audit Pembuangan Limbah Cair Infeksius","[C]Audit Sterilisasi Alat",
                "[M]Pengkajian Awal Medis Ralan Psikiatri","[P]Persetujuan/Penolakan Tindakan","[C]Audit Bundle ISK","[C]Audit Bundle PLABSI","[C]Audit Bundle VAP",
                "[L]Host To Host Bank Papua","[K]Pembayaran Bank Papua","[M]Pengkajian Awal Medis Ralan Penyakit Dalam","[M]Pengkajian Awal Medis Ralan Mata",
                "[M]Pengkajian Awal Medis Ralan Neurologi","[D]Sirkulasi Obat, Alkes & BHP 6","[M]Pengkajian Awal Medis Ralan Orthopedi","[M]Pengkajian Awal Medis Ralan Bedah",
                "[U]Integrasi Khanza Health Services","[M]SOAP Ralan Anggota TNI","[M]SOAP Ranap Anggota TNI","[J]Jumlah Pengunjung Ralan TNI","[J]Laporan Penyakit TNI",
                "[M]Catatan Keperawatan Ranap","[M]Master Rencana Keperawatan Gigi","[M]Master Rencana Keperawatan Mata","[M]Master Rencana Keperawatan IGD",
                "[M]Master Masalah Keperawatan Psikiatri","[M]Master Rencana Keperawatan Psikiatri","[M]Pengkajian Awal Keperawatan Ralan Psikiatri","[M]Pemantauan PEWS Pasien Anak",
                "[P]Pulang Atas Permintaan Sendiri","[M]Master Template Hasil Radiologi","[J]Laporan Bulanan IRJ","[M]Master Template Pemeriksaan","[A]Periksa Lab MB",
                "[A]Ubah Petugas Lab MB","[M]Pengkajian Pre Operasi","[M]Pengkajian Pre Anestesi","[M]Perencanaan Pemulangan","[M]Pengkajian Lanjutan Risiko Jatuh Dewasa",
                "[M]Pengkajian Lanjutan Risiko Jatuh Anak","[M]Pengkajian Awal Medis Ralan Geriatri","[M]Pengkajian Tambahan Pasien Geriatri","[M]Skrining Nutrisi Pasien Dewasa",
                "[M]Skrining Nutrisi Pasien Lansia","[M]Hasil USG Kandungan","[M]Skrining Nutrisi Pasien Anak","[L]Host To Host Bank Jabar","[K]Pembayaran Bank Jabar",
                "[P]Pernyataan Pasien Umum","[M]Konseling Farmasi","[M]Pelayanan Informasi Obat","[M]Jawaban PIO Apoteker","[P]Persetujuan Umum","[M]Transfer Pasien Antar Ruang",
                "[L]Referensi Praktisi Satu Sehat","[L]Referensi Pasien Satu Sehat","[L]Mapping Organisasi Satu Sehat","[L]Mapping Lokasi Satu Sehat","[L]Kirim Encounter Satu Sehat",
                "[M]Catatan Cek GDS","[L]Kirim Condition Satu Sehat","[M]Check List Pre Operasi","[L]Kirim Observation-TTV Satu Sehat","[M]Sign-In Sebelum Anestesi",
                "[L]Kirim Procedure Satu Sehat","[J]Operasi Per Bulan","[M]Time-Out Sebelum Insisi","[M]Sign-Out Sebelum Menutup Luka","[F]Barang Dapur","[F]Stok Opname Dapur",
                "[L]Mapping Vaksin Satu Sehat","[F]Suplier Dapur","[L]Kirim Imunisasi Satu Sehat","[M]Check List Post Operasi","[F]Pengadaan Barang Dapur","[F]Stok Keluar Dapur",
                "[F]Riwayat Barang Dapur","[F]Permintaan Barang Dapur","[M]Rekonsiliasi Obat","[F]Biaya Pengadaan Dapur","[F]Rekap Pengadaan Dapur","[G]Limbah Cair B3 Medis",
                "[O]Limbah B3 Cair Per Tanggal","[O]Limbah B3 Cair Per Bulan","[I]Rekap Biaya Registrasi","[M]Konfirmasi Rekonsiliasi Obat","[L]Kirim Clinical Impression Satu Sehat",
                "[M]Pengkajian Pasien Terminal","[P]Persetujuan Rawat Inap","[M]Monitoring Reaksi Tranfusi","[M]Pengkajian Korban Kekerasan","[M]Pengkajian Lanjutan Risiko Jatuh Lansia",
                "[M]Pengkajian Pasien Penyakit Menular","[M]Skrining Manajer Pelayanan Pasien","[M]Edukasi Pasien & Keluarga Rawat Jalan","[M]Pemantauan EWS Pasien Dewasa",
                "[M]Pengkajian Tambahan Bunuh Diri","[L]Antrean Per Tanggal Mobile JKN","[M]Pengkajian Tambahan Perilaku Kekerasan","[M]Pengkajian Tambahan Melarikan Diri",
                "[P]Persetujuan Penundaan Pelayanan","[J]Sisa Diet Pasien","[M]Pengkajian Awal Medis Ralan Bedah Mulut","[M]Pengkajian Pasien Keracunan","[M]Pemantauan MEOWS Pasien Obstetri",
                "[M]Catatan ADIME Gizi","[K]Pengajuan Biaya","[M]Pengkajian Awal Keperawatan Ralan Geriatri","[M]Master Masalah Keperawatan Geriatri","[M]Master Rencana Keperawatan Geriatri",
                "[M]Check List Kriteria Masuk HCU","[M]Check List Kriteria Keluar HCU","[M]Pengkajian Risiko Dekubitus","[P]Master Menolak Anjuran Medis","[P]Penolakan Anjuran Medis",
                "[J]Laporan Tahunan Penolakan Anjuran Medis","[M]Master Template Laporan Operasi","[M]Dokumentasi Tindakan ESWL","[M]Check List Kriteria Masuk ICU",
                "[M]Check List Kriteria Keluar ICU","[A]Akses Ke Dokter Lain Rawat Jalan","[M]Follow Up DBD","[M]Pengkajian Lanjutan Risiko Jatuh Neonatus","[K]Persetujuan Pengajuan Biaya",
                "[J]Pemeriksaan Fisik Ralan Per Penyakit","[M]Pengkajian Lanjutan Risiko Jatuh Geriatri","[M]Pemantauan EWS Pasien Neonatus","[K]Validasi Persetujuan Pengajuan Biaya",
                "[L]Riwayat Perawatan ICare BPJS","[K]Rekap Pengajuan Biaya","[M]Pengkajian Awal Medis Ralan Kulit & Kelamin","[L]Host To Host Bank Mandiri","[M]Pengkajian Awal Medis Pasien Hemodialisa",
                "[M]Pengkajian Level Kecemasan Ranap Anak","[M]Pengkajian Lanjutan Risiko Jatuh Psikiatri","[M]Pengkajian Lanjutan Skrining Fungsional","[M]Pengkajian Awal Medis Ralan Fisik & Rehabilitasi",
                "[M]Laporan Anestesi","[P]Template Persetujuan Penolakan Tindakan","[M]Pengkajian Awal Medis IGD Psikiatri","[L]Referensi Setting PPK Apotek BPJS","[L]Referensi Obat Apotek BPJS",
                "[L]Mapping Obat Apotek BPJS","[K]Pembayaran Bank Mandiri","[M]Pengkajian Ulang Nyeri","[M]Pengkajian Terapi Wicara","[L]Obat ApOL Apotek BPJS","[M]Pengkajian Restrain",
                "[L]Pencarian SEP Apotek BPJS","[L]Monitoring Klaim Apotek BPJS","[L]Daftar Pelayanan Obat Apotek BPJS","[M]Pengkajian Awal Medis Ralan Paru","[M]Catatan Keperawatan Ralan",
                "[M]Catatan Persalinan","[M]Skor Aldrette Pasca Anestesi","[M]Skor Steward Pasca Anestesi","[M]Skor Bromage Pasca Anestesi","[M]Pengkajian Pre Induksi","[M]Hasil USG Urologi",
                "[M]Hasil USG Gynecologi","[M]Hasil Pemeriksaan EKG","[L]Hapus/Edit SEP VClaim","[L]Kirim Diet Satu Sehat","[L]Mapping Obat/Alkes/BHP Satu Sehat","[F]Ringkasan Pengadaan Barang Dapur",
                "[L]Kirim Medication Satu Sehat","[L]Kirim Medication Request Satu Sehat","[M]Penatalaksanaan Terapi Okupasi","[L]Kirim Medication Dispense Satu Sehat","[M]Hasil USG Neonatus",
                "[M]Hasil Endoskopi Faring/Laring","[L]Mapping Tindakan Radiologi Satu Sehat","[L]Kirim Service Request Radiologi Satu Sehat","[M]Hasil Endoskopi Hidung",
                "[L]Kirim Specimen Radiologi Satu Sehat","[M]Master Masalah Keperawatan Neonatus","[M]Master Rencana Keperawatan Neonatus","[M]Pengkajian Awal Keperawatan Ranap Neonatus",
                "[L]Kirim Observation Radiologi Satu Sehat","[L]Kirim Diagnostic Report Radiologi Satu Sehat","[M]Hasil Endoskopi Telinga","[L]Mapping Tindakan Lab PK & MB Satu Sehat",
                "[L]Kirim Service Request Lab PK Satu Sehat","[L]Kirim Service Request Lab MB Satu Sehat","[L]Kirim Specimen Lab PK Satu Sehat","[L]Kirim Specimen Lab MB Satu Sehat",
                "[L]Kirim Observation Lab PK Satu Sehat","[L]Kirim Observation Lab MB Satu Sehat","[L]Kirim Diagnostic Report Lab PK Satu Sehat","[L]Kirim Diagnostic Report Lab MB Satu Sehat",
                "[J]Kepatuhan Kelengkapan Keselamatan Bedah","[K]Nilai Piutang Per Cara Bayar Per Bulan","[K]Ringkasan Piutang Per Cara Bayar","[M]Pengkajian Pasien Imunitas Rendah",
                "[M]Keseimbangan Cairan","[M]Catatan Observasi CHBP","[M]Catatan Observasi Induksi Persalinan","[C]Kategori Pengkajian SKP","[C]Kriteria Pengkajian SKP","[C]Pengkajian SKP Petugas/Dokter",
                "[L]Referensi Poli Mobile JKN FKTP","[L]Referensi Dokter Mobile JKN FKTP","[C]Rekapitulasi Pengkajian SKP","[K]Pembayaran Pihak Ke 3 Bank Mandiri","[L]Metode Pembayaran Bank Mandiri",
                "[L]Bank Tujuan Transfer Bank Mandiri","[L]Kode Transaksi Tujuan Transfer Bank Mandiri","[M]Konsultasi Medik","[M]Jawaban Konsultasi Medik","[L]Referensi Alergi PCare",
                "[L]Referensi Prognosa PCare","[J]Data Sasaran Usia Produktif","[J]Data Sasaran Usia Lansia","[M]Skrining Merokok Usia Sekolah & Remaja","[M]Skrining Kekerasan Pada Perempuan",
                "[M]Skrining Obesitas","[M]Skrining Risiko Kanker Payudara","[M]Skrining Risiko Kanker Paru","[M]Skrining TBC","[M]Skrining Kesehatan Gigi & Mulut Remaja",
                "[M]Pengkajian Awal Keperawatan Ranap Bayi/Anak","[A]Booking MCU Perusahaan","[M]Catatan Observasi Restrain Nonfarmakologi","[M]Catatan Observasi Ventilator",
                "[M]Catatan Anestesi-Sedasi","[M]Skrining PUMA","[L]Kirim Care Plan Satu Sehat","[L]Kirim Medication Statement Satu Sehat","[M]Skrining Adiksi Nikotin","[M]Skrining Thalassemia",
                "[M]Skrining Instrumen SDQ","[M]Skrining Instrumen SRQ","[M]Check List Pemberian Fibrinolitik","[M]Skrining Kanker Kolorektal","[F]Penerimaan Barang Dapur","[K]Bayar Pesan Dapur",
                "[K]Hutang Barang Dapur","[K]Titip Faktur/Tagihan Dapur","[K]Validasi Titip Faktur/Tagihan Dapur","[F]Surat Pemesanan Barang Dapur","[F]Pengajuan Barang Dapur",
                "[F]Retur Ke Suplier Dapur","[F]Hibah Barang Dapur","[F]Ringkasan Penerimaan Dapur","[F]Ringkasan Pengajuan Dapur","[F]Ringkasan Pemesanan Dapur","[F]Ringkasan Retur Suplier Dapur",
                "[F]Ringkasan Stok Keluar Dapur","[F]Stok Keluar Dapur Per Tanggal","[F]Sirkulasi Barang Dapur","[F]Sirkulasi Barang Dapur 2","[F]Verifikasi Penerimaan Dapur",
                "[F]Nilai Penerimaan Vendor Dapur Per Bulan","[K]Ringkasan Hutang Vendor Dapur","[M]Pengkajian Psikologi Klinis","[M]Pengkajian Awal Medis Ranap Neonatus","[M]Pengkajian Derajat Dehidrasi",
                "[I]Ringkasan Jasa Tindakan Pasien","[I]Pendapatan Per Akun Rekening","[M]Hasil Pemeriksaan ECHO","[M]Pengkajian Bayi Baru Lahir","[J]RL 1.3 Ketersediaan Tempat Tidur",
                "[I]Pendapatan Per Akun Closing","[K]Pengeluaran-pengeluaran/Kas Keluar","[M]Skrining Diabetes Melitus","[M]Laporan Tindakan","[M]Pelaksanaan Informasi & Edukasi",
                "[M]Layanan Kedokteran Fisik & Rehabilitasi","[M]Skrining Kesehatan Gigi & Mulut Balita","[M]Skrining Anemia","[M]Layanan Program KFR","[M]Skrining Hipertensi",
                "[M]Skrining Kesehatan Penglihatan","[M]Catatan Observasi Hemodialisa","[M]Skrining Kesehatan Gigi & Mulut Dewasa","[M]Skrining Risiko Kanker Serviks","[M]Catatan Cairan Hemodialisa",
                "[M]Skrining Kesehatan Gigi & Mulut Lansia","[M]Skrining Indra Pendengaran","[M]Catatan Pengkajian Paska Operasi","[M]Skrining Frailty Syndrome","[G]Sirkulasi CSSD",
                "[J]Lama Pelayanan CSSD","[M]Catatan Observasi Bayi","[C]Riwayat Surat Peringatan","[M]Master Kesimpulan & Anjuran MCU","[K]Kategori Piutang Jasa Perusahaan",
                "[K]Piutang Jasa Perusahaan","[K]Bayar Piutang Jasa Perusahaan","[K]Piutang Jasa Perusahaan Belum Lunas","[M]Check List Kesiapan Anestesi","[K]Piutang Peminjaman Uang Belum Lunas",
                "[M]Hasil Pemeriksaan Slit Lamp","[M]Hasil Pemeriksaan OCT","[K]Beban Hutang Lain","[J]Poli Asal Pasien Ranap","[K]Pemberi Hutang Lain","[J]Dokter Asal Pasien Ranap",
                "[L]Rekap Keluar Duta Parking","[P]Surat Keterangan Layak Terbang","[K]Bayar Beban Hutang Lain","[P]Surat Persetujuan Pemeriksaan HIV","[M]Skrining Instrumen ACRS",
                "[P]Surat Pernyataan Memilih DPJP","[M]Skrining Instrumen Mental Emosional Anak","[B]Pelanggan Lab Kesehatan Lingkungan","[M]Check List Kriteria Masuk NICU",
                "[M]Check List Kriteria Keluar NICU","[M]Pengkajian Awal Medis Ranap Psikiatri","[M]Check List Kriteria Masuk PICU","[M]Check List Kriteria Keluar PICU","[B]Master Sampel Lab Kesling",
                "[M]Skrining Instrumen AMT","[B]Parameter Pengujian Lab Kesling","[B]Nilai Normal Baku Mutu Lab Kesling","[M]Skrining Pneumonia Severity Index","[B]Permintaan Pengujian Sampel Lab Kesling",
                "[M]Pengkajian Awal Medis Ralan Jantung","[M]Pengkajian Awal Medis Ralan Urologi","[M]Hasil Pemeriksaan Treadmill","[M]Hasil Pemeriksaan ECHO Pediatrik","[M]Master Template Informasi & Edukasi",
                "[M]Skrining Instrumen ESAT","[M]Pengkajian Awal Medis Ranap Jantung","[B]Data Penugasan Pengujian Sampel Lab Kesling","[B]Data Hasil Pengujian Sampel Lab Kesling",
                "[B]Data Verifikasi Pengujian Sampel Lab Kesling","[B]Data Validasi Pengujian Sampel Lab Kesling","[B]Rekap Pelayanan Lab Kesling","[B]Pembayaran Pengujian Sampel Lab Kesling",
                "[M]Skrining CURB-65","[L]Potensi PRB di VClaim","[L]Riwayat Pelayanan Obat Apotek BPJS","[M]Skrining Gizi Kehamilan","[L]Rekap Peserta PRB Apotek BPJS","[P]Serah Terima Anggota Tubuh/Barang",
                "[R]Jenis Aktivitas Proyek PCRA","[R]Lokasi & Kelompok Risiko Area PCRA","[R]Kelas Risiko/Kelas Pencegahan PCRA","[R]Tindakan Pengendalian PCRA","[R]Identifikasi Risiko Infeksi PCRA",
                "[R]Identifikasi Risiko Keselamatan PCRA","[R]Identifikasi Risiko Kebakaran PCRA","[R]Identifikasi Risiko Utilitas PCRA","[L]Daftar Resep Apotek BPJS","[L]Permintaan Resep Iterasi Apotek BPJS",
                "[R]Pengkajian Risiko Pra Konstruksi/PCRA","[R]Persyaratan Harus Dipenuhi PCRA","[L]Kirim Q.R. Telaah Farmasi Satu Sehat","[L]Kirim Allergy Intolerance Satu Sehat","[M]Konsultasi Perawat",
                "[M]Jawaban Konsultasi Perawat","[L]Kirim FHIR Smart Klaim BPJS","[L]Mapping Prosedur Smart Klaim BPJS","[L]Mapping Penyakit Smart Klaim BPJS","[P]Persetujuan Bimbingan Rohani & Mental",
                "[P]Surat Permintaan Perlindungan Diri Dari Kekerasan","[P]Surat Permohonan Privasi","[P]Surat Permintaan Second Opinion","[P]Surat Keterangan Berobat","[P]Surat Penolakan Resusitasi",
                "[M]Catatan Observasi Ruang Operasi","[M]Hasil USG Abdomen","[M]Intervensi Nyeri Farmakologi","[M]Intervensi Nyeri Non Farmakologi","[P]Surat Pengajuan Cuti Perawatan",
                "[M]Check List Kriteria Masuk Isolasi","[L]Mapping Tindakan Ralan KPTL Satu Sehat","[L]Mapping Tindakan Ranap KPTL Satu Sehat","[L]Mapping Tindakan Radiologi KPTL Satu Sehat",
                "[L]Mapping Tindakan Laborat KPTL Satu Sehat","[L]Mapping Tindakan Operasi KPTL Satu Sehat","[L]Mapping Tarif Kamar KPTL Satu Sehat","[M]Check List Kriteria Keluar Isolasi",
                "[L]Bridging TTE Satu Sehat","[L]Kirim Composition Satu Sehat","[K]Ringkasan Hutang Vendor Aset/Inventaris","[K]Ringkasan Beban Hutang Lain","[U]Set Resep Per Cara Bayar",
                "[M]Skrining TOLAC","[M]Admisi & Skoring TOLAC","[M]Monitoring Selama TOLAC","[M]Resume Hasil Persalinan TOLAC","[M]Laporan Reaksi Tranfusi","[M]Penilaian GRACE Score",
                "[M]Penilaian BISHOP Score"
        };

        tabMode=new DefaultTableModel(null,row){
              @Override
              public boolean isCellEditable(int rowIndex, int colIndex){
                  boolean a = true;
                    if ((colIndex==0)||(colIndex==1)||(colIndex==2)) {
                        a=false;
                    }
                    return a;
              }
              Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class,java.lang.Object.class,java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class,
                java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };

        tbUser.setModel(tabMode);
        //tbJabatan.setDefaultRenderer(Object.class, new WarnaTable(Scroll.getBackground(),Color.GREEN));
        tbUser.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbUser.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 1227;i++) {
            TableColumn column = tbUser.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(130);
                    break;
                case 1:
                    column.setPreferredWidth(180);
                    break;
                case 2:
                    column.setPreferredWidth(100);
                    break;
                case 3:
                    column.setPreferredWidth(130);
                    break;
                case 4:
                    column.setPreferredWidth(55);
                    break;
                case 5:
                    column.setPreferredWidth(88);
                    break;
                case 6:
                    column.setPreferredWidth(55);
                    break;
                case 7:
                    column.setPreferredWidth(95);
                    break;
                case 8:
                    column.setPreferredWidth(63);
                    break;
                case 9:
                    column.setPreferredWidth(59);
                    break;
                case 10:
                    column.setPreferredWidth(73);
                    break;
                case 11:
                    column.setPreferredWidth(99);
                    break;
                case 12:
                    column.setPreferredWidth(84);
                    break;
                case 13:
                    column.setPreferredWidth(103);
                    break;
                case 14:
                    column.setPreferredWidth(66);
                    break;
                case 15:
                    column.setPreferredWidth(100);
                    break;
                case 16:
                    column.setPreferredWidth(100);
                    break;
                case 17:
                    column.setPreferredWidth(135);
                    break;
                case 18:
                    column.setPreferredWidth(94);
                    break;
                case 19:
                    column.setPreferredWidth(110);
                    break;
                case 20:
                    column.setPreferredWidth(82);
                    break;
                case 21:
                    column.setPreferredWidth(93);
                    break;
                case 22:
                    column.setPreferredWidth(82);
                    break;
                case 23:
                    column.setPreferredWidth(107);
                    break;
                case 24:
                    column.setPreferredWidth(81);
                    break;
                case 25:
                    column.setPreferredWidth(95);
                    break;
                case 26:
                    column.setPreferredWidth(95);
                    break;
                case 27:
                    column.setPreferredWidth(135);
                    break;
                case 28:
                    column.setPreferredWidth(110);
                    break;
                case 29:
                    column.setPreferredWidth(103);
                    break;
                case 30:
                    column.setPreferredWidth(108);
                    break;
                case 31:
                    column.setPreferredWidth(100);
                    break;
                case 32:
                    column.setPreferredWidth(92);
                    break;
                case 33:
                    column.setPreferredWidth(136);
                    break;
                case 34:
                    column.setPreferredWidth(98);
                    break;
                case 35:
                    column.setPreferredWidth(105);
                    break;
                case 36:
                    column.setPreferredWidth(87);
                    break;
                case 37:
                    column.setPreferredWidth(114);
                    break;
                case 38:
                    column.setPreferredWidth(127);
                    break;
                case 39:
                    column.setPreferredWidth(110);
                    break;
                case 40:
                    column.setPreferredWidth(170);
                    break;
                case 41:
                    column.setPreferredWidth(170);
                    break;
                case 42:
                    column.setPreferredWidth(163);
                    break;
                case 43:
                    column.setPreferredWidth(153);
                    break;
                case 44:
                    column.setPreferredWidth(101);
                    break;
                case 45:
                    column.setPreferredWidth(115);
                    break;
                case 46:
                    column.setPreferredWidth(178);
                    break;
                case 230:
                    column.setPreferredWidth(125);
                    break;
                case 231:
                    column.setPreferredWidth(100);
                    break;
                case 232:
                    column.setPreferredWidth(165);
                    break;
                case 233:
                    column.setPreferredWidth(133);
                    break;
                case 234:
                    column.setPreferredWidth(133);
                    break;
                case 235:
                    column.setPreferredWidth(73);
                    break;
                case 236:
                    column.setPreferredWidth(80);
                    break;
                case 237:
                    column.setPreferredWidth(82);
                    break;
                case 238:
                    column.setPreferredWidth(75);
                    break;
                case 239:
                    column.setPreferredWidth(155);
                    break;
                case 240:
                    column.setPreferredWidth(90);
                    break;
                case 241:
                    column.setPreferredWidth(135);
                    break;
                case 242:
                    column.setPreferredWidth(80);
                    break;
                case 243:
                    column.setPreferredWidth(96);
                    break;
                case 244:
                    column.setPreferredWidth(120);
                    break;
                case 245:
                    column.setPreferredWidth(98);
                    break;
                case 246:
                    column.setPreferredWidth(133);
                    break;
                case 247:
                    column.setPreferredWidth(149);
                    break;
                case 248:
                    column.setPreferredWidth(165);
                    break;
                case 249:
                    column.setPreferredWidth(167);
                    break;
                case 250:
                    column.setPreferredWidth(147);
                    break;
                case 251:
                    column.setPreferredWidth(139);
                    break;
                case 252:
                    column.setPreferredWidth(110);
                    break;
                case 253:
                    column.setPreferredWidth(107);
                    break;
                case 254:
                    column.setPreferredWidth(142);
                    break;
                case 255:
                    column.setPreferredWidth(155);
                    break;
                case 256:
                    column.setPreferredWidth(120);
                    break;
                case 257:
                    column.setPreferredWidth(120);
                    break;
                case 258:
                    column.setPreferredWidth(116);
                    break;
                case 259:
                    column.setPreferredWidth(116);
                    break;
                case 260:
                    column.setPreferredWidth(113);
                    break;
                case 261:
                    column.setPreferredWidth(128);
                    break;
                case 262:
                    column.setPreferredWidth(125);
                    break;
                case 263:
                    column.setPreferredWidth(134);
                    break;
                case 264:
                    column.setPreferredWidth(130);
                    break;
                case 265:
                    column.setPreferredWidth(143);
                    break;
                case 266:
                    column.setPreferredWidth(142);
                    break;
                case 267:
                    column.setPreferredWidth(140);
                    break;
                case 268:
                    column.setPreferredWidth(144);
                    break;
                case 269:
                    column.setPreferredWidth(157);
                    break;
                case 270:
                    column.setPreferredWidth(160);
                    break;
                case 271:
                    column.setPreferredWidth(150);
                    break;
                case 272:
                    column.setPreferredWidth(165);
                    break;
                case 273:
                    column.setPreferredWidth(123);
                    break;
                case 274:
                    column.setPreferredWidth(154);
                    break;
                case 275:
                    column.setPreferredWidth(170);
                    break;
                case 276:
                    column.setPreferredWidth(186);
                    break;
                case 277:
                    column.setPreferredWidth(186);
                    break;
                case 278:
                    column.setPreferredWidth(159);
                    break;
                case 279:
                    column.setPreferredWidth(112);
                    break;
                case 280:
                    column.setPreferredWidth(175);
                    break;
                case 281:
                    column.setPreferredWidth(176);
                    break;
                case 282:
                    column.setPreferredWidth(94);
                    break;
                case 283:
                    column.setPreferredWidth(156);
                    break;
                case 284:
                    column.setPreferredWidth(134);
                    break;
                case 285:
                    column.setPreferredWidth(166);
                    break;
                case 286:
                    column.setPreferredWidth(164);
                    break;
                case 287:
                    column.setPreferredWidth(163);
                    break;
                case 288:
                    column.setPreferredWidth(196);
                    break;
                case 289:
                    column.setPreferredWidth(192);
                    break;
                case 290:
                    column.setPreferredWidth(131);
                    break;
                case 291:
                    column.setPreferredWidth(146);
                    break;
                case 292:
                    column.setPreferredWidth(162);
                    break;
                case 293:
                    column.setPreferredWidth(134);
                    break;
                case 294:
                    column.setPreferredWidth(154);
                    break;
                case 295:
                    column.setPreferredWidth(168);
                    break;
                case 296:
                    column.setPreferredWidth(158);
                    break;
                case 297:
                    column.setPreferredWidth(166);
                    break;
                case 298:
                    column.setPreferredWidth(80);
                    break;
                case 299:
                    column.setPreferredWidth(81);
                    break;
                case 300:
                    column.setPreferredWidth(158);
                    break;
                case 301:
                    column.setPreferredWidth(186);
                    break;
                case 302:
                    column.setPreferredWidth(170);
                    break;
                case 303:
                    column.setPreferredWidth(127);
                    break;
                case 304:
                    column.setPreferredWidth(150);
                    break;
                case 305:
                    column.setPreferredWidth(170);
                    break;
                case 306:
                    column.setPreferredWidth(105);
                    break;
                case 307:
                    column.setPreferredWidth(172);
                    break;
                case 308:
                    column.setPreferredWidth(108);
                    break;
                case 309:
                    column.setPreferredWidth(100);
                    break;
                case 310:
                    column.setPreferredWidth(182);
                    break;
                case 311:
                    column.setPreferredWidth(185);
                    break;
                case 312:
                    column.setPreferredWidth(116);
                    break;
                case 313:
                    column.setPreferredWidth(85);
                    break;
                case 314:
                    column.setPreferredWidth(158);
                    break;
                case 315:
                    column.setPreferredWidth(180);
                    break;
                case 316:
                    column.setPreferredWidth(168);
                    break;
                case 317:
                    column.setPreferredWidth(132);
                    break;
                case 318:
                    column.setPreferredWidth(120);
                    break;
                case 319:
                    column.setPreferredWidth(102);
                    break;
                case 320:
                    column.setPreferredWidth(114);
                    break;
                case 321:
                    column.setPreferredWidth(104);
                    break;
                case 322:
                    column.setPreferredWidth(150);
                    break;
                case 323:
                    column.setPreferredWidth(127);
                    break;
                case 324:
                    column.setPreferredWidth(108);
                    break;
                case 325:
                    column.setPreferredWidth(165);
                    break;
                case 326:
                    column.setPreferredWidth(130);
                    break;
                case 327:
                    column.setPreferredWidth(143);
                    break;
                case 328:
                    column.setPreferredWidth(160);
                    break;
                case 329:
                    column.setPreferredWidth(116);
                    break;
                case 330:
                    column.setPreferredWidth(152);
                    break;
                case 331:
                    column.setPreferredWidth(132);
                    break;
                case 332:
                    column.setPreferredWidth(128);
                    break;
                case 333:
                    column.setPreferredWidth(141);
                    break;
                case 334:
                    column.setPreferredWidth(90);
                    break;
                case 335:
                    column.setPreferredWidth(127);
                    break;
                case 336:
                    column.setPreferredWidth(140);
                    break;
                case 337:
                    column.setPreferredWidth(142);
                    break;
                case 338:
                    column.setPreferredWidth(148);
                    break;
                case 339:
                    column.setPreferredWidth(140);
                    break;
                case 340:
                    column.setPreferredWidth(115);
                    break;
                case 341:
                    column.setPreferredWidth(88);
                    break;
                case 342:
                    column.setPreferredWidth(85);
                    break;
                case 343:
                    column.setPreferredWidth(73);
                    break;
                case 344:
                    column.setPreferredWidth(79);
                    break;
                case 345:
                    column.setPreferredWidth(79);
                    break;
                case 346:
                    column.setPreferredWidth(96);
                    break;
                case 347:
                    column.setPreferredWidth(86);
                    break;
                case 348:
                    column.setPreferredWidth(90);
                    break;
                case 349:
                    column.setPreferredWidth(90);
                    break;
                case 350:
                    column.setPreferredWidth(70);
                    break;
                case 351:
                    column.setPreferredWidth(178);
                    break;
                case 352:
                    column.setPreferredWidth(150);
                    break;
                case 353:
                    column.setPreferredWidth(95);
                    break;
                case 354:
                    column.setPreferredWidth(130);
                    break;
                case 355:
                    column.setPreferredWidth(167);
                    break;
                case 356:
                    column.setPreferredWidth(83);
                    break;
                case 357:
                    column.setPreferredWidth(71);
                    break;
                case 358:
                    column.setPreferredWidth(109);
                    break;
                case 359:
                    column.setPreferredWidth(140);
                    break;
                case 360:
                    column.setPreferredWidth(155);
                    break;
                case 361:
                    column.setPreferredWidth(157);
                    break;
                case 362:
                    column.setPreferredWidth(163);
                    break;
                case 363:
                    column.setPreferredWidth(163);
                    break;
                case 364:
                    column.setPreferredWidth(144);
                    break;
                case 365:
                    column.setPreferredWidth(94);
                    break;
                case 366:
                    column.setPreferredWidth(120);
                    break;
                case 367:
                    column.setPreferredWidth(82);
                    break;
                case 368:
                    column.setPreferredWidth(70);
                    break;
                case 369:
                    column.setPreferredWidth(81);
                    break;
                case 370:
                    column.setPreferredWidth(69);
                    break;
                case 371:
                    column.setPreferredWidth(82);
                    break;
                case 372:
                    column.setPreferredWidth(96);
                    break;
                case 373:
                    column.setPreferredWidth(83);
                    break;
                case 374:
                    column.setPreferredWidth(73);
                    break;
                case 375:
                    column.setPreferredWidth(99);
                    break;
                case 376:
                    column.setPreferredWidth(80);
                    break;
                case 377:
                    column.setPreferredWidth(132);
                    break;
                case 378:
                    column.setPreferredWidth(117);
                    break;
                case 379:
                    column.setPreferredWidth(140);
                    break;
                case 380:
                    column.setPreferredWidth(155);
                    break;
                case 381:
                    column.setPreferredWidth(140);
                    break;
                case 382:
                    column.setPreferredWidth(158);
                    break;
                case 383:
                    column.setPreferredWidth(134);
                    break;
                case 384:
                    column.setPreferredWidth(134);
                    break;
                case 385:
                    column.setPreferredWidth(124);
                    break;
                case 386:
                    column.setPreferredWidth(145);
                    break;
                case 387:
                    column.setPreferredWidth(145);
                    break;
                case 388:
                    column.setPreferredWidth(139);
                    break;
                case 389:
                    column.setPreferredWidth(190);
                    break;
                case 390:
                    column.setPreferredWidth(119);
                    break;
                case 391:
                    column.setPreferredWidth(130);
                    break;
                case 392:
                    column.setPreferredWidth(113);
                    break;
                case 393:
                    column.setPreferredWidth(110);
                    break;
                case 394:
                    column.setPreferredWidth(142);
                    break;
                case 395:
                    column.setPreferredWidth(135);
                    break;
                case 396:
                    column.setPreferredWidth(158);
                    break;
                case 397:
                    column.setPreferredWidth(158);
                    break;
                case 398:
                    column.setPreferredWidth(145);
                    break;
                case 399:
                    column.setPreferredWidth(127);
                    break;
                case 400:
                    column.setPreferredWidth(103);
                    break;
                case 401:
                    column.setPreferredWidth(169);
                    break;
                case 402:
                    column.setPreferredWidth(131);
                    break;
                case 403:
                    column.setPreferredWidth(99);
                    break;
                case 404:
                    column.setPreferredWidth(142);
                    break;
                case 405:
                    column.setPreferredWidth(117);
                    break;
                case 406:
                    column.setPreferredWidth(92);
                    break;
                case 407:
                    column.setPreferredWidth(94);
                    break;
                case 408:
                    column.setPreferredWidth(80);
                    break;
                case 409:
                    column.setPreferredWidth(102);
                    break;
                case 410:
                    column.setPreferredWidth(137);
                    break;
                case 411:
                    column.setPreferredWidth(110);
                    break;
                case 412:
                    column.setPreferredWidth(120);
                    break;
                case 413:
                    column.setPreferredWidth(126);
                    break;
                case 414:
                    column.setPreferredWidth(147);
                    break;
                case 415:
                    column.setPreferredWidth(153);
                    break;
                case 416:
                    column.setPreferredWidth(137);
                    break;
                case 417:
                    column.setPreferredWidth(146);
                    break;
                case 418:
                    column.setPreferredWidth(98);
                    break;
                case 419:
                    column.setPreferredWidth(166);
                    break;
                case 420:
                    column.setPreferredWidth(106);
                    break;
                case 421:
                    column.setPreferredWidth(148);
                    break;
                case 422:
                    column.setPreferredWidth(135);
                    break;
                case 423:
                    column.setPreferredWidth(67);
                    break;
                case 424:
                    column.setPreferredWidth(120);
                    break;
                case 425:
                    column.setPreferredWidth(129);
                    break;
                case 426:
                    column.setPreferredWidth(139);
                    break;
                case 427:
                    column.setPreferredWidth(167);
                    break;
                case 428:
                    column.setPreferredWidth(149);
                    break;
                case 429:
                    column.setPreferredWidth(129);
                    break;
                case 430:
                    column.setPreferredWidth(129);
                    break;
                case 431:
                    column.setPreferredWidth(100);
                    break;
                case 432:
                    column.setPreferredWidth(75);
                    break;
                case 433:
                    column.setPreferredWidth(118);
                    break;
                case 434:
                    column.setPreferredWidth(123);
                    break;
                case 435:
                    column.setPreferredWidth(150);
                    break;
                case 436:
                    column.setPreferredWidth(152);
                    break;
                case 437:
                    column.setPreferredWidth(133);
                    break;
                case 438:
                    column.setPreferredWidth(154);
                    break;
                case 439:
                    column.setPreferredWidth(170);
                    break;
                case 440:
                    column.setPreferredWidth(113);
                    break;
                case 441:
                    column.setPreferredWidth(60);
                    break;
                case 442:
                    column.setPreferredWidth(168);
                    break;
                case 443:
                    column.setPreferredWidth(118);
                    break;
                case 444:
                    column.setPreferredWidth(78);
                    break;
                case 445:
                    column.setPreferredWidth(78);
                    break;
                case 446:
                    column.setPreferredWidth(110);
                    break;
                case 447:
                    column.setPreferredWidth(90);
                    break;
                case 448:
                    column.setPreferredWidth(102);
                    break;
                case 449:
                    column.setPreferredWidth(139);
                    break;
                case 450:
                    column.setPreferredWidth(139);
                    break;
                case 451:
                    column.setPreferredWidth(102);
                    break;
                case 452:
                    column.setPreferredWidth(152);
                    break;
                case 453:
                    column.setPreferredWidth(103);
                    break;
                case 454:
                    column.setPreferredWidth(107);
                    break;
                case 455:
                    column.setPreferredWidth(67);
                    break;
                case 456:
                    column.setPreferredWidth(98);
                    break;
                case 457:
                    column.setPreferredWidth(122);
                    break;
                case 458:
                    column.setPreferredWidth(133);
                    break;
                case 459:
                    column.setPreferredWidth(184);
                    break;
                case 460:
                    column.setPreferredWidth(172);
                    break;
                case 461:
                    column.setPreferredWidth(164);
                    break;
                case 462:
                    column.setPreferredWidth(152);
                    break;
                case 463:
                    column.setPreferredWidth(136);
                    break;
                case 464:
                    column.setPreferredWidth(197);
                    break;
                case 465:
                    column.setPreferredWidth(186);
                    break;
                case 466:
                    column.setPreferredWidth(101);
                    break;
                case 467:
                    column.setPreferredWidth(82);
                    break;
                case 468:
                    column.setPreferredWidth(121);
                    break;
                case 469:
                    column.setPreferredWidth(99);
                    break;
                case 470:
                    column.setPreferredWidth(83);
                    break;
                case 471:
                    column.setPreferredWidth(113);
                    break;
                case 472:
                    column.setPreferredWidth(99);
                    break;
                case 473:
                    column.setPreferredWidth(125);
                    break;
                case 474:
                    column.setPreferredWidth(140);
                    break;
                case 475:
                    column.setPreferredWidth(141);
                    break;
                case 476:
                    column.setPreferredWidth(122);
                    break;
                case 477:
                    column.setPreferredWidth(132);
                    break;
                case 478:
                    column.setPreferredWidth(188);
                    break;
                case 479:
                    column.setPreferredWidth(152);
                    break;
                case 480:
                    column.setPreferredWidth(114);
                    break;
                case 481:
                    column.setPreferredWidth(97);
                    break;
                case 482:
                    column.setPreferredWidth(142);
                    break;
                case 483:
                    column.setPreferredWidth(87);
                    break;
                case 484:
                    column.setPreferredWidth(112);
                    break;
                case 485:
                    column.setPreferredWidth(111);
                    break;
                case 486:
                    column.setPreferredWidth(113);
                    break;
                case 487:
                    column.setPreferredWidth(103);
                    break;
                case 488:
                    column.setPreferredWidth(82);
                    break;
                case 489:
                    column.setPreferredWidth(86);
                    break;
                case 490:
                    column.setPreferredWidth(83);
                    break;
                case 491:
                    column.setPreferredWidth(95);
                    break;
                case 492:
                    column.setPreferredWidth(116);
                    break;
                case 493:
                    column.setPreferredWidth(161);
                    break;
                case 494:
                    column.setPreferredWidth(106);
                    break;
                case 495:
                    column.setPreferredWidth(131);
                    break;
                case 496:
                    column.setPreferredWidth(130);
                    break;
                case 497:
                    column.setPreferredWidth(132);
                    break;
                case 498:
                    column.setPreferredWidth(122);
                    break;
                case 499:
                    column.setPreferredWidth(149);
                    break;
                case 500:
                    column.setPreferredWidth(194);
                    break;
                case 501:
                    column.setPreferredWidth(139);
                    break;
                case 502:
                    column.setPreferredWidth(164);
                    break;
                case 503:
                    column.setPreferredWidth(163);
                    break;
                case 504:
                    column.setPreferredWidth(165);
                    break;
                case 505:
                    column.setPreferredWidth(155);
                    break;
                case 506:
                    column.setPreferredWidth(124);
                    break;
                case 507:
                    column.setPreferredWidth(132);
                    break;
                case 508:
                    column.setPreferredWidth(119);
                    break;
                case 509:
                    column.setPreferredWidth(200);
                    break;
                case 510:
                    column.setPreferredWidth(132);
                    break;
                case 511:
                    column.setPreferredWidth(162);
                    break;
                case 512:
                    column.setPreferredWidth(156);
                    break;
                case 513:
                    column.setPreferredWidth(168);
                    break;
                case 514:
                    column.setPreferredWidth(160);
                    break;
                case 515:
                    column.setPreferredWidth(135);
                    break;
                case 516:
                    column.setPreferredWidth(165);
                    break;
                case 517:
                    column.setPreferredWidth(155);
                    break;
                case 518:
                    column.setPreferredWidth(145);
                    break;
                case 519:
                    column.setPreferredWidth(138);
                    break;
                case 520:
                    column.setPreferredWidth(135);
                    break;
                case 521:
                    column.setPreferredWidth(143);
                    break;
                case 522:
                    column.setPreferredWidth(123);
                    break;
                case 523:
                    column.setPreferredWidth(104);
                    break;
                case 524:
                    column.setPreferredWidth(101);
                    break;
                case 525:
                    column.setPreferredWidth(162);
                    break;
                case 526:
                    column.setPreferredWidth(97);
                    break;
                case 527:
                    column.setPreferredWidth(123);
                    break;
                case 528:
                    column.setPreferredWidth(123);
                    break;
                case 529:
                    column.setPreferredWidth(123);
                    break;
                case 530:
                    column.setPreferredWidth(123);
                    break;
                case 531:
                    column.setPreferredWidth(123);
                    break;
                case 532:
                    column.setPreferredWidth(150);
                    break;
                case 533:
                    column.setPreferredWidth(154);
                    break;
                case 534:
                    column.setPreferredWidth(130);
                    break;
                case 535:
                    column.setPreferredWidth(119);
                    break;
                case 536:
                    column.setPreferredWidth(139);
                    break;
                case 537:
                    column.setPreferredWidth(150);
                    break;
                case 538:
                    column.setPreferredWidth(128);
                    break;
                case 539:
                    column.setPreferredWidth(144);
                    break;
                case 540:
                    column.setPreferredWidth(150);
                    break;
                case 541:
                    column.setPreferredWidth(184);
                    break;
                case 542:
                    column.setPreferredWidth(135);
                    break;
                case 543:
                    column.setPreferredWidth(157);
                    break;
                case 544:
                    column.setPreferredWidth(151);
                    break;
                case 545:
                    column.setPreferredWidth(180);
                    break;
                case 546:
                    column.setPreferredWidth(193);
                    break;
                case 547:
                    column.setPreferredWidth(174);
                    break;
                case 548:
                    column.setPreferredWidth(145);
                    break;
                case 549:
                    column.setPreferredWidth(169);
                    break;
                case 550:
                    column.setPreferredWidth(162);
                    break;
                case 551:
                    column.setPreferredWidth(86);
                    break;
                case 552:
                    column.setPreferredWidth(156);
                    break;
                case 553:
                    column.setPreferredWidth(95);
                    break;
                case 554:
                    column.setPreferredWidth(132);
                    break;
                case 555:
                    column.setPreferredWidth(119);
                    break;
                case 556:
                    column.setPreferredWidth(133);
                    break;
                case 557:
                    column.setPreferredWidth(180);
                    break;
                case 558:
                    column.setPreferredWidth(133);
                    break;
                case 559:
                    column.setPreferredWidth(129);
                    break;
                case 560:
                    column.setPreferredWidth(144);
                    break;
                case 561:
                    column.setPreferredWidth(142);
                    break;
                case 562:
                    column.setPreferredWidth(160);
                    break;
                case 563:
                    column.setPreferredWidth(140);
                    break;
                case 564:
                    column.setPreferredWidth(141);
                    break;
                case 565:
                    column.setPreferredWidth(144);
                    break;
                case 566:
                    column.setPreferredWidth(127);
                    break;
                case 567:
                    column.setPreferredWidth(141);
                    break;
                case 568:
                    column.setPreferredWidth(119);
                    break;
                case 569:
                    column.setPreferredWidth(122);
                    break;
                case 570:
                    column.setPreferredWidth(137);
                    break;
                case 571:
                    column.setPreferredWidth(128);
                    break;
                case 572:
                    column.setPreferredWidth(129);
                    break;
                case 573:
                    column.setPreferredWidth(110);
                    break;
                case 574:
                    column.setPreferredWidth(73);
                    break;
                case 575:
                    column.setPreferredWidth(77);
                    break;
                case 576:
                    column.setPreferredWidth(104);
                    break;
                case 577:
                    column.setPreferredWidth(162);
                    break;
                case 578:
                    column.setPreferredWidth(163);
                    break;
                case 579:
                    column.setPreferredWidth(124);
                    break;
                case 580:
                    column.setPreferredWidth(132);
                    break;
                case 581:
                    column.setPreferredWidth(220);
                    break;
                case 582:
                    column.setPreferredWidth(103);
                    break;
                case 583:
                    column.setPreferredWidth(135);
                    break;
                case 584:
                    column.setPreferredWidth(190);
                    break;
                case 585:
                    column.setPreferredWidth(101);
                    break;
                case 586:
                    column.setPreferredWidth(163);
                    break;
                case 587:
                    column.setPreferredWidth(96);
                    break;
                case 588:
                    column.setPreferredWidth(154);
                    break;
                case 589:
                    column.setPreferredWidth(95);
                    break;
                case 590:
                    column.setPreferredWidth(83);
                    break;
                case 591:
                    column.setPreferredWidth(111);
                    break;
                case 592:
                    column.setPreferredWidth(96);
                    break;
                case 593:
                    column.setPreferredWidth(83);
                    break;
                case 594:
                    column.setPreferredWidth(146);
                    break;
                case 595:
                    column.setPreferredWidth(138);
                    break;
                case 596:
                    column.setPreferredWidth(114);
                    break;
                case 597:
                    column.setPreferredWidth(125);
                    break;
                case 598:
                    column.setPreferredWidth(134);
                    break;
                case 599:
                    column.setPreferredWidth(138);
                    break;
                case 600:
                    column.setPreferredWidth(144);
                    break;
                case 601:
                    column.setPreferredWidth(140);
                    break;
                case 602:
                    column.setPreferredWidth(81);
                    break;
                case 603:
                    column.setPreferredWidth(105);
                    break;
                case 604:
                    column.setPreferredWidth(88);
                    break;
                case 605:
                    column.setPreferredWidth(97);
                    break;
                case 606:
                    column.setPreferredWidth(149);
                    break;
                case 607:
                    column.setPreferredWidth(86);
                    break;
                case 608:
                    column.setPreferredWidth(128);
                    break;
                case 609:
                    column.setPreferredWidth(153);
                    break;
                case 610:
                    column.setPreferredWidth(150);
                    break;
                case 611:
                    column.setPreferredWidth(90);
                    break;
                case 612:
                    column.setPreferredWidth(142);
                    break;
                case 613:
                    column.setPreferredWidth(139);
                    break;
                case 614:
                    column.setPreferredWidth(146);
                    break;
                case 615:
                    column.setPreferredWidth(181);
                    break;
                case 616:
                    column.setPreferredWidth(185);
                    break;
                case 617:
                    column.setPreferredWidth(116);
                    break;
                case 618:
                    column.setPreferredWidth(121);
                    break;
                case 619:
                    column.setPreferredWidth(132);
                    break;
                case 620:
                    column.setPreferredWidth(118);
                    break;
                case 621:
                    column.setPreferredWidth(177);
                    break;
                case 622:
                    column.setPreferredWidth(134);
                    break;
                case 623:
                    column.setPreferredWidth(79);
                    break;
                case 624:
                    column.setPreferredWidth(97);
                    break;
                case 625:
                    column.setPreferredWidth(98);
                    break;
                case 626:
                    column.setPreferredWidth(127);
                    break;
                case 627:
                    column.setPreferredWidth(99);
                    break;
                case 628:
                    column.setPreferredWidth(113);
                    break;
                case 629:
                    column.setPreferredWidth(136);
                    break;
                case 630:
                    column.setPreferredWidth(144);
                    break;
                case 631:
                    column.setPreferredWidth(189);
                    break;
                case 632:
                    column.setPreferredWidth(187);
                    break;
                case 633:
                    column.setPreferredWidth(180);
                    break;
                case 634:
                    column.setPreferredWidth(183);
                    break;
                case 635:
                    column.setPreferredWidth(176);
                    break;
                case 636:
                    column.setPreferredWidth(169);
                    break;
                case 637:
                    column.setPreferredWidth(205);
                    break;
                case 638:
                    column.setPreferredWidth(172);
                    break;
                case 639:
                    column.setPreferredWidth(175);
                    break;
                case 640:
                    column.setPreferredWidth(170);
                    break;
                case 641:
                    column.setPreferredWidth(178);
                    break;
                case 642:
                    column.setPreferredWidth(157);
                    break;
                case 643:
                    column.setPreferredWidth(142);
                    break;
                case 644:
                    column.setPreferredWidth(185);
                    break;
                case 645:
                    column.setPreferredWidth(174);
                    break;
                case 646:
                    column.setPreferredWidth(181);
                    break;
                case 647:
                    column.setPreferredWidth(214);
                    break;
                case 648:
                    column.setPreferredWidth(102);
                    break;
                case 649:
                    column.setPreferredWidth(154);
                    break;
                case 650:
                    column.setPreferredWidth(116);
                    break;
                case 651:
                    column.setPreferredWidth(124);
                    break;
                case 652:
                    column.setPreferredWidth(138);
                    break;
                case 653:
                    column.setPreferredWidth(124);
                    break;
                case 654:
                    column.setPreferredWidth(186);
                    break;
                case 655:
                    column.setPreferredWidth(174);
                    break;
                case 656:
                    column.setPreferredWidth(119);
                    break;
                case 657:
                    column.setPreferredWidth(80);
                    break;
                case 658:
                    column.setPreferredWidth(122);
                    break;
                case 659:
                    column.setPreferredWidth(143);
                    break;
                case 660:
                    column.setPreferredWidth(131);
                    break;
                case 661:
                    column.setPreferredWidth(134);
                    break;
                case 662:
                    column.setPreferredWidth(156);
                    break;
                case 663:
                    column.setPreferredWidth(121);
                    break;
                case 664:
                    column.setPreferredWidth(103);
                    break;
                case 665:
                    column.setPreferredWidth(164);
                    break;
                case 666:
                    column.setPreferredWidth(140);
                    break;
                case 667:
                    column.setPreferredWidth(137);
                    break;
                case 668:
                    column.setPreferredWidth(179);
                    break;
                case 669:
                    column.setPreferredWidth(172);
                    break;
                case 670:
                    column.setPreferredWidth(156);
                    break;
                case 671:
                    column.setPreferredWidth(102);
                    break;
                case 672:
                    column.setPreferredWidth(110);
                    break;
                case 673:
                    column.setPreferredWidth(112);
                    break;
                case 674:
                    column.setPreferredWidth(116);
                    break;
                case 675:
                    column.setPreferredWidth(93);
                    break;
                case 676:
                    column.setPreferredWidth(153);
                    break;
                case 677:
                    column.setPreferredWidth(98);
                    break;
                case 678:
                    column.setPreferredWidth(107);
                    break;
                case 679:
                    column.setPreferredWidth(209);
                    break;
                case 680:
                    column.setPreferredWidth(181);
                    break;
                case 681:
                    column.setPreferredWidth(94);
                    break;
                case 682:
                    column.setPreferredWidth(185);
                    break;
                case 683:
                    column.setPreferredWidth(190);
                    break;
                case 684:
                    column.setPreferredWidth(188);
                    break;
                case 685:
                    column.setPreferredWidth(192);
                    break;
                case 686:
                    column.setPreferredWidth(162);
                    break;
                case 687:
                    column.setPreferredWidth(182);
                    break;
                case 688:
                    column.setPreferredWidth(153);
                    break;
                case 689:
                    column.setPreferredWidth(171);
                    break;
                case 690:
                    column.setPreferredWidth(189);
                    break;
                case 691:
                    column.setPreferredWidth(198);
                    break;
                case 692:
                    column.setPreferredWidth(203);
                    break;
                case 693:
                    column.setPreferredWidth(180);
                    break;
                case 694:
                    column.setPreferredWidth(178);
                    break;
                case 695:
                    column.setPreferredWidth(183);
                    break;
                case 696:
                    column.setPreferredWidth(181);
                    break;
                case 697:
                    column.setPreferredWidth(185);
                    break;
                case 698:
                    column.setPreferredWidth(182);
                    break;
                case 699:
                    column.setPreferredWidth(191);
                    break;
                case 700:
                    column.setPreferredWidth(168);
                    break;
                case 701:
                    column.setPreferredWidth(152);
                    break;
                case 702:
                    column.setPreferredWidth(137);
                    break;
                case 703:
                    column.setPreferredWidth(176);
                    break;
                case 704:
                    column.setPreferredWidth(155);
                    break;
                case 705:
                    column.setPreferredWidth(123);
                    break;
                case 706:
                    column.setPreferredWidth(138);
                    break;
                case 707:
                    column.setPreferredWidth(167);
                    break;
                case 708:
                    column.setPreferredWidth(139);
                    break;
                case 709:
                    column.setPreferredWidth(153);
                    break;
                case 710:
                    column.setPreferredWidth(145);
                    break;
                case 711:
                    column.setPreferredWidth(142);
                    break;
                case 712:
                    column.setPreferredWidth(105);
                    break;
                case 713:
                    column.setPreferredWidth(119);
                    break;
                case 714:
                    column.setPreferredWidth(133);
                    break;
                case 715:
                    column.setPreferredWidth(173);
                    break;
                case 716:
                    column.setPreferredWidth(178);
                    break;
                case 717:
                    column.setPreferredWidth(187);
                    break;
                case 718:
                    column.setPreferredWidth(165);
                    break;
                case 719:
                    column.setPreferredWidth(85);
                    break;
                case 720:
                    column.setPreferredWidth(171);
                    break;
                case 721:
                    column.setPreferredWidth(154);
                    break;
                case 722:
                    column.setPreferredWidth(175);
                    break;
                case 723:
                    column.setPreferredWidth(163);
                    break;
                case 724:
                    column.setPreferredWidth(220);
                    break;
                case 725:
                    column.setPreferredWidth(180);
                    break;
                case 726:
                    column.setPreferredWidth(105);
                    break;
                case 727:
                    column.setPreferredWidth(153);
                    break;
                case 728:
                    column.setPreferredWidth(150);
                    break;
                case 729:
                    column.setPreferredWidth(134);
                    break;
                case 730:
                    column.setPreferredWidth(157);
                    break;
                case 731:
                    column.setPreferredWidth(157);
                    break;
                case 732:
                    column.setPreferredWidth(133);
                    break;
                case 733:
                    column.setPreferredWidth(127);
                    break;
                case 734:
                    column.setPreferredWidth(172);
                    break;
                case 735:
                    column.setPreferredWidth(214);
                    break;
                case 736:
                    column.setPreferredWidth(197);
                    break;
                case 737:
                    column.setPreferredWidth(239);
                    break;
                case 738:
                    column.setPreferredWidth(103);
                    break;
                case 739:
                    column.setPreferredWidth(130);
                    break;
                case 740:
                    column.setPreferredWidth(76);
                    break;
                case 741:
                    column.setPreferredWidth(102);
                    break;
                case 742:
                    column.setPreferredWidth(168);
                    break;
                case 743:
                    column.setPreferredWidth(107);
                    break;
                case 744:
                    column.setPreferredWidth(172);
                    break;
                case 745:
                    column.setPreferredWidth(129);
                    break;
                case 746:
                    column.setPreferredWidth(118);
                    break;
                case 747:
                    column.setPreferredWidth(121);
                    break;
                case 748:
                    column.setPreferredWidth(122);
                    break;
                case 749:
                    column.setPreferredWidth(189);
                    break;
                case 750:
                    column.setPreferredWidth(190);
                    break;
                case 751:
                    column.setPreferredWidth(217);
                    break;
                case 752:
                    column.setPreferredWidth(192);
                    break;
                case 753:
                    column.setPreferredWidth(214);
                    break;
                case 754:
                    column.setPreferredWidth(211);
                    break;
                case 755:
                    column.setPreferredWidth(145);
                    break;
                case 756:
                    column.setPreferredWidth(205);
                    break;
                case 757:
                    column.setPreferredWidth(115);
                    break;
                case 758:
                    column.setPreferredWidth(129);
                    break;
                case 759:
                    column.setPreferredWidth(131);
                    break;
                case 760:
                    column.setPreferredWidth(147);
                    break;
                case 761:
                    column.setPreferredWidth(135);
                    break;
                case 762:
                    column.setPreferredWidth(173);
                    break;
                case 763:
                    column.setPreferredWidth(170);
                    break;
                case 764:
                    column.setPreferredWidth(145);
                    break;
                case 765:
                    column.setPreferredWidth(196);
                    break;
                case 766:
                    column.setPreferredWidth(151);
                    break;
                case 767:
                    column.setPreferredWidth(135);
                    break;
                case 768:
                    column.setPreferredWidth(128);
                    break;
                case 769:
                    column.setPreferredWidth(183);
                    break;
                case 770:
                    column.setPreferredWidth(171);
                    break;
                case 771:
                    column.setPreferredWidth(174);
                    break;
                case 772:
                    column.setPreferredWidth(237);
                    break;
                case 773:
                    column.setPreferredWidth(90);
                    break;
                case 774:
                    column.setPreferredWidth(108);
                    break;
                case 775:
                    column.setPreferredWidth(102);
                    break;
                case 776:
                    column.setPreferredWidth(153);
                    break;
                case 777:
                    column.setPreferredWidth(126);
                    break;
                case 778:
                    column.setPreferredWidth(113);
                    break;
                case 779:
                    column.setPreferredWidth(132);
                    break;
                case 780:
                    column.setPreferredWidth(167);
                    break;
                case 781:
                    column.setPreferredWidth(174);
                    break;
                case 782:
                    column.setPreferredWidth(127);
                    break;
                case 783:
                    column.setPreferredWidth(137);
                    break;
                case 784:
                    column.setPreferredWidth(175);
                    break;
                case 785:
                    column.setPreferredWidth(160);
                    break;
                case 786:
                    column.setPreferredWidth(164);
                    break;
                case 787:
                    column.setPreferredWidth(167);
                    break;
                case 788:
                    column.setPreferredWidth(92);
                    break;
                case 789:
                    column.setPreferredWidth(125);
                    break;
                case 790:
                    column.setPreferredWidth(126);
                    break;
                case 791:
                    column.setPreferredWidth(147);
                    break;
                case 792:
                    column.setPreferredWidth(103);
                    break;
                case 793:
                    column.setPreferredWidth(94);
                    break;
                case 794:
                    column.setPreferredWidth(125);
                    break;
                case 795:
                    column.setPreferredWidth(125);
                    break;
                case 796:
                    column.setPreferredWidth(138);
                    break;
                case 797:
                    column.setPreferredWidth(129);
                    break;
                case 798:
                    column.setPreferredWidth(119);
                    break;
                case 799:
                    column.setPreferredWidth(224);
                    break;
                case 800:
                    column.setPreferredWidth(185);
                    break;
                case 801:
                    column.setPreferredWidth(181);
                    break;
                case 802:
                    column.setPreferredWidth(163);
                    break;
                case 803:
                    column.setPreferredWidth(153);
                    break;
                case 804:
                    column.setPreferredWidth(98);
                    break;
                case 805:
                    column.setPreferredWidth(168);
                    break;
                case 806:
                    column.setPreferredWidth(189);
                    break;
                case 807:
                    column.setPreferredWidth(114);
                    break;
                case 808:
                    column.setPreferredWidth(228);
                    break;
                case 809:
                    column.setPreferredWidth(234);
                    break;
                case 810:
                    column.setPreferredWidth(112);
                    break;
                case 811:
                    column.setPreferredWidth(165);
                    break;
                case 812:
                    column.setPreferredWidth(125);
                    break;
                case 813:
                    column.setPreferredWidth(175);
                    break;
                case 814:
                    column.setPreferredWidth(188);
                    break;
                case 815:
                    column.setPreferredWidth(244);
                    break;
                case 816:
                    column.setPreferredWidth(199);
                    break;
                case 817:
                    column.setPreferredWidth(218);
                    break;
                case 818:
                    column.setPreferredWidth(133);
                    break;
                case 819:
                    column.setPreferredWidth(152);
                    break;
                case 820:
                    column.setPreferredWidth(155);
                    break;
                case 821:
                    column.setPreferredWidth(159);
                    break;
                case 822:
                    column.setPreferredWidth(136);
                    break;
                case 823:
                    column.setPreferredWidth(179);
                    break;
                case 824:
                    column.setPreferredWidth(132);
                    break;
                case 825:
                    column.setPreferredWidth(145);
                    break;
                case 826:
                    column.setPreferredWidth(198);
                    break;
                case 827:
                    column.setPreferredWidth(207);
                    break;
                case 828:
                    column.setPreferredWidth(177);
                    break;
                case 829:
                    column.setPreferredWidth(111);
                    break;
                case 830:
                    column.setPreferredWidth(142);
                    break;
                case 831:
                    column.setPreferredWidth(153);
                    break;
                case 832:
                    column.setPreferredWidth(162);
                    break;
                case 833:
                    column.setPreferredWidth(224);
                    break;
                case 834:
                    column.setPreferredWidth(143);
                    break;
                case 835:
                    column.setPreferredWidth(170);
                    break;
                case 836:
                    column.setPreferredWidth(147);
                    break;
                case 837:
                    column.setPreferredWidth(125);
                    break;
                case 838:
                    column.setPreferredWidth(112);
                    break;
                case 839:
                    column.setPreferredWidth(106);
                    break;
                case 840:
                    column.setPreferredWidth(189);
                    break;
                case 841:
                    column.setPreferredWidth(117);
                    break;
                case 842:
                    column.setPreferredWidth(227);
                    break;
                case 843:
                    column.setPreferredWidth(123);
                    break;
                case 844:
                    column.setPreferredWidth(197);
                    break;
                case 845:
                    column.setPreferredWidth(183);
                    break;
                case 846:
                    column.setPreferredWidth(104);
                    break;
                case 847:
                    column.setPreferredWidth(121);
                    break;
                case 848:
                    column.setPreferredWidth(108);
                    break;
                case 849:
                    column.setPreferredWidth(142);
                    break;
                case 850:
                    column.setPreferredWidth(140);
                    break;
                case 851:
                    column.setPreferredWidth(233);
                    break;
                case 852:
                    column.setPreferredWidth(181);
                    break;
                case 853:
                    column.setPreferredWidth(204);
                    break;
                case 854:
                    column.setPreferredWidth(164);
                    break;
                case 855:
                    column.setPreferredWidth(206);
                    break;
                case 856:
                    column.setPreferredWidth(187);
                    break;
                case 857:
                    column.setPreferredWidth(182);
                    break;
                case 858:
                    column.setPreferredWidth(142);
                    break;
                case 859:
                    column.setPreferredWidth(145);
                    break;
                case 860:
                    column.setPreferredWidth(165);
                    break;
                case 861:
                    column.setPreferredWidth(123);
                    break;
                case 862:
                    column.setPreferredWidth(161);
                    break;
                case 863:
                    column.setPreferredWidth(188);
                    break;
                case 864:
                    column.setPreferredWidth(192);
                    break;
                case 865:
                    column.setPreferredWidth(188);
                    break;
                case 866:
                    column.setPreferredWidth(205);
                    break;
                case 867:
                    column.setPreferredWidth(207);
                    break;
                case 868:
                    column.setPreferredWidth(232);
                    break;
                case 869:
                    column.setPreferredWidth(175);
                    break;
                case 870:
                    column.setPreferredWidth(177);
                    break;
                case 871:
                    column.setPreferredWidth(179);
                    break;
                case 872:
                    column.setPreferredWidth(119);
                    break;
                case 873:
                    column.setPreferredWidth(168);
                    break;
                case 874:
                    column.setPreferredWidth(95);
                    break;
                case 875:
                    column.setPreferredWidth(126);
                    break;
                case 876:
                    column.setPreferredWidth(125);
                    break;
                case 877:
                    column.setPreferredWidth(129);
                    break;
                case 878:
                    column.setPreferredWidth(146);
                    break;
                case 879:
                    column.setPreferredWidth(213);
                    break;
                case 880:
                    column.setPreferredWidth(200);
                    break;
                case 881:
                    column.setPreferredWidth(194);
                    break;
                case 882:
                    column.setPreferredWidth(194);
                    break;
                case 883:
                    column.setPreferredWidth(170);
                    break;
                case 884:
                    column.setPreferredWidth(163);
                    break;
                case 885:
                    column.setPreferredWidth(133);
                    break;
                case 886:
                    column.setPreferredWidth(157);
                    break;
                case 887:
                    column.setPreferredWidth(140);
                    break;
                case 888:
                    column.setPreferredWidth(137);
                    break;
                case 889:
                    column.setPreferredWidth(147);
                    break;
                case 890:
                    column.setPreferredWidth(109);
                    break;
                case 891:
                    column.setPreferredWidth(149);
                    break;
                case 892:
                    column.setPreferredWidth(132);
                    break;
                case 893:
                    column.setPreferredWidth(115);
                    break;
                case 894:
                    column.setPreferredWidth(160);
                    break;
                case 895:
                    column.setPreferredWidth(163);
                    break;
                case 896:
                    column.setPreferredWidth(159);
                    break;
                case 897:
                    column.setPreferredWidth(173);
                    break;
                case 898:
                    column.setPreferredWidth(151);
                    break;
                case 899:
                    column.setPreferredWidth(154);
                    break;
                case 900:
                    column.setPreferredWidth(103);
                    break;
                case 901:
                    column.setPreferredWidth(152);
                    break;
                case 902:
                    column.setPreferredWidth(129);
                    break;
                case 903:
                    column.setPreferredWidth(188);
                    break;
                case 904:
                    column.setPreferredWidth(145);
                    break;
                case 905:
                    column.setPreferredWidth(154);
                    break;
                case 906:
                    column.setPreferredWidth(106);
                    break;
                case 907:
                    column.setPreferredWidth(140);
                    break;
                case 908:
                    column.setPreferredWidth(178);
                    break;
                case 909:
                    column.setPreferredWidth(88);
                    break;
                case 910:
                    column.setPreferredWidth(119);
                    break;
                case 911:
                    column.setPreferredWidth(154);
                    break;
                case 912:
                    column.setPreferredWidth(88);
                    break;
                case 913:
                    column.setPreferredWidth(153);
                    break;
                case 914:
                    column.setPreferredWidth(135);
                    break;
                case 915:
                    column.setPreferredWidth(145);
                    break;
                case 916:
                    column.setPreferredWidth(109);
                    break;
                case 917:
                    column.setPreferredWidth(129);
                    break;
                case 918:
                    column.setPreferredWidth(146);
                    break;
                case 919:
                    column.setPreferredWidth(109);
                    break;
                case 920:
                    column.setPreferredWidth(137);
                    break;
                case 921:
                    column.setPreferredWidth(141);
                    break;
                case 922:
                    column.setPreferredWidth(127);
                    break;
                case 923:
                    column.setPreferredWidth(157);
                    break;
                case 924:
                    column.setPreferredWidth(146);
                    break;
                case 925:
                    column.setPreferredWidth(130);
                    break;
                case 926:
                    column.setPreferredWidth(163);
                    break;
                case 927:
                    column.setPreferredWidth(198);
                    break;
                case 928:
                    column.setPreferredWidth(149);
                    break;
                case 929:
                    column.setPreferredWidth(138);
                    break;
                case 930:
                    column.setPreferredWidth(153);
                    break;
                case 931:
                    column.setPreferredWidth(158);
                    break;
                case 932:
                    column.setPreferredWidth(209);
                    break;
                case 933:
                    column.setPreferredWidth(189);
                    break;
                case 934:
                    column.setPreferredWidth(195);
                    break;
                case 935:
                    column.setPreferredWidth(214);
                    break;
                case 936:
                    column.setPreferredWidth(191);
                    break;
                case 937:
                    column.setPreferredWidth(175);
                    break;
                case 938:
                    column.setPreferredWidth(176);
                    break;
                case 939:
                    column.setPreferredWidth(217);
                    break;
                case 940:
                    column.setPreferredWidth(192);
                    break;
                case 941:
                    column.setPreferredWidth(189);
                    break;
                case 942:
                    column.setPreferredWidth(98);
                    break;
                case 943:
                    column.setPreferredWidth(221);
                    break;
                case 944:
                    column.setPreferredWidth(157);
                    break;
                case 945:
                    column.setPreferredWidth(203);
                    break;
                case 946:
                    column.setPreferredWidth(118);
                    break;
                case 947:
                    column.setPreferredWidth(101);
                    break;
                case 948:
                    column.setPreferredWidth(233);
                    break;
                case 949:
                    column.setPreferredWidth(206);
                    break;
                case 950:
                    column.setPreferredWidth(208);
                    break;
                case 951:
                    column.setPreferredWidth(168);
                    break;
                case 952:
                    column.setPreferredWidth(170);
                    break;
                case 953:
                    column.setPreferredWidth(152);
                    break;
                case 954:
                    column.setPreferredWidth(169);
                    break;
                case 955:
                    column.setPreferredWidth(144);
                    break;
                case 956:
                    column.setPreferredWidth(230);
                    break;
                case 957:
                    column.setPreferredWidth(188);
                    break;
                case 958:
                    column.setPreferredWidth(164);
                    break;
                case 959:
                    column.setPreferredWidth(166);
                    break;
                case 960:
                    column.setPreferredWidth(167);
                    break;
                case 961:
                    column.setPreferredWidth(187);
                    break;
                case 962:
                    column.setPreferredWidth(94);
                    break;
                case 963:
                    column.setPreferredWidth(225);
                    break;
                case 964:
                    column.setPreferredWidth(163);
                    break;
                case 965:
                    column.setPreferredWidth(201);
                    break;
                case 966:
                    column.setPreferredWidth(215);
                    break;
                case 967:
                    column.setPreferredWidth(194);
                    break;
                case 968:
                    column.setPreferredWidth(204);
                    break;
                case 969:
                    column.setPreferredWidth(171);
                    break;
                case 970:
                    column.setPreferredWidth(134);
                    break;
                case 971:
                    column.setPreferredWidth(234);
                    break;
                case 972:
                    column.setPreferredWidth(147);
                    break;
                case 973:
                    column.setPreferredWidth(224);
                    break;
                case 974:
                    column.setPreferredWidth(216);
                    break;
                case 975:
                    column.setPreferredWidth(217);
                    break;
                case 976:
                    column.setPreferredWidth(211);
                    break;
                case 977:
                    column.setPreferredWidth(253);
                    break;
                case 978:
                    column.setPreferredWidth(108);
                    break;
                case 979:
                    column.setPreferredWidth(229);
                    break;
                case 980:
                    column.setPreferredWidth(190);
                    break;
                case 981:
                    column.setPreferredWidth(190);
                    break;
                case 982:
                    column.setPreferredWidth(158);
                    break;
                case 983:
                    column.setPreferredWidth(152);
                    break;
                case 984:
                    column.setPreferredWidth(147);
                    break;
                case 985:
                    column.setPreferredWidth(127);
                    break;
                case 986:
                    column.setPreferredWidth(139);
                    break;
                case 987:
                    column.setPreferredWidth(147);
                    break;
                case 988:
                    column.setPreferredWidth(121);
                    break;
                case 989:
                    column.setPreferredWidth(153);
                    break;
                case 990:
                    column.setPreferredWidth(166);
                    break;
                case 991:
                    column.setPreferredWidth(195);
                    break;
                case 992:
                    column.setPreferredWidth(183);
                    break;
                case 993:
                    column.setPreferredWidth(161);
                    break;
                case 994:
                    column.setPreferredWidth(117);
                    break;
                case 995:
                    column.setPreferredWidth(164);
                    break;
                case 996:
                    column.setPreferredWidth(164);
                    break;
                case 997:
                    column.setPreferredWidth(167);
                    break;
                case 998:
                    column.setPreferredWidth(125);
                    break;
                case 999:
                    column.setPreferredWidth(108);
                    break;
                case 1000:
                    column.setPreferredWidth(126);
                    break;
                case 1001:
                    column.setPreferredWidth(134);
                    break;
                case 1002:
                    column.setPreferredWidth(132);
                    break;
                case 1003:
                    column.setPreferredWidth(125);
                    break;
                case 1004:
                    column.setPreferredWidth(197);
                    break;
                case 1005:
                    column.setPreferredWidth(198);
                    break;
                case 1006:
                    column.setPreferredWidth(158);
                    break;
                case 1007:
                    column.setPreferredWidth(200);
                    break;
                case 1008:
                    column.setPreferredWidth(183);
                    break;
                case 1009:
                    column.setPreferredWidth(204);
                    break;
                case 1010:
                    column.setPreferredWidth(120);
                    break;
                case 1011:
                    column.setPreferredWidth(167);
                    break;
                case 1012:
                    column.setPreferredWidth(214);
                    break;
                case 1013:
                    column.setPreferredWidth(231);
                    break;
                case 1014:
                    column.setPreferredWidth(136);
                    break;
                case 1015:
                    column.setPreferredWidth(200);
                    break;
                case 1016:
                    column.setPreferredWidth(215);
                    break;
                case 1017:
                    column.setPreferredWidth(217);
                    break;
                case 1018:
                    column.setPreferredWidth(245);
                    break;
                case 1019:
                    column.setPreferredWidth(212);
                    break;
                case 1020:
                    column.setPreferredWidth(240);
                    break;
                case 1021:
                    column.setPreferredWidth(138);
                    break;
                case 1022:
                    column.setPreferredWidth(226);
                    break;
                case 1023:
                    column.setPreferredWidth(218);
                    break;
                case 1024:
                    column.setPreferredWidth(219);
                    break;
                case 1025:
                    column.setPreferredWidth(187);
                    break;
                case 1026:
                    column.setPreferredWidth(188);
                    break;
                case 1027:
                    column.setPreferredWidth(199);
                    break;
                case 1028:
                    column.setPreferredWidth(200);
                    break;
                case 1029:
                    column.setPreferredWidth(227);
                    break;
                case 1030:
                    column.setPreferredWidth(229);
                    break;
                case 1031:
                    column.setPreferredWidth(237);
                    break;
                case 1032:
                    column.setPreferredWidth(208);
                    break;
                case 1033:
                    column.setPreferredWidth(187);
                    break;
                case 1034:
                    column.setPreferredWidth(190);
                    break;
                case 1035:
                    column.setPreferredWidth(130);
                    break;
                case 1036:
                    column.setPreferredWidth(143);
                    break;
                case 1037:
                    column.setPreferredWidth(206);
                    break;
                case 1038:
                    column.setPreferredWidth(133);
                    break;
                case 1039:
                    column.setPreferredWidth(128);
                    break;
                case 1040:
                    column.setPreferredWidth(167);
                    break;
                case 1041:
                    column.setPreferredWidth(172);
                    break;
                case 1042:
                    column.setPreferredWidth(186);
                    break;
                case 1043:
                    column.setPreferredWidth(152);
                    break;
                case 1044:
                    column.setPreferredWidth(199);
                    break;
                case 1045:
                    column.setPreferredWidth(184);
                    break;
                case 1046:
                    column.setPreferredWidth(188);
                    break;
                case 1047:
                    column.setPreferredWidth(238);
                    break;
                case 1048:
                    column.setPreferredWidth(105);
                    break;
                case 1049:
                    column.setPreferredWidth(151);
                    break;
                case 1050:
                    column.setPreferredWidth(132);
                    break;
                case 1051:
                    column.setPreferredWidth(148);
                    break;
                case 1052:
                    column.setPreferredWidth(157);
                    break;
                case 1053:
                    column.setPreferredWidth(143);
                    break;
                case 1054:
                    column.setPreferredWidth(220);
                    break;
                case 1055:
                    column.setPreferredWidth(200);
                    break;
                case 1056:
                    column.setPreferredWidth(109);
                    break;
                case 1057:
                    column.setPreferredWidth(179);
                    break;
                case 1058:
                    column.setPreferredWidth(156);
                    break;
                case 1059:
                    column.setPreferredWidth(85);
                    break;
                case 1060:
                    column.setPreferredWidth(218);
                    break;
                case 1061:
                    column.setPreferredWidth(248);
                    break;
                case 1062:
                    column.setPreferredWidth(145);
                    break;
                case 1063:
                    column.setPreferredWidth(237);
                    break;
                case 1064:
                    column.setPreferredWidth(166);
                    break;
                case 1065:
                    column.setPreferredWidth(144);
                    break;
                case 1066:
                    column.setPreferredWidth(94);
                    break;
                case 1067:
                    column.setPreferredWidth(152);
                    break;
                case 1068:
                    column.setPreferredWidth(211);
                    break;
                case 1069:
                    column.setPreferredWidth(132);
                    break;
                case 1070:
                    column.setPreferredWidth(127);
                    break;
                case 1071:
                    column.setPreferredWidth(140);
                    break;
                case 1072:
                    column.setPreferredWidth(140);
                    break;
                case 1073:
                    column.setPreferredWidth(179);
                    break;
                case 1074:
                    column.setPreferredWidth(150);
                    break;
                case 1075:
                    column.setPreferredWidth(149);
                    break;
                case 1076:
                    column.setPreferredWidth(114);
                    break;
                case 1077:
                    column.setPreferredWidth(126);
                    break;
                case 1078:
                    column.setPreferredWidth(153);
                    break;
                case 1079:
                    column.setPreferredWidth(195);
                    break;
                case 1080:
                    column.setPreferredWidth(176);
                    break;
                case 1081:
                    column.setPreferredWidth(142);
                    break;
                case 1082:
                    column.setPreferredWidth(133);
                    break;
                case 1083:
                    column.setPreferredWidth(119);
                    break;
                case 1084:
                    column.setPreferredWidth(165);
                    break;
                case 1085:
                    column.setPreferredWidth(158);
                    break;
                case 1086:
                    column.setPreferredWidth(163);
                    break;
                case 1087:
                    column.setPreferredWidth(170);
                    break;
                case 1088:
                    column.setPreferredWidth(162);
                    break;
                case 1089:
                    column.setPreferredWidth(170);
                    break;
                case 1090:
                    column.setPreferredWidth(132);
                    break;
                case 1091:
                    column.setPreferredWidth(141);
                    break;
                case 1092:
                    column.setPreferredWidth(161);
                    break;
                case 1093:
                    column.setPreferredWidth(224);
                    break;
                case 1094:
                    column.setPreferredWidth(180);
                    break;
                case 1095:
                    column.setPreferredWidth(144);
                    break;
                case 1096:
                    column.setPreferredWidth(210);
                    break;
                case 1097:
                    column.setPreferredWidth(156);
                    break;
                case 1098:
                    column.setPreferredWidth(176);
                    break;
                case 1099:
                    column.setPreferredWidth(172);
                    break;
                case 1100:
                    column.setPreferredWidth(143);
                    break;
                case 1101:
                    column.setPreferredWidth(144);
                    break;
                case 1102:
                    column.setPreferredWidth(185);
                    break;
                case 1103:
                    column.setPreferredWidth(162);
                    break;
                case 1104:
                    column.setPreferredWidth(203);
                    break;
                case 1105:
                    column.setPreferredWidth(146);
                    break;
                case 1106:
                    column.setPreferredWidth(110);
                    break;
                case 1107:
                    column.setPreferredWidth(184);
                    break;
                case 1108:
                    column.setPreferredWidth(217);
                    break;
                case 1109:
                    column.setPreferredWidth(210);
                    break;
                case 1110:
                    column.setPreferredWidth(103);
                    break;
                case 1111:
                    column.setPreferredWidth(129);
                    break;
                case 1112:
                    column.setPreferredWidth(115);
                    break;
                case 1113:
                    column.setPreferredWidth(178);
                    break;
                case 1114:
                    column.setPreferredWidth(177);
                    break;
                case 1115:
                    column.setPreferredWidth(220);
                    break;
                case 1116:
                    column.setPreferredWidth(168);
                    break;
                case 1117:
                    column.setPreferredWidth(161);
                    break;
                case 1118:
                    column.setPreferredWidth(212);
                    break;
                case 1119:
                    column.setPreferredWidth(159);
                    break;
                case 1120:
                    column.setPreferredWidth(191);
                    break;
                case 1121:
                    column.setPreferredWidth(148);
                    break;
                case 1122:
                    column.setPreferredWidth(94);
                    break;
                case 1123:
                    column.setPreferredWidth(129);
                    break;
                case 1124:
                    column.setPreferredWidth(136);
                    break;
                case 1125:
                    column.setPreferredWidth(146);
                    break;
                case 1126:
                    column.setPreferredWidth(192);
                    break;
                case 1127:
                    column.setPreferredWidth(188);
                    break;
                case 1128:
                    column.setPreferredWidth(144);
                    break;
                case 1129:
                    column.setPreferredWidth(174);
                    break;
                case 1130:
                    column.setPreferredWidth(207);
                    break;
                case 1131:
                    column.setPreferredWidth(164);
                    break;
                case 1132:
                    column.setPreferredWidth(214);
                    break;
                case 1133:
                    column.setPreferredWidth(160);
                    break;
                case 1134:
                    column.setPreferredWidth(137);
                    break;
                case 1135:
                    column.setPreferredWidth(112);
                    break;
                case 1136:
                    column.setPreferredWidth(132);
                    break;
                case 1137:
                    column.setPreferredWidth(122);
                    break;
                case 1138:
                    column.setPreferredWidth(145);
                    break;
                case 1139:
                    column.setPreferredWidth(150);
                    break;
                case 1140:
                    column.setPreferredWidth(179);
                    break;
                case 1141:
                    column.setPreferredWidth(142);
                    break;
                case 1142:
                    column.setPreferredWidth(194);
                    break;
                case 1143:
                    column.setPreferredWidth(146);
                    break;
                case 1144:
                    column.setPreferredWidth(173);
                    break;
                case 1145:
                    column.setPreferredWidth(232);
                    break;
                case 1146:
                    column.setPreferredWidth(205);
                    break;
                case 1147:
                    column.setPreferredWidth(173);
                    break;
                case 1148:
                    column.setPreferredWidth(174);
                    break;
                case 1149:
                    column.setPreferredWidth(210);
                    break;
                case 1150:
                    column.setPreferredWidth(172);
                    break;
                case 1151:
                    column.setPreferredWidth(173);
                    break;
                case 1152:
                    column.setPreferredWidth(152);
                    break;
                case 1153:
                    column.setPreferredWidth(141);
                    break;
                case 1154:
                    column.setPreferredWidth(181);
                    break;
                case 1155:
                    column.setPreferredWidth(192);
                    break;
                case 1156:
                    column.setPreferredWidth(192);
                    break;
                case 1157:
                    column.setPreferredWidth(225);
                    break;
                case 1158:
                    column.setPreferredWidth(207);
                    break;
                case 1159:
                    column.setPreferredWidth(203);
                    break;
                case 1160:
                    column.setPreferredWidth(161);
                    break;
                case 1161:
                    column.setPreferredWidth(190);
                    break;
                case 1162:
                    column.setPreferredWidth(205);
                    break;
                case 1163:
                    column.setPreferredWidth(144);
                    break;
                case 1164:
                    column.setPreferredWidth(210);
                    break;
                case 1165:
                    column.setPreferredWidth(248);
                    break;
                case 1166:
                    column.setPreferredWidth(219);
                    break;
                case 1167:
                    column.setPreferredWidth(241);
                    break;
                case 1168:
                    column.setPreferredWidth(234);
                    break;
                case 1169:
                    column.setPreferredWidth(162);
                    break;
                case 1170:
                    column.setPreferredWidth(229);
                    break;
                case 1171:
                    column.setPreferredWidth(108);
                    break;
                case 1172:
                    column.setPreferredWidth(128);
                    break;
                case 1173:
                    column.setPreferredWidth(202);
                    break;
                case 1174:
                    column.setPreferredWidth(138);
                    break;
                case 1175:
                    column.setPreferredWidth(176);
                    break;
                case 1176:
                    column.setPreferredWidth(201);
                    break;
                case 1177:
                    column.setPreferredWidth(158);
                    break;
                case 1178:
                    column.setPreferredWidth(200);
                    break;
                case 1179:
                    column.setPreferredWidth(202);
                    break;
                case 1180:
                    column.setPreferredWidth(165);
                    break;
                case 1181:
                    column.setPreferredWidth(175);
                    break;
                case 1182:
                    column.setPreferredWidth(205);
                    break;
                case 1183:
                    column.setPreferredWidth(193);
                    break;
                case 1184:
                    column.setPreferredWidth(177);
                    break;
                case 1185:
                    column.setPreferredWidth(147);
                    break;
                case 1186:
                    column.setPreferredWidth(207);
                    break;
                case 1187:
                    column.setPreferredWidth(210);
                    break;
                case 1188:
                    column.setPreferredWidth(187);
                    break;
                case 1189:
                    column.setPreferredWidth(203);
                    break;
                case 1190:
                    column.setPreferredWidth(198);
                    break;
                case 1191:
                    column.setPreferredWidth(117);
                    break;
                case 1192:
                    column.setPreferredWidth(162);
                    break;
                case 1193:
                    column.setPreferredWidth(160);
                    break;
                case 1194:
                    column.setPreferredWidth(195);
                    break;
                case 1195:
                    column.setPreferredWidth(194);
                    break;
                case 1196:
                    column.setPreferredWidth(216);
                    break;
                case 1197:
                    column.setPreferredWidth(270);
                    break;
                case 1198:
                    column.setPreferredWidth(146);
                    break;
                case 1199:
                    column.setPreferredWidth(185);
                    break;
                case 1200:
                    column.setPreferredWidth(147);
                    break;
                case 1201:
                    column.setPreferredWidth(153);
                    break;
                case 1202:
                    column.setPreferredWidth(188);
                    break;
                case 1203:
                    column.setPreferredWidth(119);
                    break;
                case 1204:
                    column.setPreferredWidth(162);
                    break;
                case 1205:
                    column.setPreferredWidth(184);
                    break;
                case 1206:
                    column.setPreferredWidth(179);
                    break;
                case 1207:
                    column.setPreferredWidth(178);
                    break;
                case 1208:
                    column.setPreferredWidth(221);
                    break;
                case 1209:
                    column.setPreferredWidth(224);
                    break;
                case 1210:
                    column.setPreferredWidth(239);
                    break;
                case 1211:
                    column.setPreferredWidth(231);
                    break;
                case 1212:
                    column.setPreferredWidth(231);
                    break;
                case 1213:
                    column.setPreferredWidth(204);
                    break;
                case 1214:
                    column.setPreferredWidth(180);
                    break;
                case 1215:
                    column.setPreferredWidth(138);
                    break;
                case 1216:
                    column.setPreferredWidth(165);
                    break;
                case 1217:
                    column.setPreferredWidth(225);
                    break;
                case 1218:
                    column.setPreferredWidth(165);
                    break;
                case 1219:
                    column.setPreferredWidth(147);
                    break;
                case 1220:
                    column.setPreferredWidth(98);
                    break;
                case 1221:
                    column.setPreferredWidth(141);
                    break;
                case 1222:
                    column.setPreferredWidth(150);
                    break;
                case 1223:
                    column.setPreferredWidth(179);
                    break;
                case 1224:
                    column.setPreferredWidth(139);
                    break;
                case 1225:
                    column.setPreferredWidth(135);
                    break;
                case 1226:
                    column.setPreferredWidth(137);
                    break;
                default:
                    column.setPreferredWidth(133);
                    break;
            }
        }
        tbUser.setDefaultRenderer(Object.class, new WarnaTable());

        TKd.setDocument(new batasInput((byte)30).getKata(TKd));
        TPass.setDocument(new batasInput((byte)50).getKata(TPass));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do falseT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new widget.PopupMenu();
        MnCopyHakAkses = new widget.MenuItem();
        MnSetUser = new widget.MenuItem();
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
        Jabatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JabatanKeyPressed(evt);
            }
        });

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

        tbUser.setAutoCreateRowSorter(true);
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
        TNmUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNmUserKeyPressed(evt);
            }
        });
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
        if(TKd.getText().trim().equals("")||TNmUser.getText().trim().equals("")){
            Valid.textKosong(TKd,"User");
        }else if(TPass.getText().trim().equals("")){
            Valid.textKosong(TPass,"Password");
        }else{
            if(Sequel.menyimpantf("user","AES_ENCRYPT('"+TKd.getText()+"','nur'),AES_ENCRYPT('"+TPass.getText()+"','windi'),'false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false',"+
                    "'false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false','false'","User")==true){
                tabMode.addRow(new Object[]{
                    TKd.getText(),TNmUser.getText(),Jabatan.getText(),TPass.getText(),false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false
                });
                emptTeks();
                LCount.setText(""+tbUser.getRowCount());
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,TPass,BtnHapus);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbUser.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            TKd.requestFocus();
        }else if(TPass.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(! TPass.getText().trim().equals("")){
            if(Sequel.queryutf("delete from user where id_user=AES_ENCRYPT('"+TKd.getText()+"','nur')")==true){
                if(tbUser.getSelectedRow()!= -1){
                    tabMode.removeRow(tbUser.convertRowIndexToModel(tbUser.getSelectedRow()));
                    emptTeks();
                    LCount.setText(""+tbUser.getRowCount());
                }
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnSimpan, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TKd.getText().trim().equals("")||TNmUser.getText().trim().equals("")){
            Valid.textKosong(TKd,"User");
        }else if(TPass.getText().trim().equals("")){
            Valid.textKosong(TPass,"Password");
        }else{
            i=tbUser.getSelectedRow();
            if(i!= -1){
                if(Sequel.mengedittf("user","id_user=AES_ENCRYPT('"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),0).toString()+"','nur')",
                    "id_user=AES_ENCRYPT('"+TKd.getText()+"','nur'),"+
                    "password=AES_ENCRYPT('"+TPass.getText()+"','windi'),"+
                    "penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),4).toString()+"', "+
                    "obat_penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),5).toString()+"',"+
                    "dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),6).toString()+"',"+
                    "jadwal_praktek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),7).toString()+"',"+
                    "petugas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),8).toString()+"',"+
                    "pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),9).toString()+"',"+
                    "registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),10).toString()+"',"+
                    "tindakan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),11).toString()+"',"+
                    "kamar_inap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),12).toString()+"',"+
                    "tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),13).toString()+"',"+
                    "operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),14).toString()+"',"+
                    "rujukan_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),15).toString()+"',"+
                    "rujukan_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),16).toString()+"',"+
                    "beri_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),17).toString()+"',"+
                    "resep_pulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),18).toString()+"',"+
                    "pasien_meninggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),19).toString()+"',"+
                    "diet_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),20).toString()+"',"+
                    "kelahiran_bayi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),21).toString()+"',"+
                    "periksa_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),22).toString()+"',"+
                    "periksa_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),23).toString()+"',"+
                    "kasir_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),24).toString()+"',"+
                    "deposit_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),25).toString()+"',"+
                    "piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),26).toString()+"',"+
                    "peminjaman_berkas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),27).toString()+"',"+
                    "barcode='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),28).toString()+"',"+
                    "presensi_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),29).toString()+"',"+
                    "presensi_bulanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),30).toString()+"',"+
                    "pegawai_admin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),31).toString()+"',"+
                    "pegawai_user='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),32).toString()+"',"+
                    "suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),33).toString()+"',"+
                    "satuan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),34).toString()+"',"+
                    "konversi_satuan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),35).toString()+"',"+
                    "jenis_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),36).toString()+"',"+
                    "obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),37).toString()+"',"+
                    "stok_opname_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),38).toString()+"',"+
                    "stok_obat_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),39).toString()+"',"+
                    "pengadaan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),40).toString()+"',"+
                    "pemesanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),41).toString()+"',"+
                    "penjualan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),42).toString()+"',"+
                    "piutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),43).toString()+"',"+
                    "retur_ke_suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),44).toString()+"',"+
                    "retur_dari_pembeli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),45).toString()+"',"+
                    "retur_obat_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),46).toString()+"',"+
                    "retur_piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),47).toString()+"',"+
                    "keuntungan_penjualan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),48).toString()+"',"+
                    "keuntungan_beri_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),49).toString()+"',"+
                    "sirkulasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),50).toString()+"',"+
                    "ipsrs_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),51).toString()+"',"+
                    "ipsrs_pengadaan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),52).toString()+"',"+
                    "ipsrs_stok_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),53).toString()+"',"+
                    "ipsrs_rekap_pengadaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),54).toString()+"',"+
                    "ipsrs_rekap_stok_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),55).toString()+"',"+
                    "ipsrs_pengeluaran_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),56).toString()+"',"+
                    "inventaris_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),57).toString()+"',"+
                    "inventaris_kategori='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),58).toString()+"',"+
                    "inventaris_merk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),59).toString()+"',"+
                    "inventaris_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),60).toString()+"',"+
                    "inventaris_produsen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),61).toString()+"',"+
                    "inventaris_koleksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),62).toString()+"',"+
                    "inventaris_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),63).toString()+"',"+
                    "inventaris_sirkulasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),64).toString()+"',"+
                    "parkir_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),65).toString()+"',"+
                    "parkir_in='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),66).toString()+"',"+
                    "parkir_out='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),67).toString()+"',"+
                    "parkir_rekap_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),68).toString()+"',"+
                    "parkir_rekap_bulanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),69).toString()+"',"+
                    "informasi_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),70).toString()+"',"+
                    "harian_tindakan_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),71).toString()+"',"+
                    "obat_per_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),72).toString()+"',"+
                    "obat_per_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),73).toString()+"',"+
                    "obat_per_dokter_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),74).toString()+"',"+
                    "obat_per_dokter_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),75).toString()+"',"+
                    "harian_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),76).toString()+"',"+
                    "bulanan_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),77).toString()+"',"+
                    "harian_paramedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),78).toString()+"',"+
                    "bulanan_paramedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),79).toString()+"',"+
                    "pembayaran_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),80).toString()+"',"+
                    "pembayaran_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),81).toString()+"',"+
                    "rekap_pembayaran_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),82).toString()+"',"+
                    "rekap_pembayaran_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),83).toString()+"',"+
                    "tagihan_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),84).toString()+"',"+
                    "tambahan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),85).toString()+"',"+
                    "potongan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),86).toString()+"',"+
                    "resep_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),87).toString()+"',"+
                    "resume_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),88).toString()+"',"+
                    "penyakit_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),89).toString()+"',"+
                    "penyakit_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),90).toString()+"',"+
                    "kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),91).toString()+"',"+
                    "tarif_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),92).toString()+"',"+
                    "tarif_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),93).toString()+"',"+
                    "tarif_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),94).toString()+"',"+
                    "tarif_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),95).toString()+"',"+
                    "tarif_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),96).toString()+"',"+
                    "akun_rekening='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),97).toString()+"',"+
                    "rekening_tahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),98).toString()+"',"+
                    "posting_jurnal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),99).toString()+"',"+
                    "buku_besar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),100).toString()+"',"+
                    "cashflow='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),101).toString()+"',"+
                    "keuangan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),102).toString()+"',"+
                    "pengeluaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),103).toString()+"',"+
                    "setup_pjlab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),104).toString()+"',"+
                    "setup_otolokasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),105).toString()+"',"+
                    "setup_jam_kamin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),106).toString()+"',"+
                    "setup_embalase='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),107).toString()+"',"+
                    "tracer_login='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),108).toString()+"',"+
                    "display='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),109).toString()+"',"+
                    "set_harga_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),110).toString()+"',"+
                    "set_penggunaan_tarif='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),111).toString()+"',"+
                    "set_oto_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),112).toString()+"',"+
                    "biaya_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),113).toString()+"',"+
                    "biaya_masuk_sekali='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),114).toString()+"',"+
                    "set_no_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),115).toString()+"',"+
                    "billing_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),116).toString()+"',"+
                    "billing_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),117).toString()+"',"+
                    "jm_ranap_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),118).toString()+"',"+
                    "igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),119).toString()+"',"+
                    "barcoderalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),120).toString()+"',"+
                    "barcoderanap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),121).toString()+"',"+
                    "set_harga_obat_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),122).toString()+"',"+
                    "set_harga_obat_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),123).toString()+"',"+
                    "penyakit_pd3i='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),124).toString()+"',"+
                    "surveilans_pd3i='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),125).toString()+"',"+
                    "surveilans_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),126).toString()+"',"+
                    "diagnosa_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),127).toString()+"',"+
                    "surveilans_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),128).toString()+"',"+
                    "pny_takmenular_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),129).toString()+"',"+
                    "pny_takmenular_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),130).toString()+"',"+
                    "kunjungan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),131).toString()+"',"+
                    "rl32='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),132).toString()+"',"+
                    "rl33='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),133).toString()+"',"+
                    "rl37='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),134).toString()+"',"+
                    "rl38='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),135).toString()+"',"+
                    "harian_tindakan_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),136).toString()+"',"+
                    "sms='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),137).toString()+"',"+
                    "sidikjari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),138).toString()+"',"+
                    "jam_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),139).toString()+"',"+
                    "jadwal_pegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),140).toString()+"',"+
                    "parkir_barcode='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),141).toString()+"',"+
                    "set_nota='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),142).toString()+"',"+
                    "dpjp_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),143).toString()+"',"+
                    "mutasi_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),144).toString()+"',"+
                    "rl34='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),145).toString()+"',"+
                    "rl36='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),146).toString()+"',"+
                    "fee_visit_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),147).toString()+"',"+
                    "fee_bacaan_ekg='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),148).toString()+"',"+
                    "fee_rujukan_rontgen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),149).toString()+"',"+
                    "fee_rujukan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),150).toString()+"',"+
                    "fee_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),151).toString()+"',"+
                    "akun_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),152).toString()+"',"+
                    "bayar_pemesanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),153).toString()+"',"+
                    "obat_per_dokter_peresep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),154).toString()+"',"+
                    "ipsrs_jenis_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),155).toString()+"',"+
                    "pemasukan_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),156).toString()+"',"+
                    "pengaturan_rekening='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),157).toString()+"',"+
                    "closing_kasir='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),158).toString()+"',"+
                    "keterlambatan_presensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),159).toString()+"',"+
                    "set_harga_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),160).toString()+"',"+
                    "rekap_per_shift='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),161).toString()+"',"+
                    "bpjs_cek_nik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),162).toString()+"',"+
                    "bpjs_cek_kartu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),163).toString()+"',"+
                    "bpjs_cek_riwayat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),164).toString()+"',"+
                    "obat_per_cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),165).toString()+"',"+
                    "kunjungan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),166).toString()+"',"+
                    "bayar_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),167).toString()+"',"+
                    "payment_point='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),168).toString()+"',"+
                    "bpjs_cek_nomor_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),169).toString()+"',"+
                    "icd9='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),170).toString()+"',"+
                    "darurat_stok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),171).toString()+"',"+
                    "retensi_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),172).toString()+"',"+
                    "temporary_presensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),173).toString()+"',"+
                    "jurnal_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),174).toString()+"',"+
                    "sirkulasi_obat2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),175).toString()+"',"+
                    "edit_registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),176).toString()+"',"+
                    "bpjs_referensi_diagnosa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),177).toString()+"',"+
                    "bpjs_referensi_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),178).toString()+"',"+
                    "industrifarmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),179).toString()+"',"+
                    "harian_js='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),180).toString()+"',"+
                    "bulanan_js='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),181).toString()+"',"+
                    "harian_paket_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),182).toString()+"',"+
                    "bulanan_paket_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),183).toString()+"',"+
                    "piutang_pasien2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),184).toString()+"',"+
                    "bpjs_referensi_faskes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),185).toString()+"',"+
                    "bpjs_sep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),186).toString()+"',"+
                    "pengambilan_utd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),187).toString()+"',"+
                    "tarif_utd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),188).toString()+"',"+
                    "pengambilan_utd2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),189).toString()+"',"+
                    "utd_medis_rusak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),190).toString()+"',"+
                    "pengambilan_penunjang_utd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),191).toString()+"',"+
                    "pengambilan_penunjang_utd2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),192).toString()+"',"+
                    "utd_penunjang_rusak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),193).toString()+"',"+
                    "suplier_penunjang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),194).toString()+"',"+
                    "utd_donor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),195).toString()+"',"+
                    "bpjs_monitoring_klaim='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),196).toString()+"',"+
                    "utd_cekal_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),197).toString()+"',"+
                    "utd_komponen_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),198).toString()+"',"+
                    "utd_stok_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),199).toString()+"',"+
                    "utd_pemisahan_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),200).toString()+"',"+
                    "harian_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),201).toString()+"',"+
                    "rincian_piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),202).toString()+"',"+
                    "keuntungan_beri_obat_nonpiutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),203).toString()+"',"+
                    "reklasifikasi_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),204).toString()+"',"+
                    "reklasifikasi_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),205).toString()+"',"+
                    "utd_penyerahan_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),206).toString()+"',"+
                    "hutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),207).toString()+"',"+
                    "riwayat_obat_alkes_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),208).toString()+"',"+
                    "sensus_harian_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),209).toString()+"',"+
                    "rl4a='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),210).toString()+"',"+
                    "aplicare_referensi_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),211).toString()+"',"+
                    "aplicare_ketersediaan_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),212).toString()+"',"+
                    "inacbg_klaim_baru_otomatis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),213).toString()+"',"+
                    "inacbg_klaim_baru_manual='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),214).toString()+"',"+
                    "inacbg_coder_nik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),215).toString()+"',"+
                    "mutasi_berkas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),216).toString()+"',"+
                    "akun_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),217).toString()+"',"+
                    "harian_kso='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),218).toString()+"',"+
                    "bulanan_kso='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),219).toString()+"',"+
                    "harian_menejemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),220).toString()+"',"+
                    "bulanan_menejemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),221).toString()+"',"+
                    "inhealth_cek_eligibilitas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),222).toString()+"',"+
                    "inhealth_referensi_jenpel_ruang_rawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),223).toString()+"',"+
                    "inhealth_referensi_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),224).toString()+"',"+
                    "inhealth_referensi_faskes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),225).toString()+"',"+
                    "inhealth_sjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),226).toString()+"',"+
                    "piutang_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),227).toString()+"',"+
                    "piutang_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),228).toString()+"',"+
                    "detail_piutang_penjab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),229).toString()+"',"+
                    "lama_pelayanan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),230).toString()+"',"+
                    "catatan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),231).toString()+"',"+
                    "rl4b='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),232).toString()+"',"+
                    "rl4asebab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),233).toString()+"',"+
                    "rl4bsebab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),234).toString()+"',"+
                    "data_HAIs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),235).toString()+"',"+
                    "harian_HAIs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),236).toString()+"',"+
                    "bulanan_HAIs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),237).toString()+"',"+
                    "hitung_bor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),238).toString()+"',"+
                    "perusahaan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),239).toString()+"',"+
                    "resep_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),240).toString()+"',"+
                    "lama_pelayanan_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),241).toString()+"',"+
                    "hitung_alos='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),242).toString()+"',"+
                    "detail_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),243).toString()+"',"+
                    "rujukan_poli_internal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),244).toString()+"',"+
                    "rekap_poli_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),245).toString()+"',"+
                    "grafik_kunjungan_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),246).toString()+"',"+
                    "grafik_kunjungan_perdokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),247).toString()+"',"+
                    "grafik_kunjungan_perpekerjaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),248).toString()+"',"+
                    "grafik_kunjungan_perpendidikan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),249).toString()+"',"+
                    "grafik_kunjungan_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),250).toString()+"',"+
                    "berkas_digital_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),251).toString()+"',"+
                    "penyakit_menular_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),252).toString()+"',"+
                    "penyakit_menular_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),253).toString()+"',"+
                    "grafik_kunjungan_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),254).toString()+"',"+
                    "grafik_kunjungan_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),255).toString()+"',"+
                    "grafik_kunjungan_demografi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),256).toString()+"',"+
                    "grafik_kunjungan_statusdaftartahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),257).toString()+"',"+
                    "grafik_kunjungan_statusdaftartahun2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),258).toString()+"',"+
                    "grafik_kunjungan_statusdaftarbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),259).toString()+"',"+
                    "grafik_kunjungan_statusdaftarbulan2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),260).toString()+"',"+
                    "grafik_kunjungan_statusdaftartanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),261).toString()+"',"+
                    "grafik_kunjungan_statusdaftartanggal2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),262).toString()+"',"+
                    "grafik_kunjungan_statusbataltahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),263).toString()+"',"+
                    "grafik_kunjungan_statusbatalbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),264).toString()+"',"+
                    "pcare_cek_penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),265).toString()+"',"+
                    "grafik_kunjungan_statusbataltanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),266).toString()+"',"+
                    "kategori_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),267).toString()+"',"+
                    "golongan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),268).toString()+"',"+
                    "pemberian_obat_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),269).toString()+"',"+
                    "penjualan_obat_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),270).toString()+"',"+
                    "pcare_cek_kesadaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),271).toString()+"',"+
                    "pembatalan_periksa_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),272).toString()+"',"+
                    "pembayaran_per_unit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),273).toString()+"',"+
                    "rekap_pembayaran_per_unit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),274).toString()+"',"+
                    "grafik_kunjungan_percarabayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),275).toString()+"',"+
                    "ipsrs_pengadaan_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),276).toString()+"',"+
                    "ipsrs_stokkeluar_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),277).toString()+"',"+
                    "grafik_kunjungan_ranaptahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),278).toString()+"',"+
                    "pcare_cek_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),279).toString()+"',"+
                    "grafik_lab_ralantahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),280).toString()+"',"+
                    "grafik_rad_ralantahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),281).toString()+"',"+
                    "cek_entry_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),282).toString()+"',"+
                    "inacbg_klaim_baru_manual2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),283).toString()+"',"+
                    "permintaan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),284).toString()+"',"+
                    "rekap_permintaan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),285).toString()+"',"+
                    "surat_pemesanan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),286).toString()+"',"+
                    "permintaan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),287).toString()+"',"+
                    "rekap_permintaan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),288).toString()+"',"+
                    "surat_pemesanan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),289).toString()+"',"+
                    "grafik_per_perujuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),290).toString()+"',"+
                    "bpjs_cek_prosedur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),291).toString()+"',"+
                    "bpjs_cek_kelas_rawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),292).toString()+"',"+
                    "bpjs_cek_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),293).toString()+"',"+
                    "bpjs_cek_spesialistik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),294).toString()+"',"+
                    "bpjs_cek_ruangrawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),295).toString()+"',"+
                    "bpjs_cek_carakeluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),296).toString()+"',"+
                    "bpjs_cek_pasca_pulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),297).toString()+"',"+
                    "detail_tindakan_okvk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),298).toString()+"',"+
                    "billing_parsial='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),299).toString()+"',"+
                    "bpjs_cek_nomor_rujukan_rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),300).toString()+"',"+
                    "bpjs_cek_rujukan_kartu_pcare='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),301).toString()+"',"+
                    "bpjs_cek_rujukan_kartu_rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),302).toString()+"',"+
                    "akses_depo_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),303).toString()+"',"+
                    "bpjs_rujukan_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),304).toString()+"',"+
                    "grafik_lab_ralanbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),305).toString()+"',"+
                    "pengeluaran_stok_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),306).toString()+"',"+
                    "grafik_rad_ralanbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),307).toString()+"',"+
                    "detailjmdokter2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),308).toString()+"',"+
                    "pengaduan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),309).toString()+"',"+
                    "grafik_lab_ralanhari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),310).toString()+"',"+
                    "grafik_rad_ralanhari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),311).toString()+"',"+
                    "sensus_harian_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),312).toString()+"',"+
                    "metode_racik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),313).toString()+"',"+
                    "pembayaran_akun_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),314).toString()+"',"+
                    "pengguna_obat_resep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),315).toString()+"',"+
                    "rekap_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),316).toString()+"',"+
                    "master_berkas_pegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),317).toString()+"',"+
                    "berkas_kepegawaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),318).toString()+"',"+
                    "riwayat_jabatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),319).toString()+"',"+
                    "riwayat_pendidikan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),320).toString()+"',"+
                    "riwayat_naik_gaji='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),321).toString()+"',"+
                    "kegiatan_ilmiah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),322).toString()+"',"+
                    "riwayat_penghargaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),323).toString()+"',"+
                    "riwayat_penelitian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),324).toString()+"',"+
                    "penerimaan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),325).toString()+"',"+
                    "bayar_pesan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),326).toString()+"',"+
                    "hutang_barang_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),327).toString()+"',"+
                    "rekap_pemesanan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),328).toString()+"',"+
                    "insiden_keselamatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),329).toString()+"',"+
                    "insiden_keselamatan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),330).toString()+"',"+
                    "grafik_ikp_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),331).toString()+"',"+
                    "grafik_ikp_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),332).toString()+"',"+
                    "grafik_ikp_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),333).toString()+"',"+
                    "riwayat_data_batch='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),334).toString()+"',"+
                    "grafik_ikp_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),335).toString()+"',"+
                    "grafik_ikp_dampak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),336).toString()+"',"+
                    "piutang_akun_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),337).toString()+"',"+
                    "grafik_kunjungan_per_agama='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),338).toString()+"',"+
                    "grafik_kunjungan_per_umur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),339).toString()+"',"+
                    "suku_bangsa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),340).toString()+"',"+
                    "bahasa_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),341).toString()+"',"+
                    "golongan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),342).toString()+"',"+
                    "satuan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),343).toString()+"',"+
                    "jabatan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),344).toString()+"',"+
                    "pangkat_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),345).toString()+"',"+
                    "golongan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),346).toString()+"',"+
                    "satuan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),347).toString()+"',"+
                    "jabatan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),348).toString()+"',"+
                    "pangkat_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),349).toString()+"',"+
                    "cacat_fisik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),350).toString()+"',"+
                    "grafik_kunjungan_suku='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),351).toString()+"',"+
                    "grafik_kunjungan_bahasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),352).toString()+"',"+
                    "booking_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),353).toString()+"',"+
                    "mapping_poli_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),354).toString()+"',"+
                    "grafik_kunjungan_per_cacat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),355).toString()+"',"+
                    "barang_cssd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),356).toString()+"',"+
                    "skdp_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),357).toString()+"',"+
                    "booking_registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),358).toString()+"',"+
                    "bpjs_cek_propinsi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),359).toString()+"',"+
                    "bpjs_cek_kabupaten='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),360).toString()+"',"+
                    "bpjs_cek_kecamatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),361).toString()+"',"+
                    "bpjs_cek_dokterdpjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),362).toString()+"',"+
                    "bpjs_cek_riwayat_rujukanrs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),363).toString()+"',"+
                    "bpjs_cek_tanggal_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),364).toString()+"',"+
                    "permintaan_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),365).toString()+"',"+
                    "permintaan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),366).toString()+"',"+
                    "surat_indeks='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),367).toString()+"',"+
                    "surat_map='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),368).toString()+"',"+
                    "surat_almari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),369).toString()+"',"+
                    "surat_rak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),370).toString()+"',"+
                    "surat_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),371).toString()+"',"+
                    "surat_klasifikasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),372).toString()+"',"+
                    "surat_status='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),373).toString()+"',"+
                    "surat_sifat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),374).toString()+"',"+
                    "surat_balas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),375).toString()+"',"+
                    "surat_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),376).toString()+"',"+
                    "pcare_cek_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),377).toString()+"',"+
                    "pcare_cek_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),378).toString()+"',"+
                    "pcare_cek_provider='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),379).toString()+"',"+
                    "pcare_cek_statuspulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),380).toString()+"',"+
                    "pcare_cek_spesialis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),381).toString()+"',"+
                    "pcare_cek_subspesialis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),382).toString()+"',"+
                    "pcare_cek_sarana='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),383).toString()+"',"+
                    "pcare_cek_khusus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),384).toString()+"',"+
                    "pcare_cek_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),385).toString()+"',"+
                    "pcare_cek_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),386).toString()+"',"+
                    "pcare_cek_faskessubspesialis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),387).toString()+"',"+
                    "pcare_cek_faskesalihrawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),388).toString()+"',"+
                    "pcare_cek_faskesthalasemia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),389).toString()+"',"+
                    "pcare_mapping_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),390).toString()+"',"+
                    "pcare_mapping_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),391).toString()+"',"+
                    "pcare_club_prolanis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),392).toString()+"',"+
                    "pcare_mapping_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),393).toString()+"',"+
                    "pcare_kegiatan_kelompok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),394).toString()+"',"+
                    "pcare_mapping_tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),395).toString()+"',"+
                    "pcare_peserta_kegiatan_kelompok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),396).toString()+"',"+
                    "sirkulasi_obat3='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),397).toString()+"',"+
                    "bridging_pcare_daftar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),398).toString()+"',"+
                    "pcare_mapping_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),399).toString()+"',"+
                    "ranap_per_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),400).toString()+"',"+
                    "penyakit_ranap_cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),401).toString()+"',"+
                    "anggota_militer_dirawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),402).toString()+"',"+
                    "set_input_parsial='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),403).toString()+"',"+
                    "lama_pelayanan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),404).toString()+"',"+
                    "lama_pelayanan_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),405).toString()+"',"+
                    "bpjs_cek_sep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),406).toString()+"',"+
                    "catatan_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),407).toString()+"',"+
                    "surat_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),408).toString()+"',"+
                    "kegiatan_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),409).toString()+"',"+
                    "stok_opname_logistik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),410).toString()+"',"+
                    "sirkulasi_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),411).toString()+"',"+
                    "rekap_lab_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),412).toString()+"',"+
                    "perujuk_lab_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),413).toString()+"',"+
                    "rekap_radiologi_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),414).toString()+"',"+
                    "perujuk_radiologi_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),415).toString()+"',"+
                    "jumlah_porsi_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),416).toString()+"',"+
                    "jumlah_macam_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),417).toString()+"',"+
                    "payment_point2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),418).toString()+"',"+
                    "pembayaran_akun_bayar2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),419).toString()+"',"+
                    "hapus_nota_salah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),420).toString()+"',"+
                    "pengkajian_askep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),421).toString()+"',"+
                    "hais_perbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),422).toString()+"',"+
                    "ppn_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),423).toString()+"',"+
                    "saldo_akun_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),424).toString()+"',"+
                    "display_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),425).toString()+"',"+
                    "sisrute_referensi_faskes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),426).toString()+"',"+
                    "sisrute_referensi_alasanrujuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),427).toString()+"',"+
                    "sisrute_referensi_diagnosa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),428).toString()+"',"+
                    "sisrute_rujukan_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),429).toString()+"',"+
                    "sisrute_rujukan_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),430).toString()+"',"+
                    "bpjs_cek_skdp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),431).toString()+"',"+
                    "data_batch='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),432).toString()+"',"+
                    "kunjungan_permintaan_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),433).toString()+"',"+
                    "kunjungan_permintaan_lab2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),434).toString()+"',"+
                    "kunjungan_permintaan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),435).toString()+"',"+
                    "kunjungan_permintaan_radiologi2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),436).toString()+"',"+
                    "pcare_pemberian_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),437).toString()+"',"+
                    "pcare_pemberian_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),438).toString()+"',"+
                    "pembayaran_akun_bayar3='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),439).toString()+"',"+
                    "password_asuransi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),440).toString()+"',"+
                    "kemenkes_sitt='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),441).toString()+"',"+
                    "siranap_ketersediaan_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),442).toString()+"',"+
                    "grafik_tb_periodelaporan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),443).toString()+"',"+
                    "grafik_tb_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),444).toString()+"',"+
                    "grafik_tb_riwayat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),445).toString()+"',"+
                    "grafik_tb_tipediagnosis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),446).toString()+"',"+
                    "grafik_tb_statushiv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),447).toString()+"',"+
                    "grafik_tb_skoringanak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),448).toString()+"',"+
                    "grafik_tb_konfirmasiskoring5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),449).toString()+"',"+
                    "grafik_tb_konfirmasiskoring6='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),450).toString()+"',"+
                    "grafik_tb_sumberobat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),451).toString()+"',"+
                    "grafik_tb_hasilakhirpengobatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),452).toString()+"',"+
                    "grafik_tb_hasilteshiv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),453).toString()+"',"+
                    "kadaluarsa_batch='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),454).toString()+"',"+
                    "sisa_stok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),455).toString()+"',"+
                    "obat_per_resep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),456).toString()+"',"+
                    "pemakaian_air_pdam='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),457).toString()+"',"+
                    "limbah_b3_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),458).toString()+"',"+
                    "grafik_air_pdam_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),459).toString()+"',"+
                    "grafik_air_pdam_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),460).toString()+"',"+
                    "grafik_limbahb3_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),461).toString()+"',"+
                    "grafik_limbahb3_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),462).toString()+"',"+
                    "limbah_domestik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),463).toString()+"',"+
                    "grafik_limbahdomestik_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),464).toString()+"',"+
                    "grafik_limbahdomestik_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),465).toString()+"',"+
                    "mutu_air_limbah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),466).toString()+"',"+
                    "pest_control='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),467).toString()+"',"+
                    "ruang_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),468).toString()+"',"+
                    "kategori_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),469).toString()+"',"+
                    "jenis_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),470).toString()+"',"+
                    "pengarang_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),471).toString()+"',"+
                    "penerbit_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),472).toString()+"',"+
                    "koleksi_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),473).toString()+"',"+
                    "inventaris_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),474).toString()+"',"+
                    "set_peminjaman_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),475).toString()+"',"+
                    "denda_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),476).toString()+"',"+
                    "anggota_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),477).toString()+"',"+
                    "peminjaman_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),478).toString()+"',"+
                    "bayar_denda_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),479).toString()+"',"+
                    "ebook_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),480).toString()+"',"+
                    "jenis_cidera_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),481).toString()+"',"+
                    "penyebab_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),482).toString()+"',"+
                    "jenis_luka_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),483).toString()+"',"+
                    "lokasi_kejadian_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),484).toString()+"',"+
                    "dampak_cidera_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),485).toString()+"',"+
                    "jenis_pekerjaan_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),486).toString()+"',"+
                    "bagian_tubuh_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),487).toString()+"',"+
                    "peristiwa_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),488).toString()+"',"+
                    "grafik_k3_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),489).toString()+"',"+
                    "grafik_k3_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),490).toString()+"',"+
                    "grafik_k3_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),491).toString()+"',"+
                    "grafik_k3_perjeniscidera='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),492).toString()+"',"+
                    "grafik_k3_perpenyebab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),493).toString()+"',"+
                    "grafik_k3_perjenisluka='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),494).toString()+"',"+
                    "grafik_k3_lokasikejadian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),495).toString()+"',"+
                    "grafik_k3_dampakcidera='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),496).toString()+"',"+
                    "grafik_k3_perjenispekerjaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),497).toString()+"',"+
                    "grafik_k3_perbagiantubuh='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),498).toString()+"',"+
                    "jenis_cidera_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),499).toString()+"',"+
                    "penyebab_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),500).toString()+"',"+
                    "jenis_luka_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),501).toString()+"',"+
                    "lokasi_kejadian_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),502).toString()+"',"+
                    "dampak_cidera_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),503).toString()+"',"+
                    "jenis_pekerjaan_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),504).toString()+"',"+
                    "bagian_tubuh_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),505).toString()+"',"+
                    "sekrining_rawat_jalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),506).toString()+"',"+
                    "bpjs_histori_pelayanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),507).toString()+"',"+
                    "rekap_mutasi_berkas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),508).toString()+"',"+
                    "skrining_ralan_pernapasan_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),509).toString()+"',"+
                    "pengajuan_barang_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),510).toString()+"',"+
                    "pengajuan_barang_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),511).toString()+"',"+
                    "grafik_kunjungan_ranapbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),512).toString()+"',"+
                    "grafik_kunjungan_ranaptanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),513).toString()+"',"+
                    "grafik_kunjungan_ranap_peruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),514).toString()+"',"+
                    "kunjungan_bangsal_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),515).toString()+"',"+
                    "grafik_jenjang_jabatanpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),516).toString()+"',"+
                    "grafik_bidangpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),517).toString()+"',"+
                    "grafik_departemenpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),518).toString()+"',"+
                    "grafik_pendidikanpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),519).toString()+"',"+
                    "grafik_sttswppegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),520).toString()+"',"+
                    "grafik_sttskerjapegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),521).toString()+"',"+
                    "grafik_sttspulangranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),522).toString()+"',"+
                    "kip_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),523).toString()+"',"+
                    "kip_pasien_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),524).toString()+"',"+
                    "bpjs_mapping_dokterdpjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),525).toString()+"',"+
                    "data_triase_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),526).toString()+"',"+
                    "master_triase_skala1='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),527).toString()+"',"+
                    "master_triase_skala2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),528).toString()+"',"+
                    "master_triase_skala3='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),529).toString()+"',"+
                    "master_triase_skala4='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),530).toString()+"',"+
                    "master_triase_skala5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),531).toString()+"',"+
                    "master_triase_pemeriksaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),532).toString()+"',"+
                    "master_triase_macamkasus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),533).toString()+"',"+
                    "rekap_permintaan_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),534).toString()+"',"+
                    "daftar_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),535).toString()+"',"+
                    "daftar_pasien_ranaptni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),536).toString()+"',"+
                    "pengajuan_asetinventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),537).toString()+"',"+
                    "item_apotek_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),538).toString()+"',"+
                    "item_apotek_kategori='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),539).toString()+"',"+
                    "item_apotek_golongan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),540).toString()+"',"+
                    "item_apotek_industrifarmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),541).toString()+"',"+
                    "10_obat_terbanyak_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),542).toString()+"',"+
                    "grafik_pengajuan_aset_urgensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),543).toString()+"',"+
                    "grafik_pengajuan_aset_status='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),544).toString()+"',"+
                    "grafik_pengajuan_aset_departemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),545).toString()+"',"+
                    "rekap_pengajuan_aset_departemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),546).toString()+"',"+
                    "grafik_kelompok_jabatanpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),547).toString()+"',"+
                    "grafik_resiko_kerjapegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),548).toString()+"',"+
                    "grafik_emergency_indexpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),549).toString()+"',"+
                    "grafik_inventaris_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),550).toString()+"',"+
                    "harian_HAIs2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),551).toString()+"',"+
                    "grafik_inventaris_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),552).toString()+"',"+
                    "data_resume_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),553).toString()+"',"+
                    "perkiraan_biaya_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),554).toString()+"',"+
                    "rekap_obat_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),555).toString()+"',"+
                    "rekap_obat_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),556).toString()+"',"+
                    "permintaan_perbaikan_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),557).toString()+"',"+
                    "grafik_HAIs_pasienbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),558).toString()+"',"+
                    "grafik_HAIs_pasienbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),559).toString()+"',"+
                    "grafik_HAIs_laju_vap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),560).toString()+"',"+
                    "grafik_HAIs_laju_iad='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),561).toString()+"',"+
                    "grafik_HAIs_laju_pleb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),562).toString()+"',"+
                    "grafik_HAIs_laju_isk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),563).toString()+"',"+
                    "grafik_HAIs_laju_ilo='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),564).toString()+"',"+
                    "grafik_HAIs_laju_hap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),565).toString()+"',"+
                    "inhealth_mapping_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),566).toString()+"',"+
                    "inhealth_mapping_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),567).toString()+"',"+
                    "inhealth_mapping_tindakan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),568).toString()+"',"+
                    "inhealth_mapping_tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),569).toString()+"',"+
                    "inhealth_mapping_tindakan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),570).toString()+"',"+
                    "inhealth_mapping_tindakan_laborat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),571).toString()+"',"+
                    "inhealth_mapping_tindakan_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),572).toString()+"',"+
                    "hibah_obat_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),573).toString()+"',"+
                    "asal_hibah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),574).toString()+"',"+
                    "asuhan_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),575).toString()+"',"+
                    "inhealth_kirim_tagihan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),576).toString()+"',"+
                    "sirkulasi_obat4='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),577).toString()+"',"+
                    "sirkulasi_obat5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),578).toString()+"',"+
                    "sirkulasi_non_medis2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),579).toString()+"',"+
                    "monitoring_asuhan_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),580).toString()+"',"+
                    "penerimaan_obat_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),581).toString()+"',"+
                    "rekap_kunjungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),582).toString()+"',"+
                    "surat_sakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),583).toString()+"',"+
                    "penilaian_awal_keperawatan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),584).toString()+"',"+
                    "permintaan_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),585).toString()+"',"+
                    "master_masalah_keperawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),586).toString()+"',"+
                    "pengajuan_cuti='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),587).toString()+"',"+
                    "kedatangan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),588).toString()+"',"+
                    "utd_pendonor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),589).toString()+"',"+
                    "toko_suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),590).toString()+"',"+
                    "toko_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),591).toString()+"',"+
                    "toko_set_harga='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),592).toString()+"',"+
                    "toko_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),593).toString()+"',"+
                    "penagihan_piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),594).toString()+"',"+
                    "akun_penagihan_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),595).toString()+"',"+
                    "stok_opname_toko='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),596).toString()+"',"+
                    "toko_riwayat_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),597).toString()+"',"+
                    "toko_surat_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),598).toString()+"',"+
                    "toko_pengajuan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),599).toString()+"',"+
                    "toko_penerimaan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),600).toString()+"',"+
                    "toko_pengadaan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),601).toString()+"',"+
                    "toko_hutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),602).toString()+"',"+
                    "toko_bayar_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),603).toString()+"',"+
                    "toko_member='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),604).toString()+"',"+
                    "toko_penjualan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),605).toString()+"',"+
                    "registrasi_poli_per_tanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),606).toString()+"',"+
                    "toko_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),607).toString()+"',"+
                    "toko_retur_beli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),608).toString()+"',"+
                    "ipsrs_returbeli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),609).toString()+"',"+
                    "ipsrs_riwayat_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),610).toString()+"',"+
                    "pasien_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),611).toString()+"',"+
                    "toko_pendapatan_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),612).toString()+"',"+
                    "diagnosa_pasien_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),613).toString()+"',"+
                    "perawatan_pasien_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),614).toString()+"',"+
                    "penilaian_awal_keperawatan_gigi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),615).toString()+"',"+
                    "master_masalah_keperawatan_gigi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),616).toString()+"',"+
                    "toko_bayar_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),617).toString()+"',"+
                    "toko_piutang_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),618).toString()+"',"+
                    "toko_penjualan_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),619).toString()+"',"+
                    "deteksi_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),620).toString()+"',"+
                    "penilaian_awal_keperawatan_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),621).toString()+"',"+
                    "pengumuman_epasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),622).toString()+"',"+
                    "surat_hamil='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),623).toString()+"',"+
                    "set_tarif_online='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),624).toString()+"',"+
                    "booking_periksa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),625).toString()+"',"+
                    "toko_sirkulasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),626).toString()+"',"+
                    "toko_retur_jual='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),627).toString()+"',"+
                    "toko_retur_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),628).toString()+"',"+
                    "toko_sirkulasi2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),629).toString()+"',"+
                    "toko_keuntungan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),630).toString()+"',"+
                    "zis_pengeluaran_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),631).toString()+"',"+
                    "zis_penghasilan_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),632).toString()+"',"+
                    "zis_ukuran_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),633).toString()+"',"+
                    "zis_dinding_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),634).toString()+"',"+
                    "zis_lantai_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),635).toString()+"',"+
                    "zis_atap_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),636).toString()+"',"+
                    "zis_kepemilikan_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),637).toString()+"',"+
                    "zis_kamar_mandi_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),638).toString()+"',"+
                    "zis_dapur_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),639).toString()+"',"+
                    "zis_kursi_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),640).toString()+"',"+
                    "zis_kategori_phbs_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),641).toString()+"',"+
                    "zis_elektronik_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),642).toString()+"',"+
                    "zis_ternak_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),643).toString()+"',"+
                    "zis_jenis_simpanan_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),644).toString()+"',"+
                    "penilaian_awal_keperawatan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),645).toString()+"',"+
                    "zis_kategori_asnaf_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),646).toString()+"',"+
                    "master_masalah_keperawatan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),647).toString()+"',"+
                    "master_imunisasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),648).toString()+"',"+
                    "zis_patologis_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),649).toString()+"',"+
                    "pcare_cek_kartu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),650).toString()+"',"+
                    "surat_bebas_narkoba='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),651).toString()+"',"+
                    "surat_keterangan_covid='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),652).toString()+"',"+
                    "pemakaian_air_tanah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),653).toString()+"',"+
                    "grafik_air_tanah_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),654).toString()+"',"+
                    "grafik_air_tanah_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),655).toString()+"',"+
                    "lama_pelayanan_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),656).toString()+"',"+
                    "hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),657).toString()+"',"+
                    "laporan_tahunan_irj='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),658).toString()+"',"+
                    "grafik_harian_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),659).toString()+"',"+
                    "grafik_bulanan_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),660).toString()+"',"+
                    "grafik_tahunan_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),661).toString()+"',"+
                    "grafik_bulanan_meninggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),662).toString()+"',"+
                    "perbaikan_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),663).toString()+"',"+
                    "surat_cuti_hamil='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),664).toString()+"',"+
                    "permintaan_stok_obat_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),665).toString()+"',"+
                    "pemeliharaan_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),666).toString()+"',"+
                    "klasifikasi_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),667).toString()+"',"+
                    "bulanan_klasifikasi_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),668).toString()+"',"+
                    "harian_klasifikasi_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),669).toString()+"',"+
                    "klasifikasi_pasien_perbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),670).toString()+"',"+
                    "soap_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),671).toString()+"',"+
                    "klaim_rawat_jalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),672).toString()+"',"+
                    "skrining_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),673).toString()+"',"+
                    "lama_penyiapan_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),674).toString()+"',"+
                    "dosis_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),675).toString()+"',"+
                    "demografi_umur_kunjungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),676).toString()+"',"+
                    "jam_diet_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),677).toString()+"',"+
                    "rvu_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),678).toString()+"',"+
                    "verifikasi_penerimaan_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),679).toString()+"',"+
                    "verifikasi_penerimaan_logistik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),680).toString()+"',"+
                    "pemeriksaan_lab_pa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),681).toString()+"',"+
                    "ringkasan_pengajuan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),682).toString()+"',"+
                    "ringkasan_pemesanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),683).toString()+"',"+
                    "ringkasan_pengadaan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),684).toString()+"',"+
                    "ringkasan_penerimaan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),685).toString()+"',"+
                    "ringkasan_hibah_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),686).toString()+"',"+
                    "ringkasan_penjualan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),687).toString()+"',"+
                    "ringkasan_beri_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),688).toString()+"',"+
                    "ringkasan_piutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),689).toString()+"',"+
                    "ringkasan_stok_keluar_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),690).toString()+"',"+
                    "ringkasan_retur_suplier_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),691).toString()+"',"+
                    "ringkasan_retur_pembeli_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),692).toString()+"',"+
                    "penilaian_awal_keperawatan_ranapkebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),693).toString()+"',"+
                    "ringkasan_pengajuan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),694).toString()+"',"+
                    "ringkasan_pemesanan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),695).toString()+"',"+
                    "ringkasan_pengadaan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),696).toString()+"',"+
                    "ringkasan_penerimaan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),697).toString()+"',"+
                    "ringkasan_stokkeluar_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),698).toString()+"',"+
                    "ringkasan_returbeli_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),699).toString()+"',"+
                    "omset_penerimaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),700).toString()+"',"+
                    "validasi_penagihan_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),701).toString()+"',"+
                    "permintaan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),702).toString()+"',"+
                    "bpjs_diagnosa_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),703).toString()+"',"+
                    "bpjs_obat_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),704).toString()+"',"+
                    "bpjs_surat_kontrol='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),705).toString()+"',"+
                    "penggunaan_bhp_ok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),706).toString()+"',"+
                    "surat_keterangan_rawat_inap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),707).toString()+"',"+
                    "surat_keterangan_sehat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),708).toString()+"',"+
                    "pendapatan_per_carabayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),709).toString()+"',"+
                    "akun_host_to_host_bank_jateng='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),710).toString()+"',"+
                    "pembayaran_bank_jateng='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),711).toString()+"',"+
                    "bpjs_surat_pri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),712).toString()+"',"+
                    "ringkasan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),713).toString()+"',"+
                    "lama_pelayanan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),714).toString()+"',"+
                    "surat_sakit_pihak_2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),715).toString()+"',"+
                    "tagihan_hutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),716).toString()+"',"+
                    "referensi_mobilejkn_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),717).toString()+"',"+
                    "batal_pendaftaran_mobilejkn_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),718).toString()+"',"+
                    "lama_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),719).toString()+"',"+
                    "grafik_inventaris_kategori='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),720).toString()+"',"+
                    "grafik_inventaris_merk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),721).toString()+"',"+
                    "grafik_inventaris_produsen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),722).toString()+"',"+
                    "pengembalian_deposit_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),723).toString()+"',"+
                    "validasi_tagihan_hutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),724).toString()+"',"+
                    "piutang_obat_belum_lunas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),725).toString()+"',"+
                    "integrasi_briapi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),726).toString()+"',"+
                    "pengadaan_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),727).toString()+"',"+
                    "akun_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),728).toString()+"',"+
                    "suplier_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),729).toString()+"',"+
                    "penerimaan_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),730).toString()+"',"+
                    "bayar_pemesanan_iventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),731).toString()+"',"+
                    "hutang_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),732).toString()+"',"+
                    "hibah_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),733).toString()+"',"+
                    "titip_faktur_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),734).toString()+"',"+
                    "validasi_tagihan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),735).toString()+"',"+
                    "titip_faktur_aset='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),736).toString()+"',"+
                    "validasi_tagihan_aset='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),737).toString()+"',"+
                    "hibah_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),738).toString()+"',"+
                    "pcare_alasan_tacc='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),739).toString()+"',"+
                    "resep_luar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),740).toString()+"',"+
                    "surat_bebas_tbc='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),741).toString()+"',"+
                    "surat_buta_warna='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),742).toString()+"',"+
                    "surat_bebas_tato='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),743).toString()+"',"+
                    "surat_kewaspadaan_kesehatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),744).toString()+"',"+
                    "grafik_porsidiet_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),745).toString()+"',"+
                    "grafik_porsidiet_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),746).toString()+"',"+
                    "grafik_porsidiet_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),747).toString()+"',"+
                    "grafik_porsidiet_perbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),748).toString()+"',"+
                    "penilaian_awal_medis_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),749).toString()+"',"+
                    "master_masalah_keperawatan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),750).toString()+"',"+
                    "penilaian_awal_keperawatan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),751).toString()+"',"+
                    "penilaian_awal_medis_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),752).toString()+"',"+
                    "penilaian_awal_medis_ranap_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),753).toString()+"',"+
                    "penilaian_awal_medis_ralan_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),754).toString()+"',"+
                    "penilaian_awal_medis_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),755).toString()+"',"+
                    "penilaian_awal_medis_ralan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),756).toString()+"',"+
                    "bpjs_referensi_poli_hfis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),757).toString()+"',"+
                    "bpjs_referensi_dokter_hfis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),758).toString()+"',"+
                    "bpjs_referensi_jadwal_hfis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),759).toString()+"',"+
                    "penilaian_fisioterapi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),760).toString()+"',"+
                    "bpjs_program_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),761).toString()+"',"+
                    "bpjs_suplesi_jasaraharja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),762).toString()+"',"+
                    "bpjs_data_induk_kecelakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),763).toString()+"',"+
                    "bpjs_sep_internal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),764).toString()+"',"+
                    "bpjs_klaim_jasa_raharja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),765).toString()+"',"+
                    "bpjs_daftar_finger_print='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),766).toString()+"',"+
                    "bpjs_rujukan_khusus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),767).toString()+"',"+
                    "pemeliharaan_gedung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),768).toString()+"',"+
                    "grafik_perbaikan_inventaris_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),769).toString()+"',"+
                    "grafik_perbaikan_inventaris_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),770).toString()+"',"+
                    "grafik_perbaikan_inventaris_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),771).toString()+"',"+
                    "grafik_perbaikan_inventaris_perpelaksana_status='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),772).toString()+"',"+
                    "penilaian_mcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),773).toString()+"',"+
                    "peminjam_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),774).toString()+"',"+
                    "piutang_lainlain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),775).toString()+"',"+
                    "cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),776).toString()+"',"+
                    "audit_kepatuhan_apd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),777).toString()+"',"+
                    "bpjs_task_id='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),778).toString()+"',"+
                    "bayar_piutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),779).toString()+"',"+
                    "pembayaran_akun_bayar4='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),780).toString()+"',"+
                    "stok_akhir_farmasi_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),781).toString()+"',"+
                    "riwayat_kamar_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),782).toString()+"',"+
                    "uji_fungsi_kfr='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),783).toString()+"',"+
                    "hapus_berkas_digital_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),784).toString()+"',"+
                    "kategori_pengeluaran_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),785).toString()+"',"+
                    "kategori_pemasukan_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),786).toString()+"',"+
                    "pembayaran_akun_bayar5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),787).toString()+"',"+
                    "ruang_ok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),788).toString()+"',"+
                    "telaah_resep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),789).toString()+"',"+
                    "jasa_tindakan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),790).toString()+"',"+
                    "permintaan_resep_pulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),791).toString()+"',"+
                    "rekap_jm_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),792).toString()+"',"+
                    "status_data_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),793).toString()+"',"+
                    "ubah_petugas_lab_pk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),794).toString()+"',"+
                    "ubah_petugas_lab_pa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),795).toString()+"',"+
                    "ubah_petugas_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),796).toString()+"',"+
                    "gabung_norawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),797).toString()+"',"+
                    "gabung_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),798).toString()+"',"+
                    "ringkasan_biaya_obat_pasien_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),799).toString()+"',"+
                    "master_masalah_keperawatan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),800).toString()+"',"+
                    "penilaian_awal_keperawatan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),801).toString()+"',"+
                    "bpjs_referensi_dpho_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),802).toString()+"',"+
                    "bpjs_referensi_poli_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),803).toString()+"',"+
                    "bayar_jm_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),804).toString()+"',"+
                    "bpjs_referensi_faskes_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),805).toString()+"',"+
                    "bpjs_referensi_spesialistik_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),806).toString()+"',"+
                    "pembayaran_briva='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),807).toString()+"',"+
                    "penilaian_awal_keperawatan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),808).toString()+"',"+
                    "nilai_penerimaan_vendor_farmasi_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),809).toString()+"',"+
                    "akun_bayar_hutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),810).toString()+"',"+
                    "master_rencana_keperawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),811).toString()+"',"+
                    "laporan_tahunan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),812).toString()+"',"+
                    "obat_bhp_tidakbergerak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),813).toString()+"',"+
                    "ringkasan_hutang_vendor_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),814).toString()+"',"+
                    "nilai_penerimaan_vendor_nonmedis_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),815).toString()+"',"+
                    "ringkasan_hutang_vendor_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),816).toString()+"',"+
                    "master_rencana_keperawatan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),817).toString()+"',"+
                    "anggota_polri_dirawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),818).toString()+"',"+
                    "daftar_pasien_ranap_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),819).toString()+"',"+
                    "soap_ralan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),820).toString()+"',"+
                    "soap_ranap_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),821).toString()+"',"+
                    "laporan_penyakit_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),822).toString()+"',"+
                    "jumlah_pengunjung_ralan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),823).toString()+"',"+
                    "catatan_observasi_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),824).toString()+"',"+
                    "catatan_observasi_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),825).toString()+"',"+
                    "catatan_observasi_ranap_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),826).toString()+"',"+
                    "catatan_observasi_ranap_postpartum='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),827).toString()+"',"+
                    "penilaian_awal_medis_ralan_tht='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),828).toString()+"',"+
                    "penilaian_psikologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),829).toString()+"',"+
                    "audit_cuci_tangan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),830).toString()+"',"+
                    "audit_pembuangan_limbah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),831).toString()+"',"+
                    "ruang_audit_kepatuhan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),832).toString()+"',"+
                    "audit_pembuangan_benda_tajam='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),833).toString()+"',"+
                    "audit_penanganan_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),834).toString()+"',"+
                    "audit_pengelolaan_linen_kotor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),835).toString()+"',"+
                    "audit_penempatan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),836).toString()+"',"+
                    "audit_kamar_jenazah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),837).toString()+"',"+
                    "audit_bundle_iadp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),838).toString()+"',"+
                    "audit_bundle_ido='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),839).toString()+"',"+
                    "audit_fasilitas_kebersihan_tangan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),840).toString()+"',"+
                    "audit_fasilitas_apd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),841).toString()+"',"+
                    "audit_pembuangan_limbah_cair_infeksius='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),842).toString()+"',"+
                    "audit_sterilisasi_alat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),843).toString()+"',"+
                    "penilaian_awal_medis_ralan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),844).toString()+"',"+
                    "persetujuan_penolakan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),845).toString()+"',"+
                    "audit_bundle_isk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),846).toString()+"',"+
                    "audit_bundle_plabsi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),847).toString()+"',"+
                    "audit_bundle_vap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),848).toString()+"',"+
                    "akun_host_to_host_bank_papua='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),849).toString()+"',"+
                    "pembayaran_bank_papua='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),850).toString()+"',"+
                    "penilaian_awal_medis_ralan_penyakit_dalam='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),851).toString()+"',"+
                    "penilaian_awal_medis_ralan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),852).toString()+"',"+
                    "penilaian_awal_medis_ralan_neurologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),853).toString()+"',"+
                    "sirkulasi_obat6='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),854).toString()+"',"+
                    "penilaian_awal_medis_ralan_orthopedi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),855).toString()+"',"+
                    "penilaian_awal_medis_ralan_bedah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),856).toString()+"',"+
                    "integrasi_khanza_health_services='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),857).toString()+"',"+
                    "soap_ralan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),858).toString()+"',"+
                    "soap_ranap_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),859).toString()+"',"+
                    "jumlah_pengunjung_ralan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),860).toString()+"',"+
                    "laporan_penyakit_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),861).toString()+"',"+
                    "catatan_keperawatan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),862).toString()+"',"+
                    "master_rencana_keperawatan_gigi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),863).toString()+"',"+
                    "master_rencana_keperawatan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),864).toString()+"',"+
                    "master_rencana_keperawatan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),865).toString()+"',"+
                    "master_masalah_keperawatan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),866).toString()+"',"+
                    "master_rencana_keperawatan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),867).toString()+"',"+
                    "penilaian_awal_keperawatan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),868).toString()+"',"+
                    "pemantauan_pews_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),869).toString()+"',"+
                    "surat_pulang_atas_permintaan_sendiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),870).toString()+"',"+
                    "template_hasil_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),871).toString()+"',"+
                    "laporan_bulanan_irj='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),872).toString()+"',"+
                    "template_pemeriksaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),873).toString()+"',"+
                    "pemeriksaan_lab_mb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),874).toString()+"',"+
                    "ubah_petugas_lab_mb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),875).toString()+"',"+
                    "penilaian_pre_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),876).toString()+"',"+
                    "penilaian_pre_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),877).toString()+"',"+
                    "perencanaan_pemulangan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),878).toString()+"',"+
                    "penilaian_lanjutan_resiko_jatuh_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),879).toString()+"',"+
                    "penilaian_lanjutan_resiko_jatuh_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),880).toString()+"',"+
                    "penilaian_awal_medis_ralan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),881).toString()+"',"+
                    "penilaian_tambahan_pasien_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),882).toString()+"',"+
                    "skrining_nutrisi_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),883).toString()+"',"+
                    "skrining_nutrisi_lansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),884).toString()+"',"+
                    "hasil_pemeriksaan_usg='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),885).toString()+"',"+
                    "skrining_nutrisi_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),886).toString()+"',"+
                    "akun_host_to_host_bank_jabar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),887).toString()+"',"+
                    "pembayaran_bank_jabar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),888).toString()+"',"+
                    "surat_pernyataan_pasien_umum='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),889).toString()+"',"+
                    "konseling_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),890).toString()+"',"+
                    "pelayanan_informasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),891).toString()+"',"+
                    "jawaban_pio_apoteker='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),892).toString()+"',"+
                    "surat_persetujuan_umum='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),893).toString()+"',"+
                    "transfer_pasien_antar_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),894).toString()+"',"+
                    "satu_sehat_referensi_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),895).toString()+"',"+
                    "satu_sehat_referensi_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),896).toString()+"',"+
                    "satu_sehat_mapping_departemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),897).toString()+"',"+
                    "satu_sehat_mapping_lokasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),898).toString()+"',"+
                    "satu_sehat_kirim_encounter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),899).toString()+"',"+
                    "catatan_cek_gds='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),900).toString()+"',"+
                    "satu_sehat_kirim_condition='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),901).toString()+"',"+
                    "checklist_pre_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),902).toString()+"',"+
                    "satu_sehat_kirim_observationttv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),903).toString()+"',"+
                    "signin_sebelum_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),904).toString()+"',"+
                    "satu_sehat_kirim_procedure='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),905).toString()+"',"+
                    "operasi_per_bulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),906).toString()+"',"+
                    "timeout_sebelum_insisi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),907).toString()+"',"+
                    "signout_sebelum_menutup_luka='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),908).toString()+"',"+
                    "dapur_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),909).toString()+"',"+
                    "dapur_opname='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),910).toString()+"',"+
                    "satu_sehat_mapping_vaksin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),911).toString()+"',"+
                    "dapur_suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),912).toString()+"',"+
                    "satu_sehat_kirim_Immunization='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),913).toString()+"',"+
                    "checklist_post_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),914).toString()+"',"+
                    "dapur_pembelian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),915).toString()+"',"+
                    "dapur_stok_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),916).toString()+"',"+
                    "dapur_riwayat_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),917).toString()+"',"+
                    "permintaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),918).toString()+"',"+
                    "rekonsiliasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),919).toString()+"',"+
                    "biaya_pengadaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),920).toString()+"',"+
                    "rekap_pengadaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),921).toString()+"',"+
                    "kesling_limbah_b3medis_cair='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),922).toString()+"',"+
                    "grafik_limbahb3cair_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),923).toString()+"',"+
                    "grafik_limbahb3cair_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),924).toString()+"',"+
                    "rekap_biaya_registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),925).toString()+"',"+
                    "konfirmasi_rekonsiliasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),926).toString()+"',"+
                    "satu_sehat_kirim_clinicalimpression='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),927).toString()+"',"+
                    "penilaian_pasien_terminal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),928).toString()+"',"+
                    "surat_persetujuan_rawat_inap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),929).toString()+"',"+
                    "monitoring_reaksi_tranfusi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),930).toString()+"',"+
                    "penilaian_korban_kekerasan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),931).toString()+"',"+
                    "penilaian_lanjutan_resiko_jatuh_lansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),932).toString()+"',"+
                    "penilaian_pasien_penyakit_menular='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),933).toString()+"',"+
                    "mpp_skrining='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),934).toString()+"',"+
                    "edukasi_pasien_keluarga_rj='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),935).toString()+"',"+
                    "pemantauan_pews_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),936).toString()+"',"+
                    "penilaian_tambahan_bunuh_diri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),937).toString()+"',"+
                    "bpjs_antrean_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),938).toString()+"',"+
                    "penilaian_tambahan_perilaku_kekerasan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),939).toString()+"',"+
                    "penilaian_tambahan_beresiko_melarikan_diri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),940).toString()+"',"+
                    "persetujuan_penundaan_pelayanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),941).toString()+"',"+
                    "sisa_diet_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),942).toString()+"',"+
                    "penilaian_awal_medis_ralan_bedah_mulut='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),943).toString()+"',"+
                    "penilaian_pasien_keracunan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),944).toString()+"',"+
                    "pemantauan_meows_obstetri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),945).toString()+"',"+
                    "catatan_adime_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),946).toString()+"',"+
                    "pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),947).toString()+"',"+
                    "penilaian_awal_keperawatan_ralan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),948).toString()+"',"+
                    "master_masalah_keperawatan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),949).toString()+"',"+
                    "master_rencana_keperawatan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),950).toString()+"',"+
                    "checklist_kriteria_masuk_hcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),951).toString()+"',"+
                    "checklist_kriteria_keluar_hcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),952).toString()+"',"+
                    "penilaian_risiko_dekubitus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),953).toString()+"',"+
                    "master_menolak_anjuran_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),954).toString()+"',"+
                    "penolakan_anjuran_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),955).toString()+"',"+
                    "laporan_tahunan_penolakan_anjuran_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),956).toString()+"',"+
                    "template_laporan_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),957).toString()+"',"+
                    "hasil_tindakan_eswl='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),958).toString()+"',"+
                    "checklist_kriteria_masuk_icu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),959).toString()+"',"+
                    "checklist_kriteria_keluar_icu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),960).toString()+"',"+
                    "akses_dokter_lain_rawat_jalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),961).toString()+"',"+
                    "follow_up_dbd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),962).toString()+"',"+
                    "penilaian_risiko_jatuh_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),963).toString()+"',"+
                    "persetujuan_pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),964).toString()+"',"+
                    "pemeriksaan_fisik_ralan_per_penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),965).toString()+"',"+
                    "penilaian_lanjutan_resiko_jatuh_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),966).toString()+"',"+
                    "pemantauan_ews_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),967).toString()+"',"+
                    "validasi_persetujuan_pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),968).toString()+"',"+
                    "riwayat_perawatan_icare_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),969).toString()+"',"+
                    "rekap_pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),970).toString()+"',"+
                    "penilaian_awal_medis_ralan_kulit_kelamin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),971).toString()+"',"+
                    "akun_host_to_host_bank_mandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),972).toString()+"',"+
                    "penilaian_medis_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),973).toString()+"',"+
                    "penilaian_level_kecemasan_ranap_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),974).toString()+"',"+
                    "penilaian_lanjutan_resiko_jatuh_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),975).toString()+"',"+
                    "penilaian_lanjutan_skrining_fungsional='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),976).toString()+"',"+
                    "penilaian_medis_ralan_rehab_medik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),977).toString()+"',"+
                    "laporan_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),978).toString()+"',"+
                    "template_persetujuan_penolakan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),979).toString()+"',"+
                    "penilaian_medis_ralan_gawat_darurat_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),980).toString()+"',"+
                    "bpjs_referensi_setting_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),981).toString()+"',"+
                    "bpjs_referensi_obat_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),982).toString()+"',"+
                    "bpjs_mapping_obat_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),983).toString()+"',"+
                    "pembayaran_bank_mandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),984).toString()+"',"+
                    "penilaian_ulang_nyeri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),985).toString()+"',"+
                    "penilaian_terapi_wicara='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),986).toString()+"',"+
                    "bpjs_obat_23hari_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),987).toString()+"',"+
                    "pengkajian_restrain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),988).toString()+"',"+
                    "bpjs_kunjungan_sep_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),989).toString()+"',"+
                    "bpjs_monitoring_klaim_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),990).toString()+"',"+
                    "bpjs_daftar_pelayanan_obat_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),991).toString()+"',"+
                    "penilaian_awal_medis_ralan_paru='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),992).toString()+"',"+
                    "catatan_keperawatan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),993).toString()+"',"+
                    "catatan_persalinan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),994).toString()+"',"+
                    "skor_aldrette_pasca_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),995).toString()+"',"+
                    "skor_steward_pasca_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),996).toString()+"',"+
                    "skor_bromage_pasca_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),997).toString()+"',"+
                    "penilaian_pre_induksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),998).toString()+"',"+
                    "hasil_usg_urologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),999).toString()+"',"+
                    "hasil_usg_gynecologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1000).toString()+"',"+
                    "hasil_pemeriksaan_ekg='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1001).toString()+"',"+
                    "hapus_edit_sep_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1002).toString()+"',"+
                    "satu_sehat_kirim_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1003).toString()+"',"+
                    "satu_sehat_mapping_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1004).toString()+"',"+
                    "dapur_ringkasan_pembelian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1005).toString()+"',"+
                    "satu_sehat_kirim_medication='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1006).toString()+"',"+
                    "satu_sehat_kirim_medicationrequest='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1007).toString()+"',"+
                    "penatalaksanaan_terapi_okupasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1008).toString()+"',"+
                    "satu_sehat_kirim_medicationdispense='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1009).toString()+"',"+
                    "hasil_usg_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1010).toString()+"',"+
                    "hasil_endoskopi_faring_laring='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1011).toString()+"',"+
                    "satu_sehat_mapping_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1012).toString()+"',"+
                    "satu_sehat_kirim_servicerequest_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1013).toString()+"',"+
                    "hasil_endoskopi_hidung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1014).toString()+"',"+
                    "satu_sehat_kirim_specimen_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1015).toString()+"',"+
                    "master_masalah_keperawatan_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1016).toString()+"',"+
                    "master_rencana_keperawatan_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1017).toString()+"',"+
                    "penilaian_awal_keperawatan_ranap_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1018).toString()+"',"+
                    "satu_sehat_kirim_observation_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1019).toString()+"',"+
                    "satu_sehat_kirim_diagnosticreport_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1020).toString()+"',"+
                    "hasil_endoskopi_telinga='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1021).toString()+"',"+
                    "satu_sehat_mapping_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1022).toString()+"',"+
                    "satu_sehat_kirim_servicerequest_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1023).toString()+"',"+
                    "satu_sehat_kirim_servicerequest_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1024).toString()+"',"+
                    "satu_sehat_kirim_specimen_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1025).toString()+"',"+
                    "satu_sehat_kirim_specimen_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1026).toString()+"',"+
                    "satu_sehat_kirim_observation_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1027).toString()+"',"+
                    "satu_sehat_kirim_observation_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1028).toString()+"',"+
                    "satu_sehat_kirim_diagnosticreport_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1029).toString()+"',"+
                    "satu_sehat_kirim_diagnosticreport_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1030).toString()+"',"+
                    "kepatuhan_kelengkapan_keselamatan_bedah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1031).toString()+"',"+
                    "nilai_piutang_perjenis_bayar_per_bulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1032).toString()+"',"+
                    "ringkasan_piutang_jenis_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1033).toString()+"',"+
                    "penilaian_pasien_imunitas_rendah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1034).toString()+"',"+
                    "balance_cairan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1035).toString()+"',"+
                    "catatan_observasi_chbp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1036).toString()+"',"+
                    "catatan_observasi_induksi_persalinan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1037).toString()+"',"+
                    "skp_kategori_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1038).toString()+"',"+
                    "skp_kriteria_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1039).toString()+"',"+
                    "skp_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1040).toString()+"',"+
                    "referensi_poli_mobilejknfktp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1041).toString()+"',"+
                    "referensi_dokter_mobilejknfktp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1042).toString()+"',"+
                    "skp_rekapitulasi_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1043).toString()+"',"+
                    "pembayaran_pihak_ke3_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1044).toString()+"',"+
                    "metode_pembayaran_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1045).toString()+"',"+
                    "bank_tujuan_transfer_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1046).toString()+"',"+
                    "kodetransaksi_tujuan_transfer_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1047).toString()+"',"+
                    "konsultasi_medik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1048).toString()+"',"+
                    "jawaban_konsultasi_medik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1049).toString()+"',"+
                    "pcare_cek_alergi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1050).toString()+"',"+
                    "pcare_cek_prognosa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1051).toString()+"',"+
                    "data_sasaran_usiaproduktif='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1052).toString()+"',"+
                    "data_sasaran_usialansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1053).toString()+"',"+
                    "skrining_perilaku_merokok_sekolah_remaja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1054).toString()+"',"+
                    "skrining_kekerasan_pada_perempuan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1055).toString()+"',"+
                    "skrining_obesitas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1056).toString()+"',"+
                    "skrining_risiko_kanker_payudara='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1057).toString()+"',"+
                    "skrining_risiko_kanker_paru='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1058).toString()+"',"+
                    "skrining_tbc='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1059).toString()+"',"+
                    "skrining_kesehatan_gigi_mulut_remaja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1060).toString()+"',"+
                    "penilaian_awal_keperawatan_ranap_bayi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1061).toString()+"',"+
                    "booking_mcu_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1062).toString()+"',"+
                    "catatan_observasi_restrain_nonfarma='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1063).toString()+"',"+
                    "catatan_observasi_ventilator='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1064).toString()+"',"+
                    "catatan_anestesi_sedasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1065).toString()+"',"+
                    "skrining_puma='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1066).toString()+"',"+
                    "satu_sehat_kirim_careplan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1067).toString()+"',"+
                    "satu_sehat_kirim_medicationstatement='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1068).toString()+"',"+
                    "skrining_adiksi_nikotin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1069).toString()+"',"+
                    "skrining_thalassemia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1070).toString()+"',"+
                    "skrining_instrumen_sdq='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1071).toString()+"',"+
                    "skrining_instrumen_srq='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1072).toString()+"',"+
                    "checklist_pemberian_fibrinolitik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1073).toString()+"',"+
                    "skrining_kanker_kolorektal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1074).toString()+"',"+
                    "dapur_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1075).toString()+"',"+
                    "bayar_pesan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1076).toString()+"',"+
                    "hutang_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1077).toString()+"',"+
                    "titip_faktur_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1078).toString()+"',"+
                    "validasi_tagihan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1079).toString()+"',"+
                    "surat_pemesanan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1080).toString()+"',"+
                    "pengajuan_barang_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1081).toString()+"',"+
                    "dapur_returbeli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1082).toString()+"',"+
                    "hibah_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1083).toString()+"',"+
                    "ringkasan_penerimaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1084).toString()+"',"+
                    "ringkasan_pengajuan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1085).toString()+"',"+
                    "ringkasan_pemesanan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1086).toString()+"',"+
                    "ringkasan_returbeli_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1087).toString()+"',"+
                    "ringkasan_stokkeluar_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1088).toString()+"',"+
                    "dapur_stokkeluar_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1089).toString()+"',"+
                    "sirkulasi_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1090).toString()+"',"+
                    "sirkulasi_dapur2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1091).toString()+"',"+
                    "verifikasi_penerimaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1092).toString()+"',"+
                    "nilai_penerimaan_vendor_dapur_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1093).toString()+"',"+
                    "ringkasan_hutang_vendor_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1094).toString()+"',"+
                    "penilaian_psikologi_klinis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1095).toString()+"',"+
                    "penilaian_awal_medis_ranap_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1096).toString()+"',"+
                    "penilaian_derajat_dehidrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1097).toString()+"',"+
                    "ringkasan_jasa_tindakan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1098).toString()+"',"+
                    "pendapatan_per_akun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1099).toString()+"',"+
                    "hasil_pemeriksaan_echo='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1100).toString()+"',"+
                    "penilaian_bayi_baru_lahir='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1101).toString()+"',"+
                    "rl1_3_ketersediaan_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1102).toString()+"',"+
                    "pendapatan_per_akun_closing='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1103).toString()+"',"+
                    "pengeluaran_pengeluaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1104).toString()+"',"+
                    "skrining_diabetes_melitus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1105).toString()+"',"+
                    "laporan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1106).toString()+"',"+
                    "pelaksanaan_informasi_edukasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1107).toString()+"',"+
                    "layanan_kedokteran_fisik_rehabilitasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1108).toString()+"',"+
                    "skrining_kesehatan_gigi_mulut_balita='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1109).toString()+"',"+
                    "skrining_anemia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1110).toString()+"',"+
                    "layanan_program_kfr='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1111).toString()+"',"+
                    "skrining_hipertensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1112).toString()+"',"+
                    "skrining_kesehatan_penglihatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1113).toString()+"',"+
                    "catatan_observasi_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1114).toString()+"',"+
                    "skrining_kesehatan_gigi_mulut_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1115).toString()+"',"+
                    "skrining_risiko_kanker_serviks='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1116).toString()+"',"+
                    "catatan_cairan_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1117).toString()+"',"+
                    "skrining_kesehatan_gigi_mulut_lansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1118).toString()+"',"+
                    "skrining_indra_pendengaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1119).toString()+"',"+
                    "catatan_pengkajian_paska_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1120).toString()+"',"+
                    "skrining_frailty_syndrome='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1121).toString()+"',"+
                    "sirkulasi_cssd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1122).toString()+"',"+
                    "lama_pelayanan_cssd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1123).toString()+"',"+
                    "catatan_observasi_bayi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1124).toString()+"',"+
                    "riwayat_surat_peringatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1125).toString()+"',"+
                    "master_kesimpulan_anjuran_mcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1126).toString()+"',"+
                    "kategori_piutang_jasa_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1127).toString()+"',"+
                    "piutang_jasa_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1128).toString()+"',"+
                    "bayar_piutang_jasa_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1129).toString()+"',"+
                    "piutang_jasa_perusahaan_belum_lunas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1130).toString()+"',"+
                    "checklist_kesiapan_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1131).toString()+"',"+
                    "piutang_peminjaman_uang_belum_lunas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1132).toString()+"',"+
                    "hasil_pemeriksaan_slit_lamp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1133).toString()+"',"+
                    "hasil_pemeriksaan_oct='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1134).toString()+"',"+
                    "beban_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1135).toString()+"',"+
                    "poli_asal_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1136).toString()+"',"+
                    "pemberi_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1137).toString()+"',"+
                    "dokter_asal_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1138).toString()+"',"+
                    "duta_parkir_rekap_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1139).toString()+"',"+
                    "surat_keterangan_layak_terbang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1140).toString()+"',"+
                    "bayar_beban_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1141).toString()+"',"+
                    "surat_persetujuan_pemeriksaan_hiv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1142).toString()+"',"+
                    "skrining_instrumen_acrs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1143).toString()+"',"+
                    "surat_pernyataan_memilih_dpjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1144).toString()+"',"+
                    "skrining_instrumen_mental_emosional='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1145).toString()+"',"+
                    "pelanggan_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1146).toString()+"',"+
                    "kriteria_masuk_nicu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1147).toString()+"',"+
                    "kriteria_keluar_nicu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1148).toString()+"',"+
                    "penilaian_medis_ranap_psikiatrik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1149).toString()+"',"+
                    "kriteria_masuk_picu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1150).toString()+"',"+
                    "kriteria_keluar_picu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1151).toString()+"',"+
                    "master_sampel_bakumutu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1152).toString()+"',"+
                    "skrining_instrumen_amt='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1153).toString()+"',"+
                    "parameter_pengujian_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1154).toString()+"',"+
                    "nilai_normal_baku_mutu_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1155).toString()+"',"+
                    "skrining_pneumonia_severity_index='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1156).toString()+"',"+
                    "permintaan_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1157).toString()+"',"+
                    "penilaian_awal_medis_ralan_jantung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1158).toString()+"',"+
                    "penilaian_awal_medis_ralan_urologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1159).toString()+"',"+
                    "hasil_pemeriksaan_treadmill='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1160).toString()+"',"+
                    "hasil_pemeriksaan_echo_pediatrik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1161).toString()+"',"+
                    "template_pelaksanaan_informasi_edukasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1162).toString()+"',"+
                    "skrining_instrumen_esat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1163).toString()+"',"+
                    "penilaian_awal_medis_ranap_jantung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1164).toString()+"',"+
                    "penugasan_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1165).toString()+"',"+
                    "hasil_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1166).toString()+"',"+
                    "verifikasi_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1167).toString()+"',"+
                    "validasi_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1168).toString()+"',"+
                    "rekap_pelayanan_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1169).toString()+"',"+
                    "pembayaran_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1170).toString()+"',"+
                    "skrining_curb65='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1171).toString()+"',"+
                    "bpjs_potensi_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1172).toString()+"',"+
                    "bpjs_riwayat_pelayanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1173).toString()+"',"+
                    "skrining_gizi_kehamilan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1174).toString()+"',"+
                    "bpjs_rekap_peserta_prb_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1175).toString()+"',"+
                    "serah_terima_anggota_tubuh_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1176).toString()+"',"+
                    "pcra_icra_jenis_aktivitas_proyek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1177).toString()+"',"+
                    "pcra_icra_lokasi_kelompok_risiko_area='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1178).toString()+"',"+
                    "pcra_icra_kelas_risiko_pencegahan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1179).toString()+"',"+
                    "pcra_icra_tindakan_pengendalian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1180).toString()+"',"+
                    "pcra_icra_identifkasi_risiko_infeksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1181).toString()+"',"+
                    "pcra_icra_identifkasi_risiko_keselamatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1182).toString()+"',"+
                    "pcra_icra_identifkasi_risiko_kebakaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1183).toString()+"',"+
                    "pcra_icra_identifkasi_risiko_utilitas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1184).toString()+"',"+
                    "bpjs_daftar_resep_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1185).toString()+"',"+
                    "daftar_permintaan_resep_iterasi_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1186).toString()+"',"+
                    "pcra_icra_pengkajian_risiko_prakonstruksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1187).toString()+"',"+
                    "pcra_icra_persyaratan_harus_dipenuhi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1188).toString()+"',"+
                    "satu_sehat_kirim_questionresponse_telaah_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1189).toString()+"',"+
                    "satu_sehat_kirim_allergy_intolerance='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1190).toString()+"',"+
                    "konsultasi_perawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1191).toString()+"',"+
                    "jawaban_konsultasi_perawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1192).toString()+"',"+
                    "bridging_smart_klaim_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1193).toString()+"',"+
                    "mapping_prosedur_smart_klaim_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1194).toString()+"',"+
                    "mapping_penyakit_smart_klaim_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1195).toString()+"',"+
                    "permintaan_binrohtal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1196).toString()+"',"+
                    "surat_permintaan_perlindungan_dari_kekerasan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1197).toString()+"',"+
                    "surat_permohonan_privasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1198).toString()+"',"+
                    "surat_permintaan_second_opinion='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1199).toString()+"',"+
                    "surat_keterangan_berobat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1200).toString()+"',"+
                    "surat_penolakan_resusitasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1201).toString()+"',"+
                    "catatan_observasi_ruang_ok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1202).toString()+"',"+
                    "hasil_pemeriksaan_usg_abdomen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1203).toString()+"',"+
                    "intervensi_nyeri_farmakologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1204).toString()+"',"+
                    "intervensi_nyeri_nonfarmakologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1205).toString()+"',"+
                    "surat_pengajuan_cuti_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1206).toString()+"',"+
                    "checklist_kriteria_masuk_isolasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1207).toString()+"',"+
                    "satu_sehat_mapping_kptl_tindakan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1208).toString()+"',"+
                    "satu_sehat_mapping_kptl_tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1209).toString()+"',"+
                    "satu_sehat_mapping_kptl_tindakan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1210).toString()+"',"+
                    "satu_sehat_mapping_kptl_tindakan_laborat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1211).toString()+"',"+
                    "satu_sehat_mapping_kptl_tindakan_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1212).toString()+"',"+
                    "satu_sehat_mapping_kptl_tarif_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1213).toString()+"',"+
                    "checklist_kriteria_keluar_isolasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1214).toString()+"',"+
                    "satu_sehat_tanda_tangan_elektronik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1215).toString()+"',"+
                    "satu_sehat_kirim_composition='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1216).toString()+"',"+
                    "ringkasan_hutang_vendor_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1217).toString()+"',"+
                    "ringkasan_beban_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1218).toString()+"',"+
                    "set_resep_per_cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1219).toString()+"',"+
                    "skrining_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1220).toString()+"',"+
                    "admisi_skoring_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1221).toString()+"',"+
                    "monitoring_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1222).toString()+"',"+
                    "hasil_persalinan_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1223).toString()+"',"+
                    "laporan_reaksi_transfusi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1224).toString()+"',"+
                    "penilaian_grace_scor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1225).toString()+"',"+
                    "penilaian_bishop_scor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1226).toString()+"'")==true){
                    emptTeks();
                }
            }
        }

    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            dispose();
        }else{Valid.pindah(evt,BtnEdit,BtnKeluar);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void TKdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
        Valid.pindah(evt,BtnSimpan,TPass);
    }//GEN-LAST:event_TKdKeyPressed

    private void TPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPassKeyPressed
        Valid.pindah(evt,TKd,BtnSimpan);
    }//GEN-LAST:event_TPassKeyPressed

    private void BtnSeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekActionPerformed
        if (dlgdokter == null || !dlgdokter.isDisplayable()) {
            dlgdokter=new DlgCariDokter(null,false);
            dlgdokter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dlgdokter.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(dlgdokter.getTable().getSelectedRow()!= -1){
                        TKd.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(),0).toString());
                        TNmUser.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(),1).toString());
                        TPass.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(),0).toString());
                        Jabatan.setText(dlgdokter.getTable().getValueAt(dlgdokter.getTable().getSelectedRow(),10).toString());
                    }
                    TKd.requestFocus();
                    dlgdokter=null;
                }
            });
            dlgdokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dlgdokter.setLocationRelativeTo(internalFrame1);
        }
        if (dlgdokter == null) return;
        if (!dlgdokter.isVisible()) {
            dlgdokter.emptTeks();
            dlgdokter.isCek();
        }
        if (dlgdokter.isVisible()) {
            dlgdokter.toFront();
            return;
        }
        dlgdokter.setVisible(true);
    }//GEN-LAST:event_BtnSeekActionPerformed

    private void BtnSeekKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeekKeyPressed
        Valid.pindah(evt,TKd,TPass);
    }//GEN-LAST:event_BtnSeekKeyPressed

    private void BtnSeek1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek1ActionPerformed
        if (dlgpetugas == null || !dlgpetugas.isDisplayable()) {
            dlgpetugas=new DlgCariPetugas(null,false);
            dlgpetugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dlgpetugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(dlgpetugas.getTable().getSelectedRow()!= -1){
                        TKd.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(),0).toString());
                        TNmUser.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(),1).toString());
                        TPass.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(),0).toString());
                        Jabatan.setText(dlgpetugas.getTable().getValueAt(dlgpetugas.getTable().getSelectedRow(),9).toString());
                    }
                    TKd.requestFocus();
                    dlgpetugas=null;
                }
            });
            dlgpetugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dlgpetugas.setLocationRelativeTo(internalFrame1);
        }

        if (dlgpetugas == null) return;
        if (!dlgpetugas.isVisible()) {
            dlgpetugas.emptTeks();
            dlgpetugas.isCek();
        }
        if (dlgpetugas.isVisible()) {
            dlgpetugas.toFront();
            return;
        }
        dlgpetugas.setVisible(true);
    }//GEN-LAST:event_BtnSeek1ActionPerformed

    private void BtnSeek1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek1KeyPressed
        Valid.pindah(evt,TKd,TPass);
    }//GEN-LAST:event_BtnSeek1KeyPressed

    private void TNmUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNmUserKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNmUserKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TKd.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            Sequel.queryu("delete from temporary where temp37='"+akses.getalamatip()+"'");
            int row=tabMode.getRowCount();
            for(i=0;i<row;i++){
                Sequel.menyimpan("temporary","'"+i+"','"+
                                tabMode.getValueAt(i,0).toString()+"','"+
                                tabMode.getValueAt(i,1).toString()+"','"+
                                tabMode.getValueAt(i,3).toString()+"','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','"+akses.getalamatip()+"'","Data User");
            }
            Valid.MyReportqry("rptUser.jasper","report","::[ Data User ]::","select * from temporary where temporary.temp37='"+akses.getalamatip()+"' order by temporary.no",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus,BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbUser.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnCari, TKd);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void MnCopyHakAksesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCopyHakAksesActionPerformed
        if(tbUser.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data user...!!!!");
            TCari.requestFocus();
        }else if(TKd.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu data user yang mau dicopy hak aksesnya...!!!");
            tbUser.requestFocus();
        }else{
            copyhakakses="copy";
            userdicopy=TKd.getText();
            if(tbUser.getSelectedRow()!= -1){
                barisdicopy=tbUser.getSelectedRow();
            }
            JOptionPane.showMessageDialog(null,"Silahkan pilih user tujuan..!!");
        }
    }//GEN-LAST:event_MnCopyHakAksesActionPerformed

    private void MnSetUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSetUserActionPerformed
        if(tbUser.getSelectedRow()!= -1){
            if (personal == null || !personal.isDisplayable()) {
                personal=new DlgUpdateUser(null,false);
                personal.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                personal.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        personal=null;
                        tampil();
                    }
                });

                personal.setSize(460,this.getHeight()-50);
                personal.setLocationRelativeTo(internalFrame1);
                if (personal == null) return;
                if (!personal.isVisible()) {
                    personal.isUser(TKd.getText(),TNmUser.getText(),TPass.getText());
                }
                if (personal.isVisible()) {
                    personal.toFront();
                    return;
                }
                personal.setVisible(true);
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan pilih dulu nama user..!!");
        }
    }//GEN-LAST:event_MnSetUserActionPerformed

    private void tbUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbUserMouseClicked
        if(tbUser.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }

            if(evt.getClickCount()==1){
                if(copyhakakses.equals("copy")){
                    if(userdicopy.equals(TKd.getText())){
                        JOptionPane.showMessageDialog(null,"Copy hak akses gagal karena user dicopy dan user tujuan yang dipilih sama..!!");
                        userdicopy="";
                        copyhakakses="";
                        barisdicopy=-1;
                    }else{
                        int reply = JOptionPane.showConfirmDialog(rootPane,"Eeiiiiiits, udah bener belum data copy hak aksesnya..??","Konfirmasi",JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            try {
                                i=tbUser.getSelectedRow();
                                if(i!= -1){
                                    Sequel.mengedit("user","id_user=AES_ENCRYPT('"+tabMode.getValueAt(tbUser.convertRowIndexToModel(i),0).toString()+"','nur')",
                                        "id_user=AES_ENCRYPT('"+TKd.getText()+"','nur'),"+
                                        "password=AES_ENCRYPT('"+TPass.getText()+"','windi'),"+
                                        "penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),4).toString()+"', "+
                                        "obat_penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),5).toString()+"',"+
                                        "dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),6).toString()+"',"+
                                        "jadwal_praktek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),7).toString()+"',"+
                                        "petugas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),8).toString()+"',"+
                                        "pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),9).toString()+"',"+
                                        "registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),10).toString()+"',"+
                                        "tindakan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),11).toString()+"',"+
                                        "kamar_inap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),12).toString()+"',"+
                                        "tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),13).toString()+"',"+
                                        "operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),14).toString()+"',"+
                                        "rujukan_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),15).toString()+"',"+
                                        "rujukan_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),16).toString()+"',"+
                                        "beri_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),17).toString()+"',"+
                                        "resep_pulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),18).toString()+"',"+
                                        "pasien_meninggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),19).toString()+"',"+
                                        "diet_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),20).toString()+"',"+
                                        "kelahiran_bayi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),21).toString()+"',"+
                                        "periksa_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),22).toString()+"',"+
                                        "periksa_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),23).toString()+"',"+
                                        "kasir_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),24).toString()+"',"+
                                        "deposit_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),25).toString()+"',"+
                                        "piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),26).toString()+"',"+
                                        "peminjaman_berkas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),27).toString()+"',"+
                                        "barcode='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),28).toString()+"',"+
                                        "presensi_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),29).toString()+"',"+
                                        "presensi_bulanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),30).toString()+"',"+
                                        "pegawai_admin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),31).toString()+"',"+
                                        "pegawai_user='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),32).toString()+"',"+
                                        "suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),33).toString()+"',"+
                                        "satuan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),34).toString()+"',"+
                                        "konversi_satuan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),35).toString()+"',"+
                                        "jenis_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),36).toString()+"',"+
                                        "obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),37).toString()+"',"+
                                        "stok_opname_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),38).toString()+"',"+
                                        "stok_obat_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),39).toString()+"',"+
                                        "pengadaan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),40).toString()+"',"+
                                        "pemesanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),41).toString()+"',"+
                                        "penjualan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),42).toString()+"',"+
                                        "piutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),43).toString()+"',"+
                                        "retur_ke_suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),44).toString()+"',"+
                                        "retur_dari_pembeli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),45).toString()+"',"+
                                        "retur_obat_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),46).toString()+"',"+
                                        "retur_piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),47).toString()+"',"+
                                        "keuntungan_penjualan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),48).toString()+"',"+
                                        "keuntungan_beri_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),49).toString()+"',"+
                                        "sirkulasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),50).toString()+"',"+
                                        "ipsrs_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),51).toString()+"',"+
                                        "ipsrs_pengadaan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),52).toString()+"',"+
                                        "ipsrs_stok_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),53).toString()+"',"+
                                        "ipsrs_rekap_pengadaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),54).toString()+"',"+
                                        "ipsrs_rekap_stok_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),55).toString()+"',"+
                                        "ipsrs_pengeluaran_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),56).toString()+"',"+
                                        "inventaris_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),57).toString()+"',"+
                                        "inventaris_kategori='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),58).toString()+"',"+
                                        "inventaris_merk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),59).toString()+"',"+
                                        "inventaris_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),60).toString()+"',"+
                                        "inventaris_produsen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),61).toString()+"',"+
                                        "inventaris_koleksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),62).toString()+"',"+
                                        "inventaris_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),63).toString()+"',"+
                                        "inventaris_sirkulasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),64).toString()+"',"+
                                        "parkir_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),65).toString()+"',"+
                                        "parkir_in='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),66).toString()+"',"+
                                        "parkir_out='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),67).toString()+"',"+
                                        "parkir_rekap_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),68).toString()+"',"+
                                        "parkir_rekap_bulanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),69).toString()+"',"+
                                        "informasi_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),70).toString()+"',"+
                                        "harian_tindakan_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),71).toString()+"',"+
                                        "obat_per_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),72).toString()+"',"+
                                        "obat_per_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),73).toString()+"',"+
                                        "obat_per_dokter_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),74).toString()+"',"+
                                        "obat_per_dokter_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),75).toString()+"',"+
                                        "harian_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),76).toString()+"',"+
                                        "bulanan_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),77).toString()+"',"+
                                        "harian_paramedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),78).toString()+"',"+
                                        "bulanan_paramedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),79).toString()+"',"+
                                        "pembayaran_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),80).toString()+"',"+
                                        "pembayaran_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),81).toString()+"',"+
                                        "rekap_pembayaran_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),82).toString()+"',"+
                                        "rekap_pembayaran_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),83).toString()+"',"+
                                        "tagihan_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),84).toString()+"',"+
                                        "tambahan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),85).toString()+"',"+
                                        "potongan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),86).toString()+"',"+
                                        "resep_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),87).toString()+"',"+
                                        "resume_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),88).toString()+"',"+
                                        "penyakit_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),89).toString()+"',"+
                                        "penyakit_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),90).toString()+"',"+
                                        "kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),91).toString()+"',"+
                                        "tarif_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),92).toString()+"',"+
                                        "tarif_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),93).toString()+"',"+
                                        "tarif_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),94).toString()+"',"+
                                        "tarif_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),95).toString()+"',"+
                                        "tarif_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),96).toString()+"',"+
                                        "akun_rekening='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),97).toString()+"',"+
                                        "rekening_tahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),98).toString()+"',"+
                                        "posting_jurnal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),99).toString()+"',"+
                                        "buku_besar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),100).toString()+"',"+
                                        "cashflow='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),101).toString()+"',"+
                                        "keuangan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),102).toString()+"',"+
                                        "pengeluaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),103).toString()+"',"+
                                        "setup_pjlab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),104).toString()+"',"+
                                        "setup_otolokasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),105).toString()+"',"+
                                        "setup_jam_kamin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),106).toString()+"',"+
                                        "setup_embalase='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),107).toString()+"',"+
                                        "tracer_login='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),108).toString()+"',"+
                                        "display='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),109).toString()+"',"+
                                        "set_harga_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),110).toString()+"',"+
                                        "set_penggunaan_tarif='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),111).toString()+"',"+
                                        "set_oto_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),112).toString()+"',"+
                                        "biaya_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),113).toString()+"',"+
                                        "biaya_masuk_sekali='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),114).toString()+"',"+
                                        "set_no_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),115).toString()+"',"+
                                        "billing_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),116).toString()+"',"+
                                        "billing_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),117).toString()+"',"+
                                        "jm_ranap_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),118).toString()+"',"+
                                        "igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),119).toString()+"',"+
                                        "barcoderalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),120).toString()+"',"+
                                        "barcoderanap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),121).toString()+"',"+
                                        "set_harga_obat_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),122).toString()+"',"+
                                        "set_harga_obat_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),123).toString()+"',"+
                                        "penyakit_pd3i='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),124).toString()+"',"+
                                        "surveilans_pd3i='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),125).toString()+"',"+
                                        "surveilans_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),126).toString()+"',"+
                                        "diagnosa_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),127).toString()+"',"+
                                        "surveilans_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),128).toString()+"',"+
                                        "pny_takmenular_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),129).toString()+"',"+
                                        "pny_takmenular_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),130).toString()+"',"+
                                        "kunjungan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),131).toString()+"',"+
                                        "rl32='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),132).toString()+"',"+
                                        "rl33='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),133).toString()+"',"+
                                        "rl37='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),134).toString()+"',"+
                                        "rl38='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),135).toString()+"',"+
                                        "harian_tindakan_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),136).toString()+"',"+
                                        "sms='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),137).toString()+"',"+
                                        "sidikjari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),138).toString()+"',"+
                                        "jam_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),139).toString()+"',"+
                                        "jadwal_pegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),140).toString()+"',"+
                                        "parkir_barcode='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),141).toString()+"',"+
                                        "set_nota='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),142).toString()+"',"+
                                        "dpjp_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),143).toString()+"',"+
                                        "mutasi_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),144).toString()+"',"+
                                        "rl34='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),145).toString()+"',"+
                                        "rl36='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),146).toString()+"',"+
                                        "fee_visit_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),147).toString()+"',"+
                                        "fee_bacaan_ekg='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),148).toString()+"',"+
                                        "fee_rujukan_rontgen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),149).toString()+"',"+
                                        "fee_rujukan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),150).toString()+"',"+
                                        "fee_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),151).toString()+"',"+
                                        "akun_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),152).toString()+"',"+
                                        "bayar_pemesanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),153).toString()+"',"+
                                        "obat_per_dokter_peresep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),154).toString()+"',"+
                                        "ipsrs_jenis_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),155).toString()+"',"+
                                        "pemasukan_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),156).toString()+"',"+
                                        "pengaturan_rekening='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),157).toString()+"',"+
                                        "closing_kasir='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),158).toString()+"',"+
                                        "keterlambatan_presensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),159).toString()+"',"+
                                        "set_harga_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),160).toString()+"',"+
                                        "rekap_per_shift='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),161).toString()+"',"+
                                        "bpjs_cek_nik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),162).toString()+"',"+
                                        "bpjs_cek_kartu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),163).toString()+"',"+
                                        "bpjs_cek_riwayat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),164).toString()+"',"+
                                        "obat_per_cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),165).toString()+"',"+
                                        "kunjungan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),166).toString()+"',"+
                                        "bayar_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),167).toString()+"',"+
                                        "payment_point='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),168).toString()+"',"+
                                        "bpjs_cek_nomor_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),169).toString()+"',"+
                                        "icd9='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),170).toString()+"',"+
                                        "darurat_stok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),171).toString()+"',"+
                                        "retensi_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),172).toString()+"',"+
                                        "temporary_presensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),173).toString()+"',"+
                                        "jurnal_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),174).toString()+"',"+
                                        "sirkulasi_obat2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),175).toString()+"',"+
                                        "edit_registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),176).toString()+"',"+
                                        "bpjs_referensi_diagnosa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),177).toString()+"',"+
                                        "bpjs_referensi_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),178).toString()+"',"+
                                        "industrifarmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),179).toString()+"',"+
                                        "harian_js='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),180).toString()+"',"+
                                        "bulanan_js='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),181).toString()+"',"+
                                        "harian_paket_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),182).toString()+"',"+
                                        "bulanan_paket_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),183).toString()+"',"+
                                        "piutang_pasien2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),184).toString()+"',"+
                                        "bpjs_referensi_faskes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),185).toString()+"',"+
                                        "bpjs_sep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),186).toString()+"',"+
                                        "pengambilan_utd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),187).toString()+"',"+
                                        "tarif_utd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),188).toString()+"',"+
                                        "pengambilan_utd2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),189).toString()+"',"+
                                        "utd_medis_rusak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),190).toString()+"',"+
                                        "pengambilan_penunjang_utd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),191).toString()+"',"+
                                        "pengambilan_penunjang_utd2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),192).toString()+"',"+
                                        "utd_penunjang_rusak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),193).toString()+"',"+
                                        "suplier_penunjang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),194).toString()+"',"+
                                        "utd_donor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),195).toString()+"',"+
                                        "bpjs_monitoring_klaim='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),196).toString()+"',"+
                                        "utd_cekal_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),197).toString()+"',"+
                                        "utd_komponen_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),198).toString()+"',"+
                                        "utd_stok_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),199).toString()+"',"+
                                        "utd_pemisahan_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),200).toString()+"',"+
                                        "harian_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),201).toString()+"',"+
                                        "rincian_piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),202).toString()+"',"+
                                        "keuntungan_beri_obat_nonpiutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),203).toString()+"',"+
                                        "reklasifikasi_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),204).toString()+"',"+
                                        "reklasifikasi_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),205).toString()+"',"+
                                        "utd_penyerahan_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),206).toString()+"',"+
                                        "hutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),207).toString()+"',"+
                                        "riwayat_obat_alkes_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),208).toString()+"',"+
                                        "sensus_harian_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),209).toString()+"',"+
                                        "rl4a='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),210).toString()+"',"+
                                        "aplicare_referensi_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),211).toString()+"',"+
                                        "aplicare_ketersediaan_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),212).toString()+"',"+
                                        "inacbg_klaim_baru_otomatis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),213).toString()+"',"+
                                        "inacbg_klaim_baru_manual='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),214).toString()+"',"+
                                        "inacbg_coder_nik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),215).toString()+"',"+
                                        "mutasi_berkas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),216).toString()+"',"+
                                        "akun_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),217).toString()+"',"+
                                        "harian_kso='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),218).toString()+"',"+
                                        "bulanan_kso='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),219).toString()+"',"+
                                        "harian_menejemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),220).toString()+"',"+
                                        "bulanan_menejemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),221).toString()+"',"+
                                        "inhealth_cek_eligibilitas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),222).toString()+"',"+
                                        "inhealth_referensi_jenpel_ruang_rawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),223).toString()+"',"+
                                        "inhealth_referensi_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),224).toString()+"',"+
                                        "inhealth_referensi_faskes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),225).toString()+"',"+
                                        "inhealth_sjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),226).toString()+"',"+
                                        "piutang_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),227).toString()+"',"+
                                        "piutang_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),228).toString()+"',"+
                                        "detail_piutang_penjab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),229).toString()+"',"+
                                        "lama_pelayanan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),230).toString()+"',"+
                                        "catatan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),231).toString()+"',"+
                                        "rl4b='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),232).toString()+"',"+
                                        "rl4asebab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),233).toString()+"',"+
                                        "rl4bsebab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),234).toString()+"',"+
                                        "data_HAIs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),235).toString()+"',"+
                                        "harian_HAIs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),236).toString()+"',"+
                                        "bulanan_HAIs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),237).toString()+"',"+
                                        "hitung_bor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),238).toString()+"',"+
                                        "perusahaan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),239).toString()+"',"+
                                        "resep_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),240).toString()+"',"+
                                        "lama_pelayanan_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),241).toString()+"',"+
                                        "hitung_alos='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),242).toString()+"',"+
                                        "detail_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),243).toString()+"',"+
                                        "rujukan_poli_internal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),244).toString()+"',"+
                                        "rekap_poli_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),245).toString()+"',"+
                                        "grafik_kunjungan_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),246).toString()+"',"+
                                        "grafik_kunjungan_perdokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),247).toString()+"',"+
                                        "grafik_kunjungan_perpekerjaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),248).toString()+"',"+
                                        "grafik_kunjungan_perpendidikan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),249).toString()+"',"+
                                        "grafik_kunjungan_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),250).toString()+"',"+
                                        "berkas_digital_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),251).toString()+"',"+
                                        "penyakit_menular_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),252).toString()+"',"+
                                        "penyakit_menular_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),253).toString()+"',"+
                                        "grafik_kunjungan_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),254).toString()+"',"+
                                        "grafik_kunjungan_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),255).toString()+"',"+
                                        "grafik_kunjungan_demografi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),256).toString()+"',"+
                                        "grafik_kunjungan_statusdaftartahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),257).toString()+"',"+
                                        "grafik_kunjungan_statusdaftartahun2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),258).toString()+"',"+
                                        "grafik_kunjungan_statusdaftarbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),259).toString()+"',"+
                                        "grafik_kunjungan_statusdaftarbulan2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),260).toString()+"',"+
                                        "grafik_kunjungan_statusdaftartanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),261).toString()+"',"+
                                        "grafik_kunjungan_statusdaftartanggal2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),262).toString()+"',"+
                                        "grafik_kunjungan_statusbataltahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),263).toString()+"',"+
                                        "grafik_kunjungan_statusbatalbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),264).toString()+"',"+
                                        "pcare_cek_penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),265).toString()+"',"+
                                        "grafik_kunjungan_statusbataltanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),266).toString()+"',"+
                                        "kategori_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),267).toString()+"',"+
                                        "golongan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),268).toString()+"',"+
                                        "pemberian_obat_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),269).toString()+"',"+
                                        "penjualan_obat_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),270).toString()+"',"+
                                        "pcare_cek_kesadaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),271).toString()+"',"+
                                        "pembatalan_periksa_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),272).toString()+"',"+
                                        "pembayaran_per_unit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),273).toString()+"',"+
                                        "rekap_pembayaran_per_unit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),274).toString()+"',"+
                                        "grafik_kunjungan_percarabayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),275).toString()+"',"+
                                        "ipsrs_pengadaan_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),276).toString()+"',"+
                                        "ipsrs_stokkeluar_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),277).toString()+"',"+
                                        "grafik_kunjungan_ranaptahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),278).toString()+"',"+
                                        "pcare_cek_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),279).toString()+"',"+
                                        "grafik_lab_ralantahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),280).toString()+"',"+
                                        "grafik_rad_ralantahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),281).toString()+"',"+
                                        "cek_entry_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),282).toString()+"',"+
                                        "inacbg_klaim_baru_manual2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),283).toString()+"',"+
                                        "permintaan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),284).toString()+"',"+
                                        "rekap_permintaan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),285).toString()+"',"+
                                        "surat_pemesanan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),286).toString()+"',"+
                                        "permintaan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),287).toString()+"',"+
                                        "rekap_permintaan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),288).toString()+"',"+
                                        "surat_pemesanan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),289).toString()+"',"+
                                        "grafik_per_perujuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),290).toString()+"',"+
                                        "bpjs_cek_prosedur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),291).toString()+"',"+
                                        "bpjs_cek_kelas_rawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),292).toString()+"',"+
                                        "bpjs_cek_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),293).toString()+"',"+
                                        "bpjs_cek_spesialistik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),294).toString()+"',"+
                                        "bpjs_cek_ruangrawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),295).toString()+"',"+
                                        "bpjs_cek_carakeluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),296).toString()+"',"+
                                        "bpjs_cek_pasca_pulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),297).toString()+"',"+
                                        "detail_tindakan_okvk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),298).toString()+"',"+
                                        "billing_parsial='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),299).toString()+"',"+
                                        "bpjs_cek_nomor_rujukan_rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),300).toString()+"',"+
                                        "bpjs_cek_rujukan_kartu_pcare='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),301).toString()+"',"+
                                        "bpjs_cek_rujukan_kartu_rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),302).toString()+"',"+
                                        "akses_depo_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),303).toString()+"',"+
                                        "bpjs_rujukan_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),304).toString()+"',"+
                                        "grafik_lab_ralanbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),305).toString()+"',"+
                                        "pengeluaran_stok_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),306).toString()+"',"+
                                        "grafik_rad_ralanbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),307).toString()+"',"+
                                        "detailjmdokter2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),308).toString()+"',"+
                                        "pengaduan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),309).toString()+"',"+
                                        "grafik_lab_ralanhari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),310).toString()+"',"+
                                        "grafik_rad_ralanhari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),311).toString()+"',"+
                                        "sensus_harian_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),312).toString()+"',"+
                                        "metode_racik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),313).toString()+"',"+
                                        "pembayaran_akun_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),314).toString()+"',"+
                                        "pengguna_obat_resep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),315).toString()+"',"+
                                        "rekap_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),316).toString()+"',"+
                                        "master_berkas_pegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),317).toString()+"',"+
                                        "berkas_kepegawaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),318).toString()+"',"+
                                        "riwayat_jabatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),319).toString()+"',"+
                                        "riwayat_pendidikan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),320).toString()+"',"+
                                        "riwayat_naik_gaji='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),321).toString()+"',"+
                                        "kegiatan_ilmiah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),322).toString()+"',"+
                                        "riwayat_penghargaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),323).toString()+"',"+
                                        "riwayat_penelitian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),324).toString()+"',"+
                                        "penerimaan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),325).toString()+"',"+
                                        "bayar_pesan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),326).toString()+"',"+
                                        "hutang_barang_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),327).toString()+"',"+
                                        "rekap_pemesanan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),328).toString()+"',"+
                                        "insiden_keselamatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),329).toString()+"',"+
                                        "insiden_keselamatan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),330).toString()+"',"+
                                        "grafik_ikp_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),331).toString()+"',"+
                                        "grafik_ikp_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),332).toString()+"',"+
                                        "grafik_ikp_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),333).toString()+"',"+
                                        "riwayat_data_batch='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),334).toString()+"',"+
                                        "grafik_ikp_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),335).toString()+"',"+
                                        "grafik_ikp_dampak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),336).toString()+"',"+
                                        "piutang_akun_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),337).toString()+"',"+
                                        "grafik_kunjungan_per_agama='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),338).toString()+"',"+
                                        "grafik_kunjungan_per_umur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),339).toString()+"',"+
                                        "suku_bangsa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),340).toString()+"',"+
                                        "bahasa_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),341).toString()+"',"+
                                        "golongan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),342).toString()+"',"+
                                        "satuan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),343).toString()+"',"+
                                        "jabatan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),344).toString()+"',"+
                                        "pangkat_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),345).toString()+"',"+
                                        "golongan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),346).toString()+"',"+
                                        "satuan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),347).toString()+"',"+
                                        "jabatan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),348).toString()+"',"+
                                        "pangkat_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),349).toString()+"',"+
                                        "cacat_fisik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),350).toString()+"',"+
                                        "grafik_kunjungan_suku='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),351).toString()+"',"+
                                        "grafik_kunjungan_bahasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),352).toString()+"',"+
                                        "booking_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),353).toString()+"',"+
                                        "mapping_poli_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),354).toString()+"',"+
                                        "grafik_kunjungan_per_cacat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),355).toString()+"',"+
                                        "barang_cssd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),356).toString()+"',"+
                                        "skdp_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),357).toString()+"',"+
                                        "booking_registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),358).toString()+"',"+
                                        "bpjs_cek_propinsi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),359).toString()+"',"+
                                        "bpjs_cek_kabupaten='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),360).toString()+"',"+
                                        "bpjs_cek_kecamatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),361).toString()+"',"+
                                        "bpjs_cek_dokterdpjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),362).toString()+"',"+
                                        "bpjs_cek_riwayat_rujukanrs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),363).toString()+"',"+
                                        "bpjs_cek_tanggal_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),364).toString()+"',"+
                                        "permintaan_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),365).toString()+"',"+
                                        "permintaan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),366).toString()+"',"+
                                        "surat_indeks='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),367).toString()+"',"+
                                        "surat_map='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),368).toString()+"',"+
                                        "surat_almari='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),369).toString()+"',"+
                                        "surat_rak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),370).toString()+"',"+
                                        "surat_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),371).toString()+"',"+
                                        "surat_klasifikasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),372).toString()+"',"+
                                        "surat_status='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),373).toString()+"',"+
                                        "surat_sifat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),374).toString()+"',"+
                                        "surat_balas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),375).toString()+"',"+
                                        "surat_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),376).toString()+"',"+
                                        "pcare_cek_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),377).toString()+"',"+
                                        "pcare_cek_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),378).toString()+"',"+
                                        "pcare_cek_provider='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),379).toString()+"',"+
                                        "pcare_cek_statuspulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),380).toString()+"',"+
                                        "pcare_cek_spesialis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),381).toString()+"',"+
                                        "pcare_cek_subspesialis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),382).toString()+"',"+
                                        "pcare_cek_sarana='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),383).toString()+"',"+
                                        "pcare_cek_khusus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),384).toString()+"',"+
                                        "pcare_cek_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),385).toString()+"',"+
                                        "pcare_cek_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),386).toString()+"',"+
                                        "pcare_cek_faskessubspesialis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),387).toString()+"',"+
                                        "pcare_cek_faskesalihrawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),388).toString()+"',"+
                                        "pcare_cek_faskesthalasemia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),389).toString()+"',"+
                                        "pcare_mapping_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),390).toString()+"',"+
                                        "pcare_mapping_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),391).toString()+"',"+
                                        "pcare_club_prolanis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),392).toString()+"',"+
                                        "pcare_mapping_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),393).toString()+"',"+
                                        "pcare_kegiatan_kelompok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),394).toString()+"',"+
                                        "pcare_mapping_tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),395).toString()+"',"+
                                        "pcare_peserta_kegiatan_kelompok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),396).toString()+"',"+
                                        "sirkulasi_obat3='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),397).toString()+"',"+
                                        "bridging_pcare_daftar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),398).toString()+"',"+
                                        "pcare_mapping_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),399).toString()+"',"+
                                        "ranap_per_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),400).toString()+"',"+
                                        "penyakit_ranap_cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),401).toString()+"',"+
                                        "anggota_militer_dirawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),402).toString()+"',"+
                                        "set_input_parsial='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),403).toString()+"',"+
                                        "lama_pelayanan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),404).toString()+"',"+
                                        "lama_pelayanan_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),405).toString()+"',"+
                                        "bpjs_cek_sep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),406).toString()+"',"+
                                        "catatan_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),407).toString()+"',"+
                                        "surat_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),408).toString()+"',"+
                                        "kegiatan_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),409).toString()+"',"+
                                        "stok_opname_logistik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),410).toString()+"',"+
                                        "sirkulasi_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),411).toString()+"',"+
                                        "rekap_lab_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),412).toString()+"',"+
                                        "perujuk_lab_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),413).toString()+"',"+
                                        "rekap_radiologi_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),414).toString()+"',"+
                                        "perujuk_radiologi_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),415).toString()+"',"+
                                        "jumlah_porsi_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),416).toString()+"',"+
                                        "jumlah_macam_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),417).toString()+"',"+
                                        "payment_point2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),418).toString()+"',"+
                                        "pembayaran_akun_bayar2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),419).toString()+"',"+
                                        "hapus_nota_salah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),420).toString()+"',"+
                                        "pengkajian_askep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),421).toString()+"',"+
                                        "hais_perbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),422).toString()+"',"+
                                        "ppn_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),423).toString()+"',"+
                                        "saldo_akun_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),424).toString()+"',"+
                                        "display_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),425).toString()+"',"+
                                        "sisrute_referensi_faskes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),426).toString()+"',"+
                                        "sisrute_referensi_alasanrujuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),427).toString()+"',"+
                                        "sisrute_referensi_diagnosa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),428).toString()+"',"+
                                        "sisrute_rujukan_masuk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),429).toString()+"',"+
                                        "sisrute_rujukan_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),430).toString()+"',"+
                                        "bpjs_cek_skdp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),431).toString()+"',"+
                                        "data_batch='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),432).toString()+"',"+
                                        "kunjungan_permintaan_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),433).toString()+"',"+
                                        "kunjungan_permintaan_lab2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),434).toString()+"',"+
                                        "kunjungan_permintaan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),435).toString()+"',"+
                                        "kunjungan_permintaan_radiologi2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),436).toString()+"',"+
                                        "pcare_pemberian_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),437).toString()+"',"+
                                        "pcare_pemberian_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),438).toString()+"',"+
                                        "pembayaran_akun_bayar3='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),439).toString()+"',"+
                                        "password_asuransi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),440).toString()+"',"+
                                        "kemenkes_sitt='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),441).toString()+"',"+
                                        "siranap_ketersediaan_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),442).toString()+"',"+
                                        "grafik_tb_periodelaporan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),443).toString()+"',"+
                                        "grafik_tb_rujukan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),444).toString()+"',"+
                                        "grafik_tb_riwayat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),445).toString()+"',"+
                                        "grafik_tb_tipediagnosis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),446).toString()+"',"+
                                        "grafik_tb_statushiv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),447).toString()+"',"+
                                        "grafik_tb_skoringanak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),448).toString()+"',"+
                                        "grafik_tb_konfirmasiskoring5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),449).toString()+"',"+
                                        "grafik_tb_konfirmasiskoring6='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),450).toString()+"',"+
                                        "grafik_tb_sumberobat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),451).toString()+"',"+
                                        "grafik_tb_hasilakhirpengobatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),452).toString()+"',"+
                                        "grafik_tb_hasilteshiv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),453).toString()+"',"+
                                        "kadaluarsa_batch='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),454).toString()+"',"+
                                        "sisa_stok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),455).toString()+"',"+
                                        "obat_per_resep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),456).toString()+"',"+
                                        "pemakaian_air_pdam='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),457).toString()+"',"+
                                        "limbah_b3_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),458).toString()+"',"+
                                        "grafik_air_pdam_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),459).toString()+"',"+
                                        "grafik_air_pdam_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),460).toString()+"',"+
                                        "grafik_limbahb3_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),461).toString()+"',"+
                                        "grafik_limbahb3_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),462).toString()+"',"+
                                        "limbah_domestik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),463).toString()+"',"+
                                        "grafik_limbahdomestik_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),464).toString()+"',"+
                                        "grafik_limbahdomestik_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),465).toString()+"',"+
                                        "mutu_air_limbah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),466).toString()+"',"+
                                        "pest_control='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),467).toString()+"',"+
                                        "ruang_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),468).toString()+"',"+
                                        "kategori_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),469).toString()+"',"+
                                        "jenis_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),470).toString()+"',"+
                                        "pengarang_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),471).toString()+"',"+
                                        "penerbit_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),472).toString()+"',"+
                                        "koleksi_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),473).toString()+"',"+
                                        "inventaris_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),474).toString()+"',"+
                                        "set_peminjaman_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),475).toString()+"',"+
                                        "denda_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),476).toString()+"',"+
                                        "anggota_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),477).toString()+"',"+
                                        "peminjaman_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),478).toString()+"',"+
                                        "bayar_denda_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),479).toString()+"',"+
                                        "ebook_perpustakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),480).toString()+"',"+
                                        "jenis_cidera_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),481).toString()+"',"+
                                        "penyebab_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),482).toString()+"',"+
                                        "jenis_luka_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),483).toString()+"',"+
                                        "lokasi_kejadian_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),484).toString()+"',"+
                                        "dampak_cidera_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),485).toString()+"',"+
                                        "jenis_pekerjaan_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),486).toString()+"',"+
                                        "bagian_tubuh_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),487).toString()+"',"+
                                        "peristiwa_k3rs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),488).toString()+"',"+
                                        "grafik_k3_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),489).toString()+"',"+
                                        "grafik_k3_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),490).toString()+"',"+
                                        "grafik_k3_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),491).toString()+"',"+
                                        "grafik_k3_perjeniscidera='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),492).toString()+"',"+
                                        "grafik_k3_perpenyebab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),493).toString()+"',"+
                                        "grafik_k3_perjenisluka='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),494).toString()+"',"+
                                        "grafik_k3_lokasikejadian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),495).toString()+"',"+
                                        "grafik_k3_dampakcidera='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),496).toString()+"',"+
                                        "grafik_k3_perjenispekerjaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),497).toString()+"',"+
                                        "grafik_k3_perbagiantubuh='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),498).toString()+"',"+
                                        "jenis_cidera_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),499).toString()+"',"+
                                        "penyebab_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),500).toString()+"',"+
                                        "jenis_luka_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),501).toString()+"',"+
                                        "lokasi_kejadian_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),502).toString()+"',"+
                                        "dampak_cidera_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),503).toString()+"',"+
                                        "jenis_pekerjaan_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),504).toString()+"',"+
                                        "bagian_tubuh_k3rstahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),505).toString()+"',"+
                                        "sekrining_rawat_jalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),506).toString()+"',"+
                                        "bpjs_histori_pelayanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),507).toString()+"',"+
                                        "rekap_mutasi_berkas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),508).toString()+"',"+
                                        "skrining_ralan_pernapasan_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),509).toString()+"',"+
                                        "pengajuan_barang_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),510).toString()+"',"+
                                        "pengajuan_barang_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),511).toString()+"',"+
                                        "grafik_kunjungan_ranapbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),512).toString()+"',"+
                                        "grafik_kunjungan_ranaptanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),513).toString()+"',"+
                                        "grafik_kunjungan_ranap_peruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),514).toString()+"',"+
                                        "kunjungan_bangsal_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),515).toString()+"',"+
                                        "grafik_jenjang_jabatanpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),516).toString()+"',"+
                                        "grafik_bidangpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),517).toString()+"',"+
                                        "grafik_departemenpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),518).toString()+"',"+
                                        "grafik_pendidikanpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),519).toString()+"',"+
                                        "grafik_sttswppegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),520).toString()+"',"+
                                        "grafik_sttskerjapegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),521).toString()+"',"+
                                        "grafik_sttspulangranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),522).toString()+"',"+
                                        "kip_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),523).toString()+"',"+
                                        "kip_pasien_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),524).toString()+"',"+
                                        "bpjs_mapping_dokterdpjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),525).toString()+"',"+
                                        "data_triase_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),526).toString()+"',"+
                                        "master_triase_skala1='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),527).toString()+"',"+
                                        "master_triase_skala2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),528).toString()+"',"+
                                        "master_triase_skala3='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),529).toString()+"',"+
                                        "master_triase_skala4='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),530).toString()+"',"+
                                        "master_triase_skala5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),531).toString()+"',"+
                                        "master_triase_pemeriksaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),532).toString()+"',"+
                                        "master_triase_macamkasus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),533).toString()+"',"+
                                        "rekap_permintaan_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),534).toString()+"',"+
                                        "daftar_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),535).toString()+"',"+
                                        "daftar_pasien_ranaptni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),536).toString()+"',"+
                                        "pengajuan_asetinventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),537).toString()+"',"+
                                        "item_apotek_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),538).toString()+"',"+
                                        "item_apotek_kategori='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),539).toString()+"',"+
                                        "item_apotek_golongan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),540).toString()+"',"+
                                        "item_apotek_industrifarmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),541).toString()+"',"+
                                        "10_obat_terbanyak_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),542).toString()+"',"+
                                        "grafik_pengajuan_aset_urgensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),543).toString()+"',"+
                                        "grafik_pengajuan_aset_status='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),544).toString()+"',"+
                                        "grafik_pengajuan_aset_departemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),545).toString()+"',"+
                                        "rekap_pengajuan_aset_departemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),546).toString()+"',"+
                                        "grafik_kelompok_jabatanpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),547).toString()+"',"+
                                        "grafik_resiko_kerjapegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),548).toString()+"',"+
                                        "grafik_emergency_indexpegawai='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),549).toString()+"',"+
                                        "grafik_inventaris_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),550).toString()+"',"+
                                        "harian_HAIs2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),551).toString()+"',"+
                                        "grafik_inventaris_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),552).toString()+"',"+
                                        "data_resume_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),553).toString()+"',"+
                                        "perkiraan_biaya_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),554).toString()+"',"+
                                        "rekap_obat_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),555).toString()+"',"+
                                        "rekap_obat_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),556).toString()+"',"+
                                        "permintaan_perbaikan_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),557).toString()+"',"+
                                        "grafik_HAIs_pasienbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),558).toString()+"',"+
                                        "grafik_HAIs_pasienbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),559).toString()+"',"+
                                        "grafik_HAIs_laju_vap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),560).toString()+"',"+
                                        "grafik_HAIs_laju_iad='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),561).toString()+"',"+
                                        "grafik_HAIs_laju_pleb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),562).toString()+"',"+
                                        "grafik_HAIs_laju_isk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),563).toString()+"',"+
                                        "grafik_HAIs_laju_ilo='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),564).toString()+"',"+
                                        "grafik_HAIs_laju_hap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),565).toString()+"',"+
                                        "inhealth_mapping_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),566).toString()+"',"+
                                        "inhealth_mapping_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),567).toString()+"',"+
                                        "inhealth_mapping_tindakan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),568).toString()+"',"+
                                        "inhealth_mapping_tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),569).toString()+"',"+
                                        "inhealth_mapping_tindakan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),570).toString()+"',"+
                                        "inhealth_mapping_tindakan_laborat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),571).toString()+"',"+
                                        "inhealth_mapping_tindakan_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),572).toString()+"',"+
                                        "hibah_obat_bhp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),573).toString()+"',"+
                                        "asal_hibah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),574).toString()+"',"+
                                        "asuhan_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),575).toString()+"',"+
                                        "inhealth_kirim_tagihan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),576).toString()+"',"+
                                        "sirkulasi_obat4='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),577).toString()+"',"+
                                        "sirkulasi_obat5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),578).toString()+"',"+
                                        "sirkulasi_non_medis2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),579).toString()+"',"+
                                        "monitoring_asuhan_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),580).toString()+"',"+
                                        "penerimaan_obat_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),581).toString()+"',"+
                                        "rekap_kunjungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),582).toString()+"',"+
                                        "surat_sakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),583).toString()+"',"+
                                        "penilaian_awal_keperawatan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),584).toString()+"',"+
                                        "permintaan_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),585).toString()+"',"+
                                        "master_masalah_keperawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),586).toString()+"',"+
                                        "pengajuan_cuti='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),587).toString()+"',"+
                                        "kedatangan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),588).toString()+"',"+
                                        "utd_pendonor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),589).toString()+"',"+
                                        "toko_suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),590).toString()+"',"+
                                        "toko_jenis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),591).toString()+"',"+
                                        "toko_set_harga='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),592).toString()+"',"+
                                        "toko_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),593).toString()+"',"+
                                        "penagihan_piutang_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),594).toString()+"',"+
                                        "akun_penagihan_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),595).toString()+"',"+
                                        "stok_opname_toko='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),596).toString()+"',"+
                                        "toko_riwayat_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),597).toString()+"',"+
                                        "toko_surat_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),598).toString()+"',"+
                                        "toko_pengajuan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),599).toString()+"',"+
                                        "toko_penerimaan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),600).toString()+"',"+
                                        "toko_pengadaan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),601).toString()+"',"+
                                        "toko_hutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),602).toString()+"',"+
                                        "toko_bayar_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),603).toString()+"',"+
                                        "toko_member='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),604).toString()+"',"+
                                        "toko_penjualan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),605).toString()+"',"+
                                        "registrasi_poli_per_tanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),606).toString()+"',"+
                                        "toko_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),607).toString()+"',"+
                                        "toko_retur_beli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),608).toString()+"',"+
                                        "ipsrs_returbeli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),609).toString()+"',"+
                                        "ipsrs_riwayat_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),610).toString()+"',"+
                                        "pasien_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),611).toString()+"',"+
                                        "toko_pendapatan_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),612).toString()+"',"+
                                        "diagnosa_pasien_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),613).toString()+"',"+
                                        "perawatan_pasien_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),614).toString()+"',"+
                                        "penilaian_awal_keperawatan_gigi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),615).toString()+"',"+
                                        "master_masalah_keperawatan_gigi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),616).toString()+"',"+
                                        "toko_bayar_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),617).toString()+"',"+
                                        "toko_piutang_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),618).toString()+"',"+
                                        "toko_penjualan_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),619).toString()+"',"+
                                        "deteksi_corona='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),620).toString()+"',"+
                                        "penilaian_awal_keperawatan_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),621).toString()+"',"+
                                        "pengumuman_epasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),622).toString()+"',"+
                                        "surat_hamil='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),623).toString()+"',"+
                                        "set_tarif_online='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),624).toString()+"',"+
                                        "booking_periksa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),625).toString()+"',"+
                                        "toko_sirkulasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),626).toString()+"',"+
                                        "toko_retur_jual='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),627).toString()+"',"+
                                        "toko_retur_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),628).toString()+"',"+
                                        "toko_sirkulasi2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),629).toString()+"',"+
                                        "toko_keuntungan_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),630).toString()+"',"+
                                        "zis_pengeluaran_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),631).toString()+"',"+
                                        "zis_penghasilan_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),632).toString()+"',"+
                                        "zis_ukuran_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),633).toString()+"',"+
                                        "zis_dinding_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),634).toString()+"',"+
                                        "zis_lantai_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),635).toString()+"',"+
                                        "zis_atap_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),636).toString()+"',"+
                                        "zis_kepemilikan_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),637).toString()+"',"+
                                        "zis_kamar_mandi_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),638).toString()+"',"+
                                        "zis_dapur_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),639).toString()+"',"+
                                        "zis_kursi_rumah_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),640).toString()+"',"+
                                        "zis_kategori_phbs_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),641).toString()+"',"+
                                        "zis_elektronik_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),642).toString()+"',"+
                                        "zis_ternak_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),643).toString()+"',"+
                                        "zis_jenis_simpanan_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),644).toString()+"',"+
                                        "penilaian_awal_keperawatan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),645).toString()+"',"+
                                        "zis_kategori_asnaf_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),646).toString()+"',"+
                                        "master_masalah_keperawatan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),647).toString()+"',"+
                                        "master_imunisasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),648).toString()+"',"+
                                        "zis_patologis_penerima_dankes='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),649).toString()+"',"+
                                        "pcare_cek_kartu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),650).toString()+"',"+
                                        "surat_bebas_narkoba='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),651).toString()+"',"+
                                        "surat_keterangan_covid='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),652).toString()+"',"+
                                        "pemakaian_air_tanah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),653).toString()+"',"+
                                        "grafik_air_tanah_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),654).toString()+"',"+
                                        "grafik_air_tanah_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),655).toString()+"',"+
                                        "lama_pelayanan_poli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),656).toString()+"',"+
                                        "hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),657).toString()+"',"+
                                        "laporan_tahunan_irj='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),658).toString()+"',"+
                                        "grafik_harian_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),659).toString()+"',"+
                                        "grafik_bulanan_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),660).toString()+"',"+
                                        "grafik_tahunan_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),661).toString()+"',"+
                                        "grafik_bulanan_meninggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),662).toString()+"',"+
                                        "perbaikan_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),663).toString()+"',"+
                                        "surat_cuti_hamil='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),664).toString()+"',"+
                                        "permintaan_stok_obat_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),665).toString()+"',"+
                                        "pemeliharaan_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),666).toString()+"',"+
                                        "klasifikasi_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),667).toString()+"',"+
                                        "bulanan_klasifikasi_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),668).toString()+"',"+
                                        "harian_klasifikasi_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),669).toString()+"',"+
                                        "klasifikasi_pasien_perbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),670).toString()+"',"+
                                        "soap_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),671).toString()+"',"+
                                        "klaim_rawat_jalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),672).toString()+"',"+
                                        "skrining_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),673).toString()+"',"+
                                        "lama_penyiapan_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),674).toString()+"',"+
                                        "dosis_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),675).toString()+"',"+
                                        "demografi_umur_kunjungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),676).toString()+"',"+
                                        "jam_diet_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),677).toString()+"',"+
                                        "rvu_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),678).toString()+"',"+
                                        "verifikasi_penerimaan_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),679).toString()+"',"+
                                        "verifikasi_penerimaan_logistik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),680).toString()+"',"+
                                        "pemeriksaan_lab_pa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),681).toString()+"',"+
                                        "ringkasan_pengajuan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),682).toString()+"',"+
                                        "ringkasan_pemesanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),683).toString()+"',"+
                                        "ringkasan_pengadaan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),684).toString()+"',"+
                                        "ringkasan_penerimaan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),685).toString()+"',"+
                                        "ringkasan_hibah_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),686).toString()+"',"+
                                        "ringkasan_penjualan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),687).toString()+"',"+
                                        "ringkasan_beri_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),688).toString()+"',"+
                                        "ringkasan_piutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),689).toString()+"',"+
                                        "ringkasan_stok_keluar_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),690).toString()+"',"+
                                        "ringkasan_retur_suplier_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),691).toString()+"',"+
                                        "ringkasan_retur_pembeli_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),692).toString()+"',"+
                                        "penilaian_awal_keperawatan_ranapkebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),693).toString()+"',"+
                                        "ringkasan_pengajuan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),694).toString()+"',"+
                                        "ringkasan_pemesanan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),695).toString()+"',"+
                                        "ringkasan_pengadaan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),696).toString()+"',"+
                                        "ringkasan_penerimaan_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),697).toString()+"',"+
                                        "ringkasan_stokkeluar_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),698).toString()+"',"+
                                        "ringkasan_returbeli_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),699).toString()+"',"+
                                        "omset_penerimaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),700).toString()+"',"+
                                        "validasi_penagihan_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),701).toString()+"',"+
                                        "permintaan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),702).toString()+"',"+
                                        "bpjs_diagnosa_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),703).toString()+"',"+
                                        "bpjs_obat_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),704).toString()+"',"+
                                        "bpjs_surat_kontrol='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),705).toString()+"',"+
                                        "penggunaan_bhp_ok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),706).toString()+"',"+
                                        "surat_keterangan_rawat_inap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),707).toString()+"',"+
                                        "surat_keterangan_sehat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),708).toString()+"',"+
                                        "pendapatan_per_carabayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),709).toString()+"',"+
                                        "akun_host_to_host_bank_jateng='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),710).toString()+"',"+
                                        "pembayaran_bank_jateng='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),711).toString()+"',"+
                                        "bpjs_surat_pri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),712).toString()+"',"+
                                        "ringkasan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),713).toString()+"',"+
                                        "lama_pelayanan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),714).toString()+"',"+
                                        "surat_sakit_pihak_2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),715).toString()+"',"+
                                        "tagihan_hutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),716).toString()+"',"+
                                        "referensi_mobilejkn_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),717).toString()+"',"+
                                        "batal_pendaftaran_mobilejkn_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),718).toString()+"',"+
                                        "lama_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),719).toString()+"',"+
                                        "grafik_inventaris_kategori='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),720).toString()+"',"+
                                        "grafik_inventaris_merk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),721).toString()+"',"+
                                        "grafik_inventaris_produsen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),722).toString()+"',"+
                                        "pengembalian_deposit_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),723).toString()+"',"+
                                        "validasi_tagihan_hutang_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),724).toString()+"',"+
                                        "piutang_obat_belum_lunas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),725).toString()+"',"+
                                        "integrasi_briapi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),726).toString()+"',"+
                                        "pengadaan_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),727).toString()+"',"+
                                        "akun_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),728).toString()+"',"+
                                        "suplier_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),729).toString()+"',"+
                                        "penerimaan_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),730).toString()+"',"+
                                        "bayar_pemesanan_iventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),731).toString()+"',"+
                                        "hutang_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),732).toString()+"',"+
                                        "hibah_aset_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),733).toString()+"',"+
                                        "titip_faktur_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),734).toString()+"',"+
                                        "validasi_tagihan_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),735).toString()+"',"+
                                        "titip_faktur_aset='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),736).toString()+"',"+
                                        "validasi_tagihan_aset='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),737).toString()+"',"+
                                        "hibah_non_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),738).toString()+"',"+
                                        "pcare_alasan_tacc='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),739).toString()+"',"+
                                        "resep_luar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),740).toString()+"',"+
                                        "surat_bebas_tbc='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),741).toString()+"',"+
                                        "surat_buta_warna='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),742).toString()+"',"+
                                        "surat_bebas_tato='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),743).toString()+"',"+
                                        "surat_kewaspadaan_kesehatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),744).toString()+"',"+
                                        "grafik_porsidiet_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),745).toString()+"',"+
                                        "grafik_porsidiet_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),746).toString()+"',"+
                                        "grafik_porsidiet_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),747).toString()+"',"+
                                        "grafik_porsidiet_perbangsal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),748).toString()+"',"+
                                        "penilaian_awal_medis_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),749).toString()+"',"+
                                        "master_masalah_keperawatan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),750).toString()+"',"+
                                        "penilaian_awal_keperawatan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),751).toString()+"',"+
                                        "penilaian_awal_medis_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),752).toString()+"',"+
                                        "penilaian_awal_medis_ranap_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),753).toString()+"',"+
                                        "penilaian_awal_medis_ralan_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),754).toString()+"',"+
                                        "penilaian_awal_medis_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),755).toString()+"',"+
                                        "penilaian_awal_medis_ralan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),756).toString()+"',"+
                                        "bpjs_referensi_poli_hfis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),757).toString()+"',"+
                                        "bpjs_referensi_dokter_hfis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),758).toString()+"',"+
                                        "bpjs_referensi_jadwal_hfis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),759).toString()+"',"+
                                        "penilaian_fisioterapi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),760).toString()+"',"+
                                        "bpjs_program_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),761).toString()+"',"+
                                        "bpjs_suplesi_jasaraharja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),762).toString()+"',"+
                                        "bpjs_data_induk_kecelakaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),763).toString()+"',"+
                                        "bpjs_sep_internal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),764).toString()+"',"+
                                        "bpjs_klaim_jasa_raharja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),765).toString()+"',"+
                                        "bpjs_daftar_finger_print='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),766).toString()+"',"+
                                        "bpjs_rujukan_khusus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),767).toString()+"',"+
                                        "pemeliharaan_gedung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),768).toString()+"',"+
                                        "grafik_perbaikan_inventaris_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),769).toString()+"',"+
                                        "grafik_perbaikan_inventaris_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),770).toString()+"',"+
                                        "grafik_perbaikan_inventaris_pertahun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),771).toString()+"',"+
                                        "grafik_perbaikan_inventaris_perpelaksana_status='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),772).toString()+"',"+
                                        "penilaian_mcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),773).toString()+"',"+
                                        "peminjam_piutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),774).toString()+"',"+
                                        "piutang_lainlain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),775).toString()+"',"+
                                        "cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),776).toString()+"',"+
                                        "audit_kepatuhan_apd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),777).toString()+"',"+
                                        "bpjs_task_id='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),778).toString()+"',"+
                                        "bayar_piutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),779).toString()+"',"+
                                        "pembayaran_akun_bayar4='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),780).toString()+"',"+
                                        "stok_akhir_farmasi_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),781).toString()+"',"+
                                        "riwayat_kamar_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),782).toString()+"',"+
                                        "uji_fungsi_kfr='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),783).toString()+"',"+
                                        "hapus_berkas_digital_perawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),784).toString()+"',"+
                                        "kategori_pengeluaran_harian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),785).toString()+"',"+
                                        "kategori_pemasukan_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),786).toString()+"',"+
                                        "pembayaran_akun_bayar5='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),787).toString()+"',"+
                                        "ruang_ok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),788).toString()+"',"+
                                        "telaah_resep='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),789).toString()+"',"+
                                        "jasa_tindakan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),790).toString()+"',"+
                                        "permintaan_resep_pulang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),791).toString()+"',"+
                                        "rekap_jm_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),792).toString()+"',"+
                                        "status_data_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),793).toString()+"',"+
                                        "ubah_petugas_lab_pk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),794).toString()+"',"+
                                        "ubah_petugas_lab_pa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),795).toString()+"',"+
                                        "ubah_petugas_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),796).toString()+"',"+
                                        "gabung_norawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),797).toString()+"',"+
                                        "gabung_rm='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),798).toString()+"',"+
                                        "ringkasan_biaya_obat_pasien_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),799).toString()+"',"+
                                        "master_masalah_keperawatan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),800).toString()+"',"+
                                        "penilaian_awal_keperawatan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),801).toString()+"',"+
                                        "bpjs_referensi_dpho_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),802).toString()+"',"+
                                        "bpjs_referensi_poli_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),803).toString()+"',"+
                                        "bayar_jm_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),804).toString()+"',"+
                                        "bpjs_referensi_faskes_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),805).toString()+"',"+
                                        "bpjs_referensi_spesialistik_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),806).toString()+"',"+
                                        "pembayaran_briva='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),807).toString()+"',"+
                                        "penilaian_awal_keperawatan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),808).toString()+"',"+
                                        "nilai_penerimaan_vendor_farmasi_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),809).toString()+"',"+
                                        "akun_bayar_hutang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),810).toString()+"',"+
                                        "master_rencana_keperawatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),811).toString()+"',"+
                                        "laporan_tahunan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),812).toString()+"',"+
                                        "obat_bhp_tidakbergerak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),813).toString()+"',"+
                                        "ringkasan_hutang_vendor_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),814).toString()+"',"+
                                        "nilai_penerimaan_vendor_nonmedis_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),815).toString()+"',"+
                                        "ringkasan_hutang_vendor_nonmedis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),816).toString()+"',"+
                                        "master_rencana_keperawatan_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),817).toString()+"',"+
                                        "anggota_polri_dirawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),818).toString()+"',"+
                                        "daftar_pasien_ranap_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),819).toString()+"',"+
                                        "soap_ralan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),820).toString()+"',"+
                                        "soap_ranap_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),821).toString()+"',"+
                                        "laporan_penyakit_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),822).toString()+"',"+
                                        "jumlah_pengunjung_ralan_polri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),823).toString()+"',"+
                                        "catatan_observasi_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),824).toString()+"',"+
                                        "catatan_observasi_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),825).toString()+"',"+
                                        "catatan_observasi_ranap_kebidanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),826).toString()+"',"+
                                        "catatan_observasi_ranap_postpartum='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),827).toString()+"',"+
                                        "penilaian_awal_medis_ralan_tht='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),828).toString()+"',"+
                                        "penilaian_psikologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),829).toString()+"',"+
                                        "audit_cuci_tangan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),830).toString()+"',"+
                                        "audit_pembuangan_limbah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),831).toString()+"',"+
                                        "ruang_audit_kepatuhan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),832).toString()+"',"+
                                        "audit_pembuangan_benda_tajam='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),833).toString()+"',"+
                                        "audit_penanganan_darah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),834).toString()+"',"+
                                        "audit_pengelolaan_linen_kotor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),835).toString()+"',"+
                                        "audit_penempatan_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),836).toString()+"',"+
                                        "audit_kamar_jenazah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),837).toString()+"',"+
                                        "audit_bundle_iadp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),838).toString()+"',"+
                                        "audit_bundle_ido='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),839).toString()+"',"+
                                        "audit_fasilitas_kebersihan_tangan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),840).toString()+"',"+
                                        "audit_fasilitas_apd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),841).toString()+"',"+
                                        "audit_pembuangan_limbah_cair_infeksius='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),842).toString()+"',"+
                                        "audit_sterilisasi_alat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),843).toString()+"',"+
                                        "penilaian_awal_medis_ralan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),844).toString()+"',"+
                                        "persetujuan_penolakan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),845).toString()+"',"+
                                        "audit_bundle_isk='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),846).toString()+"',"+
                                        "audit_bundle_plabsi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),847).toString()+"',"+
                                        "audit_bundle_vap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),848).toString()+"',"+
                                        "akun_host_to_host_bank_papua='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),849).toString()+"',"+
                                        "pembayaran_bank_papua='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),850).toString()+"',"+
                                        "penilaian_awal_medis_ralan_penyakit_dalam='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),851).toString()+"',"+
                                        "penilaian_awal_medis_ralan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),852).toString()+"',"+
                                        "penilaian_awal_medis_ralan_neurologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),853).toString()+"',"+
                                        "sirkulasi_obat6='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),854).toString()+"',"+
                                        "penilaian_awal_medis_ralan_orthopedi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),855).toString()+"',"+
                                        "penilaian_awal_medis_ralan_bedah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),856).toString()+"',"+
                                        "integrasi_khanza_health_services='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),857).toString()+"',"+
                                        "soap_ralan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),858).toString()+"',"+
                                        "soap_ranap_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),859).toString()+"',"+
                                        "jumlah_pengunjung_ralan_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),860).toString()+"',"+
                                        "laporan_penyakit_tni='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),861).toString()+"',"+
                                        "catatan_keperawatan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),862).toString()+"',"+
                                        "master_rencana_keperawatan_gigi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),863).toString()+"',"+
                                        "master_rencana_keperawatan_mata='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),864).toString()+"',"+
                                        "master_rencana_keperawatan_igd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),865).toString()+"',"+
                                        "master_masalah_keperawatan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),866).toString()+"',"+
                                        "master_rencana_keperawatan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),867).toString()+"',"+
                                        "penilaian_awal_keperawatan_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),868).toString()+"',"+
                                        "pemantauan_pews_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),869).toString()+"',"+
                                        "surat_pulang_atas_permintaan_sendiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),870).toString()+"',"+
                                        "template_hasil_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),871).toString()+"',"+
                                        "laporan_bulanan_irj='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),872).toString()+"',"+
                                        "template_pemeriksaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),873).toString()+"',"+
                                        "pemeriksaan_lab_mb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),874).toString()+"',"+
                                        "ubah_petugas_lab_mb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),875).toString()+"',"+
                                        "penilaian_pre_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),876).toString()+"',"+
                                        "penilaian_pre_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),877).toString()+"',"+
                                        "perencanaan_pemulangan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),878).toString()+"',"+
                                        "penilaian_lanjutan_resiko_jatuh_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),879).toString()+"',"+
                                        "penilaian_lanjutan_resiko_jatuh_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),880).toString()+"',"+
                                        "penilaian_awal_medis_ralan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),881).toString()+"',"+
                                        "penilaian_tambahan_pasien_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),882).toString()+"',"+
                                        "skrining_nutrisi_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),883).toString()+"',"+
                                        "skrining_nutrisi_lansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),884).toString()+"',"+
                                        "hasil_pemeriksaan_usg='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),885).toString()+"',"+
                                        "skrining_nutrisi_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),886).toString()+"',"+
                                        "akun_host_to_host_bank_jabar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),887).toString()+"',"+
                                        "pembayaran_bank_jabar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),888).toString()+"',"+
                                        "surat_pernyataan_pasien_umum='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),889).toString()+"',"+
                                        "konseling_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),890).toString()+"',"+
                                        "pelayanan_informasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),891).toString()+"',"+
                                        "jawaban_pio_apoteker='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),892).toString()+"',"+
                                        "surat_persetujuan_umum='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),893).toString()+"',"+
                                        "transfer_pasien_antar_ruang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),894).toString()+"',"+
                                        "satu_sehat_referensi_dokter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),895).toString()+"',"+
                                        "satu_sehat_referensi_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),896).toString()+"',"+
                                        "satu_sehat_mapping_departemen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),897).toString()+"',"+
                                        "satu_sehat_mapping_lokasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),898).toString()+"',"+
                                        "satu_sehat_kirim_encounter='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),899).toString()+"',"+
                                        "catatan_cek_gds='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),900).toString()+"',"+
                                        "satu_sehat_kirim_condition='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),901).toString()+"',"+
                                        "checklist_pre_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),902).toString()+"',"+
                                        "satu_sehat_kirim_observationttv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),903).toString()+"',"+
                                        "signin_sebelum_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),904).toString()+"',"+
                                        "satu_sehat_kirim_procedure='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),905).toString()+"',"+
                                        "operasi_per_bulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),906).toString()+"',"+
                                        "timeout_sebelum_insisi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),907).toString()+"',"+
                                        "signout_sebelum_menutup_luka='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),908).toString()+"',"+
                                        "dapur_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),909).toString()+"',"+
                                        "dapur_opname='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),910).toString()+"',"+
                                        "satu_sehat_mapping_vaksin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),911).toString()+"',"+
                                        "dapur_suplier='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),912).toString()+"',"+
                                        "satu_sehat_kirim_Immunization='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),913).toString()+"',"+
                                        "checklist_post_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),914).toString()+"',"+
                                        "dapur_pembelian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),915).toString()+"',"+
                                        "dapur_stok_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),916).toString()+"',"+
                                        "dapur_riwayat_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),917).toString()+"',"+
                                        "permintaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),918).toString()+"',"+
                                        "rekonsiliasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),919).toString()+"',"+
                                        "biaya_pengadaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),920).toString()+"',"+
                                        "rekap_pengadaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),921).toString()+"',"+
                                        "kesling_limbah_b3medis_cair='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),922).toString()+"',"+
                                        "grafik_limbahb3cair_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),923).toString()+"',"+
                                        "grafik_limbahb3cair_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),924).toString()+"',"+
                                        "rekap_biaya_registrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),925).toString()+"',"+
                                        "konfirmasi_rekonsiliasi_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),926).toString()+"',"+
                                        "satu_sehat_kirim_clinicalimpression='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),927).toString()+"',"+
                                        "penilaian_pasien_terminal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),928).toString()+"',"+
                                        "surat_persetujuan_rawat_inap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),929).toString()+"',"+
                                        "monitoring_reaksi_tranfusi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),930).toString()+"',"+
                                        "penilaian_korban_kekerasan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),931).toString()+"',"+
                                        "penilaian_lanjutan_resiko_jatuh_lansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),932).toString()+"',"+
                                        "penilaian_pasien_penyakit_menular='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),933).toString()+"',"+
                                        "mpp_skrining='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),934).toString()+"',"+
                                        "edukasi_pasien_keluarga_rj='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),935).toString()+"',"+
                                        "pemantauan_pews_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),936).toString()+"',"+
                                        "penilaian_tambahan_bunuh_diri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),937).toString()+"',"+
                                        "bpjs_antrean_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),938).toString()+"',"+
                                        "penilaian_tambahan_perilaku_kekerasan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),939).toString()+"',"+
                                        "penilaian_tambahan_beresiko_melarikan_diri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),940).toString()+"',"+
                                        "persetujuan_penundaan_pelayanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),941).toString()+"',"+
                                        "sisa_diet_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),942).toString()+"',"+
                                        "penilaian_awal_medis_ralan_bedah_mulut='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),943).toString()+"',"+
                                        "penilaian_pasien_keracunan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),944).toString()+"',"+
                                        "pemantauan_meows_obstetri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),945).toString()+"',"+
                                        "catatan_adime_gizi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),946).toString()+"',"+
                                        "pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),947).toString()+"',"+
                                        "penilaian_awal_keperawatan_ralan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),948).toString()+"',"+
                                        "master_masalah_keperawatan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),949).toString()+"',"+
                                        "master_rencana_keperawatan_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),950).toString()+"',"+
                                        "checklist_kriteria_masuk_hcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),951).toString()+"',"+
                                        "checklist_kriteria_keluar_hcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),952).toString()+"',"+
                                        "penilaian_risiko_dekubitus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),953).toString()+"',"+
                                        "master_menolak_anjuran_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),954).toString()+"',"+
                                        "penolakan_anjuran_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),955).toString()+"',"+
                                        "laporan_tahunan_penolakan_anjuran_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),956).toString()+"',"+
                                        "template_laporan_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),957).toString()+"',"+
                                        "hasil_tindakan_eswl='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),958).toString()+"',"+
                                        "checklist_kriteria_masuk_icu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),959).toString()+"',"+
                                        "checklist_kriteria_keluar_icu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),960).toString()+"',"+
                                        "akses_dokter_lain_rawat_jalan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),961).toString()+"',"+
                                        "follow_up_dbd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),962).toString()+"',"+
                                        "penilaian_risiko_jatuh_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),963).toString()+"',"+
                                        "persetujuan_pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),964).toString()+"',"+
                                        "pemeriksaan_fisik_ralan_per_penyakit='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),965).toString()+"',"+
                                        "penilaian_lanjutan_resiko_jatuh_geriatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),966).toString()+"',"+
                                        "pemantauan_ews_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),967).toString()+"',"+
                                        "validasi_persetujuan_pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),968).toString()+"',"+
                                        "riwayat_perawatan_icare_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),969).toString()+"',"+
                                        "rekap_pengajuan_biaya='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),970).toString()+"',"+
                                        "penilaian_awal_medis_ralan_kulit_kelamin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),971).toString()+"',"+
                                        "akun_host_to_host_bank_mandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),972).toString()+"',"+
                                        "penilaian_medis_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),973).toString()+"',"+
                                        "penilaian_level_kecemasan_ranap_anak='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),974).toString()+"',"+
                                        "penilaian_lanjutan_resiko_jatuh_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),975).toString()+"',"+
                                        "penilaian_lanjutan_skrining_fungsional='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),976).toString()+"',"+
                                        "penilaian_medis_ralan_rehab_medik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),977).toString()+"',"+
                                        "laporan_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),978).toString()+"',"+
                                        "template_persetujuan_penolakan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),979).toString()+"',"+
                                        "penilaian_medis_ralan_gawat_darurat_psikiatri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),980).toString()+"',"+
                                        "bpjs_referensi_setting_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),981).toString()+"',"+
                                        "bpjs_referensi_obat_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),982).toString()+"',"+
                                        "bpjs_mapping_obat_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),983).toString()+"',"+
                                        "pembayaran_bank_mandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),984).toString()+"',"+
                                        "penilaian_ulang_nyeri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),985).toString()+"',"+
                                        "penilaian_terapi_wicara='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),986).toString()+"',"+
                                        "bpjs_obat_23hari_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),987).toString()+"',"+
                                        "pengkajian_restrain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),988).toString()+"',"+
                                        "bpjs_kunjungan_sep_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),989).toString()+"',"+
                                        "bpjs_monitoring_klaim_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),990).toString()+"',"+
                                        "bpjs_daftar_pelayanan_obat_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),991).toString()+"',"+
                                        "penilaian_awal_medis_ralan_paru='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),992).toString()+"',"+
                                        "catatan_keperawatan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),993).toString()+"',"+
                                        "catatan_persalinan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),994).toString()+"',"+
                                        "skor_aldrette_pasca_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),995).toString()+"',"+
                                        "skor_steward_pasca_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),996).toString()+"',"+
                                        "skor_bromage_pasca_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),997).toString()+"',"+
                                        "penilaian_pre_induksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),998).toString()+"',"+
                                        "hasil_usg_urologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),999).toString()+"',"+
                                        "hasil_usg_gynecologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1000).toString()+"',"+
                                        "hasil_pemeriksaan_ekg='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1001).toString()+"',"+
                                        "hapus_edit_sep_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1002).toString()+"',"+
                                        "satu_sehat_kirim_diet='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1003).toString()+"',"+
                                        "satu_sehat_mapping_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1004).toString()+"',"+
                                        "dapur_ringkasan_pembelian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1005).toString()+"',"+
                                        "satu_sehat_kirim_medication='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1006).toString()+"',"+
                                        "satu_sehat_kirim_medicationrequest='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1007).toString()+"',"+
                                        "penatalaksanaan_terapi_okupasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1008).toString()+"',"+
                                        "satu_sehat_kirim_medicationdispense='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1009).toString()+"',"+
                                        "hasil_usg_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1010).toString()+"',"+
                                        "hasil_endoskopi_faring_laring='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1011).toString()+"',"+
                                        "satu_sehat_mapping_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1012).toString()+"',"+
                                        "satu_sehat_kirim_servicerequest_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1013).toString()+"',"+
                                        "hasil_endoskopi_hidung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1014).toString()+"',"+
                                        "satu_sehat_kirim_specimen_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1015).toString()+"',"+
                                        "master_masalah_keperawatan_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1016).toString()+"',"+
                                        "master_rencana_keperawatan_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1017).toString()+"',"+
                                        "penilaian_awal_keperawatan_ranap_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1018).toString()+"',"+
                                        "satu_sehat_kirim_observation_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1019).toString()+"',"+
                                        "satu_sehat_kirim_diagnosticreport_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1020).toString()+"',"+
                                        "hasil_endoskopi_telinga='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1021).toString()+"',"+
                                        "satu_sehat_mapping_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1022).toString()+"',"+
                                        "satu_sehat_kirim_servicerequest_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1023).toString()+"',"+
                                        "satu_sehat_kirim_servicerequest_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1024).toString()+"',"+
                                        "satu_sehat_kirim_specimen_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1025).toString()+"',"+
                                        "satu_sehat_kirim_specimen_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1026).toString()+"',"+
                                        "satu_sehat_kirim_observation_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1027).toString()+"',"+
                                        "satu_sehat_kirim_observation_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1028).toString()+"',"+
                                        "satu_sehat_kirim_diagnosticreport_lab='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1029).toString()+"',"+
                                        "satu_sehat_kirim_diagnosticreport_labmb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1030).toString()+"',"+
                                        "kepatuhan_kelengkapan_keselamatan_bedah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1031).toString()+"',"+
                                        "nilai_piutang_perjenis_bayar_per_bulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1032).toString()+"',"+
                                        "ringkasan_piutang_jenis_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1033).toString()+"',"+
                                        "penilaian_pasien_imunitas_rendah='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1034).toString()+"',"+
                                        "balance_cairan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1035).toString()+"',"+
                                        "catatan_observasi_chbp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1036).toString()+"',"+
                                        "catatan_observasi_induksi_persalinan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1037).toString()+"',"+
                                        "skp_kategori_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1038).toString()+"',"+
                                        "skp_kriteria_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1039).toString()+"',"+
                                        "skp_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1040).toString()+"',"+
                                        "referensi_poli_mobilejknfktp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1041).toString()+"',"+
                                        "referensi_dokter_mobilejknfktp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1042).toString()+"',"+
                                        "skp_rekapitulasi_penilaian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1043).toString()+"',"+
                                        "pembayaran_pihak_ke3_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1044).toString()+"',"+
                                        "metode_pembayaran_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1045).toString()+"',"+
                                        "bank_tujuan_transfer_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1046).toString()+"',"+
                                        "kodetransaksi_tujuan_transfer_bankmandiri='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1047).toString()+"',"+
                                        "konsultasi_medik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1048).toString()+"',"+
                                        "jawaban_konsultasi_medik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1049).toString()+"',"+
                                        "pcare_cek_alergi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1050).toString()+"',"+
                                        "pcare_cek_prognosa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1051).toString()+"',"+
                                        "data_sasaran_usiaproduktif='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1052).toString()+"',"+
                                        "data_sasaran_usialansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1053).toString()+"',"+
                                        "skrining_perilaku_merokok_sekolah_remaja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1054).toString()+"',"+
                                        "skrining_kekerasan_pada_perempuan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1055).toString()+"',"+
                                        "skrining_obesitas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1056).toString()+"',"+
                                        "skrining_risiko_kanker_payudara='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1057).toString()+"',"+
                                        "skrining_risiko_kanker_paru='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1058).toString()+"',"+
                                        "skrining_tbc='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1059).toString()+"',"+
                                        "skrining_kesehatan_gigi_mulut_remaja='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1060).toString()+"',"+
                                        "penilaian_awal_keperawatan_ranap_bayi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1061).toString()+"',"+
                                        "booking_mcu_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1062).toString()+"',"+
                                        "catatan_observasi_restrain_nonfarma='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1063).toString()+"',"+
                                        "catatan_observasi_ventilator='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1064).toString()+"',"+
                                        "catatan_anestesi_sedasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1065).toString()+"',"+
                                        "skrining_puma='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1066).toString()+"',"+
                                        "satu_sehat_kirim_careplan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1067).toString()+"',"+
                                        "satu_sehat_kirim_medicationstatement='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1068).toString()+"',"+
                                        "skrining_adiksi_nikotin='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1069).toString()+"',"+
                                        "skrining_thalassemia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1070).toString()+"',"+
                                        "skrining_instrumen_sdq='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1071).toString()+"',"+
                                        "skrining_instrumen_srq='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1072).toString()+"',"+
                                        "checklist_pemberian_fibrinolitik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1073).toString()+"',"+
                                        "skrining_kanker_kolorektal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1074).toString()+"',"+
                                        "dapur_pemesanan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1075).toString()+"',"+
                                        "bayar_pesan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1076).toString()+"',"+
                                        "hutang_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1077).toString()+"',"+
                                        "titip_faktur_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1078).toString()+"',"+
                                        "validasi_tagihan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1079).toString()+"',"+
                                        "surat_pemesanan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1080).toString()+"',"+
                                        "pengajuan_barang_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1081).toString()+"',"+
                                        "dapur_returbeli='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1082).toString()+"',"+
                                        "hibah_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1083).toString()+"',"+
                                        "ringkasan_penerimaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1084).toString()+"',"+
                                        "ringkasan_pengajuan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1085).toString()+"',"+
                                        "ringkasan_pemesanan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1086).toString()+"',"+
                                        "ringkasan_returbeli_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1087).toString()+"',"+
                                        "ringkasan_stokkeluar_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1088).toString()+"',"+
                                        "dapur_stokkeluar_pertanggal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1089).toString()+"',"+
                                        "sirkulasi_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1090).toString()+"',"+
                                        "sirkulasi_dapur2='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1091).toString()+"',"+
                                        "verifikasi_penerimaan_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1092).toString()+"',"+
                                        "nilai_penerimaan_vendor_dapur_perbulan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1093).toString()+"',"+
                                        "ringkasan_hutang_vendor_dapur='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1094).toString()+"',"+
                                        "penilaian_psikologi_klinis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1095).toString()+"',"+
                                        "penilaian_awal_medis_ranap_neonatus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1096).toString()+"',"+
                                        "penilaian_derajat_dehidrasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1097).toString()+"',"+
                                        "ringkasan_jasa_tindakan_medis='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1098).toString()+"',"+
                                        "pendapatan_per_akun='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1099).toString()+"',"+
                                        "hasil_pemeriksaan_echo='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1100).toString()+"',"+
                                        "penilaian_bayi_baru_lahir='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1101).toString()+"',"+
                                        "rl1_3_ketersediaan_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1102).toString()+"',"+
                                        "pendapatan_per_akun_closing='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1103).toString()+"',"+
                                        "pengeluaran_pengeluaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1104).toString()+"',"+
                                        "skrining_diabetes_melitus='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1105).toString()+"',"+
                                        "laporan_tindakan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1106).toString()+"',"+
                                        "pelaksanaan_informasi_edukasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1107).toString()+"',"+
                                        "layanan_kedokteran_fisik_rehabilitasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1108).toString()+"',"+
                                        "skrining_kesehatan_gigi_mulut_balita='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1109).toString()+"',"+
                                        "skrining_anemia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1110).toString()+"',"+
                                        "layanan_program_kfr='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1111).toString()+"',"+
                                        "skrining_hipertensi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1112).toString()+"',"+
                                        "skrining_kesehatan_penglihatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1113).toString()+"',"+
                                        "catatan_observasi_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1114).toString()+"',"+
                                        "skrining_kesehatan_gigi_mulut_dewasa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1115).toString()+"',"+
                                        "skrining_risiko_kanker_serviks='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1116).toString()+"',"+
                                        "catatan_cairan_hemodialisa='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1117).toString()+"',"+
                                        "skrining_kesehatan_gigi_mulut_lansia='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1118).toString()+"',"+
                                        "skrining_indra_pendengaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1119).toString()+"',"+
                                        "catatan_pengkajian_paska_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1120).toString()+"',"+
                                        "skrining_frailty_syndrome='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1121).toString()+"',"+
                                        "sirkulasi_cssd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1122).toString()+"',"+
                                        "lama_pelayanan_cssd='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1123).toString()+"',"+
                                        "catatan_observasi_bayi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1124).toString()+"',"+
                                        "riwayat_surat_peringatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1125).toString()+"',"+
                                        "master_kesimpulan_anjuran_mcu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1126).toString()+"',"+
                                        "kategori_piutang_jasa_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1127).toString()+"',"+
                                        "piutang_jasa_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1128).toString()+"',"+
                                        "bayar_piutang_jasa_perusahaan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1129).toString()+"',"+
                                        "piutang_jasa_perusahaan_belum_lunas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1130).toString()+"',"+
                                        "checklist_kesiapan_anestesi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1131).toString()+"',"+
                                        "piutang_peminjaman_uang_belum_lunas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1132).toString()+"',"+
                                        "hasil_pemeriksaan_slit_lamp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1133).toString()+"',"+
                                        "hasil_pemeriksaan_oct='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1134).toString()+"',"+
                                        "beban_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1135).toString()+"',"+
                                        "poli_asal_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1136).toString()+"',"+
                                        "pemberi_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1137).toString()+"',"+
                                        "dokter_asal_pasien_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1138).toString()+"',"+
                                        "duta_parkir_rekap_keluar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1139).toString()+"',"+
                                        "surat_keterangan_layak_terbang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1140).toString()+"',"+
                                        "bayar_beban_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1141).toString()+"',"+
                                        "surat_persetujuan_pemeriksaan_hiv='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1142).toString()+"',"+
                                        "skrining_instrumen_acrs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1143).toString()+"',"+
                                        "surat_pernyataan_memilih_dpjp='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1144).toString()+"',"+
                                        "skrining_instrumen_mental_emosional='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1145).toString()+"',"+
                                        "pelanggan_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1146).toString()+"',"+
                                        "kriteria_masuk_nicu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1147).toString()+"',"+
                                        "kriteria_keluar_nicu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1148).toString()+"',"+
                                        "penilaian_medis_ranap_psikiatrik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1149).toString()+"',"+
                                        "kriteria_masuk_picu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1150).toString()+"',"+
                                        "kriteria_keluar_picu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1151).toString()+"',"+
                                        "master_sampel_bakumutu='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1152).toString()+"',"+
                                        "skrining_instrumen_amt='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1153).toString()+"',"+
                                        "parameter_pengujian_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1154).toString()+"',"+
                                        "nilai_normal_baku_mutu_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1155).toString()+"',"+
                                        "skrining_pneumonia_severity_index='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1156).toString()+"',"+
                                        "permintaan_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1157).toString()+"',"+
                                        "penilaian_awal_medis_ralan_jantung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1158).toString()+"',"+
                                        "penilaian_awal_medis_ralan_urologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1159).toString()+"',"+
                                        "hasil_pemeriksaan_treadmill='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1160).toString()+"',"+
                                        "hasil_pemeriksaan_echo_pediatrik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1161).toString()+"',"+
                                        "template_pelaksanaan_informasi_edukasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1162).toString()+"',"+
                                        "skrining_instrumen_esat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1163).toString()+"',"+
                                        "penilaian_awal_medis_ranap_jantung='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1164).toString()+"',"+
                                        "penugasan_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1165).toString()+"',"+
                                        "hasil_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1166).toString()+"',"+
                                        "verifikasi_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1167).toString()+"',"+
                                        "validasi_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1168).toString()+"',"+
                                        "rekap_pelayanan_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1169).toString()+"',"+
                                        "pembayaran_pengujian_sampel_lab_kesehatan_lingkungan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1170).toString()+"',"+
                                        "skrining_curb65='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1171).toString()+"',"+
                                        "bpjs_potensi_prb='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1172).toString()+"',"+
                                        "bpjs_riwayat_pelayanan_obat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1173).toString()+"',"+
                                        "skrining_gizi_kehamilan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1174).toString()+"',"+
                                        "bpjs_rekap_peserta_prb_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1175).toString()+"',"+
                                        "serah_terima_anggota_tubuh_barang='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1176).toString()+"',"+
                                        "pcra_icra_jenis_aktivitas_proyek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1177).toString()+"',"+
                                        "pcra_icra_lokasi_kelompok_risiko_area='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1178).toString()+"',"+
                                        "pcra_icra_kelas_risiko_pencegahan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1179).toString()+"',"+
                                        "pcra_icra_tindakan_pengendalian='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1180).toString()+"',"+
                                        "pcra_icra_identifkasi_risiko_infeksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1181).toString()+"',"+
                                        "pcra_icra_identifkasi_risiko_keselamatan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1182).toString()+"',"+
                                        "pcra_icra_identifkasi_risiko_kebakaran='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1183).toString()+"',"+
                                        "pcra_icra_identifkasi_risiko_utilitas='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1184).toString()+"',"+
                                        "bpjs_daftar_resep_apotek='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1185).toString()+"',"+
                                        "daftar_permintaan_resep_iterasi_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1186).toString()+"',"+
                                        "pcra_icra_pengkajian_risiko_prakonstruksi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1187).toString()+"',"+
                                        "pcra_icra_persyaratan_harus_dipenuhi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1188).toString()+"',"+
                                        "satu_sehat_kirim_questionresponse_telaah_farmasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1189).toString()+"',"+
                                        "satu_sehat_kirim_allergy_intolerance='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1190).toString()+"',"+
                                        "konsultasi_perawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1191).toString()+"',"+
                                        "jawaban_konsultasi_perawat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1192).toString()+"',"+
                                        "bridging_smart_klaim_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1193).toString()+"',"+
                                        "mapping_prosedur_smart_klaim_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1194).toString()+"',"+
                                        "mapping_penyakit_smart_klaim_bpjs='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1195).toString()+"',"+
                                        "permintaan_binrohtal='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1196).toString()+"',"+
                                        "surat_permintaan_perlindungan_dari_kekerasan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1197).toString()+"',"+
                                        "surat_permohonan_privasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1198).toString()+"',"+
                                        "surat_permintaan_second_opinion='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1199).toString()+"',"+
                                        "surat_keterangan_berobat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1200).toString()+"',"+
                                        "surat_penolakan_resusitasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1201).toString()+"',"+
                                        "catatan_observasi_ruang_ok='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1202).toString()+"',"+
                                        "hasil_pemeriksaan_usg_abdomen='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1203).toString()+"',"+
                                        "intervensi_nyeri_farmakologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1204).toString()+"',"+
                                        "intervensi_nyeri_nonfarmakologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1205).toString()+"',"+
                                        "surat_pengajuan_cuti_pasien='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1206).toString()+"',"+
                                        "checklist_kriteria_masuk_isolasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1207).toString()+"',"+
                                        "satu_sehat_mapping_kptl_tindakan_ralan='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1208).toString()+"',"+
                                        "satu_sehat_mapping_kptl_tindakan_ranap='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1209).toString()+"',"+
                                        "satu_sehat_mapping_kptl_tindakan_radiologi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1210).toString()+"',"+
                                        "satu_sehat_mapping_kptl_tindakan_laborat='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1211).toString()+"',"+
                                        "satu_sehat_mapping_kptl_tindakan_operasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1212).toString()+"',"+
                                        "satu_sehat_mapping_kptl_tarif_kamar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1213).toString()+"',"+
                                        "checklist_kriteria_keluar_isolasi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1214).toString()+"',"+
                                        "satu_sehat_tanda_tangan_elektronik='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1215).toString()+"',"+
                                        "satu_sehat_kirim_composition='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1216).toString()+"',"+
                                        "ringkasan_hutang_vendor_inventaris='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1217).toString()+"',"+
                                        "ringkasan_beban_hutang_lain='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1218).toString()+"',"+
                                        "set_resep_per_cara_bayar='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1219).toString()+"',"+
                                        "skrining_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1220).toString()+"',"+
                                        "admisi_skoring_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1221).toString()+"',"+
                                        "monitoring_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1222).toString()+"',"+
                                        "hasil_persalinan_tolac='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1223).toString()+"',"+
                                        "laporan_reaksi_transfusi='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1224).toString()+"',"+
                                        "penilaian_grace_scor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1225).toString()+"',"+
                                        "penilaian_bishop_scor='"+tabMode.getValueAt(tbUser.convertRowIndexToModel(barisdicopy),1226).toString()+"'");
                                }
                                userdicopy="";
                                copyhakakses="";
                                barisdicopy=-1;
                                tampil();
                            } catch (Exception e) {
                                userdicopy="";
                                barisdicopy=-1;
                                copyhakakses="";
                            }
                        }else{
                            userdicopy="";
                            barisdicopy=-1;
                            copyhakakses="";
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_tbUserMouseClicked

    private void tbUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbUserKeyPressed
        if(tbUser.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_V){
                if(tbUser.getSelectedColumn()>4){
                    if(tbUser.getSelectedRow()!=-1){
                        if(tabMode.getValueAt(tbUser.convertRowIndexToModel(tbUser.getSelectedRow()),tbUser.convertColumnIndexToModel(tbUser.getSelectedColumn())).toString().equals("false")){
                            tabMode.setValueAt(true,tbUser.convertRowIndexToModel(tbUser.getSelectedRow()),tbUser.convertColumnIndexToModel(tbUser.getSelectedColumn()));
                        }else{
                            tabMode.setValueAt(false,tbUser.convertRowIndexToModel(tbUser.getSelectedRow()),tbUser.convertColumnIndexToModel(tbUser.getSelectedColumn()));
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_tbUserKeyPressed

    private void JabatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JabatanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JabatanKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgUser dialog = new DlgUser(new javax.swing.JFrame(), true);
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
    private widget.MenuItem MnCopyHakAkses;
    private widget.MenuItem MnSetUser;
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
    private widget.PopupMenu jPopupMenu1;
    private widget.panelisi panelGlass5;
    private widget.panelisi panelGlass6;
    private widget.panelisi panelGlass7;
    private widget.Table tbUser;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        if(ceksukses==false){
            ceksukses=true;
            try{
                tbUser.suspendSorting();
                Valid.tabelKosong(tabMode);
                new SwingWorker<Void, Object[]>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        ps=koneksi.prepareStatement(
                            "select AES_DECRYPT(user.id_user,'nur'),AES_DECRYPT(user.password,'windi'),user.penyakit,user.obat_penyakit,user.dokter,"+
                            "user.jadwal_praktek,user.petugas,user.pasien,user.registrasi,user.tindakan_ralan,user.kamar_inap,user.tindakan_ranap,user.operasi,"+
                            "user.rujukan_keluar,user.rujukan_masuk,user.beri_obat,user.resep_pulang,user.pasien_meninggal,user.diet_pasien,user.kelahiran_bayi,"+
                            "user.periksa_lab,user.periksa_radiologi,user.kasir_ralan,user.deposit_pasien,user.piutang_pasien,user.peminjaman_berkas,user.barcode,"+
                            "user.presensi_harian,user.presensi_bulanan,user.pegawai_admin,user.pegawai_user,user.suplier,user.satuan_barang,user.konversi_satuan,"+
                            "user.jenis_barang,user.obat,user.stok_opname_obat,user.stok_obat_pasien,user.pengadaan_obat,user.pemesanan_obat,user.penjualan_obat,"+
                            "user.piutang_obat,user.retur_ke_suplier,user.retur_dari_pembeli,user.retur_obat_ranap,user.retur_piutang_pasien,user.keuntungan_penjualan,"+
                            "user.keuntungan_beri_obat,user.sirkulasi_obat,user.ipsrs_barang,user.ipsrs_pengadaan_barang,user.ipsrs_stok_keluar,user.ipsrs_rekap_pengadaan,"+
                            "user.ipsrs_rekap_stok_keluar,user.ipsrs_pengeluaran_harian,user.inventaris_jenis,user.inventaris_kategori,user.inventaris_merk,"+
                            "user.inventaris_ruang,user.inventaris_produsen,user.inventaris_koleksi,user.inventaris_inventaris,user.inventaris_sirkulasi,user.parkir_jenis,"+
                            "user.parkir_in,user.parkir_out,user.parkir_rekap_harian,user.parkir_rekap_bulanan,user.informasi_kamar,user.harian_tindakan_poli,"+
                            "user.obat_per_poli,user.obat_per_kamar,user.obat_per_dokter_ralan,user.obat_per_dokter_ranap,user.harian_dokter,user.bulanan_dokter,"+
                            "user.harian_paramedis,user.bulanan_paramedis,user.pembayaran_ralan,user.pembayaran_ranap,user.rekap_pembayaran_ralan,user.rekap_pembayaran_ranap,"+
                            "user.tagihan_masuk,user.tambahan_biaya,user.potongan_biaya,user.resep_obat,user.resume_pasien,user.penyakit_ralan,user.penyakit_ranap,user.kamar,"+
                            "user.tarif_ralan,user.tarif_ranap,user.tarif_lab,user.tarif_radiologi,user.tarif_operasi,user.akun_rekening,user.rekening_tahun,"+
                            "user.posting_jurnal,user.buku_besar,user.cashflow,user.keuangan,user.pengeluaran,user.setup_pjlab,user.setup_otolokasi,user.setup_jam_kamin,"+
                            "user.setup_embalase,user.tracer_login,user.display,user.set_harga_obat,user.set_penggunaan_tarif,user.set_oto_ralan,user.biaya_harian,"+
                            "user.biaya_masuk_sekali,user.set_no_rm,user.billing_ralan,user.billing_ranap,user.jm_ranap_dokter,user.igd,user.barcoderalan,user.barcoderanap,"+
                            "user.set_harga_obat_ralan,user.set_harga_obat_ranap,user.penyakit_pd3i,user.surveilans_pd3i,user.surveilans_ralan,user.diagnosa_pasien,"+
                            "user.surveilans_ranap,user.pny_takmenular_ranap,user.pny_takmenular_ralan,user.kunjungan_ralan,user.rl32,user.rl33,user.rl37,user.rl38,"+
                            "user.harian_tindakan_dokter,user.sms,user.sidikjari,user.jam_masuk,user.jadwal_pegawai,user.parkir_barcode,user.set_nota,user.dpjp_ranap,"+
                            "user.mutasi_barang,user.rl34,user.rl36,user.fee_visit_dokter,user.fee_bacaan_ekg,user.fee_rujukan_rontgen,user.fee_rujukan_ranap,"+
                            "user.fee_ralan,user.akun_bayar,user.bayar_pemesanan_obat,user.obat_per_dokter_peresep,user.ipsrs_jenis_barang,user.pemasukan_lain,"+
                            "user.pengaturan_rekening,user.closing_kasir,user.keterlambatan_presensi,user.set_harga_kamar,user.rekap_per_shift,user.bpjs_cek_nik,"+
                            "user.bpjs_cek_kartu,user.bpjs_cek_riwayat,user.obat_per_cara_bayar,user.kunjungan_ranap,user.bayar_piutang,user.payment_point,"+
                            "user.bpjs_cek_nomor_rujukan,user.icd9,user.darurat_stok,user.retensi_rm,user.temporary_presensi,user.jurnal_harian,user.sirkulasi_obat2,"+
                            "user.edit_registrasi,user.bpjs_referensi_diagnosa,user.bpjs_referensi_poli,user.industrifarmasi,user.harian_js,user.bulanan_js,"+
                            "user.harian_paket_bhp,user.bulanan_paket_bhp,user.piutang_pasien2,user.bpjs_referensi_faskes,user.bpjs_sep,user.pengambilan_utd,"+
                            "user.tarif_utd,user.pengambilan_utd2,user.utd_medis_rusak,user.pengambilan_penunjang_utd,user.pengambilan_penunjang_utd2,user.utd_penunjang_rusak,"+
                            "user.suplier_penunjang,user.utd_donor,user.bpjs_monitoring_klaim,user.utd_cekal_darah,user.utd_komponen_darah,user.utd_stok_darah,"+
                            "user.utd_pemisahan_darah,user.harian_kamar,user.rincian_piutang_pasien,user.keuntungan_beri_obat_nonpiutang,user.reklasifikasi_ralan,"+
                            "user.reklasifikasi_ranap,user.utd_penyerahan_darah,user.hutang_obat,user.riwayat_obat_alkes_bhp,user.sensus_harian_poli,user.rl4a,"+
                            "user.aplicare_referensi_kamar,user.aplicare_ketersediaan_kamar,user.inacbg_klaim_baru_otomatis,user.inacbg_klaim_baru_manual,"+
                            "user.inacbg_coder_nik,user.mutasi_berkas,user.akun_piutang,user.harian_kso,user.bulanan_kso,user.harian_menejemen,user.bulanan_menejemen,"+
                            "user.inhealth_cek_eligibilitas,user.inhealth_referensi_jenpel_ruang_rawat,user.inhealth_referensi_poli,user.inhealth_referensi_faskes,"+
                            "user.inhealth_sjp,user.piutang_ralan,user.piutang_ranap,user.detail_piutang_penjab,user.lama_pelayanan_ralan,user.catatan_pasien,"+
                            "user.rl4b,user.rl4asebab,user.rl4bsebab,user.data_HAIs,user.harian_HAIs,user.bulanan_HAIs,user.hitung_bor,user.perusahaan_pasien,"+
                            "user.resep_dokter,user.lama_pelayanan_apotek,user.hitung_alos,user.detail_tindakan,user.rujukan_poli_internal,user.rekap_poli_anak,"+
                            "user.grafik_kunjungan_poli,user.grafik_kunjungan_perdokter,user.grafik_kunjungan_perpekerjaan,user.grafik_kunjungan_perpendidikan,"+
                            "user.grafik_kunjungan_pertahun,user.berkas_digital_perawatan,user.penyakit_menular_ranap,user.penyakit_menular_ralan,user.grafik_kunjungan_perbulan,"+
                            "user.grafik_kunjungan_pertanggal,user.grafik_kunjungan_demografi,user.grafik_kunjungan_statusdaftartahun,user.grafik_kunjungan_statusdaftartahun2,"+
                            "user.grafik_kunjungan_statusdaftarbulan,user.grafik_kunjungan_statusdaftarbulan2,user.grafik_kunjungan_statusdaftartanggal,"+
                            "user.grafik_kunjungan_statusdaftartanggal2,user.grafik_kunjungan_statusbataltahun,user.grafik_kunjungan_statusbatalbulan,user.pcare_cek_penyakit,"+
                            "user.grafik_kunjungan_statusbataltanggal,user.kategori_barang,user.golongan_barang,user.pemberian_obat_pertanggal,user.penjualan_obat_pertanggal,"+
                            "user.pcare_cek_kesadaran,user.pembatalan_periksa_dokter,user.pembayaran_per_unit,user.rekap_pembayaran_per_unit,user.grafik_kunjungan_percarabayar,"+
                            "user.ipsrs_pengadaan_pertanggal,user.ipsrs_stokkeluar_pertanggal,user.grafik_kunjungan_ranaptahun,user.pcare_cek_rujukan,user.grafik_lab_ralantahun,"+
                            "user.grafik_rad_ralantahun,user.cek_entry_ralan,user.inacbg_klaim_baru_manual2,user.permintaan_medis,user.rekap_permintaan_medis,"+
                            "user.surat_pemesanan_medis,user.permintaan_non_medis,user.rekap_permintaan_non_medis,user.surat_pemesanan_non_medis,user.grafik_per_perujuk,"+
                            "user.bpjs_cek_prosedur,user.bpjs_cek_kelas_rawat,user.bpjs_cek_dokter,user.bpjs_cek_spesialistik,user.bpjs_cek_ruangrawat,user.bpjs_cek_carakeluar,"+
                            "user.bpjs_cek_pasca_pulang,user.detail_tindakan_okvk,user.billing_parsial,user.bpjs_cek_nomor_rujukan_rs,user.bpjs_cek_rujukan_kartu_pcare,"+
                            "user.bpjs_cek_rujukan_kartu_rs,user.akses_depo_obat,user.bpjs_rujukan_keluar,user.grafik_lab_ralanbulan,user.pengeluaran_stok_apotek,"+
                            "user.grafik_rad_ralanbulan,user.detailjmdokter2,user.pengaduan_pasien,user.grafik_lab_ralanhari,user.grafik_rad_ralanhari,user.sensus_harian_ralan,"+
                            "user.metode_racik,user.pembayaran_akun_bayar,user.pengguna_obat_resep,user.rekap_pemesanan,user.master_berkas_pegawai,user.berkas_kepegawaian,"+
                            "user.riwayat_jabatan,user.riwayat_pendidikan,user.riwayat_naik_gaji,user.kegiatan_ilmiah,user.riwayat_penghargaan,user.riwayat_penelitian,"+
                            "user.penerimaan_non_medis,user.bayar_pesan_non_medis,user.hutang_barang_non_medis,user.rekap_pemesanan_non_medis,user.insiden_keselamatan,"+
                            "user.insiden_keselamatan_pasien,user.grafik_ikp_pertahun,user.grafik_ikp_perbulan,user.grafik_ikp_pertanggal,user.riwayat_data_batch,"+
                            "user.grafik_ikp_jenis,user.grafik_ikp_dampak,user.piutang_akun_piutang,user.grafik_kunjungan_per_agama,user.grafik_kunjungan_per_umur,"+
                            "user.suku_bangsa,user.bahasa_pasien,user.golongan_tni,user.satuan_tni,user.jabatan_tni,user.pangkat_tni,user.golongan_polri,user.satuan_polri,"+
                            "user.jabatan_polri,user.pangkat_polri,user.cacat_fisik,user.grafik_kunjungan_suku,user.grafik_kunjungan_bahasa,user.booking_operasi,"+
                            "user.mapping_poli_bpjs,user.grafik_kunjungan_per_cacat,user.barang_cssd,user.skdp_bpjs,user.booking_registrasi,user.bpjs_cek_propinsi,"+
                            "user.bpjs_cek_kabupaten,user.bpjs_cek_kecamatan,user.bpjs_cek_dokterdpjp,user.bpjs_cek_riwayat_rujukanrs,user.bpjs_cek_tanggal_rujukan,"+
                            "user.permintaan_lab,user.permintaan_radiologi,user.surat_indeks,user.surat_map,user.surat_almari,user.surat_rak,user.surat_ruang,"+
                            "user.surat_klasifikasi,user.surat_status,user.surat_sifat,user.surat_balas,user.surat_masuk,user.pcare_cek_dokter,user.pcare_cek_poli,"+
                            "user.pcare_cek_provider,user.pcare_cek_statuspulang,user.pcare_cek_spesialis,user.pcare_cek_subspesialis,user.pcare_cek_sarana,user.pcare_cek_khusus,"+
                            "user.pcare_cek_obat,user.pcare_cek_tindakan,user.pcare_cek_faskessubspesialis,user.pcare_cek_faskesalihrawat,user.pcare_cek_faskesthalasemia,"+
                            "user.pcare_mapping_obat,user.pcare_mapping_tindakan,user.pcare_club_prolanis,user.pcare_mapping_poli,user.pcare_kegiatan_kelompok,"+
                            "user.pcare_mapping_tindakan_ranap,user.pcare_peserta_kegiatan_kelompok,user.sirkulasi_obat3,user.bridging_pcare_daftar,user.pcare_mapping_dokter,"+
                            "user.ranap_per_ruang,user.penyakit_ranap_cara_bayar,user.anggota_militer_dirawat,user.set_input_parsial,user.lama_pelayanan_radiologi,"+
                            "user.lama_pelayanan_lab,user.bpjs_cek_sep,user.catatan_perawatan,user.surat_keluar,user.kegiatan_farmasi,user.stok_opname_logistik,"+
                            "user.sirkulasi_non_medis,user.rekap_lab_pertahun,user.perujuk_lab_pertahun,user.rekap_radiologi_pertahun,user.perujuk_radiologi_pertahun,"+
                            "user.jumlah_porsi_diet,user.jumlah_macam_diet,user.payment_point2,user.pembayaran_akun_bayar2,user.hapus_nota_salah,user.pengkajian_askep,"+
                            "user.hais_perbangsal,user.ppn_obat,user.saldo_akun_perbulan,user.display_apotek,user.sisrute_referensi_faskes,user.sisrute_referensi_alasanrujuk,"+
                            "user.sisrute_referensi_diagnosa,user.sisrute_rujukan_masuk,user.sisrute_rujukan_keluar,user.bpjs_cek_skdp,user.data_batch,user.kunjungan_permintaan_lab,"+
                            "user.kunjungan_permintaan_lab2,user.kunjungan_permintaan_radiologi,user.kunjungan_permintaan_radiologi2,user.pcare_pemberian_obat,"+
                            "user.pcare_pemberian_tindakan,user.pembayaran_akun_bayar3,user.password_asuransi,user.kemenkes_sitt,user.siranap_ketersediaan_kamar,"+
                            "user.grafik_tb_periodelaporan,user.grafik_tb_rujukan,user.grafik_tb_riwayat,user.grafik_tb_tipediagnosis,user.grafik_tb_statushiv,"+
                            "user.grafik_tb_skoringanak,user.grafik_tb_konfirmasiskoring5,user.grafik_tb_konfirmasiskoring6,user.grafik_tb_sumberobat,"+
                            "user.grafik_tb_hasilakhirpengobatan,user.grafik_tb_hasilteshiv,user.kadaluarsa_batch,user.sisa_stok,user.obat_per_resep,user.pemakaian_air_pdam,"+
                            "user.limbah_b3_medis,user.grafik_air_pdam_pertanggal,user.grafik_air_pdam_perbulan,user.grafik_limbahb3_pertanggal,user.grafik_limbahb3_perbulan,"+
                            "user.limbah_domestik,user.grafik_limbahdomestik_pertanggal,user.grafik_limbahdomestik_perbulan,user.mutu_air_limbah,user.pest_control,"+
                            "user.ruang_perpustakaan,user.kategori_perpustakaan,user.jenis_perpustakaan,user.pengarang_perpustakaan,user.penerbit_perpustakaan,"+
                            "user.koleksi_perpustakaan,user.inventaris_perpustakaan,user.set_peminjaman_perpustakaan,user.denda_perpustakaan,user.anggota_perpustakaan,"+
                            "user.peminjaman_perpustakaan,user.bayar_denda_perpustakaan,user.ebook_perpustakaan,user.jenis_cidera_k3rs,user.penyebab_k3rs,user.jenis_luka_k3rs,"+
                            "user.lokasi_kejadian_k3rs,user.dampak_cidera_k3rs,user.jenis_pekerjaan_k3rs,user.bagian_tubuh_k3rs,user.peristiwa_k3rs,user.grafik_k3_pertahun,"+
                            "user.grafik_k3_perbulan,user.grafik_k3_pertanggal,user.grafik_k3_perjeniscidera,user.grafik_k3_perpenyebab,user.grafik_k3_perjenisluka,"+
                            "user.grafik_k3_lokasikejadian,user.grafik_k3_dampakcidera,user.grafik_k3_perjenispekerjaan,user.grafik_k3_perbagiantubuh,user.jenis_cidera_k3rstahun,"+
                            "user.penyebab_k3rstahun,user.jenis_luka_k3rstahun,user.lokasi_kejadian_k3rstahun,user.dampak_cidera_k3rstahun,user.jenis_pekerjaan_k3rstahun,"+
                            "user.bagian_tubuh_k3rstahun,user.sekrining_rawat_jalan,user.bpjs_histori_pelayanan,user.rekap_mutasi_berkas,user.skrining_ralan_pernapasan_pertahun,"+
                            "user.pengajuan_barang_medis,user.pengajuan_barang_nonmedis,user.grafik_kunjungan_ranapbulan,user.grafik_kunjungan_ranaptanggal,"+
                            "user.grafik_kunjungan_ranap_peruang,user.kunjungan_bangsal_pertahun,user.grafik_jenjang_jabatanpegawai,user.grafik_bidangpegawai,"+
                            "user.grafik_departemenpegawai,user.grafik_pendidikanpegawai,user.grafik_sttswppegawai,user.grafik_sttskerjapegawai,user.grafik_sttspulangranap,"+
                            "user.kip_pasien_ranap,user.kip_pasien_ralan,user.bpjs_mapping_dokterdpjp,user.data_triase_igd,user.master_triase_skala1,user.master_triase_skala2,"+
                            "user.master_triase_skala3,user.master_triase_skala4,user.master_triase_skala5,user.master_triase_pemeriksaan,user.master_triase_macamkasus,"+
                            "user.rekap_permintaan_diet,user.daftar_pasien_ranap,user.daftar_pasien_ranaptni,user.pengajuan_asetinventaris,user.item_apotek_jenis,"+
                            "user.item_apotek_kategori,user.item_apotek_golongan,user.item_apotek_industrifarmasi,user.10_obat_terbanyak_poli,user.grafik_pengajuan_aset_urgensi,"+
                            "user.grafik_pengajuan_aset_status,user.grafik_pengajuan_aset_departemen,user.rekap_pengajuan_aset_departemen,user.grafik_kelompok_jabatanpegawai,"+
                            "user.grafik_resiko_kerjapegawai,user.grafik_emergency_indexpegawai,user.grafik_inventaris_ruang,user.harian_HAIs2,user.grafik_inventaris_jenis,"+
                            "user.data_resume_pasien,user.perkiraan_biaya_ranap,user.rekap_obat_poli,user.rekap_obat_pasien,user.permintaan_perbaikan_inventaris,"+
                            "user.grafik_HAIs_pasienbangsal,user.grafik_HAIs_pasienbulan,user.grafik_HAIs_laju_vap,user.grafik_HAIs_laju_iad,user.grafik_HAIs_laju_pleb,"+
                            "user.grafik_HAIs_laju_isk,user.grafik_HAIs_laju_ilo,user.grafik_HAIs_laju_hap,user.inhealth_mapping_poli,user.inhealth_mapping_dokter,"+
                            "user.inhealth_mapping_tindakan_ralan,user.inhealth_mapping_tindakan_ranap,user.inhealth_mapping_tindakan_radiologi,user.inhealth_mapping_tindakan_laborat,"+
                            "user.inhealth_mapping_tindakan_operasi,user.hibah_obat_bhp,user.asal_hibah,user.asuhan_gizi,user.inhealth_kirim_tagihan,user.sirkulasi_obat4,"+
                            "user.sirkulasi_obat5,user.sirkulasi_non_medis2,user.monitoring_asuhan_gizi,user.penerimaan_obat_perbulan,user.rekap_kunjungan,user.surat_sakit,"+
                            "user.penilaian_awal_keperawatan_ralan,user.permintaan_diet,user.master_masalah_keperawatan,user.pengajuan_cuti,user.kedatangan_pasien,user.utd_pendonor,"+
                            "user.toko_suplier,user.toko_jenis,user.toko_set_harga,user.toko_barang,user.penagihan_piutang_pasien,user.akun_penagihan_piutang,user.stok_opname_toko,"+
                            "user.toko_riwayat_barang,user.toko_surat_pemesanan,user.toko_pengajuan_barang,user.toko_penerimaan_barang,user.toko_pengadaan_barang,user.toko_hutang,"+
                            "user.toko_bayar_pemesanan,user.toko_member,user.toko_penjualan,user.registrasi_poli_per_tanggal,user.toko_piutang,user.toko_retur_beli,user.ipsrs_returbeli,"+
                            "user.ipsrs_riwayat_barang,user.pasien_corona,user.toko_pendapatan_harian,user.diagnosa_pasien_corona,user.perawatan_pasien_corona,"+
                            "user.penilaian_awal_keperawatan_gigi,user.master_masalah_keperawatan_gigi,user.toko_bayar_piutang,user.toko_piutang_harian,user.toko_penjualan_harian,"+
                            "user.deteksi_corona,user.penilaian_awal_keperawatan_kebidanan,user.pengumuman_epasien,user.surat_hamil,user.set_tarif_online,user.booking_periksa,"+
                            "user.toko_sirkulasi,user.toko_retur_jual,user.toko_retur_piutang,user.toko_sirkulasi2,user.toko_keuntungan_barang,user.zis_pengeluaran_penerima_dankes,"+
                            "user.zis_penghasilan_penerima_dankes,user.zis_ukuran_rumah_penerima_dankes,user.zis_dinding_rumah_penerima_dankes,user.zis_lantai_rumah_penerima_dankes,"+
                            "user.zis_atap_rumah_penerima_dankes,user.zis_kepemilikan_rumah_penerima_dankes,user.zis_kamar_mandi_penerima_dankes,user.zis_dapur_rumah_penerima_dankes,"+
                            "user.zis_kursi_rumah_penerima_dankes,user.zis_kategori_phbs_penerima_dankes,user.zis_elektronik_penerima_dankes,user.zis_ternak_penerima_dankes,"+
                            "user.zis_jenis_simpanan_penerima_dankes,user.penilaian_awal_keperawatan_anak,user.zis_kategori_asnaf_penerima_dankes,user.master_masalah_keperawatan_anak,"+
                            "user.master_imunisasi,user.zis_patologis_penerima_dankes,user.pcare_cek_kartu,user.surat_bebas_narkoba,user.surat_keterangan_covid,user.pemakaian_air_tanah,"+
                            "user.grafik_air_tanah_pertanggal,user.grafik_air_tanah_perbulan,user.lama_pelayanan_poli,user.hemodialisa,user.laporan_tahunan_irj,"+
                            "user.grafik_harian_hemodialisa,user.grafik_bulanan_hemodialisa,user.grafik_tahunan_hemodialisa,user.grafik_bulanan_meninggal,user.perbaikan_inventaris,"+
                            "user.surat_cuti_hamil,user.permintaan_stok_obat_pasien,user.pemeliharaan_inventaris,user.klasifikasi_pasien_ranap,user.bulanan_klasifikasi_pasien_ranap,"+
                            "user.harian_klasifikasi_pasien_ranap,user.klasifikasi_pasien_perbangsal,user.soap_perawatan,user.klaim_rawat_jalan,user.skrining_gizi,"+
                            "user.lama_penyiapan_rm,user.dosis_radiologi,user.demografi_umur_kunjungan,user.jam_diet_pasien,user.rvu_bpjs,user.verifikasi_penerimaan_farmasi,"+
                            "user.verifikasi_penerimaan_logistik,user.pemeriksaan_lab_pa,user.ringkasan_pengajuan_obat,user.ringkasan_pemesanan_obat,user.ringkasan_pengadaan_obat,"+
                            "user.ringkasan_penerimaan_obat,user.ringkasan_hibah_obat,user.ringkasan_penjualan_obat,user.ringkasan_beri_obat,user.ringkasan_piutang_obat,"+
                            "user.ringkasan_stok_keluar_obat,user.ringkasan_retur_suplier_obat,user.ringkasan_retur_pembeli_obat,user.penilaian_awal_keperawatan_ranapkebidanan,"+
                            "user.ringkasan_pengajuan_nonmedis,user.ringkasan_pemesanan_nonmedis,user.ringkasan_pengadaan_nonmedis,user.ringkasan_penerimaan_nonmedis,"+
                            "user.ringkasan_stokkeluar_nonmedis,user.ringkasan_returbeli_nonmedis,user.omset_penerimaan,user.validasi_penagihan_piutang,user.permintaan_ranap,"+
                            "user.bpjs_diagnosa_prb,user.bpjs_obat_prb,user.bpjs_surat_kontrol,user.penggunaan_bhp_ok,user.surat_keterangan_rawat_inap,user.surat_keterangan_sehat,"+
                            "user.pendapatan_per_carabayar,user.akun_host_to_host_bank_jateng,user.pembayaran_bank_jateng,user.bpjs_surat_pri,user.ringkasan_tindakan,"+
                            "user.lama_pelayanan_pasien,user.surat_sakit_pihak_2,user.tagihan_hutang_obat,user.referensi_mobilejkn_bpjs,user.batal_pendaftaran_mobilejkn_bpjs,"+
                            "user.lama_operasi,user.grafik_inventaris_kategori,user.grafik_inventaris_merk,user.grafik_inventaris_produsen,user.pengembalian_deposit_pasien,"+
                            "user.validasi_tagihan_hutang_obat,user.piutang_obat_belum_lunas,user.integrasi_briapi,user.pengadaan_aset_inventaris,user.akun_aset_inventaris,"+
                            "user.suplier_inventaris,user.penerimaan_aset_inventaris,user.bayar_pemesanan_iventaris,user.hutang_aset_inventaris,user.hibah_aset_inventaris,"+
                            "user.titip_faktur_non_medis,user.validasi_tagihan_non_medis,user.titip_faktur_aset,user.validasi_tagihan_aset,user.hibah_non_medis,"+
                            "user.pcare_alasan_tacc,user.resep_luar,user.surat_bebas_tbc,user.surat_buta_warna,user.surat_bebas_tato,user.surat_kewaspadaan_kesehatan,"+
                            "user.grafik_porsidiet_pertanggal,user.grafik_porsidiet_perbulan,user.grafik_porsidiet_pertahun,user.grafik_porsidiet_perbangsal,"+
                            "user.penilaian_awal_medis_ralan,user.master_masalah_keperawatan_mata,user.penilaian_awal_keperawatan_mata,user.penilaian_awal_medis_ranap,"+
                            "user.penilaian_awal_medis_ranap_kebidanan,user.penilaian_awal_medis_ralan_kebidanan,user.penilaian_awal_medis_igd,user.penilaian_awal_medis_ralan_anak,"+
                            "user.bpjs_referensi_poli_hfis,user.bpjs_referensi_dokter_hfis,user.bpjs_referensi_jadwal_hfis,user.penilaian_fisioterapi,user.bpjs_program_prb,"+
                            "user.bpjs_suplesi_jasaraharja,user.bpjs_data_induk_kecelakaan,user.bpjs_sep_internal,user.bpjs_klaim_jasa_raharja,user.bpjs_daftar_finger_print,"+
                            "user.bpjs_rujukan_khusus,user.pemeliharaan_gedung,user.grafik_perbaikan_inventaris_pertanggal,user.grafik_perbaikan_inventaris_perbulan,"+
                            "user.grafik_perbaikan_inventaris_pertahun,user.grafik_perbaikan_inventaris_perpelaksana_status,user.penilaian_mcu,user.peminjam_piutang,"+
                            "user.piutang_lainlain,user.cara_bayar,user.audit_kepatuhan_apd,user.bpjs_task_id,user.bayar_piutang_lain,user.pembayaran_akun_bayar4,"+
                            "user.stok_akhir_farmasi_pertanggal,user.riwayat_kamar_pasien,user.uji_fungsi_kfr,user.hapus_berkas_digital_perawatan,user.kategori_pengeluaran_harian,"+
                            "user.kategori_pemasukan_lain,user.pembayaran_akun_bayar5,user.ruang_ok,user.telaah_resep,user.jasa_tindakan_pasien,user.permintaan_resep_pulang,"+
                            "user.rekap_jm_dokter,user.status_data_rm,user.ubah_petugas_lab_pk,user.ubah_petugas_lab_pa,user.ubah_petugas_radiologi,user.gabung_norawat,"+
                            "user.gabung_rm,user.ringkasan_biaya_obat_pasien_pertanggal,user.master_masalah_keperawatan_igd,user.penilaian_awal_keperawatan_igd,"+
                            "user.bpjs_referensi_dpho_apotek,user.bpjs_referensi_poli_apotek,user.bayar_jm_dokter,user.bpjs_referensi_faskes_apotek,user.bpjs_referensi_spesialistik_apotek,"+
                            "user.pembayaran_briva,user.penilaian_awal_keperawatan_ranap,user.nilai_penerimaan_vendor_farmasi_perbulan,user.akun_bayar_hutang,user.master_rencana_keperawatan,"+
                            "user.laporan_tahunan_igd,user.obat_bhp_tidakbergerak,user.ringkasan_hutang_vendor_farmasi,user.nilai_penerimaan_vendor_nonmedis_perbulan,"+
                            "user.ringkasan_hutang_vendor_nonmedis,user.master_rencana_keperawatan_anak,user.anggota_polri_dirawat,user.daftar_pasien_ranap_polri,user.soap_ralan_polri,"+
                            "user.soap_ranap_polri,user.laporan_penyakit_polri,user.jumlah_pengunjung_ralan_polri,user.catatan_observasi_igd,user.catatan_observasi_ranap,"+
                            "user.catatan_observasi_ranap_kebidanan,user.catatan_observasi_ranap_postpartum,user.penilaian_awal_medis_ralan_tht,user.penilaian_psikologi,"+
                            "user.audit_cuci_tangan_medis,user.audit_pembuangan_limbah,user.ruang_audit_kepatuhan,user.audit_pembuangan_benda_tajam,user.audit_penanganan_darah,"+
                            "user.audit_pengelolaan_linen_kotor,user.audit_penempatan_pasien,user.audit_kamar_jenazah,user.audit_bundle_iadp,user.audit_bundle_ido,"+
                            "user.audit_fasilitas_kebersihan_tangan,user.audit_fasilitas_apd,user.audit_pembuangan_limbah_cair_infeksius,user.audit_sterilisasi_alat,"+
                            "user.penilaian_awal_medis_ralan_psikiatri,user.persetujuan_penolakan_tindakan,user.audit_bundle_isk,user.audit_bundle_plabsi,user.audit_bundle_vap,"+
                            "user.akun_host_to_host_bank_papua,user.pembayaran_bank_papua,user.penilaian_awal_medis_ralan_penyakit_dalam,user.penilaian_awal_medis_ralan_mata,"+
                            "user.penilaian_awal_medis_ralan_neurologi,user.sirkulasi_obat6,user.penilaian_awal_medis_ralan_orthopedi,user.penilaian_awal_medis_ralan_bedah,"+
                            "user.integrasi_khanza_health_services,user.soap_ralan_tni,user.soap_ranap_tni,user.jumlah_pengunjung_ralan_tni,user.laporan_penyakit_tni,"+
                            "user.catatan_keperawatan_ranap,user.master_rencana_keperawatan_gigi,user.master_rencana_keperawatan_mata,user.master_rencana_keperawatan_igd,"+
                            "user.master_masalah_keperawatan_psikiatri,user.master_rencana_keperawatan_psikiatri,user.penilaian_awal_keperawatan_psikiatri,user.pemantauan_pews_anak,"+
                            "user.surat_pulang_atas_permintaan_sendiri,user.template_hasil_radiologi,user.laporan_bulanan_irj,user.template_pemeriksaan,user.pemeriksaan_lab_mb,"+
                            "user.ubah_petugas_lab_mb,user.penilaian_pre_operasi,user.penilaian_pre_anestesi,user.perencanaan_pemulangan,user.penilaian_lanjutan_resiko_jatuh_dewasa,"+
                            "user.penilaian_lanjutan_resiko_jatuh_anak,user.penilaian_awal_medis_ralan_geriatri,user.penilaian_tambahan_pasien_geriatri,user.skrining_nutrisi_dewasa,"+
                            "user.skrining_nutrisi_lansia,user.hasil_pemeriksaan_usg,user.skrining_nutrisi_anak,user.akun_host_to_host_bank_jabar,user.pembayaran_bank_jabar,"+
                            "user.surat_pernyataan_pasien_umum,user.konseling_farmasi,user.pelayanan_informasi_obat,user.jawaban_pio_apoteker,user.surat_persetujuan_umum,"+
                            "user.transfer_pasien_antar_ruang,user.satu_sehat_referensi_dokter,user.satu_sehat_referensi_pasien,user.satu_sehat_mapping_departemen,"+
                            "user.satu_sehat_mapping_lokasi,user.satu_sehat_kirim_encounter,user.catatan_cek_gds,user.satu_sehat_kirim_condition,user.checklist_pre_operasi,"+
                            "user.satu_sehat_kirim_observationttv,user.signin_sebelum_anestesi,user.satu_sehat_kirim_procedure,user.operasi_per_bulan,user.timeout_sebelum_insisi,"+
                            "user.signout_sebelum_menutup_luka,user.dapur_barang,user.dapur_opname,user.satu_sehat_mapping_vaksin,user.dapur_suplier,user.satu_sehat_kirim_Immunization,"+
                            "user.checklist_post_operasi,user.dapur_pembelian,user.dapur_stok_keluar,user.dapur_riwayat_barang,user.permintaan_dapur,user.rekonsiliasi_obat,"+
                            "user.biaya_pengadaan_dapur,user.rekap_pengadaan_dapur,user.kesling_limbah_b3medis_cair,user.grafik_limbahb3cair_pertanggal,user.grafik_limbahb3cair_perbulan,"+
                            "user.rekap_biaya_registrasi,user.konfirmasi_rekonsiliasi_obat,user.satu_sehat_kirim_clinicalimpression,user.penilaian_pasien_terminal,"+
                            "user.surat_persetujuan_rawat_inap,user.monitoring_reaksi_tranfusi,user.penilaian_korban_kekerasan,user.penilaian_lanjutan_resiko_jatuh_lansia,"+
                            "user.penilaian_pasien_penyakit_menular,user.mpp_skrining,user.edukasi_pasien_keluarga_rj,user.pemantauan_pews_dewasa,user.penilaian_tambahan_bunuh_diri,"+
                            "user.bpjs_antrean_pertanggal,user.penilaian_tambahan_perilaku_kekerasan,user.penilaian_tambahan_beresiko_melarikan_diri,user.persetujuan_penundaan_pelayanan,"+
                            "user.sisa_diet_pasien,user.penilaian_awal_medis_ralan_bedah_mulut,user.penilaian_pasien_keracunan,user.pemantauan_meows_obstetri,user.catatan_adime_gizi,"+
                            "user.pengajuan_biaya,user.penilaian_awal_keperawatan_ralan_geriatri,user.master_masalah_keperawatan_geriatri,user.master_rencana_keperawatan_geriatri,"+
                            "user.checklist_kriteria_masuk_hcu,user.checklist_kriteria_keluar_hcu,user.penilaian_risiko_dekubitus,user.master_menolak_anjuran_medis,user.penolakan_anjuran_medis,"+
                            "user.laporan_tahunan_penolakan_anjuran_medis,user.template_laporan_operasi,user.hasil_tindakan_eswl,user.checklist_kriteria_masuk_icu,"+
                            "user.checklist_kriteria_keluar_icu,user.akses_dokter_lain_rawat_jalan,user.follow_up_dbd,user.penilaian_risiko_jatuh_neonatus,user.persetujuan_pengajuan_biaya,"+
                            "user.pemeriksaan_fisik_ralan_per_penyakit,user.penilaian_lanjutan_resiko_jatuh_geriatri,user.pemantauan_ews_neonatus,user.validasi_persetujuan_pengajuan_biaya,"+
                            "user.riwayat_perawatan_icare_bpjs,user.rekap_pengajuan_biaya,user.penilaian_awal_medis_ralan_kulit_kelamin,user.akun_host_to_host_bank_mandiri,"+
                            "user.penilaian_medis_hemodialisa,user.penilaian_level_kecemasan_ranap_anak,user.penilaian_lanjutan_resiko_jatuh_psikiatri,user.penilaian_lanjutan_skrining_fungsional,"+
                            "user.penilaian_medis_ralan_rehab_medik,user.laporan_anestesi,user.template_persetujuan_penolakan_tindakan,user.penilaian_medis_ralan_gawat_darurat_psikiatri,"+
                            "user.bpjs_referensi_setting_apotek,user.bpjs_referensi_obat_apotek,user.bpjs_mapping_obat_apotek,user.pembayaran_bank_mandiri,user.penilaian_ulang_nyeri,"+
                            "user.penilaian_terapi_wicara,user.bpjs_obat_23hari_apotek,user.pengkajian_restrain,user.bpjs_kunjungan_sep_apotek,user.bpjs_monitoring_klaim_apotek,"+
                            "user.bpjs_daftar_pelayanan_obat_apotek,user.penilaian_awal_medis_ralan_paru,user.catatan_keperawatan_ralan,user.catatan_persalinan,user.skor_aldrette_pasca_anestesi,"+
                            "user.skor_steward_pasca_anestesi,user.skor_bromage_pasca_anestesi,user.penilaian_pre_induksi,user.hasil_usg_urologi,user.hasil_usg_gynecologi,user.hasil_pemeriksaan_ekg,"+
                            "user.hapus_edit_sep_bpjs,user.satu_sehat_kirim_diet,user.satu_sehat_mapping_obat,user.dapur_ringkasan_pembelian,user.satu_sehat_kirim_medication,"+
                            "user.satu_sehat_kirim_medicationrequest,user.penatalaksanaan_terapi_okupasi,user.satu_sehat_kirim_medicationdispense,user.hasil_usg_neonatus,"+
                            "user.hasil_endoskopi_faring_laring,user.satu_sehat_mapping_radiologi,user.satu_sehat_kirim_servicerequest_radiologi,user.hasil_endoskopi_hidung,"+
                            "user.satu_sehat_kirim_specimen_radiologi,user.master_masalah_keperawatan_neonatus,user.master_rencana_keperawatan_neonatus,user.penilaian_awal_keperawatan_ranap_neonatus,"+
                            "user.satu_sehat_kirim_observation_radiologi,user.satu_sehat_kirim_diagnosticreport_radiologi,user.hasil_endoskopi_telinga,user.satu_sehat_mapping_lab,"+
                            "user.satu_sehat_kirim_servicerequest_lab,user.satu_sehat_kirim_servicerequest_labmb,user.satu_sehat_kirim_specimen_lab,user.satu_sehat_kirim_specimen_labmb,"+
                            "user.satu_sehat_kirim_observation_lab,user.satu_sehat_kirim_observation_labmb,user.satu_sehat_kirim_diagnosticreport_lab,user.satu_sehat_kirim_diagnosticreport_labmb,"+
                            "user.kepatuhan_kelengkapan_keselamatan_bedah,user.nilai_piutang_perjenis_bayar_per_bulan,user.ringkasan_piutang_jenis_bayar,user.penilaian_pasien_imunitas_rendah,"+
                            "user.balance_cairan,user.catatan_observasi_chbp,user.catatan_observasi_induksi_persalinan,user.skp_kategori_penilaian,user.skp_kriteria_penilaian,"+
                            "user.skp_penilaian,user.referensi_poli_mobilejknfktp,user.referensi_dokter_mobilejknfktp,user.skp_rekapitulasi_penilaian,user.pembayaran_pihak_ke3_bankmandiri,"+
                            "user.metode_pembayaran_bankmandiri,user.bank_tujuan_transfer_bankmandiri,user.kodetransaksi_tujuan_transfer_bankmandiri,user.konsultasi_medik,user.jawaban_konsultasi_medik,"+
                            "user.pcare_cek_alergi,user.pcare_cek_prognosa,user.data_sasaran_usiaproduktif,user.data_sasaran_usialansia,user.skrining_perilaku_merokok_sekolah_remaja,"+
                            "user.skrining_kekerasan_pada_perempuan,user.skrining_obesitas,user.skrining_risiko_kanker_payudara,user.skrining_risiko_kanker_paru,user.skrining_tbc,"+
                            "user.skrining_kesehatan_gigi_mulut_remaja,user.penilaian_awal_keperawatan_ranap_bayi,user.booking_mcu_perusahaan,user.catatan_observasi_restrain_nonfarma,"+
                            "user.catatan_observasi_ventilator,user.catatan_anestesi_sedasi,user.skrining_puma,user.satu_sehat_kirim_careplan,user.satu_sehat_kirim_medicationstatement,"+
                            "user.skrining_adiksi_nikotin,user.skrining_thalassemia,user.skrining_instrumen_sdq,user.skrining_instrumen_srq,user.checklist_pemberian_fibrinolitik,"+
                            "user.skrining_kanker_kolorektal,user.dapur_pemesanan,user.bayar_pesan_dapur,user.hutang_dapur,user.titip_faktur_dapur,user.validasi_tagihan_dapur,"+
                            "user.surat_pemesanan_dapur,user.pengajuan_barang_dapur,user.dapur_returbeli,user.hibah_dapur,user.ringkasan_penerimaan_dapur,user.ringkasan_pengajuan_dapur,"+
                            "user.ringkasan_pemesanan_dapur,user.ringkasan_returbeli_dapur,user.ringkasan_stokkeluar_dapur,user.dapur_stokkeluar_pertanggal,user.sirkulasi_dapur,"+
                            "user.sirkulasi_dapur2,user.verifikasi_penerimaan_dapur,user.nilai_penerimaan_vendor_dapur_perbulan,user.ringkasan_hutang_vendor_dapur,user.penilaian_psikologi_klinis,"+
                            "user.penilaian_awal_medis_ranap_neonatus,user.penilaian_derajat_dehidrasi,user.ringkasan_jasa_tindakan_medis,user.pendapatan_per_akun,user.hasil_pemeriksaan_echo,"+
                            "user.penilaian_bayi_baru_lahir,user.rl1_3_ketersediaan_kamar,user.pendapatan_per_akun_closing,user.pengeluaran_pengeluaran,user.skrining_diabetes_melitus,"+
                            "user.laporan_tindakan,user.pelaksanaan_informasi_edukasi,user.layanan_kedokteran_fisik_rehabilitasi,user.skrining_kesehatan_gigi_mulut_balita,user.skrining_anemia,"+
                            "user.layanan_program_kfr,user.skrining_hipertensi,user.skrining_kesehatan_penglihatan,user.catatan_observasi_hemodialisa,user.skrining_kesehatan_gigi_mulut_dewasa,"+
                            "user.skrining_risiko_kanker_serviks,user.catatan_cairan_hemodialisa,user.skrining_kesehatan_gigi_mulut_lansia,user.skrining_indra_pendengaran,user.catatan_pengkajian_paska_operasi,"+
                            "user.skrining_frailty_syndrome,user.sirkulasi_cssd,user.lama_pelayanan_cssd,user.catatan_observasi_bayi,user.riwayat_surat_peringatan,user.master_kesimpulan_anjuran_mcu,"+
                            "user.kategori_piutang_jasa_perusahaan,user.piutang_jasa_perusahaan,user.bayar_piutang_jasa_perusahaan,user.piutang_jasa_perusahaan_belum_lunas,user.checklist_kesiapan_anestesi,"+
                            "user.piutang_peminjaman_uang_belum_lunas,user.hasil_pemeriksaan_slit_lamp,user.hasil_pemeriksaan_oct,user.beban_hutang_lain,user.poli_asal_pasien_ranap,user.pemberi_hutang_lain,"+
                            "user.dokter_asal_pasien_ranap,user.duta_parkir_rekap_keluar,user.surat_keterangan_layak_terbang,user.bayar_beban_hutang_lain,user.surat_persetujuan_pemeriksaan_hiv,"+
                            "user.skrining_instrumen_acrs,user.surat_pernyataan_memilih_dpjp,user.skrining_instrumen_mental_emosional,user.pelanggan_lab_kesehatan_lingkungan,user.kriteria_masuk_nicu,"+
                            "user.kriteria_keluar_nicu,user.penilaian_medis_ranap_psikiatrik,user.kriteria_masuk_picu,user.kriteria_keluar_picu,user.master_sampel_bakumutu,user.skrining_instrumen_amt,"+
                            "user.parameter_pengujian_lab_kesehatan_lingkungan,user.nilai_normal_baku_mutu_lab_kesehatan_lingkungan,user.skrining_pneumonia_severity_index,"+
                            "user.permintaan_pengujian_sampel_lab_kesehatan_lingkungan,user.penilaian_awal_medis_ralan_jantung,user.penilaian_awal_medis_ralan_urologi,"+
                            "user.hasil_pemeriksaan_treadmill,user.hasil_pemeriksaan_echo_pediatrik,user.template_pelaksanaan_informasi_edukasi,user.skrining_instrumen_esat,"+
                            "user.penilaian_awal_medis_ranap_jantung,user.penugasan_pengujian_sampel_lab_kesehatan_lingkungan,user.hasil_pengujian_sampel_lab_kesehatan_lingkungan,"+
                            "user.verifikasi_pengujian_sampel_lab_kesehatan_lingkungan,user.validasi_pengujian_sampel_lab_kesehatan_lingkungan,user.rekap_pelayanan_lab_kesehatan_lingkungan,"+
                            "user.pembayaran_pengujian_sampel_lab_kesehatan_lingkungan,user.skrining_curb65,user.bpjs_potensi_prb,user.bpjs_riwayat_pelayanan_obat,user.skrining_gizi_kehamilan,"+
                            "user.bpjs_rekap_peserta_prb_apotek,user.serah_terima_anggota_tubuh_barang,user.pcra_icra_jenis_aktivitas_proyek,user.pcra_icra_lokasi_kelompok_risiko_area,"+
                            "user.pcra_icra_kelas_risiko_pencegahan,user.pcra_icra_tindakan_pengendalian,user.pcra_icra_identifkasi_risiko_infeksi,user.pcra_icra_identifkasi_risiko_keselamatan,"+
                            "user.pcra_icra_identifkasi_risiko_kebakaran,user.pcra_icra_identifkasi_risiko_utilitas,user.bpjs_daftar_resep_apotek,user.daftar_permintaan_resep_iterasi_bpjs,"+
                            "user.pcra_icra_pengkajian_risiko_prakonstruksi,user.pcra_icra_persyaratan_harus_dipenuhi,user.satu_sehat_kirim_questionresponse_telaah_farmasi,"+
                            "user.satu_sehat_kirim_allergy_intolerance,user.konsultasi_perawat,user.jawaban_konsultasi_perawat,user.bridging_smart_klaim_bpjs,user.mapping_prosedur_smart_klaim_bpjs,"+
                            "user.mapping_penyakit_smart_klaim_bpjs,user.permintaan_binrohtal,user.surat_permintaan_perlindungan_dari_kekerasan,user.surat_permohonan_privasi,"+
                            "user.surat_permintaan_second_opinion,user.surat_keterangan_berobat,user.surat_penolakan_resusitasi,user.catatan_observasi_ruang_ok,user.hasil_pemeriksaan_usg_abdomen,"+
                            "user.intervensi_nyeri_farmakologi,user.intervensi_nyeri_nonfarmakologi,user.surat_pengajuan_cuti_pasien,user.checklist_kriteria_masuk_isolasi,user.satu_sehat_mapping_kptl_tindakan_ralan,"+
                            "user.satu_sehat_mapping_kptl_tindakan_ranap,user.satu_sehat_mapping_kptl_tindakan_radiologi,user.satu_sehat_mapping_kptl_tindakan_laborat,user.satu_sehat_mapping_kptl_tindakan_operasi,"+
                            "user.satu_sehat_mapping_kptl_tarif_kamar,user.checklist_kriteria_keluar_isolasi,user.satu_sehat_tanda_tangan_elektronik,user.satu_sehat_kirim_composition,"+
                            "user.ringkasan_hutang_vendor_inventaris,user.ringkasan_beban_hutang_lain,user.set_resep_per_cara_bayar,user.skrining_tolac,user.admisi_skoring_tolac,user.monitoring_tolac,"+
                            "user.hasil_persalinan_tolac,user.laporan_reaksi_transfusi,user.penilaian_grace_scor,user.penilaian_bishop_scor from user order by AES_DECRYPT(user.id_user,'nur')");
                        try {
                            rs=ps.executeQuery();
                            i=0;
                            while(rs.next()){
                                user="";
                                user=Sequel.CariDokter(rs.getString(1));
                                jabatan=Sequel.cariIsi("select spesialis.nm_sps from spesialis where spesialis.kd_sps=?",Sequel.cariIsi("select dokter.kd_sps from dokter where dokter.kd_dokter=?",rs.getString(1)));
                                if(user.equals("")){
                                    user=Sequel.CariPetugas(rs.getString(1));
                                    jabatan=Sequel.cariIsi("select jabatan.nm_jbtn from jabatan where jabatan.kd_jbtn=?",Sequel.cariIsi("select petugas.kd_jbtn from petugas where petugas.nip=?",rs.getString(1)));
                                }
                                try {
                                    if(rs.getString(1).toLowerCase().contains(TCari.getText().toLowerCase())||
                                            user.toLowerCase().contains(TCari.getText().toLowerCase())||
                                            jabatan.toLowerCase().contains(TCari.getText().toLowerCase())){
                                        i++;
                                        publish(new Object[]{rs.getString(1),
                                           user,jabatan,rs.getString(2),
                                           rs.getBoolean("penyakit"),
                                           rs.getBoolean("obat_penyakit"),
                                           rs.getBoolean("dokter"),
                                           rs.getBoolean("jadwal_praktek"),
                                           rs.getBoolean("petugas"),
                                           rs.getBoolean("pasien"),
                                           rs.getBoolean("registrasi"),
                                           rs.getBoolean("tindakan_ralan"),
                                           rs.getBoolean("kamar_inap"),
                                           rs.getBoolean("tindakan_ranap"),
                                           rs.getBoolean("operasi"),
                                           rs.getBoolean("rujukan_keluar"),
                                           rs.getBoolean("rujukan_masuk"),
                                           rs.getBoolean("beri_obat"),
                                           rs.getBoolean("resep_pulang"),
                                           rs.getBoolean("pasien_meninggal"),
                                           rs.getBoolean("diet_pasien"),
                                           rs.getBoolean("kelahiran_bayi"),
                                           rs.getBoolean("periksa_lab"),
                                           rs.getBoolean("periksa_radiologi"),
                                           rs.getBoolean("kasir_ralan"),
                                           rs.getBoolean("deposit_pasien"),
                                           rs.getBoolean("piutang_pasien"),
                                           rs.getBoolean("peminjaman_berkas"),
                                           rs.getBoolean("barcode"),
                                           rs.getBoolean("presensi_harian"),
                                           rs.getBoolean("presensi_bulanan"),
                                           rs.getBoolean("pegawai_admin"),
                                           rs.getBoolean("pegawai_user"),
                                           rs.getBoolean("suplier"),
                                           rs.getBoolean("satuan_barang"),
                                           rs.getBoolean("konversi_satuan"),
                                           rs.getBoolean("jenis_barang"),
                                           rs.getBoolean("obat"),
                                           rs.getBoolean("stok_opname_obat"),
                                           rs.getBoolean("stok_obat_pasien"),
                                           rs.getBoolean("pengadaan_obat"),
                                           rs.getBoolean("pemesanan_obat"),
                                           rs.getBoolean("penjualan_obat"),
                                           rs.getBoolean("piutang_obat"),
                                           rs.getBoolean("retur_ke_suplier"),
                                           rs.getBoolean("retur_dari_pembeli"),
                                           rs.getBoolean("retur_obat_ranap"),
                                           rs.getBoolean("retur_piutang_pasien"),
                                           rs.getBoolean("keuntungan_penjualan"),
                                           rs.getBoolean("keuntungan_beri_obat"),
                                           rs.getBoolean("sirkulasi_obat"),
                                           rs.getBoolean("ipsrs_barang"),
                                           rs.getBoolean("ipsrs_pengadaan_barang"),
                                           rs.getBoolean("ipsrs_stok_keluar"),
                                           rs.getBoolean("ipsrs_rekap_pengadaan"),
                                           rs.getBoolean("ipsrs_rekap_stok_keluar"),
                                           rs.getBoolean("ipsrs_pengeluaran_harian"),
                                           rs.getBoolean("inventaris_jenis"),
                                           rs.getBoolean("inventaris_kategori"),
                                           rs.getBoolean("inventaris_merk"),
                                           rs.getBoolean("inventaris_ruang"),
                                           rs.getBoolean("inventaris_produsen"),
                                           rs.getBoolean("inventaris_koleksi"),
                                           rs.getBoolean("inventaris_inventaris"),
                                           rs.getBoolean("inventaris_sirkulasi"),
                                           rs.getBoolean("parkir_jenis"),
                                           rs.getBoolean("parkir_in"),
                                           rs.getBoolean("parkir_out"),
                                           rs.getBoolean("parkir_rekap_harian"),
                                           rs.getBoolean("parkir_rekap_bulanan"),
                                           rs.getBoolean("informasi_kamar"),
                                           rs.getBoolean("harian_tindakan_poli"),
                                           rs.getBoolean("obat_per_poli"),
                                           rs.getBoolean("obat_per_kamar"),
                                           rs.getBoolean("obat_per_dokter_ralan"),
                                           rs.getBoolean("obat_per_dokter_ranap"),
                                           rs.getBoolean("harian_dokter"),
                                           rs.getBoolean("bulanan_dokter"),
                                           rs.getBoolean("harian_paramedis"),
                                           rs.getBoolean("bulanan_paramedis"),
                                           rs.getBoolean("pembayaran_ralan"),
                                           rs.getBoolean("pembayaran_ranap"),
                                           rs.getBoolean("rekap_pembayaran_ralan"),
                                           rs.getBoolean("rekap_pembayaran_ranap"),
                                           rs.getBoolean("tagihan_masuk"),
                                           rs.getBoolean("tambahan_biaya"),
                                           rs.getBoolean("potongan_biaya"),
                                           rs.getBoolean("resep_obat"),
                                           rs.getBoolean("resume_pasien"),
                                           rs.getBoolean("penyakit_ralan"),
                                           rs.getBoolean("penyakit_ranap"),
                                           rs.getBoolean("kamar"),
                                           rs.getBoolean("tarif_ralan"),
                                           rs.getBoolean("tarif_ranap"),
                                           rs.getBoolean("tarif_lab"),
                                           rs.getBoolean("tarif_radiologi"),
                                           rs.getBoolean("tarif_operasi"),
                                           rs.getBoolean("akun_rekening"),
                                           rs.getBoolean("rekening_tahun"),
                                           rs.getBoolean("posting_jurnal"),
                                           rs.getBoolean("buku_besar"),
                                           rs.getBoolean("cashflow"),
                                           rs.getBoolean("keuangan"),
                                           rs.getBoolean("pengeluaran"),
                                           rs.getBoolean("setup_pjlab"),
                                           rs.getBoolean("setup_otolokasi"),
                                           rs.getBoolean("setup_jam_kamin"),
                                           rs.getBoolean("setup_embalase"),
                                           rs.getBoolean("tracer_login"),
                                           rs.getBoolean("display"),
                                           rs.getBoolean("set_harga_obat"),
                                           rs.getBoolean("set_penggunaan_tarif"),
                                           rs.getBoolean("set_oto_ralan"),
                                           rs.getBoolean("biaya_harian"),
                                           rs.getBoolean("biaya_masuk_sekali"),
                                           rs.getBoolean("set_no_rm"),
                                           rs.getBoolean("billing_ralan"),
                                           rs.getBoolean("billing_ranap"),
                                           rs.getBoolean("jm_ranap_dokter"),
                                           rs.getBoolean("igd"),
                                           rs.getBoolean("barcoderalan"),
                                           rs.getBoolean("barcoderanap"),
                                           rs.getBoolean("set_harga_obat_ralan"),
                                           rs.getBoolean("set_harga_obat_ranap"),
                                           rs.getBoolean("penyakit_pd3i"),
                                           rs.getBoolean("surveilans_pd3i"),
                                           rs.getBoolean("surveilans_ralan"),
                                           rs.getBoolean("diagnosa_pasien"),
                                           rs.getBoolean("surveilans_ranap"),
                                           rs.getBoolean("pny_takmenular_ranap"),
                                           rs.getBoolean("pny_takmenular_ralan"),
                                           rs.getBoolean("kunjungan_ralan"),
                                           rs.getBoolean("rl32"),
                                           rs.getBoolean("rl33"),
                                           rs.getBoolean("rl37"),
                                           rs.getBoolean("rl38"),
                                           rs.getBoolean("harian_tindakan_dokter"),
                                           rs.getBoolean("sms"),
                                           rs.getBoolean("sidikjari"),
                                           rs.getBoolean("jam_masuk"),
                                           rs.getBoolean("jadwal_pegawai"),
                                           rs.getBoolean("parkir_barcode"),
                                           rs.getBoolean("set_nota"),
                                           rs.getBoolean("dpjp_ranap"),
                                           rs.getBoolean("mutasi_barang"),
                                           rs.getBoolean("rl34"),
                                           rs.getBoolean("rl36"),
                                           rs.getBoolean("fee_visit_dokter"),
                                           rs.getBoolean("fee_bacaan_ekg"),
                                           rs.getBoolean("fee_rujukan_rontgen"),
                                           rs.getBoolean("fee_rujukan_ranap"),
                                           rs.getBoolean("fee_ralan"),
                                           rs.getBoolean("akun_bayar"),
                                           rs.getBoolean("bayar_pemesanan_obat"),
                                           rs.getBoolean("obat_per_dokter_peresep"),
                                           rs.getBoolean("ipsrs_jenis_barang"),
                                           rs.getBoolean("pemasukan_lain"),
                                           rs.getBoolean("pengaturan_rekening"),
                                           rs.getBoolean("closing_kasir"),
                                           rs.getBoolean("keterlambatan_presensi"),
                                           rs.getBoolean("set_harga_kamar"),
                                           rs.getBoolean("rekap_per_shift"),
                                           rs.getBoolean("bpjs_cek_nik"),
                                           rs.getBoolean("bpjs_cek_kartu"),
                                           rs.getBoolean("bpjs_cek_riwayat"),
                                           rs.getBoolean("obat_per_cara_bayar"),
                                           rs.getBoolean("kunjungan_ranap"),
                                           rs.getBoolean("bayar_piutang"),
                                           rs.getBoolean("payment_point"),
                                           rs.getBoolean("bpjs_cek_nomor_rujukan"),
                                           rs.getBoolean("icd9"),
                                           rs.getBoolean("darurat_stok"),
                                           rs.getBoolean("retensi_rm"),
                                           rs.getBoolean("temporary_presensi"),
                                           rs.getBoolean("jurnal_harian"),
                                           rs.getBoolean("sirkulasi_obat2"),
                                           rs.getBoolean("edit_registrasi"),
                                           rs.getBoolean("bpjs_referensi_diagnosa"),
                                           rs.getBoolean("bpjs_referensi_poli"),
                                           rs.getBoolean("industrifarmasi"),
                                           rs.getBoolean("harian_js"),
                                           rs.getBoolean("bulanan_js"),
                                           rs.getBoolean("harian_paket_bhp"),
                                           rs.getBoolean("bulanan_paket_bhp"),
                                           rs.getBoolean("piutang_pasien2"),
                                           rs.getBoolean("bpjs_referensi_faskes"),
                                           rs.getBoolean("bpjs_sep"),
                                           rs.getBoolean("pengambilan_utd"),
                                           rs.getBoolean("tarif_utd"),
                                           rs.getBoolean("pengambilan_utd2"),
                                           rs.getBoolean("utd_medis_rusak"),
                                           rs.getBoolean("pengambilan_penunjang_utd"),
                                           rs.getBoolean("pengambilan_penunjang_utd2"),
                                           rs.getBoolean("utd_penunjang_rusak"),
                                           rs.getBoolean("suplier_penunjang"),
                                           rs.getBoolean("utd_donor"),
                                           rs.getBoolean("bpjs_monitoring_klaim"),
                                           rs.getBoolean("utd_cekal_darah"),
                                           rs.getBoolean("utd_komponen_darah"),
                                           rs.getBoolean("utd_stok_darah"),
                                           rs.getBoolean("utd_pemisahan_darah"),
                                           rs.getBoolean("harian_kamar"),
                                           rs.getBoolean("rincian_piutang_pasien"),
                                           rs.getBoolean("keuntungan_beri_obat_nonpiutang"),
                                           rs.getBoolean("reklasifikasi_ralan"),
                                           rs.getBoolean("reklasifikasi_ranap"),
                                           rs.getBoolean("utd_penyerahan_darah"),
                                           rs.getBoolean("hutang_obat"),
                                           rs.getBoolean("riwayat_obat_alkes_bhp"),
                                           rs.getBoolean("sensus_harian_poli"),
                                           rs.getBoolean("rl4a"),
                                           rs.getBoolean("aplicare_referensi_kamar"),
                                           rs.getBoolean("aplicare_ketersediaan_kamar"),
                                           rs.getBoolean("inacbg_klaim_baru_otomatis"),
                                           rs.getBoolean("inacbg_klaim_baru_manual"),
                                           rs.getBoolean("inacbg_coder_nik"),
                                           rs.getBoolean("mutasi_berkas"),
                                           rs.getBoolean("akun_piutang"),
                                           rs.getBoolean("harian_kso"),
                                           rs.getBoolean("bulanan_kso"),
                                           rs.getBoolean("harian_menejemen"),
                                           rs.getBoolean("bulanan_menejemen"),
                                           rs.getBoolean("inhealth_cek_eligibilitas"),
                                           rs.getBoolean("inhealth_referensi_jenpel_ruang_rawat"),
                                           rs.getBoolean("inhealth_referensi_poli"),
                                           rs.getBoolean("inhealth_referensi_faskes"),
                                           rs.getBoolean("inhealth_sjp"),
                                           rs.getBoolean("piutang_ralan"),
                                           rs.getBoolean("piutang_ranap"),
                                           rs.getBoolean("detail_piutang_penjab"),
                                           rs.getBoolean("lama_pelayanan_ralan"),
                                           rs.getBoolean("catatan_pasien"),
                                           rs.getBoolean("rl4b"),
                                           rs.getBoolean("rl4asebab"),
                                           rs.getBoolean("rl4bsebab"),
                                           rs.getBoolean("data_HAIs"),
                                           rs.getBoolean("harian_HAIs"),
                                           rs.getBoolean("bulanan_HAIs"),
                                           rs.getBoolean("hitung_bor"),
                                           rs.getBoolean("perusahaan_pasien"),
                                           rs.getBoolean("resep_dokter"),
                                           rs.getBoolean("lama_pelayanan_apotek"),
                                           rs.getBoolean("hitung_alos"),
                                           rs.getBoolean("detail_tindakan"),
                                           rs.getBoolean("rujukan_poli_internal"),
                                           rs.getBoolean("rekap_poli_anak"),
                                           rs.getBoolean("grafik_kunjungan_poli"),
                                           rs.getBoolean("grafik_kunjungan_perdokter"),
                                           rs.getBoolean("grafik_kunjungan_perpekerjaan"),
                                           rs.getBoolean("grafik_kunjungan_perpendidikan"),
                                           rs.getBoolean("grafik_kunjungan_pertahun"),
                                           rs.getBoolean("berkas_digital_perawatan"),
                                           rs.getBoolean("penyakit_menular_ranap"),
                                           rs.getBoolean("penyakit_menular_ralan"),
                                           rs.getBoolean("grafik_kunjungan_perbulan"),
                                           rs.getBoolean("grafik_kunjungan_pertanggal"),
                                           rs.getBoolean("grafik_kunjungan_demografi"),
                                           rs.getBoolean("grafik_kunjungan_statusdaftartahun"),
                                           rs.getBoolean("grafik_kunjungan_statusdaftartahun2"),
                                           rs.getBoolean("grafik_kunjungan_statusdaftarbulan"),
                                           rs.getBoolean("grafik_kunjungan_statusdaftarbulan2"),
                                           rs.getBoolean("grafik_kunjungan_statusdaftartanggal"),
                                           rs.getBoolean("grafik_kunjungan_statusdaftartanggal2"),
                                           rs.getBoolean("grafik_kunjungan_statusbataltahun"),
                                           rs.getBoolean("grafik_kunjungan_statusbatalbulan"),
                                           rs.getBoolean("pcare_cek_penyakit"),
                                           rs.getBoolean("grafik_kunjungan_statusbataltanggal"),
                                           rs.getBoolean("kategori_barang"),
                                           rs.getBoolean("golongan_barang"),
                                           rs.getBoolean("pemberian_obat_pertanggal"),
                                           rs.getBoolean("penjualan_obat_pertanggal"),
                                           rs.getBoolean("pcare_cek_kesadaran"),
                                           rs.getBoolean("pembatalan_periksa_dokter"),
                                           rs.getBoolean("pembayaran_per_unit"),
                                           rs.getBoolean("rekap_pembayaran_per_unit"),
                                           rs.getBoolean("grafik_kunjungan_percarabayar"),
                                           rs.getBoolean("ipsrs_pengadaan_pertanggal"),
                                           rs.getBoolean("ipsrs_stokkeluar_pertanggal"),
                                           rs.getBoolean("grafik_kunjungan_ranaptahun"),
                                           rs.getBoolean("pcare_cek_rujukan"),
                                           rs.getBoolean("grafik_lab_ralantahun"),
                                           rs.getBoolean("grafik_rad_ralantahun"),
                                           rs.getBoolean("cek_entry_ralan"),
                                           rs.getBoolean("inacbg_klaim_baru_manual2"),
                                           rs.getBoolean("permintaan_medis"),
                                           rs.getBoolean("rekap_permintaan_medis"),
                                           rs.getBoolean("surat_pemesanan_medis"),
                                           rs.getBoolean("permintaan_non_medis"),
                                           rs.getBoolean("rekap_permintaan_non_medis"),
                                           rs.getBoolean("surat_pemesanan_non_medis"),
                                           rs.getBoolean("grafik_per_perujuk"),
                                           rs.getBoolean("bpjs_cek_prosedur"),
                                           rs.getBoolean("bpjs_cek_kelas_rawat"),
                                           rs.getBoolean("bpjs_cek_dokter"),
                                           rs.getBoolean("bpjs_cek_spesialistik"),
                                           rs.getBoolean("bpjs_cek_ruangrawat"),
                                           rs.getBoolean("bpjs_cek_carakeluar"),
                                           rs.getBoolean("bpjs_cek_pasca_pulang"),
                                           rs.getBoolean("detail_tindakan_okvk"),
                                           rs.getBoolean("billing_parsial"),
                                           rs.getBoolean("bpjs_cek_nomor_rujukan_rs"),
                                           rs.getBoolean("bpjs_cek_rujukan_kartu_pcare"),
                                           rs.getBoolean("bpjs_cek_rujukan_kartu_rs"),
                                           rs.getBoolean("akses_depo_obat"),
                                           rs.getBoolean("bpjs_rujukan_keluar"),
                                           rs.getBoolean("grafik_lab_ralanbulan"),
                                           rs.getBoolean("pengeluaran_stok_apotek"),
                                           rs.getBoolean("grafik_rad_ralanbulan"),
                                           rs.getBoolean("detailjmdokter2"),
                                           rs.getBoolean("pengaduan_pasien"),
                                           rs.getBoolean("grafik_lab_ralanhari"),
                                           rs.getBoolean("grafik_rad_ralanhari"),
                                           rs.getBoolean("sensus_harian_ralan"),
                                           rs.getBoolean("metode_racik"),
                                           rs.getBoolean("pembayaran_akun_bayar"),
                                           rs.getBoolean("pengguna_obat_resep"),
                                           rs.getBoolean("rekap_pemesanan"),
                                           rs.getBoolean("master_berkas_pegawai"),
                                           rs.getBoolean("berkas_kepegawaian"),
                                           rs.getBoolean("riwayat_jabatan"),
                                           rs.getBoolean("riwayat_pendidikan"),
                                           rs.getBoolean("riwayat_naik_gaji"),
                                           rs.getBoolean("kegiatan_ilmiah"),
                                           rs.getBoolean("riwayat_penghargaan"),
                                           rs.getBoolean("riwayat_penelitian"),
                                           rs.getBoolean("penerimaan_non_medis"),
                                           rs.getBoolean("bayar_pesan_non_medis"),
                                           rs.getBoolean("hutang_barang_non_medis"),
                                           rs.getBoolean("rekap_pemesanan_non_medis"),
                                           rs.getBoolean("insiden_keselamatan"),
                                           rs.getBoolean("insiden_keselamatan_pasien"),
                                           rs.getBoolean("grafik_ikp_pertahun"),
                                           rs.getBoolean("grafik_ikp_perbulan"),
                                           rs.getBoolean("grafik_ikp_pertanggal"),
                                           rs.getBoolean("riwayat_data_batch"),
                                           rs.getBoolean("grafik_ikp_jenis"),
                                           rs.getBoolean("grafik_ikp_dampak"),
                                           rs.getBoolean("piutang_akun_piutang"),
                                           rs.getBoolean("grafik_kunjungan_per_agama"),
                                           rs.getBoolean("grafik_kunjungan_per_umur"),
                                           rs.getBoolean("suku_bangsa"),
                                           rs.getBoolean("bahasa_pasien"),
                                           rs.getBoolean("golongan_tni"),
                                           rs.getBoolean("satuan_tni"),
                                           rs.getBoolean("jabatan_tni"),
                                           rs.getBoolean("pangkat_tni"),
                                           rs.getBoolean("golongan_polri"),
                                           rs.getBoolean("satuan_polri"),
                                           rs.getBoolean("jabatan_polri"),
                                           rs.getBoolean("pangkat_polri"),
                                           rs.getBoolean("cacat_fisik"),
                                           rs.getBoolean("grafik_kunjungan_suku"),
                                           rs.getBoolean("grafik_kunjungan_bahasa"),
                                           rs.getBoolean("booking_operasi"),
                                           rs.getBoolean("mapping_poli_bpjs"),
                                           rs.getBoolean("grafik_kunjungan_per_cacat"),
                                           rs.getBoolean("barang_cssd"),
                                           rs.getBoolean("skdp_bpjs"),
                                           rs.getBoolean("booking_registrasi"),
                                           rs.getBoolean("bpjs_cek_propinsi"),
                                           rs.getBoolean("bpjs_cek_kabupaten"),
                                           rs.getBoolean("bpjs_cek_kecamatan"),
                                           rs.getBoolean("bpjs_cek_dokterdpjp"),
                                           rs.getBoolean("bpjs_cek_riwayat_rujukanrs"),
                                           rs.getBoolean("bpjs_cek_tanggal_rujukan"),
                                           rs.getBoolean("permintaan_lab"),
                                           rs.getBoolean("permintaan_radiologi"),
                                           rs.getBoolean("surat_indeks"),
                                           rs.getBoolean("surat_map"),
                                           rs.getBoolean("surat_almari"),
                                           rs.getBoolean("surat_rak"),
                                           rs.getBoolean("surat_ruang"),
                                           rs.getBoolean("surat_klasifikasi"),
                                           rs.getBoolean("surat_status"),
                                           rs.getBoolean("surat_sifat"),
                                           rs.getBoolean("surat_balas"),
                                           rs.getBoolean("surat_masuk"),
                                           rs.getBoolean("pcare_cek_dokter"),
                                           rs.getBoolean("pcare_cek_poli"),
                                           rs.getBoolean("pcare_cek_provider"),
                                           rs.getBoolean("pcare_cek_statuspulang"),
                                           rs.getBoolean("pcare_cek_spesialis"),
                                           rs.getBoolean("pcare_cek_subspesialis"),
                                           rs.getBoolean("pcare_cek_sarana"),
                                           rs.getBoolean("pcare_cek_khusus"),
                                           rs.getBoolean("pcare_cek_obat"),
                                           rs.getBoolean("pcare_cek_tindakan"),
                                           rs.getBoolean("pcare_cek_faskessubspesialis"),
                                           rs.getBoolean("pcare_cek_faskesalihrawat"),
                                           rs.getBoolean("pcare_cek_faskesthalasemia"),
                                           rs.getBoolean("pcare_mapping_obat"),
                                           rs.getBoolean("pcare_mapping_tindakan"),
                                           rs.getBoolean("pcare_club_prolanis"),
                                           rs.getBoolean("pcare_mapping_poli"),
                                           rs.getBoolean("pcare_kegiatan_kelompok"),
                                           rs.getBoolean("pcare_mapping_tindakan_ranap"),
                                           rs.getBoolean("pcare_peserta_kegiatan_kelompok"),
                                           rs.getBoolean("sirkulasi_obat3"),
                                           rs.getBoolean("bridging_pcare_daftar"),
                                           rs.getBoolean("pcare_mapping_dokter"),
                                           rs.getBoolean("ranap_per_ruang"),
                                           rs.getBoolean("penyakit_ranap_cara_bayar"),
                                           rs.getBoolean("anggota_militer_dirawat"),
                                           rs.getBoolean("set_input_parsial"),
                                           rs.getBoolean("lama_pelayanan_radiologi"),
                                           rs.getBoolean("lama_pelayanan_lab"),
                                           rs.getBoolean("bpjs_cek_sep"),
                                           rs.getBoolean("catatan_perawatan"),
                                           rs.getBoolean("surat_keluar"),
                                           rs.getBoolean("kegiatan_farmasi"),
                                           rs.getBoolean("stok_opname_logistik"),
                                           rs.getBoolean("sirkulasi_non_medis"),
                                           rs.getBoolean("rekap_lab_pertahun"),
                                           rs.getBoolean("perujuk_lab_pertahun"),
                                           rs.getBoolean("rekap_radiologi_pertahun"),
                                           rs.getBoolean("perujuk_radiologi_pertahun"),
                                           rs.getBoolean("jumlah_porsi_diet"),
                                           rs.getBoolean("jumlah_macam_diet"),
                                           rs.getBoolean("payment_point2"),
                                           rs.getBoolean("pembayaran_akun_bayar2"),
                                           rs.getBoolean("hapus_nota_salah"),
                                           rs.getBoolean("pengkajian_askep"),
                                           rs.getBoolean("hais_perbangsal"),
                                           rs.getBoolean("ppn_obat"),
                                           rs.getBoolean("saldo_akun_perbulan"),
                                           rs.getBoolean("display_apotek"),
                                           rs.getBoolean("sisrute_referensi_faskes"),
                                           rs.getBoolean("sisrute_referensi_alasanrujuk"),
                                           rs.getBoolean("sisrute_referensi_diagnosa"),
                                           rs.getBoolean("sisrute_rujukan_masuk"),
                                           rs.getBoolean("sisrute_rujukan_keluar"),
                                           rs.getBoolean("bpjs_cek_skdp"),
                                           rs.getBoolean("data_batch"),
                                           rs.getBoolean("kunjungan_permintaan_lab"),
                                           rs.getBoolean("kunjungan_permintaan_lab2"),
                                           rs.getBoolean("kunjungan_permintaan_radiologi"),
                                           rs.getBoolean("kunjungan_permintaan_radiologi2"),
                                           rs.getBoolean("pcare_pemberian_obat"),
                                           rs.getBoolean("pcare_pemberian_tindakan"),
                                           rs.getBoolean("pembayaran_akun_bayar3"),
                                           rs.getBoolean("password_asuransi"),
                                           rs.getBoolean("kemenkes_sitt"),
                                           rs.getBoolean("siranap_ketersediaan_kamar"),
                                           rs.getBoolean("grafik_tb_periodelaporan"),
                                           rs.getBoolean("grafik_tb_rujukan"),
                                           rs.getBoolean("grafik_tb_riwayat"),
                                           rs.getBoolean("grafik_tb_tipediagnosis"),
                                           rs.getBoolean("grafik_tb_statushiv"),
                                           rs.getBoolean("grafik_tb_skoringanak"),
                                           rs.getBoolean("grafik_tb_konfirmasiskoring5"),
                                           rs.getBoolean("grafik_tb_konfirmasiskoring6"),
                                           rs.getBoolean("grafik_tb_sumberobat"),
                                           rs.getBoolean("grafik_tb_hasilakhirpengobatan"),
                                           rs.getBoolean("grafik_tb_hasilteshiv"),
                                           rs.getBoolean("kadaluarsa_batch"),
                                           rs.getBoolean("sisa_stok"),
                                           rs.getBoolean("obat_per_resep"),
                                           rs.getBoolean("pemakaian_air_pdam"),
                                           rs.getBoolean("limbah_b3_medis"),
                                           rs.getBoolean("grafik_air_pdam_pertanggal"),
                                           rs.getBoolean("grafik_air_pdam_perbulan"),
                                           rs.getBoolean("grafik_limbahb3_pertanggal"),
                                           rs.getBoolean("grafik_limbahb3_perbulan"),
                                           rs.getBoolean("limbah_domestik"),
                                           rs.getBoolean("grafik_limbahdomestik_pertanggal"),
                                           rs.getBoolean("grafik_limbahdomestik_perbulan"),
                                           rs.getBoolean("mutu_air_limbah"),
                                           rs.getBoolean("pest_control"),
                                           rs.getBoolean("ruang_perpustakaan"),
                                           rs.getBoolean("kategori_perpustakaan"),
                                           rs.getBoolean("jenis_perpustakaan"),
                                           rs.getBoolean("pengarang_perpustakaan"),
                                           rs.getBoolean("penerbit_perpustakaan"),
                                           rs.getBoolean("koleksi_perpustakaan"),
                                           rs.getBoolean("inventaris_perpustakaan"),
                                           rs.getBoolean("set_peminjaman_perpustakaan"),
                                           rs.getBoolean("denda_perpustakaan"),
                                           rs.getBoolean("anggota_perpustakaan"),
                                           rs.getBoolean("peminjaman_perpustakaan"),
                                           rs.getBoolean("bayar_denda_perpustakaan"),
                                           rs.getBoolean("ebook_perpustakaan"),
                                           rs.getBoolean("jenis_cidera_k3rs"),
                                           rs.getBoolean("penyebab_k3rs"),
                                           rs.getBoolean("jenis_luka_k3rs"),
                                           rs.getBoolean("lokasi_kejadian_k3rs"),
                                           rs.getBoolean("dampak_cidera_k3rs"),
                                           rs.getBoolean("jenis_pekerjaan_k3rs"),
                                           rs.getBoolean("bagian_tubuh_k3rs"),
                                           rs.getBoolean("peristiwa_k3rs"),
                                           rs.getBoolean("grafik_k3_pertahun"),
                                           rs.getBoolean("grafik_k3_perbulan"),
                                           rs.getBoolean("grafik_k3_pertanggal"),
                                           rs.getBoolean("grafik_k3_perjeniscidera"),
                                           rs.getBoolean("grafik_k3_perpenyebab"),
                                           rs.getBoolean("grafik_k3_perjenisluka"),
                                           rs.getBoolean("grafik_k3_lokasikejadian"),
                                           rs.getBoolean("grafik_k3_dampakcidera"),
                                           rs.getBoolean("grafik_k3_perjenispekerjaan"),
                                           rs.getBoolean("grafik_k3_perbagiantubuh"),
                                           rs.getBoolean("jenis_cidera_k3rstahun"),
                                           rs.getBoolean("penyebab_k3rstahun"),
                                           rs.getBoolean("jenis_luka_k3rstahun"),
                                           rs.getBoolean("lokasi_kejadian_k3rstahun"),
                                           rs.getBoolean("dampak_cidera_k3rstahun"),
                                           rs.getBoolean("jenis_pekerjaan_k3rstahun"),
                                           rs.getBoolean("bagian_tubuh_k3rstahun"),
                                           rs.getBoolean("sekrining_rawat_jalan"),
                                           rs.getBoolean("bpjs_histori_pelayanan"),
                                           rs.getBoolean("rekap_mutasi_berkas"),
                                           rs.getBoolean("skrining_ralan_pernapasan_pertahun"),
                                           rs.getBoolean("pengajuan_barang_medis"),
                                           rs.getBoolean("pengajuan_barang_nonmedis"),
                                           rs.getBoolean("grafik_kunjungan_ranapbulan"),
                                           rs.getBoolean("grafik_kunjungan_ranaptanggal"),
                                           rs.getBoolean("grafik_kunjungan_ranap_peruang"),
                                           rs.getBoolean("kunjungan_bangsal_pertahun"),
                                           rs.getBoolean("grafik_jenjang_jabatanpegawai"),
                                           rs.getBoolean("grafik_bidangpegawai"),
                                           rs.getBoolean("grafik_departemenpegawai"),
                                           rs.getBoolean("grafik_pendidikanpegawai"),
                                           rs.getBoolean("grafik_sttswppegawai"),
                                           rs.getBoolean("grafik_sttskerjapegawai"),
                                           rs.getBoolean("grafik_sttspulangranap"),
                                           rs.getBoolean("kip_pasien_ranap"),
                                           rs.getBoolean("kip_pasien_ralan"),
                                           rs.getBoolean("bpjs_mapping_dokterdpjp"),
                                           rs.getBoolean("data_triase_igd"),
                                           rs.getBoolean("master_triase_skala1"),
                                           rs.getBoolean("master_triase_skala2"),
                                           rs.getBoolean("master_triase_skala3"),
                                           rs.getBoolean("master_triase_skala4"),
                                           rs.getBoolean("master_triase_skala5"),
                                           rs.getBoolean("master_triase_pemeriksaan"),
                                           rs.getBoolean("master_triase_macamkasus"),
                                           rs.getBoolean("rekap_permintaan_diet"),
                                           rs.getBoolean("daftar_pasien_ranap"),
                                           rs.getBoolean("daftar_pasien_ranaptni"),
                                           rs.getBoolean("pengajuan_asetinventaris"),
                                           rs.getBoolean("item_apotek_jenis"),
                                           rs.getBoolean("item_apotek_kategori"),
                                           rs.getBoolean("item_apotek_golongan"),
                                           rs.getBoolean("item_apotek_industrifarmasi"),
                                           rs.getBoolean("10_obat_terbanyak_poli"),
                                           rs.getBoolean("grafik_pengajuan_aset_urgensi"),
                                           rs.getBoolean("grafik_pengajuan_aset_status"),
                                           rs.getBoolean("grafik_pengajuan_aset_departemen"),
                                           rs.getBoolean("rekap_pengajuan_aset_departemen"),
                                           rs.getBoolean("grafik_kelompok_jabatanpegawai"),
                                           rs.getBoolean("grafik_resiko_kerjapegawai"),
                                           rs.getBoolean("grafik_emergency_indexpegawai"),
                                           rs.getBoolean("grafik_inventaris_ruang"),
                                           rs.getBoolean("harian_HAIs2"),
                                           rs.getBoolean("grafik_inventaris_jenis"),
                                           rs.getBoolean("data_resume_pasien"),
                                           rs.getBoolean("perkiraan_biaya_ranap"),
                                           rs.getBoolean("rekap_obat_poli"),
                                           rs.getBoolean("rekap_obat_pasien"),
                                           rs.getBoolean("permintaan_perbaikan_inventaris"),
                                           rs.getBoolean("grafik_HAIs_pasienbangsal"),
                                           rs.getBoolean("grafik_HAIs_pasienbulan"),
                                           rs.getBoolean("grafik_HAIs_laju_vap"),
                                           rs.getBoolean("grafik_HAIs_laju_iad"),
                                           rs.getBoolean("grafik_HAIs_laju_pleb"),
                                           rs.getBoolean("grafik_HAIs_laju_isk"),
                                           rs.getBoolean("grafik_HAIs_laju_ilo"),
                                           rs.getBoolean("grafik_HAIs_laju_hap"),
                                           rs.getBoolean("inhealth_mapping_poli"),
                                           rs.getBoolean("inhealth_mapping_dokter"),
                                           rs.getBoolean("inhealth_mapping_tindakan_ralan"),
                                           rs.getBoolean("inhealth_mapping_tindakan_ranap"),
                                           rs.getBoolean("inhealth_mapping_tindakan_radiologi"),
                                           rs.getBoolean("inhealth_mapping_tindakan_laborat"),
                                           rs.getBoolean("inhealth_mapping_tindakan_operasi"),
                                           rs.getBoolean("hibah_obat_bhp"),
                                           rs.getBoolean("asal_hibah"),
                                           rs.getBoolean("asuhan_gizi"),
                                           rs.getBoolean("inhealth_kirim_tagihan"),
                                           rs.getBoolean("sirkulasi_obat4"),
                                           rs.getBoolean("sirkulasi_obat5"),
                                           rs.getBoolean("sirkulasi_non_medis2"),
                                           rs.getBoolean("monitoring_asuhan_gizi"),
                                           rs.getBoolean("penerimaan_obat_perbulan"),
                                           rs.getBoolean("rekap_kunjungan"),
                                           rs.getBoolean("surat_sakit"),
                                           rs.getBoolean("penilaian_awal_keperawatan_ralan"),
                                           rs.getBoolean("permintaan_diet"),
                                           rs.getBoolean("master_masalah_keperawatan"),
                                           rs.getBoolean("pengajuan_cuti"),
                                           rs.getBoolean("kedatangan_pasien"),
                                           rs.getBoolean("utd_pendonor"),
                                           rs.getBoolean("toko_suplier"),
                                           rs.getBoolean("toko_jenis"),
                                           rs.getBoolean("toko_set_harga"),
                                           rs.getBoolean("toko_barang"),
                                           rs.getBoolean("penagihan_piutang_pasien"),
                                           rs.getBoolean("akun_penagihan_piutang"),
                                           rs.getBoolean("stok_opname_toko"),
                                           rs.getBoolean("toko_riwayat_barang"),
                                           rs.getBoolean("toko_surat_pemesanan"),
                                           rs.getBoolean("toko_pengajuan_barang"),
                                           rs.getBoolean("toko_penerimaan_barang"),
                                           rs.getBoolean("toko_pengadaan_barang"),
                                           rs.getBoolean("toko_hutang"),
                                           rs.getBoolean("toko_bayar_pemesanan"),
                                           rs.getBoolean("toko_member"),
                                           rs.getBoolean("toko_penjualan"),
                                           rs.getBoolean("registrasi_poli_per_tanggal"),
                                           rs.getBoolean("toko_piutang"),
                                           rs.getBoolean("toko_retur_beli"),
                                           rs.getBoolean("ipsrs_returbeli"),
                                           rs.getBoolean("ipsrs_riwayat_barang"),
                                           rs.getBoolean("pasien_corona"),
                                           rs.getBoolean("toko_pendapatan_harian"),
                                           rs.getBoolean("diagnosa_pasien_corona"),
                                           rs.getBoolean("perawatan_pasien_corona"),
                                           rs.getBoolean("penilaian_awal_keperawatan_gigi"),
                                           rs.getBoolean("master_masalah_keperawatan_gigi"),
                                           rs.getBoolean("toko_bayar_piutang"),
                                           rs.getBoolean("toko_piutang_harian"),
                                           rs.getBoolean("toko_penjualan_harian"),
                                           rs.getBoolean("deteksi_corona"),
                                           rs.getBoolean("penilaian_awal_keperawatan_kebidanan"),
                                           rs.getBoolean("pengumuman_epasien"),
                                           rs.getBoolean("surat_hamil"),
                                           rs.getBoolean("set_tarif_online"),
                                           rs.getBoolean("booking_periksa"),
                                           rs.getBoolean("toko_sirkulasi"),
                                           rs.getBoolean("toko_retur_jual"),
                                           rs.getBoolean("toko_retur_piutang"),
                                           rs.getBoolean("toko_sirkulasi2"),
                                           rs.getBoolean("toko_keuntungan_barang"),
                                           rs.getBoolean("zis_pengeluaran_penerima_dankes"),
                                           rs.getBoolean("zis_penghasilan_penerima_dankes"),
                                           rs.getBoolean("zis_ukuran_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_dinding_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_lantai_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_atap_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_kepemilikan_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_kamar_mandi_penerima_dankes"),
                                           rs.getBoolean("zis_dapur_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_kursi_rumah_penerima_dankes"),
                                           rs.getBoolean("zis_kategori_phbs_penerima_dankes"),
                                           rs.getBoolean("zis_elektronik_penerima_dankes"),
                                           rs.getBoolean("zis_ternak_penerima_dankes"),
                                           rs.getBoolean("zis_jenis_simpanan_penerima_dankes"),
                                           rs.getBoolean("penilaian_awal_keperawatan_anak"),
                                           rs.getBoolean("zis_kategori_asnaf_penerima_dankes"),
                                           rs.getBoolean("master_masalah_keperawatan_anak"),
                                           rs.getBoolean("master_imunisasi"),
                                           rs.getBoolean("zis_patologis_penerima_dankes"),
                                           rs.getBoolean("pcare_cek_kartu"),
                                           rs.getBoolean("surat_bebas_narkoba"),
                                           rs.getBoolean("surat_keterangan_covid"),
                                           rs.getBoolean("pemakaian_air_tanah"),
                                           rs.getBoolean("grafik_air_tanah_pertanggal"),
                                           rs.getBoolean("grafik_air_tanah_perbulan"),
                                           rs.getBoolean("lama_pelayanan_poli"),
                                           rs.getBoolean("hemodialisa"),
                                           rs.getBoolean("laporan_tahunan_irj"),
                                           rs.getBoolean("grafik_harian_hemodialisa"),
                                           rs.getBoolean("grafik_bulanan_hemodialisa"),
                                           rs.getBoolean("grafik_tahunan_hemodialisa"),
                                           rs.getBoolean("grafik_bulanan_meninggal"),
                                           rs.getBoolean("perbaikan_inventaris"),
                                           rs.getBoolean("surat_cuti_hamil"),
                                           rs.getBoolean("permintaan_stok_obat_pasien"),
                                           rs.getBoolean("pemeliharaan_inventaris"),
                                           rs.getBoolean("klasifikasi_pasien_ranap"),
                                           rs.getBoolean("bulanan_klasifikasi_pasien_ranap"),
                                           rs.getBoolean("harian_klasifikasi_pasien_ranap"),
                                           rs.getBoolean("klasifikasi_pasien_perbangsal"),
                                           rs.getBoolean("soap_perawatan"),
                                           rs.getBoolean("klaim_rawat_jalan"),
                                           rs.getBoolean("skrining_gizi"),
                                           rs.getBoolean("lama_penyiapan_rm"),
                                           rs.getBoolean("dosis_radiologi"),
                                           rs.getBoolean("demografi_umur_kunjungan"),
                                           rs.getBoolean("jam_diet_pasien"),
                                           rs.getBoolean("rvu_bpjs"),
                                           rs.getBoolean("verifikasi_penerimaan_farmasi"),
                                           rs.getBoolean("verifikasi_penerimaan_logistik"),
                                           rs.getBoolean("pemeriksaan_lab_pa"),
                                           rs.getBoolean("ringkasan_pengajuan_obat"),
                                           rs.getBoolean("ringkasan_pemesanan_obat"),
                                           rs.getBoolean("ringkasan_pengadaan_obat"),
                                           rs.getBoolean("ringkasan_penerimaan_obat"),
                                           rs.getBoolean("ringkasan_hibah_obat"),
                                           rs.getBoolean("ringkasan_penjualan_obat"),
                                           rs.getBoolean("ringkasan_beri_obat"),
                                           rs.getBoolean("ringkasan_piutang_obat"),
                                           rs.getBoolean("ringkasan_stok_keluar_obat"),
                                           rs.getBoolean("ringkasan_retur_suplier_obat"),
                                           rs.getBoolean("ringkasan_retur_pembeli_obat"),
                                           rs.getBoolean("penilaian_awal_keperawatan_ranapkebidanan"),
                                           rs.getBoolean("ringkasan_pengajuan_nonmedis"),
                                           rs.getBoolean("ringkasan_pemesanan_nonmedis"),
                                           rs.getBoolean("ringkasan_pengadaan_nonmedis"),
                                           rs.getBoolean("ringkasan_penerimaan_nonmedis"),
                                           rs.getBoolean("ringkasan_stokkeluar_nonmedis"),
                                           rs.getBoolean("ringkasan_returbeli_nonmedis"),
                                           rs.getBoolean("omset_penerimaan"),
                                           rs.getBoolean("validasi_penagihan_piutang"),
                                           rs.getBoolean("permintaan_ranap"),
                                           rs.getBoolean("bpjs_diagnosa_prb"),
                                           rs.getBoolean("bpjs_obat_prb"),
                                           rs.getBoolean("bpjs_surat_kontrol"),
                                           rs.getBoolean("penggunaan_bhp_ok"),
                                           rs.getBoolean("surat_keterangan_rawat_inap"),
                                           rs.getBoolean("surat_keterangan_sehat"),
                                           rs.getBoolean("pendapatan_per_carabayar"),
                                           rs.getBoolean("akun_host_to_host_bank_jateng"),
                                           rs.getBoolean("pembayaran_bank_jateng"),
                                           rs.getBoolean("bpjs_surat_pri"),
                                           rs.getBoolean("ringkasan_tindakan"),
                                           rs.getBoolean("lama_pelayanan_pasien"),
                                           rs.getBoolean("surat_sakit_pihak_2"),
                                           rs.getBoolean("tagihan_hutang_obat"),
                                           rs.getBoolean("referensi_mobilejkn_bpjs"),
                                           rs.getBoolean("batal_pendaftaran_mobilejkn_bpjs"),
                                           rs.getBoolean("lama_operasi"),
                                           rs.getBoolean("grafik_inventaris_kategori"),
                                           rs.getBoolean("grafik_inventaris_merk"),
                                           rs.getBoolean("grafik_inventaris_produsen"),
                                           rs.getBoolean("pengembalian_deposit_pasien"),
                                           rs.getBoolean("validasi_tagihan_hutang_obat"),
                                           rs.getBoolean("piutang_obat_belum_lunas"),
                                           rs.getBoolean("integrasi_briapi"),
                                           rs.getBoolean("pengadaan_aset_inventaris"),
                                           rs.getBoolean("akun_aset_inventaris"),
                                           rs.getBoolean("suplier_inventaris"),
                                           rs.getBoolean("penerimaan_aset_inventaris"),
                                           rs.getBoolean("bayar_pemesanan_iventaris"),
                                           rs.getBoolean("hutang_aset_inventaris"),
                                           rs.getBoolean("hibah_aset_inventaris"),
                                           rs.getBoolean("titip_faktur_non_medis"),
                                           rs.getBoolean("validasi_tagihan_non_medis"),
                                           rs.getBoolean("titip_faktur_aset"),
                                           rs.getBoolean("validasi_tagihan_aset"),
                                           rs.getBoolean("hibah_non_medis"),
                                           rs.getBoolean("pcare_alasan_tacc"),
                                           rs.getBoolean("resep_luar"),
                                           rs.getBoolean("surat_bebas_tbc"),
                                           rs.getBoolean("surat_buta_warna"),
                                           rs.getBoolean("surat_bebas_tato"),
                                           rs.getBoolean("surat_kewaspadaan_kesehatan"),
                                           rs.getBoolean("grafik_porsidiet_pertanggal"),
                                           rs.getBoolean("grafik_porsidiet_perbulan"),
                                           rs.getBoolean("grafik_porsidiet_pertahun"),
                                           rs.getBoolean("grafik_porsidiet_perbangsal"),
                                           rs.getBoolean("penilaian_awal_medis_ralan"),
                                           rs.getBoolean("master_masalah_keperawatan_mata"),
                                           rs.getBoolean("penilaian_awal_keperawatan_mata"),
                                           rs.getBoolean("penilaian_awal_medis_ranap"),
                                           rs.getBoolean("penilaian_awal_medis_ranap_kebidanan"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_kebidanan"),
                                           rs.getBoolean("penilaian_awal_medis_igd"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_anak"),
                                           rs.getBoolean("bpjs_referensi_poli_hfis"),
                                           rs.getBoolean("bpjs_referensi_dokter_hfis"),
                                           rs.getBoolean("bpjs_referensi_jadwal_hfis"),
                                           rs.getBoolean("penilaian_fisioterapi"),
                                           rs.getBoolean("bpjs_program_prb"),
                                           rs.getBoolean("bpjs_suplesi_jasaraharja"),
                                           rs.getBoolean("bpjs_data_induk_kecelakaan"),
                                           rs.getBoolean("bpjs_sep_internal"),
                                           rs.getBoolean("bpjs_klaim_jasa_raharja"),
                                           rs.getBoolean("bpjs_daftar_finger_print"),
                                           rs.getBoolean("bpjs_rujukan_khusus"),
                                           rs.getBoolean("pemeliharaan_gedung"),
                                           rs.getBoolean("grafik_perbaikan_inventaris_pertanggal"),
                                           rs.getBoolean("grafik_perbaikan_inventaris_perbulan"),
                                           rs.getBoolean("grafik_perbaikan_inventaris_pertahun"),
                                           rs.getBoolean("grafik_perbaikan_inventaris_perpelaksana_status"),
                                           rs.getBoolean("penilaian_mcu"),
                                           rs.getBoolean("peminjam_piutang"),
                                           rs.getBoolean("piutang_lainlain"),
                                           rs.getBoolean("cara_bayar"),
                                           rs.getBoolean("audit_kepatuhan_apd"),
                                           rs.getBoolean("bpjs_task_id"),
                                           rs.getBoolean("bayar_piutang_lain"),
                                           rs.getBoolean("pembayaran_akun_bayar4"),
                                           rs.getBoolean("stok_akhir_farmasi_pertanggal"),
                                           rs.getBoolean("riwayat_kamar_pasien"),
                                           rs.getBoolean("uji_fungsi_kfr"),
                                           rs.getBoolean("hapus_berkas_digital_perawatan"),
                                           rs.getBoolean("kategori_pengeluaran_harian"),
                                           rs.getBoolean("kategori_pemasukan_lain"),
                                           rs.getBoolean("pembayaran_akun_bayar5"),
                                           rs.getBoolean("ruang_ok"),
                                           rs.getBoolean("telaah_resep"),
                                           rs.getBoolean("jasa_tindakan_pasien"),
                                           rs.getBoolean("permintaan_resep_pulang"),
                                           rs.getBoolean("rekap_jm_dokter"),
                                           rs.getBoolean("status_data_rm"),
                                           rs.getBoolean("ubah_petugas_lab_pk"),
                                           rs.getBoolean("ubah_petugas_lab_pa"),
                                           rs.getBoolean("ubah_petugas_radiologi"),
                                           rs.getBoolean("gabung_norawat"),
                                           rs.getBoolean("gabung_rm"),
                                           rs.getBoolean("ringkasan_biaya_obat_pasien_pertanggal"),
                                           rs.getBoolean("master_masalah_keperawatan_igd"),
                                           rs.getBoolean("penilaian_awal_keperawatan_igd"),
                                           rs.getBoolean("bpjs_referensi_dpho_apotek"),
                                           rs.getBoolean("bpjs_referensi_poli_apotek"),
                                           rs.getBoolean("bayar_jm_dokter"),
                                           rs.getBoolean("bpjs_referensi_faskes_apotek"),
                                           rs.getBoolean("bpjs_referensi_spesialistik_apotek"),
                                           rs.getBoolean("pembayaran_briva"),
                                           rs.getBoolean("penilaian_awal_keperawatan_ranap"),
                                           rs.getBoolean("nilai_penerimaan_vendor_farmasi_perbulan"),
                                           rs.getBoolean("akun_bayar_hutang"),
                                           rs.getBoolean("master_rencana_keperawatan"),
                                           rs.getBoolean("laporan_tahunan_igd"),
                                           rs.getBoolean("obat_bhp_tidakbergerak"),
                                           rs.getBoolean("ringkasan_hutang_vendor_farmasi"),
                                           rs.getBoolean("nilai_penerimaan_vendor_nonmedis_perbulan"),
                                           rs.getBoolean("ringkasan_hutang_vendor_nonmedis"),
                                           rs.getBoolean("master_rencana_keperawatan_anak"),
                                           rs.getBoolean("anggota_polri_dirawat"),
                                           rs.getBoolean("daftar_pasien_ranap_polri"),
                                           rs.getBoolean("soap_ralan_polri"),
                                           rs.getBoolean("soap_ranap_polri"),
                                           rs.getBoolean("laporan_penyakit_polri"),
                                           rs.getBoolean("jumlah_pengunjung_ralan_polri"),
                                           rs.getBoolean("catatan_observasi_igd"),
                                           rs.getBoolean("catatan_observasi_ranap"),
                                           rs.getBoolean("catatan_observasi_ranap_kebidanan"),
                                           rs.getBoolean("catatan_observasi_ranap_postpartum"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_tht"),
                                           rs.getBoolean("penilaian_psikologi"),
                                           rs.getBoolean("audit_cuci_tangan_medis"),
                                           rs.getBoolean("audit_pembuangan_limbah"),
                                           rs.getBoolean("ruang_audit_kepatuhan"),
                                           rs.getBoolean("audit_pembuangan_benda_tajam"),
                                           rs.getBoolean("audit_penanganan_darah"),
                                           rs.getBoolean("audit_pengelolaan_linen_kotor"),
                                           rs.getBoolean("audit_penempatan_pasien"),
                                           rs.getBoolean("audit_kamar_jenazah"),
                                           rs.getBoolean("audit_bundle_iadp"),
                                           rs.getBoolean("audit_bundle_ido"),
                                           rs.getBoolean("audit_fasilitas_kebersihan_tangan"),
                                           rs.getBoolean("audit_fasilitas_apd"),
                                           rs.getBoolean("audit_pembuangan_limbah_cair_infeksius"),
                                           rs.getBoolean("audit_sterilisasi_alat"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_psikiatri"),
                                           rs.getBoolean("persetujuan_penolakan_tindakan"),
                                           rs.getBoolean("audit_bundle_isk"),
                                           rs.getBoolean("audit_bundle_plabsi"),
                                           rs.getBoolean("audit_bundle_vap"),
                                           rs.getBoolean("akun_host_to_host_bank_papua"),
                                           rs.getBoolean("pembayaran_bank_papua"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_penyakit_dalam"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_mata"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_neurologi"),
                                           rs.getBoolean("sirkulasi_obat6"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_orthopedi"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_bedah"),
                                           rs.getBoolean("integrasi_khanza_health_services"),
                                           rs.getBoolean("soap_ralan_tni"),
                                           rs.getBoolean("soap_ranap_tni"),
                                           rs.getBoolean("jumlah_pengunjung_ralan_tni"),
                                           rs.getBoolean("laporan_penyakit_tni"),
                                           rs.getBoolean("catatan_keperawatan_ranap"),
                                           rs.getBoolean("master_rencana_keperawatan_gigi"),
                                           rs.getBoolean("master_rencana_keperawatan_mata"),
                                           rs.getBoolean("master_rencana_keperawatan_igd"),
                                           rs.getBoolean("master_masalah_keperawatan_psikiatri"),
                                           rs.getBoolean("master_rencana_keperawatan_psikiatri"),
                                           rs.getBoolean("penilaian_awal_keperawatan_psikiatri"),
                                           rs.getBoolean("pemantauan_pews_anak"),
                                           rs.getBoolean("surat_pulang_atas_permintaan_sendiri"),
                                           rs.getBoolean("template_hasil_radiologi"),
                                           rs.getBoolean("laporan_bulanan_irj"),
                                           rs.getBoolean("template_pemeriksaan"),
                                           rs.getBoolean("pemeriksaan_lab_mb"),
                                           rs.getBoolean("ubah_petugas_lab_mb"),
                                           rs.getBoolean("penilaian_pre_operasi"),
                                           rs.getBoolean("penilaian_pre_anestesi"),
                                           rs.getBoolean("perencanaan_pemulangan"),
                                           rs.getBoolean("penilaian_lanjutan_resiko_jatuh_dewasa"),
                                           rs.getBoolean("penilaian_lanjutan_resiko_jatuh_anak"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_geriatri"),
                                           rs.getBoolean("penilaian_tambahan_pasien_geriatri"),
                                           rs.getBoolean("skrining_nutrisi_dewasa"),
                                           rs.getBoolean("skrining_nutrisi_lansia"),
                                           rs.getBoolean("hasil_pemeriksaan_usg"),
                                           rs.getBoolean("skrining_nutrisi_anak"),
                                           rs.getBoolean("akun_host_to_host_bank_jabar"),
                                           rs.getBoolean("pembayaran_bank_jabar"),
                                           rs.getBoolean("surat_pernyataan_pasien_umum"),
                                           rs.getBoolean("konseling_farmasi"),
                                           rs.getBoolean("pelayanan_informasi_obat"),
                                           rs.getBoolean("jawaban_pio_apoteker"),
                                           rs.getBoolean("surat_persetujuan_umum"),
                                           rs.getBoolean("transfer_pasien_antar_ruang"),
                                           rs.getBoolean("satu_sehat_referensi_dokter"),
                                           rs.getBoolean("satu_sehat_referensi_pasien"),
                                           rs.getBoolean("satu_sehat_mapping_departemen"),
                                           rs.getBoolean("satu_sehat_mapping_lokasi"),
                                           rs.getBoolean("satu_sehat_kirim_encounter"),
                                           rs.getBoolean("catatan_cek_gds"),
                                           rs.getBoolean("satu_sehat_kirim_condition"),
                                           rs.getBoolean("checklist_pre_operasi"),
                                           rs.getBoolean("satu_sehat_kirim_observationttv"),
                                           rs.getBoolean("signin_sebelum_anestesi"),
                                           rs.getBoolean("satu_sehat_kirim_procedure"),
                                           rs.getBoolean("operasi_per_bulan"),
                                           rs.getBoolean("timeout_sebelum_insisi"),
                                           rs.getBoolean("signout_sebelum_menutup_luka"),
                                           rs.getBoolean("dapur_barang"),
                                           rs.getBoolean("dapur_opname"),
                                           rs.getBoolean("satu_sehat_mapping_vaksin"),
                                           rs.getBoolean("dapur_suplier"),
                                           rs.getBoolean("satu_sehat_kirim_Immunization"),
                                           rs.getBoolean("checklist_post_operasi"),
                                           rs.getBoolean("dapur_pembelian"),
                                           rs.getBoolean("dapur_stok_keluar"),
                                           rs.getBoolean("dapur_riwayat_barang"),
                                           rs.getBoolean("permintaan_dapur"),
                                           rs.getBoolean("rekonsiliasi_obat"),
                                           rs.getBoolean("biaya_pengadaan_dapur"),
                                           rs.getBoolean("rekap_pengadaan_dapur"),
                                           rs.getBoolean("kesling_limbah_b3medis_cair"),
                                           rs.getBoolean("grafik_limbahb3cair_pertanggal"),
                                           rs.getBoolean("grafik_limbahb3cair_perbulan"),
                                           rs.getBoolean("rekap_biaya_registrasi"),
                                           rs.getBoolean("konfirmasi_rekonsiliasi_obat"),
                                           rs.getBoolean("satu_sehat_kirim_clinicalimpression"),
                                           rs.getBoolean("penilaian_pasien_terminal"),
                                           rs.getBoolean("surat_persetujuan_rawat_inap"),
                                           rs.getBoolean("monitoring_reaksi_tranfusi"),
                                           rs.getBoolean("penilaian_korban_kekerasan"),
                                           rs.getBoolean("penilaian_lanjutan_resiko_jatuh_lansia"),
                                           rs.getBoolean("penilaian_pasien_penyakit_menular"),
                                           rs.getBoolean("mpp_skrining"),
                                           rs.getBoolean("edukasi_pasien_keluarga_rj"),
                                           rs.getBoolean("pemantauan_pews_dewasa"),
                                           rs.getBoolean("penilaian_tambahan_bunuh_diri"),
                                           rs.getBoolean("bpjs_antrean_pertanggal"),
                                           rs.getBoolean("penilaian_tambahan_perilaku_kekerasan"),
                                           rs.getBoolean("penilaian_tambahan_beresiko_melarikan_diri"),
                                           rs.getBoolean("persetujuan_penundaan_pelayanan"),
                                           rs.getBoolean("sisa_diet_pasien"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_bedah_mulut"),
                                           rs.getBoolean("penilaian_pasien_keracunan"),
                                           rs.getBoolean("pemantauan_meows_obstetri"),
                                           rs.getBoolean("catatan_adime_gizi"),
                                           rs.getBoolean("pengajuan_biaya"),
                                           rs.getBoolean("penilaian_awal_keperawatan_ralan_geriatri"),
                                           rs.getBoolean("master_masalah_keperawatan_geriatri"),
                                           rs.getBoolean("master_rencana_keperawatan_geriatri"),
                                           rs.getBoolean("checklist_kriteria_masuk_hcu"),
                                           rs.getBoolean("checklist_kriteria_keluar_hcu"),
                                           rs.getBoolean("penilaian_risiko_dekubitus"),
                                           rs.getBoolean("master_menolak_anjuran_medis"),
                                           rs.getBoolean("penolakan_anjuran_medis"),
                                           rs.getBoolean("laporan_tahunan_penolakan_anjuran_medis"),
                                           rs.getBoolean("template_laporan_operasi"),
                                           rs.getBoolean("hasil_tindakan_eswl"),
                                           rs.getBoolean("checklist_kriteria_masuk_icu"),
                                           rs.getBoolean("checklist_kriteria_keluar_icu"),
                                           rs.getBoolean("akses_dokter_lain_rawat_jalan"),
                                           rs.getBoolean("follow_up_dbd"),
                                           rs.getBoolean("penilaian_risiko_jatuh_neonatus"),
                                           rs.getBoolean("persetujuan_pengajuan_biaya"),
                                           rs.getBoolean("pemeriksaan_fisik_ralan_per_penyakit"),
                                           rs.getBoolean("penilaian_lanjutan_resiko_jatuh_geriatri"),
                                           rs.getBoolean("pemantauan_ews_neonatus"),
                                           rs.getBoolean("validasi_persetujuan_pengajuan_biaya"),
                                           rs.getBoolean("riwayat_perawatan_icare_bpjs"),
                                           rs.getBoolean("rekap_pengajuan_biaya"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_kulit_kelamin"),
                                           rs.getBoolean("akun_host_to_host_bank_mandiri"),
                                           rs.getBoolean("penilaian_medis_hemodialisa"),
                                           rs.getBoolean("penilaian_level_kecemasan_ranap_anak"),
                                           rs.getBoolean("penilaian_lanjutan_resiko_jatuh_psikiatri"),
                                           rs.getBoolean("penilaian_lanjutan_skrining_fungsional"),
                                           rs.getBoolean("penilaian_medis_ralan_rehab_medik"),
                                           rs.getBoolean("laporan_anestesi"),
                                           rs.getBoolean("template_persetujuan_penolakan_tindakan"),
                                           rs.getBoolean("penilaian_medis_ralan_gawat_darurat_psikiatri"),
                                           rs.getBoolean("bpjs_referensi_setting_apotek"),
                                           rs.getBoolean("bpjs_referensi_obat_apotek"),
                                           rs.getBoolean("bpjs_mapping_obat_apotek"),
                                           rs.getBoolean("pembayaran_bank_mandiri"),
                                           rs.getBoolean("penilaian_ulang_nyeri"),
                                           rs.getBoolean("penilaian_terapi_wicara"),
                                           rs.getBoolean("bpjs_obat_23hari_apotek"),
                                           rs.getBoolean("pengkajian_restrain"),
                                           rs.getBoolean("bpjs_kunjungan_sep_apotek"),
                                           rs.getBoolean("bpjs_monitoring_klaim_apotek"),
                                           rs.getBoolean("bpjs_daftar_pelayanan_obat_apotek"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_paru"),
                                           rs.getBoolean("catatan_keperawatan_ralan"),
                                           rs.getBoolean("catatan_persalinan"),
                                           rs.getBoolean("skor_aldrette_pasca_anestesi"),
                                           rs.getBoolean("skor_steward_pasca_anestesi"),
                                           rs.getBoolean("skor_bromage_pasca_anestesi"),
                                           rs.getBoolean("penilaian_pre_induksi"),
                                           rs.getBoolean("hasil_usg_urologi"),
                                           rs.getBoolean("hasil_usg_gynecologi"),
                                           rs.getBoolean("hasil_pemeriksaan_ekg"),
                                           rs.getBoolean("hapus_edit_sep_bpjs"),
                                           rs.getBoolean("satu_sehat_kirim_diet"),
                                           rs.getBoolean("satu_sehat_mapping_obat"),
                                           rs.getBoolean("dapur_ringkasan_pembelian"),
                                           rs.getBoolean("satu_sehat_kirim_medication"),
                                           rs.getBoolean("satu_sehat_kirim_medicationrequest"),
                                           rs.getBoolean("penatalaksanaan_terapi_okupasi"),
                                           rs.getBoolean("satu_sehat_kirim_medicationdispense"),
                                           rs.getBoolean("hasil_usg_neonatus"),
                                           rs.getBoolean("hasil_endoskopi_faring_laring"),
                                           rs.getBoolean("satu_sehat_mapping_radiologi"),
                                           rs.getBoolean("satu_sehat_kirim_servicerequest_radiologi"),
                                           rs.getBoolean("hasil_endoskopi_hidung"),
                                           rs.getBoolean("satu_sehat_kirim_specimen_radiologi"),
                                           rs.getBoolean("master_masalah_keperawatan_neonatus"),
                                           rs.getBoolean("master_rencana_keperawatan_neonatus"),
                                           rs.getBoolean("penilaian_awal_keperawatan_ranap_neonatus"),
                                           rs.getBoolean("satu_sehat_kirim_observation_radiologi"),
                                           rs.getBoolean("satu_sehat_kirim_diagnosticreport_radiologi"),
                                           rs.getBoolean("hasil_endoskopi_telinga"),
                                           rs.getBoolean("satu_sehat_mapping_lab"),
                                           rs.getBoolean("satu_sehat_kirim_servicerequest_lab"),
                                           rs.getBoolean("satu_sehat_kirim_servicerequest_labmb"),
                                           rs.getBoolean("satu_sehat_kirim_specimen_lab"),
                                           rs.getBoolean("satu_sehat_kirim_specimen_labmb"),
                                           rs.getBoolean("satu_sehat_kirim_observation_lab"),
                                           rs.getBoolean("satu_sehat_kirim_observation_labmb"),
                                           rs.getBoolean("satu_sehat_kirim_diagnosticreport_lab"),
                                           rs.getBoolean("satu_sehat_kirim_diagnosticreport_labmb"),
                                           rs.getBoolean("kepatuhan_kelengkapan_keselamatan_bedah"),
                                           rs.getBoolean("nilai_piutang_perjenis_bayar_per_bulan"),
                                           rs.getBoolean("ringkasan_piutang_jenis_bayar"),
                                           rs.getBoolean("penilaian_pasien_imunitas_rendah"),
                                           rs.getBoolean("balance_cairan"),
                                           rs.getBoolean("catatan_observasi_chbp"),
                                           rs.getBoolean("catatan_observasi_induksi_persalinan"),
                                           rs.getBoolean("skp_kategori_penilaian"),
                                           rs.getBoolean("skp_kriteria_penilaian"),
                                           rs.getBoolean("skp_penilaian"),
                                           rs.getBoolean("referensi_poli_mobilejknfktp"),
                                           rs.getBoolean("referensi_dokter_mobilejknfktp"),
                                           rs.getBoolean("skp_rekapitulasi_penilaian"),
                                           rs.getBoolean("pembayaran_pihak_ke3_bankmandiri"),
                                           rs.getBoolean("metode_pembayaran_bankmandiri"),
                                           rs.getBoolean("bank_tujuan_transfer_bankmandiri"),
                                           rs.getBoolean("kodetransaksi_tujuan_transfer_bankmandiri"),
                                           rs.getBoolean("konsultasi_medik"),
                                           rs.getBoolean("jawaban_konsultasi_medik"),
                                           rs.getBoolean("pcare_cek_alergi"),
                                           rs.getBoolean("pcare_cek_prognosa"),
                                           rs.getBoolean("data_sasaran_usiaproduktif"),
                                           rs.getBoolean("data_sasaran_usialansia"),
                                           rs.getBoolean("skrining_perilaku_merokok_sekolah_remaja"),
                                           rs.getBoolean("skrining_kekerasan_pada_perempuan"),
                                           rs.getBoolean("skrining_obesitas"),
                                           rs.getBoolean("skrining_risiko_kanker_payudara"),
                                           rs.getBoolean("skrining_risiko_kanker_paru"),
                                           rs.getBoolean("skrining_tbc"),
                                           rs.getBoolean("skrining_kesehatan_gigi_mulut_remaja"),
                                           rs.getBoolean("penilaian_awal_keperawatan_ranap_bayi"),
                                           rs.getBoolean("booking_mcu_perusahaan"),
                                           rs.getBoolean("catatan_observasi_restrain_nonfarma"),
                                           rs.getBoolean("catatan_observasi_ventilator"),
                                           rs.getBoolean("catatan_anestesi_sedasi"),
                                           rs.getBoolean("skrining_puma"),
                                           rs.getBoolean("satu_sehat_kirim_careplan"),
                                           rs.getBoolean("satu_sehat_kirim_medicationstatement"),
                                           rs.getBoolean("skrining_adiksi_nikotin"),
                                           rs.getBoolean("skrining_thalassemia"),
                                           rs.getBoolean("skrining_instrumen_sdq"),
                                           rs.getBoolean("skrining_instrumen_srq"),
                                           rs.getBoolean("checklist_pemberian_fibrinolitik"),
                                           rs.getBoolean("skrining_kanker_kolorektal"),
                                           rs.getBoolean("dapur_pemesanan"),
                                           rs.getBoolean("bayar_pesan_dapur"),
                                           rs.getBoolean("hutang_dapur"),
                                           rs.getBoolean("titip_faktur_dapur"),
                                           rs.getBoolean("validasi_tagihan_dapur"),
                                           rs.getBoolean("surat_pemesanan_dapur"),
                                           rs.getBoolean("pengajuan_barang_dapur"),
                                           rs.getBoolean("dapur_returbeli"),
                                           rs.getBoolean("hibah_dapur"),
                                           rs.getBoolean("ringkasan_penerimaan_dapur"),
                                           rs.getBoolean("ringkasan_pengajuan_dapur"),
                                           rs.getBoolean("ringkasan_pemesanan_dapur"),
                                           rs.getBoolean("ringkasan_returbeli_dapur"),
                                           rs.getBoolean("ringkasan_stokkeluar_dapur"),
                                           rs.getBoolean("dapur_stokkeluar_pertanggal"),
                                           rs.getBoolean("sirkulasi_dapur"),
                                           rs.getBoolean("sirkulasi_dapur2"),
                                           rs.getBoolean("verifikasi_penerimaan_dapur"),
                                           rs.getBoolean("nilai_penerimaan_vendor_dapur_perbulan"),
                                           rs.getBoolean("ringkasan_hutang_vendor_dapur"),
                                           rs.getBoolean("penilaian_psikologi_klinis"),
                                           rs.getBoolean("penilaian_awal_medis_ranap_neonatus"),
                                           rs.getBoolean("penilaian_derajat_dehidrasi"),
                                           rs.getBoolean("ringkasan_jasa_tindakan_medis"),
                                           rs.getBoolean("pendapatan_per_akun"),
                                           rs.getBoolean("hasil_pemeriksaan_echo"),
                                           rs.getBoolean("penilaian_bayi_baru_lahir"),
                                           rs.getBoolean("rl1_3_ketersediaan_kamar"),
                                           rs.getBoolean("pendapatan_per_akun_closing"),
                                           rs.getBoolean("pengeluaran_pengeluaran"),
                                           rs.getBoolean("skrining_diabetes_melitus"),
                                           rs.getBoolean("laporan_tindakan"),
                                           rs.getBoolean("pelaksanaan_informasi_edukasi"),
                                           rs.getBoolean("layanan_kedokteran_fisik_rehabilitasi"),
                                           rs.getBoolean("skrining_kesehatan_gigi_mulut_balita"),
                                           rs.getBoolean("skrining_anemia"),
                                           rs.getBoolean("layanan_program_kfr"),
                                           rs.getBoolean("skrining_hipertensi"),
                                           rs.getBoolean("skrining_kesehatan_penglihatan"),
                                           rs.getBoolean("catatan_observasi_hemodialisa"),
                                           rs.getBoolean("skrining_kesehatan_gigi_mulut_dewasa"),
                                           rs.getBoolean("skrining_risiko_kanker_serviks"),
                                           rs.getBoolean("catatan_cairan_hemodialisa"),
                                           rs.getBoolean("skrining_kesehatan_gigi_mulut_lansia"),
                                           rs.getBoolean("skrining_indra_pendengaran"),
                                           rs.getBoolean("catatan_pengkajian_paska_operasi"),
                                           rs.getBoolean("skrining_frailty_syndrome"),
                                           rs.getBoolean("sirkulasi_cssd"),
                                           rs.getBoolean("lama_pelayanan_cssd"),
                                           rs.getBoolean("catatan_observasi_bayi"),
                                           rs.getBoolean("riwayat_surat_peringatan"),
                                           rs.getBoolean("master_kesimpulan_anjuran_mcu"),
                                           rs.getBoolean("kategori_piutang_jasa_perusahaan"),
                                           rs.getBoolean("piutang_jasa_perusahaan"),
                                           rs.getBoolean("bayar_piutang_jasa_perusahaan"),
                                           rs.getBoolean("piutang_jasa_perusahaan_belum_lunas"),
                                           rs.getBoolean("checklist_kesiapan_anestesi"),
                                           rs.getBoolean("piutang_peminjaman_uang_belum_lunas"),
                                           rs.getBoolean("hasil_pemeriksaan_slit_lamp"),
                                           rs.getBoolean("hasil_pemeriksaan_oct"),
                                           rs.getBoolean("beban_hutang_lain"),
                                           rs.getBoolean("poli_asal_pasien_ranap"),
                                           rs.getBoolean("pemberi_hutang_lain"),
                                           rs.getBoolean("dokter_asal_pasien_ranap"),
                                           rs.getBoolean("duta_parkir_rekap_keluar"),
                                           rs.getBoolean("surat_keterangan_layak_terbang"),
                                           rs.getBoolean("bayar_beban_hutang_lain"),
                                           rs.getBoolean("surat_persetujuan_pemeriksaan_hiv"),
                                           rs.getBoolean("skrining_instrumen_acrs"),
                                           rs.getBoolean("surat_pernyataan_memilih_dpjp"),
                                           rs.getBoolean("skrining_instrumen_mental_emosional"),
                                           rs.getBoolean("pelanggan_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("kriteria_masuk_nicu"),
                                           rs.getBoolean("kriteria_keluar_nicu"),
                                           rs.getBoolean("penilaian_medis_ranap_psikiatrik"),
                                           rs.getBoolean("kriteria_masuk_picu"),
                                           rs.getBoolean("kriteria_keluar_picu"),
                                           rs.getBoolean("master_sampel_bakumutu"),
                                           rs.getBoolean("skrining_instrumen_amt"),
                                           rs.getBoolean("parameter_pengujian_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("nilai_normal_baku_mutu_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("skrining_pneumonia_severity_index"),
                                           rs.getBoolean("permintaan_pengujian_sampel_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_jantung"),
                                           rs.getBoolean("penilaian_awal_medis_ralan_urologi"),
                                           rs.getBoolean("hasil_pemeriksaan_treadmill"),
                                           rs.getBoolean("hasil_pemeriksaan_echo_pediatrik"),
                                           rs.getBoolean("template_pelaksanaan_informasi_edukasi"),
                                           rs.getBoolean("skrining_instrumen_esat"),
                                           rs.getBoolean("penilaian_awal_medis_ranap_jantung"),
                                           rs.getBoolean("penugasan_pengujian_sampel_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("hasil_pengujian_sampel_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("verifikasi_pengujian_sampel_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("validasi_pengujian_sampel_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("rekap_pelayanan_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("pembayaran_pengujian_sampel_lab_kesehatan_lingkungan"),
                                           rs.getBoolean("skrining_curb65"),
                                           rs.getBoolean("bpjs_potensi_prb"),
                                           rs.getBoolean("bpjs_riwayat_pelayanan_obat"),
                                           rs.getBoolean("skrining_gizi_kehamilan"),
                                           rs.getBoolean("bpjs_rekap_peserta_prb_apotek"),
                                           rs.getBoolean("serah_terima_anggota_tubuh_barang"),
                                           rs.getBoolean("pcra_icra_jenis_aktivitas_proyek"),
                                           rs.getBoolean("pcra_icra_lokasi_kelompok_risiko_area"),
                                           rs.getBoolean("pcra_icra_kelas_risiko_pencegahan"),
                                           rs.getBoolean("pcra_icra_tindakan_pengendalian"),
                                           rs.getBoolean("pcra_icra_identifkasi_risiko_infeksi"),
                                           rs.getBoolean("pcra_icra_identifkasi_risiko_keselamatan"),
                                           rs.getBoolean("pcra_icra_identifkasi_risiko_kebakaran"),
                                           rs.getBoolean("pcra_icra_identifkasi_risiko_utilitas"),
                                           rs.getBoolean("bpjs_daftar_resep_apotek"),
                                           rs.getBoolean("daftar_permintaan_resep_iterasi_bpjs"),
                                           rs.getBoolean("pcra_icra_pengkajian_risiko_prakonstruksi"),
                                           rs.getBoolean("pcra_icra_persyaratan_harus_dipenuhi"),
                                           rs.getBoolean("satu_sehat_kirim_questionresponse_telaah_farmasi"),
                                           rs.getBoolean("satu_sehat_kirim_allergy_intolerance"),
                                           rs.getBoolean("konsultasi_perawat"),
                                           rs.getBoolean("jawaban_konsultasi_perawat"),
                                           rs.getBoolean("bridging_smart_klaim_bpjs"),
                                           rs.getBoolean("mapping_prosedur_smart_klaim_bpjs"),
                                           rs.getBoolean("mapping_penyakit_smart_klaim_bpjs"),
                                           rs.getBoolean("permintaan_binrohtal"),
                                           rs.getBoolean("surat_permintaan_perlindungan_dari_kekerasan"),
                                           rs.getBoolean("surat_permohonan_privasi"),
                                           rs.getBoolean("surat_permintaan_second_opinion"),
                                           rs.getBoolean("surat_keterangan_berobat"),
                                           rs.getBoolean("surat_penolakan_resusitasi"),
                                           rs.getBoolean("catatan_observasi_ruang_ok"),
                                           rs.getBoolean("hasil_pemeriksaan_usg_abdomen"),
                                           rs.getBoolean("intervensi_nyeri_farmakologi"),
                                           rs.getBoolean("intervensi_nyeri_nonfarmakologi"),
                                           rs.getBoolean("surat_pengajuan_cuti_pasien"),
                                           rs.getBoolean("checklist_kriteria_masuk_isolasi"),
                                           rs.getBoolean("satu_sehat_mapping_kptl_tindakan_ralan"),
                                           rs.getBoolean("satu_sehat_mapping_kptl_tindakan_ranap"),
                                           rs.getBoolean("satu_sehat_mapping_kptl_tindakan_radiologi"),
                                           rs.getBoolean("satu_sehat_mapping_kptl_tindakan_laborat"),
                                           rs.getBoolean("satu_sehat_mapping_kptl_tindakan_operasi"),
                                           rs.getBoolean("satu_sehat_mapping_kptl_tarif_kamar"),
                                           rs.getBoolean("checklist_kriteria_keluar_isolasi"),
                                           rs.getBoolean("satu_sehat_tanda_tangan_elektronik"),
                                           rs.getBoolean("satu_sehat_kirim_composition"),
                                           rs.getBoolean("ringkasan_hutang_vendor_inventaris"),
                                           rs.getBoolean("ringkasan_beban_hutang_lain"),
                                           rs.getBoolean("set_resep_per_cara_bayar"),
                                           rs.getBoolean("skrining_tolac"),
                                           rs.getBoolean("admisi_skoring_tolac"),
                                           rs.getBoolean("monitoring_tolac"),
                                           rs.getBoolean("hasil_persalinan_tolac"),
                                           rs.getBoolean("laporan_reaksi_transfusi"),
                                           rs.getBoolean("penilaian_grace_scor"),
                                           rs.getBoolean("penilaian_bishop_scor")
                                        });
                                    }
                                } catch (Exception e) {
                                    i++;
                                    publish(new Object[]{rs.getString(1),
                                       "Turn Out","Jabatan",rs.getString(2),
                                       rs.getBoolean("penyakit"),
                                       rs.getBoolean("obat_penyakit"),
                                       rs.getBoolean("dokter"),
                                       rs.getBoolean("jadwal_praktek"),
                                       rs.getBoolean("petugas"),
                                       rs.getBoolean("pasien"),
                                       rs.getBoolean("registrasi"),
                                       rs.getBoolean("tindakan_ralan"),
                                       rs.getBoolean("kamar_inap"),
                                       rs.getBoolean("tindakan_ranap"),
                                       rs.getBoolean("operasi"),
                                       rs.getBoolean("rujukan_keluar"),
                                       rs.getBoolean("rujukan_masuk"),
                                       rs.getBoolean("beri_obat"),
                                       rs.getBoolean("resep_pulang"),
                                       rs.getBoolean("pasien_meninggal"),
                                       rs.getBoolean("diet_pasien"),
                                       rs.getBoolean("kelahiran_bayi"),
                                       rs.getBoolean("periksa_lab"),
                                       rs.getBoolean("periksa_radiologi"),
                                       rs.getBoolean("kasir_ralan"),
                                       rs.getBoolean("deposit_pasien"),
                                       rs.getBoolean("piutang_pasien"),
                                       rs.getBoolean("peminjaman_berkas"),
                                       rs.getBoolean("barcode"),
                                       rs.getBoolean("presensi_harian"),
                                       rs.getBoolean("presensi_bulanan"),
                                       rs.getBoolean("pegawai_admin"),
                                       rs.getBoolean("pegawai_user"),
                                       rs.getBoolean("suplier"),
                                       rs.getBoolean("satuan_barang"),
                                       rs.getBoolean("konversi_satuan"),
                                       rs.getBoolean("jenis_barang"),
                                       rs.getBoolean("obat"),
                                       rs.getBoolean("stok_opname_obat"),
                                       rs.getBoolean("stok_obat_pasien"),
                                       rs.getBoolean("pengadaan_obat"),
                                       rs.getBoolean("pemesanan_obat"),
                                       rs.getBoolean("penjualan_obat"),
                                       rs.getBoolean("piutang_obat"),
                                       rs.getBoolean("retur_ke_suplier"),
                                       rs.getBoolean("retur_dari_pembeli"),
                                       rs.getBoolean("retur_obat_ranap"),
                                       rs.getBoolean("retur_piutang_pasien"),
                                       rs.getBoolean("keuntungan_penjualan"),
                                       rs.getBoolean("keuntungan_beri_obat"),
                                       rs.getBoolean("sirkulasi_obat"),
                                       rs.getBoolean("ipsrs_barang"),
                                       rs.getBoolean("ipsrs_pengadaan_barang"),
                                       rs.getBoolean("ipsrs_stok_keluar"),
                                       rs.getBoolean("ipsrs_rekap_pengadaan"),
                                       rs.getBoolean("ipsrs_rekap_stok_keluar"),
                                       rs.getBoolean("ipsrs_pengeluaran_harian"),
                                       rs.getBoolean("inventaris_jenis"),
                                       rs.getBoolean("inventaris_kategori"),
                                       rs.getBoolean("inventaris_merk"),
                                       rs.getBoolean("inventaris_ruang"),
                                       rs.getBoolean("inventaris_produsen"),
                                       rs.getBoolean("inventaris_koleksi"),
                                       rs.getBoolean("inventaris_inventaris"),
                                       rs.getBoolean("inventaris_sirkulasi"),
                                       rs.getBoolean("parkir_jenis"),
                                       rs.getBoolean("parkir_in"),
                                       rs.getBoolean("parkir_out"),
                                       rs.getBoolean("parkir_rekap_harian"),
                                       rs.getBoolean("parkir_rekap_bulanan"),
                                       rs.getBoolean("informasi_kamar"),
                                       rs.getBoolean("harian_tindakan_poli"),
                                       rs.getBoolean("obat_per_poli"),
                                       rs.getBoolean("obat_per_kamar"),
                                       rs.getBoolean("obat_per_dokter_ralan"),
                                       rs.getBoolean("obat_per_dokter_ranap"),
                                       rs.getBoolean("harian_dokter"),
                                       rs.getBoolean("bulanan_dokter"),
                                       rs.getBoolean("harian_paramedis"),
                                       rs.getBoolean("bulanan_paramedis"),
                                       rs.getBoolean("pembayaran_ralan"),
                                       rs.getBoolean("pembayaran_ranap"),
                                       rs.getBoolean("rekap_pembayaran_ralan"),
                                       rs.getBoolean("rekap_pembayaran_ranap"),
                                       rs.getBoolean("tagihan_masuk"),
                                       rs.getBoolean("tambahan_biaya"),
                                       rs.getBoolean("potongan_biaya"),
                                       rs.getBoolean("resep_obat"),
                                       rs.getBoolean("resume_pasien"),
                                       rs.getBoolean("penyakit_ralan"),
                                       rs.getBoolean("penyakit_ranap"),
                                       rs.getBoolean("kamar"),
                                       rs.getBoolean("tarif_ralan"),
                                       rs.getBoolean("tarif_ranap"),
                                       rs.getBoolean("tarif_lab"),
                                       rs.getBoolean("tarif_radiologi"),
                                       rs.getBoolean("tarif_operasi"),
                                       rs.getBoolean("akun_rekening"),
                                       rs.getBoolean("rekening_tahun"),
                                       rs.getBoolean("posting_jurnal"),
                                       rs.getBoolean("buku_besar"),
                                       rs.getBoolean("cashflow"),
                                       rs.getBoolean("keuangan"),
                                       rs.getBoolean("pengeluaran"),
                                       rs.getBoolean("setup_pjlab"),
                                       rs.getBoolean("setup_otolokasi"),
                                       rs.getBoolean("setup_jam_kamin"),
                                       rs.getBoolean("setup_embalase"),
                                       rs.getBoolean("tracer_login"),
                                       rs.getBoolean("display"),
                                       rs.getBoolean("set_harga_obat"),
                                       rs.getBoolean("set_penggunaan_tarif"),
                                       rs.getBoolean("set_oto_ralan"),
                                       rs.getBoolean("biaya_harian"),
                                       rs.getBoolean("biaya_masuk_sekali"),
                                       rs.getBoolean("set_no_rm"),
                                       rs.getBoolean("billing_ralan"),
                                       rs.getBoolean("billing_ranap"),
                                       rs.getBoolean("jm_ranap_dokter"),
                                       rs.getBoolean("igd"),
                                       rs.getBoolean("barcoderalan"),
                                       rs.getBoolean("barcoderanap"),
                                       rs.getBoolean("set_harga_obat_ralan"),
                                       rs.getBoolean("set_harga_obat_ranap"),
                                       rs.getBoolean("penyakit_pd3i"),
                                       rs.getBoolean("surveilans_pd3i"),
                                       rs.getBoolean("surveilans_ralan"),
                                       rs.getBoolean("diagnosa_pasien"),
                                       rs.getBoolean("surveilans_ranap"),
                                       rs.getBoolean("pny_takmenular_ranap"),
                                       rs.getBoolean("pny_takmenular_ralan"),
                                       rs.getBoolean("kunjungan_ralan"),
                                       rs.getBoolean("rl32"),
                                       rs.getBoolean("rl33"),
                                       rs.getBoolean("rl37"),
                                       rs.getBoolean("rl38"),
                                       rs.getBoolean("harian_tindakan_dokter"),
                                       rs.getBoolean("sms"),
                                       rs.getBoolean("sidikjari"),
                                       rs.getBoolean("jam_masuk"),
                                       rs.getBoolean("jadwal_pegawai"),
                                       rs.getBoolean("parkir_barcode"),
                                       rs.getBoolean("set_nota"),
                                       rs.getBoolean("dpjp_ranap"),
                                       rs.getBoolean("mutasi_barang"),
                                       rs.getBoolean("rl34"),
                                       rs.getBoolean("rl36"),
                                       rs.getBoolean("fee_visit_dokter"),
                                       rs.getBoolean("fee_bacaan_ekg"),
                                       rs.getBoolean("fee_rujukan_rontgen"),
                                       rs.getBoolean("fee_rujukan_ranap"),
                                       rs.getBoolean("fee_ralan"),
                                       rs.getBoolean("akun_bayar"),
                                       rs.getBoolean("bayar_pemesanan_obat"),
                                       rs.getBoolean("obat_per_dokter_peresep"),
                                       rs.getBoolean("ipsrs_jenis_barang"),
                                       rs.getBoolean("pemasukan_lain"),
                                       rs.getBoolean("pengaturan_rekening"),
                                       rs.getBoolean("closing_kasir"),
                                       rs.getBoolean("keterlambatan_presensi"),
                                       rs.getBoolean("set_harga_kamar"),
                                       rs.getBoolean("rekap_per_shift"),
                                       rs.getBoolean("bpjs_cek_nik"),
                                       rs.getBoolean("bpjs_cek_kartu"),
                                       rs.getBoolean("bpjs_cek_riwayat"),
                                       rs.getBoolean("obat_per_cara_bayar"),
                                       rs.getBoolean("kunjungan_ranap"),
                                       rs.getBoolean("bayar_piutang"),
                                       rs.getBoolean("payment_point"),
                                       rs.getBoolean("bpjs_cek_nomor_rujukan"),
                                       rs.getBoolean("icd9"),
                                       rs.getBoolean("darurat_stok"),
                                       rs.getBoolean("retensi_rm"),
                                       rs.getBoolean("temporary_presensi"),
                                       rs.getBoolean("jurnal_harian"),
                                       rs.getBoolean("sirkulasi_obat2"),
                                       rs.getBoolean("edit_registrasi"),
                                       rs.getBoolean("bpjs_referensi_diagnosa"),
                                       rs.getBoolean("bpjs_referensi_poli"),
                                       rs.getBoolean("industrifarmasi"),
                                       rs.getBoolean("harian_js"),
                                       rs.getBoolean("bulanan_js"),
                                       rs.getBoolean("harian_paket_bhp"),
                                       rs.getBoolean("bulanan_paket_bhp"),
                                       rs.getBoolean("piutang_pasien2"),
                                       rs.getBoolean("bpjs_referensi_faskes"),
                                       rs.getBoolean("bpjs_sep"),
                                       rs.getBoolean("pengambilan_utd"),
                                       rs.getBoolean("tarif_utd"),
                                       rs.getBoolean("pengambilan_utd2"),
                                       rs.getBoolean("utd_medis_rusak"),
                                       rs.getBoolean("pengambilan_penunjang_utd"),
                                       rs.getBoolean("pengambilan_penunjang_utd2"),
                                       rs.getBoolean("utd_penunjang_rusak"),
                                       rs.getBoolean("suplier_penunjang"),
                                       rs.getBoolean("utd_donor"),
                                       rs.getBoolean("bpjs_monitoring_klaim"),
                                       rs.getBoolean("utd_cekal_darah"),
                                       rs.getBoolean("utd_komponen_darah"),
                                       rs.getBoolean("utd_stok_darah"),
                                       rs.getBoolean("utd_pemisahan_darah"),
                                       rs.getBoolean("harian_kamar"),
                                       rs.getBoolean("rincian_piutang_pasien"),
                                       rs.getBoolean("keuntungan_beri_obat_nonpiutang"),
                                       rs.getBoolean("reklasifikasi_ralan"),
                                       rs.getBoolean("reklasifikasi_ranap"),
                                       rs.getBoolean("utd_penyerahan_darah"),
                                       rs.getBoolean("hutang_obat"),
                                       rs.getBoolean("riwayat_obat_alkes_bhp"),
                                       rs.getBoolean("sensus_harian_poli"),
                                       rs.getBoolean("rl4a"),
                                       rs.getBoolean("aplicare_referensi_kamar"),
                                       rs.getBoolean("aplicare_ketersediaan_kamar"),
                                       rs.getBoolean("inacbg_klaim_baru_otomatis"),
                                       rs.getBoolean("inacbg_klaim_baru_manual"),
                                       rs.getBoolean("inacbg_coder_nik"),
                                       rs.getBoolean("mutasi_berkas"),
                                       rs.getBoolean("akun_piutang"),
                                       rs.getBoolean("harian_kso"),
                                       rs.getBoolean("bulanan_kso"),
                                       rs.getBoolean("harian_menejemen"),
                                       rs.getBoolean("bulanan_menejemen"),
                                       rs.getBoolean("inhealth_cek_eligibilitas"),
                                       rs.getBoolean("inhealth_referensi_jenpel_ruang_rawat"),
                                       rs.getBoolean("inhealth_referensi_poli"),
                                       rs.getBoolean("inhealth_referensi_faskes"),
                                       rs.getBoolean("inhealth_sjp"),
                                       rs.getBoolean("piutang_ralan"),
                                       rs.getBoolean("piutang_ranap"),
                                       rs.getBoolean("detail_piutang_penjab"),
                                       rs.getBoolean("lama_pelayanan_ralan"),
                                       rs.getBoolean("catatan_pasien"),
                                       rs.getBoolean("rl4b"),
                                       rs.getBoolean("rl4asebab"),
                                       rs.getBoolean("rl4bsebab"),
                                       rs.getBoolean("data_HAIs"),
                                       rs.getBoolean("harian_HAIs"),
                                       rs.getBoolean("bulanan_HAIs"),
                                       rs.getBoolean("hitung_bor"),
                                       rs.getBoolean("perusahaan_pasien"),
                                       rs.getBoolean("resep_dokter"),
                                       rs.getBoolean("lama_pelayanan_apotek"),
                                       rs.getBoolean("hitung_alos"),
                                       rs.getBoolean("detail_tindakan"),
                                       rs.getBoolean("rujukan_poli_internal"),
                                       rs.getBoolean("rekap_poli_anak"),
                                       rs.getBoolean("grafik_kunjungan_poli"),
                                       rs.getBoolean("grafik_kunjungan_perdokter"),
                                       rs.getBoolean("grafik_kunjungan_perpekerjaan"),
                                       rs.getBoolean("grafik_kunjungan_perpendidikan"),
                                       rs.getBoolean("grafik_kunjungan_pertahun"),
                                       rs.getBoolean("berkas_digital_perawatan"),
                                       rs.getBoolean("penyakit_menular_ranap"),
                                       rs.getBoolean("penyakit_menular_ralan"),
                                       rs.getBoolean("grafik_kunjungan_perbulan"),
                                       rs.getBoolean("grafik_kunjungan_pertanggal"),
                                       rs.getBoolean("grafik_kunjungan_demografi"),
                                       rs.getBoolean("grafik_kunjungan_statusdaftartahun"),
                                       rs.getBoolean("grafik_kunjungan_statusdaftartahun2"),
                                       rs.getBoolean("grafik_kunjungan_statusdaftarbulan"),
                                       rs.getBoolean("grafik_kunjungan_statusdaftarbulan2"),
                                       rs.getBoolean("grafik_kunjungan_statusdaftartanggal"),
                                       rs.getBoolean("grafik_kunjungan_statusdaftartanggal2"),
                                       rs.getBoolean("grafik_kunjungan_statusbataltahun"),
                                       rs.getBoolean("grafik_kunjungan_statusbatalbulan"),
                                       rs.getBoolean("pcare_cek_penyakit"),
                                       rs.getBoolean("grafik_kunjungan_statusbataltanggal"),
                                       rs.getBoolean("kategori_barang"),
                                       rs.getBoolean("golongan_barang"),
                                       rs.getBoolean("pemberian_obat_pertanggal"),
                                       rs.getBoolean("penjualan_obat_pertanggal"),
                                       rs.getBoolean("pcare_cek_kesadaran"),
                                       rs.getBoolean("pembatalan_periksa_dokter"),
                                       rs.getBoolean("pembayaran_per_unit"),
                                       rs.getBoolean("rekap_pembayaran_per_unit"),
                                       rs.getBoolean("grafik_kunjungan_percarabayar"),
                                       rs.getBoolean("ipsrs_pengadaan_pertanggal"),
                                       rs.getBoolean("ipsrs_stokkeluar_pertanggal"),
                                       rs.getBoolean("grafik_kunjungan_ranaptahun"),
                                       rs.getBoolean("pcare_cek_rujukan"),
                                       rs.getBoolean("grafik_lab_ralantahun"),
                                       rs.getBoolean("grafik_rad_ralantahun"),
                                       rs.getBoolean("cek_entry_ralan"),
                                       rs.getBoolean("inacbg_klaim_baru_manual2"),
                                       rs.getBoolean("permintaan_medis"),
                                       rs.getBoolean("rekap_permintaan_medis"),
                                       rs.getBoolean("surat_pemesanan_medis"),
                                       rs.getBoolean("permintaan_non_medis"),
                                       rs.getBoolean("rekap_permintaan_non_medis"),
                                       rs.getBoolean("surat_pemesanan_non_medis"),
                                       rs.getBoolean("grafik_per_perujuk"),
                                       rs.getBoolean("bpjs_cek_prosedur"),
                                       rs.getBoolean("bpjs_cek_kelas_rawat"),
                                       rs.getBoolean("bpjs_cek_dokter"),
                                       rs.getBoolean("bpjs_cek_spesialistik"),
                                       rs.getBoolean("bpjs_cek_ruangrawat"),
                                       rs.getBoolean("bpjs_cek_carakeluar"),
                                       rs.getBoolean("bpjs_cek_pasca_pulang"),
                                       rs.getBoolean("detail_tindakan_okvk"),
                                       rs.getBoolean("billing_parsial"),
                                       rs.getBoolean("bpjs_cek_nomor_rujukan_rs"),
                                       rs.getBoolean("bpjs_cek_rujukan_kartu_pcare"),
                                       rs.getBoolean("bpjs_cek_rujukan_kartu_rs"),
                                       rs.getBoolean("akses_depo_obat"),
                                       rs.getBoolean("bpjs_rujukan_keluar"),
                                       rs.getBoolean("grafik_lab_ralanbulan"),
                                       rs.getBoolean("pengeluaran_stok_apotek"),
                                       rs.getBoolean("grafik_rad_ralanbulan"),
                                       rs.getBoolean("detailjmdokter2"),
                                       rs.getBoolean("pengaduan_pasien"),
                                       rs.getBoolean("grafik_lab_ralanhari"),
                                       rs.getBoolean("grafik_rad_ralanhari") ,
                                       rs.getBoolean("sensus_harian_ralan"),
                                       rs.getBoolean("metode_racik"),
                                       rs.getBoolean("pembayaran_akun_bayar"),
                                       rs.getBoolean("pengguna_obat_resep"),
                                       rs.getBoolean("rekap_pemesanan"),
                                       rs.getBoolean("master_berkas_pegawai"),
                                       rs.getBoolean("berkas_kepegawaian"),
                                       rs.getBoolean("riwayat_jabatan"),
                                       rs.getBoolean("riwayat_pendidikan"),
                                       rs.getBoolean("riwayat_naik_gaji"),
                                       rs.getBoolean("kegiatan_ilmiah"),
                                       rs.getBoolean("riwayat_penghargaan"),
                                       rs.getBoolean("riwayat_penelitian"),
                                       rs.getBoolean("penerimaan_non_medis"),
                                       rs.getBoolean("bayar_pesan_non_medis"),
                                       rs.getBoolean("hutang_barang_non_medis"),
                                       rs.getBoolean("rekap_pemesanan_non_medis"),
                                       rs.getBoolean("insiden_keselamatan"),
                                       rs.getBoolean("insiden_keselamatan_pasien"),
                                       rs.getBoolean("grafik_ikp_pertahun"),
                                       rs.getBoolean("grafik_ikp_perbulan"),
                                       rs.getBoolean("grafik_ikp_pertanggal"),
                                       rs.getBoolean("riwayat_data_batch"),
                                       rs.getBoolean("grafik_ikp_jenis"),
                                       rs.getBoolean("grafik_ikp_dampak"),
                                       rs.getBoolean("piutang_akun_piutang"),
                                       rs.getBoolean("grafik_kunjungan_per_agama"),
                                       rs.getBoolean("grafik_kunjungan_per_umur"),
                                       rs.getBoolean("suku_bangsa"),
                                       rs.getBoolean("bahasa_pasien"),
                                       rs.getBoolean("golongan_tni"),
                                       rs.getBoolean("satuan_tni"),
                                       rs.getBoolean("jabatan_tni"),
                                       rs.getBoolean("pangkat_tni"),
                                       rs.getBoolean("golongan_polri"),
                                       rs.getBoolean("satuan_polri"),
                                       rs.getBoolean("jabatan_polri"),
                                       rs.getBoolean("pangkat_polri"),
                                       rs.getBoolean("cacat_fisik"),
                                       rs.getBoolean("grafik_kunjungan_suku"),
                                       rs.getBoolean("grafik_kunjungan_bahasa"),
                                       rs.getBoolean("booking_operasi"),
                                       rs.getBoolean("mapping_poli_bpjs"),
                                       rs.getBoolean("grafik_kunjungan_per_cacat"),
                                       rs.getBoolean("barang_cssd"),
                                       rs.getBoolean("skdp_bpjs"),
                                       rs.getBoolean("booking_registrasi"),
                                       rs.getBoolean("bpjs_cek_propinsi"),
                                       rs.getBoolean("bpjs_cek_kabupaten"),
                                       rs.getBoolean("bpjs_cek_kecamatan"),
                                       rs.getBoolean("bpjs_cek_dokterdpjp"),
                                       rs.getBoolean("bpjs_cek_riwayat_rujukanrs"),
                                       rs.getBoolean("bpjs_cek_tanggal_rujukan"),
                                       rs.getBoolean("permintaan_lab"),
                                       rs.getBoolean("permintaan_radiologi"),
                                       rs.getBoolean("surat_indeks"),
                                       rs.getBoolean("surat_map"),
                                       rs.getBoolean("surat_almari"),
                                       rs.getBoolean("surat_rak"),
                                       rs.getBoolean("surat_ruang"),
                                       rs.getBoolean("surat_klasifikasi"),
                                       rs.getBoolean("surat_status"),
                                       rs.getBoolean("surat_sifat"),
                                       rs.getBoolean("surat_balas"),
                                       rs.getBoolean("surat_masuk"),
                                       rs.getBoolean("pcare_cek_dokter"),
                                       rs.getBoolean("pcare_cek_poli"),
                                       rs.getBoolean("pcare_cek_provider"),
                                       rs.getBoolean("pcare_cek_statuspulang"),
                                       rs.getBoolean("pcare_cek_spesialis"),
                                       rs.getBoolean("pcare_cek_subspesialis"),
                                       rs.getBoolean("pcare_cek_sarana"),
                                       rs.getBoolean("pcare_cek_khusus"),
                                       rs.getBoolean("pcare_cek_obat"),
                                       rs.getBoolean("pcare_cek_tindakan"),
                                       rs.getBoolean("pcare_cek_faskessubspesialis"),
                                       rs.getBoolean("pcare_cek_faskesalihrawat"),
                                       rs.getBoolean("pcare_cek_faskesthalasemia"),
                                       rs.getBoolean("pcare_mapping_obat"),
                                       rs.getBoolean("pcare_mapping_tindakan"),
                                       rs.getBoolean("pcare_club_prolanis"),
                                       rs.getBoolean("pcare_mapping_poli"),
                                       rs.getBoolean("pcare_kegiatan_kelompok"),
                                       rs.getBoolean("pcare_mapping_tindakan_ranap"),
                                       rs.getBoolean("pcare_peserta_kegiatan_kelompok"),
                                       rs.getBoolean("sirkulasi_obat3"),
                                       rs.getBoolean("bridging_pcare_daftar"),
                                       rs.getBoolean("pcare_mapping_dokter"),
                                       rs.getBoolean("ranap_per_ruang"),
                                       rs.getBoolean("penyakit_ranap_cara_bayar"),
                                       rs.getBoolean("anggota_militer_dirawat"),
                                       rs.getBoolean("set_input_parsial"),
                                       rs.getBoolean("lama_pelayanan_radiologi"),
                                       rs.getBoolean("lama_pelayanan_lab"),
                                       rs.getBoolean("bpjs_cek_sep"),
                                       rs.getBoolean("catatan_perawatan"),
                                       rs.getBoolean("surat_keluar"),
                                       rs.getBoolean("kegiatan_farmasi"),
                                       rs.getBoolean("stok_opname_logistik"),
                                       rs.getBoolean("sirkulasi_non_medis"),
                                       rs.getBoolean("rekap_lab_pertahun"),
                                       rs.getBoolean("perujuk_lab_pertahun"),
                                       rs.getBoolean("rekap_radiologi_pertahun"),
                                       rs.getBoolean("perujuk_radiologi_pertahun"),
                                       rs.getBoolean("jumlah_porsi_diet"),
                                       rs.getBoolean("jumlah_macam_diet"),
                                       rs.getBoolean("payment_point2"),
                                       rs.getBoolean("pembayaran_akun_bayar2"),
                                       rs.getBoolean("hapus_nota_salah"),
                                       rs.getBoolean("pengkajian_askep"),
                                       rs.getBoolean("hais_perbangsal"),
                                       rs.getBoolean("ppn_obat"),
                                       rs.getBoolean("saldo_akun_perbulan"),
                                       rs.getBoolean("display_apotek"),
                                       rs.getBoolean("sisrute_referensi_faskes"),
                                       rs.getBoolean("sisrute_referensi_alasanrujuk"),
                                       rs.getBoolean("sisrute_referensi_diagnosa"),
                                       rs.getBoolean("sisrute_rujukan_masuk"),
                                       rs.getBoolean("sisrute_rujukan_keluar"),
                                       rs.getBoolean("bpjs_cek_skdp"),
                                       rs.getBoolean("data_batch"),
                                       rs.getBoolean("kunjungan_permintaan_lab"),
                                       rs.getBoolean("kunjungan_permintaan_lab2"),
                                       rs.getBoolean("kunjungan_permintaan_radiologi"),
                                       rs.getBoolean("kunjungan_permintaan_radiologi2"),
                                       rs.getBoolean("pcare_pemberian_obat"),
                                       rs.getBoolean("pcare_pemberian_tindakan"),
                                       rs.getBoolean("pembayaran_akun_bayar3"),
                                       rs.getBoolean("password_asuransi"),
                                       rs.getBoolean("kemenkes_sitt"),
                                       rs.getBoolean("siranap_ketersediaan_kamar"),
                                       rs.getBoolean("grafik_tb_periodelaporan"),
                                       rs.getBoolean("grafik_tb_rujukan"),
                                       rs.getBoolean("grafik_tb_riwayat"),
                                       rs.getBoolean("grafik_tb_tipediagnosis"),
                                       rs.getBoolean("grafik_tb_statushiv"),
                                       rs.getBoolean("grafik_tb_skoringanak"),
                                       rs.getBoolean("grafik_tb_konfirmasiskoring5"),
                                       rs.getBoolean("grafik_tb_konfirmasiskoring6"),
                                       rs.getBoolean("grafik_tb_sumberobat"),
                                       rs.getBoolean("grafik_tb_hasilakhirpengobatan"),
                                       rs.getBoolean("grafik_tb_hasilteshiv"),
                                       rs.getBoolean("kadaluarsa_batch"),
                                       rs.getBoolean("sisa_stok"),
                                       rs.getBoolean("obat_per_resep"),
                                       rs.getBoolean("pemakaian_air_pdam"),
                                       rs.getBoolean("limbah_b3_medis"),
                                       rs.getBoolean("grafik_air_pdam_pertanggal"),
                                       rs.getBoolean("grafik_air_pdam_perbulan"),
                                       rs.getBoolean("grafik_limbahb3_pertanggal"),
                                       rs.getBoolean("grafik_limbahb3_perbulan"),
                                       rs.getBoolean("limbah_domestik"),
                                       rs.getBoolean("grafik_limbahdomestik_pertanggal"),
                                       rs.getBoolean("grafik_limbahdomestik_perbulan"),
                                       rs.getBoolean("mutu_air_limbah"),
                                       rs.getBoolean("pest_control"),
                                       rs.getBoolean("ruang_perpustakaan"),
                                       rs.getBoolean("kategori_perpustakaan"),
                                       rs.getBoolean("jenis_perpustakaan"),
                                       rs.getBoolean("pengarang_perpustakaan"),
                                       rs.getBoolean("penerbit_perpustakaan"),
                                       rs.getBoolean("koleksi_perpustakaan"),
                                       rs.getBoolean("inventaris_perpustakaan"),
                                       rs.getBoolean("set_peminjaman_perpustakaan"),
                                       rs.getBoolean("denda_perpustakaan"),
                                       rs.getBoolean("anggota_perpustakaan"),
                                       rs.getBoolean("peminjaman_perpustakaan"),
                                       rs.getBoolean("bayar_denda_perpustakaan"),
                                       rs.getBoolean("ebook_perpustakaan"),
                                       rs.getBoolean("jenis_cidera_k3rs"),
                                       rs.getBoolean("penyebab_k3rs"),
                                       rs.getBoolean("jenis_luka_k3rs"),
                                       rs.getBoolean("lokasi_kejadian_k3rs"),
                                       rs.getBoolean("dampak_cidera_k3rs"),
                                       rs.getBoolean("jenis_pekerjaan_k3rs"),
                                       rs.getBoolean("bagian_tubuh_k3rs"),
                                       rs.getBoolean("peristiwa_k3rs"),
                                       rs.getBoolean("grafik_k3_pertahun"),
                                       rs.getBoolean("grafik_k3_perbulan"),
                                       rs.getBoolean("grafik_k3_pertanggal"),
                                       rs.getBoolean("grafik_k3_perjeniscidera"),
                                       rs.getBoolean("grafik_k3_perpenyebab"),
                                       rs.getBoolean("grafik_k3_perjenisluka"),
                                       rs.getBoolean("grafik_k3_lokasikejadian"),
                                       rs.getBoolean("grafik_k3_dampakcidera"),
                                       rs.getBoolean("grafik_k3_perjenispekerjaan"),
                                       rs.getBoolean("grafik_k3_perbagiantubuh"),
                                       rs.getBoolean("jenis_cidera_k3rstahun"),
                                       rs.getBoolean("penyebab_k3rstahun"),
                                       rs.getBoolean("jenis_luka_k3rstahun"),
                                       rs.getBoolean("lokasi_kejadian_k3rstahun"),
                                       rs.getBoolean("dampak_cidera_k3rstahun"),
                                       rs.getBoolean("jenis_pekerjaan_k3rstahun"),
                                       rs.getBoolean("bagian_tubuh_k3rstahun"),
                                       rs.getBoolean("sekrining_rawat_jalan"),
                                       rs.getBoolean("bpjs_histori_pelayanan"),
                                       rs.getBoolean("rekap_mutasi_berkas"),
                                       rs.getBoolean("skrining_ralan_pernapasan_pertahun"),
                                       rs.getBoolean("pengajuan_barang_medis"),
                                       rs.getBoolean("pengajuan_barang_nonmedis"),
                                       rs.getBoolean("grafik_kunjungan_ranapbulan"),
                                       rs.getBoolean("grafik_kunjungan_ranaptanggal"),
                                       rs.getBoolean("grafik_kunjungan_ranap_peruang"),
                                       rs.getBoolean("kunjungan_bangsal_pertahun"),
                                       rs.getBoolean("grafik_jenjang_jabatanpegawai"),
                                       rs.getBoolean("grafik_bidangpegawai"),
                                       rs.getBoolean("grafik_departemenpegawai"),
                                       rs.getBoolean("grafik_pendidikanpegawai"),
                                       rs.getBoolean("grafik_sttswppegawai"),
                                       rs.getBoolean("grafik_sttskerjapegawai"),
                                       rs.getBoolean("grafik_sttspulangranap"),
                                       rs.getBoolean("kip_pasien_ranap"),
                                       rs.getBoolean("kip_pasien_ralan"),
                                       rs.getBoolean("bpjs_mapping_dokterdpjp"),
                                       rs.getBoolean("data_triase_igd"),
                                       rs.getBoolean("master_triase_skala1"),
                                       rs.getBoolean("master_triase_skala2"),
                                       rs.getBoolean("master_triase_skala3"),
                                       rs.getBoolean("master_triase_skala4"),
                                       rs.getBoolean("master_triase_skala5"),
                                       rs.getBoolean("master_triase_pemeriksaan"),
                                       rs.getBoolean("master_triase_macamkasus"),
                                       rs.getBoolean("rekap_permintaan_diet"),
                                       rs.getBoolean("daftar_pasien_ranap"),
                                       rs.getBoolean("daftar_pasien_ranaptni"),
                                       rs.getBoolean("pengajuan_asetinventaris"),
                                       rs.getBoolean("item_apotek_jenis"),
                                       rs.getBoolean("item_apotek_kategori"),
                                       rs.getBoolean("item_apotek_golongan"),
                                       rs.getBoolean("item_apotek_industrifarmasi"),
                                       rs.getBoolean("10_obat_terbanyak_poli"),
                                       rs.getBoolean("grafik_pengajuan_aset_urgensi"),
                                       rs.getBoolean("grafik_pengajuan_aset_status"),
                                       rs.getBoolean("grafik_pengajuan_aset_departemen"),
                                       rs.getBoolean("rekap_pengajuan_aset_departemen"),
                                       rs.getBoolean("grafik_kelompok_jabatanpegawai"),
                                       rs.getBoolean("grafik_resiko_kerjapegawai"),
                                       rs.getBoolean("grafik_emergency_indexpegawai"),
                                       rs.getBoolean("grafik_inventaris_ruang"),
                                       rs.getBoolean("harian_HAIs2"),
                                       rs.getBoolean("grafik_inventaris_jenis"),
                                       rs.getBoolean("data_resume_pasien"),
                                       rs.getBoolean("perkiraan_biaya_ranap"),
                                       rs.getBoolean("rekap_obat_poli"),
                                       rs.getBoolean("rekap_obat_pasien"),
                                       rs.getBoolean("permintaan_perbaikan_inventaris"),
                                       rs.getBoolean("grafik_HAIs_pasienbangsal"),
                                       rs.getBoolean("grafik_HAIs_pasienbulan"),
                                       rs.getBoolean("grafik_HAIs_laju_vap"),
                                       rs.getBoolean("grafik_HAIs_laju_iad"),
                                       rs.getBoolean("grafik_HAIs_laju_pleb"),
                                       rs.getBoolean("grafik_HAIs_laju_isk"),
                                       rs.getBoolean("grafik_HAIs_laju_ilo"),
                                       rs.getBoolean("grafik_HAIs_laju_hap"),
                                       rs.getBoolean("inhealth_mapping_poli"),
                                       rs.getBoolean("inhealth_mapping_dokter"),
                                       rs.getBoolean("inhealth_mapping_tindakan_ralan"),
                                       rs.getBoolean("inhealth_mapping_tindakan_ranap"),
                                       rs.getBoolean("inhealth_mapping_tindakan_radiologi"),
                                       rs.getBoolean("inhealth_mapping_tindakan_laborat"),
                                       rs.getBoolean("inhealth_mapping_tindakan_operasi"),
                                       rs.getBoolean("hibah_obat_bhp"),
                                       rs.getBoolean("asal_hibah"),
                                       rs.getBoolean("asuhan_gizi"),
                                       rs.getBoolean("inhealth_kirim_tagihan"),
                                       rs.getBoolean("sirkulasi_obat4"),
                                       rs.getBoolean("sirkulasi_obat5"),
                                       rs.getBoolean("sirkulasi_non_medis2"),
                                       rs.getBoolean("monitoring_asuhan_gizi"),
                                       rs.getBoolean("penerimaan_obat_perbulan"),
                                       rs.getBoolean("rekap_kunjungan"),
                                       rs.getBoolean("surat_sakit"),
                                       rs.getBoolean("penilaian_awal_keperawatan_ralan"),
                                       rs.getBoolean("permintaan_diet"),
                                       rs.getBoolean("master_masalah_keperawatan"),
                                       rs.getBoolean("pengajuan_cuti"),
                                       rs.getBoolean("kedatangan_pasien"),
                                       rs.getBoolean("utd_pendonor"),
                                       rs.getBoolean("toko_suplier"),
                                       rs.getBoolean("toko_jenis"),
                                       rs.getBoolean("toko_set_harga"),
                                       rs.getBoolean("toko_barang"),
                                       rs.getBoolean("penagihan_piutang_pasien"),
                                       rs.getBoolean("akun_penagihan_piutang"),
                                       rs.getBoolean("stok_opname_toko"),
                                       rs.getBoolean("toko_riwayat_barang"),
                                       rs.getBoolean("toko_surat_pemesanan"),
                                       rs.getBoolean("toko_pengajuan_barang"),
                                       rs.getBoolean("toko_penerimaan_barang"),
                                       rs.getBoolean("toko_pengadaan_barang"),
                                       rs.getBoolean("toko_hutang"),
                                       rs.getBoolean("toko_bayar_pemesanan"),
                                       rs.getBoolean("toko_member"),
                                       rs.getBoolean("toko_penjualan"),
                                       rs.getBoolean("registrasi_poli_per_tanggal"),
                                       rs.getBoolean("toko_piutang"),
                                       rs.getBoolean("toko_retur_beli"),
                                       rs.getBoolean("ipsrs_returbeli"),
                                       rs.getBoolean("ipsrs_riwayat_barang"),
                                       rs.getBoolean("pasien_corona"),
                                       rs.getBoolean("toko_pendapatan_harian"),
                                       rs.getBoolean("diagnosa_pasien_corona"),
                                       rs.getBoolean("perawatan_pasien_corona"),
                                       rs.getBoolean("penilaian_awal_keperawatan_gigi"),
                                       rs.getBoolean("master_masalah_keperawatan_gigi"),
                                       rs.getBoolean("toko_bayar_piutang"),
                                       rs.getBoolean("toko_piutang_harian"),
                                       rs.getBoolean("toko_penjualan_harian"),
                                       rs.getBoolean("deteksi_corona"),
                                       rs.getBoolean("penilaian_awal_keperawatan_kebidanan"),
                                       rs.getBoolean("pengumuman_epasien"),
                                       rs.getBoolean("surat_hamil"),
                                       rs.getBoolean("set_tarif_online"),
                                       rs.getBoolean("booking_periksa"),
                                       rs.getBoolean("toko_sirkulasi"),
                                       rs.getBoolean("toko_retur_jual"),
                                       rs.getBoolean("toko_retur_piutang"),
                                       rs.getBoolean("toko_sirkulasi2"),
                                       rs.getBoolean("toko_keuntungan_barang"),
                                       rs.getBoolean("zis_pengeluaran_penerima_dankes"),
                                       rs.getBoolean("zis_penghasilan_penerima_dankes"),
                                       rs.getBoolean("zis_ukuran_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_dinding_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_lantai_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_atap_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_kepemilikan_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_kamar_mandi_penerima_dankes"),
                                       rs.getBoolean("zis_dapur_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_kursi_rumah_penerima_dankes"),
                                       rs.getBoolean("zis_kategori_phbs_penerima_dankes"),
                                       rs.getBoolean("zis_elektronik_penerima_dankes"),
                                       rs.getBoolean("zis_ternak_penerima_dankes"),
                                       rs.getBoolean("zis_jenis_simpanan_penerima_dankes"),
                                       rs.getBoolean("penilaian_awal_keperawatan_anak"),
                                       rs.getBoolean("zis_kategori_asnaf_penerima_dankes"),
                                       rs.getBoolean("master_masalah_keperawatan_anak"),
                                       rs.getBoolean("master_imunisasi"),
                                       rs.getBoolean("zis_patologis_penerima_dankes"),
                                       rs.getBoolean("pcare_cek_kartu"),
                                       rs.getBoolean("surat_bebas_narkoba"),
                                       rs.getBoolean("surat_keterangan_covid"),
                                       rs.getBoolean("pemakaian_air_tanah"),
                                       rs.getBoolean("grafik_air_tanah_pertanggal"),
                                       rs.getBoolean("grafik_air_tanah_perbulan"),
                                       rs.getBoolean("lama_pelayanan_poli"),
                                       rs.getBoolean("hemodialisa"),
                                       rs.getBoolean("laporan_tahunan_irj"),
                                       rs.getBoolean("grafik_harian_hemodialisa"),
                                       rs.getBoolean("grafik_bulanan_hemodialisa"),
                                       rs.getBoolean("grafik_tahunan_hemodialisa"),
                                       rs.getBoolean("grafik_bulanan_meninggal"),
                                       rs.getBoolean("perbaikan_inventaris"),
                                       rs.getBoolean("surat_cuti_hamil"),
                                       rs.getBoolean("permintaan_stok_obat_pasien"),
                                       rs.getBoolean("pemeliharaan_inventaris"),
                                       rs.getBoolean("klasifikasi_pasien_ranap"),
                                       rs.getBoolean("bulanan_klasifikasi_pasien_ranap"),
                                       rs.getBoolean("harian_klasifikasi_pasien_ranap"),
                                       rs.getBoolean("klasifikasi_pasien_perbangsal"),
                                       rs.getBoolean("soap_perawatan"),
                                       rs.getBoolean("klaim_rawat_jalan"),
                                       rs.getBoolean("skrining_gizi"),
                                       rs.getBoolean("lama_penyiapan_rm"),
                                       rs.getBoolean("dosis_radiologi"),
                                       rs.getBoolean("demografi_umur_kunjungan"),
                                       rs.getBoolean("jam_diet_pasien"),
                                       rs.getBoolean("rvu_bpjs"),
                                       rs.getBoolean("verifikasi_penerimaan_farmasi"),
                                       rs.getBoolean("verifikasi_penerimaan_logistik"),
                                       rs.getBoolean("pemeriksaan_lab_pa"),
                                       rs.getBoolean("ringkasan_pengajuan_obat"),
                                       rs.getBoolean("ringkasan_pemesanan_obat"),
                                       rs.getBoolean("ringkasan_pengadaan_obat"),
                                       rs.getBoolean("ringkasan_penerimaan_obat"),
                                       rs.getBoolean("ringkasan_hibah_obat"),
                                       rs.getBoolean("ringkasan_penjualan_obat"),
                                       rs.getBoolean("ringkasan_beri_obat"),
                                       rs.getBoolean("ringkasan_piutang_obat"),
                                       rs.getBoolean("ringkasan_stok_keluar_obat"),
                                       rs.getBoolean("ringkasan_retur_suplier_obat"),
                                       rs.getBoolean("ringkasan_retur_pembeli_obat"),
                                       rs.getBoolean("penilaian_awal_keperawatan_ranapkebidanan"),
                                       rs.getBoolean("ringkasan_pengajuan_nonmedis"),
                                       rs.getBoolean("ringkasan_pemesanan_nonmedis"),
                                       rs.getBoolean("ringkasan_pengadaan_nonmedis"),
                                       rs.getBoolean("ringkasan_penerimaan_nonmedis"),
                                       rs.getBoolean("ringkasan_stokkeluar_nonmedis"),
                                       rs.getBoolean("ringkasan_returbeli_nonmedis"),
                                       rs.getBoolean("omset_penerimaan"),
                                       rs.getBoolean("validasi_penagihan_piutang"),
                                       rs.getBoolean("permintaan_ranap"),
                                       rs.getBoolean("bpjs_diagnosa_prb"),
                                       rs.getBoolean("bpjs_obat_prb"),
                                       rs.getBoolean("bpjs_surat_kontrol"),
                                       rs.getBoolean("penggunaan_bhp_ok"),
                                       rs.getBoolean("surat_keterangan_rawat_inap"),
                                       rs.getBoolean("surat_keterangan_sehat"),
                                       rs.getBoolean("pendapatan_per_carabayar"),
                                       rs.getBoolean("akun_host_to_host_bank_jateng"),
                                       rs.getBoolean("pembayaran_bank_jateng"),
                                       rs.getBoolean("bpjs_surat_pri"),
                                       rs.getBoolean("ringkasan_tindakan"),
                                       rs.getBoolean("lama_pelayanan_pasien"),
                                       rs.getBoolean("surat_sakit_pihak_2"),
                                       rs.getBoolean("tagihan_hutang_obat"),
                                       rs.getBoolean("referensi_mobilejkn_bpjs"),
                                       rs.getBoolean("batal_pendaftaran_mobilejkn_bpjs"),
                                       rs.getBoolean("lama_operasi"),
                                       rs.getBoolean("grafik_inventaris_kategori"),
                                       rs.getBoolean("grafik_inventaris_merk"),
                                       rs.getBoolean("grafik_inventaris_produsen"),
                                       rs.getBoolean("pengembalian_deposit_pasien"),
                                       rs.getBoolean("validasi_tagihan_hutang_obat"),
                                       rs.getBoolean("piutang_obat_belum_lunas"),
                                       rs.getBoolean("integrasi_briapi"),
                                       rs.getBoolean("pengadaan_aset_inventaris"),
                                       rs.getBoolean("akun_aset_inventaris"),
                                       rs.getBoolean("suplier_inventaris"),
                                       rs.getBoolean("penerimaan_aset_inventaris"),
                                       rs.getBoolean("bayar_pemesanan_iventaris"),
                                       rs.getBoolean("hutang_aset_inventaris"),
                                       rs.getBoolean("hibah_aset_inventaris"),
                                       rs.getBoolean("titip_faktur_non_medis"),
                                       rs.getBoolean("validasi_tagihan_non_medis"),
                                       rs.getBoolean("titip_faktur_aset"),
                                       rs.getBoolean("validasi_tagihan_aset"),
                                       rs.getBoolean("hibah_non_medis"),
                                       rs.getBoolean("pcare_alasan_tacc"),
                                       rs.getBoolean("resep_luar"),
                                       rs.getBoolean("surat_bebas_tbc"),
                                       rs.getBoolean("surat_buta_warna"),
                                       rs.getBoolean("surat_bebas_tato"),
                                       rs.getBoolean("surat_kewaspadaan_kesehatan"),
                                       rs.getBoolean("grafik_porsidiet_pertanggal"),
                                       rs.getBoolean("grafik_porsidiet_perbulan"),
                                       rs.getBoolean("grafik_porsidiet_pertahun"),
                                       rs.getBoolean("grafik_porsidiet_perbangsal"),
                                       rs.getBoolean("penilaian_awal_medis_ralan"),
                                       rs.getBoolean("master_masalah_keperawatan_mata"),
                                       rs.getBoolean("penilaian_awal_keperawatan_mata"),
                                       rs.getBoolean("penilaian_awal_medis_ranap"),
                                       rs.getBoolean("penilaian_awal_medis_ranap_kebidanan"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_kebidanan"),
                                       rs.getBoolean("penilaian_awal_medis_igd"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_anak"),
                                       rs.getBoolean("bpjs_referensi_poli_hfis"),
                                       rs.getBoolean("bpjs_referensi_dokter_hfis"),
                                       rs.getBoolean("bpjs_referensi_jadwal_hfis"),
                                       rs.getBoolean("penilaian_fisioterapi"),
                                       rs.getBoolean("bpjs_program_prb"),
                                       rs.getBoolean("bpjs_suplesi_jasaraharja"),
                                       rs.getBoolean("bpjs_data_induk_kecelakaan"),
                                       rs.getBoolean("bpjs_sep_internal"),
                                       rs.getBoolean("bpjs_klaim_jasa_raharja"),
                                       rs.getBoolean("bpjs_daftar_finger_print"),
                                       rs.getBoolean("bpjs_rujukan_khusus"),
                                       rs.getBoolean("pemeliharaan_gedung"),
                                       rs.getBoolean("grafik_perbaikan_inventaris_pertanggal"),
                                       rs.getBoolean("grafik_perbaikan_inventaris_perbulan"),
                                       rs.getBoolean("grafik_perbaikan_inventaris_pertahun"),
                                       rs.getBoolean("grafik_perbaikan_inventaris_perpelaksana_status"),
                                       rs.getBoolean("penilaian_mcu"),
                                       rs.getBoolean("peminjam_piutang"),
                                       rs.getBoolean("piutang_lainlain"),
                                       rs.getBoolean("cara_bayar"),
                                       rs.getBoolean("audit_kepatuhan_apd"),
                                       rs.getBoolean("bpjs_task_id"),
                                       rs.getBoolean("bayar_piutang_lain"),
                                       rs.getBoolean("pembayaran_akun_bayar4"),
                                       rs.getBoolean("stok_akhir_farmasi_pertanggal"),
                                       rs.getBoolean("riwayat_kamar_pasien"),
                                       rs.getBoolean("uji_fungsi_kfr"),
                                       rs.getBoolean("hapus_berkas_digital_perawatan"),
                                       rs.getBoolean("kategori_pengeluaran_harian"),
                                       rs.getBoolean("kategori_pemasukan_lain"),
                                       rs.getBoolean("pembayaran_akun_bayar5"),
                                       rs.getBoolean("ruang_ok"),
                                       rs.getBoolean("telaah_resep"),
                                       rs.getBoolean("jasa_tindakan_pasien"),
                                       rs.getBoolean("permintaan_resep_pulang"),
                                       rs.getBoolean("rekap_jm_dokter"),
                                       rs.getBoolean("status_data_rm"),
                                       rs.getBoolean("ubah_petugas_lab_pk"),
                                       rs.getBoolean("ubah_petugas_lab_pa"),
                                       rs.getBoolean("ubah_petugas_radiologi"),
                                       rs.getBoolean("gabung_norawat"),
                                       rs.getBoolean("gabung_rm"),
                                       rs.getBoolean("ringkasan_biaya_obat_pasien_pertanggal"),
                                       rs.getBoolean("master_masalah_keperawatan_igd"),
                                       rs.getBoolean("penilaian_awal_keperawatan_igd"),
                                       rs.getBoolean("bpjs_referensi_dpho_apotek"),
                                       rs.getBoolean("bpjs_referensi_poli_apotek"),
                                       rs.getBoolean("bayar_jm_dokter"),
                                       rs.getBoolean("bpjs_referensi_faskes_apotek"),
                                       rs.getBoolean("bpjs_referensi_spesialistik_apotek"),
                                       rs.getBoolean("pembayaran_briva"),
                                       rs.getBoolean("penilaian_awal_keperawatan_ranap"),
                                       rs.getBoolean("nilai_penerimaan_vendor_farmasi_perbulan"),
                                       rs.getBoolean("akun_bayar_hutang"),
                                       rs.getBoolean("master_rencana_keperawatan"),
                                       rs.getBoolean("laporan_tahunan_igd"),
                                       rs.getBoolean("obat_bhp_tidakbergerak"),
                                       rs.getBoolean("ringkasan_hutang_vendor_farmasi"),
                                       rs.getBoolean("nilai_penerimaan_vendor_nonmedis_perbulan"),
                                       rs.getBoolean("ringkasan_hutang_vendor_nonmedis"),
                                       rs.getBoolean("master_rencana_keperawatan_anak"),
                                       rs.getBoolean("anggota_polri_dirawat"),
                                       rs.getBoolean("daftar_pasien_ranap_polri"),
                                       rs.getBoolean("soap_ralan_polri"),
                                       rs.getBoolean("soap_ranap_polri"),
                                       rs.getBoolean("laporan_penyakit_polri"),
                                       rs.getBoolean("jumlah_pengunjung_ralan_polri"),
                                       rs.getBoolean("catatan_observasi_igd"),
                                       rs.getBoolean("catatan_observasi_ranap"),
                                       rs.getBoolean("catatan_observasi_ranap_kebidanan"),
                                       rs.getBoolean("catatan_observasi_ranap_postpartum"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_tht"),
                                       rs.getBoolean("penilaian_psikologi"),
                                       rs.getBoolean("audit_cuci_tangan_medis"),
                                       rs.getBoolean("audit_pembuangan_limbah"),
                                       rs.getBoolean("ruang_audit_kepatuhan"),
                                       rs.getBoolean("audit_pembuangan_benda_tajam"),
                                       rs.getBoolean("audit_penanganan_darah"),
                                       rs.getBoolean("audit_pengelolaan_linen_kotor"),
                                       rs.getBoolean("audit_penempatan_pasien"),
                                       rs.getBoolean("audit_kamar_jenazah"),
                                       rs.getBoolean("audit_bundle_iadp"),
                                       rs.getBoolean("audit_bundle_ido"),
                                       rs.getBoolean("audit_fasilitas_kebersihan_tangan"),
                                       rs.getBoolean("audit_fasilitas_apd"),
                                       rs.getBoolean("audit_pembuangan_limbah_cair_infeksius"),
                                       rs.getBoolean("audit_sterilisasi_alat"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_psikiatri"),
                                       rs.getBoolean("persetujuan_penolakan_tindakan"),
                                       rs.getBoolean("audit_bundle_isk"),
                                       rs.getBoolean("audit_bundle_plabsi"),
                                       rs.getBoolean("audit_bundle_vap"),
                                       rs.getBoolean("akun_host_to_host_bank_papua"),
                                       rs.getBoolean("pembayaran_bank_papua"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_penyakit_dalam"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_mata"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_neurologi"),
                                       rs.getBoolean("sirkulasi_obat6"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_orthopedi"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_bedah"),
                                       rs.getBoolean("integrasi_khanza_health_services"),
                                       rs.getBoolean("soap_ralan_tni"),
                                       rs.getBoolean("soap_ranap_tni"),
                                       rs.getBoolean("jumlah_pengunjung_ralan_tni"),
                                       rs.getBoolean("laporan_penyakit_tni"),
                                       rs.getBoolean("catatan_keperawatan_ranap"),
                                       rs.getBoolean("master_rencana_keperawatan_gigi"),
                                       rs.getBoolean("master_rencana_keperawatan_mata"),
                                       rs.getBoolean("master_rencana_keperawatan_igd"),
                                       rs.getBoolean("master_masalah_keperawatan_psikiatri"),
                                       rs.getBoolean("master_rencana_keperawatan_psikiatri"),
                                       rs.getBoolean("penilaian_awal_keperawatan_psikiatri"),
                                       rs.getBoolean("pemantauan_pews_anak"),
                                       rs.getBoolean("surat_pulang_atas_permintaan_sendiri"),
                                       rs.getBoolean("template_hasil_radiologi"),
                                       rs.getBoolean("laporan_bulanan_irj"),
                                       rs.getBoolean("template_pemeriksaan"),
                                       rs.getBoolean("pemeriksaan_lab_mb"),
                                       rs.getBoolean("ubah_petugas_lab_mb"),
                                       rs.getBoolean("penilaian_pre_operasi"),
                                       rs.getBoolean("penilaian_pre_anestesi"),
                                       rs.getBoolean("perencanaan_pemulangan"),
                                       rs.getBoolean("penilaian_lanjutan_resiko_jatuh_dewasa"),
                                       rs.getBoolean("penilaian_lanjutan_resiko_jatuh_anak"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_geriatri"),
                                       rs.getBoolean("penilaian_tambahan_pasien_geriatri"),
                                       rs.getBoolean("skrining_nutrisi_dewasa"),
                                       rs.getBoolean("skrining_nutrisi_lansia"),
                                       rs.getBoolean("hasil_pemeriksaan_usg"),
                                       rs.getBoolean("skrining_nutrisi_anak"),
                                       rs.getBoolean("akun_host_to_host_bank_jabar"),
                                       rs.getBoolean("pembayaran_bank_jabar"),
                                       rs.getBoolean("surat_pernyataan_pasien_umum"),
                                       rs.getBoolean("konseling_farmasi"),
                                       rs.getBoolean("pelayanan_informasi_obat"),
                                       rs.getBoolean("jawaban_pio_apoteker"),
                                       rs.getBoolean("surat_persetujuan_umum"),
                                       rs.getBoolean("transfer_pasien_antar_ruang"),
                                       rs.getBoolean("satu_sehat_referensi_dokter"),
                                       rs.getBoolean("satu_sehat_referensi_pasien"),
                                       rs.getBoolean("satu_sehat_mapping_departemen"),
                                       rs.getBoolean("satu_sehat_mapping_lokasi"),
                                       rs.getBoolean("satu_sehat_kirim_encounter"),
                                       rs.getBoolean("catatan_cek_gds"),
                                       rs.getBoolean("satu_sehat_kirim_condition"),
                                       rs.getBoolean("checklist_pre_operasi"),
                                       rs.getBoolean("satu_sehat_kirim_observationttv"),
                                       rs.getBoolean("signin_sebelum_anestesi"),
                                       rs.getBoolean("satu_sehat_kirim_procedure"),
                                       rs.getBoolean("operasi_per_bulan"),
                                       rs.getBoolean("timeout_sebelum_insisi"),
                                       rs.getBoolean("signout_sebelum_menutup_luka"),
                                       rs.getBoolean("dapur_barang"),
                                       rs.getBoolean("dapur_opname"),
                                       rs.getBoolean("satu_sehat_mapping_vaksin"),
                                       rs.getBoolean("dapur_suplier"),
                                       rs.getBoolean("satu_sehat_kirim_Immunization"),
                                       rs.getBoolean("checklist_post_operasi"),
                                       rs.getBoolean("dapur_pembelian"),
                                       rs.getBoolean("dapur_stok_keluar"),
                                       rs.getBoolean("dapur_riwayat_barang"),
                                       rs.getBoolean("permintaan_dapur"),
                                       rs.getBoolean("rekonsiliasi_obat"),
                                       rs.getBoolean("biaya_pengadaan_dapur"),
                                       rs.getBoolean("rekap_pengadaan_dapur"),
                                       rs.getBoolean("kesling_limbah_b3medis_cair"),
                                       rs.getBoolean("grafik_limbahb3cair_pertanggal"),
                                       rs.getBoolean("grafik_limbahb3cair_perbulan"),
                                       rs.getBoolean("rekap_biaya_registrasi"),
                                       rs.getBoolean("konfirmasi_rekonsiliasi_obat"),
                                       rs.getBoolean("satu_sehat_kirim_clinicalimpression"),
                                       rs.getBoolean("penilaian_pasien_terminal"),
                                       rs.getBoolean("surat_persetujuan_rawat_inap"),
                                       rs.getBoolean("monitoring_reaksi_tranfusi"),
                                       rs.getBoolean("penilaian_korban_kekerasan"),
                                       rs.getBoolean("penilaian_lanjutan_resiko_jatuh_lansia"),
                                       rs.getBoolean("penilaian_pasien_penyakit_menular"),
                                       rs.getBoolean("mpp_skrining"),
                                       rs.getBoolean("edukasi_pasien_keluarga_rj"),
                                       rs.getBoolean("pemantauan_pews_dewasa"),
                                       rs.getBoolean("penilaian_tambahan_bunuh_diri"),
                                       rs.getBoolean("bpjs_antrean_pertanggal"),
                                       rs.getBoolean("penilaian_tambahan_perilaku_kekerasan"),
                                       rs.getBoolean("penilaian_tambahan_beresiko_melarikan_diri"),
                                       rs.getBoolean("persetujuan_penundaan_pelayanan"),
                                       rs.getBoolean("sisa_diet_pasien"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_bedah_mulut"),
                                       rs.getBoolean("penilaian_pasien_keracunan"),
                                       rs.getBoolean("pemantauan_meows_obstetri"),
                                       rs.getBoolean("catatan_adime_gizi"),
                                       rs.getBoolean("pengajuan_biaya"),
                                       rs.getBoolean("penilaian_awal_keperawatan_ralan_geriatri"),
                                       rs.getBoolean("master_masalah_keperawatan_geriatri"),
                                       rs.getBoolean("master_rencana_keperawatan_geriatri"),
                                       rs.getBoolean("checklist_kriteria_masuk_hcu"),
                                       rs.getBoolean("checklist_kriteria_keluar_hcu"),
                                       rs.getBoolean("penilaian_risiko_dekubitus"),
                                       rs.getBoolean("master_menolak_anjuran_medis"),
                                       rs.getBoolean("penolakan_anjuran_medis"),
                                       rs.getBoolean("laporan_tahunan_penolakan_anjuran_medis"),
                                       rs.getBoolean("template_laporan_operasi"),
                                       rs.getBoolean("hasil_tindakan_eswl"),
                                       rs.getBoolean("checklist_kriteria_masuk_icu"),
                                       rs.getBoolean("checklist_kriteria_keluar_icu"),
                                       rs.getBoolean("akses_dokter_lain_rawat_jalan"),
                                       rs.getBoolean("follow_up_dbd"),
                                       rs.getBoolean("penilaian_risiko_jatuh_neonatus"),
                                       rs.getBoolean("persetujuan_pengajuan_biaya"),
                                       rs.getBoolean("pemeriksaan_fisik_ralan_per_penyakit"),
                                       rs.getBoolean("penilaian_lanjutan_resiko_jatuh_geriatri"),
                                       rs.getBoolean("pemantauan_ews_neonatus"),
                                       rs.getBoolean("validasi_persetujuan_pengajuan_biaya"),
                                       rs.getBoolean("riwayat_perawatan_icare_bpjs"),
                                       rs.getBoolean("rekap_pengajuan_biaya"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_kulit_kelamin"),
                                       rs.getBoolean("akun_host_to_host_bank_mandiri"),
                                       rs.getBoolean("penilaian_medis_hemodialisa"),
                                       rs.getBoolean("penilaian_level_kecemasan_ranap_anak"),
                                       rs.getBoolean("penilaian_lanjutan_resiko_jatuh_psikiatri"),
                                       rs.getBoolean("penilaian_lanjutan_skrining_fungsional"),
                                       rs.getBoolean("penilaian_medis_ralan_rehab_medik"),
                                       rs.getBoolean("laporan_anestesi"),
                                       rs.getBoolean("template_persetujuan_penolakan_tindakan"),
                                       rs.getBoolean("penilaian_medis_ralan_gawat_darurat_psikiatri"),
                                       rs.getBoolean("bpjs_referensi_setting_apotek"),
                                       rs.getBoolean("bpjs_referensi_obat_apotek"),
                                       rs.getBoolean("bpjs_mapping_obat_apotek"),
                                       rs.getBoolean("pembayaran_bank_mandiri"),
                                       rs.getBoolean("penilaian_ulang_nyeri"),
                                       rs.getBoolean("penilaian_terapi_wicara"),
                                       rs.getBoolean("bpjs_obat_23hari_apotek"),
                                       rs.getBoolean("pengkajian_restrain"),
                                       rs.getBoolean("bpjs_kunjungan_sep_apotek"),
                                       rs.getBoolean("bpjs_monitoring_klaim_apotek"),
                                       rs.getBoolean("bpjs_daftar_pelayanan_obat_apotek"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_paru"),
                                       rs.getBoolean("catatan_keperawatan_ralan"),
                                       rs.getBoolean("catatan_persalinan"),
                                       rs.getBoolean("skor_aldrette_pasca_anestesi"),
                                       rs.getBoolean("skor_steward_pasca_anestesi"),
                                       rs.getBoolean("skor_bromage_pasca_anestesi"),
                                       rs.getBoolean("penilaian_pre_induksi"),
                                       rs.getBoolean("hasil_usg_urologi"),
                                       rs.getBoolean("hasil_usg_gynecologi"),
                                       rs.getBoolean("hasil_pemeriksaan_ekg"),
                                       rs.getBoolean("hapus_edit_sep_bpjs"),
                                       rs.getBoolean("satu_sehat_kirim_diet"),
                                       rs.getBoolean("satu_sehat_mapping_obat"),
                                       rs.getBoolean("dapur_ringkasan_pembelian"),
                                       rs.getBoolean("satu_sehat_kirim_medication"),
                                       rs.getBoolean("satu_sehat_kirim_medicationrequest"),
                                       rs.getBoolean("penatalaksanaan_terapi_okupasi"),
                                       rs.getBoolean("satu_sehat_kirim_medicationdispense"),
                                       rs.getBoolean("hasil_usg_neonatus"),
                                       rs.getBoolean("hasil_endoskopi_faring_laring"),
                                       rs.getBoolean("satu_sehat_mapping_radiologi"),
                                       rs.getBoolean("satu_sehat_kirim_servicerequest_radiologi"),
                                       rs.getBoolean("hasil_endoskopi_hidung"),
                                       rs.getBoolean("satu_sehat_kirim_specimen_radiologi"),
                                       rs.getBoolean("master_masalah_keperawatan_neonatus"),
                                       rs.getBoolean("master_rencana_keperawatan_neonatus"),
                                       rs.getBoolean("penilaian_awal_keperawatan_ranap_neonatus"),
                                       rs.getBoolean("satu_sehat_kirim_observation_radiologi"),
                                       rs.getBoolean("satu_sehat_kirim_diagnosticreport_radiologi"),
                                       rs.getBoolean("hasil_endoskopi_telinga"),
                                       rs.getBoolean("satu_sehat_mapping_lab"),
                                       rs.getBoolean("satu_sehat_kirim_servicerequest_lab"),
                                       rs.getBoolean("satu_sehat_kirim_servicerequest_labmb"),
                                       rs.getBoolean("satu_sehat_kirim_specimen_lab"),
                                       rs.getBoolean("satu_sehat_kirim_specimen_labmb"),
                                       rs.getBoolean("satu_sehat_kirim_observation_lab"),
                                       rs.getBoolean("satu_sehat_kirim_observation_labmb"),
                                       rs.getBoolean("satu_sehat_kirim_diagnosticreport_lab"),
                                       rs.getBoolean("satu_sehat_kirim_diagnosticreport_labmb"),
                                       rs.getBoolean("kepatuhan_kelengkapan_keselamatan_bedah"),
                                       rs.getBoolean("nilai_piutang_perjenis_bayar_per_bulan"),
                                       rs.getBoolean("ringkasan_piutang_jenis_bayar"),
                                       rs.getBoolean("penilaian_pasien_imunitas_rendah"),
                                       rs.getBoolean("balance_cairan"),
                                       rs.getBoolean("catatan_observasi_chbp"),
                                       rs.getBoolean("catatan_observasi_induksi_persalinan"),
                                       rs.getBoolean("skp_kategori_penilaian"),
                                       rs.getBoolean("skp_kriteria_penilaian"),
                                       rs.getBoolean("skp_penilaian"),
                                       rs.getBoolean("referensi_poli_mobilejknfktp"),
                                       rs.getBoolean("referensi_dokter_mobilejknfktp"),
                                       rs.getBoolean("skp_rekapitulasi_penilaian"),
                                       rs.getBoolean("pembayaran_pihak_ke3_bankmandiri"),
                                       rs.getBoolean("metode_pembayaran_bankmandiri"),
                                       rs.getBoolean("bank_tujuan_transfer_bankmandiri"),
                                       rs.getBoolean("kodetransaksi_tujuan_transfer_bankmandiri"),
                                       rs.getBoolean("konsultasi_medik"),
                                       rs.getBoolean("jawaban_konsultasi_medik"),
                                       rs.getBoolean("pcare_cek_alergi"),
                                       rs.getBoolean("pcare_cek_prognosa"),
                                       rs.getBoolean("data_sasaran_usiaproduktif"),
                                       rs.getBoolean("data_sasaran_usialansia"),
                                       rs.getBoolean("skrining_perilaku_merokok_sekolah_remaja"),
                                       rs.getBoolean("skrining_kekerasan_pada_perempuan"),
                                       rs.getBoolean("skrining_obesitas"),
                                       rs.getBoolean("skrining_risiko_kanker_payudara"),
                                       rs.getBoolean("skrining_risiko_kanker_paru"),
                                       rs.getBoolean("skrining_tbc"),
                                       rs.getBoolean("skrining_kesehatan_gigi_mulut_remaja"),
                                       rs.getBoolean("penilaian_awal_keperawatan_ranap_bayi"),
                                       rs.getBoolean("booking_mcu_perusahaan"),
                                       rs.getBoolean("catatan_observasi_restrain_nonfarma"),
                                       rs.getBoolean("catatan_observasi_ventilator"),
                                       rs.getBoolean("catatan_anestesi_sedasi"),
                                       rs.getBoolean("skrining_puma"),
                                       rs.getBoolean("satu_sehat_kirim_careplan"),
                                       rs.getBoolean("satu_sehat_kirim_medicationstatement"),
                                       rs.getBoolean("skrining_adiksi_nikotin"),
                                       rs.getBoolean("skrining_thalassemia"),
                                       rs.getBoolean("skrining_instrumen_sdq"),
                                       rs.getBoolean("skrining_instrumen_srq"),
                                       rs.getBoolean("checklist_pemberian_fibrinolitik"),
                                       rs.getBoolean("skrining_kanker_kolorektal"),
                                       rs.getBoolean("dapur_pemesanan"),
                                       rs.getBoolean("bayar_pesan_dapur"),
                                       rs.getBoolean("hutang_dapur"),
                                       rs.getBoolean("titip_faktur_dapur"),
                                       rs.getBoolean("validasi_tagihan_dapur"),
                                       rs.getBoolean("surat_pemesanan_dapur"),
                                       rs.getBoolean("pengajuan_barang_dapur"),
                                       rs.getBoolean("dapur_returbeli"),
                                       rs.getBoolean("hibah_dapur"),
                                       rs.getBoolean("ringkasan_penerimaan_dapur"),
                                       rs.getBoolean("ringkasan_pengajuan_dapur"),
                                       rs.getBoolean("ringkasan_pemesanan_dapur"),
                                       rs.getBoolean("ringkasan_returbeli_dapur"),
                                       rs.getBoolean("ringkasan_stokkeluar_dapur"),
                                       rs.getBoolean("dapur_stokkeluar_pertanggal"),
                                       rs.getBoolean("sirkulasi_dapur"),
                                       rs.getBoolean("sirkulasi_dapur2"),
                                       rs.getBoolean("verifikasi_penerimaan_dapur"),
                                       rs.getBoolean("nilai_penerimaan_vendor_dapur_perbulan"),
                                       rs.getBoolean("ringkasan_hutang_vendor_dapur"),
                                       rs.getBoolean("penilaian_psikologi_klinis"),
                                       rs.getBoolean("penilaian_awal_medis_ranap_neonatus"),
                                       rs.getBoolean("penilaian_derajat_dehidrasi"),
                                       rs.getBoolean("ringkasan_jasa_tindakan_medis"),
                                       rs.getBoolean("pendapatan_per_akun"),
                                       rs.getBoolean("hasil_pemeriksaan_echo"),
                                       rs.getBoolean("penilaian_bayi_baru_lahir"),
                                       rs.getBoolean("rl1_3_ketersediaan_kamar"),
                                       rs.getBoolean("pendapatan_per_akun_closing"),
                                       rs.getBoolean("pengeluaran_pengeluaran"),
                                       rs.getBoolean("skrining_diabetes_melitus"),
                                       rs.getBoolean("laporan_tindakan"),
                                       rs.getBoolean("pelaksanaan_informasi_edukasi"),
                                       rs.getBoolean("layanan_kedokteran_fisik_rehabilitasi"),
                                       rs.getBoolean("skrining_kesehatan_gigi_mulut_balita"),
                                       rs.getBoolean("skrining_anemia"),
                                       rs.getBoolean("layanan_program_kfr"),
                                       rs.getBoolean("skrining_hipertensi"),
                                       rs.getBoolean("skrining_kesehatan_penglihatan"),
                                       rs.getBoolean("catatan_observasi_hemodialisa"),
                                       rs.getBoolean("skrining_kesehatan_gigi_mulut_dewasa"),
                                       rs.getBoolean("skrining_risiko_kanker_serviks"),
                                       rs.getBoolean("catatan_cairan_hemodialisa"),
                                       rs.getBoolean("skrining_kesehatan_gigi_mulut_lansia"),
                                       rs.getBoolean("skrining_indra_pendengaran"),
                                       rs.getBoolean("catatan_pengkajian_paska_operasi"),
                                       rs.getBoolean("skrining_frailty_syndrome"),
                                       rs.getBoolean("sirkulasi_cssd"),
                                       rs.getBoolean("lama_pelayanan_cssd"),
                                       rs.getBoolean("catatan_observasi_bayi"),
                                       rs.getBoolean("riwayat_surat_peringatan"),
                                       rs.getBoolean("master_kesimpulan_anjuran_mcu"),
                                       rs.getBoolean("kategori_piutang_jasa_perusahaan"),
                                       rs.getBoolean("piutang_jasa_perusahaan"),
                                       rs.getBoolean("bayar_piutang_jasa_perusahaan"),
                                       rs.getBoolean("piutang_jasa_perusahaan_belum_lunas"),
                                       rs.getBoolean("checklist_kesiapan_anestesi"),
                                       rs.getBoolean("piutang_peminjaman_uang_belum_lunas"),
                                       rs.getBoolean("hasil_pemeriksaan_slit_lamp"),
                                       rs.getBoolean("hasil_pemeriksaan_oct"),
                                       rs.getBoolean("beban_hutang_lain"),
                                       rs.getBoolean("poli_asal_pasien_ranap"),
                                       rs.getBoolean("pemberi_hutang_lain"),
                                       rs.getBoolean("dokter_asal_pasien_ranap"),
                                       rs.getBoolean("duta_parkir_rekap_keluar"),
                                       rs.getBoolean("surat_keterangan_layak_terbang"),
                                       rs.getBoolean("bayar_beban_hutang_lain"),
                                       rs.getBoolean("surat_persetujuan_pemeriksaan_hiv"),
                                       rs.getBoolean("skrining_instrumen_acrs"),
                                       rs.getBoolean("surat_pernyataan_memilih_dpjp"),
                                       rs.getBoolean("skrining_instrumen_mental_emosional"),
                                       rs.getBoolean("pelanggan_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("kriteria_masuk_nicu"),
                                       rs.getBoolean("kriteria_keluar_nicu"),
                                       rs.getBoolean("penilaian_medis_ranap_psikiatrik"),
                                       rs.getBoolean("kriteria_masuk_picu"),
                                       rs.getBoolean("kriteria_keluar_picu"),
                                       rs.getBoolean("master_sampel_bakumutu"),
                                       rs.getBoolean("skrining_instrumen_amt"),
                                       rs.getBoolean("parameter_pengujian_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("nilai_normal_baku_mutu_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("skrining_pneumonia_severity_index"),
                                       rs.getBoolean("permintaan_pengujian_sampel_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_jantung"),
                                       rs.getBoolean("penilaian_awal_medis_ralan_urologi"),
                                       rs.getBoolean("hasil_pemeriksaan_treadmill"),
                                       rs.getBoolean("hasil_pemeriksaan_echo_pediatrik"),
                                       rs.getBoolean("template_pelaksanaan_informasi_edukasi"),
                                       rs.getBoolean("skrining_instrumen_esat"),
                                       rs.getBoolean("penilaian_awal_medis_ranap_jantung"),
                                       rs.getBoolean("penugasan_pengujian_sampel_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("hasil_pengujian_sampel_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("verifikasi_pengujian_sampel_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("validasi_pengujian_sampel_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("rekap_pelayanan_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("pembayaran_pengujian_sampel_lab_kesehatan_lingkungan"),
                                       rs.getBoolean("skrining_curb65"),
                                       rs.getBoolean("bpjs_potensi_prb"),
                                       rs.getBoolean("bpjs_riwayat_pelayanan_obat"),
                                       rs.getBoolean("skrining_gizi_kehamilan"),
                                       rs.getBoolean("bpjs_rekap_peserta_prb_apotek"),
                                       rs.getBoolean("serah_terima_anggota_tubuh_barang"),
                                       rs.getBoolean("pcra_icra_jenis_aktivitas_proyek"),
                                       rs.getBoolean("pcra_icra_lokasi_kelompok_risiko_area"),
                                       rs.getBoolean("pcra_icra_kelas_risiko_pencegahan"),
                                       rs.getBoolean("pcra_icra_tindakan_pengendalian"),
                                       rs.getBoolean("pcra_icra_identifkasi_risiko_infeksi"),
                                       rs.getBoolean("pcra_icra_identifkasi_risiko_keselamatan"),
                                       rs.getBoolean("pcra_icra_identifkasi_risiko_kebakaran"),
                                       rs.getBoolean("pcra_icra_identifkasi_risiko_utilitas"),
                                       rs.getBoolean("bpjs_daftar_resep_apotek"),
                                       rs.getBoolean("daftar_permintaan_resep_iterasi_bpjs"),
                                       rs.getBoolean("pcra_icra_pengkajian_risiko_prakonstruksi"),
                                       rs.getBoolean("pcra_icra_persyaratan_harus_dipenuhi"),
                                       rs.getBoolean("satu_sehat_kirim_questionresponse_telaah_farmasi"),
                                       rs.getBoolean("satu_sehat_kirim_allergy_intolerance"),
                                       rs.getBoolean("konsultasi_perawat"),
                                       rs.getBoolean("jawaban_konsultasi_perawat"),
                                       rs.getBoolean("bridging_smart_klaim_bpjs"),
                                       rs.getBoolean("mapping_prosedur_smart_klaim_bpjs"),
                                       rs.getBoolean("mapping_penyakit_smart_klaim_bpjs"),
                                       rs.getBoolean("permintaan_binrohtal"),
                                       rs.getBoolean("surat_permintaan_perlindungan_dari_kekerasan"),
                                       rs.getBoolean("surat_permohonan_privasi"),
                                       rs.getBoolean("surat_permintaan_second_opinion"),
                                       rs.getBoolean("surat_keterangan_berobat"),
                                       rs.getBoolean("surat_penolakan_resusitasi"),
                                       rs.getBoolean("catatan_observasi_ruang_ok"),
                                       rs.getBoolean("hasil_pemeriksaan_usg_abdomen"),
                                       rs.getBoolean("intervensi_nyeri_farmakologi"),
                                       rs.getBoolean("intervensi_nyeri_nonfarmakologi"),
                                       rs.getBoolean("surat_pengajuan_cuti_pasien"),
                                       rs.getBoolean("checklist_kriteria_masuk_isolasi"),
                                       rs.getBoolean("satu_sehat_mapping_kptl_tindakan_ralan"),
                                       rs.getBoolean("satu_sehat_mapping_kptl_tindakan_ranap"),
                                       rs.getBoolean("satu_sehat_mapping_kptl_tindakan_radiologi"),
                                       rs.getBoolean("satu_sehat_mapping_kptl_tindakan_laborat"),
                                       rs.getBoolean("satu_sehat_mapping_kptl_tindakan_operasi"),
                                       rs.getBoolean("satu_sehat_mapping_kptl_tarif_kamar"),
                                       rs.getBoolean("checklist_kriteria_keluar_isolasi"),
                                       rs.getBoolean("satu_sehat_tanda_tangan_elektronik"),
                                       rs.getBoolean("satu_sehat_kirim_composition"),
                                       rs.getBoolean("ringkasan_hutang_vendor_inventaris"),
                                       rs.getBoolean("ringkasan_beban_hutang_lain"),
                                       rs.getBoolean("set_resep_per_cara_bayar"),
                                       rs.getBoolean("skrining_tolac"),
                                       rs.getBoolean("admisi_skoring_tolac"),
                                       rs.getBoolean("monitoring_tolac"),
                                       rs.getBoolean("hasil_persalinan_tolac"),
                                       rs.getBoolean("laporan_reaksi_transfusi"),
                                       rs.getBoolean("penilaian_grace_scor"),
                                       rs.getBoolean("penilaian_bishop_scor")
                                    });
                                }
                             }
                        } catch (Exception e) {
                            System.out.println(e);
                        } finally{
                            if(rs!=null){
                                rs.close();
                            }
                            if(ps!=null){
                                ps.close();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void process(List<Object[]> data) {
                        for (Object[] row : data) {
                            tabMode.addRow(row);
                        }
                    }

                    @Override
                    protected void done() {
                        LCount.setText(""+i);
                        ceksukses = false;
                        tbUser.resumeSorting();
                    }
                }.execute();
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
    }

    public void emptTeks() {
        TKd.setText("");
        TNmUser.setText("");
        TPass.setText("");
        TKd.requestFocus();
    }

    private void getData() {
        i=tbUser.getSelectedRow();
        if(i!= -1){
            TKd.setText(tabMode.getValueAt(tbUser.convertRowIndexToModel(i),0).toString());
            TNmUser.setText(tabMode.getValueAt(tbUser.convertRowIndexToModel(i),1).toString());
            TPass.setText(tabMode.getValueAt(tbUser.convertRowIndexToModel(i),3).toString());
        }
    }
}