<?php

namespace App\Http\Requests\API;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class UpdateHasilLabRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
            'no_laboratorium'                                          => ['required', 'exists:registrasi,no_laboratorium'],
            'no_registrasi'                                            => ['required'],
            'kode_RS'                                                  => ['required', 'string'],
            'kode_lab'                                                 => ['required', 'string'],
            'username'                                                 => ['sometimes', 'nullable', 'string'],
            'pasien.no_rm'                                             => ['nullable', 'string'],
            'pasien.nama_pasien'                                       => ['sometimes', 'nullable', 'string'],
            'pasien.jenis_kelamin'                                     => ['sometimes', 'nullable', 'string'],
            'pasien.tanggal_lahir'                                     => ['sometimes', 'nullable', 'string'],
            'pasien.nik'                                               => ['sometimes', 'nullable', 'string'],
            'pasien.ras'                                               => ['sometimes', 'nullable', 'string'],
            'pasien.berat_badan'                                       => ['nullable', 'string'],
            'pasien.jenis_registrasi'                                  => ['nullable', Rule::in(['Reguler', 'Cito'])],
            'username'                                                 => ['sometimes', 'nullable', 'string'],
            'nama_pegawai'                                             => ['sometimes', 'nullable', 'string'],
            'dokter_penanggung_jawab'                                  => ['sometimes', 'nullable', 'string'],
            'pemeriksaan'                                              => ['array'],
            'pemeriksaan.*.kategori_pemeriksaan.nama_kategori'         => ['nullable', 'string'],
            'pemeriksaan.*.sub_kategori_pemeriksaan.nama_sub_kategori' => ['nullable', 'string'],
            'pemeriksaan.*.nomor_urut'                                 => ['nullable', 'integer'],
            'pemeriksaan.*.kode_tindakan_simrs'                        => ['required', 'string'],
            'pemeriksaan.*.kode_pemeriksaan_lis'                       => ['required', 'string'],
            'pemeriksaan.*.nama_pemeriksaan_lis'                       => ['required', 'string'],
            'pemeriksaan.*.status_bridging'                            => ['nullable', 'boolean'],
            'pemeriksaan.*.hasil.nilai_hasil'                          => ['present', 'required', 'string'],
            'pemeriksaan.*.hasil.flag_kode'                            => ['sometimes', 'nullable', 'string'],
        ];
    }
}
