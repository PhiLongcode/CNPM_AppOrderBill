package com.giadinh.apporderbill.identity.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.identity.model.Module;
import com.giadinh.apporderbill.identity.repository.ModuleRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ModuleRepositoryImplTest {

    private static final String DB_URL = "jdbc:sqlite:test_identity.db";
    private ModuleRepository moduleRepository;
    private Connection connection;

    @BeforeAll
    static void setupDatabaseFile() throws SQLException {
        // Đảm bảo file database test được tạo và xóa sau này
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS modules;");
            conn.createStatement().execute("CREATE TABLE modules (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE);");
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        // Clear table before each test
        connection.createStatement().execute("DELETE FROM modules;");
        moduleRepository = new ModuleRepositoryImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void save_newModule_shouldAssignIdAndSave() {
        Module module = new Module(0, "Test Module");
        moduleRepository.save(module);

        assertNotEquals(0, module.getId());
        Optional<Module> foundModule = moduleRepository.findById(module.getId());
        assertTrue(foundModule.isPresent());
        assertEquals("Test Module", foundModule.get().getName());
    }

    @Test
    void findById_existingModule_shouldReturnModule() {
        Module module = new Module(0, "Existing Module");
        moduleRepository.save(module);

        Optional<Module> foundModule = moduleRepository.findById(module.getId());
        assertTrue(foundModule.isPresent());
        assertEquals("Existing Module", foundModule.get().getName());
    }

    @Test
    void findById_nonExistingModule_shouldReturnEmpty() {
        Optional<Module> foundModule = moduleRepository.findById(999);
        assertTrue(foundModule.isEmpty());
    }

    @Test
    void update_existingModule_shouldUpdateName() {
        Module module = new Module(0, "Old Name");
        moduleRepository.save(module);

        module.setName("New Name");
        moduleRepository.save(module);

        Optional<Module> updatedModule = moduleRepository.findById(module.getId());
        assertTrue(updatedModule.isPresent());
        assertEquals("New Name", updatedModule.get().getName());
    }

    @Test
    void delete_existingModule_shouldRemoveModule() {
        Module module = new Module(0, "Module to Delete");
        moduleRepository.save(module);

        moduleRepository.delete(module.getId());

        Optional<Module> foundModule = moduleRepository.findById(module.getId());
        assertTrue(foundModule.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllModules() {
        moduleRepository.save(new Module(0, "Module 1"));
        moduleRepository.save(new Module(0, "Module 2"));

        assertEquals(2, moduleRepository.findAll().size());
    }
}
