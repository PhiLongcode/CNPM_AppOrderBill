package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SimpleKitchenTicketFormatter implements KitchenTicketFormatter {

    private static final int MAX_CHARS_PER_LINE = 40; // Giới hạn ký tự cho máy in 80mm

    @Override
    public String formatKitchenTicket(Order order) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy", new Locale("vi", "VN"));

        sb.append(centerText("--- PHIẾU BẾP ---", MAX_CHARS_PER_LINE)).append("\n");
        sb.append(repeatChar('-', MAX_CHARS_PER_LINE)).append("\n");
        sb.append(String.format("Thời gian: %s\n", order.getOrderDate().format(formatter)));
        sb.append(String.format("Bàn số: %s\n", order.getTableId()));
        sb.append(String.format("Mã ĐH: %s\n", order.getOrderId().substring(0, 8).toUpperCase()));
        sb.append(repeatChar('-', MAX_CHARS_PER_LINE)).append("\n");

        sb.append(String.format("%-25s %s\n", "Món", "SL"));
        sb.append(repeatChar('.', MAX_CHARS_PER_LINE)).append("\n");

        for (OrderItem item : order.getItems()) {
            sb.append(String.format("%-25s x %d\n", truncateText(item.getMenuItemName(), 25), item.getQuantity()));
            if (item.getNote() != null && !item.getNote().isEmpty()) {
                sb.append(String.format("  * %s\n", truncateText(item.getNote(), MAX_CHARS_PER_LINE - 4)));
            }
        }

        sb.append(repeatChar('=', MAX_CHARS_PER_LINE)).append("\n");
        sb.append(centerText("--- HẾT PHIẾU ---", MAX_CHARS_PER_LINE)).append("\n");

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
