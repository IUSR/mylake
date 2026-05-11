# Server-side column filters

Column filters let you narrow the data table to rows whose values match a
keyword, per column. Unlike the in-page row filter, column filters send the
search criteria to the server — so they work across the **full table**, not
just the current page.

## Opening the filter row

Click the **🔍 Filter** button in the toolbar above the data grid. A filter
row appears directly below the column headers. Each column has its own text
input. Click **🔍 Filter** again to hide the filter row (your filter values
are preserved while the row is hidden).

## Filtering by column

Type in any column's filter input. After a 400 ms pause, MyLake sends a new
request to the server with your filter criteria and reloads the first page of
results. The total row count shown in the pager reflects how many rows in the
full table match all active filters.

The filter is:

- **Case-insensitive** — `Order` matches `order`, `ORDER`, and `New Order`.
- **Substring-based** — `son` matches `Johnson`, `mason`, and `Anson`.
- **Applied to all data types** — numeric, date, and timestamp values are
  cast to text before matching, so `2024` matches any value containing
  that string.

Column inputs with active filter values are highlighted with a yellow
background so you can see at a glance which columns are being filtered.
Column headers are also highlighted (in amber/yellow) when that column has
a filter.

## Multiple active filters

You can type in more than one column's filter input at the same time. All
active filters are combined with **AND** — a row is included only if it
matches every active filter. This lets you narrow to a specific combination
(for example, all rows where `country` contains `US` and `status` contains
`active`).

## Active filter indicator

The **🔍 Filter** button shows a dot (●) next to "Filter" when at least one
column filter is active, even if the filter row is hidden. This acts as a
reminder that the data you see may not be the full table.

## Clearing filters

Delete the text from a column's input to remove that column's filter. All
other active filters remain. The data reloads immediately (after the 400 ms
debounce). To clear all filters at once, switch to a different table — all
column filters are reset when you select a new table.

## How this differs from the in-page row filter

MyLake has two filtering mechanisms with different scopes:

| | Column filters (this page) | [Row filter](filtering.md) |
| --- | --- | --- |
| **Scope** | Full table (server-side) | Current page only (browser) |
| **Per column?** | Yes — each column has its own input | No — one text box searches all columns |
| **Pagination** | Resets to page 1 on change; pager shows filtered count | Does not affect pagination |
| **Performance** | Suitable for very large tables | May be slow on large page sizes |
| **API request** | New request per filter change | No request |

Use column filters when you want to find rows that match specific criteria
across the full dataset. Use the row filter for a quick glance at rows on
the current page without waiting for a network round-trip.

## API details

Column filters are passed to the `GET /api/table/data` endpoint as query
parameters in the form `filter[colName]=value`. For example:

```
GET /api/table/data?path=/data/lake&table=events&page=0&size=50&filter[country]=US&filter[status]=active
```

The server applies `CAST(colName AS VARCHAR) ILIKE '%value%'` for each active
filter, combined with `AND`.

## Related

- Issue [#147](https://github.com/IUSR/mylake/issues/147) — implementation
  issue for server-side column filters.
- Issue [#197](https://github.com/IUSR/mylake/issues/197) — the documentation
  ticket that produced this page.
- [Filtering rows](filtering.md) — in-page row filter (current page only, all columns).
- [Inline SQL query editor](sql-editor.md) — for complex filter expressions
  beyond simple substring matching.
- [Column sorting](sorting.md) — sort the filtered results by any column.
