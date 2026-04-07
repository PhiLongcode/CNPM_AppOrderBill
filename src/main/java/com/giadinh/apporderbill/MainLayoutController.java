package com.giadinh.apporderbill;

import com.giadinh.apporderbill.billing.usecase.GetTodayPaymentsUseCase;
import com.giadinh.apporderbill.billing.usecase.ReprintReceiptUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetDailyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetMonthlyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetRevenueByDateRangeUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetWeeklyRevenueUseCase;
import com.giadinh.apporderbill.shared.util.DataModeConfig;
import com.giadinh.apporderbill.javafx.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller cho Main Layout - quản lý navigation giữa các màn hình.
 */
public class MainLayoutController {

    @FXML
    private StackPane contentPane;

    private Parent orderScreen;
    private Parent dashboardScreen;
    private Parent menuManagementScreen;
    private com.giadinh.apporderbill.javafx.menu.MenuManagementController menuManagementController;
    private com.giadinh.apporderbill.javafx.menu.MenuManagementPresenter menuManagementPresenter;
    private Parent printerConfigScreen;
    private Parent userGuideScreen;
    private Parent customerManagementScreen;
    private Parent adminManagementScreen;
    private Parent tableManagementScreen;
    private com.giadinh.apporderbill.javafx.table.TableManagementController tableManagementController;

    private OrderScreenController orderScreenController;
    private DashboardController dashboardController;
    @FXML
    private MenuItem menuManagementMenuItem;
    @FXML
    private MenuItem customerManagementMenuItem;
    @FXML
    private MenuItem tableManagementMenuItem;
    @FXML
    private MenuItem adminManagementMenuItem;
    private com.giadinh.apporderbill.customer.usecase.CustomerUseCases customerUseCases;
    private com.giadinh.apporderbill.identity.IdentityComponent identityComponent;

    // Use cases for injection
    private GetDailyRevenueUseCase getDailyRevenueUseCase;
    private GetWeeklyRevenueUseCase getWeeklyRevenueUseCase;
    private GetMonthlyRevenueUseCase getMonthlyRevenueUseCase;
    private GetRevenueByDateRangeUseCase getRevenueByDateRangeUseCase;
    private GetTodayPaymentsUseCase getTodayPaymentsUseCase;
    private com.giadinh.apporderbill.billing.usecase.GetPaymentsByDateRangeUseCase getPaymentsByDateRangeUseCase;
    private com.giadinh.apporderbill.billing.usecase.GetPaymentDetailUseCase getPaymentDetailUseCase;
    private com.giadinh.apporderbill.billing.usecase.DeletePaymentsByDateRangeUseCase deletePaymentsByDateRangeUseCase;
    private ReprintReceiptUseCase reprintReceiptUseCase;

    @FXML
    public void initialize() {
        // Load Order Screen as default
        showOrderScreen();
    }

    @FXML
    protected void showOrderScreen() {
        try {
            if (orderScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/order/order-screen.fxml"));
                orderScreen = loader.load();
                orderScreenController = loader.getController();
            } else {
                // Refresh menu items khi quay lại màn hình Order
                if (orderScreenController != null) {
                    orderScreenController.refreshMenuItems();
                }
            }
            contentPane.getChildren().setAll(orderScreen);
        } catch (IOException e) {
            showError("Lỗi khi tải màn hình đơn hàng: " + e.getMessage());
        }
    }

    @FXML
    protected void showDashboardScreen() {
        try {
            if (dashboardScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/dashboard/dashboard.fxml"));
                dashboardScreen = loader.load();
                dashboardController = loader.getController();

                // Inject use cases if available
                if (getDailyRevenueUseCase != null && dashboardController != null) {
                    dashboardController.init(
                            getDailyRevenueUseCase,
                            getWeeklyRevenueUseCase,
                            getMonthlyRevenueUseCase,
                            getRevenueByDateRangeUseCase,
                            getTodayPaymentsUseCase,
                            getPaymentsByDateRangeUseCase,
                            getPaymentDetailUseCase,
                            deletePaymentsByDateRangeUseCase,
                            reprintReceiptUseCase);
                }
            }
            contentPane.getChildren().setAll(dashboardScreen);
        } catch (IOException e) {
            showError("Lỗi khi tải màn hình thống kê: " + e.getMessage());
        }
    }

