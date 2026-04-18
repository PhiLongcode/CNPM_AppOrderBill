package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.shared.util.PrintUtils;
import com.giadinh.apporderbill.shared.util.TemplateProcessor;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RichKitchenTicketFormatter {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("vi-VN"));

    public String format(Order order, PrintTemplate template, int lineWidth, String ticketTypeLabel) {
        if (order == null) return "";
        return formatItems(order, order.getItems(), template, lineWidth, ticketTypeLabel);
    }

    /**
     * Kitchen ticket for a subset of line items (selected print).
     */
    public String formatItems(Order order, List<OrderItem> items, PrintTemplate template, int lineWidth, String ticketTypeLabel) {
        if (order == null || items == null || items.isEmpty()) return "";
        if (lineWidth <= 0) lineWidth = 32;

        String ticketCode = kitchenTicketCode(order);
        String timeStr = order.getOrderDate() == null ? "" : order.getOrderDate().format(dateFormat);
        String typeLine = (ticketTypeLabel == null || ticketTypeLabel.isBlank()) ? "Phi\u1ebfp b\u1ebfp" : ticketTypeLabel;

        TemplateProcessor processor = new TemplateProcessor();
        processor.set("Ma_Don_Hang", order.getOrderId());
        processor.set("Ma_Don_Viet_Tat", order.getOrderCode() == null ? "" : order.getOrderCode());
        processor.set("Ma_Phieu_Bep", ticketCode);
        processor.set("Ten_Phong_Ban", order.getTableNumber());
        processor.set("Thoi_Gian", timeStr);
        processor.set("Loai_Phieu", typeLine);

        StringBuilder sb = new StringBuilder();

        if (template != null) {
            if (template.getStoreName() != null && !template.getStoreName().isBlank()) {
                sb.append(PrintUtils.centerText(processor.process(template.getStoreName()), lineWidth)).append("\n");
            }
            if (template.getStoreAddress() != null && !template.getStoreAddress().isBlank()) {
                sb.append(PrintUtils.centerText(processor.process(template.getStoreAddress()), lineWidth)).append("\n");
            }
            if (template.getStorePhone() != null && !template.getStorePhone().isBlank()) {
                sb.append(PrintUtils.centerText("\u0110T: " + processor.process(template.getStorePhone()), lineWidth)).append("\n");
            }
            if (template.getHeader() != null && !template.getHeader().isBlank()) {
                for (String line : processor.process(template.getHeader()).split("\\R")) {
                    sb.append(PrintUtils.centerText(line, lineWidth)).append("\n");
                }
            }
        }

        sb.append(PrintUtils.createLine(lineWidth, "=")).append("\n");
        sb.append("M\u00e3 phi\u1ebfu b\u1ebfp: ").append(ticketCode).append("\n");
        sb.append("Ph\u00f2ng b\u00e0n: ").append(order.getTableNumber() == null ? "" : order.getTableNumber()).append("\n");
        sb.append("Lo\u1ea1i phi\u1ebfu: ").append(typeLine).append("\n");
        sb.append("Th\u1eddi gian: ").append(timeStr).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "=")).append("\n");

        int dishCount = appendGroupedItemLines(sb, items, lineWidth);

        sb.append(PrintUtils.createLine(lineWidth, "=")).append("\n");
        sb.append("T\u1ed5ng s\u1ed1 m\u00f3n: ").append(dishCount).append("\n");

        if (template != null && template.getFooter() != null && !template.getFooter().isBlank()) {
            for (String line : processor.process(template.getFooter()).split("\\R")) {
                sb.append(PrintUtils.centerText(line, lineWidth)).append("\n");
            }
        }
        return sb.toString();
    }

    private static String kitchenTicketCode(Order order) {
        if (order == null) return "PB000000";
        if (order.getOrderCode() != null && !order.getOrderCode().isBlank()) {
            return order.getOrderCode().trim();
        }
        String oid = order.getOrderId();
        if (oid == null) return "PB000000";
        String digits = oid.replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            try {
                long n = Long.parseLong(digits);
                return "PB" + String.format("%06d", n % 1_000_000L);
            } catch (NumberFormatException ignored) {
            }
        }
        int h = Math.abs(oid.hashCode() % 1_000_000);
        return "PB" + String.format("%06d", h);
    }

    private int appendGroupedItemLines(StringBuilder sb, List<OrderItem> items, int lineWidth) {
        Map<String, Integer> grouped = new LinkedHashMap<>();
        Map<String, String> notes = new LinkedHashMap<>();
        for (OrderItem it : items) {
            String name = displayItemName(it);
            String note = it.getNote() == null ? "" : it.getNote().trim();
            String key = name + "||" + note;
            grouped.put(key, grouped.getOrDefault(key, 0) + it.getQuantity());
            if (!note.isEmpty()) {
                notes.put(key, note);
            }
        }

        int qtyColWidth = lineWidth >= 40 ? 12 : 10;
        int nameColWidth = Math.max(8, lineWidth - qtyColWidth - 1);

        sb.append(String.format("%-" + nameColWidth + "s%s\n",
                "T\u00ean m\u00f3n",
                PrintUtils.formatRight("S\u1ed1 l\u01b0\u1ee3ng", qtyColWidth)));
        sb.append(PrintUtils.createLine(lineWidth, "-")).append("\n");

        for (var e : grouped.entrySet()) {
            String key = e.getKey();
            int qty = e.getValue();
            String itemName = key.split("\\|\\|", 2)[0];
            var wrapped = PrintUtils.wordWrap(itemName, nameColWidth);
            for (int i = 0; i < wrapped.size(); i++) {
                String line = wrapped.get(i);
                String qtyCell = (i == 0) ? PrintUtils.formatRight(String.valueOf(qty), qtyColWidth)
                        : PrintUtils.formatRight("", qtyColWidth);
                sb.append(String.format("%-" + nameColWidth + "s%s\n", line, qtyCell));
            }
            String note = notes.get(key);
            if (note != null && !note.isBlank()) {
                appendKitchenNote(sb, note, lineWidth);
            }
        }
        return grouped.size();
    }

    private static void appendKitchenNote(StringBuilder sb, String note, int lineWidth) {
        String block = "  *** GHI CH\u00da: " + note + " ***";
        var wrapped = PrintUtils.wordWrap(block, lineWidth);
        for (String ln : wrapped) {
            sb.append(ln).append("\n");
        }
    }

    private static String displayItemName(OrderItem it) {
        String name = it.getMenuItemName() == null ? "" : it.getMenuItemName();
        if (it.getLoyaltyRedeemPoints() > 0) {
            return name + " (\u0111\u1ed5i \u0111i\u1ec3m)";
        }
        return name;
    }
}
