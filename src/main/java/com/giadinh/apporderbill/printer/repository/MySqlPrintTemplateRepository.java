package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

public class MySqlPrintTemplateRepository implements PrintTemplateRepository {
    private final MySqlConnectionProvider connectionProvider;

    public MySqlPrintTemplateRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        initializeDefaults();
    }

    private void initializeDefaults() {
        save(new PrintTemplate("RECEIPT", "AppOrderBill", "", "", "", "Cam on quy khach"));
        save(new PrintTemplate("KITCHEN", "AppOrderBill", "", "", "", ""));
    }

    @Override
    public PrintTemplate save(PrintTemplate template) {
        String sql = """
                INSERT INTO print_templates(template_type, store_name, store_address, store_phone, header, footer)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    store_name = VALUES(store_name),
                    store_address = VALUES(store_address),
                    store_phone = VALUES(store_phone),
                    header = VALUES(header),
                    footer = VALUES(footer)
                """;
        try (var c = connectionProvider.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, template.getTemplateType());
            ps.setString(2, template.getStoreName());
            ps.setString(3, template.getStoreAddress());
            ps.setString(4, template.getStorePhone());
            ps.setString(5, template.getHeader());
            ps.setString(6, template.getFooter());
            ps.executeUpdate();
        } catch (Exception ignored) {}
        return template;
    }

    @Override
    public PrintTemplate getByType(String type) {
        String sql = "SELECT template_type, store_name, store_address, store_phone, header, footer FROM print_templates WHERE template_type = ?";
        try (var c = connectionProvider.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, type);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PrintTemplate(
                            rs.getString("template_type"),
                            rs.getString("store_name"),
                            rs.getString("store_address"),
                            rs.getString("store_phone"),
                            rs.getString("header"),
                            rs.getString("footer"));
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
