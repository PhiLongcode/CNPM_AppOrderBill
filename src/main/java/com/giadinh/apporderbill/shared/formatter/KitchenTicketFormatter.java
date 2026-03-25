package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.orders.model.Order;

public interface KitchenTicketFormatter {
    String formatKitchenTicket(Order order);
}
