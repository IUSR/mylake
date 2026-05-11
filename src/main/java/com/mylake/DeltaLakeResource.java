package com.mylake;

import com.mylake.model.ColumnInfo;
import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.PrintWriter;
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

    @ConfigProperty(name = "mylake.export.max-rows", defaultValue = "500000")
    int exportMaxRows;

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
    @Path("/table/export")
    @Produces(MediaType.WILDCARD)
    public Response export(
            @QueryParam("path") String path,
            @QueryParam("table") String table,
            @QueryParam("format") @DefaultValue("csv") String format) {

        if (path == null || path.isBlank()) return bad("missing 'path' parameter");
        if (table == null || table.isBlank()) return bad("missing 'table' parameter");
        if (table.contains("..") || table.contains("/") || table.contains("\\")) {
            return bad("invalid table name");
        }
        boolean isCsv = !"json".equalsIgnoreCase(format);
        String ext = isCsv ? "csv" : "json";
        String contentType = isCsv ? "text/csv" : "application/json";

        String tablePath = path.endsWith("/") || path.endsWith("\\")
            ? path + table
            : path + "/" + table;

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = svc.exportAll(tablePath, exportMaxRows);
            @SuppressWarnings("unchecked")
            List<ColumnInfo> cols = (List<ColumnInfo>) data.get("columns");
            @SuppressWarnings("unchecked")
            List<List<Object>> rows = (List<List<Object>>) data.get("rows");

            StreamingOutput stream;
            if (isCsv) {
                stream = os -> {
                    try (PrintWriter w = new PrintWriter(os)) {
                        // Header
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < cols.size(); i++) {
                            if (i > 0) sb.append(',');
                            sb.append(csvQuote(cols.get(i).name()));
                        }
                        w.println(sb);
                        // Rows
                        for (List<Object> row : rows) {
                            sb.setLength(0);
                            for (int i = 0; i < row.size(); i++) {
                                if (i > 0) sb.append(',');
                                sb.append(csvQuote(row.get(i)));
                            }
                            w.println(sb);
                        }
                    }
                };
            } else {
                // JSON array of objects
                stream = os -> {
                    try (PrintWriter w = new PrintWriter(os)) {
                        w.print('[');
                        for (int r = 0; r < rows.size(); r++) {
                            if (r > 0) w.print(',');
                            List<Object> row = rows.get(r);
                            w.print('{');
                            for (int i = 0; i < cols.size(); i++) {
                                if (i > 0) w.print(',');
                                w.print('"');
                                w.print(cols.get(i).name().replace("\"", "\\\""));
                                w.print("\":");
                                Object v = row.get(i);
                                if (v == null) {
                                    w.print("null");
                                } else if (v instanceof Number || v instanceof Boolean) {
                                    w.print(v);
                                } else {
                                    w.print('"');
                                    w.print(v.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"));
                                    w.print('"');
                                }
                            }
                            w.print('}');
                        }
                        w.print(']');
                    }
                };
            }

            return Response.ok(stream)
                .header("Content-Type", contentType + ";charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"" + table + "-full." + ext + "\"")
                .build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return bad(e.getMessage());
        } catch (Exception e) {
            LOG.errorf("export error for %s: %s", tablePath, e.getMessage());
            return err(e.getMessage());
        }
    }

    /** RFC 4180 CSV quoting */
    private static String csvQuote(Object v) {
        if (v == null) return "";
        String s = String.valueOf(v);
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\r') >= 0 || s.indexOf('\n') >= 0) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    @POST
    @Path("/table/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response runQuery(Map<String, String> body) {
        String path = body.get("path");
        String sql  = body.get("sql");

        if (path == null || path.isBlank()) return bad("missing 'path' field");
        if (sql  == null || sql.isBlank())  return bad("missing 'sql' field");

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
