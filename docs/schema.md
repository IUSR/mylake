# Table schema inspector

The schema inspector panel shows the structure of the Delta table you are
currently viewing — column names, data types, and nullability — without
leaving the data view. This page explains how to open it and what information
it provides.

## Opening the panel

Click the **Schema** button in the toolbar above the data table. The panel
opens as a sidebar to the right of (or overlaying) the grid.

Click the **Schema** button again, or press **Escape**, to close the panel.
The data table remains fully functional while the panel is open.

## What the panel shows

Each row in the schema panel represents one column of the Delta table:

| Field    | Description                                                  |
| -------- | ------------------------------------------------------------ |
| **Name** | The column name as stored in the Delta table metadata.       |
| **Type** | The Spark / Delta data type, e.g. `string`, `long`, `timestamp`, `array<string>`. |
| **Nullable** | Whether the column allows `null` values (`Yes` or `No`). |

Columns are listed in the same order as they appear in the table schema
(i.e., the order in `delta_log` — not the display order, which you can change
by scrolling horizontally).

### Nested and complex types

Delta tables often contain nested types such as `struct<…>`, `map<…>`, and
`array<…>`. The panel renders the full type string so you can see the nested
field names and types at a glance.

## Use cases

- **Understanding an unfamiliar table** — check types before writing a query
  or exporting, so you know whether a column is a `date` or a `string`.
- **Debugging null surprises** — confirm which columns are nullable before
  assuming a missing cell is a bug.
- **Verifying schema after a migration** — a quick visual check that all
  expected columns are present with the right types.

## Schema freshness

The schema is fetched at the same time as the table data and reflects the
**current version** of the table. If you are using the
[version history viewer](version-history.md) to browse an older snapshot, the
schema panel shows the schema for _that version_, not the latest one.

## Related

- Issue [#80](https://github.com/IUSR/mylake/issues/80) — implementation
  request and discussion.
- Issue [#81](https://github.com/IUSR/mylake/issues/81) — the documentation
  ticket that produced this page.
- [Browsing tables](browsing.md) — pagination and rows-per-page selector.
- [Version history](version-history.md) — browse earlier table snapshots.
