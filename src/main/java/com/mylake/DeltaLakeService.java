package com.mylake;

import com.mylake.model.ColumnInfo;
import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
            throw new IllegalArgumentException("路径不存在: " + lakePath);
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("不是目录: " + lakePath);
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
            throw new IllegalStateException("Delta 扩展未就绪" + (initError != null ? ": " + initError : ""));
        }
        if (tablePath.contains("'") || tablePath.contains(";")) {
            throw new IllegalArgumentException("路径包含非法字符");
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
}
