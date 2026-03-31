package com.giadinh.apporderbill.identity.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.identity.model.Function;
import com.giadinh.apporderbill.identity.repository.FunctionRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FunctionRepositoryImpl implements FunctionRepository {

    private final Connection connection;

    public FunctionRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS functions (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, moduleId INTEGER, FOREIGN KEY (moduleId) REFERENCES modules(id))");
        } catch (SQLException e) {
            System.err.println("Error creating functions table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Function> findById(int id) {
        String sql = "SELECT id, name, moduleId FROM functions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Function(rs.getInt("id"), rs.getString("name"), rs.getInt("moduleId")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding function by id: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Function> findByName(String name) {
        String sql = "SELECT id, name, moduleId FROM functions WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Function(rs.getInt("id"), rs.getString("name"), rs.getInt("moduleId")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding function by name: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Function> findAll() {
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT id, name, moduleId FROM functions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                functions.add(new Function(rs.getInt("id"), rs.getString("name"), rs.getInt("moduleId")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all functions: " + e.getMessage());
            e.printStackTrace();
        }
        return functions;
    }

    @Override
    public List<Function> findByModuleId(int moduleId) {
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT id, name, moduleId FROM functions WHERE moduleId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, moduleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                functions.add(new Function(rs.getInt("id"), rs.getString("name"), rs.getInt("moduleId")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding functions by module id: " + e.getMessage());
            e.printStackTrace();
        }
        return functions;
    }

    @Override
    public void save(Function function) {
        if (function.getId() == 0) { // New function
            String sql = "INSERT INTO functions (name, moduleId) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, function.getName());
                pstmt.setInt(2, function.getModuleId());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        // For simplicity, we are not setting the generated ID back to the object
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error saving new function: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Existing function
            String sql = "UPDATE functions SET name = ?, moduleId = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, function.getName());
                pstmt.setInt(2, function.getModuleId());
                pstmt.setInt(3, function.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating function: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM functions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting function: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
