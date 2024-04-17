<?php
    include_once "conf/command.php";
    require_once('../conf/conf.php');
    
    $usere      = trim(isset($_GET['usere']))?trim($_GET['usere']):NULL;
    $passwordte = trim(isset($_GET['passwordte']))?trim($_GET['passwordte']):NULL;
    if ($_GET['act']=="loginbilling"){
        if((USERHYBRIDWEB==$usere)&&(PASHYBRIDWEB==$passwordte)){
            session_start();
            $_SESSION['ses_admin_berkas_rawat']="billing";
            $url = "index.php?act=List";			
        }else{
            session_start();
            session_destroy();
            if (cekSessiAdmin()){
                session_unregister("ses_admin_berkas_rawat");
            }
            $url = "index.php?act=HomeAdmin";
        }
        header("Location:".$url);
    }
?>


<?php
    include_once "../conf/conf.php";
    include_once "conf/command.php";
    $act=isset($_GET['act'])?$_GET['act']:NULL;
    if ($act=="login"){
        $usere      = validTeks4((trim($_POST['usere'])?$_POST['usere']:NULL),30);
        $passwordte = validTeks4((trim($_POST['passwordte'])?$_POST['passwordte']:NULL),30);
        
        $sql = "SELECT password_asuransi.kd_pj FROM password_asuransi WHERE password_asuransi.usere=aes_encrypt('".$usere."','nur') AND password_asuransi.passworde=aes_encrypt('".$passwordte."','windi')";
        $hasil=bukaquery($sql);
        if($baris = mysqli_fetch_array($hasil)) {
            session_start();
            $_SESSION["ses_vedika"]="admin";
            $_SESSION["carabayar"]=encrypt_decrypt($baris[0],"e");
            header("Location:index.php?act=ListVedika");
        }else{
            session_start();
            $_SESSION["ses_vedika"]=null;
            unset($_SESSION["ses_vedika"]);
            $_SESSION["carabayar"]=null;
            unset($_SESSION["carabayar"]); 
            session_destroy();
            header("Location:index.php");
        }
    }
    
?>
