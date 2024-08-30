<?php

use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;

if (! function_exists('tracker_start')) {
    function tracker_start(string $connection = 'mysql'): void
    {
        if (app()->runningUnitTests()) {
            return;
        }

        DB::connection($connection)->enableQueryLog();
    }
}

if (! function_exists('tracker_end')) {
    function tracker_end(string $connection = 'mysql'): void
    {
        if (! DB::connection($connection)->logging()) {
            return;
        }

        if (app()->runningUnitTests()) {
            DB::connection($connection)->disableQueryLog();

            return;
        }

        foreach (DB::connection($connection)->getQueryLog() as $log) {
            foreach ($log['bindings'] as $pos => $value) {
                if (is_string($value)) {
                    $log['bindings'][$pos] = "'{$value}'";
                }
            }

            $sql = str($log['query'])
                ->replaceArray('?', $log['bindings'])
                ->value();

            DB::connection('mysql_smc')->table('trackersql')->insert([
                'tanggal'    => now(),
                'sqle'       => $sql,
                'connection' => $connection,
            ]);
        }

        DB::connection($connection)->flushQueryLog();
        DB::connection($connection)->disableQueryLog();
    }
}

if (! function_exists('tracker_dispose')) {
    function tracker_dispose(string $connection): void
    {
        DB::connection($connection)->flushQueryLog();
        DB::connection($connection)->disableQueryLog();
    }
}

if (! function_exists('str')) {
    /**
     * @template  T of string|null
     *
     * @param  \T  $value
     * @return \Illuminate\Support\Stringable|string|mixed
     *
     * @psalm-return (T is null ? object : \Illuminate\Support\Stringable)
     */
    function str($value = null)
    {
        if (func_num_args() === 0) {
            return new class
            {
                public function __call($method, $parameters)
                {
                    return Str::$method(...$parameters);
                }

                public function __toString()
                {
                    return '';
                }
            };
        }

        return Str::of($value);
    }
}
