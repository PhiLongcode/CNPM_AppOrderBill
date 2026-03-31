package com.giadinh.apporderbill.orders.usecase.dto;

public class CalculateOrderTotalOutput {
    private final long subtotal;
    private final long discountAmount;
    private final long finalAmount;

    public CalculateOrderTotalOutput(long subtotal, long discountAmount, long finalAmount) {
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public long getSubtotal() { return subtotal; }
    public long getDiscountAmount() { return discountAmount; }
    public long getFinalAmount() { return finalAmount; }
}

