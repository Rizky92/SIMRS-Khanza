<?php

namespace App\Http\Controllers\API;

use App\Http\Requests\API\SimpanHasilLabRequest;
use App\Jobs\SimpanHasilLabKeSIMRS;
use App\Models\Pemeriksaan;
use App\Models\Registrasi;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Arr;

class SimpanHasilLabController
{
    public function __invoke(SimpanHasilLabRequest $request): JsonResponse
    {
        $data = $request->validated();

        tracker_start('mysql');
        Registrasi::firstOrCreate(
            ['no_registrasi'   => Arr::get($data, 'no_registrasi'), 'no_laboratorium' => Arr::get($data, 'no_laboratorium')],
            [
                'waktu_registrasi'        => Arr::get($data, 'waktu_registrasi'),
                'keterangan_hasil'        => Arr::get($data, 'keterangan_hasil'),
                'diagnosa_awal'           => Arr::get($data, 'diagnosa_awal'),
                'kode_rs'                 => Arr::get($data, 'kode_RS'),
                'kode_lab'                => Arr::get($data, 'kode_lab'),
                'username'                => Arr::get($data, 'username'),
                'nama_pegawai'            => Arr::get($data, 'nama_pegawai'),
                'dokter_penanggung_jawab' => Arr::get($data, 'dokter_penanggung_jawab'),
                'umur_tahun'              => Arr::get($data, 'umur.tahun'),
                'umur_bulan'              => Arr::get($data, 'umur.bulan'),
                'umur_hari'               => Arr::get($data, 'umur.hari'),
                'pasien_no_rm'            => Arr::get($data, 'pasien.no_rm'),
                'pasien_nama_pasien'      => Arr::get($data, 'pasien.nama_pasien'),
                'pasien_jenis_kelamin'    => Arr::get($data, 'pasien.jenis_kelamin'),
                'pasien_tanggal_lahir'    => Arr::get($data, 'pasien.tanggal_lahir'),
                'pasien_alamat'           => Arr::get($data, 'pasien.alamat'),
                'pasien_nik'              => Arr::get($data, 'pasien.nik'),
                'pasien_no_telphone'      => Arr::get($data, 'pasien.no_telphone'),
                'pasien_ras'              => Arr::get($data, 'pasien.ras'),
                'pasien_berat_badan'      => Arr::get($data, 'pasien.berat_badan'),
                'pasien_jenis_registrasi' => Arr::get($data, 'pasien.jenis_registrasi'),
                'dokter_pengirim_kode'    => Arr::get($data, 'dokter_pengirim.kode'),
                'dokter_pengirim_nama'    => Arr::get($data, 'dokter_pengirim.nama'),
                'unit_asal_kode'          => Arr::get($data, 'unit_asal.kode'),
                'unit_asal_nama'          => Arr::get($data, 'unit_asal.nama'),
                'penjamin_kode'           => Arr::get($data, 'penjamin.kode'),
                'penjamin_nama'           => Arr::get($data, 'penjamin.nama'),
            ]
        );

        foreach ($data['pemeriksaan'] as $pemeriksaan) {
            if (Arr::get($pemeriksaan, 'status_bridging')) {
                Pemeriksaan::query()
                    ->where('no_laboratorium', $data['no_laboratorium'])
                    ->where('no_registrasi', $data['no_registrasi'])
                    ->where('kode_tindakan_simrs', Arr::get($pemeriksaan, 'kode_tindakan_simrs'))
                    ->where('kode_pemeriksaan_lis', Arr::get($pemeriksaan, 'kode_pemeriksaan_lis'))
                    ->where('nama_pemeriksaan_lis', Arr::get($pemeriksaan, 'nama_pemeriksaan_lis'))
                    ->update([
                        'status_bridging'   => true,
                        'hasil_nilai_hasil' => Arr::get($pemeriksaan, 'hasil.nilai_hasil'),
                        'hasil_flag_kode'   => Arr::get($pemeriksaan, 'hasil.flag_kode'),
                    ]);
            } else {
                Pemeriksaan::create([
                    'no_laboratorium'               => $data['no_laboratorium'],
                    'no_registrasi'                 => $data['no_registrasi'],
                    'kategori_pemeriksaan_nama'     => Arr::get($pemeriksaan, 'kategori_pemeriksaan.nama_kategori'),
                    'kategori_pemeriksaan_urut'     => Arr::get($pemeriksaan, 'kategori_pemeriksaan.nomor_urut'),
                    'sub_kategori_pemeriksaan_nama' => Arr::get($pemeriksaan, 'sub_kategori_pemeriksaan.nama_sub_kategori'),
                    'sub_kategori_pemeriksaan_urut' => Arr::get($pemeriksaan, 'sub_kategori_pemeriksaan.nomor_urut'),
                    'nomor_urut'                    => Arr::get($pemeriksaan, 'nomor_urut'),
                    'kode_tindakan_simrs'           => Arr::get($pemeriksaan, 'kode_tindakan_simrs'),
                    'kode_pemeriksaan_lis'          => Arr::get($pemeriksaan, 'kode_pemeriksaan_lis'),
                    'nama_pemeriksaan_lis'          => Arr::get($pemeriksaan, 'nama_pemeriksaan_lis'),
                    'metode'                        => Arr::get($pemeriksaan, 'metode'),
                    'waktu_pemeriksaan'             => Arr::get($pemeriksaan, 'waktu_pemeriksaan'),
                    'status_bridging'               => false,
                    'hasil_satuan'                  => Arr::get($pemeriksaan, 'hasil.satuan'),
                    'hasil_nilai_hasil'             => Arr::get($pemeriksaan, 'hasil.nilai_hasil'),
                    'hasil_nilai_rujukan'           => Arr::get($pemeriksaan, 'hasil.nilai_rujukan'),
                    'hasil_flag_kode'               => Arr::get($pemeriksaan, 'hasil.flag_kode'),
                    'compound'                      => sprintf('%s-%s-adamlabs', Arr::get($pemeriksaan, 'kode_pemeriksaan_lis'), Arr::get($pemeriksaan, 'kategori_pemeriksaan.nama_kategori')),
                ]);
            }
        }
        tracker_end('mysql', $data['username']);

        SimpanHasilLabKeSIMRS::dispatch([
            'no_laboratorium' => $data['no_laboratorium'],
            'no_registrasi'   => $data['no_registrasi'],
            'username'        => $data['username'],
        ]);

        return response()->json([
            'status'  => true,
            'code'    => 200,
            'message' => 'Hasil pemeriksaan segera diproses.',
        ]);
    }
}
