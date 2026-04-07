package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.Optional;

public class ManageUserUseCase {

    private final UserRepository userRepository;
    private final RoleGroupRepository roleGroupRepository;

    public ManageUserUseCase(UserRepository userRepository, RoleGroupRepository roleGroupRepository) {
        this.userRepository = userRepository;
        this.roleGroupRepository = roleGroupRepository;
    }

    public ManageUserOutput create(ManageUserInput input) {
        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
            throw new DomainException(ErrorCode.USER_USERNAME_DUPLICATE);
        }
        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            throw new DomainException(ErrorCode.ROLE_GROUP_NOT_FOUND);
        }

        User newUser = new User(0, input.getUsername(), input.getPassword(), input.getRoleGroupId());
        userRepository.save(newUser);
        return new ManageUserOutput(newUser.getId());
    }

    public ManageUserOutput update(int userId, ManageUserInput input) {
        Optional<User> existingUserOptional = userRepository.findById(userId);
        if (existingUserOptional.isEmpty()) {
            throw new DomainException(ErrorCode.USER_NOT_FOUND);
        }
        User existingUser = existingUserOptional.get();

        Optional<User> userWithSameUsername = userRepository.findByUsername(input.getUsername());
        if (userWithSameUsername.isPresent() && userWithSameUsername.get().getId() != userId) {
            throw new DomainException(ErrorCode.USER_USERNAME_DUPLICATE);
        }

        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            throw new DomainException(ErrorCode.ROLE_GROUP_NOT_FOUND);
        }

        existingUser.setPasswordHash(input.getPassword());
        existingUser.setRoleGroupId(input.getRoleGroupId());

        userRepository.save(existingUser);
        return new ManageUserOutput(userId);
    }

    public void delete(int userId) {
        userRepository.delete(userId);
    }
}
