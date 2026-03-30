package com.giadinh.apporderbill.identity;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.Module;
import com.giadinh.apporderbill.identity.repository.ModuleRepository;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.usecase.LoginUseCase;
import com.giadinh.apporderbill.identity.usecase.CheckAccessUseCase;
import com.giadinh.apporderbill.identity.usecase.ManageUserUseCase;
import com.giadinh.apporderbill.identity.usecase.ManageRoleGroupUseCase;
import com.giadinh.apporderbill.identity.usecase.ManagePermissionAssignmentUseCase;

import com.giadinh.apporderbill.identity.usecase.dto.LoginInput;
import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentOutput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupOutput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserOutput;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class IdentityComponentImpl implements IdentityComponent {

    // Repositories
    private final ModuleRepository moduleRepository;
    private final FunctionRepository functionRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final PermissionAssignmentRepository permissionAssignmentRepository;
    private final UserRepository userRepository;

    // Use Cases
    private final LoginUseCase loginUseCase;
    private final CheckAccessUseCase checkAccessUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final ManageRoleGroupUseCase manageRoleGroupUseCase;
    private final ManagePermissionAssignmentUseCase managePermissionAssignmentUseCase;

    public IdentityComponentImpl(ModuleRepository moduleRepository,
                                 FunctionRepository functionRepository,
                                 RoleGroupRepository roleGroupRepository,
                                 PermissionAssignmentRepository permissionAssignmentRepository,
                                 UserRepository userRepository) {
        this.moduleRepository = moduleRepository;
        this.functionRepository = functionRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.userRepository = userRepository;

        // Initialize Use Cases (This would ideally be done via DI as well)
        this.loginUseCase = new LoginUseCase(userRepository, roleGroupRepository, permissionAssignmentRepository, functionRepository);
        this.checkAccessUseCase = new CheckAccessUseCase(userRepository, roleGroupRepository, permissionAssignmentRepository, functionRepository);
        this.manageUserUseCase = new ManageUserUseCase(userRepository, roleGroupRepository);
        this.manageRoleGroupUseCase = new ManageRoleGroupUseCase(roleGroupRepository, permissionAssignmentRepository, functionRepository);
        this.managePermissionAssignmentUseCase = new ManagePermissionAssignmentUseCase(permissionAssignmentRepository, roleGroupRepository, functionRepository);
    }

    @Override
    public LoginOutput login(LoginInput input) {
        return loginUseCase.execute(input);
    }

    @Override
    public boolean checkAccess(String username, String functionName, boolean requiresOperatePermission) {
        return checkAccessUseCase.execute(username, functionName, requiresOperatePermission);
    }

    // -- User Management --
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    @Override
    public ManageUserOutput createUser(ManageUserInput input) {
        return manageUserUseCase.create(input);
    }

    @Override
    public ManageUserOutput updateUser(int userId, ManageUserInput input) {
        return manageUserUseCase.update(userId, input);
    }

    @Override
    public void deleteUser(int userId) {
        manageUserUseCase.delete(userId);
    }

    // -- Role Group Management --
    @Override
    public List<RoleGroup> getAllRoleGroups() {
        return roleGroupRepository.findAll();
    }

    @Override
    public Optional<RoleGroup> getRoleGroupById(int roleGroupId) {
        return roleGroupRepository.findById(roleGroupId);
    }

    @Override
    public ManageRoleGroupOutput createRoleGroup(ManageRoleGroupInput input) {
        return manageRoleGroupUseCase.create(input);
    }

    @Override
    public ManageRoleGroupOutput updateRoleGroup(int roleGroupId, ManageRoleGroupInput input) {
        return manageRoleGroupUseCase.update(roleGroupId, input);
    }

    @Override
    public void deleteRoleGroup(int roleGroupId) {
        manageRoleGroupUseCase.delete(roleGroupId);
    }

    @Override
    public Set<Function> getFunctionsByRoleGroup(int roleGroupId) {
        return manageRoleGroupUseCase.getFunctionsByRoleGroup(roleGroupId);
    }

    // -- Permission Assignment Management --
    @Override
    public List<PermissionAssignment> getAllPermissionAssignments() {
        return permissionAssignmentRepository.findAll();
    }

    @Override
    public Optional<PermissionAssignment> getPermissionAssignmentById(int assignmentId) {
        return permissionAssignmentRepository.findById(assignmentId);
    }

    @Override
    public ManagePermissionAssignmentOutput createPermissionAssignment(ManagePermissionAssignmentInput input) {
        return managePermissionAssignmentUseCase.create(input);
    }

    @Override
    public ManagePermissionAssignmentOutput updatePermissionAssignment(int assignmentId, ManagePermissionAssignmentInput input) {
        return managePermissionAssignmentUseCase.update(assignmentId, input);
    }

    @Override
    public void deletePermissionAssignment(int assignmentId) {
        managePermissionAssignmentUseCase.delete(assignmentId);
    }

    // -- Module & Function Management --
    @Override
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    @Override
    public Optional<Module> getModuleById(int moduleId) {
        return moduleRepository.findById(moduleId);
    }

    @Override
    public List<Function> getAllFunctions() {
        return functionRepository.findAll();
    }

    @Override
    public Optional<Function> getFunctionById(int functionId) {
        return functionRepository.findById(functionId);
    }

    @Override
    public List<Function> getFunctionsByModuleId(int moduleId) {
        return functionRepository.findByModuleId(moduleId);
    }
}
