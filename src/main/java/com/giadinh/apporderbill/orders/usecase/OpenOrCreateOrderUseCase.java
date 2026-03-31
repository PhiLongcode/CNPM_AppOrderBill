package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.OpenOrCreateOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.OpenOrCreateOrderOutput;

public class OpenOrCreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public OpenOrCreateOrderUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public OpenOrCreateOrderOutput execute(OpenOrCreateOrderInput input) {
        Order order = orderRepository.findActiveByTable(input.getTableNumber())
                .orElseGet(() -> {
                    long newId = System.currentTimeMillis();
                    Order created = new Order(String.valueOf(newId), input.getTableNumber(),
                            java.time.LocalDateTime.now(),
                            com.giadinh.apporderbill.orders.model.OrderStatus.PENDING,
                            0);
                    orderRepository.save(created);
                    return created;
                });
        Long orderId = order.getId();
        return new OpenOrCreateOrderOutput(
                orderId == null ? Long.parseLong(order.getOrderId()) : orderId,
                order.getTableNumber(),
                OrderUseCaseSupport.toOutputs(order),
                OrderUseCaseSupport.total(order));
    }
}

