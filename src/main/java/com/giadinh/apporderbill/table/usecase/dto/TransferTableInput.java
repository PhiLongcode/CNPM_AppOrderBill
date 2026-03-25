package com.giadinh.apporderbill.table.usecase.dto;

public class TransferTableInput {
    private String oldTableId;
    private String newTableId;

    public TransferTableInput(String oldTableId, String newTableId) {
        this.oldTableId = oldTableId;
        this.newTableId = newTableId;
    }

    public String getOldTableId() {
        return oldTableId;
    }

    public String getNewTableId() {
        return newTableId;
    }
}
