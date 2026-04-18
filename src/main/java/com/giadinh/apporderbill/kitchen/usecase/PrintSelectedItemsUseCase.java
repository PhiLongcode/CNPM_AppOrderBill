package com.giadinh.apporderbill.kitchen.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketOutput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintSelectedItemsInput;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.shared.service.PrinterService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

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
            throw new DomainException(ErrorCode.KITCHEN_SELECTED_ITEMS_REQUIRED);
        }

        Order order = orderRepository.findById(String.valueOf(input.getOrderId()))
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        LinkedHashSet<Integer> selectedIndexes = new LinkedHashSet<>();
        int totalItems = order.getItems().size();
        for (Long id : input.getOrderItemIds()) {
            if (id == null) {
                continue;
            }
            int idx = id.intValue() - 1;
            if (idx >= 0 && idx < totalItems) {
                selectedIndexes.add(idx);
            }
        }

        List<Long> ids = new ArrayList<>(input.getOrderItemIds());
        if (selectedIndexes.isEmpty()) {
            throw new DomainException(ErrorCode.KITCHEN_NO_ITEMS_TO_PRINT);
        }

        try {
            if (!printerService.printKitchenTicketSelected(input.getOrderId(), ids)) {
                throw new DomainException(ErrorCode.PRINTER_KITCHEN_SEND_FAILED);
            }
        } catch (PrinterService.PrinterException e) {
            throw new DomainException(ErrorCode.PRINTER_KITCHEN_SEND_FAILED);
        }

        // Đồng bộ trạng thái đã in cho đúng với các món vừa in chọn.
        // Nhờ đó ở màn Order, khi thêm lại cùng món đã in sẽ tách dòng mới thay vì cộng dồn.
        for (Integer idx : selectedIndexes) {
            order.getItems().get(idx).setPrintedToKitchen(true);
        }
        orderRepository.save(order);

        return new PrintKitchenTicketOutput(true, null);
    }
}

