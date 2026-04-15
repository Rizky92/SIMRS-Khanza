<?php

/**
 * MariaDB Schema Diff Tool
 *
 * Compares two database schemas and generates DDL migration statements.
 *
 * Usage:
 *   php schema-diff.php <source_db> <target_db> [options]
 *
 * Options:
 *   --host=<host>       Database host (default: localhost)
 *   --port=<port>       Database port (default: 3306)
 *   --user=<user>       Database user (default: root)
 *   --pass=<pass>       Database password (default: empty)
 *   --output=<file>     Write output to file instead of stdout
 *   --no-drop           Skip all DROP statements (tables and columns)
 *   --no-drop-table     Skip DROP TABLE statements only
 *   --no-drop-column    Skip DROP COLUMN/INDEX/FK statements on existing tables only
 *   --tables=<list>     Compare only these tables (comma-separated)
 *   --exclude=<list>    Exclude these tables (comma-separated)
 *   --migrate           Execute DDL directly against the current database
 *   --yes               Skip confirmation prompt when using --migrate
 *
 * First positional arg  = latest/updated database
 * Second positional arg = current database
 * Generates DDL to migrate current → latest.
 */

if (php_sapi_name() !== 'cli') {
    die('CLI only.');
}

$opts = parseArgs($argv);

if (empty($opts['latest']) || empty($opts['current'])) {
    fwrite(STDERR, "Usage: php schema-diff.php <latest_db> <current_db> [--host=localhost] [--port=3306] [--user=root] [--pass=] [--output=file] [--no-drop] [--no-drop-table] [--no-drop-column] [--tables=t1,t2] [--exclude=t1,t2]\n");
    exit(1);
}

$host = $opts['host'] ?? 'localhost';
$port = $opts['port'] ?? '3306';
$user = $opts['user'] ?? 'root';
$pass = $opts['pass'] ?? '';
$noDropAll = isset($opts['no-drop']);
$noDropTable = $noDropAll || isset($opts['no-drop-table']);
$noDropColumn = $noDropAll || isset($opts['no-drop-column']);
$doMigrate = isset($opts['migrate']);
$skipConfirm = isset($opts['yes']);
$onlyTables = !empty($opts['tables']) ? array_map('trim', explode(',', $opts['tables'])) : [];
$excludeTables = !empty($opts['exclude']) ? array_map('trim', explode(',', $opts['exclude'])) : [];

try {
    $pdo = new PDO("mysql:host={$host};port={$port}", $user, $pass, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    ]);
} catch (PDOException $e) {
    fwrite(STDERR, "Connection failed: " . $e->getMessage() . "\n");
    exit(1);
}

$latest = $opts['latest'];
$current = $opts['current'];

// Verify both databases exist
foreach ([$latest, $current] as $db) {
    $stmt = $pdo->prepare("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?");
    $stmt->execute([$db]);
    if (!$stmt->fetch()) {
        fwrite(STDERR, "Database '{$db}' not found.\n");
        exit(1);
    }
}

fwrite(STDERR, "Comparing: {$latest} → {$current}\n");

// Bulk-load all schema metadata (6 queries total instead of 3 per table)
fwrite(STDERR, "Loading schema metadata...\n");

$srcTables = getTables($pdo, $current);
$tgtTables = getTables($pdo, $latest);
$srcAllCols = getAllColumns($pdo, $current);
$tgtAllCols = getAllColumns($pdo, $latest);
$srcAllIdx = getAllIndexes($pdo, $current);
$tgtAllIdx = getAllIndexes($pdo, $latest);
$srcAllFks = getAllForeignKeys($pdo, $current);
$tgtAllFks = getAllForeignKeys($pdo, $latest);

fwrite(STDERR, "Metadata loaded.\n");

// Apply table filters
if ($onlyTables) {
    $srcTables = array_intersect_key($srcTables, array_flip($onlyTables));
    $tgtTables = array_intersect_key($tgtTables, array_flip($onlyTables));
}
if ($excludeTables) {
    $srcTables = array_diff_key($srcTables, array_flip($excludeTables));
    $tgtTables = array_diff_key($tgtTables, array_flip($excludeTables));
}

