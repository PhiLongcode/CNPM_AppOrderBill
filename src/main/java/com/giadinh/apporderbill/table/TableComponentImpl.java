package com.giadinh.apporderbill.table;

import com.giadinh.apporderbill.table.repository.TableRepository;
import com.giadinh.apporderbill.table.usecase.AddTableUseCase;
import com.giadinh.apporderbill.table.usecase.ClearTableUseCase;
import com.giadinh.apporderbill.table.usecase.DeleteTableUseCase;
import com.giadinh.apporderbill.table.usecase.GetAllTablesUseCase;
import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;
import com.giadinh.apporderbill.table.usecase.dto.AddTableOutput;
import com.giadinh.apporderbill.table.usecase.dto.ClearTableInput;
import com.giadinh.apporderbill.table.usecase.dto.DeleteTableInput;
import com.giadinh.apporderbill.table.usecase.dto.TableOutput;

import java.util.List;

public class TableComponentImpl implements TableComponent {
    private final AddTableUseCase addTableUseCase;
    private final DeleteTableUseCase deleteTableUseCase;
    private final ClearTableUseCase clearTableUseCase;
    private final GetAllTablesUseCase getAllTablesUseCase;

    public TableComponentImpl(TableRepository tableRepository, Object orderRepository) {
        this.addTableUseCase = new AddTableUseCase(tableRepository);
        this.deleteTableUseCase = new DeleteTableUseCase(tableRepository);
        this.clearTableUseCase = new ClearTableUseCase(tableRepository);
        this.getAllTablesUseCase = new GetAllTablesUseCase(tableRepository);
    }

    @Override
    public List<TableOutput> getAllTables() {
        return getAllTablesUseCase.execute();
    }

    @Override
    public AddTableOutput addTable(AddTableInput input) {
        return new AddTableOutput(addTableUseCase.execute(input));
    }

    @Override
    public void deleteTable(DeleteTableInput input) {
        deleteTableUseCase.execute(input);
    }

    @Override
    public void clearTable(ClearTableInput input) {
        clearTableUseCase.execute(input);
    }
}

