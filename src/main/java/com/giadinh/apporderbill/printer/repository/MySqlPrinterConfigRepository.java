package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrinterConfig;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

public class MySqlPrinterConfigRepository implements PrinterConfigRepository {
    private final MySqlConnectionProvider connectionProvider;

    public MySqlPrinterConfigRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        ensureDefaultRow();
    }

    @Override
    public PrinterConfig save(PrinterConfig config) {
        String sql = """
                INSERT INTO printer_configs(id, printer_name, connection_type, paper_size, copies, default_kitchen, default_receipt)
                VALUES (1, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    printer_name=VALUES(printer_name),
                    connection_type=VALUES(connection_type),
                    paper_size=VALUES(paper_size),
                    copies=VALUES(copies),
                    default_kitchen=VALUES(default_kitchen),
                    default_receipt=VALUES(default_receipt)
                """;
        try (var c = connectionProvider.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, config.getPrinterName());
            ps.setString(2, config.getConnectionType());
            ps.setString(3, config.getPaperSize());
            ps.setInt(4, config.getCopies());
            ps.setInt(5, config.isDefaultKitchen() ? 1 : 0);
            ps.setInt(6, config.isDefaultReceipt() ? 1 : 0);
            ps.executeUpdate();
        } catch (Exception ignored) {}
        return config;
    }

    @Override
    public PrinterConfig getCurrent() {
        String sql = """
                SELECT printer_name, connection_type, paper_size, copies, default_kitchen, default_receipt
                FROM printer_configs WHERE id = 1
                """;
        try (var c = connectionProvider.getConnection(); var ps = c.prepareStatement(sql); var rs = ps.executeQuery()) {
            if (rs.next()) {
                return new PrinterConfig(
                        rs.getString("printer_name"),
                        rs.getString("connection_type"),
                        rs.getString("paper_size"),
                        rs.getInt("copies"),
                        rs.getInt("default_kitchen") == 1,
                        rs.getInt("default_receipt") == 1
                );
            }
        } catch (Exception ignored) {}
        return defaults();
    }

    private void ensureDefaultRow() {
        save(defaults());
    }

    private PrinterConfig defaults() {
        return new PrinterConfig("Default Printer", "WINDOWS", "80mm", 1, true, true);
    }
}
