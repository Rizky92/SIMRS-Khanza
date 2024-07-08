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

$_sql = "select poliklinik.nm_poli, pasien.nm_pasien, resep_obat.jam as jam_validasi,
         exists(select * from resep_dokter_racikan where resep_dokter_racikan.no_resep = resep_obat.no_resep) as is_racikan,
         dokter.nm_dokter from resep_obat
         join reg_periksa on resep_obat.no_rawat = reg_periksa.no_rawat
         join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis
         join dokter on resep_obat.kd_dokter = dokter.kd_dokter
         join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli
         where resep_obat.tgl_peresepan = current_date()
         and resep_obat.jam != '00:00:00'
         and resep_obat.jam_peresepan != '00:00:00'
         and resep_obat.jam_penyerahan = '00:00:00'
         and resep_obat.jam between current_time() - interval 120 minute and current_time()
         and resep_obat.status = 'ralan'
         and reg_periksa.kd_poli != 'IGDK'
         order by resep_obat.jam desc";

$hasil = bukaquery($_sql);
?>

<table class="default">
    <thead>
        <tr class="head4">
            <td style="padding-top: 6px; padding-bottom: 6px"><b>Pasien</b></td>
            <td style="padding-top: 6px; padding-bottom: 6px"><b>Jenis Resep</b></td>
            <td style="padding-top: 6px; padding-bottom: 6px"><b>Mulai Pengerjaan</b></td>
        </tr>
    </thead>
    <tbody>
        <?php while ($data = mysqli_fetch_array($hasil)): ?>
            <tr class="isi7">
                <td style="padding-top: 2px; padding-bottom: 12px">
                    <span style="font-weight: 600"><?= $data['nm_pasien'] ?></span> (<span style="color: #1f2937"><?= $data['nm_poli'] ?></span>) <br>
                    <span style="font-size: 11pt; color: #374151">Dokter Peresep : <?= $data['nm_dokter'] ?></span>
                </td>
                <td><?= ($data['is_racikan'] == '1') ? 'Racikan' : 'Non Racikan' ?></td>
                <td><?= $data['jam_validasi'] ?></td>
            </tr>
        <?php endwhile; ?>
    </tbody>
</table>