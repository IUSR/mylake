package com.mylake.model;

public record ColumnStats(
    String name,
    String type,
    long nullCount,
    Object min,
    Object max,
    long distinctCount
) {}
