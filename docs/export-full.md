# Exporting all rows

In addition to exporting the current page, MyLake can download the entire
table (or up to a configured maximum) in a single file. This is done
server-side: the backend reads the Delta table and streams the file directly
to your browser without loading all the data into the page first.

## How to use it

The **Export CSV** and **Export JSON** buttons in the toolbar are split
buttons — each has a small dropdown arrow on the right side. Click the arrow
to reveal two options:

| Option | What it does |
| --- | --- |
| **Current page** | Same as the plain button click — exports the rows currently shown in the grid. |
| **All rows (up to 500,000)** | Triggers a server-side export of the full table, streamed directly as a download. |

The plain button click (left part of the split button) still exports the
current page, so the default behaviour is unchanged.

## File naming

Full-table exports are named after the table:

- CSV: `<table>-full.csv`
- JSON: `<table>-full.json`

## Format

- **CSV** follows RFC 4180: comma-separated, fields with commas/quotes/newlines
  double-quoted, `NULL` values rendered as empty fields. The first row is the
  column header.
- **JSON** is an array of objects, one object per row, with column names as
  keys and raw values as values. `NULL` values are rendered as JSON `null`.

These are the same formats used by the per-page CSV and JSON exports. The
only difference is that all rows are included instead of one page.

## Row limit

The default maximum is **500,000 rows**. If the table has more rows than the
limit, the export silently stops at the limit — you receive the first
500,000 rows in sort order.

A server administrator can change the limit in `application.properties`:

```properties
# Maximum number of rows exported by /api/table/export.
# Default: 500000
mylake.export.max-rows=500000
```

Raising the limit increases memory usage on the server during the export
(all rows are fetched before streaming begins).

## Comparison with page export

| | Page export | Full-table export |
| --- | --- | --- |
| **Scope** | Current page only | All rows (up to limit) |
| **Triggered by** | Plain button click | Dropdown → All rows |
| **Processing** | Browser (in-memory) | Server-side streaming |
| **File name** | `<table>-page<N>.csv` | `<table>-full.csv` |
| **Column visibility** | Respects hidden columns | All columns included |
| **Row limit** | Rows-per-page (max 1,000) | `mylake.export.max-rows` (default 500,000) |

## Column visibility note

The full-table export always includes **all columns** — it does not apply the
column visibility settings configured in the browser. If you need to exclude
columns, use the per-page export after hiding the unwanted columns, or
post-process the full export file.

## Related

- Issue [#151](https://github.com/IUSR/mylake/issues/151) — implementation
  issue for the full-table export.
- Issue [#194](https://github.com/IUSR/mylake/issues/194) — the documentation
  ticket that produced this page.
- [Exporting data (CSV)](exporting.md) — per-page CSV export.
- [Exporting data (JSON)](json-export.md) — per-page JSON export.
- [Column visibility](column-visibility.md) — hiding columns (affects page export only).
- [Inline SQL query editor](sql-editor.md) — alternative for custom data extraction.
