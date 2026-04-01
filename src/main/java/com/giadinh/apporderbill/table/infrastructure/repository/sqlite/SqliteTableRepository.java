package com.giadinh.apporderbill.table.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.model.TableStatus;
import com.giadinh.apporderbill.table.repository.TableRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTableRepository implements TableRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqliteTableRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Table> findById(String tableId) {
        if (tableId == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, table_name, status, current_order_id FROM tables WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqliteTableRepository.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Table> findByTableName(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT id, table_name, status, current_order_id FROM tables WHERE lower(table_name) = lower(?)";
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqliteTableRepository.findByTableName: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Table> findAll() {
        List<Table> out = new ArrayList<>();
        String sql = """
                SELECT id, table_name, status, current_order_id FROM tables
                ORDER BY length(table_name), table_name COLLATE NOCASE
                """;
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("SqliteTableRepository.findAll: " + e.getMessage());
        }
        return out;
    }

    @Override
    public void save(Table table) {
        if (table == null) {
            return;
        }
        String sql = """
                INSERT INTO tables (id, table_name, status, current_order_id)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    table_name = excluded.table_name,
                    status = excluded.status,
                    current_order_id = excluded.current_order_id
                """;
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, table.getTableId());
            ps.setString(2, table.getTableName());
            ps.setString(3, table.getStatus().name());
            if (table.getCurrentOrderId() == null || table.getCurrentOrderId().isBlank()) {
                ps.setString(4, null);
            } else {
                ps.setString(4, table.getCurrentOrderId());
            }
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("SqliteTableRepository.save: " + e.getMessage());
        }
    }

    @Override
    public void delete(String tableId) {
        if (tableId == null) {
            return;
        }
        String sql = "DELETE FROM tables WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("SqliteTableRepository.delete: " + e.getMessage());
        }
    }

    private static Table mapRow(ResultSet rs) throws Exception {
        String id = rs.getString("id");
        String name = rs.getString("table_name");
        String statusStr = rs.getString("status");
        String orderId = rs.getString("current_order_id");
        if (rs.wasNull()) {
            orderId = null;
        }
        TableStatus status;
        try {
            status = TableStatus.valueOf(statusStr != null ? statusStr : "AVAILABLE");
        } catch (IllegalArgumentException e) {
            status = TableStatus.AVAILABLE;
        }
        return new Table(id, name, status, orderId);
    }
}
