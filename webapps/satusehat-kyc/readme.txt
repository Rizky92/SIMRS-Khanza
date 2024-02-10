Readme PHP KYC Library 2.0

Prerequisite :
- PHP 7 ~ PHP 8 (PHP 5.6 Tidak mendukung)
- phpseclib (https://phpseclib.com/docs/install)

1. Install PHP, baca selengkapnya di https://www.apachefriends.org/download.html
2. Letakkan hasil folder source-code 'kyc-library-php' di folder htdocs
3. Install phpseclib pada direktori kyc-library-php diatas dengan perintah 'composer install' (baca selengkapnya di https://phpseclib.com/docs/install)
4. rename file satusehat.ini.template menjadi satusehat.ini
5. Sesuaikan variabel input : $agent_name, $agent_nik pada file induk index.php
6. Buka url http://localhost/kyc-library-php/ pada browser, hasil yg ditampilkan di layar adalah dari fungsi generate-url, cara kerja input output sebagai berikut :
  - Parameter function : agent_name, agent_nik, access_token
  - Payload Response terdeskripsi : code (status), agent_name, agent_nik, token, url (web validasi)
7. Penjelasan dari fungsi-fungsi di function.php
   1) generateKey: key-generation RSA utk menghasilkan 1 pasang: public key & private key
   2) encryptMessage:
       a. generateSymmetricKey: key-generation AES utk menghasilkan 1 buah AES symmetric-key
       b. aesEncrypt: menyematkan RSA public-key (hasil no. 1) ke dalam pesan, serta melakukan enkripsi keseluruhan pesan menggunakan AES symmetric-key (hasil no. 2.a) 
       c. $serverKey->encrypt: enkripsi hasil no.2.a dengan RSA public key dari SatuSehat
       d. menggabungkan hasil dari 2.b & 2.c
   3) curl_exec: Pemanggilan API Generate URL ke SatuSehat, menggunakan payload yg merupakan hasil no. 2.d
   4) decryptMessage:
       a. $key->decrypt: decrypt AES symmetric-key dari response API Generate URL, menggunakan RSA private-key dari no. 1
       b. aesDecrypt: decrypt pesan dari response API Generate URL menggunakan AES symmetric-key dari hasil no. 4.a