package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageRoleGroupOutput;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ManageRoleGroupUseCase {

    private final RoleGroupRepository roleGroupRepository;
    private final PermissionAssignmentRepository permissionAssignmentRepository;
    private final FunctionRepository functionRepository;

    public ManageRoleGroupUseCase(RoleGroupRepository roleGroupRepository,
                                  PermissionAssignmentRepository permissionAssignmentRepository,
                                  FunctionRepository functionRepository) {
        this.roleGroupRepository = roleGroupRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.functionRepository = functionRepository;
    }

    public ManageRoleGroupOutput create(ManageRoleGroupInput input) {
        if (roleGroupRepository.findByName(input.getName()).isPresent()) {
            return new ManageRoleGroupOutput(false, "Tên nhóm quyền đã tồn tại.", 0);
        }

        RoleGroup newRoleGroup = new RoleGroup(0, input.getName(), input.getDescription()); // ID sẽ được DB tự động tạo
        roleGroupRepository.save(newRoleGroup);

        // Gán các quyền ban đầu
        if (input.getFunctionIds() != null && !input.getFunctionIds().isEmpty()) {
            for (Integer functionId : input.getFunctionIds()) {
                Optional<Function> functionOptional = functionRepository.findById(functionId);
                if (functionOptional.isPresent()) {
                    PermissionAssignment assignment = new PermissionAssignment(0, newRoleGroup.getId(), functionId, true, false); // Mặc định có quyền xem
                    permissionAssignmentRepository.save(assignment);
                }
            }
        }

        return new ManageRoleGroupOutput(true, "Tạo nhóm quyền thành công.", newRoleGroup.getId());
    }

    public ManageRoleGroupOutput update(int roleGroupId, ManageRoleGroupInput input) {
        Optional<RoleGroup> existingRoleGroupOptional = roleGroupRepository.findById(roleGroupId);
        if (existingRoleGroupOptional.isEmpty()) {
            return new ManageRoleGroupOutput(false, "Nhóm quyền không tồn tại.", roleGroupId);
        }
        RoleGroup existingRoleGroup = existingRoleGroupOptional.get();

        // Kiểm tra trùng tên
        Optional<RoleGroup> roleGroupWithSameName = roleGroupRepository.findByName(input.getName());
        if (roleGroupWithSameName.isPresent() && roleGroupWithSameName.get().getId() != roleGroupId) {
            return new ManageRoleGroupOutput(false, "Tên nhóm quyền đã tồn tại bởi nhóm khác.", roleGroupId);
        }

        existingRoleGroup.setName(input.getName());
        existingRoleGroup.setDescription(input.getDescription());
        roleGroupRepository.save(existingRoleGroup);

        // Cập nhật quyền hạn: xóa hết quyền cũ và thêm lại quyền mới
        List<PermissionAssignment> currentAssignments = permissionAssignmentRepository.findByRoleGroupId(roleGroupId);
        for (PermissionAssignment assignment : currentAssignments) {
            permissionAssignmentRepository.delete(assignment.getId());
        }

        if (input.getFunctionIds() != null && !input.getFunctionIds().isEmpty()) {
            for (Integer functionId : input.getFunctionIds()) {
                Optional<Function> functionOptional = functionRepository.findById(functionId);
                if (functionOptional.isPresent()) {
                    PermissionAssignment assignment = new PermissionAssignment(0, roleGroupId, functionId, true, false); // Mặc định có quyền xem
                    permissionAssignmentRepository.save(assignment);
                }
            }
        }

        return new ManageRoleGroupOutput(true, "Cập nhật nhóm quyền thành công.", roleGroupId);
    }

    public void delete(int roleGroupId) {
        // Xóa tất cả các phân quyền liên quan trước
        List<PermissionAssignment> assignments = permissionAssignmentRepository.findByRoleGroupId(roleGroupId);
        for (PermissionAssignment assignment : assignments) {
            permissionAssignmentRepository.delete(assignment.getId());
        }
        roleGroupRepository.delete(roleGroupId);
    }

    public Set<Function> getFunctionsByRoleGroup(int roleGroupId) {
        List<PermissionAssignment> assignments = permissionAssignmentRepository.findByRoleGroupId(roleGroupId);
        Set<Integer> functionIds = assignments.stream()
                                            .map(PermissionAssignment::getFunctionId)
                                            .collect(Collectors.toSet());
        
        return functionIds.stream()
                            .map(functionRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toSet());
    }
}
