package com.giadinh.apporderbill.identity.usecase.dto;

public class LoginInput {
    private String username;
    private String password;

    public LoginInput(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
