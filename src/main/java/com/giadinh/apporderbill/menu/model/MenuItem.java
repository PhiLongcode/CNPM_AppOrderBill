package com.giadinh.apporderbill.menu.model;

public class MenuItem {
    private Long menuItemId;
    private String name;
    private String category;
    private Long unitPrice;
    private String imageUrl;
    private String baseUnit;
    private boolean stockTracked;
    private Long stockQty;
    private Long stockMin;
    private boolean active;

    public MenuItem(Long menuItemId, String name, String category, Long unitPrice,
            String imageUrl, String baseUnit, boolean stockTracked, Long stockQty, Long stockMin, boolean active) {
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

    public Long getMenuItemId() {
        return menuItemId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public boolean isStockTracked() {
        return stockTracked;
    }

    public Long getStockQty() {
        return stockQty;
    }

    public Long getStockMin() {
        return stockMin;
    }

    public boolean isActive() {
        return active;
    }

    public void updateStock(Long newQty) {
        this.stockQty = newQty == null ? 0L : newQty;
    }
}

