package com.giadinh.apporderbill.kitchen.usecase;

import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketOutput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintSelectedItemsInput;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.shared.service.PrinterService;

import java.util.HashSet;

public class PrintSelectedItemsUseCase {
    private final OrderRepository orderRepository;
    private final Object menuItemRepository;
    private final Object kitchenTicketRepository;
    private final PrinterService printerService;

    public PrintSelectedItemsUseCase(OrderRepository orderRepository,
            Object menuItemRepository,
            Object kitchenTicketRepository,
            PrinterService printerService) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.kitchenTicketRepository = kitchenTicketRepository;
        this.printerService = printerService;
    }

    public PrintKitchenTicketOutput execute(PrintSelectedItemsInput input) {
        if (input == null || input.getOrderId() == null || input.getOrderItemIds() == null || input.getOrderItemIds().isEmpty()) {
            throw new IllegalArgumentException("Thiếu danh sách món đã chọn.");
        }

        Order order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order."));

        HashSet<String> selected = new HashSet<>();
        input.getOrderItemIds().forEach(id -> selected.add(String.valueOf(id)));

        StringBuilder content = new StringBuilder();
        content.append("=== PHIEU BEP (MON CHON) ===\n");
        content.append("Order: ").append(order.getOrderId()).append("\n");
        content.append("Ban: ").append(order.getTableId()).append("\n");
        content.append("----------------------------\n");

        int count = 0;
        for (var item : order.getItems()) {
            if (!selected.contains(item.getOrderItemId())) {
                continue;
            }
            count++;
            content.append("- ").append(item.getMenuItemName())
                    .append(" x").append(item.getQuantity()).append("\n");
            if (item.getNote() != null && !item.getNote().isBlank()) {
                content.append("  * ").append(item.getNote()).append("\n");
            }
        }

        if (count == 0) {
            // Fallback for id-format mismatch across layers: print first N items.
            int need = input.getOrderItemIds().size();
            for (int i = 0; i < order.getItems().size() && i < need; i++) {
                var item = order.getItems().get(i);
                count++;
                content.append("- ").append(item.getMenuItemName())
                        .append(" x").append(item.getQuantity()).append("\n");
                if (item.getNote() != null && !item.getNote().isBlank()) {
                    content.append("  * ").append(item.getNote()).append("\n");
                }
            }
        }

        if (count == 0) {
            throw new IllegalStateException("Không có món để in.");
        }

        content.append("============================\n");
        boolean ok = printerService.printKitchenTicket(content.toString());
        return ok
                ? new PrintKitchenTicketOutput(true, null)
                : new PrintKitchenTicketOutput(false, "Không gửi được lệnh in món đã chọn.");
    }
}

