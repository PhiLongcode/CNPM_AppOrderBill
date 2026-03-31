package com.giadinh.apporderbill.orders.usecase.dto;

public class DeleteOrderItemInput {
    private final Long orderId;
    private final Long orderItemId;

    public DeleteOrderItemInput(Long orderId, Long orderItemId) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
    }

    public Long getOrderId() { return orderId; }
    public Long getOrderItemId() { return orderItemId; }
}

