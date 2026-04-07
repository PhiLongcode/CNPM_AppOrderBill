package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import java.util.List;

public class GetAllMenuItemsUseCase {
    private final MenuItemRepository repository;
    public GetAllMenuItemsUseCase(MenuItemRepository repository) { this.repository = repository; }
    public List<MenuItemOutput> execute() {
        return repository.findAll().stream().map(item -> new MenuItemOutput(
                item.getMenuItemId(), item.getName(), item.getCategory(), item.getUnitPrice(), item.getImageUrl(),
                item.getBaseUnit(), item.isStockTracked(), item.getStockQty(), item.getStockMin(), item.isActive())).toList();
    }
}
