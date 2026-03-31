package com.giadinh.apporderbill.orders.usecase.dto;

public class CancelOrderInput {
    private final Long orderId;
    private final String reason;
    private final String operator;

    public CancelOrderInput(Long orderId, String reason, String operator) {
        this.orderId = orderId;
        this.reason = reason;
        this.operator = operator;
    }

    public Long getOrderId() { return orderId; }
    public String getReason() { return reason; }
    public String getOperator() { return operator; }
}

