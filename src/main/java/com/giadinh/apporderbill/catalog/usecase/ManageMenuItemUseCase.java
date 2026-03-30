package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.Category;
import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.repository.CategoryRepository;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemOutput;

import java.util.Optional;

public class ManageMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public ManageMenuItemUseCase(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    public ManageMenuItemOutput create(ManageMenuItemInput input) {
        if (menuItemRepository.findByName(input.getName()).isPresent()) {
            return new ManageMenuItemOutput(false, "Tên món ăn đã tồn tại.", 0);
        }
        if (categoryRepository.findByName(input.getCategoryName()).isEmpty()) {
            return new ManageMenuItemOutput(false, "Loại món ăn không tồn tại.", 0);
        }

        MenuItem newMenuItem = new MenuItem(
                0, // ID sẽ được DB tự động tạo
                input.getName(),
                input.getPrice(),
                input.getCategoryName(),
                input.getImageUrl(),
                input.isStockManaged(),
                input.getCurrentStockQuantity(),
                input.getMinStockQuantity(),
                input.getMaxStockQuantity(),
                input.getUnitOfMeasureName(),
                input.getStatus()
        );
        menuItemRepository.save(newMenuItem);
        return new ManageMenuItemOutput(true, "Tạo món ăn thành công.", newMenuItem.getId());
    }

    public ManageMenuItemOutput update(int itemId, ManageMenuItemInput input) {
        Optional<MenuItem> existingMenuItemOptional = menuItemRepository.findById(itemId);
        if (existingMenuItemOptional.isEmpty()) {
            return new ManageMenuItemOutput(false, "Món ăn không tồn tại.", itemId);
        }
        MenuItem existingMenuItem = existingMenuItemOptional.get();

        // Kiểm tra trùng tên món ăn (trừ chính nó)
        Optional<MenuItem> itemWithSameName = menuItemRepository.findByName(input.getName());
        if (itemWithSameName.isPresent() && itemWithSameName.get().getId() != itemId) {
            return new ManageMenuItemOutput(false, "Tên món ăn đã tồn tại bởi món khác.", itemId);
        }

        if (categoryRepository.findByName(input.getCategoryName()).isEmpty()) {
            return new ManageMenuItemOutput(false, "Loại món ăn không tồn tại.", itemId);
        }

        // Cập nhật các thuộc tính
        existingMenuItem.setName(input.getName());
        existingMenuItem.setPrice(input.getPrice());
        existingMenuItem.setCategoryName(input.getCategoryName());
        existingMenuItem.setImageUrl(input.getImageUrl());
        existingMenuItem.setStockManaged(input.isStockManaged());
        // currentStockQuantity không nên được cập nhật trực tiếp qua đây, dùng updateMenuItemStockUseCase
        existingMenuItem.setMinStockQuantity(input.getMinStockQuantity());
        existingMenuItem.setMaxStockQuantity(input.getMaxStockQuantity());
        existingMenuItem.setUnitOfMeasureName(input.getUnitOfMeasureName());
        existingMenuItem.setStatus(input.getStatus());

        menuItemRepository.save(existingMenuItem);
        return new ManageMenuItemOutput(true, "Cập nhật món ăn thành công.", itemId);
    }

    public void delete(int itemId) {
        menuItemRepository.delete(itemId);
    }
}
