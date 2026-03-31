package com.giadinh.apporderbill.table;

import com.giadinh.apporderbill.table.usecase.dto.AddTableInput;
import com.giadinh.apporderbill.table.usecase.dto.AddTableOutput;
import com.giadinh.apporderbill.table.usecase.dto.ClearTableInput;
import com.giadinh.apporderbill.table.usecase.dto.DeleteTableInput;
import com.giadinh.apporderbill.table.usecase.dto.TableOutput;

import java.util.List;

public interface TableComponent {
    List<TableOutput> getAllTables();
    AddTableOutput addTable(AddTableInput input);
    void deleteTable(DeleteTableInput input);
    void clearTable(ClearTableInput input);
}

