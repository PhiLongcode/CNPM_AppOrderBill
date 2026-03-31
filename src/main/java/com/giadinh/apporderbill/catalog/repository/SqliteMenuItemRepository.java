package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.MenuItem;
import com.giadinh.apporderbill.catalog.model.MenuItemStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Temporary in-memory repository while SQLite implementation is pending.
 */
public class SqliteMenuItemRepository implements MenuItemRepository {
    private final ConcurrentHashMap<Integer, MenuItem> data = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public SqliteMenuItemRepository(Object connectionProvider) {
        // Keep constructor signature compatible with existing wiring.
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<MenuItem> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return data.values().stream()
                .filter(item -> name.equalsIgnoreCase(item.getName()))
                .findFirst();
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<MenuItem> findByCategoryName(String categoryName) {
        return data.values().stream()
                .filter(item -> categoryName != null && categoryName.equalsIgnoreCase(item.getCategoryName()))
                .toList();
    }

    @Override
    public List<MenuItem> findByStatus(MenuItemStatus status) {
        return data.values().stream()
                .filter(item -> item.getStatus() == status)
                .toList();
    }

    @Override
    public void save(MenuItem menuItem) {
        if (menuItem.getId() <= 0) {
            int newId = idGenerator.getAndIncrement();
            menuItem = new MenuItem(
                    newId,
                    menuItem.getName(),
                    menuItem.getPrice(),
                    menuItem.getCategoryName(),
                    menuItem.getImageUrl(),
                    menuItem.isStockManaged(),
                    menuItem.getCurrentStockQuantity(),
                    menuItem.getMinStockQuantity(),
                    menuItem.getMaxStockQuantity(),
                    menuItem.getUnitOfMeasureName(),
                    menuItem.getStatus());
        }
        data.put(menuItem.getId(), menuItem);
    }

    @Override
    public void delete(int id) {
        data.remove(id);
    }
}

