package com.giadinh.apporderbill.identity.usecase.dto;

public class ManageUserInput {
    private String username;
    private String password; // Raw password, will be hashed in UseCase
    private int roleGroupId;

    public ManageUserInput(String username, String password, int roleGroupId) {
        this.username = username;
        this.password = password;
        this.roleGroupId = roleGroupId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRoleGroupId() {
        return roleGroupId;
    }
}
