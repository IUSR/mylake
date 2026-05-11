# MyLake documentation

MyLake is a browser-based viewer for Delta Lake tables. This index lists every
documentation page currently available, grouped by topic.

## Getting started

| Page | What it covers |
| ---- | -------------- |
| [Opening a lake and recent paths](lake-paths.md) | Entering a Delta Lake root directory, browsing the filesystem, and reusing recently opened paths. |

## Browsing data

| Page | What it covers |
| ---- | -------------- |
| [Browsing tables](browsing.md) | Selecting a table from the sidebar, navigating pages, and setting rows per page. |
| [Searching the table list](table-search.md) | Filtering the sidebar table list by name using a keyword. |
| [Filtering rows](filtering.md) | Using the row filter to narrow down the data table by keyword. |
| [Column sorting](sorting.md) | Sorting the data table by any column. |
| [Column visibility](column-visibility.md) | Hiding and showing columns in the data grid; effect on CSV and JSON export. |
| [Row detail panel](row-detail.md) | Viewing all fields for a selected row in a slide-in side panel. |
| [Cell detail popover](cell-detail.md) | Clicking a cell to see its full untruncated value and copy it to the clipboard. |
| [Table schema inspector](schema.md) | Viewing column names and data types in the schema side-panel. |
| [Per-column statistics panel](column-stats.md) | Null count, min, max, and distinct count for every column. |

## Exporting data

| Page | What it covers |
| ---- | -------------- |
| [Exporting data (CSV)](exporting.md) | Downloading the current page as a CSV file. |
| [Exporting data (JSON)](json-export.md) | Downloading the current page as a JSON file. |
| [Exporting all rows](export-full.md) | Downloading the full table (up to 500,000 rows) as CSV or JSON via server-side streaming. |
| [Inline SQL query editor](sql-editor.md) | Writing and running DuckDB SQL against the current table from within the browser. |
| [Refreshing table data](refresh.md) | Reloading the current page data from the server without changing view settings. |

## Table metadata

| Page | What it covers |
| ---- | -------------- |
| [Delta table version history](version-history.md) | Browsing the Delta transaction log and viewing historical snapshots. |
| [Table history panel](table-history.md) | Viewing the write history (version, operation, timestamp) as a read-only audit log. |
| [Table properties](table-properties.md) | Viewing the Delta metadata stored in `_delta_log`: table ID, name, partition columns, protocol versions, and custom configuration. |

## UI and accessibility

| Page | What it covers |
| ---- | -------------- |
| [Dark mode](dark-mode.md) | Toggling between light and dark themes; preference persistence. |
| [Language switcher](language.md) | Switching the UI between English and Simplified Chinese. |
| [Keyboard shortcuts](keyboard-shortcuts.md) | All keyboard shortcuts for navigation, filtering, exporting, and more. |
| [Keyboard navigation](keyboard-navigation.md) | Navigating the sidebar table list and paging with the keyboard alone. |
| [Shareable URL](sharing.md) | Copying a URL (hash-based) that restores the lake path, table, page, and rows-per-page when shared. |

## Administration

| Page | What it covers |
| ---- | -------------- |
| [Allowed paths](allowed-paths.md) | Restricting which filesystem directories MyLake can access via the `mylake.allowed-paths` configuration property. |
| [Full-table export configuration](export-full.md#row-limit) | Setting the `mylake.export.max-rows` limit for server-side full-table exports. |

## Contributing to the docs

Documentation lives in the `docs/` directory of the repository. Each page is a
Markdown file. If you spot an error or want to add coverage for a new feature,
open a GitHub issue and assign it to `alex-young-cs`.
