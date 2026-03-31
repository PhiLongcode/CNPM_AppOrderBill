package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.DeleteOrderItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.DeleteOrderItemOutput;

public class DeleteOrderItemUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public DeleteOrderItemUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public DeleteOrderItemOutput execute(DeleteOrderItemInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        int idx = Math.max(0, input.getOrderItemId().intValue() - 1);
        if (idx >= order.getItems().size()) {
            throw new IllegalArgumentException("Không tìm thấy món trong order.");
        }
        var item = order.getItems().get(idx);
        order.removeOrderItem(item.getMenuItemId());
        orderRepository.save(order);
        return new DeleteOrderItemOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }
}

