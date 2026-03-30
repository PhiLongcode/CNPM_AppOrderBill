package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    Optional<MenuItem> findById(int id);
    Optional<MenuItem> findByName(String name);
    List<MenuItem> findAll();
    List<MenuItem> findByCategoryName(String categoryName);
    List<MenuItem> findByStatus(MenuItemStatus status);
    void save(MenuItem menuItem);
    void delete(int id);
}
