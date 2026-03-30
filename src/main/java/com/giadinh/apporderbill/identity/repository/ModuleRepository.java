package com.giadinh.apporderbill.identity.repository;

import com.giadinh.apporderbill.identity.model.Module;
import java.util.List;
import java.util.Optional;

public interface ModuleRepository {
    Optional<Module> findById(int id);
    List<Module> findAll();
    void save(Module module);
    void delete(int id);
}
