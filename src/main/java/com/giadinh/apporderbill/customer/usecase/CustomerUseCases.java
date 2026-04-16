package com.giadinh.apporderbill.customer.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.model.PointTransaction;
import com.giadinh.apporderbill.customer.repository.CustomerRepository;
import com.giadinh.apporderbill.customer.repository.PointTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerUseCases {
    private final CustomerRepository repository;
    private PointTransactionRepository pointTransactionRepository;
    private LoyaltyConfig loyaltyConfig;

    public CustomerUseCases(CustomerRepository repository) {
        this.repository = repository;
        this.loyaltyConfig = LoyaltyConfig.defaults();
    }

    public void setLoyaltyConfig(LoyaltyConfig loyaltyConfig) {
        this.loyaltyConfig = loyaltyConfig != null ? loyaltyConfig : LoyaltyConfig.defaults();
    }

    public void setPointTransactionRepository(PointTransactionRepository repo) {
        this.pointTransactionRepository = repo;
    }

    public LoyaltyConfig getLoyaltyConfig() { return loyaltyConfig; }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public List<Customer> getAll(String keyword) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(c -> k.isEmpty()
                        || (c.getName() != null && c.getName().toLowerCase(Locale.ROOT).contains(k))
                        || (c.getPhone() != null && c.getPhone().contains(k)))
                .collect(Collectors.toList());
    }

    public Customer create(String name, String phone, int points) {
        if (phone == null || phone.isBlank()) {
            throw new DomainException(ErrorCode.CUSTOMER_PHONE_REQUIRED);
        }
        if (repository.findByPhone(phone.trim()).isPresent()) {
            throw new DomainException(ErrorCode.CUSTOMER_PHONE_DUPLICATE);
        }
        return repository.save(new Customer(null, name, phone, points));
    }

    public Customer getCustomerById(Long id) {
        if (id == null) return null;
        return repository.findById(id).orElse(null);
    }

    public Optional<Customer> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) return Optional.empty();
        return repository.findByPhone(phone.trim());
    }

    public Customer update(Long id, String name, String phone, int points) {
        Customer existing = repository.findById(id).orElseThrow();
        repository.findByPhone(phone).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new DomainException(ErrorCode.CUSTOMER_PHONE_DUPLICATE);
            }
        });
        existing.setName(name);
        existing.setPhone(phone);
        existing.setPoints(points);
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    // ─── Lookup ──────────────────────────────────────────────────────────────



    /**
     * Tạo mới hoặc lấy khách hàng theo SĐT.
     * Nếu chưa có, tạo khách mới với tên mặc định.
     */
    public Customer createOrGet(String phone, String name) {
        if (phone == null || phone.isBlank()) return null;
        return repository.findByPhone(phone.trim())
                .orElseGet(() -> repository.save(new Customer(
                        null,
                        name != null && !name.isBlank() ? name : "Khách " + phone.trim(),
                        phone.trim(), 0)));
    }

    // ─── Points ──────────────────────────────────────────────────────────────

    /**
     * Cộng điểm sau khi thanh toán. Ghi lịch sử EARN.
     * orderId: để trace back về đơn hàng (nullable).
     */
    public Customer addPointsByPhone(String phone, int pointsToAdd) {
        return addPointsByPhone(phone, pointsToAdd, null);
    }

    public Customer addPointsByPhone(String phone, int pointsToAdd, String orderId) {
        if (phone == null || phone.isBlank() || pointsToAdd <= 0) {
            return null;
        }
        Customer customer = repository.findByPhone(phone.trim())
                .orElseGet(() -> repository.save(
                        new Customer(null, "Khách " + phone.trim(), phone.trim(), 0)));
        return addPoints(customer.getId(), pointsToAdd, orderId);
    }

    public Customer addPoints(Long customerId, int pointsToAdd, String orderId) {
        if (customerId == null || pointsToAdd <= 0) return null;
        Customer customer = repository.findById(customerId).orElse(null);
        if (customer == null) return null;

        int before = customer.getPoints();
        customer.setPoints(before + pointsToAdd);
        Customer saved = repository.save(customer);
        logTransaction(saved.getId(), pointsToAdd, saved.getPoints(),
                PointTransaction.Type.EARN,
                String.format("Tích điểm thanh toán (+%d điểm)", pointsToAdd),
                orderId);
        return saved;
    }

    /**
     * Đổi điểm lấy giảm giá. Trả về số tiền giảm giá (VNĐ). Ghi lịch sử REDEEM.
     * Nếu khách không đủ điểm, ném exception.
     */
    public long redeemPoints(String phone, int pointsToRedeem) {
        return redeemPoints(phone, pointsToRedeem, null);
    }

    public long redeemPoints(String phone, int pointsToRedeem, String orderId) {
        if (phone == null || phone.isBlank() || pointsToRedeem <= 0) return 0L;
        Customer customer = repository.findByPhone(phone.trim())
                .orElseThrow(() -> new DomainException(ErrorCode.CUSTOMER_NOT_FOUND));
        return redeemPoints(customer.getId(), pointsToRedeem, orderId);
    }

    public long redeemPoints(Long customerId, int pointsToRedeem, String orderId) {
        if (customerId == null || pointsToRedeem <= 0) return 0L;
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new DomainException(ErrorCode.CUSTOMER_NOT_FOUND));
        if (customer.getPoints() < pointsToRedeem) {
            throw new DomainException(ErrorCode.CUSTOMER_INSUFFICIENT_POINTS);
        }
        long discount = loyaltyConfig.calcRedeemDiscount(pointsToRedeem);
        customer.setPoints(customer.getPoints() - pointsToRedeem);
        Customer saved = repository.save(customer);
        logTransaction(saved.getId(), -pointsToRedeem, saved.getPoints(),
                PointTransaction.Type.REDEEM,
                String.format("Đổi %d điểm → giảm %,d VNĐ", pointsToRedeem, discount),
                orderId);
        return discount;
    }

    /** Lịch sử điểm của khách */
    public List<PointTransaction> getPointHistory(Long customerId) {
        if (pointTransactionRepository == null) return List.of();
        return pointTransactionRepository.findByCustomerId(customerId);
    }

    // ─── Internal ────────────────────────────────────────────────────────────

    private void logTransaction(Long customerId, int delta, int balanceAfter,
                                PointTransaction.Type type, String note, String orderId) {
        if (pointTransactionRepository == null) return;
        PointTransaction tx = new PointTransaction(
                null, customerId, delta, balanceAfter,
                type, note, orderId, LocalDateTime.now());
        pointTransactionRepository.save(tx);
    }
}

