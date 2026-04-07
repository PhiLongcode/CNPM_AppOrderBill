package com.giadinh.apporderbill;

import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.repository.PrintTemplateRepository;
import com.giadinh.apporderbill.kitchen.repository.SqliteKitchenTicketRepository;
import com.giadinh.apporderbill.kitchen.repository.KitchenTicketRepository;
import com.giadinh.apporderbill.billing.repository.SqlitePaymentRepository;
import com.giadinh.apporderbill.billing.repository.PaymentRepository;
import com.giadinh.apporderbill.menu.repository.SqliteMenuItemRepository;
import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.orders.repository.SqliteOrderRepository;
import com.giadinh.apporderbill.orders.repository.OrderRepository;

import com.giadinh.apporderbill.shared.util.*;
import com.giadinh.apporderbill.shared.service.PrinterService;
import com.giadinh.apporderbill.billing.usecase.PrintReceiptUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintKitchenTicketUseCase;
import com.giadinh.apporderbill.kitchen.usecase.PrintSelectedItemsUseCase;
import com.giadinh.apporderbill.menu.usecase.*;
import com.giadinh.apporderbill.menu.service.ExcelService;
import com.giadinh.apporderbill.orders.usecase.*;
import com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetDailyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetMonthlyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetRevenueByDateRangeUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetWeeklyRevenueUseCase;
import com.giadinh.apporderbill.shared.service.SimplePrinterService;
import com.giadinh.apporderbill.javafx.order.OrderScreenPresenter;
import com.giadinh.apporderbill.shared.util.DataModeConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;

