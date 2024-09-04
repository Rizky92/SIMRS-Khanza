<?php

namespace App\Models\SIMRS;

use App\Models\Pemeriksaan;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Reedware\LaravelCompositeRelations\HasCompositeRelations;

class PemeriksaanLab extends Model
{
    use HasCompositeRelations;

    protected $connection = 'mysql_sik';

    protected $primaryKey = 'id';

    protected $keyType = 'int';

    protected $table = 'pemeriksaan_labpk';

    public $incrementing = false;

    public $timestamps = false;

    public function kategori(): BelongsTo
    {
        return $this->belongsTo(PemeriksaanLabKategori::class, 'nama', 'kategori');
    }

    public function templateLab(): BelongsToMany
    {
        return $this->belongsToMany(
            TindakanLabTemplate::class, 'mapping_pemeriksaan_labpk',
            'id_pemeriksaan', 'id_template',
            'id', 'id_template'
        );
    }

    public function hasil(): HasMany
    {
        return $this->hasMany(Pemeriksaan::class, 'kode_compound', 'compound');
    }

    public function scopeSelectKolomMappingAdamlabs(Builder $query): Builder
    {
        $sqlSelect = <<<'SQL'
            pemeriksaan_labpk.id,
            pemeriksaan_labpk.kode_compound,
            pemeriksaan_labpk.kategori,
            template_laboratorium.kd_jenis_prw,
            mapping_pemeriksaan_labpk.id_template,
            pemeriksaan_labpk.kode_pemeriksaan,
            pemeriksaan_labpk.nama_pemeriksaan,
            template_laboratorium.satuan,
            template_laboratorium.bagian_rs as pemeriksaan_jasa_sarana,
            template_laboratorium.bhp as pemeriksaan_bhp,
            template_laboratorium.bagian_perujuk as pemeriksaan_jasa_perujuk,
            template_laboratorium.bagian_dokter as pemeriksaan_jasa_medis_dokter,
            template_laboratorium.bagian_laborat as pemeriksaan_jasa_medis_petugas,
            template_laboratorium.kso as pemeriksaan_kso,
            template_laboratorium.menejemen as pemeriksaan_manajemen,
            template_laboratorium.biaya_item as pemeriksaan_pendapatan,
            pemeriksaan_labpk_kategori.urut as urut_kategori,
            pemeriksaan_labpk.urut
            SQL;

        return $query
            ->selectRaw($sqlSelect)
            ->join('mapping_pemeriksaan_labpk', 'pemeriksaan_labpk.id', '=', 'mapping_pemeriksaan_labpk.id_pemeriksaan')
            ->join('template_laboratorium', 'mapping_pemeriksaan_labpk.id_template', '=', 'template_laboratorium.id_template')
            ->join('pemeriksaan_labpk_kategori', 'pemeriksaan_labpk.kategori', '=', 'pemeriksaan_labpk_kategori.nama')
            ->where('pemeriksaan_labpk.vendor', 'adamlabs');
    }

    /**
     * @param  \Illuminate\Database\Eloquent\Collection<array-key, string>|array<array-key, string>  $kategori
     * @param  \Illuminate\Database\Eloquent\Collection<array-key, string>|array<array-key, string>  $tindakan
     * @param  \Illuminate\Database\Eloquent\Collection<array-key, string>|array<array-key, string>  $compound
     */
    public function scopeUntukHasilPemeriksaan(Builder $query, $kategori, $tindakan, $compound): Builder
    {
        return $query
            ->selectKolomMappingAdamlabs()
            ->whereIn('pemeriksaan_labpk.kategori', $kategori)
            ->whereIn('template_laboratorium.kd_jenis_prw', $tindakan)
            ->whereIn('pemeriksaan_labpk.kode_compound', $compound)
            ->orderBy('pemeriksaan_labpk_kategori.urut')
            ->orderBy('pemeriksaan_labpk.urut');
    }
}
