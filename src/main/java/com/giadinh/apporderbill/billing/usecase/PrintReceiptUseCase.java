package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptInput;
import com.giadinh.apporderbill.shared.service.PrinterService;

public class PrintReceiptUseCase {
    private final PaymentRepository paymentRepository;
    private final PrinterService printerService;

    public PrintReceiptUseCase(PaymentRepository paymentRepository, PrinterService printerService) {
        this.paymentRepository = paymentRepository;
        this.printerService = printerService;
    }

    public void execute(PrintReceiptInput input) {
        if (input == null || input.getPaymentId() == null) {
            throw new DomainException(ErrorCode.PRINT_RECEIPT_PAYMENT_ID_REQUIRED);
        }

        var payment = paymentRepository.findById(input.getPaymentId())
                .orElseThrow(() -> new DomainException(ErrorCode.BILL_NOT_FOUND));

        try {
            if (!printerService.printReceipt(payment.getPaymentId(), payment.getOrderId())) {
                throw new DomainException(ErrorCode.PRINTER_RECEIPT_SEND_FAILED);
            }
        } catch (PrinterService.PrinterException e) {
            throw new DomainException(ErrorCode.PRINTER_RECEIPT_SEND_FAILED);
        }
    }
}
