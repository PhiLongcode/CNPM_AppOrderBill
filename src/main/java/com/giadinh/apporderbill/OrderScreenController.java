package com.giadinh.apporderbill;

import com.giadinh.apporderbill.billing.usecase.PrintReceiptUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintKitchenTicketUseCase;
import com.giadinh.apporderbill.catalog.usecase.GetActiveMenuItemsUseCase;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.orders.usecase.*;
import com.giadinh.apporderbill.javafx.order.OrderItemViewModel;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import com.giadinh.apporderbill.javafx.order.OrderScreenView;
import com.giadinh.apporderbill.javafx.order.handlers.CheckoutHandler;
import com.giadinh.apporderbill.javafx.order.handlers.MenuItemHandler;
import com.giadinh.apporderbill.javafx.order.handlers.OrderItemHandler;
import com.giadinh.apporderbill.javafx.order.handlers.TableHandler;
import com.giadinh.apporderbill.shared.error.DomainMessages;
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
    private ScrollPane menuItemsScrollPane;
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
    private com.giadinh.apporderbill.catalog.repository.MenuItemRepository menuItemRepository;

    // Table Use Cases
    private com.giadinh.apporderbill.table.usecase.AddTableUseCase addTableUseCase;
    private com.giadinh.apporderbill.table.usecase.DeleteTableUseCase deleteTableUseCase;
    private com.giadinh.apporderbill.table.usecase.ClearTableUseCase clearTableUseCase;
    private com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase getAllTablesUseCase;
    private com.giadinh.apporderbill.table.usecase.RenameTableUseCase renameTableUseCase;
    private com.giadinh.apporderbill.table.usecase.SetTableReservationUseCase setTableReservationUseCase;
    private com.giadinh.apporderbill.orders.usecase.TransferOrderBetweenTablesUseCase transferOrderBetweenTablesUseCase;
    private com.giadinh.apporderbill.orders.usecase.ReleaseEmptyActiveOrderUseCase releaseEmptyActiveOrderUseCase;
    private com.giadinh.apporderbill.orders.repository.OrderRepository orderRepository;

    // Handlers
    private MenuItemHandler menuItemHandler;
    private TableHandler tableHandler;
    private OrderItemHandler orderItemHandler;
    private CheckoutHandler checkoutHandler;

    private ToggleGroup categoryGroup;
    private ToggleGroup tableFilterGroup;
    private Long currentOrderId;
    private Set<String> tablesInUse = new HashSet<>();
    private boolean discountListenerInitialized;

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

        if (menuItemsScrollPane != null) {
            menuItemsScrollPane.setPannable(false);
        }

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

    @FXML
    private TableColumn<OrderItemViewModel, Double> discountAmountColumn;

    private void initializeHandlers() {
        try {
            // OrderItem Handler
            if (orderItemsTable != null && selectColumn != null && itemNumberColumn != null &&
                    nameColumn != null && quantityColumn != null && unitColumn != null && unitPriceColumn != null &&
                    totalColumn != null && discountColumn != null && discountAmountColumn != null && notesColumn != null && printedColumn != null && actionColumn != null) {

                orderItemHandler = new OrderItemHandler(
                        orderItemsTable, selectColumn, itemNumberColumn, nameColumn,
                        quantityColumn, unitColumn, unitPriceColumn, totalColumn, discountColumn, discountAmountColumn, notesColumn,
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
                    showSuccess(msg("ui.order.checkout_success"));
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
                tableHandler.setOnTablesChanged(this::refreshTableLayout);
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
        setupDiscountListener();

        // Refresh table status after presenter is set
        refreshTablesInUse();
        updateTableButtonStyles();

        // Setup keyboard shortcuts after scene is ready
        javafx.application.Platform.runLater(() -> {
            setupKeyboardShortcutsAfterSceneReady();
        });
    }

    private void setupDiscountListener() {
        if (discountListenerInitialized || discountField == null) {
            return;
        }
        discountListenerInitialized = true;
        discountField.textProperty().addListener((obs, oldV, newV) -> {
            if (presenter == null || presenter.getCurrentOrderId() == null) {
                return;
            }
            long discountAmount = parseDiscountAmount(newV);
            presenter.calculateTotalWithDiscount(discountAmount, null);
        });
    }

    private long parseDiscountAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(raw.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public void setMenuItemRepository(
            com.giadinh.apporderbill.catalog.repository.MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
        if (checkoutHandler != null) {
            checkoutHandler.setMenuItemRepository(menuItemRepository);
        }
    }

    public void setOrderRepository(
            com.giadinh.apporderbill.orders.repository.OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        if (checkoutHandler != null) {
            checkoutHandler.setOrderRepository(orderRepository);
        }
        if (tableHandler != null) {
            tableHandler.setOrderRepository(orderRepository);
        }
    }

    private void refreshTableLayout() {
        if (tableHandler != null) {
            tableHandler.loadTablesFromDatabase();
            refreshTablesInUse();
            updateTableButtonStyles();
        }
    }

    public void setTransferOrderBetweenTablesUseCase(
            com.giadinh.apporderbill.orders.usecase.TransferOrderBetweenTablesUseCase useCase) {
        this.transferOrderBetweenTablesUseCase = useCase;
        if (tableHandler != null) {
            tableHandler.setTransferOrderBetweenTablesUseCase(useCase);
        }
    }

    public void setReleaseEmptyActiveOrderUseCase(
            com.giadinh.apporderbill.orders.usecase.ReleaseEmptyActiveOrderUseCase useCase) {
        this.releaseEmptyActiveOrderUseCase = useCase;
        if (tableHandler != null) {
            tableHandler.setReleaseEmptyActiveOrderUseCase(useCase);
        }
    }

    public void setRenameTableUseCase(com.giadinh.apporderbill.table.usecase.RenameTableUseCase useCase) {
        this.renameTableUseCase = useCase;
        if (tableHandler != null) {
            tableHandler.setRenameTableUseCase(useCase);
        }
    }

    public void setSetTableReservationUseCase(
            com.giadinh.apporderbill.table.usecase.SetTableReservationUseCase useCase) {
        this.setTableReservationUseCase = useCase;
        if (tableHandler != null) {
            tableHandler.setSetTableReservationUseCase(useCase);
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
            showError(msg("ui.order.restock_item_unknown"));
            return;
        }
        if (menuItemRepository == null) {
            showError(msg("ui.order.restock_not_ready"));
            return;
        }

        Long currentStock = menuItem.getStockQty();
        if (currentStock == null) {
            currentStock = 0L;
        }

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(msg("ui.order.restock_dialog_title"));
        dialog.setHeaderText(msg("ui.order.restock_dialog_header", menuItem.getName()));
        dialog.setContentText(msg("ui.order.restock_dialog_content", currentStock));

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        try {
            long addQty = Long.parseLong(result.get().trim());
            if (addQty <= 0) {
                showError(msg("ui.order.restock_quantity_positive"));
                return;
            }

            // Lấy lại MenuItem mới nhất từ DB để tránh lệch dữ liệu
            var menuItemOpt = menuItemRepository.findById(menuItem.getMenuItemId());
            if (menuItemOpt.isEmpty()) {
                showError(msg("ui.order.restock_item_not_found"));
                return;
            }

            var entity = menuItemOpt.get();
            long oldStock = entity.getStockQty();
            long newStock = oldStock + addQty;

            entity.updateStock(newStock);
            menuItemRepository.save(entity);

            showSuccess(msg("ui.order.restock_success", addQty, menuItem.getName(), oldStock, newStock));

            // Cập nhật lại menu trên màn Order
            refreshMenuItems();
        } catch (NumberFormatException e) {
            showError(msg("ui.order.quantity_invalid"));
        } catch (Exception e) {
            showError(msg("ui.order.restock_failed", e.getMessage()));
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
                showError(msg("ui.order.menu_empty"));
                return;
            }

            if (menuItemHandler != null) {
                menuItemHandler.setGetActiveMenuItemsUseCase(getActiveMenuItemsUseCase);
                menuItemHandler.setOnMenuItemClickHandler(this::onMenuItemClicked);
                menuItemHandler.loadMenuItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(msg("ui.order.load_menu_failed", e.getMessage()));
        }
    }

    private void onMenuItemClicked(MenuItemOutput menuItem) {
        if (presenter == null) {
            showError(msg("ui.order.presenter_not_ready"));
            return;
        }
        if (presenter.getCurrentOrderId() == null) {
            showError(msg("ui.order.select_table_in_list_first"));
            return;
        }

        try {
            int quantity = Integer.parseInt(quickQuantityField.getText().trim());
            if (quantity <= 0) {
                quantity = 1;
                quickQuantityField.setText("1");
            }

            // Nếu đã có dòng cùng món và chưa in phiếu bếp thì cộng dồn số lượng vào dòng đó.
            // Món đã in phiếu bếp sẽ tạo dòng mới để tách rõ phần add-on.
            OrderItemViewModel unprintedSameItem = null;
            if (orderItemHandler != null) {
                unprintedSameItem = orderItemHandler.getItemViewModels().stream()
                        .filter(item -> !item.isCanceled())
                        .filter(item -> !item.isPrintedToKitchen())
                        .filter(item -> item.getName() != null && item.getName().equals(menuItem.getName()))
                        .filter(item -> item.getUnitPrice() == menuItem.getUnitPrice())
                        .findFirst()
                        .orElse(null);
            }

            if (unprintedSameItem != null) {
                presenter.updateItemQuantity(
                        unprintedSameItem.getOrderItemId(),
                        unprintedSameItem.getQuantity() + quantity);
            } else {
                presenter.onAddMenuItem(menuItem.getMenuItemId(), quantity);
            }
            currentOrderId = presenter.getCurrentOrderId();
        } catch (NumberFormatException e) {
            showError(msg("ui.order.quantity_invalid"));
        } catch (Exception e) {
            showError(msg("ui.order.add_item_failed", e.getMessage()));
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
        updateOrderStatus(msg("ui.order.status_serving"));
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
            showError(msg("ui.order.select_item_to_cancel"));
            return;
        }
        
        if (presenter == null) {
            showError(msg("ui.order.system_not_ready"));
            return;
        }

        if (selectedOrderItem.isCanceled()) {
            showError(msg("ui.order.item_already_canceled"));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(msg("ui.order.confirm_cancel_title"));
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(msg("ui.order.confirm_cancel_message", selectedOrderItem.getName()));
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
            showError(msg("ui.order.select_item_to_delete"));
            return;
        }
        
        if (presenter == null) {
            showError(msg("ui.order.system_not_ready"));
            return;
        }

        if (selectedOrderItem.isPrintedToKitchen()) {
            showError(msg("ui.order.cannot_delete_printed_item"));
            return;
        }

        if (selectedOrderItem.isCanceled()) {
            showError(msg("ui.order.item_already_canceled"));
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(msg("ui.order.confirm_delete_title"));
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(msg("ui.order.confirm_delete_message", selectedOrderItem.getName()));
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
            showError(msg("ui.order.select_table_first"));
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
            showError(msg("ui.order.select_items_first"));
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
        alert.setTitle(msg("ui.common.error_title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(msg("ui.common.success_title"));
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

        ButtonType yesButton = new ButtonType(msg("ui.order.confirm_yes_save_receipt"), ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType(msg("ui.order.confirm_no_keep_for_reprint"), ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
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
