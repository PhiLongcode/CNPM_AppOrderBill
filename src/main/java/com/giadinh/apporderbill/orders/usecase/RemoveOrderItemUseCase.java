package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.RemoveOrderItemInput;
import com.giadinh.apporderbill.orders.usecase.dto.RemoveOrderItemOutput;

public class RemoveOrderItemUseCase {
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public RemoveOrderItemUseCase(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public RemoveOrderItemOutput execute(RemoveOrderItemInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        int idx = Math.max(0, input.getOrderItemId().intValue() - 1);
        if (idx >= order.getItems().size()) {
            throw new DomainException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }
        var item = order.getItems().get(idx);
        rollbackStock(item.getMenuItemId(), item.getQuantity());
        order.removeOrderItemAt(idx);
        orderRepository.save(order);
        return new RemoveOrderItemOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }

    private void rollbackStock(String menuItemIdText, int quantity) {
        Integer menuItemId = parseMenuItemId(menuItemIdText);
        if (menuItemId == null) {
            return;
        }
        var menuOpt = menuItemRepository.findById(menuItemId);
        if (menuOpt.isEmpty() || !menuOpt.get().isStockTracked()) {
            return;
        }
        menuItemRepository.increaseStockAtomic(menuItemId, quantity);
    }

    private Integer parseMenuItemId(String menuItemIdText) {
        try {
            return Integer.parseInt(menuItemIdText);
        } catch (Exception e) {
            return null;
        }
    }
}

