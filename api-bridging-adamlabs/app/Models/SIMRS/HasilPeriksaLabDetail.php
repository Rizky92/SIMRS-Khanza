<?php

namespace App\Models\SIMRS;

use Illuminate\Database\Eloquent\Model;
use Reedware\LaravelCompositeRelations\CompositeBelongsTo;
use Reedware\LaravelCompositeRelations\HasCompositeRelations;

class HasilPeriksaLabDetail extends Model
{
    use HasCompositeRelations;

    protected $connection = 'mysql_sik';

    protected $table = 'detail_periksa_lab';

    protected $primaryKey = false;

    protected $keyType = false;

    public $incrementing = false;

    public $timestamps = false;

    protected $fillable = [
        'no_rawat',
        'kd_jenis_prw',
        'tgl_periksa',
        'jam',
        'id_template',
        'nilai',
        'nilai_rujukan',
        'keterangan',
        'bagian_rs',
        'bhp',
        'bagian_perujuk',
        'bagian_dokter',
        'bagian_laborat',
        'kso',
        'menejemen',
        'biaya_item',
    ];

    public function parent(): CompositeBelongsTo
    {
        return $this
            ->compositeBelongsTo(
                HasilPeriksaLab::class,
                ['no_rawat', 'kd_jenis_prw', 'tgl_periksa', 'jam'],
                ['no_rawat', 'kd_jenis_prw', 'tgl_periksa', 'jam'],
            )
            ->where('kategori', 'PK');
    }
}
