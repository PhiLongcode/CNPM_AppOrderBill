package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.billing.model.Bill;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SimpleReceiptFormatter implements ReceiptFormatter {

    // Giới hạn ký tự cho máy in 80mm (thường là 32 hoặc 48 tùy font và cài đặt)
    private static final int MAX_CHARS_PER_LINE = 40;

    @Override
    public String formatReceipt(Bill bill, Order order) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("vi", "VN"));

        sb.append(centerText("--- HÓA ĐƠN THANH TOÁN ---", MAX_CHARS_PER_LINE)).append("\n");
        sb.append(centerText("Cửa hàng Gia Đình", MAX_CHARS_PER_LINE)).append("\n");
        sb.append(centerText("Địa chỉ: 123 Đường ABC, TP.HCM", MAX_CHARS_PER_LINE)).append("\n");
        sb.append(centerText("ĐT: 0123.456.789", MAX_CHARS_PER_LINE)).append("\n");
        sb.append(repeatChar('-', MAX_CHARS_PER_LINE)).append("\n");

        sb.append(String.format("Ngày: %s\n", bill.getBillDate().format(formatter)));
        sb.append(String.format("Bàn: %s\n", order.getTableId())); // Giả sử TableId là tên bàn
        sb.append(String.format("Mã HĐ: %s\n", bill.getBillId().substring(0, 8).toUpperCase()));
        sb.append(String.format("Mã ĐH: %s\n", order.getOrderId().substring(0, 8).toUpperCase()));
        sb.append(repeatChar('-', MAX_CHARS_PER_LINE)).append("\n");

        sb.append(String.format("%-25s %-5s %s\n", "Món", "SL", "Thành tiền"));
        sb.append(repeatChar('.', MAX_CHARS_PER_LINE)).append("\n");

        for (OrderItem item : order.getItems()) {
            sb.append(String.format("%-25s %-5d %,.0f\n", 
                                    truncateText(item.getMenuItemName(), 25),
                                    item.getQuantity(),
                                    item.getQuantity() * item.getPrice()));
            if (item.getNote() != null && !item.getNote().isEmpty()) {
                sb.append(String.format("  * %s\n", truncateText(item.getNote(), MAX_CHARS_PER_LINE - 4)));
            }
        }

        sb.append(repeatChar('-', MAX_CHARS_PER_LINE)).append("\n");
        sb.append(String.format("%-25s %s %,.0f VND\n", "TỔNG CỘNG", "", bill.getTotalAmount()));
        sb.append(String.format("%-25s %s %,.0f VND\n", "Đã thanh toán", "", bill.getPaidAmount()));
        sb.append(String.format("%-25s %s %,.0f VND\n", "Tiền thừa", "", bill.getPaidAmount() - bill.getTotalAmount()));
        sb.append(repeatChar('=', MAX_CHARS_PER_LINE)).append("\n");

        sb.append(centerText("Cảm ơn quý khách và hẹn gặp lại!", MAX_CHARS_PER_LINE)).append("\n");
        sb.append(centerText("Powered by AppOrderBill", MAX_CHARS_PER_LINE)).append("\n");

        return sb.toString();
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return String.format("%" + padding + "s%s%" + (width - text.length() - padding) + "s", "", text, "");
    }

    private String repeatChar(char c, int count) {
        return String.valueOf(c).repeat(Math.max(0, count));
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }
}
