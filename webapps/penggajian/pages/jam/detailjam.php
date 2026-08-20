<?php
    if(strpos($_SERVER['REQUEST_URI'],"pages")){
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
        <form name="frm_aturadmin" onsubmit="return validasiIsi();" method="post" action="" enctype=multipart/form-data>
            <?php
                echo "";
                $action = isset($_GET['action'])?$_GET['action']:NULL;
                $no_id  = validTeks(isset($_GET['no_id'])?$_GET['no_id']:NULL);
                echo "<input type=hidden name=no_id  value=$no_id><input type=hidden name=action value=$action>";
            ?>
            <table width="100%" align="center">
                <tr class="head">
                    <td width="25%" >Departemen</td><td width="">:</td>
                    <td width="75%">
                        <select name="dep_id" class="text2" onkeydown="setDefault(this, document.getElementById('MsgIsi1'));" id="TxtIsi1" autofocus>
                            <!--<option id='TxtIsi12' value='null'>- Ruang -</option>-->
                            <?php
                                $_sql = "SELECT departemen.dep_id,departemen.nama FROM departemen ORDER BY departemen.dep_id";
                                $hasil=bukaquery($_sql);
                                while($baris = mysqli_fetch_array($hasil)) {
                                    echo "<option id='TxtIsi1' value='$baris[0]'>$baris[0] $baris[1]</option>";
                                }
                            ?>
                        </select>
                        <span id="MsgIsi1" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="25%" >Jam Shift</td><td width="">:</td>
                    <td width="75%">
                        <select name="shift" class="text2" onkeydown="setDefault(this, document.getElementById('MsgIsi2'));" id="TxtIsi2">
                            <?php foreach ($shiftLegacy as $shift): ?>
                                <option id='TxtIsi2' value='<?= $shift ?>'><?= $shift ?></option>
                            <?php endforeach; ?>
                        </select>
                        <span id="MsgIsi2" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="25%" >Jam Masuk</td><td width="">:</td>
                    <td width="75%">
                        <select name="jam_masuk" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi3'));" id="TxtIsi3">
                             <?php
                                loadJam();
                             ?>
                        </select>
			<select name="menit_masuk" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi3'));" id="TxtIsi3">
                             <?php
                                loadMenit();
                             ?>
                        </select>
                        <span id="MsgIsi3" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
                <tr class="head">
                    <td width="25%" >Jam Pulang</td><td width="">:</td>
                    <td width="75%">
                        <select name="jam_pulang" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi4'));" id="TxtIsi4">
                             <?php
                                loadJam();
                             ?>
                        </select>
			<select name="menit_pulang" class="text" onkeydown="setDefault(this, document.getElementById('MsgIsi4'));" id="TxtIsi4">
                             <?php
                                loadMenit();
                             ?>
                        </select>
                        <span id="MsgIsi4" style="color:#CC0000; font-size:10px;"></span>
                    </td>
                </tr>
            </table>
            <div align="center"><input name=BtnSimpan type=submit class="button" value="SIMPAN">&nbsp;<input name=BtnKosong type=reset class="button" value="KOSONG"></div><br>
            <?php
                $BtnSimpan=isset($_POST['BtnSimpan'])?$_POST['BtnSimpan']:NULL;
                if (isset($BtnSimpan)) {
                    $no_id              = validTeks(trim($_POST['no_id']));
                    $dep_id             = validTeks(trim($_POST['dep_id']));
                    $shift              = validTeks(trim($_POST['shift']));
                    $jam_masuk          = validTeks(trim($_POST['jam_masuk']));
                    $menit_masuk        = validTeks(trim($_POST['menit_masuk']));
                    $jam_pulang         = validTeks(trim($_POST['jam_pulang']));
                    $menit_pulang       = validTeks(trim($_POST['menit_pulang']));

                    if (!empty($dep_id)) {
                        switch($action) {
                            case "TAMBAH":
                                try {
                                    Tambah(" jam_jaga "," '0','$dep_id','$shift','$jam_masuk:$menit_masuk:00',
                                        '$jam_pulang:$menit_pulang:00' "," Jam Jaga ");
                                    echo"<meta http-equiv='refresh' content='1;URL=?act=ListJam&action=TAMBAH'>";
                                } catch(mysqli_sql_exception $e) {
                                    if($e->getCode()==1062){
                                        echo "<b style='color:red'>Data jam jaga sudah ada..!!!</b>";
                                    }else{
                                        echo "<b style='color:red'>Gagal menyimpan</b>";
                                    }
                                }
                                break;
                        }
                    }else if (empty($dep_id)){
                        echo 'Semua field harus isi..!!!';
                    }
                }
            ?>
            <div style="width: 100%; height: 57%; overflow: auto;">
            <?php
                $_sql = "SELECT jam_jaga.no_id,jam_jaga.dep_id,jam_jaga.shift,
                        jam_jaga.jam_masuk,jam_jaga.jam_pulang from jam_jaga
                        ORDER BY jam_jaga.dep_id ";
                $hasil=bukaquery($_sql);
                $jumlah=mysqli_num_rows($hasil);
                if(mysqli_num_rows($hasil)!=0) {
                    echo "<table width='99.6%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                            <tr class='head'>
                                <td width='10%'><div align='center'>Proses</div></td>
                                <td width='20%'><div align='center'>Departemen</div></td>
                                <td width='24%'><div align='center'>Shift</div></td>
                                <td width='23%'><div align='center'>Jam Datang</div></td>
                                <td width='23%'><div align='center'>Jam Pulang</div></td>
                            </tr>";
                    while($baris = mysqli_fetch_array($hasil)) {
                      echo "<tr class='isi'>
                                <td>
                                    <center>";?>
                                    <a href="?act=ListJam&action=HAPUS&no_id=<?php print $baris[0] ?>" >[hapus]</a>
                            <?php
                            echo "</center>
                                </td>
                                <td>$baris[1]</td>
                                <td>$baris[2]</td>
                                <td>$baris[3]</td>
                                <td>$baris[4]</td>
                           </tr>";
                    }
                    echo "</table>";
                } else {
                    echo "<table width='99.6%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                                <tr class='head'>
                                    <td width='10%'><div align='center'>Proses</div></td>
                                    <td width='20%'><div align='center'>Departemen</div></td>
                                    <td width='24%'><div align='center'>Shift</div></td>
                                    <td width='23%'><div align='center'>Jam Datang</div></td>
                                    <td width='23%'><div align='center'>Jam Pulang</div></td>
                                </tr>
                         </table>";
                }

                if ($action=="HAPUS") {
                    try {
                        Hapus("  jam_jaga "," no_id ='".$no_id."' ","?act=ListJam&action=TAMBAH");
                    } catch(mysqli_sql_exception $e) {
                        echo "<b style='color:red'>Gagal menghapus</b>";
                    }
                }

                echo "<table width='99.6%' border='0' align='center' cellpadding='0' cellspacing='0' class='tbl_form'>
                        <tr class='head'>
                            <td><div align='left'>Data : $jumlah</div></td>
                        </tr>
                     </table>";
            ?>
            </div>
        </form>
    </div>
</div>