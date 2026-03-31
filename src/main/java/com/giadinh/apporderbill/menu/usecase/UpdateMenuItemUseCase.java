package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.model.MenuItem;
import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.menu.usecase.dto.UpdateMenuItemInput;

public class UpdateMenuItemUseCase {
    private final MenuItemRepository repository;

    public UpdateMenuItemUseCase(MenuItemRepository repository) {
        this.repository = repository;
    }

    public MenuItemOutput execute(UpdateMenuItemInput input) {
        var current = repository.findById(input.getMenuItemId()).orElseThrow();
        MenuItem updated = new MenuItem(
                current.getMenuItemId(),
                input.getName() == null ? current.getName() : input.getName(),
                input.getCategory() == null ? current.getCategory() : input.getCategory(),
                input.getUnitPrice() == null ? current.getUnitPrice() : input.getUnitPrice(),
                input.getImageUrl() == null ? current.getImageUrl() : input.getImageUrl(),
                input.getBaseUnit() == null ? current.getBaseUnit() : input.getBaseUnit(),
                input.getStockTracked() == null ? current.isStockTracked() : input.getStockTracked(),
                input.getStockQty() == null ? current.getStockQty() : input.getStockQty(),
                input.getStockMin() == null ? current.getStockMin() : input.getStockMin(),
                current.isActive());
        repository.save(updated);
        return new MenuItemOutput(
                updated.getMenuItemId(),
                updated.getName(),
                updated.getCategory(),
                updated.getUnitPrice(),
                updated.getImageUrl(),
                updated.getBaseUnit(),
                updated.isStockTracked(),
                updated.getStockQty(),
                updated.getStockMin(),
                updated.isActive());
    }
}

