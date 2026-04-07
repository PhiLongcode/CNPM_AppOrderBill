package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.CheckoutDialogController;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class CheckoutHandler {
    private final Label totalAmountLabel;
    private final Label finalAmountLabel;
    private final TextField discountField;
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    private Consumer<String> errorHandler = m -> {};
    private Runnable onCheckoutSuccessHandler;
    private OrderScreenPresenter presenter;

    private long totalAmount;
    private long finalAmount;
    private String discountAmount = "0";
    private String paidAmount = "0";
    private String paymentMethod = "CASH";
    private String customerPhone = "";

    public CheckoutHandler(Object totalAmountLabel, Object finalAmountLabel, Object discountField) {
        this.totalAmountLabel = (Label) totalAmountLabel;
        this.finalAmountLabel = (Label) finalAmountLabel;
        this.discountField = (TextField) discountField;
    }

    public void setErrorHandler(Consumer<String> errorHandler) { this.errorHandler = errorHandler; }
    public void setOnCheckoutSuccessHandler(Runnable r) { this.onCheckoutSuccessHandler = r; }
    public void setPresenter(OrderScreenPresenter presenter) { this.presenter = presenter; }
    public void setMenuItemRepository(Object menuItemRepository) {}
    public void setOrderRepository(Object orderRepository) {}

    public void onPrintKitchenTicketClick() {
        if (presenter == null) {
            errorHandler.accept(msg("ui.order.system_not_ready"));
            return;
        }
        presenter.printKitchenTicket(false);
    }

    public void onReprintKitchenTicketClick() {
        if (presenter == null) {
            errorHandler.accept(msg("ui.order.system_not_ready"));
            return;
        }
        presenter.reprintKitchenTicket();
    }

    public void onReprintReceiptClick() {
        if (presenter == null) {
            errorHandler.accept(msg("ui.order.system_not_ready"));
            return;
        }
        presenter.showReprintReceiptDialog();
    }

    public void onPrintSelectedItemsClick(List<Long> selectedItemIds, Runnable done) {
        if (presenter == null) {
            errorHandler.accept(msg("ui.order.system_not_ready"));
            return;
        }
        boolean success = presenter.printSelectedItems(selectedItemIds);
        if (success && done != null) {
            done.run();
        }
    }

    public void onCheckoutClick(Object window) {
        if (presenter == null) {
            errorHandler.accept(msg("ui.order.system_not_ready"));
            return;
        }

        long currentDiscount = parseLong(discountField.getText(), 0L);
        presenter.calculateTotalWithDiscount(currentDiscount, null);
        showCheckoutDialog(window);
    }

    public void onPrintDraftReceiptClick() {
        if (presenter == null) {
            errorHandler.accept(msg("ui.order.system_not_ready"));
            return;
        }
        long currentDiscount = parseLong(discountField.getText(), 0L);
        presenter.printDraftReceipt(currentDiscount, null);
    }

    public void setCurrentOrderId(Long currentOrderId) {
        // Kept for compatibility with controller flow.
    }

    public void displayTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
        totalAmountLabel.setText(numberFormat.format(totalAmount));
    }

    public void displayFinalAmount(long finalAmount) {
        this.finalAmount = finalAmount;
        finalAmountLabel.setText(numberFormat.format(finalAmount));
    }

    public void displayDiscountAmount(long discountAmount) {
        this.discountAmount = String.valueOf(discountAmount);
        String normalized = String.valueOf(discountAmount);
        if (!normalized.equals(discountField.getText())) {
            discountField.setText(normalized);
        }
    }

    public void displayChangeAmount(long changeAmount) {}

    public String getDiscountAmount() { return discountAmount; }
    public String getPaidAmount() { return paidAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCustomerPhone() { return customerPhone; }

    private void showCheckoutDialog(Object window) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CheckoutHandler.class.getResource("/com/giadinh/apporderbill/javafx/order/checkout-dialog.fxml"));
            DialogPane dialogPane = new DialogPane();
            CheckoutDialogController controller = new CheckoutDialogController();
            loader.setRoot(dialogPane);
            loader.setController(controller);
            loader.load();

            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            if (window instanceof Window owner) {
                dialog.initOwner(owner);
            }
            dialog.setTitle(msg("ui.order.checkout_dialog_title"));

            String tableInfo = presenter.getCurrentTableNumber() == null ? "" : msg("ui.order.table_label", presenter.getCurrentTableNumber());
            controller.initSummary(totalAmount, finalAmount, tableInfo);

            controller.getCancelButton().setOnAction(e -> dialog.close());
            controller.getConfirmButton().setOnAction(e -> {
                CheckoutDialogController.Result result = controller.buildResult();
                Long beforeOrderId = presenter.getCurrentOrderId();
                presenter.checkout(result.paidAmount(), result.paymentMethod(), result.discountAmount(), null);
                boolean success = beforeOrderId != null && presenter.getCurrentOrderId() == null;
                if (success) {
                    paidAmount = String.valueOf(result.paidAmount());
                    paymentMethod = result.paymentMethod();
                    discountAmount = String.valueOf(result.discountAmount());
                    if (onCheckoutSuccessHandler != null) {
                        onCheckoutSuccessHandler.run();
                    }
                    dialog.close();
                }
            });

            dialog.showAndWait();
        } catch (IOException e) {
            errorHandler.accept(msg("ui.order.open_checkout_dialog_failed", e.getMessage()));
        }
    }

    private long parseLong(String text, long defaultValue) {
        if (text == null || text.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}

