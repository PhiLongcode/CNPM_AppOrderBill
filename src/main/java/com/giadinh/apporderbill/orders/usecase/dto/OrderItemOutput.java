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
    private final Double discountAmount;

    public OrderItemOutput(Long orderItemId, String itemName, int quantity, long unitPrice,
            String notes, String unitName, boolean printedToKitchen, boolean canceled, Double discountPercent, Double discountAmount) {
        this.orderItemId = orderItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.notes = notes;
        this.unitName = unitName;
        this.printedToKitchen = printedToKitchen;
        this.canceled = canceled;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        
        double appliedDiscountAmount = discountAmount != null ? discountAmount : 0.0;
        double appliedDiscountPercent = discountPercent != null ? discountPercent : 0.0;
        this.lineTotal = Math.round((quantity * unitPrice - appliedDiscountAmount) * (1.0 - appliedDiscountPercent / 100.0));
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
    public Double getDiscountAmount() { return discountAmount; }
}

