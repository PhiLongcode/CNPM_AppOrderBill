package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqliteLoyaltyConfigRepository {

    private final SqliteConnectionProvider connectionProvider;

    public SqliteLoyaltyConfigRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public LoyaltyConfig load() {
        LoyaltyConfig config = LoyaltyConfig.defaults();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT key, value FROM settings WHERE key LIKE 'loyalty.%'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("key");
                String value = rs.getString("value");
                switch (key) {
                    case "loyalty.earnUnitAmount" -> config.setEarnUnitAmount(parseLong(value, 10_000L));
                    case "loyalty.pointsPerUnit" -> config.setPointsPerUnit(parseInt(value, 1));
                    case "loyalty.redeemPointsRequired" -> config.setRedeemPointsRequired(parseInt(value, 100));
                    case "loyalty.redeemValue" -> config.setRedeemValue(parseLong(value, 5_000L));
                }
            }
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyConfigRepository.load: " + e.getMessage());
        }
        return config;
    }

    public void save(LoyaltyConfig config) {
        String sql = "INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            upsert(ps, "loyalty.earnUnitAmount", String.valueOf(config.getEarnUnitAmount()));
            upsert(ps, "loyalty.pointsPerUnit", String.valueOf(config.getPointsPerUnit()));
            upsert(ps, "loyalty.redeemPointsRequired", String.valueOf(config.getRedeemPointsRequired()));
            upsert(ps, "loyalty.redeemValue", String.valueOf(config.getRedeemValue()));
        } catch (Exception e) {
            System.err.println("SqliteLoyaltyConfigRepository.save: " + e.getMessage());
        }
    }

    private void upsert(PreparedStatement ps, String key, String value) throws Exception {
        ps.setString(1, key);
        ps.setString(2, value);
        ps.executeUpdate();
    }

    private long parseLong(String s, long fallback) {
        try { return Long.parseLong(s); } catch (Exception e) { return fallback; }
    }

    private int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }
}
