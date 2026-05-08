# Exporting data as JSON

In addition to [CSV export](exporting.md), MyLake can save the rows you are
currently viewing as a JSON file. This is handy when you want to consume the
data in JavaScript, feed it into another tool, or keep the column names as
explicit keys rather than relying on positional CSV columns.

## Where to find it

When a Delta table is open, the toolbar above the data grid contains an
**Export JSON** button alongside the existing **Export CSV** button. The button
is greyed out if the current page has no rows to export.

## What gets exported

Like CSV export, **only the rows on the current page** are included in the
download — not the full table.

The output is a JSON array where each element is an object representing one
row. Object keys are the column names; values are the cell values.

**Example** (a two-row export from a table with columns `id`, `name`, `score`):

```json
[
  { "id": 1, "name": "Alice", "score": 98.5 },
  { "id": 2, "name": "Bob",   "score": 74.0 }
]
```

### Type handling

MyLake preserves native JSON types where possible:

| Delta / Spark type          | JSON representation                          |
| --------------------------- | -------------------------------------------- |
| `long`, `integer`, `short`  | JSON number                                  |
| `double`, `float`, `decimal`| JSON number (floating-point)                 |
| `boolean`                   | `true` / `false`                             |
| `string`                    | JSON string                                  |
| `date`, `timestamp`         | ISO 8601 string, e.g. `"2024-03-15T12:00:00Z"` |
| `null`                      | `null`                                       |
| `array`, `map`, `struct`    | Nested JSON array / object                   |

### File naming

The downloaded file is named `<table>-page<N>-<timestamp>.json`, where the
timestamp is in the format `YYYY-MM-DDTHH-MM-SS` (colons replaced with
hyphens). Page numbers start at 1. For example, exporting page 2 of a table
called `events` on 2024-03-15 at 12:00 produces a file named something like
`events-page2-2024-03-15T12-00-00.json`.

## Tips

- **Need more rows?** Raise the **Rows per page** setting (see
  [Browsing tables](browsing.md)) before clicking **Export JSON** — the export
  always matches what is on screen.
- **Want CSV instead?** Use **Export CSV** for spreadsheet-friendly output
  (see [Exporting data](exporting.md)).
- **Processing the file?** The output is standard JSON — any JSON parser will
  handle it. The array wrapper means you can use `jq .[N]` to pick individual
  rows.

## Related

- Issue [#82](https://github.com/IUSR/mylake/issues/82) — implementation
  request and discussion.
- Issue [#83](https://github.com/IUSR/mylake/issues/83) — the documentation
  ticket that produced this page.
- [Exporting data](exporting.md) — CSV export.
- [Browsing tables](browsing.md) — pagination and rows-per-page selector.
