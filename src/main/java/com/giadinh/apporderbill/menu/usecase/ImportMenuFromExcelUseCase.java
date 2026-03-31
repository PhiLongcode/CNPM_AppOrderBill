package com.giadinh.apporderbill.menu.usecase;

import com.giadinh.apporderbill.menu.repository.MenuItemRepository;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelInput;
import com.giadinh.apporderbill.menu.usecase.dto.ImportMenuFromExcelOutput;

public class ImportMenuFromExcelUseCase {
    private final MenuItemRepository repository;
    private final Object excelService;

    public ImportMenuFromExcelUseCase(MenuItemRepository repository, Object excelService) {
        this.repository = repository;
        this.excelService = excelService;
    }

    public ImportMenuFromExcelOutput execute(ImportMenuFromExcelInput input) {
        int currentSize = repository.findAll().size();
        String msg = excelService == null
                ? "Chức năng import đang dùng dữ liệu catalog hiện có."
                : "Đã nhận yêu cầu import với excel service.";
        return new ImportMenuFromExcelOutput(true, msg, currentSize);
    }
}

