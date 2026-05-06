# Exporting data

MyLake can save the rows you are looking at to a CSV file with a single click.
This page explains where to find the feature, exactly what gets written to the
file, and how to grab a larger sample when you need one.

## Where to find it

When you open a Delta table, MyLake shows the data in a paginated grid. Above
the grid is a small toolbar with the row and column counts. The **Export CSV**
button sits on that toolbar.

The button is greyed out until rows are actually displayed — if the current
page is empty, there is nothing to export.

## What gets exported

**Only the page you are currently looking at**, not the entire table.

If your table has 10,000 rows and your page size is 50, clicking **Export CSV**
saves 50 rows. To export a different slice, navigate to a different page (or
change the page size — see [Tips](#tips) below) and click again.

> Why only the current page? MyLake streams pages on demand to keep the UI
> snappy on tables of any size. A full-table export needs a different code
> path; it is on our wish-list rather than in the box today. If you need it,
> please let us know on the issue tracker.

## File format

| Detail        | Value                                                  |
| ------------- | ------------------------------------------------------ |
| Filename      | `<table-name>-page<N>.csv` (page numbers start at 1)   |
| Encoding      | UTF-8                                                  |
| Line endings  | CRLF (`\r\n`), with a trailing newline                 |
| Header row    | Column names, in the same order as the on-screen grid  |
| Quoting       | RFC 4180 — fields containing a comma, double quote, carriage return, or line feed are wrapped in double quotes; embedded double quotes are escaped by doubling them |
| Null values   | Written as an empty cell                               |

So if you are browsing a table called `orders` on page 3, the downloaded file
will be `orders-page3.csv`.

## Tips

- **Want more rows in one file?** Use the **Rows per page** selector in the
  pager bar (see [Browsing tables](browsing.md)) to increase the page size up
  to 500 before clicking **Export CSV**. The export always matches the page
  that is currently rendered, so a larger page produces a larger file.
- **Spreadsheet quirks.** The file is plain UTF-8 without a byte-order mark.
  Most modern spreadsheet applications import it cleanly; if yours mangles
  non-ASCII characters, look for an "Import CSV" / "From Text" option that
  lets you pick UTF-8 explicitly instead of double-clicking the file.
- **No re-fetch.** Exporting uses the rows already on screen, so it is
  instant and does not put extra load on the backend.

## Related

- Issue [#3](https://github.com/IUSR/mylake/issues/3) — the original feature
  request and discussion.
- Issue [#6](https://github.com/IUSR/mylake/issues/6) — the documentation
  ticket that produced this page.
- [Browsing tables](browsing.md) — rows-per-page selector and pagination.
