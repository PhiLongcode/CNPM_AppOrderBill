package com.giadinh.apporderbill.menu;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.service.ExcelService;
import com.giadinh.apporderbill.menu.usecase.CreateMenuItemUseCase;
import com.giadinh.apporderbill.menu.usecase.DeleteMenuItemUseCase;
import com.giadinh.apporderbill.menu.usecase.GetActiveMenuItemsUseCase;
import com.giadinh.apporderbill.menu.usecase.GetAllMenuItemsUseCase;
import com.giadinh.apporderbill.menu.usecase.ImportMenuFromExcelUseCase;
import com.giadinh.apporderbill.menu.usecase.UpdateMenuItemUseCase;
import com.giadinh.apporderbill.menu.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.menu.usecase.dto.DeleteMenuItemInput;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelInput;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelOutput;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.menu.usecase.dto.UpdateMenuItemInput;

import java.util.List;

public class MenuComponentImpl implements MenuComponent {
    private final CreateMenuItemUseCase createUseCase;
    private final UpdateMenuItemUseCase updateUseCase;
    private final DeleteMenuItemUseCase deleteUseCase;
    private final GetAllMenuItemsUseCase getAllUseCase;
    private final GetActiveMenuItemsUseCase getActiveUseCase;
    private final ImportMenuFromExcelUseCase importUseCase;

    public MenuComponentImpl(MenuItemRepository menuItemRepository, ExcelService excelService) {
        this.createUseCase = new CreateMenuItemUseCase(menuItemRepository);
        this.updateUseCase = new UpdateMenuItemUseCase(menuItemRepository);
        this.deleteUseCase = new DeleteMenuItemUseCase(menuItemRepository);
        this.getAllUseCase = new GetAllMenuItemsUseCase(menuItemRepository);
        this.getActiveUseCase = new GetActiveMenuItemsUseCase(menuItemRepository);
        this.importUseCase = new ImportMenuFromExcelUseCase(menuItemRepository, excelService);
    }

    @Override
    public List<MenuItemOutput> getAllMenuItems() {
        return getAllUseCase.execute();
    }

    @Override
    public List<MenuItemOutput> getActiveMenuItems() {
        return getActiveUseCase.execute();
    }

    @Override
    public MenuItemOutput createMenuItem(CreateMenuItemInput input) {
        return createUseCase.execute(input);
    }

    @Override
    public MenuItemOutput updateMenuItem(UpdateMenuItemInput input) {
        return updateUseCase.execute(input);
    }

    @Override
    public void deleteMenuItem(DeleteMenuItemInput input) {
        deleteUseCase.execute(input);
    }

    @Override
    public ImportMenuFromExcelOutput importMenuFromExcel(ImportMenuFromExcelInput input) {
        return importUseCase.execute(input);
    }
}

