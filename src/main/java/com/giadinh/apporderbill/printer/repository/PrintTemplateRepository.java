package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrintTemplate;

public interface PrintTemplateRepository {
    PrintTemplate save(PrintTemplate template);
    PrintTemplate getByType(String type);
}

