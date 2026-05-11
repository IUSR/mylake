# Restricting filesystem access with allowed paths

By default, MyLake can browse any directory that the server process has
read permission to. In a shared or production environment you may want to
limit the directories that users can explore. The `mylake.allowed-paths`
configuration property lets you define an allowlist of path prefixes.

## Configuration

Set the property in `src/main/resources/application.properties`:

```properties
# Comma-separated list of allowed filesystem path prefixes.
# Leave empty (default) to allow all paths.
mylake.allowed-paths=/data/lake,/mnt/delta
```

The default value is empty, which means **all paths are allowed** — the
behaviour is unchanged from previous versions.

## How the allowlist works

When `mylake.allowed-paths` is non-empty, every API request that accepts a
filesystem path checks the requested path against the configured prefixes:

1. The requested path is **normalized** (resolved to an absolute path with
   `..` segments removed) before comparison.
2. The normalized path must **start with** at least one of the configured
   prefixes (also normalized).
3. If the path matches a prefix, the request proceeds normally.
4. If the path does not match any prefix, the server returns **HTTP 403** with
   the error message:

   ```
   Access denied: path is outside the allowed directories
   ```

The following API endpoints are protected:

| Endpoint | Path parameter |
| --- | --- |
| `GET /api/tables?path=…` | The lake root directory |
| `GET /api/table/data?path=…` | The lake root directory |
| `POST /api/table/query` (body field `path`) | The full table path |
| `GET /api/fs/browse?path=…` | The directory to browse |

## Example

To allow users to access only `/data/lake` and `/mnt/delta`:

```properties
mylake.allowed-paths=/data/lake,/mnt/delta
```

With this configuration:

- `/data/lake/events` — **allowed** (starts with `/data/lake`)
- `/data/lake/../etc/passwd` — **denied** (normalizes to `/data/etc/passwd`, which does not start with `/data/lake`)
- `/mnt/delta/orders` — **allowed** (starts with `/mnt/delta`)
- `/tmp/scratch` — **denied** (no matching prefix)

## Security notes

- The normalization step prevents directory traversal attacks via `..` path
  segments.
- Path comparison is case-sensitive on all supported platforms.
- The allowlist is enforced server-side and cannot be bypassed from the
  browser.
- The property is read at startup. Changing it requires a server restart.

## When to use this

Leave `mylake.allowed-paths` empty (the default) for single-user local
development. Set it to one or more specific paths when running MyLake in a
shared environment where you do not want users browsing arbitrary filesystem
locations.

## Related

- Issue [#164](https://github.com/IUSR/mylake/issues/164) — implementation
  issue for the allowed-paths feature.
- Issue [#191](https://github.com/IUSR/mylake/issues/191) — the documentation
  ticket that produced this page.
- [Opening a lake and recent paths](lake-paths.md) — how to enter and browse a
  lake path in the UI.
