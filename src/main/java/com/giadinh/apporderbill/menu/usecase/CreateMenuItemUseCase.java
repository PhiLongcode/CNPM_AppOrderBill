package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.model.MenuItem;
import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;

public class CreateMenuItemUseCase {
    private final MenuItemRepository repository;

    public CreateMenuItemUseCase(MenuItemRepository repository) {
        this.repository = repository;
    }

    public MenuItemOutput execute(CreateMenuItemInput input) {
        MenuItem item = new MenuItem(
                null,
                input.getName(),
                input.getCategory(),
                input.getUnitPrice() == null ? 0L : input.getUnitPrice(),
                input.getImageUrl(),
                input.getBaseUnit(),
                input.getStockTracked() != null && input.getStockTracked(),
                input.getStockQty() == null ? 0L : input.getStockQty(),
                input.getStockMin() == null ? 0L : input.getStockMin(),
                true);
        repository.save(item);
        return new MenuItemOutput(
                item.getMenuItemId(),
                item.getName(),
                item.getCategory(),
                item.getUnitPrice(),
                item.getImageUrl(),
                item.getBaseUnit(),
                item.isStockTracked(),
                item.getStockQty(),
                item.getStockMin(),
                item.isActive());
    }
}

