package com.giadinh.apporderbill.table.repository;

import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;
import com.giadinh.apporderbill.table.model.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SqliteTableRepository implements TableRepository {
    private final ConcurrentMap<String, Table> store = new ConcurrentHashMap<>();

    public SqliteTableRepository(SqliteConnectionProvider connectionProvider) {
    }

    @Override
    public Optional<Table> findById(String tableId) { return Optional.ofNullable(store.get(tableId)); }
    @Override
    public Optional<Table> findByTableName(String tableName) {
        return store.values().stream().filter(t -> t.getTableName().equalsIgnoreCase(tableName)).findFirst();
    }
    @Override
    public List<Table> findAll() { return new ArrayList<>(store.values()); }
    @Override
    public void save(Table table) { store.put(table.getTableId(), table); }
    @Override
    public void delete(String tableId) { store.remove(tableId); }
}

