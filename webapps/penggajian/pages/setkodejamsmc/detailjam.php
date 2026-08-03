<?php
if (strpos($_SERVER['REQUEST_URI'], "pages")) {
    exit(header("Location:../index.php"));
}

$shiftLegacy = [];
foreach (['Pagi', 'Siang', 'Malam', 'Midle Pagi', 'Midle Siang', 'Midle Malam'] as $kelompok) {
    for ($urutan = 1; $urutan <= 40; $urutan++) {
        if (strpos($kelompok, 'Midle') === 0) {
            $shiftLegacy[] = $kelompok . $urutan;
        } else {
            $shiftLegacy[] = 1 === $urutan ? $kelompok : $kelompok . $urutan;
        }
    }
}
?>

<div id="post">
    <div class="entry">
        <div align="center" class="link">
            <a href=?act=HomeAdmin>| Menu Utama |</a>
        </div>
        <form name="frm_aturadmin" onsubmit="return validasiIsi();" method="post" enctype="application/x-www-form-urlencoded">
            <?php
            $action = isset($_GET['action']) ? $_GET['action'] : null;
            $kode   = validTeks(isset($_GET['kode_shift']) ? $_GET['kode_shift'] : null);
            ?>
            <input type="hidden" name="action" value="<?= $action ?>">

            <table width="100%" align="center">
                <tr class="head">
                    <td width="25%">Kode Shift</td>
                    <td>:</td>
                    <td width="75%">
                        <select name="kode_shift" class="text2" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" id="TxtIsi1" required autofocus>
                            <?php $daftar = bukaquery('select kode_shift, nama_shift from jam_masuk_smc order by kode_shift'); ?>
                            <?php while ($baris = mysqli_fetch_array($daftar)): ?>
                                <option value="<?= $baris[0] ?>"><?= $baris[0] ?> - <?= $baris[1] ?></option>
                            <?php endwhile; ?>
                        </select>
                        <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="25%">Jam Jaga Shift</td>
                    <td>:</td>
                    <td width="75%">
                        <select name="shift" class="text2" onkeydown="setDefault(this, document.getElementById('MsgIsi2'));" id="TxtIsi2">
                            <?php foreach ($shiftLegacy as $shift): ?>
                                <option value="<?= $shift ?>"><?= $shift ?></option>
                            <?php endforeach; ?>
                        </select>
                        <span id="MsgIsi2" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
            </table>
            <div align="center">
                <button type="submit" name="BtnSimpan" class="button"><span>&nbsp;&nbsp;SIMPAN&nbsp;&nbsp;</span></button>
                <button type="reset" class="button"><span>KOSONG</span></button>
            </div><br>
            <?php
            switch ($action) {
                case 'TAMBAH':
                    $BtnSimpan = isset($_POST['BtnSimpan']) ? $_POST['BtnSimpan'] : null;
                    if (isset($BtnSimpan)) {
                        $shift      = validTeks(trim($_POST['shift']));
                        $kode_shift = validTeks(trim($_POST['kode_shift']));
                        if ($shift !== '' && $kode_shift !== '') {
                            try {
                                bukaquery2(sprintf(
                                    "insert into set_kode_shift_smc (shift, kode_shift) values ('%s', '%s') on duplicate key update shift = values(shift)",
                                    $shift,
                                    $kode_shift
                                ));
                                echo <<<HTML
                                    <meta http-equiv="refresh" content="1;URL=?act=ListKodeShiftSmc&action=TAMBAH">
                                    HTML;
                            } catch (mysqli_sql_exception $e) {
                                if ($e->getCode() == 1062) {
                                    echo "<b style='color:red'>Jam jaga shift sudah dipakai kode shift lain..!!!</b>";
                                } else {
                                    echo "<b style='color:red'>Gagal menyimpan</b>";
                                }
                            }
                        } else {
                            echo 'Semua field harus isi..!!!';
                        }
                    }
                    break;
                case 'HAPUS':
                    try {
                        bukaquery2(sprintf("delete from set_kode_shift_smc where kode_shift = '%s'", $kode));
                    } catch (mysqli_sql_exception $e) {
                        echo "<b style='color:red'>Gagal menghapus</b>";
                    }
                    break;
            }
            ?>
            <div style="width: 100%; height: 57%; overflow: auto">
                <table width="99%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                    <?php
                    $hasil  = bukaquery(
                        'select set_kode_shift_smc.kode_shift, ifnull(jam_masuk_smc.nama_shift, \'\') as nama_shift, set_kode_shift_smc.shift ' .
                        'from set_kode_shift_smc left join jam_masuk_smc on set_kode_shift_smc.kode_shift = jam_masuk_smc.kode_shift ' .
                        'order by set_kode_shift_smc.kode_shift'
                    );
                    $jumlah = mysqli_num_rows($hasil);
                    ?>

                    <?php if (mysqli_num_rows($hasil) !== 0): ?>
                        <tr class="head">
                            <td width="5%" align="center" valign="center">Aksi</td>
                            <td width="15%" align="center" valign="center">Kode Shift</td>
                            <td width="45%" align="center" valign="center">Nama Shift</td>
                            <td width="35%" align="center" valign="center">Jam Jaga Shift</td>
                        </tr>
                        <?php while ($baris = mysqli_fetch_array($hasil)): ?>
                            <tr class="isi">
                                <td width="5%" align="center">
                                    <a href="?act=ListKodeShiftSmc&action=HAPUS&kode_shift=<?= urlencode($baris[0]) ?>"><span>[hapus]</span></a>
                                </td>
                                <td width="15%"><?= $baris[0] ?></td>
                                <td width="45%"><?= $baris[1] ?></td>
                                <td width="35%"><?= $baris[2] ?></td>
                            </tr>
                        <?php endwhile; ?>
                    <?php endif; ?>
                </table>
                <table width="99%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                    <tr class="head">
                        <td align="left">
                            <Data : <?= $jumlah ?>
                        </td>
                    </tr>
                </table>
            </div>
        </form>
    </div>
</div>
