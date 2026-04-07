package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.AddCustomItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.CalculateOrderTotalInput;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderInput;
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
}

