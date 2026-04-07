package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.AddCustomItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.AddMenuItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalInput;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.DeleteOrderItemInput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderUseCasesTest {
    @Test
    void addCustomItemAndCalculateAndCancel() {
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        Order order = new Order("1", "B1", LocalDateTime.now(), OrderStatus.PENDING, 0);
        repo.save(order);

        AddCustomItemToOrderUseCase addUseCase = new AddCustomItemToOrderUseCase(repo);
        addUseCase.execute(new AddCustomItemInput(1L, "Pho", 2, 50000, ""));

        CalculateOrderTotalUseCase calcUseCase = new CalculateOrderTotalUseCase(repo);
        var total = calcUseCase.execute(new CalculateOrderTotalInput(1L, 10000, null));
        assertEquals(100000, total.getSubtotal());
        assertEquals(90000, total.getFinalAmount());

        CancelOrderUseCase cancelUseCase = new CancelOrderUseCase(repo, null, null);
        cancelUseCase.execute(new CancelOrderInput(1L, "test", "tester"));
        assertEquals(OrderStatus.CANCELLED, repo.findById("1").orElseThrow().getStatus());
    }

    @Test
    void addMenuItem_shouldDeductStock_andFailWhenInsufficient() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("1", "B1", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(10, "Pho", 50000, 1);

        AddMenuItemToOrderUseCase useCase = new AddMenuItemToOrderUseCase(orderRepo, menuRepo);
        useCase.execute(new AddMenuItemInput(1L, 10L, 1, null));
        assertEquals(0, menuRepo.currentStock(10));

        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new AddMenuItemInput(1L, 10L, 1, null)));
        assertEquals(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT, ex.getErrorCode());
    }

    @Test
    void deleteItem_andCancelOrder_shouldRollbackStock() {
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        Order order = new Order("1", "B1", LocalDateTime.now(), OrderStatus.PENDING, 0);
        orderRepo.save(order);
        InMemoryMenuRepository menuRepo = new InMemoryMenuRepository();
        menuRepo.putTrackedItem(11, "Bun", 45000, 5);
        AddMenuItemToOrderUseCase addUseCase = new AddMenuItemToOrderUseCase(orderRepo, menuRepo);
        addUseCase.execute(new AddMenuItemInput(1L, 11L, 2, null));
        assertEquals(3, menuRepo.currentStock(11));

        DeleteOrderItemUseCase deleteUseCase = new DeleteOrderItemUseCase(orderRepo, menuRepo);
        deleteUseCase.execute(new DeleteOrderItemInput(1L, 1L));
        assertEquals(5, menuRepo.currentStock(11));

        addUseCase.execute(new AddMenuItemInput(1L, 11L, 2, null));
        assertEquals(3, menuRepo.currentStock(11));

        CancelOrderUseCase cancelUseCase = new CancelOrderUseCase(orderRepo, menuRepo, null);
        cancelUseCase.execute(new CancelOrderInput(1L, "test", "tester"));
        assertEquals(5, menuRepo.currentStock(11));
    }

    private static class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, Order> map = new HashMap<>();
        @Override public Optional<Order> findById(String orderId) { return Optional.ofNullable(map.get(orderId)); }
        @Override public Optional<Order> findByTableIdAndStatusPending(String tableId) {
            return map.values().stream().filter(o -> Objects.equals(o.getTableId(), tableId) && o.getStatus() == OrderStatus.PENDING).findFirst();
        }
        @Override public void save(Order order) { map.put(order.getOrderId(), order); }
        @Override public void delete(String orderId) { map.remove(orderId); }
        @Override public List<Order> findAllPendingOrders() {
            return map.values().stream().filter(o -> o.getStatus() == OrderStatus.PENDING).toList();
        }
    }

    private static class InMemoryMenuRepository implements MenuItemRepository {
        private final Map<Integer, MenuItem> items = new HashMap<>();

        void putTrackedItem(int id, String name, long unitPrice, int stockQty) {
            items.put(id, new MenuItem(id, name, unitPrice, "Food", null, true, stockQty, 0, 100, "phan",
                    MenuItemStatus.ACTIVE));
        }

        int currentStock(int id) {
            return items.get(id).getCurrentStockQuantity();
        }

        @Override public Optional<MenuItem> findById(int id) { return Optional.ofNullable(items.get(id)); }
        @Override public Optional<MenuItem> findByName(String name) { return items.values().stream().filter(i -> i.getName().equals(name)).findFirst(); }
        @Override public List<MenuItem> findAll() { return new ArrayList<>(items.values()); }
        @Override public List<MenuItem> findByCategoryName(String categoryName) { return List.of(); }
        @Override public List<MenuItem> findByStatus(MenuItemStatus status) { return List.of(); }
        @Override public void save(MenuItem menuItem) { items.put(menuItem.getId(), menuItem); }
        @Override public boolean decreaseStockAtomic(int id, int quantity) {
            var item = items.get(id);
            if (item == null || !item.isStockTracked()) return false;
            if (item.getCurrentStockQuantity() < quantity) return false;
            item.updateStock(item.getCurrentStockQuantity() - quantity);
            return true;
        }
        @Override public boolean increaseStockAtomic(int id, int quantity) {
            var item = items.get(id);
            if (item == null || !item.isStockTracked()) return false;
            item.updateStock(item.getCurrentStockQuantity() + quantity);
            return true;
        }
        @Override public void delete(int id) { items.remove(id); }
    }
}

