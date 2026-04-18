package com.giadinh.apporderbill.identity.usecase.dto;

/** Logout request (current username). */
public class LogoutInput {
    private final String username;

    public LogoutInput(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
