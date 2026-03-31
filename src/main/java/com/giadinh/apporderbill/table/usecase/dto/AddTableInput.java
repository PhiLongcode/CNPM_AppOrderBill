package com.giadinh.apporderbill.table.usecase.dto;

public class AddTableInput {
    private final String tableName;

    public AddTableInput(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}

