package com.giadinh.apporderbill.orders.usecase.dto;

import java.util.List;

public class DeleteOrderItemOutput {
    private final List<OrderItemOutput> items;
    private final long totalAmount;

    public DeleteOrderItemOutput(List<OrderItemOutput> items, long totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<OrderItemOutput> getItems() { return items; }
    public long getTotalAmount() { return totalAmount; }
}

