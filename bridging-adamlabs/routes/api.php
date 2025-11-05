<?php

use App\Http\Controllers\API\SimpanHasilLabController;
use App\Http\Controllers\API\UpdateHasilLabController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::group(['middleware' => 'api.key'], function () {
    Route::post('/adam-lis/bridging', SimpanHasilLabController::class);
    Route::post('/adam-lis/bridging/update-hasil', UpdateHasilLabController::class);
});