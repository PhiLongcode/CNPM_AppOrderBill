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
        ensureExtendedColumns();
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        String sql = """
                SELECT id, name, category, unit_price, is_active, image_url,
                       stock_tracked, stock_qty, stock_min, stock_max, base_unit
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
                SELECT id, name, category, unit_price, is_active, image_url,
                       stock_tracked, stock_qty, stock_min, stock_max, base_unit
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
                SELECT id, name, category, unit_price, is_active, image_url,
                       stock_tracked, stock_qty, stock_min, stock_max, base_unit
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
                SELECT id, name, category, unit_price, is_active, image_url,
                       stock_tracked, stock_qty, stock_min, stock_max, base_unit
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
                SELECT id, name, category, unit_price, is_active, image_url,
                       stock_tracked, stock_qty, stock_min, stock_max, base_unit
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
                    INSERT INTO menu_items(name, category, unit_price, is_active, image_url, base_unit,
                                           stock_tracked, stock_qty, stock_min, stock_max, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))
                    """;
            try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, menuItem.getName());
                ps.setString(2, menuItem.getCategoryName());
                ps.setLong(3, Math.round(menuItem.getPrice()));
                ps.setInt(4, active ? 1 : 0);
                ps.setString(5, menuItem.getImageUrl());
                ps.setString(6, menuItem.getUnitOfMeasureName());
                ps.setInt(7, menuItem.isStockManaged() ? 1 : 0);
                ps.setInt(8, menuItem.getCurrentStockQuantity());
                ps.setInt(9, menuItem.getMinStockQuantity());
                ps.setInt(10, menuItem.getMaxStockQuantity());
                ps.executeUpdate();
            } catch (Exception ignored) {
            }
            return;
        }
        String sql = """
                UPDATE menu_items
                SET name = ?, category = ?, unit_price = ?, is_active = ?, image_url = ?, base_unit = ?,
                    stock_tracked = ?, stock_qty = ?, stock_min = ?, stock_max = ?, updated_at = datetime('now')
                WHERE id = ?
                """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, menuItem.getName());
            ps.setString(2, menuItem.getCategoryName());
            ps.setLong(3, Math.round(menuItem.getPrice()));
            ps.setInt(4, active ? 1 : 0);
            ps.setString(5, menuItem.getImageUrl());
            ps.setString(6, menuItem.getUnitOfMeasureName());
            ps.setInt(7, menuItem.isStockManaged() ? 1 : 0);
            ps.setInt(8, menuItem.getCurrentStockQuantity());
            ps.setInt(9, menuItem.getMinStockQuantity());
            ps.setInt(10, menuItem.getMaxStockQuantity());
            ps.setInt(11, menuItem.getId());
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

    /**
     * Backward-compatible migration: old DB only had base menu columns.
     * Add newer columns if they are missing so SELECT/UPDATE won't fail.
     */
    private void ensureExtendedColumns() {
        if (connectionProvider == null) {
            return;
        }
        try (Connection c = getConnection(); PreparedStatement ps1 = c.prepareStatement(
                "ALTER TABLE menu_items ADD COLUMN image_url TEXT");
                PreparedStatement ps2 = c.prepareStatement(
                        "ALTER TABLE menu_items ADD COLUMN stock_tracked INTEGER NOT NULL DEFAULT 0");
                PreparedStatement ps3 = c.prepareStatement(
                        "ALTER TABLE menu_items ADD COLUMN stock_qty INTEGER NOT NULL DEFAULT 0");
                PreparedStatement ps4 = c.prepareStatement(
                        "ALTER TABLE menu_items ADD COLUMN stock_min INTEGER NOT NULL DEFAULT 0");
                PreparedStatement ps5 = c.prepareStatement(
                        "ALTER TABLE menu_items ADD COLUMN stock_max INTEGER NOT NULL DEFAULT 0");
                PreparedStatement ps6 = c.prepareStatement(
                        "ALTER TABLE menu_items ADD COLUMN base_unit TEXT")) {
            try {
                ps1.executeUpdate();
            } catch (Exception ignored) {
            }
            try {
                ps2.executeUpdate();
            } catch (Exception ignored) {
            }
            try {
                ps3.executeUpdate();
            } catch (Exception ignored) {
            }
            try {
                ps4.executeUpdate();
            } catch (Exception ignored) {
            }
            try {
                ps5.executeUpdate();
            } catch (Exception ignored) {
            }
            try {
                ps6.executeUpdate();
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }

    private MenuItem mapRow(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        long unitPrice = rs.getLong("unit_price");
        boolean active = rs.getInt("is_active") == 1;
        String imageUrl = rs.getString("image_url");
        boolean stockTracked = rs.getInt("stock_tracked") == 1;
        int stockQty = rs.getInt("stock_qty");
        int stockMin = rs.getInt("stock_min");
        int stockMax = rs.getInt("stock_max");
        String baseUnit = rs.getString("base_unit");
        return new MenuItem(
                id,
                name,
                unitPrice,
                category == null ? "Khác" : category,
                imageUrl,
                stockTracked,
                stockQty,
                stockMin,
                stockMax,
                baseUnit == null ? "phần" : baseUnit,
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

