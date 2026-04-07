package com.giadinh.apporderbill.javafx.order.handlers;

import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.ReleaseEmptyActiveOrderUseCase;
import com.giadinh.apporderbill.orders.usecase.TransferOrderBetweenTablesUseCase;
import com.giadinh.apporderbill.table.usecase.DeleteTableUseCase;
import com.giadinh.apporderbill.table.usecase.AddTableUseCase;
import com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase;
import com.giadinh.apporderbill.table.usecase.RenameTableUseCase;
import com.giadinh.apporderbill.table.usecase.SetTableReservationUseCase;
import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;
import com.giadinh.apporderbill.table.usecase.dto.DeleteTableInput;
import com.giadinh.apporderbill.table.usecase.dto.TableOutput;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private DeleteTableUseCase deleteTableUseCase;
    private OrderRepository orderRepository;
    private TransferOrderBetweenTablesUseCase transferOrderBetweenTablesUseCase;
    private ReleaseEmptyActiveOrderUseCase releaseEmptyActiveOrderUseCase;
    private RenameTableUseCase renameTableUseCase;
    private SetTableReservationUseCase setTableReservationUseCase;

    private Runnable onTablesChanged;

    private final List<Button> tableButtons = new ArrayList<>();
    private final Map<String, TableOutput> tableRowByName = new HashMap<>();
    private String selectedTableName;
    private Set<String> tablesInUseCache = new HashSet<>();

    private static final class TableRef {
        final String id;
        final String name;

        TableRef(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

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

    public void setOnTablesChanged(Runnable onTablesChanged) {
        this.onTablesChanged = onTablesChanged;
    }

    public void setPresenter(OrderScreenPresenter presenter) {
        this.presenter = presenter;
    }

    public void setAddTableUseCase(AddTableUseCase useCase) {
        this.addTableUseCase = useCase;
    }

    public void setDeleteTableUseCase(DeleteTableUseCase useCase) {
        this.deleteTableUseCase = useCase;
    }

    public void setClearTableUseCase(com.giadinh.apporderbill.table.usecase.ClearTableUseCase useCase) {
    }

    public void setGetAllTablesUseCase(GetAllTablesUseCase useCase) {
        this.getAllTablesUseCase = useCase;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setTransferOrderBetweenTablesUseCase(TransferOrderBetweenTablesUseCase useCase) {
        this.transferOrderBetweenTablesUseCase = useCase;
    }

    public void setReleaseEmptyActiveOrderUseCase(ReleaseEmptyActiveOrderUseCase useCase) {
        this.releaseEmptyActiveOrderUseCase = useCase;
    }

    public void setRenameTableUseCase(RenameTableUseCase useCase) {
        this.renameTableUseCase = useCase;
    }

    public void setSetTableReservationUseCase(SetTableReservationUseCase useCase) {
        this.setTableReservationUseCase = useCase;
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
        tableRowByName.clear();

        var rows = getAllTablesUseCase.execute();
        for (var row : rows) {
            tableRowByName.put(row.getTableName(), row);
            String name = row.getTableName();
            Button btn = new Button(name);
            btn.setUserData(new TableRef(row.getTableId(), name));
            btn.setMinWidth(Region.USE_PREF_SIZE);
            btn.setPrefHeight(40);
            btn.setStyle(buildButtonStyle(false, false, isReserved(row)));
            btn.setOnAction(e -> onTableButtonClick(name));
            btn.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    e.consume();
                }
            });
            btn.setOnContextMenuRequested(ev -> {
                TableRef ref = (TableRef) btn.getUserData();
                if (ref != null) {
                    showTableContextMenu(btn, ref, ev.getScreenX(), ev.getScreenY());
                }
                ev.consume();
            });
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
            TableRef ref = (TableRef) btn.getUserData();
            if (ref == null) {
                continue;
            }
            String name = ref.name;
            boolean inUse = tablesInUseCache.contains(name);
            boolean selected = name.equals(selectedTableName);
            boolean reserved = isReserved(tableRowByName.get(name));
            btn.setStyle(buildButtonStyle(inUse, selected, reserved));
        }
        applyTableFilter();
    }

    private boolean isReserved(TableOutput row) {
        return row != null && "RESERVED".equalsIgnoreCase(row.getStatus());
    }

    private void applyTableFilter() {
        boolean showAll = allBtn == null || allBtn.isSelected();
        boolean showInUse = inUseBtn != null && inUseBtn.isSelected();
        boolean showEmpty = emptyBtn != null && emptyBtn.isSelected();

        for (Button btn : tableButtons) {
            TableRef ref = (TableRef) btn.getUserData();
            if (ref == null) {
                continue;
            }
            String name = ref.name;
            boolean inUse = tablesInUseCache.contains(name);
            boolean visible = showAll || (showInUse && inUse) || (showEmpty && !inUse);
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    private static String buildButtonStyle(boolean inUse, boolean selected, boolean reserved) {
        String bg;
        if (inUse) {
            bg = "#ffcdd2";
        } else if (reserved) {
            bg = "#fff9c4";
        } else {
            bg = "#c8e6c9";
        }
        String border = selected ? "-fx-border-color: #1565c0; -fx-border-width: 2px; -fx-border-radius: 4px;" : "";
        return "-fx-background-color: " + bg + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 14 8 14; "
                + "-fx-background-radius: 4px; " + border;
    }

    private Window ownerWindow() {
        if (tableContainer != null && tableContainer.getScene() != null && tableContainer.getScene().getWindow() != null) {
            return tableContainer.getScene().getWindow();
        }
        return null;
    }

    private boolean tableHasActiveOrder(String tableName) {
        return orderRepository != null && orderRepository.findActiveByTable(tableName).isPresent();
    }

    private void notifyTablesChanged() {
        if (onTablesChanged != null) {
            onTablesChanged.run();
        }
    }

    private void showTableContextMenu(Button anchor, TableRef ref, double screenX, double screenY) {
        ContextMenu menu = new ContextMenu();

        MenuItem transfer = new MenuItem("Chuyển bàn…");
        transfer.setOnAction(e -> doTransferTable(ref));
        transfer.setDisable(!tableHasActiveOrder(ref.name) || transferOrderBetweenTablesUseCase == null);

        MenuItem rename = new MenuItem("Đổi tên bàn…");
        rename.setOnAction(e -> doRenameTable(ref));
        rename.setDisable(tableHasActiveOrder(ref.name) || renameTableUseCase == null);

        MenuItem releaseMistake = new MenuItem("Hủy mở bàn nhầm (order rỗng)");
        releaseMistake.setOnAction(e -> doReleaseEmptyOrder(ref));
        releaseMistake.setDisable(!tableHasActiveOrder(ref.name) || releaseEmptyActiveOrderUseCase == null);

        MenuItem reserve = new MenuItem("Đặt bàn trước");
        reserve.setOnAction(e -> doSetReserved(ref, true));
        reserve.setDisable(tableHasActiveOrder(ref.name) || isReserved(tableRowByName.get(ref.name))
                || setTableReservationUseCase == null);

        MenuItem unreserve = new MenuItem("Hủy đặt trước");
        unreserve.setOnAction(e -> doSetReserved(ref, false));
        unreserve.setDisable(!isReserved(tableRowByName.get(ref.name)) || setTableReservationUseCase == null);

        MenuItem deleteTable = new MenuItem("Xóa bàn…");
        deleteTable.setOnAction(e -> doDeleteTable(ref));
        deleteTable.setDisable(
                tableHasActiveOrder(ref.name) || isReserved(tableRowByName.get(ref.name)) || deleteTableUseCase == null);

        menu.getItems().addAll(transfer, rename, releaseMistake, reserve, unreserve, deleteTable);
        menu.show(anchor, screenX, screenY);
    }

    private void doTransferTable(TableRef ref) {
        if (transferOrderBetweenTablesUseCase == null) {
            return;
        }
        List<String> candidates = new ArrayList<>();
        for (Button b : tableButtons) {
            TableRef r = (TableRef) b.getUserData();
            if (r == null || r.name.equals(ref.name)) {
                continue;
            }
            if (!tableHasActiveOrder(r.name)) {
                candidates.add(r.name);
            }
        }
        if (candidates.isEmpty()) {
            if (errorHandler != null) {
                errorHandler.accept("Không có bàn trống để chuyển tới.");
            }
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(candidates.get(0), candidates);
        dialog.setTitle("Chuyển bàn");
        dialog.setHeaderText("Chuyển order từ " + ref.name + " sang bàn:");
        dialog.setContentText("Bàn đích:");
        dialog.initOwner(ownerWindow());
        Optional<String> picked = dialog.showAndWait();
        if (picked.isEmpty()) {
            return;
        }
        try {
            transferOrderBetweenTablesUseCase.execute(ref.name, picked.get());
        } catch (DomainException e) {
            if (errorHandler != null) {
                errorHandler.accept(e.getMessage());
            }
            return;
        }
        if (successHandler != null) {
            successHandler.accept(DomainMessages.formatKey("success.transferOrderBetweenTables", picked.get()));
        }
        if (presenter != null && ref.name.equals(presenter.getCurrentTableNumber())) {
            presenter.onTableSelected(picked.get());
            if (tableSelectionHandler != null) {
                tableSelectionHandler.accept(picked.get(), presenter.getCurrentOrderId());
            }
            selectedTableName = picked.get();
        }
        notifyTablesChanged();
    }

    private void doRenameTable(TableRef ref) {
        if (renameTableUseCase == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(ref.name);
        dialog.setTitle("Đổi tên bàn");
        dialog.setHeaderText("Cập nhật tên hiển thị (chỉ khi bàn chưa có order)");
        dialog.setContentText("Tên bàn mới:");
        dialog.initOwner(ownerWindow());
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
        try {
            renameTableUseCase.execute(ref.id, raw);
        } catch (DomainException e) {
            if (errorHandler != null) {
                errorHandler.accept(e.getMessage());
            }
            return;
        }
        if (successHandler != null) {
            successHandler.accept(DomainMessages.formatKey("success.renameTable", raw));
        }
        if (ref.name.equals(selectedTableName)) {
            selectedTableName = raw;
        }
        notifyTablesChanged();
    }

    private void doReleaseEmptyOrder(TableRef ref) {
        if (releaseEmptyActiveOrderUseCase == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(ownerWindow());
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Xóa order rỗng trên bàn " + ref.name + "? (Khi mở nhầm bàn, chưa gọi món)");
        Optional<ButtonType> ans = confirm.showAndWait();
        if (ans.isEmpty() || ans.get() != ButtonType.OK) {
            return;
        }
        try {
            releaseEmptyActiveOrderUseCase.execute(ref.name);
            if (successHandler != null) {
                successHandler.accept("Đã hủy order rỗng.");
            }
            if (presenter != null && ref.name.equals(presenter.getCurrentTableNumber())) {
                presenter.clearCurrentOrderUiAfterEmptyRelease();
            }
            notifyTablesChanged();
        } catch (DomainException ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex.getMessage());
            }
        } catch (Exception ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex.getMessage());
            }
        }
    }

    private void doSetReserved(TableRef ref, boolean reserved) {
        if (setTableReservationUseCase == null) {
            return;
        }
        try {
            setTableReservationUseCase.setReserved(ref.name, reserved);
        } catch (DomainException e) {
            if (errorHandler != null) {
                errorHandler.accept(e.getMessage());
            }
            return;
        }
        if (successHandler != null) {
            if (reserved) {
                successHandler.accept(DomainMessages.formatKey("success.setTableReserved", ref.name));
            } else {
                successHandler.accept(DomainMessages.formatKey("success.clearTableReservation", ref.name));
            }
        }
        notifyTablesChanged();
    }

    private void doDeleteTable(TableRef ref) {
        if (deleteTableUseCase == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(ownerWindow());
        confirm.setTitle("Xóa bàn");
        confirm.setHeaderText(null);
        confirm.setContentText("Xóa bàn \"" + ref.name + "\" khỏi hệ thống?");
        Optional<ButtonType> ans = confirm.showAndWait();
        if (ans.isEmpty() || ans.get() != ButtonType.OK) {
            return;
        }
        try {
            deleteTableUseCase.execute(new DeleteTableInput(ref.id));
            if (ref.name.equals(selectedTableName)) {
                selectedTableName = null;
            }
            if (successHandler != null) {
                successHandler.accept("Đã xóa bàn.");
            }
            notifyTablesChanged();
        } catch (DomainException ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex.getMessage());
            }
        } catch (Exception ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex.getMessage());
            }
        }
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
        dialog.initOwner(ownerWindow());

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

        try {
            addTableUseCase.execute(new AddTableInput(raw));
        } catch (DomainException e) {
            if (errorHandler != null) {
                errorHandler.accept(e.getMessage());
            }
            return;
        }
        if (successHandler != null) {
            successHandler.accept(DomainMessages.formatKey("success.addTable"));
        }
        notifyTablesChanged();
    }
}
