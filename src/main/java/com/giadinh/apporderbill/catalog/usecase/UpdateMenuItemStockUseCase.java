package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.Optional;

public class UpdateMenuItemStockUseCase {

    private final MenuItemRepository menuItemRepository;

    public UpdateMenuItemStockUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public UpdateMenuItemStockOutput execute(int itemId, UpdateMenuItemStockInput input) {
        Optional<MenuItem> menuItemOptional = menuItemRepository.findById(itemId);
        if (menuItemOptional.isEmpty()) {
            throw new DomainException(ErrorCode.MENU_ITEM_NOT_FOUND);
        }
        MenuItem menuItem = menuItemOptional.get();

        int newStockQuantity;
        if (input.getOperation() == UpdateMenuItemStockInput.StockOperation.INCREASE) {
            newStockQuantity = menuItem.increaseStock(input.getQuantity());
        } else if (input.getOperation() == UpdateMenuItemStockInput.StockOperation.DECREASE) {
            newStockQuantity = menuItem.decreaseStock(input.getQuantity());
        } else {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_OPERATION_INVALID);
        }
        menuItemRepository.save(menuItem);
        return new UpdateMenuItemStockOutput(newStockQuantity);
    }
}
