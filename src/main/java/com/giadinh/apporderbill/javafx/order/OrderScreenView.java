package com.giadinh.apporderbill.javafx.order;

import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput;

import java.util.List;
import java.util.function.Consumer;

public interface OrderScreenView {
    void displayTableNumber(String tableNumber);
    void displayOrderItems(List<OrderItemViewModel> items);
    void displayTotalAmount(long totalAmount);
    void displayFinalAmount(long finalAmount);
    void displayDiscountAmount(long discountAmount);
    void displayChangeAmount(long changeAmount);
    String getDiscountAmount();
    String getPaidAmount();
    String getPaymentMethod();
    String getCustomerPhone();
    void setCustomerInfo(String phone, String name);
    void clearItemInputFields();
    void showError(String message);
    void showSuccess(String message);
    boolean showConfirmation(String title, String message);
    void setAddItemButtonEnabled(boolean enabled);
    String getItemName();
    String getItemQuantity();
    String getItemPrice();
    void showReprintReceiptDialog(List<PaymentSummaryOutput> payments, Consumer<Long> onSelect);
    void refreshMenuItems();
    void showQuickRestockDialog(MenuItemOutput menuItem);
}

