package com.giadinh.apporderbill.javafx.menu;

import com.giadinh.apporderbill.menu.usecase.CreateMenuItemUseCase;
import com.giadinh.apporderbill.menu.usecase.DeleteMenuItemUseCase;
import com.giadinh.apporderbill.menu.usecase.ExportMenuToExcelUseCase;
import com.giadinh.apporderbill.menu.usecase.GetAllMenuItemsUseCase;
import com.giadinh.apporderbill.menu.usecase.ImportMenuFromExcelUseCase;
import com.giadinh.apporderbill.menu.usecase.UpdateMenuItemUseCase;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;

import java.util.Collections;
import java.util.List;

public class MenuManagementPresenter {
    private final GetAllMenuItemsUseCase getAllUseCase;

    public MenuManagementPresenter(
            MenuManagementController controller,
            CreateMenuItemUseCase create,
            UpdateMenuItemUseCase update,
            DeleteMenuItemUseCase delete,
            GetAllMenuItemsUseCase getAll,
            ImportMenuFromExcelUseCase importExcel,
            ExportMenuToExcelUseCase exportExcel) {
        this.getAllUseCase = getAll;
    }

    public List<MenuItemOutput> loadAllMenuItems() {
        try {
            return getAllUseCase.execute();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

