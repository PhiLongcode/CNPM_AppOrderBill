package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.PointTransaction;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlitePointTransactionRepository implements PointTransactionRepository {

    private final SqliteConnectionProvider connectionProvider;

    public SqlitePointTransactionRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public PointTransaction save(PointTransaction tx) {
        String sql = """
                INSERT INTO point_transactions
                    (customer_id, delta, balance_after, type, note, order_id, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, tx.getCustomerId());
            ps.setInt(2, tx.getDelta());
            ps.setInt(3, tx.getBalanceAfter());
            ps.setString(4, tx.getType().name());
            ps.setString(5, tx.getNote());
            ps.setString(6, tx.getOrderId());
            ps.setString(7, tx.getCreatedAt() != null
                    ? tx.getCreatedAt().toString() : LocalDateTime.now().toString());
            ps.executeUpdate();
            try (Statement id = c.createStatement();
                 ResultSet rs = id.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    tx.setId(rs.getLong(1));
                }
            }
        } catch (Exception e) {
            System.err.println("SqlitePointTransactionRepository.save: " + e.getMessage());
        }
        return tx;
    }

    @Override
    public List<PointTransaction> findByCustomerId(Long customerId) {
        String sql = """
                SELECT * FROM point_transactions
                WHERE customer_id = ?
                ORDER BY created_at DESC
                """;
        return query(sql, ps -> ps.setLong(1, customerId));
    }

    @Override
    public List<PointTransaction> findByCustomerIdAndDateBetween(Long customerId,
                                                                 LocalDateTime from,
                                                                 LocalDateTime to) {
        String sql = """
                SELECT * FROM point_transactions
                WHERE customer_id = ?
                  AND created_at >= ?
                  AND created_at <= ?
                ORDER BY created_at DESC
                """;
        return query(sql, ps -> {
            ps.setLong(1, customerId);
            ps.setString(2, from.toString());
            ps.setString(3, to.toString());
        });
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    @FunctionalInterface
    private interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private List<PointTransaction> query(String sql, ParamSetter setter) {
        List<PointTransaction> list = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqlitePointTransactionRepository.query: " + e.getMessage());
        }
        return list;
    }

    private PointTransaction mapRow(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("type");
        PointTransaction.Type type;
        try {
            type = PointTransaction.Type.valueOf(typeStr);
        } catch (Exception e) {
            type = PointTransaction.Type.EARN;
        }
        String createdAtStr = rs.getString("created_at");
        LocalDateTime createdAt = null;
        try {
            if (createdAtStr != null) createdAt = LocalDateTime.parse(createdAtStr);
        } catch (Exception ignored) {}

        return new PointTransaction(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getInt("delta"),
                rs.getInt("balance_after"),
                type,
                rs.getString("note"),
                rs.getString("order_id"),
                createdAt);
    }
}
