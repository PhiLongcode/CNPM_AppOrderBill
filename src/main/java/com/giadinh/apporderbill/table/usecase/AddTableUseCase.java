package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;

public class AddTableUseCase {
    private final TableRepository tableRepository;

    public AddTableUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public String execute(AddTableInput input) {
        if (input == null || input.getTableName() == null || input.getTableName().isBlank()) {
            throw new DomainException(ErrorCode.TABLE_ADD_NAME_INVALID);
        }
        String name = input.getTableName().trim();
        if (tableRepository.findByTableName(name).isPresent()) {
            throw new DomainException(ErrorCode.TABLE_NAME_DUPLICATE);
        }
        Table table = new Table(name);
        tableRepository.save(table);
        return table.getTableId();
    }
}
