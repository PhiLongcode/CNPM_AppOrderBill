package com.giadinh.apporderbill.javafx.printer;

import com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrintTemplateInput;
import com.giadinh.apporderbill.shared.service.PrinterService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PrinterConfigController {
    @FXML private TextField printerNameField;
    @FXML private ComboBox<String> connectionTypeCombo;
    @FXML private TextField paperSizeField;
    @FXML private Spinner<Integer> copiesSpinner;
    @FXML private CheckBox defaultKitchenCheckBox;
    @FXML private CheckBox defaultReceiptCheckBox;
    @FXML private ComboBox<String> templateTypeCombo;
    @FXML private TextField storeNameField;
    @FXML private TextField storeAddressField;
    @FXML private TextField storePhoneField;
    @FXML private TextArea headerTextArea;
    @FXML private TextArea footerTextArea;

    private UpdatePrinterConfigUseCase updatePrinterConfigUseCase;
    private UpdatePrintTemplateUseCase updatePrintTemplateUseCase;
    private GetPrinterConfigUseCase getPrinterConfigUseCase;
    private GetPrintTemplateUseCase getPrintTemplateUseCase;
    private PrinterService printerService;

    public void init(UpdatePrinterConfigUseCase updatePrinterConfigUseCase,
            UpdatePrintTemplateUseCase updatePrintTemplateUseCase,
            GetPrinterConfigUseCase getPrinterConfigUseCase,
            GetPrintTemplateUseCase getPrintTemplateUseCase,
            PrinterService printerService) {
        this.updatePrinterConfigUseCase = updatePrinterConfigUseCase;
        this.updatePrintTemplateUseCase = updatePrintTemplateUseCase;
        this.getPrinterConfigUseCase = getPrinterConfigUseCase;
        this.getPrintTemplateUseCase = getPrintTemplateUseCase;
        this.printerService = printerService;

        if (this.getPrinterConfigUseCase != null && printerNameField != null) {
            var cfg = this.getPrinterConfigUseCase.execute();
            if (cfg != null) {
                printerNameField.setText(cfg.getPrinterName());
            }
        }
        if (this.getPrintTemplateUseCase != null && storeNameField != null) {
            var tpl = this.getPrintTemplateUseCase.execute("RECEIPT");
            if (tpl != null) {
                storeNameField.setText(tpl.getStoreName());
            }
        }
    }

    @FXML
    private void onSavePrinterConfig() {
        if (updatePrinterConfigUseCase == null) return;
        updatePrinterConfigUseCase.execute(new UpdatePrinterConfigInput(
                printerNameField == null ? "" : printerNameField.getText(),
                connectionTypeCombo == null || connectionTypeCombo.getValue() == null ? "WINDOWS" : connectionTypeCombo.getValue(),
                paperSizeField == null ? "80mm" : paperSizeField.getText(),
                copiesSpinner == null || copiesSpinner.getValue() == null ? 1 : copiesSpinner.getValue(),
                defaultKitchenCheckBox != null && defaultKitchenCheckBox.isSelected(),
                defaultReceiptCheckBox != null && defaultReceiptCheckBox.isSelected()
        ));
    }

    @FXML
    private void onSaveTemplate() {
        if (updatePrintTemplateUseCase == null) return;
        updatePrintTemplateUseCase.execute(new UpdatePrintTemplateInput(
                templateTypeCombo == null || templateTypeCombo.getValue() == null ? "RECEIPT" : templateTypeCombo.getValue(),
                storeNameField == null ? "" : storeNameField.getText(),
                storeAddressField == null ? "" : storeAddressField.getText(),
                storePhoneField == null ? "" : storePhoneField.getText(),
                headerTextArea == null ? "" : headerTextArea.getText(),
                footerTextArea == null ? "" : footerTextArea.getText()
        ));
    }

    @FXML private void onRefreshPrinters() {}
    @FXML private void onTestConnection() {}
    @FXML private void onRefreshPreview() {}
    @FXML private void onShowPaperPreview() {}

    @FXML
    private void onTestPrint() {
        if (printerService != null) {
            printerService.printTest("TEST PRINTER");
        }
    }
}

