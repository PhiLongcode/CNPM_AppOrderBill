package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.service.ExcelService;

public class ExportMenuToExcelUseCase {
    private final MenuItemRepository repository;
    private final ExcelService excelService;

    public ExportMenuToExcelUseCase(MenuItemRepository repository, ExcelService excelService) {
        this.repository = repository;
        this.excelService = excelService;
    }

    public void execute(String filePath) {
        if (excelService == null) {
            return;
        }
        excelService.exportMenu(repository.findAll(), filePath);
    }
}
