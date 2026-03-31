package com.giadinh.apporderbill.kitchen;

import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketInput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintKitchenTicketOutput;
import com.giadinh.apporderbill.kitchen.usecase.dto.PrintSelectedItemsInput;

public interface KitchenComponent {
    PrintKitchenTicketOutput printKitchenTicket(PrintKitchenTicketInput input);
    PrintKitchenTicketOutput printSelectedItems(PrintSelectedItemsInput input);
}

