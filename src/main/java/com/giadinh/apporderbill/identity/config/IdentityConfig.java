package com.giadinh.apporderbill.identity.config;

import com.giadinh.apporderbill.identity.IdentityComponent;
import com.giadinh.apporderbill.identity.IdentityComponentImpl;
import com.giadinh.apporderbill.identity.repository.*;
import com.giadinh.apporderbill.identity.infrastructure.repository.sqlite.*;
import com.giadinh.apporderbill.identity.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class IdentityConfig {

    private static final String DB_URL = "jdbc:sqlite:output/pos.db"; // Dùng chung DB với desktop POS

    // Bean để cung cấp kết nối SQLite
    @Bean
    public Connection sqliteConnection() throws SQLException {
        // Đảm bảo tạo database và các bảng nếu chưa có
        Connection connection = DriverManager.getConnection(DB_URL);
        // Kích hoạt Foreign Key Support cho SQLite
        try (java.sql.Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }

    // --- Repository Beans ---
    @Bean
    public ModuleRepository moduleRepository(Connection connection) {
        return new ModuleRepositoryImpl(connection);
    }

    @Bean
    public FunctionRepository functionRepository(Connection connection) {
        return new FunctionRepositoryImpl(connection);
    }

    @Bean
    public RoleGroupRepository roleGroupRepository(Connection connection) {
        return new RoleGroupRepositoryImpl(connection);
    }

    @Bean
    public PermissionAssignmentRepository permissionAssignmentRepository(Connection connection) {
        return new PermissionAssignmentRepositoryImpl(connection);
    }

    @Bean
    public UserRepository userRepository(Connection connection) {
        return new UserRepositoryImpl(connection);
    }

    // --- Use Case Beans ---
    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository,
                                     RoleGroupRepository roleGroupRepository,
                                     PermissionAssignmentRepository permissionAssignmentRepository) {
        return new LoginUseCase(userRepository, roleGroupRepository, permissionAssignmentRepository);
    }

    @Bean
    public CheckAccessUseCase checkAccessUseCase(UserRepository userRepository,
                                               RoleGroupRepository roleGroupRepository,
                                               PermissionAssignmentRepository permissionAssignmentRepository,
                                               FunctionRepository functionRepository) {
        return new CheckAccessUseCase(userRepository, roleGroupRepository, permissionAssignmentRepository, functionRepository);
    }

    @Bean
    public ManageUserUseCase manageUserUseCase(UserRepository userRepository, RoleGroupRepository roleGroupRepository) {
        return new ManageUserUseCase(userRepository, roleGroupRepository);
    }

    @Bean
    public ManageRoleGroupUseCase manageRoleGroupUseCase(RoleGroupRepository roleGroupRepository,
                                                         PermissionAssignmentRepository permissionAssignmentRepository,
                                                         FunctionRepository functionRepository) {
        return new ManageRoleGroupUseCase(roleGroupRepository, permissionAssignmentRepository, functionRepository);
    }

    @Bean
    public ManagePermissionAssignmentUseCase managePermissionAssignmentUseCase(PermissionAssignmentRepository permissionAssignmentRepository,
                                                                               RoleGroupRepository roleGroupRepository,
                                                                               FunctionRepository functionRepository) {
        return new ManagePermissionAssignmentUseCase(permissionAssignmentRepository, roleGroupRepository, functionRepository);
    }

    // --- Component Bean ---
    @Bean
    public IdentityComponent identityComponent(ModuleRepository moduleRepository,
                                               FunctionRepository functionRepository,
                                               RoleGroupRepository roleGroupRepository,
                                               PermissionAssignmentRepository permissionAssignmentRepository,
                                               UserRepository userRepository,
                                               LoginUseCase loginUseCase,
                                               CheckAccessUseCase checkAccessUseCase,
                                               ManageUserUseCase manageUserUseCase,
                                               ManageRoleGroupUseCase manageRoleGroupUseCase,
                                               ManagePermissionAssignmentUseCase managePermissionAssignmentUseCase) {
        // IdentityComponentImpl sẽ nhận các UseCase thông qua constructor injection nếu chúng là @Service
        // Hoặc chúng ta có thể truyền các Repository vào đây và UseCase tự khởi tạo bên trong
        // Nhưng để tuân thủ DI tốt hơn, ta nên inject các UseCase vào ComponentImpl
        // Vì các UseCase đã là @Bean ở trên, ta có thể inject trực tiếp vào IdentityComponentImpl constructor
        return new IdentityComponentImpl(moduleRepository, functionRepository, roleGroupRepository,
                permissionAssignmentRepository, userRepository, loginUseCase, checkAccessUseCase, manageUserUseCase,
                manageRoleGroupUseCase, managePermissionAssignmentUseCase);
    }
}
