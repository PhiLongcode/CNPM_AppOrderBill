package com.giadinh.apporderbill.orders.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String orderCode;
    private String tableId; // ID của bàn
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItem> items;
    private double totalAmount;

    // Constructor cho Order mới
    public Order(String tableId) {
        this.orderId = UUID.randomUUID().toString();
        this.orderCode = null;
        this.tableId = tableId;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING; // Mặc định là đang chờ
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    // Constructor cho việc tải Order từ Repository
    public Order(String orderId, String tableId, LocalDateTime orderDate, OrderStatus status, double totalAmount) {
        this.orderId = orderId;
        this.orderCode = null;
        this.tableId = tableId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = new ArrayList<>(); // Items sẽ được load riêng bởi Repository
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getId() {
        try {
            return Long.parseLong(orderId);
        } catch (Exception e) {
            return null;
        }
    }

    public String getTableId() {
        return tableId;
    }

    public String getTableNumber() {
        return tableId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items); // Trả về danh sách không thể sửa đổi
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void updateTableId(String newTableId) {
        if (newTableId == null || newTableId.trim().isEmpty()) {
            throw new DomainException(ErrorCode.ORDER_NEW_TABLE_ID_REQUIRED);
        }
        this.tableId = newTableId;
    }

    // Phương thức thêm OrderItem vào Order
    public void addOrderItem(OrderItem item) {
        if (this.status == OrderStatus.PENDING || this.status == OrderStatus.IN_PROGRESS) {
            this.items.add(item);
            calculateTotalAmount();
        } else {
            throw new DomainException(ErrorCode.ORDER_NOT_MODIFIABLE);
        }
    }

    // Phương thức cập nhật số lượng OrderItem
    public void updateOrderItemQuantity(String menuItemId, int quantity) {
        if (this.status == OrderStatus.PENDING || this.status == OrderStatus.IN_PROGRESS) {
            for (OrderItem item : items) {
                if (item.getMenuItemId().equals(menuItemId)) {
                    item.updateQuantity(quantity);
                    calculateTotalAmount();
                    return;
                }
            }
            throw new DomainException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        } else {
            throw new DomainException(ErrorCode.ORDER_NOT_MODIFIABLE);
        }
    }

    // Phương thức xóa OrderItem khỏi Order
    public void removeOrderItem(String menuItemId) {
        if (this.status == OrderStatus.PENDING || this.status == OrderStatus.IN_PROGRESS) {
            boolean removed = this.items.removeIf(item -> item.getMenuItemId().equals(menuItemId));
            if (removed) {
                calculateTotalAmount();
            } else {
                throw new DomainException(ErrorCode.ORDER_ITEM_NOT_FOUND);
            }
        } else {
            throw new DomainException(ErrorCode.ORDER_NOT_MODIFIABLE);
        }
    }
    
    // Phương thức tính toán lại tổng tiền của đơn hàng
    private void calculateTotalAmount() {
        this.totalAmount = this.items.stream()
                .mapToDouble(OrderItem::getLineTotal)
                .sum();
    }

    /** Gọi sau khi restoreOrderItem từ DB để đồng bộ totalAmount với các dòng. */
    public void recomputeTotalFromItems() {
        calculateTotalAmount();
    }

    // Thêm OrderItem đã có sẵn vào danh sách (dùng khi tải từ DB)
    public void restoreOrderItem(OrderItem item) {
        this.items.add(item);
    }

    public void markAllItemsAsPrinted() {
        for (OrderItem item : items) {
            item.setPrintedToKitchen(true);
        }
    }
}
