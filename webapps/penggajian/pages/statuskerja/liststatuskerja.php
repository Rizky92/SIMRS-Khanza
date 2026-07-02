<div id="post">
    <div class="entry">
        <div align="center" class="link">
            <a href=?act=InputSttskerja&action=TAMBAH>| Input Data |</a>
            <a href=?act=ListSttskerja>| List Data |</a>
            <a href=?act=HomeAdmin>| Menu Utama |</a>
        </div>
        <form name="frm_aturadmin" onsubmit="return validasiIsi();" method="post" action="" enctype=multipart/form-data>
            <?php
                $action  = isset($_GET['action'])?$_GET['action']:NULL;
                $keyword = trim(isset($_POST['keyword']))?trim($_POST['keyword']):NULL;
                $keyword = validTeks($keyword);
            ?>
            <input type="hidden" name="keyword" value="<?= $keyword ?>">
            <input type="hidden" name="action" value="<?= $action ?>">
            <table width="100%" align="center">
                <tr class="head">
                    <td width="25%" >Keyword</td><td width="">:</td>
                    <td width="82%">
                        <input name="keyword" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" type="text" id="TxtIsi1" value="<?= $keyword ?>" size="65" maxlength="50" pattern="[a-zA-Z 0-9-]{1,50}" title=" a-z A-Z 0-9 (Maksimal 50 karakter)" autocomplete="off" autofocus />
                        <button type="submit" class="button"><span>&nbsp;&nbsp;Cari&nbsp;&nbsp;</span></button>
                    </td>
                </tr>
            </table><br>
        </form>
        <div style="width: 100%; height: 78%; overflow: auto;">
            <?php
                /*
                $_sql   = "SELECT stts_kerja.stts,stts_kerja.ktg,stts_kerja.indek,stts_kerja.hakcuti FROM stts_kerja ".(!empty($keyword)?"where stts_kerja.stts like '%".$keyword."%' or stts_kerja.ktg like '%".$keyword."%'":"")." ORDER BY stts_kerja.indek desc";
                $hasil  = bukaquery($_sql);
                $jumlah = mysqli_num_rows($hasil);
                if(mysqli_num_rows($hasil)!=0) {
                    echo "<table width='99.6%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                            <tr class='head'>
                                <td width='12%'><div align='center'>Proses</div></td>
                                <td width='23%'><div align='center'>Status</div></td>
                                <td width='35%'><div align='center'>Keterangan</div></td>
                                <td width='10%'><div align='center'>Index Status</div></td>
                                <td width='10%'><div align='center'>Hak Cuti Tahunan</div></td>
                                <td width='10%'><div align='center'>Hak Cuti Normatif</div></td>
                            </tr>";
                            while($baris = mysqli_fetch_array($hasil)) {
                                echo "<tr class='isi'>
                                        <td>
                                            <center>
                                                <a href=?act=InputSttskerja&action=UBAH&stts=".str_replace(" ","_",$baris[0]).">[edit]</a>
                                                <a href=?act=ListSttskerja&action=HAPUS&stts=".str_replace(" ","_",$baris[0]).">[hapus]</a>
                                        </center>
                                        </td>
                                        <td>$baris[0]</td>
                                        <td>$baris[1]</td>
                                        <td align='center'>$baris[2]</td>
                                        <td align='center'>$baris[3]</td>
                                    </tr>";
                            }
                    echo "</table>";
                } else {
                    echo "<table width='99.6%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                            <tr class='head'>
                                <td width='12%'><div align='center'>Proses</div></td>
                                <td width='28%'><div align='center'>Status</div></td>
                                <td width='40%'><div align='center'>Keterangan</div></td>
                                <td width='10%'><div align='center'>Index Status</div></td>
                                <td width='10%'><div align='center'>Hak Cuti</div></td>
                            </tr>
                        </table>";
                }

                $aksi=isset($_GET['action'])?$_GET['action']:NULL;
                if ($aksi=="HAPUS") {
                    try {
                        Hapus(" stts_kerja "," stts ='".validTeks(str_replace("_"," ",$_GET['stts']))."' ","?act=ListSttskerja");
                    } catch(mysqli_sql_exception $e) {
                        echo "<b style='color:red'>Gagal menghapus</b>";
                    }
                }
                */

            $_sql = 'select stts_kerja.stts, stts_kerja.ktg, stts_kerja.indek, stts_kerja.hakcuti, stts_kerja.cuti_besar, stts_kerja.hakcuti_besar from stts_kerja';
            if (!empty($keyword)) {
                $_sql .= sprintf(" where stts_kerja.stts like '%s' or stts_kerja.ktg like '%s'", $keyword, $keyword);
            }
            $hasil  = bukaquery($_sql);
            $jumlah = mysqli_num_rows($hasil);
            if (mysqli_num_rows($hasil) != 0):
            ?>
            <table width="99%" border="0" align="center" cellpading="0" cellspacing="0" class="tbl_form">
                <tr class="head">
                    <td width="8%" align="center">Proses</td>
                    <td width="19%" align="center">Status</td>
                    <td width="29%" align="center">Keterangan</td>
                    <td width="8%" align="center">Index</td>
                    <td width="8%" align="center">Hak Cuti Tahunan</td>
                    <td width="20%" align="center">Cuti Besar</td>
                    <td width="8%" align="center">Hak Cuti Besar</td>
                </tr>
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
            </table>
            <?php endif; ?>

            <?php
                $aksi = isset($_GET['action']) ? $_GET['action'] : null;
                if ($aksi === 'HAPUS') {
                    try {
                        Hapus(' stts_kerja ', sprintf(" stts = '%s'", validTeks(str_replace('_', ' ', $_GET['stts']))), '?act=ListSttskerja');
                    } catch (mysqli_sql_exception $e) {
                        echo <<<'HTML'
                            <b style="color: red">Gagal menghapus..!!</b>
                            HTML;
                    }
                }
            ?>
        </div>
        <table width="99%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
            <tr class="head">
                <td align="left">
                    Data : <?= $jumlah ?>
                    <span>&nbsp;|&nbsp;</span>
                    <a target="_blank" href="../penggajian/pages/statuskerja/LaporanStatusKerja.php?keyword=<?= $keyword ?>">Laporan</a>
                    <span>&nbsp;|&nbsp;</span>
                    <a target="_blank" href="../penggajian/pages/statuskerja/LaporanStatusKerjaExcel.php?keyword=<?= $keyword ?>">Excel</a>
                </td>
            </tr>
        </table>
    </div>
</div>
