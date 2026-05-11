# Refreshing table data

The **↺ Refresh** button reloads the current page of table data from the
server without changing any of your view settings — the table, page number,
sort order, and rows-per-page selection all stay as they were.

## Where to find it

The Refresh button is in the toolbar above the data grid, between the
**Export JSON** button and the **SQL Editor** button.

## When to use it

MyLake fetches table data on demand when you select a table or navigate to a
new page. It does not poll for changes in the background. If the underlying
Delta table is being written to by another process, the data shown in the grid
may become stale. Click **↺ Refresh** to fetch the latest rows without losing
your current position.

Typical use cases:

- You are monitoring a table that receives frequent writes and want to see the
  latest rows.
- You ran a write operation in a separate tool and want to confirm the data
  landed correctly.
- You shared a URL with a colleague and want to make sure you are both looking
  at the same snapshot.

## What the button does not do

- It does **not** reload the table list from the sidebar — only the data for
  the currently selected table is refreshed.
- It does **not** reset the page, sort, or rows-per-page settings.
- It does **not** clear the row filter or close the SQL editor panel.

## Visual feedback

While the refresh is in progress, the button is disabled and its label changes
to **⟳ Refresh** (spinning arrow). Once the new data is loaded and rendered,
the button returns to its normal **↺ Refresh** state.

## Dark mode

The Refresh button respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#140](https://github.com/IUSR/mylake/issues/140) — implementation
  issue for the manual refresh button.
- Issue [#170](https://github.com/IUSR/mylake/issues/170) — the documentation
  ticket that produced this page.
- [Browsing tables](browsing.md) — navigating pages and setting rows per page.
- [Delta table version history](version-history.md) — if you need to see
  exactly when data was written, the version history panel shows timestamps for
  each transaction.
