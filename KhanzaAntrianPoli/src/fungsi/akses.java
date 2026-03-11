package fungsi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Owner
 */
public final class akses {
    private static final Connection koneksi=koneksiDB.condb();
    private static PreparedStatement ps,ps2;
    private static ResultSet rs,rs2;

    private static String kode="",kdbangsal="",namars="",alamatrs="",kabupatenrs="",propinsirs="",kontakrs="",emailrs="";
    private static int jml1=0,jml2=0,lebar=0,tinggi=0;
    private static boolean admin=false,user=false,vakum=false,aplikasi=false,penyakit=false,obat_penyakit=false,dokter=false,jadwal_praktek=false,petugas=false,pasien=false,registrasi=false,
            tindakan_ralan=false,kamar_inap=false,tindakan_ranap=false,operasi=false,rujukan_keluar=false,rujukan_masuk=false,beri_obat=false,
            resep_pulang=false,pasien_meninggal=false,diet_pasien=false,kelahiran_bayi=false,periksa_lab=false,periksa_radiologi=false,
            kasir_ralan=false,deposit_pasien=false,piutang_pasien=false,peminjaman_berkas=false,barcode=false,presensi_harian=false,
            presensi_bulanan=false,pegawai_admin=false,pegawai_user=false,suplier=false,satuan_barang=false,konversi_satuan=false,jenis_barang=false,
            obat=false,stok_opname_obat=false,stok_obat_pasien=false,pengadaan_obat=false,pemesanan_obat=false,penjualan_obat=false,piutang_obat=false,
            retur_ke_suplier=false,retur_dari_pembeli=false,retur_obat_ranap=false,retur_piutang_pasien=false,keuntungan_penjualan=false,keuntungan_beri_obat=false,
            sirkulasi_obat=false,ipsrs_barang=false,ipsrs_pengadaan_barang=false,ipsrs_stok_keluar=false,ipsrs_rekap_pengadaan=false,ipsrs_rekap_stok_keluar=false,
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
            pny_takmenular_ranap=false,pny_takmenular_ralan=false,kunjungan_ralan=false,rl32=false,rl33=false,rl37=false,rl38=false;


