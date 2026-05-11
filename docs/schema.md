# Table schema inspector

The schema inspector panel shows the structure and key metadata of the Delta
table you are currently viewing — column names, data types, nullability, row
count, table size, and the current Delta version — without leaving the data
view.

## Opening the panel

Click the **Schema** button in the toolbar above the data table. The panel
slides in from the right side of the content area. MyLake fetches the schema
from the server (via `GET /api/table/schema`) so the information is always
up to date.

Click the **Schema** button again, or the **×** (close) button in the panel
header, to close the panel. The data table remains fully functional while the
panel is open.

## Table metadata

At the top of the schema panel, MyLake shows table-level statistics:

| Field | Description |
| --- | --- |
| **Row Count** | Total number of rows in the Delta table (full table, not just the current page). |
| **Size** | Approximate total size of the table data files, in MB. |
| **Delta Version** | The current version number of the Delta table (number of committed transactions). |

## Column list

Below the table statistics, each row represents one column of the Delta table:

| Field | Description |
| --- | --- |
| **Name** | The column name as stored in the Delta table metadata. Columns that cannot be `NULL` are labelled **NOT NULL**. |
| **Type** | The Spark / Delta data type, e.g. `string`, `long`, `timestamp`, `array<string>`. |

Columns are listed in the same order as they appear in the table metadata.

### Nested and complex types

Delta tables often contain nested types such as `struct<…>`, `map<…>`, and
`array<…>`. The panel renders the full type string so you can see the nested
field names and types at a glance.

## Fallback behaviour

If the schema endpoint is unreachable or returns an error, the panel falls
back to the column metadata already loaded for the current page. In this
case, the table statistics (row count, size, Delta version) will not be
shown, but column names and types will still be available.

## Use cases

- **Understanding an unfamiliar table** — check types and whether columns are
  nullable before writing a query or exporting.
- **Verifying schema after a migration** — a quick visual check that all
  expected columns are present with the right types.
- **Checking table health** — row count and size give a quick sense of scale
  without writing SQL.

## Dark mode

The schema inspector respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#80](https://github.com/IUSR/mylake/issues/80) — original
  implementation of the schema inspector.
- Issue [#149](https://github.com/IUSR/mylake/issues/149) — metadata
  enhancements (row count, size, Delta version, NOT NULL).
- Issue [#200](https://github.com/IUSR/mylake/issues/200) — the documentation
  update ticket that produced this revision.
- [Per-column statistics panel](column-stats.md) — detailed aggregate statistics
  (null count, min, max, distinct count) per column.
- [Table properties](table-properties.md) — Delta-level metadata (table ID,
  partition columns, protocol versions, custom config).
- [Browsing tables](browsing.md) — pagination and rows-per-page selector.
