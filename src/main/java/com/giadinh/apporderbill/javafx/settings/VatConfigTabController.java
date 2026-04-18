package com.giadinh.apporderbill.javafx.settings;

import com.giadinh.apporderbill.customer.usecase.CustomerUseCases;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class VatConfigTabController {

    @FXML private TextField vatPercentField;
    @FXML private Label vatInfoLabel;

    private CustomerUseCases useCases;

    @FXML
    public void initialize() {
        if (vatInfoLabel != null) {
            String message = msg("ui.customer.vat_hint");
            Tooltip tooltip = new Tooltip(message);
            Tooltip.install(vatInfoLabel, tooltip);
            vatInfoLabel.setStyle(vatInfoLabel.getStyle() + "; -fx-cursor: hand;");
            vatInfoLabel.setOnMouseClicked(e -> showInfo(message));
        }
    }

    public void setUseCases(CustomerUseCases useCases) {
        this.useCases = useCases;
        loadFromUseCases();
    }

    @FXML
    private void onSave() {
        if (useCases == null) {
            return;
        }
        try {
            useCases.updateVatPercent(parseNonNegativeDouble(vatPercentField, "ui.customer.vat_invalid"));
            showInfo(msg("ui.settings.vat_saved"));
        } catch (Exception e) {
            showInfo(e.getMessage());
        }
    }

    private void loadFromUseCases() {
        if (useCases == null || vatPercentField == null) {
            return;
        }
        vatPercentField.setText(String.valueOf(useCases.getVatPercent()));
    }

    private double parseNonNegativeDouble(TextField field, String errorMessageKey) {
        if (field == null) {
            return 0.0;
        }
        double value;
        try {
            value = Double.parseDouble(field.getText().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
        if (value < 0) {
            throw new IllegalArgumentException(msg(errorMessageKey));
        }
        return value;
    }

    private void showInfo(String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
    }
}
