<?php
if (strpos($_SERVER['REQUEST_URI'], "pages")) {
    exit(header("Location:../index.php"));
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
            $shift  = validTeks(isset($_GET['shift']) ? $_GET['shift'] : null);
            ?>
            <input type="hidden" name="shift" value="<?= $shift ?>">
            <input type="hidden" name="action" value="<?= $action ?>">

            <table width="100%" align="center">
                <tr class="head">
                    <td width="25%">Kode Shift</td>
                    <td>:</td>
                    <td width="75%">
                        <input name="kode_shift" class="text inputbox" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" type="text" id="TxtIsi1" size="10" maxlength="5" pattern="[a-zA-Z 0-9-]{1,5}" title=" a-z A-Z 0-9 (Maksimal 5 karakter)" autocomplete="off" required autofocus />
                        <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="25%">Jam Shift</td>
                    <td>:</td>
                    <td width="75%">
                        <select name="shift" class="text2" onkeydown="setDefault(this, document.getElementById('MsgIsi2'));" id="TxtIsi2">
                            <option id='TxtIsi2' value='Pagi'>Pagi</option>
                            <option id='TxtIsi2' value='Pagi2'>Pagi2</option>
                            <option id='TxtIsi2' value='Pagi3'>Pagi3</option>
                            <option id='TxtIsi2' value='Pagi4'>Pagi4</option>
                            <option id='TxtIsi2' value='Pagi5'>Pagi5</option>
                            <option id='TxtIsi2' value='Pagi6'>Pagi6</option>
                            <option id='TxtIsi2' value='Pagi7'>Pagi7</option>
                            <option id='TxtIsi2' value='Pagi8'>Pagi8</option>
                            <option id='TxtIsi2' value='Pagi9'>Pagi9</option>
                            <option id='TxtIsi2' value='Pagi10'>Pagi10</option>
                            <option id='TxtIsi2' value='Siang'>Siang</option>
                            <option id='TxtIsi2' value='Siang2'>Siang2</option>
                            <option id='TxtIsi2' value='Siang3'>Siang3</option>
                            <option id='TxtIsi2' value='Siang4'>Siang4</option>
                            <option id='TxtIsi2' value='Siang5'>Siang5</option>
                            <option id='TxtIsi2' value='Siang6'>Siang6</option>
                            <option id='TxtIsi2' value='Siang7'>Siang7</option>
                            <option id='TxtIsi2' value='Siang8'>Siang8</option>
                            <option id='TxtIsi2' value='Siang9'>Siang9</option>
                            <option id='TxtIsi2' value='Siang10'>Siang10</option>
                            <option id='TxtIsi2' value='Malam'>Malam</option>
                            <option id='TxtIsi2' value='Malam2'>Malam2</option>
                            <option id='TxtIsi2' value='Malam3'>Malam3</option>
                            <option id='TxtIsi2' value='Malam4'>Malam4</option>
                            <option id='TxtIsi2' value='Malam5'>Malam5</option>
                            <option id='TxtIsi2' value='Malam6'>Malam6</option>
                            <option id='TxtIsi2' value='Malam7'>Malam7</option>
                            <option id='TxtIsi2' value='Malam8'>Malam8</option>
                            <option id='TxtIsi2' value='Malam9'>Malam9</option>
                            <option id='TxtIsi2' value='Malam10'>Malam10</option>
                            <option id='TxtIsi2' value='Midle Pagi1'>Midle Pagi1</option>
                            <option id='TxtIsi2' value='Midle Pagi2'>Midle Pagi2</option>
                            <option id='TxtIsi2' value='Midle Pagi3'>Midle Pagi3</option>
                            <option id='TxtIsi2' value='Midle Pagi4'>Midle Pagi4</option>
                            <option id='TxtIsi2' value='Midle Pagi5'>Midle Pagi5</option>
                            <option id='TxtIsi2' value='Midle Pagi6'>Midle Pagi6</option>
                            <option id='TxtIsi2' value='Midle Pagi7'>Midle Pagi7</option>
                            <option id='TxtIsi2' value='Midle Pagi8'>Midle Pagi8</option>
                            <option id='TxtIsi2' value='Midle Pagi9'>Midle Pagi9</option>
                            <option id='TxtIsi2' value='Midle Pagi10'>Midle Pagi10</option>
                            <option id='TxtIsi2' value='Midle Siang1'>Midle Siang1</option>
                            <option id='TxtIsi2' value='Midle Siang2'>Midle Siang2</option>
                            <option id='TxtIsi2' value='Midle Siang3'>Midle Siang3</option>
                            <option id='TxtIsi2' value='Midle Siang4'>Midle Siang4</option>
                            <option id='TxtIsi2' value='Midle Siang5'>Midle Siang5</option>
                            <option id='TxtIsi2' value='Midle Siang6'>Midle Siang6</option>
                            <option id='TxtIsi2' value='Midle Siang7'>Midle Siang7</option>
                            <option id='TxtIsi2' value='Midle Siang8'>Midle Siang8</option>
                            <option id='TxtIsi2' value='Midle Siang9'>Midle Siang9</option>
                            <option id='TxtIsi2' value='Midle Siang10'>Midle Siang10</option>
                            <option id='TxtIsi2' value='Midle Malam1'>Midle Malam1</option>
                            <option id='TxtIsi2' value='Midle Malam2'>Midle Malam2</option>
                            <option id='TxtIsi2' value='Midle Malam3'>Midle Malam3</option>
                            <option id='TxtIsi2' value='Midle Malam4'>Midle Malam4</option>
                            <option id='TxtIsi2' value='Midle Malam5'>Midle Malam5</option>
                            <option id='TxtIsi2' value='Midle Malam6'>Midle Malam6</option>
                            <option id='TxtIsi2' value='Midle Malam7'>Midle Malam7</option>
                            <option id='TxtIsi2' value='Midle Malam8'>Midle Malam8</option>
                            <option id='TxtIsi2' value='Midle Malam9'>Midle Malam9</option>
                            <option id='TxtIsi2' value='Midle Malam10'>Midle Malam10</option>
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
                                bukaquery2(sprintf("insert into set_kode_shift_smc values ('%s', '%s')", $shift, $kode_shift));
                                echo <<<HTML
                                    <meta http-equiv="refresh" content="1;URL=?act=ListKodeShiftSmc&action=TAMBAH">
                                    HTML;
                            } catch (mysqli_sql_exception $e) {
                                if ($e->getCode() == 1062) {
                                    echo "<b style='color:red'>Data kode jam shift sudah ada..!!!</b>";
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
                        bukaquery2(sprintf("delete from set_kode_shift_smc where shift = '%s'", $shift));
                    } catch (mysqli_sql_exception $e) {
                        echo "<b style='color:red'>Gagal menghapus</b>";
                    }
                    break;
            }
            ?>
            <div style="width: 100%; height: 57%; overflow: auto">
                <table width="99%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                    <?php
                    $hasil  = bukaquery('select set_kode_shift_smc.shift, set_kode_shift_smc.kode_shift from set_kode_shift_smc order by set_kode_shift_smc.shift');
                    $jumlah = mysqli_num_rows($hasil);
                    ?>

                    <?php if (mysqli_num_rows($hasil) !== 0): ?>
                        <tr class="head">
                            <td width="5%" align="center" valign="center">Aksi</td>
                            <td width="15%" align="center" valign="center">Kode Shift</td>
                            <td width="80%" align="center" valign="center">Jam Jaga Shift</td>
                        </tr>
                        <?php while ($baris = mysqli_fetch_array($hasil)): ?>
                            <tr class="isi">
                                <td width="5%" align="center">
                                    <a href="?act=ListKodeShiftSmc&action=HAPUS&shift=<?=  str_replace(' ', '_', $baris[1]) ?>"><span>[hapus]</span></a>
                                </td>
                                <td width="15%"><?= $baris[0] ?></td>
                                <td width="80%"><?= $baris[1] ?></td>
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