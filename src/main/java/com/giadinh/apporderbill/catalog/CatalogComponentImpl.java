package com.giadinh.apporderbill.catalog;

import com.giadinh.apporderbill.catalog.model.Category;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.CategoryRepository;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.ManageCategoryUseCase;
import com.giadinh.apporderbill.catalog.usecase.ManageMenuItemUseCase;
import com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemStockUseCase;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockOutput;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CatalogComponentImpl implements CatalogComponent {

    // Repositories
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    // Use Cases
    private final ManageMenuItemUseCase manageMenuItemUseCase;
    private final ManageCategoryUseCase manageCategoryUseCase;
    private final UpdateMenuItemStockUseCase updateMenuItemStockUseCase;

    public CatalogComponentImpl(MenuItemRepository menuItemRepository,
                                CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;

        // Initialize Use Cases (This would ideally be done via DI as well)
        this.manageMenuItemUseCase = new ManageMenuItemUseCase(menuItemRepository, categoryRepository);
        this.manageCategoryUseCase = new ManageCategoryUseCase(categoryRepository);
        this.updateMenuItemStockUseCase = new UpdateMenuItemStockUseCase(menuItemRepository);
    }

    // -- Menu Item Management Use Cases --
    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public Optional<MenuItem> getMenuItemById(int itemId) {
        return menuItemRepository.findById(itemId);
    }

    @Override
    public List<MenuItem> getMenuItemsByCategory(String categoryName) {
        return menuItemRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<MenuItem> getMenuItemsByStatus(MenuItemStatus status) {
        return menuItemRepository.findByStatus(status);
    }

    @Override
    public ManageMenuItemOutput createMenuItem(ManageMenuItemInput input) {
        return manageMenuItemUseCase.create(input);
    }

    @Override
    public ManageMenuItemOutput updateMenuItem(int itemId, ManageMenuItemInput input) {
        return manageMenuItemUseCase.update(itemId, input);
    }

    @Override
    public void deleteMenuItem(int itemId) {
        manageMenuItemUseCase.delete(itemId);
    }

    @Override
    public UpdateMenuItemStockOutput updateMenuItemStock(int itemId, UpdateMenuItemStockInput input) {
        return updateMenuItemStockUseCase.execute(itemId, input);
    }

    // -- Category Management Use Cases --
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }

    @Override
    public ManageCategoryOutput createCategory(ManageCategoryInput input) {
        return manageCategoryUseCase.create(input);
    }

    @Override
    public ManageCategoryOutput updateCategory(int categoryId, ManageCategoryInput input) {
        return manageCategoryUseCase.update(categoryId, input);
    }

    @Override
    public void deleteCategory(int categoryId) {
        manageCategoryUseCase.delete(categoryId);
    }
}
