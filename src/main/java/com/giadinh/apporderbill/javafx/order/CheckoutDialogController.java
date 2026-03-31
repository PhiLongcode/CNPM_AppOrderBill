package com.giadinh.apporderbill.javafx.order;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class CheckoutDialogController {

    @FXML
    private Label orderTitleLabel;
    @FXML
    private Label orderInfoLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label finalAmountLabel;
    @FXML
    private Label changeAmountLabel;
    @FXML
    private TextField discountField;
    @FXML
    private TextField paidAmountField;
    @FXML
    private ToggleGroup paymentMethodGroup;
    @FXML
    private ToggleButton cashButton;
    @FXML
    private ToggleButton transferButton;
    @FXML
    private ToggleButton cardButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TableView<?> itemsTableView;
    @FXML
    private TableColumn<?, ?> itemIndexColumn;
    @FXML
    private TableColumn<?, ?> itemNameColumn;

    public record Result(long paidAmount, long discountAmount, String paymentMethod) {
    }

    public void initSummary(long totalAmount, long finalAmount, String tableInfo) {
        totalAmountLabel.setText(String.valueOf(totalAmount));
        finalAmountLabel.setText(String.valueOf(finalAmount));
        changeAmountLabel.setText("0");
        orderInfoLabel.setText(tableInfo);
    }

    public Result buildResult() {
        long discount = parseLong(discountField.getText(), 0L);
        long paid = parseLong(paidAmountField.getText(), 0L);
        String method = resolvePaymentMethod();
        return new Result(paid, discount, method);
    }

    private long parseLong(String text, long defaultValue) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String resolvePaymentMethod() {
        if (paymentMethodGroup == null || paymentMethodGroup.getSelectedToggle() == null) {
            return "CASH";
        }
        if (paymentMethodGroup.getSelectedToggle() == cashButton) {
            return "CASH";
        }
        if (paymentMethodGroup.getSelectedToggle() == transferButton) {
            return "BANK_TRANSFER";
        }
        if (paymentMethodGroup.getSelectedToggle() == cardButton) {
            return "CARD";
        }
        return "CASH";
    }
}

