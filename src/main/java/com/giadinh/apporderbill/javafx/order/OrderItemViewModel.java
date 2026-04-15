package com.giadinh.apporderbill.javafx.order;

public class OrderItemViewModel {
    private final Long orderItemId;
    private final String name;
    private final int quantity;
    private final long unitPrice;
    private final long totalPrice;
    private final String notes;
    private final String unitName;
    private final boolean printedToKitchen;
    private final boolean canceled;
    private final Double discountPercent;
    private final Double discountAmount;
    private boolean selected;

    public OrderItemViewModel(Long orderItemId, String name, int quantity, long unitPrice, long totalPrice,
            String notes, String unitName, boolean printedToKitchen, boolean canceled, Double discountPercent, Double discountAmount) {
        this.orderItemId = orderItemId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.notes = notes;
        this.unitName = unitName;
        this.printedToKitchen = printedToKitchen;
        this.canceled = canceled;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.selected = false;
    }

    public Long getOrderItemId() { return orderItemId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public long getUnitPrice() { return unitPrice; }
    public long getTotalPrice() { return totalPrice; }
    public String getNotes() { return notes; }
    public String getUnitName() { return unitName; }
    public boolean isPrintedToKitchen() { return printedToKitchen; }
    public boolean isCanceled() { return canceled; }
    public Double getDiscountPercent() { return discountPercent; }
    public Double getDiscountAmount() { return discountAmount; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