$newTables = array_diff(array_keys($tgtTables), array_keys($srcTables));
$droppedTables = array_diff(array_keys($srcTables), array_keys($tgtTables));
$commonTables = array_intersect(array_keys($srcTables), array_keys($tgtTables));

$latestCount = count($tgtTables);
$currentCount = count($srcTables);
fwrite(STDERR, "Latest tables: {$latestCount}, Current tables: {$currentCount}\n");
fwrite(STDERR, "New: " . count($newTables) . ", Dropped: " . count($droppedTables) . ", Common: " . count($commonTables) . "\n");

// Build forward DDL (current → latest)
$ddl = buildDdl($pdo, $current, $latest, $srcTables, $tgtTables, $srcAllCols, $tgtAllCols, $srcAllIdx, $tgtAllIdx, $srcAllFks, $tgtAllFks, $noDropTable, $noDropColumn);

// Extract executable statements (skip comments and blank lines)
$statements = [];
foreach ($ddl as $line) {
    if ($line !== '' && !str_starts_with($line, '--')) {
        $statements[] = $line;
    }
}

$output = implode("\n", $ddl);

if (count($statements) <= 2) {
    // Only FK_CHECKS toggle, no actual changes
    fwrite(STDERR, "No differences found.\n");
    if (!empty($opts['output'])) {
        file_put_contents($opts['output'], '');
        fwrite(STDERR, "Cleared {$opts['output']}\n");
    }
    exit(0);
}

fwrite(STDERR, "Generated " . (count($statements) - 2) . " DDL statement(s).\n");

if (!$doMigrate) {
    // Output mode: print or write to file
    if (!empty($opts['output'])) {
        file_put_contents($opts['output'], $output);
        fwrite(STDERR, "Written to {$opts['output']}\n");
    } else {
        echo $output;
    }
    exit(0);
}

// === Migrate mode ===

// Generate full rollback script (reverse direction: latest → current) and save for reference
fwrite(STDERR, "Generating rollback script...\n");
$rollbackDdl = buildDdl($pdo, $latest, $current, $tgtTables, $srcTables, $tgtAllCols, $srcAllCols, $tgtAllIdx, $srcAllIdx, $tgtAllFks, $srcAllFks, $noDropTable, $noDropColumn);
$rollbackFile = 'rollback_' . date('Ymd_His') . '.sql';
file_put_contents($rollbackFile, implode("\n", $rollbackDdl));
fwrite(STDERR, "Rollback script saved to: {$rollbackFile}\n");

// Build a forward→reverse mapping so we can revert only what was applied
$rollbackStatements = [];
foreach ($rollbackDdl as $line) {
    if ($line !== '' && !str_starts_with($line, '--')) {
        $rollbackStatements[] = $line;
    }
}
$reverseMap = buildReverseMap($statements, $rollbackStatements);

// Warning
fwrite(STDERR, "\n");
fwrite(STDERR, "╔══════════════════════════════════════════════════════════════╗\n");
fwrite(STDERR, "║  WARNING: THIS WILL DIRECTLY MODIFY DATABASE `{$current}`\n");
fwrite(STDERR, "║\n");
fwrite(STDERR, "║  " . (count($statements) - 2) . " DDL statement(s) will be executed.\n");
fwrite(STDERR, "║  This action is DESTRUCTIVE and may cause DATA LOSS.\n");
fwrite(STDERR, "║\n");
fwrite(STDERR, "║  A rollback script has been saved to: {$rollbackFile}\n");
fwrite(STDERR, "║  On failure, applied changes will be automatically reverted.\n");
fwrite(STDERR, "╚══════════════════════════════════════════════════════════════╝\n");
fwrite(STDERR, "\n");

if (!$skipConfirm) {
    fwrite(STDERR, "Type 'yes' to proceed: ");
    $answer = trim(fgets(STDIN));
    if ($answer !== 'yes') {
        fwrite(STDERR, "Aborted.\n");
        exit(1);
    }
}

