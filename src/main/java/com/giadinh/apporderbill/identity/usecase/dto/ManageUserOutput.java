package com.giadinh.apporderbill.identity.usecase.dto;

public class ManageUserOutput {
    private boolean success;
    private String message;
    private int userId;

    public ManageUserOutput(boolean success, String message, int userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }
}
