<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Registrasi extends Model
{
    protected $connection = 'mysql';

    protected $table = 'registrasi';

    protected $primaryKey = 'no_laboratorium';

    protected $keyType = 'string';

    public $incrementing = false;

    public $timestamps = false;

    protected $fillable = [
        'no_laboratorium',
        'no_registrasi',
        'waktu_registrasi',
        'keterangan_hasil',
        'diagnosa_awal',
        'kode_rs',
        'kode_lab',
        'username',
        'nama_pegawai',
        'dokter_penanggung_jawab',
        'umur_tahun',
        'umur_bulan',
        'umur_hari',
        'pasien_no_rm',
        'pasien_nama_pasien',
        'pasien_jenis_kelamin',
        'pasien_tanggal_lahir',
        'pasien_alamat',
        'pasien_nik',
        'pasien_no_telphone',
        'pasien_ras',
        'pasien_berat_badan',
        'pasien_jenis_registrasi',
        'dokter_pengirim_kode',
        'dokter_pengirim_nama',
        'unit_asal_kode',
        'unit_asal_nama',
        'penjamin_kode',
        'penjamin_nama',
    ];

    public function pemeriksaan(): HasMany
    {
        return $this->hasMany(Pemeriksaan::class, 'no_laboratorium', 'no_laboratorium');
    }
}
