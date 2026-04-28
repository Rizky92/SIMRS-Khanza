<?php
    include_once "conf/command.php";
    require_once('../conf/conf.php');
    $usere      = isset($_GET['usere']) ? trim($_GET['usere']) : null;
    $passwordte = isset($_GET['passwordte']) ? trim($_GET['passwordte']) : null;
    $url        = "index.php?act=Home";
    if ($_GET['act'] == "login") {
        if ($usere == USERHYBRIDWEB && $passwordte == PASHYBRIDWEB) {
            session_start();
            $_SESSION['ses_admin_fotokelahiranbayismc'] = "admin";
            $url = "index.php?act=Kamera";
        } else {
            session_start();
            session_destroy();
            if (cekSessiAdmin()){
                session_unregister("ses_admin_fotokelahiranbayismc");
            }
        }
    }
    header("Location:".$url);
?>
