<?php

namespace App\Models\SIMRS;

use Illuminate\Database\Eloquent\Model;
use Reedware\LaravelCompositeRelations\CompositeBelongsTo;
use Reedware\LaravelCompositeRelations\HasCompositeRelations;

class KesanSaran extends Model
{
    use HasCompositeRelations;

    protected $connection = 'mysql_sik';

    protected $table = 'saran_kesan_lab';

    protected $primaryKey = false;

    protected $keyType = false;

    public $incrementing = false;

    public $timestamps = false;

    protected $fillable = [
        'no_rawat',
        'tgl_periksa',
        'jam',
        'saran',
        'kesan',
    ];

    public function pemeriksaan(): CompositeBelongsTo
    {
        return $this->compositeBelongsTo(
            HasilPeriksaLab::class,
            ['no_rawat', 'tgl_periksa', 'jam'],
            ['no_rawat', 'tgl_periksa', 'jam'],
        );
    }
}
