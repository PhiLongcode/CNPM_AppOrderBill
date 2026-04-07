package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.billing.model.Payment;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.shared.util.PrintUtils;
import com.giadinh.apporderbill.shared.util.TemplateProcessor;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RichReceiptFormatter {
    private final NumberFormat money = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("vi-VN"));

    public String formatReceipt(Payment payment, Order order, PrintTemplate template, int lineWidth, String copyLabel) {
        if (payment == null) return "";
        if (lineWidth <= 0) lineWidth = 32;

        TemplateProcessor processor = new TemplateProcessor();
        processor.set("Ma_Hoa_Don", payment.getPaymentId());
        processor.set("Ma_Don_Hang", payment.getOrderId());
        processor.set("Ten_Phong_Ban", order == null ? "" : order.getTableNumber());
        processor.set("Ngay_Ban", payment.getPaidAt() == null ? "" : payment.getPaidAt().format(dateFormat));
        processor.set("Tong_Tien_Hang", money.format(payment.getTotalAmount()));
        processor.set("Tong_Cong", money.format(payment.getFinalAmount()));
        processor.set("Khach_Thanh_Toan", money.format(payment.getPaidAmount()));

        long change = payment.getPaidAmount() - payment.getFinalAmount();

        StringBuilder sb = new StringBuilder();

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

        sb.append(PrintUtils.centerText("--- HÓA ĐƠN ---", lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        sb.append("HĐ: #").append(payment.getPaymentId()).append("\n");
        sb.append("Đơn: ").append(payment.getOrderId()).append("\n");
        if (order != null) sb.append("Bàn: ").append(order.getTableNumber()).append("\n");
        if (payment.getPaidAt() != null) sb.append("Ngày: ").append(payment.getPaidAt().format(dateFormat)).append("\n");
        if (copyLabel != null && !copyLabel.isBlank()) sb.append("Liên: ").append(copyLabel).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");

        if (order != null) {
            // group by name+note+price
            Map<String, Group> groups = new LinkedHashMap<>();
            for (OrderItem it : order.getItems()) {
                String name = it.getMenuItemName() == null ? "" : it.getMenuItemName();
                String note = it.getNote() == null ? "" : it.getNote().trim();
                String key = name + "||" + note + "||" + it.getPrice();
                groups.computeIfAbsent(key, k -> new Group(name, note, it.getPrice()))
                        .add(it.getQuantity());
            }

            int priceWidth = lineWidth >= 40 ? 12 : 10;
            int totalWidth = lineWidth >= 40 ? 12 : 10;
            int nameWidth = Math.max(10, lineWidth - priceWidth - totalWidth - 2);

            sb.append(String.format("%-" + nameWidth + "s %" + priceWidth + "s %" + totalWidth + "s\n",
                    "Món", "Đ.Giá", "T.Tiền"));
            sb.append(PrintUtils.createLine(lineWidth, "·")).append("\n");

            for (Group g : groups.values()) {
                String display = g.name + " x" + g.qty;
                long lineTotal = Math.round(g.unitPrice * g.qty);

                var wrapped = PrintUtils.wordWrap(display, nameWidth);
                for (int i = 0; i < wrapped.size(); i++) {
                    String ln = wrapped.get(i);
                    if (i == wrapped.size() - 1) {
                        sb.append(String.format("%-" + nameWidth + "s %" + priceWidth + "s %" + totalWidth + "s\n",
                                ln,
                                PrintUtils.formatRight(money.format(Math.round(g.unitPrice)), priceWidth),
                                PrintUtils.formatRight(money.format(lineTotal), totalWidth)));
                    } else {
                        sb.append(ln).append("\n");
                    }
                }
                if (!g.note.isBlank()) sb.append("  * ").append(g.note).append("\n");
            }
        }

        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");
        sb.append(PrintUtils.formatRight("Tạm tính: " + money.format(payment.getTotalAmount()), lineWidth)).append("\n");
        if (payment.getDiscountAmount() != null && payment.getDiscountAmount() > 0) {
            sb.append(PrintUtils.formatRight("Giảm giá: " + money.format(payment.getDiscountAmount()), lineWidth)).append("\n");
        }
        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");
        sb.append(PrintUtils.formatRight("TỔNG CỘNG: " + money.format(payment.getFinalAmount()), lineWidth)).append("\n");
        sb.append(PrintUtils.formatRight("Khách trả: " + money.format(payment.getPaidAmount()), lineWidth)).append("\n");
        sb.append(PrintUtils.formatRight("Tiền thừa: " + money.format(change), lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");

        if (template != null && template.getFooter() != null && !template.getFooter().isBlank()) {
            for (String line : processor.process(template.getFooter()).split("\\R")) {
                sb.append(PrintUtils.centerText(line, lineWidth)).append("\n");
            }
        }
        return sb.toString();
    }

    public String formatDraftReceipt(Order order, long totalAmount, long discountAmount, long finalAmount, PrintTemplate template, int lineWidth) {
        if (lineWidth <= 0) lineWidth = 32;
        StringBuilder sb = new StringBuilder();

        if (template != null && template.getStoreName() != null && !template.getStoreName().isBlank()) {
            sb.append(PrintUtils.centerText(template.getStoreName(), lineWidth)).append("\n");
        }
        sb.append(PrintUtils.centerText("*** PHIẾU TẠM ***", lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        if (order != null) {
            sb.append("Đơn: ").append(order.getOrderId()).append("\n");
            sb.append("Bàn: ").append(order.getTableNumber()).append("\n");
        }
        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");
        sb.append(PrintUtils.formatRight("Tạm tính: " + money.format(totalAmount), lineWidth)).append("\n");
        if (discountAmount > 0) sb.append(PrintUtils.formatRight("Giảm giá: " + money.format(discountAmount), lineWidth)).append("\n");
        sb.append(PrintUtils.formatRight("TỔNG CỘNG: " + money.format(finalAmount), lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        return sb.toString();
    }

    private static final class Group {
        final String name;
        final String note;
        final double unitPrice;
        int qty;

        Group(String name, String note, double unitPrice) {
            this.name = name == null ? "" : name;
            this.note = note == null ? "" : note;
            this.unitPrice = unitPrice;
        }

        void add(int q) { qty += q; }
    }
}

