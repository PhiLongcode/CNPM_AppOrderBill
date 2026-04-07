package com.giadinh.apporderbill.billing.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.UUID;

public class Bill {
    private String billId;
    private String orderId; // ID của đơn hàng mà hóa đơn này thanh toán
    private LocalDateTime billDate;
    private double totalAmount; // Tổng số tiền của hóa đơn
    private double paidAmount; // Số tiền khách đã trả
    private BillStatus status;

    // Constructor cho Bill mới
    public Bill(String orderId, double totalAmount) {
        if (totalAmount <= 0) {
            throw new DomainException(ErrorCode.BILL_TOTAL_INVALID);
        }
        this.billId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.billDate = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.paidAmount = 0.0;
        this.status = BillStatus.PENDING; // Mặc định là đang chờ thanh toán
    }

    // Constructor cho việc tải Bill từ Repository
    public Bill(String billId, String orderId, LocalDateTime billDate, double totalAmount, double paidAmount, BillStatus status) {
        this.billId = billId;
        this.orderId = orderId;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.status = status;
    }

    public String getBillId() {
        return billId;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    // Phương thức thực hiện thanh toán cho hóa đơn
    public void processPayment(double amount) {
        if (amount <= 0) {
            throw new DomainException(ErrorCode.BILL_PAYMENT_AMOUNT_INVALID);
        }
        if (this.status == BillStatus.PENDING) {
            this.paidAmount += amount;
            if (this.paidAmount >= this.totalAmount) {
                this.status = BillStatus.PAID; // Nếu số tiền trả đủ hoặc hơn, chuyển sang PAID
            }
        } else if (this.status == BillStatus.PAID) {
            throw new DomainException(ErrorCode.BILL_ALREADY_PAID);
        } else {
            throw new DomainException(ErrorCode.BILL_PAYMENT_STATE_INVALID);
        }
    }
}
