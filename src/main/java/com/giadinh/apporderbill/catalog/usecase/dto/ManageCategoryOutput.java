package com.giadinh.apporderbill.catalog.usecase.dto;

public class ManageCategoryOutput {
    private final int categoryId;

    public ManageCategoryOutput(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
