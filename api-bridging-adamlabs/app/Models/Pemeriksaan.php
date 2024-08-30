<?php

namespace App\Models;

use App\Models\SIMRS\HasilPeriksaLabDetail;
use App\Models\SIMRS\MappingTindakan;
use App\Models\SIMRS\PemeriksaanLab;
use Exception;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Query\JoinClause;
use Illuminate\Support\Facades\DB;
use Reedware\LaravelCompositeRelations\CompositeBelongsTo;
use Reedware\LaravelCompositeRelations\HasCompositeRelations;

class Pemeriksaan extends Model
{
    use HasCompositeRelations;

    protected $connection = 'mysql';

    protected $table = 'pemeriksaan';

    protected $primaryKey = 'id';

    protected $keyType = 'int';

    public $incrementing = true;

    public $timestamps = false;

    protected $fillable = [
        'no_laboratorium',
        'no_registrasi',
        'kategori_pemeriksaan_nama',
        'kategori_pemeriksaan_urut',
        'sub_kategori_pemeriksaan_nama',
        'sub_kategori_pemeriksaan_urut',
        'nomor_urut',
        'kode_tindakan_simrs',
        'kode_pemeriksaan_lis',
        'nama_pemeriksaan_lis',
        'metode',
        'waktu_pemeriksaan',
        'status_bridging',
        'hasil_satuan',
        'hasil_nilai_hasil',
        'hasil_nilai_rujukan',
        'hasil_flag_kode',
        'compound',
    ];

    protected $casts = [
        'status_bridging' => 'boolean',
    ];

    public function registrasi(): BelongsTo
    {
        return $this->belongsTo(Registrasi::class, 'no_laboratorium', 'no_laboratorium');
    }

    public function mapping(): BelongsTo
    {
        return $this->belongsTo(PemeriksaanLab::class, 'compound', 'kode_compound');
    }
}
