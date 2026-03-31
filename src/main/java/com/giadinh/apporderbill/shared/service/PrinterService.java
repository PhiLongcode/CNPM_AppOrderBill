package com.giadinh.apporderbill.shared.service;

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
}
