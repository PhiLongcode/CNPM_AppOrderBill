package com.giadinh.apporderbill.javafx.login;

import com.giadinh.apporderbill.identity.IdentityComponent;
import com.giadinh.apporderbill.identity.usecase.dto.LoginInput;
import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
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
    private IdentityComponent identityComponentForRelogin;
    private LoginOutput loginOutput;

    /**
     * When set (after initial startup), login uses the shared {@link IdentityComponent} instead of a second SQLite stack.
     */
    public void setIdentityComponentForRelogin(IdentityComponent identityComponent) {
        this.identityComponentForRelogin = identityComponent;
    }

    /** Clear validation message and password before showing the dialog again. */
    public void prepareForRelogin() {
        if (messageLabel != null) {
            messageLabel.setText("");
        }
        if (passwordField != null) {
            passwordField.clear();
        }
    }

    @FXML
    public void initialize() {
        usernameField.setText("admin");
        passwordField.setText("admin_pass");
    }

    @FXML
    private void onLoginClick() {
        try {
            if (identityComponentForRelogin != null) {
                loginOutput = identityComponentForRelogin.login(
                        new LoginInput(usernameField.getText(), passwordField.getText()));
            } else {
                loginOutput = authService.login(usernameField.getText(), passwordField.getText());
            }
            close();
        } catch (DomainException e) {
            messageLabel.setText(DomainMessages.format(e));
        }
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
        if (loginOutput == null) {
            return false;
        }
        if (identityComponentForRelogin != null) {
            return identityComponentForRelogin.checkAccess(loginOutput.getUsername(), functionName, true);
        }
        return authService.canOperate(loginOutput.getUsername(), functionName);
    }

    private void close() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}

