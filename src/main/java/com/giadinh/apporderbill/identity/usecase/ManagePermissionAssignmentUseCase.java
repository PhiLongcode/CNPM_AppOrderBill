package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManagePermissionAssignmentOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

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
            throw new DomainException(ErrorCode.ROLE_GROUP_NOT_FOUND);
        }
        if (functionRepository.findById(input.getFunctionId()).isEmpty()) {
            throw new DomainException(ErrorCode.IDENTITY_FUNCTION_NOT_FOUND);
        }

        if (permissionAssignmentRepository.findByRoleGroupAndFunction(input.getRoleGroupId(), input.getFunctionId()).isPresent()) {
            throw new DomainException(ErrorCode.PERMISSION_ASSIGNMENT_DUPLICATE);
        }

        PermissionAssignment newAssignment = new PermissionAssignment(0, input.getRoleGroupId(), input.getFunctionId(), input.isCanView(), input.isCanOperate());
        permissionAssignmentRepository.save(newAssignment);
        return new ManagePermissionAssignmentOutput(newAssignment.getId());
    }

    public ManagePermissionAssignmentOutput update(int assignmentId, ManagePermissionAssignmentInput input) {
        Optional<PermissionAssignment> existingAssignmentOptional = permissionAssignmentRepository.findById(assignmentId);
        if (existingAssignmentOptional.isEmpty()) {
            throw new DomainException(ErrorCode.PERMISSION_ASSIGNMENT_NOT_FOUND);
        }
        PermissionAssignment existingAssignment = existingAssignmentOptional.get();

        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            throw new DomainException(ErrorCode.ROLE_GROUP_NOT_FOUND);
        }
        if (functionRepository.findById(input.getFunctionId()).isEmpty()) {
            throw new DomainException(ErrorCode.IDENTITY_FUNCTION_NOT_FOUND);
        }

        Optional<PermissionAssignment> existingDuplicateAssignment = permissionAssignmentRepository.findByRoleGroupAndFunction(input.getRoleGroupId(), input.getFunctionId());
        if (existingDuplicateAssignment.isPresent() && existingDuplicateAssignment.get().getId() != assignmentId) {
            throw new DomainException(ErrorCode.PERMISSION_ASSIGNMENT_DUPLICATE);
        }

        existingAssignment.setCanView(input.isCanView());
        existingAssignment.setCanOperate(input.isCanOperate());

        permissionAssignmentRepository.save(existingAssignment);
        return new ManagePermissionAssignmentOutput(assignmentId);
    }

    public void delete(int assignmentId) {
        permissionAssignmentRepository.delete(assignmentId);
    }
}
