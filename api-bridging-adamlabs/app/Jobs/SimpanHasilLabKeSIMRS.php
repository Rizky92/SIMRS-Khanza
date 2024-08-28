<?php

namespace App\Jobs;

use App\Models\Pemeriksaan;
use App\Models\Registrasi;
use App\Models\SIMRS\HasilPeriksaLab;
use App\Models\SIMRS\HasilPeriksaLabDetail;
use App\Models\SIMRS\Jurnal;
use App\Models\SIMRS\KesanSaran;
use App\Models\SIMRS\PemeriksaanLab;
use App\Models\SIMRS\PermintaanLabPK;
use App\Models\SIMRS\TindakanLab;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\DB;
use Throwable;

class SimpanHasilLabKeSIMRS implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    private string $noLaboratorium;

    private string $noRegistrasi;

    private string $noRawat;

    private string $statusRawat;

    private string $tgl;

    private string $jam;

    private string $dokterPerujuk;

    private string $dokterPj;

    private string $username;

    private string $nip;

    private float $totalJasaMedisDokter = 0;

    private float $totalJasaMedisPetugas = 0;

    private float $totalKSO = 0;
    
    private float $totalPendapatan = 0;

    private float $totalBHP = 0;

    private float $totalJasaSarana = 0;

    private float $totalJasaPerujuk = 0;

    private float $totalManajemen = 0;

    /**
     * Create a new job instance.
     * 
     * @param  array{
     *     no_laboratorium: string,
     *     no_registrasi: string
     *     user: string
     * }  $params
     */
    public function __construct(array $params)
    {
        $this->noLaboratorium = $params['no_laboratorium'];
        $this->noRegistrasi = $params['no_registrasi'];
        $this->username = $params['username'];
    }

    /**
     * Execute the job.
     */
    public function handle()
    {
        $this->cariUser();
        $this->simpanHasilLab();
    }

    private function cariUser(): void
    {
        $this->nip = DB::connection('mysql_sik')->table('mapping_user_bridginglab')
            ->where('vendor', 'adamlabs')
            ->where('username', $this->username)
            ->value('nip');
    }

    private function simpanHasilLab(): void
    {
        try {
            $permintaanLab = PermintaanLabPK::query()
                ->where('noorder', $this->noRegistrasi)
                ->firstOrFail();

            $registrasi = Registrasi::query()
                ->with('pemeriksaan')
                ->where('no_laboratorium', $this->noLaboratorium)
                ->where('no_registrasi', $this->noRegistrasi)
                ->firstOrFail();

            $this->noRawat = $permintaanLab->no_rawat;
            $this->statusRawat = $permintaanLab->status;

            $waktuRegistrasi = carbon_immutable(optional($registrasi->pemeriksaan->first())->waktu_pemeriksaan);

            $this->tgl = $permintaanLab->tgl_hasil !== '0000-00-00' ? $permintaanLab->tgl_hasil : $waktuRegistrasi->toDateString();
            $this->jam = $permintaanLab->jam_hasil !== '00:00:00' ? $permintaanLab->jam_hasil : $waktuRegistrasi->format('H:i:s');

            $kategori = $registrasi->pemeriksaan->pluck('kategori_pemeriksaan_nama')->filter()->unique()->values();
            $tindakan = $registrasi->pemeriksaan->pluck('kode_tindakan_simrs')->filter()->unique()->values();
            $tindakanSudahAda = $registrasi->pemeriksaan->filter(fn ($p) => $p->status_bridging === true)->pluck('kode_tindakan_simrs')->filter()->unique()->values();
            $compound = $registrasi->pemeriksaan->pluck('compound')->filter()->unique()->values();

            $this->dokterPj = DB::connection('mysql_sik')->table('set_pjlab')->value('kd_dokterlab');

            DB::connection('mysql_sik')
                ->transaction(function () use ($registrasi, $kategori, $tindakan, $tindakanSudahAda, $compound) {
                    PermintaanLabPK::query()
                        ->where('noorder', $this->noRegistrasi)
                        ->update([
                            'tgl_hasil' => $this->tgl,
                            'jam_hasil' => $this->jam,
                        ]);
                    
                    TindakanLab::query()
                        ->whereIn('kd_jenis_prw', $tindakan->diff($tindakanSudahAda))
                        ->get()
                        ->each(function (TindakanLab $t) use ($registrasi) {
                            HasilPeriksaLab::create([
                                'no_rawat'               => $this->noRawat,
                                'nip'                    => $this->nip,
                                'kd_jenis_prw'           => $t->kd_jenis_prw,
                                'tgl_periksa'            => $this->tgl,
                                'jam'                    => $this->jam,
                                'dokter_perujuk'         => $registrasi->dokter_pengirim_kode,
                                'bagian_rs'              => $t->bagian_rs,
                                'bhp'                    => $t->bhp,
                                'tarif_perujuk'          => $t->tarif_perujuk,
                                'tarif_tindakan_dokter'  => $t->tarif_tindakan_dokter,
                                'tarif_tindakan_petugas' => $t->tarif_tindakan_petugas,
                                'kso'                    => $t->kso,
                                'menejemen'              => $t->menejemen,
                                'biaya'                  => $t->total_byr,
                                'kd_dokter'              => $this->dokterPj,
                                'status'                 => str($this->statusRawat)->title()->value(),
                                'kategori'               => 'PK'
                            ]);

                            $this->totalJasaSarana       += $t->bagian_rs;
                            $this->totalBHP              += $t->bhp;
                            $this->totalJasaPerujuk      += $t->tarif_perujuk;
                            $this->totalJasaMedisDokter  += $t->tarif_tindakan_dokter;
                            $this->totalJasaMedisPetugas += $t->tarif_tindakan_petugas;
                            $this->totalKSO              += $t->kso;
                            $this->totalManajemen        += $t->menejemen;
                            $this->totalPendapatan       += $t->total_byr;
                        });
                    
                    PemeriksaanLab::query()
                        ->untukHasilPemeriksaan($kategori, $tindakan, $compound)
                        ->get()
                        ->each(function (PemeriksaanLab $p) use ($registrasi) {
                            $pemeriksaan = $registrasi->pemeriksaan
                                ->whereStrict('kategori_pemeriksaan_nama', $p->kategori)
                                ->whereStrict('kode_tindakan_simrs', $p->kd_jenis_prw)
                                ->whereStrict('compound', $p->kode_compound)
                                ->first();

                            if ($pemeriksaan->status_bridging) {
                                HasilPeriksaLabDetail::query()
                                    ->where('no_rawat', $this->noRawat)
                                    ->where('kd_jenis_prw', $p->kd_jenis_prw)
                                    ->where('tgl_periksa', $this->tgl)
                                    ->where('jam', $this->jam)
                                    ->where('id_template', $p->id_template)
                                    ->update([
                                        'nilai'      => $pemeriksaan->hasil_nilai_hasil,
                                        'keterangan' => $pemeriksaan->hasil_flag_kode ?? '',
                                    ]);
                            } else {
                                HasilPeriksaLabDetail::create([
                                    'no_rawat'       => $this->noRawat,
                                    'kd_jenis_prw'   => $p->kd_jenis_prw,
                                    'tgl_periksa'    => $this->tgl,
                                    'jam'            => $this->jam,
                                    'id_template'    => $p->id_template,
                                    'nilai'          => $pemeriksaan->hasil_nilai_hasil,
                                    'nilai_rujukan'  => $pemeriksaan->hasil_nilai_rujukan ?? '',
                                    'keterangan'     => $pemeriksaan->hasil_flag_kode ?? '',
                                    'bagian_rs'      => $p->pemeriksaan_jasa_sarana,
                                    'bhp'            => $p->pemeriksaan_bhp,
                                    'bagian_perujuk' => $p->pemeriksaan_jasa_perujuk,
                                    'bagian_dokter'  => $p->pemeriksaan_jasa_medis_dokter,
                                    'bagian_laborat' => $p->pemeriksaan_jasa_medis_petugas,
                                    'kso'            => $p->pemeriksaan_kso,
                                    'menejemen'      => $p->pemeriksaan_manajemen,
                                    'biaya_item'     => $p->pemeriksaan_pendapatan,
                                ]);
        
                                $this->totalJasaSarana       += $p->pemeriksaan_jasa_sarana;
                                $this->totalBHP              += $p->pemeriksaan_bhp;
                                $this->totalJasaPerujuk      += $p->pemeriksaan_jasa_perujuk;
                                $this->totalJasaMedisDokter  += $p->pemeriksaan_jasa_medis_dokter;
                                $this->totalJasaMedisPetugas += $p->pemeriksaan_jasa_medis_petugas;
                                $this->totalKSO              += $p->pemeriksaan_kso;
                                $this->totalManajemen        += $p->pemeriksaan_manajemen;
                                $this->totalPendapatan       += $p->pemeriksaan_pendapatan;
                            }
                        });

                    $this->isiCatatanPemeriksaan($registrasi);

                    $this->catatJurnal();
                });
        } catch (Throwable $e) {
            Pemeriksaan::query()
                ->where('no_registrasi', $this->noRegistrasi)
                ->where('no_laboratorium', $this->noLaboratorium)
                ->where('status_bridging', false)
                ->delete();

            if (Pemeriksaan::query()
                ->where('no_registrasi', $this->noRegistrasi)
                ->where('no_laboratorium', $this->noLaboratorium)
                ->count() === 0
            ) {
                Registrasi::query()
                    ->where('no_registrasi', $this->noRegistrasi)
                    ->where('no_laboratorium', $this->noLaboratorium)
                    ->delete();
            }

            throw $e;
        }
    }

    private function isiCatatanPemeriksaan(Registrasi $registrasi): void
    {
        if (! KesanSaran::query()
            ->where('no_rawat', $this->noRawat)
            ->where('tgl_periksa', $this->tgl)
            ->where('jam', $this->jam)
            ->exists()
        ) {
            KesanSaran::create([
                'no_rawat'    => $this->noRawat,
                'tgl_periksa' => $this->tgl,
                'jam'         => $this->jam,
                'saran'       => '',
                'kesan'       => $registrasi->keterangan_hasil,
            ]);
        } else {
            KesanSaran::query()
                ->where('no_rawat', $this->noRawat)
                ->where('tgl_periksa', $this->tgl)
                ->where('jam', $this->jam)
                ->update(['kesan' => $registrasi->keterangan_hasil]);
        }
    }

    private function catatJurnal(): void
    {
        $akunLaborat = null;
        $statusRawat = null;

        if ($this->statusRawat === 'ranap') {
            $akunLaborat = DB::connection('mysql_sik')
                ->table('set_akun_ranap')
                ->select([
                    'Suspen_Piutang_Laborat_Ranap as suspen_piutang',
                    'Laborat_Ranap as tindakan_laborat',
                    'Beban_Jasa_Medik_Dokter_Laborat_Ranap as beban_jasa_medik_dokter',
                    'Utang_Jasa_Medik_Dokter_Laborat_Ranap as utang_jasa_medik_dokter',
                    'Beban_Jasa_Medik_Petugas_Laborat_Ranap as beban_jasa_medik_petugas',
                    'Utang_Jasa_Medik_Petugas_Laborat_Ranap as utang_jasa_medik_petugas',
                    'Beban_Kso_Laborat_Ranap as beban_kso',
                    'Utang_Kso_Laborat_Ranap as utang_kso',
                    'HPP_Persediaan_Laborat_Rawat_inap as hpp_persediaan',
                    'Persediaan_BHP_Laborat_Rawat_Inap as persediaan_bhp',
                    'Beban_Jasa_Sarana_Laborat_Ranap as beban_jasa_sarana',
                    'Utang_Jasa_Sarana_Laborat_Ranap as utang_jasa_sarana',
                    'Beban_Jasa_Perujuk_Laborat_Ranap as beban_jasa_perujuk',
                    'Utang_Jasa_Perujuk_Laborat_Ranap as utang_jasa_perujuk',
                    'Beban_Jasa_Menejemen_Laborat_Ranap as beban_jasa_manajemen',
                    'Utang_Jasa_Menejemen_Laborat_Ranap as utang_jasa_manajemen',
                ])
                ->first();
            $statusRawat = 'RAWAT INAP';
        } else {
            $akunLaborat = DB::connection('mysql_sik')
                ->table('set_akun_ralan')
                ->select([
                    'Suspen_Piutang_Laborat_Ralan as suspen_piutang',
                    'Laborat_Ralan as tindakan_laborat',
                    'Beban_Jasa_Medik_Dokter_Laborat_Ralan as beban_jasa_medik_dokter',
                    'Utang_Jasa_Medik_Dokter_Laborat_Ralan as utang_jasa_medik_dokter',
                    'Beban_Jasa_Medik_Petugas_Laborat_Ralan as beban_jasa_medik_petugas',
                    'Utang_Jasa_Medik_Petugas_Laborat_Ralan as utang_jasa_medik_petugas',
                    'Beban_Kso_Laborat_Ralan as beban_kso',
                    'Utang_Kso_Laborat_Ralan as utang_kso',
                    'HPP_Persediaan_Laborat_Rawat_Jalan as hpp_persediaan',
                    'Persediaan_BHP_Laborat_Rawat_Jalan as persediaan_bhp',
                    'Beban_Jasa_Sarana_Laborat_Ralan as beban_jasa_sarana',
                    'Utang_Jasa_Sarana_Laborat_Ralan as utang_jasa_sarana',
                    'Beban_Jasa_Perujuk_Laborat_Ralan as beban_jasa_perujuk',
                    'Utang_Jasa_Perujuk_Laborat_Ralan as utang_jasa_perujuk',
                    'Beban_Jasa_Menejemen_Laborat_Ralan as beban_jasa_manajemen',
                    'Utang_Jasa_Menejemen_Laborat_Ralan as utang_jasa_manajemen',
                ])
                ->first();
            $statusRawat = 'RAWAT JALAN';
        }

        if (! $akunLaborat) {
            return;
        }

        $detailJurnal = collect();

        if ($this->totalPendapatan > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->suspen_piutang, 'debet' => $this->totalPendapatan, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->tindakan_laborat, 'debet' => 0, 'kredit' => $this->totalPendapatan]);
        }

        if ($this->totalJasaMedisDokter > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->beban_jasa_medis_dokter, 'debet' => $this->totalJasaMedisDokter, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->utang_jasa_medis_dokter, 'debet' => 0, 'kredit' => $this->totalJasaMedisDokter]);
        }

        if ($this->totalJasaMedisPetugas > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->beban_jasa_medis_petugas, 'debet' => $this->totalJasaMedisPetugas, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->utang_jasa_medis_petugas, 'debet' => 0, 'kredit' => $this->totalJasaMedisPetugas]);
        }

        if ($this->totalBHP > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->hpp_persediaan, 'debet' => $this->totalBHP, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->persediaan_bhp, 'debet' => 0, 'kredit' => $this->totalBHP]);
        }

        if ($this->totalKSO > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->beban_kso, 'debet' => $this->totalKSO, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->utang_kso, 'debet' => 0, 'kredit' => $this->totalKSO]);
        }

        if ($this->totalJasaSarana > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->beban_jasa_sarana, 'debet' => $this->totalJasaSarana, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->utang_jasa_sarana, 'debet' => 0, 'kredit' => $this->totalJasaSarana]);
        }

        if ($this->totalJasaPerujuk > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->beban_jasa_perujuk, 'debet' => $this->totalJasaPerujuk, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->utang_jasa_perujuk, 'debet' => 0, 'kredit' => $this->totalJasaPerujuk]);
        }

        if ($this->totalManajemen > 0) {
            $detailJurnal->push(['kd_rek' => $akunLaborat->beban_manajemen, 'debet' => $this->totalManajemen, 'kredit' => 0]);
            $detailJurnal->push(['kd_rek' => $akunLaborat->utang_manajemen, 'debet' => 0, 'kredit' => $this->totalManajemen]);
        }

        $detailJurnal = $detailJurnal->reject(fn (array $value): bool =>
            isset($value['kd_rek'], $value['debet'], $value['kredit']) &&
            (round($value['debet'], 2) === 0.00 && round($value['kredit'], 2) === 0.00)
        );

        if ($detailJurnal->isNotEmpty()) {
            Jurnal::catat(
                $this->noRawat,
                sprintf('PEMERIKSAAN LABORAT RAWAT %s, DIPOSTING OLEH %s', str()->title($this->statusRawat), $this->nip),
                'now',
                $detailJurnal->all()        
            );
        }
    }
}
