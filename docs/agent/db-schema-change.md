# Database structure changes

Applies to any agent working in this repository.

## Where changes go
- **All** database structure changes MUST be in `sik_modif.sql`.
- **DO NOT CHANGE** any other `.sql` file (`sik.sql`, dumps, etc.). Those either track the upstream
  repository or hold referenced data dumps.

## Ordering
Additions are sorted **alphabetically, snake_case, by table name**.

## Table already declared in `sik_modif.sql`
Before writing anything, check whether the table's `CREATE TABLE` already lives in `sik_modif.sql`:

```bash
grep -n 'CREATE TABLE IF NOT EXISTS `<table>`' sik_modif.sql
```

- **It is there** -> edit that `CREATE TABLE` statement **directly** (add/modify the column, its index, etc.).
  Do not append an `ALTER TABLE ... ADD COLUMN` for it.
- **It is not there** (the table comes from `sik.sql` / upstream) -> leave the other `.sql` files alone and
  express the change in `sik_modif.sql` as an `ALTER TABLE`, kept in the same alphabetical position.

## Forward references to a later table
If a table has a foreign key on a table whose name sorts **after** it, still keep alphabetical order:
declare the column and its index inline, and add the constraint in the `ALTER TABLE` block at the end.

```sql
CREATE TABLE IF NOT EXISTS `current_table`  (
    -- ...
    `column_from_related_table` varchar(255) NOT NULL,
    -- ...
    INDEX `column_from_related_table`(`column_from_related_table`) USING BTREE,
    -- ...
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ...

CREATE TABLE IF NOT EXISTS `related_table`  (
    `column_from_related_table` varchar(255) NOT NULL,
    -- ...
    PRIMARY KEY (`column_from_related_table`),
    -- ...
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- foreign key constraints are added and sorted alphabetically by table names
ALTER TABLE `current_table` ADD CONSTRAINT `<constraint name>` FOREIGN KEY IF NOT EXISTS (`column_of_related_table`) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ...
```

Conventions to keep: `CREATE TABLE IF NOT EXISTS`, `ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic`,
and `FOREIGN KEY IF NOT EXISTS` in the trailing constraint block (itself sorted alphabetically by table name).

## Naming
New tables added for this fork are affixed `_smc`. See [`smc-conventions.md`](smc-conventions.md).