// Execute statements one by one, tracking reverse for each applied
$pdo->exec("USE `{$current}`");
$pdo->exec("SET FOREIGN_KEY_CHECKS = 0");
$appliedReverse = [];
$failed = false;
$appliedCount = 0;

foreach ($statements as $i => $stmt) {
    try {
        $pdo->exec($stmt);
        $appliedCount++;
        // Track the reverse statement(s) for this forward statement
        if (isset($reverseMap[$i])) {
            foreach ($reverseMap[$i] as $rev) {
                array_unshift($appliedReverse, $rev);
            }
        }
        fwrite(STDERR, "  [OK] " . mb_strimwidth($stmt, 0, 100, '...') . "\n");
    } catch (PDOException $e) {
        $failed = true;
        fwrite(STDERR, "  [FAIL] " . mb_strimwidth($stmt, 0, 100, '...') . "\n");
        fwrite(STDERR, "  Error: " . $e->getMessage() . "\n");
        break;
    }
}

if ($failed) {
    fwrite(STDERR, "\nMigration failed after {$appliedCount} statement(s). Reverting applied changes...\n");

    // Always ensure FK checks are off during revert
    try { $pdo->exec("SET FOREIGN_KEY_CHECKS = 0;"); } catch (PDOException $e) {}

    foreach ($appliedReverse as $stmt) {
        try {
            $pdo->exec($stmt);
            fwrite(STDERR, "  [REVERTED] " . mb_strimwidth($stmt, 0, 100, '...') . "\n");
        } catch (PDOException $e) {
            fwrite(STDERR, "  [REVERT FAIL] " . mb_strimwidth($stmt, 0, 100, '...') . "\n");
            fwrite(STDERR, "  Error: " . $e->getMessage() . "\n");
        }
    }

    try { $pdo->exec("SET FOREIGN_KEY_CHECKS = 1;"); } catch (PDOException $e) {}

    fwrite(STDERR, "Revert completed. Full rollback script at: {$rollbackFile}\n");
    exit(1);
} else {
    fwrite(STDERR, "\nMigration completed. " . ($appliedCount - 2) . " DDL statement(s) applied.\n");
    fwrite(STDERR, "Rollback script kept at: {$rollbackFile}\n");
}

// ========== Functions ==========

function parseArgs(array $argv): array
{
    $result = [];
    $positional = [];
    for ($i = 1; $i < count($argv); $i++) {
        $arg = $argv[$i];
        if (str_starts_with($arg, '--')) {
            $arg = substr($arg, 2);
            if (str_contains($arg, '=')) {
                [$key, $val] = explode('=', $arg, 2);
                $result[$key] = $val;
            } else {
                $result[$arg] = true;
            }
        } else {
            $positional[] = $arg;
        }
    }
    $result['latest'] = $positional[0] ?? null;
    $result['current'] = $positional[1] ?? null;
    return $result;
}

function getTables(PDO $pdo, string $db): array
{
    $stmt = $pdo->prepare("
        SELECT TABLE_NAME, ENGINE, TABLE_COLLATION
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'
        ORDER BY TABLE_NAME
    ");
    $stmt->execute([$db]);
    $tables = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $tables[$row['TABLE_NAME']] = $row;
    }
    return $tables;
}

function getAllColumns(PDO $pdo, string $db): array
{
    $stmt = $pdo->prepare("
        SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, EXTRA, COLUMN_KEY, ORDINAL_POSITION
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = ?
        ORDER BY TABLE_NAME, ORDINAL_POSITION
    ");
    $stmt->execute([$db]);
    $cols = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $table = $row['TABLE_NAME'];
        if (!isset($cols[$table])) {
            $cols[$table] = [];
        }
        $cols[$table][$row['COLUMN_NAME']] = $row;
    }
    return $cols;
}

