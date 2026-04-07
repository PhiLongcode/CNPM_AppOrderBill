package com.giadinh.apporderbill.identity.model;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.Objects;
import java.util.UUID;

public class Permission {
    private String permissionId;
    private String name; // Tên quyền (ví dụ: "manage_menu", "create_order", "view_reports")
    private String description; // Mô tả quyền

    public Permission(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException(ErrorCode.PERMISSION_NAME_REQUIRED);
        }
        this.permissionId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
    }

    // Constructor cho việc tải từ Repository
    public Permission(String permissionId, String name, String description) {
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Permission{" +
               "permissionId='" + permissionId + '\'' +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
