<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('registrasi', function (Blueprint $table) {
            $table->string('no_laboratorium', 20);
            $table->string('no_registrasi', 15)->index();
            $table->dateTime('waktu_registrasi')->index();
            $table->string('keterangan_hasil', 700)->nullable();
            $table->string('diagnosa_awal', 20)->nullable();
            $table->string('kode_rs', 10)->nullable();
            $table->string('kode_lab', 10)->nullable();
            $table->string('username', 100)->nullable();
            $table->string('nama_pegawai', 150)->nullable();
            $table->string('dokter_penanggung_jawab', 100)->nullable();
            $table->integer('umur_tahun')->unsgined()->nullable();
            $table->integer('umur_bulan')->unsigned()->nullable();
            $table->integer('umur_hari')->unsigned()->nullable();
            $table->string('pasien_no_rm', 20)->index();
            $table->string('pasien_nama_pasien', 100)->index();
            $table->string('pasien_jenis_kelamin', 20)->nullable()->index();
            $table->date('pasien_tanggal_lahir')->nullable()->index();
            $table->string('pasien_alamat', 100)->nullable()->index();
            $table->string('pasien_nik', 20)->nullable()->index();
            $table->string('pasien_no_telphone', 20)->nullable()->index();
            $table->string('pasien_ras', 10)->nullable();
            $table->string('pasien_berat_badan', 10)->nullable();
            $table->string('pasien_jenis_registrasi', 10)->nullable()->index();
            $table->string('dokter_pengirim_kode', 10)->nullable()->index();
            $table->string('dokter_pengirim_nama', 80)->nullable()->index();
            $table->string('unit_asal_kode', 10)->nullable()->index();
            $table->string('unit_asal_nama', 30)->nullable()->index();
            $table->string('penjamin_kode', 4)->nullable()->index();
            $table->string('penjamin_nama', 100)->nullable()->index();

            $table->primary('no_laboratorium');
        });
    }
};
