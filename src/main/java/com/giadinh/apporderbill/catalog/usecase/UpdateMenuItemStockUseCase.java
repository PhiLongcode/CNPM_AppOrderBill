package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemStockOutput;

import java.util.Optional;

public class UpdateMenuItemStockUseCase {

    private final MenuItemRepository menuItemRepository;

    public UpdateMenuItemStockUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public UpdateMenuItemStockOutput execute(int itemId, UpdateMenuItemStockInput input) {
        Optional<MenuItem> menuItemOptional = menuItemRepository.findById(itemId);
        if (menuItemOptional.isEmpty()) {
            return new UpdateMenuItemStockOutput(false, "Món ăn không tồn tại.", 0);
        }
        MenuItem menuItem = menuItemOptional.get();

        try {
            int newStockQuantity;
            if (input.getOperation() == UpdateMenuItemStockInput.StockOperation.INCREASE) {
                newStockQuantity = menuItem.increaseStock(input.getQuantity());
            } else if (input.getOperation() == UpdateMenuItemStockInput.StockOperation.DECREASE) {
                newStockQuantity = menuItem.decreaseStock(input.getQuantity());
            } else {
                return new UpdateMenuItemStockOutput(false, "Thao tác tồn kho không hợp lệ.", menuItem.getCurrentStockQuantity());
            }
            menuItemRepository.save(menuItem);
            return new UpdateMenuItemStockOutput(true, "Cập nhật tồn kho thành công.", newStockQuantity);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new UpdateMenuItemStockOutput(false, "Lỗi cập nhật tồn kho: " + e.getMessage(), menuItem.getCurrentStockQuantity());
        } catch (Exception e) {
            return new UpdateMenuItemStockOutput(false, "Đã xảy ra lỗi không xác định khi cập nhật tồn kho: " + e.getMessage(), menuItem.getCurrentStockQuantity());
        }
    }
}
