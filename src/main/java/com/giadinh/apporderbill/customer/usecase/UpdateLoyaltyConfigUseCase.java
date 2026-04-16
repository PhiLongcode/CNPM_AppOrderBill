package com.giadinh.apporderbill.customer.usecase;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.repository.LoyaltyConfigRepository;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

public class UpdateLoyaltyConfigUseCase {
    private final LoyaltyConfigRepository repository;

    public UpdateLoyaltyConfigUseCase(LoyaltyConfigRepository repository) {
        this.repository = repository;
    }

    public LoyaltyConfig execute(LoyaltyConfig input) {
        validate(input);
        if (repository != null) {
            repository.save(input);
        }
        return input;
    }

    private void validate(LoyaltyConfig input) {
        if (input == null) {
            throw new DomainException(ErrorCode.LOYALTY_CONFIG_INVALID);
        }
        if (input.getEarnUnitAmount() <= 0
                || input.getPointsPerUnit() <= 0
                || input.getRedeemPointsRequired() <= 0
                || input.getRedeemValue() <= 0) {
            throw new DomainException(ErrorCode.LOYALTY_CONFIG_INVALID);
        }
    }
}
