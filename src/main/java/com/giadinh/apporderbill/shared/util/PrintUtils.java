package com.giadinh.apporderbill.shared.util;

import java.util.ArrayList;
import java.util.List;

public final class PrintUtils {
    private PrintUtils() {}

    /**
     * Ước lượng số ký tự mỗi dòng dựa trên khổ giấy (mm).
     * Công thức kinh nghiệm: (paperSizeMm - marginMm*2) / 2.5
     */
    public static int calculateCharsPerLine(int paperSizeMm) {
        int marginMmEachSide = 2;
        double usableMm = Math.max(30, paperSizeMm - marginMmEachSide * 2.0);
        int chars = (int) Math.floor(usableMm / 2.5);
        if (chars < 24) chars = 24;
        if (chars > 48) chars = 48;
        return chars;
    }

    public static String createLine(int width, String ch) {
        if (width <= 0) return "";
        if (ch == null || ch.isEmpty()) ch = "-";
        return ch.repeat(width);
    }

    public static String centerText(String text, int width) {
        if (text == null) text = "";
        String t = text;
        if (t.length() >= width) return t;
        int left = (width - t.length()) / 2;
        int right = width - t.length() - left;
        return " ".repeat(left) + t + " ".repeat(right);
    }

    /**
     * Căn giữa text nhưng bỏ qua marker (ví dụ [FONT_SIZE...]) khi tính độ dài.
     */
    public static String centerTextWithMarker(String text, int width, String openMarker, String closeMarker) {
        if (text == null) text = "";
        String raw = text;
        String clean = raw;
        if (openMarker != null) clean = clean.replace(openMarker, "");
        if (closeMarker != null) clean = clean.replace(closeMarker, "");
        if (clean.length() >= width) {
            return (openMarker == null ? "" : openMarker) + raw + (closeMarker == null ? "" : closeMarker);
        }
        int left = (width - clean.length()) / 2;
        int right = width - clean.length() - left;
        return " ".repeat(left)
                + (openMarker == null ? "" : openMarker)
                + raw
                + (closeMarker == null ? "" : closeMarker)
                + " ".repeat(right);
    }

    public static List<String> wordWrap(String text, int width) {
        if (text == null) text = "";
        if (width <= 0) return List.of(text);
        String s = text.trim();
        if (s.isEmpty()) return List.of("");

        List<String> lines = new ArrayList<>();
        String[] words = s.split("\\s+");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            if (cur.isEmpty()) {
                cur.append(w);
            } else if (cur.length() + 1 + w.length() <= width) {
                cur.append(' ').append(w);
            } else {
                lines.add(cur.toString());
                cur.setLength(0);
                cur.append(w);
            }
        }
        if (!cur.isEmpty()) lines.add(cur.toString());
        return lines;
    }

    public static String formatRight(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(text.length() - width);
        return " ".repeat(width - text.length()) + text;
    }
}

