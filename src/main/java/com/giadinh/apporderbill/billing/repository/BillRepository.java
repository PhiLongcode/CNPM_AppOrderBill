package com.giadinh.apporderbill.billing.repository;

import com.giadinh.apporderbill.billing.model.Bill;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository {
    Optional<Bill> findById(String billId);
    Optional<Bill> findByOrderId(String orderId);
    void save(Bill bill);
    List<Bill> findBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
