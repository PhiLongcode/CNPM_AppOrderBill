package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyGift;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMode;
import com.giadinh.apporderbill.customer.repository.CustomerRepository;
import com.giadinh.apporderbill.customer.repository.LoyaltyGiftRepository;
import com.giadinh.apporderbill.customer.repository.LoyaltyRedeemMenuItemRepository;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderInput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.repository.TableRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutOrderLoyaltyModesTest {

    @Test
    void billDiscount_redeemsPointsAndReducesFinalAmountOnPayment() {
        long orderId = 11_001L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 500));

        Order order = new Order(String.valueOf(orderId), "B1", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon A", 1, 100_000));
        orderRepo.save(order);

        CustomerUseCases customerUseCases = new CustomerUseCases(customerRepo);
        CalculateOrderTotalUseCase calc = new CalculateOrderTotalUseCase(orderRepo);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, customerUseCases, calc, null, null, null);

        // 100 points -> 5_000 VND off (LoyaltyConfig.defaults)
        useCase.execute(new CheckoutOrderInput(
                orderId, 95_000L, "CASH", null, null, "tester", 1L, 100,
                LoyaltyRedeemMode.BILL_DISCOUNT, null, null));

        assertEquals(1, paymentRepo.saved.size());
        Payment p = paymentRepo.saved.get(0);
        assertEquals(5_000L, p.getPointsDiscountAmount());
        assertEquals(95_000L, p.getFinalAmount());

        Customer after = customerRepo.findById(1L).orElseThrow();
        // 500 - 100 redeem + 9 earn on 95_000 final (10_000 VND / point)
        assertEquals(409, after.getPoints());
    }

    @Test
    void legacyPointsOnly_infersBillDiscountMode() {
        long orderId = 11_002L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 200));

        Order order = new Order(String.valueOf(orderId), "B2", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon B", 1, 50_000));
        orderRepo.save(order);

        CustomerUseCases customerUseCases = new CustomerUseCases(customerRepo);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, customerUseCases,
                new CalculateOrderTotalUseCase(orderRepo), null, null, null);

        CheckoutOrderInput input = new CheckoutOrderInput(
                orderId, 45_000L, "CASH", null, null, "tester", 1L, 100);
        assertEquals(LoyaltyRedeemMode.BILL_DISCOUNT, input.resolveLoyaltyRedeemMode());

        useCase.execute(input);
        assertEquals(5_000L, paymentRepo.saved.get(0).getPointsDiscountAmount());
    }

    @Test
    void dishRedeem_addsFreeLineAndDeductsPoints() {
        long orderId = 11_003L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 200));

        Order order = new Order(String.valueOf(orderId), "B3", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon C", 1, 80_000));
        orderRepo.save(order);

        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putUntracked(10, "Mon Doi", 40_000);
        InMemoryLoyaltyRedeemMenuRepository redeemRepo = new InMemoryLoyaltyRedeemMenuRepository();
        redeemRepo.put(new LoyaltyRedeemMenuItem(77L, 10, 50, true));

        CustomerUseCases customerUseCases = new CustomerUseCases(customerRepo);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, customerUseCases,
                new CalculateOrderTotalUseCase(orderRepo), menuRepo, redeemRepo, null);

        useCase.execute(new CheckoutOrderInput(
                orderId, 80_000L, "CASH", null, null, "tester", 1L, 0,
                LoyaltyRedeemMode.DISH, 77L, null));

        Order completed = orderRepo.findById(String.valueOf(orderId)).orElseThrow();
        assertEquals(2, completed.getItems().size());
        assertTrue(completed.getItems().stream().anyMatch(i ->
                i.getMenuItemName() != null && i.getMenuItemName().contains("Doi diem")));
        assertEquals(0.0, completed.getItems().get(completed.getItems().size() - 1).getPrice(), 0.001);

        Customer after = customerRepo.findById(1L).orElseThrow();
        // 200 - 50 dish + 8 earn on 80_000
        assertEquals(158, after.getPoints());
        assertEquals(0L, paymentRepo.saved.get(0).getPointsDiscountAmount());
    }

    @Test
    void dishRedeem_failsWhenStockInsufficient() {
        long orderId = 11_004L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 200));

        Order order = new Order(String.valueOf(orderId), "B4", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon D", 1, 50_000));
        orderRepo.save(order);

        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(20, "Het hang", 30_000, 0);
        InMemoryLoyaltyRedeemMenuRepository redeemRepo = new InMemoryLoyaltyRedeemMenuRepository();
        redeemRepo.put(new LoyaltyRedeemMenuItem(88L, 20, 10, true));

        CustomerUseCases customerUseCases = new CustomerUseCases(customerRepo);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, customerUseCases,
                new CalculateOrderTotalUseCase(orderRepo), menuRepo, redeemRepo, null);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                new CheckoutOrderInput(orderId, 50_000L, "CASH", null, null, "tester", 1L, 0,
                        LoyaltyRedeemMode.DISH, 88L, null)));
        assertEquals(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT, ex.getErrorCode());
        assertTrue(paymentRepo.saved.isEmpty());
    }

    @Test
    void dishRedeem_failsWhenCatalogInactive() {
        long orderId = 11_005L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 200));

        Order order = new Order(String.valueOf(orderId), "B5", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon E", 1, 50_000));
        orderRepo.save(order);

        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putUntracked(30, "Mon F", 25_000);
        InMemoryLoyaltyRedeemMenuRepository redeemRepo = new InMemoryLoyaltyRedeemMenuRepository();
        redeemRepo.put(new LoyaltyRedeemMenuItem(99L, 30, 10, false));

        CustomerUseCases customerUseCases = new CustomerUseCases(customerRepo);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, customerUseCases,
                new CalculateOrderTotalUseCase(orderRepo), menuRepo, redeemRepo, null);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                new CheckoutOrderInput(orderId, 50_000L, "CASH", null, null, "tester", 1L, 0,
                        LoyaltyRedeemMode.DISH, 99L, null)));
        assertEquals(ErrorCode.LOYALTY_REDEEM_CATALOG_INACTIVE, ex.getErrorCode());
    }

    @Test
    void giftRedeem_afterPayment_deductsPoints() {
        long orderId = 11_006L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 500));

        Order order = new Order(String.valueOf(orderId), "B6", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon G", 1, 100_000));
        orderRepo.save(order);

        InMemoryLoyaltyGiftRepository giftRepo = new InMemoryLoyaltyGiftRepository();
        giftRepo.put(new LoyaltyGift(5L, "Nona", 40, true));

        CustomerUseCases customerUseCases = new CustomerUseCases(customerRepo);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, customerUseCases,
                new CalculateOrderTotalUseCase(orderRepo), null, null, giftRepo);

        useCase.execute(new CheckoutOrderInput(
                orderId, 100_000L, "CASH", null, null, "tester", 1L, 0,
                LoyaltyRedeemMode.GIFT_NON_MONETARY, null, 5L));

        assertEquals(0L, paymentRepo.saved.get(0).getPointsDiscountAmount());
        Customer after = customerRepo.findById(1L).orElseThrow();
        // 500 - 40 gift + 10 earn on 100_000
        assertEquals(470, after.getPoints());
    }

    @Test
    void loyaltyRedeem_requiresCustomer() {
        long orderId = 11_007L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();

        Order order = new Order(String.valueOf(orderId), "B7", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon H", 1, 40_000));
        orderRepo.save(order);

        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, new CustomerUseCases(new InMemoryCustomerRepository()),
                new CalculateOrderTotalUseCase(orderRepo), null, null, null);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                new CheckoutOrderInput(orderId, 35_000L, "CASH", null, null, "tester", null, 100,
                        LoyaltyRedeemMode.BILL_DISCOUNT, null, null)));
        assertEquals(ErrorCode.LOYALTY_REDEEM_REQUIRES_CUSTOMER, ex.getErrorCode());
    }

    @Test
    void dishMode_requiresCatalogId() {
        long orderId = 11_008L;
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        InMemoryCustomerRepository customerRepo = new InMemoryCustomerRepository();
        customerRepo.put(new Customer(1L, "Khach", "0900000001", 50));

        Order order = new Order(String.valueOf(orderId), "B8", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(
                order.getOrderId(), "1", "Mon I", 1, 30_000));
        orderRepo.save(order);

        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(
                orderRepo, paymentRepo, tableRepo, new CustomerUseCases(customerRepo),
                new CalculateOrderTotalUseCase(orderRepo), null, null, null);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                new CheckoutOrderInput(orderId, 30_000L, "CASH", null, null, "tester", 1L, 0,
                        LoyaltyRedeemMode.DISH, null, null)));
        assertEquals(ErrorCode.LOYALTY_REDEEM_MODE_INVALID, ex.getErrorCode());
    }

    private static class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, Order> store = new HashMap<>();

        @Override
        public Optional<Order> findById(String orderId) {
            return Optional.ofNullable(store.get(orderId));
        }

        @Override
        public Optional<Order> findByTableIdAndStatusPending(String tableId) {
            return store.values().stream()
                    .filter(o -> Objects.equals(o.getTableId(), tableId)
                            && (o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.IN_PROGRESS))
                    .findFirst();
        }

        @Override
        public void save(Order order) {
            store.put(order.getOrderId(), order);
        }

        @Override
        public void delete(String orderId) {
            store.remove(orderId);
        }

        @Override
        public List<Order> findAllPendingOrders() {
            return store.values().stream().filter(o -> o.getStatus() == OrderStatus.PENDING).toList();
        }
    }

    private static class InMemoryPaymentRepository implements PaymentRepository {
        private final List<Payment> saved = new ArrayList<>();
        private long seq = 1L;

        @Override
        public Payment save(Payment payment) {
            payment.setPaymentId(seq++);
            saved.add(payment);
            return payment;
        }

        @Override
        public Optional<Payment> findById(Long paymentId) {
            return saved.stream().filter(p -> Objects.equals(p.getPaymentId(), paymentId)).findFirst();
        }

        @Override
        public List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
            return saved.stream()
                    .filter(p -> !p.getPaidAt().isBefore(start) && !p.getPaidAt().isAfter(end))
                    .toList();
        }

        @Override
        public void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
            saved.removeIf(p -> !p.getPaidAt().isBefore(start) && !p.getPaidAt().isAfter(end));
        }
    }

    private static class InMemoryTableRepository implements TableRepository {
        private final Map<String, Table> tables = new HashMap<>();

        @Override
        public Optional<Table> findById(String tableId) {
            return Optional.ofNullable(tables.get(tableId));
        }

        @Override
        public Optional<Table> findByTableName(String tableName) {
            return tables.values().stream().filter(t -> Objects.equals(t.getTableName(), tableName)).findFirst();
        }

        @Override
        public List<Table> findAll() {
            return new ArrayList<>(tables.values());
        }

        @Override
        public void save(Table table) {
            tables.put(table.getTableId(), table);
        }

        @Override
        public void delete(String tableId) {
            tables.remove(tableId);
        }
    }

    private static class InMemoryCustomerRepository implements CustomerRepository {
        private final Map<Long, Customer> byId = new HashMap<>();
        private long seq = 1L;

        void put(Customer c) {
            byId.put(c.getId(), c);
        }

        @Override
        public List<Customer> findAll() {
            return new ArrayList<>(byId.values());
        }

        @Override
        public Optional<Customer> findById(Long id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<Customer> findByPhone(String phone) {
            if (phone == null) {
                return Optional.empty();
            }
            return byId.values().stream().filter(c -> phone.equals(c.getPhone())).findFirst();
        }

        @Override
        public Customer save(Customer customer) {
            if (customer.getId() == null) {
                Customer withId = new Customer(seq++, customer.getName(), customer.getPhone(), customer.getPoints());
                byId.put(withId.getId(), withId);
                return withId;
            }
            byId.put(customer.getId(), customer);
            return customer;
        }

        @Override
        public void delete(Long id) {
            byId.remove(id);
        }
    }

    private static class InMemoryMenuRepository implements MenuItemRepository {
        private final Map<Integer, MenuItem> items = new HashMap<>();

        void putTrackedItem(int id, String name, long unitPrice, int stockQty) {
            items.put(id, new MenuItem(id, name, unitPrice, "Food", null, true, stockQty, 0, 100, "phan",
                    MenuItemStatus.ACTIVE));
        }

        void putUntracked(int id, String name, long unitPrice) {
            items.put(id, new MenuItem(id, name, unitPrice, "Food", null, false, 0, 0, 100, "phan",
                    MenuItemStatus.ACTIVE));
        }

        @Override
        public Optional<MenuItem> findById(int id) {
            return Optional.ofNullable(items.get(id));
        }

        @Override
        public Optional<MenuItem> findByName(String name) {
            return items.values().stream().filter(i -> i.getName().equals(name)).findFirst();
        }

        @Override
        public List<MenuItem> findAll() {
            return new ArrayList<>(items.values());
        }

        @Override
        public List<MenuItem> findByCategoryName(String categoryName) {
            return List.of();
        }

        @Override
        public List<MenuItem> findByStatus(MenuItemStatus status) {
            return List.of();
        }

        @Override
        public void save(MenuItem menuItem) {
            items.put(menuItem.getId(), menuItem);
        }

        @Override
        public boolean decreaseStockAtomic(int id, int quantity) {
            MenuItem item = items.get(id);
            if (item == null || !item.isStockTracked() || item.getCurrentStockQuantity() < quantity) {
                return false;
            }
            item.updateStock(item.getCurrentStockQuantity() - quantity);
            return true;
        }

        @Override
        public boolean increaseStockAtomic(int id, int quantity) {
            MenuItem item = items.get(id);
            if (item == null || !item.isStockTracked()) {
                return false;
            }
            item.updateStock(item.getCurrentStockQuantity() + quantity);
            return true;
        }

        @Override
        public void delete(int id) {
            items.remove(id);
        }
    }

    private static class InMemoryLoyaltyRedeemMenuRepository implements LoyaltyRedeemMenuItemRepository {
        private final Map<Long, LoyaltyRedeemMenuItem> rows = new HashMap<>();

        void put(LoyaltyRedeemMenuItem row) {
            rows.put(row.getId(), row);
        }

        @Override
        public List<LoyaltyRedeemMenuItem> findAllActive() {
            return rows.values().stream().filter(LoyaltyRedeemMenuItem::isActive).toList();
        }

        @Override
        public List<LoyaltyRedeemMenuItem> findAll() {
            return new ArrayList<>(rows.values());
        }

        @Override
        public Optional<LoyaltyRedeemMenuItem> findById(long id) {
            return Optional.ofNullable(rows.get(id));
        }

        @Override
        public void save(LoyaltyRedeemMenuItem row) {
            rows.put(row.getId(), row);
        }

        @Override
        public void delete(long id) {
            rows.remove(id);
        }
    }

    private static class InMemoryLoyaltyGiftRepository implements LoyaltyGiftRepository {
        private final Map<Long, LoyaltyGift> rows = new HashMap<>();

        void put(LoyaltyGift g) {
            rows.put(g.getId(), g);
        }

        @Override
        public List<LoyaltyGift> findAllActive() {
            return rows.values().stream().filter(LoyaltyGift::isActive).toList();
        }

        @Override
        public List<LoyaltyGift> findAll() {
            return new ArrayList<>(rows.values());
        }

        @Override
        public Optional<LoyaltyGift> findById(long id) {
            return Optional.ofNullable(rows.get(id));
        }

        @Override
        public void save(LoyaltyGift gift) {
            rows.put(gift.getId(), gift);
        }

        @Override
        public void delete(long id) {
            rows.remove(id);
        }
    }
}
