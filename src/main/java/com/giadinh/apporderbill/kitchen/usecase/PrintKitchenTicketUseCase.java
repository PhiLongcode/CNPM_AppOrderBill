package com.giadinh.apporderbill.kitchen.usecase;

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
            return new PrintKitchenTicketOutput(false, "Thiếu orderId để in phiếu bếp.");
        }

        Order order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Order chưa có món để in phiếu bếp.");
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

        boolean ok = printerService.printKitchenTicket(content.toString());
        return ok
                ? new PrintKitchenTicketOutput(true, null)
                : new PrintKitchenTicketOutput(false, "Không gửi được lệnh in tới máy in bếp.");
    }
}

