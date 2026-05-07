# Keyboard shortcuts

MyLake provides keyboard shortcuts for common actions in the table viewer.
If you spend a lot of time browsing and exporting data, these can save you a
lot of mouse travel.

## Quick reference

Press **?** at any time to open a shortcuts help overlay in the app. The table
below lists all available shortcuts:

### Navigation

| Shortcut         | Action                                          |
| ---------------- | ----------------------------------------------- |
| `→` / `←`       | Move focus one cell right / left                |
| `↑` / `↓`       | Move focus one cell up / down                   |
| `Tab`            | Move focus to the next cell (wraps to next row) |
| `Shift+Tab`      | Move focus to the previous cell                 |
| `Home`           | Jump to the first cell in the current row       |
| `End`            | Jump to the last cell in the current row        |
| `Ctrl+Home`      | Jump to the first cell of the first row         |
| `Ctrl+End`       | Jump to the last cell of the last row           |

### Paging

| Shortcut         | Action                                          |
| ---------------- | ----------------------------------------------- |
| `Alt+→`          | Go to the next page                             |
| `Alt+←`          | Go to the previous page                         |

### Search and filter

| Shortcut         | Action                                          |
| ---------------- | ----------------------------------------------- |
| `/`              | Focus the **Filter rows** search input          |
| `Ctrl+F`         | Focus the **Filter rows** search input          |
| `Escape`         | Clear the filter and return focus to the table  |

### Exporting

| Shortcut         | Action                                          |
| ---------------- | ----------------------------------------------- |
| `Ctrl+E`         | Export the current page to CSV                  |
| `Ctrl+J`         | Export the current page to JSON                 |

### Other

| Shortcut         | Action                                          |
| ---------------- | ----------------------------------------------- |
| `?`              | Open / close the keyboard shortcuts help overlay |

## Platform notes

- On **macOS**, substitute `Cmd` where `Ctrl` is written above.
- Shortcuts are active when focus is inside the table viewer area. They do
  not fire when you are typing in the filter input or another text field.

## Conflicts with browser shortcuts

A small number of shortcuts overlap with browser defaults:

- `Ctrl+F` (or `Cmd+F` on macOS) normally opens the browser's built-in page
  search. MyLake intercepts this shortcut _only when focus is inside the table
  viewer_, redirecting it to the filter input. The browser search can still be
  opened by clicking outside the table and then pressing `Ctrl+F`.

## Discovering shortcuts in the app

The shortcuts help overlay (press **?**) is always up to date. If a new
shortcut is added after this documentation was published, the overlay will
know about it before this page does.

## Related

- Issue [#90](https://github.com/IUSR/mylake/issues/90) — implementation
  request and discussion.
- Issue [#91](https://github.com/IUSR/mylake/issues/91) — the documentation
  ticket that produced this page.
- [Filtering rows](filtering.md) — the search bar you can focus with `/`.
- [Exporting data](exporting.md) — CSV export (`Ctrl+E`).
- [Exporting data as JSON](json-export.md) — JSON export (`Ctrl+J`).
