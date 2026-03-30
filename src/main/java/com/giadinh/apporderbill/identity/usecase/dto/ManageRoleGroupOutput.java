package com.giadinh.apporderbill.identity.usecase.dto;

public class ManageRoleGroupOutput {
    private boolean success;
    private String message;
    private int roleGroupId;

    public ManageRoleGroupOutput(boolean success, String message, int roleGroupId) {
        this.success = success;
        this.message = message;
        this.roleGroupId = roleGroupId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getRoleGroupId() {
        return roleGroupId;
    }
}
