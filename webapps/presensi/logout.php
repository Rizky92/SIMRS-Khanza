<?php
	session_start();
	unset($id);
	unset($nama);
	session_destroy();
	require_once "conf/command.php";
	if (cekSessiAdmin())
	{
		unset($_SESSION["ses_admin"]);
	}
	header("Location:index.php");

?>