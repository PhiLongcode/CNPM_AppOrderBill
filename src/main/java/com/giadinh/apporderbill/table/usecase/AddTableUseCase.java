package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;
import com.giadinh.apporderbill.table.usecase.dto.AddTableOutput;

public class AddTableUseCase {
    private final TableRepository tableRepository;

    public AddTableUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public AddTableOutput execute(AddTableInput input) {
        if (input == null || input.getTableName() == null || input.getTableName().isBlank()) {
            return new AddTableOutput(false, "Tên bàn không hợp lệ.", null);
        }
        if (tableRepository.findByTableName(input.getTableName()).isPresent()) {
            return new AddTableOutput(false, "Bàn đã tồn tại.", null);
        }
        Table table = new Table(input.getTableName().trim());
        tableRepository.save(table);
        return new AddTableOutput(true, "Tạo bàn thành công.", table.getTableId());
    }
}

