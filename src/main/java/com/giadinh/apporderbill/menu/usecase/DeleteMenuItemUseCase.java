package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.usecase.dto.DeleteMenuItemInput;

public class DeleteMenuItemUseCase {
    private final MenuItemRepository repository;

    public DeleteMenuItemUseCase(MenuItemRepository repository) {
        this.repository = repository;
    }

    public void execute(DeleteMenuItemInput input) {
        if (input != null && input.getMenuItemId() != null) {
            repository.delete(input.getMenuItemId());
        }
    }
}

