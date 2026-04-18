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
import java.util.List;
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
        processor.set("Phuong_Thuc", payment.getPaymentMethod() == null ? "" : payment.getPaymentMethod());
        processor.set("Thu_Ngan", payment.getCashier() == null ? "" : payment.getCashier());
        processor.set("Ma_Khach_Hang", payment.getCustomerId() == null ? "" : String.valueOf(payment.getCustomerId()));
        processor.set("Tien_Truoc_VAT", money.format(payment.getNetAmountBeforeVat()));
        processor.set("VAT_Phan_Tram", String.format(Locale.US, "%.1f", payment.getVatPercent()));
        processor.set("Tien_VAT", money.format(payment.getVatAmount()));
        processor.set("Sau_VAT_Truoc_Diem", money.format(payment.getAmountAfterVatBeforePoints()));
        processor.set("Giam_Doi_Diem", money.format(payment.getPointsDiscountAmount()));

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

        sb.append(PrintUtils.centerText("--- H\u00d3A \u0110\u01a0N ---", lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        appendWrapped(sb, "H\u0110: #" + payment.getPaymentId(), lineWidth);
        appendWrapped(sb, "\u0110\u01a1n: " + payment.getOrderId(), lineWidth);
        if (order != null && order.getOrderCode() != null && !order.getOrderCode().isBlank()) {
            appendWrapped(sb, "M\u00e3: " + order.getOrderCode(), lineWidth);
        }
        if (order != null) {
            appendWrapped(sb, "B\u00e0n: " + order.getTableNumber(), lineWidth);
        }
        if (payment.getPaidAt() != null) {
            appendWrapped(sb, "Ng\u00e0y: " + payment.getPaidAt().format(dateFormat), lineWidth);
        }
        if (copyLabel != null && !copyLabel.isBlank()) {
            appendWrapped(sb, "Li\u00ean: " + copyLabel, lineWidth);
        }
        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");

        if (order != null) {
            Map<String, Group> groups = new LinkedHashMap<>();
            for (OrderItem it : order.getItems()) {
                String key = groupKey(it);
                groups.compute(key, (k, v) -> {
                    if (v == null) {
                        return new Group(it);
                    }
                    v.addItem(it);
                    return v;
                });
            }

            int priceWidth = lineWidth >= 40 ? 12 : 10;
            int totalWidth = lineWidth >= 40 ? 12 : 10;
            int paddingWidth = Math.max(1, lineWidth - priceWidth - totalWidth - 2);

            sb.append(String.format("%-" + paddingWidth + "s %" + priceWidth + "s %" + totalWidth + "s\n",
                    "M\u00f3n \u0103n", "\u0110.Gi\u00e1", "T.Ti\u1ec1n"));
            sb.append(PrintUtils.createLine(lineWidth, "-")).append("\n");

            for (Group g : groups.values()) {
                StringBuilder display = new StringBuilder(g.name).append(" x").append(g.qty);
                if (g.discountPercent != null && g.discountPercent > 0) {
                    display.append(" - Gi\u1ea3m ")
                            .append(String.format(Locale.US, "%.0f", g.discountPercent))
                            .append("%");
                }
                List<String> nameLines = PrintUtils.wordWrap(display.toString(), lineWidth);
                for (String nl : nameLines) {
                    sb.append(String.format("%-" + lineWidth + "s\n", nl));
                }
                String priceCell = PrintUtils.formatRight(money.format(Math.round(g.unitPrice)), priceWidth);
                String totalCell = PrintUtils.formatRight(money.format(g.lineTotalSum), totalWidth);
                sb.append(String.format("%-" + paddingWidth + "s %" + priceWidth + "s %" + totalWidth + "s\n",
                        "", priceCell, totalCell));
                if (!g.note.isBlank()) {
                    for (String noteLine : PrintUtils.wordWrap("  * " + g.note, lineWidth)) {
                        sb.append(noteLine).append("\n");
                    }
                }
            }
        }

        sb.append(PrintUtils.createLine(lineWidth, "-")).append("\n");
        appendLabelValue(sb, "T\u1ea1m t\u00ednh:", money.format(payment.getTotalAmount()), lineWidth);
        if (payment.getDiscountAmount() != null && payment.getDiscountAmount() > 0) {
            appendLabelValue(sb, "Gi\u1ea3m gi\u00e1:", money.format(payment.getDiscountAmount()), lineWidth);
        }
        if (payment.getDiscountPercent() != null && payment.getDiscountPercent() > 0) {
            appendLabelValue(sb,
                    "CK " + String.format(Locale.US, "%.1f", payment.getDiscountPercent()) + "%",
                    "",
                    lineWidth);
        }
        appendLabelValue(sb, "Ti\u1ec1n tr\u01b0\u1edbc VAT:", money.format(payment.getNetAmountBeforeVat()), lineWidth);
        if (payment.getVatPercent() > 0 || payment.getVatAmount() > 0) {
            appendLabelValue(sb,
                    "VAT (" + String.format(Locale.US, "%.1f", payment.getVatPercent()) + "%):",
                    money.format(payment.getVatAmount()),
                    lineWidth);
        }
        if (payment.getPointsDiscountAmount() > 0) {
            appendLabelValue(sb,
                    "Sau VAT (tr\u01b0\u1edbc \u0111i\u1ec3m):",
                    money.format(payment.getAmountAfterVatBeforePoints()),
                    lineWidth);
            appendLabelValue(sb,
                    "Gi\u1ea3m \u0111\u1ed5i \u0111i\u1ec3m:",
                    money.format(payment.getPointsDiscountAmount()),
                    lineWidth);
        }
        sb.append(PrintUtils.createLine(lineWidth, "=")).append("\n");
        appendLabelValue(sb, "T\u1ed4NG C\u1ed8NG:", money.format(payment.getFinalAmount()), lineWidth);
        sb.append(PrintUtils.createLine(lineWidth, "=")).append("\n");
        appendLabelValue(sb, "Kh\u00e1ch tr\u1ea3:", money.format(payment.getPaidAmount()), lineWidth);
        appendLabelValue(sb, "Ti\u1ec1n th\u1eea:", money.format(change), lineWidth);
        if (payment.getPaymentMethod() != null && !payment.getPaymentMethod().isBlank()) {
            appendLabelValue(sb, "PTTT:", payment.getPaymentMethod(), lineWidth);
        }
        if (payment.getCashier() != null && !payment.getCashier().isBlank()) {
            appendLabelValue(sb, "Thu ng\u00e2n:", payment.getCashier(), lineWidth);
        }
        if (payment.getCustomerId() != null) {
            appendLabelValue(sb, "KH:", "#" + payment.getCustomerId(), lineWidth);
        }
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");

        if (template != null && template.getFooter() != null && !template.getFooter().isBlank()) {
            for (String line : processor.process(template.getFooter()).split("\\R")) {
                for (String w : PrintUtils.wordWrap(line, lineWidth)) {
                    sb.append(PrintUtils.centerText(w, lineWidth)).append("\n");
                }
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
        sb.append(PrintUtils.centerText("*** PHI\u1ebeU T\u1ea0M ***", lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        if (order != null) {
            sb.append("Đơn: ").append(order.getOrderId()).append("\n");
            sb.append("Bàn: ").append(order.getTableNumber()).append("\n");
        }
        sb.append(PrintUtils.createLine(lineWidth, "─")).append("\n");
        sb.append(PrintUtils.formatRight("Tạm tính: " + money.format(totalAmount), lineWidth)).append("\n");
        if (discountAmount > 0) {
            sb.append(PrintUtils.formatRight("Giảm giá: " + money.format(discountAmount), lineWidth)).append("\n");
        }
        sb.append(PrintUtils.formatRight("T\u1ed4NG C\u1ed8NG: " + money.format(finalAmount), lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "═")).append("\n");
        return sb.toString();
    }

    private static String itemDisplayName(OrderItem it) {
        String name = it.getMenuItemName() == null ? "" : it.getMenuItemName();
        if (it.getLoyaltyRedeemPoints() > 0) {
            return name + " (\u0111\u1ed5i \u0111i\u1ec3m)";
        }
        return name;
    }

    private static String groupKey(OrderItem it) {
        String name = itemDisplayName(it);
        String note = it.getNote() == null ? "" : it.getNote().trim();
        double dp = it.getDiscountPercent() == null ? 0.0 : it.getDiscountPercent();
        double da = it.getDiscountAmount() == null ? 0.0 : it.getDiscountAmount();
        return name + "||" + note + "||" + it.getPrice() + "||" + dp + "||" + da;
    }

    private static void appendWrapped(StringBuilder sb, String text, int lineWidth) {
        for (String ln : PrintUtils.wordWrap(text, lineWidth)) {
            sb.append(ln).append("\n");
        }
    }

    /**
     * Nhãn + giá trị căn phải: wordWrap nhãn để không tràn kh\u1ed5 gi\u1ea5y (tr\u00e1nh c\u1eaft ch\u1eef).
     */
    private static void appendLabelValue(StringBuilder sb, String label, String value, int lineWidth) {
        int valueW = Math.max(8, Math.min(14, lineWidth * 2 / 7));
        int labelW = Math.max(1, lineWidth - valueW - 1);
        List<String> labLines = PrintUtils.wordWrap(label, labelW);
        String v = PrintUtils.formatRight(value == null ? "" : value, valueW);
        for (int i = 0; i < labLines.size(); i++) {
            if (i < labLines.size() - 1) {
                sb.append(labLines.get(i)).append("\n");
            } else {
                sb.append(String.format("%-" + labelW + "s%s\n", labLines.get(i), v));
            }
        }
    }

    private static final class Group {
        final String name;
        final String note;
        final double unitPrice;
        int qty;
        long lineTotalSum;
        Double discountPercent;

        Group(OrderItem it) {
            this.name = itemDisplayName(it);
            this.note = it.getNote() == null ? "" : it.getNote().trim();
            this.unitPrice = it.getPrice();
            this.qty = it.getQuantity();
            this.lineTotalSum = Math.round(it.getLineTotal());
            Double dp = it.getDiscountPercent();
            this.discountPercent = (dp != null && dp > 0) ? dp : null;
        }

        void addItem(OrderItem it) {
            this.qty += it.getQuantity();
            this.lineTotalSum += Math.round(it.getLineTotal());
        }
    }
}
