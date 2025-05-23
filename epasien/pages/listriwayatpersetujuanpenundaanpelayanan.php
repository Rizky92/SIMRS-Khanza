<?php
    if(strpos($_SERVER['REQUEST_URI'],"pages")){
        exit(header("Location:../index.php"));
    }
?>
<div class="block-header">
    <h2><center>RIWAYAT PERSETUJUAN PENUNDAAN PELAYANAN</center></h2>
</div>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        <div class="card">
            <div class="body">
                <div class="table-responsive">
                    <table class="table table-bordered table-striped table-hover js-basic-example dataTable">
                        <thead>
                            <tr>
                                <th width="10%"><center>Tanggal</center></th>
                                <th width="17%"><center>No.Persetujuan</center></th>
                                <th width="17%"><center>Nomor KTP P.J.</center></th>
                                <th width="30%"><center>P.J. Pasien</center></th>
                                <th width="16%"><center>No.Telp P.J.</center></th>
                                <th width="10%"><center>Detail</center></th>
                            </tr>
                        </thead>
                        <tbody>
                        <?php 
                            $queryperiksa = bukaquery(
                                "select persetujuan_penundaan_pelayanan.no_surat,date_format(persetujuan_penundaan_pelayanan.tanggal,'%d/%m/%Y') as tanggalperiksa,persetujuan_penundaan_pelayanan.nama_pj,persetujuan_penundaan_pelayanan.no_ktppj,persetujuan_penundaan_pelayanan.no_telppj,".
                                "ifnull(bukti_persetujuan_penundaan_pelayanan.photo,'') as photo from persetujuan_penundaan_pelayanan inner join reg_periksa on persetujuan_penundaan_pelayanan.no_rawat=reg_periksa.no_rawat ".
                                "left join bukti_persetujuan_penundaan_pelayanan on persetujuan_penundaan_pelayanan.no_surat=bukti_persetujuan_penundaan_pelayanan.no_surat where reg_periksa.no_rkm_medis='".cleankar(encrypt_decrypt($_SESSION["ses_pasien"],"d"))."' ".
                                "order by persetujuan_penundaan_pelayanan.tanggal desc"
                            );
                            while($rsqueryperiksa = mysqli_fetch_array($queryperiksa)) {
                               echo "<tr>
                                        <td align='center' valign='middle'>".$rsqueryperiksa["tanggalperiksa"]."</td>
                                        <td align='center' valign='middle'>".$rsqueryperiksa["no_surat"]."</td>
                                        <td align='center' valign='middle'>".$rsqueryperiksa["no_ktppj"]."</td>
                                        <td align='center' valign='middle'>".$rsqueryperiksa["nama_pj"]."</td>
                                        <td align='center' valign='middle'>".$rsqueryperiksa["no_telppj"]."</td>".
                                        ($rsqueryperiksa["photo"]==""?"<td align='center' valign='middle'><a href='index.php?act=AmbilPersetujuanPenundaanPelayanan&iyem=".encrypt_decrypt("{\"nopersetujuan\":\"".$rsqueryperiksa["no_surat"]."\"}","e")."' class='btn btn-warning waves-effect'>Ambil</a></td>":
                                        "<td align='center' valign='middle'><a href='index.php?act=HasilPersetujuanPenundaanPelayanan&iyem=".encrypt_decrypt("{\"nopersetujuan\":\"".$rsqueryperiksa["no_surat"]."\",\"photo\":\"".$rsqueryperiksa["photo"]."\"}","e")."' class='btn btn-danger waves-effect'>Lihat</a></td>").
                                    "</tr>";
                            }
                        ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>