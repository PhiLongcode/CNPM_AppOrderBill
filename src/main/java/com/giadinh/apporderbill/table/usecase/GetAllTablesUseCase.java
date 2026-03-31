package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.dto.TableOutput;

import java.util.List;

public class GetAllTablesUseCase {
    private final TableRepository tableRepository;

    public GetAllTablesUseCase(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<TableOutput> execute() {
        return tableRepository.findAll().stream()
                .map(t -> new TableOutput(
                        t.getTableId(),
                        t.getTableName(),
                        String.valueOf(t.getStatus()),
                        t.getCurrentOrderId()))
                .toList();
    }
}

