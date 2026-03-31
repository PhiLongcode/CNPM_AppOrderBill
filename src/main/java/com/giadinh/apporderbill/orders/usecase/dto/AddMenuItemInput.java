package com.giadinh.apporderbill.orders.usecase.dto;

public class AddMenuItemInput {
    private final Long orderId;
    private final Long menuItemId;
    private final int quantity;
    private final String notes;

    public AddMenuItemInput(Long orderId, Long menuItemId, int quantity, String notes) {
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.notes = notes;
    }

    public Long getOrderId() { return orderId; }
    public Long getMenuItemId() { return menuItemId; }
    public int getQuantity() { return quantity; }
    public String getNotes() { return notes; }
}

