package com.giadinh.apporderbill.javafx.menu;

import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    }

    @FXML private void onAddClick() { showInfo("Tính năng thêm món đang được hoàn thiện."); }
    @FXML private void onEditClick() { showInfo("Tính năng sửa món đang được hoàn thiện."); }
    @FXML private void onDeleteClick() {
        MenuItemRow selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Vui lòng chọn món cần xóa.");
            return;
        }
        allRows.remove(selected);
        applyFilters();
    }
    @FXML private void onToggleActiveClick() { showInfo("Tính năng bật/tắt món đang được hoàn thiện."); }
    @FXML private void onAddStockClick() { showInfo("Tính năng nhập kho đang được hoàn thiện."); }
    @FXML private void onRefreshClick() { reloadFromDatabase(); }
    @FXML private void onImportClick() { showInfo("Tính năng import Excel đang được hoàn thiện."); }
    @FXML private void onExportClick() { showInfo("Tính năng export Excel đang được hoàn thiện."); }
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
            boolean matchActive = !activeOnly || row.isActive();
            boolean isLowStock = row.isStockTracked() && row.getStockValue() != null && row.getStockMin() != null
                    && row.getStockValue() <= row.getStockMin();
            boolean matchStock = !lowStockOnly || isLowStock;
            return matchKeyword && matchCategory && matchActive && matchStock;
        }).collect(Collectors.toList()));
    }

    private void showInfo(String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}

