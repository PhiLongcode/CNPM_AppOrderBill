package com.giadinh.apporderbill;

import com.giadinh.apporderbill.billing.usecase.PrintReceiptUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintKitchenTicketUseCase;
import com.giadinh.apporderbill.menu.usecase.GetActiveMenuItemsUseCase;
import com.giadinh.apporderbill.menu.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.orders.usecase.*;
import com.giadinh.apporderbill.javafx.order.OrderItemViewModel;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import com.giadinh.apporderbill.javafx.order.OrderScreenView;
import com.giadinh.apporderbill.javafx.order.handlers.CheckoutHandler;
import com.giadinh.apporderbill.javafx.order.handlers.MenuItemHandler;
import com.giadinh.apporderbill.javafx.order.handlers.OrderItemHandler;
import com.giadinh.apporderbill.javafx.order.handlers.TableHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller cho màn hình Order Entry.
 * Implement Humble Object pattern - chỉ làm nhiệm vụ binding UI và delegate cho
 * Presenter.
 */
public class OrderScreenController implements OrderScreenView {

    // UI Components
    @FXML
    private TextField tableNumberField;
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemPriceField;
    @FXML
    private TextField searchField;
    @FXML
    private TextField quickQuantityField;
    @FXML
    private TextField discountField;
    @FXML
    private FlowPane tableContainer;
    @FXML
    private FlowPane menuItemsContainer;
    @FXML
    private HBox categoryTabsContainer;
    @FXML
    private Button addCustomItemButton;
    @FXML
    private Button printKitchenTicketButton;
    @FXML
    private Button printSelectedButton;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button printDraftReceiptButton;
    @FXML
    private TableView<OrderItemViewModel> orderItemsTable;
    @FXML
    private TableColumn<OrderItemViewModel, Boolean> selectColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Integer> itemNumberColumn;
    @FXML
    private TableColumn<OrderItemViewModel, String> nameColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderItemViewModel, String> unitColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Long> unitPriceColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Long> totalColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Double> discountColumn;
    @FXML
    private TableColumn<OrderItemViewModel, String> notesColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Boolean> printedColumn;
    @FXML
    private TableColumn<OrderItemViewModel, Void> actionColumn;
    @FXML
    private CheckMenuItem showSelectColumn;
    @FXML
    private CheckMenuItem showNumberColumn;
    @FXML
    private CheckMenuItem showNameColumn;
    @FXML
    private CheckMenuItem showQuantityColumn;
    @FXML
    private CheckMenuItem showUnitColumn;
    @FXML
    private CheckMenuItem showUnitPriceColumn;
    @FXML
    private CheckMenuItem showTotalColumn;
    @FXML
    private CheckMenuItem showDiscountColumn;
    @FXML
    private CheckMenuItem showNotesColumn;
    @FXML
    private CheckMenuItem showPrintedColumn;
    @FXML
    private CheckMenuItem showActionColumn;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label finalAmountLabel;
    @FXML
    private Label orderStatusLabel;
    @FXML
    private ToggleButton allCategoryButton;
    @FXML
    private ToggleButton allTablesFilterBtn;
    @FXML
    private ToggleButton inUseTablesFilterBtn;
    @FXML
    private ToggleButton emptyTablesFilterBtn;
    @FXML
    private Button addTableButton;
    @FXML
    private HBox itemActionButtonsContainer;
    @FXML
    private Button cancelItemButton;
    @FXML
    private Button deleteItemButton;

    // Data
    private OrderScreenPresenter presenter;
    private GetActiveMenuItemsUseCase getActiveMenuItemsUseCase;
    private AddMenuItemToOrderUseCase addMenuItemToOrderUseCase;
    private com.giadinh.apporderbill.menu.repository.MenuItemRepository menuItemRepository;

