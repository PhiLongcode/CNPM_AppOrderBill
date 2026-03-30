package com.giadinh.apporderbill.identity.repository;

import com.giadinh.apporderbill.identity.model.RoleGroup;
import java.util.List;
import java.util.Optional;

public interface RoleGroupRepository {
    Optional<RoleGroup> findById(int id);
    Optional<RoleGroup> findByName(String name);
    List<RoleGroup> findAll();
    void save(RoleGroup roleGroup);
    void delete(int id);
}
