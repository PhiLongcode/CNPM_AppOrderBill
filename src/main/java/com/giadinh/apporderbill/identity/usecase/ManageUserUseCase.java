package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserInput;
import com.giadinh.apporderbill.identity.usecase.dto.ManageUserOutput;

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
            return new ManageUserOutput(false, "Tên đăng nhập đã tồn tại.", 0);
        }
        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            return new ManageUserOutput(false, "Nhóm quyền không tồn tại.", 0);
        }

        // Trong thực tế, mật khẩu cần được hash trước khi lưu
        User newUser = new User(0, input.getUsername(), input.getPassword(), input.getRoleGroupId()); // ID sẽ được DB tự động tạo
        userRepository.save(newUser);
        return new ManageUserOutput(true, "Tạo người dùng thành công.", newUser.getId());
    }

    public ManageUserOutput update(int userId, ManageUserInput input) {
        Optional<User> existingUserOptional = userRepository.findById(userId);
        if (existingUserOptional.isEmpty()) {
            return new ManageUserOutput(false, "Người dùng không tồn tại.", userId);
        }
        User existingUser = existingUserOptional.get();

        // Kiểm tra xem username mới có bị trùng với người dùng khác không
        Optional<User> userWithSameUsername = userRepository.findByUsername(input.getUsername());
        if (userWithSameUsername.isPresent() && userWithSameUsername.get().getId() != userId) {
            return new ManageUserOutput(false, "Tên đăng nhập đã tồn tại bởi người dùng khác.", userId);
        }

        if (roleGroupRepository.findById(input.getRoleGroupId()).isEmpty()) {
            return new ManageUserOutput(false, "Nhóm quyền không tồn tại.", userId);
        }

        // Cập nhật thông tin người dùng
        // existingUser.setUsername(input.getUsername()); // Username không nên thay đổi sau khi tạo hoặc cần một UseCase riêng
        existingUser.setPasswordHash(input.getPassword()); // Cần hash trong thực tế
        existingUser.setRoleGroupId(input.getRoleGroupId());

        userRepository.save(existingUser);
        return new ManageUserOutput(true, "Cập nhật người dùng thành công.", userId);
    }

    public void delete(int userId) {
        userRepository.delete(userId);
    }
}