function getAllIndexes(PDO $pdo, string $db): array
{
    $stmt = $pdo->prepare("
        SELECT TABLE_NAME, INDEX_NAME, NON_UNIQUE, COLUMN_NAME, SEQ_IN_INDEX
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = ?
        ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX
    ");
    $stmt->execute([$db]);
    $indexes = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $table = $row['TABLE_NAME'];
        $name = $row['INDEX_NAME'];
        if (!isset($indexes[$table])) {
            $indexes[$table] = [];
        }
        if (!isset($indexes[$table][$name])) {
            $indexes[$table][$name] = ['non_unique' => $row['NON_UNIQUE'], 'columns' => []];
        }
        $indexes[$table][$name]['columns'][] = $row['COLUMN_NAME'];
    }
    return $indexes;
}

function getAllForeignKeys(PDO $pdo, string $db): array
{
    $stmt = $pdo->prepare("
        SELECT
            kcu.TABLE_NAME,
            kcu.CONSTRAINT_NAME,
            kcu.COLUMN_NAME,
            kcu.ORDINAL_POSITION,
            kcu.REFERENCED_TABLE_NAME,
            kcu.REFERENCED_COLUMN_NAME,
            rc.UPDATE_RULE,
            rc.DELETE_RULE
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
        JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc
            ON rc.CONSTRAINT_SCHEMA = kcu.TABLE_SCHEMA
            AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
        WHERE kcu.TABLE_SCHEMA = ?
            AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
        ORDER BY kcu.TABLE_NAME, kcu.CONSTRAINT_NAME, kcu.ORDINAL_POSITION
    ");
    $stmt->execute([$db]);
    $fks = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $table = $row['TABLE_NAME'];
        $name = $row['CONSTRAINT_NAME'];
        if (!isset($fks[$table])) {
            $fks[$table] = [];
        }
        if (!isset($fks[$table][$name])) {
            $fks[$table][$name] = [
                'columns' => [],
                'ref_table' => $row['REFERENCED_TABLE_NAME'],
                'ref_columns' => [],
                'update_rule' => normalizeFkRule($row['UPDATE_RULE']),
                'delete_rule' => normalizeFkRule($row['DELETE_RULE']),
            ];
        }
        $fks[$table][$name]['columns'][] = $row['COLUMN_NAME'];
        $fks[$table][$name]['ref_columns'][] = $row['REFERENCED_COLUMN_NAME'];
    }
    return $fks;
}

/**
 * Returns [createDdl, fkStatements[]] where createDdl has FK constraints
 * stripped out, and fkStatements are separate ALTER TABLE ADD CONSTRAINT lines.
 */
function getCreateTable(PDO $pdo, string $db, string $table): array
{
    $stmt = $pdo->query("SHOW CREATE TABLE `{$db}`.`{$table}`");
    $row = $stmt->fetch(PDO::FETCH_NUM);
    $ddl = $row[1];

    // Strip database qualifier from REFERENCES `db`.`table` → REFERENCES `table`
    $ddl = preg_replace('/REFERENCES\s+`' . preg_quote($db, '/') . '`\.`/', 'REFERENCES `', $ddl);

    // Extract and remove CONSTRAINT ... FOREIGN KEY lines from CREATE TABLE
    $fkStatements = [];
    $lines = explode("\n", $ddl);
    $cleaned = [];
    foreach ($lines as $line) {
        if (preg_match('/^\s*CONSTRAINT\s+`([^`]+)`\s+FOREIGN\s+KEY/i', $line)) {
            // Remove trailing comma from the previous cleaned line if needed
            $fkLine = rtrim($line, ", \r\n");
            $fkStatements[] = "ALTER TABLE `{$table}` ADD {$fkLine};";
        } else {
            $cleaned[] = $line;
        }
    }

    // Fix trailing comma on the last column/index line before closing paren
    $createDdl = implode("\n", $cleaned);
    $createDdl = preg_replace('/,(\s*\n\s*\)\s*(ENGINE|DEFAULT|ROW_FORMAT|COLLATE|AUTO_INCREMENT|CHARSET))/i', '$1', $createDdl);

    return [$createDdl, $fkStatements];
}

