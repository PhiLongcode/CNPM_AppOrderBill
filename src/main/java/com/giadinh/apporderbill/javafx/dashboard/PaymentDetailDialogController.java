package com.giadinh.apporderbill.javafx.dashboard;

import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PaymentDetailDialogController {
    @FXML private Label paymentIdLabel;
    @FXML private Label tableNumberLabel;
    @FXML private Label paidAtLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label discountAmountLabel;
    @FXML private Label finalAmountLabel;
    @FXML private Label paidAmountLabel;
    @FXML private Label changeAmountLabel;
    @FXML private TableView<ItemRow> itemsTable;
    @FXML private TableColumn<ItemRow, String> itemNameColumn;
    @FXML private TableColumn<ItemRow, Integer> quantityColumn;
    @FXML private TableColumn<ItemRow, String> unitColumn;
    @FXML private TableColumn<ItemRow, Long> unitPriceColumn;
    @FXML private TableColumn<ItemRow, String> discountColumn;
    @FXML private TableColumn<ItemRow, Long> lineTotalColumn;

    @FXML
    public void initialize() {
        itemNameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        quantityColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getQty()));
        unitColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUnit()));
        unitPriceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPrice()));
        discountColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDiscountText()));
        lineTotalColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getLineTotal()));
    }

    public void setPaymentDetail(PaymentDetailOutput detail) {
        if (detail == null) return;
        paymentIdLabel.setText(String.valueOf(detail.getPaymentId()));
        tableNumberLabel.setText(detail.getTableNumber());
        paidAtLabel.setText(detail.getPaidAt() == null ? "" : detail.getPaidAt().toString());
        paymentMethodLabel.setText(detail.getPaymentMethod());
        totalAmountLabel.setText(detail.getTotalAmount() + " VNĐ");
        discountAmountLabel.setText((detail.getDiscountAmount() == null ? 0L : detail.getDiscountAmount()) + " VNĐ");
        finalAmountLabel.setText(detail.getFinalAmount() + " VNĐ");
        paidAmountLabel.setText(detail.getPaidAmount() + " VNĐ");
        changeAmountLabel.setText(detail.getChangeAmount() + " VNĐ");
        itemsTable.getItems().clear();
    }

    public static class ItemRow {
        private final String name;
        private final int qty;
        private final String unit;
        private final long price;
        private final String discountText;
        private final long lineTotal;

        public ItemRow(String name, int qty, String unit, long price, String discountText, long lineTotal) {
            this.name = name;
            this.qty = qty;
            this.unit = unit;
            this.price = price;
            this.discountText = discountText;
            this.lineTotal = lineTotal;
        }
        public String getName() { return name; }
        public int getQty() { return qty; }
        public String getUnit() { return unit; }
        public long getPrice() { return price; }
        public String getDiscountText() { return discountText; }
        public long getLineTotal() { return lineTotal; }
    }
}

