<?php
    require_once("../../conf/conf.php");
    session_start();
    header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); 
    header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT"); 
    header("Cache-Control: no-store, no-cache, must-revalidate"); 
    header("Cache-Control: post-check=0, pre-check=0", false);
    header("Pragma: no-cache");
?>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link href="../css/default.css" rel="stylesheet" type="text/css" />
    </head>
    <body bgcolor="#ffffff" style="max-width: 100vw; margin-right: 1.25rem">
    <?php
        $norawat = isset($_GET['iyem']) ? trim($_GET['iyem']) : null;
        $norawat = encrypt_decrypt($norawat, 'd');
    ?>
    <?php if (isset($norawat) && ($_SESSION['ses_admin_berkas_rawat'] ?? null) === 'billing'): ?>
        <?php
            $no_rawat = validTeks4($norawat, 20);
            $_sql = "select billing.no,billing.nm_perawatan,billing.pemisah,if(billing.biaya=0,'',billing.biaya),if(billing.jumlah=0,'',billing.jumlah),if(billing.tambahan=0,'',billing.tambahan),if(billing.totalbiaya=0,'',billing.totalbiaya),billing.totalbiaya from billing where billing.no_rawat='$no_rawat' ";
            $hasil = bukaquery($_sql);
            $setting = mysqli_fetch_array(bukaquery("select setting.nama_instansi,setting.alamat_instansi,setting.kabupaten,setting.propinsi,setting.kontak,setting.email,setting.logo from setting"));
        ?>
        <?php if (mysqli_num_rows($hasil) !== 0): ?>
            <table width="<?= getOne("select nota1ranap from set_nota") ?>" bgcolor="#ffffff" align="left" border="0" padding="0" cellspacing="0" cellpadding="0">
                <tr class="isi12" padding="0">
                    <td colspan="7" padding="0">
                        <table width="100%" bgcolor="#ffffff" align="left" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="20%"><img width="45" height="45" src="data:image/jpeg;base64,<?= base64_encode($setting['logo']) ?>"/></td>
                                <td>
                                    <center>
                                        <font color="000000" size="3"  face="Tahoma"> <?= $setting['nama_instansi'] ?></font><br>
                                        <font color="000000" size="1" face="Tahoma">
                                            <?= $setting['alamat_instansi']?>, <?= $setting['kabupaten']?>, <?= $setting['propinsi'] ?><br />
                                            <?= $setting['kontak'] ?>, E-Mail : <?= $setting['email'] ?>
                                        </font>
                                    </center>
                                </td>
                                <td width="20%">&nbsp;</td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" padding="0">
                        <hr />
                        <center><font color="333333" size="1" face="Tahoma">BILLING</font></center>
                    </td>
                </tr>
                <?php $total = 0; $row = 0; ?>
                <?php while($inapdrpasien = mysqli_fetch_array($hasil)): ?>
                    <?php $total += $inapdrpasien[7]; ?>
                    <?php if ($row++ < 6): ?>
                        <tr class="isi12" padding="0">
                            <td padding="0" width="30%"><font color="000000" size="1" face="Tahoma"><?= str_replace('  ', '&nbsp;&nbsp;', $inapdrpasien[0]) ?></font></td>
                            <td padding="0" width="40%" colspan="6"><font color="000000" size="1" face="Tahoma"><?= $inapdrpasien[1] ?></font></td>
                        </tr>
                    <?php else: ?>
                        <?php if (empty($inapdrpasien[6]) && empty($inapdrpasien[0])): ?>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="18%"><font color="111111" size="1" face="Tahoma"><?= str_replace('   ', '&nbsp;&nbsp;', $inapdrpasien[0]) ?></font></td>
                                <td padding="0" width="40%" colspan="6" align="right"><font color="111111" size="1" face="Tahoma"><?= $inapdrpasien[1] ?></font></td>
                            </tr>
                        <?php else: ?>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="18%"><font color="111111" size="1" face="Tahoma"><?= str_replace('   ', '&nbsp;&nbsp;', $inapdrpasien[0]) ?></font></td>
                                <td padding="0" width="40%"><font color="111111" size="1" face="Tahoma"><?= str_replace('  ', '&nbsp;&nbsp;', $inapdrpasien[1]) ?></font></td>
                                <td padding="0" width="2%"><font color="111111" size="1" face="Tahoma"><?= $inapdrpasien[2] ?></font></td>
                                <td padding="0" width="10%" align="right"><font color="111111" size="1" face="Tahoma"><?= formatDuitSmc($inapdrpasien[3]) ?></font></td>
                                <td padding="0" width="5%" align="right"><font color="111111" size="1" face="Tahoma"><?= formatDuitSmc($inapdrpasien[4]) ?></font></td>
                                <td padding="0" width="10%" align="right"><font color="111111" size="1" face="Tahoma"><?= formatDuitSmc($inapdrpasien[5]) ?></font></td>
                                <td padding="0" width="15%" align="right"><font color="111111" size="1" face="Tahoma"><?= formatDuitSmc($inapdrpasien[6]) ?></font></td>
                            </tr>
                        <?php endif; ?>
                    <?php endif; ?>
                <?php endwhile; ?>
                <tr class="isi12" padding="0">
                    <td padding="0" width="18%"><font color="111111" size="1" face="Tahoma"><b>TOTAL BIAYA</b></font></td>
                    <td padding="0" width="40%"><font color="111111" size="1" face="Tahoma"><b>:</b></font></td>
                    <td padding="0" width="2%"><font color="111111" size="1" face="Tahoma"></font></td>
                    <td padding="0" width="10%" align="right"><font color="111111" size="1" face="Tahoma"></font></td>
                    <td padding="0" width="5%" align="right"><font color="111111" size="1" face="Tahoma"></font></td>
                    <td padding="0" width="10%" align="right"><font color="111111" size="1" face="Tahoma"></font></td>
                    <td padding="0" width="15%" align="right"><font color="111111" size="1" face="Tahoma"><b><?= formatDuitSmc($total) ?></b></font></td>
                </tr>
                <tr class="isi12" padding="0">
                    <td colspan="7" padding="0">
                        <table width="100%" bgcolor="#ffffff" align="left" border="0" padding="0" cellspacing="0" cellpadding="0">
                            <tr class="isi12" padding="0">
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="20%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                            </tr>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma">Keluarga Pasien</font></td>
                                <td padding="0" width="20%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma"><?= getOne("select setting.kabupaten from setting") ?></font></td>
                            </tr>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma"></font></td>
                                <td padding="0" width="20%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma">Kasir</font></td>
                            </tr>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="40%"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="20%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="40%" align="right"><font color="000000" size="1" face="Tahoma"></font></td>
                            </tr>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="40%"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="20%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="40%" align="right"><font color="000000" size="1" face="Tahoma"></font></td>
                            </tr>
                            <tr class="isi12" padding="0">
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma">( ............................. )</font></td>
                                <td padding="0" width="20%" align="center"><font color="000000" size="1" face="Tahoma">&nbsp;</font></td>
                                <td padding="0" width="40%" align="center"><font color="000000" size="1" face="Tahoma">( ............................. )</font></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr class="isi12" padding="0">
                    <td colspan="7" padding="0">&nbsp</td>
                </tr>
            </table>
        <?php else: ?>
            <font color="333333" size="1" face="Times New Roman"><b>Data Billing masih kosong !</b></font>
        <?php endif; ?>
    <?php endif; ?>
    </body>
</html>
