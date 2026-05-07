# Dark mode

MyLake supports a dark color theme that is easier on the eyes in low-light
environments. This page explains how to switch between light and dark mode and
how your preference is remembered.

## Toggling dark mode

Click the **moon icon** (🌙) in the top-right corner of the navbar to switch
to dark mode. When dark mode is active, the icon changes to a **sun icon**
(☀️) — click it again to return to light mode.

The entire UI updates immediately: the background, text, table grid, panels,
and all controls switch to the dark palette. No page reload is required.

## Preference persistence

MyLake saves your theme preference in the browser's `localStorage` under the
key `mylake-theme`. The value is either `"dark"` or `"light"`. This means:

- The theme is restored the next time you open MyLake in the same browser.
- Your choice is per-browser, not per-account — if you use MyLake in multiple
  browsers or devices, you may need to set the preference on each one.

> **Private / Incognito mode.** `localStorage` is not available in private
> browsing sessions. In that case, MyLake falls back to the system default
> (see below) and does not save your preference.

## System default

If you have never set a preference (or are in a private browsing session),
MyLake checks the browser's `prefers-color-scheme` media query and defaults to
dark mode if your operating system or browser is set to a dark theme.
Otherwise it defaults to light mode.

| Condition                              | MyLake default |
| -------------------------------------- | -------------- |
| `localStorage` has `mylake-theme`      | Uses saved preference |
| No saved preference, OS in dark mode   | Dark mode      |
| No saved preference, OS in light mode  | Light mode     |

## Accessibility

Dark mode uses a contrast ratio that meets WCAG 2.1 AA guidelines for text
readability. If you find the contrast too high or too low, let us know on the
issue tracker.

## Related

- Issue [#88](https://github.com/IUSR/mylake/issues/88) — implementation
  request and discussion.
- Issue [#89](https://github.com/IUSR/mylake/issues/89) — the documentation
  ticket that produced this page.
- [Language switcher](language.md) — the EN / 中文 toggle in the same navbar
  area.
