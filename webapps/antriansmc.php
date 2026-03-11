<?php
session_start();
require_once('conf/conf.php');
header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
header("Cache-Control: no-store, no-cache, must-revalidate");
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache"); // HTTP/1.0
$tanggal = mktime(date("m"), date("d"), date("Y"));
date_default_timezone_set('Asia/Makassar');
$jam = date("H:i");
?>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="css/default.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="conf/validator.js"></script>
    <title>Jadwal Praktek Dokter</title>
    <script src="Scripts/AC_RunActiveContent.js" type="text/javascript"></script>
    <script src="Scripts/AC_ActiveX.js" type="text/javascript"></script>
    <style type="text/css">
        /*
        body {
            background-image: url();
            background-repeat: no-repeat;
            background-color: #FFFFCC;
        }
        */
    </style>
</head>

<body>
    <div align="left">
        <script type="text/javascript">
            AC_AX_RunContent('width', '32', 'height', '32'); //end AC code
        </script>
        <noscript>
            <object width="32" height="32">
                <embed width="32" height="32"></embed>
            </object>
        </noscript>
        <?php
        $token      = trim(isset($_GET['iyem'])) ? trim($_GET['iyem']) : null;
        $token      = json_decode(encrypt_decrypt($token, "d"), true);
        $kd_poli    = "";
        $kd_dokter  = "";
        if (isset($token["kd_poli"])) {
            $kd_poli    = $token["kd_poli"];
            $kd_dokter  = $token["kd_dokter"];
        } else {
            exit(header("Location: https://www.google.com"));
        }

        $kd_poli    = validTeks4($kd_poli, 20);
        $kd_dokter  = validTeks4($kd_dokter, 20);
        $setting    = mysqli_fetch_array(bukaquery("select setting.nama_instansi, setting.alamat_instansi, setting.kabupaten, setting.propinsi, setting.kontak, setting.email, setting.logo from setting"));
        ?>
        <table width="100%" align="center" border="0" class="tbl_form" cellspacing="0" cellpadding="0">
            <tr>
                <td width="10%" align="right" valign="center">
                    <img width="90" height="90" src="data:image/jpeg;base64,<?= base64_encode($setting['logo']) ?>" />
                </td>
                <td>
                    <center>
                        <font size="6" color="#aa00aa" face="Tahoma"><?= $setting['nama_instansi'] ?></font><br>
                        <font size="5" color="#aa00aa" face="Tahoma"><?= $setting['alamat_instansi'] . ', ' . $setting['kabupaten'] . ', ' . $setting['propinsi'] ?></font><br>
                        <font size="5" color="#aa00aa" face="Tahoma"> Antrina Poli <?= getOne("select nm_poli from poliklinik where kd_poli = '{$kd_poli}'") ?>, Dokter <?= getOne("select nm_dokter from dokter where kd_dokter = '{$kd_dokter}'") ?>
                            <br>
                            <?= date('d-M-Y', $tanggal) . ' ' . $jam ?>
                        </font>
                    </center>
                </td>
                <td width="10%" align="left"> &nbsp; </td>
                <td width="10%" align="left" valign="top">
                    <img width="180" height="130" src="header-kanan.jpg" />
                </td>
            </tr>
        </table>
        <table width="100%" bgcolor="FFFFFF" border="0" align="center" cellpadding="0" cellspacing="0">
            <tr class="head5">
                <td width="100%">
                    <div align="center"></div>
                </td>
            </tr>
        </table>
        <table border="0" witdh="100%" cellpadding="0" cellspacing="0">
            <tr class="head2" border="0">
                <td width="35%" align="center"><font size="6" color="#DD0000"><b>Panggilan Poli</b></font></td>
                <td><font size="6" color="#DD0000"><b>:</b></font></td>
                <td width="64%" align="center">
                    <?php $hasil = bukaquery("select * from antripoli where antripoli.kd_poli = '{$kd_poli}' and antripoli.kd_dokter = '{$kd_dokter}'"); ?>
                    <?php while ($data = mysqli_fetch_array($hasil)) : ?>
                        <div style="font-size: 32px; color: #DD0000; font-weight: 700; padding-top: 1rem; padding-bottom: 1rem">
                            <?= getOne("select concat(reg_periksa.no_reg, ' ', reg_periksa.no_rawat, ' ', pasien.nm_pasien) from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where reg_periksa.no_rawat = '{$data['no_rawat']}'"); ?>
                            <?php if ($data['status'] == "1") : ?>
                                <script>
                                    const synth = window.speechSynthesis;
                                    let voices = [];

                                    synth.addEventListener("voiceschanged", () => {
                                        voices = synth.getVoices().sort(function (a, b) {
                                            const aname = a.name.toUpperCase();
                                            const bname = b.name.toUpperCase();

                                            if (aname < bname) {
                                                return -1;
                                            } else if (aname == bname) {
                                                return 0;
                                            } else {
                                                return +1;
                                            }
                                        });

                                        let suaraDipilih = null;
                                        for (let i = 0; i < voices.length; i++) {
                                            if (voices[i].name.includes("Google")) {
                                                if (voices[i].lang.includes("id-ID")) {
                                                    suaraDipilih = voices[i];
                                                }
                                            } else if (voices[i].name.includes("Microsoft")) {
                                                if (voices[i].name.toLowerCase().includes("gadis") && voices[i].lang.includes("id-ID")) {
                                                    suaraDipilih = voices[i];
                                                }
                                            }
                                        }

                                        let noAntrian = '<?= strtolower(getOne("select concat(reg_periksa.no_reg, ', ', pasien.nm_pasien) from reg_periksa join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis where reg_periksa.no_rawat = '{$data['no_rawat']}'")); ?>'
                                        let poli = '<?= strtolower(getOne("select nm_poli from poliklinik where kd_poli = '{$kd_poli}'")); ?>'
                                        let textAntrian = `Nomor antrian ${noAntrian}, ${poli}`;

                                        if (synth.speaking) {
                                            console.error("speechSynthesis.speaking");
                                            return;
                                        }

                                        const utterThis = new SpeechSynthesisUtterance(textAntrian);

                                        utterThis.onend = function (event) {
                                            console.log("SpeechSynthesisUtterance.onend");
                                        };

                                        utterThis.onerror = function (event) {
                                            console.error("SpeechSynthesisUtterance.onerror");
                                        };

                                        utterThis.voice = suaraDipilih;
                                        utterThis.pitch = 1.0;
                                        utterThis.rate = 0.9;
                                        console.log(utterThis);
                                        synth.speak(utterThis);
                                    });
                                </script>
                                <?php bukaquery2("update antripoli set antripoli.status = '0' where antripoli.kd_poli = '{$kd_poli}' and antripoli.kd_dokter = '{$kd_dokter}'"); ?>
                            <?php endif; ?>
                        </div>
                    <?php endwhile; ?>
                </td>
            </tr>
        </table>
        <table width="100%" bgcolor="FFFFFF" border="0" align="center" cellpadding="0" cellspacing="0">
            <tr class="head4">
                <td width="10%">
                    <div align="center">
                        <font size="5"><b>NO</b></font>
                    </div>
                </td>
                <td width="25%">
                    <div align="center">
                        <font size="5"><b>NO.RAWAT</b></font>
                    </div>
                </td>
                <td width="65%">
                    <div align="center">
                        <font size="5"><b>NAMA PASIEN</b></font>
                    </div>
                </td>
            </tr>
            <?php
            $_sql = "select reg_periksa.no_reg,reg_periksa.no_rawat,pasien.nm_pasien
                     from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis
                     where reg_periksa.kd_poli = '{$kd_poli}' and reg_periksa.kd_dokter = '{$kd_dokter}'
                     and reg_periksa.tgl_registrasi = '" . date("Y-m-d", $tanggal) . "' and stts = 'Belum' order by reg_periksa.no_reg";
            $hasil = bukaquery($_sql);
            ?>
            <?php while ($data = mysqli_fetch_array($hasil)): ?>
                <tr class="isi7">
                    <td align="center"><font size="5" color="#000000" face="Tahoma"><?= $data['no_reg'] ?></font></td>
                    <td align="center"><font size="5" color="#000000" face="Tahoma"><?= $data['no_rawat'] ?></font></td>
                    <td align="center"><font size="5" color="#000000" face="Tahoma"><?= $data['nm_pasien'] ?></font></td>
                </tr>
            <?php endwhile; ?>
        </table>
        <table width="100%" bgcolor="FFFFFF" border="0" align="center" cellpadding="0" cellspacing="0">
            <tr class="head5">
                <td width="100%">
                    <div align="center"></div>
                </td>
            </tr>
        </table>
        <img src="ft-2.jpg" alt="bar-pic" width="100%" height="83">
    </div>
</body>
<meta http-equiv="refresh" content="20;URL=?iyem=<?= encrypt_decrypt(json_encode(['kd_poli' => $kd_poli, 'kd_dokter' => $kd_dokter]), 'e') ?>">