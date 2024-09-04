<?php

namespace App\Models\SIMRS;

use App\Exceptions\InequalJournalException;
use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Support\Arr;
use Illuminate\Support\Str;
use RuntimeException;

class Jurnal extends Model
{
    protected $connection = 'mysql_sik';

    protected $primaryKey = 'no_jurnal';

    protected $keyType = 'string';

    protected $table = 'jurnal';

    public $incrementing = false;

    public $timestamps = false;

    protected $fillable = [
        'no_jurnal',
        'no_bukti',
        'keterangan',
        'jenis',
        'tgl_jurnal',
        'jam_jurnal',
    ];

    protected $searchColumns = [
        'no_jurnal',
        'no_bukti',
        'keterangan',
    ];

    public function detail(): HasMany
    {
        return $this->hasMany(JurnalDetail::class, 'no_jurnal', 'no_jurnal');
    }

    /**
     * @param  \DateTimeInterface|string  $date
     */
    public static function noJurnalBaru($date): string
    {
        $date = carbon($date)->format('Ymd');

        $index = 1;

        $noJurnalTerakhir = static::query()
            ->whereRaw('no_jurnal like ?', [str($date)->wrap('JR', '%')->value()])
            ->orderBy('no_jurnal', 'desc')
            ->value('no_jurnal');

        if ($noJurnalTerakhir) {
            $index += str($noJurnalTerakhir)->substr(-6)->toInt();
        }

        return str('JR')
            ->append($date)
            ->append(Str::padLeft((string) $index, 6, '0'))
            ->value();
    }

    /**
     * @param  "U"|"P"  $jenis
     * @param  Carbon|\DateTime|string  $waktuTransaksi
     * @param  array<array{kd_rek: string, debet: int|float, kredit: int|float}>  $detail
     * @return static
     */
    public static function catat(string $noBukti, string $keterangan, $waktuTransaksi, array $detail, string $jenis = 'U'): ?self
    {
        if (! $waktuTransaksi instanceof Carbon) {
            $waktuTransaksi = carbon($waktuTransaksi);
        }

        if ($waktuTransaksi->isToday()) {
            $waktuTransaksi = now();
        }

        $noJurnal = static::noJurnalBaru($waktuTransaksi);

        $detail = collect($detail);

        $detail = collect($detail)
            ->each(function (array $d) {
                if ($d['debet'] < 0 || $d['kredit'] < 0) {
                    throw new RuntimeException('Proses penjurnalan tidak boleh memiliki transaksi < 0');
                }
            });

        [$debet, $kredit] = [round($detail->sum('debet'), 2), round($detail->sum('kredit'), 2)];

        if ($debet !== $kredit) {
            throw new InequalJournalException($debet, $kredit, $noJurnal);
        }

        $jurnal = static::create([
            'no_jurnal'  => $noJurnal,
            'no_bukti'   => $noBukti,
            'keterangan' => $keterangan,
            'jenis'      => $jenis,
            'tgl_jurnal' => $waktuTransaksi->format('Y-m-d'),
            'jam_jurnal' => $waktuTransaksi->format('H:i:s'),
        ]);

        $detail = $jurnal
            ->detail()
            ->createMany($detail);

        return $jurnal->load('detail');
    }
}
