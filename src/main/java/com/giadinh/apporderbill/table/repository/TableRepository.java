package com.giadinh.apporderbill.table.repository;

import com.giadinh.apporderbill.table.model.Table;
import java.util.List;
import java.util.Optional;

public interface TableRepository {
    Optional<Table> findById(String tableId);
    Optional<Table> findByTableName(String tableName);
    List<Table> findAll();
    void save(Table table);
    void delete(String tableId);
}
