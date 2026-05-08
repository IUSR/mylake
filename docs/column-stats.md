# Per-column statistics panel

The column statistics panel shows aggregate statistics for every column in the
current Delta table — null count, minimum and maximum values, and distinct
count. The panel appears below the data table and is loaded on demand, so it
does not slow down the initial table view.

## Opening the panel

Click the **Column Stats** button in the toolbar above the data table. The
panel expands below the pager bar and MyLake fetches statistics from the
server. Click **Column Stats** again (or click the **Column Statistics**
header bar inside the panel) to collapse it.

## Statistics computed per column

| Column | Description |
| --- | --- |
| **Column** | Column name |
| **Type** | Delta / Spark data type (e.g. `string`, `long`, `timestamp`) |
| **Null Count** | Number of rows where the value is `NULL` |
| **Min** | Smallest value in the column; `NULL` if the column is all-null |
| **Max** | Largest value in the column; `NULL` if the column is all-null |
| **Distinct Count** | Number of unique non-null values |

Min and max follow natural ordering for each type — lexicographic for strings,
numeric for integer and float types, chronological for dates and timestamps.

## Lazy loading and caching

Stats are fetched from the server the first time you open the panel for a
given table. Subsequent toggles (hiding and showing the panel) reuse the
cached result without making another request. The cache is cleared whenever
you navigate to a different table or reload the current one.

## Performance note

Statistics are computed by running a single aggregate DuckDB query over the
entire Delta table. For very large tables this may take a few seconds. A
"Loading statistics…" placeholder is shown while the query runs.

## Dark mode

The statistics panel respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#120](https://github.com/IUSR/mylake/issues/120) — implementation
  issue for the column statistics panel.
- [Table schema inspector](schema.md) — view column names and types without
  computing aggregate statistics.
- [Inline SQL query editor](sql-editor.md) — run custom DuckDB queries against
  the table.
