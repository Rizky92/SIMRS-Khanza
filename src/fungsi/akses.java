package fungsi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 *
 * @author Owner
 */

/**
 *
 * @author Owner
 */
public final class akses {
    private static final Connection koneksi=koneksiDB.condb();
    private static PreparedStatement ps,ps2;
    private static ResultSet rs,rs2;

    private static boolean edit = false;
    private static long tglSelesai = -1;
    private static String kode="",kdbangsal="",alamatip="",namars="",alamatrs="",kabupatenrs="",propinsirs="",kontakrs="",emailrs="",form="",namauser="",kode_ppk="",kode_ppk_kemenkes="";
    private static int jml1=0,jml2=0,lebar=0,tinggi=0;
    private static boolean aktif=false,admin=false,user=false,vakum=false,aplikasi=false,penyakit=false,obat_penyakit=false,dokter=false,jadwal_praktek=false,petugas=false,pasien=false,registrasi=false,
            tindakan_ralan=false,kamar_inap=false,tindakan_ranap=false,operasi=false,rujukan_keluar=false,rujukan_masuk=false,beri_obat=false,
            resep_pulang=false,pasien_meninggal=false,diet_pasien=false,kelahiran_bayi=false,periksa_lab=false,periksa_radiologi=false,
            kasir_ralan=false,deposit_pasien=false,piutang_pasien=false,peminjaman_berkas=false,barcode=false,presensi_harian=false,
            presensi_bulanan=false,pegawai_admin=false,pegawai_user=false,suplier=false,satuan_barang=false,konversi_satuan=false,jenis_barang=false,
            obat=false,stok_opname_obat=false,stok_obat_pasien=false,pengadaan_obat=false,pemesanan_obat=false,penjualan_obat=false,piutang_obat=false,
            retur_ke_suplier=false,retur_dari_pembeli=false,retur_obat_ranap=false,retur_piutang_pasien=false,keuntungan_penjualan=false,keuntungan_beri_obat=false,
            sirkulasi_obat=false,ipsrs_barang=false,ipsrs_jenis_barang=false,ipsrs_pengadaan_barang=false,ipsrs_stok_keluar=false,ipsrs_rekap_pengadaan=false,ipsrs_rekap_stok_keluar=false,
            ipsrs_pengeluaran_harian=false,inventaris_jenis=false,inventaris_kategori=false,inventaris_merk=false,inventaris_ruang=false,inventaris_produsen=false,
            inventaris_koleksi=false,inventaris_inventaris=false,inventaris_sirkulasi=false,parkir_jenis=false,parkir_in=false,parkir_out=false,
            parkir_rekap_harian=false,parkir_rekap_bulanan=false,informasi_kamar=false,harian_tindakan_poli=false,obat_per_poli=false,obat_per_kamar=false,
            obat_per_dokter_ralan=false,obat_per_dokter_ranap=false,harian_dokter=false,bulanan_dokter=false,harian_paramedis=false,bulanan_paramedis=false,
            pembayaran_ralan=false,pembayaran_ranap=false,rekap_pembayaran_ralan=false,rekap_pembayaran_ranap=false,tagihan_masuk=false,tambahan_biaya=false,
            potongan_biaya=false,resep_obat=false,resume_pasien=false,penyakit_ralan=false,penyakit_ranap=false,kamar=false,tarif_ralan=false,tarif_ranap=false,
            tarif_lab=false,tarif_radiologi=false,tarif_operasi=false,akun_rekening=false,rekening_tahun=false,posting_jurnal=false,buku_besar=false,
            cashflow=false,keuangan=false,pengeluaran=false,setup_pjlab=false,setup_otolokasi=false,setup_jam_kamin=false,setup_embalase=false,tracer_login=false,
            display=false,set_harga_obat=false,set_penggunaan_tarif=false,set_oto_ralan=false,biaya_harian=false,biaya_masuk_sekali=false,set_no_rm=false,
            billing_ralan=false,billing_ranap=false,status=false,jm_ranap_dokter=false,igd=false,barcoderalan=false,barcoderanap=false,set_harga_obat_ralan=false,
            set_harga_obat_ranap=false,penyakit_pd3i=false,surveilans_pd3i=false,surveilans_ralan=false,diagnosa_pasien=false,surveilans_ranap=false,
            pny_takmenular_ranap=false,pny_takmenular_ralan=false,kunjungan_ralan=false,rl32=false,rl33=false,rl37=false,rl38=false,harian_tindakan_dokter=false,
            sms=false,sidikjari=false,jam_masuk=false,jadwal_pegawai=false,parkir_barcode=false,set_nota=false,dpjp_ranap=false,mutasi_barang=false,rl34=false,rl36=false,
            fee_visit_dokter=false,fee_bacaan_ekg=false,fee_rujukan_rontgen=false,fee_rujukan_ranap=false,fee_ralan=false,akun_bayar=false,bayar_pemesanan_obat=false,
            obat_per_dokter_peresep=false,pemasukan_lain=false,pengaturan_rekening=false,closing_kasir=false,keterlambatan_presensi=false,set_harga_kamar=false,
            rekap_per_shift=false,bpjs_cek_nik=false,bpjs_cek_kartu=false,bpjs_cek_riwayat=false,obat_per_cara_bayar=false,kunjungan_ranap=false,bayar_piutang=false,
            payment_point=false,bpjs_cek_nomor_rujukan=false,icd9=false,darurat_stok=false,retensi_rm=false,temporary_presensi=false,jurnal_harian=false,
            sirkulasi_obat2=false,edit_registrasi=false,bpjs_referensi_diagnosa=false,bpjs_referensi_poli=false,industrifarmasi=false,harian_js=false,bulanan_js=false,
            harian_paket_bhp=false,bulanan_paket_bhp=false,piutang_pasien2=false,bpjs_referensi_faskes=false,bpjs_sep=false,pengambilan_utd=false,tarif_utd=false,
            pengambilan_utd2=false,utd_medis_rusak=false,pengambilan_penunjang_utd=false,pengambilan_penunjang_utd2=false,utd_penunjang_rusak=false,
            suplier_penunjang=false,utd_donor=false,bpjs_monitoring_klaim=false,utd_cekal_darah=false,utd_komponen_darah=false,utd_stok_darah=false,
            utd_pemisahan_darah=false,harian_kamar=false,rincian_piutang_pasien=false,keuntungan_beri_obat_nonpiutang=false,reklasifikasi_ralan=false,
            reklasifikasi_ranap=false,utd_penyerahan_darah=false,hutang_obat=false,riwayat_obat_alkes_bhp=false,sensus_harian_poli=false,rl4a=false,
            aplicare_referensi_kamar=false,aplicare_ketersediaan_kamar=false,inacbg_klaim_baru_otomatis=false,inacbg_klaim_baru_manual=false,inacbg_coder_nik=false,
            mutasi_berkas=false,akun_piutang=false,harian_kso=false,bulanan_kso=false,harian_menejemen=false,bulanan_menejemen=false,inhealth_cek_eligibilitas=false,
            inhealth_referensi_jenpel_ruang_rawat=false,inhealth_referensi_poli=false,inhealth_referensi_faskes=false,inhealth_sjp=false,piutang_ralan=false,
            piutang_ranap=false,detail_piutang_penjab=false,lama_pelayanan_ralan=false,catatan_pasien=false,rl4b=false,rl4asebab=false,rl4bsebab=false,
            data_HAIs=false,harian_HAIs=false,bulanan_HAIs=false,hitung_bor=false,perusahaan_pasien=false,resep_dokter=false,lama_pelayanan_apotek=false,
            hitung_alos=false,detail_tindakan=false,rujukan_poli_internal=false,rekap_poli_anak=false,grafik_kunjungan_poli=false,grafik_kunjungan_perdokter=false,
            grafik_kunjungan_perpekerjaan=false,grafik_kunjungan_perpendidikan=false,grafik_kunjungan_pertahun=false,berkas_digital_perawatan=false,
            penyakit_menular_ranap=false,penyakit_menular_ralan=false,grafik_kunjungan_perbulan=false,grafik_kunjungan_pertanggal=false,grafik_kunjungan_demografi=false,
            grafik_kunjungan_statusdaftartahun=false,grafik_kunjungan_statusdaftartahun2=false,grafik_kunjungan_statusdaftarbulan=false,grafik_kunjungan_statusdaftarbulan2=false,
            grafik_kunjungan_statusdaftartanggal=false,grafik_kunjungan_statusdaftartanggal2=false,grafik_kunjungan_statusbataltahun=false,grafik_kunjungan_statusbatalbulan=false,
            pcare_cek_penyakit=false,grafik_kunjungan_statusbataltanggal=false,kategori_barang=false,golongan_barang=false,pemberian_obat_pertanggal=false,
            penjualan_obat_pertanggal=false,pcare_cek_kesadaran=false,pembatalan_periksa_dokter=false,pembayaran_per_unit=false,rekap_pembayaran_per_unit=false,
            grafik_kunjungan_percarabayar=false,ipsrs_pengadaan_pertanggal=false,ipsrs_stokkeluar_pertanggal=false,grafik_kunjungan_ranaptahun=false,
            pcare_cek_rujukan=false,grafik_lab_ralantahun=false,grafik_rad_ralantahun=false,cek_entry_ralan=false,inacbg_klaim_baru_manual2=false,
            permintaan_medis=false,rekap_permintaan_medis=false,surat_pemesanan_medis=false,permintaan_non_medis=false,rekap_permintaan_non_medis=false,
            surat_pemesanan_non_medis=false,grafik_per_perujuk=false,bpjs_cek_prosedur=false,bpjs_cek_kelas_rawat=false,bpjs_cek_dokter=false,
            bpjs_cek_spesialistik=false,bpjs_cek_ruangrawat=false,bpjs_cek_carakeluar=false,bpjs_cek_pasca_pulang=false,detail_tindakan_okvk=false,
            billing_parsial=false,bpjs_cek_nomor_rujukan_rs=false,bpjs_cek_rujukan_kartu_pcare=false,bpjs_cek_rujukan_kartu_rs=false,akses_depo_obat=false,
            bpjs_rujukan_keluar=false,grafik_lab_ralanbulan=false,pengeluaran_stok_apotek=false,grafik_rad_ralanbulan=false,detailjmdokter2=false,
            pengaduan_pasien=false,grafik_lab_ralanhari=false,grafik_rad_ralanhari=false,sensus_harian_ralan=false,metode_racik=false,pembayaran_akun_bayar=false,
            pengguna_obat_resep=false,rekap_pemesanan=false,master_berkas_pegawai=false,berkas_kepegawaian=false,riwayat_jabatan=false,riwayat_pendidikan=false,
            riwayat_naik_gaji=false,kegiatan_ilmiah=false,riwayat_penghargaan=false,riwayat_penelitian=false,penerimaan_non_medis=false,bayar_pesan_non_medis=false,
            hutang_barang_non_medis=false,rekap_pemesanan_non_medis=false,insiden_keselamatan=false,insiden_keselamatan_pasien=false,grafik_ikp_pertahun=false,
            grafik_ikp_perbulan=false,grafik_ikp_pertanggal=false,riwayat_data_batch=false,grafik_ikp_jenis=false,grafik_ikp_dampak=false,piutang_akun_piutang=false,
            grafik_kunjungan_per_agama=false,grafik_kunjungan_per_umur=false,suku_bangsa=false,bahasa_pasien=false,golongan_tni=false,satuan_tni=false,
            jabatan_tni=false,pangkat_tni=false,golongan_polri=false,satuan_polri=false,jabatan_polri=false,pangkat_polri=false,cacat_fisik=false,
            grafik_kunjungan_suku=false,grafik_kunjungan_bahasa=false,booking_operasi=false,mapping_poli_bpjs=false,grafik_kunjungan_per_cacat=false,
            barang_cssd=false,skdp_bpjs=false,booking_registrasi=false,bpjs_cek_propinsi=false,bpjs_cek_kabupaten=false,bpjs_cek_kecamatan=false,
            bpjs_cek_dokterdpjp=false,bpjs_cek_riwayat_rujukanrs=false,bpjs_cek_tanggal_rujukan=false,permintaan_lab=false,permintaan_radiologi=false,
            surat_indeks=false,surat_map=false,surat_almari=false,surat_rak=false,surat_ruang=false,surat_klasifikasi=false,surat_status=false,
            surat_sifat=false,surat_balas=false,surat_masuk=false,pcare_cek_dokter=false,pcare_cek_poli=false,pcare_cek_provider=false,
            pcare_cek_statuspulang=false,pcare_cek_spesialis=false,pcare_cek_subspesialis=false,pcare_cek_sarana=false,pcare_cek_khusus=false,
            pcare_cek_obat=false,pcare_cek_tindakan=false,pcare_cek_faskessubspesialis=false,pcare_cek_faskesalihrawat=false,
            pcare_cek_faskesthalasemia=false,pcare_mapping_obat=false,pcare_mapping_tindakan=false,pcare_club_prolanis=false,
            pcare_mapping_poli=false,pcare_kegiatan_kelompok=false,pcare_mapping_tindakan_ranap=false,pcare_peserta_kegiatan_kelompok=false,
            sirkulasi_obat3=false,bridging_pcare_daftar=false,pcare_mapping_dokter=false,ranap_per_ruang=false,penyakit_ranap_cara_bayar=false,
            anggota_militer_dirawat=false,set_input_parsial=false,lama_pelayanan_radiologi=false,lama_pelayanan_lab=false,bpjs_cek_sep=false,
            catatan_perawatan=false,surat_keluar=false,kegiatan_farmasi=false,stok_opname_logistik=false,sirkulasi_non_medis=false,
            rekap_lab_pertahun=false,perujuk_lab_pertahun=false,rekap_radiologi_pertahun=false,perujuk_radiologi_pertahun=false,
            jumlah_porsi_diet=false,jumlah_macam_diet=false,payment_point2=false,pembayaran_akun_bayar2=false,hapus_nota_salah=false,
            hais_perbangsal=false,ppn_obat=false,saldo_akun_perbulan=false,display_apotek=false,sisrute_referensi_faskes=false,
            sisrute_referensi_alasanrujuk=false,sisrute_referensi_diagnosa=false,sisrute_rujukan_masuk=false,sisrute_rujukan_keluar=false,
            bpjs_cek_skdp=false,data_batch=false,kunjungan_permintaan_lab=false,kunjungan_permintaan_lab2=false,kunjungan_permintaan_radiologi=false,
            kunjungan_permintaan_radiologi2=false,pcare_pemberian_obat=false,pcare_pemberian_tindakan=false,pembayaran_akun_bayar3=false,
            password_asuransi=false,kemenkes_sitt=false,siranap_ketersediaan_kamar=false,grafik_tb_periodelaporan=false,grafik_tb_rujukan=false,
            grafik_tb_riwayat=false,grafik_tb_tipediagnosis=false,grafik_tb_statushiv=false,grafik_tb_skoringanak=false,grafik_tb_konfirmasiskoring5=false,
            grafik_tb_konfirmasiskoring6=false,grafik_tb_sumberobat=false,grafik_tb_hasilakhirpengobatan=false,grafik_tb_hasilteshiv=false,
            kadaluarsa_batch=false,sisa_stok=false,obat_per_resep=false,pemakaian_air_pdam=false,limbah_b3_medis=false,grafik_air_pdam_pertanggal=false,
            grafik_air_pdam_perbulan=false,grafik_limbahb3_pertanggal=false,grafik_limbahb3_perbulan=false,limbah_domestik=false,
            grafik_limbahdomestik_pertanggal=false,grafik_limbahdomestik_perbulan=false,mutu_air_limbah=false,pest_control=false,ruang_perpustakaan=false,
            kategori_perpustakaan=false,jenis_perpustakaan=false,pengarang_perpustakaan=false,penerbit_perpustakaan=false,koleksi_perpustakaan=false,
            inventaris_perpustakaan=false,set_peminjaman_perpustakaan=false,denda_perpustakaan=false,anggota_perpustakaan=false,
            peminjaman_perpustakaan=false,bayar_denda_perpustakaan=false,ebook_perpustakaan=false,jenis_cidera_k3rs=false,penyebab_k3rs=false,
            jenis_luka_k3rs=false,lokasi_kejadian_k3rs=false,dampak_cidera_k3rs=false,jenis_pekerjaan_k3rs=false,bagian_tubuh_k3rs=false,
            peristiwa_k3rs=false,grafik_k3_pertahun=false,grafik_k3_perbulan=false,grafik_k3_pertanggal=false,grafik_k3_perjeniscidera=false,
            grafik_k3_perpenyebab=false,grafik_k3_perjenisluka=false,grafik_k3_lokasikejadian=false,grafik_k3_dampakcidera=false,
            grafik_k3_perjenispekerjaan=false,grafik_k3_perbagiantubuh=false,jenis_cidera_k3rstahun=false,penyebab_k3rstahun=false,
            jenis_luka_k3rstahun=false,lokasi_kejadian_k3rstahun=false,dampak_cidera_k3rstahun=false,jenis_pekerjaan_k3rstahun=false,
            bagian_tubuh_k3rstahun=false,sekrining_rawat_jalan=false,bpjs_histori_pelayanan=false,rekap_mutasi_berkas=false,
            skrining_ralan_pernapasan_pertahun=false,pengajuan_barang_medis=false,pengajuan_barang_nonmedis=false,grafik_kunjungan_ranapbulan=false,
            grafik_kunjungan_ranaptanggal=false,grafik_kunjungan_ranap_peruang=false,kunjungan_bangsal_pertahun=false,grafik_jenjang_jabatanpegawai=false,
            grafik_bidangpegawai=false,grafik_departemenpegawai=false,grafik_pendidikanpegawai=false,grafik_sttswppegawai=false,
            grafik_sttskerjapegawai=false,grafik_sttspulangranap=false,kip_pasien_ranap=false,kip_pasien_ralan=false,bpjs_mapping_dokterdpjp=false,
            data_triase_igd=false,master_triase_skala1=false,master_triase_skala2=false,master_triase_skala3=false,master_triase_skala4=false,
            master_triase_skala5=false,master_triase_pemeriksaan=false,master_triase_macamkasus=false,rekap_permintaan_diet=false,daftar_pasien_ranap=false,
            daftar_pasien_ranaptni=false,pengajuan_asetinventaris=false,item_apotek_jenis=false,item_apotek_kategori=false,item_apotek_golongan=false,
            item_apotek_industrifarmasi=false,obat10_terbanyak_poli=false,grafik_pengajuan_aset_urgensi=false,grafik_pengajuan_aset_status=false,
            grafik_pengajuan_aset_departemen=false,rekap_pengajuan_aset_departemen=false,grafik_kelompok_jabatanpegawai=false,grafik_resiko_kerjapegawai=false,
            grafik_emergency_indexpegawai=false,grafik_inventaris_ruang=false,harian_HAIs2=false,grafik_inventaris_jenis=false,data_resume_pasien=false,
            perkiraan_biaya_ranap=false,rekap_obat_poli=false,rekap_obat_pasien=false,grafik_HAIs_pasienbangsal=false,grafik_HAIs_pasienbulan=false,
            permintaan_perbaikan_inventaris=false,grafik_HAIs_laju_vap=false,grafik_HAIs_laju_iad=false,grafik_HAIs_laju_pleb=false,grafik_HAIs_laju_isk=false,
            grafik_HAIs_laju_ilo=false,grafik_HAIs_laju_hap=false,inhealth_mapping_poli=false,inhealth_mapping_dokter=false,inhealth_mapping_tindakan_ralan=false,
            inhealth_mapping_tindakan_ranap=false,inhealth_mapping_tindakan_radiologi=false,inhealth_mapping_tindakan_laborat=false,
            inhealth_mapping_tindakan_operasi=false,hibah_obat_bhp=false,asal_hibah=false,asuhan_gizi=false,inhealth_kirim_tagihan=false,
            sirkulasi_obat4=false,sirkulasi_obat5=false,sirkulasi_non_medis2=false,monitoring_asuhan_gizi=false,penerimaan_obat_perbulan=false,
            rekap_kunjungan=false,surat_sakit=false,penilaian_awal_keperawatan_ralan=false,permintaan_diet=false,master_masalah_keperawatan=false,
            pengajuan_cuti=false,kedatangan_pasien=false,utd_pendonor=false,toko_suplier=false,toko_jenis=false,toko_set_harga=false,
            toko_barang=false,penagihan_piutang_pasien=false,akun_penagihan_piutang=false,stok_opname_toko=false,toko_riwayat_barang=false,
            toko_surat_pemesanan=false,toko_pengajuan_barang=false,toko_penerimaan_barang=false,toko_pengadaan_barang=false,toko_hutang=false,
            toko_bayar_pemesanan=false,toko_member=false,toko_penjualan=false,registrasi_poli_per_tanggal=false,toko_piutang=false,toko_retur_beli=false,
            ipsrs_returbeli=false,ipsrs_riwayat_barang=false,pasien_corona=false,toko_pendapatan_harian=false,diagnosa_pasien_corona=false,
            perawatan_pasien_corona=false,penilaian_awal_keperawatan_gigi=false,master_masalah_keperawatan_gigi=false,toko_bayar_piutang=false,
            toko_piutang_harian=false,toko_penjualan_harian=false,deteksi_corona=false,penilaian_awal_keperawatan_kebidanan=false,pengumuman_epasien=false,
            surat_hamil=false,set_tarif_online=false,booking_periksa=false,toko_sirkulasi=false,toko_retur_jual=false,toko_retur_piutang=false,
            toko_sirkulasi2=false,toko_keuntungan_barang=false,zis_pengeluaran_penerima_dankes=false,zis_penghasilan_penerima_dankes=false,
            zis_ukuran_rumah_penerima_dankes=false,zis_dinding_rumah_penerima_dankes=false,zis_lantai_rumah_penerima_dankes=false,
            zis_atap_rumah_penerima_dankes=false,zis_kepemilikan_rumah_penerima_dankes=false,zis_kamar_mandi_penerima_dankes=false,
            zis_dapur_rumah_penerima_dankes=false,zis_kursi_rumah_penerima_dankes=false,zis_kategori_phbs_penerima_dankes=false,
            zis_elektronik_penerima_dankes=false,zis_ternak_penerima_dankes=false,zis_jenis_simpanan_penerima_dankes=false,penilaian_awal_keperawatan_anak=false,
            zis_kategori_asnaf_penerima_dankes=false,master_masalah_keperawatan_anak=false,master_imunisasi=false,zis_patologis_penerima_dankes=false,
            pcare_cek_kartu=false,surat_bebas_narkoba=false,surat_keterangan_covid=false,pemakaian_air_tanah=false,grafik_air_tanah_pertanggal=false,
            grafik_air_tanah_perbulan=false,lama_pelayanan_poli=false,hemodialisa=false,grafik_harian_hemodialisa=false,grafik_bulanan_hemodialisa=false,
            grafik_tahunan_hemodialisa=false,grafik_bulanan_meninggal=false,laporan_tahunan_irj=false,perbaikan_inventaris=false,surat_cuti_hamil=false,
            permintaan_stok_obat_pasien=false,pemeliharaan_inventaris=false,klasifikasi_pasien_ranap=false,bulanan_klasifikasi_pasien_ranap,
            harian_klasifikasi_pasien_ranap=false,klasifikasi_pasien_perbangsal=false,soap_perawatan=false,klaim_rawat_jalan=false,
            skrining_gizi=false,lama_penyiapan_rm=false,dosis_radiologi=false,demografi_umur_kunjungan=false,jam_diet_pasien=false,
            rvu_bpjs=false,verifikasi_penerimaan_farmasi=false,verifikasi_penerimaan_logistik=false,pemeriksaan_lab_pa=false,ringkasan_pengajuan_obat=false,
            ringkasan_pemesanan_obat=false,ringkasan_pengadaan_obat=false,ringkasan_penerimaan_obat=false,ringkasan_hibah_obat=false,
            ringkasan_penjualan_obat=false,ringkasan_beri_obat=false,ringkasan_piutang_obat=false,ringkasan_stok_keluar_obat=false,
            ringkasan_retur_suplier_obat=false,ringkasan_retur_pembeli_obat=false,penilaian_awal_keperawatan_ranapkebidanan=false,
            ringkasan_pengajuan_nonmedis=false,ringkasan_pemesanan_nonmedis=false,ringkasan_pengadaan_nonmedis=false,ringkasan_penerimaan_nonmedis=false,
            ringkasan_stokkeluar_nonmedis=false,ringkasan_returbeli_nonmedis=false,omset_penerimaan=false,validasi_penagihan_piutang=false,
            permintaan_ranap=false,bpjs_diagnosa_prb=false,bpjs_obat_prb=false,bpjs_surat_kontrol=false,penggunaan_bhp_ok=false,surat_keterangan_rawat_inap=false,
            surat_keterangan_sehat=false,pendapatan_per_carabayar=false,akun_host_to_host_bank_jateng=false,pembayaran_bank_jateng=false,
            bpjs_surat_pri=false,ringkasan_tindakan=false,lama_pelayanan_pasien=false,surat_sakit_pihak_2=false,tagihan_hutang_obat=false,
            referensi_mobilejkn_bpjs=false,batal_pendaftaran_mobilejkn_bpjs=false,lama_operasi=false,grafik_inventaris_kategori=false,grafik_inventaris_merk=false,
            grafik_inventaris_produsen=false,pengembalian_deposit_pasien=false,validasi_tagihan_hutang_obat=false,piutang_obat_belum_lunas=false,
            integrasi_briapi=false,pengadaan_aset_inventaris=false,akun_aset_inventaris=false,suplier_inventaris=false,penerimaan_aset_inventaris=false,
            bayar_pemesanan_iventaris=false,hutang_aset_inventaris=false,hibah_aset_inventaris=false,titip_faktur_non_medis=false,validasi_tagihan_non_medis=false,
            titip_faktur_aset=false,validasi_tagihan_aset=false,hibah_non_medis=false,pcare_alasan_tacc=false,resep_luar=false,surat_bebas_tbc=false,
            surat_buta_warna=false,surat_bebas_tato=false,surat_kewaspadaan_kesehatan=false,grafik_porsidiet_pertanggal=false,grafik_porsidiet_perbulan=false,
            grafik_porsidiet_pertahun=false,grafik_porsidiet_perbangsal=false,penilaian_awal_medis_ralan=false,master_masalah_keperawatan_mata=false,
            penilaian_awal_keperawatan_mata=false,penilaian_awal_medis_ranap=false,penilaian_awal_medis_ranap_kebidanan=false,penilaian_awal_medis_ralan_kebidanan=false,
            penilaian_awal_medis_igd=false,penilaian_awal_medis_ralan_anak=false,bpjs_referensi_poli_hfis=false,bpjs_referensi_dokter_hfis=false,
            bpjs_referensi_jadwal_hfis=false,penilaian_fisioterapi=false,bpjs_program_prb=false,bpjs_suplesi_jasaraharja=false,bpjs_data_induk_kecelakaan=false,
            bpjs_sep_internal=false,bpjs_klaim_jasa_raharja=false,bpjs_daftar_finger_print=false,bpjs_rujukan_khusus=false,pemeliharaan_gedung=false,
            grafik_perbaikan_inventaris_pertanggal=false,grafik_perbaikan_inventaris_perbulan=false,grafik_perbaikan_inventaris_pertahun=false,
            grafik_perbaikan_inventaris_perpelaksana_status=false,penilaian_mcu=false,peminjam_piutang=false,piutang_lainlain=false,cara_bayar=false,
            audit_kepatuhan_apd=false,bpjs_task_id=false,bayar_piutang_lain=false,pembayaran_akun_bayar4=false,stok_akhir_farmasi_pertanggal=false,
            riwayat_kamar_pasien=false,uji_fungsi_kfr=false,hapus_berkas_digital_perawatan=false,kategori_pengeluaran_harian=false,kategori_pemasukan_lain=false,
            pembayaran_akun_bayar5=false,ruang_ok=false,jasa_tindakan_pasien=false,telaah_resep=false,permintaan_resep_pulang=false,rekap_jm_dokter=false,
            status_data_rm=false,ubah_petugas_lab_pk=false,ubah_petugas_lab_pa=false,ubah_petugas_radiologi=false,gabung_norawat=false,gabung_rm=false,
            ringkasan_biaya_obat_pasien_pertanggal=false,master_masalah_keperawatan_igd=false,penilaian_awal_keperawatan_igd=false,bpjs_referensi_dpho_apotek=false,
            bpjs_referensi_poli_apotek=false,bayar_jm_dokter=false,bpjs_referensi_faskes_apotek=false,bpjs_referensi_spesialistik_apotek=false,
            pembayaran_briva=false,penilaian_awal_keperawatan_ranap=false,nilai_penerimaan_vendor_farmasi_perbulan=false,akun_bayar_hutang=false,
            master_rencana_keperawatan=false,laporan_tahunan_igd=false,obat_bhp_tidakbergerak=false,ringkasan_hutang_vendor_farmasi=false,
            nilai_penerimaan_vendor_nonmedis_perbulan=false,ringkasan_hutang_vendor_nonmedis=false,anggota_polri_dirawat=false,daftar_pasien_ranap_polri=false,
            soap_ralan_polri=false,soap_ranap_polri=false,laporan_penyakit_polri=false,master_rencana_keperawatan_anak=false,jumlah_pengunjung_ralan_polri=false,
            catatan_observasi_igd=false,catatan_observasi_ranap=false,catatan_observasi_ranap_kebidanan=false,catatan_observasi_ranap_postpartum=false,
            penilaian_awal_medis_ralan_tht=false,penilaian_psikologi=false,audit_cuci_tangan_medis=false,audit_pembuangan_limbah=false,ruang_audit_kepatuhan=false,
            audit_pembuangan_benda_tajam=false,audit_penanganan_darah=false,audit_pengelolaan_linen_kotor=false,audit_penempatan_pasien=false,
            audit_kamar_jenazah=false,audit_bundle_iadp=false,audit_bundle_ido=false,audit_fasilitas_kebersihan_tangan=false,audit_fasilitas_apd=false,
            audit_pembuangan_limbah_cair_infeksius=false,audit_sterilisasi_alat=false,penilaian_awal_medis_ralan_psikiatri=false,persetujuan_penolakan_tindakan=false,
            audit_bundle_isk=false,audit_bundle_plabsi=false,audit_bundle_vap=false,akun_host_to_host_bank_papua=false,pembayaran_bank_papua=false,
            penilaian_awal_medis_ralan_penyakit_dalam=false,penilaian_awal_medis_ralan_mata=false,penilaian_awal_medis_ralan_neurologi=false,sirkulasi_obat6=false,
            penilaian_awal_medis_ralan_orthopedi=false,penilaian_awal_medis_ralan_bedah=false,integrasi_khanza_health_services=false,soap_ralan_tni=false,
            soap_ranap_tni=false,jumlah_pengunjung_ralan_tni=false,laporan_penyakit_tni=false,catatan_keperawatan_ranap=false,master_rencana_keperawatan_gigi=false,
            master_rencana_keperawatan_mata=false,master_rencana_keperawatan_igd=false,master_masalah_keperawatan_psikiatri=false,master_rencana_keperawatan_psikiatri=false,
            penilaian_awal_keperawatan_psikiatri=false,pemantauan_pews_anak=false,surat_pulang_atas_permintaan_sendiri=false,template_hasil_radiologi=false,
            laporan_bulanan_irj=false,template_pemeriksaan=false,pemeriksaan_lab_mb=false,ubah_petugas_lab_mb=false,penilaian_pre_operasi=false,penilaian_pre_anestesi=false,
            perencanaan_pemulangan=false,penilaian_lanjutan_resiko_jatuh_dewasa=false,penilaian_lanjutan_resiko_jatuh_anak=false,penilaian_awal_medis_ralan_geriatri=false,
            penilaian_tambahan_pasien_geriatri=false,skrining_nutrisi_dewasa=false,skrining_nutrisi_lansia=false,hasil_pemeriksaan_usg=false,skrining_nutrisi_anak=false,
            akun_host_to_host_bank_jabar=false,pembayaran_bank_jabar=false,surat_pernyataan_pasien_umum=false,konseling_farmasi=false,pelayanan_informasi_obat=false,
            jawaban_pio_apoteker=false,surat_persetujuan_umum=false,transfer_pasien_antar_ruang=false,satu_sehat_referensi_dokter=false,satu_sehat_referensi_pasien=false,
            satu_sehat_mapping_departemen=false,satu_sehat_mapping_lokasi=false,satu_sehat_kirim_encounter=false,catatan_cek_gds=false,satu_sehat_kirim_condition=false,
            checklist_pre_operasi=false,satu_sehat_kirim_observationttv=false,signin_sebelum_anestesi=false,satu_sehat_kirim_procedure=false,operasi_per_bulan=false,
            timeout_sebelum_insisi=false,signout_sebelum_menutup_luka=false,dapur_barang=false,dapur_opname=false,satu_sehat_mapping_vaksin=false,dapur_suplier=false,
            satu_sehat_kirim_Immunization=false,checklist_post_operasi=false,dapur_pembelian=false,dapur_stok_keluar=false,dapur_riwayat_barang=false,permintaan_dapur=false,
            rekonsiliasi_obat=false,biaya_pengadaan_dapur=false,rekap_pengadaan_dapur=false,kesling_limbah_b3medis_cair=false,grafik_limbahb3cair_pertanggal=false,
            grafik_limbahb3cair_perbulan=false,rekap_biaya_registrasi=false,konfirmasi_rekonsiliasi_obat=false,satu_sehat_kirim_clinicalimpression=false,
            penilaian_pasien_terminal=false,surat_persetujuan_rawat_inap=false,monitoring_reaksi_tranfusi=false,penilaian_korban_kekerasan=false,
            penilaian_lanjutan_resiko_jatuh_lansia=false,mpp_skrining=false,penilaian_pasien_penyakit_menular=false,edukasi_pasien_keluarga_rj=false,pemantauan_pews_dewasa=false,
            penilaian_tambahan_bunuh_diri=false,bpjs_antrean_pertanggal=false,penilaian_tambahan_perilaku_kekerasan=false,penilaian_tambahan_beresiko_melarikan_diri=false,
            persetujuan_penundaan_pelayanan=false,sisa_diet_pasien=false,penilaian_awal_medis_ralan_bedah_mulut=false,penilaian_pasien_keracunan=false,
            pemantauan_meows_obstetri=false,catatan_adime_gizi=false,pengajuan_biaya=false,penilaian_awal_keperawatan_ralan_geriatri=false,master_masalah_keperawatan_geriatri=false,
            master_rencana_keperawatan_geriatri=false,checklist_kriteria_masuk_hcu=false,checklist_kriteria_keluar_hcu=false,penilaian_risiko_dekubitus=false,
            master_menolak_anjuran_medis=false,penolakan_anjuran_medis=false,laporan_tahunan_penolakan_anjuran_medis=false,template_laporan_operasi=false,hasil_tindakan_eswl=false,
            checklist_kriteria_masuk_icu=false,checklist_kriteria_keluar_icu=false,akses_dokter_lain_rawat_jalan=false,follow_up_dbd=false,penilaian_risiko_jatuh_neonatus=false,
            persetujuan_pengajuan_biaya=false,pemeriksaan_fisik_ralan_per_penyakit=false,penilaian_lanjutan_resiko_jatuh_geriatri=false,pemantauan_ews_neonatus=false,
            validasi_persetujuan_pengajuan_biaya=false,riwayat_perawatan_icare_bpjs=false,rekap_pengajuan_biaya=false,penilaian_awal_medis_ralan_kulit_kelamin=false,
            akun_host_to_host_bank_mandiri=false,penilaian_medis_hemodialisa=false,penilaian_level_kecemasan_ranap_anak=false,penilaian_lanjutan_resiko_jatuh_psikiatri=false,
            penilaian_lanjutan_skrining_fungsional=false,penilaian_medis_ralan_rehab_medik=false,laporan_anestesi=false,template_persetujuan_penolakan_tindakan=false,
            penilaian_medis_ralan_gawat_darurat_psikiatri=false,bpjs_referensi_setting_apotek=false,bpjs_referensi_obat_apotek=false,bpjs_mapping_obat_apotek=false,
            pembayaran_bank_mandiri=false,penilaian_ulang_nyeri=false,penilaian_terapi_wicara=false,bpjs_obat_23hari_apotek=false,pengkajian_restrain=false,
            bpjs_kunjungan_sep_apotek=false,bpjs_monitoring_klaim_apotek=false,bpjs_daftar_pelayanan_obat_apotek=false,penilaian_awal_medis_ralan_paru=false,
            catatan_keperawatan_ralan=false,catatan_persalinan=false,skor_aldrette_pasca_anestesi=false,skor_steward_pasca_anestesi=false,skor_bromage_pasca_anestesi=false,
            penilaian_pre_induksi=false,hasil_usg_urologi=false,hasil_usg_gynecologi=false,hasil_pemeriksaan_ekg=false,hapus_edit_sep_bpjs=false,satu_sehat_kirim_diet=false,
            satu_sehat_mapping_obat=false,dapur_ringkasan_pembelian=false,satu_sehat_kirim_medication=false,satu_sehat_kirim_medicationrequest=false,
            penatalaksanaan_terapi_okupasi=false,satu_sehat_kirim_medicationdispense=false,hasil_usg_neonatus=false,hasil_endoskopi_faring_laring=false,
            satu_sehat_mapping_radiologi=false,satu_sehat_kirim_servicerequest_radiologi=false,hasil_endoskopi_hidung=false,satu_sehat_kirim_specimen_radiologi=false,
            master_masalah_keperawatan_neonatus=false,master_rencana_keperawatan_neonatus=false,penilaian_awal_keperawatan_ranap_neonatus=false,
            satu_sehat_kirim_observation_radiologi=false,satu_sehat_kirim_diagnosticreport_radiologi=false,hasil_endoskopi_telinga=false,satu_sehat_mapping_lab=false,
            satu_sehat_kirim_servicerequest_lab=false,satu_sehat_kirim_servicerequest_labmb=false,satu_sehat_kirim_specimen_lab=false,satu_sehat_kirim_specimen_labmb=false,
            satu_sehat_kirim_observation_lab=false,satu_sehat_kirim_observation_labmb=false,satu_sehat_kirim_diagnosticreport_lab=false,satu_sehat_kirim_diagnosticreport_labmb=false,
            kepatuhan_kelengkapan_keselamatan_bedah=false,nilai_piutang_perjenis_bayar_per_bulan=false,ringkasan_piutang_jenis_bayar=false,penilaian_pasien_imunitas_rendah=false,
            balance_cairan=false,catatan_observasi_chbp=false,catatan_observasi_induksi_persalinan=false,skp_kategori_penilaian=false,skp_kriteria_penilaian=false,
            skp_penilaian=false,referensi_poli_mobilejknfktp=false,referensi_dokter_mobilejknfktp=false,skp_rekapitulasi_penilaian=false,pembayaran_pihak_ke3_bankmandiri=false,
            metode_pembayaran_bankmandiri=false,bank_tujuan_transfer_bankmandiri=false,kodetransaksi_tujuan_transfer_bankmandiri=false,konsultasi_medik=false,jawaban_konsultasi_medik=false,
            pcare_cek_alergi=false,pcare_cek_prognosa=false,data_sasaran_usiaproduktif=false,data_sasaran_usialansia=false,skrining_perilaku_merokok_sekolah_remaja=false,
            skrining_kekerasan_pada_perempuan=false,skrining_obesitas=false,skrining_risiko_kanker_payudara=false,skrining_risiko_kanker_paru=false,skrining_tbc=false,
            skrining_kesehatan_gigi_mulut_remaja=false,penilaian_awal_keperawatan_ranap_bayi=false,booking_mcu_perusahaan=false,catatan_observasi_restrain_nonfarma=false,
            catatan_observasi_ventilator=false,catatan_anestesi_sedasi=false,skrining_puma=false,satu_sehat_kirim_careplan=false,satu_sehat_kirim_medicationstatement=false,
            skrining_adiksi_nikotin=false,skrining_thalassemia=false,skrining_instrumen_sdq=false,skrining_instrumen_srq=false,checklist_pemberian_fibrinolitik=false,
            skrining_kanker_kolorektal=false,dapur_pemesanan=false,bayar_pesan_dapur=false,hutang_dapur=false,titip_faktur_dapur=false,validasi_tagihan_dapur=false,
            surat_pemesanan_dapur=false,pengajuan_barang_dapur=false,dapur_returbeli=false,hibah_dapur=false,ringkasan_penerimaan_dapur=false,ringkasan_pengajuan_dapur=false,
            ringkasan_pemesanan_dapur=false,ringkasan_returbeli_dapur=false,ringkasan_stokkeluar_dapur=false,dapur_stokkeluar_pertanggal=false,sirkulasi_dapur=false,
            sirkulasi_dapur2=false,verifikasi_penerimaan_dapur=false,nilai_penerimaan_vendor_dapur_perbulan=false,ringkasan_hutang_vendor_dapur=false,penilaian_psikologi_klinis=false,
            penilaian_awal_medis_ranap_neonatus=false,penilaian_derajat_dehidrasi=false,ringkasan_jasa_tindakan_medis=false,pendapatan_per_akun=false,hasil_pemeriksaan_echo=false,
            penilaian_bayi_baru_lahir=false,rl1_3_ketersediaan_kamar=false,pendapatan_per_akun_closing=false,pengeluaran_pengeluaran=false,skrining_diabetes_melitus=false,
            laporan_tindakan=false,pelaksanaan_informasi_edukasi=false,layanan_kedokteran_fisik_rehabilitasi=false,skrining_kesehatan_gigi_mulut_balita=false,skrining_anemia=false,
            layanan_program_kfr=false,skrining_hipertensi=false,skrining_kesehatan_penglihatan=false,catatan_observasi_hemodialisa=false,skrining_kesehatan_gigi_mulut_dewasa=false,
            skrining_risiko_kanker_serviks=false,catatan_cairan_hemodialisa=false,skrining_kesehatan_gigi_mulut_lansia=false,skrining_indra_pendengaran=false,
            catatan_pengkajian_paska_operasi=false,skrining_frailty_syndrome=false,sirkulasi_cssd=false,lama_pelayanan_cssd=false,catatan_observasi_bayi=false,
            riwayat_surat_peringatan=false,master_kesimpulan_anjuran_mcu=false,kategori_piutang_jasa_perusahaan=false,piutang_jasa_perusahaan=false,bayar_piutang_jasa_perusahaan=false,
            piutang_jasa_perusahaan_belum_lunas=false,checklist_kesiapan_anestesi=false,piutang_peminjaman_uang_belum_lunas=false,hasil_pemeriksaan_slit_lamp=false,
            hasil_pemeriksaan_oct=false,beban_hutang_lain=false,poli_asal_pasien_ranap=false,pemberi_hutang_lain=false,dokter_asal_pasien_ranap=false,duta_parkir_rekap_keluar=false,
            surat_keterangan_layak_terbang=false,bayar_beban_hutang_lain=false,surat_persetujuan_pemeriksaan_hiv=false,skrining_instrumen_acrs=false,surat_pernyataan_memilih_dpjp=false,
            skrining_instrumen_mental_emosional=false,pelanggan_lab_kesehatan_lingkungan=false,kriteria_masuk_nicu=false,kriteria_keluar_nicu=false,penilaian_medis_ranap_psikiatrik=false,
            kriteria_masuk_picu=false,kriteria_keluar_picu=false,master_sampel_bakumutu=false,skrining_instrumen_amt=false,parameter_pengujian_lab_kesehatan_lingkungan=false,
            nilai_normal_baku_mutu_lab_kesehatan_lingkungan=false,skrining_pneumonia_severity_index=false,permintaan_pengujian_sampel_lab_kesehatan_lingkungan=false,
            penilaian_awal_medis_ralan_jantung=false,penilaian_awal_medis_ralan_urologi=false,hasil_pemeriksaan_treadmill=false,hasil_pemeriksaan_echo_pediatrik=false,
            template_pelaksanaan_informasi_edukasi=false,skrining_instrumen_esat=false,penilaian_awal_medis_ranap_jantung=false,e_eksekutif=false,penugasan_pengujian_sampel_lab_kesehatan_lingkungan=false,
            hasil_pengujian_sampel_lab_kesehatan_lingkungan=false,verifikasi_pengujian_sampel_lab_kesehatan_lingkungan=false,validasi_pengujian_sampel_lab_kesehatan_lingkungan=false,
            rekap_pelayanan_lab_kesehatan_lingkungan=false,pembayaran_pengujian_sampel_lab_kesehatan_lingkungan=false,skrining_curb65=false,bpjs_potensi_prb=false,
            bpjs_riwayat_pelayanan_obat=false,skrining_gizi_kehamilan=false,bpjs_rekap_peserta_prb_apotek=false,serah_terima_anggota_tubuh_barang=false,pcra_icra_jenis_aktivitas_proyek=false,
            pcra_icra_lokasi_kelompok_risiko_area=false,pcra_icra_kelas_risiko_pencegahan=false,pcra_icra_tindakan_pengendalian=false,pcra_icra_identifkasi_risiko_infeksi=false,
            pcra_icra_identifkasi_risiko_keselamatan=false,pcra_icra_identifkasi_risiko_kebakaran=false,pcra_icra_identifkasi_risiko_utilitas=false,bpjs_daftar_resep_apotek=false,
            daftar_permintaan_resep_iterasi_bpjs=false,pcra_icra_pengkajian_risiko_prakonstruksi=false,pcra_icra_persyaratan_harus_dipenuhi=false,satu_sehat_kirim_questionresponse_telaah_farmasi=false,
            satu_sehat_kirim_allergy_intolerance=false,konsultasi_perawat=false,jawaban_konsultasi_perawat=false,bridging_smart_klaim_bpjs=false,mapping_prosedur_smart_klaim_bpjs=false,
            mapping_penyakit_smart_klaim_bpjs=false,permintaan_binrohtal=false,surat_permintaan_perlindungan_dari_kekerasan=false,surat_permohonan_privasi=false,surat_permintaan_second_opinion=false,
            surat_keterangan_berobat=false,surat_penolakan_resusitasi=false,catatan_observasi_ruang_ok=false,hasil_pemeriksaan_usg_abdomen=false,intervensi_nyeri_farmakologi=false;

    private static boolean edit_hapus_spo_medis = false,
        edit_hapus_spo_nonmedis = false,
        bpjs_kompilasi_berkas_klaim = false,
        pindah_kamar_pilihan_2 = false,
        bpjs_kirim_obat_smc = false,
        bpjs_edit_kirim_obat_smc = false,
        bpjs_riwayat_obat_smc = false,
        bpjs_riwayat_pelayanan_resep_smc = false,
        set_pintu_poli = false,
        pintu_poli = false,
        bpjs_riwayat_surat_smc = false;

    private static final Set<String> columns = new LinkedHashSet();

    public static void setData(String user, String pass){
        int retries=2;
        do{
            try(
                PreparedStatement ps=koneksi.prepareStatement("select * from admin where admin.usere=AES_ENCRYPT(?,'nur') and admin.passworde=AES_ENCRYPT(?,'windi')");
                PreparedStatement ps2=koneksi.prepareStatement("select * from user where user.id_user=AES_ENCRYPT(?,'nur') and user.password=AES_ENCRYPT(?,'windi')")
            ){
                ps.setString(1,user);
                ps.setString(2,pass);
                ps2.setString(1,user);
                ps2.setString(2,pass);
                try(
                    ResultSet rs=ps.executeQuery();
                    ResultSet rs2=ps2.executeQuery()
                ){
                    rs.last();
                    rs2.last();
                    akses.jml1=rs.getRow();
                    akses.jml2=rs2.getRow();
                    if(rs.getRow()>=1){
                        akses.setAdminUtama("Admin Utama", true);
                        break;
                    }else if(rs2.getRow()>=1){
                        rs2.beforeFirst();
                        rs2.next();
                        if (columns.isEmpty()) {
                            ResultSetMetaData md = rs2.getMetaData();
                            for (int i = 1; i < md.getColumnCount(); i++) {
                                if (md.getColumnLabel(i).equalsIgnoreCase("id_user") || md.getColumnLabel(i).equalsIgnoreCase("password")) {
                                    continue;
                                }
                                columns.add(md.getColumnLabel(i));
                            }
                        }
                        akses.kode=user;
                        akses.penyakit=akses.getBoolean(rs2, "penyakit");
                        akses.obat_penyakit=akses.getBoolean(rs2, "obat_penyakit");
                        akses.dokter=akses.getBoolean(rs2, "dokter");
                        akses.jadwal_praktek=akses.getBoolean(rs2, "jadwal_praktek");
                        akses.petugas=akses.getBoolean(rs2, "petugas");
                        akses.pasien=akses.getBoolean(rs2, "pasien");
                        akses.registrasi=akses.getBoolean(rs2, "registrasi");
                        akses.tindakan_ralan=akses.getBoolean(rs2, "tindakan_ralan");
                        akses.kamar_inap=akses.getBoolean(rs2, "kamar_inap");
                        akses.tindakan_ranap=akses.getBoolean(rs2, "tindakan_ranap");
                        akses.operasi=akses.getBoolean(rs2, "operasi");
                        akses.rujukan_keluar=akses.getBoolean(rs2, "rujukan_keluar");
                        akses.rujukan_masuk=akses.getBoolean(rs2, "rujukan_masuk");
                        akses.beri_obat=akses.getBoolean(rs2, "beri_obat");
                        akses.resep_pulang=akses.getBoolean(rs2, "resep_pulang");
                        akses.pasien_meninggal=akses.getBoolean(rs2, "pasien_meninggal");
                        akses.diet_pasien=akses.getBoolean(rs2, "diet_pasien");
                        akses.kelahiran_bayi=akses.getBoolean(rs2, "kelahiran_bayi");
                        akses.periksa_lab=akses.getBoolean(rs2, "periksa_lab");
                        akses.periksa_radiologi=akses.getBoolean(rs2, "periksa_radiologi");
                        akses.kasir_ralan=akses.getBoolean(rs2, "kasir_ralan");
                        akses.deposit_pasien=akses.getBoolean(rs2, "deposit_pasien");
                        akses.piutang_pasien=akses.getBoolean(rs2, "piutang_pasien");
                        akses.peminjaman_berkas=akses.getBoolean(rs2, "peminjaman_berkas");
                        akses.barcode=akses.getBoolean(rs2, "barcode");
                        akses.presensi_harian=akses.getBoolean(rs2, "presensi_harian");
                        akses.presensi_bulanan=akses.getBoolean(rs2, "presensi_bulanan");
                        akses.pegawai_admin=akses.getBoolean(rs2, "pegawai_admin");
                        akses.pegawai_user=akses.getBoolean(rs2, "pegawai_user");
                        akses.suplier=akses.getBoolean(rs2, "suplier");
                        akses.satuan_barang=akses.getBoolean(rs2, "satuan_barang");
                        akses.konversi_satuan=akses.getBoolean(rs2, "konversi_satuan");
                        akses.jenis_barang=akses.getBoolean(rs2, "jenis_barang");
                        akses.obat=akses.getBoolean(rs2, "obat");
                        akses.stok_opname_obat=akses.getBoolean(rs2, "stok_opname_obat");
                        akses.stok_obat_pasien=akses.getBoolean(rs2, "stok_obat_pasien");
                        akses.pengadaan_obat=akses.getBoolean(rs2, "pengadaan_obat");
                        akses.pemesanan_obat=akses.getBoolean(rs2, "pemesanan_obat");
                        akses.penjualan_obat=akses.getBoolean(rs2, "penjualan_obat");
                        akses.piutang_obat=akses.getBoolean(rs2, "piutang_obat");
                        akses.retur_ke_suplier=akses.getBoolean(rs2, "retur_ke_suplier");
                        akses.retur_dari_pembeli=akses.getBoolean(rs2, "retur_dari_pembeli");
                        akses.retur_obat_ranap=akses.getBoolean(rs2, "retur_obat_ranap");
                        akses.retur_piutang_pasien=akses.getBoolean(rs2, "retur_piutang_pasien");
                        akses.keuntungan_penjualan=akses.getBoolean(rs2, "keuntungan_penjualan");
                        akses.keuntungan_beri_obat=akses.getBoolean(rs2, "keuntungan_beri_obat");
                        akses.sirkulasi_obat=akses.getBoolean(rs2, "sirkulasi_obat");
                        akses.ipsrs_barang=akses.getBoolean(rs2, "ipsrs_barang");
                        akses.ipsrs_pengadaan_barang=akses.getBoolean(rs2, "ipsrs_pengadaan_barang");
                        akses.ipsrs_stok_keluar=akses.getBoolean(rs2, "ipsrs_stok_keluar");
                        akses.ipsrs_jenis_barang=akses.getBoolean(rs2, "ipsrs_jenis_barang");
                        akses.ipsrs_rekap_pengadaan=akses.getBoolean(rs2, "ipsrs_rekap_pengadaan");
                        akses.ipsrs_rekap_stok_keluar=akses.getBoolean(rs2, "ipsrs_rekap_stok_keluar");
                        akses.ipsrs_pengeluaran_harian=akses.getBoolean(rs2, "ipsrs_pengeluaran_harian");
                        akses.inventaris_jenis=akses.getBoolean(rs2, "inventaris_jenis");
                        akses.inventaris_kategori=akses.getBoolean(rs2, "inventaris_kategori");
                        akses.inventaris_merk=akses.getBoolean(rs2, "inventaris_merk");
                        akses.inventaris_ruang=akses.getBoolean(rs2, "inventaris_ruang");
                        akses.inventaris_produsen=akses.getBoolean(rs2, "inventaris_produsen");
                        akses.inventaris_koleksi=akses.getBoolean(rs2, "inventaris_koleksi");
                        akses.inventaris_inventaris=akses.getBoolean(rs2, "inventaris_inventaris");
                        akses.inventaris_sirkulasi=akses.getBoolean(rs2, "inventaris_sirkulasi");
                        akses.parkir_jenis=akses.getBoolean(rs2, "parkir_jenis");
                        akses.parkir_in=akses.getBoolean(rs2, "parkir_in");
                        akses.parkir_out=akses.getBoolean(rs2, "parkir_out");
                        akses.parkir_rekap_harian=akses.getBoolean(rs2, "parkir_rekap_harian");
                        akses.parkir_rekap_bulanan=akses.getBoolean(rs2, "parkir_rekap_bulanan");
                        akses.informasi_kamar=akses.getBoolean(rs2, "informasi_kamar");
                        akses.harian_tindakan_poli=akses.getBoolean(rs2, "harian_tindakan_poli");
                        akses.obat_per_poli=akses.getBoolean(rs2, "obat_per_poli");
                        akses.obat_per_kamar=akses.getBoolean(rs2, "obat_per_kamar");
                        akses.obat_per_dokter_ralan=akses.getBoolean(rs2, "obat_per_dokter_ralan");
                        akses.obat_per_dokter_ranap=akses.getBoolean(rs2, "obat_per_dokter_ranap");
                        akses.harian_dokter=akses.getBoolean(rs2, "harian_dokter");
                        akses.bulanan_dokter=akses.getBoolean(rs2, "bulanan_dokter");
                        akses.harian_paramedis=akses.getBoolean(rs2, "harian_paramedis");
                        akses.bulanan_paramedis=akses.getBoolean(rs2, "bulanan_paramedis");
                        akses.pembayaran_ralan=akses.getBoolean(rs2, "pembayaran_ralan");
                        akses.pembayaran_ranap=akses.getBoolean(rs2, "pembayaran_ranap");
                        akses.rekap_pembayaran_ralan=akses.getBoolean(rs2, "rekap_pembayaran_ralan");
                        akses.rekap_pembayaran_ranap=akses.getBoolean(rs2, "rekap_pembayaran_ranap");
                        akses.tagihan_masuk=akses.getBoolean(rs2, "tagihan_masuk");
                        akses.tambahan_biaya=akses.getBoolean(rs2, "tambahan_biaya");
                        akses.potongan_biaya=akses.getBoolean(rs2, "potongan_biaya");
                        akses.resep_obat=akses.getBoolean(rs2, "resep_obat");
                        akses.resume_pasien=akses.getBoolean(rs2, "resume_pasien");
                        akses.penyakit_ralan=akses.getBoolean(rs2, "penyakit_ralan");
                        akses.penyakit_ranap=akses.getBoolean(rs2, "penyakit_ranap");
                        akses.kamar=akses.getBoolean(rs2, "kamar");
                        akses.tarif_ralan=akses.getBoolean(rs2, "tarif_ralan");
                        akses.tarif_ranap=akses.getBoolean(rs2, "tarif_ranap");
                        akses.tarif_lab=akses.getBoolean(rs2, "tarif_lab");
                        akses.tarif_radiologi=akses.getBoolean(rs2, "tarif_radiologi");
                        akses.tarif_operasi=akses.getBoolean(rs2, "tarif_operasi");
                        akses.akun_rekening=akses.getBoolean(rs2, "akun_rekening");
                        akses.rekening_tahun=akses.getBoolean(rs2, "rekening_tahun");
                        akses.posting_jurnal=akses.getBoolean(rs2, "posting_jurnal");
                        akses.buku_besar=akses.getBoolean(rs2, "buku_besar");
                        akses.cashflow=akses.getBoolean(rs2, "cashflow");
                        akses.keuangan=akses.getBoolean(rs2, "keuangan");
                        akses.pengeluaran=akses.getBoolean(rs2, "pengeluaran");
                        akses.setup_pjlab=akses.getBoolean(rs2, "setup_pjlab");
                        akses.setup_otolokasi=akses.getBoolean(rs2, "setup_otolokasi");
                        akses.setup_jam_kamin=akses.getBoolean(rs2, "setup_jam_kamin");
                        akses.setup_embalase=akses.getBoolean(rs2, "setup_embalase");
                        akses.tracer_login=akses.getBoolean(rs2, "tracer_login");
                        akses.display=akses.getBoolean(rs2, "display");
                        akses.set_harga_obat=akses.getBoolean(rs2, "set_harga_obat");
                        akses.set_penggunaan_tarif=akses.getBoolean(rs2, "set_penggunaan_tarif");
                        akses.set_oto_ralan=akses.getBoolean(rs2, "set_oto_ralan");
                        akses.biaya_harian=akses.getBoolean(rs2, "biaya_harian");
                        akses.biaya_masuk_sekali=akses.getBoolean(rs2, "biaya_masuk_sekali");
                        akses.set_no_rm=akses.getBoolean(rs2, "set_no_rm");
                        akses.billing_ralan=akses.getBoolean(rs2, "billing_ralan");
                        akses.billing_ranap=akses.getBoolean(rs2, "billing_ranap");
                        akses.jm_ranap_dokter=akses.getBoolean(rs2, "jm_ranap_dokter");
                        akses.igd=akses.getBoolean(rs2, "igd");
                        akses.barcoderalan=akses.getBoolean(rs2, "barcoderalan");
                        akses.barcoderanap=akses.getBoolean(rs2, "barcoderanap");
                        akses.set_harga_obat_ralan=akses.getBoolean(rs2, "set_harga_obat_ralan");
                        akses.set_harga_obat_ranap=akses.getBoolean(rs2, "set_harga_obat_ranap");
                        akses.penyakit_pd3i=akses.getBoolean(rs2, "penyakit_pd3i");
                        akses.surveilans_pd3i=akses.getBoolean(rs2, "surveilans_pd3i");
                        akses.surveilans_ralan=akses.getBoolean(rs2, "surveilans_ralan");
                        akses.diagnosa_pasien=akses.getBoolean(rs2, "diagnosa_pasien");
                        akses.surveilans_ranap=akses.getBoolean(rs2, "surveilans_ranap");
                        akses.admin=false;
                        akses.user=false;
                        akses.e_eksekutif=false;
                        akses.vakum=false;
                        akses.aplikasi=false;
                        akses.pny_takmenular_ranap=akses.getBoolean(rs2, "pny_takmenular_ranap");
                        akses.pny_takmenular_ralan=akses.getBoolean(rs2, "pny_takmenular_ralan");
                        akses.kunjungan_ralan=akses.getBoolean(rs2, "kunjungan_ralan");
                        akses.rl32=akses.getBoolean(rs2, "rl32");
                        akses.rl33=akses.getBoolean(rs2, "rl33");
                        akses.rl37=akses.getBoolean(rs2, "rl37");
                        akses.rl38=akses.getBoolean(rs2, "rl38");
                        akses.harian_tindakan_dokter=akses.getBoolean(rs2, "harian_tindakan_dokter");
                        akses.sms=akses.getBoolean(rs2, "sms");
                        akses.sidikjari=akses.getBoolean(rs2, "sidikjari");
                        akses.jam_masuk=akses.getBoolean(rs2, "jam_masuk");
                        akses.jadwal_pegawai=akses.getBoolean(rs2, "jadwal_pegawai");
                        akses.parkir_barcode=akses.getBoolean(rs2, "parkir_barcode");
                        akses.set_nota=akses.getBoolean(rs2, "set_nota");
                        akses.dpjp_ranap=akses.getBoolean(rs2, "dpjp_ranap");
                        akses.mutasi_barang=akses.getBoolean(rs2, "mutasi_barang");
                        akses.rl34=akses.getBoolean(rs2, "rl34");
                        akses.rl36=akses.getBoolean(rs2, "rl36");
                        akses.fee_visit_dokter=akses.getBoolean(rs2, "fee_visit_dokter");
                        akses.fee_bacaan_ekg=akses.getBoolean(rs2, "fee_bacaan_ekg");
                        akses.fee_rujukan_rontgen=akses.getBoolean(rs2, "fee_rujukan_rontgen");
                        akses.fee_rujukan_ranap=akses.getBoolean(rs2, "fee_rujukan_ranap");
                        akses.fee_ralan=akses.getBoolean(rs2, "fee_ralan");
                        akses.akun_bayar=akses.getBoolean(rs2, "akun_bayar");
                        akses.bayar_pemesanan_obat=akses.getBoolean(rs2, "bayar_pemesanan_obat");
                        akses.obat_per_dokter_peresep=akses.getBoolean(rs2, "obat_per_dokter_peresep");
                        akses.pemasukan_lain=akses.getBoolean(rs2, "pemasukan_lain");
                        akses.pengaturan_rekening=akses.getBoolean(rs2, "pengaturan_rekening");
                        akses.closing_kasir=akses.getBoolean(rs2, "closing_kasir");
                        akses.keterlambatan_presensi=akses.getBoolean(rs2, "keterlambatan_presensi");
                        akses.set_harga_kamar=akses.getBoolean(rs2, "set_harga_kamar");
                        akses.rekap_per_shift=akses.getBoolean(rs2, "rekap_per_shift");
                        akses.bpjs_cek_nik=akses.getBoolean(rs2, "bpjs_cek_nik");
                        akses.bpjs_cek_kartu=akses.getBoolean(rs2, "bpjs_cek_kartu");
                        akses.bpjs_cek_riwayat=akses.getBoolean(rs2, "bpjs_cek_riwayat");
                        akses.obat_per_cara_bayar=akses.getBoolean(rs2, "obat_per_cara_bayar");
                        akses.kunjungan_ranap=akses.getBoolean(rs2, "kunjungan_ranap");
                        akses.bayar_piutang=akses.getBoolean(rs2, "bayar_piutang");
                        akses.payment_point=akses.getBoolean(rs2, "payment_point");
                        akses.bpjs_cek_nomor_rujukan=akses.getBoolean(rs2, "bpjs_cek_nomor_rujukan");
                        akses.icd9=akses.getBoolean(rs2, "icd9");
                        akses.darurat_stok=akses.getBoolean(rs2, "darurat_stok");
                        akses.retensi_rm=akses.getBoolean(rs2, "retensi_rm");
                        akses.temporary_presensi=akses.getBoolean(rs2, "temporary_presensi");
                        akses.jurnal_harian=akses.getBoolean(rs2, "jurnal_harian");
                        akses.sirkulasi_obat2=akses.getBoolean(rs2, "sirkulasi_obat2");
                        akses.edit_registrasi=akses.getBoolean(rs2, "edit_registrasi");
                        akses.bpjs_referensi_diagnosa=akses.getBoolean(rs2, "bpjs_referensi_diagnosa");
                        akses.bpjs_referensi_poli=akses.getBoolean(rs2, "bpjs_referensi_poli");
                        akses.industrifarmasi=akses.getBoolean(rs2, "industrifarmasi");
                        akses.harian_js=akses.getBoolean(rs2, "harian_js");
                        akses.bulanan_js=akses.getBoolean(rs2, "bulanan_js");
                        akses.harian_paket_bhp=akses.getBoolean(rs2, "harian_paket_bhp");
                        akses.bulanan_paket_bhp=akses.getBoolean(rs2, "bulanan_paket_bhp");
                        akses.piutang_pasien2=akses.getBoolean(rs2, "piutang_pasien2");
                        akses.bpjs_referensi_faskes=akses.getBoolean(rs2, "bpjs_referensi_faskes");
                        akses.bpjs_sep=akses.getBoolean(rs2, "bpjs_sep");
                        akses.pengambilan_utd=akses.getBoolean(rs2, "pengambilan_utd");
                        akses.tarif_utd=akses.getBoolean(rs2, "tarif_utd");
                        akses.pengambilan_utd2=akses.getBoolean(rs2, "pengambilan_utd2");
                        akses.utd_medis_rusak=akses.getBoolean(rs2, "utd_medis_rusak");
                        akses.pengambilan_penunjang_utd=akses.getBoolean(rs2, "pengambilan_penunjang_utd");
                        akses.pengambilan_penunjang_utd2=akses.getBoolean(rs2, "pengambilan_penunjang_utd2");
                        akses.utd_penunjang_rusak=akses.getBoolean(rs2, "utd_penunjang_rusak");
                        akses.suplier_penunjang=akses.getBoolean(rs2, "suplier_penunjang");
                        akses.utd_donor=akses.getBoolean(rs2, "utd_donor");
                        akses.bpjs_monitoring_klaim=akses.getBoolean(rs2, "bpjs_monitoring_klaim");
                        akses.utd_cekal_darah=akses.getBoolean(rs2, "utd_cekal_darah");
                        akses.utd_komponen_darah=akses.getBoolean(rs2, "utd_komponen_darah");
                        akses.utd_stok_darah=akses.getBoolean(rs2, "utd_stok_darah");
                        akses.utd_pemisahan_darah=akses.getBoolean(rs2, "utd_pemisahan_darah");
                        akses.harian_kamar=akses.getBoolean(rs2, "harian_kamar");
                        akses.rincian_piutang_pasien=akses.getBoolean(rs2, "rincian_piutang_pasien");
                        akses.keuntungan_beri_obat_nonpiutang=akses.getBoolean(rs2, "keuntungan_beri_obat_nonpiutang");
                        akses.reklasifikasi_ralan=akses.getBoolean(rs2, "reklasifikasi_ralan");
                        akses.reklasifikasi_ranap=akses.getBoolean(rs2, "reklasifikasi_ranap");
                        akses.utd_penyerahan_darah=akses.getBoolean(rs2, "utd_penyerahan_darah");
                        akses.hutang_obat=akses.getBoolean(rs2, "hutang_obat");
                        akses.riwayat_obat_alkes_bhp=akses.getBoolean(rs2, "riwayat_obat_alkes_bhp");
                        akses.sensus_harian_poli=akses.getBoolean(rs2, "sensus_harian_poli");
                        akses.rl4a=akses.getBoolean(rs2, "rl4a");
                        akses.aplicare_referensi_kamar=akses.getBoolean(rs2, "aplicare_referensi_kamar");
                        akses.aplicare_ketersediaan_kamar=akses.getBoolean(rs2, "aplicare_ketersediaan_kamar");
                        akses.inacbg_klaim_baru_otomatis=akses.getBoolean(rs2, "inacbg_klaim_baru_otomatis");
                        akses.inacbg_klaim_baru_manual=akses.getBoolean(rs2, "inacbg_klaim_baru_manual");
                        akses.inacbg_coder_nik=akses.getBoolean(rs2, "inacbg_coder_nik");
                        akses.mutasi_berkas=akses.getBoolean(rs2, "mutasi_berkas");
                        akses.akun_piutang=akses.getBoolean(rs2, "akun_piutang");
                        akses.harian_kso=akses.getBoolean(rs2, "harian_kso");
                        akses.bulanan_kso=akses.getBoolean(rs2, "bulanan_kso");
                        akses.harian_menejemen=akses.getBoolean(rs2, "harian_menejemen");
                        akses.bulanan_menejemen=akses.getBoolean(rs2, "bulanan_menejemen");
                        akses.inhealth_cek_eligibilitas=akses.getBoolean(rs2, "inhealth_cek_eligibilitas");
                        akses.inhealth_referensi_jenpel_ruang_rawat=akses.getBoolean(rs2, "inhealth_referensi_jenpel_ruang_rawat");
                        akses.inhealth_referensi_poli=akses.getBoolean(rs2, "inhealth_referensi_poli");
                        akses.inhealth_referensi_faskes=akses.getBoolean(rs2, "inhealth_referensi_faskes");
                        akses.inhealth_sjp=akses.getBoolean(rs2, "inhealth_sjp");
                        akses.piutang_ralan=akses.getBoolean(rs2, "piutang_ralan");
                        akses.piutang_ranap=akses.getBoolean(rs2, "piutang_ranap");
                        akses.detail_piutang_penjab=akses.getBoolean(rs2, "detail_piutang_penjab");
                        akses.lama_pelayanan_ralan=akses.getBoolean(rs2, "lama_pelayanan_ralan");
                        akses.catatan_pasien=akses.getBoolean(rs2, "catatan_pasien");
                        akses.rl4b=akses.getBoolean(rs2, "rl4b");
                        akses.rl4asebab=akses.getBoolean(rs2, "rl4asebab");
                        akses.rl4bsebab=akses.getBoolean(rs2, "rl4bsebab");
                        akses.data_HAIs=akses.getBoolean(rs2, "data_HAIs");
                        akses.harian_HAIs=akses.getBoolean(rs2, "harian_HAIs");
                        akses.bulanan_HAIs=akses.getBoolean(rs2, "bulanan_HAIs");
                        akses.hitung_bor=akses.getBoolean(rs2, "hitung_bor");
                        akses.perusahaan_pasien=akses.getBoolean(rs2, "perusahaan_pasien");
                        akses.resep_dokter=akses.getBoolean(rs2, "resep_dokter");
                        akses.lama_pelayanan_apotek=akses.getBoolean(rs2, "lama_pelayanan_apotek");
                        akses.hitung_alos=akses.getBoolean(rs2, "hitung_alos");
                        akses.detail_tindakan=akses.getBoolean(rs2, "detail_tindakan");
                        akses.rujukan_poli_internal=akses.getBoolean(rs2, "rujukan_poli_internal");
                        akses.rekap_poli_anak=akses.getBoolean(rs2, "rekap_poli_anak");
                        akses.grafik_kunjungan_poli=akses.getBoolean(rs2, "grafik_kunjungan_poli");
                        akses.grafik_kunjungan_perdokter=akses.getBoolean(rs2, "grafik_kunjungan_perdokter");
                        akses.grafik_kunjungan_perpekerjaan=akses.getBoolean(rs2, "grafik_kunjungan_perpekerjaan");
                        akses.grafik_kunjungan_perpendidikan=akses.getBoolean(rs2, "grafik_kunjungan_perpendidikan");
                        akses.grafik_kunjungan_pertahun=akses.getBoolean(rs2, "grafik_kunjungan_pertahun");
                        akses.berkas_digital_perawatan=akses.getBoolean(rs2, "berkas_digital_perawatan");
                        akses.penyakit_menular_ranap=akses.getBoolean(rs2, "penyakit_menular_ranap");
                        akses.penyakit_menular_ralan=akses.getBoolean(rs2, "penyakit_menular_ralan");
                        akses.grafik_kunjungan_perbulan=akses.getBoolean(rs2, "grafik_kunjungan_perbulan");
                        akses.grafik_kunjungan_pertanggal=akses.getBoolean(rs2, "grafik_kunjungan_pertanggal");
                        akses.grafik_kunjungan_demografi=akses.getBoolean(rs2, "grafik_kunjungan_demografi");
                        akses.grafik_kunjungan_statusdaftartahun=akses.getBoolean(rs2, "grafik_kunjungan_statusdaftartahun");
                        akses.grafik_kunjungan_statusdaftartahun2=akses.getBoolean(rs2, "grafik_kunjungan_statusdaftartahun2");
                        akses.grafik_kunjungan_statusdaftarbulan=akses.getBoolean(rs2, "grafik_kunjungan_statusdaftarbulan");
                        akses.grafik_kunjungan_statusdaftarbulan2=akses.getBoolean(rs2, "grafik_kunjungan_statusdaftarbulan2");
                        akses.grafik_kunjungan_statusdaftartanggal=akses.getBoolean(rs2, "grafik_kunjungan_statusdaftartanggal");
                        akses.grafik_kunjungan_statusdaftartanggal2=akses.getBoolean(rs2, "grafik_kunjungan_statusdaftartanggal2");
                        akses.grafik_kunjungan_statusbataltahun=akses.getBoolean(rs2, "grafik_kunjungan_statusbataltahun");
                        akses.grafik_kunjungan_statusbatalbulan=akses.getBoolean(rs2, "grafik_kunjungan_statusbatalbulan");
                        akses.pcare_cek_penyakit=akses.getBoolean(rs2, "pcare_cek_penyakit");
                        akses.grafik_kunjungan_statusbataltanggal=akses.getBoolean(rs2, "grafik_kunjungan_statusbataltanggal");
                        akses.kategori_barang=akses.getBoolean(rs2, "kategori_barang");
                        akses.golongan_barang=akses.getBoolean(rs2, "golongan_barang");
                        akses.pemberian_obat_pertanggal=akses.getBoolean(rs2, "pemberian_obat_pertanggal");
                        akses.penjualan_obat_pertanggal=akses.getBoolean(rs2, "penjualan_obat_pertanggal");
                        akses.pcare_cek_kesadaran=akses.getBoolean(rs2, "pcare_cek_kesadaran");
                        akses.pembatalan_periksa_dokter=akses.getBoolean(rs2, "pembatalan_periksa_dokter");
                        akses.pembayaran_per_unit=akses.getBoolean(rs2, "pembayaran_per_unit");
                        akses.rekap_pembayaran_per_unit=akses.getBoolean(rs2, "rekap_pembayaran_per_unit");
                        akses.grafik_kunjungan_percarabayar=akses.getBoolean(rs2, "grafik_kunjungan_percarabayar");
                        akses.ipsrs_pengadaan_pertanggal=akses.getBoolean(rs2, "ipsrs_pengadaan_pertanggal");
                        akses.ipsrs_stokkeluar_pertanggal=akses.getBoolean(rs2, "ipsrs_stokkeluar_pertanggal");
                        akses.grafik_kunjungan_ranaptahun=akses.getBoolean(rs2, "grafik_kunjungan_ranaptahun");
                        akses.pcare_cek_rujukan=akses.getBoolean(rs2, "pcare_cek_rujukan");
                        akses.grafik_lab_ralantahun=akses.getBoolean(rs2, "grafik_lab_ralantahun");
                        akses.grafik_rad_ralantahun=akses.getBoolean(rs2, "grafik_rad_ralantahun");
                        akses.cek_entry_ralan=akses.getBoolean(rs2, "cek_entry_ralan");
                        akses.inacbg_klaim_baru_manual2=akses.getBoolean(rs2, "inacbg_klaim_baru_manual2");
                        akses.permintaan_medis=akses.getBoolean(rs2, "permintaan_medis");
                        akses.rekap_permintaan_medis=akses.getBoolean(rs2, "rekap_permintaan_medis");
                        akses.surat_pemesanan_medis=akses.getBoolean(rs2, "surat_pemesanan_medis");
                        akses.permintaan_non_medis=akses.getBoolean(rs2, "permintaan_non_medis");
                        akses.rekap_permintaan_non_medis=akses.getBoolean(rs2, "rekap_permintaan_non_medis");
                        akses.surat_pemesanan_non_medis=akses.getBoolean(rs2, "surat_pemesanan_non_medis");
                        akses.grafik_per_perujuk=akses.getBoolean(rs2, "grafik_per_perujuk");
                        akses.bpjs_cek_prosedur=akses.getBoolean(rs2, "bpjs_cek_prosedur");
                        akses.bpjs_cek_kelas_rawat=akses.getBoolean(rs2, "bpjs_cek_kelas_rawat");
                        akses.bpjs_cek_dokter=akses.getBoolean(rs2, "bpjs_cek_dokter");
                        akses.bpjs_cek_spesialistik=akses.getBoolean(rs2, "bpjs_cek_spesialistik");
                        akses.bpjs_cek_ruangrawat=akses.getBoolean(rs2, "bpjs_cek_ruangrawat");
                        akses.bpjs_cek_carakeluar=akses.getBoolean(rs2, "bpjs_cek_carakeluar");
                        akses.bpjs_cek_pasca_pulang=akses.getBoolean(rs2, "bpjs_cek_pasca_pulang");
                        akses.detail_tindakan_okvk=akses.getBoolean(rs2, "detail_tindakan_okvk");
                        akses.billing_parsial=akses.getBoolean(rs2, "billing_parsial");
                        akses.bpjs_cek_nomor_rujukan_rs=akses.getBoolean(rs2, "bpjs_cek_nomor_rujukan_rs");
                        akses.bpjs_cek_rujukan_kartu_pcare=akses.getBoolean(rs2, "bpjs_cek_rujukan_kartu_pcare");
                        akses.bpjs_cek_rujukan_kartu_rs=akses.getBoolean(rs2, "bpjs_cek_rujukan_kartu_rs");
                        akses.akses_depo_obat=akses.getBoolean(rs2, "akses_depo_obat");
                        akses.bpjs_rujukan_keluar=akses.getBoolean(rs2, "bpjs_rujukan_keluar");
                        akses.grafik_lab_ralanbulan=akses.getBoolean(rs2, "grafik_lab_ralanbulan");
                        akses.pengeluaran_stok_apotek=akses.getBoolean(rs2, "pengeluaran_stok_apotek");
                        akses.grafik_rad_ralanbulan=akses.getBoolean(rs2, "grafik_rad_ralanbulan");
                        akses.detailjmdokter2=akses.getBoolean(rs2, "detailjmdokter2");
                        akses.pengaduan_pasien=akses.getBoolean(rs2, "pengaduan_pasien");
                        akses.grafik_lab_ralanhari=akses.getBoolean(rs2, "grafik_lab_ralanhari");
                        akses.grafik_rad_ralanhari=akses.getBoolean(rs2, "grafik_rad_ralanhari");
                        akses.sensus_harian_ralan=akses.getBoolean(rs2, "sensus_harian_ralan");
                        akses.metode_racik=akses.getBoolean(rs2, "metode_racik");
                        akses.pembayaran_akun_bayar=akses.getBoolean(rs2, "pembayaran_akun_bayar");
                        akses.pengguna_obat_resep=akses.getBoolean(rs2, "pengguna_obat_resep");
                        akses.rekap_pemesanan=akses.getBoolean(rs2, "rekap_pemesanan");
                        akses.master_berkas_pegawai=akses.getBoolean(rs2, "master_berkas_pegawai");
                        akses.berkas_kepegawaian=akses.getBoolean(rs2, "berkas_kepegawaian");
                        akses.riwayat_jabatan=akses.getBoolean(rs2, "riwayat_jabatan");
                        akses.riwayat_pendidikan=akses.getBoolean(rs2, "riwayat_pendidikan");
                        akses.riwayat_naik_gaji=akses.getBoolean(rs2, "riwayat_naik_gaji");
                        akses.kegiatan_ilmiah=akses.getBoolean(rs2, "kegiatan_ilmiah");
                        akses.riwayat_penghargaan=akses.getBoolean(rs2, "riwayat_penghargaan");
                        akses.riwayat_penelitian=akses.getBoolean(rs2, "riwayat_penelitian");
                        akses.penerimaan_non_medis=akses.getBoolean(rs2, "penerimaan_non_medis");
                        akses.bayar_pesan_non_medis=akses.getBoolean(rs2, "bayar_pesan_non_medis");
                        akses.hutang_barang_non_medis=akses.getBoolean(rs2, "hutang_barang_non_medis");
                        akses.rekap_pemesanan_non_medis=akses.getBoolean(rs2, "rekap_pemesanan_non_medis");
                        akses.insiden_keselamatan=akses.getBoolean(rs2, "insiden_keselamatan");
                        akses.insiden_keselamatan_pasien=akses.getBoolean(rs2, "insiden_keselamatan_pasien");
                        akses.grafik_ikp_pertahun=akses.getBoolean(rs2, "grafik_ikp_pertahun");
                        akses.grafik_ikp_perbulan=akses.getBoolean(rs2, "grafik_ikp_perbulan");
                        akses.grafik_ikp_pertanggal=akses.getBoolean(rs2, "grafik_ikp_pertanggal");
                        akses.riwayat_data_batch=akses.getBoolean(rs2, "riwayat_data_batch");
                        akses.grafik_ikp_jenis=akses.getBoolean(rs2, "grafik_ikp_jenis");
                        akses.grafik_ikp_dampak=akses.getBoolean(rs2, "grafik_ikp_dampak");
                        akses.piutang_akun_piutang=akses.getBoolean(rs2, "piutang_akun_piutang");
                        akses.grafik_kunjungan_per_agama=akses.getBoolean(rs2, "grafik_kunjungan_per_agama");
                        akses.grafik_kunjungan_per_umur=akses.getBoolean(rs2, "grafik_kunjungan_per_umur");
                        akses.suku_bangsa=akses.getBoolean(rs2, "suku_bangsa");
                        akses.bahasa_pasien=akses.getBoolean(rs2, "bahasa_pasien");
                        akses.golongan_tni=akses.getBoolean(rs2, "golongan_tni");
                        akses.satuan_tni=akses.getBoolean(rs2, "satuan_tni");
                        akses.jabatan_tni=akses.getBoolean(rs2, "jabatan_tni");
                        akses.pangkat_tni=akses.getBoolean(rs2, "pangkat_tni");
                        akses.golongan_polri=akses.getBoolean(rs2, "golongan_polri");
                        akses.satuan_polri=akses.getBoolean(rs2, "satuan_polri");
                        akses.jabatan_polri=akses.getBoolean(rs2, "jabatan_polri");
                        akses.pangkat_polri=akses.getBoolean(rs2, "pangkat_polri");
                        akses.cacat_fisik=akses.getBoolean(rs2, "cacat_fisik");
                        akses.grafik_kunjungan_suku=akses.getBoolean(rs2, "grafik_kunjungan_suku");
                        akses.grafik_kunjungan_bahasa=akses.getBoolean(rs2, "grafik_kunjungan_bahasa");
                        akses.booking_operasi=akses.getBoolean(rs2, "booking_operasi");
                        akses.mapping_poli_bpjs=akses.getBoolean(rs2, "mapping_poli_bpjs");
                        akses.grafik_kunjungan_per_cacat=akses.getBoolean(rs2, "grafik_kunjungan_per_cacat");
                        akses.barang_cssd=akses.getBoolean(rs2, "barang_cssd");
                        akses.skdp_bpjs=akses.getBoolean(rs2, "skdp_bpjs");
                        akses.booking_registrasi=akses.getBoolean(rs2, "booking_registrasi");
                        akses.bpjs_cek_propinsi=akses.getBoolean(rs2, "bpjs_cek_propinsi");
                        akses.bpjs_cek_kabupaten=akses.getBoolean(rs2, "bpjs_cek_kabupaten");
                        akses.bpjs_cek_kecamatan=akses.getBoolean(rs2, "bpjs_cek_kecamatan");
                        akses.bpjs_cek_dokterdpjp=akses.getBoolean(rs2, "bpjs_cek_dokterdpjp");
                        akses.bpjs_cek_riwayat_rujukanrs=akses.getBoolean(rs2, "bpjs_cek_riwayat_rujukanrs");
                        akses.bpjs_cek_tanggal_rujukan=akses.getBoolean(rs2, "bpjs_cek_tanggal_rujukan");
                        akses.permintaan_lab=akses.getBoolean(rs2, "permintaan_lab");
                        akses.permintaan_radiologi=akses.getBoolean(rs2, "permintaan_radiologi");
                        akses.surat_indeks=akses.getBoolean(rs2, "surat_indeks");
                        akses.surat_map=akses.getBoolean(rs2, "surat_map");
                        akses.surat_almari=akses.getBoolean(rs2, "surat_almari");
                        akses.surat_rak=akses.getBoolean(rs2, "surat_rak");
                        akses.surat_ruang=akses.getBoolean(rs2, "surat_ruang");
                        akses.surat_klasifikasi=akses.getBoolean(rs2, "surat_klasifikasi");
                        akses.surat_status=akses.getBoolean(rs2, "surat_status");
                        akses.surat_sifat=akses.getBoolean(rs2, "surat_sifat");
                        akses.surat_balas=akses.getBoolean(rs2, "surat_balas");
                        akses.surat_masuk=akses.getBoolean(rs2, "surat_masuk");
                        akses.pcare_cek_dokter=akses.getBoolean(rs2, "pcare_cek_dokter");
                        akses.pcare_cek_poli=akses.getBoolean(rs2, "pcare_cek_poli");
                        akses.pcare_cek_provider=akses.getBoolean(rs2, "pcare_cek_provider");
                        akses.pcare_cek_statuspulang=akses.getBoolean(rs2, "pcare_cek_statuspulang");
                        akses.pcare_cek_spesialis=akses.getBoolean(rs2, "pcare_cek_spesialis");
                        akses.pcare_cek_subspesialis=akses.getBoolean(rs2, "pcare_cek_subspesialis");
                        akses.pcare_cek_sarana=akses.getBoolean(rs2, "pcare_cek_sarana");
                        akses.pcare_cek_khusus=akses.getBoolean(rs2, "pcare_cek_khusus");
                        akses.pcare_cek_obat=akses.getBoolean(rs2, "pcare_cek_obat");
                        akses.pcare_cek_tindakan=akses.getBoolean(rs2, "pcare_cek_tindakan");
                        akses.pcare_cek_faskessubspesialis=akses.getBoolean(rs2, "pcare_cek_faskessubspesialis");
                        akses.pcare_cek_faskesalihrawat=akses.getBoolean(rs2, "pcare_cek_faskesalihrawat");
                        akses.pcare_cek_faskesthalasemia=akses.getBoolean(rs2, "pcare_cek_faskesthalasemia");
                        akses.pcare_mapping_obat=akses.getBoolean(rs2, "pcare_mapping_obat");
                        akses.pcare_mapping_tindakan=akses.getBoolean(rs2, "pcare_mapping_tindakan");
                        akses.pcare_club_prolanis=akses.getBoolean(rs2, "pcare_club_prolanis");
                        akses.pcare_mapping_poli=akses.getBoolean(rs2, "pcare_mapping_poli");
                        akses.pcare_kegiatan_kelompok=akses.getBoolean(rs2, "pcare_kegiatan_kelompok");
                        akses.pcare_mapping_tindakan_ranap=akses.getBoolean(rs2, "pcare_mapping_tindakan_ranap");
                        akses.pcare_peserta_kegiatan_kelompok=akses.getBoolean(rs2, "pcare_peserta_kegiatan_kelompok");
                        akses.sirkulasi_obat3=akses.getBoolean(rs2, "sirkulasi_obat3");
                        akses.bridging_pcare_daftar=akses.getBoolean(rs2, "bridging_pcare_daftar");
                        akses.pcare_mapping_dokter=akses.getBoolean(rs2, "pcare_mapping_dokter");
                        akses.ranap_per_ruang=akses.getBoolean(rs2, "ranap_per_ruang");
                        akses.penyakit_ranap_cara_bayar=akses.getBoolean(rs2, "penyakit_ranap_cara_bayar");
                        akses.anggota_militer_dirawat=akses.getBoolean(rs2, "anggota_militer_dirawat");
                        akses.set_input_parsial=akses.getBoolean(rs2, "set_input_parsial");
                        akses.lama_pelayanan_radiologi=akses.getBoolean(rs2, "lama_pelayanan_radiologi");
                        akses.lama_pelayanan_lab=akses.getBoolean(rs2, "lama_pelayanan_lab");
                        akses.bpjs_cek_sep=akses.getBoolean(rs2, "bpjs_cek_sep");
                        akses.catatan_perawatan=akses.getBoolean(rs2, "catatan_perawatan");
                        akses.surat_keluar=akses.getBoolean(rs2, "surat_keluar");
                        akses.kegiatan_farmasi=akses.getBoolean(rs2, "kegiatan_farmasi");
                        akses.stok_opname_logistik=akses.getBoolean(rs2, "stok_opname_logistik");
                        akses.sirkulasi_non_medis=akses.getBoolean(rs2, "sirkulasi_non_medis");
                        akses.rekap_lab_pertahun=akses.getBoolean(rs2, "rekap_lab_pertahun");
                        akses.perujuk_lab_pertahun=akses.getBoolean(rs2, "perujuk_lab_pertahun");
                        akses.rekap_radiologi_pertahun=akses.getBoolean(rs2, "rekap_radiologi_pertahun");
                        akses.perujuk_radiologi_pertahun=akses.getBoolean(rs2, "perujuk_radiologi_pertahun");
                        akses.jumlah_porsi_diet=akses.getBoolean(rs2, "jumlah_porsi_diet");
                        akses.jumlah_macam_diet=akses.getBoolean(rs2, "jumlah_macam_diet");
                        akses.payment_point2=akses.getBoolean(rs2, "payment_point2");
                        akses.pembayaran_akun_bayar2=akses.getBoolean(rs2, "pembayaran_akun_bayar2");
                        akses.hapus_nota_salah=akses.getBoolean(rs2, "hapus_nota_salah");
                        akses.hais_perbangsal=akses.getBoolean(rs2, "hais_perbangsal");
                        akses.ppn_obat=akses.getBoolean(rs2, "ppn_obat");
                        akses.saldo_akun_perbulan=akses.getBoolean(rs2, "saldo_akun_perbulan");
                        akses.display_apotek=akses.getBoolean(rs2, "display_apotek");
                        akses.sisrute_referensi_faskes=akses.getBoolean(rs2, "sisrute_referensi_faskes");
                        akses.sisrute_referensi_alasanrujuk=akses.getBoolean(rs2, "sisrute_referensi_alasanrujuk");
                        akses.sisrute_referensi_diagnosa=akses.getBoolean(rs2, "sisrute_referensi_diagnosa");
                        akses.sisrute_rujukan_masuk=akses.getBoolean(rs2, "sisrute_rujukan_masuk");
                        akses.sisrute_rujukan_keluar=akses.getBoolean(rs2, "sisrute_rujukan_keluar");
                        akses.bpjs_cek_skdp=akses.getBoolean(rs2, "bpjs_cek_skdp");
                        akses.data_batch=akses.getBoolean(rs2, "data_batch");
                        akses.kunjungan_permintaan_lab=akses.getBoolean(rs2, "kunjungan_permintaan_lab");
                        akses.kunjungan_permintaan_lab2=akses.getBoolean(rs2, "kunjungan_permintaan_lab2");
                        akses.kunjungan_permintaan_radiologi=akses.getBoolean(rs2, "kunjungan_permintaan_radiologi");
                        akses.kunjungan_permintaan_radiologi2=akses.getBoolean(rs2, "kunjungan_permintaan_radiologi2");
                        akses.pcare_pemberian_obat=akses.getBoolean(rs2, "pcare_pemberian_obat");
                        akses.pcare_pemberian_tindakan=akses.getBoolean(rs2, "pcare_pemberian_tindakan");
                        akses.pembayaran_akun_bayar3=akses.getBoolean(rs2, "pembayaran_akun_bayar3");
                        akses.password_asuransi=akses.getBoolean(rs2, "password_asuransi");
                        akses.kemenkes_sitt=akses.getBoolean(rs2, "kemenkes_sitt");
                        akses.siranap_ketersediaan_kamar=akses.getBoolean(rs2, "siranap_ketersediaan_kamar");
                        akses.grafik_tb_periodelaporan=akses.getBoolean(rs2, "grafik_tb_periodelaporan");
                        akses.grafik_tb_rujukan=akses.getBoolean(rs2, "grafik_tb_rujukan");
                        akses.grafik_tb_riwayat=akses.getBoolean(rs2, "grafik_tb_riwayat");
                        akses.grafik_tb_tipediagnosis=akses.getBoolean(rs2, "grafik_tb_tipediagnosis");
                        akses.grafik_tb_statushiv=akses.getBoolean(rs2, "grafik_tb_statushiv");
                        akses.grafik_tb_skoringanak=akses.getBoolean(rs2, "grafik_tb_skoringanak");
                        akses.grafik_tb_konfirmasiskoring5=akses.getBoolean(rs2, "grafik_tb_konfirmasiskoring5");
                        akses.grafik_tb_konfirmasiskoring6=akses.getBoolean(rs2, "grafik_tb_konfirmasiskoring6");
                        akses.grafik_tb_sumberobat=akses.getBoolean(rs2, "grafik_tb_sumberobat");
                        akses.grafik_tb_hasilakhirpengobatan=akses.getBoolean(rs2, "grafik_tb_hasilakhirpengobatan");
                        akses.grafik_tb_hasilteshiv=akses.getBoolean(rs2, "grafik_tb_hasilteshiv");
                        akses.kadaluarsa_batch=akses.getBoolean(rs2, "kadaluarsa_batch");
                        akses.sisa_stok=akses.getBoolean(rs2, "sisa_stok");
                        akses.obat_per_resep=akses.getBoolean(rs2, "obat_per_resep");
                        akses.pemakaian_air_pdam=akses.getBoolean(rs2, "pemakaian_air_pdam");
                        akses.limbah_b3_medis=akses.getBoolean(rs2, "limbah_b3_medis");
                        akses.grafik_air_pdam_pertanggal=akses.getBoolean(rs2, "grafik_air_pdam_pertanggal");
                        akses.grafik_air_pdam_perbulan=akses.getBoolean(rs2, "grafik_air_pdam_perbulan");
                        akses.grafik_limbahb3_pertanggal=akses.getBoolean(rs2, "grafik_limbahb3_pertanggal");
                        akses.grafik_limbahb3_perbulan=akses.getBoolean(rs2, "grafik_limbahb3_perbulan");
                        akses.limbah_domestik=akses.getBoolean(rs2, "limbah_domestik");
                        akses.grafik_limbahdomestik_pertanggal=akses.getBoolean(rs2, "grafik_limbahdomestik_pertanggal");
                        akses.grafik_limbahdomestik_perbulan=akses.getBoolean(rs2, "grafik_limbahdomestik_perbulan");
                        akses.mutu_air_limbah=akses.getBoolean(rs2, "mutu_air_limbah");
                        akses.pest_control=akses.getBoolean(rs2, "pest_control");
                        akses.ruang_perpustakaan=akses.getBoolean(rs2, "ruang_perpustakaan");
                        akses.kategori_perpustakaan=akses.getBoolean(rs2, "kategori_perpustakaan");
                        akses.jenis_perpustakaan=akses.getBoolean(rs2, "jenis_perpustakaan");
                        akses.pengarang_perpustakaan=akses.getBoolean(rs2, "pengarang_perpustakaan");
                        akses.penerbit_perpustakaan=akses.getBoolean(rs2, "penerbit_perpustakaan");
                        akses.koleksi_perpustakaan=akses.getBoolean(rs2, "koleksi_perpustakaan");
                        akses.inventaris_perpustakaan=akses.getBoolean(rs2, "inventaris_perpustakaan");
                        akses.set_peminjaman_perpustakaan=akses.getBoolean(rs2, "set_peminjaman_perpustakaan");
                        akses.denda_perpustakaan=akses.getBoolean(rs2, "denda_perpustakaan");
                        akses.anggota_perpustakaan=akses.getBoolean(rs2, "anggota_perpustakaan");
                        akses.peminjaman_perpustakaan=akses.getBoolean(rs2, "peminjaman_perpustakaan");
                        akses.bayar_denda_perpustakaan=akses.getBoolean(rs2, "bayar_denda_perpustakaan");
                        akses.ebook_perpustakaan=akses.getBoolean(rs2, "ebook_perpustakaan");
                        akses.jenis_cidera_k3rs=akses.getBoolean(rs2, "jenis_cidera_k3rs");
                        akses.penyebab_k3rs=akses.getBoolean(rs2, "penyebab_k3rs");
                        akses.jenis_luka_k3rs=akses.getBoolean(rs2, "jenis_luka_k3rs");
                        akses.lokasi_kejadian_k3rs=akses.getBoolean(rs2, "lokasi_kejadian_k3rs");
                        akses.dampak_cidera_k3rs=akses.getBoolean(rs2, "dampak_cidera_k3rs");
                        akses.jenis_pekerjaan_k3rs=akses.getBoolean(rs2, "jenis_pekerjaan_k3rs");
                        akses.bagian_tubuh_k3rs=akses.getBoolean(rs2, "bagian_tubuh_k3rs");
                        akses.peristiwa_k3rs=akses.getBoolean(rs2, "peristiwa_k3rs");
                        akses.grafik_k3_pertahun=akses.getBoolean(rs2, "grafik_k3_pertahun");
                        akses.grafik_k3_perbulan=akses.getBoolean(rs2, "grafik_k3_perbulan");
                        akses.grafik_k3_pertanggal=akses.getBoolean(rs2, "grafik_k3_pertanggal");
                        akses.grafik_k3_perjeniscidera=akses.getBoolean(rs2, "grafik_k3_perjeniscidera");
                        akses.grafik_k3_perpenyebab=akses.getBoolean(rs2, "grafik_k3_perpenyebab");
                        akses.grafik_k3_perjenisluka=akses.getBoolean(rs2, "grafik_k3_perjenisluka");
                        akses.grafik_k3_lokasikejadian=akses.getBoolean(rs2, "grafik_k3_lokasikejadian");
                        akses.grafik_k3_dampakcidera=akses.getBoolean(rs2, "grafik_k3_dampakcidera");
                        akses.grafik_k3_perjenispekerjaan=akses.getBoolean(rs2, "grafik_k3_perjenispekerjaan");
                        akses.grafik_k3_perbagiantubuh=akses.getBoolean(rs2, "grafik_k3_perbagiantubuh");
                        akses.jenis_cidera_k3rstahun=akses.getBoolean(rs2, "jenis_cidera_k3rstahun");
                        akses.penyebab_k3rstahun=akses.getBoolean(rs2, "penyebab_k3rstahun");
                        akses.jenis_luka_k3rstahun=akses.getBoolean(rs2, "jenis_luka_k3rstahun");
                        akses.lokasi_kejadian_k3rstahun=akses.getBoolean(rs2, "lokasi_kejadian_k3rstahun");
                        akses.dampak_cidera_k3rstahun=akses.getBoolean(rs2, "dampak_cidera_k3rstahun");
                        akses.jenis_pekerjaan_k3rstahun=akses.getBoolean(rs2, "jenis_pekerjaan_k3rstahun");
                        akses.bagian_tubuh_k3rstahun=akses.getBoolean(rs2, "bagian_tubuh_k3rstahun");
                        akses.sekrining_rawat_jalan=akses.getBoolean(rs2, "sekrining_rawat_jalan");
                        akses.bpjs_histori_pelayanan=akses.getBoolean(rs2, "bpjs_histori_pelayanan");
                        akses.rekap_mutasi_berkas=akses.getBoolean(rs2, "rekap_mutasi_berkas");
                        akses.skrining_ralan_pernapasan_pertahun=akses.getBoolean(rs2, "skrining_ralan_pernapasan_pertahun");
                        akses.pengajuan_barang_medis=akses.getBoolean(rs2, "pengajuan_barang_medis");
                        akses.pengajuan_barang_nonmedis=akses.getBoolean(rs2, "pengajuan_barang_nonmedis");
                        akses.grafik_kunjungan_ranapbulan=akses.getBoolean(rs2, "grafik_kunjungan_ranapbulan");
                        akses.grafik_kunjungan_ranaptanggal=akses.getBoolean(rs2, "grafik_kunjungan_ranaptanggal");
                        akses.grafik_kunjungan_ranap_peruang=akses.getBoolean(rs2, "grafik_kunjungan_ranap_peruang");
                        akses.kunjungan_bangsal_pertahun=akses.getBoolean(rs2, "kunjungan_bangsal_pertahun");
                        akses.grafik_jenjang_jabatanpegawai=akses.getBoolean(rs2, "grafik_jenjang_jabatanpegawai");
                        akses.grafik_bidangpegawai=akses.getBoolean(rs2, "grafik_bidangpegawai");
                        akses.grafik_departemenpegawai=akses.getBoolean(rs2, "grafik_departemenpegawai");
                        akses.grafik_pendidikanpegawai=akses.getBoolean(rs2, "grafik_pendidikanpegawai");
                        akses.grafik_sttswppegawai=akses.getBoolean(rs2, "grafik_sttswppegawai");
                        akses.grafik_sttskerjapegawai=akses.getBoolean(rs2, "grafik_sttskerjapegawai");
                        akses.grafik_sttspulangranap=akses.getBoolean(rs2, "grafik_sttspulangranap");
                        akses.kip_pasien_ranap=akses.getBoolean(rs2, "kip_pasien_ranap");
                        akses.kip_pasien_ralan=akses.getBoolean(rs2, "kip_pasien_ralan");
                        akses.bpjs_mapping_dokterdpjp=akses.getBoolean(rs2, "bpjs_mapping_dokterdpjp");
                        akses.data_triase_igd=akses.getBoolean(rs2, "data_triase_igd");
                        akses.master_triase_skala1=akses.getBoolean(rs2, "master_triase_skala1");
                        akses.master_triase_skala2=akses.getBoolean(rs2, "master_triase_skala2");
                        akses.master_triase_skala3=akses.getBoolean(rs2, "master_triase_skala3");
                        akses.master_triase_skala4=akses.getBoolean(rs2, "master_triase_skala4");
                        akses.master_triase_skala5=akses.getBoolean(rs2, "master_triase_skala5");
                        akses.master_triase_pemeriksaan=akses.getBoolean(rs2, "master_triase_pemeriksaan");
                        akses.master_triase_macamkasus=akses.getBoolean(rs2, "master_triase_macamkasus");
                        akses.rekap_permintaan_diet=akses.getBoolean(rs2, "rekap_permintaan_diet");
                        akses.daftar_pasien_ranap=akses.getBoolean(rs2, "daftar_pasien_ranap");
                        akses.daftar_pasien_ranaptni=akses.getBoolean(rs2, "daftar_pasien_ranaptni");
                        akses.pengajuan_asetinventaris=akses.getBoolean(rs2, "pengajuan_asetinventaris");
                        akses.item_apotek_jenis=akses.getBoolean(rs2, "item_apotek_jenis");
                        akses.item_apotek_kategori=akses.getBoolean(rs2, "item_apotek_kategori");
                        akses.item_apotek_golongan=akses.getBoolean(rs2, "item_apotek_golongan");
                        akses.item_apotek_industrifarmasi=akses.getBoolean(rs2, "item_apotek_industrifarmasi");
                        akses.obat10_terbanyak_poli=akses.getBoolean(rs2, "10_obat_terbanyak_poli");
                        akses.grafik_pengajuan_aset_urgensi=akses.getBoolean(rs2, "grafik_pengajuan_aset_urgensi");
                        akses.grafik_pengajuan_aset_status=akses.getBoolean(rs2, "grafik_pengajuan_aset_status");
                        akses.grafik_pengajuan_aset_departemen=akses.getBoolean(rs2, "grafik_pengajuan_aset_departemen");
                        akses.rekap_pengajuan_aset_departemen=akses.getBoolean(rs2, "rekap_pengajuan_aset_departemen");
                        akses.grafik_kelompok_jabatanpegawai=akses.getBoolean(rs2, "grafik_kelompok_jabatanpegawai");
                        akses.grafik_resiko_kerjapegawai=akses.getBoolean(rs2, "grafik_resiko_kerjapegawai");
                        akses.grafik_emergency_indexpegawai=akses.getBoolean(rs2, "grafik_emergency_indexpegawai");
                        akses.grafik_inventaris_ruang=akses.getBoolean(rs2, "grafik_inventaris_ruang");
                        akses.harian_HAIs2=akses.getBoolean(rs2, "harian_HAIs2");
                        akses.grafik_inventaris_jenis=akses.getBoolean(rs2, "grafik_inventaris_jenis");
                        akses.data_resume_pasien=akses.getBoolean(rs2, "data_resume_pasien");
                        akses.perkiraan_biaya_ranap=akses.getBoolean(rs2, "perkiraan_biaya_ranap");
                        akses.rekap_obat_poli=akses.getBoolean(rs2, "rekap_obat_poli");
                        akses.rekap_obat_pasien=akses.getBoolean(rs2, "rekap_obat_pasien");
                        akses.grafik_HAIs_pasienbangsal=akses.getBoolean(rs2, "grafik_HAIs_pasienbangsal");
                        akses.grafik_HAIs_pasienbulan=akses.getBoolean(rs2, "grafik_HAIs_pasienbulan");
                        akses.permintaan_perbaikan_inventaris=akses.getBoolean(rs2, "permintaan_perbaikan_inventaris");
                        akses.grafik_HAIs_laju_vap=akses.getBoolean(rs2, "grafik_HAIs_laju_vap");
                        akses.grafik_HAIs_laju_iad=akses.getBoolean(rs2, "grafik_HAIs_laju_iad");
                        akses.grafik_HAIs_laju_pleb=akses.getBoolean(rs2, "grafik_HAIs_laju_pleb");
                        akses.grafik_HAIs_laju_isk=akses.getBoolean(rs2, "grafik_HAIs_laju_isk");
                        akses.grafik_HAIs_laju_ilo=akses.getBoolean(rs2, "grafik_HAIs_laju_ilo");
                        akses.grafik_HAIs_laju_hap=akses.getBoolean(rs2, "grafik_HAIs_laju_hap");
                        akses.inhealth_mapping_poli=akses.getBoolean(rs2, "inhealth_mapping_poli");
                        akses.inhealth_mapping_dokter=akses.getBoolean(rs2, "inhealth_mapping_dokter");
                        akses.inhealth_mapping_tindakan_ralan=akses.getBoolean(rs2, "inhealth_mapping_tindakan_ralan");
                        akses.inhealth_mapping_tindakan_ranap=akses.getBoolean(rs2, "inhealth_mapping_tindakan_ranap");
                        akses.inhealth_mapping_tindakan_radiologi=akses.getBoolean(rs2, "inhealth_mapping_tindakan_radiologi");
                        akses.inhealth_mapping_tindakan_laborat=akses.getBoolean(rs2, "inhealth_mapping_tindakan_laborat");
                        akses.inhealth_mapping_tindakan_operasi=akses.getBoolean(rs2, "inhealth_mapping_tindakan_operasi");
                        akses.hibah_obat_bhp=akses.getBoolean(rs2, "hibah_obat_bhp");
                        akses.asal_hibah=akses.getBoolean(rs2, "asal_hibah");
                        akses.asuhan_gizi=akses.getBoolean(rs2, "asuhan_gizi");
                        akses.inhealth_kirim_tagihan=akses.getBoolean(rs2, "inhealth_kirim_tagihan");
                        akses.sirkulasi_obat4=akses.getBoolean(rs2, "sirkulasi_obat4");
                        akses.sirkulasi_obat5=akses.getBoolean(rs2, "sirkulasi_obat5");
                        akses.sirkulasi_non_medis2=akses.getBoolean(rs2, "sirkulasi_non_medis2");
                        akses.monitoring_asuhan_gizi=akses.getBoolean(rs2, "monitoring_asuhan_gizi");
                        akses.penerimaan_obat_perbulan=akses.getBoolean(rs2, "penerimaan_obat_perbulan");
                        akses.rekap_kunjungan=akses.getBoolean(rs2, "rekap_kunjungan");
                        akses.surat_sakit=akses.getBoolean(rs2, "surat_sakit");
                        akses.penilaian_awal_keperawatan_ralan=akses.getBoolean(rs2, "penilaian_awal_keperawatan_ralan");
                        akses.permintaan_diet=akses.getBoolean(rs2, "permintaan_diet");
                        akses.master_masalah_keperawatan=akses.getBoolean(rs2, "master_masalah_keperawatan");
                        akses.pengajuan_cuti=akses.getBoolean(rs2, "pengajuan_cuti");
                        akses.kedatangan_pasien=akses.getBoolean(rs2, "kedatangan_pasien");
                        akses.utd_pendonor=akses.getBoolean(rs2, "utd_pendonor");
                        akses.toko_suplier=akses.getBoolean(rs2, "toko_suplier");
                        akses.toko_jenis=akses.getBoolean(rs2, "toko_jenis");
                        akses.toko_set_harga=akses.getBoolean(rs2, "toko_set_harga");
                        akses.toko_barang=akses.getBoolean(rs2, "toko_barang");
                        akses.penagihan_piutang_pasien=akses.getBoolean(rs2, "penagihan_piutang_pasien");
                        akses.akun_penagihan_piutang=akses.getBoolean(rs2, "akun_penagihan_piutang");
                        akses.stok_opname_toko=akses.getBoolean(rs2, "stok_opname_toko");
                        akses.toko_riwayat_barang=akses.getBoolean(rs2, "toko_riwayat_barang");
                        akses.toko_surat_pemesanan=akses.getBoolean(rs2, "toko_surat_pemesanan");
                        akses.toko_pengajuan_barang=akses.getBoolean(rs2, "toko_pengajuan_barang");
                        akses.toko_penerimaan_barang=akses.getBoolean(rs2, "toko_penerimaan_barang");
                        akses.toko_pengadaan_barang=akses.getBoolean(rs2, "toko_pengadaan_barang");
                        akses.toko_hutang=akses.getBoolean(rs2, "toko_hutang");
                        akses.toko_bayar_pemesanan=akses.getBoolean(rs2, "toko_bayar_pemesanan");
                        akses.toko_member=akses.getBoolean(rs2, "toko_member");
                        akses.toko_penjualan=akses.getBoolean(rs2, "toko_penjualan");
                        akses.registrasi_poli_per_tanggal=akses.getBoolean(rs2, "registrasi_poli_per_tanggal");
                        akses.toko_piutang=akses.getBoolean(rs2, "toko_piutang");
                        akses.toko_retur_beli=akses.getBoolean(rs2, "toko_retur_beli");
                        akses.ipsrs_returbeli=akses.getBoolean(rs2, "ipsrs_returbeli");
                        akses.ipsrs_riwayat_barang=akses.getBoolean(rs2, "ipsrs_riwayat_barang");
                        akses.pasien_corona=akses.getBoolean(rs2, "pasien_corona");
                        akses.toko_pendapatan_harian=akses.getBoolean(rs2, "toko_pendapatan_harian");
                        akses.diagnosa_pasien_corona=akses.getBoolean(rs2, "diagnosa_pasien_corona");
                        akses.perawatan_pasien_corona=akses.getBoolean(rs2, "perawatan_pasien_corona");
                        akses.penilaian_awal_keperawatan_gigi=akses.getBoolean(rs2, "penilaian_awal_keperawatan_gigi");
                        akses.master_masalah_keperawatan_gigi=akses.getBoolean(rs2, "master_masalah_keperawatan_gigi");
                        akses.toko_bayar_piutang=akses.getBoolean(rs2, "toko_bayar_piutang");
                        akses.toko_piutang_harian=akses.getBoolean(rs2, "toko_piutang_harian");
                        akses.toko_penjualan_harian=akses.getBoolean(rs2, "toko_penjualan_harian");
                        akses.deteksi_corona=akses.getBoolean(rs2, "deteksi_corona");
                        akses.penilaian_awal_keperawatan_kebidanan=akses.getBoolean(rs2, "penilaian_awal_keperawatan_kebidanan");
                        akses.pengumuman_epasien=akses.getBoolean(rs2, "pengumuman_epasien");
                        akses.surat_hamil=akses.getBoolean(rs2, "surat_hamil");
                        akses.set_tarif_online=akses.getBoolean(rs2, "set_tarif_online");
                        akses.booking_periksa=akses.getBoolean(rs2, "booking_periksa");
                        akses.toko_sirkulasi=akses.getBoolean(rs2, "toko_sirkulasi");
                        akses.toko_retur_jual=akses.getBoolean(rs2, "toko_retur_jual");
                        akses.toko_retur_piutang=akses.getBoolean(rs2, "toko_retur_piutang");
                        akses.toko_sirkulasi2=akses.getBoolean(rs2, "toko_sirkulasi2");
                        akses.toko_keuntungan_barang=akses.getBoolean(rs2, "toko_keuntungan_barang");
                        akses.zis_pengeluaran_penerima_dankes=akses.getBoolean(rs2, "zis_pengeluaran_penerima_dankes");
                        akses.zis_penghasilan_penerima_dankes=akses.getBoolean(rs2, "zis_penghasilan_penerima_dankes");
                        akses.zis_ukuran_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_ukuran_rumah_penerima_dankes");
                        akses.zis_dinding_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_dinding_rumah_penerima_dankes");
                        akses.zis_lantai_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_lantai_rumah_penerima_dankes");
                        akses.zis_atap_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_atap_rumah_penerima_dankes");
                        akses.zis_kepemilikan_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_kepemilikan_rumah_penerima_dankes");
                        akses.zis_kamar_mandi_penerima_dankes=akses.getBoolean(rs2, "zis_kamar_mandi_penerima_dankes");
                        akses.zis_dapur_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_dapur_rumah_penerima_dankes");
                        akses.zis_kursi_rumah_penerima_dankes=akses.getBoolean(rs2, "zis_kursi_rumah_penerima_dankes");
                        akses.zis_kategori_phbs_penerima_dankes=akses.getBoolean(rs2, "zis_kategori_phbs_penerima_dankes");
                        akses.zis_elektronik_penerima_dankes=akses.getBoolean(rs2, "zis_elektronik_penerima_dankes");
                        akses.zis_ternak_penerima_dankes=akses.getBoolean(rs2, "zis_ternak_penerima_dankes");
                        akses.zis_jenis_simpanan_penerima_dankes=akses.getBoolean(rs2, "zis_jenis_simpanan_penerima_dankes");
                        akses.penilaian_awal_keperawatan_anak=akses.getBoolean(rs2, "penilaian_awal_keperawatan_anak");
                        akses.zis_kategori_asnaf_penerima_dankes=akses.getBoolean(rs2, "zis_kategori_asnaf_penerima_dankes");
                        akses.master_masalah_keperawatan_anak=akses.getBoolean(rs2, "master_masalah_keperawatan_anak");
                        akses.master_imunisasi=akses.getBoolean(rs2, "master_imunisasi");
                        akses.zis_patologis_penerima_dankes=akses.getBoolean(rs2, "zis_patologis_penerima_dankes");
                        akses.pcare_cek_kartu=akses.getBoolean(rs2, "pcare_cek_kartu");
                        akses.surat_bebas_narkoba=akses.getBoolean(rs2, "surat_bebas_narkoba");
                        akses.surat_keterangan_covid=akses.getBoolean(rs2, "surat_keterangan_covid");
                        akses.pemakaian_air_tanah=akses.getBoolean(rs2, "pemakaian_air_tanah");
                        akses.grafik_air_tanah_pertanggal=akses.getBoolean(rs2, "grafik_air_tanah_pertanggal");
                        akses.grafik_air_tanah_perbulan=akses.getBoolean(rs2, "grafik_air_tanah_perbulan");
                        akses.lama_pelayanan_poli=akses.getBoolean(rs2, "lama_pelayanan_poli");
                        akses.hemodialisa=akses.getBoolean(rs2, "hemodialisa");
                        akses.grafik_harian_hemodialisa=akses.getBoolean(rs2, "grafik_harian_hemodialisa");
                        akses.grafik_bulanan_hemodialisa=akses.getBoolean(rs2, "grafik_bulanan_hemodialisa");
                        akses.grafik_tahunan_hemodialisa=akses.getBoolean(rs2, "grafik_tahunan_hemodialisa");
                        akses.grafik_bulanan_meninggal=akses.getBoolean(rs2, "grafik_bulanan_meninggal");
                        akses.laporan_tahunan_irj=akses.getBoolean(rs2, "laporan_tahunan_irj");
                        akses.perbaikan_inventaris=akses.getBoolean(rs2, "perbaikan_inventaris");
                        akses.surat_cuti_hamil=akses.getBoolean(rs2, "surat_cuti_hamil");
                        akses.permintaan_stok_obat_pasien=akses.getBoolean(rs2, "permintaan_stok_obat_pasien");
                        akses.pemeliharaan_inventaris=akses.getBoolean(rs2, "pemeliharaan_inventaris");
                        akses.klasifikasi_pasien_ranap=akses.getBoolean(rs2, "klasifikasi_pasien_ranap");
                        akses.bulanan_klasifikasi_pasien_ranap=akses.getBoolean(rs2, "bulanan_klasifikasi_pasien_ranap");
                        akses.harian_klasifikasi_pasien_ranap=akses.getBoolean(rs2, "harian_klasifikasi_pasien_ranap");
                        akses.klasifikasi_pasien_perbangsal=akses.getBoolean(rs2, "klasifikasi_pasien_perbangsal");
                        akses.soap_perawatan=akses.getBoolean(rs2, "soap_perawatan");
                        akses.klaim_rawat_jalan=akses.getBoolean(rs2, "klaim_rawat_jalan");
                        akses.skrining_gizi=akses.getBoolean(rs2, "skrining_gizi");
                        akses.lama_penyiapan_rm=akses.getBoolean(rs2, "lama_penyiapan_rm");
                        akses.dosis_radiologi=akses.getBoolean(rs2, "dosis_radiologi");
                        akses.demografi_umur_kunjungan=akses.getBoolean(rs2, "demografi_umur_kunjungan");
                        akses.jam_diet_pasien=akses.getBoolean(rs2, "jam_diet_pasien");
                        akses.rvu_bpjs=akses.getBoolean(rs2, "rvu_bpjs");
                        akses.verifikasi_penerimaan_farmasi=akses.getBoolean(rs2, "verifikasi_penerimaan_farmasi");
                        akses.verifikasi_penerimaan_logistik=akses.getBoolean(rs2, "verifikasi_penerimaan_logistik");
                        akses.pemeriksaan_lab_pa=akses.getBoolean(rs2, "pemeriksaan_lab_pa");
                        akses.ringkasan_pengajuan_obat=akses.getBoolean(rs2, "ringkasan_pengajuan_obat");
                        akses.ringkasan_pemesanan_obat=akses.getBoolean(rs2, "ringkasan_pemesanan_obat");
                        akses.ringkasan_pengadaan_obat=akses.getBoolean(rs2, "ringkasan_pengadaan_obat");
                        akses.ringkasan_penerimaan_obat=akses.getBoolean(rs2, "ringkasan_penerimaan_obat");
                        akses.ringkasan_hibah_obat=akses.getBoolean(rs2, "ringkasan_hibah_obat");
                        akses.ringkasan_penjualan_obat=akses.getBoolean(rs2, "ringkasan_penjualan_obat");
                        akses.ringkasan_beri_obat=akses.getBoolean(rs2, "ringkasan_beri_obat");
                        akses.ringkasan_piutang_obat=akses.getBoolean(rs2, "ringkasan_piutang_obat");
                        akses.ringkasan_stok_keluar_obat=akses.getBoolean(rs2, "ringkasan_stok_keluar_obat");
                        akses.ringkasan_retur_suplier_obat=akses.getBoolean(rs2, "ringkasan_retur_suplier_obat");
                        akses.ringkasan_retur_pembeli_obat=akses.getBoolean(rs2, "ringkasan_retur_pembeli_obat");
                        akses.penilaian_awal_keperawatan_ranapkebidanan=akses.getBoolean(rs2, "penilaian_awal_keperawatan_ranapkebidanan");
                        akses.ringkasan_pengajuan_nonmedis=akses.getBoolean(rs2, "ringkasan_pengajuan_nonmedis");
                        akses.ringkasan_pemesanan_nonmedis=akses.getBoolean(rs2, "ringkasan_pemesanan_nonmedis");
                        akses.ringkasan_pengadaan_nonmedis=akses.getBoolean(rs2, "ringkasan_pengadaan_nonmedis");
                        akses.ringkasan_penerimaan_nonmedis=akses.getBoolean(rs2, "ringkasan_penerimaan_nonmedis");
                        akses.ringkasan_stokkeluar_nonmedis=akses.getBoolean(rs2, "ringkasan_stokkeluar_nonmedis");
                        akses.ringkasan_returbeli_nonmedis=akses.getBoolean(rs2, "ringkasan_returbeli_nonmedis");
                        akses.omset_penerimaan=akses.getBoolean(rs2, "omset_penerimaan");
                        akses.validasi_penagihan_piutang=akses.getBoolean(rs2, "validasi_penagihan_piutang");
                        akses.permintaan_ranap=akses.getBoolean(rs2, "permintaan_ranap");
                        akses.bpjs_diagnosa_prb=akses.getBoolean(rs2, "bpjs_diagnosa_prb");
                        akses.bpjs_obat_prb=akses.getBoolean(rs2, "bpjs_obat_prb");
                        akses.bpjs_surat_kontrol=akses.getBoolean(rs2, "bpjs_surat_kontrol");
                        akses.penggunaan_bhp_ok=akses.getBoolean(rs2, "penggunaan_bhp_ok");
                        akses.surat_keterangan_rawat_inap=akses.getBoolean(rs2, "surat_keterangan_rawat_inap");
                        akses.surat_keterangan_sehat=akses.getBoolean(rs2, "surat_keterangan_sehat");
                        akses.pendapatan_per_carabayar=akses.getBoolean(rs2, "pendapatan_per_carabayar");
                        akses.akun_host_to_host_bank_jateng=akses.getBoolean(rs2, "akun_host_to_host_bank_jateng");
                        akses.pembayaran_bank_jateng=akses.getBoolean(rs2, "pembayaran_bank_jateng");
                        akses.bpjs_surat_pri=akses.getBoolean(rs2, "bpjs_surat_pri");
                        akses.ringkasan_tindakan=akses.getBoolean(rs2, "ringkasan_tindakan");
                        akses.lama_pelayanan_pasien=akses.getBoolean(rs2, "lama_pelayanan_pasien");
                        akses.surat_sakit_pihak_2=akses.getBoolean(rs2, "surat_sakit_pihak_2");
                        akses.tagihan_hutang_obat=akses.getBoolean(rs2, "tagihan_hutang_obat");
                        akses.referensi_mobilejkn_bpjs=akses.getBoolean(rs2, "referensi_mobilejkn_bpjs");
                        akses.batal_pendaftaran_mobilejkn_bpjs=akses.getBoolean(rs2, "batal_pendaftaran_mobilejkn_bpjs");
                        akses.lama_operasi=akses.getBoolean(rs2, "lama_operasi");
                        akses.grafik_inventaris_kategori=akses.getBoolean(rs2, "grafik_inventaris_kategori");
                        akses.grafik_inventaris_merk=akses.getBoolean(rs2, "grafik_inventaris_merk");
                        akses.grafik_inventaris_produsen=akses.getBoolean(rs2, "grafik_inventaris_produsen");
                        akses.pengembalian_deposit_pasien=akses.getBoolean(rs2, "pengembalian_deposit_pasien");
                        akses.validasi_tagihan_hutang_obat=akses.getBoolean(rs2, "validasi_tagihan_hutang_obat");
                        akses.piutang_obat_belum_lunas=akses.getBoolean(rs2, "piutang_obat_belum_lunas");
                        akses.integrasi_briapi=akses.getBoolean(rs2, "integrasi_briapi");
                        akses.pengadaan_aset_inventaris=akses.getBoolean(rs2, "pengadaan_aset_inventaris");
                        akses.akun_aset_inventaris=akses.getBoolean(rs2, "akun_aset_inventaris");
                        akses.suplier_inventaris=akses.getBoolean(rs2, "suplier_inventaris");
                        akses.penerimaan_aset_inventaris=akses.getBoolean(rs2, "penerimaan_aset_inventaris");
                        akses.bayar_pemesanan_iventaris=akses.getBoolean(rs2, "bayar_pemesanan_iventaris");
                        akses.hutang_aset_inventaris=akses.getBoolean(rs2, "hutang_aset_inventaris");
                        akses.hibah_aset_inventaris=akses.getBoolean(rs2, "hibah_aset_inventaris");
                        akses.titip_faktur_non_medis=akses.getBoolean(rs2, "titip_faktur_non_medis");
                        akses.validasi_tagihan_non_medis=akses.getBoolean(rs2, "validasi_tagihan_non_medis");
                        akses.titip_faktur_aset=akses.getBoolean(rs2, "titip_faktur_aset");
                        akses.validasi_tagihan_aset=akses.getBoolean(rs2, "validasi_tagihan_aset");
                        akses.hibah_non_medis=akses.getBoolean(rs2, "hibah_non_medis");
                        akses.pcare_alasan_tacc=akses.getBoolean(rs2, "pcare_alasan_tacc");
                        akses.resep_luar=akses.getBoolean(rs2, "resep_luar");
                        akses.surat_bebas_tbc=akses.getBoolean(rs2, "surat_bebas_tbc");
                        akses.surat_buta_warna=akses.getBoolean(rs2, "surat_buta_warna");
                        akses.surat_bebas_tato=akses.getBoolean(rs2, "surat_bebas_tato");
                        akses.surat_kewaspadaan_kesehatan=akses.getBoolean(rs2, "surat_kewaspadaan_kesehatan");
                        akses.grafik_porsidiet_pertanggal=akses.getBoolean(rs2, "grafik_porsidiet_pertanggal");
                        akses.grafik_porsidiet_perbulan=akses.getBoolean(rs2, "grafik_porsidiet_perbulan");
                        akses.grafik_porsidiet_pertahun=akses.getBoolean(rs2, "grafik_porsidiet_pertahun");
                        akses.grafik_porsidiet_perbangsal=akses.getBoolean(rs2, "grafik_porsidiet_perbangsal");
                        akses.penilaian_awal_medis_ralan=akses.getBoolean(rs2, "penilaian_awal_medis_ralan");
                        akses.master_masalah_keperawatan_mata=akses.getBoolean(rs2, "master_masalah_keperawatan_mata");
                        akses.penilaian_awal_keperawatan_mata=akses.getBoolean(rs2, "penilaian_awal_keperawatan_mata");
                        akses.penilaian_awal_medis_ranap=akses.getBoolean(rs2, "penilaian_awal_medis_ranap");
                        akses.penilaian_awal_medis_ranap_kebidanan=akses.getBoolean(rs2, "penilaian_awal_medis_ranap_kebidanan");
                        akses.penilaian_awal_medis_ralan_kebidanan=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_kebidanan");
                        akses.penilaian_awal_medis_igd=akses.getBoolean(rs2, "penilaian_awal_medis_igd");
                        akses.penilaian_awal_medis_ralan_anak=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_anak");
                        akses.bpjs_referensi_poli_hfis=akses.getBoolean(rs2, "bpjs_referensi_poli_hfis");
                        akses.bpjs_referensi_dokter_hfis=akses.getBoolean(rs2, "bpjs_referensi_dokter_hfis");
                        akses.bpjs_referensi_jadwal_hfis=akses.getBoolean(rs2, "bpjs_referensi_jadwal_hfis");
                        akses.penilaian_fisioterapi=akses.getBoolean(rs2, "penilaian_fisioterapi");
                        akses.bpjs_program_prb=akses.getBoolean(rs2, "bpjs_program_prb");
                        akses.bpjs_suplesi_jasaraharja=akses.getBoolean(rs2, "bpjs_suplesi_jasaraharja");
                        akses.bpjs_data_induk_kecelakaan=akses.getBoolean(rs2, "bpjs_data_induk_kecelakaan");
                        akses.bpjs_sep_internal=akses.getBoolean(rs2, "bpjs_sep_internal");
                        akses.bpjs_klaim_jasa_raharja=akses.getBoolean(rs2, "bpjs_klaim_jasa_raharja");
                        akses.bpjs_daftar_finger_print=akses.getBoolean(rs2, "bpjs_daftar_finger_print");
                        akses.bpjs_rujukan_khusus=akses.getBoolean(rs2, "bpjs_rujukan_khusus");
                        akses.pemeliharaan_gedung=akses.getBoolean(rs2, "pemeliharaan_gedung");
                        akses.grafik_perbaikan_inventaris_pertanggal=akses.getBoolean(rs2, "grafik_perbaikan_inventaris_pertanggal");
                        akses.grafik_perbaikan_inventaris_perbulan=akses.getBoolean(rs2, "grafik_perbaikan_inventaris_perbulan");
                        akses.grafik_perbaikan_inventaris_pertahun=akses.getBoolean(rs2, "grafik_perbaikan_inventaris_pertahun");
                        akses.grafik_perbaikan_inventaris_perpelaksana_status=akses.getBoolean(rs2, "grafik_perbaikan_inventaris_perpelaksana_status");
                        akses.penilaian_mcu=akses.getBoolean(rs2, "penilaian_mcu");
                        akses.peminjam_piutang=akses.getBoolean(rs2, "peminjam_piutang");
                        akses.piutang_lainlain=akses.getBoolean(rs2, "piutang_lainlain");
                        akses.cara_bayar=akses.getBoolean(rs2, "cara_bayar");
                        akses.audit_kepatuhan_apd=akses.getBoolean(rs2, "audit_kepatuhan_apd");
                        akses.bpjs_task_id=akses.getBoolean(rs2, "bpjs_task_id");
                        akses.bayar_piutang_lain=akses.getBoolean(rs2, "bayar_piutang_lain");
                        akses.pembayaran_akun_bayar4=akses.getBoolean(rs2, "pembayaran_akun_bayar4");
                        akses.stok_akhir_farmasi_pertanggal=akses.getBoolean(rs2, "stok_akhir_farmasi_pertanggal");
                        akses.riwayat_kamar_pasien=akses.getBoolean(rs2, "riwayat_kamar_pasien");
                        akses.uji_fungsi_kfr=akses.getBoolean(rs2, "uji_fungsi_kfr");
                        akses.hapus_berkas_digital_perawatan=akses.getBoolean(rs2, "hapus_berkas_digital_perawatan");
                        akses.kategori_pengeluaran_harian=akses.getBoolean(rs2, "kategori_pengeluaran_harian");
                        akses.kategori_pemasukan_lain=akses.getBoolean(rs2, "kategori_pemasukan_lain");
                        akses.pembayaran_akun_bayar5=akses.getBoolean(rs2, "pembayaran_akun_bayar5");
                        akses.ruang_ok=akses.getBoolean(rs2, "ruang_ok");
                        akses.jasa_tindakan_pasien=akses.getBoolean(rs2, "jasa_tindakan_pasien");
                        akses.telaah_resep=akses.getBoolean(rs2, "telaah_resep");
                        akses.permintaan_resep_pulang=akses.getBoolean(rs2, "permintaan_resep_pulang");
                        akses.rekap_jm_dokter=akses.getBoolean(rs2, "rekap_jm_dokter");
                        akses.status_data_rm=akses.getBoolean(rs2, "status_data_rm");
                        akses.ubah_petugas_lab_pk=akses.getBoolean(rs2, "ubah_petugas_lab_pk");
                        akses.ubah_petugas_lab_pa=akses.getBoolean(rs2, "ubah_petugas_lab_pa");
                        akses.ubah_petugas_radiologi=akses.getBoolean(rs2, "ubah_petugas_radiologi");
                        akses.gabung_norawat=akses.getBoolean(rs2, "gabung_norawat");
                        akses.gabung_rm=akses.getBoolean(rs2, "gabung_rm");
                        akses.ringkasan_biaya_obat_pasien_pertanggal=akses.getBoolean(rs2, "ringkasan_biaya_obat_pasien_pertanggal");
                        akses.master_masalah_keperawatan_igd=akses.getBoolean(rs2, "master_masalah_keperawatan_igd");
                        akses.penilaian_awal_keperawatan_igd=akses.getBoolean(rs2, "penilaian_awal_keperawatan_igd");
                        akses.bpjs_referensi_dpho_apotek=akses.getBoolean(rs2, "bpjs_referensi_dpho_apotek");
                        akses.bpjs_referensi_poli_apotek=akses.getBoolean(rs2, "bpjs_referensi_poli_apotek");
                        akses.bayar_jm_dokter=akses.getBoolean(rs2, "bayar_jm_dokter");
                        akses.bpjs_referensi_faskes_apotek=akses.getBoolean(rs2, "bpjs_referensi_faskes_apotek");
                        akses.bpjs_referensi_spesialistik_apotek=akses.getBoolean(rs2, "bpjs_referensi_spesialistik_apotek");
                        akses.pembayaran_briva=akses.getBoolean(rs2, "pembayaran_briva");
                        akses.penilaian_awal_keperawatan_ranap=akses.getBoolean(rs2, "penilaian_awal_keperawatan_ranap");
                        akses.nilai_penerimaan_vendor_farmasi_perbulan=akses.getBoolean(rs2, "nilai_penerimaan_vendor_farmasi_perbulan");
                        akses.akun_bayar_hutang=akses.getBoolean(rs2, "akun_bayar_hutang");
                        akses.master_rencana_keperawatan=akses.getBoolean(rs2, "master_rencana_keperawatan");
                        akses.laporan_tahunan_igd=akses.getBoolean(rs2, "laporan_tahunan_igd");
                        akses.obat_bhp_tidakbergerak=akses.getBoolean(rs2, "obat_bhp_tidakbergerak");
                        akses.ringkasan_hutang_vendor_farmasi=akses.getBoolean(rs2, "ringkasan_hutang_vendor_farmasi");
                        akses.nilai_penerimaan_vendor_nonmedis_perbulan=akses.getBoolean(rs2, "nilai_penerimaan_vendor_nonmedis_perbulan");
                        akses.ringkasan_hutang_vendor_nonmedis=akses.getBoolean(rs2, "ringkasan_hutang_vendor_nonmedis");
                        akses.anggota_polri_dirawat=akses.getBoolean(rs2, "anggota_polri_dirawat");
                        akses.daftar_pasien_ranap_polri=akses.getBoolean(rs2, "daftar_pasien_ranap_polri");
                        akses.soap_ralan_polri=akses.getBoolean(rs2, "soap_ralan_polri");
                        akses.soap_ranap_polri=akses.getBoolean(rs2, "soap_ranap_polri");
                        akses.laporan_penyakit_polri=akses.getBoolean(rs2, "laporan_penyakit_polri");
                        akses.master_rencana_keperawatan_anak=akses.getBoolean(rs2, "master_rencana_keperawatan_anak");
                        akses.jumlah_pengunjung_ralan_polri=akses.getBoolean(rs2, "jumlah_pengunjung_ralan_polri");
                        akses.catatan_observasi_igd=akses.getBoolean(rs2, "catatan_observasi_igd");
                        akses.catatan_observasi_ranap=akses.getBoolean(rs2, "catatan_observasi_ranap");
                        akses.catatan_observasi_ranap_kebidanan=akses.getBoolean(rs2, "catatan_observasi_ranap_kebidanan");
                        akses.catatan_observasi_ranap_postpartum=akses.getBoolean(rs2, "catatan_observasi_ranap_postpartum");
                        akses.penilaian_awal_medis_ralan_tht=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_tht");
                        akses.penilaian_psikologi=akses.getBoolean(rs2, "penilaian_psikologi");
                        akses.audit_cuci_tangan_medis=akses.getBoolean(rs2, "audit_cuci_tangan_medis");
                        akses.audit_pembuangan_limbah=akses.getBoolean(rs2, "audit_pembuangan_limbah");
                        akses.ruang_audit_kepatuhan=akses.getBoolean(rs2, "ruang_audit_kepatuhan");
                        akses.audit_pembuangan_benda_tajam=akses.getBoolean(rs2, "audit_pembuangan_benda_tajam");
                        akses.audit_penanganan_darah=akses.getBoolean(rs2, "audit_penanganan_darah");
                        akses.audit_pengelolaan_linen_kotor=akses.getBoolean(rs2, "audit_pengelolaan_linen_kotor");
                        akses.audit_penempatan_pasien=akses.getBoolean(rs2, "audit_penempatan_pasien");
                        akses.audit_kamar_jenazah=akses.getBoolean(rs2, "audit_kamar_jenazah");
                        akses.audit_bundle_iadp=akses.getBoolean(rs2, "audit_bundle_iadp");
                        akses.audit_bundle_ido=akses.getBoolean(rs2, "audit_bundle_ido");
                        akses.audit_fasilitas_kebersihan_tangan=akses.getBoolean(rs2, "audit_fasilitas_kebersihan_tangan");
                        akses.audit_fasilitas_apd=akses.getBoolean(rs2, "audit_fasilitas_apd");
                        akses.audit_pembuangan_limbah_cair_infeksius=akses.getBoolean(rs2, "audit_pembuangan_limbah_cair_infeksius");
                        akses.audit_sterilisasi_alat=akses.getBoolean(rs2, "audit_sterilisasi_alat");
                        akses.penilaian_awal_medis_ralan_psikiatri=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_psikiatri");
                        akses.persetujuan_penolakan_tindakan=akses.getBoolean(rs2, "persetujuan_penolakan_tindakan");
                        akses.audit_bundle_isk=akses.getBoolean(rs2, "audit_bundle_isk");
                        akses.audit_bundle_plabsi=akses.getBoolean(rs2, "audit_bundle_plabsi");
                        akses.audit_bundle_vap=akses.getBoolean(rs2, "audit_bundle_vap");
                        akses.akun_host_to_host_bank_papua=akses.getBoolean(rs2, "akun_host_to_host_bank_papua");
                        akses.pembayaran_bank_papua=akses.getBoolean(rs2, "pembayaran_bank_papua");
                        akses.penilaian_awal_medis_ralan_penyakit_dalam=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_penyakit_dalam");
                        akses.penilaian_awal_medis_ralan_mata=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_mata");
                        akses.penilaian_awal_medis_ralan_neurologi=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_neurologi");
                        akses.sirkulasi_obat6=akses.getBoolean(rs2, "sirkulasi_obat6");
                        akses.penilaian_awal_medis_ralan_orthopedi=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_orthopedi");
                        akses.penilaian_awal_medis_ralan_bedah=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_bedah");
                        akses.integrasi_khanza_health_services=akses.getBoolean(rs2, "integrasi_khanza_health_services");
                        akses.soap_ralan_tni=akses.getBoolean(rs2, "soap_ralan_tni");
                        akses.soap_ranap_tni=akses.getBoolean(rs2, "soap_ranap_tni");
                        akses.jumlah_pengunjung_ralan_tni=akses.getBoolean(rs2, "jumlah_pengunjung_ralan_tni");
                        akses.laporan_penyakit_tni=akses.getBoolean(rs2, "laporan_penyakit_tni");
                        akses.catatan_keperawatan_ranap=akses.getBoolean(rs2, "catatan_keperawatan_ranap");
                        akses.master_rencana_keperawatan_gigi=akses.getBoolean(rs2, "master_rencana_keperawatan_gigi");
                        akses.master_rencana_keperawatan_mata=akses.getBoolean(rs2, "master_rencana_keperawatan_mata");
                        akses.master_rencana_keperawatan_igd=akses.getBoolean(rs2, "master_rencana_keperawatan_igd");
                        akses.master_masalah_keperawatan_psikiatri=akses.getBoolean(rs2, "master_masalah_keperawatan_psikiatri");
                        akses.master_rencana_keperawatan_psikiatri=akses.getBoolean(rs2, "master_rencana_keperawatan_psikiatri");
                        akses.penilaian_awal_keperawatan_psikiatri=akses.getBoolean(rs2, "penilaian_awal_keperawatan_psikiatri");
                        akses.pemantauan_pews_anak=akses.getBoolean(rs2, "pemantauan_pews_anak");
                        akses.surat_pulang_atas_permintaan_sendiri=akses.getBoolean(rs2, "surat_pulang_atas_permintaan_sendiri");
                        akses.template_hasil_radiologi=akses.getBoolean(rs2, "template_hasil_radiologi");
                        akses.laporan_bulanan_irj=akses.getBoolean(rs2, "laporan_bulanan_irj");
                        akses.template_pemeriksaan=akses.getBoolean(rs2, "template_pemeriksaan");
                        akses.pemeriksaan_lab_mb=akses.getBoolean(rs2, "pemeriksaan_lab_mb");
                        akses.ubah_petugas_lab_mb=akses.getBoolean(rs2, "ubah_petugas_lab_mb");
                        akses.penilaian_pre_operasi=akses.getBoolean(rs2, "penilaian_pre_operasi");
                        akses.penilaian_pre_anestesi=akses.getBoolean(rs2, "penilaian_pre_anestesi");
                        akses.perencanaan_pemulangan=akses.getBoolean(rs2, "perencanaan_pemulangan");
                        akses.penilaian_lanjutan_resiko_jatuh_dewasa=akses.getBoolean(rs2, "penilaian_lanjutan_resiko_jatuh_dewasa");
                        akses.penilaian_lanjutan_resiko_jatuh_anak=akses.getBoolean(rs2, "penilaian_lanjutan_resiko_jatuh_anak");
                        akses.penilaian_awal_medis_ralan_geriatri=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_geriatri");
                        akses.penilaian_tambahan_pasien_geriatri=akses.getBoolean(rs2, "penilaian_tambahan_pasien_geriatri");
                        akses.skrining_nutrisi_dewasa=akses.getBoolean(rs2, "skrining_nutrisi_dewasa");
                        akses.skrining_nutrisi_lansia=akses.getBoolean(rs2, "skrining_nutrisi_lansia");
                        akses.hasil_pemeriksaan_usg=akses.getBoolean(rs2, "hasil_pemeriksaan_usg");
                        akses.skrining_nutrisi_anak=akses.getBoolean(rs2, "skrining_nutrisi_anak");
                        akses.akun_host_to_host_bank_jabar=akses.getBoolean(rs2, "akun_host_to_host_bank_jabar");
                        akses.pembayaran_bank_jabar=akses.getBoolean(rs2, "pembayaran_bank_jabar");
                        akses.surat_pernyataan_pasien_umum=akses.getBoolean(rs2, "surat_pernyataan_pasien_umum");
                        akses.konseling_farmasi=akses.getBoolean(rs2, "konseling_farmasi");
                        akses.pelayanan_informasi_obat=akses.getBoolean(rs2, "pelayanan_informasi_obat");
                        akses.jawaban_pio_apoteker=akses.getBoolean(rs2, "jawaban_pio_apoteker");
                        akses.surat_persetujuan_umum=akses.getBoolean(rs2, "surat_persetujuan_umum");
                        akses.transfer_pasien_antar_ruang=akses.getBoolean(rs2, "transfer_pasien_antar_ruang");
                        akses.satu_sehat_referensi_dokter=akses.getBoolean(rs2, "satu_sehat_referensi_dokter");
                        akses.satu_sehat_referensi_pasien=akses.getBoolean(rs2, "satu_sehat_referensi_pasien");
                        akses.satu_sehat_mapping_departemen=akses.getBoolean(rs2, "satu_sehat_mapping_departemen");
                        akses.satu_sehat_mapping_lokasi=akses.getBoolean(rs2, "satu_sehat_mapping_lokasi");
                        akses.satu_sehat_kirim_encounter=akses.getBoolean(rs2, "satu_sehat_kirim_encounter");
                        akses.catatan_cek_gds=akses.getBoolean(rs2, "catatan_cek_gds");
                        akses.satu_sehat_kirim_condition=akses.getBoolean(rs2, "satu_sehat_kirim_condition");
                        akses.checklist_pre_operasi=akses.getBoolean(rs2, "checklist_pre_operasi");
                        akses.satu_sehat_kirim_observationttv=akses.getBoolean(rs2, "satu_sehat_kirim_observationttv");
                        akses.signin_sebelum_anestesi=akses.getBoolean(rs2, "signin_sebelum_anestesi");
                        akses.satu_sehat_kirim_procedure=akses.getBoolean(rs2, "satu_sehat_kirim_procedure");
                        akses.operasi_per_bulan=akses.getBoolean(rs2, "operasi_per_bulan");
                        akses.timeout_sebelum_insisi=akses.getBoolean(rs2, "timeout_sebelum_insisi");
                        akses.signout_sebelum_menutup_luka=akses.getBoolean(rs2, "signout_sebelum_menutup_luka");
                        akses.dapur_barang=akses.getBoolean(rs2, "dapur_barang");
                        akses.dapur_opname=akses.getBoolean(rs2, "dapur_opname");
                        akses.satu_sehat_mapping_vaksin=akses.getBoolean(rs2, "satu_sehat_mapping_vaksin");
                        akses.dapur_suplier=akses.getBoolean(rs2, "dapur_suplier");
                        akses.satu_sehat_kirim_Immunization=akses.getBoolean(rs2, "satu_sehat_kirim_Immunization");
                        akses.checklist_post_operasi=akses.getBoolean(rs2, "checklist_post_operasi");
                        akses.dapur_pembelian=akses.getBoolean(rs2, "dapur_pembelian");
                        akses.dapur_stok_keluar=akses.getBoolean(rs2, "dapur_stok_keluar");
                        akses.dapur_riwayat_barang=akses.getBoolean(rs2, "dapur_riwayat_barang");
                        akses.permintaan_dapur=akses.getBoolean(rs2, "permintaan_dapur");
                        akses.rekonsiliasi_obat=akses.getBoolean(rs2, "rekonsiliasi_obat");
                        akses.biaya_pengadaan_dapur=akses.getBoolean(rs2, "biaya_pengadaan_dapur");
                        akses.rekap_pengadaan_dapur=akses.getBoolean(rs2, "rekap_pengadaan_dapur");
                        akses.kesling_limbah_b3medis_cair=akses.getBoolean(rs2, "kesling_limbah_b3medis_cair");
                        akses.grafik_limbahb3cair_pertanggal=akses.getBoolean(rs2, "grafik_limbahb3cair_pertanggal");
                        akses.grafik_limbahb3cair_perbulan=akses.getBoolean(rs2, "grafik_limbahb3cair_perbulan");
                        akses.rekap_biaya_registrasi=akses.getBoolean(rs2, "rekap_biaya_registrasi");
                        akses.konfirmasi_rekonsiliasi_obat=akses.getBoolean(rs2, "konfirmasi_rekonsiliasi_obat");
                        akses.satu_sehat_kirim_clinicalimpression=akses.getBoolean(rs2, "satu_sehat_kirim_clinicalimpression");
                        akses.penilaian_pasien_terminal=akses.getBoolean(rs2, "penilaian_pasien_terminal");
                        akses.surat_persetujuan_rawat_inap=akses.getBoolean(rs2, "surat_persetujuan_rawat_inap");
                        akses.monitoring_reaksi_tranfusi=akses.getBoolean(rs2, "monitoring_reaksi_tranfusi");
                        akses.penilaian_korban_kekerasan=akses.getBoolean(rs2, "penilaian_korban_kekerasan");
                        akses.penilaian_lanjutan_resiko_jatuh_lansia=akses.getBoolean(rs2, "penilaian_lanjutan_resiko_jatuh_lansia");
                        akses.mpp_skrining=akses.getBoolean(rs2, "mpp_skrining");
                        akses.penilaian_pasien_penyakit_menular=akses.getBoolean(rs2, "penilaian_pasien_penyakit_menular");
                        akses.edukasi_pasien_keluarga_rj=akses.getBoolean(rs2, "edukasi_pasien_keluarga_rj");
                        akses.pemantauan_pews_dewasa=akses.getBoolean(rs2, "pemantauan_pews_dewasa");
                        akses.penilaian_tambahan_bunuh_diri=akses.getBoolean(rs2, "penilaian_tambahan_bunuh_diri");
                        akses.bpjs_antrean_pertanggal=akses.getBoolean(rs2, "bpjs_antrean_pertanggal");
                        akses.penilaian_tambahan_perilaku_kekerasan=akses.getBoolean(rs2, "penilaian_tambahan_perilaku_kekerasan");
                        akses.penilaian_tambahan_beresiko_melarikan_diri=akses.getBoolean(rs2, "penilaian_tambahan_beresiko_melarikan_diri");
                        akses.persetujuan_penundaan_pelayanan=akses.getBoolean(rs2, "persetujuan_penundaan_pelayanan");
                        akses.sisa_diet_pasien=akses.getBoolean(rs2, "sisa_diet_pasien");
                        akses.penilaian_awal_medis_ralan_bedah_mulut=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_bedah_mulut");
                        akses.penilaian_pasien_keracunan=akses.getBoolean(rs2, "penilaian_pasien_keracunan");
                        akses.pemantauan_meows_obstetri=akses.getBoolean(rs2, "pemantauan_meows_obstetri");
                        akses.catatan_adime_gizi=akses.getBoolean(rs2, "catatan_adime_gizi");
                        akses.pengajuan_biaya=akses.getBoolean(rs2, "pengajuan_biaya");
                        akses.penilaian_awal_keperawatan_ralan_geriatri=akses.getBoolean(rs2, "penilaian_awal_keperawatan_ralan_geriatri");
                        akses.master_masalah_keperawatan_geriatri=akses.getBoolean(rs2, "master_masalah_keperawatan_geriatri");
                        akses.master_rencana_keperawatan_geriatri=akses.getBoolean(rs2, "master_rencana_keperawatan_geriatri");
                        akses.checklist_kriteria_masuk_hcu=akses.getBoolean(rs2, "checklist_kriteria_masuk_hcu");
                        akses.checklist_kriteria_keluar_hcu=akses.getBoolean(rs2, "checklist_kriteria_keluar_hcu");
                        akses.penilaian_risiko_dekubitus=akses.getBoolean(rs2, "penilaian_risiko_dekubitus");
                        akses.master_menolak_anjuran_medis=akses.getBoolean(rs2, "master_menolak_anjuran_medis");
                        akses.penolakan_anjuran_medis=akses.getBoolean(rs2, "penolakan_anjuran_medis");
                        akses.laporan_tahunan_penolakan_anjuran_medis=akses.getBoolean(rs2, "laporan_tahunan_penolakan_anjuran_medis");
                        akses.template_laporan_operasi=akses.getBoolean(rs2, "template_laporan_operasi");
                        akses.hasil_tindakan_eswl=akses.getBoolean(rs2, "hasil_tindakan_eswl");
                        akses.checklist_kriteria_masuk_icu=akses.getBoolean(rs2, "checklist_kriteria_masuk_icu");
                        akses.checklist_kriteria_keluar_icu=akses.getBoolean(rs2, "checklist_kriteria_keluar_icu");
                        akses.akses_dokter_lain_rawat_jalan=akses.getBoolean(rs2, "akses_dokter_lain_rawat_jalan");
                        akses.follow_up_dbd=akses.getBoolean(rs2, "follow_up_dbd");
                        akses.penilaian_risiko_jatuh_neonatus=akses.getBoolean(rs2, "penilaian_risiko_jatuh_neonatus");
                        akses.persetujuan_pengajuan_biaya=akses.getBoolean(rs2, "persetujuan_pengajuan_biaya");
                        akses.pemeriksaan_fisik_ralan_per_penyakit=akses.getBoolean(rs2, "pemeriksaan_fisik_ralan_per_penyakit");
                        akses.penilaian_lanjutan_resiko_jatuh_geriatri=akses.getBoolean(rs2, "penilaian_lanjutan_resiko_jatuh_geriatri");
                        akses.pemantauan_ews_neonatus=akses.getBoolean(rs2, "pemantauan_ews_neonatus");
                        akses.validasi_persetujuan_pengajuan_biaya=akses.getBoolean(rs2, "validasi_persetujuan_pengajuan_biaya");
                        akses.riwayat_perawatan_icare_bpjs=akses.getBoolean(rs2, "riwayat_perawatan_icare_bpjs");
                        akses.rekap_pengajuan_biaya=akses.getBoolean(rs2, "rekap_pengajuan_biaya");
                        akses.penilaian_awal_medis_ralan_kulit_kelamin=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_kulit_kelamin");
                        akses.akun_host_to_host_bank_mandiri=akses.getBoolean(rs2, "akun_host_to_host_bank_mandiri");
                        akses.penilaian_medis_hemodialisa=akses.getBoolean(rs2, "penilaian_medis_hemodialisa");
                        akses.penilaian_level_kecemasan_ranap_anak=akses.getBoolean(rs2, "penilaian_level_kecemasan_ranap_anak");
                        akses.penilaian_lanjutan_resiko_jatuh_psikiatri=akses.getBoolean(rs2, "penilaian_lanjutan_resiko_jatuh_psikiatri");
                        akses.penilaian_lanjutan_skrining_fungsional=akses.getBoolean(rs2, "penilaian_lanjutan_skrining_fungsional");
                        akses.penilaian_medis_ralan_rehab_medik=akses.getBoolean(rs2, "penilaian_medis_ralan_rehab_medik");
                        akses.laporan_anestesi=akses.getBoolean(rs2, "laporan_anestesi");
                        akses.template_persetujuan_penolakan_tindakan=akses.getBoolean(rs2, "template_persetujuan_penolakan_tindakan");
                        akses.penilaian_medis_ralan_gawat_darurat_psikiatri=akses.getBoolean(rs2, "penilaian_medis_ralan_gawat_darurat_psikiatri");
                        akses.bpjs_referensi_setting_apotek=akses.getBoolean(rs2, "bpjs_referensi_setting_apotek");
                        akses.bpjs_referensi_obat_apotek=akses.getBoolean(rs2, "bpjs_referensi_obat_apotek");
                        akses.bpjs_mapping_obat_apotek=akses.getBoolean(rs2, "bpjs_mapping_obat_apotek");
                        akses.pembayaran_bank_mandiri=akses.getBoolean(rs2, "pembayaran_bank_mandiri");
                        akses.penilaian_ulang_nyeri=akses.getBoolean(rs2, "penilaian_ulang_nyeri");
                        akses.penilaian_terapi_wicara=akses.getBoolean(rs2, "penilaian_terapi_wicara");
                        akses.bpjs_obat_23hari_apotek=akses.getBoolean(rs2, "bpjs_obat_23hari_apotek");
                        akses.pengkajian_restrain=akses.getBoolean(rs2, "pengkajian_restrain");
                        akses.bpjs_kunjungan_sep_apotek=akses.getBoolean(rs2, "bpjs_kunjungan_sep_apotek");
                        akses.bpjs_monitoring_klaim_apotek=akses.getBoolean(rs2, "bpjs_monitoring_klaim_apotek");
                        akses.bpjs_daftar_pelayanan_obat_apotek=akses.getBoolean(rs2, "bpjs_daftar_pelayanan_obat_apotek");
                        akses.penilaian_awal_medis_ralan_paru=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_paru");
                        akses.catatan_keperawatan_ralan=akses.getBoolean(rs2, "catatan_keperawatan_ralan");
                        akses.catatan_persalinan=akses.getBoolean(rs2, "catatan_persalinan");
                        akses.skor_aldrette_pasca_anestesi=akses.getBoolean(rs2, "skor_aldrette_pasca_anestesi");
                        akses.skor_steward_pasca_anestesi=akses.getBoolean(rs2, "skor_steward_pasca_anestesi");
                        akses.skor_bromage_pasca_anestesi=akses.getBoolean(rs2, "skor_bromage_pasca_anestesi");
                        akses.penilaian_pre_induksi=akses.getBoolean(rs2, "penilaian_pre_induksi");
                        akses.hasil_usg_urologi=akses.getBoolean(rs2, "hasil_usg_urologi");
                        akses.hasil_usg_gynecologi=akses.getBoolean(rs2, "hasil_usg_gynecologi");
                        akses.hasil_pemeriksaan_ekg=akses.getBoolean(rs2, "hasil_pemeriksaan_ekg");
                        akses.hapus_edit_sep_bpjs=akses.getBoolean(rs2, "hapus_edit_sep_bpjs");
                        akses.satu_sehat_kirim_diet=akses.getBoolean(rs2, "satu_sehat_kirim_diet");
                        akses.satu_sehat_mapping_obat=akses.getBoolean(rs2, "satu_sehat_mapping_obat");
                        akses.dapur_ringkasan_pembelian=akses.getBoolean(rs2, "dapur_ringkasan_pembelian");
                        akses.satu_sehat_kirim_medication=akses.getBoolean(rs2, "satu_sehat_kirim_medication");
                        akses.satu_sehat_kirim_medicationrequest=akses.getBoolean(rs2, "satu_sehat_kirim_medicationrequest");
                        akses.penatalaksanaan_terapi_okupasi=akses.getBoolean(rs2, "penatalaksanaan_terapi_okupasi");
                        akses.satu_sehat_kirim_medicationdispense=akses.getBoolean(rs2, "satu_sehat_kirim_medicationdispense");
                        akses.edit_hapus_spo_medis=akses.getBoolean(rs2, "edit_hapus_spo_medis");
                        akses.edit_hapus_spo_nonmedis=akses.getBoolean(rs2, "edit_hapus_spo_nonmedis");
                        akses.hasil_usg_neonatus=akses.getBoolean(rs2, "hasil_usg_neonatus");
                        akses.hasil_endoskopi_faring_laring=akses.getBoolean(rs2, "hasil_endoskopi_faring_laring");
                        akses.satu_sehat_mapping_radiologi=akses.getBoolean(rs2, "satu_sehat_mapping_radiologi");
                        akses.satu_sehat_kirim_servicerequest_radiologi=akses.getBoolean(rs2, "satu_sehat_kirim_servicerequest_radiologi");
                        akses.hasil_endoskopi_hidung=akses.getBoolean(rs2, "hasil_endoskopi_hidung");
                        akses.satu_sehat_kirim_specimen_radiologi=akses.getBoolean(rs2, "satu_sehat_kirim_specimen_radiologi");
                        akses.bpjs_kompilasi_berkas_klaim=akses.getBoolean(rs2, "bpjs_kompilasi_berkas_klaim");
                        akses.master_masalah_keperawatan_neonatus=akses.getBoolean(rs2, "master_masalah_keperawatan_neonatus");
                        akses.master_rencana_keperawatan_neonatus=akses.getBoolean(rs2, "master_rencana_keperawatan_neonatus");
                        akses.penilaian_awal_keperawatan_ranap_neonatus=akses.getBoolean(rs2, "penilaian_awal_keperawatan_ranap_neonatus");
                        akses.satu_sehat_kirim_observation_radiologi=akses.getBoolean(rs2, "satu_sehat_kirim_observation_radiologi");
                        akses.satu_sehat_kirim_diagnosticreport_radiologi=akses.getBoolean(rs2, "satu_sehat_kirim_diagnosticreport_radiologi");
                        akses.hasil_endoskopi_telinga=akses.getBoolean(rs2, "hasil_endoskopi_telinga");
                        akses.satu_sehat_mapping_lab=akses.getBoolean(rs2, "satu_sehat_mapping_lab");
                        akses.satu_sehat_kirim_servicerequest_lab=akses.getBoolean(rs2, "satu_sehat_kirim_servicerequest_lab");
                        akses.satu_sehat_kirim_servicerequest_labmb=akses.getBoolean(rs2, "satu_sehat_kirim_servicerequest_labmb");
                        akses.satu_sehat_kirim_specimen_lab=akses.getBoolean(rs2, "satu_sehat_kirim_specimen_lab");
                        akses.satu_sehat_kirim_specimen_labmb=akses.getBoolean(rs2, "satu_sehat_kirim_specimen_labmb");
                        akses.satu_sehat_kirim_observation_lab=akses.getBoolean(rs2, "satu_sehat_kirim_observation_lab");
                        akses.satu_sehat_kirim_observation_labmb=akses.getBoolean(rs2, "satu_sehat_kirim_observation_labmb");
                        akses.satu_sehat_kirim_diagnosticreport_lab=akses.getBoolean(rs2, "satu_sehat_kirim_diagnosticreport_lab");
                        akses.satu_sehat_kirim_diagnosticreport_labmb=akses.getBoolean(rs2, "satu_sehat_kirim_diagnosticreport_labmb");
                        akses.kepatuhan_kelengkapan_keselamatan_bedah=akses.getBoolean(rs2, "kepatuhan_kelengkapan_keselamatan_bedah");
                        akses.nilai_piutang_perjenis_bayar_per_bulan=akses.getBoolean(rs2, "nilai_piutang_perjenis_bayar_per_bulan");
                        akses.ringkasan_piutang_jenis_bayar=akses.getBoolean(rs2, "ringkasan_piutang_jenis_bayar");
                        akses.penilaian_pasien_imunitas_rendah=akses.getBoolean(rs2, "penilaian_pasien_imunitas_rendah");
                        akses.balance_cairan=akses.getBoolean(rs2, "balance_cairan");
                        akses.catatan_observasi_chbp=akses.getBoolean(rs2, "catatan_observasi_chbp");
                        akses.catatan_observasi_induksi_persalinan=akses.getBoolean(rs2, "catatan_observasi_induksi_persalinan");
                        akses.skp_kategori_penilaian=akses.getBoolean(rs2, "skp_kategori_penilaian");
                        akses.skp_kriteria_penilaian=akses.getBoolean(rs2, "skp_kriteria_penilaian");
                        akses.skp_penilaian=akses.getBoolean(rs2, "skp_penilaian");
                        akses.referensi_poli_mobilejknfktp=akses.getBoolean(rs2, "referensi_poli_mobilejknfktp");
                        akses.referensi_dokter_mobilejknfktp=akses.getBoolean(rs2, "referensi_dokter_mobilejknfktp");
                        akses.skp_rekapitulasi_penilaian=akses.getBoolean(rs2, "skp_rekapitulasi_penilaian");
                        akses.pembayaran_pihak_ke3_bankmandiri=akses.getBoolean(rs2, "pembayaran_pihak_ke3_bankmandiri");
                        akses.metode_pembayaran_bankmandiri=akses.getBoolean(rs2, "metode_pembayaran_bankmandiri");
                        akses.bank_tujuan_transfer_bankmandiri=akses.getBoolean(rs2, "bank_tujuan_transfer_bankmandiri");
                        akses.kodetransaksi_tujuan_transfer_bankmandiri=akses.getBoolean(rs2, "kodetransaksi_tujuan_transfer_bankmandiri");
                        akses.konsultasi_medik=akses.getBoolean(rs2, "konsultasi_medik");
                        akses.jawaban_konsultasi_medik=akses.getBoolean(rs2, "jawaban_konsultasi_medik");
                        akses.pcare_cek_alergi=akses.getBoolean(rs2, "pcare_cek_alergi");
                        akses.pcare_cek_prognosa=akses.getBoolean(rs2, "pcare_cek_prognosa");
                        akses.data_sasaran_usiaproduktif=akses.getBoolean(rs2, "data_sasaran_usiaproduktif");
                        akses.data_sasaran_usialansia=akses.getBoolean(rs2, "data_sasaran_usialansia");
                        akses.skrining_perilaku_merokok_sekolah_remaja=akses.getBoolean(rs2, "skrining_perilaku_merokok_sekolah_remaja");
                        akses.skrining_kekerasan_pada_perempuan=akses.getBoolean(rs2, "skrining_kekerasan_pada_perempuan");
                        akses.skrining_obesitas=akses.getBoolean(rs2, "skrining_obesitas");
                        akses.skrining_risiko_kanker_payudara=akses.getBoolean(rs2, "skrining_risiko_kanker_payudara");
                        akses.skrining_risiko_kanker_paru=akses.getBoolean(rs2, "skrining_risiko_kanker_paru");
                        akses.skrining_tbc=akses.getBoolean(rs2, "skrining_tbc");
                        akses.skrining_kesehatan_gigi_mulut_remaja=akses.getBoolean(rs2, "skrining_kesehatan_gigi_mulut_remaja");
                        akses.penilaian_awal_keperawatan_ranap_bayi=akses.getBoolean(rs2, "penilaian_awal_keperawatan_ranap_bayi");
                        akses.booking_mcu_perusahaan=akses.getBoolean(rs2, "booking_mcu_perusahaan");
                        akses.catatan_observasi_restrain_nonfarma=akses.getBoolean(rs2, "catatan_observasi_restrain_nonfarma");
                        akses.catatan_observasi_ventilator=akses.getBoolean(rs2, "catatan_observasi_ventilator");
                        akses.catatan_anestesi_sedasi=akses.getBoolean(rs2, "catatan_anestesi_sedasi");
                        akses.skrining_puma=akses.getBoolean(rs2, "skrining_puma");
                        akses.satu_sehat_kirim_careplan=akses.getBoolean(rs2, "satu_sehat_kirim_careplan");
                        akses.satu_sehat_kirim_medicationstatement=akses.getBoolean(rs2, "satu_sehat_kirim_medicationstatement");
                        akses.skrining_adiksi_nikotin=akses.getBoolean(rs2, "skrining_adiksi_nikotin");
                        akses.skrining_thalassemia=akses.getBoolean(rs2, "skrining_thalassemia");
                        akses.skrining_instrumen_sdq=akses.getBoolean(rs2, "skrining_instrumen_sdq");
                        akses.skrining_instrumen_srq=akses.getBoolean(rs2, "skrining_instrumen_srq");
                        akses.checklist_pemberian_fibrinolitik=akses.getBoolean(rs2, "checklist_pemberian_fibrinolitik");
                        akses.skrining_kanker_kolorektal=akses.getBoolean(rs2, "skrining_kanker_kolorektal");
                        akses.dapur_pemesanan=akses.getBoolean(rs2, "dapur_pemesanan");
                        akses.bayar_pesan_dapur=akses.getBoolean(rs2, "bayar_pesan_dapur");
                        akses.hutang_dapur=akses.getBoolean(rs2, "hutang_dapur");
                        akses.titip_faktur_dapur=akses.getBoolean(rs2, "titip_faktur_dapur");
                        akses.validasi_tagihan_dapur=akses.getBoolean(rs2, "validasi_tagihan_dapur");
                        akses.surat_pemesanan_dapur=akses.getBoolean(rs2, "surat_pemesanan_dapur");
                        akses.pengajuan_barang_dapur=akses.getBoolean(rs2, "pengajuan_barang_dapur");
                        akses.dapur_returbeli=akses.getBoolean(rs2, "dapur_returbeli");
                        akses.hibah_dapur=akses.getBoolean(rs2, "hibah_dapur");
                        akses.ringkasan_penerimaan_dapur=akses.getBoolean(rs2, "ringkasan_penerimaan_dapur");
                        akses.ringkasan_pengajuan_dapur=akses.getBoolean(rs2, "ringkasan_pengajuan_dapur");
                        akses.ringkasan_pemesanan_dapur=akses.getBoolean(rs2, "ringkasan_pemesanan_dapur");
                        akses.ringkasan_returbeli_dapur=akses.getBoolean(rs2, "ringkasan_returbeli_dapur");
                        akses.ringkasan_stokkeluar_dapur=akses.getBoolean(rs2, "ringkasan_stokkeluar_dapur");
                        akses.dapur_stokkeluar_pertanggal=akses.getBoolean(rs2, "dapur_stokkeluar_pertanggal");
                        akses.sirkulasi_dapur=akses.getBoolean(rs2, "sirkulasi_dapur");
                        akses.sirkulasi_dapur2=akses.getBoolean(rs2, "sirkulasi_dapur2");
                        akses.verifikasi_penerimaan_dapur=akses.getBoolean(rs2, "verifikasi_penerimaan_dapur");
                        akses.nilai_penerimaan_vendor_dapur_perbulan=akses.getBoolean(rs2, "nilai_penerimaan_vendor_dapur_perbulan");
                        akses.ringkasan_hutang_vendor_dapur=akses.getBoolean(rs2, "ringkasan_hutang_vendor_dapur");
                        akses.pindah_kamar_pilihan_2=akses.getBoolean(rs2, "pindah_kamar_pilihan_2");
                        akses.penilaian_psikologi_klinis=akses.getBoolean(rs2, "penilaian_psikologi_klinis");
                        akses.penilaian_awal_medis_ranap_neonatus=akses.getBoolean(rs2, "penilaian_awal_medis_ranap_neonatus");
                        akses.penilaian_derajat_dehidrasi=akses.getBoolean(rs2, "penilaian_derajat_dehidrasi");
                        akses.ringkasan_jasa_tindakan_medis=akses.getBoolean(rs2, "ringkasan_jasa_tindakan_medis");
                        akses.pendapatan_per_akun=akses.getBoolean(rs2, "pendapatan_per_akun");
                        akses.hasil_pemeriksaan_echo=akses.getBoolean(rs2, "hasil_pemeriksaan_echo");
                        akses.penilaian_bayi_baru_lahir=akses.getBoolean(rs2, "penilaian_bayi_baru_lahir");
                        akses.rl1_3_ketersediaan_kamar=akses.getBoolean(rs2, "rl1_3_ketersediaan_kamar");
                        akses.pendapatan_per_akun_closing=akses.getBoolean(rs2, "pendapatan_per_akun_closing");
                        akses.pengeluaran_pengeluaran=akses.getBoolean(rs2, "pengeluaran_pengeluaran");
                        akses.skrining_diabetes_melitus=akses.getBoolean(rs2, "skrining_diabetes_melitus");
                        akses.laporan_tindakan=akses.getBoolean(rs2, "laporan_tindakan");
                        akses.pelaksanaan_informasi_edukasi=akses.getBoolean(rs2, "pelaksanaan_informasi_edukasi");
                        akses.layanan_kedokteran_fisik_rehabilitasi=akses.getBoolean(rs2, "layanan_kedokteran_fisik_rehabilitasi");
                        akses.skrining_kesehatan_gigi_mulut_balita=akses.getBoolean(rs2, "skrining_kesehatan_gigi_mulut_balita");
                        akses.skrining_anemia=akses.getBoolean(rs2, "skrining_anemia");
                        akses.layanan_program_kfr=akses.getBoolean(rs2, "layanan_program_kfr");
                        akses.skrining_hipertensi=akses.getBoolean(rs2, "skrining_hipertensi");
                        akses.skrining_kesehatan_penglihatan=akses.getBoolean(rs2, "skrining_kesehatan_penglihatan");
                        akses.catatan_observasi_hemodialisa=akses.getBoolean(rs2, "catatan_observasi_hemodialisa");
                        akses.skrining_kesehatan_gigi_mulut_dewasa=akses.getBoolean(rs2, "skrining_kesehatan_gigi_mulut_dewasa");
                        akses.skrining_risiko_kanker_serviks=akses.getBoolean(rs2, "skrining_risiko_kanker_serviks");
                        akses.catatan_cairan_hemodialisa=akses.getBoolean(rs2, "catatan_cairan_hemodialisa");
                        akses.skrining_kesehatan_gigi_mulut_lansia=akses.getBoolean(rs2, "skrining_kesehatan_gigi_mulut_lansia");
                        akses.skrining_indra_pendengaran=akses.getBoolean(rs2, "skrining_indra_pendengaran");
                        akses.catatan_pengkajian_paska_operasi=akses.getBoolean(rs2, "catatan_pengkajian_paska_operasi");
                        akses.skrining_frailty_syndrome=akses.getBoolean(rs2, "skrining_frailty_syndrome");
                        akses.sirkulasi_cssd=akses.getBoolean(rs2, "sirkulasi_cssd");
                        akses.lama_pelayanan_cssd=akses.getBoolean(rs2, "lama_pelayanan_cssd");
                        akses.catatan_observasi_bayi=akses.getBoolean(rs2, "catatan_observasi_bayi");
                        akses.riwayat_surat_peringatan=akses.getBoolean(rs2, "riwayat_surat_peringatan");
                        akses.master_kesimpulan_anjuran_mcu=akses.getBoolean(rs2, "master_kesimpulan_anjuran_mcu");
                        akses.kategori_piutang_jasa_perusahaan=akses.getBoolean(rs2, "kategori_piutang_jasa_perusahaan");
                        akses.piutang_jasa_perusahaan=akses.getBoolean(rs2, "piutang_jasa_perusahaan");
                        akses.bayar_piutang_jasa_perusahaan=akses.getBoolean(rs2, "bayar_piutang_jasa_perusahaan");
                        akses.piutang_jasa_perusahaan_belum_lunas=akses.getBoolean(rs2, "piutang_jasa_perusahaan_belum_lunas");
                        akses.checklist_kesiapan_anestesi=akses.getBoolean(rs2, "checklist_kesiapan_anestesi");
                        akses.piutang_peminjaman_uang_belum_lunas=akses.getBoolean(rs2, "piutang_peminjaman_uang_belum_lunas");
                        akses.hasil_pemeriksaan_slit_lamp=akses.getBoolean(rs2, "hasil_pemeriksaan_slit_lamp");
                        akses.hasil_pemeriksaan_oct=akses.getBoolean(rs2, "hasil_pemeriksaan_oct");
                        akses.beban_hutang_lain=akses.getBoolean(rs2, "beban_hutang_lain");
                        akses.poli_asal_pasien_ranap=akses.getBoolean(rs2, "poli_asal_pasien_ranap");
                        akses.pemberi_hutang_lain=akses.getBoolean(rs2, "pemberi_hutang_lain");
                        akses.dokter_asal_pasien_ranap=akses.getBoolean(rs2, "dokter_asal_pasien_ranap");
                        akses.duta_parkir_rekap_keluar=akses.getBoolean(rs2, "duta_parkir_rekap_keluar");
                        akses.surat_keterangan_layak_terbang=akses.getBoolean(rs2, "surat_keterangan_layak_terbang");
                        akses.bayar_beban_hutang_lain=akses.getBoolean(rs2, "bayar_beban_hutang_lain");
                        akses.surat_persetujuan_pemeriksaan_hiv=akses.getBoolean(rs2, "surat_persetujuan_pemeriksaan_hiv");
                        akses.skrining_instrumen_acrs=akses.getBoolean(rs2, "skrining_instrumen_acrs");
                        akses.surat_pernyataan_memilih_dpjp=akses.getBoolean(rs2, "surat_pernyataan_memilih_dpjp");
                        akses.skrining_instrumen_mental_emosional=akses.getBoolean(rs2, "skrining_instrumen_mental_emosional");
                        akses.pelanggan_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "pelanggan_lab_kesehatan_lingkungan");
                        akses.kriteria_masuk_nicu=akses.getBoolean(rs2, "kriteria_masuk_nicu");
                        akses.kriteria_keluar_nicu=akses.getBoolean(rs2, "kriteria_keluar_nicu");
                        akses.penilaian_medis_ranap_psikiatrik=akses.getBoolean(rs2, "penilaian_medis_ranap_psikiatrik");
                        akses.kriteria_masuk_picu=akses.getBoolean(rs2, "kriteria_masuk_picu");
                        akses.kriteria_keluar_picu=akses.getBoolean(rs2, "kriteria_keluar_picu");
                        akses.master_sampel_bakumutu=akses.getBoolean(rs2, "master_sampel_bakumutu");
                        akses.skrining_instrumen_amt=akses.getBoolean(rs2, "skrining_instrumen_amt");
                        akses.parameter_pengujian_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "parameter_pengujian_lab_kesehatan_lingkungan");
                        akses.bpjs_kirim_obat_smc=akses.getBoolean(rs2, "bpjs_kirim_obat_smc");
                        akses.bpjs_edit_kirim_obat_smc=akses.getBoolean(rs2, "bpjs_edit_kirim_obat_smc");
                        akses.bpjs_riwayat_obat_smc=akses.getBoolean(rs2, "bpjs_riwayat_obat_smc");
                        akses.bpjs_riwayat_pelayanan_resep_smc=akses.getBoolean(rs2, "bpjs_riwayat_pelayanan_resep_smc");
                        akses.nilai_normal_baku_mutu_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "nilai_normal_baku_mutu_lab_kesehatan_lingkungan");
                        akses.skrining_pneumonia_severity_index=akses.getBoolean(rs2, "skrining_pneumonia_severity_index");
                        akses.permintaan_pengujian_sampel_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "permintaan_pengujian_sampel_lab_kesehatan_lingkungan");
                        akses.penilaian_awal_medis_ralan_jantung=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_jantung");
                        akses.penilaian_awal_medis_ralan_urologi=akses.getBoolean(rs2, "penilaian_awal_medis_ralan_urologi");
                        akses.hasil_pemeriksaan_treadmill=akses.getBoolean(rs2, "hasil_pemeriksaan_treadmill");
                        akses.hasil_pemeriksaan_echo_pediatrik=akses.getBoolean(rs2, "hasil_pemeriksaan_echo_pediatrik");
                        akses.template_pelaksanaan_informasi_edukasi=akses.getBoolean(rs2, "template_pelaksanaan_informasi_edukasi");
                        akses.skrining_instrumen_esat=akses.getBoolean(rs2, "skrining_instrumen_esat");
                        akses.penilaian_awal_medis_ranap_jantung=akses.getBoolean(rs2, "penilaian_awal_medis_ranap_jantung");
                        akses.penugasan_pengujian_sampel_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "penugasan_pengujian_sampel_lab_kesehatan_lingkungan");
                        akses.hasil_pengujian_sampel_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "hasil_pengujian_sampel_lab_kesehatan_lingkungan");
                        akses.verifikasi_pengujian_sampel_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "verifikasi_pengujian_sampel_lab_kesehatan_lingkungan");
                        akses.validasi_pengujian_sampel_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "validasi_pengujian_sampel_lab_kesehatan_lingkungan");
                        akses.set_pintu_poli=akses.getBoolean(rs2, "set_pintu_poli");
                        akses.rekap_pelayanan_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "rekap_pelayanan_lab_kesehatan_lingkungan");
                        akses.pembayaran_pengujian_sampel_lab_kesehatan_lingkungan=akses.getBoolean(rs2, "pembayaran_pengujian_sampel_lab_kesehatan_lingkungan");
                        akses.skrining_curb65=akses.getBoolean(rs2, "skrining_curb65");
                        akses.bpjs_potensi_prb=akses.getBoolean(rs2, "bpjs_potensi_prb");
                        akses.pintu_poli=akses.getBoolean(rs2, "pintu_poli");
                        akses.bpjs_riwayat_pelayanan_obat=akses.getBoolean(rs2, "bpjs_riwayat_pelayanan_obat");
                        akses.skrining_gizi_kehamilan=akses.getBoolean(rs2, "skrining_gizi_kehamilan");
                        akses.bpjs_rekap_peserta_prb_apotek=akses.getBoolean(rs2, "bpjs_rekap_peserta_prb_apotek");
                        akses.bpjs_riwayat_surat_smc = akses.getBoolean(rs2, "bpjs_riwayat_surat_smc");
                        akses.serah_terima_anggota_tubuh_barang=akses.getBoolean(rs2, "serah_terima_anggota_tubuh_barang");
                        akses.pcra_icra_jenis_aktivitas_proyek=akses.getBoolean(rs2, "pcra_icra_jenis_aktivitas_proyek");
                        akses.pcra_icra_lokasi_kelompok_risiko_area=akses.getBoolean(rs2, "pcra_icra_lokasi_kelompok_risiko_area");
                        akses.pcra_icra_kelas_risiko_pencegahan=akses.getBoolean(rs2, "pcra_icra_kelas_risiko_pencegahan");
                        akses.pcra_icra_tindakan_pengendalian=akses.getBoolean(rs2, "pcra_icra_tindakan_pengendalian");
                        akses.pcra_icra_identifkasi_risiko_infeksi=akses.getBoolean(rs2, "pcra_icra_identifkasi_risiko_infeksi");
                        akses.pcra_icra_identifkasi_risiko_keselamatan=akses.getBoolean(rs2, "pcra_icra_identifkasi_risiko_keselamatan");
                        akses.pcra_icra_identifkasi_risiko_kebakaran=akses.getBoolean(rs2, "pcra_icra_identifkasi_risiko_kebakaran");
                        akses.pcra_icra_identifkasi_risiko_utilitas=akses.getBoolean(rs2, "pcra_icra_identifkasi_risiko_utilitas");
                        akses.bpjs_daftar_resep_apotek=akses.getBoolean(rs2, "bpjs_daftar_resep_apotek");
                        akses.daftar_permintaan_resep_iterasi_bpjs=akses.getBoolean(rs2, "daftar_permintaan_resep_iterasi_bpjs");
                        akses.pcra_icra_pengkajian_risiko_prakonstruksi=akses.getBoolean(rs2, "pcra_icra_pengkajian_risiko_prakonstruksi");
                        akses.pcra_icra_persyaratan_harus_dipenuhi=akses.getBoolean(rs2, "pcra_icra_persyaratan_harus_dipenuhi");
                        akses.satu_sehat_kirim_questionresponse_telaah_farmasi=akses.getBoolean(rs2, "satu_sehat_kirim_questionresponse_telaah_farmasi");
                        akses.satu_sehat_kirim_allergy_intolerance=akses.getBoolean(rs2, "satu_sehat_kirim_allergy_intolerance");
                        akses.konsultasi_perawat=akses.getBoolean(rs2, "konsultasi_perawat");
                        akses.jawaban_konsultasi_perawat=akses.getBoolean(rs2, "jawaban_konsultasi_perawat");
                        akses.bridging_smart_klaim_bpjs=akses.getBoolean(rs2, "bridging_smart_klaim_bpjs");
                        akses.mapping_prosedur_smart_klaim_bpjs=akses.getBoolean(rs2, "mapping_prosedur_smart_klaim_bpjs");
                        akses.mapping_penyakit_smart_klaim_bpjs=akses.getBoolean(rs2, "mapping_penyakit_smart_klaim_bpjs");
                        akses.permintaan_binrohtal=akses.getBoolean(rs2, "permintaan_binrohtal");
                        akses.surat_permintaan_perlindungan_dari_kekerasan=akses.getBoolean(rs2, "surat_permintaan_perlindungan_dari_kekerasan");
                        akses.surat_permohonan_privasi=akses.getBoolean(rs2, "surat_permohonan_privasi");
                        akses.surat_permintaan_second_opinion=akses.getBoolean(rs2, "surat_permintaan_second_opinion");
                        akses.surat_keterangan_berobat=akses.getBoolean(rs2, "surat_keterangan_berobat");
                        akses.surat_penolakan_resusitasi=akses.getBoolean(rs2, "surat_penolakan_resusitasi");
                        akses.catatan_observasi_ruang_ok=akses.getBoolean(rs2, "catatan_observasi_ruang_ok");
                        akses.hasil_pemeriksaan_usg_abdomen=akses.getBoolean(rs2, "hasil_pemeriksaan_usg_abdomen");
                        akses.intervensi_nyeri_farmakologi=akses.getBoolean(rs2, "intervensi_nyeri_farmakologi");
                        try (PreparedStatement psx = koneksi.prepareStatement("select * from set_akses_edit_sementara where id_user = ? and now() < tgl_selesai")) {
                            psx.setString(1, user);
                            try (ResultSet rsx = psx.executeQuery()) {
                                if (rsx.next()) {
                                    akses.tglSelesai = rsx.getTimestamp("tgl_selesai").getTime();
                                    akses.edit = ((System.currentTimeMillis() - akses.tglSelesai) / 1000) < 0;
                                } else {
                                    akses.tglSelesai = -1;
                                    akses.edit = false;
                                }
                            }
                        }
                        break;
                    }else if((rs.getRow()==0)&&(rs2.getRow()==0)){
                        akses.setLogOut();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
                if (e.getMessage().contains("The last packet successfully received from the server") || e.getMessage().contains("Communications link failure")) {
                    --retries;
                } else {
                    retries = 0;
                    akses.setLogOut();
                }
            }
        }while(retries > 0);
    }

    public static void setLogOut() {
        setAdminUtama("", false);
    }

    private static boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
        if (columns.contains(columnName)) {
            return rs.getBoolean(columnName);
        }
        return false;
    }

    private static void setAdminUtama(String kode, boolean isadmin) {
        akses.kode=kode;
        akses.penyakit=isadmin;
        akses.obat_penyakit=isadmin;
        akses.dokter=isadmin;
        akses.jadwal_praktek=isadmin;
        akses.petugas=isadmin;
        akses.pasien=isadmin;
        akses.registrasi=isadmin;
        akses.tindakan_ralan=isadmin;
        akses.kamar_inap=isadmin;
        akses.tindakan_ranap=isadmin;
        akses.operasi=isadmin;
        akses.rujukan_keluar=isadmin;
        akses.rujukan_masuk=isadmin;
        akses.beri_obat=isadmin;
        akses.resep_pulang=isadmin;
        akses.pasien_meninggal=isadmin;
        akses.diet_pasien=isadmin;
        akses.kelahiran_bayi=isadmin;
        akses.periksa_lab=isadmin;
        akses.periksa_radiologi=isadmin;
        akses.kasir_ralan=isadmin;
        akses.deposit_pasien=isadmin;
        akses.piutang_pasien=isadmin;
        akses.peminjaman_berkas=isadmin;
        akses.barcode=isadmin;
        akses.presensi_harian=isadmin;
        akses.presensi_bulanan=isadmin;
        akses.pegawai_admin=isadmin;
        akses.pegawai_user=isadmin;
        akses.suplier=isadmin;
        akses.satuan_barang=isadmin;
        akses.konversi_satuan=isadmin;
        akses.jenis_barang=isadmin;
        akses.obat=isadmin;
        akses.stok_opname_obat=isadmin;
        akses.stok_obat_pasien=isadmin;
        akses.pengadaan_obat=isadmin;
        akses.pemesanan_obat=isadmin;
        akses.penjualan_obat=isadmin;
        akses.piutang_obat=isadmin;
        akses.retur_ke_suplier=isadmin;
        akses.retur_dari_pembeli=isadmin;
        akses.retur_obat_ranap=isadmin;
        akses.retur_piutang_pasien=isadmin;
        akses.keuntungan_penjualan=isadmin;
        akses.keuntungan_beri_obat=isadmin;
        akses.sirkulasi_obat=isadmin;
        akses.ipsrs_barang=isadmin;
        akses.ipsrs_pengadaan_barang=isadmin;
        akses.ipsrs_stok_keluar=isadmin;
        akses.ipsrs_rekap_pengadaan=isadmin;
        akses.ipsrs_rekap_stok_keluar=isadmin;
        akses.ipsrs_pengeluaran_harian=isadmin;
        akses.ipsrs_jenis_barang=isadmin;
        akses.inventaris_jenis=isadmin;
        akses.inventaris_kategori=isadmin;
        akses.inventaris_merk=isadmin;
        akses.inventaris_ruang=isadmin;
        akses.inventaris_produsen=isadmin;
        akses.inventaris_koleksi=isadmin;
        akses.inventaris_inventaris=isadmin;
        akses.inventaris_sirkulasi=isadmin;
        akses.parkir_jenis=isadmin;
        akses.parkir_in=isadmin;
        akses.parkir_out=isadmin;
        akses.parkir_rekap_harian=isadmin;
        akses.parkir_rekap_bulanan=isadmin;
        akses.informasi_kamar=isadmin;
        akses.harian_tindakan_poli=isadmin;
        akses.obat_per_poli=isadmin;
        akses.obat_per_kamar=isadmin;
        akses.obat_per_dokter_ralan=isadmin;
        akses.obat_per_dokter_ranap=isadmin;
        akses.harian_dokter=isadmin;
        akses.bulanan_dokter=isadmin;
        akses.harian_paramedis=isadmin;
        akses.bulanan_paramedis=isadmin;
        akses.pembayaran_ralan=isadmin;
        akses.pembayaran_ranap=isadmin;
        akses.rekap_pembayaran_ralan=isadmin;
        akses.rekap_pembayaran_ranap=isadmin;
        akses.tagihan_masuk=isadmin;
        akses.tambahan_biaya=isadmin;
        akses.potongan_biaya=isadmin;
        akses.resep_obat=isadmin;
        akses.resume_pasien=isadmin;
        akses.penyakit_ralan=isadmin;
        akses.penyakit_ranap=isadmin;
        akses.kamar=isadmin;
        akses.tarif_ralan=isadmin;
        akses.tarif_ranap=isadmin;
        akses.tarif_lab=isadmin;
        akses.tarif_radiologi=isadmin;
        akses.tarif_operasi=isadmin;
        akses.akun_rekening=isadmin;
        akses.rekening_tahun=isadmin;
        akses.posting_jurnal=isadmin;
        akses.buku_besar=isadmin;
        akses.cashflow=isadmin;
        akses.keuangan=isadmin;
        akses.pengeluaran=isadmin;
        akses.setup_pjlab=isadmin;
        akses.setup_otolokasi=isadmin;
        akses.setup_jam_kamin=isadmin;
        akses.setup_embalase=isadmin;
        akses.tracer_login=isadmin;
        akses.display=isadmin;
        akses.set_harga_obat=isadmin;
        akses.set_penggunaan_tarif=isadmin;
        akses.set_oto_ralan=isadmin;
        akses.biaya_harian=isadmin;
        akses.biaya_masuk_sekali=isadmin;
        akses.set_no_rm=isadmin;
        akses.billing_ralan=isadmin;
        akses.billing_ranap=isadmin;
        akses.jm_ranap_dokter=isadmin;
        akses.igd=isadmin;
        akses.barcoderalan=isadmin;
        akses.barcoderanap=isadmin;
        akses.set_harga_obat_ralan=isadmin;
        akses.set_harga_obat_ranap=isadmin;
        akses.admin=isadmin;
        akses.user=isadmin;
        akses.e_eksekutif=isadmin;
        akses.vakum=isadmin;
        akses.aplikasi=isadmin;
        akses.penyakit_pd3i=isadmin;
        akses.surveilans_pd3i=isadmin;
        akses.surveilans_ralan=isadmin;
        akses.diagnosa_pasien=isadmin;
        akses.surveilans_ranap=isadmin;
        akses.pny_takmenular_ranap=isadmin;
        akses.pny_takmenular_ralan=isadmin;
        akses.kunjungan_ralan=isadmin;
        akses.rl32=isadmin;
        akses.rl33=isadmin;
        akses.rl37=isadmin;
        akses.rl38=isadmin;
        akses.harian_tindakan_dokter=isadmin;
        akses.sms=isadmin;
        akses.sidikjari=isadmin;
        akses.jam_masuk=isadmin;
        akses.jadwal_pegawai=isadmin;
        akses.parkir_barcode=isadmin;
        akses.set_nota=isadmin;
        akses.dpjp_ranap=isadmin;
        akses.mutasi_barang=isadmin;
        akses.rl34=isadmin;
        akses.rl36=isadmin;
        akses.fee_visit_dokter=isadmin;
        akses.fee_bacaan_ekg=isadmin;
        akses.fee_rujukan_rontgen=isadmin;
        akses.fee_rujukan_ranap=isadmin;
        akses.fee_ralan=isadmin;
        akses.akun_bayar=isadmin;
        akses.bayar_pemesanan_obat=isadmin;
        akses.obat_per_dokter_peresep=isadmin;
        akses.pemasukan_lain=isadmin;
        akses.pengaturan_rekening=isadmin;
        akses.closing_kasir=isadmin;
        akses.keterlambatan_presensi=isadmin;
        akses.set_harga_kamar=isadmin;
        akses.rekap_per_shift=isadmin;
        akses.bpjs_cek_nik=isadmin;
        akses.bpjs_cek_kartu=isadmin;
        akses.bpjs_cek_riwayat=isadmin;
        akses.obat_per_cara_bayar=isadmin;
        akses.kunjungan_ranap=isadmin;
        akses.bayar_piutang=isadmin;
        akses.payment_point=isadmin;
        akses.bpjs_cek_nomor_rujukan=isadmin;
        akses.icd9=isadmin;
        akses.darurat_stok=isadmin;
        akses.retensi_rm=isadmin;
        akses.temporary_presensi=isadmin;
        akses.jurnal_harian=isadmin;
        akses.sirkulasi_obat2=isadmin;
        akses.edit_registrasi=isadmin;
        akses.bpjs_referensi_diagnosa=isadmin;
        akses.bpjs_referensi_poli=isadmin;
        akses.industrifarmasi=isadmin;
        akses.harian_js=isadmin;
        akses.bulanan_js=isadmin;
        akses.harian_paket_bhp=isadmin;
        akses.bulanan_paket_bhp=isadmin;
        akses.piutang_pasien2=isadmin;
        akses.bpjs_referensi_faskes=isadmin;
        akses.bpjs_sep=isadmin;
        akses.pengambilan_utd=isadmin;
        akses.tarif_utd=isadmin;
        akses.pengambilan_utd2=isadmin;
        akses.utd_medis_rusak=isadmin;
        akses.pengambilan_penunjang_utd=isadmin;
        akses.pengambilan_penunjang_utd2=isadmin;
        akses.utd_penunjang_rusak=isadmin;
        akses.suplier_penunjang=isadmin;
        akses.utd_donor=isadmin;
        akses.bpjs_monitoring_klaim=isadmin;
        akses.utd_cekal_darah=isadmin;
        akses.utd_komponen_darah=isadmin;
        akses.utd_stok_darah=isadmin;
        akses.utd_pemisahan_darah=isadmin;
        akses.harian_kamar=isadmin;
        akses.rincian_piutang_pasien=isadmin;
        akses.keuntungan_beri_obat_nonpiutang=isadmin;
        akses.reklasifikasi_ralan=isadmin;
        akses.reklasifikasi_ranap=isadmin;
        akses.utd_penyerahan_darah=isadmin;
        akses.hutang_obat=isadmin;
        akses.riwayat_obat_alkes_bhp=isadmin;
        akses.sensus_harian_poli=isadmin;
        akses.rl4a=isadmin;
        akses.aplicare_referensi_kamar=isadmin;
        akses.aplicare_ketersediaan_kamar=isadmin;
        akses.inacbg_klaim_baru_otomatis=isadmin;
        akses.inacbg_klaim_baru_manual=isadmin;
        akses.inacbg_coder_nik=isadmin;
        akses.mutasi_berkas=isadmin;
        akses.akun_piutang=isadmin;
        akses.harian_kso=isadmin;
        akses.bulanan_kso=isadmin;
        akses.harian_menejemen=isadmin;
        akses.bulanan_menejemen=isadmin;
        akses.inhealth_cek_eligibilitas=isadmin;
        akses.inhealth_referensi_jenpel_ruang_rawat=isadmin;
        akses.inhealth_referensi_poli=isadmin;
        akses.inhealth_referensi_faskes=isadmin;
        akses.inhealth_sjp=isadmin;
        akses.piutang_ralan=isadmin;
        akses.piutang_ranap=isadmin;
        akses.detail_piutang_penjab=isadmin;
        akses.lama_pelayanan_ralan=isadmin;
        akses.catatan_pasien=isadmin;
        akses.rl4b=isadmin;
        akses.rl4asebab=isadmin;
        akses.rl4bsebab=isadmin;
        akses.data_HAIs=isadmin;
        akses.harian_HAIs=isadmin;
        akses.bulanan_HAIs=isadmin;
        akses.hitung_bor=isadmin;
        akses.perusahaan_pasien=isadmin;
        akses.resep_dokter=isadmin;
        akses.lama_pelayanan_apotek=isadmin;
        akses.hitung_alos=isadmin;
        akses.detail_tindakan=isadmin;
        akses.rujukan_poli_internal=isadmin;
        akses.rekap_poli_anak=isadmin;
        akses.grafik_kunjungan_poli=isadmin;
        akses.grafik_kunjungan_perdokter=isadmin;
        akses.grafik_kunjungan_perpekerjaan=isadmin;
        akses.grafik_kunjungan_perpendidikan=isadmin;
        akses.grafik_kunjungan_pertahun=isadmin;
        akses.berkas_digital_perawatan=isadmin;
        akses.penyakit_menular_ranap=isadmin;
        akses.penyakit_menular_ralan=isadmin;
        akses.grafik_kunjungan_perbulan=isadmin;
        akses.grafik_kunjungan_pertanggal=isadmin;
        akses.grafik_kunjungan_demografi=isadmin;
        akses.grafik_kunjungan_statusdaftartahun=isadmin;
        akses.grafik_kunjungan_statusdaftartahun2=isadmin;
        akses.grafik_kunjungan_statusdaftarbulan=isadmin;
        akses.grafik_kunjungan_statusdaftarbulan2=isadmin;
        akses.grafik_kunjungan_statusdaftartanggal=isadmin;
        akses.grafik_kunjungan_statusdaftartanggal2=isadmin;
        akses.grafik_kunjungan_statusbataltahun=isadmin;
        akses.grafik_kunjungan_statusbatalbulan=isadmin;
        akses.pcare_cek_penyakit=isadmin;
        akses.grafik_kunjungan_statusbataltanggal=isadmin;
        akses.kategori_barang=isadmin;
        akses.golongan_barang=isadmin;
        akses.pemberian_obat_pertanggal=isadmin;
        akses.penjualan_obat_pertanggal=isadmin;
        akses.pcare_cek_kesadaran=isadmin;
        akses.pembatalan_periksa_dokter=isadmin;
        akses.pembayaran_per_unit=isadmin;
        akses.rekap_pembayaran_per_unit=isadmin;
        akses.grafik_kunjungan_percarabayar=isadmin;
        akses.ipsrs_pengadaan_pertanggal=isadmin;
        akses.ipsrs_stokkeluar_pertanggal=isadmin;
        akses.grafik_kunjungan_ranaptahun=isadmin;
        akses.pcare_cek_rujukan=isadmin;
        akses.grafik_lab_ralantahun=isadmin;
        akses.grafik_rad_ralantahun=isadmin;
        akses.cek_entry_ralan=isadmin;
        akses.inacbg_klaim_baru_manual2=isadmin;
        akses.permintaan_medis=isadmin;
        akses.rekap_permintaan_medis=isadmin;
        akses.surat_pemesanan_medis=isadmin;
        akses.permintaan_non_medis=isadmin;
        akses.rekap_permintaan_non_medis=isadmin;
        akses.surat_pemesanan_non_medis=isadmin;
        akses.grafik_per_perujuk=isadmin;
        akses.bpjs_cek_prosedur=isadmin;
        akses.bpjs_cek_kelas_rawat=isadmin;
        akses.bpjs_cek_dokter=isadmin;
        akses.bpjs_cek_spesialistik=isadmin;
        akses.bpjs_cek_ruangrawat=isadmin;
        akses.bpjs_cek_carakeluar=isadmin;
        akses.bpjs_cek_pasca_pulang=isadmin;
        akses.detail_tindakan_okvk=isadmin;
        akses.billing_parsial=isadmin;
        akses.bpjs_cek_nomor_rujukan_rs=isadmin;
        akses.bpjs_cek_rujukan_kartu_pcare=isadmin;
        akses.bpjs_cek_rujukan_kartu_rs=isadmin;
        akses.akses_depo_obat=isadmin;
        akses.bpjs_rujukan_keluar=isadmin;
        akses.grafik_lab_ralanbulan=isadmin;
        akses.pengeluaran_stok_apotek=isadmin;
        akses.grafik_rad_ralanbulan=isadmin;
        akses.detailjmdokter2=isadmin;
        akses.pengaduan_pasien=isadmin;
        akses.grafik_lab_ralanhari=isadmin;
        akses.grafik_rad_ralanhari=isadmin;
        akses.sensus_harian_ralan=isadmin;
        akses.metode_racik=isadmin;
        akses.pembayaran_akun_bayar=isadmin;
        akses.pengguna_obat_resep=isadmin;
        akses.rekap_pemesanan=isadmin;
        akses.master_berkas_pegawai=isadmin;
        akses.berkas_kepegawaian=isadmin;
        akses.riwayat_jabatan=isadmin;
        akses.riwayat_pendidikan=isadmin;
        akses.riwayat_naik_gaji=isadmin;
        akses.kegiatan_ilmiah=isadmin;
        akses.riwayat_penghargaan=isadmin;
        akses.riwayat_penelitian=isadmin;
        akses.penerimaan_non_medis=isadmin;
        akses.bayar_pesan_non_medis=isadmin;
        akses.hutang_barang_non_medis=isadmin;
        akses.rekap_pemesanan_non_medis=isadmin;
        akses.insiden_keselamatan=isadmin;
        akses.insiden_keselamatan_pasien=isadmin;
        akses.grafik_ikp_pertahun=isadmin;
        akses.grafik_ikp_perbulan=isadmin;
        akses.grafik_ikp_pertanggal=isadmin;
        akses.riwayat_data_batch=isadmin;
        akses.grafik_ikp_jenis=isadmin;
        akses.grafik_ikp_dampak=isadmin;
        akses.piutang_akun_piutang=isadmin;
        akses.grafik_kunjungan_per_agama=isadmin;
        akses.grafik_kunjungan_per_umur=isadmin;
        akses.suku_bangsa=isadmin;
        akses.bahasa_pasien=isadmin;
        akses.golongan_tni=isadmin;
        akses.satuan_tni=isadmin;
        akses.jabatan_tni=isadmin;
        akses.pangkat_tni=isadmin;
        akses.golongan_polri=isadmin;
        akses.satuan_polri=isadmin;
        akses.jabatan_polri=isadmin;
        akses.pangkat_polri=isadmin;
        akses.cacat_fisik=isadmin;
        akses.grafik_kunjungan_suku=isadmin;
        akses.grafik_kunjungan_bahasa=isadmin;
        akses.booking_operasi=isadmin;
        akses.mapping_poli_bpjs=isadmin;
        akses.grafik_kunjungan_per_cacat=isadmin;
        akses.barang_cssd=isadmin;
        akses.skdp_bpjs=isadmin;
        akses.booking_registrasi=isadmin;
        akses.bpjs_cek_propinsi=isadmin;
        akses.bpjs_cek_kabupaten=isadmin;
        akses.bpjs_cek_kecamatan=isadmin;
        akses.bpjs_cek_dokterdpjp=isadmin;
        akses.bpjs_cek_riwayat_rujukanrs=isadmin;
        akses.bpjs_cek_tanggal_rujukan=isadmin;
        akses.permintaan_lab=isadmin;
        akses.permintaan_radiologi=isadmin;
        akses.surat_indeks=isadmin;
        akses.surat_map=isadmin;
        akses.surat_almari=isadmin;
        akses.surat_rak=isadmin;
        akses.surat_ruang=isadmin;
        akses.surat_klasifikasi=isadmin;
        akses.surat_status=isadmin;
        akses.surat_sifat=isadmin;
        akses.surat_balas=isadmin;
        akses.surat_masuk=isadmin;
        akses.pcare_cek_dokter=isadmin;
        akses.pcare_cek_poli=isadmin;
        akses.pcare_cek_provider=isadmin;
        akses.pcare_cek_statuspulang=isadmin;
        akses.pcare_cek_spesialis=isadmin;
        akses.pcare_cek_subspesialis=isadmin;
        akses.pcare_cek_sarana=isadmin;
        akses.pcare_cek_khusus=isadmin;
        akses.pcare_cek_obat=isadmin;
        akses.pcare_cek_tindakan=isadmin;
        akses.pcare_cek_faskessubspesialis=isadmin;
        akses.pcare_cek_faskesalihrawat=isadmin;
        akses.pcare_cek_faskesthalasemia=isadmin;
        akses.pcare_mapping_obat=isadmin;
        akses.pcare_mapping_tindakan=isadmin;
        akses.pcare_club_prolanis=isadmin;
        akses.pcare_mapping_poli=isadmin;
        akses.pcare_kegiatan_kelompok=isadmin;
        akses.pcare_mapping_tindakan_ranap=isadmin;
        akses.pcare_peserta_kegiatan_kelompok=isadmin;
        akses.sirkulasi_obat3=isadmin;
        akses.bridging_pcare_daftar=isadmin;
        akses.pcare_mapping_dokter=isadmin;
        akses.ranap_per_ruang=isadmin;
        akses.penyakit_ranap_cara_bayar=isadmin;
        akses.anggota_militer_dirawat=isadmin;
        akses.set_input_parsial=isadmin;
        akses.lama_pelayanan_radiologi=isadmin;
        akses.lama_pelayanan_lab=isadmin;
        akses.bpjs_cek_sep=isadmin;
        akses.catatan_perawatan=isadmin;
        akses.surat_keluar=isadmin;
        akses.kegiatan_farmasi=isadmin;
        akses.stok_opname_logistik=isadmin;
        akses.sirkulasi_non_medis=isadmin;
        akses.rekap_lab_pertahun=isadmin;
        akses.perujuk_lab_pertahun=isadmin;
        akses.rekap_radiologi_pertahun=isadmin;
        akses.perujuk_radiologi_pertahun=isadmin;
        akses.jumlah_porsi_diet=isadmin;
        akses.jumlah_macam_diet=isadmin;
        akses.payment_point2=isadmin;
        akses.pembayaran_akun_bayar2=isadmin;
        akses.hapus_nota_salah=isadmin;
        akses.hais_perbangsal=isadmin;
        akses.ppn_obat=isadmin;
        akses.saldo_akun_perbulan=isadmin;
        akses.display_apotek=isadmin;
        akses.sisrute_referensi_faskes=isadmin;
        akses.sisrute_referensi_alasanrujuk=isadmin;
        akses.sisrute_referensi_diagnosa=isadmin;
        akses.sisrute_rujukan_masuk=isadmin;
        akses.sisrute_rujukan_keluar=isadmin;
        akses.bpjs_cek_skdp=isadmin;
        akses.data_batch=isadmin;
        akses.kunjungan_permintaan_lab=isadmin;
        akses.kunjungan_permintaan_lab2=isadmin;
        akses.kunjungan_permintaan_radiologi=isadmin;
        akses.kunjungan_permintaan_radiologi2=isadmin;
        akses.pcare_pemberian_obat=isadmin;
        akses.pcare_pemberian_tindakan=isadmin;
        akses.pembayaran_akun_bayar3=isadmin;
        akses.password_asuransi=isadmin;
        akses.kemenkes_sitt=isadmin;
        akses.siranap_ketersediaan_kamar=isadmin;
        akses.grafik_tb_periodelaporan=isadmin;
        akses.grafik_tb_rujukan=isadmin;
        akses.grafik_tb_riwayat=isadmin;
        akses.grafik_tb_tipediagnosis=isadmin;
        akses.grafik_tb_statushiv=isadmin;
        akses.grafik_tb_skoringanak=isadmin;
        akses.grafik_tb_konfirmasiskoring5=isadmin;
        akses.grafik_tb_konfirmasiskoring6=isadmin;
        akses.grafik_tb_sumberobat=isadmin;
        akses.grafik_tb_hasilakhirpengobatan=isadmin;
        akses.grafik_tb_hasilteshiv=isadmin;
        akses.kadaluarsa_batch=isadmin;
        akses.sisa_stok=isadmin;
        akses.obat_per_resep=isadmin;
        akses.pemakaian_air_pdam=isadmin;
        akses.limbah_b3_medis=isadmin;
        akses.grafik_air_pdam_pertanggal=isadmin;
        akses.grafik_air_pdam_perbulan=isadmin;
        akses.grafik_limbahb3_pertanggal=isadmin;
        akses.grafik_limbahb3_perbulan=isadmin;
        akses.limbah_domestik=isadmin;
        akses.grafik_limbahdomestik_pertanggal=isadmin;
        akses.grafik_limbahdomestik_perbulan=isadmin;
        akses.mutu_air_limbah=isadmin;
        akses.pest_control=isadmin;
        akses.ruang_perpustakaan=isadmin;
        akses.kategori_perpustakaan=isadmin;
        akses.jenis_perpustakaan=isadmin;
        akses.pengarang_perpustakaan=isadmin;
        akses.penerbit_perpustakaan=isadmin;
        akses.koleksi_perpustakaan=isadmin;
        akses.inventaris_perpustakaan=isadmin;
        akses.set_peminjaman_perpustakaan=isadmin;
        akses.denda_perpustakaan=isadmin;
        akses.anggota_perpustakaan=isadmin;
        akses.peminjaman_perpustakaan=isadmin;
        akses.bayar_denda_perpustakaan=isadmin;
        akses.ebook_perpustakaan=isadmin;
        akses.jenis_cidera_k3rs=isadmin;
        akses.penyebab_k3rs=isadmin;
        akses.jenis_luka_k3rs=isadmin;
        akses.lokasi_kejadian_k3rs=isadmin;
        akses.dampak_cidera_k3rs=isadmin;
        akses.jenis_pekerjaan_k3rs=isadmin;
        akses.bagian_tubuh_k3rs=isadmin;
        akses.peristiwa_k3rs=isadmin;
        akses.grafik_k3_pertahun=isadmin;
        akses.grafik_k3_perbulan=isadmin;
        akses.grafik_k3_pertanggal=isadmin;
        akses.grafik_k3_perjeniscidera=isadmin;
        akses.grafik_k3_perpenyebab=isadmin;
        akses.grafik_k3_perjenisluka=isadmin;
        akses.grafik_k3_lokasikejadian=isadmin;
        akses.grafik_k3_dampakcidera=isadmin;
        akses.grafik_k3_perjenispekerjaan=isadmin;
        akses.grafik_k3_perbagiantubuh=isadmin;
        akses.jenis_cidera_k3rstahun=isadmin;
        akses.penyebab_k3rstahun=isadmin;
        akses.jenis_luka_k3rstahun=isadmin;
        akses.lokasi_kejadian_k3rstahun=isadmin;
        akses.dampak_cidera_k3rstahun=isadmin;
        akses.jenis_pekerjaan_k3rstahun=isadmin;
        akses.bagian_tubuh_k3rstahun=isadmin;
        akses.sekrining_rawat_jalan=isadmin;
        akses.bpjs_histori_pelayanan=isadmin;
        akses.rekap_mutasi_berkas=isadmin;
        akses.skrining_ralan_pernapasan_pertahun=isadmin;
        akses.pengajuan_barang_medis=isadmin;
        akses.pengajuan_barang_nonmedis=isadmin;
        akses.grafik_kunjungan_ranapbulan=isadmin;
        akses.grafik_kunjungan_ranaptanggal=isadmin;
        akses.grafik_kunjungan_ranap_peruang=isadmin;
        akses.kunjungan_bangsal_pertahun=isadmin;
        akses.grafik_jenjang_jabatanpegawai=isadmin;
        akses.grafik_bidangpegawai=isadmin;
        akses.grafik_departemenpegawai=isadmin;
        akses.grafik_pendidikanpegawai=isadmin;
        akses.grafik_sttswppegawai=isadmin;
        akses.grafik_sttskerjapegawai=isadmin;
        akses.grafik_sttspulangranap=isadmin;
        akses.kip_pasien_ranap=isadmin;
        akses.kip_pasien_ralan=isadmin;
        akses.bpjs_mapping_dokterdpjp=isadmin;
        akses.data_triase_igd=isadmin;
        akses.master_triase_skala1=isadmin;
        akses.master_triase_skala2=isadmin;
        akses.master_triase_skala3=isadmin;
        akses.master_triase_skala4=isadmin;
        akses.master_triase_skala5=isadmin;
        akses.master_triase_pemeriksaan=isadmin;
        akses.master_triase_macamkasus=isadmin;
        akses.rekap_permintaan_diet=isadmin;
        akses.daftar_pasien_ranap=isadmin;
        akses.daftar_pasien_ranaptni=isadmin;
        akses.pengajuan_asetinventaris=isadmin;
        akses.item_apotek_jenis=isadmin;
        akses.item_apotek_kategori=isadmin;
        akses.item_apotek_golongan=isadmin;
        akses.item_apotek_industrifarmasi=isadmin;
        akses.obat10_terbanyak_poli=isadmin;
        akses.grafik_pengajuan_aset_urgensi=isadmin;
        akses.grafik_pengajuan_aset_status=isadmin;
        akses.grafik_pengajuan_aset_departemen=isadmin;
        akses.rekap_pengajuan_aset_departemen=isadmin;
        akses.grafik_kelompok_jabatanpegawai=isadmin;
        akses.grafik_resiko_kerjapegawai=isadmin;
        akses.grafik_emergency_indexpegawai=isadmin;
        akses.grafik_inventaris_ruang=isadmin;
        akses.harian_HAIs2=isadmin;
        akses.grafik_inventaris_jenis=isadmin;
        akses.data_resume_pasien=isadmin;
        akses.perkiraan_biaya_ranap=isadmin;
        akses.rekap_obat_poli=isadmin;
        akses.rekap_obat_pasien=isadmin;
        akses.grafik_HAIs_pasienbangsal=isadmin;
        akses.grafik_HAIs_pasienbulan=isadmin;
        akses.permintaan_perbaikan_inventaris=isadmin;
        akses.grafik_HAIs_laju_vap=isadmin;
        akses.grafik_HAIs_laju_iad=isadmin;
        akses.grafik_HAIs_laju_pleb=isadmin;
        akses.grafik_HAIs_laju_isk=isadmin;
        akses.grafik_HAIs_laju_ilo=isadmin;
        akses.grafik_HAIs_laju_hap=isadmin;
        akses.inhealth_mapping_poli=isadmin;
        akses.inhealth_mapping_dokter=isadmin;
        akses.inhealth_mapping_tindakan_ralan=isadmin;
        akses.inhealth_mapping_tindakan_ranap=isadmin;
        akses.inhealth_mapping_tindakan_radiologi=isadmin;
        akses.inhealth_mapping_tindakan_laborat=isadmin;
        akses.inhealth_mapping_tindakan_operasi=isadmin;
        akses.hibah_obat_bhp=isadmin;
        akses.asal_hibah=isadmin;
        akses.asuhan_gizi=isadmin;
        akses.inhealth_kirim_tagihan=isadmin;
        akses.sirkulasi_obat4=isadmin;
        akses.sirkulasi_obat5=isadmin;
        akses.sirkulasi_non_medis2=isadmin;
        akses.monitoring_asuhan_gizi=isadmin;
        akses.penerimaan_obat_perbulan=isadmin;
        akses.rekap_kunjungan=isadmin;
        akses.surat_sakit=isadmin;
        akses.penilaian_awal_keperawatan_ralan=isadmin;
        akses.permintaan_diet=isadmin;
        akses.master_masalah_keperawatan=isadmin;
        akses.pengajuan_cuti=isadmin;
        akses.kedatangan_pasien=isadmin;
        akses.utd_pendonor=isadmin;
        akses.toko_suplier=isadmin;
        akses.toko_jenis=isadmin;
        akses.toko_set_harga=isadmin;
        akses.toko_barang=isadmin;
        akses.penagihan_piutang_pasien=isadmin;
        akses.akun_penagihan_piutang=isadmin;
        akses.stok_opname_toko=isadmin;
        akses.toko_riwayat_barang=isadmin;
        akses.toko_surat_pemesanan=isadmin;
        akses.toko_pengajuan_barang=isadmin;
        akses.toko_penerimaan_barang=isadmin;
        akses.toko_pengadaan_barang=isadmin;
        akses.toko_hutang=isadmin;
        akses.toko_bayar_pemesanan=isadmin;
        akses.toko_member=isadmin;
        akses.toko_penjualan=isadmin;
        akses.registrasi_poli_per_tanggal=isadmin;
        akses.toko_piutang=isadmin;
        akses.toko_retur_beli=isadmin;
        akses.ipsrs_returbeli=isadmin;
        akses.ipsrs_riwayat_barang=isadmin;
        akses.pasien_corona=isadmin;
        akses.toko_pendapatan_harian=isadmin;
        akses.diagnosa_pasien_corona=isadmin;
        akses.perawatan_pasien_corona=isadmin;
        akses.penilaian_awal_keperawatan_gigi=isadmin;
        akses.master_masalah_keperawatan_gigi=isadmin;
        akses.toko_bayar_piutang=isadmin;
        akses.toko_piutang_harian=isadmin;
        akses.toko_penjualan_harian=isadmin;
        akses.deteksi_corona=isadmin;
        akses.penilaian_awal_keperawatan_kebidanan=isadmin;
        akses.pengumuman_epasien=isadmin;
        akses.surat_hamil=isadmin;
        akses.set_tarif_online=isadmin;
        akses.booking_periksa=isadmin;
        akses.toko_sirkulasi=isadmin;
        akses.toko_retur_jual=isadmin;
        akses.toko_retur_piutang=isadmin;
        akses.toko_sirkulasi2=isadmin;
        akses.toko_keuntungan_barang=isadmin;
        akses.zis_pengeluaran_penerima_dankes=isadmin;
        akses.zis_penghasilan_penerima_dankes=isadmin;
        akses.zis_ukuran_rumah_penerima_dankes=isadmin;
        akses.zis_dinding_rumah_penerima_dankes=isadmin;
        akses.zis_lantai_rumah_penerima_dankes=isadmin;
        akses.zis_atap_rumah_penerima_dankes=isadmin;
        akses.zis_kepemilikan_rumah_penerima_dankes=isadmin;
        akses.zis_kamar_mandi_penerima_dankes=isadmin;
        akses.zis_dapur_rumah_penerima_dankes=isadmin;
        akses.zis_kursi_rumah_penerima_dankes=isadmin;
        akses.zis_kategori_phbs_penerima_dankes=isadmin;
        akses.zis_elektronik_penerima_dankes=isadmin;
        akses.zis_ternak_penerima_dankes=isadmin;
        akses.zis_jenis_simpanan_penerima_dankes=isadmin;
        akses.penilaian_awal_keperawatan_anak=isadmin;
        akses.zis_kategori_asnaf_penerima_dankes=isadmin;
        akses.master_masalah_keperawatan_anak=isadmin;
        akses.master_imunisasi=isadmin;
        akses.zis_patologis_penerima_dankes=isadmin;
        akses.pcare_cek_kartu=isadmin;
        akses.surat_bebas_narkoba=isadmin;
        akses.surat_keterangan_covid=isadmin;
        akses.pemakaian_air_tanah=isadmin;
        akses.grafik_air_tanah_pertanggal=isadmin;
        akses.grafik_air_tanah_perbulan=isadmin;
        akses.lama_pelayanan_poli=isadmin;
        akses.hemodialisa=isadmin;
        akses.grafik_harian_hemodialisa=isadmin;
        akses.grafik_bulanan_hemodialisa=isadmin;
        akses.grafik_tahunan_hemodialisa=isadmin;
        akses.grafik_bulanan_meninggal=isadmin;
        akses.laporan_tahunan_irj=isadmin;
        akses.perbaikan_inventaris=isadmin;
        akses.surat_cuti_hamil=isadmin;
        akses.permintaan_stok_obat_pasien=isadmin;
        akses.pemeliharaan_inventaris=isadmin;
        akses.klasifikasi_pasien_ranap=isadmin;
        akses.bulanan_klasifikasi_pasien_ranap=isadmin;
        akses.harian_klasifikasi_pasien_ranap=isadmin;
        akses.klasifikasi_pasien_perbangsal=isadmin;
        akses.soap_perawatan=isadmin;
        akses.klaim_rawat_jalan=isadmin;
        akses.skrining_gizi=isadmin;
        akses.lama_penyiapan_rm=isadmin;
        akses.dosis_radiologi=isadmin;
        akses.demografi_umur_kunjungan=isadmin;
        akses.jam_diet_pasien=isadmin;
        akses.rvu_bpjs=isadmin;
        akses.verifikasi_penerimaan_farmasi=isadmin;
        akses.verifikasi_penerimaan_logistik=isadmin;
        akses.pemeriksaan_lab_pa=isadmin;
        akses.ringkasan_pengajuan_obat=isadmin;
        akses.ringkasan_pemesanan_obat=isadmin;
        akses.ringkasan_pengadaan_obat=isadmin;
        akses.ringkasan_penerimaan_obat=isadmin;
        akses.ringkasan_hibah_obat=isadmin;
        akses.ringkasan_penjualan_obat=isadmin;
        akses.ringkasan_beri_obat=isadmin;
        akses.ringkasan_piutang_obat=isadmin;
        akses.ringkasan_stok_keluar_obat=isadmin;
        akses.ringkasan_retur_suplier_obat=isadmin;
        akses.ringkasan_retur_pembeli_obat=isadmin;
        akses.penilaian_awal_keperawatan_ranapkebidanan=isadmin;
        akses.ringkasan_pengajuan_nonmedis=isadmin;
        akses.ringkasan_pemesanan_nonmedis=isadmin;
        akses.ringkasan_pengadaan_nonmedis=isadmin;
        akses.ringkasan_penerimaan_nonmedis=isadmin;
        akses.ringkasan_stokkeluar_nonmedis=isadmin;
        akses.ringkasan_returbeli_nonmedis=isadmin;
        akses.omset_penerimaan=isadmin;
        akses.validasi_penagihan_piutang=isadmin;
        akses.permintaan_ranap=isadmin;
        akses.bpjs_diagnosa_prb=isadmin;
        akses.bpjs_obat_prb=isadmin;
        akses.bpjs_surat_kontrol=isadmin;
        akses.penggunaan_bhp_ok=isadmin;
        akses.surat_keterangan_rawat_inap=isadmin;
        akses.surat_keterangan_sehat=isadmin;
        akses.pendapatan_per_carabayar=isadmin;
        akses.akun_host_to_host_bank_jateng=isadmin;
        akses.pembayaran_bank_jateng=isadmin;
        akses.bpjs_surat_pri=isadmin;
        akses.ringkasan_tindakan=isadmin;
        akses.lama_pelayanan_pasien=isadmin;
        akses.surat_sakit_pihak_2=isadmin;
        akses.tagihan_hutang_obat=isadmin;
        akses.referensi_mobilejkn_bpjs=isadmin;
        akses.batal_pendaftaran_mobilejkn_bpjs=isadmin;
        akses.lama_operasi=isadmin;
        akses.grafik_inventaris_kategori=isadmin;
        akses.grafik_inventaris_merk=isadmin;
        akses.grafik_inventaris_produsen=isadmin;
        akses.pengembalian_deposit_pasien=isadmin;
        akses.validasi_tagihan_hutang_obat=isadmin;
        akses.piutang_obat_belum_lunas=isadmin;
        akses.integrasi_briapi=isadmin;
        akses.pengadaan_aset_inventaris=isadmin;
        akses.akun_aset_inventaris=isadmin;
        akses.suplier_inventaris=isadmin;
        akses.penerimaan_aset_inventaris=isadmin;
        akses.bayar_pemesanan_iventaris=isadmin;
        akses.hutang_aset_inventaris=isadmin;
        akses.hibah_aset_inventaris=isadmin;
        akses.titip_faktur_non_medis=isadmin;
        akses.validasi_tagihan_non_medis=isadmin;
        akses.titip_faktur_aset=isadmin;
        akses.validasi_tagihan_aset=isadmin;
        akses.hibah_non_medis=isadmin;
        akses.pcare_alasan_tacc=isadmin;
        akses.resep_luar=isadmin;
        akses.surat_bebas_tbc=isadmin;
        akses.surat_buta_warna=isadmin;
        akses.surat_bebas_tato=isadmin;
        akses.surat_kewaspadaan_kesehatan=isadmin;
        akses.grafik_porsidiet_pertanggal=isadmin;
        akses.grafik_porsidiet_perbulan=isadmin;
        akses.grafik_porsidiet_pertahun=isadmin;
        akses.grafik_porsidiet_perbangsal=isadmin;
        akses.penilaian_awal_medis_ralan=isadmin;
        akses.master_masalah_keperawatan_mata=isadmin;
        akses.penilaian_awal_keperawatan_mata=isadmin;
        akses.penilaian_awal_medis_ranap=isadmin;
        akses.penilaian_awal_medis_ranap_kebidanan=isadmin;
        akses.penilaian_awal_medis_ralan_kebidanan=isadmin;
        akses.penilaian_awal_medis_igd=isadmin;
        akses.penilaian_awal_medis_ralan_anak=isadmin;
        akses.bpjs_referensi_poli_hfis=isadmin;
        akses.bpjs_referensi_dokter_hfis=isadmin;
        akses.bpjs_referensi_jadwal_hfis=isadmin;
        akses.penilaian_fisioterapi=isadmin;
        akses.bpjs_program_prb=isadmin;
        akses.bpjs_suplesi_jasaraharja=isadmin;
        akses.bpjs_data_induk_kecelakaan=isadmin;
        akses.bpjs_sep_internal=isadmin;
        akses.bpjs_klaim_jasa_raharja=isadmin;
        akses.bpjs_daftar_finger_print=isadmin;
        akses.bpjs_rujukan_khusus=isadmin;
        akses.pemeliharaan_gedung=isadmin;
        akses.grafik_perbaikan_inventaris_pertanggal=isadmin;
        akses.grafik_perbaikan_inventaris_perbulan=isadmin;
        akses.grafik_perbaikan_inventaris_pertahun=isadmin;
        akses.grafik_perbaikan_inventaris_perpelaksana_status=isadmin;
        akses.penilaian_mcu=isadmin;
        akses.peminjam_piutang=isadmin;
        akses.piutang_lainlain=isadmin;
        akses.cara_bayar=isadmin;
        akses.audit_kepatuhan_apd=isadmin;
        akses.bpjs_task_id=isadmin;
        akses.bayar_piutang_lain=isadmin;
        akses.pembayaran_akun_bayar4=isadmin;
        akses.stok_akhir_farmasi_pertanggal=isadmin;
        akses.riwayat_kamar_pasien=isadmin;
        akses.uji_fungsi_kfr=isadmin;
        akses.hapus_berkas_digital_perawatan=isadmin;
        akses.kategori_pengeluaran_harian=isadmin;
        akses.kategori_pemasukan_lain=isadmin;
        akses.pembayaran_akun_bayar5=isadmin;
        akses.ruang_ok=isadmin;
        akses.jasa_tindakan_pasien=isadmin;
        akses.telaah_resep=isadmin;
        akses.permintaan_resep_pulang=isadmin;
        akses.rekap_jm_dokter=isadmin;
        akses.status_data_rm=isadmin;
        akses.ubah_petugas_lab_pk=isadmin;
        akses.ubah_petugas_lab_pa=isadmin;
        akses.ubah_petugas_radiologi=isadmin;
        akses.gabung_norawat=isadmin;
        akses.gabung_rm=isadmin;
        akses.ringkasan_biaya_obat_pasien_pertanggal=isadmin;
        akses.master_masalah_keperawatan_igd=isadmin;
        akses.penilaian_awal_keperawatan_igd=isadmin;
        akses.bpjs_referensi_dpho_apotek=isadmin;
        akses.bpjs_referensi_poli_apotek=isadmin;
        akses.bayar_jm_dokter=isadmin;
        akses.bpjs_referensi_faskes_apotek=isadmin;
        akses.bpjs_referensi_spesialistik_apotek=isadmin;
        akses.pembayaran_briva=isadmin;
        akses.penilaian_awal_keperawatan_ranap=isadmin;
        akses.nilai_penerimaan_vendor_farmasi_perbulan=isadmin;
        akses.akun_bayar_hutang=isadmin;
        akses.master_rencana_keperawatan=isadmin;
        akses.laporan_tahunan_igd=isadmin;
        akses.obat_bhp_tidakbergerak=isadmin;
        akses.ringkasan_hutang_vendor_farmasi=isadmin;
        akses.nilai_penerimaan_vendor_nonmedis_perbulan=isadmin;
        akses.ringkasan_hutang_vendor_nonmedis=isadmin;
        akses.anggota_polri_dirawat=isadmin;
        akses.daftar_pasien_ranap_polri=isadmin;
        akses.soap_ralan_polri=isadmin;
        akses.soap_ranap_polri=isadmin;
        akses.laporan_penyakit_polri=isadmin;
        akses.master_rencana_keperawatan_anak=isadmin;
        akses.jumlah_pengunjung_ralan_polri=isadmin;
        akses.catatan_observasi_igd=isadmin;
        akses.catatan_observasi_ranap=isadmin;
        akses.catatan_observasi_ranap_kebidanan=isadmin;
        akses.catatan_observasi_ranap_postpartum=isadmin;
        akses.penilaian_awal_medis_ralan_tht=isadmin;
        akses.penilaian_psikologi=isadmin;
        akses.audit_cuci_tangan_medis=isadmin;
        akses.audit_pembuangan_limbah=isadmin;
        akses.ruang_audit_kepatuhan=isadmin;
        akses.audit_pembuangan_benda_tajam=isadmin;
        akses.audit_penanganan_darah=isadmin;
        akses.audit_pengelolaan_linen_kotor=isadmin;
        akses.audit_penempatan_pasien=isadmin;
        akses.audit_kamar_jenazah=isadmin;
        akses.audit_bundle_iadp=isadmin;
        akses.audit_bundle_ido=isadmin;
        akses.audit_fasilitas_kebersihan_tangan=isadmin;
        akses.audit_fasilitas_apd=isadmin;
        akses.audit_pembuangan_limbah_cair_infeksius=isadmin;
        akses.audit_sterilisasi_alat=isadmin;
        akses.penilaian_awal_medis_ralan_psikiatri=isadmin;
        akses.persetujuan_penolakan_tindakan=isadmin;
        akses.audit_bundle_isk=isadmin;
        akses.audit_bundle_plabsi=isadmin;
        akses.audit_bundle_vap=isadmin;
        akses.akun_host_to_host_bank_papua=isadmin;
        akses.pembayaran_bank_papua=isadmin;
        akses.penilaian_awal_medis_ralan_penyakit_dalam=isadmin;
        akses.penilaian_awal_medis_ralan_mata=isadmin;
        akses.penilaian_awal_medis_ralan_neurologi=isadmin;
        akses.sirkulasi_obat6=isadmin;
        akses.penilaian_awal_medis_ralan_orthopedi=isadmin;
        akses.penilaian_awal_medis_ralan_bedah=isadmin;
        akses.integrasi_khanza_health_services=isadmin;
        akses.soap_ralan_tni=isadmin;
        akses.soap_ranap_tni=isadmin;
        akses.jumlah_pengunjung_ralan_tni=isadmin;
        akses.laporan_penyakit_tni=isadmin;
        akses.catatan_keperawatan_ranap=isadmin;
        akses.master_rencana_keperawatan_gigi=isadmin;
        akses.master_rencana_keperawatan_mata=isadmin;
        akses.master_rencana_keperawatan_igd=isadmin;
        akses.master_masalah_keperawatan_psikiatri=isadmin;
        akses.master_rencana_keperawatan_psikiatri=isadmin;
        akses.penilaian_awal_keperawatan_psikiatri=isadmin;
        akses.pemantauan_pews_anak=isadmin;
        akses.surat_pulang_atas_permintaan_sendiri=isadmin;
        akses.template_hasil_radiologi=isadmin;
        akses.laporan_bulanan_irj=isadmin;
        akses.template_pemeriksaan=isadmin;
        akses.pemeriksaan_lab_mb=isadmin;
        akses.ubah_petugas_lab_mb=isadmin;
        akses.penilaian_pre_operasi=isadmin;
        akses.penilaian_pre_anestesi=isadmin;
        akses.perencanaan_pemulangan=isadmin;
        akses.penilaian_lanjutan_resiko_jatuh_dewasa=isadmin;
        akses.penilaian_lanjutan_resiko_jatuh_anak=isadmin;
        akses.penilaian_awal_medis_ralan_geriatri=isadmin;
        akses.penilaian_tambahan_pasien_geriatri=isadmin;
        akses.skrining_nutrisi_dewasa=isadmin;
        akses.skrining_nutrisi_lansia=isadmin;
        akses.hasil_pemeriksaan_usg=isadmin;
        akses.skrining_nutrisi_anak=isadmin;
        akses.akun_host_to_host_bank_jabar=isadmin;
        akses.pembayaran_bank_jabar=isadmin;
        akses.surat_pernyataan_pasien_umum=isadmin;
        akses.konseling_farmasi=isadmin;
        akses.pelayanan_informasi_obat=isadmin;
        akses.jawaban_pio_apoteker=isadmin;
        akses.surat_persetujuan_umum=isadmin;
        akses.transfer_pasien_antar_ruang=isadmin;
        akses.satu_sehat_referensi_dokter=isadmin;
        akses.satu_sehat_referensi_pasien=isadmin;
        akses.satu_sehat_mapping_departemen=isadmin;
        akses.satu_sehat_mapping_lokasi=isadmin;
        akses.satu_sehat_kirim_encounter=isadmin;
        akses.catatan_cek_gds=isadmin;
        akses.satu_sehat_kirim_condition=isadmin;
        akses.checklist_pre_operasi=isadmin;
        akses.satu_sehat_kirim_observationttv=isadmin;
        akses.signin_sebelum_anestesi=isadmin;
        akses.satu_sehat_kirim_procedure=isadmin;
        akses.operasi_per_bulan=isadmin;
        akses.timeout_sebelum_insisi=isadmin;
        akses.signout_sebelum_menutup_luka=isadmin;
        akses.dapur_barang=isadmin;
        akses.dapur_opname=isadmin;
        akses.satu_sehat_mapping_vaksin=isadmin;
        akses.dapur_suplier=isadmin;
        akses.satu_sehat_kirim_Immunization=isadmin;
        akses.checklist_post_operasi=isadmin;
        akses.dapur_pembelian=isadmin;
        akses.dapur_stok_keluar=isadmin;
        akses.dapur_riwayat_barang=isadmin;
        akses.permintaan_dapur=isadmin;
        akses.rekonsiliasi_obat=isadmin;
        akses.biaya_pengadaan_dapur=isadmin;
        akses.rekap_pengadaan_dapur=isadmin;
        akses.kesling_limbah_b3medis_cair=isadmin;
        akses.grafik_limbahb3cair_pertanggal=isadmin;
        akses.grafik_limbahb3cair_perbulan=isadmin;
        akses.rekap_biaya_registrasi=isadmin;
        akses.konfirmasi_rekonsiliasi_obat=isadmin;
        akses.satu_sehat_kirim_clinicalimpression=isadmin;
        akses.penilaian_pasien_terminal=isadmin;
        akses.surat_persetujuan_rawat_inap=isadmin;
        akses.monitoring_reaksi_tranfusi=isadmin;
        akses.penilaian_korban_kekerasan=isadmin;
        akses.penilaian_lanjutan_resiko_jatuh_lansia=isadmin;
        akses.mpp_skrining=isadmin;
        akses.penilaian_pasien_penyakit_menular=isadmin;
        akses.edukasi_pasien_keluarga_rj=isadmin;
        akses.pemantauan_pews_dewasa=isadmin;
        akses.penilaian_tambahan_bunuh_diri=isadmin;
        akses.bpjs_antrean_pertanggal=isadmin;
        akses.penilaian_tambahan_perilaku_kekerasan=isadmin;
        akses.penilaian_tambahan_beresiko_melarikan_diri=isadmin;
        akses.persetujuan_penundaan_pelayanan=isadmin;
        akses.sisa_diet_pasien=isadmin;
        akses.penilaian_awal_medis_ralan_bedah_mulut=isadmin;
        akses.penilaian_pasien_keracunan=isadmin;
        akses.pemantauan_meows_obstetri=isadmin;
        akses.catatan_adime_gizi=isadmin;
        akses.pengajuan_biaya=isadmin;
        akses.penilaian_awal_keperawatan_ralan_geriatri=isadmin;
        akses.master_masalah_keperawatan_geriatri=isadmin;
        akses.master_rencana_keperawatan_geriatri=isadmin;
        akses.checklist_kriteria_masuk_hcu=isadmin;
        akses.checklist_kriteria_keluar_hcu=isadmin;
        akses.penilaian_risiko_dekubitus=isadmin;
        akses.master_menolak_anjuran_medis=isadmin;
        akses.penolakan_anjuran_medis=isadmin;
        akses.laporan_tahunan_penolakan_anjuran_medis=isadmin;
        akses.template_laporan_operasi=isadmin;
        akses.hasil_tindakan_eswl=isadmin;
        akses.checklist_kriteria_masuk_icu=isadmin;
        akses.checklist_kriteria_keluar_icu=isadmin;
        akses.akses_dokter_lain_rawat_jalan=isadmin;
        akses.follow_up_dbd=isadmin;
        akses.penilaian_risiko_jatuh_neonatus=isadmin;
        akses.persetujuan_pengajuan_biaya=isadmin;
        akses.pemeriksaan_fisik_ralan_per_penyakit=isadmin;
        akses.penilaian_lanjutan_resiko_jatuh_geriatri=isadmin;
        akses.pemantauan_ews_neonatus=isadmin;
        akses.validasi_persetujuan_pengajuan_biaya=isadmin;
        akses.riwayat_perawatan_icare_bpjs=isadmin;
        akses.rekap_pengajuan_biaya=isadmin;
        akses.penilaian_awal_medis_ralan_kulit_kelamin=isadmin;
        akses.akun_host_to_host_bank_mandiri=isadmin;
        akses.penilaian_medis_hemodialisa=isadmin;
        akses.penilaian_level_kecemasan_ranap_anak=isadmin;
        akses.penilaian_lanjutan_resiko_jatuh_psikiatri=isadmin;
        akses.penilaian_lanjutan_skrining_fungsional=isadmin;
        akses.penilaian_medis_ralan_rehab_medik=isadmin;
        akses.laporan_anestesi=isadmin;
        akses.template_persetujuan_penolakan_tindakan=isadmin;
        akses.penilaian_medis_ralan_gawat_darurat_psikiatri=isadmin;
        akses.bpjs_referensi_setting_apotek=isadmin;
        akses.bpjs_referensi_obat_apotek=isadmin;
        akses.bpjs_mapping_obat_apotek=isadmin;
        akses.pembayaran_bank_mandiri=isadmin;
        akses.penilaian_ulang_nyeri=isadmin;
        akses.penilaian_terapi_wicara=isadmin;
        akses.bpjs_obat_23hari_apotek=isadmin;
        akses.pengkajian_restrain=isadmin;
        akses.bpjs_kunjungan_sep_apotek=isadmin;
        akses.bpjs_monitoring_klaim_apotek=isadmin;
        akses.bpjs_daftar_pelayanan_obat_apotek=isadmin;
        akses.penilaian_awal_medis_ralan_paru=isadmin;
        akses.catatan_keperawatan_ralan=isadmin;
        akses.catatan_persalinan=isadmin;
        akses.skor_aldrette_pasca_anestesi=isadmin;
        akses.skor_steward_pasca_anestesi=isadmin;
        akses.skor_bromage_pasca_anestesi=isadmin;
        akses.penilaian_pre_induksi=isadmin;
        akses.hasil_usg_urologi=isadmin;
        akses.hasil_usg_gynecologi=isadmin;
        akses.hasil_pemeriksaan_ekg=isadmin;
        akses.hapus_edit_sep_bpjs=isadmin;
        akses.satu_sehat_kirim_diet=isadmin;
        akses.satu_sehat_mapping_obat=isadmin;
        akses.dapur_ringkasan_pembelian=isadmin;
        akses.satu_sehat_kirim_medication=isadmin;
        akses.satu_sehat_kirim_medicationrequest=isadmin;
        akses.penatalaksanaan_terapi_okupasi=isadmin;
        akses.satu_sehat_kirim_medicationdispense=isadmin;
        akses.edit_hapus_spo_medis=isadmin;
        akses.edit_hapus_spo_nonmedis=isadmin;
        akses.hasil_usg_neonatus=isadmin;
        akses.hasil_endoskopi_faring_laring=isadmin;
        akses.satu_sehat_mapping_radiologi=isadmin;
        akses.satu_sehat_kirim_servicerequest_radiologi=isadmin;
        akses.hasil_endoskopi_hidung=isadmin;
        akses.satu_sehat_kirim_specimen_radiologi=isadmin;
        akses.bpjs_kompilasi_berkas_klaim=isadmin;
        akses.master_masalah_keperawatan_neonatus=isadmin;
        akses.master_rencana_keperawatan_neonatus=isadmin;
        akses.penilaian_awal_keperawatan_ranap_neonatus=isadmin;
        akses.satu_sehat_kirim_observation_radiologi=isadmin;
        akses.satu_sehat_kirim_diagnosticreport_radiologi=isadmin;
        akses.hasil_endoskopi_telinga=isadmin;
        akses.satu_sehat_mapping_lab=isadmin;
        akses.satu_sehat_kirim_servicerequest_lab=isadmin;
        akses.satu_sehat_kirim_servicerequest_labmb=isadmin;
        akses.satu_sehat_kirim_specimen_lab=isadmin;
        akses.satu_sehat_kirim_specimen_labmb=isadmin;
        akses.satu_sehat_kirim_observation_lab=isadmin;
        akses.satu_sehat_kirim_observation_labmb=isadmin;
        akses.satu_sehat_kirim_diagnosticreport_lab=isadmin;
        akses.satu_sehat_kirim_diagnosticreport_labmb=isadmin;
        akses.kepatuhan_kelengkapan_keselamatan_bedah=isadmin;
        akses.nilai_piutang_perjenis_bayar_per_bulan=isadmin;
        akses.ringkasan_piutang_jenis_bayar=isadmin;
        akses.penilaian_pasien_imunitas_rendah=isadmin;
        akses.balance_cairan=isadmin;
        akses.catatan_observasi_chbp=isadmin;
        akses.catatan_observasi_induksi_persalinan=isadmin;
        akses.skp_kategori_penilaian=isadmin;
        akses.skp_kriteria_penilaian=isadmin;
        akses.skp_penilaian=isadmin;
        akses.referensi_poli_mobilejknfktp=isadmin;
        akses.referensi_dokter_mobilejknfktp=isadmin;
        akses.skp_rekapitulasi_penilaian=isadmin;
        akses.pembayaran_pihak_ke3_bankmandiri=isadmin;
        akses.metode_pembayaran_bankmandiri=isadmin;
        akses.bank_tujuan_transfer_bankmandiri=isadmin;
        akses.kodetransaksi_tujuan_transfer_bankmandiri=isadmin;
        akses.konsultasi_medik=isadmin;
        akses.jawaban_konsultasi_medik=isadmin;
        akses.pcare_cek_alergi=isadmin;
        akses.pcare_cek_prognosa=isadmin;
        akses.data_sasaran_usiaproduktif=isadmin;
        akses.data_sasaran_usialansia=isadmin;
        akses.skrining_perilaku_merokok_sekolah_remaja=isadmin;
        akses.skrining_kekerasan_pada_perempuan=isadmin;
        akses.skrining_obesitas=isadmin;
        akses.skrining_risiko_kanker_payudara=isadmin;
        akses.skrining_risiko_kanker_paru=isadmin;
        akses.skrining_tbc=isadmin;
        akses.skrining_kesehatan_gigi_mulut_remaja=isadmin;
        akses.penilaian_awal_keperawatan_ranap_bayi=isadmin;
        akses.booking_mcu_perusahaan=isadmin;
        akses.catatan_observasi_restrain_nonfarma=isadmin;
        akses.catatan_observasi_ventilator=isadmin;
        akses.catatan_anestesi_sedasi=isadmin;
        akses.skrining_puma=isadmin;
        akses.satu_sehat_kirim_careplan=isadmin;
        akses.satu_sehat_kirim_medicationstatement=isadmin;
        akses.skrining_adiksi_nikotin=isadmin;
        akses.skrining_thalassemia=isadmin;
        akses.skrining_instrumen_sdq=isadmin;
        akses.skrining_instrumen_srq=isadmin;
        akses.checklist_pemberian_fibrinolitik=isadmin;
        akses.skrining_kanker_kolorektal=isadmin;
        akses.dapur_pemesanan=isadmin;
        akses.bayar_pesan_dapur=isadmin;
        akses.hutang_dapur=isadmin;
        akses.titip_faktur_dapur=isadmin;
        akses.validasi_tagihan_dapur=isadmin;
        akses.surat_pemesanan_dapur=isadmin;
        akses.pengajuan_barang_dapur=isadmin;
        akses.dapur_returbeli=isadmin;
        akses.hibah_dapur=isadmin;
        akses.ringkasan_penerimaan_dapur=isadmin;
        akses.ringkasan_pengajuan_dapur=isadmin;
        akses.ringkasan_pemesanan_dapur=isadmin;
        akses.ringkasan_returbeli_dapur=isadmin;
        akses.ringkasan_stokkeluar_dapur=isadmin;
        akses.dapur_stokkeluar_pertanggal=isadmin;
        akses.sirkulasi_dapur=isadmin;
        akses.sirkulasi_dapur2=isadmin;
        akses.verifikasi_penerimaan_dapur=isadmin;
        akses.nilai_penerimaan_vendor_dapur_perbulan=isadmin;
        akses.ringkasan_hutang_vendor_dapur=isadmin;
        akses.pindah_kamar_pilihan_2=isadmin;
        akses.penilaian_psikologi_klinis=isadmin;
        akses.penilaian_awal_medis_ranap_neonatus=isadmin;
        akses.penilaian_derajat_dehidrasi=isadmin;
        akses.ringkasan_jasa_tindakan_medis=isadmin;
        akses.pendapatan_per_akun=isadmin;
        akses.hasil_pemeriksaan_echo=isadmin;
        akses.penilaian_bayi_baru_lahir=isadmin;
        akses.rl1_3_ketersediaan_kamar=isadmin;
        akses.pendapatan_per_akun_closing=isadmin;
        akses.pengeluaran_pengeluaran=isadmin;
        akses.skrining_diabetes_melitus=isadmin;
        akses.laporan_tindakan=isadmin;
        akses.pelaksanaan_informasi_edukasi=isadmin;
        akses.layanan_kedokteran_fisik_rehabilitasi=isadmin;
        akses.skrining_kesehatan_gigi_mulut_balita=isadmin;
        akses.skrining_anemia=isadmin;
        akses.layanan_program_kfr=isadmin;
        akses.skrining_hipertensi=isadmin;
        akses.skrining_kesehatan_penglihatan=isadmin;
        akses.catatan_observasi_hemodialisa=isadmin;
        akses.skrining_kesehatan_gigi_mulut_dewasa=isadmin;
        akses.skrining_risiko_kanker_serviks=isadmin;
        akses.catatan_cairan_hemodialisa=isadmin;
        akses.skrining_kesehatan_gigi_mulut_lansia=isadmin;
        akses.skrining_indra_pendengaran=isadmin;
        akses.catatan_pengkajian_paska_operasi=isadmin;
        akses.skrining_frailty_syndrome=isadmin;
        akses.sirkulasi_cssd=isadmin;
        akses.lama_pelayanan_cssd=isadmin;
        akses.catatan_observasi_bayi=isadmin;
        akses.riwayat_surat_peringatan=isadmin;
        akses.master_kesimpulan_anjuran_mcu=isadmin;
        akses.kategori_piutang_jasa_perusahaan=isadmin;
        akses.piutang_jasa_perusahaan=isadmin;
        akses.bayar_piutang_jasa_perusahaan=isadmin;
        akses.piutang_jasa_perusahaan_belum_lunas=isadmin;
        akses.checklist_kesiapan_anestesi=isadmin;
        akses.piutang_peminjaman_uang_belum_lunas=isadmin;
        akses.hasil_pemeriksaan_slit_lamp=isadmin;
        akses.hasil_pemeriksaan_oct=isadmin;
        akses.beban_hutang_lain=isadmin;
        akses.poli_asal_pasien_ranap=isadmin;
        akses.pemberi_hutang_lain=isadmin;
        akses.dokter_asal_pasien_ranap=isadmin;
        akses.duta_parkir_rekap_keluar=isadmin;
        akses.surat_keterangan_layak_terbang=isadmin;
        akses.bayar_beban_hutang_lain=isadmin;
        akses.surat_persetujuan_pemeriksaan_hiv=isadmin;
        akses.skrining_instrumen_acrs=isadmin;
        akses.surat_pernyataan_memilih_dpjp=isadmin;
        akses.skrining_instrumen_mental_emosional=isadmin;
        akses.pelanggan_lab_kesehatan_lingkungan=isadmin;
        akses.kriteria_masuk_nicu=isadmin;
        akses.kriteria_keluar_nicu=isadmin;
        akses.penilaian_medis_ranap_psikiatrik=isadmin;
        akses.kriteria_masuk_picu=isadmin;
        akses.kriteria_keluar_picu=isadmin;
        akses.master_sampel_bakumutu=isadmin;
        akses.skrining_instrumen_amt=isadmin;
        akses.parameter_pengujian_lab_kesehatan_lingkungan=isadmin;
        akses.bpjs_kirim_obat_smc=isadmin;
        akses.bpjs_edit_kirim_obat_smc=isadmin;
        akses.bpjs_riwayat_obat_smc=isadmin;
        akses.bpjs_riwayat_pelayanan_resep_smc=isadmin;
        akses.nilai_normal_baku_mutu_lab_kesehatan_lingkungan=isadmin;
        akses.skrining_pneumonia_severity_index=isadmin;
        akses.permintaan_pengujian_sampel_lab_kesehatan_lingkungan=isadmin;
        akses.penilaian_awal_medis_ralan_jantung=isadmin;
        akses.penilaian_awal_medis_ralan_urologi=isadmin;
        akses.hasil_pemeriksaan_treadmill=isadmin;
        akses.hasil_pemeriksaan_echo_pediatrik=isadmin;
        akses.template_pelaksanaan_informasi_edukasi=isadmin;
        akses.skrining_instrumen_esat=isadmin;
        akses.penilaian_awal_medis_ranap_jantung=isadmin;
        akses.e_eksekutif=isadmin;
        akses.penugasan_pengujian_sampel_lab_kesehatan_lingkungan=isadmin;
        akses.hasil_pengujian_sampel_lab_kesehatan_lingkungan=isadmin;
        akses.verifikasi_pengujian_sampel_lab_kesehatan_lingkungan=isadmin;
        akses.validasi_pengujian_sampel_lab_kesehatan_lingkungan=isadmin;
        akses.set_pintu_poli=isadmin;
        akses.rekap_pelayanan_lab_kesehatan_lingkungan=isadmin;
        akses.pembayaran_pengujian_sampel_lab_kesehatan_lingkungan=isadmin;
        akses.skrining_curb65=isadmin;
        akses.bpjs_potensi_prb=isadmin;
        akses.pintu_poli=isadmin;
        akses.bpjs_riwayat_pelayanan_obat=isadmin;
        akses.skrining_gizi_kehamilan=isadmin;
        akses.bpjs_rekap_peserta_prb_apotek=isadmin;
        akses.bpjs_riwayat_surat_smc=isadmin;
        akses.serah_terima_anggota_tubuh_barang=isadmin;
        akses.pcra_icra_jenis_aktivitas_proyek=isadmin;
        akses.pcra_icra_lokasi_kelompok_risiko_area=isadmin;
        akses.pcra_icra_kelas_risiko_pencegahan=isadmin;
        akses.pcra_icra_tindakan_pengendalian=isadmin;
        akses.pcra_icra_identifkasi_risiko_infeksi=isadmin;
        akses.pcra_icra_identifkasi_risiko_keselamatan=isadmin;
        akses.pcra_icra_identifkasi_risiko_kebakaran=isadmin;
        akses.pcra_icra_identifkasi_risiko_utilitas=isadmin;
        akses.bpjs_daftar_resep_apotek=isadmin;
        akses.daftar_permintaan_resep_iterasi_bpjs=isadmin;
        akses.pcra_icra_pengkajian_risiko_prakonstruksi=isadmin;
        akses.pcra_icra_persyaratan_harus_dipenuhi=isadmin;
        akses.satu_sehat_kirim_questionresponse_telaah_farmasi=isadmin;
        akses.satu_sehat_kirim_allergy_intolerance=isadmin;
        akses.konsultasi_perawat=isadmin;
        akses.jawaban_konsultasi_perawat=isadmin;
        akses.bridging_smart_klaim_bpjs=isadmin;
        akses.mapping_prosedur_smart_klaim_bpjs=isadmin;
        akses.mapping_penyakit_smart_klaim_bpjs=isadmin;
        akses.permintaan_binrohtal=isadmin;
        akses.surat_permintaan_perlindungan_dari_kekerasan=isadmin;
        akses.surat_permohonan_privasi=isadmin;
        akses.surat_permintaan_second_opinion=isadmin;
        akses.surat_keterangan_berobat=isadmin;
        akses.surat_penolakan_resusitasi=isadmin;
        akses.catatan_observasi_ruang_ok=isadmin;
        akses.hasil_pemeriksaan_usg_abdomen=isadmin;
        akses.intervensi_nyeri_farmakologi=isadmin;
        akses.edit=isadmin;
        akses.tglSelesai=-1;
    }

    public static int getjml1() {return akses.jml1;}
    public static int getjml2() {return akses.jml2;}
    public static boolean getadmin(){return akses.admin;}
    public static boolean getuser(){return akses.user;}
    public static boolean gete_eksekutif(){return akses.e_eksekutif;}
    public static boolean getvakum(){return akses.vakum;}
    public static boolean getaplikasi(){return akses.aplikasi;}
    public static boolean getpenyakit(){return akses.penyakit;}
    public static boolean getobat_penyakit(){return akses.obat_penyakit;}
    public static boolean getdokter(){return akses.dokter;}
    public static boolean getjadwal_praktek(){return akses.jadwal_praktek;}
    public static boolean getpetugas(){return akses.petugas;}
    public static boolean getpasien(){return akses.pasien;}
    public static boolean getregistrasi(){return akses.registrasi;}
    public static boolean gettindakan_ralan(){return akses.tindakan_ralan;}
    public static boolean getkamar_inap(){return akses.kamar_inap;}
    public static boolean gettindakan_ranap(){return akses.tindakan_ranap;}
    public static boolean getoperasi(){return akses.operasi;}
    public static boolean getrujukan_keluar(){return akses.rujukan_keluar;}
    public static boolean getrujukan_masuk(){return akses.rujukan_masuk;}
    public static boolean getberi_obat(){return akses.beri_obat;}
    public static boolean getresep_pulang(){return akses.resep_pulang;}
    public static boolean getpasien_meninggal(){return akses.pasien_meninggal;}
    public static boolean getdiet_pasien(){return akses.diet_pasien;}
    public static boolean getkelahiran_bayi(){return akses.kelahiran_bayi;}
    public static boolean getperiksa_lab(){return akses.periksa_lab;}
    public static boolean getperiksa_radiologi(){return akses.periksa_radiologi;}
    public static boolean getkasir_ralan(){return akses.kasir_ralan;}
    public static boolean getdeposit_pasien(){return akses.deposit_pasien;}
    public static boolean getpiutang_pasien(){return akses.piutang_pasien;}
    public static boolean getpeminjaman_berkas(){return akses.peminjaman_berkas;}
    public static boolean getbarcode(){return akses.barcode;}
    public static boolean getpresensi_harian(){return akses.presensi_harian;}
    public static boolean getpresensi_bulanan(){return akses.presensi_bulanan;}
    public static boolean getpegawai_admin(){return akses.pegawai_admin;}
    public static boolean getpegawai_user(){return akses.pegawai_user;}
    public static boolean getsuplier(){return akses.suplier;}
    public static boolean getsatuan_barang(){return akses.satuan_barang;}
    public static boolean getkonversi_satuan(){return akses.konversi_satuan;}
    public static boolean getjenis_barang(){return akses.jenis_barang;}
    public static boolean getobat(){return akses.obat;}
    public static boolean getstok_opname_obat(){return akses.stok_opname_obat;}
    public static boolean getstok_obat_pasien(){return akses.stok_obat_pasien;}
    public static boolean getpengadaan_obat(){return akses.pengadaan_obat;}
    public static boolean getpemesanan_obat(){return akses.pemesanan_obat;}
    public static boolean getpenjualan_obat(){return akses.penjualan_obat;}
    public static void setpenjualan_obatfalse(){akses.penjualan_obat=false;}
    public static boolean getpiutang_obat(){return akses.piutang_obat;}
    public static boolean getretur_ke_suplier(){return akses.retur_ke_suplier;}
    public static boolean getretur_dari_pembeli(){return akses.retur_dari_pembeli;}
    public static boolean getretur_obat_ranap(){return akses.retur_obat_ranap;}
    public static boolean getretur_piutang_pasien(){return akses.retur_piutang_pasien;}
    public static boolean getkeuntungan_penjualan(){return akses.keuntungan_penjualan;}
    public static boolean getkeuntungan_beri_obat(){return akses.keuntungan_beri_obat;}
    public static boolean getsirkulasi_obat(){return akses.sirkulasi_obat;}
    public static boolean getipsrs_barang(){return akses.ipsrs_barang;}
    public static boolean getipsrs_pengadaan_barang(){return akses.ipsrs_pengadaan_barang;}
    public static boolean getipsrs_stok_keluar(){return akses.ipsrs_stok_keluar;}
    public static boolean getipsrs_rekap_pengadaan(){return akses.ipsrs_rekap_pengadaan;}
    public static boolean getipsrs_rekap_stok_keluar(){return akses.ipsrs_rekap_stok_keluar;}
    public static boolean getipsrs_pengeluaran_harian(){return akses.ipsrs_pengeluaran_harian;}
    public static boolean getipsrs_jenis_barang(){return akses.ipsrs_jenis_barang;}
    public static boolean getinventaris_jenis(){return akses.inventaris_jenis;}
    public static boolean getinventaris_kategori(){return akses.inventaris_kategori;}
    public static boolean getinventaris_merk(){return akses.inventaris_merk;}
    public static boolean getinventaris_ruang(){return akses.inventaris_ruang;}
    public static boolean getinventaris_produsen(){return akses.inventaris_produsen;}
    public static boolean getinventaris_koleksi(){return akses.inventaris_koleksi;}
    public static boolean getinventaris_inventaris(){return akses.inventaris_inventaris;}
    public static boolean getinventaris_sirkulasi(){return akses.inventaris_sirkulasi;}
    public static boolean getparkir_jenis(){return akses.parkir_jenis;}
    public static boolean getparkir_in(){return akses.parkir_in;}
    public static boolean getparkir_out(){return akses.parkir_out;}
    public static boolean getparkir_rekap_harian(){return akses.parkir_rekap_harian;}
    public static boolean getparkir_rekap_bulanan(){return akses.parkir_rekap_bulanan;}
    public static boolean getinformasi_kamar(){return akses.informasi_kamar;}
    public static boolean getharian_tindakan_poli(){return akses.harian_tindakan_poli;}
    public static boolean getobat_per_poli(){return akses.obat_per_poli;}
    public static boolean getobat_per_kamar(){return akses.obat_per_kamar;}
    public static boolean getobat_per_dokter_ralan(){return akses.obat_per_dokter_ralan;}
    public static boolean getobat_per_dokter_ranap(){return akses.obat_per_dokter_ranap;}
    public static boolean getharian_dokter(){return akses.harian_dokter;}
    public static boolean getbulanan_dokter(){return akses.bulanan_dokter;}
    public static boolean getharian_paramedis(){return akses.harian_paramedis;}
    public static boolean getbulanan_paramedis(){return akses.bulanan_paramedis;}
    public static boolean getpembayaran_ralan(){return akses.pembayaran_ralan;}
    public static boolean getpembayaran_ranap(){return akses.pembayaran_ranap;}
    public static boolean getrekap_pembayaran_ralan(){return akses.rekap_pembayaran_ralan;}
    public static boolean getrekap_pembayaran_ranap(){return akses.rekap_pembayaran_ranap;}
    public static boolean gettagihan_masuk(){return akses.tagihan_masuk;}
    public static boolean gettambahan_biaya(){return akses.tambahan_biaya;}
    public static boolean getpotongan_biaya(){return akses.potongan_biaya;}
    public static boolean getresep_obat(){return akses.resep_obat;}
    public static boolean getresume_pasien(){return akses.resume_pasien;}
    public static boolean getpenyakit_ralan(){return akses.penyakit_ralan;}
    public static boolean getpenyakit_ranap(){return akses.penyakit_ranap;}
    public static boolean getkamar(){return akses.kamar;}
    public static boolean gettarif_ralan(){return akses.tarif_ralan;}
    public static boolean gettarif_ranap(){return akses.tarif_ranap;}
    public static boolean gettarif_lab(){return akses.tarif_lab;}
    public static boolean gettarif_radiologi(){return akses.tarif_radiologi;}
    public static boolean gettarif_operasi(){return akses.tarif_operasi;}
    public static boolean getakun_rekening(){return akses.akun_rekening;}
    public static boolean getrekening_tahun(){return akses.rekening_tahun;}
    public static boolean getposting_jurnal(){return akses.posting_jurnal;}
    public static boolean getbuku_besar(){return akses.buku_besar;}
    public static boolean getcashflow(){return akses.cashflow;}
    public static boolean getkeuangan(){return akses.keuangan;}
    public static boolean getpengeluaran(){return akses.pengeluaran;}
    public static boolean getsetup_pjlab(){return akses.setup_pjlab;}
    public static boolean getsetup_otolokasi(){return akses.setup_otolokasi;}
    public static boolean getsetup_jam_kamin(){return akses.setup_jam_kamin;}
    public static boolean getsetup_embalase(){return akses.setup_embalase;}
    public static boolean gettracer_login(){return akses.tracer_login;}
    public static boolean getdisplay(){return akses.display;}
    public static boolean getset_harga_obat(){return akses.set_harga_obat;}
    public static boolean getset_penggunaan_tarif(){return akses.set_penggunaan_tarif;}
    public static boolean getset_oto_ralan(){return akses.set_oto_ralan;}
    public static boolean getbiaya_harian(){return akses.biaya_harian;}
    public static boolean getbiaya_masuk_sekali(){return akses.biaya_masuk_sekali;}
    public static boolean getset_no_rm(){return akses.set_no_rm;}
    public static boolean getbilling_ralan(){return akses.billing_ralan;}
    public static boolean getbilling_ranap(){return akses.billing_ranap;}
    public static String getkode(){return akses.kode;}
    public static void setkdbangsal(String kdbangsal){akses.kdbangsal=kdbangsal;}
    public static String getkdbangsal(){return akses.kdbangsal;}
    public static void setalamatip(String alamatip){akses.alamatip=alamatip;}
    public static String getalamatip(){return akses.alamatip;}
    public static void setform(String form){akses.form=form;}
    public static String getform(){return akses.form;}
    public static void setnamauser(String namauser){akses.namauser=namauser;}
    public static String getnamauser(){return akses.namauser;}
    public static void setstatus(boolean status){akses.status=status;}
    public static boolean getstatus(){return akses.status;}
    public static boolean getjm_ranap_dokter(){return akses.jm_ranap_dokter;}
    public static boolean getigd(){return akses.igd;}
    public static boolean getbarcoderalan(){return akses.barcoderalan;}
    public static boolean getbarcoderanap(){return akses.barcoderanap;}
    public static boolean getset_harga_obat_ralan(){return akses.set_harga_obat_ralan;}
    public static boolean getset_harga_obat_ranap(){return akses.set_harga_obat_ranap;}
    public static boolean getpenyakit_pd3i(){return akses.penyakit_pd3i;}
    public static boolean getsurveilans_pd3i(){return akses.surveilans_pd3i;}
    public static boolean getsurveilans_ralan(){return akses.surveilans_ralan;}
    public static boolean getdiagnosa_pasien(){return akses.diagnosa_pasien;}
    public static boolean getsurveilans_ranap(){return akses.surveilans_ranap;}
    public static boolean getpny_takmenular_ranap(){return akses.pny_takmenular_ranap;}
    public static boolean getpny_takmenular_ralan(){return akses.pny_takmenular_ralan;}
    public static void setnamars(String namars){akses.namars=namars;}
    public static void setalamatrs(String alamatrs){akses.alamatrs=alamatrs;}
    public static void setkabupatenrs(String kabupatenrs){akses.kabupatenrs=kabupatenrs;}
    public static void setpropinsirs(String propinsirs){akses.propinsirs=propinsirs;}
    public static void setkontakrs(String kontakrs){akses.kontakrs=kontakrs;}
    public static void setemailrs(String emailrs){akses.emailrs=emailrs;}
    public static void setkodeppkbpjs(String kode_ppk){akses.kode_ppk=kode_ppk;}
    public static void setkodeppkkemenkes(String kode_ppk){akses.kode_ppk_kemenkes=kode_ppk;}
    public static String getnamars(){return akses.namars;}
    public static String getalamatrs(){return akses.alamatrs;}
    public static String getkabupatenrs(){return akses.kabupatenrs;}
    public static String getpropinsirs(){return akses.propinsirs;}
    public static String getkontakrs(){return akses.kontakrs;}
    public static String getemailrs(){return akses.emailrs;}
    public static String getkodeppkbpjs(){return akses.kode_ppk;}
    public static String getkodeppkkemenkes(){return akses.kode_ppk_kemenkes;}
    public static boolean getkunjungan_ralan(){return akses.kunjungan_ralan;}
    public static boolean getrl32(){return akses.rl32;}
    public static boolean getrl33(){return akses.rl33;}
    public static boolean getrl37(){return akses.rl37;}
    public static boolean getrl38(){return akses.rl38;}
    public static boolean getharian_tindakan_dokter(){return akses.harian_tindakan_dokter;}
    public static boolean getsms(){return akses.sms;}
    public static boolean getsidikjari(){return akses.sidikjari;}
    public static boolean getjam_masuk(){return akses.jam_masuk;}
    public static boolean getjadwal_pegawai(){return akses.jadwal_pegawai;}
    public static boolean getparkir_barcode(){return akses.parkir_barcode;}
    public static boolean getset_nota(){return akses.set_nota;}
    public static boolean getdpjp_ranap(){return akses.dpjp_ranap;}
    public static boolean getmutasi_barang(){return akses.mutasi_barang;}
    public static boolean getrl34(){return akses.rl34;}
    public static boolean getrl36(){return akses.rl36;}
    public static boolean getfee_visit_dokter(){return akses.fee_visit_dokter;}
    public static boolean getfee_bacaan_ekg(){return akses.fee_bacaan_ekg;}
    public static boolean getfee_rujukan_rontgen(){return akses.fee_rujukan_rontgen;}
    public static boolean getfee_rujukan_ranap(){return akses.fee_rujukan_ranap;}
    public static boolean getfee_ralan(){return akses.fee_ralan;}
    public static boolean getakun_bayar(){return akses.akun_bayar;}
    public static boolean getbayar_pemesanan_obat(){return akses.bayar_pemesanan_obat;}
    public static boolean getobat_per_dokter_peresep(){return akses.obat_per_dokter_peresep;}
    public static boolean getpemasukan_lain(){return akses.pemasukan_lain;}
    public static boolean getpengaturan_rekening(){return akses.pengaturan_rekening;}
    public static boolean getclosing_kasir(){return akses.closing_kasir;}
    public static boolean getketerlambatan_presensi(){return akses.keterlambatan_presensi;}
    public static boolean getset_harga_kamar(){return akses.set_harga_kamar;}
    public static boolean getrekap_per_shift(){return akses.rekap_per_shift;}
    public static boolean getbpjs_cek_nik(){return akses.bpjs_cek_nik;}
    public static boolean getbpjs_cek_kartu(){return akses.bpjs_cek_kartu;}
    public static boolean getbpjs_cek_riwayat(){return akses.bpjs_cek_riwayat;}
    public static boolean getobat_per_cara_bayar(){return akses.obat_per_cara_bayar;}
    public static boolean getkunjungan_ranap(){return akses.kunjungan_ranap;}
    public static boolean getbayar_piutang(){return akses.bayar_piutang;}
    public static boolean getpayment_point(){return akses.payment_point;}
    public static boolean getbpjs_cek_nomor_rujukan(){return akses.bpjs_cek_nomor_rujukan;}
    public static boolean geticd9(){return akses.icd9;}
    public static boolean getdarurat_stok(){return akses.darurat_stok;}
    public static boolean getretensi_rm(){return akses.retensi_rm;}
    public static boolean gettemporary_presensi(){return akses.temporary_presensi;}
    public static boolean getjurnal_harian(){return akses.jurnal_harian;}
    public static boolean getsirkulasi_obat2(){return akses.sirkulasi_obat2;}
    public static boolean getedit_registrasi(){return akses.edit_registrasi;}
    public static boolean getbpjs_referensi_diagnosa(){return akses.bpjs_referensi_diagnosa;}
    public static boolean getbpjs_referensi_poli(){return akses.bpjs_referensi_poli;}
    public static boolean getindustrifarmasi(){return akses.industrifarmasi;}
    public static boolean getharian_js(){return akses.harian_js;}
    public static boolean getbulanan_js(){return akses.bulanan_js;}
    public static boolean getharian_paket_bhp(){return akses.harian_paket_bhp;}
    public static boolean getbulanan_paket_bhp(){return akses.bulanan_paket_bhp;}
    public static boolean getpiutang_pasien2(){return akses.piutang_pasien2;}
    public static boolean getbpjs_referensi_faskes(){return akses.bpjs_referensi_faskes;}
    public static boolean getbpjs_sep(){return akses.bpjs_sep;}
    public static boolean getpengambilan_utd(){return akses.pengambilan_utd;}
    public static boolean gettarif_utd(){return akses.tarif_utd;}
    public static boolean getpengambilan_utd2(){return akses.pengambilan_utd2;}
    public static boolean getutd_medis_rusak(){return akses.utd_medis_rusak;}
    public static boolean getpengambilan_penunjang_utd(){return akses.pengambilan_penunjang_utd;}
    public static boolean getpengambilan_penunjang_utd2(){return akses.pengambilan_penunjang_utd2;}
    public static boolean getutd_penunjang_rusak(){return akses.utd_penunjang_rusak;}
    public static boolean getsuplier_penunjang(){return akses.suplier_penunjang;}
    public static boolean getutd_donor(){return akses.utd_donor;}
    public static boolean getbpjs_monitoring_klaim(){return akses.bpjs_monitoring_klaim;}
    public static boolean getutd_cekal_darah(){return akses.utd_cekal_darah;}
    public static boolean getutd_komponen_darah(){return akses.utd_komponen_darah;}
    public static boolean getutd_stok_darah(){return akses.utd_stok_darah;}
    public static boolean getutd_pemisahan_darah(){return akses.utd_pemisahan_darah;}
    public static boolean getharian_kamar(){return akses.harian_kamar;}
    public static boolean getrincian_piutang_pasien(){return akses.rincian_piutang_pasien;}
    public static boolean getkeuntungan_beri_obat_nonpiutang(){return akses.keuntungan_beri_obat_nonpiutang;}
    public static boolean getreklasifikasi_ralan(){return akses.reklasifikasi_ralan;}
    public static boolean getreklasifikasi_ranap(){return akses.reklasifikasi_ranap;}
    public static boolean getutd_penyerahan_darah(){return akses.utd_penyerahan_darah;}
    public static void setutd_penyerahan_darahfalse(){akses.utd_penyerahan_darah=false;}
    public static boolean gethutang_obat(){return akses.hutang_obat;}
    public static boolean getriwayat_obat_alkes_bhp(){return akses.riwayat_obat_alkes_bhp;}
    public static boolean getsensus_harian_poli(){return akses.sensus_harian_poli;}
    public static boolean getrl4a(){return akses.rl4a;}
    public static boolean getaplicare_referensi_kamar(){return akses.aplicare_referensi_kamar;}
    public static boolean getaplicare_ketersediaan_kamar(){return akses.aplicare_ketersediaan_kamar;}
    public static boolean getinacbg_klaim_baru_otomatis(){return akses.inacbg_klaim_baru_otomatis;}
    public static boolean getinacbg_klaim_baru_manual(){return akses.inacbg_klaim_baru_manual;}
    public static boolean getinacbg_coder_nik(){return akses.inacbg_coder_nik;}
    public static boolean getmutasi_berkas(){return akses.mutasi_berkas;}
    public static boolean getakun_piutang(){return akses.akun_piutang;}
    public static boolean getharian_kso(){return akses.harian_kso;}
    public static boolean getbulanan_kso(){return akses.bulanan_kso;}
    public static boolean getharian_menejemen(){return akses.harian_menejemen;}
    public static boolean getbulanan_menejemen(){return akses.bulanan_menejemen;}
    public static boolean getinhealth_cek_eligibilitas(){return akses.inhealth_cek_eligibilitas;}
    public static boolean getinhealth_referensi_jenpel_ruang_rawat(){return akses.inhealth_referensi_jenpel_ruang_rawat;}
    public static boolean getinhealth_referensi_poli(){return akses.inhealth_referensi_poli;}
    public static boolean getinhealth_referensi_faskes(){return akses.inhealth_referensi_faskes;}
    public static boolean getinhealth_sjp(){return akses.inhealth_sjp;}
    public static boolean getpiutang_ralan(){return akses.piutang_ralan;}
    public static boolean getpiutang_ranap(){return akses.piutang_ranap;}
    public static boolean getdetail_piutang_penjab(){return akses.detail_piutang_penjab;}
    public static boolean getlama_pelayanan_ralan(){return akses.lama_pelayanan_ralan;}
    public static boolean getcatatan_pasien(){return akses.catatan_pasien;}
    public static boolean getrl4b(){return akses.rl4b;}
    public static boolean getrl4asebab(){return akses.rl4asebab;}
    public static boolean getrl4bsebab(){return akses.rl4bsebab;}
    public static boolean getdata_HAIs(){return akses.data_HAIs;}
    public static boolean getharian_HAIs(){return akses.harian_HAIs;}
    public static boolean getbulanan_HAIs(){return akses.bulanan_HAIs;}
    public static boolean gethitung_bor(){return akses.hitung_bor;}
    public static boolean getperusahaan_pasien(){return akses.perusahaan_pasien;}
    public static boolean getresep_dokter(){return akses.resep_dokter;}
    public static void setresep_dokterfalse(){akses.resep_dokter=false;}
    public static boolean getlama_pelayanan_apotek(){return akses.lama_pelayanan_apotek;}
    public static boolean gethitung_alos(){return akses.hitung_alos;}
    public static boolean getdetail_tindakan(){return akses.detail_tindakan;}
    public static boolean getrujukan_poli_internal(){return akses.rujukan_poli_internal;}
    public static boolean getrekap_poli_anak(){return akses.rekap_poli_anak;}
    public static boolean getgrafik_kunjungan_poli(){return akses.grafik_kunjungan_poli;}
    public static boolean getgrafik_kunjungan_perdokter(){return akses.grafik_kunjungan_perdokter;}
    public static boolean getgrafik_kunjungan_perpekerjaan(){return akses.grafik_kunjungan_perpekerjaan;}
    public static boolean getgrafik_kunjungan_perpendidikan(){return akses.grafik_kunjungan_perpendidikan;}
    public static boolean getgrafik_kunjungan_pertahun(){return akses.grafik_kunjungan_pertahun;}
    public static boolean getberkas_digital_perawatan(){return akses.berkas_digital_perawatan;}
    public static boolean getpenyakit_menular_ranap(){return akses.penyakit_menular_ranap;}
    public static boolean getpenyakit_menular_ralan(){return akses.penyakit_menular_ralan;}
    public static boolean getgrafik_kunjungan_perbulan(){return akses.grafik_kunjungan_perbulan;}
    public static boolean getgrafik_kunjungan_pertanggal(){return akses.grafik_kunjungan_pertanggal;}
    public static boolean getgrafik_kunjungan_demografi(){return akses.grafik_kunjungan_demografi;}
    public static boolean getgrafik_kunjungan_statusdaftartahun(){return akses.grafik_kunjungan_statusdaftartahun;}
    public static boolean getgrafik_kunjungan_statusdaftartahun2(){return akses.grafik_kunjungan_statusdaftartahun2;}
    public static boolean getgrafik_kunjungan_statusdaftarbulan(){return akses.grafik_kunjungan_statusdaftarbulan;}
    public static boolean getgrafik_kunjungan_statusdaftarbulan2(){return akses.grafik_kunjungan_statusdaftarbulan2;}
    public static boolean getgrafik_kunjungan_statusdaftartanggal(){return akses.grafik_kunjungan_statusdaftartanggal;}
    public static boolean getgrafik_kunjungan_statusdaftartanggal2(){return akses.grafik_kunjungan_statusdaftartanggal2;}
    public static boolean getgrafik_kunjungan_statusbataltahun(){return akses.grafik_kunjungan_statusbataltahun;}
    public static boolean getgrafik_kunjungan_statusbatalbulan(){return akses.grafik_kunjungan_statusbatalbulan;}
    public static boolean getpcare_cek_penyakit(){return akses.pcare_cek_penyakit;}
    public static boolean getgrafik_kunjungan_statusbataltanggal(){return akses.grafik_kunjungan_statusbataltanggal;}
    public static boolean getkategori_barang(){return akses.kategori_barang;}
    public static boolean getgolongan_barang(){return akses.golongan_barang;}
    public static boolean getpemberian_obat_pertanggal(){return akses.pemberian_obat_pertanggal;}
    public static boolean getpenjualan_obat_pertanggal(){return akses.penjualan_obat_pertanggal;}
    public static boolean getpcare_cek_kesadaran(){return akses.pcare_cek_kesadaran;}
    public static boolean getpembatalan_periksa_dokter(){return akses.pembatalan_periksa_dokter;}
    public static boolean getpembayaran_per_unit(){return akses.pembayaran_per_unit;}
    public static boolean getrekap_pembayaran_per_unit(){return akses.rekap_pembayaran_per_unit;}
    public static boolean getgrafik_kunjungan_percarabayar(){return akses.grafik_kunjungan_percarabayar;}
    public static boolean getipsrs_pengadaan_pertanggal(){return akses.ipsrs_pengadaan_pertanggal;}
    public static boolean getipsrs_stokkeluar_pertanggal(){return akses.ipsrs_stokkeluar_pertanggal;}
    public static boolean getgrafik_kunjungan_ranaptahun(){return akses.grafik_kunjungan_ranaptahun;}
    public static boolean getpcare_cek_rujukan(){return akses.pcare_cek_rujukan;}
    public static boolean getgrafik_lab_ralantahun(){return akses.grafik_lab_ralantahun;}
    public static boolean getgrafik_rad_ralantahun(){return akses.grafik_rad_ralantahun;}
    public static boolean getcek_entry_ralan(){return akses.cek_entry_ralan;}
    public static boolean getinacbg_klaim_baru_manual2(){return akses.inacbg_klaim_baru_manual2;}
    public static boolean getpermintaan_medis(){return akses.permintaan_medis;}
    public static boolean getrekap_permintaan_medis(){return akses.rekap_permintaan_medis;}
    public static boolean getsurat_pemesanan_medis(){return akses.surat_pemesanan_medis;}
    public static boolean getpermintaan_non_medis(){return akses.permintaan_non_medis;}
    public static boolean getrekap_permintaan_non_medis(){return akses.rekap_permintaan_non_medis;}
    public static boolean getsurat_pemesanan_non_medis(){return akses.surat_pemesanan_non_medis;}
    public static boolean getgrafik_per_perujuk(){return akses.grafik_per_perujuk;}
    public static boolean getbpjs_cek_prosedur(){return akses.bpjs_cek_prosedur;}
    public static boolean getbpjs_cek_kelas_rawat(){return akses.bpjs_cek_kelas_rawat;}
    public static boolean getbpjs_cek_dokter(){return akses.bpjs_cek_dokter;}
    public static boolean getbpjs_cek_spesialistik(){return akses.bpjs_cek_spesialistik;}
    public static boolean getbpjs_cek_ruangrawat(){return akses.bpjs_cek_ruangrawat;}
    public static boolean getbpjs_cek_carakeluar(){return  akses.bpjs_cek_carakeluar;}
    public static boolean getbpjs_cek_pasca_pulang(){return akses.bpjs_cek_pasca_pulang;}
    public static boolean getdetail_tindakan_okvk(){return akses.detail_tindakan_okvk;}
    public static boolean getbilling_parsial(){return akses.billing_parsial;}
    public static boolean getbpjs_cek_nomor_rujukan_rs(){return akses.bpjs_cek_nomor_rujukan_rs;}
    public static boolean getbpjs_cek_rujukan_kartu_pcare(){return akses.bpjs_cek_rujukan_kartu_pcare;}
    public static boolean getbpjs_cek_rujukan_kartu_rs(){return akses.bpjs_cek_rujukan_kartu_rs;}
    public static boolean getakses_depo_obat(){return akses.akses_depo_obat;}
    public static boolean getbpjs_rujukan_keluar(){return akses.bpjs_rujukan_keluar;}
    public static boolean getgrafik_lab_ralanbulan(){return akses.grafik_lab_ralanbulan;}
    public static boolean getpengeluaran_stok_apotek(){return akses.pengeluaran_stok_apotek;}
    public static boolean getgrafik_rad_ralanbulan(){return akses.grafik_rad_ralanbulan;}
    public static boolean getdetailjmdokter2(){return akses.detailjmdokter2;}
    public static boolean getpengaduan_pasien(){return akses.pengaduan_pasien;}
    public static boolean getgrafik_lab_ralanhari(){return akses.grafik_lab_ralanhari;}
    public static boolean getgrafik_rad_ralanhari(){return akses.grafik_rad_ralanhari;}
    public static boolean getsensus_harian_ralan(){return akses.sensus_harian_ralan;}
    public static boolean getmetode_racik(){return akses.metode_racik;}
    public static boolean getpembayaran_akun_bayar(){return akses.pembayaran_akun_bayar;}
    public static boolean getpengguna_obat_resep(){return akses.pengguna_obat_resep;}
    public static boolean getrekap_pemesanan(){return akses.rekap_pemesanan;}
    public static boolean getmaster_berkas_pegawai(){return akses.master_berkas_pegawai;}
    public static boolean getberkas_kepegawaian(){return akses.berkas_kepegawaian;}
    public static boolean getriwayat_jabatan(){return akses.riwayat_jabatan;}
    public static boolean getriwayat_pendidikan(){return akses.riwayat_pendidikan;}
    public static boolean getriwayat_naik_gaji(){return akses.riwayat_naik_gaji;}
    public static boolean getkegiatan_ilmiah(){return akses.kegiatan_ilmiah;}
    public static boolean getriwayat_penghargaan(){return akses.riwayat_penghargaan;}
    public static boolean getriwayat_penelitian(){return akses.riwayat_penelitian;}
    public static boolean getpenerimaan_non_medis(){return akses.penerimaan_non_medis;}
    public static boolean getbayar_pesan_non_medis(){return akses.bayar_pesan_non_medis;}
    public static boolean gethutang_barang_non_medis(){return akses.hutang_barang_non_medis;}
    public static boolean getrekap_pemesanan_non_medis(){return akses.rekap_pemesanan_non_medis;}
    public static boolean getinsiden_keselamatan(){return akses.insiden_keselamatan;}
    public static boolean getinsiden_keselamatan_pasien(){return akses.insiden_keselamatan_pasien;}
    public static boolean getgrafik_ikp_pertahun(){return akses.grafik_ikp_pertahun;}
    public static boolean getgrafik_ikp_perbulan(){return akses.grafik_ikp_perbulan;}
    public static boolean getgrafik_ikp_pertanggal(){return akses.grafik_ikp_pertanggal;}
    public static boolean getriwayat_data_batch(){return akses.riwayat_data_batch;}
    public static boolean getgrafik_ikp_jenis(){return akses.grafik_ikp_jenis;}
    public static boolean getgrafik_ikp_dampak(){return akses.grafik_ikp_dampak;}
    public static boolean getpiutang_akun_piutang(){return akses.piutang_akun_piutang;}
    public static void setresep_obatfalse(){akses.resep_obat=false;}
    public static boolean getgrafik_kunjungan_per_agama(){return akses.grafik_kunjungan_per_agama;}
    public static boolean getgrafik_kunjungan_per_umur(){return akses.grafik_kunjungan_per_umur;}
    public static boolean getsuku_bangsa(){return akses.suku_bangsa;}
    public static boolean getbahasa_pasien(){return akses.bahasa_pasien;}
    public static boolean getgolongan_tni(){return akses.golongan_tni;}
    public static boolean getsatuan_tni(){return akses.satuan_tni;}
    public static boolean getjabatan_tni(){return akses.jabatan_tni;}
    public static boolean getpangkat_tni(){return akses.pangkat_tni;}
    public static boolean getgolongan_polri(){return akses.golongan_polri;}
    public static boolean getsatuan_polri(){return akses.satuan_polri;}
    public static boolean getjabatan_polri(){return akses.jabatan_polri;}
    public static boolean getpangkat_polri(){return akses.pangkat_polri;}
    public static boolean getcacat_fisik(){return akses.cacat_fisik;}
    public static boolean getgrafik_kunjungan_suku(){return akses.grafik_kunjungan_suku;}
    public static boolean getgrafik_kunjungan_bahasa(){return akses.grafik_kunjungan_bahasa;}
    public static boolean getbooking_operasi(){return akses.booking_operasi;}
    public static boolean getmapping_poli_bpjs(){return akses.mapping_poli_bpjs;}
    public static boolean getgrafik_kunjungan_per_cacat(){return akses.grafik_kunjungan_per_cacat;}
    public static boolean getbarang_cssd(){return akses.barang_cssd;}
    public static boolean getskdp_bpjs(){return akses.skdp_bpjs;}
    public static boolean getbooking_registrasi(){return akses.booking_registrasi;}
    public static boolean getbpjs_cek_propinsi(){return akses.bpjs_cek_propinsi;}
    public static boolean getbpjs_cek_kabupaten(){return akses.bpjs_cek_kabupaten;}
    public static boolean getbpjs_cek_kecamatan(){return akses.bpjs_cek_kecamatan;}
    public static boolean getbpjs_cek_dokterdpjp(){return akses.bpjs_cek_dokterdpjp;}
    public static boolean getbpjs_cek_riwayat_rujukanrs(){return akses.bpjs_cek_riwayat_rujukanrs;}
    public static boolean getbpjs_cek_tanggal_rujukan(){return akses.bpjs_cek_tanggal_rujukan;}
    public static boolean getpermintaan_lab(){return akses.permintaan_lab;}
    public static void setperiksalabfalse(){akses.periksa_lab=false;}
    public static void setperiksalabpafalse(){akses.pemeriksaan_lab_pa=false;}
    public static void setperiksalabmbfalse(){akses.pemeriksaan_lab_mb=false;}
    public static void setpermintaanlabfalse(){akses.permintaan_lab=false;}
    public static boolean getpermintaan_radiologi(){return akses.permintaan_radiologi;}
    public static void setperiksaradiologifalse(){akses.periksa_radiologi=false;}
    public static void setpermintaanradiologifalse(){akses.permintaan_radiologi=false;}
    public static boolean getsurat_indeks(){return akses.surat_indeks;}
    public static boolean getsurat_map(){return akses.surat_map;}
    public static boolean getsurat_almari(){return akses.surat_almari;}
    public static boolean getsurat_rak(){return akses.surat_rak;}
    public static boolean getsurat_ruang(){return akses.surat_ruang;}
    public static boolean getsurat_klasifikasi(){return akses.surat_klasifikasi;}
    public static boolean getsurat_status(){return akses.surat_status;}
    public static boolean getsurat_sifat(){return akses.surat_sifat;}
    public static boolean getsurat_balas(){return akses.surat_balas;}
    public static boolean getsurat_masuk(){return akses.surat_masuk;}
    public static boolean getpcare_cek_dokter(){return akses.pcare_cek_dokter;}
    public static boolean getpcare_cek_poli(){return akses.pcare_cek_poli;}
    public static boolean getpcare_cek_provider(){return akses.pcare_cek_provider;}
    public static boolean getpcare_cek_statuspulang(){return akses.pcare_cek_statuspulang;}
    public static boolean getpcare_cek_spesialis(){return akses.pcare_cek_spesialis;}
    public static boolean getpcare_cek_subspesialis(){return akses.pcare_cek_subspesialis;}
    public static boolean getpcare_cek_sarana(){return akses.pcare_cek_sarana;}
    public static boolean getpcare_cek_khusus(){return akses.pcare_cek_khusus;}
    public static boolean getpcare_cek_obat(){return akses.pcare_cek_obat;}
    public static boolean getpcare_cek_tindakan(){return akses.pcare_cek_tindakan;}
    public static boolean getpcare_cek_faskessubspesialis(){return akses.pcare_cek_faskessubspesialis;}
    public static boolean getpcare_cek_faskesalihrawat(){return akses.pcare_cek_faskesalihrawat;}
    public static boolean getpcare_cek_faskesthalasemia(){return akses.pcare_cek_faskesthalasemia;}
    public static boolean getpcare_mapping_obat(){return akses.pcare_mapping_obat;}
    public static boolean getpcare_mapping_tindakan(){return akses.pcare_mapping_tindakan;}
    public static boolean getpcare_club_prolanis(){return akses.pcare_club_prolanis;}
    public static boolean getpcare_mapping_poli(){return akses.pcare_mapping_poli;}
    public static boolean getpcare_kegiatan_kelompok(){return akses.pcare_kegiatan_kelompok;}
    public static boolean getpcare_mapping_tindakan_ranap(){return akses.pcare_mapping_tindakan_ranap;}
    public static boolean getpcare_peserta_kegiatan_kelompok(){return akses.pcare_peserta_kegiatan_kelompok;}
    public static boolean getsirkulasi_obat3(){return akses.sirkulasi_obat3;}
    public static boolean getbridging_pcare_daftar(){return akses.bridging_pcare_daftar;}
    public static boolean getpcare_mapping_dokter(){return akses.pcare_mapping_dokter;}
    public static boolean getranap_per_ruang(){return akses.ranap_per_ruang;}
    public static boolean getpenyakit_ranap_cara_bayar(){return akses.penyakit_ranap_cara_bayar;}
    public static boolean getanggota_militer_dirawat(){return akses.anggota_militer_dirawat;}
    public static boolean getset_input_parsial(){return akses.set_input_parsial;}
    public static boolean getlama_pelayanan_radiologi(){return akses.lama_pelayanan_radiologi;}
    public static boolean getlama_pelayanan_lab(){return akses.lama_pelayanan_lab;}
    public static boolean getbpjs_cek_sep(){return akses.bpjs_cek_sep;}
    public static boolean getcatatan_perawatan(){return akses.catatan_perawatan;}
    public static boolean getsurat_keluar(){return akses.surat_keluar;}
    public static boolean getkegiatan_farmasi(){return akses.kegiatan_farmasi;}
    public static boolean getstok_opname_logistik(){return akses.stok_opname_logistik;}
    public static boolean getsirkulasi_non_medis(){return akses.sirkulasi_non_medis;}
    public static boolean getrekap_lab_pertahun(){return akses.rekap_lab_pertahun;}
    public static boolean getperujuk_lab_pertahun(){return akses.perujuk_lab_pertahun;}
    public static boolean getrekap_radiologi_pertahun(){return akses.rekap_radiologi_pertahun;}
    public static boolean getperujuk_radiologi_pertahun(){return akses.perujuk_radiologi_pertahun;}
    public static boolean getjumlah_porsi_diet(){return akses.jumlah_porsi_diet;}
    public static boolean getjumlah_macam_diet(){return akses.jumlah_macam_diet;}
    public static boolean getpayment_point2(){return akses.payment_point2;}
    public static boolean getpembayaran_akun_bayar2(){return akses.pembayaran_akun_bayar2;}
    public static boolean gethapus_nota_salah(){return akses.hapus_nota_salah;}
    public static boolean gethais_perbangsal(){return akses.hais_perbangsal;}
    public static boolean getppn_obat(){return akses.ppn_obat;}
    public static boolean getsaldo_akun_perbulan(){return akses.saldo_akun_perbulan;}
    public static boolean getdisplay_apotek(){return akses.display_apotek;}
    public static boolean getsisrute_referensi_faskes(){return akses.sisrute_referensi_faskes;}
    public static boolean getsisrute_referensi_alasanrujuk(){return akses.sisrute_referensi_alasanrujuk;}
    public static boolean getsisrute_referensi_diagnosa(){return akses.sisrute_referensi_diagnosa;}
    public static boolean getsisrute_rujukan_masuk(){return akses.sisrute_rujukan_masuk;}
    public static boolean getAktif(){return akses.aktif;}
    public static void setAktif(boolean status){akses.aktif=status;}
    public static boolean getsisrute_rujukan_keluar(){return akses.sisrute_rujukan_keluar;}
    public static boolean getbpjs_cek_skdp(){return akses.bpjs_cek_skdp;}
    public static boolean getdata_batch(){return akses.data_batch;}
    public static boolean getkunjungan_permintaan_lab(){return akses.kunjungan_permintaan_lab;}
    public static boolean getkunjungan_permintaan_lab2(){return akses.kunjungan_permintaan_lab2;}
    public static boolean getkunjungan_permintaan_radiologi(){return akses.kunjungan_permintaan_radiologi;}
    public static boolean getkunjungan_permintaan_radiologi2(){return akses.kunjungan_permintaan_radiologi2;}
    public static boolean getpcare_pemberian_obat(){return akses.pcare_pemberian_obat;}
    public static boolean getpcare_pemberian_tindakan(){return akses.pcare_pemberian_tindakan;}
    public static boolean getpembayaran_akun_bayar3(){return akses.pembayaran_akun_bayar3;}
    public static boolean getpassword_asuransi(){return akses.password_asuransi;}
    public static boolean getkemenkes_sitt(){return akses.kemenkes_sitt;}
    public static boolean getsiranap_ketersediaan_kamar(){return akses.siranap_ketersediaan_kamar;}
    public static boolean getgrafik_tb_periodelaporan(){return akses.grafik_tb_periodelaporan;}
    public static boolean getgrafik_tb_rujukan(){return akses.grafik_tb_rujukan;}
    public static boolean getgrafik_tb_riwayat(){return akses.grafik_tb_riwayat;}
    public static boolean getgrafik_tb_tipediagnosis(){return akses.grafik_tb_tipediagnosis;}
    public static boolean getgrafik_tb_statushiv(){return akses.grafik_tb_statushiv;}
    public static boolean getgrafik_tb_skoringanak(){return akses.grafik_tb_skoringanak;}
    public static boolean getgrafik_tb_konfirmasiskoring5(){return akses.grafik_tb_konfirmasiskoring5;}
    public static boolean getgrafik_tb_konfirmasiskoring6(){return akses.grafik_tb_konfirmasiskoring6;}
    public static boolean getgrafik_tb_sumberobat(){return akses.grafik_tb_sumberobat;}
    public static boolean getgrafik_tb_hasilakhirpengobatan(){return akses.grafik_tb_hasilakhirpengobatan;}
    public static boolean getgrafik_tb_hasilteshiv(){return akses.grafik_tb_hasilteshiv;}
    public static boolean getkadaluarsa_batch(){return akses.kadaluarsa_batch;}
    public static boolean getsisa_stok(){return akses.sisa_stok;}
    public static boolean getobat_per_resep(){return akses.obat_per_resep;}
    public static boolean getpemakaian_air_pdam(){return akses.pemakaian_air_pdam;}
    public static boolean getlimbah_b3_medis(){return akses.limbah_b3_medis;}
    public static boolean getgrafik_air_pdam_pertanggal(){return akses.grafik_air_pdam_pertanggal;}
    public static boolean getgrafik_air_pdam_perbulan(){return akses.grafik_air_pdam_perbulan;}
    public static boolean getgrafik_limbahb3_pertanggal(){return akses.grafik_limbahb3_pertanggal;}
    public static boolean getgrafik_limbahb3_perbulan(){return akses.grafik_limbahb3_perbulan;}
    public static boolean getlimbah_domestik(){return akses.limbah_domestik;}
    public static boolean getgrafik_limbahdomestik_pertanggal(){return akses.grafik_limbahdomestik_pertanggal;}
    public static boolean getgrafik_limbahdomestik_perbulan(){return akses.grafik_limbahdomestik_perbulan;}
    public static boolean getmutu_air_limbah(){return akses.mutu_air_limbah;}
    public static boolean getpest_control(){return akses.pest_control;}
    public static boolean getruang_perpustakaan(){return akses.ruang_perpustakaan;}
    public static boolean getkategori_perpustakaan(){return akses.kategori_perpustakaan;}
    public static boolean getjenis_perpustakaan(){return akses.jenis_perpustakaan;}
    public static boolean getpengarang_perpustakaan(){return akses.pengarang_perpustakaan;}
    public static boolean getpenerbit_perpustakaan(){return akses.penerbit_perpustakaan;}
    public static boolean getkoleksi_perpustakaan(){return akses.koleksi_perpustakaan;}
    public static boolean getinventaris_perpustakaan(){return akses.inventaris_perpustakaan;}
    public static boolean getset_peminjaman_perpustakaan(){return akses.set_peminjaman_perpustakaan;}
    public static boolean getdenda_perpustakaan(){return akses.denda_perpustakaan;}
    public static boolean getanggota_perpustakaan(){return akses.anggota_perpustakaan;}
    public static boolean getpeminjaman_perpustakaan(){return akses.peminjaman_perpustakaan;}
    public static boolean getbayar_denda_perpustakaan(){return akses.bayar_denda_perpustakaan;}
    public static boolean getebook_perpustakaan(){return akses.ebook_perpustakaan;}
    public static boolean getjenis_cidera_k3rs(){return akses.jenis_cidera_k3rs;}
    public static boolean getpenyebab_k3rs(){return akses.penyebab_k3rs;}
    public static boolean getjenis_luka_k3rs(){return akses.jenis_luka_k3rs;}
    public static boolean getlokasi_kejadian_k3rs(){return akses.lokasi_kejadian_k3rs;}
    public static boolean getdampak_cidera_k3rs(){return akses.dampak_cidera_k3rs;}
    public static boolean getjenis_pekerjaan_k3rs(){return akses.jenis_pekerjaan_k3rs;}
    public static boolean getbagian_tubuh_k3rs(){return akses.bagian_tubuh_k3rs;}
    public static boolean getperistiwa_k3rs(){return akses.peristiwa_k3rs;}
    public static boolean getgrafik_k3_pertahun(){return akses.grafik_k3_pertahun;}
    public static boolean getgrafik_k3_perbulan(){return akses.grafik_k3_perbulan;}
    public static boolean getgrafik_k3_pertanggal(){return akses.grafik_k3_pertanggal;}
    public static boolean getgrafik_k3_perjeniscidera(){return akses.grafik_k3_perjeniscidera;}
    public static boolean getgrafik_k3_perpenyebab(){return akses.grafik_k3_perpenyebab;}
    public static boolean getgrafik_k3_perjenisluka(){return akses.grafik_k3_perjenisluka;}
    public static boolean getgrafik_k3_lokasikejadian(){return akses.grafik_k3_lokasikejadian;}
    public static boolean getgrafik_k3_dampakcidera(){return akses.grafik_k3_dampakcidera;}
    public static boolean getgrafik_k3_perjenispekerjaan(){return akses.grafik_k3_perjenispekerjaan;}
    public static boolean getgrafik_k3_perbagiantubuh(){return akses.grafik_k3_perbagiantubuh;}
    public static boolean getjenis_cidera_k3rstahun(){return akses.jenis_cidera_k3rstahun;}
    public static boolean getpenyebab_k3rstahun(){return akses.penyebab_k3rstahun;}
    public static boolean getjenis_luka_k3rstahun(){return akses.jenis_luka_k3rstahun;}
    public static boolean getlokasi_kejadian_k3rstahun(){return akses.lokasi_kejadian_k3rstahun;}
    public static boolean getdampak_cidera_k3rstahun(){return akses.dampak_cidera_k3rstahun;}
    public static boolean getjenis_pekerjaan_k3rstahun(){return akses.jenis_pekerjaan_k3rstahun;}
    public static boolean getbagian_tubuh_k3rstahun(){return akses.bagian_tubuh_k3rstahun;}
    public static boolean getsekrining_rawat_jalan(){return akses.sekrining_rawat_jalan;}
    public static boolean getbpjs_histori_pelayanan(){return akses.bpjs_histori_pelayanan;}
    public static boolean getrekap_mutasi_berkas(){return akses.rekap_mutasi_berkas;}
    public static boolean getskrining_ralan_pernapasan_pertahun(){return akses.skrining_ralan_pernapasan_pertahun;}
    public static boolean getpengajuan_barang_medis(){return akses.pengajuan_barang_medis;}
    public static boolean getpengajuan_barang_nonmedis(){return akses.pengajuan_barang_nonmedis;}
    public static boolean getgrafik_kunjungan_ranapbulan(){return akses.grafik_kunjungan_ranapbulan;}
    public static boolean getgrafik_kunjungan_ranaptanggal(){return akses.grafik_kunjungan_ranaptanggal;}
    public static boolean getgrafik_kunjungan_ranap_peruang(){return akses.grafik_kunjungan_ranap_peruang;}
    public static boolean getkunjungan_bangsal_pertahun(){return akses.kunjungan_bangsal_pertahun;}
    public static boolean getgrafik_jenjang_jabatanpegawai(){return akses.grafik_jenjang_jabatanpegawai;}
    public static boolean getgrafik_bidangpegawai(){return akses.grafik_bidangpegawai;}
    public static boolean getgrafik_departemenpegawai(){return akses.grafik_departemenpegawai;}
    public static boolean getgrafik_pendidikanpegawai(){return akses.grafik_pendidikanpegawai;}
    public static boolean getgrafik_sttswppegawai(){return akses.grafik_sttswppegawai;}
    public static boolean getgrafik_sttskerjapegawai(){return akses.grafik_sttskerjapegawai;}
    public static boolean getgrafik_sttspulangranap(){return akses.grafik_sttspulangranap;}
    public static boolean getkip_pasien_ranap(){return akses.kip_pasien_ranap;}
    public static boolean getkip_pasien_ralan(){return akses.kip_pasien_ralan;}
    public static boolean getbpjs_mapping_dokterdpjp(){return akses.bpjs_mapping_dokterdpjp;}
    public static boolean getdata_triase_igd(){return akses.data_triase_igd;}
    public static boolean getmaster_triase_skala1(){return akses.master_triase_skala1;}
    public static boolean getmaster_triase_skala2(){return akses.master_triase_skala2;}
    public static boolean getmaster_triase_skala3(){return akses.master_triase_skala3;}
    public static boolean getmaster_triase_skala4(){return akses.master_triase_skala4;}
    public static boolean getmaster_triase_skala5(){return akses.master_triase_skala5;}
    public static boolean getmaster_triase_pemeriksaan(){return akses.master_triase_pemeriksaan;}
    public static boolean getmaster_triase_macamkasus(){return akses.master_triase_macamkasus;}
    public static boolean getrekap_permintaan_diet(){return akses.rekap_permintaan_diet;}
    public static boolean getdaftar_pasien_ranap(){return akses.daftar_pasien_ranap;}
    public static boolean getdaftar_pasien_ranaptni(){return akses.daftar_pasien_ranaptni;}
    public static boolean getpengajuan_asetinventaris(){return akses.pengajuan_asetinventaris;}
    public static boolean getitem_apotek_jenis(){return akses.item_apotek_jenis;}
    public static boolean getitem_apotek_kategori(){return akses.item_apotek_kategori;}
    public static boolean getitem_apotek_golongan(){return akses.item_apotek_golongan;}
    public static boolean getitem_apotek_industrifarmasi(){return akses.item_apotek_industrifarmasi;}
    public static boolean getobat10_terbanyak_poli(){return akses.obat10_terbanyak_poli;}
    public static boolean getgrafik_pengajuan_aset_urgensi(){return akses.grafik_pengajuan_aset_urgensi;}
    public static boolean getgrafik_pengajuan_aset_status(){return akses.grafik_pengajuan_aset_status;}
    public static boolean getgrafik_pengajuan_aset_departemen(){return akses.grafik_pengajuan_aset_departemen;}
    public static boolean getrekap_pengajuan_aset_departemen(){return akses.rekap_pengajuan_aset_departemen;}
    public static boolean getgrafik_kelompok_jabatanpegawai(){return akses.grafik_kelompok_jabatanpegawai;}
    public static boolean getgrafik_resiko_kerjapegawai(){return akses.grafik_resiko_kerjapegawai;}
    public static boolean getgrafik_emergency_indexpegawai(){return akses.grafik_emergency_indexpegawai;}
    public static boolean getgrafik_inventaris_ruang(){return akses.grafik_inventaris_ruang;}
    public static boolean getharian_HAIs2(){return akses.harian_HAIs2;}
    public static boolean getgrafik_inventaris_jenis(){return akses.grafik_inventaris_jenis;}
    public static boolean getdata_resume_pasien(){return akses.data_resume_pasien;}
    public static boolean getperkiraan_biaya_ranap(){return akses.perkiraan_biaya_ranap;}
    public static boolean getrekap_obat_poli(){return akses.rekap_obat_poli;}
    public static boolean getrekap_obat_pasien(){return akses.rekap_obat_pasien;}
    public static boolean getgrafik_HAIs_pasienbangsal(){return akses.grafik_HAIs_pasienbangsal;}
    public static boolean getgrafik_HAIs_pasienbulan(){return akses.grafik_HAIs_pasienbulan;}
    public static boolean getpermintaan_perbaikan_inventaris(){return akses.permintaan_perbaikan_inventaris;}
    public static boolean getgrafik_HAIs_laju_vap(){return akses.grafik_HAIs_laju_vap;}
    public static boolean getgrafik_HAIs_laju_iad(){return akses.grafik_HAIs_laju_iad;}
    public static boolean getgrafik_HAIs_laju_pleb(){return akses.grafik_HAIs_laju_pleb;}
    public static boolean getgrafik_HAIs_laju_isk(){return akses.grafik_HAIs_laju_isk;}
    public static boolean getgrafik_HAIs_laju_ilo(){return akses.grafik_HAIs_laju_ilo;}
    public static boolean getgrafik_HAIs_laju_hap(){return akses.grafik_HAIs_laju_hap;}
    public static boolean getinhealth_mapping_poli(){return akses.inhealth_mapping_poli;}
    public static boolean getinhealth_mapping_dokter(){return akses.inhealth_mapping_dokter;}
    public static boolean getinhealth_mapping_tindakan_ralan(){return akses.inhealth_mapping_tindakan_ralan;}
    public static boolean getinhealth_mapping_tindakan_ranap(){return akses.inhealth_mapping_tindakan_ranap;}
    public static boolean getinhealth_mapping_tindakan_radiologi(){return akses.inhealth_mapping_tindakan_radiologi;}
    public static boolean getinhealth_mapping_tindakan_laborat(){return akses.inhealth_mapping_tindakan_laborat;}
    public static boolean getinhealth_mapping_tindakan_operasi(){return akses.inhealth_mapping_tindakan_operasi;}
    public static boolean gethibah_obat_bhp(){return akses.hibah_obat_bhp;}
    public static boolean getasal_hibah(){return akses.asal_hibah;}
    public static boolean getasuhan_gizi(){return akses.asuhan_gizi;}
    public static boolean getinhealth_kirim_tagihan(){return akses.inhealth_kirim_tagihan;}
    public static boolean getsirkulasi_obat4(){return akses.sirkulasi_obat4;}
    public static boolean getsirkulasi_obat5(){return akses.sirkulasi_obat5;}
    public static boolean getsirkulasi_non_medis2(){return akses.sirkulasi_non_medis2;}
    public static boolean getmonitoring_asuhan_gizi(){return akses.monitoring_asuhan_gizi;}
    public static boolean getpenerimaan_obat_perbulan(){return akses.penerimaan_obat_perbulan;}
    public static boolean getrekap_kunjungan(){return akses.rekap_kunjungan;}
    public static boolean getsurat_sakit(){return akses.surat_sakit;}
    public static boolean getpenilaian_awal_keperawatan_ralan(){return akses.penilaian_awal_keperawatan_ralan;}
    public static boolean getpermintaan_diet(){return akses.permintaan_diet;}
    public static boolean getmaster_masalah_keperawatan(){return akses.master_masalah_keperawatan;}
    public static boolean getpengajuan_cuti(){return akses.pengajuan_cuti;}
    public static boolean getkedatangan_pasien(){return akses.kedatangan_pasien;}
    public static boolean getutd_pendonor(){return akses.utd_pendonor;}
    public static boolean gettoko_suplier(){return akses.toko_suplier;}
    public static boolean gettoko_jenis(){return akses.toko_jenis;}
    public static boolean gettoko_set_harga(){return akses.toko_set_harga;}
    public static boolean gettoko_barang(){return akses.toko_barang;}
    public static boolean getpenagihan_piutang_pasien(){return akses.penagihan_piutang_pasien;}
    public static boolean getakun_penagihan_piutang(){return akses.akun_penagihan_piutang;}
    public static boolean getstok_opname_toko(){return akses.stok_opname_toko;}
    public static boolean gettoko_riwayat_barang(){return akses.toko_riwayat_barang;}
    public static boolean gettoko_surat_pemesanan(){return akses.toko_surat_pemesanan;}
    public static boolean gettoko_pengajuan_barang(){return akses.toko_pengajuan_barang;}
    public static boolean gettoko_penerimaan_barang(){return akses.toko_penerimaan_barang;}
    public static boolean gettoko_pengadaan_barang(){return akses.toko_pengadaan_barang;}
    public static boolean gettoko_hutang(){return akses.toko_hutang;}
    public static boolean gettoko_bayar_pemesanan(){return akses.toko_bayar_pemesanan;}
    public static boolean gettoko_member(){return akses.toko_member;}
    public static boolean gettoko_penjualan(){return akses.toko_penjualan;}
    public static boolean getregistrasi_poli_per_tanggal(){return akses.registrasi_poli_per_tanggal;}
    public static boolean gettoko_piutang(){return akses.toko_piutang;}
    public static boolean gettoko_retur_beli(){return akses.toko_retur_beli;}
    public static boolean getipsrs_returbeli(){return akses.ipsrs_returbeli;}
    public static boolean getipsrs_riwayat_barang(){return akses.ipsrs_riwayat_barang;}
    public static boolean getpasien_corona(){return akses.pasien_corona;}
    public static boolean gettoko_pendapatan_harian(){return akses.toko_pendapatan_harian;}
    public static boolean getdiagnosa_pasien_corona(){return akses.diagnosa_pasien_corona;}
    public static boolean getperawatan_pasien_corona(){return akses.perawatan_pasien_corona;}
    public static boolean getpenilaian_awal_keperawatan_gigi(){return akses.penilaian_awal_keperawatan_gigi;}
    public static boolean getmaster_masalah_keperawatan_gigi(){return akses.master_masalah_keperawatan_gigi;}
    public static boolean gettoko_bayar_piutang(){return akses.toko_bayar_piutang;}
    public static boolean gettoko_piutang_harian(){return akses.toko_piutang_harian;}
    public static boolean gettoko_penjualan_harian(){return akses.toko_penjualan_harian;}
    public static boolean getdeteksi_corona(){return akses.deteksi_corona;}
    public static boolean getpenilaian_awal_keperawatan_kebidanan(){return akses.penilaian_awal_keperawatan_kebidanan;}
    public static boolean getpengumuman_epasien(){return akses.pengumuman_epasien;}
    public static boolean getsurat_hamil(){return akses.surat_hamil;}
    public static boolean getset_tarif_online(){return akses.set_tarif_online;}
    public static boolean getbooking_periksa(){return akses.booking_periksa;}
    public static boolean gettoko_sirkulasi(){return akses.toko_sirkulasi;}
    public static boolean gettoko_retur_jual(){return akses.toko_retur_jual;}
    public static boolean gettoko_retur_piutang(){return akses.toko_retur_piutang;}
    public static boolean gettoko_sirkulasi2(){return akses.toko_sirkulasi2;}
    public static boolean gettoko_keuntungan_barang(){return akses.toko_keuntungan_barang;}
    public static boolean getzis_pengeluaran_penerima_dankes(){return akses.zis_pengeluaran_penerima_dankes;}
    public static boolean getzis_penghasilan_penerima_dankes(){return akses.zis_penghasilan_penerima_dankes;}
    public static boolean getzis_ukuran_rumah_penerima_dankes(){return akses.zis_ukuran_rumah_penerima_dankes;}
    public static boolean getzis_dinding_rumah_penerima_dankes(){return akses.zis_dinding_rumah_penerima_dankes;}
    public static boolean getzis_lantai_rumah_penerima_dankes(){return akses.zis_lantai_rumah_penerima_dankes;}
    public static boolean getzis_atap_rumah_penerima_dankes(){return akses.zis_atap_rumah_penerima_dankes;}
    public static boolean getzis_kepemilikan_rumah_penerima_dankes(){return akses.zis_kepemilikan_rumah_penerima_dankes;}
    public static boolean getzis_kamar_mandi_penerima_dankes(){return akses.zis_kamar_mandi_penerima_dankes;}
    public static boolean getzis_dapur_rumah_penerima_dankes(){return akses.zis_dapur_rumah_penerima_dankes;}
    public static boolean getzis_kursi_rumah_penerima_dankes(){return akses.zis_kursi_rumah_penerima_dankes;}
    public static boolean getzis_kategori_phbs_penerima_dankes(){return akses.zis_kategori_phbs_penerima_dankes;}
    public static boolean getzis_elektronik_penerima_dankes(){return akses.zis_elektronik_penerima_dankes;}
    public static boolean getzis_ternak_penerima_dankes(){return akses.zis_ternak_penerima_dankes;}
    public static boolean getzis_jenis_simpanan_penerima_dankes(){return akses.zis_jenis_simpanan_penerima_dankes;}
    public static boolean getpenilaian_awal_keperawatan_anak(){return akses.penilaian_awal_keperawatan_anak;}
    public static boolean getzis_kategori_asnaf_penerima_dankes(){return akses.zis_kategori_asnaf_penerima_dankes;}
    public static boolean getmaster_masalah_keperawatan_anak(){return akses.master_masalah_keperawatan_anak;}
    public static boolean getmaster_imunisasi(){return akses.master_imunisasi;}
    public static boolean getzis_patologis_penerima_dankes(){return akses.zis_patologis_penerima_dankes;}
    public static boolean getpcare_cek_kartu(){return akses.pcare_cek_kartu;}
    public static boolean getsurat_bebas_narkoba(){return akses.surat_bebas_narkoba;}
    public static boolean getsurat_keterangan_covid(){return akses.surat_keterangan_covid;}
    public static boolean getpemakaian_air_tanah(){return akses.pemakaian_air_tanah;}
    public static boolean getgrafik_air_tanah_pertanggal(){return akses.grafik_air_tanah_pertanggal;}
    public static boolean getgrafik_air_tanah_perbulan(){return akses.grafik_air_tanah_perbulan;}
    public static boolean getlama_pelayanan_poli(){return akses.lama_pelayanan_poli;}
    public static boolean gethemodialisa(){return akses.hemodialisa;}
    public static boolean getgrafik_harian_hemodialisa(){return akses.grafik_harian_hemodialisa;}
    public static boolean getgrafik_bulanan_hemodialisa(){return akses.grafik_bulanan_hemodialisa;}
    public static boolean getgrafik_tahunan_hemodialisa(){return akses.grafik_tahunan_hemodialisa;}
    public static boolean getgrafik_bulanan_meninggal(){return akses.grafik_bulanan_meninggal;}
    public static boolean getlaporan_tahunan_irj(){return akses.laporan_tahunan_irj;}
    public static boolean getperbaikan_inventaris(){return akses.perbaikan_inventaris;}
    public static boolean getsurat_cuti_hamil(){return akses.surat_cuti_hamil;}
    public static boolean getpermintaan_stok_obat_pasien(){return akses.permintaan_stok_obat_pasien;}
    public static boolean getpemeliharaan_inventaris(){return akses.pemeliharaan_inventaris;}
    public static boolean getklasifikasi_pasien_ranap(){return akses.klasifikasi_pasien_ranap;}
    public static boolean getbulanan_klasifikasi_pasien_ranap(){return akses.bulanan_klasifikasi_pasien_ranap;}
    public static boolean getharian_klasifikasi_pasien_ranap(){return akses.harian_klasifikasi_pasien_ranap;}
    public static boolean getklasifikasi_pasien_perbangsal(){return akses.klasifikasi_pasien_perbangsal;}
    public static boolean getsoap_perawatan(){return akses.soap_perawatan;}
    public static boolean getklaim_rawat_jalan(){return akses.klaim_rawat_jalan;}
    public static boolean getskrining_gizi(){return akses.skrining_gizi;}
    public static boolean getlama_penyiapan_rm(){return akses.lama_penyiapan_rm;}
    public static boolean getdosis_radiologi(){return akses.dosis_radiologi;}
    public static boolean getdemografi_umur_kunjungan(){return akses.demografi_umur_kunjungan;}
    public static boolean getjam_diet_pasien(){return akses.jam_diet_pasien;}
    public static boolean getrvu_bpjs(){return akses.rvu_bpjs;}
    public static boolean getverifikasi_penerimaan_farmasi(){return akses.verifikasi_penerimaan_farmasi;}
    public static boolean getverifikasi_penerimaan_logistik(){return akses.verifikasi_penerimaan_logistik;}
    public static boolean getpemeriksaan_lab_pa(){return akses.pemeriksaan_lab_pa;}
    public static boolean getringkasan_pengajuan_obat(){return akses.ringkasan_pengajuan_obat;}
    public static boolean getringkasan_pemesanan_obat(){return akses.ringkasan_pemesanan_obat;}
    public static boolean getringkasan_pengadaan_obat(){return akses.ringkasan_pengadaan_obat;}
    public static boolean getringkasan_penerimaan_obat(){return akses.ringkasan_penerimaan_obat;}
    public static boolean getringkasan_hibah_obat(){return akses.ringkasan_hibah_obat;}
    public static boolean getringkasan_penjualan_obat(){return akses.ringkasan_penjualan_obat;}
    public static boolean getringkasan_beri_obat(){return akses.ringkasan_beri_obat;}
    public static boolean getringkasan_piutang_obat(){return akses.ringkasan_piutang_obat;}
    public static boolean getringkasan_stok_keluar_obat(){return akses.ringkasan_stok_keluar_obat;}
    public static boolean getringkasan_retur_suplier_obat(){return akses.ringkasan_retur_suplier_obat;}
    public static boolean getringkasan_retur_pembeli_obat(){return akses.ringkasan_retur_pembeli_obat;}
    public static boolean getpenilaian_awal_keperawatan_ranapkebidanan(){return akses.penilaian_awal_keperawatan_ranapkebidanan;}
    public static boolean getringkasan_pengajuan_nonmedis(){return akses.ringkasan_pengajuan_nonmedis;}
    public static boolean getringkasan_pemesanan_nonmedis(){return akses.ringkasan_pemesanan_nonmedis;}
    public static boolean getringkasan_pengadaan_nonmedis(){return akses.ringkasan_pengadaan_nonmedis;}
    public static boolean getringkasan_penerimaan_nonmedis(){return akses.ringkasan_penerimaan_nonmedis;}
    public static boolean getringkasan_stokkeluar_nonmedis(){return akses.ringkasan_stokkeluar_nonmedis;}
    public static boolean getringkasan_returbeli_nonmedis(){return akses.ringkasan_returbeli_nonmedis;}
    public static boolean getomset_penerimaan(){return akses.omset_penerimaan;}
    public static boolean getvalidasi_penagihan_piutang(){return akses.validasi_penagihan_piutang;}
    public static boolean getpermintaan_ranap(){return akses.permintaan_ranap;}
    public static boolean getbpjs_diagnosa_prb(){return akses.bpjs_diagnosa_prb;}
    public static boolean getbpjs_obat_prb(){return akses.bpjs_obat_prb;}
    public static boolean getbpjs_surat_kontrol(){return akses.bpjs_surat_kontrol;}
    public static boolean getpenggunaan_bhp_ok(){return akses.penggunaan_bhp_ok;}
    public static boolean getsurat_keterangan_rawat_inap(){return akses.surat_keterangan_rawat_inap;}
    public static boolean getsurat_keterangan_sehat(){return akses.surat_keterangan_sehat;}
    public static boolean getpendapatan_per_carabayar(){return akses.pendapatan_per_carabayar;}
    public static boolean getakun_host_to_host_bank_jateng(){return akses.akun_host_to_host_bank_jateng;}
    public static boolean getpembayaran_bank_jateng(){return akses.pembayaran_bank_jateng;}
    public static boolean getbpjs_surat_pri(){return akses.bpjs_surat_pri;}
    public static boolean getringkasan_tindakan(){return akses.ringkasan_tindakan;}
    public static boolean getlama_pelayanan_pasien(){return akses.lama_pelayanan_pasien;}
    public static boolean getsurat_sakit_pihak_2(){return akses.surat_sakit_pihak_2;}
    public static boolean gettagihan_hutang_obat(){return akses.tagihan_hutang_obat;}
    public static boolean getreferensi_mobilejkn_bpjs(){return akses.referensi_mobilejkn_bpjs;}
    public static boolean getbatal_pendaftaran_mobilejkn_bpjs(){return akses.batal_pendaftaran_mobilejkn_bpjs;}
    public static boolean getlama_operasi(){return akses.lama_operasi;}
    public static boolean getgrafik_inventaris_kategori(){return akses.grafik_inventaris_kategori;}
    public static boolean getgrafik_inventaris_merk(){return akses.grafik_inventaris_merk;}
    public static boolean getgrafik_inventaris_produsen(){return akses.grafik_inventaris_produsen;}
    public static boolean getpengembalian_deposit_pasien(){return akses.pengembalian_deposit_pasien;}
    public static boolean getvalidasi_tagihan_hutang_obat(){return akses.validasi_tagihan_hutang_obat;}
    public static boolean getpiutang_obat_belum_lunas(){return akses.piutang_obat_belum_lunas;}
    public static boolean getintegrasi_briapi(){return akses.integrasi_briapi;}
    public static boolean getpengadaan_aset_inventaris(){return akses.pengadaan_aset_inventaris;}
    public static boolean getakun_aset_inventaris(){return akses.akun_aset_inventaris;}
    public static boolean getsuplier_inventaris(){return akses.suplier_inventaris;}
    public static boolean getpenerimaan_aset_inventaris(){return akses.penerimaan_aset_inventaris;}
    public static boolean getbayar_pemesanan_iventaris(){return akses.bayar_pemesanan_iventaris;}
    public static boolean gethutang_aset_inventaris(){return akses.hutang_aset_inventaris;}
    public static boolean gethibah_aset_inventaris(){return akses.hibah_aset_inventaris;}
    public static boolean gettitip_faktur_non_medis(){return akses.titip_faktur_non_medis;}
    public static boolean getvalidasi_tagihan_non_medis(){return akses.validasi_tagihan_non_medis;}
    public static boolean gettitip_faktur_aset(){return akses.titip_faktur_aset;}
    public static boolean getvalidasi_tagihan_aset(){return akses.validasi_tagihan_aset;}
    public static boolean gethibah_non_medis(){return akses.hibah_non_medis;}
    public static boolean getpcare_alasan_tacc(){return akses.pcare_alasan_tacc;}
    public static boolean getresep_luar(){return akses.resep_luar;}
    public static boolean getsurat_bebas_tbc(){return akses.surat_bebas_tbc;}
    public static boolean getsurat_buta_warna(){return akses.surat_buta_warna;}
    public static boolean getsurat_bebas_tato(){return akses.surat_bebas_tato;}
    public static boolean getsurat_kewaspadaan_kesehatan(){return akses.surat_kewaspadaan_kesehatan;}
    public static boolean getgrafik_porsidiet_pertanggal(){return akses.grafik_porsidiet_pertanggal;}
    public static boolean getgrafik_porsidiet_perbulan(){return akses.grafik_porsidiet_perbulan;}
    public static boolean getgrafik_porsidiet_pertahun(){return akses.grafik_porsidiet_pertahun;}
    public static boolean getgrafik_porsidiet_perbangsal(){return akses.grafik_porsidiet_perbangsal;}
    public static boolean getpenilaian_awal_medis_ralan(){return akses.penilaian_awal_medis_ralan;}
    public static boolean getmaster_masalah_keperawatan_mata(){return akses.master_masalah_keperawatan_mata;}
    public static boolean getpenilaian_awal_keperawatan_mata(){return akses.penilaian_awal_keperawatan_mata;}
    public static boolean getpenilaian_awal_medis_ranap(){return akses.penilaian_awal_medis_ranap;}
    public static boolean getpenilaian_awal_medis_ranap_kebidanan(){return akses.penilaian_awal_medis_ranap_kebidanan;}
    public static boolean getpenilaian_awal_medis_ralan_kebidanan(){return akses.penilaian_awal_medis_ralan_kebidanan;}
    public static boolean getpenilaian_awal_medis_igd(){return akses.penilaian_awal_medis_igd;}
    public static boolean getpenilaian_awal_medis_ralan_anak(){return akses.penilaian_awal_medis_ralan_anak;}
    public static boolean getbpjs_referensi_poli_hfis(){return akses.bpjs_referensi_poli_hfis;}
    public static boolean getbpjs_referensi_dokter_hfis(){return akses.bpjs_referensi_dokter_hfis;}
    public static boolean getbpjs_referensi_jadwal_hfis(){return akses.bpjs_referensi_jadwal_hfis;}
    public static boolean getpenilaian_fisioterapi(){return akses.penilaian_fisioterapi;}
    public static boolean getbpjs_program_prb(){return akses.bpjs_program_prb;}
    public static boolean getbpjs_suplesi_jasaraharja(){return akses.bpjs_suplesi_jasaraharja;}
    public static boolean getbpjs_data_induk_kecelakaan(){return akses.bpjs_data_induk_kecelakaan;}
    public static boolean getbpjs_sep_internal(){return akses.bpjs_sep_internal;}
    public static boolean getbpjs_klaim_jasa_raharja(){return akses.bpjs_klaim_jasa_raharja;}
    public static boolean getbpjs_daftar_finger_print(){return akses.bpjs_daftar_finger_print;}
    public static boolean getbpjs_rujukan_khusus(){return akses.bpjs_rujukan_khusus;}
    public static boolean getpemeliharaan_gedung(){return akses.pemeliharaan_gedung;}
    public static boolean getgrafik_perbaikan_inventaris_pertanggal(){return akses.grafik_perbaikan_inventaris_pertanggal;}
    public static boolean getgrafik_perbaikan_inventaris_perbulan(){return akses.grafik_perbaikan_inventaris_perbulan;}
    public static boolean getgrafik_perbaikan_inventaris_pertahun(){return akses.grafik_perbaikan_inventaris_pertahun;}
    public static boolean getgrafik_perbaikan_inventaris_perpelaksana_status(){return akses.grafik_perbaikan_inventaris_perpelaksana_status;}
    public static boolean getpenilaian_mcu(){return akses.penilaian_mcu;}
    public static boolean getpeminjam_piutang(){return akses.peminjam_piutang;}
    public static boolean getpiutang_lainlain(){return akses.piutang_lainlain;}
    public static boolean getcara_bayar(){return akses.cara_bayar;}
    public static boolean getaudit_kepatuhan_apd(){return akses.audit_kepatuhan_apd;}
    public static boolean getbpjs_task_id(){return akses.bpjs_task_id;}
    public static boolean getbayar_piutang_lain(){return akses.bayar_piutang_lain;}
    public static boolean getpembayaran_akun_bayar4(){return akses.pembayaran_akun_bayar4;}
    public static boolean getstok_akhir_farmasi_pertanggal(){return akses.stok_akhir_farmasi_pertanggal;}
    public static boolean getriwayat_kamar_pasien(){return akses.riwayat_kamar_pasien;}
    public static boolean getuji_fungsi_kfr(){return akses.uji_fungsi_kfr;}
    public static boolean gethapus_berkas_digital_perawatan(){return akses.hapus_berkas_digital_perawatan;}
    public static boolean getkategori_pengeluaran_harian(){return akses.kategori_pengeluaran_harian;}
    public static boolean getkategori_pemasukan_lain(){return akses.kategori_pemasukan_lain;}
    public static boolean getpembayaran_akun_bayar5(){return akses.pembayaran_akun_bayar5;}
    public static boolean getruang_ok(){return akses.ruang_ok;}
    public static boolean getjasa_tindakan_pasien(){return akses.jasa_tindakan_pasien;}
    public static boolean gettelaah_resep(){return akses.telaah_resep;}
    public static boolean getpermintaan_resep_pulang(){return akses.permintaan_resep_pulang;}
    public static boolean getrekap_jm_dokter(){return akses.rekap_jm_dokter;}
    public static boolean getstatus_data_rm(){return akses.status_data_rm;}
    public static boolean getubah_petugas_lab_pk(){return akses.ubah_petugas_lab_pk;}
    public static boolean getubah_petugas_lab_pa(){return akses.ubah_petugas_lab_pa;}
    public static boolean getubah_petugas_radiologi(){return akses.ubah_petugas_radiologi;}
    public static boolean getgabung_norawat(){return akses.gabung_norawat;}
    public static boolean getgabung_rm(){return akses.gabung_rm;}
    public static boolean getringkasan_biaya_obat_pasien_pertanggal(){return akses.ringkasan_biaya_obat_pasien_pertanggal;}
    public static boolean getmaster_masalah_keperawatan_igd(){return akses.master_masalah_keperawatan_igd;}
    public static boolean getpenilaian_awal_keperawatan_igd(){return akses.penilaian_awal_keperawatan_igd;}
    public static boolean getbpjs_referensi_dpho_apotek(){return akses.bpjs_referensi_dpho_apotek;}
    public static boolean getbpjs_referensi_poli_apotek(){return akses.bpjs_referensi_poli_apotek;}
    public static boolean getbayar_jm_dokter(){return akses.bayar_jm_dokter;}
    public static boolean getbpjs_referensi_faskes_apotek(){return akses.bpjs_referensi_faskes_apotek;}
    public static boolean getbpjs_referensi_spesialistik_apotek(){return akses.bpjs_referensi_spesialistik_apotek;}
    public static boolean getpembayaran_briva(){return akses.pembayaran_briva;}
    public static boolean getpenilaian_awal_keperawatan_ranap(){return akses.penilaian_awal_keperawatan_ranap;}
    public static boolean getnilai_penerimaan_vendor_farmasi_perbulan(){return akses.nilai_penerimaan_vendor_farmasi_perbulan;}
    public static boolean getakun_bayar_hutang(){return akses.akun_bayar_hutang;}
    public static boolean getmaster_rencana_keperawatan(){return akses.master_rencana_keperawatan;}
    public static boolean getlaporan_tahunan_igd(){return akses.laporan_tahunan_igd;}
    public static boolean getobat_bhp_tidakbergerak(){return akses.obat_bhp_tidakbergerak;}
    public static boolean getringkasan_hutang_vendor_farmasi(){return akses.ringkasan_hutang_vendor_farmasi;}
    public static boolean getnilai_penerimaan_vendor_nonmedis_perbulan(){return akses.nilai_penerimaan_vendor_nonmedis_perbulan;}
    public static boolean getringkasan_hutang_vendor_nonmedis(){return akses.ringkasan_hutang_vendor_nonmedis;}
    public static boolean getanggota_polri_dirawat(){return akses.anggota_polri_dirawat;}
    public static boolean getdaftar_pasien_ranap_polri(){return akses.daftar_pasien_ranap_polri;}
    public static boolean getsoap_ralan_polri(){return akses.soap_ralan_polri;}
    public static boolean getsoap_ranap_polri(){return akses.soap_ranap_polri;}
    public static boolean getlaporan_penyakit_polri(){return akses.laporan_penyakit_polri;}
    public static boolean getmaster_rencana_keperawatan_anak(){return akses.master_rencana_keperawatan_anak;}
    public static boolean getjumlah_pengunjung_ralan_polri(){return akses.jumlah_pengunjung_ralan_polri;}
    public static boolean getcatatan_observasi_igd(){return akses.catatan_observasi_igd;}
    public static boolean getcatatan_observasi_ranap(){return akses.catatan_observasi_ranap;}
    public static boolean getcatatan_observasi_ranap_kebidanan(){return akses.catatan_observasi_ranap_kebidanan;}
    public static boolean getcatatan_observasi_ranap_postpartum(){return akses.catatan_observasi_ranap_postpartum;}
    public static boolean getpenilaian_awal_medis_ralan_tht(){return akses.penilaian_awal_medis_ralan_tht;}
    public static boolean getpenilaian_psikologi(){return akses.penilaian_psikologi;}
    public static boolean getaudit_cuci_tangan_medis(){return akses.audit_cuci_tangan_medis;}
    public static boolean getaudit_pembuangan_limbah(){return akses.audit_pembuangan_limbah;}
    public static boolean getruang_audit_kepatuhan(){return akses.ruang_audit_kepatuhan;}
    public static boolean getaudit_pembuangan_benda_tajam(){return akses.audit_pembuangan_benda_tajam;}
    public static boolean getaudit_penanganan_darah(){return akses.audit_penanganan_darah;}
    public static boolean getaudit_pengelolaan_linen_kotor(){return akses.audit_pengelolaan_linen_kotor;}
    public static boolean getaudit_penempatan_pasien(){return akses.audit_penempatan_pasien;}
    public static boolean getaudit_kamar_jenazah(){return akses.audit_kamar_jenazah;}
    public static boolean getaudit_bundle_iadp(){return akses.audit_bundle_iadp;}
    public static boolean getaudit_bundle_ido(){return akses.audit_bundle_ido;}
    public static boolean getaudit_fasilitas_kebersihan_tangan(){return akses.audit_fasilitas_kebersihan_tangan;}
    public static boolean getaudit_fasilitas_apd(){return akses.audit_fasilitas_apd;}
    public static boolean getaudit_pembuangan_limbah_cair_infeksius(){return akses.audit_pembuangan_limbah_cair_infeksius;}
    public static boolean getaudit_sterilisasi_alat(){return akses.audit_sterilisasi_alat;}
    public static boolean getpenilaian_awal_medis_ralan_psikiatri(){return akses.penilaian_awal_medis_ralan_psikiatri;}
    public static boolean getpersetujuan_penolakan_tindakan(){return akses.persetujuan_penolakan_tindakan;}
    public static boolean getaudit_bundle_isk(){return akses.audit_bundle_isk;}
    public static boolean getaudit_bundle_plabsi(){return akses.audit_bundle_plabsi;}
    public static boolean getaudit_bundle_vap(){return akses.audit_bundle_vap;}
    public static boolean getakun_host_to_host_bank_papua(){return akses.akun_host_to_host_bank_papua;}
    public static boolean getpembayaran_bank_papua(){return akses.pembayaran_bank_papua;}
    public static boolean getpenilaian_awal_medis_ralan_penyakit_dalam(){return akses.penilaian_awal_medis_ralan_penyakit_dalam;}
    public static boolean getpenilaian_awal_medis_ralan_mata(){return akses.penilaian_awal_medis_ralan_mata;}
    public static boolean getpenilaian_awal_medis_ralan_neurologi(){return akses.penilaian_awal_medis_ralan_neurologi;}
    public static boolean getsirkulasi_obat6(){return akses.sirkulasi_obat6;}
    public static boolean getpenilaian_awal_medis_ralan_orthopedi(){return akses.penilaian_awal_medis_ralan_orthopedi;}
    public static boolean getpenilaian_awal_medis_ralan_bedah(){return akses.penilaian_awal_medis_ralan_bedah;}
    public static boolean getintegrasi_khanza_health_services(){return akses.integrasi_khanza_health_services;}
    public static boolean getsoap_ralan_tni(){return akses.soap_ralan_tni;}
    public static boolean getsoap_ranap_tni(){return akses.soap_ranap_tni;}
    public static boolean getjumlah_pengunjung_ralan_tni(){return akses.jumlah_pengunjung_ralan_tni;}
    public static boolean getlaporan_penyakit_tni(){return akses.laporan_penyakit_tni;}
    public static boolean getcatatan_keperawatan_ranap(){return akses.catatan_keperawatan_ranap;}
    public static boolean getmaster_rencana_keperawatan_gigi(){return akses.master_rencana_keperawatan_gigi;}
    public static boolean getmaster_rencana_keperawatan_mata(){return akses.master_rencana_keperawatan_mata;}
    public static boolean getmaster_rencana_keperawatan_igd(){return akses.master_rencana_keperawatan_igd;}
    public static boolean getmaster_masalah_keperawatan_psikiatri(){return akses.master_masalah_keperawatan_psikiatri;}
    public static boolean getmaster_rencana_keperawatan_psikiatri(){return akses.master_rencana_keperawatan_psikiatri;}
    public static boolean getpenilaian_awal_keperawatan_psikiatri(){return akses.penilaian_awal_keperawatan_psikiatri;}
    public static boolean getpemantauan_pews_anak(){return akses.pemantauan_pews_anak;}
    public static boolean getsurat_pulang_atas_permintaan_sendiri(){return akses.surat_pulang_atas_permintaan_sendiri;}
    public static boolean gettemplate_hasil_radiologi(){return akses.template_hasil_radiologi;}
    public static boolean getlaporan_bulanan_irj(){return akses.laporan_bulanan_irj;}
    public static boolean gettemplate_pemeriksaan(){return akses.template_pemeriksaan;}
    public static boolean getpemeriksaan_lab_mb(){return akses.pemeriksaan_lab_mb;}
    public static boolean getubah_petugas_lab_mb(){return akses.ubah_petugas_lab_mb;}
    public static boolean getpenilaian_pre_operasi(){return akses.penilaian_pre_operasi;}
    public static boolean getpenilaian_pre_anestesi(){return akses.penilaian_pre_anestesi;}
    public static boolean getperencanaan_pemulangan(){return akses.perencanaan_pemulangan;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_dewasa(){return akses.penilaian_lanjutan_resiko_jatuh_dewasa;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_anak(){return akses.penilaian_lanjutan_resiko_jatuh_anak;}
    public static boolean getpenilaian_awal_medis_ralan_geriatri(){return akses.penilaian_awal_medis_ralan_geriatri;}
    public static boolean getpenilaian_tambahan_pasien_geriatri(){return akses.penilaian_tambahan_pasien_geriatri;}
    public static boolean getskrining_nutrisi_dewasa(){return akses.skrining_nutrisi_dewasa;}
    public static boolean getskrining_nutrisi_lansia(){return akses.skrining_nutrisi_lansia;}
    public static boolean gethasil_pemeriksaan_usg(){return akses.hasil_pemeriksaan_usg;}
    public static boolean getskrining_nutrisi_anak(){return akses.skrining_nutrisi_anak;}
    public static boolean getakun_host_to_host_bank_jabar(){return akses.akun_host_to_host_bank_jabar;}
    public static boolean getpembayaran_bank_jabar(){return akses.pembayaran_bank_jabar;}
    public static boolean getsurat_pernyataan_pasien_umum(){return akses.surat_pernyataan_pasien_umum;}
    public static boolean getkonseling_farmasi(){return akses.konseling_farmasi;}
    public static boolean getpelayanan_informasi_obat(){return akses.pelayanan_informasi_obat;}
    public static boolean getjawaban_pio_apoteker(){return akses.jawaban_pio_apoteker;}
    public static boolean getsurat_persetujuan_umum(){return akses.surat_persetujuan_umum;}
    public static boolean gettransfer_pasien_antar_ruang(){return akses.transfer_pasien_antar_ruang;}
    public static boolean getsatu_sehat_referensi_dokter(){return akses.satu_sehat_referensi_dokter;}
    public static boolean getsatu_sehat_referensi_pasien(){return akses.satu_sehat_referensi_pasien;}
    public static boolean getsatu_sehat_mapping_departemen(){return akses.satu_sehat_mapping_departemen;}
    public static boolean getsatu_sehat_mapping_lokasi(){return akses.satu_sehat_mapping_lokasi;}
    public static boolean getsatu_sehat_kirim_encounter(){return akses.satu_sehat_kirim_encounter;}
    public static boolean getcatatan_cek_gds(){return akses.catatan_cek_gds;}
    public static boolean getsatu_sehat_kirim_condition(){return akses.satu_sehat_kirim_condition;}
    public static boolean getchecklist_pre_operasi(){return akses.checklist_pre_operasi;}
    public static boolean getsatu_sehat_kirim_observationttv(){return akses.satu_sehat_kirim_observationttv;}
    public static boolean getsignin_sebelum_anestesi(){return akses.signin_sebelum_anestesi;}
    public static boolean getsatu_sehat_kirim_procedure(){return akses.satu_sehat_kirim_procedure;}
    public static boolean getoperasi_per_bulan(){return akses.operasi_per_bulan;}
    public static boolean gettimeout_sebelum_insisi(){return akses.timeout_sebelum_insisi;}
    public static boolean getsignout_sebelum_menutup_luka(){return akses.signout_sebelum_menutup_luka;}
    public static boolean getdapur_barang(){return akses.dapur_barang;}
    public static boolean getdapur_opname(){return akses.dapur_opname;}
    public static boolean getsatu_sehat_mapping_vaksin(){return akses.satu_sehat_mapping_vaksin;}
    public static boolean getdapur_suplier(){return akses.dapur_suplier;}
    public static boolean getsatu_sehat_kirim_Immunization(){return akses.satu_sehat_kirim_Immunization;}
    public static boolean getchecklist_post_operasi(){return akses.checklist_post_operasi;}
    public static boolean getdapur_pembelian(){return akses.dapur_pembelian;}
    public static boolean getdapur_stok_keluar(){return akses.dapur_stok_keluar;}
    public static boolean getdapur_riwayat_barang(){return akses.dapur_riwayat_barang;}
    public static boolean getpermintaan_dapur(){return akses.permintaan_dapur;}
    public static boolean getrekonsiliasi_obat(){return akses.rekonsiliasi_obat;}
    public static boolean getbiaya_pengadaan_dapur(){return akses.biaya_pengadaan_dapur;}
    public static boolean getrekap_pengadaan_dapur(){return akses.rekap_pengadaan_dapur;}
    public static boolean getkesling_limbah_b3medis_cair(){return akses.kesling_limbah_b3medis_cair;}
    public static boolean getgrafik_limbahb3cair_pertanggal(){return akses.grafik_limbahb3cair_pertanggal;}
    public static boolean getgrafik_limbahb3cair_perbulan(){return akses.grafik_limbahb3cair_perbulan;}
    public static boolean getrekap_biaya_registrasi(){return akses.rekap_biaya_registrasi;}
    public static boolean getkonfirmasi_rekonsiliasi_obat(){return akses.konfirmasi_rekonsiliasi_obat;}
    public static boolean getsatu_sehat_kirim_clinicalimpression(){return akses.satu_sehat_kirim_clinicalimpression;}
    public static boolean getpenilaian_pasien_terminal(){return akses.penilaian_pasien_terminal;}
    public static boolean getsurat_persetujuan_rawat_inap(){return akses.surat_persetujuan_rawat_inap;}
    public static boolean getmonitoring_reaksi_tranfusi(){return akses.monitoring_reaksi_tranfusi;}
    public static boolean getpenilaian_korban_kekerasan(){return akses.penilaian_korban_kekerasan;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_lansia(){return akses.penilaian_lanjutan_resiko_jatuh_lansia;}
    public static boolean getmpp_skrining(){return akses.mpp_skrining;}
    public static boolean getpenilaian_pasien_penyakit_menular(){return akses.penilaian_pasien_penyakit_menular;}
    public static boolean getedukasi_pasien_keluarga_rj(){return akses.edukasi_pasien_keluarga_rj;}
    public static boolean getpemantauan_pews_dewasa(){return akses.pemantauan_pews_dewasa;}
    public static boolean getpenilaian_tambahan_bunuh_diri(){return akses.penilaian_tambahan_bunuh_diri;}
    public static boolean getbpjs_antrean_pertanggal(){return akses.bpjs_antrean_pertanggal;}
    public static boolean getpenilaian_tambahan_perilaku_kekerasan(){return akses.penilaian_tambahan_perilaku_kekerasan;}
    public static boolean getpenilaian_tambahan_beresiko_melarikan_diri(){return akses.penilaian_tambahan_beresiko_melarikan_diri;}
    public static boolean getpersetujuan_penundaan_pelayanan(){return akses.persetujuan_penundaan_pelayanan;}
    public static boolean getsisa_diet_pasien(){return akses.sisa_diet_pasien;}
    public static boolean getpenilaian_awal_medis_ralan_bedah_mulut(){return akses.penilaian_awal_medis_ralan_bedah_mulut;}
    public static boolean getpenilaian_pasien_keracunan(){return akses.penilaian_pasien_keracunan;}
    public static boolean getpemantauan_meows_obstetri(){return akses.pemantauan_meows_obstetri;}
    public static boolean getcatatan_adime_gizi(){return akses.catatan_adime_gizi;}
    public static boolean getpengajuan_biaya(){return akses.pengajuan_biaya;}
    public static boolean getpenilaian_awal_keperawatan_ralan_geriatri(){return akses.penilaian_awal_keperawatan_ralan_geriatri;}
    public static boolean getmaster_masalah_keperawatan_geriatri(){return akses.master_masalah_keperawatan_geriatri;}
    public static boolean getmaster_rencana_keperawatan_geriatri(){return akses.master_rencana_keperawatan_geriatri;}
    public static boolean getchecklist_kriteria_masuk_hcu(){return akses.checklist_kriteria_masuk_hcu;}
    public static boolean getchecklist_kriteria_keluar_hcu(){return akses.checklist_kriteria_keluar_hcu;}
    public static boolean getpenilaian_risiko_dekubitus(){return akses.penilaian_risiko_dekubitus;}
    public static boolean getmaster_menolak_anjuran_medis(){return akses.master_menolak_anjuran_medis;}
    public static boolean getpenolakan_anjuran_medis(){return akses.penolakan_anjuran_medis;}
    public static boolean getlaporan_tahunan_penolakan_anjuran_medis(){return akses.laporan_tahunan_penolakan_anjuran_medis;}
    public static boolean gettemplate_laporan_operasi(){return akses.template_laporan_operasi;}
    public static boolean gethasil_tindakan_eswl(){return akses.hasil_tindakan_eswl;}
    public static boolean getchecklist_kriteria_masuk_icu(){return akses.checklist_kriteria_masuk_icu;}
    public static boolean getchecklist_kriteria_keluar_icu(){return akses.checklist_kriteria_keluar_icu;}
    public static boolean getakses_dokter_lain_rawat_jalan(){return akses.akses_dokter_lain_rawat_jalan;}
    public static boolean getfollow_up_dbd(){return akses.follow_up_dbd;}
    public static boolean getpenilaian_risiko_jatuh_neonatus(){return akses.penilaian_risiko_jatuh_neonatus;}
    public static boolean getpersetujuan_pengajuan_biaya(){return akses.persetujuan_pengajuan_biaya;}
    public static boolean getpemeriksaan_fisik_ralan_per_penyakit(){return akses.pemeriksaan_fisik_ralan_per_penyakit;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_geriatri(){return akses.penilaian_lanjutan_resiko_jatuh_geriatri;}
    public static boolean getpemantauan_ews_neonatus(){return akses.pemantauan_ews_neonatus;}
    public static boolean getvalidasi_persetujuan_pengajuan_biaya(){return akses.validasi_persetujuan_pengajuan_biaya;}
    public static boolean getriwayat_perawatan_icare_bpjs(){return akses.riwayat_perawatan_icare_bpjs;}
    public static boolean getrekap_pengajuan_biaya(){return akses.rekap_pengajuan_biaya;}
    public static boolean getpenilaian_awal_medis_ralan_kulit_kelamin(){return akses.penilaian_awal_medis_ralan_kulit_kelamin;}
    public static boolean getakun_host_to_host_bank_mandiri(){return akses.akun_host_to_host_bank_mandiri;}
    public static boolean getpenilaian_medis_hemodialisa(){return akses.penilaian_medis_hemodialisa;}
    public static boolean getpenilaian_level_kecemasan_ranap_anak(){return akses.penilaian_level_kecemasan_ranap_anak;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_psikiatri(){return akses.penilaian_lanjutan_resiko_jatuh_psikiatri;}
    public static boolean getpenilaian_lanjutan_skrining_fungsional(){return akses.penilaian_lanjutan_skrining_fungsional;}
    public static boolean getpenilaian_medis_ralan_rehab_medik(){return akses.penilaian_medis_ralan_rehab_medik;}
    public static boolean getlaporan_anastesi(){return akses.laporan_anestesi;}
    public static boolean gettemplate_persetujuan_penolakan_tindakan(){return akses.template_persetujuan_penolakan_tindakan;}
    public static boolean getpenilaian_medis_ralan_gawat_darurat_psikiatri(){return akses.penilaian_medis_ralan_gawat_darurat_psikiatri;}
    public static boolean getbpjs_referensi_setting_apotek(){return akses.bpjs_referensi_setting_apotek;}
    public static boolean getbpjs_referensi_obat_apotek(){return akses.bpjs_referensi_obat_apotek;}
    public static boolean getbpjs_mapping_obat_apotek(){return akses.bpjs_mapping_obat_apotek;}
    public static boolean getpembayaran_bank_mandiri(){return akses.pembayaran_bank_mandiri;}
    public static boolean getpenilaian_ulang_nyeri(){return akses.penilaian_ulang_nyeri;}
    public static boolean getpenilaian_terapi_wicara(){return akses.penilaian_terapi_wicara;}
    public static boolean getbpjs_obat_23hari_apotek(){return akses.bpjs_obat_23hari_apotek;}
    public static boolean getpengkajian_restrain(){return akses.pengkajian_restrain;}
    public static boolean getbpjs_kunjungan_sep_apotek(){return akses.bpjs_kunjungan_sep_apotek;}
    public static boolean getbpjs_monitoring_klaim_apotek(){return akses.bpjs_monitoring_klaim_apotek;}
    public static boolean getbpjs_daftar_pelayanan_obat_apotek(){return akses.bpjs_daftar_pelayanan_obat_apotek;}
    public static boolean getpenilaian_awal_medis_ralan_paru(){return akses.penilaian_awal_medis_ralan_paru;}
    public static boolean getcatatan_keperawatan_ralan(){return akses.catatan_keperawatan_ralan;}
    public static boolean getcatatan_persalinan(){return akses.catatan_persalinan;}
    public static boolean getskor_aldrette_pasca_anestesi(){return akses.skor_aldrette_pasca_anestesi;}
    public static boolean getskor_steward_pasca_anestesi(){return akses.skor_steward_pasca_anestesi;}
    public static boolean getskor_bromage_pasca_anestesi(){return akses.skor_bromage_pasca_anestesi;}
    public static boolean getpenilaian_pre_induksi(){return akses.penilaian_pre_induksi;}
    public static boolean gethasil_usg_urologi(){return akses.hasil_usg_urologi;}
    public static boolean gethasil_usg_gynecologi(){return akses.hasil_usg_gynecologi;}
    public static boolean gethasil_pemeriksaan_ekg(){return akses.hasil_pemeriksaan_ekg;}
    public static boolean gethapus_edit_sep_bpjs(){return akses.hapus_edit_sep_bpjs;}
    public static boolean getsatu_sehat_kirim_diet(){return akses.satu_sehat_kirim_diet;}
    public static boolean getsatu_sehat_mapping_obat(){return akses.satu_sehat_mapping_obat;}
    public static boolean getdapur_ringkasan_pembelian(){return akses.dapur_ringkasan_pembelian;}
    public static boolean getsatu_sehat_kirim_medication(){return akses.satu_sehat_kirim_medication;}
    public static boolean getsatu_sehat_kirim_medicationrequest(){return akses.satu_sehat_kirim_medicationrequest;}
    public static boolean getpenatalaksanaan_terapi_okupasi(){return akses.penatalaksanaan_terapi_okupasi;}
    public static boolean getsatu_sehat_kirim_medicationdispense(){return akses.satu_sehat_kirim_medicationdispense;}
    public static boolean getedit_hapus_spo_medis() { return akses.edit_hapus_spo_medis; }
    public static boolean getedit_hapus_spo_nonmedis() { return akses.edit_hapus_spo_nonmedis; }
    public static boolean gethasil_usg_neonatus(){return akses.hasil_usg_neonatus;}
    public static boolean gethasil_endoskopi_faring_laring(){return akses.hasil_endoskopi_faring_laring;}
    public static boolean getsatu_sehat_mapping_radiologi(){return akses.satu_sehat_mapping_radiologi;}
    public static boolean getsatu_sehat_kirim_servicerequest_radiologi(){return akses.satu_sehat_kirim_servicerequest_radiologi;}
    public static boolean gethasil_endoskopi_hidung(){return akses.hasil_endoskopi_hidung;}
    public static boolean getsatu_sehat_kirim_specimen_radiologi(){return akses.satu_sehat_kirim_specimen_radiologi;}
    public static boolean getbpjs_kompilasi_berkas_klaim(){return akses.bpjs_kompilasi_berkas_klaim;}
    public static boolean getmaster_masalah_keperawatan_neonatus(){return akses.master_masalah_keperawatan_neonatus;}
    public static boolean getmaster_rencana_keperawatan_neonatus(){return akses.master_rencana_keperawatan_neonatus;}
    public static boolean getpenilaian_awal_keperawatan_ranap_neonatus(){return akses.penilaian_awal_keperawatan_ranap_neonatus;}
    public static boolean getsatu_sehat_kirim_observation_radiologi(){return akses.satu_sehat_kirim_observation_radiologi;}
    public static boolean getsatu_sehat_kirim_diagnosticreport_radiologi(){return akses.satu_sehat_kirim_diagnosticreport_radiologi;}
    public static boolean gethasil_endoskopi_telinga(){return akses.hasil_endoskopi_telinga;}
    public static boolean getsatu_sehat_mapping_lab(){return akses.satu_sehat_mapping_lab;}
    public static boolean getsatu_sehat_kirim_servicerequest_lab(){return akses.satu_sehat_kirim_servicerequest_lab;}
    public static boolean getsatu_sehat_kirim_servicerequest_labmb(){return akses.satu_sehat_kirim_servicerequest_labmb;}
    public static boolean getsatu_sehat_kirim_specimen_lab(){return akses.satu_sehat_kirim_specimen_lab;}
    public static boolean getsatu_sehat_kirim_specimen_labmb(){return akses.satu_sehat_kirim_specimen_labmb;}
    public static boolean getsatu_sehat_kirim_observation_lab(){return akses.satu_sehat_kirim_observation_lab;}
    public static boolean getsatu_sehat_kirim_observation_labmb(){return akses.satu_sehat_kirim_observation_labmb;}
    public static boolean getsatu_sehat_kirim_diagnosticreport_lab(){return akses.satu_sehat_kirim_diagnosticreport_lab;}
    public static boolean getsatu_sehat_kirim_diagnosticreport_labmb(){return akses.satu_sehat_kirim_diagnosticreport_labmb;}
    public static boolean getkepatuhan_kelengkapan_keselamatan_bedah(){return akses.kepatuhan_kelengkapan_keselamatan_bedah;}
    public static boolean getnilai_piutang_perjenis_bayar_per_bulan(){return akses.nilai_piutang_perjenis_bayar_per_bulan;}
    public static boolean getringkasan_piutang_jenis_bayar(){return akses.ringkasan_piutang_jenis_bayar;}
    public static boolean getpenilaian_pasien_imunitas_rendah(){return akses.penilaian_pasien_imunitas_rendah;}
    public static boolean getbalance_cairan(){return akses.balance_cairan;}
    public static boolean getcatatan_observasi_chbp(){return akses.catatan_observasi_chbp;}
    public static boolean getcatatan_observasi_induksi_persalinan(){return akses.catatan_observasi_induksi_persalinan;}
    public static boolean getskp_kategori_penilaian(){return akses.skp_kategori_penilaian;}
    public static boolean getskp_kriteria_penilaian(){return akses.skp_kriteria_penilaian;}
    public static boolean getskp_penilaian(){return akses.skp_penilaian;}
    public static boolean getreferensi_poli_mobilejknfktp(){return akses.referensi_poli_mobilejknfktp;}
    public static boolean getreferensi_dokter_mobilejknfktp(){return akses.referensi_dokter_mobilejknfktp;}
    public static boolean getskp_rekapitulasi_penilaian(){return akses.skp_rekapitulasi_penilaian;}
    public static boolean getpembayaran_pihak_ke3_bankmandiri(){return akses.pembayaran_pihak_ke3_bankmandiri;}
    public static boolean getmetode_pembayaran_bankmandiri(){return akses.metode_pembayaran_bankmandiri;}
    public static boolean getbank_tujuan_transfer_bankmandiri(){return akses.bank_tujuan_transfer_bankmandiri;}
    public static boolean getkodetransaksi_tujuan_transfer_bankmandiri(){return akses.kodetransaksi_tujuan_transfer_bankmandiri;}
    public static boolean getkonsultasi_medik(){return akses.konsultasi_medik;}
    public static boolean getjawaban_konsultasi_medik(){return akses.jawaban_konsultasi_medik;}
    public static boolean getpcare_cek_alergi(){return akses.pcare_cek_alergi;}
    public static boolean getpcare_cek_prognosa(){return akses.pcare_cek_prognosa;}
    public static boolean getdata_sasaran_usiaproduktif(){return akses.data_sasaran_usiaproduktif;}
    public static boolean getdata_sasaran_usialansia(){return akses.data_sasaran_usialansia;}
    public static boolean getskrining_perilaku_merokok_sekolah_remaja(){return akses.skrining_perilaku_merokok_sekolah_remaja;}
    public static boolean getskrining_kekerasan_pada_perempuan(){return akses.skrining_kekerasan_pada_perempuan;}
    public static boolean getskrining_obesitas(){return akses.skrining_obesitas;}
    public static boolean getskrining_risiko_kanker_payudara(){return akses.skrining_risiko_kanker_payudara;}
    public static boolean getskrining_risiko_kanker_paru(){return akses.skrining_risiko_kanker_paru;}
    public static boolean getskrining_tbc(){return akses.skrining_tbc;}
    public static boolean getskrining_kesehatan_gigi_mulut_remaja(){return akses.skrining_kesehatan_gigi_mulut_remaja;}
    public static boolean getpenilaian_awal_keperawatan_ranap_bayi(){return akses.penilaian_awal_keperawatan_ranap_bayi;}
    public static boolean getbooking_mcu_perusahaan(){return akses.booking_mcu_perusahaan;}
    public static boolean getcatatan_observasi_restrain_nonfarma(){return akses.catatan_observasi_restrain_nonfarma;}
    public static boolean getcatatan_observasi_ventilator(){return akses.catatan_observasi_ventilator;}
    public static boolean getcatatan_anestesi_sedasi(){return akses.catatan_anestesi_sedasi;}
    public static boolean getskrining_puma(){return akses.skrining_puma;}
    public static boolean getsatu_sehat_kirim_careplan(){return akses.satu_sehat_kirim_careplan;}
    public static boolean getsatu_sehat_kirim_medicationstatement(){return akses.satu_sehat_kirim_medicationstatement;}
    public static boolean getskrining_adiksi_nikotin(){return akses.skrining_adiksi_nikotin;}
    public static boolean getskrining_thalassemia(){return akses.skrining_thalassemia;}
    public static boolean getskrining_instrumen_sdq(){return akses.skrining_instrumen_sdq;}
    public static boolean getskrining_instrumen_srq(){return akses.skrining_instrumen_srq;}
    public static boolean getchecklist_pemberian_fibrinolitik(){return akses.checklist_pemberian_fibrinolitik;}
    public static boolean getskrining_kanker_kolorektal(){return akses.skrining_kanker_kolorektal;}
    public static boolean getdapur_pemesanan(){return akses.dapur_pemesanan;}
    public static boolean getbayar_pesan_dapur(){return akses.bayar_pesan_dapur;}
    public static boolean gethutang_dapur(){return akses.hutang_dapur;}
    public static boolean gettitip_faktur_dapur(){return akses.titip_faktur_dapur;}
    public static boolean getvalidasi_tagihan_dapur(){return akses.validasi_tagihan_dapur;}
    public static boolean getsurat_pemesanan_dapur(){return akses.surat_pemesanan_dapur;}
    public static boolean getpengajuan_barang_dapur(){return akses.pengajuan_barang_dapur;}
    public static boolean getdapur_returbeli(){return akses.dapur_returbeli;}
    public static boolean gethibah_dapur(){return akses.hibah_dapur;}
    public static boolean getringkasan_penerimaan_dapur(){return akses.ringkasan_penerimaan_dapur;}
    public static boolean getringkasan_pengajuan_dapur(){return akses.ringkasan_pengajuan_dapur;}
    public static boolean getringkasan_pemesanan_dapur(){return akses.ringkasan_pemesanan_dapur;}
    public static boolean getringkasan_returbeli_dapur(){return akses.ringkasan_returbeli_dapur;}
    public static boolean getringkasan_stokkeluar_dapur(){return akses.ringkasan_stokkeluar_dapur;}
    public static boolean getdapur_stokkeluar_pertanggal(){return akses.dapur_stokkeluar_pertanggal;}
    public static boolean getsirkulasi_dapur(){return akses.sirkulasi_dapur;}
    public static boolean getsirkulasi_dapur2(){return akses.sirkulasi_dapur2;}
    public static boolean getverifikasi_penerimaan_dapur(){return akses.verifikasi_penerimaan_dapur;}
    public static boolean getnilai_penerimaan_vendor_dapur_perbulan(){return akses.nilai_penerimaan_vendor_dapur_perbulan;}
    public static boolean getringkasan_hutang_vendor_dapur(){return akses.ringkasan_hutang_vendor_dapur;}
    public static boolean getpindah_kamar_pilihan_2(){return akses.pindah_kamar_pilihan_2;}
    public static boolean getpenilaian_psikologi_klinis(){return akses.penilaian_psikologi_klinis;}
    public static boolean getpenilaian_awal_medis_ranap_neonatus(){return akses.penilaian_awal_medis_ranap_neonatus;}
    public static boolean getpenilaian_derajat_dehidrasi(){return akses.penilaian_derajat_dehidrasi;}
    public static boolean getringkasan_jasa_tindakan_medis(){return akses.ringkasan_jasa_tindakan_medis;}
    public static boolean getpendapatan_per_akun(){return akses.pendapatan_per_akun;}
    public static boolean gethasil_pemeriksaan_echo(){return akses.hasil_pemeriksaan_echo;}
    public static boolean getpenilaian_bayi_baru_lahir(){return akses.penilaian_bayi_baru_lahir;}
    public static boolean getrl1_3_ketersediaan_kamar(){return akses.rl1_3_ketersediaan_kamar;}
    public static boolean getpendapatan_per_akun_closing(){return akses.pendapatan_per_akun_closing;}
    public static boolean getpengeluaran_pengeluaran(){return akses.pengeluaran_pengeluaran;}
    public static boolean getskrining_diabetes_melitus(){return akses.skrining_diabetes_melitus;}
    public static boolean getlaporan_tindakan(){return akses.laporan_tindakan;}
    public static boolean getpelaksanaan_informasi_edukasi(){return akses.pelaksanaan_informasi_edukasi;}
    public static boolean getlayanan_kedokteran_fisik_rehabilitasi(){return akses.layanan_kedokteran_fisik_rehabilitasi;}
    public static boolean getskrining_kesehatan_gigi_mulut_balita(){return akses.skrining_kesehatan_gigi_mulut_balita;}
    public static boolean getskrining_anemia(){return akses.skrining_anemia;}
    public static boolean getlayanan_program_kfr(){return akses.layanan_program_kfr;}
    public static boolean getskrining_hipertensi(){return akses.skrining_hipertensi;}
    public static boolean getskrining_kesehatan_penglihatan(){return akses.skrining_kesehatan_penglihatan;}
    public static boolean getcatatan_observasi_hemodialisa(){return akses.catatan_observasi_hemodialisa;}
    public static boolean getskrining_kesehatan_gigi_mulut_dewasa(){return akses.skrining_kesehatan_gigi_mulut_dewasa;}
    public static boolean getskrining_risiko_kanker_serviks(){return akses.skrining_risiko_kanker_serviks;}
    public static boolean getcatatan_cairan_hemodialisa(){return akses.catatan_cairan_hemodialisa;}
    public static boolean getskrining_kesehatan_gigi_mulut_lansia(){return akses.skrining_kesehatan_gigi_mulut_lansia;}
    public static boolean getskrining_indra_pendengaran(){return akses.skrining_indra_pendengaran;}
    public static boolean getcatatan_pengkajian_paska_operasi(){return akses.catatan_pengkajian_paska_operasi;}
    public static boolean getskrining_frailty_syndrome(){return akses.skrining_frailty_syndrome;}
    public static boolean getsirkulasi_cssd(){return akses.sirkulasi_cssd;}
    public static boolean getlama_pelayanan_cssd(){return akses.lama_pelayanan_cssd;}
    public static boolean getcatatan_observasi_bayi(){return akses.catatan_observasi_bayi;}
    public static boolean getriwayat_surat_peringatan(){return akses.riwayat_surat_peringatan;}
    public static boolean getmaster_kesimpulan_anjuran_mcu(){return akses.master_kesimpulan_anjuran_mcu;}
    public static boolean getkategori_piutang_jasa_perusahaan(){return akses.kategori_piutang_jasa_perusahaan;}
    public static boolean getpiutang_jasa_perusahaan(){return akses.piutang_jasa_perusahaan;}
    public static boolean getbayar_piutang_jasa_perusahaan(){return akses.bayar_piutang_jasa_perusahaan;}
    public static boolean getpiutang_jasa_perusahaan_belum_lunas(){return akses.piutang_jasa_perusahaan_belum_lunas;}
    public static boolean getchecklist_kesiapan_anestesi(){return akses.checklist_kesiapan_anestesi;}
    public static boolean getpiutang_peminjaman_uang_belum_lunas(){return akses.piutang_peminjaman_uang_belum_lunas;}
    public static boolean gethasil_pemeriksaan_slit_lamp(){return akses.hasil_pemeriksaan_slit_lamp;}
    public static boolean gethasil_pemeriksaan_oct(){return akses.hasil_pemeriksaan_oct;}
    public static boolean getbeban_hutang_lain(){return akses.beban_hutang_lain;}
    public static boolean getpoli_asal_pasien_ranap(){return akses.poli_asal_pasien_ranap;}
    public static boolean getpemberi_hutang_lain(){return akses.pemberi_hutang_lain;}
    public static boolean getdokter_asal_pasien_ranap(){return akses.dokter_asal_pasien_ranap;}
    public static boolean getduta_parkir_rekap_keluar(){return akses.duta_parkir_rekap_keluar;}
    public static boolean getsurat_keterangan_layak_terbang(){return akses.surat_keterangan_layak_terbang;}
    public static boolean getbayar_beban_hutang_lain(){return akses.bayar_beban_hutang_lain;}
    public static boolean getsurat_persetujuan_pemeriksaan_hiv(){return akses.surat_persetujuan_pemeriksaan_hiv;}
    public static boolean getskrining_instrumen_acrs(){return akses.skrining_instrumen_acrs;}
    public static boolean getsurat_pernyataan_memilih_dpjp(){return akses.surat_pernyataan_memilih_dpjp;}
    public static boolean getskrining_instrumen_mental_emosional(){return akses.skrining_instrumen_mental_emosional;}
    public static boolean getpelanggan_lab_kesehatan_lingkungan(){return akses.pelanggan_lab_kesehatan_lingkungan;}
    public static boolean getkriteria_masuk_nicu(){return akses.kriteria_masuk_nicu;}
    public static boolean getkriteria_keluar_nicu(){return akses.kriteria_keluar_nicu;}
    public static boolean getpenilaian_medis_ranap_psikiatrik(){return akses.penilaian_medis_ranap_psikiatrik;}
    public static boolean getkriteria_masuk_picu(){return akses.kriteria_masuk_picu;}
    public static boolean getkriteria_keluar_picu(){return akses.kriteria_keluar_picu;}
    public static boolean getmaster_sampel_bakumutu(){return akses.master_sampel_bakumutu;}
    public static boolean getskrining_instrumen_amt(){return akses.skrining_instrumen_amt;}
    public static boolean getparameter_pengujian_lab_kesehatan_lingkungan(){return akses.parameter_pengujian_lab_kesehatan_lingkungan;}
    public static boolean getbpjs_kirim_obat_smc() {return akses.bpjs_kirim_obat_smc;}
    public static boolean getbpjs_edit_kirim_obat_smc() {return akses.bpjs_edit_kirim_obat_smc;}
    public static boolean getbpjs_riwayat_obat_smc() {return akses.bpjs_riwayat_obat_smc;}
    public static boolean getbpjs_riwayat_pelayanan_obat_smc() {return false;}
    public static boolean getbpjs_riwayat_pelayanan_resep_smc() {return akses.bpjs_riwayat_pelayanan_resep_smc;}
    public static boolean getnilai_normal_baku_mutu_lab_kesehatan_lingkungan(){return akses.nilai_normal_baku_mutu_lab_kesehatan_lingkungan;}
    public static boolean getskrining_pneumonia_severity_index(){return akses.skrining_pneumonia_severity_index;}
    public static boolean getpermintaan_pengujian_sampel_lab_kesehatan_lingkungan(){return akses.permintaan_pengujian_sampel_lab_kesehatan_lingkungan;}
    public static boolean getpenilaian_awal_medis_ralan_jantung(){return akses.penilaian_awal_medis_ralan_jantung;}
    public static boolean getpenilaian_awal_medis_ralan_urologi(){return akses.penilaian_awal_medis_ralan_urologi;}
    public static boolean gethasil_pemeriksaan_treadmill(){return akses.hasil_pemeriksaan_treadmill;}
    public static boolean gethasil_pemeriksaan_echo_pediatrik(){return akses.hasil_pemeriksaan_echo_pediatrik;}
    public static boolean gettemplate_pelaksanaan_informasi_edukasi(){return akses.template_pelaksanaan_informasi_edukasi;}
    public static boolean getskrining_instrumen_esat(){return akses.skrining_instrumen_esat;}
    public static boolean getpenilaian_awal_medis_ranap_jantung(){return akses.penilaian_awal_medis_ranap_jantung;}
    public static boolean getpenugasan_pengujian_sampel_lab_kesehatan_lingkungan(){return akses.penugasan_pengujian_sampel_lab_kesehatan_lingkungan;}
    public static boolean gethasil_pengujian_sampel_lab_kesehatan_lingkungan(){return akses.hasil_pengujian_sampel_lab_kesehatan_lingkungan;}
    public static boolean getverifikasi_pengujian_sampel_lab_kesehatan_lingkungan(){return akses.verifikasi_pengujian_sampel_lab_kesehatan_lingkungan;}
    public static boolean getvalidasi_pengujian_sampel_lab_kesehatan_lingkungan(){return akses.validasi_pengujian_sampel_lab_kesehatan_lingkungan;}
    public static boolean getset_pintu_poli(){return akses.set_pintu_poli;}
    public static boolean getrekap_pelayanan_lab_kesehatan_lingkungan(){return akses.rekap_pelayanan_lab_kesehatan_lingkungan;}
    public static boolean getpembayaran_pengujian_sampel_lab_kesehatan_lingkungan(){return akses.pembayaran_pengujian_sampel_lab_kesehatan_lingkungan;}
    public static boolean getskrining_curb65(){return akses.skrining_curb65;}
    public static boolean getbpjs_potensi_prb(){return akses.bpjs_potensi_prb;}
    public static boolean getpintu_poli_smc(){return akses.pintu_poli;}
    public static boolean getbpjs_riwayat_pelayanan_obat(){return akses.bpjs_riwayat_pelayanan_obat;}
    public static boolean getskrining_gizi_kehamilan(){return akses.skrining_gizi_kehamilan;}
    public static boolean getbpjs_rekap_peserta_prb_apotek(){return akses.bpjs_rekap_peserta_prb_apotek;}
    public static boolean getbpjs_riwayat_surat_smc(){return akses.bpjs_riwayat_surat_smc;}
    public static boolean getserah_terima_anggota_tubuh_barang(){return akses.serah_terima_anggota_tubuh_barang;}
    public static boolean getpcra_icra_jenis_aktivitas_proyek(){return akses.pcra_icra_jenis_aktivitas_proyek;}
    public static boolean getpcra_icra_lokasi_kelompok_risiko_area(){return akses.pcra_icra_lokasi_kelompok_risiko_area;}
    public static boolean getpcra_icra_kelas_risiko_pencegahan(){return akses.pcra_icra_kelas_risiko_pencegahan;}
    public static boolean getpcra_icra_tindakan_pengendalian(){return akses.pcra_icra_tindakan_pengendalian;}
    public static boolean getpcra_icra_identifkasi_risiko_infeksi(){return akses.pcra_icra_identifkasi_risiko_infeksi;}
    public static boolean getpcra_icra_identifkasi_risiko_keselamatan(){return akses.pcra_icra_identifkasi_risiko_keselamatan;}
    public static boolean getpcra_icra_identifkasi_risiko_kebakaran(){return akses.pcra_icra_identifkasi_risiko_kebakaran;}
    public static boolean getpcra_icra_identifkasi_risiko_utilitas(){return akses.pcra_icra_identifkasi_risiko_utilitas;}
    public static boolean getbpjs_daftar_resep_apotek(){return akses.bpjs_daftar_resep_apotek;}
    public static boolean getdaftar_permintaan_resep_iterasi_bpjs(){return akses.daftar_permintaan_resep_iterasi_bpjs;}
    public static boolean getpcra_icra_pengkajian_risiko_prakonstruksi(){return akses.pcra_icra_pengkajian_risiko_prakonstruksi;}
    public static boolean getpcra_icra_persyaratan_harus_dipenuhi(){return akses.pcra_icra_persyaratan_harus_dipenuhi;}
    public static boolean getsatu_sehat_kirim_questionresponse_telaah_farmasi(){return akses.satu_sehat_kirim_questionresponse_telaah_farmasi;}
    public static boolean getsatu_sehat_kirim_allergy_intolerance(){return akses.satu_sehat_kirim_allergy_intolerance;}
    public static boolean getkonsultasi_perawat(){return akses.konsultasi_perawat;}
    public static boolean getjawaban_konsultasi_perawat(){return akses.jawaban_konsultasi_perawat;}
    public static boolean getbridging_smart_klaim_bpjs(){return akses.bridging_smart_klaim_bpjs;}
    public static boolean getmapping_prosedur_smart_klaim_bpjs(){return akses.mapping_prosedur_smart_klaim_bpjs;}
    public static boolean getmapping_penyakit_smart_klaim_bpjs(){return akses.mapping_penyakit_smart_klaim_bpjs;}
    public static boolean getpermintaan_binrohtal(){return akses.permintaan_binrohtal;}
    public static boolean getsurat_permintaan_perlindungan_dari_kekerasan(){return akses.surat_permintaan_perlindungan_dari_kekerasan;}
    public static boolean getsurat_permohonan_privasi(){return akses.surat_permohonan_privasi;}
    public static boolean getsurat_permintaan_second_opinion(){return akses.surat_permintaan_second_opinion;}
    public static boolean getsurat_keterangan_berobat(){return akses.surat_keterangan_berobat;}
    public static boolean getsurat_penolakan_resusitasi(){return akses.surat_penolakan_resusitasi;}
    public static boolean getcatatan_observasi_ruang_ok(){return akses.catatan_observasi_ruang_ok;}
    public static boolean gethasil_pemeriksaan_usg_abdomen(){return akses.hasil_pemeriksaan_usg_abdomen;}
    public static boolean getintervensi_nyeri_farmakologi(){return akses.intervensi_nyeri_farmakologi;}
    public static boolean getakses_edit_sementara() {akses.setEdit();return akses.edit;}
    public static void resetEdit() {akses.edit = false; akses.tglSelesai = -1;}
    private static void setEdit() {
        if (! akses.edit) {
            return;
        }
        try {
            if (((new sekuel().cariTglSmc("select now()").getTime() - akses.tglSelesai) / 1000) > 0) {
                resetEdit();
            }
        } catch (Exception e) {
            resetEdit();
        }
    }
}
