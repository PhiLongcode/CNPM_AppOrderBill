package com.giadinh.apporderbill.orders.usecase.dto;

public class CancelOrderOutput {
    private final boolean success;

    public CancelOrderOutput(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

