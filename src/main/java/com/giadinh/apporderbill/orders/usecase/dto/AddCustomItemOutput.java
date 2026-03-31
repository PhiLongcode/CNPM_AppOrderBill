package com.giadinh.apporderbill.orders.usecase.dto;

import java.util.List;

public class AddCustomItemOutput {
    private final String tableNumber;
    private final List<OrderItemOutput> items;
    private final long totalAmount;

    public AddCustomItemOutput(String tableNumber, List<OrderItemOutput> items, long totalAmount) {
        this.tableNumber = tableNumber;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public String getTableNumber() { return tableNumber; }
    public List<OrderItemOutput> getItems() { return items; }
    public long getTotalAmount() { return totalAmount; }
}

