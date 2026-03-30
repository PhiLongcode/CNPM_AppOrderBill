package com.giadinh.apporderbill.catalog.usecase.dto;

public class UpdateMenuItemStockInput {
    public enum StockOperation {
        INCREASE, DECREASE
    }

    private StockOperation operation;
    private int quantity;

    public UpdateMenuItemStockInput(StockOperation operation, int quantity) {
        this.operation = operation;
        this.quantity = quantity;
    }

    public StockOperation getOperation() {
        return operation;
    }

    public int getQuantity() {
        return quantity;
    }
}
