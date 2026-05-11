# Searching the table list

When a lake contains many Delta tables, scrolling through the full list can
be tedious. The sidebar search input lets you narrow the list to tables whose
names match a keyword, so you can jump to the right table in a keystroke or
two.

## Where to find it

The search input appears at the top of the sidebar, directly below the
**Tables** header and above the table list. It is always visible once a lake
has been loaded.

## How to use it

Type any part of a table name into the input. The list updates instantly,
hiding tables whose names do not contain the search text. The match is:

- **Case-insensitive** — searching for `orders` matches `Orders`, `ORDERS`,
  and `daily_orders`.
- **Substring-based** — searching for `user` matches `users`, `user_events`,
  and `monthly_user_summary`.

If none of the tables match the current query, the list shows
**No matching tables** instead of an empty space.

To clear the filter, delete the text from the input or click the browser's
built-in × clear button (present on most browsers for `type="search"` inputs).

## Keyboard shortcut

Press `/` (forward slash) to focus the sidebar search input from anywhere on
the page, as long as focus is not already inside another text input, textarea,
or select element. This lets you jump from the data grid to the table search
without reaching for the mouse.

## Scope

The sidebar search filters the **already-loaded table list**. It does not send
a new request to the server or search tables outside the current lake path.
To search a different lake, enter a new path in the lake-path bar and click
**Load Tables**.

## Auto-clear

The search input is automatically cleared whenever you load a new lake path
(click **Load Tables** or press Enter in the path bar). This prevents a
previous search from hiding newly loaded tables.

## Dark mode

The sidebar search input respects the MyLake dark mode setting. See
[Dark mode](dark-mode.md) for how to enable it.

## Related

- Issue [#159](https://github.com/IUSR/mylake/issues/159) — implementation
  issue for the sidebar table search.
- Issue [#174](https://github.com/IUSR/mylake/issues/174) — the documentation
  ticket that produced this page.
- [Opening a lake and recent paths](lake-paths.md) — how to load a lake and
  navigate the table list.
- [Filtering rows](filtering.md) — filtering rows inside a table (different
  from searching the table list).
- [Keyboard shortcuts](keyboard-shortcuts.md) — full list of shortcuts.
