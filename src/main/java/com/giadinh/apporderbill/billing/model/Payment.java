package com.giadinh.apporderbill.billing.model;

import java.time.LocalDateTime;

public class Payment {
    private Long paymentId;
    private String orderId;
    private long totalAmount;
    private long finalAmount;
    private long paidAmount;
    private String paymentMethod;
    private Long discountAmount;
    private Double discountPercent;
    private String cashier;
    private LocalDateTime paidAt;
    private Long customerId;

    public Payment(String orderId,
            long totalAmount,
            long finalAmount,
            long paidAmount,
            String paymentMethod,
            Long discountAmount,
            Double discountPercent,
            String cashier,
            Long customerId) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
        this.customerId = customerId;
        this.paidAt = LocalDateTime.now();
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public long getFinalAmount() {
        return finalAmount;
    }

    public long getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public String getCashier() {
        return cashier;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
