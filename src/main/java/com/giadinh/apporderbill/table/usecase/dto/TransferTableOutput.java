package com.giadinh.apporderbill.table.usecase.dto;

public class TransferTableOutput {
    private boolean success;
    private String message;

    public TransferTableOutput(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
