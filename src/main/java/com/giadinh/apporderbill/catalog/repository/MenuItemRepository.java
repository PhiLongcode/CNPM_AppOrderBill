package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    Optional<MenuItem> findById(int id);
    default Optional<MenuItem> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return findById(id.intValue());
    }
    Optional<MenuItem> findByName(String name);
    List<MenuItem> findAll();
    default List<MenuItem> findActive() {
        return findByStatus(MenuItemStatus.ACTIVE);
    }
    List<MenuItem> findByCategoryName(String categoryName);
    List<MenuItem> findByStatus(MenuItemStatus status);
    void save(MenuItem menuItem);
    void delete(int id);
    default void delete(Long id) {
        if (id != null) {
            delete(id.intValue());
        }
    }
}
