package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.AddCustomItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.AddCustomItemOutput;

public class AddCustomItemToOrderUseCase {
    private final OrderRepository orderRepository;

    public AddCustomItemToOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public AddCustomItemOutput execute(AddCustomItemInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        OrderItem item = new OrderItem(
                order.getOrderId(),
                "custom-" + System.nanoTime(),
                input.getItemName(),
                input.getQuantity(),
                input.getUnitPrice());
        if (input.getNotes() != null) {
            item.setNote(input.getNotes());
        }
        order.addOrderItem(item);
        orderRepository.save(order);

        return new AddCustomItemOutput(
                order.getTableNumber(),
                OrderUseCaseSupport.toOutputs(order),
                OrderUseCaseSupport.total(order));
    }
}

