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
    private Double discountPercent; // Phần trăm giảm giá (0-100)
    private Double discountAmount; // Số tiền giảm giá
    private boolean printedToKitchen; // Trạng thái đã in phiếu bếp

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
        this.discountPercent = 0.0;
        this.discountAmount = 0.0;
        this.printedToKitchen = false;
    }

    // Constructor cho việc tải OrderItem từ Repository
    public OrderItem(String orderItemId, String orderId, String menuItemId, String menuItemName, int quantity, double price, String note, Double discountPercent, Double discountAmount, boolean printedToKitchen) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.price = price;
        this.note = note;
        this.discountPercent = discountPercent != null ? discountPercent : 0.0;
        this.discountAmount = discountAmount != null ? discountAmount : 0.0;
        this.printedToKitchen = printedToKitchen;
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
    
    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent != null ? discountPercent : 0.0;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount != null ? discountAmount : 0.0;
    }

    public boolean isPrintedToKitchen() {
        return printedToKitchen;
    }

    public void setPrintedToKitchen(boolean printedToKitchen) {
        this.printedToKitchen = printedToKitchen;
    }

    // Cập nhật số lượng
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new DomainException(ErrorCode.ORDER_ITEM_QUANTITY_INVALID);
        }
        this.quantity = newQuantity;
    }
    
    // Tính tổng tiền sau khi giảm giá
    public double getLineTotal() {
        return (quantity * price - discountAmount) * (1.0 - discountPercent / 100.0);
    }
}
