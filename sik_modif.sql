SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE IF NOT EXISTS `antriloketcetak_smc`  (
  `nomor` int(10) UNSIGNED NOT NULL,
  `tanggal` date NOT NULL,
  `jam` time NULL DEFAULT NULL,
  `jam_panggil` time NULL DEFAULT NULL,
  `no_rawat` varchar(17) NULL DEFAULT NULL,
  `no_rkm_medis` varchar(15) NULL DEFAULT NULL,
  PRIMARY KEY (`tanggal`, `nomor`) USING BTREE,
  INDEX `antriloketcetak_smc_jam_IDX`(`jam`) USING BTREE,
  INDEX `antriloketcetak_smc_tanggal_IDX`(`tanggal`) USING BTREE,
  INDEX `antriloketcetak_smc_no_rawat_IDX`(`no_rawat`) USING BTREE,
  INDEX `antriloketcetak_smc_no_rkm_medis_IDX`(`no_rkm_medis`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `infus` double NOT NULL DEFAULT 0 AFTER `jam_rawat`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `tranfusi` double NOT NULL DEFAULT 0 AFTER `infus`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `minum` double NOT NULL DEFAULT 0 AFTER `tranfusi`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `urine` double NOT NULL DEFAULT 0 AFTER `minum`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `drain` double NOT NULL DEFAULT 0 AFTER `urine`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `ngt` double NOT NULL DEFAULT 0 AFTER `drain`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `iwl` double NOT NULL DEFAULT 0 AFTER `ngt`;

ALTER TABLE `catatan_keseimbangan_cairan` MODIFY COLUMN IF EXISTS `keseimbangan` double NOT NULL DEFAULT 0 AFTER `iwl`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `alamat` varchar(100) NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `kota` varchar(50) NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `no_telp` varchar(20) NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `datasuplier` MODIFY COLUMN IF EXISTS `nama_bank` varchar(50) NULL DEFAULT NULL AFTER `no_telp`;

CREATE TABLE IF NOT EXISTS `detail_pemberian_obat_selanjutnya`  (
  `tgl_perawatan` date NOT NULL,
  `jam` time NOT NULL,
  `no_rkm_medis` varchar(17) NOT NULL,
  `kode_brng` varchar(15) NOT NULL,
  `tgl_pemberian_selanjutnya` date NULL DEFAULT NULL,
  `total_hari` varchar(100) NULL DEFAULT NULL,
  PRIMARY KEY (`tgl_perawatan`, `jam`, `no_rkm_medis`, `kode_brng`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_databarang_FK`(`kode_brng`) USING BTREE,
  INDEX `detail_pemberian_obat_selanjutnya_pasien_FK`(`no_rkm_medis`) USING BTREE,
  CONSTRAINT `detail_pemberian_obat_selanjutnya_databarang_FK` FOREIGN KEY (`kode_brng`) REFERENCES `databarang` (`kode_brng`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `detail_pemberian_obat_selanjutnya_pasien_FK` FOREIGN KEY (`no_rkm_medis`) REFERENCES `pasien` (`no_rkm_medis`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `detail_penagihan_piutang` ADD COLUMN IF NOT EXISTS `diskon` double NULL DEFAULT NULL AFTER `sisapiutang`;

ALTER TABLE `dokter` MODIFY COLUMN IF EXISTS `almt_tgl` varchar(100) NULL DEFAULT NULL AFTER `agama`;

CREATE TABLE IF NOT EXISTS `eklaim_icd10`  (
  `code` varchar(7) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` tinyint(3) UNSIGNED NULL DEFAULT 1,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `emergency_index` MODIFY COLUMN IF EXISTS `nama_emergency` varchar(200) NULL DEFAULT NULL AFTER `kode_emergency`;

CREATE TABLE IF NOT EXISTS `inacbg_cetak_klaim`  (
  `no_sep` varchar(40) NOT NULL,
  `path` varchar(100) NULL DEFAULT NULL,
  PRIMARY KEY (`no_sep`) USING BTREE,
  CONSTRAINT `inacbg_cetak_klaim_bridging_sep_FK` FOREIGN KEY (`no_sep`) REFERENCES `bridging_sep` (`no_sep`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `industrifarmasi` MODIFY COLUMN IF EXISTS `alamat` varchar(200) NULL DEFAULT NULL AFTER `nama_industri`;

ALTER TABLE `industrifarmasi` MODIFY COLUMN IF EXISTS `kota` varchar(30) NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `alamat` varchar(100) NULL DEFAULT NULL AFTER `nama_suplier`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `kota` varchar(50) NULL DEFAULT NULL AFTER `alamat`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `no_telp` varchar(20) NULL DEFAULT NULL AFTER `kota`;

ALTER TABLE `ipsrssuplier` MODIFY COLUMN IF EXISTS `nama_bank` varchar(50) NULL DEFAULT NULL AFTER `no_telp`;

ALTER TABLE `jns_perawatan_inap` MODIFY COLUMN IF EXISTS `nm_perawatan` varchar(200) NULL DEFAULT NULL AFTER `kd_jenis_prw`;

ALTER TABLE `pasien` MODIFY COLUMN IF EXISTS `nm_pasien` varchar(60) NULL DEFAULT NULL AFTER `no_rkm_medis`;

ALTER TABLE `pasien` MODIFY COLUMN IF EXISTS `tmp_lahir` varchar(30) NULL DEFAULT NULL AFTER `jk`;

ALTER TABLE `pasien` MODIFY COLUMN IF EXISTS `nm_ibu` varchar(60) NOT NULL AFTER `tgl_lahir`;

CREATE TRIGGER IF NOT EXISTS `set_password_user`
  AFTER INSERT ON `pasien`
  FOR EACH ROW
  INSERT INTO `personal_pasien` VALUES (
    NEW.no_rkm_medis,
    '',
    AES_ENCRYPT(DATE_FORMAT(NEW.tgl_lahir, '%d%m%Y'), 'windi')
  );

ALTER TABLE `pegawai` MODIFY COLUMN IF EXISTS `alamat` varchar(150) NOT NULL AFTER `tgl_lahir`;

ALTER TABLE `pengeluaran_harian` MODIFY COLUMN IF EXISTS `keterangan` varchar(250) NOT NULL DEFAULT '' AFTER `nip`;

ALTER TABLE `penilaian_awal_keperawatan_ranap` MODIFY COLUMN IF EXISTS `rpd` varchar(300) NOT NULL AFTER `rps`;

CREATE TABLE IF NOT EXISTS `referensi_mobilejkn_bpjs_taskid_response`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `no_rawat` varchar(17) NULL DEFAULT NULL,
  `jenispasien` enum('JKN','NON JKN') NULL DEFAULT NULL,
  `taskid` char(2) NULL DEFAULT NULL,
  `code` varchar(5) NULL DEFAULT NULL,
  `message` varchar(200) NULL DEFAULT NULL,
  `waktu` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `referensi_mobilejkn_bpjs_taskid_response_no_rawat_IDX`(`no_rawat`) USING BTREE,
  INDEX `referensi_mobilejkn_bpjs_taskid_response_waktu_IDX`(`waktu`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `resep_obat` ADD COLUMN IF NOT EXISTS `nama_template` varchar(100) NULL DEFAULT NULL AFTER `jam_penyerahan`;

ALTER TABLE `resiko_kerja` MODIFY COLUMN IF EXISTS `nama_resiko` varchar(200) NULL DEFAULT NULL AFTER `kode_resiko`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_utama` varchar(200) NOT NULL AFTER `hasil_laborat`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder` varchar(200) NOT NULL AFTER `kd_diagnosa_utama`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder2` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder3` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder2`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `diagnosa_sekunder4` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder3`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_utama` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder4`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_sekunder` varchar(200) NOT NULL AFTER `kd_prosedur_utama`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_sekunder2` varchar(200) NOT NULL AFTER `kd_prosedur_sekunder`;

ALTER TABLE `resume_pasien` MODIFY COLUMN IF EXISTS `prosedur_sekunder3` varchar(200) NOT NULL AFTER `kd_prosedur_sekunder2`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_utama` varchar(200) NOT NULL AFTER `obat_di_rs`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder` varchar(200) NOT NULL AFTER `kd_diagnosa_utama`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder2` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder3` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder2`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `diagnosa_sekunder4` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder3`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_utama` varchar(200) NOT NULL AFTER `kd_diagnosa_sekunder4`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_sekunder` varchar(200) NOT NULL AFTER `kd_prosedur_utama`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_sekunder2` varchar(200) NOT NULL AFTER `kd_prosedur_sekunder`;

ALTER TABLE `resume_pasien_ranap` MODIFY COLUMN IF EXISTS `prosedur_sekunder3` varchar(200) NOT NULL AFTER `kd_prosedur_sekunder2`;

ALTER TABLE `satu_sehat_mapping_obat` MODIFY COLUMN IF EXISTS `obat_display` varchar(150) NULL DEFAULT NULL AFTER `obat_system`;

ALTER TABLE `setting` ADD COLUMN IF NOT EXISTS `pemberlakuan_2x24_jam` enum('Yes','No') NULL DEFAULT NULL AFTER `logo`;

ALTER TABLE `skdp_bpjs` MODIFY COLUMN IF EXISTS `terapi` varchar(50) NOT NULL AFTER `diagnosa`;

CREATE TABLE IF NOT EXISTS `tampjurnal_rvpbpjs`  (
  `kd_rek` char(15) NOT NULL,
  `nm_rek` varchar(100) NULL DEFAULT NULL,
  `debet` double NOT NULL,
  `kredit` double NOT NULL,
  PRIMARY KEY (`kd_rek`) USING HASH,
  INDEX `nm_rek`(`nm_rek`) USING HASH,
  INDEX `debet`(`debet`) USING HASH,
  INDEX `kredit`(`kredit`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

CREATE TABLE IF NOT EXISTS `tampjurnal_smc`  (
  `kd_rek` char(15) NOT NULL,
  `nm_rek` varchar(100) NULL DEFAULT NULL,
  `debet` double NOT NULL,
  `kredit` double NOT NULL,
  `user_id` varchar(20) NOT NULL,
  `ip` varchar(25) NOT NULL,
  PRIMARY KEY (`kd_rek`, `user_id`, `ip`) USING HASH
) ENGINE = MEMORY CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Fixed;

CREATE TABLE IF NOT EXISTS `tempinacbg`  (
  `coder_nik` varchar(20) NOT NULL,
  `cmg_code` varchar(10) NOT NULL,
  `cmg_description` varchar(100) NULL DEFAULT NULL,
  `cmg_type` varchar(50) NULL DEFAULT NULL,
  PRIMARY KEY (`coder_nik`, `cmg_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

ALTER TABLE `transfer_pasien_antar_ruang` MODIFY COLUMN IF EXISTS `diagnosa_utama` varchar(100) NULL DEFAULT NULL AFTER `ruang_selanjutnya`;

ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `edit_hapus_spo_medis` enum('true','false') NULL DEFAULT NULL AFTER `penatalaksanaan_terapi_okupasi`;

ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `edit_hapus_spo_nonmedis` enum('true','false') NULL DEFAULT NULL AFTER `edit_hapus_spo_medis`;

ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `bpjs_kompilasi_berkas_klaim` enum('true','false') NULL DEFAULT NULL AFTER `satu_sehat_kirim_specimen_radiologi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `penyakit` enum('true','false') NULL DEFAULT NULL AFTER `password`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `obat_penyakit` enum('true','false') NULL DEFAULT NULL AFTER `penyakit`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `dokter` enum('true','false') NULL DEFAULT NULL AFTER `obat_penyakit`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `jadwal_praktek` enum('true','false') NULL DEFAULT NULL AFTER `dokter`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `petugas` enum('true','false') NULL DEFAULT NULL AFTER `jadwal_praktek`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pasien` enum('true','false') NULL DEFAULT NULL AFTER `petugas`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `registrasi` enum('true','false') NULL DEFAULT NULL AFTER `pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tindakan_ralan` enum('true','false') NULL DEFAULT NULL AFTER `registrasi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kamar_inap` enum('true','false') NULL DEFAULT NULL AFTER `tindakan_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tindakan_ranap` enum('true','false') NULL DEFAULT NULL AFTER `kamar_inap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `operasi` enum('true','false') NULL DEFAULT NULL AFTER `tindakan_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rujukan_keluar` enum('true','false') NULL DEFAULT NULL AFTER `operasi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rujukan_masuk` enum('true','false') NULL DEFAULT NULL AFTER `rujukan_keluar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `beri_obat` enum('true','false') NULL DEFAULT NULL AFTER `rujukan_masuk`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `resep_pulang` enum('true','false') NULL DEFAULT NULL AFTER `beri_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pasien_meninggal` enum('true','false') NULL DEFAULT NULL AFTER `resep_pulang`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `diet_pasien` enum('true','false') NULL DEFAULT NULL AFTER `pasien_meninggal`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kelahiran_bayi` enum('true','false') NULL DEFAULT NULL AFTER `diet_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `periksa_lab` enum('true','false') NULL DEFAULT NULL AFTER `kelahiran_bayi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `periksa_radiologi` enum('true','false') NULL DEFAULT NULL AFTER `periksa_lab`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kasir_ralan` enum('true','false') NULL DEFAULT NULL AFTER `periksa_radiologi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `deposit_pasien` enum('true','false') NULL DEFAULT NULL AFTER `kasir_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `piutang_pasien` enum('true','false') NULL DEFAULT NULL AFTER `deposit_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `peminjaman_berkas` enum('true','false') NULL DEFAULT NULL AFTER `piutang_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `barcode` enum('true','false') NULL DEFAULT NULL AFTER `peminjaman_berkas`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `presensi_harian` enum('true','false') NULL DEFAULT NULL AFTER `barcode`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `presensi_bulanan` enum('true','false') NULL DEFAULT NULL AFTER `presensi_harian`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pegawai_admin` enum('true','false') NULL DEFAULT NULL AFTER `presensi_bulanan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pegawai_user` enum('true','false') NULL DEFAULT NULL AFTER `pegawai_admin`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `suplier` enum('true','false') NULL DEFAULT NULL AFTER `pegawai_user`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `satuan_barang` enum('true','false') NULL DEFAULT NULL AFTER `suplier`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `konversi_satuan` enum('true','false') NULL DEFAULT NULL AFTER `satuan_barang`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `jenis_barang` enum('true','false') NULL DEFAULT NULL AFTER `konversi_satuan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `obat` enum('true','false') NULL DEFAULT NULL AFTER `jenis_barang`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `stok_opname_obat` enum('true','false') NULL DEFAULT NULL AFTER `obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `stok_obat_pasien` enum('true','false') NULL DEFAULT NULL AFTER `stok_opname_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pengadaan_obat` enum('true','false') NULL DEFAULT NULL AFTER `stok_obat_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pemesanan_obat` enum('true','false') NULL DEFAULT NULL AFTER `pengadaan_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `penjualan_obat` enum('true','false') NULL DEFAULT NULL AFTER `pemesanan_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `piutang_obat` enum('true','false') NULL DEFAULT NULL AFTER `penjualan_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `retur_ke_suplier` enum('true','false') NULL DEFAULT NULL AFTER `piutang_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `retur_dari_pembeli` enum('true','false') NULL DEFAULT NULL AFTER `retur_ke_suplier`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `retur_obat_ranap` enum('true','false') NULL DEFAULT NULL AFTER `retur_dari_pembeli`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `retur_piutang_pasien` enum('true','false') NULL DEFAULT NULL AFTER `retur_obat_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `keuntungan_penjualan` enum('true','false') NULL DEFAULT NULL AFTER `retur_piutang_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `keuntungan_beri_obat` enum('true','false') NULL DEFAULT NULL AFTER `keuntungan_penjualan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `sirkulasi_obat` enum('true','false') NULL DEFAULT NULL AFTER `keuntungan_beri_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `ipsrs_barang` enum('true','false') NULL DEFAULT NULL AFTER `sirkulasi_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `ipsrs_pengadaan_barang` enum('true','false') NULL DEFAULT NULL AFTER `ipsrs_barang`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `ipsrs_stok_keluar` enum('true','false') NULL DEFAULT NULL AFTER `ipsrs_pengadaan_barang`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `ipsrs_rekap_pengadaan` enum('true','false') NULL DEFAULT NULL AFTER `ipsrs_stok_keluar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `ipsrs_rekap_stok_keluar` enum('true','false') NULL DEFAULT NULL AFTER `ipsrs_rekap_pengadaan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `ipsrs_pengeluaran_harian` enum('true','false') NULL DEFAULT NULL AFTER `ipsrs_rekap_stok_keluar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_jenis` enum('true','false') NULL DEFAULT NULL AFTER `ipsrs_pengeluaran_harian`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_kategori` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_jenis`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_merk` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_kategori`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_ruang` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_merk`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_produsen` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_ruang`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_koleksi` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_produsen`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_inventaris` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_koleksi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `inventaris_sirkulasi` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_inventaris`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `parkir_jenis` enum('true','false') NULL DEFAULT NULL AFTER `inventaris_sirkulasi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `parkir_in` enum('true','false') NULL DEFAULT NULL AFTER `parkir_jenis`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `parkir_out` enum('true','false') NULL DEFAULT NULL AFTER `parkir_in`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `parkir_rekap_harian` enum('true','false') NULL DEFAULT NULL AFTER `parkir_out`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `parkir_rekap_bulanan` enum('true','false') NULL DEFAULT NULL AFTER `parkir_rekap_harian`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `informasi_kamar` enum('true','false') NULL DEFAULT NULL AFTER `parkir_rekap_bulanan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `harian_tindakan_poli` enum('true','false') NULL DEFAULT NULL AFTER `informasi_kamar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `obat_per_poli` enum('true','false') NULL DEFAULT NULL AFTER `harian_tindakan_poli`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `obat_per_kamar` enum('true','false') NULL DEFAULT NULL AFTER `obat_per_poli`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `obat_per_dokter_ralan` enum('true','false') NULL DEFAULT NULL AFTER `obat_per_kamar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `obat_per_dokter_ranap` enum('true','false') NULL DEFAULT NULL AFTER `obat_per_dokter_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `harian_dokter` enum('true','false') NULL DEFAULT NULL AFTER `obat_per_dokter_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `bulanan_dokter` enum('true','false') NULL DEFAULT NULL AFTER `harian_dokter`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `harian_paramedis` enum('true','false') NULL DEFAULT NULL AFTER `bulanan_dokter`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `bulanan_paramedis` enum('true','false') NULL DEFAULT NULL AFTER `harian_paramedis`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pembayaran_ralan` enum('true','false') NULL DEFAULT NULL AFTER `bulanan_paramedis`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pembayaran_ranap` enum('true','false') NULL DEFAULT NULL AFTER `pembayaran_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rekap_pembayaran_ralan` enum('true','false') NULL DEFAULT NULL AFTER `pembayaran_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rekap_pembayaran_ranap` enum('true','false') NULL DEFAULT NULL AFTER `rekap_pembayaran_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tagihan_masuk` enum('true','false') NULL DEFAULT NULL AFTER `rekap_pembayaran_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tambahan_biaya` enum('true','false') NULL DEFAULT NULL AFTER `tagihan_masuk`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `potongan_biaya` enum('true','false') NULL DEFAULT NULL AFTER `tambahan_biaya`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `resep_obat` enum('true','false') NULL DEFAULT NULL AFTER `potongan_biaya`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `resume_pasien` enum('true','false') NULL DEFAULT NULL AFTER `resep_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `penyakit_ralan` enum('true','false') NULL DEFAULT NULL AFTER `resume_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `penyakit_ranap` enum('true','false') NULL DEFAULT NULL AFTER `penyakit_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kamar` enum('true','false') NULL DEFAULT NULL AFTER `penyakit_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tarif_ralan` enum('true','false') NULL DEFAULT NULL AFTER `kamar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tarif_ranap` enum('true','false') NULL DEFAULT NULL AFTER `tarif_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tarif_lab` enum('true','false') NULL DEFAULT NULL AFTER `tarif_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tarif_radiologi` enum('true','false') NULL DEFAULT NULL AFTER `tarif_lab`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tarif_operasi` enum('true','false') NULL DEFAULT NULL AFTER `tarif_radiologi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `akun_rekening` enum('true','false') NULL DEFAULT NULL AFTER `tarif_operasi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rekening_tahun` enum('true','false') NULL DEFAULT NULL AFTER `akun_rekening`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `posting_jurnal` enum('true','false') NULL DEFAULT NULL AFTER `rekening_tahun`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `buku_besar` enum('true','false') NULL DEFAULT NULL AFTER `posting_jurnal`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `cashflow` enum('true','false') NULL DEFAULT NULL AFTER `buku_besar`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `keuangan` enum('true','false') NULL DEFAULT NULL AFTER `cashflow`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pengeluaran` enum('true','false') NULL DEFAULT NULL AFTER `keuangan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `setup_pjlab` enum('true','false') NULL DEFAULT NULL AFTER `pengeluaran`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `setup_otolokasi` enum('true','false') NULL DEFAULT NULL AFTER `setup_pjlab`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `setup_jam_kamin` enum('true','false') NULL DEFAULT NULL AFTER `setup_otolokasi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `setup_embalase` enum('true','false') NULL DEFAULT NULL AFTER `setup_jam_kamin`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `tracer_login` enum('true','false') NULL DEFAULT NULL AFTER `setup_embalase`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `display` enum('true','false') NULL DEFAULT NULL AFTER `tracer_login`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_harga_obat` enum('true','false') NULL DEFAULT NULL AFTER `display`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_penggunaan_tarif` enum('true','false') NULL DEFAULT NULL AFTER `set_harga_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_oto_ralan` enum('true','false') NULL DEFAULT NULL AFTER `set_penggunaan_tarif`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `biaya_harian` enum('true','false') NULL DEFAULT NULL AFTER `set_oto_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `biaya_masuk_sekali` enum('true','false') NULL DEFAULT NULL AFTER `biaya_harian`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_no_rm` enum('true','false') NULL DEFAULT NULL AFTER `biaya_masuk_sekali`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `billing_ralan` enum('true','false') NULL DEFAULT NULL AFTER `set_no_rm`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `billing_ranap` enum('true','false') NULL DEFAULT NULL AFTER `billing_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `jm_ranap_dokter` enum('true','false') NULL DEFAULT NULL AFTER `billing_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `igd` enum('true','false') NULL DEFAULT NULL AFTER `jm_ranap_dokter`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `barcoderalan` enum('true','false') NULL DEFAULT NULL AFTER `igd`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `barcoderanap` enum('true','false') NULL DEFAULT NULL AFTER `barcoderalan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_harga_obat_ralan` enum('true','false') NULL DEFAULT NULL AFTER `barcoderanap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_harga_obat_ranap` enum('true','false') NULL DEFAULT NULL AFTER `set_harga_obat_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `penyakit_pd3i` enum('true','false') NULL DEFAULT NULL AFTER `set_harga_obat_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `surveilans_pd3i` enum('true','false') NULL DEFAULT NULL AFTER `penyakit_pd3i`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `surveilans_ralan` enum('true','false') NULL DEFAULT NULL AFTER `surveilans_pd3i`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `diagnosa_pasien` enum('true','false') NULL DEFAULT NULL AFTER `surveilans_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `surveilans_ranap` enum('true','false') NULL DEFAULT NULL AFTER `diagnosa_pasien`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pny_takmenular_ranap` enum('true','false') NULL DEFAULT NULL AFTER `surveilans_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pny_takmenular_ralan` enum('true','false') NULL DEFAULT NULL AFTER `pny_takmenular_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kunjungan_ralan` enum('true','false') NULL DEFAULT NULL AFTER `pny_takmenular_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rl32` enum('true','false') NULL DEFAULT NULL AFTER `kunjungan_ralan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rl33` enum('true','false') NULL DEFAULT NULL AFTER `rl32`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rl37` enum('true','false') NULL DEFAULT NULL AFTER `rl33`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rl38` enum('true','false') NULL DEFAULT NULL AFTER `rl37`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `harian_tindakan_dokter` enum('true','false') NULL DEFAULT NULL AFTER `rl38`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `sms` enum('true','false') NULL DEFAULT NULL AFTER `harian_tindakan_dokter`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `sidikjari` enum('true','false') NULL DEFAULT NULL AFTER `sms`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `jam_masuk` enum('true','false') NULL DEFAULT NULL AFTER `sidikjari`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `jadwal_pegawai` enum('true','false') NULL DEFAULT NULL AFTER `jam_masuk`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `parkir_barcode` enum('true','false') NULL DEFAULT NULL AFTER `jadwal_pegawai`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `set_nota` enum('true','false') NULL DEFAULT NULL AFTER `parkir_barcode`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `dpjp_ranap` enum('true','false') NULL DEFAULT NULL AFTER `set_nota`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `mutasi_barang` enum('true','false') NULL DEFAULT NULL AFTER `dpjp_ranap`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `rl36` enum('true','false') NULL DEFAULT NULL AFTER `rl34`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `grafik_per_perujuk` enum('true','false') NULL DEFAULT NULL AFTER `surat_pemesanan_non_medis`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `surat_balas` enum('true','false') NULL DEFAULT NULL AFTER `surat_sifat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `pcare_pemberian_tindakan` enum('true','false') NULL DEFAULT NULL AFTER `pcare_pemberian_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kemenkes_sitt` enum('true','false') NULL DEFAULT NULL AFTER `password_asuransi`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `grafik_tb_hasilteshiv` enum('true','false') NULL DEFAULT NULL AFTER `grafik_tb_hasilakhirpengobatan`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `kadaluarsa_batch` enum('true','false') NULL DEFAULT NULL AFTER `grafik_tb_hasilteshiv`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `peminjam_piutang` enum('true','false') NULL DEFAULT NULL AFTER `penilaian_mcu`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `satu_sehat_kirim_clinicalimpression` enum('true','false') NULL DEFAULT NULL AFTER `konfirmasi_rekonsiliasi_obat`;

ALTER TABLE `user` MODIFY COLUMN IF EXISTS `template_persetujuan_penolakan_tindakan` enum('true','false') NULL DEFAULT NULL AFTER `laporan_anestesi`;

SET FOREIGN_KEY_CHECKS=1;