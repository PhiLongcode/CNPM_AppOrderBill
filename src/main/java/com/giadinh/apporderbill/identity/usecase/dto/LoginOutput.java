package com.giadinh.apporderbill.identity.usecase.dto;

import com.giadinh.apporderbill.identity.model.PermissionAssignment;

import java.util.Set;

public class LoginOutput {
    private final int userId;
    private final String username;
    private final String roleGroupName;
    private final Set<PermissionAssignment> permissions;

    public LoginOutput(int userId, String username, String roleGroupName, Set<PermissionAssignment> permissions) {
        this.userId = userId;
        this.username = username;
        this.roleGroupName = roleGroupName;
        this.permissions = permissions;
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
