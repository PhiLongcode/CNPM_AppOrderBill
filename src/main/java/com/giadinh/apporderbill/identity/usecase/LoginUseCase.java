package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import com.giadinh.apporderbill.identity.usecase.dto.LoginInput;
import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final PermissionAssignmentRepository permissionAssignmentRepository;
    private final FunctionRepository functionRepository;

    public LoginUseCase(UserRepository userRepository,
                        RoleGroupRepository roleGroupRepository,
                        PermissionAssignmentRepository permissionAssignmentRepository,
                        FunctionRepository functionRepository) {
        this.userRepository = userRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.functionRepository = functionRepository;
    }

    public LoginOutput execute(LoginInput input) {
        // 1. Tìm người dùng theo username
        Optional<User> userOptional = userRepository.findByUsername(input.getUsername());
        if (userOptional.isEmpty()) {
            return new LoginOutput(false, "Tên đăng nhập hoặc mật khẩu không đúng.", 0, null, null, null);
        }

        User user = userOptional.get();

        // 2. Kiểm tra mật khẩu (trong thực tế: so sánh hash mật khẩu)
        if (!user.getPasswordHash().equals(input.getPassword())) { // Đơn giản hóa, cần hash trong thực tế
            return new LoginOutput(false, "Tên đăng nhập hoặc mật khẩu không đúng.", 0, null, null, null);
        }

        // 3. Lấy thông tin nhóm quyền của người dùng
        Optional<RoleGroup> roleGroupOptional = roleGroupRepository.findById(user.getRoleGroupId());
        if (roleGroupOptional.isEmpty()) {
            return new LoginOutput(false, "Không tìm thấy nhóm quyền của người dùng.", user.getId(), user.getUsername(), null, null);
        }

        RoleGroup roleGroup = roleGroupOptional.get();

        // 4. Lấy tất cả các quyền được gán cho nhóm quyền này
        List<PermissionAssignment> assignments = permissionAssignmentRepository.findByRoleGroupId(roleGroup.getId());
        Set<PermissionAssignment> activePermissions = new HashSet<>(assignments);

        return new LoginOutput(true, "Đăng nhập thành công.", user.getId(), user.getUsername(), roleGroup.getName(), activePermissions);
    }
}
