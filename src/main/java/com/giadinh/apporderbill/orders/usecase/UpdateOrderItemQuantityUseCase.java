package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemQuantityInput;
import com.giadinh.apporderbill.orders.usecase.dto.UpdateOrderItemQuantityOutput;

public class UpdateOrderItemQuantityUseCase {
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public UpdateOrderItemQuantityUseCase(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public UpdateOrderItemQuantityOutput execute(UpdateOrderItemQuantityInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        int idx = Math.max(0, input.getOrderItemId().intValue() - 1);
        if (idx >= order.getItems().size()) {
            throw new DomainException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }
        var item = order.getItems().get(idx);
        int oldQuantity = item.getQuantity();
        int newQuantity = input.getNewQuantity();
        if (newQuantity > oldQuantity) {
            int delta = newQuantity - oldQuantity;
            applyStockDecrease(item.getMenuItemId(), delta);
        } else if (newQuantity < oldQuantity) {
            int delta = oldQuantity - newQuantity;
            applyStockIncrease(item.getMenuItemId(), delta);
        }
        order.updateOrderItemQuantityAt(idx, input.getNewQuantity());
        orderRepository.save(order);
        return new UpdateOrderItemQuantityOutput(OrderUseCaseSupport.toOutputs(order), OrderUseCaseSupport.total(order));
    }

    private void applyStockDecrease(String menuItemIdText, int delta) {
        Integer menuItemId = parseMenuItemId(menuItemIdText);
        if (menuItemId == null) {
            return;
        }
        var menuOpt = menuItemRepository.findById(menuItemId);
        if (menuOpt.isEmpty() || !menuOpt.get().isStockTracked()) {
            return;
        }
        boolean ok = menuItemRepository.decreaseStockAtomic(menuItemId, delta);
        if (!ok) {
            throw new DomainException(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT);
        }
    }

    private void applyStockIncrease(String menuItemIdText, int delta) {
        Integer menuItemId = parseMenuItemId(menuItemIdText);
        if (menuItemId == null) {
            return;
        }
        var menuOpt = menuItemRepository.findById(menuItemId);
        if (menuOpt.isEmpty() || !menuOpt.get().isStockTracked()) {
            return;
        }
        menuItemRepository.increaseStockAtomic(menuItemId, delta);
    }

    private Integer parseMenuItemId(String menuItemIdText) {
        try {
            return Integer.parseInt(menuItemIdText);
        } catch (Exception e) {
            return null;
        }
    }
}

