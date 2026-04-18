package com.giadinh.apporderbill.customer.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.model.LoyaltyGift;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;
import com.giadinh.apporderbill.customer.model.PointTransaction;
import com.giadinh.apporderbill.customer.repository.CustomerRepository;
import com.giadinh.apporderbill.customer.repository.LoyaltyConfigRepository;
import com.giadinh.apporderbill.customer.repository.LoyaltyGiftRepository;
import com.giadinh.apporderbill.customer.repository.LoyaltyRedeemMenuItemRepository;
import com.giadinh.apporderbill.customer.repository.PointTransactionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerUseCases {
    private final CustomerRepository repository;
    private PointTransactionRepository pointTransactionRepository;
    private LoyaltyConfigRepository loyaltyConfigRepository;
    private LoyaltyRedeemMenuItemRepository loyaltyRedeemMenuItemRepository;
    private LoyaltyGiftRepository loyaltyGiftRepository;
    private LoyaltyConfig loyaltyConfig;
    private final BTreePhonePrefixIndex phonePrefixIndex = new BTreePhonePrefixIndex();
    private boolean phoneIndexDirty = true;

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

    public void setLoyaltyConfigRepository(LoyaltyConfigRepository loyaltyConfigRepository) {
        this.loyaltyConfigRepository = loyaltyConfigRepository;
    }

    public void setLoyaltyRedeemMenuItemRepository(LoyaltyRedeemMenuItemRepository loyaltyRedeemMenuItemRepository) {
        this.loyaltyRedeemMenuItemRepository = loyaltyRedeemMenuItemRepository;
    }

    public void setLoyaltyGiftRepository(LoyaltyGiftRepository loyaltyGiftRepository) {
        this.loyaltyGiftRepository = loyaltyGiftRepository;
    }

    public LoyaltyConfig getLoyaltyConfig() { return loyaltyConfig; }

    public LoyaltyConfig reloadLoyaltyConfig() {
        if (loyaltyConfigRepository == null) {
            this.loyaltyConfig = LoyaltyConfig.defaults();
            return this.loyaltyConfig;
        }
        this.loyaltyConfig = new GetLoyaltyConfigUseCase(loyaltyConfigRepository).execute();
        return this.loyaltyConfig;
    }

    public LoyaltyConfig updateLoyaltyConfig(LoyaltyConfig config) {
        LoyaltyConfig updated = new UpdateLoyaltyConfigUseCase(loyaltyConfigRepository).execute(config);
        this.loyaltyConfig = updated;
        return updated;
    }

    public double getVatPercent() {
        if (loyaltyConfigRepository == null) {
            return 0.0;
        }
        return Math.max(0.0, loyaltyConfigRepository.loadVatPercent());
    }

    public void updateVatPercent(double vatPercent) {
        if (loyaltyConfigRepository == null) {
            return;
        }
        loyaltyConfigRepository.saveVatPercent(Math.max(0.0, vatPercent));
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public List<Customer> getAll(String keyword) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(c -> k.isEmpty()
                        || (c.getName() != null && c.getName().toLowerCase(Locale.ROOT).contains(k))
                        || (c.getPhone() != null && c.getPhone().contains(k)))
                .collect(Collectors.toList());
    }

    /**
     * Search by phone with B-Tree index.
     * Query starts only when user enters at least 4 digits.
     */
    public List<Customer> searchByPhonePrefixBTree(String input) {
        String normalized = normalizePhone(input);
        if (normalized.length() < 4) {
            return List.of();
        }
        rebuildPhonePrefixIndexIfNeeded();
        String bucket = normalized.substring(0, 4);
        List<Customer> candidates = phonePrefixIndex.search(bucket);
        if (candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .filter(c -> normalizePhone(c.getPhone()).startsWith(normalized))
                .collect(Collectors.toList());
    }

    public Customer create(String name, String phone, int points) {
        if (phone == null || phone.isBlank()) {
            throw new DomainException(ErrorCode.CUSTOMER_PHONE_REQUIRED);
        }
        if (repository.findByPhone(phone.trim()).isPresent()) {
            throw new DomainException(ErrorCode.CUSTOMER_PHONE_DUPLICATE);
        }
        Customer saved = repository.save(new Customer(null, name, phone, points));
        phoneIndexDirty = true;
        return saved;
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
        Customer saved = repository.save(existing);
        phoneIndexDirty = true;
        return saved;
    }

    public void delete(Long id) {
        repository.delete(id);
        phoneIndexDirty = true;
    }

    // ─── Lookup ──────────────────────────────────────────────────────────────



    /**
     * Tạo mới hoặc lấy khách hàng theo SĐT.
     * Nếu chưa có, tạo khách mới với tên mặc định.
     */
    public Customer createOrGet(String phone, String name) {
        if (phone == null || phone.isBlank()) return null;
        return repository.findByPhone(phone.trim())
                .orElseGet(() -> {
                    Customer created = repository.save(new Customer(
                            null,
                            name != null && !name.isBlank() ? name : "Khách " + phone.trim(),
                            phone.trim(), 0));
                    phoneIndexDirty = true;
                    return created;
                });
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

    /**
     * Tr�� điểm khi đ��i món (không quy đ��i tiền trên bill). Ghi {@link PointTransaction.Type#REDEEM_DISH}.
     */
    /**
     * Redeem points for a dish line (no bill discount). Logs {@link PointTransaction.Type#REDEEM_DISH}.
     */
    public void redeemPointsForDish(Long customerId, int pointsCost, String dishLabel, String orderId) {
        if (pointsCost <= 0) {
            return;
        }
        String note = String.format("Doi %d diem -> mon: %s", pointsCost, dishLabel == null ? "" : dishLabel);
        deductPointsForRedeem(customerId, pointsCost, note, orderId, PointTransaction.Type.REDEEM_DISH);
    }

    /**
     * Redeem points for a non-monetary gift. Logs {@link PointTransaction.Type#REDEEM_GIFT}.
     */
    public void redeemPointsForGift(Long customerId, int pointsCost, String giftName, String orderId) {
        if (pointsCost <= 0) {
            return;
        }
        String note = String.format("Doi %d diem -> qua: %s", pointsCost, giftName == null ? "" : giftName);
        deductPointsForRedeem(customerId, pointsCost, note, orderId, PointTransaction.Type.REDEEM_GIFT);
    }

    public List<LoyaltyRedeemMenuItem> listActiveLoyaltyRedeemDishes() {
        if (loyaltyRedeemMenuItemRepository == null) {
            return List.of();
        }
        return loyaltyRedeemMenuItemRepository.findAllActive();
    }

    public List<LoyaltyGift> listActiveLoyaltyGifts() {
        if (loyaltyGiftRepository == null) {
            return List.of();
        }
        return loyaltyGiftRepository.findAllActive();
    }

    public boolean isLoyaltyCatalogPersistenceAvailable() {
        return loyaltyRedeemMenuItemRepository != null && loyaltyGiftRepository != null;
    }

    public List<LoyaltyRedeemMenuItem> listAllLoyaltyRedeemMenuCatalog() {
        if (loyaltyRedeemMenuItemRepository == null) {
            return List.of();
        }
        return loyaltyRedeemMenuItemRepository.findAll();
    }

    public List<LoyaltyGift> listAllLoyaltyGiftsCatalog() {
        if (loyaltyGiftRepository == null) {
            return List.of();
        }
        return loyaltyGiftRepository.findAll();
    }

    public void saveLoyaltyRedeemMenuCatalogRow(LoyaltyRedeemMenuItem row) {
        if (loyaltyRedeemMenuItemRepository == null) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR);
        }
        if (row == null || row.getMenuItemId() <= 0 || row.getPointsCost() <= 0) {
            throw new DomainException(ErrorCode.COMMON_VALIDATION_FAILED);
        }
        loyaltyRedeemMenuItemRepository.save(row);
    }

    public void deleteLoyaltyRedeemMenuCatalogRow(long id) {
        if (loyaltyRedeemMenuItemRepository == null) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR);
        }
        loyaltyRedeemMenuItemRepository.delete(id);
    }

    public void saveLoyaltyGiftCatalogRow(LoyaltyGift gift) {
        if (loyaltyGiftRepository == null) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR);
        }
        if (gift == null || gift.getName() == null || gift.getName().isBlank() || gift.getPointsCost() <= 0) {
            throw new DomainException(ErrorCode.COMMON_VALIDATION_FAILED);
        }
        loyaltyGiftRepository.save(gift);
    }

    public void deleteLoyaltyGiftCatalogRow(long id) {
        if (loyaltyGiftRepository == null) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR);
        }
        loyaltyGiftRepository.delete(id);
    }

    private void deductPointsForRedeem(Long customerId, int pointsCost, String note, String orderId,
            PointTransaction.Type type) {
        if (customerId == null) {
            throw new DomainException(ErrorCode.LOYALTY_REDEEM_REQUIRES_CUSTOMER);
        }
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new DomainException(ErrorCode.CUSTOMER_NOT_FOUND));
        if (customer.getPoints() < pointsCost) {
            throw new DomainException(ErrorCode.CUSTOMER_INSUFFICIENT_POINTS);
        }
        customer.setPoints(customer.getPoints() - pointsCost);
        Customer saved = repository.save(customer);
        logTransaction(saved.getId(), -pointsCost, saved.getPoints(), type, note, orderId);
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

    private void rebuildPhonePrefixIndexIfNeeded() {
        if (!phoneIndexDirty) {
            return;
        }
        phonePrefixIndex.clear();
        for (Customer customer : repository.findAll()) {
            String phone = normalizePhone(customer.getPhone());
            if (phone.length() >= 4) {
                phonePrefixIndex.insert(phone.substring(0, 4), customer);
            }
        }
        phoneIndexDirty = false;
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return "";
        }
        return phone.replaceAll("\\D", "");
    }

    private static class BTreePhonePrefixIndex {
        private final BTreeMap tree = new BTreeMap();

        void insert(String prefix4, Customer customer) {
            List<Customer> current = tree.search(prefix4);
            if (current == null) {
                List<Customer> customers = new ArrayList<>();
                customers.add(customer);
                tree.insert(prefix4, customers);
                return;
            }
            current.add(customer);
        }

        List<Customer> search(String prefix4) {
            List<Customer> found = tree.search(prefix4);
            return found == null ? List.of() : found;
        }

        void clear() {
            tree.clear();
        }
    }

    /**
     * Minimal B-Tree map for String key lookups (exact match).
     * Used to group customers by the first 4 phone digits.
     */
    private static class BTreeMap {
        private static final int MIN_DEGREE = 3;
        private BTreeNode root = new BTreeNode(true);

        void clear() {
            root = new BTreeNode(true);
        }

        List<Customer> search(String key) {
            return search(root, key);
        }

        void insert(String key, List<Customer> value) {
            if (root.isFull()) {
                BTreeNode newRoot = new BTreeNode(false);
                newRoot.children.add(root);
                splitChild(newRoot, 0);
                root = newRoot;
            }
            insertNonFull(root, key, value);
        }

        private List<Customer> search(BTreeNode node, String key) {
            int i = 0;
            while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
                i++;
            }
            if (i < node.keys.size() && key.equals(node.keys.get(i))) {
                return node.values.get(i);
            }
            if (node.leaf) {
                return null;
            }
            return search(node.children.get(i), key);
        }

        private void insertNonFull(BTreeNode node, String key, List<Customer> value) {
            int i = node.keys.size() - 1;
            if (node.leaf) {
                while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                    i--;
                }
                int insertPos = i + 1;
                if (insertPos < node.keys.size() && key.equals(node.keys.get(insertPos))) {
                    node.values.set(insertPos, value);
                    return;
                }
                node.keys.add(insertPos, key);
                node.values.add(insertPos, value);
                return;
            }

            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) {
                i--;
            }
            int childIndex = i + 1;
            BTreeNode child = node.children.get(childIndex);
            if (child.isFull()) {
                splitChild(node, childIndex);
                if (key.compareTo(node.keys.get(childIndex)) > 0) {
                    childIndex++;
                }
            }
            insertNonFull(node.children.get(childIndex), key, value);
        }

        private void splitChild(BTreeNode parent, int childIndex) {
            BTreeNode fullChild = parent.children.get(childIndex);
            BTreeNode rightChild = new BTreeNode(fullChild.leaf);

            int medianIndex = MIN_DEGREE - 1;
            String medianKey = fullChild.keys.get(medianIndex);
            List<Customer> medianValue = fullChild.values.get(medianIndex);

            for (int j = medianIndex + 1; j < fullChild.keys.size(); j++) {
                rightChild.keys.add(fullChild.keys.get(j));
                rightChild.values.add(fullChild.values.get(j));
            }

            if (!fullChild.leaf) {
                for (int j = MIN_DEGREE; j < fullChild.children.size(); j++) {
                    rightChild.children.add(fullChild.children.get(j));
                }
            }

            while (fullChild.keys.size() > medianIndex) {
                fullChild.keys.remove(fullChild.keys.size() - 1);
                fullChild.values.remove(fullChild.values.size() - 1);
            }
            if (!fullChild.leaf) {
                while (fullChild.children.size() > MIN_DEGREE) {
                    fullChild.children.remove(fullChild.children.size() - 1);
                }
            }

            parent.keys.add(childIndex, medianKey);
            parent.values.add(childIndex, medianValue);
            parent.children.add(childIndex + 1, rightChild);
        }

        private static class BTreeNode {
            private final boolean leaf;
            private final List<String> keys = new ArrayList<>();
            private final List<List<Customer>> values = new ArrayList<>();
            private final List<BTreeNode> children = new ArrayList<>();

            private BTreeNode(boolean leaf) {
                this.leaf = leaf;
            }

            private boolean isFull() {
                return keys.size() == 2 * MIN_DEGREE - 1;
            }
        }
    }
}

