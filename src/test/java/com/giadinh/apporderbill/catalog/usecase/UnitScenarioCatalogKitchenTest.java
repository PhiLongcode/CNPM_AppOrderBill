package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockInput;
import com.giadinh.apporderbill.kitchen.usecase.PrintKitchenTicketUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintSelectedItemsUseCase;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketInput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintSelectedItemsInput;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.shared.service.PrinterService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UnitScenarioCatalogKitchenTest {

    @Test
    void createMenuItem_validInput_savesSuccessfully() {
        InMemoryMenuRepository repo = new InMemoryMenuRepository();
        CreateMenuItemUseCase useCase = new CreateMenuItemUseCase(repo);
        CreateMenuItemInput input = new CreateMenuItemInput();
        input.setName("Bò lúc lắc");
        input.setCategory("Bò");
        input.setUnitPrice(99000L);
        input.setStockTracked(true);
        input.setStockQty(20L);
        input.setStockMin(5L);
        input.setStockMax(100L);
        input.setBaseUnit("phần");

        var out = useCase.execute(input);

        assertEquals("Bò lúc lắc", out.getName());
        assertTrue(out.isStockTracked());
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void createMenuItem_invalidPrice_throwsDomainException() {
        InMemoryMenuRepository repo = new InMemoryMenuRepository();
        CreateMenuItemUseCase useCase = new CreateMenuItemUseCase(repo);
        CreateMenuItemInput input = new CreateMenuItemInput();
        input.setName("Món lỗi");
        input.setCategory("Khác");
        input.setUnitPrice(0L);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(input));
        assertEquals(ErrorCode.MENU_ITEM_PRICE_INVALID, ex.getErrorCode());
    }

    @Test
    void updateMenuItem_existingItem_updatesFields() {
        InMemoryMenuRepository repo = new InMemoryMenuRepository();
        repo.save(new MenuItem(1, "Cũ", 10000, "A", null, false, 0, 0, 0, "phần", MenuItemStatus.ACTIVE));
        UpdateMenuItemUseCase useCase = new UpdateMenuItemUseCase(repo);
        UpdateMenuItemInput in = new UpdateMenuItemInput();
        in.setMenuItemId(1L);
        in.setName("Mới");
        in.setUnitPrice(15000L);
        in.setCategory("B");

        var out = useCase.execute(in);

        assertEquals("Mới", out.getName());
        assertEquals("B", out.getCategory());
        assertEquals(15000L, out.getUnitPrice());
    }

    @Test
    void updateMenuItem_notFound_throwsNoSuchElementException() {
        InMemoryMenuRepository repo = new InMemoryMenuRepository();
        UpdateMenuItemUseCase useCase = new UpdateMenuItemUseCase(repo);
        UpdateMenuItemInput in = new UpdateMenuItemInput();
        in.setMenuItemId(99L);

        assertThrows(NoSuchElementException.class, () -> useCase.execute(in));
    }

    @Test
    void updateMenuItemStock_increaseAndDecrease_behavesCorrectly() {
        InMemoryMenuRepository repo = new InMemoryMenuRepository();
        repo.save(new MenuItem(2, "Trà chanh", 12000, "Nước", null, true, 10, 0, 100, "ly", MenuItemStatus.ACTIVE));
        UpdateMenuItemStockUseCase useCase = new UpdateMenuItemStockUseCase(repo);

        var inc = useCase.execute(2, new UpdateMenuItemStockInput(UpdateMenuItemStockInput.StockOperation.INCREASE, 5));
        var dec = useCase.execute(2, new UpdateMenuItemStockInput(UpdateMenuItemStockInput.StockOperation.DECREASE, 3));

        assertEquals(15, inc.getCurrentStockQuantity());
        assertEquals(12, dec.getCurrentStockQuantity());
    }

    @Test
    void printKitchenTicket_hasItems_printerCalledAndSuccess() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("100", "Bàn 1", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Phở", 1, 50000));
        orderRepo.save(order);
        SpyPrinterService printer = new SpyPrinterService(true);
        PrintKitchenTicketUseCase useCase = new PrintKitchenTicketUseCase(orderRepo, null, null, printer);

        var out = useCase.execute(new PrintKitchenTicketInput(100L, false));

        assertTrue(out.isPrinted());
        assertEquals(1, printer.kitchenPrintCount);
        assertEquals(100L, printer.lastKitchenOrderId);
        assertNull(printer.lastKitchenItemIds);
    }

    @Test
    void printKitchenTicket_emptyOrder_throwsDomainException() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        orderRepo.save(new Order("101", "Bàn 2", LocalDateTime.now(), OrderStatus.PENDING, 0));
        PrintKitchenTicketUseCase useCase = new PrintKitchenTicketUseCase(orderRepo, null, null, new SpyPrinterService(true));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new PrintKitchenTicketInput(101L, false)));
        assertEquals(ErrorCode.KITCHEN_ORDER_NO_ITEMS_FOR_TICKET, ex.getErrorCode());
    }

    @Test
    void printSelectedItems_emptySelection_throwsDomainException() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("102", "Bàn 3", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Bún", 1, 40000));
        orderRepo.save(order);
        PrintSelectedItemsUseCase useCase = new PrintSelectedItemsUseCase(orderRepo, null, null, new SpyPrinterService(true));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new PrintSelectedItemsInput(102L, List.of())));
        assertEquals(ErrorCode.KITCHEN_SELECTED_ITEMS_REQUIRED, ex.getErrorCode());
    }

    @Test
    void printSelectedItems_validSelection_callsPrinterOnce() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("103", "Bàn 4", LocalDateTime.now(), OrderStatus.PENDING, 0);
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "1", "Món A", 1, 20000));
        order.addOrderItem(new com.giadinh.apporderbill.orders.model.OrderItem(order.getOrderId(), "2", "Món B", 2, 15000));
        orderRepo.save(order);
        SpyPrinterService printer = new SpyPrinterService(true);
        PrintSelectedItemsUseCase useCase = new PrintSelectedItemsUseCase(orderRepo, null, null, printer);

        var out = useCase.execute(new PrintSelectedItemsInput(103L, List.of(2L)));

        assertTrue(out.isPrinted());
        assertEquals(1, printer.kitchenPrintCount);
        assertEquals(103L, printer.lastKitchenOrderId);
        assertEquals(List.of(2L), printer.lastKitchenItemIds);
    }

    private static class InMemoryMenuRepository implements MenuItemRepository {
        private final Map<Integer, MenuItem> map = new HashMap<>();
        private int seq = 1;
        @Override public Optional<MenuItem> findById(int id) { return Optional.ofNullable(map.get(id)); }
        @Override public Optional<MenuItem> findByName(String name) { return map.values().stream().filter(i -> i.getName().equals(name)).findFirst(); }
        @Override public List<MenuItem> findAll() { return new ArrayList<>(map.values()); }
        @Override public List<MenuItem> findByCategoryName(String categoryName) { return List.of(); }
        @Override public List<MenuItem> findByStatus(MenuItemStatus status) { return List.of(); }
        @Override public void save(MenuItem menuItem) {
            if (menuItem.getId() <= 0) {
                MenuItem assigned = new MenuItem(seq++, menuItem.getName(), menuItem.getUnitPrice(), menuItem.getCategory(), menuItem.getImageUrl(),
                        menuItem.isStockTracked(), menuItem.getStockQty().intValue(), menuItem.getStockMin().intValue(),
                        menuItem.getMaxStockQuantity(), menuItem.getBaseUnit(), menuItem.getStatus());
                map.put(assigned.getId(), assigned);
            } else {
                map.put(menuItem.getId(), menuItem);
            }
        }
        @Override public boolean decreaseStockAtomic(int id, int quantity) {
            MenuItem item = map.get(id);
            if (item == null || !item.isStockTracked() || item.getCurrentStockQuantity() < quantity) return false;
            item.updateStock(item.getCurrentStockQuantity() - quantity);
            return true;
        }
        @Override public boolean increaseStockAtomic(int id, int quantity) {
            MenuItem item = map.get(id);
            if (item == null || !item.isStockTracked()) return false;
            item.updateStock(item.getCurrentStockQuantity() + quantity);
            return true;
        }
        @Override public void delete(int id) { map.remove(id); }
    }

    private static class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, Order> map = new HashMap<>();
        @Override public Optional<Order> findById(String orderId) { return Optional.ofNullable(map.get(orderId)); }
        @Override public Optional<Order> findByTableIdAndStatusPending(String tableId) { return Optional.empty(); }
        @Override public void save(Order order) { map.put(order.getOrderId(), order); }
        @Override public void delete(String orderId) { map.remove(orderId); }
        @Override public List<Order> findAllPendingOrders() { return List.of(); }
    }

    private static class SpyPrinterService implements PrinterService {
        private final boolean success;
        int kitchenPrintCount = 0;
        String lastKitchenContent = "";
        Long lastKitchenOrderId;
        List<Long> lastKitchenItemIds;
        SpyPrinterService(boolean success) { this.success = success; }
        @Override public boolean printKitchenTicket(String content) { kitchenPrintCount++; lastKitchenContent = content; return success; }
        @Override
        public boolean printKitchenTicket(Long orderId, boolean isAddOn, boolean isReprint) throws PrinterService.PrinterException {
            kitchenPrintCount++;
            lastKitchenOrderId = orderId;
            lastKitchenItemIds = null;
            lastKitchenContent = "KITCHEN_LONG";
            return success;
        }
        @Override
        public boolean printKitchenTicketSelected(Long orderId, List<Long> orderItemIds) throws PrinterService.PrinterException {
            kitchenPrintCount++;
            lastKitchenOrderId = orderId;
            lastKitchenItemIds = orderItemIds == null ? null : new ArrayList<>(orderItemIds);
            lastKitchenContent = "KITCHEN_SELECTED";
            return success;
        }
        @Override public boolean printReceipt(String content) { return success; }
        @Override public boolean printTest(String content) { return success; }
    }
}
