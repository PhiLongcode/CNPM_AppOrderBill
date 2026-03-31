package com.giadinh.apporderbill.billing.usecase.dto;

public class PrintReceiptOutput {
    private final boolean success;
    private final String message;

    public PrintReceiptOutput(boolean success, String message) {
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
