package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemDiscountInput;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemDiscountOutput;

public class UpdateOrderItemDiscountUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public UpdateOrderItemDiscountUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public UpdateOrderItemDiscountOutput execute(UpdateOrderItemDiscountInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        // Current OrderItem model has no discount field yet. Keep item list unchanged.
        orderRepository.save(order);
        return new UpdateOrderItemDiscountOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }
}

