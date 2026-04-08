package com.mylake.model;

import java.util.List;

public record TableData(
    List<ColumnInfo> columns,
    List<List<Object>> rows,
    long totalRows,
    int page,
    int pageSize,
    int totalPages
) {}
