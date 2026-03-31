package com.giadinh.apporderbill.billing.repository;

import com.giadinh.apporderbill.billing.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Temporary in-memory implementation to keep checkout flow functional
 * while SQLite persistence is being completed.
 */
public class SqlitePaymentRepository implements PaymentRepository {
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, Payment> storage = new ConcurrentHashMap<>();

    public SqlitePaymentRepository(Object connectionProvider) {
        // Keep signature compatible with application wiring.
    }

    @Override
    public Payment save(Payment payment) {
        if (payment.getPaymentId() == null) {
            payment.setPaymentId(idGenerator.getAndIncrement());
        }
        storage.put(payment.getPaymentId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        return Optional.ofNullable(storage.get(paymentId));
    }

    @Override
    public List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
        return storage.values().stream()
                .filter(p -> p.getPaidAt() != null
                        && (start == null || !p.getPaidAt().isBefore(start))
                        && (end == null || !p.getPaidAt().isAfter(end)))
                .sorted((a, b) -> b.getPaidAt().compareTo(a.getPaidAt()))
                .toList();
    }

    @Override
    public void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
        storage.entrySet().removeIf(e -> {
            Payment p = e.getValue();
            if (p.getPaidAt() == null) return false;
            boolean afterStart = start == null || !p.getPaidAt().isBefore(start);
            boolean beforeEnd = end == null || !p.getPaidAt().isAfter(end);
            return afterStart && beforeEnd;
        });
    }
}
