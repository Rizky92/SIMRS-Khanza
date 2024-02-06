SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `antriloketcetak_smc`  (
  `nomor` varchar(4) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `tanggal` date NOT NULL,
  `jam` time NULL DEFAULT NULL,
  `jam_panggil` time NULL DEFAULT NULL,
  `no_rawat` varchar(17) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`tanggal`, `nomor`) USING BTREE,
  INDEX `antriloketcetak_smc_jam_IDX`(`jam`) USING BTREE,
  INDEX `antriloketcetak_smc_tanggal_IDX`(`tanggal`) USING BTREE,
  INDEX `antriloketcetak_smc_no_rawat_IDX`(`no_rawat`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `datasuplier` MODIFY COLUMN `alamat` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `datasuplier` MODIFY COLUMN `kota` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `datasuplier` MODIFY COLUMN `no_telp` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `datasuplier` MODIFY COLUMN `nama_bank` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_telp`;

CREATE TABLE `detail_pemberian_obat_selanjutnya`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tgl_perawatan` date NULL DEFAULT NULL,
  `jam` time NULL DEFAULT NULL,
  `no_rawat` varchar(17) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `kode_brng` varchar(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `tgl_pemberian_selanjutnya` date NULL DEFAULT NULL,
  `total_hari` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_reg_periksa_FK`(`no_rawat`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_databarang_FK`(`kode_brng`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_tgl_perawatan_IDX`(`tgl_perawatan`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_jam_IDX`(`jam`) USING BTREE,
  CONSTRAINT `detail_pemberian_obat_selanjutnya_databarang_FK` FOREIGN KEY (`kode_brng`) REFERENCES `databarang` (`kode_brng`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `detail_pemberian_obat_selanjutnya_reg_periksa_FK` FOREIGN KEY (`no_rawat`) REFERENCES `reg_periksa` (`no_rawat`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `dokter` MODIFY COLUMN `almt_tgl` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `agama`;

ALTER TABLE `emergency_index` MODIFY COLUMN `nama_emergency` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kode_emergency`;

ALTER TABLE `industrifarmasi` MODIFY COLUMN `alamat` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_industri`;

ALTER TABLE `industrifarmasi` MODIFY COLUMN `kota` varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN `alamat` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN `kota` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN `no_telp` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN `nama_bank` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_telp`;

ALTER TABLE `jns_perawatan_inap` MODIFY COLUMN `nm_perawatan` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kd_jenis_prw`;

ALTER TABLE `pasien` MODIFY COLUMN `nm_pasien` varchar(60) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_rkm_medis`;

ALTER TABLE `pasien` MODIFY COLUMN `tmp_lahir` varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jk`;

ALTER TABLE `pasien` MODIFY COLUMN `nm_ibu` varchar(60) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `tgl_lahir`;

CREATE DEFINER = `root`@`%` TRIGGER `set_password_user` AFTER INSERT ON `pasien` FOR EACH ROW insert into personal_pasien values(
  new.no_rkm_medis,
  '',
  aes_encrypt(date_format(new.tgl_lahir, '%d%m%Y'), 'windi')
);

ALTER TABLE `pegawai` MODIFY COLUMN `alamat` varchar(150) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `tgl_lahir`;

ALTER TABLE `pengeluaran_harian` MODIFY COLUMN `keterangan` varchar(250) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' AFTER `nip`;

ALTER TABLE `penilaian_awal_keperawatan_ranap` MODIFY COLUMN `rpd` varchar(300) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `rps`;

CREATE TABLE `referensi_mobilejkn_bpjs_taskid_response`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `no_rawat` varchar(17) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `jenispasien` enum('JKN','NON JKN') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `taskid` char(2) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `code` varchar(5) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `message` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `waktu` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `referensi_mobilejkn_bpjs_taskid_response_no_rawat_IDX`(`no_rawat`) USING BTREE,
  INDEX `referensi_mobilejkn_bpjs_taskid_response_waktu_IDX`(`waktu`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `resep_obat` ADD COLUMN `nama_template` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jam_penyerahan`;

ALTER TABLE `resiko_kerja` MODIFY COLUMN `nama_resiko` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kode_resiko`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `diagnosa_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `hasil_laborat`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `diagnosa_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_utama`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `diagnosa_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `diagnosa_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder2`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `diagnosa_sekunder4` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder3`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `prosedur_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder4`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `prosedur_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_utama`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `prosedur_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder`;

ALTER TABLE `resume_pasien` MODIFY COLUMN `prosedur_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder2`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `diagnosa_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `obat_di_rs`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `diagnosa_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_utama`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `diagnosa_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `diagnosa_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder2`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `diagnosa_sekunder4` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder3`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `prosedur_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder4`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `prosedur_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_utama`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `prosedur_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN `prosedur_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder2`;

ALTER TABLE `skdp_bpjs` MODIFY COLUMN `terapi` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `diagnosa`;

CREATE TABLE `tampjurnal_rvpbpjs`  (
  `kd_rek` char(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `nm_rek` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `debet` double NULL DEFAULT NULL,
  `kredit` double NULL DEFAULT NULL,
  PRIMARY KEY (`kd_rek`) USING HASH,
  INDEX `nm_rek`(`nm_rek`) USING HASH,
  INDEX `debet`(`debet`) USING HASH,
  INDEX `kredit`(`kredit`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

CREATE TABLE `tampjurnal_smc`  (
  `kd_rek` char(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `nm_rek` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `debet` double NULL DEFAULT NULL,
  `kredit` double NULL DEFAULT NULL,
  `user_id` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `ip` varchar(25) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`kd_rek`, `user_id`, `ip`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

ALTER TABLE `tokoopname` MODIFY COLUMN `kode_brng` varchar(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL FIRST;

ALTER TABLE `transfer_pasien_antar_ruang` MODIFY COLUMN `diagnosa_utama` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ruang_selanjutnya`;

ALTER TABLE `user` ADD COLUMN `edit_hapus_spo_medis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penatalaksanaan_terapi_okupasi`;

ALTER TABLE `user` ADD COLUMN `edit_hapus_spo_nonmedis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `edit_hapus_spo_medis`;

ALTER TABLE `user` MODIFY COLUMN `password` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `id_user`;

ALTER TABLE `user` MODIFY COLUMN `penyakit` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `password`;

ALTER TABLE `user` MODIFY COLUMN `obat_penyakit` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penyakit`;

ALTER TABLE `user` MODIFY COLUMN `dokter` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `obat_penyakit`;

ALTER TABLE `user` MODIFY COLUMN `jadwal_praktek` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `dokter`;

ALTER TABLE `user` MODIFY COLUMN `petugas` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jadwal_praktek`;

ALTER TABLE `user` MODIFY COLUMN `pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `petugas`;

ALTER TABLE `user` MODIFY COLUMN `registrasi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pasien`;

ALTER TABLE `user` MODIFY COLUMN `tindakan_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `registrasi`;

ALTER TABLE `user` MODIFY COLUMN `kamar_inap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tindakan_ralan`;

ALTER TABLE `user` MODIFY COLUMN `tindakan_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kamar_inap`;

ALTER TABLE `user` MODIFY COLUMN `operasi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tindakan_ranap`;

ALTER TABLE `user` MODIFY COLUMN `rujukan_keluar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `operasi`;

ALTER TABLE `user` MODIFY COLUMN `rujukan_masuk` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rujukan_keluar`;

ALTER TABLE `user` MODIFY COLUMN `beri_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rujukan_masuk`;

ALTER TABLE `user` MODIFY COLUMN `resep_pulang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `beri_obat`;

ALTER TABLE `user` MODIFY COLUMN `pasien_meninggal` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `resep_pulang`;

ALTER TABLE `user` MODIFY COLUMN `diet_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pasien_meninggal`;

ALTER TABLE `user` MODIFY COLUMN `kelahiran_bayi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `diet_pasien`;

ALTER TABLE `user` MODIFY COLUMN `periksa_lab` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kelahiran_bayi`;

ALTER TABLE `user` MODIFY COLUMN `periksa_radiologi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `periksa_lab`;

ALTER TABLE `user` MODIFY COLUMN `kasir_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `periksa_radiologi`;

ALTER TABLE `user` MODIFY COLUMN `deposit_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kasir_ralan`;

ALTER TABLE `user` MODIFY COLUMN `piutang_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `deposit_pasien`;

ALTER TABLE `user` MODIFY COLUMN `peminjaman_berkas` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `piutang_pasien`;

ALTER TABLE `user` MODIFY COLUMN `barcode` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `peminjaman_berkas`;

ALTER TABLE `user` MODIFY COLUMN `presensi_harian` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `barcode`;

ALTER TABLE `user` MODIFY COLUMN `presensi_bulanan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `presensi_harian`;

ALTER TABLE `user` MODIFY COLUMN `pegawai_admin` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `presensi_bulanan`;

ALTER TABLE `user` MODIFY COLUMN `pegawai_user` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pegawai_admin`;

ALTER TABLE `user` MODIFY COLUMN `suplier` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pegawai_user`;

ALTER TABLE `user` MODIFY COLUMN `satuan_barang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `suplier`;

ALTER TABLE `user` MODIFY COLUMN `konversi_satuan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `satuan_barang`;

ALTER TABLE `user` MODIFY COLUMN `jenis_barang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `konversi_satuan`;

ALTER TABLE `user` MODIFY COLUMN `obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jenis_barang`;

ALTER TABLE `user` MODIFY COLUMN `stok_opname_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `obat`;

ALTER TABLE `user` MODIFY COLUMN `stok_obat_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `stok_opname_obat`;

ALTER TABLE `user` MODIFY COLUMN `pengadaan_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `stok_obat_pasien`;

ALTER TABLE `user` MODIFY COLUMN `pemesanan_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pengadaan_obat`;

ALTER TABLE `user` MODIFY COLUMN `penjualan_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pemesanan_obat`;

ALTER TABLE `user` MODIFY COLUMN `piutang_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penjualan_obat`;

ALTER TABLE `user` MODIFY COLUMN `retur_ke_suplier` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `piutang_obat`;

ALTER TABLE `user` MODIFY COLUMN `retur_dari_pembeli` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `retur_ke_suplier`;

ALTER TABLE `user` MODIFY COLUMN `retur_obat_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `retur_dari_pembeli`;

ALTER TABLE `user` MODIFY COLUMN `retur_piutang_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `retur_obat_ranap`;

ALTER TABLE `user` MODIFY COLUMN `keuntungan_penjualan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `retur_piutang_pasien`;

ALTER TABLE `user` MODIFY COLUMN `keuntungan_beri_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `keuntungan_penjualan`;

ALTER TABLE `user` MODIFY COLUMN `sirkulasi_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `keuntungan_beri_obat`;

ALTER TABLE `user` MODIFY COLUMN `ipsrs_barang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `sirkulasi_obat`;

ALTER TABLE `user` MODIFY COLUMN `ipsrs_pengadaan_barang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ipsrs_barang`;

ALTER TABLE `user` MODIFY COLUMN `ipsrs_stok_keluar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ipsrs_pengadaan_barang`;

ALTER TABLE `user` MODIFY COLUMN `ipsrs_rekap_pengadaan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ipsrs_stok_keluar`;

ALTER TABLE `user` MODIFY COLUMN `ipsrs_rekap_stok_keluar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ipsrs_rekap_pengadaan`;

ALTER TABLE `user` MODIFY COLUMN `ipsrs_pengeluaran_harian` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ipsrs_rekap_stok_keluar`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_jenis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ipsrs_pengeluaran_harian`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_kategori` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_jenis`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_merk` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_kategori`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_ruang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_merk`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_produsen` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_ruang`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_koleksi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_produsen`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_inventaris` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_koleksi`;

ALTER TABLE `user` MODIFY COLUMN `inventaris_sirkulasi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_inventaris`;

ALTER TABLE `user` MODIFY COLUMN `parkir_jenis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `inventaris_sirkulasi`;

ALTER TABLE `user` MODIFY COLUMN `parkir_in` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `parkir_jenis`;

ALTER TABLE `user` MODIFY COLUMN `parkir_out` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `parkir_in`;

ALTER TABLE `user` MODIFY COLUMN `parkir_rekap_harian` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `parkir_out`;

ALTER TABLE `user` MODIFY COLUMN `parkir_rekap_bulanan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `parkir_rekap_harian`;

ALTER TABLE `user` MODIFY COLUMN `informasi_kamar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `parkir_rekap_bulanan`;

ALTER TABLE `user` MODIFY COLUMN `harian_tindakan_poli` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `informasi_kamar`;

ALTER TABLE `user` MODIFY COLUMN `obat_per_poli` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `harian_tindakan_poli`;

ALTER TABLE `user` MODIFY COLUMN `obat_per_kamar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `obat_per_poli`;

ALTER TABLE `user` MODIFY COLUMN `obat_per_dokter_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `obat_per_kamar`;

ALTER TABLE `user` MODIFY COLUMN `obat_per_dokter_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `obat_per_dokter_ralan`;

ALTER TABLE `user` MODIFY COLUMN `harian_dokter` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `obat_per_dokter_ranap`;

ALTER TABLE `user` MODIFY COLUMN `bulanan_dokter` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `harian_dokter`;

ALTER TABLE `user` MODIFY COLUMN `harian_paramedis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `bulanan_dokter`;

ALTER TABLE `user` MODIFY COLUMN `bulanan_paramedis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `harian_paramedis`;

ALTER TABLE `user` MODIFY COLUMN `pembayaran_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `bulanan_paramedis`;

ALTER TABLE `user` MODIFY COLUMN `pembayaran_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pembayaran_ralan`;

ALTER TABLE `user` MODIFY COLUMN `rekap_pembayaran_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pembayaran_ranap`;

ALTER TABLE `user` MODIFY COLUMN `rekap_pembayaran_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rekap_pembayaran_ralan`;

ALTER TABLE `user` MODIFY COLUMN `tagihan_masuk` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rekap_pembayaran_ranap`;

ALTER TABLE `user` MODIFY COLUMN `tambahan_biaya` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tagihan_masuk`;

ALTER TABLE `user` MODIFY COLUMN `potongan_biaya` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tambahan_biaya`;

ALTER TABLE `user` MODIFY COLUMN `resep_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `potongan_biaya`;

ALTER TABLE `user` MODIFY COLUMN `resume_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `resep_obat`;

ALTER TABLE `user` MODIFY COLUMN `penyakit_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `resume_pasien`;

ALTER TABLE `user` MODIFY COLUMN `penyakit_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penyakit_ralan`;

ALTER TABLE `user` MODIFY COLUMN `kamar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penyakit_ranap`;

ALTER TABLE `user` MODIFY COLUMN `tarif_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kamar`;

ALTER TABLE `user` MODIFY COLUMN `tarif_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tarif_ralan`;

ALTER TABLE `user` MODIFY COLUMN `tarif_lab` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tarif_ranap`;

ALTER TABLE `user` MODIFY COLUMN `tarif_radiologi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tarif_lab`;

ALTER TABLE `user` MODIFY COLUMN `tarif_operasi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tarif_radiologi`;

ALTER TABLE `user` MODIFY COLUMN `akun_rekening` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tarif_operasi`;

ALTER TABLE `user` MODIFY COLUMN `rekening_tahun` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `akun_rekening`;

ALTER TABLE `user` MODIFY COLUMN `posting_jurnal` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rekening_tahun`;

ALTER TABLE `user` MODIFY COLUMN `buku_besar` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `posting_jurnal`;

ALTER TABLE `user` MODIFY COLUMN `cashflow` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `buku_besar`;

ALTER TABLE `user` MODIFY COLUMN `keuangan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `cashflow`;

ALTER TABLE `user` MODIFY COLUMN `pengeluaran` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `keuangan`;

ALTER TABLE `user` MODIFY COLUMN `setup_pjlab` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pengeluaran`;

ALTER TABLE `user` MODIFY COLUMN `setup_otolokasi` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `setup_pjlab`;

ALTER TABLE `user` MODIFY COLUMN `setup_jam_kamin` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `setup_otolokasi`;

ALTER TABLE `user` MODIFY COLUMN `setup_embalase` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `setup_jam_kamin`;

ALTER TABLE `user` MODIFY COLUMN `tracer_login` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `setup_embalase`;

ALTER TABLE `user` MODIFY COLUMN `display` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `tracer_login`;

ALTER TABLE `user` MODIFY COLUMN `set_harga_obat` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `display`;

ALTER TABLE `user` MODIFY COLUMN `set_penggunaan_tarif` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_harga_obat`;

ALTER TABLE `user` MODIFY COLUMN `set_oto_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_penggunaan_tarif`;

ALTER TABLE `user` MODIFY COLUMN `biaya_harian` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_oto_ralan`;

ALTER TABLE `user` MODIFY COLUMN `biaya_masuk_sekali` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `biaya_harian`;

ALTER TABLE `user` MODIFY COLUMN `set_no_rm` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `biaya_masuk_sekali`;

ALTER TABLE `user` MODIFY COLUMN `billing_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_no_rm`;

ALTER TABLE `user` MODIFY COLUMN `billing_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `billing_ralan`;

ALTER TABLE `user` MODIFY COLUMN `jm_ranap_dokter` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `billing_ranap`;

ALTER TABLE `user` MODIFY COLUMN `igd` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jm_ranap_dokter`;

ALTER TABLE `user` MODIFY COLUMN `barcoderalan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `igd`;

ALTER TABLE `user` MODIFY COLUMN `barcoderanap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `barcoderalan`;

ALTER TABLE `user` MODIFY COLUMN `set_harga_obat_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `barcoderanap`;

ALTER TABLE `user` MODIFY COLUMN `set_harga_obat_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_harga_obat_ralan`;

ALTER TABLE `user` MODIFY COLUMN `penyakit_pd3i` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_harga_obat_ranap`;

ALTER TABLE `user` MODIFY COLUMN `surveilans_pd3i` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penyakit_pd3i`;

ALTER TABLE `user` MODIFY COLUMN `surveilans_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `surveilans_pd3i`;

ALTER TABLE `user` MODIFY COLUMN `diagnosa_pasien` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `surveilans_ralan`;

ALTER TABLE `user` MODIFY COLUMN `surveilans_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `diagnosa_pasien`;

ALTER TABLE `user` MODIFY COLUMN `pny_takmenular_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `surveilans_ranap`;

ALTER TABLE `user` MODIFY COLUMN `pny_takmenular_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pny_takmenular_ranap`;

ALTER TABLE `user` MODIFY COLUMN `kunjungan_ralan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pny_takmenular_ralan`;

ALTER TABLE `user` MODIFY COLUMN `rl32` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kunjungan_ralan`;

ALTER TABLE `user` MODIFY COLUMN `rl33` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rl32`;

ALTER TABLE `user` MODIFY COLUMN `rl37` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rl33`;

ALTER TABLE `user` MODIFY COLUMN `rl38` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rl37`;

ALTER TABLE `user` MODIFY COLUMN `harian_tindakan_dokter` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rl38`;

ALTER TABLE `user` MODIFY COLUMN `sms` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `harian_tindakan_dokter`;

ALTER TABLE `user` MODIFY COLUMN `sidikjari` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `sms`;

ALTER TABLE `user` MODIFY COLUMN `jam_masuk` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `sidikjari`;

ALTER TABLE `user` MODIFY COLUMN `jadwal_pegawai` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jam_masuk`;

ALTER TABLE `user` MODIFY COLUMN `parkir_barcode` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jadwal_pegawai`;

ALTER TABLE `user` MODIFY COLUMN `set_nota` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `parkir_barcode`;

ALTER TABLE `user` MODIFY COLUMN `dpjp_ranap` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `set_nota`;

ALTER TABLE `user` MODIFY COLUMN `mutasi_barang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `dpjp_ranap`;

ALTER TABLE `user` MODIFY COLUMN `rl36` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `rl34`;

ALTER TABLE `user` MODIFY COLUMN `grafik_per_perujuk` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `surat_pemesanan_non_medis`;

ALTER TABLE `user` MODIFY COLUMN `surat_balas` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `surat_sifat`;

ALTER TABLE `user` MODIFY COLUMN `pcare_pemberian_tindakan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `pcare_pemberian_obat`;

ALTER TABLE `user` MODIFY COLUMN `kemenkes_sitt` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `password_asuransi`;

ALTER TABLE `user` MODIFY COLUMN `grafik_tb_hasilteshiv` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `grafik_tb_hasilakhirpengobatan`;

ALTER TABLE `user` MODIFY COLUMN `kadaluarsa_batch` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `grafik_tb_hasilteshiv`;

ALTER TABLE `user` MODIFY COLUMN `peminjam_piutang` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penilaian_mcu`;

ALTER TABLE `user` MODIFY COLUMN `satu_sehat_kirim_clinicalimpression` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `konfirmasi_rekonsiliasi_obat`;

ALTER TABLE `user` MODIFY COLUMN `template_persetujuan_penolakan_tindakan` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `laporan_anestesi`;

SET FOREIGN_KEY_CHECKS=1;