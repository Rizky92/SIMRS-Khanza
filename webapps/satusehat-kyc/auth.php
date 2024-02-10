<?php

function authenticateWithOAuth2($clientId, $clientSecret, $tokenUrl) {
    
  $curl = curl_init();
  $params = [
    'grant_type' => 'client_credentials',
    'client_id' => $clientId,
    'client_secret' => $clientSecret
  ];
  
  curl_setopt_array($curl, array(
    CURLOPT_URL => "${tokenUrl}/accesstoken?grant_type=client_credentials",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS => http_build_query($params),
    CURLOPT_HTTPHEADER => array(
      'Content-Type: application/x-www-form-urlencoded'
    ),
  ));
  
  $response = curl_exec($curl);
  
  curl_close($curl);
  //echo $response;
  // Parse the response body
  $data = json_decode($response, true);

    // Return the access token
  return $data['access_token'];
}

