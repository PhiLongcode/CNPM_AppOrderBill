package com.giadinh.apporderbill.orders.usecase.dto;

public class UpdateOrderItemQuantityInput {
    private final Long orderId;
    private final Long orderItemId;
    private final int newQuantity;

    public UpdateOrderItemQuantityInput(Long orderId, Long orderItemId, int newQuantity) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.newQuantity = newQuantity;
    }

    public Long getOrderId() { return orderId; }
    public Long getOrderItemId() { return orderItemId; }
    public int getNewQuantity() { return newQuantity; }
}

