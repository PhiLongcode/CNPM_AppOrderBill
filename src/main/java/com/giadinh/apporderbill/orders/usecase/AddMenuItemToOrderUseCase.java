package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
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
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));
        var menu = menuItemRepository.findById(input.getMenuItemId().intValue())
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_MENU_ITEM_NOT_FOUND));
        if (menu.isStockTracked()) {
            boolean ok = menuItemRepository.decreaseStockAtomic(menu.getId(), input.getQuantity());
            if (!ok) {
                throw new DomainException(ErrorCode.MENU_ITEM_STOCK_INSUFFICIENT);
            }
        }

        String targetMenuItemId = String.valueOf(menu.getMenuItemId());

        // Nếu món đã có trong order (cùng menuItemId, chưa có ghi chú riêng, chưa in phiếu bếp) thì cộng dồn
        // thay vì tạo thêm dòng mới. Dòng đã in bếp không gộp — thêm món trùng sẽ là dòng mới để in add-on.
        for (OrderItem existing : order.getItems()) {
            String note = existing.getNote();
            boolean hasCustomNote = note != null && !note.isBlank();
            if (targetMenuItemId.equals(existing.getMenuItemId()) && !hasCustomNote && !existing.isPrintedToKitchen()) {
                existing.updateQuantity(existing.getQuantity() + input.getQuantity());
                order.recomputeTotalFromItems();
                orderRepository.save(order);
                return new AddCustomItemOutput(
                        order.getTableNumber(),
                        OrderUseCaseSupport.toOutputs(order),
                        OrderUseCaseSupport.total(order));
            }
        }

        OrderItem item = new OrderItem(
                order.getOrderId(),
                targetMenuItemId,
                menu.getName(),
                input.getQuantity(),
                menu.getUnitPrice());
        if (input.getNotes() != null && !input.getNotes().isBlank()) {
            item.setNote(input.getNotes().trim());
        }

        order.addOrderItem(item);
        orderRepository.save(order);
        return new AddCustomItemOutput(
                order.getTableNumber(),
                OrderUseCaseSupport.toOutputs(order),
                OrderUseCaseSupport.total(order));
    }
}

