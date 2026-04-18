package com.giadinh.apporderbill.billing.repository;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlitePaymentRepository implements PaymentRepository {
    private final SqliteConnectionProvider connectionProvider;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SqlitePaymentRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Payment save(Payment payment) {
        boolean isInsert = payment.getPaymentId() == null;
        try (Connection c = connectionProvider.getConnection()) {
            if (isInsert) {
                String sql = """
                    INSERT INTO payments (order_id, total_amount, final_amount, paid_amount, payment_method, discount_amount, discount_percent, cashier, paid_at, customer_id, vat_percent, vat_amount, net_before_vat, amount_after_vat_before_points, points_discount_amount)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    bindParams(ps, payment);
                    ps.executeUpdate();
                }
                // SQLite JDBC does not support RETURN_GENERATED_KEYS, use last_insert_rowid() instead
                try (PreparedStatement ps2 = c.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        payment.setPaymentId(rs.getLong(1));
                    }
                }
            } else {
                String sql = """
                    UPDATE payments SET order_id = ?, total_amount = ?, final_amount = ?, paid_amount = ?, payment_method = ?, discount_amount = ?, discount_percent = ?, cashier = ?, paid_at = ?, customer_id = ?, vat_percent = ?, vat_amount = ?, net_before_vat = ?, amount_after_vat_before_points = ?, points_discount_amount = ?
                    WHERE id = ?
                    """;
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    bindParams(ps, payment);
                    ps.setLong(16, payment.getPaymentId());
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("SqlitePaymentRepository.save: " + e.getMessage());
        }
        return payment;
    }

    private void bindParams(PreparedStatement ps, Payment payment) throws Exception {
        ps.setString(1, payment.getOrderId());
        ps.setLong(2, payment.getTotalAmount());
        ps.setLong(3, payment.getFinalAmount());
        ps.setLong(4, payment.getPaidAmount());
        ps.setString(5, payment.getPaymentMethod());
        if (payment.getDiscountAmount() != null) {
            ps.setLong(6, payment.getDiscountAmount());
        } else {
            ps.setNull(6, java.sql.Types.INTEGER);
        }
        if (payment.getDiscountPercent() != null) {
            ps.setDouble(7, payment.getDiscountPercent());
        } else {
            ps.setNull(7, java.sql.Types.REAL);
        }
        ps.setString(8, payment.getCashier());
        ps.setString(9, payment.getPaidAt() != null ? payment.getPaidAt().format(DT) : LocalDateTime.now().format(DT));
        if (payment.getCustomerId() != null) {
            ps.setLong(10, payment.getCustomerId());
        } else {
            ps.setNull(10, java.sql.Types.INTEGER);
        }
        ps.setDouble(11, payment.getVatPercent());
        ps.setLong(12, payment.getVatAmount());
        ps.setLong(13, payment.getNetAmountBeforeVat());
        ps.setLong(14, payment.getAmountAfterVatBeforePoints());
        ps.setLong(15, payment.getPointsDiscountAmount());
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        if (paymentId == null) return Optional.empty();
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqlitePaymentRepository.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE 1=1";
        if (start != null) sql += " AND paid_at >= ?";
        if (end != null) sql += " AND paid_at <= ?";
        sql += " ORDER BY paid_at DESC";

        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int pIndex = 1;
            if (start != null) ps.setString(pIndex++, start.format(DT));
            if (end != null) ps.setString(pIndex, end.format(DT));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqlitePaymentRepository.findByPaidAtBetween: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "DELETE FROM payments WHERE 1=1";
        if (start != null) sql += " AND paid_at >= ?";
        if (end != null) sql += " AND paid_at <= ?";

        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int pIndex = 1;
            if (start != null) ps.setString(pIndex++, start.format(DT));
            if (end != null) ps.setString(pIndex, end.format(DT));
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("SqlitePaymentRepository.deleteByPaidAtBetween: " + e.getMessage());
        }
    }

    private Payment mapRow(ResultSet rs) throws Exception {
        Long id = rs.getLong("id");
        String orderId = rs.getString("order_id");
        long totalAmount = rs.getLong("total_amount");
        long finalAmount = rs.getLong("final_amount");
        long paidAmount = rs.getLong("paid_amount");
        String paymentMethod = rs.getString("payment_method");
        Long discountAmount = rs.getLong("discount_amount");
        if (rs.wasNull()) discountAmount = null;
        Double discountPercent = rs.getDouble("discount_percent");
        if (rs.wasNull()) discountPercent = null;
        String cashier = rs.getString("cashier");
        String paidAtStr = rs.getString("paid_at");
        LocalDateTime paidAt = paidAtStr != null ? LocalDateTime.parse(paidAtStr, DT) : LocalDateTime.now();

        Long customerId = null;
        try { // handle old schema that might not have the column
            long idVal = rs.getLong("customer_id");
            if (!rs.wasNull()) {
                customerId = idVal;
            }
        } catch (Exception ignored) {}

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

        Payment payment = new Payment(orderId, totalAmount, finalAmount, paidAmount, paymentMethod, discountAmount, discountPercent, cashier, customerId,
                vatAmount, vatPercent, pointsDiscount, netBeforeVat, amountAfterVatBeforePoints);
        payment.setPaymentId(id);
        payment.setPaidAt(paidAt);
        return payment;
    }
}