function compareTableOptions(array $src, array $tgt): ?string
{
    $parts = [];
    if ($src['ENGINE'] !== $tgt['ENGINE']) {
        $parts[] = "ENGINE={$tgt['ENGINE']}";
    }
    if ($src['TABLE_COLLATION'] !== $tgt['TABLE_COLLATION']) {
        $parts[] = "COLLATE={$tgt['TABLE_COLLATION']}";
    }
    return $parts ? implode(' ', $parts) : null;
}

function columnDef(array $col): string
{
    $def = "`{$col['COLUMN_NAME']}` {$col['COLUMN_TYPE']}";
    $def .= $col['IS_NULLABLE'] === 'NO' ? ' NOT NULL' : ' NULL';
    if ($col['COLUMN_DEFAULT'] !== null) {
        $default = $col['COLUMN_DEFAULT'];
        if (is_numeric($default) || in_array(strtoupper($default), ['CURRENT_TIMESTAMP', 'NULL'])) {
            $def .= " DEFAULT {$default}";
        } else {
            $def .= " DEFAULT '{$default}'";
        }
    } elseif ($col['IS_NULLABLE'] === 'YES') {
        $def .= " DEFAULT NULL";
    }
    if ($col['EXTRA']) {
        $def .= " {$col['EXTRA']}";
    }
    return $def;
}

function columnChanged(array $src, array $tgt): bool
{
    return $src['COLUMN_TYPE'] !== $tgt['COLUMN_TYPE']
        || $src['IS_NULLABLE'] !== $tgt['IS_NULLABLE']
        || $src['COLUMN_DEFAULT'] !== $tgt['COLUMN_DEFAULT']
        || $src['EXTRA'] !== $tgt['EXTRA'];
}

function getAfterClause(string $col, array $order): string
{
    $pos = array_search($col, $order);
    if ($pos === 0) {
        return 'FIRST';
    }
    return "AFTER `{$order[$pos - 1]}`";
}

function indexDef(string $name, array $idx): string
{
    $cols = implode('`, `', $idx['columns']);
    if ($name === 'PRIMARY') {
        return "PRIMARY KEY (`{$cols}`)";
    }
    $type = $idx['non_unique'] ? 'INDEX' : 'UNIQUE INDEX';
    return "{$type} `{$name}` (`{$cols}`)";
}

function normalizeFkRule(string $rule): string
{
    // In MariaDB, RESTRICT and NO ACTION are functionally identical
    return $rule === 'NO ACTION' ? 'RESTRICT' : $rule;
}

function normalizeFk(array $fk): array
{
    $fk['update_rule'] = normalizeFkRule($fk['update_rule']);
    $fk['delete_rule'] = normalizeFkRule($fk['delete_rule']);
    return $fk;
}

/**
 * Maps each forward statement index to its corresponding reverse statement(s).
 *
 * Matching logic:
 * - SET FOREIGN_KEY_CHECKS: maps to opposite toggle
 * - CREATE TABLE `x`: reverse is DROP TABLE IF EXISTS `x`
 * - DROP TABLE IF EXISTS `x`: reverse is CREATE TABLE from rollback
 * - ALTER TABLE `x` ...: matched to ALTER TABLE `x` ... in rollback
 */
