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

        String content = buildReceiptContent(payment.getPaymentId(),
                payment.getOrderId(),
                payment.getFinalAmount(),
                payment.getPaidAmount(),
                payment.getPaymentMethod());
        if (!printerService.printReceipt(content)) {
            throw new DomainException(ErrorCode.PRINTER_RECEIPT_SEND_FAILED);
        }
    }

    private String buildReceiptContent(Long paymentId,
            String orderId,
            long finalAmount,
            long paidAmount,
            String paymentMethod) {
        long change = paidAmount - finalAmount;
        StringBuilder sb = new StringBuilder();
        sb.append("=== HOA DON THANH TOAN ===\n");
        sb.append("Payment: #").append(paymentId).append("\n");
        sb.append("Order: ").append(orderId).append("\n");
        sb.append("Phuong thuc: ").append(paymentMethod).append("\n");
        sb.append("Thanh tien: ").append(finalAmount).append(" VND\n");
        sb.append("Khach tra: ").append(paidAmount).append(" VND\n");
        sb.append("Tien thua: ").append(change).append(" VND\n");
        sb.append("==========================\n");
        return sb.toString();
    }
}
