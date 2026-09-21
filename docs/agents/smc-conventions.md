# SMC naming conventions

Applies to any agent working in this repository.

## Prefer the Smc-affixed helpers
When code touches `sekuel` / `validasi` class methods (usually referenced as `Sequel` and `Valid`, e.g.
`Sequel.menyimpantf("dpjp_ranap", "?, ?, ?", 3, new String[]{"a", "b", "c"})`), look for the alternative in
those classes affixed with `Smc` and use it instead. **Read the parameter requirements carefully** — the
Smc variants do not always take the same arguments in the same order as the originals.

## Affixing new names
New features must carry the SMC affix, styled per kind:

| Kind | Style | Example |
| --- | --- | --- |
| Method / property name | `Smc` | `simpanDataSmc()`, `dataPasienSmc` |
| Permission / table name | `_smc` | `set_depo_ralan_smc` |
| File / class / static property / `database.xml` parameter | `SMC` | `DlgResepSMC.java`, `HOSTSMC` |

No affix needed for: menus, form titles, and local declarations.
**Exception:** in a class whose name is already affixed with SMC, its methods and (static) properties do not
need affixes.

## Acronyms
Acronyms (SEP, KFA, INACBG, IDRG, API, …) are **ALL UPPERCASE** everywhere they are not part of the SMC affix.

The SMC affix is not an acronym in this rule — it keeps its own casing from the table above. So on an
Smc-affixed method the acronym stays uppercase and the affix stays `Smc`:

- correct: `obatKFASmc`
- incorrect: `obatKFASMC`, `obatKfaSmc`

In database structures (column and table names) and named keys, the whole name is `snake_case`, acronym
included: `no_sep`, `kode_kfa`, `tarif_inacbg`.

## New `database.xml` parameters
Adding a parameter to `database.xml.example` MUST be reflected in `src/fungsi/koneksiDB.java`.

- Parameters are **append-only**: place them **before `WAHOST`** in `database.xml.example`, and
  **before `HOST()`** in `koneksiDB.java`.
- Values are unencrypted by default. **Sensitive** parameters (secret keys and similar) are stored
  encrypted, so the Java side must decrypt before use.
