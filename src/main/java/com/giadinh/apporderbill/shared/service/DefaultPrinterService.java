package com.giadinh.apporderbill.shared.service;

public class DefaultPrinterService implements PrinterService {

    @Override
    public boolean printKitchenTicket(String content) {
        System.out.println("--- IN PHIẾU BẾP (DEFAULT - CONSOLE) ---");
        System.out.println(content);
        System.out.println("--------------------------------------");
        return true;
    }

    @Override
    public boolean printReceipt(String content) {
        System.out.println("--- IN HÓA ĐƠN (DEFAULT - CONSOLE) ---");
        System.out.println(content);
        System.out.println("-------------------------------------");
        return true;
    }

    @Override
    public boolean printTest(String content) {
        System.out.println("--- IN KIỂM TRA (DEFAULT - CONSOLE) ---");
        System.out.println(content);
        System.out.println("-------------------------------------");
        return true;
    }
}
