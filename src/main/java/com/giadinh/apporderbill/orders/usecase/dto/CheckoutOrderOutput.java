package com.giadinh.apporderbill.orders.usecase.dto;

public class CheckoutOrderOutput {
    private final Long paymentId;
    private final Long orderId;

    public CheckoutOrderOutput(Long paymentId, Long orderId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
