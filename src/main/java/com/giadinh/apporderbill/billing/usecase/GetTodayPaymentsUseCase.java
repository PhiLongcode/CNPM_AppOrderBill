package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput;
import com.giadinh.apporderbill.orders.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class GetTodayPaymentsUseCase {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public GetTodayPaymentsUseCase(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public List<PaymentSummaryOutput> execute() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        return paymentRepository.findByPaidAtBetween(start, end).stream()
                .map(p -> {
                    String table = orderRepository.findById(p.getOrderId())
                            .map(o -> o.getTableId())
                            .orElse("?");
                    return new PaymentSummaryOutput(
                            p.getPaymentId(),
                            table,
                            p.getFinalAmount(),
                            p.getPaymentMethod(),
                            p.getPaidAt());
                })
                .toList();
    }
}

