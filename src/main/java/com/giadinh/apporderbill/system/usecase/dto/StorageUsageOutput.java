package com.giadinh.apporderbill.system.usecase.dto;

public class StorageUsageOutput {
    private final String summary;

    public StorageUsageOutput(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}

