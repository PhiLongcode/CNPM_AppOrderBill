package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.model.OrderItem;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.AddCustomItemOutput;
import com.giadinh.apporderbill.orders.usecase.dto.AddMenuItemInput;

public class AddMenuItemToOrderUseCase {
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public AddMenuItemToOrderUseCase(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public AddCustomItemOutput execute(AddMenuItemInput input) {
        var order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));
        var menu = menuItemRepository.findById(input.getMenuItemId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món trong menu."));

        OrderItem item = new OrderItem(
                order.getOrderId(),
                String.valueOf(menu.getMenuItemId()),
                menu.getName(),
                input.getQuantity(),
                menu.getUnitPrice());
        if (input.getNotes() != null) {
            item.setNote(input.getNotes());
        }

        order.addOrderItem(item);
        orderRepository.save(order);
        return new AddCustomItemOutput(
                order.getTableNumber(),
                OrderUseCaseSupport.toOutputs(order),
                OrderUseCaseSupport.total(order));
    }
}

