package com.giadinh.apporderbill.orders.model;

public enum OrderStatus {
    PENDING,        // Đơn hàng đang chờ xử lý (chưa thanh toán, chưa in bếp)
    IN_PROGRESS,    // Đơn hàng đang được phục vụ/chuẩn bị
    COMPLETED,      // Đơn hàng đã hoàn thành và thanh toán
    CANCELLED       // Đơn hàng đã bị hủy
}
