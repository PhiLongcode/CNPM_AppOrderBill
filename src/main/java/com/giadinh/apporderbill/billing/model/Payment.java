package com.giadinh.apporderbill.billing.model;

import java.time.LocalDateTime;

/**
 * Thanh toán đơn hàng — lưu đầy đủ chuỗi nghiệp vụ tiền tại thời điểm chốt:
 * <ol>
 *   <li>{@link #totalAmount} — tổng tiền món (subtotal)</li>
 *   <li>trừ {@link #discountAmount} / {@link #discountPercent} — giảm giá cấp đơn</li>
 *   <li>{@link #netAmountBeforeVat} — tiền sau giảm, làm cơ sở tính thuế</li>
 *   <li>cộng {@link #vatAmount} theo {@link #vatPercent}</li>
 *   <li>{@link #amountAfterVatBeforePoints} — sau VAT, trước đổi điểm</li>
 *   <li>trừ {@link #pointsDiscountAmount} — giảm từ đổi điểm</li>
 *   <li>{@link #finalAmount} — tổng khách phải trả; {@link #paidAmount} — khách đưa</li>
 * </ol>
 */
public class Payment {
    private Long paymentId;
    private String orderId;
    private long totalAmount;
    private long finalAmount;
    private long paidAmount;
    private String paymentMethod;
    private Long discountAmount;
    private Double discountPercent;
    private String cashier;
    private LocalDateTime paidAt;
    private Long customerId;
    private long vatAmount;
    private double vatPercent;
    private long pointsDiscountAmount;
    private long netAmountBeforeVat;
    private long amountAfterVatBeforePoints;

    public Payment(String orderId,
            long totalAmount,
            long finalAmount,
            long paidAmount,
            String paymentMethod,
            Long discountAmount,
            Double discountPercent,
            String cashier,
            Long customerId) {
        this(orderId, totalAmount, finalAmount, paidAmount, paymentMethod, discountAmount,
                discountPercent, cashier, customerId, 0L, 0.0, 0L, 0L, 0L);
    }

    public Payment(String orderId,
            long totalAmount,
            long finalAmount,
            long paidAmount,
            String paymentMethod,
            Long discountAmount,
            Double discountPercent,
            String cashier,
            Long customerId,
            long vatAmount,
            double vatPercent,
            long pointsDiscountAmount,
            long netAmountBeforeVat,
            long amountAfterVatBeforePoints) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
        this.customerId = customerId;
        this.vatAmount = vatAmount;
        this.vatPercent = vatPercent;
        this.pointsDiscountAmount = pointsDiscountAmount;
        this.netAmountBeforeVat = netAmountBeforeVat;
        this.amountAfterVatBeforePoints = amountAfterVatBeforePoints;
        this.paidAt = LocalDateTime.now();
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public long getFinalAmount() {
        return finalAmount;
    }

    public long getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public String getCashier() {
        return cashier;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public double getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(double vatPercent) {
        this.vatPercent = vatPercent;
    }

    public long getPointsDiscountAmount() {
        return pointsDiscountAmount;
    }

    public void setPointsDiscountAmount(long pointsDiscountAmount) {
        this.pointsDiscountAmount = pointsDiscountAmount;
    }

    public long getNetAmountBeforeVat() {
        return netAmountBeforeVat;
    }

    public void setNetAmountBeforeVat(long netAmountBeforeVat) {
        this.netAmountBeforeVat = netAmountBeforeVat;
    }

    public long getAmountAfterVatBeforePoints() {
        return amountAfterVatBeforePoints;
    }

    public void setAmountAfterVatBeforePoints(long amountAfterVatBeforePoints) {
        this.amountAfterVatBeforePoints = amountAfterVatBeforePoints;
    }
}
