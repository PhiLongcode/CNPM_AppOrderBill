package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderOutput;

public class CancelOrderUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public CancelOrderUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public CancelOrderOutput execute(CancelOrderInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return new CancelOrderOutput(true);
    }
}

