<?php
if (strpos($_SERVER['REQUEST_URI'], "pages")) {
    exit(header("Location:../index.php"));
}

$BATAS_TAMPIL = 100;

$keyword = validTeks(isset($_POST['keyword']) ? trim($_POST['keyword']) : '');
$status  = validTeks(isset($_POST['status']) ? trim($_POST['status']) : 'AKTIF');
$pesan   = '';

$BtnSimpan = isset($_POST['BtnSimpan']) ? $_POST['BtnSimpan'] : null;
if (isset($BtnSimpan) && isset($_POST['pin']) && is_array($_POST['pin'])) {
    $dikirim = [];
    foreach ($_POST['pin'] as $id => $pin) {
        $dikirim[validTeks(trim($id))] = validTeks(trim($pin));
    }

    $lama = [];
    $milik = [];
    $hasilLama = bukaquery('select pin, id from mapping_pin_pegawai_smc');
    while ($baris = mysqli_fetch_array($hasilLama)) {
        $milik[$baris[0]] = $baris[1];
        $lama[$baris[1]]  = $baris[0];
    }

    $berubah = 0;
    $bentrok = [];
    foreach ($dikirim as $id => $pin) {
        $sebelumnya = isset($lama[$id]) ? $lama[$id] : '';
        if ($pin === $sebelumnya) {
            continue;
        }
        if ($pin !== '' && isset($milik[$pin]) && $milik[$pin] !== $id && !isset($dikirim[$milik[$pin]])) {
            $bentrok[] = $pin;
            continue;
        }

        try {
            bukaquery2(sprintf("delete from mapping_pin_pegawai_smc where id = '%s'", $id));
            if ($pin !== '') {
                bukaquery2(sprintf(
                    "insert into mapping_pin_pegawai_smc (pin, id) values ('%s', '%s') on duplicate key update id = values(id)",
                    $pin,
                    $id
                ));
            }
            $berubah++;
        } catch (mysqli_sql_exception $e) {
            $bentrok[] = $pin;
        }
    }

    if ($berubah > 0) {
        $pesan .= "<b style='color:green'>$berubah PIN diperbarui.</b> ";
    }
    if (count($bentrok) > 0) {
        $pesan .= "<b style='color:red'>PIN sudah dipakai pegawai lain : " . implode(', ', $bentrok) . "</b>";
    }
    if ($berubah === 0 && count($bentrok) === 0) {
        $pesan .= "Tidak ada perubahan.";
    }
}
?>

<div id="post">
    <div class="entry">
        <div align="center" class="link">
            <a href=?act=ListKodeShiftSmc&action=TAMBAH>| Kode Jam Jaga |</a>
            <a href=?act=HomeAdmin>| Menu Utama |</a>
        </div>
        <form name="frm_aturadmin" method="post" enctype="application/x-www-form-urlencoded">
            <table width="100%" align="center">
                <tr class="head">
                    <td width="10%">Status</td>
                    <td>:</td>
                    <td width="25%">
                        <select name="status" class="text2">
                            <option value="<?= $status ?>"><?= $status ?></option>
                            <option value="AKTIF">AKTIF</option>
                            <option value="CUTI">CUTI</option>
                            <option value="TENAGA LUAR">TENAGA LUAR</option>
                            <option value="KELUAR">KELUAR</option>
                        </select>
                    </td>
                    <td width="10%">Keyword</td>
                    <td>:</td>
                    <td width="55%">
                        <input name="keyword" class="text inputbox" type="text" size="40" maxlength="50" value="<?= $keyword ?>" placeholder="NIK / nama / departemen" autocomplete="off" autofocus />
                        <button type="submit" class="button"><span>&nbsp;&nbsp;CARI&nbsp;&nbsp;</span></button>
                    </td>
                </tr>
            </table>
            <div align="center"><?= $pesan ?></div><br>

            <div style="width: 100%; height: 57%; overflow: auto">
                <?php
                $filter = '';
                if ($keyword !== '') {
                    $filter = sprintf(
                        " and (pegawai.nik like '%%%s%%' or pegawai.nama like '%%%s%%' or departemen.nama like '%%%s%%') ",
                        $keyword,
                        $keyword,
                        $keyword
                    );
                }

                $hasil = bukaquery(sprintf(
                    "select pegawai.id, pegawai.nik, pegawai.nama, departemen.nama as departemen, ifnull(mapping_pin_pegawai_smc.pin, '') as pin " .
                    "from pegawai inner join departemen on pegawai.departemen = departemen.dep_id " .
                    "left join mapping_pin_pegawai_smc on pegawai.id = mapping_pin_pegawai_smc.id " .
                    "where pegawai.stts_aktif = '%s' %s order by pegawai.nama limit %d",
                    $status,
                    $filter,
                    $BATAS_TAMPIL + 1
                ));

                $daftar = [];
                while ($baris = mysqli_fetch_array($hasil)) {
                    $daftar[] = $baris;
                }
                $lebih  = count($daftar) > $BATAS_TAMPIL;
                $daftar = array_slice($daftar, 0, $BATAS_TAMPIL);
                $jumlah = count($daftar);
                ?>

                <table width="99%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                    <?php if ($jumlah !== 0): ?>
                        <tr class="head">
                            <td width="15%" align="center" valign="center">NIK</td>
                            <td width="40%" align="center" valign="center">Nama</td>
                            <td width="30%" align="center" valign="center">Departemen</td>
                            <td width="15%" align="center" valign="center">PIN Mesin</td>
                        </tr>
                        <?php foreach ($daftar as $baris): ?>
                            <tr class="isi">
                                <td width="15%"><?= $baris[1] ?></td>
                                <td width="40%"><?= $baris[2] ?></td>
                                <td width="30%"><?= $baris[3] ?></td>
                                <td width="15%">
                                    <input name="pin[<?= $baris[0] ?>]" class="text inputbox" type="text" size="10" maxlength="10" value="<?= $baris[4] ?>" pattern="[a-zA-Z0-9-]{0,10}" title=" a-z A-Z 0-9 (Maksimal 10 karakter)" autocomplete="off" />
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php endif; ?>
                </table>
                <table width="99%" border="0" align="center" cellpadding="0" cellspacing="0" class="tbl_form">
                    <tr class="head">
                        <td align="left">
                            <Data : <?= $jumlah ?><?= $lebih ? " (dipersempit, gunakan keyword untuk menyaring)" : "" ?>
                        </td>
                    </tr>
                </table>
            </div>
            <div align="center">
                <button type="submit" name="BtnSimpan" class="button"><span>&nbsp;&nbsp;SIMPAN&nbsp;&nbsp;</span></button>
            </div>
        </form>
    </div>
</div>
