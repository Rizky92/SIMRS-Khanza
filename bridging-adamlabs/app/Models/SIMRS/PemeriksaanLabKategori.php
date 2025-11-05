<?php

namespace App\Models\SIMRS;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class PemeriksaanLabKategori extends Model
{
    protected $connection = 'mysql_sik';

    protected $primaryKey = 'id';

    protected $keyType = 'int';

    protected $table = 'pemeriksaan_labpk_kategori';

    public $incrementing = false;

    public $timestamps = false;

    public function pemeriksaan(): HasMany
    {
        return $this->hasMany(PemeriksaanLab::class, 'kategori', 'nama');
    }
}
