package com.giadinh.apporderbill.table.service;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.table.model.TableStatus;

public class TableTransferService {

    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;

    public TableTransferService(TableRepository tableRepository, OrderRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    public void transferOrder(String oldTableId, String newTableId) {
        Table oldTable = tableRepository.findById(oldTableId)
                .orElseThrow(() -> new DomainException(ErrorCode.SOURCE_TABLE_NOT_FOUND));
        Table newTable = tableRepository.findById(newTableId)
                .orElseThrow(() -> new DomainException(ErrorCode.TARGET_TABLE_NOT_FOUND));

        if (oldTable.getStatus() != TableStatus.OCCUPIED || oldTable.getCurrentOrderId() == null) {
            throw new DomainException(ErrorCode.SOURCE_TABLE_NO_ACTIVE_ORDER);
        }

        if (newTable.getStatus() != TableStatus.AVAILABLE) {
            throw new DomainException(ErrorCode.TARGET_TABLE_NOT_EMPTY);
        }

        String orderIdToTransfer = oldTable.getCurrentOrderId();
        Order order = orderRepository.findById(orderIdToTransfer)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật thông tin đơn hàng sang bàn mới
        // order.updateTableId(newTableId); // Cần thêm phương thức này vào Order Entity

        // Cập nhật trạng thái bàn cũ và bàn mới
        oldTable.setAvailableAfterTransfer();
        newTable.setOccupiedAfterTransfer(orderIdToTransfer); // Gán Order ID cho bàn mới

        // Lưu các thay đổi
        tableRepository.save(oldTable);
        tableRepository.save(newTable);
        orderRepository.save(order); // Lưu Order đã cập nhật tableId

        System.out.println(String.format("Đã chuyển đơn hàng %s từ bàn %s sang bàn %s", orderIdToTransfer, oldTable.getTableName(), newTable.getTableName()));
    }
}
