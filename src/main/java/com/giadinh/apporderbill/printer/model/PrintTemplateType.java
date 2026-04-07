package com.giadinh.apporderbill.printer.model;

/**
 * Loại mẫu in lưu trong bảng print_templates.template_type.
 *
 * Lưu ý: dùng String ở DB để dễ migrate; enum này chỉ để tránh typo trong code.
 */
public enum PrintTemplateType {
    RECEIPT,
    KITCHEN,
    DRAFT,
    TEST;

    public String key() {
        return name();
    }
}

