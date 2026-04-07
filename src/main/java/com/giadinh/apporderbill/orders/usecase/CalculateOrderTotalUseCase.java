package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalInput;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalOutput;

public class CalculateOrderTotalUseCase {
    private final OrderRepository orderRepository;

    public CalculateOrderTotalUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public CalculateOrderTotalOutput execute(CalculateOrderTotalInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        long subtotal = OrderUseCaseSupport.total(order);
        long discount = Math.max(0, input.getDiscountAmount());
        if (input.getDiscountPercent() != null && input.getDiscountPercent() > 0) {
            discount += Math.round(subtotal * (input.getDiscountPercent() / 100.0));
        }
        long finalAmount = Math.max(0, subtotal - discount);
        return new CalculateOrderTotalOutput(subtotal, discount, finalAmount);
    }
}

