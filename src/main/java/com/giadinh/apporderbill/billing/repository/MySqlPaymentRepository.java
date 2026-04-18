package com.giadinh.apporderbill.billing.repository;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
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
                INSERT INTO payments(order_id, total_amount, final_amount, paid_amount, payment_method, discount_amount, discount_percent, cashier, paid_at, customer_id, vat_percent, vat_amount, net_before_vat, amount_after_vat_before_points, points_discount_amount)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, payment.getOrderId());
            ps.setLong(2, payment.getTotalAmount());
            ps.setLong(3, payment.getFinalAmount());
            ps.setLong(4, payment.getPaidAmount());
            ps.setString(5, payment.getPaymentMethod());
            if (payment.getDiscountAmount() != null) {
                ps.setLong(6, payment.getDiscountAmount());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            if (payment.getDiscountPercent() != null) {
                ps.setDouble(7, payment.getDiscountPercent());
            } else {
                ps.setNull(7, Types.DOUBLE);
            }
            ps.setString(8, payment.getCashier());
            ps.setString(9, payment.getPaidAt() == null ? LocalDateTime.now().format(DT) : payment.getPaidAt().format(DT));
            if (payment.getCustomerId() != null) {
                ps.setLong(10, payment.getCustomerId());
            } else {
                ps.setNull(10, Types.BIGINT);
            }
            ps.setDouble(11, payment.getVatPercent());
            ps.setLong(12, payment.getVatAmount());
            ps.setLong(13, payment.getNetAmountBeforeVat());
            ps.setLong(14, payment.getAmountAfterVatBeforePoints());
            ps.setLong(15, payment.getPointsDiscountAmount());
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
        Long discountAmount = rs.getLong("discount_amount");
        if (rs.wasNull()) {
            discountAmount = null;
        }
        Double discountPercent = rs.getDouble("discount_percent");
        if (rs.wasNull()) {
            discountPercent = null;
        }
        double vatPercent = 0.0;
        long vatAmount = 0L;
        long netBeforeVat = 0L;
        long amountAfterVatBeforePoints = 0L;
        long pointsDiscount = 0L;
        try {
            vatPercent = rs.getDouble("vat_percent");
        } catch (Exception ignored) {
        }
        try {
            vatAmount = rs.getLong("vat_amount");
        } catch (Exception ignored) {
        }
        try {
            netBeforeVat = rs.getLong("net_before_vat");
        } catch (Exception ignored) {
        }
        try {
            amountAfterVatBeforePoints = rs.getLong("amount_after_vat_before_points");
        } catch (Exception ignored) {
        }
        try {
            pointsDiscount = rs.getLong("points_discount_amount");
        } catch (Exception ignored) {
        }

        Payment payment = new Payment(
                rs.getString("order_id"),
                rs.getLong("total_amount"),
                rs.getLong("final_amount"),
                rs.getLong("paid_amount"),
                rs.getString("payment_method"),
                discountAmount,
                discountPercent,
                rs.getString("cashier"),
                null,
                vatAmount,
                vatPercent,
                pointsDiscount,
                netBeforeVat,
                amountAfterVatBeforePoints);

        try {
            long idVal = rs.getLong("customer_id");
            if (!rs.wasNull()) {
                payment.setCustomerId(idVal);
            }
        } catch (Exception ignored) {}

        payment.setPaymentId(rs.getLong("id"));
        String paidAtStr = rs.getString("paid_at");
        if (paidAtStr != null && !paidAtStr.isBlank()) {
            payment.setPaidAt(LocalDateTime.parse(paidAtStr, DT));
        }
        return payment;
    }
}
