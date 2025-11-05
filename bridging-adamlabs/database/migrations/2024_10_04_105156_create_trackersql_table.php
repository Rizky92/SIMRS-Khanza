<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::connection('mysql')->create('trackersql', function (Blueprint $table) {
            $table->dateTime('tanggal')->useCurrent();
            $table->text('sqle');
            $table->string('usere', 20);
            $table->string('ip', 45);
            $table->string('connection', 20);
            
            $table->index('tanggal');
            $table->index('usere');
            $table->index('connection');
        });
    }
};
