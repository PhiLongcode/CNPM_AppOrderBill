package com.giadinh.apporderbill.identity.usecase.dto;

public class ManagePermissionAssignmentOutput {
    private final int assignmentId;

    public ManagePermissionAssignmentOutput(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }
}
