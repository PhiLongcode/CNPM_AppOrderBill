package com.giadinh.apporderbill.javafx.settings;

import com.giadinh.apporderbill.catalog.usecase.GetAllMenuItemsUseCase;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.model.LoyaltyGift;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.StringConverter;

public class LoyaltyConfigTabController {

    @FXML private TextField earnUnitAmountField;
    @FXML private TextField pointsPerUnitField;
    @FXML private TextField redeemPointsRequiredField;
    @FXML private TextField redeemValueField;
    @FXML private Label earnUnitInfoLabel;
    @FXML private Label pointsPerUnitInfoLabel;
    @FXML private Label redeemPointsInfoLabel;
    @FXML private Label redeemValueInfoLabel;

    @FXML private Label redeemDishSectionTitle;
    @FXML private TableView<LoyaltyRedeemMenuItem> redeemDishesTable;
    @FXML private TableColumn<LoyaltyRedeemMenuItem, Long> dishColId;
    @FXML private TableColumn<LoyaltyRedeemMenuItem, Long> dishColMenuId;
    @FXML private TableColumn<LoyaltyRedeemMenuItem, Long> dishColPoints;
    @FXML private TableColumn<LoyaltyRedeemMenuItem, String> dishColActive;
    @FXML private ComboBox<MenuItemOutput> dishMenuCombo;
    @FXML private TextField dishCatalogPointsField;
    @FXML private CheckBox dishCatalogActiveCheck;
    @FXML private Button dishNewButton;
    @FXML private Button dishSaveButton;
    @FXML private Button dishDeleteButton;

    @FXML private Label redeemGiftSectionTitle;
    @FXML private TableView<LoyaltyGift> redeemGiftsTable;
    @FXML private TableColumn<LoyaltyGift, Long> giftColId;
    @FXML private TableColumn<LoyaltyGift, String> giftColName;
    @FXML private TableColumn<LoyaltyGift, Long> giftColPoints;
    @FXML private TableColumn<LoyaltyGift, String> giftColActive;
    @FXML private TextField giftNameField;
    @FXML private TextField giftPointsField;
    @FXML private CheckBox giftActiveCheck;
    @FXML private Button giftNewButton;
    @FXML private Button giftSaveButton;
    @FXML private Button giftDeleteButton;

    private CustomerUseCases useCases;
    private GetAllMenuItemsUseCase getAllMenuItemsUseCase;

    @FXML
    public void initialize() {
        initHints();
        setupDishTableColumns();
        setupGiftTableColumns();
        installMenuComboConverter();
        if (redeemDishesTable != null) {
            redeemDishesTable.getSelectionModel().selectedItemProperty().addListener((obs, prev, row) -> {
                if (row == null) {
                    clearDishForm();
                } else {
                    applyDishRowToForm(row);
                }
            });
        }
        if (redeemGiftsTable != null) {
            redeemGiftsTable.getSelectionModel().selectedItemProperty().addListener((obs, prev, row) -> {
                if (row == null) {
                    clearGiftForm();
                } else {
                    applyGiftRowToForm(row);
                }
            });
        }
    }

    public void setUseCases(CustomerUseCases useCases) {
        this.useCases = useCases;
        loadFromUseCases();
        refreshCatalogTables();
        applyCatalogAvailability();
        reloadMenuItemsCombo();
    }

    public void setGetAllMenuItemsUseCase(GetAllMenuItemsUseCase getAllMenuItemsUseCase) {
        this.getAllMenuItemsUseCase = getAllMenuItemsUseCase;
        reloadMenuItemsCombo();
    }

