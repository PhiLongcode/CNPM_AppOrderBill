package com.giadinh.apporderbill.orders.usecase.dto;

public class AddCustomItemInput {
    private final Long orderId;
    private final String itemName;
    private final int quantity;
    private final long unitPrice;
    private final String notes;

    public AddCustomItemInput(Long orderId, String itemName, int quantity, long unitPrice, String notes) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.notes = notes;
    }

    public Long getOrderId() { return orderId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public long getUnitPrice() { return unitPrice; }
    public String getNotes() { return notes; }
}

