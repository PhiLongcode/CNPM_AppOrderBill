package com.giadinh.apporderbill.javafx.dashboard;

import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentDetailDialogController {
    private static final NumberFormat MONEY = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    @FXML private Label paymentIdLabel;
    @FXML private Label tableNumberLabel;
    @FXML private Label paidAtLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label customerLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label discountAmountLabel;
    @FXML private Label netBeforeVatLabel;
    @FXML private Label amountAfterVatLabel;
    @FXML private Label finalAmountLabel;
    @FXML private Label paidAmountLabel;
    @FXML private Label changeAmountLabel;
    @FXML private HBox pointsDiscountRow;
    @FXML private Label pointsDiscountLabel;
    @FXML private Label vatSummaryLabel;
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
        String custLine = "—";
        if (detail.getCustomerName() != null && !detail.getCustomerName().isBlank()) {
            custLine = detail.getCustomerName();
            if (detail.getCustomerPhone() != null && !detail.getCustomerPhone().isBlank()) {
                custLine = custLine + " (" + detail.getCustomerPhone() + ")";
            }
        }
        customerLabel.setText(custLine);
        totalAmountLabel.setText(MONEY.format(detail.getTotalAmount()) + " VNĐ");
        discountAmountLabel.setText(MONEY.format(detail.getDiscountAmount() == null ? 0L : detail.getDiscountAmount()) + " VNĐ");
        netBeforeVatLabel.setText(MONEY.format(detail.getNetAmountBeforeVat()) + " VNĐ");
        amountAfterVatLabel.setText(MONEY.format(detail.getAmountAfterVatBeforePoints()) + " VNĐ");
        long pts = detail.getPointsDiscountAmount();
        if (pts > 0 && pointsDiscountRow != null) {
            pointsDiscountRow.setVisible(true);
            pointsDiscountRow.setManaged(true);
            pointsDiscountLabel.setText(MONEY.format(pts) + " VNĐ");
        } else if (pointsDiscountRow != null) {
            pointsDiscountRow.setVisible(false);
            pointsDiscountRow.setManaged(false);
        }
        if (detail.getVatAmount() > 0 || detail.getVatPercent() > 0) {
            vatSummaryLabel.setText(String.format(Locale.forLanguageTag("vi-VN"),
                    "%s%% — %s VNĐ",
                    stripTrailingZeros(detail.getVatPercent()),
                    MONEY.format(detail.getVatAmount())));
        } else {
            vatSummaryLabel.setText("Không");
        }
        finalAmountLabel.setText(MONEY.format(detail.getFinalAmount()) + " VNĐ");
        paidAmountLabel.setText(MONEY.format(detail.getPaidAmount()) + " VNĐ");
        changeAmountLabel.setText(MONEY.format(detail.getChangeAmount()) + " VNĐ");
        itemsTable.getItems().clear();
        for (PaymentDetailOutput.PaymentItemOutput item : detail.getItems()) {
            itemsTable.getItems().add(new ItemRow(
                    item.name(),
                    item.qty(),
                    item.unit(),
                    item.unitPrice(),
                    item.discountText(),
                    item.lineTotal()));
        }
    }

    private static String stripTrailingZeros(double v) {
        if (v == (long) v) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
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

