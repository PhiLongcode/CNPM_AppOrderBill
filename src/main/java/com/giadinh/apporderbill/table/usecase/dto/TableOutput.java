package com.giadinh.apporderbill.table.usecase.dto;

public class TableOutput {
    private final String tableId;
    private final String tableName;
    private final String status;
    private final String currentOrderId;

    public TableOutput(String tableId, String tableName, String status, String currentOrderId) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.status = status;
        this.currentOrderId = currentOrderId;
    }

    public String getTableId() { return tableId; }
    public String getTableName() { return tableName; }
    public String getStatus() { return status; }
    public String getCurrentOrderId() { return currentOrderId; }
}

