<?php

namespace App\Models\SIMRS;

use Exception;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Query\JoinClause;
use Illuminate\Support\Facades\DB;

class TindakanLabTemplate extends Model
{
    protected $connection = 'mysql_sik';

    protected $table = 'template_laboratorium';

    protected $primaryKey = 'id_template';

    protected $keyType = 'int';

    public $incrementing = true;

    public $timestamps = false;

    public function tindakan(): BelongsTo
    {
        return $this->belongsTo(TindakanLab::class, 'kd_jenis_prw', 'kd_jenis_prw');
    }

    public function mapping(): BelongsToMany
    {
        return $this->belongsToMany(
            PemeriksaanLab::class, 'mapping_pemeriksaan_labpk',
            'id_template', 'id_pemeriksaan',
            'id_template', 'id'
        );
    }

    /**
     * @param  non-empty-string  $noRawat
     * @param  non-empty-string  $tanggal
     * @param  non-empty-string  $jam
     */
    public function scopeIsiHasilPeriksaLabDetail(
        Builder $query,
        string $noRawat,
        string $tanggal,
        string $jam,
        string $jk,
        string $statusUmur,
        string $kodeTindakan
    ): Builder {
        if (empty($noRawat) || empty($tanggal) || empty($jam)) {
            throw new Exception(sprintf('Parameters must not contain empty values'));
        }

        $adamlabs = DB::connection('mysql')->getDatabaseName();

        $sqlSelectHasilAdamlabs = <<<SQL
            $adamlabs.pemeriksaan.id,
            $adamlabs.pemeriksaan.kode_tindakan_simrs,
            $adamlabs.pemeriksaan.nama_pemeriksaan_lis,
            $adamlabs.pemeriksaan.hasil_satuan,
            $adamlabs.pemeriksaan.hasil_nilai_hasil,
            $adamlabs.pemeriksaan.hasil_nilai_rujukan,
            $adamlabs.pemeriksaan.hasil_flag_kode,
            mapping_adamlabs.id_template
            SQL;

        $hasilAdamlabs = DB::connection('mysql_sik')
            ->table("$adamlabs.registrasi")
            ->selectRaw($sqlSelectHasilAdamlabs)
            ->join("$adamlabs.pemeriksaan", fn (JoinClause $join) => $join
                ->on("$adamlabs.registrasi.no_laboratorium", '=', "$adamlabs.pemeriksaan.no_laboratorium")
                ->on("$adamlabs.registrasi.no_registrasi", '=', "$adamlabs.pemeriksaan.no_registrasi")
            )
            ->join('mapping_adamlabs', fn (JoinClause $join) => $join
                ->on("$adamlabs.pemeriksaan.kode_tindakan_simrs", '=', 'mapping_adamlabs.kd_jenis_prw')
                ->on("$adamlabs.pemeriksaan.nama_pemeriksaan_lis", '=', 'mapping_adamlabs.pemeriksaan')
                ->on("$adamlabs.registrasi.pasien_jenis_kelamin", '=', DB::raw("'$jk'"))
                ->on(DB::raw("'$statusUmur'"), '=', 'mapping_adamlabs.status_umur')
            )
            ->groupBy("$adamlabs.pemeriksaan.id");

        $sqlSelect = <<<SQL
            ? as no_rawat,
            template_laboratorium.kd_jenis_prw,
            ? as tgl_periksa,
            ? as jam,
            template_laboratorium.id_template,
            hasil_adamlabs.hasil_nilai_hasil as nilai,
            hasil_adamlabs.hasil_nilai_rujukan,
            hasil_adamlabs.hasil_flag_kode as keterangan,
            template_laboratorium.bagian_rs,
            template_laboratorium.bhp,
            template_laboratorium.bagian_perujuk,
            template_laboratorium.bagian_dokter,
            template_laboratorium.bagian_laborat,
            template_laboratorium.kso,
            template_laboratorium.menejemen,
            template_laboratorium.biaya_item
            SQL;

        return $query
            ->selectRaw($sqlSelect, [$noRawat, $tanggal, $jam])
            ->leftJoinSub($hasilAdamlabs, 'hasil_adamlabs', fn (JoinClause $join) => 
                $join->on('template_laboratorium.id_template', '=', 'hasil_adamlabs.id_template')
            )
            ->where('template_laboratorium.kd_jenis_prw', $kodeTindakan)
            ->orderBy('template_laboratorium.urut');
    }
}
