<?php
require_once('../../conf/conf.php');

$no_rkm_medis = validTeks4($_POST["no_rkm_medis"], 15);

if(file_exists(host(). '/webapps/fotokelahiranbayismc/pages/upload/'.$no_rkm_medis.'.jpeg')){
    @unlink(host().'/webapps/fotokelahiranbayismc/pages/upload/'.$no_rkm_medis.'.jpeg');
}

$img            = $_POST['image'];
$folderPath     = 'upload/';
$image_parts    = explode(';base64,', $img);
$image_type_aux = explode('image/', $image_parts[0]);
$image_type     = $image_type_aux[1];
$image_base64   = base64_decode($image_parts[1]);
$fileName       = $no_rkm_medis.'.jpeg';
$file           = $folderPath.$fileName;

file_put_contents($file, $image_base64);

bukainput(sprintf("insert ignore into pasien_bayi_gambar_smc values ('%s', 'pages/upload/%s')", $no_rkm_medis, $fileName));

?>
<!DOCTYPE html>
<html>
<head>
    <title>SIMKES Khanza</title>
    <link rel="stylesheet" href="../css/bootstrap.min.css" />
</head>
<body>
    <center>
        Proses Pengambilan Foto Bayi Telah Selesai ..!!
        <br />
        <a href="../index.php" class="btn btn-secondary">Kembali</a>
    </center>
</body>
</html>
