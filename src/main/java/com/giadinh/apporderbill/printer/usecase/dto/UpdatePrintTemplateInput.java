package com.giadinh.apporderbill.printer.usecase.dto;

public class UpdatePrintTemplateInput {
    private final String templateType;
    private final String storeName;
    private final String storeAddress;
    private final String storePhone;
    private final String header;
    private final String footer;

    public UpdatePrintTemplateInput(String templateType, String storeName, String storeAddress, String storePhone,
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

