<?php
    if(strpos($_SERVER['REQUEST_URI'],"pages")){
        exit(header("Location:../index.php"));
    }
?>
<div id="post">
    <div class="entry">        
        <form name="frm_aturadmin" onsubmit="return validasiIsi();" method="post" action="" enctype="multipart/form-data">
            <?php
                echo "";
                $action  = isset($_GET['action']) ? $_GET['action'] : null;
                $kode    = isset($_GET['iyem']) ? trim($_GET['iyem']) : null;
                $kode    = json_decode(encrypt_decrypt($kode, "d"), true);
                $nama    = '';
                $_sql2 = '';
                $hasil = null;
                $includeKompilasi = true;
                if (isset($kode["kode"])) {
                    $kode = validTeks4($kode["kode"], 10);
                    $_sql2 = "select * from master_berkas_digital where kode = '$kode'";
                    $hasil = bukaquery2($_sql2);
                    if ($row = mysqli_fetch_assoc($hasil)) {
                        $kode = $row['kode'];
                        $nama = $row['nama'];
                        $includeKompilasi = filter_var($row['include_kompilasi_berkas'], FILTER_VALIDATE_BOOLEAN);
                    }
                } else {
                    $kode = '';
                }
            ?>
            <input type="hidden" name="kodelama" value="<?= $kode ?>">
            <input type="hidden" name="action" value="<?= $action ?>">
            <div align="center" class="link">
                <span>|</span> <a href="?act=List">List Berkas</a> <span>|</span>
            </div>
            <div style="width: 100%; height: 13%; overflow: auto;">
                <table width="100%" align="center">
                    <tr class="head">
                        <td width="25%">Kode Berkas Digital</td>
                        <td width="">:</td>
                        <td width="74%">
                            <input name="kode" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));"
                                type="text" id="TxtIsi1" class="inputbox" value="<?= $kode ?>" size="30" maxlength="10"
                                pattern="[A-Z0-9-]{1,10}" title="A-Z0-9 - (Maksimal 10 karakter)" autocomplete="off"
                                required autofocus>
                            <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                        </td>
                    </tr>
                    <tr class="head">
                        <td width="25%">Nama Berkas Digital</td>
                        <td width="">:</td>
                        <td width="74%">
                            <input name="nama" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi2'));"
                                type="text" id="TxtIsi2" class="inputbox" value="<?= $nama ?>" size="70" maxlength="100"
                                pattern="[a-zA-Z 0-9-]{1,100}" title=" a-zA-Z0-9 - (Maksimal 100 karakter)"
                                autocomplete="off" required>
                            <span id="MsgIsi2" style="color:#CC0000; font-size:10px;"></span>
                        </td>
                    </tr>
                    <tr class="head">
                        <td width="25%">Masuk ke berkas kompilasi?</td>
                        <td width=""></td>
                        <td width="74%">
                            <select class="text" name="include_kompilasi_berkas">
                                <option value="1" <?= $includeKompilasi ? 'selected' : null ?>>Ya</option>
                                <option value="0" <?= !$includeKompilasi ? 'selected' : null ?>>Tidak</option>
                            </select>
                        </td>
                    </tr>
                </table>
            </div>
            <div align="center">
                <input name="BtnSimpan" type="submit" class="button" value="&nbsp;&nbsp;Simpan&nbsp;&nbsp;">&nbsp
                <input name="BtnKosong" type="reset" class="button" value="&nbsp;&nbsp;Kosong&nbsp;&nbsp;">
            </div><br />
            <?php
                $BtnSimpan = isset($_POST['BtnSimpan']) ? $_POST['BtnSimpan'] : null;
                if (isset($BtnSimpan)) {
                    $kode             = trim($_POST['kode']);
                    $nama             = trim($_POST['nama']);
                    $includeKompilasi = trim($_POST['include_kompilasi_berkas']);
                    $kode             = validTeks4($kode, 10);
                    $nama             = validTeks4($nama, 100);
                    $includeKompilasi = validTeks4($includeKompilasi, 1);
                    $includeKompilasi = (int) filter_var($includeKompilasi, FILTER_VALIDATE_BOOLEAN);
                    if ($action == 'TAMBAH') {
                        if (!empty($kode) && !empty($nama)) {
                            Tambah("master_berkas_digital", "'$kode', '$nama', $includeKompilasi", "Master Berkas Digital");
                            echo "<meta http-equiv='refresh' content='1;URL=?act=MasterBerkas&action=TAMBAH'>";
                        } else {
                            echo 'Semua field harus isi..!!!';
                        }
                    } else if ($action == 'EDIT') {
                        $kodelama = trim($_POST['kodelama']);
                        if (!empty($kode) && !empty($nama)) {
                            Ubah("master_berkas_digital", "kode='$kode', nama='$nama', include_kompilasi_berkas=$includeKompilasi where kode='$kodelama'", "Master Berkas Digital");
                            echo "<meta http-equiv='refresh' content='1;URL=?act=MasterBerkas&action=TAMBAH'>";
                        } else {
                            echo 'Semua field harus isi..!!!';
                        }
                    } else if ($action == 'HAPUS') {
                        Hapus("master_berkas_digital", "kode='$kode'", "?act=MasterBerkas&action=TAMBAH");
                    }
                }
            ?>
            <div style="width: 100%; height: 67%; overflow: auto;">
                <?php
                    $_sql = "select * from master_berkas_digital order by kode asc";
                    $hasil = bukaquery($_sql);
                    $jumlah = mysqli_num_rows($hasil);
                    $ttllembur = 0;
                    $ttlhr = 0;
                ?>

                <?php if ($jumlah != 0): ?>
                    <table width="99.6%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                        <tr class="head">
                            <td width="5%">
                                <div align="center">Proses</div>
                            </td>
                            <td width="18%">
                                <div align="center">Kode Berkas Digital</div>
                            </td>
                            <td width="70%">
                                <div align="center">Nama Berkas Digital</div>
                            </td>
                            <td width="6%">
                                <div align="center">Masuk Berkas Kompilasi</div>
                            </td>
                        </tr>
                        <?php while ($baris = mysqli_fetch_array($hasil)): ?>
                            <tr class="isi">
                                <td width="70">
                                    <center>
                                        <a href="?act=MasterBerkas&action=EDIT&iyem=<?= encrypt_decrypt('{"kode":"'.$baris[0].'"}', 'e') ?>">[edit]</a>|
                                        <a href="?act=MasterBerkas&action=HAPUS&iyem=<?= encrypt_decrypt('{"kode":"'.$baris[0].'"}', 'e') ?>">[hapus]</a>
                                    </center>
                                </td>
                                <td><?= $baris[0] ?></td>
                                <td><?= $baris[1] ?></td>
                                <td><?= $baris[2] == 1 ? 'Ya' : 'Tidak' ?></td>
                            </tr>
                        <?php endwhile; ?>
                    </table>
                <?php else: ?>
                    <table width="99.6%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                        <tr class="head">
                            <td width="5%">
                                <div align="center">Proses</div>
                            </td>
                            <td width="18%">
                                <div align="center">Kode Berkas Digital</div>
                            </td>
                            <td width="70%">
                                <div align="center">Nama Berkas Digital</div>
                            </td>
                            <td width="6%">
                                <div align="center">Masuk Berkas Kompilasi</div>
                            </td>
                        </tr>
                    </table>
                <?php endif; ?>
            </div>
        </form>
        <table width="99.6%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
            <tr class="head">
                <td>
                    <div align="left">Data: <?= $jumlah ?></div>
                </td>
            </tr>
        </table>
    </div>
</div>
