package com.giadinh.apporderbill.orders.repository;

import com.giadinh.apporderbill.orders.model.Order;
import java.util.Optional;
import java.util.List;

public interface OrderRepository {
    Optional<Order> findById(String orderId);
    Optional<Order> findByTableIdAndStatusPending(String tableId);
    void save(Order order);
    void delete(String orderId);
    List<Order> findAllPendingOrders();
}
