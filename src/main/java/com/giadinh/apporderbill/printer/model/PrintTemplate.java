package com.giadinh.apporderbill.printer.model;

public class PrintTemplate {
    private String templateType;
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private String header;
    private String footer;

    public PrintTemplate(String templateType, String storeName, String storeAddress, String storePhone,
            String header, String footer) {
        this.templateType = templateType;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storePhone = storePhone;
        this.header = header;
        this.footer = footer;
    }

    public String getTemplateType() { return templateType; }
    public String getStoreName() { return storeName; }
    public String getStoreAddress() { return storeAddress; }
    public String getStorePhone() { return storePhone; }
    public String getHeader() { return header; }
    public String getFooter() { return footer; }
}

