package com.giadinh.apporderbill.javafx.printer;

import com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.dto.PrintTemplateOutput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrintTemplateInput;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import com.giadinh.apporderbill.shared.service.PrinterService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.print.Printer;

import java.util.List;
import java.util.stream.Collectors;

public class PrinterConfigController {
    @FXML private TextField printerNameField;
    @FXML private ComboBox<String> connectionTypeCombo;
    @FXML private ComboBox<String> windowsPrinterCombo;
    @FXML private TextField paperSizeField;
    @FXML private Spinner<Integer> copiesSpinner;
    @FXML private CheckBox defaultKitchenCheckBox;
    @FXML private CheckBox defaultReceiptCheckBox;
    @FXML private ComboBox<String> templateTypeCombo;
    @FXML private ComboBox<String> testPrintTypeCombo;
    @FXML private Label escposStatusLabel;
    @FXML private Label printMethodInfoLabel;
    @FXML private TextArea previewTextArea;
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
        initializeCombos();
        onRefreshPrinters();

        if (this.getPrinterConfigUseCase != null && printerNameField != null) {
            var cfg = this.getPrinterConfigUseCase.execute();
            if (cfg != null) {
                printerNameField.setText(cfg.getPrinterName());
                if (connectionTypeCombo != null) connectionTypeCombo.setValue(cfg.getConnectionType());
                if (paperSizeField != null) paperSizeField.setText(cfg.getPaperSize());
                if (copiesSpinner != null) copiesSpinner.getValueFactory().setValue(cfg.getCopies());
                if (defaultKitchenCheckBox != null) defaultKitchenCheckBox.setSelected(cfg.isDefaultKitchen());
                if (defaultReceiptCheckBox != null) defaultReceiptCheckBox.setSelected(cfg.isDefaultReceipt());
            }
        }
        if (templateTypeCombo != null && templateTypeCombo.getValue() == null) {
            templateTypeCombo.setValue("RECEIPT");
        }
        loadTemplate();
        refreshPreview();
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
        showInfo(msg("ui.printer.config_saved"));
        updatePrintMethodInfo();
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
        showInfo(msg("ui.printer.template_saved"));
        refreshPreview();
    }

    @FXML
    private void onRefreshPrinters() {
        if (windowsPrinterCombo == null) return;
        List<String> names = Printer.getAllPrinters().stream()
                .map(Printer::getName)
                .sorted()
                .collect(Collectors.toList());
        windowsPrinterCombo.getItems().setAll(names);
        if (!names.isEmpty() && windowsPrinterCombo.getValue() == null) {
            windowsPrinterCombo.setValue(names.get(0));
        }
        if (escposStatusLabel != null) {
            escposStatusLabel.setText(msg("ui.printer.escpos_ready"));
        }
    }

    @FXML
    private void onTestConnection() {
        if (connectionTypeCombo == null || connectionTypeCombo.getValue() == null) {
            showInfo(msg("ui.printer.connection_type_required"));
            return;
        }
        showInfo(msg("ui.printer.connection_ready", connectionTypeCombo.getValue()));
    }

    @FXML
    private void onRefreshPreview() {
        refreshPreview();
    }

    @FXML
    private void onShowPaperPreview() {
        String paperSize = paperSizeField == null ? "80mm" : paperSizeField.getText();
        showInfo(msg("ui.printer.paper_size_current", paperSize));
    }

    @FXML
    private void onTestPrint() {
        if (printerService != null) {
            String type = testPrintTypeCombo == null || testPrintTypeCombo.getValue() == null
                    ? "TEST"
                    : testPrintTypeCombo.getValue();
            printerService.printTest(msg("ui.printer.test_print_content", type));
        }
    }

    @FXML
    private void initialize() {
        initializeCombos();
    }

    private void initializeCombos() {
        if (connectionTypeCombo != null && connectionTypeCombo.getItems().isEmpty()) {
            connectionTypeCombo.getItems().setAll("WINDOWS", "USB", "NETWORK");
            connectionTypeCombo.setValue("WINDOWS");
        }
        if (templateTypeCombo != null && templateTypeCombo.getItems().isEmpty()) {
            templateTypeCombo.getItems().setAll("RECEIPT", "KITCHEN", "DRAFT", "TEST");
            templateTypeCombo.setValue("RECEIPT");
            templateTypeCombo.valueProperty().addListener((obs, oldV, newV) -> {
                loadTemplate();
                refreshPreview();
            });
        }
        if (testPrintTypeCombo != null && testPrintTypeCombo.getItems().isEmpty()) {
            testPrintTypeCombo.getItems().setAll("TEST", "RECEIPT", "KITCHEN", "DRAFT");
            testPrintTypeCombo.setValue("TEST");
        }
        if (copiesSpinner != null && copiesSpinner.getValueFactory() == null) {
            copiesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        }
        updatePrintMethodInfo();
    }

    private void loadTemplate() {
        if (getPrintTemplateUseCase == null || templateTypeCombo == null) return;
        PrintTemplateOutput tpl = getPrintTemplateUseCase.execute(templateTypeCombo.getValue());
        if (tpl == null) return;
        if (storeNameField != null) storeNameField.setText(tpl.getStoreName());
        if (storeAddressField != null) storeAddressField.setText(tpl.getStoreAddress());
        if (storePhoneField != null) storePhoneField.setText(tpl.getStorePhone());
        if (headerTextArea != null) headerTextArea.setText(tpl.getHeader());
        if (footerTextArea != null) footerTextArea.setText(tpl.getFooter());
    }

    private void refreshPreview() {
        if (previewTextArea == null) return;
        String type = templateTypeCombo == null ? "RECEIPT" : templateTypeCombo.getValue();
        previewTextArea.setText("""
                %s
                %s
                %s
                %s
                %s
                %s
                %s
                ...
                %s
                %s
                """.formatted(
                msg("ui.printer.preview_top"),
                msg("ui.printer.preview_template_type", type),
                msg("ui.printer.preview_store_name", storeNameField == null ? "" : storeNameField.getText()),
                msg("ui.printer.preview_store_address", storeAddressField == null ? "" : storeAddressField.getText()),
                msg("ui.printer.preview_store_phone", storePhoneField == null ? "" : storePhoneField.getText()),
                msg("ui.printer.preview_separator"),
                type,
                headerTextArea == null ? "" : headerTextArea.getText(),
                footerTextArea == null ? "" : footerTextArea.getText(),
                msg("ui.printer.preview_bottom")
        ));
    }

    private void updatePrintMethodInfo() {
        if (printMethodInfoLabel == null || connectionTypeCombo == null) return;
        String type = connectionTypeCombo.getValue() == null ? "WINDOWS" : connectionTypeCombo.getValue();
        printMethodInfoLabel.setText(msg("ui.printer.method_info", type));
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}

