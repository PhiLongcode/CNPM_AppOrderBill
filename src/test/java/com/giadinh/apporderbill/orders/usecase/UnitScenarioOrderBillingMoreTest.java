package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.PrintReceiptUseCase;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptInput;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.AddCustomItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.AddMenuItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderInput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.shared.service.PrinterService;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.model.TableStatus;
import com.giadinh.apporderbill.table.repository.TableRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UnitScenarioOrderBillingMoreTest {

    @Test
    void addCustomItem_validInput_addsToOrderAndRecalculatesTotal() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("10001", "Bàn A", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        AddCustomItemToOrderUseCase useCase = new AddCustomItemToOrderUseCase(orderRepo);

        var out = useCase.execute(new AddCustomItemInput(10001L, "Combo đặc biệt", 2, 45000, "ít cay"));

        assertEquals(1, out.getItems().size());
        assertEquals(90000L, out.getTotalAmount());
    }

    @Test
    void addCustomItem_invalidQuantity_throwsValidationError() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("10002", "Bàn B", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        AddCustomItemToOrderUseCase useCase = new AddCustomItemToOrderUseCase(orderRepo);

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new AddCustomItemInput(10002L, "Lỗi", 0, 10000, "")));
        assertEquals(ErrorCode.ORDER_ITEM_QUANTITY_INVALID, ex.getErrorCode());
    }

    @Test
    void cancelOrder_restoresStock_setsCancelled_andClearsTable() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        InMemoryTableRepository tableRepo = new InMemoryTableRepository();
        menuRepo.putTrackedItem(7, "Bò viên", 30000, 10);
        Order order = new Order("10003", "Bàn C", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        tableRepo.save(new Table("TB-C", "Bàn C", TableStatus.OCCUPIED, "10003"));
        new AddMenuItemToOrderUseCase(orderRepo, menuRepo).execute(new AddMenuItemInput(10003L, 7L, 3, null));
        assertEquals(7, menuRepo.stockOf(7));

        CancelOrderUseCase useCase = new CancelOrderUseCase(orderRepo, menuRepo, tableRepo);
        var out = useCase.execute(new CancelOrderInput(10003L, "khách hủy", "tester"));

        assertTrue(out.isSuccess());
        assertEquals(OrderStatus.CANCELLED, orderRepo.findById("10003").orElseThrow().getStatus());
        assertEquals(10, menuRepo.stockOf(7));
        assertEquals(TableStatus.AVAILABLE, tableRepo.findByTableName("Bàn C").orElseThrow().getStatus());
    }

    @Test
    void printReceipt_withValidPayment_callsPrinter() {
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        SpyPrinterService printer = new SpyPrinterService(true);
        Payment p = new Payment("ORDER-1", 120000, 100000, 100000, "CASH", 20000L, null, "cashier");
        paymentRepo.save(p);
        PrintReceiptUseCase useCase = new PrintReceiptUseCase(paymentRepo, printer);

        useCase.execute(new PrintReceiptInput(p.getPaymentId(), 1L));

        assertEquals(1, printer.receiptPrintCount);
        assertTrue(printer.lastReceiptContent.contains("HOA DON THANH TOAN"));
    }

    @Test
    void printReceipt_whenPrinterFails_throwsDomainException() {
        InMemoryPaymentRepository paymentRepo = new InMemoryPaymentRepository();
        SpyPrinterService printer = new SpyPrinterService(false);
        Payment p = new Payment("ORDER-2", 50000, 50000, 50000, "CASH", null, null, "cashier");
        paymentRepo.save(p);
        PrintReceiptUseCase useCase = new PrintReceiptUseCase(paymentRepo, printer);

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new PrintReceiptInput(p.getPaymentId(), 2L)));
        assertEquals(ErrorCode.PRINTER_RECEIPT_SEND_FAILED, ex.getErrorCode());
    }

    private static class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, Order> map = new HashMap<>();
        @Override public Optional<Order> findById(String orderId) { return Optional.ofNullable(map.get(orderId)); }
        @Override public Optional<Order> findByTableIdAndStatusPending(String tableId) {
            return map.values().stream()
                    .filter(o -> Objects.equals(o.getTableId(), tableId) && (o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.IN_PROGRESS))
                    .findFirst();
        }
        @Override public void save(Order order) { map.put(order.getOrderId(), order); }
        @Override public void delete(String orderId) { map.remove(orderId); }
        @Override public List<Order> findAllPendingOrders() { return map.values().stream().filter(o -> o.getStatus() == OrderStatus.PENDING).toList(); }
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

    private static class InMemoryPaymentRepository implements PaymentRepository {
        private final List<Payment> payments = new ArrayList<>();
        private long seq = 1L;
        @Override public Payment save(Payment payment) { payment.setPaymentId(seq++); payments.add(payment); return payment; }
        @Override public Optional<Payment> findById(Long paymentId) {
            return payments.stream().filter(p -> Objects.equals(p.getPaymentId(), paymentId)).findFirst();
        }
        @Override public List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
            return payments.stream().filter(p -> !p.getPaidAt().isBefore(start) && !p.getPaidAt().isAfter(end)).toList();
        }
        @Override public void deleteByPaidAtBetween(LocalDateTime start, LocalDateTime end) {
            payments.removeIf(p -> !p.getPaidAt().isBefore(start) && !p.getPaidAt().isAfter(end));
        }
    }

    private static class SpyPrinterService implements PrinterService {
        private final boolean shouldSucceed;
        int receiptPrintCount = 0;
        String lastReceiptContent = "";
        SpyPrinterService(boolean shouldSucceed) { this.shouldSucceed = shouldSucceed; }
        @Override public boolean printKitchenTicket(String content) { return shouldSucceed; }
        @Override public boolean printReceipt(String content) { receiptPrintCount++; lastReceiptContent = content; return shouldSucceed; }
        @Override public boolean printTest(String content) { return shouldSucceed; }
    }
}
