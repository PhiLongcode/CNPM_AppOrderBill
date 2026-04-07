package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.service.ExcelService;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportMenuFromExcelInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportMenuFromExcelOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

public class ImportMenuFromExcelUseCase {
    private final MenuItemRepository repository;
    private final ExcelService excelService;

    public ImportMenuFromExcelUseCase(MenuItemRepository repository, ExcelService excelService) {
        this.repository = repository;
        this.excelService = excelService;
    }

    public ImportMenuFromExcelOutput execute(ImportMenuFromExcelInput input) {
        if (excelService == null) {
            throw new DomainException(ErrorCode.MENU_IMPORT_EXCEL_NOT_CONFIGURED);
        }
        try {
            var imported = excelService.importMenu(input == null ? null : input.getFilePath());
            if (imported != null) {
                imported.forEach(repository::save);
            }
            return new ImportMenuFromExcelOutput(imported == null ? 0 : imported.size());
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, null, e.getMessage(), null);
        }
    }
}
