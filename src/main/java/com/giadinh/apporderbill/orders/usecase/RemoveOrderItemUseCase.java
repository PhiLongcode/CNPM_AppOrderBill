package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.RemoveOrderItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.RemoveOrderItemOutput;

public class RemoveOrderItemUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public RemoveOrderItemUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public RemoveOrderItemOutput execute(RemoveOrderItemInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        int idx = Math.max(0, input.getOrderItemId().intValue() - 1);
        if (idx >= order.getItems().size()) {
            throw new IllegalArgumentException("Không tìm thấy món trong order.");
        }
        var item = order.getItems().get(idx);
        order.removeOrderItem(item.getMenuItemId());
        orderRepository.save(order);
        return new RemoveOrderItemOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }
}

