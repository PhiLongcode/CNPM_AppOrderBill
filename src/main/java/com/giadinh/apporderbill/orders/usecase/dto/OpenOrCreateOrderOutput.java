package com.giadinh.apporderbill.orders.usecase.dto;

import java.util.List;

public class OpenOrCreateOrderOutput {
    private final Long orderId;
    private final String tableNumber;
    private final List<OrderItemOutput> items;
    private final long totalAmount;

    public OpenOrCreateOrderOutput(Long orderId, String tableNumber, List<OrderItemOutput> items, long totalAmount) {
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() { return orderId; }
    public String getTableNumber() { return tableNumber; }
    public List<OrderItemOutput> getItems() { return items; }
    public long getTotalAmount() { return totalAmount; }
}

