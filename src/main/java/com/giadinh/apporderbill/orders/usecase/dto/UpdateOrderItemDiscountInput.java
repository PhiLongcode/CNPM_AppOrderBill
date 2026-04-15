package com.giadinh.apporderbill.orders.usecase.dto;

public class UpdateOrderItemDiscountInput {
    private final Long orderId;
    private final Long orderItemId;
    private final Double discountPercent;
    private final Double discountAmount;

    public UpdateOrderItemDiscountInput(Long orderId, Long orderItemId, Double discountPercent, Double discountAmount) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
    }

    public Long getOrderId() { return orderId; }
    public Long getOrderItemId() { return orderItemId; }
    public Double getDiscountPercent() { return discountPercent; }
    public Double getDiscountAmount() { return discountAmount; }
}

