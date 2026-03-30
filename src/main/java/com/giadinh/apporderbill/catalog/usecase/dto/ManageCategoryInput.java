package com.giadinh.apporderbill.catalog.usecase.dto;

public class ManageCategoryInput {
    private String name;
    private String description;

    public ManageCategoryInput(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
