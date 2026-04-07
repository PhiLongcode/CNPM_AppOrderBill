package com.giadinh.apporderbill.table.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.UUID;

public class Table {
    private String tableId;
    private String tableName; // Tên bàn (ví dụ: Bàn 1, Bàn VIP)
    private TableStatus status; // Trạng thái của bàn
    private String currentOrderId; // ID của đơn hàng đang phục vụ trên bàn này (nếu có)

    // Constructor cho Table mới
    public Table(String tableName) {
        this.tableId = UUID.randomUUID().toString();
        this.tableName = tableName;
        this.status = TableStatus.AVAILABLE; // Mặc định là trống
        this.currentOrderId = null;
    }

    // Constructor cho việc tải Table từ Repository
    public Table(String tableId, String tableName, TableStatus status, String currentOrderId) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.status = status;
        this.currentOrderId = currentOrderId;
    }

    public String getTableId() {
        return tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public TableStatus getStatus() {
        return status;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setStatus(TableStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    // Phương thức gán Order cho bàn
    public void assignOrder(String orderId) {
        if (this.status == TableStatus.AVAILABLE || this.status == TableStatus.RESERVED) {
            this.currentOrderId = orderId;
            this.status = TableStatus.OCCUPIED; // Chuyển trạng thái thành đang phục vụ
        } else {
            throw new DomainException(ErrorCode.TABLE_NOT_AVAILABLE_FOR_ASSIGN);
        }
    }

    // Phương thức giải phóng bàn
    public void clearTable() {
        this.currentOrderId = null;
        this.status = TableStatus.AVAILABLE; // Chuyển trạng thái thành trống
    }

    // Phương thức chuyển đơn hàng sang bàn khác
    public void transferOrder(String newOrderId) {
        this.currentOrderId = newOrderId; // Cập nhật Order ID mới
    }

    // Phương thức đặt bàn lại thành trống sau khi chuyển đơn hàng đi
    public void setAvailableAfterTransfer() {
        this.currentOrderId = null;
        this.status = TableStatus.AVAILABLE;
    }

    // Phương thức đặt bàn thành đang phục vụ sau khi chuyển đơn hàng đến
    public void setOccupiedAfterTransfer(String orderId) {
        this.currentOrderId = orderId;
        this.status = TableStatus.OCCUPIED;
    }
}
