package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.shared.util.PrintUtils;
import com.giadinh.apporderbill.shared.util.TemplateProcessor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RichKitchenTicketFormatter {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("vi-VN"));

    public String format(Order order, PrintTemplate template, int lineWidth, String ticketTypeLabel) {
        if (order == null) return "";
        if (lineWidth <= 0) lineWidth = 32;

        TemplateProcessor processor = new TemplateProcessor();
        processor.set("Ma_Don_Hang", order.getOrderId());
        processor.set("Ten_Phong_Ban", order.getTableNumber());
        processor.set("Thoi_Gian", order.getOrderDate() == null ? "" : order.getOrderDate().format(dateFormat));

        StringBuilder sb = new StringBuilder();

        // Header store info
        if (template != null) {
            if (template.getStoreName() != null && !template.getStoreName().isBlank()) {
                sb.append(PrintUtils.centerText(processor.process(template.getStoreName()), lineWidth)).append("\n");
            }
            if (template.getStoreAddress() != null && !template.getStoreAddress().isBlank()) {
                sb.append(PrintUtils.centerText(processor.process(template.getStoreAddress()), lineWidth)).append("\n");
            }
            if (template.getStorePhone() != null && !template.getStorePhone().isBlank()) {
                sb.append(PrintUtils.centerText("ĐT: " + processor.process(template.getStorePhone()), lineWidth)).append("\n");
            }
            if (template.getHeader() != null && !template.getHeader().isBlank()) {
                for (String line : processor.process(template.getHeader()).split("\\R")) {
                    sb.append(PrintUtils.centerText(line, lineWidth)).append("\n");
                }
            }
        }

        sb.append(PrintUtils.centerText("--- PHIẾU BẾP ---", lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        sb.append("Bàn: ").append(order.getTableNumber()).append("\n");
        sb.append("Đơn: ").append(order.getOrderId()).append("\n");
        sb.append("Loại: ").append(ticketTypeLabel == null ? "" : ticketTypeLabel).append("\n");
        sb.append("TG: ").append(order.getOrderDate() == null ? "" : order.getOrderDate().format(dateFormat)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");

        // Group items by name+note+price (simple)
        Map<String, Integer> grouped = new TreeMap<>();
        Map<String, String> notes = new TreeMap<>();
        for (OrderItem it : order.getItems()) {
            String name = it.getMenuItemName() == null ? "" : it.getMenuItemName();
            String note = it.getNote() == null ? "" : it.getNote().trim();
            String key = name + "||" + note;
            grouped.put(key, grouped.getOrDefault(key, 0) + it.getQuantity());
            if (!note.isEmpty()) notes.put(key, note);
        }

        int qtyColWidth = 6;
        int nameColWidth = Math.max(10, lineWidth - qtyColWidth - 1);
        sb.append(String.format("%-" + nameColWidth + "s %" + qtyColWidth + "s\n", "Món", "SL"));
        sb.append(PrintUtils.createLine(lineWidth, "·")).append("\n");

        for (var e : grouped.entrySet()) {
            String key = e.getKey();
            int qty = e.getValue();
            String itemName = key.split("\\|\\|", 2)[0];
            for (String line : PrintUtils.wordWrap(itemName, nameColWidth)) {
                if (line.equals(itemName) || line.equals(PrintUtils.wordWrap(itemName, nameColWidth).get(0))) {
                    sb.append(String.format("%-" + nameColWidth + "s %" + qtyColWidth + "d\n", line, qty));
                } else {
                    sb.append(String.format("%-" + nameColWidth + "s\n", line));
                }
            }
            String note = notes.get(key);
            if (note != null && !note.isBlank()) {
                sb.append("  * ").append(note).append("\n");
            }
            sb.append("\n");
        }

        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");

        if (template != null && template.getFooter() != null && !template.getFooter().isBlank()) {
            for (String line : processor.process(template.getFooter()).split("\\R")) {
                sb.append(PrintUtils.centerText(line, lineWidth)).append("\n");
            }
        }
        return sb.toString();
    }
}

