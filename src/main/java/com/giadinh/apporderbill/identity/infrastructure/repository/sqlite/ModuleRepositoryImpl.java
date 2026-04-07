package com.giadinh.apporderbill.identity.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.identity.model.Module;
import com.giadinh.apporderbill.identity.repository.ModuleRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ModuleRepositoryImpl implements ModuleRepository {

    private final Connection connection;

    public ModuleRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS modules (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)");
        } catch (SQLException e) {
            System.err.println("Error creating modules table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Module> findById(int id) {
        String sql = "SELECT id, name FROM modules WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Module(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding module by id: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Module> findAll() {
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT id, name FROM modules";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                modules.add(new Module(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all modules: " + e.getMessage());
            e.printStackTrace();
        }
        return modules;
    }

    @Override
    public void save(Module module) {
        if (module.getId() == 0) { // New module
            String sql = "INSERT INTO modules (name) VALUES (?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, module.getName());
                pstmt.executeUpdate();
                // SQLite way to get generated id.
                try (Statement s = connection.createStatement();
                        ResultSet rs = s.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        module.setId(rs.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error saving new module: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Existing module
            String updateSql = "UPDATE modules SET name = ? WHERE id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setString(1, module.getName());
                updateStmt.setInt(2, module.getId());
                int updated = updateStmt.executeUpdate();
                if (updated == 0) {
                    String insertSql = "INSERT INTO modules (id, name) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, module.getId());
                        insertStmt.setString(2, module.getName());
                        insertStmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error upserting module: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM modules WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting module: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
