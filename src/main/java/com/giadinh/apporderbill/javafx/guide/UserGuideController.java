package com.giadinh.apporderbill.javafx.guide;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class UserGuideController {
    @FXML private VBox section1;
    @FXML private VBox section2;
    @FXML private VBox section3;
    @FXML private VBox section4;
    @FXML private VBox section5;
    @FXML private VBox section6;
    @FXML private VBox section7;
    @FXML private VBox section8;
    @FXML private VBox section9;
    @FXML private VBox section10;

    @FXML private void scrollToSection1() { scrollTo(section1); }
    @FXML private void scrollToSection2() { scrollTo(section2); }
    @FXML private void scrollToSection3() { scrollTo(section3); }
    @FXML private void scrollToSection4() { scrollTo(section4); }
    @FXML private void scrollToSection5() { scrollTo(section5); }
    @FXML private void scrollToSection6() { scrollTo(section6); }
    @FXML private void scrollToSection7() { scrollTo(section7); }
    @FXML private void scrollToSection8() { scrollTo(section8); }
    @FXML private void scrollToSection9() { scrollTo(section9); }
    @FXML private void scrollToSection10() { scrollTo(section10); }

    private void scrollTo(Node target) {
        if (target == null) return;
        ScrollPane scrollPane = findScrollPane(target);
        if (scrollPane == null || scrollPane.getContent() == null) return;
        Platform.runLater(() -> {
            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
            if (contentHeight <= 0) return;
            double y = target.getBoundsInParent().getMinY();
            double viewport = scrollPane.getViewportBounds().getHeight();
            double v = y / Math.max(1, contentHeight - viewport);
            scrollPane.setVvalue(Math.max(0, Math.min(1, v)));
        });
    }

    private ScrollPane findScrollPane(Node node) {
        Node p = node;
        while (p != null && !(p instanceof ScrollPane)) {
            p = p.getParent();
        }
        return (ScrollPane) p;
    }
}

