SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `khanza`.`datasuplier` MODIFY COLUMN `alamat` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `khanza`.`datasuplier` MODIFY COLUMN `kota` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `khanza`.`datasuplier` MODIFY COLUMN `no_telp` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `khanza`.`datasuplier` MODIFY COLUMN `nama_bank` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_telp`;

ALTER TABLE `khanza`.`dokter` MODIFY COLUMN `almt_tgl` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `agama`;

ALTER TABLE `khanza`.`emergency_index` MODIFY COLUMN `nama_emergency` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kode_emergency`;

ALTER TABLE `khanza`.`industrifarmasi` MODIFY COLUMN `alamat` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_industri`;

ALTER TABLE `khanza`.`industrifarmasi` MODIFY COLUMN `kota` varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `khanza`.`ipsrssuplier` MODIFY COLUMN `alamat` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `khanza`.`ipsrssuplier` MODIFY COLUMN `kota` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `khanza`.`ipsrssuplier` MODIFY COLUMN `no_telp` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `khanza`.`ipsrssuplier` MODIFY COLUMN `nama_bank` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_telp`;

ALTER TABLE `khanza`.`jns_perawatan_inap` MODIFY COLUMN `nm_perawatan` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kd_jenis_prw`;

ALTER TABLE `khanza`.`pasien` MODIFY COLUMN `nm_pasien` varchar(60) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `no_rkm_medis`;

ALTER TABLE `khanza`.`pasien` MODIFY COLUMN `tmp_lahir` varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jk`;

ALTER TABLE `khanza`.`pasien` MODIFY COLUMN `nm_ibu` varchar(60) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `tgl_lahir`;

CREATE DEFINER = `root` @`%` TRIGGER `set_password_user` AFTER INSERT ON `pasien` FOR EACH ROW
insert into personal_pasien
values(
        new.no_rkm_medis,
        '',
        aes_encrypt(date_format(new.tgl_lahir, '%d%m%Y'), 'windi')
    );
ALTER TABLE `khanza`.`pegawai` MODIFY COLUMN `alamat` varchar(150) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `tgl_lahir`;

ALTER TABLE `khanza`.`pengeluaran_harian` MODIFY COLUMN `keterangan` varchar(250) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' AFTER `nip`;

ALTER TABLE `khanza`.`resep_obat` ADD COLUMN `nama_template` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `jam_penyerahan`;

ALTER TABLE `khanza`.`resiko_kerja` MODIFY COLUMN `nama_resiko` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `kode_resiko`;

ALTER TABLE `khanza`.`surat_persetujuan_umum` MODIFY COLUMN `umur_pj` varchar(3) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `nama_pj`;

CREATE TABLE `khanza`.`tampjurnal_rvpbpjs` (
    `kd_rek` char(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    `nm_rek` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
    `debet` double NULL DEFAULT NULL,
    `kredit` double NULL DEFAULT NULL,
    PRIMARY KEY (`kd_rek`) USING HASH,
    INDEX `nm_rek`(`nm_rek`) USING HASH,
    INDEX `debet`(`debet`) USING HASH,
    INDEX `kredit`(`kredit`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

CREATE TABLE `khanza`.`tampjurnal_smc` (
    `kd_rek` char(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    `nm_rek` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
    `debet` double NULL DEFAULT NULL,
    `kredit` double NULL DEFAULT NULL,
    `user_id` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    `ip` varchar(25) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    PRIMARY KEY (`kd_rek`, `user_id`, `ip`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

ALTER TABLE `khanza`.`tokoopname` MODIFY COLUMN `kode_brng` varchar(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL FIRST;

ALTER TABLE `khanza`.`transfer_pasien_antar_ruang` MODIFY COLUMN `diagnosa_utama` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL AFTER `ruang_selanjutnya`;

SET FOREIGN_KEY_CHECKS = 1;