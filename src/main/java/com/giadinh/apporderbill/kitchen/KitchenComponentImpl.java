package com.giadinh.apporderbill.kitchen;

import com.giadinh.apporderbill.kitchen.usecase.PrintKitchenTicketUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintSelectedItemsUseCase;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketInput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketOutput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintSelectedItemsInput;
import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.shared.service.PrinterService;

public class KitchenComponentImpl implements KitchenComponent {
    private final PrintKitchenTicketUseCase printKitchenTicketUseCase;
    private final PrintSelectedItemsUseCase printSelectedItemsUseCase;

    public KitchenComponentImpl(OrderRepository orderRepository, MenuItemRepository menuItemRepository,
            Object kitchenTicketRepository, PrinterService printerService) {
        this.printKitchenTicketUseCase = new PrintKitchenTicketUseCase(
                orderRepository, menuItemRepository, kitchenTicketRepository, printerService);
        this.printSelectedItemsUseCase = new PrintSelectedItemsUseCase(
                orderRepository, menuItemRepository, kitchenTicketRepository, printerService);
    }

    @Override
    public PrintKitchenTicketOutput printKitchenTicket(PrintKitchenTicketInput input) {
        return printKitchenTicketUseCase.execute(input);
    }

    @Override
    public PrintKitchenTicketOutput printSelectedItems(PrintSelectedItemsInput input) {
        return printSelectedItemsUseCase.execute(input);
    }
}

