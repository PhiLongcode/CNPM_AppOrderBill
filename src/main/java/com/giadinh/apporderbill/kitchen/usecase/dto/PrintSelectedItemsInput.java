package com.giadinh.apporderbill.kitchen.usecase.dto;

import java.util.List;

public class PrintSelectedItemsInput {
    private final Long orderId;
    private final List<Long> orderItemIds;

    public PrintSelectedItemsInput(Long orderId, List<Long> orderItemIds) {
        this.orderId = orderId;
        this.orderItemIds = orderItemIds;
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<Long> getOrderItemIds() {
        return orderItemIds;
    }
}

