package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrinterConfig;

public interface PrinterConfigRepository {
    PrinterConfig save(PrinterConfig config);
    PrinterConfig getCurrent();
}

