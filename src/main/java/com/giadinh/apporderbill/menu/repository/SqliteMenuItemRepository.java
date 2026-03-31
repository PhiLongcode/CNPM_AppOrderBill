package com.giadinh.apporderbill.menu.repository;

import com.giadinh.apporderbill.catalog.model.MenuItemStatus;
import com.giadinh.apporderbill.menu.model.MenuItem;

import java.util.List;
import java.util.Optional;

/**
 * Compatibility adapter: menu repository delegates to catalog repository.
 */
public class SqliteMenuItemRepository implements MenuItemRepository {
    private final com.giadinh.apporderbill.catalog.repository.MenuItemRepository catalogRepository;

    public SqliteMenuItemRepository(Object connectionProvider) {
        this.catalogRepository = new com.giadinh.apporderbill.catalog.repository.SqliteMenuItemRepository(connectionProvider);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return catalogRepository.findById(id.intValue()).map(this::toMenuModel);
    }

    @Override
    public Optional<MenuItem> findByName(String name) {
        return catalogRepository.findByName(name).map(this::toMenuModel);
    }

    @Override
    public List<MenuItem> findAll() {
        return catalogRepository.findAll().stream().map(this::toMenuModel).toList();
    }

    @Override
    public List<MenuItem> findActive() {
        return catalogRepository.findByStatus(MenuItemStatus.ACTIVE).stream().map(this::toMenuModel).toList();
    }

    @Override
    public void save(MenuItem item) {
        var status = item.isActive() ? MenuItemStatus.ACTIVE : MenuItemStatus.INACTIVE;
        var catalogItem = new com.giadinh.apporderbill.catalog.model.MenuItem(
                item.getMenuItemId() == null ? 0 : item.getMenuItemId().intValue(),
                item.getName(),
                item.getUnitPrice() == null ? 0.0 : item.getUnitPrice().doubleValue(),
                item.getCategory(),
                item.getImageUrl(),
                item.isStockTracked(),
                item.getStockQty() == null ? 0 : item.getStockQty().intValue(),
                item.getStockMin() == null ? 0 : item.getStockMin().intValue(),
                item.getStockMin() == null ? 0 : item.getStockMin().intValue() * 2,
                item.getBaseUnit(),
                status);
        catalogRepository.save(catalogItem);
    }

    @Override
    public void delete(Long id) {
        if (id != null) {
            catalogRepository.delete(id.intValue());
        }
    }

    private MenuItem toMenuModel(com.giadinh.apporderbill.catalog.model.MenuItem item) {
        boolean active = item.getStatus() == MenuItemStatus.ACTIVE;
        return new MenuItem(
                (long) item.getId(),
                item.getName(),
                item.getCategoryName(),
                Math.round(item.getPrice()),
                item.getImageUrl(),
                item.getUnitOfMeasureName(),
                item.isStockManaged(),
                (long) item.getCurrentStockQuantity(),
                (long) item.getMinStockQuantity(),
                active);
    }
}

