package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;

public class ExportMenuToExcelUseCase {
    private final MenuItemRepository repository;
    private final Object excelService;

    public ExportMenuToExcelUseCase(MenuItemRepository repository, Object excelService) {
        this.repository = repository;
        this.excelService = excelService;
    }

    public void execute() {
        int size = repository.findAll().size();
        if (excelService == null && size >= 0) {
            return;
        }
    }
}

