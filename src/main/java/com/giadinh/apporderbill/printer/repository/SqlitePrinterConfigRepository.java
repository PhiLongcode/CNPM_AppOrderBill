package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrinterConfig;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

public class SqlitePrinterConfigRepository implements PrinterConfigRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqlitePrinterConfigRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public PrinterConfig save(PrinterConfig config) {
        if (config == null) {
            return getCurrent();
        }
        try (var c = connectionProvider.getConnection();
                var ps = c.prepareStatement("""
                        INSERT INTO printer_configs(printer_name, connection_type, paper_size, copies, default_kitchen, default_receipt)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """)) {
            ps.setString(1, config.getPrinterName());
            ps.setString(2, config.getConnectionType());
            ps.setString(3, config.getPaperSize());
            ps.setInt(4, config.getCopies());
            ps.setInt(5, config.isDefaultKitchen() ? 1 : 0);
            ps.setInt(6, config.isDefaultReceipt() ? 1 : 0);
            ps.executeUpdate();
        } catch (Exception ignored) {
            // Keep behavior non-fatal for POS: if DB write fails, fallback to in-memory via getCurrent()
        }
        return config;
    }

    @Override
    public PrinterConfig getCurrent() {
        try (var c = connectionProvider.getConnection();
                var ps = c.prepareStatement("""
                        SELECT printer_name, connection_type, paper_size, copies, default_kitchen, default_receipt
                        FROM printer_configs
                        ORDER BY id DESC
                        LIMIT 1
                        """);
                var rs = ps.executeQuery()) {
            if (rs.next()) {
                return new PrinterConfig(
                        rs.getString("printer_name"),
                        rs.getString("connection_type"),
                        rs.getString("paper_size"),
                        rs.getInt("copies"),
                        rs.getInt("default_kitchen") == 1,
                        rs.getInt("default_receipt") == 1);
            }
        } catch (Exception ignored) {
        }
        // Nếu chưa có bản ghi nào, khởi tạo cấu hình mặc định và lưu vào DB
        PrinterConfig defaults = new PrinterConfig("Default Printer", "WINDOWS", "80mm", 1, true, true);
        save(defaults);
        return defaults;
    }
}

