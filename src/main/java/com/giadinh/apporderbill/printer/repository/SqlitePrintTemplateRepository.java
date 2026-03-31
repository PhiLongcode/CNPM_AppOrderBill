package com.giadinh.apporderbill.printer.repository;

import com.giadinh.apporderbill.printer.model.PrintTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlitePrintTemplateRepository implements PrintTemplateRepository {
    private final Map<String, PrintTemplate> data = new ConcurrentHashMap<>();

    public SqlitePrintTemplateRepository(Object connectionProvider) {
        data.put("RECEIPT", new PrintTemplate("RECEIPT", "AppOrderBill", "", "", "", "Cam on quy khach"));
    }

    @Override
    public PrintTemplate save(PrintTemplate template) {
        data.put(template.getTemplateType(), template);
        return template;
    }

    @Override
    public PrintTemplate getByType(String type) {
        return data.get(type);
    }
}

