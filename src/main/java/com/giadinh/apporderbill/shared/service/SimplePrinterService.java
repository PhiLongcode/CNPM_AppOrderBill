package com.giadinh.apporderbill.shared.service;

public class SimplePrinterService implements PrinterService {
    public SimplePrinterService(Object orderRepository, Object menuItemRepository, Object printTemplateRepository,
            Object printerConfigRepository) {
    }

    @Override
    public boolean printKitchenTicket(String content) {
        return true;
    }

    @Override
    public boolean printReceipt(String content) {
        return true;
    }

    @Override
    public boolean printTest(String content) {
        return true;
    }
}

