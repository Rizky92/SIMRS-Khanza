<?php

require_once('conf/conf.php');

header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT");
header("Cache-Control: no-store, no-cache, must-revalidate");
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache"); // HTTP/1.0

date_default_timezone_set("Asia/Makassar");

$tanggal = mktime(date("m"), date("d"), date("Y"));
$jam = date("H:i");

$_sql = "select * from antriapotek3";
$hasil = bukaquery($_sql);

?>
 
<div class="row" style="position: relative; z-index: 1080">
    <div class="col s12" id="header-instansi">
        <div class="card deep-orange accent-3 white-text">
            <div class="card-content">
                <h5>
                    <table border="0" witdh="100%">
                        <tr border="0">
                            <td width="20%">Panggilan Penyerahan Obat</td><td width="10px">:</td>
                            <td style="text-align: left">
                                <?php while ($data = mysqli_fetch_array($hasil)): ?>
                                    <?= getOne("select pasien.nm_pasien from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where reg_periksa.no_rawat = '{$data['no_rawat']}'") ?>
                                    <?= '('.getOne("select poliklinik.nm_poli from reg_periksa join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli where reg_periksa.no_rawat = '{$data['no_rawat']}'").')' ?>
                                    <?php if ($data['status'] == '1'): ?>
                                        <audio autoplay="true" src="bell2.wav"></audio>
                                        <?php bukaquery2("update antriapotek3 set status = '0"); ?>
                                    <?php endif; ?>
                                <?php endwhile; ?>
                            </td>
                        </tr>
                    </table>
                </h5>
            </div>
        </div>
    </div>
</div>