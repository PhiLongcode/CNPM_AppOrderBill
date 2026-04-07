package com.giadinh.apporderbill.catalog.usecase.dto;

import java.util.List;

public class CreateMenuItemInput {
    private String name;
    private String category;
    private Long unitPrice;
    private String imageUrl;
    private String baseUnit;
    private Boolean stockTracked;
    private Long stockQty;
    private Long stockMin;
    private Long stockMax;
    private List<MenuItemUnitDto> units;

    public String getName() { return name; }
    public String getCategory() { return category; }
    public Long getUnitPrice() { return unitPrice; }
    public String getImageUrl() { return imageUrl; }
    public String getBaseUnit() { return baseUnit; }
    public Boolean getStockTracked() { return stockTracked; }
    public Long getStockQty() { return stockQty; }
    public Long getStockMin() { return stockMin; }
    public Long getStockMax() { return stockMax; }
    public List<MenuItemUnitDto> getUnits() { return units; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setUnitPrice(Long unitPrice) { this.unitPrice = unitPrice; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setBaseUnit(String baseUnit) { this.baseUnit = baseUnit; }
    public void setStockTracked(Boolean stockTracked) { this.stockTracked = stockTracked; }
    public void setStockQty(Long stockQty) { this.stockQty = stockQty; }
    public void setStockMin(Long stockMin) { this.stockMin = stockMin; }
    public void setStockMax(Long stockMax) { this.stockMax = stockMax; }
    public void setUnits(List<MenuItemUnitDto> units) { this.units = units; }
}
