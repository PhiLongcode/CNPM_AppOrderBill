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
        try {
            if (!printerService.printReceipt(payment.getPaymentId(), payment.getOrderId(), "IN L\u1ea0I")) {
                throw new DomainException(ErrorCode.PRINTER_RECEIPT_SEND_FAILED);
            }
        } catch (PrinterService.PrinterException e) {
            throw new DomainException(ErrorCode.PRINTER_RECEIPT_SEND_FAILED);
        }
    }
}

