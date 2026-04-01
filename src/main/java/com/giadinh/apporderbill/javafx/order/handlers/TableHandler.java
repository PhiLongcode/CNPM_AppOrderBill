package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import com.giadinh.apporderbill.table.usecase.AddTableUseCase;
import com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase;
import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TableHandler {
    private final FlowPane tableContainer;
    private final ToggleButton allBtn;
    private final ToggleButton inUseBtn;
    private final ToggleButton emptyBtn;

    private Consumer<String> errorHandler;
    private Consumer<String> successHandler;
    private BiConsumer<String, Long> tableSelectionHandler;

    private OrderScreenPresenter presenter;
    private AddTableUseCase addTableUseCase;
    private GetAllTablesUseCase getAllTablesUseCase;

    private final List<Button> tableButtons = new ArrayList<>();
    private String selectedTableName;
    private Set<String> tablesInUseCache = new HashSet<>();

    public TableHandler(FlowPane tableContainer, ToggleButton allBtn, ToggleButton inUseBtn, ToggleButton emptyBtn) {
        this.tableContainer = tableContainer;
        this.allBtn = allBtn;
        this.inUseBtn = inUseBtn;
        this.emptyBtn = emptyBtn;

        javafx.scene.control.ToggleGroup filterGroup = new javafx.scene.control.ToggleGroup();
        allBtn.setToggleGroup(filterGroup);
        inUseBtn.setToggleGroup(filterGroup);
        emptyBtn.setToggleGroup(filterGroup);
        allBtn.setSelected(true);

        allBtn.setOnAction(e -> applyTableFilter());
        inUseBtn.setOnAction(e -> applyTableFilter());
        emptyBtn.setOnAction(e -> applyTableFilter());
    }

    public void setErrorHandler(Consumer<String> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setSuccessHandler(Consumer<String> successHandler) {
        this.successHandler = successHandler;
    }

    public void setTableSelectionHandler(BiConsumer<String, Long> tableSelectionHandler) {
        this.tableSelectionHandler = tableSelectionHandler;
    }

    public void setPresenter(OrderScreenPresenter presenter) {
        this.presenter = presenter;
    }

    public void setAddTableUseCase(AddTableUseCase useCase) {
        this.addTableUseCase = useCase;
    }

    public void setDeleteTableUseCase(com.giadinh.apporderbill.table.usecase.DeleteTableUseCase useCase) {
    }

    public void setClearTableUseCase(com.giadinh.apporderbill.table.usecase.ClearTableUseCase useCase) {
    }

    public void setGetAllTablesUseCase(GetAllTablesUseCase useCase) {
        this.getAllTablesUseCase = useCase;
    }

    public void initializeTables() {
        loadTablesFromDatabase();
    }

    public void loadTablesFromDatabase() {
        if (tableContainer == null || getAllTablesUseCase == null) {
            return;
        }
        tableContainer.getChildren().clear();
        tableButtons.clear();

        var rows = getAllTablesUseCase.execute();
        for (var row : rows) {
            String name = row.getTableName();
            Button btn = new Button(name);
            btn.setUserData(name);
            btn.setMinWidth(Region.USE_PREF_SIZE);
            btn.setPrefHeight(40);
            btn.setStyle(buildButtonStyle(false, false));
            btn.setOnAction(e -> onTableButtonClick(name));
            tableButtons.add(btn);
            tableContainer.getChildren().add(btn);
        }
        applyTableFilter();
        updateTableButtonStyles(tablesInUseCache);
    }

    private void onTableButtonClick(String tableName) {
        if (presenter == null) {
            if (errorHandler != null) {
                errorHandler.accept("Hệ thống chưa sẵn sàng. Vui lòng thử lại sau vài giây.");
            }
            return;
        }
        selectedTableName = tableName;
        presenter.onTableSelected(tableName);
        if (tableSelectionHandler != null) {
            tableSelectionHandler.accept(tableName, presenter.getCurrentOrderId());
        }
        updateTableButtonStyles(tablesInUseCache);
    }

    public void updateTableButtonStyles(Set<String> tablesInUse) {
        tablesInUseCache = tablesInUse != null ? new HashSet<>(tablesInUse) : new HashSet<>();
        for (Button btn : tableButtons) {
            String name = (String) btn.getUserData();
            boolean inUse = tablesInUseCache.contains(name);
            boolean selected = name.equals(selectedTableName);
            btn.setStyle(buildButtonStyle(inUse, selected));
        }
        applyTableFilter();
    }

    private void applyTableFilter() {
        boolean showAll = allBtn == null || allBtn.isSelected();
        boolean showInUse = inUseBtn != null && inUseBtn.isSelected();
        boolean showEmpty = emptyBtn != null && emptyBtn.isSelected();

        for (Button btn : tableButtons) {
            String name = (String) btn.getUserData();
            boolean inUse = tablesInUseCache.contains(name);
            boolean visible = showAll || (showInUse && inUse) || (showEmpty && !inUse);
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    private static String buildButtonStyle(boolean inUse, boolean selected) {
        String bg = inUse ? "#ffcdd2" : "#c8e6c9";
        String border = selected ? "-fx-border-color: #1565c0; -fx-border-width: 2px; -fx-border-radius: 4px;" : "";
        return "-fx-background-color: " + bg + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 14 8 14; "
                + "-fx-background-radius: 4px; " + border;
    }

    public void onAddTableClick() {
        if (addTableUseCase == null) {
            if (errorHandler != null) {
                errorHandler.accept("Chức năng thêm bàn chưa được khởi tạo.");
            }
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm bàn mới");
        dialog.setHeaderText("Nhập tên bàn hiển thị trên POS");
        dialog.setContentText("Tên bàn:");
        if (tableContainer != null && tableContainer.getScene() != null && tableContainer.getScene().getWindow() != null) {
            dialog.initOwner(tableContainer.getScene().getWindow());
        }

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        String raw = result.get().trim();
        if (raw.isEmpty()) {
            if (errorHandler != null) {
                errorHandler.accept("Tên bàn không được để trống.");
            }
            return;
        }

        var out = addTableUseCase.execute(new AddTableInput(raw));
        if (!out.isSuccess()) {
            if (errorHandler != null) {
                errorHandler.accept(out.getMessage());
            }
            return;
        }
        if (successHandler != null) {
            successHandler.accept(out.getMessage());
        }
        loadTablesFromDatabase();
    }
}
