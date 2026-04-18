package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;

public interface LoyaltyConfigRepository {
    LoyaltyConfig load();
    void save(LoyaltyConfig config);

    default double loadVatPercent() {
        return 0.0;
    }

    default void saveVatPercent(double vatPercent) {
        // optional for implementations that do not persist VAT
    }
}
