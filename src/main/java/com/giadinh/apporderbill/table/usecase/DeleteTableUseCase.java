package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.DeleteTableInput;

public class DeleteTableUseCase {
    private final TableRepository tableRepository;

    public DeleteTableUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void execute(DeleteTableInput input) {
        if (input == null || input.getTableId() == null) {
            throw new IllegalArgumentException("Thiếu tableId.");
        }
        tableRepository.delete(input.getTableId());
    }
}

