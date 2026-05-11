# Row detail panel

The row detail panel shows all values for a single row in a vertical list,
one field per line. It is useful for reading long or truncated cell values that
do not fit in the compact data grid columns, or for inspecting a specific row
without having to scroll horizontally.

## Opening the panel

Click any data row in the table grid. The panel slides in from the right edge
of the content area and displays every column name and its value for that row.
The panel header shows the row's sequence number (e.g. **Row 42**) based on
its position in the current page.

The selected row is highlighted with a yellow (light mode) or amber (dark mode)
background so you can always see which row the panel is showing.

## Reading the panel

Each field in the panel shows:

- **Column name** — displayed in small uppercase text above the value.
- **Value** — displayed below the column name. Long values wrap across multiple
  lines rather than being truncated.

`NULL` values are shown as `NULL` in grey italics, matching the style used in
the data grid.

## Closing the panel

Click the **×** button in the panel header. The panel slides out and the row
highlight is removed.

The panel also closes automatically when you:

- Switch to a different table.
- Navigate to a different page (previous/next or a page number link).

## Working alongside the panel

The row detail panel is designed to coexist with other panels:

- The **schema inspector** (right-side slide-in) and the row detail panel share
  the same right edge of the content area. Only one can be visible at a time —
  opening the schema panel while the row detail panel is open will push the row
  detail panel behind it. Close the schema panel to return to the row detail
  view.
- The **column statistics panel** (below the data grid) is independent and can
  be open at the same time as the row detail panel.
- The **SQL editor** (below the data grid) is also independent.

## Dark mode

The row detail panel respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#138](https://github.com/IUSR/mylake/issues/138) — implementation
  issue for the row detail side panel.
- Issue [#169](https://github.com/IUSR/mylake/issues/169) — the documentation
  ticket that produced this page.
- [Table schema inspector](schema.md) — view column names and types for the
  whole table (not a single row).
- [Filtering rows](filtering.md) — narrow the data grid to rows matching a keyword.
- [Column sorting](sorting.md) — reorder rows by any column.
