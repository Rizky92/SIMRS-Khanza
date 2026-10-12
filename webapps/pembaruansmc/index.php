<?php

require_once __DIR__ . '/../conf/conf.php';

if (!is_file(__DIR__ . '/config.php')) {
    kirimGalat(500, 'config.php belum dibuat, salin dari config.example.php');
}

require_once __DIR__ . '/config.php';

const KANAL_PEMBARUAN   = ['sumberdaya'];
const JEDA_STABIL_DETIK = 30;

if (!autentikasiValid()) {
    kirimGalat(401, 'Autentikasi gagal');
}

$kanal = $_GET['kanal'] ?? '';
$aksi  = $_GET['aksi'] ?? '';

if (!in_array($kanal, KANAL_PEMBARUAN, true)) {
    kirimGalat(404, 'Kanal tidak dikenal');
}

$direktoriKanal = realpath(DIREKTORI_PEMBARUANSMC . DIRECTORY_SEPARATOR . $kanal);

if ($direktoriKanal === false || !is_dir($direktoriKanal)) {
    kirimGalat(404, 'Direktori kanal tidak ditemukan');
}

switch ($aksi) {
    case 'manifest':
        kirimManifest($kanal, $direktoriKanal);
        break;
    case 'berkas':
        kirimBerkas($direktoriKanal, $_SERVER['HTTP_X_BERKAS'] ?? '');
        break;
    default:
        kirimGalat(400, 'Aksi tidak dikenal');
}

function autentikasiValid(): bool
{
    $usere      = $_SERVER['HTTP_X_USERE'] ?? '';
    $passwordte = $_SERVER['HTTP_X_PASSWORDTE'] ?? '';

    return hash_equals(USERHYBRIDWEB, $usere) && hash_equals(PASHYBRIDWEB, $passwordte);
}

function kirimGalat(int $status, string $pesan): never
{
    http_response_code($status);
    header('Content-Type: application/json');
    header('Cache-Control: no-store');
    echo json_encode(['pesan' => $pesan]);

    exit;
}

function daftarBerkas(string $direktori): array
{
    $daftar   = [];
    $batas    = time() - JEDA_STABIL_DETIK;
    $iterator = new RecursiveIteratorIterator(
        new RecursiveDirectoryIterator($direktori, FilesystemIterator::SKIP_DOTS | FilesystemIterator::CURRENT_AS_FILEINFO),
    );

    foreach ($iterator as $info) {
        if (!$info->isFile()) {
            continue;
        }

        $jalur = str_replace('\\', '/', substr($info->getPathname(), strlen($direktori) + 1));

        if (str_starts_with($info->getFilename(), '.') || str_ends_with($info->getFilename(), '.tmp')) {
            continue;
        }

        if ($info->getMTime() > $batas) {
            continue;
        }

        $daftar[$jalur] = [
            'jalur'   => $jalur,
            'ukuran'  => $info->getSize(),
            'diubah'  => $info->getMTime(),
            'lengkap' => $info->getPathname(),
        ];
    }

    ksort($daftar, SORT_STRING);

    return $daftar;
}

function kirimManifest(string $kanal, string $direktori): never
{
    $daftar = daftarBerkas($direktori);
    $sidik  = '';

    foreach ($daftar as $berkas) {
        $sidik .= $berkas['jalur'] . "\0" . $berkas['ukuran'] . "\0" . $berkas['diubah'] . "\n";
    }

    $etag = '"' . sha1($sidik) . '"';

    header('Cache-Control: no-cache');
    header('ETag: ' . $etag);

    if (trim($_SERVER['HTTP_IF_NONE_MATCH'] ?? '') === $etag) {
        http_response_code(304);

        exit;
    }

    $lokasiCache = sys_get_temp_dir() . DIRECTORY_SEPARATOR . 'pembaruansmc-' . $kanal . '-' . md5($direktori) . '.json';
    $cacheLama   = is_file($lokasiCache) ? json_decode((string) file_get_contents($lokasiCache), true) : null;
    $cacheLama   = is_array($cacheLama) ? $cacheLama : [];
    $cacheBaru   = [];
    $manifest    = [];

    foreach ($daftar as $jalur => $berkas) {
        $lama = $cacheLama[$jalur] ?? null;

        if (is_array($lama) && $lama['ukuran'] === $berkas['ukuran'] && $lama['diubah'] === $berkas['diubah']) {
            $sha256 = $lama['sha256'];
        } else {
            $sha256 = hash_file('sha256', $berkas['lengkap']);
        }

        if ($sha256 === false) {
            continue;
        }

        $cacheBaru[$jalur] = [
            'ukuran' => $berkas['ukuran'],
            'diubah' => $berkas['diubah'],
            'sha256' => $sha256,
        ];
        $manifest[] = [
            'jalur'  => $jalur,
            'ukuran' => $berkas['ukuran'],
            'sha256' => $sha256,
        ];
    }

    @file_put_contents($lokasiCache, json_encode($cacheBaru), LOCK_EX);

    header('Content-Type: application/json');
    echo json_encode(['berkas' => $manifest], JSON_UNESCAPED_SLASHES);

    exit;
}

function kirimBerkas(string $direktori, string $jalur): never
{
    if (
        $jalur === ''
        || str_contains($jalur, '\\')
        || str_contains($jalur, ':')
        || str_contains($jalur, "\0")
        || str_starts_with($jalur, '/')
        || in_array('..', explode('/', $jalur), true)
    ) {
        kirimGalat(400, 'Jalur berkas tidak valid');
    }

    $lengkap = realpath($direktori . DIRECTORY_SEPARATOR . str_replace('/', DIRECTORY_SEPARATOR, $jalur));

    if ($lengkap === false || !is_file($lengkap) || !str_starts_with($lengkap, $direktori . DIRECTORY_SEPARATOR)) {
        kirimGalat(404, 'Berkas tidak ditemukan');
    }

    set_time_limit(0);

    while (ob_get_level() > 0) {
        ob_end_clean();
    }

    header('Content-Type: application/octet-stream');
    header('Content-Length: ' . filesize($lengkap));
    header('Cache-Control: no-store');
    readfile($lengkap);

    exit;
}
