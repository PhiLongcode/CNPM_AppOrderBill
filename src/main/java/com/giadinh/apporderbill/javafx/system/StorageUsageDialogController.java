package com.giadinh.apporderbill.javafx.system;

import com.giadinh.apporderbill.system.usecase.CheckStorageUsageUseCase;
import javafx.scene.control.Alert;
import javafx.stage.Window;

public class StorageUsageDialogController {
    public static void showDialog(Window owner, CheckStorageUsageUseCase useCase) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle("Dung luong su dung");
        alert.setHeaderText("Thong tin bo nho");
        String text = useCase == null ? "Khong co du lieu." : useCase.execute();
        alert.setContentText(text);
        alert.showAndWait();
    }
}

