package com.giadinh.apporderbill.catalog.usecase.dto;

public class ImportMenuFromExcelInput {
    private String filePath;
    private ImportConflictStrategy conflictStrategy = ImportConflictStrategy.CREATE_NEW_ID;
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public ImportConflictStrategy getConflictStrategy() { return conflictStrategy; }
    public void setConflictStrategy(ImportConflictStrategy conflictStrategy) {
        this.conflictStrategy = conflictStrategy == null ? ImportConflictStrategy.CREATE_NEW_ID : conflictStrategy;
    }
}
