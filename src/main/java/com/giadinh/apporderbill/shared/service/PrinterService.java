package com.giadinh.apporderbill.shared.service;

import com.giadinh.apporderbill.printer.model.PrintTemplateType;

import java.util.List;

public interface PrinterService {
    class PrinterException extends Exception {
        public PrinterException(String message) { super(message); }
    }
    /**
     * In phiếu bếp.
     * @param content Nội dung phiếu bếp đã được định dạng.
     * @return true nếu in thành công, false nếu thất bại.
     */
    boolean printKitchenTicket(String content);

    /**
     * In hóa đơn thanh toán.
     * @param content Nội dung hóa đơn đã được định dạng.
     * @return true nếu in thành công, false nếu thất bại.
     */
    boolean printReceipt(String content);

    /**
     * In một nội dung kiểm tra.
     * @param content Nội dung kiểm tra.
     * @return true nếu in thành công, false nếu thất bại.
     */
    boolean printTest(String content);

    default boolean printDraftReceipt(Long orderId, long subtotal, Double discountPercent) throws PrinterException {
        return printReceipt("DRAFT RECEIPT #" + orderId + " subtotal=" + subtotal + " discount=" + discountPercent);
    }

    /**
     * API mức use-case: printer tự format + chọn template/cấu hình.
     * Mặc định fallback về print chuỗi (để không phá vỡ API-side PrinterService).
     */
    default boolean printKitchenTicket(Long orderId, boolean isAddOn, boolean isReprint) throws PrinterException {
        return printKitchenTicket("KITCHEN_TICKET orderId=" + orderId + " addOn=" + isAddOn + " reprint=" + isReprint);
    }

    /**
     * Phiếu bếp một phần (món chọn). Mặc định gửi stub chu\u1ed7i cho API/REST.
     */
    default boolean printKitchenTicketSelected(Long orderId, List<Long> orderItemIds) throws PrinterException {
        return printKitchenTicket("KITCHEN_TICKET_SELECTED orderId=" + orderId + " items=" + orderItemIds);
    }

    default boolean printReceipt(Long paymentId, String orderId) throws PrinterException {
        return printReceipt(paymentId, orderId, "Li\u00ean 1");
    }

    default boolean printReceipt(Long paymentId, String orderId, String copyLabel) throws PrinterException {
        return printReceipt("RECEIPT paymentId=" + paymentId + " orderId=" + orderId + " copy=" + copyLabel);
    }

    default boolean testPrint(PrintTemplateType type) throws PrinterException {
        return printTest("TEST_PRINT templateType=" + (type == null ? "null" : type.key()));
    }
}
