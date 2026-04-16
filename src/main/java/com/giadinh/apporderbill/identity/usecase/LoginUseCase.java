package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.security.PasswordHasher;
import com.giadinh.apporderbill.identity.usecase.dto.LoginInput;
import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final PermissionAssignmentRepository permissionAssignmentRepository;

    public LoginUseCase(UserRepository userRepository,
                        RoleGroupRepository roleGroupRepository,
                        PermissionAssignmentRepository permissionAssignmentRepository) {
        this.userRepository = userRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
    }

    public LoginOutput execute(LoginInput input) {
        Optional<User> userOptional = userRepository.findByUsername(input.getUsername());
        if (userOptional.isEmpty()) {
            throw new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        User user = userOptional.get();

        if (!PasswordHasher.matches(input.getPassword(), user.getPasswordHash())) {
            throw new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        // Backward compatibility: auto-upgrade legacy plain-text values after successful login.
        if (PasswordHasher.needsRehash(user.getPasswordHash())) {
            user.setPasswordHash(PasswordHasher.hash(input.getPassword()));
            userRepository.save(user);
        }

        Optional<RoleGroup> roleGroupOptional = roleGroupRepository.findById(user.getRoleGroupId());
        if (roleGroupOptional.isEmpty()) {
            throw new DomainException(ErrorCode.USER_ROLE_GROUP_NOT_FOUND);
        }

        RoleGroup roleGroup = roleGroupOptional.get();

        List<PermissionAssignment> assignments = permissionAssignmentRepository.findByRoleGroupId(roleGroup.getId());
        Set<PermissionAssignment> activePermissions = new HashSet<>(assignments);

        return new LoginOutput(user.getId(), user.getUsername(), roleGroup.getName(), activePermissions);
    }
}
