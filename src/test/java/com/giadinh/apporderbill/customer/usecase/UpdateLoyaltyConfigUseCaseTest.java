package com.giadinh.apporderbill.customer.usecase;

import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.repository.LoyaltyConfigRepository;
import com.giadinh.apporderbill.shared.error.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateLoyaltyConfigUseCaseTest {

    @Test
    void shouldSaveWhenConfigValid() {
        InMemoryRepo repo = new InMemoryRepo();
        UpdateLoyaltyConfigUseCase useCase = new UpdateLoyaltyConfigUseCase(repo);

        LoyaltyConfig updated = useCase.execute(new LoyaltyConfig(10_000, 1, 100, 5_000));

        assertNotNull(updated);
        assertEquals(10_000, repo.saved.getEarnUnitAmount());
    }

    @Test
    void shouldThrowWhenAnyValueNotPositive() {
        InMemoryRepo repo = new InMemoryRepo();
        UpdateLoyaltyConfigUseCase useCase = new UpdateLoyaltyConfigUseCase(repo);

        assertThrows(DomainException.class, () -> useCase.execute(new LoyaltyConfig(0, 1, 1, 1)));
        assertThrows(DomainException.class, () -> useCase.execute(new LoyaltyConfig(1, 0, 1, 1)));
        assertThrows(DomainException.class, () -> useCase.execute(new LoyaltyConfig(1, 1, 0, 1)));
        assertThrows(DomainException.class, () -> useCase.execute(new LoyaltyConfig(1, 1, 1, 0)));
    }

    private static class InMemoryRepo implements LoyaltyConfigRepository {
        LoyaltyConfig saved = LoyaltyConfig.defaults();

        @Override
        public LoyaltyConfig load() {
            return saved;
        }

        @Override
        public void save(LoyaltyConfig config) {
            this.saved = config;
        }
    }
}
