package com.giadinh.apporderbill.orders.repository;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lưu đơn hàng và chi tiết đơn vào SQLite (bảng {@code orders}, {@code order_items}).
 */
public class SqliteOrderRepository implements OrderRepository {
    private static final DateTimeFormatter DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final SqliteConnectionProvider connectionProvider;

    public SqliteOrderRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Order> findById(String orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        String sqlOrder = "SELECT id, order_code, table_id, order_date, status, total_amount FROM orders WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sqlOrder)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Order order = mapOrderHeader(rs);
                loadItems(c, order);
                order.recomputeTotalFromItems();
                return Optional.of(order);
            }
        } catch (Exception e) {
            System.err.println("SqliteOrderRepository.findById: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> findByTableIdAndStatusPending(String tableId) {
        if (tableId == null) {
            return Optional.empty();
        }
        String sql = """
                SELECT id FROM orders
                WHERE table_id = ? AND status IN ('PENDING', 'IN_PROGRESS')
                ORDER BY order_date DESC
                LIMIT 1
                """;
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return findById(rs.getString("id"));
                }
            }
        } catch (Exception e) {
            System.err.println("SqliteOrderRepository.findByTableIdAndStatusPending: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void save(Order order) {
        if (order == null || order.getOrderId() == null) {
            return;
        }
        String upsert = """
                INSERT INTO orders (id, table_id, order_date, status, total_amount, order_code)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    table_id = excluded.table_id,
                    order_date = excluded.order_date,
                    status = excluded.status,
                    total_amount = excluded.total_amount,
                    order_code = excluded.order_code
                """;
        try (Connection c = connectionProvider.getConnection()) {
            c.setAutoCommit(false);
            try {
                order.recomputeTotalFromItems();
                try (PreparedStatement ps = c.prepareStatement(upsert)) {
                    ps.setString(1, order.getOrderId());
                    ps.setString(2, order.getTableId());
                    ps.setString(3, order.getOrderDate() != null ? order.getOrderDate().format(DT) : LocalDateTime.now().format(DT));
                    ps.setString(4, order.getStatus().name());
                    ps.setDouble(5, order.getTotalAmount());
                    ps.setString(6, order.getOrderCode());
                    ps.executeUpdate();
                }
                try (PreparedStatement del = c.prepareStatement("DELETE FROM order_items WHERE order_id = ?")) {
                    del.setString(1, order.getOrderId());
                    del.executeUpdate();
                }
                String insItem = """
                        INSERT INTO order_items (id, order_id, menu_item_id, menu_item_name, quantity, price, note, status, is_printed)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = c.prepareStatement(insItem)) {
                    for (OrderItem it : order.getItems()) {
                        ps.setString(1, it.getOrderItemId());
                        ps.setString(2, order.getOrderId());
                        ps.setString(3, it.getMenuItemId());
                        ps.setString(4, it.getMenuItemName());
                        ps.setInt(5, it.getQuantity());
                        ps.setDouble(6, it.getPrice());
                        ps.setString(7, it.getNote() == null ? "" : it.getNote());
                        ps.setString(8, null);
                        ps.setInt(9, 0);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.err.println("SqliteOrderRepository.save: " + e.getMessage());
        }
    }

    @Override
    public void delete(String orderId) {
        if (orderId == null) {
            return;
        }
        try (Connection c = connectionProvider.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM order_items WHERE order_id = ?")) {
                    ps.setString(1, orderId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM orders WHERE id = ?")) {
                    ps.setString(1, orderId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.err.println("SqliteOrderRepository.delete: " + e.getMessage());
        }
    }

    @Override
    public List<Order> findAllPendingOrders() {
        List<Order> out = new ArrayList<>();
        String sql = "SELECT id FROM orders WHERE status IN ('PENDING', 'IN_PROGRESS')";
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                findById(rs.getString("id")).ifPresent(out::add);
            }
        } catch (Exception e) {
            System.err.println("SqliteOrderRepository.findAllPendingOrders: " + e.getMessage());
        }
        return out;
    }

    private static Order mapOrderHeader(ResultSet rs) throws Exception {
        String id = rs.getString("id");
        String orderCode = rs.getString("order_code");
        String tableId = rs.getString("table_id");
        String dateStr = rs.getString("order_date");
        LocalDateTime orderDate = dateStr != null && !dateStr.isBlank()
                ? LocalDateTime.parse(dateStr, DT)
                : LocalDateTime.now();
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(rs.getString("status"));
        } catch (Exception e) {
            status = OrderStatus.PENDING;
        }
        double total = rs.getDouble("total_amount");
        Order order = new Order(id, tableId, orderDate, status, total);
        order.setOrderCode(orderCode);
        return order;
    }

    private static void loadItems(Connection c, Order order) throws Exception {
        String sql = """
                SELECT id, order_id, menu_item_id, menu_item_name, quantity, price, note
                FROM order_items WHERE order_id = ?
                ORDER BY rowid
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, order.getOrderId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String note = rs.getString("note");
                    if (note == null) {
                        note = "";
                    }
                    OrderItem it = new OrderItem(
                            rs.getString("id"),
                            rs.getString("order_id"),
                            rs.getString("menu_item_id"),
                            rs.getString("menu_item_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            note);
                    order.restoreOrderItem(it);
                }
            }
        }
    }
}
