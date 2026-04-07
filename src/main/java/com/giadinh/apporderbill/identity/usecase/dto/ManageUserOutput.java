package com.giadinh.apporderbill.identity.usecase.dto;

public class ManageUserOutput {
    private final int userId;

    public ManageUserOutput(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