    // Table Use Cases
    private com.giadinh.apporderbill.table.usecase.AddTableUseCase addTableUseCase;
    private com.giadinh.apporderbill.table.usecase.DeleteTableUseCase deleteTableUseCase;
    private com.giadinh.apporderbill.table.usecase.ClearTableUseCase clearTableUseCase;
    private com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase getAllTablesUseCase;

    // Handlers
    private MenuItemHandler menuItemHandler;
    private TableHandler tableHandler;
    private OrderItemHandler orderItemHandler;
    private CheckoutHandler checkoutHandler;

    private ToggleGroup categoryGroup;
    private ToggleGroup tableFilterGroup;
    private Long currentOrderId;
    private Set<String> tablesInUse = new HashSet<>();

    @FXML
    public void initialize() {
        categoryGroup = new ToggleGroup();
        tableFilterGroup = new ToggleGroup();

        // Khởi tạo: disable 2 nút hủy/xóa khi chưa chọn món
        if (cancelItemButton != null) {
            cancelItemButton.setDisable(true);
        }
        if (deleteItemButton != null) {
            deleteItemButton.setDisable(true);
        }

        // Initialize handlers
        initializeHandlers();

        setupQuickQuantityField();
        setupColumnVisibilityFilters();
        setupKeyboardShortcuts();
    }

    private void setupColumnVisibilityFilters() {
        if (showSelectColumn != null) {
            selectColumn.visibleProperty().bind(showSelectColumn.selectedProperty());
        }
        if (showNumberColumn != null) {
            itemNumberColumn.visibleProperty().bind(showNumberColumn.selectedProperty());
        }
        if (showNameColumn != null) {
            nameColumn.visibleProperty().bind(showNameColumn.selectedProperty());
        }
        if (showQuantityColumn != null) {
            quantityColumn.visibleProperty().bind(showQuantityColumn.selectedProperty());
        }
        if (showUnitColumn != null) {
            unitColumn.visibleProperty().bind(showUnitColumn.selectedProperty());
        }
        if (showUnitPriceColumn != null) {
            unitPriceColumn.visibleProperty().bind(showUnitPriceColumn.selectedProperty());
        }
        if (showTotalColumn != null) {
            totalColumn.visibleProperty().bind(showTotalColumn.selectedProperty());
        }
        if (showDiscountColumn != null && discountColumn != null) {
            discountColumn.visibleProperty().bind(showDiscountColumn.selectedProperty());
        }
        if (showNotesColumn != null) {
            notesColumn.visibleProperty().bind(showNotesColumn.selectedProperty());
        }
        if (showPrintedColumn != null) {
            printedColumn.visibleProperty().bind(showPrintedColumn.selectedProperty());
        }
        if (showActionColumn != null) {
            actionColumn.visibleProperty().bind(showActionColumn.selectedProperty());
        }
    }