    @FXML
    private void onSave() {
        if (useCases == null) {
            return;
        }
        try {
            LoyaltyConfig config = new LoyaltyConfig(
                    parsePositiveLong(earnUnitAmountField, "ui.customer.loyalty_earn_unit_invalid"),
                    parsePositiveInt(pointsPerUnitField, "ui.customer.loyalty_points_per_unit_invalid"),
                    parsePositiveInt(redeemPointsRequiredField, "ui.customer.loyalty_redeem_points_invalid"),
                    parsePositiveLong(redeemValueField, "ui.customer.loyalty_redeem_value_invalid"));
            useCases.updateLoyaltyConfig(config);
            showInfo(msg("ui.settings.loyalty_saved"));
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    @FXML
    private void onRedeemDishNew() {
        if (redeemDishesTable != null) {
            redeemDishesTable.getSelectionModel().clearSelection();
        }
        clearDishForm();
    }

    @FXML
    private void onRedeemDishSave() {
        if (!ensureCatalogReady()) {
            return;
        }
        try {
            MenuItemOutput menu = dishMenuCombo != null ? dishMenuCombo.getSelectionModel().getSelectedItem() : null;
            if (menu == null || menu.getMenuItemId() == null) {
                throw new IllegalArgumentException(msg("ui.settings.loyalty_catalog_pick_menu"));
            }
            int pts = parseNonNegativeInt(dishCatalogPointsField, "ui.settings.loyalty_catalog_points_invalid");
            if (pts <= 0) {
                throw new IllegalArgumentException(msg("ui.settings.loyalty_catalog_points_invalid"));
            }
            boolean active = dishCatalogActiveCheck == null || dishCatalogActiveCheck.isSelected();
            LoyaltyRedeemMenuItem selected = redeemDishesTable != null
                    ? redeemDishesTable.getSelectionModel().getSelectedItem()
                    : null;
            Long id = selected != null ? selected.getId() : null;
            LoyaltyRedeemMenuItem row = new LoyaltyRedeemMenuItem(id, menu.getMenuItemId().intValue(), pts, active);
            useCases.saveLoyaltyRedeemMenuCatalogRow(row);
            refreshCatalogTables();
            showInfo(msg("ui.settings.loyalty_catalog_dish_saved"));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
        }
    }

    @FXML
    private void onRedeemDishDelete() {
        if (!ensureCatalogReady() || redeemDishesTable == null) {
            return;
        }
        LoyaltyRedeemMenuItem selected = redeemDishesTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            showError(msg("ui.settings.loyalty_catalog_select_row"));
            return;
        }
        try {
            useCases.deleteLoyaltyRedeemMenuCatalogRow(selected.getId());
            onRedeemDishNew();
            refreshCatalogTables();
            showInfo(msg("ui.settings.loyalty_catalog_dish_deleted"));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
        }
    }

    @FXML
    private void onGiftNew() {
        if (redeemGiftsTable != null) {
            redeemGiftsTable.getSelectionModel().clearSelection();
        }
        clearGiftForm();
    }

    @FXML
    private void onGiftSave() {
        if (!ensureCatalogReady()) {
            return;
        }
        try {
            String name = giftNameField != null ? giftNameField.getText() : "";
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(msg("ui.settings.loyalty_catalog_gift_name_required"));
            }
            int pts = parseNonNegativeInt(giftPointsField, "ui.settings.loyalty_catalog_points_invalid");
            if (pts <= 0) {
                throw new IllegalArgumentException(msg("ui.settings.loyalty_catalog_points_invalid"));
            }
            boolean active = giftActiveCheck == null || giftActiveCheck.isSelected();
            LoyaltyGift selected = redeemGiftsTable != null ? redeemGiftsTable.getSelectionModel().getSelectedItem() : null;
            Long id = selected != null ? selected.getId() : null;
            LoyaltyGift gift = new LoyaltyGift(id, name.trim(), pts, active);
            useCases.saveLoyaltyGiftCatalogRow(gift);
            refreshCatalogTables();
            showInfo(msg("ui.settings.loyalty_catalog_gift_saved"));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
        }
    }

