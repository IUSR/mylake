# Table properties

The table properties modal shows the Delta-level metadata stored in the
`_delta_log` for the table you are currently viewing: its unique ID,
human-readable name and description, partition columns, protocol versions, and
any key-value configuration properties.

## Opening the modal

Click the **Properties** button in the toolbar above the data table. A modal
dialog opens and MyLake fetches the table metadata from the server. Close the
modal with the **Close** button or the **×** in the modal header.

## Fields displayed

| Field | Description |
| --- | --- |
| **Table ID (UUID)** | The unique identifier assigned to this Delta table when it was created. Stable across writes and schema changes. |
| **Name** | The table name stored in the Delta log metadata, if set. |
| **Description** | The table description stored in the Delta log metadata, if set. |
| **Partition Columns** | Columns used to partition the table. Shows **None (unpartitioned)** if the table has no partition columns. |
| **Reader Version** | Minimum Delta protocol reader version required to read this table. |
| **Writer Version** | Minimum Delta protocol writer version required to write to this table. |
| **Table Properties** | Key-value configuration properties stored in the Delta log (e.g. `delta.enableChangeDataFeed = true`). Empty if no properties have been set. |

Name and Description are optional fields in the Delta protocol; they are shown
as `—` if the table creator did not supply them.

## Data source

Properties are read from the `_delta_log` JSON commit files of the table by
the `GET /api/table/properties` endpoint. The data reflects the most recent
checkpoint or commit — it is not cached in the browser between page loads.

## Dark mode

The table properties modal respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#122](https://github.com/IUSR/mylake/issues/122) — implementation
  issue for the table properties modal.
- [Table schema inspector](schema.md) — view column names and data types.
- [Per-column statistics panel](column-stats.md) — view aggregate statistics
  for each column.