    public void setReportingUseCases(GetDailyRevenueUseCase getDailyRevenueUseCase,
            GetWeeklyRevenueUseCase getWeeklyRevenueUseCase,
            GetMonthlyRevenueUseCase getMonthlyRevenueUseCase,
            GetRevenueByDateRangeUseCase getRevenueByDateRangeUseCase,
            GetTodayPaymentsUseCase getTodayPaymentsUseCase,
            com.giadinh.apporderbill.billing.usecase.GetPaymentsByDateRangeUseCase getPaymentsByDateRangeUseCase,
            com.giadinh.apporderbill.billing.usecase.GetPaymentDetailUseCase getPaymentDetailUseCase,
            com.giadinh.apporderbill.billing.usecase.DeletePaymentsByDateRangeUseCase deletePaymentsByDateRangeUseCase,
            ReprintReceiptUseCase reprintReceiptUseCase) {
        this.getDailyRevenueUseCase = getDailyRevenueUseCase;
        this.getWeeklyRevenueUseCase = getWeeklyRevenueUseCase;
        this.getMonthlyRevenueUseCase = getMonthlyRevenueUseCase;
        this.getRevenueByDateRangeUseCase = getRevenueByDateRangeUseCase;
        this.getTodayPaymentsUseCase = getTodayPaymentsUseCase;
        this.getPaymentsByDateRangeUseCase = getPaymentsByDateRangeUseCase;
        this.getPaymentDetailUseCase = getPaymentDetailUseCase;
        this.deletePaymentsByDateRangeUseCase = deletePaymentsByDateRangeUseCase;
        this.reprintReceiptUseCase = reprintReceiptUseCase;

        // If dashboard is already loaded, inject use cases
        if (dashboardController != null) {
            dashboardController.init(
                    getDailyRevenueUseCase,
                    getWeeklyRevenueUseCase,
                    getMonthlyRevenueUseCase,
                    getRevenueByDateRangeUseCase,
                    getTodayPaymentsUseCase,
                    getPaymentsByDateRangeUseCase,
                    getPaymentDetailUseCase,
                    deletePaymentsByDateRangeUseCase,
                    reprintReceiptUseCase);
        }
    }

