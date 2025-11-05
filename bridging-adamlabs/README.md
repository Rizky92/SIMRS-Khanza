# Webservice Bridging ADAMLABS
Webservice Bridging ADAMLABS digunakan untuk menerima kiriman hasil pemeriksaan lab dari LIS ADAMLABS ke SIMRS Khanza. Webservice tersedia sebagai Rest API berdasarkan [Dokumentasi Bridging ADAMLABS](https://adamlabs.id/docs-bridging-adamlabs#table_of_content_heading_1735033496270_8).  

Untuk integrasi ADAMLABS tambahan dari sisi SIMRS (kirim order lab, mapping pemeriksaan) bisa dilihat pada pull request [disini](https://github.com/rizky92/SIMRS-Khanza/pull/113/files).

## System Requirement
1. PHP ^7.4 dan MariaDB ^10.4, bisa menggunakan [XAMPP](https://www.apachefriends.org/download.html) atau [Laragon](https://laragon.org/download/)
2. [Composer ^2.2](https://getcomposer.org/download/)

## Instalasi
Git clone repo dan masuk ke directory.
```bash
git clone https://github.com/rizky92/api-bridging-adamlabs.git --depth=1 api-bridging-adamlabs \
    && cd api-bridging-adamlabs
```

Kemudian install dependency menggunakan composer, lalu copy file `.env.example` menjadi `.env` di directory yang sama.
```bash
composer install && cp .env.example .env
```

Lakukan konfigurasi `API_KEY` dan database SIMRS di file .env yang baru dicopy tadi pada bagian `# WAJIB`.

Apabila sudah, jalankan perintah berikut secara berurutan.
```bash
php artisan key:generate \
    && php artisan migrate
```

Test aplikasi berhasil diinstal dengan perintah berikut, lalu buka http://127.0.0.1:8000.
```bash
php artisan serve
```

## Fitur & Cara Kerja Webservice
Tersedia dua route untuk menerima hasil kiriman LIS ke SIMRS.
| Method | URI | Action |
|---|---|---|
| `POST` | `{APP_URL}/api/adam-lis/bridging` | `App\Http\Controllers\API\SimpanHasilLabController` |
| `POST` | `{APP_URL}/api/adam-lis/bridging/update-hasil` | `App\Http\Controllers\API\UpdateHasilLabController` |

### `{APP_URL}/api/adam-lis/bridging`
Route ini digunakan untuk menerima hasil pemeriksaan lab baru dari LIS ke SIMRS. Pada route ini, proses pengembalian bisa secara full atau parsial. Untuk pengembalian parsial akan melihat status pemeriksaan di SIMRS dan flag `status_bridging = true` dari request yang dikirim dari LIS.

### `{APP_URL}/api/adam-lis/bridging/update-hasil`
Route ini digunakan untuk mengupdate hasil pemeriksaan lab yang sebelumnya sudah pernah dirkirim ke SIMRS. Pada route ini, hanya pemeriksaan yang sudah ada di SIMRS atau dengan flag `status_bridging = true` dari request yang dikirim ke LIS.

### Modul Keuangan
Setiap pengembalian hasil akan melakukan proses perhitungan keuangan yang kemudian akan dicatat sebagai transaksi jurnal di SIMRS. Apabila penggunaan keuangan telah terintegrasi ke SIMRS, maka **SANGAT DISARANKAN** untuk melakukan uji coba dahulu.  

### TODO:
- [x] [Mengupdate waktu hasil agar menggunakan waktu terbaru dari hasil pengembalian LIS.](https://github.com/Rizky92/bridging-adamlabs/pull/2)
- [ ] Update validasi
