package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrinterConfig;

public class SqlitePrinterConfigRepository implements PrinterConfigRepository {
    private PrinterConfig current = new PrinterConfig("Default Printer", "WINDOWS", "80mm", 1, true, true);

    public SqlitePrinterConfigRepository(Object connectionProvider) {
    }

    @Override
    public PrinterConfig save(PrinterConfig config) {
        this.current = config;
        return config;
    }

    @Override
    public PrinterConfig getCurrent() {
        return current;
    }
}

