package com.giadinh.apporderbill.orders.usecase.dto;

public class GetOrderDetailsInput {
    private final Long orderId;

    public GetOrderDetailsInput(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}

