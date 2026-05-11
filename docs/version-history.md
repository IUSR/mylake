# Delta table version history

Delta Lake keeps a transaction log of every change made to a table. MyLake
surfaces this log as a **Version history** viewer, letting you browse past
snapshots of the data without leaving the UI. This page covers how to use it.

## Opening the version history

Click the **History** button in the top navbar (to the left of the status
badge). The button is disabled when no table is selected. A panel slides in
from the right side of the content area, listing all commits for the current
table (newest first).

Click **History** again, or the **×** (close) button in the panel header, to
close the panel.

## Reading the history list

Each entry in the history list represents one Delta table version (a
transaction log commit). The list is sorted newest-first and shows:

| Field         | Description                                                  |
| ------------- | ------------------------------------------------------------ |
| **Version**   | The Delta version number (an incrementing integer, starting at 0), shown as `v0`, `v1`, etc. |
| **Operation** | The write operation that created this version, e.g. `WRITE`, `DELETE`, `UPDATE`, `MERGE`, `OPTIMIZE`. |
| **Timestamp** | When this version was written.                               |
| **Rows out**  | Number of rows written by this operation (when available).   |

## Viewing a past snapshot

Click any row in the history list to load the table data _as it existed at
that version_. The data grid updates to show the historical rows and a banner
appears at the top of the content area indicating which version you are viewing:

> **Viewing version 12** — written 2024-03-14 09:41:22  
> [Return to latest version]

While browsing a past version:
- The **Export CSV** and **Export JSON** buttons work normally — they export the
  historical rows currently on screen.
- Filtering, sorting, and pagination all work as usual.
- The currently selected version is highlighted in the history panel.

## Returning to the latest version

Click the **Return to latest version** link in the banner to go back to the
current state of the table. You can also switch to a different table — the
history state resets automatically.

## Keyboard shortcut

Press **Ctrl+H** to toggle the version history panel without reaching for
the mouse.

## Use cases

- **Audit trail** — see what the data looked like before a bulk update or
  deletion.
- **Debugging** — compare the current and historical rows to find when an
  anomalous value was introduced.
- **Time travel queries** — preview a past snapshot without writing a Spark
  query (great for a quick sanity check).

## Limitations

- The version history viewer is read-only. You cannot roll back or restore
  data through the UI — that requires Delta Lake's native `RESTORE TABLE`
  command run on the cluster.
- Very old versions may no longer be accessible if the Delta table has been
  `VACUUM`-ed with a short retention window. In that case, those versions will
  not appear in the history list.

## Related

- Issue [#84](https://github.com/IUSR/mylake/issues/84) — implementation
  request and discussion.
- Issue [#85](https://github.com/IUSR/mylake/issues/85) — the documentation
  ticket that produced this page.
- [Table schema inspector](schema.md) — view column names and types.
- [Exporting data](exporting.md) — CSV export (works on historical views too).
- [Exporting data as JSON](json-export.md) — JSON export.
- [Keyboard shortcuts](keyboard-shortcuts.md) — full list of shortcuts.
