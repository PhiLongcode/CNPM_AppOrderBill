package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;

public interface LoyaltyConfigRepository {
    LoyaltyConfig load();
    void save(LoyaltyConfig config);
}
