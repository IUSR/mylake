# Opening a lake

To browse Delta tables in MyLake, you first tell it where your lake lives by
typing (or pasting) a filesystem path into the **Lake path** bar at the top of
the page and pressing **Load Tables** (or hitting Enter).

## Recent paths

After you successfully load a lake, MyLake remembers that path. The next time
you want to go back, you can pick it from the dropdown instead of typing it
again.

The dropdown caret sits immediately to the right of the path input, inside the
same input group. Click it to open a menu listing your last **five** unique
paths, most-recent first.

Selecting an entry fills the path input and immediately triggers the table
load — same as typing the path and pressing **Load Tables**.

### How persistence works

Recent paths are stored in your browser's `localStorage` under the key
`mylake.recentPaths`. The list is:

- **Deduplicated** — loading the same path twice does not create a duplicate
  entry; it moves that path to the top of the list instead.
- **Capped at 5 entries** — oldest entries fall off automatically when the
  list would exceed five items.
- **Cross-tab aware** — the menu is repopulated from `localStorage` each time
  you open it, so a path you loaded in another browser tab shows up immediately
  the next time you click the caret.

A path is recorded only on a **successful** response from the API — so typos
and unreachable directories will not pollute your history.

### Empty-lake paths are still remembered

If the path points to a valid directory that happens to contain zero Delta
tables, MyLake still records it. The API call itself succeeded, and you may
want to revisit that directory as tables are added.

### Private / Incognito mode

`localStorage` is not available in private browsing sessions. In that case the
recent-paths list is always empty and nothing is saved. The feature degrades
silently — no error message, no disabled controls.

### Clearing recent paths

You can clear the list at any time through your browser's developer tools
(`Application → Local Storage → mylake.recentPaths → delete`) or by clearing
site data in the browser settings. MyLake does not provide a clear button in
the UI today.

## Related

- Issue [#5](https://github.com/IUSR/mylake/issues/5) — feature request and
  implementation discussion.
- [Browsing tables](browsing.md) — rows-per-page selector and pagination.
