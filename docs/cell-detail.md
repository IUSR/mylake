# Cell detail popover

Table cells in the data grid are compact: long values are truncated with an
ellipsis. The cell detail popover lets you see the full, untruncated value of
any cell and copy it to the clipboard with a single click.

## Opening the popover

Click any data cell in the table body (any column except the row-number `#`
column on the left). A small popover appears near the cell showing:

- **Column name** — displayed in the popover header, so you always know which
  field you are reading.
- **Full value** — shown in a monospace pre-formatted box. The value is
  displayed exactly as stored, with no truncation. Very long values scroll
  vertically within the popover (up to 300 px height).

## Copying the value

Click the **Copy** button in the popover header to copy the raw cell value to
the clipboard. The button briefly shows **Copied!** to confirm. If the copy
fails (e.g. due to browser permissions), it shows **Failed** instead.

The copied text is the raw value exactly — no quotes, no formatting, no
trailing newlines.

## NULL values

`NULL` cells are displayed as `NULL (no value)` in grey italic text inside the
popover, matching the style used in the data grid.

## Dismissing the popover

The popover closes when you:

- Click anywhere outside the popover.
- Press **Escape**.

The popover also closes automatically when you navigate to a different page or
select a different table.

## Positioning

The popover appears just below the clicked cell. If there is not enough space
below, it repositions itself above the cell instead. The popover also stays
within the visible viewport horizontally.

## Relationship to the row detail panel

If you click a cell, the cell detail popover opens. The click does **not** also
open the row detail panel — the popover takes priority. To open the row detail
panel for the same row, close the popover first and then click the row.

## Dark mode

The cell detail popover respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#161](https://github.com/IUSR/mylake/issues/161) — implementation
  issue for the cell detail popover.
- Issue [#175](https://github.com/IUSR/mylake/issues/175) — the documentation
  ticket that produced this page.
- [Row detail panel](row-detail.md) — view all fields for a single row in a
  slide-in panel.
- [Filtering rows](filtering.md) — search across all columns by keyword.
- [Inline SQL query editor](sql-editor.md) — write SQL to retrieve exact values
  without browsing cell by cell.
