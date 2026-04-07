package com.giadinh.apporderbill.catalog.usecase.dto;

public class MenuItemOutput {
    private final Long menuItemId;
    private final String name;
    private final String category;
    private final Long unitPrice;
    private final String imageUrl;
    private final String baseUnit;
    private final boolean stockTracked;
    private final Long stockQty;
    private final Long stockMin;
    private final boolean active;

    public MenuItemOutput(Long menuItemId, String name, String category, Long unitPrice, String imageUrl,
            String baseUnit, boolean stockTracked, Long stockQty, Long stockMin, boolean active) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.baseUnit = baseUnit;
        this.stockTracked = stockTracked;
        this.stockQty = stockQty;
        this.stockMin = stockMin;
        this.active = active;
    }

    public Long getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public Long getUnitPrice() { return unitPrice; }
    public String getImageUrl() { return imageUrl; }
    public String getBaseUnit() { return baseUnit; }
    public boolean isStockTracked() { return stockTracked; }
    public Long getStockQty() { return stockQty; }
    public Long getStockMin() { return stockMin; }
    public boolean isActive() { return active; }
}
