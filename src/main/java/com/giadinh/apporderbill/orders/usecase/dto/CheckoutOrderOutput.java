package com.giadinh.apporderbill.orders.usecase.dto;

public class CheckoutOrderOutput {
    private final boolean success;
    private final String message;
    private final Long paymentId;
    private final Long orderId;

    public CheckoutOrderOutput(boolean success, String message, Long paymentId, Long orderId) {
        this.success = success;
        this.message = message;
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
