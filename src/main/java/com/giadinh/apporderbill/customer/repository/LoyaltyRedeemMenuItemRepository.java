package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;

import java.util.List;
import java.util.Optional;

public interface LoyaltyRedeemMenuItemRepository {
    List<LoyaltyRedeemMenuItem> findAllActive();
    List<LoyaltyRedeemMenuItem> findAll();
    Optional<LoyaltyRedeemMenuItem> findById(long id);
    void save(LoyaltyRedeemMenuItem row);
    void delete(long id);
}
