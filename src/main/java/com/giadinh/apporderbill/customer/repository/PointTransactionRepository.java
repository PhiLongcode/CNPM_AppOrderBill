package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.PointTransaction;

import java.time.LocalDateTime;
import java.util.List;

public interface PointTransactionRepository {

    /** Lưu một giao dịch điểm mới */
    PointTransaction save(PointTransaction transaction);

    /** Lịch sử điểm theo khách hàng (mới nhất lên đầu) */
    List<PointTransaction> findByCustomerId(Long customerId);

    /** Lịch sử trong khoảng thời gian */
    List<PointTransaction> findByCustomerIdAndDateBetween(Long customerId,
                                                          LocalDateTime from,
                                                          LocalDateTime to);
}
