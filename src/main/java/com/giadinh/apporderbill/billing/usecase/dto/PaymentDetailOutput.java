package com.giadinh.apporderbill.billing.usecase.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentDetailOutput {
    private final Long paymentId;
    private final String orderId;
    private final String tableNumber;
    private final long totalAmount;
    private final long finalAmount;
    private final long paidAmount;
    private final long changeAmount;
    private final String paymentMethod;
    private final Long discountAmount;
    private final Double discountPercent;
    private final String cashier;
    private final LocalDateTime paidAt;
    private final List<PaymentItemOutput> items;
    private final String customerName;
    private final String customerPhone;
    private final long vatAmount;
    private final double vatPercent;
    private final long pointsDiscountAmount;
    private final long netAmountBeforeVat;
    private final long amountAfterVatBeforePoints;

    public record PaymentItemOutput(String name, int qty, String unit, long unitPrice, String discountText, long lineTotal) {}

    public PaymentDetailOutput(Long paymentId, String orderId, String tableNumber,
            long totalAmount, long finalAmount, long paidAmount,
            String paymentMethod, Long discountAmount, Double discountPercent,
            String cashier, LocalDateTime paidAt, List<PaymentItemOutput> items,
            String customerName, String customerPhone,
            long vatAmount, double vatPercent, long pointsDiscountAmount,
            long netAmountBeforeVat, long amountAfterVatBeforePoints) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.paidAmount = paidAmount;
        this.changeAmount = paidAmount - finalAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.cashier = cashier;
        this.paidAt = paidAt;
        this.items = items != null ? items : List.of();
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.vatAmount = vatAmount;
        this.vatPercent = vatPercent;
        this.pointsDiscountAmount = pointsDiscountAmount;
        this.netAmountBeforeVat = netAmountBeforeVat;
        this.amountAfterVatBeforePoints = amountAfterVatBeforePoints;
    }

    public Long getPaymentId() { return paymentId; }
    public String getOrderId() { return orderId; }
    public String getTableNumber() { return tableNumber; }
    public long getTotalAmount() { return totalAmount; }
    public long getFinalAmount() { return finalAmount; }
    public long getPaidAmount() { return paidAmount; }
    public long getChangeAmount() { return changeAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public Long getDiscountAmount() { return discountAmount; }
    public Double getDiscountPercent() { return discountPercent; }
    public String getCashier() { return cashier; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public List<PaymentItemOutput> getItems() { return items; }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public double getVatPercent() {
        return vatPercent;
    }

    public long getPointsDiscountAmount() {
        return pointsDiscountAmount;
    }

    public long getNetAmountBeforeVat() {
        return netAmountBeforeVat;
    }

    public long getAmountAfterVatBeforePoints() {
        return amountAfterVatBeforePoints;
    }
}

