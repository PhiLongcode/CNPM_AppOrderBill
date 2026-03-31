package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;

import java.time.LocalDateTime;

public class DeletePaymentsByDateRangeUseCase {
    private final PaymentRepository paymentRepository;

    public DeletePaymentsByDateRangeUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void execute(LocalDateTime start, LocalDateTime end) {
        paymentRepository.deleteByPaidAtBetween(start, end);
    }
}

