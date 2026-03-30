package com.giadinh.apporderbill.identity.model;

import java.util.Objects;

public class User {
    private int id;
    private String username;
    private String passwordHash; // In practice, store as hash
    private int roleGroupId; // Foreign key to RoleGroup

    public User(int id, String username, String passwordHash, int roleGroupId) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleGroupId = roleGroupId;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getRoleGroupId() {
        return roleGroupId;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRoleGroupId(int roleGroupId) {
        this.roleGroupId = roleGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", passwordHash='" + passwordHash + '\'' +
               ", roleGroupId=" + roleGroupId +
               '}';
    }
}
