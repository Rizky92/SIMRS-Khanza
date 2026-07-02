<?php
    include '../../../conf/conf.php';
?>
<html>
    <head>
        <link href="../../css/default.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
    <?php
        /*
        $keyword = $_GET['keyword'];
        $keyword = validTeks($keyword);
        $_sql    = "SELECT stts_kerja.stts,stts_kerja.ktg,stts_kerja.indek,stts_kerja.hakcuti FROM stts_kerja ".(!empty($keyword)?"where stts_kerja.stts like '%".$keyword."%' or stts_kerja.ktg like '%".$keyword."%'":"")." ORDER BY stts_kerja.indek desc";
        $hasil   = bukaquery($_sql);
        $no      = 1;
        if(mysqli_num_rows($hasil)!=0) {
            echo "<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                    <caption><h3><font color='999999'>Laporan Master Status Kerja</font></h3></caption>
                    <tr class='head'>
                        <td width='10%'><div align='center'>No.</strong></div></td>
                        <td width='28%'><div align='center'>Status</div></td>
                        <td width='40%'><div align='center'>Keterangan</div></td>
                        <td width='10%'><div align='center'>Index Status</div></td>
                        <td width='10%'><div align='center'>Hak Cuti</div></td>
                    </tr>";
            while($baris = mysqli_fetch_array($hasil)) {
                echo "<tr class='isi'>
                        <td>$no</td>
                        <td>$baris[0]</td>
                        <td>$baris[1]</td>
                        <td align='center'>$baris[2]</td>
                        <td align='center'>$baris[3]</td>
                     </tr>";
                $no++;
            }
            echo "</table>";
        }else{
            echo "<table width='100%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                    <caption><h3><font color='999999'>Laporan Master Status Kerja</font></h3></caption>
                    <tr class='head'>
                        <td width='10%'><div align='center'>No.</strong></div></td>
                        <td width='28%'><div align='center'>Status</div></td>
                        <td width='40%'><div align='center'>Keterangan</div></td>
                        <td width='10%'><div align='center'>Index Status</div></td>
                        <td width='10%'><div align='center'>Hak Cuti</div></td>
                    </tr>
                  </table>";
        }
        */
    $keyword = isset($_GET['keyword']) ? $_GET['keyword'] : null;
    $keyword = validTeks($keyword);
    $_sql    = 'select stts_kerja.stts, stts_kerja.ktg, stts_kerja.indek, stts_kerja.hakcuti, stts_kerja.cuti_besar, stts_kerja.hakcuti_besar from stts_kerja';
    if (!empty($keyword)) {
        $_sql .= sprintf(" where stts_kerja.stts like '%s' or stts_kerja.ktg like '%s' or stts_kerja.cuti_normatif like '%s'", $keyword, $keyword, $keyword);
    }
    $hasil = bukaquery($_sql);
    $no = 1;
    ?>
        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
            <caption>
                <h3 style="color: 999999">
                    Laporan Master Status Kerja
                </h3>
            <caption>
            <tr>
                <td width="8%" align="center">Proses</td>
                <td width="19%" align="center">Status</td>
                <td width="29%" align="center">Keterangan</td>
                <td width="8%" align="center">Index</td>
                <td width="8%" align="center">Hak Cuti Tahunan</td>
                <td width="20%" align="center">Cuti Besar</td>
                <td width="8%" align="center">Hak Cuti Besar</td>
            </tr>
            <?php if (mysqli_num_rows($hasil) !== 0): ?>
                <?php while ($baris = mysqli_fetch_array($hasil)): ?>
                    <tr class="isi">
                        <td>
                            <center>
                                <a href="?act=InputSttskerja&action=UBAH&stts=<?=  str_replace(' ', '_', $baris[0]) ?>"><span>[edit]</span>
                                <span>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
                                <a href="?act=ListSttskerja&action=HAPUS&stts=<?=  str_replace(' ', '_', $baris[0]) ?>"><span>[hapus]</span>
                            </center>
                        </td>
                        <td><?= $baris[0] ?></td>
                        <td><?= $baris[1] ?></td>
                        <td><?= $baris[2] ?></td>
                        <td><?= $baris[3] ?></td>
                        <td><?= $baris[4] ?></td>
                        <td><?= $baris[5] ?></td>
                    </tr>
                <?php endwhile; ?>
            <?php endif; ?>
        </table>
    </body>
</html>
