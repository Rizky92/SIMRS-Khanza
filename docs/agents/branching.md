# Branching workflow

Applies to any agent working in this repository.

## 1. Check the current branch first
Before doing anything — before iterating a plan and before the first edit:

```bash
git rev-parse --abbrev-ref HEAD
```

**If HEAD is not on a `custom-` branch, the user is asking for changes on that branch.** Stay on it: do not
create or switch to a new branch, and skip section 3 entirely.

## 2. Start from the latest date-versioned branch
When working from a `custom-` branch, make sure it is the latest one. Date-versioned branches are named
`custom-` followed by the date in `yyyy-MM-dd` (e.g. `custom-2026-09-04`).

```bash
git branch --list 'custom-*' | sed 's/^[* ] //' | sort | tail -1
```

If HEAD is on an older `custom-` branch than that, say so before continuing.

## 3. After you start editing, switch to a new branch
Only when the work started from a `custom-` branch.

- Name: the first letter of your vendor name, then `/`, then the title in `kebab-case`, **at most 4 words**.
  - Claude Code uses `c/` — see `CLAUDE.md`.
  - Good: `c/fix-resep-queue`, `c/beban-hutang-lain`
  - Bad: `c/claude-fix-the-resep-printing-queue` (too many words), `fix-resep` (missing prefix)
- **DO NOT force-create** a branch that already exists. Check first, and pick a different title if taken:

```bash
git rev-parse --verify --quiet "refs/heads/<prefix>/<title>" || git switch -c "<prefix>/<title>"
```

The switch happens *after* editing has begun — do not branch pre-emptively before knowing what the change is.

## 4. Do not commit
After completing the task, do not make a commit (and do not push, tag, or open a PR) unless the user
explicitly asks in that turn. Leave changes in the working tree.
