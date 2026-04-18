package com.giadinh.apporderbill.javafx.order;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.model.LoyaltyGift;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMenuItem;
import com.giadinh.apporderbill.customer.model.LoyaltyRedeemMode;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CheckoutDialogController {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label orderTitleLabel;
    @FXML
    private Label orderInfoLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label itemCountLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label finalAmountLabel;
    @FXML
    private Label vatRateLabel;
    @FXML
    private Label vatAmountLabel;
    @FXML
    private Label changeAmountLabel;
    @FXML
    private TextField discountField;
    @FXML
    private TextField paidAmountField;
    @FXML
    private ToggleGroup paymentMethodGroup;
    @FXML
    private ToggleButton cashButton;
    @FXML
    private ToggleButton transferButton;
    @FXML
    private ToggleButton cardButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TableView<OrderItemViewModel> itemsTableView;
    // Customer section
    @FXML private TextField customerPhoneField;
    @FXML private ListView<Customer> customerSuggestListView;
    @FXML private VBox customerInfoBox;
    @FXML private Label customerNameLabel;
    @FXML private Label customerPointsLabel;
    @FXML private VBox newCustomerBox;
    @FXML private TextField newCustomerNameField;
    @FXML private VBox redeemPointsBox;
    @FXML private Label loyaltyModeLabel;
    @FXML private ComboBox<LoyaltyRedeemMode> loyaltyModeCombo;
    @FXML private VBox loyaltyBillBox;
    @FXML private VBox loyaltyDishBox;
    @FXML private Label loyaltyDishLabel;
    @FXML private ComboBox<LoyaltyRedeemMenuItem> loyaltyDishCatalogCombo;
    @FXML private VBox loyaltyGiftBox;
    @FXML private Label loyaltyGiftLabel;
    @FXML private ComboBox<LoyaltyGift> loyaltyGiftCombo;
    @FXML private TextField redeemPointsField;
    @FXML private Label redeemDiscountLabel;
    @FXML
    private TableColumn<OrderItemViewModel, Integer> itemIndexColumn;
    @FXML
    private TableColumn<OrderItemViewModel, String> itemNameColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Long> itemPriceColumn;
    @FXML
    private TableColumn<OrderItemViewModel, String> itemDiscountPercentColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Long> itemDiscountAmountColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Long> itemTotalColumn;

    private final NumberFormat integerMoneyFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
    private long subtotalAmount;
    private BiConsumer<Long, Double> itemDiscountPercentUpdater;
    private Supplier<List<OrderItemViewModel>> orderItemsSupplier;
    private CustomerUseCases customerUseCases;
    private LoyaltyConfig loyaltyConfig = LoyaltyConfig.defaults();
    private double vatPercent;
    private Customer currentCustomer;
    private boolean suppressCustomerSuggestEvents;
    private int lastValidRedeemPoints;
    private boolean suppressRedeemEvent;

    public record Result(long paidAmount, long discountAmount, String paymentMethod,
                         Long customerId, String customerPhone, int pointsUsed,
                         LoyaltyRedeemMode loyaltyRedeemMode,
                         Long loyaltyRedeemCatalogId,
                         Long loyaltyGiftId) {
        public static Result of(long paid, long discount, String method) {
            return new Result(paid, discount, method, null, null, 0,
                    LoyaltyRedeemMode.NONE, null, null);
        }
    }

    @FXML
    private void initialize() {
        installNumericFormatter(discountField);
        installNumericFormatter(paidAmountField);
        installNumericFormatter(redeemPointsField);
        if (customerPhoneField != null) {
            customerPhoneField.setOnAction(e -> onSearchCustomer());
            customerPhoneField.textProperty().addListener((obs, oldV, newV) -> {
                if (suppressCustomerSuggestEvents) {
                    return;
                }
                String query = newV == null ? "" : newV.trim();
                String digits = query.replaceAll("\\D", "");
                if (digits.length() >= 4) {
                    showCustomerSuggestions(query);
                } else {
                    hideCustomerSuggestions();
                }
            });
        }
        if (customerSuggestListView != null) {
            customerSuggestListView.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName() + " - " + item.getPhone());
                }
            });
            customerSuggestListView.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 1) {
                    applySelectedCustomerFromSuggestion();
                }
            });
        }
        installLoyaltyModeCombo();
        setupPaymentMethodListener();
    }

    public void setCustomerUseCases(CustomerUseCases customerUseCases) {
        this.customerUseCases = customerUseCases;
        if (customerUseCases != null) {
            this.loyaltyConfig = customerUseCases.getLoyaltyConfig();
            this.vatPercent = Math.max(0.0, customerUseCases.getVatPercent());
        }
        refreshVatInfo();
        refreshLoyaltyCatalogs();
        applyLoyaltyModeUi();
    }

    public void initSummary(long totalAmount, long finalAmount, String tableInfo, String orderCode) {
        this.subtotalAmount = totalAmount;
        if (dateTimeLabel != null) {
            dateTimeLabel.setText(LocalDateTime.now().format(DATE_TIME_FMT));
        }
        if (orderTitleLabel != null) {
            if (orderCode != null && !orderCode.isBlank()) {
                orderTitleLabel.setText(DomainMessages.formatKey("ui.order.checkout_dlg_heading", orderCode));
            } else {
                orderTitleLabel.setText(DomainMessages.formatKey("ui.order.checkout_dialog_title"));
            }
        }
        totalAmountLabel.setText(integerMoneyFormat.format(totalAmount));
        finalAmountLabel.setText(integerMoneyFormat.format(finalAmount));
        changeAmountLabel.setText(integerMoneyFormat.format(0));
        orderInfoLabel.setText(tableInfo);
        long impliedDiscount = Math.max(0, totalAmount - finalAmount);
        discountField.setDisable(false);
        discountField.setEditable(true);
        discountField.setText(impliedDiscount > 0 ? String.valueOf(impliedDiscount) : "0");
        bindAmountListeners();
        refreshFinalAndChange();
        refreshPaidAmountAvailability();
    }

    private void installNumericFormatter(TextField field) {
        if (field == null) {
            return;
        }
        UnaryOperator<TextFormatter.Change> numericOnly = change -> {
            String next = change.getControlNewText();
            return next.matches("\\d*") ? change : null;
        };
        field.setTextFormatter(new TextFormatter<>(numericOnly));
        field.setDisable(false);
        field.setEditable(true);
    }

    /**
     * Gọi một lần từ {@linkplain #initSummary}; giữ tiền khách đưa / tiền thối khớp khi sửa giảm giá hoặc tiền mặt.
     */
    private void bindAmountListeners() {
        if (discountField == null || paidAmountField == null) {
            return;
        }
        discountField.textProperty().removeListener(this::onAmountFieldsChanged);
        paidAmountField.textProperty().removeListener(this::onAmountFieldsChanged);
        discountField.textProperty().addListener(this::onAmountFieldsChanged);
        paidAmountField.textProperty().addListener(this::onAmountFieldsChanged);
    }

    private void onAmountFieldsChanged(javafx.beans.value.ObservableValue<?> o, String oldV, String newV) {
        refreshFinalAndChange();
    }

    private void refreshFinalAndChange() {
        long net = computeNetBeforeVat();
        long vatAmount = computeVatAmount(net);
        long due = computeTotalDue();
        finalAmountLabel.setText(integerMoneyFormat.format(due));
        if (vatAmountLabel != null) {
            vatAmountLabel.setText(integerMoneyFormat.format(Math.max(0, vatAmount)));
        }
        long paid = isNonCashPayment()
                ? due
                : parseLong(paidAmountField != null ? paidAmountField.getText() : "0", 0L);
        long change = paid - due;
        changeAmountLabel.setText(integerMoneyFormat.format(Math.max(0, change)));
    }

    private long computeNetBeforeVat() {
        long discount = parseLong(discountField != null ? discountField.getText() : "0", 0L);
        return Math.max(0, subtotalAmount - discount);
    }

    private long computeVatAmount(long netBeforeVat) {
        return Math.round(netBeforeVat * (vatPercent / 100.0));
    }

    private long computeBillRedeemMoneyOff() {
        if (loyaltyModeCombo == null || currentCustomer == null || loyaltyConfig == null) {
            return 0L;
        }
        if (loyaltyModeCombo.getSelectionModel().getSelectedItem() != LoyaltyRedeemMode.BILL_DISCOUNT) {
            return 0L;
        }
        int pts = (int) parseLong(redeemPointsField != null ? redeemPointsField.getText() : "0", 0L);
        return loyaltyConfig.calcRedeemDiscount(pts);
    }

    private long computeTotalDue() {
        long net = computeNetBeforeVat();
        long vat = computeVatAmount(net);
        return Math.max(0, net + Math.max(0, vat) - computeBillRedeemMoneyOff());
    }

    private LoyaltyRedeemMode getSelectedLoyaltyMode() {
        if (loyaltyModeCombo == null) {
            return LoyaltyRedeemMode.NONE;
        }
        LoyaltyRedeemMode m = loyaltyModeCombo.getSelectionModel().getSelectedItem();
        return m != null ? m : LoyaltyRedeemMode.NONE;
    }

    private void installLoyaltyModeCombo() {
        if (loyaltyModeCombo == null) {
            return;
        }
        loyaltyModeCombo.getItems().setAll(
                LoyaltyRedeemMode.NONE,
                LoyaltyRedeemMode.BILL_DISCOUNT,
                LoyaltyRedeemMode.DISH,
                LoyaltyRedeemMode.GIFT_NON_MONETARY);
        loyaltyModeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(LoyaltyRedeemMode m) {
                if (m == null) {
                    return "";
                }
                return switch (m) {
                    case NONE -> DomainMessages.formatKey("ui.order.loyalty_mode_none");
                    case BILL_DISCOUNT -> DomainMessages.formatKey("ui.order.loyalty_mode_bill");
                    case DISH -> DomainMessages.formatKey("ui.order.loyalty_mode_dish");
                    case GIFT_NON_MONETARY -> DomainMessages.formatKey("ui.order.loyalty_mode_gift");
                };
            }

            @Override
            public LoyaltyRedeemMode fromString(String s) {
                return null;
            }
        });
        loyaltyModeCombo.getSelectionModel().select(LoyaltyRedeemMode.NONE);
    }

    private void refreshLoyaltyCatalogs() {
        if (customerUseCases == null) {
            return;
        }
        if (loyaltyDishCatalogCombo != null) {
            loyaltyDishCatalogCombo.setItems(FXCollections.observableArrayList(
                    customerUseCases.listActiveLoyaltyRedeemDishes()));
            loyaltyDishCatalogCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(LoyaltyRedeemMenuItem item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : formatDishCatalogRow(item));
                }
            });
            loyaltyDishCatalogCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(LoyaltyRedeemMenuItem item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : formatDishCatalogRow(item));
                }
            });
        }
        if (loyaltyGiftCombo != null) {
            loyaltyGiftCombo.setItems(FXCollections.observableArrayList(customerUseCases.listActiveLoyaltyGifts()));
            loyaltyGiftCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(LoyaltyGift item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : formatGiftRow(item));
                }
            });
            loyaltyGiftCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(LoyaltyGift item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : formatGiftRow(item));
                }
            });
        }
    }

    private String formatDishCatalogRow(LoyaltyRedeemMenuItem row) {
        return "#" + row.getId() + " · menu " + row.getMenuItemId()
                + " — " + row.getPointsCost() + " " + DomainMessages.formatKey("ui.order.points_suffix");
    }

    private String formatGiftRow(LoyaltyGift g) {
        String n = g.getName() != null ? g.getName() : ("#" + g.getId());
        return n + " — " + g.getPointsCost() + " " + DomainMessages.formatKey("ui.order.points_suffix");
    }

    @FXML
    private void onLoyaltyModeChanged() {
        applyLoyaltyModeUi();
        refreshFinalAndChange();
    }

    @FXML
    private void onLoyaltyCatalogChanged() {
        refreshFinalAndChange();
    }

    private void applyLoyaltyModeUi() {
        if (loyaltyModeCombo == null) {
            return;
        }
        LoyaltyRedeemMode m = getSelectedLoyaltyMode();
        setVisible(loyaltyBillBox, m == LoyaltyRedeemMode.BILL_DISCOUNT);
        setVisible(loyaltyDishBox, m == LoyaltyRedeemMode.DISH);
        setVisible(loyaltyGiftBox, m == LoyaltyRedeemMode.GIFT_NON_MONETARY);
        if (m != LoyaltyRedeemMode.BILL_DISCOUNT && redeemPointsField != null) {
            suppressRedeemEvent = true;
            redeemPointsField.clear();
            lastValidRedeemPoints = 0;
            suppressRedeemEvent = false;
        }
        if (redeemDiscountLabel != null && m != LoyaltyRedeemMode.BILL_DISCOUNT) {
            redeemDiscountLabel.setText("→ 0 VNĐ");
        }
        if (m == LoyaltyRedeemMode.BILL_DISCOUNT && currentCustomer != null) {
            updateRedeemAvailability(currentCustomer);
        } else if (redeemPointsField != null && m != LoyaltyRedeemMode.BILL_DISCOUNT) {
            redeemPointsField.setDisable(true);
            redeemPointsField.setEditable(false);
        }
    }

    public void setOrderItems(List<OrderItemViewModel> items) {
        if (itemsTableView == null) {
            return;
        }
        configureItemColumns();
        List<OrderItemViewModel> aggregatedItems = aggregateItemsForCheckout(items);
        itemsTableView.setItems(FXCollections.observableArrayList(aggregatedItems));
        itemsTableView.setSortPolicy(tv -> false);
        int n = aggregatedItems.size();
        if (itemCountLabel != null) {
            itemCountLabel.setText(String.valueOf(n));
        }
        subtotalAmount = aggregatedItems.stream()
                .mapToLong(OrderItemViewModel::getTotalPrice)
                .sum();
        totalAmountLabel.setText(integerMoneyFormat.format(subtotalAmount));
        refreshFinalAndChange();
    }

    public void setItemDiscountPercentUpdater(BiConsumer<Long, Double> updater) {
        this.itemDiscountPercentUpdater = updater;
    }

    public void setOrderItemsSupplier(Supplier<List<OrderItemViewModel>> supplier) {
        this.orderItemsSupplier = supplier;
    }

    private void configureItemColumns() {
        if (itemIndexColumn == null || itemIndexColumn.getCellValueFactory() != null) {
            return;
        }
        itemIndexColumn.setCellValueFactory(col -> {
            int idx = col.getTableView().getItems().indexOf(col.getValue());
            return new ReadOnlyObjectWrapper<>(idx >= 0 ? idx + 1 : null);
        });
        itemIndexColumn.setStyle("-fx-alignment: CENTER;");

        itemNameColumn.setCellValueFactory(col -> {
            OrderItemViewModel vm = col.getValue();
            if (vm == null) {
                return new ReadOnlyObjectWrapper<>(null);
            }
            String u = vm.getUnitName();
            String name = vm.getName() != null ? vm.getName() : "";
            if (u != null && !u.isBlank()) {
                return new ReadOnlyObjectWrapper<>(name + " (" + u.trim() + ")");
            }
            return new ReadOnlyObjectWrapper<>(name);
        });

        itemQuantityColumn.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(col.getValue() != null ? col.getValue().getQuantity() : null));
        itemQuantityColumn.setStyle("-fx-alignment: CENTER;");

        itemPriceColumn.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(col.getValue() != null ? col.getValue().getUnitPrice() : null));
        moneyCellFactory(itemPriceColumn);

        itemDiscountPercentColumn.setCellValueFactory(col -> {
            OrderItemViewModel vm = col.getValue();
            if (vm == null || vm.getDiscountPercent() == null) {
                return new ReadOnlyObjectWrapper<>("-");
            }
            if (vm.getDiscountPercent() <= 0) {
                return new ReadOnlyObjectWrapper<>("-");
            }
            String s = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(vm.getDiscountPercent());
            return new ReadOnlyObjectWrapper<>(s + "%");
        });
        itemDiscountPercentColumn.setStyle("-fx-alignment: CENTER;");
        itemDiscountPercentColumn.setEditable(true);
        itemDiscountPercentColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object == null ? "" : object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));
        itemDiscountPercentColumn.setOnEditCommit(event -> {
            OrderItemViewModel vm = event.getRowValue();
            if (vm == null || vm.getOrderItemId() == null) {
                return;
            }
            String raw = event.getNewValue() == null ? "" : event.getNewValue().trim();
            raw = raw.replace("%", "").replace(",", ".");
            double percent;
            try {
                percent = raw.isEmpty() ? 0.0 : Double.parseDouble(raw);
            } catch (Exception ex) {
                refreshRowsFromSource();
                return;
            }
            percent = Math.max(0.0, Math.min(100.0, percent));
            applyItemDiscount(vm.getOrderItemId(), percent);
        });

        itemDiscountAmountColumn.setCellValueFactory(col -> {
            OrderItemViewModel vm = col.getValue();
            if (vm == null) {
                return new ReadOnlyObjectWrapper<>(null);
            }
            long gross = (long) vm.getQuantity() * vm.getUnitPrice();
            long disc = Math.max(0, gross - vm.getTotalPrice());
            return new ReadOnlyObjectWrapper<>(disc);
        });
        moneyCellFactory(itemDiscountAmountColumn);
        itemDiscountAmountColumn.setEditable(true);
        itemDiscountAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Long object) {
                return object == null ? "" : String.valueOf(object);
            }

            @Override
            public Long fromString(String string) {
                if (string == null || string.isBlank()) {
                    return 0L;
                }
                return parseLong(string, 0L);
            }
        }));
        itemDiscountAmountColumn.setOnEditCommit(event -> {
            OrderItemViewModel vm = event.getRowValue();
            if (vm == null || vm.getOrderItemId() == null) {
                return;
            }
            long discountAmount = event.getNewValue() == null ? 0L : Math.max(0L, event.getNewValue());
            long gross = (long) vm.getQuantity() * vm.getUnitPrice();
            if (gross <= 0) {
                applyItemDiscount(vm.getOrderItemId(), 0.0);
                return;
            }
            discountAmount = Math.min(discountAmount, gross);
            double percent = (discountAmount * 100.0) / gross;
            applyItemDiscount(vm.getOrderItemId(), percent);
        });

        itemTotalColumn.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(col.getValue() != null ? col.getValue().getTotalPrice() : null));
        moneyCellFactory(itemTotalColumn);
        itemsTableView.setEditable(true);
    }

    private void applyItemDiscount(Long orderItemId, double percent) {
        if (itemDiscountPercentUpdater != null) {
            itemDiscountPercentUpdater.accept(orderItemId, percent);
        }
        refreshRowsFromSource();
    }

    private void refreshRowsFromSource() {
        if (orderItemsSupplier != null) {
            setOrderItems(orderItemsSupplier.get());
        } else {
            itemsTableView.refresh();
            refreshFinalAndChange();
        }
    }

    private void moneyCellFactory(TableColumn<OrderItemViewModel, Long> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Long amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(integerMoneyFormat.format(amount));
                }
            }
        });
        column.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    public Result buildResult() {
        long discount = parseLong(discountField.getText(), 0L);
        long due = computeTotalDue();
        long paid = isNonCashPayment() ? due : parseLong(paidAmountField.getText(), 0L);
        String method = resolvePaymentMethod();
        Long id = currentCustomer != null ? currentCustomer.getId() : null;
        String phone = currentCustomer != null ? currentCustomer.getPhone() : null;
        LoyaltyRedeemMode mode = getSelectedLoyaltyMode();
        int pointsUsed = parsePointsUsed();
        Long catalogId = null;
        Long giftId = null;
        if (mode == LoyaltyRedeemMode.DISH) {
            if (currentCustomer == null) {
                throw new IllegalArgumentException(DomainMessages.formatKey("error.LOYALTY_REDEEM_REQUIRES_CUSTOMER"));
            }
            LoyaltyRedeemMenuItem row = loyaltyDishCatalogCombo != null
                    ? loyaltyDishCatalogCombo.getSelectionModel().getSelectedItem()
                    : null;
            if (row == null) {
                throw new IllegalArgumentException(DomainMessages.formatKey("ui.order.loyalty_pick_dish"));
            }
            if (currentCustomer.getPoints() < row.getPointsCost()) {
                throw new IllegalArgumentException(
                        DomainMessages.formatKey("ui.order.redeem_points_insufficient_balance", currentCustomer.getPoints()));
            }
            catalogId = row.getId();
        } else if (mode == LoyaltyRedeemMode.GIFT_NON_MONETARY) {
            if (currentCustomer == null) {
                throw new IllegalArgumentException(DomainMessages.formatKey("error.LOYALTY_REDEEM_REQUIRES_CUSTOMER"));
            }
            LoyaltyGift gift = loyaltyGiftCombo != null ? loyaltyGiftCombo.getSelectionModel().getSelectedItem() : null;
            if (gift == null) {
                throw new IllegalArgumentException(DomainMessages.formatKey("ui.order.loyalty_pick_gift"));
            }
            if (currentCustomer.getPoints() < gift.getPointsCost()) {
                throw new IllegalArgumentException(
                        DomainMessages.formatKey("ui.order.redeem_points_insufficient_balance", currentCustomer.getPoints()));
            }
            giftId = gift.getId();
        }
        return new Result(paid, discount, method, id, phone, pointsUsed, mode, catalogId, giftId);
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    private long parseLong(String text, long defaultValue) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String resolvePaymentMethod() {
        if (paymentMethodGroup == null || paymentMethodGroup.getSelectedToggle() == null) {
            return "CASH";
        }
        if (paymentMethodGroup.getSelectedToggle() == cashButton) {
            return "CASH";
        }
        if (paymentMethodGroup.getSelectedToggle() == transferButton) {
            return "BANK_TRANSFER";
        }
        if (paymentMethodGroup.getSelectedToggle() == cardButton) {
            return "CARD";
        }
        return "CASH";
    }

    public void setInitialCustomer(Customer customer) {
        if (customer != null) {
            if (customerPhoneField != null) customerPhoneField.setText(customer.getPhone());
            showCustomerFound(customer);
        }
    }

    // ─── Customer section handlers ──────────────────────────────────────────

    @FXML
    private void onSearchCustomer() {
        if (customerUseCases == null || customerPhoneField == null) return;
        String phone = customerPhoneField.getText() == null ? "" : customerPhoneField.getText().trim();
        if (phone.isEmpty()) return;
        hideCustomerSuggestions();
        customerUseCases.findByPhone(phone).ifPresentOrElse(
                this::showCustomerFound,
                () -> showCustomerNotFound(phone));
    }

    @FXML
    private void onAddCustomer() {
        if (customerUseCases == null || customerPhoneField == null) return;
        String phone = customerPhoneField.getText() == null ? "" : customerPhoneField.getText().trim();
        String name = newCustomerNameField != null ? newCustomerNameField.getText() : "";
        try {
            Customer c = customerUseCases.createOrGet(phone, name);
            showCustomerFound(c);
        } catch (Exception e) {
            // nếu lỗi, ẩn hộp thêm mới
        }
    }

    @FXML
    private void onRedeemPointsChanged() {
        if (redeemDiscountLabel == null || loyaltyConfig == null || currentCustomer == null) return;
        if (suppressRedeemEvent) {
            return;
        }
        int availablePoints = Math.max(0, currentCustomer.getPoints());
        int points = (int) parseLong(redeemPointsField != null ? redeemPointsField.getText() : "0", 0L);
        if (points > availablePoints) {
            showError(DomainMessages.formatKey("ui.order.redeem_points_insufficient_balance", availablePoints));
            suppressRedeemEvent = true;
            redeemPointsField.setText(String.valueOf(lastValidRedeemPoints));
            suppressRedeemEvent = false;
            points = lastValidRedeemPoints;
        } else {
            points = Math.max(0, points);
            lastValidRedeemPoints = points;
        }
        long discount = loyaltyConfig.calcRedeemDiscount(points);
        redeemDiscountLabel.setText("→ " + integerMoneyFormat.format(discount) + " VNĐ");
        refreshFinalAndChange();
    }

    private void showCustomerFound(Customer customer) {
        this.currentCustomer = customer;
        if (customerNameLabel != null) customerNameLabel.setText(customer.getName() != null ? customer.getName() : "");
        if (customerPointsLabel != null) customerPointsLabel.setText(customer.getPoints() + " điểm");
        setVisible(customerInfoBox, true);
        setVisible(newCustomerBox, false);
        setVisible(redeemPointsBox, true);
        applyLoyaltyModeUi();
    }

    private void showCustomerNotFound(String phone) {
        this.currentCustomer = null;
        setVisible(customerInfoBox, false);
        setVisible(newCustomerBox, true);
        setVisible(redeemPointsBox, false);
        resetRedeemField();
    }

    private void showCustomerSuggestions(String query) {
        if (customerUseCases == null || customerSuggestListView == null) {
            return;
        }
        List<Customer> matches = customerUseCases.searchByPhonePrefixBTree(query);
        customerSuggestListView.getItems().setAll(matches);
        boolean visible = !matches.isEmpty();
        customerSuggestListView.setVisible(visible);
        customerSuggestListView.setManaged(visible);
    }

    private void hideCustomerSuggestions() {
        if (customerSuggestListView == null) {
            return;
        }
        customerSuggestListView.getItems().clear();
        customerSuggestListView.setVisible(false);
        customerSuggestListView.setManaged(false);
    }

    private void applySelectedCustomerFromSuggestion() {
        if (customerSuggestListView == null || customerPhoneField == null) {
            return;
        }
        Customer selected = customerSuggestListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        suppressCustomerSuggestEvents = true;
        customerPhoneField.setText(selected.getPhone());
        suppressCustomerSuggestEvents = false;
        hideCustomerSuggestions();
        showCustomerFound(selected);
    }

    private void setVisible(VBox box, boolean visible) {
        if (box != null) {
            box.setVisible(visible);
            box.setManaged(visible);
        }
    }

    private int parsePointsUsed() {
        if (getSelectedLoyaltyMode() != LoyaltyRedeemMode.BILL_DISCOUNT) {
            return 0;
        }
        if (redeemPointsField == null) return 0;
        int points = (int) parseLong(redeemPointsField.getText(), 0L);
        if (currentCustomer != null) {
            if (points > currentCustomer.getPoints()) {
                throw new IllegalArgumentException(
                        DomainMessages.formatKey("ui.order.redeem_points_insufficient_balance", currentCustomer.getPoints()));
            }
        }
        return Math.max(0, points);
    }

    private void setupPaymentMethodListener() {
        if (paymentMethodGroup == null) {
            return;
        }
        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            refreshPaidAmountAvailability();
            refreshFinalAndChange();
        });
    }

    private void refreshPaidAmountAvailability() {
        if (paidAmountField == null) {
            return;
        }
        boolean nonCash = isNonCashPayment();
        paidAmountField.setDisable(nonCash);
        paidAmountField.setEditable(!nonCash);
        if (nonCash) {
            paidAmountField.clear();
            paidAmountField.setPromptText(DomainMessages.formatKey("ui.order.checkout_paid_amount_not_required"));
        } else {
            paidAmountField.setPromptText(DomainMessages.formatKey("ui.order.checkout_paid_amount_required"));
        }
    }

    private boolean isNonCashPayment() {
        String method = resolvePaymentMethod();
        return "BANK_TRANSFER".equals(method) || "CARD".equals(method);
    }

    private void updateRedeemAvailability(Customer customer) {
        if (redeemPointsField == null || redeemDiscountLabel == null || customer == null) {
            return;
        }
        if (loyaltyModeCombo != null && getSelectedLoyaltyMode() != LoyaltyRedeemMode.BILL_DISCOUNT) {
            return;
        }
        int requiredPoints = loyaltyConfig != null ? Math.max(0, loyaltyConfig.getRedeemPointsRequired()) : 0;
        int availablePoints = Math.max(0, customer.getPoints());
        if (availablePoints < requiredPoints) {
            redeemPointsField.clear();
            redeemPointsField.setDisable(true);
            redeemPointsField.setEditable(false);
            redeemPointsField.setPromptText(DomainMessages.formatKey(
                    "ui.order.redeem_points_min_required",
                    requiredPoints));
            redeemDiscountLabel.setText("→ 0 VNĐ");
            showError(DomainMessages.formatKey("ui.order.redeem_points_not_enough_to_start", requiredPoints));
            lastValidRedeemPoints = 0;
            return;
        }
        redeemPointsField.setDisable(false);
        redeemPointsField.setEditable(true);
        redeemPointsField.setPromptText("0");
        lastValidRedeemPoints = 0;
    }

    private void resetRedeemField() {
        if (loyaltyModeCombo != null) {
            loyaltyModeCombo.getSelectionModel().select(LoyaltyRedeemMode.NONE);
        }
        if (redeemPointsField != null) {
            redeemPointsField.clear();
            redeemPointsField.setDisable(false);
            redeemPointsField.setEditable(true);
            redeemPointsField.setPromptText("0");
        }
        if (redeemDiscountLabel != null) {
            redeemDiscountLabel.setText("→ 0 VNĐ");
        }
        lastValidRedeemPoints = 0;
        applyLoyaltyModeUi();
    }

    private void refreshVatInfo() {
        if (vatRateLabel != null) {
            vatRateLabel.setText(String.format(Locale.US, "%.1f%%", Math.max(0.0, vatPercent)));
        }
        if (vatAmountLabel != null) {
            vatAmountLabel.setText(integerMoneyFormat.format(0));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(DomainMessages.formatKey("ui.common.error_title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<OrderItemViewModel> aggregateItemsForCheckout(List<OrderItemViewModel> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        Map<String, AggregatedCheckoutItem> grouped = new LinkedHashMap<>();
        for (OrderItemViewModel item : items) {
            if (item == null || item.isCanceled()) {
                continue;
            }
            String key = buildAggregationKey(item);
            AggregatedCheckoutItem bucket = grouped.computeIfAbsent(key, k ->
                    new AggregatedCheckoutItem(item.getName(), item.getUnitName(), item.getUnitPrice()));
            bucket.add(item);
        }
        return grouped.values().stream()
                .map(AggregatedCheckoutItem::toViewModel)
                .toList();
    }

    private String buildAggregationKey(OrderItemViewModel item) {
        String name = item.getName() == null ? "" : item.getName().trim();
        String unit = item.getUnitName() == null ? "" : item.getUnitName().trim();
        return name + "|" + unit + "|" + item.getUnitPrice();
    }

    private static final class AggregatedCheckoutItem {
        private final String name;
        private final String unitName;
        private final long unitPrice;
        private int quantity;
        private long totalPrice;
        private long grossAmount;

        private AggregatedCheckoutItem(String name, String unitName, long unitPrice) {
            this.name = name;
            this.unitName = unitName;
            this.unitPrice = unitPrice;
        }

        private void add(OrderItemViewModel item) {
            int itemQty = Math.max(0, item.getQuantity());
            quantity += itemQty;
            totalPrice += Math.max(0, item.getTotalPrice());
            grossAmount += Math.max(0, (long) itemQty * item.getUnitPrice());
        }

        private OrderItemViewModel toViewModel() {
            long discountAmount = Math.max(0L, grossAmount - totalPrice);
            double discountPercent = grossAmount <= 0
                    ? 0.0
                    : (discountAmount * 100.0) / grossAmount;
            return new OrderItemViewModel(
                    null,
                    name,
                    quantity,
                    unitPrice,
                    totalPrice,
                    null,
                    unitName,
                    true,
                    false,
                    discountPercent,
                    (double) discountAmount);
        }
    }
}
