package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.billing.model.Bill;
import com.giadinh.apporderbill.orders.model.Order;

public interface ReceiptFormatter {
    String formatReceipt(Bill bill, Order order);
}
