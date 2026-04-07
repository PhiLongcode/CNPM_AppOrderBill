package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.model.TableStatus;
import com.giadinh.apporderbill.table.repository.TableRepository;

/**
 * Đặt trước / hủy đặt trước bàn (không có order đang mở).
 */
public class SetTableReservationUseCase {

    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;

    public SetTableReservationUseCase(TableRepository tableRepository, OrderRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    public void setReserved(String tableName, boolean reserved) {
        if (tableName == null || tableName.isBlank()) {
            throw new DomainException(ErrorCode.TABLE_NUMBER_REQUIRED);
        }
        String name = tableName.trim();
        Table table = tableRepository.findByTableName(name)
                .orElseThrow(() -> new DomainException(ErrorCode.TABLE_NOT_FOUND));
        if (orderRepository.findActiveByTable(name).isPresent()) {
            throw new DomainException(ErrorCode.TABLE_RESERVATION_BLOCKED_BY_ACTIVE_ORDER);
        }
        if (table.getCurrentOrderId() != null && !table.getCurrentOrderId().isBlank()) {
            throw new DomainException(ErrorCode.TABLE_RESERVATION_BLOCKED_BY_LINKED_ORDER);
        }
        if (reserved) {
            if (table.getStatus() == TableStatus.OCCUPIED) {
                throw new DomainException(ErrorCode.TABLE_RESERVATION_OCCUPIED_CANNOT_RESERVE);
            }
            table.setStatus(TableStatus.RESERVED);
            tableRepository.save(table);
        } else {
            if (table.getStatus() != TableStatus.RESERVED) {
                throw new DomainException(ErrorCode.TABLE_NOT_IN_RESERVED_STATE);
            }
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }
    }
}
