package com.giadinh.apporderbill.menu.usecase.dto;

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
    private List<MenuItemUnitDto> units;

    public String getName() { return name; }
    public String getCategory() { return category; }
    public Long getUnitPrice() { return unitPrice; }
    public String getImageUrl() { return imageUrl; }
    public String getBaseUnit() { return baseUnit; }
    public Boolean getStockTracked() { return stockTracked; }
    public Long getStockQty() { return stockQty; }
    public Long getStockMin() { return stockMin; }
    public List<MenuItemUnitDto> getUnits() { return units; }
}

