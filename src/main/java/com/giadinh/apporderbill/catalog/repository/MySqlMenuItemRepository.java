package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlMenuItemRepository implements MenuItemRepository {
    private final MySqlConnectionProvider connectionProvider;

    public MySqlMenuItemRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        String sql = "SELECT * FROM menu_items WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public Optional<MenuItem> findByName(String name) {
        String sql = "SELECT * FROM menu_items WHERE lower(name)=lower(?) LIMIT 1";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public List<MenuItem> findAll() {
        return queryMany("SELECT * FROM menu_items ORDER BY id", null);
    }

    @Override
    public List<MenuItem> findByCategoryName(String categoryName) {
        return queryMany("SELECT * FROM menu_items WHERE lower(category)=lower(?) ORDER BY id", ps -> ps.setString(1, categoryName));
    }

    @Override
    public List<MenuItem> findByStatus(MenuItemStatus status) {
        int active = status == MenuItemStatus.ACTIVE ? 1 : 0;
        return queryMany("SELECT * FROM menu_items WHERE is_active = ? ORDER BY id", ps -> ps.setInt(1, active));
    }

    @Override
    public void save(MenuItem menuItem) {
        if (menuItem.getId() <= 0) {
            String ins = """
                    INSERT INTO menu_items(name, category, unit_price, is_active, image_url, base_unit, stock_tracked, stock_qty, stock_min, stock_max)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(ins)) {
                bind(ps, menuItem, false);
                ps.executeUpdate();
            } catch (Exception ignored) {}
            return;
        }
        String upd = """
                UPDATE menu_items SET name=?, category=?, unit_price=?, is_active=?, image_url=?, base_unit=?, stock_tracked=?, stock_qty=?, stock_min=?, stock_max=?
                WHERE id = ?
                """;
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(upd)) {
            bind(ps, menuItem, true);
            ps.setInt(11, menuItem.getId());
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Override
    public boolean decreaseStockAtomic(int id, int quantity) {
        if (quantity <= 0) return true;
        String sql = "UPDATE menu_items SET stock_qty = stock_qty - ? WHERE id = ? AND stock_tracked = 1 AND stock_qty >= ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, id);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean increaseStockAtomic(int id, int quantity) {
        if (quantity <= 0) return true;
        String sql = "UPDATE menu_items SET stock_qty = stock_qty + ? WHERE id = ? AND stock_tracked = 1";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM menu_items WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private interface Binder { void bind(PreparedStatement ps) throws Exception; }
    private List<MenuItem> queryMany(String sql, Binder binder) {
        List<MenuItem> out = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (Exception ignored) {}
        return out;
    }

    private void bind(PreparedStatement ps, MenuItem m, boolean isUpdate) throws Exception {
        ps.setString(1, m.getName());
        ps.setString(2, m.getCategoryName());
        ps.setLong(3, Math.round(m.getPrice()));
        ps.setInt(4, m.getStatus() == MenuItemStatus.ACTIVE ? 1 : 0);
        ps.setString(5, m.getImageUrl());
        ps.setString(6, m.getUnitOfMeasureName());
        ps.setInt(7, m.isStockManaged() ? 1 : 0);
        ps.setInt(8, m.getCurrentStockQuantity());
        ps.setInt(9, m.getMinStockQuantity());
        ps.setInt(10, m.getMaxStockQuantity());
    }

    private MenuItem mapRow(ResultSet rs) throws Exception {
        return new MenuItem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getLong("unit_price"),
                rs.getString("category"),
                rs.getString("image_url"),
                rs.getInt("stock_tracked") == 1,
                rs.getInt("stock_qty"),
                rs.getInt("stock_min"),
                rs.getInt("stock_max"),
                rs.getString("base_unit"),
                rs.getInt("is_active") == 1 ? MenuItemStatus.ACTIVE : MenuItemStatus.INACTIVE
        );
    }
}
