package com.giadinh.apporderbill.menu.usecase.dto;

public class ImportMenuFromExcelOutput {
    private final boolean success;
    private final String message;
    private final int importedCount;

    public ImportMenuFromExcelOutput(boolean success, String message, int importedCount) {
        this.success = success;
        this.message = message;
        this.importedCount = importedCount;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getImportedCount() { return importedCount; }
}

