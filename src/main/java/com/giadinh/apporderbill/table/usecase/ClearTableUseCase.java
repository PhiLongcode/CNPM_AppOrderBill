package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.ClearTableInput;

public class ClearTableUseCase {
    private final TableRepository tableRepository;

    public ClearTableUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void execute(ClearTableInput input) {
        if (input == null || input.getTableId() == null) {
            throw new IllegalArgumentException("Thiếu tableId.");
        }
        var table = tableRepository.findById(input.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn."));
        table.clearTable();
        tableRepository.save(table);
    }
}

