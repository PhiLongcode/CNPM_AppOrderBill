package com.giadinh.apporderbill.identity.repository;

import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import java.util.List;
import java.util.Optional;

public interface PermissionAssignmentRepository {
    Optional<PermissionAssignment> findById(int id);
    Optional<PermissionAssignment> findByRoleGroupAndFunction(int roleGroupId, int functionId);
    List<PermissionAssignment> findAll();
    List<PermissionAssignment> findByRoleGroupId(int roleGroupId);
    void save(PermissionAssignment permissionAssignment);
    void delete(int id);
}
