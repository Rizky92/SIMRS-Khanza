## APM Custom
Aplikasi anjungan pasien mandiri (APM) modifikasi dari [APM RS Indriati Boyolali](https://github.com/abdulrokhimrepo/anjunganmandiriSEP).

### Konfigurasi
Terdapat dua jenis konfigurasi. Konfigurasi bawaan [SIMRS Khanza SMC (database.xml)](../setting/database.xml.example), dan konfigurasi khusus untuk APM.  
Berikut adalah konfigurasi yang disediakan dalam file `apm.xml.example`:  

```xml
<entry key="PRINTER_REGISTRASI"></entry>
<entry key="PRINTER_BARCODE"></entry>
<entry key="PRINTER_ANTRIAN"></entry>
<entry key="PRINTJUMLAHBARCODE">3</entry>
<entry key="PRINTJUMLAHANTRIANFARMASI">2</entry>
<entry key="URLAPLIKASIFINGERPRINTBPJS"></entry>
<entry key="URLAPLIKASIFRISTABPJS"></entry>
<entry key="USERFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="PASSWORDFINGERPRINTBPJS">l4nh5eVYrLAER/I2A4b3Tw==</entry>
<entry key="TOMBOLDIMATIKAN">satusehat</entry>
<entry key="KODEPOLIEKSEKUTIF"></entry>
```

### `PRINTER_REGISTRASI`
Pengaturan ini digunakan untuk mencari nama printer untuk mencetak bukti registrasi dan lembar SEP.  
Untuk mengambil nama printer bisa dilakukan dengan pertama kali membuka APM dan melihat daftar printer yang muncul di terminal.  

value: nama printer yang muncul di terminal  
default: `""`  

### `PRINTER_BARCODE`
Pengaturan ini digunakan untuk mencari nama printer untuk mencetak lembar barcode.  
Untuk mengambil nama printer bisa dilakukan dengan pertama kali membuka APM dan melihat daftar printer yang muncul di terminal.  

value: nama printer yang muncul di terminal  
default: `""`  

### `PRINTER_ANTRIAN`
Pengaturan ini digunakan untuk mencari nama printer untuk mencetak nomor antrian dari APM.  
Untuk mengambil nama printer bisa dilakukan dengan pertama kali membuka APM dan melihat daftar printer yang muncul di terminal.  

value: nama printer yang muncul di terminal  
default: `""`  

### `PRINTJUMLAHBARCODE`
Pengaturan ini digunakan untuk menentukan jumlah barcode yang mau dicetak.  

value: integer  
default: `3`  

### `PRINTJUMLAHANTRIANFARMASI`
Pengaturan ini digunakan untuk menentukan jumlah antrian farmasi yang mau dicetak.  

value: integer  
default: `2`  

### `URLAPLIKASIFINGERPRINTBPJS`
Pengaturan ini digunakan untuk mencari lokasi file exe aplikasi fingerprint BPJS Kesehatan untuk proses validasi biometrik fingerprint dari APM.  

value: Path file exe  
default: `""`  

### `URLAPLIKASIFRISTABPJS`
Pengaturan ini digunakan untuk mencari lokasi file exe aplikasi FRISTA BPJS Kesehatan untuk proses validasi biometrik pengenalan wajah dari APM.  

value: Path file exe  
default: `""`  

### `USERFINGERPRINTBPJS`
Pengaturan ini digunakan untuk mengisi kolom username pada saat pertama kali membuka aplikasi validasi biometrik BPJS Kesehatan. Harus dilakukan enkripsi dahulu.  

value: encrypted  
default: `""`  

### `PASSWORDFINGERPRINTBPJS`
Pengaturan ini digunakan untuk mengisi kolom password pada saat pertama kali membuka aplikasi validasi biometrik BPJS Kesehatan. Harus dilakukan enkripsi dahulu.  

value: encrypted  
default: `""`  

### `TOMBOLDIMATIKAN`
Pengaturan ini digunakan untuk mengatur menu apa yang dimatikan dari halaman depan APM.  

values: List menu, dipisah dengan koma  
| value | keterangan |
| --- | --- |
| `antrian` | Mematikan tombol "Antrian Loket" |  
| `antrianfarmasi` | Mematikan tombol "Antrian Farmasi" |
| `cekin` | Mematikan tombol "Cek In Booking" |
| `daftarpoli` | Mematikan tombol "Pendaftaran Poliklinik" |
| `seppertama` | Mematikan tombol "SEP Kunjungan Pertama" |
| `sepkontrol` | Mematikan tombol "SEP Kontrol" |
| `sepbedapoli` | Mematikan tombol "SEP Kontrol Beda Poli" |
| `mobilejkn` | Mematikan tombol "Cek In MobileJKN" |
| `satusehat` | Mematikan tombol "Aktivasi Satu Sehat" |

default: `"satusehat"`  

### `KODEPOLIEKSEKUTIF`
Pengaturan ini digunakan untuk mengatur kode poli yang digunakan sebagai default untuk pendaftaran registrasi eksekutif.  

value: kode poli eksekutif dari SIMRS  
default: `""`  
