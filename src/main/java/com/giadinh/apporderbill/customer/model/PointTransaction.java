package com.giadinh.apporderbill.customer.model;

import java.time.LocalDateTime;

/**
 * Lịch sử giao dịch điểm của một khách hàng.
 * M��i bản ghi thể hiện 1 lần cộng hoặc tr�� điểm.
 */
public class PointTransaction {

    public enum Type {
        EARN,
        REDEEM,
        REDEEM_DISH,
        REDEEM_GIFT
    }

    private Long id;
    private Long customerId;
    private int delta;
    private int balanceAfter;
    private Type type;
    private String note;
    private String orderId;
    private LocalDateTime createdAt;

    public PointTransaction(Long id, Long customerId, int delta, int balanceAfter,
                            Type type, String note, String orderId, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.delta = delta;
        this.balanceAfter = balanceAfter;
        this.type = type;
        this.note = note;
        this.orderId = orderId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public int getDelta() { return delta; }
    public int getBalanceAfter() { return balanceAfter; }
    public Type getType() { return type; }
    public String getNote() { return note; }
    public String getOrderId() { return orderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
