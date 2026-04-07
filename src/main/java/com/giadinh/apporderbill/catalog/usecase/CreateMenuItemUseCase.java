package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;

public class CreateMenuItemUseCase {
    private final MenuItemRepository repository;
    public CreateMenuItemUseCase(MenuItemRepository repository) { this.repository = repository; }

    public MenuItemOutput execute(CreateMenuItemInput input) {
        MenuItem item = new MenuItem(
                0, input.getName(), input.getUnitPrice() == null ? 0L : input.getUnitPrice(),
                input.getCategory(), input.getImageUrl(),
                input.getStockTracked() != null && input.getStockTracked(),
                input.getStockQty() == null ? 0 : input.getStockQty().intValue(),
                input.getStockMin() == null ? 0 : input.getStockMin().intValue(),
                0, input.getBaseUnit(), MenuItemStatus.ACTIVE);
        repository.save(item);
        return new MenuItemOutput(item.getMenuItemId(), item.getName(), item.getCategory(), item.getUnitPrice(),
                item.getImageUrl(), item.getBaseUnit(), item.isStockTracked(), item.getStockQty(), item.getStockMin(),
                item.isActive());
    }
}
