package com.giadinh.apporderbill.shared.formatter;

import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.printer.model.PrintTemplateType;
import com.giadinh.apporderbill.shared.util.PrintUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TestPrintFormatter {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("vi-VN"));

    public String format(PrintTemplateType type, PrintTemplate template, int lineWidth) {
        StringBuilder sb = new StringBuilder();
        String title = type == null ? "TEST" : type.key();

        sb.append(PrintUtils.centerText("=== TEST PRINT ===", lineWidth)).append("\n");
        sb.append(PrintUtils.centerText(title, lineWidth)).append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "-")).append("\n");

        if (template != null) {
            if (template.getStoreName() != null && !template.getStoreName().isBlank()) {
                sb.append(PrintUtils.centerText(template.getStoreName(), lineWidth)).append("\n");
            }
            if (template.getStoreAddress() != null && !template.getStoreAddress().isBlank()) {
                sb.append(PrintUtils.centerText(template.getStoreAddress(), lineWidth)).append("\n");
            }
            if (template.getStorePhone() != null && !template.getStorePhone().isBlank()) {
                sb.append(PrintUtils.centerText("ĐT: " + template.getStorePhone(), lineWidth)).append("\n");
            }
        }

        sb.append(PrintUtils.createLine(lineWidth, "-")).append("\n");
        sb.append("Thời gian: ").append(LocalDateTime.now().format(dateFormat)).append("\n");
        sb.append("LineWidth: ").append(lineWidth).append(" chars").append("\n");
        sb.append(PrintUtils.createLine(lineWidth, "-")).append("\n");
        sb.append("Dòng 1: 0123456789\n");
        sb.append("Dòng 2: ABCDEFGHIJKLMNOPQRSTUVWXYZ\n");
        sb.append(PrintUtils.createLine(lineWidth, "=")).append("\n");
        sb.append(PrintUtils.centerText("OK", lineWidth)).append("\n");
        return sb.toString();
    }
}

