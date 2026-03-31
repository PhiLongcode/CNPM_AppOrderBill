package com.giadinh.apporderbill.javafx.printer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PrintPreviewController {
    @FXML private Label zoomLabel;
    @FXML private Label scaleLabel;
    @FXML private TextArea previewTextArea;
    @FXML private VBox paperBox;

    private double zoom = 1.0;

    @FXML
    public void initialize() {
        applyZoom();
    }

    @FXML private void onZoomIn() { zoom = Math.min(2.5, zoom + 0.1); applyZoom(); }
    @FXML private void onZoomOut() { zoom = Math.max(0.4, zoom - 0.1); applyZoom(); }
    @FXML private void onActualSize() { zoom = 1.0; applyZoom(); }
    @FXML private void onResetZoom() { zoom = 1.0; applyZoom(); }
    @FXML private void onClose() {
        Stage stage = (Stage) previewTextArea.getScene().getWindow();
        stage.close();
    }

    public void setPreviewText(String content) {
        previewTextArea.setText(content == null ? "" : content);
    }

    private void applyZoom() {
        paperBox.setScaleX(zoom);
        paperBox.setScaleY(zoom);
        int percent = (int) Math.round(zoom * 100);
        zoomLabel.setText(percent + "%");
        scaleLabel.setText(percent == 100 ? "1:1 (Kích thước thực tế)" : ("Tỷ lệ " + percent + "%"));
    }
}

