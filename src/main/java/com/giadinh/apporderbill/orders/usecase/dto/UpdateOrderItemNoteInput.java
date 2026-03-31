package com.giadinh.apporderbill.orders.usecase.dto;

public class UpdateOrderItemNoteInput {
    private final Long orderId;
    private final Long orderItemId;
    private final String note;

    public UpdateOrderItemNoteInput(Long orderId, Long orderItemId, String note) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.note = note;
    }

    public Long getOrderId() { return orderId; }
    public Long getOrderItemId() { return orderItemId; }
    public String getNote() { return note; }
}

