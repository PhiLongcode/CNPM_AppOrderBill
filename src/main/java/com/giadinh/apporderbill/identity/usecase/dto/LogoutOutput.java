package com.giadinh.apporderbill.identity.usecase.dto;

/** Result of a confirmed logout (username echo; room for audit fields later). */
public class LogoutOutput {
    private final String username;

    public LogoutOutput(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
