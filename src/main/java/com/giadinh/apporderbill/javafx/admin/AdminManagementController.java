package com.giadinh.apporderbill.javafx.admin;

import com.giadinh.apporderbill.identity.IdentityComponent;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserInput;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AdminManagementController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Number> userIdCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> userRoleCol;

    @FXML private TableView<RoleGroup> rolesTable;
    @FXML private TableColumn<RoleGroup, Number> roleIdCol;
    @FXML private TableColumn<RoleGroup, String> roleNameCol;
    @FXML private TableColumn<RoleGroup, String> roleDescCol;

    @FXML private TableView<PermissionAssignment> permissionsTable;
    @FXML private TableColumn<PermissionAssignment, Number> permIdCol;
    @FXML private TableColumn<PermissionAssignment, String> permRoleCol;
    @FXML private TableColumn<PermissionAssignment, String> permFunctionCol;
    @FXML private TableColumn<PermissionAssignment, Boolean> permViewCol;
    @FXML private TableColumn<PermissionAssignment, Boolean> permOperateCol;

    private IdentityComponent identityComponent;
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<RoleGroup> roles = FXCollections.observableArrayList();
    private final ObservableList<PermissionAssignment> permissions = FXCollections.observableArrayList();
    private final Map<Integer, String> roleNameById = new HashMap<>();
    private final Map<Integer, String> functionNameById = new HashMap<>();

    @FXML
    public void initialize() {
        userIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        userRoleCol.setCellValueFactory(c -> new SimpleStringProperty(roleNameById.getOrDefault(c.getValue().getRoleGroupId(), "N/A")));
        usersTable.setItems(users);

        roleIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        roleNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        roleDescCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        rolesTable.setItems(roles);

        permIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        permRoleCol.setCellValueFactory(c -> new SimpleStringProperty(roleNameById.getOrDefault(c.getValue().getRoleGroupId(), "N/A")));
        permFunctionCol.setCellValueFactory(c -> new SimpleStringProperty(functionNameById.getOrDefault(c.getValue().getFunctionId(), "N/A")));
        permViewCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanView()));
        permOperateCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanOperate()));
        permissionsTable.setItems(permissions);
    }

    public void setIdentityComponent(IdentityComponent identityComponent) {
        this.identityComponent = identityComponent;
        refreshAll();
    }

    @FXML
    private void onRefreshAll() {
        refreshAll();
    }

    @FXML
    private void onAddUser() {
        if (identityComponent == null) return;
        Dialog<UserFormResult> dialog = createUserDialog(null);
        Optional<UserFormResult> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        UserFormResult input = result.get();
        try {
            identityComponent.createUser(new ManageUserInput(input.username(), input.password(), input.roleGroupId()));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
            return;
        }
        refreshAll();
    }

    @FXML
    private void onEditUser() {
        if (identityComponent == null) return;
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn user để sửa.");
            return;
        }
        Dialog<UserFormResult> dialog = createUserDialog(selected);
        Optional<UserFormResult> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        UserFormResult input = result.get();
        try {
            identityComponent.updateUser(selected.getId(), new ManageUserInput(input.username(), input.password(), input.roleGroupId()));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
            return;
        }
        refreshAll();
    }

    @FXML
    private void onDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn user để xóa.");
            return;
        }
        if (!confirm("Xóa user '" + selected.getUsername() + "'?")) return;
        identityComponent.deleteUser(selected.getId());
        refreshAll();
    }

    @FXML
    private void onAddRole() {
        Dialog<RoleFormResult> dialog = createRoleDialog(null);
        Optional<RoleFormResult> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        RoleFormResult input = result.get();
        try {
            identityComponent.createRoleGroup(new ManageRoleGroupInput(input.name(), input.description(), Set.of()));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
            return;
        }
        refreshAll();
    }

    @FXML
    private void onEditRole() {
        RoleGroup selected = rolesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn nhóm quyền để sửa.");
            return;
        }
        Set<Integer> existingFunctions = identityComponent.getFunctionsByRoleGroup(selected.getId()).stream()
                .map(Function::getId).collect(java.util.stream.Collectors.toSet());
        Dialog<RoleFormResult> dialog = createRoleDialog(selected);
        Optional<RoleFormResult> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        RoleFormResult input = result.get();
        try {
            identityComponent.updateRoleGroup(
                    selected.getId(),
                    new ManageRoleGroupInput(input.name(), input.description(), existingFunctions));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
            return;
        }
        refreshAll();
    }

    @FXML
    private void onDeleteRole() {
        RoleGroup selected = rolesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn nhóm quyền để xóa.");
            return;
        }
        if (!confirm("Xóa nhóm quyền '" + selected.getName() + "'?")) return;
        identityComponent.deleteRoleGroup(selected.getId());
        refreshAll();
    }

    @FXML
    private void onAddPermission() {
        Dialog<PermissionFormResult> dialog = createPermissionDialog(null);
        Optional<PermissionFormResult> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        PermissionFormResult input = result.get();
        try {
            identityComponent.createPermissionAssignment(
                    new ManagePermissionAssignmentInput(input.roleGroupId(), input.functionId(), input.canView(), input.canOperate()));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
            return;
        }
        refreshAll();
    }

    @FXML
    private void onEditPermission() {
        PermissionAssignment selected = permissionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn phân quyền để sửa.");
            return;
        }
        Dialog<PermissionFormResult> dialog = createPermissionDialog(selected);
        Optional<PermissionFormResult> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        PermissionFormResult input = result.get();
        try {
            identityComponent.updatePermissionAssignment(
                    selected.getId(),
                    new ManagePermissionAssignmentInput(input.roleGroupId(), input.functionId(), input.canView(), input.canOperate()));
        } catch (DomainException e) {
            showError(DomainMessages.format(e));
            return;
        }
        refreshAll();
    }

    @FXML
    private void onDeletePermission() {
        PermissionAssignment selected = permissionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Vui lòng chọn phân quyền để xóa.");
            return;
        }
        if (!confirm("Xóa phân quyền ID " + selected.getId() + "?")) return;
        identityComponent.deletePermissionAssignment(selected.getId());
        refreshAll();
    }

    private void refreshAll() {
        if (identityComponent == null) return;
        roles.setAll(identityComponent.getAllRoleGroups());
        roleNameById.clear();
        for (RoleGroup role : roles) roleNameById.put(role.getId(), role.getName());

        functionNameById.clear();
        for (Function function : identityComponent.getAllFunctions()) {
            functionNameById.put(function.getId(), function.getName());
        }

        users.setAll(identityComponent.getAllUsers());
        permissions.setAll(identityComponent.getAllPermissionAssignments());
        usersTable.refresh();
        permissionsTable.refresh();
    }

    private Dialog<UserFormResult> createUserDialog(User existing) {
        Dialog<UserFormResult> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Thêm user" : "Sửa user");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField usernameField = new TextField(existing == null ? "" : existing.getUsername());
        PasswordField passwordField = new PasswordField();
        ComboBox<RoleGroup> roleCombo = new ComboBox<>(FXCollections.observableArrayList(roles));
        roleCombo.setCellFactory(v -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(RoleGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        roleCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(RoleGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        if (existing != null) {
            roleCombo.getItems().stream().filter(r -> r.getId() == existing.getRoleGroupId()).findFirst().ifPresent(roleCombo::setValue);
        } else if (!roleCombo.getItems().isEmpty()) {
            roleCombo.setValue(roleCombo.getItems().get(0));
        }

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.addRow(0, new Label("Username"), usernameField);
        gp.addRow(1, new Label("Password"), passwordField);
        gp.addRow(2, new Label("Nhóm quyền"), roleCombo);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
            String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
            RoleGroup role = roleCombo.getValue();
            if (username.isEmpty() || password.isEmpty() || role == null) {
                showError("Username, password và nhóm quyền là bắt buộc.");
                return null;
            }
            return new UserFormResult(username, password, role.getId());
        });
        return dialog;
    }

    private Dialog<RoleFormResult> createRoleDialog(RoleGroup existing) {
        Dialog<RoleFormResult> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Thêm nhóm quyền" : "Sửa nhóm quyền");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(existing == null ? "" : existing.getName());
        TextField descField = new TextField(existing == null ? "" : existing.getDescription());
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.addRow(0, new Label("Tên nhóm"), nameField);
        gp.addRow(1, new Label("Mô tả"), descField);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Tên nhóm quyền không được để trống.");
                return null;
            }
            return new RoleFormResult(name, descField.getText() == null ? "" : descField.getText().trim());
        });
        return dialog;
    }

    private Dialog<PermissionFormResult> createPermissionDialog(PermissionAssignment existing) {
        Dialog<PermissionFormResult> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Thêm phân quyền" : "Sửa phân quyền");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<RoleGroup> roleCombo = new ComboBox<>(FXCollections.observableArrayList(roles));
        roleCombo.setCellFactory(v -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(RoleGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        roleCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(RoleGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        ObservableList<Function> functions = FXCollections.observableArrayList(identityComponent.getAllFunctions());
        ComboBox<Function> functionCombo = new ComboBox<>(functions);
        functionCombo.setCellFactory(v -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Function item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        functionCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Function item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        CheckBox canView = new CheckBox("Can View");
        CheckBox canOperate = new CheckBox("Can Operate");

        if (existing != null) {
            roleCombo.getItems().stream().filter(r -> r.getId() == existing.getRoleGroupId()).findFirst().ifPresent(roleCombo::setValue);
            functionCombo.getItems().stream().filter(f -> f.getId() == existing.getFunctionId()).findFirst().ifPresent(functionCombo::setValue);
            canView.setSelected(existing.isCanView());
            canOperate.setSelected(existing.isCanOperate());
        } else {
            if (!roleCombo.getItems().isEmpty()) roleCombo.setValue(roleCombo.getItems().get(0));
            if (!functionCombo.getItems().isEmpty()) functionCombo.setValue(functionCombo.getItems().get(0));
            canView.setSelected(true);
        }

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.addRow(0, new Label("Nhóm quyền"), roleCombo);
        gp.addRow(1, new Label("Chức năng"), functionCombo);
        gp.addRow(2, canView, canOperate);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            RoleGroup role = roleCombo.getValue();
            Function function = functionCombo.getValue();
            if (role == null || function == null) {
                showError("Nhóm quyền và chức năng là bắt buộc.");
                return null;
            }
            return new PermissionFormResult(role.getId(), function.getId(), canView.isSelected(), canOperate.isSelected());
        });
        return dialog;
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private record UserFormResult(String username, String password, int roleGroupId) {}
    private record RoleFormResult(String name, String description) {}
    private record PermissionFormResult(int roleGroupId, int functionId, boolean canView, boolean canOperate) {}
}
