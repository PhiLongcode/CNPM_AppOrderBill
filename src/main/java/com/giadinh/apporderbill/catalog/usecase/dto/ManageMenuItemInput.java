package com.giadinh.apporderbill.catalog.usecase.dto;

import com.giadinh.apporderbill.catalog.model.MenuItemStatus;

public class ManageMenuItemInput {
    private String name;
    private double price;
    private String categoryName;
    private String imageUrl;
    private boolean isStockManaged;
    private int currentStockQuantity;
    private int minStockQuantity;
    private int maxStockQuantity;
    private String unitOfMeasureName;
    private MenuItemStatus status;

    public ManageMenuItemInput(String name, double price, String categoryName, String imageUrl,
                               boolean isStockManaged, int currentStockQuantity, int minStockQuantity,
                               int maxStockQuantity, String unitOfMeasureName, MenuItemStatus status) {
        this.name = name;
        this.price = price;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.isStockManaged = isStockManaged;
        this.currentStockQuantity = currentStockQuantity;
        this.minStockQuantity = minStockQuantity;
        this.maxStockQuantity = maxStockQuantity;
        this.unitOfMeasureName = unitOfMeasureName;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isStockManaged() {
        return isStockManaged;
    }

    public int getCurrentStockQuantity() {
        return currentStockQuantity;
    }

    public int getMinStockQuantity() {
        return minStockQuantity;
    }

    public int getMaxStockQuantity() {
        return maxStockQuantity;
    }

    public String getUnitOfMeasureName() {
        return unitOfMeasureName;
    }

    public MenuItemStatus getStatus() {
        return status;
    }
}
