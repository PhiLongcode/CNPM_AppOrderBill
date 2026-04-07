package com.giadinh.apporderbill.javafx.table;

import com.giadinh.apporderbill.table.usecase.AddTableUseCase;
import com.giadinh.apporderbill.table.usecase.ClearTableUseCase;
import com.giadinh.apporderbill.table.usecase.DeleteTableUseCase;
import com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
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
        dialog.setTitle(msg("ui.table.add_title"));
        dialog.setHeaderText(msg("ui.table.add_header"));
        dialog.setContentText(msg("ui.table.name_label"));
        if (tablesTable.getScene() != null && tablesTable.getScene().getWindow() != null) {
            dialog.initOwner(tablesTable.getScene().getWindow());
        }
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        String name = result.get().trim();
        if (name.isEmpty()) {
            alert(Alert.AlertType.WARNING, msg("ui.table.name_required"));
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
            alert(Alert.AlertType.WARNING, msg("ui.table.select_to_delete"));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                msg("ui.table.confirm_delete", selected.getTableName()),
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
            alert(Alert.AlertType.WARNING, msg("ui.table.select_to_clear"));
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

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}
