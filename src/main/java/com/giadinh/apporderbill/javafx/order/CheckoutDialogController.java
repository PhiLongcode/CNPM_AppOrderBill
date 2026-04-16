package com.giadinh.apporderbill.javafx.order;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.customer.model.LoyaltyConfig;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.List;
import java.util.Locale;
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
    @FXML private VBox customerInfoBox;
    @FXML private Label customerNameLabel;
    @FXML private Label customerPointsLabel;
    @FXML private VBox newCustomerBox;
    @FXML private TextField newCustomerNameField;
    @FXML private VBox redeemPointsBox;
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
    private Customer currentCustomer;

    public record Result(long paidAmount, long discountAmount, String paymentMethod,
                         Long customerId, String customerPhone, int pointsUsed) {
        // Backward-compat factory without customer
        public static Result of(long paid, long discount, String method) {
            return new Result(paid, discount, method, null, null, 0);
        }
    }

    @FXML
    private void initialize() {
        installNumericFormatter(discountField);
        installNumericFormatter(paidAmountField);
        if (customerPhoneField != null) {
            customerPhoneField.setOnAction(e -> onSearchCustomer());
        }
    }

    public void setCustomerUseCases(CustomerUseCases customerUseCases) {
        this.customerUseCases = customerUseCases;
        if (customerUseCases != null) {
            this.loyaltyConfig = customerUseCases.getLoyaltyConfig();
        }
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
        long discount = parseLong(discountField != null ? discountField.getText() : "0", 0L);
        long due = Math.max(0, subtotalAmount - discount);
        finalAmountLabel.setText(integerMoneyFormat.format(due));
        long paid = parseLong(paidAmountField != null ? paidAmountField.getText() : "0", 0L);
        long change = paid - due;
        changeAmountLabel.setText(integerMoneyFormat.format(Math.max(0, change)));
    }

    public void setOrderItems(List<OrderItemViewModel> items) {
        if (itemsTableView == null) {
            return;
        }
        configureItemColumns();
        itemsTableView.setItems(FXCollections.observableArrayList(items != null ? items : List.of()));
        itemsTableView.setSortPolicy(tv -> false);
        int n = items != null ? items.size() : 0;
        if (itemCountLabel != null) {
            itemCountLabel.setText(String.valueOf(n));
        }
        subtotalAmount = (items == null ? List.<OrderItemViewModel>of() : items).stream()
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
        long paid = parseLong(paidAmountField.getText(), 0L);
        String method = resolvePaymentMethod();
        Long id = currentCustomer != null ? currentCustomer.getId() : null;
        String phone = currentCustomer != null ? currentCustomer.getPhone() : null;
        int pointsUsed = parsePointsUsed();
        return new Result(paid, discount, method, id, phone, pointsUsed);
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
        int points = parsePointsUsed();
        points = Math.min(points, currentCustomer.getPoints()); // giới hạn theo số điểm có
        long discount = loyaltyConfig.calcRedeemDiscount(points);
        redeemDiscountLabel.setText("→ " + integerMoneyFormat.format(discount) + " VNĐ");
        // Cập nhật discountField để refreshFinalAndChange tự tính
        long baseDiscount = subtotalAmount; // giả sử tính lại từ ô giảm giá cũ
        long currentManualDiscount = parseLong(discountField != null ? discountField.getText() : "0", 0L);
        // Tách phần giảm giá điểm ra: ghi đè discount bằng tổng
        // Đơn giản nhất: lưu points discount riêng và cộng vào khi buildResult
    }

    private void showCustomerFound(Customer customer) {
        this.currentCustomer = customer;
        if (customerNameLabel != null) customerNameLabel.setText(customer.getName() != null ? customer.getName() : "");
        if (customerPointsLabel != null) customerPointsLabel.setText(customer.getPoints() + " điểm");
        setVisible(customerInfoBox, true);
        setVisible(newCustomerBox, false);
        setVisible(redeemPointsBox, customer.getPoints() > 0);
    }

    private void showCustomerNotFound(String phone) {
        this.currentCustomer = null;
        setVisible(customerInfoBox, false);
        setVisible(newCustomerBox, true);
        setVisible(redeemPointsBox, false);
    }

    private void setVisible(VBox box, boolean visible) {
        if (box != null) {
            box.setVisible(visible);
            box.setManaged(visible);
        }
    }

    private int parsePointsUsed() {
        if (redeemPointsField == null) return 0;
        int points = (int) parseLong(redeemPointsField.getText(), 0L);
        if (currentCustomer != null) {
            points = Math.min(points, currentCustomer.getPoints());
        }
        return Math.max(0, points);
    }
}
