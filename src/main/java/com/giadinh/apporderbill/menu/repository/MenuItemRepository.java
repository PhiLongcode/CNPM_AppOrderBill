package com.giadinh.apporderbill.menu.repository;

import com.giadinh.apporderbill.menu.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    Optional<MenuItem> findById(Long id);

    Optional<MenuItem> findByName(String name);

    List<MenuItem> findAll();

    List<MenuItem> findActive();

    void save(MenuItem item);

    void delete(Long id);
}