function buildReverseMap(array $forward, array $reverse): array
{
    $map = [];

    // Group reverse statements by table name for lookup
    $reverseByTable = [];
    foreach ($reverse as $ri => $rstmt) {
        $table = extractTableName($rstmt);
        if ($table !== null) {
            $reverseByTable[$table][] = $rstmt;
        }
    }

    foreach ($forward as $fi => $fstmt) {
        $upper = strtoupper(trim($fstmt));

        // FK check toggles: reverse is the opposite
        if (str_starts_with($upper, 'SET FOREIGN_KEY_CHECKS')) {
            // Don't map these — handled explicitly during revert
            continue;
        }

        $table = extractTableName($fstmt);
        if ($table === null) {
            continue;
        }

        // CREATE TABLE → DROP TABLE
        if (preg_match('/^CREATE\s+TABLE/i', $fstmt)) {
            $map[$fi] = ["DROP TABLE IF EXISTS `{$table}`;"];
            continue;
        }

        // DROP TABLE → find CREATE TABLE in reverse
        if (preg_match('/^DROP\s+TABLE/i', $fstmt)) {
            foreach ($reverse as $rstmt) {
                if (preg_match('/^CREATE\s+TABLE/i', $rstmt) && extractTableName($rstmt) === $table) {
                    $map[$fi] = [$rstmt];
                    break;
                }
            }
            continue;
        }

        // ALTER TABLE: find matching reverse ALTER for same table
        if (preg_match('/^ALTER\s+TABLE/i', $fstmt) && isset($reverseByTable[$table])) {
            $matched = matchAlterReverse($fstmt, $reverseByTable[$table]);
            if ($matched) {
                $map[$fi] = $matched;
            }
        }
    }

    return $map;
}

function extractTableName(string $stmt): ?string
{
    // Match CREATE TABLE `name`, DROP TABLE ... `name`, ALTER TABLE `name`
    if (preg_match('/(?:CREATE|DROP|ALTER)\s+TABLE\s+(?:IF\s+(?:NOT\s+)?EXISTS\s+)?`([^`]+)`/i', $stmt, $m)) {
        return $m[1];
    }
    return null;
}

