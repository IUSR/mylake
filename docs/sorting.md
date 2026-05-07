# Sorting columns

MyLake lets you reorder rows in the data table by clicking any column header.
This page explains how sorting works and what to expect when you use it.

## How to sort

Click a **column header** in the data table grid. The table immediately
re-renders with rows ordered by the values in that column, ascending (A → Z,
smallest → largest, earliest → latest).

Click the **same column header again** to reverse the order (descending).
Click a **different column header** at any time to switch to that column.

A small arrow indicator on the active column header shows the current sort
direction:

| Indicator | Meaning                   |
| --------- | ------------------------- |
| ↑ (up)    | Ascending (A → Z, 1 → 9)  |
| ↓ (down)  | Descending (Z → A, 9 → 1) |

## Scope of sorting

Sorting is applied to the **current page** of data. MyLake fetches each page
from the backend and sorts the rows on that page in the browser. If your table
has multiple pages, the sort does not span pages — page 2 is sorted
independently of page 1.

> **Tip:** If you need a globally ordered view, set the **Rows per page**
> selector (see [Browsing tables](browsing.md)) to a value large enough to
> capture all the rows you care about, then sort.

## Supported column types

Sorting works on all column types that have a natural comparison order:
strings, numbers, dates, and timestamps. For columns that contain complex
types (structs, arrays, maps), the sort may fall back to a string
representation — results may not be in the order you expect.

## Resetting the sort

To remove sorting and return to the original row order, click the active
(highlighted) column header a third time. The sort indicator disappears and
rows return to their default order.

Navigating to a different table or reloading the page also resets the sort.

## Related

- Issue [#76](https://github.com/IUSR/mylake/issues/76) — implementation
  request and discussion.
- Issue [#77](https://github.com/IUSR/mylake/issues/77) — the documentation
  ticket that produced this page.
- [Browsing tables](browsing.md) — pagination and rows-per-page selector.
- [Filtering rows](filtering.md) — search bar and row-level filtering.
