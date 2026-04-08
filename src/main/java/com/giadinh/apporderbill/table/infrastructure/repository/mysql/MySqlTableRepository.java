package com.giadinh.apporderbill.table.infrastructure.repository.mysql;

import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.model.TableStatus;
import com.giadinh.apporderbill.table.repository.TableRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlTableRepository implements TableRepository {
    private final MySqlConnectionProvider connectionProvider;

    public MySqlTableRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Table> findById(String tableId) {
        String sql = "SELECT id, table_name, status, current_order_id FROM tables WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public Optional<Table> findByTableName(String tableName) {
        String sql = "SELECT id, table_name, status, current_order_id FROM tables WHERE lower(table_name)=lower(?)";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public List<Table> findAll() {
        List<Table> out = new ArrayList<>();
        String sql = "SELECT id, table_name, status, current_order_id FROM tables ORDER BY table_name";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        } catch (Exception ignored) {}
        return out;
    }

    @Override
    public void save(Table table) {
        String sql = """
                INSERT INTO tables(id, table_name, status, current_order_id)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    table_name = VALUES(table_name),
                    status = VALUES(status),
                    current_order_id = VALUES(current_order_id)
                """;
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, table.getTableId());
            ps.setString(2, table.getTableName());
            ps.setString(3, table.getStatus().name());
            ps.setString(4, table.getCurrentOrderId());
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Override
    public void delete(String tableId) {
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM tables WHERE id=?")) {
            ps.setString(1, tableId);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private Table mapRow(ResultSet rs) throws Exception {
        TableStatus status;
        try {
            status = TableStatus.valueOf(rs.getString("status"));
        } catch (Exception ex) {
            status = TableStatus.AVAILABLE;
        }
        return new Table(rs.getString("id"), rs.getString("table_name"), status, rs.getString("current_order_id"));
    }
}
