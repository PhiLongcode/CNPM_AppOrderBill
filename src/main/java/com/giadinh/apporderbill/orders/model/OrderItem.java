package com.giadinh.apporderbill.orders.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.UUID;

public class OrderItem {
    private String orderItemId;
    private String orderId;
    private String menuItemId;
    private String menuItemName; // Tên món ăn để hiển thị
    private int quantity;
    private double price; // Giá tại thời điểm đặt hàng
    private String note; // Ghi chú cho món ăn (ví dụ: ít đường, thêm đá)

    // Constructor cho OrderItem mới
    public OrderItem(String orderId, String menuItemId, String menuItemName, int quantity, double price) {
        if (quantity <= 0) {
            throw new DomainException(ErrorCode.ORDER_ITEM_QUANTITY_INVALID);
        }
        this.orderItemId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.price = price;
        this.note = ""; // Mặc định không có ghi chú
    }

    // Constructor cho việc tải OrderItem từ Repository
    public OrderItem(String orderItemId, String orderId, String menuItemId, String menuItemName, int quantity, double price, String note) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.price = price;
        this.note = note;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Cập nhật số lượng
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new DomainException(ErrorCode.ORDER_ITEM_QUANTITY_INVALID);
        }
        this.quantity = newQuantity;
    }
}