    private void initializeHandlers() {
        try {
            // OrderItem Handler
            if (orderItemsTable != null && selectColumn != null && itemNumberColumn != null &&
                    nameColumn != null && quantityColumn != null && unitColumn != null && unitPriceColumn != null &&
                    totalColumn != null && discountColumn != null && notesColumn != null && printedColumn != null && actionColumn != null) {

                orderItemHandler = new OrderItemHandler(
                        orderItemsTable, selectColumn, itemNumberColumn, nameColumn,
                        quantityColumn, unitColumn, unitPriceColumn, totalColumn, discountColumn, notesColumn,
                        printedColumn, actionColumn);
                orderItemHandler.setErrorHandler(this::showError);
                orderItemHandler.setOnRowSelectedCallback(this::onOrderItemRowSelected);
            } else {
                System.err.println("WARNING: Some TableView fields are null, OrderItemHandler not initialized");
            }

            // Checkout Handler
            if (totalAmountLabel != null && finalAmountLabel != null && discountField != null) {
                checkoutHandler = new CheckoutHandler(
                        totalAmountLabel, finalAmountLabel, discountField);
                checkoutHandler.setErrorHandler(this::showError);
                checkoutHandler.setOnCheckoutSuccessHandler(() -> {
                    clearItemInputFields();
                    currentOrderId = null;
                    refreshTablesInUse();
                    updateTableButtonStyles();
                    // Refresh menu items để cập nhật tồn kho sau thanh toán
                    refreshMenuItems();
                });
            } else {
                System.err.println("WARNING: Some checkout fields are null, CheckoutHandler not initialized");
            }

            // Menu Item Handler
            if (menuItemsContainer != null && searchField != null && categoryTabsContainer != null &&
                    categoryGroup != null) {

                menuItemHandler = new MenuItemHandler(
                        menuItemsContainer, searchField, categoryTabsContainer, categoryGroup);
                menuItemHandler.setErrorHandler(this::showError);
            } else {
                System.err.println("WARNING: Some menu fields are null, MenuItemHandler not initialized");
            } // Table Handler
            if (tableContainer != null && allTablesFilterBtn != null &&
                    inUseTablesFilterBtn != null && emptyTablesFilterBtn != null) {

                tableHandler = new TableHandler(
                        tableContainer, allTablesFilterBtn,
                        inUseTablesFilterBtn, emptyTablesFilterBtn);
                tableHandler.setErrorHandler(this::showError);
                tableHandler.setSuccessHandler(this::showSuccess);
                tableHandler.setTableSelectionHandler(this::handleTableSelected);
            } else {
                System.err.println("WARNING: Some table fields are null, TableHandler not initialized");
            }
        } catch (Exception e) {
            System.err.println("Error initializing handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupQuickQuantityField() {
        quickQuantityField.setText("1");
    }

    public void init(OpenOrCreateOrderUseCase openOrCreateOrderUseCase,
            AddCustomItemToOrderUseCase addCustomItemToOrderUseCase,
            AddMenuItemToOrderUseCase addMenuItemToOrderUseCase,
            GetOrderDetailsUseCase getOrderDetailsUseCase,
            CalculateOrderTotalUseCase calculateOrderTotalUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            PrintKitchenTicketUseCase printKitchenTicketUseCase,
            CheckoutOrderUseCase checkoutOrderUseCase,
            PrintReceiptUseCase printReceiptUseCase,
            GetActiveMenuItemsUseCase getActiveMenuItemsUseCase,
            UpdateOrderItemQuantityUseCase updateOrderItemQuantityUseCase,
            RemoveOrderItemUseCase removeOrderItemUseCase,
            com.giadinh.apporderbill.orders.usecase.UpdateOrderItemDiscountUseCase updateOrderItemDiscountUseCase) {
        this.getActiveMenuItemsUseCase = getActiveMenuItemsUseCase;
        this.addMenuItemToOrderUseCase = addMenuItemToOrderUseCase;

        // Presenter will be set by OrderPosApplication after repository is available
        this.presenter = null;

        loadMenuItems();
        initializeTables();
    }

    public void setPresenter(OrderScreenPresenter presenter) {
        this.presenter = presenter;

        // Set presenter cho các handlers
        if (orderItemHandler != null) {
            orderItemHandler.setPresenter(presenter);
        }
        if (checkoutHandler != null) {
            checkoutHandler.setPresenter(presenter);
        }
        if (menuItemHandler != null) {
            menuItemHandler.setPresenter(presenter);
        }
        if (tableHandler != null) {
            tableHandler.setPresenter(presenter);
        }

        // Refresh table status after presenter is set
        refreshTablesInUse();
        updateTableButtonStyles();

        // Setup keyboard shortcuts after scene is ready
        javafx.application.Platform.runLater(() -> {
            setupKeyboardShortcutsAfterSceneReady();
        });
    }

    public void setMenuItemRepository(
            com.giadinh.apporderbill.menu.repository.MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
        if (checkoutHandler != null) {
            checkoutHandler.setMenuItemRepository(menuItemRepository);
        }
    }

    public void setOrderRepository(
            com.giadinh.apporderbill.orders.repository.OrderRepository orderRepository) {
        if (checkoutHandler != null) {
            checkoutHandler.setOrderRepository(orderRepository);
        }
    }

    public void setAddTableUseCase(com.giadinh.apporderbill.table.usecase.AddTableUseCase addTableUseCase) {
        this.addTableUseCase = addTableUseCase;
        if (tableHandler != null) {
            tableHandler.setAddTableUseCase(addTableUseCase);
        }
    }

    public void setDeleteTableUseCase(
            com.giadinh.apporderbill.table.usecase.DeleteTableUseCase deleteTableUseCase) {
        this.deleteTableUseCase = deleteTableUseCase;
        if (tableHandler != null) {
            tableHandler.setDeleteTableUseCase(deleteTableUseCase);
        }
    }

    public void setClearTableUseCase(
            com.giadinh.apporderbill.table.usecase.ClearTableUseCase clearTableUseCase) {
        this.clearTableUseCase = clearTableUseCase;
        if (tableHandler != null) {
            tableHandler.setClearTableUseCase(clearTableUseCase);
        }
    }

    public void setGetAllTablesUseCase(
            com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase getAllTablesUseCase) {
        this.getAllTablesUseCase = getAllTablesUseCase;
        if (tableHandler != null) {
            tableHandler.setGetAllTablesUseCase(getAllTablesUseCase);
        }
        loadTablesFromDatabase();
    }

    /**
     * Refresh lại danh sách menu items (gọi từ MainLayoutController khi quay lại
     * màn hình hoặc sau khi thêm món để cập nhật tồn kho realtime)
     */
    @Override
    public void refreshMenuItems() {
        loadMenuItems();
    }

    @Override
    public void showQuickRestockDialog(MenuItemOutput menuItem) {
        if (menuItem == null || menuItem.getMenuItemId() == null) {
            showError("Không xác định được món để nhập kho.");
            return;
        }
        if (menuItemRepository == null) {
            showError("Chức năng nhập kho nhanh chưa sẵn sàng (thiếu MenuItemRepository).");
            return;
        }

        Long currentStock = menuItem.getStockQty();
        if (currentStock == null) {
            currentStock = 0L;
        }

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Nhập kho nhanh");
        dialog.setHeaderText("Nhập thêm hàng cho: " + menuItem.getName());
        dialog.setContentText("Tồn hiện tại (đã trừ các bàn đang order): " + currentStock
                + "\n\nSố lượng nhập thêm:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        try {
            long addQty = Long.parseLong(result.get().trim());
            if (addQty <= 0) {
                showError("Số lượng nhập thêm phải lớn hơn 0");
                return;
            }

            // Lấy lại MenuItem mới nhất từ DB để tránh lệch dữ liệu
            var menuItemOpt = menuItemRepository.findById(menuItem.getMenuItemId());
            if (menuItemOpt.isEmpty()) {
                showError("Không tìm thấy món trong hệ thống để nhập kho.");
                return;
            }

            var entity = menuItemOpt.get();
            long oldStock = entity.getStockQty();
            long newStock = oldStock + addQty;

            entity.updateStock(newStock);
            menuItemRepository.save(entity);

            showSuccess("Đã nhập thêm " + addQty + " vào kho cho '" + menuItem.getName()
                    + "'.\nTồn cũ: " + oldStock + " → Tồn mới: " + newStock);

            // Cập nhật lại menu trên màn Order
            refreshMenuItems();
        } catch (NumberFormatException e) {
            showError("Số lượng không hợp lệ");
        } catch (Exception e) {
            showError("Lỗi khi nhập kho: " + e.getMessage());
        }
    }

    private void loadMenuItems() {
        try {
            if (getActiveMenuItemsUseCase == null) {
                System.err.println("GetActiveMenuItemsUseCase chưa được khởi tạo!");
                return;
            }

            List<MenuItemOutput> items = getActiveMenuItemsUseCase.execute();
            System.out.println("Đã load " + items.size() + " món từ database");

            if (items.isEmpty()) {
                showError("Không có món nào trong menu. Vui lòng kiểm tra database.");
                return;
            }

            if (menuItemHandler != null) {
                menuItemHandler.setGetActiveMenuItemsUseCase(getActiveMenuItemsUseCase);
                menuItemHandler.setOnMenuItemClickHandler(this::onMenuItemClicked);
                menuItemHandler.loadMenuItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi khi tải menu: " + e.getMessage());
        }
    }

    private void onMenuItemClicked(MenuItemOutput menuItem) {
        if (presenter == null) {
            showError("Presenter chưa được khởi tạo");
            return;
        }

        try {
            int quantity = Integer.parseInt(quickQuantityField.getText().trim());
            if (quantity <= 0) {
                quantity = 1;
                quickQuantityField.setText("1");
            }

            presenter.onAddMenuItem(menuItem.getMenuItemId(), quantity);
            currentOrderId = presenter.getCurrentOrderId();
        } catch (NumberFormatException e) {
            showError("Số lượng không hợp lệ");
        } catch (Exception e) {
            showError("Lỗi khi thêm món: " + e.getMessage());
        }
    }

    private void initializeTables() {
        if (tableHandler != null) {
            tableHandler.initializeTables();
        }
    }

    private void loadTablesFromDatabase() {
        if (tableHandler != null) {
            tableHandler.loadTablesFromDatabase();
        }
    }

    private void refreshTablesInUse() {
        if (tableHandler != null && presenter != null) {
            Set<String> tablesInUse = presenter.getTablesInUse();
            tableHandler.updateTableButtonStyles(tablesInUse);
        }
    }

    private void updateTableButtonStyles() {
        refreshTablesInUse();
    }

    private void setupKeyboardShortcuts() {
        // Note: Scene might not be available in initialize(), so we'll set this up
        // after scene is shown
        // We'll call this from a Platform.runLater or from onTableSelected
    }

    public void setupKeyboardShortcutsAfterSceneReady() {
        if (searchField.getScene() == null) {
            return;
        }

        // F3: Focus vào search field
        searchField.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.F3) {
                searchField.requestFocus();
                event.consume();
            } else if (event.getCode() == javafx.scene.input.KeyCode.F9) {
                // F9: Thanh toán
                onCheckoutClick();
                event.consume();
            }
        });
    }