    @FXML
    protected void showMenuManagementScreen() {
        try {
            if (menuManagementScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/menu/menu-management.fxml"));
                menuManagementScreen = loader.load();
                menuManagementController = loader.getController();

                // Inject use cases if available
                if (createMenuItemUseCase != null && menuManagementController != null) {
                    if (menuManagementPresenter == null) {
                        menuManagementPresenter = new com.giadinh.apporderbill.javafx.menu.MenuManagementPresenter(
                                menuManagementController,
                                createMenuItemUseCase, updateMenuItemUseCase, deleteMenuItemUseCase,
                                getAllMenuItemsUseCase, importMenuFromExcelUseCase, exportMenuToExcelUseCase);
                    }
                    menuManagementController.setPresenter(menuManagementPresenter);
                    menuManagementController.reloadFromDatabase();
                }
            } else {
                // Màn đã load trước đó: reload lại dữ liệu từ DB để cập nhật tồn kho
                if (menuManagementController != null) {
                    menuManagementController.reloadFromDatabase();
                }
            }
            contentPane.getChildren().setAll(menuManagementScreen);
        } catch (Exception e) {
            String detail = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null && !e.getCause().getMessage().isBlank()) {
                detail = e.getCause().getMessage();
            }
            showError("Lỗi khi tải màn hình quản lý menu: " + detail);
            e.printStackTrace();
        }
    }

    @FXML
    protected void showCustomerManagementScreen() {
        try {
            if (customerManagementScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/customer/customer-management.fxml"));
                customerManagementScreen = loader.load();
                com.giadinh.apporderbill.javafx.customer.CustomerManagementController controller = loader.getController();
                if (customerUseCases != null) {
                    controller.setUseCases(customerUseCases);
                }
            }
            contentPane.getChildren().setAll(customerManagementScreen);
        } catch (IOException e) {
            showError("Lỗi khi tải màn hình quản lý khách hàng: " + e.getMessage());
        }
    }

    @FXML
    protected void showTableManagementScreen() {
        try {
            if (tableManagementScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/table/table-management.fxml"));
                tableManagementScreen = loader.load();
                tableManagementController = loader.getController();
                if (addTableUseCase != null && deleteTableUseCase != null && clearTableUseCase != null
                        && getAllTablesUseCase != null) {
                    tableManagementController.setUseCases(addTableUseCase, deleteTableUseCase, clearTableUseCase,
                            getAllTablesUseCase);
                }
            } else if (tableManagementController != null) {
                tableManagementController.reloadFromDatabase();
            }
            contentPane.getChildren().setAll(tableManagementScreen);
        } catch (IOException e) {
            showError("Lỗi khi tải màn hình quản lý bàn: " + e.getMessage());
        }
    }

    public void setCustomerUseCases(com.giadinh.apporderbill.customer.usecase.CustomerUseCases customerUseCases) {
        this.customerUseCases = customerUseCases;
    }

    public void setIdentityComponent(com.giadinh.apporderbill.identity.IdentityComponent identityComponent) {
        this.identityComponent = identityComponent;
    }

    @FXML
    protected void showAdminManagementScreen() {
        try {
            if (adminManagementScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/admin/admin-management.fxml"));
                adminManagementScreen = loader.load();
                com.giadinh.apporderbill.javafx.admin.AdminManagementController controller = loader.getController();
                if (identityComponent != null) {
                    controller.setIdentityComponent(identityComponent);
                }
            }
            contentPane.getChildren().setAll(adminManagementScreen);
        } catch (IOException e) {
            showError("Lỗi khi tải màn hình quản trị phân quyền: " + e.getMessage());
        }
    }

    public void setCurrentUser(String username, boolean canManageMenu, boolean canManageCustomer, boolean canManageAdmin,
            boolean canManageTables) {
        if (menuManagementMenuItem != null) {
            menuManagementMenuItem.setDisable(!canManageMenu);
        }
        if (customerManagementMenuItem != null) {
            customerManagementMenuItem.setDisable(!canManageCustomer);
        }
        if (tableManagementMenuItem != null) {
            tableManagementMenuItem.setDisable(!canManageTables);
        }
        if (adminManagementMenuItem != null) {
            adminManagementMenuItem.setDisable(!canManageAdmin);
        }
    }

    private com.giadinh.apporderbill.table.usecase.AddTableUseCase addTableUseCase;
    private com.giadinh.apporderbill.table.usecase.DeleteTableUseCase deleteTableUseCase;
    private com.giadinh.apporderbill.table.usecase.ClearTableUseCase clearTableUseCase;
    private com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase getAllTablesUseCase;

    public void setTableManagementUseCases(
            com.giadinh.apporderbill.table.usecase.AddTableUseCase addTableUseCase,
            com.giadinh.apporderbill.table.usecase.DeleteTableUseCase deleteTableUseCase,
            com.giadinh.apporderbill.table.usecase.ClearTableUseCase clearTableUseCase,
            com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase getAllTablesUseCase) {
        this.addTableUseCase = addTableUseCase;
        this.deleteTableUseCase = deleteTableUseCase;
        this.clearTableUseCase = clearTableUseCase;
        this.getAllTablesUseCase = getAllTablesUseCase;
        if (tableManagementScreen != null && tableManagementController != null && addTableUseCase != null) {
            tableManagementController.setUseCases(addTableUseCase, deleteTableUseCase, clearTableUseCase,
                    getAllTablesUseCase);
        }
    }

    // Use cases for menu management
    private com.giadinh.apporderbill.catalog.usecase.CreateMenuItemUseCase createMenuItemUseCase;
    private com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase updateMenuItemUseCase;
    private com.giadinh.apporderbill.catalog.usecase.DeleteMenuItemUseCase deleteMenuItemUseCase;
    private com.giadinh.apporderbill.catalog.usecase.GetAllMenuItemsUseCase getAllMenuItemsUseCase;
    private com.giadinh.apporderbill.catalog.usecase.ImportMenuFromExcelUseCase importMenuFromExcelUseCase;
    private com.giadinh.apporderbill.catalog.usecase.ExportMenuToExcelUseCase exportMenuToExcelUseCase;

    public void setMenuUseCases(
            com.giadinh.apporderbill.catalog.usecase.CreateMenuItemUseCase createMenuItemUseCase,
            com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase updateMenuItemUseCase,
            com.giadinh.apporderbill.catalog.usecase.DeleteMenuItemUseCase deleteMenuItemUseCase,
            com.giadinh.apporderbill.catalog.usecase.GetAllMenuItemsUseCase getAllMenuItemsUseCase,
            com.giadinh.apporderbill.catalog.usecase.ImportMenuFromExcelUseCase importMenuFromExcelUseCase,
            com.giadinh.apporderbill.catalog.usecase.ExportMenuToExcelUseCase exportMenuToExcelUseCase) {
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
        this.getAllMenuItemsUseCase = getAllMenuItemsUseCase;
        this.importMenuFromExcelUseCase = importMenuFromExcelUseCase;
        this.exportMenuToExcelUseCase = exportMenuToExcelUseCase;

        // If menu management is already loaded, inject use cases
        if (menuManagementScreen != null && menuManagementController != null) {
            if (menuManagementPresenter == null) {
                menuManagementPresenter = new com.giadinh.apporderbill.javafx.menu.MenuManagementPresenter(
                        menuManagementController,
                        createMenuItemUseCase, updateMenuItemUseCase, deleteMenuItemUseCase,
                        getAllMenuItemsUseCase, importMenuFromExcelUseCase, exportMenuToExcelUseCase);
            }
            menuManagementController.setPresenter(menuManagementPresenter);
        }
    }

    @FXML
    protected void showPrinterConfigScreen() {
        try {
            if (printerConfigScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/printer/printer-config.fxml"));
                printerConfigScreen = loader.load();
                com.giadinh.apporderbill.javafx.printer.PrinterConfigController printerController = loader
                        .getController();

                // Inject use cases if available
                if (updatePrinterConfigUseCase != null && printerController != null) {
                    printerController.init(
                            updatePrinterConfigUseCase,
                            updatePrintTemplateUseCase,
                            getPrinterConfigUseCase,
                            getPrintTemplateUseCase,
                            printerService);
                }
            }
            contentPane.getChildren().setAll(printerConfigScreen);
        } catch (IOException e) {
            showError("Lỗi khi tải màn hình cấu hình máy in: " + e.getMessage());
        }
    }

    // Use cases for printer configuration
    private com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase updatePrinterConfigUseCase;
    private com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase updatePrintTemplateUseCase;
    private com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase getPrinterConfigUseCase;
    private com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase getPrintTemplateUseCase;
    private com.giadinh.apporderbill.shared.service.PrinterService printerService;

    // Use case for storage usage check
    private com.giadinh.apporderbill.system.usecase.CheckStorageUsageUseCase checkStorageUsageUseCase;

    // Chế độ dữ liệu hiện tại (REAL / DEMO)
    private DataModeConfig.Mode currentDataMode = DataModeConfig.Mode.REAL;
    private String currentDataModeLabel = "DỮ LIỆU THẬT (pos.db)";

    public void setPrinterUseCases(
            com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase updatePrinterConfigUseCase,
            com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase updatePrintTemplateUseCase,
            com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase getPrinterConfigUseCase,
            com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase getPrintTemplateUseCase,
            com.giadinh.apporderbill.shared.service.PrinterService printerService) {
        this.updatePrinterConfigUseCase = updatePrinterConfigUseCase;
        this.updatePrintTemplateUseCase = updatePrintTemplateUseCase;
        this.getPrinterConfigUseCase = getPrinterConfigUseCase;
        this.getPrintTemplateUseCase = getPrintTemplateUseCase;
        this.printerService = printerService;
    }

    public void setCheckStorageUsageUseCase(
            com.giadinh.apporderbill.system.usecase.CheckStorageUsageUseCase checkStorageUsageUseCase) {
        this.checkStorageUsageUseCase = checkStorageUsageUseCase;
    }

    public void setDataMode(DataModeConfig.Mode mode) {
        if (mode == null) {
            mode = DataModeConfig.Mode.REAL;
        }
        this.currentDataMode = mode;
        this.currentDataModeLabel = (mode == DataModeConfig.Mode.DEMO)
                ? "DEMO / TEST (pos-demo.db)"
                : "DỮ LIỆU THẬT (pos.db)";
    }

    /**
     * Hiển thị dialog cấu hình chế độ dữ liệu (THẬT / DEMO).
     * Lưu ý: sau khi bấm "Lưu & khởi động lại", ứng dụng sẽ thoát,
     * bạn cần mở lại để áp dụng chế độ mới.
     */
    @FXML
    protected void showDataModeDialog() {
        // Tạo dialog với 2 radio button: REAL / DEMO
        javafx.scene.control.Dialog<ButtonType> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Chế độ dữ liệu");
        dialog.setHeaderText("Chọn chế độ dữ liệu cho ứng dụng");

        javafx.scene.control.RadioButton realButton = new javafx.scene.control.RadioButton("DỮ LIỆU THẬT (pos.db)");
        javafx.scene.control.RadioButton demoButton = new javafx.scene.control.RadioButton("DEMO / TEST (pos-demo.db)");

        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();
        realButton.setToggleGroup(group);
        demoButton.setToggleGroup(group);

        if (currentDataMode == DataModeConfig.Mode.DEMO) {
            demoButton.setSelected(true);
        } else {
            realButton.setSelected(true);
        }

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10, realButton, demoButton);
        vbox.setStyle("-fx-padding: 10;");
        dialog.getDialogPane().setContent(vbox);

        ButtonType saveAndRestart = new ButtonType("Lưu & khởi động lại", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().setAll(saveAndRestart, ButtonType.CANCEL);

        java.util.Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveAndRestart) {
            DataModeConfig.Mode newMode = realButton.isSelected()
                    ? DataModeConfig.Mode.REAL
                    : DataModeConfig.Mode.DEMO;

            // Lưu cấu hình
            DataModeConfig.save(newMode);
            setDataMode(newMode);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Khởi động lại ứng dụng");
            info.setHeaderText("Đã lưu chế độ dữ liệu: " + currentDataModeLabel);
            info.setContentText("Ứng dụng sẽ thoát ngay bây giờ.\n"
                    + "Vui lòng mở lại để sử dụng chế độ dữ liệu mới.");
            info.showAndWait();

            // Thoát ứng dụng, người dùng tự mở lại
            javafx.application.Platform.exit();
        }
    }

    @FXML
    protected void showUserGuideScreen() {
        try {
            if (userGuideScreen == null) {
                FXMLLoader loader = new FXMLLoader(requireFxml("javafx/guide/user-guide.fxml"));
                userGuideScreen = loader.load();
            }
            contentPane.getChildren().setAll(userGuideScreen);
        } catch (Exception e) {
            showError("Lỗi khi tải màn hình hướng dẫn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void showStorageUsageDialog() {
        if (checkStorageUsageUseCase == null) {
            showError("Chức năng kiểm tra dung lượng chưa được khởi tạo.");
            return;
        }

        javafx.scene.control.Dialog<ButtonType> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Kiểm tra dung lượng sử dụng");

        // Lấy owner window từ contentPane
        javafx.stage.Window owner = null;
        if (contentPane != null && contentPane.getScene() != null) {
            owner = contentPane.getScene().getWindow();
        }

        com.giadinh.apporderbill.javafx.system.StorageUsageDialogController.showDialog(
                owner,
                checkStorageUsageUseCase);
    }

    @FXML
    protected void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Về ứng dụng");
        alert.setHeaderText("Hệ thống Quản lý Order Bò");
        alert.setContentText("Phiên bản: 1.0\n\n" +
                "Ứng dụng quản lý order và thanh toán cho nhà hàng.\n\n" +
                "Tính năng chính:\n" +
                "• Quản lý order và bàn\n" +
                "• Quản lý menu và tồn kho\n" +
                "• In phiếu bếp và hóa đơn\n" +
                "• Thống kê doanh thu\n\n" +
                "© 2025 - Tất cả quyền được bảo lưu");
        alert.showAndWait();
    }

    @FXML
    protected void exitApplication() {
        javafx.application.Platform.exit();
    }

    public OrderScreenController getOrderScreenController() {
        return orderScreenController;
    }

    private java.net.URL requireFxml(String relativePath) throws IOException {
        String normalizedPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        java.net.URL resource = getClass().getResource("/com/giadinh/apporderbill/" + normalizedPath);
        if (resource == null) {
            throw new IOException("Không tìm thấy file FXML: /com/giadinh/apporderbill/" + normalizedPath);
        }
        return resource;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
