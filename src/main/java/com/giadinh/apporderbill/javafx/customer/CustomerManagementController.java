package com.giadinh.apporderbill.javafx.customer;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.model.PointTransaction;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerManagementController {

    // ── Customer table ─────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Long>    idColumn;
    @FXML private TableColumn<Customer, String>  nameColumn;
    @FXML private TableColumn<Customer, String>  phoneColumn;
    @FXML private TableColumn<Customer, Integer> pointsColumn;

    // ── Point history panel ────────────────────────────────────────────────
    @FXML private Label historyCustomerLabel;
    @FXML private Label historyBalanceLabel;
    @FXML private TableView<PointTransaction>    pointHistoryTable;
    @FXML private TableColumn<PointTransaction, String> txDateColumn;
    @FXML private TableColumn<PointTransaction, String> txTypeColumn;
    @FXML private TableColumn<PointTransaction, String> txDeltaColumn;
    @FXML private TableColumn<PointTransaction, Integer> txBalanceColumn;
    @FXML private TableColumn<PointTransaction, String> txNoteColumn;
    @FXML private TableColumn<PointTransaction, String> txOrderColumn;
    @FXML private TextField earnUnitAmountField;
    @FXML private TextField pointsPerUnitField;
    @FXML private TextField redeemPointsRequiredField;
    @FXML private TextField redeemValueField;
    @FXML private Label earnUnitInfoLabel;
    @FXML private Label pointsPerUnitInfoLabel;
    @FXML private Label redeemPointsInfoLabel;
    @FXML private Label redeemValueInfoLabel;

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ObservableList<Customer>         rows        = FXCollections.observableArrayList();
    private final ObservableList<PointTransaction> historyRows = FXCollections.observableArrayList();
    private CustomerUseCases useCases;

    @FXML
    public void initialize() {
        // Customer table columns
        customerTable.setItems(rows);
        idColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        phoneColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        pointsColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPoints()));
        // Highlight selected row event
        customerTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> showHistoryFor(selected));

        // Point history table columns
        pointHistoryTable.setItems(historyRows);
        txDateColumn.setCellValueFactory(c -> {
            PointTransaction tx = c.getValue();
            String s = tx.getCreatedAt() != null ? tx.getCreatedAt().format(DT_FMT) : "";
            return new SimpleStringProperty(s);
        });
        txTypeColumn.setCellValueFactory(c -> {
            PointTransaction tx = c.getValue();
            String label = switch (tx.getType()) {
                case EARN   -> msg("ui.customer.tx_type_earn");
                case REDEEM -> msg("ui.customer.tx_type_redeem");
            };
            return new SimpleStringProperty(label);
        });
        txDeltaColumn.setCellValueFactory(c -> {
            int delta = c.getValue().getDelta();
            String s = (delta >= 0 ? "+" : "") + delta + " " + msg("ui.customer.points_suffix");
            return new SimpleStringProperty(s);
        });
        // Color coding: green = earn, orange = redeem
        txDeltaColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("+")) {
                        setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e65100; -fx-font-weight: bold;");
                    }
                }
            }
        });
        txBalanceColumn.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getBalanceAfter()));
        txNoteColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNote()));
        txOrderColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrderId()));
        initLoyaltyHints();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> onSearch());
        }
    }

    public void setUseCases(CustomerUseCases useCases) {
        this.useCases = useCases;
        loadLoyaltyConfig();
        onRefresh();
    }

    // ── Customer actions ───────────────────────────────────────────────────

    @FXML private void onSearch() {
        if (useCases == null) return;
        String query = searchField == null ? "" : searchField.getText();
        String digitsOnly = query == null ? "" : query.replaceAll("\\D", "");
        if (digitsOnly.length() >= 4) {
            rows.setAll(useCases.searchByPhonePrefixBTree(query));
            return;
        }
        if (digitsOnly.isEmpty()) {
            rows.setAll(useCases.getAll(query));
            return;
        }
        rows.clear();
    }

    @FXML private void onRefresh() {
        if (useCases == null) return;
        loadLoyaltyConfig();
        rows.setAll(useCases.getAll(""));
        historyRows.clear();
        historyCustomerLabel.setText(msg("ui.customer.history_select_hint"));
        historyBalanceLabel.setText("");
    }

    @FXML private void onAdd() {
        if (useCases == null) return;
        CustomerFormData data = showCustomerForm(msg("ui.customer.form_add_title"), null);
        if (data == null) return;
        try {
            useCases.create(data.name(), data.phone(), data.points());
            onRefresh();
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    @FXML private void onEdit() {
        if (useCases == null) return;
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo(msg("ui.customer.select_customer")); return; }
        CustomerFormData data = showCustomerForm(msg("ui.customer.form_edit_title"), selected);
        if (data == null) return;
        try {
            useCases.update(selected.getId(), data.name(), data.phone(), data.points());
            onRefresh();
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    @FXML private void onDelete() {
        if (useCases == null) return;
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showInfo(msg("ui.customer.select_customer")); return; }
        useCases.delete(selected.getId());
        onRefresh();
    }

    /** Gọi khi click vào dòng trong bảng khách (hoặc qua selection listener) */
    @FXML private void onCustomerSelected() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        showHistoryFor(selected);
    }

    // ── History ────────────────────────────────────────────────────────────

    private void showHistoryFor(Customer customer) {
        historyRows.clear();
        if (customer == null || useCases == null) {
            historyCustomerLabel.setText(msg("ui.customer.history_select_hint"));
            historyBalanceLabel.setText("");
            return;
        }
        historyCustomerLabel.setText(
                (customer.getName() != null ? customer.getName() : "") +
                " — " + customer.getPhone());
        historyBalanceLabel.setText(msg("ui.customer.current_points", customer.getPoints()));

        List<PointTransaction> history = useCases.getPointHistory(customer.getId());
        historyRows.setAll(history);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private CustomerFormData showCustomerForm(String title, Customer existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType saveType = new ButtonType(msg("ui.customer.save_button"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(saveType, ButtonType.CANCEL);

        TextField nameField   = new TextField(existing == null ? "" : existing.getName());
        TextField phoneField  = new TextField(existing == null ? "" : existing.getPhone());
        TextField pointsField = new TextField(existing == null ? "0" : String.valueOf(existing.getPoints()));
        pointsField.setDisable(existing != null); // không cho sửa điểm trực tiếp nếu đã có lịch sử

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.addRow(0, new Label(msg("ui.customer.form_name_label")), nameField);
        form.addRow(1, new Label(msg("ui.customer.form_phone_label")), phoneField);
        form.addRow(2, new Label(msg("ui.customer.form_points_label")), pointsField);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(400);

        dialog.showAndWait();
        if (dialog.getResult() != saveType) return null;

        String name   = nameField.getText() == null ? "" : nameField.getText().trim();
        String phone  = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String pText  = pointsField.getText() == null ? "0" : pointsField.getText().trim();
        if (name.isBlank())                       { showInfo(msg("ui.customer.name_required"));     return null; }
        if (phone.isBlank() || !phone.matches("\\d{9,11}")) { showInfo(msg("ui.customer.phone_invalid")); return null; }
        int pts;
        try { pts = Integer.parseInt(pText); } catch (Exception e) { showInfo(msg("ui.customer.points_integer")); return null; }
        if (pts < 0) { showInfo(msg("ui.customer.points_non_negative")); return null; }
        return new CustomerFormData(name, phone, pts);
    }

    private record CustomerFormData(String name, String phone, int points) {}

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }

    @FXML
    private void onSaveLoyaltyConfig() {
        if (useCases == null) return;
        try {
            LoyaltyConfig config = new LoyaltyConfig(
                    parsePositiveLong(earnUnitAmountField, "ui.customer.loyalty_earn_unit_invalid"),
                    parsePositiveInt(pointsPerUnitField, "ui.customer.loyalty_points_per_unit_invalid"),
                    parsePositiveInt(redeemPointsRequiredField, "ui.customer.loyalty_redeem_points_invalid"),
                    parsePositiveLong(redeemValueField, "ui.customer.loyalty_redeem_value_invalid")
            );
            useCases.updateLoyaltyConfig(config);
            showInfo(msg("ui.customer.loyalty_save_success"));
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    private void loadLoyaltyConfig() {
        if (useCases == null || earnUnitAmountField == null) return;
        LoyaltyConfig config = useCases.reloadLoyaltyConfig();
        earnUnitAmountField.setText(String.valueOf(config.getEarnUnitAmount()));
        pointsPerUnitField.setText(String.valueOf(config.getPointsPerUnit()));
        redeemPointsRequiredField.setText(String.valueOf(config.getRedeemPointsRequired()));
        redeemValueField.setText(String.valueOf(config.getRedeemValue()));
    }

    private long parsePositiveLong(TextField field, String errorMessageKey) {
        long value;
        try {
            value = Long.parseLong(field.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
        if (value <= 0) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
        return value;
    }

    private int parsePositiveInt(TextField field, String errorMessageKey) {
        int value;
        try {
            value = Integer.parseInt(field.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
        if (value <= 0) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
        return value;
    }

    private void initLoyaltyHints() {
        installHint(earnUnitInfoLabel, "ui.customer.loyalty_hint_earn_unit");
        installHint(pointsPerUnitInfoLabel, "ui.customer.loyalty_hint_points_per_unit");
        installHint(redeemPointsInfoLabel, "ui.customer.loyalty_hint_redeem_points");
        installHint(redeemValueInfoLabel, "ui.customer.loyalty_hint_redeem_value");
    }

    private void installHint(Label label, String key) {
        if (label == null) {
            return;
        }
        String message = msg(key);
        Tooltip tooltip = new Tooltip(message);
        Tooltip.install(label, tooltip);
        label.setStyle(label.getStyle() + "; -fx-cursor: hand;");
        label.setOnMouseClicked(event -> showInfo(message));
    }
}
