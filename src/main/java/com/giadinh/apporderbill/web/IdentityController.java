package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.identity.IdentityComponent;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.Module;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/identity")
@Tag(name = "Identity", description = "Authentication and authorization management")
public class IdentityController {
    private final IdentityComponent identityComponent;
    private final ApiAuthorizationService authorizationService;

    public IdentityController(IdentityComponent identityComponent, ApiAuthorizationService authorizationService) {
        this.identityComponent = identityComponent;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOutput> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(identityComponent.login(new LoginInput(request.getUsername(), request.getPassword())));
    }

    @PostMapping("/check-access")
    public ResponseEntity<Boolean> checkAccess(@RequestHeader(value = "X-Username", required = false) String username,
                                               @RequestBody CheckAccessRequest request) {
        authorizationService.requireView(username, "Manage Permissions");
        boolean result = identityComponent.checkAccess(request.getUsername(), request.getFunctionName(), request.isRequiresOperatePermission());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Users");
        return ResponseEntity.ok(identityComponent.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<ManageUserOutput> createUser(@RequestHeader(value = "X-Username", required = false) String username,
                                                       @RequestBody ManageUserRequest request) {
        authorizationService.requireOperate(username, "Manage Users");
        ManageUserOutput output = identityComponent.createUser(new ManageUserInput(request.getUsername(), request.getPassword(), request.getRoleGroupId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ManageUserOutput> updateUser(@RequestHeader(value = "X-Username", required = false) String username,
                                                       @PathVariable int userId, @RequestBody ManageUserRequest request) {
        authorizationService.requireOperate(username, "Manage Users");
        return ResponseEntity.ok(identityComponent.updateUser(userId, new ManageUserInput(request.getUsername(), request.getPassword(), request.getRoleGroupId())));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@RequestHeader(value = "X-Username", required = false) String username, @PathVariable int userId) {
        authorizationService.requireOperate(username, "Manage Users");
        identityComponent.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role-groups")
    public ResponseEntity<List<RoleGroup>> getAllRoleGroups(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Roles");
        return ResponseEntity.ok(identityComponent.getAllRoleGroups());
    }

    @PostMapping("/role-groups")
    public ResponseEntity<ManageRoleGroupOutput> createRoleGroup(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @RequestBody ManageRoleGroupRequest request) {
        authorizationService.requireOperate(username, "Manage Roles");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identityComponent.createRoleGroup(new ManageRoleGroupInput(request.getName(), request.getDescription(), request.getFunctionIds())));
    }

    @PutMapping("/role-groups/{roleGroupId}")
    public ResponseEntity<ManageRoleGroupOutput> updateRoleGroup(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @PathVariable int roleGroupId, @RequestBody ManageRoleGroupRequest request) {
        authorizationService.requireOperate(username, "Manage Roles");
        return ResponseEntity.ok(identityComponent.updateRoleGroup(roleGroupId,
                new ManageRoleGroupInput(request.getName(), request.getDescription(), request.getFunctionIds())));
    }

    @DeleteMapping("/role-groups/{roleGroupId}")
    public ResponseEntity<Void> deleteRoleGroup(@RequestHeader(value = "X-Username", required = false) String username,
                                                @PathVariable int roleGroupId) {
        authorizationService.requireOperate(username, "Manage Roles");
        identityComponent.deleteRoleGroup(roleGroupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role-groups/{roleGroupId}/functions")
    public ResponseEntity<Set<Function>> getFunctionsByRoleGroup(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @PathVariable int roleGroupId) {
        authorizationService.requireView(username, "Manage Roles");
        return ResponseEntity.ok(identityComponent.getFunctionsByRoleGroup(roleGroupId));
    }

    @GetMapping("/permission-assignments")
    public ResponseEntity<List<PermissionAssignment>> getAllPermissionAssignments(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Permissions");
        return ResponseEntity.ok(identityComponent.getAllPermissionAssignments());
    }

    @PostMapping("/permission-assignments")
    public ResponseEntity<ManagePermissionAssignmentOutput> createPermissionAssignment(@RequestHeader(value = "X-Username", required = false) String username,
                                                                                       @RequestBody ManagePermissionAssignmentRequest request) {
        authorizationService.requireOperate(username, "Manage Permissions");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identityComponent.createPermissionAssignment(new ManagePermissionAssignmentInput(
                        request.getRoleGroupId(), request.getFunctionId(), request.isCanView(), request.isCanOperate())));
    }

    @PutMapping("/permission-assignments/{assignmentId}")
    public ResponseEntity<ManagePermissionAssignmentOutput> updatePermissionAssignment(@RequestHeader(value = "X-Username", required = false) String username,
                                                                                       @PathVariable int assignmentId,
                                                                                       @RequestBody ManagePermissionAssignmentRequest request) {
        authorizationService.requireOperate(username, "Manage Permissions");
        return ResponseEntity.ok(identityComponent.updatePermissionAssignment(assignmentId, new ManagePermissionAssignmentInput(
                request.getRoleGroupId(), request.getFunctionId(), request.isCanView(), request.isCanOperate())));
    }

    @DeleteMapping("/permission-assignments/{assignmentId}")
    public ResponseEntity<Void> deletePermissionAssignment(@RequestHeader(value = "X-Username", required = false) String username,
                                                           @PathVariable int assignmentId) {
        authorizationService.requireOperate(username, "Manage Permissions");
        identityComponent.deletePermissionAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/modules")
    public ResponseEntity<List<Module>> getAllModules(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Permissions");
        return ResponseEntity.ok(identityComponent.getAllModules());
    }

    @GetMapping("/functions")
    public ResponseEntity<List<Function>> getAllFunctions(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Permissions");
        return ResponseEntity.ok(identityComponent.getAllFunctions());
    }

    @GetMapping("/modules/{moduleId}/functions")
    public ResponseEntity<List<Function>> getFunctionsByModuleId(@RequestHeader(value = "X-Username", required = false) String username,
                                                                 @PathVariable int moduleId) {
        authorizationService.requireView(username, "Manage Permissions");
        return ResponseEntity.ok(identityComponent.getFunctionsByModuleId(moduleId));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class CheckAccessRequest {
        private String username;
        private String functionName;
        private boolean requiresOperatePermission;

        public String getUsername() {
            return username;
        }

        public String getFunctionName() {
            return functionName;
        }

        public boolean isRequiresOperatePermission() {
            return requiresOperatePermission;
        }
    }

    public static class ManageUserRequest {
        private String username;
        private String password;
        private int roleGroupId;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public int getRoleGroupId() {
            return roleGroupId;
        }
    }

    public static class ManageRoleGroupRequest {
        private String name;
        private String description;
        private Set<Integer> functionIds;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Set<Integer> getFunctionIds() {
            return functionIds;
        }
    }

    public static class ManagePermissionAssignmentRequest {
        private int roleGroupId;
        private int functionId;
        private boolean canView;
        private boolean canOperate;

        public int getRoleGroupId() {
            return roleGroupId;
        }

        public int getFunctionId() {
            return functionId;
        }

        public boolean isCanView() {
            return canView;
        }

        public boolean isCanOperate() {
            return canOperate;
        }
    }
}