    @FXML
    private void onGiftDelete() {
        if (!ensureCatalogReady() || redeemGiftsTable == null) {
            return;
        }
        LoyaltyGift selected = redeemGiftsTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            showError(msg("ui.settings.loyalty_catalog_select_row"));
            return;
        }
        try {
            useCases.deleteLoyaltyGiftCatalogRow(selected.getId());
            onGiftNew();
            refreshCatalogTables();
            showInfo(msg("ui.settings.loyalty_catalog_gift_deleted"));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
        }
    }

    private boolean ensureCatalogReady() {
        if (useCases == null) {
            showError(msg("ui.settings.loyalty_catalog_usecases_missing"));
            return false;
        }
        if (!useCases.isLoyaltyCatalogPersistenceAvailable()) {
            showError(msg("ui.settings.loyalty_catalog_db_unavailable"));
            return false;
        }
        return true;
    }

    private void loadFromUseCases() {
        if (useCases == null || earnUnitAmountField == null) {
            return;
        }
        LoyaltyConfig config = useCases.reloadLoyaltyConfig();
        earnUnitAmountField.setText(String.valueOf(config.getEarnUnitAmount()));
        pointsPerUnitField.setText(String.valueOf(config.getPointsPerUnit()));
        redeemPointsRequiredField.setText(String.valueOf(config.getRedeemPointsRequired()));
        redeemValueField.setText(String.valueOf(config.getRedeemValue()));
    }

    private void refreshCatalogTables() {
        if (useCases == null) {
            return;
        }
        if (redeemDishesTable != null) {
            redeemDishesTable.setItems(FXCollections.observableArrayList(useCases.listAllLoyaltyRedeemMenuCatalog()));
        }
        if (redeemGiftsTable != null) {
            redeemGiftsTable.setItems(FXCollections.observableArrayList(useCases.listAllLoyaltyGiftsCatalog()));
        }
    }

    private void applyDishRowToForm(LoyaltyRedeemMenuItem row) {
        if (dishCatalogPointsField != null) {
            dishCatalogPointsField.setText(String.valueOf(row.getPointsCost()));
        }
        if (dishCatalogActiveCheck != null) {
            dishCatalogActiveCheck.setSelected(row.isActive());
        }
        selectMenuByItemId(row.getMenuItemId());
    }

    private void clearDishForm() {
        if (dishMenuCombo != null) {
            dishMenuCombo.getSelectionModel().clearSelection();
        }
        if (dishCatalogPointsField != null) {
            dishCatalogPointsField.clear();
        }
        if (dishCatalogActiveCheck != null) {
            dishCatalogActiveCheck.setSelected(true);
        }
    }

    private void applyGiftRowToForm(LoyaltyGift row) {
        if (giftNameField != null) {
            giftNameField.setText(row.getName() != null ? row.getName() : "");
        }
        if (giftPointsField != null) {
            giftPointsField.setText(String.valueOf(row.getPointsCost()));
        }
        if (giftActiveCheck != null) {
            giftActiveCheck.setSelected(row.isActive());
        }
    }

    private void clearGiftForm() {
        if (giftNameField != null) {
            giftNameField.clear();
        }
        if (giftPointsField != null) {
            giftPointsField.clear();
        }
        if (giftActiveCheck != null) {
            giftActiveCheck.setSelected(true);
        }
    }

    private void selectMenuByItemId(int menuItemId) {
        if (dishMenuCombo == null || dishMenuCombo.getItems() == null) {
            return;
        }
        for (MenuItemOutput m : dishMenuCombo.getItems()) {
            if (m.getMenuItemId() != null && m.getMenuItemId().intValue() == menuItemId) {
                dishMenuCombo.getSelectionModel().select(m);
                return;
            }
        }
        dishMenuCombo.getSelectionModel().clearSelection();
    }

    private void reloadMenuItemsCombo() {
        if (dishMenuCombo == null || getAllMenuItemsUseCase == null) {
            return;
        }
        dishMenuCombo.setItems(FXCollections.observableArrayList(getAllMenuItemsUseCase.execute()));
    }

    private void installMenuComboConverter() {
        if (dishMenuCombo == null) {
            return;
        }
        dishMenuCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(MenuItemOutput o) {
                if (o == null) {
                    return "";
                }
                return o.getName() + " (#" + o.getMenuItemId() + ")";
            }

            @Override
            public MenuItemOutput fromString(String s) {
                return null;
            }
        });
    }

    private void setupDishTableColumns() {
        if (dishColId == null) {
            return;
        }
        dishColId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        dishColMenuId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>((long) c.getValue().getMenuItemId()));
        dishColPoints.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>((long) c.getValue().getPointsCost()));
        dishColActive.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().isActive() ? msg("ui.common.yes") : msg("ui.common.no")));
    }

    private void setupGiftTableColumns() {
        if (giftColId == null) {
            return;
        }
        giftColId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        giftColName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getName() != null ? c.getValue().getName() : ""));
        giftColPoints.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>((long) c.getValue().getPointsCost()));
        giftColActive.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().isActive() ? msg("ui.common.yes") : msg("ui.common.no")));
    }

    private void applyCatalogAvailability() {
        boolean ok = useCases != null && useCases.isLoyaltyCatalogPersistenceAvailable();
        setDisableDeep(redeemDishSectionTitle, !ok);
        setDisableDeep(redeemGiftSectionTitle, !ok);
        setDisableDeep(redeemDishesTable, !ok);
        setDisableDeep(redeemGiftsTable, !ok);
        setDisableDeep(dishMenuCombo, !ok);
        setDisableDeep(dishCatalogPointsField, !ok);
        setDisableDeep(dishCatalogActiveCheck, !ok);
        setDisableDeep(dishNewButton, !ok);
        setDisableDeep(dishSaveButton, !ok);
        setDisableDeep(dishDeleteButton, !ok);
        setDisableDeep(giftNameField, !ok);
        setDisableDeep(giftPointsField, !ok);
        setDisableDeep(giftActiveCheck, !ok);
        setDisableDeep(giftNewButton, !ok);
        setDisableDeep(giftSaveButton, !ok);
        setDisableDeep(giftDeleteButton, !ok);
    }

    private static void setDisableDeep(Object node, boolean disabled) {
        if (node instanceof javafx.scene.Node n) {
            n.setDisable(disabled);
        }
    }

    private void initHints() {
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

    private void showInfo(String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }

    private void showError(String text) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
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

    private int parseNonNegativeInt(TextField field, String errorMessageKey) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
    }
}
