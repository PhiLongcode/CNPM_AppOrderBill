package com.giadinh.apporderbill.orders.usecase.dto;

public class CalculateOrderTotalInput {
    private final Long orderId;
    private final long discountAmount;
    private final Double discountPercent;

    public CalculateOrderTotalInput(Long orderId, long discountAmount, Double discountPercent) {
        this.orderId = orderId;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
    }

    public Long getOrderId() { return orderId; }
    public long getDiscountAmount() { return discountAmount; }
    public Double getDiscountPercent() { return discountPercent; }
}

