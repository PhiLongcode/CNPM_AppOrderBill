package com.giadinh.apporderbill.catalog.usecase.dto;

public class ImportMenuFromExcelOutput {
    private final int importedCount;
    public ImportMenuFromExcelOutput(int importedCount) { this.importedCount = importedCount; }
    public int getImportedCount() { return importedCount; }
}
