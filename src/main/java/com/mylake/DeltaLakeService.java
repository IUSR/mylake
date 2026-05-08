package com.mylake;

import com.mylake.model.ColumnInfo;
import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import com.mylake.model.VersionEntry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * Reads the Delta transaction log for a table and returns a list of version entries,
     * sorted newest-first. Parses the JSON commit files in _delta_log/.
     */
    public List<VersionEntry> listVersions(String tablePath) throws IOException {
        Path logDir = Path.of(tablePath, "_delta_log");
        if (!Files.isDirectory(logDir)) {
            throw new IllegalArgumentException("Not a Delta table (no _delta_log): " + tablePath);
        }

        List<VersionEntry> entries = new ArrayList<>();

        // Pattern to match commit JSON files: 00000000000000000000.json
        Pattern jsonPat = Pattern.compile("^(\\d+)\\.json$");

        try (Stream<Path> files = Files.list(logDir)) {
            List<Path> commitFiles = files
                .filter(p -> jsonPat.matcher(p.getFileName().toString()).matches())
                .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                .toList();

            for (Path commitFile : commitFiles) {
                Matcher m = jsonPat.matcher(commitFile.getFileName().toString());
                if (!m.matches()) continue;
                long version = Long.parseLong(m.group(1));

                String json = Files.readString(commitFile, StandardCharsets.UTF_8);
                VersionEntry entry = parseCommitJson(version, json);
                entries.add(entry);
            }
        }

        // Return newest first
        entries.sort((a, b) -> Long.compare(b.version(), a.version()));
        return entries;
    }

    /**
     * Queries a Delta table at a specific version using DuckDB's delta_scan with version parameter.
     */
    public synchronized TableData queryAtVersion(String tablePath, long version, int page, int pageSize) throws SQLException {
        if (!ready) {
            throw new IllegalStateException("Delta extension not ready" + (initError != null ? ": " + initError : ""));
        }
        if (tablePath.contains("'") || tablePath.contains(";")) {
            throw new IllegalArgumentException("Path contains invalid characters");
        }

        String escaped = tablePath.replace("\\", "\\\\");

        long total;
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT COUNT(*) FROM delta_scan('" + escaped + "', version=" + version + ")")) {
            rs.next();
            total = rs.getLong(1);
        }

        int offset = page * pageSize;
        List<ColumnInfo> cols = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT * FROM delta_scan('" + escaped + "', version=" + version + ") LIMIT " + pageSize + " OFFSET " + offset)) {

            ResultSetMetaData meta = rs.getMetaData();
            int n = meta.getColumnCount();
            for (int i = 1; i <= n; i++) {
                cols.add(new ColumnInfo(meta.getColumnName(i), meta.getColumnTypeName(i)));
            }
            while (rs.next()) {
                List<Object> row = new ArrayList<>(n);
                for (int i = 1; i <= n; i++) {
                    Object v = rs.getObject(i);
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

    // ── Delta log JSON parsing ─────────────────────────────────────────────────

    private VersionEntry parseCommitJson(long version, String json) {
        String timestamp = extractJsonString(json, "timestamp");
        String operation = extractNestedString(json, "commitInfo", "operation");
        String operationParameters = extractNestedString(json, "commitInfo", "operationParameters");

        Long numFiles = extractLong(json, "numFiles");
        Long numOutputRows = extractLong(json, "numOutputRows");
        Long numRemovedFiles = extractLong(json, "numRemovedFiles");

        String formattedTs = timestamp;
        if (timestamp != null && timestamp.matches("\\d+")) {
            long epochMs = Long.parseLong(timestamp);
            formattedTs = java.time.Instant.ofEpochMilli(epochMs)
                .toString()
                .replace("T", " ")
                .replace("Z", " UTC");
        }

        return new VersionEntry(version, formattedTs, operation, operationParameters,
                                numFiles, numOutputRows, numRemovedFiles);
    }

    private String extractJsonString(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"\\\\]*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private String extractNestedString(String json, String parent, String key) {
        Pattern blockP = Pattern.compile("\"" + Pattern.quote(parent) + "\"\\s*:\\s*\\{([^}]*(?:\\{[^}]*\\}[^}]*)*)\\}");
        Matcher blockM = blockP.matcher(json);
        if (blockM.find()) {
            return extractJsonString(blockM.group(1), key);
        }
        return null;
    }

    private Long extractLong(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        return m.find() ? Long.parseLong(m.group(1)) : null;
    }
}
