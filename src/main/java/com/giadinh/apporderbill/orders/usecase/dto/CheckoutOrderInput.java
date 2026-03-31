package com.giadinh.apporderbill.orders.usecase.dto;

public class CheckoutOrderInput {
    private Long orderId;
    private long paidAmount;
    private String paymentMethod;
    private Long discountAmount;
    private Double discountPercent;
    private String cashier;

    public CheckoutOrderInput(Long orderId,
            long paidAmount,
            String paymentMethod,
            Long discountAmount,
            Double discountPercent,
            String cashier) {
        this.orderId = orderId;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
    }

    public Long getOrderId() {
        return orderId;
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
}
