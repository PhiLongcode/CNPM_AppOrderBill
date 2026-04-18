package com.giadinh.apporderbill.javafx.settings;

import com.giadinh.apporderbill.catalog.usecase.GetAllMenuItemsUseCase;
import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.javafx.printer.PrinterConfigController;
import com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase;
import com.giadinh.apporderbill.shared.service.PrinterService;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * Màn cấu hình gồm tab: tích điểm, VAT, máy in.
 */
public class AppSettingsController {

    @FXML private TabPane settingsTabPane;

    @FXML private LoyaltyConfigTabController loyaltyTabController;
    @FXML private VatConfigTabController vatTabController;
    @FXML private PrinterConfigController printerTabController;

    public void setCustomerUseCases(CustomerUseCases customerUseCases) {
        if (loyaltyTabController != null) {
            loyaltyTabController.setUseCases(customerUseCases);
        }
        if (vatTabController != null) {
            vatTabController.setUseCases(customerUseCases);
        }
    }

    public void setGetAllMenuItemsUseCase(GetAllMenuItemsUseCase getAllMenuItemsUseCase) {
        if (loyaltyTabController != null) {
            loyaltyTabController.setGetAllMenuItemsUseCase(getAllMenuItemsUseCase);
        }
    }

    public void initPrinterTab(UpdatePrinterConfigUseCase updatePrinterConfigUseCase,
            UpdatePrintTemplateUseCase updatePrintTemplateUseCase,
            GetPrinterConfigUseCase getPrinterConfigUseCase,
            GetPrintTemplateUseCase getPrintTemplateUseCase,
            PrinterService printerService) {
        if (printerTabController == null || updatePrinterConfigUseCase == null) {
            return;
        }
        printerTabController.init(
                updatePrinterConfigUseCase,
                updatePrintTemplateUseCase,
                getPrinterConfigUseCase,
                getPrintTemplateUseCase,
                printerService);
    }

    public TabPane getSettingsTabPane() {
        return settingsTabPane;
    }
}
