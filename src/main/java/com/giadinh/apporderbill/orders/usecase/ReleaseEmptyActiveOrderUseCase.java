package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.table.repository.TableRepository;

/**
 * Xóa order rỗng (chưa có món) khi thu ngân mở nhầm bàn.
 */
public class ReleaseEmptyActiveOrderUseCase {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;

    public ReleaseEmptyActiveOrderUseCase(OrderRepository orderRepository, TableRepository tableRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
    }

    public void execute(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new DomainException(ErrorCode.TABLE_NUMBER_REQUIRED);
        }
        String name = tableName.trim();
        Order order = orderRepository.findActiveByTable(name)
                .orElseThrow(() -> new DomainException(ErrorCode.NO_ACTIVE_ORDER_FOR_TABLE));
        if (!order.getItems().isEmpty()) {
            throw new DomainException(ErrorCode.ORDER_HAS_ITEMS_CANNOT_RELEASE);
        }
        orderRepository.delete(order.getOrderId());
        tableRepository.findByTableName(name).ifPresent(t -> {
            t.clearTable();
            tableRepository.save(t);
        });
    }
}
