# Hiding and showing columns

Wide tables with many columns can be hard to read in the data grid. The
column visibility control lets you hide any column you do not need and focus
on the ones that matter.

## Opening the column picker

Click the **Columns** button in the toolbar above the data grid (it appears
next to the row-count and column-count badges). A dropdown opens listing every
column in the current table. Each column has a checkbox:

- **Checked (✓)** — the column is visible in the grid.
- **Unchecked** — the column is hidden.

## Toggling column visibility

Click a checkbox to toggle that column's visibility. The grid re-renders
instantly — no page reload and no network request. You can check and uncheck
as many columns as you like.

## Effect on CSV and JSON export

When you export data with **Export CSV** or **Export JSON**, only the columns
that are currently visible are included in the downloaded file. Hidden columns
are omitted. This is useful for producing a focused extract without
post-processing the file.

## Resetting column visibility

Column visibility is reset automatically when you switch to a different table.
All columns return to visible when you select a new table from the sidebar.

Column visibility is **not** persisted between page reloads. Refreshing the
browser or navigating away and back will restore all columns to visible.

## Sorting a hidden column

If you sort the table by a column and then hide that column:

- The underlying data is still sorted in the same order.
- The sort indicator (▲/▼) is not visible because the column header is hidden.
- The sort is preserved until you switch tables or clear it by clicking a
  visible column header twice.

## Related

- Issue [#157](https://github.com/IUSR/mylake/issues/157) — implementation
  issue for the column visibility toggle.
- Issue [#181](https://github.com/IUSR/mylake/issues/181) — the documentation
  ticket that produced this page.
- [Column sorting](sorting.md) — sort the data grid by any visible column.
- [Table schema inspector](schema.md) — view the full list of column names and
  types without changing what is shown in the grid.
- [Exporting data (CSV)](exporting.md) — column visibility affects CSV output.
- [Exporting data (JSON)](json-export.md) — column visibility affects JSON output.
