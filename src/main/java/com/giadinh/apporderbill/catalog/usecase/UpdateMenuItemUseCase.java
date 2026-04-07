package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemInput;

public class UpdateMenuItemUseCase {
    private final MenuItemRepository repository;
    public UpdateMenuItemUseCase(MenuItemRepository repository) { this.repository = repository; }

    public MenuItemOutput execute(UpdateMenuItemInput input) {
        MenuItem current = repository.findById(input.getMenuItemId()).orElseThrow();
        MenuItem updated = new MenuItem(
                current.getId(),
                input.getName() == null ? current.getName() : input.getName(),
                input.getUnitPrice() == null ? current.getUnitPrice() : input.getUnitPrice(),
                input.getCategory() == null ? current.getCategory() : input.getCategory(),
                input.getImageUrl() == null ? current.getImageUrl() : input.getImageUrl(),
                input.getStockTracked() == null ? current.isStockTracked() : input.getStockTracked(),
                input.getStockQty() == null ? current.getStockQty().intValue() : input.getStockQty().intValue(),
                input.getStockMin() == null ? current.getStockMin().intValue() : input.getStockMin().intValue(),
                current.getMaxStockQuantity(),
                input.getBaseUnit() == null ? current.getBaseUnit() : input.getBaseUnit(),
                current.getStatus());
        repository.save(updated);
        return new MenuItemOutput(updated.getMenuItemId(), updated.getName(), updated.getCategory(), updated.getUnitPrice(),
                updated.getImageUrl(), updated.getBaseUnit(), updated.isStockTracked(), updated.getStockQty(),
                updated.getStockMin(), updated.isActive());
    }
}
