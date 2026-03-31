package com.giadinh.apporderbill.billing.repository;

import com.giadinh.apporderbill.billing.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(Long paymentId);

    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);

    void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}
