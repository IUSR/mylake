package com.mylake.model;

public record VersionEntry(
    long version,
    String timestamp,
    String operation,
    String operationParameters,
    Long numFiles,
    Long numOutputRows,
    Long numRemovedFiles
) {}
