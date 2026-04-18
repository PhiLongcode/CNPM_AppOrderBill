package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySqlLoyaltyConfigRepository implements LoyaltyConfigRepository {

    private final MySqlConnectionProvider connectionProvider;

    public MySqlLoyaltyConfigRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public LoyaltyConfig load() {
        LoyaltyConfig config = LoyaltyConfig.defaults();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT `key`, `value` FROM settings WHERE `key` LIKE 'loyalty.%'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("key");
                String value = rs.getString("value");
                switch (key) {
                    case "loyalty.earnUnitAmount" -> config.setEarnUnitAmount(parseLong(value, 10_000L));
                    case "loyalty.pointsPerUnit" -> config.setPointsPerUnit(parseInt(value, 1));
                    case "loyalty.redeemPointsRequired" -> config.setRedeemPointsRequired(parseInt(value, 100));
                    case "loyalty.redeemValue" -> config.setRedeemValue(parseLong(value, 5_000L));
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("MySqlLoyaltyConfigRepository.load: " + e.getMessage());
        }
        return config;
    }

    @Override
    public void save(LoyaltyConfig config) {
        String sql = """
                INSERT INTO settings(`key`, `value`) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)
                """;
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            upsert(ps, "loyalty.earnUnitAmount", String.valueOf(config.getEarnUnitAmount()));
            upsert(ps, "loyalty.pointsPerUnit", String.valueOf(config.getPointsPerUnit()));
            upsert(ps, "loyalty.redeemPointsRequired", String.valueOf(config.getRedeemPointsRequired()));
            upsert(ps, "loyalty.redeemValue", String.valueOf(config.getRedeemValue()));
        } catch (Exception e) {
            System.err.println("MySqlLoyaltyConfigRepository.save: " + e.getMessage());
        }
    }

    @Override
    public double loadVatPercent() {
        String sql = "SELECT `value` FROM settings WHERE `key` = 'tax.vatPercent' LIMIT 1";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return parseDouble(rs.getString("value"), 0.0);
            }
        } catch (Exception e) {
            System.err.println("MySqlLoyaltyConfigRepository.loadVatPercent: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public void saveVatPercent(double vatPercent) {
        String sql = """
                INSERT INTO settings(`key`, `value`) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)
                """;
        double normalized = Math.max(0.0, vatPercent);
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            upsert(ps, "tax.vatPercent", String.valueOf(normalized));
        } catch (Exception e) {
            System.err.println("MySqlLoyaltyConfigRepository.saveVatPercent: " + e.getMessage());
        }
    }

    private void upsert(PreparedStatement ps, String key, String value) throws Exception {
        ps.setString(1, key);
        ps.setString(2, value);
        ps.executeUpdate();
    }

    private long parseLong(String s, long fallback) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return fallback;
        }
    }

    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return fallback;
        }
    }

    private double parseDouble(String s, double fallback) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return fallback;
        }
    }
}
