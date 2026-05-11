# Keyboard navigation

MyLake supports keyboard navigation throughout the table browser, letting you
move between the sidebar, the data grid, and the controls without reaching for
the mouse.

## Navigating the table list

Once a lake has been loaded, the tables in the sidebar are fully keyboard
accessible.

| Key | Action |
| --- | ------ |
| `Tab` | Move focus into the table list from the path bar, search input, or another control. |
| `↑` | Move focus to the previous table item in the sidebar. |
| `↓` | Move focus to the next table item in the sidebar. |
| `Enter` or `Space` | Select the focused table and load its data. |

Focused table items are highlighted with a blue outline so you can see where
keyboard focus is at all times.

After you select a table (with Enter, Space, or a mouse click), focus moves
to the content area automatically, so you can immediately use `←` / `→` to
navigate pages without pressing Tab.

## Paging with arrow keys

When focus is anywhere in the content area (and not inside a text input or
textarea), press:

| Key | Action |
| --- | ------ |
| `←` | Go to the previous page. |
| `→` | Go to the next page. |

These shortcuts are suppressed when focus is inside a text input, textarea, or
select element (for example, when you are typing in the row filter or the SQL
editor). See [Keyboard shortcuts](keyboard-shortcuts.md) for the full list of
shortcuts.

## Sidebar table search integration

The sidebar search input (`/` to focus) also plays well with keyboard
navigation: after typing a query, press `↓` to move focus directly into the
filtered table list and use `↑` / `↓` / `Enter` to select a table.

## Accessibility notes

- Table items use `tabindex="0"` and `role="button"` so that assistive
  technologies recognise them as interactive elements.
- The focus outline meets WCAG 2.1 AA visibility requirements.
- Arrow-key navigation skips hidden items (tables filtered out by the sidebar
  search).

## Related

- Issue [#155](https://github.com/IUSR/mylake/issues/155) — implementation
  issue for keyboard navigation.
- Issue [#180](https://github.com/IUSR/mylake/issues/180) — the documentation
  ticket that produced this page.
- [Keyboard shortcuts](keyboard-shortcuts.md) — the full shortcut reference,
  including paging, exporting, and the SQL editor.
- [Searching the table list](table-search.md) — filter the sidebar by table
  name using the `/ ` shortcut.
