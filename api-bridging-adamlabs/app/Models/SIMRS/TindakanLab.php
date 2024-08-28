<?php

namespace App\Models\SIMRS;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class TindakanLab extends Model
{
    protected $connection = 'mysql_sik';

    protected $primaryKey = 'kd_jenis_prw';

    protected $keyType = 'string';

    protected $table = 'jns_perawatan_lab';

    public $incrementing = false;

    public $timestamps = false;

    public function template(): HasMany
    {
        return $this->hasMany(TindakanLabTemplate::class, 'kd_jenis_prw', 'kd_jenis_prw');
    }
}
