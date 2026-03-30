package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentOutput;

import java.util.Optional;

public class ManagePermissionAssignmentUseCase {

    private final PermissionAssignmentRepository permissionAssignmentRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final FunctionRepository functionRepository;

    public ManagePermissionAssignmentUseCase(PermissionAssignmentRepository permissionAssignmentRepository,
                                             RoleGroupRepository roleGroupRepository,
                                             FunctionRepository functionRepository) {
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.functionRepository = functionRepository;
    }

    public ManagePermissionAssignmentOutput create(ManagePermissionAssignmentInput input) {
        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            return new ManagePermissionAssignmentOutput(false, "Nhóm quyền không tồn tại.", 0);
        }
        if (functionRepository.findById(input.getFunctionId()).isEmpty()) {
            return new ManagePermissionAssignmentOutput(false, "Chức năng không tồn tại.", 0);
        }

        // Kiểm tra xem phân quyền này đã tồn tại chưa
        if (permissionAssignmentRepository.findByRoleGroupAndFunction(input.getRoleGroupId(), input.getFunctionId()).isPresent()) {
            return new ManagePermissionAssignmentOutput(false, "Phân quyền cho nhóm và chức năng này đã tồn tại.", 0);
        }

        PermissionAssignment newAssignment = new PermissionAssignment(0, input.getRoleGroupId(), input.getFunctionId(), input.isCanView(), input.isCanOperate());
        permissionAssignmentRepository.save(newAssignment);
        return new ManagePermissionAssignmentOutput(true, "Tạo phân quyền thành công.", newAssignment.getId());
    }

    public ManagePermissionAssignmentOutput update(int assignmentId, ManagePermissionAssignmentInput input) {
        Optional<PermissionAssignment> existingAssignmentOptional = permissionAssignmentRepository.findById(assignmentId);
        if (existingAssignmentOptional.isEmpty()) {
            return new ManagePermissionAssignmentOutput(false, "Phân quyền không tồn tại.", assignmentId);
        }
        PermissionAssignment existingAssignment = existingAssignmentOptional.get();

        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            return new ManagePermissionAssignmentOutput(false, "Nhóm quyền không tồn tại.", assignmentId);
        }
        if (functionRepository.findById(input.getFunctionId()).isEmpty()) {
            return new ManagePermissionAssignmentOutput(false, "Chức năng không tồn tại.", assignmentId);
        }

        // Kiểm tra xem có phân quyền khác trùng với thông tin mới không (nếu có thay đổi roleGroupId hoặc functionId)
        Optional<PermissionAssignment> existingDuplicateAssignment = permissionAssignmentRepository.findByRoleGroupAndFunction(input.getRoleGroupId(), input.getFunctionId());
        if (existingDuplicateAssignment.isPresent() && existingDuplicateAssignment.get().getId() != assignmentId) {
            return new ManagePermissionAssignmentOutput(false, "Phân quyền trùng lặp đã tồn tại.", assignmentId);
        }

        // Cập nhật thông tin phân quyền
        // existingAssignment.setRoleGroupId(input.getRoleGroupId()); // Không cho phép thay đổi FKs trực tiếp sau khi tạo
        // existingAssignment.setFunctionId(input.getFunctionId());   // Nếu muốn thay đổi, cần xóa và tạo lại
        existingAssignment.setCanView(input.isCanView());
        existingAssignment.setCanOperate(input.isCanOperate());

        permissionAssignmentRepository.save(existingAssignment);
        return new ManagePermissionAssignmentOutput(true, "Cập nhật phân quyền thành công.", assignmentId);
    }

    public void delete(int assignmentId) {
        permissionAssignmentRepository.delete(assignmentId);
    }
}
