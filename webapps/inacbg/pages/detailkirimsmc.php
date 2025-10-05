<?php
    if (strpos($_SERVER['REQUEST_URI'], "pages")) {
        exit(header("Location:../index.php"));
    }
?>
<div id="post">
    <?php 
        // sleep(10);
        $codernik  = isset($_GET['codernik']) ? validTeks($_GET['codernik']) : null;
        $corona    = isset($_GET['corona']) ? validTeks($_GET['corona']) : null;
        $nosep     = isset($_GET['nosep']) ? validTeks($_GET['nosep']) : null;
        $action    = isset($_GET['action']) ? validTeks($_GET['action']) : null;
        $grouper   = isset($_GET['grouper']) ? validTeks($_GET['grouper']) : null;
        $sukses    = isset($_GET['sukses']) ? validTeks($_GET['sukses']) : null;
        $carabayar = isset($_GET['carabayar']) ? validTeks(str_replace('_', ' ', $_GET['carabayar'])) : null;
    ?>
    <?php if ((isset($_GET['action']) ? validTeks($_GET['action']) : null) === 'selesai'): ?>
        <?php $queryurl = http_build_query(compact('codernik', 'nosep', 'corona')); ?>
        <div class="entry" style="font-family: Tahoma; font-size: 10pt; font-weight: 700; color: #22c55e; margin-top: 0.5rem; margin-left: 0.5rem">
            Klaim berhasil diproses!
        </div>
        <div class="entry" style="font-family: Tahoma; margin-top: 0.5rem; margin-left: 0.5rem">
            <a href="?act=DetailKirimSmc&<?= $queryurl."&action=reedit&grouper=idrg" ?>">[Edit Klaim IDRG]</a>
            <br />
            <br />
            <a href="?act=DetailKirimSmc&<?= $queryurl."&action=reedit&grouper=inacbg_stage1" ?>">[Edit Klaim INACBG]</a>
            <br />
            <br />
            <a href="?act=DetailKirimSmc&<?= $queryurl ?>&action=cetak">[Tarik ulang hasil cetak klaim]</a>
        </div>
    <?php elseif ((isset($_GET['action']) ? validTeks($_GET['action']) : null) === 'cetak'): ?>
        <?php $queryurl = http_build_query(compact('codernik', 'nosep', 'corona')); ?>
        <?php if (CetakKlaimSmc($nosep)['success']): ?>
            <meta http-equiv="refresh" content="2;URL=?act=DetailKirimSmc&<?= $queryurl ?>&action=selesai">
        <?php endif; ?>
    <?php else: ?>
        <form name="frm_aturadmin" onsubmit="return validasiIsi();" method="post" action="" enctype="multipart/form-data">
            <div class="entry">
                <?php
                    $judul     = 'SIMPAN & KIRIM KE EKLAIM';

                    if ($action === 'reedit') {
                        if ($grouper === 'inacbg_stage2') {
                            bukaquery2("delete from tempinacbg where coder_nik = '$codernik'");
                            bukaquery2("delete from inacbg_grouping_stage12 where no_sep = '$nosep'");
                        } else {
                            ReeditKlaimSmc($nosep, $grouper);
                        }
                    }

                    ['kode' => $isError, 'deskripsi' => $pesanError] = mysqli_fetch_array(bukaquery(<<<SQL
                        (
                            select inacbg_grouping_stage12.code_cbg as kode, inacbg_grouping_stage12.deskripsi
                            from inacbg_grouping_stage12
                            where inacbg_grouping_stage12.no_sep = '$nosep'
                            and (left(inacbg_grouping_stage12.code_cbg, 1)) = 'X'
                            limit 1
                        ) union all (
                            select idrg_grouping_smc.drg_code as kode, concat(idrg_grouping_smc.mdc_description, ' - ', idrg_grouping_smc.drg_description) as deskripsi
                            from idrg_grouping_smc
                            where idrg_grouping_smc.no_sep = '$nosep'
                            and idrg_grouping_smc.mdc_number = '36'
                            limit 1
                        )
                        SQL
                    ));
                ?>
                <?php if (!empty($isError)): ?>
                    <div class="center" style="margin-right: 1rem; margin-left: 0.7rem">
                        <span style="font-family: Tahoma; font-size: 10pt; font-weight: 700; color: #ff0000"><?= $pesanError ?></span>
                    </div>
                <?php endif; ?>
                <div style="width: 100%; height: 90%; overflow: auto">
                    <table width="100%" align="center">
                        <?php if ($grouper === 'idrg'): ?>
                            <?php
                                $baris = mysqli_fetch_array(bukaquery(<<<SQL
                                    select
                                        bridging_sep.no_sep, bridging_sep.asal_rujukan, bridging_sep.no_kartu, date(bridging_sep.tglpulang) as tglpulang,
                                        reg_periksa.*, pasien.nm_pasien, pasien.jk, pasien.umur, pasien.tgl_lahir, dokter.nm_dokter, poliklinik.nm_poli,
                                        penjab.png_jawab
                                    from bridging_sep
                                    join maping_dokter_dpjpvclaim on bridging_sep.kddpjp = maping_dokter_dpjpvclaim.kd_dokter_bpjs
                                    join dokter on maping_dokter_dpjpvclaim.kd_dokter = dokter.kd_dokter
                                    join reg_periksa on bridging_sep.no_rawat = reg_periksa.no_rawat
                                    join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis
                                    join poliklinik on reg_periksa.kd_poli = poliklinik.kd_poli
                                    join penjab on reg_periksa.kd_pj = penjab.kd_pj
                                    where bridging_sep.no_sep = '$nosep'
                                    SQL
                                ));
                                $norawat        = $baris['no_rawat'];
                                $no_rkm_medis   = $baris['no_rkm_medis'];
                                $nokartu        = $baris['no_kartu'];
                                $nm_pasien      = $baris['nm_pasien'];
                                $umurdaftar     = $baris['umurdaftar'];
                                $sttsumur       = $baris['sttsumur'];
                                $tgl_lahir      = $baris['tgl_lahir'];
                                $jk             = $baris['jk'];
                                $almt_pj        = $baris['almt_pj'];
                                $norawat        = $baris['no_rawat'];
                                $tgl_registrasi = $baris['tgl_registrasi'];
                                $tgl_keluar     = $baris['tglpulang'];
                                $jam_reg        = $baris['jam_reg'];
                                $nm_poli        = $baris['nm_poli'];
                                $asalrujukan    = $baris['asal_rujukan'];
                                $nm_dokter      = $baris['nm_dokter'];
                                $status_lanjut  = $baris['status_lanjut'];
                                $png_jawab      = $baris['png_jawab'];
                                $sistole        = '120';
                                $diastole       = '90';
                                $jnsrawat       = '1';

                                if ($status_lanjut == 'Ranap') {
                                    $jnsrawat = '1';
                                    $tensi = explode('/', getOne("(select tensi from pemeriksaan_ranap where no_rawat = '$norawat' order by tgl_perawatan desc, jam_rawat desc) union all (select '120/90' as tensi)"));
                                    if (! empty($tensi[0])) {
                                        $sistole = $tensi[0];
                                    }
                                    if (! empty($tensi[1])) {
                                        $diastole = $tensi[1];
                                    }
                                    if (empty($tgl_keluar)) {
                                        $tgl_keluar = getOne("select tgl_keluar from kamar_inap where no_rawat = '$norawat' order by tgl_keluar desc limit 1");
                                    }
                                } else {
                                    $jnsrawat = '2';
                                    $tensi = explode('/', getOne("(select tensi from pemeriksaan_ralan where no_rawat = '$norawat' order by tgl_perawatan desc, jam_rawat desc) union all (select '120/90' as tensi)"));
                                    if (! empty($tensi[0])) {
                                        $sistole = $tensi[0];
                                    }
                                    if (! empty($tensi[1])) {
                                        $diastole = $tensi[1];
                                    }
                                }

                                if ($corona == 'PasienCorona') {
                                    $nosep = getOne("select no_klaim from inacbg_noklaim_corona where no_rawat = '$norawat'");
                                    if (empty($nosep)) {
                                        $nosep = GenerateNomorCovid();
                                        Tambah3('inacbg_noklaim_corona', "'$norawat', '$nosep'");
                                    }
                                }

                                $naikkelas = getOne("select klsnaik from bridging_sep where no_rawat = '$norawat'");
                                if (empty($naikkelas)) {
                                    $naikkelas = getOne("select klsnaik from bridging_sep_internal where no_rawat = '$norawat'");
                                }

                                $upgrade_class_ind = '0';
                                if (! empty($naikkelas)) {
                                    $upgrade_class_ind = '1';
                                    if ($naikkelas == '1') {
                                        $naikkelas = 'Kelas VVIP';
                                    } else if ($naikkelas == '2') {
                                        $naikkelas = 'Kelas VIP';
                                    } else if ($naikkelas == '3') {
                                        $naikkelas = 'Kelas 1';
                                    } else if ($naikkelas == '4') {
                                        $naikkelas = 'Kelas 2';
                                    } else {
                                        $naikkelas = '';
                                    }
                                } else {
                                    $naikkelas = '';
                                }
                            ?>
                            <tr class="head">
                                <td width="28%">No. Rawat</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $norawat ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">No. SEP</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nosep ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">No. RM</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $no_rkm_medis ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">No. Kartu Peserta</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nokartu ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Nama Pasien</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nm_pasien.', '.$umurdaftar.' '.$sttsumur ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Jenis Kelamin</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $jk ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Alamat</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $almt_pj ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Tgl. Registrasi</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $tgl_registrasi.' '.$jam_reg ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Poliklinik</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nm_poli ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Dokter DPJP</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nm_dokter ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Status</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $status_lanjut.' ('.$png_jawab.')' ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Tgl. Keluar</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <input name="keluar" class="text inputbox" type="text" style="font-family: Tahoma; width: 95%" value="<?= $tgl_keluar ?>" size="15" maxlength="10">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Kelas Rawat</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="kelas_rawat" class="text" style="font-family: Tahoma; width: 95%">
                                        <?php if ($status_lanjut == 'Ralan'): ?>
                                            <option value="3">Kelas Reguler</option>
                                            <option value="1">Kelas Eksekutif</option>
                                        <?php else: ?>
                                            <?php $kelas = getOne("select klsrawat from bridging_sep where no_sep = '$nosep'"); ?>
                                            <option <?= $kelas == '1' ? 'selected' : '' ?> value="1">Kelas 1</option>
                                            <option <?= $kelas == '2' ? 'selected' : '' ?> value="2">Kelas 2</option>
                                            <option <?= $kelas == '3' ? 'selected' : '' ?> value="3">Kelas 3</option>
                                        <?php endif; ?>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Cara Masuk</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="cara_masuk" class="text" style="font-family: Tahoma; width: 95%">
                                        <option <?= $asalrujukan === '1' ? 'selected' : '' ?> value="gp">Rujukan FKTP</option>
                                        <option <?= $asalrujukan === '2' ? 'selected' : '' ?> value="hosp-trans">Rujukan FKRTL</option>
                                        <option value="mp">Rujukan Spesialis</option>
                                        <option value="outp">Dari Rawat Jalan</option>
                                        <option value="inp">Dari Rawat Inap</option>
                                        <option value="emd">Dari Rawat Darurat</option>
                                        <option value="born">Lahir di RS</option>
                                        <option value="nursing">Rujukan Panti Jompo</option>
                                        <option value="psych">Rujukan dari RS Jiwa</option>
                                        <option value="rehab">Rujukan Fasilitas Rehab</option>
                                        <option <?= $asalrujukan > '2' ? 'selected' : '' ?> value="other">Lain-lain</option>
                                    </select>
                                </td>
                            </tr>
                            <?php if ($status_lanjut == 'Ranap'): ?>
                                <tr class="head">
                                    <td width="28%">ADL Sub Acute</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="adl_sub_acute" class="text3" style="font-family: Tahoma; width: 95%">
                                            <option value="0"></option>
                                            <?php for ($i = 12; $i <= 60; $i++): ?>
                                                <option value="<?= $i ?>"><?= $i ?></option>
                                            <?php endfor; ?>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">ADL Chronic</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="adl_chronic" class="text3" style="font-family: Tahoma; width: 95%">
                                            <option value="0"></option>
                                            <?php for ($i = 12; $i <= 60; $i++): ?>
                                                <option value="<?= $i ?>"><?= $i ?></option>
                                            <?php endfor; ?>
                                        </select>
                                    </td>
                                </tr>
                                <?php
                                    $icu = 0;
                                    $adaIcu = mysqli_fetch_assoc(bukaquery2("select sum(lama) as total_icu from kamar_inap where kd_kamar like '%icu%' and no_rawat = '$norawat'"));
                                    if ($adaIcu) {
                                        $icu = (int) $adaIcu['total_icu'];
                                    }
                                ?>
                                <tr class="head">
                                    <td width="28%">ICU Indikator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="icu_indikator" class="text3" style="font-family: Tahoma; width: 95%">
                                            <option value="0" <?= ($icu <= 0) ? 'selected' : '' ?>>0</option>
                                            <option value="1" <?= ($icu > 0) ? 'selected' : '' ?>>1</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">ICU Los</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="icu_los" class="text inputbox" style="font-family: Tahoma" type="text" value="<?= $icu ?>" size="5" maxlength="5" pattern="[0-9]{1,5}" title="0-9 (Maksimal 5 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Jam Penggunaan Ventilator di ICU</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="ventilator_hour" class="text inputbox" style="font-family: Tahoma; width: 95%" type="text" value="0" size="5" maxlength="5" pattern="[0-9]{1,5}" title="0-9 (Maksimal 5 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                            <?php else: ?>
                                <input type="hidden" name="adl_sub_acute" value="0">
                                <input type="hidden" name="adl_chronic" value="0">
                                <input type="hidden" name="icu_indikator" value="0">
                                <input type="hidden" name="icu_los" value="0">
                                <input type="hidden" name="ventilator_hour" value="0">
                            <?php endif; ?>
                            <tr class="head">
                                <td width="28%">Indikator Upgrade Kelas</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="upgrade_class_ind" class="text3"style="font-family: Tahoma; width: 95%">
                                        <option value="<?= $upgrade_class_ind ?>"><?= $upgrade_class_ind ?></option>
                                        <option value="0">0</option>
                                        <option value="1">1</option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Naik ke Kelas</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="upgrade_class_class" class="text2" style="font-family: Tahoma; width: 95%">
                                        <option value="<?= $naikkelas ?>"><?= $naikkelas ?></option>
                                        <option value="kelas_1">Kelas 1</option>
                                        <option value="kelas_2">Kelas 2</option>
                                        <option value="vip">Kelas VIP</option>
                                        <option value="vvip">Kelas VVIP</option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Lama Hari Naik Kelas</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <input name="upgrade_class_los" class="text inputbox" style="font-family: Tahoma; width: 95%" type="text" value="0" size="5" maxlength="5" pattern="[0-9]{1,5}" title="0-9 (Maksimal 5 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Tambahan</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <input name="add_payment_pct" class="text inputbox" style="font-family: Tahoma; width: 95%" type="text" value="0" size="20" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Berat Saat Lahir</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <input name="birth_weight" class="text inputbox" style="font-family: Tahoma; width: 95%" type="text" value="" size="5" maxlength="5" pattern="[0-9]{1,5}" title="0-9 (Maksimal 5 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Sistole</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <input name="sistole" class="text inputbox" style="font-family: Tahoma; width: 95%" type="text" value="<?= $sistole ?>" size="5" maxlength="3" pattern="[0-9]{1,3}" title="0-9 (Maksimal 3 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Diastole</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <input name="diastole" class="text inputbox" style="font-family: Tahoma; width: 95%" type="text" value="<?= $diastole ?>" size="5" maxlength="3" pattern="[0-9]{1,3}" title="0-9 (Maksimal 3 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Status Pulang</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="discharge_status" class="text2" style="font-family: Tahoma; width: 95%">
                                        <?php if (getOne("select count(*) from kamar_inap where stts_pulang = 'Sembuh' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="1">Atas persetujuan dokter</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Sehat' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="1">Atas persetujuan dokter</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Rujuk' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="2">Dirujuk</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'APS' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="3">Atas permintaan sendiri</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Pulang Paksa' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="3">Atas permintaan sendiri</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Meninggal' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="4">Meninggal</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = '+' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="4">Meninggal</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Atas Persetujuan Dokter' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="1">Atas persetujuan dokter</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Atas Permintaan Sendiri' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="3">Atas permintaan sendiri</option>
                                        <?php elseif (getOne("select count(*) from kamar_inap where stts_pulang = 'Lain-lain' and no_rawat = '$norawat'") > 0): ?>
                                            <option value="5">Lain-lain</option>
                                        <?php else: ?>
                                            <option value="1">Atas persetujuan dokter</option>
                                        <?php endif; ?>
                                        <option value="1">Atas persetujuan dokter</option>
                                        <option value="2">Dirujuk</option>
                                        <option value="3">Atas permintaan sendiri</option>
                                        <option value="4">Meninggal</option>
                                        <option value="5">Lain-lain</option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Dializer Single Use</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <?php $dializer_single_use = getOne("select exists(select * from bridging_sep where no_sep = '$nosep' and nmpolitujuan like 'hemodial%')"); ?>
                                    <select name="dializer_single_use" class="text2" style="font-family: Tahoma; width: 95%">
                                        <option <?= $dializer_single_use == '1' ? 'selected ' : '' ?>value="1">Ya</Option>
                                        <option <?= $dializer_single_use == '0' ? 'selected ' : '' ?>value="0">Tidak</Option>
                                    </select>
                                </td>
                            </tr>
                            <?php if ($corona == 'PasienCorona'): ?>
                                <?php
                                    $bariscorona = mysqli_fetch_array(bukaquery(<<<SQL
                                        select
                                            pemulasaraan_jenazah, if (pemulasaraan_jenazah = 'Ya', 1, 0) as ytpemulasaraan_jenazah,
                                            kantong_jenazah, if (kantong_jenazah = 'Ya', 1, 0) as ytkantong_jenazah,
                                            peti_jenazah, if (peti_jenazah = 'Ya', 1, 0) as ytpeti_jenazah,
                                            plastik_erat, if (plastik_erat = 'Ya', 1, 0) as ytplastik_erat,
                                            desinfektan_jenazah, if (desinfektan_jenazah = 'Ya', 1, 0) as ytdesinfektan_jenazah,
                                            mobil_jenazah, if (mobil_jenazah = 'Ya', 1, 0) as ytmobil_jenazah,
                                            desinfektan_mobil_jenazah, if (desinfektan_mobil_jenazah = 'Ya', 1, 0) as ytdesinfektan_mobil_jenazah,
                                            covid19_status_cd, if (covid19_status_cd = 'ODP', 1, if (covid19_status_cd = 'PDP',2 ,3)) as ytcovid19_status_cd,
                                            covid19_cc_ind, if (covid19_cc_ind = 'Ya', 1, 0) as ytcovid19_cc_ind,
                                            nomor_kartu_t, episodes1, episodes2, episodes3, episodes4, episodes5, episodes6
                                        from perawatan_corona
                                        where no_rawat = '$norawat'
                                        SQL
                                    ));
                                ?>
                                <tr class="head">
                                    <td width="28%">Dilakukan Pemulasaran Jenazah?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="pemulasaraan_jenazah" class="text3">
                                            <option value="<?= $bariscorona['ytpemulasaraan_jenazah'] ?>"><?= $bariscorona['pemulasaraan_jenazah'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Menggunakan Kantong Jenazah?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="kantong_jenazah" class="text3">
                                            <option value="<?= $bariscorona['ytkantong_jenazah'] ?>"><?= $bariscorona['kantong_jenazah'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Menggunakan Peti Jenazah?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="peti_jenazah" class="text3">
                                            <option value="<?= $bariscorona['ytpeti_jenazah'] ?>"><?= $bariscorona['peti_jenazah'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Menggunakan Plastik Erat?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="plastik_erat" class="text3">
                                            <option value="<?= $bariscorona['ytplastik_erat'] ?>"><?= $bariscorona['plastik_erat'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Menggunakan Desinfektan Jenazah?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="desinfektan_jenazah" class="text3">
                                            <option value="<?= $bariscorona['ytdesinfektan_jenazah'] ?>"><?= $bariscorona['desinfektan_jenazah'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Menggunakan Mobil Jenazah?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="mobil_jenazah" class="text3">
                                            <option value="<?= $bariscorona['ytmobil_jenazah'] ?>"><?= $bariscorona['mobil_jenazah'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Menggunakan Desinfektan Mobil Jenazah?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="desinfektan_mobil_jenazah" class="text3">
                                            <option value="<?= $bariscorona['ytdesinfektan_mobil_jenazah'] ?>"><?= $bariscorona['desinfektan_mobil_jenazah'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Status Covid/Corona</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="covid19_status_cd" class="text3">
                                            <option value="<?= $bariscorona['ytcovid19_status_cd'] ?>"><?= $bariscorona['covid19_status_cd'] ?></option>
                                            <option value="1">ODP</option>
                                            <option value="2">PDP</option>
                                            <option value="3">Positif</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">No. Jaminan/NIK/KITAS/KITAP/PASPOR/JKN</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="nomor_kartu_t" class="text" type="text" class="inputbox" value="<?= $bariscorona['nomor_kartu_t'] ?>" size="40" maxlength="40" pattern="[A-Z0-9-]{1,40}" title=" A-Z0-9- (Maksimal 40 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Hari Penggunaan Ruang ICU Dengan Ventilator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="episodes1" class="text" type="text" class="inputbox" value="<?= $bariscorona['episodes1'] ?>" size="7" maxlength="3" pattern="[0-9]{1,3}" title=" 0-9 (Maksimal 3 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Hari Penggunaan Ruang ICU Tanpa Ventilator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="episodes2" class="text" type="text" class="inputbox" value="<?= $bariscorona['episodes2'] ?>" size="7" maxlength="3" pattern="[0-9]{1,3}" title=" 0-9 (Maksimal 3 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Hari Penggunaan Ruang Isolasi Tekanan Negatif Dengan Ventilator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="episodes3" class="text" type="text" class="inputbox" value="<?= $bariscorona['episodes3'] ?>" size="7" maxlength="3" pattern="[0-9]{1,3}" title=" 0-9 (Maksimal 3 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Hari Penggunaan Ruang Isolasi Tekanan Negatif Tanpa Ventilator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="episodes4" class="text" type="text" class="inputbox" value="<?= $bariscorona['episodes4'] ?>" size="7" maxlength="3" pattern="[0-9]{1,3}" title=" 0-9 (Maksimal 3 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Hari Penggunaan Ruang Isolasi Non Tekanan Negatif Dengan Ventilator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="episodes5" class="text" type="text" class="inputbox" value="<?= $bariscorona['episodes5'] ?>" size="7" maxlength="3" pattern="[0-9]{1,3}" title=" 0-9 (Maksimal 3 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Jumlah Hari Penggunaan Ruang Isolasi Non Tekanan Negatif Tanpa Ventilator</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <input name="episodes6" class="text" type="text" class="inputbox" value="<?= $bariscorona['episodes6'] ?>" size="7" maxlength="3" pattern="[0-9]{1,3}" title=" 0-9 (Maksimal 3 karakter)" autocomplete="off">
                                    </td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Ada Comorbid/Complexity/Penyerta?</td>
                                    <td width="1%">:</td>
                                    <td width="70%">
                                        <select name="covid19_cc_ind" class="text3">
                                            <option value="<?= $bariscorona['ytcovid19_cc_ind'] ?>"><?= $bariscorona['covid19_cc_ind'] ?></option>
                                            <option value="1">Ya</option>
                                            <option value="0">Tidak</option>
                                        </select>
                                    </td>
                                </tr>
                            <?php endif; ?>
                            <?php
                                $billing = mysqli_fetch_array(bukaquery(<<<SQL
                                    select
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Dokter Paramedis', 'Ranap Dokter Paramedis') and billing.nm_perawatan not like '%terapi%') as prosedur_non_bedah,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Operasi') as prosedur_bedah,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Dokter', 'Ranap Dokter')) as konsultasi,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Paramedis', 'Ranap Paramedis')) as keperawatan,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Radiologi') as radiologi,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Laborat') as laboratorium,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Registrasi', 'Kamar')) as kamar,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Obat' and billing.nm_perawatan like '%kronis%') as obat_kronis,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Obat' and billing.nm_perawatan like '%kemo%') as obat_kemoterapi,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Obat', 'Retur Obat', 'Resep Pulang')) as obat,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status = 'Tambahan') as bmhp,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Harian', 'Service')) as sewa_alat,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat and billing.status in ('Ralan Dokter Paramedis', 'Ranap Dokter Paramedis') and billing.nm_perawatan like '%terapi%') as rehabilitasi,
                                        (select ifnull(round(sum(billing.totalbiaya)), 0) from billing where billing.no_rawat = reg_periksa.no_rawat) as totalbilling
                                    from reg_periksa where reg_periksa.no_rawat = '$norawat'
                                    SQL
                                ));
                                $prosedur_non_bedah    = $billing['prosedur_non_bedah'];
                                $prosedur_bedah        = $billing['prosedur_bedah'];
                                $konsultasi            = $billing['konsultasi'];
                                $tenaga_ahli           = 0;
                                $keperawatan           = $billing['keperawatan'];
                                $radiologi             = $billing['radiologi'];
                                $laboratorium          = $billing['laboratorium'];
                                $kamar                 = $billing['kamar'];
                                $obat_kronis           = $billing['obat_kronis'];
                                $obat_kemoterapi       = $billing['obat_kemoterapi'];
                                $obat                  = $billing['obat'] - $obat_kronis - $obat_kemoterapi;
                                $bmhp                  = $billing['bmhp'];
                                $sewa_alat             = $billing['sewa_alat'];
                                $rehabilitasi          = $billing['rehabilitasi'];
                                $totalbilling          = $billing['totalbilling'];
                                $totalbillingsementara = $prosedur_non_bedah + $prosedur_bedah + $konsultasi + $tenaga_ahli
                                                       + $keperawatan + $radiologi + $laboratorium + $kamar + $obat_kronis
                                                       + $obat_kemoterapi + $obat + $bmhp + $sewa_alat + $rehabilitasi;
                            ?>
                            <tr class="head">
                                <td width="28%">Biaya Prosedur Non Bedah</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_prosedur_non_bedah" name="prosedur_non_bedah" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $prosedur_non_bedah ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_prosedur_non_bedah" name="diskon_prosedur_non_bedah" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Prosedur Bedah</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_prosedur_bedah" name="prosedur_bedah" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $prosedur_bedah ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_prosedur_bedah" name="diskon_prosedur_bedah" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Konsultasi</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_konsultasi" name="konsultasi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $konsultasi ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_konsultasi" name="diskon_konsultasi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Tenaga Ahli</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_tenaga_ahli" name="tenaga_ahli" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $tenaga_ahli ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_tenaga_ahli" name="diskon_tenaga_ahli" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Keperawatan</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_keperawatan" name="keperawatan" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $keperawatan ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_keperawatan" name="diskon_keperawatan" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Penunjang</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_penunjang" name="penunjang" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_penunjang" name="diskon_penunjang" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Radiologi</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_radiologi" name="radiologi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $radiologi ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_radiologi" name="diskon_radiologi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Laboratorium</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_laboratorium" name="laboratorium" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $laboratorium ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_laboratorium" name="diskon_laboratorium" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Pelayanan Darah</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_pelayanan_darah" name="pelayanan_darah" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_pelayanan_darah" name="diskon_pelayanan_darah" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Rehabilitasi</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_rehabilitasi" name="rehabilitasi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_rehabilitasi" name="diskon_rehabilitasi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Kamar</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_kamar" name="kamar" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $kamar ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_kamar" name="diskon_kamar" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Rawat Intensif</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_rawat_intensif" name="rawat_intensif" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_rawat_intensif" name="diskon_rawat_intensif" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Obat</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_obat" name="obat" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $obat ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_obat" name="diskon_obat" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Obat Kronis</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_obat_kronis" name="obat_kronis" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $obat_kronis ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_obat_kronis" name="diskon_obat_kronis" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Obat Kemoterapi</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_obat_kemoterapi" name="obat_kemoterapi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $obat_kemoterapi ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_obat_kemoterapi" name="diskon_obat_kemoterapi" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Alkes</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_alkes" name="alkes" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_alkes" name="diskon_alkes" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya BMHP</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_bmhp" name="bmhp" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $bmhp ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_bmhp" name="diskon_bmhp" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Biaya Sewa Alat</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_sewa_alat" name="sewa_alat" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="<?= $sewa_alat ?>" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_sewa_alat" name="diskon_sewa_alat" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Tarif Poli Eksekutif</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span>
                                    <input id="billing_tarif_poli_eks" name="tarif_poli_eks" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="15" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                    <span> Diskon </span>
                                    <input id="diskon_billing_tarif_poli_eks" name="diskon_tarif_poli_eks" class="text inputbox" type="text" style="font-family: Tahoma; text-align: right" value="0" size="10" maxlength="15" pattern="[0-9]{1,15}" title="0-9 (Maksimal 15 karakter)" autocomplete="off">
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Total Rincian Biaya</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span><span id="totalbillingsementara"><?= $totalbillingsementara ?></span>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">TOTAL BILLING</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <span>Rp. </span><span id="totalbilling"><?= $totalbilling ?></span>
                                </td>
                            </tr>
                            <script>
                                let totalbilling              = document.querySelector('#totalbilling')
                                let totalbillingsementara     = document.querySelector('#totalbillingsementara')

                                let prosedur_non_bedah        = document.querySelector('#billing_prosedur_non_bedah')
                                let diskon_prosedur_non_bedah = document.querySelector('#diskon_billing_prosedur_non_bedah')
                                let prosedur_bedah            = document.querySelector('#billing_prosedur_bedah')
                                let diskon_prosedur_bedah     = document.querySelector('#diskon_billing_prosedur_bedah')
                                let konsultasi                = document.querySelector('#billing_konsultasi')
                                let diskon_konsultasi         = document.querySelector('#diskon_billing_konsultasi')
                                let tenaga_ahli               = document.querySelector('#billing_tenaga_ahli')
                                let diskon_tenaga_ahli        = document.querySelector('#diskon_billing_tenaga_ahli')
                                let keperawatan               = document.querySelector('#billing_keperawatan')
                                let diskon_keperawatan        = document.querySelector('#diskon_billing_keperawatan')
                                let penunjang                 = document.querySelector('#billing_penunjang')
                                let diskon_penunjang          = document.querySelector('#diskon_billing_penunjang')
                                let radiologi                 = document.querySelector('#billing_radiologi')
                                let diskon_radiologi          = document.querySelector('#diskon_billing_radiologi')
                                let laboratorium              = document.querySelector('#billing_laboratorium')
                                let diskon_laboratorium       = document.querySelector('#diskon_billing_laboratorium')
                                let pelayanan_darah           = document.querySelector('#billing_pelayanan_darah')
                                let diskon_pelayanan_darah    = document.querySelector('#diskon_billing_pelayanan_darah')
                                let rehabilitasi              = document.querySelector('#billing_rehabilitasi')
                                let diskon_rehabilitasi       = document.querySelector('#diskon_billing_rehabilitasi')
                                let kamar                     = document.querySelector('#billing_kamar')
                                let diskon_kamar              = document.querySelector('#diskon_billing_kamar')
                                let rawat_intensif            = document.querySelector('#billing_rawat_intensif')
                                let diskon_rawat_intensif     = document.querySelector('#diskon_billing_rawat_intensif')
                                let obat                      = document.querySelector('#billing_obat')
                                let diskon_obat               = document.querySelector('#diskon_billing_obat')
                                let obat_kronis               = document.querySelector('#billing_obat_kronis')
                                let diskon_obat_kronis        = document.querySelector('#diskon_billing_obat_kronis')
                                let obat_kemoterapi           = document.querySelector('#billing_obat_kemoterapi')
                                let diskon_obat_kemoterapi    = document.querySelector('#diskon_billing_obat_kemoterapi')
                                let alkes                     = document.querySelector('#billing_alkes')
                                let diskon_alkes              = document.querySelector('#diskon_billing_alkes')
                                let bmhp                      = document.querySelector('#billing_bmhp')
                                let diskon_bmhp               = document.querySelector('#diskon_billing_bmhp')
                                let sewa_alat                 = document.querySelector('#billing_sewa_alat')
                                let diskon_sewa_alat          = document.querySelector('#diskon_billing_sewa_alat')
                                let tarif_poli_eks            = document.querySelector('#billing_tarif_poli_eks')
                                let diskon_tarif_poli_eks     = document.querySelector('#diskon_billing_tarif_poli_eks')

                                function hitungRincianBilling() {
                                    let nilaibilling = totalbilling.innerHTML

                                    let totalrincianbilling
                                        = (parseInt(prosedur_non_bedah.value) - parseInt(diskon_prosedur_non_bedah.value))
                                        + (parseInt(prosedur_bedah.value) - parseInt(diskon_prosedur_bedah.value))
                                        + (parseInt(konsultasi.value) - parseInt(diskon_konsultasi.value))
                                        + (parseInt(tenaga_ahli.value) - parseInt(diskon_tenaga_ahli.value))
                                        + (parseInt(keperawatan.value) - parseInt(diskon_keperawatan.value))
                                        + (parseInt(penunjang.value) - parseInt(diskon_penunjang.value))
                                        + (parseInt(radiologi.value) - parseInt(diskon_radiologi.value))
                                        + (parseInt(laboratorium.value) - parseInt(diskon_laboratorium.value))
                                        + (parseInt(pelayanan_darah.value) - parseInt(diskon_pelayanan_darah.value))
                                        + (parseInt(rehabilitasi.value) - parseInt(diskon_rehabilitasi.value))
                                        + (parseInt(kamar.value) - parseInt(diskon_kamar.value))
                                        + (parseInt(rawat_intensif.value) - parseInt(diskon_rawat_intensif.value))
                                        + (parseInt(obat.value) - parseInt(diskon_obat.value))
                                        + (parseInt(obat_kronis.value) - parseInt(diskon_obat_kronis.value))
                                        + (parseInt(obat_kemoterapi.value) - parseInt(diskon_obat_kemoterapi.value))
                                        + (parseInt(alkes.value) - parseInt(diskon_alkes.value))
                                        + (parseInt(bmhp.value) - parseInt(diskon_bmhp.value))
                                        + (parseInt(sewa_alat.value) - parseInt(diskon_sewa_alat.value))
                                        + (parseInt(tarif_poli_eks.value) - parseInt(diskon_tarif_poli_eks.value))

                                    totalbillingsementara.innerHTML = totalrincianbilling

                                    if (parseInt(totalrincianbilling) == parseInt(nilaibilling)) {
                                        totalbillingsementara.style.fontWeight = '400'
                                        totalbillingsementara.style.color = 'inherit'
                                    } else {
                                        totalbillingsementara.style.fontWeight = '700'
                                        totalbillingsementara.style.color = '#f00'
                                    }
                                }

                                function janganSubmitSaatEnter(e) {
                                    if (e.key == 'Enter' || e.keyCode == 13) {
                                        e.preventDefault()

                                        hitungRincianBilling()

                                        return false;
                                    }
                                    return true
                                }

                                document.addEventListener('DOMContentLoaded', () => {
                                    hitungRincianBilling();
                                    prosedur_non_bedah.addEventListener('change', (e) => hitungRincianBilling())
                                    prosedur_non_bedah.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_prosedur_non_bedah.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_prosedur_non_bedah.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    prosedur_bedah.addEventListener('change', (e) => hitungRincianBilling())
                                    prosedur_bedah.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_prosedur_bedah.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_prosedur_bedah.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    konsultasi.addEventListener('change', (e) => hitungRincianBilling())
                                    konsultasi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_konsultasi.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_konsultasi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    tenaga_ahli.addEventListener('change', (e) => hitungRincianBilling())
                                    tenaga_ahli.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_tenaga_ahli.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_tenaga_ahli.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    keperawatan.addEventListener('change', (e) => hitungRincianBilling())
                                    keperawatan.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_keperawatan.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_keperawatan.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    penunjang.addEventListener('change', (e) => hitungRincianBilling())
                                    penunjang.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_penunjang.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_penunjang.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    radiologi.addEventListener('change', (e) => hitungRincianBilling())
                                    radiologi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_radiologi.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_radiologi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    laboratorium.addEventListener('change', (e) => hitungRincianBilling())
                                    laboratorium.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_laboratorium.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_laboratorium.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    pelayanan_darah.addEventListener('change', (e) => hitungRincianBilling())
                                    pelayanan_darah.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_pelayanan_darah.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_pelayanan_darah.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    rehabilitasi.addEventListener('change', (e) => hitungRincianBilling())
                                    rehabilitasi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_rehabilitasi.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_rehabilitasi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    kamar.addEventListener('change', (e) => hitungRincianBilling())
                                    kamar.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_kamar.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_kamar.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    rawat_intensif.addEventListener('change', (e) => hitungRincianBilling())
                                    rawat_intensif.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_rawat_intensif.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_rawat_intensif.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    obat.addEventListener('change', (e) => hitungRincianBilling())
                                    obat.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_obat.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_obat.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    obat_kronis.addEventListener('change', (e) => hitungRincianBilling())
                                    obat_kronis.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_obat_kronis.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_obat_kronis.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    obat_kemoterapi.addEventListener('change', (e) => hitungRincianBilling())
                                    obat_kemoterapi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_obat_kemoterapi.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_obat_kemoterapi.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    alkes.addEventListener('change', (e) => hitungRincianBilling())
                                    alkes.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_alkes.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_alkes.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    bmhp.addEventListener('change', (e) => hitungRincianBilling())
                                    bmhp.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_bmhp.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_bmhp.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    sewa_alat.addEventListener('change', (e) => hitungRincianBilling())
                                    sewa_alat.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_sewa_alat.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_sewa_alat.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    tarif_poli_eks.addEventListener('change', (e) => hitungRincianBilling())
                                    tarif_poli_eks.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                    diskon_tarif_poli_eks.addEventListener('change', (e) => hitungRincianBilling())
                                    diskon_tarif_poli_eks.addEventListener('keydown', (e) => janganSubmitSaatEnter(e))
                                })
                            </script>

                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>

                            <?php
                                $diagnosa_idrg = '';
                                $querydiagnosa_idrg = bukaquery("select i.kode_icd10, r.deskripsi, i.urut from idrg_diagnosa_pasien_smc i join idrg_referensi_icd10_smc r on i.kode_icd10 = r.code1 where i.no_sep = '$nosep' order by i.urut asc");
                            ?>
                            <?php while ($barisdiagnosa_idrg = mysqli_fetch_array($querydiagnosa_idrg)): ?>
                                <?php if ($barisdiagnosa_idrg['urut'] == '1'): ?>
                                    <?php $diagnosa_idrg = $barisdiagnosa_idrg['kode_icd10']; ?>
                                    <tr class="head">
                                        <td width="28%">Diagnosa</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisdiagnosa_idrg['kode_icd10'] ?> (UTAMA)<br /><?= $barisdiagnosa_idrg['deskripsi'] ?></td>
                                    </tr>
                                <?php else: ?>
                                    <?php $diagnosa_idrg .= '#'.$barisdiagnosa_idrg['kode_icd10']; ?>
                                    <tr class="head">
                                        <td colspan="2" width="29%"></td>
                                        <td width="70%"><?= $barisdiagnosa_idrg['kode_icd10'] ?><br /><?= $barisdiagnosa_idrg['deskripsi'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php
                                $prosedur_idrg = '';
                                $queryprosedur_idrg = bukaquery("select i.kode_icd9, r.deskripsi, i.multiplicity, i.urut from idrg_prosedur_pasien_smc i join idrg_referensi_icd9cm_smc r on i.kode_icd9 = r.code1 where i.no_sep = '$nosep' order by i.urut asc");
                            ?>
                            <?php while ($barisprosedur_idrg = mysqli_fetch_array($queryprosedur_idrg)): ?>
                                <?php if ($barisprosedur_idrg['urut'] == '1'): ?>
                                    <?php $prosedur_idrg = $barisprosedur_idrg['kode_icd9'].'+'.$barisprosedur_idrg['multiplicity']; ?>
                                    <tr class="head">
                                        <td width="28%">Prosedur</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisprosedur_idrg['kode_icd9'] ?> x <?= $barisprosedur_idrg['multiplicity'] ?> (UTAMA)<br /><?= $barisprosedur_idrg['deskripsi'] ?></td>
                                    </tr>
                                <?php else: ?>
                                    <?php $prosedur_idrg .= '#'.$barisprosedur_idrg['kode_icd9'].'+'.$barisprosedur_idrg['multiplicity']; ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisprosedur_idrg['kode_icd9'] ?> x <?= $barisprosedur_idrg['multiplicity'] ?><br /><?= $barisprosedur_idrg['deskripsi'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $judul = 'SIMPAN DATA KLAIM & GROUPING IDRG'; ?>
                        <?php elseif ($grouper === 'inacbg_stage1'): ?>
                            <tr class="head">
                                <td width="28%">No. SEP</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nosep ?></td>
                            </tr>
                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>
                            <tr class="head">
                                <td colspan="3">
                                    <span style="font-family: Tahoma; font-size: 10pt; font-weight: 700; color: #0c684cff; margin-top: 0.5rem">
                                        Status Grouping IDRG (FINAL)
                                    </span>
                                </td>
                            </tr>
                            <?php $hasilgroupingidrg = mysqli_fetch_assoc(bukaquery("select * from idrg_grouping_smc where no_sep = '$nosep'")); ?>
                            <tr class="head">
                                <td width="28%">MDC Number</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['mdc_number'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">MDC Description</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['mdc_description'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">DRG Code</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['drg_code'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">DRG Description</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['drg_description'] ?></td>
                            </tr>
                            <?php
                                $diagnosa_idrg = '';
                                $querydiagnosa_idrg = bukaquery("select kode_icd10, urut from idrg_diagnosa_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisdiagnosa_idrg = mysqli_fetch_array($querydiagnosa_idrg)): ?>
                                <?php $diagnosa_idrg .= $barisdiagnosa_idrg['kode_icd10'].'#'; ?>
                                <?php if ($barisdiagnosa_idrg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Diagnosa IDRG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisdiagnosa_idrg['kode_icd10'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisdiagnosa_idrg['kode_icd10'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $diagnosa_idrg = mb_substr($diagnosa_idrg, 0, -1); ?>
                            <?php
                                $prosedur_idrg = '';
                                $queryprosedur_idrg = bukaquery("select kode_icd9, multiplicity, urut from idrg_prosedur_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisprosedur_idrg = mysqli_fetch_array($queryprosedur_idrg)): ?>
                                <?php
                                    if ($barisprosedur_idrg['multiplicity'] > 1) {
                                        $prosedur_idrg .= $barisprosedur_idrg['kode_icd9'].'+'.$barisprosedur_idrg['multiplicity'].'#';
                                    } else {
                                        $prosedur_idrg .= $barisprosedur_idrg['kode_icd9'].'#';
                                    }
                                ?>
                                <?php if ($barisprosedur_idrg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Prosedur IDRG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisprosedur_idrg['kode_icd9'] ?> x <?= $barisprosedur_idrg['multiplicity'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisprosedur_idrg['kode_icd9'] ?> x <?= $barisprosedur_idrg['multiplicity'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $prosedur_idrg = mb_substr($prosedur_idrg, 0, -1); ?>
                            <tr class="head">
                                <td colspan="3" width="99%"><a href="<?= "?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&action=reedit&grouper=idrg" ?>">[Edit IDRG]</a></td>
                            </tr>
                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>
                            <tr class="head">
                                <td colspan="3">
                                    <span style="font-family: Tahoma; font-size: 10pt; font-weight: 700; color: #ff1100; margin-top: 0.5rem">
                                        Status Grouping INACBG (Belum Final)
                                    </span>
                                </td>
                            </tr>
                            <?php $hasilgroupinginacbg = mysqli_fetch_assoc(bukaquery("select * from inacbg_grouping_stage12 where no_sep = '$nosep'")); ?>
                            <?php if ($hasilgroupinginacbg): ?>
                                <tr class="head">
                                    <td width="28%">Kode CBG</td>
                                    <td width="1%">:</td>
                                    <td width="70%"><?= $hasilgroupinginacbg['code_cbg'] ?></td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Deskripsi</td>
                                    <td width="1%">:</td>
                                    <td width="70%"><?= $hasilgroupinginacbg['deskripsi'] ?></td>
                                </tr>
                                <tr class="head">
                                    <td width="28%">Total tarif</td>
                                    <td width="1%">:</td>
                                    <td width="70%"><?= $hasilgroupinginacbg['tarif'] ?></td>
                                </tr>
                            <?php endif; ?>
                            <?php
                                $diagnosa_inacbg = '';
                                $querydiagnosa_inacbg = bukaquery("select kode_icd10, urut from inacbg_diagnosa_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisdiagnosa_inacbg = mysqli_fetch_array($querydiagnosa_inacbg)): ?>
                                <?php $diagnosa_inacbg .= $barisdiagnosa_inacbg['kode_icd10'].'#'; ?>
                                <?php if ($barisdiagnosa_inacbg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Diagnosa INACBG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisdiagnosa_inacbg['kode_icd10'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisdiagnosa_inacbg['kode_icd10'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $diagnosa_inacbg = mb_substr($diagnosa_inacbg, 0, -1); ?>
                            <?php
                                $prosedur_inacbg = '';
                                $queryprosedur_inacbg = bukaquery("select kode_icd9, urut from inacbg_prosedur_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisprosedur_inacbg = mysqli_fetch_array($queryprosedur_inacbg)): ?>
                                <?php $prosedur_inacbg .= $barisprosedur_inacbg['kode_icd9'].'#'; ?>
                                <?php if ($barisprosedur_inacbg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Prosedur INACBG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisprosedur_inacbg['kode_icd9'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisprosedur_inacbg['kode_icd9'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $prosedur_inacbg = mb_substr($prosedur_inacbg, 0, -1); ?>
                            <?php $judul = 'GROUPING INACBG & FINALISASI KLAIM'; ?>
                        <?php elseif ($grouper === 'inacbg_stage2'): ?>
                            <tr class="head">
                                <td width="28%">No. SEP</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $nosep ?></td>
                            </tr>
                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>
                            <tr class="head">
                                <td colspan="3">
                                    <span style="font-family: Tahoma; font-size: 10pt; font-weight: 700; color: #0c684cff; margin-top: 0.5rem">
                                        Hasil Grouping IDRG (FINAL)
                                    </span>
                                </td>
                            </tr>
                            <?php $hasilgroupingidrg = mysqli_fetch_assoc(bukaquery("select * from idrg_grouping_smc where no_sep = '$nosep'")); ?>
                            <tr class="head">
                                <td width="28%">MDC Number</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['mdc_number'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">MDC Description</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['mdc_description'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">DRG Code</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['drg_code'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">DRG Description</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupingidrg['drg_description'] ?></td>
                            </tr>
                            <?php
                                $diagnosa_idrg = '';
                                $querydiagnosa_idrg = bukaquery("select kode_icd10, urut from idrg_diagnosa_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisdiagnosa_idrg = mysqli_fetch_array($querydiagnosa_idrg)): ?>
                                <?php $diagnosa_idrg .= $barisdiagnosa_idrg['kode_icd10'].'#'; ?>
                                <?php if ($barisdiagnosa_idrg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Diagnosa IDRG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisdiagnosa_idrg['kode_icd10'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisdiagnosa_idrg['kode_icd10'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $diagnosa_idrg = mb_substr($diagnosa_idrg, 0, -1); ?>
                            <?php
                                $prosedur_idrg = '';
                                $queryprosedur_idrg = bukaquery("select kode_icd9, multiplicity, urut from idrg_prosedur_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisprosedur_idrg = mysqli_fetch_array($queryprosedur_idrg)): ?>
                                <?php
                                    if ($barisprosedur_idrg['multiplicity'] > 1) {
                                        $prosedur_idrg .= $barisprosedur_idrg['kode_icd9'].'+'.$barisprosedur_idrg['multiplicity'].'#';
                                    } else {
                                        $prosedur_idrg .= $barisprosedur_idrg['kode_icd9'].'#';
                                    }
                                ?>
                                <?php if ($barisprosedur_idrg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Prosedur IDRG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisprosedur_idrg['kode_icd9'] ?> x <?= $barisprosedur_idrg['multiplicity'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisprosedur_idrg['kode_icd9'] ?> x <?= $barisprosedur_idrg['multiplicity'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $prosedur_idrg = mb_substr($prosedur_idrg, 0, -1); ?>
                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>
                            <tr class="head">
                                <td colspan="3">
                                    <span style="font-family: Tahoma; font-size: 10pt; font-weight: 700; color: #0c684cff; margin-top: 0.5rem">
                                        Hasil Grouping INACBG (Ada top up)
                                    </span>
                                </td>
                            </tr>
                            <?php $hasilgroupinginacbg = mysqli_fetch_assoc(bukaquery("select * from inacbg_grouping_stage12 where no_sep = '$nosep'")); ?>
                            <tr class="head">
                                <td width="28%">Kode CBG</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupinginacbg['code_cbg'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Deskripsi</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupinginacbg['deskripsi'] ?></td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Total tarif</td>
                                <td width="1%">:</td>
                                <td width="70%"><?= $hasilgroupinginacbg['tarif'] ?></td>
                            </tr>
                            <?php
                                $diagnosa_inacbg = '';
                                $querydiagnosa_inacbg = bukaquery("select kode_icd10, urut from inacbg_diagnosa_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisdiagnosa_inacbg = mysqli_fetch_array($querydiagnosa_inacbg)): ?>
                                <?php $diagnosa_inacbg .= $barisdiagnosa_inacbg['kode_icd10'].'#'; ?>
                                <?php if ($barisdiagnosa_inacbg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Diagnosa INACBG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisdiagnosa_inacbg['kode_icd10'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisdiagnosa_inacbg['kode_icd10'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $diagnosa_inacbg = mb_substr($diagnosa_inacbg, 0, -1); ?>
                            <?php
                                $prosedur_inacbg = '';
                                $queryprosedur_inacbg = bukaquery("select kode_icd9, urut from inacbg_prosedur_pasien_smc where no_sep = '$nosep' order by urut asc");
                            ?>
                            <?php while ($barisprosedur_inacbg = mysqli_fetch_array($queryprosedur_inacbg)): ?>
                                <?php $prosedur_inacbg .= $barisprosedur_inacbg['kode_icd9'].'#'; ?>
                                <?php if ($barisprosedur_inacbg['urut'] == '1'): ?>
                                    <tr class="head">
                                        <td width="28%">Prosedur INACBG</td>
                                        <td width="1%">:</td>
                                        <td width="70%"><?= $barisprosedur_inacbg['kode_icd9'] ?> (UTAMA)</td>
                                    </tr>
                                <?php else: ?>
                                    <tr class="head">
                                        <td colspan="2" width="28%"></td>
                                        <td width="70%"><?= $barisprosedur_inacbg['kode_icd9'] ?></td>
                                    </tr>
                                <?php endif; ?>
                            <?php endwhile; ?>
                            <?php $prosedur_inacbg = mb_substr($prosedur_inacbg, 0, -1); ?>
                            <tr class="head">
                                <td colspan="3" width="99%"><a href="<?= "?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&action=reedit&grouper=inacbg_stage1" ?>">[Edit INACBG]</a></td>
                            </tr>
                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>
                            <?php
                                $data_special_procedure = [];
                                $data_special_prosthesis = [];
                                $data_special_investigation = [];
                                $data_special_drug = [];

                                $querycmg = bukaquery2("select cmg_code, cmg_description, cmg_type from tempinacbg where coder_nik = '$codernik'");

                                while ($bariscmg = mysqli_fetch_assoc($querycmg)) {
                                    if ($bariscmg['cmg_type'] == 'Special Procedure') {
                                        $data_special_procedure[] = $bariscmg;
                                    } else if ($bariscmg['cmg_type'] == 'Special Prosthesis') {
                                        $data_special_prosthesis[] = $bariscmg;
                                    } else if ($bariscmg['cmg_type'] == 'Special Investigation') {
                                        $data_special_investigation[] = $bariscmg;
                                    } else if ($bariscmg['cmg_type'] == 'Special Drug') {
                                        $data_special_drug[] = $bariscmg;
                                    }
                                }
                            ?>
                            <tr class="head">
                                <td width="28%">Special Procedure</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="special_procedure" class="text" style="font-family: Tahoma">
                                        <?php foreach ($data_special_procedure as $data_cmg): ?>
                                            <option value="<?= $data_cmg['cmg_code'] ?>"><?= $data_cmg['cmg_code'].' - '.$data_cmg['cmg_description'] ?></option>
                                        <?php endforeach; ?>
                                        <option value=""></option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Special Prosthesis</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="special_prosthesis" class="text" style="font-family: Tahoma">
                                        <?php foreach ($data_special_prosthesis as $data_cmg): ?>
                                            <option value="<?= $data_cmg['cmg_code'] ?>"><?= $data_cmg['cmg_code'].' - '.$data_cmg['cmg_description'] ?></option>
                                        <?php endforeach; ?>
                                        <option value=""></option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Special Investigation</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="special_investigation" class="text" style="font-family: Tahoma">
                                        <?php foreach ($data_special_investigation as $data_cmg): ?>
                                            <option value="<?= $data_cmg['cmg_code'] ?>"><?= $data_cmg['cmg_code'].' - '.$data_cmg['cmg_description'] ?></option>
                                        <?php endforeach; ?>
                                        <option value=""></option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head">
                                <td width="28%">Special Drug</td>
                                <td width="1%">:</td>
                                <td width="70%">
                                    <select name="special_drug" class="text" style="font-family: Tahoma">
                                        <?php foreach ($data_special_drug as $data_cmg): ?>
                                            <option value="<?= $data_cmg['cmg_code'] ?>"><?= $data_cmg['cmg_code'].' - '.$data_cmg['cmg_description'] ?></option>
                                        <?php endforeach; ?>
                                        <option value=""></option>
                                    </select>
                                </td>
                            </tr>
                            <tr class="head"><td colspan="3" width="98%"><hr style="color: #909090; border-color: inherit"></td></tr>
                            <?php $judul = 'GROUPING INACBG STAGE2 & FINALISASI KLAIM'; ?>
                        <?php endif; ?>
                    </table>
                </div>
            </div>
            <div align="center">
                <input name="BtnSimpan" type="submit" style="padding: 1rem 0.75rem; font-family: Tahoma; font-size: 0.75rem; font-weight: 500; cursor: pointer" value="<?= $judul ?>">
            </div>
            <?php
                $BtnSimpan = $_POST['BtnSimpan'] ?? null;

                if (isset($BtnSimpan)) {
                    if ($grouper === 'idrg') {
                        $validasi = 0;
                        $cara_masuk          = validTeks(trim($_POST['cara_masuk']));
                        $keluar              = validTeks(trim($_POST['keluar']));
                        $kelas_rawat         = validTeks(trim($_POST['kelas_rawat']));
                        $adl_sub_acute       = validTeks(trim($_POST['adl_sub_acute']));
                        $adl_chronic         = validTeks(trim($_POST['adl_chronic']));
                        $icu_indikator       = validTeks(trim($_POST['icu_indikator']));
                        $icu_los             = validTeks(trim($_POST['icu_los']));
                        $ventilator_hour     = validTeks(trim($_POST['ventilator_hour']));
                        $upgrade_class_ind   = validTeks(trim($_POST['upgrade_class_ind']));
                        $upgrade_class_class = validTeks(trim($_POST['upgrade_class_class']));
                        $upgrade_class_los   = validTeks(trim($_POST['upgrade_class_los']));
                        $add_payment_pct     = validTeks(trim($_POST['add_payment_pct']));
                        $birth_weight        = validTeks(trim($_POST['birth_weight']));
                        $discharge_status    = validTeks(trim($_POST['discharge_status']));
                        $sistole             = validTeks(trim($_POST['sistole']));
                        $diastole            = validTeks(trim($_POST['diastole']));
                        $gender              = ($jk == 'L') ? '1' : '2';

                        $prosedur_non_bedah        = validTeks(trim($_POST['prosedur_non_bedah']));
                        $diskon_prosedur_non_bedah = validTeks(trim($_POST['diskon_prosedur_non_bedah']));
                        $prosedur_bedah            = validTeks(trim($_POST['prosedur_bedah']));
                        $diskon_prosedur_bedah     = validTeks(trim($_POST['diskon_prosedur_bedah']));
                        $konsultasi                = validTeks(trim($_POST['konsultasi']));
                        $diskon_konsultasi         = validTeks(trim($_POST['diskon_konsultasi']));
                        $tenaga_ahli               = validTeks(trim($_POST['tenaga_ahli']));
                        $diskon_tenaga_ahli        = validTeks(trim($_POST['diskon_tenaga_ahli']));
                        $keperawatan               = validTeks(trim($_POST['keperawatan']));
                        $diskon_keperawatan        = validTeks(trim($_POST['diskon_keperawatan']));
                        $penunjang                 = validTeks(trim($_POST['penunjang']));
                        $diskon_penunjang          = validTeks(trim($_POST['diskon_penunjang']));
                        $radiologi                 = validTeks(trim($_POST['radiologi']));
                        $diskon_radiologi          = validTeks(trim($_POST['diskon_radiologi']));
                        $laboratorium              = validTeks(trim($_POST['laboratorium']));
                        $diskon_laboratorium       = validTeks(trim($_POST['diskon_laboratorium']));
                        $pelayanan_darah           = validTeks(trim($_POST['pelayanan_darah']));
                        $diskon_pelayanan_darah    = validTeks(trim($_POST['diskon_pelayanan_darah']));
                        $rehabilitasi              = validTeks(trim($_POST['rehabilitasi']));
                        $diskon_rehabilitasi       = validTeks(trim($_POST['diskon_rehabilitasi']));
                        $kamar                     = validTeks(trim($_POST['kamar']));
                        $diskon_kamar              = validTeks(trim($_POST['diskon_kamar']));
                        $rawat_intensif            = validTeks(trim($_POST['rawat_intensif']));
                        $diskon_rawat_intensif     = validTeks(trim($_POST['diskon_rawat_intensif']));
                        $obat                      = validTeks(trim($_POST['obat']));
                        $diskon_obat               = validTeks(trim($_POST['diskon_obat']));
                        $obat_kronis               = validTeks(trim($_POST['obat_kronis']));
                        $diskon_obat_kronis        = validTeks(trim($_POST['diskon_obat_kronis']));
                        $obat_kemoterapi           = validTeks(trim($_POST['obat_kemoterapi']));
                        $diskon_obat_kemoterapi    = validTeks(trim($_POST['diskon_obat_kemoterapi']));
                        $alkes                     = validTeks(trim($_POST['alkes']));
                        $diskon_alkes              = validTeks(trim($_POST['diskon_alkes']));
                        $bmhp                      = validTeks(trim($_POST['bmhp']));
                        $diskon_bmhp               = validTeks(trim($_POST['diskon_bmhp']));
                        $sewa_alat                 = validTeks(trim($_POST['sewa_alat']));
                        $diskon_sewa_alat          = validTeks(trim($_POST['diskon_sewa_alat']));
                        $tarif_poli_eks            = validTeks(trim($_POST['tarif_poli_eks']));
                        $diskon_tarif_poli_eks     = validTeks(trim($_POST['diskon_tarif_poli_eks']));
                        $dializer_single_use       = validTeks(trim($_POST['dializer_single_use']));

                        $totalbillingsementara
                            = ($prosedur_non_bedah - $diskon_prosedur_non_bedah)
                            + ($prosedur_bedah - $diskon_prosedur_bedah)
                            + ($konsultasi - $diskon_konsultasi)
                            + ($tenaga_ahli - $diskon_tenaga_ahli)
                            + ($keperawatan - $diskon_keperawatan)
                            + ($penunjang - $diskon_penunjang)
                            + ($radiologi - $diskon_radiologi)
                            + ($laboratorium - $diskon_laboratorium)
                            + ($pelayanan_darah - $diskon_pelayanan_darah)
                            + ($rehabilitasi - $diskon_rehabilitasi)
                            + ($kamar - $diskon_kamar)
                            + ($rawat_intensif - $diskon_rawat_intensif)
                            + ($obat - $diskon_obat)
                            + ($obat_kronis - $diskon_obat_kronis)
                            + ($obat_kemoterapi - $diskon_obat_kemoterapi)
                            + ($alkes - $diskon_alkes)
                            + ($bmhp - $diskon_bmhp)
                            + ($sewa_alat - $diskon_sewa_alat)
                            + ($tarif_poli_eks - $diskon_tarif_poli_eks);

                        $validasi = $totalbilling - $totalbillingsementara;

                        if ((int) round($validasi) === 0) {
                            if ($corona == 'PasienCorona') {
                                echo "Bridging klaim INACBG untuk Pasien Covid-19 belum support!";
                            } else {
                                if ((!empty($norawat)) && (!empty($nosep)) && (!empty($nokartu))) {
                                    ['success' => $success, 'data' => $response, 'error' => $error] = BuatKlaimBaruSmc($nokartu, $nosep, $no_rkm_medis, $nm_pasien, $tgl_lahir." 00:00:00", $gender, $norawat);
                                    if ($success === true) {
                                        ['success' => $success, 'data' => $response, 'error' => $error] = UpdateDataKlaimSmc(
                                            $nosep, $nokartu, $tgl_registrasi, $keluar, $jnsrawat, $kelas_rawat, $adl_sub_acute,
                                            $adl_chronic, $icu_indikator, $icu_los, $ventilator_hour, $upgrade_class_ind, $upgrade_class_class,
                                            $upgrade_class_los, $add_payment_pct, $birth_weight, $discharge_status, $tarif_poli_eks,
                                            $cara_masuk, $nm_dokter, getKelasRS(), "3", "JKN", "#", $codernik, $prosedur_non_bedah,
                                            $prosedur_bedah, $konsultasi, $tenaga_ahli, $keperawatan, $penunjang, $radiologi,
                                            $laboratorium, $pelayanan_darah, $rehabilitasi, $kamar, $rawat_intensif, $obat,
                                            $obat_kronis, $obat_kemoterapi, $alkes, $bmhp, $sewa_alat, $sistole, $diastole,
                                            $dializer_single_use
                                        );
                                    }

                                    if ($success === true) {
                                        $set_diagnosa = SetDiagnosaIdrgSmc($nosep, $diagnosa_idrg);
                                        $set_prosedur = SetProsedurIdrgSmc($nosep, $prosedur_idrg);

                                        if ($set_diagnosa['success'] === false) {
                                            return $set_diagnosa;
                                        }

                                        if ($set_prosedur['success'] === false) {
                                            return $set_prosedur;
                                        }

                                        ['success' => $success, 'data' => $response, 'error' => $error] = GroupingStage1IdrgSmc($nosep, $norawat, $status_lanjut, $codernik);
                                    }

                                    if ($success === false) {
                                        echo $error;
                                        echo <<<HTML
                                            <meta http-equiv="refresh" content="5;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=false&grouper=idrg">
                                            HTML;
                                    } else {
                                        echo <<<HTML
                                            <meta http-equiv="refresh" content="1;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=true&grouper=inacbg_stage1">
                                            HTML;
                                    }
                                } else {
                                    echo 'Semua field harus isi..!!!';
                                }
                            }
                        } else {
                            echo 'Total billing tidak sesuai dengan billing pasien!';
                        }
                    } else if ($grouper === 'inacbg_stage1') {
                        ['success' => $success, 'data' => $response, 'error' => $error] = GroupingStage1InacbgSmc($nosep, $diagnosa_inacbg, $prosedur_inacbg, $codernik);
                        if ($success === true) {
                            if ($response === 'inacbg_stage2') {
                                echo <<<HTML
                                    <meta http-equiv="refresh" content="2;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=true&grouper=inacbg_stage2">
                                    HTML;
                            } else {
                                echo <<<HTML
                                    <meta http-equiv="refresh" content="2;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=true&action=selesai">
                                    HTML;
                            }
                        } else {
                            echo $error;
                            echo <<<HTML
                                <meta http-equiv="refresh" content="5;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=false&grouper=inacbg_stage1">
                                HTML;
                        }
                    } else if ($grouper === 'inacbg_stage2') {
                        $special_procedure     = isset($_POST['special_procedure']) ? validTeks(trim($_POST['special_procedure'])) : '';
                        $special_prosthesis    = isset($_POST['special_prosthesis']) ? validTeks(trim($_POST['special_prosthesis'])) : '';
                        $special_investigation = isset($_POST['special_investigation']) ? validTeks(trim($_POST['special_investigation'])) : '';
                        $special_drug          = isset($_POST['special_drug']) ? validTeks(trim($_POST['special_drug'])) : '';

                        $special_cmg = implode('#', array_filter([
                            $special_procedure,
                            $special_prosthesis,
                            $special_investigation,
                            $special_drug,
                        ]));

                        ['success' => $success, 'data' => $response, 'error' => $error] = GroupingStage2InacbgSmc($nosep, $codernik, $special_cmg);

                        if (! $success) {
                            echo $error;
                            echo <<<HTML
                                <meta http-equiv="refresh" content="2;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=false&grouper=inacbg_stage2">
                                HTML;
                        } else {
                            echo <<<HTML
                                <meta http-equiv="refresh" content="2;URL=?act=DetailKirimSmc&codernik={$codernik}&nosep={$nosep}&carabayar={$carabayar}&corona={$corona}&sukses=true&action=selesai">
                                HTML;
                        }
                    }
                }
            ?>
        </form>
    <?php endif; ?>
</div>
