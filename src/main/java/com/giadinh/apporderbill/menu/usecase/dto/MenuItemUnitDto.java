package com.giadinh.apporderbill.menu.usecase.dto;

public class MenuItemUnitDto {
    private final String unitName;
    private final long ratio;
    private final long price;
    private final String sku;
    private final boolean isDefault;

    public MenuItemUnitDto(String unitName, long ratio, long price, String sku, boolean isDefault) {
        this.unitName = unitName;
        this.ratio = ratio;
        this.price = price;
        this.sku = sku;
        this.isDefault = isDefault;
    }

    public String getUnitName() { return unitName; }
    public long getRatio() { return ratio; }
    public long getPrice() { return price; }
    public String getSku() { return sku; }
    public boolean isDefault() { return isDefault; }
}

