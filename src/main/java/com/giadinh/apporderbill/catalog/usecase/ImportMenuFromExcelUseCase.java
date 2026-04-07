package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.service.ExcelService;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportConflictStrategy;
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
            ImportConflictStrategy strategy = input == null ? ImportConflictStrategy.CREATE_NEW_ID : input.getConflictStrategy();
            int importedCount = 0;
            if (imported != null) {
                for (var item : imported) {
                    if (item.getId() > 0 && repository.findById(item.getId()).isPresent()) {
                        if (strategy == ImportConflictStrategy.KEEP_EXISTING) {
                            continue;
                        }
                        if (strategy == ImportConflictStrategy.CREATE_NEW_ID) {
                            item = new com.giadinh.apporderbill.catalog.model.MenuItem(
                                    0,
                                    item.getName(),
                                    item.getPrice(),
                                    item.getCategoryName(),
                                    item.getImageUrl(),
                                    item.isStockManaged(),
                                    item.getCurrentStockQuantity(),
                                    item.getMinStockQuantity(),
                                    item.getMaxStockQuantity(),
                                    item.getUnitOfMeasureName(),
                                    item.getStatus());
                        }
                    }
                    repository.save(item);
                    importedCount++;
                }
            }
            return new ImportMenuFromExcelOutput(importedCount);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, null, e.getMessage(), null);
        }
    }
}
