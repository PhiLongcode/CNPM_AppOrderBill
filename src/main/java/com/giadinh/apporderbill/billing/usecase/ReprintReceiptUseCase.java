package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.shared.service.PrinterService;

public class ReprintReceiptUseCase {
    private final PaymentRepository paymentRepository;
    private final PrinterService printerService;

    public ReprintReceiptUseCase(PaymentRepository paymentRepository, PrinterService printerService) {
        this.paymentRepository = paymentRepository;
        this.printerService = printerService;
    }

    public void execute(Long paymentId) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new DomainException(ErrorCode.BILL_NOT_FOUND));
        String content = "=== IN LAI HOA DON ===\n"
                + "Payment: #" + payment.getPaymentId() + "\n"
                + "Order: " + payment.getOrderId() + "\n"
                + "Thanh tien: " + payment.getFinalAmount() + " VND\n"
                + "Khach tra: " + payment.getPaidAmount() + " VND\n"
                + "Tien thua: " + (payment.getPaidAmount() - payment.getFinalAmount()) + " VND\n"
                + "======================\n";
        printerService.printReceipt(content);
    }
}

