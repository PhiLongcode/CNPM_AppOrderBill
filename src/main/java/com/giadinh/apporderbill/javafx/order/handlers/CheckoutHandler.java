package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;

import java.util.function.Consumer;

public class CheckoutHandler {
    private Consumer<String> errorHandler;
    private Runnable onCheckoutSuccessHandler;
    private String discountAmount = "0";
    private String paidAmount = "0";
    private String paymentMethod = "CASH";
    private String customerPhone = "";

    public CheckoutHandler(Object totalAmountLabel, Object finalAmountLabel, Object discountField) {}
    public void setErrorHandler(Consumer<String> errorHandler) { this.errorHandler = errorHandler; }
    public void setOnCheckoutSuccessHandler(Runnable r) { this.onCheckoutSuccessHandler = r; }
    public void setPresenter(OrderScreenPresenter presenter) {}
    public void setMenuItemRepository(Object menuItemRepository) {}
    public void setOrderRepository(Object orderRepository) {}
    public void onPrintKitchenTicketClick() {}
    public void onReprintKitchenTicketClick() {}
    public void onReprintReceiptClick() {}
    public void onPrintSelectedItemsClick(java.util.List<Long> selectedItemIds, Runnable done) { if (done != null) done.run(); }
    public void onCheckoutClick(Object window) { if (onCheckoutSuccessHandler != null) onCheckoutSuccessHandler.run(); }
    public void onPrintDraftReceiptClick() {}
    public void setCurrentOrderId(Long currentOrderId) {}
    public void displayTotalAmount(long totalAmount) {}
    public void displayFinalAmount(long finalAmount) {}
    public void displayDiscountAmount(long discountAmount) { this.discountAmount = String.valueOf(discountAmount); }
    public void displayChangeAmount(long changeAmount) {}
    public String getDiscountAmount() { return discountAmount; }
    public String getPaidAmount() { return paidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCustomerPhone() { return customerPhone; }
}

