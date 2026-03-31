package com.giadinh.apporderbill.shared.util;

import java.text.Normalizer;

public final class VietnameseTextUtils {
    private VietnameseTextUtils() {}

    public static boolean containsIgnoreVietnameseAccents(String source, String keyword) {
        if (source == null || keyword == null) {
            return false;
        }
        String src = normalize(source);
        String key = normalize(keyword);
        return src.contains(key);
    }

    public static String normalize(String input) {
        String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return noAccent.toLowerCase();
    }
}

