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
| [Filtering rows](filtering.md) | Using the row filter to narrow down the data table by keyword. |
| [Column sorting](sorting.md) | Sorting the data table by any column. |
| [Table schema inspector](schema.md) | Viewing column names and data types in the schema side-panel. |
| [Per-column statistics panel](column-stats.md) | Null count, min, max, and distinct count for every column. |

## Exporting data

| Page | What it covers |
| ---- | -------------- |
| [Exporting data (CSV)](exporting.md) | Downloading the current page as a CSV file. |
| [Exporting data (JSON)](json-export.md) | Downloading the current page as a JSON file. |
| [Inline SQL query editor](sql-editor.md) | Writing and running DuckDB SQL against the current table from within the browser. |

## Table metadata

| Page | What it covers |
| ---- | -------------- |
| [Delta table version history](version-history.md) | Browsing the Delta transaction log and viewing historical snapshots. |
| [Table properties](table-properties.md) | Viewing the Delta metadata stored in `_delta_log`: table ID, name, partition columns, protocol versions, and custom configuration. |

## UI and accessibility

| Page | What it covers |
| ---- | -------------- |
| [Dark mode](dark-mode.md) | Toggling between light and dark themes; preference persistence. |
| [Language switcher](language.md) | Switching the UI between English and Simplified Chinese. |
| [Keyboard shortcuts](keyboard-shortcuts.md) | All keyboard shortcuts for navigation, filtering, exporting, and more. |
| [Shareable URL](shareable-url.md) | Copying a URL that restores the current lake path, table, and page on reload or when shared. |

## Contributing to the docs

Documentation lives in the `docs/` directory of the repository. Each page is a
Markdown file. If you spot an error or want to add coverage for a new feature,
open a GitHub issue and assign it to `alex-young-cs`.
