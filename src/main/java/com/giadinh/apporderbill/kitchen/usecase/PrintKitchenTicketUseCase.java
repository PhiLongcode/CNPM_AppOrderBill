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

        try {
            if (!printerService.printKitchenTicket(input.getOrderId(), input.isAddOn(), input.isReprint())) {
                throw new DomainException(ErrorCode.PRINTER_KITCHEN_SEND_FAILED);
            }
        } catch (PrinterService.PrinterException e) {
            throw new DomainException(ErrorCode.PRINTER_KITCHEN_SEND_FAILED);
        }

        if (!input.isReprint()) {
            order.markAllItemsAsPrinted();
            orderRepository.save(order);
        }
        
        return new PrintKitchenTicketOutput(true, null);
    }
}
