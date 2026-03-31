package com.giadinh.apporderbill.printer.usecase.dto;

public class PrinterConfigOutput {
    private final String printerName;
    private final String connectionType;
    private final String paperSize;
    private final int copies;
    private final boolean defaultKitchen;
    private final boolean defaultReceipt;

    public PrinterConfigOutput(String printerName, String connectionType, String paperSize, int copies,
            boolean defaultKitchen, boolean defaultReceipt) {
        this.printerName = printerName;
        this.connectionType = connectionType;
        this.paperSize = paperSize;
        this.copies = copies;
        this.defaultKitchen = defaultKitchen;
        this.defaultReceipt = defaultReceipt;
    }

    public String getPrinterName() { return printerName; }
    public String getConnectionType() { return connectionType; }
    public String getPaperSize() { return paperSize; }
    public int getCopies() { return copies; }
    public boolean isDefaultKitchen() { return defaultKitchen; }
    public boolean isDefaultReceipt() { return defaultReceipt; }
}

