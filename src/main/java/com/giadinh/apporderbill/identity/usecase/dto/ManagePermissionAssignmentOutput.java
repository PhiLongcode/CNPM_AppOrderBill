package com.giadinh.apporderbill.identity.usecase.dto;

public class ManagePermissionAssignmentOutput {
    private boolean success;
    private String message;
    private int assignmentId; // ID của phân quyền được tạo/cập nhật

    public ManagePermissionAssignmentOutput(boolean success, String message, int assignmentId) {
        this.success = success;
        this.message = message;
        this.assignmentId = assignmentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getAssignmentId() {
        return assignmentId;
    }
}
