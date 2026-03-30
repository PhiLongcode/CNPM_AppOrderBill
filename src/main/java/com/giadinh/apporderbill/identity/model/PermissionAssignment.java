package com.giadinh.apporderbill.identity.model;

import java.util.Objects;

public class PermissionAssignment {
    private int id;
    private int roleGroupId; // Foreign key to RoleGroup
    private int functionId;  // Foreign key to Function
    private boolean canView;    // View permission (true/false)
    private boolean canOperate; // Operate permission (true/false)

    public PermissionAssignment(int id, int roleGroupId, int functionId, boolean canView, boolean canOperate) {
        this.id = id;
        this.roleGroupId = roleGroupId;
        this.functionId = functionId;
        this.canView = canView;
        this.canOperate = canOperate;
    }

    public int getId() {
        return id;
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

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public void setCanOperate(boolean canOperate) {
        this.canOperate = canOperate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionAssignment that = (PermissionAssignment) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PermissionAssignment{" +
               "id=" + id +
               ", roleGroupId=" + roleGroupId +
               ", functionId=" + functionId +
               ", canView=" + canView +
               ", canOperate=" + canOperate +
               '}';
    }
}
