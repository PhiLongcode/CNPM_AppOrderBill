package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemInput;

public class UpdateMenuItemUseCase {
    private final MenuItemRepository repository;
    public UpdateMenuItemUseCase(MenuItemRepository repository) { this.repository = repository; }

    public MenuItemOutput execute(UpdateMenuItemInput input) {
        MenuItem current = repository.findById(input.getMenuItemId()).orElseThrow();
        int minStock = input.getStockMin() == null ? current.getStockMin().intValue() : input.getStockMin().intValue();
        int maxStock = input.getStockMax() == null ? Math.max(minStock, current.getMaxStockQuantity())
                : input.getStockMax().intValue();
        MenuItem updated = new MenuItem(
                current.getId(),
                input.getName() == null ? current.getName() : input.getName(),
                input.getUnitPrice() == null ? current.getUnitPrice() : input.getUnitPrice(),
                input.getCategory() == null ? current.getCategory() : input.getCategory(),
                input.getImageUrl() == null ? current.getImageUrl() : input.getImageUrl(),
                input.getStockTracked() == null ? current.isStockTracked() : input.getStockTracked(),
                input.getStockQty() == null ? current.getStockQty().intValue() : input.getStockQty().intValue(),
                minStock,
                maxStock,
                input.getBaseUnit() == null ? current.getBaseUnit() : input.getBaseUnit(),
                resolveStatus(current, input));
        repository.save(updated);
        return new MenuItemOutput(updated.getMenuItemId(), updated.getName(), updated.getCategory(), updated.getUnitPrice(),
                updated.getImageUrl(), updated.getBaseUnit(), updated.isStockTracked(), updated.getStockQty(),
                updated.getStockMin(), updated.isActive());
    }

    private MenuItemStatus resolveStatus(MenuItem current, UpdateMenuItemInput input) {
        if (input.getActive() == null) {
            return current.getStatus();
        }
        return input.getActive() ? MenuItemStatus.ACTIVE : MenuItemStatus.INACTIVE;
    }
}
