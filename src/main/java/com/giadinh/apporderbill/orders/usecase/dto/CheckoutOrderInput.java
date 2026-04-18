package com.giadinh.apporderbill.orders.usecase.dto;

import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMode;

/**
 * Checkout payload. Legacy clients send only {@code pointsUsed}; when {@code loyaltyRedeemMode}
 * is null, mode is inferred as {@link LoyaltyRedeemMode#BILL_DISCOUNT} if points are used.
 */
public class CheckoutOrderInput {
    private Long orderId;
    private long paidAmount;
    private String paymentMethod;
    private Long discountAmount;
    private Double discountPercent;
    private String cashier;
    private Long customerId;
    private int pointsUsed;
    private LoyaltyRedeemMode loyaltyRedeemMode;
    private Long loyaltyRedeemCatalogId;
    private Long loyaltyGiftId;

    public CheckoutOrderInput() {
    }

    public CheckoutOrderInput(Long orderId,
            long paidAmount,
            String paymentMethod,
            Long discountAmount,
            Double discountPercent,
            String cashier,
            Long customerId,
            int pointsUsed) {
        this.orderId = orderId;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
        this.customerId = customerId;
        this.pointsUsed = pointsUsed;
        this.loyaltyRedeemMode = null;
        this.loyaltyRedeemCatalogId = null;
        this.loyaltyGiftId = null;
    }

    public CheckoutOrderInput(Long orderId,
            long paidAmount,
            String paymentMethod,
            Long discountAmount,
            Double discountPercent,
            String cashier,
            Long customerId,
            int pointsUsed,
            LoyaltyRedeemMode loyaltyRedeemMode,
            Long loyaltyRedeemCatalogId,
            Long loyaltyGiftId) {
        this.orderId = orderId;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
        this.customerId = customerId;
        this.pointsUsed = pointsUsed;
        this.loyaltyRedeemMode = loyaltyRedeemMode;
        this.loyaltyRedeemCatalogId = loyaltyRedeemCatalogId;
        this.loyaltyGiftId = loyaltyGiftId;
    }

    /**
     * Effective redeem mode for this bill (one mode per checkout).
     */
    public LoyaltyRedeemMode resolveLoyaltyRedeemMode() {
        if (loyaltyRedeemMode != null && loyaltyRedeemMode != LoyaltyRedeemMode.NONE) {
            return loyaltyRedeemMode;
        }
        if (pointsUsed > 0) {
            return LoyaltyRedeemMode.BILL_DISCOUNT;
        }
        return LoyaltyRedeemMode.NONE;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public long getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(long paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(int pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public LoyaltyRedeemMode getLoyaltyRedeemMode() {
        return loyaltyRedeemMode;
    }

    public void setLoyaltyRedeemMode(LoyaltyRedeemMode loyaltyRedeemMode) {
        this.loyaltyRedeemMode = loyaltyRedeemMode;
    }

    public Long getLoyaltyRedeemCatalogId() {
        return loyaltyRedeemCatalogId;
    }

    public void setLoyaltyRedeemCatalogId(Long loyaltyRedeemCatalogId) {
        this.loyaltyRedeemCatalogId = loyaltyRedeemCatalogId;
    }

    public Long getLoyaltyGiftId() {
        return loyaltyGiftId;
    }

    public void setLoyaltyGiftId(Long loyaltyGiftId) {
        this.loyaltyGiftId = loyaltyGiftId;
    }
}
