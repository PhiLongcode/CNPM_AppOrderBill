package com.giadinh.apporderbill.javafx.menu;

import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemUnitDto;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho dialog thêm/sửa menu item (FXML version).
 */
public class MenuItemDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private Button browseImageBtn;
    @FXML
    private TextField priceField;
    @FXML
    private TextField costPriceField;
    @FXML
    private TextField skuField;
    @FXML
    private TextField baseUnitField;
    @FXML
    private CheckBox stockTrackedCheck;
    @FXML
    private TextField stockQtyField;
    @FXML
    private TextField stockMinField;
    @FXML
    private TextField stockMaxField;
    @FXML
    private VBox unitsContainer;
    @FXML
    private Button addUnitButton;

    private MenuManagementController.MenuItemRow existingItem;

    @FXML
    public void initialize() {
        // Chỉ set default values nếu chưa có existingItem (tức là đang thêm mới)
        // Nếu đang sửa, setExistingItem sẽ được gọi sau và sẽ set các giá trị
        if (existingItem == null) {
            // Default values cho item mới
            baseUnitField.setText("unit");
            stockQtyField.setText("0");
            stockMinField.setText("0");
            stockMaxField.setText("0");

            // Add first empty unit row (nếu có unitsContainer)
            if (unitsContainer != null) {
                addUnitRow(null, null, null, null, false);
            }
        }
    }

    /**
     * Chọn file hình ảnh
     */
    @FXML
    private void onBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh món ăn");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"),
                new FileChooser.ExtensionFilter("Tất cả", "*.*"));

        Window window = browseImageBtn.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            imageUrlField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Load dữ liệu từ item đang sửa
     */
    public void setExistingItem(MenuManagementController.MenuItemRow item) {
        this.existingItem = item;
        if (item == null) {
            return;
        }

        // Kiểm tra các field đã được inject chưa
        if (nameField == null || categoryField == null || priceField == null) {
            System.err.println("Warning: FXML fields not yet initialized when setExistingItem called");
            return;
        }

        // Basic info
        nameField.setText(item.getName() != null ? item.getName() : "");
        categoryField.setText(item.getCategory() != null ? item.getCategory() : "");
        priceField.setText(String.valueOf(item.getPriceValue()));

        // Image URL
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            imageUrlField.setText(item.getImageUrl());
        }

        // Cost price
        if (item.getCostValue() != null) {
            costPriceField.setText(String.valueOf(item.getCostValue()));
        }

        // SKU
        if (item.getSku() != null && !item.getSku().isEmpty()) {
            skuField.setText(item.getSku());
        }

        // Base unit
        if (item.getBaseUnit() != null && !item.getBaseUnit().isEmpty()) {
            baseUnitField.setText(item.getBaseUnit());
        }

        // Stock info
        stockTrackedCheck.setSelected(item.isStockTracked());
        if (item.getStockValue() != null) {
            stockQtyField.setText(String.valueOf(item.getStockValue()));
        }
        if (item.getStockMin() != null) {
            stockMinField.setText(String.valueOf(item.getStockMin()));
        }
        if (item.getStockMax() != null) {
            stockMaxField.setText(String.valueOf(item.getStockMax()));
        }

        // Units - clear and reload (nếu có unitsContainer)
        if (unitsContainer != null) {
            unitsContainer.getChildren().clear();
            if (item.getUnits() != null && !item.getUnits().isEmpty()) {
                for (var unit : item.getUnits()) {
                    addUnitRow(
                            unit.getUnitName(),
                            String.valueOf(unit.getRatioToBase()),
                            String.valueOf(unit.getSalePrice()),
                            unit.getSku(),
                            unit.isDefaultForSale());
                }
            } else {
                addUnitRow(null, null, null, null, false);
            }
        }
    }

    @FXML
    private void onAddUnit() {
        if (unitsContainer != null) {
            addUnitRow(null, null, null, null, false);
        }
    }

    private void addUnitRow(String unitName, String ratio, String price, String sku, boolean isDefault) {
        if (unitsContainer == null) {
            return;
        }

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("unit-row");
        row.setPadding(new Insets(4));

        TextField unitNameField = new TextField();
        unitNameField.setPrefWidth(100);
        unitNameField.setPromptText("Tên đơn vị");
        if (unitName != null)
            unitNameField.setText(unitName);

        TextField ratioField = new TextField();
        ratioField.setPrefWidth(50);
        ratioField.setPromptText("1");
        ratioField.setTooltip(new Tooltip("Số đơn vị cơ bản trong 1 đơn vị này"));
        if (ratio != null)
            ratioField.setText(ratio);

        TextField priceField = new TextField();
        priceField.setPrefWidth(80);
        priceField.setPromptText("Giá bán");
        if (price != null)
            priceField.setText(price);

        TextField skuField = new TextField();
        skuField.setPrefWidth(80);
        skuField.setPromptText("SKU");
        if (sku != null)
            skuField.setText(sku);

        CheckBox defaultCheck = new CheckBox();
        defaultCheck.setSelected(isDefault);
        defaultCheck.setTooltip(new Tooltip("Mặc định khi bán"));
        defaultCheck.setMinWidth(60);

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-unit-btn");
        removeBtn.setOnAction(e -> unitsContainer.getChildren().remove(row));

        row.getChildren().addAll(unitNameField, ratioField, priceField, skuField, defaultCheck, removeBtn);
        unitsContainer.getChildren().add(row);
    }

    /**
     * Validate và lấy kết quả từ form
     */
    public Result getResult() {
        try {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();

            if (name.isEmpty()) {
                showError(msg("ui.menu_item_dialog.name_required"));
                return null;
            }
            if (category.isEmpty()) {
                showError(msg("ui.menu_item_dialog.category_required"));
                return null;
            }

            long price = Long.parseLong(priceField.getText().trim().replace(",", ""));
            if (price <= 0) {
                showError(msg("ui.menu_item_dialog.price_positive"));
                return null;
            }

            Long costPrice = parseLongNullable(costPriceField.getText());
            String sku = skuField.getText().trim().isEmpty() ? null : skuField.getText().trim();
            String baseUnit = baseUnitField.getText().trim().isEmpty() ? null : baseUnitField.getText().trim();
            String imageUrl = imageUrlField.getText().trim().isEmpty() ? null : imageUrlField.getText().trim();
            Boolean stockTracked = stockTrackedCheck.isSelected();
            Long stockQty = parseLongNullable(stockQtyField.getText());
            Long stockMin = parseLongNullable(stockMinField.getText());
            Long stockMax = parseLongNullable(stockMaxField.getText());

            List<MenuItemUnitDto> units = buildUnitDtos(price);

            return new Result(name, category, price, costPrice, sku, baseUnit, imageUrl,
                    stockTracked, stockQty, stockMin, stockMax, units);
        } catch (NumberFormatException e) {
            showError(msg("ui.menu_item_dialog.price_invalid"));
            return null;
        }
    }

    private Long parseLongNullable(String text) {
        if (text == null || text.trim().isEmpty())
            return null;
        return Long.parseLong(text.trim().replace(",", ""));
    }

    private long parseLongWithDefault(String text, long defaultValue) {
        if (text == null || text.trim().isEmpty())
            return defaultValue;
        return Long.parseLong(text.trim().replace(",", ""));
    }

    private List<MenuItemUnitDto> buildUnitDtos(long basePrice) {
        List<MenuItemUnitDto> list = new ArrayList<>();
        if (unitsContainer == null) {
            return list;
        }
        for (var node : unitsContainer.getChildren()) {
            if (node instanceof HBox row) {
                TextField unitNameField = (TextField) row.getChildren().get(0);
                TextField ratioField = (TextField) row.getChildren().get(1);
                TextField priceField = (TextField) row.getChildren().get(2);
                TextField skuField = (TextField) row.getChildren().get(3);
                CheckBox defaultCheck = (CheckBox) row.getChildren().get(4);

                String uName = unitNameField.getText().trim();
                if (uName.isEmpty())
                    continue;

                long ratio = parseLongWithDefault(ratioField.getText(), 1);
                long uPrice = priceField.getText().trim().isEmpty()
                        ? basePrice * ratio
                        : parseLongWithDefault(priceField.getText(), basePrice * ratio);
                String uSku = skuField.getText().trim().isEmpty() ? null : skuField.getText().trim();
                boolean def = defaultCheck.isSelected();

                list.add(new MenuItemUnitDto(uName, ratio, uPrice, uSku, def));
            }
        }
        return list;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(msg("ui.common.error_title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Tạo và hiển thị dialog
     */
    public static Result showDialog(MenuManagementController.MenuItemRow existingItem) {
        try {
            var fxmlUrl = MenuItemDialogController.class.getResource(
                    "/com/giadinh/apporderbill/javafx/menu/menu-item-dialog.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException("menu-item-dialog.fxml not found");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            ClassLoader cl = MenuItemDialogController.class.getClassLoader();
            if (cl != null) {
                loader.setClassLoader(cl);
            }
            DialogPane dialogPane = loader.load();
            MenuItemDialogController controller = loader.getController();

            Dialog<Result> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(existingItem == null ? controller.msg("ui.menu_item_dialog.add_title")
                    : controller.msg("ui.menu_item_dialog.edit_title"));
            dialog.setHeaderText(existingItem == null ? controller.msg("ui.menu_item_dialog.add_header")
                    : controller.msg("ui.menu_item_dialog.edit_header"));

            // Set existingItem trước để initialize() biết là đang edit
            if (existingItem != null) {
                controller.existingItem = existingItem;
            }

            // Load existing data if editing - gọi sau khi initialize() đã hoàn tất
            // initialize() sẽ được gọi tự động bởi JavaFX sau khi load FXML
            // Vì vậy chúng ta cần gọi setExistingItem sau khi dialog được set
            // Sử dụng Platform.runLater để đảm bảo initialize() đã chạy xong
            if (existingItem != null) {
                javafx.application.Platform.runLater(() -> {
                    controller.setExistingItem(existingItem);
                });
            }

            // Handle result
            dialog.setResultConverter(buttonType -> {
                if (buttonType != null && buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return controller.getResult();
                }
                return null;
            });

            return dialog.showAndWait().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DomainMessages.formatKey("ui.common.error_title"));
            alert.setContentText(DomainMessages.formatKey("ui.menu_item_dialog.open_failed", e.getMessage()));
            alert.showAndWait();
            return null;
        }
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }

    /**
     * Result class - giữ nguyên như cũ
     */
    public static class Result {
        private final String name;
        private final String category;
        private final long unitPrice;
        private final Long costPrice;
        private final String sku;
        private final String baseUnit;
        private final String imageUrl;
        private final Boolean stockTracked;
        private final Long stockQty;
        private final Long stockMin;
        private final Long stockMax;
        private final List<MenuItemUnitDto> units;

        public Result(String name, String category, long unitPrice,
                Long costPrice, String sku, String baseUnit, String imageUrl,
                Boolean stockTracked, Long stockQty, Long stockMin, Long stockMax,
                List<MenuItemUnitDto> units) {
            this.name = name;
            this.category = category;
            this.unitPrice = unitPrice;
            this.costPrice = costPrice;
            this.sku = sku;
            this.baseUnit = baseUnit;
            this.imageUrl = imageUrl;
            this.stockTracked = stockTracked;
            this.stockQty = stockQty;
            this.stockMin = stockMin;
            this.stockMax = stockMax;
            this.units = units;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public long getUnitPrice() {
            return unitPrice;
        }

        public Long getCostPrice() {
            return costPrice;
        }

        public String getSku() {
            return sku;
        }

        public String getBaseUnit() {
            return baseUnit;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Boolean getStockTracked() {
            return stockTracked;
        }

        public Long getStockQty() {
            return stockQty;
        }

        public Long getStockMin() {
            return stockMin;
        }

        public Long getStockMax() {
            return stockMax;
        }

        public List<MenuItemUnitDto> getUnits() {
            return units;
        }
    }
}
