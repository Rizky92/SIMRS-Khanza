<?php
    session_start();
    session_destroy();
    require_once "conf/command.php";
    if (cekSessiAdmin()){
        unset($_SESSION["ses_admin_penolakanresusitasi"]);
    }
    header("Location:index.php");
?>
