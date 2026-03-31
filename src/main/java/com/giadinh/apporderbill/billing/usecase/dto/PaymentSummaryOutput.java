package com.giadinh.apporderbill.billing.usecase.dto;

import java.time.LocalDateTime;

public class PaymentSummaryOutput {
    private final Long paymentId;
    private final String tableNumber;
    private final long finalAmount;
    private final String paymentMethod;
    private final LocalDateTime paidAt;

    public PaymentSummaryOutput(Long paymentId, String tableNumber, long finalAmount, String paymentMethod,
            LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.tableNumber = tableNumber;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.paidAt = paidAt;
    }

    public Long getPaymentId() { return paymentId; }
    public String getTableNumber() { return tableNumber; }
    public long getFinalAmount() { return finalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaidAt() { return paidAt; }
}

