package com.giadinh.apporderbill.identity.infrastructure.init;

import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.model.Module;
import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IdentityDataInitializer implements CommandLineRunner {

    private final ModuleRepository moduleRepository;
    private final FunctionRepository functionRepository;
    private final RoleGroupRepository roleGroupRepository;
    private final PermissionAssignmentRepository permissionAssignmentRepository;
    private final UserRepository userRepository;

    public IdentityDataInitializer(ModuleRepository moduleRepository,
                                   FunctionRepository functionRepository,
                                   RoleGroupRepository roleGroupRepository,
                                   PermissionAssignmentRepository permissionAssignmentRepository,
                                   UserRepository userRepository) {
        this.moduleRepository = moduleRepository;
        this.functionRepository = functionRepository;
        this.roleGroupRepository = roleGroupRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing Identity data...");

        // 1. Create Modules
        Module moduleIdentity = createModuleIfNotExists(1, "Identity");
        Module moduleCatalog = createModuleIfNotExists(2, "Catalog");
        Module moduleSales = createModuleIfNotExists(3, "Sales");
        Module moduleInventory = createModuleIfNotExists(4, "Inventory");
        Module modulePrinter = createModuleIfNotExists(5, "Printer");

        // 2. Create Functions
        Function funcManageUsers = createFunctionIfNotExists(101, "Manage Users", moduleIdentity.getId());
        Function funcManageRoles = createFunctionIfNotExists(102, "Manage Roles", moduleIdentity.getId());
        Function funcManagePermissions = createFunctionIfNotExists(103, "Manage Permissions", moduleIdentity.getId());
        Function funcLogin = createFunctionIfNotExists(104, "Login", moduleIdentity.getId());

        Function funcManageMenuItems = createFunctionIfNotExists(201, "Manage Menu Items", moduleCatalog.getId());
        Function funcManageCategories = createFunctionIfNotExists(202, "Manage Categories", moduleCatalog.getId());

        Function funcCreateOrder = createFunctionIfNotExists(301, "Create Order", moduleSales.getId());
        Function funcCheckoutOrder = createFunctionIfNotExists(302, "Checkout Order", moduleSales.getId());
        Function funcTransferTable = createFunctionIfNotExists(303, "Transfer Table", moduleSales.getId());
        Function funcPrintKitchenTicket = createFunctionIfNotExists(304, "Print Kitchen Ticket", moduleSales.getId());
        Function funcPrintReceipt = createFunctionIfNotExists(305, "Print Receipt", moduleSales.getId());
        Function funcManageTables = createFunctionIfNotExists(306, "Manage Tables", moduleSales.getId());
        Function funcManageCustomers = createFunctionIfNotExists(307, "Manage Customers", moduleSales.getId());
        Function funcViewReports = createFunctionIfNotExists(308, "View Reports", moduleSales.getId());

        // 3. Create RoleGroups
        RoleGroup adminRoleGroup = createRoleGroupIfNotExists(1, "ADMIN", "Administrator with full access");
        RoleGroup cashierRoleGroup = createRoleGroupIfNotExists(2, "CASHIER", "Cashier with limited access");

        // 4. Assign Permissions to RoleGroups
        // ADMIN has all permissions
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManageUsers.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManageRoles.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManagePermissions.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcLogin.getId(), true, false);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManageMenuItems.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManageCategories.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcCreateOrder.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcCheckoutOrder.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcTransferTable.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcPrintKitchenTicket.getId(), true, false);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcPrintReceipt.getId(), true, false);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManageTables.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcManageCustomers.getId(), true, true);
        assignPermissionIfNotExists(adminRoleGroup.getId(), funcViewReports.getId(), true, true);

        // CASHIER permissions
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcLogin.getId(), true, false);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcCreateOrder.getId(), true, true);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcCheckoutOrder.getId(), true, true);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcTransferTable.getId(), true, true);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcPrintKitchenTicket.getId(), true, false);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcPrintReceipt.getId(), true, false);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcManageMenuItems.getId(), true, false); // Cashier chỉ xem menu
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcManageCategories.getId(), true, false); // Cashier chỉ xem category
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcManageTables.getId(), true, true);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcManageCustomers.getId(), true, true);
        assignPermissionIfNotExists(cashierRoleGroup.getId(), funcViewReports.getId(), true, false);

        // 5. Create Users
        createUserIfNotExists("admin", "admin_pass", adminRoleGroup.getId()); // In practice, use strong password hashing
        createUserIfNotExists("cashier1", "cashier_pass", cashierRoleGroup.getId());

        System.out.println("Identity data initialization completed.");
    }

    private Module createModuleIfNotExists(int id, String name) {
        Optional<Module> existing = moduleRepository.findById(id);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            Module module = new Module(id, name);
            moduleRepository.save(module);
            return module;
        }
    }

    private Function createFunctionIfNotExists(int id, String name, int moduleId) {
        Optional<Function> existing = functionRepository.findById(id);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            Function function = new Function(id, name, moduleId);
            functionRepository.save(function);
            return function;
        }
    }

    private RoleGroup createRoleGroupIfNotExists(int id, String name, String description) {
        Optional<RoleGroup> existing = roleGroupRepository.findById(id);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            RoleGroup roleGroup = new RoleGroup(id, name, description);
            roleGroupRepository.save(roleGroup);
            return roleGroup;
        }
    }

    private PermissionAssignment assignPermissionIfNotExists(int roleGroupId, int functionId, boolean canView, boolean canOperate) {
        Optional<PermissionAssignment> existing = permissionAssignmentRepository.findByRoleGroupAndFunction(roleGroupId, functionId);
        if (existing.isPresent()) {
            // Update existing assignment if necessary (e.g., if canView/canOperate changed)
            PermissionAssignment assignment = existing.get();
            boolean changed = false;
            if (assignment.isCanView() != canView) { assignment.setCanView(canView); changed = true; }
            if (assignment.isCanOperate() != canOperate) { assignment.setCanOperate(canOperate); changed = true; }
            if (changed) {
                permissionAssignmentRepository.save(assignment);
            }
            return assignment;
        } else {
            PermissionAssignment assignment = new PermissionAssignment(0, roleGroupId, functionId, canView, canOperate);
            permissionAssignmentRepository.save(assignment);
            // Note: the ID of the new assignment won't be set back to the object automatically here
            return assignment;
        }
    }

    private User createUserIfNotExists(String username, String passwordHash, int roleGroupId) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            User current = existing.get();
            // Keep seeded accounts consistent to avoid login mismatch in local environments.
            current.setPasswordHash(passwordHash);
            current.setRoleGroupId(roleGroupId);
            userRepository.save(current);
            return current;
        } else {
            User user = new User(0, username, passwordHash, roleGroupId);
            userRepository.save(user);
            // Note: the ID of the new user won't be set back to the object automatically here
            return user;
        }
    }
}
