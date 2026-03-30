package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(int id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    void save(Category category);
    void delete(int id);
}
