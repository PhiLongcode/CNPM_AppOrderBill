package com.giadinh.apporderbill.billing;

import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptInput;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptOutput;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BillingComponent {
    List<PaymentSummaryOutput> getTodayPayments();
    List<PaymentSummaryOutput> getPaymentsByDate(LocalDate date);
    List<PaymentSummaryOutput> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    PaymentDetailOutput getPaymentDetail(Long paymentId);
    void deletePaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    PrintReceiptOutput printReceipt(PrintReceiptInput input);
    PrintReceiptOutput reprintReceipt(Long paymentId);
}