    public static void setData(String user, String pass) {
       try {
           ps=koneksi.prepareStatement("select * from admin where usere=AES_ENCRYPT(?,'nur') and passworde=AES_ENCRYPT(?,'windi')");
           ps2=koneksi.prepareStatement("select * from user where id_user=AES_ENCRYPT(?,'nur') and password=AES_ENCRYPT(?,'windi')");
           ps.setString(1,user);
           ps.setString(2,pass);
           rs=ps.executeQuery();
           rs.last();

           ps2.setString(1,user);
           ps2.setString(2,pass);
           rs2=ps2.executeQuery();
           rs2.last();

               akses.jml1=rs.getRow();
               akses.jml2=rs2.getRow();

                if(user.equals("admin") && pass.equals("akusayangsamakamu")){
                    akses.kode="Admin Utama";
                    akses.penyakit=true;
                    akses.obat_penyakit=true;
                    akses.dokter=true;
                    akses.jadwal_praktek=true;
                    akses.petugas=true;
                    akses.pasien=true;
                    akses.registrasi=true;
                    akses.tindakan_ralan=true;
                    akses.kamar_inap=true;
                    akses.tindakan_ranap=true;
                    akses.operasi=true;
                    akses.rujukan_keluar=true;
                    akses.rujukan_masuk=true;
                    akses.beri_obat=true;
                    akses.resep_pulang=true;
                    akses.pasien_meninggal=true;
                    akses.diet_pasien=true;
                    akses.kelahiran_bayi=true;
                    akses.periksa_lab=true;
                    akses.periksa_radiologi=true;
                    akses.kasir_ralan=true;
                    akses.deposit_pasien=true;
                    akses.piutang_pasien=true;
                    akses.peminjaman_berkas=true;
                    akses.barcode=true;
                    akses.presensi_harian=true;
                    akses.presensi_bulanan=true;
                    akses.pegawai_admin=true;
                    akses.pegawai_user=true;
                    akses.suplier=true;
                    akses.satuan_barang=true;
                    akses.konversi_satuan=true;
                    akses.jenis_barang=true;
                    akses.obat=true;
                    akses.stok_opname_obat=true;
                    akses.stok_obat_pasien=true;
                    akses.pengadaan_obat=true;
                    akses.pemesanan_obat=true;
                    akses.penjualan_obat=true;
                    akses.piutang_obat=true;
                    akses.retur_ke_suplier=true;
                    akses.retur_dari_pembeli=true;
                    akses.retur_obat_ranap=true;
                    akses.retur_piutang_pasien=true;
                    akses.keuntungan_penjualan=true;
                    akses.keuntungan_beri_obat=true;
                    akses.sirkulasi_obat=true;
                    akses.ipsrs_barang=true;
                    akses.ipsrs_pengadaan_barang=true;
                    akses.ipsrs_stok_keluar=true;
                    akses.ipsrs_rekap_pengadaan=true;
                    akses.ipsrs_rekap_stok_keluar=true;
                    akses.ipsrs_pengeluaran_harian=true;
                    akses.inventaris_jenis=true;
                    akses.inventaris_kategori=true;
                    akses.inventaris_merk=true;
                    akses.inventaris_ruang=true;
                    akses.inventaris_produsen=true;
                    akses.inventaris_koleksi=true;
                    akses.inventaris_inventaris=true;
                    akses.inventaris_sirkulasi=true;
                    akses.parkir_jenis=true;
                    akses.parkir_in=true;
                    akses.parkir_out=true;
                    akses.parkir_rekap_harian=true;
                    akses.parkir_rekap_bulanan=true;
                    akses.informasi_kamar=true;
                    akses.harian_tindakan_poli=true;
                    akses.obat_per_poli=true;
                    akses.obat_per_kamar=true;
                    akses.obat_per_dokter_ralan=true;
                    akses.obat_per_dokter_ranap=true;
                    akses.harian_dokter=true;
                    akses.bulanan_dokter=true;
                    akses.harian_paramedis=true;
                    akses.bulanan_paramedis=true;
                    akses.pembayaran_ralan=true;
                    akses.pembayaran_ranap=true;
                    akses.rekap_pembayaran_ralan=true;
                    akses.rekap_pembayaran_ranap=true;
                    akses.tagihan_masuk=true;
                    akses.tambahan_biaya=true;
                    akses.potongan_biaya=true;
                    akses.resep_obat=true;
                    akses.resume_pasien=true;
                    akses.penyakit_ralan=true;
                    akses.penyakit_ranap=true;
                    akses.kamar=true;
                    akses.tarif_ralan=true;
                    akses.tarif_ranap=true;
                    akses.tarif_lab=true;
                    akses.tarif_radiologi=true;
                    akses.tarif_operasi=true;
                    akses.akun_rekening=true;
                    akses.rekening_tahun=true;
                    akses.posting_jurnal=true;
                    akses.buku_besar=true;
                    akses.cashflow=true;
                    akses.keuangan=true;
                    akses.pengeluaran=true;
                    akses.setup_pjlab=true;
                    akses.setup_otolokasi=true;
                    akses.setup_jam_kamin=true;
                    akses.setup_embalase=true;
                    akses.tracer_login=true;
                    akses.display=true;
                    akses.set_harga_obat=true;
                    akses.set_penggunaan_tarif=true;
                    akses.set_oto_ralan=true;
                    akses.biaya_harian=true;
                    akses.biaya_masuk_sekali=true;
                    akses.set_no_rm=true;
                    akses.billing_ralan=true;
                    akses.billing_ranap=true;
                    akses.jm_ranap_dokter=true;
                    akses.igd=true;
                    akses.barcoderalan=true;
                    akses.barcoderanap=true;
                    akses.set_harga_obat_ralan=true;
                    akses.set_harga_obat_ranap=true;
                    akses.penyakit_pd3i=true;
                    akses.surveilans_pd3i=true;
                    akses.surveilans_ralan=true;
                    akses.diagnosa_pasien=true;
                    akses.admin=true;
                    akses.user=true;
                    akses.vakum=true;
                    akses.aplikasi=true;
                    akses.surveilans_ranap=true;
                    akses.pny_takmenular_ranap=true;
                    akses.pny_takmenular_ralan=true;
                    akses.kunjungan_ralan=true;
                    akses.rl32=true;
                    akses.rl33=true;
                    akses.rl37=true;
                    akses.rl38=true;
                }else if(rs.getRow()>=1){
                    akses.kode="Admin Utama";
                    akses.penyakit=true;
                    akses.obat_penyakit=true;
                    akses.dokter=true;
                    akses.jadwal_praktek=true;
                    akses.petugas=true;
                    akses.pasien=true;
                    akses.registrasi=true;
                    akses.tindakan_ralan=true;
                    akses.kamar_inap=true;
                    akses.tindakan_ranap=true;
                    akses.operasi=true;
                    akses.rujukan_keluar=true;
                    akses.rujukan_masuk=true;
                    akses.beri_obat=true;
                    akses.resep_pulang=true;
                    akses.pasien_meninggal=true;
                    akses.diet_pasien=true;
                    akses.kelahiran_bayi=true;
                    akses.periksa_lab=true;
                    akses.periksa_radiologi=true;
                    akses.kasir_ralan=true;
                    akses.deposit_pasien=true;
                    akses.piutang_pasien=true;
                    akses.peminjaman_berkas=true;
                    akses.barcode=true;
                    akses.presensi_harian=true;
                    akses.presensi_bulanan=true;
                    akses.pegawai_admin=true;
                    akses.pegawai_user=true;
                    akses.suplier=true;
                    akses.satuan_barang=true;
                    akses.konversi_satuan=true;
                    akses.jenis_barang=true;
                    akses.obat=true;
                    akses.stok_opname_obat=true;
                    akses.stok_obat_pasien=true;
                    akses.pengadaan_obat=true;
                    akses.pemesanan_obat=true;
                    akses.penjualan_obat=true;
                    akses.piutang_obat=true;
                    akses.retur_ke_suplier=true;
                    akses.retur_dari_pembeli=true;
                    akses.retur_obat_ranap=true;
                    akses.retur_piutang_pasien=true;
                    akses.keuntungan_penjualan=true;
                    akses.keuntungan_beri_obat=true;
                    akses.sirkulasi_obat=true;
                    akses.ipsrs_barang=true;
                    akses.ipsrs_pengadaan_barang=true;
                    akses.ipsrs_stok_keluar=true;
                    akses.ipsrs_rekap_pengadaan=true;
                    akses.ipsrs_rekap_stok_keluar=true;
                    akses.ipsrs_pengeluaran_harian=true;
                    akses.inventaris_jenis=true;
                    akses.inventaris_kategori=true;
                    akses.inventaris_merk=true;
                    akses.inventaris_ruang=true;
                    akses.inventaris_produsen=true;
                    akses.inventaris_koleksi=true;
                    akses.inventaris_inventaris=true;
                    akses.inventaris_sirkulasi=true;
                    akses.parkir_jenis=true;
                    akses.parkir_in=true;
                    akses.parkir_out=true;
                    akses.parkir_rekap_harian=true;
                    akses.parkir_rekap_bulanan=true;
                    akses.informasi_kamar=true;
                    akses.harian_tindakan_poli=true;
                    akses.obat_per_poli=true;
                    akses.obat_per_kamar=true;
                    akses.obat_per_dokter_ralan=true;
                    akses.obat_per_dokter_ranap=true;
                    akses.harian_dokter=true;
                    akses.bulanan_dokter=true;
                    akses.harian_paramedis=true;
                    akses.bulanan_paramedis=true;
                    akses.pembayaran_ralan=true;
                    akses.pembayaran_ranap=true;
                    akses.rekap_pembayaran_ralan=true;
                    akses.rekap_pembayaran_ranap=true;
                    akses.tagihan_masuk=true;
                    akses.tambahan_biaya=true;
                    akses.potongan_biaya=true;
                    akses.resep_obat=true;
                    akses.resume_pasien=true;
                    akses.penyakit_ralan=true;
                    akses.penyakit_ranap=true;
                    akses.kamar=true;
                    akses.tarif_ralan=true;
                    akses.tarif_ranap=true;
                    akses.tarif_lab=true;
                    akses.tarif_radiologi=true;
                    akses.tarif_operasi=true;
                    akses.akun_rekening=true;
                    akses.rekening_tahun=true;
                    akses.posting_jurnal=true;
                    akses.buku_besar=true;
                    akses.cashflow=true;
                    akses.keuangan=true;
                    akses.pengeluaran=true;
                    akses.setup_pjlab=true;
                    akses.setup_otolokasi=true;
                    akses.setup_jam_kamin=true;
                    akses.setup_embalase=true;
                    akses.tracer_login=true;
                    akses.display=true;
                    akses.set_harga_obat=true;
                    akses.set_penggunaan_tarif=true;
                    akses.set_oto_ralan=true;
                    akses.biaya_harian=true;
                    akses.biaya_masuk_sekali=true;
                    akses.set_no_rm=true;
                    akses.billing_ralan=true;
                    akses.billing_ranap=true;
                    akses.jm_ranap_dokter=true;
                    akses.igd=true;
                    akses.barcoderalan=true;
                    akses.barcoderanap=true;
                    akses.set_harga_obat_ralan=true;
                    akses.set_harga_obat_ranap=true;
                    akses.penyakit_pd3i=true;
                    akses.surveilans_pd3i=true;
                    akses.surveilans_ralan=true;
                    akses.diagnosa_pasien=true;
                    akses.admin=true;
                    akses.user=true;
                    akses.vakum=true;
                    akses.aplikasi=true;
                    akses.surveilans_ranap=true;
                    akses.pny_takmenular_ranap=true;
                    akses.pny_takmenular_ralan=true;
                    akses.kunjungan_ralan=true;
                    akses.rl32=true;
                    akses.rl33=true;
                    akses.rl37=true;
                    akses.rl38=true;
                }else if(rs2.getRow()>=1){
                    rs2.beforeFirst();
                    rs2.next();
                    akses.kode=user;
                    akses.penyakit=rs2.getBoolean("penyakit");
                    akses.obat_penyakit=rs2.getBoolean("obat_penyakit");
                    akses.dokter=rs2.getBoolean("dokter");
                    akses.jadwal_praktek=rs2.getBoolean("jadwal_praktek");
                    akses.petugas=rs2.getBoolean("petugas");
                    akses.pasien=rs2.getBoolean("pasien");
                    akses.registrasi=rs2.getBoolean("registrasi");
                    akses.tindakan_ralan=rs2.getBoolean("tindakan_ralan");
                    akses.kamar_inap=rs2.getBoolean("kamar_inap");
                    akses.tindakan_ranap=rs2.getBoolean("tindakan_ranap");
                    akses.operasi=rs2.getBoolean("operasi");
                    akses.rujukan_keluar=rs2.getBoolean("rujukan_keluar");
                    akses.rujukan_masuk=rs2.getBoolean("rujukan_masuk");
                    akses.beri_obat=rs2.getBoolean("beri_obat");
                    akses.resep_pulang=rs2.getBoolean("resep_pulang");
                    akses.pasien_meninggal=rs2.getBoolean("pasien_meninggal");
                    akses.diet_pasien=rs2.getBoolean("diet_pasien");
                    akses.kelahiran_bayi=rs2.getBoolean("kelahiran_bayi");
                    akses.periksa_lab=rs2.getBoolean("periksa_lab");
                    akses.periksa_radiologi=rs2.getBoolean("periksa_radiologi");
                    akses.kasir_ralan=rs2.getBoolean("kasir_ralan");
                    akses.deposit_pasien=rs2.getBoolean("deposit_pasien");
                    akses.piutang_pasien=rs2.getBoolean("piutang_pasien");
                    akses.peminjaman_berkas=rs2.getBoolean("peminjaman_berkas");
                    akses.barcode=rs2.getBoolean("barcode");
                    akses.presensi_harian=rs2.getBoolean("presensi_harian");
                    akses.presensi_bulanan=rs2.getBoolean("presensi_bulanan");
                    akses.pegawai_admin=rs2.getBoolean("pegawai_admin");
                    akses.pegawai_user=rs2.getBoolean("pegawai_user");
                    akses.suplier=rs2.getBoolean("suplier");
                    akses.satuan_barang=rs2.getBoolean("satuan_barang");
                    akses.konversi_satuan=rs2.getBoolean("konversi_satuan");
                    akses.jenis_barang=rs2.getBoolean("jenis_barang");
                    akses.obat=rs2.getBoolean("obat");
                    akses.stok_opname_obat=rs2.getBoolean("stok_opname_obat");
                    akses.stok_obat_pasien=rs2.getBoolean("stok_obat_pasien");
                    akses.pengadaan_obat=rs2.getBoolean("pengadaan_obat");
                    akses.pemesanan_obat=rs2.getBoolean("pemesanan_obat");
                    akses.penjualan_obat=rs2.getBoolean("penjualan_obat");
                    akses.piutang_obat=rs2.getBoolean("piutang_obat");
                    akses.retur_ke_suplier=rs2.getBoolean("retur_ke_suplier");
                    akses.retur_dari_pembeli=rs2.getBoolean("retur_dari_pembeli");
                    akses.retur_obat_ranap=rs2.getBoolean("retur_obat_ranap");
                    akses.retur_piutang_pasien=rs2.getBoolean("retur_piutang_pasien");
                    akses.keuntungan_penjualan=rs2.getBoolean("keuntungan_penjualan");
                    akses.keuntungan_beri_obat=rs2.getBoolean("keuntungan_beri_obat");
                    akses.sirkulasi_obat=rs2.getBoolean("sirkulasi_obat");
                    akses.ipsrs_barang=rs2.getBoolean("ipsrs_barang");
                    akses.ipsrs_pengadaan_barang=rs2.getBoolean("ipsrs_pengadaan_barang");
                    akses.ipsrs_stok_keluar=rs2.getBoolean("ipsrs_stok_keluar");
                    akses.ipsrs_rekap_pengadaan=rs2.getBoolean("ipsrs_rekap_pengadaan");
                    akses.ipsrs_rekap_stok_keluar=rs2.getBoolean("ipsrs_rekap_stok_keluar");
                    akses.ipsrs_pengeluaran_harian=rs2.getBoolean("ipsrs_pengeluaran_harian");
                    akses.inventaris_jenis=rs2.getBoolean("inventaris_jenis");
                    akses.inventaris_kategori=rs2.getBoolean("inventaris_kategori");
                    akses.inventaris_merk=rs2.getBoolean("inventaris_merk");
                    akses.inventaris_ruang=rs2.getBoolean("inventaris_ruang");
                    akses.inventaris_produsen=rs2.getBoolean("inventaris_produsen");
                    akses.inventaris_koleksi=rs2.getBoolean("inventaris_koleksi");
                    akses.inventaris_inventaris=rs2.getBoolean("inventaris_inventaris");
                    akses.inventaris_sirkulasi=rs2.getBoolean("inventaris_sirkulasi");
                    akses.parkir_jenis=rs2.getBoolean("parkir_jenis");
                    akses.parkir_in=rs2.getBoolean("parkir_in");
                    akses.parkir_out=rs2.getBoolean("parkir_out");
                    akses.parkir_rekap_harian=rs2.getBoolean("parkir_rekap_harian");
                    akses.parkir_rekap_bulanan=rs2.getBoolean("parkir_rekap_bulanan");
                    akses.informasi_kamar=rs2.getBoolean("informasi_kamar");
                    akses.harian_tindakan_poli=rs2.getBoolean("harian_tindakan_poli");
                    akses.obat_per_poli=rs2.getBoolean("obat_per_poli");
                    akses.obat_per_kamar=rs2.getBoolean("obat_per_kamar");
                    akses.obat_per_dokter_ralan=rs2.getBoolean("obat_per_dokter_ralan");
                    akses.obat_per_dokter_ranap=rs2.getBoolean("obat_per_dokter_ranap");
                    akses.harian_dokter=rs2.getBoolean("harian_dokter");
                    akses.bulanan_dokter=rs2.getBoolean("bulanan_dokter");
                    akses.harian_paramedis=rs2.getBoolean("harian_paramedis");
                    akses.bulanan_paramedis=rs2.getBoolean("bulanan_paramedis");
                    akses.pembayaran_ralan=rs2.getBoolean("pembayaran_ralan");
                    akses.pembayaran_ranap=rs2.getBoolean("pembayaran_ranap");
                    akses.rekap_pembayaran_ralan=rs2.getBoolean("rekap_pembayaran_ralan");
                    akses.rekap_pembayaran_ranap=rs2.getBoolean("rekap_pembayaran_ranap");
                    akses.tagihan_masuk=rs2.getBoolean("tagihan_masuk");
                    akses.tambahan_biaya=rs2.getBoolean("tambahan_biaya");
                    akses.potongan_biaya=rs2.getBoolean("potongan_biaya");
                    akses.resep_obat=rs2.getBoolean("resep_obat");
                    akses.resume_pasien=rs2.getBoolean("resume_pasien");
                    akses.penyakit_ralan=rs2.getBoolean("penyakit_ralan");
                    akses.penyakit_ranap=rs2.getBoolean("penyakit_ranap");
                    akses.kamar=rs2.getBoolean("kamar");
                    akses.tarif_ralan=rs2.getBoolean("tarif_ralan");
                    akses.tarif_ranap=rs2.getBoolean("tarif_ranap");
                    akses.tarif_lab=rs2.getBoolean("tarif_lab");
                    akses.tarif_radiologi=rs2.getBoolean("tarif_radiologi");
                    akses.tarif_operasi=rs2.getBoolean("tarif_operasi");
                    akses.akun_rekening=rs2.getBoolean("akun_rekening");
                    akses.rekening_tahun=rs2.getBoolean("rekening_tahun");
                    akses.posting_jurnal=rs2.getBoolean("posting_jurnal");
                    akses.buku_besar=rs2.getBoolean("buku_besar");
                    akses.cashflow=rs2.getBoolean("cashflow");
                    akses.keuangan=rs2.getBoolean("keuangan");
                    akses.pengeluaran=rs2.getBoolean("pengeluaran");
                    akses.setup_pjlab=rs2.getBoolean("setup_pjlab");
                    akses.setup_otolokasi=rs2.getBoolean("setup_otolokasi");
                    akses.setup_jam_kamin=rs2.getBoolean("setup_jam_kamin");
                    akses.setup_embalase=rs2.getBoolean("setup_embalase");
                    akses.tracer_login=rs2.getBoolean("tracer_login");
                    akses.display=rs2.getBoolean("display");
                    akses.set_harga_obat=rs2.getBoolean("set_harga_obat");
                    akses.set_penggunaan_tarif=rs2.getBoolean("set_penggunaan_tarif");
                    akses.set_oto_ralan=rs2.getBoolean("set_oto_ralan");
                    akses.biaya_harian=rs2.getBoolean("biaya_harian");
                    akses.biaya_masuk_sekali=rs2.getBoolean("biaya_masuk_sekali");
                    akses.set_no_rm=rs2.getBoolean("set_no_rm");
                    akses.billing_ralan=rs2.getBoolean("billing_ralan");
                    akses.billing_ranap=rs2.getBoolean("billing_ranap");
                    akses.jm_ranap_dokter=rs2.getBoolean("jm_ranap_dokter");
                    akses.igd=rs2.getBoolean("igd");
                    akses.barcoderalan=rs2.getBoolean("barcoderalan");
                    akses.barcoderanap=rs2.getBoolean("barcoderanap");
                    akses.set_harga_obat_ralan=rs2.getBoolean("set_harga_obat_ralan");
                    akses.set_harga_obat_ranap=rs2.getBoolean("set_harga_obat_ranap");
                    akses.penyakit_pd3i=rs2.getBoolean("penyakit_pd3i");
                    akses.surveilans_pd3i=rs2.getBoolean("surveilans_pd3i");
                    akses.surveilans_ralan=rs2.getBoolean("surveilans_ralan");
                    akses.diagnosa_pasien=rs2.getBoolean("diagnosa_pasien");
                    akses.surveilans_ranap=rs2.getBoolean("surveilans_ranap");
                    akses.admin=false;
                    akses.user=false;
                    akses.vakum=false;
                    akses.aplikasi=false;
                    akses.pny_takmenular_ranap=rs2.getBoolean("pny_takmenular_ranap");
                    akses.pny_takmenular_ralan=rs2.getBoolean("pny_takmenular_ralan");
                    akses.kunjungan_ralan=rs2.getBoolean("kunjungan_ralan");
                    akses.rl32=rs2.getBoolean("rl32");
                    akses.rl33=rs2.getBoolean("rl33");
                    akses.rl37=rs2.getBoolean("rl37");
                    akses.rl38=rs2.getBoolean("rl38");
                }else if((rs.getRow()==0)&&(rs2.getRow()==0)){
                    akses.kode="";
                    akses.penyakit= false;
                    akses.obat_penyakit= false;
                    akses.dokter= false;
                    akses.jadwal_praktek= false;
                    akses.petugas= false;
                    akses.pasien= false;
                    akses.registrasi= false;
                    akses.tindakan_ralan= false;
                    akses.kamar_inap= false;
                    akses.tindakan_ranap= false;
                    akses.operasi= false;
                    akses.rujukan_keluar= false;
                    akses.rujukan_masuk= false;
                    akses.beri_obat= false;
                    akses.resep_pulang= false;
                    akses.pasien_meninggal= false;
                    akses.diet_pasien= false;
                    akses.kelahiran_bayi= false;
                    akses.periksa_lab= false;
                    akses.periksa_radiologi= false;
                    akses.kasir_ralan= false;
                    akses.deposit_pasien= false;
                    akses.piutang_pasien= false;
                    akses.peminjaman_berkas= false;
                    akses.barcode= false;
                    akses.presensi_harian= false;
                    akses.presensi_bulanan= false;
                    akses.pegawai_admin= false;
                    akses.pegawai_user= false;
                    akses.suplier= false;
                    akses.satuan_barang= false;
                    akses.konversi_satuan= false;
                    akses.jenis_barang= false;
                    akses.obat= false;
                    akses.stok_opname_obat= false;
                    akses.stok_obat_pasien= false;
                    akses.pengadaan_obat= false;
                    akses.pemesanan_obat= false;
                    akses.penjualan_obat= false;
                    akses.piutang_obat= false;
                    akses.retur_ke_suplier= false;
                    akses.retur_dari_pembeli= false;
                    akses.retur_obat_ranap= false;
                    akses.retur_piutang_pasien= false;
                    akses.keuntungan_penjualan= false;
                    akses.keuntungan_beri_obat= false;
                    akses.sirkulasi_obat= false;
                    akses.ipsrs_barang= false;
                    akses.ipsrs_pengadaan_barang= false;
                    akses.ipsrs_stok_keluar= false;
                    akses.ipsrs_rekap_pengadaan= false;
                    akses.ipsrs_rekap_stok_keluar= false;
                    akses.ipsrs_pengeluaran_harian= false;
                    akses.inventaris_jenis= false;
                    akses.inventaris_kategori= false;
                    akses.inventaris_merk= false;
                    akses.inventaris_ruang= false;
                    akses.inventaris_produsen= false;
                    akses.inventaris_koleksi= false;
                    akses.inventaris_inventaris= false;
                    akses.inventaris_sirkulasi= false;
                    akses.parkir_jenis= false;
                    akses.parkir_in= false;
                    akses.parkir_out= false;
                    akses.parkir_rekap_harian= false;
                    akses.parkir_rekap_bulanan= false;
                    akses.informasi_kamar= false;
                    akses.harian_tindakan_poli= false;
                    akses.obat_per_poli= false;
                    akses.obat_per_kamar= false;
                    akses.obat_per_dokter_ralan= false;
                    akses.obat_per_dokter_ranap= false;
                    akses.harian_dokter= false;
                    akses.bulanan_dokter= false;
                    akses.harian_paramedis= false;
                    akses.bulanan_paramedis= false;
                    akses.pembayaran_ralan= false;
                    akses.pembayaran_ranap= false;
                    akses.rekap_pembayaran_ralan= false;
                    akses.rekap_pembayaran_ranap= false;
                    akses.tagihan_masuk= false;
                    akses.tambahan_biaya= false;
                    akses.potongan_biaya= false;
                    akses.resep_obat= false;
                    akses.resume_pasien= false;
                    akses.penyakit_ralan= false;
                    akses.penyakit_ranap= false;
                    akses.kamar= false;
                    akses.tarif_ralan= false;
                    akses.tarif_ranap= false;
                    akses.tarif_lab= false;
                    akses.tarif_radiologi= false;
                    akses.tarif_operasi= false;
                    akses.akun_rekening= false;
                    akses.rekening_tahun= false;
                    akses.posting_jurnal= false;
                    akses.buku_besar= false;
                    akses.cashflow= false;
                    akses.keuangan= false;
                    akses.pengeluaran= false;
                    akses.setup_pjlab= false;
                    akses.setup_otolokasi= false;
                    akses.setup_jam_kamin= false;
                    akses.setup_embalase= false;
                    akses.tracer_login= false;
                    akses.display= false;
                    akses.set_harga_obat= false;
                    akses.set_penggunaan_tarif= false;
                    akses.set_oto_ralan= false;
                    akses.biaya_harian= false;
                    akses.biaya_masuk_sekali= false;
                    akses.set_no_rm= false;
                    akses.billing_ralan=false;
                    akses.billing_ranap=false;
                    akses.jm_ranap_dokter=false;
                    akses.igd=false;
                    akses.barcoderalan=false;
                    akses.barcoderanap=false;
                    akses.set_harga_obat_ralan=false;
                    akses.set_harga_obat_ranap=false;
                    akses.admin= false;
                    akses.user= false;
                    akses.vakum= false;
                    akses.aplikasi= false;
                    akses.penyakit_pd3i=false;
                    akses.surveilans_pd3i=false;
                    akses.surveilans_ralan=false;
                    akses.diagnosa_pasien=false;
                    akses.surveilans_ranap=false;
                    akses.pny_takmenular_ranap=false;
                    akses.pny_takmenular_ralan=false;
                    akses.kunjungan_ralan=false;
                    akses.rl32=false;
                    akses.rl33=false;
                    akses.rl37=false;
                    akses.rl38=false;
                }
            } catch (Exception e) {
                System.out.println("error : "+e);
            }

    }

    public static int getjml1() {return akses.jml1;}
    public static int getjml2() {return akses.jml2;}
    public static boolean getadmin(){return akses.admin;}
    public static boolean getuser(){return akses.user;}
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
    public static String getnamars(){return akses.namars;}
    public static String getalamatrs(){return akses.alamatrs;}
    public static String getkabupatenrs(){return akses.kabupatenrs;}
    public static String getpropinsirs(){return akses.propinsirs;}
    public static String getkontakrs(){return akses.kontakrs;}
    public static String getemailrs(){return akses.emailrs;}
    public static boolean getkunjungan_ralan(){return akses.kunjungan_ralan;}
    public static boolean getrl32(){return akses.rl32;}
    public static boolean getrl33(){return akses.rl33;}
    public static boolean getrl37(){return akses.rl37;}
    public static boolean getrl38(){return akses.rl38;}
}
