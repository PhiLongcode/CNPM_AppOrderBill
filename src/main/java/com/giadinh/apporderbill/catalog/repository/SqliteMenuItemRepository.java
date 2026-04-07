package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public class SqliteMenuItemRepository implements MenuItemRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqliteMenuItemRepository(Object connectionProvider) {
        this.connectionProvider = connectionProvider instanceof SqliteConnectionProvider
                ? (SqliteConnectionProvider) connectionProvider
                : null;
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        String sql = """
                SELECT id, name, category, unit_price, is_active
                FROM menu_items
                WHERE id = ?
                """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<MenuItem> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String sql = """
                SELECT id, name, category, unit_price, is_active
                FROM menu_items
                WHERE lower(name) = lower(?)
                LIMIT 1
                """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    @Override
    public List<MenuItem> findAll() {
        String sql = """
                SELECT id, name, category, unit_price, is_active
                FROM menu_items
                ORDER BY id
                """;
        return queryMany(sql, null);
    }

    @Override
    public List<MenuItem> findByCategoryName(String categoryName) {
        if (categoryName == null) {
            return List.of();
        }
        String sql = """
                SELECT id, name, category, unit_price, is_active
                FROM menu_items
                WHERE lower(category) = lower(?)
                ORDER BY id
                """;
        return queryMany(sql, ps -> ps.setString(1, categoryName));
    }

    @Override
    public List<MenuItem> findByStatus(MenuItemStatus status) {
        if (status == null) {
            return List.of();
        }
        String sql = """
                SELECT id, name, category, unit_price, is_active
                FROM menu_items
                WHERE is_active = ?
                ORDER BY id
                """;
        int activeFlag = status == MenuItemStatus.ACTIVE ? 1 : 0;
        return queryMany(sql, ps -> ps.setInt(1, activeFlag));
    }

    @Override
    public void save(MenuItem menuItem) {
        if (menuItem == null) {
            return;
        }
        boolean active = menuItem.getStatus() == MenuItemStatus.ACTIVE;
        if (menuItem.getId() <= 0) {
            String sql = """
                    INSERT INTO menu_items(name, category, unit_price, is_active, created_at, updated_at)
                    VALUES (?, ?, ?, ?, datetime('now'), datetime('now'))
                    """;
            try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, menuItem.getName());
                ps.setString(2, menuItem.getCategoryName());
                ps.setLong(3, Math.round(menuItem.getPrice()));
                ps.setInt(4, active ? 1 : 0);
                ps.executeUpdate();
            } catch (Exception ignored) {
            }
            return;
        }
        String sql = """
                UPDATE menu_items
                SET name = ?, category = ?, unit_price = ?, is_active = ?, updated_at = datetime('now')
                WHERE id = ?
                """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, menuItem.getName());
            ps.setString(2, menuItem.getCategoryName());
            ps.setLong(3, Math.round(menuItem.getPrice()));
            ps.setInt(4, active ? 1 : 0);
            ps.setInt(5, menuItem.getId());
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private Connection getConnection() throws Exception {
        if (connectionProvider == null) {
            throw new DomainException(ErrorCode.SQLITE_CONNECTION_NOT_CONFIGURED);
        }
        return connectionProvider.getConnection();
    }

    private MenuItem mapRow(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        long unitPrice = rs.getLong("unit_price");
        boolean active = rs.getInt("is_active") == 1;
        return new MenuItem(
                id,
                name,
                unitPrice,
                category == null ? "Khác" : category,
                null,
                false,
                0,
                0,
                0,
                "phần",
                active ? MenuItemStatus.ACTIVE : MenuItemStatus.INACTIVE);
    }

    @FunctionalInterface
    private interface Binder {
        void bind(PreparedStatement ps) throws Exception;
    }

    private List<MenuItem> queryMany(String sql, Binder binder) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                java.util.ArrayList<MenuItem> out = new java.util.ArrayList<>();
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
                return out;
            }
        } catch (Exception ignored) {
            return List.of();
        }
    }
}

