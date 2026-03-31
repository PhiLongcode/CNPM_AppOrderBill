package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;

import java.util.List;

public class GetActiveMenuItemsUseCase {
    private final MenuItemRepository repository;

    public GetActiveMenuItemsUseCase(MenuItemRepository repository) {
        this.repository = repository;
    }

    public List<MenuItemOutput> execute() {
        return repository.findActive().stream()
                .map(item -> new MenuItemOutput(
                        item.getMenuItemId(),
                        item.getName(),
                        item.getCategory(),
                        item.getUnitPrice(),
                        item.getImageUrl(),
                        item.getBaseUnit(),
                        item.isStockTracked(),
                        item.getStockQty(),
                        item.getStockMin(),
                        item.isActive()))
                .toList();
    }
}

