package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.Category;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Temporary in-memory repository while SQLite implementation is pending.
 */
public class SqliteCategoryRepository implements CategoryRepository {
    private final ConcurrentHashMap<Integer, Category> data = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public SqliteCategoryRepository(Object connectionProvider) {
        save(new Category(0, "Món chính", "Danh mục mặc định"));
        save(new Category(0, "Đồ uống", "Danh mục mặc định"));
    }

    @Override
    public Optional<Category> findById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<Category> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return data.values().stream()
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .findFirst();
    }

    @Override
    public java.util.List<Category> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(Category category) {
        if (category.getId() <= 0) {
            int newId = idGenerator.getAndIncrement();
            category = new Category(newId, category.getName(), category.getDescription());
        }
        data.put(category.getId(), category);
    }

    @Override
    public void delete(int id) {
        data.remove(id);
    }
}

