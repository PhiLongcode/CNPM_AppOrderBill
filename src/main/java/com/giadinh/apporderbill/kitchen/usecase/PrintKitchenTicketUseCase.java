package com.giadinh.apporderbill.kitchen.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketInput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketOutput;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.shared.service.PrinterService;

public class PrintKitchenTicketUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;
    private final Object kitchenTicketRepository;
    private final PrinterService printerService;

    public PrintKitchenTicketUseCase(OrderRepository orderRepository,
            Object menuItemRepository,
            Object kitchenTicketRepository,
            PrinterService printerService) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.kitchenTicketRepository = kitchenTicketRepository;
        this.printerService = printerService;
    }

    public PrintKitchenTicketOutput execute(PrintKitchenTicketInput input) {
        if (input == null || input.getOrderId() == null) {
            throw new DomainException(ErrorCode.KITCHEN_ORDER_ID_REQUIRED);
        }

        Order order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getItems().isEmpty()) {
            throw new DomainException(ErrorCode.KITCHEN_ORDER_NO_ITEMS_FOR_TICKET);
        }

        StringBuilder content = new StringBuilder();
        content.append("=== PHIEU BEP ===\n");
        content.append("Order: ").append(order.getOrderId()).append("\n");
        content.append("Ban: ").append(order.getTableId()).append("\n");
        if (input.isReprint()) {
            content.append("Loai: IN LAI\n");
        } else if (input.isAddOn()) {
            content.append("Loai: THEM MON\n");
        } else {
            content.append("Loai: PHIEU MOI\n");
        }
        content.append("-----------------\n");
        order.getItems().forEach(item -> {
            content.append("- ").append(item.getMenuItemName())
                    .append(" x").append(item.getQuantity()).append("\n");
            if (item.getNote() != null && !item.getNote().isBlank()) {
                content.append("  * ").append(item.getNote()).append("\n");
            }
        });
        content.append("=================\n");

        if (!printerService.printKitchenTicket(content.toString())) {
            throw new DomainException(ErrorCode.PRINTER_KITCHEN_SEND_FAILED);
        }

        if (!input.isReprint()) {
            order.markAllItemsAsPrinted();
            orderRepository.save(order);
        }
        
        return new PrintKitchenTicketOutput(true, null);
    }
}
