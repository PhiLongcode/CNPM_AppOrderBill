package com.giadinh.apporderbill.kitchen.usecase.dto;

public class PrintKitchenTicketInput {
    private final Long orderId;
    private final boolean addOn;
    private final boolean reprint;

    public PrintKitchenTicketInput(Long orderId, boolean addOn) {
        this(orderId, addOn, false);
    }

    public PrintKitchenTicketInput(Long orderId, boolean addOn, boolean reprint) {
        this.orderId = orderId;
        this.addOn = addOn;
        this.reprint = reprint;
    }

    public Long getOrderId() {
        return orderId;
    }

    public boolean isAddOn() {
        return addOn;
    }

    public boolean isReprint() {
        return reprint;
    }
}

