package com.giadinh.apporderbill.orders.usecase.dto;

public class UpdateOrderItemDiscountInput {
    private final Long orderId;
    private final Long orderItemId;
    private final double discountPercent;

    public UpdateOrderItemDiscountInput(Long orderId, Long orderItemId, double discountPercent) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.discountPercent = discountPercent;
    }

    public Long getOrderId() { return orderId; }
    public Long getOrderItemId() { return orderItemId; }
    public double getDiscountPercent() { return discountPercent; }
}

