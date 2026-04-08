package com.giadinh.apporderbill.billing.repository;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlPaymentRepository implements PaymentRepository {
    private static final DateTimeFormatter DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final MySqlConnectionProvider connectionProvider;

    public MySqlPaymentRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Payment save(Payment payment) {
        String sql = """
                INSERT INTO payments(order_id, total_amount, final_amount, paid_amount, payment_method, discount_amount, discount_percent, cashier, paid_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, payment.getOrderId());
            ps.setLong(2, payment.getTotalAmount());
            ps.setLong(3, payment.getFinalAmount());
            ps.setLong(4, payment.getPaidAmount());
            ps.setString(5, payment.getPaymentMethod());
            ps.setLong(6, payment.getDiscountAmount());
            ps.setDouble(7, payment.getDiscountPercent());
            ps.setString(8, payment.getCashier());
            ps.setString(9, payment.getPaidAt() == null ? LocalDateTime.now().format(DT) : payment.getPaidAt().format(DT));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) payment.setPaymentId(rs.getLong(1));
            }
        } catch (Exception ignored) {}
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
        List<Payment> out = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Payment p = map(rs);
                if ((start == null || !p.getPaidAt().isBefore(start)) && (end == null || !p.getPaidAt().isAfter(end))) {
                    out.add(p);
                }
            }
        } catch (Exception ignored) {}
        out.sort((a, b) -> b.getPaidAt().compareTo(a.getPaidAt()));
        return out;
    }

    @Override
    public void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "DELETE FROM payments WHERE paid_at >= ? AND paid_at <= ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, start.format(DT));
            ps.setString(2, end.format(DT));
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private Payment map(ResultSet rs) throws Exception {
        Payment payment = new Payment(
                rs.getString("order_id"),
                rs.getLong("total_amount"),
                rs.getLong("final_amount"),
                rs.getLong("paid_amount"),
                rs.getString("payment_method"),
                rs.getLong("discount_amount"),
                rs.getDouble("discount_percent"),
                rs.getString("cashier"));
        payment.setPaymentId(rs.getLong("id"));
        return payment;
    }
}
