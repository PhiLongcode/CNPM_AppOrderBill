package com.giadinh.apporderbill.identity;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.Module;
import com.giadinh.apporderbill.identity.usecase.dto.LoginInput;
import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentOutput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupOutput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserOutput;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * IdentityComponent là giao diện chính để tương tác với Identity & Administration Bounded Context.
 * Nó cung cấp các phương thức để quản lý người dùng, vai trò, quyền hạn và xác thực.
 */
public interface IdentityComponent {

    // -- Authentication & Authorization Use Cases --
    LoginOutput login(LoginInput input);
    boolean checkAccess(String username, String functionName, boolean requiresOperatePermission);
    
    // -- User Management Use Cases --
    List<User> getAllUsers();
    Optional<User> getUserById(int userId);
    ManageUserOutput createUser(ManageUserInput input);
    ManageUserOutput updateUser(int userId, ManageUserInput input);
    void deleteUser(int userId);

    // -- Role Group Management Use Cases --
    List<RoleGroup> getAllRoleGroups();
    Optional<RoleGroup> getRoleGroupById(int roleGroupId);
    ManageRoleGroupOutput createRoleGroup(ManageRoleGroupInput input);
    ManageRoleGroupOutput updateRoleGroup(int roleGroupId, ManageRoleGroupInput input);
    void deleteRoleGroup(int roleGroupId);
    Set<Function> getFunctionsByRoleGroup(int roleGroupId);

    // -- Permission Assignment Management Use Cases --
    List<PermissionAssignment> getAllPermissionAssignments();
    Optional<PermissionAssignment> getPermissionAssignmentById(int assignmentId);
    ManagePermissionAssignmentOutput createPermissionAssignment(ManagePermissionAssignmentInput input);
    ManagePermissionAssignmentOutput updatePermissionAssignment(int assignmentId, ManagePermissionAssignmentInput input);
    void deletePermissionAssignment(int assignmentId);

    // -- Module & Function Management Use Cases --
    List<Module> getAllModules();
    Optional<Module> getModuleById(int moduleId);
    List<Function> getAllFunctions();
    Optional<Function> getFunctionById(int functionId);
    List<Function> getFunctionsByModuleId(int moduleId);
}
