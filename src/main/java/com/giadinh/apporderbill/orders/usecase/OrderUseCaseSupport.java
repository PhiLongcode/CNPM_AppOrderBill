package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.orders.usecase.dto.OrderItemOutput;

import java.util.ArrayList;
import java.util.List;

final class OrderUseCaseSupport {
    private OrderUseCaseSupport() {}

    static long parseOrderId(Long id) {
        return id == null ? 0L : id;
    }

    static List<OrderItemOutput> toOutputs(Order order) {
        List<OrderItemOutput> items = new ArrayList<>();
        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem item = order.getItems().get(i);
            long outputId = i + 1L;
            items.add(new OrderItemOutput(
                    outputId,
                    item.getMenuItemName(),
                    item.getQuantity(),
                    Math.round(item.getPrice()),
                    item.getNote(),
                    "unit",
                    false,
                    false,
                    0.0));
        }
        return items;
    }

    static long total(Order order) {
        return Math.round(order.getTotalAmount());
    }
}

