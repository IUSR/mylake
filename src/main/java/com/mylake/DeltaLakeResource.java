package com.mylake;

import com.mylake.model.TableData;
import com.mylake.model.TableInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

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
        if (path == null || path.isBlank()) return bad("缺少 path 参数");
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

        if (path == null || path.isBlank()) return bad("缺少 path 参数");
        if (table == null || table.isBlank()) return bad("缺少 table 参数");
        // Prevent path traversal
        if (table.contains("..") || table.contains("/") || table.contains("\\")) {
            return bad("非法的表名");
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

    private static Response bad(String msg) {
        return Response.status(400).entity(Map.of("error", msg)).build();
    }

    private static Response err(String msg) {
        return Response.status(500).entity(Map.of("error", msg != null ? msg : "内部错误")).build();
    }
}
