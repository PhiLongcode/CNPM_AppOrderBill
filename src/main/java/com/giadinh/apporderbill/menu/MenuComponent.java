package com.giadinh.apporderbill.menu;

import com.giadinh.apporderbill.menu.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.menu.usecase.dto.DeleteMenuItemInput;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelInput;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelOutput;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.menu.usecase.dto.UpdateMenuItemInput;

import java.util.List;

public interface MenuComponent {
    List<MenuItemOutput> getAllMenuItems();

    List<MenuItemOutput> getActiveMenuItems();

    MenuItemOutput createMenuItem(CreateMenuItemInput input);

    MenuItemOutput updateMenuItem(UpdateMenuItemInput input);

    void deleteMenuItem(DeleteMenuItemInput input);

    ImportMenuFromExcelOutput importMenuFromExcel(ImportMenuFromExcelInput input);
}

