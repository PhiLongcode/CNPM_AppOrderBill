package com.giadinh.apporderbill.orders.usecase.dto;

public class OrderItemOutput {
    private final Long orderItemId;
    private final String itemName;
    private final int quantity;
    private final long unitPrice;
    private final long lineTotal;
    private final String notes;
    private final String unitName;
    private final boolean printedToKitchen;
    private final boolean canceled;
    private final Double discountPercent;

    public OrderItemOutput(Long orderItemId, String itemName, int quantity, long unitPrice,
            String notes, String unitName, boolean printedToKitchen, boolean canceled, Double discountPercent) {
        this.orderItemId = orderItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = Math.round(quantity * unitPrice * (1.0 - (discountPercent == null ? 0.0 : discountPercent) / 100.0));
        this.notes = notes;
        this.unitName = unitName;
        this.printedToKitchen = printedToKitchen;
        this.canceled = canceled;
        this.discountPercent = discountPercent;
    }

    public Long getOrderItemId() { return orderItemId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public long getUnitPrice() { return unitPrice; }
    public long getLineTotal() { return lineTotal; }
    public String getNotes() { return notes; }
    public String getUnitName() { return unitName; }
    public boolean isPrintedToKitchen() { return printedToKitchen; }
    public boolean isCanceled() { return canceled; }
    public Double getDiscountPercent() { return discountPercent; }
}

