package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;

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

    public TableHandler(FlowPane tableContainer, ToggleButton allBtn, ToggleButton inUseBtn, ToggleButton emptyBtn) {
        this.tableContainer = tableContainer;
        this.allBtn = allBtn;
        this.inUseBtn = inUseBtn;
        this.emptyBtn = emptyBtn;
    }

    public void setErrorHandler(Consumer<String> errorHandler) { this.errorHandler = errorHandler; }
    public void setSuccessHandler(Consumer<String> successHandler) { this.successHandler = successHandler; }
    public void setTableSelectionHandler(BiConsumer<String, Long> tableSelectionHandler) { this.tableSelectionHandler = tableSelectionHandler; }
    public void setPresenter(OrderScreenPresenter presenter) {}
    public void setAddTableUseCase(com.giadinh.apporderbill.table.usecase.AddTableUseCase useCase) {}
    public void setDeleteTableUseCase(com.giadinh.apporderbill.table.usecase.DeleteTableUseCase useCase) {}
    public void setClearTableUseCase(com.giadinh.apporderbill.table.usecase.ClearTableUseCase useCase) {}
    public void setGetAllTablesUseCase(com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase useCase) {}

    public void initializeTables() {}
    public void loadTablesFromDatabase() {}
    public void updateTableButtonStyles(Set<String> tablesInUse) {}
    public void onAddTableClick() {
        if (successHandler != null) successHandler.accept("Da mo dialog them ban (placeholder).");
    }
}