function matchAlterReverse(string $forward, array $reverseStmts): ?array
{
    // ADD COLUMN `x` → DROP COLUMN `x`
    if (preg_match('/ADD\s+COLUMN\s+`([^`]+)`/i', $forward, $m)) {
        $col = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/DROP\s+COLUMN\s+`' . preg_quote($col, '/') . '`/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // DROP COLUMN `x` → ADD COLUMN `x`
    if (preg_match('/DROP\s+COLUMN\s+`([^`]+)`/i', $forward, $m)) {
        $col = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/ADD\s+COLUMN\s+`' . preg_quote($col, '/') . '`/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // MODIFY COLUMN `x` → MODIFY COLUMN `x` (reverse has the old definition)
    if (preg_match('/MODIFY\s+COLUMN\s+`([^`]+)`/i', $forward, $m)) {
        $col = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/MODIFY\s+COLUMN\s+`' . preg_quote($col, '/') . '`/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // ADD PRIMARY KEY → DROP PRIMARY KEY
    if (preg_match('/ADD\s+PRIMARY\s+KEY/i', $forward)) {
        foreach ($reverseStmts as $r) {
            if (preg_match('/DROP\s+PRIMARY\s+KEY/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // DROP PRIMARY KEY → ADD PRIMARY KEY
    if (preg_match('/DROP\s+PRIMARY\s+KEY/i', $forward)) {
        foreach ($reverseStmts as $r) {
            if (preg_match('/ADD\s+PRIMARY\s+KEY/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // ADD [UNIQUE] INDEX `x` → DROP INDEX `x`
    if (preg_match('/ADD\s+(?:UNIQUE\s+)?INDEX\s+`([^`]+)`/i', $forward, $m)) {
        $idx = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/DROP\s+INDEX\s+`' . preg_quote($idx, '/') . '`/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // DROP INDEX `x` → ADD INDEX `x`
    if (preg_match('/DROP\s+INDEX\s+`([^`]+)`/i', $forward, $m)) {
        $idx = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/ADD\s+(?:UNIQUE\s+)?INDEX\s+`' . preg_quote($idx, '/') . '`/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // ADD CONSTRAINT `x` FOREIGN KEY → DROP FOREIGN KEY `x`
    if (preg_match('/ADD\s+CONSTRAINT\s+`([^`]+)`\s+FOREIGN\s+KEY/i', $forward, $m)) {
        $fk = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/DROP\s+FOREIGN\s+KEY\s+`' . preg_quote($fk, '/') . '`/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // DROP FOREIGN KEY `x` → ADD CONSTRAINT `x` FOREIGN KEY
    if (preg_match('/DROP\s+FOREIGN\s+KEY\s+`([^`]+)`/i', $forward, $m)) {
        $fk = $m[1];
        foreach ($reverseStmts as $r) {
            if (preg_match('/ADD\s+CONSTRAINT\s+`' . preg_quote($fk, '/') . '`\s+FOREIGN\s+KEY/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    // ENGINE/COLLATE changes → reverse ENGINE/COLLATE
    if (preg_match('/^\s*ALTER\s+TABLE\s+`[^`]+`\s+(ENGINE|COLLATE)/i', $forward)) {
        foreach ($reverseStmts as $r) {
            if (preg_match('/^\s*ALTER\s+TABLE\s+`[^`]+`\s+(ENGINE|COLLATE)/i', $r)) {
                return [$r];
            }
        }
        return null;
    }

    return null;
}

function buildDdl(
    PDO $pdo,
    string $srcDb,
    string $tgtDb,
    array $srcTables,
    array $tgtTables,
    array $srcAllCols,
    array $tgtAllCols,
    array $srcAllIdx,
    array $tgtAllIdx,
    array $srcAllFks,
    array $tgtAllFks,
    bool $noDropTable,
    bool $noDropColumn
): array {
    $ddl = [];
    $deferredFks = [];
    $ddl[] = "SET FOREIGN_KEY_CHECKS = 0;";
    $ddl[] = "";

    $newTables = array_diff(array_keys($tgtTables), array_keys($srcTables));
    $droppedTables = array_diff(array_keys($srcTables), array_keys($tgtTables));
    $commonTables = array_intersect(array_keys($srcTables), array_keys($tgtTables));

    // New tables: strip FKs from CREATE TABLE, defer them to the end
    foreach ($newTables as $table) {
        [$createDdl, $fkStmts] = getCreateTable($pdo, $tgtDb, $table);
        $ddl[] = "-- New table: {$table}";
        $ddl[] = $createDdl . ";";
        $ddl[] = "";
        if ($fkStmts) {
            $deferredFks = array_merge($deferredFks, $fkStmts);
        }
    }

    if (!$noDropTable) {
        foreach ($droppedTables as $table) {
            $ddl[] = "-- Dropped table: {$table}";
            $ddl[] = "DROP TABLE IF EXISTS `{$table}`;";
            $ddl[] = "";
        }
    }

    foreach ($commonTables as $table) {
        $tableDdl = [];

        $optionsDiff = compareTableOptions($srcTables[$table], $tgtTables[$table]);
        if ($optionsDiff) {
            $tableDdl[] = "ALTER TABLE `{$table}` {$optionsDiff};";
        }

        $srcCols = $srcAllCols[$table] ?? [];
        $tgtCols = $tgtAllCols[$table] ?? [];
        $newCols = array_diff(array_keys($tgtCols), array_keys($srcCols));
        $dropCols = array_diff(array_keys($srcCols), array_keys($tgtCols));
        $commonCols = array_intersect(array_keys($srcCols), array_keys($tgtCols));
        $tgtColOrder = array_keys($tgtCols);

        foreach ($newCols as $col) {
            $after = getAfterClause($col, $tgtColOrder);
            $tableDdl[] = "ALTER TABLE `{$table}` ADD COLUMN " . columnDef($tgtCols[$col]) . " {$after};";
        }
        if (!$noDropColumn) {
            foreach ($dropCols as $col) {
                $tableDdl[] = "ALTER TABLE `{$table}` DROP COLUMN `{$col}`;";
            }
        }
        foreach ($commonCols as $col) {
            if (columnChanged($srcCols[$col], $tgtCols[$col])) {
                $tableDdl[] = "ALTER TABLE `{$table}` MODIFY COLUMN " . columnDef($tgtCols[$col]) . ";";
            }
        }

        $srcIdx = $srcAllIdx[$table] ?? [];
        $tgtIdx = $tgtAllIdx[$table] ?? [];
        $newIdx = array_diff(array_keys($tgtIdx), array_keys($srcIdx));
        $dropIdx = array_diff(array_keys($srcIdx), array_keys($tgtIdx));
        $commonIdx = array_intersect(array_keys($srcIdx), array_keys($tgtIdx));

        foreach ($commonIdx as $idx) {
            if ($srcIdx[$idx] !== $tgtIdx[$idx]) {
                if ($idx === 'PRIMARY') {
                    $tableDdl[] = "ALTER TABLE `{$table}` DROP PRIMARY KEY;";
                } else {
                    $tableDdl[] = "ALTER TABLE `{$table}` DROP INDEX `{$idx}`;";
                }
                $tableDdl[] = "ALTER TABLE `{$table}` ADD " . indexDef($idx, $tgtIdx[$idx]) . ";";
            }
        }
        if (!$noDropColumn) {
            foreach ($dropIdx as $idx) {
                if ($idx === 'PRIMARY') {
                    $tableDdl[] = "ALTER TABLE `{$table}` DROP PRIMARY KEY;";
                } else {
                    $tableDdl[] = "ALTER TABLE `{$table}` DROP INDEX `{$idx}`;";
                }
            }
        }
        foreach ($newIdx as $idx) {
            $tableDdl[] = "ALTER TABLE `{$table}` ADD " . indexDef($idx, $tgtIdx[$idx]) . ";";
        }

        // FK drops and changes go inline (table already exists)
        $srcFks = $srcAllFks[$table] ?? [];
        $tgtFks = $tgtAllFks[$table] ?? [];
        $newFks = array_diff(array_keys($tgtFks), array_keys($srcFks));
        $dropFks = array_diff(array_keys($srcFks), array_keys($tgtFks));
        $commonFks = array_intersect(array_keys($srcFks), array_keys($tgtFks));

        foreach ($commonFks as $fk) {
            if (normalizeFk($srcFks[$fk]) !== normalizeFk($tgtFks[$fk])) {
                $tableDdl[] = "ALTER TABLE `{$table}` DROP FOREIGN KEY `{$fk}`;";
                // Defer the ADD to the end so referenced columns/tables exist
                $deferredFks[] = "ALTER TABLE `{$table}` ADD " . foreignKeyDef($fk, $tgtFks[$fk]) . ";";
            }
        }
        if (!$noDropColumn) {
            foreach ($dropFks as $fk) {
                $tableDdl[] = "ALTER TABLE `{$table}` DROP FOREIGN KEY `{$fk}`;";
            }
        }
        // Defer new FK additions to the end
        foreach ($newFks as $fk) {
            $deferredFks[] = "ALTER TABLE `{$table}` ADD " . foreignKeyDef($fk, $tgtFks[$fk]) . ";";
        }

        if ($tableDdl) {
            $ddl[] = "-- Altered table: {$table}";
            $ddl = array_merge($ddl, $tableDdl);
            $ddl[] = "";
        }
    }

    // Add all deferred FK constraints at the end
    if ($deferredFks) {
        $ddl[] = "-- Deferred foreign key constraints";
        $ddl = array_merge($ddl, $deferredFks);
        $ddl[] = "";
    }

    $ddl[] = "SET FOREIGN_KEY_CHECKS = 1;";
    return $ddl;
}

function foreignKeyDef(string $name, array $fk): string
{
    $fk = normalizeFk($fk);
    $cols = implode('`, `', $fk['columns']);
    $refCols = implode('`, `', $fk['ref_columns']);
    $def = "CONSTRAINT `{$name}` FOREIGN KEY (`{$cols}`) REFERENCES `{$fk['ref_table']}` (`{$refCols}`)";
    if ($fk['update_rule'] !== 'RESTRICT') {
        $def .= " ON UPDATE {$fk['update_rule']}";
    }
    if ($fk['delete_rule'] !== 'RESTRICT') {
        $def .= " ON DELETE {$fk['delete_rule']}";
    }
    return $def;
}
