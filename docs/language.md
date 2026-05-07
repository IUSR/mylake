# Changing the display language

MyLake's user interface is available in **English** and **Simplified Chinese**
(简体中文). You can switch between them at any time without reloading the page.

## The language switcher

The switcher sits in the top-right corner of the navbar, just to the left of
the status badge. It consists of two buttons:

| Button | Language             |
| ------ | -------------------- |
| **EN** | English              |
| **中文** | Simplified Chinese   |

The active language is highlighted — the button appears with a white
background and bold text.

Click either button to switch. All visible strings in the UI update
immediately: table headers, status messages, pagination labels, modal
dialogs, error toasts, and the path-bar placeholder.

## Persistence

MyLake saves your language choice in the browser's `localStorage` under the
key `mylake-lang`. The setting survives page reloads and browser restarts, so
you only need to set it once per browser.

> **Private / Incognito mode.** `localStorage` is unavailable in private
> browsing sessions. MyLake silently falls back to English in that case — no
> error message, no broken controls. Switching the language still works for
> the duration of the session; it just will not be remembered after the tab
> is closed.

## Translated strings

The following parts of the UI are translated:

- Path bar — placeholder text and **Load Tables** button label
- Sidebar — section header and empty-state message
- Content area — empty-state and "select a table" prompt
- Pager bar — "Per page", "Showing", "of", "rows", "columns" labels
- Status badge — checking, ready, not-ready, and server-offline states
- Loading and empty-state messages in the table grid
- Directory browser modal — title, navigation labels, **Cancel** and
  **Select This Directory** buttons
- Inline error messages (missing path, failed extension load)

## Related

- Issue [#1](https://github.com/IUSR/mylake/issues/1) — original i18n
  request.
- Issue [#43](https://github.com/IUSR/mylake/issues/43) — documentation
  ticket that produced this page.
