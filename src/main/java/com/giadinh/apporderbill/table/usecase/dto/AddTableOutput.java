package com.giadinh.apporderbill.table.usecase.dto;

public class AddTableOutput {
    private final boolean success;
    private final String message;
    private final String tableId;

    public AddTableOutput(boolean success, String message, String tableId) {
        this.success = success;
        this.message = message;
        this.tableId = tableId;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getTableId() { return tableId; }
}

