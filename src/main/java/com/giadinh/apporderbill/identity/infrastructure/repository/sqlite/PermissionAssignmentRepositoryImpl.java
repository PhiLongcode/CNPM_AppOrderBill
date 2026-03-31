package com.giadinh.apporderbill.identity.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.identity.model.PermissionAssignment;
import com.giadinh.apporderbill.identity.repository.PermissionAssignmentRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PermissionAssignmentRepositoryImpl implements PermissionAssignmentRepository {

    private final Connection connection;

    public PermissionAssignmentRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS permission_assignments (id INTEGER PRIMARY KEY AUTOINCREMENT, roleGroupId INTEGER NOT NULL, functionId INTEGER NOT NULL, canView BOOLEAN NOT NULL, canOperate BOOLEAN NOT NULL, UNIQUE (roleGroupId, functionId), FOREIGN KEY (roleGroupId) REFERENCES rolegroups(id), FOREIGN KEY (functionId) REFERENCES functions(id))");
        } catch (SQLException e) {
            System.err.println("Error creating permission_assignments table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<PermissionAssignment> findById(int id) {
        String sql = "SELECT id, roleGroupId, functionId, canView, canOperate FROM permission_assignments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new PermissionAssignment(
                        rs.getInt("id"),
                        rs.getInt("roleGroupId"),
                        rs.getInt("functionId"),
                        rs.getBoolean("canView"),
                        rs.getBoolean("canOperate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding permission assignment by id: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<PermissionAssignment> findByRoleGroupAndFunction(int roleGroupId, int functionId) {
        String sql = "SELECT id, roleGroupId, functionId, canView, canOperate FROM permission_assignments WHERE roleGroupId = ? AND functionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roleGroupId);
            pstmt.setInt(2, functionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new PermissionAssignment(
                        rs.getInt("id"),
                        rs.getInt("roleGroupId"),
                        rs.getInt("functionId"),
                        rs.getBoolean("canView"),
                        rs.getBoolean("canOperate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding permission assignment by roleGroup and function: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<PermissionAssignment> findAll() {
        List<PermissionAssignment> assignments = new ArrayList<>();
        String sql = "SELECT id, roleGroupId, functionId, canView, canOperate FROM permission_assignments";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assignments.add(new PermissionAssignment(
                        rs.getInt("id"),
                        rs.getInt("roleGroupId"),
                        rs.getInt("functionId"),
                        rs.getBoolean("canView"),
                        rs.getBoolean("canOperate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all permission assignments: " + e.getMessage());
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public List<PermissionAssignment> findByRoleGroupId(int roleGroupId) {
        List<PermissionAssignment> assignments = new ArrayList<>();
        String sql = "SELECT id, roleGroupId, functionId, canView, canOperate FROM permission_assignments WHERE roleGroupId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, roleGroupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                assignments.add(new PermissionAssignment(
                        rs.getInt("id"),
                        rs.getInt("roleGroupId"),
                        rs.getInt("functionId"),
                        rs.getBoolean("canView"),
                        rs.getBoolean("canOperate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding permission assignments by roleGroup id: " + e.getMessage());
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public void save(PermissionAssignment assignment) {
        if (assignment.getId() == 0) { // New assignment
            String sql = "INSERT INTO permission_assignments (roleGroupId, functionId, canView, canOperate) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, assignment.getRoleGroupId());
                pstmt.setInt(2, assignment.getFunctionId());
                pstmt.setBoolean(3, assignment.isCanView());
                pstmt.setBoolean(4, assignment.isCanOperate());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        // For simplicity, not setting generated ID back to object for now
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error saving new permission assignment: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Existing assignment
            String sql = "UPDATE permission_assignments SET roleGroupId = ?, functionId = ?, canView = ?, canOperate = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, assignment.getRoleGroupId());
                pstmt.setInt(2, assignment.getFunctionId());
                pstmt.setBoolean(3, assignment.isCanView());
                pstmt.setBoolean(4, assignment.isCanOperate());
                pstmt.setInt(5, assignment.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating permission assignment: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM permission_assignments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting permission assignment: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
