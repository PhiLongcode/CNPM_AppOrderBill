package com.giadinh.apporderbill.javafx.menu;

import com.giadinh.apporderbill.catalog.usecase.CreateMenuItemUseCase;
import com.giadinh.apporderbill.catalog.usecase.DeleteMenuItemUseCase;
import com.giadinh.apporderbill.catalog.usecase.ExportMenuToExcelUseCase;
import com.giadinh.apporderbill.catalog.usecase.GetAllMenuItemsUseCase;
import com.giadinh.apporderbill.catalog.usecase.ImportMenuFromExcelUseCase;
import com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.DeleteMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportConflictStrategy;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportMenuFromExcelInput;

import java.util.Collections;
import java.util.List;

public class MenuManagementPresenter {
    private final CreateMenuItemUseCase createUseCase;
    private final UpdateMenuItemUseCase updateUseCase;
    private final DeleteMenuItemUseCase deleteUseCase;
    private final GetAllMenuItemsUseCase getAllUseCase;
    private final ImportMenuFromExcelUseCase importUseCase;
    private final ExportMenuToExcelUseCase exportUseCase;

    public MenuManagementPresenter(
            MenuManagementController controller,
            CreateMenuItemUseCase create,
            UpdateMenuItemUseCase update,
            DeleteMenuItemUseCase delete,
            GetAllMenuItemsUseCase getAll,
            ImportMenuFromExcelUseCase importExcel,
            ExportMenuToExcelUseCase exportExcel) {
        this.createUseCase = create;
        this.updateUseCase = update;
        this.deleteUseCase = delete;
        this.getAllUseCase = getAll;
        this.importUseCase = importExcel;
        this.exportUseCase = exportExcel;
    }

    public List<MenuItemOutput> loadAllMenuItems() {
        try {
            return getAllUseCase.execute();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public MenuItemOutput createMenuItem(MenuManagementController.MenuItemRow ignored, MenuItemDialogController.Result form) {
        CreateMenuItemInput input = new CreateMenuItemInput();
        input.setName(form.getName());
        input.setCategory(form.getCategory());
        input.setUnitPrice(form.getUnitPrice());
        input.setImageUrl(form.getImageUrl());
        input.setBaseUnit(form.getBaseUnit());
        input.setStockTracked(Boolean.TRUE.equals(form.getStockTracked()));
        input.setStockQty(form.getStockQty());
        input.setStockMin(form.getStockMin());
        input.setStockMax(form.getStockMax());
        input.setUnits(form.getUnits());
        return createUseCase.execute(input);
    }

    public MenuItemOutput updateMenuItem(Long menuItemId, MenuItemDialogController.Result form) {
        UpdateMenuItemInput input = new UpdateMenuItemInput();
        input.setMenuItemId(menuItemId);
        input.setName(form.getName());
        input.setCategory(form.getCategory());
        input.setUnitPrice(form.getUnitPrice());
        input.setImageUrl(form.getImageUrl());
        input.setBaseUnit(form.getBaseUnit());
        input.setStockTracked(Boolean.TRUE.equals(form.getStockTracked()));
        input.setStockQty(form.getStockQty());
        input.setStockMin(form.getStockMin());
        input.setStockMax(form.getStockMax());
        input.setUnits(form.getUnits());
        return updateUseCase.execute(input);
    }

    public void deleteMenuItem(Long menuItemId) {
        DeleteMenuItemInput input = new DeleteMenuItemInput();
        input.setMenuItemId(menuItemId);
        deleteUseCase.execute(input);
    }

    public MenuItemOutput toggleActive(Long menuItemId, boolean currentActive) {
        UpdateMenuItemInput input = new UpdateMenuItemInput();
        input.setMenuItemId(menuItemId);
        input.setActive(!currentActive);
        return updateUseCase.execute(input);
    }

    public MenuItemOutput addStock(Long menuItemId, long addedStock, long currentStock) {
        UpdateMenuItemInput input = new UpdateMenuItemInput();
        input.setMenuItemId(menuItemId);
        input.setStockQty(Math.max(0, currentStock + addedStock));
        return updateUseCase.execute(input);
    }

    public int importFromExcel(String filePath, ImportConflictStrategy strategy) {
        ImportMenuFromExcelInput input = new ImportMenuFromExcelInput();
        input.setFilePath(filePath);
        input.setConflictStrategy(strategy);
        return importUseCase.execute(input).getImportedCount();
    }

    public void exportToExcel(String filePath) {
        exportUseCase.execute(filePath);
    }
}

