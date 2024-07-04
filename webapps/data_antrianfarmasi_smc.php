<?php

require_once('conf/conf.php');

header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT");
header("Cache-Control: no-store, no-cache, must-revalidate");
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");

date_default_timezone_set("Asia/Makassar");

$tanggal = mktime(date("m"), date("d"), date("Y"));
$jam = date("H:i");

$_sql = "select poliklinik.nm_poli, pasien.nm_pasien,
         if (resep_obat.jam = '00:00:00', '', resep_obat.jam) as jam_validasi,
         if (resep_obat.jam_penyerahan = '00:00:00', '', resep_obat.jam_penyerahan) as jam_penyerahan,
         exists(select * from resep_dokter_racikan where resep_dokter_racikan.no_resep = resep_obat.no_resep) as is_racikan,
         dokter.nm_dokter from resep_obat
         join reg_periksa on resep_obat.no_rawat = reg_periksa.no_rawat
         join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis
         join dokter on resep_obat.kd_dokter = dokter.kd_dokter
         join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli
         where resep_obat.jam_peresepan != '00:00:00'
         and resep_obat.jam != '00:00:00'
         and resep_obat.jam between current_time() - interval 3 hour and current_time()
         and resep_obat.status = 'ralan'
         and resep_obat.tgl_peresepan = current_date()
         and reg_periksa.kd_poli != 'IGDK'
         order by resep_obat.jam desc";

$hasil = bukaquery($_sql);
?>

<div class="col s12 row">
    <div class="col s12">
        <table class="default">
            <thead>
                <tr class="head4">
                    <td><b>Asal Poli</b></td>
                    <td><b>Nama Pasien</b></td>
                    <td><b>Jenis Resep</b></td>
                    <td><b>Dokter Peresep</b></td>
                    <td><b>Mulai Pengerjaan</b></td>
                    <td><b>Penyerahan</b></td>
                </tr>
            </thead>
            <tbody>
                <?php while ($data = mysqli_fetch_array($hasil)): ?>
                    <tr class="isi7">
                        <td><?= $data['nm_poli'] ?></td>
                        <td><?= $data['nm_pasien'] ?></td>
                        <td><?= ($data['is_racikan'] == '1') ? 'Racikan' : 'Non Racikan' ?></td>
                        <td><?= $data['nm_dokter'] ?></td>
                        <td><?= $data['jam_validasi'] ?></td>
                        <td><?= $data['jam_penyerahan'] ?></td>
                    </tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>
</div>