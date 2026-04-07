package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.DeleteTableInput;

public class DeleteTableUseCase {
    private final TableRepository tableRepository;

    public DeleteTableUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void execute(DeleteTableInput input) {
        if (input == null || input.getTableId() == null) {
            throw new DomainException(ErrorCode.TABLE_ID_REQUIRED);
        }
        tableRepository.delete(input.getTableId());
    }
}

