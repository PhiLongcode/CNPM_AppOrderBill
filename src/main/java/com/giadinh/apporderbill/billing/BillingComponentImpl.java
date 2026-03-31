package com.giadinh.apporderbill.billing;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.DeletePaymentsByDateRangeUseCase;
import com.giadinh.apporderbill.billing.usecase.GetPaymentDetailUseCase;
import com.giadinh.apporderbill.billing.usecase.GetPaymentsByDateRangeUseCase;
import com.giadinh.apporderbill.billing.usecase.GetTodayPaymentsUseCase;
import com.giadinh.apporderbill.billing.usecase.PrintReceiptUseCase;
import com.giadinh.apporderbill.billing.usecase.ReprintReceiptUseCase;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptInput;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptOutput;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.shared.service.PrinterService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BillingComponentImpl implements BillingComponent {
    private final GetTodayPaymentsUseCase getTodayPaymentsUseCase;
    private final GetPaymentsByDateRangeUseCase getPaymentsByDateRangeUseCase;
    private final GetPaymentDetailUseCase getPaymentDetailUseCase;
    private final DeletePaymentsByDateRangeUseCase deletePaymentsByDateRangeUseCase;
    private final PrintReceiptUseCase printReceiptUseCase;
    private final ReprintReceiptUseCase reprintReceiptUseCase;

    public BillingComponentImpl(PaymentRepository paymentRepository, OrderRepository orderRepository,
            Object menuItemRepository, PrinterService printerService) {
        this.getTodayPaymentsUseCase = new GetTodayPaymentsUseCase(paymentRepository, orderRepository);
        this.getPaymentsByDateRangeUseCase = new GetPaymentsByDateRangeUseCase(paymentRepository, orderRepository);
        this.getPaymentDetailUseCase = new GetPaymentDetailUseCase(paymentRepository, orderRepository, menuItemRepository);
        this.deletePaymentsByDateRangeUseCase = new DeletePaymentsByDateRangeUseCase(paymentRepository);
        this.printReceiptUseCase = new PrintReceiptUseCase(paymentRepository, printerService);
        this.reprintReceiptUseCase = new ReprintReceiptUseCase(paymentRepository, printerService);
    }

    @Override
    public List<PaymentSummaryOutput> getTodayPayments() { return getTodayPaymentsUseCase.execute(); }
    @Override
    public List<PaymentSummaryOutput> getPaymentsByDate(LocalDate date) {
        return getPaymentsByDateRangeUseCase.execute(date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusNanos(1));
    }
    @Override
    public List<PaymentSummaryOutput> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return getPaymentsByDateRangeUseCase.execute(start, end);
    }
    @Override
    public PaymentDetailOutput getPaymentDetail(Long paymentId) { return getPaymentDetailUseCase.execute(paymentId); }
    @Override
    public void deletePaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        deletePaymentsByDateRangeUseCase.execute(start, end);
    }
    @Override
    public PrintReceiptOutput printReceipt(PrintReceiptInput input) { return printReceiptUseCase.execute(input); }
    @Override
    public PrintReceiptOutput reprintReceipt(Long paymentId) {
        reprintReceiptUseCase.execute(paymentId);
        return new PrintReceiptOutput(true, "In lại hóa đơn thành công.");
    }
}

