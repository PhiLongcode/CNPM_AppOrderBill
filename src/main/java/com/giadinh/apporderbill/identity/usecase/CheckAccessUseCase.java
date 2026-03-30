package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckAccessUseCase {

    private final UserRepository userRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final PermissionAssignmentRepository permissionAssignmentRepository;
    private final FunctionRepository functionRepository;

    public CheckAccessUseCase(UserRepository userRepository,
                              RoleGroupRepository roleGroupRepository,
                              PermissionAssignmentRepository permissionAssignmentRepository,
                              FunctionRepository functionRepository) {
        this.userRepository = userRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.functionRepository = functionRepository;
    }

    /**
     * Kiểm tra xem một người dùng có quyền truy cập một chức năng cụ thể không.
     *
     * @param username Tên đăng nhập của người dùng.
     * @param functionName Tên chức năng cần kiểm tra (ví dụ: "CREATE_ORDER", "VIEW_REPORTS").
     * @param requiresOperatePermission true nếu cần quyền thao tác, false nếu chỉ cần quyền xem.
     * @return true nếu người dùng có quyền, false nếu không.
     */
    public boolean execute(String username, String functionName, boolean requiresOperatePermission) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return false; // Người dùng không tồn tại
        }
        User user = userOptional.get();

        Optional<RoleGroup> roleGroupOptional = roleGroupRepository.findById(user.getRoleGroupId());
        if (roleGroupOptional.isEmpty()) {
            return false; // Nhóm quyền không tồn tại
        }
        RoleGroup roleGroup = roleGroupOptional.get();

        Optional<Function> functionOptional = functionRepository.findByName(functionName); // Cần thêm findByName vào FunctionRepository
        if (functionOptional.isEmpty()) {
            return false; // Chức năng không tồn tại
        }
        Function targetFunction = functionOptional.get();

        // Lấy tất cả các quyền được gán cho nhóm quyền này
        List<PermissionAssignment> assignments = permissionAssignmentRepository.findByRoleGroupId(roleGroup.getId());

        // Kiểm tra quyền cụ thể cho chức năng targetFunction
        return assignments.stream()
                .filter(pa -> pa.getFunctionId() == targetFunction.getId())
                .anyMatch(pa -> (requiresOperatePermission && pa.isCanOperate()) || (!requiresOperatePermission && pa.isCanView()));
    }
}
