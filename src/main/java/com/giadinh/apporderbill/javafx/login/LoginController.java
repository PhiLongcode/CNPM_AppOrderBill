package com.giadinh.apporderbill.javafx.login;

import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final IdentityAuthService authService = new IdentityAuthService();
    private LoginOutput loginOutput;

    @FXML
    public void initialize() {
        usernameField.setText("admin");
        passwordField.setText("admin_pass");
    }

    @FXML
    private void onLoginClick() {
        LoginOutput out = authService.login(usernameField.getText(), passwordField.getText());
        if (out.isSuccess()) {
            loginOutput = out;
            close();
            return;
        }
        messageLabel.setText(out.getMessage());
    }

    @FXML
    private void onCancelClick() {
        loginOutput = null;
        close();
    }

    public LoginOutput getLoginOutput() {
        return loginOutput;
    }

    public boolean canOperate(String functionName) {
        return loginOutput != null && authService.canOperate(loginOutput.getUsername(), functionName);
    }

    private void close() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}

