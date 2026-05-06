# Browsing tables

MyLake displays the rows of any Delta table in a paginated grid. This page
covers the controls that let you move through large datasets comfortably and
tune how many rows appear at once.

## Rows per page

The pager bar at the bottom of the table grid contains a **Rows per page**
selector. Use it to control how many rows are fetched and displayed for each
page.

Available options: **25, 50, 100, 250, 500**. The default is **50**.

MyLake saves your choice in the browser's `localStorage` under the key
`mylake.pageSize`, so the selection survives page reloads and browser restarts.
The next time you open MyLake in the same browser, it picks up right where you
left off — no need to re-set your preferred size every session.

> **Private / Incognito mode.** `localStorage` is not available in private
> browsing. MyLake silently falls back to the 50-row default in that case —
> no error, no drama.

### Relationship to CSV export

The **Export CSV** button (see [Exporting data](exporting.md)) always exports
the page that is currently rendered. Raising the rows-per-page setting before
exporting is the simplest way to capture a larger slice of data in a single
file.

### Backend cap

The backend enforces a hard maximum of **1,000 rows per request**. All
selector options are well below this ceiling, so you will not encounter it
under normal use.

## Navigating pages

Below the data grid, the pager bar shows the current row range and the total
row count, together with previous/next buttons and individual page-number
links. Click any page number or the **‹** / **›** buttons to jump between
pages.

The page resets to the first page whenever you change the table, the lake
path, or the rows-per-page setting.

## Related

- Issue [#4](https://github.com/IUSR/mylake/issues/4) — feature request and
  implementation discussion.
- [Exporting data](exporting.md) — how to save the current page to a CSV file.
