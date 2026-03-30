package com.giadinh.apporderbill.identity.usecase.dto;

import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.RoleGroup;

import java.util.List;
import java.util.Set;

public class LoginOutput {
    private boolean success;
    private String message;
    private int userId; // ID người dùng nếu đăng nhập thành công
    private String username; // Tên đăng nhập
    private String roleGroupName; // Tên nhóm quyền
    private Set<PermissionAssignment> permissions; // Các quyền được gán trực tiếp cho nhóm quyền

    public LoginOutput(boolean success, String message, int userId, String username, String roleGroupName, Set<PermissionAssignment> permissions) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.roleGroupName = roleGroupName;
        this.permissions = permissions;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRoleGroupName() {
        return roleGroupName;
    }

    public Set<PermissionAssignment> getPermissions() {
        return permissions;
    }
}
