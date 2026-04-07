package com.giadinh.apporderbill.identity.usecase.dto;

public class ManageRoleGroupOutput {
    private final int roleGroupId;

    public ManageRoleGroupOutput(int roleGroupId) {
        this.roleGroupId = roleGroupId;
    }

    public int getRoleGroupId() {
        return roleGroupId;
    }
}
