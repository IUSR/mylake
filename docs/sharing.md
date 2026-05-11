# Sharing a link to the current view

MyLake keeps the browser URL in sync with what you are looking at. This means
you can copy the URL from the address bar at any time and share it — whoever
opens the link lands on exactly the same view: same lake path, same table,
same page, same rows-per-page setting.

## How it works

As you interact with the table viewer, MyLake updates the URL **hash** (the
`#` fragment at the end of the address) using the browser's History API, so
the page does not reload. The hash captures:

| Parameter | What it records                                | Example value           |
| --------- | ---------------------------------------------- | ----------------------- |
| `path`    | The lake path (Delta table root directory)     | `/data/my_lake`         |
| `table`   | The selected table name                        | `events`                |
| `page`    | The current page number (0-based, omitted on page 0) | `2`             |
| `size`    | The rows-per-page setting (omitted if default) | `100`                   |

A full example URL might look like:

```
http://localhost:8080/#path=%2Fdata%2Fmy_lake&table=events&page=2&size=100
```

## Sharing a link

1. Navigate to the table view you want to share.
2. Navigate to the page you want the recipient to see.
3. Copy the URL from your browser's address bar (the hash is included
   automatically).
4. Paste it wherever you like — email, Slack, a GitHub issue comment, etc.

When the recipient opens the URL, MyLake reads the hash on page load, restores
the lake path and table list, and loads the specified table and page. If the
path no longer exists or the table cannot be found, a brief error message
appears and the app falls back gracefully to the empty state.

## Bookmarking

You can bookmark any URL to save a frequently visited table view. The bookmark
will open the same lake path, table, and page each time.

## Rows-per-page is preserved

Unlike the previous URL format, the hash also captures the `size` parameter if
you have changed the rows-per-page from the default. When a recipient opens the
link, they see the same number of rows per page you had selected.

## Limitations

- The URL reflects **current view coordinates** (path, table, page), not a
  frozen data snapshot. If the underlying table data changes between when you
  share the link and when the recipient opens it, they will see the latest
  data at those coordinates, not a historical copy. Use the
  [version history viewer](version-history.md) if you need to share a specific
  past snapshot.
- The browser **Back** and **Forward** buttons do not navigate between
  previously visited tables or pages. MyLake uses `replaceState` (not
  `pushState`), so each navigation updates the current history entry rather
  than creating a new one. Use the sidebar or the pager controls to navigate.

## Related

- Issue [#153](https://github.com/IUSR/mylake/issues/153) — implementation
  of the hash-based shareable URL.
- Issue [#185](https://github.com/IUSR/mylake/issues/185) — the documentation
  update ticket that produced this revision.
- [Browsing tables](browsing.md) — navigating pages and rows-per-page settings.
- [Delta table version history](version-history.md) — share a link to a
  specific historical snapshot.
- [Recent paths](lake-paths.md) — another way to quickly return to a
  frequently used lake.
