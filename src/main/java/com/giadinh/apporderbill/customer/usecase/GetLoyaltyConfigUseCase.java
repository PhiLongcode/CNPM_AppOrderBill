package com.giadinh.apporderbill.customer.usecase;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.repository.LoyaltyConfigRepository;

public class GetLoyaltyConfigUseCase {
    private final LoyaltyConfigRepository repository;

    public GetLoyaltyConfigUseCase(LoyaltyConfigRepository repository) {
        this.repository = repository;
    }

    public LoyaltyConfig execute() {
        if (repository == null) {
            return LoyaltyConfig.defaults();
        }
        return repository.load();
    }
}
