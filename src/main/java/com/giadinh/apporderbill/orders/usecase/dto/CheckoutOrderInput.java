package com.giadinh.apporderbill.orders.usecase.dto;

public class CheckoutOrderInput {
    private String orderId;
    private double amountPaid;

    public CheckoutOrderInput(String orderId, double amountPaid) {
        this.orderId = orderId;
        this.amountPaid = amountPaid;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }
}
