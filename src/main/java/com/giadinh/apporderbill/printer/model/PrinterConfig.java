package com.giadinh.apporderbill.printer.model;

public class PrinterConfig {
    private String printerName;
    private String connectionType;
    private String paperSize;
    private int copies;
    private boolean defaultKitchen;
    private boolean defaultReceipt;

    public PrinterConfig(String printerName, String connectionType, String paperSize, int copies,
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

