package com.giadinh.apporderbill.orders.usecase.dto;

import java.util.List;

public class UpdateOrderItemNoteOutput {
    private final List<OrderItemOutput> items;
    private final long totalAmount;

    public UpdateOrderItemNoteOutput(List<OrderItemOutput> items, long totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<OrderItemOutput> getItems() { return items; }
    public long getTotalAmount() { return totalAmount; }
}

