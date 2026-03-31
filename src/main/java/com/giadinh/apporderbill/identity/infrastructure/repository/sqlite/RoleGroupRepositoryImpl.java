package com.giadinh.apporderbill.identity.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.identity.model.RoleGroup;
import com.giadinh.apporderbill.identity.repository.RoleGroupRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleGroupRepositoryImpl implements RoleGroupRepository {

    private final Connection connection;

    public RoleGroupRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rolegroups (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, description TEXT)");
        } catch (SQLException e) {
            System.err.println("Error creating rolegroups table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<RoleGroup> findById(int id) {
        String sql = "SELECT id, name, description FROM rolegroups WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new RoleGroup(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding rolegroup by id: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<RoleGroup> findByName(String name) {
        String sql = "SELECT id, name, description FROM rolegroups WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new RoleGroup(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding rolegroup by name: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<RoleGroup> findAll() {
        List<RoleGroup> roleGroups = new ArrayList<>();
        String sql = "SELECT id, name, description FROM rolegroups";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roleGroups.add(new RoleGroup(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all rolegroups: " + e.getMessage());
            e.printStackTrace();
        }
        return roleGroups;
    }

    @Override
    public void save(RoleGroup roleGroup) {
        if (roleGroup.getId() == 0) { // New rolegroup
            String sql = "INSERT INTO rolegroups (name, description) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roleGroup.getName());
                pstmt.setString(2, roleGroup.getDescription());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving new rolegroup: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Existing rolegroup
            String updateSql = "UPDATE rolegroups SET name = ?, description = ? WHERE id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setString(1, roleGroup.getName());
                updateStmt.setString(2, roleGroup.getDescription());
                updateStmt.setInt(3, roleGroup.getId());
                int updated = updateStmt.executeUpdate();
                if (updated == 0) {
                    String insertSql = "INSERT INTO rolegroups (id, name, description) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, roleGroup.getId());
                        insertStmt.setString(2, roleGroup.getName());
                        insertStmt.setString(3, roleGroup.getDescription());
                        insertStmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error upserting rolegroup: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM rolegroups WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting rolegroup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
