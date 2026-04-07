package com.giadinh.apporderbill.catalog.usecase.dto;

public class UpdateMenuItemStockOutput {
    private final int currentStockQuantity;

    public UpdateMenuItemStockOutput(int currentStockQuantity) {
        this.currentStockQuantity = currentStockQuantity;
    }

    public int getCurrentStockQuantity() {
        return currentStockQuantity;
    }
}
