package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.repository.TableRepository;

public class RenameTableUseCase {

    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;

    public RenameTableUseCase(TableRepository tableRepository, OrderRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    public void execute(String tableId, String newDisplayName) {
        if (tableId == null || tableId.isBlank()) {
            throw new DomainException(ErrorCode.TABLE_ID_REQUIRED);
        }
        if (newDisplayName == null || newDisplayName.isBlank()) {
            throw new DomainException(ErrorCode.TABLE_DISPLAY_NAME_REQUIRED);
        }
        String newName = newDisplayName.trim();
        Table table = tableRepository.findById(tableId.trim())
                .orElseThrow(() -> new DomainException(ErrorCode.TABLE_NOT_FOUND));
        String oldName = table.getTableName();
        if (orderRepository.findActiveByTable(oldName).isPresent()) {
            throw new DomainException(ErrorCode.RENAME_TABLE_ACTIVE_ORDER_EXISTS);
        }
        if (tableRepository.findByTableName(newName).filter(t -> !t.getTableId().equals(tableId)).isPresent()) {
            throw new DomainException(ErrorCode.TABLE_NAME_DUPLICATE);
        }
        table.setTableName(newName);
        tableRepository.save(table);
    }
}
