package com.giadinh.apporderbill.orders.usecase.dto;

public class CheckoutOrderOutput {
    private boolean success;
    private String message;
    private String billId; // ID của hóa đơn được tạo

    public CheckoutOrderOutput(boolean success, String message, String billId) {
        this.success = success;
        this.message = message;
        this.billId = billId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getBillId() {
        return billId;
    }
}
