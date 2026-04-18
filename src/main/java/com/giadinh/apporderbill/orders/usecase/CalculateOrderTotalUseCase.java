package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalInput;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalOutput;
import java.util.function.DoubleSupplier;

public class CalculateOrderTotalUseCase {
    private final OrderRepository orderRepository;
    private final DoubleSupplier vatPercentSupplier;

    public CalculateOrderTotalUseCase(OrderRepository orderRepository) {
        this(orderRepository, () -> 0.0);
    }

    public CalculateOrderTotalUseCase(OrderRepository orderRepository, DoubleSupplier vatPercentSupplier) {
        this.orderRepository = orderRepository;
        this.vatPercentSupplier = vatPercentSupplier != null ? vatPercentSupplier : () -> 0.0;
    }

    public CalculateOrderTotalOutput execute(CalculateOrderTotalInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        long subtotal = OrderUseCaseSupport.total(order);
        long discount = Math.max(0, input.getDiscountAmount());
        if (input.getDiscountPercent() != null && input.getDiscountPercent() > 0) {
            discount += Math.round(subtotal * (input.getDiscountPercent() / 100.0));
        }
        long netAmountBeforeVat = Math.max(0, subtotal - discount);
        double vatPercent = Math.max(0.0, vatPercentSupplier.getAsDouble());
        long vatAmount = Math.round(netAmountBeforeVat * (vatPercent / 100.0));
        long finalAmount = netAmountBeforeVat + Math.max(0, vatAmount);
        return new CalculateOrderTotalOutput(subtotal, discount, netAmountBeforeVat, vatPercent, vatAmount, finalAmount);
    }
}

