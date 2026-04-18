package com.giadinh.apporderbill.orders.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.UUID;

public class OrderItem {
    private String orderItemId;
    private String orderId;
    private String menuItemId;
    private String menuItemName;
    private int quantity;
    private double price;
    private String note;
    private Double discountPercent;
    private Double discountAmount;
    private boolean printedToKitchen;
    /** Points redeemed for this line (0 = normal sale line). */
    private int loyaltyRedeemPoints;

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
        this.note = "";
        this.discountPercent = 0.0;
        this.discountAmount = 0.0;
        this.printedToKitchen = false;
        this.loyaltyRedeemPoints = 0;
    }

    public OrderItem(String orderItemId, String orderId, String menuItemId, String menuItemName, int quantity,
            double price, String note, Double discountPercent, Double discountAmount, boolean printedToKitchen,
            int loyaltyRedeemPoints) {
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
        this.loyaltyRedeemPoints = loyaltyRedeemPoints;
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

    public int getLoyaltyRedeemPoints() {
        return loyaltyRedeemPoints;
    }

    public void setLoyaltyRedeemPoints(int loyaltyRedeemPoints) {
        this.loyaltyRedeemPoints = Math.max(0, loyaltyRedeemPoints);
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new DomainException(ErrorCode.ORDER_ITEM_QUANTITY_INVALID);
        }
        this.quantity = newQuantity;
    }

    public double getLineTotal() {
        return (quantity * price - discountAmount) * (1.0 - discountPercent / 100.0);
    }
}
