package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.GetOrderDetailsInput;
import com.giadinh.apporderbill.orders.usecase.dto.GetOrderDetailsOutput;

public class GetOrderDetailsUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public GetOrderDetailsUseCase(OrderRepository orderRepository, Object menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public GetOrderDetailsOutput execute(GetOrderDetailsInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        return new GetOrderDetailsOutput(
                order.getTableNumber(),
                OrderUseCaseSupport.toOutputs(order),
                OrderUseCaseSupport.total(order));
    }
}

