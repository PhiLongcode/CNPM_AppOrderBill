package com.giadinh.apporderbill.catalog.usecase.dto;

public class ManageMenuItemOutput {
    private boolean success;
    private String message;
    private int menuItemId;

    public ManageMenuItemOutput(boolean success, String message, int menuItemId) {
        this.success = success;
        this.message = message;
        this.menuItemId = menuItemId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getMenuItemId() {
        return menuItemId;
    }
}
