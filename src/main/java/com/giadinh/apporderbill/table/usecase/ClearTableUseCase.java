package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.ClearTableInput;

public class ClearTableUseCase {
    private final TableRepository tableRepository;

    public ClearTableUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void execute(ClearTableInput input) {
        if (input == null || input.getTableId() == null) {
            throw new DomainException(ErrorCode.TABLE_ID_REQUIRED);
        }
        var table = tableRepository.findById(input.getTableId())
                .orElseThrow(() -> new DomainException(ErrorCode.TABLE_NOT_FOUND));
        table.clearTable();
        tableRepository.save(table);
    }
}

