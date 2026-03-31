package com.giadinh.apporderbill.orders.repository;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SqliteOrderRepository implements OrderRepository {
    private final ConcurrentMap<String, Order> store = new ConcurrentHashMap<>();

    public SqliteOrderRepository(SqliteConnectionProvider connectionProvider) {
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(store.get(orderId));
    }

    @Override
    public Optional<Order> findByTableIdAndStatusPending(String tableId) {
        return store.values().stream()
                .filter(o -> tableId.equals(o.getTableId()) && (o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.IN_PROGRESS))
                .findFirst();
    }

    @Override
    public void save(Order order) {
        String key = order.getOrderId() != null ? order.getOrderId() : String.valueOf(order.getId());
        store.put(key, order);
    }

    @Override
    public void delete(String orderId) {
        store.remove(orderId);
    }

    @Override
    public List<Order> findAllPendingOrders() {
        List<Order> out = new ArrayList<>();
        for (Order o : store.values()) {
            if (o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.IN_PROGRESS) {
                out.add(o);
            }
        }
        return out;
    }
}