    // Handler callback method
    private void handleTableSelected(String tableNumber, Long orderId) {
        currentOrderId = orderId;
        updateOrderStatus("Đang phục vụ");
        refreshTablesInUse();
        updateTableButtonStyles();
    }

    private OrderItemViewModel selectedOrderItem;

    private void onOrderItemRowSelected(OrderItemViewModel item) {
        selectedOrderItem = item;
        if (item != null && itemActionButtonsContainer != null) {
            // Cập nhật trạng thái enable/disable của nút dựa trên món được chọn
            boolean canCancel = !item.isCanceled();
            boolean canDelete = !item.isPrintedToKitchen() && !item.isCanceled();
            
            if (cancelItemButton != null) {
                cancelItemButton.setDisable(!canCancel);
            }
            if (deleteItemButton != null) {
                deleteItemButton.setDisable(!canDelete);
            }
        } else {
            // Chưa chọn món - disable cả 2 nút
            if (cancelItemButton != null) {
                cancelItemButton.setDisable(true);
            }
            if (deleteItemButton != null) {
                deleteItemButton.setDisable(true);
            }
        }
    }

    @FXML
    protected void onCancelItemClick() {
        if (selectedOrderItem == null) {
            showError("Vui lòng chọn một món để hủy");
            return;
        }
        
        if (presenter == null) {
            showError("Hệ thống chưa sẵn sàng");
            return;
        }

        if (selectedOrderItem.isCanceled()) {
            showError("Món này đã bị hủy rồi");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận hủy món");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn HỦY món '" +
                selectedOrderItem.getName() + "'?\n(Món sẽ vẫn hiển thị nhưng bị gạch ngang)");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                presenter.cancelItem(selectedOrderItem.getOrderItemId());
                // Reset selection sau khi hủy
                selectedOrderItem = null;
                if (cancelItemButton != null) {
                    cancelItemButton.setDisable(true);
                }
            }
        });
    }

    @FXML
    protected void onDeleteItemClick() {
        if (selectedOrderItem == null) {
            showError("Vui lòng chọn một món để xóa");
            return;
        }
        
        if (presenter == null) {
            showError("Hệ thống chưa sẵn sàng");
            return;
        }

        if (selectedOrderItem.isPrintedToKitchen()) {
            showError("Không thể xóa món đã in phiếu bếp. Vui lòng dùng chức năng 'Hủy món'");
            return;
        }

        if (selectedOrderItem.isCanceled()) {
            showError("Món này đã bị hủy rồi");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn XÓA HOÀN TOÀN món '" +
                selectedOrderItem.getName() + "'?\n(Món chưa in phiếu bếp)");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                presenter.deleteItem(selectedOrderItem.getOrderItemId());
                // Reset selection sau khi xóa
                selectedOrderItem = null;
                if (deleteItemButton != null) {
                    deleteItemButton.setDisable(true);
                }
            }
        });
    }

    @FXML
    protected void onAddCustomItemClick() {
        if (presenter != null) {
            presenter.onAddCustomItem();
        }
    }

    @FXML
    protected void onAddTableClick() {
        if (tableHandler != null) {
            tableHandler.onAddTableClick();
        }
    }

    @FXML
    protected void onCategorySelected() {
        // Delegate to MenuItemHandler
        if (menuItemHandler != null) {
            menuItemHandler.onCategorySelected();
        }
    }

    @FXML
    protected void onPrintKitchenTicketClick() {
        if (checkoutHandler != null) {
            checkoutHandler.onPrintKitchenTicketClick();
        }
    }

    @FXML
    protected void onReprintKitchenTicketClick() {
        if (checkoutHandler != null) {
            checkoutHandler.onReprintKitchenTicketClick();
        }
    }

    @FXML
    protected void onReprintReceiptClick() {
        if (checkoutHandler != null) {
            checkoutHandler.onReprintReceiptClick();
        }
    }

    @FXML
    protected void onPrintSelectedItemsClick() {
        if (currentOrderId == null) {
            showError("Vui lòng chọn bàn trước");
            return;
        }

        if (orderItemHandler == null || checkoutHandler == null) {
            return;
        }

        // Lấy danh sách món đã chọn
        List<Long> selectedItemIds = orderItemHandler.getItemViewModels().stream()
                .filter(OrderItemViewModel::isSelected)
                .map(OrderItemViewModel::getOrderItemId)
                .collect(Collectors.toList());

        if (selectedItemIds.isEmpty()) {
            showError("Vui lòng chọn ít nhất một món để in");
            return;
        }

        checkoutHandler.onPrintSelectedItemsClick(selectedItemIds, () -> {
            orderItemHandler.getItemViewModels().forEach(item -> item.setSelected(false));
            orderItemsTable.refresh();
        });
    }

    @FXML
    protected void onCheckoutClick() {
        if (checkoutHandler != null && checkoutButton != null) {
            checkoutHandler.onCheckoutClick(checkoutButton.getScene().getWindow());
        }
    }

    @FXML
    protected void onPrintDraftReceiptClick() {
        if (checkoutHandler != null) {
            checkoutHandler.onPrintDraftReceiptClick();
        }
    }

    private void updateOrderStatus(String status) {
        orderStatusLabel.setText("(" + status + ")");
    }

    // ========== Implementation of OrderScreenView interface ==========

    @Override
    public void displayTableNumber(String tableNumber) {
        tableNumberField.setText(tableNumber);
    }

    @Override
    public void displayOrderItems(List<OrderItemViewModel> items) {
        if (orderItemHandler != null) {
            orderItemHandler.displayOrderItems(items);
        }
        if (checkoutHandler != null) {
            checkoutHandler.setCurrentOrderId(currentOrderId);
        }
    }

    @Override
    public void displayTotalAmount(long totalAmount) {
        if (checkoutHandler != null) {
            checkoutHandler.displayTotalAmount(totalAmount);
        }
    }

    @Override
    public void displayFinalAmount(long finalAmount) {
        if (checkoutHandler != null) {
            checkoutHandler.displayFinalAmount(finalAmount);
        }
    }

    @Override
    public void displayDiscountAmount(long discountAmount) {
        if (checkoutHandler != null) {
            checkoutHandler.displayDiscountAmount(discountAmount);
        }
    }

    @Override
    public void displayChangeAmount(long changeAmount) {
        if (checkoutHandler != null) {
            checkoutHandler.displayChangeAmount(changeAmount);
        }
    }

    @Override
    public String getDiscountAmount() {
        return checkoutHandler != null ? checkoutHandler.getDiscountAmount() : "0";
    }

    @Override
    public String getPaidAmount() {
        return checkoutHandler != null ? checkoutHandler.getPaidAmount() : "0";
    }

    @Override
    public String getPaymentMethod() {
        return checkoutHandler != null ? checkoutHandler.getPaymentMethod() : "CASH";
    }

    @Override
    public String getCustomerPhone() {
        return checkoutHandler != null ? checkoutHandler.getCustomerPhone() : "";
    }

    @Override
    public void clearItemInputFields() {
        itemNameField.clear();
        itemPriceField.clear();
        quickQuantityField.setText("1");
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Có, lưu hóa đơn", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Không, giữ để in lại", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    @Override
    public void setAddItemButtonEnabled(boolean enabled) {
        addCustomItemButton.setDisable(!enabled);
    }

    @Override
    public String getItemName() {
        return itemNameField.getText();
    }

    @Override
    public String getItemQuantity() {
        return quickQuantityField.getText();
    }

    @Override
    public String getItemPrice() {
        return itemPriceField.getText();
    }

    @Override
    public void showReprintReceiptDialog(
            List<com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput> payments,
            java.util.function.Consumer<Long> onSelect) {
        // Tạo dialog chọn hóa đơn
        Dialog<Long> dialog = new Dialog<>();
        dialog.setTitle("In lại hóa đơn");
        dialog.setHeaderText("Chọn hóa đơn cần in lại (Hôm nay: " + payments.size() + " hóa đơn)");
        dialog.initOwner(tableContainer.getScene().getWindow());

        // Tạo ListView hiển thị danh sách hóa đơn
        ListView<com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput> listView = new ListView<>();
        listView.setPrefHeight(300);
        listView.setPrefWidth(500);
        listView.getItems().addAll(payments);

        // Custom cell factory để hiển thị thông tin hóa đơn đẹp hơn
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(
                    com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String time = item.getPaidAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String method = switch (item.getPaymentMethod()) {
                        case "CASH" -> "Tiền mặt";
                        case "BANK_TRANSFER" -> "Chuyển khoản";
                        default -> item.getPaymentMethod();
                    };
                    setText(String.format("HD#%d | Bàn %s | %,d VNĐ | %s | %s",
                            item.getPaymentId(),
                            item.getTableNumber(),
                            item.getFinalAmount(),
                            method,
                            time));
                    setStyle("-fx-font-size: 13px;");
                }
            }
        });

        // Tạo VBox chứa ListView
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Nhấp đúp hoặc chọn và nhấn 'In lại' để in:"),
                listView);
        content.setPadding(new javafx.geometry.Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("🖨 In lại", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);

        // Xử lý double-click
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                var selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    dialog.setResult(selected.getPaymentId());
                    dialog.close();
                }
            }
        });

        // Converter cho result
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                var selected = listView.getSelectionModel().getSelectedItem();
                return selected != null ? selected.getPaymentId() : null;
            }
            return null;
        });

        // Show và xử lý kết quả
        Optional<Long> result = dialog.showAndWait();
        result.ifPresent(onSelect);
    }
}
