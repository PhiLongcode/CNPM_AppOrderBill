package com.giadinh.apporderbill.billing.usecase.dto;

import java.time.LocalDateTime;

public class PaymentDetailOutput {
    private final Long paymentId;
    private final String orderId;
    private final String tableNumber;
    private final long totalAmount;
    private final long finalAmount;
    private final long paidAmount;
    private final long changeAmount;
    private final String paymentMethod;
    private final Long discountAmount;
    private final Double discountPercent;
    private final String cashier;
    private final LocalDateTime paidAt;

    public PaymentDetailOutput(Long paymentId, String orderId, String tableNumber,
            long totalAmount, long finalAmount, long paidAmount,
            String paymentMethod, Long discountAmount, Double discountPercent,
            String cashier, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.paidAmount = paidAmount;
        this.changeAmount = paidAmount - finalAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
        this.paidAt = paidAt;
    }

    public Long getPaymentId() { return paymentId; }
    public String getOrderId() { return orderId; }
    public String getTableNumber() { return tableNumber; }
    public long getTotalAmount() { return totalAmount; }
    public long getFinalAmount() { return finalAmount; }
    public long getPaidAmount() { return paidAmount; }
    public long getChangeAmount() { return changeAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public Long getDiscountAmount() { return discountAmount; }
    public Double getDiscountPercent() { return discountPercent; }
    public String getCashier() { return cashier; }
    public LocalDateTime getPaidAt() { return paidAt; }
}

