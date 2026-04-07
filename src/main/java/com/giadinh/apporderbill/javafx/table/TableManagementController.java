package com.giadinh.apporderbill.javafx.table;

import com.giadinh.apporderbill.table.usecase.AddTableUseCase;
import com.giadinh.apporderbill.table.usecase.ClearTableUseCase;
import com.giadinh.apporderbill.table.usecase.DeleteTableUseCase;
import com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;
import com.giadinh.apporderbill.table.usecase.dto.ClearTableInput;
import com.giadinh.apporderbill.table.usecase.dto.DeleteTableInput;
import com.giadinh.apporderbill.table.usecase.dto.TableOutput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TableManagementController {

    @FXML
    private TableView<TableOutput> tablesTable;
    @FXML
    private TableColumn<TableOutput, String> idColumn;
    @FXML
    private TableColumn<TableOutput, String> nameColumn;
    @FXML
    private TableColumn<TableOutput, String> statusColumn;
    @FXML
    private TableColumn<TableOutput, String> orderColumn;

    private final ObservableList<TableOutput> rows = FXCollections.observableArrayList();

    private AddTableUseCase addTableUseCase;
    private DeleteTableUseCase deleteTableUseCase;
    private ClearTableUseCase clearTableUseCase;
    private GetAllTablesUseCase getAllTablesUseCase;

    @FXML
    public void initialize() {
        tablesTable.setItems(rows);
        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTableId()));
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTableName()));
        statusColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        orderColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getCurrentOrderId() == null ? "" : c.getValue().getCurrentOrderId()));
    }

    public void setUseCases(
            AddTableUseCase addTableUseCase,
            DeleteTableUseCase deleteTableUseCase,
            ClearTableUseCase clearTableUseCase,
            GetAllTablesUseCase getAllTablesUseCase) {
        this.addTableUseCase = addTableUseCase;
        this.deleteTableUseCase = deleteTableUseCase;
        this.clearTableUseCase = clearTableUseCase;
        this.getAllTablesUseCase = getAllTablesUseCase;
        onRefresh();
    }

    public void reloadFromDatabase() {
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        if (getAllTablesUseCase == null) {
            return;
        }
        rows.setAll(getAllTablesUseCase.execute());
    }

    @FXML
    private void onAdd() {
        if (addTableUseCase == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm bàn");
        dialog.setHeaderText("Nhập tên bàn hiển thị trên POS (ví dụ: Bàn VIP)");
        dialog.setContentText("Tên bàn:");
        if (tablesTable.getScene() != null && tablesTable.getScene().getWindow() != null) {
            dialog.initOwner(tablesTable.getScene().getWindow());
        }
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        String name = result.get().trim();
        if (name.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Tên bàn không được để trống.");
            return;
        }
        try {
            addTableUseCase.execute(new AddTableInput(name));
        } catch (DomainException e) {
            alert(Alert.AlertType.ERROR, e.getMessage());
            return;
        }
        onRefresh();
    }

    @FXML
    private void onDelete() {
        if (deleteTableUseCase == null) {
            return;
        }
        TableOutput selected = tablesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Vui lòng chọn một bàn để xóa.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa bàn \"" + selected.getTableName() + "\" khỏi hệ thống?\n(Các order đang mở vẫn có thể tham chiếu tên bàn.)",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            deleteTableUseCase.execute(new DeleteTableInput(selected.getTableId()));
            onRefresh();
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void onClearStatus() {
        if (clearTableUseCase == null) {
            return;
        }
        TableOutput selected = tablesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Vui lòng chọn bàn cần đặt trạng thái trống trong danh mục bàn.");
            return;
        }
        try {
            clearTableUseCase.execute(new ClearTableInput(selected.getTableId()));
            onRefresh();
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    private void alert(Alert.AlertType type, String message) {
        Alert a = new Alert(type, message, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
