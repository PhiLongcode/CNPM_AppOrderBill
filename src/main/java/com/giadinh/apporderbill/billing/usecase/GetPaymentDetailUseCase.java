package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import com.giadinh.apporderbill.orders.repository.OrderRepository;

public class GetPaymentDetailUseCase {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;

    public GetPaymentDetailUseCase(PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            Object menuItemRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public PaymentDetailOutput execute(Long paymentId) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new DomainException(ErrorCode.BILL_NOT_FOUND));

        String table = orderRepository.findById(payment.getOrderId())
                .map(o -> o.getTableId())
                .orElse("?");

        return new PaymentDetailOutput(
                payment.getPaymentId(),
                payment.getOrderId(),
                table,
                payment.getTotalAmount(),
                payment.getFinalAmount(),
                payment.getPaidAmount(),
                payment.getPaymentMethod(),
                payment.getDiscountAmount(),
                payment.getDiscountPercent(),
                payment.getCashier(),
                payment.getPaidAt());
    }
}

