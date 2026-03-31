package com.giadinh.apporderbill.billing.usecase;

import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptInput;
import com.giadinh.apporderbill.billing.usecase.dto.PrintReceiptOutput;
import com.giadinh.apporderbill.shared.service.PrinterService;

public class PrintReceiptUseCase {
    private final PaymentRepository paymentRepository;
    private final PrinterService printerService;

    public PrintReceiptUseCase(PaymentRepository paymentRepository, PrinterService printerService) {
        this.paymentRepository = paymentRepository;
        this.printerService = printerService;
    }

    public PrintReceiptOutput execute(PrintReceiptInput input) {
        if (input == null || input.getPaymentId() == null) {
            return new PrintReceiptOutput(false, "Thiếu paymentId để in hóa đơn.");
        }

        var paymentOpt = paymentRepository.findById(input.getPaymentId());
        if (paymentOpt.isEmpty()) {
            return new PrintReceiptOutput(false, "Không tìm thấy giao dịch thanh toán.");
        }

        var payment = paymentOpt.get();
        String content = buildReceiptContent(payment.getPaymentId(),
                payment.getOrderId(),
                payment.getFinalAmount(),
                payment.getPaidAmount(),
                payment.getPaymentMethod());
        boolean printed = printerService.printReceipt(content);
        if (!printed) {
            return new PrintReceiptOutput(false, "Gửi lệnh in thất bại.");
        }
        return new PrintReceiptOutput(true, "In hóa đơn thành công.");
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
