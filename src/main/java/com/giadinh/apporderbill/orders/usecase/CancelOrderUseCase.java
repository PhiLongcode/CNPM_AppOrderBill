package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.CancelOrderOutput;
import com.giadinh.apporderbill.table.repository.TableRepository;

public class CancelOrderUseCase {
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;

    public CancelOrderUseCase(OrderRepository orderRepository, MenuItemRepository menuItemRepository,
            TableRepository tableRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
    }

    public CancelOrderOutput execute(CancelOrderInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        restoreOrderStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        String tableName = order.getTableNumber();
        if (tableRepository != null && tableName != null) {
            tableRepository.findByTableName(tableName).ifPresent(table -> {
                table.clearTable();
                tableRepository.save(table);
            });
        }
        return new CancelOrderOutput(true);
    }

    private void restoreOrderStock(com.giadinh.apporderbill.orders.model.Order order) {
        for (var item : order.getItems()) {
            Integer menuItemId = parseMenuItemId(item.getMenuItemId());
            if (menuItemId == null) {
                continue;
            }
            var menuOpt = menuItemRepository.findById(menuItemId);
            if (menuOpt.isEmpty() || !menuOpt.get().isStockTracked()) {
                continue;
            }
            menuItemRepository.increaseStockAtomic(menuItemId, item.getQuantity());
        }
    }

    private Integer parseMenuItemId(String menuItemIdText) {
        try {
            return Integer.parseInt(menuItemIdText);
        } catch (Exception e) {
            return null;
        }
    }
}

