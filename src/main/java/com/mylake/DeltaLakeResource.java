package com.mylake;

import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
