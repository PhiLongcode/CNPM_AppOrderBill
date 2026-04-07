package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.service.OrderCodeService;
import com.giadinh.apporderbill.orders.usecase.dto.OpenOrCreateOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.OpenOrCreateOrderOutput;

public class OpenOrCreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;
    private final OrderCodeService orderCodeService;

    public OpenOrCreateOrderUseCase(
            OrderRepository orderRepository,
            Object menuItemRepository,
            OrderCodeService orderCodeService) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderCodeService = orderCodeService;
    }

    public OpenOrCreateOrderOutput execute(OpenOrCreateOrderInput input) {
        Order order = orderRepository.findActiveByTable(input.getTableNumber())
                .orElseGet(() -> {
                    long newId = System.currentTimeMillis();
                    Order created = new Order(String.valueOf(newId), input.getTableNumber(),
                            java.time.LocalDateTime.now(),
                            com.giadinh.apporderbill.orders.model.OrderStatus.PENDING,
                            0);
                    if (orderCodeService != null) {
                        created.setOrderCode(orderCodeService.generate(created.getOrderId(), created.getOrderDate()));
                    }
                    orderRepository.save(created);
                    return created;
                });
        if ((order.getOrderCode() == null || order.getOrderCode().isBlank()) && orderCodeService != null) {
            order.setOrderCode(orderCodeService.generate(order.getOrderId(), order.getOrderDate()));
            orderRepository.save(order);
        }
        Long orderId = order.getId();
        return new OpenOrCreateOrderOutput(
                orderId == null ? Long.parseLong(order.getOrderId()) : orderId,
                order.getTableNumber(),
                OrderUseCaseSupport.toOutputs(order),
                OrderUseCaseSupport.total(order));
    }
}

