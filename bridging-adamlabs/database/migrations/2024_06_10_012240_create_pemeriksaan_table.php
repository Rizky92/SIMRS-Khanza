<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('pemeriksaan', function (Blueprint $table) {
            $table->id();
            $table->string('no_laboratorium', 20);
            $table->string('no_registrasi', 15)->index();
            $table->string('kategori_pemeriksaan_nama', 30)->nullable()->index();
            $table->integer('kategori_pemeriksaan_urut')->nullable();
            $table->string('sub_kategori_pemeriksaan_nama', 30)->nullable()->index();
            $table->integer('sub_kategori_pemeriksaan_urut')->nullable();
            $table->integer('nomor_urut')->nullable();
            $table->string('kode_tindakan_simrs', 20)->index();
            $table->string('kode_pemeriksaan_lis', 15)->index();
            $table->string('nama_pemeriksaan_lis', 50)->index();
            $table->string('metode', 30)->nullable()->index();
            $table->dateTime('waktu_pemeriksaan')->nullable()->index();
            $table->boolean('status_bridging')->default(0);
            $table->string('hasil_satuan', 12)->nullable()->index();
            $table->string('hasil_nilai_hasil', 500)->nullable()->index();
            $table->string('hasil_nilai_rujukan', 500)->nullable()->index();
            $table->string('hasil_flag_kode', 3)->nullable()->index();
            $table->string('compound')->index();

            $table->foreign('no_laboratorium')
                ->references('no_laboratorium')
                ->on('registrasi')
                ->cascadeOnUpdate()
                ->cascadeOnDelete();
        });
    }
};
