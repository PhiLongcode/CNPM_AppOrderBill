package com.giadinh.apporderbill.orders.service;

import java.time.LocalDateTime;

public interface OrderCodeService {
    String generate(String orderId, LocalDateTime orderDate);
}
