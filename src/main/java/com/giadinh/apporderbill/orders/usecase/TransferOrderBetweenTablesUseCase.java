package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.table.repository.TableRepository;

/**
 * Chuyển toàn bộ order đang mở từ bàn này sang bàn khác (theo tên bàn hiển thị trên POS).
 */
public class TransferOrderBetweenTablesUseCase {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;

    public TransferOrderBetweenTablesUseCase(OrderRepository orderRepository, TableRepository tableRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
    }

    public void execute(String fromTableName, String toTableName) {
        if (fromTableName == null || fromTableName.isBlank()) {
            throw new DomainException(ErrorCode.TRANSFER_SOURCE_TABLE_NAME_REQUIRED);
        }
        if (toTableName == null || toTableName.isBlank()) {
            throw new DomainException(ErrorCode.TRANSFER_TARGET_TABLE_NAME_REQUIRED);
        }
        String from = fromTableName.trim();
        String to = toTableName.trim();
        if (from.equalsIgnoreCase(to)) {
            throw new DomainException(ErrorCode.TRANSFER_SAME_SOURCE_AND_TARGET_TABLE);
        }

        Order order = orderRepository.findActiveByTable(from)
                .orElseThrow(() -> new DomainException(ErrorCode.NO_ACTIVE_ORDER_FOR_TABLE));
        if (orderRepository.findActiveByTable(to).isPresent()) {
            throw new DomainException(ErrorCode.TARGET_TABLE_NOT_EMPTY);
        }

        String orderId = order.getOrderId();
        order.updateTableId(to);
        orderRepository.save(order);

        tableRepository.findByTableName(from).ifPresent(oldT -> {
            oldT.setAvailableAfterTransfer();
            tableRepository.save(oldT);
        });
        tableRepository.findByTableName(to).ifPresent(newT -> {
            newT.setOccupiedAfterTransfer(orderId);
            tableRepository.save(newT);
        });
    }
}
