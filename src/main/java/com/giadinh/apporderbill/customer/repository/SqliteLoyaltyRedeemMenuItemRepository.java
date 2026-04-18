package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteLoyaltyRedeemMenuItemRepository implements LoyaltyRedeemMenuItemRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqliteLoyaltyRedeemMenuItemRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<LoyaltyRedeemMenuItem> findAllActive() {
        return query("SELECT id, menu_item_id, points_cost, active FROM loyalty_redeem_menu_items WHERE active = 1 ORDER BY id");
    }

    @Override
    public List<LoyaltyRedeemMenuItem> findAll() {
        return query("SELECT id, menu_item_id, points_cost, active FROM loyalty_redeem_menu_items ORDER BY id");
    }

    @Override
    public Optional<LoyaltyRedeemMenuItem> findById(long id) {
        String sql = "SELECT id, menu_item_id, points_cost, active FROM loyalty_redeem_menu_items WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyRedeemMenuItemRepository.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void save(LoyaltyRedeemMenuItem row) {
        if (row.getId() == null) {
            String sql = "INSERT INTO loyalty_redeem_menu_items (menu_item_id, points_cost, active) VALUES (?, ?, ?)";
            try (Connection c = connectionProvider.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, row.getMenuItemId());
                ps.setInt(2, row.getPointsCost());
                ps.setInt(3, row.isActive() ? 1 : 0);
                ps.executeUpdate();
                try (PreparedStatement idPs = c.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = idPs.executeQuery()) {
                    if (rs.next()) {
                        row.setId(rs.getLong(1));
                    }
                }
            } catch (Exception e) {
                System.err.println("SqliteLoyaltyRedeemMenuItemRepository.save insert: " + e.getMessage());
            }
        } else {
            String sql = "UPDATE loyalty_redeem_menu_items SET menu_item_id = ?, points_cost = ?, active = ? WHERE id = ?";
            try (Connection c = connectionProvider.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, row.getMenuItemId());
                ps.setInt(2, row.getPointsCost());
                ps.setInt(3, row.isActive() ? 1 : 0);
                ps.setLong(4, row.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("SqliteLoyaltyRedeemMenuItemRepository.save update: " + e.getMessage());
            }
        }
    }

    @Override
    public void delete(long id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM loyalty_redeem_menu_items WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyRedeemMenuItemRepository.delete: " + e.getMessage());
        }
    }

    private List<LoyaltyRedeemMenuItem> query(String sql) {
        List<LoyaltyRedeemMenuItem> out = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyRedeemMenuItemRepository.query: " + e.getMessage());
        }
        return out;
    }

    private static LoyaltyRedeemMenuItem map(ResultSet rs) throws Exception {
        return new LoyaltyRedeemMenuItem(
                rs.getLong("id"),
                rs.getInt("menu_item_id"),
                rs.getInt("points_cost"),
                rs.getInt("active") == 1);
    }
}
