package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.service.ExcelService;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelInput;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelOutput;

public class ImportMenuFromExcelUseCase {
    private final MenuItemRepository repository;
    private final ExcelService excelService;

    public ImportMenuFromExcelUseCase(MenuItemRepository repository, ExcelService excelService) {
        this.repository = repository;
        this.excelService = excelService;
    }

    public ImportMenuFromExcelOutput execute(ImportMenuFromExcelInput input) {
        if (excelService == null) {
            return new ImportMenuFromExcelOutput(false, "Chua cau hinh ExcelService.", 0);
        }
        var imported = excelService.importMenu(input == null ? null : input.getFilePath());
        if (imported != null) {
            imported.forEach(repository::save);
        }
        int count = imported == null ? 0 : imported.size();
        return new ImportMenuFromExcelOutput(true, "Import menu thanh cong.", count);
    }
}

