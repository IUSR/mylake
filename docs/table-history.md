# Table history panel

The table history panel displays the Delta transaction log for the current
table: every write operation that has ever touched the table, listed newest
first. It is a quick way to see how active a table is, what kinds of
operations have been run on it, and when the most recent write happened.

## Opening the panel

Click the **History** button in the toolbar above the data grid. The panel
expands below the pager bar and MyLake fetches the history from the server.
Click **History** again, or the **×** (close) button in the panel header, to
collapse it.

## Reading the history list

Each row in the history list represents one Delta table version (a transaction
log commit). The list is sorted newest-first and shows three fields:

| Field | Description |
| --- | --- |
| **Version** | The Delta version number, shown as `v0`, `v1`, etc. Version 0 is the table creation commit; each subsequent write increments the version. |
| **Operation** | The type of write that produced this version — for example `WRITE`, `DELETE`, `UPDATE`, `MERGE`, or `OPTIMIZE`. |
| **Timestamp** | When this version was committed. Shown as `—` if the timestamp is not available in the log. |

## What this panel is not

The history panel is an **audit log view** — it shows what happened, not what
the data looked like at each point in time. Clicking a history row does nothing;
the panel does not support loading historical data.

If you need to actually browse the data as it existed at a past version, refer
to the [Delta table version history viewer](version-history.md), which provides
full time-travel capability.

## Dark mode

The table history panel respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#145](https://github.com/IUSR/mylake/issues/145) — implementation
  issue for the table history panel.
- Issue [#188](https://github.com/IUSR/mylake/issues/188) — the documentation
  ticket that produced this page.
- [Delta table version history viewer](version-history.md) — time travel:
  load the table data as it existed at any past version.
- [Table properties](table-properties.md) — view Delta metadata such as table
  ID, partition columns, and protocol versions.
- [Per-column statistics panel](column-stats.md) — view aggregate statistics
  for every column.
