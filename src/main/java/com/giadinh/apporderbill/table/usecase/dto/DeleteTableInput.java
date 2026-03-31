package com.giadinh.apporderbill.table.usecase.dto;

public class DeleteTableInput {
    private final String tableId;

    public DeleteTableInput(String tableId) {
        this.tableId = tableId;
    }

    public String getTableId() {
        return tableId;
    }
}

