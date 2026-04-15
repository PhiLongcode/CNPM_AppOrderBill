package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
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
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        
        boolean found = false;
        long orderItemIndex = input.getOrderItemId() - 1; // get 0-index based list index
        if (orderItemIndex >= 0 && orderItemIndex < order.getItems().size()) {
            var item = order.getItems().get((int) orderItemIndex);
            if (input.getDiscountPercent() != null) {
                item.setDiscountPercent(input.getDiscountPercent());
            }
            if (input.getDiscountAmount() != null) {
                item.setDiscountAmount(input.getDiscountAmount());
            }
            found = true;
        }

        if (!found) {
             throw new DomainException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        orderRepository.save(order);
        return new UpdateOrderItemDiscountOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }
}

