package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.LoyaltyGift;

import java.util.List;
import java.util.Optional;

public interface LoyaltyGiftRepository {
    List<LoyaltyGift> findAllActive();
    List<LoyaltyGift> findAll();
    Optional<LoyaltyGift> findById(long id);
    void save(LoyaltyGift gift);
    void delete(long id);
}
