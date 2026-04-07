package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

public class SqlitePrintTemplateRepository implements PrintTemplateRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqlitePrintTemplateRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        initializeDefaults();
    }

    private void initializeDefaults() {
        try (var c = connectionProvider.getConnection();
                var ps = c.prepareStatement("""
                        INSERT OR IGNORE INTO print_templates(template_type, store_name, store_address, store_phone, header, footer)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """)) {
            // Phiếu hóa đơn (RECEIPT)
            ps.setString(1, "RECEIPT");
            ps.setString(2, "AppOrderBill");
            ps.setString(3, "");
            ps.setString(4, "");
            ps.setString(5, "");
            ps.setString(6, "Cảm ơn quý khách");
            ps.executeUpdate();

            // Phiếu bếp (KITCHEN)
            ps.setString(1, "KITCHEN");
            ps.setString(2, "AppOrderBill");
            ps.setString(3, "");
            ps.setString(4, "");
            ps.setString(5, "");
            ps.setString(6, "");
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Override
    public PrintTemplate save(PrintTemplate template) {
        if (template == null) {
            return null;
        }
        try (var c = connectionProvider.getConnection();
                var ps = c.prepareStatement("""
                        INSERT INTO print_templates(template_type, store_name, store_address, store_phone, header, footer)
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON CONFLICT(template_type) DO UPDATE SET
                            store_name=excluded.store_name,
                            store_address=excluded.store_address,
                            store_phone=excluded.store_phone,
                            header=excluded.header,
                            footer=excluded.footer
                        """)) {
            ps.setString(1, template.getTemplateType());
            ps.setString(2, template.getStoreName());
            ps.setString(3, template.getStoreAddress());
            ps.setString(4, template.getStorePhone());
            ps.setString(5, template.getHeader());
            ps.setString(6, template.getFooter());
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
        return template;
    }

    @Override
    public PrintTemplate getByType(String type) {
        if (type == null) {
            return null;
        }
        try (var c = connectionProvider.getConnection();
                var ps = c.prepareStatement("""
                        SELECT template_type, store_name, store_address, store_phone, header, footer
                        FROM print_templates
                        WHERE template_type = ?
                        """)) {
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
        } catch (Exception ignored) {
        }
        return null;
    }
}

