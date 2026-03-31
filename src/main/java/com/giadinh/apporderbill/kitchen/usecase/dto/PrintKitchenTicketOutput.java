package com.giadinh.apporderbill.kitchen.usecase.dto;

public class PrintKitchenTicketOutput {
    private final boolean printed;
    private final String printError;

    public PrintKitchenTicketOutput(boolean printed, String printError) {
        this.printed = printed;
        this.printError = printError;
    }

    public boolean isPrinted() {
        return printed;
    }

    public String getPrintError() {
        return printError;
    }
}

