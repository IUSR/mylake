# Table schema inspector

The schema inspector panel shows the structure of the Delta table you are
currently viewing — column names and data types — without leaving the data
view. This page explains how to open it and what information it provides.

## Opening the panel

Click the **Schema** button in the toolbar above the data table. The panel
slides in from the right side of the content area.

Click the **Schema** button again, or the **×** (close) button in the panel
header, to close the panel. The data table remains fully functional while the
panel is open.

## What the panel shows

Each row in the schema panel represents one column of the Delta table:

| Field    | Description                                                  |
| -------- | ------------------------------------------------------------ |
| **Name** | The column name as stored in the Delta table metadata.       |
| **Type** | The Spark / Delta data type, e.g. `string`, `long`, `timestamp`, `array<string>`. |

Columns are listed in the same order as they appear in the table metadata
returned by the backend.

### Nested and complex types

Delta tables often contain nested types such as `struct<…>`, `map<…>`, and
`array<…>`. The panel renders the full type string so you can see the nested
field names and types at a glance.

## Use cases

- **Understanding an unfamiliar table** — check types before writing a query
  or exporting, so you know whether a column is a `date` or a `string`.
- **Verifying schema after a migration** — a quick visual check that all
  expected columns are present with the right types.

## Schema freshness

The schema panel uses the column metadata already cached in the browser from
the most recent table data load — no extra API call is made. If you reload the
table data, the schema panel will reflect the updated schema.

## Related

- Issue [#80](https://github.com/IUSR/mylake/issues/80) — implementation
  request and discussion.
- Issue [#81](https://github.com/IUSR/mylake/issues/81) — the documentation
  ticket that produced this page.
- [Browsing tables](browsing.md) — pagination and rows-per-page selector.
