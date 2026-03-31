package com.giadinh.apporderbill.table.usecase.dto;

public class ClearTableInput {
    private final String tableId;

    public ClearTableInput(String tableId) {
        this.tableId = tableId;
    }

    public String getTableId() {
        return tableId;
    }
}

