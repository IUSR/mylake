# Inline SQL query editor

The inline SQL query editor lets you run ad-hoc DuckDB `SELECT` queries
against the Delta table you are currently viewing, without leaving the MyLake
UI. Results appear in a scrollable table directly below the editor panel.

## Opening the editor

Click the **SQL Editor** button in the toolbar above the data table. The
editor panel expands below the toolbar. Click **SQL Editor** again to hide it.
The editor panel is hidden by default when a table is first loaded.

## Writing a query

Use `tbl` as the alias for the current Delta table. MyLake rewrites `tbl` to
`delta_scan('<path>')` at query execution time, so you do not need to know the
physical table path.

The editor pre-fills with a starter query when you open it for the first time
on a table:

```sql
SELECT * FROM tbl LIMIT 100
```

You can write any valid DuckDB `SELECT` statement. Only read (`SELECT`)
queries are accepted — the endpoint rejects other statement types.

## Running a query

| Method | Action |
| --- | --- |
| **Run** button | Click the **Run** button to execute the query |
| **Ctrl+Enter** | Keyboard shortcut to run from within the text area |

A "Running…" placeholder appears while the query executes. Once complete, the
row count is shown above the result table (e.g. `42 rows returned`).

## Query results

Results are displayed in a compact, scrollable table below the editor. The
result table is independent of the main data grid — paging and sorting
controls in the main table have no effect on query results.

- Up to **1,000 rows** are returned per query.
- Column headers show the column name and its data type.
- Long cell values are truncated with an ellipsis; hover over a cell to see
  the full value in the browser tooltip.
- `NULL` values are shown as a distinct `NULL` label.

To retrieve more than 1,000 rows, add a `LIMIT` and `OFFSET` clause and run
the query multiple times.

## Clearing the editor

Click the **Clear** button to erase the query input and remove the result
table from view.

## Dark mode

The SQL editor panel and its result table respect the MyLake dark mode
setting. See [Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#121](https://github.com/IUSR/mylake/issues/121) — implementation
  issue for the inline SQL query editor.
- [Browsing tables](browsing.md) — pagination and rows-per-page controls for
  the main data grid.
- [Table schema inspector](schema.md) — view column names and types before
  writing a query.
