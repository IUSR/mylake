package com.mylake;

import com.mylake.model.ColumnInfo;
import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

@ApplicationScoped
public class DeltaLakeService {

    private static final Logger LOG = Logger.getLogger(DeltaLakeService.class);

    private Connection conn;
    private boolean ready = false;
    private String initError;

    @PostConstruct
    void init() {
        try {
            Class.forName("org.duckdb.DuckDBDriver");
            // Use a temp directory so each app restart gets a fresh DB (avoids stale lock files)
            Path dbDir = Path.of(System.getProperty("java.io.tmpdir"), "mylake_duck");
            Files.createDirectories(dbDir);
            Path dbFile = dbDir.resolve("db.duckdb");
            Files.deleteIfExists(dbFile);
            conn = DriverManager.getConnection("jdbc:duckdb:" + dbFile.toAbsolutePath());
            exec("INSTALL delta");
            exec("LOAD delta");
            ready = true;
            LOG.info("DuckDB ready with delta extension");
        } catch (Exception e) {
            initError = e.getMessage();
            LOG.errorf("DuckDB init failed: %s", e.getMessage());
        }
    }

    @PreDestroy
    void destroy() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ignored) {}
    }

    private void exec(String sql) throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute(sql);
        }
    }

    public boolean isReady() {
        return ready;
    }

    public String getInitError() {
        return initError;
    }

    /**
     * Lists all Delta tables (subdirectories containing _delta_log/) under the given path.
     */
    public List<TableInfo> listTables(String lakePath) throws IOException {
        Path root = Path.of(lakePath);
        if (!Files.exists(root)) {
            throw new IllegalArgumentException("Path does not exist: " + lakePath);
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Not a directory: " + lakePath);
        }
        try (Stream<Path> stream = Files.list(root)) {
            return stream
                .filter(Files::isDirectory)
                .filter(p -> Files.isDirectory(p.resolve("_delta_log")))
                .map(p -> new TableInfo(p.getFileName().toString(), p.toAbsolutePath().toString()))
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toList();
        }
    }

    /**
     * Queries a Delta table with pagination. Uses DuckDB delta_scan().
     */
    public synchronized TableData query(String tablePath, int page, int pageSize) throws SQLException {
        if (!ready) {
            throw new IllegalStateException("Delta extension not ready" + (initError != null ? ": " + initError : ""));
        }
        if (tablePath.contains("'") || tablePath.contains(";")) {
            throw new IllegalArgumentException("Path contains invalid characters");
        }

        // Escape backslashes for DuckDB string literal
        String escaped = tablePath.replace("\\", "\\\\");

        // Count total rows (DuckDB uses Delta stats when available, usually fast)
        long total;
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM delta_scan('" + escaped + "')")) {
            rs.next();
            total = rs.getLong(1);
        }

        int offset = page * pageSize;
        List<ColumnInfo> cols = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT * FROM delta_scan('" + escaped + "') LIMIT " + pageSize + " OFFSET " + offset)) {

            ResultSetMetaData meta = rs.getMetaData();
            int n = meta.getColumnCount();
            for (int i = 1; i <= n; i++) {
                cols.add(new ColumnInfo(meta.getColumnName(i), meta.getColumnTypeName(i)));
            }
            while (rs.next()) {
                List<Object> row = new ArrayList<>(n);
                for (int i = 1; i <= n; i++) {
                    Object v = rs.getObject(i);
                    // Keep numbers and booleans as-is; convert everything else to String
                    row.add(v == null ? null : (v instanceof Number || v instanceof Boolean ? v : v.toString()));
                }
                rows.add(row);
            }
        }

        int totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 1;
        return new TableData(cols, rows, total, page, pageSize, totalPages);
    }

    /**
     * Runs a user-supplied SELECT query (replacing the placeholder __table__ with
     * a delta_scan() call) and returns columns + rows.  Only SELECT statements are allowed.
     */
    public synchronized Map<String, Object> runQuery(String tablePath, String sql) throws SQLException {
        if (!ready) {
            throw new IllegalStateException("Delta extension not ready" + (initError != null ? ": " + initError : ""));
        }
        if (tablePath.contains("'") || tablePath.contains(";")) {
            throw new IllegalArgumentException("Path contains invalid characters");
        }
        // Rudimentary guard: only allow SELECT
        String trimmed = sql.trim();
        if (!trimmed.toUpperCase().startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT statements are allowed");
        }

        String escaped = tablePath.replace("\\", "\\\\");
        // Replace the placeholder token with a delta_scan reference so users can write
        // "SELECT … FROM tbl" while tbl is substituted at runtime.
        String rewritten = trimmed.replaceAll("(?i)\\bFROM\\s+tbl\\b",
                                              "FROM delta_scan('" + escaped + "')");
        // If no substitution happened and no delta_scan present, inject FROM clause
        if (!rewritten.contains("delta_scan")) {
            rewritten = trimmed;
        }

        List<Map<String, Object>> columns = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(rewritten)) {
            ResultSetMetaData meta = rs.getMetaData();
            int n = meta.getColumnCount();
            for (int i = 1; i <= n; i++) {
                Map<String, Object> col = new LinkedHashMap<>();
                col.put("name", meta.getColumnName(i));
                col.put("type", meta.getColumnTypeName(i));
                columns.add(col);
            }
            int limit = 1000;
            while (rs.next() && rows.size() < limit) {
                List<Object> row = new ArrayList<>(n);
                for (int i = 1; i <= n; i++) {
                    Object v = rs.getObject(i);
                    row.add(v == null ? null : (v instanceof Number || v instanceof Boolean ? v : v.toString()));
                }
                rows.add(row);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("columns", columns);
        result.put("rows", rows);
        result.put("rowCount", rows.size());
        return result;
    }

    /**
     * Reads the Delta _delta_log to extract table UUID, partition columns,
     * protocol version, and table configuration properties.
     */
    public Map<String, Object> tableProperties(String tablePath) throws IOException {
        Path logDir = Path.of(tablePath, "_delta_log");
        if (!Files.isDirectory(logDir)) {
            throw new IllegalArgumentException("No _delta_log directory found at: " + tablePath);
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = new LinkedHashMap<>();

        // Defaults
        result.put("id", null);
        result.put("name", null);
        result.put("description", null);
        result.put("partitionColumns", List.of());
        result.put("readerVersion", null);
        result.put("writerVersion", null);
        result.put("configuration", Map.of());

        // Read JSON log files in order; latest values win
        try (Stream<Path> files = Files.list(logDir)) {
            List<Path> jsonFiles = files
                .filter(p -> p.getFileName().toString().endsWith(".json"))
                .sorted()
                .toList();

            for (Path logFile : jsonFiles) {
                List<String> lines = Files.readAllLines(logFile);
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    try {
                        JsonNode root = mapper.readTree(line);

                        // protocol action
                        if (root.has("protocol")) {
                            JsonNode p = root.get("protocol");
                            if (p.has("minReaderVersion")) result.put("readerVersion", p.get("minReaderVersion").asInt());
                            if (p.has("minWriterVersion")) result.put("writerVersion", p.get("minWriterVersion").asInt());
                        }

                        // metaData action
                        if (root.has("metaData")) {
                            JsonNode m = root.get("metaData");
                            if (m.has("id"))          result.put("id", m.get("id").asText());
                            if (m.has("name") && !m.get("name").isNull()) result.put("name", m.get("name").asText());
                            if (m.has("description") && !m.get("description").isNull()) result.put("description", m.get("description").asText());
                            if (m.has("partitionColumns")) {
                                List<String> parts = new ArrayList<>();
                                m.get("partitionColumns").forEach(n -> parts.add(n.asText()));
                                result.put("partitionColumns", parts);
                            }
                            if (m.has("configuration")) {
                                Map<String, String> config = new TreeMap<>();
                                m.get("configuration").fields().forEachRemaining(e -> config.put(e.getKey(), e.getValue().asText()));
                                result.put("configuration", config);
                            }
                        }
                    } catch (Exception ignored) {
                        // Skip malformed lines
                    }
                }
            }
        }
        return result;
    }
}