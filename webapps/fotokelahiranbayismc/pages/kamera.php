<?php

if (strpos($_SERVER['REQUEST_URI'], "pages")) {
    exit(header("Location:../index.php"));
}

$namars       = getOne("select setting.nama_instansi from setting");
$no_rkm_medis = "";

$_sql  = "select * from antrifotokelahiranbayismc" ;
$hasil = bukaquery2($_sql);

while ($data = mysqli_fetch_array($hasil)) {
    $no_rkm_medis = $data['no_rkm_medis'];
}

$nm_pasien = "";
$jk        = "";
$umur      = "";
$tgl_lahir = "";
$alamat    = "";

$_sql2 = <<<SQL
    select pasien.no_rkm_medis, pasien.nm_pasien, if(pasien.jk = 'L', 'LAKI-LAKI', 'PEREMPUAN') as jk, pasien.umur, date_format(pasien.tgl_lahir, '%d-%m-%Y') as
    tgl_lahir, concat(pasien.alamat, ', ', kelurahan.nm_kel, ', ', kecamatan.nm_kec, ', ', kabupaten.nm_kab) as alamat from pasien inner join kelurahan on
    pasien.kd_kel = kelurahan.kd_kel inner join kecamatan on pasien.kd_kec = kecamatan.kd_kec inner join kabupaten on pasien.kd_kab = kabupaten.kd_kab where
    pasien.no_rkm_medis = '{$no_rkm_medis}'
    SQL;

$data2 = mysqli_fetch_array(bukaquery2($_sql2));

$nm_pasien    = $data2['nm_pasien'];
$jk           = $data2['jk'];
$umur         = $data2['umur'];
$tgl_lahir    = $data2['tgl_lahir'];
$alamat       = $data2['alamat'];
?>

<!DOCTYPE html>
<html>
<head>
    <script src="js/jquery.min.js"></script>
    <script src="js/webcam.min.js"></script>
    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <style type="text/css">
        #results {
            padding: 0px;
            background: #EEFFEE;
            width: 480;
            height: 270
        }
    </style>
    <title>SIMKES Khanza</title>
</head>
<body>
    <div class="container">
        <h5 class="text-dark">
            <center>
                <button class="btn btn-secondary" onclick="window.location.reload();">Refresh</button>
                <br />
                <br />
                FOTO BAYI UNTUK SURAT KELAHIRAN BAYI
            </center>
        </h5>
        <br/>
        <form method="POST" action="pages/storeImage.php" onsubmit="return validasiIsi();" enctype="multipart/form-data">
            <input type="hidden" name="no_rkm_medis" value="<?= $no_rkm_medis ?>">
            <table class="default" width="100%" border="0" align="center" cellpadding="3px" cellspacing="0px">
                <tr class="text-dark">
                    <td width="25%">Nama Pasien</td>
                    <td width="75%">: <?=$nm_pasien ?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Nomor Rekam Medis</td>
                    <td width="75%">: <?=$no_rkm_medis ?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Jenis Kelamin</td>
                    <td width="75%">: <?=$jk ?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Tanggal Lahir</td>
                    <td width="75%">: <?=$tgl_lahir ?></td>
                </tr>
                <tr class="text-dark">
                    <td width="25%">Umur</td>
                    <td width="75%">: <?=$umur ?></td>
                </tr>
            </table>
            <br/>
            <h7 class="text-dark">
                Silahkan lakukan pengambilan gambar apabila sudah siap
            </h7>
            <br/>
            <br/>
            <div class="row">
                <div class="col-md-6">
                    <div style="margin-left: auto; margin-right: auto" id="my_camera"></div>
                    <input type="hidden" name="image" class="image-tag" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" id="TxtIsi1">
                </div>
                <div class="col-md-6">
                    <div id="results"><h7 class="text-success"><center>Gambar akan diambil jika anda sudah mengeklik "Ambil"</center></h7></div>
                    <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                </div>
                <br/>
                <div class="col-md-12 text-center mt-3">
                    <input type="button" class="btn btn-warning" value="Ambil" onClick="take_snapshot()">
                    <button class="btn btn-danger">Simpan</button>
                </div>
            </div>
        </form>
    </div>

    <script language="JavaScript">
        Webcam.set({
            width: 480,
            height: 270,
            dest_width: 1280,
            dest_height: 720,
            image_format: 'jpeg',
            jpeg_quality: 90
        });

        Webcam.attach( '#my_camera' );

        function take_snapshot() {
            Webcam.snap(function(data_uri) {
                $(".image-tag").val(data_uri);
                document.getElementById('results').innerHTML = '<img style="display: block; margin-left: auto; margin-right: auto" src="'+data_uri+'" width="480" height="270"/>';
            });
        }
    </script>
</body>
</html>
