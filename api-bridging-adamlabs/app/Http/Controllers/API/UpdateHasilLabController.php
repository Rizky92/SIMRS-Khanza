<?php

namespace App\Http\Controllers\API;

use App\Http\Requests\API\UpdateHasilLabRequest;
use App\Jobs\UpdateHasilLabKeSIMRS;
use App\Models\Pemeriksaan;
use App\Models\Registrasi;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Arr;

class UpdateHasilLabController
{
    public function __invoke(UpdateHasilLabRequest $request): JsonResponse
    {
        $data = $request->validated();

        tracker_start('mysql');
        Registrasi::query()
            ->where('no_laboratorium', $data['no_laboratorium'])
            ->where('no_registrasi', $data['no_registrasi'])
            ->update([
                'keterangan_hasil' => $data['keterangan_hasil'],
            ]);

        foreach ($data['pemeriksaan'] as $pemeriksaan) {
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
        }
        tracker_end('mysql', $data['username']);

        UpdateHasilLabKeSIMRS::dispatch([
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
