package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemNoteInput;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemNoteOutput;

public class UpdateOrderItemNoteUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public UpdateOrderItemNoteUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public UpdateOrderItemNoteOutput execute(UpdateOrderItemNoteInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        int idx = Math.max(0, input.getOrderItemId().intValue() - 1);
        if (idx >= order.getItems().size()) {
            throw new IllegalArgumentException("Không tìm thấy món trong order.");
        }
        order.getItems().get(idx).setNote(input.getNote());
        orderRepository.save(order);
        return new UpdateOrderItemNoteOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }
}

