package com.mylake;

import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class DeltaLakeResource {

    private static final Logger LOG = Logger.getLogger(DeltaLakeResource.class);

    @Inject
    DeltaLakeService svc;

    /**
     * Comma-separated list of allowed path prefixes. Empty = no restriction (default).
     * Example: mylake.allowed-paths=/data/lake,/mnt/delta
     */
    @ConfigProperty(name = "mylake.allowed-paths", defaultValue = "")
    String allowedPathsConfig;

    /**
     * Validates that the given path is within the configured allowlist.
     * Normalises the path before checking. Returns null if allowed, or a 403 Response if denied.
     */
    private Response validatePath(String rawPath) {
        if (allowedPathsConfig == null || allowedPathsConfig.isBlank()) {
            return null; // no restriction
        }
        java.nio.file.Path normalised = java.nio.file.Path.of(rawPath).normalize().toAbsolutePath();
        String[] prefixes = allowedPathsConfig.split(",");
        for (String prefix : prefixes) {
            String trimmed = prefix.trim();
            if (trimmed.isEmpty()) continue;
            java.nio.file.Path allowedPrefix = java.nio.file.Path.of(trimmed).normalize().toAbsolutePath();
            if (normalised.startsWith(allowedPrefix)) {
                return null; // allowed
            }
        }
        LOG.warnf("Path rejected by allowlist: %s", normalised);
        return Response.status(403).entity(Map.of("error", "Access denied: path is outside the allowed directories")).build();
    }

    @GET
    @Path("/status")
    public Map<String, Object> status() {
        return Map.of(
            "ready", svc.isReady(),
            "error", svc.getInitError() != null ? svc.getInitError() : ""
        );
    }

    @GET
    @Path("/tables")
    public Response tables(@QueryParam("path") String path) {
        if (path == null || path.isBlank()) return bad("missing 'path' parameter");
        Response denied = validatePath(path);
        if (denied != null) return denied;
        try {
            List<TableInfo> tables = svc.listTables(path);
            return Response.ok(Map.of("tables", tables)).build();
        } catch (IllegalArgumentException e) {
            return bad(e.getMessage());
        } catch (Exception e) {
            LOG.errorf("listTables error at %s: %s", path, e.getMessage());
            return err(e.getMessage());
        }
    }

    @GET
    @Path("/table/data")
    public Response data(
            @QueryParam("path") String path,
            @QueryParam("table") String table,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {

        if (path == null || path.isBlank()) return bad("missing 'path' parameter");
        if (table == null || table.isBlank()) return bad("missing 'table' parameter");
        // Prevent path traversal
        if (table.contains("..") || table.contains("/") || table.contains("\\")) {
            return bad("invalid table name");
        }
        Response denied = validatePath(path);
        if (denied != null) return denied;

        page = Math.max(0, page);
        size = Math.min(1000, Math.max(1, size));

        String tablePath = path.endsWith("/") || path.endsWith("\\")
            ? path + table
            : path + "/" + table;

        try {
            TableData result = svc.query(tablePath, page, size);
            return Response.ok(result).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return bad(e.getMessage());
        } catch (Exception e) {
            LOG.errorf("query error for %s: %s", tablePath, e.getMessage());
            return err(e.getMessage());
        }
    }


    @POST
    @Path("/table/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response runQuery(Map<String, String> body) {
        String path = body.get("path");
        String sql  = body.get("sql");

        if (path == null || path.isBlank()) return bad("missing 'path' field");
        if (sql  == null || sql.isBlank())  return bad("missing 'sql' field");
        // The path here is the full tablePath (includes table name), validate against allowed prefixes
        Response denied = validatePath(path);
        if (denied != null) return denied;

        try {
            Map<String, Object> result = svc.runQuery(path, sql);
            return Response.ok(result).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return bad(e.getMessage());
        } catch (Exception e) {
            LOG.errorf("runQuery error: %s", e.getMessage());
            return err(e.getMessage());
        }
    }

    @GET
    @Path("/fs/cwd")
    public Map<String, String> cwd() {
        return Map.of("path", java.nio.file.Path.of(System.getProperty("user.dir")).toAbsolutePath().toString());
    }

    @GET
    @Path("/fs/browse")
    public Response browse(@QueryParam("path") String path) {
        if (path == null || path.isBlank()) {
            path = System.getProperty("user.dir");
        }
        Response denied = validatePath(path);
        if (denied != null) return denied;
        java.nio.file.Path dir = java.nio.file.Path.of(path).toAbsolutePath().normalize();
        if (!Files.isDirectory(dir)) {
            return bad("Not a directory: " + dir);
        }

        List<Map<String, String>> entries = new ArrayList<>();
        try (Stream<java.nio.file.Path> stream = Files.list(dir)) {
            stream
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing(p -> p.getFileName().toString(), String.CASE_INSENSITIVE_ORDER))
                .forEach(p -> entries.add(Map.of(
                    "name", p.getFileName().toString(),
                    "path", p.toString()
                )));
        } catch (IOException e) {
            return err("Cannot read directory: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("path", dir.toString());
        result.put("parent", dir.getParent() != null ? dir.getParent().toString() : null);
        result.put("dirs", entries);
        return Response.ok(result).build();
    }

    private static Response bad(String msg) {
        return Response.status(400).entity(Map.of("error", msg)).build();
    }

    private static Response err(String msg) {
        return Response.status(500).entity(Map.of("error", msg != null ? msg : "internal error")).build();
    }
}
