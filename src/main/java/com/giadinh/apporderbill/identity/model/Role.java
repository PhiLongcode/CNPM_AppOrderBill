package com.giadinh.apporderbill.identity.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Role {
    private String roleId;
    private String name; // Tên vai trò (ví dụ: "ADMIN", "CASHIER")
    private String description; // Mô tả vai trò
    private Set<Permission> permissions; // Tập hợp các quyền của vai trò này

    public Role(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên vai trò không được trống.");
        }
        this.roleId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.permissions = new HashSet<>();
    }

    // Constructor cho việc tải từ Repository
    public Role(String roleId, String name, String description) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.permissions = new HashSet<>(); // Quyền sẽ được load riêng bởi Repository
    }

    public String getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Trả về một tập hợp quyền không thể sửa đổi
    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Thêm một quyền vào vai trò
    public void addPermission(Permission permission) {
        Objects.requireNonNull(permission, "Quyền không được null.");
        this.permissions.add(permission);
    }

    // Xóa một quyền khỏi vai trò
    public void removePermission(Permission permission) {
        Objects.requireNonNull(permission, "Quyền không được null.");
        this.permissions.remove(permission);
    }

    // Kiểm tra xem vai trò có quyền cụ thể không
    public boolean hasPermission(String permissionName) {
        return permissions.stream().anyMatch(p -> p.getName().equals(permissionName));
    }

    // Cập nhật tập hợp quyền (dùng khi tải từ DB)
    public void restorePermissions(Set<Permission> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
               "roleId='" + roleId + '\'' +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", permissions=" + permissions.size() +
               '}';
    }
}
