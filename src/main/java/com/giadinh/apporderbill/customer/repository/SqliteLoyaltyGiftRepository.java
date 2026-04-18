package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyGift;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteLoyaltyGiftRepository implements LoyaltyGiftRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqliteLoyaltyGiftRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<LoyaltyGift> findAllActive() {
        return query("SELECT id, name, points_cost, active FROM loyalty_gifts WHERE active = 1 ORDER BY id");
    }

    @Override
    public List<LoyaltyGift> findAll() {
        return query("SELECT id, name, points_cost, active FROM loyalty_gifts ORDER BY id");
    }

    @Override
    public Optional<LoyaltyGift> findById(long id) {
        String sql = "SELECT id, name, points_cost, active FROM loyalty_gifts WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyGiftRepository.findById: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void save(LoyaltyGift gift) {
        if (gift.getId() == null) {
            String sql = "INSERT INTO loyalty_gifts (name, points_cost, active) VALUES (?, ?, ?)";
            try (Connection c = connectionProvider.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, gift.getName());
                ps.setInt(2, gift.getPointsCost());
                ps.setInt(3, gift.isActive() ? 1 : 0);
                ps.executeUpdate();
                try (PreparedStatement idPs = c.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = idPs.executeQuery()) {
                    if (rs.next()) {
                        gift.setId(rs.getLong(1));
                    }
                }
            } catch (Exception e) {
                System.err.println("SqliteLoyaltyGiftRepository.save insert: " + e.getMessage());
            }
        } else {
            String sql = "UPDATE loyalty_gifts SET name = ?, points_cost = ?, active = ? WHERE id = ?";
            try (Connection c = connectionProvider.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, gift.getName());
                ps.setInt(2, gift.getPointsCost());
                ps.setInt(3, gift.isActive() ? 1 : 0);
                ps.setLong(4, gift.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("SqliteLoyaltyGiftRepository.save update: " + e.getMessage());
            }
        }
    }

    @Override
    public void delete(long id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM loyalty_gifts WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyGiftRepository.delete: " + e.getMessage());
        }
    }

    private List<LoyaltyGift> query(String sql) {
        List<LoyaltyGift> out = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyGiftRepository.query: " + e.getMessage());
        }
        return out;
    }

    private static LoyaltyGift map(ResultSet rs) throws Exception {
        return new LoyaltyGift(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("points_cost"),
                rs.getInt("active") == 1);
    }
}
