package com.giadinh.apporderbill.shared.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Template processor đơn giản: thay thế biến theo dạng {{KEY}}.
 * Dùng cho header/footer/storeName trong PrintTemplate khi muốn chèn dữ liệu động.
 */
public class TemplateProcessor {
    private final Map<String, Object> vars = new HashMap<>();

    public TemplateProcessor set(String key, Object value) {
        if (key == null) return this;
        vars.put(key, value);
        return this;
    }

    public String process(String template) {
        if (template == null) return "";
        String out = template;
        for (var e : vars.entrySet()) {
            String k = "{{" + e.getKey() + "}}";
            String v = e.getValue() == null ? "" : String.valueOf(e.getValue());
            out = out.replace(k, v);
        }
        return out;
    }
}

