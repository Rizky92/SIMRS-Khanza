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
            'no_laboratorium'                                      => ['required', 'exists:registrasi,no_laboratorium'],
            'no_registrasi'                                        => ['required'],
            'waktu_registrasi'                                     => ['sometimes', 'nullable', 'date'],
            'keterangan_hasil'                                     => ['nullable', 'string', 'max:700'],
            'diagnosa_awal'                                        => ['nullable', 'string'],
            'kode_RS'                                              => ['required', 'string'],
            'kode_lab'                                             => ['required', 'string'],
            'username'                                             => ['sometimes', 'nullable', 'string'],
            'nama_pegawai'                                         => ['sometimes', 'nullable', 'string'],
            'umur.tahun'                                           => ['nullable', 'integer'],
            'umur.bulan'                                           => ['nullable', 'integer'],
            'umur.hari'                                            => ['nullable', 'integer'],
            'pasien.no_rm'                                         => ['nullable', 'string'],
            'pasien.nama_pasien'                                   => ['sometimes', 'nullable', 'string'],
            'pasien.jenis_kelamin'                                 => ['sometimes', 'nullable', 'string'],
            'pasien.tanggal_lahir'                                 => ['sometimes', 'nullable', 'string'],
            'pasien.alamat'                                        => ['sometimes', 'nullable', 'string'],
            'pasien.nik'                                           => ['sometimes', 'nullable', 'string'],
            'pasien.no_telphone'                                   => ['sometimes', 'nullable', 'string'],
            'pasien.ras'                                           => ['sometimes', 'nullable', 'string'],
            'pasien.berat_badan'                                   => ['nullable', 'string'],
            'pasien.jenis_registrasi'                              => ['nullable', Rule::in(['Reguler', 'Cito'])],
            'dokter_pengirim.kode'                                 => ['nullable'],
            'dokter_pengirim.nama'                                 => ['nullable'],
            'unit_asal.kode'                                       => ['nullable'],
            'unit_asal.nama'                                       => ['nullable'],
            'penjamin.kode'                                        => ['nullable'],
            'penjamin.nama'                                        => ['nullable'],
            'username'                                             => ['sometimes', 'nullable', 'string'],
            'nama_pegawai'                                         => ['sometimes', 'nullable', 'string'],
            'dokter_penanggung_jawab'                              => ['sometimes', 'nullable', 'string'],
            'pemeriksaan'                                          => ['array'],
            'pemeriksaan.*.nomor_urut'                             => ['nullable', 'integer'],
            'pemeriksaan.*.kode_tindakan_simrs'                    => ['required', 'string'],
            'pemeriksaan.*.kode_pemeriksaan_lis'                   => ['required', 'string'],
            'pemeriksaan.*.nama_pemeriksaan_lis'                   => ['required', 'string'],
            'pemeriksaan.*.metode'                                 => ['nullable', 'string'],
            'pemeriksaan.*.waktu_pemeriksaan'                      => ['nullable', 'date'],
            'pemeriksaan.*.status_bridging'                        => ['nullable', 'boolean'],
            'pemeriksaan.*.kategori_pemeriksaan.nama_kategori'     => ['nullable', 'string'],
            'pemeriksaan.*.kategori_pemeriksaan.nomor_urut'        => ['nullable', 'integer'],
            'pemeriksaan.*.sub_kategori_pemeriksaan.nama_kategori' => ['nullable', 'string'],
            'pemeriksaan.*.sub_kategori_pemeriksaan.nomor_urut'    => ['nullable', 'integer'],
            'pemeriksaan.*.hasil.satuan'                           => ['sometimes', 'nullable', 'string'],
            'pemeriksaan.*.hasil.nilai_hasil'                      => ['present', 'required', 'string'],
            'pemeriksaan.*.hasil.nilai_rujukan'                    => ['sometimes', 'nullable', 'string'],
            'pemeriksaan.*.hasil.flag_kode'                        => ['sometimes', 'nullable', 'string'],
        ];
    }
}
