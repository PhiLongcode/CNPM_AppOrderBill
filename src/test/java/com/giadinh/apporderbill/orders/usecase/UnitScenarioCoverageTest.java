package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.service.ConfigurableOrderCodeService;
import com.giadinh.apporderbill.orders.usecase.dto.*;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.model.TableStatus;
import com.giadinh.apporderbill.table.repository.TableRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UnitScenarioCoverageTest {

    @Test
    void openOrCreateOrder_createsPendingOrder_withOrderCode() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        OpenOrCreateOrderUseCase useCase = new OpenOrCreateOrderUseCase(orderRepo, null, new ConfigurableOrderCodeService());

        var output = useCase.execute(new OpenOrCreateOrderInput("Bàn 1", "tester"));
        Order created = orderRepo.findById(String.valueOf(output.getOrderId())).orElseThrow();

        assertEquals(OrderStatus.PENDING, created.getStatus());
        assertNotNull(created.getOrderCode());
        assertTrue(created.getOrderCode().startsWith("ORDER"));
    }

    @Test
    void addMenuItem_failsWhenStockInsufficient() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        Order order = new Order("2001", "Bàn 3", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        menuRepo.putTrackedItem(10, "Bia", 15000, 1);
        AddMenuItemToOrderUseCase useCase = new AddMenuItemToOrderUseCase(orderRepo, menuRepo);

        useCase.execute(new AddMenuItemInput(2001L, 10L, 1, null));
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new AddMenuItemInput(2001L, 10L, 1, null)));

        assertEquals(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT, ex.getErrorCode());
    }

    @Test
    void updateQuantity_increaseAndDecrease_adjustsStock() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(20, "Cơm", 30000, 10);
        Order order = new Order("3001", "Bàn 4", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);

        AddMenuItemToOrderUseCase addUseCase = new AddMenuItemToOrderUseCase(orderRepo, menuRepo);
        addUseCase.execute(new AddMenuItemInput(3001L, 20L, 2, null));

        UpdateOrderItemQuantityUseCase updateUseCase = new UpdateOrderItemQuantityUseCase(orderRepo, menuRepo);
        updateUseCase.execute(new UpdateOrderItemQuantityInput(3001L, 1L, 4));
        assertEquals(6, menuRepo.stockOf(20));

        updateUseCase.execute(new UpdateOrderItemQuantityInput(3001L, 1L, 1));
        assertEquals(9, menuRepo.stockOf(20));
    }

    @Test
    void calculateOrderTotal_withDiscountAmountAndPercent() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("7001", "Bàn 8", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Bún", 2, 50000));
        orderRepo.save(order);
        CalculateOrderTotalUseCase useCase = new CalculateOrderTotalUseCase(orderRepo);

        var output = useCase.execute(new CalculateOrderTotalInput(7001L, 10000L, 10.0));
        assertEquals(100000L, output.getSubtotal());
        assertEquals(20000L, output.getDiscountAmount());
        assertEquals(80000L, output.getFinalAmount());
    }

    @Test
    void deleteOrderItem_rollsBackStockAndUpdatesTotal() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(30, "Nước ngọt", 20000, 10);
        Order order = new Order("8001", "Bàn 9", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        AddMenuItemToOrderUseCase addUseCase = new AddMenuItemToOrderUseCase(orderRepo, menuRepo);
        addUseCase.execute(new AddMenuItemInput(8001L, 30L, 2, null));

        DeleteOrderItemUseCase deleteUseCase = new DeleteOrderItemUseCase(orderRepo, menuRepo);
        var output = deleteUseCase.execute(new DeleteOrderItemInput(8001L, 1L));
        assertEquals(10, menuRepo.stockOf(30));
        assertEquals(0, output.getItems().size());
        assertEquals(0L, output.getTotalAmount());
    }

    @Test
    void removeOrderItem_rollsBackStockAndUpdatesTotal() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(31, "Trà đá", 10000, 5);
        Order order = new Order("9001", "Bàn 10", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        AddMenuItemToOrderUseCase addUseCase = new AddMenuItemToOrderUseCase(orderRepo, menuRepo);
        addUseCase.execute(new AddMenuItemInput(9001L, 31L, 3, null));

        RemoveOrderItemUseCase removeUseCase = new RemoveOrderItemUseCase(orderRepo, menuRepo);
        var output = removeUseCase.execute(new RemoveOrderItemInput(9001L, 1L));
        assertEquals(5, menuRepo.stockOf(31));
        assertEquals(0, output.getItems().size());
        assertEquals(0L, output.getTotalAmount());
    }

    @Test
    void checkout_success_savesPayment_completesOrder_clearsTable() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();

        Order order = new Order("4001", "Bàn 5", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Phở", 2, 50000));
        orderRepo.save(order);
        tableRepo.save(new Table("T1", "Bàn 5", TableStatus.OCCUPIED, "4001"));

        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(orderRepo, paymentRepo, tableRepo, null);
        var output = useCase.execute(new CheckoutOrderInput(4001L, 100000L, "CASH", null, null, "tester", null, 0));
        assertNotNull(output.getPaymentId());
        assertEquals(OrderStatus.COMPLETED, orderRepo.findById("4001").orElseThrow().getStatus());
        assertEquals(1, paymentRepo.saved.size());
        assertEquals(TableStatus.AVAILABLE, tableRepo.findByTableName("Bàn 5").orElseThrow().getStatus());
    }

    @Test
    void checkout_failsWhenPaidAmountInsufficient() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();

        Order order = new Order("5001", "Bàn 6", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Lẩu", 1, 120000));
        orderRepo.save(order);
        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(orderRepo, paymentRepo, tableRepo, null);

        DomainException ex = assertThrows(DomainException.class, () ->
                useCase.execute(new CheckoutOrderInput(5001L, 50000L, "CASH", null, null, "tester", null, 0)));
        assertEquals(ErrorCode.CHECKOUT_PAID_AMOUNT_INSUFFICIENT, ex.getErrorCode());
        assertEquals(0, paymentRepo.saved.size());
    }

    @Test
    void checkout_withDiscountAmountAndPercent_computesFinalAndSavesPayment() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();

        Order order = new Order("5101", "Bàn 11", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Set", 1, 200000));
        orderRepo.save(order);
        tableRepo.save(new Table("T11", "Bàn 11", TableStatus.OCCUPIED, "5101"));

        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(orderRepo, paymentRepo, tableRepo, null);
        useCase.execute(new CheckoutOrderInput(5101L, 180000L, "CASH", 10000L, 10.0, "tester", null, 0));

        assertEquals(1, paymentRepo.saved.size());
        Payment p = paymentRepo.saved.get(0);
        assertEquals(200000L, p.getTotalAmount());
        assertEquals(170000L, p.getFinalAmount());
    }

    @Test
    void checkout_nonPayableState_throwsConflictCode() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();

        Order order = new Order("5201", "Bàn 12", LocalDateTime.now(), OrderStatus.COMPLETED, 100000);
        orderRepo.save(order);

        CheckoutOrderUseCase useCase = new CheckoutOrderUseCase(orderRepo, paymentRepo, tableRepo, null);
        DomainException ex = assertThrows(DomainException.class, () ->
                useCase.execute(new CheckoutOrderInput(5201L, 100000L, "CASH", null, null, "tester", null, 0)));
        assertEquals(ErrorCode.CHECKOUT_ORDER_NOT_PAYABLE_STATE, ex.getErrorCode());
    }

    @Test
    void updateQuantity_toZero_throwsValidationError() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(40, "Mỳ", 25000, 10);
        Order order = new Order("5301", "Bàn 13", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        new AddMenuItemToOrderUseCase(orderRepo, menuRepo).execute(new AddMenuItemInput(5301L, 40L, 1, null));

        UpdateOrderItemQuantityUseCase useCase = new UpdateOrderItemQuantityUseCase(orderRepo, menuRepo);
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new UpdateOrderItemQuantityInput(5301L, 1L, 0)));
        assertEquals(ErrorCode.ORDER_ITEM_QUANTITY_INVALID, ex.getErrorCode());
    }

    @Test
    void configurableOrderCodeService_generatesExpectedPattern() {
        ConfigurableOrderCodeService service = new ConfigurableOrderCodeService();
        LocalDateTime dt = LocalDateTime.of(2026, 1, 7, 10, 30);
        String code = service.generate("123456789", dt);
        assertEquals("ORDER" + dt.format(DateTimeFormatter.ofPattern("ddMMyy")) + "56789", code);
    }

    private static class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, Order> store = new HashMap<>();
        @Override public Optional<Order> findById(String orderId) { return Optional.ofNullable(store.get(orderId)); }
        @Override public Optional<Order> findByTableIdAndStatusPending(String tableId) {
            return store.values().stream().filter(o -> Objects.equals(o.getTableId(), tableId)
                    && (o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.IN_PROGRESS)).findFirst();
        }
        @Override public void save(Order order) { store.put(order.getOrderId(), order); }
        @Override public void delete(String orderId) { store.remove(orderId); }
        @Override public List<Order> findAllPendingOrders() {
            return store.values().stream().filter(o -> o.getStatus() == OrderStatus.PENDING).toList();
        }
    }

    private static class InMemoryMenuRepository implements MenuItemRepository {
        private final Map<Integer, MenuItem> items = new HashMap<>();
        void putTrackedItem(int id, String name, long unitPrice, int stockQty) {
            items.put(id, new MenuItem(id, name, unitPrice, "Food", null, true, stockQty, 0, 100, "phan", MenuItemStatus.ACTIVE));
        }
        int stockOf(int id) { return items.get(id).getCurrentStockQuantity(); }
        @Override public Optional<MenuItem> findById(int id) { return Optional.ofNullable(items.get(id)); }
        @Override public Optional<MenuItem> findByName(String name) { return items.values().stream().filter(i -> i.getName().equals(name)).findFirst(); }
        @Override public List<MenuItem> findAll() { return new ArrayList<>(items.values()); }
        @Override public List<MenuItem> findByCategoryName(String categoryName) { return List.of(); }
        @Override public List<MenuItem> findByStatus(MenuItemStatus status) { return List.of(); }
        @Override public void save(MenuItem menuItem) { items.put(menuItem.getId(), menuItem); }
        @Override public boolean decreaseStockAtomic(int id, int quantity) {
            MenuItem item = items.get(id);
            if (item == null || !item.isStockTracked() || item.getCurrentStockQuantity() < quantity) return false;
            item.updateStock(item.getCurrentStockQuantity() - quantity);
            return true;
        }
        @Override public boolean increaseStockAtomic(int id, int quantity) {
            MenuItem item = items.get(id);
            if (item == null || !item.isStockTracked()) return false;
            item.updateStock(item.getCurrentStockQuantity() + quantity);
            return true;
        }
        @Override public void delete(int id) { items.remove(id); }
    }

    private static class InMemoryPaymentRepository implements PaymentRepository {
        private final List<Payment> saved = new ArrayList<>();
        private long seq = 1L;
        @Override public Payment save(Payment payment) { payment.setPaymentId(seq++); saved.add(payment); return payment; }
        @Override public Optional<Payment> findById(Long paymentId) { return saved.stream().filter(p -> Objects.equals(p.getPaymentId(), paymentId)).findFirst(); }
        @Override public List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
            return saved.stream().filter(p -> !p.getPaidAt().isBefore(start) && !p.getPaidAt().isAfter(end)).toList();
        }
        @Override public void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
            saved.removeIf(p -> !p.getPaidAt().isBefore(start) && !p.getPaidAt().isAfter(end));
        }
    }

    private static class InMemoryTableRepository implements TableRepository {
        private final Map<String, Table> tables = new HashMap<>();
        @Override public Optional<Table> findById(String tableId) { return Optional.ofNullable(tables.get(tableId)); }
        @Override public Optional<Table> findByTableName(String tableName) {
            return tables.values().stream().filter(t -> Objects.equals(t.getTableName(), tableName)).findFirst();
        }
        @Override public List<Table> findAll() { return new ArrayList<>(tables.values()); }
        @Override public void save(Table table) { tables.put(table.getTableId(), table); }
        @Override public void delete(String tableId) { tables.remove(tableId); }
    }
}
