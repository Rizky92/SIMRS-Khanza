<?php
    include_once "conf/command.php";
    require_once('../conf/conf.php');

    $iyem = $_GET;
    unset($iyem['act']);
    extract($iyem);

    if ($_GET['act'] === 'login') {
        if (USERHYBRIDWEB === $usere && PASHYBRIDWEB === $passwordte) {
            session_start();
            $_SESSION['ses_admin_berkas_rawat'] = 'billing';
            $url = 'pages/billingklaim.php?iyem=' . encrypt_decrypt($norawat, 'e');
        } else {
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