package com.giadinh.apporderbill.javafx.menu;

import com.giadinh.apporderbill.catalog.usecase.dto.ImportConflictStrategy;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MenuManagementController {
    @FXML private TableView<MenuItemRow> menuItemsTable;
    @FXML private TableColumn<MenuItemRow, Long> idColumn;
    @FXML private TableColumn<MenuItemRow, String> nameColumn;
    @FXML private TableColumn<MenuItemRow, String> categoryColumn;
    @FXML private TableColumn<MenuItemRow, Long> priceColumn;
    @FXML private TableColumn<MenuItemRow, Long> costColumn;
    @FXML private TableColumn<MenuItemRow, Long> stockColumn;
    @FXML private TableColumn<MenuItemRow, String> skuColumn;
    @FXML private TableColumn<MenuItemRow, String> baseUnitColumn;
    @FXML private TableColumn<MenuItemRow, String> activeColumn;
    @FXML private TableColumn<MenuItemRow, String> createdAtColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private CheckBox activeOnlyCheckBox;
    @FXML private CheckBox lowStockOnlyCheckBox;
    @FXML private CheckMenuItem showIdColumn;
    @FXML private CheckMenuItem showNameColumn;
    @FXML private CheckMenuItem showCategoryColumn;
    @FXML private CheckMenuItem showPriceColumn;
    @FXML private CheckMenuItem showCostColumn;
    @FXML private CheckMenuItem showStockColumn;
    @FXML private CheckMenuItem showSkuColumn;
    @FXML private CheckMenuItem showBaseUnitColumn;
    @FXML private CheckMenuItem showActiveColumn;
    @FXML private CheckMenuItem showCreatedAtColumn;
    @FXML private HBox stockWarningBanner;
    @FXML private Label stockWarningLabel;

    private final ObservableList<MenuItemRow> displayedRows = FXCollections.observableArrayList();
    private final List<MenuItemRow> allRows = new ArrayList<>();
    private MenuManagementPresenter presenter;

    public static class UnitRow {
        public String getUnitName() { return ""; }
        public double getRatioToBase() { return 1.0; }
        public long getSalePrice() { return 0L; }
        public String getSku() { return ""; }
        public boolean isDefaultForSale() { return false; }
    }

    public static class MenuItemRow {
        private final Long id;
        private final String name;
        private final String category;
        private final long priceValue;
        private final String imageUrl;
        private final Long costValue;
        private final String sku;
        private final String baseUnit;
        private final Long stockValue;
        private final Long stockMin;
        private final Long stockMax;
        private final boolean stockTracked;
        private final boolean active;
        private final List<UnitRow> units;

        public MenuItemRow(Long id, String name, String category, long priceValue, String imageUrl, Long costValue,
                String sku, String baseUnit, Long stockValue, Long stockMin, Long stockMax, boolean stockTracked,
                boolean active, List<UnitRow> units) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.priceValue = priceValue;
            this.imageUrl = imageUrl;
            this.costValue = costValue;
            this.sku = sku;
            this.baseUnit = baseUnit;
            this.stockValue = stockValue;
            this.stockMin = stockMin;
            this.stockMax = stockMax;
            this.stockTracked = stockTracked;
            this.active = active;
            this.units = units == null ? List.of() : units;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public long getPriceValue() { return priceValue; }
        public String getImageUrl() { return imageUrl; }
        public Long getCostValue() { return costValue; }
        public String getSku() { return sku; }
        public String getBaseUnit() { return baseUnit; }
        public Long getStockValue() { return stockValue; }
        public Long getStockMin() { return stockMin; }
        public Long getStockMax() { return stockMax; }
        public boolean isStockTracked() { return stockTracked; }
        public boolean isActive() { return active; }
        public String getActiveText() { return active ? "Đang bán" : "Ẩn"; }
        public String getCreatedAtText() { return ""; }
        public java.util.List<UnitRow> getUnits() { return units; }
    }

    @FXML
    public void initialize() {
        menuItemsTable.setItems(displayedRows);
        menuItemsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        categoryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategory()));
        priceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPriceValue()));
        costColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCostValue()));
        stockColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStockValue()));
        skuColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSku()));
        baseUnitColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBaseUnit()));
        activeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getActiveText()));
        createdAtColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAtText()));

        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        activeOnlyCheckBox.selectedProperty().addListener((obs, o, n) -> applyFilters());
        lowStockOnlyCheckBox.selectedProperty().addListener((obs, o, n) -> applyFilters());
    }

    public void setPresenter(MenuManagementPresenter presenter) {
        this.presenter = presenter;
        reloadFromDatabase();
    }

    public void reloadFromDatabase() {
        if (presenter == null) {
            return;
        }
        List<MenuItemOutput> outputs = presenter.loadAllMenuItems();
        allRows.clear();
        for (MenuItemOutput item : outputs) {
            allRows.add(new MenuItemRow(
                    item.getMenuItemId(), item.getName(), item.getCategory(),
                    item.getUnitPrice() == null ? 0L : item.getUnitPrice(), item.getImageUrl(),
                    null, "", item.getBaseUnit(), item.getStockQty(), item.getStockMin(), null,
                    item.isStockTracked(), item.isActive(), List.of()));
        }
        categoryFilter.getItems().setAll(allRows.stream().map(MenuItemRow::getCategory).filter(Objects::nonNull).distinct().sorted().toList());
        applyFilters();
        updateStockWarningBanner();
    }

    @FXML private void onAddClick() {
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        MenuItemDialogController.Result result = MenuItemDialogController.showDialog(null);
        if (result == null) {
            return;
        }
        try {
            presenter.createMenuItem(null, result);
            reloadFromDatabase();
            showInfo(msg("ui.menu.create_success"));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }

    @FXML private void onEditClick() {
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        MenuItemRow selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            showInfo(msg("ui.menu.select_item_to_edit"));
            return;
        }
        MenuItemDialogController.Result result = MenuItemDialogController.showDialog(selected);
        if (result == null) {
            return;
        }
        try {
            presenter.updateMenuItem(selected.getId(), result);
            reloadFromDatabase();
            showInfo(msg("ui.menu.update_success"));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }

    @FXML private void onDeleteClick() {
        List<MenuItemRow> selectedRows = new ArrayList<>(menuItemsTable.getSelectionModel().getSelectedItems());
        if (selectedRows.isEmpty()) {
            showInfo(msg("ui.menu.select_item_to_delete"));
            return;
        }
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        ButtonType yes = new ButtonType(msg("ui.common.yes"));
        ButtonType no = new ButtonType(msg("ui.common.no"), ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                msg("ui.menu.delete_multi_confirm", selectedRows.size()),
                yes, no);
        confirm.setHeaderText(null);
        if (confirm.showAndWait().orElse(no) != yes) {
            return;
        }
        try {
            int deletedCount = 0;
            for (MenuItemRow row : selectedRows) {
                if (row.getId() == null) {
                    continue;
                }
                presenter.deleteMenuItem(row.getId());
                deletedCount++;
            }
            reloadFromDatabase();
            showInfo(msg("ui.menu.delete_multi_success", deletedCount));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }

    @FXML private void onToggleActiveClick() {
        MenuItemRow selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            showInfo(msg("ui.menu.select_item_to_toggle"));
            return;
        }
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        try {
            presenter.toggleActive(selected.getId(), selected.isActive());
            reloadFromDatabase();
            showInfo(msg("ui.menu.toggle_success"));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }

    @FXML private void onAddStockClick() {
        MenuItemRow selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            showInfo(msg("ui.menu.select_item_to_stock"));
            return;
        }
        if (!selected.isStockTracked()) {
            showInfo(msg("ui.menu.stock_not_tracked"));
            return;
        }
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        TextInputDialog inputDialog = new TextInputDialog("1");
        inputDialog.setTitle(msg("ui.menu.stock_add_title"));
        inputDialog.setHeaderText(null);
        inputDialog.setContentText(msg("ui.menu.stock_add_prompt"));
        var value = inputDialog.showAndWait();
        if (value.isEmpty()) {
            return;
        }
        try {
            long delta = Long.parseLong(value.get().trim());
            if (delta <= 0) {
                showError(msg("ui.menu.stock_positive_required"));
                return;
            }
            long currentStock = selected.getStockValue() == null ? 0L : selected.getStockValue();
            presenter.addStock(selected.getId(), delta, currentStock);
            reloadFromDatabase();
            showInfo(msg("ui.menu.stock_add_success"));
        } catch (NumberFormatException ex) {
            showError(msg("ui.menu.stock_invalid_number"));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }

    @FXML private void onRefreshClick() { reloadFromDatabase(); }
    @FXML private void onImportClick() {
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = chooser.showOpenDialog(menuItemsTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            ImportConflictStrategy strategy = askImportConflictStrategy();
            if (strategy == null) {
                return;
            }
            int imported = presenter.importFromExcel(file.getAbsolutePath(), strategy);
            reloadFromDatabase();
            showInfo(msg("ui.menu.import_success", imported));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }

    @FXML private void onExportClick() {
        if (presenter == null) {
            showError(msg("ui.menu.presenter_not_ready"));
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("menu-export.xlsx");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = chooser.showSaveDialog(menuItemsTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            presenter.exportToExcel(file.getAbsolutePath());
            showInfo(msg("ui.menu.export_success"));
        } catch (Exception e) {
            showError(errorMessage(e));
        }
    }
    @FXML private void onShowLowStock() { lowStockOnlyCheckBox.setSelected(true); applyFilters(); }

    @FXML
    private void onColumnVisibilityChange() {
        idColumn.setVisible(showIdColumn.isSelected());
        nameColumn.setVisible(showNameColumn.isSelected());
        categoryColumn.setVisible(showCategoryColumn.isSelected());
        priceColumn.setVisible(showPriceColumn.isSelected());
        costColumn.setVisible(showCostColumn.isSelected());
        stockColumn.setVisible(showStockColumn.isSelected());
        skuColumn.setVisible(showSkuColumn.isSelected());
        baseUnitColumn.setVisible(showBaseUnitColumn.isSelected());
        activeColumn.setVisible(showActiveColumn.isSelected());
        createdAtColumn.setVisible(showCreatedAtColumn.isSelected());
    }

    private void applyFilters() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String category = categoryFilter.getValue();
        boolean activeOnly = activeOnlyCheckBox.isSelected();
        boolean lowStockOnly = lowStockOnlyCheckBox.isSelected();

        displayedRows.setAll(allRows.stream().filter(row -> {
            boolean matchKeyword = keyword.isEmpty() || (row.getName() != null && row.getName().toLowerCase().contains(keyword));
            boolean matchCategory = category == null || category.isBlank() || category.equals(row.getCategory());
            boolean isLowStock = isLowOrOutOfStock(row);
            boolean matchActive = !activeOnly || row.isActive() || (lowStockOnly && isLowStock);
            boolean matchStock = !lowStockOnly || isLowStock;
            return matchKeyword && matchCategory && matchActive && matchStock;
        }).collect(Collectors.toList()));
    }

    private boolean isLowOrOutOfStock(MenuItemRow row) {
        if (!row.isStockTracked() || row.getStockValue() == null) {
            return false;
        }
        if (row.getStockValue() <= 0) {
            return true;
        }
        return row.getStockMin() != null && row.getStockValue() <= row.getStockMin();
    }

    private void updateStockWarningBanner() {
        if (stockWarningBanner == null || stockWarningLabel == null) {
            return;
        }
        long warningCount = allRows.stream().filter(this::isLowOrOutOfStock).count();
        if (warningCount <= 0) {
            stockWarningBanner.setManaged(false);
            stockWarningBanner.setVisible(false);
            return;
        }
        stockWarningBanner.setManaged(true);
        stockWarningBanner.setVisible(true);
        stockWarningLabel.setText(msg("ui.menu.stock_warning_with_count", warningCount));
    }

    private void showInfo(String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showError(String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private String errorMessage(Exception e) {
        return e.getMessage() == null || e.getMessage().isBlank()
                ? msg("ui.menu.operation_failed")
                : e.getMessage();
    }

    private ImportConflictStrategy askImportConflictStrategy() {
        ButtonType replace = new ButtonType(msg("ui.menu.import_strategy_replace"));
        ButtonType keep = new ButtonType(msg("ui.menu.import_strategy_keep"));
        ButtonType createNew = new ButtonType(msg("ui.menu.import_strategy_new_id"));
        ButtonType cancel = new ButtonType(msg("ui.common.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, msg("ui.menu.import_strategy_prompt"),
                replace, keep, createNew, cancel);
        dialog.setHeaderText(null);
        ButtonType selected = dialog.showAndWait().orElse(cancel);
        if (selected == replace) {
            return ImportConflictStrategy.REPLACE;
        }
        if (selected == keep) {
            return ImportConflictStrategy.KEEP_EXISTING;
        }
        if (selected == createNew) {
            return ImportConflictStrategy.CREATE_NEW_ID;
        }
        return null;
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}

