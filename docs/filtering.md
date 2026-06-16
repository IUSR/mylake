# Filtering rows

The row filter lets you narrow down the data table to rows that match a
keyword, without writing SQL. This page covers how to use it and what to
expect.

## Where to find it

Click the **🔍 Filter** button in the content header toolbar (above the data
grid) to open the filter bar. The filter bar slides in below the toolbar.
Click **🔍 Filter** again, or the **✕** button inside the filter bar, to close
it and clear any active filter.

## How it works

Type any text into the filter input. MyLake immediately hides rows that do not
contain that text in any visible column, leaving only the matching rows on
screen. A match count (e.g. **3 matches**) is displayed next to the input.

The filter is:

- **Case-insensitive** — `delta` matches `Delta`, `DELTA`, and `DeLtA`.
- **Substring-based** — `ord` matches `order`, `orders`, and `reordered`.
- **Applied across all columns** — if any cell in a row contains the search
  text, that row is shown.

If no rows match, the table body shows **No rows match the filter**.

## Clearing the filter

Click the **✕** button inside the filter bar to clear the filter text and
restore all rows. This also closes the filter bar.

## Scope of filtering

Filtering operates on the **rows currently loaded on the page**. It does not
send a new request to the backend or search the full table — it hides rows
that are already in the browser.

If you want to search a larger slice of the table, increase the **Rows per
page** setting (see [Browsing tables](browsing.md)) before typing in the
filter box.

> **Heads up:** Because the filter works on the current page only, a filter
> that produces zero visible rows does not necessarily mean the value does not
> exist in the table — there may be matching rows on other pages. Navigate
> page-by-page or raise the page size to cast a wider net.

The filter state also resets automatically when you switch to a different table.

## Performance note

For very wide tables (many columns) or very large page sizes, the real-time
filtering may have a small delay. If you notice sluggishness, try reducing the
rows-per-page to 50 or 100 while filtering.

## Related

- Issue [#78](https://github.com/IUSR/mylake/issues/78) — implementation
  request and discussion.
- Issue [#79](https://github.com/IUSR/mylake/issues/79) — the documentation
  ticket that produced this page.
- [Browsing tables](browsing.md) — pagination and rows-per-page selector.
- [Sorting columns](sorting.md) — reorder rows by column values.
