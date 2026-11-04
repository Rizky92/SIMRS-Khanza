<?php
        session_start();
	session_destroy();
	require_once "conf/command.php";
	if (cekSessiAdmin()){
	    unset($_SESSION["ses_admin_pengajuancutiperawatan"]);
	}

	header("Location:index.php");

?>
