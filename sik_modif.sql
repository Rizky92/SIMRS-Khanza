SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE IF NOT EXISTS `antriloketcetak_smc`  (
  `nomor` int(10) UNSIGNED CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `tanggal` date NOT NULL,
  `jam` time NULL DEFAULT NULL,
  `jam_panggil` time NULL DEFAULT NULL,
  `no_rawat` varchar(17) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `no_rkm_medis` varchar(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`tanggal`, `nomor`) USING BTREE,
  INDEX `antriloketcetak_smc_jam_IDX`(`jam`) USING BTREE,
  INDEX `antriloketcetak_smc_tanggal_IDX`(`tanggal`) USING BTREE,
  INDEX `antriloketcetak_smc_no_rawat_IDX`(`no_rawat`) USING BTREE,
  INDEX `antriloketcetak_smc_no_rkm_medis_IDX`(`no_rkm_medis`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `alamat` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `kota` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `no_telp` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `nama_bank` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_telp`;

CREATE TABLE IF NOT EXISTS `detail_pemberian_obat_selanjutnya`  (
  `tgl_perawatan` date NOT NULL,
  `jam` time NOT NULL,
  `no_rkm_medis` varchar(17) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `kode_brng` varchar(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `tgl_pemberian_selanjutnya` date NULL DEFAULT NULL,
  `total_hari` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`tgl_perawatan`, `jam`, `no_rkm_medis`, `kode_brng`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_databarang_FK`(`kode_brng`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_pasien_FK`(`no_rkm_medis`) USING BTREE,
  CONSTRAINT `detail_pemberian_obat_selanjutnya_databarang_FK` FOREIGN KEY (`kode_brng`) REFERENCES `databarang` (`kode_brng`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `detail_pemberian_obat_selanjutnya_pasien_FK` FOREIGN KEY (`no_rkm_medis`) REFERENCES `pasien` (`no_rkm_medis`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `dokter` MODIFY COLUMN IF EXISTS `almt_tgl` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `agama`;

ALTER TABLE `emergency_index` MODIFY COLUMN IF EXISTS `nama_emergency` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kode_emergency`;

ALTER TABLE `industrifarmasi` MODIFY COLUMN IF EXISTS `alamat` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_industri`;

ALTER TABLE `industrifarmasi` MODIFY COLUMN IF EXISTS `kota` varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `alamat` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `kota` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `no_telp` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `nama_bank` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_telp`;

ALTER TABLE `jns_perawatan_inap` MODIFY COLUMN IF EXISTS `nm_perawatan` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kd_jenis_prw`;

ALTER TABLE `pasien` MODIFY COLUMN IF EXISTS `nm_pasien` varchar(60) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_rkm_medis`;

ALTER TABLE `pasien` MODIFY COLUMN IF EXISTS `tmp_lahir` varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jk`;

ALTER TABLE `pasien` MODIFY COLUMN IF EXISTS `nm_ibu` varchar(60) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `tgl_lahir`;

CREATE DEFINER = `root`@`%` TRIGGER IF NOT EXISTS `set_password_user` AFTER INSERT ON `pasien` FOR EACH ROW insert into personal_pasien values(
  new.no_rkm_medis,
  '',
  aes_encrypt(date_format(new.tgl_lahir, '%d%m%Y'), 'windi')
);

ALTER TABLE `pegawai` MODIFY COLUMN IF EXISTS `alamat` varchar(150) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `tgl_lahir`;

ALTER TABLE `pengeluaran_harian` MODIFY COLUMN IF EXISTS `keterangan` varchar(250) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' AFTER `nip`;

ALTER TABLE `penilaian_awal_keperawatan_ranap` MODIFY COLUMN IF EXISTS `rpd` varchar(300) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `rps`;

CREATE TABLE IF NOT EXISTS `referensi_mobilejkn_bpjs_taskid_response`  (
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

ALTER TABLE `resep_obat` ADD COLUMN IF NOT EXISTS `nama_template` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jam_penyerahan`;

ALTER TABLE `resiko_kerja` MODIFY COLUMN IF EXISTS `nama_resiko` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kode_resiko`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `hasil_laborat`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_utama`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder2`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder4` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder3`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder4`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_utama`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder2`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `obat_di_rs`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_utama`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder2`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder4` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder3`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_utama` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_diagnosa_sekunder4`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_sekunder` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_utama`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_sekunder2` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_sekunder3` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `kd_prosedur_sekunder2`;

ALTER TABLE `skdp_bpjs` MODIFY COLUMN IF EXISTS `terapi` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `diagnosa`;

CREATE TABLE IF NOT EXISTS `tampjurnal_rvpbpjs`  (
  `kd_rek` char(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `nm_rek` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `debet` double NULL DEFAULT NULL,
  `kredit` double NULL DEFAULT NULL,
  PRIMARY KEY (`kd_rek`) USING HASH,
  INDEX `nm_rek`(`nm_rek`) USING HASH,
  INDEX `debet`(`debet`) USING HASH,
  INDEX `kredit`(`kredit`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

CREATE TABLE IF NOT EXISTS `tampjurnal_smc`  (
  `kd_rek` char(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `nm_rek` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `debet` double NULL DEFAULT NULL,
  `kredit` double NULL DEFAULT NULL,
  `user_id` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `ip` varchar(25) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`kd_rek`, `user_id`, `ip`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

ALTER TABLE `tokoopname` MODIFY COLUMN IF EXISTS `kode_brng` varchar(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL FIRST;

ALTER TABLE `transfer_pasien_antar_ruang` MODIFY COLUMN IF EXISTS `diagnosa_utama` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `ruang_selanjutnya`;

ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `edit_hapus_spo_medis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `penatalaksanaan_terapi_okupasi`;

ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `edit_hapus_spo_nonmedis` enum('true','false') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `edit_hapus_spo_medis`;

SET FOREIGN_KEY_CHECKS=1;