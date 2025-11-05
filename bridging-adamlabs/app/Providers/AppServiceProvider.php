<?php

namespace App\Providers;

use Illuminate\Support\Collection;
use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Stringable;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     *
     * @return void
     */
    public function register()
    {
        //
    }

    /**
     * Bootstrap any application services.
     *
     * @return void
     */
    public function boot()
    {
        Stringable::macro('value', fn (): string => $this->value);
        Stringable::macro('toInt', fn (): int => intval($this->value));
        Stringable::macro('wrap', fn (string $startsWith, ?string $endsWith = null): Stringable => is_null($endsWith)
            ? new Stringable($startsWith.$this->value.$startsWith)
            : new Stringable($startsWith.$this->value.$endsWith));
    }
}