public class OrderPosApplication extends Application {
        @Override
        public void start(Stage stage) {
                try {
                        FXMLLoader loginLoader = new FXMLLoader(
                                        OrderPosApplication.class.getResource("javafx/login/login.fxml"));
                        Scene loginScene = new Scene(loginLoader.load(), 420, 240);
                        Stage loginStage = new Stage();
                        loginStage.setTitle("Đăng nhập");
                        loginStage.initModality(Modality.APPLICATION_MODAL);
                        loginStage.setScene(loginScene);
                        loginStage.showAndWait();
                        com.giadinh.apporderbill.javafx.login.LoginController loginController = loginLoader.getController();
                        if (loginController == null || loginController.getLoginOutput() == null) {
                                Platform.exit();
                                return;
                        }

                        // Đọc chế độ dữ liệu từ file config (REAL/DEMO), mặc định là REAL
                        DataModeConfig dataModeConfig = DataModeConfig.load();
                        boolean useDemoDatabase = dataModeConfig.isDemo();

                        SqliteConnectionProvider connectionProvider = new SqliteConnectionProvider(useDemoDatabase);

                        // Load Main Layout
                        FXMLLoader mainLoader = new FXMLLoader(
                                        OrderPosApplication.class.getResource("main-layout.fxml"));
                        Scene scene = new Scene(mainLoader.load(), 1600, 900);
                        stage.setMinWidth(1200);
                        stage.setMinHeight(700);
                        MainLayoutController mainController = mainLoader.getController();
                        // Cập nhật chế độ dữ liệu cho MainLayoutController để hiển thị trong cấu hình
                        mainController.setDataMode(dataModeConfig.getMode());

                        // Khởi tạo dữ liệu menu items từ SQL script
                        connectionProvider.initializeMenuData();

                        // Initialize Repositories
                        OrderRepository orderRepository = new SqliteOrderRepository(connectionProvider);
                        MenuItemRepository menuItemRepository = new SqliteMenuItemRepository(connectionProvider);
                        PaymentRepository paymentRepository = new SqlitePaymentRepository(connectionProvider);
                        KitchenTicketRepository kitchenTicketRepository = new SqliteKitchenTicketRepository(
                                        connectionProvider);
                        PrintTemplateRepository printTemplateRepository = new com.giadinh.apporderbill.printer.repository.SqlitePrintTemplateRepository(
                                        connectionProvider);
                        PrinterConfigRepository printerConfigRepository = new com.giadinh.apporderbill.printer.repository.SqlitePrinterConfigRepository(
                                        connectionProvider);
                        com.giadinh.apporderbill.table.repository.TableRepository tableRepository = new com.giadinh.apporderbill.table.infrastructure.repository.sqlite.SqliteTableRepository(
                                        connectionProvider);
                        com.giadinh.apporderbill.customer.repository.CustomerRepository customerRepository = new com.giadinh.apporderbill.customer.repository.SqliteCustomerRepository(
                                        connectionProvider);
                        com.giadinh.apporderbill.customer.usecase.CustomerUseCases customerUseCases = new com.giadinh.apporderbill.customer.usecase.CustomerUseCases(
                                        customerRepository);

                        Connection identityConnection = connectionProvider.getConnection();
                        com.giadinh.apporderbill.identity.repository.ModuleRepository moduleRepository = new com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.ModuleRepositoryImpl(
                                        identityConnection);
                        com.giadinh.apporderbill.identity.repository.FunctionRepository functionRepository = new com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.FunctionRepositoryImpl(
                                        identityConnection);
                        com.giadinh.apporderbill.identity.repository.RoleGroupRepository roleGroupRepository = new com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.RoleGroupRepositoryImpl(
                                        identityConnection);
                        com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository permissionAssignmentRepository = new com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.PermissionAssignmentRepositoryImpl(
                                        identityConnection);
                        com.giadinh.apporderbill.identity.repository.UserRepository userRepository = new com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.UserRepositoryImpl(
                                        identityConnection);
                        com.giadinh.apporderbill.identity.usecase.LoginUseCase loginUseCase = new com.giadinh.apporderbill.identity.usecase.LoginUseCase(
                                        userRepository, roleGroupRepository, permissionAssignmentRepository);
                        com.giadinh.apporderbill.identity.usecase.CheckAccessUseCase checkAccessUseCase = new com.giadinh.apporderbill.identity.usecase.CheckAccessUseCase(
                                        userRepository, roleGroupRepository, permissionAssignmentRepository, functionRepository);
                        com.giadinh.apporderbill.identity.usecase.ManageUserUseCase manageUserUseCase = new com.giadinh.apporderbill.identity.usecase.ManageUserUseCase(
                                        userRepository, roleGroupRepository);
                        com.giadinh.apporderbill.identity.usecase.ManageRoleGroupUseCase manageRoleGroupUseCase = new com.giadinh.apporderbill.identity.usecase.ManageRoleGroupUseCase(
                                        roleGroupRepository, permissionAssignmentRepository, functionRepository);
                        com.giadinh.apporderbill.identity.usecase.ManagePermissionAssignmentUseCase managePermissionAssignmentUseCase = new com.giadinh.apporderbill.identity.usecase.ManagePermissionAssignmentUseCase(
                                        permissionAssignmentRepository, roleGroupRepository, functionRepository);
                        com.giadinh.apporderbill.identity.IdentityComponent identityComponent = new com.giadinh.apporderbill.identity.IdentityComponentImpl(
                                        moduleRepository,
                                        functionRepository,
                                        roleGroupRepository,
                                        permissionAssignmentRepository,
                                        userRepository,
                                        loginUseCase,
                                        checkAccessUseCase,
                                        manageUserUseCase,
                                        manageRoleGroupUseCase,
                                        managePermissionAssignmentUseCase);

                        // Khởi tạo 20 bàn mặc định vào database nếu chưa có
                        connectionProvider.initializeTables(tableRepository);

                        com.giadinh.apporderbill.table.usecase.AddTableUseCase addTableUseCase =
                                        new com.giadinh.apporderbill.table.usecase.AddTableUseCase(tableRepository);
                        com.giadinh.apporderbill.table.usecase.DeleteTableUseCase deleteTableUseCase =
                                        new com.giadinh.apporderbill.table.usecase.DeleteTableUseCase(tableRepository);
                        com.giadinh.apporderbill.table.usecase.ClearTableUseCase clearTableUseCase =
                                        new com.giadinh.apporderbill.table.usecase.ClearTableUseCase(tableRepository);
                        com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase getAllTablesUseCase =
                                        new com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase(tableRepository);

                        PrinterService printerService = new SimplePrinterService(orderRepository, menuItemRepository,
                                        printTemplateRepository, printerConfigRepository);

                        // Initialize Use Cases - Order Management
                        OpenOrCreateOrderUseCase openOrCreateOrderUseCase = new OpenOrCreateOrderUseCase(
                                        orderRepository,
                                        menuItemRepository);
                        AddCustomItemToOrderUseCase addCustomItemToOrderUseCase = new AddCustomItemToOrderUseCase(
                                        orderRepository);
                        AddMenuItemToOrderUseCase addMenuItemToOrderUseCase = new AddMenuItemToOrderUseCase(
                                        orderRepository,
                                        menuItemRepository);
                        GetOrderDetailsUseCase getOrderDetailsUseCase = new GetOrderDetailsUseCase(orderRepository,
                                        menuItemRepository);
                        CalculateOrderTotalUseCase calculateOrderTotalUseCase = new CalculateOrderTotalUseCase(
                                        orderRepository);
                        CancelOrderUseCase cancelOrderUseCase = new CancelOrderUseCase(orderRepository,
                                        menuItemRepository, tableRepository);

                        com.giadinh.apporderbill.orders.usecase.TransferOrderBetweenTablesUseCase transferOrderBetweenTablesUseCase = new com.giadinh.apporderbill.orders.usecase.TransferOrderBetweenTablesUseCase(
                                        orderRepository, tableRepository);
                        com.giadinh.apporderbill.orders.usecase.ReleaseEmptyActiveOrderUseCase releaseEmptyActiveOrderUseCase = new com.giadinh.apporderbill.orders.usecase.ReleaseEmptyActiveOrderUseCase(
                                        orderRepository, tableRepository);
                        com.giadinh.apporderbill.table.usecase.RenameTableUseCase renameTableUseCase = new com.giadinh.apporderbill.table.usecase.RenameTableUseCase(
                                        tableRepository, orderRepository);
                        com.giadinh.apporderbill.table.usecase.SetTableReservationUseCase setTableReservationUseCase = new com.giadinh.apporderbill.table.usecase.SetTableReservationUseCase(
                                        tableRepository, orderRepository);

                        // Initialize Use Cases - Kitchen
                        PrintKitchenTicketUseCase printKitchenTicketUseCase = new PrintKitchenTicketUseCase(
                                        orderRepository,
                                        menuItemRepository,
                                        kitchenTicketRepository,
                                        printerService);
                        PrintSelectedItemsUseCase printSelectedItemsUseCase = new PrintSelectedItemsUseCase(
                                        orderRepository,
                                        menuItemRepository,
                                        kitchenTicketRepository,
                                        printerService);

                        // Initialize Use Cases - Billing
                        CheckoutOrderUseCase checkoutOrderUseCase = new CheckoutOrderUseCase(
                                        orderRepository,
                                        paymentRepository,
                                        tableRepository,
                                        customerUseCases);
                        PrintReceiptUseCase printReceiptUseCase = new PrintReceiptUseCase(paymentRepository,
                                        printerService);

                        // Initialize Use Cases - Menu
                        GetActiveMenuItemsUseCase getActiveMenuItemsUseCase = new GetActiveMenuItemsUseCase(
                                        menuItemRepository);
                        CreateMenuItemUseCase createMenuItemUseCase = new CreateMenuItemUseCase(menuItemRepository);
                        UpdateMenuItemUseCase updateMenuItemUseCase = new UpdateMenuItemUseCase(menuItemRepository);
                        DeleteMenuItemUseCase deleteMenuItemUseCase = new DeleteMenuItemUseCase(menuItemRepository);
                        GetAllMenuItemsUseCase getAllMenuItemsUseCase = new GetAllMenuItemsUseCase(menuItemRepository);
                        ExcelService excelService = new ExcelService();
                        ImportMenuFromExcelUseCase importMenuFromExcelUseCase = new ImportMenuFromExcelUseCase(
                                        menuItemRepository,
                                        excelService);
                        ExportMenuToExcelUseCase exportMenuToExcelUseCase = new ExportMenuToExcelUseCase(
                                        menuItemRepository, excelService);

                        // Initialize Use Cases - Item Management
                        UpdateOrderItemQuantityUseCase updateOrderItemQuantityUseCase = new UpdateOrderItemQuantityUseCase(
                                        orderRepository, menuItemRepository);
                        RemoveOrderItemUseCase removeOrderItemUseCase = new RemoveOrderItemUseCase(orderRepository,
                                        menuItemRepository);
                        DeleteOrderItemUseCase deleteOrderItemUseCase = new DeleteOrderItemUseCase(orderRepository,
                                        menuItemRepository);
                        UpdateOrderItemNoteUseCase updateOrderItemNoteUseCase = new UpdateOrderItemNoteUseCase(
                                        orderRepository, menuItemRepository);
                        com.giadinh.apporderbill.orders.usecase.UpdateOrderItemDiscountUseCase updateOrderItemDiscountUseCase = new com.giadinh.apporderbill.orders.usecase.UpdateOrderItemDiscountUseCase(
                                        orderRepository, menuItemRepository);

                        // Initialize Use Cases - Reporting
                        GetRevenueByDateRangeUseCase getRevenueByDateRangeUseCase = new GetRevenueByDateRangeUseCase(
                                        paymentRepository);
                        GetDailyRevenueUseCase getDailyRevenueUseCase = new GetDailyRevenueUseCase(paymentRepository);
                        GetWeeklyRevenueUseCase getWeeklyRevenueUseCase = new GetWeeklyRevenueUseCase(
                                        getRevenueByDateRangeUseCase);
                        GetMonthlyRevenueUseCase getMonthlyRevenueUseCase = new GetMonthlyRevenueUseCase(
                                        getRevenueByDateRangeUseCase);

                        // Initialize Use Cases - Billing (Payment History & Reprint)
                        com.giadinh.apporderbill.billing.usecase.GetTodayPaymentsUseCase getTodayPaymentsUseCase = new com.giadinh.apporderbill.billing.usecase.GetTodayPaymentsUseCase(
                                        paymentRepository, orderRepository);
                        com.giadinh.apporderbill.billing.usecase.GetPaymentsByDateRangeUseCase getPaymentsByDateRangeUseCase = new com.giadinh.apporderbill.billing.usecase.GetPaymentsByDateRangeUseCase(
                                        paymentRepository, orderRepository);
                        com.giadinh.apporderbill.billing.usecase.GetPaymentDetailUseCase getPaymentDetailUseCase = new com.giadinh.apporderbill.billing.usecase.GetPaymentDetailUseCase(
                                        paymentRepository, orderRepository, menuItemRepository);
                        com.giadinh.apporderbill.billing.usecase.DeletePaymentsByDateRangeUseCase deletePaymentsByDateRangeUseCase = new com.giadinh.apporderbill.billing.usecase.DeletePaymentsByDateRangeUseCase(
                                        paymentRepository);
                        com.giadinh.apporderbill.billing.usecase.ReprintReceiptUseCase reprintReceiptUseCase = new com.giadinh.apporderbill.billing.usecase.ReprintReceiptUseCase(
                                        paymentRepository, printerService);

                        // Initialize Use Cases - Printer
                        UpdatePrinterConfigUseCase updatePrinterConfigUseCase = new UpdatePrinterConfigUseCase(
                                        printerConfigRepository);
                        UpdatePrintTemplateUseCase updatePrintTemplateUseCase = new UpdatePrintTemplateUseCase(
                                        printTemplateRepository);
                        com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase getPrinterConfigUseCase = new com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase(
                                        printerConfigRepository);
                        com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase getPrintTemplateUseCase = new com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase(
                                        printTemplateRepository);

                        // Inject reporting use cases into MainLayoutController
                        mainController.setReportingUseCases(
                                        getDailyRevenueUseCase,
                                        getWeeklyRevenueUseCase,
                                        getMonthlyRevenueUseCase,
                                        getRevenueByDateRangeUseCase,
                                        getTodayPaymentsUseCase,
                                        getPaymentsByDateRangeUseCase,
                                        getPaymentDetailUseCase,
                                        deletePaymentsByDateRangeUseCase,
                                        reprintReceiptUseCase);

                        // Inject menu use cases into MainLayoutController
                        mainController.setMenuUseCases(
                                        createMenuItemUseCase,
                                        updateMenuItemUseCase,
                                        deleteMenuItemUseCase,
                                        getAllMenuItemsUseCase,
                                        importMenuFromExcelUseCase,
                                        exportMenuToExcelUseCase);
                        mainController.setCustomerUseCases(customerUseCases);
                        mainController.setIdentityComponent(identityComponent);
                        mainController.setTableManagementUseCases(
                                        addTableUseCase, deleteTableUseCase, clearTableUseCase, getAllTablesUseCase);
                        mainController.setCurrentUser(
                                        loginController.getLoginOutput().getUsername(),
                                        loginController.canOperate("Manage Menu Items"),
                                        loginController.canOperate("Manage Users"),
                                        loginController.canOperate("Manage Permissions"),
                                        loginController.canOperate("Manage Tables"));

                        // Inject printer use cases into MainLayoutController
                        mainController.setPrinterUseCases(
                                        updatePrinterConfigUseCase,
                                        updatePrintTemplateUseCase,
                                        getPrinterConfigUseCase,
                                        getPrintTemplateUseCase,
                                        printerService);

                        // Initialize Use Case - System (Storage Usage Check)
                        com.giadinh.apporderbill.system.usecase.CheckStorageUsageUseCase checkStorageUsageUseCase = new com.giadinh.apporderbill.system.usecase.CheckStorageUsageUseCase(
                                        connectionProvider);

                        // Inject storage usage use case into MainLayoutController
                        mainController.setCheckStorageUsageUseCase(checkStorageUsageUseCase);

                        // Get OrderScreenController from MainLayoutController after it loads order
                        // screen
                        javafx.application.Platform.runLater(() -> {
                                OrderScreenController controller = mainController.getOrderScreenController();
                                if (controller != null) {
                                        // Initialize Controller với tất cả use cases
                                        controller.init(
                                                        openOrCreateOrderUseCase,
                                                        addCustomItemToOrderUseCase,
                                                        addMenuItemToOrderUseCase,
                                                        getOrderDetailsUseCase,
                                                        calculateOrderTotalUseCase,
                                                        cancelOrderUseCase,
                                                        printKitchenTicketUseCase,
                                                        checkoutOrderUseCase,
                                                        printReceiptUseCase,
                                                        getActiveMenuItemsUseCase,
                                                        updateOrderItemQuantityUseCase,
                                                        removeOrderItemUseCase,
                                                        updateOrderItemDiscountUseCase);

                                        // Create presenter and set it
                                        OrderScreenPresenter presenter = new OrderScreenPresenter(
                                                        controller,
                                                        openOrCreateOrderUseCase,
                                                        addCustomItemToOrderUseCase,
                                                        addMenuItemToOrderUseCase,
                                                        getOrderDetailsUseCase,
                                                        calculateOrderTotalUseCase,
                                                        cancelOrderUseCase,
                                                        printKitchenTicketUseCase,
                                                        printSelectedItemsUseCase,
                                                        checkoutOrderUseCase,
                                                        printReceiptUseCase,
                                                        updateOrderItemQuantityUseCase,
                                                        removeOrderItemUseCase,
                                                        deleteOrderItemUseCase,
                                                        updateOrderItemNoteUseCase,
                                                        updateOrderItemDiscountUseCase,
                                                        orderRepository);
                                        controller.setPresenter(presenter);
                                        controller.setMenuItemRepository(menuItemRepository);
                                        controller.setOrderRepository(orderRepository);

                                        // Set printer service for draft receipt
                                        presenter.setPrinterService(printerService);

                                        // Inject table use cases vào controller
                                        controller.setAddTableUseCase(addTableUseCase);
                                        controller.setDeleteTableUseCase(deleteTableUseCase);
                                        controller.setClearTableUseCase(clearTableUseCase);
                                        controller.setGetAllTablesUseCase(getAllTablesUseCase);
                                        controller.setTransferOrderBetweenTablesUseCase(transferOrderBetweenTablesUseCase);
                                        controller.setReleaseEmptyActiveOrderUseCase(releaseEmptyActiveOrderUseCase);
                                        controller.setRenameTableUseCase(renameTableUseCase);
                                        controller.setSetTableReservationUseCase(setTableReservationUseCase);

                                        // Inject reprint use cases vào presenter
                                        presenter.setReprintUseCases(getTodayPaymentsUseCase, reprintReceiptUseCase);
                                }
                        });

                        stage.setTitle("Hệ thống Quản lý Order - Thu ngân");
                        stage.setScene(scene);
                        stage.show();
                } catch (Exception e) {
                        // In lỗi ra console để debug
                        System.err.println("Lỗi khởi động ứng dụng:");
                        e.printStackTrace();

                        // Hiển thị dialog lỗi nếu có thể
                        javafx.application.Platform.runLater(() -> {
                                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                                javafx.scene.control.Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi khởi động ứng dụng");
                                alert.setHeaderText("Không thể khởi động ứng dụng");
                                alert.setContentText("Lỗi: " + e.getMessage()
                                                + "\n\nVui lòng kiểm tra console để xem chi tiết.");
                                alert.showAndWait();
                        });
                }
        }

        public static void main(String[] args) {
                System.out.println("Starting application...");
                System.out.println("Working directory: " + System.getProperty("user.dir"));

                // Bắt uncaught exceptions
                Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
                        System.err.println("========================================");
                        System.err.println("Uncaught exception in thread " + thread.getName() + ":");
                        System.err.println("========================================");
                        exception.printStackTrace();
                        System.err.println("========================================");
                });

                try {
                        System.out.println("Launching JavaFX Application...");
                        launch();
                } catch (Exception e) {
                        System.err.println("========================================");
                        System.err.println("Failed to launch application:");
                        System.err.println("========================================");
                        e.printStackTrace();
                        System.err.println("========================================");
                }
        }
}
