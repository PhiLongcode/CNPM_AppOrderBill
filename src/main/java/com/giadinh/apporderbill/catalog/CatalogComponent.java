package com.giadinh.apporderbill.catalog;

import com.giadinh.apporderbill.catalog.model.Category;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockOutput;

import java.util.List;
import java.util.Optional;

/**
 * CatalogComponent là giao diện chính để tương tác với Catalog Bounded Context.
 * Nó cung cấp các phương thức để quản lý món ăn và danh mục.
 */
public interface CatalogComponent {

    // -- Menu Item Management Use Cases --
    List<MenuItem> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(int itemId);
    List<MenuItem> getMenuItemsByCategory(String categoryName);
    List<MenuItem> getMenuItemsByStatus(MenuItemStatus status);
    ManageMenuItemOutput createMenuItem(ManageMenuItemInput input);
    ManageMenuItemOutput updateMenuItem(int itemId, ManageMenuItemInput input);
    void deleteMenuItem(int itemId);
    UpdateMenuItemStockOutput updateMenuItemStock(int itemId, UpdateMenuItemStockInput input);

    // -- Category Management Use Cases --
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(int categoryId);
    Optional<Category> getCategoryByName(String categoryName);
    ManageCategoryOutput createCategory(ManageCategoryInput input);
    ManageCategoryOutput updateCategory(int categoryId, ManageCategoryInput input);
    void deleteCategory(int categoryId);
}
