<?php

namespace App\Jobs;

use App\Models\Registrasi;
use App\Models\SIMRS\KesanSaran;
use App\Models\SIMRS\PemeriksaanLab;
use App\Models\SIMRS\PeriksaLabDetail;
use App\Models\SIMRS\PermintaanLabPK;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\DB;

class UpdateHasilLabKeSIMRS implements ShouldQueue
{
    use Dispatchable;
    use InteractsWithQueue;
    use Queueable;
    use SerializesModels;

    private string $noLaboratorium;

    private string $noRegistrasi;

    private string $noRawat;

    private string $tgl;

    private string $jam;

    private string $username;

    /**
     * Create a new job instance.
     *
     * @param  array{no_laboratorium: string, no_registrasi: string, username: string}  $options
     */
    public function __construct(array $options)
    {
        $this->noLaboratorium = $options['no_laboratorium'];
        $this->noRegistrasi = $options['no_registrasi'];
        $this->username = $options['username'];
    }

    /**
     * Execute the job.
     */
    public function handle()
    {
        $this->simpanHasilLab();
    }

    private function simpanHasilLab(): void
    {
        $permintaanLab = PermintaanLabPK::query()
            ->where('noorder', $this->noRegistrasi)
            ->firstOrFail();

        $registrasi = Registrasi::query()
            ->with('pemeriksaan', fn ($q) => $q->where('status_bridging', 1))
            ->where('no_laboratorium', $this->noLaboratorium)
            ->where('no_registrasi', $this->noRegistrasi)
            ->firstOrFail();

        $this->noRawat = $permintaanLab->no_rawat;
        $this->tgl = $permintaanLab->tgl_hasil;
        $this->jam = $permintaanLab->jam_hasil;

        $kategori = $registrasi->pemeriksaan->pluck('kategori_pemeriksaan_nama')->filter()->unique()->values();
        $tindakan = $registrasi->pemeriksaan->pluck('kode_tindakan_simrs')->filter()->unique()->values();
        $compound = $registrasi->pemeriksaan->pluck('compound')->filter()->unique()->values();

        DB::connection('mysql_sik')->transaction(function () use ($registrasi, $kategori, $tindakan, $compound) {
            PemeriksaanLab::query()
                ->untukHasilPemeriksaan($kategori, $tindakan, $compound)
                ->get()
                ->each(function (PemeriksaanLab $p) use ($registrasi) {
                    $pemeriksaan = $registrasi->pemeriksaan
                        ->whereStrict('kategori_pemeriksaan_nama', $p->kategori)
                        ->whereStrict('kode_tindakan_simrs', $p->kd_jenis_prw)
                        ->whereStrict('compound', $p->kode_compound)
                        ->first();

                    tracker_start('mysql_sik');
                    PeriksaLabDetail::query()
                        ->where('no_rawat', $this->noRawat)
                        ->where('kd_jenis_prw', $p->kd_jenis_prw)
                        ->where('tgl_periksa', $this->tgl)
                        ->where('jam', $this->jam)
                        ->where('id_template', $p->id_template)
                        ->update([
                            'nilai'      => $pemeriksaan->hasil_nilai_hasil,
                            'keterangan' => $pemeriksaan->hasil_flag_kode ?? '',
                        ]);
                    tracker_end('mysql_sik', $this->username);
                });

            $this->isiCatatanPemeriksaan($registrasi);
        });
    }

    private function isiCatatanPemeriksaan(Registrasi $registrasi): void
    {
        if (! KesanSaran::query()
            ->where('no_rawat', $this->noRawat)
            ->where('tgl_periksa', $this->tgl)
            ->where('jam', $this->jam)
            ->exists()
        ) {
            tracker_start('mysql_sik');
            KesanSaran::create([
                'no_rawat'    => $this->noRawat,
                'tgl_periksa' => $this->tgl,
                'jam'         => $this->jam,
                'saran'       => '',
                'kesan'       => $registrasi->keterangan_hasil,
            ]);
            tracker_end('mysql_sik', $this->username);
        } else {
            tracker_start('mysql_sik');
            KesanSaran::query()
                ->where('no_rawat', $this->noRawat)
                ->where('tgl_periksa', $this->tgl)
                ->where('jam', $this->jam)
                ->update(['kesan' => $registrasi->keterangan_hasil]);
            tracker_end('mysql_sik', $this->username);
        }
    }
}
