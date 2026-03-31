package com.giadinh.apporderbill.javafx.customer;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class CustomerManagementController {
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Long> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, Integer> pointsColumn;

    private final ObservableList<Customer> rows = FXCollections.observableArrayList();
    private CustomerUseCases useCases;

    @FXML
    public void initialize() {
        customerTable.setItems(rows);
        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        phoneColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhone()));
        pointsColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPoints()));
    }

    public void setUseCases(CustomerUseCases useCases) {
        this.useCases = useCases;
        onRefresh();
    }

    @FXML
    private void onSearch() {
        if (useCases == null) return;
        rows.setAll(useCases.getAll(searchField.getText()));
    }

    @FXML
    private void onRefresh() {
        if (useCases == null) return;
        rows.setAll(useCases.getAll(""));
    }

    @FXML
    private void onAdd() {
        if (useCases == null) return;
        CustomerFormData data = showCustomerForm("Thêm khách hàng", null);
        if (data == null) return;
        try {
            useCases.create(data.name(), data.phone(), data.points());
            onRefresh();
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        if (useCases == null) return;
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Vui lòng chọn khách hàng.");
            return;
        }
        CustomerFormData data = showCustomerForm("Sửa khách hàng", selected);
        if (data == null) return;
        try {
            useCases.update(selected.getId(), data.name(), data.phone(), data.points());
            onRefresh();
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        if (useCases == null) return;
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Vui lòng chọn khách hàng.");
            return;
        }
        useCases.delete(selected.getId());
        onRefresh();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private CustomerFormData showCustomerForm(String title, Customer existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType saveType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(saveType, ButtonType.CANCEL);

        TextField nameField = new TextField(existing == null ? "" : existing.getName());
        TextField phoneField = new TextField(existing == null ? "" : existing.getPhone());
        TextField pointsField = new TextField(existing == null ? "0" : String.valueOf(existing.getPoints()));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Tên khách:"), nameField);
        form.addRow(1, new Label("Số điện thoại:"), phoneField);
        form.addRow(2, new Label("Điểm:"), pointsField);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait();
        if (dialog.getResult() != saveType) {
            return null;
        }

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String pointsText = pointsField.getText() == null ? "0" : pointsField.getText().trim();
        if (name.isBlank()) {
            showInfo("Tên khách không được trống.");
            return null;
        }
        if (phone.isBlank() || !phone.matches("\\d{9,11}")) {
            showInfo("Số điện thoại không hợp lệ (9-11 chữ số).");
            return null;
        }
        int points;
        try {
            points = Integer.parseInt(pointsText);
        } catch (Exception e) {
            showInfo("Điểm phải là số nguyên.");
            return null;
        }
        if (points < 0) {
            showInfo("Điểm không được âm.");
            return null;
        }
        return new CustomerFormData(name, phone, points);
    }

    private record CustomerFormData(String name, String phone, int points) {}
}

