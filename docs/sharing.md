# Sharing a link to the current view

MyLake keeps the browser URL in sync with what you are looking at. This means
you can copy the URL from the address bar at any time and share it — whoever
opens the link lands on exactly the same view you were looking at: same table,
same page, same sort, same filter.

## How it works

As you interact with the table viewer, MyLake updates the URL's query
parameters in the background (using the browser's History API, so the page
does not reload). The URL captures:

| Parameter | What it records                              | Example value       |
| --------- | -------------------------------------------- | ------------------- |
| `path`    | The lake path (Delta table location)         | `s3://my-bucket/events` |
| `page`    | The current page number (1-based)            | `3`                 |
| `sort`    | The column being sorted, if any              | `created_at`        |
| `dir`     | Sort direction (`asc` or `desc`)             | `desc`              |
| `filter`  | The current filter query, if any             | `alice`             |

## Sharing a link

1. Navigate to the table view you want to share.
2. Apply any sort, filter, or page navigation you want the recipient to see.
3. Copy the URL from your browser's address bar.
4. Paste it wherever you like — email, Slack, a GitHub issue comment, etc.

The recipient opens the URL and sees the same view immediately. No login or
configuration required (beyond having access to the same MyLake instance and
the underlying Delta table).

## Browser navigation

Because the URL updates as you navigate, the browser's **Back** and
**Forward** buttons work as expected — you can step back through pages and
sorts just like navigating between web pages.

## Bookmarking

You can bookmark any URL to save a frequently visited table view. The bookmark
will always open the same table at the same page, sort, and filter.

## Limitations

- The URL reflects the **current page state**, not the full table. If the
  table data changes between when you copy the URL and when the recipient opens
  it, they will see the updated data at the same coordinates (page 3, column
  sorted, etc.) rather than a frozen snapshot. Use the
  [version history viewer](version-history.md) if you need to share a specific
  historical snapshot.
- Very long filter strings or table paths may make the URL unwieldy, but they
  will still work.
- The URL parameters are plain text and not encrypted. Avoid putting sensitive
  credentials or tokens in the filter field.

## Related

- Issue [#86](https://github.com/IUSR/mylake/issues/86) — implementation
  request and discussion.
- Issue [#87](https://github.com/IUSR/mylake/issues/87) — the documentation
  ticket that produced this page.
- [Filtering rows](filtering.md) — search bar and row-level filtering.
- [Sorting columns](sorting.md) — column sort controls.
- [Version history](version-history.md) — share a link to a specific past
  snapshot.
