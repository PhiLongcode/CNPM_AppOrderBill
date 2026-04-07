package com.giadinh.apporderbill.table.usecase.dto;

public class AddTableOutput {
    private final String tableId;

    public AddTableOutput(String tableId) {
        this.tableId = tableId;
    }

    public String getTableId() {
        return tableId;
    }
}
