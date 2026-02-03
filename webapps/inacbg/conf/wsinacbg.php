<?php
    if(strpos($_SERVER['REQUEST_URI'],"conf")){
        exit(header("Location:../index.php"));
    }
    require_once('../conf/conf.php');

    function getKey()
    {
       $keyRS = "";

       if (empty($keyRS)) {
            throw new \Exception("Key belum ada!");
       }

       return $keyRS;
    }

    function getUrlWS()
    {
        $UrlWS = "http://localhost/E-Klaim/ws.php";
        return $UrlWS;
    }

    function getKelasRS()
    {
        $kelasRS = "";

        if (empty($kelasRS)) {
            throw new Exception("Kelas RS belum ada!");
        }

        return $kelasRS;
    }

    function mc_encrypt($data, $strkey) {
        $key = hex2bin($strkey);
        if (mb_strlen($key, "8bit") !== 32) {
                throw new Exception("Needs a 256-bit key!");
        }

        $iv_size = openssl_cipher_iv_length("aes-256-cbc");
        $iv = openssl_random_pseudo_bytes($iv_size);
        $encrypted = openssl_encrypt($data,"aes-256-cbc",$key,OPENSSL_RAW_DATA,$iv );
        $signature = mb_substr(hash_hmac("sha256",$encrypted,$key,true),0,10,"8bit");
        $encoded = chunk_split(base64_encode($signature.$iv.$encrypted));
        return $encoded;
    }

    function mc_decrypt($str, $strkey){
        $key = hex2bin($strkey);
        if (mb_strlen($key, "8bit") !== 32) {
            throw new Exception("Needs a 256-bit key!");
        }

        $iv_size = openssl_cipher_iv_length("aes-256-cbc");
        $decoded = base64_decode($str);
        $signature = mb_substr($decoded,0,10,"8bit");
        $iv = mb_substr($decoded,10,$iv_size,"8bit");
        $encrypted = mb_substr($decoded,$iv_size+10,NULL,"8bit");
        $calc_signature = mb_substr(hash_hmac("sha256",$encrypted,$key,true),0,10,"8bit");
        if(!mc_compare($signature,$calc_signature)) {
            return "SIGNATURE_NOT_MATCH";
        }

        $decrypted = openssl_decrypt($encrypted,"aes-256-cbc",$key,OPENSSL_RAW_DATA,$iv);
        return $decrypted;
    }

    function mc_compare($a, $b) {
        if (strlen($a) !== strlen($b)) {
            return false;
        }

        $result = 0;

        for($i = 0; $i < strlen($a); $i ++) {
            $result |= ord($a[$i]) ^ ord($b[$i]);
        }

        return $result == 0;
    }

    function GenerateNomorCovid(){
        $nomor="";
        $request ='{
                        "metadata": {
                            "method": "generate_claim_number"
                        },
                        "data": {
                            "payor_id": "71"
                        }
                    }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            $nomor=$msg['response']['claim_number'];
        }
        return $nomor;
    }

    function BuatKlaimBaru($nomor_kartu,$nomor_sep,$nomor_rm,$nama_pasien,$tgl_lahir,$gender){
        $request ='{
                        "metadata":{
                            "method":"new_claim"
                        },
                        "data":{
                            "nomor_kartu":"'.$nomor_kartu.'",
                            "nomor_sep":"'.$nomor_sep.'",
                            "nomor_rm":"'.$nomor_rm.'",
                            "nama_pasien":"'.$nama_pasien.'",
                            "tgl_lahir":"'.$tgl_lahir.'",
                            "gender":"'.$gender.'"
                        }
                    }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_klaim_baru","no_sep='".$nomor_sep."'");
            InsertData2("inacbg_klaim_baru","'".$nomor_sep."','".$msg['response']['patient_id']."','".$msg['response']['admission_id']."','".$msg['response']['hospital_admission_id']."'");
        }else{
            echo "\n<br>Respon Klaim Baru : ".$msg['metadata']['message'];
        }
        return $msg['metadata']['message'];
    }

    function BuatKlaimBaruInternal($nomor_kartu,$nomor_sep,$nomor_rm,$nama_pasien,$tgl_lahir,$gender){
        $request ='{
                        "metadata":{
                            "method":"new_claim"
                        },
                        "data":{
                            "nomor_kartu":"'.$nomor_kartu.'",
                            "nomor_sep":"'.$nomor_sep.'",
                            "nomor_rm":"'.$nomor_rm.'",
                            "nama_pasien":"'.$nama_pasien.'",
                            "tgl_lahir":"'.$tgl_lahir.'",
                            "gender":"'.$gender.'"
                        }
                    }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_klaim_baru_internal","no_sep='".$nomor_sep."'");
            InsertData2("inacbg_klaim_baru_internal","'".$nomor_sep."','".$msg['response']['patient_id']."','".$msg['response']['admission_id']."','".$msg['response']['hospital_admission_id']."'");
        }else{
            echo "\n<br>Respon Klaim Internal : ".$msg['metadata']['message'];
        }
        return $msg['metadata']['message'];
    }

    function BuatKlaimBaru2($nomor_kartu,$nomor_sep,$nomor_rm,$nama_pasien,$tgl_lahir,$gender,$norawat){
        $request ='{
                        "metadata":{
                            "method":"new_claim"
                        },
                        "data":{
                            "nomor_kartu":"'.$nomor_kartu.'",
                            "nomor_sep":"'.$nomor_sep.'",
                            "nomor_rm":"'.$nomor_rm.'",
                            "nama_pasien":"'.$nama_pasien.'",
                            "tgl_lahir":"'.$tgl_lahir.'",
                            "gender":"'.$gender.'"
                        }
                    }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_klaim_baru2","no_sep='".$nomor_sep."'");
            InsertData2("inacbg_klaim_baru2","'".$norawat."','".$nomor_sep."','".$msg['response']['patient_id']."','".$msg['response']['admission_id']."','".$msg['response']['hospital_admission_id']."'");
        }else{
            echo "\n<br>Respon Klaim Baru : ".$msg['metadata']['message'];
        }
        return $msg['metadata']['message'];
    }

    function UpdateDataPasien($nomor_rmlama,$nomor_kartu,$nomor_rm,$nama_pasien,$tgl_lahir,$gender){
        $request ='{
                        "metadata": {
                            "method": "update_patient",
                            "nomor_rm": "'.$nomor_rmlama.'"
                        },
                        "data": {
                            "nomor_kartu": "'.$nomor_kartu.'",
                            "nomor_rm": "'.$nomor_rm.'",
                            "nama_pasien": "'.$nama_pasien.'",
                            "tgl_lahir": "'.$tgl_lahir.'",
                            "gender": "'.$gender.'"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Update Data Pasien : ".$msg['metadata']['message'];
    }

    function HapusDataPasien($nomor_rm,$coder_nik){
        $request ='{
                        "metadata": {
                            "method": "delete_patient"
                        },
                        "data": {
                            "nomor_rm": "'.$nomor_rm.'",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Hapus Data Pasien : ".$msg['metadata']['message'];
    }

    function UpdateDataKlaim($nomor_sep,$nomor_kartu,$tgl_masuk,$tgl_pulang,$jenis_rawat,$kelas_rawat,$adl_sub_acute,
                            $adl_chronic,$icu_indikator,$icu_los,$ventilator_hour,$upgrade_class_ind,$upgrade_class_class,
                            $upgrade_class_los,$add_payment_pct,$birth_weight,$discharge_status,$diagnosa,$procedure,$diagnosainacbg,$procedureinacbg,
                            $tarif_poli_eks,$nama_dokter,$kode_tarif,$payor_id,$payor_cd,$cob_cd,$coder_nik,$norawat,$sistole,$diastole,$asalrujukan){

        $prosedur_non_bedah=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Dokter Paramedis' and nm_perawatan not like '%terapi%'")+
                            getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Dokter Paramedis' and nm_perawatan not like '%terapi%'");
        if($prosedur_non_bedah==""){
            $prosedur_non_bedah="0";
        }
        $prosedur_bedah=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Operasi'");
        if($prosedur_bedah==""){
            $prosedur_bedah="0";
        }
        $konsultasi=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Dokter'")+
                     getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Dokter'"));
        if($konsultasi==""){
            $konsultasi="0";
        }
        $tenaga_ahli=0;
        if($tenaga_ahli==""){
            $tenaga_ahli="0";
        }
        $keperawatan=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Paramedis'")+
                      getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Paramedis'"));
        if($keperawatan==""){
            $keperawatan="0";
        }
        $radiologi=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Radiologi'");
        if($radiologi==""){
            $radiologi="0";
        }
        $laboratorium=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Laborat'");
        if($laboratorium==""){
            $laboratorium="0";
        }
        $kamar=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Kamar'");
        if($kamar==""){
            $kamar="0";
        }
        $obat_kronis=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where nm_perawatan like '%kronis%' and no_rawat='".$norawat."' and status='Obat'");
        $obat_kemoterapi=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where nm_perawatan like '%kemo%' and no_rawat='".$norawat."' and status='Obat'");
        $obat=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Obat'")+
               getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Retur Obat'")+
               getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Resep Pulang'")-$obat_kronis-$obat_kemoterapi);
        if($obat==""){
            $obat="0";
        }
        if($obat_kemoterapi==""){
            $obat_kemoterapi="0";
        }
        if($obat_kronis==""){
            $obat_kronis="0";
        }
        $bmhp=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Tambahan'");
        if($bmhp==""){
            $bmhp="0";
        }
        $sewa_alat=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Harian'")+
                    getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Service'"));
        if($sewa_alat==""){
            $sewa_alat="0";
        }
        $rehabilitasi=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Dokter Paramedis' and nm_perawatan like '%terapi%'")+
                            getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Dokter Paramedis' and nm_perawatan like '%terapi%'");
        if($rehabilitasi==""){
            $rehabilitasi="0";
        }

        $hasilcorona=bukaquery(
                "select pemulasaraan_jenazah,if(pemulasaraan_jenazah='Ya',1,0) as ytpemulasaraan_jenazah,
                kantong_jenazah,if(kantong_jenazah='Ya',1,0) as ytkantong_jenazah,
                peti_jenazah,if(peti_jenazah='Ya',1,0) as ytpeti_jenazah,
                plastik_erat,if(plastik_erat='Ya',1,0) as ytplastik_erat,
                desinfektan_jenazah,if(desinfektan_jenazah='Ya',1,0) as ytdesinfektan_jenazah,
                mobil_jenazah,if(mobil_jenazah='Ya',1,0) as ytmobil_jenazah,
                desinfektan_mobil_jenazah,if(desinfektan_mobil_jenazah='Ya',1,0) as ytdesinfektan_mobil_jenazah,
                covid19_status_cd,if(covid19_status_cd='ODP',1,if(covid19_status_cd='PDP',2,3)) as ytcovid19_status_cd,
                nomor_kartu_t, episodes1, episodes2,episodes3, episodes4, episodes5, episodes6,
                covid19_cc_ind,if(covid19_cc_ind='Ya',1,0) as ytcovid19_cc_ind
                from perawatan_corona where no_rawat='".$norawat."'");
        if($bariscorona = mysqli_fetch_array($hasilcorona)) {
            $episodes1 = $bariscorona["episodes1"];
            $episodes2 = $bariscorona["episodes2"];
            $episodes3 = $bariscorona["episodes3"];
            $episodes4 = $bariscorona["episodes4"];
            $episodes5 = $bariscorona["episodes5"];
            $episodes6 = $bariscorona["episodes6"];
            $episodes  = ($episodes1==0?"":"1;$episodes1#").($episodes2==0?"":"2;$episodes2#").($episodes3==0?"":"3;$episodes3#").($episodes4==0?"":"4;$episodes4#").($episodes5==0?"":"5;$episodes5#").($episodes6==0?"":"6;$episodes6#");
            $episodes  = substr($episodes, 0, -1);
            $request ='{
                            "metadata": {
                                "method": "set_claim_data",
                                "nomor_sep": "'.$nomor_sep.'"
                            },
                            "data": {
                                "nomor_sep": "'.$nomor_sep.'",
                                "nomor_kartu": "'.$nomor_kartu.'",
                                "tgl_masuk": "'.$tgl_masuk.'",
                                "tgl_pulang": "'.$tgl_pulang.'",
                                "cara_masuk": "'.$asalrujukan.'",
                                "jenis_rawat": "'.$jenis_rawat.'",
                                "kelas_rawat": "'.$kelas_rawat.'",
                                "adl_sub_acute": "'.$adl_sub_acute.'",
                                "adl_chronic": "'.$adl_chronic.'",
                                "icu_indikator": "'.$icu_indikator.'",
                                "icu_los": "'.$icu_los.'",
                                "ventilator_hour": "'.$ventilator_hour.'",
                                "upgrade_class_ind": "'.$upgrade_class_ind.'",
                                "upgrade_class_class": "'.$upgrade_class_class.'",
                                "upgrade_class_los": "'.$upgrade_class_los.'",
                                "add_payment_pct": "'.$add_payment_pct.'",
                                "birth_weight": "'.$birth_weight.'",
                                "sistole": '.$sistole.',
                                "diastole": '.$diastole.',
                                "discharge_status": "'.$discharge_status.'",
                                "tarif_rs": {
                                    "prosedur_non_bedah": "'.$prosedur_non_bedah.'",
                                    "prosedur_bedah": "'.$prosedur_bedah.'",
                                    "konsultasi": "'.$konsultasi.'",
                                    "tenaga_ahli": "'.$tenaga_ahli.'",
                                    "keperawatan": "'.$keperawatan.'",
                                    "penunjang": "0",
                                    "radiologi": "'.$radiologi.'",
                                    "laboratorium": "'.$laboratorium.'",
                                    "pelayanan_darah": "0",
                                    "rehabilitasi": "'.$rehabilitasi.'",
                                    "kamar": "'.($kamar+$tarif_poli_eks).'",
                                    "rawat_intensif": "0",
                                    "obat": "'.$obat.'",
                                    "obat_kronis": "'.$obat_kronis.'",
                                    "obat_kemoterapi": "'.$obat_kemoterapi.'",
                                    "alkes": "0",
                                    "bmhp": "'.$bmhp.'",
                                    "sewa_alat": "'.$sewa_alat.'"
                                 },
                                "pemulasaraan_jenazah": "'.$bariscorona["ytpemulasaraan_jenazah"].'",
                                "kantong_jenazah": "'.$bariscorona["ytkantong_jenazah"].'",
                                "peti_jenazah": "'.$bariscorona["ytpeti_jenazah"].'",
                                "plastik_erat": "'.$bariscorona["ytplastik_erat"].'",
                                "desinfektan_jenazah": "'.$bariscorona["ytdesinfektan_jenazah"].'",
                                "mobil_jenazah": "'.$bariscorona["ytmobil_jenazah"].'",
                                "desinfektan_mobil_jenazah": "'.$bariscorona["ytdesinfektan_mobil_jenazah"].'",
                                "covid19_status_cd": "'.$bariscorona["ytcovid19_status_cd"].'",
                                "nomor_kartu_t": "'.$bariscorona["nomor_kartu_t"].'",
                                "episodes": "'.$episodes.'",
                                "covid19_cc_ind": "'.$bariscorona["ytcovid19_cc_ind"].'",
                                "tarif_poli_eks": "'.$tarif_poli_eks.'",
                                "nama_dokter": "'.$nama_dokter.'",
                                "kode_tarif": "'.$kode_tarif.'",
                                "payor_id": "71",
                                "payor_cd": "JAMINAN COVID-19",
                                "cob_cd": "'.$cob_cd.'",
                                "coder_nik": "'.$coder_nik.'"
                            }
                       }';
        }else{
            $request ='{
                            "metadata": {
                                "method": "set_claim_data",
                                "nomor_sep": "'.$nomor_sep.'"
                            },
                            "data": {
                                "nomor_sep": "'.$nomor_sep.'",
                                "nomor_kartu": "'.$nomor_kartu.'",
                                "tgl_masuk": "'.$tgl_masuk.'",
                                "tgl_pulang": "'.$tgl_pulang.'",
                                "jenis_rawat": "'.$jenis_rawat.'",
                                "kelas_rawat": "'.$kelas_rawat.'",
                                "adl_sub_acute": "'.$adl_sub_acute.'",
                                "adl_chronic": "'.$adl_chronic.'",
                                "icu_indikator": "'.$icu_indikator.'",
                                "icu_los": "'.$icu_los.'",
                                "ventilator_hour": "'.$ventilator_hour.'",
                                "upgrade_class_ind": "'.$upgrade_class_ind.'",
                                "upgrade_class_class": "'.$upgrade_class_class.'",
                                "upgrade_class_los": "'.$upgrade_class_los.'",
                                "add_payment_pct": "'.$add_payment_pct.'",
                                "birth_weight": "'.$birth_weight.'",
                                "sistole": '.$sistole.',
                                "diastole": '.$diastole.',
                                "discharge_status": "'.$discharge_status.'",
                                "tarif_rs": {
                                    "prosedur_non_bedah": "'.$prosedur_non_bedah.'",
                                    "prosedur_bedah": "'.$prosedur_bedah.'",
                                    "konsultasi": "'.$konsultasi.'",
                                    "tenaga_ahli": "'.$tenaga_ahli.'",
                                    "keperawatan": "'.$keperawatan.'",
                                    "penunjang": "0",
                                    "radiologi": "'.$radiologi.'",
                                    "laboratorium": "'.$laboratorium.'",
                                    "pelayanan_darah": "0",
                                    "rehabilitasi": "0",
                                    "kamar": "'.($kamar+$tarif_poli_eks).'",
                                    "rawat_intensif": "0",
                                    "obat": "'.$obat.'",
                                    "obat_kronis": "'.$obat_kronis.'",
                                    "obat_kemoterapi": "'.$obat_kemoterapi.'",
                                    "alkes": "0",
                                    "bmhp": "'.$bmhp.'",
                                    "sewa_alat": "'.$sewa_alat.'"
                                 },
                                "tarif_poli_eks": "0",
                                "nama_dokter": "'.$nama_dokter.'",
                                "kode_tarif": "'.$kode_tarif.'",
                                "payor_id": "3",
                                "payor_cd": "JKN",
                                "cob_cd": "'.$cob_cd.'",
                                "coder_nik": "'.$coder_nik.'"
                            }
                       }';
        }

        //echo "Data : ".$request;
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_data_terkirim", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_data_terkirim","'".$nomor_sep."','".$coder_nik."'");
            SetDiagnosaDRG($nomor_sep, $diagnosa);
            SetProsedurDRG($nomor_sep, $procedure);
            if(GroupingDRG($nomor_sep)=="Ok"){
                InacBGToDRG($nomor_sep,$diagnosainacbg,$procedureinacbg);
                GroupingStage1($nomor_sep,$coder_nik);
            }
        }else{
            echo "\n<br>Respon Update Klaim : ".$msg['metadata']['message'];
        }
    }

    function UpdateDataKlaimInternal($nomor_sep,$nomor_kartu,$tgl_masuk,$tgl_pulang,$jenis_rawat,$kelas_rawat,$adl_sub_acute,
                            $adl_chronic,$icu_indikator,$icu_los,$ventilator_hour,$upgrade_class_ind,$upgrade_class_class,
                            $upgrade_class_los,$add_payment_pct,$birth_weight,$discharge_status,$diagnosa,$procedure,$diagnosainacbg,$procedureinacbg,
                            $tarif_poli_eks,$nama_dokter,$kode_tarif,$payor_id,$payor_cd,$cob_cd,$coder_nik,$norawat,$sistole,$diastole,$asalrujukan){

        $prosedur_non_bedah=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Dokter Paramedis' and nm_perawatan not like '%terapi%'")+
                            getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Dokter Paramedis' and nm_perawatan not like '%terapi%'");
        if($prosedur_non_bedah==""){
            $prosedur_non_bedah="0";
        }
        $prosedur_bedah=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Operasi'");
        if($prosedur_bedah==""){
            $prosedur_bedah="0";
        }
        $konsultasi=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Dokter'")+
                     getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Dokter'"));
        if($konsultasi==""){
            $konsultasi="0";
        }
        $tenaga_ahli=0;
        if($tenaga_ahli==""){
            $tenaga_ahli="0";
        }
        $keperawatan=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Paramedis'")+
                      getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Paramedis'"));
        if($keperawatan==""){
            $keperawatan="0";
        }
        $radiologi=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Radiologi'");
        if($radiologi==""){
            $radiologi="0";
        }
        $laboratorium=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Laborat'");
        if($laboratorium==""){
            $laboratorium="0";
        }
        $kamar=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Kamar'");
        if($kamar==""){
            $kamar="0";
        }
        $obat_kronis=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where nm_perawatan like '%kronis%' and no_rawat='".$norawat."' and status='Obat'");
        $obat_kemoterapi=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where nm_perawatan like '%kemo%' and no_rawat='".$norawat."' and status='Obat'");
        $obat=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Obat'")+
               getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Retur Obat'")+
               getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Resep Pulang'")-$obat_kronis-$obat_kemoterapi);
        if($obat==""){
            $obat="0";
        }
        if($obat_kemoterapi==""){
            $obat_kemoterapi="0";
        }
        if($obat_kronis==""){
            $obat_kronis="0";
        }
        $bmhp=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Tambahan'");
        if($bmhp==""){
            $bmhp="0";
        }
        $sewa_alat=(getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Harian'")+
                    getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Service'"));
        if($sewa_alat==""){
            $sewa_alat="0";
        }
        $rehabilitasi=getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ralan Dokter Paramedis' and nm_perawatan like '%terapi%'")+
                            getOne("select if(sum(totalbiaya)='','0',sum(totalbiaya)) from billing where no_rawat='".$norawat."' and status='Ranap Dokter Paramedis' and nm_perawatan like '%terapi%'");
        if($rehabilitasi==""){
            $rehabilitasi="0";
        }

        $hasilcorona=bukaquery(
                "select pemulasaraan_jenazah,if(pemulasaraan_jenazah='Ya',1,0) as ytpemulasaraan_jenazah,
                kantong_jenazah,if(kantong_jenazah='Ya',1,0) as ytkantong_jenazah,
                peti_jenazah,if(peti_jenazah='Ya',1,0) as ytpeti_jenazah,
                plastik_erat,if(plastik_erat='Ya',1,0) as ytplastik_erat,
                desinfektan_jenazah,if(desinfektan_jenazah='Ya',1,0) as ytdesinfektan_jenazah,
                mobil_jenazah,if(mobil_jenazah='Ya',1,0) as ytmobil_jenazah,
                desinfektan_mobil_jenazah,if(desinfektan_mobil_jenazah='Ya',1,0) as ytdesinfektan_mobil_jenazah,
                covid19_status_cd,if(covid19_status_cd='ODP',1,if(covid19_status_cd='PDP',2,3)) as ytcovid19_status_cd,
                nomor_kartu_t, episodes1, episodes2,episodes3, episodes4, episodes5, episodes6,
                covid19_cc_ind,if(covid19_cc_ind='Ya',1,0) as ytcovid19_cc_ind
                from perawatan_corona where no_rawat='".$norawat."'");
        if($bariscorona = mysqli_fetch_array($hasilcorona)) {
            $episodes1 = $bariscorona["episodes1"];
            $episodes2 = $bariscorona["episodes2"];
            $episodes3 = $bariscorona["episodes3"];
            $episodes4 = $bariscorona["episodes4"];
            $episodes5 = $bariscorona["episodes5"];
            $episodes6 = $bariscorona["episodes6"];
            $episodes  = ($episodes1==0?"":"1;$episodes1#").($episodes2==0?"":"2;$episodes2#").($episodes3==0?"":"3;$episodes3#").($episodes4==0?"":"4;$episodes4#").($episodes5==0?"":"5;$episodes5#").($episodes6==0?"":"6;$episodes6#");
            $episodes  = substr($episodes, 0, -1);
            $request ='{
                            "metadata": {
                                "method": "set_claim_data",
                                "nomor_sep": "'.$nomor_sep.'"
                            },
                            "data": {
                                "nomor_sep": "'.$nomor_sep.'",
                                "nomor_kartu": "'.$nomor_kartu.'",
                                "tgl_masuk": "'.$tgl_masuk.'",
                                "tgl_pulang": "'.$tgl_pulang.'",
                                "cara_masuk": "'.$asalrujukan.'",
                                "jenis_rawat": "'.$jenis_rawat.'",
                                "kelas_rawat": "'.$kelas_rawat.'",
                                "adl_sub_acute": "'.$adl_sub_acute.'",
                                "adl_chronic": "'.$adl_chronic.'",
                                "icu_indikator": "'.$icu_indikator.'",
                                "icu_los": "'.$icu_los.'",
                                "ventilator_hour": "'.$ventilator_hour.'",
                                "upgrade_class_ind": "'.$upgrade_class_ind.'",
                                "upgrade_class_class": "'.$upgrade_class_class.'",
                                "upgrade_class_los": "'.$upgrade_class_los.'",
                                "add_payment_pct": "'.$add_payment_pct.'",
                                "birth_weight": "'.$birth_weight.'",
                                "sistole": '.$sistole.',
                                "diastole": '.$diastole.',
                                "discharge_status": "'.$discharge_status.'",
                                "tarif_rs": {
                                    "prosedur_non_bedah": "'.$prosedur_non_bedah.'",
                                    "prosedur_bedah": "'.$prosedur_bedah.'",
                                    "konsultasi": "'.$konsultasi.'",
                                    "tenaga_ahli": "'.$tenaga_ahli.'",
                                    "keperawatan": "'.$keperawatan.'",
                                    "penunjang": "0",
                                    "radiologi": "'.$radiologi.'",
                                    "laboratorium": "'.$laboratorium.'",
                                    "pelayanan_darah": "0",
                                    "rehabilitasi": "'.$rehabilitasi.'",
                                    "kamar": "'.($kamar+$tarif_poli_eks).'",
                                    "rawat_intensif": "0",
                                    "obat": "'.$obat.'",
                                    "obat_kronis": "'.$obat_kronis.'",
                                    "obat_kemoterapi": "'.$obat_kemoterapi.'",
                                    "alkes": "0",
                                    "bmhp": "'.$bmhp.'",
                                    "sewa_alat": "'.$sewa_alat.'"
                                 },
                                "pemulasaraan_jenazah": "'.$bariscorona["ytpemulasaraan_jenazah"].'",
                                "kantong_jenazah": "'.$bariscorona["ytkantong_jenazah"].'",
                                "peti_jenazah": "'.$bariscorona["ytpeti_jenazah"].'",
                                "plastik_erat": "'.$bariscorona["ytplastik_erat"].'",
                                "desinfektan_jenazah": "'.$bariscorona["ytdesinfektan_jenazah"].'",
                                "mobil_jenazah": "'.$bariscorona["ytmobil_jenazah"].'",
                                "desinfektan_mobil_jenazah": "'.$bariscorona["ytdesinfektan_mobil_jenazah"].'",
                                "covid19_status_cd": "'.$bariscorona["ytcovid19_status_cd"].'",
                                "nomor_kartu_t": "'.$bariscorona["nomor_kartu_t"].'",
                                "episodes": "'.$episodes.'",
                                "covid19_cc_ind": "'.$bariscorona["ytcovid19_cc_ind"].'",
                                "tarif_poli_eks": "'.$tarif_poli_eks.'",
                                "nama_dokter": "'.$nama_dokter.'",
                                "kode_tarif": "'.$kode_tarif.'",
                                "payor_id": "71",
                                "payor_cd": "JAMINAN COVID-19",
                                "cob_cd": "'.$cob_cd.'",
                                "coder_nik": "'.$coder_nik.'"
                            }
                       }';
        }else{
            $request ='{
                            "metadata": {
                                "method": "set_claim_data",
                                "nomor_sep": "'.$nomor_sep.'"
                            },
                            "data": {
                                "nomor_sep": "'.$nomor_sep.'",
                                "nomor_kartu": "'.$nomor_kartu.'",
                                "tgl_masuk": "'.$tgl_masuk.'",
                                "tgl_pulang": "'.$tgl_pulang.'",
                                "jenis_rawat": "'.$jenis_rawat.'",
                                "kelas_rawat": "'.$kelas_rawat.'",
                                "adl_sub_acute": "'.$adl_sub_acute.'",
                                "adl_chronic": "'.$adl_chronic.'",
                                "icu_indikator": "'.$icu_indikator.'",
                                "icu_los": "'.$icu_los.'",
                                "ventilator_hour": "'.$ventilator_hour.'",
                                "upgrade_class_ind": "'.$upgrade_class_ind.'",
                                "upgrade_class_class": "'.$upgrade_class_class.'",
                                "upgrade_class_los": "'.$upgrade_class_los.'",
                                "add_payment_pct": "'.$add_payment_pct.'",
                                "birth_weight": "'.$birth_weight.'",
                                "sistole": '.$sistole.',
                                "diastole": '.$diastole.',
                                "discharge_status": "'.$discharge_status.'",
                                "tarif_rs": {
                                    "prosedur_non_bedah": "'.$prosedur_non_bedah.'",
                                    "prosedur_bedah": "'.$prosedur_bedah.'",
                                    "konsultasi": "'.$konsultasi.'",
                                    "tenaga_ahli": "'.$tenaga_ahli.'",
                                    "keperawatan": "'.$keperawatan.'",
                                    "penunjang": "0",
                                    "radiologi": "'.$radiologi.'",
                                    "laboratorium": "'.$laboratorium.'",
                                    "pelayanan_darah": "0",
                                    "rehabilitasi": "0",
                                    "kamar": "'.($kamar+$tarif_poli_eks).'",
                                    "rawat_intensif": "0",
                                    "obat": "'.$obat.'",
                                    "obat_kronis": "'.$obat_kronis.'",
                                    "obat_kemoterapi": "'.$obat_kemoterapi.'",
                                    "alkes": "0",
                                    "bmhp": "'.$bmhp.'",
                                    "sewa_alat": "'.$sewa_alat.'"
                                 },
                                "tarif_poli_eks": "0",
                                "nama_dokter": "'.$nama_dokter.'",
                                "kode_tarif": "'.$kode_tarif.'",
                                "payor_id": "3",
                                "payor_cd": "JKN",
                                "cob_cd": "'.$cob_cd.'",
                                "coder_nik": "'.$coder_nik.'"
                            }
                       }';
        }

        //echo "Data : ".$request;
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_data_terkirim_internal", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_data_terkirim_internal","'".$nomor_sep."','".$coder_nik."'");
            SetDiagnosaDRG($nomor_sep, $diagnosa);
            SetProsedurDRG($nomor_sep, $procedure);
            if(GroupingDRG($nomor_sep)=="Ok"){
                InacBGToDRG($nomor_sep,$diagnosainacbg,$procedureinacbg);
                GroupingStage1Internal($nomor_sep,$coder_nik);
            }
        }else{
            echo "\n<br>Respon Update Klaim : ".$msg['metadata']['message'];
        }
    }

    function UpdateDataKlaim2($nomor_sep,$nomor_kartu,$tgl_masuk,$tgl_pulang,$jenis_rawat,$kelas_rawat,$adl_sub_acute,
                            $adl_chronic,$icu_indikator,$icu_los,$ventilator_hour,$upgrade_class_ind,$upgrade_class_class,
                            $upgrade_class_los,$add_payment_pct,$birth_weight,$discharge_status,$diagnosa,$procedure,$diagnosainacbg,$procedureinacbg,
                            $tarif_poli_eks,$nama_dokter,$kode_tarif,$payor_id,$payor_cd,$cob_cd,$coder_nik,
                            $prosedur_non_bedah,$prosedur_bedah,$konsultasi,$tenaga_ahli,$keperawatan,$penunjang,
                            $radiologi,$laboratorium,$pelayanan_darah,$rehabilitasi,$kamar,$rawat_intensif,$obat,
                            $obat_kronis,$obat_kemoterapi,$alkes,$bmhp,$sewa_alat,$sistole,$diastole,$cara_masuk){
        $request ='{
                        "metadata": {
                            "method": "set_claim_data",
                            "nomor_sep": "'.$nomor_sep.'"
                        },
                        "data": {
                            "nomor_sep": "'.$nomor_sep.'",
                            "nomor_kartu": "'.$nomor_kartu.'",
                            "tgl_masuk": "'.$tgl_masuk.'",
                            "tgl_pulang": "'.$tgl_pulang.'",
                            "cara_masuk": "'.$cara_masuk.'",
                            "jenis_rawat": "'.$jenis_rawat.'",
                            "kelas_rawat": "'.$kelas_rawat.'",
                            "adl_sub_acute": "'.$adl_sub_acute.'",
                            "adl_chronic": "'.$adl_chronic.'",
                            "icu_indikator": "'.$icu_indikator.'",
                            "icu_los": "'.$icu_los.'",
                            "ventilator_hour": "'.$ventilator_hour.'",
                            "upgrade_class_ind": "'.$upgrade_class_ind.'",
                            "upgrade_class_class": "'.$upgrade_class_class.'",
                            "upgrade_class_los": "'.$upgrade_class_los.'",
                            "add_payment_pct": "'.$add_payment_pct.'",
                            "birth_weight": "'.$birth_weight.'",
                            "sistole": '.$sistole.',
                            "diastole": '.$diastole.',
                            "discharge_status": "'.$discharge_status.'",
                            "tarif_rs": {
                                "prosedur_non_bedah": "'.$prosedur_non_bedah.'",
                                "prosedur_bedah": "'.$prosedur_bedah.'",
                                "konsultasi": "'.$konsultasi.'",
                                "tenaga_ahli": "'.$tenaga_ahli.'",
                                "keperawatan": "'.$keperawatan.'",
                                "penunjang": "'.$penunjang.'",
                                "radiologi": "'.$radiologi.'",
                                "laboratorium": "'.$laboratorium.'",
                                "pelayanan_darah": "'.$pelayanan_darah.'",
                                "rehabilitasi": "'.$rehabilitasi.'",
                                "kamar": "'.$kamar.'",
                                "rawat_intensif": "'.$rawat_intensif.'",
                                "obat": "'.$obat.'",
                                "obat_kronis": "'.$obat_kronis.'",
                                "obat_kemoterapi": "'.$obat_kemoterapi.'",
                                "alkes": "'.$alkes.'",
                                "bmhp": "'.$bmhp.'",
                                "sewa_alat": "'.$sewa_alat.'"
                             },
                            "tarif_poli_eks": "'.$tarif_poli_eks.'",
                            "nama_dokter": "'.$nama_dokter.'",
                            "kode_tarif": "'.$kode_tarif.'",
                            "payor_id": "'.$payor_id.'",
                            "payor_cd": "'.$payor_cd.'",
                            "cob_cd": "'.$cob_cd.'",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        //echo "Data : ".$request;
        $msg= Request($request);
        $respon="Berhasil";
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_data_terkirim2", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_data_terkirim2","'".$nomor_sep."','".$coder_nik."'");
            SetDiagnosaDRG($nomor_sep, $diagnosa);
            SetProsedurDRG($nomor_sep, $procedure);
            if(GroupingDRG($nomor_sep)=="Ok"){
                InacBGToDRG($nomor_sep,$diagnosainacbg,$procedureinacbg);
                GroupingStage12($nomor_sep,$coder_nik);
            }
            $respon="Berhasil";
        }else{
            $respon="Gagal";
            echo "\n<br>Respon Update Klaim : ".$msg['metadata']['message'];
        }
        return $respon;
    }

    function SetDiagnosaDRG($nomorsep,$diagnosa){
        if($diagnosa!=""){
            $request ='{
                            "metadata": {
                                "method": "idrg_diagnosa_set",
                                "nomor_sep": "'.$nomorsep.'"
                            },
                            "data": {
                                "diagnosa": "#"
                            }
                       }';
            $msg= Request($request);
            $request ='{
                            "metadata": {
                                "method": "idrg_diagnosa_set",
                                "nomor_sep": "'.$nomorsep.'"
                            },
                            "data": {
                                "diagnosa": "'.$diagnosa.'"
                            }
                       }';
            $msg= Request($request);
            echo "\n<br>Respon Set Diagnosa DRG : ".$msg['metadata']['message'];
        }
    }

    function SetProsedurDRG($nomorsep,$prosedur){
        if($prosedur!=""){
            $request ='{
                            "metadata": {
                                "method": "idrg_procedure_set",
                                "nomor_sep": "'.$nomorsep.'"
                            },
                            "data": {
                                "procedure": "#"
                            }
                       }';
            $msg= Request($request);
            $request ='{
                            "metadata": {
                                "method": "idrg_procedure_set",
                                "nomor_sep": "'.$nomorsep.'"
                            },
                            "data": {
                                "procedure": "'.$prosedur.'"
                            }
                       }';
            $msg= Request($request);
            echo "\n<br>Respon Set Prosedur DRG : ".$msg['metadata']['message'];
        }
    }

    function UpdateDataKlaim3($nomor_sep,$nomor_kartu,$tgl_masuk,$tgl_pulang,$jenis_rawat,$kelas_rawat,$adl_sub_acute,
                            $adl_chronic,$icu_indikator,$icu_los,$ventilator_hour,$upgrade_class_ind,$upgrade_class_class,
                            $upgrade_class_los,$add_payment_pct,$birth_weight,$discharge_status,$diagnosa,$procedure,$diagnosainacbg,$procedureinacbg,
                            $tarif_poli_eks,$nama_dokter,$kode_tarif,$payor_id,$payor_cd,$cob_cd,$coder_nik,
                            $prosedur_non_bedah,$prosedur_bedah,$konsultasi,$tenaga_ahli,$keperawatan,$penunjang,
                            $radiologi,$laboratorium,$pelayanan_darah,$rehabilitasi,$kamar,$rawat_intensif,$obat,
                            $obat_kronis,$obat_kemoterapi,$alkes,$bmhp,$sewa_alat,$pemulasaraan_jenazah,$kantong_jenazah,
                            $peti_jenazah,$plastik_erat,$desinfektan_jenazah,$mobil_jenazah,$desinfektan_mobil_jenazah,
                            $covid19_status_cd,$nomor_kartu_t,$episodes,$covid19_cc_ind,$sistole,$diastole,$cara_masuk){
        $request ='{
                        "metadata": {
                            "method": "set_claim_data",
                            "nomor_sep": "'.$nomor_sep.'"
                        },
                        "data": {
                            "nomor_sep": "'.$nomor_sep.'",
                            "nomor_kartu": "'.$nomor_kartu.'",
                            "tgl_masuk": "'.$tgl_masuk.'",
                            "tgl_pulang": "'.$tgl_pulang.'",
                            "cara_masuk": "'.$cara_masuk.'",
                            "jenis_rawat": "'.$jenis_rawat.'",
                            "kelas_rawat": "'.$kelas_rawat.'",
                            "adl_sub_acute": "'.$adl_sub_acute.'",
                            "adl_chronic": "'.$adl_chronic.'",
                            "icu_indikator": "'.$icu_indikator.'",
                            "icu_los": "'.$icu_los.'",
                            "ventilator_hour": "'.$ventilator_hour.'",
                            "upgrade_class_ind": "'.$upgrade_class_ind.'",
                            "upgrade_class_class": "'.$upgrade_class_class.'",
                            "upgrade_class_los": "'.$upgrade_class_los.'",
                            "add_payment_pct": "'.$add_payment_pct.'",
                            "birth_weight": "'.$birth_weight.'",
                            "sistole": '.$sistole.',
                            "diastole": '.$diastole.',
                            "discharge_status": "'.$discharge_status.'",
                            "tarif_rs": {
                                "prosedur_non_bedah": "'.$prosedur_non_bedah.'",
                                "prosedur_bedah": "'.$prosedur_bedah.'",
                                "konsultasi": "'.$konsultasi.'",
                                "tenaga_ahli": "'.$tenaga_ahli.'",
                                "keperawatan": "'.$keperawatan.'",
                                "penunjang": "'.$penunjang.'",
                                "radiologi": "'.$radiologi.'",
                                "laboratorium": "'.$laboratorium.'",
                                "pelayanan_darah": "'.$pelayanan_darah.'",
                                "rehabilitasi": "'.$rehabilitasi.'",
                                "kamar": "'.$kamar.'",
                                "rawat_intensif": "'.$rawat_intensif.'",
                                "obat": "'.$obat.'",
                                "obat_kronis": "'.$obat_kronis.'",
                                "obat_kemoterapi": "'.$obat_kemoterapi.'",
                                "alkes": "'.$alkes.'",
                                "bmhp": "'.$bmhp.'",
                                "sewa_alat": "'.$sewa_alat.'"
                             },
                            "pemulasaraan_jenazah": "'.$pemulasaraan_jenazah.'",
                            "kantong_jenazah": "'.$kantong_jenazah.'",
                            "peti_jenazah": "'.$peti_jenazah.'",
                            "plastik_erat": "'.$plastik_erat.'",
                            "desinfektan_jenazah": "'.$desinfektan_jenazah.'",
                            "mobil_jenazah": "'.$mobil_jenazah.'",
                            "desinfektan_mobil_jenazah": "'.$desinfektan_mobil_jenazah.'",
                            "covid19_status_cd": "'.$covid19_status_cd.'",
                            "nomor_kartu_t": "'.$nomor_kartu_t.'",
                            "episodes": "'.$episodes.'",
                            "covid19_cc_ind": "'.$covid19_cc_ind.'",
                            "tarif_poli_eks": "'.$tarif_poli_eks.'",
                            "nama_dokter": "'.$nama_dokter.'",
                            "kode_tarif": "'.$kode_tarif.'",
                            "payor_id": "'.$payor_id.'",
                            "payor_cd": "'.$payor_cd.'",
                            "cob_cd": "'.$cob_cd.'",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        //echo "Data : ".$request;
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_data_terkirim2", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_data_terkirim2","'".$nomor_sep."','".$coder_nik."'");
            SetDiagnosaDRG($nomor_sep, $diagnosa);
            SetProsedurDRG($nomor_sep, $procedure);
            if(GroupingDRG($nomor_sep)=="Ok"){
                InacBGToDRG($nomor_sep,$diagnosainacbg,$procedureinacbg);
                GroupingStage13($nomor_sep,$coder_nik);
            }
        }else{
            echo "\n<br>Respon Update Klaim : ".$msg['metadata']['message'];
        }
    }

    function UpdateDataProsedur($nomor_sep,$procedure,$coder_nik){
        $request ='{
                        "metadata": {
                            "method": "set_claim_data",
                            "nomor_sep": "'.$nomor_sep.'",
                        },
                        "data": {
                            "procedure": "'.$procedure.'",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Update Prosedur : ".$msg['metadata']['message'];
    }

    function HapusSemuaProsedur($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method": "set_claim_data",
                            "nomor_sep": "'.$nomor_sep.'"
                        },
                        "data": {
                            "procedure_inagrouper": "#",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Hapus Prosedur : ".$msg['metadata']['message'];
    }

    function HapusSemuaDiagnosa($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method": "set_claim_data",
                            "nomor_sep": "'.$nomor_sep.'"
                        },
                        "data": {
                            "diagnosa_inagrouper": "#",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Hapus Diangnosa : ".$msg['metadata']['message'];
    }

    function GroupingDRG($nomor_sep){
        $request ='{
                        "metadata": {
                            "method":"grouper",
                            "stage":"1",
                            "grouper":"idrg"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Grouping DRG : ".$msg['metadata']['message'];
        $pesan="Gagal";
        if($msg['metadata']['message']=="Ok"){
            $pesan=$msg['metadata']['message'];
            $request ='{
                            "metadata": {
                                "method":"idrg_grouper_final"
                            },
                            "data": {
                                "nomor_sep":"'.$nomor_sep.'"
                            }
                       }';
            $msg= Request($request);
            echo "\n<br>Respon Final DRG : ".$msg['metadata']['message'];
        }
        return $pesan;
    }

    function GroupingStage1($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method":"grouper",
                            "stage":"1",
                            "grouper": "inacbg"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_grouping_stage1", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_grouping_stage1","'".$nomor_sep."','".$msg['response_inacbg']['cbg']['code']."','".$msg['response_inacbg']['cbg']['description']."','".validangka($msg['response_inacbg']['tariff'])."'");
            FinalisasiKlaim($nomor_sep,$coder_nik);
        }else{
            echo "\n<br>Respon Grouping Inacbg : ".$msg['metadata']['message'];
        }
    }

    function GroupingStage1Internal($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method":"grouper",
                            "stage":"1",
                            "grouper": "inacbg"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_grouping_stage1_internal", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_grouping_stage1_internal","'".$nomor_sep."','".$msg['response_inacbg']['cbg']['code']."','".$msg['response_inacbg']['cbg']['description']."','".validangka($msg['response_inacbg']['tariff'])."'");
            FinalisasiKlaim($nomor_sep,$coder_nik);
        }else{
            echo "\n<br>Respon Grouping INACBG : ".$msg['metadata']['message'];
        }
    }

    function InacBGToDRG($nomor_sep,$diagnosainacbg,$procedureinacbg){
        $request ='{
                    "metadata": {
                        "method": "idrg_to_inacbg_import"
                    },
                    "data": {
                        "nomor_sep": "'.$nomor_sep.'"
                    }
                }';
        $msg= Request($request);
        echo "\n<br>Respon Import DRG To CBG : ".$msg['metadata']['message'];
        if($msg['metadata']['message']=="Ok"){
            if($diagnosainacbg!=""){
                $request ='{
                                "metadata": {
                                    "method": "inacbg_diagnosa_set",
                                    "nomor_sep": "'.$nomor_sep.'"
                                },
                                "data": {
                                    "diagnosa": "#"
                                }
                           }';
                $msg= Request($request);
                $request ='{
                                "metadata": {
                                    "method": "inacbg_diagnosa_set",
                                    "nomor_sep": "'.$nomor_sep.'"
                                },
                                "data": {
                                    "diagnosa": "'.$diagnosainacbg.'"
                                }
                           }';
                $msg= Request($request);
                echo "\n<br>Respon Set Diagnosa CBG : ".$msg['metadata']['message'];
            }

            if($procedureinacbg!=""){
                $request ='{
                                "metadata": {
                                    "method": "inacbg_procedure_set",
                                    "nomor_sep": "'.$nomor_sep.'"
                                },
                                "data": {
                                    "procedure": "#"
                                }
                           }';
                $msg= Request($request);
                $request ='{
                                "metadata": {
                                    "method": "inacbg_procedure_set",
                                    "nomor_sep": "'.$nomor_sep.'"
                                },
                                "data": {
                                    "procedure": "'.$procedureinacbg.'"
                                }
                           }';
                $msg= Request($request);
                echo "\n<br>Respon set Procedure INACBG : ".$msg['metadata']['message'];
            }
        }
    }

    function GroupingStage12($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method":"grouper",
                            "stage":"1",
                            "grouper": "inacbg"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_grouping_stage12", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_grouping_stage12","'".$nomor_sep."','".$msg['response_inacbg']['cbg']['code']."','".$msg['response_inacbg']['cbg']['description']."','".validangka($msg['response_inacbg']['tariff'])."'");
            FinalisasiKlaim($nomor_sep,$coder_nik);
        }else{
            echo "\n<br>Respon Grouping INACBG : ".$msg['metadata']['message'];
        }
    }

    function GroupingStage13($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method":"grouper",
                            "stage":"1",
                            "grouper": "inacbg"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            Hapus2("inacbg_grouping_stage12", "no_sep='".$nomor_sep."'");
            InsertData2("inacbg_grouping_stage12","'".$nomor_sep."','".$msg['response_inacbg']['cbg']['code']."','".$msg['response_inacbg']['cbg']['description']."','".validangka($msg['response_inacbg']['tariff'])."'");
            FinalisasiKlaim($nomor_sep,$coder_nik);
        }else{
            echo "\n<br>Respon Grouping INACBG : ".$msg['metadata']['message'];
        }
    }

    function GroupingStage2($nomor_sep,$special_cmg){
        $request ='{
                        "metadata": {
                            "method":"grouper",
                            "stage":"2",
                            "grouper": "inacbg"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'",
                            "special_cmg": "'.$special_cmg.'"
                        }
                   }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
        echo "\n<br>Respon Grouping INACBG : ".$msg['metadata']['message'];
    }

    function FinalisasiKlaim($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method":"inacbg_grouper_final"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        $request ='{
                        "metadata": {
                            "method":"claim_final"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'",
                            "coder_nik": "'.$coder_nik.'"
                        }
                   }';
        $msg= Request($request);
        if($msg['metadata']['message']=="Ok"){
            KirimKlaimIndividualKeDC($nomor_sep);
        }else{
            echo "\n<br>Respon Final Klaim INACBG : ".$msg['metadata']['message'];
        }
    }

    function EditUlangKlaim($nomor_sep){
        $request ='{
                        "metadata": {
                            "method":"reedit_claim"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        //echo "\n<br>Respon Edit Ulang Klaim : ".$msg['metadata']['message'];
        $request ='{
                        "metadata": {
                            "method":"idrg_grouper_reedit"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        //echo "\n<br>Respon Edit Ulang Grouping IDRG : ".$msg['metadata']['message'];
        $request ='{
                        "metadata": {
                            "method":"inacbg_grouper_reedit"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        //echo "\n<br>Respon Edit Ulang Grouping INACBG : ".$msg['metadata']['message'];
    }

    function KirimKlaimPeriodeKeDC($start_dt,$stop_dt,$jenis_rawat){
        $request ='{
                        "metadata": {
                            "method":"send_claim"
                        },
                        "data": {
                            "start_dt":"'.$start_dt.'",
                            "stop_dt":"'.$stop_dt.'",
                            "jenis_rawat":"'.$jenis_rawat.'",
                            "date_type":"2"
                        }
                   }';
        $msg= Request($request);
        echo "\n<br>Respon Kirim Ke Data Center : ".$msg['metadata']['message'];
    }

    function KirimKlaimIndividualKeDC($nomor_sep){
        $request ='{
                        "metadata": {
                            "method":"send_claim_individual"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
    }

    function MenarikDataKlaimPeriode($start_dt,$stop_dt,$jenis_rawat){
        $request ='{
                        "metadata": {
                            "method":"pull_claim"
                        },
                        "data": {
                            "start_dt":"'.$start_dt.'",
                            "stop_dt":"'.$stop_dt.'",
                            "jenis_rawat":"'.$jenis_rawat.'"
                        }
                   }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
    }

    function MengambilDataDetailPerklaim($nomor_sep){
        $request ='{
                        "metadata": {
                            "method":"get_claim_data"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
    }

    function MengambilSetatusPerklaim($nomor_sep){
        $request ='{
                        "metadata": {
                            "method":"get_claim_status"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
    }

    function MenghapusKlaim($nomor_sep,$coder_nik){
        $request ='{
                        "metadata": {
                            "method":"delete_claim"
                        },
                        "data": {
                            "nomor_sep":"'.$nomor_sep.'",
                            "coder_nik":"'.$coder_nik.'"
                        }
                  }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
    }

    function CetakKlaim($nomor_sep){
        $request ='{
                        "metadata": {
                            "method": "claim_print"
                        },
                        "data": {
                            "nomor_sep": "'.$nomor_sep.'"
                        }
                   }';
        $msg= Request($request);
        echo $msg['metadata']['message']."";
    }

    function Request($request)
    {
        $json = mc_encrypt($request, getKey());
        $header = ['Content-Type: application/x-www-form-urlencoded'];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, getUrlWS());
        curl_setopt($ch, CURLOPT_HEADER, 0);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        $response = curl_exec($ch);

        $first = strpos($response, "\n") + 1;
        $last = strrpos($response, "\n") - 1;
        $hasilresponse = substr($response, $first, strlen($response) - $first - $last);
        $hasildecrypt = mc_decrypt($hasilresponse, getKey());

        // print_r(['request' => $request, 'response' => $hasildecrypt]);
        // echo "<br /><br />";

        return json_decode($hasildecrypt, true);
    }

    function Get($request)
    {
        $json = mc_encrypt($request, getKey());
        $header = ['Content-Type: application/json', 'Accept: application/json'];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, getUrlWS());
        curl_setopt($ch, CURLOPT_HEADER, 0);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'GET');
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        $response = curl_exec($ch);

        $first = strpos($response, "\n") + 1;
        $last = strrpos($response, "\n") - 1;
        $hasilresponse = substr($response, $first, strlen($response) - $first - $last);
        $hasildecrypt = mc_decrypt($hasilresponse, getKey());

        // print_r(['request' => $request, 'response' => $hasildecrypt]);
        // echo "<br /><br />";

        return json_decode($hasildecrypt, true);
    }

    function BuatKlaimBaruSmc($nomor_kartu, $nomor_sep, $nomor_rm, $nama_pasien, $tgl_lahir, $gender, $norawat)
    {
        $request = [
            'metadata' => [
                'method' => 'new_claim',
            ],
            'data' => [
                'nomor_kartu' => $nomor_kartu,
                'nomor_sep' => $nomor_sep,
                'nomor_rm' => $nomor_rm,
                'nama_pasien' => $nama_pasien,
                'tgl_lahir' => $tgl_lahir,
                'gender' => $gender,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "new_claim": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            return GetDataKlaimSmc($nomor_sep, $norawat, $nomor_kartu, $nomor_rm, $nama_pasien, $tgl_lahir, $gender, $error);
        }

        InsertData2('inacbg_klaim_baru2', sprintf("'%s', '%s', '%s', '%s', '%s'",
            $norawat,
            $nomor_sep,
            $msg['response']['patient_id'],
            $msg['response']['admission_id'],
            $msg['response']['hospital_admission_id']
        ));

        return [
            'success' => true,
            'data' => 'Klaim berhasil disimpan',
            'error' => null,
        ];
    }

    function GetDataKlaimSmc($nomor_sep, $norawat, $nomor_kartu, $nomor_rm, $nama_pasien, $tgl_lahir, $gender, $error_klaim_baru)
    {
        $request = [
            'metadata' => [
                'method' => 'get_claim_data',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Get(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "get_claim_data": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            if ($msg['metadata']['error_no'] == 'E2004' && str_contains($error_klaim_baru, 'E2043')) {
                echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">Tidak dapat mengambil status data klaim, kemungkinan no. SEP dihapus dari eklaim! Lakukan pembuatan klaim baru dari menggunakan no. SEP di eklaim!</span><br /><br />';
            }
            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error_klaim_baru.'</span><br /><br />';
            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        bukaquery2("delete from inacbg_klaim_baru2 where no_rawat = '$norawat'");
        InsertData2('inacbg_klaim_baru2', sprintf("'%s', '%s', '%s', '%s', '%s'",
            $norawat,
            $nomor_sep,
            $msg['response']['data']['patient_id'],
            $msg['response']['data']['admission_id'],
            $msg['response']['data']['hospital_admission_id']
        ));

        UpdateDataPasienSmc($nomor_kartu, $nomor_rm, $nama_pasien, $tgl_lahir, $gender);

        if ($msg['response']['data']['klaim_status_cd'] == 'final') {
            ['success' => $success, 'data' => $response, 'error' => $_error] = ReeditKlaimSmc($nomor_sep, $norawat);
            if ($success === true) {
                ['success' => $success, 'data' => $response, 'error' => $_error] = ReeditIdrgSmc($nomor_sep);
            }
        }

        return [
            'success' => true,
            'data' => 'Klaim berhasil disimpan',
            'error' => null,
        ];
    }

    function UpdateDataPasienSmc($nomor_kartu, $nomor_rm, $nama_pasien, $tgl_lahir, $gender)
    {
        $request = [
            'metadata' => [
                'method' => 'update_patient',
                'nomor_rm' => $nomor_rm,
            ],
            'data' => [
                'nomor_kartu' => $nomor_kartu,
                'nomor_rm' => $nomor_rm,
                'nama_pasien' => $nama_pasien,
                'tgl_lahir' => $tgl_lahir,
                'gender' => $gender,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "update_patient": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        return [
            'success' => true,
            'data' => 'Data pasien berhasil diupdate',
            'error' => null,
        ];
    }

    function ReeditKlaimSmc($nomor_sep, $norawat)
    {
        $request = [
            'metadata' => [
                'method' => 'reedit_claim',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "reedit_claim": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            @bukaquery2("delete from inacbg_klaim_baru2 where no_rawat = '$norawat'");
            @bukaquery2("delete from inacbg_data_terkirim2 where no_sep = '$nomor_sep'");
            @bukaquery2("delete from idrg_grouping_smc where no_sep = '$nomor_sep'");
            @bukaquery2("delete from idrg_klaim_final_smc where no_sep = '$nomor_sep'");
            @bukaquery2("delete from inacbg_diagnosa_pasien_smc where no_sep = '$nomor_sep'");
            @bukaquery2("delete from inacbg_prosedur_pasien_smc where no_sep = '$nomor_sep'");
            @bukaquery2("delete from inacbg_grouping_stage12 where no_sep = '$nomor_sep'");
            @bukaquery2("delete from inacbg_grouping_stage2_smc where no_sep = '$nomor_sep'");
            @bukaquery2("delete from inacbg_klaim_final_smc where no_sep = '$nomor_sep'");
            @bukaquery2("delete from inacbg_cetak_klaim where no_sep = '$nomor_sep'");

            return [
                'success' => false,
                'data' => 'grouper',
                'error' => $error,
            ];
        }

        bukaquery2("delete from inacbg_cetak_klaim where no_sep = '$nomor_sep'");

        return [
            'success' => true,
            'data' => 'Klaim berhasil diedit!',
            'error' => null,
        ];
    }

    function ReeditIdrgSmc($nomor_sep)
    {
        $request = [
            'metadata' => [
                'method' => 'idrg_grouper_reedit',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "idrg_grouper_reedit": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';
        }

        @bukaquery2("delete from idrg_grouping_smc where no_sep = '$nomor_sep'");
        @bukaquery2("delete from idrg_klaim_final_smc where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_diagnosa_pasien_smc where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_prosedur_pasien_smc where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_grouping_stage12 where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_grouping_stage2_smc where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_klaim_final_smc where no_sep = '$nomor_sep'");

        return [
            'success' => true,
            'data' => 'Klaim IDRG berhasil diedit',
            'error' => null,
        ];
    }

    function ReeditInacbgSmc($nomor_sep)
    {
        $request = [
            'metadata' => [
                'method' => 'inacbg_grouper_reedit',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "inacbg_grouper_reedit": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        @bukaquery2("delete from inacbg_grouping_stage2_smc where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_grouping_stage12 where no_sep = '$nomor_sep'");
        @bukaquery2("delete from inacbg_klaim_final_smc where no_sep = '$nomor_sep'");

        return [
            'success' => true,
            'data' => 'Klaim INACBG berhasil diedit',
            'error' => null,
        ];
    }

    function UpdateDataKlaimSmc(
        $nomor_sep, $nomor_kartu, $nomor_rm, $tgl_masuk, $tgl_pulang, $jenis_rawat, $kelas_rawat, $adl_sub_acute, $adl_chronic, $icu_indikator, $icu_los, $ventilator_hour,
        $upgrade_class_ind, $upgrade_class_class, $upgrade_class_los, $add_payment_pct, $birth_weight, $discharge_status, $tarif_poli_eks, $cara_masuk,
        $nama_dokter, $kode_tarif, $payor_id, $payor_cd, $cob_cd, $coder_nik, $prosedur_non_bedah, $prosedur_bedah, $konsultasi, $tenaga_ahli, $keperawatan,
        $penunjang, $radiologi, $laboratorium, $pelayanan_darah, $rehabilitasi, $kamar, $rawat_intensif, $obat, $obat_kronis, $obat_kemoterapi, $alkes, $bmhp,
        $sewa_alat, $sistole, $diastole, $dializer_single_use = "0"
    ) {
        $request = [
            'metadata' => [
                'method' => 'set_claim_data',
                'nomor_sep' => $nomor_sep,
            ],
            'data' => [
                'nomor_sep'           => $nomor_sep,
                'nomor_kartu'         => $nomor_kartu,
                'tgl_masuk'           => $tgl_masuk.' 00:00:01',
                'tgl_pulang'          => $tgl_pulang.' 23:59:59',
                'cara_masuk'          => $cara_masuk,
                'jenis_rawat'         => $jenis_rawat,
                'kelas_rawat'         => $kelas_rawat,
                'adl_sub_acute'       => $adl_sub_acute,
                'adl_chronic'         => $adl_chronic,
                'icu_indikator'       => $icu_indikator,
                'icu_los'             => $icu_los,
                'ventilator_hour'     => $ventilator_hour,
                'upgrade_class_ind'   => $upgrade_class_ind,
                'upgrade_class_class' => $upgrade_class_class,
                'upgrade_class_los'   => $upgrade_class_los,
                'add_payment_pct'     => $add_payment_pct,
                'birth_weight'        => $birth_weight,
                'sistole'             => $sistole,
                'diastole'            => $diastole,
                'discharge_status'    => $discharge_status,
                'dializer_single_use' => $dializer_single_use,
                'tarif_rs'            => [
                    'prosedur_non_bedah' => $prosedur_non_bedah,
                    'prosedur_bedah'     => $prosedur_bedah,
                    'konsultasi'         => $konsultasi,
                    'tenaga_ahli'        => $tenaga_ahli,
                    'keperawatan'        => $keperawatan,
                    'penunjang'          => $penunjang,
                    'radiologi'          => $radiologi,
                    'laboratorium'       => $laboratorium,
                    'pelayanan_darah'    => $pelayanan_darah,
                    'rehabilitasi'       => $rehabilitasi,
                    'kamar'              => $kamar,
                    'rawat_intensif'     => $rawat_intensif,
                    'obat'               => $obat,
                    'obat_kronis'        => $obat_kronis,
                    'obat_kemoterapi'    => $obat_kemoterapi,
                    'alkes'              => $alkes,
                    'bmhp'               => $bmhp,
                    'sewa_alat'          => $sewa_alat,
                ],
                'tarif_poli_eks' => $tarif_poli_eks,
                'nama_dokter'    => $nama_dokter,
                'kode_tarif'     => $kode_tarif,
                'payor_id'       => $payor_id,
                'payor_cd'       => $payor_cd,
                'cob_cd'         => $cob_cd,
                'coder_nik'      => $coder_nik,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "set_claim_data": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        bukaquery2("delete from inacbg_data_terkirim2 where no_sep = '$nomor_sep'");
        InsertData2('inacbg_data_terkirim2', "'$nomor_sep', '$coder_nik'");

        return [
            'success' => true,
            'data' => 'Data klaim berhasil disimpan!',
            'error' => null,
        ];
    }

    function ValidasiRegistrasiSITBSmc($nomor_sep, $nomor_rm, $nomor_sitb)
    {
        $request = [
            'metadata' => [
                'method' => 'sitb_validate',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
                'nomor_register_sitb' => $nomor_sitb,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "sitb_validate": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        bukaquery2(sprintf("insert into inacbg_pasien_tb_smc values ('%s', '%s', '%s', '%s') on duplicate key update no_rkm_medis = values(no_rkm_medis), no_sitb = values(no_sitb), status_validasi = values(status_validasi)",
            $nomor_sep, $nomor_rm, $nomor_sitb, $msg['response']['status'].' - '.$msg['response']['detail']
        ));

        return [
            'success' => true,
            'data' => $msg['response']['status'].' - '.$msg['response']['detail'],
            'error' => null,
        ];
    }

    function SetDiagnosaIdrgSmc($nomor_sep, $diagnosa)
    {
        $request = [
            'metadata' => [
                'method' => 'idrg_diagnosa_set',
                'nomor_sep' => $nomor_sep,
            ],
            'data' => [
                'diagnosa' => $diagnosa,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "idrg_diagnosa_set": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        return [
            'success' => true,
            'data' => 'Diagnosa IDRG berhasil disimpan!',
            'error' => null,
        ];
    }

    function SetProsedurIdrgSmc($nomor_sep, $prosedur)
    {
        if (empty($prosedur)) {
            $prosedur = '#';
        }
        $request = [
            'metadata' => [
                'method' => 'idrg_procedure_set',
                'nomor_sep' => $nomor_sep,
            ],
            'data' => [
                'procedure' => $prosedur,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "idrg_procedure_set": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        return [
            'success' => true,
            'data' => 'Prosedur IDRG berhasil disimpan!',
            'error' => null,
        ];
    }

    function GroupingStage1IdrgSmc($nomor_sep, $norawat, $status_rawat, $coder_nik)
    {
        $request = [
            'metadata' => [
                'method' => 'grouper',
                'stage' => '1',
                'grouper' => 'idrg',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ]
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "grouper idrg": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        try {
            bukaquery2(sprintf(
                "insert into idrg_grouping_smc values ('%s', '%s', '%s', '%s', '%s') on duplicate key update mdc_number = values(mdc_number), mdc_description = values(mdc_description), drg_code = values(drg_code), drg_description = values(drg_description)",
                $nomor_sep,
                $msg['response_idrg']['mdc_number'],
                $msg['response_idrg']['mdc_description'],
                $msg['response_idrg']['drg_code'],
                $msg['response_idrg']['drg_description']
            ));
        } catch (\Exception $e) {
            return [
                'success' => false,
                'data' => null,
                'error' => $e,
            ];
        }

        if ($msg['response_idrg']['mdc_number'] == '36') {
            return [
                'success' => false,
                'data' => null,
                'error' => $msg['response_idrg']['drg_description'],
            ];
        }

        return [
            'success' => true,
            'data' => 'Grouping IDRG berhasil disimpan!',
            'error' => null,
        ];
    }

    function FinalIdrgSmc($nomor_sep, $coder_nik)
    {
        $request = [
            'metadata' => [
                'method' => 'idrg_grouper_final',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "idrg_grouper_final": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        InsertData2('idrg_klaim_final_smc', "'$nomor_sep', '$coder_nik'");

        return [
            'success' => true,
            'data' => 'Grouping IDRG berhasil final!',
            'error' => null,
        ];
    }

    function ImportIdrgToInacbgSmc($nomor_sep)
    {
        $request = [
            'metadata' => [
                'method' => 'idrg_to_inacbg_import',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "idrg_to_inacbg_import": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        $data_diagnosa = $msg['data']['diagnosa']['expanded'];
        $data_prosedur = $msg['data']['procedure']['expanded'];

        usort($data_diagnosa, fn ($a, $b) => (int) $a['no'] <=> (int) $b['no']);
        usort($data_prosedur, fn ($a, $b) => (int) $a['no'] <=> (int) $b['no']);

        bukaquery2("delete from inacbg_diagnosa_pasien_smc where no_sep = '$nomor_sep'");
        foreach ($data_diagnosa as $dx) {
            $keterangan = '';
            $locked = '1';
            if ($dx['metadata']['code'] != '200') {
                $keterangan = $dx['metadata']['error_no'].' - '.$dx['metadata']['message'];
                $locked = '0';
            }
            try {
                bukaquery2(sprintf("insert into inacbg_diagnosa_pasien_smc values ('%s', '%s', '%s', %s, '%s', %s)", $nomor_sep, $dx['code'], cleankar($dx['display']), $dx['no'], $keterangan, $locked));
            } catch (\Exception $e) {
                continue;
            }
        }

        bukaquery2("delete from inacbg_prosedur_pasien_smc where no_sep = '$nomor_sep'");
        foreach ($data_prosedur as $p) {
            $keterangan = '';
            $locked = '1';
            if ($p['metadata']['code'] != '200') {
                $keterangan = $p['metadata']['error_no'].' - '.$p['metadata']['message'];
                $locked = '0';
            }
            try {
                bukaquery2(sprintf("insert into inacbg_prosedur_pasien_smc values ('%s', '%s', '%s', %s, '%s', %s)", $nomor_sep, $p['code'], cleankar($p['display']), $p['no'], $keterangan, $locked));
            } catch (\Exception $e) {
                continue;
            }
        }

        return [
            'success' => true,
            'data' => 'Import koding ke INACBG berhasil!',
            'error' => null,
        ];
    }

    function SetDiagnosaInacbgSmc($nomor_sep, $diagnosa)
    {
        $request = [
            'metadata' => [
                'method' => 'inacbg_diagnosa_set',
                'nomor_sep' => $nomor_sep,
            ],
            'data' => [
                'diagnosa' => $diagnosa,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "inacbg_diagnosa_set": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        return [
            'success' => true,
            'data' => 'Diagnosa INACBG berhasil disimpan!',
            'error' => null,
        ];
    }

    function SetProsedurInacbgSmc($nomor_sep, $prosedur)
    {
        if (empty($prosedur)) {
            $prosedur = '#';
        }

        $request = [
            'metadata' => [
                'method' => 'inacbg_procedure_set',
                'nomor_sep' => $nomor_sep,
            ],
            'data' => [
                'procedure' => $prosedur,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "inacbg_procedure_set": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        return [
            'success' => true,
            'data' => 'Prosedur INACBG berhasil disimpan!',
            'error' => null,
        ];
    }

    function GroupingStage1InacbgSmc($nomor_sep, $coder_nik)
    {
        $request = [
            'metadata' => [
                'method' => 'grouper',
                'stage' => '1',
                'grouper' => 'inacbg',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ]
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "grouper stage 1": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        $tariff = 0;
        if (isset($msg['response_inacbg']['tariff'])) {
            $tariff = $msg['response_inacbg']['tariff'];
        }

        try {
            bukaquery2(sprintf(
                "insert into inacbg_grouping_stage12 values ('%s', '%s', '%s', %s, '%s') on duplicate key update code_cbg = values(code_cbg), deskripsi = values(deskripsi), tarif = values(tarif), top_up = values(top_up)",
                $nomor_sep,
                $msg['response_inacbg']['cbg']['code'],
                $msg['response_inacbg']['cbg']['description'],
                $tariff,
                'Tidak Ada'
            ));
        } catch (\Exception $e) {

        }

        if (mb_substr($msg['response_inacbg']['cbg']['code'], 0, 1) === 'X') {
            return [
                'success' => false,
                'data' => $msg['response_inacbg']['cbg']['description'],
                'error' => $msg['response_inacbg']['cbg']['code']
            ];
        }

        if (isset($msg['special_cmg_option']) && count($msg['special_cmg_option']) > 0) {
            Hapus2('tempinacbg', "coder_nik = '$coder_nik'");
            foreach ($msg['special_cmg_option'] as ['code' => $code, 'description' => $desc, 'type' => $type]) {
                InsertData2('tempinacbg', "'$coder_nik', '$code', '$desc', '$type'");
            }

            ubahSmc('inacbg_grouping_stage12', "top_up = 'Belum'", "no_sep = '$nomor_sep'");

            return [
                'success' => true,
                'data' => 'inacbg_stage2',
                'error' => null,
            ];
        }

        return [
            'success' => true,
            'data' => 'Grouping INACBG berhasil disimpan!',
            'error' => null,
        ];
    }

    function GroupingStage2InacbgSmc($nomor_sep, $coder_nik, $special_cmg)
    {
        $request = [
            'metadata' => [
                'method' => 'grouper',
                'stage' => 2,
                'grouper' => 'inacbg',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
                'special_cmg' => $special_cmg,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "grouper stage 2": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        Hapus2("tempinacbg", "coder_nik = '$coder_nik'");

        $top_up = 'Tidak Ada';

        if (!empty($msg['response_inacbg']['special_cmg'])) {
            $top_up = 'Sudah';
            foreach ($msg['response_inacbg']['special_cmg'] as $special_cmg) {
                InsertData2('inacbg_grouping_stage2_smc', sprintf("'%s', '%s', '%s', '%s', %s",
                    $nomor_sep,
                    $special_cmg['code'],
                    $special_cmg['description'],
                    $special_cmg['type'],
                    $special_cmg['tariff']
                ));
            }
        }

        ubahSmc('inacbg_grouping_stage12', sprintf("code_cbg = '%s', deskripsi = '%s', tarif = %s, top_up = '%s'",
            $msg['response_inacbg']['cbg']['code'],
            $msg['response_inacbg']['cbg']['description'],
            $msg['response_inacbg']['tariff'],
            $top_up
        ), "no_sep = '$nomor_sep'");

        return [
            'success' => true,
            'data' => 'Top Up INACBG berhasil disimpan!',
            'error' => null,
        ];
    }

    function FinalInacbgSmc($nomor_sep, $coder_nik)
    {
        $request = [
            'metadata' => [
                'method' => 'inacbg_grouper_final',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "inacbg_grouper_final": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        InsertData2('inacbg_klaim_final_smc', "'$nomor_sep', '$coder_nik'");

        return [
            'success' => true,
            'data' => 'Grouping INACBG sudah final dan berhasil disimpan!',
            'error' => null,
        ];
    }

    function FinalisasiKlaimSmc($nomor_sep, $coder_nik)
    {
        $request = [
            'metadata' => [
                'method' => 'claim_final',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
                'coder_nik' => $coder_nik,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "claim_final": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        $import_koding = trim(getOne('select setting.sistem_import_koding from setting'));
        $norawat = trim(getOne("select bridging_sep.no_rawat from bridging_sep where bridging_sep.no_sep = '$nomor_sep'"));
        $status_lanjut = trim(getOne("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat = '$norawat'"));

        if ($import_koding === 'IDRG') {
            bukaquery2("delete from diagnosa_pasien where diagnosa_pasien.no_rawat = '$norawat' and diagnosa_pasien.status = '$status_lanjut'");
            bukaquery2("delete from prosedur_pasien where prosedur_pasien.no_rawat = '$norawat' and prosedur_pasien.status = '$status_lanjut'");

            bukaquery2(<<<SQL
                insert into diagnosa_pasien (no_rawat, kd_penyakit, status, prioritas, status_penyakit) select * from (select s.no_rawat,
                i.kode_icd10 as kd_penyakit, r.status_lanjut as status, i.urut as prioritas, if(exists(select * from diagnosa_pasien d join
                reg_periksa r2 on d.no_rawat = r2.no_rawat where r2.no_rkm_medis = r.no_rkm_medis), 'Lama', 'Baru') as status_penyakit from
                idrg_diagnosa_pasien_smc i join bridging_sep s on i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat where
                i.no_sep = '$nomor_sep') t
                SQL);

            bukaquery2(<<<SQL
                insert into prosedur_pasien (no_rawat, kode, status, prioritas, jumlah) select * from (select s.no_rawat, i.kode_icd9 as kode,
                r.status_lanjut as status, i.urut as prioritas, i.multiplicity as jumlah from idrg_prosedur_pasien_smc i join bridging_sep s on
                i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat where i.no_sep = '$nomor_sep') t
                SQL);
        } else if ($import_koding === 'INA-CBG') {
            bukaquery2("delete from diagnosa_pasien where diagnosa_pasien.no_rawat = '$norawat' and diagnosa_pasien.status = '$status_lanjut'");
            bukaquery2("delete from prosedur_pasien where prosedur_pasien.no_rawat = '$norawat' and prosedur_pasien.status = '$status_lanjut'");

            bukaquery2(<<<SQL
                insert into diagnosa_pasien (no_rawat, kd_penyakit, status, prioritas, status_penyakit) select * from (select s.no_rawat,
                i.kode_icd10 as kd_penyakit, r.status_lanjut as status, i.urut as prioritas, if(exists(select * from diagnosa_pasien d join
                reg_periksa r2 on d.no_rawat = r2.no_rawat where r2.no_rkm_medis = r.no_rkm_medis), 'Lama', 'Baru') as status_penyakit from
                inacbg_diagnosa_pasien_smc i join bridging_sep s on i.no_sep = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat where
                i.no_sep = '$nomor_sep') t
                SQL);

            bukaquery2(<<<SQL
                insert into prosedur_pasien (no_rawat, kode, status, prioritas, jumlah) select * from (select s.no_rawat, i.kode_icd9 as kode,
                r.status_lanjut as status, i.urut as prioritas, 1 as jumlah from inacbg_prosedur_pasien_smc i join bridging_sep s on i.no_sep
                = s.no_sep join reg_periksa r on s.no_rawat = r.no_rawat where i.no_sep = '$nomor_sep') t
                SQL);
        }

        return CetakKlaimSmc($nomor_sep);
    }

    function CetakKlaimSmc($nomor_sep)
    {
        $request = [
            'metadata' => [
                'method' => 'claim_print',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "claim_print": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error
            ];
        }

        $encodedPDF = $msg['data'];

        $filename = 'pages/pdf/'.$nomor_sep.'.pdf';

        if (file_exists($filename)) {
            unlink($filename);
        }

        file_put_contents($filename, base64_decode($encodedPDF));

        if (getOne("select exists(select * from inacbg_cetak_klaim where no_sep = '$nomor_sep')") == '0') {
            InsertData('inacbg_cetak_klaim', "'{$nomor_sep}', 'pages/pdf/{$nomor_sep}.pdf', null");
        }

        return [
            'success' => true,
            'data' => 'Klaim berhasil disimpan!',
            'error' => null,
        ];
    }

    function KirimKlaimIndividualSmc($nomor_sep)
    {
        $request = [
            'metadata' => [
                'method' => 'send_claim_individual',
            ],
            'data' => [
                'nomor_sep' => $nomor_sep,
            ],
        ];

        $msg = Request(json_encode($request));

        if ($msg['metadata']['code'] != '200') {
            $error = sprintf(
                '[%s] method "claim_final": %s - %s',
                $msg['metadata']['code'],
                $msg['metadata']['error_no'],
                $msg['metadata']['message']
            );

            echo '<span style="font-weight: bold; font-size: 16; color: rgb(255, 0, 0)">'.$error.'</span><br /><br />';

            return [
                'success' => false,
                'data' => null,
                'error' => $error,
            ];
        }

        bukaquery2("update inacbg_cetak_klaim set kirim_ke_dc = now() where no_sep = '$nomor_sep'");

        return [
            'success' => true,
            'data' => 'Klaim berhasil dikirim online!',
            'error' => null,
        ];
    }