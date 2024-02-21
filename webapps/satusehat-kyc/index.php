<?php

require_once('conf/auth.php');
require_once('conf/function.php');
require_once('../conf/conf.php');

header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); 
header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT"); 
header("Cache-Control: no-store, no-cache, must-revalidate"); 
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache"); // HTTP/1.0

$init = parse_ini_file("satusehat.ini");
$client_id = $init["client_id"];
$client_secret = $init["client_secret"];
$auth_url = $init["auth_url"];
$api_url = $init["api_url"];
$environment = $init["environment"];

// nama petugas/operator Fasilitas Pelayanan Kesehatan (Fasyankes) yang akan melakukan validasi
$agent_name = 'Nama Petugas';

// NIK petugas/operator Fasilitas Pelayanan Kesehatan (Fasyankes) yang akan melakukan validasi
$agent_nik ='123456789012436';

// auth to satusehat
$auth_result = authenticateWithOAuth2($client_id, $client_secret, $auth_url);

// Example usage
$json = generateUrl($agent_name, $agent_nik , $auth_result, $api_url, $environment);

$validation_web = json_decode($json, TRUE);

?>

<html>
<head>
  <script type="text/javascript">

    const url = "<?php echo $validation_web["data"]["url"]?>"

    function loadFormPopup() {
      let params = `scrollbars=no,resizable=no,status=no,location=no,toolbar=no,menubar=no,width=0,height=0,left=100,top=100`;
      window.open(url,"KYC",params)
    }

    function loadFormNewTab() {
      window.open(url,"_blank")
    }

  </script>
</head>
<body>
  <button onclick="loadFormPopup()">KYC Pasien Popup</button>
  <button onclick="loadFormNewTab()">KYC Pasien New Tab</button>
</body>
</html>