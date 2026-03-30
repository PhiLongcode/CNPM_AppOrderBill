package com.giadinh.apporderbill.catalog.model;

import java.util.Objects;

public class Category {
    private int id; // Mã danh mục (PK)
    private String name; // Tên danh mục (NOT NULL)
    private String description; // Mô tả danh mục (NULL)

    // Constructor
    public Category(int id, String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được trống.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    // Setters (cho các thuộc tính có thể thay đổi)
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được trống.");
        }
        this.name = name;
    }

    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
