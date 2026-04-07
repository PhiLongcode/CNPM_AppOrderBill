package com.giadinh.apporderbill.catalog.usecase.dto;

public class ManageMenuItemOutput {
    private final int menuItemId;

    public ManageMenuItemOutput(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }
}
