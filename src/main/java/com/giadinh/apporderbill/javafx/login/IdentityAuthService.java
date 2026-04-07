package com.giadinh.apporderbill.javafx.login;

import com.giadinh.apporderbill.identity.infrastructure.init.IdentityDataInitializer;
import com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.FunctionRepositoryImpl;
import com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.ModuleRepositoryImpl;
import com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.PermissionAssignmentRepositoryImpl;
import com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.RoleGroupRepositoryImpl;
import com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.UserRepositoryImpl;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import com.giadinh.apporderbill.identity.repository.ModuleRepository;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import com.giadinh.apporderbill.identity.usecase.CheckAccessUseCase;
import com.giadinh.apporderbill.identity.usecase.LoginUseCase;
import com.giadinh.apporderbill.identity.usecase.dto.LoginInput;
import com.giadinh.apporderbill.identity.usecase.dto.LoginOutput;

import java.sql.Connection;
import java.sql.DriverManager;

public class IdentityAuthService {
    private final LoginUseCase loginUseCase;
    private final CheckAccessUseCase checkAccessUseCase;

    public IdentityAuthService() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:output/pos.db");
            ModuleRepository moduleRepository = new ModuleRepositoryImpl(connection);
            FunctionRepository functionRepository = new FunctionRepositoryImpl(connection);
            RoleGroupRepository roleGroupRepository = new RoleGroupRepositoryImpl(connection);
            PermissionAssignmentRepository permissionAssignmentRepository = new PermissionAssignmentRepositoryImpl(connection);
            UserRepository userRepository = new UserRepositoryImpl(connection);
            new IdentityDataInitializer(moduleRepository, functionRepository, roleGroupRepository,
                    permissionAssignmentRepository, userRepository).run();
            this.loginUseCase = new LoginUseCase(userRepository, roleGroupRepository, permissionAssignmentRepository);
            this.checkAccessUseCase = new CheckAccessUseCase(userRepository, roleGroupRepository, permissionAssignmentRepository, functionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Không thể khởi tạo IdentityAuthService", e);
        }
    }

    public LoginOutput login(String username, String password) {
        return loginUseCase.execute(new LoginInput(username, password));
    }

    public boolean canOperate(String username, String functionName) {
        return checkAccessUseCase.execute(username, functionName, true);
    }
}

