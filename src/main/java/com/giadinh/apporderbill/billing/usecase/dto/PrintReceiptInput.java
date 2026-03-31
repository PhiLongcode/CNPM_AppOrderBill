package com.giadinh.apporderbill.billing.usecase.dto;

public class PrintReceiptInput {
    private final Long paymentId;
    private final Long orderId;

    public PrintReceiptInput(Long paymentId, Long orderId) {
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
