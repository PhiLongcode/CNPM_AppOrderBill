package com.giadinh.apporderbill.identity.usecase.dto;

public class ManagePermissionAssignmentInput {
    private int roleGroupId;
    private int functionId;
    private boolean canView;
    private boolean canOperate;

    public ManagePermissionAssignmentInput(int roleGroupId, int functionId, boolean canView, boolean canOperate) {
        this.roleGroupId = roleGroupId;
        this.functionId = functionId;
        this.canView = canView;
        this.canOperate = canOperate;
    }

    public int getRoleGroupId() {
        return roleGroupId;
    }

    public int getFunctionId() {
        return functionId;
    }

    public boolean isCanView() {
        return canView;
    }

    public boolean isCanOperate() {
        return canOperate;
    }
}
