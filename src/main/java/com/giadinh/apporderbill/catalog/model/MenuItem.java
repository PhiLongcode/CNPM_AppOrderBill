package com.giadinh.apporderbill.catalog.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.Objects;

public class MenuItem {
    private int id; // Mã món ăn (PK)
    private String name; // Tên món ăn (NOT NULL)
    private double price; // Giá bán thiết lập (NOT NULL)
    private String categoryName; // Loại món (NOT NULL)
    private String imageUrl; // Ảnh món ăn (NULL)
    private boolean isStockManaged; // Bật/Tắt quản lý tồn kho (DEFAULT 0)
    private int currentStockQuantity; // Số lượng hàng tồn hiện tại (DEFAULT 0)
    private int minStockQuantity; // Số lượng hàng tồn nhỏ nhất (DEFAULT 0)
    private int maxStockQuantity; // Số lượng hàng tồn lớn nhất (DEFAULT 0)
    private String unitOfMeasureName; // Đơn vị tính (NULL)
    private MenuItemStatus status; // Đang bán/ngừng bán (DEFAULT 1 -> ACTIVE)

    // Constructor cho MenuItem mới
    public MenuItem(int id, String name, double price, String categoryName, String imageUrl, 
                      boolean isStockManaged, int currentStockQuantity, int minStockQuantity, 
                      int maxStockQuantity, String unitOfMeasureName, MenuItemStatus status) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException(ErrorCode.MENU_ITEM_NAME_REQUIRED);
        }
        if (price <= 0) {
            throw new DomainException(ErrorCode.MENU_ITEM_PRICE_INVALID);
        }
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new DomainException(ErrorCode.MENU_ITEM_CATEGORY_REQUIRED);
        }
        if (isStockManaged && (minStockQuantity < 0 || maxStockQuantity < minStockQuantity)) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_LEVELS_INVALID);
        }
        if (isStockManaged && currentStockQuantity < 0) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_CURRENT_INVALID);
        }

        this.id = id;
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

        // Đảm bảo trạng thái OUT_OF_STOCK nếu hết hàng và quản lý tồn kho
        if (isStockManaged && currentStockQuantity == 0 && status == MenuItemStatus.ACTIVE) {
            this.status = MenuItemStatus.OUT_OF_STOCK;
        }
    }

    // Getters
    public int getId() { return id; }
    public Long getMenuItemId() { return (long) id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Long getUnitPrice() { return Math.round(price); }
    public String getCategoryName() { return categoryName; }
    public String getCategory() { return categoryName; }
    public String getImageUrl() { return imageUrl; }
    public boolean isStockManaged() { return isStockManaged; }
    public boolean isStockTracked() { return isStockManaged; }
    public int getCurrentStockQuantity() { return currentStockQuantity; }
    public Long getStockQty() { return (long) currentStockQuantity; }
    public int getMinStockQuantity() { return minStockQuantity; }
    public Long getStockMin() { return (long) minStockQuantity; }
    public int getMaxStockQuantity() { return maxStockQuantity; }
    public String getUnitOfMeasureName() { return unitOfMeasureName; }
    public String getBaseUnit() { return unitOfMeasureName; }
    public MenuItemStatus getStatus() { return status; }
    public boolean isActive() { return status == MenuItemStatus.ACTIVE; }

    // Setters (chỉ cho các thuộc tính có thể thay đổi nghiệp vụ)
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException(ErrorCode.MENU_ITEM_NAME_REQUIRED);
        }
        this.name = name;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new DomainException(ErrorCode.MENU_ITEM_PRICE_INVALID);
        }
        this.price = price;
    }

    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new DomainException(ErrorCode.MENU_ITEM_CATEGORY_REQUIRED);
        }
        this.categoryName = categoryName;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStockManaged(boolean stockManaged) { isStockManaged = stockManaged; }
    public void updateStock(long stockQuantity) { this.currentStockQuantity = (int) stockQuantity; }
    public void setMinStockQuantity(int minStockQuantity) { this.minStockQuantity = minStockQuantity; }
    public void setMaxStockQuantity(int maxStockQuantity) { this.maxStockQuantity = maxStockQuantity; }
    public void setUnitOfMeasureName(String unitOfMeasureName) { this.unitOfMeasureName = unitOfMeasureName; }

    public void setStatus(MenuItemStatus status) {
        // Logic nghiệp vụ: nếu món hết hàng, không thể đặt ACTIVE
        if (this.isStockManaged && this.currentStockQuantity == 0 && status == MenuItemStatus.ACTIVE) {
            throw new DomainException(ErrorCode.MENU_ITEM_CANNOT_ACTIVATE_OUT_OF_STOCK);
        }
        this.status = status;
    }

    // --- Các hành vi nghiệp vụ liên quan đến tồn kho (Uncle Bob's Principle) ---

    /**
     * Giảm số lượng tồn kho của món ăn.
     * @param quantity Giảm số lượng.
     * @return Số lượng tồn kho còn lại.
     */
    public int decreaseStock(int quantity) {
        if (!isStockManaged) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_NOT_TRACKED);
        }
        if (quantity <= 0) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_DECREASE_INVALID);
        }
        if (this.currentStockQuantity < quantity) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT);
        }
        this.currentStockQuantity -= quantity;
        if (this.currentStockQuantity == 0) {
            this.status = MenuItemStatus.OUT_OF_STOCK;
        } else if (this.currentStockQuantity > 0 && this.status == MenuItemStatus.OUT_OF_STOCK) {
            this.status = MenuItemStatus.ACTIVE; // Tự động active lại nếu có hàng
        }
        return this.currentStockQuantity;
    }

    /**
     * Tăng số lượng tồn kho của món ăn.
     * @param quantity Tăng số lượng.
     * @return Số lượng tồn kho còn lại.
     */
    public int increaseStock(int quantity) {
        if (!isStockManaged) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_NOT_TRACKED);
        }
        if (quantity <= 0) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_INCREASE_INVALID);
        }
        this.currentStockQuantity += quantity;
        if (this.currentStockQuantity > 0 && this.status == MenuItemStatus.OUT_OF_STOCK) {
            this.status = MenuItemStatus.ACTIVE; // Tự động active lại nếu có hàng
        }
        return this.currentStockQuantity;
    }

    /**
     * Kiểm tra xem có đủ số lượng tồn kho để bán không.
     * @param quantity Số lượng cần kiểm tra.
     * @return true nếu đủ, false nếu không.
     */
    public boolean checkStockAvailability(int quantity) {
        if (!isStockManaged) {
            return true; // Nếu không quản lý tồn kho, luôn coi là có sẵn
        }
        return this.currentStockQuantity >= quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return id == menuItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", price=" + price +
               ", categoryName='" + categoryName + '\'' +
               ", imageUrl='" + imageUrl + '\'' +
               ", isStockManaged=" + isStockManaged +
               ", currentStockQuantity=" + currentStockQuantity +
               ", minStockQuantity=" + minStockQuantity +
               ", maxStockQuantity=" + maxStockQuantity +
               ", unitOfMeasureName='" + unitOfMeasureName + '\'' +
               ", status=" + status +
               '}';
    }
}
