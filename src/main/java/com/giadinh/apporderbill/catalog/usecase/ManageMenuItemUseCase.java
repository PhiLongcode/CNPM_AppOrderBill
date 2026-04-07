package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.repository.CategoryRepository;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageMenuItemOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

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
            throw new DomainException(ErrorCode.MENU_ITEM_NAME_DUPLICATE);
        }
        if (categoryRepository.findByName(input.getCategoryName()).isEmpty()) {
            throw new DomainException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        MenuItem newMenuItem = new MenuItem(
                0,
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
        return new ManageMenuItemOutput(newMenuItem.getId());
    }

    public ManageMenuItemOutput update(int itemId, ManageMenuItemInput input) {
        Optional<MenuItem> existingMenuItemOptional = menuItemRepository.findById(itemId);
        if (existingMenuItemOptional.isEmpty()) {
            throw new DomainException(ErrorCode.MENU_ITEM_NOT_FOUND);
        }
        MenuItem existingMenuItem = existingMenuItemOptional.get();

        Optional<MenuItem> itemWithSameName = menuItemRepository.findByName(input.getName());
        if (itemWithSameName.isPresent() && itemWithSameName.get().getId() != itemId) {
            throw new DomainException(ErrorCode.MENU_ITEM_NAME_DUPLICATE);
        }

        if (categoryRepository.findByName(input.getCategoryName()).isEmpty()) {
            throw new DomainException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        existingMenuItem.setName(input.getName());
        existingMenuItem.setPrice(input.getPrice());
        existingMenuItem.setCategoryName(input.getCategoryName());
        existingMenuItem.setImageUrl(input.getImageUrl());
        existingMenuItem.setStockManaged(input.isStockManaged());
        existingMenuItem.setMinStockQuantity(input.getMinStockQuantity());
        existingMenuItem.setMaxStockQuantity(input.getMaxStockQuantity());
        existingMenuItem.setUnitOfMeasureName(input.getUnitOfMeasureName());
        existingMenuItem.setStatus(input.getStatus());

        menuItemRepository.save(existingMenuItem);
        return new ManageMenuItemOutput(itemId);
    }

    public void delete(int itemId) {
        menuItemRepository.delete(itemId);
    }
}
