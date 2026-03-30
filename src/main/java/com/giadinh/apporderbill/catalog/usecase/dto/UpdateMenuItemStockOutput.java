package com.giadinh.apporderbill.catalog.usecase.dto;

public class UpdateMenuItemStockOutput {
    private boolean success;
    private String message;
    private int currentStockQuantity;

    public UpdateMenuItemStockOutput(boolean success, String message, int currentStockQuantity) {
        this.success = success;
        this.message = message;
        this.currentStockQuantity = currentStockQuantity;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCurrentStockQuantity() {
        return currentStockQuantity;
    }
}
