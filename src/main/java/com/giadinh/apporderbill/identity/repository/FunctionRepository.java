package com.giadinh.apporderbill.identity.repository;

import com.giadinh.apporderbill.identity.model.Function;
import java.util.List;
import java.util.Optional;

public interface FunctionRepository {
    Optional<Function> findById(int id);
    List<Function> findAll();
    List<Function> findByModuleId(int moduleId);
    void save(Function function);
    void delete(int id);
}
