<div id="post">
    <div align="center" class="link">
        <a href=?act=InputSttskerja&action=TAMBAH>| Input Data |</a>
        <a href=?act=ListSttskerja>| List Data |</a>
        <a href=?act=HomeAdmin>| Menu Utama |</a>
    </div>
    <div class="entry">
        <form name="frm_pelatihan" onsubmit="return validasiIsi();" method="post" enctype="application/x-www-form-urlencoded">
            <?php
                /*
                $action   = isset($_GET['action'])?$_GET['action']:NULL;
                $stts     = validTeks(str_replace("_"," ",isset($_GET['stts']))?str_replace("_"," ",$_GET['stts']):NULL);
                if($action == "TAMBAH"){
                    $stts         = validTeks(str_replace("_"," ",isset($_GET['stts']))?str_replace("_"," ",$_GET['stts']):NULL);
                    $ktg          = "";
                    $indek        = "";
                    $hakcuti      = "";
                }else if($action == "UBAH"){
                    $_sql         = "SELECT * FROM stts_kerja WHERE stts_kerja.stts='$stts'";
                    $hasil        = bukaquery($_sql);
                    $baris        = mysqli_fetch_row($hasil);
                    $stts         = $baris[0];
                    $ktg          = $baris[1];
                    $indek        = $baris[2];
                    $hakcuti      = $baris[3];
                }
                echo"<input type=hidden name=stts value=$stts><input type=hidden name=action value=$action>";
            ?>
            <table width="100%" align="center">
                <tr class="head">
                    <td width="31%" >Status</td><td width="">:</td>
                    <td width="67%">
                        <input name="stts" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" type=text id="TxtIsi1" class="inputbox" value="<?php echo $stts;?>" size="10" maxlength="3" pattern="[a-zA-Z 0-9-]{1,3}" title=" a-z A-Z 0-9 (Maksimal 3 karakter)" autocomplete="off" required autofocus/>
                        <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%" >Keterangan</td><td width="">:</td>
                    <td width="67%">
                        <input name="ktg" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi2'));" type=text id="TxtIsi2" class="inputbox" value="<?php echo $ktg;?>" size="40" maxlength="20" pattern="[a-zA-Z 0-9-]{1,20}" title=" a-z A-Z 0-9 (Maksimal 20 karakter)" autocomplete="off" required/>
                        <span id="MsgIsi2" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%" >Index Status</td><td width="">:</td>
                    <td width="67%">
                        <input name="indek" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi3'));" type=text id="TxtIsi3" class="inputbox" value="<?php echo $indek;?>" size="10" maxlength="2" pattern="[0-9]{1,2}" title=" 0-9 (Maksimal 2 karakter)" autocomplete="off" required/>
                        <span id="MsgIsi3" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%" >Hak Cuti</td><td width="">:</td>
                    <td width="67%">
                        <input name="hakcuti" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi4'));" type=text id="TxtIsi4" class="inputbox" value="<?php echo $hakcuti;?>" size="10" maxlength="2" pattern="[0-9]{1,2}" title=" 0-9 (Maksimal 2 karakter)" autocomplete="off" required/>
                        <span id="MsgIsi4" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
            </table>
            <div align="center"><input name=BtnSimpan type=submit class="button" value="SIMPAN">&nbsp<input name=BtnKosong type=reset class="button" value="KOSONG"></div>
            <?php
                $BtnSimpan=isset($_POST['BtnSimpan'])?$_POST['BtnSimpan']:NULL;
                if (isset($BtnSimpan)) {
                    $stts    = validTeks(trim($_POST['stts']));
                    $ktg     = validTeks(trim($_POST['ktg']));
                    $indek   = validangka(trim($_POST['indek']));
                    $hakcuti = validangka(trim($_POST['hakcuti']));
                    if ((isset($stts))&&(isset($ktg))&&(isset($indek))&&(isset($hakcuti))) {
                        switch($action) {
                            case "TAMBAH":
                                try {
                                    Tambah(" stts_kerja "," '$stts','$ktg','$indek','$hakcuti' "," status kerja ");
                                    echo"<html><head><title></title><meta http-equiv='refresh' content='1;URL=?act=InputSttskerja&action=TAMBAH'></head><body></body></html>";
                                } catch(mysqli_sql_exception $e) {
                                    if($e->getCode()==1062){
                                        echo "<b style='color:red'>Status kerja sudah ada..!!!</b>";
                                    }else{
                                        echo "<b style='color:red'>Gagal menyimpan</b>";
                                    }
                                }
                                break;
                            case "UBAH":
                                try {
                                    Ubah(" stts_kerja "," ktg='$ktg',indek='$indek',hakcuti='$hakcuti' WHERE stts='$stts' "," status kerja ");
                                    echo"<html><head><title></title><meta http-equiv='refresh' content='2;URL=?act=ListSttskerja'></head><body></body></html>";
                                } catch(mysqli_sql_exception $e) {
                                    echo "<b style='color:red'>Gagal mengubah</b>";
                                }
                                break;
                        }
                    }else{
                        echo 'Semua field harus isi..!!';
                    }
                }
                */
            $action        = isset($_GET['action']) ? $_GET['action'] : null;
            $stts          = isset($_GET['stts']) ? $_GET['stts'] : null;
            $stts          = validTeks(str_replace('_', ' ', $stts));
            $ktg           = '';
            $indek         = '';
            $hakcuti       = '';
            $cuti_besar    = '';
            $hakcuti_besar = '';

            if ($action === 'UBAH') {
                $_sql          = sprintf("select stts_kerja.stts, stts_kerja.ktg, stts_kerja.indek, stts_kerja.hakcuti, stts_kerja.cuti_besar, stts_kerja.hakcuti_besar from stts_kerja where stts_kerja.stts = '%s'", $stts);
                $hasil         = bukaquery($_sql);
                $baris         = mysqli_fetch_row($hasil);
                $stts          = $baris[0];
                $ktg           = $baris[1];
                $indek         = $baris[2];
                $hakcuti       = $baris[3];
                $cuti_besar    = $baris[4];
                $hakcuti_besar = $baris[5];
            }
            ?>
            <input type="hidden" name="stts" value="<?= $stts ?>">
            <table width="100%" align="center">
                <tr class="head">
                    <td width="31%">Status</td>
                    <td width="">:</td>
                    <td width="67%">
                        <input name="stts" class="text inputbox" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" type="text" id="TxtIsi1" value="<?= $stts ?>" size="10" maxlength="3" pattern="[a-zA-Z 0-9-]{1,3}" title=" a-z A-Z 0-9 (Maksimal 3 karakter)" autocomplete="off" required autofocus />
                        <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%">Keterangan</td>
                    <td width="">:</td>
                    <td width="67%">
                        <input name="ktg" class="text inputbox" onkeydown="setDefault(this, document.getElementById('MsgIsi2'));" type="text" id="TxtIsi2" value="<?= $ktg ?>" size="40" maxlength="20" pattern="[a-zA-Z 0-9-]{1,3}" title=" a-z A-Z 0-9 (Maksimal 3 karakter)" autocomplete="off" required />
                        <span id="MsgIsi2" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%">Index Status</td>
                    <td width="">:</td>
                    <td width="67%">
                        <input name="indek" class="text inputbox" onkeydown="setDefault(this, document.getElementById('MsgIsi3'));" type="text" id="TxtIsi3" value="<?= $indek ?>" size="10" maxlength="2" pattern="[a-zA-Z 0-9-]{1,3}" title=" a-z A-Z 0-9 (Maksimal 3 karakter)" autocomplete="off" required />
                        <span id="MsgIsi3" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%">Hak Cuti Tahunan</td>
                    <td width="">:</td>
                    <td width="67%">
                        <input name="hakcuti" class="text inputbox" onkeydown="setDefault(this, document.getElementById('MsgIsi4'));" type="text" id="TxtIsi4" value="<?= $hakcuti ?>" size="10" maxlength="2" pattern="[a-zA-Z 0-9-]{1,3}" title=" a-z A-Z 0-9 (Maksimal 3 karakter)" autocomplete="off" required />
                        <span id="MsgIsi4" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%">Cuti Besar</td>
                    <td width="">:</td>
                    <td width="67%">
                        <select name="cuti_besar" class="text3" style="width: 95%">
                            <option value=""></option>
                            <option value="Tidak Ada" <?= $cuti_besar === 'Tidak Ada' ? 'selected' : '' ?>>Tidak Ada</option>
                            <option value="10 Tahun Dari TMT" <?= $cuti_besar === '10 Tahun Dari TMT' ? 'selected' : '' ?>>10 Tahun Dari TMT Kerja</option>
                        </select>
                    </td>
                </tr>
                <tr class="head">
                    <td width="31%">Hak Cuti Besar</td>
                    <td width="">:</td>
                    <td width="67%">
                        <input name="hakcuti_besar" class="text inputbox" onkeydown="setDefault(this, document.getElementById('MsgIsi5'));" type="text" id="TxtIsi5" value="<?= $hakcuti_besar ?>" size="10" maxlength="2" pattern="[a-zA-Z 0-9-]{1,3}" title=" a-z A-Z 0-9 (Maksimal 3 karakter)" autocomplete="off" required />
                        <span id="MsgIsi5" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
            </table>

            <div>
                <center>
                    <button type="submit" name="BtnSimpan" class="button">
                        <span>&nbsp;&nbsp;SIMPAN&nbsp;&nbsp;</span>
                    </button>
                </center>
            </div>

            <?php
            $simpan = isset($_POST['BtnSimpan']) ? $_POST['BtnSimpan'] : null;
            if ($simpan !== null) {
                $stts             = validTeks(trim($_POST['stts']));
                $ktg              = validTeks(trim($_POST['ktg']));
                $indek            = validTeks(trim($_POST['indek']));
                $hakcuti          = validTeks(trim($_POST['hakcuti']));
                $cuti_besar    = validTeks(trim($_POST['cuti_besar']));
                $hakcuti_besar = validTeks(trim($_POST['hakcuti_besar']));
                if (!empty($stts) && !empty($ktg) && !empty($indek) && !empty($hakcuti) && !empty($cuti_besar) && !empty($hakcuti_besar)) {
                    switch ($action) {
                        case 'TAMBAH':
                            try {
                                bukaquery2(sprintf("insert into stts_kerja values ('%s', '%s', %s, %s, '%s', %s)", $stts, $ktg, $indek, $hakcuti, $cuti_besar, $hakcuti_besar));
                                echo <<<'HTML'
                                    <meta http-equiv="refresh" content="1;URL=?act=InputSttskerja&action=TAMBAH">
                                    HTML;
                            } catch (mysqli_sql_exception $e) {
                                if ($e->getCode() === 1062) {
                                    echo <<<'HTML'
                                        <b style="color: red">Status kerja sudah ada..!!</b>
                                        HTML;
                                } else {
                                    echo <<<'HTML'
                                        <b style="color: red">Gagal menyimpan..!!</b>
                                        HTML;
                                }
                            }
                            break;
                        case 'UBAH':
                            try {
                                bukaquery2(sprintf("update stts_kerja set ktg = '%s', indek = %s, hakcuti = %s, cuti_besar = '%s', hakcuti_besar = %s where stts = '%s'", $ktg, $indek, $hakcuti, $cuti_besar, $hakcuti_besar, $stts));
                                echo <<<'HTML'
                                    <meta http-equiv="refresh" content="1;URL=?act=ListSttskerja&action=UBAH">
                                    HTML;
                            } catch (mysqli_sql_exception $e) {
                                echo <<<'HTML'
                                    <b style="color: red">Gagal menyimpan..!!</b>
                                    HTML;
                            }
                            break;
                    }
                } else {
                    echo 'Semua Field harus diisi..!!';
                }
            }
            ?>
        </form>
    </div>
</div>
