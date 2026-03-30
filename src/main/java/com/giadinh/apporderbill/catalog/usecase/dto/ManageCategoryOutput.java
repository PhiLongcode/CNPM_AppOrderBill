package com.giadinh.apporderbill.catalog.usecase.dto;

public class ManageCategoryOutput {
    private boolean success;
    private String message;
    private int categoryId;

    public ManageCategoryOutput(boolean success, String message, int categoryId) {
        this.success = success;
        this.message = message;
        this.categoryId = categoryId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
