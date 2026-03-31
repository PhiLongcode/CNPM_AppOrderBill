package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemQuantityInput;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemQuantityOutput;

public class UpdateOrderItemQuantityUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public UpdateOrderItemQuantityUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public UpdateOrderItemQuantityOutput execute(UpdateOrderItemQuantityInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        int idx = Math.max(0, input.getOrderItemId().intValue() - 1);
        if (idx >= order.getItems().size()) {
            throw new IllegalArgumentException("Không tìm thấy món trong order.");
        }
        var item = order.getItems().get(idx);
        order.updateOrderItemQuantity(item.getMenuItemId(), input.getNewQuantity());
        orderRepository.save(order);
        return new UpdateOrderItemQuantityOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }
}

