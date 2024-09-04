<?php

namespace App\Models\SIMRS;

use Illuminate\Database\Eloquent\Model;
use Reedware\LaravelCompositeRelations\CompositeBelongsTo;
use Reedware\LaravelCompositeRelations\CompositeHasMany;
use Reedware\LaravelCompositeRelations\CompositeHasOne;
use Reedware\LaravelCompositeRelations\HasCompositeRelations;

class HasilPeriksaLab extends Model
{
    use HasCompositeRelations;

    protected $connection = 'mysql_sik';

    protected $table = 'periksa_lab';

    protected $primaryKey = false;

    protected $keyType = false;

    public $incrementing = false;

    public $timestamps = false;

    protected $fillable = [
        'no_rawat',
        'nip',
        'kd_jenis_prw',
        'tgl_periksa',
        'jam',
        'dokter_perujuk',
        'bagian_rs',
        'bhp',
        'tarif_perujuk',
        'tarif_tindakan_dokter',
        'tarif_tindakan_petugas',
        'kso',
        'menejemen',
        'biaya',
        'kd_dokter',
        'status',
        'kategori',
    ];

    public function permintaan(): CompositeBelongsTo
    {
        return $this
            ->compositeBelongsTo(
                PermintaanLabPK::class,
                ['no_rawat', 'tgl_hasil', 'jam_hasil'],
                ['no_rawat', 'tgl_periksa', 'jam']
            )
            ->where('kategori', 'PK');
    }

    public function catatan(): CompositeHasOne
    {
        return $this->compositeHasOne(
            KesanSaran::class,
            ['no_rawat', 'tgl_periksa', 'jam'],
            ['no_rawat', 'tgl_periksa', 'jam']
        );
    }

    public function detail(): CompositeHasMany
    {
        return $this
            ->compositeHasMany(
                HasilPeriksaLabDetail::class,
                ['no_rawat', 'tgl_periksa', 'jam', 'kd_jenis_prw'],
                ['no_rawat', 'tgl_periksa', 'jam', 'kd_jenis_prw'],
            )
            ->where('kategori', 'PK');
    }
}
